package com.recon.control;




import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.service.AdjustmentFileService;
import com.recon.service.ISourceService;
import com.recon.util.CSRFToken;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdjustmentFileController {
  private static final Logger logger = Logger.getLogger(com.recon.control.AdjustmentFileController.class);
  
  @Autowired
  ISourceService iSourceService;
  
  @Autowired
  AdjustmentFileService adjService;
  
  @RequestMapping(value = {"AdjustmentFileUpload"}, method = {RequestMethod.GET})
  public ModelAndView AdjustmentFileUploadGet(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** AdjustmentFileUpload.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("nfsFileUpload GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("AdjustmentFileUpload");
    logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"JCBAdjustmentFileUpload"}, method = {RequestMethod.GET})
  public ModelAndView JCBAdjustmentFileUpload(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** JCBAdjustmentFileUpload.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("JCBAdjustmentFileUpload GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("JCBAdjustmentFileUpload");
    logger.info("***** JCBAdjustmentFileUpload.JCBAdjustmentFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"DFSAdjustmentFileUpload"}, method = {RequestMethod.GET})
  public ModelAndView DFSAdjustmentFileUpload(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** DFSAdjustmentFileUpload.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("DFSAdjustmentFileUpload GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("DFSAdjustmentFileUpload");
    logger.info("***** DFSAdjustmentFileUpload.DFSAdjustmentFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"ICDAdjustmentFileUpload"}, method = {RequestMethod.GET})
  public ModelAndView ICDAdjustmentFileUpload(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** AdjustmentFileUpload.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("nfsFileUpload GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("ICDAdjustmentFileUpload");
    logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"AdjustmentFileUpload"}, method = {RequestMethod.POST})
  @ResponseBody
  public String AdjustmentFileUploadPost(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean, HttpServletRequest request, @RequestParam("file") MultipartFile file, String filename, String category, String stSubCategory, String datepicker, HttpSession httpSession, Model model, ModelAndView modelAndView, RedirectAttributes redirectAttributes) throws Exception {
    logger.info("***** AdjustmentFileUpload.Post Start ****");
    HashMap<String, Object> output = null;
    try {
      logger.info("RECON PROCESS GET");
      String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
      logger.info("Created by is " + Createdby);
      nfsSettlementBean.setCreatedBy(Createdby);
      if (file.getOriginalFilename().contains("_") && nfsSettlementBean.getCategory().contains("NFS")) {
        logger.info("Time period is " + nfsSettlementBean.getTimePeriod());
        String fileName = file.getOriginalFilename();
        nfsSettlementBean.setFileName(file.getOriginalFilename());
        logger.info("FileName is " + fileName);
        String[] fileNames = fileName.split("_");
        if (fileNames.length > 1) {
          String cycle = fileNames[1].substring(0, 1);
          logger.info("Cycle is: " + cycle);
          nfsSettlementBean.setCycle(Integer.parseInt(cycle));
        } 
      } else {
        nfsSettlementBean.setCycle(1);
      } 

      logger.info("VALUES ARE " + nfsSettlementBean + " " + category + " " + stSubCategory + datepicker + filename);
      if (category.contains("ICD")) {
        nfsSettlementBean.setCategory("ICD-ADJUSTMENT");
        nfsSettlementBean.setStSubCategory("-");
        nfsSettlementBean.setCreatedBy("ICD");
        nfsSettlementBean.setFileName(file.getOriginalFilename());
      } else if (filename.contains("NTSL-DFS")) {
        nfsSettlementBean.setCategory("DFS_ADJUSTMENT");
        nfsSettlementBean.setStSubCategory("-");
        nfsSettlementBean.setCreatedBy("NTSL-DFS");
        nfsSettlementBean.setFileName(file.getOriginalFilename());
        nfsSettlementBean.setAdjType(filename);
      } else if (filename.contains("NTSL-JCB")) {
        nfsSettlementBean.setCategory("JCB_ADJUSTMENT");
        nfsSettlementBean.setStSubCategory("-");
        nfsSettlementBean.setCreatedBy("NTSL-JCB");
        nfsSettlementBean.setFileName(file.getOriginalFilename());
        nfsSettlementBean.setAdjType(filename);
      } else {
        nfsSettlementBean.setCategory("NFS_ADJUSTMENT");
        nfsSettlementBean.setStSubCategory("-");
        nfsSettlementBean.setCreatedBy("NTSL-NFS");
        nfsSettlementBean.setFileName(file.getOriginalFilename());
      } 
      HashMap<String, Object> result = this.adjService.validateAdjustmentFileUpload(nfsSettlementBean);
      if (result != null && ((Boolean)result.get("result")).booleanValue()) {
        output = this.adjService.uploadAdjustmentFile(nfsSettlementBean, file);
        logger.info("***** AdjustmentFileController.AdjustmentFileUpload Post End ****");
        if (((Boolean)output.get("result")).booleanValue())
          return "File Uploaded Successfully"; 
        if (((Integer)output.get("count")).intValue() == -1)
          return "AdjSettlementDate Column has date different than selected Date"; 
        return "Error while Uploading file";
      } 
      return result.get("msg").toString();
    } catch (Exception e) {
      logger.info("Exception in NFSSettlementController " + e);
      if (output != null && !((Boolean)output.get("result")).booleanValue())
        return "Error Occured at Line " + (Integer)output.get("count"); 
      return "Error Occurred in reading";
    } 
  }
}
