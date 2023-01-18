package com.my.travel.wanderer.activity.bookticket;

import static com.my.travel.wanderer.data.AppConstants.BOOKING_TICKET_URL;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.my.travel.wanderer.data.AppConstants;
import com.my.travel.wanderer.utils.Utils;
import com.bpackingapp.vietnam.travel.R;

import java.util.Objects;

import io.realm.internal.Util;

public class BookTicketActivity extends AppCompatActivity {
    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, BookTicketActivity.class);
        return in;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ticket);
        initView();
        Utils.setStatusBarColor(BookTicketActivity.this, 0);
    }

    private void initView(){
        int color = getColor(R.color.colorPrimary);
        int secondaryColor = getColor(R.color.colorPrimaryDark);
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        CustomTabColorSchemeParams defaultColors = new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(color)
                .setSecondaryToolbarColor(secondaryColor)
                .build();
        intentBuilder.setDefaultColorSchemeParams(defaultColors);
        intentBuilder.setShowTitle(false);
        intentBuilder.setUrlBarHidingEnabled(false);
        intentBuilder.setCloseButtonIcon(toBitmap(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.btn_back))));
        intentBuilder.setStartAnimations(this, R.anim.svslide_in_top, R.anim.svslide_out_bottom);
        intentBuilder.build().launchUrl(this, Uri.parse(BOOKING_TICKET_URL));
    }
   public Bitmap toBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Rect oldBounds = new Rect(drawable.getBounds());

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(new Canvas(bitmap));

        drawable.setBounds(oldBounds);
        return bitmap;
    }
}
