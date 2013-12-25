package com.amiramit.bitsafe.client.service;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.dto.UIVerifyException;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("logininfo")
public interface LoginInfoService extends XsrfProtectedService {
	UILoginInfo getLoginInfo() throws UIVerifyException, NotLoggedInException;

	String logout() throws NotLoggedInException;
}
