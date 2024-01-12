package com.vv.collegeattendence;
public class AttendenceListModel {
    public String pinNo;
    public String sno;
    public int attendence;
    public int id;

    AttendenceListModel(String pinNo,String sno,int id,int attendence){
        this.pinNo = pinNo;
        this.sno=sno;
        this.id=id;
        this.attendence=attendence;
    }
    AttendenceListModel(String pinNo){
        this.pinNo = pinNo;
    }
    AttendenceListModel(int id,boolean attendence){
        this.id=id;
    }

}