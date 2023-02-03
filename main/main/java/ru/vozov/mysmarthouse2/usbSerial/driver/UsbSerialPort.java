package ru.vozov.mysmarthouse2.usbSerial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import androidx.annotation.IntDef;

import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.EnumSet;

//интерфейс для сигналов серийного порта
public interface UsbSerialPort extends Closeable {
    /** 5 data bits. */
    int DATABITS_5 = 5;
    /** 6 data bits. */
    int DATABITS_6 = 6;
    /** 7 data bits. */
    int DATABITS_7 = 7;
    /** 8 data bits. */
    int DATABITS_8 = 8;

    //Значения для заданных параметров
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PARITY_NONE, PARITY_ODD, PARITY_EVEN, PARITY_MARK, PARITY_SPACE})
    @interface Parity {}
    /** No parity. */
    int PARITY_NONE = 0;
    /** Odd parity. */
    int PARITY_ODD = 1;
    /** Even parity. */
    int PARITY_EVEN = 2;
    /** Mark parity. */
    int PARITY_MARK = 3;
    /** Space parity. */
    int PARITY_SPACE = 4;

    /** 1 stop bit. */
    int STOPBITS_1 = 1;
    /** 1.5 stop bits. */
    int STOPBITS_1_5 = 3;
    /** 2 stop bits. */
    int STOPBITS_2 = 2;

    //значения для линий управления
    enum ControlLine { RTS, CTS,  DTR, DSR,  CD, RI }

  //Возвращает драйвер, используемый этим портом
    UsbSerialDriver getDriver();
  // Возвращает текущее привязанное USB-устройство
    UsbDevice getDevice();
  //Номер порта в драйвере
    int getPortNumber();
  //Серийный номер базового подключения Usb-устройства или {@code null}.
    /**
     * @return value from {@link UsbDeviceConnection#getSerial()}
     * @throws SecurityException starting with target SDK 29 (Android 10) if permission for USB device is not granted
     */
    String getSerial();
  //Открывает и инициализирует порт. После успеха вызывающий абонент должен убедиться, что
    /**
     * {@link #close()} is eventually called.
     *
     * @param connection an open device connection, acquired with
     *                   {@link UsbManager#openDevice(android.hardware.usb.UsbDevice)}
     * @throws IOException on error opening or initializing the port.
     */
    void open(UsbDeviceConnection connection) throws IOException;
  //Закрывает порт
    /**
     * @throws IOException on error closing the port.
     */
    void close() throws IOException;
  //Считывает как можно больше байтов в буфер назначения
    /**
     * @param dest буфер байтов назначения
     * @param timeout тайм-аут для чтения в миллисекундах, 0 бесконечен
     * @return фактическое количество прочитанных байтов
     * @throws IOException если во время чтения произошла ошибка
     */
    int read(final byte[] dest, final int timeout) throws IOException;
  //Записывает как можно больше байтов из исходного буфера
    /**
     * @param src the source byte buffer
     * @param timeout the timeout for writing in milliseconds, 0 is infinite
     // @throws SerialTimeoutException if timeout reached before sending all data.
     *                                ex.bytesTransferred may contain bytes transferred
     * @throws IOException if an error occurred during writing
     */
    void write(final byte[] src, final int timeout) throws IOException;
  //Устанавливает различные параметры последовательного порта
    /**
     * @param baudRate baud rate as an integer, for example {@code 115200}.
     * @param dataBits one of {@link #DATABITS_5}, {@link #DATABITS_6},
     *                 {@link #DATABITS_7}, or {@link #DATABITS_8}.
     * @param stopBits one of {@link #STOPBITS_1}, {@link #STOPBITS_1_5}, or {@link #STOPBITS_2}.
     * @param parity one of {@link #PARITY_NONE}, {@link #PARITY_ODD},
     *               {@link #PARITY_EVEN}, {@link #PARITY_MARK}, or {@link #PARITY_SPACE}.
     * @throws IOException on error setting the port parameters
     * @throws UnsupportedOperationException if values are not supported by a specific device
     */
    void setParameters(int baudRate, int dataBits, int stopBits, @Parity int parity) throws IOException;
  //Получает бит CD (Carrier Detect) от базового UART
    /**
     * @return the current state
     * @throws IOException if an error occurred during reading
     * @throws UnsupportedOperationException if not supported
     */
    boolean getCD() throws IOException;
  //Получает бит CTS (Clear To Send) от базового UART
    /**
     * @return the current state
     * @throws IOException if an error occurred during reading
     * @throws UnsupportedOperationException if not supported
     */
    boolean getCTS() throws IOException;
  //Получает бит DSR (Data Set Ready) из базового UART
    /**
     * @return the current state
     * @throws IOException if an error occurred during reading
     * @throws UnsupportedOperationException if not supported
     */
    boolean getDSR() throws IOException;
  //Получает сигнал DTR (терминал данных готов) от базовых UART.
    /**
     * @return the current state
     * @throws IOException if an error occurred during reading
     * @throws UnsupportedOperationException if not supported
     */
    boolean getDTR() throws IOException;
  //Устанавливает бит DTR (Data Terminal Ready) на базовом UART, если он поддерживается
    /**
     * @param value the value to set
     * @throws IOException if an error occurred during writing
     * @throws UnsupportedOperationException if not supported
     */
    void setDTR(boolean value) throws IOException;

    /**
     * Gets the RI (Ring Indicator) bit from the underlying UART.
     *
     * @return the current state
     * @throws IOException if an error occurred during reading
     * @throws UnsupportedOperationException if not supported
     */
    boolean getRI() throws IOException;

    /**
     * Gets the RTS (Request To Send) bit from the underlying UART.
     *
     * @return the current state
     * @throws IOException if an error occurred during reading
     * @throws UnsupportedOperationException if not supported
     */
    boolean getRTS() throws IOException;

    /**
     * Sets the RTS (Request To Send) bit on the underlying UART, if supported.
     *
     * @param value the value to set
     * @throws IOException if an error occurred during writing
     * @throws UnsupportedOperationException if not supported
     */
    void setRTS(boolean value) throws IOException;

    /**
     * Gets all control line values from the underlying UART, if supported.
     * Requires less USB calls than calling getRTS() + ... + getRI() individually.
     *
     * @return EnumSet.contains(...) is {@code true} if set, else {@code false}
     * @throws IOException if an error occurred during reading
     */
    EnumSet<ControlLine> getControlLines() throws IOException;

    /**
     * Gets all control line supported flags.
     *
     * @return EnumSet.contains(...) is {@code true} if supported, else {@code false}
     * @throws IOException if an error occurred during reading
     */
    EnumSet<ControlLine> getSupportedControlLines() throws IOException;

    /**
     * Purge non-transmitted output data and / or non-read input data.
     *
     * @param purgeWriteBuffers {@code true} to discard non-transmitted output data
     * @param purgeReadBuffers {@code true} to discard non-read input data
     * @throws IOException if an error occurred during flush
     * @throws UnsupportedOperationException if not supported
     */
    void purgeHwBuffers(boolean purgeWriteBuffers, boolean purgeReadBuffers) throws IOException;

    /**
     * send BREAK condition.
     *
     * @param value set/reset
     */
    void setBreak(boolean value) throws IOException;

    /**
     * Returns the current state of the connection.
     */
    boolean isOpen();

}
