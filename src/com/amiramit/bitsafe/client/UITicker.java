package com.amiramit.bitsafe.client;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.amiramit.bitsafe.shared.FieldVerifier;

public class UITicker implements Serializable, UIElement {

	private static final long serialVersionUID = 1L;
	
	private String tradableIdentifier;
	private BigDecimal last;
	private BigDecimal bid;
	private BigDecimal ask;
	private BigDecimal high;
	private BigDecimal low;
	private BigDecimal volume;
	private Date timestamp;

	/**
	 * Constructor
	 * 
	 * @param tradableIdentifier
	 *            The tradable identifier (e.g. BTC in BTC/USD)
	 * @param last
	 * @param bid
	 * @param ask
	 * @param high
	 * @param low
	 * @param volume
	 *            24h volume
	 * @param timestamp
	 */
	public UITicker(String tradableIdentifier, BigDecimal last, BigDecimal bid,
			BigDecimal ask, BigDecimal high, BigDecimal low, BigDecimal volume,
			Date timestamp) {

		this.tradableIdentifier = tradableIdentifier;
		this.last = last;
		this.bid = bid;
		this.ask = ask;
		this.high = high;
		this.low = low;
		this.volume = volume;
		this.timestamp = timestamp;
	}

	public UITicker() {
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

	@Override
	public void verify() throws UIVerifyException {
		FieldVerifier.verifyValidSymbol(tradableIdentifier);
		FieldVerifier.verifyNotNull(last);
		FieldVerifier.verifyNotNull(bid);
		FieldVerifier.verifyNotNull(ask);
		FieldVerifier.verifyNotNull(high);
		FieldVerifier.verifyNotNull(low);
		FieldVerifier.verifyNotNull(volume);
		FieldVerifier.verifyNotNull(timestamp);
	}
}