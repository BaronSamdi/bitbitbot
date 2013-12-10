package com.amiramit.bitsafe.server;

import java.io.IOException;
import java.util.logging.Logger;

import com.google.appengine.api.LifecycleManager;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.NotAvailableFromExchangeException;
import com.xeiam.xchange.NotYetImplementedForExchangeException;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.mtgox.v2.MtGoxExchange;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

/**
 * Encapsulates some market data monitoring behavior
 */
class TradeDataRunnable implements Runnable {
	private static final Logger LOG = Logger.getLogger(TradeDataRunnable.class
			.getName());

	private final PollingMarketDataService marketDataService;
	private Ticker lastTicker;

	public TradeDataRunnable() {
		// Use the default MtGox settings
		Exchange mtGoxExchange = ExchangeFactory.INSTANCE
				.createExchange(MtGoxExchange.class.getName());

		// Interested in the public polling market data feed (no authentication)
		this.marketDataService = mtGoxExchange.getPollingMarketDataService();
	}

	@Override
	public void run() {
		try {
			while (!LifecycleManager.getInstance().isShuttingDown()) {
				// Get the latest ticker data showing BTC to USD
				this.setLastTicker(marketDataService.getTicker(Currencies.BTC,
						Currencies.USD));
				Thread.sleep(1000);
			}

			LOG.info(Thread.currentThread().getName()
					+ ": LifecycleManager.getInstance().isShuttingDown()");

		} catch (ExchangeException | NotAvailableFromExchangeException
				| NotYetImplementedForExchangeException | IOException
				| InterruptedException e) {
			LOG.severe("ERROR in TradeDataRunnable - quitting: " + e);
		} finally {
			// Disconnect and exit
			LOG.info(Thread.currentThread().getName()
					+ ": In finally. Disconnecting...");
		}
	}

	public Ticker getLastTicker() {
		return lastTicker;
	}

	private void setLastTicker(Ticker lastTicker) {
		this.lastTicker = lastTicker;
	}
}