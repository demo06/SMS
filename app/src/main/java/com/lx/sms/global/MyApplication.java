package com.lx.sms.global;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.lx.sms.activitys.BaseActivity;
import com.lx.sms.net.clientAndApi.HttpClientAndFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;


public class MyApplication extends Application {
    private static MyApplication instance;
    public static Context context;
    public static boolean login;
    public static String imei;
    private List<BaseActivity> activityList = new LinkedList<BaseActivity>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();
        initLoginInfo();
        HttpClientAndFactory.init();

    }

    public static MyApplication getInstance() {
        return instance;
    }

    //64k配置
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    public String getProperty(String key) {
        String res = AppConfig.getAppConfig(this).get(key);
        return res;
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    public void saveLoginInfo(final String simei) {
        imei = simei;
        login = true;
        setProperties(new Properties() {
            {
                setProperty("login.imei", imei);
            }
        });
    }

    //
    private void initLoginInfo() {
        if (!TextUtils.isEmpty(getProperty("login.imei"))) {
            login = true;
            imei = getProperty("login.imei");
        } else {
            login = false;
        }
    }

    public void clearLoginInfo() {
        removeProperty("login.imei");
    }


    public void clearActivity() {
        for (BaseActivity ac :
                activityList) {
            ac.finish();
        }
        activityList.clear();
    }
}
