package ru.vozov.mysmarthouse2.usbSerial.driver;

import android.hardware.usb.UsbDevice;

import java.util.List;

public interface UsbSerialDriver {
   //Возвращает необработанное Usb-устройство {@link}, поддерживающее этот порт
    UsbDevice getDevice();

  //Возвращает все доступные порты для этого устройства. В этом списке должно быть как минимум
  // одна запись
    List<UsbSerialPort> getPorts();
}
