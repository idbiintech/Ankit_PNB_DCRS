package com.recon.dao;


import com.recon.model.CompareBean;
import com.recon.model.CompareSetupBean;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public interface IReconProcessDao {
  String chkFileUpload(String paramString1, String paramString2, List<CompareSetupBean> paramList, String paramString3) throws Exception;
  
  List<CompareSetupBean> getFileList(String paramString1, String paramString2, String paramString3) throws Exception;
  
  List<CompareSetupBean> getFileList2(String paramString1, String paramString2, String paramString3, String paramString4) throws Exception;
  
  String validateFile(String paramString1, List<CompareSetupBean> paramList, String paramString2) throws Exception;
  
  boolean processFile(String paramString1, List<CompareSetupBean> paramList, String paramString2, String paramString3, String paramString4) throws Exception;
  
  boolean compareFiles(String paramString1, String paramString2, CompareBean paramCompareBean, String paramString3, String paramString4) throws Exception;
  
  CompareSetupBean chkStatus(List<CompareSetupBean> paramList, String paramString1, String paramString2) throws Exception;
  
  HashMap<String, Object> processRupayIntRecon(String paramString1, String paramString2);
  
  boolean CardtoCardACQPRC(String paramString1, String paramString2, String paramString3) throws ParseException, Exception;
  
  boolean VISACROSSPROCPOSINTDOM(String paramString1, String paramString2, String paramString3) throws ParseException, Exception;
  
  boolean VISACROSSPROCATMINTDOM(String paramString1, String paramString2, String paramString3) throws ParseException, Exception;
  
  boolean VISACROSSPROCPOSDOMINT(String paramString1, String paramString2, String paramString3) throws ParseException, Exception;
  
  boolean VISACROSSPROCATMDOMINT(String paramString1, String paramString2, String paramString3) throws ParseException, Exception;
  
  HashMap<String, Object> checkCardtoCardRawFiles(String paramString);
  
  HashMap<String, Object> checkCardtoCardPrevRecon(String paramString);
  
  boolean iccwprocessFile(String paramString1, String paramString2, String paramString3);
  
  boolean checkRecon(String paramString);
  
  boolean checkFileUp(String paramString);
  
  String validateFile1(String paramString1, List<CompareSetupBean> paramList, String paramString2);
  
  String validateFile2(String paramString1, List<CompareSetupBean> paramList, String paramString2);
  
  String validateFile3(String paramString1, List<CompareSetupBean> paramList, String paramString2);
  
  String validateFile4(String paramString1, List<CompareSetupBean> paramList, String paramString2);
  
  String validateFile1CTC(String paramString1, List<CompareSetupBean> paramList, String paramString2);
  
  String validateFile1CTC2(String paramString1, List<CompareSetupBean> paramList, String paramString2);
  
  
  String validateFile1CTC3(String paramString1, List<CompareSetupBean> paramList, String paramString2);
  
  String validateFile1CTC4(String paramString1, List<CompareSetupBean> paramList, String paramString2);
  
  HashMap<String, Object> checkCardtoCardRecon(String paramString1, String paramString2);
  
  boolean CardtoCardISSPRC(String paramString1, String paramString2, String paramString3) throws ParseException, Exception;
  
  boolean CardtoCardACQClassify(String paramString1, String paramString2, String paramString3) throws ParseException, Exception;
  
  boolean CardtoCardISSClassify(String paramString1, String paramString2, String paramString3) throws ParseException, Exception;
  
  HashMap<String, Object> checkRupayIntRecon(String paramString1, String paramString2);
}
