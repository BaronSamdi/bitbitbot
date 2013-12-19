package com.amiramit.bitsafe.client.uitypes;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

//Declare the factory type
public interface UIBeanFactory extends AutoBeanFactory {
	AutoBean<UITicker> ticker();

	AutoBean<UIRuleTriggerResult> ruleTriggerResult();
}