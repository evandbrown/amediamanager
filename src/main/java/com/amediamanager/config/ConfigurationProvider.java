package com.amediamanager.config;

import java.util.Properties;

public interface ConfigurationProvider {
	public Properties getProperties();
	public String getPrettyName();
	public void refresh();
}
