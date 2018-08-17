package com.example.dengqian.netcolltool.bean;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private List<weakInformation> globalWeakList;

    public List<weakInformation> getGlobalWeakList() {
        return globalWeakList;
    }

    public void setGlobalWeakList(List<weakInformation> globalWeakList) {
        this.globalWeakList=new ArrayList<weakInformation>();
        for(weakInformation wi:globalWeakList){
            weakInformation wein=new weakInformation(wi);
            this.globalWeakList.add(wein);
        }
    }
}
