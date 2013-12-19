package com.amiramit.bitsafe.client.service;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.uitypes.UITradeRule;
import com.amiramit.bitsafe.client.uitypes.UIVerifyException;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("rule")
public interface RuleService extends XsrfProtectedService {
	Long addRule(UITradeRule rule) throws NotLoggedInException,
			UIVerifyException;

	void removeRule(Long id) throws NotLoggedInException,
			UIVerifyException;

	UITradeRule[] getRules() throws NotLoggedInException;
}
