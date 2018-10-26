package com.lx.sms.bean;


public class SignInfo {


    public String lat;
    public String lng;
    public String url;
    public String signFlag;

    public SignInfo(String lat, String lng, String url, String signFlag) {
        this.lat = lat;
        this.lng = lng;
        this.url = url;
        this.signFlag = signFlag;
    }

    @Override
    public String toString() {
        return
                "lat:\"" + lat +
                        "\", lng:\"" + lng +
                        "\", url:\"" + url +
                        "\", signFlag:\"" + signFlag +
                        "\"";
    }
}
