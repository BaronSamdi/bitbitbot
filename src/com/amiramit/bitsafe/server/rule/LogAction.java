package com.amiramit.bitsafe.server.rule;

import java.util.logging.Logger;

import com.amiramit.bitsafe.client.uitypes.uibeans.UIBeanFactory;
import com.amiramit.bitsafe.client.uitypes.uibeans.UIRuleTriggerResult;
import com.amiramit.bitsafe.server.BLUser;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;

public class LogAction extends Action {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(LogAction.class
			.getName());

	@Override
	public void run(final Rule myRule) {
		assert myRule.getAction().equals(this);
		// print to log, notify user, disable log

		// Since this rule has been triggered and we don't want it to
		// trigger again, make it inactive
		myRule.setActive(false);
		myRule.save();

		// Try to send rule trigger notification to user
		final BLUser blUser = BLUser.getUserFromId(myRule.getUserId());
		final String userChannelId = blUser.getChannelClientID();

		if (userChannelId != null) {
			LOG.info("Action: " + this.toString()
					+ " triggered. Notifing user on channel.");
			final com.amiramit.bitsafe.client.uitypes.uibeans.UIBeanFactory factory = AutoBeanFactorySource
					.create(UIBeanFactory.class);

			final AutoBean<UIRuleTriggerResult> triggerResultBean = factory
					.ruleTriggerResult();
			triggerResultBean.as().setRuleId(myRule.getKey());

			final String beanPayload = AutoBeanCodex.encode(triggerResultBean)
					.getPayload();
			ChannelServiceFactory.getChannelService().sendMessage(
					new ChannelMessage(userChannelId, beanPayload));
		} else {
			LOG.info("Action: " + this.toString()
					+ " triggered. User not connected.");
		}
	}
}
