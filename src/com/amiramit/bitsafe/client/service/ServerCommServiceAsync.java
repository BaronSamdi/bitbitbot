package com.amiramit.bitsafe.client.service;

import com.amiramit.bitsafe.client.UITypes.UITicker;
import com.amiramit.bitsafe.shared.ExchangeName;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>ServerComm</code>.
 */
public interface ServerCommServiceAsync {
	void getTicker(ExchangeName atExchange, AsyncCallback<UITicker> callback);
}
