package com.run.socialactivitytest.socialactivitytest;

import android.os.Bundle;

import com.android.lib.core.activity.BaseActivity;
import com.android.lib.core.util.DebugLog;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class TwitterExampleActivity extends BaseActivity {

    @Override
    protected void preOnCreate(Bundle savedInstanceState) {

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
