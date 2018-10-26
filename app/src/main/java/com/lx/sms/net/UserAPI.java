package com.lx.sms.net;


import com.lx.sms.bean.AuditInfo;
import com.lx.sms.bean.BaseBean;
import com.lx.sms.bean.ResponBean;
import com.lx.sms.bean.Response;
import com.lx.sms.bean.SignBean;
import com.lx.sms.bean.UpdateInfo;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * 用户信息
 */

public interface UserAPI {

    /**
     * 登陆
     *
     * @return
     */
    @FormUrlEncoded
    @POST(Constant.USER_LOGIN)
    Observable<BaseBean> login(@Field("params") String params);

    /**
     * 更新
     *
     * @return
     */
    @FormUrlEncoded
    @POST(Constant.UPLOAD_APP)
    Observable<BaseBean> update(@Field("params") String params);


    /**
     * 签到
     */
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @FormUrlEncoded
    @POST(Constant.SIGN_INFO)
    Observable<BaseBean> sign(@Field("params") String params
    );

    /**
     * 获取签到信息
     */
    @FormUrlEncoded
    @POST(Constant.GET_SIGN_INFO)
    Observable<ResponBean<Response<List<SignBean>>>> getSignInfo(@Field("page") String page,
                                                                 @Field("rows") String rows,
                                                                 @Field("params") String params);

    /**
     * 保存请假信息
     */

    @FormUrlEncoded
    @POST(Constant.ADD_LEAVE_INFO)
    Observable<BaseBean> post4Leave(@Field("params") String params);


    /**
     * 修改请假信息
     */

    @FormUrlEncoded
    @POST(Constant.EDIT_LEAVE_INFO)
    Observable<BaseBean> edit4Leave(@Field("params") String params);


    /**
     * 获取待审核列表数据
     *
     * @return
     */
    @FormUrlEncoded
    @POST(Constant.AUDIT_LEAVE_INFO)
    Observable<ResponBean<Response<List<AuditInfo>>>> getAuditListInfo(@Field("page") String page,
                                                                       @Field("rows") String rows,
                                                                       @Field("params") String params);

    /**
     * 获取我的请假数据
     *
     * @return
     */
    @FormUrlEncoded
    @POST(Constant.MY_LEAVE_INFO)
    Observable<ResponBean<Response<List<AuditInfo>>>> getMyLeaveListInfo(@Field("page") String page,
                                                                         @Field("rows") String rows,
                                                                         @Field("params") String params);

    /**
     * 获取已审核列表数据
     *
     * @return
     */
    @FormUrlEncoded
    @POST(Constant.AUDIT_INFO)
    Observable<BaseBean> getAuditInfo(@Field("params") String params);


    /**
     * 获取已审核列表数据
     *
     * @return
     */
    @FormUrlEncoded
    @POST(Constant.CANCEL_LEAVE_INFO)
    Observable<BaseBean> getCancelLeaveInfo(@Field("params") String params);

    /**
     * upload
     *
     * @return
     */
    @Multipart
    @POST(Constant.UPLOAD_VIDEO)
    Observable<BaseBean> uploadFile(
            @Part("fileName") String name, @Part MultipartBody.Part file);

}
