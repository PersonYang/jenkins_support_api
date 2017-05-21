package com.apin.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component 
public class SimpleCORSFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(SimpleCORSFilter.class);
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		logger.debug("===方法==="+httpRequest.getMethod()+"=====url{}"+httpRequest.getRequestURI());
		/*httpResponse.setHeader("Content-Type", "application/json;charset=utf-8");*/
		httpResponse.setHeader("Access-Control-Allow-Origin", "*");
		httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");  
		httpResponse.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,applicationId,sign,channelId,userId");  
		httpResponse.setHeader("Access-Control-Max-Age","3600");
	    chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}
	
}
