package com.recon.dao.impl;


import com.recon.dao.ICompareConfigDao;
import com.recon.model.CompareSetupBean;
import com.recon.model.FileColumnDtls;
import com.recon.model.FileSourceBean;
import com.recon.model.ManualCompareBean;
import com.recon.model.ManualFileColumnDtls;
import com.recon.model.Pos_Bean;
import com.recon.model.ReadVisaFile;
import com.recon.service.ICompareConfigService;
import com.recon.service.NFSSettlementCalService;
import com.recon.service.NFSSettlementService;
import com.recon.util.OracleConn;
import com.recon.util.Pos_Reading;
import com.recon.util.ReadCardToCardCBS;
import com.recon.util.ReadCashNetFile;
import com.recon.util.ReadMastercard461Rawdata;
import com.recon.util.ReadNUploadCBSFiles;
import com.recon.util.ReadNUploadOnusPosFile;
import com.recon.util.ReadNfsRawData;
import com.recon.util.ReadRupay;
import com.recon.util.ReadRupay88File;
import com.recon.util.ReadUCOSwitchFile;
import com.recon.util.Utility;
import com.recon.util.demo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CompareConfigDaoImpl extends JdbcDaoSupport implements ICompareConfigDao {
  @Autowired
  NFSSettlementService nfsSettlementService;
  
  @Autowired
  ICompareConfigService icompareConfigService;
  
  @Autowired
  NFSSettlementCalService nfsSettlementCalService;
  
  private static final String O_ERROR_MESSAGE = "o_error_message";
  
  private static final int STRING = 0;
  
  private static final int NUMARIC = 0;
  
  int reconcount = 0;
  
  int manreconcount = 0;
  
  int manmatchcount = 0;
  
  private PlatformTransactionManager transactionManager;
  
  private Integer uploadcount;
  
  private Integer filecount;
  
  String upload_flag = "Y";
  
  String man_flag = "N";
  
  String value = null;
  
  Statement st;
  
  Connection con;
  
  public void setTransactionManager() {
    try {
      this.logger.info("***** CompareConfigDaoImpl.setTransactionManager Start ****");
      ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext();
      classPathXmlApplicationContext = new ClassPathXmlApplicationContext("/resources/bean.xml");
      this.logger.info("in settransactionManager");
      this.transactionManager = (PlatformTransactionManager)classPathXmlApplicationContext.getBean("transactionManager");
      this.logger.info(" settransactionManager completed");
      this.logger.info("***** CompareConfigDaoImpl.setTransactionManager End ****");
      classPathXmlApplicationContext.close();
    } catch (Exception ex) {
      this.logger.error(" error in CompareConfigDaoImpl.setTransactionManager", 
          new Exception("CompareConfigDaoImpl.setTransactionManager", ex));
    } 
  }
  
  public List<CompareSetupBean> getFileDetails() {
    this.logger.info("***** CompareConfigDaoImpl.getFileDetails Start ****");
    List<CompareSetupBean> filelist = null;
    try {
      String query = "SELECT filesrc.Fileid as inFileId , filesrc.FileName as stFileName ,filesrc.dataseparator ,filesrc.rddatafrm ,filesrc.charpatt, filesrc.Activeflag as activeFlag   FROM main_filesource filesrc  WHERE activeFlag='A' ";
      this.logger.info("query==" + query);
      filelist = getJdbcTemplate().query(query, (RowMapper)new BeanPropertyRowMapper(CompareSetupBean.class));
      this.logger.info("***** CompareConfigDaoImpl.getFileDetails End ****");
    } catch (Exception ex) {
      this.logger.error(" error in CompareConfigDaoImpl.getFileDetails", 
          new Exception("CompareConfigDaoImpl.getFileDetails", ex));
      throw ex;
    } 
    return filelist;
  }
  
  public boolean saveCompareDetails(CompareSetupBean setupBean) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.saveCompareDetails Start ****");
    setTransactionManager();
    DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus status = this.transactionManager.getTransaction((TransactionDefinition)defaultTransactionDefinition);
    this.logger.info("status==" + status);
    List<FileColumnDtls> columnDtls = new ArrayList<>();
    columnDtls = setupBean.getColumnDtls();
    try {
      String category = setupBean.getStSubCategory().equals("") ? setupBean.getCategory() : (
        String.valueOf(setupBean.getCategory()) + "_" + setupBean.getStSubCategory());
      this.logger.info("category==" + category);
      boolean result = true;
      this.reconcount = ((Integer)getJdbcTemplate().queryForObject(
          "SELECT CASE WHEN  (SELECT MAX(rec_Set_ID) FROM MAIN_RECON_SEQUENCE where  category ='" + category + 
          "') is null then 0 else (SELECT MAX(rec_Set_ID) FROM MAIN_RECON_SEQUENCE where  category ='" + 
          category + "') end as FLAG from dual", 
          Integer.class)).intValue();
      this.reconcount++;
      this.logger.info("reconcount==" + this.reconcount);
      result = insertMain_ReconSetupDetails(setupBean);
      if (result)
        result = insertReconParam(setupBean, columnDtls); 
      this.logger.info("***** CompareConfigDaoImpl.saveCompareDetails End ****");
      return result;
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.saveCompareDetails");
      this.logger.error(" error in CompareConfigDaoImpl.saveCompareDetails", 
          new Exception("CompareConfigDaoImpl.saveCompareDetails", ex));
      return false;
    } 
  }
  
  private boolean insertReconParam(CompareSetupBean setupBean, List<FileColumnDtls> columnDtls) {
    this.logger.info("***** CompareConfigDaoImpl.insertReconParam Start ****");
    int count = 0;
    List<CompareSetupBean> setup_dtl_list = setupBean.getSetup_dtl_list();
    setTransactionManager();
    DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus status = this.transactionManager.getTransaction((TransactionDefinition)defaultTransactionDefinition);
    this.logger.info("status==" + status);
    try {
      String category = setupBean.getStSubCategory().equals("") ? setupBean.getCategory() : (
        String.valueOf(setupBean.getCategory()) + "_" + setupBean.getStSubCategory());
      this.logger.info("category==" + category);
      for (CompareSetupBean compareSetupBean : setup_dtl_list) {
        getJdbcTemplate().update(
            "Insert into MAIN_RECON_PARAM (CATEGORY,TABLE_HEADER,PADDING,START_CHARPOS,CONDITION,CHARSIZE,ENTRY_BY,ENTRY_DATE,TABLE_FILE_ID,PATTERN,REC_SET_ID)values ('" + 
            category + "','" + compareSetupBean.getTable_header() + "','" + 
            compareSetupBean.getStPadding() + "','" + compareSetupBean.getStart_charpos() + "'," + 
            "'" + compareSetupBean.getCondition() + "'," + compareSetupBean.getCharsize() + ",'" + 
            setupBean.getEntryBy() + "',sysdate," + setupBean.getCompareFile1() + ",'" + 
            compareSetupBean.getSrch_Pattern() + "'," + this.reconcount + ")");
        if (compareSetupBean.getMatchCondn().equals("Y"))
          getJdbcTemplate().update(
              "Insert into MAIN_MATCHING_CONDITION (CATEGORY,TABLE_HEADER,PADDING,START_CHARPOS,CONDITION,CHARSIZE,ENTRY_BY,ENTRY_DATE,TABLE_FILE_ID,PATTERN,REC_SET_ID)values ('" + 
              category + "','" + compareSetupBean.getTable_header() + "','" + 
              compareSetupBean.getStPadding() + "','" + compareSetupBean.getStart_charpos() + 
              "'," + "'" + compareSetupBean.getCondition() + "'," + 
              compareSetupBean.getCharsize() + ",'" + setupBean.getEntryBy() + "',sysdate," + 
              setupBean.getCompareFile1() + ",'" + compareSetupBean.getSrch_Pattern() + "'," + 
              this.reconcount + ")"); 
      } 
      for (FileColumnDtls fileColumnDtls : columnDtls) {
        count++;
        if (setupBean.getCompareFile1() > 0)
          getJdbcTemplate().update(
              "Insert into MAIN_MATCHING_CRITERIA (CATEGORY,MATCH_HEADER,PADDING,START_CHARPOS,CHAR_SIZE,ENTRY_BY,ENTRY_DATE,FILE_ID,DATATYPE,DATA_PATTERN,MATCH_ID,REC_SET_ID,RELAX_PARAM)values ('" + 
              category + "','" + fileColumnDtls.getFileColumn1() + "','" + 
              fileColumnDtls.getStPadding() + "','" + fileColumnDtls.getInStart_Char_Position() + 
              "'," + fileColumnDtls.getInEnd_char_position() + ",'" + 
              setupBean.getEntryBy() + "',sysdate," + setupBean.getCompareFile1() + ",'" + 
              fileColumnDtls.getDataType() + "'," + "'" + fileColumnDtls.getDatpattern() + "'," + 
              count + "," + this.reconcount + "," + setupBean.getStRelaxParam1() + ")"); 
        if (setupBean.getCompareFile2() > 0)
          getJdbcTemplate().update(
              "Insert into MAIN_MATCHING_CRITERIA (CATEGORY,MATCH_HEADER,PADDING,START_CHARPOS,CHAR_SIZE,ENTRY_BY,ENTRY_DATE,FILE_ID,DATATYPE,DATA_PATTERN,MATCH_ID,REC_SET_ID,RELAX_PARAM)values ('" + 
              category + "','" + fileColumnDtls.getFileColumn2() + "','" + 
              fileColumnDtls.getStPadding2() + "','" + 
              fileColumnDtls.getInStart_Char_Position2() + "'," + 
              fileColumnDtls.getInEnd_char_position2() + ",'" + setupBean.getEntryBy() + 
              "',sysdate," + setupBean.getCompareFile2() + ",'" + fileColumnDtls.getDataType2() + 
              "'," + "'" + fileColumnDtls.getDatpattern2() + "'," + count + "," + this.reconcount + 
              "," + setupBean.getStRelaxParam2() + ")"); 
        if (setupBean.getCompareFile3() > 0)
          getJdbcTemplate().update(
              "Insert into MAIN_MATCHING_CRITERIA (CATEGORY,MATCH_HEADER,PADDING,START_CHARPOS,CHAR_SIZE,ENTRY_BY,ENTRY_DATE,FILE_ID,DATATYPE,DATA_PATTERN,MATCH_ID,REC_SET_ID)values ('" + 
              category + "','" + fileColumnDtls.getFileColumn3() + "','" + 
              fileColumnDtls.getStPadding3() + "','" + 
              fileColumnDtls.getInStart_Char_Position3() + "'," + 
              fileColumnDtls.getInEnd_char_position3() + ",'" + setupBean.getEntryBy() + 
              "',sysdate," + setupBean.getCompareFile3() + ",'" + fileColumnDtls.getDataType3() + 
              "'," + "'" + fileColumnDtls.getDatpattern3() + "'," + count + "," + this.reconcount + 
              ",)"); 
      } 
      this.transactionManager.commit(status);
      this.logger.info("***** CompareConfigDaoImpl.insertReconParam Start ****");
      return true;
    } catch (Exception e) {
      this.transactionManager.rollback(status);
      this.logger.error(" error in CompareConfigDaoImpl.insertReconParam", 
          new Exception("CompareConfigDaoImpl.insertReconParam", e));
      return false;
    } 
  }
  
  public boolean chkMain_ReconSetupDetails(CompareSetupBean setupBean) {
    try {
      this.logger.info("***** CompareConfigDaoImpl.chkMain_ReconSetupDetails Start ****");
      String category = setupBean.getStSubCategory().equals("") ? setupBean.getCategory() : (
        String.valueOf(setupBean.getCategory()) + "_" + setupBean.getStSubCategory());
      this.logger.info("category==" + category);
      int rowcount = 0;
      String query = "SELECT count(*) from MAIN_RECON_SEQUENCE WHERE (file1 ='" + 
        setupBean.getCompreFileName1().toUpperCase() + "' or file1 ='" + 
        setupBean.getCompreFileName2().toUpperCase() + "' )" + "  AND (file2 = '" + 
        setupBean.getCompreFileName1().toUpperCase() + "' or file2='" + 
        setupBean.getCompreFileName2().toUpperCase() + "') " + " AND category='" + category + "' ";
      this.logger.info(query);
      rowcount = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
      this.logger.info(Integer.valueOf(rowcount));
      this.logger.info("***** CompareConfigDaoImpl.chkMain_ReconSetupDetails End ****");
      if (rowcount > 0)
        return false; 
      return true;
    } catch (Exception ex) {
      this.logger.error(" error in CompareConfigDaoImpl.chkMain_ReconSetupDetails", 
          new Exception("CompareConfigDaoImpl.chkMain_ReconSetupDetails", ex));
      return false;
    } 
  }
  
  public boolean insertMain_ReconSetupDetails(CompareSetupBean setupBean) {
    this.logger.info("***** CompareConfigDaoImpl.insertMain_ReconSetupDetails Start ****");
    String category = setupBean.getStSubCategory().equals("") ? setupBean.getCategory() : (
      String.valueOf(setupBean.getCategory()) + "_" + setupBean.getStSubCategory());
    this.logger.info("category==" + category);
    String query = "Insert into MAIN_RECON_SEQUENCE(rec_Set_ID,CATEGORY,FILE1,FILE2,FILE1_MATCHED,FILE2_MATCHED,entry_dt,entry_by) values(" + 
      this.reconcount + ",'" + category + "','" + setupBean.getCompreFileName1().toUpperCase() + "','" + 
      setupBean.getCompreFileName2().toUpperCase() + "','" + setupBean.getFile1match() + "','" + 
      setupBean.getFile2match() + "',sysdate,'" + setupBean.getEntryBy() + "') ";
    this.logger.info("query==" + query);
    int count = getJdbcTemplate().update(query);
    this.logger.info("count==" + count);
    this.logger.info("***** CompareConfigDaoImpl.insertMain_ReconSetupDetails End ****");
    if (count > 0)
      return true; 
    return false;
  }
  
  public ArrayList<CompareSetupBean> getCompareFiles(String type, String subcat) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.getCompareFiles Start ****");
    ArrayList<CompareSetupBean> compareSetupBeans = null;
    String query = null;
    subcat = (subcat == null) ? "" : subcat;
    String file_cat = null;
    file_cat = type;
    if (type.equals("MASTERCARD") || type.equals("RUPAY") || type.equals("VISA") || type.equals("POS")) {
      file_cat = type;
      type = String.valueOf(type) + "_" + subcat;
    } 
    try {
      String stCategory = subcat.equals("") ? (String.valueOf(type) + "_" + subcat) : type;
      if (subcat.equalsIgnoreCase("")) {
        query = " SELECT rec_set_id,RECON_CATEGORY as category,(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = (select FILEID from main_filesource where upper(filename) = mrecdet.FILE1 and FILE_CATEGORY='" + 
          
          type + "' and FILE_SUBCATEGORY='-')) compreFileName1 ," + 
          "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = (select FILEID from main_filesource where upper(filename) = mrecdet.FILE2 and FILE_CATEGORY='" + 
          type + "' and FILE_SUBCATEGORY='-') )compreFileName2 " + "  FROM MAIN_RECON_SEQUENCE mrecdet " + 
          " WHERE RECON_CATEGORY='" + type + "' ";
      } else {
        query = " SELECT rec_set_id,RECON_CATEGORY as category,(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = (select FILEID from main_filesource where upper(filename) = mrecdet.FILE1 and FILE_CATEGORY='" + 
          
          file_cat + "' and FILE_SUBCATEGORY='" + subcat + "')) compreFileName1 ," + 
          "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = (select FILEID from main_filesource where upper(filename) = mrecdet.FILE2 and FILE_CATEGORY='" + 
          file_cat + "' and FILE_SUBCATEGORY='" + subcat + "') )compreFileName2 " + 
          "  FROM MAIN_RECON_SEQUENCE mrecdet " + " WHERE RECON_CATEGORY='" + type + "' ";
      } 
      this.logger.info("query==" + query);
      compareSetupBeans = (ArrayList<CompareSetupBean>)getJdbcTemplate().query(query, 
          (RowMapper)new BeanPropertyRowMapper(CompareSetupBean.class));
      this.logger.info("***** CompareConfigDaoImpl.getCompareFiles End ****");
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.getCompareFiles");
      this.logger.error(" error in CompareConfigDaoImpl.getCompareFiles", 
          new Exception("CompareConfigDaoImpl.getCompareFiles", ex));
      throw ex;
    } 
    return compareSetupBeans;
  }
  
  public ArrayList<CompareSetupBean> getmatchcrtlist(int rec_set_id, String Cate) throws Exception {
    ArrayList<CompareSetupBean> compareSetupBeans = null;
    this.logger.info("***** CompareConfigDaoImpl.getmatchcrtlist Start ****");
    try {
      String query = " SELECT category,match_header as table_header,padding,start_charpos,char_size, (SELECT filename FROM main_filesource WHERE fileid = mmatchcrt.file_id)filename,datatype,match_id,entry_by,to_char(entry_date,'dd/MON/yyyy')entry_date  FROM main_matching_criteria mmatchcrt  WHERE mmatchcrt.REC_SET_ID=" + 
        
        rec_set_id + 
        " and mmatchcrt.CATEGORY='" + Cate + "'" + "  ORDER BY match_id  ";
      this.logger.info(query);
      compareSetupBeans = (ArrayList<CompareSetupBean>)getJdbcTemplate().query(query, 
          (RowMapper)new BeanPropertyRowMapper(CompareSetupBean.class));
      this.logger.info("***** CompareConfigDaoImpl.getmatchcrtlist End ****");
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.getmatchcrtlist");
      this.logger.error(" error in CompareConfigDaoImpl.getmatchcrtlist", 
          new Exception("CompareConfigDaoImpl.getmatchcrtlist", ex));
      throw ex;
    } 
    return compareSetupBeans;
  }
  
  public ArrayList<CompareSetupBean> getmatchcondnlist(int rec_set_id, String Cate) throws Exception {
    ArrayList<CompareSetupBean> compareSetupBeans = null;
    this.logger.info("***** CompareConfigDaoImpl.getmatchcondnlist Start ****");
    try {
      String query = " SELECT category,table_header,padding,start_charpos,condition,charsize, (SELECT filename FROM main_filesource WHERE fileid =mmatchcon.TABLE_FILE_ID)filename,pattern,entry_by,to_char(entry_date,'dd/MON/yyyy')entry_date   FROM main_matching_condition mmatchcon   WHERE mmatchcon.REC_SET_ID=" + 
        
        rec_set_id + 
        " and mmatchcon.CATEGORY='" + Cate + "'";
      this.logger.info(query);
      compareSetupBeans = (ArrayList<CompareSetupBean>)getJdbcTemplate().query(query, 
          (RowMapper)new BeanPropertyRowMapper(CompareSetupBean.class));
      this.logger.info("***** CompareConfigDaoImpl.getmatchcondnlist End ****");
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.getmatchcondnlist");
      this.logger.error(" error in CompareConfigDaoImpl.getmatchcondnlist", 
          new Exception("CompareConfigDaoImpl.getmatchcondnlist", ex));
      throw ex;
    } 
    return compareSetupBeans;
  }
  
  public ArrayList<CompareSetupBean> getrecparamlist(int rec_set_id, String Cate) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.getrecparamlist Start ****");
    ArrayList<CompareSetupBean> compareSetupBeans = null;
    try {
      String query = "SELECT category,table_header,padding,start_charpos,condition,charsize, (SELECT filename FROM main_filesource WHERE fileid =mrecpar.TABLE_FILE_ID)filename,pattern,entry_by,to_char(entry_date,'dd/MON/yyyy')entry_date  FROM Main_recon_param mrecpar WHERE mrecpar.REC_SET_ID=" + 
        
        rec_set_id + 
        " and  mrecpar.CATEGORY='" + Cate + "'";
      this.logger.info(query);
      compareSetupBeans = (ArrayList<CompareSetupBean>)getJdbcTemplate().query(query, 
          (RowMapper)new BeanPropertyRowMapper(CompareSetupBean.class));
      this.logger.info("***** CompareConfigDaoImpl.getrecparamlist End ****");
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.getrecparamlist");
      this.logger.error(" error in CompareConfigDaoImpl.getrecparamlist", 
          new Exception("CompareConfigDaoImpl.getrecparamlist", ex));
      throw ex;
    } 
    return compareSetupBeans;
  }
  
  public ArrayList<CompareSetupBean> getFileList() {
    this.logger.info("***** CompareConfigDaoImpl.getFileList Start ****");
    List<CompareSetupBean> filelist = null;
    try {
      String query = "SELECT distinct filesrc.FileName as stFileName FROM main_filesource filesrc WHERE  FILENAME not like '%NTSL%'";
      this.logger.info("query" + query);
      filelist = getJdbcTemplate().query(query, (RowMapper)new BeanPropertyRowMapper(CompareSetupBean.class));
      this.logger.info("***** CompareConfigDaoImpl.getFileList End ****");
    } catch (Exception ex) {
      this.logger.error(" error in CompareConfigDaoImpl.getFileList", 
          new Exception("CompareConfigDaoImpl.getFileList", ex));
      throw ex;
    } 
    return (ArrayList<CompareSetupBean>)filelist;
  }
  
  public ArrayList<Pos_Bean> getFileNameList(String filedate) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.getFileList Start ****");
    List<Pos_Bean> filelist = null;
    try {
      String filedateval = Utility.dateConveter_ddmonyyyy(filedate);
      String query = "select distinct os1.FILE_NAME from MASTERCARD_POS_RAWDATA os1 where os1.FILEDATE='" + 
        filedateval + "' ";
      this.logger.info("query" + query);
      filelist = getJdbcTemplate().query(query, (RowMapper)new BeanPropertyRowMapper(Pos_Bean.class));
      this.logger.info("***** CompareConfigDaoImpl.getFileList End ****");
    } catch (Exception ex) {
      this.logger.error(" error in CompareConfigDaoImpl.getFileList", 
          new Exception("CompareConfigDaoImpl.getFileList", ex));
      throw ex;
    } 
    return (ArrayList<Pos_Bean>)filelist;
  }
  
  public boolean chkFileupload(CompareSetupBean setupBean) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.chkFileupload Start **** "+ setupBean.getFilename());
    boolean result = false;
    try {
      if (setupBean.getFilename().equalsIgnoreCase("RUPAY")) {
        this.logger.info(" chkFileupload RUPAY****");
        if (setupBean.getP_FILE_NAME().startsWith("86") && setupBean.getFilename().equalsIgnoreCase("RUPAY") && 
          setupBean.getCategory().equalsIgnoreCase("RUPAY") && 
          setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")) {
          this.logger.info(" chkFileupload RUPAY****");
          int fileid = 0;
          String query = "Select COUNT(*) from rupay_rupay_86_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND FILEDATE=str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else if (setupBean.getP_FILE_NAME().startsWith("86") && 
          setupBean.getCategory().equalsIgnoreCase("QSPARC") && 
          setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")) {
          this.logger.info(" chkFileupload QSPARC****");
          int fileid = 0;
          String query = "Select COUNT(*) from  qsparc_86_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND FILEDATE=str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End SWITCH ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else if (setupBean.getP_FILE_NAME().startsWith("88") && 
          setupBean.getCategory().equalsIgnoreCase("QSPARC") && 
          setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")) {
          this.logger.info(" chkFileupload QSPARC****");
          int fileid = 0;
          String query = "Select COUNT(*) from qsparc_88_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND FILEDATE=str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End SWITCH ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else if (setupBean.getStSubCategoryid().equalsIgnoreCase("INTERNATIONAL") && 
          setupBean.getCategory().equalsIgnoreCase("QSPARC")) {
          int fileid = 0;
          if (setupBean.getP_FILE_NAME().startsWith("01") || setupBean.getP_FILE_NAME().startsWith("05") || 
            setupBean.getP_FILE_NAME().startsWith("03") || 
            setupBean.getP_FILE_NAME().startsWith("02")) {
            String query = "Select COUNT(*) from rupay_qsparc_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND FILEDATE= str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
            this.logger.info(query);
            fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          } else if (setupBean.getP_FILE_NAME().startsWith("88")) {
            String query = "Select COUNT(*) from  qsparc_int_88_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND FILEDATE= str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
            this.logger.info(query);
            fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          } else {
            String query = "Select COUNT(*) from  qsparc_int_86_rawdata  where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND FILEDATE= str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
            this.logger.info(query);
            fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          } 
          this.logger.info(" chkFileupload QSPARC_INT_86_RAWDATA****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else if (setupBean.getStSubCategoryid().equalsIgnoreCase("INTERNATIONAL") && 
          setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
          this.logger.info(" chkFileupload RUPAY_RUPAY_86_INT_RAWDATA****");
          int fileid = 0;
          if (setupBean.getP_FILE_NAME().startsWith("01") || setupBean.getP_FILE_NAME().startsWith("05") || 
            setupBean.getP_FILE_NAME().startsWith("03") || 
            setupBean.getP_FILE_NAME().startsWith("02")) {
            String query = "Select COUNT(*) from rupay_rupay_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND FILEDATE=str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
            this.logger.info(query);
            fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          } else {
            String query = "Select COUNT(*) from  rupay_rupay_86_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND FILEDATE=str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
            this.logger.info(query);
            fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          } 
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End SWITCH ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else if (setupBean.getP_FILE_NAME().startsWith("88") && 
          setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
          this.logger.info(" chkFileupload RUPAY_RUPAY_88_RAWDATA****");
          int fileid = 0;
          String query = "Select COUNT(*) from rupay_rupay_88_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND FILEDATE= str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End SWITCH ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else if (setupBean.getCategory().equalsIgnoreCase("QSPARC") && 
          setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")) {
          int fileid = 0;
          if (setupBean.getP_FILE_NAME().startsWith("01") || setupBean.getP_FILE_NAME().startsWith("05") || 
            setupBean.getP_FILE_NAME().startsWith("03")) {
            String query = "Select COUNT(*) from  rupay_qsparc_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND FILEDATE=str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
            this.logger.info(query);
            fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          } 
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else {
          this.logger.info(" chkFileupload RUPAY_RUPAY_RAWDATA****");
          int fileid = 0;
          String query = "Select COUNT(*) from  rupay_rupay_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND FILEDATE=str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End SWITCH ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } 
      } else if (setupBean.getFilename().equalsIgnoreCase("QSPARC")) {
        this.logger.info(" chkFileupload RUPAY****");
        if (setupBean.getP_FILE_NAME().startsWith("86") && setupBean.getFilename().equalsIgnoreCase("RUPAY") && 
          setupBean.getCategory().equalsIgnoreCase("RUPAY") && 
          setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")) {
          this.logger.info(" chkFileupload RUPAY****");
          int fileid = 0;
          String query = "Select COUNT(*) from rupay_rupay_86_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND FILEDATE= str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else if (setupBean.getP_FILE_NAME().startsWith("86") && 
          setupBean.getCategory().equalsIgnoreCase("QSPARC") && 
          setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")) {
          this.logger.info(" chkFileupload QSPARC****");
          int fileid = 0;
          String query = "Select COUNT(*) from  qsparc_86_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND FILEDATE=str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End SWITCH ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else if (setupBean.getStSubCategoryid().equalsIgnoreCase("INTERNATIONAL") && 
          setupBean.getCategory().equalsIgnoreCase("QSPARC")) {
          int fileid = 0;
          if (setupBean.getP_FILE_NAME().startsWith("01") || setupBean.getP_FILE_NAME().startsWith("05") || 
            setupBean.getP_FILE_NAME().startsWith("03") || 
            setupBean.getP_FILE_NAME().startsWith("02")) {
            String query = "Select COUNT(*) from rupay_qsparc_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND FILEDATE= str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
            this.logger.info(query);
            fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          } else if (setupBean.getP_FILE_NAME().startsWith("86")) {
            String query = "Select COUNT(*) from   qsparc_int_86_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND FILEDATE= str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
            this.logger.info(query);
            fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          } else if (setupBean.getP_FILE_NAME().startsWith("88")) {
            String query = "Select COUNT(*) from   qsparc_int_88_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND FILEDATE= str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
            this.logger.info(query);
            fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          } 
          this.logger.info(" chkFileupload QSPARC_INT_86_RAWDATA****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else if (setupBean.getStSubCategoryid().equalsIgnoreCase("INTERNATIONAL") && 
          setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
          this.logger.info(" chkFileupload RUPAY_RUPAY_86_INT_RAWDATA****");
          int fileid = 0;
          if (setupBean.getP_FILE_NAME().startsWith("01") || setupBean.getP_FILE_NAME().startsWith("05") || 
            setupBean.getP_FILE_NAME().startsWith("03") || 
            setupBean.getP_FILE_NAME().startsWith("02")) {
            String query = "Select COUNT(*) from  rupay_rupay_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND FILEDATE= str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
            this.logger.info(query);
            fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          } else {
            String query = "Select COUNT(*) from  rupay_rupay_86_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND FILEDATE= str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
            this.logger.info(query);
            fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          } 
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End SWITCH ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else if (setupBean.getP_FILE_NAME().startsWith("88") && 
          setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
          this.logger.info(" chkFileupload RUPAY_RUPAY_88_RAWDATA****");
          int fileid = 0;
          String query = "Select COUNT(*) from  rupay_rupay_88_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND FILEDATE=str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End SWITCH ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else if (setupBean.getCategory().equalsIgnoreCase("QSPARC") && 
          setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")) {
          int fileid = 0;
          if (setupBean.getP_FILE_NAME().startsWith("01") || setupBean.getP_FILE_NAME().startsWith("05") || 
            setupBean.getP_FILE_NAME().startsWith("03")) {
            String query = "Select COUNT(*) from  rupay_qsparc_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND FILEDATE= str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
            this.logger.info(query);
            fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          } 
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else {
          this.logger.info(" chkFileupload RUPAY_RUPAY_RAWDATA****");
          int fileid = 0;
          String query = "Select COUNT(*) from  rupay_rupay_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND FILEDATE=str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End SWITCH ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } 
      } else if (setupBean.getFilename().contains("SWITCH")) {
        this.logger.info(" chkFileupload SWITCH****");
        if (setupBean.getP_FILE_NAME().contains("P")) {
          this.logger.info(" chkFileupload SWITCH POS****");
          int fileid = 0;
          String query = "Select COUNT(*) from  switch_data_validation where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND filedate= STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')  ";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else if (setupBean.getP_FILE_NAME().contains("TX") || setupBean.getP_FILE_NAME().contains("c") || 
          setupBean.getP_FILE_NAME().contains("e")) {
          this.logger.info(" chkFileupload SWITCH ATM****");
          int fileid = 0;
          String query = "Select COUNT(*) from switch_data_validation where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')   ";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End SWITCH ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else {
          if (setupBean.getP_FILE_NAME().toUpperCase().contains(".TXT")) {
            this.logger.info(" chkFileupload SWITCH ATM****");
            int fileid = 0;
            String query = "Select COUNT(*) from switch_data_validation where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND str_to_date(filedate,'DD-MM-YY') = str_to_date('" + 
              setupBean.getFileDate() + "','DD-MM-YY') ";
            this.logger.info(query);
            this.logger.info("***** CompareConfigDaoImpl.chkFileupload End SWITCH ****");
            if (fileid == 0) {
              result = true;
            } else {
              result = false;
            } 
          } 
          result = true;
        } 
      } else if (setupBean.getFilename().contains("CBS")) {
        this.logger.info(" chkFileupload CBS****");
        int fileid = 0;
        String query = "Select COUNT(*) from switch_data_validation where FILENAME='" + 
          setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
        this.logger.info(query);
        this.logger.info("*****chkFileupload End CBS ****");
        if (fileid == 0) {
          result = true;
        } else {
          result = false;
        } 
      } else if (setupBean.getCategory().equalsIgnoreCase("VISA")) {
        this.logger.info(" chkFileupload CBS****");
        int fileid = 0;
        String query = " select count(*) from   visa_visa_rawdata where  filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
        fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
        fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
        this.logger.info("*****chkFileupload End CBS ****");
        if (fileid == 0) {
          result = true;
        } else {
          result = false;
        } 
      } else if (setupBean.getCategory().contains("VISA_ACQ")) {
        this.logger.info(" chkFileupload CBS****");
        int fileid = 0;
        String query = " select count(*) from  visa_acq_rawdata where  filedate= STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
        fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
        fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
        this.logger.info("*****chkFileupload End CBS ****");
        if (fileid == 0) {
          result = true;
        } else {
          result = false;
        } 
      } else if (setupBean.getFilename().contains("NFS")) {
        this.logger.info(" chkFileupload NFS****");
        if (setupBean.getP_FILE_NAME().contains("ISS")) {
          this.logger.info(" chkFileupload NFS ISS****");
          int fileid = 0;
          String query = "Select COUNT(*) from nfs_nfs_iss_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("*****chkFileupload End NFS ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else {
          this.logger.info(" chkFileupload NFS ACQ****");
          int fileid = 0;
          String query = "Select COUNT(*) from nfs_nfs_acq_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND  filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')  ";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("*****chkFileupload End NFS ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } 
      } else if (setupBean.getFilename().contains("ICD")) {
        this.logger.info(" chkFileupload NFS****");
        if (setupBean.getP_FILE_NAME().contains("ISS")) {
          this.logger.info(" chkFileupload NFS ISS****");
          int fileid = 0;
          String query = "Select COUNT(*) from  icd_icd_iss_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND filedate= STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("*****chkFileupload End NFS ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else {
          this.logger.info(" chkFileupload NFS ACQ****");
          int fileid = 0;
          String query = "Select COUNT(*) from  icd_icd_acq_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND  filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("*****chkFileupload End NFS ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } 
      } else if (setupBean.getFilename().contains("DFS")) {
        this.logger.info(" chkFileupload NFS****");
        if (setupBean.getP_FILE_NAME().contains("ISS")) {
          this.logger.info(" chkFileupload NFS ISS****");
          int fileid = 0;
          String query = "Select COUNT(*) from  dfs_dfs_iss_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND filedate= STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')  ";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("*****chkFileupload End NFS ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else {
          this.logger.info(" chkFileupload NFS ACQ****");
          int fileid = 0;
          String query = "Select COUNT(*) from   dfs_dfs_acq_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND  filedate=STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("*****chkFileupload End NFS ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } 
      } else if (setupBean.getFilename().contains("JCB")) {
        this.logger.info(" chkFileupload NFS****");
        if (setupBean.getP_FILE_NAME().contains("ISS")) {
          this.logger.info(" chkFileupload NFS ISS****");
          int fileid = 0;
          String query = "Select COUNT(*) from  jcb_jcb_iss_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND filedate =STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("*****chkFileupload End NFS ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } else {
          this.logger.info(" chkFileupload NFS ACQ****");
          int fileid = 0;
          String query = "Select COUNT(*) from  jcb_jcb_acq_rawdata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND  filedate =STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
          this.logger.info(query);
          fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
          this.logger.info("*****chkFileupload End NFS ****");
          if (fileid == 0) {
            result = true;
          } else {
            result = false;
          } 
        } 
      } else if (setupBean.getFilename().contains("REV_REPORT")) {
        this.logger.info(" chkFileupload REV_REPORT****");
        int fileid = 0;
        String query = "Select COUNT(*) from nfs_rev_acq_report where FILENAME='" + setupBean.getP_FILE_NAME() + 
          "' AND filedate =STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
        this.logger.info(query);
        fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
        this.logger.info("*****chkFileupload End REV_REPORT ****");
        if (fileid == 0) {
          result = true;
        } else {
          result = false;
        } 
      } else if (setupBean.getFilename().contains("MC-TA")) {
        this.logger.info(" chkFileupload REV_REPORT****");
        int fileid = 0;
        String query = "Select COUNT(*) from  mastercard_ta_rawdata where FILENAME='" + 
          setupBean.getP_FILE_NAME() + "' AND filedate= STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
        this.logger.info(query);
        fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
        this.logger.info("*****chkFileupload End REV_REPORT ****");
        if (fileid == 0) {
          result = true;
        } else {
          result = false;
        } 
      } else if (setupBean.getFilename().contains("POS") && setupBean.getCategory().contains("MASTERCARD")) {
        this.logger.info(" chkFileupload REV_REPORT****");
        int fileid = 0;
        String query = "Select COUNT(*) from mastercard_pos_rawdata where FILE_NAME='" + 
          setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
        this.logger.info(query);
        fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
        this.logger.info("*****chkFileupload End REV_REPORT ****");
        if (fileid == 0) {
          result = true;
        } else {
          result = false;
        } 
      } else if (setupBean.getFilename().contains("ATM") && setupBean.getCategory().contains("MASTERCARD")) {
        this.logger.info(" chkFileupload REV_REPORT****");
        int fileid = 0;
        String query = "Select COUNT(*) from   mastercard_atm_rawdata where FILENAME='" + 
          setupBean.getP_FILE_NAME() + "' AND filedate =STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
        this.logger.info(query);
        fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
        this.logger.info("*****chkFileupload End REV_REPORT ****");
        if (fileid == 0) {
          result = true;
        } else {
          result = false;
        } 
      } 
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.chkFileupload");
      this.logger.error(" error in CompareConfigDaoImpl.chkFileupload", 
          new Exception("CompareConfigDaoImpl.chkFileupload", ex));
      return false;
    } 
    return result;
  }
  
  public boolean chkFileupload1(CompareSetupBean setupBean) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.chkFileupload Start ATM SWITCH****");
    boolean result = false;
    try {
      int fileid = 0;
      String query = "Select COUNT(*) from UBI_atm_SWITCH_RAWDATA where FILENAME='" + setupBean.getP_FILE_NAME() + 
        "' AND FILEDATE = str_to_date(str_to_date('" + setupBean.getFileDate() + "' ,'DD-MM-YY')) ";
      this.logger.info(query);
      fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
      this.logger.info("***** CompareConfigDaoImpl.chkFileupload End ****");
      if (fileid == 0) {
        result = true;
      } else {
        result = false;
      } 
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.chkFileupload");
      this.logger.error(" error in CompareConfigDaoImpl.chkFileupload", 
          new Exception("CompareConfigDaoImpl.chkFileupload", ex));
      return false;
    } 
    return result;
  }
  
  public boolean chkFileupload2(CompareSetupBean setupBean) throws Exception {
    this.logger.info("*****chkFileupload Start CBS ****");
    boolean result = false;
    try {
      int fileid = 0;
      String query = "Select COUNT(*) from ubi_cbs_rawdata where FILENAME='" + setupBean.getP_FILE_NAME() + 
        "' AND FILEDATE = '" + setupBean.getFileDate() + "' ";
      this.logger.info(query);
      fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
      this.logger.info("*****chkFileupload End CBS ****");
      if (fileid == 0) {
        result = true;
      } else {
        result = false;
      } 
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.chkFileupload");
      this.logger.error(" error in CompareConfigDaoImpl.chkFileupload", 
          new Exception("CompareConfigDaoImpl.chkFileupload", ex));
      return false;
    } 
    return result;
  }
  
  public HashMap<String, Object> uploadFile(CompareSetupBean setupBean, MultipartFile file) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.uploadFile Start  file name****" + setupBean.getFilename());
    boolean result = false;
    String tablename = "";
    HashMap<String, Object> output = new HashMap<>();
    boolean outputFlag = false;
    String fileName = "";
    FileSourceBean sourceBean = null;
    try {
      if (setupBean.getFilename().equalsIgnoreCase("SWITCH")) {
        this.logger.info("Switch ak ");
        ReadUCOSwitchFile switchFile = new ReadUCOSwitchFile();
        String[] fileNames = file.getOriginalFilename().split("\\_");
        if (file.getOriginalFilename().contains("P")) {
          fileName = "POS";
          output = switchFile.uploadPOSSwitchData(setupBean, getConnection(), file, sourceBean);
          result = ((Boolean)output.get("result")).booleanValue();
        } else if (file.getOriginalFilename().startsWith("TX")) {
          fileName = "ATM";
          output = switchFile.uploadATMSwitchData(setupBean, getConnection(), file, sourceBean);
          result = ((Boolean)output.get("result")).booleanValue();
        } else {
          ReadNUploadCBSFiles cbsFile = new ReadNUploadCBSFiles();
          if (file.getOriginalFilename().toUpperCase().contains(".TXT")) {
            output = cbsFile.uploadUbiSWITCHData(setupBean, getConnection(), file, sourceBean);
            result = ((Boolean)output.get("result")).booleanValue();
          } 
        } 
        this.logger.info("filedate " + setupBean.getFileDate() + "fileName  " + fileName);
      } else if (setupBean.getFilename().equalsIgnoreCase("CBS")) {
        fileName = file.getOriginalFilename();
        System.out.println("Reading of CBS Starts" + fileName);
        ReadNUploadCBSFiles cbsFile = new ReadNUploadCBSFiles();
        if (fileName.toUpperCase().contains(".TXT")) {
          output = cbsFile.uploadUbiCBSData(setupBean, getConnection(), file, sourceBean);
          result = ((Boolean)output.get("result")).booleanValue();
        } 
      } else if (setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
        fileName = file.getOriginalFilename();
        System.out.println("fileName.substring(0, 2) " + fileName.substring(0, 2));
        if (fileName.substring(0, 2).equalsIgnoreCase("88")) {
          ReadRupay88File readRupay = new ReadRupay88File();
          result = readRupay.readData(setupBean, getConnection(), file, sourceBean);
        } else {
          ReadRupay readRupay = new ReadRupay();
          result = readRupay.readData(setupBean, getConnection(), file, sourceBean);
        } 
      } else if (setupBean.getCategory().equalsIgnoreCase("QSPARC")) {
        fileName = file.getOriginalFilename();
        if (fileName.substring(0, 2).equalsIgnoreCase("86")) {
          ReadRupay readRupay = new ReadRupay();
          result = readRupay.readData(setupBean, getConnection(), file, sourceBean);
        } else if (fileName.substring(0, 2).equalsIgnoreCase("88")) {
          ReadRupay88File readRupay = new ReadRupay88File();
          result = readRupay.readData(setupBean, getConnection(), file, sourceBean);
        } else {
          ReadRupay readRupay = new ReadRupay();
          result = readRupay.readData(setupBean, getConnection(), file, sourceBean);
        } 
      } else if (setupBean.getFilename().equalsIgnoreCase("NFS") || 
        setupBean.getFilename().equalsIgnoreCase("ICD") || 
        setupBean.getFilename().equalsIgnoreCase("DFS") || 
        setupBean.getFilename().equalsIgnoreCase("JCB")) {
        ReadNfsRawData nfsRawData = new ReadNfsRawData();
        result = nfsRawData.readData(setupBean, getConnection(), file, sourceBean);
      } else if (setupBean.getFilename().equalsIgnoreCase("CASHNET")) {
        ReadCashNetFile readcashnet = new ReadCashNetFile();
        result = readcashnet.readData(setupBean, getConnection(), file, sourceBean);
      } else if (setupBean.getFilename().equalsIgnoreCase("VISA") || 
        setupBean.getFilename().equalsIgnoreCase("VISA_ACQ")) {
        ReadVisaFile readRupay = new ReadVisaFile();
        result = readRupay.readData(setupBean, getConnection(), file, sourceBean);
      } 
      if (setupBean.getCategory().equalsIgnoreCase("POS") && 
        setupBean.getFilename().equalsIgnoreCase("POS")) {
        ReadNUploadOnusPosFile readonus_pos = new ReadNUploadOnusPosFile();
        result = ReadNUploadOnusPosFile.read_method_onp(setupBean, getConnection(), file);
      } 
      if (setupBean.getCategory().equalsIgnoreCase("MASTERCARD")) {
        System.out.println("filename " + setupBean.getFilename());
        if (setupBean.getFilename().equalsIgnoreCase("POS")) {
          Pos_Reading readmas_pos = new Pos_Reading();
          result = Pos_Reading.read_method(setupBean, getConnection(), file);
        } else if (setupBean.getFilename().equalsIgnoreCase("ATM")) {
          ReadMastercard461Rawdata readmas_atm = new ReadMastercard461Rawdata();
          output = readmas_atm.read464File(file, getConnection(), setupBean);
          result = ((Boolean)output.get("result")).booleanValue();
        } 
      } else if (setupBean.getFilename().equalsIgnoreCase("BANKREPO")) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        String original_date = setupBean.getFileDate();
        try {
          Date varDate = dateFormat.parse(setupBean.getFileDate());
          dateFormat = new SimpleDateFormat("mm/dd/yyyy");
          System.out.println("Date :" + dateFormat.format(varDate));
          setupBean.setFileDate(dateFormat.format(varDate));
        } catch (Exception e) {
          e.printStackTrace();
        } 
        ReadCardToCardCBS bankway = new ReadCardToCardCBS();
        result = bankway.uploadDatacardtocardbank(setupBean, getConnection(), file, sourceBean);
        setupBean.setFileDate(original_date);
      } 
      if (result)
        if (fileName.equalsIgnoreCase("POS")) {
          System.out.println("date " + setupBean.getFileDate());
          outputFlag = updatefile(setupBean);
        } else if (fileName.equalsIgnoreCase("ATM")) {
          outputFlag = updatefile(setupBean);
        } else {
          System.out.println("filename3 " + setupBean.getFilename() + "cat " + setupBean.getCategory() + 
              " sub cat " + setupBean.getStSubCategory());
          outputFlag = updatefile(setupBean);
        }  
      this.logger.info("***** CompareConfigDaoImpl.uploadFile End ****");
      if (result) {
        output.put("result", Boolean.valueOf(true));
      } else {
        output.put("result", Boolean.valueOf(false));
      } 
      return output;
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.uploadFile");
      this.logger.error(" error in CompareConfigDaoImpl.uploadFile", 
          new Exception("CompareConfigDaoImpl.uploadFile", ex));
      ex.printStackTrace();
      output.put("result", Boolean.valueOf(false));
      return output;
    } 
  }
  
  public boolean updatefile(CompareSetupBean setupBean) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.updatefile Start ****");
    this.logger.info("File name is " + setupBean.getFilename() + " sub category is " + setupBean.getStSubCategory());
    int count = 0;
    String subcatquery = null;
    if (setupBean.getCategory().equals("WCC") && setupBean.getFilename().equals("VISA")) {
      if (setupBean.getStSubCategory().equals("-") && setupBean.getFilename().equals("WCC-VISA")) {
        subcatquery = "Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where filename LIKE '%" + 
          setupBean.getFilename() + "%' and FILE_SUBCATEGORY = '-' ";
      } else if (setupBean.getFilename().equals("VISA") && setupBean.getStSubCategory().equals("ISSUER")) {
        subcatquery = "Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where filename LIKE '%" + 
          setupBean.getFilename() + "%' and  FILE_SUBCATEGORY!='-'";
        subcatquery = "Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where filename LIKE '%" + 
          setupBean.getFilename() + "%' and FILE_SUBCATEGORY = '-' ";
      } else {
        subcatquery = "Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where filename LIKE '%" + 
          setupBean.getFilename() + "%' ";
      } 
    } else if (setupBean.getCategory().equalsIgnoreCase("QSPARC")) {
      subcatquery = "Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where filename ='QSPARC'";
    } else if (setupBean.getCategory().equalsIgnoreCase("MASTERCARD")) {
      subcatquery = "Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where";
    } else {
      subcatquery = "Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where filename ='" + 
        setupBean.getFilename() + "' ";
    } 
    if (setupBean.getFilename().equals("SWITCH") || setupBean.getFilename().equals("VISA")) {
      if (setupBean.getFilename().equals("VISA")) {
        if (setupBean.getCategory().equalsIgnoreCase("VISA")) {
          subcatquery = String.valueOf(subcatquery) + " and FILE_CATEGORY = 'VISA'";
        } else {
          subcatquery = String.valueOf(subcatquery) + " and FILE_CATEGORY = 'VISA_ACQ'";
        } 
      } else {
        subcatquery = "Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where filename = '" + 
          setupBean.getFilename() + "' ";
      } 
    } else if (setupBean.getFilename().equals("CBS")) {
      subcatquery = String.valueOf(subcatquery) + " and tablename = 'CBS_RUPAY_RAWDATA' ";
    } else if (setupBean.getStSubCategory().equalsIgnoreCase("CARDTOCARD")) {
      subcatquery = String.valueOf(subcatquery) + " AND FILE_CATEGORY = '" + setupBean.getStSubCategory() + "' ";
    } else if (setupBean.getCategory().equalsIgnoreCase("NFS")) {
      subcatquery = String.valueOf(subcatquery) + " AND FILE_SUBCATEGORY = '" + setupBean.getStSubCategory() + "' ";
    } else if (setupBean.getCategory().equalsIgnoreCase("MASTERCARD")) {
    
      
      subcatquery = String.valueOf(subcatquery) + " FILE_CATEGORY = '" + setupBean.getCategory() + "'  AND FILE_SUBCATEGORY ='"+ setupBean.getStSubCategory() +"'";
      
      
    } else if (setupBean.getCategory().equalsIgnoreCase("ICD")) {
      subcatquery = String.valueOf(subcatquery) + " AND FILE_SUBCATEGORY = '" + setupBean.getStSubCategory() + "' ";
    } else if (setupBean.getCategory().equalsIgnoreCase("DFS")) {
      subcatquery = String.valueOf(subcatquery) + " AND FILE_SUBCATEGORY = '" + setupBean.getStSubCategory() + "' ";
    } else if (setupBean.getCategory().equalsIgnoreCase("JCB")) {
      subcatquery = String.valueOf(subcatquery) + " AND FILE_SUBCATEGORY = '" + setupBean.getStSubCategory() + "' ";
    } else {
      subcatquery = String.valueOf(subcatquery) + " AND FILE_SUBCATEGORY = '" + setupBean.getStSubCategoryid() + "' ";
    } 
    this.logger.info("subcatquery==" + subcatquery);
    ArrayList<CompareSetupBean> fileids = new ArrayList<>();
    fileids = (ArrayList<CompareSetupBean>)getJdbcTemplate().query(subcatquery, 
        (RowMapper)new BeanPropertyRowMapper(CompareSetupBean.class));
    this.logger.info("fileids are " + fileids.toString());
    for (CompareSetupBean bean : fileids) {
      String query = "";
      if (setupBean.getFileType().equalsIgnoreCase("MANUAL")) {
        if (chkUploadFlag("UPLOAD_FLAG", setupBean).equalsIgnoreCase("N")) {
          query = " insert into main_file_upload_dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) values (" + 
            bean.getInFileId() + ",STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ,'" + setupBean.getCreatedBy() + "',sysdate,'" + bean.getCategory() + "','" + 
            bean.getStSubCategory() + "'" + ",'N','N','N','N','N','Y')";
        } else {
          query = "Update main_file_upload_dtls set MANUPLOAD_FLAG ='Y'  WHERE  FILEDATE = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')   AND CATEGORY = '" + bean.getCategory() + 
            "' AND FileId = " + bean.getInFileId() + " ";
        } 
      } else {
        bean.setFilename(setupBean.getFilename());
        bean.setFileDate(setupBean.getFileDate());
        if (chkUploadFlag("MANUPLOAD_FLAG", bean).equalsIgnoreCase("N")) {
          if (setupBean.getCategory().equalsIgnoreCase("CARDTOCARD")) {
            if (setupBean.getFilename().equalsIgnoreCase("BANKREPO")) {
              query = " insert into main_file_upload_dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) values (" + 
                bean.getInFileId() + ",STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ,'" + setupBean.getCreatedBy() + "',sysdate,'" + bean.getCategory() + 
                "','" + bean.getStSubCategory() + "'" + ",'Y','N','N','N','N','Y')";
            } else {
              query = " insert into main_file_upload_dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) values (" + 
                bean.getInFileId() + ",STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ,'" + setupBean.getCreatedBy() + "',sysdate,'" + bean.getCategory() + 
                "','" + bean.getStSubCategory() + "'" + ",'Y','N','N','N','N','Y')";
            } 
          } else if (setupBean.getStSubCategory().equals("CARDTOCARD")) {
            if (setupBean.getFilename().equalsIgnoreCase("BANKREPO")) {
              query = " insert into main_file_upload_dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) values (" + 
                bean.getInFileId() + ",STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ,'" + setupBean.getCreatedBy() + "',sysdate,'" + bean.getCategory() + 
                "','" + bean.getStSubCategory() + "'" + ",'Y','N','N','N','N','Y')";
            } else {
              SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yy");
              try {
                Date varDate = dateFormat.parse(setupBean.getFileDate());
                dateFormat = new SimpleDateFormat("dd-mm-yy");
                System.out.println("Date :" + dateFormat.format(varDate));
                setupBean.setFileDate(dateFormat.format(varDate));
                query = " insert into main_file_upload_dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) values (" + 
                  bean.getInFileId() + ",STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ),'" + setupBean.getCreatedBy() + 
                  "',sysdate,'" + bean.getCategory() + "','" + bean.getStSubCategory() + "'" + 
                  ",'Y','N','N','N','N','Y')";
              } catch (Exception e) {
                e.printStackTrace();
              } 
            } 
          } else {
            if (setupBean.getFilename().equalsIgnoreCase("VISA") && 
              setupBean.getCategory().equalsIgnoreCase("WCC")) {
              if (getFileCount(setupBean) > 0) {
                updateFlag1(setupBean);
              } else {
                updateFlag2(setupBean);
              } 
              return true;
            } 
            query = " insert into main_file_upload_dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) values (" + 
              bean.getInFileId() + ",STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ,'" + setupBean.getCreatedBy() + "',sysdate(),'" + bean.getCategory() + 
              "','" + bean.getStSubCategory() + "'" + ",'Y','N','N','N','N','N')";
          } 
          this.logger.info("query==" + query);
          String getupld_count = "Select case when FILE_COUNT is null then 0 else file_count end as file_count from main_file_upload_dtls where CATEGORY ='" + 
            bean.getCategory() + "'  and FILE_SUBCATEGORY = '" + bean.getStSubCategory() + 
            "' and FILEID =" + bean.getInFileId() + " ";
          if (setupBean.getFilename().equalsIgnoreCase("BANKREPO")) {
            getupld_count = String.valueOf(getupld_count) + " and str_to_date(FILEDATE,'DD-MM-YY') = str_to_date('" + 
              setupBean.getFileDate() + "','DD-MM-YY')  ";
          } else if (setupBean.getStSubCategory().equals("CARDTOCARD")) {
            getupld_count = String.valueOf(getupld_count) + " and FILEDATE = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')  ";
          } else {
            getupld_count = String.valueOf(getupld_count) + " and  FILEDATE = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')   ";
          } 
          getupld_count = " SELECT CASE WHEN exists (" + getupld_count + ") then (" + getupld_count + 
            ") else 0 end as FLAG from dual";
          this.logger.info("getupld_count==" + getupld_count);
          this.uploadcount = (Integer)getJdbcTemplate().queryForObject(getupld_count, Integer.class);
          this.logger.info("uploadcount==" + this.uploadcount);
          this.uploadcount = Integer.valueOf(this.uploadcount.intValue() + 1);
          if (setupBean.getFilename().equalsIgnoreCase("NFS") || 
            setupBean.getFilename().equalsIgnoreCase("RUPAY") || 
            setupBean.getFilename().equalsIgnoreCase("QSPARC") || 
            setupBean.getFilename().equalsIgnoreCase("VISA") || 
            setupBean.getFilename().equalsIgnoreCase("VISA_ACQ") || 
            setupBean.getFilename().equalsIgnoreCase("SWITCH") || 
            setupBean.getFilename().equalsIgnoreCase("ATM") || 
            setupBean.getFilename().equalsIgnoreCase("POS") || 
            setupBean.getFilename().equalsIgnoreCase("ICD") || 
            setupBean.getFilename().equalsIgnoreCase("DFS") || 
            setupBean.getFilename().equalsIgnoreCase("JCB") || 
            setupBean.getFilename().equalsIgnoreCase("CBS"))
            if (this.uploadcount.intValue() == 1) {
              query = " insert into main_file_upload_dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG,FILE_COUNT) values (" + 
                bean.getInFileId() + ",STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ,'" + setupBean.getCreatedBy() + "',sysdate(),'" + 
                bean.getCategory() + "','" + bean.getStSubCategory() + "'" + 
                ",'Y','N','N','N','N','N'," + this.uploadcount + ")";
            } else {
              query = "Update main_file_upload_dtls set FILE_COUNT ='" + this.uploadcount + 
                "'  WHERE FILEDATE = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') AND CATEGORY = '" + bean.getCategory() + 
                "' AND FileId = " + bean.getInFileId() + " ";
            }  
        } else {
          query = "Update main_file_upload_dtls set UPLOAD_FLAG ='Y'  WHERE FILEDATE = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')  AND CATEGORY = '" + bean.getCategory() + 
            "' AND FileId = " + bean.getInFileId() + " ";
        } 
      } 
      this.logger.info("query==" + query);
      try {
        getJdbcTemplate().update(query);
        count++;
        this.logger.info("***** CompareConfigDaoImpl.updatefile End ****");
      } catch (Exception ex) {
        demo.logSQLException(ex, "CompareConfigDaoImpl.updatefile");
        this.logger.error(" error in CompareConfigDaoImpl.updatefile", 
            new Exception("CompareConfigDaoImpl.updatefile", ex));
        return false;
      } 
    } 
    if (count > 0)
      return true; 
    return false;
  }
  
  public String chkFlag(String flag, CompareSetupBean setupBean) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.chkFlag Start ****");
    String flg = "";
    try {
      String query = "SELECT " + flag + " FROM MAIN_FILE_UPLOAD_DTLS WHERE filedate = '" + setupBean.getFileDate() + 
        "'  " + " AND CATEGORY = '" + setupBean.getCategory() + "' AND FileId = " + 
        setupBean.getInFileId() + "  ";
      if (setupBean.getStSubCategory() != null && !setupBean.getStSubCategory().equals(""))
        query = String.valueOf(query) + " AND FILE_SUBCATEGORY = '" + setupBean.getStSubCategory() + "' "; 
      query = " SELECT CASE WHEN exists (" + query + ") then (" + query + ") else 'N' end as FLAG from dual";
      this.logger.info("query == " + query);
      flg = (String)getJdbcTemplate().queryForObject(query, String.class);
      this.logger.info("***** CompareConfigDaoImpl.chkFlag End ****");
    } catch (Exception e) {
      demo.logSQLException(e, "CompareConfigDaoImpl.chkFlag");
      this.logger.error(" error in CompareConfigDaoImpl.chkFlag ", new Exception("CompareConfigDaoImpl.chkFlag ", e));
      throw e;
    } 
    return flg;
  }
  
  public boolean updateFlag(String flag, CompareSetupBean setupBean) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.updateFlag Start ****");
    try {
      String query = "Update MAIN_FILE_UPLOAD_DTLS set " + flag + " ='Y'  WHERE filedate = '" + 
        setupBean.getFileDate() + "' " + " AND FileId = " + setupBean.getInFileId();
      this.logger.info("query==" + query);
      int rowupdate = getJdbcTemplate().update(query);
      this.logger.info("rowupdate==" + rowupdate);
      this.logger.info("***** CompareConfigDaoImpl.updateFlag End ****");
      if (rowupdate > 0)
        return true; 
      return false;
    } catch (Exception e) {
      demo.logSQLException(e, "CompareConfigDaoImpl.updateFlag");
      this.logger.error(" error in CompareConfigDaoImpl.updateFlag", 
          new Exception("CompareConfigDaoImpl.updateFlag", e));
      throw e;
    } 
  }
  
  public boolean validateFile(CompareSetupBean setupBean, MultipartFile file) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.validateFile Start ****");
    try {
      File readFile = setupBean.getDataFile();
      FileInputStream fis = new FileInputStream("\\\\10.144.143.191\\led\\DCRS\\test" + File.separator + file + ".txt");
      int count = 0;
      BufferedReader br = new BufferedReader(new InputStreamReader(fis));
      String thisline;
      while ((thisline = br.readLine()) != null) {
        if (count == 0 && setupBean.getStFileName().equalsIgnoreCase("SWITCH")) {
          String[] splitarray = null;
          splitarray = thisline.split(Pattern.quote("|"));
          String seperator = (String)getJdbcTemplate().queryForObject(
              "SELECT Dataseparator FROM main_filesource WHERE fileid=" + setupBean.getInFileId() + " ", 
              String.class);
          this.logger.info("seperator==" + seperator);
          return chkfiledetails(splitarray, seperator, setupBean);
        } 
        if (count == 1 && setupBean.getStFileName().equalsIgnoreCase("CBS")) {
          String[] splitarray = null;
          splitarray = thisline.split(Pattern.quote("|"));
          String seperator = (String)getJdbcTemplate().queryForObject(
              "SELECT Dataseparator FROM main_filesource WHERE fileid=" + setupBean.getInFileId() + " ", 
              String.class);
          this.logger.info("seperator==" + seperator);
          return chkfiledetails(splitarray, seperator, setupBean);
        } 
        if (count == 1 && setupBean.getStFileName().equals("MANUALCBS")) {
          String[] splitarray = null;
          splitarray = thisline.split(Pattern.quote("|"));
          String seperator = (String)getJdbcTemplate().queryForObject(
              "SELECT Dataseparator FROM main_filesource WHERE fileid=" + setupBean.getInFileId() + " ", 
              String.class);
          this.logger.info("seperator==" + seperator);
          return chkfiledetails(splitarray, seperator, setupBean);
        } 
        count++;
      } 
      this.logger.info("***** CompareConfigDaoImpl.validateFile End ****");
    } catch (Exception ex) {
      this.logger.error(" error in CompareConfigDaoImpl.validateFile", 
          new Exception("CompareConfigDaoImpl.validateFile", ex));
      throw ex;
    } 
    return false;
  }
  
  public boolean chkfiledetails(String[] splitArray, String seperator, CompareSetupBean setupBean) {
    this.logger.info("***** CompareConfigDaoImpl.chkfiledetails Start ****");
    int acceptorName = 0;
    boolean result = false;
    if (setupBean.getStFileName().equalsIgnoreCase("SWITCH"))
      if (splitArray[0].trim().length() == 3 || splitArray[1].trim().length() == 16) {
        String sql = "SELECT count(*) FROM  SWITCH_RAWDATA  WHERE amount ='" + splitArray[7] + 
          "' AND respcode = '" + splitArray[9] + "' " + " AND pan ='" + splitArray[1] + 
          "'  AND trace ='" + splitArray[6] + "' " + " AND authnum ='" + splitArray[21] + "'";
        this.logger.info("sql==" + sql);
        acceptorName = ((Integer)getJdbcTemplate().queryForObject(sql, Integer.class)).intValue();
        this.logger.info("acceptorName==" + acceptorName);
        if (acceptorName == 0) {
          result = true;
        } else {
          result = false;
        } 
      } else {
        return false;
      }  
    if (setupBean.getStFileName().equalsIgnoreCase("CBS")) {
      int len = 0;
      if (setupBean.getFileType().equalsIgnoreCase("MANUAL")) {
        len = splitArray[3].length();
      } else {
        len = splitArray[3].trim().length();
      } 
      if (len == 9) {
        String sql = "SELECT count(*) FROM  CBS_RAWDATA  WHERE TRAN_ID ='" + splitArray[3] + "'";
        this.logger.info(sql);
        acceptorName = ((Integer)getJdbcTemplate().queryForObject(sql, Integer.class)).intValue();
        this.logger.info("acceptorName==" + acceptorName);
        if (acceptorName == 0) {
          result = true;
        } else {
          result = false;
        } 
      } else {
        return false;
      } 
    } 
    if (setupBean.getStFileName().equals("MANUALCBS"))
      if (splitArray[3].length() == 9) {
        String sql = "SELECT count(*) FROM  cbs_Data  WHERE TRAN_ID ='" + splitArray[3] + "'";
        this.logger.info(sql);
        acceptorName = ((Integer)getJdbcTemplate().queryForObject(sql, Integer.class)).intValue();
        this.logger.info("acceptorName==" + acceptorName);
        if (acceptorName == 0) {
          result = true;
        } else {
          result = false;
        } 
      } else {
        return false;
      }  
    this.logger.info("***** CompareConfigDaoImpl.chkfiledetails End ****");
    return result;
  }
  
  public List<CompareSetupBean> getlastUploadDetails() throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.getlastUploadDetails Start ****");
    ArrayList<CompareSetupBean> setupBeans = null;
    try {
      String query = "SELECT filesrc.fileName as stFileName,to_char(filedate,'dd/mm/yy') as filedate,filter_Flag ,\tknockoff_Flag , comapre_Flag , manualcompare_Flag , upload_Flag ,manupload_flag, updlodby as createdBy,category,filesrc.file_subcategory as stSubCategory,to_char(uploaddate,'dd/mm/yy') as entry_date \tFROM MAIN_FILE_UPLOAD_DTLS  uplddtls  INNER JOIN  MAIN_FILESOURCE filesrc ON  filesrc.fileId = uplddtls.fileid ORDER BY str_to_date(FILEDATE,'DD/MM/YYYY')";
      this.logger.info(query);
      setupBeans = (ArrayList<CompareSetupBean>)getJdbcTemplate().query(query, 
          (RowMapper)new BeanPropertyRowMapper(CompareSetupBean.class));
      this.logger.info("***** CompareConfigDaoImpl.getlastUploadDetails End ****");
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.getlastUploadDetails");
      this.logger.error(" error in CompareConfigDaoImpl.getlastUploadDetails ", 
          new Exception("CompareConfigDaoImpl.getlastUploadDetails ", ex));
      throw ex;
    } 
    return setupBeans;
  }
  
  public boolean CheckAlreadyProcessed(CompareSetupBean setupBean) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.CheckAlreadyProcessed Start ****");
    int file1 = 0, file2 = 0;
    try {
      String query = "SELECT count(*) FROM main_File_upload_dtls where MANUALCOMPARE_FLAG = 'Y' AND UPLOAD_FLAG = 'Y'  AND Fileid=" + 
        setupBean.getCompareFile1() + " AND to_char(filedate,'dd-mm-yy') = '" + setupBean.getFileDate() + 
        "' ";
      this.logger.info(query);
      file1 = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
      query = "SELECT count(*) FROM main_File_upload_dtls where MANUALCOMPARE_FLAG = 'Y' AND MANUPLOAD_FLAG= 'Y'  AND Fileid=" + 
        setupBean.getCompareFile2() + " AND to_char(filedate,'dd-mm-yy') = '" + setupBean.getFileDate() + 
        "' ";
      this.logger.info(query);
      file2 = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
      this.logger.info("***** CompareConfigDaoImpl.CheckAlreadyProcessed End ****");
      if (file1 != 0 || file2 != 0)
        return true; 
      return false;
    } catch (Exception e) {
      demo.logSQLException(e, "CompareConfigDaoImpl.CheckAlreadyProcessed");
      this.logger.error(" error in CompareConfigDaoImpl.CheckAlreadyProcessed ", 
          new Exception("CompareConfigDaoImpl.CheckAlreadyProcessed ", e));
      return false;
    } 
  }
  
  public boolean chkCompareFiles(CompareSetupBean setupBean) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.chkCompareFiles Start ****");
    boolean result = false;
    try {
      int file1 = 0, file2 = 0;
      boolean alreadyProcessed = false;
      if (setupBean.getCompareLvl().equals("2")) {
        String query = "SELECT count(*) FROM main_File_upload_dtls where MANUALCOMPARE_FLAG = 'N' AND UPLOAD_FLAG = 'Y'  AND Fileid=" + 
          setupBean.getCompareFile1() + " AND to_char(filedate,'dd/mm/yyyy') = '" + 
          setupBean.getFileDate() + "' ";
        this.logger.info(query);
        file1 = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
        query = "SELECT count(*) FROM main_File_upload_dtls where MANUALCOMPARE_FLAG = 'N' AND MANUPLOAD_FLAG= 'Y'  AND Fileid=" + 
          setupBean.getCompareFile2() + " AND to_char(filedate,'dd/mm/yyyy') = '" + 
          setupBean.getFileDate() + "' ";
        this.logger.info(query);
        file2 = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
        this.logger.info("***** CompareConfigDaoImpl.chkCompareFiles End ****");
        if (file1 == 0 || file2 == 0) {
          result = false;
        } else {
          result = true;
        } 
      } 
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.chkCompareFiles");
      this.logger.error(" error in CompareConfigDaoImpl.chkCompareFiles ", 
          new Exception("CompareConfigDaoImpl.chkCompareFiles ", ex));
      throw ex;
    } 
    return result;
  }
  
  public String getTableName(int Fileid) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.getTableName Start ****");
    String tablename = "";
    try {
      String query = "Select tablename from main_filesource where fileid=" + Fileid;
      tablename = (String)getJdbcTemplate().queryForObject(query, String.class);
      this.logger.info("query" + query);
      this.logger.info("tablename" + tablename);
      this.logger.info("***** CompareConfigDaoImpl.getTableName End ****");
      return tablename;
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.getTableName");
      this.logger.error(" error in CompareConfigDaoImpl.getTableName ", 
          new Exception("CompareConfigDaoImpl.getTableName ", ex));
      return null;
    } 
  }
  
  public boolean validate_File(String Filedate, CompareSetupBean setupBean) throws Exception {
    this.logger.info("***** CompareConfigDoImpl.validate_File Start ****");
    PreparedStatement ps = null;
    int count = 0;
    try {
      String prevData = "select count(*) from SWITCH_data where TO_CHAR(createddate,'DD/MM/YYYY') = TO_CHAR(sysdate-1,'DD/MM/YYYY')";
      this.logger.info("prevData==" + prevData);
      String tablename = (String)getJdbcTemplate().queryForObject(
          "select tablename from MAIN_FILESOURCE where FILEID = " + setupBean.getInFileId(), String.class);
      this.logger.info("tablename==" + tablename);
      String chkData = "select count(*) from " + tablename + 
        " where TO_CHAR(CREATEDDATE,'DD/MM/YYYY') < TO_CHAR(sysdate,'DD/MM/YYYY')";
      this.logger.info(chkData);
      int dataCount = ((Integer)getJdbcTemplate().queryForObject(chkData, Integer.class)).intValue();
      this.logger.info("dataCount==" + dataCount);
      if (dataCount > 0) {
        String query = "SELECT count (*) FROM MAIN_FILE_UPLOAD_DTLS WHERE (TRUNC (filedate)) = (TRUNC (str_to_date ('" + 
          Filedate + "', 'dd/mm/yyyy') - 1) ) " + "\tAND Fileid =" + setupBean.getInFileId() + 
          " AND category='ONUS' " + " AND FILTER_FLAG='Y' AND KNOCKOFF_FLAG='Y' AND COMAPRE_FLAG='Y' " + 
          " AND UPLOAD_FLAG='Y'  ";
        count = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
        this.logger.info("query==" + query);
        this.logger.info("count==" + count);
        this.logger.info("***** CompareConfigDoImpl.validate_File End ****");
        if (count > 0)
          return true; 
        return false;
      } 
      return true;
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.validate_File");
      this.logger.error(" error in CompareConfigDoImpl.validate_File", 
          new Exception("CompareConfigDoImpl.validate_File", ex));
      return false;
    } 
  }
  
  public boolean saveManCompareDetails(ManualCompareBean manualCompareBean) throws Exception {
    this.logger.info("***** CompareConfigDaoImpl.saveManCompareDetails Start ****");
    setTransactionManager();
    DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus status = this.transactionManager.getTransaction((TransactionDefinition)defaultTransactionDefinition);
    List<ManualFileColumnDtls> columnDtls = new ArrayList<>();
    List<ManualCompareBean> comp_dtl_list = new ArrayList<>();
    columnDtls = manualCompareBean.getColumnDtls();
    comp_dtl_list = manualCompareBean.getComp_dtl_list();
    try {
      this.manmatchcount = ((Integer)getJdbcTemplate()
        .queryForObject("select (count (MAN_ID))+1 from MAIN_MANUAL_MASTER where category='" + 
          manualCompareBean.getCategory() + "'", Integer.class)).intValue();
      this.logger.info("manmatchcount==" + this.manmatchcount);
      insertMAIN_MANUAL_MASTER(comp_dtl_list, manualCompareBean);
      insertMAIN_MANUAL_CONDITION(columnDtls, manualCompareBean);
      this.logger.info("***** CompareConfigDaoImpl.saveManCompareDetails End ****");
      return true;
    } catch (Exception ex) {
      demo.logSQLException(ex, "CompareConfigDaoImpl.saveManCompareDetails");
      this.logger.error(" error in CompareConfigDoImpl.saveManCompareDetails", 
          new Exception("CompareConfigDoImpl.saveManCompareDetails", ex));
      return false;
    } 
  }
  
  private void insertMAIN_MANUAL_CONDITION(List<ManualFileColumnDtls> columnDtls, ManualCompareBean manualCompareBean) {
    this.logger.info("***** CompareConfigDaoImpl.insertMAIN_MANUAL_CONDITION Start ****");
    int compid = 1;
    for (ManualFileColumnDtls filedtls : columnDtls) {
      if (filedtls.getFileColumn1() != null) {
        if (!filedtls.getFileColumn1().equals("0")) {
          getJdbcTemplate().update(
              "Insert into MAIN_MANUAL_CONDITION (Man_Id,Category,Ref_File_Id,Comp_Id,File_Header,Padding,Start_Charposition,Charsize,Entry_By,Entry_Dt)values (" + 
              this.manmatchcount + ",'" + manualCompareBean.getCategory() + "'," + 
              manualCompareBean.getCategory() + "," + compid + ",'" + filedtls.getFileColumn1() + 
              "'," + "'" + filedtls.getStPadding() + "','" + filedtls.getInStart_Char_Position() + 
              "'," + filedtls.getInEnd_char_position() + ",'" + manualCompareBean.getEntryBy() + 
              "',sysdate)");
          this.logger.info(
              "Insert into MAIN_MANUAL_CONDITION (Man_Id,Category,Ref_File_Id,Comp_Id,File_Header,Padding,Start_Charposition,Charsize,Entry_By,Entry_Dt)values (" + 
              this.manmatchcount + ",'" + manualCompareBean.getCategory() + "'," + 
              manualCompareBean.getCategory() + "," + compid + ",'" + filedtls.getFileColumn1() + 
              "'," + "'" + filedtls.getStPadding() + "','" + filedtls.getInStart_Char_Position() + 
              "'," + filedtls.getInEnd_char_position() + ",'" + manualCompareBean.getEntryBy() + 
              "',sysdate)");
        } 
        if (!filedtls.getFileColumn2().equals("0"))
          getJdbcTemplate().update(
              "Insert into MAIN_MANUAL_CONDITION (Man_Id,Category,Ref_File_Id,Comp_Id,File_Header,Padding,Start_Charposition,Charsize,Entry_By,Entry_Dt)values (" + 
              this.manmatchcount + ",'" + manualCompareBean.getCategory() + "'," + 
              manualCompareBean.getMan_File() + "," + compid + ",'" + filedtls.getFileColumn2() + 
              "'," + "'" + filedtls.getStPadding() + "','" + 
              filedtls.getInStart_Char_Position2() + "'," + filedtls.getInEnd_char_position2() + 
              ",'" + manualCompareBean.getEntryBy() + "',sysdate)"); 
        compid++;
      } 
    } 
    this.logger.info("***** CompareConfigDaoImpl.insertMAIN_MANUAL_CONDITION End ****");
  }
  
  private void insertMAIN_MANUAL_MASTER(List<ManualCompareBean> comp_dtl_list, ManualCompareBean manualCompareBean) {
    this.logger.info("***** CompareConfigDaoImpl.insertMAIN_MANUAL_MASTER Start ****");
    for (ManualCompareBean filedtls : comp_dtl_list) {
      if (filedtls.getRefFileId() != null && 
        !filedtls.getRefFileId().equals("0"))
        getJdbcTemplate().update(
            "Insert into MAIN_MANUAL_MASTER (MAN_ID,CATEGORY,REF_FILE_ID,FILE_HEADER,SEARCH_PATTERN,PADDING,START_CHARPOSITION,CHARSIZE,CONDITION,ENTRY_BY,ENTRY_DT)values (" + 
            this.manmatchcount + ",'" + manualCompareBean.getCategory() + "'," + 
            filedtls.getRefFileId() + ",'" + filedtls.getRefFileHdr() + "','" + 
            filedtls.getStSearch_Pattern() + "'" + ",'" + filedtls.getStPadding() + "','" + 
            filedtls.getStChar_Pos() + "'," + filedtls.getStChar_Size() + ",'" + 
            filedtls.getCondition() + "','" + manualCompareBean.getEntryBy() + "',sysdate)"); 
    } 
    this.logger.info("***** CompareConfigDaoImpl.insertMAIN_MANUAL_MASTER End ****");
  }
  
  public List<CompareSetupBean> getFileList(String category) {
    List<CompareSetupBean> beans = null;
    String query = "Select fileid as inFileId, filename as stFileName,REC_SET_ID  FROM MAIN_FILESOURCE inner join MAIN_RECONSETUPDETAILS on REC_CATEGORY = FILE_CATEGORY where FILE_CATEGORY='" + 
      
      category + "'";
    this.logger.info(query);
    beans = getJdbcTemplate().query(query, 
        (RowMapper)new BeanPropertyRowMapper(CompareSetupBean.class));
    return beans;
  }
  
  public String chkUploadFlag(String flag, CompareSetupBean setupBean) {
    System.out.println("filename1 " + setupBean.getFilename() + "cat " + setupBean.getCategory() + " sub cat " + 
        setupBean.getStSubCategory());
    this.logger.info("***** CompareConfigDaoImpl.chkUploadFlag Start file name ****" + setupBean.getFilename());
    String flg = "";
    String query = "";
    try {
      if (setupBean.getFilename().equals("NFS") || setupBean.getFilename().equals("RUPAY") || (
        setupBean.getFilename().equals("VISA") && setupBean.getStSubCategory().equals("-")) || 
        setupBean.getFilename().equalsIgnoreCase("SWITCH") || 
        setupBean.getFilename().equalsIgnoreCase("CBS")) {
        if (setupBean.getCategory().equalsIgnoreCase("WCC") && setupBean.getFilename().equals("VISA")) {
          setupBean.setCategory("WCC");
          setupBean.setStSubCategory("-");
        } else if (setupBean.getFilename().equals("VISA") && 
          !setupBean.getCategory().equalsIgnoreCase("WCC")) {
          setupBean.setCategory("VISA");
          setupBean.setStSubCategory("ISSUER");
        } else if (setupBean.getFilename().equals("SWITCH") || setupBean.getFilename().equals("CBS")) {
          setupBean.getStSubCategory().equals("-");
        } 
        System.out.println("filename " + setupBean.getFilename() + "cat " + setupBean.getCategory() + 
            " sub cat " + setupBean.getStSubCategory());
        if (setupBean.getFilename().equalsIgnoreCase("NFS") && 
          setupBean.getCategory().equalsIgnoreCase("NFS"))
          setupBean.setCategory("NFS"); 
        String getupld_count = "";
        if (setupBean.getCategory().equalsIgnoreCase("QSPARC")) {
          getupld_count = "Select case when FILE_COUNT is null then 0 else file_count end as file_count from main_file_upload_dtls where CATEGORY ='QSPARC'  and FILE_SUBCATEGORY = '" + 
            setupBean.getStSubCategory() + "' " + 
            "and FileId in(Select fileid from main_filesource where filename='QSPARC' AND FILE_SUBCATEGORY = '" + 
            setupBean.getStSubCategory() + "') " + "and  filedate = str_to_date('" + 
            setupBean.getFileDate() + "','dd-mm-yy')  ";
        } else {
          getupld_count = "Select case when FILE_COUNT is null then 0 else file_count end as file_count from main_file_upload_dtls where CATEGORY ='" + 
            setupBean.getCategory() + "'  and FILE_SUBCATEGORY = '" + setupBean.getStSubCategory() + 
            "' " + "and FileId in(Select fileid from main_filesource where filename='" + 
            setupBean.getFilename() + "' AND FILE_SUBCATEGORY = '" + setupBean.getStSubCategory() + 
            "') " + "and  filedate = str_to_date('" + setupBean.getFileDate() + "','dd-mm-yy')  ";
        } 
        this.logger.info(getupld_count);
        getupld_count = " SELECT CASE WHEN exists (" + getupld_count + ") then (" + getupld_count + 
          ") else 0 end as FLAG from dual";
        this.logger.info(getupld_count);
        this.uploadcount = (Integer)getJdbcTemplate().queryForObject(getupld_count, Integer.class);
        this.logger.info("uploadcount==" + this.uploadcount);
        String getFile_count = "";
        System.out.println("filetyle =+ " + setupBean.getCategory());
        if (setupBean.getCategory().equalsIgnoreCase("QSPARC")) {
          getFile_count = "Select FILE_COUNT from main_filesource where FILE_CATEGORY ='QSPARC' and filename='QSPARC'  and FILE_SUBCATEGORY = '" + 
            setupBean.getStSubCategory() + "' ";
        } else {
          getFile_count = "Select FILE_COUNT from main_filesource where FILE_CATEGORY ='" + 
            setupBean.getCategory() + "' and filename='" + setupBean.getFilename() + 
            "'  and FILE_SUBCATEGORY = '" + setupBean.getStSubCategory() + "' ";
        } 
        if (setupBean.getFilename().equalsIgnoreCase("CBS"))
          getFile_count = "Select DISTINCT FILE_COUNT from main_filesource where FILE_CATEGORY ='NFS' and filename='CBS'"; 
        this.logger.info("getFile_count==" + getFile_count);
        this.filecount = (Integer)getJdbcTemplate().queryForObject(getFile_count, Integer.class);
        this.logger.info("filecount==" + this.filecount);
        if (this.filecount == this.uploadcount)
          return "Y"; 
        return "N";
      } 
      if (setupBean.getCategory().equalsIgnoreCase("MASTERCARD") && 
        setupBean.getFilename().equalsIgnoreCase("POS")) {
        query = "SELECT distinct 'N' as flag FROM main_file_upload_dtls WHERE str_to_date(filedate,'dd-mm-yy') = '" + 
          setupBean.getFileDate() + "'  " + 
          
          " AND FileId in(Select fileid from main_filesource where filename='" + setupBean.getFilename() + 
          "' ";
      } else {
        query = "SELECT distinct " + flag + 
          " FROM  main_file_upload_dtls WHERE str_to_date(filedate,'dd-mm-yy') = '" + 
          setupBean.getFileDate() + "'  " + 
          
          " AND FileId in(Select fileid from main_filesource where filename='" + setupBean.getFilename() + 
          "' ";
      } 
      if (!setupBean.getStSubCategory().equals("-")) {
        if (setupBean.getFilename().equalsIgnoreCase("CBS")) {
          query = String.valueOf(query) + " AND CATEGORY = '" + setupBean.getCategory() + "' AND FILE_SUBCATEGORY = '" + 
            setupBean.getStSubCategory() + "' )";
        } else {
          query = String.valueOf(query) + " AND FILE_SUBCATEGORY = '" + setupBean.getStSubCategory() + "' )";
        } 
      } else if (!setupBean.getCategory().isEmpty()) {
        query = String.valueOf(query) + " ) AND CATEGORY = '" + setupBean.getCategory() + "' ";
      } else {
        query = String.valueOf(query) + " )";
      } 
      query = " SELECT CASE WHEN exists (" + query + ") then (" + query + ") else 'N' end as FLAG from dual";
      this.logger.info("query==" + query);
      flg = (String)getJdbcTemplate().queryForObject(query, String.class);
      this.logger.info("flg==" + flg);
      this.logger.info("***** CompareConfigDaoImpl.chkUploadFlag End ****");
      return flg;
    } catch (Exception e) {
      this.logger.error(" error in CompareConfigDaoImpl.chkUploadFlag", 
          new Exception("CompareConfigDaoImpl.chkUploadFlag", e));
      e.printStackTrace();
      throw e;
    } 
  }
  
  public int getrecordcount(CompareSetupBean setupBean) {
    try {
      this.logger.info("***** CompareConfigDaoImpl.getrecordcount Start ****");
      String tablename = "";
      int count = 0;
      String query = "";
      if (setupBean.getFilename().contains("SWITCH")) {
        this.logger.info(" chkFileupload SWITCH****");
        if (setupBean.getP_FILE_NAME().contains("p")) {
          this.logger.info(" chkFileupload SWITCH POS****");
          String queryq = "Select COUNT(*) from   switch_pos_rawadata where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND date_format(filedate,'%d-%b-y%') = date_format('" + 
            setupBean.getFileDate() + "','%d-%b-y%') ";
          this.logger.info(queryq);
          count = ((Integer)getJdbcTemplate().queryForObject(queryq, Integer.class)).intValue();
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End ****");
        } else {
          this.logger.info(" chkFileupload SWITCH ATM****");
          String queryq = "Select COUNT(*) from SWITCH_ATM_RAWDATA where FILENAME='" + 
            setupBean.getP_FILE_NAME() + "' AND date_format(filedate,'%d-%b-y%') = date_format('" + 
            setupBean.getFileDate() + "','%d-%b-y%')";
          this.logger.info(queryq);
          count = ((Integer)getJdbcTemplate().queryForObject(queryq, Integer.class)).intValue();
          this.logger.info("***** CompareConfigDaoImpl.chkFileupload End SWITCH ****");
        } 
      } else if (setupBean.getStSubCategory().equalsIgnoreCase("CARDTOCARD")) {
        count = ((Integer)getJdbcTemplate().queryForObject("Select  count(*) from " + tablename + 
            " where str_to_date(filedate,'DD-MM-YY') = str_to_date('" + setupBean.getFileDate() + 
            "','DD-MM-YY')  ", Integer.class)).intValue();
      } else if (setupBean.getCategory().equalsIgnoreCase("MASTERCARD") && 
        setupBean.getFilename().equalsIgnoreCase("POS")) {
        String mquery = "Select  count(*) from  mastercard_pos_rawdata where filedate =STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') and FILE_NAME='" + setupBean.getP_FILE_NAME() + "'";
        this.logger.info(mquery);
        count = ((Integer)getJdbcTemplate().queryForObject(mquery, Integer.class)).intValue();
      } else if (setupBean.getCategory().equalsIgnoreCase("MASTERCARD") && 
        setupBean.getFilename().equalsIgnoreCase("DOM")) {
        String mquery = "Select  count(*) from  mastercard_atm_rawdata where filedate= STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') and FILENAME='" + setupBean.getP_FILE_NAME() + "'";
        this.logger.info(mquery);
        count = ((Integer)getJdbcTemplate().queryForObject(mquery, Integer.class)).intValue();
      } else if (setupBean.getFilename().equalsIgnoreCase("QSPARC")) {
        String Rquery = "";
        this.logger.info(" chkcount RUPAY****");
        if (setupBean.getP_FILE_NAME().startsWith("86") && 
          setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC") && 
          setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
          this.logger.info(" chkFileupload RUPAY RUPAY_RUPAY_86_RAWDATA****");
          Rquery = "Select COUNT(*) from  rupay_rupay_86_rawdata  where FILENAME='" + setupBean.getP_FILE_NAME() + 
            "' AND filedate =STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
          this.logger.info(Rquery);
          count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          this.logger.info("table name and count " + count);
        } else if (setupBean.getP_FILE_NAME().startsWith("88") && 
          setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC") && 
          setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
          this.logger.info(" chkFileupload RUPAY RUPAY_RUPAY_88_RAWDATA****");
          Rquery = "Select COUNT(*) from  rupay_rupay_88_rawdata where FILENAME='" + setupBean.getP_FILE_NAME() + 
            "' AND filedate =STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
          this.logger.info(Rquery);
          count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          this.logger.info("table name and count " + count);
        } else if (setupBean.getCategory().equalsIgnoreCase("QSPARC") && 
          setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")) {
          this.logger.info(" chkFileupload QSPARC****");
          if (setupBean.getP_FILE_NAME().startsWith("86")) {
            Rquery = "Select COUNT(*) from  qsparc_86_rawdata where FILENAME='" + setupBean.getP_FILE_NAME() + 
              "' AND filedate=STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          } else if (setupBean.getP_FILE_NAME().startsWith("01") || 
            setupBean.getP_FILE_NAME().startsWith("05") || 
            setupBean.getP_FILE_NAME().startsWith("03") || 
            setupBean.getP_FILE_NAME().startsWith("02")) {
            Rquery = "Select COUNT(*) from   rupay_qsparc_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          } 
          this.logger.info("table name and count " + count);
        } else if (setupBean.getStSubCategoryid().equalsIgnoreCase("INTERNATIONAL") && 
          setupBean.getCategory().equalsIgnoreCase("QSPARC")) {
          this.logger.info(" chkFileupload QSPARC_INT_86_RAWDATA****");
          if (setupBean.getP_FILE_NAME().startsWith("86")) {
            Rquery = "Select COUNT(*) from  qsparc_int_86_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          } else if (setupBean.getP_FILE_NAME().startsWith("88")) {
            Rquery = "Select COUNT(*) from qsparc_int_88_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          } else if (setupBean.getP_FILE_NAME().startsWith("01") || 
            setupBean.getP_FILE_NAME().startsWith("05") || 
            setupBean.getP_FILE_NAME().startsWith("02") || 
            setupBean.getP_FILE_NAME().startsWith("03")) {
            Rquery = "Select COUNT(*) from  rupay_qsparc_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          } 
          this.logger.info("table name and count " + count);
        } else if (setupBean.getStSubCategoryid().equalsIgnoreCase("INTERNATIONAL") && 
          setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
          this.logger.info(" chkFileupload RUPAY_RUPAY_86_INT_RAWDATA****");
          if (setupBean.getP_FILE_NAME().startsWith("01") || setupBean.getP_FILE_NAME().startsWith("05") || 
            setupBean.getP_FILE_NAME().startsWith("02") || 
            setupBean.getP_FILE_NAME().startsWith("03")) {
            Rquery = "Select COUNT(*) from  rupay_rupay_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
            this.logger.info("table name and count " + count);
          } else if (setupBean.getP_FILE_NAME().startsWith("88")) {
            Rquery = "Select COUNT(*) from  rupay_rupay_88_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
            this.logger.info("table name and count " + count);
          } else {
            Rquery = "Select COUNT(*) from  rupay_rupay_86_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
            this.logger.info("table name and count " + count);
          } 
        } else if (setupBean.getP_FILE_NAME().startsWith("88") && 
          setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
          this.logger.info(" chkFileupload RUPAY_RUPAY_88_RAWDATA****");
          Rquery = "Select COUNT(*) from  rupay_rupay_88_rawdata where FILENAME='" + setupBean.getP_FILE_NAME() + 
            "' AND filedate= STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
          this.logger.info(Rquery);
          count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          this.logger.info("table name and count " + count);
        } else {
          this.logger.info(" chkFileupload RUPAY_RUPAY_RAWDATA****");
          Rquery = "Select COUNT(*) from  rupay_rupay_rawdata where FILENAME='" + setupBean.getP_FILE_NAME() + 
            "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
          this.logger.info(Rquery);
          count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          this.logger.info("table name and count " + count);
        } 
      } else if (setupBean.getFilename().equalsIgnoreCase("RUPAY")) {
        String Rquery = "";
        this.logger.info(" chkcount RUPAY****");
        if (setupBean.getP_FILE_NAME().startsWith("86") && 
          setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC") && 
          setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
          this.logger.info(" chkFileupload RUPAY RUPAY_RUPAY_86_RAWDATA****");
          Rquery = "Select COUNT(*) from  rupay_rupay_86_rawdata where FILENAME='" + setupBean.getP_FILE_NAME() + 
            "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
          this.logger.info(Rquery);
          count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          this.logger.info("table name and count " + count);
        } else if (setupBean.getP_FILE_NAME().startsWith("88") && 
          setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC") && 
          setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
          this.logger.info(" chkFileupload RUPAY RUPAY_RUPAY_88_RAWDATA****");
          Rquery = "Select COUNT(*) from  rupay_rupay_88_rawdata where FILENAME='" + setupBean.getP_FILE_NAME() + 
            "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')";
          this.logger.info(Rquery);
          count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          this.logger.info("table name and count " + count);
        } else if (setupBean.getCategory().equalsIgnoreCase("QSPARC") && 
          setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")) {
          this.logger.info(" chkFileupload QSPARC****");
          if (setupBean.getP_FILE_NAME().startsWith("86")) {
            Rquery = "Select COUNT(*) from  qsparc_86_rawdata where FILENAME='" + setupBean.getP_FILE_NAME() + 
              "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          } else if (setupBean.getP_FILE_NAME().startsWith("88")) {
            Rquery = "Select COUNT(*) from  qsparc_88_rawdata where FILENAME='" + setupBean.getP_FILE_NAME() + 
              "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          } else if (setupBean.getP_FILE_NAME().startsWith("01") || 
            setupBean.getP_FILE_NAME().startsWith("05") || 
            setupBean.getP_FILE_NAME().startsWith("03") || 
            setupBean.getP_FILE_NAME().startsWith("02")) {
            Rquery = "Select COUNT(*) from  rupay_qsparc_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          } 
          this.logger.info("table name and count " + count);
        } else if (setupBean.getStSubCategoryid().equalsIgnoreCase("INTERNATIONAL") && 
          setupBean.getCategory().equalsIgnoreCase("QSPARC")) {
          this.logger.info(" chkFileupload QSPARC_INT_86_RAWDATA****");
          if (setupBean.getP_FILE_NAME().startsWith("86")) {
            Rquery = "Select COUNT(*) from qsparc_int_86_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')  ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          } else if (setupBean.getP_FILE_NAME().startsWith("88")) {
            Rquery = "Select COUNT(*) from  qsparc_int_88_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')  ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          } else if (setupBean.getP_FILE_NAME().startsWith("01") || 
            setupBean.getP_FILE_NAME().startsWith("05") || 
            setupBean.getP_FILE_NAME().startsWith("02") || 
            setupBean.getP_FILE_NAME().startsWith("03")) {
            Rquery = "Select COUNT(*) from  rupay_qsparc_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')  ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          } 
          this.logger.info("table name and count " + count);
        } else if (setupBean.getStSubCategoryid().equalsIgnoreCase("INTERNATIONAL") && 
          setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
          this.logger.info(" chkFileupload RUPAY_RUPAY_86_INT_RAWDATA****");
          if (setupBean.getP_FILE_NAME().startsWith("01") || setupBean.getP_FILE_NAME().startsWith("05") || 
            setupBean.getP_FILE_NAME().startsWith("02") || 
            setupBean.getP_FILE_NAME().startsWith("03")) {
            Rquery = "Select COUNT(*) from  rupay_rupay_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')  ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
            this.logger.info("table name and count " + count);
          } else if (setupBean.getP_FILE_NAME().startsWith("88")) {
            Rquery = "Select COUNT(*) from  rupay_rupay_88_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate= STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')  ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
            this.logger.info("table name and count " + count);
          } else {
            Rquery = "Select COUNT(*) from rupay_rupay_86_int_rawdata where FILENAME='" + 
              setupBean.getP_FILE_NAME() + "' AND filedate= STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')  ";
            this.logger.info(Rquery);
            count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
            this.logger.info("table name and count " + count);
          } 
        } else {
          this.logger.info(" chkFileupload RUPAY_RUPAY_RAWDATA****");
          Rquery = "Select COUNT(*) from  rupay_rupay_rawdata where FILENAME='" + setupBean.getP_FILE_NAME() + 
            "' AND filedate=STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
          this.logger.info(Rquery);
          count = ((Integer)getJdbcTemplate().queryForObject(Rquery, Integer.class)).intValue();
          this.logger.info("table name and count " + count);
        } 
      } else if (setupBean.getFilename().equalsIgnoreCase("NFS")) {
        if (setupBean.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
          this.logger.info(
              "Select  count(*) from nfs_nfs_acq_rawdata  where  filedate=STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ");
          count = ((Integer)getJdbcTemplate()
            .queryForObject("Select  count(*) from nfs_nfs_acq_rawdata where filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ", Integer.class)).intValue();
        } else {
          this.logger.info("Select  count(*) from nfs_nfs_iss_rawdata  where filedate=STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ");
          count = ((Integer)getJdbcTemplate()
            .queryForObject("Select  count(*) from  nfs_nfs_iss_rawdata  where filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ", Integer.class)).intValue();
        } 
      } else if (setupBean.getFilename().equalsIgnoreCase("ICD")) {
        if (setupBean.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
          this.logger.info(
              "Select  count(*) from  icd_icd_acq_rawdata  where  filedate=STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ");
          count = ((Integer)getJdbcTemplate().queryForObject(
              "Select  count(*) from icd_icd_acq_rawdata where filedate =STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ", 
              Integer.class)).intValue();
        } else {
          this.logger.info("Select  count(*) from ICD_ICD_ISS_RAWDATA  where filedate=STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ");
          count = ((Integer)getJdbcTemplate().queryForObject(
              "Select  count(*) from    icd_icd_iss_rawdata where filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ", 
              Integer.class)).intValue();
        } 
      } else if (setupBean.getFilename().equalsIgnoreCase("DFS")) {
        if (setupBean.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
          this.logger.info(
              "Select  count(*) from   dfs_dfs_acq_rawdata where  filedate=STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ");
          count = ((Integer)getJdbcTemplate().queryForObject(
              "Select  count(*) from dfs_dfs_acq_rawdata where filedate= STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ", 
              Integer.class)).intValue();
        } else {
          this.logger.info("Select  count(*) from  dfs_dfs_iss_rawdata where filedate=STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ");
          count = ((Integer)getJdbcTemplate().queryForObject(
              "Select  count(*) from    dfs_dfs_iss_rawdata where filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ", 
              Integer.class)).intValue();
        } 
      } else if (setupBean.getFilename().equalsIgnoreCase("JCB")) {
        if (setupBean.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
          this.logger.info(
              "Select  count(*) from  jcb_jcb_acq_rawdata  where  filedate=STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ");
          count = ((Integer)getJdbcTemplate().queryForObject(
              "Select  count(*) from  jcb_jcb_acq_rawdata where filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ", 
              Integer.class)).intValue();
        } else {
          this.logger.info("Select  count(*) from JCB_JCB_ISS_RAWDATA  where filedate=STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ");
          count = ((Integer)getJdbcTemplate().queryForObject(
              "Select  count(*) from  jcb_jcb_iss_rawdata  where filedate =STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ", 
              Integer.class)).intValue();
        } 
      } else if (setupBean.getFilename().equals("CBS")) {
        query = " select count(*) from cbs_rupay_rawdata where str_to_date(filedate,'DD-MM-YY') = str_to_date('" + 
          setupBean.getFileDate() + "','DD-MM-YY')";
        count = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
      } else if (setupBean.getCategory().equalsIgnoreCase("VISA_ACQ")) {
        query = " select count(*) from  visa_acq_rawdata where filedate = STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
        count = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
      } else if (setupBean.getCategory().equalsIgnoreCase("VISA")) {
        query = " select count(*) from  visa_visa_rawdata where filedate =STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ";
        count = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
      } else {
        count = ((Integer)getJdbcTemplate().queryForObject(
            "Select  count(*) from " + tablename + " where filedate =STR_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') ", 
            Integer.class)).intValue();
      } 
      this.logger.info("count==" + count);
      this.logger.info("***** CompareConfigDaoImpl.getrecordcount End ****");
      return count;
    } catch (Exception e) {
      this.logger.info("Exception while getting count " + e);
      return 0;
    } 
  }
  
  public HashMap<String, Object> uploadREV_File2(CompareSetupBean setupBean, MultipartFile file) {
    int extn = file.getOriginalFilename().indexOf(".");
    HashMap<String, Object> output = new HashMap<>();
    PreparedStatement ps = null;
    FormulaEvaluator objFormulaEvaluator = null;
    int lineNumber = 0, fileUploadedCount = 0, count = 0, countA = 0;
    try {
      XSSFWorkbook xSSFWorkbook = null;
      Workbook wb = null;
      Sheet sheet = null;
      String sql = "INSERT INTO  mastercard_ta_rawdata (BANKNAME, BANKROUTINGNO, PAYMENTCURRENCY, SERVICENAME, SERVICEID, TRANAGENTBANKNAME, CUSTOMERID, SETTLACCOUNTNUMBER, VALUEDATE, TRANAGENTBANKACCOUNTNO, RECON_DATE, RECON_CYCLE, INPUT_SOURCE, CLEARING_FILE_REF_ID, ORIENTED_AMOUNT, ORIENTED_DC, RECEIVED_AMOUNT, RECEIVED_DC, NET_AMOUNT, NET_DC,  FILENAME, FILEDATE,DECSCIPTION) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      ps = getConnection().prepareStatement(sql);
      String cycle = "";
      String fileName = file.getOriginalFilename();
      this.logger.info("FileName is " + fileName);
      String[] fileNames = fileName.split("_");
      if (fileNames.length > 0)
        cycle = fileNames[1].substring(0, 1); 
      this.logger.info("Cycle is: " + cycle);
      if (file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLS")) {
        HSSFWorkbook hSSFWorkbook = new HSSFWorkbook(file.getInputStream());
        sheet = hSSFWorkbook.getSheetAt(0);
        HSSFFormulaEvaluator hSSFFormulaEvaluator = new HSSFFormulaEvaluator(hSSFWorkbook);
      } else if (file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLSX")) {
        xSSFWorkbook = new XSSFWorkbook(file.getInputStream());
        sheet = xSSFWorkbook.getSheetAt(0);
        XSSFFormulaEvaluator xSSFFormulaEvaluator = new XSSFFormulaEvaluator(xSSFWorkbook);
      } 
      System.out.println("success");
      double OR_AMOUNT = 0.0D, RECEIVE_AMOUNT = 0.0D, NET_AMOUNT = 0.0D, OR_AMOUNT_T = 0.0D, RECEIVE_AMOUNT_T = 0.0D;
      double NET_AMOUNT_T = 0.0D;
      String BANKNAME = "", CUSTOMETOTAL = "", BANKROUTINGNO = "", PAYMENTCURRENCY = "", SERVICENAME = "";
      String SERVICEID = "", TRANAGENTBANKNAME = "", CUSTOMERID = "", SETTLACCOUNTNUMBER = "", VALUEDATE = "";
      String TRANAGENTBANKACCOUNTNO = "", RECON_DATE = "", RECON_CYCLE = "", INPUT_SOURCE = "";
      String CLEARING_FILE_REF_ID = "", ORIENTED_DC = "", RECEIVED_DC = "", NET_DC = "";
      if (xSSFWorkbook != null) {
        for (Row row : sheet) {
          for (Cell cell : row) {
            lineNumber++;
            System.out.println("cell.getCellType() " + cell.getCellType());
            switch (cell.getCellType()) {
              case 0:
                if (DateUtil.isCellDateFormatted(cell)) {
                  System.out.println(String.valueOf(cell.getStringCellValue()) + "\t");
                  continue;
                } 
                if (lineNumber > 12) {
                  System.out.println("countA  " + countA);
                  System.out.println("cell 0 " + cell.getNumericCellValue() + "\t");
                  if (countA == 0)
                    OR_AMOUNT_T = cell.getNumericCellValue(); 
                  if (countA == 1) {
                    OR_AMOUNT = cell.getNumericCellValue();
                  } else if (countA == 2) {
                    RECEIVE_AMOUNT = cell.getNumericCellValue();
                  } else if (countA == 3) {
                    NET_AMOUNT = cell.getNumericCellValue();
                  } 
                  countA++;
                } 
              case 1:
                if (cell.getStringCellValue().contains("Mastercard Settlement Bank Name")) {
                  BANKNAME = cell.getStringCellValue().substring(32, cell.getStringCellValue().length());
                } else {
                  if (cell.getStringCellValue().contains("Transfer Agent Bank Routing Number")) {
                    BANKROUTINGNO = cell.getStringCellValue().substring(37, 
                        cell.getStringCellValue().length());
                    continue;
                  } 
                  if (cell.getStringCellValue().contains("Payment Currency")) {
                    PAYMENTCURRENCY = cell.getStringCellValue().substring(18, 
                        cell.getStringCellValue().length());
                    continue;
                  } 
                  if (cell.getStringCellValue().contains("Service Name")) {
                    SERVICENAME = cell.getStringCellValue().substring(13, 
                        cell.getStringCellValue().length());
                    continue;
                  } 
                  if (cell.getStringCellValue().contains("Service ID")) {
                    SERVICEID = cell.getStringCellValue().substring(12, cell.getStringCellValue().length());
                    continue;
                  } 
                  if (cell.getStringCellValue().contains("Transfer Agent Bank Name")) {
                    TRANAGENTBANKNAME = cell.getStringCellValue().substring(25, 
                        cell.getStringCellValue().length());
                    continue;
                  } 
                  if (cell.getStringCellValue().contains("Customer ID")) {
                    CUSTOMERID = cell.getStringCellValue().substring(12, 
                        cell.getStringCellValue().length());
                    continue;
                  } 
                  if (cell.getStringCellValue().contains("Mastercard Settlement Account Number")) {
                    SETTLACCOUNTNUMBER = cell.getStringCellValue().substring(37, 
                        cell.getStringCellValue().length());
                    continue;
                  } 
                  if (cell.getStringCellValue().contains("Value Date")) {
                    VALUEDATE = cell.getStringCellValue().substring(11, cell.getStringCellValue().length());
                    continue;
                  } 
                  if (cell.getStringCellValue().contains("Transfer Agent Bank Account Number")) {
                    TRANAGENTBANKACCOUNTNO = cell.getStringCellValue().substring(34, 
                        cell.getStringCellValue().length());
                    continue;
                  } 
                } 
                if (lineNumber > 12) {
                  System.out.println("count  " + count);
                  System.out.println("cell 1 " + cell.getStringCellValue() + "\t");
                  if (count == 0) {
                    RECON_DATE = cell.getStringCellValue();
                  } else if (count == 1) {
                    RECON_CYCLE = cell.getStringCellValue();
                  } else if (count == 2) {
                    INPUT_SOURCE = cell.getStringCellValue();
                  } else if (count == 3) {
                    CLEARING_FILE_REF_ID = cell.getStringCellValue();
                  } else if (count == 4) {
                    ORIENTED_DC = cell.getStringCellValue();
                  } else if (count == 5) {
                    RECEIVED_DC = cell.getStringCellValue();
                  } else if (count == 6) {
                    NET_DC = cell.getStringCellValue();
                  } 
                  count++;
                  continue;
                } 
                if (cell.getStringCellValue().contains("Customer Totals") || 
                  cell.getStringCellValue().contains("Grand Total for")) {
                  CUSTOMETOTAL = cell.getStringCellValue();
                  System.out.println("CUSTOMETOTAL " + CUSTOMETOTAL);
                } 
            } 
          } 
          System.out.println("OR_AMOUNT_T " + OR_AMOUNT_T);
          System.out.println("NET_AMOUNT_T" + NET_AMOUNT_T);
          System.out.println("RECEIVE_AMOUNT_T " + RECEIVE_AMOUNT_T);
          System.out.println("OR_AMOUNT" + OR_AMOUNT);
          System.out.println("NET_AMOUNT " + NET_AMOUNT);
          System.out.println("RECEIVE_AMOUNT " + RECEIVE_AMOUNT);
          System.out.println("RECON_DATE " + RECON_DATE);
          System.out.println("RECON_CYCLE " + RECON_CYCLE);
          System.out.println("INPUT_SOURCE " + INPUT_SOURCE);
          System.out.println("CLEARING_FILE_REF_ID " + CLEARING_FILE_REF_ID);
          System.out.println("ORIENTED_DC " + ORIENTED_DC);
          System.out.println("RECEIVED_DC " + RECEIVED_DC);
          System.out.println("NET_DC " + NET_DC);
          if ((lineNumber > 15 && !RECON_DATE.contains("Customer Name") && !RECON_DATE.contains("Originated") && 
            !RECON_DATE.contains("No.") && ORIENTED_DC.equalsIgnoreCase("C")) || 
            ORIENTED_DC.equalsIgnoreCase("D")) {
            ps.setString(1, BANKNAME);
            ps.setString(2, BANKROUTINGNO);
            ps.setString(3, PAYMENTCURRENCY);
            ps.setString(4, SERVICENAME);
            ps.setString(5, SERVICEID);
            ps.setString(6, TRANAGENTBANKNAME);
            ps.setString(7, CUSTOMERID);
            ps.setString(8, SETTLACCOUNTNUMBER);
            ps.setString(9, VALUEDATE);
            ps.setString(10, TRANAGENTBANKACCOUNTNO);
            if (RECON_DATE.contains("Customer Totals") || RECON_DATE.contains("Grand Total for")) {
              ps.setString(11, "");
            } else {
              ps.setString(11, RECON_DATE.replaceAll(",", "").replaceAll("\\s", ""));
            } 
            ps.setString(12, RECON_CYCLE);
            ps.setString(13, INPUT_SOURCE);
            ps.setString(14, CLEARING_FILE_REF_ID);
            if (RECON_DATE.contains("Customer Totals") || RECON_DATE.contains("Grand Total for")) {
              ps.setDouble(15, OR_AMOUNT_T);
            } else {
              ps.setDouble(15, OR_AMOUNT);
            } 
            ps.setString(16, ORIENTED_DC);
            if (RECON_DATE.contains("Customer Totals") || RECON_DATE.contains("Grand Total for")) {
              ps.setDouble(17, OR_AMOUNT);
            } else {
              ps.setDouble(17, RECEIVE_AMOUNT);
            } 
            ps.setString(18, RECEIVED_DC);
            if (RECON_DATE.contains("Customer Totals") || RECON_DATE.contains("Grand Total for")) {
              ps.setDouble(19, RECEIVE_AMOUNT);
            } else {
              ps.setDouble(19, NET_AMOUNT);
            } 
            ps.setString(20, NET_DC);
            ps.setString(21, file.getOriginalFilename());
            ps.setString(22, setupBean.getFileDate());
            if (RECON_DATE.contains("Customer Totals") || RECON_DATE.contains("Grand Total for")) {
              ps.setString(23, RECON_DATE);
            } else {
              ps.setString(23, "TA");
            } 
            ps.execute();
          } 
          count = 0;
          countA = 0;
          System.out.println("");
        } 
        xSSFWorkbook.close();
      } 
      output.put("result", Boolean.valueOf(true));
      output.put("msg", "Successfully Uploaded ");
      System.out.println("success");
      return output;
    } catch (NotOLE2FileException e) {
      this.logger.info("File is not excel " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Uploaded file is not Excel");
      return output;
    } catch (Exception ex) {
      this.logger.info(ex.toString());
      ex.printStackTrace();
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occurred while uplaoding file");
      return output;
    } 
  }
  
  public HashMap<String, Object> uploadREV_File(CompareSetupBean setupBean, MultipartFile file) {
    int extn = file.getOriginalFilename().indexOf(".");
    HashMap<String, Object> output = new HashMap<>();
    PreparedStatement ps = null;
    FormulaEvaluator objFormulaEvaluator = null;
    int lineNumber = 0, fileUploadedCount = 0;
    try {
      XSSFFormulaEvaluator xSSFFormulaEvaluator = null;
      Workbook wb = null;
      Sheet sheet = null;
      String cycle = "";
      String fileName = file.getOriginalFilename();
      this.logger.info("FileName is " + fileName);
      String[] fileNames = fileName.split("_");
      if (fileNames.length > 0)
        cycle = fileNames[1].substring(0, 1); 
      this.logger.info("Cycle is: " + cycle);
      if (file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLS")) {
        HSSFWorkbook hSSFWorkbook = new HSSFWorkbook(file.getInputStream());
        sheet = hSSFWorkbook.getSheetAt(0);
        HSSFFormulaEvaluator hSSFFormulaEvaluator = new HSSFFormulaEvaluator(hSSFWorkbook);
      } else if (file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLSX")) {
        XSSFWorkbook xSSFWorkbook = new XSSFWorkbook(file.getInputStream());
        sheet = xSSFWorkbook.getSheetAt(0);
        xSSFFormulaEvaluator = new XSSFFormulaEvaluator(xSSFWorkbook);
      } 
      System.out.println("success");
      int uploadCount = ((Integer)getJdbcTemplate().queryForObject(
          "SELECT COUNT(*) FROM  main_file_upload_dtls WHERE FILEDATE=str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') AND FILEID IN (SELECT FILEID FROM main_filesource WHERE FILENAME = 'REV_REPORT')", 
          Integer.class)).intValue();
      if (uploadCount > 0)
        fileUploadedCount = ((Integer)getJdbcTemplate().queryForObject(
            "select FILE_COUNT from main_file_upload_dtls where FILEDATE= str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d')\tAND FILEID IN (SELECT FILEID FROM main_filesource WHERE FILENAME = 'REV_REPORT')", 
            Integer.class)).intValue(); 
      int ActualFileCount = ((Integer)getJdbcTemplate().queryForObject(
          "SELECT FILE_COUNT FROM  main_filesource WHERE FILENAME = 'REV_REPORT'", Integer.class)).intValue();
      if (fileUploadedCount != ActualFileCount) {
        int dataCount = ((Integer)getJdbcTemplate().queryForObject(
            "Select count(*) from  nfs_rev_acq_report where FILEDATE = str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d') AND CYCLE = '" + cycle + "' and FILENAME='" + 
            file.getOriginalFilename() + "'", 
            Integer.class)).intValue();
        boolean result = false;
        if (dataCount > 0)
          result = true; 
        if (!result) {
          Row row = sheet.getRow(4);
          try {
            short s = sheet.getRow(3).getLastCellNum();
          } catch (NullPointerException ne) {
            if (uploadCount > 0) {
              String updateTable = "UPDATE main_file_upload_dtls SET FILE_COUNT = FILE_COUNT+1 WHERE FILEID IN (SELECT FILEID FROM main_filesource WHERE FILENAME = 'REV_REPORT') AND FILEDATE =str_to_date('" + 
                setupBean.getFileDate() + "','%Y/%m/%d')";
              getJdbcTemplate().execute(updateTable);
            } else {
              int FILEID = ((Integer)getJdbcTemplate().queryForObject(
                  "SELECT FILEID FROM  main_filesource WHERE FILENAME = 'REV_REPORT'", Integer.class)).intValue();
              String insertData = "INSERT INTO main_file_upload_dtls (FILEID,FILEDATE,UPDLODBY, CATEGORY, FILTER_FLAG, KNOCKOFF_FLAG, COMAPRE_FLAG, MANUALCOMPARE_FLAG ,UPLOAD_FLAG,MANUPLOAD_FLAG, FILE_SUBCATEGORY, FILE_COUNT) VALUES('" + 
                FILEID + "', str_to_date(FILEDATE,'DD-MM-YY'), '" + 
                setupBean.getCreatedBy() + "', 'NFS', 'N','N','N','N','Y','Y', 'ACQUIRER' ,'1')";
              getJdbcTemplate().execute(insertData);
            } 
            output.put("result", Boolean.valueOf(false));
            output.put("msg", "Uploaded File is blank!!");
            return output;
          } 
          System.out.println("sheet.getLastRowNum()" + sheet.getLastRowNum());
          row = sheet.getRow(1);
          System.out.println("sheet.getLastRowNum()" + row.getCell(0).getStringCellValue());
          if (row.getCell(0).getStringCellValue().contains("Verification Report")) {
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
              row = sheet.getRow(i);
              String offsitebTotal = row.getCell(0).getStringCellValue();
              SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
              String sql = "insert into  nfs_rev_acq_report (TransType ,Resp_Code,Cardno ,RRN ,StanNo ,ACQ ,ISS ,Trasn_Date,Trans_Time ,ATMId ,SettleDate ,RequestAmt ,ReceivedAmt ,Status,dcrs_remarks,Filedate,CYCLE,MERCHANT_TYPE,FPAN,FILENAME) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,str_to_date(?,'%Y/%m/%d'),?,?,?,?)";
              ps = getConnection().prepareStatement(sql);
              DataFormatter objDefaultFormat = new DataFormatter();
              Cell txn_time = row.getCell(8);
              Cell atm_id = row.getCell(9);
              Cell settl_dt = row.getCell(10);
              Cell cell7 = row.getCell(7);
              xSSFFormulaEvaluator.evaluate(atm_id);
              String atmid = objDefaultFormat.formatCellValue(atm_id, (FormulaEvaluator)xSSFFormulaEvaluator);
              xSSFFormulaEvaluator.evaluate(txn_time);
              String txntime = objDefaultFormat.formatCellValue(txn_time, (FormulaEvaluator)xSSFFormulaEvaluator);
              xSSFFormulaEvaluator.evaluate(settl_dt);
              String settldt = objDefaultFormat.formatCellValue(settl_dt, (FormulaEvaluator)xSSFFormulaEvaluator);
              xSSFFormulaEvaluator.evaluate(cell7);
              String cell_7 = objDefaultFormat.formatCellValue(cell7, (FormulaEvaluator)xSSFFormulaEvaluator);
              ps.setString(1, row.getCell(0).getStringCellValue().replace("'", ""));
              ps.setString(2, row.getCell(1).getStringCellValue().replace("'", ""));
              String new_pan = row.getCell(2).getStringCellValue().replace("`", "").trim();
              new_pan = String.valueOf(new_pan.substring(0, 6)) + "XXXXXX" + new_pan.substring(new_pan.length() - 4);
              ps.setString(3, row.getCell(2).getStringCellValue().replace("'", "").trim());
              ps.setString(4, row.getCell(3).getStringCellValue().replace("'", ""));
				ps.setString(5, row.getCell(4).getStringCellValue().replace("'", ""));
				ps.setString(6, row.getCell(5).getStringCellValue().replace("'", ""));
				ps.setString(7, row.getCell(6).getStringCellValue().replace("'", ""));
				ps.setString(8, (cell_7.replaceAll("\u00A0", "").replace("'", "")));
				ps.setString(9, setupBean.getFileDate());

				final Date dateObj = sdf.parse(txntime.replaceAll("\u00A0", ""));

				ps.setString(9, new SimpleDateFormat("Kmmss").format(dateObj));
				ps.setString(10, (atmid.replaceAll("\u00A0", "")).replace("'", ""));
				ps.setString(11, (settldt.replaceAll("\u00A0", "")).replace("'", ""));

				ps.setString(12, row.getCell(11).getStringCellValue());
				String value = null;
				int type = row.getCell(12).getCellType();
				if (type == 0) {
					value = NumberToTextConverter.toText(row.getCell(12).getNumericCellValue());
				} else {
					value = String.valueOf(row.getCell(12).getStringCellValue());
				}

				ps.setString(13, value);
				ps.setString(14, row.getCell(13).getStringCellValue().replace("'", ""));
				ps.setString(15, "UNMATCHED");
				ps.setString(16, setupBean.getFileDate());
				ps.setString(17, cycle);
              String pan = row.getCell(2).getStringCellValue().replace("`", "");
              String Merchant_type = "";
              ps.setString(18, Merchant_type);
              ps.setString(19, row.getCell(2).getStringCellValue().replace("'", "").trim());
              ps.setString(20, setupBean.getP_FILE_NAME());
              lineNumber++;
              ps.executeUpdate();
            } 
          } else {
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
              row = sheet.getRow(i);
              String offsitebTotal = row.getCell(0).getStringCellValue();
              SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
              String sql = "insert into  nfs_rev_acq_report (TransType ,Resp_Code,Cardno ,RRN ,StanNo ,ACQ ,ISS ,Trasn_Date,Trans_Time ,ATMId ,SettleDate ,RequestAmt ,ReceivedAmt ,Status,dcrs_remarks,Filedate,CYCLE,MERCHANT_TYPE,FPAN,FILENAME) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,str_to_date(?,'%Y/%m/%d'),?,?,?,?)";
              ps = getConnection().prepareStatement(sql);
              DataFormatter objDefaultFormat = new DataFormatter();
              Cell txn_time = row.getCell(8);
              Cell atm_id = row.getCell(9);
              Cell settl_dt = row.getCell(10);
              Cell cell7 = row.getCell(7);
              xSSFFormulaEvaluator.evaluate(atm_id);
              String atmid = objDefaultFormat.formatCellValue(atm_id, (FormulaEvaluator)xSSFFormulaEvaluator);
              xSSFFormulaEvaluator.evaluate(txn_time);
              String txntime = objDefaultFormat.formatCellValue(txn_time, (FormulaEvaluator)xSSFFormulaEvaluator);
              xSSFFormulaEvaluator.evaluate(settl_dt);
              String settldt = objDefaultFormat.formatCellValue(settl_dt, (FormulaEvaluator)xSSFFormulaEvaluator);
              xSSFFormulaEvaluator.evaluate(cell7);
              String cell_7 = objDefaultFormat.formatCellValue(cell7, (FormulaEvaluator)xSSFFormulaEvaluator);
              ps.setString(1, row.getCell(0).getStringCellValue().replace("'", ""));
              ps.setString(2, row.getCell(1).getStringCellValue().replace("'", ""));
              String new_pan = row.getCell(2).getStringCellValue().replace("'", "").trim();
              new_pan = String.valueOf(new_pan.substring(0, 6)) + "XXXXXX" + new_pan.substring(new_pan.length() - 4);
              this.logger.info("New Pan is " + new_pan);
              ps.setString(3, row.getCell(2).getStringCellValue().replace("`", "").trim());
              ps.setString(4, row.getCell(3).getStringCellValue().replace("'", ""));
              ps.setString(5, row.getCell(4).getStringCellValue().replace("'", ""));
              ps.setString(6, row.getCell(5).getStringCellValue().replace("'", ""));
              ps.setString(7, row.getCell(6).getStringCellValue().replace("'", ""));
              ps.setString(8, (cell_7.replaceAll("\u00A0", "").replace("'", "")));
              ps.setString(9, setupBean.getFileDate());
              final Date dateObj = sdf.parse(txntime.replaceAll("\u00A0", ""));
              ps.setString(9, (new SimpleDateFormat("Kmmss")).format(dateObj));
      		ps.setString(10, (atmid.replaceAll("\u00A0", "")).replace("'", ""));
			ps.setString(11, (settldt.replaceAll("\u00A0", "")).replace("'", ""));
              ps.setString(12, row.getCell(11).getStringCellValue());
              String value = null;
              int type = row.getCell(12).getCellType();
              if (type == 0) {
                value = NumberToTextConverter.toText(row.getCell(12).getNumericCellValue());
              } else {
                value = String.valueOf(row.getCell(12).getStringCellValue());
              } 
              ps.setString(13, value);
              ps.setString(14, row.getCell(13).getStringCellValue().replace("'", ""));
              ps.setString(15, "UNMATCHED");
              ps.setString(16, setupBean.getFileDate());
              ps.setString(17, cycle);
              String pan = row.getCell(2).getStringCellValue().replace("`", "");
              String Merchant_type = "";
              ps.setString(18, Merchant_type);
              ps.setString(19, row.getCell(2).getStringCellValue().replace("'", "").trim());
              ps.setString(20, setupBean.getP_FILE_NAME());
              lineNumber++;
              ps.executeUpdate();
            } 
          } 
          ps.close();
          if (uploadCount > 0) {
            String updateTable = "UPDATE main_file_upload_dtls SET FILE_COUNT = FILE_COUNT+1 WHERE FILEID IN (SELECT FILEID FROM  main_filesource WHERE FILENAME = 'REV_REPORT') AND FILEDATE  =str_to_date('" + 
              setupBean.getFileDate() + "','%Y/%m/%d')";
            getJdbcTemplate().execute(updateTable);
          } else {
            String query = "SELECT FILEID FROM   main_filesource WHERE FILENAME = 'REV_REPORT'";
            int fileid = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
            String insertData = "INSERT INTO main_file_upload_dtls (FILEID,FILEDATE,UPDLODBY, CATEGORY, FILTER_FLAG, KNOCKOFF_FLAG, COMAPRE_FLAG, MANUALCOMPARE_FLAG ,UPLOAD_FLAG,MANUPLOAD_FLAG, FILE_SUBCATEGORY, FILE_COUNT) VALUES('" + 
              fileid + "',  str_to_date('" + setupBean.getFileDate() + "','%Y/%m/%d'), '" + 
              setupBean.getCreatedBy() + "', 'NFS', 'N','N','N','N','Y','Y', 'ACQUIRER' ,'1')";
            getJdbcTemplate().execute(insertData);
          } 
          output.put("result", Boolean.valueOf(true));
          output.put("msg", "success " + lineNumber);
          System.out.println("success");
          return output;
        } 
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "Selected cycle file is already Uploaded");
        System.out.println("File already Uploaded");
        System.out.println("already");
        return output;
      } 
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Uploaded File is blanck!");
      this.logger.info("All Files are uploaded");
      System.out.println("uploaded");
      return output;
    } catch (NotOLE2FileException e) {
      this.logger.info("File is not excel " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Uploaded file is not Excel");
      return output;
    } catch (Exception ex) {
      this.logger.info(ex.toString());
      ex.printStackTrace();
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occurred while uplaoding file");
      return output;
    } 
  }
  
  public int getREVrecordcount(CompareSetupBean setupBean) {
    int count = 0;
    String query = "";
    if (setupBean.getFilename().equals("MC-TA"))
      query = "select count(*) from MASTERCARD_TA_RAWDATA where str_to_date(FILEDATE,'DD-MM-YY')=str_to_date('" + 
        setupBean.getFileDate() + "','dd-mm-yy')   "; 
    count = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
    return count;
  }
  
  public boolean chkBeforeUploadFile(String flag, CompareSetupBean setupBean) throws Exception {
	return false;}
  
  public boolean insertUploadTRAN(CompareSetupBean setupBean) {
    String query = "";
    String BATCHNO = "TO_CHAR(str_to_date('" + setupBean.getFILEDATE() + "', 'dd/mm/rrrr'), 'ddmmrr') || " + 
      setupBean.getFILEID() + "||" + setupBean.getSUB_FILEID() + " || " + setupBean.getP_FILE_NAME();
    try {
      query = "INSERT INTO  FILE_UPLOAD_MASTER (ID, FILEID, SUB_FILEID ,P_FILE_NAME ) VALUES(" + BATCHNO + " , " + 
        setupBean.getFILEID() + "," + setupBean.getSUB_FILEID() + "  ," + setupBean.getP_FILE_NAME() + 
        "  )";
      int count = getJdbcTemplate().update(query);
      if (count > 0) {
        query = " INSERT INTO   FILE_UPLOAD_TRANS(ID,FILEDATE) VALUES (" + BATCHNO + ", str_to_date( '" + 
          setupBean.getFILEDATE() + "' , 'dd/mm/rrrr') )";
        count = getJdbcTemplate().update(query);
      } 
      if (count > 0)
        return true; 
      return false;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    } 
  }
  
  public boolean insertFileTranDate(CompareSetupBean setupBean) {
    String sql = "";
    try {
      sql = "INSERT FILE_UPLOAD_TRANS ( ID,FILEDATE)VALUES ( ?, ?)";
      System.out.println("sql::>>>>>>>>>>>>" + sql);
      int row = getJdbcTemplate().update(sql, new Object[] { Integer.valueOf(setupBean.getFILEID()), setupBean.getFILEDATE() });
      System.out.println(String.valueOf(row) + " row inserted.");
    } catch (Exception exception) {}
    return false;
  }
  
  public boolean updateFlag2(CompareSetupBean setupBean) {
    try {
      int rowupdate = 0;
      int rowupdate1 = 0;
      int count1 = 0;
      OracleConn conn = new OracleConn();
      String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'VISA' and FILE_CATEGORY='WCC'   ";
      this.con = conn.getconn();
      this.st = this.con.createStatement();
      ResultSet rs = this.st.executeQuery(switchList);
      while (rs.next()) {
        String query = " insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) values (" + 
          rs.getString("FILEID") + ",str_to_date('" + setupBean.getFileDate() + 
          "','DD-MM-YY'),'AUTOMATION',sysdate,'" + setupBean.getCategory() + "','" + 
          rs.getString("file_subcategory") + "'" + ",'" + this.upload_flag + "','N','N','N','N','" + this.man_flag + 
          "')";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        this.st.executeUpdate(query);
        count1++;
        String insert_count = "update Main_File_Upload_Dtls set file_count='" + count1 + 
          "'  WHERE to_char(filedate,'DD-MM-YY') = to_char(str_to_date('" + setupBean.getFileDate() + 
          "','dd/MM/yyyy'),'MM/dd/yyyy') " + " AND CATEGORY = 'WCC' AND FileId = '58'";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        this.st.executeUpdate(insert_count);
        String count = "SELECT file_count from Main_File_Upload_Dtls WHERE to_char(filedate,'MM/dd/yyyy') = to_char(str_to_date('" + 
          setupBean.getFileDate() + "','dd/MM/yyyy'),'DD-MM-YY') " + 
          " AND CATEGORY = 'WCC' AND FileId = '58'   ";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        ResultSet rs1 = this.st.executeQuery(count);
        while (rs1.next()) {
          this.value = rs1.getString("file_count");
          if (this.value.equals("3")) {
            String query1 = "Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='Y'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(str_to_date('" + 
              setupBean.getFileDate() + "','dd/MM/yyyy'),'mm/dd/yyyy') " + 
              " AND CATEGORY = 'WCC' AND FileId ='58'";
            this.con = conn.getconn();
            this.st = this.con.createStatement();
            rowupdate = this.st.executeUpdate(query1);
            continue;
          } 
          String query2 = "Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='N'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(str_to_date('" + 
            setupBean.getFileDate() + "','dd/MM/yyyy'),'mm/dd/yyyy') " + 
            " AND CATEGORY = 'WCC' AND FileId ='58' ";
          this.con = conn.getconn();
          this.st = this.con.createStatement();
          rowupdate1 = this.st.executeUpdate(query2);
        } 
      } 
      return true;
    } catch (Exception ex) {
      System.out.println(ex);
      ex.printStackTrace();
      this.logger.error(ex.getMessage());
      return false;
    } finally {
      try {
        this.con.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } 
    } 
  }
  
  private int getFileCount(CompareSetupBean setupBean) {
    try {
      int count = 0;
      OracleConn conn = new OracleConn();
      String query = "Select count (*) from MAIN_FILE_UPLOAD_DTLS  WHERE str_to_date(FILEDATE,'DD-MM-YY')= str_to_date('" + 
        setupBean.getFileDate() + "','DD-MM-YY')  AND CATEGORY = '" + setupBean.getCategory() + 
        "' AND FileId ='58'";
      this.con = conn.getconn();
      this.st = this.con.createStatement();
      ResultSet rs = this.st.executeQuery(query);
      while (rs.next())
        count = rs.getInt(1); 
      return count;
    } catch (Exception ex) {
      ex.printStackTrace();
      return -1;
    } finally {
      try {
        this.st.close();
        this.con.close();
      } catch (SQLException e) {
        e.printStackTrace();
        this.logger.error(e.getMessage());
      } 
    } 
  }
  
  public boolean updateFlag1(CompareSetupBean setupBean) {
    try {
      OracleConn conn = new OracleConn();
      String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'VISA' and FILE_CATEGORY='WCC'  ";
      this.con = conn.getconn();
      this.st = this.con.createStatement();
      ResultSet rs = this.st.executeQuery(switchList);
      int rowupdate = 0;
      int rowupdate1 = 0;
      while (rs.next()) {
        int val = 0;
        String query = "Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='Y'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(str_to_date('" + 
          setupBean.getFileDate() + "','dd/MM/yyyy'),'mm/dd/yyyy') " + 
          " AND CATEGORY = 'WCC' AND FileId = '58' ";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        rowupdate = this.st.executeUpdate(query);
        String count1 = "SELECT file_count from Main_File_Upload_Dtls WHERE to_char(filedate,'MM/dd/yyyy') = to_char(str_to_date('" + 
          setupBean.getFileDate() + "','dd/MM/yyyy'),'MM/dd/yyyy') " + 
          " AND CATEGORY = 'WCC' AND FileId = '58'   ";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        ResultSet rs12 = this.st.executeQuery(count1);
        while (rs12.next()) {
          val = rs12.getInt("file_count");
          val++;
        } 
        String insert_count = "update Main_File_Upload_Dtls set file_count='" + val + 
          "'  WHERE to_char(filedate,'MM/dd/yyyy') = to_char(str_to_date('" + setupBean.getFileDate() + 
          "','dd/MM/yyyy'),'MM/dd/yyyy') " + " AND CATEGORY = 'WCC' AND FileId = '58'  ";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        this.st.executeUpdate(insert_count);
        String count = "SELECT file_count from Main_File_Upload_Dtls WHERE to_char(filedate,'MM/dd/yyyy') = to_char(str_to_date('" + 
          setupBean.getFileDate() + "','dd/MM/yyyy'),'MM/dd/yyyy') " + 
          " AND CATEGORY = 'WCC' AND FileId = '58'   ";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        ResultSet rs1 = this.st.executeQuery(count);
        while (rs1.next()) {
          this.value = rs1.getString("file_count");
          if (this.value.equals("3")) {
            String query1 = "Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='Y'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(str_to_date('" + 
              setupBean.getFileDate() + "','dd/MM/yyyy'),'mm/dd/yyyy') " + 
              " AND CATEGORY = 'WCC' AND FileId = '58' ";
            this.con = conn.getconn();
            this.st = this.con.createStatement();
            rowupdate = this.st.executeUpdate(query1);
            continue;
          } 
          String query2 = "Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG='N'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(str_to_date('" + 
            setupBean.getFileDate() + "','dd/MM/yyyy'),'mm/dd/yyyy') " + 
            " AND CATEGORY = 'WCC' AND FileId = '58' ";
          this.con = conn.getconn();
          this.st = this.con.createStatement();
          rowupdate1 = this.st.executeUpdate(query2);
        } 
      } 
      if (rowupdate > 0)
        return true; 
      return false;
    } catch (Exception ex) {
      ex.printStackTrace();
      this.logger.error(ex.getMessage());
      return false;
    } finally {
      try {
        this.st.close();
        this.con.close();
      } catch (SQLException e) {
        e.printStackTrace();
        this.logger.error(e.getMessage());
      } 
    } 
  }
  
  public HashMap<String, Object> uploadFileSwitchTlf(CompareSetupBean setupBean, MultipartFile file) {
    HashMap<String, Object> output = new HashMap<>();
    String thisline = null;
    int lineNumber = 0, feesize = 1;
    int count = 1;
    int sr_no = 1, batchNumber = 0, batchSize = 1000;
    boolean batchExecuted = false;
    String Filename = file.getOriginalFilename();
    String InsertQuery = " insert into  VISA_SWITCH_RAWDATA  (pan,TERMID ,Card_MEMBER_NUM ,Region_Id ,\r\n BRANCH_ID ,\r\nTRAN_DATE ,\r\nTRAN_TIME ,\r\nTERMI_TYPE  ,\r\n POSTED_DATE  ,          \r\nACQ_Inter_SettDt ,\r\nIss_Inter_SettDt ,\r\nSeq_Num ,\r\n From_Acc_Type ,\r\nTo_Acc_Type ,\r\nfrom_Acc ,\r\nTo_Acc  ,\r\nResp_Code  ,\r\n Termi_City  ,\r\nTermi_Name_Loa  ,\r\nCurrency_Code   ,\r\nCrad_Accep_Id  ,\r\n Card_Iss_Id  ,\r\nTran_amount ,\r\nReversal_Amt  ,\r\nRemains_bal  ,\r\n Tran_Code_Frm  ,\r\nTran_Code_To   ,\r\nTran_Code   ,\r\nDeposite_Bal  ,\r\n Crad_Identification_Status ,filename,\r\n filedate \r\n) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String update_query = "INSERT INTO SWITCH_DATA_VALIDATION(FILENAME,FILEDATE,COUNT,FILETYPE) VALUES(?,?,?,?)";
    try {
      OracleConn orc = new OracleConn();
      Connection getconn = orc.getconn();
      getconn.setAutoCommit(false);
      int batchCount = 0;
      PreparedStatement ps = getconn.prepareStatement(InsertQuery);
      PreparedStatement updatps = getconn.prepareStatement(update_query);
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      while ((thisline = br.readLine()) != null) {
        if (!thisline.trim().equals("")) {
          count++;
          batchExecuted = false;
          sr_no = 1;
          String pan = thisline.substring(71, 90).trim();
          String TERMID = thisline.substring(47, 63).trim();
          String Card_MEMBER_NUM = thisline.substring(90, 93).trim();
          String Region_Id = thisline.substring(97, 101).trim();
          String BRANCH_ID = thisline.substring(93, 97).trim();
          String TRAN_DATE = thisline.substring(170, 176).trim();
          String TRAN_TIME = thisline.substring(176, 184).trim();
          String TERMI_TYPE = thisline.substring(214, 216).trim();
          String POSTED_DATE = thisline.substring(184, 190).trim();
          String ACQ_Inter_SettDt = thisline.substring(190, 196).trim();
          String Iss_Inter_SettDt = thisline.substring(196, 202).trim();
          String Seq_Num = thisline.substring(202, 214).trim();
          String From_Acc_Type = thisline.substring(245, 247).trim();
          String To_Acc_Type = thisline.substring(247, 249).trim();
          String from_Acc = thisline.substring(249, 268).trim();
          String To_Acc = thisline.substring(269, 288).trim();
          String Resp_Code = thisline.substring(358, 358).trim();
          String Termi_City = thisline.substring(407, 420).trim();
          String Termi_Name_Loa = thisline.substring(360, 382).trim();
          String Currency_Code = thisline.substring(453, 456).trim();
          String Crad_Accep_Id = thisline.substring(565, 566).trim();
          String Card_Iss_Id = thisline.substring(576, 587).trim();
          String Tran_amount = thisline.substring(289, 308).trim();
          String Reversal_Amt = thisline.substring(308, 327).trim();
          String Remains_bal = thisline.substring(327, 346).trim();
          String Tran_Code_Frm = thisline.substring(245, 247).trim();
          String Tran_Code_To = thisline.substring(247, 249).trim();
          String Tran_Code = thisline.substring(245, 249).trim();
          String Deposite_Bal = thisline.substring(346, 356).trim();
          String Crad_Identification_Status = thisline.substring(63, 93).trim();
          System.out.println(String.valueOf(pan) + "==" + TERMID + "==" + Card_MEMBER_NUM + "==" + TRAN_DATE + "==" + TRAN_TIME + "==" + from_Acc + "==" + To_Acc);
          ps.setString(1, pan);
          ps.setString(2, TERMID);
          ps.setString(3, Card_MEMBER_NUM);
          ps.setString(4, Region_Id);
          ps.setString(5, BRANCH_ID);
          ps.setString(6, TRAN_DATE);
          ps.setString(7, TRAN_TIME);
          ps.setString(8, TERMI_TYPE);
          ps.setString(9, POSTED_DATE);
          ps.setString(10, ACQ_Inter_SettDt);
          ps.setString(11, Iss_Inter_SettDt);
          ps.setString(12, Seq_Num);
          ps.setString(13, From_Acc_Type);
          ps.setString(14, To_Acc_Type);
          ps.setString(15, from_Acc);
          ps.setString(16, To_Acc);
          ps.setString(17, Resp_Code);
          ps.setString(18, Termi_City);
          ps.setString(19, Termi_Name_Loa);
          ps.setString(20, Currency_Code);
          ps.setString(21, Crad_Accep_Id);
          ps.setString(22, Card_Iss_Id);
          ps.setString(23, Tran_amount);
          ps.setString(24, Reversal_Amt);
          ps.setString(25, Remains_bal);
          ps.setString(26, Tran_Code_Frm);
          ps.setString(27, Tran_Code_To);
          ps.setString(28, Tran_Code);
          ps.setString(29, Deposite_Bal);
          ps.setString(30, Crad_Identification_Status);
          ps.setString(31, Filename);
          ps.setString(32, setupBean.getFileDate());
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
}
