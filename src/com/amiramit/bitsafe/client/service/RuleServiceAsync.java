package com.amiramit.bitsafe.client.service;

import com.amiramit.bitsafe.client.dto.RuleDTO;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RuleServiceAsync {

	void addRule(RuleDTO rule, AsyncCallback<Long> callback);

	void getRules(AsyncCallback<RuleDTO[]> callback);

	void removeRule(Long id, AsyncCallback<Void> callback);

	void updateRule(RuleDTO uiRule, AsyncCallback<Void> callback);

}
