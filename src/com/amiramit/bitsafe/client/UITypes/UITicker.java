package com.amiramit.bitsafe.client.UITypes;

import java.math.BigDecimal;
import java.util.Date;

import com.amiramit.bitsafe.shared.ExchangeName;

public interface UITicker {
	public String getTradableIdentifier();

	public BigDecimal getLast();

	public BigDecimal getBid();

	public BigDecimal getAsk();

	public BigDecimal getHigh();

	public BigDecimal getLow();

	public BigDecimal getVolume();

	public Date getTimestamp();

	public ExchangeName getAtExchange();
}