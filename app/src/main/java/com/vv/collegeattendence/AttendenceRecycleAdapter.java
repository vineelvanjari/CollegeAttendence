package com.vv.collegeattendence;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class AttendenceRecycleAdapter extends RecyclerView.Adapter<AttendenceRecycleAdapter.ViewHolder> {
    Context context;
    ArrayList<AttendenceListModel> arrayList;
    String date;
    public ArrayList<CheckBoxModel> CheckBoxArrayList = new ArrayList<>();

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
        String name= arrayList.get(position).name;
        if (name.length() < 15) {
            // Calculate the number of spaces needed
            int spacesToAdd = 15 - name.length();

            // Add spaces to the string
            for (int i = 0; i < spacesToAdd; i++) {
                name += " ";
            }
            holder.studentName.setText(name);
        } else {
            StringBuilder stringBuilder = new StringBuilder(name);
            stringBuilder.insert(15,"\n");
            holder.studentName.setText(stringBuilder.toString());
        }
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
            Log.d("flag",arrayList.get(position).id+""+flag);
            CheckBoxModel checkBoxModel = new CheckBoxModel(arrayList.get(position).id,flag);
            CheckBoxArrayList.add(checkBoxModel);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pinNO,sno,studentName;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pinNO=itemView.findViewById(R.id.pinNo);
            sno=itemView.findViewById(R.id.sno);
            checkBox = itemView.findViewById(R.id.checkbox);
            studentName=itemView.findViewById(R.id.studentName);
        }
    }
}
