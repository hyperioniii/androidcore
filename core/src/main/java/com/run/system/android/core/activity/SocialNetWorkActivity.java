package com.run.system.android.core.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Request.GraphUserCallback;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.Builder;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.run.system.android.core.ArrayUtil;
import com.run.system.android.core.StringUtil;
import com.run.system.android.core.activity.listener.IActionLikeFacebook;
import com.run.system.android.core.activity.listener.IActionShareFacebook;
import com.run.system.android.core.activity.listener.ILoginFacebook;
import com.run.system.android.core.activity.listener.IPostCommentFacebook;
import com.run.system.android.core.activity.listener.IUserFaceBookListenner;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public abstract class SocialNetWorkActivity extends BaseActivity {
	private UiLifecycleHelper uiHelper;
	private GraphUser mUser;
    private IActionShareFacebook mActionShareFacebookListener;

    @Override
    protected void preOnCreate(Bundle savedInstanceState) {
        uiHelper = new UiLifecycleHelper(this, new StatusCallback() {

            @Override
            public void call(Session session, SessionState state,
                             Exception exception) {

            }
        });
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data,
				new FacebookDialog.Callback() {
					@Override
					public void onError(FacebookDialog.PendingCall pendingCall,
							Exception error, Bundle data) {
						Log.e("Activity",
								String.format("Error: %s", error.toString()));
                        if(mActionShareFacebookListener != null ){
                            mActionShareFacebookListener.onShareFail(error,data);
                        }
					}

					@Override
					public void onComplete(
							FacebookDialog.PendingCall pendingCall, Bundle data) {
                        Log.i("Activity", "Success!");
                        if(mActionShareFacebookListener != null ){
                            String postId = FacebookDialog.getNativeDialogPostId(data);
                            mActionShareFacebookListener.onShareSuccess(postId);
                        }
					}
				});
		if (Session.getActiveSession() != null)
			Session.getActiveSession().onActivityResult(this, requestCode,
					resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	public GraphUser getActiveUserFacebook() {
		return mUser;
	}

	public String getKeyHash(String pakageName) {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(pakageName,
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md;
				md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String something = new String(Base64.encode(md.digest(), 0));
				// String something = new
				// String(Base64.encodeBytes(md.digest()));
				Log.e("hash key", " pakageName "+pakageName);
				Log.e("hash key", something);
				return something;
			}
		} catch (NameNotFoundException e1) {
			Log.e("name not found", e1.toString());
		} catch (NoSuchAlgorithmException e) {
			Log.e("no such an algorithm", e.toString());
		} catch (Exception e) {
			Log.e("exception", e.toString());
		}
		return null;
	}

	/**
	 * get user info
	 * 
	 * @param listenner
	 */
	public void getUserInfo(final IUserFaceBookListenner listenner) {
		Request requestme = Request.newMeRequest(Session.getActiveSession(),
				new GraphUserCallback() {

					@Override
					public void onCompleted(GraphUser user, Response response) {
						Log.e("", "user res " + response);
						mUser = user;
						listenner.onGetUserInfoSuccess(user);
					}
				});
		RequestAsyncTask asyncTask = new RequestAsyncTask(requestme);
		asyncTask.execute();
	}

    public void actionNeedLoginSequence(final String[] readPermissions,final String[] publishPermissions,ILoginFacebook listener){
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
     * share fb with login sequence
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
		if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
				FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
			// Publish the post using the Share Dialog
            FacebookDialog.ShareDialogBuilder builder = new FacebookDialog.ShareDialogBuilder(this);
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
		params.putString("name", name);
		params.putString("link", link);
		params.putString("picture", pictureLink);
		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(this,
				Session.getActiveSession(), params)).setOnCompleteListener(
				new OnCompleteListener() {

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
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
						} else {
							// Generic, ex: network error
						}
					}

				}).build();
		feedDialog.show();
	}

	/**
	 * login facebook
	 * 
	 * @param readPermissions
	 * @param onLoginFacebookSuccess
	 */
	public void loginFacebook(final String[] readPermissions,final String[] publishPermissions,
			final ILoginFacebook onLoginFacebookSuccess) {
		Log.e("", "loginFacebook 1 "+ Session.getActiveSession().toString());
		if(readPermissions!=null && readPermissions.length>0){
			openReadPermission(true, new StatusCallback() {

				@Override
				public void call(Session session, SessionState state, Exception exception) {
					if(session.isOpened()){
						if(publishPermissions!=null && publishPermissions.length>0){
							openPublishPermission(true, new  StatusCallback() {

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
			openPublishPermission(true, new  StatusCallback() {

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
			Session.openActiveSession(this, true, new StatusCallback() {

				@Override
				public void call(Session session, SessionState state,
						Exception exception) {
					Log.e("", "loginFacebook "+ session.toString());
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
	private  Session openPublishPermission( boolean allowLoginUI, final StatusCallback callback, final List<String> permissions) {
		OpenRequest openRequest = new OpenRequest(this).setPermissions(permissions).setCallback(callback);
	    Session session = new Builder(this).build();
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
	private  Session openReadPermission( boolean allowLoginUI, StatusCallback callback, List<String> permissions) {
	    OpenRequest openRequest = new OpenRequest(this).setPermissions(permissions).setCallback(callback);
	    Session session = new Builder(this).build();
	    if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
	        Session.setActiveSession(session);
	        session.openForRead(openRequest);
	        return session;
	    }
	    return null;
	}

    /**
     * unLike fb with login sequence
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
				+ objectId + "/likes", null, HttpMethod.DELETE, new Callback() {

			@Override
			public void onCompleted(Response response) {
				// stub

				GraphObject graphObject = response.getGraphObject();
				JSONObject json = graphObject.getInnerJSONObject();
				Log.d("", "unLikeFacebook " + json.toString());
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
     * @param objectId
     * @param msg
     * @param listenner
     */
    public void postComment(final String objectId, final String msg,
                                    final IPostCommentFacebook listenner){
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
	public void postCommentFacebook(String objectId, String msg,
			final IPostCommentFacebook listenner) {
		Bundle bundle = new Bundle();
		bundle.putString("message", msg);
		Request request = new Request(Session.getActiveSession(), "/"
				+ objectId + "/comments", bundle, HttpMethod.POST,
				new Callback() {

					@Override
					public void onCompleted(Response response) {
						// stub
						Log.d("",
								"postCommentFacebook rs " + response.toString());
						GraphObject graphObject = response.getGraphObject();
						JSONObject json = graphObject.getInnerJSONObject();
						try {
							Log.d("",
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
     * @param objectId
     * @param listenner
     */
    public void like(final String objectId, final IActionLikeFacebook listenner){
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
				+ objectId + "/likes", null, HttpMethod.POST, new Callback() {

			@Override
			public void onCompleted(Response response) {
				// stub
				Log.d("", "res like " + response.toString());
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

    public IActionShareFacebook getActionShareFacebookListener() {
        return mActionShareFacebookListener;
    }

    public void setActionShareFacebookListener(IActionShareFacebook actionShareFacebookListener) {
        this.mActionShareFacebookListener = actionShareFacebookListener;
    }
}
