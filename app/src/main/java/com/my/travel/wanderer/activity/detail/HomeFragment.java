package com.my.travel.wanderer.activity.detail;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.my.travel.wanderer.WandererApplication;
import com.my.travel.wanderer.activity.askNshare.AskShareActivity;
import com.my.travel.wanderer.activity.detail.TipView.TipAdapter;
import com.my.travel.wanderer.activity.detail.seedoo.SeeDoHomeAdapter;
import com.my.travel.wanderer.data.AppConstants;
import com.my.travel.wanderer.model.CategoryInfo;
import com.my.travel.wanderer.model.Place;
import com.my.travel.wanderer.utils.FontUtils;
import com.my.travel.wanderer.utils.LoggerFactory;
import com.my.travel.wanderer.utils.Utils;

import com.bpackingapp.vietnam.travel.R;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by phamngocthanh on 7/26/17.
 */

public class HomeFragment extends Fragment {
    String TAG = HomeFragment.class.getSimpleName();
    public static final String TRANSPORT_DATA = "TRANSPORT_DATA";
    public static final String EMERGENCY_DATA = "EMERGENCY_DATA";
    public static final String CITY_INFO_DATA = "CITY_INFO_DATA";
    ArrayList<Place> listCategory1 = new ArrayList<>();
    ArrayList<Place> listCategory2 = new ArrayList<>();

    public static List<Place> transportData = new ArrayList<>();

    RecyclerView recyclerViewCategory1;
    RecyclerView recyclerViewCategory2;
    DetailTabsActivity detailTabsActivity;

    TextView tvCategoryName, tvViewAll;
    TextView tvCategoryName2, tvViewAll2;
    TextView tvGetToKnown;
    Button btnMask;

    public void setDetailTabsActivity(DetailTabsActivity detailTabsActivity) {
        this.detailTabsActivity = detailTabsActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_place_home, container, false);
        setCategory1View(view);
        setCategory2View(view);

