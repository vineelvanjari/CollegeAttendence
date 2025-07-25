package com.vv.collegeattendence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<SubjectModel> arrayList;
    SubjectRecycleAdapter adapter;
    private final int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    private final int  REQUEST_CSV_FILE = 24;
    String yearString,sectionString,subjectName;
    SubjectNamesDB subjectDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        recyclerView=findViewById(R.id.subjectsRecycle);
        subjectDB=new SubjectNamesDB(this);
        arrayList=subjectDB.getSubjects();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(arrayList.size()>0){
            adapter= new SubjectRecycleAdapter(this,arrayList);
            recyclerView.setAdapter(adapter);

        }

        findViewById(R.id.openDialog).setOnClickListener(v->{
            Dialog dialog=new Dialog(this,R.style.Dialogbox_border);
            dialog.setContentView(R.layout.subject_dialogbox);

            Spinner year = dialog.findViewById(R.id.year);
            Spinner section = dialog.findViewById(R.id.section);
            ArrayList<String> yearAL = new ArrayList<>();
            yearAL.add("1st semister");
            yearAL.add("2st semister");
            yearAL.add("3st semister");
            yearAL.add("4st semister");
            yearAL.add("5st semister");
            yearAL.add("6st semister");
            ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, yearAL);
            year.setAdapter(yearAdapter);
            ArrayList<String> sectionAL = new ArrayList<>();
            sectionAL.add("1st section");
            sectionAL.add("2nd section");
            ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sectionAL);
            section.setAdapter(sectionAdapter);

            dialog.show();
            dialog.findViewById(R.id.add).setOnClickListener(a->{
                EditText editText = dialog.findViewById(R.id.subjectName);
                yearString = (String) year.getSelectedItem();
                sectionString = (String) section.getSelectedItem();
                subjectName=editText.getText().toString();
                if(subjectName.isEmpty()){
                    Toast.makeText (this, "Enter Subject Name", Toast.LENGTH_SHORT).show();
                }else
                {
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                EXTERNAL_STORAGE_PERMISSION_CODE);
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/csv/*"); // Allow all text file
                    startActivityForResult(intent, REQUEST_CSV_FILE);
                    dialog.dismiss();
                }
            });
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CSV_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {

                Uri uri = data.getData();
                try {
                    // Open an InputStream to read the selected CSV file
                    assert uri != null;
                    InputStream inputStream = getContentResolver().openInputStream(uri);

                    // Read the contents of the CSV file
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                    reader.readLine();   //read a row to skip the column names in file
                    while ((line = reader.readLine()) != null) {
                        insertCustomersData(line);
                    }
                    // Close the reader and input stream
                    reader.close();
                    assert inputStream != null;
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else Toast.makeText(this, "No Data available in file!", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertCustomersData(String cusData) {
        StringTokenizer customerData = new StringTokenizer(cusData, ",");
        ArrayList<String> cusDataDownload = new ArrayList<>();

        StringBuilder str = new StringBuilder();
        while (customerData.hasMoreTokens()) {
            String data = customerData.nextToken();
            cusDataDownload.add(data);
            str.append(data).append(" ");
        }

        String sNo=cusDataDownload.get(0);
        String name = cusDataDownload.get(1);
        String pinNO = cusDataDownload.get(2);
        if(!subjectDB.insertSubject(subjectName,yearString,sectionString,sNo,name,pinNO))
            Toast.makeText(this, "added failed", Toast.LENGTH_SHORT).show();
        arrayList=subjectDB.getSubjects();
        SubjectRecycleAdapter adapter1 = new SubjectRecycleAdapter(this,arrayList);
        recyclerView.setAdapter(adapter1);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }


}