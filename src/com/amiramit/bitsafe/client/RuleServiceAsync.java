package com.amiramit.bitsafe.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RuleServiceAsync {

	void addRule(AbstractUITradeRule rule,
			AsyncCallback<AbstractUITradeRule> callback);

	void getRules(AsyncCallback<AbstractUITradeRule[]> callback);

	void removeRule(Long id, AsyncCallback<Void> callback);

}
