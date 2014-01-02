package com.amiramit.bitsafe.server;

import java.io.IOException;
import java.util.logging.Logger;

import com.amiramit.bitsafe.shared.CurrencyPair;
import com.amiramit.bitsafe.shared.Exchange;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.NotAvailableFromExchangeException;
import com.xeiam.xchange.NotYetImplementedForExchangeException;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.mtgox.v2.MtGoxExchange;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

public class FetchPriceFromExchangeTask implements DeferredTask {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger
			.getLogger(FetchPriceFromExchangeTask.class.getName());

	private Exchange exchangeInfo;

	public FetchPriceFromExchangeTask(final Exchange exchangeInfo) {
		super();
		this.exchangeInfo = exchangeInfo;
	}

	@Override
	public void run() {
		final PollingMarketDataService marketDataService = getMarketDataService();

		for (final CurrencyPair cp : exchangeInfo.getSupportedCurrencyPairs()) {
			Ticker curTicker;

			try {
				curTicker = marketDataService.getTicker(
						cp.getBaseCurrency().toString(),
						cp.getCounterCurrency().toString());
			} catch (ExchangeException | NotAvailableFromExchangeException
					| NotYetImplementedForExchangeException | IOException e) {
				LOG.severe("Failed to get ticker for currency pair: " + cp
						+ ": " + e);
				e.printStackTrace();
				// No need to retry now as this task is periodically ran anyway
				continue;
			}

			final BLLastTicker blLastTicker = new BLLastTicker(exchangeInfo,
					cp, curTicker);

			LOG.info("FetchPriceFromExchangeServlet got ticker: " + curTicker);

			// No need to call now() as it is called automatically in the end of
			// the
			// request
			OfyService.ofy().save().entity(blLastTicker);
		}

		// Create matching ProcessRulesServlet task
		// TODO: have ProcessRulesTask process only specific exchange/currency
		// and create task for every ticker loaded
		final ProcessRulesTask task = new ProcessRulesTask(exchangeInfo, null);
		final Queue queue = QueueFactory.getQueue("ProcessRules");
		final TaskOptions taskOptions = TaskOptions.Builder.withPayload(task);
		queue.add(taskOptions);
	}

	private PollingMarketDataService getMarketDataService() {
		final String exchangeName;
		// For example: MtGoxExchange.class.getName());
		switch (exchangeInfo) {
		case MtGox:
			exchangeName = MtGoxExchange.class.getName();
			break;
		default:
			LOG.severe("exchange name: " + exchangeInfo + " is not handled!");
			return null;
		}
		final com.xeiam.xchange.Exchange xeiamExchange = ExchangeFactory.INSTANCE
				.createExchange(exchangeName);
		final PollingMarketDataService marketDataService = xeiamExchange
				.getPollingMarketDataService();
		return marketDataService;
	}
}
