package com.android.lib.core.activity.facebook.listener;

import com.facebook.model.GraphUser;

/**
 * @author Nguyen Xuan Tuan
 *
 */
public interface IUserFaceBookListenner {
	public void onGetUserInfoSuccess(GraphUser user);
}
