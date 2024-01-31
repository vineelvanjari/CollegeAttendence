package com.vv.collegeattendence;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class AttendenceRecycleAdapter extends RecyclerView.Adapter<AttendenceRecycleAdapter.ViewHolder> {
    Context context;
    ArrayList<AttendenceListModel> arrayList;
    String date;
    public ArrayList<CheckBoxModel> CheckBoxArrayList = new ArrayList<>();
    public String checked,finalDate;
    boolean loopOnceTrue=true;
    boolean loopOnceFalse=true;
    String TABLE_NAME;
    boolean flag=false;
    String sno,pin,name;
    boolean showEdit;


    public AttendenceRecycleAdapter(Context context, ArrayList<AttendenceListModel> arrayList,String date,String checked,String TABLE_NAME,String finalDate,boolean showEdit){
        this.context=context;
        this.arrayList=arrayList;
        this.date=date;
        this.checked=checked;
        this.TABLE_NAME=TABLE_NAME;
        this.finalDate=finalDate;
        this.showEdit=showEdit;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(context).inflate(R.layout.activity_attendence_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            DataBase database = new DataBase(context);
            sno=arrayList.get(position).sno;
            pin=arrayList.get(position).pinNo;
            holder.pinNO.setText(pin);
            holder.sno.setText(sno);
            if(showEdit){
                holder.checkBox.setEnabled(false);
            }
            int id=arrayList.get(position).id;
            name= arrayList.get(position).name;
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
            if(checked.equals("checkAll")){
                holder.checkBox.setChecked(true);
                if(loopOnceTrue){
                    for(int i=0;i<=arrayList.size();i++){
                        CheckBoxModel checkBoxModel = new CheckBoxModel(i,true);
                        CheckBoxArrayList.add(checkBoxModel);
                    }
                    loopOnceTrue=false;
                }

            }
            else if (checked.equals("unCheckAll")) {
                holder.checkBox.setChecked(false);
                if(loopOnceFalse){
                    for (int i = 0; i <= arrayList.size(); i++) {
                        CheckBoxModel checkBoxModel = new CheckBoxModel(i, false);
                        CheckBoxArrayList.add(checkBoxModel);
                    }
                    loopOnceFalse=false;
                }
            } else if (checked.equals("default")) {
                if((arrayList.get(position).attendence)==1){
                    holder.checkBox.setChecked(true);
                    flag=true;
                }
                else {
                    holder.checkBox.setChecked(false);
                    flag=false;
                }
            }
            holder.checkBox.setOnClickListener(v ->{

                if(holder.checkBox.isChecked()){
                    flag=true;
                }
                CheckBoxModel checkBoxModel = new CheckBoxModel(arrayList.get(position).id,flag);
                CheckBoxArrayList.add(checkBoxModel);
            });

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    sno=arrayList.get(position).sno;
                    pin=arrayList.get(position).pinNo;
                    name= arrayList.get(position).name;
                    String  oldPin=pin;
                    Dialog dialog = new Dialog(context,R.style.Dialogbox_border);
                    dialog.setContentView(R.layout.add_student);
                    EditText snoET,pinNoET,nameET;
                    Button add;
                    TextView titleTV=dialog.findViewById(R.id.title);

                    snoET=dialog.findViewById(R.id.sno);
                    pinNoET=dialog.findViewById(R.id.pinNo);
                    nameET=dialog.findViewById(R.id.name);
                    add=dialog.findViewById(R.id.add);
                    add.setText("UPDATE");
                    titleTV.setText("EDIT STUDENT");
                    snoET.setText(sno);
                    pinNoET.setText(pin);
                    nameET.setText(name.trim());
                    dialog.findViewById(R.id.cancel).setOnClickListener(va ->{
                        dialog.dismiss();
                    });

                    add.setOnClickListener(a ->{
                        sno=snoET.getText().toString().trim();
                        pin=pinNoET.getText().toString().trim();
                        name=nameET.getText().toString().trim();
                        if(sno.isEmpty() || pin.isEmpty() || name.isEmpty()){
                            if(sno.isEmpty() && pin.isEmpty() && name.isEmpty()) {
                                Toast.makeText(context, "Enter all fields", Toast.LENGTH_SHORT).show();
                            }
                            else if(sno.isEmpty()) {
                                Toast.makeText(context, "Enter S NO", Toast.LENGTH_SHORT).show();
                            }
                            else if(pin.isEmpty()) {
                                Toast.makeText(context, "Enter PIN NO", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(context, "Enter NAME", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            if(oldPin.equals(pin)){
                                if(database.updateStudent(TABLE_NAME,sno,name,pin,id)) {
                                    Toast.makeText(context, "Student Updated Successfully", Toast.LENGTH_SHORT).show();
                                    int i=0;
                                    if(flag) {
                                        i=1;
                                    }
                                    arrayList.set(position,new AttendenceListModel(name,pin,sno,id,i));
                                    notifyItemChanged(position);
                                    dialog.dismiss();
                                }
                                else {
                                    Toast.makeText(context, "Student Updated Failed !!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if(!database.checkStudentPinNO(TABLE_NAME,pin) ){
                                if(database.updateStudent(TABLE_NAME,sno,name,pin,id)) {
                                    Toast.makeText(context, "Student Updated Successfully", Toast.LENGTH_SHORT).show();
                                    int i=0;
                                    if(flag) {
                                        i=1;
                                    }
                                    arrayList.set(position,new AttendenceListModel(name,pin,sno,id,i));
                                    notifyItemChanged(position);
                                    dialog.dismiss();
                                }
                                else {
                                    Toast.makeText(context, "Student Updated Failed !!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(context, "Student already present with this pin no", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.findViewById(R.id.delete).setOnClickListener(va ->{
                        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Dialogbox_border);
                        builder.setTitle("DO YOU WANT TO DELETE THIS SUBJECT ??");
                        builder.setIcon(R.drawable.delete);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                               if(database.deleteStudent(TABLE_NAME,id)){
                                   Toast.makeText(context, pin+" deleted", Toast.LENGTH_SHORT).show();
                                }
                               else{
                                   Toast.makeText(context, pin+" deletion failed !!", Toast.LENGTH_SHORT).show();
                               }
                                dialogInterface.dismiss();
                               dialog.dismiss();
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
                    });
                    dialog.show();
                    return false;
                }
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pinNO,sno,studentName;
        CheckBox checkBox;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pinNO=itemView.findViewById(R.id.pinNo);
            sno=itemView.findViewById(R.id.sno);
            checkBox = itemView.findViewById(R.id.checkbox);
            studentName=itemView.findViewById(R.id.studentName);
             cardView= itemView.findViewById(R.id.cardView);
        }
    }
}
