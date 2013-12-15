package com.amiramit.bitsafe.server;

import static com.amiramit.bitsafe.server.OfyService.ofy;

import java.math.BigDecimal;
import java.util.Date;

import com.amiramit.bitsafe.client.UITypes.UITicker;
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

	protected BLLastTicker() {

	}

	public BLLastTicker(ExchangeName atExchange, Ticker ticker) {
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

	static public BLLastTicker getLastTicker(ExchangeName atExchange) {
		return ofy().load().type(BLLastTicker.class).id(atExchange.toString())
				.safe();
	}

	public UITicker toUITicker() {
		return new UITicker(getAtExchange(), getTradableIdentifier(),
				getLast(), getBid(), getAsk(), getHigh(), getLow(),
				getVolume(), getTimestamp());
	}
}
