package com.amiramit.bitsafe.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("rule")
public interface RuleService extends RemoteService {
	public void addRule(AbstractUITradeRule rule) throws NotLoggedInException,
			UIVerifyException;

	public void removeRule(Long id) throws NotLoggedInException, UIVerifyException;

	AbstractUITradeRule[] getRules() throws NotLoggedInException;
}
