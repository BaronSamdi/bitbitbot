package com.amiramit.bitsafe.server;

import static com.amiramit.bitsafe.server.OfyService.ofy;

import java.math.BigDecimal;
import java.util.Date;

import com.amiramit.bitsafe.shared.CurrencyPair;
import com.amiramit.bitsafe.shared.Exchange;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Serialize;
import com.xeiam.xchange.dto.marketdata.Ticker;

@Entity
@Cache
public class BLLastTicker {

	@Id
	private String id;

	private Exchange exchange;
	private CurrencyPair currencyPair;

	@Serialize
	private BigDecimal last;
	@Serialize
	private BigDecimal bid;
	@Serialize
	private BigDecimal ask;
	@Serialize
	private BigDecimal high;
	@Serialize
	private BigDecimal low;
	@Serialize
	private BigDecimal volume;

	private Date timestamp;

	/**
	 * This constructor exists for frameworks (e.g. Objectify) that require it
	 * for serialization purposes. It should not be called explicitly.
	 */
	@SuppressWarnings("unused")
	private BLLastTicker() {
	}

	public BLLastTicker(final Exchange exchange,
			final CurrencyPair currencyPair, final Ticker ticker) {
		this.exchange = exchange;
		this.currencyPair = currencyPair;
		this.id = getId(exchange, currencyPair);

		if (!ticker.getTradableIdentifier().equals(
				currencyPair.getBaseCurrency().toString())) {
			throw new RuntimeException(
					"Something went wrong - getTradableIdentifier: "
							+ ticker.getTradableIdentifier()
							+ " != currencyPair.baseCurrency.toString(): "
							+ currencyPair.getBaseCurrency().toString());
		}
		this.last = ticker.getLast().getAmount();
		this.bid = ticker.getBid().getAmount();
		this.ask = ticker.getAsk().getAmount();
		this.high = ticker.getHigh().getAmount();
		this.low = ticker.getLow().getAmount();
		this.volume = ticker.getVolume();
		this.timestamp = ticker.getTimestamp();
	}

	private static String getId(final Exchange exchange,
			final CurrencyPair currencyPair) {
		return exchange.toString() + currencyPair.toString();
	}

	public BigDecimal getLast() {

		return last;
	}

	public BigDecimal getBid() {

		return bid;
	}

	public BigDecimal getAsk() {

		return ask;
	}

	public BigDecimal getHigh() {

		return high;
	}

	public BigDecimal getLow() {

		return low;
	}

	public BigDecimal getVolume() {

		return volume;
	}

	public Date getTimestamp() {

		return timestamp;
	}

	public Exchange getExchange() {
		return exchange;
	}

	public CurrencyPair getCurrencyPair() {
		return currencyPair;
	}

	@Override
	public String toString() {
		return "BLLastTicker [id=" + id + ", exchange=" + exchange
				+ ", currencyPair=" + currencyPair + ", last=" + last
				+ ", bid=" + bid + ", ask=" + ask + ", high=" + high + ", low="
				+ low + ", volume=" + volume + ", timestamp=" + timestamp + "]";
	}

	public static BLLastTicker getLastTicker(final Exchange exchange,
			final CurrencyPair currencyPair) {
		return ofy().load().type(BLLastTicker.class)
				.id(getId(exchange, currencyPair)).now();
	}

	/*
	 * public AutoBean<UITicker> toUITickerBean() { final UIBeanFactory factory
	 * = AutoBeanFactorySource .create(UIBeanFactory.class); // TODO: just for
	 * check, remove this! final AutoBean<UITicker> tickerBean =
	 * factory.ticker(); final BLLastTicker lastTicker = BLLastTicker
	 * .getLastTicker(getAtExchange());
	 * tickerBean.as().setAsk(lastTicker.getAsk());
	 * tickerBean.as().setAtExchange(lastTicker.getAtExchange());
	 * tickerBean.as().setBid(lastTicker.getBid());
	 * tickerBean.as().setHigh(lastTicker.getHigh());
	 * tickerBean.as().setLast(lastTicker.getLast());
	 * tickerBean.as().setLow(lastTicker.getLow());
	 * tickerBean.as().setTimestamp(lastTicker.getTimestamp());
	 * tickerBean.as().setTradableIdentifier(
	 * lastTicker.getTradableIdentifier());
	 * tickerBean.as().setVolume(lastTicker.getVolume()); return tickerBean; }
	 */
}
