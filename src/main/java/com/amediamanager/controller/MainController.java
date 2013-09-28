package com.amediamanager.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;
import com.amediamanager.service.UserService;

@Controller
public class MainController {
	@Autowired
	UserService userService;
	
	@RequestMapping(value={"/", "/home", "/welcome"}, method = RequestMethod.GET)
	public String home(ModelMap model, HttpSession session) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		// If the user is not authenticated, show a different view
		if (auth instanceof AnonymousAuthenticationToken) {
			model.addAttribute("templateName", "welcome");
		} else {
			// If there is no User object in the session, get one
			if(session.getAttribute("user") == null) {
			    session.setAttribute("user", userService.find(auth.getName()));
			}
			model.addAttribute("templateName", "home");
		}
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
	
	@RequestMapping(value="/login-failed", method = RequestMethod.GET)
	public String loginerror(ModelMap model, HttpSession session) {
		model.addAttribute("error", "Login failed.");
		return home(model, session);
	}
 
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logout(ModelMap model, HttpSession session) {
		return home(model, session);
	}
}
