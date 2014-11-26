package com.android.lib.core.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by nguyenxuan on 11/25/2014.
 */
public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preOnCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        initView();
        initData();
    }

    /**
     * do some work before create view
     * @param savedInstanceState
     */
    protected abstract void preOnCreate(Bundle savedInstanceState);

    /**
     * innit field and properties for view
     */
    protected abstract void initView();

    /**
     * init data for view
     */
    protected abstract void initData();

    /**
     *
     * @return layout resource id , use for setcontentView method
     */
    protected abstract int getLayoutResourceId();
}
