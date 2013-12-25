package com.amiramit.bitsafe.server;

import com.amiramit.bitsafe.server.rule.Rule;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public final class OfyService {

	private OfyService() {
	}

	static {
		factory().register(Rule.class);
		factory().register(BLLastTicker.class);
		factory().register(BLUser.class);
	}

	public static Objectify ofy() {
		return ObjectifyService.ofy();
	}

	public static ObjectifyFactory factory() {
		return ObjectifyService.factory();
	}
}