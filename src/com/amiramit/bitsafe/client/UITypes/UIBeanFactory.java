package com.amiramit.bitsafe.client.UITypes;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

//Declare the factory type
public interface UIBeanFactory extends AutoBeanFactory {
	public AutoBean<UITicker> ticker();
	public AutoBean<UIRuleTriggerResult> ruleTriggerResult();
}