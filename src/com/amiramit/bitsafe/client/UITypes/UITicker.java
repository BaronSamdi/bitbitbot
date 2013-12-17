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
	
	public void setTradableIdentifier(String s);
	public void setLast(BigDecimal b);
	public void setBid(BigDecimal b);
	public void setAsk(BigDecimal b);
	public void setHigh(BigDecimal b);
	public void setLow(BigDecimal b);
	public void setVolume(BigDecimal b);
	public void setTimestamp(Date d);
	public void setAtExchange(ExchangeName e);
}