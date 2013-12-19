package com.amiramit.bitsafe.client.service;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("push")
public interface PushService extends XsrfProtectedService {
	String getChannelKey() throws NotLoggedInException;
}
