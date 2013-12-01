package com.jaxrs.restapi;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Application;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MyApplication extends Application implements ContainerResponseFilter {
    @Override
    public Set<Class<?>> getClasses() {
    	final Set<Class<?>> classes = new HashSet<Class<?>>();
        // register resources and features
        classes.add(MultiPartFeature.class);
        classes.add(VideosResource.class);
        classes.add(UsersResource.class);
        classes.add(LoggingFilter.class);
        
        return classes;
    }

	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		responseContext.getHeaders().add("Access-Control-Allow-Origin", '*');
	}
}
