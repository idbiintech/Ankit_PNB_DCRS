package com.recon.control;



import com.recon.model.LoginBean;
import com.recon.model.RoleBean;
import com.recon.model.UserBean;
import com.recon.service.RoleService;
import com.recon.service.UserService;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RoleController {
  @Autowired
  RoleService roleService;
  
  @Autowired
  UserService userService;
  
  Logger logger = Logger.getLogger(com.recon.control.RoleController.class);
  
  @RequestMapping(value = {"RoleManager"}, method = {RequestMethod.GET})
  public String RoleManager(Model model, UserBean userBean, HttpSession httpSession) {
    try {
      userBean.setUser_id(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
      List<UserBean> userList = this.userService.viewRole(userBean);
      model.addAttribute("userList", userList);
      return "RoleManager";
    } catch (Exception e) {
      this.logger.error(e.getMessage());
      model.addAttribute("error_msg", e.getMessage());
      model.addAttribute("userList", null);
      return "RoleManager";
    } 
  }
  
  @RequestMapping(value = {"UserRole"}, method = {RequestMethod.POST})
  @ResponseBody
  public String userRole(@RequestParam("user_id") String user_id, RoleBean roleBean) {
    try {
      roleBean.setUser_id(user_id);
      return this.roleService.viewUserRole(roleBean);
    } catch (Exception e) {
      this.logger.error(e.getMessage());
      return null;
    } 
  }
  
  @RequestMapping(value = {"EditRole"}, method = {RequestMethod.GET})
  public String EditRole(@RequestParam("user_id") String user_id, UserBean userBean, Model model, HttpServletRequest request) {
    try {
      userBean.setUser_id(user_id);
      userBean = this.userService.viewRoleDetail(userBean);
      model.addAttribute("user", userBean);
      return "ModalEditRole";
    } catch (Exception e) {
      this.logger.error(e.getMessage());
      model.addAttribute("user", new UserBean());
      return "ModalEditRole";
    } 
  }
  
  @RequestMapping(value = {"EditRole"}, method = {RequestMethod.POST})
  public String editRoleDetail(@ModelAttribute("user") UserBean userBean, BindingResult bindingResult, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
    try {
      userBean.setUpdt_by(((LoginBean)session.getAttribute("loginBean")).getUser_id());
      System.out.println("d " + userBean.getPassword());
      this.userService.modifyRole(userBean);
      redirectAttributes.addFlashAttribute("success_msg", "Record Modified Successfully.");
      return "redirect:RoleManager.do";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return "redirect:RoleManager.do";
    } 
  }
  
  @RequestMapping(value = {"AssignRole"}, method = {RequestMethod.POST})
  @ResponseBody
  public String assignRole(@RequestParam("roles") List<Integer> roles, @RequestParam("user_id") String user_id, RoleBean roleBean, HttpSession session) {
    try {
      roleBean.setEntry_by(((LoginBean)session.getAttribute("loginBean")).getUser_id());
      roleBean.setUser_id(user_id);
      this.roleService.assignRole(roles, roleBean);
      return "Roles Assigned Successfully.";
    } catch (Exception e) {
      this.logger.error(e.getMessage());
      return e.getMessage();
    } 
  }
  
  @RequestMapping(value = {"RevokeRole"}, method = {RequestMethod.POST})
  @ResponseBody
  public String revokeRole(@RequestParam("roles") List<Integer> roles, @RequestParam("user_id") String user_id, RoleBean roleBean, HttpSession session) {
    try {
      roleBean.setUpdt_by(((LoginBean)session.getAttribute("loginBean")).getUser_id());
      roleBean.setUser_id(user_id);
      this.roleService.revokeRole(roles, roleBean);
      return "Roles Revoked Successfully.";
    } catch (Exception e) {
      this.logger.error(e.getMessage());
      return e.getMessage();
    } 
  }
  
  @RequestMapping({"CheckRole"})
  @ResponseBody
  public boolean checkRole(@RequestParam("page_url") String page_url, RoleBean roleBean, HttpSession session) {
    try {
      roleBean.setPage_url(page_url);
      roleBean.setUpdt_by(((LoginBean)session.getAttribute("loginBean")).getUser_id());
      return this.roleService.checkRole(roleBean);
    } catch (Exception e) {
      this.logger.error(e.getMessage());
      return false;
    } 
  }
}
