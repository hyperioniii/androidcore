package com.run.socialactivitytest.socialactivitytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.lib.core.activity.BaseActivity;
import com.android.lib.core.util.DebugLog;
import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends BaseActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "QuGfCDp3fwDJuJ9G0OGJqt2NT";
    private static final String TWITTER_SECRET = "jNBuuaKA4ldL4CZkdSFqsBkFE9wNdMQAT5QcofDznSmrSMj7hM";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }


    @Override
    protected void preOnCreate(Bundle savedInstanceState) {
        DebugLog.setEnable(true);
        final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    public void facebookCLick(View view) {
        DebugLog.d("");
        startActivity(new Intent(this,FacebookExampleActivity.class));
    }

    public void googleCLick(View view) {
        DebugLog.d("");
        startActivity(new Intent(this,GoogleExampleActivity.class));
    }

    public void twitterCLick(View view) {
        DebugLog.d("");
        startActivity(new Intent(this,TwitterExampleActivity.class));
    }
}
