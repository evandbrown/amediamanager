package com.amediamanager.config;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.amediamanager.config.ConfigurationSettings.ConfigProps;

public class S3ConfigurationProvider extends ConfigurationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(S3ConfigurationProvider.class);
    private final String s3ConfigBucketEnvVarName = "S3_CONFIG_BUCKET";
    private final String s3ConfigKeyEnvVarName = "S3_CONFIG_KEY";
    private final String bucket;
    private final String key;
    private Properties properties;

    public S3ConfigurationProvider() {
        this.bucket = System.getProperty(this.s3ConfigBucketEnvVarName);
        this.key = System.getProperty(this.s3ConfigKeyEnvVarName);
    }

    @Override
    public Properties getProperties() {
        if (this.properties == null && bucket != null && key != null) {
            AmazonS3 s3Client = new AmazonS3Client();
            try {
                S3Object object = s3Client.getObject(this.bucket, this.key);
                if (object != null) {
                    this.properties = new Properties();
                    try {
                        this.properties.load(object.getObjectContent());
                    } catch (IOException e) {
                        this.properties = null;
                        LOG.warn("Failed to get properties.", e);
                    } finally {
                        try {
                            object.close();
                        } catch (IOException e) {
                            // Don't care
                        }
                    }
                }
            } catch (AmazonS3Exception ase) {
                LOG.error("Error loading config from s3://{}/{}", new Object[]{this.bucket, this.key, ase});
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
    public void persistNewProperty(String key, String value) {
        if (this.properties != null) {
            this.properties.put(key, value);
            AmazonS3 s3Client = new AmazonS3Client();
            try {
                s3Client.putObject(this.bucket, this.key,
                        IOUtils.toInputStream(this.propsToString(this.properties)), null);
            } catch (AmazonS3Exception ase) {
                LOG.error("Error persisting config from s3://{}/{}", new Object[]{this.bucket, this.key, ase});
            }
        }
    }

    @Override
    public void persistNewProperty(ConfigProps property, String value) {
        persistNewProperty(property.name(), value);
    }

    @Override
    public String getPrettyName() {
        String source = "s3://" + this.bucket + "/" + this.key;
        return this.getClass().getSimpleName() + " (" + source + ")";
    }

}
