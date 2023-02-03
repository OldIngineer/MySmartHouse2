package ru.vozov.mysmarthouse2.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ru.vozov.mysmarthouse2.R;

public class SecurityFragment extends Fragment {
    private String[] mAttrSecurity = new String[4];
    // Вызывается при первом создании класса
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//вызов конструктора суперкласса
    }
    //создание отображения фрагмента
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_security, container, false);
        //возврат значения признака охраны и текстов из активности
        mAttrSecurity = mListener.inAttrSecurity();
        //картинка
        final ImageView imageView = v.findViewById(R.id.imageView2);
        //кнопка охрана
        final Button but1 = v.findViewById(R.id.button_security);
        if (mAttrSecurity[0].equals("false")){
            but1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            but1.setText("ОТКЛЮЧЕНА");
        }else{
            but1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            but1.setText("ВКЛЮЧЕНА");
            imageView.setVisibility(View.VISIBLE);
        }
        //получение ссылок на виджет и заполнение данными запомненными в профиле
        EditText messageSMS = v.findViewById(R.id.editTextMessage);
        messageSMS.setText(mAttrSecurity[1]);
        EditText comSMSout = v.findViewById(R.id.editTextTeamOut);
        comSMSout.setText(mAttrSecurity[2]);
        EditText comSMSin = v.findViewById(R.id.editTextTeamIn);
        comSMSin.setText(mAttrSecurity[3]);
        //создание класса-слушателя на событие нажатия кнопки
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAttrSecurity[0].equals("false")){
                    mAttrSecurity[0] = "true";
                    //смена цвета кнопки И НАИМЕНОВАНИЯ
                    but1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    but1.setText("ВКЛЮЧЕНА");
                    //запись изменения в признак охраны и тексты
                    mAttrSecurity[1] = messageSMS.getText().toString();
                    mAttrSecurity[2] = comSMSout.getText().toString();
                    mAttrSecurity[3] = comSMSin.getText().toString();
                    mListener.outAttrSecurity(mAttrSecurity);
                    //отображение картинки
                    imageView.setVisibility(View.VISIBLE);
                    } else {
                    mAttrSecurity[0] = "false";
                    //смена цвета кнопки и наименования
                    but1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    but1.setText("ОТКЛЮЧЕНА");
                    //запись изменения в признак охраны и тексты
                    mListener.outAttrSecurity(mAttrSecurity);
                    //не отображать картинку
                    imageView.setVisibility(View.INVISIBLE);
                   }
            }
        });
    return v;
    }
    //====реализовать этот интерфейс, чтобы обрабатывать в активности события===
    public interface CallBackActivity {
        //передача в активность данных
        void outAttrSecurity(String[] data);
        //получение из активности данных
        String[] inAttrSecurity();
    }
    // объявить экземпляр интерфейса
    SecurityFragment.CallBackActivity mListener;
    // Переопределить метод Fragment.onAttach() для создания экземпляра интерфейса
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        //Убедитесь, что хост активности реализует интерфейс обратного вызова
        try {
            //Создать экземпляр интерфейса, чтобы мы могли отправить события хосту
            mListener = (SecurityFragment.CallBackActivity) activity;
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
