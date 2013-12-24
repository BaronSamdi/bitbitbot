package com.amiramit.bitsafe.server.service;

import static com.amiramit.bitsafe.server.OfyService.ofy;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import rule.Rule;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.service.RuleService;
import com.amiramit.bitsafe.client.uitypes.UIStopLossRule;
import com.amiramit.bitsafe.client.uitypes.UITradeRule;
import com.amiramit.bitsafe.client.uitypes.UIVerifyException;
import com.amiramit.bitsafe.server.BLUser;
import com.amiramit.bitsafe.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

@SuppressWarnings("serial")
public class RuleServiceImpl extends XsrfProtectedServiceServlet implements
		RuleService {

	private static final Logger LOG = Logger.getLogger(RuleServiceImpl.class
			.getName());

	@Override
	public Long addRule(final UITradeRule uiRule) throws NotLoggedInException,
			UIVerifyException {
		// TODO: Limit users to <Magic Number> rules in total!!!
		// See also getRules
		final HttpSession session = getThreadLocalRequest().getSession();
		final BLUser blUser = BLUser.checkLoggedIn(session);
		FieldVerifier.verifyNotNull(uiRule);
		uiRule.verify();

		LOG.info("addRule with rule: " + uiRule);

		Rule srvRule = null;
		if (uiRule instanceof UIStopLossRule) {
			final UIStopLossRule slRule = ((UIStopLossRule) uiRule);

			srvRule = new StopLossRule(blUser.getUserId(), uiRule.getName(),
					uiRule.getActive(), uiRule.getAtExchange(),
					slRule.getAtPrice());
		} else {
			throw new UIVerifyException("Unknown ui rule type: "
					+ uiRule.getClass().getName());
		}

		srvRule.save();

		return srvRule.getKey();
	}

	@Override
	public void removeRule(final Long id) throws NotLoggedInException,
			UIVerifyException {
		final HttpSession session = getThreadLocalRequest().getSession();
		final BLUser blUser = BLUser.checkLoggedIn(session);
		FieldVerifier.verifyNotNull(id);
		LOG.info("removeRule with id: " + id);
		final Rule dbRule = ofy().load().type(Rule.class).id(id).safe();
		if (dbRule.getUserId() != blUser.getUserId()) {
			LOG.severe("removeRule with id: " + id + " from User: " + blUser
					+ "failed because dbule user: " + dbRule.getUserId()
					+ " does not match!");
			return;
		}

		// No need to call now() as GAE completes all asynchronous operations at
		// the end of the request.
		ofy().delete().entity(dbRule);
	}

	@Override
	public UITradeRule[] getRules() throws NotLoggedInException {
		final HttpSession session = getThreadLocalRequest().getSession();
		final BLUser blUser = BLUser.checkLoggedIn(session);
		LOG.info("getRules called for user: " + blUser);

		// TODO: Make this limit known to user somehow!
		final List<Rule> dbRules = ofy().load().type(Rule.class)
				.filter("userId", blUser.getUserId()).limit(100).list();
		LOG.info("getRules returning " + dbRules.size() + " rules");

		final UITradeRule[] ret = new UITradeRule[dbRules.size()];
		final Iterator<Rule> it = dbRules.iterator();
		int i = 0;
		while (it.hasNext()) {
			final Rule curRule = it.next();
			ret[i] = tradeRuleToUIRule(curRule);
			++i;
		}

		return ret;
	}

	private UITradeRule tradeRuleToUIRule(final Rule curRule) {
		if (curRule instanceof StopLossRule) {
			final StopLossRule slRule = (StopLossRule) curRule;
			return new UIStopLossRule(curRule.getKey(),
					curRule.getCreateDate(), curRule.getName(),
					curRule.getActive(), curRule.getAtExchange(),
					slRule.getAtPrice());
		}

		LOG.severe("Unknow rule class: " + curRule.getClass()
				+ " in tradeRuleToUIRule");
		return null;
	}

}
