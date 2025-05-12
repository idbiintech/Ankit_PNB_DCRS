package com.recon.control;


import com.recon.model.LoginBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.RupayAdjustntFileUpService;
import com.recon.service.SettlementTTUMService;
import com.recon.util.GeneralUtil;
import com.recon.util.GenerateUCOTTUM;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
public class RupayNetwrkAdjustController {
  @Autowired
  RupayAdjustntFileUpService rupayAdjustntFileUpService;
  
  @Autowired
  SettlementTTUMService SETTLTTUMSERVICE;
  
  @Autowired
  GeneralUtil generalUtil;
  
  public static final String OUTPUT_FOLDER = String.valueOf(System.getProperty("catalina.home")) + File.separator + "FUNDING";
  
  private static final Logger logger = Logger.getLogger(com.recon.control.RupayNetwrkAdjustController.class);
  
  @RequestMapping(value = {"rupayNetworkAdjustment"}, method = {RequestMethod.GET})
  public String rupayNetwrkAdjust() {
    return "rupayNetworkAdjust";
  }
  
  @RequestMapping(value = {"QsparcNetworkAdjustment"}, method = {RequestMethod.GET})
  public String QsparcNetworkAdjustment() {
    return "qsparcNetworkAdjust";
  }
  
  @RequestMapping(value = {"rupayAdjustmentFileUpload"}, method = {RequestMethod.POST})
  @ResponseBody
  public String rupayAdjustmentFileUpload(@RequestParam("file") MultipartFile file, String fileDate, String cycle, String network, String subcate, HttpServletRequest request, HttpSession httpSession) throws IOException {
    HashMap<String, Object> output = new HashMap<>();
    System.out.println("fILE DATE IS " + fileDate);
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    System.out.println("Created by is " + Createdby);
    boolean presentmentFile = false;
    System.out.println("subcategory/................. " + subcate);
    if (file.getOriginalFilename().contains("OfflinePresentment"))
      presentmentFile = true; 
    output = this.rupayAdjustntFileUpService.validateAdjustmentUpload(fileDate, cycle, network, subcate, presentmentFile, 
        file.getOriginalFilename());
    if (output != null && ((Boolean)output.get("result")).booleanValue()) {
      int extn = file.getOriginalFilename().indexOf(".");
      System.out.println(
          "file.getOriginalFilename().substring(extn)    " + file.getOriginalFilename().substring(extn));
      if (file.getOriginalFilename().contains("IRGCS")) {
        output = this.rupayAdjustntFileUpService.rupayIntPresentFileUpload(fileDate, Createdby, cycle, network, file, 
            subcate);
      } else if (!presentmentFile) {
        output = this.rupayAdjustntFileUpService.rupayAdjustmentFileUpload(fileDate, Createdby, cycle, network, file, 
            subcate);
      } 
      if (((Boolean)output.get("result")).booleanValue())
        return "File Uploaded Successfuly \n Count is " + output.get("count"); 
      return "File Uploading Failed";
    } 
    return output.get("msg").toString();
  }
  
  @RequestMapping(value = {"rupayPresentment"}, method = {RequestMethod.GET})
  public String rupayPresentment() {
    return "rupayPresentment";
  }
  
  @RequestMapping(value = {"qsparcPresentment"}, method = {RequestMethod.GET})
  public String qsparcPresentment() {
    return "qsparcPresentment";
  }
  
  @RequestMapping(value = {"rupayPresentmentUpload"}, method = {RequestMethod.POST})
  @ResponseBody
  public String rupayPresentment(@RequestParam("file") MultipartFile file, String fileDate, String cycle, String network, String subcate, String filetype, HttpServletRequest request, HttpSession httpSession) throws IOException {
    HashMap<String, Object> output = new HashMap<>();
    System.out.println("fILE DATE IS " + fileDate);
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    System.out.println("Created by is " + Createdby);
    boolean presentmentFile = false;
    System.out.println("subcategory/................. " + cycle + subcate + filetype + " " + 
        file.getOriginalFilename() + " ne " + network);
    output = this.rupayAdjustntFileUpService.validatePresentmentUpload(fileDate, cycle, network, subcate, 
        presentmentFile, file.getOriginalFilename());
    if (output != null && ((Boolean)output.get("result")).booleanValue()) {
      output = this.rupayAdjustntFileUpService.rupayIntPresentFileUpload(fileDate, Createdby, cycle, network, file, 
          subcate);
      if (((Boolean)output.get("result")).booleanValue())
        return "File Uploaded Successfuly \n Count is " + output.get("count"); 
      return "File Uploading Failed";
    } 
    return output.get("msg").toString();
  }
  
