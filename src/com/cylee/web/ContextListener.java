package com.cylee.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by cylee on 16/10/22.
 */
@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Log.d("contextInit-->"+servletContextEvent.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Log.d("contextDestroyed-->"+servletContextEvent.getServletContext());
    }
}
