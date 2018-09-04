package com.example.dengqian.netcolltool.bean;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.LogWriter;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * android本地数据操作工具类
 * Created by dengqian on 2018/3/14.
 */

public class informDBHelperForWeakConfirm extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NetWorkInfColl";

    public informDBHelperForWeakConfirm(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }




    String sql="CREATE TABLE IF NOT EXISTS 'bu_weak_coverage_demand' (" +
            "'weakCollID' varchar(50) DEFAULT NULL," +
            "'weakAddress' varchar(50) DEFAULT NULL," +
            "'demandID' varchar(50) DEFAULT NULL," +
            "'preStName' varchar(30) DEFAULT NULL," +
            "'stAddress' varchar(50) DEFAULT NULL," +
            "'netModel' varchar(32) DEFAULT NULL," +
            "'stPrope' varchar(32) DEFAULT NULL," +
            "'buildType' varchar(32) DEFAULT NULL," +
            "'reqCellNum' varchar(32) DEFAULT NULL," +
            "'isPass' varchar(32) DEFAULT NULL," +
            "'personCharge' varchar(32) DEFAULT NULL," +
            "'personTel' varchar(32) DEFAULT NULL," +
            "'remark' varchar(500) DEFAULT NULL," +
            "'confirm_eci' varchar(32) DEFAULT NULL," +
            "'confirm_tac' varchar(32) DEFAULT NULL," +
            "'confirm_bsss' varchar(32) DEFAULT NULL," +
            "'confirm_networktype' varchar(32) DEFAULT NULL," +
            "'confirm_lon' varchar(32) DEFAULT NULL," +
            "'confirm_lat' varchar(32) DEFAULT NULL," +
            "'isUpload' varchar(4) DEFAULT '0'" +
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


    public void checkTable(SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table';", null);
        Map<String,String> map=new HashMap<>();

        while(cursor.moveToNext()){
            //遍历出表名
            String name = cursor.getString(0);
            map.put(name,name);

        }
        if(!"bu_weak_coverage_demand".equals(map.get("bu_weak_coverage_demand"))){
            onCreate(db);
        }



    }


    public List<weakCoverageDemand> query(SQLiteDatabase db, String sql,String []selection){
        List<weakCoverageDemand> infoList = new ArrayList<weakCoverageDemand>();
        Log.d("sql",sql);
        weakCoverageDemand info=new weakCoverageDemand();
        try{
            Cursor cursor=db.rawQuery(sql,selection);
            while (cursor.moveToNext()) {
                info=new weakCoverageDemand();
                 info.setWeakCollID(cursor.getString(cursor.getColumnIndex("weakCollID")));
                 info.setWeakAddress(cursor.getString(cursor.getColumnIndex("weakAddress")));
                 info.setDemandID(cursor.getString(cursor.getColumnIndex("demandID")));
                 info.setNetModel(cursor.getString(cursor.getColumnIndex("netModel")));
                 info.setIsPass(cursor.getString(cursor.getColumnIndex("isPass")));
                 info.setBuildType(cursor.getString(cursor.getColumnIndex("buildType")));
                 info.setStPrope(cursor.getString(cursor.getColumnIndex("stPrope")));
                 info.setPreStName(cursor.getString(cursor.getColumnIndex("preStName")));
                 info.setStAddress(cursor.getString(cursor.getColumnIndex("stAddress")));
                 info.setReqCellNum(cursor.getString(cursor.getColumnIndex("reqCellNum")));
                 info.setPersonCharge(cursor.getString(cursor.getColumnIndex("personCharge")));
                 info.setPersonTel(cursor.getString(cursor.getColumnIndex("personTel")));
                 info.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
                info.setConfirm_eci(cursor.getString(cursor.getColumnIndex("confirm_eci")));
                info.setConfirm_tac(cursor.getString(cursor.getColumnIndex("confirm_tac")));
                info.setConfirm_bsss(cursor.getString(cursor.getColumnIndex("confirm_bsss")));
                info.setConfirm_networktype(cursor.getString(cursor.getColumnIndex("confirm_networktype")));
                info.setConfirm_lon(cursor.getString(cursor.getColumnIndex("confirm_lon")));
                info.setConfirm_lat(cursor.getString(cursor.getColumnIndex("confirm_lat")));
                infoList.add(info);
            }
            return infoList;
        }catch(Exception e){
            Log.e("",e.toString());
            return infoList;
        }

    }


    public void delete(SQLiteDatabase db,String id,Context context){
        String sql="DELETE FROM 'bu_weak_coverage_demand' WHERE weakCollID='"+id+"'";
        db.execSQL(sql);
    }

    public boolean save(SQLiteDatabase db, weakCoverageDemand info, Context context, Activity activity){
        try{
            String sql="INSERT INTO 'bu_weak_coverage_demand' (weakCollID,weakAddress,demandID,preStName,stAddress,netModel,stPrope,buildType,reqCellNum,isPass,personCharge,personTel,remark,confirm_eci,confirm_tac,confirm_bsss,confirm_networktype,confirm_lon,confirm_lat) VALUES " +
                    "('"+info.getWeakCollID()+"', " +
                    "'"+info.getWeakAddress()+"', " +
                    "'"+info.getDemandID()+"', " +
                    "'"+info.getPreStName()+"', " +
                    "'"+info.getStAddress()+"', " +
                    "'"+info.getNetModel()+"', " +
                    "'"+info.getStPrope()+"', " +
                    "'"+info.getBuildType()+"', " +
                    "'"+info.getReqCellNum()+"', " +
                    "'"+info.getIsPass()+"', " +
                    "'"+info.getPersonCharge()+"', " +
                    "'"+info.getPersonTel()+"', " +
                    "'"+info.getRemark()+"',"+
                    "'"+info.getConfirm_eci()+"',"+
                    "'"+info.getConfirm_tac()+"',"+
                    "'"+info.getConfirm_bsss()+"',"+
                    "'"+info.getConfirm_networktype()+"',"+
                    "'"+info.getConfirm_lon()+"',"+
                    "'"+info.getConfirm_lat()+"');";
            Log.e("demandSql:",sql);

        db.execSQL(sql);
        return true;
        }catch (Exception e){
            Log.e("demandSqlerror",e.toString());
            Toast.makeText(activity,e.toString() , Toast.LENGTH_LONG).show();
            return false;
        }
    }



}

