package com.amediamanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;

@Controller
public class MainController {
	@RequestMapping(value={"/", "/home"}, method = RequestMethod.GET)
	public String home(ModelMap model) {
		model.addAttribute("templateName", "home");
		return "base";
	}
	
	@RequestMapping(value="/videos", method = RequestMethod.GET)
	public String videos(ModelMap model) {
		model.addAttribute("templateName", "only_videos");
		return "base";
	}
	
	@RequestMapping(value="/error", method = RequestMethod.GET)
	public String error(ModelMap model) {
  		return "base";
	}
	
	@RequestMapping(value="/empty", method = RequestMethod.GET)
	public String empty(ModelMap model) {
  		return "base";
	}
	
	@RequestMapping(value="/not-found", method = RequestMethod.GET)
	public String notFound(ModelMap model) {
		model.addAttribute("error", "Resource not found");
		return "base";
	}
}
