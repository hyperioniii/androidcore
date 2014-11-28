package com.run.socialactivitytest.socialactivitytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.lib.core.activity.BaseActivity;
import com.android.lib.core.util.DebugLog;


public class MainActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }


    @Override
    protected void preOnCreate(Bundle savedInstanceState) {
        DebugLog.setEnable(true);

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
