package com.example.dengqian.netcolltool.bean;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dengqian on 2018/3/14.
 */

public class information {

    //记录ID
    private String ID="";

    // LAC，Location Area Code，位置区域码；
    private String TAC="";

    //ID，cellID，基站编号；
    private String ECI="";

    //BSSS，Base station signal strength，基站信号强度。
    private int BSSS;

    //当前GPS定位
    private String GPS="";

    //手机号码
    private String phoneNumber="";

    //手机品牌型号
    private String phoneType="";

    //覆盖场景
    private String overlayScene="";

    //采集时间
    private String collTime="";

    //是否上传
    private String isUpload="";


    private String district="";

    private String address="";

    private String NetworkOperatorName="";

    private String solveStatus="";

    private String solveTime="";


    public information(String ID, String TAC, String ECI, int BSSS, String GPS, String phoneNumber, String phoneType, String overlayScene, String collTime, String isUpload,String NetworkOperatorName) {
        this.ID = ID;
        this.TAC = TAC;
        this.ECI = ECI;
        this.BSSS = BSSS;
        this.GPS = GPS;
        this.phoneNumber = phoneNumber;
        this.phoneType = phoneType;
        this.overlayScene = overlayScene;
        this.collTime = collTime;
        this.isUpload = isUpload;
        this.NetworkOperatorName=NetworkOperatorName;
    }
    public information(information in) {
        this.ID=in.getID();
        this.TAC = in.getTAC();
        this.ECI = in.getECI();
        this.BSSS = in.getBSSS();
        this.GPS = in.getGPS();
        this.phoneNumber = in.getPhoneNumber();
        this.phoneType = in.getPhoneType();
        this.overlayScene = in.getOverlayScene();
        this.collTime = in.collTime;
        this.isUpload = in.getIsUpload();
        this.NetworkOperatorName=in.getNetworkOperatorName();
        this.district=in.getDistrict();
        this.address=in.getAddress();
        this.solveStatus=in.getSolveStatus();
        this.solveTime=in.getSolveTime();
    }

    public information(){

    }

    public void setSolveStatus(String solveStatus) {
        this.solveStatus = solveStatus;
    }

    public void setSolveTime(String solveTime) {
        this.solveTime = solveTime;
    }

    public String getSolveStatus() {
        return solveStatus;
    }

    public String getSolveTime() {
        return solveTime;
    }

    public String getNetworkOperatorName() {
        return NetworkOperatorName;
    }

    public void setNetworkOperatorName(String networkOperatorName) {
        NetworkOperatorName = networkOperatorName;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public String getAddress() {
        return address;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setTAC(String TAC) {
        this.TAC = TAC;
    }

    public void setECI(String ECI) {
        this.ECI = ECI;
    }

    public void setBSSS(int BSSS) {
        this.BSSS = BSSS;
    }

    public void setGPS(String GPS) {
        this.GPS = GPS;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public void setOverlayScene(String overlayScene) {
        this.overlayScene = overlayScene;
    }

    public void setCollTime(String collTime) {
        this.collTime = collTime;
    }

    public void setIsUpload(String isUpload) {
        this.isUpload = isUpload;
    }

    public String getID() {
        return ID;
    }

    public String getTAC() {
        return TAC;
    }

    public String getECI() {
        return ECI;
    }

    public int getBSSS() {
        return BSSS;
    }

    public String getGPS() {
        if(this.GPS.equals("")){
            GPS="No Gps";
        }else{
            String pattern = "\\d+\\.\\d{0,4}";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(GPS);
            List<String> arr=new ArrayList<>();
            while (m.find()){
                System.out.println(m.group());
                arr.add(m.group());
            }
            if(arr.size()==2){
                GPS="("+arr.get(0)+","+arr.get(1)+")";
            }
        }
        return GPS;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public String getOverlayScene() {
        return overlayScene;
    }

    public String getCollTime() {
        return collTime;
    }

    public String getIsUpload() {
        return isUpload;
    }


    /**
     * 对本类的数据进行非空检查
     * @return
     */
    public boolean checkData(){
        boolean flag=false;
        if(!this.getID().equals("")&&
                !this.getIsUpload().equals("")&&
                !this.getCollTime().equals("")&&
                !this.getNetworkOperatorName().equals("")&&
                !this.getAddress().equals("")&&
                !this.getDistrict().equals("")&&
                !this.getOverlayScene().equals("")&&
                this.getBSSS()!=0&&
                !this.getECI().equals("")&&
                !this.getGPS().equals("")&&
                !this.getPhoneNumber().equals("")&&
                !this.getPhoneType().equals("")&&
                !this.getTAC().equals(""))
            flag=true;

        return flag;
    }

    /**
     * 调试程序时使用，可查看该对象的详细情况
     * @return
     */
    public String show(){
        StringBuffer sb=new StringBuffer();
        sb.append("---------------------------------\n");
        sb.append("ID:"+this.getID()+"\n");
        sb.append("TAC:"+this.getTAC()+"\n");
        sb.append("ECI:"+this.getECI()+"\n");
        sb.append("GPS:"+this.getGPS()+"\n");
        sb.append("信号强度:"+this.getBSSS()+"\n");
        sb.append("手机类型:"+this.getPhoneType()+"\n");
        sb.append("手机号码:"+this.getPhoneNumber()+"\n");
        sb.append("覆盖场景:"+this.getOverlayScene()+"\n");
        sb.append("区县:"+this.getDistrict()+"\n");
        sb.append("地址:"+this.getAddress()+"\n");
        sb.append("运营商信息:"+this.getNetworkOperatorName()+"\n");
        sb.append("是否上传:"+(this.getIsUpload().equals("0")?"未上传":"已上传")+"\n");
        sb.append("-----------------------------------------");
        return sb.toString();
    }

}