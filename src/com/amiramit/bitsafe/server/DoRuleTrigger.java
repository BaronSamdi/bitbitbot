package com.amiramit.bitsafe.server;

import static com.amiramit.bitsafe.server.OfyService.ofy;

import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.DeferredTask;

class DoRuleTrigger implements DeferredTask {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(DoRuleTrigger.class
			.getName());
	
	private Long dbKey; 

	public DoRuleTrigger(Long dbKey) {
		this.dbKey = dbKey;
	}

	@Override
    public void run() {
		TradeRule dbRule = ofy().load().type(TradeRule.class).id(dbKey).safe();
		LOG.info("ProcessRulesServlet processing rule: " + dbRule);
		dbRule.trigger();
	}
};