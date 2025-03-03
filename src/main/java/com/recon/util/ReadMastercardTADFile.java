package com.recon.util;


import com.recon.model.MastercardUploadBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.springframework.web.multipart.MultipartFile;

public class ReadMastercardTADFile {
  public HashMap<String, Object> readTADFile(MultipartFile file, Connection conn, MastercardUploadBean beanObj) {
    int lineNumber = 0, count = 0;
    String line = null, sett_date = null, file_type = null;
    boolean filerecords = false, fileAmount = false;
    HashMap<Integer, String> fileIds = new HashMap<>();
    HashMap<Integer, String> fileAmts = new HashMap<>();
    HashMap<Integer, String> fileAction = new HashMap<>();
    HashMap<Integer, String> reconDate = new HashMap<>();
    HashMap<String, Object> output = new HashMap<>();
    boolean alreadyUploaded = false;
    boolean netAmount = false;
    String insertQuery = "INSERT INTO MASTERCARD_TAD_DATA (SR_NO,SETTLEMENT_DATE,RECON_DATE,FILE_ID,AMOUNT,PART_TRAN_TYPE,FILE_TYPE,CREATEDBY,FILEDATE) VALUES(?,TO_DATE(?,'DD/MM/YYYY'),TO_DATE(?,'DD/MM/YYYY')-1,?,?,?,?,?,TO_DATE(?,'DD/MM/YYYY'))";
    String fileName = file.getOriginalFilename();
    String[] names = fileName.split("\\.");
    String file_date = names[0].substring(names[0].length() - 10);
    try {
      DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
      Date date = df1.parse(file_date);
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      int dayOfWeek = cal.get(7);
      System.out.println(cal.get(7));
      PreparedStatement pst = conn.prepareStatement(insertQuery);
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      while ((line = br.readLine()) != null) {
        lineNumber++;
        if (line.contains(":")) {
          String[] values = line.split(":");
          if (values[0].equalsIgnoreCase("SETTLEMENT SERVICE ID")) {
            file_type = values[1].trim().substring(0, 2);
            System.out.println("file type is " + file_type);
            beanObj.setFileType(file_type);
            continue;
          } 
          if (values[0].trim().equalsIgnoreCase("MEMBER TOTALS IN PAYMENT CURRENCY 356"))
            netAmount = true; 
          continue;
        } 
        if (!line.trim().equalsIgnoreCase("")) {
          String[] values = line.trim().split("\\s");
          if (values[0].equalsIgnoreCase("SOURCE")) {
            filerecords = true;
            continue;
          } 
          if (values[0].equalsIgnoreCase("Payment")) {
            fileAmount = true;
            continue;
          } 
          if (filerecords && !values[0].equalsIgnoreCase("NO")) {
            fileIds.put(Integer.valueOf(Integer.parseInt(values[0])), values[values.length - 1]);
            reconDate.put(Integer.valueOf(Integer.parseInt(values[0])), String.valueOf(values[2]) + "/" + values[3] + "/" + values[4]);
            if (Integer.parseInt(values[0]) == 1) {
              sett_date = reconDate.get(Integer.valueOf(1));
              beanObj.setFileDate(sett_date);
              alreadyUploaded = checkFileUpload(beanObj, conn);
              if (alreadyUploaded) {
                output.put("result", Boolean.valueOf(false));
                output.put("msg", "File is already Uploaded");
                return output;
              } 
            } 
          } else {
            filerecords = false;
          } 
          if (fileAmount && fileIds.size() != fileAction.size()) {
            fileAction.put(Integer.valueOf(Integer.parseInt(values[0])), values[values.length - 1]);
            fileAmts.put(Integer.valueOf(Integer.parseInt(values[0])), values[values.length - 2]);
          } else {
            fileAmount = false;
          } 
          if (netAmount) {
            System.out.println("Net amount is " + line.substring(24, 44));
            System.out.println("Net credit/debit is " + line.substring(44, 45));
            System.out.println("Net Fee is " + line.substring(43, 66));
            System.out.println("Fee credit/debit is " + line.substring(66, 67));
            netAmount = false;
            pst.setInt(1, 0);
            pst.setString(2, sett_date);
            pst.setString(3, null);
            pst.setString(4, "TOTAL");
            pst.setString(5, values[values.length - 2]);
            pst.setString(6, values[values.length - 1]);
            pst.setString(7, file_type);
            pst.setString(8, beanObj.getCreatedBy());
            pst.setString(9, sett_date);
            pst.execute();
          } 
        } 
      } 
      System.out.println(fileIds);
      System.out.println(fileAmts);
      System.out.println(fileAction);
      System.out.println(reconDate);
      for (int i = 1; i <= fileIds.size(); i++) {
        pst.setInt(1, i);
        pst.setString(2, sett_date);
        pst.setString(3, reconDate.get(Integer.valueOf(i)));
        pst.setString(4, fileIds.get(Integer.valueOf(i)));
        pst.setString(5, fileAmts.get(Integer.valueOf(i)));
        pst.setString(6, fileAction.get(Integer.valueOf(i)));
        pst.setString(7, file_type);
        pst.setString(8, beanObj.getCreatedBy());
        pst.setString(9, sett_date);
        pst.execute();
      } 
      System.out.println("Completed Reading file");
      output.put("result", Boolean.valueOf(true));
      output.put("msg", "File is uploaded \\n Count is " + fileIds.size());
    } catch (Exception e) {
      System.out.println("Exception in ReadMastercardTADFile " + e);
      System.out.println("Exception at Line Number " + lineNumber);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception at Line number " + lineNumber);
    } 
    return output;
  }
  
