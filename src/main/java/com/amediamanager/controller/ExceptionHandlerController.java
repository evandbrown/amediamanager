package com.amediamanager.controller;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;

@ControllerAdvice
public class ExceptionHandlerController {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerController.class);

    private ModelAndView handle(Exception e, String msg) {
        LOG.error(msg, e);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("base");
        modelAndView.addObject("error", msg);
        modelAndView.addObject("exMessage", e.getMessage());
        modelAndView.addObject("stackTrace", Arrays.toString(e.getStackTrace()));
        return modelAndView;
    }

    @ExceptionHandler({ DataSourceTableDoesNotExistException.class })
    public ModelAndView handleDataSourceTableDoesNotExistException(DataSourceTableDoesNotExistException e) {
        return handle(e, "A required data source table does not exist. Check your application's configuration.");
    }

    @ExceptionHandler({ Exception.class })
    public ModelAndView handleException(Exception e) {
        return handle(e, "An unhandled exception was thrown. The full stack trace is below.");
    }

    @ExceptionHandler({ RuntimeException.class })
    public ModelAndView handleException(RuntimeException e) {
        return handle(e, "An unchecked runtime exception was thrown. The full stack trace is below.");
    }
}