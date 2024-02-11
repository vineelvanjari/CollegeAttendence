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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    DataBase dataBase;
    private RecyclerView recyclerView;
    private ArrayList<SubjectModel> arrayList;
    SubjectRecycleAdapter adapter;
    private final int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    private final int  REQUEST_CSV_FILE = 24;
    String semisterString,sectionString,subjectName,TABLE_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
            setSupportActionBar(toolbar);
            recyclerView=findViewById(R.id.subjectsRecycle);
            dataBase=new DataBase(this);
            arrayList=dataBase.getSubjects();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            if(arrayList.size()>0){
                adapter= new SubjectRecycleAdapter(this,arrayList);
                recyclerView.setAdapter(adapter);
            }

            findViewById(R.id.openDialog).setOnClickListener(v->{
                Dialog dialog=new Dialog(this,R.style.Dialogbox_border);
                dialog.setContentView(R.layout.subject_dialogbox);

                Spinner semisterSpinner = dialog.findViewById(R.id.semister);
                Spinner section = dialog.findViewById(R.id.section);
                ArrayList<String> semisterAL = new ArrayList<>();
                semisterAL.add("1st semester");
                semisterAL.add("2nd semester");
                semisterAL.add("3rd semester");
                semisterAL.add("4th semester");
                semisterAL.add("5th semester");
                semisterAL.add("6th semester");
                ArrayAdapter<String> semisterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, semisterAL);
                semisterSpinner.setAdapter(semisterAdapter);
                ArrayList<String> sectionAL = new ArrayList<>();
                sectionAL.add("1st section");
                sectionAL.add("2nd section");
                ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sectionAL);
                section.setAdapter(sectionAdapter);

                dialog.show();
                dialog.findViewById(R.id.add).setOnClickListener(a->{
                    EditText editText = dialog.findViewById(R.id.subjectName);
                    semisterString =  semisterSpinner.getSelectedItem().toString();
                    sectionString =  section.getSelectedItem().toString();
                    subjectName=editText.getText().toString().trim();
                    subjectName=subjectName.replaceAll("\\s+", "\\$");
                    if(subjectName.isEmpty()){
                        Toast.makeText (this, "Enter Subject Name", Toast.LENGTH_SHORT).show();
                    } else if (isFirstCharDigit(subjectName)) {
                        Toast.makeText(this, "1st number shouldn't be a number", Toast.LENGTH_SHORT).show();
                    }
                    else if (!containsSpecialCharacters(subjectName)) {
                        Toast.makeText(this, "Table Name should contain a-z or  0-9  or _  or $ ", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        subjectName=subjectName.replaceAll("\\s+", "\\$");
                        semisterString =semisterString.replaceAll("\\s+", "\\$");
                        sectionString= sectionString.replaceAll("\\s+", "\\$");
                        TABLE_NAME= subjectName+"_"+semisterString+"_"+sectionString;
                        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("text/csv/*"); // Allow all text file
                        startActivityForResult(intent, REQUEST_CSV_FILE);
                        dialog.dismiss();
                    }
                });
            });

        }
        catch (Exception e){
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == REQUEST_CSV_FILE && resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    try {
                        assert uri != null;
                        String fileType = getContentResolver().getType(uri);
                        Log.d("csv",fileType);
                        if ("text/comma-separated-values".equals(fileType)) {

                            // Open an InputStream to read the selected CSV file
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            // Read the contents of the CSV file
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                            String line;
                            reader.readLine();   //read a row to skip the column names in file
                            if(!dataBase.isTableExist(TABLE_NAME)){
                                dataBase.createTable(TABLE_NAME);
                                while ((line = reader.readLine()) != null) {
                                    insertStudentData(line);
                                }
                            }
                            else {
                                Toast.makeText(this, "Subject Already Existed ", Toast.LENGTH_SHORT).show();
                            }
                            // Close the reader and input stream
                            reader.close();
                            assert inputStream != null;
                            inputStream.close();
                        }
                        else
                            Toast.makeText(this, "File type should be .csv", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else Toast.makeText(this, "No Data available in file!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void insertStudentData(String stuData) {
      try{
          StringTokenizer studentData = new StringTokenizer(stuData, ",");
          ArrayList<String> studentDataDownload = new ArrayList<>();

          StringBuilder str = new StringBuilder();
          while (studentData.hasMoreTokens()) {
              String data = studentData.nextToken();
              studentDataDownload.add(data);
              str.append(data).append(" ");
          }
          String name = studentDataDownload.get(0);
          String pinNO = studentDataDownload.get(1);
          String parentsNumber=studentDataDownload.get(2);

          if(!dataBase.insertSubject(TABLE_NAME,parentsNumber,name,pinNO))
              Toast.makeText(this, "added failed", Toast.LENGTH_SHORT).show();
          arrayList=dataBase.getSubjects();
          adapter = new SubjectRecycleAdapter(this,arrayList);
          recyclerView.setAdapter(adapter);
      }
      catch (Exception e){
          Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
      }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.settings)
        {
            startActivity(new Intent(this, settings.class));
        }
        return super.onOptionsItemSelected(item);
    }
    public static boolean isFirstCharDigit(String input) {
        // Check if the string is not empty and the first character is a digit
        return !input.isEmpty() && Character.isDigit(input.charAt(0));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }
    public boolean containsSpecialCharacters(String input) {
        String regex = "^[a-zA-Z0-9_$ ]*$";
        return input.matches(regex);
    }
}