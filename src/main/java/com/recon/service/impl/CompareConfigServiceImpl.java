package com.recon.service.impl;


import com.recon.dao.ICompareConfigDao;
import com.recon.model.CompareSetupBean;
import com.recon.model.ManualCompareBean;
import com.recon.model.Pos_Bean;
import com.recon.service.ICompareConfigService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CompareConfigServiceImpl implements ICompareConfigService {
  @Autowired
  ICompareConfigDao compareDao;
  
  public List<CompareSetupBean> getFileDetails() {
    return this.compareDao.getFileDetails();
  }
  
  public boolean saveCompareDetails(CompareSetupBean setupBean) throws Exception {
    return this.compareDao.saveCompareDetails(setupBean);
  }
  
  public boolean chkMain_ReconSetupDetails(CompareSetupBean setupBean) {
    return this.compareDao.chkMain_ReconSetupDetails(setupBean);
  }
  
  public ArrayList<CompareSetupBean> getCompareFiles(String type, String subcat) throws Exception {
    return this.compareDao.getCompareFiles(type, subcat);
  }
  
  public ArrayList<CompareSetupBean> getmatchcrtlist(int rec_set_id, String Cate) throws Exception {
    return this.compareDao.getmatchcrtlist(rec_set_id, Cate);
  }
  
  public ArrayList<CompareSetupBean> getrecparamlist(int rec_set_id, String Cate) throws Exception {
    return this.compareDao.getrecparamlist(rec_set_id, Cate);
  }
  
  public ArrayList<CompareSetupBean> getFileList() {
    return this.compareDao.getFileList();
  }
  
  public boolean chkFileupload(CompareSetupBean setupBean) throws Exception {
    return this.compareDao.chkFileupload(setupBean);
  }
  
  public boolean chkFileupload1(CompareSetupBean setupBean) throws Exception {
    return this.compareDao.chkFileupload1(setupBean);
  }
  
  public boolean chkFileupload2(CompareSetupBean setupBean) throws Exception {
    return this.compareDao.chkFileupload2(setupBean);
  }
  
  public HashMap<String, Object> uploadFile(CompareSetupBean setupBean, MultipartFile file) throws Exception {
    return this.compareDao.uploadFile(setupBean, file);
  }
  
  public boolean validateFile(CompareSetupBean setupBean, MultipartFile file) throws Exception {
    return this.compareDao.validateFile(setupBean, file);
  }
  
  public List<CompareSetupBean> getlastUploadDetails() throws Exception {
    return this.compareDao.getlastUploadDetails();
  }
  
  public boolean chkCompareFiles(CompareSetupBean setupBean) throws Exception {
    return this.compareDao.chkCompareFiles(setupBean);
  }
  
  public boolean CheckAlreadyProcessed(CompareSetupBean setupBean) throws Exception {
    return this.compareDao.CheckAlreadyProcessed(setupBean);
  }
  
  public String getTableName(int Fileid) throws Exception {
    return this.compareDao.getTableName(Fileid);
  }
  
  public String chkFlag(String flag, CompareSetupBean setupBean) throws Exception {
    return this.compareDao.chkFlag(flag, setupBean);
  }
  
  public boolean updateFlag(String flag, CompareSetupBean setupBean) throws Exception {
    return this.compareDao.updateFlag(flag, setupBean);
  }
  
  public boolean validate_File(String Filedate, CompareSetupBean setupBean) throws Exception {
    return this.compareDao.validate_File(Filedate, setupBean);
  }
  
  public boolean saveManCompareDetails(ManualCompareBean manualCompareBean) throws Exception {
    return this.compareDao.saveManCompareDetails(manualCompareBean);
  }
  
  public List<CompareSetupBean> getFileList(String category) {
    return this.compareDao.getFileList(category);
  }
  
  public ArrayList<CompareSetupBean> getmatchcondnlist(int rec_set_id, String Cate) throws Exception {
    return this.compareDao.getmatchcondnlist(rec_set_id, Cate);
  }
  
  public String chkUploadFlag(String flag, CompareSetupBean setupBean) {
    return this.compareDao.chkUploadFlag(flag, setupBean);
  }
  
  public boolean chkBeforeUploadFile(String flag, CompareSetupBean setupBean) throws Exception {
    return this.compareDao.chkBeforeUploadFile(flag, setupBean);
  }
  
  public boolean insertFileTranDate(CompareSetupBean setupBean) {
    return this.compareDao.insertFileTranDate(setupBean);
  }
  
  public int getrecordcount(CompareSetupBean setupBean) {
    return this.compareDao.getrecordcount(setupBean);
  }
  
  public HashMap<String, Object> uploadREV_File(CompareSetupBean setupBean, MultipartFile file) {
    return this.compareDao.uploadREV_File(setupBean, file);
  }
  
  public HashMap<String, Object> uploadREV_File2(CompareSetupBean setupBean, MultipartFile file) {
    return this.compareDao.uploadREV_File2(setupBean, file);
  }
  
  public int getREVrecordcount(CompareSetupBean setupBean) {
    return this.compareDao.getREVrecordcount(setupBean);
  }
  
  public boolean insertUploadTRAN(CompareSetupBean setupBean) {
    return this.compareDao.insertUploadTRAN(setupBean);
  }
  
  public ArrayList<Pos_Bean> getFileNameList(String filedate) throws Exception {
    return this.compareDao.getFileNameList(filedate);
  }
  
  public HashMap<String, Object> uploadFileSwitchTlf(CompareSetupBean setupBean, MultipartFile file) {
    return this.compareDao.uploadFileSwitchTlf(setupBean, file);
  }
}
