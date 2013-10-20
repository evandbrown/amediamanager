package com.amediamanager.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amediamanager.config.ConfigurationSettings;
import com.amediamanager.config.ProvisionableResource;
import com.amediamanager.config.S3ConfigurationProvider;

@Controller
public class ConfigController {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ConfigurationSettings config;

	@RequestMapping(value="/config", method = RequestMethod.GET)
	public String config(ModelMap model) {
		model.addAttribute("templateName", "config");
		model.addAttribute("configLoadedFrom", config.getConfigurationProvider().getPrettyName());
		model.addAttribute("appConfig", config.toString());
		model.addAttribute("accessKey", config.getAWSCredentialsProvider().getCredentials().getAWSAccessKeyId());
		model.addAttribute("isToken", config.getAWSCredentialsProvider().getCredentials() instanceof BasicSessionCredentials);

		// Inject info about S3 config
		if(config.getConfigurationProvider() instanceof S3ConfigurationProvider) {
			model.addAttribute("isS3Config", true);
			model.addAttribute("configBucket", ((S3ConfigurationProvider)config.getConfigurationProvider()).getBucket());
			model.addAttribute("configKey", ((S3ConfigurationProvider)config.getConfigurationProvider()).getKey());
		}
		
		Map<String, ProvisionableResource> provisionableResources = context.getBeansOfType(ProvisionableResource.class);

		// Get all ProvisionableResources
		model.addAttribute("prs", provisionableResources);

		return "base";
	}

	@RequestMapping(value="/config/provision/{provisionableBeanName}", method=RequestMethod.GET)
	public String provision(ModelMap model, @PathVariable String provisionableBeanName) {
		ProvisionableResource pr = (ProvisionableResource)context.getBean(provisionableBeanName);
		pr.provision();
		return config(model);
	}
}
