package com.amiramit.bitsafe.client.service;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.UITypes.UITicker;
import com.amiramit.bitsafe.client.UITypes.UIVerifyException;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("srv")
public interface ServerCommService extends XsrfProtectedService {
	UITicker getTicker(String symbol) throws IllegalArgumentException,
			UIVerifyException, NotLoggedInException;
}
