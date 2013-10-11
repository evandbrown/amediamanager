package com.amediamanager.config;

import java.util.Enumeration;
import java.util.Properties;

public abstract class ConfigurationProvider {
	
	public abstract Properties getProperties();
	public abstract String getPrettyName();
	public abstract void refresh();
	public abstract void persistNewProperty(String key, String value);
	
	public String propsToString(Properties properties) {
		StringBuilder sb = new StringBuilder();
		Enumeration<?> e = properties.propertyNames();
		while (e.hasMoreElements()){ 
			String key = (String) e.nextElement();
			sb.append(key);
			sb.append("=");
			sb.append(properties.getProperty(key));
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
