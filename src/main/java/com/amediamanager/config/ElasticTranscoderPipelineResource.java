package com.amediamanager.config;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ElasticTranscoderPipelineResource implements ProvisionableResource {

	private static final String name = "Elastic Transcoder Pipeline";
	
	@Override
	public ProvisionState getState() {
		return ProvisionState.UNPROVISIONED;
	}

	@Override
	public String getName() {
		return ElasticTranscoderPipelineResource.name;
	}

	@Override
	public void provision() {
		// TODO Auto-generated method stub

	}

}
