package com.amediamanager.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amediamanager.config.ConfigurationSettings.ConfigProps;

public class ClassResourceConfigurationProvider extends ConfigurationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(ClassResourceConfigurationProvider.class);
    private final String resourceFilePath;
    private Properties properties;

    public ClassResourceConfigurationProvider(String resourceFilePath) {
        this.resourceFilePath = resourceFilePath;
    }

    @Override
    public Properties getProperties() {
        if (properties == null) {
            InputStream stream = getClass().getResourceAsStream(
                    this.resourceFilePath);
            try {
                properties = new Properties();
                properties.load(stream);
            } catch (IOException e) {
                properties = null;
                LOG.error("Failed to get properties.", e);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    // Don't care
                }
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
        return this.getClass().getSimpleName() + " (" + this.resourceFilePath + ")";
    }

    @Override
    public void persistNewProperty(String key, String value) {
        //TODO: implement
    }

    @Override
    public void persistNewProperty(ConfigProps property, String value) {
        persistNewProperty(property.name(), value);
    }

}
