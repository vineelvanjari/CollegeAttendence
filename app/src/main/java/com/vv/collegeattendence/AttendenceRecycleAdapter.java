package com.vv.collegeattendence;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AttendenceRecycleAdapter extends RecyclerView.Adapter<AttendenceRecycleAdapter.ViewHolder> {
    Context context;
    ArrayList<AttendenceListModel> arrayList;
    String date;

    public AttendenceRecycleAdapter(Context context, ArrayList<AttendenceListModel> arrayList,String date){
        this.context=context;
        this.arrayList=arrayList;
        this.date=date;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(context).inflate(R.layout.activity_attendence_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.pinNO.setText(arrayList.get(position).pinNo);
        holder.sno.setText(arrayList.get(position).sno);
        if((arrayList.get(position).attendence)==1){
            holder.checkBox.setChecked(true);
        }
        else {
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnClickListener(v ->{
            boolean flag=false;
            if(holder.checkBox.isChecked()){
                flag=true;
            }
            SubjectNamesDB subjectNamesDB = new SubjectNamesDB(context);
            subjectNamesDB.inserDateValue(arrayList.get(position).id,date,flag);

        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pinNO,sno;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pinNO=itemView.findViewById(R.id.pinNo);
            sno=itemView.findViewById(R.id.sno);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
