package ru.vozov.mysmarthouse2.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import ru.vozov.mysmarthouse2.R;
    //класс окна-диалога для ввода телефонов SMS оповещения и выбора тел. управления
public class TelDialogFragment extends DialogFragment {
        static private String[] pNumberTel = new String[7];
        static private boolean[] pCheckActiveTel = new boolean[7];
        @Override
        public void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            pNumberTel = mListener.inTelNumber();
            pCheckActiveTel = mListener.inTelCheck();
        }
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.dialog_devices, container,false);
            TextView text = v.findViewById(R.id.textView);
            text.setText("Выбор телефона управления");
            //получение ссылок на виджет и заполнение данными запомненными в профиле
            EditText name1 = v.findViewById(R.id.editTextName1);
            name1.setText(pNumberTel[1]);
            CheckBox checkBox1 = v.findViewById(R.id.checkBox1);
            checkBox1.setChecked(pCheckActiveTel[1]);
            EditText name2 = v.findViewById(R.id.editTextName2);
            name2.setText(pNumberTel[2]);
            CheckBox checkBox2 = v.findViewById(R.id.checkBox2);
            checkBox2.setChecked(pCheckActiveTel[2]);
            EditText name3 = v.findViewById(R.id.editTextName3);
            name3.setText(pNumberTel[3]);
            CheckBox checkBox3 = v.findViewById(R.id.checkBox3);
            checkBox3.setChecked(pCheckActiveTel[3]);
            EditText name4 = v.findViewById(R.id.editTextName4);
            name4.setText(pNumberTel[4]);
            CheckBox checkBox4 = v.findViewById(R.id.checkBox4);
            checkBox4.setChecked(pCheckActiveTel[4]);
            EditText name5 = v.findViewById(R.id.editTextName5);
            name5.setText(pNumberTel[5]);
            CheckBox checkBox5 = v.findViewById(R.id.checkBox5);
            checkBox5.setChecked(pCheckActiveTel[5]);
            EditText name6 = v.findViewById(R.id.editTextName6);
            name6.setText(pNumberTel[6]);
            CheckBox checkBox6 = v.findViewById(R.id.checkBox6);
            checkBox6.setChecked(pCheckActiveTel[6]);
            Button button_positive = v.findViewById(R.id.button_positive);
            Button button_negative = v.findViewById(R.id.button_negative);
            // Следим за нажатием кнопки
            button_positive.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    // Когда кнопка нажата, вызывается действие владельца
                    pNumberTel[1] = name1.getText().toString();
                    pCheckActiveTel[1] = checkBox1.isChecked();
                    pNumberTel[2] = name2.getText().toString();
                    pCheckActiveTel[2] = checkBox2.isChecked();
                    pNumberTel[3] = name3.getText().toString();
                    pCheckActiveTel[3] = checkBox3.isChecked();
                    pNumberTel[4] = name4.getText().toString();
                    pCheckActiveTel[4] = checkBox4.isChecked();
                    pNumberTel[5] = name5.getText().toString();
                    pCheckActiveTel[5] = checkBox5.isChecked();
                    pNumberTel[6] = name6.getText().toString();
                    pCheckActiveTel[6] = checkBox6.isChecked();
                    mListener.outDataTel(pNumberTel, pCheckActiveTel);
                    mListener.replaceMainFragment1();
                }
            });
            button_negative.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    // Когда кнопка нажата, вызывается действие владельца
                    mListener.replaceMainFragment1();
                }
            });
            return v;
        }
        //====реализовать этот интерфейс, чтобы обрабатывать в активности события===
        public interface CallBackActivity {
            //удаление диалога, обновление главного фрагмента
            void replaceMainFragment1();
            //получение запомненных значений
            public String[] inTelNumber();
            public boolean[] inTelCheck();
            //запись значений
            public void outDataTel(String[] name, boolean[] check);
        }
        // объявить экземпляр интерфейса
        ru.vozov.mysmarthouse2.ui.TelDialogFragment.CallBackActivity mListener;
        // Переопределить метод Fragment.onAttach() для создания экземпляра интерфейса
        @Override
        public void onAttach(@NonNull Activity activity) {
            super.onAttach(activity);
            //Убедитесь, что хост активности реализует интерфейс обратного вызова
            try {
                //Создать экземпляр интерфейса, чтобы мы могли отправить события хосту
                mListener = (ru.vozov.mysmarthouse2.ui.TelDialogFragment.CallBackActivity) activity;
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
