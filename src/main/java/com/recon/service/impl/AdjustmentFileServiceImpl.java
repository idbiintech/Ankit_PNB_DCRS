package com.recon.service.impl;


import com.recon.model.NFSSettlementBean;
import com.recon.service.AdjustmentFileService;
import com.recon.util.ReadNFSAdjustmentFile;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.web.multipart.MultipartFile;

public class AdjustmentFileServiceImpl extends JdbcDaoSupport implements AdjustmentFileService {
  private static final Logger logger = Logger.getLogger(com.recon.service.impl.AdjustmentFileServiceImpl.class);
  
  public HashMap<String, Object> validateAdjustmentFileUpload(NFSSettlementBean beanObj) {
    HashMap<String, Object> validate = new HashMap<>();
    try {
      System.out.println("beanObj.getCreatedBy() " + beanObj.getCreatedBy() + " beanObj.getCategory() " + 
          beanObj.getCategory() + "beanObj.getStSubCategory() " + beanObj.getStSubCategory());
      if (beanObj.getCreatedBy().equalsIgnoreCase("NTSL-DFS")) {
        int file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getCreatedBy(), beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
        logger.info("File id is " + file_id);
        int filecount = ((Integer)getJdbcTemplate().queryForObject(
            "SELECT COUNT(*) FROM dfs_adjustment_rawdata  WHERE FILENAME = ? AND FILEDATE = STR_to_date(?,'%Y/%m/%d')", 
            new Object[] { beanObj.getFileName(), beanObj.getDatepicker() }, Integer.class)).intValue();
        logger.info("Filecount is" + filecount);
        if (file_id == 16 && filecount > 0) {
          validate.put("result", Boolean.valueOf(false));
          validate.put("msg", "File is already uploaded!!!");
        } else {
          validate.put("result", Boolean.valueOf(true));
          validate.put("msg", "upload ");
        } 
      } else if (beanObj.getCreatedBy().equalsIgnoreCase("NTSL-JCB")) {
        int file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getCreatedBy(), beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
        logger.info("File id is " + file_id);
        int filecount = ((Integer)getJdbcTemplate().queryForObject(
            "SELECT COUNT(*) FROM jcb_adjustment_rawdata  WHERE FILENAME = ? AND FILEDATE= STR_to_date(?,'%Y/%m/%d')", 
            new Object[] { beanObj.getFileName(), beanObj.getDatepicker() }, Integer.class)).intValue();
        logger.info("Filecount is" + filecount);
        if (file_id == 16 && filecount > 0) {
          validate.put("result", Boolean.valueOf(false));
          validate.put("msg", "File is already uploaded!!!");
        } else {
          validate.put("result", Boolean.valueOf(true));
          validate.put("msg", "upload ");
        } 
      } else if (beanObj.getCreatedBy().equalsIgnoreCase("ICD")) {
        int file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getCreatedBy(), beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
        logger.info("File id is " + file_id);
        int filecount = ((Integer)getJdbcTemplate().queryForObject(
            "SELECT COUNT(*) FROM icd_adjustment_rawdata WHERE FILENAME = ? AND FILEDATE = STR_to_date(?,'%Y/%m/%d')", 
            new Object[] { beanObj.getFileName(), beanObj.getDatepicker() }, Integer.class)).intValue();
        logger.info("Filecount is" + filecount);
        if (file_id == 16 && filecount > 0) {
          validate.put("result", Boolean.valueOf(false));
          validate.put("msg", "File is already uploaded!!!");
        } else {
          validate.put("result", Boolean.valueOf(true));
          validate.put("msg", "upload ");
        } 
      } else {
        int file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getCreatedBy(), beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
        logger.info("File id is " + file_id);
        int filecount = ((Integer)getJdbcTemplate().queryForObject(
            "SELECT COUNT(*) FROM nfs_adjustment_rawdata WHERE FILENAME = ? AND FILEDATE =STR_to_date(?,'%Y/%m/%d')", 
            new Object[] { beanObj.getFileName(), beanObj.getDatepicker() }, Integer.class)).intValue();
        logger.info("Filecount is" + filecount);
        if (file_id == 16 && filecount > 0) {
          validate.put("result", Boolean.valueOf(false));
          validate.put("msg", "File is already uploaded!!!");
        } else {
          validate.put("result", Boolean.valueOf(true));
          validate.put("msg", "upload ");
        } 
      } 
    } catch (Exception e) {
      logger.info("Exception is " + e);
      validate.put("result", Boolean.valueOf(false));
      validate.put("msg", "Exception Occured!!");
    } 
    return validate;
  }
  
