package com.recon.service.impl;


import com.recon.dao.ISourceDao;
import com.recon.model.CompareSetupBean;
import com.recon.model.ConfigurationBean;
import com.recon.model.SettlementBean;
import com.recon.model.Settlement_FinalBean;
import com.recon.service.ISourceService;
import com.recon.util.GenerateSettleTTUMBean;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SourceServiceImpl implements ISourceService {
  @Autowired
  ISourceDao iSourceDao;
  
  public List<ConfigurationBean> getFileDetails() throws Exception {
    return this.iSourceDao.getFileDetails();
  }
  
  public boolean updateFileDetails(ConfigurationBean ftpBean) throws Exception {
    return this.iSourceDao.updateFileDetails(ftpBean);
  }
  
  public boolean addFileSource(ConfigurationBean configBean) throws Exception {
    return this.iSourceDao.addFileSource(configBean);
  }
  
  public int getFileId(ConfigurationBean configBean) {
    return this.iSourceDao.getFileId(configBean);
  }
  
  public boolean chkTblExistOrNot(ConfigurationBean configBean) throws Exception {
    return this.iSourceDao.chkTblExistOrNot(configBean);
  }
  
  public boolean addConfigParams(ConfigurationBean configBean) throws Exception {
    return this.iSourceDao.addConfigParams(configBean);
  }
  
  public String getHeaderList(int fileId) {
    return this.iSourceDao.getHeaderList(fileId);
  }
  
  public ArrayList<ConfigurationBean> getCompareDetails(int fileId, String category, String subcat) throws Exception {
    return this.iSourceDao.getCompareDetails(fileId, category, subcat);
  }
  
  public List<ConfigurationBean> getknockoffDetails(int fileId, String category, String subcat) throws Exception {
    return this.iSourceDao.getknockoffDetails(fileId, category, subcat);
  }
  
  public List<ConfigurationBean> getknockoffcrt(int fileId, String category, String subcat) throws Exception {
    return this.iSourceDao.getknockoffcrt(fileId, category, subcat);
  }
  
  public List<CompareSetupBean> getFileList(String category, String subcat) throws Exception {
    return this.iSourceDao.getFileList(category, subcat);
  }
  
  public List<String> gettable_list() {
    return this.iSourceDao.gettable_list();
  }
  
  public List<CompareSetupBean> getFileTypeList(String category, String subcat, String table) throws Exception {
    return this.iSourceDao.getFileTypeList(category, subcat, table);
  }
  
  public List<String> getSubcategories(String category) throws Exception {
    return this.iSourceDao.getSubcategories(category);
  }
  
  public Settlement_FinalBean getReportDetails(SettlementBean settlementBean) {
    return this.iSourceDao.getReportDetails(settlementBean);
  }
  
  public List<GenerateSettleTTUMBean> generateSettlTTUM(SettlementBean settlementBean) {
    return this.iSourceDao.generateSettlTTUM(settlementBean);
  }
}
