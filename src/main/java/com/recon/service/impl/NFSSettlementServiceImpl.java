package com.recon.service.impl;


import com.recon.model.AddNewSolBean;
import com.recon.model.CompareSetupBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.RupayUploadBean;
import com.recon.service.NFSSettlementService;
import com.recon.util.ReadDFSJCBMonthlyNTSLFile;
import com.recon.util.ReadDFSRawFile;
import com.recon.util.ReadNFSMonthlyNTSLFile;
import com.recon.util.ReadNFSNTSLFile;
import com.recon.util.ReadNUploadSUSPECTFiles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.web.multipart.MultipartFile;

public class NFSSettlementServiceImpl extends JdbcDaoSupport implements NFSSettlementService {
  private int procCount;
  
  public HashMap<String, Object> validatePrevFileUpload(NFSSettlementBean beanObj) {
    HashMap<String, Object> validate = new HashMap<>();
    int file_id = 0, totalCount = 0;
    String checkTotalCount = "";
    try {
      try {
        if (beanObj.getCategory().contains("ICD")) {
          file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
              new Object[] { "ICD", "ICD-SETTLEMENT", "-" }, Integer.class)).intValue();
        } else if (beanObj.getFileName().contains("NTSL-DFS")) {
          file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
              new Object[] { "NTSL-DFS", "DFS_SETTLEMENT", "-" }, Integer.class)).intValue();
        } else if (beanObj.getFileName().contains("NTSL-JCB")) {
          file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
              new Object[] { "NTSL-JCB", "JCB_SETTLEMENT", "-" }, Integer.class)).intValue();
        } else {
          file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
              new Object[] { "NTSL-NFS", "NFS_SETTLEMENT", "-" }, Integer.class)).intValue();
        } 
        System.out.println("File id is " + file_id + beanObj.getCycle() + " " + beanObj.getFileName() + " " + 
            beanObj.getCategory());
      } catch (Exception e) {
        validate.put("result", Boolean.valueOf(true));
        validate.put("msg", "File is not Configured!!!");
        return validate;
      } 
      if (beanObj.getCategory().contains("ICD")) {
        checkTotalCount = "SELECT COUNT(*) FROM main_settlement_file_upload WHERE CATEGORY = ? AND FILE_SUBCATEGORY = ? AND FILEID = ?";
        totalCount = ((Integer)getJdbcTemplate().queryForObject(checkTotalCount, 
            new Object[] { "ICD-SETTLEMENT", "-", Integer.valueOf(file_id) }, Integer.class)).intValue();
      } else if (beanObj.getFileName().contains("NTSL-DFS")) {
        checkTotalCount = "SELECT COUNT(*) FROM main_settlement_file_upload WHERE CATEGORY = ? AND FILE_SUBCATEGORY = ? AND FILEID = ?";
        totalCount = ((Integer)getJdbcTemplate().queryForObject(checkTotalCount, 
            new Object[] { "DFS_SETTLEMENT", "-", Integer.valueOf(file_id) }, Integer.class)).intValue();
      } else if (beanObj.getFileName().contains("NTSL-JCB")) {
        checkTotalCount = "SELECT COUNT(*) FROM main_settlement_file_upload WHERE CATEGORY = ? AND FILE_SUBCATEGORY = ? AND FILEID = ?";
        totalCount = ((Integer)getJdbcTemplate().queryForObject(checkTotalCount, 
            new Object[] { "JCB_SETTLEMENT", "-", Integer.valueOf(file_id) }, Integer.class)).intValue();
      } else {
        checkTotalCount = "SELECT COUNT(*) FROM main_settlement_file_upload WHERE CATEGORY = ? AND FILE_SUBCATEGORY = ? AND FILEID = ?";
        totalCount = ((Integer)getJdbcTemplate().queryForObject(checkTotalCount, 
            new Object[] { "NFS_SETTLEMENT", "-", Integer.valueOf(file_id) }, Integer.class)).intValue();
      } 
      System.out.println("checkTotalCount " + checkTotalCount + "  " + totalCount);
      if (totalCount > 0) {
        if (beanObj.getCycle() != 0 && beanObj.getCategory().contains("NFS")) {
          String checkForSameDate = "SELECT COUNT(*) FROM ntsl_nfs_rawdata WHERE FILEDATE =STR_to_date(?,'%Y/%m/%d') and CYCLE='" + 
            beanObj.getCycle() + "'";
          int dataCount = ((Integer)getJdbcTemplate().queryForObject(checkForSameDate, 
              new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
          System.out.println("checkTotalCountntsl_nfs_rawdata " + checkForSameDate + "  " + dataCount);
          if (dataCount > 0) {
            validate.put("result", Boolean.valueOf(true));
            validate.put("msg", "File is already uploaded!!!");
          } else {
            validate.put("result", Boolean.valueOf(false));
          } 
        } else if (beanObj.getCategory().contains("ICD")) {
          String checkForSameDate = "SELECT COUNT(*) FROM ntsl_icd_rawdata WHERE  FILEDATE =STR_to_date(?,'%Y/%m/%d') ";
          int dataCount = ((Integer)getJdbcTemplate().queryForObject(checkForSameDate, 
              
              new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
          System.out.println("checkTotalCountntsl_icd_rawdata " + checkForSameDate + "  " + dataCount);
          if (dataCount > 0) {
            validate.put("result", Boolean.valueOf(true));
            validate.put("msg", "File is already uploaded!!!");
          } else {
            validate.put("result", Boolean.valueOf(false));
          } 
        } else if (beanObj.getFileName().contains("NTSL-DFS")) {
          String checkForSameDate = "SELECT COUNT(*) FROM ntsl_dfs_rawdata WHERE  FILEDATE =STR_to_date(?,'%Y/%m/%d') ";
          int dataCount = ((Integer)getJdbcTemplate().queryForObject(checkForSameDate, 
              new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
          System.out.println("checkTotalCountntsl_DFS_rawdata " + checkForSameDate + "  " + dataCount);
          if (dataCount > 0) {
            validate.put("result", Boolean.valueOf(true));
            validate.put("msg", "File is already uploaded!!!");
          } else {
            validate.put("result", Boolean.valueOf(false));
          } 
        } else if (beanObj.getFileName().contains("NTSL-JCB")) {
          String checkForSameDate = "SELECT COUNT(*) FROM ntsl_jcb_rawdata WHERE FILEDATE =  STR_to_date(?,'%Y/%m/%d')";
          int dataCount = ((Integer)getJdbcTemplate().queryForObject(checkForSameDate, 
              new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
          if (dataCount > 0) {
            validate.put("result", Boolean.valueOf(true));
            validate.put("msg", "File is already uploaded!!!");
          } else {
            validate.put("result", Boolean.valueOf(false));
          } 
        } else {
          String checkForSameDate = "SELECT COUNT(*) FROM main_file_upload_dtls WHERE FILEID = ? AND FILEDATE =  STR_to_date(?,'%Y/%m/%d')";
          int dataCount = ((Integer)getJdbcTemplate().queryForObject(checkForSameDate, 
              new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker() }, Integer.class)).intValue();
          System.out.println("checkTotalCountntsl_nfs_rawdata " + checkForSameDate + "  " + dataCount);
          if (dataCount > 0) {
            validate.put("result", Boolean.valueOf(true));
            validate.put("msg", "File is already uploaded!!");
          } else {
            validate.put("result", Boolean.valueOf(false));
          } 
        } 
      } else {
        System.out.println("Its first time upload");
        validate.put("result", Boolean.valueOf(false));
      } 
    } catch (Exception e) {
      System.out.println("Exception in NFSSettlementServiceImpl: validatePrevFileUpload " + e);
      validate.put("result", Boolean.valueOf(true));
      validate.put("msg", "Exception Occured!!");
    } 
    return validate;
  }
  
  public HashMap<String, Object> validatePrevFileUploadSUS(NFSSettlementBean beanObj) {
    HashMap<String, Object> validate = new HashMap<>();
    int file_id = 0, totalCount = 0;
    String checkTotalCount = "";
    try {
      checkTotalCount = "SELECT COUNT(*) FROM SUSPECT_RAWDATA WHERE date_format(FILEDATE,'%d%m%y') = date_format('" + beanObj.getDatepicker() + "','%d%m%y')";
      totalCount = ((Integer)getJdbcTemplate().queryForObject(checkTotalCount, 
          new Object[0], Integer.class)).intValue();
      System.out.println("validatePrevFileUploadSUS " + checkTotalCount + totalCount);
      if (totalCount > 0) {
        validate.put("result", Boolean.valueOf(true));
        validate.put("msg", "File is already uploaded!!!");
      } else {
        validate.put("result", Boolean.valueOf(false));
      } 
    } catch (Exception e) {
      System.out.println("Exception in NFSSettlementServiceImpl: validatePrevFileUpload " + e);
      validate.put("result", Boolean.valueOf(true));
      validate.put("msg", "Exception Occured!!");
    } 
    return validate;
  }
  
  public HashMap<String, Object> uploadDFSRawData(NFSSettlementBean beanObj, MultipartFile file) {
    HashMap<String, Object> mapObj = new HashMap<>();
    try {
      ReadDFSRawFile nfsRawData = new ReadDFSRawFile();
      mapObj = nfsRawData.readData(beanObj, getConnection(), file);
      System.out.println("result is " + mapObj);
      boolean result = ((Boolean)mapObj.get("result")).booleanValue();
      int count = ((Integer)mapObj.get("count")).intValue();
      if (result) {
        int file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getFileName(), beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
        System.out.println("File id is " + file_id);
        String insertData = "INSERT INTO main_settlement_file_upload(FILEID, FILEDATE, UPLOADBY, UPLOADDATE, CATEGORY, UPLOAD_FLAG, FILE_SUBCATEGORY,CYCLE,SETTLEMENT_FLAG,INTERCHANGE_FLAG,FILE_COUNT) VALUES('" + 
          file_id + "',date_format('" + beanObj.getDatepicker() + "','%d/%m/%Y'),'" + 
          beanObj.getCreatedBy() + "',sysdate(),'" + beanObj.getCategory() + "','Y','" + 
          beanObj.getStSubCategory() + "'," + "'1','N','N','1')";
        getJdbcTemplate().execute(insertData);
        mapObj.put("entry", Boolean.valueOf(true));
      } 
      return mapObj;
    } catch (Exception e) {
      System.out.println("Exception is " + e);
      mapObj.put("result", Boolean.valueOf(false));
      mapObj.put("count", Integer.valueOf(0));
      return mapObj;
    } 
  }
  
  public HashMap<String, Object> uploadSUSPECTFile(NFSSettlementBean beanObj, MultipartFile file) {
    HashMap<String, Object> mapObj = new HashMap<>();
    int file_id = 0;
    boolean getresult = false;
    try {
      System.out.println("uploadSUSPECTFile");
      ReadNUploadSUSPECTFiles cbsFile = new ReadNUploadSUSPECTFiles();
      mapObj = cbsFile.uploadSUSPECTData(beanObj, getConnection(), file);
      getresult = ((Boolean)mapObj.get("result")).booleanValue();
      System.out.println("result is " + mapObj + " filename " + getresult);
      boolean result = ((Boolean)mapObj.get("result")).booleanValue();
      return mapObj;
    } catch (Exception e) {
      this.logger.info("Exception in uploadNTSLFile " + e);
      mapObj.put("result", Boolean.valueOf(false));
      mapObj.put("count", Integer.valueOf(0));
      return mapObj;
    } 
  }
  
  public HashMap<String, Object> uploadNTSLFile(NFSSettlementBean beanObj, MultipartFile file) {
    HashMap<String, Object> mapObj = new HashMap<>();
    int file_id = 0;
    try {
      if (beanObj.getFileName().contains("NFS")) {
        ReadNFSNTSLFile readObj = new ReadNFSNTSLFile();
        mapObj = readObj.fileupload1(beanObj, file, getConnection());
      } else if (beanObj.getFileName().contains("NTSL-DFS")) {
        ReadNFSNTSLFile readObj = new ReadNFSNTSLFile();
        mapObj = readObj.fileuploadDFS(beanObj, file, getConnection());
      } else if (beanObj.getFileName().contains("NTSL-JCB")) {
        ReadNFSNTSLFile readObj = new ReadNFSNTSLFile();
        mapObj = readObj.fileuploadJCB(beanObj, file, getConnection());
      } else if (beanObj.getFileName().contains("ICD")) {
        ReadNFSNTSLFile readObj = new ReadNFSNTSLFile();
        mapObj = readObj.fileuploadICD(beanObj, file, getConnection());
      } else {
        ReadNFSNTSLFile readObj = new ReadNFSNTSLFile();
        mapObj = readObj.fileupload1(beanObj, file, getConnection());
      } 
      System.out.println("result is " + mapObj + " filename " + beanObj.getFileName());
      boolean result = ((Boolean)mapObj.get("result")).booleanValue();
      int count = ((Integer)mapObj.get("count")).intValue();
      if (result) {
        if (beanObj.getFileName().contains("ICD")) {
          beanObj.setCategory("ICD-SETTLEMENT");
          beanObj.setStSubCategory("-");
          file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
              
              new Object[] { "ICD", beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
        } else if (beanObj.getFileName().contains("NTSL-DFS")) {
          beanObj.setCategory("DFS_SETTLEMENT");
          beanObj.setStSubCategory("-");
          file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
              
              new Object[] { "NTSL-DFS", beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
        } else if (beanObj.getFileName().contains("NTSL-JCB")) {
          beanObj.setCategory("JCB_SETTLEMENT");
          beanObj.setStSubCategory("-");
          file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
              
              new Object[] { "NTSL-JCB", beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
        } else {
          beanObj.setCategory("NFS_SETTLEMENT");
          beanObj.setStSubCategory("-");
          file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
              
              new Object[] { "NTSL-NFS", beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
        } 
        System.out.println("File id is " + file_id);
        String insertData = "INSERT INTO main_settlement_file_upload(FILEID, FILEDATE, UPLOADBY, CATEGORY, UPLOAD_FLAG, FILE_SUBCATEGORY,CYCLE,SETTLEMENT_FLAG,INTERCHANGE_FLAG,FILE_COUNT) VALUES('" + 
          file_id + "',STR_to_date('" + beanObj.getDatepicker() + "','%Y/%m/%d'),'" + 
          beanObj.getCreatedBy() + "','" + beanObj.getCategory() + "','Y','" + 
          beanObj.getStSubCategory() + "','" + beanObj.getCycle() + "'," + "'N','N','1')";
        System.out.println("main file insert query  " + insertData);
        getJdbcTemplate().execute(insertData);
        mapObj.put("entry", Boolean.valueOf(true));
      } 
      return mapObj;
    } catch (Exception e) {
      this.logger.info("Exception in uploadNTSLFile " + e);
      mapObj.put("result", Boolean.valueOf(false));
      mapObj.put("count", Integer.valueOf(0));
      return mapObj;
    } 
  }
  
  public HashMap<String, Object> iccwuploadNTSLFile(NFSSettlementBean beanObj, MultipartFile file, CompareSetupBean setupBean) {
    HashMap<String, Object> mapObj = new HashMap<>();
    try {
      String Fname = file.getOriginalFilename();
      System.out.println("entered NTSL Filename is :" + Fname);
      ReadNFSNTSLFile readObj = new ReadNFSNTSLFile();
      mapObj = readObj.iccwfileupload(beanObj, file, getConnection());
      return mapObj;
    } catch (Exception e) {
      System.out.println(e);
      return mapObj;
    } 
  }
  
  public HashMap<String, Object> ValidateDailySettProcess(NFSSettlementBean beanObj) {
    HashMap<String, Object> result = new HashMap<>();
    int rawCount = 0;
    List<String> subcategories = new ArrayList<>();
    subcategories.add("ISSUER");
    subcategories.add("ACQUIRER");
    int uploadCount = 0;
    int file_id = 0;
    int adjustmentFileCount = 0;
    try {
      try {
        file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getFileName(), beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
      } catch (Exception e) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "File is not Configured!");
        return result;
      } 
      String checkFileUploaded = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE  FILEDATE = ? ";
      uploadCount = ((Integer)getJdbcTemplate().queryForObject(checkFileUploaded, new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
      if (beanObj.getFileName().equalsIgnoreCase("NTSL-NFS")) {
        try {
          int adjfile_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
              new Object[] { beanObj.getFileName(), "NFS_ADJUSTMENT", "-" }, Integer.class)).intValue();
          adjustmentFileCount = ((Integer)getJdbcTemplate().queryForObject(
              "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE = ? AND CYCLE = '1'", 
              new Object[] { Integer.valueOf(adjfile_id), beanObj.getDatepicker() }, Integer.class)).intValue();
          this.logger.info("Adjustment file count is " + adjustmentFileCount);
        } catch (Exception e) {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Adjustment File is not Configured!");
          return result;
        } 
      } else {
        adjustmentFileCount = 1;
      } 
      if (uploadCount > 0 && adjustmentFileCount > 0) {
        System.out.println("File id is " + file_id);
        String alreadyProc = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE SETTLEMENT_FLAG = 'Y' AND FILEID = ? AND FILEDATE = ? AND CYCLE = ?";
        int procCount = ((Integer)getJdbcTemplate().queryForObject(alreadyProc, 
            new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker(), Integer.valueOf(beanObj.getCycle()) }, Integer.class)).intValue();
        System.out.println("Already Process count " + alreadyProc);
        if (procCount == 0) {
          String checkFirstTime = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE < '" + 
            beanObj.getDatepicker() + "'";
          int firstCount = ((Integer)getJdbcTemplate().queryForObject(checkFirstTime, new Object[] { Integer.valueOf(file_id) }, Integer.class)).intValue();
          if (firstCount > 0) {
            int getFileCount = ((Integer)getJdbcTemplate().queryForObject(
                "SELECT FILE_COUNT FROM MAIN_FILESOURCE WHERE FILEID = ?", new Object[] { Integer.valueOf(file_id) }, Integer.class)).intValue();
            String prevdayProcess = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE SETTLEMENT_FLAG = 'Y' AND FILEID = ? AND FILEDATE = date_format('" + 
              beanObj.getDatepicker() + "','DD/MON/YYYY')-1";
            int checkCount = ((Integer)getJdbcTemplate().queryForObject(prevdayProcess, new Object[] { Integer.valueOf(file_id) }, Integer.class)).intValue();
            if (checkCount > 0 && checkCount == getFileCount) {
              boolean rawfileUploaded = true;
              for (String subcategory : subcategories) {
                if (beanObj.getFileName().equalsIgnoreCase("NTSL-NFS")) {
                  file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
                      new Object[] { "NFS", "NFS", subcategory }, Integer.class)).intValue();
                  System.out.println("File id is " + file_id);
                  String checkRawData = "SELECT count(*) from MAIN_FILE_UPLOAD_DTLS WHERE FILEID = ? AND FILEDATE =?";
                  rawCount = ((Integer)getJdbcTemplate().queryForObject(checkRawData, 
                      new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker() }, Integer.class)).intValue();
                  System.out.println("rawCount is " + rawCount);
                } else {
                  file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", new Object[] { beanObj.getFileName().split("-")[1], beanObj.getCategory(), subcategory }, Integer.class)).intValue();
                  System.out.println("File id is " + file_id);
                  String checkRawData = "SELECT count(*) from MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE = ?";
                  rawCount = ((Integer)getJdbcTemplate().queryForObject(checkRawData, 
                      new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker() }, Integer.class)).intValue();
                  System.out.println("rawCount is " + rawCount);
                } 
                if (rawCount == 0)
                  rawfileUploaded = false; 
              } 
              if (rawfileUploaded) {
                file_id = ((Integer)getJdbcTemplate()
                  .queryForObject(
                    "SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", new Object[] { beanObj.getFileName(), 
                      beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
                System.out.println("File id is " + file_id);
                String checkNTSLEntries = "select count(*) from main_settlement_file_upload where FILEID = ? AND filedate = ? AND CYCLE = ?";
                int entryCount = ((Integer)getJdbcTemplate().queryForObject(checkNTSLEntries, 
                    new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker(), Integer.valueOf(beanObj.getCycle()) }, Integer.class)).intValue();
                System.out.println("Entry count is " + entryCount);
                if (entryCount == 0) {
                  System.out.println("NTSL File is not uploaded for selected date");
                  result.put("result", Boolean.valueOf(false));
                  result.put("msg", "Please Upload NTSL Files of selected date");
                } else {
                  result.put("result", Boolean.valueOf(true));
                } 
              } else {
                result.put("result", Boolean.valueOf(false));
                result.put("msg", "Issuer and Acquirer Raw Files are not uploaded");
              } 
            } else {
              result.put("result", Boolean.valueOf(false));
              result.put("msg", "Previous day settlement is not processed");
            } 
          } else {
            result.put("result", Boolean.valueOf(true));
          } 
        } else {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "File is already Processed");
        } 
      } else if (uploadCount == 0) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "Upload NTSL File for selected Date");
      } else {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "Upload Adjustment File for selected Date");
      } 
    } catch (Exception e) {
      System.out.println("Exception in ValidateDailySettProcess ");
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred");
    } 
    return result;
  }
  
  public HashMap<String, Object> checkNFSMonthlyProcess(NFSSettlementBean beanObj) {
    HashMap<String, Object> result = new HashMap<>();
    int entryCount = 0, totalDays = 0, cycleCount = 0;
    int file_id = 0;
    String checkFlag = null;
    try {
      totalDays = Integer.parseInt(beanObj.getToDate().substring(0, 2));
      System.out.println("diff is " + totalDays);
      try {
        file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getFileName(), "MONTHLY_SETTLEMENT", beanObj.getStSubCategory() }, Integer.class)).intValue();
      } catch (Exception e) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "File is not configured");
        return result;
      } 
      try {
        checkFlag = (String)getJdbcTemplate().queryForObject(
            "SELECT PROCESS_FLAG FROM MAIN_MONTHLY_NTSL_UPLOAD WHERE FILEID = ? AND FILEDATE = date_format(?,'DD/MM/YYYY')", 
            new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker() }, String.class);
        this.logger.info("Flag is " + checkFlag);
      } catch (Exception e) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "NTSL File is not uploaded for selected month");
        return result;
      } 
      if (checkFlag != null) {
        if (checkFlag.equalsIgnoreCase("Y")) {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Settlement is already processed for selected month");
        } else {
          result.put("result", Boolean.valueOf(true));
          if (!beanObj.getFileName().contains("NFS")) {
            file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
                new Object[] { beanObj.getFileName(), beanObj.getCategory(), "-" }, Integer.class)).intValue();
            System.out.println("File id is " + file_id);
            try {
              String checkNTSLEntries1Cycle = "select count(*) from main_settlement_file_upload where FILEID = ? AND filedate between date_format(?,'DD/MM/YYYY') AND date_format(?,'DD/MM/YYYY')";
              entryCount = ((Integer)getJdbcTemplate().queryForObject(checkNTSLEntries1Cycle, 
                  new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker(), beanObj.getToDate() }, Integer.class)).intValue();
              System.out.println("Entry count is " + entryCount);
              String getCycleCount = "select FILE_COUNT from main_filesource where FILEID = ?";
              cycleCount = ((Integer)getJdbcTemplate().queryForObject(getCycleCount, new Object[] { Integer.valueOf(file_id) }, Integer.class)).intValue();
              this.logger.info("Cycle count is " + cycleCount);
            } catch (Exception e) {
              this.logger.info("Exception while validating cycle 1 entires " + e);
              result.put("result", Boolean.valueOf(false));
              result.put("msg", "Exception while validating NTSL entires ");
              return result;
            } 
            if (entryCount / cycleCount != totalDays) {
              System.out.println("NTSL are not uploaded for all days of selected month");
              result.put("result", Boolean.valueOf(false));
              result.put("msg", "Please process all Settlement First");
            } else {
              result.put("result", Boolean.valueOf(true));
            } 
          } 
        } 
        return result;
      } 
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "NTSL File is not uploaded for selected month");
      return result;
    } catch (Exception e) {
      System.out.println("Exception in checkNFSMonthlyProcess " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred");
      return result;
    } 
  }
  
  public HashMap<String, Object> uploadMonthlyNTSLFile(NFSSettlementBean beanObj, MultipartFile file) {
    HashMap<String, Object> mapObj = new HashMap<>();
    try {
      if (beanObj.getFileName().contains("NFS")) {
        ReadNFSMonthlyNTSLFile readObj = new ReadNFSMonthlyNTSLFile();
        mapObj = readObj.fileupload(beanObj, file, getConnection());
      } else if (beanObj.getFileName().contains("DFS")) {
        ReadDFSJCBMonthlyNTSLFile readObj = new ReadDFSJCBMonthlyNTSLFile();
        mapObj = readObj.DFSfileupload(beanObj, file, getConnection());
      } else {
        ReadDFSJCBMonthlyNTSLFile readObj = new ReadDFSJCBMonthlyNTSLFile();
        mapObj = readObj.JCBfileupload(beanObj, file, getConnection());
      } 
      System.out.println("result is " + mapObj);
      boolean result = ((Boolean)mapObj.get("result")).booleanValue();
      int count = ((Integer)mapObj.get("count")).intValue();
      if (result) {
        int file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getFileName(), beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
        System.out.println("File id is " + file_id);
        String insertData = "INSERT INTO MAIN_MONTHLY_NTSL_UPLOAD(FILEID, FILEDATE, UPLOADBY, UPLOADDATE, CATEGORY, UPLOAD_FLAG, FILE_SUBCATEGORY,PROCESS_FLAG,TTUM_FLAG) VALUES('" + 
          file_id + "',date_format('" + beanObj.getDatepicker() + "','MM/YYYY'),'" + 
          beanObj.getCreatedBy() + "',sysdate,'" + beanObj.getCategory() + "','Y','" + 
          beanObj.getStSubCategory() + "','N','N')";
        getJdbcTemplate().execute(insertData);
        mapObj.put("entry", Boolean.valueOf(true));
      } 
      return mapObj;
    } catch (Exception e) {
      this.logger.info("Exception in uploadNTSLFile " + e);
      mapObj.put("result", Boolean.valueOf(false));
      mapObj.put("count", Integer.valueOf(0));
      return mapObj;
    } 
  }
  
  public HashMap<String, Object> checkMonthlyNTSLUploaded(NFSSettlementBean beanObj) {
    HashMap<String, Object> mapObj = new HashMap<>();
    int file_id = 0;
    try {
      try {
        file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getFileName(), beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
        System.out.println("File id is " + file_id);
      } catch (Exception e) {
        mapObj.put("result", Boolean.valueOf(false));
        mapObj.put("msg", "File is not configured!");
        return mapObj;
      } 
      String checkEntry = "SELECT COUNT(*) FROM MAIN_MONTHLY_NTSL_UPLOAD WHERE FILEID = ? AND FILEDATE= date_format(?,'MM/YYYY')";
      int count = ((Integer)getJdbcTemplate().queryForObject(checkEntry, new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker() }, Integer.class)).intValue();
      this.logger.info("Count is " + count);
      if (count > 0) {
        mapObj.put("result", Boolean.valueOf(false));
        mapObj.put("msg", "File is already uploaded");
      } else {
        mapObj.put("result", Boolean.valueOf(true));
      } 
    } catch (Exception e) {
      this.logger.info("Exception in checkMonthlyNTSL " + e);
      mapObj.put("result", Boolean.valueOf(false));
      mapObj.put("msg", "File is already uploaded");
    } 
    return mapObj;
  }
  
  public HashMap<String, Object> ValidateDailyInterchangeProcess(NFSSettlementBean beanObj) {
    HashMap<String, Object> result = new HashMap<>();
    int rawCount = 0;
    List<String> subcategories = new ArrayList<>();
    subcategories.add("ISSUER");
    subcategories.add("ACQUIRER");
    int uploadCount = 0;
    int file_id = 0;
    try {
      try {
        file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getFileName(), beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
      } catch (Exception e) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "File is not Configured!");
        return result;
      } 
      String checkFileUploaded = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE = ?";
      uploadCount = ((Integer)getJdbcTemplate().queryForObject(checkFileUploaded, 
          new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker() }, Integer.class)).intValue();
      System.out.println("Upload count" + uploadCount);
      String getCycleCount = "select FILE_COUNT from main_filesource where FILEID = ?";
      int cycleCount = ((Integer)getJdbcTemplate().queryForObject(getCycleCount, new Object[] { Integer.valueOf(file_id) }, Integer.class)).intValue();
      this.logger.info("Cycle count is " + cycleCount);
      if (uploadCount > 0 && uploadCount == cycleCount) {
        System.out.println("File id is " + file_id);
        String alreadyProc = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE INTERCHANGE_FLAG = 'Y' AND FILEID = ? AND FILEDATE = ?";
        int procCount = ((Integer)getJdbcTemplate().queryForObject(alreadyProc, 
            new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker() }, Integer.class)).intValue();
        System.out.println("Already Process count " + alreadyProc);
        if (procCount == 0) {
          String checkFirstTime = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE < '" + 
            beanObj.getDatepicker() + "'";
          int firstCount = ((Integer)getJdbcTemplate().queryForObject(checkFirstTime, new Object[] { Integer.valueOf(file_id) }, Integer.class)).intValue();
          if (firstCount > 0) {
            String prevdayProcess = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE INTERCHANGE_FLAG = 'Y' AND FILEID = ? AND FILEDATE = date_format('" + 
              beanObj.getDatepicker() + "','DD/MON/YYYY')-1";
            int checkCount = ((Integer)getJdbcTemplate().queryForObject(prevdayProcess, new Object[] { Integer.valueOf(file_id) }, Integer.class)).intValue();
            if (checkCount > 0 && checkCount == cycleCount) {
              int ttumCount = ((Integer)getJdbcTemplate().queryForObject(
                  "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE TTUM_FLAG = 'Y' AND FILEID = ? AND FILEDATE = date_format(?,'DD/MM/YYYY')-1", 
                  new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker() }, Integer.class)).intValue();
              this.logger.info("Previous day ttum count is " + ttumCount);
              if (ttumCount > 0 && ttumCount == cycleCount) {
                boolean rawfileUploaded = true;
                for (String subcategory : subcategories) {
                  if (beanObj.getFileName().equalsIgnoreCase("NTSL-NFS")) {
                    file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
                        new Object[] { "NFS", "NFS", subcategory }, Integer.class)).intValue();
                    System.out.println("File id is " + file_id);
                    String str = "SELECT count(*) from MAIN_FILE_UPLOAD_DTLS WHERE FILEID = ? AND FILEDATE =?";
                    rawCount = ((Integer)getJdbcTemplate().queryForObject(str, 
                        new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker() }, Integer.class)).intValue();
                    System.out.println("rawCount is " + rawCount);
                    continue;
                  } 
                  file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
                      new Object[] { beanObj.getFileName().split("-")[1], 
                        beanObj.getCategory(), subcategory }, Integer.class)).intValue();
                  System.out.println("File id is " + file_id);
                  String checkRawData = "SELECT count(*) from MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE = ?";
                  rawCount = ((Integer)getJdbcTemplate().queryForObject(checkRawData, 
                      new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker() }, Integer.class)).intValue();
                  System.out.println("rawCount is " + rawCount);
                } 
                if (rawCount == 0) {
                  result.put("result", Boolean.valueOf(false));
                  result.put("msg", "Issuer and Acquirer Raw Files are not uploaded");
                } else {
                  result.put("result", Boolean.valueOf(true));
                } 
              } else {
                result.put("result", Boolean.valueOf(false));
                result.put("msg", "Previous day Interchange TTUM is not processed");
              } 
            } else {
              result.put("result", Boolean.valueOf(false));
              result.put("msg", "Previous day Interchange is not processed");
            } 
          } else {
            result.put("result", Boolean.valueOf(true));
          } 
        } else {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "File is already Processed");
        } 
      } else {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "Upload all NTSL for selected Date");
      } 
    } catch (Exception e) {
      System.out.println("Exception in ValidateDailyInterchangeProcess ");
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred");
    } 
    return result;
  }
  
  public HashMap<String, Object> ValidateForSettVoucher(NFSSettlementBean beanObj) {
    HashMap<String, Object> result = new HashMap<>();
    int rawCount = 0;
    List<String> subcategories = new ArrayList<>();
    subcategories.add("ISSUER");
    subcategories.add("ACQUIRER");
    int uploadCount = 0;
    int file_id = 0;
    try {
      try {
        file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getFileName(), beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
      } catch (Exception e) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "File is not Configured!");
        return result;
      } 
      System.out.println("File id is " + file_id);
      String checkFileUploaded = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ?  AND FILEDATE = ? AND CYCLE = ?";
      uploadCount = ((Integer)getJdbcTemplate().queryForObject(checkFileUploaded, 
          new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker(), Integer.valueOf(beanObj.getCycle()) }, Integer.class)).intValue();
      System.out.println("Upload count " + uploadCount);
      if (uploadCount > 0) {
        String alreadyProc = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE SETTLEMENT_FLAG = 'Y' AND FILEID = ? AND FILEDATE = ? AND CYCLE = ?";
        int procCount = ((Integer)getJdbcTemplate().queryForObject(alreadyProc, 
            new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker(), Integer.valueOf(beanObj.getCycle()) }, Integer.class)).intValue();
        System.out.println("Already Process count " + alreadyProc);
        if (procCount > 0) {
          if (beanObj.getFileName().equalsIgnoreCase("NTSL-NFS")) {
            int adj_file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
                new Object[] { beanObj.getFileName(), "NFS_ADJUSTMENT", "-" }, Integer.class)).intValue();
            String checkAdjFileUploaded = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE = ?";
            uploadCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjFileUploaded, 
                new Object[] { Integer.valueOf(adj_file_id), beanObj.getDatepicker() }, Integer.class)).intValue();
            System.out.println("Already Process count " + uploadCount);
            if (uploadCount == 0) {
              System.out.println("Error message ");
              result.put("result", Boolean.valueOf(false));
              result.put("msg", "Adjustment File is not uploaded for Selected Date!");
            } else {
              String diffQuery = "select count(*) from  NFS_SETTLEMENT_REPORT_DIFF where (NO_OF_TXNS = 'Y' OR DEBIT = 'Y' OR CREDIT = 'Y') AND FILEDATE = ? AND RECTIFIED = 'N' AND CYCLE = ?";
              int diffCount = ((Integer)getJdbcTemplate().queryForObject(diffQuery, 
                  new Object[] { beanObj.getDatepicker(), Integer.valueOf(beanObj.getCycle()) }, Integer.class)).intValue();
              this.logger.info("diffCount is " + diffCount);
              if (diffCount > 0) {
                result.put("result", Boolean.valueOf(false));
                result.put("msg", "Final Settlement Amount is mismatching !");
              } else {
                result.put("result", Boolean.valueOf(true));
              } 
            } 
          } else {
            result.put("result", Boolean.valueOf(true));
          } 
        } else {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Please process Settlement first!");
        } 
      } else {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "NTSL File is not uploaded for Selected Date!");
      } 
    } catch (Exception e) {
      System.out.println("Exception in ValidateDailySettProcess " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred");
    } 
    return result;
  }
  
  public boolean checkSettVoucherProcess(NFSSettlementBean beanObj) {
    try {
      if (!beanObj.getFileName().contains("PBGB")) {
        int file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getFileName(), beanObj.getCategory(), "-" }, Integer.class)).intValue();
        String checkProcessFlag = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE SETT_VOUCHER = 'Y' AND FILEID = ? AND FILEDATE = ? AND CYCLE = ?";
        int procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, 
            new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker(), Integer.valueOf(beanObj.getCycle()) }, Integer.class)).intValue();
        this.logger.info("Already Process count " + procCount);
        if (procCount > 0)
          if (beanObj.getFileName().equalsIgnoreCase("NTSL-NFS")) {
            String str = "DELETE FROM NFS_SETTLEMENT_VOUCHER WHERE FILEDATE ='" + 
              beanObj.getDatepicker() + "' AND CYCLE = '" + beanObj.getCycle() + "'";
            getJdbcTemplate().execute(str);
          } else if (beanObj.getFileName().equalsIgnoreCase("NTSL-DFS")) {
            String str = "DELETE FROM DFS_SETTLEMENT_VOUCHER WHERE FILEDATE ='" + 
              beanObj.getDatepicker() + "' AND CYCLE = '" + beanObj.getCycle() + "'";
            getJdbcTemplate().execute(str);
          } else {
            String str = "DELETE FROM JCB_SETTLEMENT_VOUCHER WHERE FILEDATE ='" + 
              beanObj.getDatepicker() + "' AND CYCLE = '" + beanObj.getCycle() + "'";
            getJdbcTemplate().execute(str);
          }  
        return true;
      } 
      String deleteQuery = "DELETE FROM PBGB_SETTLEMENT_VOUCHER WHERE FILEDATE ='" + beanObj.getDatepicker() + 
        "'";
      getJdbcTemplate().execute(deleteQuery);
      return true;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
  
  public HashMap<String, Object> ValidateForAdjTTUM(NFSSettlementBean beanObj) {
    HashMap<String, Object> result = new HashMap<>();
    int rawCount = 0;
    List<String> subcategories = new ArrayList<>();
    subcategories.add("ISSUER");
    subcategories.add("ACQUIRER");
    String checkDataPresent = "";
    int uploadCount = 0;
    int processCount = 0;
    int file_id = 0;
    try {
      String checkAdjProcess = "";
      if (beanObj.getAdjType().contains("PENALTY")) {
        checkAdjProcess = "SELECT COUNT(*) FROM  nfs_adj_report WHERE FILEDATE=  str_to_date(?,'%Y/%m/%d')    and UPPER(ADJTYPE) LIKE '%" + 
          beanObj.getAdjType() + "%'";
        processCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjProcess, 
            new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
      } else if (beanObj.getAdjType().contains("ACUIRER PENALTY")) {
        checkAdjProcess = "SELECT COUNT(*) FROM  nfs_acq_penalty_ttum WHERE FILEDATE= str_to_date(?,'%Y/%m/%d')  ";
        processCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjProcess, 
            new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
      } else {
        checkAdjProcess = "SELECT COUNT(*) FROM  nfs_adj_report WHERE FILEDATE= str_to_date(?,'%Y/%m/%d')   and adjtype = ?";
        processCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjProcess, 
            new Object[] { beanObj.getDatepicker(), beanObj.getAdjType() }, Integer.class)).intValue();
      } 
      if (processCount > 0) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "Adjustment TTUM is already processed.\n Please download TTUM");
      } else {
        if (beanObj.getAdjType().contains("PENALTY")) {
          checkDataPresent = "SELECT COUNT(*) FROM nfs_adjustment_rawdata WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') AND ISR='MIT' AND CUSTOMERPENALTY <> 0";
        } else if (beanObj.getAdjType().contains("CREDIT ADJUSTMENT ICCW ATM")) {
          checkDataPresent = "SELECT COUNT(*) FROM nfs_adjustment_rawdata WHERE FILEDATE=  str_to_date(?,'%Y/%m/%d')   AND ISR='UOB' AND ADJTYPE= 'Credit Adjustment' AND PAN_ENTRY_MODE='08' AND MCC_CODE='6013'";
        } else {
          checkDataPresent = "select count(*) FROM nfs_adjustment_rawdata  where FILEDATE= str_to_date(?,'%Y/%m/%d')   AND UPPER(ADJTYPE) LIKE '%" + 
            beanObj.getAdjType() + "%'";
        } 
        System.out.println("checkDataPresent " + checkDataPresent);
        int getcount = ((Integer)getJdbcTemplate().queryForObject(checkDataPresent, 
            new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
        System.out.println("data count " + getcount);
        if (getcount > 0) {
          result.put("result", Boolean.valueOf(true));
        } else {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Data is not present in rawdata for generating TTUM");
        } 
      } 
    } catch (Exception e) {
      System.out.println("Exception in ValidateDailySettProcess " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred");
    } 
    return result;
  }
  
  public HashMap<String, Object> ValidateForAdjPENALTYTTUM(NFSSettlementBean beanObj) {
    HashMap<String, Object> result = new HashMap<>();
    int rawCount = 0;
    List<String> subcategories = new ArrayList<>();
    subcategories.add("ISSUER");
    subcategories.add("ACQUIRER");
    String checkDataPresent = "";
    int uploadCount = 0;
    int processCount = 0;
    int file_id = 0;
    try {
      String checkAdjProcess = "";
      if (beanObj.getAdjType().contains("ACUIRER PENALTY")) {
        checkAdjProcess = "SELECT COUNT(*) FROM  nfs_acq_penality_ttum  WHERE FILEDATE= date_format(?,'%Y/%m/%d') ";
        processCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjProcess, 
            new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
      } else {
        checkAdjProcess = "SELECT COUNT(*) FROM  nfs_adj_report WHERE FILEDATE= date_format(?,'%Y/%m/%d')  and adjtype = ?";
        processCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjProcess, 
            new Object[] { beanObj.getDatepicker(), beanObj.getAdjType() }, Integer.class)).intValue();
      } 
      if (processCount > 0) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "Adjustment TTUM is already processed.\n Please download TTUM");
      } else {
        result.put("result", Boolean.valueOf(true));
        result.put("msg", "Adjustment TTUM not process");
      } 
    } catch (Exception e) {
      System.out.println("Exception in ValidateDailySettProcess " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred");
    } 
    return result;
  }
  
  public HashMap<String, Object> ValidateForAdjTTUMICD(NFSSettlementBean beanObj) {
    HashMap<String, Object> result = new HashMap<>();
    int rawCount = 0;
    List<String> subcategories = new ArrayList<>();
    subcategories.add("ISSUER");
    subcategories.add("ACQUIRER");
    String checkDataPresent = "";
    int uploadCount = 0;
    int processCount = 0;
    int file_id = 0;
    try {
      String checkAdjProcess = "";
      if (beanObj.getAdjType().contains("PENALTY")) {
        checkAdjProcess = "SELECT COUNT(*) FROM ICD_ADJ_REPORT WHERE date_format(FILEDATE,'%d%m%y')= date_format(?,'%d%m%y')   and UPPER(ADJTYPE) LIKE '%" + 
          beanObj.getAdjType() + "%'";
        processCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjProcess, 
            new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
      } else {
        checkAdjProcess = "SELECT COUNT(*) FROM ICD_ADJ_REPORT WHERE date_format(FILEDATE,'%d%m%y')= date_format(?,'%d%m%y')  and adjtype = ?";
        processCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjProcess, 
            new Object[] { beanObj.getDatepicker(), beanObj.getAdjType() }, Integer.class)).intValue();
      } 
      if (processCount > 0) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "Adjustment TTUM is already processed.\n Please download TTUM");
      } else {
        if (beanObj.getAdjType().contains("PENALTY")) {
          checkDataPresent = "SELECT COUNT(*) FROM ICD_ADJUSTMENT_RAWDATA  WHERE date_format(FILEDATE,'%d%m%y')= date_format(?,'%d%m%y')  AND ISR='UOB' AND TO_NUMBER(CUSTOMERPENALTY) <> 0 ";
        } else if (beanObj.getAdjType().contains("CREDIT ADJUSTMENT ICCW ATM")) {
          checkDataPresent = "SELECT COUNT(*) FROM ICD_ADJUSTMENT_RAWDATA  WHERE date_format(FILEDATE,'%d%m%y')= date_format(?,'%d%m%y')  AND ISR='UOB' AND ADJTYPE= 'Credit Adjustment' AND PAN_ENTRY_MODE='08' AND MCC_CODE='6013'";
        } else {
          checkDataPresent = "select count(*) FROM ICD_ADJUSTMENT_RAWDATA  where date_format(FILEDATE,'%d%m%y')= date_format(?,'%d%m%y')  AND UPPER(ADJTYPE) LIKE '%" + 
            beanObj.getAdjType() + "%'";
        } 
        System.out.println("checkDataPresent " + checkDataPresent);
        int getcount = ((Integer)getJdbcTemplate().queryForObject(checkDataPresent, 
            new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
        if (getcount > 0) {
          result.put("result", Boolean.valueOf(true));
        } else {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Data is not present in rawdata for generating TTUM");
        } 
      } 
    } catch (Exception e) {
      System.out.println("Exception in ValidateDailySettProcess " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred");
    } 
    return result;
  }
  
  public HashMap<String, Object> ValidateForAdjTTUMSETTL(NFSSettlementBean beanObj) {
    HashMap<String, Object> result = new HashMap<>();
    int rawCount = 0;
    String checkDataPresent = "";
    List<String> subcategories = new ArrayList<>();
    subcategories.add("ISSUER");
    subcategories.add("ACQUIRER");
    int uploadCount = 0;
    int file_id = 0;
    try {
      String checkAdjProcess = "";
      if (beanObj.getAdjCategory().contains("ICD")) {
        checkAdjProcess = "SELECT COUNT(*) FROM  icd_settlement_report WHERE filedate =str_to_date(?,'%Y/%m/%d')  ";
      } else if (beanObj.getCategory().contains("JCB")) {
        checkAdjProcess = "SELECT COUNT(*) FROM  jcb_settlement_report WHERE filedate=str_to_date(?,'%Y/%m/%d') ";
      } else if (beanObj.getCategory().contains("DFS")) {
        checkAdjProcess = "SELECT COUNT(*) FROM  dfs_settlement_report WHERE filedate=str_to_date(?,'%Y/%m/%d') ";
      } else {
        checkAdjProcess = "SELECT COUNT(*) FROM  nfs_settlement_report WHERE filedate = str_to_date(?,'%Y/%m/%d') and cycle ='" + beanObj.getCycle() + "'";
      } 
      int processCount = 0;
      processCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjProcess, new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
      System.out.println(" processCount report " + processCount + " " + beanObj.getDatepicker());
      if (processCount > 0) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "Settlement TTUM is already processed.\n Please download TTUM");
      } else {
        if (beanObj.getAdjCategory().equalsIgnoreCase("ICD")) {
          checkDataPresent = "select count(*) FROM   ntsl_icd_rawdata where filedate =str_to_date(?,'%Y/%m/%d') ";
        } else if (beanObj.getCategory().equalsIgnoreCase("JCB")) {
          checkDataPresent = "select count(*) FROM  ntsl_jcb_rawdata  where filedate = str_to_date(?,'%Y/%m/%d')";
        } else if (beanObj.getCategory().equalsIgnoreCase("DFS")) {
          checkDataPresent = "select count(*) FROM  ntsl_dfs_rawdata  where filedate=str_to_date(?,'%Y/%m/%d')";
        } else {
          checkDataPresent = "select count(*) FROM ntsl_nfs_rawdata where filedate= str_to_date(?,'%Y/%m/%d') ";
        } 
        int getcount = ((Integer)getJdbcTemplate().queryForObject(checkDataPresent, 
            new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
        if (getcount > 0) {
          result.put("result", Boolean.valueOf(true));
        } else {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Data is not present in rawdata for generating TTUM");
        } 
      } 
    } catch (Exception e) {
      System.out.println("Exception in ValidateDailySettProcess " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred");
    } 
    return result;
  }
  
  public HashMap<String, Object> ValidateForAdjTTUMTEXT(NFSSettlementBean beanObj) {
    this.logger.info(" ValidateForAdjTTUMTEXT ");
    HashMap<String, Object> result = new HashMap<>();
    int rawCount = 0;
    List<String> subcategories = new ArrayList<>();
    subcategories.add("ISSUER");
    subcategories.add("ACQUIRER");
    int uploadCount = 0;
    int file_id = 0;
    try {
      String checkAdjProcess = "SELECT COUNT(*) FROM NFS_ADJUSTMENT_TTUM WHERE date_format(FILEDATE,'%d%m%y')= date_format(?,'%d%m%y')  AND adjtype = ?";
      int processCount = 0;
      processCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjProcess, 
          new Object[] { beanObj.getDatepicker(), beanObj.getAdjType() }, Integer.class)).intValue();
      if (processCount > 0) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "Adjustment TTUM is already processed.\n Please download TTUM");
      } else {
        int getcount = 0;
        if (beanObj.getAdjType().equalsIgnoreCase("PENALTY")) {
          String checkDataPresent = "    SELECT COUNT(*) as DATA_COUNT  FROM NFS_ADJUSTMENT_RAWDATA WHERE date_format(FILEDATE,'%d%m%y') =date_format('" + 
            beanObj.getDatepicker() + "' ,'%d%m%y') AND \r\n" + 
            "    date_format(CONVTODATE(ADJSETTLEMENTDATE),'%d%m%y')=date_format('" + 
            beanObj.getDatepicker() + 
            "' ,'%d%m%y') AND ISR='UOB' AND TO_NUMBER(CUSTOMERPENALTY) <> 0";
          System.out.println("checkDataPresent " + checkDataPresent);
          getcount = ((Integer)getJdbcTemplate().queryForObject(checkDataPresent, new Object[0], Integer.class)).intValue();
        } else if (beanObj.getAdjType().contains("CREDIT ADJUSTMENT ICCW ATM")) {
          String checkDataPresent = "SELECT COUNT(*) FROM NFS_ADJUSTMENT_RAWDATA WHERE date_format(FILEDATE,'%d%m%y')= date_format(?,'%d%m%y')  AND ISR='UOB' AND ADJTYPE= 'Credit Adjustment' AND PAN_ENTRY_MODE='08' AND MCC_CODE='6013'";
          getcount = ((Integer)getJdbcTemplate().queryForObject(checkDataPresent, 
              new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
        } else {
          String checkDataPresent = "select count(*) FROM NFS_ADJUSTMENT_RAWDATA  where date_format(FILEDATE,'%d%m%y')= date_format(?,'%d%m%y')  AND UPPER(adjtype) ='" + 
            beanObj.getAdjType() + "'";
          getcount = ((Integer)getJdbcTemplate().queryForObject(checkDataPresent, 
              new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
        } 
        if (getcount > 0) {
          result.put("result", Boolean.valueOf(true));
          result.put("msg", "Data Already Present!!");
        } else {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Data is not present in rawdata for generating TTUM");
        } 
      } 
    } catch (Exception e) {
      System.out.println("Exception in ValidateDailySettProcess " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred");
    } 
    return result;
  }
  
  public HashMap<String, Object> ValidateForAdjTTUMTEXTICD(NFSSettlementBean beanObj) {
    this.logger.info(" ValidateForAdjTTUMTEXT ");
    HashMap<String, Object> result = new HashMap<>();
    int rawCount = 0;
    List<String> subcategories = new ArrayList<>();
    subcategories.add("ISSUER");
    subcategories.add("ACQUIRER");
    int uploadCount = 0;
    int file_id = 0;
    try {
      String checkAdjProcess = "SELECT COUNT(*) FROM ICD_ADJUSTMENT_TTUM WHERE date_format(FILEDATE,'%d%m%y')= date_format(?,'%d%m%y')  AND adjtype = ?";
      int processCount = 0;
      processCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjProcess, 
          new Object[] { beanObj.getDatepicker(), beanObj.getAdjType() }, Integer.class)).intValue();
      if (processCount > 0) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "Adjustment TTUM is already processed.\n Please download TTUM");
      } else {
        int getcount = 0;
        if (beanObj.getAdjType().equalsIgnoreCase("PENALTY")) {
          String checkDataPresent = "    SELECT COUNT(*) as DATA_COUNT  FROM ICD_ADJUSTMENT_RAWDATA WHERE date_format(FILEDATE,'%d%m%y') =date_format('" + 
            beanObj.getDatepicker() + "' ,'%d%m%y') AND \r\n" + 
            "    date_format(CONVTODATE(ADJSETTLEMENTDATE),'%d%m%y')=date_format('" + 
            beanObj.getDatepicker() + 
            "' ,'%d%m%y') AND ISR='UOB' AND TO_NUMBER(CUSTOMERPENALTY) <> 0";
          System.out.println("checkDataPresent " + checkDataPresent);
          getcount = ((Integer)getJdbcTemplate().queryForObject(checkDataPresent, new Object[0], Integer.class)).intValue();
        } else if (beanObj.getAdjType().contains("CREDIT ADJUSTMENT ICCW ATM")) {
          String checkDataPresent = "SELECT COUNT(*) FROM ICD_ADJUSTMENT_RAWDATA WHERE date_format(FILEDATE,'%d%m%y')= date_format(?,'%d%m%y')  AND ISR='UOB' AND ADJTYPE= 'Credit Adjustment' AND PAN_ENTRY_MODE='08' AND MCC_CODE='6013'";
          getcount = ((Integer)getJdbcTemplate().queryForObject(checkDataPresent, 
              new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
        } else {
          String checkDataPresent = "select count(*) FROM ICD_ADJUSTMENT_RAWDATA  where date_format(FILEDATE,'%d%m%y')= date_format(?,'%d%m%y')  AND UPPER(adjtype) ='" + 
            beanObj.getAdjType() + "'";
          getcount = ((Integer)getJdbcTemplate().queryForObject(checkDataPresent, 
              new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
        } 
        if (getcount > 0) {
          result.put("result", Boolean.valueOf(true));
        } else {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Data is not present in rawdata for generating TTUM");
        } 
      } 
    } catch (Exception e) {
      System.out.println("Exception in ValidateDailySettProcess " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred");
    } 
    return result;
  }
  
  public HashMap<String, Object> ValidateForrcon(NFSSettlementBean beanObj) {
    this.logger.info(" ValidateForAdjTTUMTEXT ");
    HashMap<String, Object> result = new HashMap<>();
    int rawCount = 0;
    List<String> subcategories = new ArrayList<>();
    subcategories.add("ISSUER");
    subcategories.add("ACQUIRER");
    int uploadCount = 0;
    int file_id = 0;
    try {
      String checkAdjProcess = "select count(*) from main_file_upload_dtls where date_format(FILEDATE,'%d%m%y')= date_format(?,'%d%m%y')  and fileid in ('3','2','1')";
      int processCount = 0;
      processCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjProcess, 
          new Object[] { beanObj.getDatepicker(), beanObj.getAdjType() }, Integer.class)).intValue();
      if (processCount == 2) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "RECON NOT PROCESS!");
      } else {
        int getcount = 0;
        if (beanObj.getAdjType().equalsIgnoreCase("PENALTY")) {
          String checkDataPresent = "    SELECT COUNT(*) as DATA_COUNT  FROM NFS_ADJUSTMENT_RAWDATA WHERE date_format(FILEDATE,'%d%m%y') =date_format('" + 
            beanObj.getDatepicker() + "' ,'%d%m%y') AND \r\n" + 
            "    date_format(CONVTODATE(ADJSETTLEMENTDATE),'%d%m%y')=date_format('" + 
            beanObj.getDatepicker() + "' ,'%d%m%y') AND ISR='UOB'";
          getcount = ((Integer)getJdbcTemplate().queryForObject(checkDataPresent, new Object[0], Integer.class)).intValue();
        } else {
          String checkDataPresent = "select count(*) FROM NFS_ADJUSTMENT_RAWDATA  where date_format(FILEDATE,'%d%m%y')= date_format(?,'%d%m%y')  AND UPPER(adjtype) ='" + 
            beanObj.getAdjType() + "'";
          getcount = ((Integer)getJdbcTemplate().queryForObject(checkDataPresent, 
              new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
        } 
        if (getcount > 0) {
          result.put("result", Boolean.valueOf(true));
        } else {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Data is not present in rawdata for generating TTUM");
        } 
      } 
    } catch (Exception e) {
      System.out.println("Exception in ValidateDailySettProcess " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred");
    } 
    return result;
  }
  
  public HashMap<String, Object> ValidateForAdjTTUMTEXTSETTL(NFSSettlementBean beanObj) {
    this.logger.info(" ValidateForAdjTTUMTEXT ");
    HashMap<String, Object> result = new HashMap<>();
    int rawCount = 0;
    List<String> subcategories = new ArrayList<>();
    subcategories.add("ISSUER");
    subcategories.add("ACQUIRER");
    int uploadCount = 0;
    int file_id = 0;
    try {
      String checkAdjProcess = "";
      if (beanObj.getAdjCategory().contains("ICD")) {
        checkAdjProcess = "SELECT count(*) FROM  icd_settlement_daily_ttum  WHERE date_format(FILEDATE,'%d%m%y')=date_format(date_format('" + 
          beanObj.getDatepicker() + "' ,'%d%m%y'))";
      } else {
        checkAdjProcess = "SELECT count(*) FROM nfs_settlement_daily_ttum WHERE date_format(FILEDATE,'%d%m%y')=date_format(date_format('" + 
          beanObj.getDatepicker() + "' ,'%d%m%y'))";
      } 
      int processCount = 0;
      System.out.println("query " + checkAdjProcess + " count " + processCount);
      processCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjProcess, new Object[0], Integer.class)).intValue();
      if (processCount > 0) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "Settlement TTUM is already processed.\n Please download TTUM");
      } else {
        String checkDataPresent = "";
        if (beanObj.getAdjCategory().contains("ICD")) {
          checkDataPresent = "select count(*) FROM  ntsl_icd_rawdata  where date_format(filedate,'%d-%m-%y') = date_format(?,'%d-%m-%y') ";
        } else {
          checkDataPresent = "select count(*) FROM  ntsl_nfs_rawdata  where date_format(filedate,'%d-%m-%y') = date_format(?,'%d-%m-%y') ";
        } 
        int getcount = ((Integer)getJdbcTemplate().queryForObject(checkDataPresent, 
            new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
        if (getcount > 0) {
          result.put("result", Boolean.valueOf(true));
        } else {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Data is not present in rawdata for generating TTUM");
        } 
      } 
    } catch (Exception e) {
      System.out.println("Exception in ValidateDailySettProcess " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred");
    } 
    return result;
  }
  
  public boolean checkAdjTTUMProcessREPORTSETTL(NFSSettlementBean beanObj) {
    try {
      int procCount = 0;
      String checkProcessFlag = "";
      if (beanObj.getAdjCategory().equalsIgnoreCase("ICD")) {
        checkProcessFlag = "SELECT COUNT(*) FROM icd_settlement_report WHERE date_format(filedate,'%d%m%y') = date_format(?,'%d%m%y') ";
      } else if (beanObj.getCategory().contains("JCB")) {
        checkProcessFlag = "SELECT COUNT(*) FROM   jcb_settlement_report WHERE date_format(filedate,'%d%m%y') = date_format(?,'%d%m%y') ";
      } else if (beanObj.getCategory().contains("DFS")) {
        checkProcessFlag = "SELECT COUNT(*) FROM dfs_settlement_report WHERE date_format(filedate,'%d%m%y') = date_format(?,'%d%m%y') ";
      } else {
        checkProcessFlag = "SELECT COUNT(*) FROM  nfs_settlement_report WHERE date_format(filedate,'%d-%m-%y') = date_format(?,'%d-%m-%y') ";
      } 
      this.logger.info("checkProcessFlag sql  " + checkProcessFlag);
      procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
      this.logger.info("Already Process count " + procCount);
      if (procCount > 0)
        return true; 
      return false;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
  
  public boolean checkSettlementRupay(RupayUploadBean beanObj) {
    try {
      int procCount = 0;
      String checkProcessFlag = "SELECT COUNT(*) FROM rupay_dscr_rawdata WHERE filedate = str_to_date(?,'%Y/%m/%d') and cycle=? ";
      this.logger.info("checkProcessFlag sql  " + checkProcessFlag);
      procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, new Object[] { beanObj.getFileDate(),  beanObj.getCycle() }, Integer.class)).intValue();
      this.logger.info("Already Process count " + procCount);
      if (procCount > 0)
        return true; 
      return false;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
  
  public boolean checkSettlementRupay2(RupayUploadBean beanObj) {
	    try {
	      int procCount = 0;
	      String checkProcessFlag = "SELECT COUNT(*) FROM rupay_dscr_rawdata WHERE filedate = str_to_date(?,'%Y/%m/%d')";
	      this.logger.info("checkProcessFlag sql  " + checkProcessFlag);
	      procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
	      this.logger.info("Already Process count " + procCount);
	      if (procCount > 0)
	        return true; 
	      return false;
	    } catch (Exception e) {
	      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
	      return false;
	    } 
	  }
  
  public boolean checkSettlementVisa(RupayUploadBean beanObj) {
    try {
      int procCount = 0;
      String checkUpload = "";
      if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC") && beanObj.getSubcategory().equalsIgnoreCase("ACQUIRER")) {
        checkUpload = "SELECT COUNT(*)  FROM visa_dom_acq_settlement_report WHERE filedate = STR_to_date('" + beanObj.getFileDate() + "','%Y/%m/%d') ";
      } else if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC") && beanObj.getSubcategory().equalsIgnoreCase("ISSUER")) {
        checkUpload = "SELECT COUNT(*)  FROM visa_dom_iss_settlement_report WHERE filedate = STR_to_date('" + beanObj.getFileDate() + "','%Y/%m/%d') ";
      } else if (beanObj.getSubcategory().equalsIgnoreCase("INTERNATIONAL") && beanObj.getSubcategory().equalsIgnoreCase("ACQUIRER")) {
        checkUpload = "SELECT COUNT(*)  FROM visa_int_acq_settlement_report WHERE filedate = STR_to_date('" + beanObj.getFileDate() + "','%Y/%m/%d') ";
      } else if (beanObj.getSubcategory().equalsIgnoreCase("INTERNATIONAL") && beanObj.getSubcategory().equalsIgnoreCase("ISSUER")) {
        checkUpload = "SELECT COUNT(*)  FROM visa_int_iss_settlement_report WHERE filedate = STR_to_date('" + beanObj.getFileDate() + "','%Y/%m/%d') ";
      } 
      procCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
      this.logger.info("Already Process count " + procCount);
      if (procCount > 0)
        return true; 
      return false;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
  
  public boolean checkSettlementRupayINT(RupayUploadBean beanObj) {
    try {
      int procCount = 0;
      String checkProcessFlag = "SELECT COUNT(*) FROM irgcs_rupay_int_dsr_rawdata WHERE filedate = str_to_date(?,'%Y/%m/%d')";
      this.logger.info("checkProcessFlag sql  " + checkProcessFlag);
      procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
      this.logger.info("Already Process count " + procCount);
      if (procCount > 0)
        return true; 
      return false;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
  
  public boolean checkSettlementQsparcINT(RupayUploadBean beanObj) {
    try {
      int procCount = 0;
      String checkProcessFlag = "SELECT COUNT(*) FROM irgcs_qsparc_int_dsr_rawdata WHERE filedate=STR_to_date(?,'%Y/%m/%d')";
      this.logger.info("checkProcessFlag sql  " + checkProcessFlag);
      procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
      this.logger.info("Already Process count " + procCount);
      if (procCount > 0)
        return true; 
      return false;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
  
  public boolean checkSettlementQsparc(RupayUploadBean beanObj) {
    try {
      int procCount = 0;
      String checkProcessFlag = "SELECT COUNT(*) FROM qsparc_dscr_rawdata WHERE filedate = STR_to_date(?,'%Y/%m/%d') and cycle='" + beanObj.getCycle() + "'";
      this.logger.info("checkProcessFlag sql  " + checkProcessFlag);
      procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
      this.logger.info("Already Process count " + procCount);
      if (procCount > 0)
        return true; 
      return false;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
  
  public boolean checkAdjTTUMProcessREPORT(NFSSettlementBean beanObj) {
    try {
      int procCount = 0;
      String checkProcessFlag = "";
      if (beanObj.getAdjType().equalsIgnoreCase("PENALTY")) {
        checkProcessFlag = "SELECT COUNT(*) FROM NFS_ADJ_REPORT WHERE  date_format(FILEDATE,'%d%m%y')=date_format(date_format(? ,'%d%m%y')) and ADJTYPE like'%" + 
          beanObj.getAdjType() + "%'";
        procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
      } else {
        checkProcessFlag = "SELECT COUNT(*) FROM NFS_ADJ_REPORT WHERE  date_format(FILEDATE,'%d%m%y')=date_format(date_format(? ,'%d%m%y')) and ADJTYPE = ?";
        procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, 
            new Object[] { beanObj.getDatepicker(), beanObj.getAdjType() }, Integer.class)).intValue();
      } 
      this.logger.info("checkProcessFlag sql  " + checkProcessFlag + "  " + procCount);
      this.logger.info("Already Process count " + procCount);
      if (procCount > 0)
        return true; 
      return false;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
  
  public boolean checkAdjTTUMProcessREPORTICD(NFSSettlementBean beanObj) {
    try {
      int procCount = 0;
      String checkProcessFlag = "";
      if (beanObj.getAdjType().equalsIgnoreCase("PENALTY")) {
        checkProcessFlag = "SELECT COUNT(*) FROM ICD_ADJ_REPORT WHERE  date_format(FILEDATE,'%d%m%y')=date_format(date_format(? ,'%d%m%y')) and ADJTYPE like'%" + 
          beanObj.getAdjType() + "%'";
        procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
      } else {
        checkProcessFlag = "SELECT COUNT(*) FROM ICD_ADJ_REPORT WHERE  date_format(FILEDATE,'%d%m%y')=date_format(date_format(? ,'%d%m%y')) and ADJTYPE = ?";
        procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, 
            new Object[] { beanObj.getDatepicker(), beanObj.getAdjType() }, Integer.class)).intValue();
      } 
      this.logger.info("checkProcessFlag sql  " + checkProcessFlag + "  " + procCount);
      this.logger.info("Already Process count " + procCount);
      if (procCount > 0)
        return true; 
      return false;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
  
  public boolean checkAdjTTUMProcessSETTL(NFSSettlementBean beanObj) {
    try {
      int procCount = 0;
      String checkProcessFlag = "";
      if (beanObj.getAdjCategory().contains("ICD")) {
        checkProcessFlag = "SELECT count(*) FROM ICD_SETTLEMENT_DAILY_TTUM WHERE date_format(FILEDATE,'%d%m%y')=date_format(date_format(? ,'%d%m%y'))";
      } else {
        checkProcessFlag = "SELECT count(*) FROM NFS_SETTLEMENT_DAILY_TTUM WHERE date_format(FILEDATE,'%d%m%y')=date_format(date_format(? ,'%d%m%y'))";
      } 
      this.logger.info("checkProcessFlag sql  " + checkProcessFlag);
      procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
      this.logger.info("Already Process count " + procCount);
      if (procCount > 0)
        return true; 
      return false;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
  
  public boolean checkAdjTTUMProcess(NFSSettlementBean beanObj) {
    try {
      int procCount = 0;
      String checkProcessFlag = "";
      if (beanObj.getAdjType().equalsIgnoreCase("PENALTY")) {
        checkProcessFlag = "SELECT COUNT(*) as DATA_COUNT  FROM NFS_ADJUSTMENT_RAWDATA WHERE date_format(FILEDATE,'%d%m%y') =date_format('" + 
          beanObj.getDatepicker() + "' ,'%d%m%y') AND \r\n" + 
          "    date_format(CONVTODATE(ADJSETTLEMENTDATE),'%d%m%y')=date_format('" + beanObj.getDatepicker() + 
          "' ,'%d%m%y') AND ISR='UOB' AND TO_NUMBER(CUSTOMERPENALTY) <> 0";
        procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, new Object[0], Integer.class)).intValue();
      } else {
        checkProcessFlag = "SELECT COUNT(*) FROM NFS_ADJUSTMENT_TTUM WHERE date_format(FILEDATE,'%d%m%y')=date_format(date_format(? ,'%d%m%y')) and UPPER(ADJTYPE) = ?";
        procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, 
            new Object[] { beanObj.getDatepicker(), beanObj.getAdjType() }, Integer.class)).intValue();
      } 
      this.logger.info("checkProcessFlag sql  " + checkProcessFlag);
      this.logger.info("Already Process count " + procCount);
      if (procCount > 0)
        return true; 
      return false;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
  
  public boolean checkAdjTTUMProcessICD(NFSSettlementBean beanObj) {
    try {
      int procCount = 0;
      String checkProcessFlag = "";
      if (beanObj.getAdjType().equalsIgnoreCase("PENALTY")) {
        checkProcessFlag = "SELECT COUNT(*) as DATA_COUNT  FROM ICD_ADJUSTMENT_RAWDATA WHERE date_format(FILEDATE,'%d%m%y') =date_format('" + 
          beanObj.getDatepicker() + "' ,'%d%m%y') AND \r\n" + 
          "    date_format(CONVTODATE(ADJSETTLEMENTDATE),'%d%m%y')=date_format('" + beanObj.getDatepicker() + 
          "' ,'%d%m%y') AND ISR='UOB' AND TO_NUMBER(CUSTOMERPENALTY) <> 0";
        procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, new Object[0], Integer.class)).intValue();
      } else {
        checkProcessFlag = "SELECT COUNT(*) FROM ICD_ADJUSTMENT_TTUM WHERE date_format(FILEDATE,'%d%m%y')=date_format(date_format(? ,'%d%m%y')) and UPPER(ADJTYPE) = ?";
        procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, 
            new Object[] { beanObj.getDatepicker(), beanObj.getAdjType() }, Integer.class)).intValue();
      } 
      this.logger.info("checkProcessFlag sql  " + checkProcessFlag);
      this.logger.info("Already Process count " + procCount);
      if (procCount > 0)
        return true; 
      return false;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
  
  public HashMap<String, Object> ValidateCooperativeBank(NFSSettlementBean beanObj) {
    HashMap<String, Object> result = new HashMap<>();
    int rawCount = 0;
    List<String> subcategories = new ArrayList<>();
    subcategories.add("ISSUER");
    subcategories.add("ACQUIRER");
    int uploadCount = 0;
    int file_id = 0;
    try {
      try {
        file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getFileName(), beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
      } catch (Exception e) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "File is not Configured!");
        return result;
      } 
      System.out.println("File id is " + file_id);
      String checkFileUploaded = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ?  AND FILEDATE = date_format(? ,'dd-mm-rr') AND CYCLE = ?";
      uploadCount = ((Integer)getJdbcTemplate().queryForObject(checkFileUploaded, 
          new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker(), Integer.valueOf(beanObj.getCycle()) }, Integer.class)).intValue();
      System.out.println("Upload count " + uploadCount);
      if (uploadCount > 0) {
        String alreadyProc = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE SETTLEMENT_FLAG = 'Y' AND FILEID = ? AND FILEDATE = date_format(? ,'dd-mm-rr') AND CYCLE = ?";
        int procCount = ((Integer)getJdbcTemplate().queryForObject(alreadyProc, 
            new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker(), Integer.valueOf(beanObj.getCycle()) }, Integer.class)).intValue();
        System.out.println("Already Process count " + procCount);
        if (procCount == 0) {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Please process Settlement first!");
        } else {
          result.put("result", Boolean.valueOf(true));
        } 
      } else {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "NTSL File is not uploaded for Selected Date!");
      } 
    } catch (Exception e) {
      System.out.println("Exception in ValidateDailySettProcess " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred while Validating");
    } 
    return result;
  }
  
  public boolean checkCoopTTUMProcess(NFSSettlementBean beanObj) {
    try {
      int file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
          new Object[] { beanObj.getFileName(), beanObj.getCategory(), "-" }, Integer.class)).intValue();
      String checkProcessFlag = "SELECT COUNT(*) FROM NFS_COOPERATIVE_BANK_TTUM WHERE CYCLE = ? AND FILEDATE = ?";
      int procCount = ((Integer)getJdbcTemplate().queryForObject(checkProcessFlag, 
          new Object[] { Integer.valueOf(beanObj.getCycle()), beanObj.getDatepicker() }, Integer.class)).intValue();
      this.logger.info("Already Process count " + procCount);
      if (procCount > 0) {
        String deleteQuery = "DELETE FROM NFS_COOPERATIVE_BANK_TTUM WHERE FILEDATE ='" + beanObj.getDatepicker() + 
          "' AND CYCLE = '" + beanObj.getCycle() + "'";
        getJdbcTemplate().execute(deleteQuery);
      } 
      return true;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
  
  public HashMap<String, Object> validateSettDifference(NFSSettlementBean beanObj) {
    HashMap<String, Object> result = new HashMap<>();
    String updateAmount = null;
    try {
      String checkDiffTab = "select COUNT(*) from NFS_SETTLEMENT_REPORT_DIFF WHERE FILEDATE = ? AND CYCLE = ? AND RECTIFIED = 'N'";
      int getcount = ((Integer)getJdbcTemplate().queryForObject(checkDiffTab, 
          new Object[] { beanObj.getDatepicker(), Integer.valueOf(beanObj.getCycle()) }, Integer.class)).intValue();
      if (getcount > 0) {
        if (beanObj.getFileName().contains("NFS")) {
          String getDiffCol = "select CASE WHEN DEBIT = 'Y' THEN 'DEBIT' ELSE 'CREDIT' END AS DIFF_COL from nfs_settlement_report_diff where (NO_OF_TXNS = 'Y' OR DEBIT = 'Y' OR CREDIT = 'Y') and filedate = ? and cycle = ?";
          String colName = (String)getJdbcTemplate().queryForObject(getDiffCol, 
              new Object[] { beanObj.getDatepicker(), Integer.valueOf(beanObj.getCycle()) }, String.class);
          this.logger.info("Diff Column name is " + colName);
          String getDiffAmt = "select T2." + colName + " - T1." + colName + 
            " from nfs_settlement_report t1, ntsl_nfs_rawdata t2 WHERE t1.FILEDATE = T2.FILEDATE AND T1.CYCLE = T2.CYCLE " + 
            "AND t1.DESCRIPTION = T2.DESCRIPTION AND T1.FILEDATE = ? " + 
            "AND T1.CYCLE = ? AND T1.DESCRIPTION ='Final Settlement Amount' ";
          Double diffAmount = (Double)getJdbcTemplate().queryForObject(getDiffAmt, 
              new Object[] { beanObj.getDatepicker(), Integer.valueOf(beanObj.getCycle()) }, Double.class);
          if (diffAmount.doubleValue() > 0.99D) {
            result.put("result", Boolean.valueOf(false));
            result.put("msg", "Difference amount is greater than 1");
            return result;
          } 
          if (diffAmount.doubleValue() != Double.parseDouble(beanObj.getRectAmt())) {
            result.put("result", Boolean.valueOf(false));
            result.put("msg", "Difference amount and entered amount is different!");
            return result;
          } 
          updateAmount = "update nfs_settlement_report set " + colName + " = " + colName + " +" + 
            beanObj.getRectAmt() + " WHERE FILEDATE = '" + beanObj.getDatepicker() + 
            "' AND CYCLE = '" + beanObj.getCycle() + 
            "' AND DESCRIPTION ='Final Settlement Amount'";
          this.logger.info("UpdateAmount query is " + updateAmount);
          getJdbcTemplate().execute(updateAmount);
          String updateTable = "update NFS_SETTLEMENT_REPORT_DIFF set RECTIFIED = 'Y' WHERE FILEDATE = '" + 
            beanObj.getDatepicker() + "' AND CYCLE = '" + beanObj.getCycle() + "'";
          getJdbcTemplate().execute(updateTable);
          result.put("result", Boolean.valueOf(true));
          result.put("msg", "Amount is Rectified!");
          return result;
        } 
      } else {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "Selected Date does not have difference!");
      } 
    } catch (Exception e) {
      this.logger.info("Exception occurred in validateSettDifference " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception occured in Updating!");
      return result;
    } 
    return result;
  }
  
  public boolean addCooperativeBank(String bankName, String accNumber) {
    try {
      String insertData = "INSERT INTO main_cooperative_master (BANK_NAME,ACCOUNT_NUMB) VALUES('" + bankName + 
        "','" + accNumber + "')";
      getJdbcTemplate().execute(insertData);
      return true;
    } catch (Exception e) {
      return false;
    } 
  }
  
  public List<String> getNodalData(String state) {
    List<String> data = new ArrayList<>();
    try {
      String getNodalId = "select DISTINCT NODAL_SOL_ID from nodal_sol_master where state = ?";
      String nodalId = (String)getJdbcTemplate().queryForObject(getNodalId, new Object[] { state }, String.class);
      this.logger.info("Nodal Sol ID " + nodalId);
      data.add(nodalId);
      String getNodalPh = "select DISTINCT NODAL_PH from nodal_sol_master where state = ?";
      String nodalPh = (String)getJdbcTemplate().queryForObject(getNodalPh, new Object[] { state }, String.class);
      this.logger.info("Nodal Ph " + nodalPh);
      data.add(nodalPh);
    } catch (Exception e) {
      this.logger.info("Exception in getNodalData" + e);
    } 
    return data;
  }
  
  public boolean SaveNodalDetails(AddNewSolBean beanObj) {
    try {
      String InsertQuery = "INSERT INTO nodal_sol_master(SOL_ID,STATE,GSTIN,NODAL_PH,NODAL_ACCOUNT_NUMBER,INCOME_PH,INCOME_ACCOUNT_NUMBER,CREATEDBY,CREATEDDATE) VALUES('" + 
        beanObj.getSolId() + "','" + beanObj.getState() + "','" + beanObj.getGstin() + "','" + 
        beanObj.getNodalPh() + "','" + beanObj.getNodalAccNo() + "','" + beanObj.getIncomePH() + "','" + 
        beanObj.getIncomeAccNo() + "','" + beanObj.getCreatedBy() + "',sysdate)";
      getJdbcTemplate().execute(InsertQuery);
      return true;
    } catch (Exception e) {
      this.logger.info("Exception in SaveNodalDetails " + e);
      return false;
    } 
  }
  
  public HashMap<String, Object> CheckSettlementProcess(NFSSettlementBean beanObj) {
    HashMap<String, Object> result = new HashMap<>();
    String checkSettProcess = "";
    String checkFlag = "";
    String tableName = "";
    try {
      if (beanObj.getFileName().contains("NFS")) {
        checkSettProcess = "select settlement_flag from main_Settlement_file_upload where filedate = ? and cycle = ? and fileid = (select fileid from main_filesource where file_category = 'NFS_SETTLEMENT' AND FILENAME = ?)";
        checkFlag = (String)getJdbcTemplate().queryForObject(checkSettProcess, 
            new Object[] { beanObj.getDatepicker(), Integer.valueOf(beanObj.getCycle()), beanObj.getFileName() }, String.class);
        if (checkFlag.equalsIgnoreCase("Y")) {
          result.put("result", Boolean.valueOf(true));
        } else {
          result.put("result", Boolean.valueOf(true));
        } 
      } else {
        if (beanObj.getFileName().contains("PBGB")) {
          tableName = "PBGB_SETTLEMENT_REPORT";
        } else if (beanObj.getFileName().contains("DFS")) {
          tableName = "DFS_SETTLEMENT_REPORT";
        } else if (beanObj.getFileName().contains("JCB")) {
          tableName = "JCB_SETTLEMENT_REPORT";
        } 
        checkSettProcess = "SELECT COUNT(*) from " + tableName + " where filedate = date_format(?,'dd/mm/yyyy')";
        int settCount = ((Integer)getJdbcTemplate().queryForObject(checkSettProcess, 
            new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
        if (settCount == 0) {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Settlement is not processed");
        } else {
          result.put("result", Boolean.valueOf(true));
        } 
      } 
    } catch (Exception e) {
      this.logger.info("Exception in checking settlement process " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception while checking settlement process");
    } 
    return result;
  }
  
  public HashMap<String, Object> ValidateOtherSettProcess(NFSSettlementBean beanObj) {
    HashMap<String, Object> result = new HashMap<>();
    String tableName = "";
    String reportTable = "";
    try {
      if (beanObj.getFileName().contains("PBGB")) {
        tableName = "NFS_NFS_ACQ_RAWDATA";
        reportTable = "PBGB_SETTLEMENT_REPORT";
      } else if (beanObj.getFileName().contains("DFS")) {
        tableName = "nfs_dfs_acq_rawdata";
        reportTable = "DFS_SETTLEMENT_REPORT";
      } else {
        tableName = "nfs_jcb_acq_rawdata";
        reportTable = "JCB_SETTLEMENT_REPORT";
      } 
      String rawQuery = "SELECT COUNT(*) FROM " + tableName + " where filedate = date_format(?,'%d%m%Y')";
      int rawCount = ((Integer)getJdbcTemplate().queryForObject(rawQuery, new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
      if (rawCount == 0) {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "Raw Files are not uploaded");
      } else {
        String alreadyProcQuery = "SELECT COUNT(*) FROM " + reportTable + 
          " where filedate = date_format(?,'%d%m%Y')";
        int procCount = ((Integer)getJdbcTemplate().queryForObject(alreadyProcQuery, 
            new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
        if (procCount > 0) {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Settlement is already processed");
        } else {
          result.put("result", Boolean.valueOf(true));
        } 
      } 
    } catch (Exception e) {
      this.logger.info("Exception is " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception occurred while validating");
    } 
    return result;
  }
  
  public boolean checkIccwFileUpload(CompareSetupBean setupBean) {
    String rawQuery = "";
    int rawCount = 0;
    if (setupBean.getFilename().equalsIgnoreCase("SWITCH")) {
      rawQuery = "SELECT COUNT(*) FROM ICCW_SWITCH_DATA_CUB where filedate = '" + setupBean.getFileDate() + "'";
    } else if (setupBean.getFilename().equalsIgnoreCase("CBS")) {
      if (setupBean.getFileType().equalsIgnoreCase("CBS")) {
        rawQuery = "SELECT COUNT(*) FROM ICCW_CBS_DATA_CUB where filedate = '" + setupBean.getFileDate() + "'";
      } else if (setupBean.getFileType().equalsIgnoreCase("REVERSAL")) {
        rawQuery = "SELECT COUNT(*) FROM ICCW_CBS_DATA_CUB_REV where filedate = '" + setupBean.getFileDate() + 
          "'";
      } 
    } 
    rawCount = ((Integer)getJdbcTemplate().queryForObject(rawQuery, new Object[0], Integer.class)).intValue();
    if (rawCount > 0)
      return true; 
    return false;
  }
}
