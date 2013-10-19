package com.amediamanager.config;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.amediamanager.config.ConfigurationSettings.ConfigProps;

public class ConfigurationProviderChain extends ConfigurationProvider {
	private Properties properties;
	private ConfigurationProvider theProvider;
	private final List<ConfigurationProvider> configurationProviders = new LinkedList<ConfigurationProvider>();

	public ConfigurationProviderChain(
			ConfigurationProvider... configurationProviders) {
		for (ConfigurationProvider configurationProvider : configurationProviders) {
			this.configurationProviders.add(configurationProvider);
		}
		getProperties();
	}

	@Override
	public Properties getProperties() {
		if (this.properties == null) {
			for (ConfigurationProvider provider : this.configurationProviders) {
				provider.refresh();
				this.properties = provider.getProperties();
				if (this.properties != null) {
					this.theProvider = provider;
					break;
				}
			}
			if (properties == null) {
				throw new RuntimeException(
						"Unable to load properties from any provider in the chain.");
			}
		}
		return properties;
	}

	@Override
	public void refresh() {
		this.properties = null;
		this.properties = getProperties();
	}

	@Override
	public String getPrettyName() {
		return this.getTheProvider().getPrettyName();
	}

	@Override
	public void persistNewProperty(String key, String value) {
		this.theProvider.persistNewProperty(key, value);
		this.refresh();
	}

	@Override
	public void persistNewProperty(ConfigProps property, String value) {
		persistNewProperty(property.name(), value);
	}

	public ConfigurationProvider getTheProvider() {
		return this.theProvider;
	}
}
