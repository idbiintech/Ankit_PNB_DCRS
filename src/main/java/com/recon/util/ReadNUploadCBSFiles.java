package com.recon.util;


import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;
import com.recon.service.ICompareConfigService;
import com.recon.util.OracleConn;
import com.recon.util.ReadNUploadCBSAcquirer;
import com.recon.util.ReadNUploadCBSIssuer;
import com.recon.util.ReadNUploadCBSOnus;
import com.recon.util.Utility;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

public class ReadNUploadCBSFiles {
  @Autowired
  ICompareConfigService icompareConfigService;
  
  String partid;
  
  private int Part_id;
  
  private static final String O_ERROR_MESSAGE = "o_error_message";
  
  public static final String CATALINA_HOME = "catalina.home";
  
  public static final String OUTPUT_FOLDER = String.valueOf(System.getProperty("catalina.home")) + File.separator + "OUTPUT_FOLDER";
  
  @Autowired
  Utility utility;
  
  public boolean uploadCBSData(String fileName, String filepath) {
    String[] filenameSplit = fileName.split("_");
    try {
      boolean uploaded = false;
      System.out.println(fileName);
      if (filenameSplit[0].contains("CBS702")) {
        System.out.println("Entered CBS File is Issuer");
        ReadNUploadCBSIssuer readIssuer = new ReadNUploadCBSIssuer();
        uploaded = readIssuer.uploadCBSData(fileName, filepath);
      } else if (filenameSplit[0].contains("CBS703")) {
        System.out.println("Entered CBS File is Acquirer");
        ReadNUploadCBSAcquirer readacquirer = new ReadNUploadCBSAcquirer();
        uploaded = readacquirer.uploadCBSData(fileName, filepath);
      } else if (filenameSplit[0].equalsIgnoreCase("CBSC43") || filenameSplit[0].equalsIgnoreCase("CBS43")) {
        System.out.println("Entered CBS File is ONUS");
        ReadNUploadCBSOnus readOnus = new ReadNUploadCBSOnus();
        uploaded = readOnus.Read_CBSData(fileName, filepath);
      } else {
        System.out.println("Entered File is Wrong");
        return false;
      } 
      return true;
    } catch (Exception e) {
      System.out.println("Error Occured");
      e.printStackTrace();
      return false;
    } 
  }
  
  public static void main(String[] args) {
    com.recon.util.ReadNUploadCBSFiles readcbs = new com.recon.util.ReadNUploadCBSFiles();
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter file path: ");
    System.out.flush();
    String filename = scanner.nextLine();
    File file = new File(filename);
    if (readcbs.uploadCBSData(file.getName(), file.getPath())) {
      System.out.println("File uploaded successfully");
    } else {
      System.out.println("File uploading failed");
    } 
  }
  
  public boolean uploadCBSData(CompareSetupBean setupBean, Connection connection, MultipartFile file, FileSourceBean sourceBean) {
    System.out.println("uploadCBSData method called");
    try {
      boolean uploaded = false;
      System.out.println("Entered CBS File IS " + file.getOriginalFilename());
      return uploadISSData(setupBean, connection, file, sourceBean);
    } catch (Exception e) {
      System.out.println("Error Occured");
      e.printStackTrace();
      return false;
    } 
  }
  
  public boolean iccwuploadCBSData(CompareSetupBean setupBean, Connection connection, MultipartFile file, FileSourceBean sourceBean) {
    try {
      boolean uploaded = false;
      System.out.println("Entered CBS File IS " + file.getOriginalFilename());
      return iccwuploadcbsData(setupBean, connection, file, sourceBean);
    } catch (Exception e) {
      System.out.println("Error Occured");
      e.printStackTrace();
      return false;
    } 
  }
  
