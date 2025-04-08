package com.recon.control;


import com.recon.model.LoginBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.ISourceService;
import com.recon.service.SettlementTTUMService;
import com.recon.util.FileDetailsJson;
import com.recon.util.GenerateUCOTTUM;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ExtractRawDataController {
  public static final String OUTPUT_FOLDER = String.valueOf(System.getProperty("catalina.home")) + File.separator + "FUNDING";
  
  private static final Logger logger = Logger.getLogger(com.recon.control.ExtractRawDataController.class);
  
  private static final String ERROR_MSG = "error_msg";
  
  @Autowired
  SettlementTTUMService SETTLTTUMSERVICE;
  
  @Autowired
  ISourceService iSourceService;
  
  @RequestMapping(value = {"ExtractVisaRawdata"}, method = {RequestMethod.GET})
  public ModelAndView ExtractVisaRawdata(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** ExtractVisaRawdata Start Get method  ****");
    modelAndView.addObject("category", category);
    String display = "";
    List<String> subcat = new ArrayList<>();
    UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
    logger.info("in GetHeaderList" + category);
    beanObj.setCategory(category);
    subcat = this.iSourceService.getSubcategories(category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("display", display);
    modelAndView.addObject("unmatchedTTUMBean", beanObj);
    modelAndView.setViewName("ExtractVisaRawdata");
    logger.info("***** ExtractVisaRawdata.ExtractVisaRawdata GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"ExtractSwitchRawdata"}, method = {RequestMethod.GET})
  public ModelAndView ExtractSwitchRawdata(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** ExtractSwitchRawdata Start Get method  ****");
    modelAndView.addObject("category", category);
    String display = "";
    List<String> subcat = new ArrayList<>();
    UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
    logger.info("in GetHeaderList" + category);
    beanObj.setCategory(category);
    subcat = this.iSourceService.getSubcategories(category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("display", display);
    modelAndView.addObject("unmatchedTTUMBean", beanObj);
    modelAndView.setViewName("ExtractSwitchRawdata");
    logger.info("***** ExtractVisaRawdata.ExtractSwitchRawdata GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"ExtractCbsRawdata"}, method = {RequestMethod.GET})
  public ModelAndView ExtractCbsRawdata(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** ExtractCbsRawdata Start Get method  ****");
    modelAndView.addObject("category", category);
    String display = "";
    List<String> subcat = new ArrayList<>();
    UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
    logger.info("in GetHeaderList" + category);
    beanObj.setCategory(category);
    subcat = this.iSourceService.getSubcategories(category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("display", display);
    modelAndView.addObject("unmatchedTTUMBean", beanObj);
    modelAndView.setViewName("ExtractCbsRawdata");
    logger.info("***** ExtractCbsRawdata.ExtractCbsRawdata GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"checkExtractVisaRawdata"}, method = {RequestMethod.POST})
  @ResponseBody
  public String checkExtractVisaRawdata(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response, HttpServletRequest request) {
    return "success";
  }
  
  @RequestMapping(value = {"DownloadExtractVisaRawdata"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadExtractVisaRawdata(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** DownloadExtractVisaRawdata.DownloadExtractVisaRawdata post Start ****" + 
        beanObj.getTTUMCategory());
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    String fileName = "", zipName = "";
    List<Object> Excel_data = new ArrayList();
    List<Object> TTUMData = new ArrayList();
    List<Object> TTUMData2 = new ArrayList();
    List<Object> PBGB_TTUMData = new ArrayList();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    logger.info("File Name is " + beanObj.getFileName());
    boolean executed = false;
    TTUMData = this.SETTLTTUMSERVICE.ExtractVisaRawdata(beanObj);
    String stPath = OUTPUT_FOLDER;
    logger.info("TEMP_DIR" + stPath);
    GenerateUCOTTUM obj = new GenerateUCOTTUM();
    stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", ""), beanObj.getCategory());
    logger.info("File is created");
    List<String> Column_list = new ArrayList<>();
    Map<String, String> table_Data = new HashMap<>();
    Column_list.add("TC");
    Column_list.add("TCR_CODE");
    Column_list.add("CARD_NUMBER ");
    Column_list.add("SOURCE_AMOUNT");
    Column_list.add("AUTHORIZATION_CODE");
    Column_list.add("DCRS_SEQ_NO");
    Column_list.add("TRACE");
    Column_list.add("REFERENCE_NUMBER");
    Column_list.add("RESPONSE_CODE");
    Column_list.add("PART_ID");
    Column_list.add("CREATEDDATE");
    Column_list.add("CREATEDBY");
    Column_list.add("FILEDATE");
    Column_list.add("REQ_MSGTYPE");
    Column_list.add("FLOOR_LIMIT_INDI");
    Column_list.add("ARN");
    Column_list.add("ACQUIRER_BUSI_ID");
    Column_list.add("PURCHASE_DATE");
    Column_list.add("DESTINATION_AMOUNT");
    Column_list.add("DESTINATION_CURR_CODE");
    Column_list.add("SOURCE_CURR_CODE");
    Column_list.add("MERCHANT_NAME");
    Column_list.add("MERCHANT_CITY");
    Column_list.add("MERCHANT_COUNTRY_CODE");
    Column_list.add("MERCHANT_CATEGORY_CODE");
    Column_list.add("MERCHANT_ZIP_CODE");
    Column_list.add("USAGE_CODE");
    Column_list.add("SETTLEMENT_FLAG");
    Column_list.add("AUTH_CHARA_IND");
    Column_list.add("POS_TERMINAL_CAPABILITY");
    Column_list.add("CARDHOLDER_ID_METHOD");
    Column_list.add("COLLECTION_ONLY_FLAG");
    Column_list.add("POS_ENTRY_MODE");
    Column_list.add("CENTRAL_PROCESS_DATE");
    Column_list.add("REIMBURSEMENT_ATTR");
    Column_list.add("DESTINATION_BIN");
    Column_list.add("SOURCE_BIN");
    Column_list.add("REASON_CODE");
    Column_list.add("COUNTRY_CODE");
    Column_list.add("EVENT_DATE");
    Column_list.add("MESSAGE_TEXT");
    Column_list.add("TRANSAC_IDENTIFIER");
    Column_list.add("TRAN_ID");
    Column_list.add("FPAN");
    Column_list.add("FILENAME");
    Column_list.add("DCRS_REMARKS");
    Excel_data.add(Column_list);
    Excel_data.add(TTUMData);
    System.out.println("filename in nfs ttum is " + fileName);
    if (beanObj.getTTUMCategory().equalsIgnoreCase("ACQUIRER")) {
      fileName = "ACQEXTRACTVISARAWDATA" + beanObj.getLocalDate().replaceAll("/", "") + ".xls";
      zipName = "ACQEXTRACTVISARAWDATA" + beanObj.getLocalDate().replaceAll("/", "") + ".zip";
    } else {
      fileName = "ISSEXTRACTVISARAWDATA" + beanObj.getLocalDate().replaceAll("/", "") + ".xls";
      zipName = "ISSEXTRACTVISARAWDATA" + beanObj.getLocalDate().replaceAll("/", "") + ".zip";
    } 
    obj.generateExcelTTUM(stPath, fileName, Excel_data, "VISA_" + beanObj.getLocalDate().replaceAll("/", ""), 
        zipName);
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
  
  @RequestMapping(value = {"DownloadExtractSwitchRawdata"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadExtractSwitchRawdata(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** DownloadExtractVisaRawdata.DownloadExtractVisaRawdata post Start ****" + 
        beanObj.getTTUMCategory());
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    String fileName = "", zipName = "";
    List<Object> Excel_data = new ArrayList();
    List<Object> TTUMData = new ArrayList();
    List<Object> TTUMData2 = new ArrayList();
    List<Object> PBGB_TTUMData = new ArrayList();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    logger.info("File Name is " + beanObj.getFileName());
    boolean executed = false;
    TTUMData = this.SETTLTTUMSERVICE.ExtractSwitchRawdata(beanObj);
    String stPath = OUTPUT_FOLDER;
    logger.info("TEMP_DIR" + stPath);
    GenerateUCOTTUM obj = new GenerateUCOTTUM();
    stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", ""), beanObj.getCategory());
    logger.info("File is created");
    List<String> Column_list = new ArrayList<>();
    Map<String, String> table_Data = new HashMap<>();
    Column_list.add("TC");
    Column_list.add("TCR_CODE");
    Column_list.add("CARD_NUMBER ");
    Column_list.add("SOURCE_AMOUNT");
    Column_list.add("AUTHORIZATION_CODE");
    Column_list.add("DCRS_SEQ_NO");
    Column_list.add("TRACE");
    Column_list.add("REFERENCE_NUMBER");
    Column_list.add("RESPONSE_CODE");
    Column_list.add("PART_ID");
    Column_list.add("CREATEDDATE");
    Column_list.add("CREATEDBY");
    Column_list.add("FILEDATE");
    Column_list.add("REQ_MSGTYPE");
    Column_list.add("FLOOR_LIMIT_INDI");
    Column_list.add("ARN");
    Column_list.add("ACQUIRER_BUSI_ID");
    Column_list.add("PURCHASE_DATE");
    Column_list.add("DESTINATION_AMOUNT");
    Column_list.add("DESTINATION_CURR_CODE");
    Column_list.add("SOURCE_CURR_CODE");
    Column_list.add("MERCHANT_NAME");
    Column_list.add("MERCHANT_CITY");
    Column_list.add("MERCHANT_COUNTRY_CODE");
    Column_list.add("MERCHANT_CATEGORY_CODE");
    Column_list.add("MERCHANT_ZIP_CODE");
    Column_list.add("USAGE_CODE");
    Column_list.add("SETTLEMENT_FLAG");
    Column_list.add("AUTH_CHARA_IND");
    Column_list.add("POS_TERMINAL_CAPABILITY");
    Column_list.add("CARDHOLDER_ID_METHOD");
    Column_list.add("COLLECTION_ONLY_FLAG");
    Column_list.add("POS_ENTRY_MODE");
    Column_list.add("CENTRAL_PROCESS_DATE");
    Column_list.add("REIMBURSEMENT_ATTR");
    Column_list.add("DESTINATION_BIN");
    Column_list.add("SOURCE_BIN");
    Column_list.add("REASON_CODE");
    Column_list.add("COUNTRY_CODE");
    Column_list.add("EVENT_DATE");
    Column_list.add("MESSAGE_TEXT");
    Column_list.add("TRANSAC_IDENTIFIER");
    Column_list.add("TRAN_ID");
    Column_list.add("FPAN");
    Column_list.add("FILENAME");
    Column_list.add("DCRS_REMARKS");
    Excel_data.add(Column_list);
    Excel_data.add(TTUMData);
    System.out.println("filename in nfs ttum is " + fileName);
    if (beanObj.getTTUMCategory().equalsIgnoreCase("PTLF")) {
      fileName = "PTLFSWITCHRAWDATA" + beanObj.getLocalDate().replaceAll("/", "") + ".xls";
      zipName = "PTLFSWITCHRAWDATA" + beanObj.getLocalDate().replaceAll("/", "") + ".zip";
    } else {
      fileName = "TLFSWITCHRAWDATA" + beanObj.getLocalDate().replaceAll("/", "") + ".xls";
      zipName = "TLFSWITCHRAWDATA" + beanObj.getLocalDate().replaceAll("/", "") + ".zip";
    } 
    obj.generateExcelTTUM(stPath, fileName, Excel_data, "VISA_" + beanObj.getLocalDate().replaceAll("/", ""), 
        zipName);
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
  
  @RequestMapping(value = {"DownloadExtractCbsRawdata"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadExtractCbsRawdata(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** DownloadExtractCbsRawdata.DownloadExtractCbsRawdata post Start ****" + 
        beanObj.getTTUMCategory());
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    String fileName = "", zipName = "";
    List<Object> Excel_data = new ArrayList();
    List<Object> TTUMData = new ArrayList();
    List<Object> TTUMData2 = new ArrayList();
    List<Object> PBGB_TTUMData = new ArrayList();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    logger.info("File Name is " + beanObj.getFileName());
    boolean executed = false;
    TTUMData = this.SETTLTTUMSERVICE.ExtractCbsRawdata(beanObj);
    String stPath = OUTPUT_FOLDER;
    logger.info("TEMP_DIR" + stPath);
    GenerateUCOTTUM obj = new GenerateUCOTTUM();
    stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", ""), beanObj.getCategory());
    logger.info("File is created");
    List<String> Column_list = new ArrayList<>();
    Map<String, String> table_Data = new HashMap<>();
    if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139926") || 
      beanObj.getTTUMCategory().equalsIgnoreCase("5165005131355") || 
      beanObj.getTTUMCategory().equalsIgnoreCase("5165005132550") || 
      beanObj.getTTUMCategory().equalsIgnoreCase("5165005132549")) {
      Column_list.add("tran_particular");
      Column_list.add("tran_amount");
      Column_list.add("tran_date");
      Column_list.add("rcre_time");
      Column_list.add("init_sol_id");
      Column_list.add("value_date");
      Column_list.add("part_tran_type");
      Column_list.add("foracid");
      Column_list.add("tran_remarks");
      Column_list.add("cummulative_balance");
      Column_list.add("tran_particular1");
      Column_list.add("narration");
      Column_list.add("sol_id");
      Column_list.add("tran_id");
      Column_list.add("tran_remarks1");
      Column_list.add("posted_time");
      Column_list.add("approval_code");
      Column_list.add("FILEDATE");
      Column_list.add("FILENAME");
      Column_list.add("CREATEDDATE");
      Column_list.add("narration1");
      Column_list.add("flag");
    } else if (beanObj.getTTUMCategory().equalsIgnoreCase("FN100")) {
      Column_list.add("Atm_Id");
      Column_list.add("Ref_Num");
      Column_list.add("Card_Number");
      Column_list.add("Tran_Amt");
      Column_list.add("Tran_Date");
      Column_list.add("Rcre_Time");
      Column_list.add("Init_Sol_Id");
      Column_list.add("Value_Date");
      Column_list.add("Part_Tran_Type");
      Column_list.add("Foracid");
      Column_list.add("Bank_Id");
      Column_list.add("Cummulative_Balance");
      Column_list.add("Entry_User");
      Column_list.add("Tran_Particular");
      Column_list.add("Tran_Id");
      Column_list.add("Contra_Foracid");
      Column_list.add("Tran_Remark");
      Column_list.add("Posted_Time");
      Column_list.add("Verified_Time");
      Column_list.add("Tran_Part_1");
      Column_list.add("Tran_Part_2");
      Column_list.add("FILEDATE");
      Column_list.add("FILENAME");
      Column_list.add("CREATEDDATE");
    } else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139924")) {
      Column_list.add("tran_particular1");
      Column_list.add("tran_particular2");
      Column_list.add("tran_particular3");
      Column_list.add("tran_amount");
      Column_list.add("tran_date");
      Column_list.add("rcre_time");
      Column_list.add("init_sol_id");
      Column_list.add("value_date");
      Column_list.add("part_tran_type");
      Column_list.add("foracid");
      Column_list.add("tran_remarks");
      Column_list.add("cummulative_balance");
      Column_list.add("tran_particular");
      Column_list.add("narration");
      Column_list.add("sol_id");
      Column_list.add("tran_id");
      Column_list.add("tran_remarks1");
      Column_list.add("posted_time");
      Column_list.add("verified_time");
      Column_list.add("FILEDATE");
      Column_list.add("FILENAME");
      Column_list.add("CREATEDDATE");
    } else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139948") || 
      beanObj.getTTUMCategory().equalsIgnoreCase("5165005811354")) {
      Column_list.add("tran_date");
      Column_list.add("narration");
      Column_list.add("dr_amount");
      Column_list.add("cr_amount");
      Column_list.add("GL_BALANCE");
      Column_list.add("tran_date2");
      Column_list.add("dummy");
      Column_list.add("sys_man");
      Column_list.add("tran_id");
      Column_list.add("acc_number");
      Column_list.add("narration2");
      Column_list.add("FILEDATE");
      Column_list.add("FILENAME");
      Column_list.add("CREATEDDATE");
    } else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139949")) {
      Column_list.add("tran_particular1");
      Column_list.add("seq_num");
      Column_list.add("card_no");
      Column_list.add("tran_amount");
      Column_list.add("value_date");
      Column_list.add("rcre_time");
      Column_list.add("sol_id");
      Column_list.add("tran_date");
      Column_list.add("part_tran_type");
      Column_list.add("atm_gl");
      Column_list.add("tran_remarks");
      Column_list.add("cummulative_balance");
      Column_list.add("tran_particular");
      Column_list.add("narration");
      Column_list.add("tran_id");
      Column_list.add("uid");
      Column_list.add("tran_remarks1");
      Column_list.add("posted_time");
      Column_list.add("verified_time");
      Column_list.add("approval_code");
      Column_list.add("reference_number");
      Column_list.add("FILEDATE");
      Column_list.add("FILENAME");
      Column_list.add("CREATEDDATE");
    } else {
      Column_list.add("tran_particular1");
      Column_list.add("seq_num");
      Column_list.add("card_no");
      Column_list.add("tran_amount");
      Column_list.add("value_date");
      Column_list.add("rcre_time");
      Column_list.add("sol_id");
      Column_list.add("tran_date");
      Column_list.add("part_tran_type");
      Column_list.add("atm_gl");
      Column_list.add("tran_remarks");
      Column_list.add("cummulative_balance");
      Column_list.add("tran_particular");
      Column_list.add("narration");
      Column_list.add("tran_id");
      Column_list.add("uid");
      Column_list.add("tran_remarks1");
      Column_list.add("posted_time");
      Column_list.add("verified_time");
      Column_list.add("approval_code");
      Column_list.add("FILEDATE");
      Column_list.add("FILENAME");
      Column_list.add("CREATEDDATE");
      Column_list.add("reference_number");
      Column_list.add("sol_balance");
      Column_list.add("flag");
    } 
    Excel_data.add(Column_list);
    Excel_data.add(TTUMData);
    System.out.println("filename in nfs ttum is " + fileName);
    if (beanObj.getTTUMCategory().equalsIgnoreCase("PTLF")) {
      fileName = String.valueOf(beanObj.getTTUMCategory()) + "CBS" + beanObj.getLocalDate().replaceAll("/", "") + ".xls";
      zipName = String.valueOf(beanObj.getTTUMCategory()) + "CBS" + beanObj.getLocalDate().replaceAll("/", "") + ".zip";
    } else {
      fileName = String.valueOf(beanObj.getTTUMCategory()) + "CBS" + beanObj.getLocalDate().replaceAll("/", "") + ".xls";
      zipName = String.valueOf(beanObj.getTTUMCategory()) + "CBS" + beanObj.getLocalDate().replaceAll("/", "") + ".zip";
    } 
    obj.generateExcelTTUM(stPath, fileName, Excel_data, "VISA_" + beanObj.getLocalDate().replaceAll("/", ""), 
        zipName);
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
  
  @RequestMapping(value = {"ExtractNFSRawdata"}, method = {RequestMethod.GET})
  public ModelAndView ExtractNFSRawdata(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** ExtractNFSRawdata Start Get method  ****");
    modelAndView.addObject("category", category);
    String display = "";
    List<String> subcat = new ArrayList<>();
    UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
    logger.info("in GetHeaderList" + category);
    beanObj.setCategory(category);
    subcat = this.iSourceService.getSubcategories(category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("display", display);
    modelAndView.addObject("unmatchedTTUMBean", beanObj);
    modelAndView.setViewName("ExtractNFSRawdata");
    logger.info("***** ExtractNFSRawdata.ExtractNFSRawdata GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"ExtractJCBDFSICDRawdata"}, method = {RequestMethod.GET})
  public ModelAndView ExtractJCBDFSICDRawdata(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** ExtractJCBDFSICDRawdata Start Get method  ****");
    modelAndView.addObject("category", category);
    String display = "";
    List<String> subcat = new ArrayList<>();
    UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
    logger.info("in GetHeaderList" + category);
    beanObj.setCategory(category);
    subcat = this.iSourceService.getSubcategories(category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("display", display);
    modelAndView.addObject("unmatchedTTUMBean", beanObj);
    modelAndView.setViewName("ExtractJCBDFSICDRawdata");
    logger.info("***** ExtractJCBDFSICDRawdata.ExtractJCBDFSICDRawdata GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"checkExtractNFSRawdata"}, method = {RequestMethod.POST})
  @ResponseBody
  public String checkExtractNFSRawdata(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response, HttpServletRequest request) {
    return "success";
  }
  
  @RequestMapping(value = {"DownloadExtractNFSRawdata"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadExtractNFSRawdata(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** DownloadExtractVisaRawdata.DownloadExtractVisaRawdata post Start ****" + 
        beanObj.getTTUMCategory() + beanObj.getCategory() + beanObj.getCycle());
    beanObj.setCategory(beanObj.getFileName());
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    String fileName = "", zipName = "";
    List<Object> Excel_data = new ArrayList();
    List<Object> TTUMData = new ArrayList();
    List<Object> TTUMData2 = new ArrayList();
    List<Object> PBGB_TTUMData = new ArrayList();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    logger.info("File Name is " + beanObj.getFileName() + beanObj.getCategory());
    boolean executed = false;
    TTUMData = this.SETTLTTUMSERVICE.ExtractNFSRawdata(beanObj);
    String stPath = OUTPUT_FOLDER;
    logger.info("TEMP_DIR" + stPath);
    GenerateUCOTTUM obj = new GenerateUCOTTUM();
    stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", ""), beanObj.getCategory());
    logger.info("File is created");
    List<String> Column_list = new ArrayList<>();
    Map<String, String> table_Data = new HashMap<>();
    if (beanObj.getCategory().contains("NFS") || beanObj.getCategory().contains("DFS") || 
      beanObj.getCategory().contains("JCB")) {
      if (beanObj.getTTUMCategory().contains("ACQUIRER")) {
        Column_list.add("PARTICIPANT_ID");
        Column_list.add("TRANSACTION_TYPE");
        Column_list.add("FROM_ACCOUNT_TYPE");
        Column_list.add("TO_ACCOUNT_TYPE");
        Column_list.add("TXN_SERIAL_NO");
        Column_list.add("RESPONSE_CODE");
        Column_list.add("PAN_NUMBER");
        Column_list.add("MEMBER_NUMBER");
        Column_list.add("APPROVAL_NUMBER");
        Column_list.add("SYS_TRACE_AUDIT_NO");
        Column_list.add("TRANSACTION_DATE");
        Column_list.add("TRANSACTION_TIME");
        Column_list.add("MERCHANT_CATEGORY_CD");
        Column_list.add("CARD_ACC_SETTLE_DT");
        Column_list.add("CARD_ACC_ID");
        Column_list.add("CARD_ACC_TERMINAL_ID");
        Column_list.add("CARD_ACC_TERMINAL_LOC");
        Column_list.add("ACQUIRER_ID");
        Column_list.add("ACQ_SETTLE_DATE");
        Column_list.add("TXN_CURRENCY_CODE");
        Column_list.add("TXN_AMOUNT");
        Column_list.add("ACTUAL_TXN_AMT");
        Column_list.add("TXN_ACTIVITY_FEE");
        Column_list.add("ACQ_SETTLE_CURRENCY_CD");
        Column_list.add("ACQ_SETTLE_AMNT");
        Column_list.add("ACQ_SETTLE_FEE");
        Column_list.add("ACQ_SETTLE_PROCESS_FEE");
        Column_list.add("TXN_ACQ_CONV_RATE");
        Column_list.add("PART_ID");
        Column_list.add("DCRS_TRAN_NO");
        Column_list.add("NEXT_TRAN_DATE");
        Column_list.add("CREATEDDATE");
        Column_list.add("CREATEDBY");
        Column_list.add("FILEDATE");
        Column_list.add("DCRS_REMARKS");
        Column_list.add("CYCLE");
        Column_list.add("FPAN");
        Column_list.add("FILENAME");
      } else {
        Column_list.add("PARTICIPANT_ID");
        Column_list.add("TRANSACTION_TYPE");
        Column_list.add("FROM_ACCOUNT_TYPE");
        Column_list.add("TO_ACCOUNT_TYPE");
        Column_list.add("TXN_SERIAL_NO");
        Column_list.add("RESPONSE_CODE");
        Column_list.add("PAN_NUMBER");
        Column_list.add("MEMBER_NUMBER");
        Column_list.add("APPROVAL_NUMBER");
        Column_list.add("SYS_TRACE_AUDIT_NO");
        Column_list.add("TRANSACTION_DATE");
        Column_list.add("TRANSACTION_TIME");
        Column_list.add("MERCHANT_CATEGORY_CD");
        Column_list.add("CARD_ACC_SETTLE_DT");
        Column_list.add("CARD_ACC_ID");
        Column_list.add("CARD_ACC_TERMINAL_ID");
        Column_list.add("CARD_ACC_TERMINAL_LOC");
        Column_list.add("ACQUIRER_ID");
        Column_list.add("NETWORK_ID");
        Column_list.add("ACCOUNT_1_NUMBER");
        Column_list.add("ACCOUNT_1_BRANCH_ID");
        Column_list.add("ACCOUNT_2_NUMBER");
        Column_list.add("ACCOUNT_2_BRANCH_ID");
        Column_list.add("TXN_CURRENCY_CODE");
        Column_list.add("TXN_AMOUNT");
        Column_list.add("ACTUAL_TXN_AMT");
        Column_list.add("TXN_ACTIVITY_FEE");
        Column_list.add("ISS_SETTLE_CURRENCY_CD");
        Column_list.add("ISS_SETTLE_AMNT");
        Column_list.add("ISS_SETTLE_FEE");
        Column_list.add("ISS_SETTLE_PROCESS_FEE");
        Column_list.add("CARDHOLDER_BILL_CURRNCY_C");
        Column_list.add("CARDHOLDER_BILL_AMOUNT");
        Column_list.add("CARDHOLDER_BILL_ACT_FEE");
        Column_list.add("CARDHOLDER_BILL_PROCESS_F");
        Column_list.add("CARDHOLDER_BILL_SERV_FEE");
        Column_list.add("TXN_ISS_CONVERSION_RT");
        Column_list.add("TXN_CARDHOLDER_CONV_RT");
        Column_list.add("PART_ID");
        Column_list.add("DCRS_TRAN_NO");
        Column_list.add("NEXT_TRAN_DATE");
        Column_list.add("CREATEDDATE");
        Column_list.add("CREATEDBY");
        Column_list.add("FILEDATE");
        Column_list.add("DCRS_REMARKS");
        Column_list.add("FPAN");
        Column_list.add("CYCLE");
        Column_list.add("FILENAME");
      } 
    } else if (beanObj.getCategory().contains("ICD")) {
      if (beanObj.getTTUMCategory().contains("ACQUIRER")) {
        Column_list.add("PARTICIPANT_ID");
        Column_list.add("TRANSACTION_TYPE");
        Column_list.add("FROM_ACCOUNT_TYPE");
        Column_list.add("TO_ACCOUNT_TYPE");
        Column_list.add("TXN_SERIAL_NO");
        Column_list.add("RESPONSE_CODE");
        Column_list.add("PAN_NUMBER");
        Column_list.add("MEMBER_NUMBER");
        Column_list.add("APPROVAL_NUMBER");
        Column_list.add("SYS_TRACE_AUDIT_NO");
        Column_list.add("TRANSACTION_DATE");
        Column_list.add("TRANSACTION_TIME");
        Column_list.add("MERCHANT_CATEGORY_CD");
        Column_list.add("CARD_ACC_SETTLE_DT");
        Column_list.add("CARD_ACC_ID");
        Column_list.add("CARD_ACC_TERMINAL_ID");
        Column_list.add("CARD_ACC_TERMINAL_LOC");
        Column_list.add("ACQUIRER_ID");
        Column_list.add("ACQ_SETTLE_DATE");
        Column_list.add("TXN_CURRENCY_CODE");
        Column_list.add("TXN_AMOUNT");
        Column_list.add("ACTUAL_TXN_AMT");
        Column_list.add("TXN_ACTIVITY_FEE");
        Column_list.add("ACQ_SETTLE_CURRENCY_CD");
        Column_list.add("ACQ_SETTLE_AMNT");
        Column_list.add("ACQ_SETTLE_FEE");
        Column_list.add("ACQ_SETTLE_PROCESS_FEE");
        Column_list.add("TXN_ACQ_CONV_RATE");
        Column_list.add("PART_ID");
        Column_list.add("DCRS_TRAN_NO");
        Column_list.add("NEXT_TRAN_DATE");
        Column_list.add("CREATEDDATE");
        Column_list.add("CREATEDBY");
        Column_list.add("FILEDATE");
        Column_list.add("DCRS_REMARKS");
        Column_list.add("CYCLE");
        Column_list.add("FPAN");
        Column_list.add("FILENAME");
      } else {
        Column_list.add("PARTICIPANT_ID");
        Column_list.add("TRANSACTION_TYPE");
        Column_list.add("FROM_ACCOUNT_TYPE");
        Column_list.add("TO_ACCOUNT_TYPE");
        Column_list.add("TXN_SERIAL_NO");
        Column_list.add("RESPONSE_CODE");
        Column_list.add("PAN_NUMBER");
        Column_list.add("MEMBER_NUMBER");
        Column_list.add("APPROVAL_NUMBER");
        Column_list.add("SYS_TRACE_AUDIT_NO");
        Column_list.add("TRANSACTION_DATE");
        Column_list.add("TRANSACTION_TIME");
        Column_list.add("MERCHANT_CATEGORY_CD");
        Column_list.add("CARD_ACC_SETTLE_DT");
        Column_list.add("CARD_ACC_ID");
        Column_list.add("CARD_ACC_TERMINAL_ID");
        Column_list.add("CARD_ACC_TERMINAL_LOC");
        Column_list.add("ACQUIRER_ID");
        Column_list.add("NETWORK_ID");
        Column_list.add("ACCOUNT_1_NUMBER");
        Column_list.add("ACCOUNT_1_BRANCH_ID");
        Column_list.add("ACCOUNT_2_NUMBER");
        Column_list.add("ACCOUNT_2_BRANCH_ID");
        Column_list.add("TXN_CURRENCY_CODE");
        Column_list.add("TXN_AMOUNT");
        Column_list.add("ACTUAL_TXN_AMT");
        Column_list.add("TXN_ACTIVITY_FEE");
        Column_list.add("ISS_SETTLE_CURRENCY_CD");
        Column_list.add("ISS_SETTLE_AMNT");
        Column_list.add("ISS_SETTLE_FEE");
        Column_list.add("ISS_SETTLE_PROCESS_FEE");
        Column_list.add("CARDHOLDER_BILL_CURRNCY_C");
        Column_list.add("CARDHOLDER_BILL_AMOUNT");
        Column_list.add("CARDHOLDER_BILL_ACT_FEE");
        Column_list.add("CARDHOLDER_BILL_PROCESS_F");
        Column_list.add("CARDHOLDER_BILL_SERV_FEE");
        Column_list.add("TXN_ISS_CONVERSION_RT");
        Column_list.add("TXN_CARDHOLDER_CONV_RT");
        Column_list.add("PART_ID");
        Column_list.add("DCRS_TRAN_NO");
        Column_list.add("NEXT_TRAN_DATE");
        Column_list.add("CREATEDDATE");
        Column_list.add("CREATEDBY");
        Column_list.add("FILEDATE");
        Column_list.add("DCRS_REMARKS");
        Column_list.add("FPAN");
        Column_list.add("CYCLE");
        Column_list.add("FILENAME");
      } 
    } 
    Excel_data.add(Column_list);
    Excel_data.add(TTUMData);
    System.out.println("filename in nfs ttum is " + fileName);
    if (beanObj.getCategory().contains("NFS")) {
      fileName = "EXTRACT" + beanObj.getCategory() + beanObj.getTTUMCategory() + 
        beanObj.getLocalDate().replaceAll("/", "") + beanObj.getCycle() + ".xls";
      zipName = "EXTRACT" + beanObj.getCategory() + beanObj.getTTUMCategory() + 
        beanObj.getLocalDate().replaceAll("/", "") + beanObj.getCycle() + ".zip";
    } else {
      fileName = "EXTRACT" + beanObj.getCategory() + beanObj.getTTUMCategory() + 
        beanObj.getLocalDate().replaceAll("/", "") + ".xls";
      zipName = "EXTRACT" + beanObj.getCategory() + beanObj.getTTUMCategory() + 
        beanObj.getLocalDate().replaceAll("/", "") + ".zip";
    } 
    obj.generateExcelTTUM(stPath, fileName, Excel_data, "NFS_" + beanObj.getLocalDate().replaceAll("/", ""), 
        zipName);
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
  
  @RequestMapping(value = {"ExtractRUPAYRawdata"}, method = {RequestMethod.GET})
  public ModelAndView ExtractRUPAYRawdata(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** ExtractRUPAYRawdata Start Get method  ****");
    modelAndView.addObject("category", category);
    String display = "";
    List<String> subcat = new ArrayList<>();
    UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
    logger.info("in GetHeaderList" + category);
    beanObj.setCategory(category);
    subcat = this.iSourceService.getSubcategories(category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("display", display);
    modelAndView.addObject("unmatchedTTUMBean", beanObj);
    modelAndView.setViewName("ExtractRUPAYRawdata");
    logger.info("***** ExtractRUPAYRawdata.ExtractRUPAYRawdata GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"checkExtractRUPAYRawdata"}, method = {RequestMethod.POST})
  @ResponseBody
  public String checkExtractRUPAYRawdata(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response, HttpServletRequest request) {
    return "success";
  }
  
  @RequestMapping(value = {"DownloadExtractRUPAYRawdata"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadExtractRUPAYRawdata(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** DownloadExtractRUPAYRawdata.DownloadExtractRUPAYRawdata post Start ****" + 
        beanObj.getTTUMCategory() + beanObj.getCategory() + beanObj.getCycle());
    beanObj.setCategory(beanObj.getFileName());
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    String fileName = "", zipName = "";
    List<Object> Excel_data = new ArrayList();
    List<Object> TTUMData = new ArrayList();
    List<Object> TTUMData2 = new ArrayList();
    List<Object> PBGB_TTUMData = new ArrayList();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    logger.info("File Name is " + beanObj.getFileName() + beanObj.getCategory());
    boolean executed = false;
    TTUMData = this.SETTLTTUMSERVICE.ExtractRUPAYRawdata(beanObj);
    String stPath = OUTPUT_FOLDER;
    logger.info("TEMP_DIR" + stPath);
    GenerateUCOTTUM obj = new GenerateUCOTTUM();
    stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", ""), beanObj.getCategory());
    logger.info("File is created");
    List<String> Column_list = new ArrayList<>();
    Map<String, String> table_Data = new HashMap<>();
    Column_list.add("MTI");
    Column_list.add("FUNCTION_CODE");
    Column_list.add("RECORD_NUMBER");
    Column_list.add("MEMBER_INSTITUTION_ID_CODE");
    Column_list.add("UNIQUE_FILE_NAME");
    Column_list.add("DATE_SETTLEMENT");
    Column_list.add("PRODUCT_CODE");
    Column_list.add("SETTLEMENT_BIN");
    Column_list.add("FILE_CATEGORY");
    Column_list.add("VERSION_NUMBER");
    Column_list.add("ENTIRE_FILE_REJECT_INDICATOR");
    Column_list.add("FILE_REJECT_REASON_CODE");
    Column_list.add("TRANSACTIONS_COUNT");
    Column_list.add("RUN_TOTAL_AMOUNT");
    Column_list.add("ACQUIRER_INSTITUTION_ID_CODE");
    Column_list.add("AMOUNT_SETTLEMENT");
    Column_list.add("AMOUNT_TRANSACTION");
    Column_list.add("APPROVAL_CODE");
    Column_list.add("ACQUIRER_REFERENCE_DATA");
    Column_list.add("CASE_NUMBER");
    Column_list.add("CURRENCY_CODE_SETTLEMENT");
    Column_list.add("CURRENCY_CODE_TRANSACTION");
    Column_list.add("CONVERSION_RATE_SETTLEMENT");
    Column_list.add("CARD_ACCEPTOR_ADDI_ADDR");
    Column_list.add("CARD_ACCEPTOR_TERMINAL_ID");
    Column_list.add("CARD_ACCEPTOR_ZIP_CODE");
    Column_list.add("DATEANDTIME_LOCAL_TRANSACTION");
    Column_list.add("TXNFUNCTION_CODE");
    Column_list.add("LATE_PRESENTMENT_INDICATOR");
    Column_list.add("TXNMTI");
    Column_list.add("PRIMARY_ACCOUNT_NUMBER");
    Column_list.add("TXNRECORD_NUMBER");
    Column_list.add("RGCS_RECEIVED_DATE");
    Column_list.add("SETTLEMENT_DR_CR_INDICATOR");
    Column_list.add("TXN_DESTI_INSTI_ID_CODE");
    Column_list.add("TXN_ORIGIN_INSTI_ID_CODE");
    Column_list.add("CARD_HOLDER_UID");
    Column_list.add("AMOUNT_BILLING");
    Column_list.add("CURRENCY_CODE_BILLING");
    Column_list.add("CONVERSION_RATE_BILLING");
    Column_list.add("MESSAGE_REASON_CODE");
    Column_list.add("FEE_DR_CR_INDICATOR1");
    Column_list.add("FEE_AMOUNT1");
    Column_list.add("FEE_CURRENCY1");
    Column_list.add("FEE_TYPE_CODE1");
    Column_list.add("INTERCHANGE_CATEGORY1");
    Column_list.add("FEE_DR_CR_INDICATOR2");
    Column_list.add("FEE_AMOUNT2");
    Column_list.add("FEE_CURRENCY2");
    Column_list.add("FEE_TYPE_CODE2");
    Column_list.add("INTERCHANGE_CATEGORY2");
    Column_list.add("FEE_DR_CR_INDICATOR3");
    Column_list.add("FEE_AMOUNT3");
    Column_list.add("FEE_CURRENCY3");
    Column_list.add("FEE_TYPE_CODE3");
    Column_list.add("INTERCHANGE_CATEGORY3");
    Column_list.add("FEE_DR_CR_INDICATOR4");
    Column_list.add("FEE_AMOUNT4");
    Column_list.add("FEE_CURRENCY4");
    Column_list.add("FEE_TYPE_CODE4");
    Column_list.add("INTERCHANGE_CATEGORY4");
    Column_list.add("FEE_DR_CR_INDICATOR5");
    Column_list.add("FEE_AMOUNT5");
    Column_list.add("FEE_CURRENCY5");
    Column_list.add("FEE_TYPE_CODE5");
    Column_list.add("INTERCHANGE_CATEGORY5");
    Column_list.add("FLAG");
    Column_list.add("TRL_FUNCTION_CODE");
    Column_list.add("TRL_RECORD_NUMBER");
    Column_list.add("PART_ID");
    Column_list.add("DCRS_TRAN_NO");
    Column_list.add("NEXT_TRAN_DATE");
    Column_list.add("CREATEDDATE");
    Column_list.add("CREATEDBY");
    Column_list.add("FILEDATE");
    Column_list.add("PAN");
    Column_list.add("RRN");
    Column_list.add("FILENAME");
    Excel_data.add(Column_list);
    Excel_data.add(TTUMData);
    System.out.println("filename in nfs ttum is " + fileName);
    fileName = "EXTRACT" + beanObj.getCategory() + beanObj.getTTUMCategory() + 
      beanObj.getLocalDate().replaceAll("/", "") + ".xls";
    zipName = "EXTRACT" + beanObj.getCategory() + beanObj.getTTUMCategory() + 
      beanObj.getLocalDate().replaceAll("/", "") + ".zip";
    obj.generateExcelTTUM(stPath, fileName, Excel_data, "RUPAY_" + beanObj.getLocalDate().replaceAll("/", ""), 
        zipName);
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
  
  @RequestMapping(value = {"ExtractMASTERCARDRawdata"}, method = {RequestMethod.GET})
  public ModelAndView ExtractMASTERCARDRawdata(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** ExtractMASTERCARDRawdata Start Get method  ****");
    modelAndView.addObject("category", category);
    String display = "";
    List<String> subcat = new ArrayList<>();
    UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
    logger.info("in GetHeaderList" + category);
    beanObj.setCategory(category);
    subcat = this.iSourceService.getSubcategories(category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("display", display);
    modelAndView.addObject("unmatchedTTUMBean", beanObj);
    modelAndView.setViewName("ExtractMASTERCARDRawdata");
    logger.info("***** ExtractMASTERCARDRawdata.ExtractMASTERCARDRawdata GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"checkExtractMASTERCARDRawdata"}, method = {RequestMethod.POST})
  @ResponseBody
  public String checkExtractMASTERCARDRawdata(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response, HttpServletRequest request) {
    return "success";
  }
  
  @RequestMapping(value = {"DownloadExtractMASTERCARDRawdata"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadExtractMASTERCARDRawdata(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** DownloadExtractMASTERCARDRawdata.DownloadExtractMASTERCARDRawdata post Start ****");
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    String fileName = "", zipName = "";
    beanObj.setCategory(beanObj.getFileName());
    List<Object> Excel_data = new ArrayList();
    List<Object> TTUMData = new ArrayList();
    List<Object> TTUMData2 = new ArrayList();
    List<Object> PBGB_TTUMData = new ArrayList();
    logger.info("Created by is " + Createdby);
    beanObj.setCreatedBy(Createdby);
    logger.info("File Name is " + beanObj.getFileName());
    boolean executed = false;
    TTUMData = this.SETTLTTUMSERVICE.ExtractMASTERCARDRawdata(beanObj);
    String stPath = OUTPUT_FOLDER;
    logger.info("TEMP_DIR" + stPath);
    GenerateUCOTTUM obj = new GenerateUCOTTUM();
    stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", ""), beanObj.getCategory());
    logger.info("File is created");
    List<String> Column_list = new ArrayList<>();
    Map<String, String> table_Data = new HashMap<>();
    if (beanObj.getCategory().contains("MASTERCARD_ATM")) {
      Column_list.add("MSG_TYPE_IND");
      Column_list.add("SWITCH_SERIAL_NUM");
      Column_list.add("PROCESSOR_ACQ_OR_ISS");
      Column_list.add("PROCESSOR_ID");
      Column_list.add("TRANSACTION_DATE");
      Column_list.add("TRANSACTION_TIME");
      Column_list.add("PAN_LENGTH");
      Column_list.add("PAN");
      Column_list.add("PROCESSING_CODE");
      Column_list.add("TRACE_NUMBER");
      Column_list.add("MERCHANT_TYPE");
      Column_list.add("POS_ENTRY");
      Column_list.add("REFERENCE_NUMBER");
      Column_list.add("ACQUIRER_INSTITUTION_ID");
      Column_list.add("TERMINAL_ID");
      Column_list.add("RESPONSE_CODE");
      Column_list.add("BRAND");
      Column_list.add("ADVICE_REASON_CODE");
      Column_list.add("INTERACURRENCY_AGREEMENT_CODE");
      Column_list.add("AUTHORIZATION_ID");
      Column_list.add("CURRENCY_CODE");
      Column_list.add("IMPLIED_DECIMAL");
      Column_list.add("COMPLETED_AMT_TRANS");
      Column_list.add("COMPLETED_AMT_INDICATOR");
      Column_list.add("CASH_BACK_AMT");
      Column_list.add("CASH_BACK_INDICATOR");
      Column_list.add("ACCESS_FEE");
      Column_list.add("ACCESS_FEE_INDICATOR");
      Column_list.add("CURRENCY_CODE_SETTLEMENT");
      Column_list.add("IMPLIED_DECIMAL_SETTLEMENT");
      Column_list.add("CONVERSION_RATE_SETTLEMENT");
      Column_list.add("COMPLETED_AMT_SETTLEMENT");
      Column_list.add("COMPLETED_AMT_SETTLEMENT_INDICATOR");
      Column_list.add("INTERCHANGE_FEE");
      Column_list.add("INTERCHANGE_FEE_INDICATOR");
      Column_list.add("SERVICE_LEVEL_INDICATOR");
      Column_list.add("RESPONSE_CODE_2");
      Column_list.add("FILLER");
      Column_list.add("POSITIVE_ID_INDICATOR");
      Column_list.add("ATM_SURCHARGE_FREE_PROGRAM_ID");
      Column_list.add("CROSS_BORDER_INDICATOR");
      Column_list.add("CROSS_BORDER_CURRENCY_INDICATOR");
      Column_list.add("VISA_ISA_FEE_INDICATOR");
      Column_list.add("REQUEST_ATM_TRANS_LOCAL");
      Column_list.add("FILLER_ADJ");
      Column_list.add("TRACE_NUMBER_ADJUSTMENT_TRANS");
      Column_list.add("RECON_ACTIVITY");
      Column_list.add("FILEDATE");
      Column_list.add("FILENAME");
    } else {
      Column_list.add("MSGTYPE");
      Column_list.add("PAN");
      Column_list.add("PROCESSING_CODE");
      Column_list.add("AMOUNT");
      Column_list.add("AMOUNT_RECON");
      Column_list.add("AMOUNT_CARDHOLDER");
      Column_list.add("CONV_RATE_RECON");
      Column_list.add("DATE_VAL");
      Column_list.add("EXPIRE_DATE");
      Column_list.add("DATA_CODE");
      Column_list.add("CARD_SEQ_NUM");
      Column_list.add("FUNCATION_CODE");
      Column_list.add("MSG_RES_CODE");
      Column_list.add("CARD_ACC_CODE");
      Column_list.add("AMOUNT_ORG");
      Column_list.add("AQUIERER_REF_NO");
      Column_list.add("FI_ID_CODE");
      Column_list.add("RETRV_REF_NO");
      Column_list.add("APPROVAL_CODE");
      Column_list.add("SERVICE_CODE");
      Column_list.add("CARD_ACC_TERM_ID");
      Column_list.add("CARD_ACC_ID_CODE");
      Column_list.add("ADDITIONAL_DATA");
      Column_list.add("CURRENCY_CODE_TRAN");
      Column_list.add("CURRENCY_CODE_RECON");
      Column_list.add("TRAN_LIFECYCLE_ID");
      Column_list.add("MSG_NUM");
      Column_list.add("DATE_ACTION");
      Column_list.add("TRAN_DEST_ID_CODE");
      Column_list.add("TRAN_ORG_ID_CODE");
      Column_list.add("CARD_ISS_REF_DATA");
      Column_list.add("RECV_INST_IDCODE");
      Column_list.add("TERMINAL_TYPE");
      Column_list.add("ELEC_COM_INDIC");
      Column_list.add("PROCESSING_MODE");
      Column_list.add("CURRENCY_EXPONENT");
      Column_list.add("BUSINESS_ACT");
      Column_list.add("SETTLEMENT_IND");
      Column_list.add("CARD_ACCP_NAME_LOC");
      Column_list.add("HEADER_TYPE");
      Column_list.add("FILE_NAME");
      Column_list.add("FILEDATE");
      Column_list.add("FPAN");
    } 
    Excel_data.add(Column_list);
    Excel_data.add(TTUMData);
    System.out.println("filename in nfs ttum is " + fileName);
    fileName = "EXTRACT" + beanObj.getCategory() + beanObj.getLocalDate().replaceAll("/", "") + ".xls";
    zipName = "EXTRACT" + beanObj.getCategory() + beanObj.getLocalDate().replaceAll("/", "") + ".zip";
    obj.generateExcelTTUM(stPath, fileName, Excel_data, "Mastercard_" + beanObj.getLocalDate().replaceAll("/", ""), 
        zipName);
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
