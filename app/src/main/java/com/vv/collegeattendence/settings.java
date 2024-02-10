package com.vv.collegeattendence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

public class settings extends AppCompatActivity {
    NumberPicker hourPicker, minutePicker;
    SharedPreferences shareAttendance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ColorStateList trackColorStateList = new ColorStateList(
                new int[][] {{android.R.attr.state_checked}, {}},
                new int[] { Color.GREEN, Color.RED});
        Switch shareAttendenceSwitch = (Switch) findViewById(R.id.switch1);
        Switch presentSwitch = (Switch) findViewById(R.id.present);
        Switch absentSwitch = (Switch) findViewById(R.id.absent);
        Switch endTimeSwitch = (Switch) findViewById(R.id.endTimeSwitch);
        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);

        shareAttendenceSwitch.setTrackTintList(trackColorStateList);
        presentSwitch.setTrackTintList(trackColorStateList);
        absentSwitch.setTrackTintList(trackColorStateList);
        endTimeSwitch.setTrackTintList(trackColorStateList);
        shareAttendance = getSharedPreferences("shareAttendance",MODE_PRIVATE);
        if(shareAttendance.getBoolean("shareAtt",true))
            shareAttendenceSwitch.setChecked(true);
        if(shareAttendance.getBoolean("present",true))
            presentSwitch.setChecked(true);
        if(shareAttendance.getBoolean("absent",true))
            absentSwitch.setChecked(true);
        if(shareAttendance.getBoolean("endTime",true)){
            endTimeSwitch.setChecked(true);
            endTimeMethod(true);
        }
        else {
            endTimeMethod(false);
        }


        shareAttendenceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = shareAttendance.edit();
                if (isChecked) {
                    // The switch is checked
                    editor.putBoolean("shareAtt",true);
                    Toast.makeText(settings.this, "share Attendance ENABLE", Toast.LENGTH_SHORT).show();
                    shareAttendenceSwitch.setChecked(true);
                } else {
                    // The switch is unchecked
                    editor.putBoolean("shareAtt",false);
                    Toast.makeText(settings.this, "share Attendance DISABLE", Toast.LENGTH_SHORT).show();
                    shareAttendenceSwitch.setChecked(false);
                }
                editor.apply();
            }
        });
        presentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = shareAttendance.edit();
                if (isChecked) {
                    // The switch is checked
                    editor.putBoolean("present",true);
                    Toast.makeText(settings.this, "present ENABLE", Toast.LENGTH_SHORT).show();
                    presentSwitch.setChecked(true);
                } else {
                    // The switch is unchecked
                    editor.putBoolean("present",false);
                    Toast.makeText(settings.this, "present DISABLE", Toast.LENGTH_SHORT).show();
                    presentSwitch.setChecked(false);
                }
                editor.apply();
            }
        });
        absentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = shareAttendance.edit();
                if (isChecked) {
                    // The switch is checked
                    editor.putBoolean("absent",true);
                    Toast.makeText(settings.this, "absent ENABLE", Toast.LENGTH_SHORT).show();
                    absentSwitch.setChecked(true);
                } else {
                    // The switch is unchecked
                    editor.putBoolean("absent",false);
                    Toast.makeText(settings.this, "absent DISABLE", Toast.LENGTH_SHORT).show();
                    absentSwitch.setChecked(false);
                }
                editor.apply();
            }
        });
        endTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = shareAttendance.edit();
                if (isChecked) {
                    // The switch is checked
                    editor.putBoolean("endTime",true);
                    Toast.makeText(settings.this, "auto End Time  ENABLE", Toast.LENGTH_SHORT).show();
                    endTimeSwitch.setChecked(true);
                    endTimeMethod(true);
                } else {
                    // The switch is unchecked
                    editor.putBoolean("endTime",false);
                    Toast.makeText(settings.this, "auto End Time DISABLE", Toast.LENGTH_SHORT).show();
                    endTimeSwitch.setChecked(false);
                    endTimeMethod(false);
                }
                editor.apply();
            }
        });
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                SharedPreferences.Editor editor = shareAttendance.edit();
                editor.putInt("hour",newVal);
                editor.apply();
                Log.d("picker",""+newVal);
            }
        });
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                SharedPreferences.Editor editor = shareAttendance.edit();
                editor.putInt("minute",newVal);
                editor.apply();
                Log.d("picker",""+newVal);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    public void endTimeMethod(boolean flag){
        LinearLayout linearLayout = findViewById(R.id.end_time_linear_layout);
        if(flag){
            linearLayout.setVisibility(View.VISIBLE);
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(23);
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(59);
            hourPicker.setValue(shareAttendance.getInt("hour",0));
            minutePicker.setValue(shareAttendance.getInt("minute",0));
            hourPicker.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.format("%02d", value);
                }
            });

            minutePicker.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.format("%02d", value);
                }
            });
        }
        else {
            linearLayout.setVisibility(View.GONE);
        }
    }

}