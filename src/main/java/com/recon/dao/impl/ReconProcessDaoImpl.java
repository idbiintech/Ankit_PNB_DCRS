package com.recon.dao.impl;


import com.google.gson.Gson;
import com.recon.dao.IReconProcessDao;
import com.recon.model.CompareBean;
import com.recon.model.CompareSetupBean;
import com.recon.service.CompareRupayService;
import com.recon.service.CompareService;
import com.recon.service.FilterationService;
import com.recon.service.ICompareConfigService;
import com.recon.util.GeneralUtil;
import com.recon.util.demo;

import oracle.jdbc.OracleTypes;

import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;

@Component
public class ReconProcessDaoImpl extends JdbcDaoSupport implements IReconProcessDao {
  @Autowired
  FilterationService filterationService;
  
  @Autowired
  GeneralUtil generalUtil;
  
  @Autowired
  CompareService compareService;
  
  @Autowired
  CompareRupayService compareRupayService;
  
  @Autowired
  ICompareConfigService icompareConfigService;
  
  private static final Logger logger = Logger.getLogger(com.recon.dao.impl.ReconProcessDaoImpl.class);
  
  public String chkFileUpload(String Category, String filedate, List<CompareSetupBean> compareSetupBeans, String subCat) throws Exception {
    logger.info("***** ReconProcessDaoImpl.chkFileUpload Start ****" + Category);
    String msg = null;
    try {
      for (CompareSetupBean setupBean : compareSetupBeans) {
        if (setupBean.getInFileId() != 8 && Category.equalsIgnoreCase("INTERNATIONAL") && 
          !setupBean.getStFileName().equalsIgnoreCase("REV_REPORT")) {
          String query = "SELECT distinct UPLOAD_FLAG FROM main_file_upload_dtls where  filedate = STR_TO_DATE('" + filedate + "','%Y/%m/%d')  AND FileId = " + setupBean.getInFileId();
          query = " SELECT CASE WHEN exists (" + query + ") then (" + query + 
            ") else 'N' end as FLAG from dual";
          logger.info("Query==" + query);
          String flg = (String)getJdbcTemplate().queryForObject(query, String.class);
          query = "";
          query = "SELECT distinct COMAPRE_FLAG FROM main_file_upload_dtls where  filedate = STR_TO_DATE('" + filedate + "','%Y/%m/%d')  AND FileId = " + setupBean.getInFileId();
          query = " SELECT CASE WHEN exists (" + query + ") then (" + query + 
            ") else 'N' end as FLAG from dual";
          logger.info("compareflag" + query);
          String compareflag = (String)getJdbcTemplate().queryForObject(query, String.class);
          logger.info("compareflagdf " + compareflag);
          if (compareflag.equalsIgnoreCase("N")) {
            if (flg.equalsIgnoreCase("N")) {
              msg = "Files are Not Uploaded.";
              logger.info("msg==" + msg);
              return msg;
            } 
            if (setupBean.getStFileName().equalsIgnoreCase("CBS")) {
              String MANUAL_FILE_CHECK = "SELECT MANUPLOAD_FLAG FROM main_file_upload_dtls where  filedate = STR_TO_DATE('" + filedate + "','%Y/%m/%d')  AND FileId = " + setupBean.getInFileId();
              MANUAL_FILE_CHECK = " SELECT CASE WHEN exists (" + MANUAL_FILE_CHECK + ") then (" + 
                MANUAL_FILE_CHECK + ") else 'N' end as FLAG from dual";
              logger.info("MANUAL_FILE_CHECK== " + MANUAL_FILE_CHECK);
              flg = (String)getJdbcTemplate().queryForObject(MANUAL_FILE_CHECK, String.class);
              if (setupBean.getInFileId() == 39)
                flg = "Y"; 
              if (flg.equalsIgnoreCase("N")) {
                msg = "Manual File is not uploaded";
                logger.info("msg==" + msg);
                return msg;
              } 
            } 
            continue;
          } 
          msg = "Files are Already Processed.";
          logger.info("msg==" + msg);
          return msg;
        } 
      } 
      logger.info("***** ReconProcessDaoImpl.chkFileUpload End ****");
    } catch (Exception ex) {
      logger.error(" error in ReconProcessDaoImpl.chkFileUpload", 
          new Exception("ReconProcessDaoImpl.chkFileUpload", ex));
      throw ex;
    } 
    return msg;
  }
  
