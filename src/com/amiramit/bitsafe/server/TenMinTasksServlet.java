package com.amiramit.bitsafe.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@SuppressWarnings("serial")
public class TenMinTasksServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(TenMinTasksServlet.class
			.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOG.info("TenMinTasksServlet called");

		// Create 10 min worth of FetchPriceFromExchangeTask
		ProcessRulesTask task = new ProcessRulesTask(blExchangeName);
		Queue queue = QueueFactory.getQueue("ProcessRules");
		TaskOptions taskOptions = TaskOptions.Builder.withPayload(task);
		queue.add(taskOptions);
	}
}
