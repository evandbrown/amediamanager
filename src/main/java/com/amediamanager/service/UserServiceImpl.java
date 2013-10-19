package com.amediamanager.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.amediamanager.config.ConfigurationSettings;
import com.amediamanager.dao.UserDao;
import com.amediamanager.domain.User;
import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;
import com.amediamanager.exceptions.UserDoesNotExistException;
import com.amediamanager.exceptions.UserExistsException;

@Service("userService")
public class UserServiceImpl implements UserService, AuthenticationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private ConfigurationSettings configurationSettings;

    @Override
    public void save(User user) throws DataSourceTableDoesNotExistException, UserExistsException {
        // MD5 password
        user.setPassword(MD5HashPassword(user.getPassword()));

        // Default profile pic URL
        user.setProfilePicKey(getDefaultProfilePicKey());

        userDao.save(user);
    }

    @Override
    public void update(User user) {
        // Make sure the user exists
        User existing = this.find(user.getEmail());

        // If the user does not exist, throw an exception
        if(null == existing) {
            throw new UserDoesNotExistException();
        }

        // Don't allow empty password
        if(user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existing.getPassword());
        } else {
            // Otherwise MD5 password
            user.setPassword(MD5HashPassword(user.getPassword()));
        }

        // Don't allow empty profile pic
        if(user.getProfilePicKey() == null || user.getProfilePicKey() == "") {
            user.setProfilePicKey(existing.getProfilePicKey());
        }

        // Upload pic to S3 if it exists

        userDao.update(user);
    }

    @Override
    public User find(String email) {
        User user = null;
        try {
            user = userDao.find(email);
        } catch (DataSourceTableDoesNotExistException e) {
            LOG.info("Failed to find user {}", email, e);
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

        if(null == user || (! user.getPassword().equals(MD5HashPassword(password)))) {
            throw new BadCredentialsException("Invalid username or password");
        }

        List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));

        // Create new auth token
        auth = new UsernamePasswordAuthenticationToken(username, null, grantedAuths);
        auth.setDetails(user);
        return auth;
    }

    /**
     * Convert a plain-text password string to an MD5 hex string
     *
     * @param plainTextPassword
     *            The plain-text password to be encoded as an MD5 hex string
     *
     * @return MD5-encoded hex string of the provided password.
     */
    public static String MD5HashPassword(String plainTextPassword) {
        return org.apache.commons.codec.digest.DigestUtils
                .md5Hex(plainTextPassword);
    }

    @Override
    public boolean supports(Class<?> arg0) {
        return true;
    }

    /**
     * Default placeholder image for profile pic
     * @return
     */
    private String getDefaultProfilePicKey() {
        return "http://" + configurationSettings.getProperty(ConfigurationSettings.ConfigProps.S3_UPLOAD_BUCKET) + ".s3.amazonaws.com/" + configurationSettings.getProperty(ConfigurationSettings.ConfigProps.DEFAULT_PROFILE_PIC_KEY);
    }
}
