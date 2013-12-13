package com.amiramit.bitsafe.server;

import static com.amiramit.bitsafe.server.OfyService.ofy;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.UITypes.UITradeRule;
import com.amiramit.bitsafe.client.UITypes.UIBigMoney;
import com.amiramit.bitsafe.client.UITypes.UICurrencyUnit;
import com.amiramit.bitsafe.client.UITypes.UIStopLossRule;
import com.amiramit.bitsafe.client.UITypes.UIVerifyException;
import com.amiramit.bitsafe.client.service.RuleService;
import com.amiramit.bitsafe.shared.FieldVerifier;
import com.google.appengine.api.users.User;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

public class RuleServiceImpl extends XsrfProtectedServiceServlet implements
		RuleService {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(RuleServiceImpl.class
			.getName());

	@Override
	public Long addRule(final UITradeRule uiRule)
			throws NotLoggedInException, UIVerifyException {
		// TODO: Limit users to <Magic Number> rules in total!!!
		// See also getRules
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

		// TODO: We need to call now() in order to retrieve the key?
		ofy().save().entity(srvRule).now();
		assert (srvRule.getKey() != null);

		return srvRule.getKey();
	}

	@Override
	public void removeRule(final Long id) throws NotLoggedInException,
			UIVerifyException {
		User user = ServerCommServiceImpl.checkLoggedIn();
		FieldVerifier.verifyNotNull(id);
		LOG.info("removeRule with id: " + id);
		TradeRule dbRule = ofy().load().type(TradeRule.class).id(id).safe();
		if (!dbRule.getUser().equals(user)) {
			LOG.severe("removeRule with id: " + id + " from User: " + user
					+ "failed because dbule user: " + dbRule.getUser()
					+ " does not match!");
			return;
		}

		// No need to call now() as GAE completes all asynchronous operations at
		// the end of the request.
		ofy().delete().entity(dbRule);
	}

	@Override
	public UITradeRule[] getRules() throws NotLoggedInException {
		final User user = ServerCommServiceImpl.checkLoggedIn();
		LOG.info("getRules called for user: " + user);

		// TODO: Make this limit known to user somehow!
		List<TradeRule> dbRules = ofy().load().type(TradeRule.class).limit(100)
				.list();
		LOG.info("getRules returning " + dbRules.size() + " rules");

		final UITradeRule[] ret = new UITradeRule[dbRules
				.size()];
		final Iterator<TradeRule> it = dbRules.iterator();
		int i = 0;
		while (it.hasNext()) {
			final TradeRule curRule = it.next();
			ret[i] = tradeRuleToUIRule(curRule);
			++i;
		}

		return ret;
	}

	private UITradeRule tradeRuleToUIRule(final TradeRule curRule) {
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

}
