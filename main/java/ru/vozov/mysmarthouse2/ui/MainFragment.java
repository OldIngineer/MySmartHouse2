package ru.vozov.mysmarthouse2.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ru.vozov.mysmarthouse2.MainActivity;
import ru.vozov.mysmarthouse2.R;

public class MainFragment extends Fragment {
    private boolean[] mScan = new boolean[4];
    // Вызывается при первом создании класса
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//вызов конструктора суперкласса
    }
    //создание отображения фрагмента
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mScan = mListener.inAttributeScanNet();//возврат значения признака сканирования из активности
        //кнопка сканирования
        final Button but1 = v.findViewById(R.id.button_scan);
        if (!mScan[0]){
            but1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            but1.setText("ВКЛЮЧИТЬ СКАНИРОВАНИЕ УСТРОЙСТВ ДОМАШНЕЙ СЕТИ");
        }else{
            but1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            but1.setText("ВЫКЛЮЧИТЬ СКАНИРОВАНИЕ УСТРОЙСТВ ДОМАШНЕЙ СЕТИ");
        }
        //получение ссылок на виджет и заполнение данными запомненными в активности
        CheckBox checkBoxSMS = v.findViewById(R.id.checkBox17);
        checkBoxSMS.setChecked(mScan[1]);
        CheckBox checkBoxTelegram = v.findViewById(R.id.checkBox18);
        checkBoxTelegram.setChecked(mScan[2]);
        //если не введены идентификационные данные канала Telegram - не отображается
        if((MainActivity.token.equals("bot_token"))||(MainActivity.id.equals("chat_id"))) {
            checkBoxTelegram.setVisibility(View.INVISIBLE);
        } else checkBoxTelegram.setVisibility(View.VISIBLE);
        CheckBox checkBoxComSMS = v.findViewById(R.id.checkBox19);
        checkBoxComSMS.setChecked(mScan[3]);
        //создание класса-слушателя на событие нажатия кнопки
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (!mScan[0]){
                mScan[0] = true;
                //смена цвета кнопки И НАИМЕНОВАНИЯ
                but1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                but1.setText("ВЫКЛЮЧИТЬ СКАНИРОВАНИЕ УСТРОЙСТВ ДОМАШНЕЙ СЕТИ");
                //запись изменения в признаки сканирования
                mListener.outAttributeScanNet(mScan);
                //инициализация сканирования
                mListener.initScanNetwork();
            } else {
                mScan[0] = false;
                //смена цвета кнопки и наименования
                but1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                but1.setText("ВКЛЮЧИТЬ СКАНИРОВАНИЕ УСТРОЙСТВ ДОМАШНЕЙ СЕТИ");
                //запись изменения в признаки сканирования
                mListener.outAttributeScanNet(mScan);
                //прекращение сканирования
                mListener.stopScanNetwork();
            }
          }
        });
        //создание класса-слушателя на событие нажатие checkBox - выбор сообщений SMS
        checkBoxSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mScan[1] = checkBoxSMS.isChecked();
                //запись изменения в признаки сканирования
                mListener.outAttributeScanNet(mScan);
            }
        });
        //создание класса-слушателя на событие нажатие checkBox - выбор сообщений Telegram
        checkBoxTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScan[2] = checkBoxTelegram.isChecked();
                //запись изменения в признаки сканирования
                mListener.outAttributeScanNet(mScan);
            }
        });
        //создание класса-слушателя на событие нажатие checkBox - выбор управления через SMS
        checkBoxComSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScan[3] = checkBoxComSMS.isChecked();
                //запись изменения в признаки сканирования
                mListener.outAttributeScanNet(mScan);
            }
        });
        return v;
    }
    //====реализовать этот интерфейс, чтобы обрабатывать в активности события===
    public interface CallBackActivity {
        //инициализация сканирования сети
        void initScanNetwork();
        //остановить сканирование устройств
        void stopScanNetwork();
        //получение состояния сканирования
        boolean[] inAttributeScanNet();
        //передача в активность состояния сканирования
        void outAttributeScanNet(boolean[] data);
    }
    // объявить экземпляр интерфейса
    CallBackActivity mListener;
    // Переопределить метод Fragment.onAttach() для создания экземпляра интерфейса
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        //Убедитесь, что хост активности реализует интерфейс обратного вызова
        try {
            //Создать экземпляр интерфейса, чтобы мы могли отправить события хосту
            mListener = (CallBackActivity) activity;
        } catch (ClassCastException e) {
            //активность не реализует интерфейс, исключение
            throw new ClassCastException(activity.toString()
                    + " must implement CallBackActivity");
        }
    }
    // переопределить метод Fragment.onDetach() для удаления экземпляра интерфейса
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    //========================================================================
}
