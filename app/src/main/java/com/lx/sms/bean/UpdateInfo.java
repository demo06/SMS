package com.lx.sms.bean;

/**
 * SMS
 * author:Created by admin on 2018-10-23.
 * date:demo06@126.com
 * QQ:290221978
 */
public class UpdateInfo {
    /**
     * id : 2000
     * version : 1.0.1
     * vesionDescri : 测试版本升级
     * ifCompel : 1
     * url : http://192.168.0.158:8080/upload/apk/sms-release.apk
     * remark : 备注
     */

    public int id;
    public String version;
    public String vesionDescri;
    public String ifCompel;
    public String url;
    public String remark;

    @Override
    public String toString() {
        return
                " version:'" + version + '\''
                ;
    }
}
