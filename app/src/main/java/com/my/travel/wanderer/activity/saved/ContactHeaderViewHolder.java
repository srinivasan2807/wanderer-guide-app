/*
 * Copyright (c) 2017.
 *
 * Created by Pham Ngoc Thanh
 * Contact via Email: ngocthanh2207@gmail.com
 */

package com.my.travel.wanderer.activity.saved;

import android.view.View;
import android.widget.TextView;

import com.my.travel.wanderer.utils.FontUtils;
import com.bpackingapp.vietnam.travel.R;

/**
 * Created by phamngocthanh on 8/8/17.
 */

public class ContactHeaderViewHolder {
    TextView tvHeaderName;

    public ContactHeaderViewHolder(View itemView) {
        tvHeaderName = (TextView) itemView.findViewById(R.id.tvHeaderName);
        FontUtils.setFont(itemView.findViewById(R.id.tvHeaderName));
    }
}