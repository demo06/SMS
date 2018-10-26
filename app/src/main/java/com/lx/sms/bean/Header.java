package com.lx.sms.bean;

/**
 * header
 */

public class Header {
    /**
     * page : 0
     * pageCount : 0
     * recordCount : 0
     * rows : 0
     * rspCode : 0000
     * rspDesc : 返回成功!
     */

    public int page;
    public int pageCount;
    public int recordCount;
    public int rows;
    public String rspCode;
    public String rspDesc;
    public String mobileSimei;

    public Header(String imei) {
        this.mobileSimei = imei;
    }

    @Override
    public String toString() {
        return "mobileSimei:" + mobileSimei;
    }

}
