package ru.vozov.mysmarthouse2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import ru.vozov.mysmarthouse2.ui.ClimateFragment;
import ru.vozov.mysmarthouse2.ui.DevicesDialogFragment;
import ru.vozov.mysmarthouse2.ui.MainFragment;
import ru.vozov.mysmarthouse2.ui.SMScomDialogFragment;
import ru.vozov.mysmarthouse2.ui.SecurityFragment;
import ru.vozov.mysmarthouse2.ui.TelDialogFragment;
import ru.vozov.mysmarthouse2.ui.TelegramDialogFragment;
import ru.vozov.mysmarthouse2.usbSerial.driver.UsbSerialDriver;
import ru.vozov.mysmarthouse2.usbSerial.driver.UsbSerialPort;
import ru.vozov.mysmarthouse2.usbSerial.driver.UsbSerialProber;
import static java.nio.charset.StandardCharsets.UTF_8;
//AppCompatActivity - Базовый класс для Activity, которые хотят использовать
// некоторые новые функции платформы на старых устройствах Android
public class MainActivity extends AppCompatActivity
    implements
    //реализуется обратный интерфейс с классом DevicesDialogFragment
    DevicesDialogFragment.CallBackActivity
        //реализуется обратный интерфейс с классом DevicesDialogFragment
    , TelDialogFragment.CallBackActivity
        //реализуется обратный интерфейс с классом MainFragment
    , MainFragment.CallBackActivity
        //реализуется обратный интерфейс с классом SecurityFragment
    , SecurityFragment.CallBackActivity
        //реализуется обратный интерфейс с классом ClimateFragment
    , ClimateFragment.CallBackActivity
        //реализуется обратный интерфейс с классом telegramDialogFragment
    , TelegramDialogFragment.CallBackActivity
        //реализуется обратный интерфейс с классом SMScomDialogFragment
    , SMScomDialogFragment.CallBackActivity
   {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
       //===создать широковещательный приемник, который будет отслеживать входящие SMS====
       BroadcastReceiver smsReceiver = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           if(intent.getAction().equals(SMS_RECEIVED)) {
               //---получить входящее SMS сообщение---
               Bundle bundle = intent.getExtras();
               SmsMessage[] msgs;
               String tel = "";
               StringBuilder sms = new StringBuilder();
               StringBuilder str = new StringBuilder();
               if (bundle != null) {
//---извлечь полученное SMS ---
                   Object[] pdus = (Object[]) bundle.get("pdus");
                   msgs = new SmsMessage[pdus.length];
                   for (int n = 0; n < msgs.length; n++) {
                       msgs[n] = SmsMessage.createFromPdu((byte[]) pdus[n]);
                       tel = msgs[n].getOriginatingAddress();
                       str.append("SMS from ").append(tel);
                       str.append(" :");
                       str.append(msgs[n].getMessageBody());
                       sms.append(msgs[n].getMessageBody());
                       str.append("\n");
                   }
//---Показать новое SMS сообщение---
                   Toast.makeText(context, str.toString(), Toast.LENGTH_LONG).show();
                   if(scan[3])//есть признак передачи команд через SMS
                   {//---отослать на дальнейшую обработку -----
                       smsCom(tel, sms.toString());
                   }
               }
           }
       }
   };
       //=================================================================================
    private TextView mainText;
    public int baudRate = 19200;//скорость обмена по последовательному порту
    public int dataBits = UsbSerialPort.DATABITS_8;//размер слова данных
    public int stopBits = UsbSerialPort.STOPBITS_1;//количество стоп битов
    public int paritty = UsbSerialPort.PARITY_EVEN;//паритет четности
    private UsbSerialPort port;//экземпляр интерфейса
    private String msgParameters = "";
    private final byte[] request = {0,3,0,0,0,3,1,1};//запрос
    public byte[] response = new byte[128];//ответ байт
    private static final int WRITE_WAIT_MILLIS = 5;//тайм-аут на передачу данных
    private static final int READ_WAIT_MILLIS  = 50;//тайм-аут на получение ответных данных
    private Handler mHandler;//временной поток
    //интервал времени(ms) ожидания ответа на запрос от ВУ
    private static final long PERIOD_WAIT_RESP = 50;//было 10
    //интервал времени(ms) между опросами модулей ВУ (с учетом на ошибку ответа)
    //почему-то если ответа нет время ожидания 40-50мс
    private static final long PERIOD_WAIT_ED = 200;//было 100
    //интервал времени (ms) ожидания ответа на команду посланную ВУ, включая запрос/ответ по BLE
    private static final long PERIOD_WAIT_COM = 2000;
    //задержка включения охраны после нажатия кнопки - 3 мин., а также время работы сигнализации
    private static final long WAIT_ACTIVE_SECURITY = 3*60*1000;
    //номер опрашиваемого в цикле ВУ
    private int mi;
    //число циклов опроса ВУ между запросом на получение новых сообщений от Telegram
    private int mj = 3;//12000 ms
    private boolean[] scan = {false, false, false, false, false};
                                                    //scan[0]-признак сканирования устройств
                                                    //scan[1]-признак отсылки SMS
                                                    //scan[2]- признак отсылки сообщений в Telegram
                                                    //scan[3]- признак передачи команд через SMS
                                                    //scan[4]- признак передачи команд через Telegram
    private boolean attrSecurity = false; //признак намерения включения охраны
    private boolean securityActive = false;//признак включения охраны после задержки
    static private final int n = 16+1;//число модулей RS485
    //static private final boolean[] errorModule = new  boolean[n];//признаки ошибок обмена с модулями
    // команды отсылаемые на модули RS485 - 8 байт
       private byte[] comByte0 = new byte[8];//команды вкл.\выкл. сигнализации (охрана)
       private byte[] comByte1 = new byte[8];//команды дешифрованные из SMS
       private byte[] comByte2 = new byte[8];// команды выполнения сценария
    private final boolean[] flagCom = {false,false,false};//признак команды для исполнения
                  //flagCom[0]: команда вкл.\выкл. сигнализации, наивысший приоритет
                 //flagCom[1]: команда поступившая через SMS
                //flagCom[2]: команда исполняемая по сценарию, низший приоритет
    private boolean flagOffAlarm = false;//признак задержки отключения сигнализации
    //errorModule[0] - общая ошибка обмена
    //массив признаков управления котлом, его члены:
              //0 - номер узла домашней сети к которому подключен котел
              //1 - номер узла домашней сети который управляет вкл./выкл. котла
              //2 - признак включения режима "КЛИМАТ" - 1, выключение - 0
              static private byte[] controlBoiler = new byte[3];
    //====ДАННЫЕ СОХРАНЯЕМЫЕ В ПРОФИЛЕ=================================
    private final String path = "Profile";//указание места расположения файла профиля
    boolean FistBegin = false;//признак первого включения программы
    //наименование модулей RS485 и признак подключения
    static private String[] nameModule = new String[n];
    static private boolean[] checkActiveModule = new boolean[n];
    //номера телефонов для SMS оповещения и признак выбора SMS управления
    static private final int m = 6+1;// max число номеров
    static private String[] numberTel = new String[m];
    static private boolean[] checkActiveTel = new boolean[m];
    //тексты сообщений
    static private String[] message = {"сработал датчик перемещений", "sim sim open"
            , "sim sim close", "состояние" };
    //тексты команд для объектов
    static private final String[] comText = {"мотор", "свет", "вода", "сирена", "отопление", "розетка"};
    //температура включения/выключения отопления для каждого модуля
    static private String[] tempIn = new String[n];
    static private String[] tempOut = new String[n];
    //двумерный массив состояний обьектов сети (вып.команды):
       //- первый индекс, число модулей n;
       //- второй индекс, число обектов модуля, тоже n
       //если содержимое ячейки =1 включено, если =0 выключено
    private byte[][] stateObject = new byte[n][n];
       //===================================================================
    private FragmentManager fm;
    private int numberTabSelect = 0;//номер выбранной вкладки
    //private static final String ACTION_USB_PERMISSION =
    //        "com.android.example.USB_PERMISSION";
    private DialogFragment fragmentDialog;
    private final Profile profile = new Profile();//создание объекта на основе класса Profile
    private final DecryptMessages decryptMessages = new DecryptMessages();//создание объекта на основе класса
    private String outMesRemember = "";//последнее запомненное передаваемое сообщение
    private String[] tempCurrent = new String[n];//текущее значение температуры узла
    private String[] humidityCurrent = new String[n];//текущее значение влажности
    private String[] humidityGround = new String[n]; //текущее значение влажности земли
    //---Telegram----------------------------
    //bot:@MySmartHouse2_bot
    public static String token = "bot_token";
    //public static String token = "";
    //канал:MySmartHouse2
    public static String id = "chat_id";
    //public static String id = "";
    private String updateTelegram = "";//новое сообщение в канале
    private String upIdRemember = "";//номер последнего запомненного сообщения из Telegram
    //============================================================================================
    @Override
    //Вызывается когда Activity стартует
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//вызов конструктора super класса
        for (int i = 1; i < n; i++) {
            nameModule[i] = "Присвойте имя";
            checkActiveModule[i] = false;
            tempIn[i] = "no";
            tempOut[i] = "no";
            tempCurrent[i] = "н/д";
            humidityCurrent[i] = "н/д";
            humidityGround[i] = "н/д";
        }
        for (int i = 1; i < m; i++) {
            numberTel[i] = "Введите номер";
            checkActiveTel[i] = false;
        }
        mHandler = new Handler();//создать поток
        readFile();//чтение параметров из файла профиля
        //Установить отображение содержания на основе макета ресурсов.
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //привязка шаблона отображения к объекту
        TabLayout tabLayout = findViewById(R.id.tabLayout1);
        //создание вкладок
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_text_1));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_text_2));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_text_3));
        //tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_text_4));
        //tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_text_5));
        //создать FragmentManager для управления фрагментами
        fm = this.getSupportFragmentManager();
        //по умолчанию добавляется главный фрагмент
        MainFragment fragment = new MainFragment();
        fm.beginTransaction()
                .replace(R.id.container1, fragment)
                .commit();
        //создание класса-слушателя на событие выбора вкладки
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String nameTab = (String) tab.getText();
                assert nameTab != null;
                if (nameTab.equals(getString(R.string.tab_text_1))) {
                    MainFragment fragment = new MainFragment();
                    fm.beginTransaction()
                            .replace(R.id.container1, fragment)
                            .commit();
                    numberTabSelect = 0;
                }
                if (nameTab.equals(getString(R.string.tab_text_2))) {
                    SecurityFragment fragment = new SecurityFragment();
                    fm.beginTransaction()
                            .replace(R.id.container1, fragment)
                            .commit();
                    numberTabSelect = 1;
                }
                if (nameTab.equals(getString(R.string.tab_text_3))) {
                    ClimateFragment fragment = new ClimateFragment();
                    fm.beginTransaction()
                            .replace(R.id.container1, fragment)
                            .commit();
                    numberTabSelect = 2;
                }
                /*
                if (nameTab.equals(getString(R.string.tab_text_4))) {
                    LightingFragment fragment = new LightingFragment();
                    fm.beginTransaction()
                            .replace(R.id.container1, fragment)
                            .commit();
                    numberTabSelect = 3;
                }
                if (nameTab.equals(getString(R.string.tab_text_5))) {
                    WateringFragment fragment = new WateringFragment();
                    fm.beginTransaction()
                            .replace(R.id.container1, fragment)
                            .commit();
                    numberTabSelect = 4;
                }
                */
             }

            //происходит после закрытия выбранной вкладки
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            //происходит при повторном выборе вкладки
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //привязка шаблона отображения строки состояния
        mainText = findViewById(R.id.text_view1);
        // Находим все доступные драйверы от подключенных USB устройств
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().
                findAllDrivers(manager);
        //если отсутствуют USB устройства или они не того типа, возврат
        if (availableDrivers.isEmpty()) {
            statusUpdate("Отсутствуют подключенные устройства USB/RS485. " +
                    "Необходимо установить параметры в  меню: НАСТРОЙКИ," +
                    " подключить устройство и задать режим OTG.");
            return;
        }
        // Открываем соединение с первым доступным драйвером
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // добавляем здесь обработку UsbManager.requestPermission(driver.getDevice(), ..)
            statusUpdate("Подключиться к устройству USB/RS485 не удалось.");
            return;
        }
        port = driver.getPorts().get(0); // Большинство устройств имеют только один порт (порт 0)
        try {
            port.open(connection);
            port.setParameters(baudRate, dataBits, stopBits, paritty);
            msgParameters = "Соединение USB/RS485 установлено. скорость обмена:" + baudRate +
                    "; слово данных:" + dataBits + "; количество стоп-битов:" + stopBits +
                    "; паритет:" + paritty;
            statusUpdate(msgParameters);
        } catch (Exception e) {
            statusUpdate("Ошибка соединения. " + e.getMessage());
            disconnect();
        }
        
      }
    //вызывается когда приложение становится активным
      @Override
      public void onResume() {
          super.onResume();
        //регистрация широковещательного приемника
          IntentFilter filter = new IntentFilter(SMS_RECEIVED);
          registerReceiver(smsReceiver, filter);
      }
    //Вызывается когда приложение становится не активным
    @Override
    protected void onPause() {
        super.onPause();
        //внесение изменений параметров в профиль пользователя
        writeFile();//запись параметров в файл профиля
        unregisterReceiver(smsReceiver);//отменить регистрацию приемника
    }
    //Вызывается когда приложение закрывается
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //========методы активности относящиеся к "меню"=========================
    // Раздуть меню; это добавляет элементы в панель действий, если она присутствует.
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.menu_activity_main, menu);
       return true;
   }
    //метод, вызывается когда пользователь открывает меню панели
    //метод при нажатии значка меню вызывается дважды и поэтому
    //необходима проверка на ноль для первого раза
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {//featureId - означает наличие панели на которой находится меню
        if (menu != null) {
            //варианты отображения групп меню для разных вкладок
            //menu.setGroupVisible(R.id.group1, numberTabSelect == 1);
            menu.setGroupVisible(R.id.group2, numberTabSelect == 0);
        }
        return super.onMenuOpened(featureId, menu);
    }
        //метод обработки нажатия элемента меню
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                //ввод имени подключенных устройств через окно-диалог
                case R.id.number_devices:
                    fragmentDialog = new DevicesDialogFragment();
                    fragmentDialog.show(getSupportFragmentManager(), "DevicesDialogFragment");
                    return true;
                //ввод номеров телефонов для SMS оповещения/управления
                case R.id.sms_tel:
                    fragmentDialog = new TelDialogFragment();
                    fragmentDialog.show(getSupportFragmentManager(), "TelDialogFragment");
                    return true;
                //ввод идентификационных  данных канала TELEGRAM
                case R.id.telegram:
                    fragmentDialog = new TelegramDialogFragment();
                    fragmentDialog.show(getSupportFragmentManager(),"TelegramDialogFragment");
                    return true;
                //изменение команд SMS
                case R.id.com_sms:
                    fragmentDialog = new SMScomDialogFragment();
                    fragmentDialog.show(getSupportFragmentManager(),"SMScomDialogFragment");
                    return true;
                default:
                    return super.onOptionsItemSelected(item);//возврат false
                }
    }
    //==============================================================================
    //============МЕТОДЫ обратного вызова интерфейса класса DevicesDialogFragment===========
    //класс получает ссылку на эту деятельность через Fragment.onAttach (),
    //обратный вызов, который он использует для вызова следующих методов
    @Override//получение запомненных значений
    public String[] inDataName() {
        return nameModule;
    }
    public boolean[] inDataCheck() {
        return checkActiveModule;
    }
    @Override//запись значений
    public void outData(String[] name, boolean[] check) {
        nameModule = name;
        checkActiveModule = check;
    }
    @Override//удаление диалога, обновление главного фрагмента
    public void replaceMainFragment() {
        fragmentDialog.dismiss();//удаление диалога
        MainFragment fragment = new MainFragment();
        fm.beginTransaction()
                .replace(R.id.container1, fragment)
                .commit();
    }
    //=================================================================================
    //============МЕТОДЫ обратного вызова интерфейса класса TelDialogFragment===========
    //класс получает ссылку на эту деятельность через Fragment.onAttach (),
    //обратный вызов, который он использует для вызова следующих методов
    @Override//получение запомненных значений
    public String[] inTelNumber() {
        return numberTel;
    }
    public boolean[] inTelCheck() {
        return checkActiveTel;
    }
    @Override//запись значений
    public void outDataTel(String[] name, boolean[] check) {
        numberTel = name;
        checkActiveTel = check;
    }
    @Override//удаление диалога, обновление главного фрагмента
    public void replaceMainFragment1() {
        fragmentDialog.dismiss();//удаление диалога
        MainFragment fragment = new MainFragment();
        fm.beginTransaction()
                .replace(R.id.container1, fragment)
                .commit();
    }
    //=============================================================================
    //======МЕТОДЫ обратного вызова интерфейса класса TelegramDialogFragment===========
    //класс получает ссылку на эту деятельность через Fragment.onAttach (),
    //обратный вызов, который он использует для вызова следующих методов
    @Override//получение токена бота
    public String inToken() {
        return token;
    }
    @Override//получение кода канала
    public String inId() {
        return id;
    }
    @Override//удаление диалога, обновление главного фрагмента
    public void replaceMainFragment2() {
        fragmentDialog.dismiss();//удаление диалога
        MainFragment fragment = new MainFragment();
        fm.beginTransaction()
                .replace(R.id.container1, fragment)
                .commit();
    }
    @Override//запись нового токена
    public void outToken(String mToken) {
        token = mToken;
    }
    public void outId(String mId) {
        id = mId;
    }
    //=============================================================================
    //======МЕТОДЫ обратного вызова интерфейса класса SMScomDialogFragment===========
    //класс получает ссылку на эту деятельность через Fragment.onAttach (),
    //обратный вызов, который он использует для вызова следующих методов
       @Override//получение наименования команд
       public String[] inNameCom() {
        String[] name = new String[16];
        name[0] = message[3];//команда выдачи состояния сети
        name[1] = getString(R.string.tab_text_3);//наименование вкладки КЛИМАТ
           return name;
       }
       @Override//запись в активность измененных команд
       public void outNameCom(String[] nameOut) {
        message[3] = nameOut[0];
       }
       @Override//удаление диалога, обновление главного фрагмента
       public void replaceMainFragment3() {
           fragmentDialog.dismiss();//удаление диалога
           MainFragment fragment = new MainFragment();
           fm.beginTransaction()
                   .replace(R.id.container1, fragment)
                   .commit();
       }
    //============МЕТОДЫ обратного вызова интерфейса класса MainFragment===========
    //класс получает ссылку на эту деятельность через Fragment.onAttach (),
    //обратный вызов, который он использует для вызова следующих методов
    @Override//остановить сканирование
    public void stopScanNetwork() {
        scan[0] = false;
    }
    @Override//получение признака сканирования от активности
       public boolean[] inAttributeScanNet() { return scan;  }
    @Override//запись признаков сканирования в активность
       public void outAttributeScanNet(boolean[] data) {
           scan = data;
          }
    @Override//инициализация сканирования домашней сети
    public void initScanNetwork(){ //ФОРМИРОВАНИЕ ПЕРИОДИЧЕСКОГО ОПРОСА МОДУЛЕЙ RS485
        //запуск потока на время, по окончании
//выполняется метод run
        mHandler.postDelayed(() -> {
            //цикл опроса устройств RS485
                mi =0;//номер опрашиваемого ВУ
               ciklDataExtDevice();
             },PERIOD_WAIT_ED);
    }
       //=================================================================================
       //============МЕТОДЫ обратного вызова интерфейса класса SecurityFragment===========
       //класс получает ссылку на эту деятельность через Fragment.onAttach (),
       //обратный вызов, который он использует для вызова следующих методов
       @Override//получение из активности данных вкладки
       public String[] inAttrSecurity() {
           String[] atSec = new String[4];
           if(attrSecurity||securityActive) atSec[0] = "true";else atSec[0] = "false";
           atSec[1] = message[0]; atSec[2] = message[1]; atSec[3] = message[2];
           return atSec;
       }
       @Override//запись в активность данных вкладки
       public void outAttrSecurity(String[] outData) {
           message[0] = outData[1];
           message[1] = outData[2];
           message[2] = outData[3];
           boolean attSec = Boolean.parseBoolean(outData[0]);
           if(!attrSecurity&attSec)//если включена кнопка охраны
           {
               attrSecurity = true;
               //формирование задержки для включения сигнализации
               //запуск потока на время, по окончании
//выполняется метод run
               mHandler.postDelayed(() -> {
                   securityActive = true;
                   outMesRemember = "ОХРАНА ВКЛЮЧЕНА!";
                   sendMessage();//отсылка сообщения
               }, WAIT_ACTIVE_SECURITY);
           }
           if((attrSecurit||securityActive)&!attSec)//если выключается кнопка охраны
           {
               attrSecurity = false;
               securityActive = false;
               outMesRemember = "ОХРАНА ОТКЛЮЧЕНА!";
               sendMessage();//отсылка сообщения
           }
       }
       //===============================================================================
       //============МЕТОДЫ обратного вызова интерфейса класса ClimateFragment===========
       //класс получает ссылку на эту деятельность через Fragment.onAttach (),
       //обратный вызов, который он использует для вызова следующих методов
       @Override//получение значений
             public String[] inDataName1() {
                 return nameModule;
             }
             public boolean[] inDataCheck1() {
                 return checkActiveModule;
             }
             public String[] inTempIn() {
                return tempIn;
             }
             public String[] inTempOut() {
                return tempOut;
             }
             public String[] inTempCurrent() {return tempCurrent;}
             public String[] inHumidityCurrent() {return humidityCurrent;}
             public String[] inHumidityGround() {return humidityGround;}
             public byte[] inControlBoiler() {return controlBoiler;}
       @Override//запись значений
             public void outDataTemp(String[] outTempIn, String[] outTempOut,
                                     byte[] outControlBoiler) {
                tempIn = outTempIn;
                tempOut = outTempOut;
                controlBoiler = outControlBoiler;
       }
       @Override//обновление фрагмента
       public void replaceClimateFragment() {
           ClimateFragment fragment = new ClimateFragment();
           fm.beginTransaction()
                   .replace(R.id.container1, fragment)
                   .commit();
       }
       //=================================================================================
    //-----------п/п относящиеся к организации сканирования ВУ-------------------------
    //посылка запроса на выбранное ВУ
    private void requestExtDevice(byte[] mRequest)
    {
        try {
            String req = Arrays.toString(mRequest);
            msgParameters = "Запрос: " + req;
            statusUpdate(msgParameters);
            port.write(mRequest, WRITE_WAIT_MILLIS);
        } catch (IOException e) {
            //e.printStackTrace();
            statusUpdate("Ошибка запроса: " + e.getMessage());
        }
    }
    //цикл опроса всех ВУ
    private void ciklDataExtDevice()
    {
        //запуск потока на время, по окончании
//выполняется метод run
        mHandler.postDelayed(() -> {
            mi++;
            if (mi <n)
            {
           //если узел активный
             if (checkActiveModule[mi]) {
              int numberExtDevice = mi;//номер ВУ с которым в данный момент происходит обмен
                byte[] pRequest = request;
                  pRequest[0] = (byte) mi;//адрес устройства
                        //посылка запроса
                  requestExtDevice(pRequest);
                        //получение ответа
                 //запуск потока на время, по окончании
//выполняется метод run
                 mHandler.postDelayed(() -> {
       try {
             int len = port.read(response, READ_WAIT_MILLIS);
                         byte[] resp = new byte[len];
                         if (len > 0) {
                             int j = len;
                             while (j != 0) {
                                 resp[len - j] = response[len - j];
                                 j = j - 1;
                             }
         //Анализ полученного ответа от модуля блока (обращение к методу)
                 analysisResp(resp);
                   } else {
                             msgParameters = "Ответа нет";
                             statusUpdate(msgParameters);
        //посылка запроса повтор
                   requestExtDevice(pRequest);
        //получение ответа на повторный запрос
                             //запуск потока на время, по окончании
//выполняется метод run
                             mHandler.postDelayed(() -> {
                   try {
                       int len1 = port.read(response, READ_WAIT_MILLIS);
                         byte[] resp1 = new byte[len1];
                           if (len1 != 0) {
                              int j = len1;
                            while (j != 0) {
                              resp1[len1 - j] = response[len1 - j];
                                     j = j - 1;
                                                }
       //Анализ полученного ответа от модуля блока (обращение к методу)
                         analysisResp(resp1);
                           } else {
                   msgParameters = "Ответа нет";
                       statusUpdate(msgParameters);
                       //исключаем данный узел из активных
                       checkActiveModule[numberExtDevice] = false;
                       //записываем признак ошибки обмена
                       //errorModule[numberExtDevice] = true;
                   //обращение к п\п отсылки сообщения
                        outMessage(nameModule[mi]+": "+ msgParameters);
                                            }
              } catch (IOException e) {
                 //e.printStackTrace();
              statusUpdate("Ошибка ответа: " + e.getMessage());
                   }
                                    }, PERIOD_WAIT_RESP);
                   }
                     } catch (IOException e) {
                         //e.printStackTrace();
                         statusUpdate("Ошибка ответа: " + e.getMessage());
                     }

                 }, PERIOD_WAIT_RESP);
                    }
                ciklDataExtDevice();//запуск опроса следующего ВУ
            }
          else {
              if(scan[4]) { //есть признак получения команды из telegram-канала
                  //формирование таймера для периодическуого опроса Telegram
                  mj--;
                  if (mj == 0) {
                      //получение нового сообщения из telegram-канала(команда)
                      new GetMessageTelegram().execute();
                      mj = 3;
                  }
              }
              doCom();//запуск п/п записи команды во ВУ через RS485
            }
        }, PERIOD_WAIT_ED);
    }
    //-----------------------------------------------------------------------------
    //===========Метод анализа полученного ответа от датчиков модуля===============
    private void analysisResp(byte[] mResp) {
        String res = Arrays.toString(mResp);
        msgParameters = "Получен ответ: " + res;
        //statusUpdate(msgParameters);
        //дешифрация полученного ответа от блока для получения текущей температуры воздуха
        tempCurrent = decryptMessages.decryptTemp(mResp, tempCurrent);
        //дешифрация полученного ответа от блока для получения текущей влажности воздуха
        humidityCurrent = decryptMessages.decryptHumidity(mResp, humidityCurrent);
        //дешифрация полученного ответа от блока для получения текущей влажности земли
        humidityGround = decryptMessages.decryptHumidityGround(mResp, humidityGround);
        //дешифрация полученного ответа от блока для сообщений
        String decRec = decryptMessages.decryptMessagesModule(mResp, nameModule, message);
        //запись состояний управляемых выходов блока в матрицу состояния объектов
        stateObject = decryptMessages.changeStateObject(mResp, stateObject);
        //обращение к п\п отсылки сообщения
        outMessage(decRec);
        //если "сработал датчик перемещений", включена охрана,
        //сигнализация ранее не была запущена
        if(decRec.endsWith(message[0])&securityActive&(!flagOffAlarm))
        {
            flagCom[0]=true;//установить флаг передачи команды на ВУ
            //устанавливаем код команды:
            //адрес - 0б=0, широковещательная рассылка
            //включить - 4б=64; сигнализацию - 5б=8+2 и свет;
            comByte0 = new byte[]{0, 6, 0, 0, 64, 10, 1, 1};
            flagOffAlarm = true;
            //запустить п/п отключения сигнализации
            offAlarm();
        }
    }
    //=============================================================================
       //==========Метод передачи команды на исполнение ВУ модуля RS485============
    private void doCom()
    {
        //проверка условий сценариев
 comByte2=decryptMessages.decryptScriptClimate(checkActiveModule,tempCurrent,tempIn,tempOut,stateObject,controlBoiler);
            if(comByte2[0]!=0) {
            //если есть команда для выполнения условий сценариев (адрес исполняемой команды не нулевой)
              flagCom[2] = true;
            }
        if(flagCom[0]||flagCom[1]||flagCom[2])
        {//если есть хотя бы одна команда
            //запуск потока на время, по окончании
//выполняется метод run
            mHandler.postDelayed(() -> {
                if (flagCom[0]) //если есть признак команды высшего уровня для исполнения (охрана)
                {
                    //посылка запроса
                    requestExtDevice(comByte0);
                    flagCom[0] = false;//снять признак отправки команды
                    //запуск потока на время, по окончании
//выполняется метод run
                    mHandler.postDelayed(() -> {//здесь ничего нет, так как широковещательная
                        //рассылка не требует ответа
                        //если есть признак сканирования, запуск повторного цикла сканирования
                        if (scan[0]) {
                            initScanNetwork();
                        }
                    }, PERIOD_WAIT_COM); //повышенная задержка, для того чтобы освободился
                    //процессор ВУ от передачи команды по BLE
                }

                if (flagCom[1] & !flagCom[0])//если есть признак команды из SMS,
                //и нет признака команды охраны
                {
                    returnCom(comByte1);
                    //flagCom[1] = false;//снять признак отправки команды
                    //если есть признак сканирования, запуск повторного цикла сканирования
                    //if (scan[0]) {initScanNetwork();}
                }
                if (flagCom[2] & !flagCom[0] & !flagCom[1])//если есть признак команды
                // по сценариям и нет признака: команды охраны; команды из SMS
                {
                    returnCom(comByte2);
                    //flagCom[2] = false;//снять признак отправки команды
                    //если есть признак сканирования, запуск повторного цикла сканирования
                    //if (scan[0]) {initScanNetwork();}
                }

            }, PERIOD_WAIT_ED);
        } else{//в случае отсутствия команд
                //если есть признак сканирования, запуск повторного цикла сканирования
                if (scan[0]) {
                    initScanNetwork();
                }
            }
 }
    //метод получения ответа от модуля узла после посылки команды
    private void returnCom(byte[] mComByte)
    {
//посылка запроса
        requestExtDevice(mComByte);
        //запуск потока на время, по окончании
//выполняется метод run
        mHandler.postDelayed(() -> {
            try {
                int len = port.read(response, READ_WAIT_MILLIS);
                byte[] resp = new byte[len];
                if (len == 8) {
                    int j = len;
                    while (j != 0) {
                        resp[len - j] = response[len - j];
                        j = j - 1;
                    }
                    String res = Arrays.toString(resp);
                    msgParameters = "Получен ответ: " + res + ";число байтов: " + len;
                    statusUpdate(msgParameters);
                    }
                    //нормальный ответ повторяет запрос
                    if (Arrays.equals(resp, mComByte)) {
                        if (flagCom[2] & !(flagCom[1]))//если выполняется команда сценария
                        {
                            byte by = degree(mComByte[5]);//преобразовать число в разряд
                            if (mComByte[4] == 64) {//если команда включить,
                                // изменить состояние объекта на включено
                                stateObject[mComByte[0]][by] = 1;
                            } else {
                                stateObject[mComByte[0]][by] = 0;
                            }
                            flagCom[2] = false;//сбросить флаг
                        }
                        flagCom[1] = false;//сбросить флаг
                        //если есть признак сканирования, запуск повторного цикла сканирования
                        if (scan[0]) {initScanNetwork();}
                    } else {
                    //посылка запроса повтор
                    requestExtDevice(mComByte);
                    //получение ответа на повторный запрос
                        //запуск потока на время,
// по окончании выполняется метод run
                        mHandler.postDelayed(() -> {
                            try {
                                int plen = port.read(response, READ_WAIT_MILLIS);
                                byte[] presp = new byte[plen];
                                if (plen == 8) {
                                    int pj = plen;
                                    while (pj != 0) {
                                        presp[plen - pj] = response[plen - pj];
                                        pj = pj - 1;
                                    }
                                    //String pres = Arrays.toString(presp);
                                    //msgParameters = "Получен ответ: " + res +  ";число байтов: " + len;
                                    //statusUpdate(msgParameters);

                                    //нормальный ответ повторяет запрос
                                    if (Arrays.equals(presp, mComByte)) {
                                        if (flagCom[2] & !(flagCom[1]))//если выполняется команда сценария
                                        {
                                            byte by = degree(mComByte[5]);//преобразовать число в разряд
                                            if (mComByte[4] == 64) {//если команда включить,
                                                // изменить состояние объекта на включено
                                                stateObject[mComByte[0]][by] = 1;
                                            } else {
                                                stateObject[mComByte[0]][by] = 0;
                                            }
                                            flagCom[2] = false;//сбросить флаг
                                        }
                                        flagCom[1] = false;//сбросить флаг
                                        //если есть признак сканирования, запуск повторного цикла сканирования
                                        if (scan[0]) {initScanNetwork();}
                                    }
                             if (flagCom[1]) {//если команда получена из SMS отсылается ответ
                                             outMesRemember = "Команда не исполнена модулем";
                                            //обращение к п\п отсылки сообщения
                                            sendMessage();
                                    }
                                }
                              if (flagCom[1]) {//если команда получена из SMS отсылается ответ
                                    msgParameters = "Ответа на команду нет";
                                    statusUpdate(msgParameters);
                                    outMesRemember = msgParameters;
                                    //обращение к п\п отсылки сообщения
                                    sendMessage();
                                }
                            } catch (IOException e) {
                                //e.printStackTrace();
                                statusUpdate("Ошибка ответа: " + e.getMessage());
                            }
                            flagCom[2] = false;//сбросить флаг
                            flagCom[1] = false;//сбросить флаг
                  //если есть признак сканирования, запуск повторного цикла сканирования
                            if (scan[0]) {initScanNetwork();}
                        }, PERIOD_WAIT_COM);//повышенная задержка,
                    // для того чтобы освободился
                    //процессор ВУ от передачи команды по BLE
                }
            } catch (IOException e) {
                //e.printStackTrace();
                statusUpdate("Ошибка ответа: " + e.getMessage());
            }
        }, PERIOD_WAIT_COM); //повышенная задержка, для того чтобы освободился
        //процессор ВУ от передачи команды по BLE
    }
    //метод перевода числа (байт) в степень двоичного числа (разряд байта)
    private byte degree(byte mDate)
    {
        byte b =n-1;
        if(mDate==1) b=0;
        if(mDate==2) b=1;
        if(mDate==4) b=2;
        if(mDate==8) b=3;
        if(mDate==16) b=4;
        if(mDate==32) b=5;
        if(mDate==64) b=6;
        return b;
    }
    //============================================================================================
    //============Метод отключения сигнализации через определенное время==========================
    private void offAlarm()
    {
        //запуск потока на время, по окончании
//выполняется метод run
        mHandler.postDelayed(() -> {
            flagCom[0]=true;//установить флаг передачи команды на ВУ
            flagOffAlarm=false;//сбросить флаг задержки выключения сигнализации
            //устанавливаем код команды:
            //адрес - 0б=0, широковещательная рассылка
            //выключить - 4б=0; сигнализацию - 5б=8+2 и свет;
            comByte0 = new byte[]{0, 6, 0, 0, 0, 10, 1, 1};
        },WAIT_ACTIVE_SECURITY );
    }
    //============================================================================================

       //==============ЧТЕНИЕ\ЗАПИСЬ ФАЙЛА ПРОФИЛЯ====================================
    //создание файла профиля для записи
    //во внутреннею память (при удалении приложения файл уничтожается)
    private void writeFile() {
        //формирование файла для записи
        String fileString = profile.profileOut(nameModule, checkActiveModule, numberTel, checkActiveTel
        , message, tempIn, tempOut, token, id);
        FileOutputStream out = null;
        try {//открыть/создать файл с заданным именем
            //в режиме только для этого приложения
            out = openFileOutput(path,MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            //в случае ошибки выдать сообщение
            //e.printStackTrace();
            Toast.makeText(this,R.string.no_file_name,Toast.LENGTH_LONG).show();
        }
        try {//запись содержимого в файл
            assert out != null;
            out.write(fileString.getBytes(UTF_8));//запись файла с кодировкой UTF-8
        } catch (IOException e) {
            //в случае ошибки выдать сообщение
            //e.printStackTrace();
            Toast.makeText(this,R.string.error_write,Toast.LENGTH_LONG).show();
        }
        try {//закрыть файл для записи
            out.close();
        } catch (IOException e) {
            //в случае ошибки выдать сообщение
            //e.printStackTrace();
            Toast.makeText(this,R.string.no_close_file,Toast.LENGTH_LONG).show();
        }
        //если все прошло нормально, выдается сообщение
        Toast.makeText(this,"write file",Toast.LENGTH_LONG).show();
    }
    //метод - чтение файла профиля
    private void readFile(){
          FileInputStream in;
        try {//открыть файл с указанным именем
            in = openFileInput(path);
        } catch (FileNotFoundException e) {
            //если файла нет
            FistBegin = true;//признак первого включения
            //выдать сообщение	"Профиль не создан"
            Toast.makeText(this,R.string.no_file_name,Toast.LENGTH_LONG).show();
            return;
        }
        byte[] bts = new byte[2048];//размер буфера профиля
        int i = 0;
        int bt = 0;
        while (bt != -1){
            try {
                bt = in.read();
            } catch (IOException e) {
                //в случае ошибки выдать сообщение
                Toast.makeText(this,R.string.error_read,Toast.LENGTH_LONG).show();
            }
            bts[i] = (byte)bt;
            i++;
           }
        try {//закрыть файл для чтения
            in.close();
        } catch (IOException e) {
            //в случае ошибки выдать сообщение
            Toast.makeText(this,R.string.no_close_file,Toast.LENGTH_LONG).show();
        }
        String str = new String(bts, UTF_8);//преобразование byte bts[] в строку с кодировкой UTF-8
        Toast.makeText(this,str,Toast.LENGTH_LONG).show();//проверка(выдача на экран)
        if (!str.contentEquals(""))//если полученная строка не нулевая
        {
            //дешифрация файла профиля и запись в таблицы значений
            nameModule = profile.profileInName(str, n);
            checkActiveModule = profile.profileInCheckModule(str, n);
            numberTel = profile.profileInTel(str, m);
            checkActiveTel = profile.profileInCheckTel(str, m);
            message = profile.profileInSecurity(str);
            tempIn = profile.profileIntempOpen(str, n);
            tempOut = profile.profileIntempClose(str, n);
            token = profile.profileToken(str);
            id = profile.profileChatId(str);
        }
    }
    //=========СОЗДАННЫЕ МЕТОДЫ КЛАССА MainActivity=================================
    //вывод текста в строку состояния
    private void statusUpdate (final String msg) {
        runOnUiThread(() -> mainText.setText(msg));
    }
    //отключение USB/RS485 устройства
    private void disconnect() {
       //пока ничего
    }
    //п\п отсылки сообщения
    private void outMessage(String outMes) {
        statusUpdate(outMes);//отображение сообщения в строке состояния
        //если дешифрированный ответ от ВУ не заканчивается на слово "норма" и включен режим охраны
        //в зависимости от выбранного метода передачи отсылается сообщение один раз до замены.
        if(!outMes.endsWith("норма")&(!outMes.equals(outMesRemember))&securityActive)
        {
            outMesRemember = outMes;//запоминается передаваемое сообщение, чтобы не было повтора
            sendMessage();//отсылка сообщения
        }
    }
    //==================================================================================
   //==================== передача/прием SMS===========================
   // метод отсылки сообщения SMS на заданные номера
        private void sendSMS(String message)
        {
            for (String s : numberTel) {
                if (s.length() > 0) {//есть записанный номер
                    try {
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(s, null, message, null,
                                null);
                        statusUpdate("СООБЩЕНИЕ:" + message + "\r\n" + "ОТПРАВЛЕНО SMS");
                    } catch (Exception e) {
                        statusUpdate("СООБЩЕНИЕ:" + message + "\r\n" + "ОШИБКА отправки SMS");
                    }
                }
            }
        }
    //метод анализа полученной команды от выбранного телефона через SMS
         private void smsCom(String tel, String text) {
             for (int i = 1; i < m; i++) {
                 if (tel.equals(numberTel[i]) & checkActiveTel[i])//SMS команда от выбранного телефона
                 {
                     messageCom(text);//вызов метода
                 }
             }
         }
     //метод фиксации команд полученных из сообщений (SMS, Telegram)
      private void messageCom(String text) {
          //если команда включения/отключения охраны; запроса состояния сети;
          // включения/отключения режима климат
          if (text.startsWith(message[1]) || text.startsWith(message[2]) ||
                  text.startsWith(message[3]) || text.startsWith(getString(R.string.tab_text_3))) {
              if (text.startsWith(message[1]))//команда отключения охраны
              {
                  attrSecurity = false;
                  securityActive = false;
                  outMesRemember = "ОХРАНА ОТКЛЮЧЕНА!";
              }
              if (text.startsWith(message[2]))//команда включения охраны
              {
                  securityActive = true;
                  outMesRemember = "ОХРАНА ВКЛЮЧЕНА!";
              }
              if (text.startsWith(message[3]))//команда запроса состояния сети
              {
                  StringBuilder condition = new StringBuilder("Охрана ");
                  if (securityActive) {
                      condition.append("включена.\n");
                  } else {
                      condition.append("отключена\n");
                  }
                  condition.append("Режим КЛИМАТ ");
                  if (controlBoiler[2] == 1) {
                      condition.append("включен.\n");
                  } else {
                      condition.append("отключен\n");
                  }
                  //проверка состояния активных модулей
                  for (int k = 1; k < n; k++) {
                      if (checkActiveModule[k]) {
                          condition.append(nameModule[k]).append(":T=").append(tempCurrent[k]).append("*C;\n");
                          if (!humidityGround[k].equals("н/д")) {
                              condition.append("влажность земли: ").append(humidityGround[k]).append("ед.\n");
                          }
                      }
                  }
                  outMesRemember = condition.toString();
              }
              if (text.startsWith(getString(R.string.tab_text_3)))
              //команда включения/отключения режима климат
              {
                  //преобразование сообщения в массив строк
                  String[] s = text.split(" ");
                  if (s[1].startsWith("вкл") || s[1].startsWith("выкл")) {
                      if (s[1].startsWith("вкл")) {
                          controlBoiler[2] = 1;
                          outMesRemember = "РЕЖИМ КЛИМАТ ВКЛЮЧЕН";
                      }
                      if (s[1].startsWith("выкл")) {
                          controlBoiler[2] = 0;
                          outMesRemember = "РЕЖИМ КЛИМАТ ВЫКЛЮЧЕН";
                      }
                  } else {
                      outMesRemember = "Команда:/" + text + "\n/не выполнена-неправильная команда"
                              + " управления режимом";
                  }
              }
          } else {  //дешифрирование команды
              comByte1 = decryptMessages.decryptSMS(text, nameModule, checkActiveModule, comText);
              if (comByte1[7] == 2) {
                  outMesRemember = "Команда:/" + text + "/не выполнена-неправильное имя";
              }
              if (comByte1[7] == 3) {
                  outMesRemember = "Команда:/" + text + "/не выполнена-модуль не активен";
              }
              if (comByte1[7] == 4) {
                  outMesRemember = "Команда:/" + text + "/не выполнена-неправильная команда";
              }
              if (comByte1[7] == 5) {
                  outMesRemember = "Команда:/" + text + "/не выполнена-объект не найден";
              }
              if (comByte1[7] == 1) {//установка флага принятой команды (для цикла опроса ВУ)
                  flagCom[1] = true;
                  outMesRemember = "Команда:/" + text + "/ выполняется";
                  //return;//выход из метода без отсылки сообщения
              }
          }
          statusUpdate(outMesRemember);//отладка
          sendMessage();//отсылка сообщения
      }
     //метод отсылки сообщений:SMS, Telegram  в зависимости от сделанного выбора пользователем
      private void sendMessage()
     {
         if(scan[1])//если есть признак отсылки SMS
         {
             sendSMS(outMesRemember);
         }
         if(scan[2])//если есть признак отсылки сообщений в Telegram
         {
             //посылка сообщения в telegram
             new SendMessageTelegram().execute();
         }
     }
    //====================================================================================
    //=====внутренний класс передачи сообщений в интернет на канал telegram=============
    //передача выполняется в асинхронном потоке в фоновом режиме посредством класса AsyncTask
    @SuppressLint("StaticFieldLeak")
    private class SendMessageTelegram extends AsyncTask<Void, Void, Void> {
        private String resultString = null;
       //метод для отображения индикатора выполнения в пользовательском интерфейсе
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        // метод для выполнения задач в фоновом режиме
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //адресс бота "телеграмм"
                String myURL =
                        "https://api.telegram.org/bot" + token +"/sendMessage";
                String param = "chat_id=" + id + "&text=" + outMesRemember;
                byte[] data;
                try {//подключаемся к серверу
                    URL url = new URL(myURL);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);//тайм-аут чтения в мс
                    conn.setConnectTimeout(10000);//тайм-аут соединения в мс
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    //задать общее свойство запроса:
                    // - повторное использование соединений HTTP "Keep-Alive";
                    // - тип формата отправки запроса по умолчанию (можно не указывать)
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    //определяем длину передаваемого сообщения
                    conn.setRequestProperty("Content-Length", Integer.toString(param.getBytes().length));
                    OutputStream os = conn.getOutputStream();
                    // конвертируем передаваемую строку в UTF-8
                    data = param.getBytes(StandardCharsets.UTF_8);
                    //передаем данные на сервер
                    os.write(data);
                    os.flush();
                    os.close();
                    conn.connect();
                    int responseCode = conn.getResponseCode();//Представляющие три цифры HTTP Status-Code
                    resultString = Integer.toString(responseCode);
                    conn.disconnect();//отключение соединения
                } catch (MalformedURLException e) {//неправильный адрес сайта

                    resultString = "MalformedURLException:" + e.getMessage();
                } catch (IOException e) {//неудачная операция ввода/вывода

                    resultString = "IOException:" + e.getMessage();
                } catch (Exception e) {//что-то пошло не так

                    resultString = "Exception:" + e.getMessage();
                }

            } catch (Exception e) {
                //e.printStackTrace();
            }
            return null;
        }
    // метод для обработки результата выполнения фоновых задач
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // для отладки
            if (resultString != null) {
             //вывод результата на экран
                Toast.makeText(MainActivity.this, resultString, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "no result", Toast.LENGTH_SHORT).show();
            }

            if (resultString.equals("200")) {//kod '200' - ok сообщение доставлено успешно
                statusUpdate( "СООБЩЕНИЕ:" + outMesRemember + "\r\n" + "ОТПРАВЛЕНО ИНТЕРНЕТ");
                //mFormAdvert.setBD(true);//запись сообщения в базу данных
            } else {
                statusUpdate( "СООБЩЕНИЕ:" + outMesRemember + "\r\n"+"ОШИБКА отправки ИНТЕРНЕТ");
               // mFormAdvert.setBD(false);//запись сообщения в базу данных
                // повторная посылка сообщения в telegram
                //new SendMessageTelegram().execute();
            }
        }
    }
    //=====================================================================================
