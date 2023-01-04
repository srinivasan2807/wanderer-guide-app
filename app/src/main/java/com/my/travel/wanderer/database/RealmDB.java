/*
 * Copyright (c) 2017.
 *
 * Created by Pham Ngoc Thanh
 * Contact via Email: ngocthanh2207@gmail.com
 */

package com.my.travel.wanderer.database;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import com.my.travel.wanderer.data.AppConstants;
import com.my.travel.wanderer.data.AppSetting;
import com.my.travel.wanderer.model.Place;
import com.my.travel.wanderer.model.SavedItem;
import com.my.travel.wanderer.utils.MSharedPreferences;


public class RealmDB {
    private Realm realm;

    public static void init(Context context) {
        Realm.init(context);
        String namedb = MSharedPreferences.getInstance(context).getString(AppConstants.DICTIONARY_ABBR_DEFAIL, "temp") + ".db";
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(namedb)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public RealmDB() {
        this.realm = Realm.getDefaultInstance();
    }


    public void updateSavedItem(SavedItem recentWord) {
        try {
            if (realm == null || recentWord == null) return;
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(recentWord);
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SavedItem getSavedItem(String placeKey) {
        if (realm != null) {
            SavedItem result = realm.where(SavedItem.class).equalTo("saveId", placeKey).findFirst();
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void removeSavedItem(String placeKey) {
        try {
            if (realm != null) {
                SavedItem result = realm.where(SavedItem.class).equalTo("saveId", placeKey).findFirst();
                if (result != null) {
                    realm.beginTransaction();
                    result.deleteFromRealm();
                    realm.commitTransaction();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<SavedItem> getSavedItemList() {
        List<SavedItem> res = new ArrayList<>();
        if (realm != null) {
            RealmResults<SavedItem> results = realm.where(SavedItem.class)
                    .findAll();
//                    .findAllSorted("lastTime", Sort.DESCENDING);
            for (SavedItem recentWord : results) {
                res.add(recentWord);
                if (res.size() == AppSetting.LIMIT_SAVED_ITEM) break;
            }
        }
        return res;
    }

    public List<Place> getSavedPlaceList(String cityKey) {
        List<Place> res = new ArrayList<>();
        if (realm != null) {
            RealmResults<SavedItem> results;
            if (cityKey != null && cityKey.length() > 0) {
                results = realm.where(SavedItem.class)
                        .equalTo("cityKey", cityKey)
                        .findAll();
            } else {
                results = realm.where(SavedItem.class)
                        .findAll();
            }
            for (SavedItem recentWord : results) {
                if (!recentWord.isDeactived()) {
                    res.add(new Place(recentWord));
                }
                if (res.size() == AppSetting.LIMIT_SAVED_ITEM) break;
            }
        }
        return res;
    }
}
