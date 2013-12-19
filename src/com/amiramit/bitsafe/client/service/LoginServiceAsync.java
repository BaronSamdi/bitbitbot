package com.amiramit.bitsafe.client.service;

import com.amiramit.bitsafe.client.uitypes.UILoginInfo;
import com.amiramit.bitsafe.client.uitypes.UIVerifyException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {

	void login(String requestUri, AsyncCallback<UILoginInfo> callback)
			throws UIVerifyException;

}
