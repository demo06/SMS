package com.lx.sms.net;

import com.lx.sms.R;
import com.lx.sms.bean.GeneralBean;

import java.util.ArrayList;
import java.util.List;


public class AppData {
    public AppData() {
    }

    public static List<GeneralBean> getIconListData() {
        List<GeneralBean> list = new ArrayList<>();
        list.add(new GeneralBean(R.mipmap.icon_sign, "打卡"));
        list.add(new GeneralBean(R.mipmap.icon_leave, "请假"));
        list.add(new GeneralBean(R.mipmap.icon_my_leave, "我的假条"));
        list.add(new GeneralBean(R.mipmap.icon_review, "审批"));
        return list;
    }





}
