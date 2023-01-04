package com.my.travel.wanderer.activity.detail.seedoo;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import com.my.travel.wanderer.WandererApplication;
import com.my.travel.wanderer.activity.detail.DetailPlaceActivity;
import com.my.travel.wanderer.activity.introslide.SlideIntroductionActivity;
import com.my.travel.wanderer.data.AppConstants;
import com.my.travel.wanderer.data.AppState;
import com.my.travel.wanderer.model.FCity;
import com.my.travel.wanderer.model.Place;
import com.my.travel.wanderer.model.SavedItem;
import com.my.travel.wanderer.service.PlaceService;
import com.my.travel.wanderer.utils.FontUtils;
import com.my.travel.wanderer.utils.LoggerFactory;
import com.my.travel.wanderer.utils.Utils;
import com.bpackingapp.vietnam.travel.R;

/**
 * Created by phamngocthanh on 7/26/17.
 */

public class SeeDoHomeAdapter extends RecyclerView.Adapter<SeeDoViewHolder> {
    private ArrayList<Place> list;
    private int categoryTtype;
    FCity mFCity;
    public SeeDoHomeAdapter(ArrayList<Place> Data, int cateType, FCity fCity) {
        list = Data;
        this.categoryTtype = cateType;
        this.mFCity = fCity;
    }
    @Override
    public SeeDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_see_do_root, parent, false);
        SeeDoViewHolder holder = new SeeDoViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final SeeDoViewHolder holder,final int position) {

        list.get(position).setCategoryType(categoryTtype);

        holder.tvCityName.setText(list.get(position).getName());
        FontUtils.setFont(holder.tvCityName, FontUtils.TYPE_NORMAL);
        holder.tvCityDistance.setText(list.get(position).getAddress());
        FontUtils.setFont(holder.tvCityDistance, FontUtils.TYPE_NORMAL);
        holder.tvNumberComment.setText(list.get(position).getCommentCount()+"");
        FontUtils.setFont(holder.tvNumberComment, FontUtils.TYPE_NORMAL);
        holder.tvNumberLove.setText(Utils.countLove(list.get(position).getLoved()) +"");
        FontUtils.setFont(holder.tvNumberLove, FontUtils.TYPE_NORMAL);

        if(list.get(position).getFromprice() != null && list.get(position).getFromprice().length() > 0) {
            if(categoryTtype == AppConstants.CATEGORY_SLEEP_TYPE) {
                holder.tvCityFee.setText(list.get(position).getFromprice() + "$");
            } else if(categoryTtype == AppConstants.CATEGORY_SEE_DO_TYPE) {
                holder.tvCityFee.setText(list.get(position).getFromprice());
            } else{
                holder.tvCityFee.setText(list.get(position).getFromprice());
            }
        } else {
            holder.tvCityFee.setText("Free");
        }

        FontUtils.setFont(holder.tvCityFee, FontUtils.TYPE_NORMAL);

        if(list.get(position).getPhotos().get(0).contains("http")) {
            ImageLoader.getInstance().displayImage(list.get(position).getPhotos().get(0), holder.imvCityThumb, WandererApplication.defaultOptions);
        } else {
            holder.imvCityThumb.setImageResource(R.drawable.img_default);
        }
        holder.imvCityThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = DetailPlaceActivity.createIntent(holder.imvCityThumb.getContext());
                intent.putExtra(AppConstants.KEY_INTENT_CITY_KEY, mFCity.getCityKey());
                intent.putExtra(AppConstants.KEY_INTENT_PLACE, list.get(position));
                intent.putExtra(AppConstants.KEY_INTENT_CATE_TYPE, categoryTtype);
                holder.imvCityThumb.getContext().startActivity(intent);
                ((Activity) holder.imvCityThumb.getContext()).overridePendingTransition(R.anim.activity_enter_back, R.anim.activity_exit_back);
            }
        });

        if(WandererApplication.getTipApplication().realmDB.getSavedItem(list.get(position).getPlaceKey()) != null){
            holder.imvSaveStatus.setImageResource(R.drawable.icon_favorited);
        } else {
            holder.imvSaveStatus.setImageResource(R.drawable.icon_favorite);
        }



        holder.imvSaveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(WandererApplication.getTipApplication().realmDB.getSavedItem(list.get(position).getPlaceKey()) != null){
                    Toast.makeText(holder.imvSaveStatus.getContext(), "Removed", Toast.LENGTH_SHORT).show();
                    WandererApplication.getTipApplication().realmDB.removeSavedItem(list.get(position).getPlaceKey());
                    holder.imvSaveStatus.setImageResource(R.drawable.icon_favorite);
                } else {
                    SavedItem savedItem = new SavedItem(list.get(position), mFCity.getCityKey());
                    savedItem.setSaveId(list.get(position).getPlaceKey());
                    savedItem.setCategoryType(categoryTtype);
                    WandererApplication.getTipApplication().realmDB.updateSavedItem(savedItem);
                    Toast.makeText(holder.imvSaveStatus.getContext(), "Saved", Toast.LENGTH_SHORT).show();
                    holder.imvSaveStatus.setImageResource(R.drawable.icon_favorited);
                }
            }
        });

        if (AppState.currentFireUser != null && list.get(position).getLoved() != null && list.get(position).getLoved().length() > 0 && list.get(position).getLoved().contains(AppState.currentFireUser.getUid())) {
            holder.imvLoveStatus.setImageResource(R.drawable.icon_loved);
        } else {
            holder.imvLoveStatus.setImageResource(R.drawable.icon_love);
        }

        holder.imvLoveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AppState.currentFireUser != null) {

                    final Place place = list.get(position);
                    if (place.getLoved() != null && place.getLoved().contains(AppState.currentFireUser.getUid())) {
                        place.setLoved(place.getLoved().replace("#" + AppState.currentFireUser.getUid(), ""));
                        holder.imvLoveStatus.setImageResource(R.drawable.icon_love);
                    } else {
                        if(place.getLoved() == null){
                            place.setLoved("");
                        }
                        place.setLoved(place.getLoved() + "#" + AppState.currentFireUser.getUid());
                        holder.imvLoveStatus.setImageResource(R.drawable.icon_loved);
                    }

                    holder.tvNumberLove.setText(Utils.countLove(place.getLoved()) + "");

                    if(mFCity != null) {
                        PlaceService placeService = new PlaceService(mFCity.getCityKey());
                        placeService.updatePlace(place, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    LoggerFactory.d("Update love success");
                                    holder.tvNumberLove.setText(Utils.countLove(place.getLoved()) + "");
                                    if (place.getLoved().contains(AppState.currentFireUser.getUid())) {
                                        holder.imvLoveStatus.setImageResource(R.drawable.icon_loved);
                                    } else {
                                        holder.imvLoveStatus.setImageResource(R.drawable.icon_love);
                                    }
                                } else {
                                    LoggerFactory.d("Update love failure");
                                }
                            }
                        });
                    }
                } else {
                    SlideIntroductionActivity.gotoLoginWindow(view.getContext());
                }
            }
        });
        holder.llTimeOpening.setVisibility(View.GONE);
        holder.llTourInfo.setVisibility(View.GONE);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}