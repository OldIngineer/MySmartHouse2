package ru.vozov.mysmarthouse2;

//класс дешифрации:
// - информации полученной от датчиков модулей RS485;
// - формирования команд из SMS и по сценариям
public class DecryptMessages {
    //метод дешифрации: входные данные - байты полученные от модуля,
    // выходные - строка сообщения
    public String decryptMessagesModule(byte[] mResp, String[] mNameModule, String[] mMessage) {
        String str = mNameModule[mResp[0]] + ": ";//присваивание в сообщении имени модуля
        if ((mResp[1]==3)&(mResp[2]==0)&(mResp[3]==6)) {
        if ((mResp[5]&1)==1) {//сработал датчик перемещений
            str += mMessage[0];
        } else str += "норма";
     } else str += "Ошибка обмена в домашней сети";
        return str;
   }
    //метод дешифрации: входные данные - байты полученные от модуля;
    // выходные - изменение состояния объектов
    public byte[][] changeStateObject(byte[] mResp, byte[][] mStateObject)
    {
          if ((mResp[1]==3)&(mResp[2]==0)&(mResp[3]==6)) {
            for(int i=0; i<8; i++)//перебор объектов для данного узла(байт)
            {
                if(((mResp[4] >> i)&1)==1)//сдвиг на заданное число разрядов и проверка 0р.
                {
                    mStateObject[mResp[0]][i] = 1;
                } else {
                    mStateObject[mResp[0]][i] = 0;
                }
            }
        }
            return mStateObject;
    }
   //метод дешифрации: входные данные - байты полученные от модуля;
   // выходные - значение текущих температур
    public String[] decryptTemp(byte[] mResp, String[] mTempCurrent)
    {
        if ((mResp[1]==3)&(mResp[2]==0)&(mResp[3]==6)) {
            if(mResp[6]==0) {//в данном узле нет датчика
               mTempCurrent[mResp[0]] = "н/д";
            } else mTempCurrent[mResp[0]] = String.valueOf(mResp[6] -1);//-1 поправка
        }
        return mTempCurrent;
    }
    //метод дешифрации: входные данные - байты полученные от модуля;
    // выходные - значение текущей влажности воздуха
    public String[] decryptHumidity(byte[] mResp, String[] mHumidityCurrent)
    {
        if ((mResp[1]==3)&(mResp[2]==0)&(mResp[3]==6)) {
            if(mResp[7]==0) {//в данном узле нет датчика
                mHumidityCurrent[mResp[0]] = "н/д";
            } else mHumidityCurrent[mResp[0]] = String.valueOf(mResp[7]);
        }
        return mHumidityCurrent;
    }
    //метод дешифрации: входные данные - байты полученные от модуля;
    // выходные - значение текущей влажности земли
    public String[] decryptHumidityGround(byte[] mResp, String[] mHumidityGround)
    {
        if ((mResp[1]==3)&(mResp[2]==0)&(mResp[3]==6)) {
            if(mResp[8]==0) {//в данном узле нет датчика
                mHumidityGround[mResp[0]] = "н/д";
            } else mHumidityGround[mResp[0]] = String.valueOf(mResp[8]);
        }
        return mHumidityGround;
    }
   //метод дешифрации: входные данные - SMS команда полученная с выбранного телефона,
   //                  выходные данные - 8 байт для передачи в модуль
    public byte[] decryptSMS(String sms, String[] mNameModule, boolean[] mCheckActiveModule, String[] mComText)
    {
        byte[] com = {0,6,0,0,0,0,1,1};
        //String[] s = {null, null, null, null};
        //преобразование сообщения в массив строк
        String[] s = sms.split(" ");//имя модуля\ вкл/выкл\ объект\ способ
        for (int i = 1; i< mNameModule.length; i++)//перебор имен модулей
        {  //если имя совпадает с именем модуля
           if(s[0].equalsIgnoreCase(mNameModule[i]))
            //if(s[0].startsWith(mNameModule[i]))
           {//если этот модуль активен
            if(mCheckActiveModule[i])
            {
                com[0] = (byte) i;//адрес модуля
                if (!(s[1].startsWith("выкл")||s[1].startsWith("вкл")))
                {
                    com[7] = 0x04;//возврат с признаком: команда неправильная
                    return com;
                }
                //перебор объектов
                for(int n=0; n< mComText.length; n++)
                {
                    if(s[2].equals(mComText[n]))
                    {
                        com[5] = (byte)(Math.pow(2,n));//разряд байта опр.через степень 2
                        if (s[1].startsWith("вкл")){com[4] = 64;}
                        if (s.length>3) {
                            if (s[3].startsWith("уд")) {com[2] = 1;}
                        }
                        return com;//возврат норма
                    }
                }
                com[7] = 0x05;//возврат с признаком: объект не найден
            } else {com[7] = 0x03;}//возврат с признаком: модуль не активен
               return com;
           }
        }
        com[7] = 0x02;// неправильное имя
        return com;
    }
//метод дешифрации: входные данные - заданные условия выполнения команд
// соглано выбранным сценариям,
    //                  выходные данные - 8 байт для передачи в модуль
    //Последовательно производится перебор объектов узлов в порядке возрастания,
    //сравнивается с запомненным состоянием и при расхождении выдается команда.
    //Причем исполнится только последняя.
    public byte[] decryptScript(boolean[] mCheckActiveModule, String[] mTempCurrent,
                        String[] mTempIn, String[] mTempOut, byte[][] mStateObject)
    {
        byte[] com = {0,6,0,0,0,0,1,1};
        //проверка сценария включения отопления
        for (int i = 1; i< mCheckActiveModule.length; i++)//перебор модулей
        {
           //если модуль активный, подключен датчик и
            // значения температур включения/выключения отопления выбраны
  if((mCheckActiveModule[i])&(!mTempCurrent[i].equals("н/д"))&(!mTempIn[i].equals("no"))
                        &(!mTempOut[i].equals("no"))) {
      if((Byte.parseByte(mTempCurrent[i])<Byte.parseByte(mTempIn[i]))
           &(mStateObject[i][4]==0)) {
                  com[0] = (byte) i;//адрес модуля
                  com[4] = 64;//включить
                  com[5] = 16;//отопление
              }
      if((Byte.parseByte(mTempCurrent[i])>Byte.parseByte(mTempOut[i]))
              &(mStateObject[i][4]==1)) {
          com[0] = (byte) i;//адрес модуля
          com[4] = 0;//выключить
          com[5] = 16;//отопление
            }

          }
        }
        return com;
    }
}