        view.findViewById(R.id.btnMask).setVisibility(View.GONE);
        view.findViewById(R.id.btnMask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMask.setVisibility(View.GONE);
            }
        });
        view.findViewById(R.id.rlDetailGettingAround).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transportData = detailTabsActivity.placeService.getPlaceTransportInfo();
                if (transportData != null) {

                    Intent intentTransport = TransportActivity.createIntent(getContext());
                    intentTransport.putExtra(AppConstants.INTENT_CLASS, AppConstants.INTENT_CLASS_TRANSPORT);
                    startActivity(intentTransport);
                    getActivity().overridePendingTransition(R.anim.activity_enter_back, R.anim.activity_exit_back);
                } else {
                    Toast.makeText(getContext(), "Comming soon", Toast.LENGTH_LONG).show();
                }
            }
        });

        view.findViewById(R.id.rlPracticalEmergency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Place emergencyData = detailTabsActivity.placeService.getEmergencyPlace();
                if (emergencyData != null) {
                    Intent emergencyIntent = TransportActivity.createIntent(getContext()).putExtra(EMERGENCY_DATA, emergencyData);
                    emergencyIntent.putExtra(AppConstants.INTENT_CLASS, AppConstants.INTENT_CLASS_EMERGENCY);
                    startActivity(emergencyIntent);
                    getActivity().overridePendingTransition(R.anim.activity_enter_back, R.anim.activity_exit_back);
                } else {
                    Toast.makeText(getContext(), "Comming soon", Toast.LENGTH_LONG).show();

                }
            }
        });

        view.findViewById(R.id.rlPracticalGetToKnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Place cityInfoData = detailTabsActivity.placeService.getPlaceInfo();
                if (cityInfoData != null) {
                    Intent emergencyIntent = TransportActivity.createIntent(getContext()).putExtra(CITY_INFO_DATA, cityInfoData);
                    emergencyIntent.putExtra(AppConstants.INTENT_CLASS, AppConstants.INTENT_CLASS_CITY_INFO);
                    startActivity(emergencyIntent);
                    getActivity().overridePendingTransition(R.anim.activity_enter_back, R.anim.activity_exit_back);
                } else {
                    Toast.makeText(getContext(), "Comming soon", Toast.LENGTH_LONG).show();
                }
            }
        });

        view.findViewById(R.id.imvVisa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detailTabsActivity.mFCity.getBannerUrl()!= null && detailTabsActivity.mFCity.getBannerUrl().length() > 0) {
                    Utils.openWebsite(getContext(), detailTabsActivity.mFCity.getBannerUrl());
                }
            }
        });

        ImageView imvVisa = (ImageView) view.findViewById(R.id.imvVisa);
        if (detailTabsActivity.mFCity.getBannerPhotoUrl()!= null && detailTabsActivity.mFCity.getBannerPhotoUrl().length() > 0) {
            ImageLoader.getInstance().displayImage(detailTabsActivity.mFCity.getBannerPhotoUrl(), imvVisa, WandererApplication.defaultOptions);
            imvVisa.setVisibility(View.VISIBLE);
        } else {
            imvVisa.setVisibility(View.GONE);
        }

        tvCategoryName = (TextView) view.findViewById(R.id.tvCategoryName);
        FontUtils.setFont(tvCategoryName, FontUtils.TYPE_NORMAL);
        tvViewAll = (TextView) view.findViewById(R.id.tvViewAll);
        FontUtils.setFont(tvViewAll, FontUtils.TYPE_NORMAL);

        tvCategoryName2 = (TextView) view.findViewById(R.id.tvCategoryName2);
        FontUtils.setFont(tvCategoryName2, FontUtils.TYPE_NORMAL);
        tvViewAll2 = (TextView) view.findViewById(R.id.tvViewAll2);
        FontUtils.setFont(tvCategoryName2, FontUtils.TYPE_NORMAL);

        tvGetToKnown = (TextView) view.findViewById(R.id.tvGetToKnown);
        FontUtils.setFont(tvGetToKnown, FontUtils.TYPE_NORMAL);

        initData();
        return view;
    }

    public void setCategory1View(View view) {
        recyclerViewCategory1 = (RecyclerView) view.findViewById(R.id.cardView);
        recyclerViewCategory1.setHasFixedSize(true);
        CustomLinearLayoutManager MyLayoutManager = new CustomLinearLayoutManager(getActivity());
        MyLayoutManager.setSmoothScrollbarEnabled(false);

        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewCategory1.setLayoutManager(MyLayoutManager);
        recyclerViewCategory1.setNestedScrollingEnabled(false);

        recyclerViewCategory1.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                LoggerFactory.d("recyclerViewCategory1 onFling....");
                return false;
            }
        });
        recyclerViewCategory1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
    }

    public void setCategory2View(View view) {
        recyclerViewCategory2 = (RecyclerView) view.findViewById(R.id.cardView2);
        recyclerViewCategory2.setHasFixedSize(true);
        CustomLinearLayoutManager MyLayoutManager = new CustomLinearLayoutManager(getActivity());
        MyLayoutManager.setSmoothScrollbarEnabled(false);
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewCategory2.setLayoutManager(MyLayoutManager);
        recyclerViewCategory2.setNestedScrollingEnabled(false);
    }

    List<CategoryInfo> categoryInfoList;

    private void initData() {
        listCategory1.clear();
        listCategory2.clear();

        categoryInfoList = detailTabsActivity.categoryInfoCleaned;

        if (categoryInfoList != null && categoryInfoList.size() > 1) {
            final CategoryInfo cate0 = categoryInfoList.get(0);
            tvCategoryName.setText(cate0.getName());
            tvViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cate0.getType() == AppConstants.CATEGORY_TIP_TYPE) {
                        Intent intent = AskShareActivity.createIntent(getContext());
                        intent.putExtra(AppConstants.KEY_INTENT_CITY_KEY, detailTabsActivity.cityKey);

                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.activity_enter_back, R.anim.activity_exit_back);
                    } else {
                        detailTabsActivity.mViewPager.setCurrentItem(detailTabsActivity.getCategoryPageIndex(cate0.getType()));
                    }
                }
            });

            final CategoryInfo cate1 = categoryInfoList.get(1);
            tvCategoryName2.setText(cate1.getName());
            tvViewAll2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cate1.getType() == AppConstants.CATEGORY_TIP_TYPE) {
                        Intent intent = AskShareActivity.createIntent(getContext());
                        intent.putExtra(AppConstants.KEY_INTENT_CITY_KEY, detailTabsActivity.cityKey);

                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.activity_enter_back, R.anim.activity_exit_back);
                    } else {
                        detailTabsActivity.mViewPager.setCurrentItem(detailTabsActivity.getCategoryPageIndex(cate1.getType()));
                    }
                }
            });


            String category0Key = WandererApplication.getTipApplication().categoryInfoService.getCategoryKeyByType(cate0.getType());
            String category1Key = WandererApplication.getTipApplication().categoryInfoService.getCategoryKeyByType(cate1.getType());
            for (int i = 0; i < detailTabsActivity.placeService.getCount(); i++) {

                try {
                    Place place = detailTabsActivity.placeService.getItem(i).getValue(Place.class);
                    place.setPlaceKey(detailTabsActivity.placeService.getItem(i).getKey());
                    if (place != null && place.getCategories() != null
                            && place.getCategories().size() > 0
                            && place.getCategories().toString().contains(category0Key) && !place.isDeactived()) {
                        listCategory1.add(place);
                    } else if (place != null && place.getCategories() != null
                            && place.getCategories().size() > 0
                            && place.getCategories().toString().contains(category1Key)
                            && !place.isDeactived()) {
                        listCategory2.add(place);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (cate0.getType() == AppConstants.CATEGORY_TIP_TYPE) {
                Collections.sort(listCategory1, new Place.PlaceComparatorCreateDate());
            } else {
                Collections.sort(listCategory1, new Place.PlaceComparatorPriority());
            }
            if (listCategory1.size() > 5) {
                ArrayList<Place> listCategory1a = new ArrayList<>();
                listCategory1a.addAll(listCategory1.subList(0, 5));
                listCategory1 = listCategory1a;
            }

            if (cate1.getType() == AppConstants.CATEGORY_TIP_TYPE) {
                Collections.sort(listCategory2, new Place.PlaceComparatorCreateDate());
            } else {
                Collections.sort(listCategory2, new Place.PlaceComparatorPriority());
            }

            if (listCategory2.size() > 5) {
                ArrayList<Place> listCategory2a = new ArrayList<>();
                listCategory2a.addAll(listCategory2.subList(0, 5));
                listCategory2 = listCategory2a;
            }

            switch (cate0.getType()) {
                case AppConstants.CATEGORY_TIP_TYPE:
                    recyclerViewCategory1.setAdapter(new TipAdapter(listCategory1, detailTabsActivity.mFCity));
                    break;
                default:
                    recyclerViewCategory1.setAdapter(new SeeDoHomeAdapter(listCategory1, cate0.getType(), detailTabsActivity.mFCity));
            }

            switch (cate1.getType()) {
                case AppConstants.CATEGORY_TIP_TYPE:
                    recyclerViewCategory2.setAdapter(new TipAdapter(listCategory2, detailTabsActivity.mFCity));
                    break;
                default:
                    recyclerViewCategory2.setAdapter(new SeeDoHomeAdapter(listCategory2, cate1.getType(), detailTabsActivity.mFCity));
            }
        }


        tvGetToKnown.setText("Get to know " + detailTabsActivity.mFCity.getName());
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

        LoggerFactory.d(TAG, "onResume");

        if (recyclerViewCategory1 != null && recyclerViewCategory1.getAdapter() != null) {
            recyclerViewCategory1.getAdapter().notifyDataSetChanged();
        }
        if (recyclerViewCategory2 != null && recyclerViewCategory2.getAdapter() != null) {
            recyclerViewCategory2.getAdapter().notifyDataSetChanged();
        }

    }


}