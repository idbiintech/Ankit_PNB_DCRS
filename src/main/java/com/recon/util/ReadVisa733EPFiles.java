package com.recon.util;


import com.recon.model.VisaUploadBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.web.multipart.MultipartFile;

public class ReadVisa733EPFiles {
  static String description = "";
  
  static String description1 = "";
  
  static int vss120_record_no = 0;
  
  public void fileupload(VisaUploadBean beanObj, MultipartFile file, Connection conn) throws SQLException {
    String line = null;
    boolean vss_110 = false, vss_120 = false, vss_130 = false, vss_140 = false, vss_210 = false;
    boolean international = false, domestic = false;
    boolean vss_110_dom_starts = false, vss_120_dom_starts = false, vss_110_int_starts = false;
    boolean vss_130_dom_starts = false, issuer = false, acquirer = false;
    boolean vss_120_int_starts = false, vss_130_int_starts = false, vss_140_int_starts = false;
    boolean vss_210_int_starts = false;
    int sr_no = 0;
    try {
      System.out.println("reading file EP_733F");
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      String Insert733F = "INSERT INTO  visa_ep733f_rawdata (SYSTEM_DATE,BANK_NAME,DESTINATION_IDENTIFIER,SOURCE_IDENTIFIER,RECORD_IDENTIFIER,DISPUTE_STATUS,ORIGINAL_TRAN_CODE,ORIGINAL_TRAN_CODE_QUAL,ORIGINATOR_RECIPIENT_ID,ACCT_NUMBER,ACQ_REF_NO,PURCHASE_CODE,SOURCE_AMOUNT,SOURCE_CURR_CODE,MERCH_NAME,MERCH_CITY,PROC_DATE,MERCH_COUNTRY_CODE,MERCH_CATEGORY_CODE,MERCH_ZIP_CODE,MERCH_STATUS,REQUESTED_PAYMENT_SERVICE,AUTHORIZATION_CODE,POS_ENTRY_MODE,CENTRAL_PROC_DATE,CARD_ACCEPTOR_ID,REIMBURSEMENT_ATTRIBUTE,NETWORK_IDEN_CODE,DISPUTE_CONDITION,VROL_FINANCIAL_ID,VROL_CASE_NUMBER,VROL_BUNDLE_CASE_NO,CLIENT_CASE_NO,MULT_CLEAR_SEQ_NO,MULT_CLEAR_SEQ_CODE,PRODUCT_ID,SPEND_QUAL_INDICATOR,DISPUTE_FINANCIAL_REASON,SETTLEMENT_FLAG,USAGE_CODE,TXN_IDENTIFIER,ACQ_BUSINESS_ID,ORG_TXN_AMOUNT,ORG_TXN_CUNTRY_CODE,SPECIAL_CHARGEBACK_INDICIA,DISPUTE_ADV_AMOUNT,DISPUTE_ADV_CURRENCY,SOURCE_SATTL_AMT_SIGN,RATE_TABLE_ID,PAGE_NO,FILEDATE,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,STR_to_date(?,'%Y/%m/%d') ,?)";
      String FILEDATE = "", FILENAME = "", PAGE_NO = "", SYSTEM_DATE = "", BANK_NAME = "";
      String DESTINATION_IDENTIFIER = "", SOURCE_IDENTIFIER = "", RECORD_IDENTIFIER = "", DISPUTE_STATUS = "";
      String ORIGINAL_TRAN_CODE = "", ORIGINAL_TRAN_CODE_QUAL = "", ORIGINATOR_RECIPIENT_ID = "";
      String ACCT_NUMBER = "", ACQ_REF_NO = "", PURCHASE_CODE = "", SOURCE_AMOUNT = "", SOURCE_CURR_CODE = "";
      String MERCH_NAME = "", MERCH_CITY = "", PROC_DATE = "", MERCH_COUNTRY_CODE = "", MERCH_CATEGORY_CODE = "";
      String MERCH_ZIP_CODE = "", MERCH_STATUS = "", REQUESTED_PAYMENT_SERVICE = "", AUTHORIZATION_CODE = "";
      String POS_ENTRY_MODE = "", CENTRAL_PROC_DATE = "", CARD_ACCEPTOR_ID = "", REIMBURSEMENT_ATTRIBUTE = "";
      String NETWORK_IDEN_CODE = "", DISPUTE_CONDITION = "", VROL_FINANCIAL_ID = "", VROL_CASE_NUMBER = "";
      String VROL_BUNDLE_CASE_NO = "", CLIENT_CASE_NO = "", MULT_CLEAR_SEQ_NO = "", MULT_CLEAR_SEQ_CODE = "";
      String PRODUCT_ID = "", SPEND_QUAL_INDICATOR = "", DISPUTE_FINANCIAL_REASON = "", SETTLEMENT_FLAG = "";
      String USAGE_CODE = "", TXN_IDENTIFIER = "", ACQ_BUSINESS_ID = "", ORG_TXN_AMOUNT = "";
      String ORG_TXN_CUNTRY_CODE = "", SPECIAL_CHARGEBACK_INDICIA = "", DISPUTE_ADV_AMOUNT = "";
      String DISPUTE_ADV_CURRENCY = "", SOURCE_SATTL_AMT_SIGN = "", RATE_TABLE_ID = "";
      int count = 0;
      PreparedStatement ps733F = conn.prepareStatement(Insert733F);
      while ((line = br.readLine()) != null) {
        if (!line.trim().equalsIgnoreCase("")) {
          if (line.trim().contains("SYSTEM DATE")) {
            SYSTEM_DATE = line.trim().substring(12, 22);
            continue;
          } 
          if (line.trim().contains("PAGE")) {
            PAGE_NO = line.substring(127, line.length()).trim().replaceAll("\\s", "");
            System.out.println("1 " + PAGE_NO);
            continue;
          } 
          if (line.trim().contains("UNION BANK")) {
            BANK_NAME = line.trim().substring(0, 21);
            continue;
          } 
          if (line.trim().contains("Destination Identifier")) {
            DESTINATION_IDENTIFIER = line.trim().substring(26, 32);
            MERCH_NAME = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Source Identifier")) {
            SOURCE_IDENTIFIER = line.trim().substring(26, 34);
            MERCH_CITY = line.substring(93, line.length()).trim();
            System.out.println("SOURCE_IDENTIFIER " + SOURCE_IDENTIFIER);
            continue;
          } 
          if (line.trim().contains("Record Identifier")) {
            RECORD_IDENTIFIER = line.trim().substring(26, 29);
            MERCH_COUNTRY_CODE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Dispute Status")) {
            DISPUTE_STATUS = line.trim().substring(26, 28);
            MERCH_CATEGORY_CODE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Original Transaction Code")) {
            ORIGINAL_TRAN_CODE = line.trim().substring(26, 28);
            MERCH_STATUS = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Original Trans Code Qual")) {
            ORIGINAL_TRAN_CODE_QUAL = line.trim().substring(26, 27);
            MERCH_ZIP_CODE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Originator Recipient indi")) {
            ORIGINATOR_RECIPIENT_ID = line.trim().substring(26, 27);
            REQUESTED_PAYMENT_SERVICE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Acct Number")) {
            ACCT_NUMBER = line.trim().substring(26, 45);
            AUTHORIZATION_CODE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Acquirer Reference Nbr")) {
            ACQ_REF_NO = line.trim().substring(26, 49);
            POS_ENTRY_MODE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Purchase Date")) {
            PROC_DATE = line.trim().substring(26, 34);
            CENTRAL_PROC_DATE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Source Amount")) {
            SOURCE_AMOUNT = line.trim().substring(26, 38);
            CARD_ACCEPTOR_ID = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Source Currency Code")) {
            SOURCE_CURR_CODE = line.trim().substring(26, 29);
            REIMBURSEMENT_ATTRIBUTE = line.substring(93, line.length()).trim();
            System.out.println("SOURCE_CURR_CODE" + SOURCE_CURR_CODE + " " + REIMBURSEMENT_ATTRIBUTE);
            continue;
          } 
          if (line.trim().contains("Network Identification Cd")) {
            NETWORK_IDEN_CODE = line.trim().substring(26, 30);
            SETTLEMENT_FLAG = line.substring(93, line.length()).trim();
            System.out.println("NETWORK_IDEN_CODE" + NETWORK_IDEN_CODE + " " + SETTLEMENT_FLAG);
            continue;
          } 
          if (line.trim().contains("Dispute Condition")) {
            DISPUTE_CONDITION = line.trim().substring(26, 27);
            USAGE_CODE = line.substring(93, line.length()).trim();
            System.out.println("DISPUTE_CONDITION" + DISPUTE_CONDITION + " " + USAGE_CODE);
            continue;
          } 
          if (line.trim().contains("VROL Financial ID")) {
            VROL_FINANCIAL_ID = line.trim().substring(26, 35);
            TXN_IDENTIFIER = line.substring(93, line.length()).trim();
            System.out.println("VROL_FINANCIAL_ID" + VROL_FINANCIAL_ID + " " + TXN_IDENTIFIER);
            continue;
          } 
          if (line.trim().contains("VROL Case Number")) {
            VROL_CASE_NUMBER = line.trim().substring(26, 36);
            ACQ_BUSINESS_ID = line.substring(93, line.length()).trim();
            System.out.println("VROL_CASE_NUMBER" + VROL_CASE_NUMBER + " " + ACQ_BUSINESS_ID);
            continue;
          } 
          if (line.trim().contains("VROL Bundle Case No")) {
            VROL_BUNDLE_CASE_NO = line.trim().substring(26, 36);
            ORG_TXN_AMOUNT = line.substring(93, line.length()).trim();
            System.out.println("VROL_BUNDLE_CASE_NO" + VROL_BUNDLE_CASE_NO + " " + ORG_TXN_AMOUNT);
            continue;
          } 
          if (line.trim().contains("Client Case Number")) {
            CLIENT_CASE_NO = "";
            ORG_TXN_CUNTRY_CODE = line.substring(93, line.length()).trim();
            System.out.println("SOURCE_CURR_CODE" + ORG_TXN_CUNTRY_CODE + " " + CLIENT_CASE_NO);
            continue;
          } 
          if (line.trim().contains("Multiple Clearing Seq Nbr")) {
            MULT_CLEAR_SEQ_NO = line.trim().substring(26, 28);
            SPECIAL_CHARGEBACK_INDICIA = line.substring(93, line.length()).trim();
            System.out.println("Multiple Clearing Seq Nbr" + SOURCE_CURR_CODE + " " + MULT_CLEAR_SEQ_NO);
            continue;
          } 
          if (line.trim().contains("Multiple Clearing Seq Cnt")) {
            MULT_CLEAR_SEQ_CODE = line.trim().substring(26, 29);
            DISPUTE_ADV_AMOUNT = line.substring(93, line.length()).trim();
            System.out
              .println("Multiple Clearing Seq Cnt" + DISPUTE_ADV_AMOUNT + " " + MULT_CLEAR_SEQ_CODE);
            continue;
          } 
          if (line.trim().contains("Product Id")) {
            PRODUCT_ID = line.trim().substring(26, 27);
            DISPUTE_ADV_CURRENCY = line.substring(93, line.length()).trim();
            System.out.println("DISPUTE_ADV_CURRENCY" + DISPUTE_ADV_CURRENCY + " " + PRODUCT_ID);
            continue;
          } 
          if (line.trim().contains("Spend Qualified Indicator")) {
            SPEND_QUAL_INDICATOR = "";
            SOURCE_SATTL_AMT_SIGN = line.substring(93, line.length()).trim();
            System.out.println(
                "Spend Qualified Indicator" + SOURCE_SATTL_AMT_SIGN + " " + REIMBURSEMENT_ATTRIBUTE);
            continue;
          } 
          if (line.trim().contains("Dispute Financial Reason")) {
            DISPUTE_FINANCIAL_REASON = line.trim().substring(26, 28);
            RATE_TABLE_ID = line.substring(93, line.length()).trim();
            System.out.println("Dispute Financial Reason" + DISPUTE_FINANCIAL_REASON + " " + RATE_TABLE_ID);
            ps733F.setString(1, SYSTEM_DATE);
            ps733F.setString(2, BANK_NAME);
            ps733F.setString(3, DESTINATION_IDENTIFIER);
            ps733F.setString(4, SOURCE_IDENTIFIER);
            ps733F.setString(5, RECORD_IDENTIFIER);
            ps733F.setString(6, DISPUTE_STATUS);
            ps733F.setString(7, ORIGINAL_TRAN_CODE);
            ps733F.setString(8, ORIGINAL_TRAN_CODE_QUAL);
            ps733F.setString(9, ORIGINATOR_RECIPIENT_ID);
            ps733F.setString(10, ACCT_NUMBER);
            ps733F.setString(11, ACQ_REF_NO);
            ps733F.setString(12, PURCHASE_CODE);
            ps733F.setString(13, SOURCE_AMOUNT);
            ps733F.setString(14, SOURCE_CURR_CODE);
            ps733F.setString(15, MERCH_NAME);
            ps733F.setString(16, MERCH_CITY);
            ps733F.setString(17, PROC_DATE);
            ps733F.setString(18, MERCH_COUNTRY_CODE);
            ps733F.setString(19, MERCH_CATEGORY_CODE);
            ps733F.setString(20, MERCH_ZIP_CODE);
            ps733F.setString(21, MERCH_STATUS);
            ps733F.setString(22, REQUESTED_PAYMENT_SERVICE);
            ps733F.setString(23, AUTHORIZATION_CODE);
            ps733F.setString(24, POS_ENTRY_MODE);
            ps733F.setString(25, CENTRAL_PROC_DATE);
            ps733F.setString(26, CARD_ACCEPTOR_ID);
            ps733F.setString(27, REIMBURSEMENT_ATTRIBUTE);
            ps733F.setString(28, NETWORK_IDEN_CODE);
            ps733F.setString(29, DISPUTE_CONDITION);
            ps733F.setString(30, VROL_FINANCIAL_ID);
            ps733F.setString(31, VROL_CASE_NUMBER);
            ps733F.setString(32, VROL_BUNDLE_CASE_NO);
            ps733F.setString(33, CLIENT_CASE_NO);
            ps733F.setString(34, MULT_CLEAR_SEQ_NO);
            ps733F.setString(35, MULT_CLEAR_SEQ_CODE);
            ps733F.setString(36, PRODUCT_ID);
            ps733F.setString(37, SPEND_QUAL_INDICATOR);
            ps733F.setString(38, DISPUTE_FINANCIAL_REASON);
            ps733F.setString(39, SETTLEMENT_FLAG);
            ps733F.setString(40, USAGE_CODE);
            ps733F.setString(41, TXN_IDENTIFIER);
            ps733F.setString(42, ACQ_BUSINESS_ID);
            ps733F.setString(43, ORG_TXN_AMOUNT);
            ps733F.setString(44, ORG_TXN_CUNTRY_CODE);
            ps733F.setString(45, SPECIAL_CHARGEBACK_INDICIA);
            ps733F.setString(46, DISPUTE_ADV_AMOUNT);
            ps733F.setString(47, DISPUTE_ADV_CURRENCY);
            ps733F.setString(48, SOURCE_SATTL_AMT_SIGN);
            ps733F.setString(49, RATE_TABLE_ID);
            ps733F.setString(50, PAGE_NO);
            ps733F.setString(51, beanObj.getFileDate());
            ps733F.setString(52, file.getOriginalFilename());
            ps733F.execute();
            count++;
            SYSTEM_DATE = "";
            BANK_NAME = "";
            DESTINATION_IDENTIFIER = "";
            SOURCE_IDENTIFIER = "";
            RECORD_IDENTIFIER = "";
            DISPUTE_STATUS = "";
            ORIGINAL_TRAN_CODE = "";
            ORIGINAL_TRAN_CODE_QUAL = "";
            ORIGINATOR_RECIPIENT_ID = "";
            ACCT_NUMBER = "";
            ACQ_REF_NO = "";
            PURCHASE_CODE = "";
            SOURCE_AMOUNT = "";
            SOURCE_CURR_CODE = "";
            MERCH_NAME = "";
            MERCH_CITY = "";
            PROC_DATE = "";
            MERCH_COUNTRY_CODE = "";
            MERCH_CATEGORY_CODE = "";
            MERCH_ZIP_CODE = "";
            MERCH_STATUS = "";
            REQUESTED_PAYMENT_SERVICE = "";
            AUTHORIZATION_CODE = "";
            POS_ENTRY_MODE = "";
            CENTRAL_PROC_DATE = "";
            CARD_ACCEPTOR_ID = "";
            REIMBURSEMENT_ATTRIBUTE = "";
            NETWORK_IDEN_CODE = "";
            DISPUTE_CONDITION = "";
            VROL_FINANCIAL_ID = "";
            VROL_CASE_NUMBER = "";
            VROL_BUNDLE_CASE_NO = "";
            CLIENT_CASE_NO = "";
            MULT_CLEAR_SEQ_NO = "";
            MULT_CLEAR_SEQ_CODE = "";
            PRODUCT_ID = "";
            SPEND_QUAL_INDICATOR = "";
            DISPUTE_FINANCIAL_REASON = "";
            SETTLEMENT_FLAG = "";
            USAGE_CODE = "";
            TXN_IDENTIFIER = "";
            ACQ_BUSINESS_ID = "";
            ORG_TXN_AMOUNT = "";
            ORG_TXN_CUNTRY_CODE = "";
            SPECIAL_CHARGEBACK_INDICIA = "";
            DISPUTE_ADV_AMOUNT = "";
            DISPUTE_ADV_CURRENCY = "";
            SOURCE_SATTL_AMT_SIGN = "";
            RATE_TABLE_ID = "";
            PAGE_NO = "";
            FILEDATE = "";
            FILENAME = "";
          } 
        } 
      } 
      String Insert733FDELETE = "DELETE FROM visa_ep733f_rawdata where ";
      PreparedStatement ps733FDELETE = conn.prepareStatement(Insert733F);
      System.out.println("SuccessFull Executed " + count);
    } catch (Exception e) {
      System.out.println("Exception in reading visa file " + e);
    } 
  }
  
