package com.recon.util;


import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

public class ReadNfsRawData {
  private static final Logger logger = Logger.getLogger(com.recon.util.ReadNfsRawData.class);
  
  public boolean readData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    logger.info("***** ReadNfsRawData.readData Start ****");
    try {
      boolean uploaded = false;
      logger.info(setupBean.getStSubCategory());
      if (setupBean.getStSubCategory().equalsIgnoreCase("ISSUER")) {
        logger.info("Entered CBS File is Issuer");
        uploaded = uploadIssuerData(setupBean, con, file, sourceBean);
      } else if (setupBean.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
        logger.info("Entered CBS File is Acquirer");
        uploaded = uploadAcquirerData(setupBean, con, file, sourceBean);
      } else {
        logger.info("Entered File is Wrong");
        return false;
      } 
      logger.info("***** ReadNfsRawData.readData End ****");
      return true;
    } catch (Exception e) {
      logger.error(" error in ReadNfsRawData.readData", new Exception("ReadNfsRawData.readData", e));
      return false;
    } 
  }
  
  public boolean uploadIssuerData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    logger.info("***** ReadNfsRawData.uploadIssuerData Start ****");
    int flag = 1, batch = 0, recordcount = 0;
    String cycle = "", query = "";
    String fileName = file.getOriginalFilename();
    logger.info("FileName is " + fileName);
    String[] fileNames = fileName.split("_");
    if (fileNames.length > 0) {
      cycle = fileNames[1].substring(0, 1);
      logger.info("Cycle is: " + cycle);
    } 
    if (setupBean.getFilename().equalsIgnoreCase("ICD")) {
      query = "insert into  icd_icd_iss_rawdata ( PARTICIPANT_ID ,TRANSACTION_TYPE ,FROM_ACCOUNT_TYPE ,TO_ACCOUNT_TYPE  ,TXN_SERIAL_NO ,RESPONSE_CODE ,PAN_NUMBER ,MEMBER_NUMBER ,APPROVAL_NUMBER ,SYS_TRACE_AUDIT_NO ,TRANSACTION_DATE ,TRANSACTION_TIME ,MERCHANT_CATEGORY_CD  ,CARD_ACC_SETTLE_DT  ,CARD_ACC_ID  ,CARD_ACC_TERMINAL_ID  ,CARD_ACC_TERMINAL_LOC ,ACQUIRER_ID  ,NETWORK_ID  ,ACCOUNT_1_NUMBER ,ACCOUNT_1_BRANCH_ID  ,ACCOUNT_2_NUMBER ,ACCOUNT_2_BRANCH_ID  ,TXN_CURRENCY_CODE ,TXN_AMOUNT ,ACTUAL_TXN_AMT ,TXN_ACTIVITY_FEE ,ISS_SETTLE_CURRENCY_CD ,ISS_SETTLE_AMNT  ,ISS_SETTLE_FEE ,ISS_SETTLE_PROCESS_FEE ,CARDHOLDER_BILL_CURRNCY_C  ,CARDHOLDER_BILL_AMOUNT ,CARDHOLDER_BILL_ACT_FEE  ,CARDHOLDER_BILL_PROCESS_F  ,CARDHOLDER_BILL_SERV_FEE ,TXN_ISS_CONVERSION_RT ,TXN_CARDHOLDER_CONV_RT ,PART_ID,DCRS_TRAN_NO ,CreatedDate , CreatedBy , FILEDATE,FPAN,CYCLE ,FILENAME) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,STR_to_date('" + 
        
        setupBean.getFileDate() + "','%Y/%m/%d'),?,?,?) ";
    } else if (setupBean.getFilename().equalsIgnoreCase("DFS")) {
      query = "insert into  dfs_dfs_iss_rawdata( PARTICIPANT_ID ,TRANSACTION_TYPE ,FROM_ACCOUNT_TYPE ,TO_ACCOUNT_TYPE  ,TXN_SERIAL_NO ,RESPONSE_CODE ,PAN_NUMBER ,MEMBER_NUMBER ,APPROVAL_NUMBER ,SYS_TRACE_AUDIT_NO ,TRANSACTION_DATE ,TRANSACTION_TIME ,MERCHANT_CATEGORY_CD  ,CARD_ACC_SETTLE_DT  ,CARD_ACC_ID  ,CARD_ACC_TERMINAL_ID  ,CARD_ACC_TERMINAL_LOC ,ACQUIRER_ID  ,NETWORK_ID  ,ACCOUNT_1_NUMBER ,ACCOUNT_1_BRANCH_ID  ,ACCOUNT_2_NUMBER ,ACCOUNT_2_BRANCH_ID  ,TXN_CURRENCY_CODE ,TXN_AMOUNT ,ACTUAL_TXN_AMT ,TXN_ACTIVITY_FEE ,ISS_SETTLE_CURRENCY_CD ,ISS_SETTLE_AMNT  ,ISS_SETTLE_FEE ,ISS_SETTLE_PROCESS_FEE ,CARDHOLDER_BILL_CURRNCY_C  ,CARDHOLDER_BILL_AMOUNT ,CARDHOLDER_BILL_ACT_FEE  ,CARDHOLDER_BILL_PROCESS_F  ,CARDHOLDER_BILL_SERV_FEE ,TXN_ISS_CONVERSION_RT ,TXN_CARDHOLDER_CONV_RT ,PART_ID,DCRS_TRAN_NO ,CreatedDate , CreatedBy , FILEDATE,FPAN,CYCLE ,FILENAME) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,STR_to_date('" + 
        
        setupBean.getFileDate() + "','%Y/%m/%d'),?,?,?) ";
    } else if (setupBean.getFilename().equalsIgnoreCase("JCB")) {
      query = "insert into  jcb_jcb_iss_rawdata ( PARTICIPANT_ID ,TRANSACTION_TYPE ,FROM_ACCOUNT_TYPE ,TO_ACCOUNT_TYPE  ,TXN_SERIAL_NO ,RESPONSE_CODE ,PAN_NUMBER ,MEMBER_NUMBER ,APPROVAL_NUMBER ,SYS_TRACE_AUDIT_NO ,TRANSACTION_DATE ,TRANSACTION_TIME ,MERCHANT_CATEGORY_CD  ,CARD_ACC_SETTLE_DT  ,CARD_ACC_ID  ,CARD_ACC_TERMINAL_ID  ,CARD_ACC_TERMINAL_LOC ,ACQUIRER_ID  ,NETWORK_ID  ,ACCOUNT_1_NUMBER ,ACCOUNT_1_BRANCH_ID  ,ACCOUNT_2_NUMBER ,ACCOUNT_2_BRANCH_ID  ,TXN_CURRENCY_CODE ,TXN_AMOUNT ,ACTUAL_TXN_AMT ,TXN_ACTIVITY_FEE ,ISS_SETTLE_CURRENCY_CD ,ISS_SETTLE_AMNT  ,ISS_SETTLE_FEE ,ISS_SETTLE_PROCESS_FEE ,CARDHOLDER_BILL_CURRNCY_C  ,CARDHOLDER_BILL_AMOUNT ,CARDHOLDER_BILL_ACT_FEE  ,CARDHOLDER_BILL_PROCESS_F  ,CARDHOLDER_BILL_SERV_FEE ,TXN_ISS_CONVERSION_RT ,TXN_CARDHOLDER_CONV_RT ,PART_ID,DCRS_TRAN_NO ,CreatedDate , CreatedBy , FILEDATE,FPAN,CYCLE ,FILENAME) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,STR_to_date('" + 
        
        setupBean.getFileDate() + "','%Y/%m/%d'),?,?,?) ";
    } else {
      query = "insert into nfs_nfs_iss_rawdata ( PARTICIPANT_ID ,TRANSACTION_TYPE ,FROM_ACCOUNT_TYPE ,TO_ACCOUNT_TYPE  ,TXN_SERIAL_NO ,RESPONSE_CODE ,PAN_NUMBER ,MEMBER_NUMBER ,APPROVAL_NUMBER ,SYS_TRACE_AUDIT_NO ,TRANSACTION_DATE ,TRANSACTION_TIME ,MERCHANT_CATEGORY_CD  ,CARD_ACC_SETTLE_DT  ,CARD_ACC_ID  ,CARD_ACC_TERMINAL_ID  ,CARD_ACC_TERMINAL_LOC ,ACQUIRER_ID  ,NETWORK_ID  ,ACCOUNT_1_NUMBER ,ACCOUNT_1_BRANCH_ID  ,ACCOUNT_2_NUMBER ,ACCOUNT_2_BRANCH_ID  ,TXN_CURRENCY_CODE ,TXN_AMOUNT ,ACTUAL_TXN_AMT ,TXN_ACTIVITY_FEE ,ISS_SETTLE_CURRENCY_CD ,ISS_SETTLE_AMNT  ,ISS_SETTLE_FEE ,ISS_SETTLE_PROCESS_FEE ,CARDHOLDER_BILL_CURRNCY_C  ,CARDHOLDER_BILL_AMOUNT ,CARDHOLDER_BILL_ACT_FEE  ,CARDHOLDER_BILL_PROCESS_F  ,CARDHOLDER_BILL_SERV_FEE ,TXN_ISS_CONVERSION_RT ,TXN_CARDHOLDER_CONV_RT ,PART_ID,DCRS_TRAN_NO ,CreatedDate , CreatedBy , FILEDATE,FPAN,CYCLE ,FILENAME) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,STR_to_date('" + 
        
        setupBean.getFileDate() + "','%Y/%m/%d'),?,?,?) ";
    } 
    logger.info("query==" + query);
    try {
      boolean readdata = false;
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      String thisLine = null;
      try {
        logger.info("Reading data " + (new Date()).toString());
        PreparedStatement ps = con.prepareStatement(query);
        int insrt = 0;
        while ((thisLine = br.readLine()) != null) {
          ps.setString(1, thisLine.substring(0, 3));
          ps.setString(2, thisLine.substring(3, 5));
          ps.setString(3, thisLine.substring(5, 7));
          ps.setString(4, thisLine.substring(7, 9));
          ps.setString(5, thisLine.substring(9, 21));
          ps.setString(6, thisLine.substring(21, 23));
          String pan = thisLine.substring(23, 42).trim();
          String Update_Pan = "";
          if (pan.length() <= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
            Update_Pan = String.valueOf(pan.substring(0, 6)) + "XXXXXX" + pan.substring(pan.length() - 4);
          } else if (pan.length() >= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
            Update_Pan = String.valueOf(pan.substring(0, 6)) + "XXXXXXXXX" + pan.substring(pan.length() - 4);
          } else {
            Update_Pan = null;
          } 
          ps.setString(7, Update_Pan);
          ps.setString(8, thisLine.substring(42, 43));
          ps.setString(9, thisLine.substring(43, 49));
          ps.setString(10, thisLine.substring(49, 61));
          ps.setString(11, thisLine.substring(61, 67));
          ps.setString(12, thisLine.substring(67, 73));
          ps.setString(13, thisLine.substring(73, 77));
          ps.setString(14, thisLine.substring(77, 83));
          ps.setString(15, thisLine.substring(83, 98));
          ps.setString(16, thisLine.substring(98, 106));
          ps.setString(17, thisLine.substring(106, 146));
          ps.setString(18, thisLine.substring(146, 157));
          ps.setString(19, thisLine.substring(157, 160));
          ps.setString(20, thisLine.substring(160, 179));
          ps.setString(21, thisLine.substring(179, 189));
          ps.setString(22, thisLine.substring(189, 208));
          ps.setString(23, thisLine.substring(208, 218));
          ps.setString(24, thisLine.substring(218, 221));
          ps.setString(25, 
              String.valueOf(thisLine.substring(221, 234).replaceAll("^0*", "0")) + "." + thisLine.substring(234, 236));
          ps.setString(26, 
              String.valueOf(thisLine.substring(236, 249).replaceAll("^0*", "0")) + "." + thisLine.substring(249, 251));
          ps.setString(27, thisLine.substring(251, 266));
          ps.setString(28, thisLine.substring(266, 269));
          ps.setString(29, 
              String.valueOf(thisLine.substring(269, 282).replaceAll("^0*", "0")) + "." + thisLine.substring(282, 284));
          ps.setString(30, thisLine.substring(284, 299));
          ps.setString(31, thisLine.substring(299, 314));
          ps.setString(32, thisLine.substring(314, 317));
          ps.setString(33, thisLine.substring(317, 332));
          ps.setString(34, thisLine.substring(332, 347));
          ps.setString(35, thisLine.substring(347, 362));
          ps.setString(36, thisLine.substring(362, 377));
          ps.setString(37, thisLine.substring(377, 392));
          ps.setString(38, thisLine.substring(392, 407));
          ps.setInt(39, 1);
          ps.setString(40, (String)null);
          ps.setString(41, "AUTOMATION");
          ps.setString(42, thisLine.substring(23, 42));
          ps.setString(43, cycle);
          ps.setString(44, file.getOriginalFilename());
          ps.addBatch();
          flag++;
          if (flag == 40000) {
            flag = 1;
            ps.executeBatch();
            logger.info("Executed batch is " + batch);
            recordcount++;
            batch++;
          } 
        } 
        ps.executeBatch();
        recordcount++;
        con.close();
        logger.info("Reading data " + (new Date()).toString());
        logger.info("***** ReadNfsRawData.uploadIssuerData End ****");
        if (recordcount > 0)
          return true; 
        return false;
      } catch (Exception ex) {
        logger.error(" error in ReadNfsRawData.uploadIssuerData", 
            new Exception("ReadNfsRawData.uploadIssuerData", ex));
        return false;
      } 
    } catch (Exception ex) {
      logger.error(" error in ReadNfsRawData.uploadIssuerData", 
          new Exception("ReadNfsRawData.uploadIssuerData", ex));
      return false;
    } 
  }
  
  public boolean uploadAcquirerData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    logger.info("***** ReadNfsRawData.uploadAcquirerData Start ****");
    int flag = 1, batch = 0;
    int getFileCount = 0;
    String cycle = "", query = "";
    try {
      if (setupBean.getFilename().contains("NFS")) {
        String fileName = file.getOriginalFilename();
        logger.info("FileName is " + fileName);
        String[] fileNames = fileName.split("_");
        if (fileNames.length > 0)
          cycle = fileNames[1].substring(0, 1); 
        logger.info("Cycle is: " + cycle);
      } 
      if (setupBean.getFilename().equalsIgnoreCase("ICD")) {
        query = "insert into  icd_icd_acq_rawdata (PARTICIPANT_ID,TRANSACTION_TYPE,FROM_ACCOUNT_TYPE,TO_ACCOUNT_TYPE,TXN_SERIAL_NO,RESPONSE_CODE,PAN_NUMBER,MEMBER_NUMBER,APPROVAL_NUMBER,SYS_TRACE_AUDIT_NO,TRANSACTION_DATE,TRANSACTION_TIME,MERCHANT_CATEGORY_CD,CARD_ACC_SETTLE_DT,CARD_ACC_ID,CARD_ACC_TERMINAL_ID,CARD_ACC_TERMINAL_LOC,ACQUIRER_ID\t,ACQ_SETTLE_DATE,TXN_CURRENCY_CODE,TXN_AMOUNT,ACTUAL_TXN_AMT,TXN_ACTIVITY_FEE,ACQ_SETTLE_CURRENCY_CD,ACQ_SETTLE_AMNT,ACQ_SETTLE_FEE,ACQ_SETTLE_PROCESS_FEE,TXN_ACQ_CONV_RATE,PART_ID,CREATEDDATE,CREATEDBY,FILEDATE,CYCLE,FPAN,FILENAME) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,STR_to_date('" + 
          
          setupBean.getFileDate() + "','%Y/%m/%d'),?,?,?) ";
      } else if (setupBean.getFilename().equalsIgnoreCase("DFS")) {
        query = "insert into dfs_dfs_acq_rawdata (PARTICIPANT_ID,TRANSACTION_TYPE,FROM_ACCOUNT_TYPE,TO_ACCOUNT_TYPE,TXN_SERIAL_NO,RESPONSE_CODE,PAN_NUMBER,MEMBER_NUMBER,APPROVAL_NUMBER,SYS_TRACE_AUDIT_NO,TRANSACTION_DATE,TRANSACTION_TIME,MERCHANT_CATEGORY_CD,CARD_ACC_SETTLE_DT,CARD_ACC_ID,CARD_ACC_TERMINAL_ID,CARD_ACC_TERMINAL_LOC,ACQUIRER_ID\t,ACQ_SETTLE_DATE,TXN_CURRENCY_CODE,TXN_AMOUNT,ACTUAL_TXN_AMT,TXN_ACTIVITY_FEE,ACQ_SETTLE_CURRENCY_CD,ACQ_SETTLE_AMNT,ACQ_SETTLE_FEE,ACQ_SETTLE_PROCESS_FEE,TXN_ACQ_CONV_RATE,PART_ID,CREATEDDATE,CREATEDBY,FILEDATE,CYCLE,FPAN,FILENAME) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,STR_to_date('" + 
          
          setupBean.getFileDate() + "','%Y/%m/%d'),?,?,?) ";
      } else if (setupBean.getFilename().equalsIgnoreCase("JCB")) {
        query = "insert into  jcb_jcb_acq_rawdata (PARTICIPANT_ID,TRANSACTION_TYPE,FROM_ACCOUNT_TYPE,TO_ACCOUNT_TYPE,TXN_SERIAL_NO,RESPONSE_CODE,PAN_NUMBER,MEMBER_NUMBER,APPROVAL_NUMBER,SYS_TRACE_AUDIT_NO,TRANSACTION_DATE,TRANSACTION_TIME,MERCHANT_CATEGORY_CD,CARD_ACC_SETTLE_DT,CARD_ACC_ID,CARD_ACC_TERMINAL_ID,CARD_ACC_TERMINAL_LOC,ACQUIRER_ID\t,ACQ_SETTLE_DATE,TXN_CURRENCY_CODE,TXN_AMOUNT,ACTUAL_TXN_AMT,TXN_ACTIVITY_FEE,ACQ_SETTLE_CURRENCY_CD,ACQ_SETTLE_AMNT,ACQ_SETTLE_FEE,ACQ_SETTLE_PROCESS_FEE,TXN_ACQ_CONV_RATE,PART_ID,CREATEDDATE,CREATEDBY,FILEDATE,CYCLE,FPAN,FILENAME) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,STR_to_date('" + 
          
          setupBean.getFileDate() + "','%Y/%m/%d'),?,?,?) ";
      } else {
        query = "insert into  nfs_nfs_acq_rawdata (PARTICIPANT_ID,TRANSACTION_TYPE,FROM_ACCOUNT_TYPE,TO_ACCOUNT_TYPE,TXN_SERIAL_NO,RESPONSE_CODE,PAN_NUMBER,MEMBER_NUMBER,APPROVAL_NUMBER,SYS_TRACE_AUDIT_NO,TRANSACTION_DATE,TRANSACTION_TIME,MERCHANT_CATEGORY_CD,CARD_ACC_SETTLE_DT,CARD_ACC_ID,CARD_ACC_TERMINAL_ID,CARD_ACC_TERMINAL_LOC,ACQUIRER_ID\t,ACQ_SETTLE_DATE,TXN_CURRENCY_CODE,TXN_AMOUNT,ACTUAL_TXN_AMT,TXN_ACTIVITY_FEE,ACQ_SETTLE_CURRENCY_CD,ACQ_SETTLE_AMNT,ACQ_SETTLE_FEE,ACQ_SETTLE_PROCESS_FEE,TXN_ACQ_CONV_RATE,PART_ID,CREATEDDATE,CREATEDBY,FILEDATE,CYCLE,FPAN,FILENAME) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,STR_to_date('" + 
          
          setupBean.getFileDate() + "','%Y/%m/%d'),?,?,?) ";
      } 
      logger.info("query==" + query);
      boolean readdata = false;
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      String thisLine = null;
      try {
        logger.info("Reading data " + (new Date()).toString());
        PreparedStatement ps = con.prepareStatement(query);
        int insrt = 0;
        while ((thisLine = br.readLine()) != null) {
          String[] splitarray = null;
          ps.setString(1, thisLine.substring(0, 3));
          ps.setString(2, thisLine.substring(3, 5));
          ps.setString(3, thisLine.substring(5, 7));
          ps.setString(4, thisLine.substring(7, 9));
          ps.setString(5, thisLine.substring(9, 21));
          ps.setString(6, thisLine.substring(21, 23));
          ps.setString(7, thisLine.substring(23, 42).trim());
          ps.setString(8, thisLine.substring(42, 43));
          ps.setString(9, thisLine.substring(43, 49));
          ps.setString(10, thisLine.substring(49, 61));
          ps.setString(11, thisLine.substring(61, 67));
          ps.setString(12, thisLine.substring(67, 73));
          ps.setString(13, thisLine.substring(73, 77));
          ps.setString(14, thisLine.substring(77, 83));
          ps.setString(15, thisLine.substring(83, 98));
          ps.setString(16, thisLine.substring(98, 106));
          ps.setString(17, thisLine.substring(106, 146));
          ps.setString(18, thisLine.substring(146, 157));
          ps.setString(19, thisLine.substring(157, 163));
          ps.setString(20, thisLine.substring(163, 166));
          ps.setString(21, 
              String.valueOf(thisLine.substring(166, 179).replaceAll("^0*", "0")) + "." + thisLine.substring(179, 181));
          ps.setString(22, thisLine.substring(181, 196));
          ps.setString(23, thisLine.substring(196, 211));
          ps.setString(24, thisLine.substring(211, 214));
          ps.setString(25, 
              String.valueOf(thisLine.substring(214, 227).replaceAll("^0*", "0")) + "." + thisLine.substring(227, 229));
          ps.setString(26, thisLine.substring(229, 244));
          ps.setString(27, thisLine.substring(244, 259));
          ps.setString(28, thisLine.substring(259, 274));
          ps.setInt(29, 1);
          ps.setString(30, "AUTOMATION");
          ps.setString(31, cycle);
          ps.setString(32, thisLine.substring(23, 42));
          ps.setString(33, file.getOriginalFilename());
          ps.addBatch();
          flag++;
          if (flag == 5000) {
            flag = 1;
            ps.executeBatch();
            logger.info("Executed batch is " + batch);
            batch++;
            getFileCount++;
          } 
        } 
        ps.executeBatch();
        br.close();
        ps.close();
        logger.info("Reading data " + (new Date()).toString());
        logger.info("***** ReadNfsRawData.uploadAcquirerData End ****");
        if (getFileCount > 0)
          return true; 
        return false;
      } catch (Exception ex) {
        logger.error(" error in ReadNfsRawData.uploadAcquirerData", 
            new Exception("ReadNfsRawData.uploadAcquirerData", ex));
        return false;
      } 
    } catch (Exception ex) {
      logger.error(" error in ReadNfsRawData.uploadAcquirerData", 
          new Exception("ReadNfsRawData.uploadAcquirerData", ex));
      return false;
    } 
  }
  
  public boolean iccwreadData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    logger.info("***** ReadNfsRawData.readData Start ****");
    try {
      boolean uploaded = false;
      String sk = file.getOriginalFilename();
      System.out.println("name is" + sk);
      if (file.getOriginalFilename().contains("ISS")) {
        logger.info("Entered CBS File is Issuer");
        uploaded = iccwuploadIssuerData(setupBean, con, file, sourceBean);
      } else if (file.getOriginalFilename().contains("ACQ")) {
        logger.info("Entered CBS File is Acquirer");
        uploaded = iccwuploadAcquirerData(setupBean, con, file, sourceBean);
      } else {
        logger.info("Entered File is Wrong");
        return false;
      } 
      logger.info("***** ReadNfsRawData.readData End ****");
      return true;
    } catch (Exception e) {
      logger.error(" error in ReadNfsRawData.readData", new Exception("ReadNfsRawData.readData", e));
      return false;
    } 
  }
  
  public boolean iccwuploadIssuerData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    logger.info("***** ReadNfsRawData.uploadIssuerData Start ****");
    int flag = 1, batch = 0, recordcount = 0;
    String cycle = "";
    String fileName = file.getOriginalFilename();
    logger.info("FileName is " + fileName);
    String[] fileNames = fileName.split("_");
    if (fileNames.length > 0)
      cycle = fileNames[1].substring(0, 1); 
    logger.info("Cycle is: " + cycle);
    String query = "INSERT INTO ICCW_ISS_RAWDATA (TRAN_TYPE , RRN , UPI_TRAN_ID , RESP_CODE , TRAN_DATE , TRAN_TIME , SETT_DATE , SETT_AMT , BENI_IFSC_CODE ,\r\nBENI_ACC_NUM , REM_IFSC_CODE , REM_ACC_NUM , ISS_CODE , ACQ_CODE , PAYER_CODE , PAYEE_CODE , PAYER_VPA ,PAYEE_VPA ,UMN,INIT_MODE ,\r\nPURP_CODE , FROM_ACC_TYPE , TO_ACC_TYPE , MCC , MAPPED_ID , CREATED_BY , FILEDATE ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?\r\n,?,to_date(?,'dd/mm/yyyy'))";
    logger.info("query==" + query);
    try {
      boolean readdata = false;
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      String thisLine = null;
      try {
        logger.info("Reading data " + (new Date()).toString());
        PreparedStatement ps = con.prepareStatement(query);
        int insrt = 0;
        while ((thisLine = br.readLine()) != null) {
          ps.setString(1, thisLine.substring(0, 3));
          ps.setString(2, thisLine.substring(3, 5));
          ps.setString(3, thisLine.substring(5, 7));
          ps.setString(4, thisLine.substring(7, 9));
          ps.setString(5, thisLine.substring(9, 21));
          ps.setString(6, thisLine.substring(21, 23));
          String pan = thisLine.substring(23, 42).trim();
          String Update_Pan = "";
          if (pan.length() <= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
            Update_Pan = String.valueOf(pan.substring(0, 6)) + "XXXXXX" + pan.substring(pan.length() - 4);
          } else if (pan.length() >= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
            Update_Pan = String.valueOf(pan.substring(0, 6)) + "XXXXXXXXX" + pan.substring(pan.length() - 4);
          } else {
            Update_Pan = null;
          } 
          ps.setString(7, Update_Pan);
          ps.setString(8, thisLine.substring(42, 43));
          ps.setString(9, thisLine.substring(43, 49));
          ps.setString(10, thisLine.substring(49, 61));
          ps.setString(11, thisLine.substring(61, 67));
          ps.setString(12, thisLine.substring(67, 73));
          ps.setString(13, thisLine.substring(73, 77));
          ps.setString(14, thisLine.substring(77, 83));
          ps.setString(15, thisLine.substring(83, 98));
          ps.setString(16, thisLine.substring(98, 106));
          ps.setString(17, thisLine.substring(106, 146));
          ps.setString(18, thisLine.substring(146, 157));
          ps.setString(19, thisLine.substring(157, 160));
          ps.setString(20, thisLine.substring(160, 179));
          ps.setString(21, thisLine.substring(179, 189));
          ps.setString(22, thisLine.substring(189, 208));
          ps.setString(23, thisLine.substring(208, 218));
          ps.setString(24, thisLine.substring(218, 221));
          ps.setString(25, 
              String.valueOf(thisLine.substring(221, 234).replaceAll("^0*", "0")) + "." + thisLine.substring(234, 236));
          ps.setString(26, 
              String.valueOf(thisLine.substring(236, 249).replaceAll("^0*", "0")) + "." + thisLine.substring(249, 251));
          ps.setString(27, thisLine.substring(251, 266));
          ps.setString(28, thisLine.substring(266, 269));
          ps.setString(29, 
              String.valueOf(thisLine.substring(269, 282).replaceAll("^0*", "0")) + "." + thisLine.substring(282, 284));
          ps.setString(30, thisLine.substring(284, 299));
          ps.setString(31, thisLine.substring(299, 314));
          ps.setString(32, thisLine.substring(314, 317));
          ps.setString(33, thisLine.substring(317, 332));
          ps.setString(34, thisLine.substring(332, 347));
          ps.setString(35, thisLine.substring(347, 362));
          ps.setString(36, thisLine.substring(362, 377));
          ps.setString(37, thisLine.substring(377, 392));
          ps.setString(38, thisLine.substring(392, 407));
          ps.setInt(39, 1);
          ps.setString(40, (String)null);
          ps.setString(41, "AUTOMATION");
          ps.setString(42, thisLine.substring(23, 42));
          ps.setString(43, cycle);
          ps.addBatch();
          flag++;
          if (flag == 20000) {
            flag = 1;
            ps.executeBatch();
            logger.info("Executed batch is " + batch);
            recordcount++;
            batch++;
          } 
        } 
        ps.executeBatch();
        logger.info("Updation Starting ");
        String updateQuery = "UPDATE NFS_NFS_iss_RAWDATA SET FPAN = ibkl_encrypt_decrypt.ibkl_set_encrypt_val(FPAN) WHERE CYCLE = '" + 
          cycle + "' AND FILEDATE = TO_DATE('" + setupBean.getFileDate() + 
          "','DD/MM/YYYY')";
        PreparedStatement pstmt = con.prepareStatement(updateQuery);
        pstmt.execute();
        logger.info("Updation done");
        recordcount++;
        br.close();
        ps.close();
        logger.info("Reading data " + (new Date()).toString());
        logger.info("***** ReadNfsRawData.uploadIssuerData End ****");
        if (recordcount > 0)
          return true; 
        return false;
      } catch (Exception ex) {
        logger.error(" error in ReadNfsRawData.uploadIssuerData", 
            new Exception("ReadNfsRawData.uploadIssuerData", ex));
        return false;
      } 
    } catch (Exception ex) {
      logger.error(" error in ReadNfsRawData.uploadIssuerData", 
          new Exception("ReadNfsRawData.uploadIssuerData", ex));
      return false;
    } 
  }
  
  public boolean iccwuploadAcquirerData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    logger.info("***** ReadNfsRawData.uploadAcquirerData Start ****");
    int flag = 1, batch = 0;
    int getFileCount = 0;
    String cycle = "";
    String stLine = null;
    int lineNumber = 0, batchNumber = 0, batchSize = 0;
    boolean batchExecuted = false;
    try {
      String fileName = file.getOriginalFilename();
      logger.info("FileName is " + fileName);
      String[] fileNames = fileName.split("_");
      if (fileNames.length > 0)
        cycle = fileNames[1].substring(0, 1); 
      System.out.println("setbean check" + setupBean.getCreatedBy());
      System.out.println("filedate is" + setupBean.getFileDate());
      String query = "INSERT INTO ICCW_ACQ_RAWDATA (PARTICIPANT_ID ,TRAN_TYPE , UPI_TRAN_ID ,RRN ,RESP_CODE,TRAN_DATE,TRAN_TIME, AMOUNT,ATTRIBUTE_9,MAPPER_ID,INITIATE_CODE,\r\nPURPOSE_CODE ,PAYER_PSP_CODE , MERCHANT_CATEGORY_CODE ,PAYEE_VPA,PAYEE_PSP_CODE,MCC_CODE ,PAYER_VPA, ACQ_BANK , ACQ_IFSC , INITIATION_MODE, ACQ_ACC_NUMBER , ISS_BANK ,ISS_IFSC , INITIATION_MODE_1,\r\nISS_ACC_NUMBER,CREATED_BY ,FILEDATE,CYCLE)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'),?)";
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      PreparedStatement ps = con.prepareStatement(query);
      while ((stLine = br.readLine()) != null) {
        int sr_no = 1;
        lineNumber++;
        batchExecuted = false;
        String[] splitData = stLine.split("\\,", -1);
        if (stLine.contains("HT") || stLine.contains("FT"))
          continue; 
        System.out.println("splitData.length: " + splitData.length);
        for (int i = 0; i < splitData.length; i++) {
          System.out.println(String.valueOf(i) + " " + splitData[i]);
          ps.setString(sr_no++, splitData[i]);
        } 
        ps.setString(sr_no++, setupBean.getCreatedBy());
        ps.setString(sr_no++, setupBean.getFileDate());
        ps.setString(sr_no++, cycle);
        ps.addBatch();
        batchSize++;
        if (batchSize == 10000) {
          batchNumber++;
          System.out.println("Batch Executed is " + batchNumber);
          ps.executeBatch();
          batchSize = 0;
          batchExecuted = true;
        } 
      } 
      if (!batchExecuted) {
        batchNumber++;
        System.out.println("Batch Executed is " + batchNumber);
        ps.executeBatch();
      } 
      br.close();
      ps.close();
      System.out.println("Reading data " + (new Date()).toString());
      return true;
    } catch (Exception e) {
      System.out.println("Issue at line " + stLine);
      System.out.println("Exception in uploadISSData " + e);
      return false;
    } 
  }
}
