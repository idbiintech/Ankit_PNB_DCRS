package com.recon.dao.impl;


import com.recon.dao.ISourceDao;
import com.recon.model.CompareSetupBean;
import com.recon.model.ConfigurationBean;
import com.recon.model.Settle_final_report_condn_bean;
import com.recon.model.SettlementBean;
import com.recon.model.Settlement_FinalBean;
import com.recon.util.GenerateSettleTTUMBean;
import com.recon.util.demo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Component
public class SourceDAoImpl extends JdbcDaoSupport implements ISourceDao {
  public final String FILE_ID = "file_id";
  
  public final String CATEGORY = "category";
  
  public final String FILE_NAME = "file_name";
  
  public final String ENTRY_BY = "Entry_By";
  
  public final String ID = "i_id";
  
  public final String TABLE_NAME = "table_name";
  
  private static final String O_ERROR_CODE = "o_error_code";
  
  private static final String O_ERROR_MESSAGE = "o_error_message";
  
  private PlatformTransactionManager transactionManager;
  
  String MAIN_KNOCKOFF_CRITERIA = "Insert into MAIN_KNOCKOFF_CRITERIA (REVERSAL_ID,FILE_ID,HEADER,ENTRY_BY,ENTRY_DATE,PADDING,START_CHARPOSITION,CHAR_SIZE,HEADER_VALUE,CONDITION)values (?,?,?,?,sysdate,?,?,?,?,?)";
  
  String MAIN_REVERSAL_DETAIL = "Insert into MAIN_REVERSAL_DETAIL (REVERSAL_ID,FILE_ID,CATEGORY,HEADER,VALUE,ENTRY_BY,ENTRY_DATE)values (?,?,?,?,?,?,sysdate)";
  
  String MAIN_REVERSAL_PARAMETERS = "Insert into MAIN_REVERSAL_PARAMETERS (REVERSAL_ID,FILE_ID,HEADER,VALUE,ENTRY_BY,ENTRY_DATE)values (?,?,?,?,?,sysdate)";
  
  private int id;
  
  public void setTransactionManager() throws Exception {
    this.logger.info("***** SourceDAoImpl.setTransactionManager Start ****");
    try {
      ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext();
      classPathXmlApplicationContext = new ClassPathXmlApplicationContext("/resources/bean.xml");
      this.logger.info("in settransactionManager");
      this.transactionManager = (PlatformTransactionManager)classPathXmlApplicationContext.getBean("transactionManager");
      this.logger.info(" settransactionManager completed");
      this.logger.info("***** SourceDAoImpl.setTransactionManager End ****");
      classPathXmlApplicationContext.close();
    } catch (Exception ex) {
      demo.logSQLException(ex, "SourceDAoImpl.setTransactionManager");
      this.logger.error(" error in SourceDAoImpl.setTransactionManager", new Exception("SourceDAoImpl.setTransactionManager", ex));
      throw ex;
    } 
  }
  
  public List<ConfigurationBean> getFileDetails() throws Exception {
    this.logger.info("***** SourceDAoImpl.getFileDetails Start ****");
    List<ConfigurationBean> filelist = null;
    try {
      String query = "SELECT filesrc.Fileid as inFileId ,filesrc.file_category as stCategory,filesrc.file_subcategory as stSubCategory, filesrc.FileName as stFileName ,filesrc.dataseparator ,filesrc.rddatafrm ,filesrc.charpatt, filesrc.Activeflag as activeFlag   FROM Main_FILESOURCE filesrc ";
      this.logger.info("query" + query);
      filelist = getJdbcTemplate().query(query, (RowMapper)new BeanPropertyRowMapper(ConfigurationBean.class));
      this.logger.info("***** SourceDAoImpl.getFileDetails End ****");
    } catch (Exception ex) {
      demo.logSQLException(ex, "SourceDAoImpl.getFileDetails");
      this.logger.error(" error in SourceDAoImpl.getFileDetails", new Exception("SourceDAoImpl.getFileDetails", ex));
      throw ex;
    } 
    return filelist;
  }
  
  public boolean updateFileDetails(ConfigurationBean ftpBean) throws Exception {
    this.logger.info("***** SourceDAoImpl.updateFileDetails Start ****");
    try {
      boolean result = false;
      int count = 0;
      String query = "";
      this.logger.info("logger.info(ftpBean)" + ftpBean);
      if (ftpBean.getInFileId() != 0) {
        this.logger.info("into the if condition");
        this.logger.info("flag" + ftpBean.getActiveFlag());
        count = getJdbcTemplate().update(
            "UPDATE Main_fileSource \tset FileName = ? ,dataseparator =? , ActiveFlag=? ,rddatafrm=?,charpatt=?   WHERE fileid = ?", new Object[] { ftpBean.getStFileName(), ftpBean.getDataSeparator(), ftpBean.getActiveFlag(), Integer.valueOf(ftpBean.getRdDataFrm()), ftpBean.getCharpatt(), Integer.valueOf(ftpBean.getInFileId()) });
        this.logger.info("count" + count);
        if (count > 0) {
          this.logger.info("Data updated successfully");
          result = true;
        } else {
          this.logger.info("Data not updated.");
          result = false;
        } 
      } 
      this.logger.info("***** SourceDAoImpl.updateFileDetails End ****");
      return result;
    } catch (Exception ex) {
      demo.logSQLException(ex, "SourceDAoImpl.updateFileDetails");
      this.logger.error(" error in SourceDAoImpl.updateFileDetails", new Exception("SourceDAoImpl.updateFileDetails", ex));
      return false;
    } 
  }
  
