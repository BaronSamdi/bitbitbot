package com.amiramit.bitsafe.client.service;

import com.amiramit.bitsafe.client.uitypes.UITradeRule;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RuleServiceAsync {

	void addRule(UITradeRule rule, AsyncCallback<Long> callback);

	void getRules(AsyncCallback<UITradeRule[]> callback);

	void removeRule(Long id, AsyncCallback<Void> callback);

}
