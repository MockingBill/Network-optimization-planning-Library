package com.example.dengqian.netcolltool.bean;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dengqian on 2018/3/27.
 */

public class connNetReq {
    /**
     * 将一个information转为json字符串
     * @param info
     * @return
     */

    public static String beanToJson(information info){
        JSONObject json =new JSONObject();
        try{
            json.put("TAC",info.getTAC());
            json.put("phoneType",info.getPhoneType());
            json.put("phoneNumber",info.getPhoneNumber());
            json.put("GPS",info.getGPS());
            json.put("ECI",info.getECI());
            json.put("BSSS",info.getBSSS());
            json.put("overlayScene",info.getOverlayScene());
            json.put("district",info.getDistrict());
            json.put("address",info.getAddress());
            json.put("NetworkOperatorName",info.getNetworkOperatorName());
            json.put("collTime",info.getCollTime());
            json.put("ID",info.getID());
            json.put("isUpload",info.getIsUpload());
            json.put("token",AesAndToken.md5());
            String JSONString="collInfo"+json.toString();
        }catch(Exception e){
            return "err";
        }

        return json.toString();
    }

    /**
     * 将一个List<information>转化为一个json数组字符串
     * @param list
     * @return
     */
    public static String beanToJson(List<information> list){
        JSONArray jsonArray=new JSONArray();
        try{
            for(information info: list){
                JSONObject json = new JSONObject();
                json.put("ID",info.getID());
                json.put("TAC",info.getTAC());
                json.put("phoneType",info.getPhoneType());
                json.put("phoneNumber",info.getPhoneNumber());
                json.put("GPS",info.getGPS());
                json.put("ECI",info.getECI());
                json.put("BSSS",info.getBSSS());
                json.put("overlayScene",info.getOverlayScene());
                json.put("district",info.getDistrict());
                json.put("address",info.getAddress());
                json.put("NetworkOperatorName",info.getNetworkOperatorName());
                json.put("collTime",info.getCollTime());
                json.put("isUpload",info.getIsUpload());
                json.put("token",AesAndToken.md5());
                jsonArray.put(json);
            }
        }catch(Exception e){
                return "err";
        }


        return jsonArray.toString();
    }


    /**
     * post发送一个http请求，该方法会抛出一个异常。
     * @param address
     * @param info
     * @return
     * @throws Exception
     */

    public static String post(String address,String info) throws Exception {

        //info = URLEncoder.encode(info);

        info=AesAndToken.encrypt(info,AesAndToken.KEY);
        byte[] data = info.getBytes();
        URL url = new URL(address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3000);
        //这是请求方式为POST
        conn.setRequestMethod("POST");
        //设置post请求必要的请求头

        conn.setRequestProperty("Authorization",AesAndToken.md5());
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // 请求头, 必须设置
        conn.setRequestProperty("Content-Length", String.valueOf(data.length)); // 注意是字节长度, 不是字符长度
        conn.setDoOutput(true); // 准备写出
        conn.getOutputStream().write(data); // 写出数据
        Log.w("resp：",conn.getResponseMessage());
        InputStream inptStream=conn.getInputStream();
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] dataReceive = new byte[1024];
        int len = 0;
        try {
            while((len = inptStream.read(dataReceive)) != -1) {
                byteArrayOutputStream.write(dataReceive, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());

        if(conn.getResponseCode() == 200)
            return resultData;
        else
            return "err";
    }

    /**
     * 将json数组转化为List<information>
     * @param str
     * @return
     */
    public static List<information> jsonToList(String str){
        List<information> list = new ArrayList<information>();
        try{
            JSONArray jsonArray=new JSONArray(str);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                information info=new information();
                info.setID(jsonObject.getString( "ID"));
                info.setAddress(jsonObject.getString( "address"));
                info.setBSSS(jsonObject.getInt( "BSSS"));
                info.setCollTime(jsonObject.getString( "collTime"));
                info.setDistrict(jsonObject.getString( "district"));
                info.setECI(jsonObject.getString( "ECI"));
                info.setGPS(jsonObject.getString( "GPS"));
                info.setNetworkOperatorName(jsonObject.getString( "NetworkOperatorName"));
                info.setOverlayScene(jsonObject.getString( "overlayScene"));
                info.setTAC(jsonObject.getString( "TAC"));
                info.setSolveStatus(jsonObject.getString( "solveStatus"));
                info.setSolveTime(jsonObject.getString( "solveTime"));
                info.setPhoneNumber(jsonObject.getString( "phoneNumber"));
                info.setPhoneType(jsonObject.getString( "phoneType"));
                list.add(info);
            }
        }catch(Exception e){
            Log.e("err","json to list fail\n"+e.toString());
            return list;
        }
        return list;
    }

    public static versionInfo jsonToVersionInfo(String str){
        versionInfo version=new versionInfo();
        try{
            JSONObject jsonObject=new JSONObject(str);
            version.setAppName(jsonObject.get("appName").toString());
            version.setServerVersion(jsonObject.get("serverVersion").toString());
            version.setUpdateUrl(jsonObject.get("updateUrl").toString());
            version.setUpgradeinfo(jsonObject.get("upgradeinfo").toString());
        }catch(Exception e){
            Log.e("jsonToVersionInfoError",e.toString());
            return new versionInfo(e);
        }


        return version;
    }









}
