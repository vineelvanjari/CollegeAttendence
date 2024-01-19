package com.vv.collegeattendence;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SubjectRecycleAdapter extends RecyclerView.Adapter<SubjectRecycleAdapter.ViewHolder> {
    Context context;
    ArrayList<SubjectModel> arrayList,subjectModel;

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
        holder.subjectNameTV.setText(arrayList.get(position).subjectName.replace("$"," "));
        holder.yearTV.setText(arrayList.get(position).semister.replace("$"," "));
        holder.sectionTV.setText(arrayList.get(position).section.replace("$"," "));
        holder.subjectCatdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(context,R.style.Dialogbox_border);
                dialog.setContentView(R.layout.date_dialog_box);
                EditText Date1 = dialog.findViewById(R.id.selectDate);
                Date currentDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yy h:mm:ss a"); // Add "ss" for seconds
                String formattedDateTime = dateFormat.format(currentDate);
                String date1 = formattedDateTime.substring(0, 8);
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
                    String TABLE_NAME=arrayList.get(position).subjectName+"_"+arrayList.get(position).semister+"_"+arrayList.get(position).section;

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
            }

        });

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectNameTV,yearTV,sectionTV;
        CardView subjectCatdView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectNameTV=itemView.findViewById(R.id.subjectNameRecycle);
            subjectCatdView=itemView.findViewById(R.id.subjectCatdView);
            yearTV=itemView.findViewById(R.id.year);
            sectionTV=itemView.findViewById(R.id.section);
        }
    }
}
