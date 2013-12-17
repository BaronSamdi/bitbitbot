package com.amiramit.bitsafe.client.service;

import com.amiramit.bitsafe.client.UITypes.UILoginInfo;
import com.amiramit.bitsafe.client.UITypes.UIVerifyException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {
	public UILoginInfo login(String requestUri) throws UIVerifyException;
}
