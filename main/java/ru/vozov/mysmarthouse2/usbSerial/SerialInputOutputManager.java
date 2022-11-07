package ru.vozov.mysmarthouse2.usbSerial;

import android.os.Process;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

import ru.vozov.mysmarthouse2.MainActivity;
import ru.vozov.mysmarthouse2.usbSerial.driver.UsbSerialPort;

//Служебный класс который обслуживает последовательный порт
public class SerialInputOutputManager implements Runnable {
    private static final String TAG = SerialInputOutputManager.class.getSimpleName();
    public static boolean DEBUG = false;
    private static final int BUFSIZ = 4096;

   //тайм-аут чтения по умолчанию бесконечен,
   // чтобы избежать потери данных с помощью API массовой передачи
    private int mReadTimeout = 0;
    private int mWriteTimeout = 0;

    private final Object mReadBufferLock = new Object();
    private final Object mWriteBufferLock = new Object();

    private ByteBuffer mReadBuffer = ByteBuffer.allocate(BUFSIZ);
    private ByteBuffer mWriteBuffer = ByteBuffer.allocate(BUFSIZ);

    public enum State {
        STOPPED,
        RUNNING,
        STOPPING
    }

    private int mThreadPriority = Process.THREAD_PRIORITY_URGENT_AUDIO;
    private State mState = State.STOPPED; // Синхронизировано "этим"
    private Listener mListener; // Синхронизировано "этим"
    private final UsbSerialPort mSerialPort;

    public interface Listener {
       //Вызывается при наличии новых входящих данных.
        public void onNewData(byte[] data);
        //Вызывается, когда {@link SerialInputOutputManager#run()} прерывается из-за ошибки.
        public void onRunError(Exception e);
    }

    public SerialInputOutputManager(UsbSerialPort serialPort, MainActivity mainActivity) {
        mSerialPort = serialPort;
    }

    public SerialInputOutputManager(UsbSerialPort serialPort, Listener listener) {
        mSerialPort = serialPort;
        mListener = listener;
    }

    public synchronized void setListener(Listener listener) {
        mListener = listener;
    }

    public synchronized Listener getListener() {
        return mListener;
    }

    //SetThreadPriority. По умолчанию используйте более высокий приоритет, чем поток
    // пользовательского интерфейса, чтобы предотвратить потерю данных
    //  @param threadPriority  see {@link Process#setThreadPriority(int)}
    public void setThreadPriority(int threadPriority) {
        if (mState != State.STOPPED)
            throw new IllegalStateException("threadPriority only configurable before SerialInputOutputManager is started");
        mThreadPriority = threadPriority;
    }
    //тайм-аут чтения
    public void setReadTimeout(int timeout) {
        //при установке, если период уже запущен, новая величина вступит в силу после его окончания
        if(mReadTimeout == 0 && timeout != 0 && mState != State.STOPPED)
            throw new IllegalStateException("ReadTimeout настраивается только перед запуском SerialInputOutputManager");
        mReadTimeout = timeout;
    }

    public int getReadTimeout() {
        return mReadTimeout;
    }
    //тайм-аут записи
    public void setWriteTimeout(int timeout) {
        mWriteTimeout = timeout;
    }

    public int getWriteTimeout() {
        return mWriteTimeout;
    }

    //размер буфера чтения
    public void setReadBufferSize(int bufferSize) {
        if (getReadBufferSize() == bufferSize)
            return;
        synchronized (mReadBufferLock) {
            mReadBuffer = ByteBuffer.allocate(bufferSize);
        }
    }

    public int getReadBufferSize() {
        return mReadBuffer.capacity();
    }
        //размер буфера записи
    public void setWriteBufferSize(int bufferSize) {
        if(getWriteBufferSize() == bufferSize)
            return;
        synchronized (mWriteBufferLock) {
            ByteBuffer newWriteBuffer = ByteBuffer.allocate(bufferSize);
            if(mWriteBuffer.position() > 0)
                newWriteBuffer.put(mWriteBuffer.array(), 0, mWriteBuffer.position());
            mWriteBuffer = newWriteBuffer;
        }
    }

    public int getWriteBufferSize() {
        return mWriteBuffer.capacity();
    }

   //при использовании WriteAsync рекомендуется использовать ReadTimeout != 0,
   // в противном случае запись будет отложена до тех пор, пока не будут доступны данные чтения
    public void writeAsync(byte[] data) {
        synchronized (mWriteBufferLock) {
            mWriteBuffer.put(data);
        }
    }

    public synchronized void stop() {
        if (getState() == State.RUNNING) {
            Log.i(TAG, "Stop requested");
            mState = State.STOPPING;
        }
    }

    public synchronized State getState() {
        return mState;
    }

  //Непрерывно обслуживает буферы чтения и записи до тех пор, пока {@link #stop()} не будет
  // вызвано или до тех пор, пока не возникнет исключение драйвера.
    @Override
    public void run() {
        if(mThreadPriority != Process.THREAD_PRIORITY_DEFAULT)
            setThreadPriority(mThreadPriority);

        synchronized (this) {
            if (getState() != State.STOPPED) {
                throw new IllegalStateException("Already running");
            }
            mState = State.RUNNING;
        }

        Log.i(TAG, "Running ...");
        try {
            while (true) {
                if (getState() != State.RUNNING) {
                    Log.i(TAG, "Stopping mState=" + getState());
                    break;
                }
                step();
            }
        } catch (Exception e) {
            Log.w(TAG, "Run ending due to exception: " + e.getMessage(), e);
            final Listener listener = getListener();
            if (listener != null) {
                listener.onRunError(e);
            }
        } finally {
            synchronized (this) {
                mState = State.STOPPED;
                Log.i(TAG, "Stopped");
            }
        }
    }

       private void step() throws IOException {
        //Обработка входящих данных
        byte[] buffer = null;
        synchronized (mReadBufferLock) {
            buffer = mReadBuffer.array();
        }
        int len = mSerialPort.read(buffer, mReadTimeout);
        if (len > 0) {
            if (DEBUG) Log.d(TAG, "Read data len=" + len);
            final Listener listener = getListener();
            if (listener != null) {
                final byte[] data = new byte[len];
                System.arraycopy(buffer, 0, data, 0, len);
                listener.onNewData(data);
            }
        }

        // Обработка исходящих данных
        buffer = null;
        synchronized (mWriteBufferLock) {
            len = mWriteBuffer.position();
            if (len > 0) {
                buffer = new byte[len];
                mWriteBuffer.rewind();
                mWriteBuffer.get(buffer, 0, len);
                mWriteBuffer.clear();
            }
        }
        if (buffer != null) {
            if (DEBUG) {
                Log.d(TAG, "Writing data len=" + len);
            }
            mSerialPort.write(buffer, mWriteTimeout);
        }
    }

}