  public boolean addFileSource(ConfigurationBean configBean) throws Exception {
    this.logger.info("***** SourceDAoImpl.addFileSource Start ****");
    boolean result = false;
    String sql = "INSERT into MAIN_FILESOURCE (FILEID,FILENAME,TABLENAME,ACTIVEFLAG,DATASEPARATOR,RDDATAFRM,CHARPATT,FILE_CATEGORY,FILE_SUBCATEGORY,FILTERATION,KNOCKOFF) values(?,?,?,?,?,?,?,?,?,?,?)";
    setTransactionManager();
    DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus status = this.transactionManager.getTransaction((TransactionDefinition)defaultTransactionDefinition);
    String categ = configBean.getStCategory();
    this.logger.info(categ);
    String headers = null;
    try {
      String tablename = "";
      if (configBean.getPrev_table() != null && configBean.getPrev_tblFlag().equals("Y")) {
        tablename = configBean.getPrev_table();
      } else {
        tablename = String.valueOf(configBean.getStFileName()) + "_" + configBean.getStCategory() + "_RAWDATA";
        this.logger.info(tablename);
      } 
      this.logger.info(String.valueOf(sql) + configBean.getInFileId() + configBean.getStFileName() + configBean.getStFileName() + configBean.getActiveFlag() + configBean.getDataSeparator());
      int value = getJdbcTemplate().update(sql, new Object[] { 
            Integer.valueOf(configBean.getInFileId()), configBean.getStFileName(), tablename, 
            configBean.getActiveFlag(), configBean.getDataSeparator(), Integer.valueOf(configBean.getRdDataFrm()), configBean.getCharpatt(), configBean.getStCategory(), configBean.getStSubCategory(), 
            configBean.getClassify_flag(), 
            configBean.getKnock_offFlag() });
      if (value > 0) {
        if (configBean.getPrev_tblFlag().equals("N")) {
          String query = "create table " + configBean.getStFileName() + "_" + configBean.getStCategory() + "_RAWDATA" + " (";
          String parameter = "";
          String[] params = configBean.getStHeader().split(",");
          for (int i = 0; i < params.length; i++)
            parameter = String.valueOf(parameter) + params[i].toUpperCase() + " varchar2(500),"; 
          parameter = String.valueOf(parameter) + "PART_ID varchar2(2),";
          parameter = String.valueOf(parameter) + "DCRS_TRAN_NO number,";
          parameter = String.valueOf(parameter) + "NEXT_TRAN_DATE date,";
          parameter = String.valueOf(parameter) + " CreatedDate date Default sysdate,";
          parameter = String.valueOf(parameter) + " CreatedBy varchar2(500),";
          parameter = String.valueOf(parameter) + " FILEDATE date Default null";
          query = String.valueOf(query) + parameter + ")";
          this.logger.info(query);
          getJdbcTemplate().execute(query);
          String hdrquery = "INSERT into MAIN_FILEHEADERS (HEADERID , FILEID,Columnheader) values(((SELECT MAX(HEADERID) FROM  MAIN_FILEHEADERS)+1),?,?)";
          String header = configBean.getStHeader();
          value = getJdbcTemplate().update(hdrquery, new Object[] { Integer.valueOf(configBean.getInFileId()), header.replace(" ", "_") });
          if (value > 0) {
            this.logger.info("Headers data inserted");
            this.transactionManager.commit(status);
            result = true;
          } else {
            this.logger.info("Headers data not inserted");
            this.transactionManager.rollback(status);
            result = false;
          } 
        } else {
          headers = (String)getJdbcTemplate().queryForObject("(SELECT COLUMNHEADER from main_fileheaders where fileid = (select * from(select fileid from main_filesource where upper(tablename) ='" + configBean.getPrev_table().toUpperCase() + "' order by fileid ) where rownum =1))", String.class);
          this.logger.info(configBean.getPrev_table().toUpperCase());
          String query = "insert into MAIN_FILEHEADERS (HEADERID , FILEID,Columnheader) values(((SELECT MAX(HEADERID) FROM  MAIN_FILEHEADERS)+1)," + 
            configBean.getInFileId() + ",'" + headers + "')";
          int count = getJdbcTemplate().update(query);
          if (count > 0) {
            this.logger.info("Headers data inserted");
            this.transactionManager.commit(status);
            result = true;
          } else {
            this.logger.info("Headers data not inserted");
            this.transactionManager.rollback(status);
            result = false;
          } 
        } 
        this.logger.info("***** SourceDAoImpl.addFileSource End ****");
      } else {
        result = false;
      } 
    } catch (Exception ex) {
      demo.logSQLException(ex, "SourceDAoImpl.addFileSource");
      this.logger.error(" error in SourceDAoImpl.addFileSource", new Exception("SourceDAoImpl.addFileSource", ex));
      this.transactionManager.rollback(status);
      result = false;
    } 
    return result;
  }
  
  public int getFileId(ConfigurationBean configBean) {
    try {
      int i = ((Integer)getJdbcTemplate().queryForObject("SELECT MAX(FILEID) FROM MAIN_FILESOURCE", new Object[0], Integer.class)).intValue();
      return i;
    } catch (Exception e) {
      throw e;
    } 
  }
  
  public boolean chkTblExistOrNot(ConfigurationBean configBean) throws Exception {
    this.logger.info("***** SourceDAoImpl.chkTblExistOrNot Start ****");
    try {
      String filesql = "SELECT count(*) FROM Main_FileSource WHERE upper (Filename) ='" + configBean.getStFileName().toUpperCase() + "'" + 
        " AND upper(FILE_CATEGORY) ='" + configBean.getStCategory().toUpperCase() + "' AND upper(FILE_SUBCATEGORY)='" + configBean.getStSubCategory() + "' ";
      int filerowNum = 0;
      this.logger.info(filesql);
      filerowNum = ((Integer)getJdbcTemplate().queryForObject(filesql, Integer.class)).intValue();
      this.logger.info(filesql);
      int rowNum = 0;
      if (configBean.getPrev_tblFlag().equals("N")) {
        String sql = "SELECT count (*) FROM tab WHERE tname  = '" + configBean.getStFileName().toUpperCase() + "_" + configBean.getStCategory() + "_RAWDATA'";
        rowNum = ((Integer)getJdbcTemplate().queryForObject(sql, Integer.class)).intValue();
        this.logger.info(sql);
      } 
      this.logger.info("***** SourceDAoImpl.chkTblExistOrNot End ****");
      if (rowNum > 0 && filerowNum > 0)
        return false; 
      return true;
    } catch (Exception e) {
      demo.logSQLException(e, "SourceDAoImpl.chkTblExistOrNot");
      this.logger.error(" error in SourceDAoImpl.chkTblExistOrNot", new Exception("SourceDAoImpl.chkTblExistOrNot", e));
      return false;
    } 
  }
  
  public boolean addConfigParams(ConfigurationBean configBean) throws Exception {
	return false;}
  
  public void insertBatch(List<ConfigurationBean> comp_dtl_list, ConfigurationBean configurationBean) {}
  
  public void insertKnockOffBatch(List<ConfigurationBean> comp_dtl_list, ConfigurationBean configurationBean) throws Exception {
    this.logger.info("***** SourceDAoImpl.insertKnockOffBatch Start ****");
    String query = "SELECT CASE WHEN  (SELECT MAX(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL) is null then 0 else (SELECT MAX(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL) end as FLAG from dual";
    this.id = ((Integer)getJdbcTemplate().queryForObject(query, new Object[0], Integer.class)).intValue() + 1;
    this.logger.info("id" + this.id);
    setTransactionManager();
    DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus status = this.transactionManager.getTransaction((TransactionDefinition)defaultTransactionDefinition);
    try {
      insertMAIN_REVERSAL_DETAIL(comp_dtl_list, configurationBean);
      insertMAIN_REVERSAL_PARAMETERS(comp_dtl_list, configurationBean);
      insertMAIN_KNOCKOFF_CRITERIA(comp_dtl_list, configurationBean);
      this.transactionManager.commit(status);
    } catch (Exception ex) {
      this.transactionManager.rollback(status);
      demo.logSQLException(ex, "SourceDAoImpl.insertKnockOffBatch");
      this.logger.error(" error in SourceDAoImpl.insertKnockOffBatch", new Exception("SourceDAoImpl.insertKnockOffBatch", ex));
    } 
    this.logger.info("***** SourceDAoImpl.insertKnockOffBatch End ****");
  }
  
  public void insertMAIN_REVERSAL_DETAIL(List<ConfigurationBean> comp_dtl_list, ConfigurationBean configurationBean) {
    this.logger.info("***** SourceDAoImpl.insertMAIN_REVERSAL_DETAIL Start ****");
    for (ConfigurationBean configbean : comp_dtl_list) {
      this.logger.info("forconfigbean.getKnockoff_col()" + configbean.getKnockoff_col());
      if (configbean.getKnockoff_OrgVal() != null) {
        String category = (configurationBean.getStSubCategory() != "") ? (String.valueOf(configurationBean.getStCategory()) + "_" + configurationBean.getStSubCategory()) : configurationBean.getStCategory();
        this.logger.info(category);
        getJdbcTemplate().update("Insert into MAIN_REVERSAL_DETAIL (REVERSAL_ID,FILE_ID,CATEGORY,HEADER,VALUE,ENTRY_BY,ENTRY_DATE)values (" + 
            this.id + "," + configurationBean.getInFileId() + ",'" + category + "','" + configbean.getKnockoff_col() + "', '" + configbean.getKnockoff_OrgVal() + "','" + configurationBean.getStEntry_By() + "',sysdate)");
      } 
    } 
    this.logger.info("***** SourceDAoImpl.insertMAIN_REVERSAL_DETAIL End ****");
  }
  
