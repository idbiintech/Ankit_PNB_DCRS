package com.recon.control;


import com.recon.model.LoginBean;
import com.recon.service.LoginService;
import java.net.InetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping({"/Login", "/"})
public class LoginController {
  @Autowired
  LoginService loginService;
  
  Logger logger = Logger.getLogger(com.recon.control.LoginController.class);
  
  private static final String ERROR_MSG = "error_msg";
  
  StringBuilder error_string = new StringBuilder();
  
  @RequestMapping(method = {RequestMethod.GET})
  public ModelAndView init(ModelAndView modelAndView, LoginBean loginBean, HttpServletRequest request) {
    modelAndView.setViewName("loginb");
    modelAndView.addObject("login", loginBean);
    return modelAndView;
  }
  
  @RequestMapping(method = {RequestMethod.POST})
  public String validateUser(@ModelAttribute("login") LoginBean loginBean, BindingResult bindingResult, Model model, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirect, ModelAndView modelAndView) throws Exception {
    try {
      if (request.getParameter("captcha") != null && request.getParameter("captchar") != null) {
        System.out.println("captcha " + request.getParameter("captcha") + " f " + request.getParameter("captchar"));
        String captcha = request.getParameter("captcha");
        String captchar = request.getParameter("captchar");
        if (captcha.contains(captchar)) {
          System.out.println("treue");
        } else {
          System.out.println("false");
        } 
      } 
      this.logger.info("***** LoginController.validateuser Start ****");
      if (bindingResult.hasErrors())
        return "loginb"; 
      InetAddress IP = InetAddress.getLocalHost();
      if (request.getSession().getId() != null)
        loginBean.setSession_id(request.getSession().getId()); 
      if (IP.getHostAddress() != null)
        loginBean.setIp_address(IP.getHostAddress()); 
      String ipAddress = request.getRemoteAddr();
      loginBean.setIp_address(ipAddress);
      loginBean = this.loginService.getUserDetail(loginBean);
      loginBean.setSession_id(request.getSession().getId());
      request.getSession().setAttribute("userType", loginBean.getUser_type());
      request.getSession().setAttribute("loginBean", loginBean);
      this.logger.info("***** LoginController.validateuser End ****" + loginBean.getUser_type());
      if (loginBean.getUser_type().equalsIgnoreCase("ADMIN"))
        return "redirect:Menu.do"; 
      return "redirect:Menu.do";
    } catch (Exception e) {
      httpSession.invalidate();
      model.addAttribute("error_msg", e.getMessage().toString());
      return "loginb";
    } 
  }
  
  @RequestMapping(value = {"invalidBrowser"}, method = {RequestMethod.GET})
  public ModelAndView invalidBrowser(ModelAndView modelAndView, LoginBean loginBean, HttpServletRequest request) {
    System.out.println("HIE");
    modelAndView.setViewName("invalidBrowser");
    return modelAndView;
  }
  
  @RequestMapping(value = {"errors"}, method = {RequestMethod.GET})
  public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
    ModelAndView errorPage = new ModelAndView("errorPage");
    String errorMsg = "";
    int httpErrorCode = getErrorCode(httpRequest);
    switch (httpErrorCode) {
      case 400:
        errorMsg = "Http Error Code: 400. Bad Request";
        break;
    } 
    errorPage.addObject("errorMsg", errorMsg);
    return errorPage;
  }
  
  private int getErrorCode(HttpServletRequest httpRequest) {
    return ((Integer)httpRequest
      .getAttribute("javax.servlet.error.status_code")).intValue();
  }
}
