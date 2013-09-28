package com.amediamanager.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;


import com.amediamanager.domain.User;
import com.amediamanager.dao.ConnectionManager;
import com.amediamanager.dao.UserDao;
import com.amediamanager.exceptions.*;

@Service("userService")
public class UserServiceImpl 
	implements UserService, AuthenticationProvider {

	@Autowired
	private UserDao userDao;
	
	@Autowired private ConnectionManager connectionManager;
	
	@Override
	public void save(User user) throws DataSourceTableDoesNotExistException, UserExistsException {
		// MD5 password
		user.setPassword(User.MD5HashPassword(user.getPassword()));
		userDao.save(user);
	}

	@Override
	public void update(User user) {
		userDao.update(user);
	}
	
	@Override
	public User find(String email) {
		User user = null;
		try {
			user = userDao.find(email);
		} catch (DataSourceTableDoesNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return user;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
	    String username = String.valueOf(auth.getPrincipal());
	    String password = String.valueOf(auth.getCredentials());
	    
	    User user = find(username);	
	    
	    if(null == user || (! user.getPassword().equals(User.MD5HashPassword(password)))) {
	    	throw new BadCredentialsException("Invalid username or password");
	    }
	    
	    List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(username, null, grantedAuths);
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}
}
