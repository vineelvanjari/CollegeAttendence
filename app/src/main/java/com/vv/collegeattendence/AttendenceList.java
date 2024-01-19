package com.vv.collegeattendence;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class AttendenceList extends AppCompatActivity{
    AttendenceRecycleAdapter adapter;
    ArrayList<AttendenceListModel> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_list);
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
        String date = getIntent().getStringExtra("date");
        subjectNameTV.setText(subject);
        semisterTV.setText(semister);
        sectionTV.setText(section);
        dateTV.setText(date);
        String finalDate="_"+date;
        RecyclerView recyclerView = findViewById(R.id.attendenceRecycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DataBase database = new DataBase(this);
        arrayList= database.getAttendenceList(TABLE_NAME,finalDate);
        if(arrayList.size()>0){
            adapter  = new AttendenceRecycleAdapter(this,arrayList,date);
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
backPress();
        });
        findViewById(R.id.dots).setOnClickListener(v ->{
            PopupMenu popupMenu = new PopupMenu(this,v);
            popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if(id==R.id.delete){
                        AlertDialog.Builder builder = new AlertDialog.Builder(AttendenceList.this,R.style.Dialogbox_border);
                        builder.setTitle("DO YOU WANT TO DELETE THIS SUBJECT ??");
                        builder.setIcon(R.drawable.delete);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                database.deleteTable(TABLE_NAME);
                                setResult(1);
                                dialogInterface.dismiss();
                                backPress();
                            }
                        });
                        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int which) {
                                dialog1.dismiss();
                            }
                        });
                        builder.show();
                    } else if (id==R.id.checkAll) {
                        database.setAllChecked(TABLE_NAME,finalDate);
                        arrayList=database.getAttendenceList(TABLE_NAME,finalDate);
                        adapter  = new AttendenceRecycleAdapter(AttendenceList.this,arrayList,date);
                        recyclerView.setAdapter(adapter);
                    }
                    return true;
                }
            });
            popupMenu.show();
        });
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
}