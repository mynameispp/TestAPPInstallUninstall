package com.ffzxnet.testsystemlauncher;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * 创建者： feifan.pi 在 2017/6/2.
 */

public class AppInfo {
    private String appName;//名字
    private Drawable appIco;//图标
    private String appPkgName;//包名
    private Intent appIntent;//启动别名
    private boolean isCheck;//是否被多选选中

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPkgName() {
        return appPkgName;
    }

    public void setAppPkgName(String appPkgName) {
        this.appPkgName = appPkgName;
    }

    public Drawable getAppIco() {
        return appIco;
    }

    public void setAppIco(Drawable appIco) {
        this.appIco = appIco;
    }

    public Intent getAppIntent() {
        return appIntent;
    }

    public void setAppIntent(Intent appIntent) {
        this.appIntent = appIntent;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