  public static void readVSS110(String line, int sr_no, PreparedStatement ps) throws SQLException {
    if (line.substring(53, 132).trim().equalsIgnoreCase("")) {
      description = line.trim();
    } else {
      System.out.println(" ********** " + description + " ************** ");
      System.out.println("Sub Description : " + line.substring(0, 37).trim());
      System.out.println("count : " + line.substring(38, 53).trim());
      System.out.println("credit amount : " + line.substring(53, 79).trim());
      System.out.println("Debit amount : " + line.substring(79, 104).trim());
      System.out.println("Total amount : " + line.substring(105, 129).trim());
      System.out.println("Sign : " + line.substring(129, 132).trim());
      System.out.println("sr no is " + sr_no);
      ps.setString(sr_no++, description);
      ps.setString(sr_no++, line.substring(0, 37).trim());
      ps.setString(sr_no++, line.substring(38, 53).trim());
      ps.setString(sr_no++, line.substring(53, 79).trim());
      ps.setString(sr_no++, line.substring(79, 104).trim());
      ps.setString(sr_no++, line.substring(105, 129).trim());
      System.out.println("sr no is " + sr_no);
      ps.setString(sr_no++, line.substring(129, 132).trim());
      System.out.println("sr no is " + sr_no);
      ps.execute();
      ps.close();
    } 
  }
  
