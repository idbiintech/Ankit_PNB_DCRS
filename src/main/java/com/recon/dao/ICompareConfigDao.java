package com.recon.dao;


import com.recon.model.CompareSetupBean;
import com.recon.model.ManualCompareBean;
import com.recon.model.Pos_Bean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ICompareConfigDao {
  List<CompareSetupBean> getFileDetails();
  
  boolean saveCompareDetails(CompareSetupBean paramCompareSetupBean) throws Exception;
  
  boolean chkMain_ReconSetupDetails(CompareSetupBean paramCompareSetupBean);
  
  ArrayList<CompareSetupBean> getCompareFiles(String paramString1, String paramString2) throws Exception;
  
  ArrayList<CompareSetupBean> getmatchcrtlist(int paramInt, String paramString) throws Exception;
  
  ArrayList<CompareSetupBean> getmatchcondnlist(int paramInt, String paramString) throws Exception;
  
  ArrayList<CompareSetupBean> getrecparamlist(int paramInt, String paramString) throws Exception;
  
  ArrayList<CompareSetupBean> getFileList();
  
  boolean chkFileupload(CompareSetupBean paramCompareSetupBean) throws Exception;
  
  HashMap<String, Object> uploadFile(CompareSetupBean paramCompareSetupBean, MultipartFile paramMultipartFile) throws Exception;
  
  boolean validateFile(CompareSetupBean paramCompareSetupBean, MultipartFile paramMultipartFile) throws Exception;
  
  List<CompareSetupBean> getlastUploadDetails() throws Exception;
  
  boolean chkCompareFiles(CompareSetupBean paramCompareSetupBean) throws Exception;
  
  String getTableName(int paramInt) throws Exception;
  
  String chkFlag(String paramString, CompareSetupBean paramCompareSetupBean) throws Exception;
  
  boolean updateFlag(String paramString, CompareSetupBean paramCompareSetupBean) throws Exception;
  
  boolean validate_File(String paramString, CompareSetupBean paramCompareSetupBean) throws Exception;
  
  boolean saveManCompareDetails(ManualCompareBean paramManualCompareBean) throws Exception;
  
  List<CompareSetupBean> getFileList(String paramString);
  
  boolean CheckAlreadyProcessed(CompareSetupBean paramCompareSetupBean) throws Exception;
  
  String chkUploadFlag(String paramString, CompareSetupBean paramCompareSetupBean);
  
  boolean chkBeforeUploadFile(String paramString, CompareSetupBean paramCompareSetupBean) throws Exception;
  
  boolean insertFileTranDate(CompareSetupBean paramCompareSetupBean);
  
  boolean insertUploadTRAN(CompareSetupBean paramCompareSetupBean);
  
  int getrecordcount(CompareSetupBean paramCompareSetupBean);
  
  HashMap<String, Object> uploadREV_File(CompareSetupBean paramCompareSetupBean, MultipartFile paramMultipartFile);
  
  int getREVrecordcount(CompareSetupBean paramCompareSetupBean);
  
  ArrayList<Pos_Bean> getFileNameList(String paramString) throws Exception;
  
  boolean chkFileupload1(CompareSetupBean paramCompareSetupBean) throws Exception;
  
  boolean chkFileupload2(CompareSetupBean paramCompareSetupBean) throws Exception;
  
  HashMap<String, Object> uploadREV_File2(CompareSetupBean paramCompareSetupBean, MultipartFile paramMultipartFile);
  
  HashMap<String, Object> uploadFileSwitchTlf(CompareSetupBean paramCompareSetupBean, MultipartFile paramMultipartFile);
}
