package com.amiramit.bitsafe.shared;

public enum Exchange {
	MtGox(new CurrencyPair[] { CurrencyPair.BTCUSD });

	private CurrencyPair[] supportedCurrencyPairs;

	Exchange(final CurrencyPair[] supportedCurrencyPairs) {
		this.supportedCurrencyPairs = supportedCurrencyPairs;
	}

	public CurrencyPair[] getSupportedCurrencyPairs() {
		return supportedCurrencyPairs;
	}

	public boolean isSupportedCurrencyPair(final CurrencyPair currencyPair) {
		for (final CurrencyPair cp : supportedCurrencyPairs) {
			if (cp.equals(currencyPair)) {
				return true;
			}
		}

		return false;
	}
}
