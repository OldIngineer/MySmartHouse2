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
import androidx.fragment.app.Fragment;

import ru.vozov.mysmarthouse2.R;

public class ClimateFragment extends Fragment {
    static private String[] pNameModule = new String[17];
    static private boolean[] pCheckBox = new boolean[17];
    static private String[] pTempIn = new String[17];
    static private String[] pTempOut = new String[17];
    static private String[] pTempCurrent = new String[17];
    static private String[] pHumidityCurrent = new String[17];
    static private String[] pHumidityGround = new String[17];
    // Вызывается при первом создании класса
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//вызов конструктора суперкласса
        pNameModule = mListener.inDataName1();
        pCheckBox = mListener.inDataCheck1();
        pTempIn = mListener.inTempIn();
        pTempOut = mListener.inTempOut();
        pTempCurrent = mListener.inTempCurrent();
        pHumidityCurrent = mListener.inHumidityCurrent();
        pHumidityGround = mListener.inHumidityGround();
    }
    //создание отображения фрагмента
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_climate, container, false);
        //получение ссылок на виджет
        TextView humGround = v.findViewById(R.id.textView41);
        Button button = v.findViewById(R.id.button);
        TextView name1 = v.findViewById(R.id.textView25);
        EditText tempIn1 = v.findViewById(R.id.editTextNumber20);
        EditText tempOut1 = v.findViewById(R.id.editTextNumber4);
        TextView name2 = v.findViewById(R.id.textView26);
        EditText tempIn2 = v.findViewById(R.id.editTextNumber21);
        EditText tempOut2 = v.findViewById(R.id.editTextNumber5);
        TextView name3 = v.findViewById(R.id.textView27);
        EditText tempIn3 = v.findViewById(R.id.editTextNumber22);
        EditText tempOut3 = v.findViewById(R.id.editTextNumber6);
        TextView name4 = v.findViewById(R.id.textView28);
        EditText tempIn4 = v.findViewById(R.id.editTextNumber23);
        EditText tempOut4 = v.findViewById(R.id.editTextNumber7);
        TextView name5 = v.findViewById(R.id.textView29);
        EditText tempIn5 = v.findViewById(R.id.editTextNumber24);
        EditText tempOut5 = v.findViewById(R.id.editTextNumber8);
        TextView name6 = v.findViewById(R.id.textView30);
        EditText tempIn6 = v.findViewById(R.id.editTextNumber25);
        EditText tempOut6 = v.findViewById(R.id.editTextNumber9);
        TextView name7 = v.findViewById(R.id.textView31);
        EditText tempIn7 = v.findViewById(R.id.editTextNumber26);
        EditText tempOut7 = v.findViewById(R.id.editTextNumber10);
        TextView name8 = v.findViewById(R.id.textView32);
        EditText tempIn8 = v.findViewById(R.id.editTextNumber27);
        EditText tempOut8 = v.findViewById(R.id.editTextNumber11);
        TextView name9 = v.findViewById(R.id.textView33);
        EditText tempIn9 = v.findViewById(R.id.editTextNumber28);
        EditText tempOut9 = v.findViewById(R.id.editTextNumber12);
        TextView name10 = v.findViewById(R.id.textView34);
        EditText tempIn10 = v.findViewById(R.id.editTextNumber29);
        EditText tempOut10 = v.findViewById(R.id.editTextNumber13);
        TextView name11 = v.findViewById(R.id.textView35);
        EditText tempIn11 = v.findViewById(R.id.editTextNumber30);
        EditText tempOut11 = v.findViewById(R.id.editTextNumber14);
        TextView name12 = v.findViewById(R.id.textView36);
        EditText tempIn12 = v.findViewById(R.id.editTextNumber31);
        EditText tempOut12 = v.findViewById(R.id.editTextNumber15);
        TextView name13 = v.findViewById(R.id.textView37);
        EditText tempIn13 = v.findViewById(R.id.editTextNumber32);
        EditText tempOut13 = v.findViewById(R.id.editTextNumber16);
        TextView name14 = v.findViewById(R.id.textView38);
        EditText tempIn14 = v.findViewById(R.id.editTextNumber33);
        EditText tempOut14 = v.findViewById(R.id.editTextNumber17);
        TextView name15 = v.findViewById(R.id.textView39);
        EditText tempIn15 = v.findViewById(R.id.editTextNumber34);
        EditText tempOut15 = v.findViewById(R.id.editTextNumber18);
        TextView name16 = v.findViewById(R.id.textView40);
        EditText tempIn16 = v.findViewById(R.id.editTextNumber35);
        EditText tempOut16 = v.findViewById(R.id.editTextNumber19);
        //заполнение данными запомненными в профиле
        tempIn1.setText(pTempIn[1]);
        tempOut1.setText(pTempOut[1]);
        tempIn2.setText(pTempIn[2]);
        tempOut2.setText(pTempOut[2]);
        tempIn3.setText(pTempIn[3]);
        tempOut3.setText(pTempOut[3]);
        tempIn4.setText(pTempIn[4]);
        tempOut4.setText(pTempOut[4]);
        tempIn5.setText(pTempIn[5]);
        tempOut5.setText(pTempOut[5]);
        tempIn6.setText(pTempIn[6]);
        tempOut6.setText(pTempOut[6]);
        tempIn7.setText(pTempIn[7]);
        tempOut7.setText(pTempOut[7]);
        tempIn8.setText(pTempIn[8]);
        tempOut8.setText(pTempOut[8]);
        tempIn9.setText(pTempIn[9]);
        tempOut9.setText(pTempOut[9]);
        tempIn10.setText(pTempIn[10]);
        tempOut10.setText(pTempOut[10]);
        tempIn11.setText(pTempIn[11]);
        tempOut11.setText(pTempOut[11]);
        tempIn12.setText(pTempIn[12]);
        tempOut12.setText(pTempOut[12]);
        tempIn13.setText(pTempIn[13]);
        tempOut13.setText(pTempOut[13]);
        tempIn14.setText(pTempIn[14]);
        tempOut14.setText(pTempOut[14]);
        tempIn15.setText(pTempIn[15]);
        tempOut15.setText(pTempOut[15]);
        tempIn16.setText(pTempIn[16]);
        tempOut16.setText(pTempOut[16]);
        //получение значения влажности земли если хотябы один датчик подключен и модуль активен
        pHumidityGround[0] = "н/д";
        for(int i = 1; i < 17; i++) {
           if((!pHumidityGround[i].equals("н/д"))&pCheckBox[i]) {
               pHumidityGround[0] = pHumidityGround[i];
           }
        }
        if(!pHumidityGround[0].equals("н/д")) {
            if(pHumidityGround[0].equals("1")) {
                humGround.setText("влажность земли: <10ед.");
            } else {humGround.setText("влажность земли: "+pHumidityGround[0]+"ед.");}
        }
        if (pCheckBox[1]) {
        name1.setText(pNameModule[1]+":T="+pTempCurrent[1]+"*C;"+"H="+pHumidityCurrent[1]+"%");
            name1.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn1.setVisibility(View.VISIBLE);
            tempOut1.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[2]) {
        name2.setText(pNameModule[2]+":T="+pTempCurrent[2]+"*C;"+"H="+pHumidityCurrent[2]+"%");
            name2.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn2.setVisibility(View.VISIBLE);
            tempOut2.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[3]) {
        name3.setText(pNameModule[3]+":T="+pTempCurrent[3]+"*C;"+"H="+pHumidityCurrent[3]+"%");
            name3.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn3.setVisibility(View.VISIBLE);
            tempOut3.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[4]) {
            name4.setText(pNameModule[4]+":T="+pTempCurrent[4]+"*C;"+"H="+pHumidityCurrent[4]+"%");
            name4.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn4.setVisibility(View.VISIBLE);
            tempOut4.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[5]) {
            name5.setText(pNameModule[5]+":T="+pTempCurrent[5]+"*C;"+"H="+pHumidityCurrent[5]+"%");
            name5.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn5.setVisibility(View.VISIBLE);
            tempOut5.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[6]) {
            name6.setText(pNameModule[6]+":T="+pTempCurrent[6]+"*C;"+"H="+pHumidityCurrent[6]+"%");
            name6.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn6.setVisibility(View.VISIBLE);
            tempOut6.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[7]) {
            name7.setText(pNameModule[7]+":T="+pTempCurrent[7]+"*C;"+"H="+pHumidityCurrent[7]+"%");
            name7.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn7.setVisibility(View.VISIBLE);
            tempOut7.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[8]) {
            name8.setText(pNameModule[8]+":T="+pTempCurrent[8]+"*C;"+"H="+pHumidityCurrent[8]+"%");
            name8.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn8.setVisibility(View.VISIBLE);
            tempOut8.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[9]) {
            name9.setText(pNameModule[9]+":T="+pTempCurrent[9]+"*C;"+"H="+pHumidityCurrent[9]+"%");
            name9.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn9.setVisibility(View.VISIBLE);
            tempOut9.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[10]) {
            name10.setText(pNameModule[10]+":T="+pTempCurrent[10]+"*C;"+"H="+pHumidityCurrent[10]+"%");
            name10.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn10.setVisibility(View.VISIBLE);
            tempOut10.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[11]) {
            name11.setText(pNameModule[11]+":T="+pTempCurrent[11]+"*C;"+"H="+pHumidityCurrent[11]+"%");
            name11.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn11.setVisibility(View.VISIBLE);
            tempOut11.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[12]) {
            name12.setText(pNameModule[12]+":T="+pTempCurrent[12]+"*C;"+"H="+pHumidityCurrent[12]+"%");
            name12.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn12.setVisibility(View.VISIBLE);
            tempOut12.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[13]) {
            name13.setText(pNameModule[13]+":T="+pTempCurrent[13]+"*C;"+"H="+pHumidityCurrent[13]+"%");
            name13.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn13.setVisibility(View.VISIBLE);
            tempOut13.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[14]) {
            name14.setText(pNameModule[14]+":T="+pTempCurrent[14]+"*C;"+"H="+pHumidityCurrent[14]+"%");
            name14.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn14.setVisibility(View.VISIBLE);
            tempOut14.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[15]) {
            name15.setText(pNameModule[15]+":T="+pTempCurrent[15]+"*C;"+"H="+pHumidityCurrent[15]+"%");
            name15.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn15.setVisibility(View.VISIBLE);
            tempOut15.setVisibility(View.VISIBLE);
        }
        if (pCheckBox[16]) {
            name16.setText(pNameModule[16]+":T="+pTempCurrent[16]+"*C;"+"H="+pHumidityCurrent[16]+"%");
            name16.setBackgroundColor(getResources().getColor(R.color.colorStatusText));
            tempIn16.setVisibility(View.VISIBLE);
            tempOut16.setVisibility(View.VISIBLE);
        }
        // Следим за нажатием кнопки
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Когда кнопка нажата, вызывается действие владельца
                pTempIn[1] = tempIn1.getText().toString();
                pTempOut[1] = tempOut1.getText().toString();
                pTempIn[2] = tempIn2.getText().toString();
                pTempOut[2] = tempOut2.getText().toString();
                pTempIn[3] = tempIn3.getText().toString();
                pTempOut[3] = tempOut3.getText().toString();
                pTempIn[4] = tempIn4.getText().toString();
                pTempOut[4] = tempOut4.getText().toString();
                pTempIn[5] = tempIn5.getText().toString();
                pTempOut[5] = tempOut5.getText().toString();
                pTempIn[6] = tempIn6.getText().toString();
                pTempOut[6] = tempOut6.getText().toString();
                pTempIn[7] = tempIn7.getText().toString();
                pTempOut[7] = tempOut7.getText().toString();
                pTempIn[8] = tempIn8.getText().toString();
                pTempOut[8] = tempOut8.getText().toString();
                pTempIn[9] = tempIn9.getText().toString();
                pTempOut[9] = tempOut9.getText().toString();
                pTempIn[10] = tempIn10.getText().toString();
                pTempOut[10] = tempOut10.getText().toString();
                pTempIn[11] = tempIn11.getText().toString();
                pTempOut[11] = tempOut11.getText().toString();
                pTempIn[12] = tempIn12.getText().toString();
                pTempOut[12] = tempOut12.getText().toString();
                pTempIn[13] = tempIn13.getText().toString();
                pTempOut[13] = tempOut13.getText().toString();
                pTempIn[14] = tempIn14.getText().toString();
                pTempOut[14] = tempOut14.getText().toString();
                pTempIn[15] = tempIn15.getText().toString();
                pTempOut[15] = tempOut15.getText().toString();
                pTempIn[16] = tempIn16.getText().toString();
                pTempOut[16] = tempOut16.getText().toString();
                mListener.outDataTemp(pTempIn, pTempOut);
                mListener.replaceClimateFragment();
            }
        });
        return v;
    }
    //====реализовать этот интерфейс, чтобы обрабатывать в активности события===
    public interface CallBackActivity {
        //обновление фрагмента
        void replaceClimateFragment();
        //передача в активность данных
        void outDataTemp(String[] outTempIn, String[] outTempOut);
        //получение из активности данных
        String[] inDataName1();
        boolean[] inDataCheck1();
        String[] inTempIn();
        String[] inTempOut();
        String[] inTempCurrent();
        String[] inHumidityCurrent();
        String[] inHumidityGround();
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
            mListener = (ClimateFragment.CallBackActivity) activity;
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
}
