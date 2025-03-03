package com.recon.control;


import com.recon.model.LoginBean;
import com.recon.model.SessionModel;
import com.recon.service.LoginService;
import com.recon.util.demo;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LogoutController {
  @Autowired
  LoginService loginService;
  
  private static final Logger logger = Logger.getLogger(com.recon.control.LogoutController.class);
  
  private static final String ERROR_MSG = "error_msg";
  
  private static final String SUCCESS_MSG = "success_msg";
  
  @RequestMapping({"Logout"})
  public String logout(LoginBean loginBean, HttpSession httpSession, HttpServletRequest req, RedirectAttributes redirectAttributes, SessionModel sessionmodel, @RequestParam("userid") String userid) throws Exception {
    try {
      loginBean = (LoginBean)httpSession.getAttribute("loginBean");
      logger.info("***** LogoutController.logout Start **** " + loginBean.getUser_id() + " e " + userid);
      loginBean.setUser_id(userid);
      this.loginService.invalidateUser(loginBean);
      try {
        sessionmodel.getReq().getSession(false).invalidate();
        sessionmodel.setReq(null);
        System.out.println("logout Session" + SessionModel.req.getSession());
        req.getSession(false).invalidate();
        httpSession.invalidate();
      } catch (Exception exception) {}
      httpSession.invalidate();
      redirectAttributes.addFlashAttribute("success_msg", "Logged Out Successfully.");
      logger.info("***** LogoutController.logout End ****");
      return "redirect:Login.do";
    } catch (Exception e) {
      demo.logSQLException(e, "LogoutController.logout");
      logger.error(" error in LogoutController.logout", new Exception("LogoutController.logout", e));
      redirectAttributes.addFlashAttribute("error_msg", "Error Logging Out.");
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping({"InvalidateSession"})
  public String invalidateSession(@RequestParam("user_id") String user_id, LoginBean loginBean, HttpSession httpSession, RedirectAttributes redirectAttributes) throws Exception {
    try {
      loginBean.setUser_id(user_id);
      this.loginService.invalidateUser(loginBean);
      httpSession.invalidate();
      redirectAttributes.addFlashAttribute("success_msg", "Login to continue.");
      return "redirect:Login.do";
    } catch (Exception e) {
      demo.logSQLException(e, "LogoutController.invalidateSession");
      logger.error(e.getMessage());
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"closeSession"}, method = {RequestMethod.GET})
  public String closeSession(LoginBean loginBean, Model model, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes) throws Exception {
    try {
      String ipAddress = request.getRemoteAddr();
      loginBean.setIp_address(ipAddress);
      Map<String, Object> allSession_map = this.loginService.getAllSession(loginBean);
      List<LoginBean> users_list = (List<LoginBean>)allSession_map.get("USER_LIST");
      if (users_list.size() == 0)
        throw new Exception("No Users Logged in"); 
      model.addAttribute("users", users_list);
      return "BounceMyLogin";
    } catch (Exception e) {
      demo.logSQLException(e, "LogoutController.closeSession");
      logger.error(e.getMessage());
      request.setAttribute("error", e.getMessage());
      return "BounceMyLogin";
    } 
  }
}
