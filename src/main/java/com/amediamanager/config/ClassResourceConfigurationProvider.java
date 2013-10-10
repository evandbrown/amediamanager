package com.amediamanager.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.amediamanager.config.ConfigurationProvider;


public class ClassResourceConfigurationProvider implements
		ConfigurationProvider {

	private String resourceFilePath;
	private Properties properties;
	
	public ClassResourceConfigurationProvider(String resourceFilePath) {
		this.resourceFilePath = resourceFilePath;
	}
	
	@Override
	public Properties getProperties() {
		InputStream stream = getClass().getResourceAsStream(this.resourceFilePath);
		try {
			properties = new Properties();
			properties.load(stream);
		} catch (IOException e) {
			properties = null;
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// Don't care
			}
		}
		
		return properties;
	}

	@Override
	public void refresh() {}
	
	@Override
	public String getPrettyName() {
		return this.getClass().getSimpleName() + " (" + this.resourceFilePath + ")";
	}

}
