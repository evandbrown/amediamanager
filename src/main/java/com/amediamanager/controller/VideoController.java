package com.amediamanager.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amediamanager.domain.Privacy;
import com.amediamanager.domain.TagSet;
import com.amediamanager.domain.Video;
import com.amediamanager.service.VideoService;
import com.amediamanager.util.CommaDelimitedTagEditor;

@Controller
public class VideoController {
	@Autowired
	VideoService videoService;
	
	@ModelAttribute("allPrivacy")
	public List<Privacy> populatePrivacy() {
	    return Arrays.asList(Privacy.ALL);
	}
	
	@RequestMapping(value="/videos", method = RequestMethod.GET)
	public String videos(ModelMap model) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		List<Video> videos = videoService.findByUserEmail(userEmail);
		
		model.addAttribute("videos", videos);
		model.addAttribute("templateName", "only_videos");
		
		return "base";
	}
	
	@RequestMapping(value="/video/**", method = RequestMethod.GET)
	public String videoGet(ModelMap model) {
		
		// Get a random video
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		List<Video> videos = videoService.findByUserEmail(userEmail);
		
		model.addAttribute("video", videos.get(0));
		model.addAttribute("templateName", "video_edit");
		
		return "base";
	}
	
	@RequestMapping(value="/video/**", method = RequestMethod.POST)
	public String videoEdit(@ModelAttribute Video video, BindingResult result, RedirectAttributes attr, HttpSession session) {
		return "redirect:/";
	}
	
	@InitBinder
	public void initDateBinder(final WebDataBinder dataBinder) {
	    // Bind dates
		final String dateformat = "MM/dd/yyyy";
	    final SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
	    sdf.setLenient(false);
	    dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, false));
	}
	
	@InitBinder
	public void initTagsBinder(final WebDataBinder dataBinder) {
		// Bind tags
	    dataBinder.registerCustomEditor(TagSet.class, new CommaDelimitedTagEditor());
	}
}