  public static void readVSS120(String line, int sr_no, PreparedStatement ps120) throws SQLException {
    if (line.substring(53, 132).trim().equalsIgnoreCase("")) {
      description = line.trim();
    } else {
      System.out.println(" ********** " + description + " ************** ");
      vss120_record_no++;
      ps120.setString(sr_no++, description);
      if (line.substring(0, 37).trim().equalsIgnoreCase("ORIGINAL WITHDRAWAL")) {
        System.out.println("sub description is : " + line.substring(0, 37).trim());
        System.out.println("count is : " + line.substring(53, 69).trim());
        System.out.println("clearing amount : " + line.substring(69, 88).trim());
        System.out.println("clearing amount sign : " + line.substring(88, 91).trim());
        System.out.println("Interchange Value CR : " + line.substring(93, 113).trim());
        System.out.println("sr_no is " + sr_no);
        ps120.setString(sr_no++, line.substring(0, 37).trim());
        ps120.setString(sr_no++, line.substring(53, 69).trim());
        ps120.setString(sr_no++, line.substring(69, 88).trim());
        ps120.setString(sr_no++, line.substring(88, 91).trim());
        ps120.setString(sr_no++, line.substring(93, 113).trim());
        ps120.setString(sr_no++, "0");
        ps120.setInt(sr_no++, vss120_record_no);
        System.out.println("sr_no is " + sr_no);
        ps120.execute();
        ps120.close();
      } else {
        System.out.println("sub description is : " + line.substring(0, 37).trim());
        System.out.println("count is : " + line.substring(53, 69).trim());
        System.out.println("clearing amount : " + line.substring(69, 88).trim());
        System.out.println("clearing amount sign : " + line.substring(88, 91).trim());
        System.out.println("Interchange Value CR : " + line.substring(93, 113).trim());
        System.out.println("Interchange Value DR : " + line.substring(114, line.length()).trim());
        System.out.println("sr_no is " + sr_no);
        ps120.setString(sr_no++, line.substring(0, 37).trim());
        ps120.setString(sr_no++, line.substring(53, 69).trim());
        ps120.setString(sr_no++, line.substring(69, 88).trim());
        ps120.setString(sr_no++, line.substring(88, 91).trim());
        ps120.setString(sr_no++, line.substring(93, 113).trim());
        ps120.setString(sr_no++, line.substring(114, line.length()).trim());
        ps120.setInt(sr_no++, vss120_record_no);
        System.out.println("sr_no is " + sr_no);
        ps120.execute();
        ps120.close();
      } 
    } 
  }
  