  public HashMap<String, Object> uploadAdjustmentFile(NFSSettlementBean beanObj, MultipartFile file) {
    HashMap<String, Object> mapObj = new HashMap<>();
    try {
      ReadNFSAdjustmentFile nfsAdjRawData = new ReadNFSAdjustmentFile();
      mapObj = nfsAdjRawData.fileupload(beanObj, file, getConnection());
      System.out.println("result is " + mapObj);
      boolean result = ((Boolean)mapObj.get("result")).booleanValue();
      int count = ((Integer)mapObj.get("count")).intValue();
      if (result) {
        if (beanObj.getCreatedBy().equalsIgnoreCase("ICD")) {
          beanObj.setFileName("ICD");
          beanObj.setCategory("ICD-ADJUSTMENT");
          beanObj.setStSubCategory("-");
        } else if (beanObj.getCreatedBy().equalsIgnoreCase("NTSL-DFS")) {
          beanObj.setFileName("NTSL-DFS");
          beanObj.setCategory("DFS_ADJUSTMENT");
          beanObj.setStSubCategory("-");
        } else if (beanObj.getCreatedBy().equalsIgnoreCase("NTSL-JCB")) {
          beanObj.setFileName("NTSL-JCB");
          beanObj.setCategory("JCB_ADJUSTMENT");
          beanObj.setStSubCategory("-");
        } else {
          beanObj.setFileName("NTSL-NFS");
          beanObj.setCategory("NFS_ADJUSTMENT");
          beanObj.setStSubCategory("-");
        } 
        beanObj.setStSubCategory("-");
        System.out.println("details " + beanObj.getFileName() + " " + beanObj.getCategory() + " " + 
            beanObj.getStSubCategory());
        int file_id = ((Integer)getJdbcTemplate().queryForObject("SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?", 
            new Object[] { beanObj.getFileName(), beanObj.getCategory(), beanObj.getStSubCategory() }, Integer.class)).intValue();
        System.out.println("File id is " + file_id);
        String insertData = "INSERT INTO main_settlement_file_upload(FILEID, FILEDATE, UPLOADBY, UPLOADDATE, CATEGORY, UPLOAD_FLAG, FILE_SUBCATEGORY,CYCLE,SETTLEMENT_FLAG,INTERCHANGE_FLAG,FILE_COUNT) VALUES('" + 
          file_id + "',STR_to_date('" + beanObj.getDatepicker() + "','%Y/%m/%d'),'" + 
          beanObj.getCreatedBy() + "',sysdate(),'" + beanObj.getCategory() + "','Y','-'," + 
          beanObj.getCycle() + ",'N','N','1')";
        System.out.println("insert query " + insertData);
        getJdbcTemplate().execute(insertData);
        mapObj.put("result", Boolean.valueOf(true));
      } else {
        mapObj.put("result", Boolean.valueOf(false));
        mapObj.put("count", Integer.valueOf(count));
      } 
      return mapObj;
    } catch (Exception e) {
      System.out.println("Exception is " + e);
      mapObj.put("result", Boolean.valueOf(false));
      mapObj.put("count", Integer.valueOf(0));
      return mapObj;
    } 
  }
}
