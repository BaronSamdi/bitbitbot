package rule;

import java.util.logging.Logger;

import com.amiramit.bitsafe.client.uitypes.UIBeanFactory;
import com.amiramit.bitsafe.client.uitypes.UIRuleTriggerResult;
import com.amiramit.bitsafe.server.BLUser;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;

public class LogAction extends Action {
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
		final BLUser blUser = BLUser.getUser(myRule.getUserId());
		final String userChannelId = blUser.getChannelClientID();

		if (userChannelId != null) {
			LOG.info("Action: " + this.toString()
					+ " triggered. Notifing user on channel.");
			final UIBeanFactory factory = AutoBeanFactorySource
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
