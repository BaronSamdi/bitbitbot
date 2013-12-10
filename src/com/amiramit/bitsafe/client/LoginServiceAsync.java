package com.amiramit.bitsafe.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {

	public void login(String requestUri, AsyncCallback<LoginInfo> callback) throws UIVerifyException;

}
