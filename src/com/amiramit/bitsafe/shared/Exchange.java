package com.amiramit.bitsafe.shared;


public enum Exchange {
	MtGox(new CurrencyPair[] { CurrencyPair.BTCUSD });

	private CurrencyPair[] supportedCurrencyPairs;

	Exchange(CurrencyPair[] supportedCurrencyPairs) {
		this.supportedCurrencyPairs = supportedCurrencyPairs;
	}

	public CurrencyPair[] getSupportedCurrencyPairs() {
		return supportedCurrencyPairs;
	}

	public boolean isSupportedCurrencyPair(CurrencyPair currencyPair) {
		for (CurrencyPair cp : supportedCurrencyPairs) {
			if (cp.equals(currencyPair)) {
				return true;
			}
		}

		return false;
	}
}
