package com.amiramit.bitsafe.server;

import java.io.IOException;
import java.util.logging.Logger;

import com.amiramit.bitsafe.shared.ExchangeName;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.NotAvailableFromExchangeException;
import com.xeiam.xchange.NotYetImplementedForExchangeException;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.mtgox.v2.MtGoxExchange;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

public class FetchPriceFromExchangeTask implements DeferredTask {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger
			.getLogger(FetchPriceFromExchangeTask.class.getName());

	private ExchangeName blExchangeName;

	public FetchPriceFromExchangeTask(final ExchangeName blExchangeName) {
		super();
		this.blExchangeName = blExchangeName;
	}

	@Override
	public void run() {
		final String exchangeName;
		// For example: MtGoxExchange.class.getName());
		switch (blExchangeName) {
		case MtGox:
			exchangeName = MtGoxExchange.class.getName();
			break;
		default:
			LOG.severe("exchange name: " + blExchangeName + " is not handled!");
			return;
		}
		final Exchange exchange = ExchangeFactory.INSTANCE
				.createExchange(exchangeName);
		final PollingMarketDataService marketDataService = exchange
				.getPollingMarketDataService();
		Ticker lastTicker;
		try {
			lastTicker = marketDataService.getTicker(Currencies.BTC,
					Currencies.USD);
		} catch (ExchangeException | NotAvailableFromExchangeException
				| NotYetImplementedForExchangeException | IOException e) {
			LOG.severe("Failed to get last ticker: " + e);
			e.printStackTrace();
			// No need to retry now as this task is periodically ran anyway
			return;
		}
		final BLLastTicker blLastTicker = new BLLastTicker(blExchangeName,
				lastTicker);

		LOG.info("FetchPriceFromExchangeServlet got ticker: " + lastTicker);

		// No need to call now() as it is called automatically in the end of the
		// request
		OfyService.ofy().save().entity(blLastTicker);

		// Create ProcessRulesServlet task
		final ProcessRulesTask task = new ProcessRulesTask(blExchangeName);
		final Queue queue = QueueFactory.getQueue("ProcessRules");
		final TaskOptions taskOptions = TaskOptions.Builder.withPayload(task);
		queue.add(taskOptions);
	}
}
