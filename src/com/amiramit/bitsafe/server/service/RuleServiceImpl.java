package com.amiramit.bitsafe.server.service;

import static com.amiramit.bitsafe.server.OfyService.ofy;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.client.dto.RuleDTO;
import com.amiramit.bitsafe.client.dto.UIVerifyException;
import com.amiramit.bitsafe.client.service.RuleService;
import com.amiramit.bitsafe.server.BLUser;
import com.amiramit.bitsafe.server.login.LoginProvider;
import com.amiramit.bitsafe.server.rule.Rule;
import com.amiramit.bitsafe.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

@SuppressWarnings("serial")
public class RuleServiceImpl extends XsrfProtectedServiceServlet implements
		RuleService {

	private static final Logger LOG = Logger.getLogger(RuleServiceImpl.class
			.getName());

	@Override
	public Long addRule(final RuleDTO uiRule) throws NotLoggedInException,
			UIVerifyException {
		// TODO: Limit users to <Magic Number> rules in total!!!
		// See also getRules
		final HttpSession session = getThreadLocalRequest().getSession();
		final BLUser blUser = LoginProvider.isLoggedIn(session);
		uiRule.verify();
		final Rule srvRule = Rule.fromDTO(blUser.getUserId(), uiRule);

		// We must saveNow to make ID available
		srvRule.saveNow();
		LOG.info("addRule with rule: " + srvRule + " success; returning id: "
				+ srvRule.getKey());
		return srvRule.getKey();
	}

	@Override
	public void updateRule(final RuleDTO uiRule) throws NotLoggedInException,
			UIVerifyException {
		final HttpSession session = getThreadLocalRequest().getSession();
		final BLUser blUser = LoginProvider.isLoggedIn(session);
		uiRule.verify(false);

		// First, load rule from db and validate it actually belongs to this
		// user
		final Rule dbRule = ofy().load().type(Rule.class).id(uiRule.getKey())
				.safe();
		if (dbRule.getUserId() != blUser.getUserId()) {
			LOG.severe("removeRule with id: " + uiRule.getKey()
					+ " from User: " + blUser + "failed because dbule user: "
					+ dbRule.getUserId() + " does not match!");
			return;
		}

		// creates new rule with the same id but updated properties from ui rule
		final Rule updatedRule = Rule.fromDTO(blUser.getUserId(), uiRule);

		// No need to save now as ID already available
		updatedRule.save();
		LOG.info("updateRule with rule: " + uiRule + " success");
	}

	@Override
	public void removeRule(final Long id) throws NotLoggedInException,
			UIVerifyException {
		final HttpSession session = getThreadLocalRequest().getSession();
		final BLUser blUser = LoginProvider.isLoggedIn(session);
		FieldVerifier.verifyNotNull(id);
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
		LOG.info("removeRule with id: " + id + " success");
	}

	@Override
	public RuleDTO[] getRules() throws NotLoggedInException, UIVerifyException {
		final HttpSession session = getThreadLocalRequest().getSession();
		final BLUser blUser = LoginProvider.isLoggedIn(session);

		// TODO: Make this limit known to user somehow!
		final List<Rule> dbRules = ofy().load().type(Rule.class)
				.filter("userId", blUser.getUserId()).limit(100).list();

		final RuleDTO[] ret = new RuleDTO[dbRules.size()];
		final Iterator<Rule> it = dbRules.iterator();
		int i = 0;
		while (it.hasNext()) {
			final Rule curRule = it.next();
			ret[i] = Rule.toDTO(curRule);
			++i;
		}

		LOG.info("getRules returning " + ret.length + " rules");
		return ret;
	}
}