  public void insertMAIN_REVERSAL_PARAMETERS(List<ConfigurationBean> comp_dtl_list, ConfigurationBean configurationBean) {
    this.logger.info("***** SourceDAoImpl.insertMAIN_REVERSAL_PARAMETERS Start ****");
    for (ConfigurationBean configbean : comp_dtl_list) {
      if (configbean.getKnockoff_comprVal() != null)
        getJdbcTemplate().update("Insert into MAIN_REVERSAL_PARAMETERS (REVERSAL_ID,FILE_ID,HEADER,VALUE,ENTRY_BY,ENTRY_DATE)values (" + this.id + ", " + configurationBean.getInFileId() + ",'" + configbean.getKnockoff_col() + "','" + configbean.getKnockoff_comprVal() + "','" + configurationBean.getStEntry_By() + "',sysdate )"); 
    } 
    this.logger.info("***** SourceDAoImpl.insertMAIN_REVERSAL_PARAMETERS End ****");
  }
  
  public void insertMAIN_KNOCKOFF_CRITERIA(List<ConfigurationBean> comp_dtl_list, ConfigurationBean configurationBean) {
    this.logger.info("***** SourceDAoImpl.insertMAIN_KNOCKOFF_CRITERIA Start ****");
    for (ConfigurationBean configbean : comp_dtl_list) {
      if (configbean.getKnockoff_header() != null)
        getJdbcTemplate().update("Insert into MAIN_KNOCKOFF_CRITERIA (REVERSAL_ID,FILE_ID,HEADER,ENTRY_BY,ENTRY_DATE,PADDING,START_CHARPOSITION,CHAR_SIZE,HEADER_VALUE,CONDITION)values (" + 
            
            this.id + ", " + configurationBean.getInFileId() + ",'" + configbean.getKnockoff_header() + "','" + 
            configurationBean.getStEntry_By() + "',sysdate,'" + configbean.getKnockoff_stPadding() + "','" + 
            configbean.getKnockoffStart_Char_Pos() + "','" + configbean.getKnockoffEnd_char_pos() + "','" + 
            configbean.getKnockoffSrch_Pattern() + "','" + configbean.getKnockoff_condition() + "')"); 
    } 
    this.logger.info("***** SourceDAoImpl.insertMAIN_KNOCKOFF_CRITERIA End ****");
  }
  
  public String getHeaderList(int fileId) {
    this.logger.info("***** SourceDAoImpl.getHeaderList Start ****");
    String sql = "SELECT columnheader FROM main_fileheaders WHERE fileid  = " + fileId;
    String hedrlist = null;
    this.logger.info("sql" + sql);
    hedrlist = (String)getJdbcTemplate().queryForObject(sql, String.class);
    this.logger.info(hedrlist);
    this.logger.info("***** SourceDAoImpl.getHeaderList End ****");
    return hedrlist;
  }
  
  public ArrayList<ConfigurationBean> getCompareDetails(int fileId, String category, String subcat) throws Exception {
    this.logger.info("***** SourceDAoImpl.getCompareDetails Start ****");
    subcat = (subcat == null) ? "" : subcat;
    ArrayList<ConfigurationBean> configurationBeans = null;
    try {
      String stcategory = !subcat.equals("-") ? (String.valueOf(category) + "_" + subcat) : category;
      String sql = "SELECT mcompdtl.File_header as stHeader ,mcompdtl.SEARCH_PATTERN as stSearch_Pattern ,  mcompdtl.PADDING as stPadding ,mcompdtl.START_CHARPOSITION as inStart_Char_Position, mcompdtl.END_CHARPOSITION as inEnd_char_position,  mcompm.category as stCategory ,mcompdtl.CONDITION as condition  FROM Main_Compare_detail mcompdtl INNER JOIN main_Compare_master mcompm ON mcompdtl.id = mcompm.ID WHERE mcompdtl.FILE_ID=" + 
        
        fileId + " and mcompm.category = '" + stcategory + "' ";
      this.logger.info(sql);
      this.logger.info("***** SourceDAoImpl.getCompareDetails End ****");
      configurationBeans = (ArrayList<ConfigurationBean>)getJdbcTemplate().query(sql, (RowMapper)new BeanPropertyRowMapper(ConfigurationBean.class));
    } catch (Exception ex) {
      demo.logSQLException(ex, "SourceDAoImpl.getCompareDetails");
      this.logger.error(" error in SourceDAoImpl.getCompareDetails", new Exception("SourceDAoImpl.getCompareDetails", ex));
      throw ex;
    } 
    return configurationBeans;
  }
  
  public List<ConfigurationBean> getknockoffDetails(int fileId, String category, String subcat) throws Exception {
    this.logger.info("***** SourceDAoImpl.getknockoffDetails Start ****");
    ArrayList<ConfigurationBean> configurationBeans = null;
    try {
      String query = "SELECT distinct mrevparam.Header as Knockoff_col,mrevdtl.VALUE as Knockoff_comprVal ,mrevparam.VALUE as Knockoff_OrgVal FROM MAIN_REVERSAL_DETAIL mrevdtl  INNER JOIN main_reversal_parameters mrevparam ON mrevdtl.REVERSAL_ID= mrevparam.REVERSAL_ID WHERE mrevparam.file_id=" + 
        
        fileId + " AND mrevdtl.CATEGORY ='" + category + "'";
      this.logger.info(query);
      configurationBeans = (ArrayList<ConfigurationBean>)getJdbcTemplate().query(query, (RowMapper)new BeanPropertyRowMapper(ConfigurationBean.class));
      this.logger.info("***** SourceDAoImpl.getknockoffDetails End ****");
    } catch (Exception ex) {
      demo.logSQLException(ex, "SourceDAoImpl.getknockoffDetails");
      this.logger.error(" error in SourceDAoImpl.getknockoffDetails", new Exception("SourceDAoImpl.getknockoffDetails", ex));
      throw ex;
    } 
    return configurationBeans;
  }
  
  public List<ConfigurationBean> getknockoffcrt(int fileId, String category, String subcat) throws Exception {
    ArrayList<ConfigurationBean> configurationBeans = null;
    this.logger.info("***** SourceDAoImpl.getknockoffcrt Start ****");
    subcat = (subcat == null) ? "" : subcat;
    try {
      String stCategory = !subcat.equals("-") ? (String.valueOf(category) + "_" + subcat) : category;
      String query = "SELECT distinct knckcrt.HEADER as knockoff_header,knckcrt.PADDING as knockoff_stPadding,knckcrt.START_CHARPOSITION as knockoffStart_Char_Pos,\t\tknckcrt.CHAR_SIZE as knockoffEnd_char_pos,knckcrt.HEADER_VALUE as knockoffSrch_Pattern ,knckcrt.CONDITION as knockoff_condition \t\t\t\t\tFROM Main_Knockoff_criteria knckcrt \t\tINNER JOIN MAIN_REVERSAL_DETAIL mrevdtl \t\tON knckcrt.REVERSAL_ID = mrevdtl.REVERSAL_ID \t\tWHERE knckcrt.FILE_ID=" + 
        
        fileId + " and mrevdtl.CATEGORY='" + stCategory + "'";
      this.logger.info(query);
      configurationBeans = (ArrayList<ConfigurationBean>)getJdbcTemplate().query(query, (RowMapper)new BeanPropertyRowMapper(ConfigurationBean.class));
      this.logger.info("***** SourceDAoImpl.getknockoffcrt End ****");
    } catch (Exception ex) {
      demo.logSQLException(ex, "SourceDAoImpl.getknockoffcrt");
      this.logger.error(" error in SourceDAoImpl.getknockoffcrt", new Exception("SourceDAoImpl.getknockoffcrt", ex));
      throw ex;
    } 
    return configurationBeans;
  }
  
