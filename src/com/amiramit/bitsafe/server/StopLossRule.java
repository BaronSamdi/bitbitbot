package com.amiramit.bitsafe.server;

import java.math.BigDecimal;
import java.util.logging.Logger;

import com.amiramit.bitsafe.client.UITypes.UIBeanFactory;
import com.amiramit.bitsafe.client.UITypes.UIRuleTriggerResult;
import com.amiramit.bitsafe.client.UITypes.UITicker;
import com.amiramit.bitsafe.shared.ExchangeName;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Serialize;

@EntitySubclass(index = true)
@Cache
public class StopLossRule extends TradeRule {
	private static final Logger LOG = Logger.getLogger(StopLossRule.class
			.getName());

	@Serialize
	private BigDecimal atPrice;

	protected StopLossRule() {
	}

	public StopLossRule(final String userId, final String name,
			final Boolean active, final ExchangeName atExchange,
			final BigDecimal atPrice) {
		super(userId, name, active, atExchange);
		assert atPrice != null;
		this.atPrice = atPrice;
	}

	public BigDecimal getAtPrice() {
		return atPrice;
	}

	@Override
	public boolean checkTrigger() {
		if (!super.checkTrigger()) {
			return false;
		}

		BLLastTicker lastTicker = BLLastTicker.getLastTicker(getAtExchange());
		LOG.info("StopLossRule: " + this.toString()
				+ " is at checkTrigger with lastTicker = " + lastTicker);
		if (lastTicker.getLast().compareTo(atPrice) < 0) {
			LOG.info("StopLossRule: " + this.toString() + " return true");
			return true;
		}

		LOG.info("StopLossRule: " + this.toString() + " return false");
		return false;
	}

	@Override
	public boolean trigger() {
		LOG.info("StopLossRule: " + this.toString() + " is at trigger()");
		// Make sure checkTrigger() condition is still valid as there might be
		// some time passed
		// between rule checkTrigger() call and trigger() call
		if (checkTrigger()) {
			LOG.severe("StopLossRule: " + this.toString() + " triggered!");
			// Currently just print to log ...
			// TODO: do the actual sell in relevant exchange ...

			// Since this rule has been triggered and we don't want it to
			// trigger
			// again, make it inactive
			this.setActive(false);
			this.save();

			// Send rule trigger notification to user
			BLUser blUser = BLUser.getUser(this.getUserId());
			String userChannelId = blUser.getChannelID();
			
			if (userChannelId != null) {
				LOG.info("Notifing user on channel: " + userChannelId);
				UIBeanFactory factory = AutoBeanFactorySource
						.create(UIBeanFactory.class);
				// TODO: just for check, remove this!
				AutoBean<UITicker> tickerBean = factory.ticker();
				BLLastTicker lastTicker = BLLastTicker
						.getLastTicker(getAtExchange());

				AutoBean<UIRuleTriggerResult> triggerResultBean = factory
						.ruleTriggerResult();
				triggerResultBean.as().setRuleId(this.getKey());
				triggerResultBean.as().setUITicker(tickerBean.as());

				String beanPayload = AutoBeanCodex.encode(triggerResultBean)
						.getPayload();
				ChannelServiceFactory.getChannelService().sendMessage(
						new ChannelMessage(userChannelId, beanPayload));
			} else {
				LOG.info("User not connected.");
			}
			
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "StopLossRule [" + super.toString() + ", atPrice=" + atPrice
				+ "]";
	}
}
