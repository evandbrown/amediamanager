package com.amediamanager.config;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class ConfigurationProviderChain implements ConfigurationProvider {
	private Properties properties;
	private ConfigurationProvider theProvider;
	private List<ConfigurationProvider> configurationProviders =
			new LinkedList<ConfigurationProvider>();
	
	public ConfigurationProviderChain(ConfigurationProvider... configurationProviders) {
		for(ConfigurationProvider configurationProvider : configurationProviders) {
			this.configurationProviders.add(configurationProvider);
		}
	}

	@Override
	public Properties getProperties() {
		for(ConfigurationProvider provider : this.configurationProviders) {
			this.properties = provider.getProperties();
			if(this.properties != null) {
				this.theProvider = provider;  
				break;
			}
		}
		if(properties == null) {
			throw new RuntimeException("Unable to load properties from any provider in the chain.");
		}
		return properties;
	}

	@Override
	public void refresh() {
		this.properties = getProperties();
	}
	
	@Override
	public String getPrettyName() {
		return this.getTheProvider().getPrettyName();
	}
	
	public ConfigurationProvider getTheProvider() {
		return this.theProvider;
	}
}
