package com.recon.service.impl;


import com.recon.service.RupayAdjustntFileUpService;
import com.recon.util.GeneralUtil;
import com.recon.util.OracleConn;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.web.multipart.MultipartFile;

public class RupayAdjustntFileUpServiceImpl extends JdbcDaoSupport implements RupayAdjustntFileUpService {
  private static final String O_ERROR_MESSAGE = "o_error_message";
  
  @Autowired
  GeneralUtil generalUtil;
  
  public HashMap<String, Object> validateAdjustmentUpload(String fileDate, String cycle, String network, String subcategory, boolean presentmentFile, String filename) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      String tableName = "";
      if (network.equalsIgnoreCase("RUPAY")) {
        if (subcategory.equalsIgnoreCase("DOMESTIC")) {
          tableName = "rupay_network_adjustment";
        } else if (!presentmentFile) {
          tableName = "rupay_international_adjustment";
        } else {
          tableName = "rupay_international_presentment";
        } 
      } else if (network.equalsIgnoreCase("QSPARC")) {
        if (subcategory.equalsIgnoreCase("DOMESTIC")) {
          tableName = "rupay_qsparc_network_adjustment";
        } else {
          tableName = "rupay_qsparc_int_network_adjustment";
        } 
      } 
      String checkUpload = "select count(*) from " + tableName + 
        " where FILEDATE  = STR_to_date(?,'%Y/%m/%d') and cycle = ? and FILENAME = ?";
      System.out.println("sql checkUpload " + checkUpload);
      int uploadCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[] { fileDate, cycle, filename }, Integer.class)).intValue();
      if (uploadCount == 0) {
        output.put("result", Boolean.valueOf(true));
      } else {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "File is already uploaded");
      } 
    } catch (Exception e) {
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occurred While checking");
      System.out.println("Exception is " + e);
    } 
    return output;
  }
  
  public HashMap<String, Object> validatePresentmentUpload(String fileDate, String cycle, String network, String subcategory, boolean presentmentFile, String filename) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      String tableName = "";
      if (filename.startsWith("Presentment") && network.equalsIgnoreCase("RUPAY") && 
        subcategory.contains("DOMESTIC")) {
        tableName = "rupay_presentment";
      } else if (filename.startsWith("IRGCS_Presentment") && network.equalsIgnoreCase("RUPAY") && 
        subcategory.contains("INTERNATIONAL")) {
        tableName = "rupay_int_presentment";
      } else if (filename.startsWith("Presentment") && network.equalsIgnoreCase("QSPARC") && 
        subcategory.contains("DOMESTIC")) {
        tableName = "qsparc_presentment";
      } else if (filename.startsWith("IRGCS_Presentment") && network.equalsIgnoreCase("QSPARC") && 
        subcategory.contains("INTERNATIONAL")) {
        tableName = "qsparc_int_presentment";
      } else if (filename.startsWith("OfflinePresentment") && network.equalsIgnoreCase("RUPAY") && 
        subcategory.contains("DOMESTIC")) {
        tableName = "offline_rupay_presentment";
      } else if (filename.startsWith("OfflinePresentment") && network.equalsIgnoreCase("RUPAY") && 
        subcategory.contains("INTERNATIONAL")) {
        tableName = "offline_rupay_int_presentment";
      } else if (filename.startsWith("OfflinePresentment") && network.equalsIgnoreCase("QSPARC") && 
        subcategory.contains("DOMESTIC")) {
        tableName = "offline_qsparc_presentment";
      } else if (filename.startsWith("OfflinePresentment") && network.equalsIgnoreCase("QSPARC") && 
        subcategory.contains("INTERNATIONAL")) {
        tableName = "offline_qsparc_int_presentment";
      } 
      String checkUpload = "select count(*) from " + tableName + 
        " where filedate  =  str_to_date(?,'%Y/%m/%d')      and  FILENAME = ?";
      System.out.println("sql checkUpload " + checkUpload);
      int uploadCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[] { fileDate, filename }, Integer.class)).intValue();
      if (uploadCount == 0) {
        output.put("result", Boolean.valueOf(true));
      } else {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "File is already uploaded");
      } 
    } catch (Exception e) {
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occurred While checking");
      System.out.println("Exception is " + e);
    } 
    return output;
  }
  
  public HashMap<String, Object> rupayAdjustmentFileUpload(String fileDate, String createdBy, String cycle, String network, MultipartFile file, String subcategory) {
    System.out.println("Inside the rupayAdjustmentFileUpload");
    HashMap<String, Object> output = new HashMap<>();
    int totalCount = 0;
    String res = "";
    String row = "";
    String line = "";
    try {
      BufferedReader csvReader1 = new BufferedReader(new InputStreamReader(file.getInputStream()));
      Connection con = getConnection();
      String tableName = "";
      if (network.equalsIgnoreCase("RUPAY")) {
        if (subcategory.equalsIgnoreCase("DOMESTIC")) {
          tableName = "rupay_network_adjustment";
        } else {
          tableName = "rupay_international_adjustment";
        } 
      } else if (subcategory.equalsIgnoreCase("DOMESTIC")) {
        tableName = "rupay_qsparc_network_adjustment";
      } else {
        tableName = "rupay_qsparc_int_network_adjustment";
      } 
      System.out.println("tableName " + tableName);
      String sql = "INSERT INTO " + tableName + 
        " (REPORT_DATE,DISPUTE_RAISE_DATE,DISPUTE_RAISED_SETTL_DATE,CASE_NUMBER,FUNCTION_CODE,FUNCTION_CODE_DESCRIPTION,PRIMARY_ACCOUNT_NUMBER,PROCESSING_CODE," + 
        "TRANSACTION_DATE,TRANSACTION_AMOUNT,TXN_CURRENCY_CODE,SETTLEMENT_AMOUNT,SETTLEMENT_CCY_CODE,TXN_SETTLEMENT_DATE,AMOUNTS_ADDITIONAL,CONTROL_NUMBER,DISPUTE_ORIGINATOR_PID," + 
        "DISPUTE_DESTINATION_PID,ACQUIRE_REF_DATA,APPROVAL_CODE,ORIGINATOR_POINT,POS_ENTRY_MODE,POS_CONDITION_CODE,ACQUIRER_INSTITUTEID_CODE,ACQUIRER_NAME_COUNTRY,ISSUER_INSTI_ID_CODE," + 
        "ISSUER_NAME_COUNTRY,CARD_TYPE,CARD_BRAND,CARD_ACCEPTOR_TERMINALID,CARD_ACCEPTOR_NAME,CARD_ACCEPT_LOCATION_ADD,CARD_ACCEPT_COUNTRY_CODE,CARD_ACCEPT_BUSS_CODE," + 
        "DISPUTE_REASON_CODE,DISPUTE_REASON_CD_DESC,DISPUTE_AMT,FULL_PARTIAL_INDICATOR,DISPUTE_MEMBER_MSG_TEXT,DISPUTE_DOCUMENT_INDICATOR,DOCUMENT_ATTACHED_DATE,MTI," + 
        "INCENTIVE_AMOUNT,TIER_CD_NONFULLFILL,TIER_CD_FULFILL,DEADLINE_DATE,DAYS_TO_ACT,DIRECTION_IW_OW,LAST_ADJ_STAGE, LAST_ADJ_DATE," + 
        " FILEDATE, CREATEDBY, CYCLE,UNQ_ID,FILENAME) " + 
        "VALUES( ? , ? , ? ,?,?,?,?,?," + 
        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, STR_to_date(?,'%Y/%m/%d') , ?, ?,?,?)";
      if (subcategory.equalsIgnoreCase("INTERNATIONAL"))
        sql = "INSERT INTO " + tableName + 
          " (REPORT_DATE,DISPUTE_RAISE_DATE,DISPUTE_RAISED_SETTL_DATE,CASE_NUMBER,Scheme_Name,Transaction_Flag," + 
          "FUNCTION_CODE,FUNCTION_CODE_DESCRIPTION,PRIMARY_ACCOUNT_NUMBER,PROCESSING_CODE," + 
          "TRANSACTION_DATE,TRANSACTION_AMOUNT,TXN_CURRENCY_CODE,SETTLEMENT_AMOUNT,SETTLEMENT_CCY_CODE,Conversion_Rate,TXN_SETTLEMENT_DATE,AMOUNTS_ADDITIONAL,CONTROL_NUMBER,DISPUTE_ORIGINATOR_PID," + 
          "DISPUTE_DESTINATION_PID,ACQUIRE_REF_DATA,APPROVAL_CODE,ORIGINATOR_POINT,POS_ENTRY_MODE,POS_CONDITION_CODE,ACQUIRER_INSTITUTEID_CODE,ACQUIRER_NAME_COUNTRY,ISSUER_INSTI_ID_CODE," + 
          "ISSUER_NAME_COUNTRY,CARD_TYPE,CARD_BRAND,CARD_ACCEPTOR_TERMINALID,CARD_ACCEPTOR_NAME,CARD_ACCEPT_LOCATION_ADD,CARD_ACCEPT_COUNTRY_CODE,CARD_ACCEPT_BUSS_CODE," + 
          "DISPUTE_REASON_CODE,DISPUTE_REASON_CD_DESC,DISPUTE_AMT,FULL_PARTIAL_INDICATOR,DISPUTE_MEMBER_MSG_TEXT,DISPUTE_DOCUMENT_INDICATOR,DOCUMENT_ATTACHED_DATE,MTI," + 
          "INCENTIVE_AMOUNT,TIER_CD_NONFULLFILL,TIER_CD_FULFILL,DEADLINE_DATE,DAYS_TO_ACT,DIRECTION_IW_OW, FILEDATE, CREATEDBY, CYCLE,FILENAME) " + 
          "VALUES(?,?,?,?,?,?,?,?,?,?,?," + 
          "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, STR_to_date(?,'%Y/%m/%d') , ?, ?,?)"; 
      PreparedStatement ps = con.prepareStatement(sql);
      int count = 1;
      int Number = 0;
      con.setAutoCommit(false);
      while ((row = csvReader1.readLine()) != null) {
        int sr_no = 1;
        Number++;
        totalCount++;
        if (row.contains("---END OF REPORT---") || row.contains("---End of Report---"))
          break; 
        if (count == 1) {
          count++;
          continue;
        } 
        if (subcategory.equalsIgnoreCase("INTERNATIONAL")) {
          line = row.replaceAll("\",", "|");
        } else {
          line = row.replaceAll("\",\"", "|");
          
          line = line.replace("\",", "|");
          line = line.replace(",\"", "|");
          line = line.replace("\"", "");
          //comment Ankit
        // line = line.replace(",", "|");
          
        } 
        String[] data = line.split("\\|", -1);
        for (int i = 0; i < data.length; i++) {
          ps.setString(sr_no, 
              (data[i] != null) ? data[i].trim().replaceAll("^\"|\"$", "").replaceAll("-", "") : "");
          sr_no++;
        } 
        if (subcategory.equalsIgnoreCase("DOMESTIC") && network.equalsIgnoreCase("RUPAY") && line.contains("|,")) {
          ps.setString(sr_no++, "18-02-23");
        } else if (subcategory.equalsIgnoreCase("DOMESTIC") && network.equalsIgnoreCase("QSPARC") && line.contains("|,")) {
          ps.setString(sr_no++, "18-02-23");
        } 
        ps.setString(sr_no++, fileDate);
        ps.setString(sr_no++, createdBy);
        ps.setString(sr_no++, cycle);
        if (subcategory.equalsIgnoreCase("DOMESTIC") && network.equalsIgnoreCase("RUPAY")) {
          if (!data[18].equals("-")) {
            ps.setString(sr_no++, (data[18] != null) ? ("C" + data[18].substring(26).trim()) : "");
          } else {
            ps.setString(sr_no++, "-");
          } 
        } else if (network.equalsIgnoreCase("QSPARC") && subcategory.equalsIgnoreCase("DOMESTIC")) {
          if (!data[18].equals("-")) {
            ps.setString(sr_no++, (data[18] != null) ? ("C" + data[18].substring(26).trim()) : "");
          } else {
            ps.setString(sr_no++, "-");
          } 
        } 
        ps.setString(sr_no++, file.getOriginalFilename());
        ps.addBatch();
        if (Number == 1000) {
          System.out.print("Executed batch");
          ps.executeBatch();
        } 
      } 
      ps.executeBatch();
      con.commit();
      res = "success";
      csvReader1.close();
      output.put("result", Boolean.valueOf(true));
      output.put("count", Integer.valueOf(totalCount));
    } catch (Exception e) {
      res = "fail";
      output.put("result", Boolean.valueOf(false));
      output.put("count", Integer.valueOf(totalCount));
      System.out.println("issue at Line number " + totalCount);
      System.out.println("Line issue " + line);
      System.out.println("Exception in reading adjustment is " + e);
      e.printStackTrace();
    } 
    return output;
  }
  
  public HashMap<String, Object> rupayIntPresentFileUpload(String fileDate, String createdBy, String cycle, String network, MultipartFile file, String subcategory) {
    HashMap<String, Object> output = new HashMap<>();
    int sr_no = 1, batchSize = 0, rowCount = 0, batchNumber = 0;
    int totalCount = 0;
    System.out.println("filename is " + file.getOriginalFilename());
    String tableName = "";
    int ind = file.getOriginalFilename().indexOf(".");
    if (file.getOriginalFilename().startsWith("Presentment") && network.equalsIgnoreCase("RUPAY") && 
      subcategory.contains("DOMESTIC")) {
      tableName = "rupay_presentment";
    } else if (file.getOriginalFilename().startsWith("IRGCS_Presentment") && network.equalsIgnoreCase("RUPAY") && 
      subcategory.contains("INTERNATIONAL")) {
      tableName = "rupay_int_presentment";
    } else if (file.getOriginalFilename().startsWith("Presentment") && network.equalsIgnoreCase("QSPARC") && 
      subcategory.contains("DOMESTIC")) {
      tableName = "qsparc_presentment";
    } else if (file.getOriginalFilename().startsWith("IRGCS_Presentment") && network.equalsIgnoreCase("QSPARC") && 
      subcategory.contains("INTERNATIONAL")) {
      tableName = "qsparc_int_presentment";
    } else if (file.getOriginalFilename().startsWith("OfflinePresentment") && network.equalsIgnoreCase("RUPAY") && 
      subcategory.contains("DOMESTIC")) {
      tableName = "offline_rupay_presentment";
    } else if (file.getOriginalFilename().startsWith("OfflinePresentment") && network.equalsIgnoreCase("RUPAY") && 
      subcategory.contains("INTERNATIONAL")) {
      tableName = "offline_rupay_int_presentment";
    } else if (file.getOriginalFilename().startsWith("OfflinePresentment") && network.equalsIgnoreCase("QSPARC") && 
      subcategory.contains("DOMESTIC")) {
      tableName = "offline_qsparc_presentment";
    } else if (file.getOriginalFilename().startsWith("OfflinePresentment") && network.equalsIgnoreCase("QSPARC") && 
      subcategory.contains("INTERNATIONAL")) {
      tableName = "offline_qsparc_int_presentment";
    } 
    String query = "";
    if (file.getOriginalFilename().startsWith("OfflinePresentment")) {
      query = "INSERT INTO " + tableName + 
        " (REPORT_DATE, PRESENTMENT_RAISE_DATE, PRESENTMENT_SETTLEMENT_DATE, FUNCTION_CODE_AND_DESCRIPTION, PRIMARY_ACCOUNT_NUMBER," + 
        " DATE_LOCAL_TRANSACTION, ACQUIRER_REFERENCE_DATA_RRN, RRN, PROCESSING_CODE, ACTION_CODE, CURRENCY_CODE_TRANSACTION, E_COMMERCE_INDICATOR, AMOUNT_TRANCACTION, AMOUNT_ADDITIONAL," + 
        " SETTLEMENT_AMOUNT_TRANSACTION, SETTLEMENT_AMOUNT_ADDITIONAL, APPROVAL_CODE, ORIGINATOR_POINT, POS_ENTRY_MODE, POS_CONDITION_CODE, ACQUIRER_INSTITUTION_ID_CODE, TRANSACTION_ORIGINATOR_INST_ID_CODE," + 
        " ACQUIRER_NAME_AND_COUNTRY, ISSUER_INSTITUTION_ID_CODE, TRANS_DEST_INST_ID_CODE, ISSUER_NAME_AND_COUNTRY, CARD_TYPE, CARD_BRAND, CARD_ACCEPTOR_TERMINAL_ID, CARD_ACCEPTOR_NAME, CARD_ACCEPTOR_LOCATION_ADDRESS, " + 
        "CARD_ACCEPTOR_COUNTRY_CODE, CARD_ACCEPTOR_BUSINESS_CODE, CARD_ACCEPTOR_ID_CODE, CARD_ACCEPTOR_STATE_NAME, CARD_ACCEPTOR_CITY, SERVICE_CODE, AGED, MTI, FILEDATE, FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    } else {
      query = "insert into " + tableName + 
        " (Report_Date, Presentment_Raise_Date, Presentment_Settlement_Date, Function_Code_and_Description, PAN, Local_Transaction, RRN, Processing_Code, Currency_Code, E_Commerce_Indicator, Amount_Transaction, Amount_Additional, Settlement_Amount_Transaction, Settlement_Amount_Additional, Approval_Code, Originator_Point, POS_Entry_Mode, POS_Condition_Code, Acquirer_Institution_ID_code, Transaction_Originator_Institution_ID_code, Acquirer_Name_and_Country, Issuer_Institution_ID_code, Transaction_Destination_Institution_ID_code, Issuer_Name_and_Country, Card_Type, Card_Brand, Card_Acceptor_Terminal_ID, Card_Acceptor_Name, Card_Acceptor_Location, Card_Acceptor_Country_Code, Card_Acceptor_Business_Code, Card_Acceptor_ID_Code, Card_Acceptor_State_Name, Card_Acceptor_City, Days_Aged, MTI, FileDate,cycle,FILENAME) values(?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
    } 
    String line = "";
    this.logger.info("query " + query);
    try {
      OracleConn oracObj = new OracleConn();
      Connection conn = oracObj.getconn();
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      PreparedStatement ps = conn.prepareStatement(query);
      conn.setAutoCommit(false);
      char quote = '"';
      String q = "'";
      char BLANK = ' ';
      if (file.getOriginalFilename().startsWith("OfflinePresentment")) {
        while ((line = br.readLine()) != null) {
          if (line.contains("Report Date") || line.contains("Presentment Raise Date") || 
            line.contains("End of Report") || line.contains("END OF REPORT"))
            continue; 
          totalCount++;
          sr_no = 1;
          String[] tempArr = line.split("\\,", -1);
          ps.setString(sr_no++, (tempArr[0] != null) ? tempArr[0].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[1] != null) ? tempArr[1].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[2] != null) ? tempArr[2].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[3] != null) ? tempArr[3].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[4] != null) ? tempArr[4].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[5] != null) ? tempArr[5].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[6] != null) ? tempArr[6].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, tempArr[6].substring(25, 37).trim());
          ps.setString(sr_no++, (tempArr[7] != null) ? tempArr[7].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[8] != null) ? tempArr[8].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[9] != null) ? tempArr[9].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[10] != null) ? tempArr[10].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[11] != null) ? tempArr[11].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[12] != null) ? tempArr[12].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[13] != null) ? tempArr[13].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[14] != null) ? tempArr[14].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[15] != null) ? tempArr[15].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[16] != null) ? tempArr[16].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[17] != null) ? tempArr[17].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[18] != null) ? tempArr[18].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[19] != null) ? tempArr[19].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[20] != null) ? tempArr[20].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[21] != null) ? tempArr[21].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[22] != null) ? tempArr[22].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[23] != null) ? tempArr[23].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[24] != null) ? tempArr[24].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[25] != null) ? tempArr[25].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[26] != null) ? tempArr[26].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[27] != null) ? tempArr[27].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[28] != null) ? tempArr[28].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[29] != null) ? tempArr[29].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[30] != null) ? tempArr[30].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[31] != null) ? tempArr[31].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[32] != null) ? tempArr[32].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[33] != null) ? tempArr[33].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[34] != null) ? tempArr[34].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[35] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[36] != null) ? tempArr[36].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, (tempArr[37] != null) ? tempArr[37].replace(quote, BLANK).trim() : "");
          ps.setString(sr_no++, fileDate);
          ps.setString(sr_no++, file.getOriginalFilename());
          ps.addBatch();
          rowCount++;
          batchSize++;
          if (batchSize == 10000) {
            batchNumber++;
            System.out.println("Batch Executed " + batchNumber);
            batchSize = 0;
            ps.executeBatch();
            continue;
          } 
          batchNumber++;
          System.out.println("Batch Executed " + batchNumber);
          batchSize = 0;
          ps.executeBatch();
        } 
      } else {
        while ((line = br.readLine()) != null) {
          if (line.contains("Report Date") || line.contains("Presentment Raise Date") || 
            line.contains("End of Report") || line.contains("END OF REPORT"))
            continue; 
          totalCount++;
          sr_no = 1;
          String[] tempArr = line.split("\\,", -1);
          ps.setString(sr_no++, 
              (tempArr[0] != null) ? tempArr[0].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[1] != null) ? tempArr[1].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[2] != null) ? tempArr[2].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[3] != null) ? tempArr[3].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[4] != null) ? tempArr[4].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[5] != null) ? tempArr[5].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[6] != null) ? tempArr[6].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[7] != null) ? tempArr[7].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[8] != null) ? tempArr[8].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[9] != null) ? tempArr[9].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[10] != null) ? tempArr[10].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[11] != null) ? tempArr[11].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[12] != null) ? tempArr[12].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[13] != null) ? tempArr[13].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[14] != null) ? tempArr[14].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[15] != null) ? tempArr[15].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[16] != null) ? tempArr[16].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[17] != null) ? tempArr[17].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[18] != null) ? tempArr[18].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[19] != null) ? tempArr[19].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[20] != null) ? tempArr[20].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[21] != null) ? tempArr[21].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[22] != null) ? tempArr[22].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[23] != null) ? tempArr[23].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[24] != null) ? tempArr[24].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[25] != null) ? tempArr[25].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[26] != null) ? tempArr[26].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[27] != null) ? tempArr[27].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[28] != null) ? tempArr[28].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[29] != null) ? tempArr[29].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[30] != null) ? tempArr[30].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[31] != null) ? tempArr[31].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[32] != null) ? tempArr[32].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[33] != null) ? tempArr[33].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[34] != null) ? tempArr[34].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, 
              (tempArr[35] != null) ? tempArr[35].replace(quote, BLANK).replaceAll(q, "").trim() : "");
          ps.setString(sr_no++, fileDate);
          ps.setString(sr_no++, cycle);
          ps.setString(sr_no++, file.getOriginalFilename());
          ps.addBatch();
          rowCount++;
          batchSize++;
          if (batchSize == 10000) {
            batchNumber++;
            System.out.println("Batch Executed " + batchNumber);
            batchSize = 0;
            ps.executeBatch();
            continue;
          } 
          ps.executeBatch();
        } 
      } 
      conn.commit();
      br.close();
      output.put("result", Boolean.valueOf(true));
      output.put("count", Integer.valueOf(totalCount));
    } catch (Exception e) {
      output.put("result", Boolean.valueOf(false));
      output.put("count", Integer.valueOf(totalCount));
      System.out.println("issue at Line number " + totalCount);
      System.out.println("Line issue " + line);
      System.out.println("Exception in reading adjustment is " + e);
      e.printStackTrace();
    } 
    return output;
  }
  
  public HashMap<String, Object> validateAdjustmentTTUM(String fileDate, String adjType, String cate) {
    HashMap<String, Object> output = new HashMap<>();
    int uploadedCount = 0;
    String checkUpload = "";
    String atype = "";
    try {
      if (cate.equalsIgnoreCase("RUPAY")) {
        if (adjType.equalsIgnoreCase("Partial Chargeback Acceptance")) {
          String type = "RePresentment Raise";
          checkUpload = "select count(*) from rupay_network_adjustment where filedate  =str_to_date('" + fileDate + "','%Y/%m/%d') ";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } else {
          checkUpload = "select count(*) from rupay_network_adjustment where filedate =str_to_date('" + fileDate + "','%Y/%m/%d')";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } 
        this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
        if (uploadedCount > 0) {
          if (adjType.equalsIgnoreCase("REFUND")) {
            checkUpload = "select count(*) from rupay_refund_report where filedate =str_to_date(?,'%Y/%m/%d') and TYPE = 'RUPAY_TTUM'";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[] { fileDate }, Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from rupay_adjustment_ttum where filedate  =str_to_date('" + fileDate + "','%Y/%m/%d')  and TTUM_TYPE='Rupay_CHB' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from rupay_adjustment_ttum where filedate =str_to_date('" + fileDate + "','%Y/%m/%d')  and TTUM_TYPE='Rupay_CHBD'  ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Partial Chargeback Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from  rupay_adjustment_ttum  where  filedate = str_to_date('" + fileDate + "','%Y/%m/%d') and TTUM_TYPE='Rupay_PCHB'  ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } 
          this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
          if (uploadedCount == 0) {
            output.put("result", Boolean.valueOf(true));
          } else {
            output.put("result", Boolean.valueOf(false));
            output.put("msg", String.valueOf(cate) + " Adjustment TTUM Already Proccessed");
          } 
        } else {
          output.put("result", Boolean.valueOf(false));
          output.put("msg", String.valueOf(adjType) + " Adjustment file not uploaded");
        } 
      } else if (cate.equalsIgnoreCase("RUPAY_INT")) {
        checkUpload = "select count(*) from rupay_network_adjustment where filedate =str_to_date('" + fileDate + "','%Y/%m/%d')  and FUNCTION_CODE_DESCRIPTION like '%" + adjType + "%'";
        uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
        if (uploadedCount > 0) {
          if (adjType.equalsIgnoreCase("REFUND")) {
            checkUpload = "select count(*) from rupay_refund_report where filedate =str_to_date(?,'%Y/%m/%d')  and TYPE = 'RUPAY_INT_TTUM'";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[] { fileDate }, Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from  rupay_adjustment_ttum where filedate =str_to_date('" + fileDate + "','%Y/%m/%d')  and TTUM_TYPE='Rupay_CHB' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from   rupay_adjustment_ttum  where  filedate =str_to_date('" + fileDate + "','%Y/%m/%d')  and TTUM_TYPE='Rupay_CHBD'  ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } 
          this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
          if (uploadedCount == 0) {
            output.put("result", Boolean.valueOf(true));
          } else {
            output.put("result", Boolean.valueOf(false));
            output.put("msg", String.valueOf(cate) + " Adjustment TTUM Already Proccessed");
          } 
        } else {
          output.put("result", Boolean.valueOf(false));
          output.put("msg", String.valueOf(adjType) + " Adjustment file not uploaded");
        } 
      } else if (cate.equalsIgnoreCase("RUPAY_INT_MFC")) {
        checkUpload = "select count(*) from rupay_network_adjustment where filedate =str_to_date('" + fileDate + "','%Y/%m/%d')  and FUNCTION_CODE_DESCRIPTION like '%" + adjType + "%'";
        uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
        if (uploadedCount > 0) {
          if (adjType.equalsIgnoreCase("REFUND")) {
            checkUpload = "select count(*) from rupay_refund_report where filedate =str_to_date(?,'%Y/%m/%d')  and TYPE = 'RUPAY_INT_MFC_TTUM'";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[] { fileDate }, Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from  rupay_adjustment_ttum where filedate =str_to_date('" + fileDate + "','%Y/%m/%d')  and TTUM_TYPE='Rupay_CHB' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from   rupay_adjustment_ttum  where  filedate =str_to_date('" + fileDate + "','%Y/%m/%d')  and TTUM_TYPE='Rupay_CHBD'  ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } 
          this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
          if (uploadedCount == 0) {
            output.put("result", Boolean.valueOf(true));
          } else {
            output.put("result", Boolean.valueOf(false));
            output.put("msg", String.valueOf(cate) + " Adjustment TTUM Already Proccessed");
          } 
        } else {
          output.put("result", Boolean.valueOf(false));
          output.put("msg", String.valueOf(adjType) + " Adjustment file not uploaded");
        } 
      } else if (cate.equalsIgnoreCase("MASTERCARD")) {
        checkUpload = "select count(*) from mastercard_pos_rawdata where filedate =str_to_date('" + fileDate + "','%Y/%m/%d') ";
        uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
        if (uploadedCount > 0) {
          if (adjType.equalsIgnoreCase("REFUND")) {
            checkUpload = "select count(*) from  mc_dom_refund_ttum where filedate =str_to_date('" + fileDate + "','%Y/%m/%d')  and REF_TYPE='DOMESTIC'";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], 
                Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from rupay_adjustment_ttum where filedate =str_to_date('" + fileDate + "','%Y/%m/%d')   and TTUM_TYPE='Rupay_CHB' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from  rupay_adjustment_ttum where  filedate =str_to_date('" + fileDate + "','%Y/%m/%d')  and TTUM_TYPE='Rupay_CHBD'  ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } 
          this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
          if (uploadedCount == 0) {
            output.put("result", Boolean.valueOf(true));
          } else {
            output.put("result", Boolean.valueOf(false));
            output.put("msg", String.valueOf(cate) + " Adjustment TTUM Already Proccessed");
          } 
        } else {
          output.put("result", Boolean.valueOf(false));
          output.put("msg", String.valueOf(adjType) + " Adjustment file not uploaded");
        } 
      } else if (cate.equalsIgnoreCase("MASTERCARD_INT")) {
        checkUpload = "select count(*) from mastercard_pos_rawdata where filedate =str_to_date('" + fileDate + "','%Y/%m/%d')  ";
        uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
        if (uploadedCount > 0) {
          if (adjType.equalsIgnoreCase("REFUND")) {
            checkUpload = "select count(*) from  mc_dom_refund_ttum where filedate =str_to_date('" + fileDate + "','%Y/%m/%d')  and REF_TYPE='INTERNATIONAL'";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], 
                Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from rupay_adjustment_ttum where filedate =str_to_date('" + fileDate + "','%Y/%m/%d')   and TTUM_TYPE='Rupay_CHB' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from rupay_adjustment_ttum where filedate =str_to_date('" + fileDate + "','%Y/%m/%d')  and TTUM_TYPE='Rupay_CHBD'  ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } 
          this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
          if (uploadedCount == 0) {
            output.put("result", Boolean.valueOf(true));
          } else {
            output.put("result", Boolean.valueOf(false));
            output.put("msg", String.valueOf(cate) + " Adjustment TTUM Already Proccessed");
          } 
        } else {
          output.put("result", Boolean.valueOf(false));
          output.put("msg", String.valueOf(adjType) + " Adjustment file not uploaded");
        } 
      } else {
        if (adjType.equalsIgnoreCase("Partial Chargeback Acceptance")) {
          String type = "RePresentment Raise";
          checkUpload = "select count(*) from rupay_qsparc_network_adjustment where filedate  = str_to_date('" + fileDate + "','%Y/%m/%d')  ";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } else {
          checkUpload = "select count(*) from rupay_qsparc_network_adjustment where filedate = str_to_date('" + fileDate + "','%Y/%m/%d') ";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } 
        this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
        if (uploadedCount > 0) {
          if (adjType.equalsIgnoreCase("REFUND")) {
            checkUpload = "select count(*) from  rupay_refund_report where filedate = str_to_date(?,'%Y/%m/%d') and TYPE = 'QSPARC_TTUM'";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[] { fileDate }, Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from  rupay_adjustment_ttum where filedate  = str_to_date('" + fileDate + "','%Y/%m/%d') and TTUM_TYPE='QSPARC_CHB'  ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from  rupay_adjustment_ttum where filedate =str_to_date('" + fileDate + "','%Y/%m/%d')and TTUM_TYPE ='QSPARC_CHBD' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Partial Chargeback Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from rupay_adjustment_ttum where filedate  =str_to_date('" + fileDate + "','%Y/%m/%d') and TTUM_TYPE ='QSPARC_PCHB' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } 
          this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
          if (uploadedCount == 0) {
            output.put("result", Boolean.valueOf(true));
          } else {
            output.put("result", Boolean.valueOf(false));
            output.put("msg", String.valueOf(cate) + " Adjustment TTUM Already Proccessed");
          } 
        } else {
          output.put("result", Boolean.valueOf(false));
          output.put("msg", String.valueOf(adjType) + " Adjustment file not uploaded");
        } 
      } 
    } catch (Exception e) {
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occurred While checking");
      System.out.println("Exception is " + e);
    } 
    return output;
  }
  
  public HashMap<String, Object> validateAdjustmentTTUMVISA(String fileDate, String adjType, String cate) {
    HashMap<String, Object> output = new HashMap<>();
    int uploadedCount = 0;
    String checkUpload = "";
    String mdate = this.generalUtil.SkDateFunction(fileDate);
    System.out.println("mdate is" + mdate);
    String sdate = "03-Oct-2023";
    System.out.println("sdate is" + sdate);
    String atype = "";
    try {
      checkUpload = "select count(*) from visa_ep706_rawdata where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
        fileDate + "','DD-MM-YY') ";
      uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
      this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
      if (uploadedCount > 0) {
        if (cate.equalsIgnoreCase("DOMESTIC")) {
          if (adjType.equalsIgnoreCase("REFUND")) {
            checkUpload = "select count(*) from VISA_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date(?,'DD-MM-YY') AND TTUM_TYPE='VISA_DOMb_REFUND' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[] { fileDate }, Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY')  and TTUM_TYPE='Rupay_CHB' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where  to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY') and TTUM_TYPE='Rupay_CHBD'  ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } 
          this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
          if (uploadedCount == 0) {
            output.put("result", Boolean.valueOf(true));
          } else {
            output.put("result", Boolean.valueOf(false));
            output.put("msg", String.valueOf(cate) + " Adjustment TTUM Already Proccessed");
          } 
        } else {
          if (adjType.equalsIgnoreCase("REFUND")) {
            checkUpload = "select count(*) from VISA_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date(?,'DD-MM-YY') AND TTUM_TYPE='VISA_INT_REFUND' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[] { fileDate }, Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY')  and TTUM_TYPE='Rupay_CHB' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where  to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY') and TTUM_TYPE='Rupay_CHBD'  ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } 
          this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
          if (uploadedCount == 0) {
            output.put("result", Boolean.valueOf(true));
          } else {
            output.put("result", Boolean.valueOf(false));
            output.put("msg", String.valueOf(cate) + " Adjustment TTUM Already Proccessed");
          } 
        } 
      } else {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", String.valueOf(adjType) + " Adjustment file not uploaded");
      } 
    } catch (Exception e) {
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occurred While checking");
      System.out.println("Exception is " + e);
    } 
    return output;
  }
  
  public HashMap<String, Object> validateAdjustmentTTUMProcess(String fileDate, String adjType, String cate, String TTUM_TYPE) {
    HashMap<String, Object> output = new HashMap<>();
    int uploadedCount = 0;
    String checkUpload = "";
    try {
      if (TTUM_TYPE.contains("EXCEL")) {
        if (cate.equalsIgnoreCase("RUPAY")) {
          if (adjType.equalsIgnoreCase("REFUND")) {
            checkUpload = "select count(*) from RUPAY_REFUND_REPORT where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY') and TYPE = 'RUPAY_TTUM'";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY') and TTUM_TYPE='Rupay_CHB' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY') AND TTUM_TYPE='Rupay_CHBD'  ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Partial Chargeback Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY') AND TTUM_TYPE='Rupay_PCHB'  ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } 
        } else if (cate.equalsIgnoreCase("RUPAY_INT")) {
          if (adjType.equalsIgnoreCase("REFUND")) {
            checkUpload = "select count(*) from RUPAY_REFUND_REPORT where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY') and TYPE = 'RUPAY_INT_TTUM'";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } 
        } else if (cate.equalsIgnoreCase("MASTERCARD")) {
          if (adjType.equalsIgnoreCase("REFUND")) {
            checkUpload = "select count(*) from MC_DOM_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY') and REF_TYPE='DOMESTIC'";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } 
        } else if (cate.equalsIgnoreCase("MASTERCARD_INT")) {
          if (adjType.equalsIgnoreCase("REFUND")) {
            checkUpload = "select count(*) from MC_DOM_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY') and REF_TYPE='INTERNATIONAL' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } 
        } else if (adjType.equalsIgnoreCase("REFUND")) {
          checkUpload = "select count(*) from RUPAY_REFUND_REPORT where to_DATE(filedate,'DD-MM-YY')  =TO_date('" + 
            fileDate + "','DD-MM-YY') and TYPE = 'QSPARC_TTUM'";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
          System.out.println("filedate passing for validation check " + fileDate);
          checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
            fileDate + "','DD-MM-YY') and TTUM_TYPE='QSPARC_CHB'  ";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
          System.out.println("filedate passing for validation check " + fileDate);
          checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  =TO_date('" + 
            fileDate + "','DD-MM-YY') and TTUM_TYPE ='QSPARC_CHBD' ";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } 
      } else if (cate.equalsIgnoreCase("RUPAY")) {
        if (adjType.equalsIgnoreCase("REFUND")) {
          checkUpload = "select count(*) from RUPAY_REFUND_REPORT where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
            fileDate + "','DD-MM-YY') and TYPE = 'RUPAY_TTUM'";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
          System.out.println("filedate passing for validation check " + fileDate);
          checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
            fileDate + "','DD-MM-YY') and TTUM_TYPE='Rupay_CHB' ";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
          System.out.println("filedate passing for validation check " + fileDate);
          checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
            fileDate + "','DD-MM-YY') AND TTUM_TYPE='Rupay_CHBD'  ";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } 
      } else if (cate.equalsIgnoreCase("RUPAY_INT")) {
        if (adjType.equalsIgnoreCase("REFUND")) {
          checkUpload = "select count(*) from RUPAY_REFUND_REPORT where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
            fileDate + "','DD-MM-YY') and TYPE = 'RUPAY_INT_TTUM'";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } 
      } else if (cate.equalsIgnoreCase("MASTERCARD")) {
        if (adjType.equalsIgnoreCase("REFUND")) {
          checkUpload = "select count(*) from MC_DOM_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
            fileDate + "','DD-MM-YY') and REF_TYPE='DOMESTIC' ";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } 
      } else if (cate.equalsIgnoreCase("MASTERCARD_INT")) {
        if (adjType.equalsIgnoreCase("REFUND")) {
          checkUpload = "select count(*) from MC_DOM_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
            fileDate + "','DD-MM-YY') and REF_TYPE='INTERNATIONAL' ";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } 
      } else if (adjType.equalsIgnoreCase("REFUND")) {
        checkUpload = "select count(*) from RUPAY_REFUND_REPORT where to_DATE(filedate,'DD-MM-YY')  =TO_date('" + 
          fileDate + "','DD-MM-YY') and TYPE = 'QSPARC_TTUM'";
        uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
      } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
        System.out.println("filedate passing for validation check " + fileDate);
        checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
          fileDate + "','DD-MM-YY') and TTUM_TYPE='QSPARC_CHB'  ";
        uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
      } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
        System.out.println("filedate passing for validation check " + fileDate);
        checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  =TO_date('" + 
          fileDate + "','DD-MM-YY') and TTUM_TYPE ='QSPARC_CHBD' ";
        uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
      } else if (adjType.equalsIgnoreCase("Partial Chargeback Acceptance")) {
        System.out.println("filedate passing for validation check " + fileDate);
        checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  =TO_date('" + 
          fileDate + "','DD-MM-YY') and TTUM_TYPE ='QSPARC_PCHB' ";
        uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
      } 
      this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
      if (uploadedCount > 0) {
        System.out.println("success" + uploadedCount);
        output.put("result", Boolean.valueOf(true));
      } else {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "TTUM is not processed");
      } 
    } catch (Exception e) {
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occurred While checking");
      System.out.println("Exception is " + e);
    } 
    return output;
  }
  
  public HashMap<String, Object> validateAdjustmentTTUMProcessVISA(String fileDate, String adjType, String cate, String TTUM_TYPE) {
    HashMap<String, Object> output = new HashMap<>();
    int uploadedCount = 0;
    String checkUpload = "";
    try {
      if (TTUM_TYPE.contains("EXCEL")) {
        if (cate.equalsIgnoreCase("DOMESTIC")) {
          if (adjType.equalsIgnoreCase("REFUND")) {
            checkUpload = "select count(*) from VISA_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY') AND TTUM_TYPE='VISA_DOM_REFUND'";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY') and TTUM_TYPE='Rupay_CHB' ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
            System.out.println("filedate passing for validation check " + fileDate);
            checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
              fileDate + "','DD-MM-YY') TTUM_TYPE='Rupay_CHBD'  ";
            uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
          } 
        } else if (adjType.equalsIgnoreCase("REFUND")) {
          checkUpload = "select count(*) from VISA_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
            fileDate + "','DD-MM-YY') AND TTUM_TYPE='VISA_INT_REFUND'";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
          System.out.println("filedate passing for validation check " + fileDate);
          checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
            fileDate + "','DD-MM-YY') and TTUM_TYPE='QSPARC_CHB'  ";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
          System.out.println("filedate passing for validation check " + fileDate);
          checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  =TO_date('" + 
            fileDate + "','DD-MM-YY') and TTUM_TYPE ='QSPARC_CHBD' ";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } 
      } else if (cate.equalsIgnoreCase("DOMESTIC")) {
        if (adjType.equalsIgnoreCase("REFUND")) {
          checkUpload = "select count(*) from VISA_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
            fileDate + "','DD-MM-YY') AND TTUM_TYPE='VISA_DOM_REFUND'";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
          System.out.println("filedate passing for validation check " + fileDate);
          checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
            fileDate + "','DD-MM-YY') and TTUM_TYPE='Rupay_CHB' ";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
          System.out.println("filedate passing for validation check " + fileDate);
          checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
            fileDate + "','DD-MM-YY') TTUM_TYPE='Rupay_CHBD'  ";
          uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
        } 
      } else if (adjType.equalsIgnoreCase("REFUND")) {
        checkUpload = "select count(*) from VISA_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
          fileDate + "','DD-MM-YY') AND TTUM_TYPE='VISA_INT_REFUND'";
        uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
      } else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
        System.out.println("filedate passing for validation check " + fileDate);
        checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('" + 
          fileDate + "','DD-MM-YY') and TTUM_TYPE='QSPARC_CHB'  ";
        uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
      } else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
        System.out.println("filedate passing for validation check " + fileDate);
        checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  =TO_date('" + 
          fileDate + "','DD-MM-YY') and TTUM_TYPE ='QSPARC_CHBD' ";
        uploadedCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
      } 
      this.logger.info("checkUpload " + checkUpload + " " + uploadedCount);
      if (uploadedCount > 0) {
        System.out.println("success" + uploadedCount);
        output.put("result", Boolean.valueOf(true));
      } else {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "TTUM is not processed");
      } 
    } catch (Exception e) {
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occurred While checking");
      System.out.println("Exception is " + e);
    } 
    return output;
  }
  
  public boolean runAdjTTUM(String fileDate, String adjType, String createdBy, String cate) {
    Map<String, Object> inParams = new HashMap<>();
    Map<String, Object> outParams2 = new HashMap<>();
    System.out.println("subcate is " + cate);
    try {
      AdjTTUMProc exe = new AdjTTUMProc( getJdbcTemplate());
      inParams.put("FILEDT", fileDate);
      inParams.put("USER_ID", createdBy);
      inParams.put("ADJTYPE", adjType);
      inParams.put("cate", cate);
      outParams2 = exe.execute(inParams);
      this.logger.info("OUT PARAMoutParams2" + outParams2.toString());
      if (outParams2 != null && outParams2.get("msg") != null) {
        this.logger.info("OUT PARAM IS " + outParams2.get("msg"));
        return false;
      } 
      return true;
    } catch (Exception e) {
      this.logger.info("Exception is " + e);
      return false;
    } 
  }
  
  private class AdjTTUMProc extends StoredProcedure {
		private static final String insert_proc = "RUPAY_QSPARC_ADJ_TTUM_PNB";

		public AdjTTUMProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE",OracleTypes.INTEGER));
			declareParameter(new SqlParameter("ADJTYPE", Types.VARCHAR));
			declareParameter(new SqlParameter("cate", Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
  public boolean runAdjTTUMREFUND(String fileDate, String adjType, String createdBy, String cate) {
    Map<String, Object> inParams = new HashMap<>();
    Map<String, Object> outParams2 = new HashMap<>();
    System.out.println("subcate is " + cate);
    try {
      AdjTTUMProcREFUND exe = new AdjTTUMProcREFUND( getJdbcTemplate());
      inParams.put("FILE_DATE", fileDate);
      inParams.put("CATEGORY", cate);
      inParams.put("USER_ID", createdBy);
      outParams2 = exe.execute(inParams);
      this.logger.info("OUT PARAMoutParams2" + outParams2.toString());
      if (outParams2 != null && outParams2.get("msg") != null) {
        this.logger.info("OUT PARAM IS " + outParams2.get("msg"));
        return false;
      } 
      return true;
    } catch (Exception e) {
      this.logger.info("Exception is " + e);
      return false;
    } 
  }
  private class AdjTTUMProcREFUND extends StoredProcedure {
		private static final String insert_proc = "REFUND_TTUM";

		public AdjTTUMProcREFUND(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILE_DATE", Types.VARCHAR));
			declareParameter(new SqlParameter("CATEGORY", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE",OracleTypes.INTEGER));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

  
  public boolean runMCTTUMREFUND(String fileDate, String subcate, String createdBy, String cate) {
    Map<String, Object> inParams = new HashMap<>();
    Map<String, Object> outParams2 = new HashMap<>();
    System.out.println("subcate is " + cate);
    try {
      AdjTTUMProcREFUNDMC exe = new AdjTTUMProcREFUNDMC( getJdbcTemplate());
      inParams.put("V_FILEDATE", fileDate);
      inParams.put("V_TYPE", subcate);
      outParams2 = exe.execute(inParams);
      this.logger.info("OUT PARAMoutParams2" + outParams2.toString());
      if (outParams2 != null && outParams2.get("msg") != null) {
        this.logger.info("OUT PARAM IS " + outParams2.get("msg"));
        return false;
      } 
      return true;
    } catch (Exception e) {
      logger.info("Exception is " + e);
      return false;
    } 
  }
  private class AdjTTUMProcREFUNDMC extends StoredProcedure {
		private static final String insert_proc = "MASTERCARD_DOM_REFUND_TTUM";

		public AdjTTUMProcREFUNDMC(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_TYPE", Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
  
  public boolean runAdjTTUMREFUNDVISA(String fileDate, String adjType, String createdBy, String cate) {
    Map<String, Object> inParams = new HashMap<>();
    Map<String, Object> outParams2 = new HashMap<>();
    System.out.println("subcate is " + cate);
    try {
      AdjTTUMProcREFUNDVISA exe = new AdjTTUMProcREFUNDVISA( getJdbcTemplate());
      inParams.put("filedt", fileDate);
      inParams.put("ERF_TYPE", cate);
      outParams2 = exe.execute(inParams);
      this.logger.info("OUT PARAMoutParams2" + outParams2.toString());
      if (outParams2 != null && outParams2.get("msg") != null) {
        this.logger.info("OUT PARAM IS " + outParams2.get("msg"));
        return false;
      } 
      return true;
    } catch (Exception e) {
      this.logger.info("Exception is " + e);
      return false;
    } 
  }
  

	// ADJ ttum detailed
	private class AdjTTUMProcREFUNDVISA extends StoredProcedure {
		private static final String insert_proc = "VISA_REF_TTUM";

		public AdjTTUMProcREFUNDVISA(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("filedt", Types.VARCHAR));
			declareParameter(new SqlParameter("ERF_TYPE", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE",OracleTypes.INTEGER));
			// declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
  @Override
	public List<Object> getAdjTTUM(String fileDate, String adjType, String subcate) {
		List<Object> data = new ArrayList<Object>();
		System.out.println("Coming date is" + fileDate);
		String sdate = generalUtil.DateFunction(fileDate);
		String zdate = generalUtil.FunctionOfDate(fileDate);
		System.out.println("adj type in download is" + adjType);

		try {
			String getData1 = null;// ,getData2 = null;
			List<Object> DailyData = new ArrayList<Object>();
			if (subcate.equals("INTERNATIONAL")) {
				adjType = "INT_REFUND";
			}

			System.out.println("ADJ TYPE IS " + adjType);

			// CHARGEBACK RAISE
			if (adjType.equals("REFUND")) {
				System.out.println("DATE IS  " + fileDate);

				getData1 = "SELECT ACC_TYPE  AS ACC_TYPE , ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRIM(TO_CHAR(reference_amount,'999999.99')) as TRANSACTION_AMOUNT,"
						+ "REMARKS as TRANSACTION_PARTICULAR,REFERENCE_NUMBER AS REMARKS , DISP_DATE AS DISP_DATE"
						+ ", FILEDATE AS FILEDATE,CYCLE AS CYCLE " + " FROM   RUPAY_ADJ_TTUM_NEW  WHERE FILEDATE = '"
						+ zdate + "'  AND ADJ_TYPE = '" + adjType + "'  ";
				System.out.println("query is" + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							logger.info("Inside rset");
							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("ACC_TYPE", rs.getString("ACC_TYPE"));
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("CYCLE", rs.getString("CYCLE")); // DISP_DATE
							table_Data.put("DISP_DATE", rs.getString("DISP_DATE"));
							beanList.add(table_Data);
						}
						return beanList;
					}
				});

			} else if (adjType.equals("INT_REFUND")) {
				System.out.println("DATE IS  " + fileDate);

				getData1 = "SELECT ACC_TYPE  AS ACC_TYPE , ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRIM(TO_CHAR(reference_amount,'999999.99')) as TRANSACTION_AMOUNT,"
						+ "REMARKS as TRANSACTION_PARTICULAR,REFERENCE_NUMBER AS REMARKS , DISP_DATE AS DISP_DATE"
						+ ", FILEDATE AS FILEDATE,CYCLE AS CYCLE " + " FROM   RUPAY_ADJ_TTUM_NEW  WHERE FILEDATE = '"
						+ zdate + "'  AND ADJ_TYPE = '" + adjType + "'  ";

				System.out.println("query is" + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("ACC_TYPE", rs.getString("ACC_TYPE"));
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("CYCLE", rs.getString("CYCLE")); // DISP_DATE
							table_Data.put("DISP_DATE", rs.getString("DISP_DATE"));
							beanList.add(table_Data);
						}
						return beanList;
					}
				});
			} else if (adjType.equals("CHBK")) {

				System.out.println("DATE IS  " + fileDate);

				getData1 = "SELECT ACC_TYPE  AS ACC_TYPE , ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRIM(TO_CHAR(reference_amount,'999999.99')) as TRANSACTION_AMOUNT,DISP_DATE AS DISP_DATE ,"
						+ "REMARKS as TRANSACTION_PARTICULAR,REFERENCE_NUMBER AS REMARKS , DISP_DATE AS DISP_DATE"
						+ ", FILEDATE AS FILEDATE,CYCLE AS CYCLE , UNQ_ID AS UNQ_ID  , TXN_SETTLEMENT_DATE as TXN_SETTLEMENT_DATE "
						+ " FROM   RUPAY_ADJ_TTUM_NEW  WHERE FILEDATE = '" + zdate + "'  AND ADJ_TYPE = '" + adjType
						+ "'  ";

				System.out.println("query is" + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("ACC_TYPE", rs.getString("ACC_TYPE"));
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("CYCLE", rs.getString("CYCLE")); // DISP_DATE
							table_Data.put("DISP_DATE", rs.getString("DISP_DATE")); // UNQ_ID
							table_Data.put("UNQ_ID", rs.getString("UNQ_ID")); // TXN_SETTLEMENT_DATE
							table_Data.put("TXN_SETTLEMENT_DATE", rs.getString("TXN_SETTLEMENT_DATE"));
							beanList.add(table_Data);
						}
						return beanList;
					}
				});
			} else if (adjType.equals("CHBKACC")) {

				System.out.println("DATE IS  " + fileDate);

				getData1 = "SELECT ACC_TYPE  AS ACC_TYPE , ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRIM(TO_CHAR(reference_amount,'999999.99')) as TRANSACTION_AMOUNT,DISP_DATE AS DISP_DATE ,"
						+ "REMARKS as TRANSACTION_PARTICULAR,REFERENCE_NUMBER AS REMARKS , DISP_DATE AS DISP_DATE"
						+ ", FILEDATE AS FILEDATE,CYCLE AS CYCLE , UNQ_ID AS UNQ_ID  , TXN_SETTLEMENT_DATE as TXN_SETTLEMENT_DATE "
						+ " FROM   RUPAY_ADJ_TTUM_NEW  WHERE  To_date(filedate,'DD-MM-YY') = TO_date('" + zdate
						+ "','DD-MM-YY') " + "  AND ADJ_TYPE = '" + adjType + "'  ";

				System.out.println("query is" + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("ACC_TYPE", rs.getString("ACC_TYPE"));
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("CYCLE", rs.getString("CYCLE")); // DISP_DATE
							table_Data.put("DISP_DATE", rs.getString("DISP_DATE")); // UNQ_ID
							table_Data.put("UNQ_ID", rs.getString("UNQ_ID")); // TXN_SETTLEMENT_DATE
							table_Data.put("TXN_SETTLEMENT_DATE", rs.getString("TXN_SETTLEMENT_DATE"));
							beanList.add(table_Data);
						}
						return beanList;
					}
				});
			} else if (adjType.equals("VOID")) {

				System.out.println("DATE IS  " + fileDate);

				getData1 = "SELECT ACC_TYPE  AS ACC_TYPE , ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRIM(TO_CHAR(reference_amount,'999999.99')) as TRANSACTION_AMOUNT,DISP_DATE AS DISP_DATE ,"
						+ "REMARKS as TRANSACTION_PARTICULAR,REFERENCE_NUMBER AS REMARKS , DISP_DATE AS DISP_DATE"
						+ ", FILEDATE AS FILEDATE,CYCLE AS CYCLE , UNQ_ID AS UNQ_ID  , TXN_SETTLEMENT_DATE as TXN_SETTLEMENT_DATE "
						+ " FROM   RUPAY_ADJ_TTUM_NEW  WHERE FILEDATE = '" + zdate + "'  AND ADJ_TYPE = '" + adjType
						+ "'  ";

				System.out.println("query is" + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("ACC_TYPE", rs.getString("ACC_TYPE"));
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("CYCLE", rs.getString("CYCLE")); // DISP_DATE
							table_Data.put("DISP_DATE", rs.getString("DISP_DATE")); // UNQ_ID
							table_Data.put("UNQ_ID", rs.getString("UNQ_ID")); // TXN_SETTLEMENT_DATE
							table_Data.put("TXN_SETTLEMENT_DATE", rs.getString("TXN_SETTLEMENT_DATE"));
							beanList.add(table_Data);
						}
						return beanList;
					}
				});
			} else {
				System.out.println("DATE IS  " + fileDate);

				getData1 = "SELECT ACC_TYPE  AS ACC_TYPE , ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRIM(TO_CHAR(reference_amount,'999999.99')) as TRANSACTION_AMOUNT,"
						+ "REMARKS as TRANSACTION_PARTICULAR,REFERENCE_NUMBER AS REMARKS , DISP_DATE AS DISP_DATE"
						+ ", FILEDATE AS FILEDATE,CYCLE AS CYCLE , UNQ_ID AS UNQ_ID "
						+ " FROM   RUPAY_ADJ_TTUM_NEW  WHERE FILEDATE = '" + zdate + "'  AND ADJ_TYPE = '" + adjType
						+ "'  ";

				System.out.println("query is" + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("ACC_TYPE", rs.getString("ACC_TYPE"));
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("CYCLE", rs.getString("CYCLE")); // DISP_DATE
							table_Data.put("DISP_DATE", rs.getString("DISP_DATE")); // UNQ_ID
							table_Data.put("UNQ_ID", rs.getString("UNQ_ID"));
							beanList.add(table_Data);
						}
						return beanList;
					}
				});
			}
			data.add(DailyData);
			return data;

		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	@Override
	public HashMap<String, Object> validateAdjustmentTTUMProcessQSPARC(String passdate, String adjType,
			String subcate) {
		// TODO Auto-generated method stub
		return null;
	}

	// @Override
	// public HashMap<String, Object> validateAdjustmentTTUMProcess(String
	// passdate, String adjType, String subcate) {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
