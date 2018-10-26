package com.lx.sms.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lx.sms.R;
import com.lx.sms.bean.GeneralBean;

import java.util.List;


public class GeneralAdapter extends BaseQuickAdapter<GeneralBean, BaseViewHolder> {
    public GeneralAdapter(List<GeneralBean> data) {
        super(R.layout.adapter_general_item, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, GeneralBean item) {
        helper.setText(R.id.item_name, item.title);
        Glide.with(helper.itemView.getContext()).load(item.img)
//                .placeholder(R.drawable.default_img)
                .into((ImageView) helper.getView(R.id.item_img));
    }
}