  public List<CompareSetupBean> getFileList(String category, String subcat) throws Exception {
    this.logger.info("***** SourceDAoImpl.getFileList Start ****");
    subcat = (subcat == null) ? "" : subcat;
    try {
      List<CompareSetupBean> beans = null;
      String query = "Select fileid as inFileId, filename as stFileName  FROM MAIN_FILESOURCE  where FILE_CATEGORY='" + 
        category + "' ";
      if (subcat != null && subcat != "-")
        query = String.valueOf(query) + " AND FILE_SUBCATEGORY='" + subcat + "'"; 
      this.logger.info(query);
      beans = getJdbcTemplate().query(query, (RowMapper)new BeanPropertyRowMapper(CompareSetupBean.class));
      this.logger.info("***** SourceDAoImpl.getFileList End ****");
      return beans;
    } catch (Exception ex) {
      demo.logSQLException(ex, "SourceDAoImpl.getFileList");
      this.logger.error(" error in SourceDAoImpl.getFileList", new Exception("SourceDAoImpl.getFileList", ex));
      return null;
    } 
  }
  
  public List<String> gettable_list() {
    String query = "Select distinct TABLENAME from main_filesource ";
    return getJdbcTemplate().queryForList(query, String.class);
  }
  
  public List<CompareSetupBean> getFileTypeList(String category, String subcat, String table) throws Exception {
    this.logger.info("***** SourceDAoImpl.getFileTypeList Start ****");
    try {
      String query = "";
      List<CompareSetupBean> beans = null;
      if (!subcat.equals("-") && !category.equals("NFS")) {
        query = "select distinct dcrs_remarks as remarks from " + table + " WHERE DCRS_REMARKS NOT LIKE '%(%'";
      } else {
        query = "select   distinct dcrs_remarks as remarks   from( select case substr(dcrs_remarks,1,20) when   'MATCHED_UNSUCCESSFUL' then 'MATCHED_UNSUCCESSFUL'else dcrs_remarks  end as dcrs_remarks    from  " + 
          
          table + " " + 
          ")";
      } 
      this.logger.info(query);
      beans = getJdbcTemplate().query(query, (RowMapper)new BeanPropertyRowMapper(CompareSetupBean.class));
      this.logger.info("***** SourceDAoImpl.getFileTypeList End ****");
      return beans;
    } catch (Exception ex) {
      demo.logSQLException(ex, "SourceDAoImpl.getFileTypeList");
      this.logger.error(" error in SourceDAoImpl.getFileTypeList", new Exception("SourceDAoImpl.getFileTypeList", ex));
      return null;
    } 
  }
  
  public List<String> getSubcategories(String category) throws Exception {
    this.logger.info("***** SourceDAoImpl.getSubcategories Start ****");
    List<String> SubCategories = new ArrayList<>();
    try {
      String GET_SUBCATE = "SELECT DISTINCT FILE_SUBCATEGORY FROM  main_filesource WHERE FILE_CATEGORY = ? and FILE_SUBCATEGORY not like '%SUR%' and file_subcategory !='-' ";
      SubCategories = getJdbcTemplate().query(GET_SUBCATE, new Object[] {category},new RowMapper()
		{
			@Override
			public String mapRow(ResultSet rs, int rownum) throws SQLException 
			{	
					String subcategory = rs.getString("FILE_SUBCATEGORY");
					return subcategory;
				
			}
		}
				);
		logger.info("***** SourceDAoImpl.getSubcategories End ****");
		return SubCategories;
    } catch (Exception e) {
      demo.logSQLException(e, "SourceDAoImpl.getSubcategories");
      this.logger.error(" error in SourceDAoImpl.getSubcategories", new Exception("SourceDAoImpl.getSubcategories", e));
      return null;
    } 
  }
  
  public Settlement_FinalBean getReportDetails(SettlementBean settlementBean) {
	return null;}
  
