package com.recon.service;


import com.recon.model.MastercardUploadBean;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface MastercardService {
  HashMap<String, Object> checkFileUpload(MastercardUploadBean paramMastercardUploadBean, String paramString);
  
  HashMap<String, Object> checkFileUploadVISA(String paramString);
  
  HashMap<String, Object> checkFileUploadE057(MastercardUploadBean paramMastercardUploadBean, String paramString);
  
  HashMap<String, Object> TADfileReading(MultipartFile paramMultipartFile, MastercardUploadBean paramMastercardUploadBean);
  
  HashMap<String, Object> TAfileReading(MultipartFile paramMultipartFile, MastercardUploadBean paramMastercardUploadBean);
  
  HashMap<String, Object> file140Reading(MultipartFile paramMultipartFile, MastercardUploadBean paramMastercardUploadBean);
  
  HashMap<String, Object> validationRawFilesForProcessing(MastercardUploadBean paramMastercardUploadBean);
  
  HashMap<String, Object> validationForProcessing(MastercardUploadBean paramMastercardUploadBean);
  
  boolean mastercardSettlmentPro(MastercardUploadBean paramMastercardUploadBean);
  
  boolean mastercardSettlmentProINT(MastercardUploadBean paramMastercardUploadBean);
  
  HashMap<String, Object> validateForReportDownload(MastercardUploadBean paramMastercardUploadBean);
  
  List<Object> getSettlementData(MastercardUploadBean paramMastercardUploadBean);
  
  HashMap<String, Object> validationForSettlementTTUMProcess(MastercardUploadBean paramMastercardUploadBean);
  
  boolean processSettlementTTUM(MastercardUploadBean paramMastercardUploadBean);
  
  List<Object> getSettlementTTUMData(MastercardUploadBean paramMastercardUploadBean);
  
  HashMap<String, Object> validationForProcessing_OLD_01112023(MastercardUploadBean paramMastercardUploadBean);
  
  void DeleteFiles(String paramString);
  
  void generateSettlementReport(MastercardUploadBean paramMastercardUploadBean);
}
