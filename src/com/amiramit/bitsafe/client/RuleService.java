package com.amiramit.bitsafe.client;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("rule")
public interface RuleService extends XsrfProtectedService {
	public void addRule(AbstractUITradeRule rule) throws NotLoggedInException,
			UIVerifyException;

	public void removeRule(Long id) throws NotLoggedInException, UIVerifyException;

	AbstractUITradeRule[] getRules() throws NotLoggedInException;
}
