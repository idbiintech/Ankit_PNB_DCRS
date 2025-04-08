package com.recon.service.impl;


import com.recon.dao.IReconProcessDao;
import com.recon.model.CompareBean;
import com.recon.model.CompareSetupBean;
import com.recon.service.IReconProcessService;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReconProcessServiceImpl implements IReconProcessService {
  @Autowired
  IReconProcessDao reconprocess;
  
  public String chkFileUpload(String Category, String filedate, List<CompareSetupBean> compareSetupBeans, String SubCat) throws Exception {
    return this.reconprocess.chkFileUpload(Category, filedate, compareSetupBeans, SubCat);
  }
  
  public List<CompareSetupBean> getFileList(String category, String filedate, String subcat) throws Exception {
    return this.reconprocess.getFileList(category, filedate, subcat);
  }
  
  public List<CompareSetupBean> getFileList2(String category, String filedate, String subcat, String type) throws Exception {
    return this.reconprocess.getFileList2(category, filedate, subcat, type);
  }
  
  public String validateFile(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception {
    return this.reconprocess.validateFile(category, compareSetupBeans, filedate);
  }
  
  public String validateFile1(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception {
    return this.reconprocess.validateFile1(category, compareSetupBeans, filedate);
  }
  
  public String validateFile2(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception {
    return this.reconprocess.validateFile2(category, compareSetupBeans, filedate);
  }
  
  public String validateFile3(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception {
    return this.reconprocess.validateFile3(category, compareSetupBeans, filedate);
  }
  
  public String validateFile4(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception {
    return this.reconprocess.validateFile4(category, compareSetupBeans, filedate);
  }
  
  public String validateFile1CTC(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception {
    return this.reconprocess.validateFile1CTC(category, compareSetupBeans, filedate);
  }
  
  public String validateFile1CTC2(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception {
    return this.reconprocess.validateFile1CTC2(category, compareSetupBeans, filedate);
  }
  
  public String validateFile1CTC3(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception {
    return this.reconprocess.validateFile1CTC3(category, compareSetupBeans, filedate);
  }
  public String validateFile1CTC4(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception {
	    return this.reconprocess.validateFile1CTC4(category, compareSetupBeans, filedate);
	  }
  public boolean processFile(String category, List<CompareSetupBean> compareSetupBeans, String filedate, String Createdby, String subCat) throws Exception {
    return this.reconprocess.processFile(category, compareSetupBeans, filedate, Createdby, subCat);
  }
  
  public boolean compareFiles(String category, String filedate, CompareBean setupBeans, String subcat, String dollar_val) throws Exception {
    return this.reconprocess.compareFiles(category, filedate, setupBeans, subcat, dollar_val);
  }
  
  public CompareSetupBean chkStatus(List<CompareSetupBean> compareSetupBeans, String category, String filedate) throws Exception {
    return this.reconprocess.chkStatus(compareSetupBeans, category, filedate);
  }
  
  public HashMap<String, Object> checkRupayIntRecon(String filedate, String cetegory) {
    return this.reconprocess.checkRupayIntRecon(filedate, cetegory);
  }
  
  public HashMap<String, Object> processRupayIntRecon(String filedate, String entryBy) {
    return this.reconprocess.processRupayIntRecon(filedate, entryBy);
  }
  
  public HashMap<String, Object> checkCardtoCardRecon(String filedate, String subCat) {
    return this.reconprocess.checkCardtoCardRecon(filedate, subCat);
  }
  
  public boolean CardtoCardACQPRC(String category, String filedate, String entry_by) throws ParseException, Exception {
    return this.reconprocess.CardtoCardACQPRC(category, filedate, entry_by);
  }
  
  public boolean VISACROSSPROCPOSINTDOM(String category, String filedate, String entry_by) throws ParseException, Exception {
    return this.reconprocess.VISACROSSPROCPOSINTDOM(category, filedate, entry_by);
  }
  
  public boolean VISACROSSPROCATMINTDOM(String category, String filedate, String entry_by) throws ParseException, Exception {
    return this.reconprocess.VISACROSSPROCATMINTDOM(category, filedate, entry_by);
  }
  
  public boolean VISACROSSPROCPOSDOMINT(String category, String filedate, String entry_by) throws ParseException, Exception {
    return this.reconprocess.VISACROSSPROCPOSDOMINT(category, filedate, entry_by);
  }
  
  public boolean VISACROSSPROCATMDOMINT(String category, String filedate, String entry_by) throws ParseException, Exception {
    return this.reconprocess.VISACROSSPROCATMDOMINT(category, filedate, entry_by);
  }
  
  public boolean CardtoCardACQClassify(String category, String filedate, String entry_by) throws ParseException, Exception {
    return this.reconprocess.CardtoCardACQClassify(category, filedate, entry_by);
  }
  
  public boolean CardtoCardISSClassify(String category, String filedate, String entry_by) throws ParseException, Exception {
    return this.reconprocess.CardtoCardISSClassify(category, filedate, entry_by);
  }
  
  public boolean CardtoCardISSPRC(String category, String filedate, String entry_by) throws ParseException, Exception {
    return this.reconprocess.CardtoCardISSPRC(category, filedate, entry_by);
  }
  
  public HashMap<String, Object> checkCardtoCardRawFiles(String filedate) {
    return this.reconprocess.checkCardtoCardRawFiles(filedate);
  }
  
  public HashMap<String, Object> checkCardtoCardPrevRecon(String filedate) {
    return this.reconprocess.checkCardtoCardPrevRecon(filedate);
  }
  
  public boolean iccwprocessFile(String category, String filedate, String Createdby) throws Exception {
    return this.reconprocess.iccwprocessFile(category, filedate, Createdby);
  }
  
  public boolean checkRecon(String filedate) {
    return this.reconprocess.checkRecon(filedate);
  }
  
  public boolean checkFileUp(String filedate) {
    return this.reconprocess.checkFileUp(filedate);
  }
}
