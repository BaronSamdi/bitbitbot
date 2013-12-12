package com.amiramit.bitsafe.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import com.amiramit.bitsafe.client.AbstractUITradeRule;
import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.RuleService;
import com.amiramit.bitsafe.client.UIBigMoney;
import com.amiramit.bitsafe.client.UICurrencyUnit;
import com.amiramit.bitsafe.client.UIStopLossRule;
import com.amiramit.bitsafe.client.UIVerifyException;
import com.amiramit.bitsafe.shared.FieldVerifier;
import com.google.appengine.api.users.User;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

public class RuleServiceImpl extends XsrfProtectedServiceServlet implements
		RuleService {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(RuleServiceImpl.class
			.getName());
	private static final PersistenceManagerFactory PMF = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	@Override
	public AbstractUITradeRule addRule(final AbstractUITradeRule uiRule)
			throws NotLoggedInException, UIVerifyException {
		// TODO: Limit users to <Magic Number> rules in total!!!
		final User user = ServerCommServiceImpl.checkLoggedIn();
		FieldVerifier.verifyNotNull(uiRule);
		uiRule.verify();

		LOG.info("addRule with rule: " + uiRule);

		TradeRule srvRule = null;
		if (uiRule instanceof UIStopLossRule) {
			final UIStopLossRule slRule = ((UIStopLossRule) uiRule);
			final CurrencyUnit cu = CurrencyUnit.getInstance(slRule.getPrice()
					.getUnit().name());
			final BigMoney bm = BigMoney.of(cu, slRule.getPrice().getAmount());

			srvRule = new StopLossRule(user, uiRule.getName(),
					uiRule.getActive(), bm);
		} else {
			throw new UIVerifyException("Unknown ui rule type: "
					+ uiRule.getClass().getName());
		}

		final PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(srvRule);
		} finally {
			pm.close();
		}
		
		return tradeRuleToUIRule(srvRule);
	}

	@Override
	public void removeRule(final Long id) throws NotLoggedInException,
			UIVerifyException {
		ServerCommServiceImpl.checkLoggedIn();
		FieldVerifier.verifyNotNull(id);
		// No need to validate long, except that it exists which we validate
		// later
		LOG.info("removeRule with id: " + id);
		final PersistenceManager pm = getPersistenceManager();
		try {
			final StopLossRule rule = pm.getObjectById(StopLossRule.class, id);
			if (rule != null) {
				LOG.info("removeRule deleteing rule: " + rule);
				pm.deletePersistent(rule);
			} else {
				LOG.severe("Rule id " + id + " not found!");
			}
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractUITradeRule[] getRules() throws NotLoggedInException {
		final User user = ServerCommServiceImpl.checkLoggedIn();
		LOG.info("getRules called for user: " + user);
		final PersistenceManager pm = getPersistenceManager();

		List<TradeRule> dbRules = new ArrayList<TradeRule>();
		try {
			final Query q = pm.newQuery(StopLossRule.class, "user == u");
			q.declareParameters("com.google.appengine.api.users.User u");
			q.setOrdering("createDate");
			dbRules = (List<TradeRule>) q.execute(user);
		} finally {
			pm.close();
		}

		LOG.info("getRules returning " + dbRules.size() + " rules");

		final Iterator<TradeRule> it = dbRules.iterator();
		int i = 0;
		final AbstractUITradeRule[] ret = new AbstractUITradeRule[dbRules
				.size()];
		while (it.hasNext()) {
			final TradeRule curRule = it.next();
			ret[i] = tradeRuleToUIRule(curRule);
			++i;
		}

		return ret;
	}

	private AbstractUITradeRule tradeRuleToUIRule(final TradeRule curRule) {
		if (curRule instanceof StopLossRule) {
			final StopLossRule slRule = (StopLossRule) curRule;
			final BigMoney price = slRule.getPrice();
			final UIBigMoney uiPrice = new UIBigMoney(
					UICurrencyUnit.valueOf(price.getCurrencyUnit().getCode()),
					price.getAmount());
			return new UIStopLossRule(curRule.getKey(),
					curRule.getCreateDate(), curRule.getName(),
					curRule.getActive(), uiPrice);
		}

		LOG.severe("Unknow rule class: " + curRule.getClass()
				+ " in tradeRuleToUIRule");
		return null;
	}

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}

}
