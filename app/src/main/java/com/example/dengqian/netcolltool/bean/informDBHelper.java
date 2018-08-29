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

    String sql="CREATE TABLE IF NOT EXISTS 'bu_weak_coverage_demand' (\n" +
            "'weakCollID' varchar(50) DEFAULT NULL,\n" +
            "'demandID' varchar(50) DEFAULT NULL,\n" +
            "'preStName' varchar(30) DEFAULT NULL,\n" +
            "'stAddress' varchar(50) DEFAULT NULL,\n" +
            "'netModel' varchar(32) DEFAULT NULL,\n" +
            "'stPrope' varchar(32) DEFAULT NULL,\n" +
            "'buildType' varchar(32) DEFAULT NULL,\n" +
            "'reqCellNum' varchar(32) DEFAULT NULL,\n" +
            "'isPass' varchar(32) DEFAULT NULL,\n" +
            "'personCharge' varchar(32) DEFAULT NULL,\n" +
            "'personTel' varchar(32) DEFAULT NULL,\n" +
            "'remark' varchar(500) DEFAULT NULL,\n" +
            "'confirm_eci' varchar(32) DEFAULT NULL,\n" +
            "'confirm_tac' varchar(32) DEFAULT NULL,\n" +
            "'confirm_bsss' varchar(32) DEFAULT NULL,\n" +
            "'confirm_networktype' varchar(32) DEFAULT NULL,\n" +
            "'confirm_lon' varchar(32) DEFAULT NULL,\n" +
            "'confirm_lat' varchar(32) DEFAULT NULL\n" +
            ");";
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