  public List<CompareSetupBean> getFileList(String category, String filedate, String subcat) throws Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.getFileList Start ****");
      List<CompareSetupBean> compareSetupBeans = new ArrayList<>();
      String query = "";
      String stSubCate = "";
      if (category.equalsIgnoreCase("VISACROSS")) {
        category = "VISA";
        if (subcat.equalsIgnoreCase("POS INT DOM"))
          subcat = " "; 
      } 
      if (category.contentEquals("DFS")) {
        query = "Select FileId as inFileId , Filename as stFileName,FILTERATION as filter_Flag,KNOCKOFF as knockoff_Flag,FILE_SUBCATEGORY AS stSubCategory FROM main_filesource WHERE FILE_CATEGORY = 'DFS' and  FILENAME !='NTSL-DFS'";
      } else if (category.contentEquals("JCB")) {
        query = "Select FileId as inFileId , Filename as stFileName,FILTERATION as filter_Flag,KNOCKOFF as knockoff_Flag,FILE_SUBCATEGORY AS stSubCategory FROM main_filesource WHERE FILE_CATEGORY = 'JCB' and  FILENAME !='NTSL-JCB'";
      } else {
        query = "Select FileId as inFileId , Filename as stFileName,FILTERATION as filter_Flag,KNOCKOFF as knockoff_Flag,FILE_SUBCATEGORY AS stSubCategory FROM main_filesource WHERE  FILE_CATEGORY = '" + 
          category + "' and FILE_SUBCATEGORY in ('" + 
          subcat + "')" + 
          " order by (case  when stFileName = 'SWITCH' then 1 when stFileName ='CBS' then 2  end) ASC";
      } 
      logger.info("FILE ID== " + query);
      compareSetupBeans = getJdbcTemplate().query(query, (RowMapper)new BeanPropertyRowMapper(CompareSetupBean.class));
      Gson g = new Gson();
      System.out.println(g.toJson(compareSetupBeans));
      return compareSetupBeans;
    } catch (Exception ex) {
      demo.logSQLException(ex, "ReconProcessDaoImpl.getFileList");
      logger.error(" error in ReconProcessDaoImpl.getFileList", 
          new Exception("ReconProcessDaoImpl.getFileList", ex));
      return null;
    } 
  }
  
  public List<CompareSetupBean> getFileList2(String category, String filedate, String subcat, String type) throws Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.getFileList Start ****");
      List<CompareSetupBean> compareSetupBeans = new ArrayList<>();
      String query = "";
      String stSubCate = "";
      if (subcat.equalsIgnoreCase("ISSUER_ATM")) {
        query = "Select FileId as inFileId , Filename as stFileName,FILTERATION as filter_Flag,KNOCKOFF as knockoff_Flag,FILE_SUBCATEGORY AS stSubCategory from main_filesource where  FILE_CATEGORY = 'MASTERCARD' and FILE_SUBCATEGORY='" + 
          subcat + "' and FILENAME !='POS'\r\n";
      } else if (subcat.equalsIgnoreCase("ACQUIRER_DOM")) {
        query = "Select FileId as inFileId , Filename as stFileName,FILTERATION as filter_Flag,KNOCKOFF as knockoff_Flag,FILE_SUBCATEGORY AS stSubCategory from main_filesource where  FILE_CATEGORY = 'MASTERCARD' and FILE_SUBCATEGORY='" + 
          subcat + "' and FILENAME !='POS' and  FILEID !='40'\r\n";
      } else {
        query = "Select FileId as inFileId , Filename as stFileName,FILTERATION as filter_Flag,KNOCKOFF as knockoff_Flag,FILE_SUBCATEGORY AS stSubCategory from main_filesource where  FILE_CATEGORY = 'MASTERCARD' and FILE_SUBCATEGORY='" + 
          subcat + "' and FILENAME !='ATM'\r\n";
      } 
      logger.info("FILE ID== " + query);
      compareSetupBeans = getJdbcTemplate().query(query, (RowMapper)new BeanPropertyRowMapper(CompareSetupBean.class));
      Gson g = new Gson();
      System.out.println(g.toJson(compareSetupBeans));
      return compareSetupBeans;
    } catch (Exception ex) {
      demo.logSQLException(ex, "ReconProcessDaoImpl.getFileList");
      logger.error(" error in ReconProcessDaoImpl.getFileList", 
          new Exception("ReconProcessDaoImpl.getFileList", ex));
      return null;
    } 
  }
  
  public String checkReconAlreadyProcessed(String category, List<CompareSetupBean> compareSetupBeans, String filedate) {
    String msg = null;
    try {
      for (CompareSetupBean setupBean : compareSetupBeans) {
        if (setupBean.getInFileId() != 8 && category.equalsIgnoreCase("INTERNATIONAL")) {
          String getcompareFlag = "select comapre_flag from main_file_upload_dtls where  filedate = STR_TO_DATE('" + filedate + "','%Y/%m/%d') and fileid = '" + setupBean.getInFileId() + "'";
          logger.info("getcompareFlag " + getcompareFlag);
          String compareFlag = (String)getJdbcTemplate().queryForObject(getcompareFlag, new Object[0], 
              String.class);
          logger.info("compareFlag " + compareFlag);
          if (compareFlag.equals("Y")) {
            msg = "Recon is already processed";
            return msg;
          } 
          continue;
        } 
        if (setupBean.getInFileId() != 8) {
          String getcompareFlag = "select comapre_flag from main_file_upload_dtls where  filedate = STR_TO_DATE('" + filedate + "','%Y/%m/%d') and fileid = '" + setupBean.getInFileId() + "'";
          logger.info("getcompareFlag " + getcompareFlag);
          String compareFlag = (String)getJdbcTemplate().queryForObject(getcompareFlag, new Object[0], 
              String.class);
          logger.info("compareFlag " + compareFlag);
          if (compareFlag.equals("Y")) {
            msg = "Recon is already processed";
            return msg;
          } 
        } 
      } 
    } catch (Exception e) {
      logger.error("Exception while checking recon process " + e);
      msg = "File not uploaded";
    } 
    return msg;
  }
  
  public String validateFile1(String category, List<CompareSetupBean> compareSetupBeans, String filedate) {
    logger.info("***** ReconProcessDaoImpl.validateFile Start ****");
    String msg = null;
    try {
      String error_msg = checkReconAlreadyProcessed(category, compareSetupBeans, filedate);
      logger.info("error_msg " + error_msg + category);
      if (error_msg == null) {
        String checkFirstTime = "select count(*) from main_file_upload_dtls where filedate < STR_TO_DATE(?,'%Y/%m/%d')";
        int prevCount = ((Integer)getJdbcTemplate().queryForObject(checkFirstTime, new Object[] { filedate }, Integer.class)).intValue();
        if (prevCount == 0)
          return null; 
        for (CompareSetupBean setupBean : compareSetupBeans) {
          if (setupBean.getInFileId() != 8 && category.equalsIgnoreCase("INTERNATIONAL")) {
            String query = "select count(*) from main_file_upload_dtls where filedate = STR_TO_DATE('" + filedate + "','%Y/%m/%d')-1 and fileid = '" + setupBean.getInFileId() + 
              "' and filter_flag = (select filteration from main_filesource where fileid = '" + 
              setupBean.getInFileId() + "')" + 
              " and knockoff_flag = (select knockoff from main_filesource where fileid = '" + 
              setupBean.getInFileId() + "') " + " and comapre_flag = 'Y' ";
            logger.info("Query is " + query);
            int count = ((Integer)getJdbcTemplate().queryForObject(query, new Object[0], Integer.class)).intValue();
            logger.info("Count is " + count);
            if (count == 0) {
              msg = "Previous File is not Processed";
              return msg;
            } 
            continue;
          } 
          if (setupBean.getInFileId() != 8) {
            String query = "select count(*) from main_file_upload_dtls where filedate =STR_TO_DATE('" + filedate + "','%Y/%m/%d')- interval 1 day and fileid = '" + setupBean.getInFileId() + 
              "' and filter_flag = (select filteration from main_filesource where fileid = '" + 
              setupBean.getInFileId() + "')" + 
              " and knockoff_flag = (select knockoff from main_filesource where fileid = '" + 
              setupBean.getInFileId() + "') " + " and comapre_flag = 'Y' ";
            logger.info("Query is " + query);
            int count = ((Integer)getJdbcTemplate().queryForObject(query, new Object[0], Integer.class)).intValue();
            logger.info("Count is " + count);
            if (count == 0) {
              msg = "Previous File is not Processed";
              return msg;
            } 
          } 
        } 
      } else {
        return error_msg;
      } 
    } catch (Exception ex) {
      logger.error(" error in ReconProcessDaoImpl.validateFile", 
          new Exception("ReconProcessDaoImpl.validateFile", ex));
    } 
    return msg;
  }
  
  public String validateFile2(String category, List<CompareSetupBean> compareSetupBeans, String filedate) {
    logger.info("***** ReconProcessDaoImpl.validateFile Start ****");
    String msg = null;
    try {
      String error_msg = checkReconAlreadyProcessed(category, compareSetupBeans, filedate);
      logger.info("error_msg " + error_msg + category);
      if (error_msg == null) {
        String checkFirstTime = "select count(*) from main_file_upload_dtls where filedate < to_date(?,'dd-mm-yy')";
        int prevCount = ((Integer)getJdbcTemplate().queryForObject(checkFirstTime, new Object[] { filedate }, Integer.class)).intValue();
        if (prevCount == 0)
          return null; 
        for (CompareSetupBean setupBean : compareSetupBeans) {
          if (setupBean.getInFileId() != 8 && category.equalsIgnoreCase("INTERNATIONAL")) {
            String str = "select count(*) from main_file_upload_dtls where to_date(filedate,'DD-MM-YY') = STR_TO_DATE('" + filedate + "','%Y/%m/%d') and fileid = '" + setupBean.getInFileId() + 
              "' and filter_flag = (select filteration from main_filesource where fileid = '" + 
              setupBean.getInFileId() + "')" + 
              " and knockoff_flag = (select knockoff from main_filesource where fileid = '" + 
              setupBean.getInFileId() + "') " + " and comapre_flag = 'Y' ";
            logger.info("Query is " + str);
            int i = ((Integer)getJdbcTemplate().queryForObject(str, new Object[0], Integer.class)).intValue();
            logger.info("Count is " + i);
            if (i == 0) {
              msg = "Recon not process for Selected date!!";
              return msg;
            } 
            continue;
          } 
          String query = "select count(*) from main_file_upload_dtls where to_date(filedate,'DD-MM-YY') = TO_DATE('" + 
            filedate + "','DD-MM-YY') and fileid = '" + setupBean.getInFileId() + 
            "' and filter_flag = (select filteration from main_filesource where fileid = '" + 
            setupBean.getInFileId() + "')" + 
            " and knockoff_flag = (select knockoff from main_filesource where fileid = '" + 
            setupBean.getInFileId() + "') " + " and comapre_flag = 'Y' ";
          logger.info("Query is " + query);
          int count = ((Integer)getJdbcTemplate().queryForObject(query, new Object[0], Integer.class)).intValue();
          logger.info("Count is " + count);
          if (count == 0) {
            msg = "Recon not process for Selected date!!";
            return msg;
          } 
        } 
      } else {
        return error_msg;
      } 
    } catch (Exception ex) {
      logger.error(" error in ReconProcessDaoImpl.validateFile", 
          new Exception("ReconProcessDaoImpl.validateFile", ex));
    } 
    return msg;
  }
  
  public String validateFile3(String category, List<CompareSetupBean> compareSetupBeans, String filedate) {
    logger.info("***** ReconProcessDaoImpl.validateFile Start ****");
    String msg = null;
    try {
      String checkFirstTime = "", compareFlagCBS = "", checkFirstTimeVISA = "", compareFlagVISA = "";
      if (category.equalsIgnoreCase("POS INT CBS DOM VISA")) {
        checkFirstTime = "SELECT COMAPRE_FLAG  FROM MAIN_FILE_UPLOAD_DTLS  WHERE\r\n    FILEID IN (SELECT FILEID FROM MAIN_FILESOURCE WHERE FILENAME = 'CBS' AND FILE_CATEGORY = 'VISA' AND FILE_SUBCATEGORY = 'ISS INT POS')\r\n    AND TO_DATE(FILEDATE,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')";
        compareFlagCBS = (String)getJdbcTemplate().queryForObject(checkFirstTime, new Object[] { filedate }, String.class);
        checkFirstTimeVISA = "    SELECT COMAPRE_FLAG  FROM MAIN_FILE_UPLOAD_DTLS  WHERE\r\n    FILEID IN (SELECT FILEID FROM MAIN_FILESOURCE WHERE FILENAME = 'VISA' AND FILE_CATEGORY = 'VISA' AND FILE_SUBCATEGORY = 'ISS DOM POS')\r\n    AND TO_DATE(FILEDATE,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')";
        compareFlagVISA = (String)getJdbcTemplate().queryForObject(checkFirstTimeVISA, new Object[] { filedate }, String.class);
      } else if (category.equalsIgnoreCase("ATM INT CBS DOM VISA")) {
        checkFirstTime = "SELECT COMAPRE_FLAG FROM MAIN_FILE_UPLOAD_DTLS  WHERE\r\n    FILEID IN (SELECT FILEID FROM MAIN_FILESOURCE WHERE FILENAME = 'CBS' AND FILE_CATEGORY = 'VISA' AND FILE_SUBCATEGORY = 'ISS INT ATM')\r\n    AND TO_DATE(FILEDATE,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')";
        compareFlagCBS = (String)getJdbcTemplate().queryForObject(checkFirstTime, new Object[] { filedate }, String.class);
        checkFirstTimeVISA = "    SELECT COMAPRE_FLAG FROM MAIN_FILE_UPLOAD_DTLS  WHERE\r\n    FILEID IN (SELECT FILEID FROM MAIN_FILESOURCE WHERE FILENAME = 'VISA' AND FILE_CATEGORY = 'VISA' AND FILE_SUBCATEGORY = 'ISS DOM ATM')\r\n    AND TO_DATE(FILEDATE,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')";
        compareFlagVISA = (String)getJdbcTemplate().queryForObject(checkFirstTimeVISA, new Object[] { filedate }, String.class);
      } else if (category.equalsIgnoreCase("POS DOM CBS INT VISA")) {
        checkFirstTime = "SELECT COMAPRE_FLAG  FROM MAIN_FILE_UPLOAD_DTLS  WHERE\r\n    FILEID IN (SELECT FILEID FROM MAIN_FILESOURCE WHERE FILENAME = 'CBS' AND FILE_CATEGORY = 'VISA' AND FILE_SUBCATEGORY = 'ISS DOM POS')\r\n    AND TO_DATE(FILEDATE,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')";
        compareFlagCBS = (String)getJdbcTemplate().queryForObject(checkFirstTime, new Object[] { filedate }, String.class);
        checkFirstTimeVISA = "    SELECT COMAPRE_FLAG FROM MAIN_FILE_UPLOAD_DTLS  WHERE\r\n    FILEID IN (SELECT FILEID FROM MAIN_FILESOURCE WHERE FILENAME = 'VISA' AND FILE_CATEGORY = 'VISA' AND FILE_SUBCATEGORY = 'ISS INT POS')\r\n    AND TO_DATE(FILEDATE,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')";
        compareFlagVISA = (String)getJdbcTemplate().queryForObject(checkFirstTimeVISA, new Object[] { filedate }, String.class);
      } else if (category.equalsIgnoreCase("ATM DOM CBS INT VISA")) {
        checkFirstTime = "SELECT COMAPRE_FLAG  FROM MAIN_FILE_UPLOAD_DTLS  WHERE\r\n    FILEID IN (SELECT FILEID FROM MAIN_FILESOURCE WHERE FILENAME = 'CBS' AND FILE_CATEGORY = 'VISA' AND FILE_SUBCATEGORY = 'ISS DOM ATM')\r\n    AND TO_DATE(FILEDATE,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')";
        compareFlagCBS = (String)getJdbcTemplate().queryForObject(checkFirstTime, new Object[] { filedate }, String.class);
        checkFirstTimeVISA = "    SELECT COMAPRE_FLAG FROM MAIN_FILE_UPLOAD_DTLS  WHERE\r\n    FILEID IN (SELECT FILEID FROM MAIN_FILESOURCE WHERE FILENAME = 'VISA' AND FILE_CATEGORY = 'VISA' AND FILE_SUBCATEGORY = 'ISS INT ATM')\r\n    AND TO_DATE(FILEDATE,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')";
        compareFlagVISA = (String)getJdbcTemplate().queryForObject(checkFirstTimeVISA, new Object[] { filedate }, String.class);
      } 
      logger.info("compareFlagCBS or compareFlagVISA  " + compareFlagCBS + compareFlagVISA);
      if (compareFlagCBS.equalsIgnoreCase("Y") && compareFlagVISA.equalsIgnoreCase("Y"))
        return null; 
      return "Recon not process!!";
    } catch (Exception ex) {
      logger.error(" error in ReconProcessDaoImpl.validateFile", 
          new Exception("ReconProcessDaoImpl.validateFile", ex));
      return msg;
    } 
  }
  
  public String validateFile4(String category, List<CompareSetupBean> compareSetupBeans, String filedate) {
    logger.info("***** ReconProcessDaoImpl.validateFile Start ****");
    String msg = null;
    try {
      String checkFirstTime = "", compareFlagCBS = "", checkFirstTimeVISA = "", compareFlagVISA = "";
      checkFirstTime = "SELECT FLAG_CBS FROM VISA_CROSS_RECON_STATUS  WHERE CATEGORY = '" + category + "' AND TO_DATE(FILEDATE,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')";
      compareFlagCBS = (String)getJdbcTemplate().queryForObject(checkFirstTime, new Object[] { filedate }, String.class);
      checkFirstTimeVISA = "    SELECT FLAG_VISA FROM VISA_CROSS_RECON_STATUS  WHERE CATEGORY = '" + category + "' AND TO_DATE(FILEDATE,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')";
      compareFlagVISA = (String)getJdbcTemplate().queryForObject(checkFirstTimeVISA, new Object[] { filedate }, String.class);
      logger.info("compareFlagCBS or compareFlagVISA  " + compareFlagCBS + compareFlagVISA);
      if (compareFlagCBS.equalsIgnoreCase("Y") && compareFlagVISA.equalsIgnoreCase("Y"))
        return "CROSS RECON Allready processed!!"; 
      return null;
    } catch (Exception ex) {
      logger.error(" error in ReconProcessDaoImpl.validateFile", 
          new Exception("ReconProcessDaoImpl.validateFile", ex));
      return msg;
    } 
  }
  
  public String validateFile1CTC(String category, List<CompareSetupBean> compareSetupBeans, String filedate) {
    logger.info("***** ReconProcessDaoImpl.validateFile Start ****");
    String msg = null;
    try {
      if (category.equalsIgnoreCase("ACQUIRER")) {
        String str = "select count(*) from SETTLEMENT_NFS_ACQ_C2C_CBS where  TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('" + 
          filedate + "','DD-MM-YY') -1 ";
        logger.info("Query is " + str);
        int i = ((Integer)getJdbcTemplate().queryForObject(str, new Object[0], Integer.class)).intValue();
        logger.info("Count is " + i);
        if (i > 0)
          return null; 
        msg = "Previous File is not Processed";
        return msg;
      } 
      String query = "select count(*) from settlement_nfs_iss_c2c_cbs where  FILEDATE = DATE_FORMAT('" + filedate + "','%Y/%m/%d') - interval 1 day ";
      logger.info("Query is " + query);
      int count = ((Integer)getJdbcTemplate().queryForObject(query, new Object[0], Integer.class)).intValue();
      logger.info("Count is " + count);
      if (count > 0)
      {
    	  String query2 = "select count(*) from settlement_nfs_iss_c2c_cbs where  FILEDATE = DATE_FORMAT('" + filedate + "','%Y/%m/%d') ";
          logger.info("Query is " + query2);
          int count2 = ((Integer)getJdbcTemplate().queryForObject(query2, new Object[0], Integer.class)).intValue();
          logger.info("Count is " + count2);

          if (count2 == 0) {
        	  
        	  return null; 
          }else {
        	  msg = "RECON Processed for selected Date";
              return msg;
        	  
          }
    	  
      }else {
    	  msg = "Previous Day Recon is not Processed";
          return msg;
    	  
      }
    	  
       
      
    
      
    } catch (Exception ex) {
      logger.error(" error in ReconProcessDaoImpl.validateFile", 
          new Exception("ReconProcessDaoImpl.validateFile", ex));
      return msg;
    } 
  }
  
  public String validateFile1CTC2(String category, List<CompareSetupBean> compareSetupBeans, String filedate) {
    logger.info("***** ReconProcessDaoImpl.validateFile Start ****");
    String msg = null;
    try {
    	
    	
      if (category.equalsIgnoreCase("ACQUIRER")) {
        String str = "select count(*) from SETTLEMENT_NFS_ACQ_C2C_CBS where  TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('" + 
          filedate + "','DD-MM-YY')  ";
        logger.info("Query is " + str);
        int i = ((Integer)getJdbcTemplate().queryForObject(str, new Object[0], Integer.class)).intValue();
        logger.info("Count is " + i);
        if (i > 0)
          return null; 
        msg = "Previous File is not Processed";
        return msg;
      }else {
    	  String query = "select count(*) from settlement_nfs_iss_c2c_cbs where FILEDATE = DATE_FORMAT('" + filedate + "','%Y/%m/%d') ";
          logger.info("Query is " + query);
          int count = ((Integer)getJdbcTemplate().queryForObject(query, new Object[0], Integer.class)).intValue();
          logger.info("Count is " + count);
          if (count > 0)
            return null; 
          msg = "Previous File is not Processed";
          return msg;
      }
           
    } catch (Exception ex) {
      logger.error(" error in ReconProcessDaoImpl.validateFile", 
          new Exception("ReconProcessDaoImpl.validateFile", ex));
      return msg;
    } 
  }
  
  public String validateFile1CTC3(String category, List<CompareSetupBean> compareSetupBeans, String filedate) {
	    logger.info("***** ReconProcessDaoImpl.validateFile Start ****");
	    String msg = null;
	    try {
	    	
	    	
	      if (category.equalsIgnoreCase("ACQUIRER")) {
	      String str = "select sum(count) from (SELECT COUNT(*) as count FROM settlement_jcb_cbs whERE filedate = STR_to_date('"+filedate+"','%Y/%m/%d')  union all SELECT COUNT(*) as count FROM settlement_jcb_jcb WHERE filedate = STR_to_date('"+filedate+"','%Y/%m/%d') ) a";

	        logger.info("Query is " + str);
	        int i = ((Integer)getJdbcTemplate().queryForObject(str, new Object[0], Integer.class)).intValue();
	        logger.info("Count is " + i);
	        if (i > 0) {
	       
	          msg = "Recon Already Proccessed";
	        }else {
	        	   return null;
	        }
	        
	        return msg;
	      }else {
	    	  String query = "select count(*) from settlement_nfs_iss_c2c_cbs where FILEDATE = DATE_FORMAT('" + filedate + "','%Y/%m/%d') ";
	          logger.info("Query is " + query);
	          int count = ((Integer)getJdbcTemplate().queryForObject(query, new Object[0], Integer.class)).intValue();
	          logger.info("Count is " + count);
	          if (count > 0)
	            return null; 
	          msg = "Previous File is not Processed";
	          return msg;
	      }
	           
	    } catch (Exception ex) {
	      logger.error(" error in ReconProcessDaoImpl.validateFile", 
	          new Exception("ReconProcessDaoImpl.validateFile", ex));
	      return msg;
	    } 
	  }
  
  public String validateFile1CTC4(String category, List<CompareSetupBean> compareSetupBeans, String filedate) {
	    logger.info("***** ReconProcessDaoImpl.validateFile Start ****");
	    String msg = null;
	    try {
	    	
	    	
	      if (category.equalsIgnoreCase("ACQUIRER")) {
	    	
	  	    String str = "select sum(count) from (SELECT COUNT(*) as count FROM settlement_dfs_cbs whERE filedate = STR_to_date('"+filedate+"','%Y/%m/%d')  union all SELECT COUNT(*) as count FROM settlement_dfs_dfs WHERE filedate = STR_to_date('"+filedate+"','%Y/%m/%d') ) a";

		        logger.info("Query is " + str);
		        int i = ((Integer)getJdbcTemplate().queryForObject(str, new Object[0], Integer.class)).intValue();
		        logger.info("Count is " + i);
		        if (i > 0) {
		       
		          msg = "Recon Already Proccessed";
		        }else {
		        	   return null;
		        }
		        
		        return msg;
	      }else {
	    	  String query = "select count(*) from settlement_nfs_iss_c2c_cbs where FILEDATE = DATE_FORMAT('" + filedate + "','%Y/%m/%d') ";
	          logger.info("Query is " + query);
	          int count = ((Integer)getJdbcTemplate().queryForObject(query, new Object[0], Integer.class)).intValue();
	          logger.info("Count is " + count);
	          if (count > 0)
	            return null; 
	          msg = "Previous File is not Processed";
	          return msg;
	      }
	           
	    } catch (Exception ex) {
	      logger.error(" error in ReconProcessDaoImpl.validateFile", 
	          new Exception("ReconProcessDaoImpl.validateFile", ex));
	      return msg;
	    } 
	  }
	  
  public String validateFile(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception {
    logger.info("***** ReconProcessDaoImpl.validateFile Start ****");
    String msg = null;
    int count = 0;
    try {
      for (CompareSetupBean setupBean : compareSetupBeans) {
        String GET_FLAGS = "SELECT FILTERATION FROM MAIN_FILESOURCE WHERE FILEID = ?";
        String stFliter_Flag = (String)getJdbcTemplate().queryForObject(GET_FLAGS, 
            new Object[] { Integer.valueOf(setupBean.getInFileId()) }, String.class);
        GET_FLAGS = "SELECT KNOCKOFF FROM MAIN_FILESOURCE WHERE FILEID = ?";
        String stKnockoff_Flag = (String)getJdbcTemplate().queryForObject(GET_FLAGS, 
            new Object[] { Integer.valueOf(setupBean.getInFileId()) }, String.class);
        String stCompare_Flag = "Y";
        if (setupBean.getStFileName().equalsIgnoreCase("REV_REPORT"))
          stCompare_Flag = "N"; 
        String tablename = (String)getJdbcTemplate().queryForObject(
            "select tablename from MAIN_FILESOURCE where FILEID = " + setupBean.getInFileId(), 
            String.class);
        String chkData = null;
        if (category.equals("CARDTOCARD")) {
          chkData = "select count(*) from CARD_TO_CARD_CBS_RAWDATA  where TO_CHAR(to_date(CREATEDDATE,'dd/MM/YY'),'dd-MON-YY') < TO_CHAR(sysdate,'DD/MM/YY')";
        } else if (category.equals("MASTERCARD")) {
          if (setupBean.getStFileName().equalsIgnoreCase("CBS") || 
            setupBean.getStFileName().equalsIgnoreCase("SWITCH")) {
            chkData = "select count(*) from " + tablename + 
              " where TO_CHAR(CREATEDDATE,'DD/MM/YY') < TO_CHAR(sysdate,'DD/MM/YY')";
          } else {
            chkData = "select count(*) from " + tablename + 
              " where TO_CHAR(FILEDATE,'DD/MM/YY') < TO_CHAR(sysdate,'DD/MM/YY')";
          } 
        } else if (category.equals("RUPAY")) {
          chkData = "select count(*) from SWITCH_RAWDATA where filedate=to_date(?,'dd-mm-rr') ";
        } else {
          chkData = "select count(*) from SWITCH_RAWDATA where filedate=to_date('" + filedate + 
            "','dd-mm-rr') ";
        } 
        logger.info("chkData == " + chkData);
        int dataCount = ((Integer)getJdbcTemplate().queryForObject(chkData, Integer.class)).intValue();
        String query = "";
        if (dataCount > 0) {
          if (setupBean.getStFileName().equalsIgnoreCase("CBS") || 
            setupBean.getStFileName().equalsIgnoreCase("SWITCH")) {
            query = "SELECT count (*) FROM MAIN_FILE_UPLOAD_DTLS WHERE filedate = (TRUNC (TO_DATE ('" + 
              filedate + "', 'dd/mm/rr') - 1) ) " + "\tAND Fileid =" + setupBean.getInFileId() + 
              " AND category='" + category + "' " + 
              " AND FILTER_FLAG= ? AND KNOCKOFF_FLAG=? AND COMAPRE_FLAG='Y' " + 
              " AND UPLOAD_FLAG='Y' AND MANUALCOMPARE_FLAG = 'Y'  ";
          } else {
            query = "SELECT count (*) FROM MAIN_FILE_UPLOAD_DTLS WHERE filedate = (TRUNC (TO_DATE ('" + 
              filedate + "', 'dd/mm/rr') - 1) ) " + "\tAND Fileid =" + setupBean.getInFileId() + 
              " AND category='" + category + "' " + 
              " AND FILTER_FLAG= ? AND KNOCKOFF_FLAG=? AND COMAPRE_FLAG='" + stCompare_Flag + "' " + 
              " AND UPLOAD_FLAG='Y'  ";
          } 
          logger.info("query==" + query + " stFliter_Flag " + stFliter_Flag + " " + " stKnockoff_Flag " + 
              stKnockoff_Flag);
          if (!setupBean.getStFileName().equalsIgnoreCase("REV_REPORT")) {
            count = ((Integer)getJdbcTemplate().queryForObject(query, new Object[] { stFliter_Flag, stKnockoff_Flag }, Integer.class)).intValue();
            if (count <= 0) {
              msg = String.valueOf(msg) + "Previous File not Processed.";
              logger.info("msg==" + msg);
            } 
          } 
        } 
      } 
      logger.info("***** ReconProcessDaoImpl.validateFile End ****");
      return msg;
    } catch (Exception ex) {
      demo.logSQLException(ex, "ReconProcessDaoImpl.validateFile");
      logger.error(" error in ReconProcessDaoImpl.validateFile", 
          new Exception("ReconProcessDaoImpl.validateFile", ex));
      return msg;
    } 
  }
  
  public boolean processFile(String category, List<CompareSetupBean> compareSetupBeans, String filedate, String Createdby, String subcat) throws Exception {
    boolean result = false;
    String StMerger_Category = "";
    String stCategory = category;
    logger.info("***** ReconProcessDaoImpl.processFile Start ****");
    try {
      if (category.equalsIgnoreCase("RUPAY")) {
        if (subcat.equalsIgnoreCase("DOMESTIC")) {
          logger.info("Inside rupay Domestic classification");
          String getCount = "select count(*) from main_file_upload_dtls a where fileid in ( SELECT FILEID FROM main_filesource WHERE FILE_CATEGORY = 'RUPAY' AND FILE_SUBCATEGORY = 'DOMESTIC')  AND filedate = STR_TO_DATE(?,'%Y/%m/%d') and FILTER_FLAG != (select FILTERATION FROM main_filesource b where a.fileid = b.fileid)";
          int pendingClass = ((Integer)getJdbcTemplate().queryForObject(getCount, new Object[] { filedate }, Integer.class)).intValue();
          if (pendingClass > 0)
            DomesticClassifydata(category, subcat, filedate, Createdby); 
        } else if (subcat.equalsIgnoreCase("INTERNATIONAL")) {
          logger.info("Inside rupay International classification");
          int fileid = 0;
          for (CompareSetupBean compareSetupBean : compareSetupBeans) {
            fileid = compareSetupBean.getInFileId();
            if (fileid != 8) {
              String getCount = "select count(*) from main_file_upload_dtls a where fileid in ( SELECT FILEID FROM main_filesource WHERE FILE_CATEGORY = 'RUPAY' AND FILE_SUBCATEGORY = 'INTERNATIONAL')  AND filedate = STR_TO_DATE(?,'%Y/%m/%d') and FILTER_FLAG != (select FILTERATION FROM main_filesource b where a.fileid = b.fileid)";
              int pendingClass = ((Integer)getJdbcTemplate().queryForObject(getCount, new Object[] { filedate }, Integer.class)).intValue();
              if (pendingClass > 0)
                InternationalClassifydata(category, subcat, filedate, Createdby); 
              continue;
            } 
            InternationalClassifydata(category, subcat, filedate, Createdby);
          } 
        } 
      } else if (category.equalsIgnoreCase("QSPARC")) {
        if (subcat.equalsIgnoreCase("DOMESTIC")) {
          logger.info("Inside QSPARC Domestic classification");
          String getCount = "select count(*) from main_file_upload_dtls a where fileid in ( SELECT FILEID FROM main_filesource WHERE FILE_CATEGORY = 'QSPARC' AND FILE_SUBCATEGORY = 'DOMESTIC')  AND filedate = STR_TO_DATE(?,'%Y/%m/%d') and FILTER_FLAG != (select FILTERATION FROM main_filesource b where a.fileid = b.fileid)";
          int pendingClass = ((Integer)getJdbcTemplate().queryForObject(getCount, new Object[] { filedate }, Integer.class)).intValue();
          if (pendingClass > 0)
            DomesticClassifydataQSPARC(category, subcat, filedate, Createdby); 
        } else if (subcat.equalsIgnoreCase("INTERNATIONAL")) {
          logger.info("Inside rupay International classification");
          int fileid = 0;
          for (CompareSetupBean compareSetupBean : compareSetupBeans) {
            fileid = compareSetupBean.getInFileId();
            if (fileid != 8) {
              String getCount = "select count(*) from main_file_upload_dtls a where fileid in ( SELECT FILEID FROM  main_filesource WHERE FILE_CATEGORY = 'QSPARC' AND FILE_SUBCATEGORY = 'INTERNATIONAL')  AND filedate =STR_TO_DATE(?,'%Y/%m/%d') and FILTER_FLAG != (select FILTERATION FROM main_filesource b where a.fileid = b.fileid)";
              int pendingClass = ((Integer)getJdbcTemplate().queryForObject(getCount, new Object[] { filedate }, Integer.class)).intValue();
              if (pendingClass > 0)
                InternationalClassifydata(category, subcat, filedate, Createdby); 
              continue;
            } 
            InternationalClassifydata(category, subcat, filedate, Createdby);
          } 
        } 
      } else if (category.equalsIgnoreCase("VISA")) {
        if (subcat.equalsIgnoreCase("ISS")) {
          logger.info("Inside VISA ISSUER classification");
          String getCount = "select count(*) from main_file_upload_dtls a where fileid in ( SELECT FILEID FROM main_filesource WHERE FILE_CATEGORY = 'VISA' AND FILE_SUBCATEGORY = 'ISS')  AND FILEDATE =STR_to_date(?,'%Y/%m/%d')and FILTER_FLAG != (select FILTERATION FROM main_filesource b where a.fileid = b.fileid)";
          int pendingClass = ((Integer)getJdbcTemplate().queryForObject(getCount, new Object[] { filedate }, Integer.class)).intValue();
          if (pendingClass > 0)
            VisaIssClassifydata(category, subcat, filedate, Createdby); 
        }
      } else if (category.equalsIgnoreCase("VISA_ACQ")) {
        if (subcat.equalsIgnoreCase("ACQ DOM ATM")) {
          logger.info("Inside VISA dOM ATM classification");
          String getCount = "select count(*) from main_file_upload_dtls a where fileid in ( SELECT FILEID FROM main_filesource WHERE FILE_CATEGORY = 'VISA_ACQ' AND FILE_SUBCATEGORY = 'ACQ DOM ATM')  AND  filedate = STR_TO_DATE(?,'%Y/%m/%d')  and FILTER_FLAG != (select FILTERATION FROM main_filesource b where a.fileid = b.fileid)";
          int pendingClass = ((Integer)getJdbcTemplate().queryForObject(getCount, new Object[] { filedate }, Integer.class)).intValue();
          if (pendingClass > 0)
            VisaClassifydataACQVISA(category, subcat, filedate, Createdby); 
        } else if (subcat.equalsIgnoreCase("ACQ INT ATM")) {
          logger.info("Inside VISA INT ATM classification");
          String getCount = "select count(*) from main_file_upload_dtls a where fileid in ( SELECT FILEID FROM main_filesource WHERE FILE_CATEGORY = 'VISA_ACQ' AND FILE_SUBCATEGORY = 'ACQ INT ATM')  AND filedate = STR_TO_DATE(?,'%Y/%m/%d')  and FILTER_FLAG != (select FILTERATION FROM main_filesource b where a.fileid = b.fileid)";
          int pendingClass = ((Integer)getJdbcTemplate().queryForObject(getCount, new Object[] { filedate }, Integer.class)).intValue();
          if (pendingClass > 0)
            VisaAcqClassifydata(category, subcat, filedate, Createdby); 
        } 
      } else if (category.equals("NFS")) {
        logger.info("******** In NFS ***********");
        if (subcat.equals("ISSUER")) {
          logger.info("******** In NFS - ISSUER ***********");
          ISSClassifydata(category, subcat, filedate, Createdby);
        } else if (subcat.equals("ACQUIRER")) {
          logger.info("******** In NFS - ACQUIRER ***********");
          AcqClassifydata(category, subcat, filedate, Createdby);
        } 
      } else if (category.equals("ICCW")) {
        logger.info("******** In NFS ***********");
        if (subcat.equals("ISSUER")) {
          logger.info("******** In NFSICCW - ISSUER ***********");
          ISSICCWClassifydata(category, subcat, filedate, Createdby);
        } else if (subcat.equals("ACQUIRER")) {
          logger.info("******** In NFS ICCW - ACQUIRER ***********");
          AcqICCWClassifydata(category, subcat, filedate, Createdby);
        } 
      } else if (category.equals("DFS")) {
        logger.info("******** In DFS ***********");
        if (subcat.equals("ISSUER")) {
          logger.info("******** In DFS - ISSUER ***********");
          ISSClassifydata(category, subcat, filedate, Createdby);
        } else if (subcat.equals("ACQUIRER")) {
          logger.info("******** In DFS - ACQUIRER ***********");
          AcqClassifydataDFS(category, subcat, filedate, Createdby);
        } 
      }else if (category.equals("JCB")) {
          logger.info("******** In DFS ***********");
          if (subcat.equals("ISSUER")) {
            logger.info("******** In DFS - ISSUER ***********");
            ISSClassifydata(category, subcat, filedate, Createdby);
          } else if (subcat.equals("ACQUIRER")) {
            logger.info("******** In JCB - ACQUIRER ***********");
            AcqClassificatonJCB(category, subcat, filedate, Createdby);
          } 
        } else if (category.equals("ICD")) {
        logger.info("******** In NFS ***********");
        if (subcat.equals("ISSUER")) {
          logger.info("******** In NFS - ISSUER ***********");
          ISSClassifydataICD(category, subcat, filedate, Createdby);
        } else if (subcat.equals("ACQUIRER")) {
          logger.info("******** In NFS - ACQUIRER ***********");
          AcqClassifydataICD(category, subcat, filedate, Createdby);
        } 
      } else if (category.equals("CASHNET")) {
        logger.info("******** In CASHNET ***********");
        if (subcat.equals("ISSUER")) {
          logger.info("******** In CASHNET - ISSUER ***********");
          cashnetISSClassifydata(category, subcat, filedate, Createdby);
        } else if (subcat.equals("ACQUIRER")) {
          logger.info("******** In CASHNET - ACQUIRER ***********");
          cashnetAcqClassifydata(category, subcat, filedate, Createdby);
        } 
      } else if (category.equals("MASTERCARD")) {
        logger.info("******** In MASTERCARD ***********");
        if (subcat.equals("ACQUIRER_DOM")) {
          logger.info("******** In MASTERCARD - ACQUIRER_DOM ***********");
          MASTERCARDISSClassifydata(category, subcat, filedate, Createdby);
        } else if (subcat.equals("ACQUIRER_INT")) {
          logger.info("******** In MASTERCARD - ACQUIRER_INT ***********");
          MASTERCARDISSposClassifydata(category, subcat, filedate, Createdby);
        } else {
          logger.info("******** In MASTERCARD - ISSUER ***********");
          MASTERCARDISSposClassifydataNew(category, subcat, filedate, Createdby);
        } 
      } 
      CompareSetupBean setupBean = chkStatus(compareSetupBeans, category, filedate);
      logger.info("knockoff" + setupBean.getKnockoff_Flag() + "filter" + setupBean.getFilter_Flag());
      if (setupBean.getKnockoff_Flag().equals("Completed") && setupBean.getFilter_Flag().equals("Completed")) {
        result = true;
      } else {
        result = true;
      } 
      logger.info("result==" + result);
      logger.info("***** ReconProcessDaoImpl.processFile End ****");
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.processFile");
      logger.error(" error in ReconProcessDaoImpl.processFile", e);
      result = false;
    } 
    return result;
  }
  
  public boolean compareFiles(String category, String filedate, CompareBean compareBean, String subcat, String dollar_val) throws Exception {
    boolean result = false;
    try {
      logger.info("***** ReconProcessDaoImpl.compareFiles Start ****");
      compareBean.setStCategory(category);
      compareBean.setStSubCategory(subcat);
      compareBean.setStFile_date(filedate);
      if (category.contains("ONUS") || (category.contains("AMEX") && !category.equals("NFS"))) {
        String stCategory;
        logger.info("********* In ONUS-AMEX-NFS ******");
        if (!subcat.equals("-")) {
          stCategory = String.valueOf(category) + "_" + subcat;
          compareBean.setStMergeCategory(String.valueOf(category) + "_" + subcat.substring(0, 3));
        } else {
          stCategory = category;
          compareBean.setStMergeCategory(category);
        } 
        List<Integer> rec_set_id = this.compareService.getRec_set_id(stCategory);
        logger.info("rec_set_id==" + rec_set_id);
        List<String> fileid = new ArrayList<>();
        CompareSetupBean setupBean = new CompareSetupBean();
        setupBean.setFileDate(filedate);
        setupBean.setCategory(category);
        setupBean.setStSubCategory(subcat);
        for (int j = 0; j < rec_set_id.size(); j++) {
          List<List<String>> tables_data = this.compareRupayService.getTableName(((Integer)rec_set_id.get(j)).intValue(), category);
          logger.info("tables_data==" + tables_data);
          List<String> tables = tables_data.get(0);
          fileid.add(tables.get(0));
          fileid.add(tables.get(1));
          if (chkStatus(setupBean, tables)) {
            int i = this.compareService.moveData(tables, compareBean, ((Integer)rec_set_id.get(j)).intValue());
            if (i == 1) {
              if (category.contains("ONUS"))
                OnusCardLessCompare(setupBean.getCategory(), setupBean.getStSubCategory(), 
                    setupBean.getFileDate(), setupBean.getEntry_by()); 
              this.compareService.updateMatchedRecords(tables, compareBean, ((Integer)rec_set_id.get(j)).intValue());
              this.compareService.moveToRecon(tables, compareBean);
              logger.info("clearing tables now.....");
              this.compareService.clearTables(tables, compareBean);
              result = true;
              logger.info("result==" + result);
            } 
            if (j == 0)
              this.compareService.TTUMRecords(tables, compareBean, ((Integer)rec_set_id.get(j)).intValue()); 
          } else {
            List<String> list = new ArrayList<>();
            list.add(subcat);
            list.add(subcat);
            updatereconstatus(setupBean, fileid, list);
            return false;
          } 
        } 
        List<String> categories = new ArrayList<>();
        categories.add(subcat);
        categories.add(subcat);
        updatereconstatus(setupBean, fileid, categories);
      } else if (category.equals("NFS")) {
        logger.info("********* In NFS ******");
        if (subcat.equals("ISSUER")) {
          logger.info("********* In ISSUER ******");
          result = ISSComparedata(category, subcat, filedate, compareBean.getStEntryBy());
        } else if (subcat.equals("ACQUIRER")) {
          logger.info("********* In ACQUIRER ******");
          result = AcqComparedata(category, subcat, filedate, compareBean.getStEntryBy());
        } 
      } else if (category.equals("ICCW")) {
        logger.info("********* In ICCW******");
        if (subcat.equals("ISSUER")) {
          logger.info("********* In ISSUER ******");
          result = IssICCWCompare(category, subcat, filedate, compareBean.getStEntryBy());
        } else if (subcat.equals("ACQUIRER")) {
          logger.info("********* In ACQUIRER ******");
          result = AcqICCWComparedata(category, subcat, filedate, compareBean.getStEntryBy());
        } 
      } else if (category.equals("DFS")) {
        logger.info("********* In DFS ******");
        if (subcat.equals("ISSUER")) {
          logger.info("********* In ISSUER ******");
          result = ISSComparedata(category, subcat, filedate, compareBean.getStEntryBy());
        } else if (subcat.equals("ACQUIRER")) {
          logger.info("********* In ACQUIRER ******");
          result = AcqComparedataDFS(category, subcat, filedate, compareBean.getStEntryBy());
        } 
      } else if (category.equals("JCB")) {
        logger.info("********* In JCB ******");
        if (subcat.equals("ISSUER")) {
          logger.info("********* In ISSUER ******");
          result = ISSComparedata(category, subcat, filedate, compareBean.getStEntryBy());
        } else if (subcat.equals("ACQUIRER")) {
          logger.info("********* In ACQUIRER ******");
          result = AcqCompareJCB(category, subcat, filedate, compareBean.getStEntryBy());
        } 
      } else if (category.equals("ICD")) {
        logger.info("********* In NFS ******");
        if (subcat.equals("ISSUER")) {
          logger.info("********* In ISSUER ******");
          result = ISSComparedataICD(category, subcat, filedate, compareBean.getStEntryBy());
        } else if (subcat.equals("ACQUIRER")) {
          logger.info("********* In ACQUIRER ******");
          result = AcqComparedataICD(category, subcat, filedate, compareBean.getStEntryBy());
        } 
      } else if (category.equals("RUPAY")) {
        logger.info("*********** In Rupay ******************");
        if (subcat.equals("DOMESTIC")) {
          logger.info("******** In Domestic ***********");
          System.out.println("inside the domesstic ");
          result = DomesticComparedata(category, subcat, filedate, compareBean.getStEntryBy());
        } else if (subcat.equals("INTERNATIONAL")) {
          logger.info("******** In International ***********");
          result = InternationalComparedata(category, subcat, filedate, compareBean.getStEntryBy());
        } 
      } else if (category.equals("QSPARC")) {
        logger.info("*********** In QSPARC ******************");
        if (subcat.equals("DOMESTIC")) {
          logger.info("******** In Domestic ***********");
          System.out.println("inside the domesstic ");
          result = DomesticComparedataQSparc(category, subcat, filedate, compareBean.getStEntryBy());
        } else if (subcat.equals("INTERNATIONAL")) {
          logger.info("******** In International ***********");
          result = InternationalComparedata(category, subcat, filedate, compareBean.getStEntryBy());
        } 
      } else if (category.equalsIgnoreCase("VISA")) {
        logger.info("*********** In Visa ******************");
        if (subcat.equals("ISS")) {
          logger.info("******** In ISS ***********");
          result = VisaCompareData(category, subcat, filedate, compareBean.getStEntryBy());
        }
      } else if (category.equalsIgnoreCase("VISA_ACQ")) {
        logger.info("*********** In Visa ******************");
        if (subcat.equals("ACQ DOM ATM")) {
          logger.info("******** In ACQUIRER ATM ***********");
          result = VisaCompareDataACQVISA(category, subcat, filedate, compareBean.getStEntryBy());
        } else if (subcat.equals("ACQ INT ATM")) {
          logger.info("******** In ACQUIRER POS ***********");
          result = VisaISSPOSCompare(category, subcat, filedate, compareBean.getStEntryBy());
        } 
      } else if (category.equals("MASTERCARD")) {
        logger.info("*********** In MASTERCARD ******************");
        if (subcat.equals("ISSUER")) {
          logger.info("******** In ISSUER ***********");
          result = MASTERCARDposCompareData(category, subcat, filedate, compareBean.getStEntryBy());
        } 
      } 
      logger.info("***** ReconProcessDaoImpl.compareFiles End ****");
      return result;
    } catch (Exception e) {
      System.out.println("Exception in reconprocessdaoImpl " + e);
      demo.logSQLException(e, "ReconProcessDaoImpl.compareFiles");
      logger.error(" error in  ReconProcessDaoImpl.compareFiles", 
          new Exception(" ReconProcessDaoImpl.compareFiles", e));
      return false;
    } 
  }
  
 
  
  
  public CompareSetupBean chkStatus(List<CompareSetupBean> compareSetupBeans, String category, String filedate) throws Exception {
    logger.info("***** ReconProcessDaoImpl.chkStatus Start ****");
    boolean upload_flag = false, Filter_flag = false, knockoff_flag = false, COMAPRE_FLAG = false;
    int upload = 0, knockoff = 0, compare = 0, filter = 0;
    CompareSetupBean bean = new CompareSetupBean();
    try {
      for (CompareSetupBean setupBean : compareSetupBeans) {
        logger.info("fileid" + setupBean.getInFileId());
        logger.info("knockoff" + setupBean.getKnockoff_Flag());
        logger.info("filter" + setupBean.getFilter_Flag());
        if (getStatus("UPLOAD_FLAG", filedate, category, setupBean.getInFileId()) > 0)
          upload++; 
        if (getStatus("COMAPRE_FLAG", filedate, category, setupBean.getInFileId()) > 0)
          compare++; 
        if (setupBean.getKnockoff_Flag().equalsIgnoreCase("Y")) {
          if (getStatus("FILTER_FLAG", filedate, category, setupBean.getInFileId()) > 0)
            filter++; 
        } else {
          filter++;
        } 
        if (setupBean.getKnockoff_Flag().equalsIgnoreCase("Y")) {
          if (getStatus("KNOCKOFF_FLAG", filedate, category, setupBean.getInFileId()) > 0)
            knockoff++; 
          continue;
        } 
        knockoff++;
      } 
      logger.info(Integer.valueOf(upload));
      logger.info(Integer.valueOf(filter));
      logger.info(Integer.valueOf(knockoff));
      logger.info(Integer.valueOf(compare));
      if (upload == compareSetupBeans.size()) {
        bean.setUpload_Flag("Completed");
      } else {
        bean.setUpload_Flag("Pending");
      } 
      if (filter == compareSetupBeans.size()) {
        bean.setFilter_Flag("Completed");
      } else if (category.equals("MASTERCARD")) {
        bean.setFilter_Flag("Completed");
      } else {
        bean.setFilter_Flag("Pending");
      } 
      if (knockoff == compareSetupBeans.size()) {
        bean.setKnockoff_Flag("Completed");
      } else if (category.equals("MASTERCARD")) {
        bean.setKnockoff_Flag("Completed");
      } else {
        bean.setKnockoff_Flag("Pending");
      } 
      if (compare == compareSetupBeans.size()) {
        bean.setComapre_Flag("Completed");
      } else {
        bean.setComapre_Flag("Pending");
      } 
      logger.info("***** ReconProcessDaoImpl.chkStatus End ****");
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.chkStatus");
      logger.error(" error in  ReconProcessDaoImpl.chkStatus", 
          new Exception(" ReconProcessDaoImpl.chkStatus", e));
      throw e;
    } 
    return bean;
  }
  
  public int getStatus(String Flag, String filedate, String category, int fileid) throws Exception {
    logger.info("***** ReconProcessDaoImpl.getStatus Start ****");
    try {
      String query = "Select count(*)  FROM  main_file_upload_dtls where category='" + category + "'" + 
        " and filedate=STR_TO_DATE('" + filedate + "','%Y/%m/%d') and " + " FILEID=" + 
        fileid + " and " + Flag + " = 'Y'";
      logger.info(query);
      logger.info("***** ReconProcessDaoImpl.getStatus End ****");
      return ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
    } catch (Exception ex) {
      demo.logSQLException(ex, "ReconProcessDaoImpl.getStatus");
      logger.error(" error in  ReconProcessDaoImpl.getStatus", 
          new Exception(" ReconProcessDaoImpl.getStatus", ex));
      return 1;
    } 
  }
  
  public boolean chkStatus(CompareSetupBean setupBean, List<String> tables) throws Exception {
    logger.info("***** ReconProcessDaoImpl.chkStatus Start ****");
    try {
      String query = "select FILEID from main_filesource where upper(filename) = '" + (String)tables.get(0) + 
        "' and FILE_CATEGORY='" + setupBean.getCategory() + "' and FILE_SUBCATEGORY='" + 
        setupBean.getStSubCategory() + "'";
      logger.info("query==" + query);
      int fileid1 = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
      logger.info("fileid1==" + fileid1);
      query = "select FILEID from main_filesource where upper(filename) = '" + (String)tables.get(1) + 
        "' and FILE_CATEGORY='" + setupBean.getCategory() + "' and FILE_SUBCATEGORY='" + 
        setupBean.getStSubCategory() + "'";
      logger.info("query==" + query);
      int fileid2 = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
      logger.info("fileid2==" + fileid2);
      String FilterFlag1 = (String)getJdbcTemplate().queryForObject(
          "select FILTERATION from main_filesource where FILEID =" + fileid1 + "  ", String.class);
      logger.info("FilterFlag1==" + FilterFlag1);
      String knockoffFlag1 = (String)getJdbcTemplate().queryForObject(
          "select KNOCKOFF from main_filesource where FILEID =" + fileid1 + "  ", String.class);
      logger.info("knockoffFlag1==" + knockoffFlag1);
      String filterflag2 = (String)getJdbcTemplate().queryForObject(
          "select FILTERATION from main_filesource where FILEID =" + fileid2 + "  ", String.class);
      logger.info("filterflag2==" + filterflag2);
      String KnockoffFlag2 = (String)getJdbcTemplate().queryForObject(
          "select KNOCKOFF from main_filesource where FILEID =" + fileid2 + " ", String.class);
      logger.info("KnockoffFlag2==" + KnockoffFlag2);
      String sql = "";
      int result1 = 0, result2 = 0;
      if (FilterFlag1.equalsIgnoreCase("Y") && knockoffFlag1.equalsIgnoreCase("Y")) {
        sql = "Select count(*) from main_file_upload_dtls where  FILEID = " + fileid1 + 
          " and Knockoff_FLAG='Y' AND Upload_FLAG = 'Y' and FILTER_FLAG = 'Y' and COMAPRE_FLAG='N' and  filedate=STR_TO_DATE('" + setupBean.getFileDate() + "','%Y/%m/%d')";
        result1 = ((Integer)getJdbcTemplate().queryForObject(sql, Integer.class)).intValue();
      } else {
        sql = "Select count(*) from main_file_upload_dtls where FILEID = " + fileid1 + 
          " and  Upload_FLAG = 'Y' and  COMAPRE_FLAG='N' and filedate=STR_TO_DATE('" + setupBean.getFileDate() + "','%Y/%m/%d')";
        result1 = ((Integer)getJdbcTemplate().queryForObject(sql, Integer.class)).intValue();
      } 
      logger.info("sql==" + sql);
      logger.info("result1==" + result1);
      if (filterflag2.equalsIgnoreCase("Y") && KnockoffFlag2.equalsIgnoreCase("Y")) {
        sql = "Select count(*) from main_file_upload_dtls where  FILEID = " + fileid2 + 
          " and Knockoff_FLAG='Y' AND Upload_FLAG = 'Y' and FILTER_FLAG = 'Y' and COMAPRE_FLAG='N' and filedate=STR_TO_DATE('" + setupBean.getFileDate() + "','%Y/%m/%d')";
        result2 = ((Integer)getJdbcTemplate().queryForObject(sql, Integer.class)).intValue();
      } else {
        sql = "Select count(*) from main_file_upload_dtls where FILEID = " + fileid2 + 
          " and  Upload_FLAG = 'Y' and  COMAPRE_FLAG='N' and to_date(filedate,'DD-MM-YY')=to_date('" + 
          setupBean.getFileDate() + "','DD-MM-YY') ";
        result2 = ((Integer)getJdbcTemplate().queryForObject(sql, Integer.class)).intValue();
      } 
      logger.info("sql==" + sql);
      logger.info("result2==" + result2);
      logger.info("***** ReconProcessDaoImpl.chkStatus end ****");
      if (result1 > 0 && result2 > 0)
        return true; 
      return false;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.chkStatus");
      logger.error(" error in  ReconProcessDaoImpl.chkStatus", 
          new Exception(" ReconProcessDaoImpl.chkStatus", e));
      throw e;
    } 
  }
  
  public boolean checkCompareStatus(String stFileDate, String stCategory, String stTable1_Subcat, String stTable2_cat, List<String> tables) throws Exception {
    logger.info("***** ReconProcessDaoImpl.checkCompareStatus Start ****");
    try {
      String query = "select FILEID from main_filesource where upper(filename) = '" + (String)tables.get(0) + 
        "' and FILE_CATEGORY='" + stCategory + "' and FILE_SUBCATEGORY='" + stTable1_Subcat + "'";
      logger.info("query==" + query);
      int fileid1 = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
      logger.info("fileid1==" + fileid1);
      query = "select FILEID from main_filesource where upper(filename) = '" + (String)tables.get(1) + 
        "' and FILE_CATEGORY='" + stCategory + "' and FILE_SUBCATEGORY='" + stTable2_cat + "'";
      logger.info("query==" + query);
      int fileid2 = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
      logger.info("fileid2==" + fileid2);
      String FilterFlag1 = (String)getJdbcTemplate().queryForObject(
          "select FILTERATION from main_filesource where FILEID =" + fileid1 + "  ", String.class);
      logger.info("FilterFlag1==" + FilterFlag1);
      String knockoffFlag1 = (String)getJdbcTemplate().queryForObject(
          "select KNOCKOFF from main_filesource where FILEID =" + fileid1 + "  ", String.class);
      logger.info("knockoffFlag1==" + knockoffFlag1);
      String filterflag2 = (String)getJdbcTemplate().queryForObject(
          "select FILTERATION from main_filesource where FILEID =" + fileid2 + "  ", String.class);
      logger.info("filterflag2==" + filterflag2);
      String KnockoffFlag2 = (String)getJdbcTemplate().queryForObject(
          "select KNOCKOFF from main_filesource where FILEID =" + fileid2 + " ", String.class);
      logger.info("KnockoffFlag2==" + KnockoffFlag2);
      String sql = "";
      int result1 = 0, result2 = 0;
      if (FilterFlag1.equalsIgnoreCase("Y") && knockoffFlag1.equalsIgnoreCase("Y")) {
        sql = "Select count(*) from MAIN_FILE_UPLOAD_DTLS where  FILEID = " + fileid1 + 
          " and Knockoff_FLAG='Y' AND Upload_FLAG = 'Y' and FILTER_FLAG = 'Y' and COMAPRE_FLAG='N' " + 
          "and  filedate =  '" + stFileDate + "'  ";
        result1 = ((Integer)getJdbcTemplate().queryForObject(sql, Integer.class)).intValue();
      } else {
        sql = "Select count(*) from MAIN_FILE_UPLOAD_DTLS where FILEID = " + fileid1 + 
          " and  Upload_FLAG = 'Y' and  COMAPRE_FLAG='N' and filedate =  '" + stFileDate + "'  ";
        result1 = ((Integer)getJdbcTemplate().queryForObject(sql, Integer.class)).intValue();
      } 
      logger.info("sql==" + sql);
      logger.info("result1==" + result1);
      if (filterflag2.equalsIgnoreCase("Y") && KnockoffFlag2.equalsIgnoreCase("Y")) {
        sql = "Select count(*) from MAIN_FILE_UPLOAD_DTLS where  FILEID = " + fileid2 + 
          " and Knockoff_FLAG='Y' AND Upload_FLAG = 'Y' and FILTER_FLAG = 'Y' and COMAPRE_FLAG='N'" + 
          " and filedate =  '" + stFileDate + "'  ";
        result2 = ((Integer)getJdbcTemplate().queryForObject(sql, Integer.class)).intValue();
      } else {
        sql = "Select count(*) from MAIN_FILE_UPLOAD_DTLS where FILEID = " + fileid2 + 
          " and  Upload_FLAG = 'Y' and  COMAPRE_FLAG='N' and filedate = '" + stFileDate + "'  ";
        result2 = ((Integer)getJdbcTemplate().queryForObject(sql, Integer.class)).intValue();
      } 
      logger.info("sql==" + sql);
      logger.info("result2==" + result2);
      logger.info("***** ReconProcessDaoImpl.checkCompareStatus End ****");
      if (result1 > 0 && result2 > 0)
        return true; 
      return false;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.checkCompareStatus");
      logger.error(" error in  ReconProcessDaoImpl.checkCompareStatus", 
          new Exception(" ReconProcessDaoImpl.checkCompareStatus", e));
      throw e;
    } 
  }
  
  public boolean updatereconstatus(CompareSetupBean setupBean, List<String> tables, List<String> categories) throws Exception {
    logger.info("***** ReconProcessDaoImpl.updatereconstatus Start ****");
    try {
      for (int i = 0; i < tables.size(); i++) {
        String query = "select FILEID from main_filesource where upper(filename) = '" + (String)tables.get(i) + 
          "' and FILE_CATEGORY='" + setupBean.getCategory() + "' and FILE_SUBCATEGORY='" + 
          (String)categories.get(i) + "'";
        logger.info("query==" + query);
        int fileid1 = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
        logger.info("fileid1==" + fileid1);
        setupBean.setInFileId(fileid1);
        this.icompareConfigService.updateFlag("MANUALCOMPARE_FLAG", setupBean);
        this.icompareConfigService.updateFlag("COMAPRE_FLAG", setupBean);
      } 
      logger.info("***** ReconProcessDaoImpl.updatereconstatus End ****");
      return true;
    } catch (Exception ex) {
      demo.logSQLException(ex, "ReconProcessDaoImpl.updatereconstatus");
      logger.error(" error in  ReconProcessDaoImpl.updatereconstatus", 
          new Exception(" ReconProcessDaoImpl.updatereconstatus", ex));
      return false;
    } 
  }
  
  public boolean updateRupayreconstatus(CompareSetupBean setupBean, Set<String> tables, Set<String> subCategory) throws Exception {
    logger.info("***** ReconProcessDaoImpl.updateRupayreconstatus Start ****");
    try {
      for (String fileName : tables) {
        for (String stSubCat : subCategory) {
          try {
            if (fileName.equals("RUPAY") && stSubCat.equals("SURCHARGE") && 
              setupBean.getCategory().equals("RUPAY")) {
              logger.info("IN RUPAY-SURCHARGE");
              continue;
            } 
            if (fileName.equals("SWITCH") && stSubCat.equals("SURCHARGE") && 
              setupBean.getCategory().equals("RUPAY")) {
              logger.info("IN RUPAY-SURCHARGE");
              continue;
            } 
            if (fileName.equals("VISA") && stSubCat.equals("SURCHARGE") && 
              setupBean.getCategory().equals("VISA")) {
              logger.info("IN RUPAY-SURCHARGE");
              continue;
            } 
            if (fileName.equals("SWITCH") && stSubCat.equals("SURCHARGE") && 
              setupBean.getCategory().equals("VISA")) {
              logger.info("IN RUPAY-SURCHARGE");
              continue;
            } 
            String query = "select FILEID from main_filesource where upper(filename) = '" + fileName + 
              "' and FILE_CATEGORY='" + setupBean.getCategory() + "' and FILE_SUBCATEGORY='" + 
              stSubCat + "'";
            logger.info("query==" + query);
            int fileid1 = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
            logger.info("fileid1==" + fileid1);
            setupBean.setInFileId(fileid1);
            this.icompareConfigService.updateFlag("COMAPRE_FLAG", setupBean);
            this.icompareConfigService.updateFlag("MANUALCOMPARE_FLAG", setupBean);
          } catch (Exception e) {
            demo.logSQLException(e, "ReconProcessDaoImpl.updateRupayreconstatus");
            logger.error(" error in ReconProcessDaoImpl.updateRupayreconstatus", 
                new Exception("ReconProcessDaoImpl.updateRupayreconstatus", e));
            throw e;
          } 
        } 
      } 
      logger.info("***** ReconProcessDaoImpl.updateRupayreconstatus End ****");
      return true;
    } catch (Exception ex) {
      demo.logSQLException(ex, "ReconProcessDaoImpl.updateRupayreconstatus");
      logger.error(" error in ReconProcessDaoImpl.updateRupayreconstatus", 
          new Exception("ReconProcessDaoImpl.updateRupayreconstatus", ex));
      return false;
    } 
  }
  
  public boolean ISSClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.ISSClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      IssClassificaton acqclassificaton = new IssClassificaton(getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.ISSClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.ISSClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.ISSClassifydata", 
          new Exception(" ReconProcessDaoImpl.ISSClassifydata", e));
      return false;
    } 
  }
  
  class IssClassificaton extends StoredProcedure {
		private static final String procName = "NFS_ISS_CLASSIFY";

		IssClassificaton(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  
  public boolean ISSICCWClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.ISSClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      ISSICCWClassifydata acqclassificaton = new ISSICCWClassifydata(getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.ISSClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.ISSClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.ISSClassifydata", 
          new Exception(" ReconProcessDaoImpl.ISSClassifydata", e));
      return false;
    } 
  }
  
  
  class ISSICCWClassifydata extends StoredProcedure {
		private static final String procName = "NFS_ICCW_ISS_CLASSIFY";

		ISSICCWClassifydata(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean ISSClassifydataICD(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.ISSClassifydataICD Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      ISSClassifydataICD acqclassificaton = new ISSClassifydataICD(getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.ISSClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.ISSClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.ISSClassifydata", 
          new Exception(" ReconProcessDaoImpl.ISSClassifydata", e));
      return false;
    } 
  }
  
  class ISSClassifydataICD extends StoredProcedure {
		private static final String procName = "NFS_ISS_CLASSIFY";

		ISSClassifydataICD(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean ISSComparedata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.ISSComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      System.out.println("category " + category + "subCat " + subCat + "filedate " + filedate);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_ENTRY_BY", entry_by);

		IssCompare issCompare = new IssCompare(getJdbcTemplate());
		Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.ISSComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.ISSComparedata");
      logger.error(" error in  ReconProcessDaoImpl.ISSComparedata", 
          new Exception(" ReconProcessDaoImpl.ISSComparedata", e));
      return false;
    } 
  }
  
  class IssCompare extends StoredProcedure {
		// private static final String procName = "NFS_Iss_COMPARE_Proc";
		private static final String procName = "RECON_NFS_ISS_PROC";

		// teST_NFS_Iss_COMPARE_Proc_rev2
		IssCompare(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}

  
  public boolean IssICCWCompare(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.ISSComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      System.out.println("category " + category + "subCat " + subCat + "filedate " + filedate);
      inParams.put("I_FILEDATE", filedate);
      IssICCWCompare issCompare = new IssICCWCompare(getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.ISSComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.ISSComparedata");
      logger.error(" error in  ReconProcessDaoImpl.ISSComparedata", 
          new Exception(" ReconProcessDaoImpl.ISSComparedata", e));
      return false;
    } 
  }
  
  
  class IssICCWCompare extends StoredProcedure {
		// private static final String procName = "NFS_Iss_COMPARE_Proc";
		private static final String procName = "RECON_NFS_ICCW_ISS_PROC1";

		// teST_NFS_Iss_COMPARE_Proc_rev2
		IssICCWCompare(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
		
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}

  public boolean ISSComparedataICD(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.ISSComparedataICD Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      System.out.println("category " + category + "subCat " + subCat + "filedate " + filedate);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_ENTRY_BY", entry_by);
      ISSComparedataICD issCompare = new ISSComparedataICD( getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.ISSComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.ISSComparedata");
      logger.error(" error in  ReconProcessDaoImpl.ISSComparedata", 
          new Exception(" ReconProcessDaoImpl.ISSComparedata", e));
      return false;
    } 
  }
  
  class ISSComparedataICD extends StoredProcedure {
		// private static final String procName = "NFS_Iss_COMPARE_Proc";
		private static final String procName = "RECON_NFS_ISS_PROC";

		// teST_NFS_Iss_COMPARE_Proc_rev2
		ISSComparedataICD(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}

  public boolean AcqClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.AcqClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILEDATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      AcqClassificaton acqclassificaton = new AcqClassificaton(getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.AcqClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.AcqClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.AcqClassifydata", 
          new Exception(" ReconProcessDaoImpl.AcqClassifydata", e));
      return false;
    } 
  }
  
  class AcqClassificaton extends StoredProcedure {
		private static final String procName = "NFS_ACQ_CLASSIFY";

		AcqClassificaton(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY", Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}

  public boolean AcqICCWClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.AcqClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILEDATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      AcqICCWClassifydata acqclassificaton = new AcqICCWClassifydata(getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.AcqClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.AcqClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.AcqClassifydata", 
          new Exception(" ReconProcessDaoImpl.AcqClassifydata", e));
      return false;
    } 
  }
  
  class AcqICCWClassifydata extends StoredProcedure {
		private static final String procName = "NFS_ICCW_ACQ_CLASSIFY";

		AcqICCWClassifydata(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY", Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}

  
  public boolean AcqClassifydataDFS(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.AcqClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();

      inParams.put("I_FILEDATE", filedate);

      AcqClassificatonDFS acqclassificaton = new AcqClassificatonDFS( getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.AcqClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.AcqClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.AcqClassifydata", 
          new Exception(" ReconProcessDaoImpl.AcqClassifydata", e));
      return false;
    } 
  }
  class AcqClassificatonDFS extends StoredProcedure {
		private static final String procName = "RECON_DFS_PROC";

		AcqClassificatonDFS(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
		
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean AcqClassificatonJCB(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
	    try {
	      logger.info("***** ReconProcessDaoImpl.AcqClassifydata Start ****");
	      String response = null;
	      Map<String, Object> inParams = new HashMap<>();
	 
	      inParams.put("I_FILEDATE", filedate);
	  
	      AcqClassificatonJCB acqclassificaton = new AcqClassificatonJCB( getJdbcTemplate());
	      Map<String, Object> outParams = acqclassificaton.execute(inParams);
	      logger.info("***** ReconProcessDaoImpl.AcqClassifydata End ****");
	      if (outParams.get("ERROR_MESSAGE") != null)
	        return false; 
	      return true;
	    } catch (Exception e) {
	      demo.logSQLException(e, "ReconProcessDaoImpl.AcqClassifydata");
	      logger.error(" error in  ReconProcessDaoImpl.AcqClassifydata", 
	          new Exception(" ReconProcessDaoImpl.AcqClassifydata", e));
	      return false;
	    } 
	  }
	  class AcqClassificatonJCB extends StoredProcedure {
			private static final String procName = "RECON_JCB_PROC";

			AcqClassificatonJCB(JdbcTemplate JdbcTemplate) {
				super(JdbcTemplate, procName);
				setFunction(false);

				declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
		
				declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
				compile();
			}
		}

  public boolean AcqClassifydataICD(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.AcqClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILEDATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      AcqClassifydataICD acqclassificaton = new AcqClassifydataICD(getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.AcqClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.AcqClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.AcqClassifydata", 
          new Exception(" ReconProcessDaoImpl.AcqClassifydata", e));
      return false;
    } 
  }
  
  class AcqClassifydataICD extends StoredProcedure {
		private static final String procName = "NFS_ACQ_CLASSIFY";

		AcqClassifydataICD(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY", Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}

  public boolean AcqComparedata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.AcqComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      System.out.println("category " + category + "subCat " + subCat + "filedate " + filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      AcqCompare acqComparedata = new AcqCompare(getJdbcTemplate());
      Map<String, Object> outParams = acqComparedata.execute(inParams);
      logger.info("Outparam is " + outParams);
      logger.info("***** ReconProcessDaoImpl.AcqComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null) {
        System.out.println(outParams.get("ERROR_MESSAGE"));
        return false;
      } 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.AcqComparedata");
      logger.error(" error in  ReconProcessDaoImpl.AcqComparedata", 
          new Exception(" ReconProcessDaoImpl.AcqComparedata", e));
      return false;
    } 
  }
  class AcqCompare extends StoredProcedure {
		private static final String procName = "RECON_NFS_ACQ_PROC";

		AcqCompare(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  
  public boolean AcqICCWComparedata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.AcqComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      System.out.println("category " + category + "subCat " + subCat + "filedate " + filedate);
      inParams.put("I_FILEDATE", filedate);
      AcqICCWComparedata acqComparedata = new AcqICCWComparedata( getJdbcTemplate());
      Map<String, Object> outParams = acqComparedata.execute(inParams);
      logger.info("Outparam is " + outParams);
      logger.info("***** ReconProcessDaoImpl.AcqComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null) {
        System.out.println(outParams.get("ERROR_MESSAGE"));
        return false;
      } 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.AcqComparedata");
      logger.error(" error in  ReconProcessDaoImpl.AcqComparedata", 
          new Exception(" ReconProcessDaoImpl.AcqComparedata", e));
      return false;
    } 
  }
  
  class AcqICCWComparedata extends StoredProcedure {
		private static final String procName = "RECON_NFS_ICCW_ACQ_PROC1";

		AcqICCWComparedata(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  
  public boolean AcqComparedataDFS(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.AcqComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      System.out.println("category " + category + "subCat " + subCat + "filedate " + filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      AcqCompareDFS acqComparedata = new AcqCompareDFS( getJdbcTemplate());
      Map<String, Object> outParams = acqComparedata.execute(inParams);
      logger.info("Outparam is " + outParams);
      logger.info("***** ReconProcessDaoImpl.AcqComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null) {
        System.out.println(outParams.get("ERROR_MESSAGE"));
        return false;
      } 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.AcqComparedata");
      logger.error(" error in  ReconProcessDaoImpl.AcqComparedata", 
          new Exception(" ReconProcessDaoImpl.AcqComparedata", e));
      return false;
    } 
  }
  
  class AcqCompareDFS extends StoredProcedure {
		private static final String procName = "RECON_NFS_ACQ_PROC";

		AcqCompareDFS(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean AcqCompareJCB(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.AcqComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      System.out.println("category " + category + "subCat " + subCat + "filedate " + filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      AcqCompareJCB acqComparedata = new AcqCompareJCB( getJdbcTemplate());
      Map<String, Object> outParams = acqComparedata.execute(inParams);
      logger.info("Outparam is " + outParams);
      logger.info("***** ReconProcessDaoImpl.AcqComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null) {
        System.out.println(outParams.get("ERROR_MESSAGE"));
        return false;
      } 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.AcqComparedata");
      logger.error(" error in  ReconProcessDaoImpl.AcqComparedata", 
          new Exception(" ReconProcessDaoImpl.AcqComparedata", e));
      return false;
    } 
  }
  class AcqCompareJCB extends StoredProcedure {
		private static final String procName = "RECON_NFS_ACQ_PROC";

		AcqCompareJCB(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  
  public boolean AcqComparedataICD(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.AcqComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      System.out.println("category " + category + "subCat " + subCat + "filedate " + filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      AcqComparedataICD acqComparedata = new AcqComparedataICD( getJdbcTemplate());
      Map<String, Object> outParams = acqComparedata.execute(inParams);
      logger.info("Outparam is " + outParams);
      logger.info("***** ReconProcessDaoImpl.AcqComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null) {
        System.out.println(outParams.get("ERROR_MESSAGE"));
        return false;
      } 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.AcqComparedata");
      logger.error(" error in  ReconProcessDaoImpl.AcqComparedata", 
          new Exception(" ReconProcessDaoImpl.AcqComparedata", e));
      return false;
    } 
  }
  
  class AcqComparedataICD extends StoredProcedure {
		private static final String procName = "RECON_NFS_ACQ_PROC";

		AcqComparedataICD(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean cashnetISSClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.cashnetISSClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      cashnetIssClassificaton acqclassificaton = new cashnetIssClassificaton( getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.cashnetISSClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null) {
        logger.info("Error is " + outParams.get("ERROR_MESSAGE"));
        return false;
      } 
      logger.info("Procedure executed successfully");
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.cashnetISSClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.cashnetISSClassifydata", 
          new Exception(" ReconProcessDaoImpl.cashnetISSClassifydata", e));
      return false;
    } 
  }
  
  class cashnetIssClassificaton extends StoredProcedure {
		private static final String procName = "NFS_ACQ_CLASSIFY";

		cashnetIssClassificaton(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY", Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}

  public boolean MASTERCARDISSClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.MASTERCARDISSClassifydata Start ****");
      logger.info("category >>" + category);
      logger.info("subCat >>" + subCat);
      logger.info("filedate >>" + filedate);
      logger.info("entry_by >>" + entry_by);
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILEDATE", filedate);
      MASTERCARDIssClassificaton acqclassificaton = new MASTERCARDIssClassificaton( getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("outParams msg1 >>>" + outParams.toString());
      logger.info("***** ReconProcessDaoImpl.MASTERCARDISSClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.MASTERCARDISSClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.MASTERCARDISSClassifydata", 
          new Exception(" ReconProcessDaoImpl.MASTERCARDISSClassifydata", e));
      return false;
    } 
  }

	class MASTERCARDIssClassificaton extends StoredProcedure {
		private static final String procName = "RECON_MC_ACQ_DOM_PROC";

		MASTERCARDIssClassificaton(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}

  
  public boolean MASTERCARDISSposClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.MASTERCARDISSClassifydata Start ****");
      logger.info("category >>" + category);
      logger.info("subCat >>" + subCat);
      logger.info("filedate >>" + filedate);
      logger.info("entry_by >>" + entry_by);
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILEDATE", filedate);
      MASTERCARDISSposClassifydata acqclassificaton = new MASTERCARDISSposClassifydata(getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("outParams msg1 >>>" + outParams.toString());
      logger.info("***** ReconProcessDaoImpl.MASTERCARDISSClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.MASTERCARDISSClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.MASTERCARDISSClassifydata", 
          new Exception(" ReconProcessDaoImpl.MASTERCARDISSClassifydata", e));
      return false;
    } 
  }
  class MASTERCARDISSposClassifydata extends StoredProcedure {
		private static final String procName = "RECON_MC_ACQ_INT_PROC";

		MASTERCARDISSposClassifydata(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  
  public boolean MASTERCARDISSposClassifydataNew(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.MASTERCARDISSClassifydata Start ****");
      logger.info("category >>" + category);
      logger.info("subCat >>" + subCat);
      logger.info("filedate >>" + filedate);
      logger.info("entry_by >>" + entry_by);
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILEDATE", filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_ENTRY_BY", entry_by);
      MASTERCARDISSposClassifydataNew acqclassificaton = new MASTERCARDISSposClassifydataNew(getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("outParams msg1 >>>" + outParams.toString());
      logger.info("***** ReconProcessDaoImpl.MASTERCARDISSClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.MASTERCARDISSClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.MASTERCARDISSClassifydata", 
          new Exception(" ReconProcessDaoImpl.MASTERCARDISSClassifydata", e));
      return false;
    } 
  }
  
  class MASTERCARDISSposClassifydataNew extends StoredProcedure {
		private static final String procName = "RECON_MC_ISS_POS_CLASSIFY";

		MASTERCARDISSposClassifydataNew(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));

			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean cashnetISSComparedata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.cashnetISSComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      cashnetIssCompare issCompare = new cashnetIssCompare( getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.cashnetISSComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.cashnetISSComparedata");
      logger.error(" error in  ReconProcessDaoImpl.cashnetISSComparedata", 
          new Exception(" ReconProcessDaoImpl.cashnetISSComparedata", e));
      return false;
    } 
  }
  
  class cashnetIssCompare extends StoredProcedure {
		private static final String procName = "CASHNET_ISS_COMPARE_PROC";

		cashnetIssCompare(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean cashnetAcqClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.cashnetAcqClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      cashnetAcqClassificaton acqclassificaton = new cashnetAcqClassificaton( getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.cashnetAcqClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.cashnetAcqClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.cashnetAcqClassifydata", 
          new Exception(" ReconProcessDaoImpl.cashnetAcqClassifydata", e));
      return false;
    } 
  }
  
	class cashnetAcqClassificaton extends StoredProcedure {
		private static final String procName = "CASHNET_ACQ_Classify";

		cashnetAcqClassificaton(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean cashnetAcqComparedata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.cashnetAcqComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      cashnetAcqCompare acqComparedata = new cashnetAcqCompare( getJdbcTemplate());
      Map<String, Object> outParams = acqComparedata.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.cashnetAcqComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.cashnetAcqComparedata");
      logger.error(" error in  ReconProcessDaoImpl.cashnetAcqComparedata", 
          new Exception(" ReconProcessDaoImpl.cashnetAcqComparedata", e));
      return false;
    } 
  }
	class cashnetAcqCompare extends StoredProcedure {
		private static final String procName = "CASHNET_ACQ_COMPARE_PROC";

		cashnetAcqCompare(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean WCCComparedata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.WCCComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      WCCCompare wccComparedata = new WCCCompare( getJdbcTemplate());
      Map<String, Object> outParams = wccComparedata.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.WCCComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.WCCComparedata");
      logger.error(" error in  ReconProcessDaoImpl.WCCComparedata", 
          new Exception(" ReconProcessDaoImpl.WCCComparedata", e));
      return false;
    } 
  }
  
	class WCCCompare extends StoredProcedure {
		private static final String procName = "CASHNET_ACQ_COMPARE_PROC";

		WCCCompare(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean insertDailyRawData(String date) throws Exception {
    String updattab = " select count(1) from CBS_RUPAY_RAWDATA_copy  ";
    logger.info(updattab);
    int count = ((Integer)getJdbcTemplate().queryForObject(updattab, Integer.class)).intValue();
    logger.info(Integer.valueOf(count));
    if (count == 0) {
      String query = " insert into CBS_RUPAY_RAWDATA_copy ( Select * from  CBS_RUPAY_RAWDATA where filedate = to_date('" + 
        date + "', 'DD-MON-RRRR') )";
      logger.info(query);
      getJdbcTemplate().execute(query);
    } 
    return true;
  }
  
  public boolean truncateDailyRawData() throws Exception {
    String query = " truncate table CBS_RUPAY_RAWDATA_copy  ";
    logger.info(query);
    getJdbcTemplate().execute(query);
    return true;
  }
  
  public boolean OnusCardLessCompare(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.cardless Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      cardlessCompare issCompare = new cardlessCompare( getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.ONUScardless End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.cashnetISSComparedata");
      logger.error(" error in  ReconProcessDaoImpl.cashnetISSComparedata", 
          new Exception(" ReconProcessDaoImpl.cashnetISSComparedata", e));
      return false;
    } 
  }
	class cardlessCompare extends StoredProcedure {
		private static final String procName = "CASHNET_ACQ_COMPARE_PROC";

		cardlessCompare(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  
  public boolean VisaIssDCBS(String category, String subCat, String filedate) throws ParseException, Exception {
	return false;}
  
  public void getResponseCodeForOnusPos(String fileDate) throws ParseException, Exception {
    try {
      String tableName = "RESPONSECODE_ONUS_POS_DATA";
      if (((Integer)getJdbcTemplate().queryForObject("SELECT count (*) FROM tab WHERE tname  = '" + tableName + "'", 
          new Object[0], Integer.class)).intValue() > 0) {
        getJdbcTemplate().execute("DROP TABLE " + tableName);
        logger.info("table dropped");
      } 
      logger.info("Now Creating table");
      String createTemp = "CREATE TABLE " + tableName + 
        " AS ( SELECT DISTINCT (T1.DCRS_REMARKS || ' ('||T2.RESPCODE||')') AS DCRS_REMARKS,t1.SEG_TRAN_ID,t1.CREATEDBY,t1.CREATEDDATE,t1.FILEDATE,t1.FORACID,t1.TRAN_DATE,t1.E,t1.AMOUNT,t1.BALANCE,t1.TRAN_ID,t1.VALUE_DATE,t1.REMARKS,t1.REF_NO," + 
        "t1.PARTICULARALS,t1.CONTRA_ACCOUNT,t1.PSTD_USER_ID,t1.ENTRY_DATE,t1.VFD_DATE,t1.PARTICULARALS2,t1.ORG_ACCT,t1.SYS_REF,t1.MAN_CONTRA_ACCOUNT " + 
        "FROM SETTLEMENT_pos_CBS t1 INNER JOIN SETTLEMENT_pos_SWITCH t2 ON(  ( t1.REMARKS =  t2.PAN) AND  (SUBSTR( t1.REF_NO,2,6) =  SUBSTR( t2.TRACE,2,6)) " + 
        "AND  TO_NUMBER( REPLACE(t1.AMOUNT,',','')) =  TO_NUMBER( REPLACE(t2.AMOUNT,',','')) ) where t1.DCRS_REMARKS = 'POS_ONU_CBS_UNMATCHED' AND t2.DCRS_REMARKS = 'POS_ONU' " + 
        "and T1.FILEDATE = '" + fileDate + "')";
      logger.info("create table query is " + createTemp);
      getJdbcTemplate().execute(createTemp);
      String deleteFromSettlement = "DELETE from SETTLEMENT_POS_CBS OS1 WHERE FILEDATE = '" + fileDate + 
        "' AND OS1.DCRS_REMARKS= 'POS_ONU_CBS_UNMATCHED' AND EXISTS " + "(SELECT 1 FROM " + tableName + 
        " OS2 WHERE OS1.REMARKS = OS2.REMARKS AND SUBSTR( OS1.REF_NO,2,6) = SUBSTR( OS2.REF_NO,2,6) " + 
        "AND TO_NUMBER( REPLACE(OS1.AMOUNT,',','')) = TO_NUMBER( REPLACE(OS2.AMOUNT,',','')))";
      logger.info("DELETE QUERY IS " + deleteFromSettlement);
      getJdbcTemplate().execute(deleteFromSettlement);
      String InsertQuery = "INSERT INTO SETTLEMENT_POS_CBS (DCRS_REMARKS,SEG_TRAN_ID,CREATEDBY,CREATEDDATE,FILEDATE,FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,PSTD_USER_ID,ENTRY_DATE,VFD_DATE,PARTICULARALS2,ORG_ACCT,SYS_REF,MAN_CONTRA_ACCOUNT) ( SELECT * FROM " + 
        
        tableName + " )";
      logger.info("Insert query is " + InsertQuery);
      getJdbcTemplate().execute(InsertQuery);
      logger.info("Completed inserting in settlement table");
      getJdbcTemplate().execute("DROP TABLE " + tableName);
      logger.info("Table dropped");
    } catch (Exception e) {
      logger.info("Exception in getResponseCodeForOnusPos " + e);
      demo.logSQLException(e, "ReconProcessDaoImpl.getResponseCodeForOnusPos");
    } 
  }
  
  public boolean DomesticClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.DomesticClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      System.out.println("I_FILEDATE " + filedate + " I_CATEGORY" + category + "I_SUBCATEGORY  " + subCat);
      inParams.put("I_FILEDATE", filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_ENTRY_BY", entry_by);
      DomesticClassificaton acqclassificaton = new DomesticClassificaton( getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.DomesticClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.DomesticClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.DomesticClassifydata", 
          new Exception(" ReconProcessDaoImpl.DomesticClassifydata", e));
      return false;
    } 
  }
  
  class DomesticClassificaton extends StoredProcedure {
		private static final String procName = "RUPAY_DOM_CLASSIFY";

		DomesticClassificaton(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}

  public boolean DomesticClassifydataQSPARC(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
	return false;}
  
  public boolean InternationalClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.DomesticClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILEDATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      InternationalClassificaton acqclassificaton = new InternationalClassificaton( getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.DomesticClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.DomesticClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.DomesticClassifydata", 
          new Exception(" ReconProcessDaoImpl.DomesticClassifydata", e));
      return false;
    } 
  }
  class InternationalClassificaton extends StoredProcedure {
		// private static final String procName = "RUPAY_INT_CLASSIFY";
		// //RUPAY_INT_CLASSIFY_1
		private static final String procName = "RUPAY_INT_CLASSIFY";

		InternationalClassificaton(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean DomesticComparedata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata Start ****");
      String response = null;
      String mdate = "01-OCT-2023";
      System.out.println("date coming for processing i " + filedate + " cat " + category + " sub cat " + subCat);
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_ENTRY_BY", entry_by);
      DomesticCompare issCompare = new DomesticCompare( getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.DomesticComparedata");
      logger.error(" error in  ReconProcessDaoImpl.DomesticComparedata", 
          new Exception(" ReconProcessDaoImpl.DomesticComparedata", e));
      return false;
    } 
  }
  class DomesticCompare extends StoredProcedure {
		private static final String procName = "RECON_RUPAY_DOM_PROC";

		DomesticCompare(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}

  public boolean DomesticComparedataQSparc(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
	return false;}
  
  public boolean InternationalComparedata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_ENTRY_BY", entry_by);
      InternationalCompare issCompare = new InternationalCompare(getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.DomesticComparedata");
      logger.error(" error in  ReconProcessDaoImpl.DomesticComparedata", 
          new Exception(" ReconProcessDaoImpl.DomesticComparedata", e));
      return false;
    } 
  }
  class InternationalCompare extends StoredProcedure {
		// private static final String procName = "RECON_RUPAY_INT";
		private static final String procName = "RECON_RUPAY_INT_PROC";

		InternationalCompare(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean VisaIssClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.VisaIssClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      VisaIssClassificaton acqclassificaton = new VisaIssClassificaton(getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.VisaIssClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.VisaIssClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.VisaIssClassifydata", 
          new Exception(" ReconProcessDaoImpl.VisaIssClassifydata", e));
      return false;
    } 
  }
  class VisaIssClassificaton extends StoredProcedure {
		private static final String procName = "VISA_ISS_CLASSIFY";

		VisaIssClassificaton(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  
  public boolean VisaClassifydataACQVISA(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.VisaIssClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      VisaClassifydataACQVISA acqclassificaton = new VisaClassifydataACQVISA( getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.VisaIssClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.VisaIssClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.VisaIssClassifydata", 
          new Exception(" ReconProcessDaoImpl.VisaIssClassifydata", e));
      return false;
    } 
  }
  
  class VisaClassifydataACQVISA extends StoredProcedure {
		private static final String procName = "VISA_ACQ_DOM_ATM_CLASSIFY";

		VisaClassifydataACQVISA(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  
  public boolean VisaCompareData(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_ENTRY_BY", entry_by);
      VisaIssCompare issCompare = new VisaIssCompare( getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.DomesticComparedata");
      logger.error(" error in  ReconProcessDaoImpl.DomesticComparedata", 
          new Exception(" ReconProcessDaoImpl.DomesticComparedata", e));
      return false;
    } 
  }
  
  class VisaIssCompare extends StoredProcedure {
		private static final String procName = "RECON_VISA_ISS_PROC";

		VisaIssCompare(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean VisaCompareDataACQVISA(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_ENTRY_BY", entry_by);
      VisaCompareDataACQVISA issCompare = new VisaCompareDataACQVISA( getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.DomesticComparedata");
      logger.error(" error in  ReconProcessDaoImpl.DomesticComparedata", 
          new Exception(" ReconProcessDaoImpl.DomesticComparedata", e));
      return false;
    } 
  }
  class VisaCompareDataACQVISA extends StoredProcedure {
		private static final String procName = "RECON_VISA_ACQ_DOM_ATM_PROC";

		VisaCompareDataACQVISA(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  
  public boolean MASTERCARDCompareData(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_ENTRY_BY", entry_by);
      MASTERCARDCompareData issCompare = new MASTERCARDCompareData( getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.DomesticComparedata");
      logger.error(" error in  ReconProcessDaoImpl.DomesticComparedata", 
          new Exception(" ReconProcessDaoImpl.DomesticComparedata", e));
      return false;
    } 
  }
  class MASTERCARDCompareData extends StoredProcedure {
		private static final String procName = "RECON_MC_ISS_ATM_PROC";

		MASTERCARDCompareData(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean MASTERCARDCompareDataSACQ(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILEDATE", filedate);
      MASTERCARDCompareDataSACQ issCompare = new MASTERCARDCompareDataSACQ( getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata End ****");
      if (outParams.get("MSG") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.DomesticComparedata");
      logger.error(" error in  ReconProcessDaoImpl.DomesticComparedata", 
          new Exception(" ReconProcessDaoImpl.DomesticComparedata", e));
      return false;
    } 
  }
	class MASTERCARDCompareDataSACQ extends StoredProcedure {
		private static final String procName = "RECON_MC_ACQ_ATM_PROC";

		MASTERCARDCompareDataSACQ(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
		

			declareParameter(new SqlOutParameter("MSG",  Types.VARCHAR));
			compile();
		}
	}
  public boolean MASTERCARDposCompareData(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_ENTRY_BY", entry_by);
      MASTERCARDposCompareData issCompare = new MASTERCARDposCompareData( getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.DomesticComparedata");
      logger.error(" error in  ReconProcessDaoImpl.DomesticComparedata", 
          new Exception(" ReconProcessDaoImpl.DomesticComparedata", e));
      return false;
    } 
  }
  class MASTERCARDposCompareData extends StoredProcedure {
		private static final String procName = "RECON_MC_ISS_POS_PROC";

		MASTERCARDposCompareData(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}

  public boolean VisaAcqClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.VisaIssClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_ENTRY_BY", entry_by);
      VisaAcqClassificaton acqclassificaton = new VisaAcqClassificaton( getJdbcTemplate());
      Map<String, Object> outParams = acqclassificaton.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.VisaIssClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.VisaIssClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.VisaIssClassifydata", 
          new Exception(" ReconProcessDaoImpl.VisaIssClassifydata", e));
      return false;
    } 
  }
  class VisaAcqClassificaton extends StoredProcedure {
		private static final String procName = "VISA_ACQ_INT_ATM_CLASSIFY";

		VisaAcqClassificaton(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean VisaISSPOSINTClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
	return false;}
  
  public boolean VisaISSINTATMClassifydata(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
	return false;}
  
  public boolean VisaISSPOSCompare(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_CATEGORY", category);
      inParams.put("I_SUBCATEGORY", subCat);
      inParams.put("I_ENTRY_BY", entry_by);
      VisaISSPOSCompare issCompare = new VisaISSPOSCompare(getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.DomesticComparedata");
      logger.error(" error in  ReconProcessDaoImpl.DomesticComparedata", 
          new Exception(" ReconProcessDaoImpl.DomesticComparedata", e));
      return false;
    } 
  }
  class VisaISSPOSCompare extends StoredProcedure {
		private static final String procName = "RECON_VISA_ACQ_INT_ATM_PROC";

		VisaISSPOSCompare(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",  Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean VisaISSINTPOSCompareData(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
	return false;}
  
  public boolean VisaISSINTATMCompareData(String category, String subCat, String filedate, String entry_by) throws ParseException, Exception {
	return false;}
  
  public boolean CardtoCardACQPRC(String category, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.CardtoCardCompareData Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILE_DATE", filedate);
      CardtoCardACQPRC issCompare = new CardtoCardACQPRC(getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.CardtoCardCompareData End ****");
      logger.info("outParams " + outParams.toString());
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.CardtoCardCompareData");
      logger.error(" error in  ReconProcessDaoImpl.CardtoCardCompareData", 
          new Exception(" ReconProcessDaoImpl.CardtoCardCompareData", e));
      return false;
    } 
  }
  class CardtoCardACQPRC extends StoredProcedure {
		private static final String procName = "RECON_NFS_C2C_ACQ_PROC";

		CardtoCardACQPRC(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
			// declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  
  public boolean VISACROSSPROCPOSINTDOM(String category, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.CardtoCardCompareData Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILEDATE", filedate);
      VISACROSSPROCPOSINTDOM issCompare = new VISACROSSPROCPOSINTDOM( getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      System.out.println("outParams " + outParams.toString());
      if (outParams != null && outParams.get("msg") == "FAILED") {
        logger.info("OUT PARAM IS " + outParams.get("msg"));
        return false;
      } 
      if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
        logger.info("OUT PARAM IS " + outParams.get("msg"));
        return false;
      } 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.CardtoCardCompareData");
      logger.error(" error in  ReconProcessDaoImpl.CardtoCardCompareData", 
          new Exception(" ReconProcessDaoImpl.CardtoCardCompareData", e));
      return false;
    } 
  }

	class VISACROSSPROCPOSINTDOM extends StoredProcedure {
		private static final String procName = "RECON_MC_ACQ_CROSS_RECON";

		VISACROSSPROCPOSINTDOM(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
		
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  
  public boolean VISACROSSPROCATMINTDOM(String category, String filedate, String entry_by) throws ParseException, Exception {
	return false;}
  
  public boolean VISACROSSPROCPOSDOMINT(String category, String filedate, String entry_by) throws ParseException, Exception {
	return false;}
  
  public boolean VISACROSSPROCATMDOMINT(String category, String filedate, String entry_by) throws ParseException, Exception {
	return false;}
  
  public boolean CardtoCardISSPRC(String category, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.CardtoCardCompareData Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILEDATE", filedate);
      CardtoCardISSPRC issCompare = new CardtoCardISSPRC( getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.CardtoCardCompareData End ****");
      logger.info("outParams " + outParams.toString());
      if (outParams.get("msg") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.CardtoCardCompareData");
      logger.error(" error in  ReconProcessDaoImpl.CardtoCardCompareData", 
          new Exception(" ReconProcessDaoImpl.CardtoCardCompareData", e));
      return false;
    } 
  }

	class CardtoCardISSPRC extends StoredProcedure {
		private static final String procName = "RECON_C2C_PROC";

		CardtoCardISSPRC(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
		
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public boolean CardtoCardACQClassify(String category, String filedate, String entry_by) throws ParseException, Exception {
	return false;}
  
  public boolean CardtoCardISSClassify(String category, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.CardtoCardACQClassify Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_FILEDATE", filedate);
      CardtoCardISSClassify issCompare = new CardtoCardISSClassify( getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.CardtoCardCompareData End ****");
      logger.info("outParams " + outParams.toString());
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.CardtoCardCompareData");
      logger.error(" error in  ReconProcessDaoImpl.CardtoCardCompareData", 
          new Exception(" ReconProcessDaoImpl.CardtoCardCompareData", e));
      return false;
    } 
  }
  class CardtoCardISSClassify extends StoredProcedure {
		private static final String procName = "NFS_ISS_C2C_CLASSIFY";

		CardtoCardISSClassify(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			// declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public HashMap<String, Object> checkRupayIntRecon(String fileDate, String cetegory) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      String checkReconProcess = "";
      if (cetegory.equalsIgnoreCase("QSPARC")) {
        checkReconProcess = "select COUNT(*) from main_file_upload_dtls T1 where filedate = ? AND FILE_SUBCATEGORY = 'INTERNATIONAL' AND CATEGORY = 'QSPARC' AND T1.COMAPRE_FLAG = 'Y' and filter_flag = (SELECT FILTERATION FROM MAIN_FILESOURCE T2 WHERE T1.FILEID = T2.FILEID AND T1.CATEGORY = T2.FILE_CATEGORY AND T1.FILE_SUBCATEGORY = T2.FILE_SUBCATEGORY AND T1.FILTER_FLAG = T2.FILTERATION AND T1.KNOCKOFF_FLAG = T2.KNOCKOFF) ";
      } else {
        checkReconProcess = "select COUNT(*) from main_file_upload_dtls T1 where filedate = ? AND FILE_SUBCATEGORY = 'INTERNATIONAL' AND CATEGORY = 'RUPAY' AND T1.COMAPRE_FLAG = 'Y' and filter_flag = (SELECT FILTERATION FROM MAIN_FILESOURCE T2 WHERE T1.FILEID = T2.FILEID AND T1.CATEGORY = T2.FILE_CATEGORY AND T1.FILE_SUBCATEGORY = T2.FILE_SUBCATEGORY AND T1.FILTER_FLAG = T2.FILTERATION AND T1.KNOCKOFF_FLAG = T2.KNOCKOFF) ";
      } 
      int checkReconCount = ((Integer)getJdbcTemplate().queryForObject(checkReconProcess, new Object[] { fileDate }, Integer.class)).intValue();
      if (checkReconCount > 0) {
        output.put("result", Boolean.valueOf(true));
        output.put("msg", "Recon is already processed");
      } else {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "Recon is not processed");
      } 
    } catch (Exception e) {
      logger.info("Exception in checkRupayIntRecon " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception in checkRupayIntRecon");
    } 
    return output;
  }
  
  public HashMap<String, Object> processRupayIntRecon(String fileDate, String entryBy) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      logger.info("***** ReconProcessDaoImpl.processRupayIntRecon Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("FILEDT", fileDate);
      inParams.put("USER_ID", entryBy);
      RupayIntProcess issCompare = new RupayIntProcess( getJdbcTemplate());
      Map<String, Object> outParams = issCompare.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.DomesticComparedata End ****");
      if (outParams.get("ERROR_MESSAGE") != null) {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "Recon not processed");
      } else {
        output.put("result", Boolean.valueOf(true));
      } 
    } catch (Exception e) {
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception is " + e);
    } 
    return output;
  }
  class RupayIntProcess extends StoredProcedure {
		private static final String procName = "RECON_RUPAY_INT";

		RupayIntProcess(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",  Types.VARCHAR));
			compile();
		}
	}
  public HashMap<String, Object> checkCardtoCardRecon(String fileDate, String subCat) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      if (subCat.equalsIgnoreCase("ACQUIRER")) {
        String checkReconProcess = "SELECT count(*) from SWITCH_RAWDATA WHERE TO_DATE(FILEDATE ,'DD-MM-YY') =TO_DATE(? ,'DD-MM-YY') AND DCRS_REMARKS='C2C' AND \r\n    TRAN_CDE IN ('TC0011','TC0001','TC0000')AND RESP_CDE='000'";
        int checkReconCount = ((Integer)getJdbcTemplate().queryForObject(checkReconProcess, new Object[] { fileDate }, Integer.class)).intValue();
        String checkReconProcess2 = "SELECT count(*) from CBS_RUPAY_RAWDATA WHERE TO_DATE(FILEDATE ,'DD-MM-YY') =TO_DATE(? ,'DD-MM-YY') AND DCRS_REMARKS='C2C_ACQ'";
        int checkReconCount2 = ((Integer)getJdbcTemplate().queryForObject(checkReconProcess2, new Object[] { fileDate }, Integer.class)).intValue();
        String checkReconProcess3 = "SELECT count(*) FROM NFS_NFS_ACQ_RAWDATA WHERE TO_DATE(FILEDATE ,'DD-MM-YY') =TO_DATE(? ,'DD-MM-YY') AND RESPONSE_CODE='00' AND TRANSACTION_TYPE='FT'";
        int checkReconCount3 = ((Integer)getJdbcTemplate().queryForObject(checkReconProcess3, new Object[] { fileDate }, Integer.class)).intValue();
        logger.info("query c " + checkReconCount + " " + checkReconCount2 + " " + checkReconCount3);
        if (checkReconCount > 0 && checkReconCount2 > 0 && checkReconCount3 > 0) {
          System.out.println("sd ");
          output.put("result", Boolean.valueOf(false));
          output.put("msg", "Go for Proccess");
        } else {
          output.put("result", Boolean.valueOf(true));
          output.put("msg", "File is not Uploaded");
        } 
      } else {
        String checkReconProcess = "SELECT count(*) FROM CBS_RUPAY_RAWDATA WHERE TO_DATE(FILEDATE ,'DD-MM-YY') =TO_DATE(? ,'DD-MM-YY') AND DCRS_REMARKS='C2C_ISS'";
        int checkReconCount = ((Integer)getJdbcTemplate().queryForObject(checkReconProcess, new Object[] { fileDate }, Integer.class)).intValue();
        String checkReconProcess2 = "SELECT count(*) from SWITCH_RAWDATA WHERE TO_DATE(FILEDATE ,'DD-MM-YY') =TO_DATE(? ,'DD-MM-YY') AND DCRS_REMARKS='C2C' \r\n    AND TRAN_CDE IN ('TD0000','TD0100','TD1100') AND RESP_CDE='000'";
        int checkReconCount2 = ((Integer)getJdbcTemplate().queryForObject(checkReconProcess2, new Object[] { fileDate }, Integer.class)).intValue();
        String checkReconProcess3 = "SELECT count(*) FROM NFS_NFS_ISS_RAWDATA WHERE TO_DATE(FILEDATE ,'DD-MM-YY') =TO_DATE(? ,'DD-MM-YY') AND RESPONSE_CODE='00' AND TRANSACTION_TYPE='TD'";
        int checkReconCount3 = ((Integer)getJdbcTemplate().queryForObject(checkReconProcess3, new Object[] { fileDate }, Integer.class)).intValue();
        logger.info("query c " + checkReconCount + " " + checkReconCount2 + " " + checkReconCount3);
        if (checkReconCount > 0 && checkReconCount2 > 0 && checkReconCount3 > 0) {
          output.put("result", Boolean.valueOf(false));
          output.put("msg", "go for Proccess");
        } else {
          output.put("result", Boolean.valueOf(true));
          output.put("msg", "File is not Uploaded");
        } 
      } 
    } catch (Exception e) {
      logger.info("Exception in checkRupayIntRecon " + e);
      output.put("result", Boolean.valueOf(true));
      output.put("msg", "Exception in checkRupayIntRecon");
    } 
    return output;
  }
  
  public HashMap<String, Object> checkCardtoCardRawFiles(String filedate) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      String checkHostFileUpload = "SELECT COUNT(*) FROM MAIN_FILE_UPLOAD_DTLS WHERE FILEDATE = ? AND CATEGORY = 'CARDTOCARD'";
      String checkNFSFileUpload = "SELECT COUNT(*) FROM MAIN_FILE_UPLOAD_DTLS WHERE FILEDATE = ? AND CATEGORY = 'NFS' AND FILE_SUBCATEGORY = 'ISSUER' AND FILEID = (SELECT FILEID FROM MAIN_FILESOURCE WHERE FILENAME = 'NFS' AND FILE_CATEGORY = 'CARDTOCARD' AND FILE_SUBCATEGORY = 'ISSUER')";
      int hostFileCount = ((Integer)getJdbcTemplate().queryForObject(checkHostFileUpload, new Object[] { filedate }, Integer.class)).intValue();
      int NFSFileCount = ((Integer)getJdbcTemplate().queryForObject(checkNFSFileUpload, new Object[] { filedate }, Integer.class)).intValue();
      if (hostFileCount == 0) {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "Host File is not uploaded");
      } else if (NFSFileCount == 0) {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "NFS File is not uploaded");
      } else {
        output.put("result", Boolean.valueOf(true));
      } 
    } catch (Exception e) {
      logger.info("Exception in checkCardtoCardRawFiles " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception while checking file upload");
    } 
    return output;
  }
  
  public HashMap<String, Object> checkCardtoCardPrevRecon(String filedate) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      String checkPrevRecon = "SELECT COUNT(*) FROM main_file_upload_dtls where filedate = to_date('" + filedate + 
        "','dd/mm/yyyy')-1 and category = 'CARDTOCARD'";
      int prevCount = ((Integer)getJdbcTemplate().queryForObject(checkPrevRecon, new Object[0], Integer.class)).intValue();
      if (prevCount == 0) {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "Previous day recon is not processed!");
      } else {
        output.put("result", Boolean.valueOf(true));
      } 
    } catch (Exception e) {
      logger.info("Exception in checkCardtoCardPrevRecon " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception while checking previous date recon!");
    } 
    return output;
  }
  
  public boolean iccwprocessFile(String category, String filedate, String createdby) {
    boolean result = false;
    String stCategory = category;
    System.out.println("category is " + stCategory);
    logger.info("***** ReconProcessDaoImpl.processFile Start ****");
    try {
      IccwAcqProcess(category, filedate, createdby);
    } catch (Exception e) {
      System.out.println(e);
    } 
    return result;
  }
  
  public boolean IccwAcqProcess(String category, String filedate, String entry_by) throws ParseException, Exception {
    try {
      logger.info("***** ReconProcessDaoImpl.DomesticClassifydata Start ****");
      String response = null;
      Map<String, Object> inParams = new HashMap<>();
      inParams.put("I_CATEGORY", category);
      inParams.put("I_FILE_DATE", filedate);
      inParams.put("I_ENTRY_BY", entry_by);
      System.out.println("category for proc is" + category);
      System.out.println("date for proc is " + filedate);
      System.out.println("entry by for proc is" + entry_by);
      AcqClassification iccwacqclassification = new AcqClassification( getJdbcTemplate());
      Map<String, Object> outParams = iccwacqclassification.execute(inParams);
      logger.info("***** ReconProcessDaoImpl.DomesticClassifydata End ****");
      if (outParams.get("ERROR_MESSAGE") != null)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "ReconProcessDaoImpl.DomesticClassifydata");
      logger.error(" error in  ReconProcessDaoImpl.DomesticClassifydata", 
          new Exception(" ReconProcessDaoImpl.DomesticClassifydata", e));
      return false;
    } 
  }

	class AcqClassification extends StoredProcedure {
//	private static final String procName = "ICCW_UCO_RECON";
		private static final String procName = "ICCW_UCO_RECON_1";

		AcqClassification(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",  Types.VARCHAR));
//		declareParameter(new SqlParameter("I_CATEGORY",  Types.VARCHAR));
//		declareParameter(new SqlParameter("I_SUBCATEGORY",
//				 Types.VARCHAR));
//		declareParameter(new SqlParameter("I_ENTRY_BY",  Types.VARCHAR));
//		declareParameter(new SqlOutParameter("ERROR_CODE",
//				 Types.VARCHAR));
//		declareParameter(new SqlOutParameter("ERROR_MESSAGE",
//				 Types.VARCHAR));
			compile();
		}
	}

  public boolean checkRecon(String filedate) {
    String checkPrevRecon = "SELECT COUNT(*) FROM SETTLEMENT_ISS_ICCW_SWITCH where filedate = to_date('" + filedate + 
      "','dd/mm/yyyy')";
    int Count = ((Integer)getJdbcTemplate().queryForObject(checkPrevRecon, new Object[0], Integer.class)).intValue();
    if (Count > 0)
      return true; 
    return false;
  }
  
  public boolean checkFileUp(String filedate) {
    String checkSwitchRecon = "SELECT COUNT(*) FROM ICCW_SWITCH_DATA_CUB where filedate =  to_date('" + filedate + 
      "','dd/mm/yyyy')";
    int switchCount = ((Integer)getJdbcTemplate().queryForObject(checkSwitchRecon, new Object[0], Integer.class)).intValue();
    String checkCbsRecon = "SELECT COUNT(*) FROM ICCW_CBS_DATA_CUB  where filedate =  to_date('" + filedate + 
      "','dd/mm/yyyy')";
    int cbsCount = ((Integer)getJdbcTemplate().queryForObject(checkCbsRecon, new Object[0], Integer.class)).intValue();
    if (switchCount > 0 && cbsCount > 0)
      return true; 
    return false;
  }
}
