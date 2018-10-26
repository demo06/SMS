package com.lx.sms.activitys;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lx.sms.R;
import com.lx.sms.adapter.AuditAdapter;
import com.lx.sms.bean.AuditInfo;
import com.lx.sms.bean.BaseBean;
import com.lx.sms.bean.Header;
import com.lx.sms.bean.ResponBean;
import com.lx.sms.bean.Response;
import com.lx.sms.global.MyApplication;
import com.lx.sms.net.clientAndApi.Network;
import com.lx.sms.util.ErrorHandler;
import com.lx.sms.util.ToastUtil;
import com.lx.sms.view.RecycleViewDivider;

import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class AuditActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    AuditAdapter adapter;
    AuditInfo auditInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.recycle;
    }

    @Override
    protected void initView() {
        initToolBar(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AuditAdapter(null);
        recyclerview.addItemDecoration(new RecycleViewDivider(this
                , LinearLayoutManager.HORIZONTAL));
        adapter.setEmptyView(R.layout.empty_view, (ViewGroup) recyclerview.getParent());
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        getAuditListInfo();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        auditInfo = list.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable("audit_data", auditInfo);
        goActivity(LeaveDetailActivity.class, bundle);
    }

    List<AuditInfo> list;

    public void getAuditListInfo() {
        Header mheader = new Header(MyApplication.imei);
        BaseBean baseBean = new BaseBean();
        baseBean.setHeader(mheader);
        showWaitDialog("获取中...").setCancelable(false);
        subscription = Network.getUserAPI().getAuditListInfo("1", "10", baseBean.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponBean<Response<List<AuditInfo>>>>() {
                    @Override
                    public void onCompleted() {
                        hideWaitDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideWaitDialog();
                        ErrorHandler.getHttpException(AuditActivity.this, e, false);
                    }

                    @Override
                    public void onNext(ResponBean<Response<List<AuditInfo>>> bean) {
                        if ("0000".equals(bean.header.rspCode)) {
                            ToastUtil.showShort("获取成功");
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

    @Override
    protected void onResume() {
        super.onResume();
        getAuditListInfo();
    }
}




