package com.android.lib.core.activity.facebook.listener;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;

import com.android.lib.core.util.ArrayUtil;
import com.android.lib.core.util.DebugLog;
import com.android.lib.core.util.StringUtil;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tuannx on 11/26/2014.
 */
public class FacebookHelper {
    private final UiLifecycleHelper uiHelper;
    private Activity context;
    private IActionShareFacebook mActionShareFacebookListener;
    private GraphUser mFacebookUser;

    public FacebookHelper(Activity context) {
        this.context = context;
        uiHelper = new UiLifecycleHelper(context, new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state,
                             Exception exception) {

            }
        });
    }

    public IActionShareFacebook getActionShareFacebookListener() {
        return mActionShareFacebookListener;
    }

    public void setActionShareFacebookListener(IActionShareFacebook actionShareFacebookListener) {
        this.mActionShareFacebookListener = actionShareFacebookListener;
    }

    public void oncreate(Bundle savedInstanceState) {
        uiHelper.onCreate(savedInstanceState);
        getKeyHash();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        uiHelper.onActivityResult(requestCode, resultCode, data,
                new FacebookDialog.Callback() {
                    @Override
                    public void onError(FacebookDialog.PendingCall pendingCall,
                                        Exception error, Bundle data) {
                        DebugLog.e(
                                String.format("Error: %s", error.toString()));
                        if (mActionShareFacebookListener != null) {
                            mActionShareFacebookListener.onShareFail(error, data);
                        }
                    }

                    @Override
                    public void onComplete(
                            FacebookDialog.PendingCall pendingCall, Bundle data) {
                        DebugLog.i( "Success!");
                        if (mActionShareFacebookListener != null) {
                            String postId = FacebookDialog.getNativeDialogPostId(data);
                            mActionShareFacebookListener.onShareSuccess(postId);
                        }
                    }
                });
        if (Session.getActiveSession() != null)
            Session.getActiveSession().onActivityResult(context, requestCode,
                    resultCode, data);
    }

    public void onResume() {
        uiHelper.onResume();
    }

    public void onSaveInstanceState(Bundle outState) {
        uiHelper.onSaveInstanceState(outState);
    }

    public void onPause() {
        uiHelper.onPause();
    }

    public void onDestroy() {
        uiHelper.onDestroy();
    }

    public GraphUser getActiveUserFacebook() {
        return mFacebookUser;
    }

    /**
     * get user info
     *
     * @param listenner
     */
    public void getUserInfo(final IUserFaceBookListenner listenner) {
        Request requestme = Request.newMeRequest(Session.getActiveSession(),
                new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        DebugLog.e( "user res " + response);
                        mFacebookUser = user;
                        listenner.onGetUserInfoSuccess(user);
                    }
                });
        RequestAsyncTask asyncTask = new RequestAsyncTask(requestme);
        asyncTask.execute();
    }


    public String getKeyHash() {
        String pakageName = context.getPackageName();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(pakageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                // String something = new
                // String(Base64.encodeBytes(md.digest()));
                DebugLog.e("hash key"+ " pakageName "+pakageName);
                DebugLog.e("hash key"+ something);
                return something;
            }
        } catch (PackageManager.NameNotFoundException e1) {
            DebugLog.e("name not found"+ e1.toString());
        } catch (NoSuchAlgorithmException e) {
            DebugLog.e("no such an algorithm"+ e.toString());
        } catch (Exception e) {
            DebugLog.e("exception"+ e.toString());
        }
        return null;
    }

    public void actionNeedLoginSequence(String[] readPermissions, String[] publishPermissions, ILoginFacebook listener) {
        Session session = Session.getActiveSession();

        if (session != null) {

            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!ArrayUtil.isSubsetStringOf(Arrays.asList(readPermissions), permissions)||!ArrayUtil.isSubsetStringOf(Arrays.asList(publishPermissions), permissions)) {
                loginFacebook(readPermissions,publishPermissions,listener);
            }else{
                if(session.isOpened()){
                    listener.onLoginFacebookSuccess();
                }else{
                    loginFacebook(readPermissions,publishPermissions,listener);
                }
            }
        }else{
            loginFacebook(readPermissions,publishPermissions,listener);
        }
    }

    /**
     * login facebook
     *
     * @param readPermissions
     * @param onLoginFacebookSuccess
     */
    public void loginFacebook(String[] readPermissions, final String[] publishPermissions, final ILoginFacebook onLoginFacebookSuccess) {
        DebugLog.e("loginFacebook 1 "+ Session.getActiveSession().toString());
        if(readPermissions!=null && readPermissions.length>0){
            openReadPermission(true, new Session.StatusCallback() {

                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    if(session.isOpened()){
                        if(publishPermissions!=null && publishPermissions.length>0){
                            openPublishPermission(true, new  Session.StatusCallback() {

                                @Override
                                public void call(Session session, SessionState state, Exception exception) {
                                    if(session.isOpened()){
                                        onLoginFacebookSuccess.onLoginFacebookSuccess();
                                    }else if(session.isClosed()){
                                        onLoginFacebookSuccess.onLoginFacebookFail(session, state, exception);
                                    }
                                }
                            }, Arrays.asList(publishPermissions));
                        }else{
                            onLoginFacebookSuccess.onLoginFacebookSuccess();
                        }
                    }else if(session.isClosed()){
                        onLoginFacebookSuccess.onLoginFacebookFail(session, state, exception);
                    }
                }
            }, Arrays.asList(readPermissions));
        }else if(publishPermissions!=null && publishPermissions.length>0){
            openPublishPermission(true, new  Session.StatusCallback() {

                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    if(session.isOpened()){
                        onLoginFacebookSuccess.onLoginFacebookSuccess();
                    }else if(session.isClosed()){
                        onLoginFacebookSuccess.onLoginFacebookFail(session, state, exception);
                    }
                }
            }, Arrays.asList(publishPermissions));
        }else{
            Session.openActiveSession(context, true, new Session.StatusCallback() {

                @Override
                public void call(Session session, SessionState state,
                                 Exception exception) {
                    DebugLog.e( "loginFacebook "+ session.toString());
                    if (state.isOpened()) {
                        onLoginFacebookSuccess.onLoginFacebookSuccess();
                    } else if (state.isClosed()) {
                        onLoginFacebookSuccess.onLoginFacebookFail(session, state,
                                exception);
                    }
                }
            });
        }
    }


    /**
     * @param allowLoginUI
     * @param callback
     * @param permissions
     * @return
     */
    public Session openPublishPermission(boolean allowLoginUI, Session.StatusCallback callback, List<String> permissions) {
        Session.OpenRequest openRequest = new Session.OpenRequest(context).setPermissions(permissions).setCallback(callback);
        Session session = new Session.Builder(context).build();
        if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
            Session.setActiveSession(session);
            session.openForPublish(openRequest);
            return session;
        }

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
    public Session openReadPermission(boolean allowLoginUI, Session.StatusCallback callback, List<String> permissions) {
        Session.OpenRequest openRequest = new Session.OpenRequest(context).setPermissions(permissions).setCallback(callback);
        Session session = new Session.Builder(context).build();
        if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
            Session.setActiveSession(session);
            session.openForRead(openRequest);
            return session;
        }
        return null;

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
        String[] publishPermissions = new String[]{"publish_action"};
        actionNeedLoginSequence(null,publishPermissions,new ILoginFacebook() {
            @Override
            public void onLoginFacebookSuccess() {
                shareFacebookWithDialog(name,caption,description,place,link,pictureLink,friends,fragment);
            }

            @Override
            public void onLoginFacebookFail(Session session, SessionState state, Exception exception) {

            }
        });
    }

    /**
     * Share Facebook with Dialog . It will open share fb activity if Facebook app available, else open web view
     *
     * @param name
     * @param link
     */
    public void shareFacebookWithDialog(String name, String caption, String description, String place, String link, String pictureLink, List<String> friends, Fragment fragment) {
        if (FacebookDialog.canPresentShareDialog(context.getApplicationContext(),
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            // Publish the post using the Share Dialog
            FacebookDialog.ShareDialogBuilder builder = new FacebookDialog.ShareDialogBuilder(context);
            if(!StringUtil.isEmpty(name)){
                builder.setName(name);
            }
            if(!StringUtil.isEmpty(caption)){
                builder.setCaption(caption);
            }
            if(!StringUtil.isEmpty(description)){
                builder.setDescription(description);
            }
            if(!StringUtil.isEmpty(place)){
                builder.setPlace(place);
            }
            if(!StringUtil.isEmpty(link)){
                builder.setLink(link);
            }
            if(!StringUtil.isEmpty(pictureLink)){
                builder.setPicture(pictureLink);
            }
            if(friends!=null && friends.size()>0){
                builder.setFriends(friends);
            }
            if(fragment!=null){
                builder.setFragment(fragment);
            }

            uiHelper.trackPendingDialogCall(builder.build().present());

        } else {
            // Fallback. For example, publish the post using the Feed Dialog
            shareFacebookWithWebDialog(name,caption,description,place, link, pictureLink);
        }
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
        Bundle params = new Bundle();
        if(!StringUtil.isEmpty(name)){
            params.putString("name", name);
        }
        if(!StringUtil.isEmpty(caption)){
            params.putString("caption", caption);
        }
        if(!StringUtil.isEmpty(description)){
            params.putString("description", description);
        }
        if(!StringUtil.isEmpty(place)){
            params.putString("place", place);
        }
        if(!StringUtil.isEmpty(link)){
            params.putString("link", link);
        }
        if(!StringUtil.isEmpty(pictureLink)){
            params.putString("picture", pictureLink);
        }
        WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(context,
                Session.getActiveSession(), params)).setOnCompleteListener(
                new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error == null) {
                            // When the story is posted, echo the success
                            // and the post Id.
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                            } else {
                                // User clicked the Cancel button
                                if (mActionShareFacebookListener != null) {
                                    mActionShareFacebookListener.onShareSuccess(postId);
                                }
                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            // User clicked the "x" button
                            if (mActionShareFacebookListener != null) {
                                mActionShareFacebookListener.onShareFail(error, values);
                            }
                        } else {
                            // Generic, ex: network error
                            if (mActionShareFacebookListener != null) {
                                mActionShareFacebookListener.onShareFail(error, values);
                            }
                        }
                    }

                }).build();
        feedDialog.show();
    }

    /**
     * unLike fb with login sequence
     *
     * @param objectId
     * @param listenner
     */
    public void unLike(final String objectId, final IActionLikeFacebook listenner) {
        String[] publishPermissions = new String[]{"publish_action"};
        actionNeedLoginSequence(null,publishPermissions,new ILoginFacebook() {
            @Override
            public void onLoginFacebookSuccess() {
                unLikeFacebook(objectId,listenner);
            }

            @Override
            public void onLoginFacebookFail(Session session, SessionState state, Exception exception) {

            }
        });
    }

    /**
     * unlike facebook
     *
     * @param objectId
     * @param listenner
     */
    public void unLikeFacebook(String objectId, final IActionLikeFacebook listenner) {
        Request request = new Request(Session.getActiveSession(), "/"
                + objectId + "/likes", null, HttpMethod.DELETE, new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                // stub

                GraphObject graphObject = response.getGraphObject();
                JSONObject json = graphObject.getInnerJSONObject();
                DebugLog.d( "unLikeFacebook " + json.toString());
                try {
                    boolean state = json.getBoolean("FACEBOOK_NON_JSON_RESULT");
                    if (state) {
                        listenner.onLikeFacebookSuccess();
                    } else {
                        listenner.onLikeFacebookFail();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    listenner.onLikeFacebookFail();
                }
            }
        });
        RequestAsyncTask asyncTask = new RequestAsyncTask(request);
        asyncTask.execute();
    }

    /**
     * postComment fb with login sequence
     *
     * @param objectId
     * @param msg
     * @param listenner
     */
    public void postComment(final String objectId, final String msg, final IPostCommentFacebook listenner) {
        String[] publishPermissions = new String[]{"publish_action"};
        actionNeedLoginSequence(null,publishPermissions,new ILoginFacebook() {
            @Override
            public void onLoginFacebookSuccess() {
                postCommentFacebook(objectId,msg,listenner);
            }

            @Override
            public void onLoginFacebookFail(Session session, SessionState state, Exception exception) {

            }
        });
    }

    /**
     * post a comment
     *
     * @param objectId
     * @param msg
     */
    public void postCommentFacebook(String objectId, String msg, final IPostCommentFacebook listenner) {
        Bundle bundle = new Bundle();
        bundle.putString("message", msg);
        Request request = new Request(Session.getActiveSession(), "/"
                + objectId + "/comments", bundle, HttpMethod.POST,
                new Request.Callback() {

                    @Override
                    public void onCompleted(Response response) {
                        // stub
                        Log.d("",
                                "postCommentFacebook rs " + response.toString());
                        GraphObject graphObject = response.getGraphObject();
                        JSONObject json = graphObject.getInnerJSONObject();
                        try {
                            DebugLog.d(
                                    "postCommentFacebook rs " + json.toString());
                            String postId = json.getString("id");
                            listenner.onPostCommentFacebookSuccess(postId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listenner.onPostCommentFacebookFail();
                        }
                    }
                });
        RequestAsyncTask asyncTask = new RequestAsyncTask(request);
        asyncTask.execute();
    }

    /**
     * like fb with login sequence
     *
     * @param objectId
     * @param listenner
     */
    public void like(final String objectId, final IActionLikeFacebook listenner) {
        String[] publishPermissions = new String[]{"publish_action"};
        actionNeedLoginSequence(null, publishPermissions, new ILoginFacebook() {
            @Override
            public void onLoginFacebookSuccess() {
                likeFacebook(objectId, listenner);
            }

            @Override
            public void onLoginFacebookFail(Session session, SessionState state, Exception exception) {

            }
        });
    }

    /**
     * like facebook
     *
     * @param objectId
     * @param listenner
     */
    public void likeFacebook(String objectId, final IActionLikeFacebook listenner) {
        Request request = new Request(Session.getActiveSession(), "/"
                + objectId + "/likes", null, HttpMethod.POST, new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                // stub
                DebugLog.d( "res like " + response.toString());
                GraphObject graphObject = response.getGraphObject();
                if(graphObject!=null){
                    JSONObject json = graphObject.getInnerJSONObject();
                    try {
                        boolean state = json.getBoolean("FACEBOOK_NON_JSON_RESULT");
                        if (state) {
                            listenner.onLikeFacebookSuccess();
                        } else {
                            listenner.onLikeFacebookFail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listenner.onLikeFacebookFail();
                    }
                }else {
                    listenner.onLikeFacebookFail();
                }

            }
        });
        RequestAsyncTask asyncTask = new RequestAsyncTask(request);
        asyncTask.execute();
    }

    public boolean isLogin() {
        Session session = Session.getActiveSession();
        if(session!=null && session.isOpened()){
            return true;
        }
        return false;
    }
}