  @RequestMapping(value = {"AdjustmentTTUM"}, method = {RequestMethod.GET})
  public String AdjustmentTTUM(ModelAndView modelAndView) {
    return "GenerateRupayAdjustmentTTUM";
  }
  
  @RequestMapping(value = {"QsparcAdjustmentTTUM"}, method = {RequestMethod.GET})
  public String QsparcAdjustmentTTUM(ModelAndView modelAndView) {
    return "GenerateQSPARCAdjustmentTTUM";
  }
  
  @RequestMapping(value = {"mastercardAdjustmentTTUM"}, method = {RequestMethod.GET})
  public String mastercardAdjustmentTTUM(ModelAndView modelAndView) {
    return "GeneratemastercardAdjustmentTTUM";
  }
  
  @RequestMapping(value = {"AdjustmentTTUMVISA"}, method = {RequestMethod.GET})
  public String AdjustmentTTUMVISA(ModelAndView modelAndView) {
    return "GenerateRupayAdjustmentTTUMVISA";
  }
  
  @RequestMapping(value = {"AdjustmentTTUMDownload"}, method = {RequestMethod.GET})
  public String AdjustmentTTUMDownload(ModelAndView modelAndView) {
    return "GenerateRupayAdjustmentTTUMDownload";
  }
  
  @RequestMapping(value = {"RupayAdjustmentProcess"}, method = {RequestMethod.POST})
  @ResponseBody
  public String NFSAdjTTUMValidation(String fileDate, String adjType, String subcate, String cate, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** RupayAdjustmentProcess.Post Start ****");
    logger.info("ADjtype is " + adjType + " filedate " + fileDate);
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    System.out.println("categoryyyyyyyyyyyyyyy is " + subcate + cate);
    System.out.println("Created by is " + Createdby);
    HashMap<String, Object> output = null;
    if (subcate.equals("DOMESTIC")) {
      output = this.rupayAdjustntFileUpService.validateAdjustmentTTUM(fileDate, adjType, cate);
    } else {
      output = this.rupayAdjustntFileUpService.validateAdjustmentTTUM(fileDate, adjType, cate);
    } 
    if (output != null && ((Boolean)output.get("result")).booleanValue()) {
      boolean executed = false;
      if (cate.equalsIgnoreCase("MASTERCARD") || cate.equalsIgnoreCase("MASTERCARD_INT")) {
        if (adjType.equalsIgnoreCase("Refund")) {
          logger.info("REFUND " + adjType);
          executed = this.rupayAdjustntFileUpService.runMCTTUMREFUND(fileDate, subcate, Createdby, cate);
        } else {
          logger.info("adjType" + adjType);
          executed = this.rupayAdjustntFileUpService.runMCTTUMREFUND(fileDate, subcate, Createdby, cate);
        } 
      } else if (adjType.equalsIgnoreCase("Refund")) {
        logger.info("REFUND " + adjType);
        executed = this.rupayAdjustntFileUpService.runAdjTTUMREFUND(fileDate, adjType, Createdby, cate);
      } else {
        logger.info("adjType" + adjType);
        executed = this.rupayAdjustntFileUpService.runAdjTTUM(fileDate, adjType, Createdby, cate);
        
        
      } 
      
      if (executed)
        return "Processing Completed \nPlease download TTUM"; 
      return "Issue while Processing";
    } 
    return output.get("msg").toString();
  }
  
  @RequestMapping(value = {"ValidateDownloadRupayAdjTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public String ValidateDownloadRupayAdjTTUM(String fileDate, String adjType, String subcate, String cate, String TTUM_TYPE, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** DownloadSettlementreport.POST Start ****");
    logger.info("DownloadAdjTTUM POST");
    HashMap<String, Object> output = null;
    return "success";
  }
  
