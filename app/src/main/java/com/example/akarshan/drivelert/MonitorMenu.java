package com.example.akarshan.drivelert;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


public class MonitorMenu extends Fragment {

    private DiscreteSeekBar discreteSeekBar;
    public double time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_monitor_menu, container, false);
        discreteSeekBar = (DiscreteSeekBar) view.findViewById(R.id.discreteSeekBar_time);

        discreteSeekBar.setMin(0);
        discreteSeekBar.setMax(10);
        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (value == 0) {
                    discreteSeekBar.setIndicatorFormatter("0.5");
                    time = 0.5;
                }
                if (value == 1) {
                    discreteSeekBar.setIndicatorFormatter("1");
                    time = 1;
                }
                if (value == 2) {
                    discreteSeekBar.setIndicatorFormatter("1.5");
                    time = 1.5;
                }
                if (value == 3) {
                    discreteSeekBar.setIndicatorFormatter("2");
                    time = 2;
                }
                if (value == 4) {
                    discreteSeekBar.setIndicatorFormatter("2.5");
                    time = 2.5;
                }
                if (value == 5) {
                    discreteSeekBar.setIndicatorFormatter("3");
                    time = 3;
                }
                if (value == 6) {
                    discreteSeekBar.setIndicatorFormatter("3.5");
                    time = 3.5;
                }
                if (value == 7) {
                    discreteSeekBar.setIndicatorFormatter("4");
                    time = 4;
                }
                if (value == 8) {
                    discreteSeekBar.setIndicatorFormatter("4.5");
                    time = 4.5;
                }
                if (value == 9) {
                    discreteSeekBar.setIndicatorFormatter("5");
                    time = 5;
                }
                if (value == 10) {
                    discreteSeekBar.setIndicatorFormatter("5.5");
                    time = 5.5;
                }

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        return view;

    }
}
