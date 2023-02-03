package ru.vozov.mysmarthouse2.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import ru.vozov.mysmarthouse2.R;

//класс окна-диалога для изменения команд посылаемых через SMS
public class SMScomDialogFragment extends DialogFragment {
    private String mNameCom[] = new String[16];//наименование команд с запасом
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNameCom = mListener.inNameCom();
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_com_sms, container, false);
        //получение ссылок на виджет
        EditText editComStatus = v.findViewById(R.id.editText_com_statusLAN);
        TextView comClimate = v.findViewById(R.id.textView29);
        Button button_positive = v.findViewById(R.id.button_edit);
        Button button_negative = v.findViewById(R.id.button_cansel);
        //запись текста
        editComStatus.setText(mNameCom[0]);
        comClimate.setText(mNameCom[1]);
        // Следим за нажатием кнопки
        button_positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Когда кнопка нажата, вызывается действие владельца
                String name = editComStatus.getText().toString();
                if (!name.equals("")) {//исключение ошибки нулевой строки
                    mNameCom[0] = name;
                }
                mListener.outNameCom(mNameCom);
                mListener.replaceMainFragment3();
            }
        });
        button_negative.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Когда кнопка нажата, вызывается действие владельца
                mListener.replaceMainFragment3();
            }
        });
        return v;
    }
    //====реализовать этот интерфейс, чтобы обрабатывать в активности события===
    public interface CallBackActivity {
        //объявлен метод получения наименований команд
        String[] inNameCom();
        //объявлен метод записи в активность измененных данных
        void outNameCom(String[] name);
        //перезагрузка главного фрагмента
        void replaceMainFragment3();
    }
    // объявить экземпляр интерфейса
    SMScomDialogFragment.CallBackActivity mListener;
    // Переопределить метод Fragment.onAttach() для создания экземпляра интерфейса
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        //Убедитесь, что хост активности реализует интерфейс обратного вызова
        try {
            //Создать экземпляр интерфейса, чтобы мы могли отправить события хосту
            mListener = (SMScomDialogFragment.CallBackActivity) activity;
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
