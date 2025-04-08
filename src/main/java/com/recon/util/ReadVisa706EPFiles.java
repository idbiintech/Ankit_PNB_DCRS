package com.recon.util;


import com.recon.model.VisaUploadBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.web.multipart.MultipartFile;

public class ReadVisa706EPFiles {
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
      System.out.println("reading file EP_706");
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      String Insert733F = "INSERT INTO  visa_ep706_rawdata (SYSTEM_DATE, BANK_NAME, ACCT_NUMBER, MERCH_STATE, FLOOR_LIMIT_INDICATOR, REQUESTED_PAYMENT_SERVICE, CRB_FILE_IND, NUMBER_OF_PAYMENT_FORM, ACQ_REF_NO, USAGE_CODE, ACQ_BUSINESS_ID, CHARGEBACK_REASON_CODE, PURCHASE_DATE, SETTLEMENT_FLAG, DESTINATION_AMOUNT, AUTH_CHARACTER_IND, DESTINATION_CURR_CODE, AUTHORIZATION_CODE, SOURCE_AMOUNT, POS_TERMINAL_CAPABILITY, SOURCE_CURR_CODE, CARDHOLDER_ID_METHOD, MERCH_NAME, COLLECTION_ONLY_FLAG, MERCH_CITY, POS_ENTRY_MODE, MERCH_COUNTRY_CODE, CENTRAL_PROC_DATE, MERCH_CATEGORY_CODE, REIMBURSEMENT_ATTRIBUTE, MERCH_ZIP_CODE, BUSINESS_FORMAT_CODE, CONVERSION_DATE, TOKEN_ASSURANCE_METHOD, ADDITIONAL_TOKEN_RESPONSE, RATE_TABLE_ID, RESERVED_DATA, RESERVED, ACCEPTANCE_TERMINAL_IND, DOCUMENTATION_IND, PREPAID_CARD_IND, MEMBER_MESSAGE_TEXT, SERVICE_DEVELOPMENT_FIELD, SPECIAL_CONDITION_IND, AVS_RESPONSE_CODE, FEE_PROGRAM_IND, AUTHORIZATION_SOURCE_CODE, ISSUER_CHARGE, PURCHASE_ID_FORMAT, PFX_APPLIED_IND, ACCOUNT_SELECTION, CARD_ACCEPTOR_ID, INSTALLMENT_PAYMENT_COUNT, TERMINAL_ID, PURCHASE_IDENTIFIER, NAT_REIMBURSEMENT_FEE, CASHBACK, MAIL_PHN_ECOMM_IND, CHIP_CONDITION_CODE, SPECIAL_CHARGEBACK_IND, POS_ENVIRONMENT,\t ENABLE_VERFICATION_VAL, VISA_INTERNAL_USE_ONLY, BUSINESS_FORMAT_CODE_SD, SD_ADDl_TXN_FEE_ONE, NETWORK_IDN_CODE, SD_ADDl_TXN_FEE_TWO, CONTACT_FOR_INFO, TOTAL_DISCOUNT_AMOUNT, ADJUSMENT_PROCESSING_IND, SURCHRG_AMT_CARD_CURRENCY, MESSAGE_REASON_CODE, MONEY_TRANSFER_FRN_EX_FEE, SURCHARGE_AMOUNT, SD_PAYMENT_ACCOUNT_REFERE, SURCHARGE_CREDIT_DEBIT_IND, TOKEN_REQUESTOR_ID,\t\t TRANSACTION_IND, ECOMMERCE_GOODS_IND, AUTHORIZED_AMOUNT, MERCHANT_VERIFICATION_VAL, AUTH_CURRENCY_CD, INTERCHANGE_FEE_AMOUNT, AUTHORIZATION_RESPONSE_CD, INTERCHANGE_FEE_SIGN, VALIDATION_CODE, SRC_TO_BASE_CURR_EX_RATE, EXCLUDED_TRANS_ID_RSN, BASE_TO_DEST_CURR_EX_RATE, CRS_PROCESSING_CODE, OPTIONAL_ISSUER_ISA_AMT, CHARGEBACK_RIGHTS_IND, PRODUCT_ID, MULTIPLE_CLEARING_SEQ_NBR, PROGRAM_ID, MULTIPLE_CLEARING_SEQ_CNT, DYNAMIC_CURR_CONV_IND, MARKET_SPECIFIC_AUTH_DATA, ACCT_TYPE_IDENTIFICATION, TOTAL_AUTHORIZED_AMOUNT, SPEND_QUALIFIED_IND, INFORMATION_INDICATOR, PAN_TOKEN, MERCHANT_TELEPHONE_NUMBER, ACCOUNT_FUNDING_SOURCE, ADDITIONAL_DATA_IND, CVV2_RESULT_CODE, MERCHANT_VOLUME_IND, PAGE_NO, FILEDATE, FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      String PAGE_NO = "", SYSTEM_DATE = "", BANK_NAME = "", ACCT_NUMBER = "", MERCH_STATE = "";
      String FLOOR_LIMIT_INDICATOR = "", REQUESTED_PAYMENT_SERVICE = "", CRB_FILE_IND = "";
      String NUMBER_OF_PAYMENT_FORM = "", ACQ_REF_NO = "", USAGE_CODE = "", ACQ_BUSINESS_ID = "";
      String CHARGEBACK_REASON_CODE = "", PURCHASE_DATE = "", SETTLEMENT_FLAG = "", DESTINATION_AMOUNT = "";
      String AUTH_CHARACTER_IND = "", DESTINATION_CURR_CODE = "", AUTHORIZATION_CODE = "", SOURCE_AMOUNT = "";
      String POS_TERMINAL_CAPABILITY = "", SOURCE_CURR_CODE = "", CARDHOLDER_ID_METHOD = "", MERCH_NAME = "";
      String COLLECTION_ONLY_FLAG = "", MERCH_CITY = "", POS_ENTRY_MODE = "", MERCH_COUNTRY_CODE = "";
      String CENTRAL_PROC_DATE = "", MERCH_CATEGORY_CODE = "", REIMBURSEMENT_ATTRIBUTE = "", MERCH_ZIP_CODE = "";
      String BUSINESS_FORMAT_CODE = "", CONVERSION_DATE = "", TOKEN_ASSURANCE_METHOD = "";
      String ADDITIONAL_TOKEN_RESPONSE = "", RATE_TABLE_ID = "", RESERVED_DATA = "", RESERVED = "";
      String ACCEPTANCE_TERMINAL_IND = "", DOCUMENTATION_IND = "", PREPAID_CARD_IND = "";
      String MEMBER_MESSAGE_TEXT = "", SERVICE_DEVELOPMENT_FIELD = "", SPECIAL_CONDITION_IND = "";
      String AVS_RESPONSE_CODE = "", FEE_PROGRAM_IND = "", AUTHORIZATION_SOURCE_CODE = "", ISSUER_CHARGE = "";
      String PURCHASE_ID_FORMAT = "", PFX_APPLIED_IND = "", ACCOUNT_SELECTION = "", CARD_ACCEPTOR_ID = "";
      String INSTALLMENT_PAYMENT_COUNT = "", TERMINAL_ID = "", PURCHASE_IDENTIFIER = "";
      String NAT_REIMBURSEMENT_FEE = "", CASHBACK = "", MAIL_PHN_ECOMM_IND = "", CHIP_CONDITION_CODE = "";
      String SPECIAL_CHARGEBACK_IND = "", POS_ENVIRONMENT = "";
      String ENABLE_VERFICATION_VAL = "", VISA_INTERNAL_USE_ONLY = "", BUSINESS_FORMAT_CODE_SD = "";
      String SD_ADDl_TXN_FEE_ONE = "", NETWORK_IDN_CODE = "", SD_ADDl_TXN_FEE_TWO = "";
      String CONTACT_FOR_INFO = "", TOTAL_DISCOUNT_AMOUNT = "", ADJUSMENT_PROCESSING_IND = "";
      String SURCHRG_AMT_CARD_CURRENCY = "", MESSAGE_REASON_CODE = "", MONEY_TRANSFER_FRN_EX_FEE = "";
      String SURCHARGE_AMOUNT = "", SD_PAYMENT_ACCOUNT_REFERE = "", SURCHARGE_CREDIT_DEBIT_IND = "";
      String TOKEN_REQUESTOR_ID = "";
      String TRANSACTION_IND = "", ECOMMERCE_GOODS_IND = "", AUTHORIZED_AMOUNT = "";
      String MERCHANT_VERIFICATION_VAL = "", AUTH_CURRENCY_CD = "", INTERCHANGE_FEE_AMOUNT = "";
      String AUTHORIZATION_RESPONSE_CD = "", INTERCHANGE_FEE_SIGN = "", VALIDATION_CODE = "";
      String SRC_TO_BASE_CURR_EX_RATE = "", EXCLUDED_TRANS_ID_RSN = "", BASE_TO_DEST_CURR_EX_RATE = "";
      String CRS_PROCESSING_CODE = "", OPTIONAL_ISSUER_ISA_AMT = "", CHARGEBACK_RIGHTS_IND = "", PRODUCT_ID = "";
      String MULTIPLE_CLEARING_SEQ_NBR = "", PROGRAM_ID = "", MULTIPLE_CLEARING_SEQ_CNT = "";
      String DYNAMIC_CURR_CONV_IND = "", MARKET_SPECIFIC_AUTH_DATA = "", ACCT_TYPE_IDENTIFICATION = "";
      String TOTAL_AUTHORIZED_AMOUNT = "", SPEND_QUALIFIED_IND = "", INFORMATION_INDICATOR = "", PAN_TOKEN = "";
      String MERCHANT_TELEPHONE_NUMBER = "", ACCOUNT_FUNDING_SOURCE = "", ADDITIONAL_DATA_IND = "";
      String CVV2_RESULT_CODE = "", MERCHANT_VOLUME_IND = "";
      int count = 0;
      PreparedStatement ps706 = conn.prepareStatement(Insert733F);
      while ((line = br.readLine()) != null) {
        if (!line.trim().equalsIgnoreCase("")) {
          if (line.trim().contains("SYSTEM DATE")) {
            SYSTEM_DATE = line.trim().substring(12, 22);
            continue;
          } 
          if (line.trim().contains("PAGE")) {
            PAGE_NO = line.substring(127, line.length()).trim().replaceAll("\\s", "");
            continue;
          } 
          if (line.trim().contains("UNION BANK")) {
            BANK_NAME = line.trim().substring(0, 21);
            continue;
          } 
          if (line.trim().contains("Acct Number & Extension")) {
            ACCT_NUMBER = line.trim().substring(26, 45);
            MERCH_STATE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Floor Limit Indicator")) {
            FLOOR_LIMIT_INDICATOR = "";
            REQUESTED_PAYMENT_SERVICE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("CRB/Exception File Ind")) {
            CRB_FILE_IND = "";
            NUMBER_OF_PAYMENT_FORM = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Acquirer Reference Nbr")) {
            ACQ_REF_NO = line.trim().substring(26, 49);
            USAGE_CODE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Acquirer's Business ID")) {
            ACQ_BUSINESS_ID = line.trim().substring(26, 34);
            CHARGEBACK_REASON_CODE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Purchase Date")) {
            PURCHASE_DATE = line.trim().substring(26, 34);
            SETTLEMENT_FLAG = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Destination Amount")) {
            DESTINATION_AMOUNT = line.trim().substring(26, 38);
            AUTH_CHARACTER_IND = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Destination Currency Code")) {
            DESTINATION_CURR_CODE = line.trim().substring(26, 29);
            AUTHORIZATION_CODE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Source Amount")) {
            SOURCE_AMOUNT = line.trim().substring(26, 38);
            POS_TERMINAL_CAPABILITY = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Source Currency Code")) {
            SOURCE_CURR_CODE = line.trim().substring(26, 29);
            CARDHOLDER_ID_METHOD = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Merchant Name")) {
            MERCH_NAME = "";
            COLLECTION_ONLY_FLAG = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Merchant City")) {
            MERCH_CITY = "";
            POS_ENTRY_MODE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Merchant Country Code")) {
            MERCH_COUNTRY_CODE = line.trim().substring(26, 28);
            CENTRAL_PROC_DATE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Merchant Category Code")) {
            MERCH_CATEGORY_CODE = line.trim().substring(26, 30);
            REIMBURSEMENT_ATTRIBUTE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Merchant ZIP Code")) {
            MERCH_ZIP_CODE = line.trim().substring(26, 31);
            continue;
          } 
          if (line.trim().contains("Business Format Code")) {
            BUSINESS_FORMAT_CODE = "";
            CONVERSION_DATE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Token Assurance Method")) {
            TOKEN_ASSURANCE_METHOD = "";
            ADDITIONAL_TOKEN_RESPONSE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Rate Table ID")) {
            RATE_TABLE_ID = "";
            RESERVED_DATA = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Reserved")) {
            RESERVED = line.trim().substring(26, 32);
            ACCEPTANCE_TERMINAL_IND = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Documentation Indicator")) {
            DOCUMENTATION_IND = "";
            PREPAID_CARD_IND = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Member Message Text")) {
            MEMBER_MESSAGE_TEXT = "";
            SERVICE_DEVELOPMENT_FIELD = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Special Condition Ind")) {
            if (line.trim().substring(27, 28) == " ") {
              SPECIAL_CONDITION_IND = "0";
            } else {
              SPECIAL_CONDITION_IND = line.trim().substring(27, 28);
              System.out.println("Special Condition Indd sd  " + SPECIAL_CONDITION_IND);
            } 
            System.out.println("Special Condition Indd sd ss " + SPECIAL_CONDITION_IND);
            AVS_RESPONSE_CODE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Fee Program Indicator")) {
            FEE_PROGRAM_IND = "";
            AUTHORIZATION_SOURCE_CODE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Issuer Charge")) {
            ISSUER_CHARGE = "";
            PURCHASE_ID_FORMAT = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("PFX Applied Indicator")) {
            PFX_APPLIED_IND = "";
            ACCOUNT_SELECTION = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Card Acceptor ID")) {
            CARD_ACCEPTOR_ID = line.trim().substring(26, 41);
            INSTALLMENT_PAYMENT_COUNT = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Terminal ID")) {
            TERMINAL_ID = line.trim().substring(26, 34);
            PURCHASE_IDENTIFIER = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Nat'l Reimbursement Fee")) {
            NAT_REIMBURSEMENT_FEE = line.trim().substring(26, 38);
            CASHBACK = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Mail/Phn/Ecomm")) {
            if (line.trim().substring(26, 27) != null)
              MAIL_PHN_ECOMM_IND = line.trim().substring(26, 27); 
            CHIP_CONDITION_CODE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Special Chargeback Ind")) {
            SPECIAL_CHARGEBACK_IND = "";
            POS_ENVIRONMENT = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Enabler Verification Val")) {
            ENABLE_VERFICATION_VAL = "";
            VISA_INTERNAL_USE_ONLY = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Business Format Code")) {
            BUSINESS_FORMAT_CODE_SD = line.trim().substring(26, 28);
            SD_ADDl_TXN_FEE_ONE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Network Identification Cd")) {
            NETWORK_IDN_CODE = line.trim().substring(26, 30);
            SD_ADDl_TXN_FEE_TWO = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Contact for Information")) {
            CONTACT_FOR_INFO = "";
            TOTAL_DISCOUNT_AMOUNT = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Adjustment Processing Ind")) {
            ADJUSMENT_PROCESSING_IND = "";
            SURCHRG_AMT_CARD_CURRENCY = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Message Reason Code")) {
            MESSAGE_REASON_CODE = "";
            MONEY_TRANSFER_FRN_EX_FEE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Surcharge Amount")) {
            SURCHARGE_AMOUNT = line.trim().substring(26, 34);
            SD_PAYMENT_ACCOUNT_REFERE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Surcharge Credit/Dbt Ind")) {
            SURCHARGE_CREDIT_DEBIT_IND = "";
            TOKEN_REQUESTOR_ID = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Transaction Identifier")) {
            TRANSACTION_IND = line.trim().substring(26, 41);
            ECOMMERCE_GOODS_IND = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Authorized Amount")) {
            AUTHORIZED_AMOUNT = line.trim().substring(26, 38);
            MERCHANT_VERIFICATION_VAL = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Auth Currency Cd")) {
            AUTH_CURRENCY_CD = line.trim().substring(26, 29);
            INTERCHANGE_FEE_AMOUNT = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Authorization Response Cd")) {
            AUTHORIZATION_RESPONSE_CD = line.trim().substring(26, 28);
            INTERCHANGE_FEE_SIGN = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Validation Code")) {
            VALIDATION_CODE = "";
            SRC_TO_BASE_CURR_EX_RATE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Excluded Trans ID Rsn")) {
            EXCLUDED_TRANS_ID_RSN = "";
            BASE_TO_DEST_CURR_EX_RATE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("CRS Processing Code")) {
            CRS_PROCESSING_CODE = "";
            OPTIONAL_ISSUER_ISA_AMT = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Chargeback Rights Ind")) {
            CHARGEBACK_RIGHTS_IND = "";
            PRODUCT_ID = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Multiple Clearing Seq Nbr")) {
            MULTIPLE_CLEARING_SEQ_NBR = line.trim().substring(26, 28);
            PROGRAM_ID = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Multiple Clearing Seq Cnt")) {
            MULTIPLE_CLEARING_SEQ_CNT = line.trim().substring(26, 28);
            DYNAMIC_CURR_CONV_IND = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Market-Specific Auth Data")) {
            MARKET_SPECIFIC_AUTH_DATA = "";
            ACCT_TYPE_IDENTIFICATION = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Total Authorized Amount")) {
            TOTAL_AUTHORIZED_AMOUNT = line.trim().substring(26, 38);
            SPEND_QUALIFIED_IND = line.substring(93, line.length()).trim();
            System.out.println("TOTAL_AUTHORIZED_AMOUNT " + TOTAL_AUTHORIZED_AMOUNT);
            continue;
          } 
          if (line.trim().contains("Information Indicator")) {
            INFORMATION_INDICATOR = line.trim().substring(26, 27);
            PAN_TOKEN = line.substring(93, line.length()).trim();
            System.out.println("INFORMATION_INDICATOR " + INFORMATION_INDICATOR);
            continue;
          } 
          if (line.trim().contains("Merchant Telephone Number")) {
            MERCHANT_TELEPHONE_NUMBER = "";
            ACCOUNT_FUNDING_SOURCE = line.substring(93, line.length()).trim();
            continue;
          } 
          if (line.trim().contains("Additional Data Indicator")) {
            ADDITIONAL_DATA_IND = line.trim().substring(26, 27);
            CVV2_RESULT_CODE = line.substring(93, line.length()).trim();
            System.out.println("ADDITIONAL_DATA_IND " + ADDITIONAL_DATA_IND);
            continue;
          } 
          if (line.trim().contains("Merchant Volume Indicator")) {
            MERCHANT_VOLUME_IND = "";
            System.out.println("MERCHANT_VOLUME_IND " + MERCHANT_VOLUME_IND);
            ps706.setString(1, SYSTEM_DATE);
            ps706.setString(2, BANK_NAME);
            ps706.setString(3, ACCT_NUMBER);
            ps706.setString(4, MERCH_STATE);
            ps706.setString(5, FLOOR_LIMIT_INDICATOR);
            ps706.setString(6, REQUESTED_PAYMENT_SERVICE);
            ps706.setString(7, CRB_FILE_IND);
            ps706.setString(8, NUMBER_OF_PAYMENT_FORM);
            ps706.setString(9, ACQ_REF_NO);
            ps706.setString(10, USAGE_CODE);
            ps706.setString(11, ACQ_BUSINESS_ID);
            ps706.setString(12, CHARGEBACK_REASON_CODE);
            ps706.setString(13, PURCHASE_DATE);
            ps706.setString(14, SETTLEMENT_FLAG);
            ps706.setString(15, DESTINATION_AMOUNT);
            ps706.setString(16, AUTH_CHARACTER_IND);
            ps706.setString(17, DESTINATION_CURR_CODE);
            ps706.setString(18, AUTHORIZATION_CODE);
            ps706.setString(19, SOURCE_AMOUNT);
            ps706.setString(20, POS_TERMINAL_CAPABILITY);
            ps706.setString(21, SOURCE_CURR_CODE);
            ps706.setString(22, CARDHOLDER_ID_METHOD);
            ps706.setString(23, MERCH_NAME);
            ps706.setString(24, COLLECTION_ONLY_FLAG);
            ps706.setString(25, MERCH_CITY);
            ps706.setString(26, POS_ENTRY_MODE);
            ps706.setString(27, MERCH_COUNTRY_CODE);
            ps706.setString(28, CENTRAL_PROC_DATE);
            ps706.setString(29, MERCH_CATEGORY_CODE);
            ps706.setString(30, REIMBURSEMENT_ATTRIBUTE);
            ps706.setString(31, MERCH_ZIP_CODE);
            ps706.setString(32, BUSINESS_FORMAT_CODE);
            ps706.setString(33, CONVERSION_DATE);
            ps706.setString(34, TOKEN_ASSURANCE_METHOD);
            ps706.setString(35, ADDITIONAL_TOKEN_RESPONSE);
            ps706.setString(36, RATE_TABLE_ID);
            ps706.setString(37, RESERVED_DATA);
            ps706.setString(38, RESERVED);
            ps706.setString(39, ACCEPTANCE_TERMINAL_IND);
            ps706.setString(40, DOCUMENTATION_IND);
            ps706.setString(41, PREPAID_CARD_IND);
            ps706.setString(42, MEMBER_MESSAGE_TEXT);
            ps706.setString(43, SERVICE_DEVELOPMENT_FIELD);
            ps706.setString(44, SPECIAL_CONDITION_IND);
            ps706.setString(45, AVS_RESPONSE_CODE);
            ps706.setString(46, FEE_PROGRAM_IND);
            ps706.setString(47, AUTHORIZATION_SOURCE_CODE);
            ps706.setString(48, ISSUER_CHARGE);
            ps706.setString(49, PURCHASE_ID_FORMAT);
            ps706.setString(50, PFX_APPLIED_IND);
            ps706.setString(51, ACCOUNT_SELECTION);
            ps706.setString(52, CARD_ACCEPTOR_ID);
            ps706.setString(53, INSTALLMENT_PAYMENT_COUNT);
            ps706.setString(54, TERMINAL_ID);
            ps706.setString(55, PURCHASE_IDENTIFIER);
            ps706.setString(56, NAT_REIMBURSEMENT_FEE);
            ps706.setString(57, CASHBACK);
            ps706.setString(58, MAIL_PHN_ECOMM_IND);
            ps706.setString(59, CHIP_CONDITION_CODE);
            ps706.setString(60, SPECIAL_CHARGEBACK_IND);
            ps706.setString(61, POS_ENVIRONMENT);
            ps706.setString(62, ENABLE_VERFICATION_VAL);
            ps706.setString(63, VISA_INTERNAL_USE_ONLY);
            ps706.setString(64, BUSINESS_FORMAT_CODE_SD);
            ps706.setString(65, SD_ADDl_TXN_FEE_ONE);
            ps706.setString(66, NETWORK_IDN_CODE);
            ps706.setString(67, SD_ADDl_TXN_FEE_TWO);
            ps706.setString(68, CONTACT_FOR_INFO);
            ps706.setString(69, TOTAL_DISCOUNT_AMOUNT);
            ps706.setString(70, ADJUSMENT_PROCESSING_IND);
            ps706.setString(71, SURCHRG_AMT_CARD_CURRENCY);
            ps706.setString(72, MESSAGE_REASON_CODE);
            ps706.setString(73, MONEY_TRANSFER_FRN_EX_FEE);
            ps706.setString(74, SURCHARGE_AMOUNT);
            ps706.setString(75, SD_PAYMENT_ACCOUNT_REFERE);
            ps706.setString(76, SURCHARGE_CREDIT_DEBIT_IND);
            ps706.setString(77, TOKEN_REQUESTOR_ID);
            ps706.setString(78, TRANSACTION_IND);
            ps706.setString(79, ECOMMERCE_GOODS_IND);
            ps706.setString(80, AUTHORIZED_AMOUNT);
            ps706.setString(81, MERCHANT_VERIFICATION_VAL);
            ps706.setString(82, AUTH_CURRENCY_CD);
            ps706.setString(83, INTERCHANGE_FEE_AMOUNT);
            ps706.setString(84, AUTHORIZATION_RESPONSE_CD);
            ps706.setString(85, INTERCHANGE_FEE_SIGN);
            ps706.setString(86, VALIDATION_CODE);
            ps706.setString(87, SRC_TO_BASE_CURR_EX_RATE);
            ps706.setString(88, EXCLUDED_TRANS_ID_RSN);
            ps706.setString(89, BASE_TO_DEST_CURR_EX_RATE);
            ps706.setString(90, CRS_PROCESSING_CODE);
            ps706.setString(91, OPTIONAL_ISSUER_ISA_AMT);
            ps706.setString(92, CHARGEBACK_RIGHTS_IND);
            ps706.setString(93, PRODUCT_ID);
            ps706.setString(94, MULTIPLE_CLEARING_SEQ_NBR);
            ps706.setString(95, PROGRAM_ID);
            ps706.setString(96, MULTIPLE_CLEARING_SEQ_CNT);
            ps706.setString(97, DYNAMIC_CURR_CONV_IND);
            ps706.setString(98, MARKET_SPECIFIC_AUTH_DATA);
            ps706.setString(99, ACCT_TYPE_IDENTIFICATION);
            ps706.setString(100, TOTAL_AUTHORIZED_AMOUNT);
            ps706.setString(101, SPEND_QUALIFIED_IND);
            ps706.setString(102, INFORMATION_INDICATOR);
            ps706.setString(103, PAN_TOKEN);
            ps706.setString(104, MERCHANT_TELEPHONE_NUMBER);
            ps706.setString(105, ACCOUNT_FUNDING_SOURCE);
            ps706.setString(106, ADDITIONAL_DATA_IND);
            ps706.setString(107, CVV2_RESULT_CODE);
            ps706.setString(108, MERCHANT_VOLUME_IND);
            ps706.setString(109, PAGE_NO);
            ps706.setString(110, beanObj.getFileDate());
            ps706.setString(111, file.getOriginalFilename());
            ps706.execute();
            count++;
            PAGE_NO = "";
            SYSTEM_DATE = "";
            BANK_NAME = "";
            ACCT_NUMBER = "";
            MERCH_STATE = "";
            FLOOR_LIMIT_INDICATOR = "";
            REQUESTED_PAYMENT_SERVICE = "";
            CRB_FILE_IND = "";
            NUMBER_OF_PAYMENT_FORM = "";
            ACQ_REF_NO = "";
            USAGE_CODE = "";
            ACQ_BUSINESS_ID = "";
            CHARGEBACK_REASON_CODE = "";
            PURCHASE_DATE = "";
            SETTLEMENT_FLAG = "";
            DESTINATION_AMOUNT = "";
            AUTH_CHARACTER_IND = "";
            DESTINATION_CURR_CODE = "";
            AUTHORIZATION_CODE = "";
            SOURCE_AMOUNT = "";
            POS_TERMINAL_CAPABILITY = "";
            SOURCE_CURR_CODE = "";
            CARDHOLDER_ID_METHOD = "";
            MERCH_NAME = "";
            COLLECTION_ONLY_FLAG = "";
            MERCH_CITY = "";
            POS_ENTRY_MODE = "";
            MERCH_COUNTRY_CODE = "";
            CENTRAL_PROC_DATE = "";
            MERCH_CATEGORY_CODE = "";
            REIMBURSEMENT_ATTRIBUTE = "";
            MERCH_ZIP_CODE = "";
            BUSINESS_FORMAT_CODE = "";
            CONVERSION_DATE = "";
            TOKEN_ASSURANCE_METHOD = "";
            ADDITIONAL_TOKEN_RESPONSE = "";
            RATE_TABLE_ID = "";
            RESERVED_DATA = "";
            RESERVED = "";
            ACCEPTANCE_TERMINAL_IND = "";
            DOCUMENTATION_IND = "";
            PREPAID_CARD_IND = "";
            MEMBER_MESSAGE_TEXT = "";
            SERVICE_DEVELOPMENT_FIELD = "";
            SPECIAL_CONDITION_IND = "";
            AVS_RESPONSE_CODE = "";
            FEE_PROGRAM_IND = "";
            AUTHORIZATION_SOURCE_CODE = "";
            ISSUER_CHARGE = "";
            PURCHASE_ID_FORMAT = "";
            PFX_APPLIED_IND = "";
            ACCOUNT_SELECTION = "";
            CARD_ACCEPTOR_ID = "";
            INSTALLMENT_PAYMENT_COUNT = "";
            TERMINAL_ID = "";
            PURCHASE_IDENTIFIER = "";
            NAT_REIMBURSEMENT_FEE = "";
            CASHBACK = "";
            MAIL_PHN_ECOMM_IND = "";
            CHIP_CONDITION_CODE = "";
            SPECIAL_CHARGEBACK_IND = "";
            POS_ENVIRONMENT = "";
            ENABLE_VERFICATION_VAL = "";
            VISA_INTERNAL_USE_ONLY = "";
            BUSINESS_FORMAT_CODE_SD = "";
            SD_ADDl_TXN_FEE_ONE = "";
            NETWORK_IDN_CODE = "";
            SD_ADDl_TXN_FEE_TWO = "";
            CONTACT_FOR_INFO = "";
            TOTAL_DISCOUNT_AMOUNT = "";
            ADJUSMENT_PROCESSING_IND = "";
            SURCHRG_AMT_CARD_CURRENCY = "";
            MESSAGE_REASON_CODE = "";
            MONEY_TRANSFER_FRN_EX_FEE = "";
            SURCHARGE_AMOUNT = "";
            SD_PAYMENT_ACCOUNT_REFERE = "";
            SURCHARGE_CREDIT_DEBIT_IND = "";
            TOKEN_REQUESTOR_ID = "";
            TRANSACTION_IND = "";
            ECOMMERCE_GOODS_IND = "";
            AUTHORIZED_AMOUNT = "";
            MERCHANT_VERIFICATION_VAL = "";
            AUTH_CURRENCY_CD = "";
            INTERCHANGE_FEE_AMOUNT = "";
            AUTHORIZATION_RESPONSE_CD = "";
            INTERCHANGE_FEE_SIGN = "";
            VALIDATION_CODE = "";
            SRC_TO_BASE_CURR_EX_RATE = "";
            EXCLUDED_TRANS_ID_RSN = "";
            BASE_TO_DEST_CURR_EX_RATE = "";
            CRS_PROCESSING_CODE = "";
            OPTIONAL_ISSUER_ISA_AMT = "";
            CHARGEBACK_RIGHTS_IND = "";
            PRODUCT_ID = "";
            MULTIPLE_CLEARING_SEQ_NBR = "";
            PROGRAM_ID = "";
            MULTIPLE_CLEARING_SEQ_CNT = "";
            DYNAMIC_CURR_CONV_IND = "";
            MARKET_SPECIFIC_AUTH_DATA = "";
            ACCT_TYPE_IDENTIFICATION = "";
            TOTAL_AUTHORIZED_AMOUNT = "";
            SPEND_QUALIFIED_IND = "";
            INFORMATION_INDICATOR = "";
            PAN_TOKEN = "";
            MERCHANT_TELEPHONE_NUMBER = "";
            ACCOUNT_FUNDING_SOURCE = "";
            ADDITIONAL_DATA_IND = "";
            CVV2_RESULT_CODE = "";
            MERCHANT_VOLUME_IND = "";
          } 
        } 
      } 
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
