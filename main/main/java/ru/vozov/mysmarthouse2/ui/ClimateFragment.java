package ru.vozov.mysmarthouse2.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    static private byte[] pControlBoiler = new byte[3];
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
        pControlBoiler = mListener.inControlBoiler();
    }
    //создание отображения фрагмента
    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_climate, container, false);
        //получение ссылок на виджет
        TextView humGround = v.findViewById(R.id.textView41);
        Button buttonUpdate = v.findViewById(R.id.button_update);
        Button buttonClimate = v.findViewById(R.id.button_climate);
        if (pControlBoiler[2]==0){
            buttonClimate.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            buttonClimate.setText("ОТКЛЮЧЕН");
        }else{
            buttonClimate.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            buttonClimate.setText("ВКЛЮЧЕН");
        }
        EditText tempIn1 = v.findViewById(R.id.editTextNumber20);
        EditText tempOut1 = v.findViewById(R.id.editTextNumber4);
        EditText tempIn2 = v.findViewById(R.id.editTextNumber21);
        EditText tempOut2 = v.findViewById(R.id.editTextNumber5);
        EditText tempIn3 = v.findViewById(R.id.editTextNumber22);
        EditText tempOut3 = v.findViewById(R.id.editTextNumber6);
        EditText tempIn4 = v.findViewById(R.id.editTextNumber23);
        EditText tempOut4 = v.findViewById(R.id.editTextNumber7);
        EditText tempIn5 = v.findViewById(R.id.editTextNumber24);
        EditText tempOut5 = v.findViewById(R.id.editTextNumber8);
        EditText tempIn6 = v.findViewById(R.id.editTextNumber25);
        EditText tempOut6 = v.findViewById(R.id.editTextNumber9);
        EditText tempIn7 = v.findViewById(R.id.editTextNumber26);
        EditText tempOut7 = v.findViewById(R.id.editTextNumber10);
        EditText tempIn8 = v.findViewById(R.id.editTextNumber27);
        EditText tempOut8 = v.findViewById(R.id.editTextNumber11);
        EditText tempIn9 = v.findViewById(R.id.editTextNumber28);
        EditText tempOut9 = v.findViewById(R.id.editTextNumber12);
        EditText tempIn10 = v.findViewById(R.id.editTextNumber29);
        EditText tempOut10 = v.findViewById(R.id.editTextNumber13);
        EditText tempIn11 = v.findViewById(R.id.editTextNumber30);
        EditText tempOut11 = v.findViewById(R.id.editTextNumber14);
        EditText tempIn12 = v.findViewById(R.id.editTextNumber31);
        EditText tempOut12 = v.findViewById(R.id.editTextNumber15);
        EditText tempIn13 = v.findViewById(R.id.editTextNumber32);
        EditText tempOut13 = v.findViewById(R.id.editTextNumber16);
        EditText tempIn14 = v.findViewById(R.id.editTextNumber33);
        EditText tempOut14 = v.findViewById(R.id.editTextNumber17);
        EditText tempIn15 = v.findViewById(R.id.editTextNumber34);
        EditText tempOut15 = v.findViewById(R.id.editTextNumber18);
        EditText tempIn16 = v.findViewById(R.id.editTextNumber35);
        EditText tempOut16 = v.findViewById(R.id.editTextNumber19);
        RadioGroup boiler_management = v.findViewById(R.id.boiler_menedger);
        RadioButton boiler_management1 = v.findViewById(R.id.boiler_menedger1);
        RadioButton boiler_management2 = v.findViewById(R.id.boiler_menedger2);
        RadioButton boiler_management3 = v.findViewById(R.id.boiler_menedger3);
        RadioButton boiler_management4 = v.findViewById(R.id.boiler_menedger4);
        RadioButton boiler_management5 = v.findViewById(R.id.boiler_menedger5);
        RadioButton boiler_management6 = v.findViewById(R.id.boiler_menedger6);
        RadioButton boiler_management7 = v.findViewById(R.id.boiler_menedger7);
        RadioButton boiler_management8 = v.findViewById(R.id.boiler_menedger8);
        RadioButton boiler_management9 = v.findViewById(R.id.boiler_menedger9);
        RadioButton boiler_management10 = v.findViewById(R.id.boiler_menedger10);
        RadioButton boiler_management11 = v.findViewById(R.id.boiler_menedger11);
        RadioButton boiler_management12 = v.findViewById(R.id.boiler_menedger12);
        RadioButton boiler_management13 = v.findViewById(R.id.boiler_menedger13);
        RadioButton boiler_management14 = v.findViewById(R.id.boiler_menedger14);
        RadioButton boiler_management15 = v.findViewById(R.id.boiler_menedger15);
        RadioButton boiler_management16 = v.findViewById(R.id.boiler_menedger16);
        RadioGroup boiler_control = v.findViewById(R.id.boiler_contr);
        RadioButton controlBoiler1 = v.findViewById(R.id.controlBoiler1);
        RadioButton controlBoiler2 = v.findViewById(R.id.controlBoiler2);
        RadioButton controlBoiler3 = v.findViewById(R.id.controlBoiler3);
        RadioButton controlBoiler4 = v.findViewById(R.id.controlBoiler4);
        RadioButton controlBoiler5 = v.findViewById(R.id.controlBoiler5);
        RadioButton controlBoiler6 = v.findViewById(R.id.controlBoiler6);
        RadioButton controlBoiler7 = v.findViewById(R.id.controlBoiler7);
        RadioButton controlBoiler8 = v.findViewById(R.id.controlBoiler8);
        RadioButton controlBoiler9 = v.findViewById(R.id.controlBoiler9);
        RadioButton controlBoiler10 = v.findViewById(R.id.controlBoiler10);
        RadioButton controlBoiler11 = v.findViewById(R.id.controlBoiler11);
        RadioButton controlBoiler12 = v.findViewById(R.id.controlBoiler12);
        RadioButton controlBoiler13 = v.findViewById(R.id.controlBoiler13);
        RadioButton controlBoiler14 = v.findViewById(R.id.controlBoiler14);
        RadioButton controlBoiler15 = v.findViewById(R.id.controlBoiler15);
        RadioButton controlBoiler16 = v.findViewById(R.id.controlBoiler16);
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
            boiler_management1.setVisibility(View.VISIBLE);
            boiler_management1.setText(pNameModule[1]+":T="+pTempCurrent[1]+"*C;"+"H="+pHumidityCurrent[1]+"%");
            controlBoiler1.setVisibility(View.VISIBLE);
            tempIn1.setVisibility(View.VISIBLE);
            tempOut1.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==1) {
                controlBoiler1.setChecked(true);
                if(pControlBoiler[1]!=1) {
                    tempIn1.setVisibility(View.INVISIBLE);
                    tempOut1.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==1) boiler_management1.setChecked(true);
        }
        if (pCheckBox[2]) {
            boiler_management2.setVisibility(View.VISIBLE);
            boiler_management2.setText(pNameModule[2]+":T="+pTempCurrent[2]+"*C;"+"H="+pHumidityCurrent[2]+"%");
            controlBoiler2.setVisibility(View.VISIBLE);
            tempIn2.setVisibility(View.VISIBLE);
            tempOut2.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==2) {
                controlBoiler2.setChecked(true);
                if(pControlBoiler[1]!=2) {
                    tempIn2.setVisibility(View.INVISIBLE);
                    tempOut2.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==2) boiler_management2.setChecked(true);
        }
        if (pCheckBox[3]) {
            boiler_management3.setVisibility(View.VISIBLE);
            boiler_management3.setText(pNameModule[3]+":T="+pTempCurrent[3]+"*C;"+"H="+pHumidityCurrent[3]+"%");
            controlBoiler3.setVisibility(View.VISIBLE);
            tempIn3.setVisibility(View.VISIBLE);
            tempOut3.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==3) {
                controlBoiler3.setChecked(true);
                if(pControlBoiler[1]!=3) {
                    tempIn3.setVisibility(View.INVISIBLE);
                    tempOut3.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==3) boiler_management3.setChecked(true);
        }
        if (pCheckBox[4]) {
            boiler_management4.setVisibility(View.VISIBLE);
            boiler_management4.setText(pNameModule[4]+":T="+pTempCurrent[4]+"*C;"+"H="+pHumidityCurrent[4]+"%");
            controlBoiler4.setVisibility(View.VISIBLE);
            tempIn4.setVisibility(View.VISIBLE);
            tempOut4.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==4) {
                controlBoiler4.setChecked(true);
                if(pControlBoiler[1]!=4) {
                    tempIn4.setVisibility(View.INVISIBLE);
                    tempOut4.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==4) boiler_management4.setChecked(true);
        }
        if (pCheckBox[5]) {
            boiler_management5.setVisibility(View.VISIBLE);
            boiler_management5.setText(pNameModule[5]+":T="+pTempCurrent[5]+"*C;"+"H="+pHumidityCurrent[5]+"%");
            controlBoiler5.setVisibility(View.VISIBLE);
            tempIn5.setVisibility(View.VISIBLE);
            tempOut5.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==5) {
                controlBoiler5.setChecked(true);
                if(pControlBoiler[1]!=5) {
                    tempIn5.setVisibility(View.INVISIBLE);
                    tempOut5.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==5) boiler_management5.setChecked(true);
        }
        if (pCheckBox[6]) {
            boiler_management6.setVisibility(View.VISIBLE);
            boiler_management6.setText(pNameModule[6]+":T="+pTempCurrent[6]+"*C;"+"H="+pHumidityCurrent[6]+"%");
            controlBoiler6.setVisibility(View.VISIBLE);
            tempIn6.setVisibility(View.VISIBLE);
            tempOut6.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==6) {
                controlBoiler6.setChecked(true);
                if(pControlBoiler[1]!=6) {
                    tempIn6.setVisibility(View.INVISIBLE);
                    tempOut6.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==6) boiler_management6.setChecked(true);
        }
        if (pCheckBox[7]) {
            boiler_management7.setVisibility(View.VISIBLE);
            boiler_management7.setText(pNameModule[7]+":T="+pTempCurrent[7]+"*C;"+"H="+pHumidityCurrent[7]+"%");
            controlBoiler7.setVisibility(View.VISIBLE);
            tempIn7.setVisibility(View.VISIBLE);
            tempOut7.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==7) {
                controlBoiler7.setChecked(true);
                if(pControlBoiler[1]!=7) {
                    tempIn7.setVisibility(View.INVISIBLE);
                    tempOut7.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==7) boiler_management7.setChecked(true);
        }
        if (pCheckBox[8]) {
            boiler_management8.setVisibility(View.VISIBLE);
            boiler_management8.setText(pNameModule[8]+":T="+pTempCurrent[8]+"*C;"+"H="+pHumidityCurrent[8]+"%");
            controlBoiler8.setVisibility(View.VISIBLE);
            tempIn8.setVisibility(View.VISIBLE);
            tempOut8.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==8) {
                controlBoiler8.setChecked(true);
                if(pControlBoiler[1]!=8) {
                    tempIn8.setVisibility(View.INVISIBLE);
                    tempOut8.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==8) boiler_management8.setChecked(true);
        }
        if (pCheckBox[9]) {
            boiler_management9.setVisibility(View.VISIBLE);
            boiler_management9.setText(pNameModule[9]+":T="+pTempCurrent[9]+"*C;"+"H="+pHumidityCurrent[9]+"%");
            controlBoiler9.setVisibility(View.VISIBLE);
            tempIn9.setVisibility(View.VISIBLE);
            tempOut9.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==9) {
                controlBoiler9.setChecked(true);
                if(pControlBoiler[1]!=9) {
                    tempIn9.setVisibility(View.INVISIBLE);
                    tempOut9.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==9) boiler_management9.setChecked(true);
        }
        if (pCheckBox[10]) {
            boiler_management10.setVisibility(View.VISIBLE);
            boiler_management10.setText(pNameModule[10]+":T="+pTempCurrent[10]+"*C;"+"H="+pHumidityCurrent[10]+"%");
            controlBoiler10.setVisibility(View.VISIBLE);
            tempIn10.setVisibility(View.VISIBLE);
            tempOut10.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==10) {
                controlBoiler10.setChecked(true);
                if(pControlBoiler[1]!=10) {
                    tempIn10.setVisibility(View.INVISIBLE);
                    tempOut10.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==10) boiler_management10.setChecked(true);
        }
        if (pCheckBox[11]) {
            boiler_management11.setVisibility(View.VISIBLE);
            boiler_management11.setText(pNameModule[11]+":T="+pTempCurrent[11]+"*C;"+"H="+pHumidityCurrent[11]+"%");
            controlBoiler11.setVisibility(View.VISIBLE);
            tempIn11.setVisibility(View.VISIBLE);
            tempOut11.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==11) {
                controlBoiler11.setChecked(true);
                if(pControlBoiler[1]!=11) {
                    tempIn11.setVisibility(View.INVISIBLE);
                    tempOut11.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==11) boiler_management11.setChecked(true);
        }
        if (pCheckBox[12]) {
            boiler_management12.setVisibility(View.VISIBLE);
            boiler_management12.setText(pNameModule[12]+":T="+pTempCurrent[12]+"*C;"+"H="+pHumidityCurrent[12]+"%");
            controlBoiler12.setVisibility(View.VISIBLE);
            tempIn12.setVisibility(View.VISIBLE);
            tempOut12.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==12) {
                controlBoiler12.setChecked(true);
                if(pControlBoiler[1]!=12) {
                    tempIn12.setVisibility(View.INVISIBLE);
                    tempOut12.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==12) boiler_management12.setChecked(true);
        }
        if (pCheckBox[13]) {
            boiler_management13.setVisibility(View.VISIBLE);
            boiler_management13.setText(pNameModule[13]+":T="+pTempCurrent[13]+"*C;"+"H="+pHumidityCurrent[13]+"%");
            controlBoiler13.setVisibility(View.VISIBLE);
            tempIn13.setVisibility(View.VISIBLE);
            tempOut13.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==13) {
                controlBoiler13.setChecked(true);
                if(pControlBoiler[1]!=13) {
                    tempIn13.setVisibility(View.INVISIBLE);
                    tempOut13.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==13) boiler_management13.setChecked(true);
        }
        if (pCheckBox[14]) {
            boiler_management14.setVisibility(View.VISIBLE);
            boiler_management14.setText(pNameModule[14]+":T="+pTempCurrent[14]+"*C;"+"H="+pHumidityCurrent[14]+"%");
            controlBoiler14.setVisibility(View.VISIBLE);
            tempIn14.setVisibility(View.VISIBLE);
            tempOut14.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==14) {
                controlBoiler14.setChecked(true);
                if(pControlBoiler[1]!=14) {
                    tempIn14.setVisibility(View.INVISIBLE);
                    tempOut14.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==14) boiler_management14.setChecked(true);
        }
        if (pCheckBox[15]) {
            boiler_management15.setVisibility(View.VISIBLE);
            boiler_management15.setText(pNameModule[15]+":T="+pTempCurrent[15]+"*C;"+"H="+pHumidityCurrent[15]+"%");
            controlBoiler15.setVisibility(View.VISIBLE);
            tempIn15.setVisibility(View.VISIBLE);
            tempOut15.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==15) {
                controlBoiler15.setChecked(true);
                if(pControlBoiler[1]!=15) {
                    tempIn15.setVisibility(View.INVISIBLE);
                    tempOut15.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==15) boiler_management15.setChecked(true);
        }
        if (pCheckBox[16]) {
            boiler_management16.setVisibility(View.VISIBLE);
            boiler_management16.setText(pNameModule[16]+":T="+pTempCurrent[16]+"*C;"+"H="+pHumidityCurrent[16]+"%");
            controlBoiler16.setVisibility(View.VISIBLE);
            tempIn16.setVisibility(View.VISIBLE);
            tempOut16.setVisibility(View.VISIBLE);
            if(pControlBoiler[0]==16) {
                controlBoiler16.setChecked(true);
                if(pControlBoiler[1]!=16) {
                    tempIn16.setVisibility(View.INVISIBLE);
                    tempOut16.setVisibility(View.INVISIBLE);
                }
            }
            if(pControlBoiler[1]==16) boiler_management16.setChecked(true);
        }
        //регистрация обратного вызова,
        // который будет вызываться при изменении отмеченного переключателя в этой группе.
        boiler_control.setOnCheckedChangeListener((group, checkedId) -> {
           switch(checkedId) {
               case R.id.controlBoiler1: pControlBoiler[0]=1;
                   break;
               case R.id.controlBoiler2: pControlBoiler[0]=2;
                   break;
               case R.id.controlBoiler3: pControlBoiler[0]=3;
                   break;
               case R.id.controlBoiler4: pControlBoiler[0]=4;
                   break;
               case R.id.controlBoiler5: pControlBoiler[0]=5;
                   break;
               case R.id.controlBoiler6: pControlBoiler[0]=6;
                   break;
               case R.id.controlBoiler7: pControlBoiler[0]=7;
                   break;
               case R.id.controlBoiler8: pControlBoiler[0]=8;
                   break;
               case R.id.controlBoiler9: pControlBoiler[0]=9;
                   break;
               case R.id.controlBoiler10: pControlBoiler[0]=10;
                   break;
               case R.id.controlBoiler11: pControlBoiler[0]=11;
                   break;
               case R.id.controlBoiler12: pControlBoiler[0]=12;
                   break;
               case R.id.controlBoiler13: pControlBoiler[0]=13;
                   break;
               case R.id.controlBoiler14: pControlBoiler[0]=14;
                   break;
               case R.id.controlBoiler15: pControlBoiler[0]=15;
                   break;
               case R.id.controlBoiler16: pControlBoiler[0]=16;
                   break;
           }
            mListener.outDataTemp(pTempIn, pTempOut, pControlBoiler);
        });
        //регистрация обратного вызова,
        // который будет вызываться при изменении отмеченного переключателя в этой группе.
        boiler_management.setOnCheckedChangeListener((group, checkedId) -> {
            switch(checkedId) {
                case R.id.boiler_menedger1: pControlBoiler[1]=1;
                    break;
                case R.id.boiler_menedger2: pControlBoiler[1]=2;
                    break;
                case R.id.boiler_menedger3: pControlBoiler[1]=3;
                    break;
                case R.id.boiler_menedger4: pControlBoiler[1]=4;
                    break;
                case R.id.boiler_menedger5: pControlBoiler[1]=5;
                    break;
                case R.id.boiler_menedger6: pControlBoiler[1]=6;
                    break;
                case R.id.boiler_menedger7: pControlBoiler[1]=7;
                    break;
                case R.id.boiler_menedger8: pControlBoiler[1]=8;
                    break;
                case R.id.boiler_menedger9: pControlBoiler[1]=9;
                    break;
                case R.id.boiler_menedger10: pControlBoiler[1]=10;
                    break;
                case R.id.boiler_menedger11: pControlBoiler[1]=11;
                    break;
                case R.id.boiler_menedger12: pControlBoiler[1]=12;
                    break;
                case R.id.boiler_menedger13: pControlBoiler[1]=13;
                    break;
                case R.id.boiler_menedger14: pControlBoiler[1]=14;
                    break;
                case R.id.boiler_menedger15: pControlBoiler[1]=15;
                    break;
                case R.id.boiler_menedger16: pControlBoiler[1]=16;
                    break;
            }
            mListener.outDataTemp(pTempIn, pTempOut, pControlBoiler);
        });
             // Следим за нажатием кнопки
        buttonUpdate.setOnClickListener(v1 -> {
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
            //проверка ввода температуры и если нет числа записать "no"
            for (int i = 1; i < 17; i++)
            {
                if(pTempIn[i].equals("")) pTempIn[i] = "no";
                if(pTempOut[i].equals("")) pTempOut[i] = "no";
            }
            mListener.outDataTemp(pTempIn, pTempOut, pControlBoiler);
            mListener.replaceClimateFragment();
        });
        // Следим за нажатием кнопки
        buttonClimate.setOnClickListener(v12 -> {
            // Когда кнопка нажата, вызывается действие владельца
            if (pControlBoiler[2]==0){
                pControlBoiler[2] = 1;
                //смена цвета кнопки И НАИМЕНОВАНИЯ
                buttonClimate.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                buttonClimate.setText("ВКЛЮЧЕН");
            } else {
                pControlBoiler[2] = 0;
                //смена цвета кнопки и наименования
                buttonClimate.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                buttonClimate.setText("ОТКЛЮЧЕН");
            }
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
            //проверка ввода температуры и если нет числа записать "no"
            for (int i = 1; i < 17; i++)
            {
                if(pTempIn[i].equals("")) pTempIn[i] = "no";
                if(pTempOut[i].equals("")) pTempOut[i] = "no";
            }
            mListener.outDataTemp(pTempIn, pTempOut, pControlBoiler);
            mListener.replaceClimateFragment();
        });
        return v;
    }
    //====реализовать этот интерфейс, чтобы обрабатывать в активности события===
    public interface CallBackActivity {
        //обновление фрагмента
        void replaceClimateFragment();
        //передача в активность данных
        void outDataTemp(String[] outTempIn, String[] outTempOut,
                         byte[] pControlBoiler);
        //получение из активности данных
        String[] inDataName1();
        boolean[] inDataCheck1();
        String[] inTempIn();
        String[] inTempOut();
        String[] inTempCurrent();
        String[] inHumidityCurrent();
        String[] inHumidityGround();
        byte[] inControlBoiler();
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
    /*
    //переопределить метод Fragment.onPause() для записи изменений когда фрагмент
    //становится не видимомым
    @Override
    public void onPause() {
        super.onPause();
        //проверка ввода температуры и если нет числа записать "no"
        for (int i = 1; i < 17; i++)
        {
            if(pTempIn[i].equals("")) pTempIn[i] = "no";
            if(pTempOut[i].equals("")) pTempOut[i] = "no";
        }
        mListener.outDataTemp(pTempIn, pTempOut, pControlBoiler);
    }
    */
}
