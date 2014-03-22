package com.amediamanager.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.amediamanager.config.ConfigurationSettings.ConfigProps;

@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class EditableConfigurationProperties {
	@Autowired
	ConfigurationSettings config;
	
	private List<EditableConfigurationProperty> configProps;
	
	@PostConstruct
	public void initialize() {
		configProps = new ArrayList<EditableConfigurationProperty>();
		configProps.add(new EditableConfigurationProperty(ConfigProps.CACHE_ENABLED, config.getProperty(ConfigProps.CACHE_ENABLED), "Enable Caching?"));
	}
	
	public List<EditableConfigurationProperty> getConfigProps() {
		return configProps;
	}
	
	public void setConfigProps(List<EditableConfigurationProperty> configProps) {
		this.configProps = configProps;
	}
}
