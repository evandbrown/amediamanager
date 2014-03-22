package com.amediamanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

public class EditableConfigurationProperty {

	private ConfigurationSettings.ConfigProps propertyName;
	private String propertyValue;
	private String displayName;
	
	public EditableConfigurationProperty() {}
	
	public EditableConfigurationProperty(ConfigurationSettings.ConfigProps propertyName, String propertyValue, String displayName) {
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
		this.displayName = displayName;
	}
	
	public ConfigurationSettings.ConfigProps getPropertyName() {
		return this.propertyName;
	}
	
	public void setPropertyName(ConfigurationSettings.ConfigProps propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue() {
		return this.propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getDisplayName() {
		return this.displayName;
	}

}
