package com.amiramit.bitsafe.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amiramit.bitsafe.client.UITypes.UITicker;
import com.google.appengine.api.LifecycleManager;
import com.google.appengine.api.LifecycleManager.ShutdownHook;
import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.backends.BackendService;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.xeiam.xchange.dto.marketdata.Ticker;

@SuppressWarnings("serial")
public class ServerMain extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(ServerMain.class
			.getName());

	private TradeDataRunnable tradeDataRunnable;

	public ServerMain() {
		LifecycleManager.getInstance().setShutdownHook(new ShutdownHook() {
			public void shutdown() {
				LifecycleManager.getInstance().interruptAllRequests();
			}
		});
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		BackendService backendsApi = BackendServiceFactory.getBackendService();

		String requestURI = req.getRequestURI();
		LOG.info("DOGET backend: " + backendsApi.getCurrentBackend()
				+ " instance: " + backendsApi.getCurrentInstance()
				+ " called with req uri: " + requestURI);

		if (requestURI.equals("/_ah/start")) {
			LOG.info("DOGET starting MTGOX polling thread");
			this.tradeDataRunnable = new TradeDataRunnable();
			Thread thread = ThreadManager
					.createBackgroundThread(tradeDataRunnable);
			thread.start();

			LOG.info("/_ah/start returning status 200");
			resp.setStatus(200);
			return;
		}

		if (requestURI.equals("/get_last_ticker")) {
			LOG.info("/get_last_ticker writing ticker to out stream");
			ObjectOutputStream os = new ObjectOutputStream(
					resp.getOutputStream());
			if (tradeDataRunnable == null) {
				LOG.severe("tradeDataRunnable is NULL!");
				os.writeObject(null);
			} else {
				Ticker lastTicker = tradeDataRunnable.getLastTicker();
				if (lastTicker == null) {
					LOG.severe("lastTicker is NULL!");
					os.writeObject(null);
				} else {
					UITicker retTicker = new UITicker(
							lastTicker.getTradableIdentifier(), lastTicker
									.getLast().getAmount(), lastTicker.getBid()
									.getAmount(), lastTicker.getAsk()
									.getAmount(), lastTicker.getHigh()
									.getAmount(), lastTicker.getLow()
									.getAmount(), lastTicker.getVolume(),
							lastTicker.getTimestamp());

					os.writeObject(retTicker);
				}
			}
		}
	}
}
