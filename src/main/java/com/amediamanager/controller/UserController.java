package com.amediamanager.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amediamanager.domain.User;
import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;
import com.amediamanager.exceptions.UserExistsException;
import com.amediamanager.service.UserService;
 
@Controller
public class UserController {
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value="/register", method = RequestMethod.POST)
	public String register(@ModelAttribute User user) {
		
		try {
			userService.save(user);
			
			List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
	        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
			Authentication auth = 
					  new UsernamePasswordAuthenticationToken(user.getEmail(), null, grantedAuths);

					SecurityContextHolder.getContext().setAuthentication(auth);
		} catch (DataSourceTableDoesNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	return "home";
	}
}
