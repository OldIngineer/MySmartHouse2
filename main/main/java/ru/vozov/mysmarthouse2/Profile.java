package ru.vozov.mysmarthouse2;

//класс формирования данных профиля для записи ввиде строки в файле
// и дешифрации данных из строки файла
/*Правила записи параметров в профиль:
        1.Если строка начинается со знака #, это строка коментариев
        2.В строке только один параметр
        3.Наименование параметра совпадает с описанным в программе
        4.Строка с параметром разделяется на две части
           в левой наименование, в правой через знак "пробел :" величина */
public class Profile {
    //формирование строки данных для записи в файл
    public String profileOut(String[] name, boolean[] check, String[] tel, boolean[] checkTel,
                             String[] messageSecurity, String[] tempIn, String[] tempOut,
                             String mToken, String mId)
    {
        StringBuilder nameMod = new StringBuilder();
        StringBuilder checkMod = new StringBuilder();
        StringBuilder numTel = new StringBuilder();
        StringBuilder activeTel = new StringBuilder();
        StringBuilder mesSec = new StringBuilder();
        StringBuilder temIn = new StringBuilder();
        StringBuilder temOut = new StringBuilder();
        for (int i = 0; i<name.length; i++) {
            nameMod.append(name[i]).append(",");
            checkMod.append(check[i]).append(",");
            temIn.append(tempIn[i]).append(",");
            temOut.append(tempOut[i]).append(",");
        }
        for (int i = 0; i<tel.length; i++) {
            numTel.append(tel[i]).append(",");
            activeTel.append(checkTel[i]).append(",");
        }
        for (String s : messageSecurity) {
            mesSec.append(s).append(",");
        }
        String str = "#Profile \n";
       str += "RS485 device :" + nameMod + "\n";
       str += "RS485 device checked :" + checkMod + "\n";
       str += "Number telephone :" + numTel + "\n";
       str += "Active telephone checked :" + activeTel + "\n";
       str += "Message Security :" + mesSec + "\n";
       str += "Temperature open :" + temIn + "\n";
       str += "Temperature close :" + temOut + "\n";
       str += "Bot Token :" + mToken + "\n";
       str += "Chat_id :" + mId + "\n";
        return str;
    }
    //дешифрация строки: получение имен узлов RS485
    public String[] profileInName(String str, int n) {
        String[] name = new String[n];

        //преобразование файла в массив строк
        String[] s = str.split("\n");
        //-----анализ строк---------
        for (String value : s) {
            //запись из профиля наименования узлов RS485
            if (value.startsWith("RS485 device :")) {
                //выделить данные (всегда после ":")
                String[] ps = value.split(":");
                //записать в переменную
                String p = ps[1];
                //преобразовать в матрицу строк
                name = p.split(",");
            }
        }
        return name;
    }
     //дешифрация строки: получение из профиля признака выбора модуля
    public boolean[] profileInCheckModule(String str, int n) {
        boolean[] checkModule = new boolean[n];
        //преобразование файла в массив строк
        String[] s = str.split("\n");
        //-----анализ строк---------
        for (String value : s) {
            if (value.startsWith("RS485 device checked :")) {
                //выделить данные (всегда после ":")
                String[] ps = value.split(":");
                //записать в переменную
                String p = ps[1];
                //преобразовать в матрицу строк
                String[] pCheckModule = p.split(",");
                boolean[] check = new boolean[pCheckModule.length];
                //преобразовать в boolean
                for (int m = 0; m < pCheckModule.length; m++) {
                    check[m] = pCheckModule[m].equals("true");
                }
                checkModule = check;
            }
        }
        return checkModule;
    }
    //дешифрация строки: получение номеров телефонов
    public String[] profileInTel(String str, int n) {
        String[] telephone = new String[n];

        //преобразование файла в массив строк
        String[] s = str.split("\n");
        //-----анализ строк---------
        for (String value : s) {
            //запись из профиля номеров телефонов
            if (value.startsWith("Number telephone :")) {
                //выделить данные (всегда после ":")
                String[] ps = value.split(":");
                //записать в переменную
                String p = ps[1];
                //преобразовать в матрицу строк
                telephone = p.split(",");
            }
        }
        return telephone;
    }
    //дешифрация строки: получение из профиля признака выбора телефона для управления
    public boolean[] profileInCheckTel(String str, int n) {
        boolean[] checkTel = new boolean[n];
        //преобразование файла в массив строк
        String[] s = str.split("\n");
        //-----анализ строк---------
        for (String value : s) {
            if (value.startsWith("Active telephone checked :")) {
                //выделить данные (всегда после ":")
                String[] ps = value.split(":");
                //записать в переменную
                String p = ps[1];
                //преобразовать в матрицу строк
                String[] pCheckModule = p.split(",");
                boolean[] check = new boolean[pCheckModule.length];
                //преобразовать в boolean
                for (int m = 0; m < pCheckModule.length; m++) {
                    check[m] = pCheckModule[m].equals("true");
                }
                checkTel = check;
            }
        }
        return checkTel;
    }
    //дешифрация строки: получение текстов сообщений вкладки "охрана"
    public String[] profileInSecurity(String str) {
        //преобразование файла в массив строк
        String[] s = str.split("\n");
        String[] sec = new String[2];
        //-----анализ строк---------
        for (String value : s) {
            //запись из профиля
            if (value.startsWith("Message Security :")) {
                //выделить данные (всегда после ":")
                String[] ps = value.split(":");
                //записать в переменную
                String p = ps[1];
                //преобразовать в матрицу строк
                sec = p.split(",");
            }
        }
        return sec;
    }
    //дешифрация строки: получение величин температуры включения отопления узлами
    public String[] profileIntempOpen(String str, int n) {
        String[] tempOpen = new String[n];
        //преобразование файла в массив строк
        String[] s = str.split("\n");
               //-----анализ строк---------
        for (String value : s) {
            //запись из профиля температуры включения отопления узлов RS485
            if (value.startsWith("Temperature open :")) {
                //выделить данные (всегда после ":")
                String[] ps = value.split(":");
                //записать в переменную
                String p = ps[1];
                //преобразовать в матрицу строк
                tempOpen = p.split(",");
            }
        }
        return tempOpen;
    }
    //дешифрация строки: получение величин температуры отключения отопления узлами
    public String[] profileIntempClose(String str, int n) {
        String[] tempClose = new String[n];
        //преобразование файла в массив строк
        String[] s = str.split("\n");
        //-----анализ строк---------
        for (String value : s) {
            //запись из профиля температуры включения отопления узлов RS485
            if (value.startsWith("Temperature close :")) {
                //выделить данные (всегда после ":")
                String[] ps = value.split(":");
                //записать в переменную
                String p = ps[1];
                //преобразовать в матрицу строк
                tempClose = p.split(",");
            }
        }
        return tempClose;
    }
    //дешифрация строки: получение Bot Token
    public String profileToken(String str) {
        String mToken = "";
        //преобразование файла в массив строк
        String[] s = str.split("\n");
        //-----анализ строк---------
        for (String value : s) {
            //запись из профиля токена
            if (value.startsWith("Bot Token :")) {
                //выделить данные (всегда после ":")
                String[] ps = value.split("Token :");
                //записать в переменную
                mToken = ps[1];
            }
        }
        return mToken;
    }
    //дешифрация строки: получение chat_id
    public String profileChatId(String str) {
        String mId = "";
        //преобразование файла в массив строк
        String[] s = str.split("\n");
        //-----анализ строк---------
        for (String value : s) {
            //запись из профиля токена
            if (value.startsWith("Chat_id :")) {
                //выделить данные (всегда после ":")
                String[] ps = value.split(":");
                //записать в переменную
                mId = ps[1];
            }
        }
        return mId;
    }
 }
