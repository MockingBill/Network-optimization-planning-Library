package com.example.dengqian.netcolltool.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * android本地数据操作工具类
 * Created by dengqian on 2018/3/14.
 */

public class informDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NetWorkInfColl";

    public informDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    String sql="CREATE TABLE IF NOT EXISTS 'NetWorkInfor' (" +
            "              'ID' varchar(50) NOT NULL ," +
            "              'TAC' varchar(20) DEFAULT NULL ," +
            "              'ECI' varchar(50) DEFAULT NULL ," +
            "              'BSSS' varchar(20) DEFAULT NULL ," +
            "              'GPS' varchar(50) DEFAULT NULL ," +
            "              'phoneNumber' varchar(80) DEFAULT NULL ," +
            "              'phoneType' varchar(50) DEFAULT NULL ," +
            "              'overlayScene' varchar(20) DEFAULT NULL ," +
            "              'collTime' varchar(32) DEFAULT NULL ," +
            "              'isUpload'int(4) DEFAULT NULL ," +
            "              'district' varchar(10) DEFAULT NULL ," +
            "              'address' varchar(80) DEFAULT NULL ," +
            "              'NetworkOperatorName' varchar(30) DEFAULT NULL ," +
            "              PRIMARY KEY ('ID')" +
            "            );";
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("");
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }




    public List<information> query(SQLiteDatabase db, String sql,String []selection){
        List<information> infoList = new ArrayList<information>();
        Log.d("sql",sql);
        information info=new information();;
        try{
            Cursor cursor=db.rawQuery(sql,selection);
            while (cursor.moveToNext()) {
                info=new information();
                 info.setID(cursor.getString(cursor.getColumnIndex("ID")));
                 info.setTAC(cursor.getString(cursor.getColumnIndex("TAC")));
                 info.setECI(cursor.getString(cursor.getColumnIndex("ECI")));
                 info.setBSSS(cursor.getInt(cursor.getColumnIndex("BSSS")));
                 info.setGPS(cursor.getString(cursor.getColumnIndex("GPS")));
                 info.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
                 info.setPhoneType(cursor.getString(cursor.getColumnIndex("phoneType")));
                 info.setOverlayScene(cursor.getString(cursor.getColumnIndex("overlayScene")));
                 info.setCollTime(cursor.getString(cursor.getColumnIndex("collTime")));
                 info.setIsUpload(cursor.getString(cursor.getColumnIndex("isUpload")));
                 info.setDistrict(cursor.getString(cursor.getColumnIndex("district")));
                 info.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                 info.setNetworkOperatorName(cursor.getString(cursor.getColumnIndex("NetworkOperatorName")));
                infoList.add(info);
            }
            return infoList;
        }catch(Exception e){
            Log.e("",e.toString());
            return infoList;
        }

    }


    public void delete(SQLiteDatabase db,String id,Context context){
        String sql="DELETE FROM 'NetWorkInfor' WHERE ID='"+id+"'";
        db.execSQL(sql);
    }

    public void updateIsUpload(SQLiteDatabase db,String id,Context context){
        String sql="UPDATE 'NetWorkInfor' set isUpload='1' where ID='"+id+"'";
        db.execSQL(sql);

    }



    public void updateListIsUpload(SQLiteDatabase db,List<String> idList,Context context){
        if(!idList.isEmpty()){
            StringBuffer sql=new StringBuffer("UPDATE 'NetWorkInfor' set isUpload=1 where ID='"+idList.get(0)+"'");
            for(String x:idList)
                sql.append(" or ID='"+x+"'");
            Log.d("sql",sql.toString());
            db.execSQL(sql.toString());
        }
    }


    public void save(SQLiteDatabase db,information info,Context context){
        String sql="INSERT INTO 'NetWorkInfor' (ID,TAC,ECI,BSSS,GPS,phoneNumber,phoneType,overlayScene,collTime,isUpload,district,address,NetworkOperatorName) VALUES " +
        "('"+info.getID()+"', " +
        "'"+info.getTAC()+"', " +
        "'"+info.getECI()+"', " +
        "'"+info.getBSSS()+"', " +
        "'"+info.getGPS()+"', " +
        "'"+info.getPhoneNumber()+"', " +
        "'"+info.getPhoneType()+"', " +
        "'"+info.getOverlayScene()+"', " +
        "'"+info.getCollTime()+"', " +
        "0, " +
        "'"+info.getDistrict()+"', " +
        "'"+info.getAddress()+"',"+
        "'"+info.getNetworkOperatorName()+"');";
        try{
            db.execSQL(sql);
        }catch(Exception e){
            Log.e("",e.toString());
        }
    }



}

