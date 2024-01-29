package com.vv.collegeattendence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataBase extends SQLiteOpenHelper {
    SQLiteDatabase dbw = this.getWritableDatabase();
    SQLiteDatabase dbr = this.getReadableDatabase();
    public  static  String ID="id";
    public static String SNO="sNo";
    public static String STUDENT_NAME="studentName";
    public static String PIN_NO = "pinNo";
    Context context1;
    public DataBase(@Nullable Context context) {
        super(context, "SubjectDB", null, 1);
        this.context1=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    public  void createTable(String TABLE_NAME){
        dbw.execSQL("create table  "+TABLE_NAME+ "( "+ ID+" integer primary key autoincrement , "+SNO+" text,"+STUDENT_NAME+" text,"+PIN_NO+" text)");
    }
    public  boolean isTableExist(String TABLE_NAME){
        Cursor cursor = dbw.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?",new String[] {TABLE_NAME} );
        if(cursor.getCount()==1)
            return true;
        else
            return false;
    }
    public boolean insertSubject (String TABLE_NAME,String sNo,String studentName,String pinNo){
        ContentValues cv = new ContentValues();
        cv.put(SNO,sNo);
        cv.put(STUDENT_NAME,studentName);
        cv.put(PIN_NO,pinNo);
        long flag= dbw.insert(TABLE_NAME,null,cv);
        if(flag!=-1)
            return true;
        else
            return false;
    }
   public ArrayList<SubjectModel> getSubjects(){
        ArrayList<SubjectModel> arrayList = new ArrayList<>();
       Cursor cursor = dbw.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        cursor.moveToFirst();
        if(cursor.getCount()>1){
            do{
                String TABLE_NAME=cursor.getString(0);
                if (!(TABLE_NAME.equals("sqlite_sequence") || TABLE_NAME.equals("android_metadata"))){
                        String[] TableNameSplit = TABLE_NAME.split("_");
                        String subject= TableNameSplit[0];
                        String semister= TableNameSplit[1];
                        String section= TableNameSplit[2];
                    SubjectModel subjectModel = new SubjectModel(subject,semister,section);
                    arrayList.add(subjectModel);
                }
            }while (cursor.moveToNext());
        }
        return  arrayList;
    }
   public ArrayList<AttendenceListModel> getAttendenceList(String TABLE_NAME,String date){
        Cursor cursor= dbr.rawQuery("select pinNo,sNo,id,"+date+" ,"+STUDENT_NAME+" from "+TABLE_NAME,null);
        ArrayList<AttendenceListModel> arrayList = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount()>0){
            do{
                int id = cursor.getInt(2);
                String pinNO=cursor.getString(0);
                String sno=cursor.getString(1);
                int attendenceValue =cursor.getInt(3);
                String name = cursor.getString(4);
                AttendenceListModel attendenceListModel = new AttendenceListModel(name,pinNO,sno,id,attendenceValue);
                arrayList.add(attendenceListModel);
            }while (cursor.moveToNext());
        }
        return  arrayList;
    }
  public void inserDate(String TABLE_NAME,String validColumnName) {
        dbw.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + validColumnName + " BOOLEAN default 0 ");
    }
    public void inserDateValue(String TABLE_NAME,String validColumnName,int id,Boolean value) {
        ContentValues cv = new ContentValues();
        cv.put(validColumnName,value);
        dbw.update(TABLE_NAME,cv,"id="+id,null);
    }
    public  boolean checkColumnName(String TABLE_NAME,String columnName){
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
    public void deleteTable(String TABLE_NAME){
        dbw.execSQL("drop table "+TABLE_NAME);
    }
    public  ArrayList<StringBuffer> downloadData(String TABLE_NAME , String startDate,String endDate){
        ArrayList<StringBuffer> studentList = new ArrayList<>();
        int check=0;
        try{
            String columnNamesInDB=SNO+","+STUDENT_NAME+","+PIN_NO;
            ArrayList<String> dateList = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault());
            Date startDateObj,endDateObj;
            startDateObj= dateFormat.parse(startDate);
            endDateObj= dateFormat.parse(endDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDateObj);
            while (!calendar.getTime().after(endDateObj)) {
                dateList.add(dateFormat.format(calendar.getTime()));
                Log.d("datesdates", dateFormat.format(calendar.getTime()));
                calendar.add(Calendar.DATE, 1);
            }
            Cursor cursorColumnCheck = dbw.rawQuery("PRAGMA table_info(" + TABLE_NAME + ")", null);
            cursorColumnCheck.moveToFirst();
            if(cursorColumnCheck.getCount()>0){
                while (cursorColumnCheck.moveToNext()){
                    Log.d("datesdates",cursorColumnCheck.getString(1));
                    for (String str : dateList) {
                        if (cursorColumnCheck.getString(1).contains("_"+str)) {
                            columnNamesInDB+=","+cursorColumnCheck.getString(1);
                            check=1;
                        }
                    }
                }
                Log.d("columnNames12",columnNamesInDB);
                StringBuffer columnNames = new StringBuffer();
               columnNames.append(columnNamesInDB);
                Log.d("columnNames12", String.valueOf(columnNames));
                studentList.add(columnNames);
                Cursor cursor = dbw.rawQuery("select "+columnNamesInDB+" from "+TABLE_NAME,null);
                cursor.moveToFirst();
                do {
                    StringBuffer str = new StringBuffer();
                    for (int i=0;(i<cursor.getColumnCount()-1);i++){
                        if(i>2){
                            if(cursor.getString(i).equals("1")){
                                str.append("present,");
                            }
                            else if(cursor.getString(i).equals("0")) {
                                str.append("absent,");
                            }
                            else {
                                str.append(cursor.getString(i)).append(",");
                            }
                        }
                        else {
                            str.append(cursor.getString(i)).append(",");
                        }
                    }
                    studentList.add(str);
                    Log.d("columnCount",str.toString());
                }while (cursor.moveToNext());
            }

        }catch (Exception ex){

        }
        if(check!=0)
        return studentList;
        else
            return  new ArrayList<StringBuffer>();
    }
    public  ArrayList<AttendencePerDayModel> checkColumnList(String TABLE_NAME,String date){
        ArrayList<AttendencePerDayModel> columns= new ArrayList<>();
        try{
           Cursor cursor = dbw.rawQuery("PRAGMA table_info(" + TABLE_NAME + ")", null);
           cursor.moveToFirst();
           if(cursor.getCount()>0){
               while (cursor.moveToNext()) {
                   String columnNameInTable = cursor.getString(1); // Index 1: column name
                   if (columnNameInTable.contains(date)) {
                       String timeForDB_=columnNameInTable.substring(11,20)+"-"+columnNameInTable.substring(21);
                       String timeForDB=timeForDB_.replace("-","_");
                       String timeForDisplay=timeForDB_.replace("_"," ");
                       columns.add(new AttendencePerDayModel(timeForDisplay,timeForDB));
                   }
               }
           }
           cursor.close();
       }
       catch (Exception ex){

           Toast.makeText(context1, ex.toString(), Toast.LENGTH_SHORT).show();

       }
        return columns;
    }
    public void changeTableName(String OLD_TABLE_NAME , String NEW_TABLE_NAME){
        dbw.execSQL("create table "+NEW_TABLE_NAME+" AS select * from "+OLD_TABLE_NAME);
        dbw.execSQL("drop table if exists "+OLD_TABLE_NAME);
    }
    public boolean checkStudentPinNO(String TABLE_NAME,String pinNO){
         Cursor cursor= dbw.rawQuery("select * from "+TABLE_NAME+" where "+PIN_NO+" = ?",new String[] {pinNO});
         if(cursor.getCount()==1)
             return true;
         else
             return false;



    }
}
