package com.amediamanager.controller;

import java.util.Arrays;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.amediamanager.exceptions.*;

@ControllerAdvice
public class ExceptionHandlerController {

	@ExceptionHandler({ DataSourceTableDoesNotExistException.class })
	public ModelAndView handleDataSourceTableDoesNotExistException(
			DataSourceTableDoesNotExistException ex) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("base");
		modelAndView
				.addObject(
						"error",
						"A required data source table does not exist. Check your application's configuration.");
		modelAndView.addObject("exMessage", ex.getMessage());
		modelAndView.addObject("stackTrace",
				Arrays.toString(ex.getStackTrace()));
		return modelAndView;
	}

	@ExceptionHandler({ Exception.class })
	public ModelAndView handleException(Exception ex) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("base");
		modelAndView
				.addObject(
						"error",
						"A required data source table does not exist. Check your application's configuration.");
		modelAndView.addObject("exMessage", ex.getMessage());
		modelAndView.addObject("stackTrace",
				Arrays.toString(ex.getStackTrace()));
		return modelAndView;
	}
}