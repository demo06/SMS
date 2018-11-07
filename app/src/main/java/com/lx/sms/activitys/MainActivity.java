package com.lx.sms.activitys;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lx.sms.R;
import com.lx.sms.receiver.UpdateAppReceiver;
import com.lx.sms.adapter.GeneralAdapter;
import com.lx.sms.bean.BaseBean;
import com.lx.sms.bean.GeneralBean;
import com.lx.sms.bean.Header;
import com.lx.sms.bean.Request;
import com.lx.sms.bean.UpdateInfo;
import com.lx.sms.global.MyApplication;
import com.lx.sms.net.AppData;
import com.lx.sms.net.Constant;
import com.lx.sms.net.clientAndApi.Network;
import com.lx.sms.util.DialogUtil;
import com.lx.sms.util.ErrorHandler;
import com.lx.sms.util.LocationUtils;
import com.lx.sms.util.PermissionUtils;
import com.lx.sms.util.TDevice;
import com.lx.sms.util.ToastUtil;
import com.lx.sms.util.UpdateAppUtils;

import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 首页
 */
public class MainActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {


    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    List<GeneralBean> generalBean;
    GeneralAdapter adapter;
    String imei, version;
    UpdateInfo updateInfo = new UpdateInfo();
    private boolean isRegisterReceiver = false;

    @Override
    protected int getLayoutId() {
        return R.layout.recycle;
    }


    @Override
    protected void initView() {
        generalBean = AppData.getIconListData();
        recyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        adapter = new GeneralAdapter(generalBean);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        addBroadCase();
    }

    @Override
    protected void initData() {
        if (PermissionUtils.checkReadPhoneAndWritePermission(this, true)) {
            login();
        } else {
            PermissionUtils.checkReadPhoneAndWritePermission(this, false);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (position) {
            case 0:
                //打卡
                goActivity(SignActivity.class);
                break;
            case 1:
                //请假
                goActivity(LeaveActivity.class);
                break;
            case 2:
                //我的假条
                goActivity(MyLeaveActivity.class);
                break;
            case 3:
                // 审批
                goActivity(AuditActivity.class);
                break;
            default:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.MY_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    login();
                } else {
                    //权限被拒绝
                    DialogUtil.showSignDialog(
                            this,
                            "权限未开启",
                            "去设置",
                            "请打开电话权限和存储权限以使程序正常运行",
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationUtils.GPS_LOCATION_REQUEST_CODE) {
            initData();
        }
    }

    /**
     * 登陆
     */
    public void login() {
//        科长
//        imei = "866783036790487";
        imei = TDevice.getIMEI();
        Header mheader = new Header(imei);
        BaseBean baseBean = new BaseBean();
        baseBean.setHeader(mheader);
        showWaitDialog("获取中...").setCancelable(false);
        subscription = Network.getUserAPI().login(baseBean.toString())
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
                        ErrorHandler.getHttpException(MainActivity.this, e, true);
                    }

                    @Override
                    public void onNext(BaseBean bean) {
                        if ("0000".equals(bean.header.rspCode)) {
                            if ("0".equals(bean.response.aduitFlag)) {
                                generalBean.remove(generalBean.size() - 1);
                                adapter.setNewData(generalBean);
                            }
                            MyApplication.getInstance().saveLoginInfo(bean.header.mobileSimei + "");
                            if (bean.response.version != null || bean.response.version.equals("")) {
                                version = bean.response.version;
                                updateInfo.version = version;
                                update();
                            }

                        } else {
                            if (!TextUtils.isEmpty(bean.header.rspDesc)) {
                                DialogUtil.showSignDialog(MainActivity.this,
                                        "登陆错误",
                                        "确定",
                                        bean.header.rspDesc,
                                        false,
                                        () -> back4App()
                                        , (dialog, keyCode, event) -> {
                                            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                                                return true;
                                            } else {
                                                return false;
                                            }
                                        });
                            }
                        }
                    }
                });
    }


    /**
     * 更新接口
     */
    public void update() {
        Header mheader = new Header(imei);
        Request<UpdateInfo> request = new Request(updateInfo);
        BaseBean baseBean = new BaseBean();
        baseBean.setHeader(mheader);
        baseBean.setRequest(request);
        showWaitDialog("获取中...").setCancelable(false);
        subscription = Network.getUserAPI().update(baseBean.toString())
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
                        ErrorHandler.getHttpException(MainActivity.this, e, true);
                    }

                    @Override
                    public void onNext(BaseBean bean) {
                        if ("0000".equals(bean.header.rspCode)) {
                            if (bean.response != null) {
                                Log.i("MainActivity", "更新正常=========================>");
                                UpdateAppUtils.from(MainActivity.this)
                                        .checkBy(UpdateAppUtils.CHECK_BY_VERSION_NAME)
                                        .serverVersionName(bean.response.version)
                                        .showNotification(true)
                                        .downloadBy(UpdateAppUtils.DOWNLOAD_BY_APP)
                                        .updateInfo(bean.response.vesionDescri)
                                        .isForce("1".equals(bean.response.ifCompel))
                                        .apkPath(Constant.IMG_URL + bean.response.url)
                                        .update();
                            }
                        } else {
                            if (!TextUtils.isEmpty(bean.header.rspDesc)) {
                                ToastUtil.showShort(bean.header.rspDesc);
                            }
                        }
                    }
                });
    }


    private void addBroadCase() {
        if (!isRegisterReceiver) {
            UpdateAppReceiver receiver = new UpdateAppReceiver();
            IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            registerReceiver(receiver, intentFilter);
            isRegisterReceiver = true;
        }
    }

}
