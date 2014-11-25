package com.run.system.android.core.activity.listener;

import android.os.Bundle;

/**
 * Created by nguyenxuan on 11/25/2014.
 */
public interface IActionShareFacebook {
    public void onShareFail(Exception error, Bundle data);

    public void onShareSuccess(String postId);
}
