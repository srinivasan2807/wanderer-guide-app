package com.my.travel.wanderer.activity.detail;

import android.content.Context;
import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import com.my.travel.wanderer.activity.detail.placeFragment.DescriptionFragment;
import com.my.travel.wanderer.activity.detail.placeFragment.FacilitiesFragment;
import com.my.travel.wanderer.data.AppConstants;
import com.my.travel.wanderer.model.Place;
import com.my.travel.wanderer.utils.FontUtils;
import com.my.travel.wanderer.utils.Utils;
import com.bpackingapp.vietnam.travel.R;

public class PlaceDescriptionActivity extends AppCompatActivity {
    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, PlaceDescriptionActivity.class);
        return in;
    }

    Place place;
    TextView tvDescription, topbarTitle;
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_description);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        topbarTitle = (TextView) findViewById(R.id.topbarTitle);
        setTitle("Description");

        findViewById(R.id.topbarLeftBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((TextView) findViewById(R.id.topbarTitle)).setText("Description");

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        FontUtils.setFont(tabLayout, FontUtils.TYPE_NORMAL);
        Utils.setStatusBarColor(PlaceDescriptionActivity.this, 0);
        getIntentData();
    }

    private void getIntentData() {
        if (getIntent() != null) {
            if (getIntent().hasExtra(AppConstants.KEY_INTENT_PLACE)) {
                place = (Place) getIntent().getSerializableExtra(AppConstants.KEY_INTENT_PLACE);
                if (place != null) {
                    tvDescription.setText(place.getDescription());
                    updateLinkify(tvDescription);

                    // primary sections of the activity.
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                    mViewPager.setAdapter(mSectionsPagerAdapter);

                    topbarTitle.setText(place.getName());
                }
            }
        }
    }

    List<String> listInfo = new LinkedList<>();

    private int getNumberDetailInfo() {
        listInfo.clear();

        if (place.getDescription() != null && place.getDescription().length() > 0) {
            listInfo.add("Description,"+DescriptionFragment.DESCRIPTION_TYPE_DES);
        }

        if (place.getFacilities() != null && place.getFacilities().size() > 0) {
            listInfo.add("Facilities,"+DescriptionFragment.DESCRIPTION_TYPE_FACILITY);
        }

        if (place.getThingstonote() != null && place.getThingstonote().length() > 0) {
            listInfo.add("Note,"+DescriptionFragment.DESCRIPTION_TYPE_NOTE);
        }


        return listInfo.size();
    }

    private void updateLinkify(TextView textView) {
//        Linkify.addLinks(textView, "BlundellApps.com", "http://www.BlundellApps.com");
        Linkify.addLinks(textView, Linkify.WEB_URLS);
        Linkify.addLinks(textView, Linkify.ALL);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (DescriptionFragment.DESCRIPTION_TYPE_DES == Integer.parseInt(listInfo.get(position).split(",")[1])) {
                DescriptionFragment homeFragment = new DescriptionFragment();
                homeFragment.setDescriptionType(DescriptionFragment.DESCRIPTION_TYPE_DES);
                homeFragment.setPlace(place);
                return homeFragment;
            } else if (DescriptionFragment.DESCRIPTION_TYPE_FACILITY == Integer.parseInt(listInfo.get(position).split(",")[1])) {
                FacilitiesFragment homeFragment = new FacilitiesFragment();
                homeFragment.setPlace(place);
                return homeFragment;
            } else {
                DescriptionFragment homeFragment = new DescriptionFragment();
                homeFragment.setDescriptionType(DescriptionFragment.DESCRIPTION_TYPE_NOTE);
                homeFragment.setPlace(place);
                return homeFragment;
            }
        }

        @Override
        public int getCount() {
            return getNumberDetailInfo();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return listInfo.get(position).split(",")[0];
        }
    }
}
