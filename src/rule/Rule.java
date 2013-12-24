package rule;

import static com.amiramit.bitsafe.server.OfyService.ofy;

import java.util.Date;

import com.amiramit.bitsafe.shared.ExchangeName;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;
import com.googlecode.objectify.condition.IfTrue;

@Entity
@Cache
public final class Rule {

	@Id
	private Long key;

	@Index
	private long userId;

	private Date createDate;

	private String description;

	@Index(IfTrue.class)
	private boolean active;

	@Serialize
	private Trigger trigger;

	@Serialize
	private Action action;

	protected Rule() {
	}

	public Rule(final long userId, final String description,
			final boolean active, final ExchangeName atExchange,
			final Trigger trigger, final Action action) {
		assert description != null;
		assert atExchange != null;
		this.createDate = new Date();
		this.userId = userId;
		this.description = description;
		this.active = active;
		this.trigger = trigger;
		this.action = action;
	}

	public Long getKey() {
		return key;
	}

	public String getDescription() {
		return description;
	}

	public long getUserId() {
		return this.userId;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setUser(final long userId) {
		this.userId = userId;
	}

	public boolean getActive() {
		return active;
	}

	protected void setActive(final boolean active) {
		this.active = active;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public Action getAction() {
		return action;
	}

	@Override
	public String toString() {
		return "UserRule [key=" + key + ", userId=" + userId + ", createDate="
				+ createDate + ", name=" + description + ", active=" + active
				+ ",trigger=" + trigger + ", action=" + action + "]";
	}

	public void save() {
		ofy().save().entity(this);
		assert (this.getKey() != null);
	}
}