  public boolean uploadONUSData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    int flag = 1, batch = 0;
    InputStream fis = null;
    boolean readdata = false;
    String thisLine = null;
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      System.out.println("Reading data " + (new Date()).toString());
      String insert = "INSERT INTO CBS_RAWDATA (ACCOUNT_NUMBER,TRANDATE,VALUEDATE,TRAN_ID,TRAN_PARTICULAR,TRAN_RMKS,PART_TRAN_TYPE,TRAN_PARTICULAR1,TRAN_AMT,BALANCE,PSTD_USER_ID,CONTRA_ACCOUNT,ENTRY_DATE,VFD_DATE,REF_NUM,TRAN_PARTICULAR_2,ORG_ACCT,Part_id,FILEDATE,CREATEDDATE) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'dd/mm/yyyy')),sysdate)";
      PreparedStatement ps = con.prepareStatement(insert);
      int insrt = 0;
      while ((thisLine = br.readLine()) != null) {
        String[] splitarray = null;
        if (thisLine.contains("ACCOUNT NUMBER|TRAN DATE|"))
          readdata = true; 
        if (!thisLine.contains("ACCOUNT NUMBER|TRAN DATE|") && readdata) {
          int srl = 1;
          ps.setString(15, (String)null);
          ps.setString(16, (String)null);
          splitarray = thisLine.split(Pattern.quote("|"));
          for (int i = 0; i < splitarray.length; i++) {
            String value = splitarray[i];
            if (!value.trim().equalsIgnoreCase("")) {
              ps.setString(srl, value.trim());
              srl++;
            } else {
              ps.setString(srl, (String)null);
              srl++;
            } 
          } 
          ps.setString(17, (String)null);
          ps.setInt(18, this.Part_id);
          ps.setString(19, setupBean.getFileDate());
          ps.addBatch();
          flag++;
          if (flag == 20000) {
            flag = 1;
            ps.executeBatch();
            System.out.println("Executed batch is " + batch);
            batch++;
          } 
        } 
      } 
      ps.executeBatch();
      br.close();
      ps.close();
      System.out.println("Reading data " + (new Date()).toString());
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Exception" + ex);
      return false;
    } 
  }
  
  public boolean uploadACQData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    int flag = 1, batch = 0;
    InputStream fis = null;
    boolean readdata = false;
    String thisLine = null;
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      System.out.println("Reading data " + (new Date()).toString());
      String insert = "INSERT INTO CBS_AMEX_RAWDATA (FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,pstd_user_id ,ENTRY_DATE,VFD_DATE,PARTICULARALS2,Part_id,FILEDATE) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'dd/mm/yyyy')))";
      PreparedStatement ps = con.prepareStatement(insert);
      int insrt = 0;
      while ((thisLine = br.readLine()) != null) {
        String[] splitarray = null;
        if (thisLine.contains("------"))
          readdata = true; 
        if (!thisLine.contains("-----") && readdata) {
          int srl = 1;
          splitarray = thisLine.split(Pattern.quote("|"));
          for (int i = 0; i < splitarray.length; i++) {
            String value = splitarray[i];
            if (!value.equalsIgnoreCase("")) {
              ps.setString(srl, value.trim());
              srl++;
            } else {
              ps.setString(srl, (String)null);
              srl++;
            } 
          } 
          ps.setInt(16, this.Part_id);
          ps.setString(17, setupBean.getFileDate());
          ps.addBatch();
          flag++;
          if (flag == 20000) {
            flag = 1;
            ps.executeBatch();
            System.out.println("Executed batch is " + batch);
            batch++;
          } 
        } 
      } 
      ps.executeBatch();
      br.close();
      ps.close();
      System.out.println("Reading data " + (new Date()).toString());
      return true;
    } catch (Exception ex) {
      System.out.println("error occurred");
      ex.printStackTrace();
      return false;
    } 
  }
  
  public boolean uploadISSData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    System.out.println("uploadISSData method called");
    String filePath = "";
    ClassPathResource classPathResource = new ClassPathResource("/resources/switchpr.asc");
    String stLine = null;
    int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0;
    boolean batchExecuted = false;
    String InsertQuery = "INSERT INTO  UBI_CBS_RAWDATA (BUSINESS_DATE,CARD_NO,TRACE_NO,AC_NO,TRAN_DATE,ATM_ID,TYPE,AMOUNT,FEE,BR_CODE,ISS_SOL_ID,MCC,REMARKS,NETWORK,POSTED_DATE,GL_ACCOUNT,TR_NO,DCRS_REMARKS,FILEDATE,FILENAME,CREATEDDATE) VALUES(TO_DATE(?,'DD/MM/YYYY'),?,?,?,TO_DATE(?,'DD/MM/YYYY'),?,?,?,?,?,?,?,?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?,?,?,TO_DATE(?,'DD/MM/YYYY'))";
    String delQuery = "delete from CBS_UCO_RAWDATA_TEMP";
    try {
      String fileNameWithExt = file.getOriginalFilename();
      int lastDotIndex = fileNameWithExt.lastIndexOf(".");
      String fileNameWithoutExt = "";
      if (lastDotIndex > 0)
        fileNameWithoutExt = fileNameWithExt.substring(0, lastDotIndex); 
      File folder = new File(OUTPUT_FOLDER);
      if (folder.exists())
        folder.delete(); 
      folder.mkdir();
      File file1 = new File(folder, fileNameWithoutExt);
      try {
        if (file1.exists())
          file1.delete(); 
        file1.createNewFile();
      } catch (Exception e) {
        System.out.println(e);
      } 
      String newPath = String.valueOf(OUTPUT_FOLDER) + File.separator + fileNameWithoutExt;
      System.out.println("File to be write at path: " + newPath);
      InputStream keyIn = classPathResource.getInputStream();
      FileOutputStream out = new FileOutputStream(newPath);
      PreparedStatement delpst = con.prepareStatement(delQuery);
      delpst.execute();
      File outFile = new File(newPath);
      System.out.println("File reading from Path: " + newPath);
      System.out.println("File reading : " + outFile);
      System.out.println("File in tomcat " + outFile);
      BufferedReader br = new BufferedReader(new FileReader(outFile));
      try {
        PreparedStatement ps = con.prepareStatement(InsertQuery);
        while ((stLine = br.readLine()) != null) {
          if (stLine.startsWith("999  ,H") || stLine.startsWith("999  ,F"))
            continue; 
          lineNumber++;
          batchExecuted = false;
          sr_no = 1;
          String[] splitData = stLine.split("\\,");
          for (int i = 0; i <= splitData.length - 1; i++) {
            if (i != 8 && i != 7 && i != 11 && i != 15)
              if (i == 2) {
                String cardNumber = formatCardNumber(splitData[i].trim());
                ps.setString(sr_no++, cardNumber);
                ps.setString(sr_no++, splitData[i].trim());
              } else {
                ps.setString(sr_no++, splitData[i].trim());
              }  
          } 
          ps.setString(sr_no++, setupBean.getCreatedBy());
          ps.setString(sr_no++, file.getOriginalFilename());
          ps.setString(sr_no++, setupBean.getFileDate());
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
      } catch (IOException e) {
        System.out.println("exception for converting is " + e);
      } 
      return true;
    } catch (Exception e) {
      System.out.println("Issue at line " + stLine);
      System.out.println("Exception in uploadISSData " + e);
      return false;
    } 
  }
  
  private String formatCardNumber(String cardNumber) {
    int totalLength = cardNumber.length();
    int firstSixDigitLength = Math.min(totalLength, 6);
    int lastFourDigitLength = Math.min(totalLength - 10, 4);
    String firstSixDigits = cardNumber.substring(0, firstSixDigitLength);
    String midX = "XXXXXX";
    String lastFourDigits = cardNumber.substring(totalLength - lastFourDigitLength);
    StringBuilder xx = new StringBuilder();
    for (int i = 0; i < totalLength - 10; i++)
      xx.append("X"); 
    String formatedCardNumber = String.valueOf(firstSixDigits) + xx + lastFourDigits;
    return formatedCardNumber;
  }
  
  public boolean uploadNewISSData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    String stLine = null;
    int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0, startLine = 1;
    boolean batchExecuted = false;
    String InsertQuery = "INSERT INTO CBS_UCO_RAWDATA(SOLID, TRAN_AMOUNT, PART_TRAN_TYPE, CARD_NUMBER, RRN, ACC_NUMBER, SYSDT, SYS_TIME, CMD, TRAN_TYPE, DEVICE_ID, TRAN_ID, TRAN_DATE, FEE, SRV_TAX, SRV_CHRG, VISAIND, CREATEDBY, FILEDATE, BANK_NAME) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, str_to_date(?,'DD/MM/YYYY'),'UCO')";
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      PreparedStatement ps = con.prepareStatement(InsertQuery);
      while ((stLine = br.readLine()) != null) {
        if (startLine >= 12) {
          if (!stLine.trim().contains("-----------------------------") && !stLine.trim().equals("")) {
            stLine = stLine.substring(10);
            lineNumber++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = stLine.split("\\|");
            for (int i = 1; i <= splitData.length; i++) {
              if (i != 13)
                ps.setString(sr_no++, splitData[i - 1]); 
            } 
            ps.setString(sr_no++, setupBean.getCreatedBy());
            ps.setString(sr_no++, setupBean.getFileDate());
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
          continue;
        } 
        startLine++;
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
      System.out.println("Exception in uploadISSData " + e);
      return false;
    } 
  }
  
  public HashMap<String, Object> uploadUbiCBSData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    HashMap<String, Object> output = new HashMap<>();
    String thisline = null;
    int lineNumber = 0, feesize = 1;
    int count = 0;
    int sr_no = 1, batchNumber = 0, batchSize = 40000;
    boolean batchExecuted = false;
    String update_query = "INSERT INTO  switch_data_validation(FILENAME,FILEDATE,COUNT,FILETYPE) VALUES(?,?,?,?)";
    String InsertQuery = "INSERT INTO   all_cbs_rawdata(Atm_Id ,Ref_Num ,Card_Number ,Tran_Amt ,Tran_Date ,Rcre_Time ,Init_Sol_Id ,Value_Date  ,Part_Tran_Type ,Foracid ,Bank_Id ,Cummulative_Balance ,Entry_User ,Tran_Particular ,Tran_Id ,Contra_Foracid ,Tran_Remark ,Posted_Time ,Verified_Time ,Tran_Part_1 ,Tran_Part_2,FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9924 = "INSERT INTO  cbs_rupay_card_tran (tran_particular1, tran_particular2, tran_particular3, tran_amount,tran_date, rcre_time,init_sol_id, value_date, part_tran_type,foracid,tran_remarks,cummulative_balance,tran_particular,narration ,sol_id,tran_id,tran_remarks1,posted_time,verified_time, FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9926 = "INSERT INTO  cbs_rupay_all_rawdata (tran_particular  ,tran_amount ,tran_date ,rcre_time ,init_sol_id  ,value_date ,part_tran_type ,foracid ,tran_remarks ,cummulative_balance , tran_particular1 ,narration ,sol_id ,tran_id ,tran_remarks1 , posted_time  ,approval_code ,FILEDATE ,FILENAME ,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9931 = "INSERT INTO  cbs_rupay_all_rawdata1 (tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,approval_code,flag,FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9932 = "INSERT INTO  cbs_rupay_all_rawdata1 (tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,approval_code,flag,FILEDATE,FILENAME,CREATEDDATE)\tVALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9933 = "INSERT INTO  cbs_rupay_all_rawdata1 (tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,approval_code,FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9934 = "INSERT INTO cbs_rupay_all_rawdata1 (tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,approval_code,FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery1355 = "INSERT INTO  cbs_rupay_all_rawdata (tran_particular  ,tran_amount ,tran_date ,rcre_time ,init_sol_id  ,value_date ,part_tran_type ,foracid ,tran_remarks ,cummulative_balance , tran_particular1 ,narration ,sol_id ,tran_id ,narration1 , posted_time  ,approval_code ,FILEDATE ,FILENAME ,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9936 = "INSERT INTO  cbs_rupay_all_rawdata1 (tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,approval_code,flag,FILEDATE,FILENAME,CREATEDDATE) \tVALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9938 = "INSERT INTO  cbs_rupay_all_rawdata1 (tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,approval_code,FILEDATE,FILENAME,CREATEDDATE)  VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9970 = "INSERT INTO  cbs_rupay_all_rawdata1(tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,approval_code,FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9971 = "INSERT INTO  cbs_rupay_all_rawdata1 (tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,approval_code,FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery2549 = "INSERT INTO  cbs_rupay_all_rawdata (tran_particular,tran_amount ,tran_date ,rcre_time ,init_sol_id  ,value_date ,part_tran_type ,foracid ,tran_remarks ,cummulative_balance , tran_particular1 ,narration ,sol_id ,tran_id ,narration1 , posted_time  ,verified_time , FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery2550 = "INSERT INTO  cbs_rupay_all_rawdata (tran_particular,tran_amount ,tran_date ,rcre_time ,init_sol_id  ,value_date ,part_tran_type ,foracid ,tran_remarks ,cummulative_balance , tran_particular1 ,narration ,sol_id ,tran_id ,narration1 , posted_time  ,verified_time, FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9954 = "INSERT INTO  cbs_rupay_all_rawdata1 (tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,approval_code,FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery2281 = "INSERT INTO  cbs_rupay_all_rawdata1 (tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,approval_code,FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery1356 = "INSERT INTO   cbs_rupay_all_rawdata1 (tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9950 = "INSERT INTO   cbs_rupay_all_rawdata1 (tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,approval_code,FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9951 = "INSERT INTO   cbs_rupay_all_rawdata1 (tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,approval_code,FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9968 = "INSERT INTO  cbs_rupay_all_rawdata1 (tran_particular1,seq_num,card_no,tran_amount,value_date,rcre_time,sol_id,tran_date,part_tran_type,atm_gl,tran_remarks,cummulative_balance,tran_particular,narration ,tran_id,uid,tran_remarks1,posted_time,verified_time,approval_code,FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9948 = "INSERT INTO  cbs_nfs_iss_rawdata (tran_date, narration,  dr_amount, cr_amount, GL_BALANCE,tran_date2, dummy, sys_man, tran_id, acc_number, narration2, FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String InsertQuery9949 = "INSERT INTO  cbs_nfs_acq_rawdata (tran_particular1, seq_num, card_no, tran_amount, value_date, rcre_time, sol_id, tran_date, part_tran_type, atm_gl, tran_remarks, cummulative_balance, tran_particular, narration, tran_id, uid, tran_remarks1, posted_time, verified_time, approval_code, reference_number, FILEDATE,FILENAME,CREATEDDATE)          VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    try {
      con.setAutoCommit(false);
      int batchCount = 0;
      PreparedStatement ps = con.prepareStatement(InsertQuery);
      PreparedStatement updatps = con.prepareStatement(update_query);
      PreparedStatement ps9931 = con.prepareStatement(InsertQuery9931);
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      PreparedStatement ps1356 = con.prepareStatement(InsertQuery1356);
      if (setupBean.getP_FILE_NAME().contains("5165005811356")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS")) {
            System.out.println("stLine " + thisline);
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps1356.setString(sr_no++, splitData[i - 1]); 
            ps1356.setString(20, setupBean.getFileDate());
            ps1356.setString(21, setupBean.getP_FILE_NAME());
            ps1356.setString(22, setupBean.getFileDate());
            ps1356.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps1356.executeBatch();
            con.commit();
          } 
        } 
        ps1356.executeBatch();
      } 
      PreparedStatement ps9968 = con.prepareStatement(InsertQuery9968);
      if (setupBean.getP_FILE_NAME().contains("5165005139968")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS")) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9968.setString(sr_no++, splitData[i - 1]); 
            ps9968.setString(21, setupBean.getFileDate());
            ps9968.setString(22, setupBean.getP_FILE_NAME());
            ps9968.setString(23, setupBean.getFileDate());
            ps9968.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9968.executeBatch();
            con.commit();
          } 
        } 
        ps9968.executeBatch();
      } 
      PreparedStatement ps9948 = con.prepareStatement(InsertQuery9948);
      if (setupBean.getP_FILE_NAME().contains("5165005139948") || 
        setupBean.getP_FILE_NAME().contains("5165005811354") || setupBean.getP_FILE_NAME().contains("5165005712453") ) {
        int linecount = 0;
        while ((thisline = br.readLine()) != null) {
          linecount++;
          if (!thisline.trim().contains("Balance B/F") && !thisline.trim().contains("end of the report") && 
            !thisline.trim().contains("-----------") && !thisline.trim().contains("A/C") && 
            !thisline.trim().contains("BO: Bombay") && 
            !thisline.trim().contains("Punjab National Bank") && thisline.trim().length() != 0)
            if (linecount == 100001 || linecount == 200001 || linecount == 300001 || linecount == 400001 || 
              linecount == 500001 || linecount == 600001 || linecount == 700001 || 
              linecount == 800001 || linecount == 900001 || linecount == 1000001 || 
              linecount == 1100001 || linecount == 1200001 || linecount == 1300001 || 
              linecount == 1400001 || linecount == 1500001 || linecount == 1600001 || linecount == 1700001 || linecount == 1800001 || linecount == 1900001 || linecount == 2000001) {
              count++;
              batchExecuted = false;
              sr_no = 1;
              ps9948.setString(1, thisline.substring(1, 11));
              ps9948.setString(2, thisline.substring(12, 59));
              ps9948.setString(3, thisline.substring(69, 96));
              ps9948.setString(4, thisline.substring(96, 113));
              ps9948.setString(5, thisline.substring(114, 143));
              ps9948.setString(6, thisline.substring(160, 171));
              ps9948.setString(7, thisline.substring(174, 197));
              ps9948.setString(8, thisline.substring(197, 199));
              ps9948.setString(9, thisline.substring(199, 208));
              ps9948.setString(10, thisline.substring(208, 225));
              ps9948.setString(11, thisline.substring(225, thisline.trim().length()));
              ps9948.setString(12, setupBean.getFileDate());
              ps9948.setString(13, setupBean.getP_FILE_NAME());
              ps9948.setString(14, setupBean.getFileDate());
              ps9948.addBatch();
            } else {
              count++;
              batchExecuted = false;
              sr_no = 1;
              ps9948.setString(1, thisline.substring(0, 10));
              ps9948.setString(2, thisline.substring(11, 58));
              ps9948.setString(3, thisline.substring(68, 95));
              ps9948.setString(4, thisline.substring(95, 112));
              ps9948.setString(5, thisline.substring(113, 142));
              ps9948.setString(6, thisline.substring(159, 170));
              ps9948.setString(7, thisline.substring(173, 196));
              ps9948.setString(8, thisline.substring(196, 198));
              ps9948.setString(9, thisline.substring(198, 207));
              ps9948.setString(10, thisline.substring(207, 224));
              ps9948.setString(11, thisline.substring(224, thisline.trim().length()));
              ps9948.setString(12, setupBean.getFileDate());
              ps9948.setString(13, setupBean.getP_FILE_NAME());
              ps9948.setString(14, setupBean.getFileDate());
              ps9948.addBatch();
            }  
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9948.executeBatch();
            con.commit();
          } 
        } 
        ps9948.executeBatch();
      } 
      PreparedStatement ps9949 = con.prepareStatement(InsertQuery9949);
      if (setupBean.getP_FILE_NAME().contains("5165005139949") || setupBean.getP_FILE_NAME().contains("5165005811351") || setupBean.getP_FILE_NAME().contains("5165005811352") || setupBean.getP_FILE_NAME().contains("5165005712451")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("solid") && !thisline.trim().contains("---------") && 
            thisline.trim().length() != 0) {
            count++;
            System.out.println("thisline is " + thisline.trim());
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9949.setString(sr_no++, splitData[i - 1]);
            if(setupBean.getP_FILE_NAME().contains("5165005139949") ) {
            	 ps9949.setString(20, "");
            	 ps9949.setString(21, "");
            }
            ps9949.setString(22, setupBean.getFileDate());
            ps9949.setString(23, setupBean.getP_FILE_NAME());
            ps9949.setString(24, setupBean.getFileDate());
            ps9949.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9949.executeBatch();
            con.commit();
          } 
        } 
        ps9949.executeBatch();
      } 
      PreparedStatement ps9950 = con.prepareStatement(InsertQuery9950);
      if (setupBean.getP_FILE_NAME().contains("5165005139950")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().startsWith("solid") && !thisline.trim().startsWith("---------") && 
            thisline.trim().length() != 0) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9950.setString(sr_no++, splitData[i - 1]); 
            ps9950.setString(21, setupBean.getFileDate());
            ps9950.setString(22, setupBean.getP_FILE_NAME());
            ps9950.setString(23, setupBean.getFileDate());
            ps9950.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9950.executeBatch();
            con.commit();
          } 
        } 
        ps9950.executeBatch();
      } 
      PreparedStatement ps9951 = con.prepareStatement(InsertQuery9951);
      if (setupBean.getP_FILE_NAME().contains("5165005139951")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS")) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9951.setString(sr_no++, splitData[i - 1]); 
            ps9951.setString(21, setupBean.getFileDate());
            ps9951.setString(22, setupBean.getP_FILE_NAME());
            ps9951.setString(23, setupBean.getFileDate());
            ps9951.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9951.executeBatch();
            con.commit();
          } 
        } 
        ps9951.executeBatch();
      } 
      PreparedStatement ps9924 = con.prepareStatement(InsertQuery9924);
      if (setupBean.getP_FILE_NAME().contains("5165005139924")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS")) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9924.setString(sr_no++, splitData[i - 1]); 
            ps9924.setString(20, setupBean.getFileDate());
            ps9924.setString(21, setupBean.getP_FILE_NAME());
            ps9924.setString(22, setupBean.getFileDate());
            ps9924.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9924.executeBatch();
            con.commit();
          } 
        } 
        ps9924.executeBatch();
      } 
      PreparedStatement ps9926 = con.prepareStatement(InsertQuery9926);
      if (setupBean.getP_FILE_NAME().contains("5165005139926")) {
        br.readLine();
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS")) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9926.setString(sr_no++, splitData[i - 1]); 
            ps9926.setString(18, setupBean.getFileDate());
            ps9926.setString(19, setupBean.getP_FILE_NAME());
            ps9926.setString(20, setupBean.getFileDate());
            ps9926.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9926.executeBatch();
            con.commit();
          } 
        } 
        ps9926.executeBatch();
      } 
      PreparedStatement ps1355 = con.prepareStatement(InsertQuery1355);
      if (setupBean.getP_FILE_NAME().contains("5165005811355")) {
        br.readLine();
        while ((thisline = br.readLine()) != null) {
          System.out.println("stLine " + thisline.trim().length());
          if (thisline.trim().length() != 11 && thisline.trim().length() != 28 && 
            !thisline.toUpperCase().trim().contains("HDRCBS") && 
            !thisline.trim().contains("FTRCBS")) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps1355.setString(sr_no++, splitData[i - 1]); 
            ps1355.setString(18, setupBean.getFileDate());
            ps1355.setString(19, setupBean.getP_FILE_NAME());
            ps1355.setString(20, setupBean.getFileDate());
            ps1355.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps1355.executeBatch();
            con.commit();
          } 
        } 
        ps1355.executeBatch();
      } 
      PreparedStatement ps2550 = con.prepareStatement(InsertQuery2550);
      if (setupBean.getP_FILE_NAME().contains("5165003182550")) {
        br.readLine();
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS")) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps2550.setString(sr_no++, splitData[i - 1]); 
            ps2550.setString(18, setupBean.getFileDate());
            ps2550.setString(19, setupBean.getP_FILE_NAME());
            ps2550.setString(20, setupBean.getFileDate());
            ps2550.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps2550.executeBatch();
            con.commit();
          } 
        } 
        ps2550.executeBatch();
      } 
      PreparedStatement ps9954 = con.prepareStatement(InsertQuery9954);
      if (setupBean.getP_FILE_NAME().contains("5165005139954")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS")) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9954.setString(sr_no++, splitData[i - 1]); 
            ps9954.setString(21, setupBean.getFileDate());
            ps9954.setString(22, setupBean.getP_FILE_NAME());
            ps9954.setString(23, setupBean.getFileDate());
            ps9954.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9954.executeBatch();
            con.commit();
          } 
        } 
        ps9954.executeBatch();
      } 
      PreparedStatement ps2281 = con.prepareStatement(InsertQuery2281);
      if (setupBean.getP_FILE_NAME().contains("0062002100152281")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS")) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps2281.setString(sr_no++, splitData[i - 1]); 
            ps2281.setString(21, setupBean.getFileDate());
            ps2281.setString(22, setupBean.getP_FILE_NAME());
            ps2281.setString(23, setupBean.getFileDate());
            ps2281.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps2281.executeBatch();
            con.commit();
          } 
        } 
        ps2281.executeBatch();
      } 
      PreparedStatement ps2549 = con.prepareStatement(InsertQuery2549);
      if (setupBean.getP_FILE_NAME().contains("5165003182549")) {
        br.readLine();
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS")) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps2549.setString(sr_no++, splitData[i - 1]); 
            ps2549.setString(18, setupBean.getFileDate());
            ps2549.setString(19, setupBean.getP_FILE_NAME());
            ps2549.setString(20, setupBean.getFileDate());
            ps2549.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps2549.executeBatch();
            con.commit();
            continue;
          } 
          batchNumber++;
          System.out.println("Batch Executed is " + batchNumber);
          ps2549.executeBatch();
          con.commit();
        } 
      } 
      if (setupBean.getP_FILE_NAME().contains("5165005139931")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("solid") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("------")) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9931.setString(sr_no++, splitData[i - 1]); 
            ps9931.setString(22, setupBean.getFileDate());
            ps9931.setString(23, setupBean.getP_FILE_NAME());
            ps9931.setString(24, setupBean.getFileDate());
            ps9931.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9931.executeBatch();
            con.commit();
          } 
        } 
        ps9931.executeBatch();
      } 
      PreparedStatement ps9933 = con.prepareStatement(InsertQuery9933);
      if (setupBean.getP_FILE_NAME().contains("5165005139933")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("solid") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("------") && thisline.trim().length() != 46 && 
            thisline.trim().length() != 1) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9933.setString(sr_no++, splitData[i - 1]); 
            ps9933.setString(21, setupBean.getFileDate());
            ps9933.setString(22, setupBean.getP_FILE_NAME());
            ps9933.setString(23, setupBean.getFileDate());
            ps9933.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9933.executeBatch();
            con.commit();
          } 
        } 
        ps9933.executeBatch();
      } 
      PreparedStatement ps9934 = con.prepareStatement(InsertQuery9934);
      if (setupBean.getP_FILE_NAME().contains("5165005139934")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("solid") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("------") && thisline.trim().length() != 46 && 
            thisline.trim().length() != 1) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9934.setString(sr_no++, splitData[i - 1]); 
            ps9934.setString(21, setupBean.getFileDate());
            ps9934.setString(22, setupBean.getP_FILE_NAME());
            ps9934.setString(23, setupBean.getFileDate());
            ps9934.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9934.executeBatch();
            con.commit();
          } 
        } 
        ps9934.executeBatch();
      } 
      PreparedStatement ps9938 = con.prepareStatement(InsertQuery9938);
      if (setupBean.getP_FILE_NAME().contains("5165005139938")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("solid") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("------") && thisline.trim().length() != 46 && 
            thisline.trim().length() != 1) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9938.setString(sr_no++, splitData[i - 1]); 
            ps9938.setString(21, setupBean.getFileDate());
            ps9938.setString(22, setupBean.getP_FILE_NAME());
            ps9938.setString(23, setupBean.getFileDate());
            ps9938.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9938.executeBatch();
            con.commit();
          } 
        } 
        ps9938.executeBatch();
      } 
      PreparedStatement ps9970 = con.prepareStatement(InsertQuery9970);
      if (setupBean.getP_FILE_NAME().contains("5165005139970")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("solid") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("------") && thisline.trim().length() != 46 && 
            thisline.trim().length() != 1) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9970.setString(sr_no++, splitData[i - 1]); 
            ps9970.setString(21, setupBean.getFileDate());
            ps9970.setString(22, setupBean.getP_FILE_NAME());
            ps9970.setString(23, setupBean.getFileDate());
            ps9970.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9970.executeBatch();
            con.commit();
          } 
        } 
        ps9970.executeBatch();
      } 
      PreparedStatement ps9971 = con.prepareStatement(InsertQuery9971);
      if (setupBean.getP_FILE_NAME().contains("5165005139971")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("solid") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("------") && thisline.trim().length() != 46 && 
            thisline.trim().length() != 1) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9971.setString(sr_no++, splitData[i - 1]); 
            ps9971.setString(21, setupBean.getFileDate());
            ps9971.setString(22, setupBean.getP_FILE_NAME());
            ps9971.setString(23, setupBean.getFileDate());
            ps9971.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9971.executeBatch();
            con.commit();
          } 
        } 
        ps9971.executeBatch();
      } 
      PreparedStatement ps9932 = con.prepareStatement(InsertQuery9932);
      if (setupBean.getP_FILE_NAME().contains("5165005139932")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("solid") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("------") && thisline.trim().length() != 46 && 
            thisline.trim().length() != 0) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9932.setString(sr_no++, splitData[i - 1]); 
            ps9932.setString(22, setupBean.getFileDate());
            ps9932.setString(23, setupBean.getP_FILE_NAME());
            ps9932.setString(24, setupBean.getFileDate());
            ps9932.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9932.executeBatch();
            con.commit();
          } 
        } 
        ps9932.executeBatch();
      } 
      PreparedStatement ps9936 = con.prepareStatement(InsertQuery9936);
      if (setupBean.getP_FILE_NAME().contains("5165005139936")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("solid") && !thisline.trim().contains("FTRCBS") && 
            !thisline.trim().contains("------") && thisline.trim().length() != 46 && 
            thisline.trim().length() != 1) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps9936.setString(sr_no++, splitData[i - 1]); 
            ps9936.setString(22, setupBean.getFileDate());
            ps9936.setString(23, setupBean.getP_FILE_NAME());
            ps9936.setString(24, setupBean.getFileDate());
            ps9936.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps9936.executeBatch();
            con.commit();
          } 
        } 
        ps9936.executeBatch();
      } 
      if (setupBean.getP_FILE_NAME().contains("FN100")) {
        while ((thisline = br.readLine()) != null) {
          if (!thisline.toUpperCase().trim().contains("HDRCBS") && !thisline.trim().contains("FTRCBS")) {
            count++;
            batchExecuted = false;
            sr_no = 1;
            String[] splitData = thisline.split("\\|", -1);
            for (int i = 1; i <= splitData.length; i++)
              ps.setString(sr_no++, splitData[i - 1]); 
            ps.setString(22, setupBean.getFileDate());
            ps.setString(23, setupBean.getP_FILE_NAME());
            ps.setString(24, setupBean.getFileDate());
            ps.addBatch();
          } 
          if (++batchCount % batchSize == 0) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps.executeBatch();
            con.commit();
          } 
        } 
        ps.executeBatch();
      } 
      con.commit();
      if (count > 0) {
        updatps.setString(1, setupBean.getP_FILE_NAME());
        updatps.setString(2, setupBean.getFileDate());
        updatps.setString(3, String.valueOf(count));
        updatps.setString(4, "CBS");
        updatps.execute();
        con.commit();
        System.out.println("Executed Batch Completedcount " + count);
      } 
      output.put("result", Boolean.valueOf(true));
      output.put("msg", "File Uploaded and Record count is " + count);
      return output;
    } catch (Exception e) {
      System.out.println("Exception in ReadUCOATMSwitchData " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Issue at Line Number " + count);
      return output;
    } 
  }
  
  public HashMap<String, Object> uploadUbiSWITCHData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    HashMap<String, Object> output = new HashMap<>();
    String thisline = null;
    int lineNumber = 0, feesize = 1;
    int count = 1;
    int sr_no = 1, batchNumber = 0, batchSize = 1000;
    boolean batchExecuted = false;
    String InsertQuery = " insert into   visa_switch_rawdata (REC_TYPE,AUTH_PPD,TERM_LN  ,  TERM_FIID  ,  TERM_TERM_ID  ,  CRD_LN  , CRD_FIID  ,CRD_PAN  ,CRD_MBR_NUM  , BRCH_ID  , REGN_ID  ,  USER_FLD1X  ,TYP_CDE  , TYP  , RTE_STAT  , ORIGINATOR  , RESPONDER  , ENTRY_TIME  , EXIT_TIME  , RE_ENTRY_TIME  , ACQ_ICHG_SETL_DAT  , ISS_ICHG_SETL_DAT  ,SEQ_NUM  ,TERM_TYP  ,TIM_OFST  ,ACQ_INST_ID_NUM  ,RCV_INST_ID_NUM  ,TRAN_CDE  ,FROM_ACCT  ,USER_FLD1  ,TO_ACCT  ,MULT_ACCT  ,AMT_1  ,AMT_2  ,AMT_3  ,DEP_BAL_CR  ,DEP_TYP  ,RESP_CDE  ,TERM_NAME_LOC  ,TERM_OWNER_NAME  ,TERM_CITY  ,TERM_ST  ,TERM_CNTRY  ,ORIG_OSEQ_NUM  , ORIG_OTRAN_DAT  ,  ORIG_OTRAN_TIM  , ORIG_B24_POST  , ORIG_CRNCY_CDE  , MULT_CRNCY_AUTH_CRNCY_CDE  ,MULT_CRNCY_AUTH_CONV_RATE  , MULT_CRNCY_SETL_CRNCY_CDE  , MULT_CRNCY_SETL_CONV_RATE  , MULT_CRNCY_CONV_DAT_TIM  ,RVSL_RSN  ,  PIN_OFST  ,  SHRG_GRP  , DEST_ORDER  ,  AUTH_ID_RESP  , REFR_IMP_IND  , REFR_AVAIL_IMP  ,  REFR_LEDG_IMP  ,  REFR_HLD_AMT_IMP  , REFR_CAF_REFR_IND  , REFR_USER_FLD3  ,  DEP_SETL_IMP_FLG  ,  ADJ_SETL_IMP_FLG  ,  REFR_IND  ,  USER_FLD4  , FRWD_INST_ID_NUM  ,  CRD_ACCPT_ID_NUM  ,  CRD_ISS_ID_NUM  , USER_FLD6  , DCRS_REMARKS,filename ,filedate ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String update_query = "INSERT INTO  switch_data_validation (FILENAME,FILEDATE,COUNT,FILETYPE) VALUES(?,?,?,?)";
    try {
      OracleConn orc = new OracleConn();
      Connection getconn = orc.getconn();
      getconn.setAutoCommit(false);
      int batchCount = 0;
      String originalFilename = file.getOriginalFilename();
      PreparedStatement ps = getconn.prepareStatement(InsertQuery);
      PreparedStatement updatps = getconn.prepareStatement(update_query);
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      while ((thisline = br.readLine()) != null) {
        if (!thisline.trim().equals("")) {
          count++;
          batchExecuted = false;
          sr_no = 1;
          String REC_TYPE = thisline.substring(33, 35).trim();
          String AUTH_PPD = thisline.substring(35, 39).trim();
          String TERM_LN = thisline.substring(39, 43).trim();
          String TERM_FIID = thisline.substring(43, 47).trim();
          String TERM_TERM_ID = thisline.substring(47, 63).trim();
          String CRD_LN = thisline.substring(63, 67).trim();
          String CRD_FIID = thisline.substring(67, 71).trim();
          String CRD_PAN = thisline.substring(71, 90).trim();
          String CRD_MBR_NUM = thisline.substring(90, 93).trim();
          String BRCH_ID = thisline.substring(93, 97).trim();
          String REGN_ID = thisline.substring(97, 101).trim();
          String USER_FLD1X = thisline.substring(101, 103).trim();
          String TYP_CDE = thisline.substring(103, 105).trim();
          String TYP = thisline.substring(105, 109).trim();
          String RTE_STAT = thisline.substring(109, 111).trim();
          String ORIGINATOR = thisline.substring(111, 112).trim();
          String RESPONDER = thisline.substring(112, 113).trim();
          String ENTRY_TIME = thisline.substring(113, 132).trim();
          String EXIT_TIME = thisline.substring(132, 151).trim();
          String RE_ENTRY_TIME = thisline.substring(151, 170).trim();
          String ACQ_ICHG_SETL_DAT = thisline.substring(190, 196).trim();
          String ISS_ICHG_SETL_DAT = thisline.substring(196, 202).trim();
          String SEQ_NUM = thisline.substring(202, 214).trim();
          String TERM_TYP = thisline.substring(214, 216).trim();
          String TIM_OFST = thisline.substring(216, 221).trim();
          String ACQ_INST_ID_NUM = thisline.substring(221, 232).trim();
          String RCV_INST_ID_NUM = thisline.substring(232, 243).trim();
          String TRAN_CDE = thisline.substring(243, 249).trim();
          String FROM_ACCT = thisline.substring(249, 268).trim();
          String USER_FLD1 = thisline.substring(268, 269).trim();
          String TO_ACCT = thisline.substring(269, 288).trim();
          String MULT_ACCT = thisline.substring(288, 289).trim();
          String AMT_1 = thisline.substring(289, 308).trim();
          String AMT_2 = thisline.substring(308, 327).trim();
          String AMT_3 = thisline.substring(327, 346).trim();
          String DEP_BAL_CR = thisline.substring(346, 356).trim();
          String DEP_TYP = thisline.substring(356, 357).trim();
          String RESP_CDE = thisline.substring(357, 360).trim();
          String TERM_NAME_LOC = thisline.substring(360, 385).trim();
          String TERM_OWNER_NAME = thisline.substring(385, 407).trim();
          String TERM_CITY = thisline.substring(407, 420).trim();
          String TERM_ST = thisline.substring(420, 423).trim();
          String TERM_CNTRY = thisline.substring(423, 425).trim();
          String ORIG_OSEQ_NUM = thisline.substring(425, 437).trim();
          String ORIG_OTRAN_DAT = thisline.substring(425, 437).trim();
          String ORIG_OTRAN_TIM = thisline.substring(441, 449).trim();
          String ORIG_B24_POST = thisline.substring(449, 453).trim();
          String ORIG_CRNCY_CDE = thisline.substring(453, 456).trim();
          String MULT_CRNCY_AUTH_CRNCY_CDE = thisline.substring(456, 459).trim();
          String MULT_CRNCY_AUTH_CONV_RATE = thisline.substring(459, 467).trim();
          String MULT_CRNCY_SETL_CRNCY_CDE = thisline.substring(467, 470).trim();
          String MULT_CRNCY_SETL_CONV_RATE = thisline.substring(470, 478).trim();
          String MULT_CRNCY_CONV_DAT_TIM = thisline.substring(478, 497).trim();
          String RVSL_RSN = thisline.substring(497, 499).trim();
          String PIN_OFST = thisline.substring(499, 515).trim();
          String SHRG_GRP = thisline.substring(515, 516).trim();
          String DEST_ORDER = thisline.substring(516, 517).trim();
          String AUTH_ID_RESP = thisline.substring(517, 533).trim();
          String REFR_IMP_IND = thisline.substring(523, 524).trim();
          String REFR_AVAIL_IMP = thisline.substring(524, 526).trim();
          String REFR_LEDG_IMP = thisline.substring(526, 528).trim();
          String REFR_HLD_AMT_IMP = thisline.substring(528, 530).trim();
          String REFR_CAF_REFR_IND = thisline.substring(530, 531).trim();
          String REFR_USER_FLD3 = thisline.substring(531, 532).trim();
          String DEP_SETL_IMP_FLG = thisline.substring(532, 533).trim();
          String ADJ_SETL_IMP_FLG = thisline.substring(533, 534).trim();
          String REFR_IND = thisline.substring(534, 538).trim();
          String USER_FLD4 = thisline.substring(538, 554).trim();
          String FRWD_INST_ID_NUM = thisline.substring(554, 565).trim();
          String CRD_ACCPT_ID_NUM = thisline.substring(565, 576).trim();
          String CRD_ISS_ID_NUM = thisline.substring(576, 587).trim();
          String USER_FLD6 = thisline.substring(587, 588).trim();
          String DCRS_REMARKS = "NA";
          ps.setString(1, REC_TYPE);
          ps.setString(2, AUTH_PPD);
          ps.setString(3, TERM_LN);
          ps.setString(4, TERM_FIID);
          ps.setString(5, TERM_TERM_ID);
          ps.setString(6, CRD_LN);
          ps.setString(7, CRD_FIID);
          ps.setString(8, CRD_PAN);
          ps.setString(9, CRD_MBR_NUM);
          ps.setString(10, BRCH_ID);
          ps.setString(11, REGN_ID);
          ps.setString(12, USER_FLD1X);
          ps.setString(13, TYP_CDE);
          ps.setString(14, TYP);
          ps.setString(15, RTE_STAT);
          ps.setString(16, ORIGINATOR);
          ps.setString(17, RESPONDER);
          ps.setString(18, ENTRY_TIME);
          ps.setString(19, EXIT_TIME);
          ps.setString(20, RE_ENTRY_TIME);
          ps.setString(21, ACQ_ICHG_SETL_DAT);
          ps.setString(22, ISS_ICHG_SETL_DAT);
          ps.setString(23, SEQ_NUM);
          ps.setString(24, TERM_TYP);
          ps.setString(25, TIM_OFST);
          ps.setString(26, ACQ_INST_ID_NUM);
          ps.setString(27, RCV_INST_ID_NUM);
          ps.setString(28, TRAN_CDE);
          ps.setString(29, FROM_ACCT);
          ps.setString(30, USER_FLD1);
          ps.setString(31, TO_ACCT);
          ps.setString(32, MULT_ACCT);
          ps.setString(33, AMT_1);
          ps.setString(34, AMT_2);
          ps.setString(35, AMT_3);
          ps.setString(36, DEP_BAL_CR);
          ps.setString(37, DEP_TYP);
          ps.setString(38, RESP_CDE);
          ps.setString(39, TERM_NAME_LOC);
          ps.setString(40, TERM_OWNER_NAME);
          ps.setString(41, TERM_CITY);
          ps.setString(42, TERM_ST);
          ps.setString(43, TERM_CNTRY);
          ps.setString(44, ORIG_OSEQ_NUM);
          ps.setString(45, ORIG_OTRAN_DAT);
          ps.setString(46, ORIG_OTRAN_TIM);
          ps.setString(47, ORIG_B24_POST);
          ps.setString(48, ORIG_CRNCY_CDE);
          ps.setString(49, MULT_CRNCY_AUTH_CRNCY_CDE);
          ps.setString(50, MULT_CRNCY_AUTH_CONV_RATE);
          ps.setString(51, MULT_CRNCY_SETL_CRNCY_CDE);
          ps.setString(52, MULT_CRNCY_SETL_CONV_RATE);
          ps.setString(53, MULT_CRNCY_CONV_DAT_TIM);
          ps.setString(54, RVSL_RSN);
          ps.setString(55, PIN_OFST);
          ps.setString(56, SHRG_GRP);
          ps.setString(57, DEST_ORDER);
          ps.setString(58, AUTH_ID_RESP);
          ps.setString(59, REFR_IMP_IND);
          ps.setString(60, REFR_AVAIL_IMP);
          ps.setString(61, REFR_LEDG_IMP);
          ps.setString(62, REFR_HLD_AMT_IMP);
          ps.setString(63, REFR_CAF_REFR_IND);
          ps.setString(64, REFR_USER_FLD3);
          ps.setString(65, DEP_SETL_IMP_FLG);
          ps.setString(66, ADJ_SETL_IMP_FLG);
          ps.setString(67, REFR_IND);
          ps.setString(68, USER_FLD4);
          ps.setString(69, FRWD_INST_ID_NUM);
          ps.setString(70, CRD_ACCPT_ID_NUM);
          ps.setString(71, CRD_ISS_ID_NUM);
          ps.setString(72, USER_FLD6);
          ps.setString(73, DCRS_REMARKS);
          ps.setString(74, originalFilename);
          ps.setString(75, setupBean.getFileDate());
          ps.addBatch();
        } 
        if (++batchCount % batchSize == 0) {
          batchNumber++;
          System.out.println("Batch Executed is " + batchNumber);
          ps.executeBatch();
          getconn.commit();
        } 
      } 
      ps.executeBatch();
      getconn.commit();
      if (count > 0) {
        updatps.setString(1, setupBean.getP_FILE_NAME());
        updatps.setString(2, setupBean.getFileDate());
        updatps.setString(3, String.valueOf(count));
        updatps.setString(4, "VISA");
        updatps.execute();
        getconn.commit();
        System.out.println("Executed Batch Completedcount " + lineNumber);
      } 
      output.put("result", Boolean.valueOf(true));
      output.put("msg", "File Uploaded and Record count is " + lineNumber);
      return output;
    } catch (Exception e) {
      System.out.println("Exception in ReadUCOATMSwitchData " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Issue at Line Number " + lineNumber);
      return output;
    } 
  }
  
  public boolean uploadPBGBData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    System.out.println("uploadPBGBData method called ");
    String stLine = null;
    int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0;
    boolean batchExecuted = false;
    String InsertQuery = "INSERT INTO  UBI_CBS_RAWDATA (BUSINESS_DATE,CARD_NO,TRACE_NO,AC_NO,TRAN_DATE,ATM_ID,TYPE,AMOUNT,FEE,BR_CODE,ISS_SOL_ID,MCC,REMARKS,NETWORK,POSTED_DATE,GL_ACCOUNT,TR_NO,DCRS_REMARKS,FILEDATE,FILENAME,CREATEDDATE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'),?,to_date(?,'dd/mm/yyyy'))";
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      PreparedStatement ps = con.prepareStatement(InsertQuery);
      while ((stLine = br.readLine()) != null) {
        if (!stLine.toUpperCase().trim().contains("BUSINESS") && !stLine.trim().equals("") && 
          !stLine.trim().equals("H0000")) {
          lineNumber++;
          batchExecuted = false;
          sr_no = 1;
          String[] splitData = stLine.split("\\|");
          for (int i = 1; i <= splitData.length; i++)
            ps.setString(sr_no++, splitData[i - 1]); 
          ps.setString(18, "");
          ps.setString(19, setupBean.getFileDate());
          ps.setString(20, setupBean.getP_FILE_NAME());
          ps.setString(21, setupBean.getFileDate());
          ps.addBatch();
          batchSize++;
          if (batchSize == 20000) {
            batchNumber++;
            System.out.println("Batch Executed is " + batchNumber);
            ps.executeBatch();
            batchSize = 0;
            batchExecuted = true;
          } 
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
      System.out.println("Exception in uploadISSData " + e);
      return false;
    } 
  }
  
  public boolean iccwuploadcbsData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    String stLine = null;
    int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0;
    boolean batchExecuted = false;
    String InsertQuery = " INSERT INTO ICCW_CBS_DATA_CUB (JOURNAL_NO, FROMACC, FROMACCNAME, FROMACCBR, TOACC, TOACCNAME, TOACCBR, TXNDATE, TXNAMOUNT, ATT1, ATT2, ATT3, ACC_TYPE, CREATED_BY , FILEDATE) VALUES  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'))";
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      PreparedStatement ps = con.prepareStatement(InsertQuery);
      while ((stLine = br.readLine()) != null) {
        lineNumber++;
        batchExecuted = false;
        sr_no = 1;
        String[] splitData = stLine.split("\\|");
        if (stLine.startsWith("JOURNAL_NO") || stLine.isEmpty() || stLine.startsWith("Elapsed:") || 
          stLine.startsWith("FT"))
          continue; 
        for (int i = 0; i <= splitData.length - 1; i++)
          ps.setString(sr_no++, splitData[i]); 
        ps.setString(sr_no++, setupBean.getCreatedBy());
        ps.setString(sr_no++, setupBean.getFileDate());
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
  
  public boolean iccwuploadRevcbsData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    String stLine = null;
    int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0;
    boolean batchExecuted = false;
    String InsertQuery = " INSERT INTO ICCW_CBS_DATA_CUB_REV (JOURNAL_NO, REV_JOURNAL, FROMACC, TOACC ,TXNAMOUNT, TXNDATE,  ACC_TYPE, CREATED_BY , FILEDATE) VALUES  (?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'))";
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      PreparedStatement ps = con.prepareStatement(InsertQuery);
      while ((stLine = br.readLine()) != null) {
        lineNumber++;
        batchExecuted = false;
        sr_no = 1;
        String[] splitData = stLine.split("\\|");
        if (stLine.startsWith("JOURNAL_NO") || stLine.isEmpty() || stLine.startsWith("Elapsed:") || 
          stLine.startsWith("FT"))
          continue; 
        for (int i = 0; i <= splitData.length - 1; i++)
          ps.setString(sr_no++, splitData[i]); 
        ps.setString(sr_no++, setupBean.getCreatedBy());
        ps.setString(sr_no++, setupBean.getFileDate());
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
  
  public boolean uploadSwitchData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    String stLine = null;
    int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0;
    boolean batchExecuted = false;
    String InsertQuery = "INSERT INTO CBS_RUPAY_RAWDATA(REMARKS, AMOUNT, TRAN_DATE, TRAN_TIME, E, PARTICULARALS, REF_NO, TRAN_ID, CREATEDBY, FILEDATE) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'))";
    String delQuery = "delete from CBS_UCO_RAWDATA_TEMP";
    try {
      PreparedStatement delpst = con.prepareStatement(delQuery);
      delpst.execute();
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      PreparedStatement ps = con.prepareStatement(InsertQuery);
      while ((stLine = br.readLine()) != null) {
        if (stLine.startsWith("999  ,H") || stLine.startsWith("999  ,F"))
          continue; 
        lineNumber++;
        batchExecuted = false;
        sr_no = 1;
        String[] splitData = stLine.split("\\,");
        for (int i = 0; i <= splitData.length - 1; i++) {
          if (i != 0 && i != 1 && i != 3 && i != 8 && i != 7 && i != 11 && i != 13 && i != 14 && i != 15)
            ps.setString(sr_no++, splitData[i].trim()); 
        } 
        ps.setString(sr_no++, setupBean.getCreatedBy());
        ps.setString(sr_no++, setupBean.getFileDate());
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
