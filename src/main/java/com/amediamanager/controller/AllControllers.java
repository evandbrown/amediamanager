package com.amediamanager.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.amediamanager.domain.User;

@ControllerAdvice
public class AllControllers {
    @ModelAttribute("user")
    public User populateUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) { return null; }
        Authentication auth = context.getAuthentication();
        if (auth == null) { return null; }
        Object user = auth.getDetails();

        return (user != null && user instanceof User) ? (User) user : null;
    }
}
