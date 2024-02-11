package com.vv.collegeattendence;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AttendenceList extends AppCompatActivity{
    AttendenceRecycleAdapter adapter;
    ArrayList<AttendenceListModel> arrayList;
    int edit1=0;
    SharedPreferences shareAttendance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_list);
        try {
            shareAttendance = getSharedPreferences("shareAttendance",MODE_PRIVATE);
            TextView subjectNameTV,semisterTV,sectionTV,dateTV;
            semisterTV=findViewById(R.id.year);
            subjectNameTV=findViewById(R.id.subjectNameRecycle);
            sectionTV=findViewById(R.id.section);
            dateTV=findViewById(R.id.date);
            String TABLE_NAME=getIntent().getStringExtra("tableName");
            String[] TableNameSplit = TABLE_NAME.split("_");
            String subject= TableNameSplit[0].replace("$"," ");
            String semister= TableNameSplit[1].replace("$"," ");
            String section= TableNameSplit[2].replace("$"," ");
            boolean flag=getIntent().getBooleanExtra("flag",false);
            String date = getIntent().getStringExtra("date");
            String startEndTime=getIntent().getStringExtra("startEndTime");
            subjectNameTV.setText(subject);
            semisterTV.setText(semister);
            sectionTV.setText(section);
            dateTV.setText(date);
            String finalDate="_"+date+startEndTime;
            RecyclerView recyclerView = findViewById(R.id.attendenceRecycle);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            DataBase database = new DataBase(this);
            arrayList= database.getAttendenceList(TABLE_NAME,finalDate);
            if(arrayList.size()>0){
                adapter  = new AttendenceRecycleAdapter(this,arrayList,date,"default",TABLE_NAME,finalDate,flag);
                recyclerView.setAdapter(adapter);
            }
            findViewById(R.id.exitbutton).setOnClickListener( v-> {
                backPress();
            });
            findViewById(R.id.submit).setOnClickListener(v ->{
                ArrayList<CheckBoxModel> checkBoxArrayList = adapter.CheckBoxArrayList;
                for(int i=0;i<checkBoxArrayList.size();i++){
                    database.inserDateValue(TABLE_NAME,finalDate,checkBoxArrayList.get(i).id,checkBoxArrayList.get(i).presentAbsent);
                }
                if(!flag){
                    if(shareAttendance.getBoolean("shareAtt",true)){
                        arrayList=database.getAttendenceList(TABLE_NAME,finalDate);
                        backPress();
                        shareAttendenc(subject,startEndTime);
                    }
                    else {
                        backPress();
                    }
                }
                else
                    backPress();


            });

            findViewById(R.id.dots).setOnClickListener(v ->{
                PopupMenu popupMenu = new PopupMenu(this,v);
                popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());
                if((!flag) || (edit1==1)){
                    MenuItem itemToRemove = popupMenu.getMenu().findItem(R.id.edit);
                    popupMenu.getMenu().removeItem(itemToRemove.getItemId());
                    MenuItem itemToRemove1 = popupMenu.getMenu().findItem(R.id.shareAttendence);
                    popupMenu.getMenu().removeItem(itemToRemove1.getItemId());
                }
                else {
                    MenuItem itemToRemove = popupMenu.getMenu().findItem(R.id.checkAll);
                    popupMenu.getMenu().removeItem(itemToRemove.getItemId());
                    MenuItem itemToRemove1 = popupMenu.getMenu().findItem(R.id.uncheckAll);
                    popupMenu.getMenu().removeItem(itemToRemove1.getItemId());
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                       if(id==R.id.deleteAttendence){
                            AlertDialog.Builder builder = new AlertDialog.Builder(AttendenceList.this,R.style.Dialogbox_border);
                            builder.setTitle("DO YOU WANT TO DELETE THIS ATTENDANCE ??");
                            builder.setIcon(R.drawable.delete);
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                   startActivity(new Intent(AttendenceList.this, MainActivity.class));
                                    finish();
                                    database.deleteColumn(TABLE_NAME,finalDate);
                                }
                            });
                            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog1, int which) {
                                    dialog1.dismiss();
                                }
                            });
                            builder.show();
                       }
                         if (id==R.id.checkAll) {
                             for (AttendenceListModel model : arrayList) {
                                 model.setValueToChange(1);

                             }
                             boolean flagCH=flag;
                             if(  edit1==1)
                                 flagCH=!flag;
                                adapter  = new AttendenceRecycleAdapter(AttendenceList.this,arrayList,date,"checkAll",TABLE_NAME,finalDate,flagCH);
                            recyclerView.setAdapter(adapter);

                        }
                        else if (id==R.id.uncheckAll) {
                             for (AttendenceListModel model : arrayList) {
                                 model.setValueToChange(0);
                             }
                             boolean flagCH=flag;
                             if(edit1 ==1)
                                 flagCH=!flag;
                                adapter  = new AttendenceRecycleAdapter(AttendenceList.this,arrayList,date,"unCheckAll",TABLE_NAME,finalDate,flagCH);
                            recyclerView.setAdapter(adapter);

                        } else if (id==R.id.edit) {
                             adapter  = new AttendenceRecycleAdapter(AttendenceList.this,arrayList,date,"default",TABLE_NAME,finalDate,!flag);
                             recyclerView.setAdapter(adapter);
                             MenuItem itemToRemove = popupMenu.getMenu().findItem(R.id.edit);
                             popupMenu.getMenu().removeItem(itemToRemove.getItemId());
                             MenuItem itemToRemove1 = popupMenu.getMenu().findItem(R.id.shareAttendence);
                             popupMenu.getMenu().removeItem(itemToRemove1.getItemId());
                             edit1=1;
                         } else if (id==R.id.shareAttendence) {
                           shareAttendenc(subject,startEndTime);
                         }
                        return true;
                    }
                });
                popupMenu.show();
            });
        }
        catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPress();
    }
    public void backPress(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    public void shareAttendenc(String subject,String startEndTime){
        String textToShare = subject+"\n"+startEndTime.replace("_"," ");
        if(!shareAttendance.getBoolean("present",true)  && !shareAttendance.getBoolean("absent",true)){
            Toast.makeText(this, "enable 'share student present' or \n 'share student absent'  in setting", Toast.LENGTH_LONG).show();
        }
        else {
            if(shareAttendance.getBoolean("present",true)){
                boolean once=true;
                for(int i=0;i<arrayList.size();i++){
                    AttendenceListModel attendenceListModel = arrayList.get(i);
                    if(attendenceListModel.attendence==1){
                        if(once){
                            textToShare+="\n present List";
                            once=false;
                        }
                        textToShare+="\n"+attendenceListModel.pinNo+" "+attendenceListModel.parentsNumber;
                    }
                }
            }
            if(shareAttendance.getBoolean("absent",true)){
                boolean once=true;
                for(int i=0;i<arrayList.size();i++){
                    AttendenceListModel attendenceListModel = arrayList.get(i);
                    if(attendenceListModel.attendence==0){
                        if(once){
                            textToShare+="\n absent List";
                            once=false;
                        }
                        textToShare+="\n"+attendenceListModel.pinNo+"\t"+attendenceListModel.parentsNumber;
                    }
                }
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
    }
}