package com.amiramit.bitsafe.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Bitsafe implements EntryPoint {
	private static final Logger LOG = Logger.getLogger(Bitsafe.class.getName());
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	private static final int REFRESH_INTERVAL = 1000; // ms

	/**
	 * Create a remote service proxy to talk to the server-side
	 */
	private final ServerCommServiceAsync serverComm = GWT
			.create(ServerCommService.class);
	private final RuleServiceAsync ruleService = GWT.create(RuleService.class);

	private final Label priceLabel = new Label("Waiting for server ...");
	private final Label lastUpdatedLabel = new Label("Waiting for server ...");
	private final Label errorLabel = new Label("");

	private LoginInfo loginInfo = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(
			"Please sign in to your Google Account to access the bitsafe application.");
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");

	private void loadLogin() {
		// Assemble login panel.
		signInLink.setHref(loginInfo.getLoginUrl());
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);
		RootPanel.get("ticker").add(loginPanel);
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		LOG.info("onModuleLoad");
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		try {
			loginService.login(GWT.getHostPageBaseURL(),
					new AsyncCallback<LoginInfo>() {
						public void onFailure(Throwable error) {
						}

						public void onSuccess(LoginInfo result) {
							loginInfo = result;
							if (loginInfo.isLoggedIn()) {
								loadWelcomePage();
							} else {
								loadLogin();
							}
						}
					});
		} catch (UIVerifyException error) {
			handleError(error);
		}
	}

	private void loadWelcomePage() {

		RootPanel.get("welcomeMsgContainer").add(
				new Label("Welcome " + loginInfo.getNickname()
						+ "! You'r email address is "
						+ loginInfo.getEmailAddress()));
		RootPanel.get("priceContainer").add(priceLabel);
		RootPanel.get("lastUpdatedContainer").add(lastUpdatedLabel);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Set up sign out hyperlink.
		signOutLink.setHref(loginInfo.getLogoutUrl());
		RootPanel.get("ticker").add(signOutLink);

		// Setup timer to refresh list automatically.
		Timer refreshTimer = new Timer() {
			@Override
			public void run() {
				refreshWatchList();
			}

			private void refreshWatchList() {
				// We send the request to the server.
				serverComm.getTicker("BTCUSD", new AsyncCallback<UITicker>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						errorLabel.setText(SERVER_ERROR);
						LOG.severe("Error in serverComm.getTicker: " + caught);
					}

					public void onSuccess(UITicker result) {
						priceLabel.setText(result.getLast().toString());
						lastUpdatedLabel.setText(DateTimeFormat.getFormat(
								PredefinedFormat.DATE_TIME_SHORT).format(
								result.getTimestamp()));
						errorLabel.setText("");
					}
				});
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
	}

	private void handleError(Throwable error) {
		Window.alert(error.getMessage());
		if (error instanceof NotLoggedInException) {
			Window.Location.replace(loginInfo.getLogoutUrl());
		}
	}
}
