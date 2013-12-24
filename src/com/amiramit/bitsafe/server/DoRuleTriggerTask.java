package com.amiramit.bitsafe.server;

import static com.amiramit.bitsafe.server.OfyService.ofy;

import java.util.logging.Logger;

import rule.Rule;

import com.google.appengine.api.taskqueue.DeferredTask;

class DoRuleTriggerTask implements DeferredTask {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(DoRuleTriggerTask.class
			.getName());

	private Long dbKey;

	public DoRuleTriggerTask(final Long dbKey) {
		this.dbKey = dbKey;
	}

	@Override
	public void run() {
		final Rule dbRule = ofy().load().type(Rule.class).id(dbKey).safe();

		// Check get active again - we lazy load so something might have
		// changed ...
		if (dbRule.getActive() && dbRule.getTrigger().check()) {
			dbRule.getAction().run(dbRule);
		} else {
			LOG.severe("ProcessRulesServlet processing rule: " + dbRule
					+ " not active / not triggered any more.");
		}
	}
};