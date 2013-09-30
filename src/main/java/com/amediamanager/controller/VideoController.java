package com.amediamanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amediamanager.domain.Video;
import com.amediamanager.service.VideoService;

@Controller
public class VideoController {
	@Autowired
	VideoService videoService;
	
	@RequestMapping(value="/videos", method = RequestMethod.GET)
	public String videos(ModelMap model) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		List<Video> videos = videoService.findByUserEmail(userEmail);
		
		model.addAttribute("videos", videos);
		model.addAttribute("templateName", "only_videos");
		
		return "base";
	}
}
