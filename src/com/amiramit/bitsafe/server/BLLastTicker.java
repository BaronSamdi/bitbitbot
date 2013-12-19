package com.amiramit.bitsafe.server;

import static com.amiramit.bitsafe.server.OfyService.ofy;

import java.math.BigDecimal;
import java.util.Date;

import com.amiramit.bitsafe.shared.ExchangeName;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Serialize;
import com.xeiam.xchange.dto.marketdata.Ticker;

@Entity
@Cache
public class BLLastTicker {

	@Id
	private String atExchange;

	private String tradableIdentifier;
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
	 * This constructor exists for frameworks (e.g. Google Web Toolkit) that
	 * require it for serialization purposes. It should not be called
	 * explicitly.
	 */
	@SuppressWarnings("unused")
	private BLLastTicker() {
	}

	public BLLastTicker(final ExchangeName atExchange, final Ticker ticker) {
		this.atExchange = atExchange.toString();
		this.tradableIdentifier = ticker.getTradableIdentifier();
		this.last = ticker.getLast().getAmount();
		this.bid = ticker.getBid().getAmount();
		this.ask = ticker.getAsk().getAmount();
		this.high = ticker.getHigh().getAmount();
		this.low = ticker.getLow().getAmount();
		this.volume = ticker.getVolume();
		this.timestamp = ticker.getTimestamp();
	}

	public String getTradableIdentifier() {

		return tradableIdentifier;
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

	@Override
	public String toString() {
		return "Ticker [tradableIdentifier=" + tradableIdentifier + ", last="
				+ last + ", bid=" + bid + ", ask=" + ask + ", high=" + high
				+ ", low=" + low + ", volume=" + volume + ", timestamp="
				+ timestamp + "]";
	}

	public ExchangeName getAtExchange() {
		return ExchangeName.valueOf(atExchange);
	}

	public static BLLastTicker getLastTicker(final ExchangeName atExchange) {
		return ofy().load().type(BLLastTicker.class).id(atExchange.toString())
				.now();
	}

	/*
	 * public UITicker toUITicker() { UIBeanFactory factory =
	 * AutoBeanFactorySource.create(UIBeanFactory.class); AutoBean<UITicker>
	 * uiTicker = factory.ticker(); return uiTicker.as(); }
	 */
}
