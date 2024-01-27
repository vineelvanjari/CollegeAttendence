package com.vv.collegeattendence;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BottomSheetDialogFrg extends BottomSheetDialogFragment {
    Context context;
    String TABLE_NAME;
    BottomSheetDialogFrg(Context context,String TABLE_NAME){
        this.context=context;
        this.TABLE_NAME=TABLE_NAME;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRef = inflater.inflate(R.layout.bottom_sheet_dialog_box, container, false);
        viewRef.findViewById(R.id.addAttendence).setOnClickListener(v ->{
            dismiss();
            Dialog dialog = new Dialog(context,R.style.Dialogbox_border);
            dialog.setContentView(R.layout.date_dialog_box);
            EditText Date1 = dialog.findViewById(R.id.selectDate);
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy h:mm:ss a"); // Add "ss" for seconds
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
            dialog.findViewById(R.id.next).setOnClickListener(a ->{
                DataBase dataBase = new DataBase(context);
                if(!dataBase.checkColumnName(TABLE_NAME,"_"+Date1.getText().toString()))
                {
                    dataBase.inserDate(TABLE_NAME,"_"+Date1.getText().toString());
                }
                Intent intent = new Intent(context, AttendenceList.class);
                intent.putExtra("date",Date1.getText().toString());
                intent.putExtra("tableName",TABLE_NAME);
                ((Activity) context).startActivityForResult(intent,1);
                dialog.dismiss();
                ((Activity) context).finish();
            });
            dialog.show();
        });
        viewRef.findViewById(R.id.viewAttendence).setOnClickListener(v ->{

        });
        return viewRef;
    }
}