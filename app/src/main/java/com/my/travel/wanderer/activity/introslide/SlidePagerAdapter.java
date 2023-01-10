package com.my.travel.wanderer.activity.introslide;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.LinkedList;
import java.util.List;


public class SlidePagerAdapter extends FragmentStatePagerAdapter {
//    private List<String> picList = new LinkedList<String>();
    private List<Integer> picList = new LinkedList<Integer>();

    public SlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return SlidePageFragment.newInstance(picList.get(i%SlideIntroductionActivity.IMAGES_SLIDER.length));
    }

    @Override
    public int getCount() {
        return SlideIntroductionActivity.MAX_PAGE_SLIDER;
    }

    public void addAll(List<Integer> picList) {
        this.picList = picList;
    }
}
