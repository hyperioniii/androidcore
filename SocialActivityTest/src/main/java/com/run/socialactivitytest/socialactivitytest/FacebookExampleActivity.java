package com.run.socialactivitytest.socialactivitytest;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.lib.core.activity.SocialNetWorkActivity;
import com.android.lib.core.activity.facebook.listener.IActionShareFacebook;
import com.android.lib.core.activity.facebook.listener.ILoginFacebook;
import com.android.lib.core.activity.facebook.listener.IUserFaceBookListenner;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;


public class FacebookExampleActivity extends SocialNetWorkActivity {

    private TextView tvWelcome;
    private TextView tvShareDialogResult;

    @Override
    public boolean isGoogleEnable() {
        return false;
    }

    @Override
    public boolean isFacebookEnable() {
        return true;
    }

    @Override
    protected void initView() {
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        tvShareDialogResult = (TextView) findViewById(R.id.tvShareDialogResult);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_facebook_example;
    }

    public void loginCLick(View view) {
        facebookHelper.loginFacebook(null,null,new ILoginFacebook() {
            @Override
            public void onLoginFacebookSuccess() {
                facebookHelper.getUserInfo(new IUserFaceBookListenner() {
                    @Override
                    public void onGetUserInfoSuccess(GraphUser user) {
                        tvWelcome.setText(getString(R.string.welcome,user.getUsername()));
                    }
                });
            }

            @Override
            public void onLoginFacebookFail(Session session, SessionState state, Exception exception) {
                tvWelcome.setText("Login fail");
            }
        });
    }

    public void shareDialogClick(View view) {
        facebookHelper.setActionShareFacebookListener(new IActionShareFacebook() {
            @Override
            public void onShareFail(Exception error, Bundle data) {
                tvShareDialogResult.setText("share fail");
            }

            @Override
            public void onShareSuccess(String postId) {
                tvShareDialogResult.setText("share success, please check facebook feed to confirm");
            }
        });
        facebookHelper.shareFacebookWithDialog("share test","This is the test","This is the test",null,null,null,null,null);
    }
}
