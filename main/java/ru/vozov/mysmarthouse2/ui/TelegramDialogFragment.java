package ru.vozov.mysmarthouse2.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import ru.vozov.mysmarthouse2.MainActivity;
import ru.vozov.mysmarthouse2.R;

//класс окна-диалога для ввода идентификационных данных канала Telegram
public class TelegramDialogFragment extends DialogFragment {
    static private String mToken;
    static private String mId;
    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mToken = mListener.inToken();
        mId = mListener.inId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_identification, container,false);
        //получение ссылок на виджет
        //объявление экземпляра ввода кода токена
        EditText nameToken = v.findViewById(R.id.telegram_token);
        nameToken.setText(mToken);
        //обьявление экземпляра ввода кода канала
        EditText nameId = v.findViewById(R.id.telegram_id);
        nameId.setText(mId);
        Button button_positive = v.findViewById(R.id.button_positive);
        Button button_negative = v.findViewById(R.id.button_negative);

        // Следим за нажатием кнопки
        button_positive.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Когда кнопка нажата, вызывается действие владельца
                String sToken = nameToken.getText().toString();
                if(!sToken.equals("")){//исключение ошибки нулевой строки
                    mToken = sToken;
                }
                String sId = nameId.getText().toString();
                if(!sId.equals("")){//исключение ошибки нулевой строки
                    mId = sId;
                }
                mListener.outToken(mToken);
                mListener.outId(mId);
                mListener.replaceMainFragment2();
            }
        });
        button_negative.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Когда кнопка нажата, вызывается действие владельца
                mListener.replaceMainFragment2();
            }
        });
        return v;
    }
    //====реализовать этот интерфейс, чтобы обрабатывать в активности события===
    public interface CallBackActivity {
        //чтение Bot_Token
        String inToken();
        //чтение chat_id
        String inId();
        //запись идентификационных данных в активности
        void replaceMainFragment2();
        //запись Bot_Token
        void outToken(String mToken);
        //запись chat_id
        void outId(String mId);
    }
    // объявить экземпляр интерфейса
    TelegramDialogFragment.CallBackActivity mListener;
    // Переопределить метод Fragment.onAttach() для создания экземпляра интерфейса
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        //Убедитесь, что хост активности реализует интерфейс обратного вызова
        try {
            //Создать экземпляр интерфейса, чтобы мы могли отправить события хосту
            mListener = (TelegramDialogFragment.CallBackActivity) activity;
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
