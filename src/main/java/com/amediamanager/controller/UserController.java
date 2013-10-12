package com.amediamanager.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amediamanager.domain.NewUser;
import com.amediamanager.domain.User;
import com.amediamanager.exceptions.UserExistsException;
import com.amediamanager.service.UserService;
 
@Controller
public class UserController {
	
	@Autowired
	UserService userService;
	
	
	@RequestMapping(value="/register", method = RequestMethod.POST)
	public String register(@Valid NewUser newUser, BindingResult result, RedirectAttributes attr, ModelMap model) {
		
		try {
			if(result.hasErrors()) {
				model.addAttribute("templateName", "welcome");
	            return "base";
	        }
			
			userService.save(newUser);
			User user = userService.find(newUser.getEmail());
			
			List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
	        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
	        
	        // Authenticate the user
	        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getEmail(), null, grantedAuths);
	        
	        // Save user in session
	        auth.setDetails(user);
	        
	        SecurityContextHolder.getContext().setAuthentication(auth);
		} catch (UserExistsException e) {
			attr.addFlashAttribute("error", "That user already exists.");
			e.printStackTrace();
		}
	
	return "redirect:/welcome";
	}
	
	@RequestMapping(value="/user", method = RequestMethod.GET)
	public String userGet(ModelMap model, HttpSession session) {
		model.addAttribute("templateName", "user");
		return "base";
	}
	
	@RequestMapping(value="/user", method = RequestMethod.POST)
	public String userPost(@ModelAttribute User user, BindingResult result, RedirectAttributes attr, HttpSession session) {
		// Don't allow user name changes
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		user.setId(auth.getName());
		user.setEmail(auth.getName());
		
		// Update user and re-set val in session
		userService.update(user);
		
		// Update user auth object in security context
		UsernamePasswordAuthenticationToken newAuth= new UsernamePasswordAuthenticationToken(auth.getName(), null, auth.getAuthorities());
		newAuth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
	
		return "redirect:/user";
	}
}
