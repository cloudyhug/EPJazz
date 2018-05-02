package com.example.charl.jazz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Created by charl on 08/12/2017.
 */

public class MaBd extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Jazz.db";
    public static final String TABLE_NAME = "Jazz_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TITLE";
    public static final String COL_3 = "ARTIST";
    public static final String COL_4 = "FICHIER";


    public MaBd(Context context) {
        super(context,DATABASE_NAME,null, 1);
        SQLiteDatabase db = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, ARTIST TEXT, FICHIER TEXT) ");
        db.execSQL("INSERT INTO "+TABLE_NAME+" VALUES (1,'All Of Me','Gerald Marks','all_of_me');");
        db.execSQL("INSERT INTO "+TABLE_NAME+" VALUES (2,'Autumn Leaves','Miles Davis','autumn_leaves');");
        db.execSQL("INSERT INTO "+TABLE_NAME+" VALUES (3,'Caravan','Duke Ellington','caravan');");
        db.execSQL("INSERT INTO "+TABLE_NAME+" VALUES (4,'Minor Swing','Django Reinhardt','minor_swing');");
        db.execSQL("INSERT INTO "+TABLE_NAME+" VALUES (5,'Blues en Fa','Unknown','blues_en_fa');");
        db.execSQL("INSERT INTO "+TABLE_NAME+" VALUES (6,'Stand By Me','Ben E.King','stand_by_me');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }

    public boolean insertData(String title, String artist, String fichier){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_2,title);
        contentValues.put(COL_3,artist);
        contentValues.put(COL_4,fichier);

        long result = db.insert(TABLE_NAME,null,contentValues);
        if (result==-1){
            return false;
        }else{
            return true;
        }
    }


    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from  "+TABLE_NAME,null);
        return res;
    }


    public boolean updateData(String id,String title, String artist,String fichier){//, String surname,String marks
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,title);
        contentValues.put(COL_3,artist);
        contentValues.put(COL_4,fichier);
        db.update(TABLE_NAME,contentValues,"ID=?",new String[] {id});
        return true;

    }

    public Integer deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }

    public Cursor getOneData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from  "+TABLE_NAME+" where "+TABLE_NAME+".ID="+id,null);
        return res;
    }

}













































































