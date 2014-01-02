package com.amiramit.bitsafe.shared;

public enum CurrencyPair {
	BTCUSD(Currency.BTC, Currency.USD);

	// Symbol pairs are quoted, for example, as BTC/USD 100 such that 1 BTC can
	// be purchased with 100 USD
	private final Currency baseCurrency;
	private final Currency counterCurrency;

	private CurrencyPair(final Currency baseCurrency,
			final Currency counterCurrency) {
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrency;
	}

	public Currency getBaseCurrency() {
		return baseCurrency;
	}

	public Currency getCounterCurrency() {
		return counterCurrency;
	}
}
