package com.amediamanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ElasticTranscoderPipelineResource implements ProvisionableResource {
	
	@Autowired
	ConfigurationSettings config;
	
	@Override
	public ProvisionState getState() {
		return ProvisionState.UNPROVISIONED;
	}

	@Override
	public String getName() {
		return "Elastic Transcoder Pipeline";
	}

	@Override
	public void provision() {
		config.getConfigurationProvider().persistNewProperty("the-ets-arn-key", "the-ets-arn-value");
	}

}
