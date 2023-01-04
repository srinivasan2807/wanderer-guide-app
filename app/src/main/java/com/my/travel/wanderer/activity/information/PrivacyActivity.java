package com.my.travel.wanderer.activity.information;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.my.travel.wanderer.data.AppConstants;
import com.my.travel.wanderer.utils.Utils;
import com.bpackingapp.vietnam.travel.R;

public class PrivacyActivity extends AppCompatActivity {
    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, PrivacyActivity.class);
        return in;
    }
    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        bindView();
        Utils.setStatusBarColor(PrivacyActivity.this, 0);
    }

    private void bindView(){
        webview = (WebView) findViewById(R.id.webview);
        webview.loadUrl(AppConstants.PRIVACY_URL);
        webview.clearCache(true);
        webview.clearHistory();
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        findViewById(R.id.topbarLeftBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ((TextView) findViewById(R.id.topbarTitle)).setText(R.string.title_activity_privacy);
    }
}
