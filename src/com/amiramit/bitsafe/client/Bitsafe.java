package com.amiramit.bitsafe.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amiramit.bitsafe.client.UITypes.UIBeanFactory;
import com.amiramit.bitsafe.client.UITypes.UILoginInfo;
import com.amiramit.bitsafe.client.UITypes.UIStopLossRule;
import com.amiramit.bitsafe.client.UITypes.UITicker;
import com.amiramit.bitsafe.client.UITypes.UITradeRule;
import com.amiramit.bitsafe.client.UITypes.UIVerifyException;
import com.amiramit.bitsafe.client.channel.Channel;
import com.amiramit.bitsafe.client.channel.ChannelListener;
import com.amiramit.bitsafe.client.service.LoginService;
import com.amiramit.bitsafe.client.service.LoginServiceAsync;
import com.amiramit.bitsafe.client.service.RuleService;
import com.amiramit.bitsafe.client.service.RuleServiceAsync;
import com.amiramit.bitsafe.server.BLUser;
import com.amiramit.bitsafe.shared.ExchangeName;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
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
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Bitsafe implements EntryPoint {
	private static final Logger LOG = Logger.getLogger(Bitsafe.class.getName());
    private static final String STOP_LOSS = "Stop Loss";

	/**
	 * Create a remote service proxy to talk to the server-side
	 */
	private final RuleServiceAsync ruleService = GWT.create(RuleService.class);
	private final UIBeanFactory uiBeanFactory = GWT.create(UIBeanFactory.class);

	private final Label priceLabel = new Label("Waiting for server ...");
	private final Label lastUpdatedLabel = new Label("Waiting for server ...");
	private final Label errorLabel = new Label("");

	private UILoginInfo loginInfo = null;
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
	@Override
	public void onModuleLoad() {

		// Check login status using login service.
		final LoginServiceAsync loginService = GWT.create(LoginService.class);
		try {
			loginService.login(GWT.getHostPageBaseURL(),
					new AsyncCallback<UILoginInfo>() {
						@Override
						public void onFailure(Throwable error) {
							handleError("loginService.login", error);
						}

						@Override
						public void onSuccess(UILoginInfo result) {
							loginInfo = result;
							if (loginInfo.isLoggedIn()) {
								getXSRFToken();
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
		final XsrfTokenServiceAsync xsrf = (XsrfTokenServiceAsync) GWT
				.create(XsrfTokenService.class);
		((ServiceDefTarget) xsrf).setServiceEntryPoint(GWT.getModuleBaseURL()
				+ "xsrf");
		AsyncCallback<XsrfToken> asyncCallback = new AsyncCallback<XsrfToken>() {

			@Override
			public void onSuccess(XsrfToken token) {
				((HasRpcToken) ruleService).setRpcToken(token);
				loadWelcomePage();
			}

			@Override
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
		rulesFlexTable.setText(0, 4, "Exchange");
		rulesFlexTable.setText(0, 5, "Remove");

		TextBox nameBox = new TextBox();
		nameBox.setText("Name your rule");
		ListBox ruleBox = new ListBox();
		ruleBox.addItem(STOP_LOSS);
		ListBox exchangeBox = new ListBox();
		for (ExchangeName i : ExchangeName.values()) {
			exchangeBox.addItem(i.toString());
		}
		TextBox priceBox = new TextBox();
		priceBox.setText("Trigger Price");
		Button addButton = new Button("Add");
		addButton.addStyleDependentName("add");
		addButton.addClickHandler(new ClickHandler() {
			@Override
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
		rulesFlexTable.setWidget(1, 4, exchangeBox);
		rulesFlexTable.setWidget(1, 5, addButton);

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

		// Setup channel listener for ticker information
		Channel tickerChannel = new Channel();
		tickerChannel.addChannelListener(new ChannelListener() {

			@Override
			public void onOpen() {
				handleError("tickerChannel open");
			}

			@Override
			public void onMessage(String tickerAsJson) {
				handleError("tickerChannel onMessage: " + tickerAsJson);
				UITicker ticker = AutoBeanCodex.decode(uiBeanFactory,
						UITicker.class, tickerAsJson).as();

				priceLabel.setText(ticker.getAtExchange() + ": "
						+ ticker.getLast().toString());
				lastUpdatedLabel.setText(DateTimeFormat.getFormat(
						PredefinedFormat.DATE_TIME_SHORT).format(
						ticker.getTimestamp()));
				errorLabel.setText("");
			}

			@Override
			public void onError(int code, String description) {
				handleError("tickerChannel error code: " + code
						+ " description: " + description);
			}

			@Override
			public void onClose() {
				handleError("tickerChannel onClose().");
			}
		});

		handleError("Tring to join channel: " + loginInfo.getChannelToken());
		tickerChannel.join(loginInfo.getChannelToken());

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
				"rulesListStringColumn");
		rulesFlexTable.getCellFormatter().addStyleName(row, 5,
				"rulesListRemoveColumn");
	}

	private void handleError(String error) {
		LOG.severe(error);
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
			@Override
			public void onFailure(Throwable error) {
				handleError("ruleService.getRules", error);
			}

			@Override
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
		ruleDisplayCheckBox.setEnabled(false);
		rulesFlexTable.setWidget(row, 0, ruleDisplayCheckBox);
		rulesFlexTable.setText(row, 1, rule.getName());
		rulesFlexTable.setText(row, 4, rule.getAtExchange().toString());
		if (rule instanceof UIStopLossRule) {
			rulesFlexTable.setText(row, 2, STOP_LOSS);
			rulesFlexTable.setText(row, 3, ((UIStopLossRule) rule).getAtPrice()
					.toString());
		} else {
			handleError("Display rule got unknown rule!");
			return;
		}
		setRulesTableRowStyle(row);

		// Add a button to remove this stock from the table.
		Button removeStockButton = new Button("x");
		removeStockButton.addStyleDependentName("remove");
		removeStockButton.addClickHandler(new ClickHandler() {
			@Override
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
		rulesFlexTable.setWidget(row, 5, removeStockButton);
	}

	private void removeRule(final UITradeRule ruleToRemove) {
		ruleService.removeRule(ruleToRemove.getDbKey(),
				new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable error) {
						handleError("ruleService.removeRule", error);
					}

					@Override
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

		ListBox lstboxAtExchange = (ListBox) rulesFlexTable.getWidget(addIndex,
				4);
		ExchangeName exchangeName = ExchangeName.valueOf(lstboxAtExchange
				.getItemText(lstboxAtExchange.getSelectedIndex()));
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
					exchangeName, price);
			try {
				ruleToAdd.verify();
			} catch (UIVerifyException e) {
				handleError("ruleToAdd.verify()", e);
				return;
			}

			ruleService.addRule(ruleToAdd, new AsyncCallback<Long>() {
				@Override
				public void onFailure(Throwable error) {
					handleError("ruleService.addRule", error);
				}

				@Override
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
