package com.amediamanager.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amediamanager.dao.TagDao.TagCount;
import com.amediamanager.domain.NewUser;
import com.amediamanager.domain.Video;
import com.amediamanager.service.TagsService;
import com.amediamanager.service.UserService;
import com.amediamanager.service.VideoService;

@Controller
public class MainController {
	@Autowired
	UserService userService;

	@Autowired
	VideoService videoService;
	
	@Autowired
	TagsService tagService;

	@RequestMapping(value = { "/", "/home", "/welcome" }, method = RequestMethod.GET)
	public String home(ModelMap model, HttpSession session) {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		// If the user is not authenticated, show a different view
		if (auth instanceof AnonymousAuthenticationToken) {
			model.addAttribute("templateName", "welcome");
		} else {
			List<Video> videos = new ArrayList<Video>();
			/* begin pjayara added */
			List<TagCount> tags = new ArrayList<TagCount>();
			/* end pjayara added */
			List<Video> v = new ArrayList<Video>();
			try {
				// Get user's videos
				videos = videoService.findByUserId(auth.getName());
				/* begin pjayara added */
				tags = tagService.getTagsForUser(auth.getName());
				System.err.println(tags);
				v = tagService.getVideosForUserByTag(auth.getName(), "abcd");
				System.err.println("AA"+v);
				/* end pjayara added */
				// Add expiring URLs (1 hour)
				videos = videoService.generateExpiringUrls(videos, 1000*60*60);
			} catch (Exception e) {
				return "redirect:/config";
			}
			model.addAttribute("videos", videos);
			model.addAttribute("templateName", "only_videos");
		}
		return "base";
	}


	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String error(ModelMap model) {
		return "base";
	}

	@RequestMapping(value = "/empty", method = RequestMethod.GET)
	public String empty(ModelMap model) {
		return "base";
	}

	@RequestMapping(value = "/not-found", method = RequestMethod.GET)
	public String notFound(ModelMap model) {
		model.addAttribute("error", "Resource not found");
		return "base";
	}

	@RequestMapping(value = "/login-failed", method = RequestMethod.GET)
	public String loginerror(ModelMap model, HttpSession session) {
		model.addAttribute("error", "Login failed.");
		return home(model, session);
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(ModelMap model, HttpSession session) {
		return home(model, session);
	}
}
