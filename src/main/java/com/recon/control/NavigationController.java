package com.recon.control;



import com.recon.model.CompareSetupBean;
import com.recon.model.LoginBean;
import com.recon.model.NavigationBean;
import com.recon.model.ProcessDtlBean;
import com.recon.service.LoginService;
import com.recon.service.NavigationService;
import com.recon.util.CSRFToken;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class NavigationController {
  @Autowired
  NavigationService navigationService;
  
  @Autowired
  LoginService loginService;
  
  Logger logger = Logger.getLogger(com.recon.control.NavigationController.class);
  
  private static final String ERROR_MSG = "error_msg";
  
  private static final String SUCCESS_MSG = "success_msg";
  
  @RequestMapping({"/Menu"})
  public String getMenu(LoginBean loginBean, NavigationBean navigationBean, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes, HttpSession httpSession) throws Exception {
    try {
      System.out.println("MENU.DO" + loginBean.getUser_type());
      loginBean.setUser_id(((LoginBean)request.getSession().getAttribute("loginBean")).getUser_id().trim());
      List<ProcessDtlBean> uploadDtlBeans = this.loginService.getProcessdtls("UPLOAD_FLAG");
      List<ProcessDtlBean> compareDtlbean = this.loginService.getProcessdtls("COMAPRE_FLAG");
      String csrf = CSRFToken.getTokenForSession(request.getSession());
      redirectAttributes.addFlashAttribute("CSRFToken", csrf);
      model.addAttribute("CSRFToken", csrf);
      model.addAttribute("UploadBean", uploadDtlBeans);
      model.addAttribute("CompareBean", compareDtlbean);
      this.logger.info("getMenu completed  ");
      return "Menu";
    } catch (Exception e) {
      this.logger.error(e.getMessage());
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"ADminMenu"}, method = {RequestMethod.GET})
  public ModelAndView ADMenu(ModelAndView modelAndView, Model model, @RequestParam("userid") String userid, HttpServletRequest request) throws Exception {
    this.logger.info("***** Start ****");
    List<CompareSetupBean> setupBeans = new ArrayList<>();
    String display = "";
    this.logger.info("RECON PROCESS GET");
    List<String> subcat = new ArrayList<>();
    this.logger.info("in GetHeaderList" + userid);
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    String getUsertype = this.loginService.getUSerDetails(userid);
    List<ProcessDtlBean> uploadDtlBeans = this.loginService.getProcessdtls("UPLOAD_FLAG");
    List<ProcessDtlBean> compareDtlbean = this.loginService.getProcessdtls("COMAPRE_FLAG");
    if (getUsertype.equalsIgnoreCase("ADMIN")) {
      modelAndView.addObject("CSRFToken", csrf);
      model.addAttribute("UploadBean", uploadDtlBeans);
      model.addAttribute("CompareBean", compareDtlbean);
      modelAndView.addObject("getUsertype", getUsertype);
      modelAndView.setViewName("ADMenu");
    } else {
      model.addAttribute("UploadBean", uploadDtlBeans);
      model.addAttribute("CompareBean", compareDtlbean);
      model.addAttribute("getUsertype", getUsertype);
      modelAndView.addObject("getUsertype", getUsertype);
      modelAndView.addObject("CSRFToken", csrf);
      modelAndView.setViewName("Menu");
    } 
    this.logger.info("***** End ****");
    return modelAndView;
  }
  
  @RequestMapping({"/ADMenu"})
  public String getADMenu(LoginBean loginBean, NavigationBean navigationBean, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes, HttpSession httpSession) throws Exception {
    try {
      System.out.println("ADMENU.DO" + loginBean.getUser_type());
      loginBean.setUser_id(((LoginBean)request.getSession().getAttribute("loginBean")).getUser_id().trim());
      List<NavigationBean> menu = this.navigationService.viewMenu(loginBean);
      List<ProcessDtlBean> uploadDtlBeans = this.loginService.getProcessdtls("UPLOAD_FLAG");
      List<ProcessDtlBean> compareDtlbean = this.loginService.getProcessdtls("COMAPRE_FLAG");
      if (menu.size() == 0)
        model.addAttribute("error_msg", "No Roles have been assigned yet, please contact DBA/System Admin."); 
      navigationBean.setMenu(menu);
      request.getSession().setAttribute("navigationBean", navigationBean);
      String csrf = CSRFToken.getTokenForSession(request.getSession());
      redirectAttributes.addFlashAttribute("CSRFToken", csrf);
      model.addAttribute("CSRFToken", csrf);
      model.addAttribute("UploadBean", uploadDtlBeans);
      model.addAttribute("CompareBean", compareDtlbean);
      return "ADMenu";
    } catch (Exception e) {
      this.logger.error(e.getMessage());
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"AdminMenu"}, method = {RequestMethod.GET})
  public ModelAndView administrator(ModelAndView modelAndView) {
    modelAndView.setViewName("AdminMenu");
    return modelAndView;
  }
  
  @RequestMapping(value = {"Master_chargeback"}, method = {RequestMethod.GET})
  public ModelAndView Master_chargeback(ModelAndView modelAndView) {
    modelAndView.setViewName("Master_chargebk");
    return modelAndView;
  }
  
  @RequestMapping(value = {"ConfigurationMenu"}, method = {RequestMethod.GET})
  public ModelAndView Configuration(ModelAndView modelAndView) {
    modelAndView.setViewName("ConfigurationMenu");
    return modelAndView;
  }
  
  @RequestMapping(value = {"Gl_balancing"}, method = {RequestMethod.GET})
  public ModelAndView Gl_balancing(ModelAndView modelAndView) {
    modelAndView.setViewName("Gl_balancing");
    return modelAndView;
  }
}
