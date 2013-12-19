package com.amiramit.bitsafe.client.service;

import com.amiramit.bitsafe.client.uitypes.UILoginInfo;
import com.amiramit.bitsafe.client.uitypes.UIVerifyException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {
	UILoginInfo login(String requestUri) throws UIVerifyException;
}
