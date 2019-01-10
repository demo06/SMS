package com.lx.sms.activitys;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lx.sms.R;
import com.lx.sms.bean.AuditInfo;
import com.lx.sms.bean.BaseBean;
import com.lx.sms.bean.Header;
import com.lx.sms.bean.LeaveInfo;
import com.lx.sms.bean.Request;
import com.lx.sms.bean.SignInfo;
import com.lx.sms.global.MyApplication;
import com.lx.sms.net.clientAndApi.Network;
import com.lx.sms.util.DialogUtil;
import com.lx.sms.util.ErrorHandler;
import com.lx.sms.util.StringUtils;
import com.lx.sms.util.TDevice;
import com.lx.sms.util.ToastUtil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LeaveActivity extends BaseActivity {


    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.rl_type)
    RelativeLayout rlType;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.rl_starttime)
    RelativeLayout rlStarttime;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.rl_endtime)
    RelativeLayout rlEndtime;
    @BindView(R.id.tv_day)
    EditText tvDay;
    @BindView(R.id.tv_reason)
    EditText tvReason;
    @BindView(R.id.rl_reason)
    RelativeLayout rlReason;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.btn_save)
    Button btnSave;
    String type, startTime, endTime, reason, flag;
    double countDays;
    //1事假2病假3丧假4婚假5年假6产假7陪产假8倒休9其他
    String item[] = {"事假", "病假", "丧假", "婚假", "年假", "产假", "陪产假", "倒休", "其他"};
    AuditInfo auditInfo;

    @Override
    public void init() {
        auditInfo = getIntent().getParcelableExtra("audit_data");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_leave;
    }

    @Override
    protected void initView() {
        initToolBar(true);
    }

    @Override
    protected void initData() {
        setViewDate();
    }

    /**
     * 设置界面数据
     */
    public void setViewDate() {
        if (auditInfo != null) {
            tvType.setText(auditInfo.leaveType);
            type = Arrays.binarySearch(item, auditInfo.leaveType) + 1 + "";
            tvStart.setText(auditInfo.beginDate);
            tvEnd.setText(auditInfo.endDate);
            tvDay.setText(auditInfo.preLeaveHours);
            tvReason.setText(auditInfo.reason);
        }
    }

    @SuppressLint("SetTextI18n")
    @OnClick({R.id.rl_type, R.id.rl_starttime, R.id.rl_endtime, R.id.btn_submit, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_type:
                DialogUtil.showListDialog(this, "请选择请假类型", item, (dialog, itemView, position, text) -> {
                    tvType.setText(text);
                    type = position + 1 + "";
                    switch (type) {
                        case "4":
                            tvDay.setText("10");
                            break;
                        case "5":
                            tvDay.setText("5");
                            break;
                        default:
                            break;
                    }
                });
                break;
            case R.id.rl_starttime:
                DialogUtil.showDatePickerDialog(this, "start", "起始日期",
                        (year, month, day) ->
                        {
                            DialogUtil.showTimerPickerDialog(this, "startTime", "选择时间", (hourOfDay, minute) -> {
                                tvStart.setText(year + "-" + month + "-" + day + " " + hourOfDay + ":" + (minute > 10 ? minute : "0" + minute) + ":00");
                                startTime = year + "-" + month + "-" + day + " " + hourOfDay + ":" + (minute > 10 ? minute : "0" + minute) + ":00";
                            });
                        });
                break;
            case R.id.rl_endtime:
                if (TextUtils.isEmpty(tvStart.getText().toString())) {
                    ToastUtil.showShort("请先选择起始日期");
                } else {
                    DialogUtil.showDatePickerDialog(this, "start", "结束日期",
                            (year, month, day) -> {
                                DialogUtil.showTimerPickerDialog(this, "endTime", "选择时间", (hourOfDay, minute) -> {
                                    endTime = year + "-" + month + "-" + day + " " + hourOfDay + ":" + (minute > 10 ? minute : "0" + minute) + ":00";
                                    if (!TextUtils.isEmpty(tvStart.getText().toString())) {
                                        long days = StringUtils.calDateDifferent(startTime, endTime);
                                        if (days > 0) {
                                            tvEnd.setText(year + "-" + month + "-" + day + " " + hourOfDay + ":" + (minute > 10 ? minute : "0" + minute) + ":00");
                                        } else {
                                            tvEnd.setText("");
                                            ToastUtil.showShort("结束日期不能选择在起始日期之前");
                                        }
                                    }

                                });

                            });
                }
                break;
            case R.id.btn_submit:
                flag = "1";
                if (auditInfo != null) {
                    //修改
                    edit();
                } else {
                    submit();
                }

                break;
            case R.id.btn_save:
                flag = "0";
                if (auditInfo != null) {
                    //修改
                    edit();
                } else {
                    //提交
                    submit();
                }

                break;
            default:
                break;
        }
    }


    /**
     * 提交请假请求
     */
    public void submit() {
        if (TextUtils.isEmpty(tvType.getText().toString())) {
            ToastUtil.showShort("请选择请假类型");
            return;
        }
        startTime = tvStart.getText().toString();
        if (TextUtils.isEmpty(startTime)) {
            ToastUtil.showShort("请选择开始日期");
            return;
        }
        endTime = tvEnd.getText().toString();
        if (TextUtils.isEmpty(endTime)) {
            ToastUtil.showShort("请选择结束日期");
            return;
        }
        countDays = Double.parseDouble(tvDay.getText().toString());
        if (TextUtils.isEmpty(countDays + "") || countDays % 0.5 != 0) {
            ToastUtil.showShort("请输入请假天数且必须是0.5的倍数");
            return;
        }
        reason = tvReason.getText().toString();
        if (TextUtils.isEmpty(reason)) {
            ToastUtil.showShort("请输入请假原因");
            return;
        }
        Header header = new Header(MyApplication.imei);
        LeaveInfo leaveInfo = new LeaveInfo(null, type, startTime, endTime, countDays + "", reason, flag);
        Request<LeaveInfo> request = new Request<>(leaveInfo);
        BaseBean baseBean = new BaseBean();
        baseBean.setHeader(header);
        baseBean.setRequest(request);
        showWaitDialog("正在提交...").setCancelable(false);
        subscription = Network.getUserAPI().post4Leave(baseBean.toString())
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
                        ErrorHandler.getHttpException(LeaveActivity.this, e, false);
                    }

                    @Override
                    public void onNext(BaseBean bean) {
                        if ("0000".equals(bean.header.rspCode)) {
                            if (flag.equals("0")) {
                                ToastUtil.showShort("已保存");
                            } else {
                                ToastUtil.showShort("提交成功,请耐心等待审核");
                            }
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(bean.header.rspDesc)) {
                                DialogUtil.showSignDialog(
                                        LeaveActivity.this,
                                        "提示",
                                        "确定",
                                        bean.header.rspDesc,
                                        true,
                                        () -> {
                                        },
                                        (dialog, keyCode, event) -> false);
//                                ToastUtil.showShort(bean.header.rspDesc);
                            }
                        }
                    }
                });
    }


    /**
     * 修改请假请求
     */
    public void edit() {
        if (TextUtils.isEmpty(tvType.getText().toString())) {
            ToastUtil.showShort("请选择请假类型");
            return;
        }
        startTime = tvStart.getText().toString();
        if (TextUtils.isEmpty(startTime)) {
            ToastUtil.showShort("请选择开始日期");
            return;
        }
        endTime = tvEnd.getText().toString();
        if (TextUtils.isEmpty(endTime)) {
            ToastUtil.showShort("请选择结束日期");
            return;
        }
        countDays = Double.parseDouble(tvDay.getText().toString());
        if (TextUtils.isEmpty(countDays + "") || countDays % 0.5 != 0) {
            ToastUtil.showShort("请输入请假天数且必须是0.5的倍数");
            return;
        }
        reason = tvReason.getText().toString();
        if (TextUtils.isEmpty(reason)) {
            ToastUtil.showShort("请输入请假原因");
            return;
        }
        Header header = new Header(MyApplication.imei);
        LeaveInfo leaveInfo = new LeaveInfo(auditInfo.id + "", type, startTime, endTime, countDays + "", reason, flag);
        Request<LeaveInfo> request = new Request<>(leaveInfo);
        BaseBean baseBean = new BaseBean();
        baseBean.setHeader(header);
        baseBean.setRequest(request);
        showWaitDialog("正在提交...").setCancelable(false);
        subscription = Network.getUserAPI().edit4Leave(baseBean.toString())
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
                        ErrorHandler.getHttpException(LeaveActivity.this, e, false);
                    }

                    @Override
                    public void onNext(BaseBean bean) {
                        if ("0000".equals(bean.header.rspCode)) {
                            if (flag.equals("0")) {
                                ToastUtil.showShort("已保存");
                            } else {
                                ToastUtil.showShort("提交成功,请耐心等待审核");
                            }
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(bean.header.rspDesc)) {
                                ToastUtil.showShort(bean.header.rspDesc);
                            }
                        }
                    }
                });
    }

}
