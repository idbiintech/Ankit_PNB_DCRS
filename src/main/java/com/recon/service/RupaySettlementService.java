package com.recon.service;


import com.recon.model.RupaySettlementBean;
import com.recon.model.RupayUploadBean;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface RupaySettlementService {
  HashMap<String, Object> uploadExcelFile(RupaySettlementBean paramRupaySettlementBean, MultipartFile paramMultipartFile) throws Exception;
  
  HashMap<String, Object> validatePrevFileUpload(RupaySettlementBean paramRupaySettlementBean);
  
  void generateRupaySettlmentTTum(String paramString, HttpServletResponse paramHttpServletResponse);
  
  boolean readFile(RupayUploadBean paramRupayUploadBean, MultipartFile paramMultipartFile);
  
  boolean readIntFile(RupayUploadBean paramRupayUploadBean, MultipartFile paramMultipartFile);
  
  boolean checkFileUploaded(RupayUploadBean paramRupayUploadBean);
  
  HashMap<String, Object> validateRawfiles(RupayUploadBean paramRupayUploadBean);
  
  HashMap<String, Object> validateSettlementFiles(RupayUploadBean paramRupayUploadBean);
  
  boolean processSettlement(RupayUploadBean paramRupayUploadBean);
  
  boolean processSettlementRRB(RupayUploadBean paramRupayUploadBean);
  
  HashMap<String, Object> processSettlementVisa(RupayUploadBean paramRupayUploadBean);
  
  HashMap<String, Object> processSettlementVisaACQ(RupayUploadBean paramRupayUploadBean);
  
  HashMap<String, Object> processSettlementVisaINT(RupayUploadBean paramRupayUploadBean);
  
  HashMap<String, Object> processSettlementVisaINTACQ(RupayUploadBean paramRupayUploadBean);
  
  HashMap<String, Object> processSettlementVisaRollback(RupayUploadBean paramRupayUploadBean);
  
  HashMap<String, Object> processSettlementVisaRollbackACQ(RupayUploadBean paramRupayUploadBean);
  
  HashMap<String, Object> processSettlementVisaRollbackINT(RupayUploadBean paramRupayUploadBean);
  
  HashMap<String, Object> rollbackUpdateDollarRate(RupayUploadBean paramRupayUploadBean);
  
  HashMap<String, Object> processSettlementVisaRollbackINTACQ(RupayUploadBean paramRupayUploadBean);
  
  boolean processSettlementVisa2(RupayUploadBean paramRupayUploadBean);
  
  boolean processSettlementINT(RupayUploadBean paramRupayUploadBean);
  boolean processSettlementRRBINT(RupayUploadBean paramRupayUploadBean);
  
  boolean processSettlementVISAINT(RupayUploadBean paramRupayUploadBean);
  
  boolean processSettlementQsparc(RupayUploadBean paramRupayUploadBean);
  
  boolean processSettlementQsparcINT(RupayUploadBean paramRupayUploadBean);
  
  boolean validateSettlementProcess(RupayUploadBean paramRupayUploadBean);
  
  boolean validateSettlementProcessRRB(RupayUploadBean paramRupayUploadBean);
  
  boolean validateSettlementProcessVisa(RupayUploadBean paramRupayUploadBean);
  
  boolean InsertDollarRateVisa(RupayUploadBean paramRupayUploadBean);
  
  boolean validateSettlementProcessVisa2(RupayUploadBean paramRupayUploadBean);
  
  boolean validateSettlementProcessINT(RupayUploadBean paramRupayUploadBean);
  
  boolean validateSettlementProcessRRBINT(RupayUploadBean paramRupayUploadBean);
  
  boolean validateSettlementProcessVISAINT(RupayUploadBean paramRupayUploadBean);
  
  boolean validateSettlementProcessQsparc(RupayUploadBean paramRupayUploadBean);
  
  boolean validateSettlementProcessQsparcINT(RupayUploadBean paramRupayUploadBean);
  
  List<Object> getSettlementData(RupayUploadBean paramRupayUploadBean);
  
  List<Object> getSettlementDataRRB(RupayUploadBean paramRupayUploadBean);
  
  List<Object> getSettlementDataVisa(RupayUploadBean paramRupayUploadBean);
  
  List<Object> getSettlementDataINT(RupayUploadBean paramRupayUploadBean);
  
  List<Object> getSettlementDataQsparc(RupayUploadBean paramRupayUploadBean);
  
  List<Object> getSettlementDataQsparcINT(RupayUploadBean paramRupayUploadBean);
  
  boolean IRGCSprocessSettlementTTUM(RupayUploadBean paramRupayUploadBean);
  
  Boolean validateSettlementTTUM(RupayUploadBean paramRupayUploadBean);
  
  Boolean visavalidateSettlementTTUM(RupayUploadBean paramRupayUploadBean);
  
  List<Object> getSettlementTTUMData(RupayUploadBean paramRupayUploadBean);
  
  List<Object> visagetSettlementTTUMData(RupayUploadBean paramRupayUploadBean);
  
  boolean processSettlementTTUM(RupayUploadBean paramRupayUploadBean);
  
  boolean visaprocessSettlementTTUM(RupayUploadBean paramRupayUploadBean);
  
  boolean IntvisaprocessSettlementTTUM(RupayUploadBean paramRupayUploadBean);
  
  boolean validateSettlementDiff(RupayUploadBean paramRupayUploadBean);
  
  boolean processRectification(RupayUploadBean paramRupayUploadBean);
  
  HashMap<String, Object> validateDiffAmount(RupayUploadBean paramRupayUploadBean);
  
  boolean checkNCMCFileUploaded(RupayUploadBean paramRupayUploadBean);
  
  boolean readNCMCFile(RupayUploadBean paramRupayUploadBean, MultipartFile paramMultipartFile);
  
  boolean readNCMCINTFile(RupayUploadBean paramRupayUploadBean, MultipartFile paramMultipartFile);
  
  boolean validateQsparcTTUM(RupayUploadBean paramRupayUploadBean);
  
  boolean processSettlementTTUMQsparc(RupayUploadBean paramRupayUploadBean);
  
  List<Object> getQsparcTTUMData(RupayUploadBean paramRupayUploadBean);
  
  boolean checkdata(RupayUploadBean paramRupayUploadBean);
  
  boolean visacheckdata(RupayUploadBean paramRupayUploadBean);
  
  String uploadPresentmentFile(RupayUploadBean paramRupayUploadBean, MultipartFile paramMultipartFile) throws IOException, SQLException, Exception;
  
  String IntuploadPresentmentFile(RupayUploadBean paramRupayUploadBean, MultipartFile paramMultipartFile) throws IOException, SQLException, Exception;
  
  boolean checkpresentmenetupload(RupayUploadBean paramRupayUploadBean);
  
  boolean checkmoneyaddupload(RupayUploadBean paramRupayUploadBean);
  
  String uploadmoneyadd(RupayUploadBean paramRupayUploadBean, MultipartFile paramMultipartFile) throws SQLException, Exception;
}
