package com.recon.util;


import com.recon.model.CompareSetupBean;
import com.recon.model.TCRFile;
import com.recon.util.OracleConn;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ReadNUploadVisaFile {
  Connection con;
  
  PreparedStatement pstmt;
  
  public boolean validateandUploadFile(String filename, String filePath, String filedate) {
    CompareSetupBean setupBean = new CompareSetupBean();
    com.recon.util.ReadNUploadVisaFile readFile = new com.recon.util.ReadNUploadVisaFile();
    boolean process_status = true;
    try {
      setupBean.setCategory("VISA");
      setupBean.setFileDate(filedate);
      List<String> files_to_be_upload = checkUpload(setupBean);
      if (files_to_be_upload.size() == 0)
        return false; 
      process_status = readFile.readFile(filePath, filedate);
      if (process_status)
        readFile.updateRecords(files_to_be_upload, setupBean); 
    } catch (Exception e) {
      System.out.println("Exception in validateFile " + e);
      return false;
    } 
    return process_status;
  }
  
  public void updateRecords(List<String> file_ids, CompareSetupBean setupBean) {
    Connection conn = null;
    try {
      OracleConn oracon = new OracleConn();
      conn = oracon.getconn();
      for (String fileId : file_ids) {
        String stSubCateg = "";
        int count = 0;
        String GET_SUBCATE = "SELECT FILE_SUBCATEGORY FROM MAIN_FILESOURCE WHERE FILEID = '" + fileId + "'";
        PreparedStatement stm = conn.prepareStatement(GET_SUBCATE);
        ResultSet rset = stm.executeQuery();
        if (rset.next())
          stSubCateg = rset.getString("FILE_SUBCATEGORY"); 
        String CHECK_DATA = "SELECT COUNT(*) FROM MAIN_FILE_UPLOAD_DTLS WHERE FILEID = '" + fileId + "' AND CATEGORY = '" + setupBean.getCategory() + "' " + 
          "AND FILE_SUBCATEGORY = '" + stSubCateg + "' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + setupBean.getFileDate() + "'";
        stm = conn.prepareStatement(CHECK_DATA);
        rset = stm.executeQuery();
        if (rset.next())
          count = rset.getInt(1); 
        if (count == 0) {
          String INSERT_DATA = "INSERT INTO MAIN_FILE_UPLOAD_DTLS (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG)  VALUES('" + 
            fileId + "',TO_DATE('" + setupBean.getFileDate() + "','DD/MM/YYYY'),'AUTOMATION',SYSDATE,'" + setupBean.getCategory() + "','" + stSubCateg + "'," + 
            "'Y','N','N','N','N','N')";
          stm = conn.prepareStatement(INSERT_DATA);
          stm.execute();
          continue;
        } 
        String UPDATE_QUERY = "UPDATE MAIN_FILE_UPLOAD_DTLS SET UPLOAD_FLAG = 'Y' WHERE FILEID = '" + fileId + "' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + setupBean.getFileDate() + "'" + 
          " AND CATEGORY = '" + setupBean.getCategory() + "' AND FILE_SUBCATEGORY = '" + stSubCateg + "'";
        PreparedStatement stm1 = conn.prepareStatement(UPDATE_QUERY);
        stm1.executeUpdate();
      } 
    } catch (Exception e) {
      System.out.println("Exception in updateRecords " + e);
    } finally {
      if (conn != null)
        try {
          conn.close();
        } catch (SQLException e) {
          System.out.println("Error while closing connextion " + e);
        }  
    } 
  }
  
  public boolean readFile(String filepath, String fileDate) {
    InputStream fis = null;
    String stTable_Name = "";
    ArrayList<String> Amount_translation = new ArrayList<>();
    Connection conn = null;
    TCRFile tcrFileObj = new TCRFile();
    try {
      fis = new FileInputStream(filepath);
    } catch (FileNotFoundException ex) {
      System.out.println("Exception" + ex);
    } 
    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
    String thisLine = null;
    try {
      OracleConn con = new OracleConn();
      conn = con.getconn();
      System.out.println("Reading data " + (new Date()).toString());
      String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'VISA_INPUT_FILE'";
      PreparedStatement pstmt1 = conn.prepareStatement(CHECK_TABLE);
      ResultSet rset1 = pstmt1.executeQuery();
      int isPresent = 0;
      if (rset1.next())
        isPresent = rset1.getInt(1); 
      if (isPresent == 0) {
        String CREATE_QUERY = "CREATE TABLE VISA_INPUT_FILE (TC VARCHAR2(100 BYTE), TCR_CODE VARCHAR2(100 BYTE), STRING VARCHAR2(1000 BYTE), DCRS_SEQ_NO VARCHAR2(100 BYTE), FILEDATE DATE)";
        PreparedStatement pstmt = conn.prepareStatement(CREATE_QUERY);
        pstmt.executeQuery();
      } 
      String GET_RAW_TABLE = "SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILENAME = 'VISA' AND FILE_CATEGORY = 'VISA' AND FILE_SUBCATEGORY = 'ACQUIRER'";
      pstmt1 = conn.prepareStatement(GET_RAW_TABLE);
      rset1 = pstmt1.executeQuery();
      if (rset1.next())
        stTable_Name = rset1.getString("TABLENAME"); 
      String stline = br.readLine();
      String DATA_INSERT_05 = "INSERT INTO " + stTable_Name + " (TC,TCR_CODE,DCRS_SEQ_NO,FILEDATE,PART_ID,CARD_NUMBER," + 
        "Floor_Limit_indi,ARN,Acquirer_Busi_ID,Purchase_Date,Destination_Amount,Destination_Curr_Code,SOURCE_AMOUNT,Source_Curr_Code,Merchant_Name,Merchant_City," + 
        "Merchant_Country_Code,Merchant_Category_Code,Merchant_ZIP_Code,Usage_Code,Reason_Code,Settlement_Flag,Auth_Chara_Ind,AUTHORIZATION_CODE," + 
        "POS_Terminal_Capability,Cardholder_ID_Method," + 
        "Collection_Only_Flag,POS_Entry_Mode,Central_Process_Date,Reimbursement_Attr) " + 
        " VALUES(?,?,?,TO_DATE(?,'DD/MM/YYYY'),?," + 
        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      String DATA_INSERT_10 = "INSERT INTO " + stTable_Name + " (TC,TCR_CODE,DCRS_SEQ_NO,FILEDATE,PART_ID,Destination_BIN,Source_BIN,Reason_Code,Country_Code,Event_Date," + 
        "CARD_NUMBER,Destination_Amount,Destination_Curr_Code,SOURCE_AMOUNT,Source_Curr_Code,Message_Text,Settlement_Flag,Transac_Identifier,Central_Process_Date,Reimbursement_Attr) " + 
        " VALUES(?,?,?,TO_DATE(?,'DD/MM/YYYY'),?," + 
        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      PreparedStatement read_stmt_05 = conn.prepareStatement(DATA_INSERT_05);
      PreparedStatement read_stmt_10 = conn.prepareStatement(DATA_INSERT_10);
      String INSERT_33 = "INSERT INTO " + stTable_Name + " (TC,TCR_CODE,CARD_NUMBER,SOURCE_AMOUNT,TRACE,REFERENCE_NUMBER,RESPONSE_CODE,DCRS_SEQ_NO,FILEDATE,PART_ID,REQ_MSGTYPE)" + 
        " VALUES(?,?,?,?,?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?,?)";
      PreparedStatement pstmt33 = conn.prepareStatement(INSERT_33);
      int count = 0;
      int batch = 1;
      float amt = 0.0F;
      String INSERT_QUERY = "INSERT INTO VISA_INPUT_FILE (TC, TCR_CODE, STRING,DCRS_SEQ_NO,FILEDATE) VALUES(?,?,?,?,TO_DATE(?,'DD/MM/YYYY'))";
      pstmt1 = conn.prepareStatement(INSERT_QUERY);
      String Seq_num = "select 'VISA'||visa_seq.nextval AS SEQ from dual";
      PreparedStatement pstmt2 = conn.prepareStatement(Seq_num);
      String seq = "";
      String SIGN_QUERY = "SELECT * FROM VISA_CHAR_TRANSLATION";
      PreparedStatement sign_pstmt = conn.prepareStatement(SIGN_QUERY);
      ResultSet sign_rset = sign_pstmt.executeQuery();
      while (sign_rset.next()) {
        String Data = String.valueOf(sign_rset.getString("CHARACTER")) + "|" + sign_rset.getInt("LAST_DIGIT") + "|" + sign_rset.getString("SIGN");
        Amount_translation.add(Data);
      } 
      List<String> stRawData = new ArrayList<>();
      while (stline != null) {
        stRawData.clear();
        boolean containsVISATable = false;
        String check_TCR = "";
        String ACC_NO = "";
        String SOURCE_AMT = "";
        String AUTH_CODE = "";
        String TRACE = "";
        String RESPONSE_CODE = "";
        String REFERENCE_NUMBER = "";
        String CARD_NUMBER = "";
        String REQ_MSG_TYPE = "";
        List<String> Data_Elements = new ArrayList<>();
        String TC = stline.substring(0, 2);
        String TCR_Code = stline.substring(2, 4);
        if (TC.equals("05") || TC.equals("06") || TC.equals("07") || TC.equals("25") || TC.equals("27")) {
          if (TCR_Code.equals("00"))
            Data_Elements = tcrFileObj.TCR050Format(); 
        } else if (TC.equals("10")) {
          if (TCR_Code.equals("00"))
            Data_Elements = tcrFileObj.TCR10FeeCollectionFormat(); 
        } else if (TC.equals("20")) {
          if (TCR_Code.equals("00"))
            Data_Elements = tcrFileObj.TCR20FundDisbursement(); 
        } else if (TC.equals("33")) {
          String stLine = stline.substring(34, stline.length());
          if (stLine.contains("VISA TABLE")) {
            containsVISATable = true;
          } else {
            check_TCR = stLine.substring(3, 6);
            if (check_TCR.equals("200"))
              Data_Elements = tcrFileObj.V22200(); 
          } 
        } 
        if (!containsVISATable)
          for (int i = 0;; i++); 
        if (TC.equals("05") || TC.equals("06") || TC.equals("07") || TC.equals("25") || TC.equals("27")) {
          count++;
          if (TCR_Code.equals("00")) {
            ResultSet rset2 = pstmt2.executeQuery();
            if (rset2.next())
              seq = rset2.getString("SEQ"); 
          } 
          pstmt1.setString(1, TC);
          pstmt1.setString(2, TCR_Code);
          pstmt1.setString(3, stline);
          pstmt1.setString(4, (new StringBuilder(String.valueOf(seq))).toString());
          pstmt1.setString(5, fileDate);
          pstmt1.addBatch();
          if (TCR_Code.equals("00")) {
            read_stmt_05.setString(1, TC);
            read_stmt_05.setString(2, TCR_Code);
            read_stmt_05.setString(3, (new StringBuilder(String.valueOf(seq))).toString());
            read_stmt_05.setString(4, fileDate);
            read_stmt_05.setString(5, "1");
            int index = 6;
            for (int m = 0; m < stRawData.size(); m++) {
              read_stmt_05.setString(index, stRawData.get(m));
              index++;
            } 
            read_stmt_05.addBatch();
          } 
        } else if (TC.equals("10") || TC.equals("20")) {
          count++;
          if (TCR_Code.equals("00")) {
            ResultSet rset2 = pstmt2.executeQuery();
            if (rset2.next())
              seq = rset2.getString("SEQ"); 
          } 
          pstmt1.setString(1, TC);
          pstmt1.setString(2, TCR_Code);
          pstmt1.setString(3, stline);
          pstmt1.setString(4, (new StringBuilder(String.valueOf(seq))).toString());
          pstmt1.setString(5, fileDate);
          pstmt1.addBatch();
          if (TCR_Code.equals("00")) {
            read_stmt_10.setString(1, TC);
            read_stmt_10.setString(2, TCR_Code);
            read_stmt_10.setString(3, (new StringBuilder(String.valueOf(seq))).toString());
            read_stmt_10.setString(4, fileDate);
            read_stmt_10.setString(5, "1");
            int index = 6;
            for (int m = 0; m < stRawData.size(); m++) {
              read_stmt_10.setString(index, stRawData.get(m));
              index++;
            } 
            read_stmt_10.addBatch();
          } 
        } else if (TC.equals("33")) {
          count++;
          if (check_TCR.equals("200") || containsVISATable) {
            ResultSet rset2 = pstmt2.executeQuery();
            if (rset2.next())
              seq = rset2.getString("SEQ"); 
          } 
          pstmt1.setString(1, TC);
          pstmt1.setString(2, check_TCR);
          pstmt1.setString(3, stline);
          pstmt1.setString(4, (new StringBuilder(String.valueOf(seq))).toString());
          pstmt1.setString(5, fileDate);
          pstmt1.addBatch();
          if (check_TCR.equals("200") && !containsVISATable) {
            pstmt33.setString(1, TC);
            pstmt33.setString(2, check_TCR);
            pstmt33.setString(3, CARD_NUMBER);
            pstmt33.setString(4, (new StringBuilder(String.valueOf(amt))).toString());
            pstmt33.setString(5, TRACE);
            pstmt33.setString(6, REFERENCE_NUMBER);
            pstmt33.setString(7, RESPONSE_CODE);
            pstmt33.setString(8, seq);
            pstmt33.setString(9, fileDate);
            pstmt33.setString(10, "1");
            pstmt33.setString(11, REQ_MSG_TYPE);
            pstmt33.addBatch();
          } 
        } 
        if (count == 10000) {
          count = 1;
          pstmt1.executeBatch();
          read_stmt_05.executeBatch();
          read_stmt_10.executeBatch();
          pstmt1.executeBatch();
          pstmt33.executeBatch();
          System.out.println("EXECUTED BATCH " + batch);
          batch++;
        } 
        stline = br.readLine();
      } 
      read_stmt_05.executeBatch();
      read_stmt_10.executeBatch();
      pstmt1.executeBatch();
      pstmt33.executeBatch();
      System.out.println("Completed Reading");
    } catch (Exception e) {
      System.out.println("Exception in readFile " + e);
      return false;
    } finally {
      if (conn != null)
        try {
          conn.close();
        } catch (SQLException e) {
          System.out.println("Error while closing connextion " + e);
        }  
    } 
    return true;
  }
  
  public List<String> checkUpload(CompareSetupBean setupBean) {
    List<String> File_to_be_uploaded = new ArrayList<>();
    try {
      String GET_FILEID = "SELECT FILEID,FILE_SUBCATEGORY FROM MAIN_FILESOURCE WHERE FILE_CATEGORY = 'VISA' AND FILENAME = 'VISA'";
      OracleConn Oraconn = new OracleConn();
      Connection conn = Oraconn.getconn();
      PreparedStatement stmt = conn.prepareStatement(GET_FILEID);
      ResultSet rset = stmt.executeQuery();
      List<String> fileid = new ArrayList<>();
      List<String> subcategory = new ArrayList<>();
      List<String> uploadStatus = new ArrayList<>();
      while (rset.next()) {
        fileid.add(rset.getString("FILEID"));
        subcategory.add(rset.getString("FILE_SUBCATEGORY"));
      } 
      for (String file_id : fileid) {
        String query = "SELECT UPLOAD_FLAG FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('" + 
          setupBean.getFileDate() + 
          "','dd/mm/yyyy'),'dd/mm/yyyy')  " + " AND CATEGORY = '" + 
          setupBean.getCategory() + "' AND FileId = " + 
          file_id + " ";
        query = " SELECT CASE WHEN exists (" + query + ") then (" + query + 
          ") else 'N' end as FLAG from dual";
        stmt = conn.prepareStatement(query);
        rset = stmt.executeQuery();
        while (rset.next())
          uploadStatus.add(rset.getString("FLAG")); 
      } 
      if (uploadStatus.contains("N")) {
        for (int i = 0; i < uploadStatus.size(); i++) {
          if (((String)uploadStatus.get(i)).equals("Y")) {
            System.out.println("File is uploaded for VISA " + (String)subcategory.get(i));
          } else if (((String)uploadStatus.get(i)).equals("N")) {
            System.out.println("FILE NOT UPLOADED FOR VISA " + (String)subcategory.get(i) + " FILE ID FOR SAME IS " + (String)fileid.get(i));
            File_to_be_uploaded.add(fileid.get(i));
          } 
        } 
      } else {
        System.out.println("FILES ARE ALREADY UPLOADED ");
        return File_to_be_uploaded;
      } 
      return File_to_be_uploaded;
    } catch (Exception e) {
      System.out.println("Exception in checkUpload " + e);
      return File_to_be_uploaded;
    } 
  }
  
  public static void main(String[] args) {
    com.recon.util.ReadNUploadVisaFile readVisa = new com.recon.util.ReadNUploadVisaFile();
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter file path: ");
    System.out.flush();
    String filename = scanner.nextLine();
    System.out.print("Enter a file date in (dd/MM/yyyy) format: ");
    String filedate = scanner.nextLine();
    File file = new File(filename);
    System.out.println(file.getName());
    boolean validate = readVisa.validateandUploadFile(file.getName(), filename, filedate);
    if (validate)
      System.out.println("FILE READING SUCCESSFULLY COMPLETED "); 
  }
}
