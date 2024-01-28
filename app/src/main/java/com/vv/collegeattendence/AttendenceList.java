package com.vv.collegeattendence;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.ArrayList;

public class AttendenceList extends AppCompatActivity{
    AttendenceRecycleAdapter adapter;
    ArrayList<AttendenceListModel> arrayList;
    private final int EXTERNAL_STORAGE_PERMISSION_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_list);
        try {
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
                adapter  = new AttendenceRecycleAdapter(this,arrayList,date,"default");
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
                                adapter  = new AttendenceRecycleAdapter(AttendenceList.this,arrayList,date,"checkAll");
                            recyclerView.setAdapter(adapter);

                        }
                        else if (id==R.id.uncheckAll) {
                                adapter  = new AttendenceRecycleAdapter(AttendenceList.this,arrayList,date,"unCheckAll");
                            recyclerView.setAdapter(adapter);

                        }
                        else if (id==R.id.downloadAttendence) {

                            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                                    ContextCompat.checkSelfPermission(AttendenceList.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                                    ContextCompat.checkSelfPermission(AttendenceList.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(AttendenceList.this,
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                        EXTERNAL_STORAGE_PERMISSION_CODE);
                            }

                            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                            ArrayList<StringBuffer> str =database.downloadData(TABLE_NAME);
                            String fileName=subject+"_"+semister+"_"+section+".csv";
                            File file = new File(folder,fileName);
                            writeTextData(file, "");

                            if(file.exists()) {
                                if (str.size() > 0) {
                                    String data = "";
                                    for (StringBuffer i:str) {
                                        data = data + i + "\n";
                                    }

                                    writeTextData(file, data);
                                    Toast.makeText(AttendenceList.this, fileName+" Downloaded", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(AttendenceList.this, "No data found to upload...", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(AttendenceList.this, "File cannot be created!", Toast.LENGTH_SHORT).show();
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
    private void writeTextData(File file, String data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}