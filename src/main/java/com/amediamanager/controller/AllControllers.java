package com.amediamanager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.amediamanager.domain.User;

@ControllerAdvice
public class AllControllers {
	@ModelAttribute("user")
	public User populateUser() {
		Object user = SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		if(user != null && user instanceof User) {
			return (User)user;
		} else return null;
	}
}
