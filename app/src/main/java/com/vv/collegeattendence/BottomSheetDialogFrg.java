package com.vv.collegeattendence;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BottomSheetDialogFrg extends BottomSheetDialogFragment {
    Context context;
    String TABLE_NAME,startTime,endTime,amPm;
    LinearLayout linearLayout,NOOFPeriodsLayout;
    int selectedHour,selectedMinute, endMinutes;;
    SharedPreferences shareAttendance;
    EditText NOOFPeriodsET;
    SimpleDateFormat dateFormat;
    private NumberPicker hourPickerStart, minutePickerStart,amPmToggleStart,hourPickerEnd, minutePickerEnd,amPmToggleEnd;
    BottomSheetDialogFrg(Context context,String TABLE_NAME){
        this.context=context;
        this.TABLE_NAME=TABLE_NAME;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRef = inflater.inflate(R.layout.bottom_sheet_dialog_box, container, false);
        viewRef.findViewById(R.id.addAttendence).setOnClickListener(v ->{

            Dialog(viewRef,true);
            dismiss();
        });
        viewRef.findViewById(R.id.viewAttendence).setOnClickListener(v ->{
            Dialog(viewRef,false);
            dismiss();
        });
        return viewRef;
    }
    public void Dialog(View viewRef,boolean flag){
    try{
        DataBase dataBase = new DataBase(context);
        Dialog dialog = new Dialog(context,R.style.Dialogbox_border);
        dialog.setContentView(R.layout.date_dialog_box);
        Switch endTimeSwitch = (Switch) dialog.findViewById(R.id.endTimeSwitch);
        ColorStateList trackColorStateList = new ColorStateList(
                new int[][] {{android.R.attr.state_checked}, {}},
                new int[] { Color.GREEN, Color.RED});
        endTimeSwitch.setTrackTintList(trackColorStateList);
        shareAttendance = context.getSharedPreferences("shareAttendance",MODE_PRIVATE);
        NOOFPeriodsLayout=dialog.findViewById(R.id.NOOFPeriodsLayout);
        linearLayout = dialog.findViewById(R.id.end_time_linear_layout);
        if(shareAttendance.getBoolean("endTime",true)){
            endTimeSwitch.setChecked(true);
            endTimeMethod(false);
        }
        else {
            endTimeMethod(true);
            endTimeSwitch.setVisibility(View.GONE);
        }
        endTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // The switch is checked
                    Toast.makeText(context, "auto End Time  ENABLE", Toast.LENGTH_SHORT).show();
                    endTimeSwitch.setChecked(true);
                    endTimeMethod(false);
                } else {
                    // The switch is unchecked
                    Toast.makeText(context, "auto End Time DISABLE", Toast.LENGTH_SHORT).show();
                    endTimeSwitch.setChecked(false);
                    endTimeMethod(true);
                }
            }
        });
        ImageView delete;
        delete=dialog.findViewById(R.id.delete);
        dialog.findViewById(R.id.cancel).setOnClickListener(v ->{
            dialog.dismiss();
        });
        delete.setVisibility(View.GONE);
        EditText Date1 = dialog.findViewById(R.id.selectDate);
        Button next=dialog.findViewById(R.id.next);
        NOOFPeriodsET=dialog.findViewById(R.id.NOOFPeriods);
        Date currentDate = new Date();
        dateFormat = new SimpleDateFormat("dd_MM_yyyy h:mm:ss a"); // Add "ss" for seconds
        String formattedDateTime = dateFormat.format(currentDate);
        String date1 = formattedDateTime.substring(0, 10);
        Date1.setText(date1);
        Date1.setOnClickListener(va ->{
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    (view, year1, month1, dayOfMonth) -> {
                        String date = String.format("%02d_%02d_%04d", dayOfMonth, month1 + 1, year1);
                        Date1.setText(date);
                    },
                    year, month, day);

            datePickerDialog.show();
        });
        if(flag) {

            hourPickerStart = dialog.findViewById(R.id.hourPickerStart);
            minutePickerStart = dialog.findViewById(R.id.minutePickerStart);
            amPmToggleStart = dialog.findViewById(R.id.amPmToggleStart);
            hourPickerStart.setMinValue(1);
            hourPickerStart.setMaxValue(12);
            hourPickerStart.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.format("%02d", value);
                }
            });
            minutePickerStart.setMinValue(0);
            minutePickerStart.setMaxValue(59);
            minutePickerStart.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.format("%02d", value);
                }
            });
            amPmToggleStart.setMinValue(0);
            amPmToggleStart.setMaxValue(1);
            String[] ampm = {"AM", "PM"};
            amPmToggleStart.setDisplayedValues(ampm);

            hourPickerEnd = dialog.findViewById(R.id.hourPickerEnd);
            minutePickerEnd = dialog.findViewById(R.id.minutePickerEnd);
            amPmToggleEnd = dialog.findViewById(R.id.amPmToggleEnd);
            hourPickerEnd.setMinValue(1);
            hourPickerEnd.setMaxValue(12);
            hourPickerEnd.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.format("%02d",value);
                }
            });
            minutePickerEnd.setMaxValue(0);
            minutePickerEnd.setMaxValue(59);
            minutePickerEnd.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.format("%02d",value);
                }
            });
            amPmToggleEnd.setMinValue(0);
            amPmToggleEnd.setMaxValue(1);
            amPmToggleEnd.setDisplayedValues(ampm);

            next.setOnClickListener(a ->{
                 selectedHour = hourPickerStart.getValue();
                 selectedMinute = minutePickerStart.getValue();

                amPm=String.valueOf(amPmToggleStart.getValue()).equals("0")?"AM":"PM";
                startTime = String.format("%02d_%02d_%s", selectedHour, selectedMinute, amPm);
                
               if(endTimeSwitch.isChecked()){
                    int noOfPeriods=1;
                    if(!NOOFPeriodsET.getText().toString().trim().isEmpty()){
                        noOfPeriods=Integer.parseInt(NOOFPeriodsET.getText().toString());
                    }
                    int amPm1=amPmToggleStart.getValue();
                    selectedHour=hourPickerStart.getValue();
                    selectedMinute=minutePickerStart.getValue();
                    int hourFromSharedPreference = shareAttendance.getInt("hour", 0);
                    int minuteFromSharedPreference = shareAttendance.getInt("minute", 0);
                    int startMinutes = selectedHour * 60 + selectedMinute;
                    // Add total period time to start time in minutes
                    int totalPeriodMinutes = noOfPeriods*(hourFromSharedPreference * 60 + minuteFromSharedPreference);
                    endMinutes = startMinutes + totalPeriodMinutes;
                    // Handle changes in AM/PM
                    int endAmPm = amPm1;
                    while (endMinutes >= 720) {  // 720 minutes in 12 hours
                        endMinutes -= 720;
                        endAmPm = (endAmPm + 1) % 2;  // Toggle AM/PM
                    }
                    // Convert back to hour, minute
                    int endHour = endMinutes / 60;
                    int endMinute = endMinutes % 60;
                    hourPickerEnd.setValue(endHour);
                    minutePickerEnd.setValue(endMinute);
                    amPmToggleEnd.setValue(endAmPm);

               }
                   selectedHour = hourPickerEnd.getValue();
                   selectedMinute = minutePickerEnd.getValue();
                   amPm=String.valueOf(amPmToggleEnd.getValue()).equals("0")? "AM":"PM";
                   endTime = String.format("%02d_%02d_%s", selectedHour, selectedMinute, amPm);
                String startEndTime="_"+startTime+"_"+endTime;
               if(amPmToggleStart.getValue()==1 && amPmToggleEnd.getValue()==0) {
                   Calendar calendar = Calendar.getInstance();
                   calendar.setTime(currentDate);
                   calendar.add(Calendar.DAY_OF_MONTH, 1);
                   Date tomorrowDate = calendar.getTime();
                   String formattedTomorrowDateTime = dateFormat.format(tomorrowDate);
                   String tomorrowDateStr = formattedTomorrowDateTime.substring(0, 10);
                   startEndTime="_"+startTime+"_"+tomorrowDateStr+"_"+endTime;
               }

                String dateColumn="_"+Date1.getText().toString()+startEndTime;
                if(!dataBase.checkColumnName(TABLE_NAME,dateColumn))
                {
                    dataBase.inserDate(TABLE_NAME,dateColumn);
                    Intent intent = new Intent(context, AttendenceList.class);
                    intent.putExtra("date",Date1.getText().toString());
                    intent.putExtra("startEndTime",startEndTime);
                    intent.putExtra("tableName",TABLE_NAME);
                    intent.putExtra("flag",false);
                    ((Activity) context).startActivityForResult(intent,1);
                    dialog.dismiss();
                    ((Activity) context).finish();
                }
                else
                    Toast.makeText(context, "Already class attendance is present on this date and time", Toast.LENGTH_SHORT).show();
            });
        }
        else {
            LinearLayout commonDialogBox;
            commonDialogBox=dialog.findViewById(R.id.commonDialogBox);
           commonDialogBox.setVisibility(View.GONE);
            next.setOnClickListener(v ->{
                ArrayList<AttendencePerDayModel> columnData=dataBase.checkColumnList(TABLE_NAME,"_"+Date1.getText().toString());
                Dialog dialog1 = new Dialog(context,R.style.Dialogbox_border);
                dialog1.setContentView(R.layout.attendence_per_day);

                ListView listView = dialog1.findViewById(R.id.attendence_per_day);

                if(columnData.size()>0){
                    ArrayList<String> columnNamesAL = new ArrayList<>();
                    for(int i=0;i<columnData.size();i++){
                        columnNamesAL.add(columnData.get(i).timeToDisplay);
                    }
                    TextView dateTV=dialog1.findViewById(R.id.date);
                    dateTV.setText(Date1.getText().toString());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.attendence_per_day_list_design,columnNamesAL);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String time=columnData.get(position).timeForDB;
                            Intent intent = new Intent(context, AttendenceList.class);
                            intent.putExtra("date",Date1.getText().toString());
                            intent.putExtra("startEndTime",time);
                            intent.putExtra("tableName",TABLE_NAME);
                            intent.putExtra("flag",true);
                            ((Activity) context).startActivity(intent);
                            dialog.dismiss();
                            ((Activity) context).finish();
                        }
                    });
                    dialog1.show();
                }
                else
                    Toast.makeText(context, "No attendence on this date", Toast.LENGTH_SHORT).show();
            });

        }
        dialog.show();
    }
    catch (Exception ex){
        Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
    }

    }

    private void endTimeMethod(boolean EndTimeflag) {
        if(EndTimeflag){
            linearLayout.setVisibility(View.VISIBLE);
            NOOFPeriodsLayout.setVisibility(View.GONE);
        }
        else {
            linearLayout.setVisibility(View.GONE);
            NOOFPeriodsLayout.setVisibility(View.VISIBLE);
        }
    }
}
