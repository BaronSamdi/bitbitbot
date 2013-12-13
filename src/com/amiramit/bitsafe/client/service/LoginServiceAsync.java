package com.amiramit.bitsafe.client.service;

import com.amiramit.bitsafe.client.LoginInfo;
import com.amiramit.bitsafe.client.UITypes.UIVerifyException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {

	public void login(String requestUri, AsyncCallback<LoginInfo> callback) throws UIVerifyException;

}
