package com.run.socialactivitytest.socialactivitytest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.lib.core.activity.SocialNetWorkActivity;
import com.android.lib.core.activity.google.GooglePlusHelper;
import com.android.lib.core.util.DebugLog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.model.people.Person;


public class GoogleExampleActivity extends SocialNetWorkActivity {

    private TextView tvWelcome;
    private TextView btnShare;

    @Override
    public boolean isGoogleEnable() {
        return true;
    }

    @Override
    public boolean isFacebookEnable() {
        return false;
    }

    @Override
    protected void initView() {
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        btnShare = (TextView) findViewById(R.id.btnShare);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_google_example;
    }

    public void loginCLick(View view) {
        googlePlusHelper.connect(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                DebugLog.d("");
                Person currentPerson = googlePlusHelper.getCurrentPerson();
                if(currentPerson !=null){
                    tvWelcome.setText(getString(R.string.welcome, currentPerson.getName()));
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                DebugLog.d("");

            }
        },new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                DebugLog.d("");
            }
        });
    }

    public void shareClick(View view) {
        GooglePlusHelper.ActionGooglePlus actionGooglePlus = new GooglePlusHelper.ActionGooglePlus(GooglePlusHelper.ActionToCall.BEAT, Uri.parse("http://google.com"),null);
        googlePlusHelper.share(actionGooglePlus, "test share", Uri.parse("http://google.com"), null, null, null, null, 28829);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 28829){
            //handle share
            if(resultCode == Activity.RESULT_OK){
                btnShare.setText("share success, please check plus feed to confirm");
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
