package com.vv.collegeattendence;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;

import static android.app.Activity.RESULT_OK;
import static android.media.CamcorderProfile.get;
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
import androidx.fragment.app.FragmentManager;
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
                String TABLE_NAME=arrayList.get(position).subjectName+"_"+arrayList.get(position).semister+"_"+arrayList.get(position).section;
                BottomSheetDialogFrg bottomSheetDialogFrg = new  BottomSheetDialogFrg(context,TABLE_NAME);
                FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
                bottomSheetDialogFrg.show(fragmentManager, bottomSheetDialogFrg.getTag());
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
