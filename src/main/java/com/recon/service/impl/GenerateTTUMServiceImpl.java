package com.recon.service.impl;


import com.recon.dao.GenerateTTUMDao;
import com.recon.model.GenerateTTUMBean;
import com.recon.model.Mastercbs_respbean;
import com.recon.service.GenerateTTUMService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenerateTTUMServiceImpl implements GenerateTTUMService {
  @Autowired
  GenerateTTUMDao generateTTUMdao;
  
  public List<List<GenerateTTUMBean>> generateTTUM(GenerateTTUMBean generateTTUMBeanObj) throws Exception {
    return this.generateTTUMdao.generateTTUM(generateTTUMBeanObj);
  }
  
  public List<List<GenerateTTUMBean>> generateTTUMForCARDTOCARD(GenerateTTUMBean generateTTUMBeanObj) throws Exception {
    return this.generateTTUMdao.generateTTUMForCARDTOCARD(generateTTUMBeanObj);
  }
  
  public void TTUMRecords(List<String> File_list, GenerateTTUMBean generateTTUMBeanObj) throws Exception {
    this.generateTTUMdao.TTUMRecords(File_list, generateTTUMBeanObj);
  }
  
  public List<List<GenerateTTUMBean>> generateTTUMForAMEX(GenerateTTUMBean generatettumBeanObj) throws Exception {
    return this.generateTTUMdao.generateTTUMForAMEX(generatettumBeanObj);
  }
  
  public List<List<GenerateTTUMBean>> generateTTUMForMastercard_C_Repo(GenerateTTUMBean generatettumBeanObj) throws Exception {
    return this.generateTTUMdao.generateTTUMForMastercard_C_Repo(generatettumBeanObj);
  }
  
  public List<List<GenerateTTUMBean>> generateTTUMForMastercard_Switch(GenerateTTUMBean generatettumBeanObj) throws Exception {
    return this.generateTTUMdao.generateTTUMForMastercard_Switch(generatettumBeanObj);
  }
  
  public List<List<Mastercbs_respbean>> generateTTUMForMastercard_Iss_cbs(GenerateTTUMBean generatettumBeanObj) throws Exception {
    return this.generateTTUMdao.generateTTUMForMastercard_Iss_cbs(generatettumBeanObj);
  }
  
  public List<List<GenerateTTUMBean>> generateTTUMForMastercard(GenerateTTUMBean generatettumBeanObj) throws Exception {
    return this.generateTTUMdao.generateTTUMForMastercard(generatettumBeanObj);
  }
  
  public List<List<GenerateTTUMBean>> generateTTUMForMastercard_Issuer(GenerateTTUMBean generatettumBeanObj) throws Exception {
    return this.generateTTUMdao.generateTTUMForMastercard_Issuer(generatettumBeanObj);
  }
  
  public List<List<GenerateTTUMBean>> generateTTUMForMastercard_Acq_cbs(GenerateTTUMBean generatettumBeanObj) throws Exception {
    return this.generateTTUMdao.generateTTUMForMastercard_Acq_cbs(generatettumBeanObj);
  }
  
  public List<List<GenerateTTUMBean>> generateTTUMForMastercard_ATM_DCC(GenerateTTUMBean generatettumBeanObj) throws Exception {
    return this.generateTTUMdao.generateTTUMForMastercard_ATM_DCC(generatettumBeanObj);
  }
  
  public List<Integer> getRespCode(String category, String subcategory, String filename, String filedate) throws Exception {
    return this.generateTTUMdao.getRespCode(category, subcategory, filename, filedate);
  }
  
  public List<List<GenerateTTUMBean>> generatecashnetTTUM(GenerateTTUMBean generatettumBeanObj) throws Exception {
    return this.generateTTUMdao.generatecashnetTTUM(generatettumBeanObj);
  }
  
  public List<List<GenerateTTUMBean>> generateVISATTUM(GenerateTTUMBean generateTTUMBean) throws Exception {
    return this.generateTTUMdao.generateVISATTUM(generateTTUMBean);
  }
  
  public List<List<GenerateTTUMBean>> generateNFSTTUM(GenerateTTUMBean generateTTUMBean) {
    return this.generateTTUMdao.generateNFSTTUM(generateTTUMBean);
  }
  
  public String getLatestFileDate(GenerateTTUMBean generateTTUMBean) {
    return this.generateTTUMdao.getLatestFileDate(generateTTUMBean);
  }
  
  public List<Integer> getRespcode(String category, String subcategory, String filename, String filedate) throws Exception {
    return this.generateTTUMdao.getRespcode(category, subcategory, filename, filedate);
  }
}