  public HashMap<String, Object> readTAFile(MultipartFile file, Connection conn, MastercardUploadBean beanObj) {
    int lineNumber = 0, count = 0;
    String line = null, sett_date = null, file_type = null;
    boolean filerecords = false, fileAmount = false;
    HashMap<Integer, String> fileIds = new HashMap<>();
    HashMap<Integer, String> fileAmts = new HashMap<>();
    HashMap<Integer, String> fileAction = new HashMap<>();
    HashMap<Integer, String> reconDate = new HashMap<>();
    HashMap<String, Object> output = new HashMap<>();
    boolean alreadyUploaded = false;
    boolean netAmount = false;
    String insertQuery = "INSERT INTO MASTERCARD_TA_DATA (SR_NO,SETTLEMENT_DATE,RECON_DATE,FILE_ID,AMOUNT,PART_TRAN_TYPE,FILE_TYPE,CREATEDBY,FILEDATE) VALUES(?,TO_DATE(?,'DD/MM/YYYY'),TO_DATE(?,'DD/MM/YYYY')-1,?,?,?,?,?,TO_DATE(?,'DD/MM/YYYY'))";
    String fileName = file.getOriginalFilename();
    String[] names = fileName.split("\\.");
    String file_date = names[0].substring(names[0].length() - 10);
    try {
      DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
      Date date = df1.parse(file_date);
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      int dayOfWeek = cal.get(7);
      System.out.println(cal.get(7));
      PreparedStatement pst = conn.prepareStatement(insertQuery);
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      while ((line = br.readLine()) != null) {
        lineNumber++;
        if (line.contains(":")) {
          String[] values = line.split(":");
          if (values[0].equalsIgnoreCase("SETTLEMENT SERVICE ID")) {
            file_type = values[1].trim().substring(0, 2);
            System.out.println("file type is " + file_type);
            beanObj.setFileType(file_type);
            continue;
          } 
          if (values[0].trim().equalsIgnoreCase("MEMBER TOTALS IN PAYMENT CURRENCY 356"))
            netAmount = true; 
          continue;
        } 
        if (!line.trim().equalsIgnoreCase("")) {
          String[] values = line.trim().split("\\s");
          if (values[0].equalsIgnoreCase("SOURCE")) {
            filerecords = true;
            continue;
          } 
          if (values[0].equalsIgnoreCase("Payment")) {
            fileAmount = true;
            continue;
          } 
          if (filerecords && !values[0].equalsIgnoreCase("NO")) {
            fileIds.put(Integer.valueOf(Integer.parseInt(values[0])), values[values.length - 1]);
            reconDate.put(Integer.valueOf(Integer.parseInt(values[0])), String.valueOf(values[2]) + "/" + values[3] + "/" + values[4]);
            if (Integer.parseInt(values[0]) == 1) {
              sett_date = reconDate.get(Integer.valueOf(1));
              beanObj.setFileDate(sett_date);
              alreadyUploaded = checkFileUpload(beanObj, conn);
              if (alreadyUploaded) {
                output.put("result", Boolean.valueOf(false));
                output.put("msg", "File is already Uploaded");
                return output;
              } 
            } 
          } else {
            filerecords = false;
          } 
          if (fileAmount && fileIds.size() != fileAction.size()) {
            fileAction.put(Integer.valueOf(Integer.parseInt(values[0])), values[values.length - 1]);
            fileAmts.put(Integer.valueOf(Integer.parseInt(values[0])), values[values.length - 2]);
          } else {
            fileAmount = false;
          } 
          if (netAmount) {
            System.out.println("Net amount is " + line.substring(24, 44));
            System.out.println("Net credit/debit is " + line.substring(44, 45));
            System.out.println("Net Fee is " + line.substring(43, 66));
            System.out.println("Fee credit/debit is " + line.substring(66, 67));
            netAmount = false;
            pst.setInt(1, 0);
            pst.setString(2, sett_date);
            pst.setString(3, null);
            pst.setString(4, "TOTAL");
            pst.setString(5, values[values.length - 2]);
            pst.setString(6, values[values.length - 1]);
            pst.setString(7, file_type);
            pst.setString(8, beanObj.getCreatedBy());
            pst.setString(9, sett_date);
            pst.execute();
          } 
        } 
      } 
      System.out.println(fileIds);
      System.out.println(fileAmts);
      System.out.println(fileAction);
      System.out.println(reconDate);
      for (int i = 1; i <= fileIds.size(); i++) {
        pst.setInt(1, i);
        pst.setString(2, sett_date);
        pst.setString(3, reconDate.get(Integer.valueOf(i)));
        pst.setString(4, fileIds.get(Integer.valueOf(i)));
        pst.setString(5, fileAmts.get(Integer.valueOf(i)));
        pst.setString(6, fileAction.get(Integer.valueOf(i)));
        pst.setString(7, file_type);
        pst.setString(8, beanObj.getCreatedBy());
        pst.setString(9, sett_date);
        pst.execute();
      } 
      System.out.println("Completed Reading file");
      output.put("result", Boolean.valueOf(true));
      output.put("msg", "File is uploaded \\n Count is " + fileIds.size());
    } catch (Exception e) {
      System.out.println("Exception in ReadMastercardTADFile " + e);
      System.out.println("Exception at Line Number " + lineNumber);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception at Line number " + lineNumber);
    } 
    return output;
  }
  
  public boolean checkFileUpload(MastercardUploadBean beanObj, Connection conn) {
    try {
      String uploadCount = null;
      String checkUpload = "SELECT COUNT(*) as count FROM MASTERCARD_TAD_DATA where filedate = TO_DATE('" + beanObj.getFileDate() + "','DD/MM/YYYY') and file_type = '" + beanObj.getFileType() + "'";
      System.out.println("checkUpload " + checkUpload);
      PreparedStatement pst = conn.prepareStatement(checkUpload);
      ResultSet rs = pst.executeQuery();
      while (rs.next())
        uploadCount = rs.getString("count"); 
      if (Integer.parseInt(uploadCount) > 0)
        return true; 
      return false;
    } catch (Exception e) {
      System.out.println("Exception in MastercardServiceImpl " + e);
      return true;
    } 
  }
}
