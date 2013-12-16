package com.amiramit.bitsafe.client.channel;

public interface ChannelListener {

	void onMessage(String message);

	void onOpen();

	void onError(int code, String description);

	void onClose();
}
