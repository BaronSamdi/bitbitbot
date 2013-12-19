package com.amiramit.bitsafe.client.uitypes;

import java.math.BigDecimal;
import java.util.Date;

import com.amiramit.bitsafe.shared.ExchangeName;

public interface UITicker {
	String getTradableIdentifier();

	BigDecimal getLast();

	BigDecimal getBid();

	BigDecimal getAsk();

	BigDecimal getHigh();

	BigDecimal getLow();

	BigDecimal getVolume();

	Date getTimestamp();

	ExchangeName getAtExchange();

	void setTradableIdentifier(String s);

	void setLast(BigDecimal b);

	void setBid(BigDecimal b);

	void setAsk(BigDecimal b);

	void setHigh(BigDecimal b);

	void setLow(BigDecimal b);

	void setVolume(BigDecimal b);

	void setTimestamp(Date d);

	void setAtExchange(ExchangeName e);
}