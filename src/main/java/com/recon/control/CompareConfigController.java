package com.recon.control;


import com.recon.model.CompareBean;
import com.recon.model.CompareSetupBean;
import com.recon.model.FileColumnDtls;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.model.LoginBean;
import com.recon.model.ManualCompareBean;
import com.recon.model.ManualFileColumnDtls;
import com.recon.model.SettlementBean;
import com.recon.service.CompareService;
import com.recon.service.FilterationService;
import com.recon.service.ICompareConfigService;
import com.recon.service.NFSSettlementCalService;
import com.recon.service.NFSSettlementService;
import com.recon.service.NFSUnmatchTTUMService;
import com.recon.service.SettlementTTUMService;
import com.recon.util.CSRFToken;
import com.recon.util.ViewFiles;
import com.recon.util.demo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CompareConfigController {
  private static final String ERROR_MSG = "error_msg";
  
  private static final String SUCCESS_MSG = "success_msg";
  
  private static final Logger logger = Logger.getLogger(com.recon.control.CompareConfigController.class);
  
  @Autowired
  SettlementTTUMService SETTLTTUMSERVICE;
  
  @Autowired
  NFSUnmatchTTUMService nfsTTUMService;
  
  @Autowired
  ICompareConfigService icompareConfigService;
  
  @Autowired
  CompareService compareService;
  
  @Autowired
  FilterationService filterationService;
  
  @Autowired
  NFSSettlementService nfsSettlementService;
  
  @Autowired
  NFSSettlementCalService nfsSettlementCalService;
  
  @RequestMapping(value = {"Compare"}, method = {RequestMethod.GET})
  public ModelAndView Configuration(ModelAndView modelAndView) {
    modelAndView.setViewName("CompareMenu");
    return modelAndView;
  }
  
  @RequestMapping(value = {"CompareSetup"}, method = {RequestMethod.GET})
  public ModelAndView comparesetup(Model model, CompareSetupBean compareSetupBean) {
    logger.info("***** CompareConfigController.comparesetup Start ****");
    List<CompareSetupBean> file_list = new ArrayList<>();
    file_list = this.icompareConfigService.getFileDetails();
    List<CompareSetupBean> setup_dtl_list = new ArrayList<>();
    for (int i = setup_dtl_list.size() + 1; i <= 1; i++)
      setup_dtl_list.add(new CompareSetupBean()); 
    List<FileColumnDtls> columnDtls = new ArrayList<>();
    for (int j = columnDtls.size() + 1; j <= 1; j++)
      columnDtls.add(new FileColumnDtls()); 
    compareSetupBean.setColumnDtls(columnDtls);
    compareSetupBean.setSetup_dtl_list(setup_dtl_list);
    ModelAndView mav = new ModelAndView("CompareSetup");
    mav.addObject("setup_dtl_list", setup_dtl_list);
    mav.addObject("file_list", file_list);
    mav.addObject("CompareSetupBean", compareSetupBean);
    mav.addObject("columnDtls", columnDtls);
    logger.info("***** CompareConfigController.comparesetup End ****");
    return mav;
  }
  
  @RequestMapping(value = {"viewSwitchDashboard"}, method = {RequestMethod.GET})
  public ModelAndView viewSwitchDashboard(ModelAndView modelAndView, HttpServletRequest request, SettlementBean settlementBean) throws Exception {
    modelAndView.addObject("SettlementBean", settlementBean);
    modelAndView.setViewName("viewSwitchDashboard");
    return modelAndView;
  }
  
  @RequestMapping(value = {"viewCBSDashboard"}, method = {RequestMethod.GET})
  public ModelAndView viewCBSDashboard(ModelAndView modelAndView, HttpServletRequest request, SettlementBean settlementBean) throws Exception {
    modelAndView.addObject("SettlementBean", settlementBean);
    modelAndView.setViewName("viewCBSDashboard");
    return modelAndView;
  }
  
  @RequestMapping(value = {"viewRowDataDashboard"}, method = {RequestMethod.GET})
  public ModelAndView viewRowDataDashboard(ModelAndView modelAndView, HttpServletRequest request, SettlementBean settlementBean) throws Exception {
    modelAndView.addObject("SettlementBean", settlementBean);
    modelAndView.setViewName("viewRowDataDashboard");
    return modelAndView;
  }
  
  @RequestMapping(value = {"ViewCompareSetup"}, method = {RequestMethod.GET})
  public ModelAndView viewcomparesetup(Model model, CompareSetupBean compareSetupBean, ModelAndView modelAndView) {
    modelAndView.setViewName("ViewCompareSetup");
    modelAndView.addObject("CompareSetupBean", compareSetupBean);
    return modelAndView;
  }
  
  @RequestMapping(value = {"searchswitchViewFile"}, method = {RequestMethod.POST})
  @ResponseBody
  public List<ViewFiles> searchswitchViewFile(@RequestParam String txntype, @RequestParam String fromDate) throws SQLException, Exception {
	  System.out.println("data "+txntype + " "+  fromDate);
    return this.nfsTTUMService.searchSwitchViewFile1(txntype, fromDate);
  }
  
  @RequestMapping(value = {"searchCBSViewFile"}, method = {RequestMethod.POST})
  @ResponseBody
  public List<ViewFiles> searchCBSViewFile(@RequestParam String txntype, @RequestParam String fromDate) throws SQLException, Exception {
    return this.nfsTTUMService.searchCBSViewFile1(txntype, fromDate);
  }
  
  @RequestMapping(value = {"searchRowDataViewFile"}, method = {RequestMethod.POST})
  @ResponseBody
  public List<ViewFiles> searchRowDataViewFile(@RequestParam String txntype, @RequestParam String fromDate) throws SQLException, Exception {
    return this.nfsTTUMService.searchRowDataViewFile1(txntype, fromDate);
  }
  
  @RequestMapping(value = {"mapswitchdashboard"}, method = {RequestMethod.GET})
  public ModelAndView mapswitchdashboard(ModelAndView modelAndView, HttpServletRequest request, SettlementBean settlementBean) throws Exception {
    modelAndView.addObject("SettlementBean", settlementBean);
    modelAndView.setViewName("mapswitchdashboard");
    return modelAndView;
  }
  
  @RequestMapping(value = {"mapcbsdashboard"}, method = {RequestMethod.GET})
  public ModelAndView mapcbsdashboard(ModelAndView modelAndView, HttpServletRequest request, SettlementBean settlementBean) throws Exception {
    modelAndView.addObject("SettlementBean", settlementBean);
    modelAndView.setViewName("mapcbsdashboard");
    return modelAndView;
  }
  
  @RequestMapping(value = {"viewDashboard"}, method = {RequestMethod.GET})
  public ModelAndView viewDashboard(ModelAndView modelAndView, HttpServletRequest request, SettlementBean settlementBean) throws Exception {
    modelAndView.addObject("SettlementBean", settlementBean);
    modelAndView.setViewName("viewDashboard");
    return modelAndView;
  }
  
  @RequestMapping(value = {"deleteDashboard"}, method = {RequestMethod.GET})
  public ModelAndView deleteDashboard(ModelAndView modelAndView, HttpServletRequest request, SettlementBean settlementBean) throws Exception {
    modelAndView.addObject("SettlementBean", settlementBean);
    modelAndView.setViewName("deleteDashboard");
    return modelAndView;
  }
  
  @RequestMapping(value = {"deleteViewFile"}, method = {RequestMethod.POST})
  @ResponseBody
  public String deleteViewFile(@RequestParam String type, @RequestParam String fromDate, @RequestParam String username, @RequestParam String filename, @RequestParam String cycle) throws SQLException, Exception {
    logger.info("cycle " + cycle + "fromDate" + fromDate + "username " + username + "filename " + filename + "type " + 
        type);
    try {
      if (username.equals("") || fromDate.equals("") || filename.equals(""))
        return "01"; 
      boolean checkDelete = this.nfsTTUMService.deleteViewFile(type, fromDate, username, filename, cycle);
      if (checkDelete)
        return "02"; 
      return "00";
    } catch (Exception e) {
      logger.info("Exception in downloadReports " + e);
      System.out.println("Exception in downloadReports " + e);
      return "02";
    } 
  }
  
  @RequestMapping(value = {"mapSwitchD"}, method = {RequestMethod.POST})
  @ResponseBody
  public String mapSwitchD(@RequestParam String Date) throws SQLException, Exception {
    logger.info(" Date " + Date);
    try {
      HashMap<String, Object> output = this.SETTLTTUMSERVICE.mapSwitchDATA(Date);
      System.out.println("output " + output.get("MSG").toString());
      if (output != null && ((Boolean)output.get("result")).booleanValue())
        return output.get("MSG").toString(); 
      return output.get("MSG").toString();
    } catch (Exception e) {
      logger.info("Exception in downloadReports " + e);
      System.out.println("Exception in downloadReports " + e);
      return "FAILED";
    } 
  }
  
  @RequestMapping(value = {"mapcbsD"}, method = {RequestMethod.POST})
  @ResponseBody
  public String mapcbsD(@RequestParam String Date) throws SQLException, Exception {
    logger.info(" Date " + Date);
    try {
      HashMap<String, Object> output = this.SETTLTTUMSERVICE.mapSwitchDATA1(Date);
      System.out.println("output " + output.toString());
      if (output != null && ((Boolean)output.get("result")).booleanValue())
        return output.get("MSG").toString(); 
      return output.get("MSG").toString();
    } catch (Exception e) {
      logger.info("Exception in downloadReports " + e);
      System.out.println("Exception in downloadReports " + e);
      return "FAILED";
    } 
  }
  
  
  @RequestMapping(value = {"increaseCount"}, method = {RequestMethod.GET})
  public ModelAndView increaseCount(ModelAndView modelAndView, HttpServletRequest request, SettlementBean settlementBean) throws Exception {
    modelAndView.addObject("SettlementBean", settlementBean);
    modelAndView.setViewName("increaseCount");
    return modelAndView;
  }
  
  @RequestMapping(value = {"increaseCountViewFile"}, method = {RequestMethod.POST})
  @ResponseBody
  public String increaseFileCount(@RequestParam String type, @RequestParam String fromDate, @RequestParam String username, @RequestParam String cycle) throws SQLException, Exception {
    logger.info("cycle " + cycle + "fromDate" + fromDate + "username " + username + "type " + type);
    try {
      if (username.equals("") || fromDate.equals(""))
        return "01"; 
      boolean checkDelete = this.nfsTTUMService.increaseViewFile(type, fromDate, username, cycle);
      if (checkDelete)
        return "02"; 
      return "00";
    } catch (Exception e) {
      logger.info("Exception in downloadReports " + e);
      System.out.println("Exception in downloadReports " + e);
      return "02";
    } 
  }
  
  @RequestMapping(value = {"searchViewFile"}, method = {RequestMethod.POST})
  @ResponseBody
  public List<ViewFiles> searchViewFile(@RequestParam String type, @RequestParam String fromDate) throws SQLException, Exception {

    return this.nfsTTUMService.searchViewFile(type, fromDate);
  }
  
  @RequestMapping(value = {"AutoCompare"}, method = {RequestMethod.GET})
  public ModelAndView AutoCompare(Model model, CompareSetupBean compareSetupBean, ModelAndView modelAndView) {
    modelAndView.setViewName("AutoCompare");
    modelAndView.addObject("CompareSetupBean", compareSetupBean);
    return modelAndView;
  }
  
  @RequestMapping(value = {"ManualCompareSetup"}, method = {RequestMethod.GET})
  public ModelAndView ManualCompareSetup(Model model, ManualCompareBean manualCompBean, ModelAndView modelAndView, HttpSession httpSession) {
    logger.info("***** CompareConfigController.ManualCompareSetup Start ****");
    ((LoginBean)httpSession.getAttribute("loginBean")).getUser_id();
    List<CompareSetupBean> file_list = new ArrayList<>();
    file_list = this.icompareConfigService.getFileDetails();
    List<ManualCompareBean> comp_dtl_list = new ArrayList<>();
    for (int i = comp_dtl_list.size() + 1; i <= 1; i++)
      comp_dtl_list.add(new ManualCompareBean()); 
    manualCompBean.setComp_dtl_list(comp_dtl_list);
    List<ManualFileColumnDtls> columnDtls = new ArrayList<>();
    for (int j = columnDtls.size() + 1; j <= 1; j++)
      columnDtls.add(new ManualFileColumnDtls()); 
    manualCompBean.setColumnDtls(columnDtls);
    ModelAndView mav = new ModelAndView("ManualCompareSetup");
    mav.addObject("file_list", file_list);
    mav.addObject("ManualCompBean", manualCompBean);
    mav.addObject("comp_dtl_list", comp_dtl_list);
    mav.addObject("columnDtls", columnDtls);
    logger.info("***** CompareConfigController.ManualCompareSetup End ****");
    return mav;
  }
  
  @RequestMapping(value = {"ManualUpload"}, method = {RequestMethod.GET})
  public ModelAndView ManualUpload(Model model, CompareSetupBean compareSetupBean, ModelAndView modelAndView, HttpSession httpSession, HttpServletRequest request) throws Exception {
    logger.info("***** CompareConfigController.ManualUpload start ****");
    try {
      compareSetupBean.setCreatedBy(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
      ArrayList<CompareSetupBean> setupBeanslist = null;
      setupBeanslist = this.icompareConfigService.getFileList();
      String csrf = CSRFToken.getTokenForSession(request.getSession());
      modelAndView.addObject("CSRFToken", csrf);
      model.addAttribute("configBeanlist", setupBeanslist);
      modelAndView.setViewName("ManualUpload");
      modelAndView.addObject("CompareSetupBean", compareSetupBean);
      logger.info("***** CompareConfigController.ManualUpload End ****");
      return modelAndView;
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigController.ManualUpload");
      logger.error(" error in CompareConfigController.ManualUpload", 
          new Exception("CompareConfigController.ManualUpload", ex));
      modelAndView.setViewName("Login");
      return modelAndView;
    } 
  }
  
  @RequestMapping(value = {"ManualCompare"}, method = {RequestMethod.GET})
  public ModelAndView ManualCompare(Model model, CompareSetupBean compareSetupBean, ModelAndView modelAndView, HttpSession httpSession) throws Exception {
    logger.info("***** CompareConfigController.ManualCompare Start ****");
    try {
      compareSetupBean.setCreatedBy(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
      ArrayList<CompareSetupBean> setupBeanslist = null;
      setupBeanslist = this.icompareConfigService.getFileList();
      model.addAttribute("configBeanlist", setupBeanslist);
      modelAndView.setViewName("ManualCompare");
      modelAndView.addObject("CompareSetupBean", compareSetupBean);
      logger.info("***** CompareConfigController.ManualCompare End ****");
      return modelAndView;
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigController.ManualCompare");
      logger.error(" error in CompareConfigController.ManualCompare", 
          new Exception("CompareConfigController.ManualCompare", ex));
      modelAndView.setViewName("Login");
      return modelAndView;
    } 
  }
  
  @RequestMapping(value = {"manualUploadFile"}, method = {RequestMethod.POST})
  public ResponseEntity UploadFile(@ModelAttribute("CompareSetupBean") CompareSetupBean setupBean, HttpServletRequest request, @RequestParam("file") MultipartFile file, String filename, String fileType, String category, String stSubCategory, String stSubCategoryid, String fileDate, HttpSession httpSession, Model model, ModelAndView modelAndView, RedirectAttributes redirectAttributes) throws Exception {
    String phyFileName = "";
    logger.info("***** CompareConfigController.UploadFile Start ****" + stSubCategoryid);
    int fileid = setupBean.getInFileId();
    System.out.println(fileid);
    phyFileName = file.getOriginalFilename();
    System.out.println("Physical File name::>" + phyFileName);
    setupBean.setP_FILE_NAME(phyFileName);
    setupBean.setFileType(fileType);
    setupBean.setFilename(filename);
    setupBean.setCategory(category);
    setupBean.setStSubCategory(stSubCategory);
    setupBean.setFileDate(fileDate);
    setupBean.setCreatedBy(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
    System.out.println("filenamep" + setupBean.getFilename() + "cat " + setupBean.getCategory() + " sub cat " + 
        setupBean.getStSubCategory());
    try {
      if (this.icompareConfigService.chkFileupload(setupBean)) {
        if (filename.contains("SWITCH") || filename.contains("CBS") || filename.contains("RUPAY") || 
          filename.contains("NFS") || filename.contains("ICD") || filename.contains("JCB") || 
          filename.contains("DFS") || setupBean.getCategory().contains("MASTERCARD") || filename.contains("QSPARC") || 
          filename.contains("VISA")) {
          HashMap<String, Object> output = this.icompareConfigService.uploadFile(setupBean, file);
          boolean result = ((Boolean)output.get("result")).booleanValue();
          if (result)
            try {
              if (output.containsKey("msg")) {
                String msg = (String)output.get("msg");
                return new ResponseEntity("File Uploaded Successfuly!!", HttpStatus.OK);
              } 
              return new ResponseEntity("File Uploaded Successfuly!!", HttpStatus.OK);
            } catch (Exception e) {
              logger.error("Exception while getting msg from hashmap");
              return new ResponseEntity("File Uploaded Successfuly!! ", HttpStatus.OK);
            }  
          if (output.containsKey("msg")) {
            logger.info("Inside else code " + output.containsKey("msg"));
            String msg = (String)output.get("msg");
            return new ResponseEntity("File not Uploaded!!", HttpStatus.OK);
          } 
          return new ResponseEntity("File not Uploaded!!", HttpStatus.OK);
        } 
        if (setupBean.getFilename().equals("REV_REPORT")) {
          logger.info("Inside REV_REPORT method  ");
          HashMap<String, Object> output = this.icompareConfigService.uploadREV_File(setupBean, file);
          if (output != null && ((Boolean)output.get("result")).booleanValue()) {
            String count = output.get("msg").toString();
            logger.info("recordcount==" + count);
            return new ResponseEntity("File Uploaded Successfuly!! \n Total Record Count : " + count, 
                HttpStatus.OK);
          } 
          logger.info("msg is " + output.get("msg").toString());
          return new ResponseEntity(output.get("msg").toString(), HttpStatus.OK);
        } 
        if (setupBean.getFilename().equals("MC-TA")) {
          logger.info("Inside REV_REPORT method  ");
          HashMap<String, Object> output = this.icompareConfigService.uploadREV_File2(setupBean, file);
          if (output != null && ((Boolean)output.get("result")).booleanValue()) {
            String count = output.get("msg").toString();
            int recordcount = this.icompareConfigService.getREVrecordcount(setupBean);
            logger.info("recordcount==" + count);
            return new ResponseEntity(
                "File Uploaded Successfuly!! \n Total Record Count : " + recordcount, 
                HttpStatus.OK);
          } 
          logger.info("msg is " + output.get("msg").toString());
          return new ResponseEntity(output.get("msg").toString(), HttpStatus.OK);
        } 
      } else {
        return new ResponseEntity("File Already Uploaded!!", 
            HttpStatus.OK);
      } 
    } catch (Exception e) {
      return new ResponseEntity(e.getMessage(), HttpStatus.OK);
    } 
    return null;
  }
  
  @RequestMapping(value = {"classifyFile"}, method = {RequestMethod.GET})
  public ModelAndView ClassifyFile(@ModelAttribute("CompareSetupBean") CompareSetupBean setupBean, HttpServletRequest request, HttpSession httpSession, Model model, ModelAndView modelAndView, RedirectAttributes redirectAttributes) throws Exception {
    logger.info("***** CompareConfigController.ClassifyFile Start ****");
    try {
      setupBean.setCreatedBy(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
      ArrayList<CompareSetupBean> setupBeanslist = null;
      setupBeanslist = this.icompareConfigService.getFileList();
      model.addAttribute("configBeanlist", setupBeanslist);
      modelAndView.setViewName("classifyFile");
      modelAndView.addObject("CompareSetupBean", setupBean);
      logger.info("***** CompareConfigController.ClassifyFile End ****");
      return modelAndView;
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigController.ClassifyFile");
      logger.error(" error in CompareConfigController.ClassifyFile", 
          new Exception("CompareConfigController.ClassifyFile", ex));
      modelAndView.setViewName("Login");
      return modelAndView;
    } 
  }
  
  @RequestMapping(value = {"ManualclassifyFile"}, method = {RequestMethod.POST})
  public String manualClassifyFile(@ModelAttribute("CompareSetupBean") CompareSetupBean setupBean, HttpServletRequest request, HttpSession httpSession, Model model, ModelAndView modelAndView, RedirectAttributes redirectAttributes) throws Exception {
    logger.info("***** CompareConfigController.manualClassifyFile Start ****");
    try {
      String date = setupBean.getFileDate();
      int fileid = setupBean.getInFileId();
      logger.info(date);
      logger.info(Integer.valueOf(fileid));
      setupBean.setCreatedBy(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
      if (!setupBean.getStFileName().equalsIgnoreCase("MANUALCBS")) {
        try {
          if (this.icompareConfigService.validate_File(setupBean.getFileDate(), setupBean)) {
            if (this.icompareConfigService.chkFlag("Filter_FlAG", setupBean).equalsIgnoreCase("N") && 
              this.icompareConfigService.chkFlag("Upload_FLAG", setupBean).equalsIgnoreCase("Y")) {
              FilterationBean filterBean = new FilterationBean();
              filterBean.setStEntry_by(setupBean.getCreatedBy());
              filterBean.setStCategory(setupBean.getCategory());
              filterBean.setStFile_Name(setupBean.getStFileName());
              filterBean.setStFile_date(setupBean.getFileDate());
              filterBean.setFileId(setupBean.getInFileId());
              logger.info("File to be processed is of date " + filterBean.getStFile_date());
              List<FilterationBean> search_params = this.filterationService.getSearchParams(filterBean);
              filterBean.setSearch_params(search_params);
              int seg_tran_id = this.filterationService.getseg_tran_id();
              logger.info("seg_tran_id==" + seg_tran_id);
              filterBean.setInseg_tran_id(seg_tran_id);
              int entry_done = this.filterationService.addEntry(filterBean);
              logger.info("entry_done : " + entry_done);
              if (entry_done == 1) {
                this.filterationService.filterRecords(filterBean);
                this.filterationService.updateseg_txn(filterBean);
                this.icompareConfigService.updateFlag("Filter_FlAG", setupBean);
              } 
            } else {
              logger.info("file already processed or not uploaded");
            } 
            if (this.icompareConfigService.chkFlag("Knockoff_FLAG", setupBean).equalsIgnoreCase("N") && 
              this.icompareConfigService.chkFlag("Upload_FLAG", setupBean).equalsIgnoreCase("Y")) {
              KnockOffBean knockoffBean = new KnockOffBean();
              knockoffBean.setStCategory(setupBean.getCategory());
              knockoffBean.setStFile_Name(setupBean.getStFileName());
              knockoffBean.setStEntry_by(setupBean.getCreatedBy());
              logger.info("*************KNOCK OFF STARTS ******************");
              knockoffBean.setStFile_date(setupBean.getFileDate());
              this.filterationService.knockoffRecords(knockoffBean);
              this.icompareConfigService.updateFlag("Knockoff_FLAG", setupBean);
            } 
            if (this.icompareConfigService.chkFlag("Knockoff_FLAG", setupBean).equalsIgnoreCase("Y") && 
              this.icompareConfigService.chkFlag("Filter_FlAG", setupBean).equalsIgnoreCase("Y")) {
              logger.info("File Filtered Successfully");
              redirectAttributes.addFlashAttribute("success_msg", "File Filtered Successfully.");
            } else {
              logger.info("File Not Filterd:File Not Uploaded or Previous File Not process");
              redirectAttributes.addFlashAttribute("error_msg", 
                  "File Not Filterd:File Not Uploaded or Previous File Not process.");
            } 
          } else {
            logger.info("File Not Filtered:File Not Uploaded or Previous File Not process");
            redirectAttributes.addFlashAttribute("error_msg", 
                "File Not Filtered:File Not Uploaded or Previous File Not process.");
          } 
        } catch (Exception ex) {
          logger.error(" error in CompareConfigController.manualClassifyFile", 
              new Exception("CompareConfigController.manualClassifyFile", ex));
          redirectAttributes.addFlashAttribute("error_msg", "error occured while Filteration");
        } 
      } else {
        logger.info("Configuration Not Found for Selected File");
        redirectAttributes.addFlashAttribute("error_msg", "Configuration Not Found for Selected File");
      } 
      logger.info("***** CompareConfigController.manualClassifyFile End ****");
      return "redirect:classifyFile.do";
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigController.manualClassifyFile");
      logger.error(" error in CompareConfigController.manualClassifyFile", 
          new Exception("CompareConfigController.manualClassifyFile", ex));
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"manualCompareFiles"}, method = {RequestMethod.POST})
  public String CompareFile(@ModelAttribute("CompareSetupBean") CompareSetupBean setupBean, HttpServletRequest request, HttpSession httpSession, Model model, ModelAndView modelAndView, RedirectAttributes redirectAttributes) throws Exception {
    logger.info("***** CompareConfigController.CompareFile Start ****");
    logger.info(Integer.valueOf(setupBean.getCompareFile1()));
    logger.info(Integer.valueOf(setupBean.getCompareFile2()));
    logger.info(setupBean.getFileDate());
    logger.info(setupBean.getCompareLvl());
    boolean result = false;
    setupBean.setInFileId(setupBean.getCompareFile1());
    setupBean.setCategory("ONUS");
    String Knockoff_FLAG1 = this.icompareConfigService.chkFlag("Knockoff_FLAG", setupBean);
    String Upload_FLAG1 = this.icompareConfigService.chkFlag("Upload_FLAG", setupBean);
    String FILTER_FLAG1 = this.icompareConfigService.chkFlag("Upload_FLAG", setupBean);
    String COMAPRE_FLAG1 = this.icompareConfigService.chkFlag("COMAPRE_FLAG", setupBean);
    logger.info("Knockoff_FLAG1==" + Knockoff_FLAG1);
    logger.info("Upload_FLAG1==" + Upload_FLAG1);
    logger.info("FILTER_FLAG1==" + FILTER_FLAG1);
    logger.info("COMAPRE_FLAG1==" + COMAPRE_FLAG1);
    setupBean.setInFileId(setupBean.getCompareFile2());
    String Knockoff_FLAG2 = this.icompareConfigService.chkFlag("Knockoff_FLAG", setupBean);
    String Upload_FLAG2 = this.icompareConfigService.chkFlag("Upload_FLAG", setupBean);
    String FILTER_FLAG2 = this.icompareConfigService.chkFlag("FILTER_FLAG", setupBean);
    String COMAPRE_FLAG2 = this.icompareConfigService.chkFlag("COMAPRE_FLAG", setupBean);
    logger.info("Knockoff_FLAG2==" + Knockoff_FLAG2);
    logger.info("Upload_FLAG2==" + Upload_FLAG2);
    logger.info("FILTER_FLAG2==" + FILTER_FLAG2);
    logger.info("COMAPRE_FLAG2==" + COMAPRE_FLAG2);
    if (Knockoff_FLAG1.equalsIgnoreCase("Y") && Upload_FLAG1.equalsIgnoreCase("Y") && 
      FILTER_FLAG1.equalsIgnoreCase("Y") && COMAPRE_FLAG1.equalsIgnoreCase("N") && 
      Knockoff_FLAG2.equalsIgnoreCase("Y") && Upload_FLAG2.equalsIgnoreCase("Y") && 
      FILTER_FLAG2.equalsIgnoreCase("Y") && COMAPRE_FLAG2.equalsIgnoreCase("N")) {
      try {
        CompareBean compareBean = new CompareBean();
        compareBean.setStFile_date(setupBean.getFileDate());
        compareBean.setStEntryBy(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
        logger.info("file date is " + compareBean.getStFile_date());
        List<String> table_list = new ArrayList<>();
        table_list.add("ONUS_SWITCH");
        table_list.add("ONUS_CBS");
        compareBean.setStFile_date(setupBean.getFileDate());
        int i = 1;
        if (i == 1) {
          setupBean.setInFileId(setupBean.getCompareFile1());
          this.icompareConfigService.updateFlag("COMAPRE_FLAG", setupBean);
          setupBean.setInFileId(setupBean.getCompareFile2());
          this.icompareConfigService.updateFlag("COMAPRE_FLAG", setupBean);
          logger.info("Comparision Completed");
          redirectAttributes.addFlashAttribute("success_msg", "Comparision Completed");
        } 
        logger.info("***** CompareConfigController.CompareFile End ****");
      } catch (Exception ex) {
        demo.logSQLException(ex, "CompareConfigController.CompareFile");
        logger.error(" error in CompareConfigController.CompareFile", 
            new Exception("CompareConfigController.CompareFile", ex));
        redirectAttributes.addFlashAttribute("error_msg", "Configuration already Exists.");
      } 
    } else {
      logger.info("Files Are Not Uploaded and Configured For Respective Date");
      redirectAttributes.addFlashAttribute("error_msg", 
          "Files Are Not Uploaded and Configured For Respective Date ");
    } 
    logger.info("result==" + result);
    return "redirect:ManualCompare.do";
  }
  
  @RequestMapping(value = {"GetUplodedFile"}, method = {RequestMethod.GET})
  public String compareData(@ModelAttribute("CompareSetupBean") CompareSetupBean setupBean, RedirectAttributes redirectAttributes, HttpSession httpsession, Model model, HttpSession httpSession) throws Exception {
    logger.info("***** CompareConfigController.compareData Start ****");
    try {
      setupBean.setCreatedBy(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
      List<CompareSetupBean> setupBeans = this.icompareConfigService.getlastUploadDetails();
      model.addAttribute("setupBeans", setupBeans);
      logger.info("***** CompareConfigController.compareData End ****");
      return "UploadedFileDetails";
    } catch (Exception e) {
      demo.logSQLException(e, "CompareConfigController.compareData");
      logger.error(" error in CompareConfigController.compareData", 
          new Exception("CompareConfigController.compareData", e));
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"saveCompareSetup"}, method = {RequestMethod.POST})
  public String SaveCompareDetails(@ModelAttribute("CompareSetupBean") CompareSetupBean setupBean, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession) throws Exception {
    logger.info("***** CompareConfigController.SaveCompareDetails Start ****");
    try {
      boolean setupresult = this.icompareConfigService.chkMain_ReconSetupDetails(setupBean);
      boolean result = false;
      setupBean.setEntryBy(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
      if (setupresult) {
        result = this.icompareConfigService.saveCompareDetails(setupBean);
        logger.info("***** CompareConfigController.SaveCompareDetails End ****");
      } else {
        logger.info("Setup already Configured");
        redirectAttributes.addFlashAttribute("error_msg", "Setup already Configured.");
        return "redirect:CompareSetup.do";
      } 
      if (result) {
        logger.info("Configuration Completed Successfully");
        redirectAttributes.addFlashAttribute("success_msg", "Configuration Completed Successfully.");
        return "redirect:CompareSetup.do";
      } 
      logger.info("Error occured while inserting data");
      redirectAttributes.addFlashAttribute("error_msg", "Error occured while inserting data.");
      return "redirect:CompareSetup.do";
    } catch (Exception e) {
      demo.logSQLException(e, "CompareConfigController.SaveCompareDetails");
      logger.error(" error in CompareConfigController.SaveCompareDetails", 
          new Exception("CompareConfigController.SaveCompareDetails", e));
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"saveManCompareSetup"}, method = {RequestMethod.POST})
  public String SaveManualCompareDetails(@ModelAttribute("ManualCompBean") ManualCompareBean manualCompBean, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession) throws Exception {
    logger.info("***** CompareConfigController.SaveManualCompareDetails Start ****");
    try {
      logger.info(manualCompBean);
      manualCompBean.setEntryBy(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
      boolean result = this.icompareConfigService.saveManCompareDetails(manualCompBean);
      if (result) {
        logger.info("Manual Compare setup Saved Successfully");
        redirectAttributes.addFlashAttribute("success_msg", "Manual Compare setup Saved Successfully.");
      } else {
        logger.info("Error Occured While Saving Data");
        redirectAttributes.addFlashAttribute("error_msg", "Error Occured While Saving Data.");
      } 
      logger.info("***** CompareConfigController.SaveManualCompareDetails End ****");
      return "redirect:ManualCompareSetup.do";
    } catch (Exception e) {
      demo.logSQLException(e, "CompareConfigController.SaveManualCompareDetails");
      logger.error(" error in CompareConfigController.SaveManualCompareDetails", 
          new Exception("CompareConfigController.SaveManualCompareDetails", e));
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"/getCompareFiles"}, method = {RequestMethod.POST})
  @ResponseBody
  public ArrayList<CompareSetupBean> getCompareFiles(@RequestParam("type") String type, @RequestParam("subcat") String subcat, HttpSession httpSession, LoginBean loginBean, HttpServletRequest request) throws Exception {
    logger.info("***** CompareConfigController.getCompareFiles Start ****");
    loginBean.setUser_id(((LoginBean)request.getSession().getAttribute("loginBean")).getUser_id().trim());
    logger.info("in getCompareFiles" + type);
    ArrayList<CompareSetupBean> filelist = this.icompareConfigService.getCompareFiles(type, subcat);
    logger.info("***** CompareConfigController.getCompareFiles End ****");
    return filelist;
  }
  
  @RequestMapping(value = {"/ViewCompareDetls"}, method = {RequestMethod.GET})
  public String ViewCompareDetls(@RequestParam("recId") int recId, @RequestParam("Cate") String Cate, LoginBean loginBean, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
    logger.info("***** CompareConfigController.ViewCompareDetls Start ****");
    logger.info("in getCompareFiles" + recId);
    logger.info("in getCompareFiles" + Cate);
    if (Cate.equals("POS"))
      Cate = "POS_ONUS"; 
    try {
      loginBean.setUser_id(((LoginBean)request.getSession().getAttribute("loginBean")).getUser_id().trim());
      logger.info("ViewCompareDetls");
      ArrayList<CompareSetupBean> matchcrtlist = this.icompareConfigService.getmatchcrtlist(recId, Cate);
      ArrayList<CompareSetupBean> matchcondlist = this.icompareConfigService.getmatchcondnlist(recId, Cate);
      ArrayList<CompareSetupBean> recparamlist = this.icompareConfigService.getrecparamlist(recId, Cate);
      logger.info(matchcrtlist);
      logger.info(matchcondlist);
      logger.info(recparamlist);
      model.addAttribute("matchcrtlist", matchcrtlist);
      model.addAttribute("matchcondlist", matchcondlist);
      model.addAttribute("recparamlist", recparamlist);
      logger.info("***** CompareConfigController.ViewCompareDetls End ****");
      return "CompareDetails";
    } catch (Exception e) {
      logger.error(" error in CompareConfigController.ViewCompareDetls", 
          new Exception("CompareConfigController.ViewCompareDetls", e));
      redirectAttributes.addFlashAttribute("error_msg", e.getMessage());
      return "redirect:Login.do";
    } 
  }
  
  @RequestMapping(value = {"ManualUploadIccw"}, method = {RequestMethod.GET})
  public ModelAndView ManualUploadIccw(Model model, CompareSetupBean compareSetupBean, ModelAndView modelAndView, HttpSession httpSession, HttpServletRequest request) throws Exception {
    try {
      compareSetupBean.setCreatedBy(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
      String csrf = CSRFToken.getTokenForSession(request.getSession());
      modelAndView.addObject("CSRFToken", csrf);
      modelAndView.setViewName("ManualUploadIccw");
      modelAndView.addObject("CompareSetupBean", compareSetupBean);
      logger.info("***** CompareConfigController.ManualUploadIccw End ****");
      return modelAndView;
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigController.ManualUpload");
      logger.error(" error in CompareConfigController.ManualUpload", 
          new Exception("CompareConfigController.ManualUpload", ex));
      modelAndView.setViewName("Login");
      return modelAndView;
    } 
  }
  
  @RequestMapping(value = {"IccwRecon"}, method = {RequestMethod.GET})
  public ModelAndView IccwRecon(Model model, CompareSetupBean compareSetupBean, ModelAndView modelAndView, HttpSession httpSession, HttpServletRequest request) throws Exception {
    try {
      compareSetupBean.setCreatedBy(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
      String csrf = CSRFToken.getTokenForSession(request.getSession());
      modelAndView.addObject("CSRFToken", csrf);
      modelAndView.setViewName("IccwReconPage");
      modelAndView.addObject("CompareSetupBean", compareSetupBean);
      logger.info("***** CompareConfigController.ManualUploadIccw End ****");
      return modelAndView;
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigController.ManualUpload");
      logger.error(" error in CompareConfigController.ManualUpload", 
          new Exception("CompareConfigController.ManualUpload", ex));
      modelAndView.setViewName("Login");
      return modelAndView;
    } 
  }
  
  @RequestMapping(value = {"IccwDownloadPage"}, method = {RequestMethod.GET})
  public ModelAndView IccwDownload(Model model, CompareSetupBean compareSetupBean, ModelAndView modelAndView, HttpSession httpSession, HttpServletRequest request) throws Exception {
    System.out.println("Inside controller");
    try {
      compareSetupBean.setCreatedBy(((LoginBean)httpSession.getAttribute("loginBean")).getUser_id());
      String csrf = CSRFToken.getTokenForSession(request.getSession());
      modelAndView.addObject("CSRFToken", csrf);
      modelAndView.setViewName("IccwDownloadPage");
      modelAndView.addObject("CompareSetupBean", compareSetupBean);
      logger.info("***** CompareConfigController.IccwDownload End ****");
      return modelAndView;
    } catch (Exception e) {
      demo.logSQLException(e, "CompareConfigController.IccwDownload");
      logger.error(" error in CompareConfigController.IccwDownload", 
          new Exception("CompareConfigController.IccwDownload", e));
      modelAndView.setViewName("Login");
      return modelAndView;
    } 
  }
}
