package com.amiramit.bitsafe.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PushServiceAsync {

	void getChannelKey(AsyncCallback<String> callback);

}
