package com.lx.sms.converter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class StringResponseBodyConverter implements Converter<ResponseBody, String> {
    @Override
    public String convert(ResponseBody responseBody) throws IOException {
        return responseBody.string();
    }
}
