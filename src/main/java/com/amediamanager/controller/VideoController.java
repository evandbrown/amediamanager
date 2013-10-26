package com.amediamanager.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.model.CreateJobOutput;
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest;
import com.amazonaws.services.elastictranscoder.model.CreateJobResult;
import com.amazonaws.services.elastictranscoder.model.JobInput;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amediamanager.config.ConfigurationSettings;
import com.amediamanager.config.ConfigurationSettings.ConfigProps;
import com.amediamanager.dao.TagDao.TagCount;
import com.amediamanager.domain.ContentType;
import com.amediamanager.domain.Privacy;
import com.amediamanager.domain.Tag;
import com.amediamanager.domain.User;
import com.amediamanager.domain.Video;
import com.amediamanager.service.TagsService;
import com.amediamanager.service.VideoService;
import com.amediamanager.util.CommaDelimitedTagEditor;
import com.amediamanager.util.PrivacyEditor;
import com.amediamanager.util.VideoUploadFormSigner;

@Controller
public class VideoController {
	private static final Logger LOG = LoggerFactory
			.getLogger(VideoController.class);

	@Autowired
	VideoService videoService;
	
	@Autowired
	TagsService tagService;

	@Autowired
	AmazonElasticTranscoder transcoderClient;

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
		return "redirect:/";
	}

	@RequestMapping(value = "/tags/{tagId}", method = RequestMethod.GET)
	public String tags(ModelMap model, @PathVariable String tagId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<Video> videos = new ArrayList<Video>();
		List<TagCount> tags = new ArrayList<TagCount>();
		try {
			// Get user's videos and tags
			videos = tagService.getVideosForUserByTag(auth.getName(), tagId);
			tags = tagService.getTagsForUser(auth.getName());

			// Add expiring URLs (1 hour)
			videos = videoService.generateExpiringUrls(videos, 1000*60*60);
		} catch (Exception e) {
			return "redirect:/config";
		}
		model.addAttribute("selectedTag", tagId);
		model.addAttribute("tags", tags);
		model.addAttribute("videos", videos);
		model.addAttribute("templateName", "only_videos");
		return "base";
	}
	
	@RequestMapping(value = "/video/{videoId}", method = RequestMethod.GET)
	public String videoGet(ModelMap model, @PathVariable String videoId,
			@RequestParam(value = "delete", required = false) String delete) {
		Video video = videoService.findById(videoId);

		if (null != delete) {
			videoService.delete(video);
			return videos(model);
		} else {
			video = videoService.generateExpiringUrl(video, 5000);
			model.addAttribute("video", video);
			model.addAttribute("templateName", "video_edit");

			return "base";
		}
	}

	@RequestMapping(value = "/video/{videoId}", method = RequestMethod.POST)
	public String videoEdit(@ModelAttribute Video video,
			@PathVariable String videoId, BindingResult result,
			RedirectAttributes attr, HttpSession session) {
		videoService.update(video);
		return "redirect:/";
	}

	@RequestMapping(value = "/video/upload", method = RequestMethod.GET)
	public String videoUpload(ModelMap model, HttpServletRequest request,
			@ModelAttribute User user) {
		// Video redirect URL
		String redirectUrl = request.getScheme() + "://"
				+ request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath() + "/video/ingest";

		// Prepare S3 form upload
		VideoUploadFormSigner formSigner = new VideoUploadFormSigner(
				config.getProperty(ConfigProps.S3_UPLOAD_BUCKET),
				config.getProperty(ConfigProps.S3_UPLOAD_PREFIX), user,
				config.getAWSCredentialsProvider(), redirectUrl);

		model.addAttribute("formSigner", formSigner);
		model.addAttribute("templateName", "video_upload");

		return "base";
	}

	public void createVideoPreview(Video video) {
		String pipelineId = config.getProperty(ConfigProps.TRANSCODE_PIPELINE);
		String presetId = config.getProperty(ConfigProps.TRANSCODE_PRESET);
		if (pipelineId == null || presetId == null) {
			return;
		}
		CreateJobRequest encodeJob = new CreateJobRequest()
				.withPipelineId(pipelineId)
				.withInput(
						new JobInput().withKey(video.getOriginalKey())
								.withAspectRatio("auto").withContainer("auto")
								.withFrameRate("auto").withInterlaced("auto")
								.withResolution("auto"))
				.withOutputKeyPrefix(
						"uploads/converted/" + video.getOwner() + "/")
				.withOutput(
						new CreateJobOutput()
								.withKey(UUID.randomUUID().toString())
								.withPresetId(presetId)
								.withThumbnailPattern(
										"thumbs/"
												+ UUID.randomUUID().toString()
												+ "-{count}"));

		try {
			CreateJobResult result = transcoderClient.createJob(encodeJob);
			video.setTranscodeJobId(result.getJob().getId());
			video.setThumbnailKey("static/img/in_progress_poster.png");
			videoService.save(video);
		} catch (AmazonServiceException e) {
			LOG.error("Failed creating transcode job for video {}",
					video.getId(), e);
		}
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
		video.setId(userMetadata.get("uuid"));
		video.setTitle(userMetadata.get("title"));
		video.setPrivacy(Privacy.fromName(userMetadata.get("privacy")));
		video.setCreatedDate(new SimpleDateFormat("MM/dd/yyyy")
				.parse(userMetadata.get("createddate")));
		video.setOriginalKey(videoKey);
		video.setBucket(userMetadata.get("bucket"));
		video.setUploadedDate(new Date());

		Set<Tag> tags = new HashSet<Tag>();
		for (String tag : userMetadata.get("tags").split(",")) {
			tags.add(new Tag(tag));
		}
		video.setTags(tags);

		videoService.save(video);

		// Kick off preview encoding
		createVideoPreview(video);

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
		dataBinder.registerCustomEditor(Set.class,
				new CommaDelimitedTagEditor());
	}

	@InitBinder
	public void initPrivacyBinder(final WebDataBinder dataBinder) {
		// Bind tags
		dataBinder.registerCustomEditor(Privacy.class, new PrivacyEditor());
	}
}
