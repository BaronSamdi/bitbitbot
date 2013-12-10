package com.amiramit.bitsafe.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("srv")
public interface ServerCommService extends RemoteService {
	UITicker getTicker(String symbol) throws IllegalArgumentException,
			UIVerifyException, NotLoggedInException;
}
