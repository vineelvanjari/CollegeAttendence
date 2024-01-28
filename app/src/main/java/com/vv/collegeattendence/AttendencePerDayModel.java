package com.vv.collegeattendence;

public class AttendencePerDayModel {
    public String  timeToDisplay;
   public String timeForDB;
    public AttendencePerDayModel(String timeToDisplay,String timeForDB){
        this.timeToDisplay=timeToDisplay;
        this.timeForDB=timeForDB;
    }
}
