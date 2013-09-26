package com.amediamanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;
 
@Controller
public class LoginController {
	
	@RequestMapping(value="/welcome", method = RequestMethod.GET)
	public String welcome(ModelMap model) {
		model.addAttribute("templateName", "welcome");
		return "base";
	}
	
	@RequestMapping(value="/error", method = RequestMethod.GET)
	public String error(ModelMap model) {
  		throw new DataSourceTableDoesNotExistException();
	}
	
	@RequestMapping(value="/login-failed", method = RequestMethod.GET)
	public String loginerror(ModelMap model) {
		model.addAttribute("error", "Login failed.");
		return welcome(model);
	}
 
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logout(ModelMap model) {
		return welcome(model);
	}
}
