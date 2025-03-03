package com.recon.dao;


import com.recon.model.GenerateTTUMBean;
import com.recon.model.Mastercbs_respbean;
import java.util.List;

public interface GenerateTTUMDao {
  List<List<GenerateTTUMBean>> generateTTUM(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  void TTUMRecords(List<String> paramList, GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generateTTUMForAMEX(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generateTTUMForCARDTOCARD(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generateTTUMForMastercard_C_Repo(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generateTTUMForMastercard_Switch(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<Mastercbs_respbean>> generateTTUMForMastercard_Iss_cbs(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generateTTUMForMastercard(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generateTTUMForMastercard_Issuer(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generateTTUMForMastercard_Acq_cbs(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generateTTUMForMastercard_ATM_DCC(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generatecashnetTTUM(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<Integer> getRespCode(String paramString1, String paramString2, String paramString3, String paramString4) throws Exception;
  
  List<Integer> getRespcode(String paramString1, String paramString2, String paramString3, String paramString4) throws Exception;
  
  List<List<GenerateTTUMBean>> generateVISATTUM(GenerateTTUMBean paramGenerateTTUMBean) throws Exception;
  
  List<List<GenerateTTUMBean>> generateNFSTTUM(GenerateTTUMBean paramGenerateTTUMBean);
  
  String getLatestFileDate(GenerateTTUMBean paramGenerateTTUMBean);
}
