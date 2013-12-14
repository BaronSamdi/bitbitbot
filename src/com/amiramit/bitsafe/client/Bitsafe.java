package com.amiramit.bitsafe.client;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.amiramit.bitsafe.client.UITypes.UIStopLossRule;
import com.amiramit.bitsafe.client.UITypes.UITicker;
import com.amiramit.bitsafe.client.UITypes.UITradeRule;
import com.amiramit.bitsafe.client.UITypes.UIVerifyException;
import com.amiramit.bitsafe.client.service.LoginService;
import com.amiramit.bitsafe.client.service.LoginServiceAsync;
import com.amiramit.bitsafe.client.service.RuleService;
import com.amiramit.bitsafe.client.service.RuleServiceAsync;
import com.amiramit.bitsafe.client.service.ServerCommService;
import com.amiramit.bitsafe.client.service.ServerCommServiceAsync;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Bitsafe implements EntryPoint {
	private static final String STOP_LOSS = "Stop Loss";

	private static final int REFRESH_INTERVAL = 30000; // ms

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

	private ArrayList<UITradeRule> rulesList = new ArrayList<UITradeRule>();
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

		getXSRFToken();

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
		// TODO: GET SESSION COOKIE FROM SERVER!
		Cookies.setCookie("session_id", Long.toString(Random.nextInt()));

		final XsrfTokenServiceAsync xsrf = (XsrfTokenServiceAsync) GWT
				.create(XsrfTokenService.class);
		((ServiceDefTarget) xsrf).setServiceEntryPoint(GWT.getModuleBaseURL()
				+ "xsrf");
		AsyncCallback<XsrfToken> asyncCallback = new AsyncCallback<XsrfToken>() {

			public void onSuccess(XsrfToken token) {
				((HasRpcToken) ruleService).setRpcToken(token);
				((HasRpcToken) serverComm).setRpcToken(token);
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
	}

	private void loadWelcomePage() {
		// Create table for stock data.
		rulesFlexTable.setText(0, 0, "Active");
		rulesFlexTable.setText(0, 1, "Rule Name");
		rulesFlexTable.setText(0, 2, "Rule Type");
		rulesFlexTable.setText(0, 3, "Trigger Price");
		rulesFlexTable.setText(0, 4, "Remove");

		TextBox nameBox = new TextBox();
		nameBox.setText("Name your rule");
		ListBox ruleBox = new ListBox();
		ruleBox.addItem(STOP_LOSS);
		TextBox priceBox = new TextBox();
		priceBox.setText("Trigger Price");
		Button addButton = new Button("Add");
		addButton.addStyleDependentName("add");
		addButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// TODO: Guard here with mutex TABLE_CHANGE_MUTEX as we relay on
				// index we look for in the list and what happens if user add /
				// remove another rule in between?
				int addIndex = rulesFlexTable.getCellForEvent(event)
						.getRowIndex();
				addRule(addIndex);
			}
		});

		rulesFlexTable.setWidget(1, 0, new CheckBox());
		rulesFlexTable.setWidget(1, 1, nameBox);
		rulesFlexTable.setWidget(1, 2, ruleBox);
		rulesFlexTable.setWidget(1, 3, priceBox);
		rulesFlexTable.setWidget(1, 4, addButton);

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
		errorLabel.setText(error);
	}

	private void handleError(String location, Throwable error) {
		handleError("at: " + location + " error: " + error.toString());
		if (error instanceof NotLoggedInException) {
			Window.Location.replace(loginInfo.getLogoutUrl());
		}
	}

	private void loadRules() {
		ruleService.getRules(new AsyncCallback<UITradeRule[]>() {
			public void onFailure(Throwable error) {
				handleError("ruleService.getRules", error);
			}

			public void onSuccess(UITradeRule[] rules) {
				// TODO: Guard here with mutex TABLE_CHANGE_MUTEX as we relay on
				// index we look for in the list and what happens if user add /
				// remove another rule in between?
				displayRules(rules);
			}
		});
	}

	private void displayRules(UITradeRule[] rules) {
		for (UITradeRule rule : rules) {
			displayRule(rule);
		}
	}

	private void displayRule(final UITradeRule rule) {
		// Add the rule to the table.
		int row = rulesFlexTable.getRowCount();
		rulesList.add(rule);
		CheckBox ruleDisplayCheckBox = new CheckBox();
		ruleDisplayCheckBox.setValue(rule.getActive());
		rulesFlexTable.setWidget(row, 0, ruleDisplayCheckBox);
		rulesFlexTable.setText(row, 1, rule.getName());
		if (rule instanceof UIStopLossRule) {
			rulesFlexTable.setText(row, 2, STOP_LOSS);
			rulesFlexTable.setText(row, 3, ((UIStopLossRule) rule).getAtPrice().toString());
		} else {
			handleError("Display rule got unknown rule!");
			return;
		}
		setRulesTableRowStyle(row);

		// Add a button to remove this stock from the table.
		Button removeStockButton = new Button("x");
		removeStockButton.addStyleDependentName("remove");
		removeStockButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// TODO: Guard here with mutex TABLE_CHANGE_MUTEX as we relay on
				// index we look for in the list and what happens if user add /
				// remove another rule in between?
				int removedIndex = rulesFlexTable.getCellForEvent(event)
						.getRowIndex();
				UITradeRule ruleToRemove = rulesList.get(removedIndex - 2);
				if (ruleToRemove.getDbKey() == null) {
					handleError("removeStockButton ClickHandler: Rule to remove has invalid ID!");
					return;
				}
				removeRule(ruleToRemove);
			}
		});
		rulesFlexTable.setWidget(row, 4, removeStockButton);
	}

	private void removeRule(final UITradeRule ruleToRemove) {
		ruleService.removeRule(ruleToRemove.getDbKey(),
				new AsyncCallback<Void>() {
					public void onFailure(Throwable error) {
						handleError("ruleService.removeRule", error);
					}

					public void onSuccess(Void ignore) {
						undisplayRule(ruleToRemove);
					}
				});
	}

	private void undisplayRule(UITradeRule ruleToRemove) {
		// TODO: Guard here with mutex TABLE_CHANGE_MUTEX as we relay on
		// index we look for in the list and what happens if user add /
		// remove another rule in between?
		int removeIndex = rulesList.indexOf(ruleToRemove);
		if (removeIndex == -1) {
			handleError("undisplayRule got rule which has been removed already!");
			return;
		}

		rulesList.remove(removeIndex);
		rulesFlexTable.removeRow(removeIndex + 2);
	}

	protected void addRule(int addIndex) {
		boolean isActive = ((CheckBox) rulesFlexTable.getWidget(addIndex, 0))
				.getValue();
		String name = ((TextBox) rulesFlexTable.getWidget(addIndex, 1))
				.getText();

		ListBox lstboxRuleType = (ListBox) rulesFlexTable
				.getWidget(addIndex, 2);
		String type = lstboxRuleType.getItemText(lstboxRuleType
				.getSelectedIndex());
		if (type.equals(STOP_LOSS)) {
			String sPrice = ((TextBox) rulesFlexTable.getWidget(addIndex, 3))
					.getText();
			BigDecimal price = null;
			try {
				price = new BigDecimal(sPrice);
			} catch (NumberFormatException error) {
				handleError("BigDecimal.valueOf(sPrice)", error);
				return;
			}

			final UITradeRule ruleToAdd = new UIStopLossRule(name, isActive,
					price);
			try {
				ruleToAdd.verify();
			} catch (UIVerifyException e) {
				handleError("ruleToAdd.verify()", e);
				return;
			}

			ruleService.addRule(ruleToAdd, new AsyncCallback<Long>() {
				public void onFailure(Throwable error) {
					handleError("ruleService.addRule", error);
				}

				public void onSuccess(Long dbKey) {
					// TODO: Guard here with mutex TABLE_CHANGE_MUTEX as we
					// relay on index we look for in the list and what happens
					// if user add / remove another rule in between?
					// TODO: This does not set the server set creationDate.
					// Should we ?
					ruleToAdd.setDbKey(dbKey);
					displayRule(ruleToAdd);
				}
			});
		}
	}
}
