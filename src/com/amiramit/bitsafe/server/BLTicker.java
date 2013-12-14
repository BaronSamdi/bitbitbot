package com.amiramit.bitsafe.server;

import java.math.BigDecimal;
import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.xeiam.xchange.dto.marketdata.Ticker;

@Entity
public class BLTicker {

	@Id
	private Long key;

	private String tradableIdentifier;
	private BigDecimal last;
	private BigDecimal bid;
	private BigDecimal ask;
	private BigDecimal volume;

	@Index
	private Date timestamp;

	protected BLTicker() {

	}

	public BLTicker(Ticker ticker) {
		this.tradableIdentifier = ticker.getTradableIdentifier();
		this.last = ticker.getLast().getAmount();
		this.bid = ticker.getBid().getAmount();
		this.ask = ticker.getAsk().getAmount();
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

	public BigDecimal getVolume() {

		return volume;
	}

	public Date getTimestamp() {

		return timestamp;
	}

	@Override
	public String toString() {
		return "Ticker [tradableIdentifier=" + tradableIdentifier + ", last="
				+ last + ", bid=" + bid + ", ask=" + ask + " volume=" + volume
				+ ", timestamp=" + timestamp + "]";
	}
}