  public static void readInternationalVSS120(String line, int sr_no, PreparedStatement ps120) throws SQLException {
    if (line.substring(53, 132).trim().equalsIgnoreCase("")) {
      description = line.trim();
    } else {
      System.out.println(" ********** " + description + " ************** ");
      vss120_record_no++;
      ps120.setString(sr_no++, description);
      if (line.substring(0, 37).trim().equalsIgnoreCase("ORIGINAL WITHDRAWAL")) {
        System.out.println("sub description is : " + line.substring(0, 37).trim());
        System.out.println("count is : " + line.substring(53, 69).trim());
        System.out.println("clearing amount : " + line.substring(69, 87).trim());
        System.out.println("clearing amount sign : " + line.substring(87, 91).trim());
        System.out.println("Interchange Value CR : " + line.substring(93, 113).trim());
        System.out.println("sr_no is " + sr_no);
        ps120.setString(sr_no++, line.substring(0, 37).trim());
        ps120.setString(sr_no++, line.substring(53, 69).trim());
        ps120.setString(sr_no++, line.substring(69, 87).trim());
        ps120.setString(sr_no++, line.substring(87, 91).trim());
        ps120.setString(sr_no++, line.substring(93, 113).trim());
        ps120.setString(sr_no++, "0");
        ps120.setInt(sr_no++, vss120_record_no);
        System.out.println("sr_no is " + sr_no);
        ps120.execute();
        ps120.close();
      } else {
        System.out.println("sub description is : " + line.substring(0, 37).trim());
        System.out.println("count is : " + line.substring(53, 69).trim());
        System.out.println("clearing amount : " + line.substring(69, 88).trim());
        System.out.println("clearing amount sign : " + line.substring(88, 91).trim());
        System.out.println("Interchange Value CR : " + line.substring(93, 113).trim());
        System.out.println("Interchange Value DR : " + line.substring(114, line.length()).trim());
        System.out.println("sr_no is " + sr_no);
        ps120.setString(sr_no++, line.substring(0, 37).trim());
        ps120.setString(sr_no++, line.substring(53, 69).trim());
        ps120.setString(sr_no++, line.substring(69, 87).trim());
        ps120.setString(sr_no++, line.substring(87, 91).trim());
        ps120.setString(sr_no++, line.substring(93, 113).trim());
        ps120.setString(sr_no++, line.substring(114, line.length()).trim());
        ps120.setInt(sr_no++, vss120_record_no);
        System.out.println("sr_no is " + sr_no);
        ps120.execute();
        ps120.close();
      } 
    } 
  }
  
