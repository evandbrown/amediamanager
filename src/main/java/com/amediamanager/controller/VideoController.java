package com.amediamanager.controller;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.DirectFieldAccessor;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amediamanager.config.ConfigurationSettings;
import com.amediamanager.config.ConfigurationSettings.ConfigProps;
import com.amediamanager.domain.ContentType;
import com.amediamanager.domain.Privacy;
import com.amediamanager.domain.TagSet;
import com.amediamanager.domain.User;
import com.amediamanager.domain.Video;
import com.amediamanager.service.VideoService;
import com.amediamanager.util.CommaDelimitedTagEditor;
import com.amediamanager.util.S3FormSigner;
import com.amediamanager.util.VideoUploadFormSigner;

@Controller
public class VideoController {
	@Autowired
	VideoService videoService;

	@Autowired
	ConfigurationSettings config;

	@Autowired
	AmazonS3 s3Client;

	@ModelAttribute("allPrivacy")
	public List<Privacy> populatePrivacy() {
		return Arrays.asList(Privacy.ALL);
	}

	@ModelAttribute("allContentType")
	public List<ContentType> populateContentType() {
		return Arrays.asList(ContentType.ALL);
	}

	@RequestMapping(value = "/videos", method = RequestMethod.GET)
	public String videos(ModelMap model) {
		String userEmail = SecurityContextHolder.getContext()
				.getAuthentication().getName();
		List<Video> videos = videoService.findByUserEmail(userEmail);

		model.addAttribute("videos", videos);
		model.addAttribute("templateName", "only_videos");

		return "base";
	}

	@RequestMapping(value = "/video/**", method = RequestMethod.GET)
	public String videoGet(ModelMap model) {

		// Get a random video
		String userEmail = SecurityContextHolder.getContext()
				.getAuthentication().getName();
		List<Video> videos = videoService.findByUserEmail(userEmail);

		model.addAttribute("video", videos.get(0));
		model.addAttribute("templateName", "video_edit");

		return "base";
	}

	@RequestMapping(value = "/video/**", method = RequestMethod.POST)
	public String videoEdit(@ModelAttribute Video video, BindingResult result,
			RedirectAttributes attr, HttpSession session) {
		return "redirect:/";
	}

	@RequestMapping(value = "/video/upload", method = RequestMethod.GET)
	public String videoUpload(ModelMap model, HttpServletRequest request,
			HttpSession session) {
		// Video redirect URL
		String redirectUrl = request.getScheme() + "://"
				+ request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath() + "/video/ingest";

		// Prepare S3 form upload
		VideoUploadFormSigner formSigner = new VideoUploadFormSigner(
				config.getProperty(ConfigProps.S3_UPLOAD_BUCKET),
				config.getProperty(ConfigProps.S3_UPLOAD_PREFIX),
				(User) session.getAttribute("user"),
				config.getAWSCredentialsProvider(), redirectUrl);

		model.addAttribute("formSigner", formSigner);
		model.addAttribute("templateName", "video_upload");

		return "base";
	}

	@RequestMapping(value = "/video/ingest", method = RequestMethod.GET)
	public String videoIngest(ModelMap model,
			@RequestParam(value = "bucket") String bucket,
			@RequestParam(value = "key") String videoKey) throws ParseException {
		
		// From bucket and key, get metadata from video that was just uploaded
		GetObjectMetadataRequest metadataReq = new GetObjectMetadataRequest(
				bucket, videoKey);
		ObjectMetadata metadata = s3Client.getObjectMetadata(metadataReq);
		Map<String, String> userMetadata = metadata.getUserMetadata();
		
		Video video = new Video();
		
		video.setDescription(userMetadata.get("description"));
		video.setOwner(userMetadata.get("owner"));
		video.setTitle(userMetadata.get("title"));
		video.setPrivacy(Privacy.fromName(userMetadata.get("privacy")));
		video.setTags(new HashSet<String>(Arrays.asList(userMetadata.get("tags").split(","))));
		video.setCreatedDate(new SimpleDateFormat("MM/dd/yyyy").parse(userMetadata.get("createddate")));
		video.setS3Key(videoKey);
		video.setUploadedDate(new Date());
		
		videoService.save(video);

		return "redirect:/";
	}

	@InitBinder
	public void initDateBinder(final WebDataBinder dataBinder) {
		// Bind dates
		final String dateformat = "MM/dd/yyyy";
		final SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		sdf.setLenient(false);
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(sdf,
				false));
	}

	@InitBinder
	public void initTagsBinder(final WebDataBinder dataBinder) {
		// Bind tags
		dataBinder.registerCustomEditor(TagSet.class,
				new CommaDelimitedTagEditor());
	}
}
