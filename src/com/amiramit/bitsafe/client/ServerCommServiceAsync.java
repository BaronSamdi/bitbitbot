package com.amiramit.bitsafe.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>ServerComm</code>.
 */
public interface ServerCommServiceAsync {
	void getTicker(String symbol, AsyncCallback<UITicker> callback);
}
