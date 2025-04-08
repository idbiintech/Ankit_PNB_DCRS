package com.recon.control;



import com.recon.model.LoginBean;
import com.recon.model.RefundTTUMBean;
import com.recon.service.RefundTTUMService;
import com.recon.util.CSRFToken;
import com.recon.util.GenerateUCOTTUM;
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

@Controller
public class RefundTTUMController {
  private static final Logger logger = Logger.getLogger(com.recon.control.RefundTTUMController.class);
  
  @Autowired
  RefundTTUMService refundTTUMService;
  
  @RequestMapping(value = {"RefundTTUMMatching"}, method = {RequestMethod.GET})
  public ModelAndView RefundTTUMMatchingGet(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
    logger.info("***** RefundTTUMMatchingGet.Get Start ****");
    RefundTTUMBean refundTTUMBean = new RefundTTUMBean();
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("refundTTUMBean", refundTTUMBean);
    modelAndView.setViewName("RefundTTUMMatching");
    logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"getRefundDataCount"}, method = {RequestMethod.POST})
  @ResponseBody
  public String getRefundDataCount(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** getRefundDataCount.post Start ****");
    logger.info("getRefundDataCount POST");
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    HashMap<String, Object> output = this.refundTTUMService.getRefundCountAmount(beanObj);
    if (output != null && ((Boolean)output.get("result")).booleanValue()) {
      String[] data = output.get("msg").toString().split("\\|");
      if (data.length == 2)
        return "Refund Transaction count is " + data[0] + "\n And  Amount is " + data[1]; 
      return "Issue while getting count and amount";
    } 
    return output.get("msg").toString();
  }
  
  @RequestMapping(value = {"RefundTTUMMatching"}, method = {RequestMethod.POST})
  @ResponseBody
  public String RefundTTUMMatchingPost(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** RefundTTUMMatchingPost.post Start ****");
    logger.info("RefundTTUMMatchingPost POST");
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    HashMap<String, Object> output = this.refundTTUMService.ValidateRefundProcessing(beanObj);
    if (output != null && ((Boolean)output.get("result")).booleanValue()) {
      output = this.refundTTUMService.validateRefundTTUM(beanObj);
      if (((Boolean)output.get("result")).booleanValue()) {
        boolean executeFlag = this.refundTTUMService.runRefundTTUMMatching(beanObj);
        if (executeFlag)
          return "Processing Completed Successfully"; 
        return "Failed to process Refund Matching";
      } 
      return output.get("msg").toString();
    } 
    return output.get("msg").toString();
  }
  
  @RequestMapping(value = {"ValidateRefundMatching"}, method = {RequestMethod.POST})
  @ResponseBody
  public String ValidateRefundMatching(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** RefundTTUMMatchingPost.post Start ****");
    logger.info("RefundTTUMMatchingPost POST");
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    HashMap<String, Object> output = this.refundTTUMService.ValidateRefundProcessing(beanObj);
    if (output != null && !((Boolean)output.get("result")).booleanValue())
      return "success"; 
    return output.get("msg").toString();
  }
  
  @RequestMapping(value = {"DownloadRefundMatching"}, method = {RequestMethod.POST})
  public String DownloadRefundMatching(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj, String category, String datepicker, HttpServletRequest request, HttpSession httpSession, Model model) throws Exception {
    logger.info("***** DownloadRefundMatching.Post Start ****");
    logger.info("DownloadRefundMatching POST " + beanObj.getCategory() + " " + beanObj.getFileDate());
    List<Object> Excel_data = new ArrayList();
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    Excel_data = this.refundTTUMService.getRefundTTUMProcessData(beanObj);
    model.addAttribute("ReportName", "Refund_TTUM_Matching");
    model.addAttribute("Monthly_data", Excel_data);
    logger.info("***** RupayRefundController.DownloadRefundMatching POST End ****");
    return "GenerateRefundTTUMReports";
  }
  
  @RequestMapping(value = {"RefundTTUMGeneration"}, method = {RequestMethod.GET})
  public ModelAndView RefundTTUMGenerationGet(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
    logger.info("***** RefundTTUMGeneration.Get Start ****");
    RefundTTUMBean refundTTUMBean = new RefundTTUMBean();
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("refundTTUMBean", refundTTUMBean);
    modelAndView.setViewName("RefundTTUMGeneration");
    logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"RefundTTUMGeneration"}, method = {RequestMethod.POST})
  @ResponseBody
  public String RefundTTUMGenerationPost(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** RefundTTUMGeneration.post Start ****");
    logger.info("RefundTTUMGeneration POST");
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    HashMap<String, Object> output = this.refundTTUMService.validateRefundTTUMGeneration(beanObj);
    if (output != null && !((Boolean)output.get("result")).booleanValue()) {
      output = this.refundTTUMService.ValidateRefundProcessing(beanObj);
      if (!((Boolean)output.get("result")).booleanValue() && output.get("msg").toString().equalsIgnoreCase("Matching is already processed")) {
        boolean executeFlag = this.refundTTUMService.runRefundTTUMGeneration(beanObj);
        if (executeFlag)
          return "Processing Completed Successfully \nPlease download the reports"; 
        return "Failed to process Refund Matching";
      } 
      return output.get("msg").toString();
    } 
    return output.get("msg").toString();
  }
  
  @RequestMapping(value = {"ValidateTTUMGeneration"}, method = {RequestMethod.POST})
  @ResponseBody
  public String ValidateTTUMGeneration(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** DownloadRefundTTUM.post Start ****");
    logger.info("DownloadRefundTTUM POST");
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    HashMap<String, Object> output = this.refundTTUMService.validateRefundTTUMGeneration(beanObj);
    if (output != null && ((Boolean)output.get("result")).booleanValue())
      return "success"; 
    return "Please process ttum generation";
  }
  
  @RequestMapping(value = {"DownloadRefundTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadRefundTTUM(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj, String category, String datepicker, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession, Model model) throws Exception {
    logger.info("***** DownloadRefundTTUM.Post Start ****");
    logger.info("DownloadRefundTTUM POST " + beanObj.getCategory() + " " + beanObj.getFileDate());
    List<Object> TTUMData = new ArrayList();
    List<Object> Excel_data = new ArrayList();
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    if (beanObj.getCategory().equalsIgnoreCase("VISA")) {
      TTUMData = this.refundTTUMService.getVisaRefundTTUMData(beanObj);
      String fileName = String.valueOf(beanObj.getCategory()) + "_REFUND_TTUM.txt";
      String stPath = System.getProperty("java.io.tmpdir");
      logger.info("TEMP_DIR" + stPath);
      GenerateUCOTTUM obj = new GenerateUCOTTUM();
      stPath = obj.checkAndMakeDirectory(beanObj.getFileDate(), category);
      Excel_data.clear();
      Excel_data.add(TTUMData.get(0));
      Excel_data.add(TTUMData.get(1));
      fileName = String.valueOf(beanObj.getCategory()) + "_REFUND_TTUM_1.xls";
      obj.generateExcelTTUM(stPath, fileName, Excel_data, fileName, stPath);
      if (TTUMData.size() == 3) {
        Excel_data.clear();
        Excel_data.add(TTUMData.get(0));
        Excel_data.add(TTUMData.get(1));
        fileName = String.valueOf(beanObj.getCategory()) + "_REFUND_TTUM_2.xls";
        obj.generateExcelTTUM(stPath, fileName, Excel_data, fileName, stPath);
        obj.generateExcelTTUM(stPath, fileName, Excel_data, fileName, stPath);
        logger.info("File is created");
      } 
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
    } else {
      Excel_data = this.refundTTUMService.getRefundTTUMData(beanObj);
      String fileName = String.valueOf(beanObj.getCategory()) + "_REFUND_TTUM.txt";
      String stPath = System.getProperty("java.io.tmpdir");
      logger.info("TEMP_DIR" + stPath);
      GenerateUCOTTUM obj = new GenerateUCOTTUM();
      stPath = obj.checkAndMakeDirectory(beanObj.getFileDate(), category);
      obj.generateExcelTTUM(stPath, fileName, Excel_data, fileName, stPath);
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
  }
  
  @RequestMapping(value = {"RefundTTUMKnockoff"}, method = {RequestMethod.GET})
  public ModelAndView RefundTTUMKnockoffGet(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
    logger.info("***** RefundTTUMKnockoff.Get Start ****");
    RefundTTUMBean refundTTUMBean = new RefundTTUMBean();
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("refundTTUMBean", refundTTUMBean);
    modelAndView.setViewName("RefundTTUMKnockoff");
    logger.info("***** RefundTTUMController.RefundTTUMKnockoff GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"RefundTTUMKnockoff"}, method = {RequestMethod.POST})
  @ResponseBody
  public String RefundTTUMKnockoffPost(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj, @RequestParam("file") MultipartFile file, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** RefundTTUMKnockoff.post Start ****");
    logger.info("RefundTTUMKnockoff POST");
    logger.info("File name is " + file.getOriginalFilename());
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    HashMap<String, Object> output = this.refundTTUMService.validationForKnockoff(beanObj);
    if (output != null && ((Boolean)output.get("result")).booleanValue()) {
      if (beanObj.getOperation().equalsIgnoreCase("1")) {
        output = this.refundTTUMService.moveUnmatchedData(file, beanObj);
        if (output != null && output.get("msg") != null)
          return output.get("msg").toString(); 
        return "Issue while processing";
      } 
      output = this.refundTTUMService.knockoffData(file, beanObj);
      return output.get("msg").toString();
    } 
    return output.get("msg").toString();
  }
  
  @RequestMapping(value = {"FullRefundTTUM"}, method = {RequestMethod.GET})
  public ModelAndView FullRefundTTUMGet(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
    logger.info("***** RefundTTUMMatchingGet.Get Start ****");
    RefundTTUMBean refundTTUMBean = new RefundTTUMBean();
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("refundTTUMBean", refundTTUMBean);
    modelAndView.setViewName("searchData");
    logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"FullRefundTTUMGeneration"}, method = {RequestMethod.POST})
  @ResponseBody
  public String FullRefundTTUMGenerationPost(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** RefundTTUMGeneration.post Start ****");
    logger.info("RefundTTUMGeneration POST ");
    logger.info("File date is " + beanObj.getFileDate());
    HashMap<String, Object> output = this.refundTTUMService.checkFullTTUMProcess(beanObj);
    if (output != null && ((Boolean)output.get("result")).booleanValue()) {
      boolean executed = this.refundTTUMService.runFullRefundTTUMGeneration(beanObj);
      if (executed)
        return "TTUM Processing completed. \n Please download the reports"; 
      return "TTUM Processing failed";
    } 
    return output.get("msg").toString();
  }
  
  @RequestMapping(value = {"ValidateFullTTUMGeneration"}, method = {RequestMethod.POST})
  @ResponseBody
  public String ValidateFullTTUMGeneration(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** DownloadRefundTTUM.post Start ****");
    logger.info("DownloadRefundTTUM POST");
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    HashMap<String, Object> output = this.refundTTUMService.checkFullTTUMProcess(beanObj);
    if (output != null && !((Boolean)output.get("result")).booleanValue())
      return "success"; 
    return "Please process ttum generation";
  }
  
  @RequestMapping(value = {"DownloadFullRefundTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadFullRefundTTUM(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj, String category, String datepicker, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession, Model model) throws Exception {
    logger.info("***** DownloadRefundTTUM.Post Start ****");
    logger.info("DownloadRefundTTUM POST " + beanObj.getCategory() + " " + beanObj.getFileDate());
    List<Object> Excel_data = new ArrayList();
    List<Object> TTUMData = new ArrayList();
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    if (beanObj.getCategory().equalsIgnoreCase("VISA")) {
      TTUMData = this.refundTTUMService.getVisaFullRefundTTUMData(beanObj);
      logger.info("TTUM Size is  " + TTUMData.size());
      String fileName = String.valueOf(beanObj.getCategory()) + "_REFUND_TTUM.txt";
      String stPath = System.getProperty("java.io.tmpdir");
      logger.info("TEMP_DIR" + stPath);
      GenerateUCOTTUM obj = new GenerateUCOTTUM();
      stPath = obj.checkAndMakeDirectory(beanObj.getFileDate(), category);
      obj.generateMultipleTTUMFiles(stPath, fileName, TTUMData.size(), TTUMData);
      logger.info("File is created");
      List<String> Column_list = new ArrayList<>();
      Column_list.add("ACCOUNT_NUMBER");
      Column_list.add("PART_TRAN_TYPE");
      Column_list.add("TRANSACTION_AMOUNT");
      Column_list.add("TRANSACTION_PARTICULAR");
      Excel_data.add(Column_list);
      Excel_data.add(TTUMData.get(0));
      fileName = String.valueOf(beanObj.getCategory()) + "_REFUND_TTUM_DOMESTIC.xls";
      this.refundTTUMService.generateExcelTTUM(stPath, fileName, Excel_data, "REFUND", response);
      logger.info("File is created");
      if (TTUMData.size() == 2 && TTUMData.get(1) != null) {
        Excel_data.clear();
        Excel_data.add(Column_list);
        Excel_data.add(TTUMData.get(1));
        fileName = String.valueOf(beanObj.getCategory()) + "_REFUND_TTUM_INTERNATIONAL.xls";
        this.refundTTUMService.generateExcelTTUM(stPath, fileName, Excel_data, "REFUND", response);
        logger.info("File is created");
      } 
      File file = new File(String.valueOf(stPath) + File.separator + "EXCEL_TTUMS.zip");
      logger.info("path of zip file " + stPath + File.separator + "EXCEL_TTUMS.zip");
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
    } else {
      TTUMData = this.refundTTUMService.getFullRefundTTUMData(beanObj);
      String fileName = String.valueOf(beanObj.getCategory()) + "_REFUND_TTUM.txt";
      String stPath = System.getProperty("java.io.tmpdir");
      logger.info("TEMP_DIR" + stPath);
      GenerateUCOTTUM obj = new GenerateUCOTTUM();
      stPath = obj.checkAndMakeDirectory(beanObj.getFileDate(), category);
      obj.generateMultipleDRMTTUMFiles(stPath, fileName, TTUMData.size(), TTUMData, "RUPAY");
      logger.info("File is created");
      this.refundTTUMService.generateRupayRefund(stPath, Excel_data, "EXCEL_TTUMS", response);
      File file = new File(String.valueOf(stPath) + File.separator + "EXCEL_TTUMS.zip");
      logger.info("path of zip file " + stPath + File.separator + "EXCEL_TTUMS.zip");
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
}
