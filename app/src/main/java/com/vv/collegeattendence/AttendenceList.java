package com.vv.collegeattendence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AttendenceList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_list);
        TextView subjectNameTV,semisterTV,sectionTV,dateTV;
        semisterTV=findViewById(R.id.year);
        subjectNameTV=findViewById(R.id.subjectNameRecycle);
        sectionTV=findViewById(R.id.section);
        dateTV=findViewById(R.id.date);

        String subjectName = getIntent().getStringExtra("subjectName");
        String semister = getIntent().getStringExtra("semister");
        String section = getIntent().getStringExtra("section");
        String date = getIntent().getStringExtra("date");
        subjectNameTV.setText(subjectName);
        semisterTV.setText(semister);
        sectionTV.setText(section);
        dateTV.setText(date);
        date="_"+date;
        RecyclerView recyclerView = findViewById(R.id.attendenceRecycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SubjectNamesDB subjectNamesDB = new SubjectNamesDB(this);
        ArrayList<AttendenceListModel> arrayList= subjectNamesDB.getAttendenceList(subjectName,semister,section,date);
        if(arrayList.size()>0){
            AttendenceRecycleAdapter adapter = new AttendenceRecycleAdapter(this,arrayList,date);
            recyclerView.setAdapter(adapter);
        }
        findViewById(R.id.exitbutton).setOnClickListener( v->{
            finish();
        });
    }
}