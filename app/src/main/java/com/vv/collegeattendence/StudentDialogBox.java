package com.vv.collegeattendence;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StudentDialogBox extends AppCompatActivity {
    String snoString,pinNoString,nameString;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public  void studentDialogBoxFun( Context contextM, String TABLE,AttendenceListModel attendenceListModel){
        try{
            String TABLE_NAME=TABLE;
            DataBase database = new DataBase(contextM);
            Dialog dialog = new Dialog(contextM,R.style.Dialogbox_border);
            dialog.setContentView(R.layout.add_student);
            EditText snoET,pinNoET,nameET;
            Button add;
            ImageView delete;
            delete=dialog.findViewById(R.id.delete);
            dialog.findViewById(R.id.cancel).setOnClickListener(v ->{
                dialog.dismiss();
            });
            delete.setVisibility(View.GONE);
            TextView titleTV=dialog.findViewById(R.id.title);
            snoET=dialog.findViewById(R.id.sno);
            pinNoET=dialog.findViewById(R.id.pinNo);
            nameET=dialog.findViewById(R.id.name);
            add=dialog.findViewById(R.id.add);
                titleTV.setText("ADD STUDENT");
                add.setOnClickListener(v ->{
                    snoString=snoET.getText().toString().trim();
                    pinNoString=pinNoET.getText().toString().trim();
                    nameString=nameET.getText().toString().trim();
                    if(snoString.isEmpty() || pinNoString.isEmpty() || nameString.isEmpty()){
                        if(snoString.isEmpty() && pinNoString.isEmpty() && nameString.isEmpty())
                            Toast.makeText(contextM, "Enter all fields", Toast.LENGTH_SHORT).show();
                        else if(snoString.isEmpty())
                            Toast.makeText(contextM, "Enter S NO", Toast.LENGTH_SHORT).show();
                        else if(pinNoString.isEmpty())
                            Toast.makeText(contextM, "Enter PIN NO", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(contextM, "Enter NAME", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(!database.checkStudentPinNO(TABLE_NAME,pinNoString)){
                            if(database.insertSubject(TABLE_NAME,snoString,nameString,pinNoString)) {
                                Toast.makeText(contextM, "Student Added Successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                            else
                                Toast.makeText(contextM, "Student Added Failed !!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(contextM, "Student already present with this pin no", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            dialog.show();
        }
        catch (Exception ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
