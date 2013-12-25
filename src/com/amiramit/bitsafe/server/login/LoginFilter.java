package com.amiramit.bitsafe.server.login;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.amiramit.bitsafe.client.NotLoggedInException;
import com.amiramit.bitsafe.server.BLUser;

public final class LoginFilter implements Filter {

	private static Logger LOG = Logger.getLogger(LoginFilter.class.getName());

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// TODO: do we really need it? all services points check for user logged
		// in anyway ...

		HttpServletRequest req = (HttpServletRequest) request;

		try {
			final HttpSession session = req.getSession(false);
			final BLUser user = BLUser.getUserFromSession(session);
			LOG.info("LoginFilter user: " + user + " logged in.");
			chain.doFilter(request, response);
		} catch (final NotLoggedInException e) {
			// Expected?
			LOG.warning("LoginFilter: request from unknown user");
		}

		HttpServletResponse resp = (HttpServletResponse) response;
		resp.sendRedirect("/");
	}

	@Override
	public void destroy() {
		// Auto-generated method stub

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// Auto-generated method stub

	}
}