  private Settlement_FinalBean setChargebackData(Settlement_FinalBean finalBean, String filedate) {
    float iss_charge_dtls_amt = ((Integer)getJdbcTemplate().queryForObject("select nvl(sum(to_number(TXN_AMOUNT)),0) - nvl(sum(to_number(ADJ_AMOUNT)),0)    from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy')='" + filedate + "' and   trim(upper(ADJ_TYPE)) in('CHARGEBACK')", Integer.class)).intValue();
    float iss_charge_dtls_cnt = ((Integer)getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy')='" + filedate + "' and     trim(upper(ADJ_TYPE)) in('CHARGEBACK') ", Integer.class)).intValue();
    float acq_charge_dtls_amt = ((Integer)getJdbcTemplate().queryForObject("select nvl(sum(to_number(TXN_AMOUNT)),0) from CASHNET_CHARGEBACK_ACQUIER where   to_date(filedate,'dd/mon/yyyy')='" + filedate + "' ", Integer.class)).intValue();
    float acq_charge_dtls_cnt = ((Integer)getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK_ACQUIER where  to_date(filedate,'dd/mon/yyyy')='" + filedate + "'  ", Integer.class)).intValue();
    float ISS_PENALTY = ((Integer)getJdbcTemplate().queryForObject("select sum(to_number(ADJCUSTPENALTY)) from CASHNET_CHARGEBACK where  to_date(filedate,'dd/mon/yyyy')='" + filedate + "'  ", Integer.class)).intValue();
    float ACQ_PENALTY = ((Integer)getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK_ACQUIER where  to_date(filedate,'dd/mon/yyyy') ='" + filedate + "'  ", Integer.class)).intValue();
    float credt_adj_amt = ((Integer)getJdbcTemplate().queryForObject("select nvl(sum(to_number(TXN_AMOUNT)),0) - nvl(sum(to_number(ADJ_AMOUNT)),0)  from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='" + filedate + "' and    ADJ_TYPE ='CREDIT' ", Integer.class)).intValue();
    float credt_adj_cnt = ((Integer)getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='" + filedate + "' and    ADJ_TYPE ='CREDIT'", Integer.class)).intValue();
    float Debit_adj_amt = ((Integer)getJdbcTemplate().queryForObject("select nvl(sum(to_number(ADJ_AMOUNT)),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='" + filedate + "' and    ADJ_TYPE ='DEBIT'", Integer.class)).intValue();
    float Debit_adj_cnt = ((Integer)getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='" + filedate + "' and    ADJ_TYPE ='DEBIT' ", Integer.class)).intValue();
    float PRE_ARBITRATION_amt = 0.0F;
    try {
      PRE_ARBITRATION_amt = ((Integer)getJdbcTemplate().queryForObject("select to_number(nvl(REPRSTMT_RECV_AMT,0)) - to_number(nvl(PRE_ARB_ADJ_RECV_AMT,0)) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy') ='" + filedate + "'  and ADJ_TYPE = 'PRE-ARBITRATION'  ", Integer.class)).intValue();
    } catch (Exception exception) {}
    float PRE_ARBITRATION_cnt = ((Integer)getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy') ='" + filedate + "' and  ADJ_TYPE ='PRE-ARBITRATION'  ", Integer.class)).intValue();
    float REPRESENTMENT_amt = ((Integer)getJdbcTemplate().queryForObject("select nvl(sum(to_number(ADJ_AMOUNT)),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy') ='" + filedate + "' and    ADJ_TYPE ='REPRESENTMENT'", Integer.class)).intValue();
    float REPRESENTMENT_cnt = ((Integer)getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy') ='" + filedate + "' and   ADJ_TYPE ='REPRESENTMENT' ", Integer.class)).intValue();
    float PRE_ARBITRATION_DEC_amt = ((Integer)getJdbcTemplate().queryForObject("select nvl(sum(to_number(ADJ_AMOUNT)),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy') ='" + filedate + "' and  ADJ_TYPE = 'PreArbitration Reject'", Integer.class)).intValue();
    float PRE_ARBITRATION_DEC_cnt = ((Integer)getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy') ='" + filedate + "' and  ADJ_TYPE = 'PreArbitration Reject'  ", Integer.class)).intValue();
    float Acq_Pre_arb_Details_Amt = ((Integer)getJdbcTemplate().queryForObject("select nvl(sum(to_number(ADJ_AMOUNT)),0) from CASHNET_CHARGEBACK_ACQUIER where NETWORK='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='" + filedate + "' and   ADJ_TYPE = 'PRE-ARBITRATION'  ", Integer.class)).intValue();
    float Acq_Pre_arb_Details_Cnt = ((Integer)getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK_ACQUIER where NETWORK='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='" + filedate + "' and     ADJ_TYPE = 'PRE-ARBITRATION' ", Integer.class)).intValue();
    float Acq_represnment_Amt = ((Integer)getJdbcTemplate().queryForObject("select nvl(sum(to_number(ADJ_AMOUNT)),0) from CASHNET_CHARGEBACK_ACQUIER where NETWORK='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='" + filedate + "' and   ADJ_TYPE = 'REPRESENTMENT'  ", Integer.class)).intValue();
    float Acq_represnment_Cnt = ((Integer)getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK_ACQUIER where NETWORK='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='" + filedate + "' and     ADJ_TYPE = 'REPRESENTMENT' ", Integer.class)).intValue();
    float Acq_CHARGEBACK_Amt = ((Integer)getJdbcTemplate().queryForObject("select nvl(sum(to_number(ADJ_AMOUNT)),0) from CASHNET_CHARGEBACK_ACQUIER where NETWORK='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='" + filedate + "' and   ADJ_TYPE = 'CHARGEBACK'  ", Integer.class)).intValue();
    float Acq_CHARGEBACK_Cnt = ((Integer)getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK_ACQUIER where NETWORK='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='" + filedate + "' and     ADJ_TYPE = 'CHARGEBACK' ", Integer.class)).intValue();
    finalBean.setAcq_Repr_Dtls_Amt(String.valueOf(Acq_represnment_Amt));
    finalBean.setAcq_Repr_Dtls_Cnt(String.valueOf(Acq_represnment_Cnt));
    finalBean.setAcq_Charg_Dtls_Amt(String.valueOf(Acq_CHARGEBACK_Amt));
    finalBean.setAcq_Charg_Dtls_Cnt(String.valueOf(Acq_CHARGEBACK_Cnt));
    finalBean.setAcq_Pre_Arb_Dec_Dtls_Amt(String.valueOf(Acq_Pre_arb_Details_Amt));
    finalBean.setAcq_Pre_Arb_Dec_Dtls_Cnt(String.valueOf(Acq_Pre_arb_Details_Cnt));
    finalBean.setIss_Pre_Arb_Dec_Dtls_Amt(String.valueOf(PRE_ARBITRATION_DEC_amt));
    finalBean.setIss_Pre_Arb_Dec_Dtls_Cnt(String.valueOf(PRE_ARBITRATION_DEC_cnt));
    finalBean.setAcq_Charg_Dtls_Amt(String.valueOf(acq_charge_dtls_amt));
    finalBean.setAcq_Charg_Dtls_Cnt(String.valueOf(acq_charge_dtls_cnt));
    finalBean.setIss_Charge_Dtls_Cnt(String.valueOf(iss_charge_dtls_cnt));
    finalBean.setIss_Charge_Dtls_Amt(String.valueOf(iss_charge_dtls_amt));
    finalBean.setIss_Cre_Adj_Dtls_Amt(String.valueOf(credt_adj_amt));
    finalBean.setIss_Cre_Adj_Dtls_Cnt(String.valueOf(credt_adj_cnt));
    finalBean.setIss_Pre_arb_Dtls_Amt(String.valueOf(PRE_ARBITRATION_amt));
    finalBean.setIss_Pre_arb_Dtls_Cnt(String.valueOf(PRE_ARBITRATION_cnt));
    finalBean.setIss_Repr_Dtls_Amt(String.valueOf(REPRESENTMENT_amt));
    finalBean.setIss_Repr_Dtls_Cnt(String.valueOf(REPRESENTMENT_cnt));
    finalBean.setIss_Debit_Adj_Dtls_Amt(String.valueOf(Debit_adj_amt));
    finalBean.setIss_Debit_Adj_Dtls_Cnt(String.valueOf(Debit_adj_cnt));
    finalBean.setIss_Repr_Dtls_Amt(String.valueOf(REPRESENTMENT_amt));
    finalBean.setIss_Repr_Dtls_Cnt(String.valueOf(REPRESENTMENT_cnt));
    finalBean.setIss_penalty(String.valueOf(ISS_PENALTY));
    finalBean.setAcq_penalty(String.valueOf(ACQ_PENALTY));
    return finalBean;
  }
  
  public Settlement_FinalBean setData(ArrayList<Settle_final_report_condn_bean> condn_beans, String filedate) {
    Settlement_FinalBean finalBean = new Settlement_FinalBean();
    try {
      String count_query = "";
      String Amount_query = "";
      String count = "";
      String Amount = "";
      for (Settle_final_report_condn_bean condn_bean : condn_beans) {
        int wdl_fee_amt;
        Integer wdl_cbk_amt, wdl_chargeamout, cbk_amount, total_amout;
        count = "";
        Amount = "";
        if (condn_bean.getReport_col().contains("Iss")) {
          count_query = "select Count(*) from cashnet_cashnet_iss_rawdata where filedate='" + filedate + "' " + 
            " and transaction_type IN ( SELECT ttypecode FROM atm_interchange_fee " + 
            " WHERE networktype = '" + condn_bean.getNetworktype() + "' AND ttype = '" + condn_bean.getTTYPE() + "')" + 
            " AND response_code in (SELECT ttypecode from ATM_INTERCHANGE_FEE where NETWORKTYPE ='" + condn_bean.getRE_Networktype() + "'  and ttype like '%" + condn_bean.getRE_TTYPE() + "')" + 
            " AND from_account_type in ('00','01','02') ";
          System.out.println("count_query" + count_query);
          count = (String)getJdbcTemplate().queryForObject(count_query, String.class);
          Amount_query = "select nvl(sum(to_number(TRANSACTION_AMOUNT)),0) from cashnet_cashnet_iss_rawdata where filedate='" + filedate + "' " + 
            " and transaction_type IN ( SELECT ttypecode FROM atm_interchange_fee " + 
            " WHERE networktype = '" + condn_bean.getNetworktype() + "' AND ttype = '" + condn_bean.getTTYPE() + "')" + 
            " AND response_code in (SELECT ttypecode from ATM_INTERCHANGE_FEE where NETWORKTYPE ='" + condn_bean.getRE_Networktype() + "'  and ttype like '%" + condn_bean.getRE_TTYPE() + "')" + 
            " AND from_account_type in ('00','01','02') ";
          System.out.println(String.valueOf(Amount_query) + "Amount_query");
          Amount = (String)getJdbcTemplate().queryForObject(Amount_query, String.class);
        } else {
          if (condn_bean.getReport_col().contains("PC_")) {
            count_query = "select Count(*) from cashnet_cashnet_ACQ_rawdata where filedate='" + filedate + "' " + 
              " and transaction_type IN ( SELECT ttypecode FROM atm_interchange_fee " + 
              " WHERE networktype = '" + condn_bean.getNetworktype() + "' AND ttype = '" + condn_bean.getTTYPE() + "')" + 
              " AND response_code in (SELECT ttypecode from ATM_INTERCHANGE_FEE where NETWORKTYPE ='" + condn_bean.getRE_Networktype() + "'  and ttype like '%" + condn_bean.getRE_TTYPE() + "') ";
            System.out.println("count_query" + count_query);
          } else {
            count_query = "select Count(*) from cashnet_cashnet_ACQ_rawdata where filedate='" + filedate + "' " + 
              " and transaction_type IN ( SELECT ttypecode FROM atm_interchange_fee " + 
              " WHERE networktype = '" + condn_bean.getNetworktype() + "' AND ttype = '" + condn_bean.getTTYPE() + "')" + 
              " AND response_code in (SELECT ttypecode from ATM_INTERCHANGE_FEE where NETWORKTYPE ='" + condn_bean.getRE_Networktype() + "'  and ttype like '%" + condn_bean.getRE_TTYPE() + "')" + 
              " AND from_account_type in (" + condn_bean.getAccount_type() + ") ";
            System.out.println("count_query" + count_query);
          } 
          count = (String)getJdbcTemplate().queryForObject(count_query, String.class);
          Amount_query = "select nvl(sum(to_number(TXN_AMOUNT)),0) from cashnet_cashnet_ACQ_rawdata where filedate='" + filedate + "' " + 
            " and transaction_type IN ( SELECT ttypecode FROM atm_interchange_fee " + 
            " WHERE networktype = '" + condn_bean.getNetworktype() + "' AND ttype = '" + condn_bean.getTTYPE() + "')" + 
            " AND response_code in (SELECT ttypecode from ATM_INTERCHANGE_FEE where NETWORKTYPE ='" + condn_bean.getRE_Networktype() + "'  and ttype like '%" + condn_bean.getRE_TTYPE() + "')" + 
            " AND from_account_type in (" + condn_bean.getAccount_type() + ") ";
          System.out.println(String.valueOf(Amount_query) + "Amount_query");
          Amount = (String)getJdbcTemplate().queryForObject(Amount_query, String.class);
        } 
        String str;
        switch ((str = condn_bean.getReport_col()).hashCode()) {
          case -1904856628:
            if (!str.equals("CCard_Acq_BI_App_Fee"))
              continue; 
            System.out.println("CCard_Acq_BI_App_Fee" + String.valueOf(Integer.parseInt(count) * 5) + count);
            finalBean.setCCard_Acq_BI_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setCCard_Acq_BI_App_Fee_Cnt(count);
          case -1886973915:
            if (!str.equals("Iss_MS_App_Fee"))
              continue; 
            finalBean.setIss_MS_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setIss_MS_App_Fee_Cnt(count);
          case -1733057695:
            if (!str.equals("Acq_MS_Appr_Fee"))
              continue; 
            finalBean.setAcq_MS_Appr_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setAcq_MS_Appr_Fee_Cnt(count);
          case -1010787365:
            if (!str.equals("Acq_WDL_Dec_App_Fee"))
              continue; 
            cbk_amount = (Integer)getJdbcTemplate().queryForObject("select nvl(sum(ACQ_FEE),0) from CASHNET_CHARGEBACK_ACQUIER where to_date(FILEDATE,'dd/mon/yyyy') = '" + filedate + "' ", Integer.class);
            total_amout = Integer.valueOf(Integer.parseInt(count) * 5 + cbk_amount.intValue());
            finalBean.setAcq_WDL_Dec_App_Fee_Amt(String.valueOf(total_amout));
            finalBean.setAcq_WDL_Dec_App_Fee_Cnt(count);
          case -899215519:
            if (!str.equals("Acq_BI_Dec_Fee"))
              continue; 
            finalBean.setAcq_BI_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setAcq_BI_Dec_App_Fee_Cnt(count);
          case -670339307:
            if (!str.equals("Iss_PC_Dec_App_Fee"))
              continue; 
            finalBean.setIss_PC_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setIss_PC_Dec_App_Fee_Cnt(count);
          case -464883601:
            if (!str.equals("CCard_Acq_BI_Dec_App_Fee"))
              continue; 
            finalBean.setCCard_Acq_BI_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setCCard_Acq_BI_Dec_App_Fee_Cnt(count);
          case -440745393:
            if (!str.equals("Acq_PC_Dec_App_Fee"))
              continue; 
            finalBean.setAcq_PC_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setAcq_PC_Dec_App_Fee_Cnt(count);
          case -225337527:
            if (!str.equals("Cash_Sha_Net_Iss_Sett"))
              continue; 
            finalBean.setCash_Sha_Net_Iss_Sett_Amt(Amount);
            finalBean.setCash_Sha_Net_Iss_Sett_Cnt(count);
          case 352096105:
            if (!str.equals("Iss_BI_Dec_App_Fee"))
              continue; 
            finalBean.setIss_BI_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setIss_BI_Dec_App_Fee_Cnt(count);
          case 446855752:
            if (!str.equals("Iss_MS_Dec_App_Fee"))
              continue; 
            finalBean.setIss_MS_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setIss_MS_Dec_App_Fee_Cnt(count);
          case 461735893:
            if (!str.equals("Iss_WDL_Dec_App_Fee"))
              continue; 
            wdl_fee_amt = Integer.parseInt(count) * 5;
            wdl_cbk_amt = (Integer)getJdbcTemplate().queryForObject("select nvl(sum(ISS_FEE),0) from  CASHNET_CHARGEBACK where to_date(FIlEdate,'dd/mon/yyyy')='" + filedate + "'", Integer.class);
            wdl_chargeamout = Integer.valueOf(wdl_cbk_amt.intValue() + wdl_fee_amt);
            finalBean.setIss_WDL_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(wdl_chargeamout))));
            System.out.println("Iss_WDL_Dec_App_Fee" + count);
            finalBean.setIss_WDL_Dec_App_Fee_Cnt(count);
          case 563770284:
            if (!str.equals("Acq_PC_App_Fee"))
              continue; 
            finalBean.setAcq_PC_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setAcq_PC_App_Fee_Cnt(count);
          case 665327852:
            if (!str.equals("CCard_Acq_WDL_App_Fee"))
              continue; 
            finalBean.setCCard_Acq_WDL_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 30))));
            finalBean.setCCard_Acq_WDL_App_Fee_Cnt(count);
          case 672953202:
            if (!str.equals("Iss_PC_App_Fee"))
              continue; 
            finalBean.setIss_PC_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setIss_PC_App_Fee_Cnt(count);
          case 676449666:
            if (!str.equals("Acq_MS_Dec_App_Fee"))
              continue; 
            finalBean.setAcq_MS_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setAcq_MS_Dec_App_Fee_Cnt(count);
          case 970408316:
            if (!str.equals("CC_Acq_WDL_Dec_App_Fee"))
              continue; 
            finalBean.setCC_Acq_WDL_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setCC_Acq_WDL_Dec_App_Fee_Cnt(count);
          case 1060167168:
            if (!str.equals("Acq_BI_App_Fee"))
              continue; 
            finalBean.setAcq_BI_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count) * 5))));
            finalBean.setAcq_BI_App_Fee_Cnt(count);
          case 1169350086:
            if (!str.equals("Iss_BI_App_Fee"))
              continue; 
            finalBean.setIss_BI_App_Fee_Amt(String.valueOf(Integer.parseInt(count) * 5));
            finalBean.setIss_BI_App_Fee_Cnt(count);
        } 
      } 
      count_query = " select count(*) from cashnet_cashnet_acq_rawdata where filedate='" + filedate + "' and response_code in ('00','26') " + 
        "and to_number(ACQ_SETTLE_AMNT) >0 " + 
        "and  (TRANSACTION_TYPE ='61' OR TRANSACTION_TYPE ='89' OR  TRANSACTION_TYPE ='29' OR TRANSACTION_TYPE ='04' OR TRANSACTION_TYPE ='88')";
      String Cash_Sha_Net_Acq_Sett_Cnt = (String)getJdbcTemplate().queryForObject(count_query, String.class);
      Amount_query = "select sum(to_number(TXN_AMOUNT)) from cashnet_cashnet_acq_rawdata where filedate='" + filedate + "' and response_code in ('00','26') " + 
        "and to_number(ACQ_SETTLE_AMNT) >0 " + 
        "and  (TRANSACTION_TYPE ='61' OR TRANSACTION_TYPE ='89' OR  TRANSACTION_TYPE ='29' OR TRANSACTION_TYPE ='04' OR TRANSACTION_TYPE ='88')";
      String Cash_Sha_Net_Acq_Sett_amt = (String)getJdbcTemplate().queryForObject(Amount_query, String.class);
      int Fee_amount = Integer.parseInt(finalBean.getIss_BI_App_Fee_Cnt()) + Integer.parseInt(finalBean.getIss_BI_Dec_App_Fee_Cnt()) + 
        Integer.parseInt(finalBean.getIss_MS_App_Fee_Cnt()) + Integer.parseInt(finalBean.getIss_MS_Dec_App_Fee_Cnt()) + Integer.parseInt(finalBean.getIss_PC_App_Fee_Cnt()) + 
        Integer.parseInt(finalBean.getIss_PC_Dec_App_Fee_Cnt()) + Integer.parseInt(finalBean.getIss_WDL_Dec_App_Fee_Cnt());
      System.out.println("Fee_amount" + Fee_amount);
      int Fee_cnt = Integer.parseInt(finalBean.getIss_BI_App_Fee_Cnt()) + Integer.parseInt(finalBean.getIss_BI_Dec_App_Fee_Cnt()) + 
        Integer.parseInt(finalBean.getIss_MS_App_Fee_Cnt()) + Integer.parseInt(finalBean.getIss_PC_App_Fee_Cnt()) + 
        Integer.parseInt(finalBean.getIss_PC_Dec_App_Fee_Cnt()) + Integer.parseInt(finalBean.getIss_WDL_Dec_App_Fee_Cnt());
      int Cash_Sha_net_Swt_Exp_cnt = Integer.parseInt(finalBean.getCash_Sha_Net_Iss_Sett_Cnt()) - Integer.parseInt(finalBean.getAcq_Pre_arb_Details_Cnt()) - Integer.parseInt(finalBean.getIss_Pre_Arb_Dec_Dtls_Cnt()) + 
        Integer.parseInt(finalBean.getAcq_Charg_Dtls_Cnt()) + Integer.parseInt(finalBean.getIss_Repr_Dtls_Cnt());
      finalBean.setIss_WDL_Approved_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Cash_Sha_net_Swt_Exp_cnt * 15))));
      finalBean.setIss_WDL_Approved_Fee_Cnt(String.valueOf(Cash_Sha_net_Swt_Exp_cnt));
      System.out.println("Cash_Sha_net_Swt_Exp_cnt" + Cash_Sha_net_Swt_Exp_cnt);
      Integer Acq_total_All_cnt = (Integer)getJdbcTemplate().queryForObject("select count(*) from  cashnet_cashnet_acq_rawdata where filedate='" + filedate + "'and (TRANSACTION_TYPE ='61' OR TRANSACTION_TYPE ='89' OR  TRANSACTION_TYPE ='29' OR TRANSACTION_TYPE ='04' OR TRANSACTION_TYPE ='88')" + 
          "AND RESPONSE_CODE in('00')  AND TXN_AMOUNT>0 ", Integer.class);
      Integer acq_wdl_cnt = Integer.valueOf(Acq_total_All_cnt.intValue() - Integer.parseInt(finalBean.getCCard_Acq_WDL_App_Fee_Cnt()));
      finalBean.setAcq_WDL_Appr_Fee_Cnt(String.valueOf(acq_wdl_cnt));
      finalBean.setAcq_WDL_Appr_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(acq_wdl_cnt.intValue() * 15))));
      Integer cbk_amt = (Integer)getJdbcTemplate().queryForObject("select nvl(sum(ISS_FEE_SW),0) from  CASHNET_CHARGEBACK where to_date(FILEDATE,'dd/mon/yyyy')='" + filedate + "'", Integer.class);
      System.out.println(cbk_amt);
      int chargeamout = cbk_amt.intValue() + Fee_amount + Cash_Sha_net_Swt_Exp_cnt;
      System.out.println(chargeamout);
      finalBean.setCash_Sha_net_Swt_Exp_Amt(String.valueOf(Float.parseFloat(String.valueOf(chargeamout))));
      finalBean.setCash_Sha_net_Swt_Exp_Cnt(String.valueOf(chargeamout));
      finalBean.setCash_Sha_Net_Acq_Sett_Cnt(Cash_Sha_Net_Acq_Sett_Cnt);
      finalBean.setCash_Sha_Net_Acq_Sett_Amt(String.valueOf(Float.parseFloat(Cash_Sha_Net_Acq_Sett_amt)));
      Float cash_sha_net_acq_sett_cnt = Float.valueOf(Float.parseFloat(finalBean.getAcq_BI_App_Fee_Cnt()) + Float.parseFloat(finalBean.getAcq_BI_Dec_App_Fee_Cnt()) + 
          Float.parseFloat(finalBean.getAcq_MS_Appr_Fee_Cnt()) + Float.parseFloat(finalBean.getAcq_MS_Dec_App_Fee_Cnt()) + 
          Float.parseFloat(finalBean.getAcq_PC_App_Fee_Cnt()) + Float.parseFloat(finalBean.getAcq_PC_Dec_App_Fee_Cnt()) + 
          Float.parseFloat(finalBean.getCCard_Acq_WDL_App_Fee_Cnt()) + Float.parseFloat(finalBean.getAcq_WDL_Dec_App_Fee_Cnt()) + 
          Float.parseFloat(finalBean.getCC_Acq_WDL_Dec_App_Fee_Cnt()) + Float.parseFloat(finalBean.getCCard_Acq_BI_Dec_App_Fee_Cnt()) + 
          Float.parseFloat(finalBean.getCCard_Acq_BI_App_Fee_Cnt()));
      Float cash_sha_net_acq_sett_amt = Float.valueOf(Float.parseFloat(finalBean.getAcq_BI_App_Fee_Amt()) + Float.parseFloat(finalBean.getAcq_BI_Dec_App_Fee_Amt()) + 
          Float.parseFloat(finalBean.getAcq_MS_Appr_Fee_Amt()) + Float.parseFloat(finalBean.getAcq_MS_Dec_App_Fee_Amt()) + 
          Float.parseFloat(finalBean.getAcq_PC_App_Fee_Amt()) + Float.parseFloat(finalBean.getAcq_PC_Dec_App_Fee_Amt()) + 
          Float.parseFloat(finalBean.getCCard_Acq_WDL_App_Fee_Amt()) + Float.parseFloat(finalBean.getAcq_WDL_Dec_App_Fee_Amt()) + 
          Float.parseFloat(finalBean.getCC_Acq_WDL_Dec_App_Fee_Amt()) + Float.parseFloat(finalBean.getCCard_Acq_BI_Dec_App_Fee_Amt()) + 
          Float.parseFloat(finalBean.getCCard_Acq_BI_App_Fee_Amt()));
      finalBean.setCash_Sha_Network_Serv_Tax_Cnt(String.valueOf(cash_sha_net_acq_sett_cnt.floatValue() + Integer.parseInt(finalBean.getAcq_WDL_Appr_Fee_Cnt())));
      System.out.println();
      Float sha_amt = Float.valueOf((float)((cash_sha_net_acq_sett_amt.floatValue() + Float.parseFloat(finalBean.getAcq_WDL_Appr_Fee_Amt())) * 0.18D));
      finalBean.setCash_Sha_Network_Serv_Tax_Amt(String.valueOf(sha_amt));
      finalBean.setTDS_on_Iss_Swtch_Exp_Cnt(finalBean.getCash_Sha_net_Swt_Exp_Cnt());
      double Exp_amt = Float.parseFloat(finalBean.getCash_Sha_net_Swt_Exp_Cnt()) * 0.015D;
      System.out.println("TDS_amount" + Exp_amt);
      finalBean.setTDS_on_Iss_Swtch_Exp_Amt(String.format("%.2f", new Object[] { Double.valueOf(Exp_amt) }));
      finalBean.setCash_Sha_net_Swt_Exp_Amt(String.valueOf(Float.parseFloat(finalBean.getCash_Sha_net_Swt_Exp_Cnt()) * 1.0F));
      float Gst_amt = Float.parseFloat(finalBean.getIss_BI_App_Fee_Amt()) + Float.parseFloat(finalBean.getIss_BI_Dec_App_Fee_Amt()) + 
        Float.parseFloat(finalBean.getIss_MS_App_Fee_Amt()) + Float.parseFloat(finalBean.getIss_MS_Dec_App_Fee_Amt()) + Float.parseFloat(finalBean.getIss_PC_App_Fee_Amt()) + 
        Float.parseFloat(finalBean.getIss_PC_Dec_App_Fee_Amt()) + Float.parseFloat(finalBean.getIss_WDL_Approved_Fee_Amt()) + Float.parseFloat(finalBean.getIss_WDL_Dec_App_Fee_Amt()) + 
        Float.parseFloat(finalBean.getCash_Sha_net_Swt_Exp_Amt());
      System.out.println("Gst_amt" + Gst_amt);
      finalBean.setCash_ATM_inter_Paid_GST_Amt(String.valueOf(Float.parseFloat(String.valueOf(Gst_amt * 0.18D))));
      finalBean.setCash_ATM_inter_Paid_GST_Cnt(String.valueOf(Float.parseFloat(finalBean.getIss_BI_App_Fee_Cnt()) + Float.parseFloat(finalBean.getIss_BI_Dec_App_Fee_Cnt()) + 
            Float.parseFloat(finalBean.getIss_MS_App_Fee_Cnt()) + Float.parseFloat(finalBean.getIss_MS_Dec_App_Fee_Cnt()) + Float.parseFloat(finalBean.getIss_PC_App_Fee_Cnt()) + 
            Float.parseFloat(finalBean.getIss_PC_Dec_App_Fee_Cnt()) + Float.parseFloat(finalBean.getIss_WDL_Approved_Fee_Cnt()) + Float.parseFloat(finalBean.getIss_WDL_Dec_App_Fee_Cnt()) + 
            Float.parseFloat(finalBean.getCash_Sha_net_Swt_Exp_Cnt())));
      return finalBean;
    } catch (Exception ex) {
      ex.printStackTrace();
      return finalBean;
    } 
  }
  
  public List<GenerateSettleTTUMBean> generateSettlTTUM(SettlementBean settlementBean) {
    List<GenerateSettleTTUMBean> beanlist = new ArrayList<>();
    GenerateSettleTTUMBean bean = new GenerateSettleTTUMBean();
    String msg = null;
    try {
      msg = settlementTTUM(settlementBean.getDatepicker(), settlementBean.getUser_id());
    } catch (Exception ex) {
      ex.printStackTrace();
    } 
    if (msg != null) {
      beanlist = getTTUMdata(settlementBean);
    } else if (msg.equals("TTUM Already Generated")) {
      beanlist = getTTUMdata(settlementBean);
    } 
    return beanlist;
  }
  
  private List<GenerateSettleTTUMBean> getTTUMdata(SettlementBean settlementBean) {
    List<GenerateSettleTTUMBean> beanlist = new ArrayList<>();
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try {
      String GET_DATA = " select ACCOUNT_NUMBER,CURRENCY_CODE_OF_ACC_NO,SERVICE_OUTLET,PART_TRAN_TYPE,TRANSACTION_AMOUNT,TRANSACTION_PARTICULAR,  REMARKS,REFERENCE_CURRENCY_CODE,REFERENCE_AMOUNT,ACCOUNT_REPORT_CODE   from CASHNET_STTLEMENT_TTUM where filedate='" + 
        
        settlementBean.getDatepicker() + "'";
      conn = getConnection();
      pstmt = conn.prepareStatement(GET_DATA);
      rset = pstmt.executeQuery();
      System.out.println(GET_DATA);
      while (rset.next()) {
        GenerateSettleTTUMBean bean = new GenerateSettleTTUMBean();
        bean.setACCOUNT_NUMBER(rset.getString("ACCOUNT_NUMBER"));
        bean.setCURRENCY_CODE_OF_ACC_NO(rset.getString("CURRENCY_CODE_OF_ACC_NO"));
        bean.setSERVICE_OUTLET(rset.getString("SERVICE_OUTLET"));
        bean.setPART_TRAN_TYPE(rset.getString("PART_TRAN_TYPE"));
        bean.setTRANSACTION_AMOUNT((rset.getString("TRANSACTION_AMOUNT") != null) ? rset.getString("TRANSACTION_AMOUNT") : "0");
        bean.setTRANSACTION_PARTICULAR(rset.getString("TRANSACTION_PARTICULAR"));
        bean.setREMARKS(rset.getString("REMARKS"));
        bean.setREFERENCE_CURRENCY_CODE(rset.getString("REFERENCE_CURRENCY_CODE"));
        bean.setREFERENCE_AMOUNT((rset.getString("REFERENCE_AMOUNT") != null) ? rset.getString("REFERENCE_AMOUNT") : "0");
        bean.setACCOUNT_REPORT_CODE(rset.getString("ACCOUNT_REPORT_CODE"));
        beanlist.add(bean);
      } 
    } catch (Exception ex) {
      ex.printStackTrace();
    } 
    return beanlist;
  }
  
  public String settlementTTUM(String filedate, String entry_by) throws ParseException, Exception {
	return entry_by;}
}
