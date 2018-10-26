package com.lx.sms.activitys;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.lx.sms.R;
import com.lx.sms.bean.AuditBean;
import com.lx.sms.bean.AuditInfo;
import com.lx.sms.bean.BaseBean;
import com.lx.sms.bean.CancelLeaveBean;
import com.lx.sms.bean.Header;
import com.lx.sms.bean.Request;
import com.lx.sms.bean.Response;
import com.lx.sms.global.MyApplication;
import com.lx.sms.net.clientAndApi.Network;
import com.lx.sms.util.DialogUtil;
import com.lx.sms.util.ErrorHandler;
import com.lx.sms.util.StringUtils;
import com.lx.sms.util.ToastUtil;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class LeaveDetailActivity extends BaseActivity {


    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.tv_reason)
    TextView tvReason;
    @BindView(R.id.btn_apply)
    Button btnApply;
    @BindView(R.id.btn_agree)
    Button btnAgree;
    @BindView(R.id.btn_reject)
    Button btnReject;
    @BindView(R.id.ll_btn)
    LinearLayout llBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_leave_detail;
    }

    @Override
    protected void initView() {
        initToolBar(true);
    }

    AuditInfo auditInfo;
    String type, id, remark, reportDate, workDate, leaveHours;


    @Override
    protected void initData() {
        auditInfo = getIntent().getParcelableExtra("audit_data");
        type = auditInfo.approvalStatus;
        id = auditInfo.id + "";
        leaveHours = auditInfo.preLeaveHours;
        if (auditInfo != null) {
            if (type.equals("5")) {
                btnApply.setVisibility(View.VISIBLE);
                setTitle("销假");
            } else {
                llBtn.setVisibility(View.VISIBLE);
            }
            tvName.setText(auditInfo.lawOfferName + "的请假");
            tvType.setText(auditInfo.leaveType);
            tvState.setText(auditInfo.approvalStatusName);
            tvStart.setText(auditInfo.beginDate);
            tvEnd.setText(auditInfo.endDate);
            tvDay.setText(auditInfo.preLeaveHours);
            tvReason.setText(auditInfo.reason);
        }


    }


    @OnClick({R.id.btn_agree, R.id.btn_reject, R.id.btn_apply})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_agree:
                post4Audit("1");
                ToastUtil.showShort("已同意");
                finish();
                break;
            case R.id.btn_reject:
                DialogUtil.showEditDialog(this, "edit", "拒绝原因", new DialogUtil.OnEditDialogConfirmListener() {
                    @Override
                    public void onEditDialogConfirm(String content) {
//                        post4Reject(content);
                        remark = content;
                        finish();
                        ToastUtil.showShort("已拒绝");
                        post4Audit("0");
                    }

                    @Override
                    public void onTextIsEmpty() {
                        post4Audit("0");
                        finish();
                        ToastUtil.showShort("已拒绝");
                    }

                });

                break;
            case R.id.btn_apply:
                reportDate = StringUtils.getDateString(new Date(System.currentTimeMillis()));
                ToastUtil.showLong("请选择上班日期");
                DialogUtil.showDatePickerDialog(this, "上班时间", "请选择上班时间",
                        (year, month, day) ->
                        {
                            DialogUtil.showTimerPickerDialog(this, "startTime", "选择时间", (hourOfDay, minute) -> {
                                workDate = year + "-" + month + "-" + day + " " + hourOfDay + ":" + (minute > 10 ? minute : "0" + minute) + ":00";
                                cancelLeave(reportDate, workDate);
                            });
                        });
                break;
            default:
                break;
        }
    }

    /**
     * 审核接口
     */
    public void post4Audit(@NonNull String approvalOpinion) {
        Header header = new Header(MyApplication.imei);
        AuditBean auditBean = new AuditBean(id, approvalOpinion, remark);
        Request<AuditBean> request = new Request<>(auditBean);
        BaseBean baseBean = new BaseBean();
        baseBean.setHeader(header);
        baseBean.setRequest(request);
        showWaitDialog("提交中...").setCancelable(false);
        subscription = Network.getUserAPI().getAuditInfo(baseBean.toString())
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
                        ErrorHandler.getHttpException(LeaveDetailActivity.this, e, true);
                    }

                    @Override
                    public void onNext(BaseBean bean) {
                        if ("0000".equals(bean.header.rspCode)) {
                            ToastUtil.showShort("审核成功,已转交下一步");
                        } else {
                            if (!TextUtils.isEmpty(bean.header.rspDesc)) {
                                ToastUtil.showShort(bean.header.rspDesc);
                            }
                        }
                    }
                });
    }

    /**
     * 销假接口
     */
    public void cancelLeave(@NonNull String reportDate, @NonNull String workDate) {
        Header header = new Header(MyApplication.imei);
        CancelLeaveBean cancelLeaveBean = new CancelLeaveBean(id, reportDate, workDate, leaveHours);
        Request<CancelLeaveBean> request = new Request<>(cancelLeaveBean);
        BaseBean baseBean = new BaseBean();
        baseBean.setHeader(header);
        baseBean.setRequest(request);
        showWaitDialog("提交中...").setCancelable(false);
        subscription = Network.getUserAPI().getCancelLeaveInfo(baseBean.toString())
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
                        ErrorHandler.getHttpException(LeaveDetailActivity.this, e, true);
                    }

                    @Override
                    public void onNext(BaseBean bean) {
                        if ("0000".equals(bean.header.rspCode)) {
                            ToastUtil.showShort("销假申请已提交");
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
