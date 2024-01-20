package com.vv.collegeattendence;
public class AttendenceListModel {
    public String name;
    public String pinNo;
    public String sno;
    public int attendence;

    public int id;

    AttendenceListModel(String name,String pinNo,String sno,int id,int attendence){
        this.name=name;
        this.pinNo = pinNo;
        this.sno=sno;
        this.id=id;
        this.attendence=attendence;
    }
}