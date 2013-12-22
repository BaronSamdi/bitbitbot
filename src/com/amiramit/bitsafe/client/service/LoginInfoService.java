package com.amiramit.bitsafe.client.service;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.uitypes.UILoginInfo;
import com.amiramit.bitsafe.client.uitypes.UIVerifyException;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("login")
public interface LoginInfoService extends XsrfProtectedService {
	UILoginInfo getLoginInfo() throws UIVerifyException, NotLoggedInException;
}
