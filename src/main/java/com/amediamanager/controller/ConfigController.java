package com.amediamanager.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;

import com.amediamanager.config.*;

@Controller
public class ConfigController {
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private ConfigurationSettings config;
	
	@RequestMapping(value="/config", method = RequestMethod.GET)
	public String home(ModelMap model) {
		model.addAttribute("templateName", "config");
		model.addAttribute("configLoadedFrom", config.getReadableConfigSource());
		model.addAttribute("appConfig", config.toString());
		model.addAttribute("accessKey", config.getAWSCredentials().getAWSAccessKeyId());
		model.addAttribute("secretKey", config.getObfuscatedSecretKey());

		Map<String, ProvisionableResource> provisionableResources = context.getBeansOfType(ProvisionableResource.class);
		
		// Get all ProvisionableResources
		model.addAttribute("prs", provisionableResources.values());
		
		for(ProvisionableResource pr : provisionableResources.values()) {
			pr.getState();
		}
		
		return "base";
	}
}
