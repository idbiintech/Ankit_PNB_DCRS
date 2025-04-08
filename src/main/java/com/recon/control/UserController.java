package com.recon.control;


import com.recon.model.LoginBean;
import com.recon.model.UserBean;
import com.recon.service.UserService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
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
public class UserController {
  @Autowired
  UserService userService;
  
  private static final Logger logger = Logger.getLogger(com.recon.control.UserController.class);
  
  @RequestMapping(value = {"UserManager"}, method = {RequestMethod.GET})
  public String userManager(Model model, UserBean userBean, HttpSession httpSession) {
    try {
      userBean.setUser_id(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
      List<UserBean> userList = this.userService.viewUser(userBean);
      model.addAttribute("userList", userList);
      return "UserManager";
    } catch (Exception e) {
      logger.error(e.getMessage());
      model.addAttribute("error_msg", e.getMessage());
      model.addAttribute("userList", null);
      return "UserManager";
    } 
  }
  
  @RequestMapping(value = {"AddUser"}, method = {RequestMethod.GET})
  public String addUser(UserBean userBean, Model model) {
    model.addAttribute("user", userBean);
    return "ModalAddUser";
  }
  
  @RequestMapping(value = {"AddUser"}, method = {RequestMethod.POST})
  public String addUserDetail(@ModelAttribute("user") UserBean userBean, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
    try {
      userBean.setEntry_by(((LoginBean)session.getAttribute("loginBean")).getUser_id());
      userBean.setUser_id(userBean.getUser_id().toUpperCase());
      userBean.setPassword(BCrypt.hashpw(userBean.getPassword(), BCrypt.gensalt()));
      this.userService.addUser(userBean);
      System.out.println("addUserDetail");
      redirectAttributes.addFlashAttribute("success_msg", "New User Added Successfully.");
      return "redirect:UserManager.do";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return "redirect:UserManager.do";
    } 
  }
  
  @RequestMapping(value = {"EditUser"}, method = {RequestMethod.GET})
  public String editUser(@RequestParam("user_id") String user_id, UserBean userBean, Model model, HttpServletRequest request) {
    try {
      userBean.setUser_id(user_id);
      userBean = this.userService.viewUserDetail(userBean);
      model.addAttribute("user", userBean);
      return "ModalEditUser";
    } catch (Exception e) {
      logger.error(e.getMessage());
      model.addAttribute("user", new UserBean());
      return "ModalEditUser";
    } 
  }
  
  @RequestMapping(value = {"EditUser"}, method = {RequestMethod.POST})
  public String editUserDetail(@ModelAttribute("user") UserBean userBean, BindingResult bindingResult, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
    try {
      userBean.setUpdt_by(((LoginBean)session.getAttribute("loginBean")).getUser_id());
      this.userService.modifyUser(userBean);
      redirectAttributes.addFlashAttribute("success_msg", "Record Modified Successfully.");
      return "redirect:UserManager.do";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return "redirect:UserManager.do";
    } 
  }
  
  @RequestMapping(value = {"DeleteUser"}, method = {RequestMethod.GET})
  public String deleteUser(@RequestParam("user_id") String user_id, UserBean userBean, Model model) {
    try {
      userBean.setUser_id(user_id);
      model.addAttribute("user", userBean);
      return "ModalDeleteUser";
    } catch (Exception e) {
      logger.error(e.getMessage());
      model.addAttribute("user", new UserBean());
      return "ModalDeleteUser";
    } 
  }
  
  @RequestMapping(value = {"DeleteUser"}, method = {RequestMethod.POST})
  public String deleteUserDetail(@ModelAttribute("user") UserBean userBean, HttpSession session, RedirectAttributes redirectAttributes) {
    try {
      userBean.setUpdt_by(((LoginBean)session.getAttribute("loginBean")).getUser_id());
      this.userService.deleteUser(userBean);
      redirectAttributes.addFlashAttribute("error_msg", "User Deleted Successfully.");
      return "redirect:UserManager.do";
    } catch (Exception e) {
      logger.error(e.getMessage());
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return "redirect:UserManager.do";
    } 
  }
  
  @RequestMapping(value = {"ModalLiveUser"}, method = {RequestMethod.GET})
  public String modalLiveUser(UserBean userBean, Model model, HttpSession session) {
    try {
      userBean.setUser_id(((LoginBean)session.getAttribute("loginBean")).getUser_id());
      List<UserBean> liveUserList = this.userService.liveUser(userBean);
      if (liveUserList.size() == 0)
        throw new Exception("No Data Found."); 
      model.addAttribute("liveUserList", liveUserList);
      return "ModalLiveUser";
    } catch (Exception e) {
      logger.error(e.getMessage());
      model.addAttribute("liveUserList", null);
      return "ModalLiveUser";
    } 
  }
  
  @RequestMapping(value = {"GetCurrentUser"}, method = {RequestMethod.POST})
  @ResponseBody
  public String getCurrentUser(UserBean userBean, HttpSession session) {
    try {
      userBean.setUser_id(((LoginBean)session.getAttribute("loginBean")).getUser_id());
      return this.userService.liveUserJSON(userBean);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return "";
    } 
  }
  
  @RequestMapping(value = {"EndUserSession"}, method = {RequestMethod.POST})
  @ResponseBody
  public String endUserSession(@RequestParam("user_id") String user_id, UserBean userBean, HttpSession session) {
    JSONObject jsonObject = new JSONObject();
    try {
      userBean.setUser_id(user_id);
      userBean.setUpdt_by(((LoginBean)session.getAttribute("loginBean")).getUser_id());
      this.userService.endUserSession(userBean);
      jsonObject.put("Result", "OK");
      jsonObject.put("Value", "Session Terminated Successfully.");
    } catch (Exception e) {
      logger.error(e.getMessage());
      jsonObject.put("Result", "ERROR");
      jsonObject.put("Value", e.getMessage());
    } 
    return jsonObject.toString();
  }
  
  @RequestMapping(value = {"CloseUserSession"}, method = {RequestMethod.POST})
  @ResponseBody
  public String closeUserSession(@RequestParam("user_id") String user_id, HttpServletRequest request, UserBean userBean, HttpSession session) {
    JSONObject jsonObject = new JSONObject();
    try {
      userBean.setUser_id(user_id);
      this.userService.endUserSession(userBean);
      jsonObject.put("Result", "OK");
      jsonObject.put("Value", "Session Terminated Successfully.");
    } catch (Exception e) {
      logger.error(e.getMessage());
      jsonObject.put("Result", "ERROR");
      jsonObject.put("Value", e.getMessage());
    } 
    return jsonObject.toString();
  }
  
  @RequestMapping(value = {"UserLog"}, method = {RequestMethod.GET})
  public String userLog(@RequestParam("user_id") String user_id, UserBean userBean, Model model) {
    try {
      userBean.setUser_id(user_id);
      List<UserBean> user_log_list = this.userService.userLog(userBean);
      model.addAttribute("user_log_list", user_log_list);
      return "ModalUserLog";
    } catch (Exception e) {
      model.addAttribute("user_log_list", "");
      return "ModalUserLog";
    } 
  }
}
