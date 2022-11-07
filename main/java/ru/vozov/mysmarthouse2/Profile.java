package ru.vozov.mysmarthouse2;

import android.app.Activity;

import androidx.annotation.NonNull;

import java.util.Arrays;

import ru.vozov.mysmarthouse2.ui.DevicesDialogFragment;

import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;

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
        String nameMod = "";
        String checkMod = "";
        String numTel = "";
        String activeTel = "";
        String mesSec = "";
        String temIn = "";
        String temOut = "";
        for (int i = 0; i<name.length; i++) {
            nameMod += name[i] + ",";
            checkMod += check[i] + ",";
            temIn += tempIn[i] + ",";
            temOut += tempOut[i] + ",";
        }
        for (int i = 0; i<tel.length; i++) {
            numTel += tel[i] + ",";
            activeTel += checkTel[i] + ",";
        }
        for (int i = 0; i<messageSecurity.length; i++) {
            mesSec += messageSecurity[i] + ",";
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
        for (int i = 0; i < s.length; i++) {
            //запись из профиля наименования узлов RS485
            if (s[i].startsWith("RS485 device :")) {
                //выделить данные (всегда после ":")
                String[] ps = s[i].split(":");
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
        for (int i = 0; i < s.length; i++) {
            if (s[i].startsWith("RS485 device checked :")) {
                //выделить данные (всегда после ":")
                String[] ps = s[i].split(":");
                //записать в переменную
                String p = ps[1];
                //преобразовать в матрицу строк
                String[] pCheckModule = p.split(",");
                boolean[] check = new boolean[pCheckModule.length];
                //преобразовать в boolean
                for (int m = 0; m < pCheckModule.length; m++) {
                    if (pCheckModule[m].equals("true")) check[m] = true;
                    else check[m] = false;
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
        for (int i = 0; i < s.length; i++) {
            //запись из профиля номеров телефонов
            if (s[i].startsWith("Number telephone :")) {
                //выделить данные (всегда после ":")
                String[] ps = s[i].split(":");
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
        for (int i = 0; i < s.length; i++) {
            if (s[i].startsWith("Active telephone checked :")) {
                //выделить данные (всегда после ":")
                String[] ps = s[i].split(":");
                //записать в переменную
                String p = ps[1];
                //преобразовать в матрицу строк
                String[] pCheckModule = p.split(",");
                boolean[] check = new boolean[pCheckModule.length];
                //преобразовать в boolean
                for (int m = 0; m < pCheckModule.length; m++) {
                    if (pCheckModule[m].equals("true")) check[m] = true;
                    else check[m] = false;
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
        for (int i = 0; i < s.length; i++) {
            //запись из профиля
            if (s[i].startsWith("Message Security :")) {
                //выделить данные (всегда после ":")
                String[] ps = s[i].split(":");
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
        for (int i = 0; i < s.length; i++) {
            //запись из профиля температуры включения отопления узлов RS485
            if (s[i].startsWith("Temperature open :")) {
                //выделить данные (всегда после ":")
                String[] ps = s[i].split(":");
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
        for (int i = 0; i < s.length; i++) {
            //запись из профиля температуры включения отопления узлов RS485
            if (s[i].startsWith("Temperature close :")) {
                //выделить данные (всегда после ":")
                String[] ps = s[i].split(":");
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
        for (int i = 0; i < s.length; i++) {
            //запись из профиля токена
            if (s[i].startsWith("Bot Token :")) {
                //выделить данные (всегда после ":")
                String[] ps = s[i].split("Token :");
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
        for (int i = 0; i < s.length; i++) {
            //запись из профиля токена
            if (s[i].startsWith("Chat_id :")) {
                //выделить данные (всегда после ":")
                String[] ps = s[i].split(":");
                //записать в переменную
                mId = ps[1];
            }
        }
        return mId;
    }
 }