//====================================================================================
    //=====внутренний класс приема новых сообщений через интернет с канала telegram=============
    //прием выполняется в асинхронном потоке в фоновом режиме посредством класса AsyncTask
    @SuppressLint("StaticFieldLeak")
    private class GetMessageTelegram extends AsyncTask<Void, Void, Void> {
        private String resultString = null;
        private String upId = null;
        // метод для выполнения задач в фоновом режиме
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //адресс бота "телеграмм"+получение последнего сообщения
                String myURL =
                  "https://api.telegram.org/bot" + token +"/getUpdates?offset=-1";
                try {//подключаемся к серверу
                    URL url = new URL(myURL);//создаем объект класса указателя ресурса в интернете
                    //создание объекта соединения  с удаленным объектом, на который ссылается URL.
                    //Приведение его к протоколу обмена HTTPs
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                        //прописываем параметры соединения
                    conn.setReadTimeout(5000);//тайм-аут чтения в мс
                    conn.setConnectTimeout(10000);//тайм-аут соединения в мс
                    conn.setRequestMethod("GET");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();//подключаемся к ресурсу
                    InputStream is = conn.getInputStream();//открыть входной поток для чтения из URL
                    //BufferedReader - Читаем текст из потока ввода символов, буферизуя символы.                    //
                    //Можно указать размер буфера или использовать размер по умолчанию.
                    // Значение по умолчанию достаточно велико для большинства целей.
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder buf = new StringBuilder();//спец.буфер для хранения строк
                    String line;
                    //Чтение строк текста пока не появится нулевая
                    while ((line=reader.readLine()) != null) {
                        buf.append(line).append("\n");//в конце каждой строки доб. символ перевод строки
                    }
                    //полученное новое сообщение из телеграм-канала, ввиде JSONObject
                    //со вложенными JSONObject внутри JSONArray (массива)
                    String newMesTelegram = buf.toString();
                 //выделение текста из сообщения
                    JSONObject jo = new JSONObject(newMesTelegram);
                    JSONArray ja = jo.getJSONArray("result");//первое вложение
                    String sj = ja.toString();
                        //удаление признаков массива
                    sj = sj.replace("[","");
                    sj = sj.replace("]","");
                    //если текст в сообщении присутствует, производим дальнейшую обработку
                    if(sj!=null) {
                        JSONObject jon = new JSONObject(sj);
                        upId = jon.getString("update_id");//номер сообщения
                        JSONObject jonm = jon.getJSONObject("channel_post");//второе вложение
                        //выделение текста из последнего JSONObject и преобразование его в
                        //строку из UTF_8 кода
                     updateTelegram = new String(jonm.getString("text").getBytes(), UTF_8);
                    } else {
                        updateTelegram = "";
                    }
                    is.close();
                    int responseCode = conn.getResponseCode();//Представляющие три цифры HTTP Status-Code
                    resultString = Integer.toString(responseCode);
                    conn.disconnect();//отключение соединения
                } catch (MalformedURLException e) {//неправильный адрес сайта

                    resultString = "MalformedURLException:" + e.getMessage();
                } catch (IOException e) {//неудачная операция ввода/вывода

                    resultString = "IOException:" + e.getMessage();
                } catch (Exception e) {//что-то пошло не так

                    resultString = "Exception:" + e.getMessage();
                }

            } catch (Exception e) {
                //e.printStackTrace();
            }
            return null;
        }
        // метод для обработки результата выполнения фоновых задач
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
/*
            // для отладки
            if (resultString != null) {
                //вывод результата на экран
                Toast.makeText(MainActivity.this, resultString, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "no result", Toast.LENGTH_LONG).show();
            }
*/
            if (resultString.equals("200")) {//kod '200' - ok сообщение получено успешно
            //если полученное сообщение с новым номером: запомнить и обработать команду
                if(!upId.equals(upIdRemember)) {
                    Toast.makeText(MainActivity.this, upId +" СООБЩЕНИЕ:" +
                    updateTelegram + "\r\n" + "ПОЛУЧЕНО ОТ TELEGRAM", Toast.LENGTH_LONG).show();
                    upIdRemember = upId;
                    messageCom(updateTelegram);
                }
            }
        }
    }
       //=====================================================================================

}
