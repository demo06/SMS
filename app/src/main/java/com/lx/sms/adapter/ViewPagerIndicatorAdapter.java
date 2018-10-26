package com.lx.sms.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

/**
 * 作者:Created by dwb on 2016/8/1.
 * 邮箱:demo06@126.com
 * QQ:290221978
 */
public class ViewPagerIndicatorAdapter extends FragmentPagerAdapter {
    private static final String TAG = "FragmentPagerAdapter";

    private ArrayList<PageInfo> pageInfos;
    private List<String> tagList = new ArrayList<>();
    FragmentManager fm;

    public ViewPagerIndicatorAdapter(FragmentManager fm, ArrayList<PageInfo> pageInfos) {
        super(fm);
        this.pageInfos = pageInfos;
        this.fm = fm;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        tagList.add(makeFragmentName(container.getId(), getItemId(position)));
        return super.instantiateItem(container, position);

    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }



    @Override
    public Fragment getItem(int position) {
        return pageInfos.get(position).getFragment();
    }


    @Override
    public int getCount() {
        return pageInfos.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageInfos.get(position).getTitle();
    }

    public static class PageInfo {
        private Fragment fragment;
        private String title;

        public PageInfo(Fragment fragment, String title) {
            this.fragment = fragment;
            this.title = title;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