  public static void readVSS140(String line, int sr_no, PreparedStatement ps) throws SQLException {
    if (line.substring(53, line.length()).trim().equalsIgnoreCase("")) {
      description = line.trim();
    } else {
      System.out.println(" ********** " + description + " ************** ");
      System.out.println("sub description is : " + line.substring(0, 60).trim());
      System.out.println("count is : " + line.substring(60, 68).trim());
      System.out.println("Interchange amount : " + line.substring(69, 87).trim());
      System.out.println("Interchange amount sign : " + line.substring(87, 91).trim());
      System.out.println("Visa Charges CR : " + line.substring(93, 113).trim());
      System.out.println("Visa Charges DR : " + line.substring(114, line.length()).trim());
      ps.setString(sr_no++, description);
      ps.setString(sr_no++, line.substring(0, 60).trim());
      ps.setString(sr_no++, line.substring(60, 68).trim());
      ps.setString(sr_no++, line.substring(69, 87).trim());
      ps.setString(sr_no++, line.substring(87, 91).trim());
      ps.setString(sr_no++, line.substring(93, 113).trim());
      ps.setString(sr_no++, line.substring(114, line.length()).trim());
      ps.execute();
      ps.close();
    } 
  }
  
  public static void readVSS210(String line, int sr_no, PreparedStatement ps) throws SQLException {
    if (line.substring(53, line.length()).trim().equalsIgnoreCase("") && !line.startsWith("VISA INTERNATIONAL")) {
      description = line.trim();
    } else if (!line.startsWith("VISA INTERNATIONAL")) {
      System.out.println(" ********** " + description + " ************** ");
      System.out.println("sett_curr Interchange amount : " + line.substring(30, 49).trim());
      System.out.println("sett_curr Interchange amount sign : " + line.substring(49, 52).trim());
      System.out.println("sett_curr Conversion Fee : " + line.substring(53, 70).trim());
      System.out.println("clr curr Interchange amount : " + line.substring(71, 91).trim());
      System.out.println("clr curr Interchange amount sign : " + line.substring(91, 94));
      System.out.println("clr curr Conversion Fee : " + line.substring(95, 111).trim());
      System.out.println("clr curr Opt Issuer Fee : " + line.substring(111, 129).trim());
      System.out.println("clr curr Opt Issuer Fee Sign : " + line.substring(129, line.length()).trim());
      System.out.println("sr_no is " + sr_no);
      ps.setString(sr_no++, description);
      ps.setString(sr_no++, line.substring(30, 49).trim());
      ps.setString(sr_no++, line.substring(49, 52).trim());
      ps.setString(sr_no++, line.substring(53, 70).trim());
      ps.setString(sr_no++, line.substring(71, 91).trim());
      ps.setString(sr_no++, line.substring(91, 94).trim());
      ps.setString(sr_no++, line.substring(95, 111).trim());
      ps.setString(sr_no++, line.substring(111, 129).trim());
      System.out.println("sr_no is " + sr_no);
      ps.setString(sr_no++, line.substring(129, line.length()).trim());
      System.out.println("sr_no is " + sr_no);
      ps.execute();
      ps.close();
    } 
  }
}
