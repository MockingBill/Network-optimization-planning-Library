package com.example.dengqian.netcolltool.bean;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import java.util.regex.*;

public class BaiDuLoactionDeal {
    public static String getRequest(String lon,String lat) throws Exception {
        String surl="http://api.map.baidu.com/geocoder/v2/?location="+lat+","+lon+"&output=json&pois=1&ak=y5pwBnfPdgA6dbN6ws0i34Hefk0zD35v";
        URL url = new URL(surl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3000);
        //这是请求方式为POST
        conn.setRequestMethod("GET");
        //设置post请求必要的请求头
        conn.setRequestProperty("Content-Type", "application/json"); // 请求头, 必须设置
        conn.setDoOutput(true); // 准备写出
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


    public static String formatAddress(String resultData) throws Exception{
        JSONObject jsonObject=new JSONObject(resultData);
        JSONObject formatted_address=new JSONObject(jsonObject.get("result").toString());
        return formatted_address.get("formatted_address").toString();
    }

}
