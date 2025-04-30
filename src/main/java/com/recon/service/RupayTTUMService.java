package com.recon.service;


import com.recon.model.RecordCount;
import com.recon.model.UnMatchedTTUMBean;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

public interface RupayTTUMService {
  HashMap<String, Object> runTTUMProces(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcesJCB(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcesDFS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcess2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcess3(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcess4(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcesICD(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  
  HashMap<String, Object> runTTUMProcesICCW(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  HashMap<String, Object> runTTUMProcesICCW2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcesNFSRUPAY(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcess2ICD(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcess3ICD(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcess4ICD(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runProcessFinacleTTUM(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcesVISA(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA3(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA4(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA5(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA6(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA7(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA8(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA9(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA10(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcesVISAPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA2POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA3POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA4POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA5POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA6POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA7POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcesVISAINT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA2INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA3INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA4INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA5INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA6INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA7INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA8INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcesVISAINTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA2INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA10INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA3INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA4INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA5INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA6INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA7INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA8INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> runTTUMProcessVISA9INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessRUPAY(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessRUPAYINT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessRUPAY2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessRUPAY2INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessRUPAY3(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessRUPAY3INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessRUPAY5(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessRUPAY5INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessRUPAY6(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessRUPAY10(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessRUPAY6INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessRUPAY4(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean UnmatchedTTUMProcMC(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  boolean UnmatchedTTUMProcMCCROSS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessRUPAY4INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessQSPARC(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessQSPARCINT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessQSPARC2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessQSPARC2INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessQSPARC3(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessQSPARC3INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessQSPARC5(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessQSPARC6(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessQSPARC4(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessQSPARC4INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessMC5(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessMC2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessMC3(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessMC2POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  boolean runTTUMProcessMC2POSINT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessMC3POSINT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runTTUMProcessMC3POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkTTUMProcessed(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  HashMap<String, Object> checkTTUMProcessedJCB(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  HashMap<String, Object> checkTTUMProcessedDFS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  
  HashMap<String, Object> checkTTUMProcessedICD(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkTTUMProcessedICCW(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  HashMap<String, Object> checkTTUMProcessedNFSRUPAY(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkrunProcessFinacleTTUM(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkTTUMProcessedVISA(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkTTUMProcessedVISANB(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkTTUMProcessedCTCSETTLEMENT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkTTUMProcessedATNPOSCROSSROUNTING(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkTTUMProcessedCTC(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkTTUMProcessedRUPAY(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkTTUMProcessedMASTERCARD(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  String createTTUMFile(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean checkAndMakeDirectory(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkReconProcessed(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getTTUMData(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkTranReconDate(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  void generateExcelTTUM(String paramString1, String paramString2, List<Object> paramList, String paramString3, HttpServletResponse paramHttpServletResponse, boolean paramBoolean);
  
  List<Object> getNIHTTUMData(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getVisaTTUMData(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getRupayTTUMData(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getIntRupayTTUMData(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getSurchargedata(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  void generateRupayExcelTTUM(String paramString1, List<Object> paramList, String paramString2, HttpServletResponse paramHttpServletResponse);
  
  HashMap<String, Object> checkInternationalTTUMProcessed(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkNIHRecords(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runInternationalTTUMProcess(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getInternationalTTUMData(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  void generateInternationalTTUMFile(String paramString1, String paramString2, List<Object> paramList);
  
  List<Object> getNIHReport(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkCardToCardTTUMProcessed(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runCardToCardTTUMProcess(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getCardToCardTTUMData(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkInternationalUpload(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> checkData(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean fundingcheckAndMakeDirectory(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getFundingdata(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<RecordCount> downloadRawdataSummary();
}
