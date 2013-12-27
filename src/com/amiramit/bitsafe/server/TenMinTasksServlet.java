package com.amiramit.bitsafe.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amiramit.bitsafe.shared.Exchange;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@SuppressWarnings("serial")
public class TenMinTasksServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(TenMinTasksServlet.class
			.getName());

	private static final int NUM_OF_FETCH_PRICE_TASKS = 10;
	// In millis
	private static final int DELAY_BETWEEN_FETCH_PRICE_TASKS = 60 * 1000;

	@Override
	public void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws IOException {
		LOG.info("TenMinTasksServlet called");

		// Create 10 min worth of FetchPriceFromExchangeTask
		for (int i = 0; i < NUM_OF_FETCH_PRICE_TASKS; ++i) {
			for (Exchange curExchange : Exchange.values()) {
				final FetchPriceFromExchangeTask task = new FetchPriceFromExchangeTask(
						curExchange);
				final Queue queue = QueueFactory
						.getQueue("FetchPriceFromExchange");
				final TaskOptions taskOptions = TaskOptions.Builder
						.withPayload(task).countdownMillis(
								i * DELAY_BETWEEN_FETCH_PRICE_TASKS);
				queue.add(taskOptions);
			}
		}
	}
}
