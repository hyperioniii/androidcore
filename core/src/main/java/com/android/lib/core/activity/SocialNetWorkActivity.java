package com.android.lib.core.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.android.lib.core.activity.facebook.listener.FacebookHelper;
import com.android.lib.core.activity.facebook.listener.IActionLikeFacebook;
import com.android.lib.core.activity.facebook.listener.ILoginFacebook;
import com.android.lib.core.activity.facebook.listener.IPostCommentFacebook;
import com.android.lib.core.activity.facebook.listener.IUserFaceBookListenner;
import com.android.lib.core.activity.google.GooglePlusHelper;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.model.GraphUser;

import java.util.List;

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

    public GraphUser getActiveUserFacebook() {
        if (facebookHelper != null)
            return facebookHelper.getActiveUserFacebook();
        return null;
    }

    public String getKeyHash(String pakageName) {
        if (facebookHelper != null)
            return facebookHelper.getKeyHash();
        return null;
    }

    /**
     * get user info
     *
     * @param listenner
     */
    public void getUserInfo(final IUserFaceBookListenner listenner) {
        if (facebookHelper != null)
            facebookHelper.getUserInfo(listenner);
    }

    public void actionNeedLoginSequence(final String[] readPermissions, final String[] publishPermissions, ILoginFacebook listener) {
        if (facebookHelper != null)
            facebookHelper.actionNeedLoginSequence(readPermissions, publishPermissions, listener);
    }

    /**
     * share fb with login sequence
     *
     * @param name
     * @param caption
     * @param description
     * @param place
     * @param link
     * @param pictureLink
     * @param friends
     * @param fragment
     */
    public void shareFacebook(final String name, final String caption, final String description, final String place, final String link, final String pictureLink, final List<String> friends, final Fragment fragment) {
        if (facebookHelper != null)
            facebookHelper.shareFacebook(name, caption, description, place, link, pictureLink, friends, fragment);
    }

    /**
     * Share Facebook with Dialog . It will open share fb activity if Facebook app available, else open web view
     *
     * @param name
     * @param link
     */
    public void shareFacebookWithDialog(String name, String caption, String description, String place, String link, String pictureLink, List<String> friends, Fragment fragment) {
        if (facebookHelper != null)
            facebookHelper.shareFacebookWithDialog(name, caption, description, place, link, pictureLink, friends, fragment);
    }

    /**
     * @param name
     * @param caption
     * @param description
     * @param place
     * @param link
     * @param pictureLink
     */
    public void shareFacebookWithWebDialog(String name, String caption, String description, String place, String link, String pictureLink) {
        if (facebookHelper != null)
            facebookHelper.shareFacebookWithWebDialog(name, caption, description, place, link, pictureLink);
    }

    /**
     * login facebook
     *
     * @param readPermissions
     * @param onLoginFacebookSuccess
     */
    public void loginFacebook(final String[] readPermissions, final String[] publishPermissions,
                              final ILoginFacebook onLoginFacebookSuccess) {
        if (facebookHelper != null)
            facebookHelper.loginFacebook(readPermissions, publishPermissions, onLoginFacebookSuccess);
    }

    /**
     * @param allowLoginUI
     * @param callback
     * @param permissions
     * @return
     */
    private Session openPublishPermission(boolean allowLoginUI, final StatusCallback callback, final List<String> permissions) {
        if (facebookHelper != null)
            return facebookHelper.openPublishPermission(allowLoginUI, callback, permissions);
        return null;
    }

    /**
     * open active session with add new permission
     *
     * @param allowLoginUI
     * @param callback
     * @param permissions
     * @return
     */
    private Session openReadPermission(boolean allowLoginUI, StatusCallback callback, List<String> permissions) {
        if (facebookHelper != null)
            return facebookHelper.openReadPermission(allowLoginUI, callback, permissions);
        return null;
    }

    /**
     * unLike fb with login sequence
     *
     * @param objectId
     * @param listenner
     */
    public void unLike(final String objectId, final IActionLikeFacebook listenner) {
        if (facebookHelper != null)
            facebookHelper.unLike(objectId, listenner);
    }

    /**
     * unlike facebook
     *
     * @param objectId
     * @param listenner
     */
    public void unLikeFacebook(String objectId, final IActionLikeFacebook listenner) {
        if (facebookHelper != null)
            facebookHelper.unLikeFacebook(objectId, listenner);
    }

    /**
     * postComment fb with login sequence
     *
     * @param objectId
     * @param msg
     * @param listenner
     */
    public void postComment(final String objectId, final String msg,
                            final IPostCommentFacebook listenner) {
        if (facebookHelper != null)
            facebookHelper.postComment(objectId, msg, listenner);
    }

    /**
     * post a comment
     *
     * @param objectId
     * @param msg
     */
    public void postCommentFacebook(String objectId, String msg,
                                    final IPostCommentFacebook listenner) {
        if (facebookHelper != null)
            facebookHelper.postCommentFacebook(objectId, msg, listenner);
    }

    /**
     * like fb with login sequence
     *
     * @param objectId
     * @param listenner
     */
    public void like(final String objectId, final IActionLikeFacebook listenner) {
        if (facebookHelper != null)
            facebookHelper.like(objectId, listenner);
    }

    /**
     * like facebook
     *
     * @param objectId
     * @param listenner
     */
    public void likeFacebook(String objectId, final IActionLikeFacebook listenner) {
        if (facebookHelper != null)
            facebookHelper.likeFacebook(objectId, listenner);
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
