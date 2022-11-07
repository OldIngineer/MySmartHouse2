package ru.vozov.mysmarthouse2.usbSerial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbRequest;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.EnumSet;

public abstract class CommonUsbSerialPort implements UsbSerialPort{
    private static final String TAG = CommonUsbSerialPort.class.getSimpleName();
    private static final int DEFAULT_WRITE_BUFFER_SIZE = 16 * 1024;
    private static final int MAX_READ_SIZE = 16 * 1024; // = old bulkTransfer limit

    protected final UsbDevice mDevice;
    protected final int mPortNumber;

    // non-null when open()
    protected UsbDeviceConnection mConnection = null;
    protected UsbEndpoint mReadEndpoint;
    protected UsbEndpoint mWriteEndpoint;
    protected UsbRequest mUsbRequest;

    protected final Object mWriteBufferLock = new Object();
    /** Internal write buffer.  Guarded by {@link #mWriteBufferLock}. */
    protected byte[] mWriteBuffer;

    public CommonUsbSerialPort(UsbDevice device, int portNumber) {
        mDevice = device;
        mPortNumber = portNumber;

        mWriteBuffer = new byte[DEFAULT_WRITE_BUFFER_SIZE];
    }

    @Override
    public String toString() {
        return String.format("<%s device_name=%s device_id=%s port_number=%s>",
                getClass().getSimpleName(), mDevice.getDeviceName(),
                mDevice.getDeviceId(), mPortNumber);
    }

    @Override
    public UsbDevice getDevice() {
        return mDevice;
    }

    @Override
    public int getPortNumber() {
        return mPortNumber;
    }

    /**
     * Returns the write endpoint.
     * @return write endpoint
     */
    public UsbEndpoint getWriteEndpoint() { return mWriteEndpoint; }

    /**
     * Returns the read endpoint.
     * @return read endpoint
     */
    public UsbEndpoint getReadEndpoint() { return mReadEndpoint; }

    /**
     * Returns the device serial number
     *  @return serial number
     */
    @Override
    public String getSerial() {
        return mConnection.getSerial();
    }

    /**
     * Sets the size of the internal buffer used to exchange data with the USB
     * stack for write operations.  Most users should not need to change this.
     *
     * @param bufferSize the size in bytes
     */
    public final void setWriteBufferSize(int bufferSize) {
        synchronized (mWriteBufferLock) {
            if (bufferSize == mWriteBuffer.length) {
                return;
            }
            mWriteBuffer = new byte[bufferSize];
        }
    }

