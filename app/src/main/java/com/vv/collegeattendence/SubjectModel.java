package com.vv.collegeattendence;

public class SubjectModel {
   public String subjectName;
   public String semister;
   public String section;
   public String TABLE_NAME;

    public SubjectModel(String subjectName,String semister,String section){
        this.subjectName=subjectName;
        this.semister=semister;
        this.section=section;
    }
public SubjectModel(String TABLE_NAME, String subjectName,String semister,String section){
        this.TABLE_NAME=TABLE_NAME;
        this.subjectName=subjectName;
        this.semister=semister;
        this.section=section;
    }
}
