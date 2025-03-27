package com.recon.service;


import com.recon.model.SettlementBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.util.SearchData;
import com.recon.util.ViewFiles;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

public interface NFSUnmatchTTUMService {
  List<ViewFiles> searchSwitchViewFile1(String paramString1, String paramString2) throws Exception, SQLException;
  
  List<ViewFiles> searchCBSViewFile1(String paramString1, String paramString2) throws Exception, SQLException;
  
  List<ViewFiles> searchRowDataViewFile1(String paramString1, String paramString2) throws Exception, SQLException;
  
  HashMap<String, Object> checkTTUMProcessed(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkReconDateAndTTUMDataPresent(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcess(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getNFSTTUMData(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  void generateExcelTTUM(String paramString1, String paramString2, List<Object> paramList, String paramString3, HttpServletResponse paramHttpServletResponse, boolean paramBoolean);
  
  boolean checkAndMakeDirectory(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  Boolean NFSTtumRollback(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<String> getNFSVoucher(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getUnreconAcqOffusTTUMReport(UnMatchedTTUMBean paramUnMatchedTTUMBean, String paramString);
  
  boolean runTTUMProcess(String paramString1, String paramString2);
  
  int checkAdjustmentTTUMProcessed(String paramString1, String paramString2);
  
  List<Object> getAdjustmentTTUMReport(String paramString1, String paramString2);
  
  List<Object> downloadDhanaReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadDhanaReport2(SettlementBean paramSettlementBean);
  
  List<Object> downloadDhanaReport3(SettlementBean paramSettlementBean);
  
  List<Object> downloadIssuerDhanaReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadIssuerDhanaReport2(SettlementBean paramSettlementBean);
  
  List<Object> downloadIssuerDhanaReport3(SettlementBean paramSettlementBean);
  
  List<Object> downloadQSPARCDOMReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadICDISSReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadICCWISSReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadICDACQReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadICCWACQReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadMCISSPOSReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadMCISSATMReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadMCISSATMReportACQ(SettlementBean paramSettlementBean);
  
  List<Object> downloadMCISSATMReportACQINT(SettlementBean paramSettlementBean);
  
  List<Object> downloadVISAISSINTPOSReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadVISAISSINTATMReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadVISAISSDOMPOSReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadVISAISSDOMATMReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadVISAACUIRERATMReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadVISAACUIRERATMINTReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadDFSACQReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadJCBACQReport(SettlementBean paramSettlementBean);
  
  int rollbackAdjustmentTTUM(String paramString1, String paramString2);
  
  List<Object> downloadRupayDhanaReport(SettlementBean paramSettlementBean);
  
  List<Object> downloadRupayDhanaReport2(SettlementBean paramSettlementBean);
  
  List<Object> downloadRupayDhanaReportINT(SettlementBean paramSettlementBean);
  
  List<Object> excelMicroAtmReportDownload(SettlementBean paramSettlementBean);
  
  List<Object> excelMicroAtmTTUMDownload(SettlementBean paramSettlementBean);
  
  List<ViewFiles> searchViewFile(String paramString1, String paramString2 )throws Exception, SQLException ;
  
  List<ViewFiles> searchViewFile1(String paramString1, String paramString2 , String ModuleType) throws Exception, SQLException;
  
  List<SearchData> searchData(String paramString1, String paramString2, String paramString3) throws Exception, SQLException;
  
  boolean deleteViewFile(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws Exception, SQLException;
  
  boolean increaseViewFile(String paramString1, String paramString2, String paramString3, String paramString4) throws Exception, SQLException;
  
  List<Object> downloadDhanaReportCTCACQ(SettlementBean paramSettlementBean);
  
  List<Object> downloadDhanaReportCTCISS(SettlementBean paramSettlementBean);
}
