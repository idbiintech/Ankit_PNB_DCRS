package com.recon.control;



import com.recon.model.AddNewSolBean;
import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.service.ISourceService;
import com.recon.service.NFSSettlementCalService;
import com.recon.service.NFSSettlementService;
import com.recon.util.CSRFToken;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class NFSSettlementController {
  private static final Logger logger = Logger.getLogger(com.recon.control.NFSSettlementController.class);
  
  private static final String ERROR_MSG = "error_msg";
  
  @Autowired
  ISourceService iSourceService;
  
  @Autowired
  NFSSettlementService nfsSettlementService;
  
  @Autowired
  NFSSettlementCalService nfsSettlementCalService;
  
  @RequestMapping(value = {"nfsFileUpload"}, method = {RequestMethod.GET})
  public ModelAndView nfsFileUploadGet(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** nfsFileUpload.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("nfsFileUpload GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    List<String> subcat = this.iSourceService.getSubcategories(category);
    if (category.equals("ONUS") || category.equals("AMEX") || category.equals("CARDTOCARD") || 
      category.equals("WCC"))
      display = "none"; 
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("NFSSettlementFileUpload");
    logger.info("***** NFSSettlementController.nfsFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"nfsFileUpload"}, method = {RequestMethod.POST})
  @ResponseBody
  public String nfsFileUploadPost(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean, HttpServletRequest request, @RequestParam("file") MultipartFile file, String filename, String category, String stSubCategory, String datepicker, HttpSession httpSession, Model model, ModelAndView modelAndView, RedirectAttributes redirectAttributes) throws Exception {
    logger.info("***** nfsFileUpload.post Start ****");
    HashMap<String, Object> output = null;
    try {
      logger.info("RECON PROCESS GET");
      String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
      logger.info("Created by is " + Createdby);
      nfsSettlementBean.setCreatedBy(Createdby);
      logger.info("VALUES ARE " + nfsSettlementBean + " " + category + " " + stSubCategory + datepicker);
      nfsSettlementBean.setCategory(String.valueOf(category) + "_SETTLEMENT");
      HashMap<String, Object> result = this.nfsSettlementService.validatePrevFileUpload(nfsSettlementBean);
      if (result != null && !((Boolean)result.get("result")).booleanValue()) {
        if (nfsSettlementBean.getFileName().equalsIgnoreCase("DFS") || 
          nfsSettlementBean.getFileName().equalsIgnoreCase("JCB-UPI"))
          output = this.nfsSettlementService.uploadDFSRawData(nfsSettlementBean, file); 
        logger.info("***** NFSSettlementController.nfsFileUpload POST End ****");
        if (((Boolean)output.get("result")).booleanValue())
          return "File Uploaded Successfully \n Count is " + (Integer)output.get("count"); 
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
  
  @RequestMapping(value = {"ICDNTSLFileUpload"}, method = {RequestMethod.GET})
  public ModelAndView ICDNTSLFileUpload(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** ICDNTSLFileUpload.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("ICDNTSLFileUpload GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    List<String> subcat = this.iSourceService.getSubcategories(category);
    if (category.equals("ONUS") || category.equals("AMEX") || category.equals("CARDTOCARD") || 
      category.equals("WCC"))
      display = "none"; 
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("ICDNTSLFileUpload");
    logger.info("***** NFSSettlementController.NTSLFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"JCBNTSLFileUpload"}, method = {RequestMethod.GET})
  public ModelAndView JCBNTSLFileUpload(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** JCBNTSLFileUpload.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("JCBNTSLFileUpload GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    List<String> subcat = this.iSourceService.getSubcategories(category);
    if (category.equals("ONUS") || category.equals("AMEX") || category.equals("CARDTOCARD") || 
      category.equals("WCC"))
      display = "none"; 
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("JCBNTSLFileUpload");
    logger.info("***** NFSSettlementController.NTSLFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"DFSNTSLFileUpload"}, method = {RequestMethod.GET})
  public ModelAndView DFSNTSLFileUpload(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** DFSNTSLFileUpload.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("DFSNTSLFileUpload GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    List<String> subcat = this.iSourceService.getSubcategories(category);
    if (category.equals("ONUS") || category.equals("AMEX") || category.equals("CARDTOCARD") || 
      category.equals("WCC"))
      display = "none"; 
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("DFSNTSLFileUpload");
    logger.info("***** DFSNTSLFileUpload.DFSNTSLFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"NTSLFileUpload"}, method = {RequestMethod.GET})
  public ModelAndView ntslFileUploadGet(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** NTSLFileUpload.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("NTSLFileUpload GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    List<String> subcat = this.iSourceService.getSubcategories(category);
    if (category.equals("ONUS") || category.equals("AMEX") || category.equals("CARDTOCARD") || 
      category.equals("WCC"))
      display = "none"; 
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("NTSLFileUpload");
    logger.info("***** NFSSettlementController.NTSLFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"NTSLFileUpload"}, method = {RequestMethod.POST})
  @ResponseBody
  public String ntslFileUploadPost(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean, HttpServletRequest request, @RequestParam("file") MultipartFile file, HttpSession httpSession, RedirectAttributes redirectAttributes) throws Exception {
    logger.info("***** NTSLFileUpload.Post Start ****");
    HashMap<String, Object> mapObj = new HashMap<>();
    logger.info("Data is " + nfsSettlementBean);
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby + nfsSettlementBean.getFileName() + " " + nfsSettlementBean.getCategory());
    nfsSettlementBean.setCreatedBy(Createdby);
    String FileName = nfsSettlementBean.getFileName();
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
    if (nfsSettlementBean.getTimePeriod() != null && nfsSettlementBean.getTimePeriod().equalsIgnoreCase("Daily"))
      try {
        HashMap<String, Object> result = this.nfsSettlementService.validatePrevFileUpload(nfsSettlementBean);
        if (result != null && !((Boolean)result.get("result")).booleanValue()) {
          nfsSettlementBean.setFileName(FileName);
          mapObj = this.nfsSettlementService.uploadNTSLFile(nfsSettlementBean, file);
          logger.info("***** NFSSettlementController.NTSLFileUpload GET End ****");
          if (((Boolean)mapObj.get("result")).booleanValue())
            return "File Uploaded Successfully!"; 
          throw new Exception("Exception on line " + (Integer)mapObj.get("count") + " while reading NTSL file");
        } 
        return result.get("msg").toString();
      } catch (Exception e) {
        logger.info("Exception in NFSSettlementController " + e);
        if (mapObj != null && !((Boolean)mapObj.get("result")).booleanValue())
          return "Error Occured at Line " + (Integer)mapObj.get("count"); 
        return "Error Occurred in reading";
      }  
    nfsSettlementBean.setCategory("MONTHLY_SETTLEMENT");
    mapObj = this.nfsSettlementService.checkMonthlyNTSLUploaded(nfsSettlementBean);
    if (mapObj != null) {
      boolean result = ((Boolean)mapObj.get("result")).booleanValue();
      if (result) {
        mapObj = this.nfsSettlementService.uploadMonthlyNTSLFile(nfsSettlementBean, file);
        if (((Boolean)mapObj.get("result")).booleanValue())
          return "NTSL Readed Successfully! "; 
        return "Exception on line " + (Integer)mapObj.get("count") + " while reading NTSL file";
      } 
      return mapObj.get("msg").toString();
    } 
    logger.info("Exception while validation");
    return "Exception while uploading";
  }
  
  @RequestMapping(value = {"SuspectFileUpload"}, method = {RequestMethod.GET})
  public ModelAndView SuspectFileUploadGet(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** SuspectFileUpload.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("SuspectFileUpload GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    List<String> subcat = this.iSourceService.getSubcategories(category);
    if (category.equals("ONUS") || category.equals("AMEX") || category.equals("CARDTOCARD") || 
      category.equals("WCC"))
      display = "none"; 
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("SuspectFileUpload");
    logger.info("***** NFSSettlementController.SuspectFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"SuspectFileUpload"}, method = {RequestMethod.POST})
  @ResponseBody
  public String SuspectFileUploadPost(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean, HttpServletRequest request, @RequestParam("file") MultipartFile file, HttpSession httpSession, RedirectAttributes redirectAttributes) throws Exception {
    logger.info("***** SuspectFileUpload.Post Start ****");
    HashMap<String, Object> mapObj = new HashMap<>();
    logger.info("Data is " + nfsSettlementBean);
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    String FileName = file.getOriginalFilename();
    if (nfsSettlementBean.getTimePeriod() != null && nfsSettlementBean.getTimePeriod().equalsIgnoreCase("Daily"))
      try {
        HashMap<String, Object> result = this.nfsSettlementService.validatePrevFileUploadSUS(nfsSettlementBean);
        if (result != null && !((Boolean)result.get("result")).booleanValue()) {
          nfsSettlementBean.setFileName(FileName);
          mapObj = this.nfsSettlementService.uploadSUSPECTFile(nfsSettlementBean, file);
          logger.info("***** NFSSettlementController.SUSFileUpload GET End ****");
          if (((Boolean)mapObj.get("result")).booleanValue())
            return "File Uploaded Successfully! "; 
          return "Exception on line " + (Integer)mapObj.get("count") + " while reading NTSL file";
        } 
        return result.get("msg").toString();
      } catch (Exception e) {
        logger.info("Exception in NFSSettlementController " + e);
        if (mapObj != null && !((Boolean)mapObj.get("result")).booleanValue())
          return "Error Occured at Line " + (Integer)mapObj.get("count"); 
        return "Error Occurred in reading";
      }  
    nfsSettlementBean.setCategory("MONTHLY_SETTLEMENT");
    mapObj = this.nfsSettlementService.checkMonthlyNTSLUploaded(nfsSettlementBean);
    if (mapObj != null) {
      boolean result = ((Boolean)mapObj.get("result")).booleanValue();
      if (result) {
        mapObj = this.nfsSettlementService.uploadMonthlyNTSLFile(nfsSettlementBean, file);
        if (((Boolean)mapObj.get("result")).booleanValue())
          return "NTSL Readed Successfully! \n Total Count is " + (Integer)mapObj.get("count"); 
        return "Exception on line " + (Integer)mapObj.get("count") + " while reading NTSL file";
      } 
      return mapObj.get("msg").toString();
    } 
    logger.info("Exception while validation");
    return "Exception while uploading";
  }
  
  @RequestMapping(value = {"NFSSettlement"}, method = {RequestMethod.GET})
  public ModelAndView NFSSettlement(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** NFSSettlement.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("NFSSettlement GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    List<String> subcat = this.iSourceService.getSubcategories(category);
    if (category.equals("ONUS") || category.equals("AMEX") || category.equals("CARDTOCARD") || 
      category.equals("WCC"))
      display = "none"; 
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("GenerateNFSSettlement");
    logger.info("***** NFSSettlementController.NFSSettlement GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"NFSSettlementValidation"}, method = {RequestMethod.POST})
  @ResponseBody
  public String NFSMonthlyValidation(String fileDate, String stSubCategory, String timePeriod, String cycle, String filename, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    HashMap<String, Object> result;
    logger.info("***** NFSSettlementProcess.Post Start ****");
    String lastDate = fileDate;
    logger.info("NFSMonthlyValidation POST");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    nfsSettlementBean.setCategory("NFS_SETTLEMENT");
    logger.info("filename is " + filename);
    nfsSettlementBean.setFileName(filename);
    nfsSettlementBean.setStSubCategory(stSubCategory);
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    nfsSettlementBean.setCreatedBy(Createdby);
    nfsSettlementBean.setCycle(Integer.parseInt(cycle));
    if (timePeriod != null && timePeriod.equalsIgnoreCase("Monthly")) {
      System.out.println("File date is " + fileDate);
      fileDate = "01/" + fileDate;
      System.out.println("Filedate is " + fileDate);
      try {
        LocalDate lastDayOfMonth = LocalDate.parse(fileDate, DateTimeFormatter.ofPattern("dd/M/yyyy"))
          .with(TemporalAdjusters.lastDayOfMonth());
        System.out.println("lastDayOfMonth " + lastDayOfMonth);
        lastDate = lastDayOfMonth.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        System.out.println("TEstdate is " + lastDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/M/yyyy");
        LocalDate t = LocalDate.parse(fileDate, formatter);
        System.out.println("t " + t);
        fileDate = t.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
      } catch (Exception e) {
        System.out.println("Exception is " + e);
        return "Error while getting last Date";
      } 
      nfsSettlementBean.setDatepicker(fileDate);
      nfsSettlementBean.setToDate(lastDate);
      nfsSettlementBean.setStSubCategory(stSubCategory);
      result = this.nfsSettlementService.checkNFSMonthlyProcess(nfsSettlementBean);
      if (result != null && ((Boolean)result.get("result")).booleanValue())
        return "success"; 
      if (result == null)
        return "Error"; 
      if (!result.get("msg").toString().equalsIgnoreCase("")) {
        System.out.println(result.get("msg").toString());
        return result.get("msg").toString();
      } 
      return "Problem Occurred!";
    } 
    nfsSettlementBean.setDatepicker(fileDate);
    if (nfsSettlementBean.getFileName().equals("NTSL-NFS")) {
      result = this.nfsSettlementService.ValidateDailySettProcess(nfsSettlementBean);
    } else {
      result = this.nfsSettlementService.ValidateOtherSettProcess(nfsSettlementBean);
    } 
    if (result != null && ((Boolean)result.get("result")).booleanValue()) {
      boolean executed = false;
      if (nfsSettlementBean.getFileName().contains("NFS")) {
        executed = this.nfsSettlementCalService.runNFSDailyProc(nfsSettlementBean);
      } else if (nfsSettlementBean.getFileName().contains("PBGB")) {
        executed = this.nfsSettlementCalService.runPBGBDailyProc(nfsSettlementBean);
      } else {
        executed = this.nfsSettlementCalService.runDFSJCBDailyProc(nfsSettlementBean);
      } 
      if (executed)
        return "Settlement Processed Successfully \n Please download report"; 
      return "Settlement Processing Failed";
    } 
    if (result == null)
      return "Error"; 
    if (!result.get("msg").toString().equalsIgnoreCase("")) {
      System.out.println(result.get("msg").toString());
      return result.get("msg").toString();
    } 
    return "Problem Occurred!";
  }
  
  @RequestMapping(value = {"NFSSettProcessValidation"}, method = {RequestMethod.POST})
  @ResponseBody
  public String NFSSettProcessValidation(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("Inside NFSSettProcessValidation Post");
    HashMap<String, Object> output = this.nfsSettlementService.CheckSettlementProcess(nfsSettlementBean);
    if (output != null && ((Boolean)output.get("result")).booleanValue())
      return "success"; 
    return output.get("msg").toString();
  }
  
  @RequestMapping(value = {"DownloadSettreport"}, method = {RequestMethod.POST})
  public String DownloadSettreport(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** DownloadSettreport.POST Start ****");
    logger.info("NFSSettlement POST");
    nfsSettlementBean.setCategory("NFS_SETTLEMENT");
    List<Object> Excel_data = new ArrayList();
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    nfsSettlementBean.setCreatedBy(Createdby);
    logger.info("File Name is " + nfsSettlementBean.getFileName());
    boolean executed = false;
    if (nfsSettlementBean.getDatepicker().contains(","));
    nfsSettlementBean.setDatepicker(nfsSettlementBean.getDatepicker().replace(",", ""));
    Excel_data = this.nfsSettlementCalService.getDailySettlementReport(nfsSettlementBean);
    model.addAttribute("ReportName", 
        String.valueOf(nfsSettlementBean.getFileName()) + "_Settlement_Cycle" + nfsSettlementBean.getCycle());
    model.addAttribute("data", Excel_data);
    logger.info("***** NFSSettlementController.NFSSettlementProcess Daily POST End ****");
    return "GenerateNFSDailyReport";
  }
  
  @RequestMapping(value = {"SkipSettlement"}, method = {RequestMethod.POST})
  @ResponseBody
  public String skipSettlement(String fileDate, String stSubCategory, String timePeriod, String cycle, String filename, @ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("Inside skipSettlement: POST");
    nfsSettlementBean.setCategory("NFS_SETTLEMENT");
    nfsSettlementBean.setFileName(filename);
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    nfsSettlementBean.setCreatedBy(Createdby);
    nfsSettlementBean.setDatepicker(fileDate);
    HashMap<String, Object> result = this.nfsSettlementCalService.skipSettlement(nfsSettlementBean);
    if (result != null && ((Boolean)result.get("result")).booleanValue())
      return "Record Updated Successfully"; 
    if (result == null)
      return "Error"; 
    if (!result.get("msg").toString().equalsIgnoreCase("")) {
      System.out.println(result.get("msg").toString());
      return result.get("msg").toString();
    } 
    return "Problem Occurred!";
  }
  
  @RequestMapping(value = {"NFSInterchange"}, method = {RequestMethod.GET})
  public ModelAndView NFSInterchange(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** NFSInterchange.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("NFSSettlement GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    List<String> subcat = this.iSourceService.getSubcategories(category);
    if (category.equals("ONUS") || category.equals("AMEX") || category.equals("CARDTOCARD") || 
      category.equals("WCC"))
      display = "none"; 
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("GenerateNFSInterchange");
    logger.info("***** NFSSettlementController.NFSInterchange GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"NFSInterchangeValidation"}, method = {RequestMethod.POST})
  @ResponseBody
  public String NFSInterchangeValidation(String fileDate, String stSubCategory, String timePeriod, String filename, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** NFSInterchangeValidation.Post Start ****");
    String lastDate = fileDate;
    logger.info("NFSInterchangeValidation POST");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    nfsSettlementBean.setCategory("NFS_SETTLEMENT");
    logger.info("filename is " + filename);
    nfsSettlementBean.setFileName(filename);
    nfsSettlementBean.setStSubCategory(stSubCategory);
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    nfsSettlementBean.setCreatedBy(Createdby);
    if (timePeriod != null && timePeriod.equalsIgnoreCase("Monthly")) {
      System.out.println("File date is " + fileDate);
      fileDate = "01/" + fileDate;
      System.out.println("Filedate is " + fileDate);
      try {
        LocalDate lastDayOfMonth = LocalDate.parse(fileDate, DateTimeFormatter.ofPattern("dd/M/yyyy"))
          .with(TemporalAdjusters.lastDayOfMonth());
        System.out.println("lastDayOfMonth " + lastDayOfMonth);
        lastDate = lastDayOfMonth.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        System.out.println("TEstdate is " + lastDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/M/yyyy");
        LocalDate t = LocalDate.parse(fileDate, formatter);
        System.out.println("t " + t);
        fileDate = t.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
      } catch (Exception e) {
        System.out.println("Exception is " + e);
        return "Error while getting last Date";
      } 
      nfsSettlementBean.setDatepicker(fileDate);
      nfsSettlementBean.setToDate(lastDate);
      nfsSettlementBean.setStSubCategory(stSubCategory);
      HashMap<String, Object> hashMap = this.nfsSettlementService.checkNFSMonthlyProcess(nfsSettlementBean);
      if (hashMap != null && ((Boolean)hashMap.get("result")).booleanValue())
        return "success"; 
      if (hashMap == null)
        return "Error"; 
      if (!hashMap.get("msg").toString().equalsIgnoreCase("")) {
        System.out.println(hashMap.get("msg").toString());
        return hashMap.get("msg").toString();
      } 
      return "Problem Occurred!";
    } 
    nfsSettlementBean.setDatepicker(fileDate);
    HashMap<String, Object> result = this.nfsSettlementService.ValidateDailyInterchangeProcess(nfsSettlementBean);
    if (result != null && ((Boolean)result.get("result")).booleanValue())
      return "success"; 
    if (result == null)
      return "Error"; 
    if (!result.get("msg").toString().equalsIgnoreCase("")) {
      System.out.println(result.get("msg").toString());
      return result.get("msg").toString();
    } 
    return "Problem Occurred!";
  }
  
  @RequestMapping(value = {"DownloadInterchangereport"}, method = {RequestMethod.POST})
  public String DownloadInterchangereport(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** DownloadSettlementreport.POST Start ****");
    logger.info("NFSSettlement POST");
    nfsSettlementBean.setCategory("NFS_SETTLEMENT");
    List<Object> Excel_data = new ArrayList();
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    nfsSettlementBean.setCreatedBy(Createdby);
    if (nfsSettlementBean.getTimePeriod().equalsIgnoreCase("MONTHLY")) {
      if (nfsSettlementBean.getDatepicker().contains(","));
      nfsSettlementBean.setDatepicker(nfsSettlementBean.getDatepicker().replace(",", ""));
      String fileDate = nfsSettlementBean.getDatepicker();
      String lastDate = fileDate;
      System.out.println("File date is " + nfsSettlementBean.getDatepicker());
      fileDate = "01/" + fileDate;
      System.out.println("Filedate is " + fileDate);
      try {
        LocalDate lastDayOfMonth = LocalDate.parse(fileDate, DateTimeFormatter.ofPattern("dd/M/yyyy"))
          .with(TemporalAdjusters.lastDayOfMonth());
        System.out.println("lastDayOfMonth " + lastDayOfMonth);
        lastDate = lastDayOfMonth.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        System.out.println("TEstdate is " + lastDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/M/yyyy");
        LocalDate t = LocalDate.parse(fileDate, formatter);
        System.out.println("t " + t);
        fileDate = t.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
      } catch (Exception e) {
        System.out.println("Exception is " + e);
        return "Error while getting last Date";
      } 
      nfsSettlementBean.setDatepicker(fileDate);
      nfsSettlementBean.setToDate(lastDate);
      nfsSettlementBean.setCategory("MONTHLY_SETTLEMENT");
      boolean bool = this.nfsSettlementCalService.runNFSMonthlyProc(nfsSettlementBean);
      if (bool)
        Excel_data = this.nfsSettlementCalService.getInterchangeData(nfsSettlementBean); 
      model.addAttribute("Monthly_data", Excel_data);
      model.addAttribute("ReportName", String.valueOf(nfsSettlementBean.getFileName()) + "SETTLEMENT_REPORT");
      logger.info("***** NFSSettlementController.NFSSettlementProcess monthly POST End ****");
      return "GenerateNFSMonthlyReport";
    } 
    logger.info("File Name is " + nfsSettlementBean.getFileName());
    boolean executed = false;
    if (nfsSettlementBean.getDatepicker().contains(","));
    nfsSettlementBean.setDatepicker(nfsSettlementBean.getDatepicker().replace(",", ""));
    executed = this.nfsSettlementCalService.runDailyInterchangeProc(nfsSettlementBean);
    if (executed)
      Excel_data = this.nfsSettlementCalService.getDailyInterchangeData(nfsSettlementBean); 
    model.addAttribute("ReportName", "Settlement");
    model.addAttribute("Monthly_data", Excel_data);
    logger.info("***** NFSSettlementController.NFSSettlementProcess Daily POST End ****");
    return "GenerateNFSMonthlyReport";
  }
  
  @RequestMapping(value = {"SettlementRectify"}, method = {RequestMethod.POST})
  @ResponseBody
  public String SettlementRectify(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean, String category, String datepicker, String stSubCategory, String timePeriod, String cycle, String fileName, String rectAmt, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** SettlementRectify.post Start ****");
    HashMap<String, Object> output = null;
    try {
      logger.info("SettlementRectify : Post");
      String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
      logger.info("Created by is " + Createdby);
      nfsSettlementBean.setCreatedBy(Createdby);
      logger.info("VALUES ARE " + nfsSettlementBean + " " + category + " " + stSubCategory + datepicker);
      nfsSettlementBean.setCategory(String.valueOf(category) + "_SETTLEMENT");
      HashMap<String, Object> result = this.nfsSettlementService.validateSettDifference(nfsSettlementBean);
      if (result != null && ((Boolean)result.get("result")).booleanValue())
        return "Amount is Rectified !"; 
      return result.get("msg").toString();
    } catch (Exception e) {
      logger.info("Exception in NFSSettlementController " + e);
      return "Error Occurred in getting Difference";
    } 
  }
  
  @RequestMapping(value = {"addCooperativeBank"}, method = {RequestMethod.GET})
  public ModelAndView addCooperativeBank(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** nfsFileUpload.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("nfsFileUpload GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    List<String> subcat = this.iSourceService.getSubcategories(category);
    if (category.equals("ONUS") || category.equals("AMEX") || category.equals("CARDTOCARD") || 
      category.equals("WCC"))
      display = "none"; 
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("category", category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("AddCoOperativeBank");
    logger.info("***** NFSSettlementController.nfsFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"addCooperativeBank"}, method = {RequestMethod.POST})
  @ResponseBody
  public String addCooperativeBankPost(ModelAndView modelAndView, String bankName, String accNumber, HttpServletRequest request) throws Exception {
    logger.info("***** addCooperativeBank.POST Start ****");
    Boolean checkFlag = Boolean.valueOf(this.nfsSettlementService.addCooperativeBank(bankName, accNumber));
    if (checkFlag.booleanValue())
      return "Record Added Successfully!"; 
    return "Failed to Add Record!";
  }
  
  @RequestMapping(value = {"addNodalSol"}, method = {RequestMethod.GET})
  public ModelAndView addNodalSol(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
    logger.info("***** addNodalSol.Get Start ****");
    NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
    logger.info("addNodalSol GET");
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
    modelAndView.setViewName("AddNewSol");
    logger.info("***** NFSSettlementController.addNodalSol GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"getNodalDetails"}, method = {RequestMethod.POST})
  @ResponseBody
  public List<String> getNodalDetails(String state, HttpServletRequest request) throws Exception {
    logger.info("***** getNodalDetails.Post Start ****");
    List<String> data = new ArrayList<>();
    data = this.nfsSettlementService.getNodalData(state);
    logger.info("data length " + data.size());
    return data;
  }
  
  @RequestMapping(value = {"saveNodalDetails"}, method = {RequestMethod.POST})
  @ResponseBody
  public String saveNodalDetails(@ModelAttribute("addNewSolBean") AddNewSolBean addNewSolBean, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** saveNodalDetails.Post Start ****");
    logger.info("addNewSolBean " + addNewSolBean.getSolId());
    logger.info("addNewSolBean " + addNewSolBean.getState());
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    addNewSolBean.setCreatedBy(Createdby);
    boolean insertFlag = this.nfsSettlementService.SaveNodalDetails(addNewSolBean);
    if (insertFlag)
      return "Record saved successfully!"; 
    return "Issue while saving the data";
  }
}
