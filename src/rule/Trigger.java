package rule;

import java.io.Serializable;

import com.amiramit.bitsafe.shared.ExchangeName;

public abstract class Trigger implements Serializable {
	private static final long serialVersionUID = 1L;

	private ExchangeName atExchange;

	public Trigger(final ExchangeName atExchange) {
		super();
		this.atExchange = atExchange;
	}

	public ExchangeName getAtExchange() {
		return atExchange;
	}

	public abstract boolean check();
}
