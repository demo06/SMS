package com.lx.sms.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lx.sms.R;
import com.lx.sms.adapter.SignAdapter;
import com.lx.sms.bean.BaseBean;
import com.lx.sms.bean.Header;
import com.lx.sms.bean.Request;
import com.lx.sms.bean.ResponBean;
import com.lx.sms.bean.Response;
import com.lx.sms.bean.SignBean;
import com.lx.sms.bean.SignInfo;
import com.lx.sms.global.MyApplication;
import com.lx.sms.net.clientAndApi.Network;
import com.lx.sms.util.DialogUtil;
import com.lx.sms.util.ErrorHandler;
import com.lx.sms.util.LocationUtils;
import com.lx.sms.util.PermissionUtils;
import com.lx.sms.util.StringUtils;
import com.lx.sms.util.ToastUtil;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SignActivity extends BaseActivity {


    String lat;
    String lng;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.btn_sign_in)
    Button btnSign;
    @BindView(R.id.btn_sign_out)
    Button btnSignOut;
    SignAdapter adapter;
    Location location;
    //文件保存路径
    String path;
    String imgUrl;
    String signFlag;

    @Override
    public void init() {
        if (LocationUtils.getInstance().isOpenGPS()) {
            if (PermissionUtils.checkCameraAndGpsPermission(this, true)) {
                getGpsLocation();
            } else {
                PermissionUtils.checkCameraAndGpsPermission(this, false);
            }
        } else {
            DialogUtil.showSignDialog(
                    this,
                    "GPS定位未开启",
                    "去开启",
                    "签到需要打开GPS获取位置信息，请打开位置信息开关",
                    false,
                    () -> LocationUtils.getInstance().openGPS(SignActivity.this),
                    (dialog, keyCode, event) -> {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                            return true;
                        } else {
                            return false;
                        }
                    });
        }

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign;

    }


    @Override
    protected void initView() {
        setBtnTime();
        initPhotoError();
        //当前项目的缓存路径下  ：/sdcard/Android/data/当前项目的包名/cache/....
        path = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + File.separator + "sign_photo.png";
        initToolBar(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SignAdapter(null);
//        recyclerview.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));
        adapter.setEmptyView(R.layout.empty_view, (ViewGroup) recyclerview.getParent());
        recyclerview.setAdapter(adapter);

    }


    @Override
    protected void initData() {
        getSingInfo();
    }


    @OnClick({R.id.btn_sign_in, R.id.btn_sign_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_in:
                signFlag = "0";
                break;
            case R.id.btn_sign_out:
                signFlag = "3";
                break;
            default:
                break;
        }
        if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng) || !lat.equals("0.0") && !lng.equals("0.0")) {
            DialogUtil.showDialog(this, "拍照", "打卡前请拍摄照片", this::takePhoto);
        } else {
            ToastUtil.showShort("请重新定位");
        }
    }

    /**
     * 获取签到定位
     */
    public void getGpsLocation() {
        location = LocationUtils.getInstance().getLocation();
        if (location != null) {
//         39.6629042030,116.4259044226 大兴坐标
//            lat = "116.4259044226";
//            lng = "39.6629042030";
            lat = String.valueOf(location.getLatitude());
            lng = String.valueOf(location.getLongitude());
        } else {
            DialogUtil.showSignDialog(
                    this,
                    "定位失败",
                    "确定",
                    "当前位置GPS信号较弱,请移动到空旷位置重新定位",
                    false,
                    this::finish,
                    (dialog, keyCode, event) -> {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                            return true;
                        } else {
                            return false;
                        }
                    });
        }

    }

    /**
     * 获取签到信息
     */
    public void getSingInfo() {
        Header mheader = new Header(MyApplication.imei);
        BaseBean baseBean = new BaseBean();
        baseBean.setHeader(mheader);
        showWaitDialog("获取中...").setCancelable(false);
        subscription = Network.getUserAPI().getSignInfo("1", "4", baseBean.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponBean<Response<List<SignBean>>>>() {
                    @Override
                    public void onCompleted() {
                        hideWaitDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideWaitDialog();
                        ErrorHandler.getHttpException(SignActivity.this, e, true);
                    }

                    @Override
                    public void onNext(ResponBean<Response<List<SignBean>>> bean) {
                        if ("0000".equals(bean.header.rspCode)) {
                            list = bean.response.jsonList;
                            adapter.setNewData(list);
                        } else {
                            if (!TextUtils.isEmpty(bean.header.rspDesc)) {
                                ToastUtil.showShort(bean.header.rspDesc);
                            }
                        }
                    }
                });
    }

    /*上传图片*/

    /**
     * 上传图片
     *
     * @param signFlag 签到时间标志
     */
    public void upload(String signFlag) {
        File file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part photo = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        showWaitDialog("上传中...").setCancelable(false);
        subscription = Network.getUserAPI().uploadFile(file.getName(), photo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean>() {
                    @Override
                    public void onCompleted() {
                        hideWaitDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideWaitDialog();
                        ErrorHandler.getHttpException(SignActivity.this, e, false);
                    }

                    @Override
                    public void onNext(BaseBean baseBean) {
                        if (("0000").equals(baseBean.header.rspCode)) {
                            imgUrl = baseBean.response.url;
                            if (imgUrl.length() > 0) {
                                sign(signFlag);
                            } else {
                                ToastUtil.showShort("请拍照后在签到");
                                return;
                            }


                        } else {
                            if (!TextUtils.isEmpty(baseBean.header.rspDesc)) {
                                ToastUtil.showShort(baseBean.header.rspDesc);
                            }
                        }
                    }
                });
    }

    List<SignBean> list;

    /**
     * 签到,签退
     */
    public void sign(String signFlag) {
        if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng) || !lat.equals("0.0") && !lng.equals("0.0")) {
//            39.6629042030,116.4259044226 大兴坐标
            Header mheader = new Header(MyApplication.imei);
            SignInfo signInfo = new SignInfo(String.valueOf(lat), String.valueOf(lng), imgUrl, signFlag);
            Request<SignInfo> request = new Request<>(signInfo);
            BaseBean baseBean = new BaseBean();
            baseBean.setHeader(mheader);
            baseBean.setRequest(request);
            showWaitDialog("获取中...").setCancelable(false);
            subscription = Network.getUserAPI().sign(baseBean.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseBean>() {
                        @Override
                        public void onCompleted() {
                            hideWaitDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideWaitDialog();
                            ErrorHandler.getHttpException(SignActivity.this, e, false);
                        }

                        @Override
                        public void onNext(BaseBean bean) {
                            if (("0000").equals(bean.header.rspCode)) {
                                ToastUtil.showLong("签到成功");
                                getSingInfo();
                            } else {
                                if (!TextUtils.isEmpty(bean.header.rspDesc)) {
                                    ToastUtil.showShort(bean.header.rspDesc);
                                }
                            }
                        }
                    });
        } else {
            ToastUtil.showShort("定位异常,请重新签到");
        }

    }


    /**
     * 跳转到拍照界面
     */
    public void takePhoto() {
        startActivityForResult(new Intent()
                .setAction(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path))), 100);
    }


    /**
     * 设置打卡按钮上显示的时间
     */
    public void setBtnTime() {
        subscription = Observable.interval(
                1000,
                1000,
                TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong ->
                        tvTime.setText(StringUtils.getCurTimeStr()));
    }

    /**
     * android 7.0系统拍照会出现错误提示,具体出现问题原因不明确,先照网上搜索的办法来,后期在调整
     */
    private void initPhotoError() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            upload(signFlag);
        } else if (requestCode == LocationUtils.GPS_LOCATION_REQUEST_CODE) {
            if (LocationUtils.getInstance().isOpenGPS()) {
                getGpsLocation();
            } else {
                DialogUtil.showSignDialog(
                        this,
                        "GPS定位未开启",
                        "去开启",
                        "签到需要打开GPS获取位置信息，请打开位置信息开关",
                        false,
                        () -> LocationUtils.getInstance().openGPS(SignActivity.this),
                        (dialog, keyCode, event) -> {
                            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                                return true;
                            } else {
                                return false;
                            }
                        });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.MY_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //已获取权限
                    getGpsLocation();
                } else {
                    //权限被拒绝
                    DialogUtil.showSignDialog(
                            this,
                            "定位权限未开启",
                            "去开启",
                            "签到需要打开定位权限获取位置信息，请打开定位权限",
                            false,
                            this::readyGoForSetting,
                            (dialog, keyCode, event) -> {
                                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                                    return true;
                                } else {
                                    return false;
                                }
                            });
                }

                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
