package com.vv.collegeattendence;
public class AttendenceListModel {
    public String name;
    public String pinNo;
    public String parentsNumber;
    public int attendence;

    public int id;

    AttendenceListModel(String name,String pinNo,String parentsNumber,int id,int attendence){
        this.name=name;
        this.pinNo = pinNo;
        this.parentsNumber=parentsNumber;
        this.id=id;
        this.attendence=attendence;
    }
    public void setValueToChange(int valueToChange) {
        this.attendence = valueToChange;
    }
}