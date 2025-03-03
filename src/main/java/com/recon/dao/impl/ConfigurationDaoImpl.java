package com.recon.dao.impl;


import com.recon.dao.ConfigurationDao;
import com.recon.model.ConfigurationBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Component
public class ConfigurationDaoImpl extends JdbcDaoSupport implements ConfigurationDao {
  public final String FILE_ID = "file_id";
  
  public final String CATEGORY = "category";
  
  public final String FILE_NAME = "file_name";
  
  public final String ENTRY_BY = "Entry_By";
  
  public final String ID = "i_id";
  
  public final String TABLE_NAME = "table_name";
  
  private static final String O_ERROR_CODE = "o_error_code";
  
  private static final String O_ERROR_MESSAGE = "o_error_message";
  
  private PlatformTransactionManager transactionManager;
  
  private int id;
  
  public void setTransactionManager() {
    try {
      ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext();
      classPathXmlApplicationContext = new ClassPathXmlApplicationContext("/resources/bean.xml");
      System.out.println("in settransactionManager");
      this.transactionManager = (PlatformTransactionManager)classPathXmlApplicationContext.getBean("transactionManager");
      System.out.println(" settransactionManager completed");
      classPathXmlApplicationContext.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    } 
  }
  
  public int getFileId(ConfigurationBean configBean) throws Exception {
    try {
      int i = ((Integer)getJdbcTemplate().queryForObject("SELECT MAX(FILEID) FROM MAIN_FILESOURCE", new Object[0], Integer.class)).intValue();
      return i;
    } catch (Exception e) {
      throw e;
    } 
  }
  
  public void addConfigParams(ConfigurationBean configBean) throws Exception {}
  
  public void insertBatch(List<ConfigurationBean> comp_dtl_list, ConfigurationBean configurationBean) {}
  
  public boolean addFileSource(ConfigurationBean configBean) throws Exception {
    boolean result = false;
    String sql = "INSERT into MAIN_FILESOURCE (FILEID,FILENAME,TABLENAME,ACTIVEFLAG,DATASEPARATOR) values(?,?,?,?,?)";
    setTransactionManager();
    DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus status = this.transactionManager.getTransaction((TransactionDefinition)defaultTransactionDefinition);
    try {
      System.out.println(String.valueOf(sql) + configBean.getInFileId() + configBean.getStFileName() + configBean.getStFileName() + configBean.getActiveFlag() + configBean.getDataSeparator());
      int value = getJdbcTemplate().update(sql, new Object[] { Integer.valueOf(configBean.getInFileId()), configBean.getStFileName(), configBean.getStFileName(), configBean.getActiveFlag(), configBean.getDataSeparator() });
      if (value > 0) {
        String query = "create table " + configBean.getStFileName() + " (";
        String parameter = "";
        String[] params = configBean.getStHeader().split(",");
        for (int i = 0; i < params.length; i++)
          parameter = String.valueOf(parameter) + params[i].toUpperCase() + " varchar2(500),"; 
        parameter = String.valueOf(parameter) + " CreatedDate date Default sysdate,";
        parameter = String.valueOf(parameter) + " CreatedBy varchar2(500)";
        query = String.valueOf(query) + parameter + ")";
        System.out.println(query);
        getJdbcTemplate().execute(query);
        String hdrquery = "INSERT into MAIN_FILEHEADERS (HEADERID , FILEID,Columnheader) values(((SELECT MAX(HEADERID) FROM  MAIN_FILEHEADERS)+1),?,?)";
        value = getJdbcTemplate().update(hdrquery, new Object[] { Integer.valueOf(configBean.getInFileId()), configBean.getStHeader() });
        if (value > 0) {
          System.out.println("Headers data inserted");
          result = true;
        } else {
          System.out.println("Headers data not inserted");
          result = false;
        } 
        String ftpquery = "INSERT into MAIN_FTPDetails (FTPDETAILID , FILEID,FILELOCATION,FILEPATH,FTPUSERNAME,FTPPASSWORD,FTPPORT)  values(((SELECT MAX(FTPDETAILID) FROM  MAIN_FTPDetails)+1),?,?,?,?,?,?)";
        value = getJdbcTemplate().update(ftpquery, new Object[] { Integer.valueOf(configBean.getInFileId()), configBean.getFileLocation(), configBean.getFilePath(), configBean.getFtpUser(), configBean.getFtpPwd(), Integer.valueOf(configBean.getFtpPort()) });
        if (value > 0) {
          System.out.println("FTP data inserted");
          this.transactionManager.commit(status);
          result = true;
        } else {
          System.out.println("FTP  data not inserted");
          this.transactionManager.rollback(status);
          result = false;
        } 
      } else {
        result = false;
      } 
    } catch (Exception ex) {
      System.out.println("Error Occurred.....");
      ex.printStackTrace();
      this.transactionManager.rollback(status);
      result = false;
    } 
    return result;
  }
  
  public boolean chkTblExistOrNot(ConfigurationBean configBean) throws Exception {
    try {
      String filesql = "SELECT count(*) FROM Main_FileSource WHERE upper (Filename) ='" + configBean.getStFileName().toUpperCase() + "' ";
      int filerowNum = 0;
      System.out.println(filesql);
      filerowNum = ((Integer)getJdbcTemplate().queryForObject(filesql, Integer.class)).intValue();
      System.out.println(filesql);
      String sql = "SELECT count (*) FROM tab WHERE tname  = '" + configBean.getStFileName().toUpperCase() + "'";
      int rowNum = 0;
      rowNum = ((Integer)getJdbcTemplate().queryForObject(sql, Integer.class)).intValue();
      System.out.println(sql);
      if (rowNum > 0 || filerowNum > 0)
        return false; 
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.toString());
      return false;
    } 
  }
}
