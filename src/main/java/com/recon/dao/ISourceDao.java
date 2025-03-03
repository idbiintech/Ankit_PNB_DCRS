package com.recon.dao;


import com.recon.model.CompareSetupBean;
import com.recon.model.ConfigurationBean;
import com.recon.model.SettlementBean;
import com.recon.model.Settlement_FinalBean;
import com.recon.util.GenerateSettleTTUMBean;
import java.util.ArrayList;
import java.util.List;

public interface ISourceDao {
  List<ConfigurationBean> getFileDetails() throws Exception;
  
  boolean updateFileDetails(ConfigurationBean paramConfigurationBean) throws Exception;
  
  boolean addFileSource(ConfigurationBean paramConfigurationBean) throws Exception;
  
  int getFileId(ConfigurationBean paramConfigurationBean);
  
  boolean chkTblExistOrNot(ConfigurationBean paramConfigurationBean) throws Exception;
  
  boolean addConfigParams(ConfigurationBean paramConfigurationBean) throws Exception;
  
  String getHeaderList(int paramInt);
  
  ArrayList<ConfigurationBean> getCompareDetails(int paramInt, String paramString1, String paramString2) throws Exception;
  
  List<ConfigurationBean> getknockoffDetails(int paramInt, String paramString1, String paramString2) throws Exception;
  
  List<ConfigurationBean> getknockoffcrt(int paramInt, String paramString1, String paramString2) throws Exception;
  
  List<CompareSetupBean> getFileList(String paramString1, String paramString2) throws Exception;
  
  List<CompareSetupBean> getFileTypeList(String paramString1, String paramString2, String paramString3) throws Exception;
  
  List<String> gettable_list();
  
  List<String> getSubcategories(String paramString) throws Exception;
  
  Settlement_FinalBean getReportDetails(SettlementBean paramSettlementBean);
  
  List<GenerateSettleTTUMBean> generateSettlTTUM(SettlementBean paramSettlementBean);
}
