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

    String TABLE_NAME;
    boolean flag=false;
    String parentNumber,pin,name;
    boolean showEdit;


    public AttendenceRecycleAdapter(Context context, ArrayList<AttendenceListModel> arrayList,String date,String checked,String TABLE_NAME,String finalDate,boolean showEdit){
        this.context=context;
        this.arrayList=arrayList;
        this.date=date;
        this.checked=checked;
        this.TABLE_NAME=TABLE_NAME;
        this.finalDate=finalDate;
        this.showEdit=showEdit;
        for(int i=0;i<arrayList.size();i++){
            CheckBoxModel checkBoxModel  = new CheckBoxModel(arrayList.get(i).id, arrayList.get(i).attendence == 1);
            CheckBoxArrayList.add(checkBoxModel);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(context).inflate(R.layout.activity_attendence_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int  position) {
        try {
            DataBase database = new DataBase(context);
            parentNumber=arrayList.get(position).parentsNumber;
            pin=arrayList.get(position).pinNo;
            int id=arrayList.get(position).id;
            holder.pinNO.setText(pin);
            holder.sno.setText(id+")");
            if(showEdit){
                holder.checkBox.setEnabled(false);
            }
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
                String[] nameList=name.split(" ");
                StringBuilder firstLine  = new StringBuilder();
                StringBuilder nextLine  = new StringBuilder();
                String displayName="";
                for (String word : nameList) {
                    if (firstLine.length() + word.length() + 1 <= 15) {
                        firstLine.append(word).append(" ");
                    } else {
                        nextLine.append("").append(word);
                    }
                }
                while (firstLine.length()<=15) {
                    firstLine.append(" ");
                }

                displayName=firstLine+"\n"+nextLine;
                holder.studentName.setText(displayName);
            }
            if((arrayList.get(position).attendence)==1){
                holder.checkBox.setChecked(true);
                flag=true;
            }
            else {
                holder.checkBox.setChecked(false);
                flag=false;
            }
            holder.checkBox.setOnClickListener(v ->{
                if(holder.checkBox.isChecked()){
                    flag=true;
                    CheckBoxModel checkBoxModel = new CheckBoxModel(arrayList.get(position).id,true);
                    CheckBoxArrayList.set(position,checkBoxModel);
                    AttendenceListModel attendenceListModel =  arrayList.get(position);
                    attendenceListModel.setValueToChange(1);
                }
                else{
                    CheckBoxModel checkBoxModel = new CheckBoxModel(arrayList.get(position).id,false);
                    CheckBoxArrayList.set(position,checkBoxModel);
                    AttendenceListModel attendenceListModel =  arrayList.get(position);
                    attendenceListModel.setValueToChange(0);
                }
            });

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    parentNumber=arrayList.get(position).parentsNumber;
                    pin=arrayList.get(position).pinNo;
                    name= arrayList.get(position).name;
                    String  oldPin=pin;
                    Dialog dialog = new Dialog(context,R.style.Dialogbox_border);
                    dialog.setContentView(R.layout.add_student);
                    EditText parentNumberET,pinNoET,nameET;
                    Button add;
                    TextView titleTV=dialog.findViewById(R.id.title);

                    parentNumberET=dialog.findViewById(R.id.parentNumber);
                    pinNoET=dialog.findViewById(R.id.pinNo);
                    nameET=dialog.findViewById(R.id.name);
                    add=dialog.findViewById(R.id.add);
                    add.setText("UPDATE");
                    titleTV.setText("EDIT STUDENT");
                    parentNumberET.setText(parentNumber);
                    pinNoET.setText(pin);
                    nameET.setText(name.trim());
                    dialog.findViewById(R.id.cancel).setOnClickListener(va ->{
                        dialog.dismiss();
                    });

                    add.setOnClickListener(a ->{
                        parentNumber=parentNumberET.getText().toString().trim();
                        pin=pinNoET.getText().toString().trim();
                        name=nameET.getText().toString().trim();
                        if(parentNumber.isEmpty() || pin.isEmpty() || name.isEmpty()){
                            if(parentNumber.isEmpty() && pin.isEmpty() && name.isEmpty()) {
                                Toast.makeText(context, "Enter all fields", Toast.LENGTH_SHORT).show();
                            }
                            else if(parentNumber.isEmpty()) {
                                Toast.makeText(context, "Enter PARENT NUMBER", Toast.LENGTH_SHORT).show();
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
                                if(database.updateStudent(TABLE_NAME,parentNumber,name,pin,id)) {
                                    Toast.makeText(context, "Student Updated Successfully", Toast.LENGTH_SHORT).show();
                                    int i=0;
                                    if(flag) {
                                        i=1;
                                    }
                                    arrayList.set(position,new AttendenceListModel(name,pin,parentNumber,id,i));
                                    notifyItemChanged(position);
                                    dialog.dismiss();
                                }
                                else {
                                    Toast.makeText(context, "Student Updated Failed !!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if(!database.checkStudentPinNO(TABLE_NAME,pin) ){
                                if(database.updateStudent(TABLE_NAME,parentNumber,name,pin,id)) {
                                    Toast.makeText(context, "Student Updated Successfully", Toast.LENGTH_SHORT).show();
                                    int i=0;
                                    if(flag) {
                                        i=1;
                                    }
                                    arrayList.set(position,new AttendenceListModel(name,pin,parentNumber,id,i));
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
