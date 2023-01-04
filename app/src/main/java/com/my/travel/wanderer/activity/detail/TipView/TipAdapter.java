package com.my.travel.wanderer.activity.detail.TipView;

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
import com.my.travel.wanderer.activity.introslide.SlideIntroductionActivity;
import com.my.travel.wanderer.activity.tip.TipActivity;
import com.my.travel.wanderer.data.AppConstants;
import com.my.travel.wanderer.data.AppState;
import com.my.travel.wanderer.model.FCity;
import com.my.travel.wanderer.model.Place;
import com.my.travel.wanderer.model.SavedItem;
import com.my.travel.wanderer.model.User;
import com.my.travel.wanderer.service.PlaceService;
import com.my.travel.wanderer.utils.FontUtils;
import com.my.travel.wanderer.utils.LoggerFactory;
import com.my.travel.wanderer.utils.Utils;
import com.bpackingapp.vietnam.travel.R;

/**
 * Created by phamngocthanh on 7/26/17.
 */

public class TipAdapter extends RecyclerView.Adapter<TipViewHolder> {
    private ArrayList<Place> list;
    FCity fCity;
    public TipAdapter(ArrayList<Place> Data, FCity fCity) {
        list = Data;
        this.fCity = fCity;
    }
    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_tip_root, parent, false);
        TipViewHolder holder = new TipViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final TipViewHolder holder, final int position) {
        if(list != null && list.get(position) != null) {
            LoggerFactory.d("TipAdapter:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            LoggerFactory.d(list.get(position).toString());

            User user = WandererApplication.getTipApplication().userService.getUserByEmail(list.get(position).getEmail());
            if (user != null) {
                LoggerFactory.d("=============== " + user.toString());
                holder.tvUserName.setText(user.getName());
            } else {
                holder.tvUserName.setText("---");
            }

            FontUtils.setFont(holder.tvUserName, FontUtils.TYPE_NORMAL);
//            if(list.get(position).getName().length() > 12) {
//                holder.tvUserName.setText(list.get(position).getName().substring(0, 10));
//            } else {
//                holder.tvUserName.setText(list.get(position).getName());
//            }

            holder.tvComment.setText(list.get(position).getDescription());
            FontUtils.setFont(holder.tvComment, FontUtils.TYPE_NORMAL);

            holder.tvNumberComment.setText(""+list.get(position).getCommentCount() + "");
            FontUtils.setFont(holder.tvNumberComment, FontUtils.TYPE_NORMAL);
            holder.tvNumberLove.setText(Utils.countLove(list.get(position).getLoved()) +"");
            FontUtils.setFont(holder.tvNumberLove, FontUtils.TYPE_NORMAL);

            if (WandererApplication.getTipApplication().realmDB.getSavedItem(list.get(position).getPlaceKey()) != null) {
                holder.imvSaveStatus.setImageResource(R.drawable.icon_favorited);
            } else {
                holder.imvSaveStatus.setImageResource(R.drawable.icon_favorite_gray);
            }

            holder.imvSaveStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (WandererApplication.getTipApplication().realmDB.getSavedItem(list.get(position).getPlaceKey()) != null) {
                        Toast.makeText(holder.imvSaveStatus.getContext(), "Removed", Toast.LENGTH_SHORT).show();
                        WandererApplication.getTipApplication().realmDB.removeSavedItem(list.get(position).getPlaceKey());
                        holder.imvSaveStatus.setImageResource(R.drawable.icon_favorite_gray);
                    } else {
                        SavedItem savedItem = new SavedItem(list.get(position), fCity.getCityKey());
                        savedItem.setSaveId(list.get(position).getPlaceKey());
                        savedItem.setCategoryType(AppConstants.CATEGORY_TIP_TYPE);
                        WandererApplication.getTipApplication().realmDB.updateSavedItem(savedItem);
                        Toast.makeText(holder.imvSaveStatus.getContext(), "Saved", Toast.LENGTH_SHORT).show();
                        holder.imvSaveStatus.setImageResource(R.drawable.icon_favorited);
                    }
                }
            });

            if (AppState.currentFireUser != null && list.get(position).getLoved() != null &&  list.get(position).getLoved().contains(AppState.currentFireUser.getUid())) {
                holder.imvLoveStatus.setImageResource(R.drawable.icon_loved);
            } else {
                holder.imvLoveStatus.setImageResource(R.drawable.icon_love);
            }

            holder.imvLoveStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(AppState.currentFireUser != null) {
                        final Place place = list.get(position);
                        if (place.getLoved() != null &&  place.getLoved().contains(AppState.currentFireUser.getUid())) {
                            place.setLoved(place.getLoved().replace("#" + AppState.currentFireUser.getUid(), ""));
                            holder.imvLoveStatus.setImageResource(R.drawable.icon_love);
                        } else {
                            if(place.getLoved() == null){
                                place.setLoved("");
                            }

                            place.setLoved(place.getLoved() + "#" + AppState.currentFireUser.getUid());
                            holder.imvLoveStatus.setImageResource(R.drawable.icon_loved);
                        }

                        holder.tvNumberLove.setText(Utils.countLove(place.getLoved())+"");

                        if(fCity != null) {
                            PlaceService placeService = new PlaceService(fCity.getCityKey());
                            placeService.updatePlace(place, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        LoggerFactory.d("Update love success");
                                        holder.tvNumberLove.setText(Utils.countLove(place.getLoved())+"");
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

            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = TipActivity.createIntent(holder.card_view.getContext());
                    intent.putExtra(AppConstants.KEY_INTENT_PLACE_KEY, list.get(position).getPlaceKey());
                    intent.putExtra(AppConstants.KEY_INTENT_CITY_KEY, fCity.getCityKey());
                    intent.putExtra(AppConstants.KEY_INTENT_PLACE, list.get(position));

                    if(fCity != null) {
                        intent.putExtra(AppConstants.KEY_INTENT_CITY_NAME, fCity.getName());
                    }

                    holder.card_view.getContext().startActivity(intent);
                }
            });



            if(user != null && user.getAvatar() != null && user.getAvatar().length() > 0 && user.getAvatar().contains("http")) {
                ImageLoader.getInstance().displayImage(user.getAvatar(), holder.imvUserAvatar, WandererApplication.defaultOptionsAvatar);
            }

            if(list.get(position).getUpdatedAt() > 0) {
                holder.tvCityDescription.setText(Utils.convertDateHistory(list.get(position).getUpdatedAt(), holder.tvCityDescription.getContext()));
            }else {
                if (list.get(position).getCreatedAt() > 0) {
                    holder.tvCityDescription.setText(Utils.convertDateHistory(list.get(position).getCreatedAt(), holder.tvCityDescription.getContext()));
                } else {
                    holder.tvCityDescription.setText("");
                }
            }
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}