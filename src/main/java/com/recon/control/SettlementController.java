package com.recon.control;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.recon.model.CompareSetupBean;
import com.recon.model.Gl_bean;
import com.recon.model.LoginBean;
import com.recon.model.Mastercard_chargeback;
import com.recon.model.NetworkFileUpdateBean;
import com.recon.model.ReconDataJson;
import com.recon.model.RupayUploadBean;
import com.recon.model.Rupay_Gl_repo;
import com.recon.model.Rupay_gl_Lpcases;
import com.recon.model.Rupay_gl_autorev;
import com.recon.model.Rupay_sur_GlBean;
import com.recon.model.SettlementBean;
import com.recon.model.SettlementTypeBean;
import com.recon.model.SettlementTypeJson;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.ISettelmentService;
import com.recon.service.ISourceService;
import com.recon.service.NFSUnmatchTTUMService;
import com.recon.service.RupayTTUMService;
import com.recon.service.SettlementTTUMService;
import com.recon.util.CSRFToken;
import com.recon.util.FileDetailsJson;
import com.recon.util.GenerateUCOTTUM;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SettlementController {
  private static final String ERROR_MSG = "error_msg";
  
  private static final String SUCCESS_MSG = "success_msg";
  
  private static final Logger logger = Logger.getLogger(com.recon.control.SettlementController.class);
  
  public static final String OUTPUT_FOLDER = String.valueOf(System.getProperty("catalina.home")) + File.separator + "FUNDING";
  
  public static final String OUTPUT_FOLDER1 = String.valueOf(System.getProperty("catalina.home")) + File.separator + "TTUM1";
  
  @Autowired
  public static final String CATALINA_HOME = "catalina.home";
  
  public static final String TTUM_FOLDER = System.getProperty("catalina.home");
  
  @Autowired
  NFSUnmatchTTUMService nfsTTUMService;
  
  @Autowired
  RupayTTUMService rupayTTUMService;
  
  @Autowired
  ISettelmentService isettelmentservice;
  
  @Autowired
  ISourceService iSourceService;
  
  @Autowired
  SettlementTTUMService SETTLTTUMSERVICE;
  
  @RequestMapping(value = {"Settlement"}, method = {RequestMethod.GET})
  public ModelAndView Configuration(ModelAndView modelAndView) {
    modelAndView.setViewName("SettlementMenu");
    return modelAndView;
  }
  
  @RequestMapping(value = {"ReconData"}, method = {RequestMethod.GET})
  public String settlement(ModelAndView modelAndView, Model model, SettlementTypeBean settlementTypeBean, LoginBean loginBean, HttpServletRequest request, HttpServletResponse response) {
    try {
      loginBean.setUser_id(((LoginBean)request.getSession().getAttribute("loginBean")).getUser_id().trim());
      modelAndView.setViewName("ReconSettlement");
      model.addAttribute("SettlementBean", settlementTypeBean);
      return "ReconSettlement";
    } catch (Exception ex) {
      ex.printStackTrace();
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"UNReconData"}, method = {RequestMethod.GET})
  public String UNReconData(ModelAndView modelAndView, Model model, SettlementTypeBean settlementTypeBean, LoginBean loginBean, HttpServletRequest request, HttpServletResponse response) {
    try {
      loginBean.setUser_id(((LoginBean)request.getSession().getAttribute("loginBean")).getUser_id().trim());
      modelAndView.setViewName("ReconSettlement");
      model.addAttribute("SettlementBean", settlementTypeBean);
      return "UNReconSettlement";
    } catch (Exception ex) {
      ex.printStackTrace();
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"ProcessedData"}, method = {RequestMethod.GET})
  public String KnockOffData(ModelAndView modelAndView, Model model, SettlementTypeBean settlementTypeBean, LoginBean loginBean, HttpServletRequest request, HttpServletResponse response) {
    try {
      loginBean.setUser_id(((LoginBean)request.getSession().getAttribute("loginBean")).getUser_id().trim());
      modelAndView.setViewName("ProcessedData");
      model.addAttribute("SettlementBean", settlementTypeBean);
      return "ProcessedData";
    } catch (Exception ex) {
      ex.printStackTrace();
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"/GetReconData"}, method = {RequestMethod.GET})
  public String GetReconData(@RequestParam("tbl") String table, @RequestParam("date") String date, @RequestParam("type") String type, @RequestParam("searchValue") String searchValue, HttpServletRequest request, LoginBean loginBean, RedirectAttributes redirectAttributes, Model model) throws Exception {
    try {
      loginBean.setUser_id(((LoginBean)request.getSession().getAttribute("loginBean")).getUser_id().trim());
      String column = "";
      ArrayList<SettlementTypeBean> dataList = this.isettelmentservice.getReconData(table.trim(), type.trim(), 
          date.trim(), searchValue.trim());
      System.out.println(column.split(","));
      model.addAttribute("table", table.trim());
      model.addAttribute("dataList", dataList);
      return "viewReconData";
    } catch (Exception e) {
      logger.error(e.getMessage());
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"/GetJtableReconData"}, method = {RequestMethod.GET})
  public String GetJtableReconData(@RequestParam("tbl") String table, @RequestParam("date") String date, @RequestParam("type") String type, @RequestParam("searchValue") String searchValue, HttpServletRequest request, LoginBean loginBean, RedirectAttributes redirectAttributes, Model model, HttpSession session) throws Exception {
    String[] split_table = table.split("_");
    String concat_table = String.valueOf(split_table[0]) + "_" + split_table[2];
    try {
      loginBean.setUser_id(((LoginBean)request.getSession().getAttribute("loginBean")).getUser_id().trim());
      session.setAttribute("tbl", table);
      session.setAttribute("date", date);
      session.setAttribute("type", type);
      session.setAttribute("searchValue", searchValue);
      ArrayList<SettlementTypeBean> dataList = this.isettelmentservice.getReconData(table.trim(), type.trim(), 
          date.trim(), searchValue.trim());
      model.addAttribute("table", table.trim());
      model.addAttribute("dataList", dataList);
      if (concat_table.trim().equalsIgnoreCase("SETTLEMENT_SWITCH"))
        return "viewSwitchReconData"; 
      if (concat_table.trim().equalsIgnoreCase("SETTLEMENT_CBS"))
        return "viewCBSReconData"; 
      return "Login.do";
    } catch (Exception e) {
      logger.error(e.getMessage());
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"/GetJtableData"}, method = {RequestMethod.POST})
  @ResponseBody
  public ReconDataJson GetJtableData(@ModelAttribute("settlementTypeBean") SettlementTypeBean settlementTypeBean, HttpServletRequest request, ReconDataJson dataJson, LoginBean loginBean, int jtStartIndex, int jtPageSize, RedirectAttributes redirectAttributes, Model model, HttpSession session) throws Exception {
    try {
      loginBean.setUser_id(((LoginBean)request.getSession().getAttribute("loginBean")).getUser_id().trim());
      HashMap<String, Object> JSONROOT = new HashMap<>();
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      String table = (String)session.getAttribute("tbl");
      String date = (String)session.getAttribute("date");
      String type = (String)session.getAttribute("type");
      String searchValue = (String)session.getAttribute("searchValue");
      ArrayList<SettlementTypeBean> dataList = this.isettelmentservice.getChngReconData(table.trim(), type.trim(), 
          date.trim(), searchValue.trim(), jtStartIndex, jtPageSize);
      JSONROOT.put("Records", dataList);
      String jsonArray = gson.toJson(JSONROOT);
      int totalRecordcount = this.isettelmentservice.getReconDataCount(table.trim(), type.trim(), date.trim(), 
          searchValue.trim());
      dataJson.setParams("OK", dataList, totalRecordcount);
      dataJson.setTotalRecordCount(totalRecordcount);
    } catch (Exception e) {
      logger.error(e.getMessage());
      dataJson.setParams("ERROR", e.getMessage());
    } 
    return dataJson;
  }
  
  @RequestMapping(value = {"/editsave"}, method = {RequestMethod.POST})
  public ResponseEntity<Integer> editsave(@ModelAttribute("settlementTypeBean") SettlementTypeBean settlementTypeBean, HttpServletRequest request, LoginBean loginBean, RedirectAttributes redirectAttributes, Model model) throws Exception {
    int result = 200;
    try {
      result = this.isettelmentservice.updateRecord(settlementTypeBean);
      loginBean.setUser_id(((LoginBean)request.getSession().getAttribute("loginBean")).getUser_id().trim());
      result = this.isettelmentservice.updateRecord(settlementTypeBean);
      return new ResponseEntity(Integer.valueOf(result), HttpStatus.OK);
    } catch (Exception e) {
      logger.error(e.getMessage());
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return new ResponseEntity(Integer.valueOf(result), HttpStatus.BAD_REQUEST);
    } 
  }
  
  @RequestMapping(value = {"GenerateReport"}, method = {RequestMethod.POST})
  public String compareData(@ModelAttribute("SettlementBean") SettlementTypeBean typeBean, RedirectAttributes redirectAttributes, HttpSession httpsession, Model model, HttpServletRequest request, HttpServletResponse response) {
    try {
      List<List<String>> DATA = new ArrayList<>();
      DATA = this.isettelmentservice.getReconData1(typeBean.getSetltbl().trim(), typeBean.getDataType().trim(), 
          typeBean.getDatepicker().trim(), typeBean.getSearchValue());
      String stFileName = this.isettelmentservice.getFileName(typeBean.getSetltbl());
      String filename = String.valueOf(stFileName) + "-" + typeBean.getDataType().trim() + "_" + typeBean.getDatepicker().trim();
      Map<String, Object> map = new HashMap<>();
      model.addAttribute("filename", filename);
      model.addAttribute("DATA", DATA);
      return "generateExcelReport";
    } catch (Exception e) {
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"/GetReconDataCount"}, method = {RequestMethod.POST})
  @ResponseBody
  public int GetReconDataCount(@RequestParam("tbl") String table, @RequestParam("date") String date, @RequestParam("type") String type, @RequestParam("searchValue") String searchValue, HttpServletRequest request, LoginBean loginBean, RedirectAttributes redirectAttributes, Model model) throws Exception {
    int count = 0;
    try {
      loginBean.setUser_id(((LoginBean)request.getSession().getAttribute("loginBean")).getUser_id().trim());
      String column = "";
      count = this.isettelmentservice.getReconDataCount(table.trim(), type.trim(), date.trim(), searchValue.trim());
      return count;
    } catch (Exception e) {
      logger.error(e.getMessage());
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return count;
    } 
  }
  
  @RequestMapping(value = {"/GetColumnList"}, method = {RequestMethod.POST})
  @ResponseBody
  public ArrayList<String> GetColumnList(@RequestParam("tableName") String tableName) {
    ArrayList<String> colList = this.isettelmentservice.getColumnList(tableName);
    return colList;
  }
  
  @RequestMapping(value = {"/GetSettelmentType"}, method = {RequestMethod.POST})
  @ResponseBody
  public ArrayList<String> GetSettelmentType(@RequestParam("tableName") String tableName) {
    ArrayList<String> typeList = this.isettelmentservice.gettype(tableName);
    return typeList;
  }
  
  @RequestMapping(value = {"/GetSettelmentTypedtls"}, produces = {"application/json"}, method = {RequestMethod.POST})
  @ResponseBody
  public SettlementTypeJson GetSettelmentTypesetails(SettlementTypeJson settlementTypeJson) {
    String action = "CIA GL", tablename = "settlement_cbs";
    try {
      HashMap<String, Object> JSONROOT = new HashMap<>();
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      List<SettlementTypeBean> list = this.isettelmentservice.getSettlmentType(action.trim(), tablename.trim());
      JSONROOT.put("Records", list);
      String jsonArray = gson.toJson(JSONROOT);
      settlementTypeJson.setParams("OK", list);
      System.out.println(settlementTypeJson);
    } catch (Exception e) {
      settlementTypeJson.setParams("ERROR", e.getMessage());
    } 
    return settlementTypeJson;
  }
  
  @RequestMapping(value = {"DownloadReports"}, method = {RequestMethod.GET})
  public ModelAndView getdownloadPage(ModelAndView modelAndView, HttpServletRequest request, SettlementBean settlementBean, @RequestParam("category") String category) throws Exception {
    List<String> subcat = new ArrayList<>();
    System.out.println("in GetHeaderList" + category);
    subcat = this.iSourceService.getSubcategories(category);
    modelAndView.addObject("category", category);
    modelAndView.addObject("subcategory", subcat);
    String csrf = CSRFToken.getTokenForSession(request.getSession());
    modelAndView.addObject("CSRFToken", csrf);
    modelAndView.addObject("SettlementBean", settlementBean);
    modelAndView.setViewName("DownloadReports");
    return modelAndView;
  }
  
  @RequestMapping(value = {"DownloadReports1"}, method = {RequestMethod.POST})
  public String downloadDhanaReports1(@ModelAttribute("SettlementBean") SettlementBean settlementBean, HttpServletResponse response, HttpServletRequest request, Model model) {
    List<Object> Excel_dataMAin = new ArrayList<Object>();


    System.out.println("SettlementBean.getStsubCategory() " + settlementBean.getStsubCategory() + " cet " + 
        settlementBean.getCategory() + " Report " );
    if (settlementBean.getStsubCategory().equalsIgnoreCase("Acquirer") && 
      settlementBean.getCategory().equalsIgnoreCase("NFS")) {
      Excel_dataMAin = this.nfsTTUMService.downloadDhanaReport(settlementBean);
      model.addAttribute("ReportName", "NFSAcquirer_Recon_Reports_SHEET_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("Acquirer") && 
      settlementBean.getCategory().equalsIgnoreCase("CARDTOCARD")) {
      Excel_dataMAin = this.nfsTTUMService.downloadDhanaReportCTCACQ(settlementBean);
      model.addAttribute("ReportName", "CTC_ACQ_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ISSUER") && 
      settlementBean.getCategory().equalsIgnoreCase("CARDTOCARD")) {
      Excel_dataMAin = this.nfsTTUMService.downloadDhanaReportCTCISS(settlementBean);
      model.addAttribute("ReportName", "CTC_ISS_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("Domestic") && 
      settlementBean.getCategory().equalsIgnoreCase("RUPAY")) {
      Excel_dataMAin = this.nfsTTUMService.downloadRupayDhanaReport(settlementBean);
      model.addAttribute("ReportName", "RupayDOM_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("INTERNATIONAL") && 
      settlementBean.getCategory().equalsIgnoreCase("RUPAY")) {
      Excel_dataMAin = this.nfsTTUMService.downloadRupayDhanaReportINT(settlementBean);
      model.addAttribute("ReportName", "RupayINT_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ISSUER") && 
      settlementBean.getCategory().equalsIgnoreCase("NFS")) {
      Excel_dataMAin = this.nfsTTUMService.downloadIssuerDhanaReport(settlementBean);
      model.addAttribute("ReportName", "NFSIssuer_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("Domestic") && 
      settlementBean.getCategory().equalsIgnoreCase("QSPARC")) {
      Excel_dataMAin = this.nfsTTUMService.downloadQSPARCDOMReport(settlementBean);
      model.addAttribute("ReportName", "QSPARCDOM_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ISSUER") && 
      settlementBean.getCategory().equalsIgnoreCase("ICD")) {
      Excel_dataMAin = this.nfsTTUMService.downloadICDISSReport(settlementBean);
      model.addAttribute("ReportName", "ICD_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQUIRER") && 
      settlementBean.getCategory().equalsIgnoreCase("ICD")) {
      Excel_dataMAin = this.nfsTTUMService.downloadICDACQReport(settlementBean);
      model.addAttribute("ReportName", "ICDISS_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ISSUER") && 
      settlementBean.getCategory().equalsIgnoreCase("ICCW")) {
      Excel_dataMAin = this.nfsTTUMService.downloadICCWISSReport(settlementBean);
      model.addAttribute("ReportName", "ICCWISS_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQUIRER") && 
      settlementBean.getCategory().equalsIgnoreCase("ICCW")) {
      Excel_dataMAin = this.nfsTTUMService.downloadICCWACQReport(settlementBean);
      model.addAttribute("ReportName", "ICCWACQ_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ISSUER") && 
      settlementBean.getCategory().equalsIgnoreCase("MASTERCARD")) {
      Excel_dataMAin = this.nfsTTUMService.downloadMCISSPOSReport(settlementBean);
      model.addAttribute("ReportName", "MASTERCARDISSUER_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ISSUER_ATM") && 
      settlementBean.getCategory().equalsIgnoreCase("MASTERCARD")) {
      Excel_dataMAin = this.nfsTTUMService.downloadMCISSATMReport(settlementBean);
      model.addAttribute("ReportName", "MASTERCARDISSUERATM_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQUIRER_DOM") && 
      settlementBean.getCategory().equalsIgnoreCase("MASTERCARD")) {
      Excel_dataMAin = this.nfsTTUMService.downloadMCISSATMReportACQ(settlementBean);
      model.addAttribute("ReportName", "MASTERCARDACQUIRER_DOM_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQUIRER_INT") && 
      settlementBean.getCategory().equalsIgnoreCase("MASTERCARD")) {
      Excel_dataMAin = this.nfsTTUMService.downloadMCISSATMReportACQINT(settlementBean);
      model.addAttribute("ReportName", "MASTERCARDACQUIRER_INT_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ISS INT POS") && 
      settlementBean.getCategory().equalsIgnoreCase("VISA")) {
      Excel_dataMAin = this.nfsTTUMService.downloadVISAISSINTPOSReport(settlementBean);
      model.addAttribute("ReportName", "VISAISSINTPOS_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ISS") && 
      settlementBean.getCategory().equalsIgnoreCase("VISA")) {
      Excel_dataMAin = this.nfsTTUMService.downloadVISAISSINTATMReport(settlementBean);
      model.addAttribute("ReportName", "VISAISS_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ISS DOM POS") && 
      settlementBean.getCategory().equalsIgnoreCase("VISA")) {
      Excel_dataMAin = this.nfsTTUMService.downloadVISAISSDOMPOSReport(settlementBean);
      model.addAttribute("ReportName", "VISAISSDOMPOS_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ISS DOM ATM") && 
      settlementBean.getCategory().equalsIgnoreCase("VISA")) {
      Excel_dataMAin = this.nfsTTUMService.downloadVISAISSDOMATMReport(settlementBean);
      model.addAttribute("ReportName", "VISAISSDOMATM_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQ DOM ATM") && 
      settlementBean.getCategory().equalsIgnoreCase("VISA_ACQ")) {
      Excel_dataMAin = this.nfsTTUMService.downloadVISAACUIRERATMReport(settlementBean);
      model.addAttribute("ReportName", "VISADOMACQUIRERATM_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQ INT ATM") && 
      settlementBean.getCategory().equalsIgnoreCase("VISA_ACQ")) {
      Excel_dataMAin = this.nfsTTUMService.downloadVISAACUIRERATMINTReport(settlementBean);
      model.addAttribute("ReportName", "VISAINTACQUIRERATM_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQUIRER") && 
      settlementBean.getCategory().equalsIgnoreCase("DFS")) {
      Excel_dataMAin = this.nfsTTUMService.downloadDFSACQReport(settlementBean);
      model.addAttribute("ReportName", "DFSACQ_Recon_Reports_" + settlementBean.getDatepicker());
    } else if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQUIRER") && 
      settlementBean.getCategory().equalsIgnoreCase("JCB")) {
      Excel_dataMAin = this.nfsTTUMService.downloadJCBACQReport(settlementBean);
      model.addAttribute("ReportName", "JCBACQ_Recon_Reports_" + settlementBean.getDatepicker());
    } else {
      Excel_dataMAin = this.nfsTTUMService.downloadIssuerDhanaReport(settlementBean);
      model.addAttribute("ReportName", "IssuerACQ_Recon_Reports_" + settlementBean.getDatepicker());
    } 
    model.addAttribute("Monthly_data", Excel_dataMAin);
    if (settlementBean.getStsubCategory().equalsIgnoreCase("Domestic") && 
      settlementBean.getCategory().equalsIgnoreCase("RUPAY"))
      return "GenerateRupayMonthly"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("INTERNATIONAL") && 
      settlementBean.getCategory().equalsIgnoreCase("RUPAY"))
      return "GenerateRupayMonthlyINT"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ISSUER") && 
      settlementBean.getCategory().equalsIgnoreCase("NFS"))
      return "GenerateNFSISSMonthlyReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("Acquirer") && 
      settlementBean.getCategory().equalsIgnoreCase("NFS"))
      return "GenerateNFSACQMonthlyReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("Acquirer") && 
      settlementBean.getCategory().equalsIgnoreCase("CARDTOCARD"))
      return "GenerateCTCACQMonthlyReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ISSUER") && 
      settlementBean.getCategory().equalsIgnoreCase("CARDTOCARD"))
      return "GenerateCTCISSMonthlyReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("Domestic") && 
      settlementBean.getCategory().equalsIgnoreCase("QSPARC"))
      return "GenerateQSPARCDOMReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ISSUER") && 
      settlementBean.getCategory().equalsIgnoreCase("ICD"))
      return "GenerateICDISSReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQUIRER") && 
      settlementBean.getCategory().equalsIgnoreCase("ICD"))
      return "GenerateICDACQReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ISSUER") && 
      settlementBean.getCategory().equalsIgnoreCase("ICCW"))
      return "GenerateICCWISSReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQUIRER") && 
      settlementBean.getCategory().equalsIgnoreCase("ICCW"))
      return "GenerateICCWACQReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ISSUER") && 
      settlementBean.getCategory().equalsIgnoreCase("MASTERCARD"))
      return "GenerateMCISSPOSReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ISSUER_ATM") && 
      settlementBean.getCategory().equalsIgnoreCase("MASTERCARD"))
      return "GenerateMCISSATMReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQUIRER_DOM") && 
      settlementBean.getCategory().equalsIgnoreCase("MASTERCARD"))
      return "GenerateMCACQATMReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQUIRER_INT") && 
      settlementBean.getCategory().equalsIgnoreCase("MASTERCARD"))
      return "GenerateMCACQATMReportINT"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ISS") && 
      settlementBean.getCategory().equalsIgnoreCase("VISA"))
      return "GenerateVISAISSINTPOSReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ISS INT ATM") && 
      settlementBean.getCategory().equalsIgnoreCase("VISA"))
      return "GenerateVISAISSINTATMReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ISS DOM POS") && 
      settlementBean.getCategory().equalsIgnoreCase("VISA"))
      return "GenerateVISAISSDOMPOSReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ISS DOM ATM") && 
      settlementBean.getCategory().equalsIgnoreCase("VISA"))
      return "GenerateVISAISSDOMATMReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQ DOM ATM") && 
      settlementBean.getCategory().equalsIgnoreCase("VISA_ACQ"))
      return "GenerateVISAACQUIRERATMReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQ INT ATM") && 
      settlementBean.getCategory().equalsIgnoreCase("VISA_ACQ"))
      return "GenerateVISAACQUIRERATMINTReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQUIRER") && 
      settlementBean.getCategory().equalsIgnoreCase("DFS"))
      return "GenerateDFSACQMonthlyReport"; 
    if (settlementBean.getStsubCategory().equalsIgnoreCase("ACQUIRER") && 
      settlementBean.getCategory().equalsIgnoreCase("JCB"))
      return "GenerateJCBACQMonthlyReport"; 
    
    
    return "GenerateNFSACQMonthlyReport";
  }
  
  public void downloadDhanaReports2(@ModelAttribute("SettlementBean") SettlementBean settlementBean, HttpServletResponse response, HttpServletRequest request, Model model) throws IOException {
    List<Object> Excel_data = new ArrayList();
    List<Object> Excel_data2 = new ArrayList();
    List<Object> data1 = new ArrayList();
    List<Object> data2 = new ArrayList();
    String fileName = "", fileName2 = "", zipName = "";
    String stPath = OUTPUT_FOLDER;
    logger.info("TEMP_DIR" + stPath);
    File folder = new File(stPath);
    GenerateUCOTTUM obj = new GenerateUCOTTUM();
    stPath = obj.checkAndMakeDirectory(settlementBean.getDatepicker().replaceAll("/", "-"), 
        settlementBean.getCategory());
    System.out.println("SettlementBean.getStsubCategory() " + settlementBean.getStsubCategory() + " cet " + 
        settlementBean.getCategory());
    if (settlementBean.getStsubCategory().equalsIgnoreCase("Acquirer") && 
      settlementBean.getCategory().equalsIgnoreCase("NFS")) {
      data1 = this.nfsTTUMService.downloadDhanaReport(settlementBean);
      data2 = this.nfsTTUMService.downloadDhanaReport2(settlementBean);
    } 
    List<String> Column_list = new ArrayList<>();
    Column_list.add("CARD_NO");
    Column_list.add("TRACE_NO");
    Column_list.add("AC_NO");
    Column_list.add("TRAN_DATE");
    Column_list.add("ATM_ID");
    Column_list.add("TYPE");
    Column_list.add("AMOUNT");
    Column_list.add("BR_CODE");
    Column_list.add("ISS_SOL_ID");
    Column_list.add("REMARKS");
    Column_list.add("DCRS_REMARKS");
    Column_list.add("FILEDATE");
    Column_list.add("CREATEDDATE");
    Excel_data.add(Column_list);
    Excel_data.add(data1);
    Column_list = new ArrayList<>();
    Column_list.add("CARD_NO");
    Column_list.add("TRACE_NO");
    Column_list.add("AC_NO");
    Column_list.add("TRAN_DATE");
    Column_list.add("ATM_ID");
    Column_list.add("TYPE");
    Column_list.add("AMOUNT");
    Column_list.add("BR_CODE");
    Column_list.add("ISS_SOL_ID");
    Column_list.add("REMARKS");
    Column_list.add("DCRS_REMARKS");
    Column_list.add("FILEDATE");
    Column_list.add("CREATEDDATE");
    Excel_data2.add(Column_list);
    Excel_data2.add(data2);
    fileName2 = "NFS_SHEET_1_" + settlementBean.getStsubCategory().replaceAll("\\s", "") + "_" + 
      settlementBean.getDatepicker().replaceAll("/", "-") + ".xls";
    fileName2 = "NFS_SHEET_2_" + settlementBean.getStsubCategory().replaceAll("\\s", "") + "_" + 
      settlementBean.getDatepicker().replaceAll("/", "-") + ".xls";
    fileName = "NFS1_" + settlementBean.getStsubCategory().replaceAll("\\s", "") + "ACQ" + "_TTUM_" + 
      settlementBean.getDatepicker().replaceAll("/", "-") + ".xls";
    fileName2 = "NFS2_" + settlementBean.getStsubCategory().replaceAll("\\s", "") + "ACQ" + "_TTUM_" + 
      settlementBean.getDatepicker().replaceAll("/", "-") + ".xls";
    zipName = "NFS_" + settlementBean.getStsubCategory().replaceAll("\\s", "") + "ACQ" + "_TTUM_" + 
      settlementBean.getDatepicker().replaceAll("/", "-") + ".zip";
    
    obj.generateExcelTTUM(stPath, fileName, Excel_data, "REPORT", zipName);
    logger.info("File is created");
    obj.generateExcelTTUM(stPath, fileName2, Excel_data2, "REPORT", zipName);
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
  
  @RequestMapping(value = {"NetworkFileUpdt"}, method = {RequestMethod.GET})
  public ModelAndView NetworkFileUpdate(ModelAndView modelAndView, NetworkFileUpdateBean settlementBean, @RequestParam("category") String category) throws Exception {
    List<String> subcat = new ArrayList<>();
    System.out.println("in GetHeaderList" + category);
    subcat = this.iSourceService.getSubcategories(category);
    modelAndView.addObject("category", category);
    modelAndView.addObject("subcategory", subcat);
    modelAndView.addObject("NetworkFileUpdateBean", settlementBean);
    modelAndView.setViewName("NetworkFileUpdate");
    return modelAndView;
  }
  
  public void DownloadReports1(@ModelAttribute("SettlementBean") SettlementBean settlementBean, HttpServletResponse response, HttpServletRequest request, Model model) throws IOException {
    logger.info("***** GenerateUnmatchedTTUM.DownloadUnmatchedTTUM post Start ****");
    logger.info("DownloadUnmatchedTTUM POST");
    String fileName = "";
    String zipName = "";
    logger.info("DownloadAdjTTUM POST");
    List<Object> Excel_data = new ArrayList();
    List<Object> TTUMData = new ArrayList();
    List<Object> TTUMData2 = new ArrayList();
    List<Object> PBGB_TTUMData = new ArrayList();
    boolean executed = false;
    Excel_data = this.nfsTTUMService.downloadDhanaReport(settlementBean);
    fileName = "RUPAY_REFUND__TTUM.txt";
    String stPath = OUTPUT_FOLDER;
    logger.info("TEMP_DIR" + stPath);
    GenerateUCOTTUM obj = new GenerateUCOTTUM();
    stPath = obj.checkAndMakeDirectory(settlementBean.getDatepicker().replaceAll("/", "-"), 
        settlementBean.getCategory());
    logger.info("File is created");
    List<String> Column_list = new ArrayList<>();
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
  }
  
  @RequestMapping(value = {"checkfileprocessed"}, method = {RequestMethod.POST})
  @ResponseBody
  public String checkfileProcess(@RequestParam("category") String category, FileDetailsJson dataJson, ModelAndView modelAndView, CompareSetupBean setupBean, HttpSession httpSession, @RequestParam("filedate") String filedate, @RequestParam("subCat") String subCat, @RequestParam("path") String stPath, HttpServletResponse response, HttpServletRequest request) {
    try {
      SettlementBean settlementBean = new SettlementBean();
      settlementBean.setCategory(category);
      settlementBean.setStsubCategory(subCat);
      settlementBean.setDatepicker(filedate);
      boolean check_process = false;
     if (settlementBean.getCategory().contains("CARDTOCARD")) {
        check_process = this.isettelmentservice.checkfileprocessedCTC(settlementBean).booleanValue();
        if (check_process)
          return "success"; 
        return "Recon not processed for selected date";
      }    if (settlementBean.getCategory().contains("DFS")) {
          check_process = this.isettelmentservice.checkfileprocessedCTC4(settlementBean).booleanValue();
          if (check_process)
            return "success"; 
          return "Recon not processed for selected date";
		}
		if (settlementBean.getCategory().contains("JCB")) {
			check_process = this.isettelmentservice.checkfileprocessedCTC3(settlementBean).booleanValue();
			if (check_process)
				return "success";
			return "Recon not processed for selected date";
		} else {
		    
		      check_process = this.isettelmentservice.checkfileprocessed(settlementBean).booleanValue();
		      if (check_process)
		        return "success"; 
		      return "Recon not processed for selected date";

			
		}
        
        }

    
    
     catch (Exception e) {
      return "Exception";
    } 
  }
  
  @RequestMapping(value = {"DownloadReports"}, method = {RequestMethod.POST})
  @ResponseBody
  public void downloadReports(@ModelAttribute("SettlementBean") SettlementBean settlementBean, HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes) {
    String TEMP_DIR = System.getProperty("java.io.tmpdir");
    ServletContext context = request.getServletContext();
    try {
      logger.info("INSIDE DOWNLOAD REPORTS");
      String stpath = String.valueOf(TEMP_DIR) + File.separator + settlementBean.getCategory();
      System.out.println("stpath is " + stpath);
      this.isettelmentservice.DeleteFiles(stpath);
      settlementBean.setStPath(stpath);
      if (!settlementBean.getStsubCategory().equals("-")) {
        settlementBean.setStMergerCategory(
            String.valueOf(settlementBean.getCategory()) + "_" + settlementBean.getStsubCategory().substring(0, 3));
      } else {
        settlementBean.setStMergerCategory(settlementBean.getCategory());
      } 
      if (settlementBean.getCategory().equals("CARDTOCARD")) {
        String path = settlementBean.getStPath();
        File myFile = new File(String.valueOf(settlementBean.getStPath()) + File.separator);
        System.out.println("myFile" + myFile);
      } 
      this.isettelmentservice.generate_Reports(settlementBean);
      String stFilename = settlementBean.getStMergerCategory();
      File file = new File(String.valueOf(settlementBean.getStPath()) + File.separator + "REPORTS.zip");
      logger.info("path of zip file " + settlementBean.getStPath() + File.separator + "REPORTS.zip");
      FileInputStream inputstream = new FileInputStream(file);
      response.setContentLength((int)file.length());
      logger.info("before downloading zip file ");
      response.setContentType("application/zip");
      logger.info("download completed");
      String headerKey = "Content-Disposition";
      String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
      response.setHeader(headerKey, headerValue);
      ServletOutputStream servletOutputStream = response.getOutputStream();
      IOUtils.copy(inputstream, (OutputStream)servletOutputStream);
      response.flushBuffer();
      Date varDate = null;
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
      try {
        varDate = dateFormat.parse(settlementBean.getDatepicker());
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        System.out.println("Date :" + dateFormat.format(varDate));
        settlementBean.setDatepicker(dateFormat.format(varDate));
      } catch (Exception e) {
        e.printStackTrace();
      } 
    } catch (Exception e) {
      logger.info("Exception in downloadReports " + e);
      System.out.println("Exception in downloadReports " + e);
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
    } 
  }
  
  @RequestMapping(value = {"/GetChargeback"}, method = {RequestMethod.POST}, produces = {"application/json"})
  @ResponseBody
  public String GetChargeback(@RequestParam("ArnNo") String arnNo, HttpServletRequest request) {
    String jsonArray = "";
    try {
      LoginBean loginBean = null, loginBean1 = (LoginBean)request.getSession().getAttribute("loginBean");
      HashMap<String, Object> JSONROOT = new HashMap<>();
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      List<Mastercard_chargeback> list = this.isettelmentservice.getMastercardchargeback(arnNo);
      JSONROOT.put("Result", "OK");
      JSONROOT.put("Records", list);
      jsonArray = gson.toJson(JSONROOT);
      System.out.println(jsonArray);
      return jsonArray;
    } catch (Exception e) {
      e.printStackTrace();
      return jsonArray;
    } 
  }
  
  @RequestMapping(value = {"/Savemastercardchargebk"}, method = {RequestMethod.POST})
  @ResponseBody
  public String Savemastercard_chargebk(@RequestParam("Microfilmid") String microfilm, @RequestParam("Refid") String refid, @RequestParam("Settlmentamount") String settlmentamount, @RequestParam("SettlmentCurrid") String settlmentCurrid, @RequestParam("Txnamountid") String txnamountid, @RequestParam("Txtcurrencyid") String txtcurrencyid, @RequestParam("Reason") String reason, @RequestParam("Dcosid") String dcosid, @RequestParam("Remrk") String remrk, HttpServletRequest request) throws IOException {
    int message = 0;
    try {
      message = this.isettelmentservice.Savechargeback(microfilm, refid, settlmentamount, settlmentCurrid, txnamountid, 
          txtcurrencyid, reason, dcosid, remrk);
    } catch (Exception e) {
      e.printStackTrace();
      return "Fail";
    } 
    return "Success";
  }
  
  @RequestMapping(value = {"GenerateMastercardChargebk"}, method = {RequestMethod.POST})
  public void GenerateMastercardChargebk(HttpServletRequest request, HttpServletResponse response, Model model) {
    String arn1 = null;
    String reason1 = null;
    String arndate = null;
    reason1 = request.getParameter("Reasonval");
    arn1 = request.getParameter("Arn");
    arndate = request.getParameter("Arndate");
    String filepath = System.getProperty("catalina.home");
    ServletOutputStream sou = null;
    try {
      String getFile = this.isettelmentservice.generateChargBk(arn1, reason1, arndate);
      System.out.println("File Received");
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment; filename= " + getFile);
      FileInputStream ins = new FileInputStream(new File(getFile));
      sou = response.getOutputStream();
      sou.write(IOUtils.toByteArray(ins));
      ins.close();
      sou.flush();
      sou.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        (new File(filepath)).delete();
      } catch (Exception exception) {}
    } 
  }
  
  @RequestMapping(value = {"/Get_glbalance"}, method = {RequestMethod.POST}, produces = {"application/json"})
  @ResponseBody
  public String Get_glbalance(@RequestParam("Date_val") String filedate, @RequestParam("Date_val2") String filedate2, HttpServletRequest request, HttpServletResponse response) {
    String jsonArray = "";
    try {
      LoginBean loginBean = null, loginBean1 = (LoginBean)request.getSession().getAttribute("loginBean");
      List<Gl_bean> list = null;
      HashMap<String, Object> JSONROOT = new HashMap<>();
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
      Date date1 = sdf.parse(filedate);
      Date date2 = sdf.parse(filedate2);
      System.out.println("date1 : " + sdf.format(date1));
      System.out.println("date2 : " + sdf.format(date2));
      Calendar start = Calendar.getInstance();
      start.setTime(date1);
      Calendar end = Calendar.getInstance();
      end.setTime(date2);
      int countval = 0;
      while (!start.after(end)) {
        Date targetDay = start.getTime();
        System.out.println("targetDay-->" + targetDay);
        String newDate = sdf.format(start.getTime());
        System.out.println("o/p-->" + newDate);
        start.add(5, 1);
        list = this.isettelmentservice.getMastercardGet_glbalance(newDate);
      } 
      List<Rupay_sur_GlBean> list1 = this.isettelmentservice.getRupaysurchargelist(filedate);
      List<Rupay_gl_autorev> list2 = this.isettelmentservice.getRupayAutorevlist(filedate);
      List<Rupay_gl_Lpcases> list3 = this.isettelmentservice.getRupayLpcaselist(filedate);
      JSONROOT.put("Result", "OK");
      JSONROOT.put("Records", list);
      JSONROOT.put("Records1", list1);
      JSONROOT.put("Records2", list2);
      JSONROOT.put("Records3", list3);
      jsonArray = gson.toJson(JSONROOT);
      System.out.println(jsonArray);
      return jsonArray;
    } catch (Exception e) {
      e.printStackTrace();
      return jsonArray;
    } 
  }
  
  @RequestMapping(value = {"/Get_Settlemntdtamnt"}, method = {RequestMethod.POST})
  public ResponseEntity Get_Settlemntdtamnt(HttpServletRequest request, HttpServletResponse response) {
    String jsonArray = "";
    String message = null;
    try {
      LoginBean loginBean = null, loginBean1 = (LoginBean)request.getSession().getAttribute("loginBean");
      String settlementAmount = request.getParameter("settlementAmount");
      String settlementDate = request.getParameter("settlementDate");
      HashMap<String, Object> JSONROOT = new HashMap<>();
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      message = this.isettelmentservice.getSettlemntAmount(settlementDate, settlementAmount);
      return new ResponseEntity(message, HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
    } 
  }
  
  @RequestMapping(value = {"/Save"}, method = {RequestMethod.POST})
  @ResponseBody
  public String updateAuditReport(Model model, HttpServletRequest request, HttpSession session) {
    try {
      String[] surcharge = request.getParameterValues("surchargeamount[]");
      String[] autorev = request.getParameterValues("autorevamount[]");
      String[] lpcase = request.getParameterValues("lpcases[]");
      String settlementmatch = request.getParameter("settlementmatch");
      String nxtdate = request.getParameter("nxtdate");
      String surcharge1 = request.getParameter("surcharge");
      String lpcase2 = request.getParameter("lpcase");
      String cbs_unrecon = request.getParameter("cbs_unrecon");
      String switch_unrecon = request.getParameter("switchunrecon");
      String surch_total = request.getParameter("surchtotal");
      String autorev_total = request.getParameter("autorevtotal");
      String lpcasetotal = request.getParameter("lpcasetotal");
      String nobase2 = request.getParameter("nobase2");
      String settlementTotal = request.getParameter("settlementTotal");
      String closingbal = request.getParameter("closingbal");
      String settlementid = request.getParameter("settlementid");
      String finaltotal = request.getParameter("finaltotal");
      String dateval = request.getParameter("dateval");
      String diff = request.getParameter("diff");
      System.out.println(Arrays.toString((Object[])surcharge));
      System.out.println(diff);
      int i = this.isettelmentservice.SaveGl(closingbal, settlementid, diff, cbs_unrecon, switch_unrecon, 
          nobase2, settlementmatch, nxtdate, surcharge1, settlementTotal, lpcase2, 
          Arrays.toString((Object[])surcharge).replaceAll("\\[", "").replaceAll("\\]", "").replace(",", ""), 
          surch_total, Arrays.toString((Object[])autorev).replaceAll("\\[", "").replaceAll("\\]", "").replace(",", ""), 
          autorev_total, Arrays.toString((Object[])lpcase).replaceAll("\\[", "").replaceAll("\\]", "").replace(",", ""), 
          lpcasetotal, finaltotal, dateval);
    } catch (Exception e) {
      e.printStackTrace();
      return "Error";
    } 
    System.out.println("Insiee");
    return "Success";
  }
  
  @RequestMapping(value = {"Download_Repo"}, method = {RequestMethod.POST})
  public String Download_Repo(@RequestParam("dateval") String dateval, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    String[] startDate = null;
    String[] endDate = null;
    String startDatePicker = null;
    String endDatePicker = null;
    System.out.println("Inside method");
    List<List<Rupay_Gl_repo>> report1 = this.isettelmentservice.GenerateGL(dateval);
    model.addAttribute("report1", report1);
    return "GenerateRepoExcel";
  }
  
  @RequestMapping(value = {"CTCSettlementProcess"}, method = {RequestMethod.GET})
  public ModelAndView CTCSettlementProcessGet(ModelAndView modelAndView, @RequestParam("category") String category, HttpServletRequest request) throws Exception {
    logger.info("***** CTCSettlementProcess.Get Start ****");
    RupayUploadBean rupaySettlementBean = new RupayUploadBean();
    logger.info("RupayFileUpload GET");
    String display = "";
    logger.info("in GetHeaderList" + category);
    modelAndView.addObject("category", category);
    modelAndView.addObject("rupaySettlementBean", rupaySettlementBean);
    modelAndView.setViewName("CTCSettlementProcess");
    logger.info("***** CTCSettlementProcess.CTCSettlementProcess GET End ****");
    return modelAndView;
  }
  
  @RequestMapping(value = {"checkTTUMProcessedCTCSETTLEMENT"}, method = {RequestMethod.POST})
  @ResponseBody
  public String checkTTUMProcessedCTCSETTLEMENT(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response, HttpServletRequest request) {
    try {
      logger.info("TTUMController: checkTTUMProcessedCTCSETTLEMENT: Entry");
      HashMap<String, Object> output = this.rupayTTUMService.checkTTUMProcessedCTCSETTLEMENT(beanObj);
      if (output != null && ((Boolean)output.get("result")).booleanValue())
        return "TTUM is not processed for selected date"; 
      return "success";
    } catch (Exception e) {
      logger.info("Exception is " + e);
      return "Exception";
    } 
  }
  
  @RequestMapping(value = {"DownloadUnmatchedTTUMCTCSETTLEMENT"}, method = {RequestMethod.POST})
  @ResponseBody
  public void DownloadUnmatchedTTUMCTCSETTLEMENT(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
    logger.info("***** GenerateUnmatchedTTUM.DownloadUnmatchedTTUMCTCSETTLEMENT post Start ****");
    logger.info("DownloadUnmatchedTTUMCTCSETTLEMENT POST");
    String Createdby = ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    logger.info("Created by is " + Createdby + " " + beanObj.getTypeOfTTUM() + " " + beanObj.getStSubCategory() + 
        " " + beanObj.getCategory() + "  " + beanObj.getLocalDate());
    String fileName = "", zipName = "";
    if (beanObj.getTTUMCategory().contains("REPORT")) {
      List<Object> Excel_data = new ArrayList();
      List<Object> TTUMData = new ArrayList();
      List<Object> TTUMData2 = new ArrayList();
      List<Object> PBGB_TTUMData = new ArrayList();
      logger.info("Created by is " + Createdby);
      beanObj.setCreatedBy(Createdby);
      logger.info("File Name is " + beanObj.getFileName());
      boolean executed = false;
      TTUMData = this.SETTLTTUMSERVICE.getTTUMCTCSETTLEMENT(beanObj);
      String stPath = OUTPUT_FOLDER;
      logger.info("TEMP_DIR" + stPath);
      GenerateUCOTTUM obj = new GenerateUCOTTUM();
      stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
      logger.info("File is created");
      List<String> Column_list = new ArrayList<>();
      Map<String, String> table_Data = new HashMap<>();
      Column_list.add("DR_CR");
      Column_list.add("ACCOUNT_NO");
      Column_list.add("BUSINESS_DATE");
      Column_list.add("CARD_NO");
      Column_list.add("TRACE_NO");
      Column_list.add("AC_NO");
      Column_list.add("TRAN_DATE");
      Column_list.add("AMOUNT");
      Column_list.add("POSTED_DATE");
      Column_list.add("TR_NO");
      Column_list.add("FILEDATE");
      Excel_data.add(Column_list);
      Excel_data.add(TTUMData);
      System.out.println("filename in nfs ttum is " + fileName);
      fileName = "CTCSETTLEMENTREPORT" + beanObj.getLocalDate() + ".xls";
      zipName = "CTCSETTLEMENTREPORT" + beanObj.getLocalDate() + ".zip";
      obj.generateExcelTTUMSettlement(stPath, fileName, Excel_data,"CTC_"+  beanObj.getLocalDate().replaceAll("/", "-"), zipName);
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
      TTUMData = this.SETTLTTUMSERVICE.getVISATTUMCTCSETTLEMENTTTUM(beanObj);
      System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
      fileName = "CTCAETTLEMENTTTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
      String stPath = OUTPUT_FOLDER;
      logger.info("TEMP_DIR" + stPath);
      GenerateUCOTTUM obj = new GenerateUCOTTUM();
      stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
      obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData, "VISA", beanObj);
      zipName = "CTCAETTLEMENTTTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
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
