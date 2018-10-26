package com.lx.sms.net.clientAndApi;


import android.util.Log;

import com.lx.sms.converter.GsonConverterFactory;
import com.lx.sms.converter.StringConverFactory;
import com.lx.sms.converter.StringGsonConverFactory;
import com.lx.sms.global.MyApplication;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class HttpClientAndFactory {
    private OkHttpClient okHttpClient;
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static Converter.Factory stringConverteFactory = new StringConverFactory();
    private static Converter.Factory stringGsonConverteFactory = StringGsonConverFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
    private static HttpClientAndFactory instance;

    public static void init() {
        instance = new HttpClientAndFactory();
    }

    public static HttpClientAndFactory getInstance() {
        return instance;
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            MyInterceptor interceptor = new MyInterceptor();
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                //打印日志
                Log.i("Retrofit:======|", message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
//                    .addInterceptor(interceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build();
        }

        return okHttpClient;

    }

    public static Converter.Factory getGsonConverterFactory() {
        return gsonConverterFactory;
    }

    public static Converter.Factory getStringConverteFactory() {
        return stringConverteFactory;
    }

    public static Converter.Factory getStringGsonConverteFactory() {
        return stringGsonConverteFactory;
    }

    public static CallAdapter.Factory getRxJavaCallAdapterFactory() {
        return rxJavaCallAdapterFactory;
    }

    public static class MyInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder newBuilder = chain.request().newBuilder();
            newBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
//            newBuilder.addHeader("token", "");
//            newBuilder.addHeader("Accept", "*/*");
//            String token = "";
//            if (MyApplication.login) {
//                token = MyApplication.token;
//            }
//            newBuilder.addHeader("Token", token);
            return chain.proceed(newBuilder.build());
        }
    }



}