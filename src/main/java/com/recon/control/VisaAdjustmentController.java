package com.recon.control;


import com.recon.control.RupayNetwrkAdjustController;
import com.recon.model.LoginBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.ISourceService;
import com.recon.service.RupayAdjustntFileUpService;
import com.recon.service.SettlementTTUMService;
import com.recon.util.GeneralUtil;
import com.recon.util.GenerateUCOTTUM;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.ParseException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VisaAdjustmentController {
  @Autowired
  GeneralUtil generalUtil;
  
  @Autowired
  ISourceService iSourceService;
  
  @Autowired
  RupayAdjustntFileUpService rupayAdjustntFileUpService;
  
  @Autowired
  SettlementTTUMService SETTLTTUMSERVICE;
  
  public static final String OUTPUT_FOLDER = String.valueOf(System.getProperty("catalina.home")) + File.separator + "FUNDING";
  
  private static final Logger logger = Logger.getLogger(RupayNetwrkAdjustController.class);
  
  @RequestMapping(value = {"DownloadAdjTTUMVISA"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadAdjTTUMVISA(String fileDate, String adjType, String stSubCategory, String TTUM_TYPE, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** DownloadAdjTTUMVISA.POST Start ****");
    logger.info(
        "DownloadAdjTTUMVISA POST " + TTUM_TYPE + " " + fileDate + " " + stSubCategory + " " + adjType);
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    String fileName = "";
    String zipName = "";
    logger.info("DownloadAdjTTUM POST");
    UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
    beanObj.setTypeOfTTUM(TTUM_TYPE);
    beanObj.setFileDate(fileDate);
    beanObj.setStSubCategory(stSubCategory);
    beanObj.setAcqtypeOfTTUM(adjType);
    if (beanObj.getStSubCategory().contains("DOMESTIC")) {
      if (beanObj.getTypeOfTTUM().contains("EXCEL")) {
        List<Object> Excel_data = new ArrayList();
        List<Object> TTUMData = new ArrayList();
        List<Object> TTUMData2 = new ArrayList();
        List<Object> PBGB_TTUMData = new ArrayList();
        logger.info("Created by is " + Createdby);
        beanObj.setCreatedBy(Createdby);
        boolean executed = false;
        TTUMData = this.SETTLTTUMSERVICE.getREEFUNDTTUMVISA(beanObj);
        System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
        fileName = "VISA_" + beanObj.getAcqtypeOfTTUM() + "_TTUM_" + beanObj.getFileDate() + "_.txt";
        String stPath = OUTPUT_FOLDER;
        logger.info("TEMP_DIR" + stPath);
        GenerateUCOTTUM obj = new GenerateUCOTTUM();
        stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
        obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData2, "VISA", beanObj);
        logger.info("File is created");
        List<String> Column_list = new ArrayList<>();
        if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
          Column_list.add("DR_CR");
          Column_list.add("DISPUTE_DATE");
          Column_list.add("BANK_NAME");
          Column_list.add("CARD_NO");
          Column_list.add("ACCOUNT_NO");
          Column_list.add("DATE_OF_TXN");
          Column_list.add("Amount");
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
        } 
        Excel_data.add(Column_list);
        Excel_data.add(TTUMData);
        System.out.println("filename in nfs ttum is " + fileName);
        fileName = "VISA_" + beanObj.getAcqtypeOfTTUM() + "_TTUMS_" + beanObj.getFileDate() + "_.xls";
        zipName = "VISA_" + beanObj.getAcqtypeOfTTUM() + "_TTUM_" + beanObj.getFileDate() + "_VAL.zip";
        obj.generateExcelTTUM(stPath, fileName, Excel_data, "VISA TTUM_"+  beanObj.getFileDate().replaceAll("/", "-"), zipName);
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
        TTUMData = this.SETTLTTUMSERVICE.getREEFUNDTTUMTEXTVISA(beanObj);
        System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
        fileName = "VISA_" + beanObj.getAcqtypeOfTTUM() + "_TTUM_" + beanObj.getFileDate() + "_VAL.txt";
        String stPath = OUTPUT_FOLDER;
        logger.info("TEMP_DIR" + stPath);
        GenerateUCOTTUM obj = new GenerateUCOTTUM();
        stPath = obj.checkAndMakeDirectory(beanObj.getFileDate(), beanObj.getCategory());
        obj.generateREFUNDTTUM(stPath, fileName, 1, TTUMData, "VISA", beanObj);
        logger.info("File is created");
        logger.info("File is created");
        zipName = "VISA_" + beanObj.getAcqtypeOfTTUM() + "_TTUM_" + beanObj.getFileDate() + "_VAL.txt";
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
      TTUMData = this.SETTLTTUMSERVICE.getREEFUNDTTUMVISAINT(beanObj);
      System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
      fileName = "VISA_INT_" + beanObj.getAcqtypeOfTTUM() + "_TTUM_" + beanObj.getFileDate() + 
        "_VAL.txt";
      String stPath = OUTPUT_FOLDER;
      logger.info("TEMP_DIR" + stPath);
      GenerateUCOTTUM obj = new GenerateUCOTTUM();
      stPath = obj.checkAndMakeDirectory(beanObj.getFileDate(), beanObj.getCategory());
      obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData2, "VISA_INT", beanObj);
      logger.info("File is created");
      List<String> Column_list = new ArrayList<>();
      if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
        Column_list.add("DR_CR");
        Column_list.add("DISPUTE_DATE");
        Column_list.add("BANK_NAME");
        Column_list.add("CARD_NO");
        Column_list.add("ACCOUNT_NO");
        Column_list.add("DATE_OF_TXN");
        Column_list.add("Amount");
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
      } 
      Excel_data.add(Column_list);
      Excel_data.add(TTUMData);
      System.out.println("filename in nfs ttum is " + fileName);
      fileName = "VISA_INT_" + beanObj.getAcqtypeOfTTUM() + "_TTUMS_" + beanObj.getFileDate() + ".xls";
      zipName = "VISA_INT_" + beanObj.getAcqtypeOfTTUM() + "_TTUM_" + beanObj.getFileDate() + ".zip";
      obj.generateExcelTTUM(stPath, fileName, Excel_data, "VISA_INT", zipName);
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
      TTUMData = this.SETTLTTUMSERVICE.getREEFUNDTTUMTEXTVISAINT(beanObj);
      System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
      fileName = "VISA_INT_" + beanObj.getAcqtypeOfTTUM() + "_TTUM.txt";
      String stPath = OUTPUT_FOLDER;
      logger.info("TEMP_DIR" + stPath);
      GenerateUCOTTUM obj = new GenerateUCOTTUM();
      stPath = obj.checkAndMakeDirectory(beanObj.getFileDate(), beanObj.getCategory());
      obj.generateREFUNDTTUM(stPath, fileName, 1, TTUMData, "VISA_INT", beanObj);
      logger.info("File is created");
      logger.info("File is created");
      zipName = "VISA_INT_" + beanObj.getAcqtypeOfTTUM() + "_TTUM.txt";
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
  
  @RequestMapping(value = {"VisaAdjustmentProcess"}, method = {RequestMethod.POST})
  @ResponseBody
  public String VisaAdjustmentProcess(String fileDate, String adjType, String subcate, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** VisaAdjustmentProcess.Post Start ****");
    logger.info("ADjtype is " + adjType + " filedate " + fileDate);
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    System.out.println("categoryyyyyyyyyyyyyyy is " + subcate);
    System.out.println("Created by is " + Createdby);
    HashMap<String, Object> output = null;
    if (subcate.equals("DOMESTIC")) {
      output = this.rupayAdjustntFileUpService.validateAdjustmentTTUMVISA(fileDate, adjType, subcate);
    } else {
      output = this.rupayAdjustntFileUpService.validateAdjustmentTTUMVISA(fileDate, adjType, subcate);
    } 
    if (output != null && ((Boolean)output.get("result")).booleanValue()) {
      boolean executed = false;
      if (adjType.equalsIgnoreCase("Refund")) {
        logger.info("REFUND " + adjType);
        executed = this.rupayAdjustntFileUpService.runAdjTTUMREFUNDVISA(fileDate, adjType, Createdby, subcate);
      } else {
        logger.info("adjType" + adjType);
        executed = this.rupayAdjustntFileUpService.runAdjTTUMREFUNDVISA(fileDate, adjType, Createdby, subcate);
      } 
      if (executed)
        return "Processing Completed \nPlease download TTUM"; 
      return "Issue while Processing";
    } 
    return output.get("msg").toString();
  }
  
  @RequestMapping(value = {"ValidateDownloadVISAAdjTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public String ValidateDownloadVISAAdjTTUM(String fileDate, String adjType, String subcate, String TTUM_TYPE, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
    logger.info("***** DownloadSettlementreport.POST Start ****");
    logger.info("DownloadAdjTTUM POST");
    String passdate = this.generalUtil.SkDateFunction(fileDate);
    System.out.println("cate is" + fileDate);
    System.out.println("filedate is" + fileDate);
    System.out.println("adj type is " + adjType);
    List<Object> Excel_data = new ArrayList();
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    HashMap<String, Object> output = null;
    if (subcate.contains("DOMESTIC")) {
      output = this.rupayAdjustntFileUpService.validateAdjustmentTTUMProcessVISA(fileDate, adjType, subcate, 
          TTUM_TYPE);
    } else {
      output = this.rupayAdjustntFileUpService.validateAdjustmentTTUMProcessVISA(fileDate, adjType, subcate, 
          TTUM_TYPE);
    } 
    if (output != null && ((Boolean)output.get("result")).booleanValue())
      return "success"; 
    return "Adjustment TTUM is not processed.\n Please process TTUM";
  }
  
  @RequestMapping(value = {"rollbackVISAADJ"}, method = {RequestMethod.POST})
  @ResponseBody
  public String rollbackVISAADJ(@RequestParam("fileDate") String fileDate, @RequestParam("subcategory") String subcategory, @RequestParam("ADJTYPE") String ADJTYPE) throws ParseException, Exception {
    logger.info("rollbackVISAADJ " + fileDate + " " + subcategory + " " + ADJTYPE);
    if (subcategory.contains("DOMESTIC")) {
      String str = this.SETTLTTUMSERVICE.rollBackRupayADJVISA(fileDate, subcategory, ADJTYPE);
      return str;
    } 
    String value = this.SETTLTTUMSERVICE.rollBackRupayADJVISA(fileDate, subcategory, ADJTYPE);
    return value;
  }
}
