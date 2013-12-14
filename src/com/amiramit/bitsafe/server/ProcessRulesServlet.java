package com.amiramit.bitsafe.server;

import static com.amiramit.bitsafe.server.OfyService.ofy;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.QueryResultIterator;

@SuppressWarnings("serial")
public class ProcessRulesServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(RuleServiceImpl.class
			.getName());

	// We want to make sure we can process all rules in less then one minute,
	// since we want to process all of them every one minute!
	public static final long LIMIT_MILLIS = 1000 * 25;

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();

		int numRules = 0;
		QueryResultIterator<TradeRule> dbRulesIt = ofy().load()
				.type(TradeRule.class).filter("active", true).iterator();
		while (dbRulesIt.hasNext()) {
			++numRules;
			TradeRule curRule = dbRulesIt.next();

			if (curRule.checkTrigger()) {
				curRule.doTrigger();
			}

			if (System.currentTimeMillis() - startTime > LIMIT_MILLIS) {
				// Log again in LIMIT_MILLIS time ...
				startTime = System.currentTimeMillis();
				LOG.severe("ProcessRulesServlet is taking too much time - DO SOMETHING!");
			}
		}

		LOG.info("ProcessRulesServlet processed " + numRules + " in "
				+ (System.currentTimeMillis() - startTime) / 1000 + " seconds");
	}
}
