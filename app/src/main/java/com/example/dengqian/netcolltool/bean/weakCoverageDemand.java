package com.example.dengqian.netcolltool.bean;

import android.widget.EditText;

public class weakCoverageDemand {


    private String weakCollID;
    private String demandID;
    private String preStName;
    private String stAddress;
    private String netModel;
    private String stPrope;
    private String buildType;
    private String reqCellNum;
    private String isPass;
    private String personCharge;
    private String personTel;
    private String remark;
    private String confirm_eci;
    private String confirm_tac;
    private String confirm_bsss;
    private String confirm_networktype;
    private String confirm_lon;
    private String confirm_lat;

    public void setConfirm_networktype(String confirm_networktype) {
        this.confirm_networktype = confirm_networktype;
    }

    public String getConfirm_networktype() {
        return confirm_networktype;
    }

    public void setWeakCollID(String weakCollID) {
        this.weakCollID = weakCollID;
    }

    public void setDemandID(String demandID) {
        this.demandID = demandID;
    }

    public void setPreStName(String preStName) {
        this.preStName = preStName;
    }

    public void setStAddress(String stAddress) {
        this.stAddress = stAddress;
    }

    public void setNetModel(String netModel) {
        this.netModel = netModel;
    }

    public void setStPrope(String stPrope) {
        this.stPrope = stPrope;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public void setReqCellNum(String reqCellNum) {
        this.reqCellNum = reqCellNum;
    }

    public void setIsPass(String isPass) {
        this.isPass = isPass;
    }

    public void setPersonCharge(String personCharge) {
        this.personCharge = personCharge;
    }

    public void setPersonTel(String personTel) {
        this.personTel = personTel;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setConfirm_eci(String confirm_eci) {
        this.confirm_eci = confirm_eci;
    }

    public void setConfirm_tac(String confirm_tac) {
        this.confirm_tac = confirm_tac;
    }

    public void setConfirm_bsss(String confirm_bsss) {
        this.confirm_bsss = confirm_bsss;
    }

    public void setConfirm_lon(String confirm_lon) {
        this.confirm_lon = confirm_lon;
    }

    public void setConfirm_lat(String confirm_lat) {
        this.confirm_lat = confirm_lat;
    }

    public String getWeakCollID() {
        return weakCollID;
    }

    public String getDemandID() {
        return demandID;
    }

    public String getPreStName() {
        return preStName;
    }

    public String getStAddress() {
        return stAddress;
    }

    public String getNetModel() {
        return netModel;
    }

    public String getStPrope() {
        return stPrope;
    }

    public String getBuildType() {
        return buildType;
    }

    public String getReqCellNum() {
        return reqCellNum;
    }

    public String getIsPass() {
        return isPass;
    }

    public String getPersonCharge() {
        return personCharge;
    }

    public String getPersonTel() {
        return personTel;
    }

    public String getRemark() {
        return remark;
    }

    public String getConfirm_eci() {
        return confirm_eci;
    }

    public String getConfirm_tac() {
        return confirm_tac;
    }

    public String getConfirm_bsss() {
        return confirm_bsss;
    }

    public String getConfirm_lon() {
        return confirm_lon;
    }

    public String getConfirm_lat() {
        return confirm_lat;
    }

    public boolean checkData(){
        boolean flag=true;
        flag=!("".equals(preStName))&&
                !("".equals(stAddress))&&
                !("".equals(netModel))&&
                !("".equals(stPrope))&&
                !("".equals(buildType))&&
                !("".equals(reqCellNum))&&
                !("".equals(isPass))&&
                !("".equals(personCharge))&&
                !("".equals(personTel))&&
                !("".equals(remark));
        flag=!(netModel.equals("选择网络制式"))&&
        !(stPrope.equals("选择站点属性"))&&
        !(buildType.equals("选择建设类型"))&&
        !(isPass.equals("是否通过联席会"));
        try{
            int d=Integer.valueOf(reqCellNum);
        }catch(Exception e){
            flag=false;
        }


        return flag;
    }
}
