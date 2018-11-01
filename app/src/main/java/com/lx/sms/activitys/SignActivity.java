package com.lx.sms.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
    String address;
    @BindView(R.id.icon_morning)
    ImageView iconMorning;
    @BindView(R.id.line1)
    View line1;
    @BindView(R.id.morning)
    LinearLayout morning;
    @BindView(R.id.tv_morning)
    TextView tvMorning;
    @BindView(R.id.icon_noon)
    ImageView iconNoon;
    @BindView(R.id.line2)
    View line2;
    @BindView(R.id.noon)
    LinearLayout noon;
    @BindView(R.id.icon_afternoon)
    ImageView iconAfternoon;
    @BindView(R.id.line3)
    View line3;
    @BindView(R.id.afternoon)
    LinearLayout afternoon;
    @BindView(R.id.icon_evening)
    ImageView iconEvening;
    @BindView(R.id.evening)
    LinearLayout evening;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.btn_sign)
    Button btnSign;
    SignAdapter adapter;
    Location location;
    //文件保存路径
    String path;
    String imgUrl;
    String curTime;

    @Override
    public void init() {
        if (LocationUtils.getInstance().isOpenGPS()) {
            if (PermissionUtils.checkCameraAndGpsPermission(this)) {
                getGpsLocation();
            } else {
                PermissionUtils.checkCameraAndGpsPermission(this);
            }


        } else {
            DialogUtil.showSignDialog(
                    this,
                    "GPS定位未开启",
                    "去开启",
                    "签到需要打开GPS获取位置信息，请打开位置信息开关",
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
    protected void initData() {
        getSingInfo();
    }


    @OnClick(R.id.btn_sign)
    public void onViewClicked() {
        if (!TextUtils.isEmpty(lat + "") && !TextUtils.isEmpty(lng + "")) {
            DialogUtil.showDialog(this, "拍照", "打卡前请拍摄照片", this::takePhoto);
        } else {
            ToastUtil.showShort("请重新定位");
        }
    }


    public void getGpsLocation() {
        location = LocationUtils.getInstance().getLocation();
        if (location != null) {
            lat = String.valueOf(location.getLatitude());
            lng = String.valueOf(location.getLongitude());
            ToastUtil.showLong("lat:" + lat + "\nlng:" + lng);
        } else {
            ToastUtil.showShort("定位失败");
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
                            for (int i = 0; i < list.size(); i++) {
                                if (!TextUtils.isEmpty(list.get(i).signFlag)) {
                                    if (list.get(i).signFlag.equals("0")) {
                                        iconMorning.setBackground(getResources().getDrawable(R.mipmap.sign_checked, null));
                                    } else if (list.get(i).signFlag.equals("1")) {
                                        iconNoon.setBackground(getResources().getDrawable(R.mipmap.sign_checked, null));
                                    } else if (list.get(i).signFlag.equals("2")) {
                                        iconAfternoon.setBackground(getResources().getDrawable(R.mipmap.sign_checked, null));
                                    } else if (list.get(i).signFlag.equals("3")) {
                                        iconEvening.setBackground(getResources().getDrawable(R.mipmap.sign_checked, null));
                                    }
                                }
                            }
                            adapter.setNewData(list);
                        } else

                        {
                            if (!TextUtils.isEmpty(bean.header.rspDesc)) {
                                ToastUtil.showShort(bean.header.rspDesc);
                            }
                        }
                    }
                });
    }

    /*上传图片*/
    public void upload() {
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
                                sign();
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
     * 签到
     */
    public void sign() {
        if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng) || !lat.equals("0.0") && !lng.equals("0.0")) {
            int hours = StringUtils.getCurrHour();
            if (7 <= hours && hours < 12) {
                curTime = "0";
            } else if (12 <= hours && hours < 13) {
                curTime = "1";
            } else if (13 <= hours && hours < 15) {
                curTime = "2";
            } else if (15 <= hours && hours < 24) {
                curTime = "3";
            } else {
                ToastUtil.showShort("当前不在签到时间内");
            }

            Header mheader = new Header(MyApplication.imei);
            SignInfo signInfo = new SignInfo(String.valueOf(lat), String.valueOf(lng), imgUrl, "1x");
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
                                ToastUtil.showLong("打卡成功");
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            upload();
        } else if (requestCode == LocationUtils.GPS_LOCATION_REQUEST_CODE) {
            if (LocationUtils.getInstance().isOpenGPS()) {
                PermissionUtils.checkCameraAndGpsPermission(this);
            } else {
                DialogUtil.showSignDialog(
                        this,
                        "GPS定位未开启",
                        "去开启",
                        "签到需要打开GPS获取位置信息，请打开位置信息开关",
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
                .subscribe(aLong -> btnSign.setText("打卡\n" + StringUtils.getCurTimeStr()));

    }

    /**
     * android 7.0系统拍照会出现错误提示,具体出现问题原因不明确,先照网上搜索的办法来,后期在调整
     */
    private void initPhotoError() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    /**
     * 图片压缩算法
     */
    public void compression() {
        //发现图片在ImageView上无法显示，原因是图片过大导致的，所以要对于图片进行处理。
        //二次采样   对于图片的宽度和高度进行处理，对于图片的质量进行处理

        //1.获取用于设置图片属性的参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        //2.对于属性进行设置，需要解锁边缘
        options.inJustDecodeBounds = true;
        //3.对于图片进行编码处理
        BitmapFactory.decodeFile(path, options);
        //4.获取原来图片的宽度和高度
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        //5.200,200  获得要压缩的比例
        //2
        int sampleHeight = outHeight / 400;
        //1.5
        int sampleWidth = outWidth / 400;
        //6.获取较大的比例
        int size = Math.max(sampleHeight, sampleWidth);
        //7.设置图片压缩的比例
        options.inSampleSize = size;
        /**图片的质量   1个字节是8位
         * ARGB_8888  32位     4字节   100*100*4 = 40000 个字节
         * ARGB_4444  16位     2字节   100*100*2 = 20000 个字节
         * RGB_565    16位      2字节  100*100*2 = 20000 个字节
         * Alpha_8    8位       1字节  100*100*1 = 10000 个字节
         *
         * 100px*100px  的图片
         * */
        //设置图片的质量类型
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        //8.锁定边缘
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
//            ivIv.setImageBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
