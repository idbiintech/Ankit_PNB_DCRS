package com.recon.control;


import com.recon.model.LoginBean;
import com.recon.model.MastercardTTUMBean;
import com.recon.service.ISourceService;
import com.recon.service.MastercardTTUMService;
import com.recon.util.FileDetailsJson;
import com.recon.util.GenerateUCOTTUM;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MastercardTTUMController {
  private static final Logger logger = Logger.getLogger(com.recon.control.MastercardTTUMController.class);
  
  @Autowired
  ISourceService iSourceService;
  
  @Autowired
  MastercardTTUMService mastrcardTTUMservice;
  
  @RequestMapping(value = {"GenerateMastercardTTUM"}, method = {RequestMethod.GET})
  public String GenerateMastercardTTUM(ModelAndView modelAndView, MastercardTTUMBean mastercardTTUMBean, @RequestParam("category") String category, Model model, HttpServletRequest request) throws Exception {
    logger.info("***** GenerateMastercardTTUM.GenerateMastercardTTUM GET() Start ****");
    List<String> subcat = new ArrayList<>();
    List<String> respcode = new ArrayList<>();
    List<String> files = new ArrayList<>();
    String display = "";
    logger.info("in GetHeaderList" + category);
    MastercardTTUMBean beanObj = new MastercardTTUMBean();
    beanObj.setCategory(category);
    subcat = this.iSourceService.getSubcategories(category);
    logger.info("-----" + subcat);
    model.addAttribute("category", category);
    model.addAttribute("subcategory", subcat);
    model.addAttribute("respcode", respcode);
    model.addAttribute("files", files);
    model.addAttribute("display", display);
    model.addAttribute("generateTTUMBean", beanObj);
    logger.info("***** GenerateMastercardTTUM.getComparePage GET() End ****");
    return "GenerateMastercardTTUM";
  }
  
  @RequestMapping(value = {"GenerateMastercardTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public String GenerateMastercardTTUM(@ModelAttribute("generateTTUMBean") MastercardTTUMBean beanObj, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** GenerateMastercardTTUM.GenerateMastercardTTUM post Start ****");
    logger.info("GenerateUnmatchedTTUM POST");
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby + " localDate is " + beanObj.getLocalDate());
    logger.info("filedate is " + beanObj.getFileDate() + " ttum type is " + beanObj.getFileName());
    System.out.println("st sub category for domestic tum is " + beanObj.getStSubCategory());
    System.out.println("FILENAme is " + beanObj.getFileName());
    beanObj.setCreatedBy(Createdby);
    boolean executed = false;
    return "Processing Completed Successfully! \n Please download Report";
  }
  
  @RequestMapping(value = {"downloadMastercardTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public void downloadMastercardTTUM(@ModelAttribute("generateTTUMBean") MastercardTTUMBean beanObj, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** GenerateUnmatchedTTUM.DownloadUnmatchedTTUM post Start ****");
    logger.info("DownloadUnmatchedTTUM POST");
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    List<Object> TTUMData = new ArrayList();
    beanObj.setCreatedBy(Createdby);
    String fileName = "";
    String fileNameCsv = "";
    try {
      logger.info("StStart_Date is " + beanObj.getStStart_Date());
      logger.info("stdate is " + beanObj.getStDate());
      beanObj.setFileDate(beanObj.getStStart_Date());
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      Date date = sdf.parse(beanObj.getFileDate());
      sdf = new SimpleDateFormat("dd-MM-yyyy");
      String stnewDate = sdf.format(date);
      List<Object> Excel_data = new ArrayList();
      String TEMP_DIR = System.getProperty("java.io.tmpdir");
      logger.info("new date is " + stnewDate);
      logger.info("TEMP_DIR" + TEMP_DIR);
      logger.info("1 " + (TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/") ? 0 : 1));
      if (!TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/")) {
        logger.info(Boolean.valueOf(!TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/")));
        TEMP_DIR = String.valueOf(TEMP_DIR) + File.separator;
      } 
      String stpath = TEMP_DIR;
      beanObj.setStPath(stpath);
      logger.info("Path is " + stpath);
      boolean directoryCreated = this.mastrcardTTUMservice.checkAndMakeDirectory(beanObj);
      if (directoryCreated) {
        stpath = String.valueOf(stpath) + beanObj.getCategory() + File.separator + stnewDate;
        logger.info("new path is " + stpath);
        logger.info("filedate is " + beanObj.getFileDate());
        logger.info("SubCategory is " + beanObj.getStSubCategory());
        logger.info("getInRec_Set_Id is " + beanObj.getInRec_Set_Id());
        GenerateUCOTTUM obj = new GenerateUCOTTUM();
        if (beanObj.getStSubCategory().equalsIgnoreCase("ISSUER") && beanObj.getCategory().equalsIgnoreCase("MASTERCARD") && (
          beanObj.getInRec_Set_Id().equalsIgnoreCase("DECLINED") || 
          beanObj.getInRec_Set_Id().equalsIgnoreCase("REFUND") || 
          beanObj.getInRec_Set_Id().equalsIgnoreCase("UNRECON") || 
          beanObj.getInRec_Set_Id().equalsIgnoreCase("FEE") || 
          beanObj.getInRec_Set_Id().equalsIgnoreCase("REFUND") || 
          beanObj.getInRec_Set_Id().equalsIgnoreCase("REPRESENTMENT") || 
          beanObj.getInRec_Set_Id().equalsIgnoreCase("CHARGEBACKRAISE") || 
          beanObj.getInRec_Set_Id().equalsIgnoreCase("LATEPRESENTMENT") || 
          beanObj.getInRec_Set_Id().equalsIgnoreCase("UNRECON2"))) {
          fileName = String.valueOf(beanObj.getCategory()) + "_" + beanObj.getInRec_Set_Id() + "_TTUM.txt";
          TTUMData = this.mastrcardTTUMservice.getMastercardTTUMData(beanObj);
          obj.generateMastercardTTUMFiles(stpath, fileName, 2, TTUMData, beanObj.getInRec_Set_Id());
          logger.info("Path returned is " + stpath);
        } 
        if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD") && beanObj.getStSubCategory().equalsIgnoreCase("ISSUER") && 
          beanObj.getInRec_Set_Id().equalsIgnoreCase("SURCHARGE")) {
          TTUMData = this.mastrcardTTUMservice.getMastercardTTUMData(beanObj);
          List<String> Column_list = new ArrayList<>();
          Column_list.add("SNO1");
          Column_list.add("SNO2");
          Column_list.add("SNO3");
          Column_list.add("DRCR");
          Column_list.add("ACCTYPE");
          Column_list.add("ACCTNO");
          Column_list.add("BRCODE");
          Column_list.add("PERTICULERS");
          Column_list.add("surchargAmt");
          Excel_data.add(Column_list);
          Excel_data.add(TTUMData);
          Excel_data.clear();
          Excel_data.add(Column_list);
          Excel_data.add(TTUMData.get(0));
          fileName = String.valueOf(beanObj.getCategory()) + "_" + beanObj.getInRec_Set_Id() + "_TTUM_1.xls";
          this.mastrcardTTUMservice.generateExcelTTUM(stpath, fileName, Excel_data, beanObj.getInRec_Set_Id(), response, false);
        } 
        logger.info("File is created");
        File file = new File(String.valueOf(stpath) + File.separator + fileName);
        logger.info("path of zip file " + stpath + File.separator + fileName);
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
    } catch (Exception e) {
      logger.info("Exception in DownloadUnmatchedTTUM " + e);
      e.printStackTrace();
    } 
  }
  
  @RequestMapping(value = {"downloadMastercardFundingTTUM"}, method = {RequestMethod.POST})
  @ResponseBody
  public void downloadMastercardFundingTTUM(@ModelAttribute("generateTTUMBean") MastercardTTUMBean beanObj, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** GenerateUnmatchedTTUM.downloadMastercardFundingTTUM post Start ****");
    logger.info("DownloadUnmatchedTTUM POST");
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby);
    List<Object> TTUMData = new ArrayList();
    beanObj.setCreatedBy(Createdby);
    String fileName = "";
    try {
      beanObj.setCategory(beanObj.getInRec_Set_Id());
      Date todayDate = new Date();
      SimpleDateFormat todaysdf = new SimpleDateFormat("dd/MM/yyyy");
      String todayStrDate = todaysdf.format(todayDate);
      beanObj.setFileDate(todayStrDate);
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      Date date = sdf.parse(beanObj.getFileDate());
      sdf = new SimpleDateFormat("dd-MM-yyyy");
      String stnewDate = sdf.format(date);
      List<Object> Excel_data = new ArrayList();
      String TEMP_DIR = System.getProperty("java.io.tmpdir");
      logger.info("new date is " + stnewDate);
      logger.info("TEMP_DIR" + TEMP_DIR);
      logger.info("1 " + (TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/") ? 0 : 1));
      if (!TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/")) {
        logger.info(Boolean.valueOf(!TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/")));
        TEMP_DIR = String.valueOf(TEMP_DIR) + File.separator;
      } 
      String stpath = TEMP_DIR;
      beanObj.setStPath(stpath);
      logger.info("Path is " + stpath);
      boolean directoryCreated = this.mastrcardTTUMservice.checkAndMakeDirectory(beanObj);
      if (directoryCreated) {
        stpath = String.valueOf(stpath) + beanObj.getCategory() + File.separator + stnewDate;
        logger.info("new path is " + stpath);
        logger.info("filedate is " + beanObj.getFileDate());
        logger.info("SubCategory is " + beanObj.getStSubCategory());
        logger.info("getInRec_Set_Id is " + beanObj.getInRec_Set_Id());
        GenerateUCOTTUM obj = new GenerateUCOTTUM();
        if (beanObj.getCategory().equalsIgnoreCase("ISSUER") || beanObj.getCategory().equalsIgnoreCase("ACQUIRER")) {
          fileName = "FUNDING_" + beanObj.getInRec_Set_Id() + "_EOD_TTUM.txt";
          TTUMData = this.mastrcardTTUMservice.getMastercardEODTTUM(beanObj);
          obj.generateMastercardEODTTUM(stpath, fileName, 2, TTUMData, beanObj.getInRec_Set_Id());
          logger.info("Path returned is " + stpath);
        } 
        logger.info("File is created");
        File file = new File(String.valueOf(stpath) + File.separator + fileName);
        logger.info("path of zip file " + stpath + File.separator + fileName);
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
    } catch (Exception e) {
      logger.info("Exception in DownloadUnmatchedTTUM " + e);
      e.printStackTrace();
    } 
  }
  
  @RequestMapping(value = {"checkMastercardTTUMProcess"}, method = {RequestMethod.POST})
  @ResponseBody
  public String checkTTUMProcessed(@ModelAttribute("generateTTUMBean") MastercardTTUMBean beanObj, FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response, HttpServletRequest request) {
    try {
      logger.info("RupayTTUMController: checkTTUMProcessed: Entry");
      return "success";
    } catch (Exception e) {
      logger.info("Exception is " + e);
      return "Exception";
    } 
  }
  
  @RequestMapping(value = {"GenerateMastercardSalaryTTUM"}, method = {RequestMethod.GET})
  public String GenerateMastercardSalaryTTUM(ModelAndView modelAndView, MastercardTTUMBean mastercardTTUMBean, @RequestParam("category") String category, Model model, HttpServletRequest request) throws Exception {
    logger.info("***** GenerateMastercardTTUM.GenerateMastercardTTUM GET() Start ****");
    List<String> subcat = new ArrayList<>();
    List<String> respcode = new ArrayList<>();
    List<String> files = new ArrayList<>();
    String display = "";
    logger.info("in GetHeaderList" + category);
    MastercardTTUMBean beanObj = new MastercardTTUMBean();
    beanObj.setCategory(category);
    subcat = this.iSourceService.getSubcategories(category);
    logger.info("-----" + subcat);
    model.addAttribute("category", category);
    model.addAttribute("subcategory", subcat);
    model.addAttribute("respcode", respcode);
    model.addAttribute("files", files);
    model.addAttribute("display", display);
    model.addAttribute("generateTTUMBean", beanObj);
    logger.info("***** GenerateMastercardTTUM.getComparePage GET() End ****");
    return "GenerateMastercardSalaryTTUM";
  }
  
  @RequestMapping(value = {"GenerateMastercardFundingTTUM"}, method = {RequestMethod.GET})
  public String GenerateMastercardFundingTTUM(ModelAndView modelAndView, MastercardTTUMBean mastercardTTUMBean, @RequestParam("category") String category, Model model, HttpServletRequest request) throws Exception {
    logger.info("***** GenerateMastercardTTUM.GenerateMastercardFundingTTUM GET() Start ****");
    List<String> subcat = new ArrayList<>();
    List<String> respcode = new ArrayList<>();
    List<String> files = new ArrayList<>();
    String display = "";
    logger.info("in GetHeaderList" + category);
    MastercardTTUMBean beanObj = new MastercardTTUMBean();
    beanObj.setCategory(category);
    subcat = this.iSourceService.getSubcategories(category);
    logger.info("-----" + subcat);
    model.addAttribute("category", category);
    model.addAttribute("subcategory", subcat);
    model.addAttribute("respcode", respcode);
    model.addAttribute("files", files);
    model.addAttribute("display", display);
    model.addAttribute("generateTTUMBean", beanObj);
    logger.info("***** GenerateMastercardTTUM.GenerateMastercardFundingTTUM End ****");
    return "GenerateMastercardFundingTTUM";
  }
}
