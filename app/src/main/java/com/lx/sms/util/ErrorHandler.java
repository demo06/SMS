package com.lx.sms.util;

import android.content.Intent;

import com.lx.sms.activitys.BaseActivity;
import com.lx.sms.activitys.MainActivity;
import com.lx.sms.global.MyApplication;

import java.io.IOException;

import retrofit2.HttpException;


public class ErrorHandler {
    public static void getHttpException(BaseActivity baseActivity, Throwable throwable, boolean isShowToast) {

        if (throwable instanceof HttpException) {
            HttpException exception = (HttpException) throwable;
            try {
                String body = exception.response().errorBody().string();
                if (exception.code() == 403 && body.contains("102")) {
//                    MyApplication.getInstance().clearLoginInfo();
                    MyApplication.login = false;
                    MyApplication.getInstance().clearActivity();
                    ToastUtil.showShort("登陆过期,请重新登陆");
                    baseActivity.startActivity(new Intent(baseActivity, MainActivity.class));
                    return;
                } else if (exception.code() == 403 && body.contains("101")) {
                    ToastUtil.showShort("你无权进行操作");
                    return;
                } else if (exception.code() == 403 && body.contains("109")) {
//                    MyApplication.getInstance().clearLoginInfo();
                    MyApplication.login = false;
                    MyApplication.getInstance().clearActivity();
                    ToastUtil.showShort("账号异地登陆,请重新登陆");
                    baseActivity.startActivity(new Intent(baseActivity, MainActivity.class));
                    return;
                } else if (exception.code() == 500 && body.contains("103")) {
                    ToastUtil.showShort("服务器异常，请稍后重试");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (isShowToast) {
            ToastUtil.showShort("网络异常，请重试");
        }

    }

}