  @RequestMapping(value = {"DownloadUnmatchedTTUMRUPAY"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadUnmatchedTTUMRUPAY(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** GenerateUnmatchedTTUM.DownloadUnmatchedTTUM post Start ****");
    logger.info("DownloadUnmatchedTTUM POST");
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    String fileName = "";
    String zipName = "";
    logger.info("DownloadAdjTTUM POST");
    if (beanObj.getCategory().contains("RUPAY")) {
      if (beanObj.getTypeOfTTUM().contains("EXCEL")) {
        List<Object> Excel_data = new ArrayList();
        List<Object> TTUMData = new ArrayList();
        List<Object> TTUMData2 = new ArrayList();
        List<Object> PBGB_TTUMData = new ArrayList();
        logger.info("Created by is " + Createdby);
        beanObj.setCreatedBy(Createdby);
        boolean executed = false;
        TTUMData = this.SETTLTTUMSERVICE.getREEFUNDTTUM(beanObj);
        System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
        fileName = "RUPAY_REFUND__TTUM.txt";
        String stPath = OUTPUT_FOLDER;
        logger.info("TEMP_DIR" + stPath);
        GenerateUCOTTUM obj = new GenerateUCOTTUM();
        stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
        obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData2, "RUPAY", beanObj);
        logger.info("File is created");
        List<String> Column_list = new ArrayList<>();
        Column_list.add("SR_NO");
        Column_list.add("GL_CODE");
        Column_list.add("PARTICULARS");
        Column_list.add("DR_COUNT");
        Column_list.add("DEBIT");
        Column_list.add("CR_COUNT");
        Column_list.add("CREDIT");
        Column_list.add("NARRATION");
        Column_list.add("FILEDATE");
        Excel_data.add(Column_list);
        Excel_data.add(TTUMData);
        System.out.println("filename in nfs ttum is " + fileName);
        fileName = "RUPAY_REFUND__TTUMS.xls";
        zipName = "RUPAY_REFUND__TTUM.zip";
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
      } else {
        beanObj.setCategory(String.valueOf(beanObj.getFileName().split("-")[1]) + "_REUND");
        List<Object> Excel_data = new ArrayList();
        List<Object> TTUMData = new ArrayList();
        logger.info("Created by is " + Createdby);
        beanObj.setCreatedBy(Createdby);
        logger.info("File Name is " + beanObj.getFileName());
        boolean executed = false;
        TTUMData = this.SETTLTTUMSERVICE.getREEFUNDTTUMTEXT(beanObj);
        System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
        fileName = "RUPAY_REFUND__TTUM.txt";
        String stPath = OUTPUT_FOLDER;
        logger.info("TEMP_DIR" + stPath);
        GenerateUCOTTUM obj = new GenerateUCOTTUM();
        stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
        obj.generateREFUNDTTUM(stPath, fileName, 1, TTUMData, "RUPAY", beanObj);
        logger.info("File is created");
        logger.info("File is created");
        zipName = "RUPAY_RUFUND__TTUM.txt";
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
    } else if (beanObj.getTypeOfTTUM().contains("EXCEL")) {
      List<Object> Excel_data = new ArrayList();
      List<Object> TTUMData = new ArrayList();
      List<Object> TTUMData2 = new ArrayList();
      List<Object> PBGB_TTUMData = new ArrayList();
      logger.info("Created by is " + Createdby);
      beanObj.setCreatedBy(Createdby);
      boolean executed = false;
      TTUMData = this.SETTLTTUMSERVICE.getREEFUNDTTUMISS(beanObj);
      System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
      fileName = "RUPAY_REFUND__TTUM.txt";
      String stPath = OUTPUT_FOLDER;
      logger.info("TEMP_DIR" + stPath);
      GenerateUCOTTUM obj = new GenerateUCOTTUM();
      stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
      obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData2, "RUPAY", beanObj);
      logger.info("File is created");
      List<String> Column_list = new ArrayList<>();
      Column_list.add("SR_NO");
      Column_list.add("GL_CODE");
      Column_list.add("PARTICULARS");
      Column_list.add("DR_COUNT");
      Column_list.add("DEBIT");
      Column_list.add("CR_COUNT");
      Column_list.add("CREDIT");
      Column_list.add("NARRATION");
      Column_list.add("FILEDATE");
      Excel_data.add(Column_list);
      Excel_data.add(TTUMData);
      System.out.println("filename in nfs ttum is " + fileName);
      fileName = "RUPAY_REFUND__TTUMS.xls";
      zipName = "RUPAY_REFUND__TTUM.zip";
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
    } else {
      beanObj.setCategory(String.valueOf(beanObj.getFileName().split("-")[1]) + "_REFUND");
      List<Object> Excel_data = new ArrayList();
      List<Object> TTUMData = new ArrayList();
      logger.info("Created by is " + Createdby);
      beanObj.setCreatedBy(Createdby);
      logger.info("File Name is " + beanObj.getFileName());
      boolean executed = false;
      TTUMData = this.SETTLTTUMSERVICE.getREEFUNDTTUMTEXTISS(beanObj);
      System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
      if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD")) {
        fileName = "MASTERCARDREFUND_TTUM_val.txt";
      } else if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD_INT")) {
        fileName = "MASTERCARDINTREFUNDTTUM_val.txt";
      } else if (beanObj.getCategory().equalsIgnoreCase("RUPAY")) {
        fileName = "RUPAYREFUNDTTUM_val.txt";
      } else if (beanObj.getCategory().equalsIgnoreCase("RUPAY_INT")) {
        fileName = "RUPAYINTREFUNDTTUM_val.txt";
      } else {
        fileName = "QSPARCREFUNDTTUM_val.txt";
      } 
      String stPath = OUTPUT_FOLDER;
      logger.info("TEMP_DIR" + stPath);
      GenerateUCOTTUM obj = new GenerateUCOTTUM();
      stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
      obj.generateREFUNDTTUM(stPath, fileName, 1, TTUMData, "RUPAY", beanObj);
      logger.info("File is created");
      logger.info("File is created");
      if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD")) {
        zipName = "MASTERCARDREFUND_TTUM_val.txt";
      } else if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD_INT")) {
        zipName = "MASTERCARDINTREFUNDTTUM_val.txt";
      } else if (beanObj.getCategory().equalsIgnoreCase("RUPAY")) {
        zipName = "RUPAYREFUNDTTUM_val.txt";
      } else if (beanObj.getCategory().equalsIgnoreCase("RUPAY_INT")) {
        zipName = "RUPAYINTREFUNDTTUM_val.txt";
      } else {
        zipName = "QSPARCREFUNDTTUM_val.txt";
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
  }
  
  @RequestMapping(value = {"DownloadRupayAdjTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadAdjTTUM(String fileDate, String adjType, String stSubCategory, String cate, String TTUM_TYPE, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** DownloadSettlementreport.POST Start ****");
    logger.info(
        "DownloadAdjTTUM POST" + cate + " " + TTUM_TYPE + " " + fileDate + " " + stSubCategory + " " + adjType);
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    String fileName = "";
    String zipName = "";
    logger.info("DownloadAdjTTUM POST");
    UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
    beanObj.setTypeOfTTUM(TTUM_TYPE);
    beanObj.setFileDate(fileDate);
    beanObj.setStSubCategory(stSubCategory);
    beanObj.setCategory(cate);
    beanObj.setAcqtypeOfTTUM(adjType);
    if (beanObj.getCategory().contains("RUPAY")) {
      if (beanObj.getTypeOfTTUM().contains("EXCEL")) {
        List<Object> Excel_data = new ArrayList();
        List<Object> TTUMData = new ArrayList();
        List<Object> TTUMData2 = new ArrayList();
        List<Object> PBGB_TTUMData = new ArrayList();
        logger.info("Created by is " + Createdby);
        beanObj.setCreatedBy(Createdby);
        boolean executed = false;
        TTUMData = this.SETTLTTUMSERVICE.getREEFUNDTTUM(beanObj);
        System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
        fileName = "RUPAY_" + beanObj.getAcqtypeOfTTUM() + "_TTUM_" + beanObj.getFileDate().replaceAll("/", "") + "_.txt";
        String stPath = OUTPUT_FOLDER;
        logger.info("TEMP_DIR" + stPath);
        GenerateUCOTTUM obj = new GenerateUCOTTUM();
        stPath = obj.checkAndMakeDirectory(beanObj.getFileDate().replaceAll("/", ""), beanObj.getStSubCategory());
        obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData2, "RUPAY", beanObj);
        logger.info("File is created");
        List<String> Column_list = new ArrayList<>();
        if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND") || beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("RUPAY_OFFLINE")) {
          Column_list.add("DISPUTE_DATE");
          Column_list.add("BANK_NAME");
          Column_list.add("CARD_NO");
          Column_list.add("ACCOUNT_NO");
          Column_list.add("TRANSECTION_DATE");
          Column_list.add("DR_CR");
          Column_list.add("AMOUNT");
          Column_list.add("RRN");
          Column_list.add("NARRATION");
        } else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
          Column_list.add("ACCOUNT_NO");
          Column_list.add("TRANSACTION_DATE");
          Column_list.add("RRN");
          Column_list.add("DR_CR");
          Column_list.add("AMOUNT");
          Column_list.add("NARRATION");
        } else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
          Column_list.add("ACCOUNT_NO");
          Column_list.add("TRANSACTION_DATE");
          Column_list.add("RRN");
          Column_list.add("DR_CR");
          Column_list.add("AMOUNT");
          Column_list.add("NARRATION");
        } else {
            Column_list.add("ACCOUNT_NO");
            Column_list.add("TRANSACTION_DATE");
            Column_list.add("RRN");
            Column_list.add("DR_CR");
            Column_list.add("AMOUNT");
            Column_list.add("NARRATION");
          }
        Excel_data.add(Column_list);
        Excel_data.add(TTUMData);
        System.out.println("filename in nfs ttum is " + fileName);
        fileName = "RUPAY_" + beanObj.getAcqtypeOfTTUM() + "_TTUMS_" + beanObj.getFileDate().replaceAll("/", "") + "_.xls";
        zipName = "RUPAY_" + beanObj.getAcqtypeOfTTUM() + "_TTUM_" + beanObj.getFileDate().replaceAll("/", "") + 
          "_VAL.zip";
        obj.generateExcelTTUM(stPath, fileName, Excel_data, "RUPAY"+ beanObj.getAcqtypeOfTTUM()+"_"+  beanObj.getFileDate().replaceAll("/", "-"), zipName);
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
      } else {
        List<Object> Excel_data = new ArrayList();
        List<Object> TTUMData = new ArrayList();
        logger.info("Created by is " + Createdby);
        beanObj.setCreatedBy(Createdby);
        logger.info("File Name is " + beanObj.getFileName());
        boolean executed = false;
        TTUMData = this.SETTLTTUMSERVICE.getREEFUNDTTUMTEXT(beanObj);
        System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
        fileName = "RUPAY" + beanObj.getAcqtypeOfTTUM() + "TTUM" + beanObj.getFileDate().replaceAll("/", "") + 
          "_VAL.txt";
        String stPath = OUTPUT_FOLDER;
        logger.info("TEMP_DIR" + stPath);
        GenerateUCOTTUM obj = new GenerateUCOTTUM();
        stPath = obj.checkAndMakeDirectory(beanObj.getFileDate().replaceAll("/", ""), beanObj.getStSubCategory());
        obj.generateREFUNDTTUM(stPath, fileName, 1, TTUMData, "RUPAY", beanObj);
        logger.info("File is created");
        logger.info("File is created");
        zipName = "RUPAY" + beanObj.getAcqtypeOfTTUM() + "TTUM" + beanObj.getFileDate().replaceAll("/", "") + 
          "_VAL.txt";
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
    } else if (beanObj.getTypeOfTTUM().contains("EXCEL")) {
      List<Object> Excel_data = new ArrayList();
      List<Object> TTUMData = new ArrayList();
      List<Object> TTUMData2 = new ArrayList();
      List<Object> PBGB_TTUMData = new ArrayList();
      logger.info("Created by is " + Createdby);
      beanObj.setCreatedBy(Createdby);
      boolean executed = false;
      TTUMData = this.SETTLTTUMSERVICE.getREEFUNDTTUMISS(beanObj);
      System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
      fileName = "RUPAYQSPARC" + beanObj.getAcqtypeOfTTUM() + "TTUM" + beanObj.getFileDate().replace("/", "") + 
        "_VAL.txt";
      String stPath = OUTPUT_FOLDER;
      logger.info("TEMP_DIR" + stPath);
      GenerateUCOTTUM obj = new GenerateUCOTTUM();
      stPath = obj.checkAndMakeDirectory(beanObj.getFileDate().replaceAll("/", ""), beanObj.getCategory());
      obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData2, "RUPAY_QSPARC", beanObj);
      logger.info("File is created");
      List<String> Column_list = new ArrayList<>();
      if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
        if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD")) {
          Column_list.add("DR_CR");
          Column_list.add("DISPUTE_DATE");
          Column_list.add("BANK_NAME");
          Column_list.add("CARD_NUMBER");
          Column_list.add("ACCOUNT_NO");
          Column_list.add("DATE_OF_TXN");
          Column_list.add("AMOUNT");
          Column_list.add("RRN");
          Column_list.add("NARRATION");
        } else {
          Column_list.add("DISPUTE_DATE");
          Column_list.add("BANK_NAME");
          Column_list.add("CARD_NO");
          Column_list.add("ACCOUNT_NO");
          Column_list.add("TRANSECTION_DATE");
          Column_list.add("DR_CR");
          Column_list.add("AMOUNT");
          Column_list.add("RRN");
          Column_list.add("NARRATION");
        } 
      } else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
        Column_list.add("ACCOUNT_NO");
        Column_list.add("TRANSACTION_DATE");
        Column_list.add("RRN");
        Column_list.add("DR_CR");
        Column_list.add("AMOUNT");
        Column_list.add("NARRATION");
      } else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
        Column_list.add("ACCOUNT_NO");
        Column_list.add("TRANSACTION_DATE");
        Column_list.add("RRN");
        Column_list.add("DR_CR");
        Column_list.add("AMOUNT");
        Column_list.add("NARRATION");
      } 
      Excel_data.add(Column_list);
      Excel_data.add(TTUMData);
      System.out.println("filename in nfs ttum is " + fileName);
      if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD")) {
        fileName = "MASTERCARD_" + beanObj.getAcqtypeOfTTUM().toUpperCase() + "_TTUM_" + 
          beanObj.getFileDate().replaceAll("/", "") + ".xls";
        zipName = "MASTERCARD_" + beanObj.getAcqtypeOfTTUM().toUpperCase() + "_TTUM_" + 
          beanObj.getFileDate().replaceAll("/", "") + ".zip";
      } else if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD_INT")) {
        fileName = "MASTERCARDINT_" + beanObj.getAcqtypeOfTTUM().toUpperCase() + "_TTUM_" + 
          beanObj.getFileDate().replaceAll("/", "") + ".xls";
        zipName = "MASTERCARDINT_" + beanObj.getAcqtypeOfTTUM().toUpperCase() + "_TTUM_" + 
          beanObj.getFileDate().replaceAll("/", "") + ".zip";
      } else {
        fileName = String.valueOf(beanObj.getCategory()) + "_" + beanObj.getAcqtypeOfTTUM().toUpperCase() + "_TTUM_" + 
          beanObj.getFileDate().replaceAll("/", "") + ".xls";
        zipName = String.valueOf(beanObj.getCategory()) + "_" + beanObj.getAcqtypeOfTTUM().toUpperCase() + "_TTUM_" + 
          beanObj.getFileDate().replaceAll("/", "") + ".zip";
      } 
      obj.generateExcelTTUM(stPath, fileName, Excel_data, beanObj.getCategory()+" "+beanObj.getAcqtypeOfTTUM()+" TTUM_"+ beanObj.getFileDate().replaceAll("/", "-"), zipName);
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
    } else {
      List<Object> Excel_data = new ArrayList();
      List<Object> TTUMData = new ArrayList();
      logger.info("Created by is " + Createdby);
      beanObj.setCreatedBy(Createdby);
      logger.info("File Name is " + beanObj.getFileName());
      boolean executed = false;
      TTUMData = this.SETTLTTUMSERVICE.getREEFUNDTTUMTEXTISS(beanObj);
      System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
      if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD")) {
        fileName = "MASTERCARD" + beanObj.getAcqtypeOfTTUM().toUpperCase() + "TTUM_val.txt";
      } else if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD_INT")) {
        fileName = "MASTERCARDINT" + beanObj.getAcqtypeOfTTUM().toUpperCase() + "TTUM_val.txt";
      } else {
        fileName = "RUPAYQSPARC" + beanObj.getAcqtypeOfTTUM().toUpperCase() + "TTUM_val.txt";
      } 
      String stPath = OUTPUT_FOLDER;
      logger.info("TEMP_DIR" + stPath);
      GenerateUCOTTUM obj = new GenerateUCOTTUM();
      stPath = obj.checkAndMakeDirectory(beanObj.getFileDate().replaceAll("/", ""), beanObj.getCategory());
      if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD")) {
        obj.generateREFUNDTTUM(stPath, fileName, 1, TTUMData, "MASTERCARD", beanObj);
      } else {
        obj.generateREFUNDTTUM(stPath, fileName, 1, TTUMData, "RUPAY_QSPARC", beanObj);
      } 
      logger.info("File is created");
      logger.info("File is created");
      if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD")) {
        zipName = "MASTERCARD" + beanObj.getAcqtypeOfTTUM().toUpperCase() + "TTUM_val.txt";
      } else if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD_INT")) {
        zipName = "MASTERCARDINT" + beanObj.getAcqtypeOfTTUM().toUpperCase() + "TTUM_val.txt";
      } else {
        zipName = "RUPAYQSPARC" + beanObj.getAcqtypeOfTTUM().toUpperCase() + "TTUM_val.txt";
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
  }
}
