package com.vv.collegeattendence;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;

import static android.app.Activity.RESULT_OK;
import static android.media.CamcorderProfile.get;
import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import static com.vv.collegeattendence.MainActivity.isFirstCharDigit;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SubjectRecycleAdapter extends RecyclerView.Adapter<SubjectRecycleAdapter.ViewHolder> {
    Context context;
    ArrayList<SubjectModel> arrayList,subjectModel;
    private final int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    String TABLE_NAME;
    String subjectName,semister,section;
    DataBase database;
    public SubjectRecycleAdapter(Context context, ArrayList<SubjectModel> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(context).inflate(R.layout.subject_name_design,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {
        try{

            subjectName=arrayList.get(position).subjectName;
            semister=arrayList.get(position).semister;
            section=arrayList.get(position).section;
            holder.subjectNameTV.setText(subjectName.replace("$"," "));
            holder.yearTV.setText(semister.replace("$"," "));
            holder.sectionTV.setText(section.replace("$"," "));
            TABLE_NAME=subjectName+"_"+semister+"_"+section;
            if(TABLE_NAME.contains(" ")){
                subjectName=subjectName.replaceAll("\\s+", "\\$");
                semister =semister.replaceAll("\\s+", "\\$");
                section= section.replaceAll("\\s+", "\\$");
            }
            TABLE_NAME=subjectName+"_"+semister+"_"+section;
            holder.subjectCatdView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subjectName=arrayList.get(position).subjectName;
                    semister=arrayList.get(position).semister;
                    section=arrayList.get(position).section;
                    TABLE_NAME=subjectName+"_"+semister+"_"+section;
                    BottomSheetDialogFrg bottomSheetDialogFrg = new  BottomSheetDialogFrg(context,TABLE_NAME);
                    FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
                    bottomSheetDialogFrg.show(fragmentManager, bottomSheetDialogFrg.getTag());
                }

            });
            holder.dots.setOnClickListener(v ->{
                subjectName=arrayList.get(position).subjectName;
                semister=arrayList.get(position).semister;
                section=arrayList.get(position).section;
                TABLE_NAME=subjectName+"_"+semister+"_"+section;
                database = new DataBase(context);
                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.subject_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id=item.getItemId();
                        if(id==R.id.delete){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Dialogbox_border);
                            builder.setTitle("DO YOU WANT TO DELETE THIS SUBJECT ??");
                            builder.setIcon(R.drawable.delete);
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    database.deleteTable(TABLE_NAME);
                                    dialogInterface.dismiss();
                                    arrayList.remove(position);
                                    notifyItemRemoved(position);
                                }
                            });
                            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog1, int which) {
                                    dialog1.dismiss();
                                }
                            });
                            AlertDialog alertDialog = builder.show();
                            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            positiveButton.setTextColor(ContextCompat.getColor(context, R.color.toolbar_color));
                            Button neutralButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                            neutralButton.setTextColor(ContextCompat.getColor(context, R.color.toolbar_color));
                        }
                        else if(id==R.id.edit){
                            Dialog dialog=new Dialog(context,R.style.Dialogbox_border);
                            dialog.setContentView(R.layout.subject_dialogbox);
                            Spinner semisterSpinner = dialog.findViewById(R.id.semister);
                            Spinner sectionSpinner = dialog.findViewById(R.id.section);
                            ArrayList<String> semisterAL = new ArrayList<>();
                            semisterAL.add("1st semister");
                            semisterAL.add("2nd semister");
                            semisterAL.add("3rd semister");
                            semisterAL.add("4th semister");
                            semisterAL.add("5th semister");
                            semisterAL.add("6th semister");
                            ArrayAdapter<String> semisterAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, semisterAL);
                            semisterSpinner.setAdapter(semisterAdapter);
                            ArrayList<String> sectionAL = new ArrayList<>();
                            sectionAL.add("1st section");
                            sectionAL.add("2nd section");
                            ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, sectionAL);
                            sectionSpinner.setAdapter(sectionAdapter);
                            String spinnerString = semister.replace("$"," ");
                            int swapInt = semisterAL.indexOf(spinnerString);
                            String swapString = semisterAL.get(0);
                            semisterAL.set(0, spinnerString);
                            semisterAL.set(swapInt, swapString);
                            semisterAdapter.notifyDataSetChanged();
                            semisterSpinner.setAdapter(semisterAdapter);
                            spinnerString = section.replace("$"," ");
                            swapInt = sectionAL.indexOf(spinnerString);
                            swapString = sectionAL.get(0);
                            sectionAL.set(0, spinnerString);
                            sectionAL.set(swapInt, swapString);
                            sectionAdapter.notifyDataSetChanged();
                            sectionSpinner.setAdapter(sectionAdapter);
                            EditText subjectNameTV = dialog.findViewById(R.id.subjectName);
                            subjectNameTV.setText(subjectName.replace("$"," "));
                            dialog.show();
                            Button add=dialog.findViewById(R.id.add);
                            add.setText("SAVE");
                            add.setOnClickListener(a->{
                                String  semisterString,sectionString,subjectName;
                                semisterString =  semisterSpinner.getSelectedItem().toString();
                                sectionString =  sectionSpinner.getSelectedItem().toString();
                                subjectName=subjectNameTV.getText().toString().trim();
                                if(subjectName.isEmpty()){
                                    Toast.makeText (context, "Enter Subject Name", Toast.LENGTH_SHORT).show();
                                } else if (isFirstCharDigit(subjectName)) {
                                    Toast.makeText(context, "1st number shouldn't be a number", Toast.LENGTH_SHORT).show();
                                } else
                                {
                                    subjectName=subjectName.replaceAll("\\s+", "\\$");
                                    semisterString =semisterString.replaceAll("\\s+", "\\$");
                                    sectionString= sectionString.replaceAll("\\s+", "\\$");
                                    String NEW_TABLE_NAME= subjectName+"_"+semisterString+"_"+sectionString;
                                    Log.d("table12345",TABLE_NAME);
                                    Log.d("table12345",NEW_TABLE_NAME);
                                    if(!TABLE_NAME.equals(NEW_TABLE_NAME)){
                                        database.changeTableName(TABLE_NAME,NEW_TABLE_NAME);
                                        TABLE_NAME=NEW_TABLE_NAME;
                                        SubjectModel subjectModel =  new SubjectModel(subjectName,semisterSpinner.getSelectedItem().toString(),sectionSpinner.getSelectedItem().toString());
                                        arrayList.set(position,subjectModel);
                                        notifyDataSetChanged();
                                    }
                                    dialog.dismiss();
                                }
                            });
                        }
                        else if(id==R.id.downloadAttendence){
                            Dialog dialog = new Dialog(context,R.style.Dialogbox_border);
                            dialog.setContentView(R.layout.start_date_end_date);
                            TextView startDateTV=dialog.findViewById(R.id.startDate);
                            TextView endDateTV=dialog.findViewById(R.id.endDate);
                            ImageView delete;
                            delete=dialog.findViewById(R.id.delete);
                            dialog.findViewById(R.id.cancel).setOnClickListener(v ->{
                                dialog.dismiss();
                            });
                            delete.setVisibility(View.GONE);
                            Date currentDate = new Date();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy h:mm:ss a"); // Add "ss" for seconds
                            String formattedDateTime = dateFormat.format(currentDate);
                            String date1 = formattedDateTime.substring(0, 10);
                            startDateTV.setText(date1);
                            startDateTV.setOnClickListener(va ->{
                                final Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);

                                DatePickerDialog datePickerDialog = new DatePickerDialog(
                                        context,
                                        (view, year1, month1, dayOfMonth) -> {
                                            String date = String.format("%02d_%02d_%04d", dayOfMonth, month1 + 1, year1);
                                            startDateTV.setText(date);
                                        },
                                        year, month, day);

                                datePickerDialog.show();
                            });
                            endDateTV.setText(date1);
                            endDateTV.setOnClickListener(va ->{
                                final Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);

                                DatePickerDialog datePickerDialog = new DatePickerDialog(
                                        context,
                                        (view, year1, month1, dayOfMonth) -> {
                                            String date = String.format("%02d_%02d_%04d", dayOfMonth, month1 + 1, year1);
                                            endDateTV.setText(date);
                                        },
                                        year, month, day);

                                datePickerDialog.show();
                            });
                            dialog.findViewById(R.id.downloadAttendence).setOnClickListener(v ->{
                                String startDate=startDateTV.getText().toString();
                                String endDate=endDateTV.getText().toString();
                                ArrayList<StringBuffer> str =database.downloadData(TABLE_NAME,startDate,endDate);
                                if(str.size()>0) {
                                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                                            ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                                            ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions((Activity) context,
                                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                EXTERNAL_STORAGE_PERMISSION_CODE);
                                    }

                                    File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                                    String fileName =TABLE_NAME.replace("$","_")+".csv";
                                    File file = new File(folder, fileName);
                                    writeTextData(file, "");

                                    if (file.exists()) {
                                        if (str.size() > 0) {
                                            String data = "";
                                            for (StringBuffer i : str) {
                                                data = data + i + "\n";
                                            }

                                            writeTextData(file, data);
                                            Toast.makeText(context, fileName + " Downloaded", Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(context, "No data found to upload...", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(context, "File cannot be created!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                                else
                                    Toast.makeText(context, "No attendence taken between "+startDate+" - "+endDate, Toast.LENGTH_SHORT).show();
                            });
                            dialog.show();
                        }
                        else if(id==R.id.addStudent){
                            StudentDialogBox studentDialogBox = new  StudentDialogBox();
                            studentDialogBox.studentDialogBoxFun(context,TABLE_NAME,new AttendenceListModel("","","",0,0));
                        }
                        return true;
                    }
                });
                popupMenu.show();
            });

        }
        catch (Exception ex){
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectNameTV,yearTV,sectionTV;
        CardView subjectCatdView;
        ImageView dots;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectNameTV=itemView.findViewById(R.id.subjectNameRecycle);
            subjectCatdView=itemView.findViewById(R.id.subjectCatdView);
            yearTV=itemView.findViewById(R.id.year);
            sectionTV=itemView.findViewById(R.id.section);
            dots=itemView.findViewById(R.id.dots);
        }
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
