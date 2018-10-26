package com.lx.sms.adapter;

import android.graphics.Color;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lx.sms.R;
import com.lx.sms.bean.SignBean;
import com.lx.sms.net.Constant;

import java.util.List;


public class SignAdapter extends BaseQuickAdapter<SignBean, BaseViewHolder> {
    public SignAdapter(List<SignBean> data) {
        super(R.layout.item_sign, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, SignBean item) {
        helper.setText(R.id.item_sign_day, item.signFlagName)
                .setText(R.id.item_sign_time, item.createDate.substring(0,item.createDate.length()-3));
        Glide.with(helper.itemView.getContext()).load(Constant.IMG_URL+item.photoUrl)
                .placeholder(R.drawable.default_img)
                .into((ImageView) helper.getView(R.id.item_sign_img));
    }


}
