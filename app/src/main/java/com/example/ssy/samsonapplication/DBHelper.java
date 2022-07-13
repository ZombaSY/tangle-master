package com.example.ssy.samsonapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DBHelper {
    myDbHelper myhelper;

    public DBHelper(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    public long addData(String date)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.CALENDAR_DATE, date);

        long id = db.insert(myDbHelper.TABLE_NAME, null , contentValues);

        return id;
    }

    public void insertData(String[] data, String date)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = getColunms();
        String[] whereArgs = {date};
        ContentValues contentValues = new ContentValues();
        Cursor cursor =db.query(myDbHelper.TABLE_NAME, columns,
                null,null,null,null,null);

        for(int i=1; i<cursor.getColumnCount(); i++){ // 주키는 바뀌지 않게 인덱스 1부터 시작
            if(data[i] != null)
                contentValues.put(cursor.getColumnName(i), data[i]);
            //ContentValues 에는 무조건 String 이 들어가야함
        }
        db.update(myDbHelper.TABLE_NAME, contentValues ,
                myDbHelper.CALENDAR_DATE+" = ?", whereArgs);
    }

    public String getUserData(String date)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = getColunms();
        StringBuffer buffer= new StringBuffer();
        String sql = "select * from " +myDbHelper.TABLE_NAME+ " " +
                "where " + myDbHelper.CALENDAR_DATE  + "="+date+";";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        for(int i=0; i<columns.length; i++){
            buffer.append(cursor.getString(cursor.getColumnIndex(columns[i]))+"\t");
        }
        buffer.append("\n");
        return buffer.toString();
    }

    public String getTableData()
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = getColunms();

        Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,
                null,null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            for(int i=0; i<columns.length; i++){
                buffer.append(cursor.getString(cursor.getColumnIndex(columns[i]))+"\t");
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public int getColumnNum(){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = getColunms();
        Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,
                null,null,null,null,null);
        return cursor.getColumnCount();
    }


    public String[] getColunms(){
        String[] columns = {myDbHelper.CALENDAR_DATE, myDbHelper.MEMO,myDbHelper.THICKNESS,
                myDbHelper.DENSITY, myDbHelper.YELLOW_DOT, myDbHelper.RED_DOT};

        return columns;
    }

    public int getUniqueNumber(int cursor_pos){

        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.CALENDAR_DATE};
        Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,
                null,null,null,null,null);
        cursor.moveToPosition(cursor_pos);

        return Integer.parseInt(cursor.getString(0));
    }

    public boolean availableDate(String date){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.CALENDAR_DATE};

        String sql = "select * from " +myDbHelper.TABLE_NAME+ " " +
                "where " + myDbHelper.CALENDAR_DATE  + "="+date+";";
        Cursor cursor =db.rawQuery(sql, null);

        while (cursor.moveToNext()){
        }

        if(cursor.getPosition() >= 1)
            return false;
        else
            return true;
    }


    public  int delete(String date)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={date};
        int count =db.delete(myDbHelper.TABLE_NAME,
                myDbHelper.CALENDAR_DATE+" = ?",whereArgs);
        return  count;
    }


    private static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "UserInfo";    // Database Name
        private static final String TABLE_NAME = "CalendarTable";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version
        private static final String CALENDAR_DATE="CalendarDate";     // Column I (Primary Key)
        private static final String MEMO = "Memo";    //Column II
        private static final String THICKNESS = "Thickness";
        private static final String DENSITY = "Density";
        private static final String YELLOW_DOT = "YellowDot";
        private static final String RED_DOT = "RedDot";
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+CALENDAR_DATE+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ""+MEMO+" VARCHAR(1024) ," +
                ""+THICKNESS+" FLOAT , " +
                ""+DENSITY+" FLOAT , " +
                ""+YELLOW_DOT+" INTEGER , " +
                ""+RED_DOT+" INTEGER );";
        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;
        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                Toast.makeText(context,""+e, Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Toast.makeText(context,"onUpgrade", Toast.LENGTH_SHORT).show();
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {
                Toast.makeText(context,""+e, Toast.LENGTH_SHORT).show();
            }
        }
    }
}