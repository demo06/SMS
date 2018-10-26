package com.lx.sms.adapter;

import android.graphics.Color;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lx.sms.R;
import com.lx.sms.bean.AuditInfo;

import java.util.List;


public class AuditAdapter extends BaseQuickAdapter<AuditInfo, BaseViewHolder> {
    public AuditAdapter(List<AuditInfo> data) {
        super(R.layout.item_audit, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, AuditInfo item) {
        if (!"".equals(item.remark) && item.remark != null) {
            if(!"同意".equals(item.remark)){
                helper.getView(R.id.item_remark).setVisibility(View.VISIBLE);
                helper.setText(R.id.item_remark, "拒绝原因：" + item.remark);
            }
        }
        helper.setText(R.id.item_title, item.lawOfferName + "的请假")
                .setText(R.id.item_type, "请假类型：" + item.leaveType)
                .setText(R.id.item_start, "开始时间：" + item.beginDate)
                .setText(R.id.item_end, "结束时间：" + item.endDate)
                .setText(R.id.item_day, "请假天数：" + item.preLeaveHours)
                .setText(R.id.item_state, item.approvalStatusName)
                .setText(R.id.item_reason, "请假原因：" + item.reason)
                .addOnClickListener(R.id.relativelayout);

    }

}
