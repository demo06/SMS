package com.lx.sms.net.clientAndApi;


import com.lx.sms.net.Constant;
import com.lx.sms.net.UserAPI;

import retrofit2.Retrofit;

/**
 * 网络请求
 */

public class Network {


    /**
     * api
     *
     * @return
     */
    public static UserAPI getUserAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(HttpClientAndFactory.getStringGsonConverteFactory())
                .addCallAdapterFactory(HttpClientAndFactory.getRxJavaCallAdapterFactory())
                .client(HttpClientAndFactory.getInstance().getOkHttpClient())
                .build();
        return retrofit.create(UserAPI.class);
    }



}
