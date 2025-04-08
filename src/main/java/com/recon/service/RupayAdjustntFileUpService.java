package com.recon.service;


import java.util.HashMap;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface RupayAdjustntFileUpService {
  HashMap<String, Object> rupayAdjustmentFileUpload(String paramString1, String paramString2, String paramString3, String paramString4, MultipartFile paramMultipartFile, String paramString5);
  
  HashMap<String, Object> validateAdjustmentTTUM(String paramString1, String paramString2, String paramString3);
  
  HashMap<String, Object> validateAdjustmentTTUMVISA(String paramString1, String paramString2, String paramString3);
  
  HashMap<String, Object> validateAdjustmentUpload(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean, String paramString5);
  
  HashMap<String, Object> validatePresentmentUpload(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean, String paramString5);
  
  List<Object> getAdjTTUM(String paramString1, String paramString2, String paramString3);
  
  HashMap<String, Object> rupayIntPresentFileUpload(String paramString1, String paramString2, String paramString3, String paramString4, MultipartFile paramMultipartFile, String paramString5);
  
  boolean runAdjTTUM(String paramString1, String paramString2, String paramString3, String paramString4);
  
  boolean runAdjTTUMREFUND(String paramString1, String paramString2, String paramString3, String paramString4);
  
  boolean runMCTTUMREFUND(String paramString1, String paramString2, String paramString3, String paramString4);
  
  HashMap<String, Object> validateAdjustmentTTUMProcess(String paramString1, String paramString2, String paramString3, String paramString4);
  
  HashMap<String, Object> validateAdjustmentTTUMProcessVISA(String paramString1, String paramString2, String paramString3, String paramString4);
  
  HashMap<String, Object> validateAdjustmentTTUMProcessQSPARC(String paramString1, String paramString2, String paramString3);
  
  boolean runAdjTTUMREFUNDVISA(String paramString1, String paramString2, String paramString3, String paramString4);
}
