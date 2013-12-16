package com.amiramit.bitsafe.server;

import static com.amiramit.bitsafe.server.OfyService.ofy;

import java.util.logging.Logger;

import com.amiramit.bitsafe.shared.ExchangeName;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class ProcessRulesTask implements DeferredTask {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(ProcessRulesTask.class
			.getName());

	// We want to make sure we can process all rules in less then one minute,
	// since we want to process all of them every one minute!
	public static final long LIMIT_MILLIS = 1000 * 25;

	private ExchangeName blExchangeName;

	public ProcessRulesTask(ExchangeName blExchangeName) {
		super();
		this.blExchangeName = blExchangeName;
	}

	@Override
	public void run() {
		LOG.info("ProcessRulesServlet starting ...");
		long startTime = System.currentTimeMillis();

		int numRules = 0;
		QueryResultIterator<TradeRule> dbRulesIt = ofy().load()
				.type(TradeRule.class).filter("active", true)
				.filter("atExchange", blExchangeName).iterator();
		while (dbRulesIt.hasNext()) {
			++numRules;
			TradeRule curRule = dbRulesIt.next();

			if (curRule.checkTrigger()) {
				DoRuleTriggerTask task = new DoRuleTriggerTask(curRule.getKey());
				Queue queue = QueueFactory.getQueue("DoRuleTrigger");
				TaskOptions taskOptions = TaskOptions.Builder.withPayload(task);
				queue.add(taskOptions);
			}

			if (System.currentTimeMillis() - startTime > LIMIT_MILLIS) {
				// Log again in LIMIT_MILLIS time ...
				startTime = System.currentTimeMillis();
				LOG.severe("ProcessRulesServlet is taking too much time - DO SOMETHING!");
			}
		}

		LOG.info("ProcessRulesServlet done. Processed " + numRules
				+ " rules in " + (System.currentTimeMillis() - startTime)
				/ 1000 + " seconds");
	}
}
