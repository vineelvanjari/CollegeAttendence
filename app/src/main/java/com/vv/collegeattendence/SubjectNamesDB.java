package com.vv.collegeattendence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SubjectNamesDB extends SQLiteOpenHelper {
    SQLiteDatabase dbw = this.getWritableDatabase();
    SQLiteDatabase dbr = this.getReadableDatabase();
    private static String TABLE_NAME="subject";
    Context context1;
    public SubjectNamesDB(@Nullable Context context) {
        super(context, "SubjectDB", null, 1);
        this.context1=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table  "+TABLE_NAME+ "(id integer primary key autoincrement , subjectName text,yearName text,sectionName text , sNo text,studentName text,pinNo text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertSubject (String subjectName ,String yearName,String sectionName,String sNo,String studentName,String pinNo){
        ContentValues cv = new ContentValues();
        cv.put("subjectName",subjectName);
        cv.put("yearName",yearName);
        cv.put("sectionName",sectionName);
        cv.put("sNo",sNo);
        cv.put("studentName",studentName);
        cv.put("pinNo",pinNo);
        long flag= dbw.insert(TABLE_NAME,null,cv);
        if(flag!=-1)
            return true;
        else
            return false;
    }
    public ArrayList<SubjectModel> getSubjects(){
        ArrayList<SubjectModel> arrayList = new ArrayList<>();
        Cursor cursor = dbr.rawQuery("select subjectName,yearName,sectionName from "+TABLE_NAME +" group by subjectName,yearName,sectionName ",null);
        cursor.moveToFirst();
        if(cursor.getCount()>0){
            do{
                String subjectName=cursor.getString(0);
                String  yearName=cursor.getString(1);
                String sectionName=cursor.getString(2);
                SubjectModel subjectModel = new SubjectModel(subjectName,yearName,sectionName);
                arrayList.add(subjectModel);
            }while (cursor.moveToNext());
        }
        return  arrayList;
    }
    public ArrayList<AttendenceListModel> getAttendenceList(String subjectName,String yearName,String sectionName,String date){
        Cursor cursor= dbr.rawQuery("select pinNo,sNo,id,"+date+" from "+TABLE_NAME+" where subjectName=? and yearName=?  and sectionName=?",new String[] {subjectName,yearName,sectionName});
        ArrayList<AttendenceListModel> arrayList = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount()>0){
            do{
                int id = cursor.getInt(2);
                String pinNO=cursor.getString(0);
                String sno=cursor.getString(1);
                int attendenceValue =cursor.getInt(3);
                AttendenceListModel attendenceListModel = new AttendenceListModel(pinNO,sno,id,attendenceValue);
                arrayList.add(attendenceListModel);
            }while (cursor.moveToNext());
        }
        return  arrayList;
    }
    public void inserDate(String validColumnName) {
        dbw.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + validColumnName + " BOOLEAN default 0 ");
    }
    public void inserDateValue(int id,String validColumnName,Boolean value) {
        ContentValues cv = new ContentValues();
        cv.put(validColumnName,value);
        dbw.update(TABLE_NAME,cv,"id="+id,null);
    }
    public  boolean checkColumnName(String columnName){
        Cursor cursor = dbw.rawQuery("PRAGMA table_info(" + TABLE_NAME + ")", null);
        boolean columnExists = false;
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String columnNameInTable = cursor.getString(1); // Index 1: column name
            if (columnNameInTable.equals(columnName)) {
                columnExists = true;
                break;
            }
        }
        cursor.close();
        if (columnExists) {
            return true;
            // Column exists in the table
        } else {
            return  false;
            // Column not found
        }

    }
}
