package com.amediamanager.security;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Intercept session events
 * @author evbrown
 *
 */
public class SessionListener implements HttpSessionListener
{
    public void sessionCreated(HttpSessionEvent httpSessionEvent)
    {
    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent)
    {
    }
}
