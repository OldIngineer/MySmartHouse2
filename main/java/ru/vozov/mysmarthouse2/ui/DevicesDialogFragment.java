package ru.vozov.mysmarthouse2.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import ru.vozov.mysmarthouse2.MainActivity;
import ru.vozov.mysmarthouse2.R;

//класс окна-диалога для нумерации устройств подключенных к RS485
public class DevicesDialogFragment extends DialogFragment {
    static private String[] pNameModule = new String[17];
    static private boolean[] pCheckBox = new boolean[17];
    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        pNameModule = mListener.inDataName();
        pCheckBox = mListener.inDataCheck();
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_devices, container,false);
        TextView text = v.findViewById(R.id.textView);
        text.setText("Выбор");
        //получение ссылок на виджет и заполнение данными запомненными в профиле
        EditText name1 = v.findViewById(R.id.editTextName1);
        name1.setText(pNameModule[1]);
        CheckBox checkBox1 = v.findViewById(R.id.checkBox1);
        checkBox1.setChecked(pCheckBox[1]);
        EditText name2 = v.findViewById(R.id.editTextName2);
        name2.setText(pNameModule[2]);
        CheckBox checkBox2 = v.findViewById(R.id.checkBox2);
        checkBox2.setChecked(pCheckBox[2]);
        EditText name3 = v.findViewById(R.id.editTextName3);
        name3.setText(pNameModule[3]);
        CheckBox checkBox3 = v.findViewById(R.id.checkBox3);
        checkBox3.setChecked(pCheckBox[3]);
        EditText name4 = v.findViewById(R.id.editTextName4);
        name4.setText(pNameModule[4]);
        CheckBox checkBox4 = v.findViewById(R.id.checkBox4);
        checkBox4.setChecked(pCheckBox[4]);
        EditText name5 = v.findViewById(R.id.editTextName5);
        name5.setText(pNameModule[5]);
        CheckBox checkBox5 = v.findViewById(R.id.checkBox5);
        checkBox5.setChecked(pCheckBox[5]);
        EditText name6 = v.findViewById(R.id.editTextName6);
        name6.setText(pNameModule[6]);
        CheckBox checkBox6 = v.findViewById(R.id.checkBox6);
        checkBox6.setChecked(pCheckBox[6]);
        EditText name7 = v.findViewById(R.id.editTextName7);
        name7.setText(pNameModule[7]);
        CheckBox checkBox7 = v.findViewById(R.id.checkBox7);
        checkBox7.setChecked(pCheckBox[7]);
        EditText name8 = v.findViewById(R.id.editTextName8);
        name8.setText(pNameModule[8]);
        CheckBox checkBox8 = v.findViewById(R.id.checkBox8);
        checkBox8.setChecked(pCheckBox[8]);
        EditText name9 = v.findViewById(R.id.editTextName9);
        name9.setText(pNameModule[9]);
        CheckBox checkBox9 = v.findViewById(R.id.checkBox9);
        checkBox9.setChecked(pCheckBox[9]);
        EditText name10 = v.findViewById(R.id.editTextName10);
        name10.setText(pNameModule[10]);
        CheckBox checkBox10 = v.findViewById(R.id.checkBox10);
        checkBox10.setChecked(pCheckBox[10]);
        EditText name11 = v.findViewById(R.id.editTextName11);
        name11.setText(pNameModule[11]);
        CheckBox checkBox11 = v.findViewById(R.id.checkBox11);
        checkBox11.setChecked(pCheckBox[11]);
        EditText name12 = v.findViewById(R.id.editTextName12);
        name12.setText(pNameModule[12]);
        CheckBox checkBox12 = v.findViewById(R.id.checkBox12);
        checkBox12.setChecked(pCheckBox[12]);
        EditText name13 = v.findViewById(R.id.editTextName13);
        name13.setText(pNameModule[13]);
        CheckBox checkBox13 = v.findViewById(R.id.checkBox13);
        checkBox13.setChecked(pCheckBox[13]);
        EditText name14 = v.findViewById(R.id.editTextName14);
        name14.setText(pNameModule[14]);
        CheckBox checkBox14 = v.findViewById(R.id.checkBox14);
        checkBox14.setChecked(pCheckBox[14]);
        EditText name15 = v.findViewById(R.id.editTextName15);
        name15.setText(pNameModule[15]);
        CheckBox checkBox15 = v.findViewById(R.id.checkBox15);
        checkBox15.setChecked(pCheckBox[15]);
        EditText name16 = v.findViewById(R.id.editTextName16);
        name16.setText(pNameModule[16]);
        CheckBox checkBox16 = v.findViewById(R.id.checkBox16);
        checkBox16.setChecked(pCheckBox[16]);
        Button button_positive = v.findViewById(R.id.button_positive);
        Button button_negative = v.findViewById(R.id.button_negative);
          // Следим за нажатием кнопки
        button_positive.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Когда кнопка нажата, вызывается действие владельца
                pNameModule[1] = name1.getText().toString();
                pCheckBox[1] = checkBox1.isChecked();
                pNameModule[2] = name2.getText().toString();
                pCheckBox[2] = checkBox2.isChecked();
                pNameModule[3] = name3.getText().toString();
                pCheckBox[3] = checkBox3.isChecked();
                pNameModule[4] = name4.getText().toString();
                pCheckBox[4] = checkBox4.isChecked();
                pNameModule[5] = name5.getText().toString();
                pCheckBox[5] = checkBox5.isChecked();
                pNameModule[6] = name6.getText().toString();
                pCheckBox[6] = checkBox6.isChecked();
                pNameModule[7] = name7.getText().toString();
                pCheckBox[7] = checkBox7.isChecked();
                pNameModule[8] = name8.getText().toString();
                pCheckBox[8] = checkBox8.isChecked();
                pNameModule[9] = name9.getText().toString();
                pCheckBox[9] = checkBox9.isChecked();
                pNameModule[10] = name10.getText().toString();
                pCheckBox[10] = checkBox10.isChecked();
                pNameModule[11] = name11.getText().toString();
                pCheckBox[11] = checkBox11.isChecked();
                pNameModule[12] = name12.getText().toString();
                pCheckBox[12] = checkBox12.isChecked();
                pNameModule[13] = name13.getText().toString();
                pCheckBox[13] = checkBox13.isChecked();
                pNameModule[14] = name14.getText().toString();
                pCheckBox[14] = checkBox14.isChecked();
                pNameModule[15] = name15.getText().toString();
                pCheckBox[15] = checkBox15.isChecked();
                pNameModule[16] = name16.getText().toString();
                pCheckBox[16] = checkBox16.isChecked();
                mListener.outData(pNameModule, pCheckBox);
                mListener.replaceMainFragment();
            }
        });
        button_negative.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Когда кнопка нажата, вызывается действие владельца
                mListener.replaceMainFragment();
            }
        });
        return v;
    }
    //====реализовать этот интерфейс, чтобы обрабатывать в активности события===
    public interface CallBackActivity {
        //удаление диалога, обновление главного фрагмента
        void replaceMainFragment();
        //получение запомненных значений
        public String[] inDataName();
        public boolean[] inDataCheck();
        //запись значений
        public void outData(String[] name, boolean[] check);
    }
    // объявить экземпляр интерфейса
    DevicesDialogFragment.CallBackActivity mListener;
    // Переопределить метод Fragment.onAttach() для создания экземпляра интерфейса
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        //Убедитесь, что хост активности реализует интерфейс обратного вызова
        try {
            //Создать экземпляр интерфейса, чтобы мы могли отправить события хосту
            mListener = (DevicesDialogFragment.CallBackActivity) activity;
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
