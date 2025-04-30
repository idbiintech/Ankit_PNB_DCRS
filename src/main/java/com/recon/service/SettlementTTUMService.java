package com.recon.service;



import com.recon.model.MastercardUploadBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.RupayUploadBean;
import com.recon.model.UnMatchedTTUMBean;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface SettlementTTUMService {
  HashMap<String, Object> validateMonthlySettlement(NFSSettlementBean paramNFSSettlementBean);

  boolean runNFSMonthlyTTUM(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getMonthlyTTUMData(NFSSettlementBean paramNFSSettlementBean);
  
  HashMap<String, Object> validateDailyInterchange(NFSSettlementBean paramNFSSettlementBean);
  
  boolean runDailyInterchangeTTUM(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getDailyInterchangeTTUMData(NFSSettlementBean paramNFSSettlementBean);
	public ArrayList<String> getColumnList(String tableName) throws SQLException, Exception ;
  boolean runSettlementVoucher(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getSettlementVoucher(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getAdjTTUM(NFSSettlementBean paramNFSSettlementBean);

  List<Object> getAdjTTUMICCW(NFSSettlementBean paramNFSSettlementBean);

  List<Object> getAdjTTUMICD(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getTTUMRUPAY(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getTTUMMASTERCARD(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getTTUMNFS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getTTUMICD(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  List<Object> getTTUMJCB(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  List<Object> getTTUMDFS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  List<Object> getTTUMICCW(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getTTUMVISA(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getTTUMVISANB(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getTTUMCTCSETTLEMENT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getTTUMATMPOSCROSSROUNTING(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getTTUMNFSTOVISACROSSROUNTING(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> ExtractVisaRawdata(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> ExtractSwitchRawdata(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> ExtractCbsRawdata(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> ExtractMASTERCARDRawdata(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> ExtractNFSRawdata(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> ExtractRUPAYRawdata(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getTTUMVISANFSRUPAY(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getFinacleTTUM(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getTTUMMASTERCARD(MastercardUploadBean paramMastercardUploadBean);
  
  List<Object> getTTUMCTC(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean runAdjTTUM(NFSSettlementBean paramNFSSettlementBean);
  
  boolean runCooperativeTTUM(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getCooperativeTTUM(NFSSettlementBean paramNFSSettlementBean);
  
  boolean checkDailyInterchangeTTUMProcess(NFSSettlementBean paramNFSSettlementBean);
  
  String ValidateLateReversalFile(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getLateReversalTTUMData(NFSSettlementBean paramNFSSettlementBean);
  
  String checkReversalTTUMProcess(NFSSettlementBean paramNFSSettlementBean) throws Exception;
  
  boolean runLateReversalTTUM(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getPBGBAdjTTUM(NFSSettlementBean paramNFSSettlementBean);
  
  boolean runVisaAdjTTUM(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getVisaAdjTTUM(NFSSettlementBean paramNFSSettlementBean);
  
  boolean adjTTUMProc(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getDailyNFSTTUMData(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getDailyNFSTTUMDataICD(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getDailyNFSTTUMDataRUPAY(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getDailyNFSTTUMDataMASTERCARD(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getNFSRECONACQTTUM(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  List<Object> getISSICCW(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  List<Object> getACQJCB(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  List<Object> getACQDFS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getICDTTUM(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getVISATTUM(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getVISATTUMNB(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getVISATTUMCTCSETTLEMENTTTUM(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getVISATTUMATMPOSCROSSROUNTING(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getVISATTUMNFSTOVISACROSSROUNTING(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getVISATTUMNFSRUPAY(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getMASTERCARDTTUM(MastercardUploadBean paramMastercardUploadBean);
  
  List<Object> getCTCTTUM(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getDailyNFSTTUMDataRupay(RupayUploadBean paramRupayUploadBean);
  
  List<Object> getDailyNFSTTUMDataRupayRRB(RupayUploadBean paramRupayUploadBean);
  
  List<Object> getDailyNFSTTUMDataVISA(RupayUploadBean paramRupayUploadBean);
  
  List<Object> getDailyNFSTTUMDataRupayINT(RupayUploadBean paramRupayUploadBean);
  
  List<Object> getDailyNFSTTUMDataRupayQSPARC(RupayUploadBean paramRupayUploadBean);
  
  List<Object> getDailyNFSTTUMDataRupayQSPARCINT(RupayUploadBean paramRupayUploadBean);
  
  String TTUMRollbackReport(String paramString1, String paramString2) throws ParseException, Exception;
  
  String TTUMRollbackReportICD(String paramString1, String paramString2) throws ParseException, Exception;
  
  boolean rollBackRupaySettl(NFSSettlementBean paramNFSSettlementBean) throws ParseException, Exception;
  
  boolean rollBackRupaySettl2(NFSSettlementBean paramNFSSettlementBean) throws ParseException, Exception;
  
  boolean rollBackRupaySettlRRB(NFSSettlementBean paramNFSSettlementBean) throws ParseException, Exception;
  
  boolean rollBackRupaySettlINT(NFSSettlementBean paramNFSSettlementBean) throws ParseException, Exception;
  
  boolean rollBackRupayADJ(NFSSettlementBean paramNFSSettlementBean) throws ParseException, Exception;
  
  boolean rollBackRupayRefund(NFSSettlementBean paramNFSSettlementBean) throws ParseException, Exception;
  
  boolean rollBackNFSADJ(NFSSettlementBean paramNFSSettlementBean) throws ParseException, Exception;
  boolean rollBackNFSADJICCW(NFSSettlementBean paramNFSSettlementBean) throws ParseException, Exception;
  
  String rollBackRupayADJVISA(String paramString1, String paramString2, String paramString3) throws ParseException, Exception;
  
  boolean rollBackRupaySettlQsparc(NFSSettlementBean paramNFSSettlementBean) throws ParseException, Exception;
  
  boolean rollBackRupaySettlQsparcINT(NFSSettlementBean paramNFSSettlementBean) throws ParseException, Exception;
  
  String TTUMRollback(String paramString1, String paramString2) throws ParseException, Exception;
  
  String TTUMRollbackICD(String paramString1, String paramString2) throws ParseException, Exception;
  
  boolean TTUMRollbackReportSETTL(NFSSettlementBean paramNFSSettlementBean) throws ParseException, Exception;
  
  String TTUMRollbackReportSETTLICD(String paramString) throws ParseException, Exception;
  
  boolean TTUMRollbackReportSETTLJCB(NFSSettlementBean paramNFSSettlementBean) throws ParseException, Exception;
  
  boolean TTUMRollbackReportSETTLDFS(NFSSettlementBean paramNFSSettlementBean) throws ParseException, Exception;
  
  boolean TTUMRollbackReportSETTLMASTERCARD(String paramString) throws ParseException, Exception;
  
  boolean TTUMRollbackReportSETTLMASTERCARDINT(String paramString) throws ParseException, Exception;
  
  String TTUMRollbackMC(String paramString) throws ParseException, Exception;
  
  String TTUMRollbackMCINTTTUM(String paramString) throws ParseException, Exception;
  
  String TTUMRollbackMCINT(String paramString) throws ParseException, Exception;
  
  String TTUMROLLBACKRUPAY(String paramString) throws ParseException, Exception;
  
  HashMap<String, Object> runAdjTTUMSETTL(NFSSettlementBean paramNFSSettlementBean);
  
  HashMap<String, Object> AdjTTUMProcSETTLICD(NFSSettlementBean paramNFSSettlementBean);
  
  HashMap<String, Object> AdjTTUMProcSETTLJCB(NFSSettlementBean paramNFSSettlementBean);
  
  HashMap<String, Object> AdjTTUMProcSETTLDFS(NFSSettlementBean paramNFSSettlementBean);
  
  HashMap<String, Object> runAdjTTUMSETTLnew(NFSSettlementBean paramNFSSettlementBean);
  
  HashMap<String, Object> AdjTTUMProcSETTLnewICD(NFSSettlementBean paramNFSSettlementBean);
  
  boolean runAdjTTUMnew(NFSSettlementBean paramNFSSettlementBean);
  
  HashMap<String, Object> ANKITTREPORTSnew(NFSSettlementBean paramNFSSettlementBean);
  HashMap<String, Object> ANKITTREPORTSnewICCW(NFSSettlementBean paramNFSSettlementBean);
  HashMap<String, Object> ANKITACQPRNALTITTUM(NFSSettlementBean paramNFSSettlementBean);
  
  HashMap<String, Object> ANKITREPORSnewICD(NFSSettlementBean paramNFSSettlementBean);
  
  HashMap<String, Object> ANKITTTUMnew(NFSSettlementBean paramNFSSettlementBean);
  
  HashMap<String, Object> ANKITTTUMnewICD(NFSSettlementBean paramNFSSettlementBean);
  
  boolean adjTTUMProcSETTL(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getAdjTTUMSETTL(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getREEFUNDTTUM(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getREEFUNDTTUMVISA(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getREEFUNDTTUMVISAINT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getREEFUNDTTUMISS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  String TTUMRollbackSETTL(String paramString) throws ParseException, Exception;
  
  String TTUMRollbackSETTLICD(String paramString) throws ParseException, Exception;
  
  List<Object> getDailyNFSTTUMDataSETTL(NFSSettlementBean paramNFSSettlementBean);
  
  List<Object> getREEFUNDTTUMTEXT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getREEFUNDTTUMTEXTVISA(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getREEFUNDTTUMTEXTVISAINT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  List<Object> getREEFUNDTTUMTEXTISS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean NFSSETTLTTUM(NFSSettlementBean paramNFSSettlementBean) throws SQLException;
  
  boolean rollBackACQCTC(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackTTUMRUPAY(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackFinacleTTUM(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA3(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA4(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA5(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA6(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA7(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA8(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA9(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  HashMap<String, Object> rollBackTTUMVISA10(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISAPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA2POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA3POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA4POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA5POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA6POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA7POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISAINT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA2INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA3INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA4INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA5INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA6INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA7INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA8INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISAINTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA2INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA3INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA4INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA5INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA6INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA10INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA7INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA8INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> rollBackTTUMVISA9INTPOS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMICD(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  boolean rollBackTTUMICCW(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  boolean rollBackTTUMICCW2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMICD2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMICD3(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMICD4(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMQSPARC(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMRUPAYINT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMNFS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  boolean rollBackTTUMJCB(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  boolean rollBackTTUMDFS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMCTC(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMCTC2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMNFSRUAPY(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  HashMap<String, Object> mapSwitchDATA(String paramString);
  
  HashMap<String, Object> mapSwitchDATA1(String paramString);
  
  boolean rollBackTTUMNFS2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMRUPAY2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMRUPAY2INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMRUPAY3(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMRUPAY3INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMRUPAY6(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMRUPAY6INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMRUPAY7(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMRUPAY7INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMRUPAY4(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMQSPARC4(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMRUPAY4INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMRUPAY5(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackACQRupay(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackINTRupay(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackTTUMMC4DR(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMMC4(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMMC2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMMC3(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMMC3CROSS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMMC2INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMMC4INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMMC3INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMMC2POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMMC3POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMMC6POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMMC10POS(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackQSPARC(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackNFSISS(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackICDISS(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackICDACQ(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackICCWDACQ(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackICCWDISS(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackNFSACQ(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackNFSDFSACQ(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackNFSJCBACQ(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackVISAISSPOS(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackVISAISSATM(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackVISACQ(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackVISACQ1(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackVISAISSINTPOS(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackVISAISSINTATM(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackMASTERCARDISSATM(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackMASTERCARDISSPOS(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackMASTERCARDACQATM(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackMASTERCARDACQATMINT(NFSSettlementBean paramNFSSettlementBean);
  
  boolean CROSS_RECON_VISA_POS_INT_DOM_ROLLBACK(NFSSettlementBean paramNFSSettlementBean);
  
  boolean CROSS_RECON_VISA_POS_DOM_INT_ROLLBACK(NFSSettlementBean paramNFSSettlementBean);
  
  boolean CROSS_RECON_VISA_ATM_DOM_INT_ROLLBACK(NFSSettlementBean paramNFSSettlementBean);
  
  boolean CROSS_RECON_VISA_ATM_INT_DOM_ROLLBACK(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackTTUMQSPARC2(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMQSPARC2INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMQSPARC3(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMQSPARC3INT(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMQSPARC6(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackTTUMQSPARC7(UnMatchedTTUMBean paramUnMatchedTTUMBean);
  
  boolean rollBackISSCTC(NFSSettlementBean paramNFSSettlementBean);
  
  boolean rollBackINTVISA(RupayUploadBean paramRupayUploadBean);
  
  boolean rollBackINTVISA2(RupayUploadBean paramRupayUploadBean);
}
