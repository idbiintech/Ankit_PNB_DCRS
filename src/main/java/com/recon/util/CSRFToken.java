package com.recon.util;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CSRFToken {

	
	private CSRFToken(){}
	
	public static final String CSRF_PARAM_NAME = "CSRFToken";
	
	private final static String CSRF_TOKEN_FOR_SESSION_ATTR_NAME= CSRFToken.class.getName()+".tokenval" ;
	
	public	static String getTokenForSession (HttpSession session) {
		 String token = null;

	     token = (String) session.getAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
	     if (null==token) {
	       token=UUID.randomUUID().toString();
	       //System.out.println(token);
	       session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, token);
	   }
	 
		 return token;
		}
	
	public	static String getTokenFromRequest (HttpServletRequest request) {
		 
		 return request.getParameter(CSRF_PARAM_NAME);
		}
	
	
	
}
