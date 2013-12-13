package com.amiramit.bitsafe.client.service;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.UITypes.UITradeRule;
import com.amiramit.bitsafe.client.UITypes.UIVerifyException;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("rule")
public interface RuleService extends XsrfProtectedService {
	public Long addRule(UITradeRule rule) throws NotLoggedInException,
			UIVerifyException;

	public void removeRule(Long id) throws NotLoggedInException, UIVerifyException;

	UITradeRule[] getRules() throws NotLoggedInException;
}
