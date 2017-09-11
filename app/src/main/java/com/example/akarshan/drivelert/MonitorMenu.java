package com.example.akarshan.drivelert;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


public class MonitorMenu extends Fragment {

    private DiscreteSeekBar discreteSeekBar;
    public boolean isHandsFreeMode;
    public double time;
    public int soundNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_monitor_menu, container, false);
        discreteSeekBar = (DiscreteSeekBar) view.findViewById(R.id.discreteSeekBar_time);
        Switch handsFreeModeSwitch = (Switch) view.findViewById(R.id.hands_free_mode_switch);
        Button buttonStart = (Button) view.findViewById(R.id.button_start);
        Spinner spinnerSoundSelect = (Spinner) view.findViewById(R.id.spinner_select_sound);
        isHandsFreeMode = false;
        soundNumber = 0;
        time = 0.5;
        final String[] sounds = {"Sound 1", "Sound 2", " Sound 3"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.custom_spinner, sounds);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerSoundSelect.setAdapter(arrayAdapter);
        discreteSeekBar.setIndicatorFormatter("0.5");

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

        handsFreeModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isHandsFreeMode =isChecked;
            }
        });

        spinnerSoundSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soundNumber = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), FaceTrackerActivity.class);
                intent.putExtra("time", time);
                intent.putExtra("handsfreemode", isHandsFreeMode);
                intent.putExtra("sound", soundNumber);
                startActivity(intent);
            }
        });

        return view;

    }
}
