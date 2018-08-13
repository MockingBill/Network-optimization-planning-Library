package com.example.dengqian.netcolltool.bean;

/**
 * {"ID":"4ae1247e-c661-42d3-a1a7-62501045dbef","address":"岑巩中学","collTime":"2018-06-06 11:45:43","GpsLon":108.833344,"GpsLat":27.186021,"ECI":"140885771","TAC":"34135","BSSS":-99,"city":"黔东南","collectUsername":"茹福永","phoneNumber":"13708558299","FromDepartment":"贵州/黔东南分公司/计划建设部","phoneType":"HUAWEI;ALP-TL00;866215032503481","overlayScene":"城区_学校","district":"岑巩县","NetworkOperatorName":"中国移动","netWorkType":"4G","solveStatus":"未处理","solveTime":"","createPersion":"app","createTime":"2018-06-06 11:45","alterpersion":"dengqian"}
 */


public class weakInformation {
    private String ID;
    private String address;
    private String collTime;
    private double GpsLon;
    private double GpsLat;
    private String ECI;
    private String TAC;
    private int BSSS;
    private String city;
    private String collectUsername;
    private String phoneNumber;
    private String FromDepartment;
    private String phoneType;
    private String overlayScene;
    private String district;
    private String NetworkOperatorName;
    private String netWorkType;
    private String solveStatus;
    private String solveTime;
    private String createPersion;
    private String createTime;
    private String alterpersion;
    private String alterTime;
    private String deleteFlag;
    private String dis;
    //是否上传
    private String isUpload="";



    public String show(){
        StringBuffer sb=new StringBuffer();
        sb.append("---------------------------------\n");
        sb.append("ID:"+this.getID()+"\n");
        sb.append("地址:"+this.getAddress()+"\n");
        sb.append("收集时间:"+this.getCollTime()+"\n");
        sb.append("GPS:"+this.getGpsLon()+","+this.getGpsLat()+"\n");
        sb.append("ECI:"+this.getECI()+"\n");
        sb.append("TAC:"+this.getTAC()+"\n");
        sb.append("信号强度:"+this.getBSSS()+"\n");
        sb.append("地市:"+this.getCity()+"\n");
        sb.append("采集用户名:"+this.getCollectUsername()+"\n");
        sb.append("手机号码:"+this.getPhoneNumber()+"\n");
        sb.append("部门:"+this.getFromDepartment()+"\n");
        sb.append("手机类型:"+this.getPhoneType()+"\n");
        sb.append("覆盖场景:"+this.getOverlayScene()+"\n");
        sb.append("区县:"+this.getDistrict()+"\n");
        sb.append("运营商信息:"+this.getNetworkOperatorName()+"\n");
        sb.append("网络制式:"+this.getNetWorkType()+"\n");
        sb.append("解决状态"+this.getSolveStatus()+"\n");
        sb.append("解决时间"+this.getSolveTime()+"\n");
        sb.append("距离"+this.getDis()+"\n");
        sb.append("是否上传:"+(this.getIsUpload().equals("0")?"未上传":"已上传")+"\n");
        sb.append("-----------------------------------------");
        return sb.toString();
    }

    public void setIsUpload(String isUpload) {
        this.isUpload = isUpload;
    }

    public String getIsUpload() {

        return isUpload;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCollTime(String collTime) {
        this.collTime = collTime;
    }

    public void setGpsLon(double gpsLon) {
        GpsLon = gpsLon;
    }

    public void setGpsLat(double gpsLat) {
        GpsLat = gpsLat;
    }

    public void setECI(String ECI) {
        this.ECI = ECI;
    }

    public void setTAC(String TAC) {
        this.TAC = TAC;
    }

    public void setBSSS(int BSSS) {
        this.BSSS = BSSS;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCollectUsername(String collectUsername) {
        this.collectUsername = collectUsername;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFromDepartment(String fromDepartment) {
        FromDepartment = fromDepartment;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public void setOverlayScene(String overlayScene) {
        this.overlayScene = overlayScene;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setNetworkOperatorName(String networkOperatorName) {
        NetworkOperatorName = networkOperatorName;
    }

    public void setNetWorkType(String netWorkType) {
        this.netWorkType = netWorkType;
    }

    public void setSolveStatus(String solveStatus) {
        this.solveStatus = solveStatus;
    }

    public void setSolveTime(String solveTime) {
        this.solveTime = solveTime;
    }

    public void setCreatePersion(String createPersion) {
        this.createPersion = createPersion;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setAlterpersion(String alterpersion) {
        this.alterpersion = alterpersion;
    }

    public void setAlterTime(String alterTime) {
        this.alterTime = alterTime;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public String getID() {
        return ID;
    }

    public String getAddress() {
        return address;
    }

    public String getCollTime() {
        return collTime;
    }

    public double getGpsLon() {
        return GpsLon;
    }

    public double getGpsLat() {
        return GpsLat;
    }

    public String getECI() {
        return ECI;
    }

    public String getTAC() {
        return TAC;
    }

    public int getBSSS() {
        return BSSS;
    }

    public String getCity() {
        return city;
    }

    public String getCollectUsername() {
        return collectUsername;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFromDepartment() {
        return FromDepartment;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public String getOverlayScene() {
        return overlayScene;
    }

    public String getDistrict() {
        return district;
    }

    public String getNetworkOperatorName() {
        return NetworkOperatorName;
    }

    public String getNetWorkType() {
        return netWorkType;
    }

    public String getSolveStatus() {
        return solveStatus;
    }

    public String getSolveTime() {
        return solveTime;
    }

    public String getCreatePersion() {
        return createPersion;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getAlterpersion() {
        return alterpersion;
    }

    public String getAlterTime() {
        return alterTime;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public String getDis() {
        return dis;
    }
}
