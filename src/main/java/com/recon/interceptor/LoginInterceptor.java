package com.recon.interceptor;


import com.recon.model.LoginBean;
import com.recon.service.RoleService;
import com.recon.service.UserService;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoginInterceptor implements HandlerInterceptor {
  private static final Logger logger = Logger.getLogger(com.recon.interceptor.LoginInterceptor.class);
  
  @Autowired
  RoleService roleService;
  
  @Autowired
  UserService userService;
  
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception exception) throws Exception {
    if (request.getSession().getAttribute("error_msg") != null)
      request.getSession().removeAttribute("error_msg"); 
  }
  
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView modelAndView) throws Exception {}
  
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
    String uriAction = "http://172.27.205.93:8080/IRECON";
    String uri = request.getRequestURI();
    System.out.println("uri is " + uri);
    if (!uri.equals("/IRECON/Login.do") && !uri.equals("/IRECON/") && !uri.equals("/IRECON/Logout.do") && !uri.equals("/IRECON/InvalidateSession.do") && 
      !uri.equals("/IRECON/closeSession.do") && !uri.equals("/IRECON/CloseUserSession.do") && !uri.equals("/IRECON/IRECONMODULE.do"))
    	   if (!uri.equals("/IRECON1/Login.do") && !uri.equals("/IRECON1/") && !uri.equals("/IRECON1/Logout.do") && !uri.equals("/IRECON1/InvalidateSession.do") && 
    			      !uri.equals("/IRECON1/closeSession.do") && !uri.equals("/IRECON1/CloseUserSession.do") && !uri.equals("/IRECON1/IRECONMODULE.do"))

      if (!uri.equals("/IRECON_ANKIT/Login.do") && !uri.equals("/IRECON_ANKIT/") && !uri.equals("/IRECON_ANKIT/Logout.do") && !uri.equals("/IRECON_ANKIT/InvalidateSession.do") && 
        !uri.equals("/IRECON_ANKIT/closeSession.do") && !uri.equals("/IRECON_ANKIT/CloseUserSession.do") && !uri.equals("/IRECON_ANKIT/IRECONMODULE.do")) {
        try {
          LoginBean loginBean = (LoginBean)request.getSession(false).getAttribute("loginBean");
          if (loginBean == null)
            throw new Exception("Invalid Session, Login to continue.."); 
        } catch (Exception e) {
          logger.error(e.getMessage());
          request.getSession().setAttribute("error_msg", e.getMessage());
          response.sendRedirect(uriAction);
          return false;
        } 
        try {
          if (Integer.parseInt((new SimpleDateFormat("mm")).format(new Date())) - Integer.parseInt((new SimpleDateFormat("mm")).format(new Date(request.getSession().getLastAccessedTime()))) > 120) {
            request.getSession().invalidate();
            throw new Exception("Session Expired.");
          } 
        } catch (Exception e) {
          logger.error(e.getMessage());
          request.getSession().removeAttribute("loginBean");
          request.getSession().setAttribute("error_msg", e.getMessage());
          response.sendRedirect(uriAction);
          return false;
        } 
      } else if (uri.equals("/IRECON/Login.do") || uri.equals("/IRECON/")) {
        try {
          LoginBean loginBean = (LoginBean)request.getSession(false).getAttribute("loginBean");
          if (loginBean != null)
            request.getSession().invalidate(); 
        } catch (Exception e) {
          LoginBean loginBean = null;
          System.out.println("exception is " + loginBean);
        } 
      }  
    return true;
  }
}
