package rule;

import java.io.Serializable;

public abstract class Action implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract void run(final Rule myRule);
}
