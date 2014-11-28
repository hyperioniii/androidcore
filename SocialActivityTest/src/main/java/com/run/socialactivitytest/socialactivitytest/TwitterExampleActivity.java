package com.run.socialactivitytest.socialactivitytest;

import android.os.Bundle;

import com.android.lib.core.activity.BaseActivity;
import com.android.lib.core.util.DebugLog;
import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

public class TwitterExampleActivity extends BaseActivity {
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "QuGfCDp3fwDJuJ9G0OGJqt2NT";
    private static final String TWITTER_SECRET = "jNBuuaKA4ldL4CZkdSFqsBkFE9wNdMQAT5QcofDznSmrSMj7hM";
    @Override
    protected void preOnCreate(Bundle savedInstanceState) {
        final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
    }

    @Override
    protected void initView() {
        TwitterLoginButton btnLogin = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        btnLogin.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a
                // TwitterSession for making API calls
                DebugLog.d("result "+ result);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_twitter;
    }
}