    @Override
    public void open(UsbDeviceConnection connection) throws IOException {
        if (mConnection != null) {
            throw new IOException("Already open");
        }
        if(connection == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        mConnection = connection;
        try {
            openInt(connection);
            if (mReadEndpoint == null || mWriteEndpoint == null) {
                throw new IOException("Could not get read & write endpoints");
            }
            mUsbRequest = new UsbRequest();
            mUsbRequest.initialize(mConnection, mReadEndpoint);
        } catch(Exception e) {
            try {
                close();
            } catch(Exception ignored) {}
            throw e;
        }
    }

    protected abstract void openInt(UsbDeviceConnection connection) throws IOException;

    @Override
    public void close() throws IOException {
        if (mConnection == null) {
            throw new IOException("Already closed");
        }
        try {
            mUsbRequest.cancel();
        } catch(Exception ignored) {}
        mUsbRequest = null;
        try {
            closeInt();
        } catch(Exception ignored) {}
        try {
            mConnection.close();
        } catch(Exception ignored) {}
        mConnection = null;
    }

    protected abstract void closeInt();

    /**
     * use simple USB request supported by all devices to test if connection is still valid
     */
    protected void testConnection() throws IOException {
        byte[] buf = new byte[2];
        int len = mConnection.controlTransfer(0x80 /*DEVICE*/, 0 /*GET_STATUS*/, 0, 0, buf, buf.length, 200);
        if(len < 0)
            throw new IOException("USB get_status request failed");
    }

    @Override
    public int read(final byte[] dest, final int timeout) throws IOException {
        return read(dest, timeout, true);
    }

    protected int read(final byte[] dest, final int timeout, boolean testConnection) throws IOException {
        if(mConnection == null) {
            throw new IOException("Connection closed");
        }
        if(dest.length <= 0) {
            throw new IllegalArgumentException("Read buffer to small");
        }
        final int nread;
        if (timeout != 0) {
        // массовая передача приведет к потере данных с коротким таймаутом
        // + высокой скоростью передачи данных в бодах + непрерывной передачей
        //https://stackoverflow.com/questions/9108548/android-usb-host-bulktransfer-is-losing-data
        // но mConnection.requestWait(timeout) доступен с Android 8.0 es еще хуже,
        // так как он выходит из строя с коротким таймаутом, например
        //   A/libc: Fatal signal 11 (SIGSEGV), code 1 (SEGV_MAPERR),
        //   fault addr 0x276a in tid 29846 (pool-2-thread-1), pid 29618 (.usbserial.test)
       //     /system/lib64/libusbhost.so (usb_request_wait+192)
       //     /system/lib64/libandroid_runtime.so
       //     (android_hardware_UsbDeviceConnection_request_wait(_JNIEnv*, _jobject*, long)+84)
       // потеря данных / сбои наблюдались с таймаутом до 200 мс
            long endTime = testConnection ? System.currentTimeMillis() + timeout : 0;
            int readMax = Math.min(dest.length, MAX_READ_SIZE);
            nread = mConnection.bulkTransfer(mReadEndpoint, dest, readMax, timeout);
            // Распространение ошибок Android можно исключить, nread == -1 может быть: тайм-аут, потеря соединения, недостаточный размер буфера,
            if(nread == -1 && testConnection && System.currentTimeMillis() < endTime)
                testConnection();

        } else {
            final ByteBuffer buf = ByteBuffer.wrap(dest);
            if (!mUsbRequest.queue(buf, dest.length)) {
                throw new IOException("Queueing USB request failed");
            }
            final UsbRequest response = mConnection.requestWait();
            if (response == null) {
                throw new IOException("Waiting for USB request failed");
            }
            nread = buf.position();
        }
        if (nread > 0)
            return nread;
        else
            return 0;
    }

    @Override
    public void write(final byte[] src, final int timeout) throws IOException {
        int offset = 0;
        final long endTime = (timeout == 0) ? 0 : (System.currentTimeMillis() + timeout);

        if(mConnection == null) {
            throw new IOException("Connection closed");
        }
        while (offset < src.length) {
            int requestTimeout;
            final int requestLength;
            final int actualLength;

            synchronized (mWriteBufferLock) {
                final byte[] writeBuffer;

                requestLength = Math.min(src.length - offset, mWriteBuffer.length);
                if (offset == 0) {
                    writeBuffer = src;
                } else {
                    // bulkTransfer does not support offsets, make a copy.
                    System.arraycopy(src, offset, mWriteBuffer, 0, requestLength);
                    writeBuffer = mWriteBuffer;
                }
                if (timeout == 0 || offset == 0) {
                    requestTimeout = timeout;
                } else {
                    requestTimeout = (int)(endTime - System.currentTimeMillis());
                    if(requestTimeout == 0)
                        requestTimeout = -1;
                }
                if (requestTimeout < 0) {
                    actualLength = -2;
                } else {
                    actualLength = mConnection.bulkTransfer(mWriteEndpoint, writeBuffer, requestLength, requestTimeout);
                }
            }
            Log.d(TAG, "Wrote " + actualLength + "/" + requestLength + " offset " + offset + "/" + src.length + " timeout " + requestTimeout);
            if (actualLength <= 0) {
                if (timeout != 0 && System.currentTimeMillis() >= endTime) {
                    SerialTimeoutException ex = new SerialTimeoutException("Error writing " + requestLength + " bytes at offset " + offset + " of total " + src.length + ", rc=" + actualLength);
                    ex.bytesTransferred = offset;
                    throw ex;
                } else {
                    throw new IOException("Error writing " + requestLength + " bytes at offset " + offset + " of total " + src.length);
                }
            }
            offset += actualLength;
        }
    }

    @Override
    public boolean isOpen() {
        return mConnection != null;
    }

    @Override
    public abstract void setParameters(int baudRate, int dataBits, int stopBits, @Parity int parity) throws IOException;

    @Override
    public boolean getCD() throws IOException { throw new UnsupportedOperationException(); }

    @Override
    public boolean getCTS() throws IOException { throw new UnsupportedOperationException(); }

    @Override
    public boolean getDSR() throws IOException { throw new UnsupportedOperationException(); }

    @Override
    public boolean getDTR() throws IOException { throw new UnsupportedOperationException(); }

    @Override
    public void setDTR(boolean value) throws IOException { throw new UnsupportedOperationException(); }

    @Override
    public boolean getRI() throws IOException { throw new UnsupportedOperationException(); }

    @Override
    public boolean getRTS() throws IOException { throw new UnsupportedOperationException(); }

    @Override
    public void setRTS(boolean value) throws IOException { throw new UnsupportedOperationException(); }

    @Override
    public abstract EnumSet<ControlLine> getControlLines() throws IOException;

    @Override
    public abstract EnumSet<ControlLine> getSupportedControlLines() throws IOException;

    @Override
    public void purgeHwBuffers(boolean purgeWriteBuffers, boolean purgeReadBuffers) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBreak(boolean value) throws IOException { throw new UnsupportedOperationException(); }

}
