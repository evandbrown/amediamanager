package com.amediamanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amediamanager.config.ConfigurationSettings;
import com.amediamanager.config.ConfigurationSettings.ConfigSource;

@Controller
public class ConfigController {
	@RequestMapping(value="/config", method = RequestMethod.GET)
	public String home(ModelMap model) {
		// Get app config
		ConfigurationSettings config = ConfigurationSettings.getInstance();
		
		model.addAttribute("templateName", "config");
		model.addAttribute("configLoadedFrom", config.getReadableConfigSource());
		model.addAttribute("appConfig", config.getPropertiesAsString());
		model.addAttribute("credsConfig", "empty");
		return "base";
	}
}
