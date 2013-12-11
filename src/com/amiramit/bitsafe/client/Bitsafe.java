package com.amiramit.bitsafe.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.rpc.XsrfTokenService;
import com.google.gwt.user.client.rpc.XsrfTokenServiceAsync;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Bitsafe implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server.";

	private static final int REFRESH_INTERVAL = 10000; // ms

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

	private ArrayList<AbstractUITradeRule> rulesList = new ArrayList<AbstractUITradeRule>();
	private FlexTable rulesFlexTable = new FlexTable();

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
		// Check login status using login service.
		final LoginServiceAsync loginService = GWT.create(LoginService.class);
		try {
			loginService.login(GWT.getHostPageBaseURL(),
					new AsyncCallback<LoginInfo>() {
						public void onFailure(Throwable error) {
							handleError("loginService.login", error);
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
			handleError("loginService.login UIVerifyException", error);
		}
	}

	private void getXSRFToken() {
		Cookies.setCookie("session_id", Long.toString(Random.nextInt()));

		final XsrfTokenServiceAsync xsrf = (XsrfTokenServiceAsync) GWT
				.create(XsrfTokenService.class);
		((ServiceDefTarget) xsrf).setServiceEntryPoint(GWT.getModuleBaseURL()
				+ "xsrf");
		AsyncCallback<XsrfToken> asyncCallback = new AsyncCallback<XsrfToken>() {

			public void onSuccess(XsrfToken token) {
				((HasRpcToken) ruleService).setRpcToken(token);
				((HasRpcToken) serverComm).setRpcToken(token);
				xsrf.notify();
			}

			public void onFailure(Throwable caught) {
				try {
					throw caught;
				} catch (RpcTokenException e) {
					handleError("xsrf.getNewXsrfToken RpcTokenException", e);
				} catch (Throwable e) {
					handleError("xsrf.getNewXsrfToken Throwable", e);
				}
			}
		};
		xsrf.getNewXsrfToken(asyncCallback);
		try {
			xsrf.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadWelcomePage() {
		
		getXSRFToken();

		// Create table for stock data.
		rulesFlexTable.setText(0, 0, "Active?");
		rulesFlexTable.setText(0, 1, "Rule Name");
		rulesFlexTable.setText(0, 2, "Rule Type");
		rulesFlexTable.setText(0, 3, "Trigger Price");
		rulesFlexTable.setText(0, 3, "Remove");

		// Add styles to elements in the stock list table.
		rulesFlexTable.setCellPadding(6);
		rulesFlexTable.addStyleName("rulesList");
		rulesFlexTable.getRowFormatter().addStyleName(0, "rulesListHeader");
		setRulesTableRowStyle(0);

		RootPanel.get("welcomeMsgContainer").add(
				new Label("Welcome " + loginInfo.getNickname()
						+ "! You'r email address is "
						+ loginInfo.getEmailAddress()));
		RootPanel.get("rulesContainer").add(rulesFlexTable);
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
						handleError("serverComm.getTicker", caught);
					}

					public void onSuccess(UITicker result) {
						if (result == null) {
							handleError("At serverComm.getTicker: Got null from server");
							return;
						}
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
		loadRules();
	}

	private void setRulesTableRowStyle(int row) {
		rulesFlexTable.getCellFormatter().addStyleName(row, 0,
				"rulesListBooleanColumn");
		rulesFlexTable.getCellFormatter().addStyleName(row, 1,
				"rulesListStringColumn");
		rulesFlexTable.getCellFormatter().addStyleName(row, 2,
				"rulesListStringColumn");
		rulesFlexTable.getCellFormatter().addStyleName(row, 3,
				"rulesListNumericColumn");
		rulesFlexTable.getCellFormatter().addStyleName(row, 4,
				"rulesListRemoveColumn");
	}

	private void handleError(String error) {
		errorLabel.setText(SERVER_ERROR + ": " + error);
	}

	private void handleError(String location, Throwable error) {
		handleError("at: " + location + " error: " + error.toString());
		if (error instanceof NotLoggedInException) {
			Window.Location.replace(loginInfo.getLogoutUrl());
		}
	}

	private void loadRules() {
		ruleService.getRules(new AsyncCallback<AbstractUITradeRule[]>() {
			public void onFailure(Throwable error) {
				handleError("ruleService.getRules", error);
			}

			public void onSuccess(AbstractUITradeRule[] rules) {
				displayRules(rules);
			}
		});
	}

	private void displayRules(AbstractUITradeRule[] rules) {
		for (AbstractUITradeRule rule : rules) {
			displayRule(rule);
		}
	}

	private void displayRule(final AbstractUITradeRule rule) {
		// Add the rule to the table.
		int row = rulesFlexTable.getRowCount();
		rulesList.add(rule);
		rulesFlexTable.setText(row, 0, rule.getActive() ? "V" : "X");
		rulesFlexTable.setText(row, 1, rule.getName());
		if (rule instanceof UIStopLossRule) {
			rulesFlexTable.setText(row, 2, "Stop Loss");
			rulesFlexTable.setText(row, 3, ((UIStopLossRule) rule).getPrice()
					.toString());
		} else {
			errorLabel.setText(SERVER_ERROR);
		}
		setRulesTableRowStyle(row);

		// Add a button to remove this stock from the table.
		Button removeStockButton = new Button("x");
		removeStockButton.addStyleDependentName("remove");
		removeStockButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int removedIndex = rulesFlexTable.getCellForEvent(event)
						.getRowIndex();
				rulesList.remove(removedIndex);
				rulesFlexTable.removeRow(removedIndex + 1);
			}
		});
		rulesFlexTable.setWidget(row, 4, removeStockButton);
	}
}
