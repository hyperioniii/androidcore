package com.android.lib.core.activity;

import android.content.Intent;
import android.os.Bundle;

import com.android.lib.core.activity.facebook.listener.FacebookHelper;
import com.android.lib.core.activity.google.GooglePlusHelper;

public abstract class SocialNetWorkActivity extends BaseActivity {
    protected FacebookHelper facebookHelper;
    protected GooglePlusHelper googlePlusHelper;
    private boolean facebookEnable = true;
    private boolean googleEnable = true;

    @Override
    protected void preOnCreate(Bundle savedInstanceState) {
        if (facebookEnable) {
            facebookHelper = new FacebookHelper(this);
            facebookHelper.oncreate(savedInstanceState);
        }
        if(googleEnable){
            googlePlusHelper = new GooglePlusHelper(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (facebookHelper != null){
            facebookHelper.onActivityResult(requestCode, resultCode, data);
        }
        if(googlePlusHelper!=null&&googlePlusHelper.onActivityResult(requestCode,resultCode,data)){
            return ;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (facebookHelper != null)
            facebookHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (facebookHelper != null)
            facebookHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (facebookHelper != null)
            facebookHelper.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (facebookHelper != null)
            facebookHelper.onDestroy();
        if(googlePlusHelper!=null){
            googlePlusHelper.onDestroy();
        }

    }

    public boolean isFacebookEnable() {
        return facebookEnable;
    }

    public void setFacebookEnable(boolean facebookEnable) {
        this.facebookEnable = facebookEnable;
    }

    public boolean isGoogleEnable() {
        return googleEnable;
    }

    public void setGoogleEnable(boolean googleEnable) {
        this.googleEnable = googleEnable;
    }
}
