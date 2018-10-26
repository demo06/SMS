package com.lx.sms.net;

public interface Constant {

    //服务器地址
    String BASE_URL = "http://192.168.0.158:8080/etc/";

//        String BASE_URL = "http://192.168.0.180:8999/etc/";
    //图片服务器地址
    String IMG_URL = "http://192.168.0.158:8080/upload";
    //登陆
    String USER_LOGIN = "app-login!loginApp.do";
    //签到
    String SIGN_INFO = "app-sign!signIn.do";
    //获取签到信息
    String GET_SIGN_INFO = "app-sign!signList.do";
    //获取我的请假列表
    String MY_LEAVE_INFO = "app-leave!leavPersonList.do";
    //获取待审核列表
    String AUDIT_LEAVE_INFO = "app-leave!auditList.do";
    //获取待审核列表
    String AUDIT_INFO = "app-leave!audit.do";
    //提交请假接口
    String ADD_LEAVE_INFO = "app-leave!leave.do";
    //保存修改请假接口
    String EDIT_LEAVE_INFO = "app-leave!leaveUpdate.do";
    //保存修改请假接口
    String CANCEL_LEAVE_INFO = "app-leave!report.do";
    //图片上传
    String UPLOAD_VIDEO = "app-sign!upload.do";
    //图片上传
    String UPLOAD_APP = "pda-version!getVersion.do";


    //当前登录的用户
    String USER = "user";
    //汇总问题分类
    String CATEGORY = "category";
    //医院项目信息tab标签
    String ARGS_PAGE = "args_page";
    //汇总标签
    String SUM_TIME = "TIME";
    //bundle传值类型
    String TYPE = "TYPE";

    interface LEAVEITEM {
        String Y = "audit";//审核
        String N = "unaudit";//未审核
        String C = "cancelLeave";//销假
    }

}
