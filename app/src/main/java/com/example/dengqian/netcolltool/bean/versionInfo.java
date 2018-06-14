package com.example.dengqian.netcolltool.bean;

/**
 * Created by dengqian on 2018/6/13.
 */

public class versionInfo {
    private String appName;
    private String serverVersion;
    private String updateUrl;
    private String upgradeinfo;


    public versionInfo(Exception e){
        appName="";
        serverVersion="";
        updateUrl="";
        upgradeinfo="";
    }
    public versionInfo(){

    }

    public String show(){
        return appName+"\n"+serverVersion+"\n"+updateUrl+"\n"+upgradeinfo;
    }

    public String getAppName() {
        return appName;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public String getUpgradeinfo() {
        return upgradeinfo;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public void setUpgradeinfo(String upgradeinfo) {
        this.upgradeinfo = upgradeinfo;
    }
}
