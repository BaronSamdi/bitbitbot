package com.amiramit.bitsafe.client.service;

import com.google.gwt.user.client.rpc.XsrfProtectedService;

public interface ChannelListenerService extends XsrfProtectedService {

	void onMessage(String message);

	void onOpen();

	void onError(int code, String description);

	void onClose();
}
