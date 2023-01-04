package com.my.travel.wanderer.activity.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.my.travel.wanderer.WandererApplication;
import com.my.travel.wanderer.activity.saved.SpinnerCategoryAdapter;
import com.my.travel.wanderer.data.AppConstants;
import com.my.travel.wanderer.model.CategoryInfo;
import com.my.travel.wanderer.model.FCity;
import com.my.travel.wanderer.model.Place;
import com.my.travel.wanderer.service.ChangeEventListener;
import com.my.travel.wanderer.service.PlaceService;
import com.my.travel.wanderer.utils.LoggerFactory;
import com.my.travel.wanderer.utils.Utils;
import com.bpackingapp.vietnam.travel.R;

public class SearchActivity extends AppCompatActivity {
    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, SearchActivity.class);
        return in;
    }

    public static final String SEARCH_TYPE = "SEARCH_TYPE";

    public static final int SEARCH_TYPE_CITY = 1;
    public static final int SEARCH_TYPE_PLACE = 2;
    public static final int SEARCH_TYPE_SAVED = 3;

    int searchType = SEARCH_TYPE_CITY;
    private SearchCardsAdapter mSearchCardsAdapter;
    private SearchCityCardsAdapter searchCityCardsAdapter;
    RecyclerView listviewSearchResult;
    TextView topbarTitle;

    Context mContext;
    AppCompatEditText topbarEdittextSearch;
    CustomSpinner spinner;
    TextView tvSpinner;
    public PlaceService placeService;
    List<CategoryInfo> categoryInfoList = new LinkedList<>();
    HashMap<String, CategoryInfo> hashMapCategoryInfo = new HashMap<>();
    List<CategoryInfo> categoryInfoCleaned = new LinkedList<>();
    String cityKey;
    FCity mFCity;
    int selectedCategoryType = AppConstants.CATEGORY_ALL_TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        if (getIntent() != null) {
            if (getIntent().hasExtra(SEARCH_TYPE)) {
                searchType = getIntent().getIntExtra(SEARCH_TYPE, SEARCH_TYPE_CITY);
            }
            if (getIntent().hasExtra(AppConstants.KEY_INTENT_CITY)) {
                mFCity = (FCity) getIntent().getSerializableExtra(AppConstants.KEY_INTENT_CITY);
            }

            if (getIntent().hasExtra(AppConstants.KEY_INTENT_CITY_KEY)) {
                cityKey = getIntent().getStringExtra(AppConstants.KEY_INTENT_CITY_KEY);
                if (mFCity== null) {
                    mFCity = WandererApplication.getTipApplication().cityService.getCityByKey(cityKey);
                }
            }
        }
        initView();
        initController();
        Utils.setStatusBarColor(SearchActivity.this, 0);
    }

    private void initView() {

        topbarTitle= (TextView) findViewById(R.id.topbarTitle);


        listviewSearchResult = (RecyclerView) findViewById(R.id.listviewSearchResult);
        listviewSearchResult.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(mContext);
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listviewSearchResult.setLayoutManager(MyLayoutManager);

        if (searchType == SEARCH_TYPE_PLACE) {
            mSearchCardsAdapter = new SearchCardsAdapter(this, mFCity, searchType);
            // Add the sticky headers decoration
            StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mSearchCardsAdapter);
            listviewSearchResult.addItemDecoration(headersDecor);
            listviewSearchResult.setAdapter(mSearchCardsAdapter);

            // Add decoration for dividers between list items
            listviewSearchResult.addItemDecoration(new DividerDecoration(this));
            topbarTitle.setVisibility(View.GONE);

        } else if (searchType == SEARCH_TYPE_SAVED) {

            topbarTitle.setVisibility(View.VISIBLE);
            topbarTitle.setText("Saved");

            mSearchCardsAdapter = new SearchCardsAdapter(this, mFCity, searchType);
            // Add the sticky headers decoration
            StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mSearchCardsAdapter);
            listviewSearchResult.addItemDecoration(headersDecor);
            listviewSearchResult.setAdapter(mSearchCardsAdapter);

            // Add decoration for dividers between list items
            listviewSearchResult.addItemDecoration(new DividerDecoration(this));

            findViewById(R.id.topbarEdittextSearch).setVisibility(View.GONE);
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                view.clearFocus();
            }

            mSearchCardsAdapter.setSearchAdapterCallBack(new SearchCardsAdapter.SearchAdapterCallBack() {
                @Override
                public void onRemove(int position) {
                    if (placeList.size() > position) {
                        placeList.remove(position);
                    }
                }

                @Override
                public void onRemovePlace(String placeId) {
                    if (seavedPlaceList != null && seavedPlaceList.size() > 0) {
                        for (int i = seavedPlaceList.size()-1; i >= 0; i--) {
                            if (seavedPlaceList.get(i).getPlaceKey().equalsIgnoreCase(placeId)) {
                                seavedPlaceList.remove(i);
                            }
                        }
                    }
                }
            });

        } else {
            topbarTitle.setVisibility(View.GONE);
            searchCityCardsAdapter = new SearchCityCardsAdapter(this);
            listviewSearchResult.setAdapter(searchCityCardsAdapter);
        }

        findViewById(R.id.topbarLeftBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvSpinner = (TextView) findViewById(R.id.tvSpinner);
        spinner = (CustomSpinner) findViewById(R.id.spinner);

        topbarEdittextSearch = (AppCompatEditText) findViewById(R.id.topbarEdittextSearch);
        topbarEdittextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LoggerFactory.d("beforeTextChanged:" + charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LoggerFactory.d("onTextChanged:" + charSequence.toString());
                search();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                LoggerFactory.d("afterTextChanged:" + editable.toString());
            }
        });

        if (searchType == SEARCH_TYPE_CITY) {
            topbarEdittextSearch.setHint("Search cities");
            spinner.setVisibility(View.GONE);
            tvSpinner.setVisibility(View.GONE);
        } else if (searchType == SEARCH_TYPE_PLACE) {
            topbarEdittextSearch.setHint("Search");
            tvSpinner.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);

            SpinnerCategoryAdapter spinnerCategoryAdapter = new SpinnerCategoryAdapter(mContext);
            spinnerCategoryAdapter.setSpinnerItemSelectListener(new SpinnerCategoryAdapter.SpinnerItemSelectListener() {
                @Override
                public void onItemSelected(int position) {
                    LoggerFactory.d("spinner select item", categoryInfoCleaned.get(position).getName());
                    tvSpinner.setText(categoryInfoCleaned.get(position).getName().replaceAll("\r\n|\n", "").replace("\n", "").replace("\r", "").trim().replaceAll("[\n\r]", ""));
                    spinner.onDetachedFromWindow();
                    selectedCategoryType = categoryInfoCleaned.get(position).getType();
                    search();
                }
            });
            spinner.setAdapter(spinnerCategoryAdapter);

            categoryInfoList = WandererApplication.getTipApplication().categoryService.getListCategoryInfoOfCity(cityKey);

            CategoryInfo cateAll = new CategoryInfo();
            cateAll.setName("All");
            cateAll.setType(AppConstants.CATEGORY_ALL_TYPE);
            categoryInfoCleaned.add(cateAll);

            for (int i = 0; i < categoryInfoList.size(); i++) {
                if (categoryInfoList.get(i).getType() != AppConstants.CATEGORY_EMERGENCY_TYPE
                        && categoryInfoList.get(i).getType() != AppConstants.CATEGORY_TRANSPORT_TYPE
                        && categoryInfoList.get(i).getType() != AppConstants.CATEGORY_CITY_INFO_TYPE
//                        && categoryInfoList.get(i).getType() != AppConstants.CATEGORY_STORIES_TYPE
                        ) {
                    categoryInfoCleaned.add(categoryInfoList.get(i));
                }
            }

            spinnerCategoryAdapter.setItems(categoryInfoCleaned);


            placeService = new PlaceService(cityKey);
            placeService.setOnChangedListener(new ChangeEventListener() {
                @Override
                public void onChildChanged(EventType type, int index, int oldIndex) {

                }

                @Override
                public void onDataChanged() {
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        } else if (searchType == SEARCH_TYPE_SAVED) {
            topbarEdittextSearch.setHint("Saved");
            tvSpinner.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);

            SpinnerCategoryAdapter spinnerCategoryAdapter = new SpinnerCategoryAdapter(mContext);
            spinnerCategoryAdapter.setSpinnerItemSelectListener(new SpinnerCategoryAdapter.SpinnerItemSelectListener() {
                @Override
                public void onItemSelected(int position) {
                    LoggerFactory.d("spinner select item", categoryInfoCleaned.get(position).getName());
                    tvSpinner.setText(categoryInfoCleaned.get(position).getName().replaceAll("\r\n|\n", "").replace("\n", "").replace("\r", "").replace("", ""));
                    spinner.onDetachedFromWindow();
                    selectedCategoryType = categoryInfoCleaned.get(position).getType();
                    search();
                }
            });

            selectedCategoryType = AppConstants.CATEGORY_ALL_TYPE;

            spinner.setAdapter(spinnerCategoryAdapter);
            placeList = WandererApplication.getTipApplication().realmDB.getSavedPlaceList(mFCity.getCityKey());
            seavedPlaceList = WandererApplication.getTipApplication().realmDB.getSavedPlaceList(mFCity.getCityKey());
            for (int i = 0; i < placeList.size(); i++) {
                Place place = placeList.get(i);
                if (place.getCategories() != null && place.getCategories().size() > 0) {
                    hashMapCategoryInfo.put(place.getCategories().get(0), WandererApplication.getTipApplication().categoryInfoService.getCategoryByKey(place.getCategories().get(0)));
                }
            }


            categoryInfoList.addAll(hashMapCategoryInfo.values());

            CategoryInfo cateAll = new CategoryInfo();
            cateAll.setName("All");
            cateAll.setType(AppConstants.CATEGORY_ALL_TYPE);
            categoryInfoCleaned.add(cateAll);

            for (int i = 0; i < categoryInfoList.size(); i++) {
                if (categoryInfoList.get(i).getType() != AppConstants.CATEGORY_EMERGENCY_TYPE
                        && categoryInfoList.get(i).getType() != AppConstants.CATEGORY_TRANSPORT_TYPE
                        && categoryInfoList.get(i).getType() != AppConstants.CATEGORY_CITY_INFO_TYPE
//                        && categoryInfoList.get(i).getType() != AppConstants.CATEGORY_STORIES_TYPE
                        ) {
                    categoryInfoCleaned.add(categoryInfoList.get(i));
                }
            }

            spinnerCategoryAdapter.setItems(categoryInfoCleaned);
            search();
        }

    }

    List<Place> placeList = new LinkedList<>();
    List<Place> seavedPlaceList = new LinkedList<>();

    private void search() {
        if (searchType == SEARCH_TYPE_CITY) {
            if (topbarEdittextSearch.getText().toString().length() > 0) {
                List<FCity> fCities = WandererApplication.getTipApplication().cityService.searchCity(topbarEdittextSearch.getText().toString().toLowerCase());
                searchCityCardsAdapter.setfCityList(fCities);
            } else {
                searchCityCardsAdapter.setfCityList(null);
            }
            searchCityCardsAdapter.notifyDataSetChanged();
        } else if (searchType == SEARCH_TYPE_PLACE) {
            placeList.clear();
            if (topbarEdittextSearch.getText().toString().length() > 0) {
                placeList.addAll(placeService.searchPlace(topbarEdittextSearch.getText().toString().toLowerCase(), selectedCategoryType));
                mSearchCardsAdapter.setPlaceList(placeList);
            }
            mSearchCardsAdapter.mFCity = mFCity;
            mSearchCardsAdapter.notifyDataSetChanged();
        } else if (searchType == SEARCH_TYPE_SAVED) {
            searchSaved();
        }
    }


    private void searchSaved() {
        placeList.clear();
        for (int i = 0; i < seavedPlaceList.size(); i++) {
            if(selectedCategoryType == AppConstants.CATEGORY_ALL_TYPE) {
                placeList.add(seavedPlaceList.get(i));
            } else {
                if (seavedPlaceList.get(i).getCategories() != null && seavedPlaceList.get(i).getCategories().size() > 0
                    && seavedPlaceList.get(i).getCategories().get(0).equalsIgnoreCase(WandererApplication.getTipApplication().categoryInfoService.getCategoryKeyByType(selectedCategoryType))) {
                    placeList.add(seavedPlaceList.get(i));
                }
            }
        }

        mSearchCardsAdapter.setPlaceList(placeList);
        mSearchCardsAdapter.notifyDataSetChanged();
    }

    private void initController() {

    }
}
