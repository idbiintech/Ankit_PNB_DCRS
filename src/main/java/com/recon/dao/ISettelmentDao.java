package com.recon.dao;


import com.recon.model.CompareSetupBean;
import com.recon.model.Gl_bean;
import com.recon.model.Mastercard_chargeback;
import com.recon.model.Rupay_Gl_repo;
import com.recon.model.Rupay_gl_Lpcases;
import com.recon.model.Rupay_gl_autorev;
import com.recon.model.Rupay_sur_GlBean;
import com.recon.model.SettlementBean;
import com.recon.model.SettlementTypeBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface ISettelmentDao {
  ArrayList<String> gettype(String paramString);
  
  List<SettlementTypeBean> getSettlmentType(String paramString1, String paramString2);
  
  ArrayList<SettlementTypeBean> getReconData(String paramString1, String paramString2, String paramString3, String paramString4);
  
  ArrayList<String> getColumnList(String paramString);
  
  int getReconDataCount(String paramString1, String paramString2, String paramString3, String paramString4);
  
  ArrayList<SettlementTypeBean> getChngReconData(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2);
  
  void manualReconToSettlement(String paramString1, String paramString2) throws Exception;
  
  int updateRecord(SettlementTypeBean paramSettlementTypeBean);
  
  List<List<String>> getReconData1(String paramString1, String paramString2, String paramString3, String paramString4);
  
  String getFileName(String paramString);
  
  Boolean checkfileprocessed(SettlementBean paramSettlementBean);
  
  Boolean checkfileprocessedCTC(SettlementBean paramSettlementBean);
  Boolean checkfileprocessedCTC3(SettlementBean paramSettlementBean);
  Boolean checkfileprocessedCTC4(SettlementBean paramSettlementBean);
  
  void generate_Reports(SettlementBean paramSettlementBean) throws Exception;
  
  void generate_Reports_ICCW(CompareSetupBean paramCompareSetupBean) throws Exception;
  
  void DeleteFiles(String paramString);
  
  void generate_ipm(String paramString) throws Exception;
  
  boolean generateCTF(SettlementBean paramSettlementBean, List<String> paramList) throws IOException;
  
  List<Mastercard_chargeback> getMastercardchargeback(String paramString) throws Exception;
  
  int Savechargeback(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9) throws Exception;
  
  List<List<Mastercard_chargeback>> GenerateReportChargebk() throws Exception;
  
  String generateChargBk(String paramString1, String paramString2, String paramString3) throws Exception;
  
  List<Gl_bean> getMastercardGet_glbalance(String paramString) throws Exception;
  
  List<Rupay_sur_GlBean> getRupaysurchargelist(String paramString) throws Exception;
  
  List<Rupay_gl_autorev> getRupayAutorevlist(String paramString) throws Exception;
  
  List<Rupay_gl_Lpcases> getRupayLpcaselist(String paramString) throws Exception;
  
  int SaveGl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, String paramString12, String paramString13, String paramString14, String paramString15, String paramString16, String paramString17, String paramString18, String paramString19) throws Exception;
  
  List<List<Rupay_Gl_repo>> GenerateGL(String paramString) throws Exception;
  
  String getSettlemntAmount(String paramString1, String paramString2) throws Exception;
}
