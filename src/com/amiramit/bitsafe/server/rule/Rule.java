package com.amiramit.bitsafe.server.rule;

import static com.amiramit.bitsafe.server.OfyService.ofy;

import java.util.Date;

import com.amiramit.bitsafe.client.dto.ActionDTO;
import com.amiramit.bitsafe.client.dto.RuleDTO;
import com.amiramit.bitsafe.client.dto.TriggerDTO;
import com.amiramit.bitsafe.client.dto.UIVerifyException;
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

	// Just for Objectify
	@SuppressWarnings("unused")
	private Rule() {
	}

	public Rule(final Long key, final Date createDate, final long userId,
			final String description, final boolean active,
			final Trigger trigger, final Action action) {
		this.key = key;
		this.createDate = createDate;
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
	}

	public void saveNow() {
		// We must save now() to make id avail.
		ofy().save().entity(this).now();
		assert (this.getKey() != null);
	}

	public static Rule fromDTO(final Long userID, final RuleDTO uiRule)
			throws UIVerifyException {
		final Trigger retTrigger = Trigger.fromDTO(uiRule.getTrigger());
		final Action retAction = Action.fromDTO(uiRule.getAction());
		Date ruledate = uiRule.getCreateDate();
		if (ruledate == null) {
			ruledate = new Date();
		}

		final Rule ret = new Rule(uiRule.getKey(), ruledate, userID,
				uiRule.getDescription(), uiRule.getActive(), retTrigger,
				retAction);
		return ret;
	}

	public static RuleDTO toDTO(final Rule curRule) throws UIVerifyException {
		final TriggerDTO retTrigger = Trigger.toDTO(curRule.getTrigger());
		final ActionDTO retAction = Action.toDTO(curRule.getAction());
		final RuleDTO ret = new RuleDTO(curRule.getKey(),
				curRule.getCreateDate(), curRule.getDescription(),
				curRule.getActive(), retTrigger, retAction);
		ret.verify(false);
		return ret;
	}
}