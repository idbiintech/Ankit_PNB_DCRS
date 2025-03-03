package com.recon.control;


import com.recon.model.CompareSetupBean;
import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.RupayUploadBean;
import com.recon.model.VisaUploadBean;
import com.recon.service.ICompareConfigService;
import com.recon.service.SettlementTTUMService;
import com.recon.service.VisaSettlementService;
import com.recon.util.CSRFToken;
import com.recon.util.GenerateUCOTTUM;
import com.recon.util.demo;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;
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
public class VisaSettlementController {
  private static final Logger logger = Logger.getLogger(com.recon.control.VisaSettlementController.class);
  
  public static final String OUTPUT_FOLDER = String.valueOf(System.getProperty("catalina.home")) + File.separator + "TTUM";
  
  @Autowired
  SettlementTTUMService SETTLTTUMSERVICE;
  
  @Autowired
  ICompareConfigService icompareConfigService;
  
  @Autowired
  VisaSettlementService visaSettlementService;
  
  @RequestMapping(value = {"VisaEPFileRead"}, method = {RequestMethod.GET})
  public ModelAndView VisaEPFileReadGet(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
    logger.info("***** VisaSettlementController.Get Start ****");
    logger.info("VisaEPFileRead GET");
    String display = "";
    VisaUploadBean visaUploadBean = new VisaUploadBean();
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("visaUploadBean", visaUploadBean);
    modelAndView.setViewName("ReadEPFile");
    logger.info("***** VisaSettlementController.VisaEPFileRead GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"VisaEPUpload"}, method = {RequestMethod.POST})
  @ResponseBody
  public String VisaEPUploadPost(@ModelAttribute("visaUploadBean") VisaUploadBean visaUploadBean, HttpServletRequest request, @RequestParam("file") MultipartFile file, HttpSession httpSession, RedirectAttributes redirectAttributes) throws Exception {
    logger.info("***** VisaSettlementController.Get Start ****");
    logger.info("VisaEPFileRead Post " + visaUploadBean.getFileDate());
    logger.info("VisaEPUpload Post : File Type is " + visaUploadBean.getFileType());
    logger.info("File name is  " + file.getOriginalFilename());
    String display = "";
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    visaUploadBean.setCreatedBy(Createdby);
    if (visaUploadBean.getFileName().equalsIgnoreCase("EP")) {
      HashMap<String, Object> output = this.visaSettlementService.checkFileAlreadyUpload(visaUploadBean.getFileDate(), 
          file.getOriginalFilename());
      if (output != null && ((Boolean)output.get("result")).booleanValue()) {
        boolean uploadFlag = this.visaSettlementService.uploadFile(visaUploadBean, file);
        if (uploadFlag)
          return "File Uploaded Successfully"; 
        return "File Not Uploaded";
      } 
      return output.get("msg").toString();
    } 
    if (visaUploadBean.getFileName().equalsIgnoreCase("CC")) {
      HashMap<String, Object> output = this.visaSettlementService
        .checkCcFileAlreadyUpload(visaUploadBean.getFileDate(), visaUploadBean.getFileType());
      if (output != null && ((Boolean)output.get("result")).booleanValue()) {
        boolean uploadFlag = this.visaSettlementService.uploadCCFile(visaUploadBean, file);
        if (uploadFlag)
          return "File Uploaded Successfully"; 
        return "File Not Uploaded";
      } 
      return output.get("msg").toString();
    } 
    return "";
  }
  
  @RequestMapping(value = {"VisaSettlementProces"}, method = {RequestMethod.GET})
  public ModelAndView VisaSettlementProces(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
    logger.info("***** VisaSettlementController.VisaSettlementProces. Get Start ****");
    logger.info("VisaSettlementProces GET");
    String display = "";
    VisaUploadBean visaUploadBean = new VisaUploadBean();
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("visaUploadBean", visaUploadBean);
    modelAndView.setViewName("VisaSettlementProcess");
    logger.info("***** VisaSettlementController.VisaSettlementProces GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"VisaSettlementProces"}, method = {RequestMethod.POST})
  @ResponseBody
  public String VisaSettlementProcesPost(@ModelAttribute("visaUploadBean") VisaUploadBean visaUploadBean, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes) throws Exception {
    logger.info("***** VisaSettlementController.VisaSettlementProces.Get Start ****");
    logger.info("VisaSettlementProces Post " + visaUploadBean.getFileDate());
    String display = "";
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    visaUploadBean.setCreatedBy(Createdby);
    boolean uploadStatus = this.visaSettlementService.checkFileUpload(visaUploadBean);
    if (uploadStatus) {
      HashMap<String, Object> output = this.visaSettlementService.checkSettlementProcess(visaUploadBean);
      if (output != null && ((Boolean)output.get("result")).booleanValue()) {
        boolean flag = this.visaSettlementService.runVisaSettlement(visaUploadBean);
        if (flag)
          return "Processing Completed Successfully \n Download the reports"; 
        return "Issue while processing settlement";
      } 
      return output.get("msg").toString();
    } 
    return "One of the Ep file is not uploaded for selected date";
  }
  
  @RequestMapping(value = {"DownloadVisaSettlement"}, method = {RequestMethod.POST})
  @ResponseBody
  public String DownloadVisaSettlement(@ModelAttribute("visaUploadBean") VisaUploadBean visaUploadBean, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes) throws Exception {
    logger.info("***** VisaSettlementController.VisaSettlementProces.Get Start ****");
    logger.info("VisaSettlementProces Post " + visaUploadBean.getFileDate());
    String display = "";
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    visaUploadBean.setCreatedBy(Createdby);
    HashMap<String, Object> output = this.visaSettlementService.checkSettlementProcess(visaUploadBean);
    if (output != null && !((Boolean)output.get("result")).booleanValue())
      return "success"; 
    return "Settlement is not processed for selected date.";
  }
  
  @RequestMapping(value = {"DownloadVisaSettlementReport"}, method = {RequestMethod.POST})
  public String DownloadVisaSettlementReport(@ModelAttribute("visaUploadBean") VisaUploadBean visaUploadBean, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** VisaSettlementController.VisaSettlementProces.Get Start ****");
    logger.info("VisaSettlementProces Post " + visaUploadBean.getFileDate());
    String display = "";
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    visaUploadBean.setCreatedBy(Createdby);
    List<Object> Excel_data = new ArrayList();
    Excel_data = this.visaSettlementService.getSettlementData(visaUploadBean);
    model.addAttribute("ReportName", "Visa_Settlement_Report");
    model.addAttribute("data", Excel_data);
    logger.info("***** VisaSettlementController.DownloadVisaSettlementReport POST End ****");
    return "GenerateVisaSettlementReport";
  }
  
  @RequestMapping(value = {"VisaSettlementProcessTTUM"}, method = {RequestMethod.GET})
  public ModelAndView VisaSettlementTTUMGet(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("HELLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
    RupayUploadBean rupaySettlementBean = new RupayUploadBean();
    logger.info("RupaySettlementTTUM GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    modelAndView.addObject("category", category);
    modelAndView.addObject("rupaySettlementBean", rupaySettlementBean);
    modelAndView.setViewName("VisaSettlementProcessTTUM");
    logger.info("***** RupaySettlementController.VisaSettlementTTUM GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"visaValidateSettlementTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public String visaValidateSettlementTTUM(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** RupaySettlementController.VISAValidateSettlementTTUM post Start ****");
    logger.info("VISA ValidateSettlementTTUM POST");
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    System.out.println("inside download validate");
    beanObj.setCreatedBy(Createdby);
    Boolean checkFlag = this.visaSettlementService.visavalidateSettlementTTUM(beanObj);
    if (checkFlag.booleanValue())
      return "success"; 
    return "Settlement TTUM is not processed for selected date and cycle";
  }
  
  @RequestMapping(value = {"DownloadSettlementVisa"}, method = {RequestMethod.POST})
  public String DownloadSettlementVisa(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model) throws Exception {
    logger.info("***** RupaySettlementController.DownloadSettlement post Start ****");
    logger.info("DownloadSettlement POST");
    logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate() + 
        beanObj.getTTUM_TYPE());
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    String fileName = "", zipName = "";
    System.out.println("File name is " + beanObj.getFileName());
    boolean executionFlag = false;
    List<Object> Excel_data = new ArrayList();
    if (beanObj.getTTUM_TYPE().equalsIgnoreCase("EXCEL_ACQ") || 
      beanObj.getTTUM_TYPE().equalsIgnoreCase("EXCEL_ISS")) {
      Excel_data = this.visaSettlementService.getSettlementDataVisa(beanObj);
    } else {
      List<Object> TTUMData = new ArrayList();
      logger.info("Created by is " + Createdby);
      beanObj.setCreatedBy(Createdby);
      logger.info("File Name is " + beanObj.getFileName());
      boolean executed = false;
      TTUMData = this.SETTLTTUMSERVICE.getDailyNFSTTUMDataVISA(beanObj);
      if (beanObj.getTTUM_TYPE().equalsIgnoreCase("EXCEL_ACQ")) {
        fileName = "VISASETTLEMENTTTUMACQ" + beanObj.getSubcategory() + 
          beanObj.getFileDate().replaceAll("/", "") + "_.Val.txt";
      } else {
        fileName = "VISASETTLEMENTTTUMISS" + beanObj.getSubcategory() + 
          beanObj.getFileDate().replaceAll("/", "") + "_.Val.txt";
      } 
      String stPath = OUTPUT_FOLDER;
      logger.info("TEMP_DIR" + stPath);
      GenerateUCOTTUM obj = new GenerateUCOTTUM();
      stPath = obj.checkAndMakeDirectory(beanObj.getFileDate().replaceAll("/", ""), beanObj.getSubcategory());
      obj.generateADJTTUMFilesRupay(stPath, fileName, 1, TTUMData, "RUPAY", beanObj);
      if (beanObj.getTTUM_TYPE().equalsIgnoreCase("EXCEL_ACQ")) {
        zipName = "VISASETTLEMENTTTUMACQ" + beanObj.getSubcategory() + beanObj.getFileDate().replaceAll("/", "") + 
          "_.Val.txt";
      } else {
        zipName = "VISASETTLEMENTTTUMISS" + beanObj.getSubcategory() + beanObj.getFileDate().replaceAll("/", "") + 
          "_.Val.txt";
      } 
      File file = new File(String.valueOf(stPath) + File.separator + zipName);
      logger.info("path of zip file " + stPath + File.separator + zipName);
      FileInputStream inputstream = new FileInputStream(file);
      response.setContentLength((int)file.length());
      logger.info("before downloading zip file ");
      response.setContentType("application/txt");
      logger.info("download completed");
      String headerKey = "Content-Disposition";
      String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
      response.setHeader(headerKey, headerValue);
      ServletOutputStream servletOutputStream = response.getOutputStream();
      IOUtils.copy(inputstream, (OutputStream)servletOutputStream);
      response.flushBuffer();
    } 
    if (beanObj.getTTUM_TYPE().equalsIgnoreCase("EXCEL_ACQ")) {
      model.addAttribute("ReportName", 
          "Visa_Settlement_Acq_" + beanObj.getSubcategory() + "_Report_" + beanObj.getFileDate());
    } else {
      model.addAttribute("ReportName", 
          "Visa_Settlement_Iss_" + beanObj.getSubcategory() + "_Report_" + beanObj.getFileDate());
    } 
    model.addAttribute("data", Excel_data);
    logger.info("***** RupaySettlementController.DownloadSettlement POST End ****");
    return "GenerateVisaSettlementReport";
  }
  
  @RequestMapping(value = {"VisaSettlementTTUMProces"}, method = {RequestMethod.POST})
  @ResponseBody
  public String VisaSettlementTTUMProces(@ModelAttribute("visaUploadBean") VisaUploadBean visaUploadBean, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes) throws Exception {
    logger.info("***** VisaSettlementController.VisaSettlementTTUMProces.Get Start ****");
    logger.info("VisaSettlementTTUMProces Post " + visaUploadBean.getFileDate());
    String display = "";
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    visaUploadBean.setCreatedBy(Createdby);
    HashMap<String, Object> output = this.visaSettlementService.CheckTTUMProcessed(visaUploadBean);
    if (output != null && ((Boolean)output.get("result")).booleanValue()) {
      boolean flag = this.visaSettlementService.runVisaSettlementTTUM(visaUploadBean);
      if (flag)
        return "Processing Completed"; 
      return "Problem while Processing TTUM";
    } 
    return output.get("msg").toString();
  }
  
  @RequestMapping(value = {"ValidateVisaSettlementTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public String ValidateSettlementTTUM(@ModelAttribute("visaUploadBean") VisaUploadBean visaUploadBean, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes) throws Exception {
    logger.info("***** VisaSettlementController.ValidateSettlementTTUM.Post Start ****");
    logger.info("ValidateSettlementTTUM Post " + visaUploadBean.getFileDate());
    String display = "";
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    visaUploadBean.setCreatedBy(Createdby);
    HashMap<String, Object> output = this.visaSettlementService.CheckTTUMProcessed(visaUploadBean);
    if (output != null && !((Boolean)output.get("result")).booleanValue())
      return "success"; 
    return "TTUM is not processed for selected date";
  }
  
  @RequestMapping(value = {"DownloadVisaSettlementTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadVisaSettlementTTUM(@ModelAttribute("visaUploadBean") VisaUploadBean visaUploadBean, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** VisaSettlementController.DownloadVisaSettlementTTUM.Get Start ****");
    logger.info("DownloadVisaSettlementTTUM Post " + visaUploadBean.getFileDate());
    String display = "";
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    visaUploadBean.setCreatedBy(Createdby);
    List<Object> Excel_data = new ArrayList();
    Excel_data = this.visaSettlementService.getSettlementTTUMData(visaUploadBean);
    String fileName = "VISA_SETTLEMENT_TTUM.txt";
    String stPath = System.getProperty("java.io.tmpdir");
    logger.info("TEMP_DIR" + stPath);
    GenerateUCOTTUM obj = new GenerateUCOTTUM();
    stPath = obj.checkAndMakeDirectory(visaUploadBean.getFileDate(), "RUPAY");
    obj.generateTTUMFile(stPath, fileName, Excel_data);
    logger.info("File is created");
    File file = new File(String.valueOf(stPath) + File.separator + fileName);
    logger.info("path of zip file " + stPath + File.separator + fileName);
    FileInputStream inputstream = new FileInputStream(file);
    response.setContentLength((int)file.length());
    logger.info("before downloading zip file ");
    response.setContentType("application/txt");
    logger.info("download completed");
    String headerKey = "Content-Disposition";
    String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
    response.setHeader(headerKey, headerValue);
    ServletOutputStream servletOutputStream = response.getOutputStream();
    IOUtils.copy(inputstream, (OutputStream)servletOutputStream);
    response.flushBuffer();
  }
  
  @RequestMapping(value = {"DownloadVisaAdjustmentTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadVisaAdjustmentTTUM(CompareSetupBean compareSetupBean, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** VisaSettlementController.DownloadVisaSettlementTTUM.Get Start ****");
    String display = "";
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    List<Object> Excel_data = new ArrayList();
  }
  
  @RequestMapping(value = {"VISAAdjustmentTTUM"}, method = {RequestMethod.GET})
  public ModelAndView VisaAdjustmentTTUM(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** VisaAdjustmentTTUM.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("NFSSettlement GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("GenerateVisaAdjustmentTTUM");
    logger.info("***** VisaSettlementController.VisaAdjustmentTTUM GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"VisaAdjustmentFileUpload"}, method = {RequestMethod.GET})
  public ModelAndView VisaAdjustmentFileUpload(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
    logger.info("***** VisaSettlementController.Get Start ****");
    logger.info("VisaEPFileRead GET");
    String display = "";
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.setViewName("VisaAdjustmentFileUpload");
    logger.info("***** VisaSettlementController.VisaEPFileRead GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"VisaAdjustmentFileUpload"}, method = {RequestMethod.POST})
  @ResponseBody
  public String VisaAdjustmentUpload(@ModelAttribute("visaUploadBean") VisaUploadBean visaUploadBean, HttpServletRequest request, @RequestParam("file") MultipartFile file, HttpSession httpSession, RedirectAttributes redirectAttributes) throws Exception {
    logger.info("***** VisaSettlementController.Get Start ****");
    logger.info("VisaEPFileRead Post " + visaUploadBean.getFileDate());
    logger.info("VisaEPUpload Post : File Type is " + visaUploadBean.getFileType());
    logger.info("File name is  " + file.getOriginalFilename());
    String display = "";
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    visaUploadBean.setCreatedBy(Createdby);
    if (visaUploadBean.getFileName().equalsIgnoreCase("DISPUTE")) {
      HashMap<String, Object> output = this.visaSettlementService
        .checkAdjustmentFileAlreadyUpload(visaUploadBean.getFileDate(), visaUploadBean.getFileType());
      if (output != null && ((Boolean)output.get("result")).booleanValue()) {
        boolean uploadFlag = this.visaSettlementService.uploadAwaitingFile(visaUploadBean, file);
        if (uploadFlag)
          return "File Uploaded Successfully"; 
        return "File Not Uploaded";
      } 
      return output.get("msg").toString();
    } 
    return null;
  }
  
  @RequestMapping(value = {"VisaAdjustmentTTUM"}, method = {RequestMethod.GET})
  public ModelAndView VisaAdjustmentTTUM(Model model, CompareSetupBean compareSetupBean, ModelAndView modelAndView, HttpSession httpSession, HttpServletRequest request) throws Exception {
    logger.info("***** VisaSettlementController.AdjustmentTTUM start ****");
    try {
      compareSetupBean.setCreatedBy(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
      ArrayList<CompareSetupBean> setupBeanslist = null;
      String csrf = CSRFToken.getTokenForSession(request.getSession());
      modelAndView.addObject("CSRFToken", csrf);
      model.addAttribute("configBeanlist", setupBeanslist);
      modelAndView.setViewName("VisaAdjustmentTTUM");
      modelAndView.addObject("CompareSetupBean", compareSetupBean);
      logger.info("***** VisaSettlementController.SettlementTTUM End ****");
      return modelAndView;
    } catch (Exception ex) {
      demo.logSQLException(ex, "VisaSettlementController.AdjustmentTTUM");
      logger.error(" error in VisaSettlementController.AdjustmentTTUM", 
          new Exception("VisaSettlementController.AdjustmentTTUM\"", ex));
      modelAndView.setViewName("Login");
      return modelAndView;
    } 
  }
  
  @RequestMapping(value = {"VisaAdjustmentTTUM1"}, method = {RequestMethod.POST})
  @ResponseBody
  public String VisaAdjustmentTTUMValidation(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** NFSAdjTTUMValidation.Post Start ****");
    logger.info("ADjtype is " + nfsSettlementBean.getAdjType());
    nfsSettlementBean.setCategory("NFS_ADJUSTMENT");
    HashMap<String, Object> result = this.visaSettlementService.ValidateForAdjTTUM(nfsSettlementBean);
    if (result != null && ((Boolean)result.get("result")).booleanValue()) {
      boolean executed = this.SETTLTTUMSERVICE.runVisaAdjTTUM(nfsSettlementBean);
      if (executed)
        return "Adjustment TTUM Processing Completed.\n Please Download the Reports"; 
      return "Exception while processing TTUM";
    } 
    if (result == null)
      return "Error"; 
    if (!result.get("msg").toString().equalsIgnoreCase("")) {
      System.out.println(result.get("msg").toString());
      return result.get("msg").toString();
    } 
    return "Problem Occurred!";
  }
  
  @RequestMapping(value = {"ValidateDownloadVisaAdjTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public String ValidateDownloadAdjTTUM(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** DownloadSettlementreport.POST Start ****");
    logger.info("DownloadAdjTTUM POST");
    nfsSettlementBean
      .setCategory(String.valueOf(nfsSettlementBean.getFileName()) + "_" + nfsSettlementBean.getAdjType() + "_ADJUSTMENT");
    List<Object> Excel_data = new ArrayList();
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    nfsSettlementBean.setCreatedBy(Createdby);
    logger.info("File Name is " + nfsSettlementBean.getFileName());
    boolean executed = false;
    if (nfsSettlementBean.getDatepicker().contains(","));
    nfsSettlementBean.setDatepicker(nfsSettlementBean.getDatepicker().replace(",", ""));
    boolean checkProcFlag = this.visaSettlementService.checkAdjTTUMProcess(nfsSettlementBean);
    if (checkProcFlag)
      return "success"; 
    return "Adjustment TTUM is not processed.\n Please process TTUM";
  }
  
  @RequestMapping(value = {"DownloadVisaAdjTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadAdjTTUM(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** DownloadSettlementreport.POST Start ****");
    logger.info("DownloadAdjTTUM POST");
    nfsSettlementBean
      .setCategory(String.valueOf(nfsSettlementBean.getFileName()) + "_" + nfsSettlementBean.getAdjType() + "_ADJUSTMENT");
    List<Object> Excel_data = new ArrayList();
    List<Object> TTUMData = new ArrayList();
    List<Object> PBGB_TTUMData = new ArrayList();
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    nfsSettlementBean.setCreatedBy(Createdby);
    logger.info("File Name is " + nfsSettlementBean.getFileName());
    boolean executed = false;
    if (nfsSettlementBean.getDatepicker().contains(","));
    nfsSettlementBean.setDatepicker(nfsSettlementBean.getDatepicker().replace(",", ""));
    TTUMData = this.SETTLTTUMSERVICE.getVisaAdjTTUM(nfsSettlementBean);
    String fileName = "VISA_ADJUSTMENT_" + nfsSettlementBean.getAdjType() + "_TTUM.txt";
    String stPath = OUTPUT_FOLDER;
    logger.info("TEMP_DIR" + stPath);
    GenerateUCOTTUM obj = new GenerateUCOTTUM();
    stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker(), nfsSettlementBean.getCategory());
    obj.generateMultipleDRMTTUMFiles(stPath, fileName, 1, TTUMData, "NFS");
    logger.info("File is created");
    List<String> Column_list = new ArrayList<>();
    Column_list.add("ACCOUNT_NUMBER");
    Column_list.add("PART_TRAN_TYPE");
    Column_list.add("TRANSACTION_AMOUNT");
    Column_list.add("TRANSACTION_PARTICULAR");
    Column_list.add("ADJTYPE");
    Excel_data.add(Column_list);
    Excel_data.add(TTUMData);
    fileName = "VISA_ADJUSTMENT_" + nfsSettlementBean.getAdjType() + "_TTUMS.xls";
    String zipName = "VISA_ADJ_" + nfsSettlementBean.getAdjType() + "_TTUM.zip";
    obj.generateExcelTTUM(stPath, fileName, Excel_data, "REFUND", zipName);
    logger.info("File is created");
    File file = new File(String.valueOf(stPath) + File.separator + zipName);
    logger.info("path of zip file " + stPath + File.separator + zipName);
    FileInputStream inputstream = new FileInputStream(file);
    response.setContentLength((int)file.length());
    logger.info("before downloading zip file ");
    response.setContentType("application/txt");
    logger.info("download completed");
    String headerKey = "Content-Disposition";
    String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
    response.setHeader(headerKey, headerValue);
    ServletOutputStream servletOutputStream = response.getOutputStream();
    IOUtils.copy(inputstream, (OutputStream)servletOutputStream);
    response.flushBuffer();
  }
}
