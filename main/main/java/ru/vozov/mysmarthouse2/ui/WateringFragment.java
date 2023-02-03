package ru.vozov.mysmarthouse2.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ru.vozov.mysmarthouse2.R;

public class WateringFragment extends Fragment {
    // Вызывается при первом создании класса
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//вызов конструктора суперкласса
    }
    //создание отображения фрагмента
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_watering, container, false);

        return v;
    }
}
