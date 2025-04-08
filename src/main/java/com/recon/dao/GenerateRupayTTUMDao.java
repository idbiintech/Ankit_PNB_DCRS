package com.recon.dao;


import com.recon.model.GenerateTTUMBean;
import java.util.List;

public interface GenerateRupayTTUMDao {
  void getTTUMSwitchRecords(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generateSwitchTTUM(GenerateTTUMBean paramGenerateTTUMBean, int paramInt) throws Exception;
  
  List<List<GenerateTTUMBean>> generateCBSTTUM(GenerateTTUMBean paramGenerateTTUMBean, List<GenerateTTUMBean> paramList) throws Exception;
  
  void TTUMRecords(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  void TTUM_forDPart(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<GenerateTTUMBean> getCandDdifference(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> getMatchedRecordsTTUM(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  void getReportCRecords(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> GenerateRupayTTUM(GenerateTTUMBean paramGenerateTTUMBean, int paramInt) throws Exception;
  
  void getRupayTTUMRecords(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  void getReportERecords(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  void getSurchargeRecords(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generateCBSSurchargeTTUM(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> getMatchedIntTxn(GenerateTTUMBean paramGenerateTTUMBean, int paramInt) throws Exception;
  
  List<List<GenerateTTUMBean>> LevyCharges(List<List<GenerateTTUMBean>> paramList, GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  String getLatestFileDate(GenerateTTUMBean paramGenerateTTUMBean);
  
  void getFailedCBSRecords(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generateDisputeTTUM(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generateVisaDisputeTTUM(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  boolean IssurttumAlreadyGenrated(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  boolean cleanAlreadyProcessedSURTTUMRecords(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
}
