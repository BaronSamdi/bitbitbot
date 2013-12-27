package com.amiramit.bitsafe.shared;

public enum CurrencyPair {
	BTCUSD(Currency.BTC, Currency.USD);
	
	// Symbol pairs are quoted, for example, as BTC/USD 100 such that 1 BTC can be purchased with 100 USD
	public final Currency baseCurrency;
	public final Currency counterCurrency;
	
	private CurrencyPair(Currency baseCurrency, Currency counterCurrency) {
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrency;
	}	
}
