package com.recon.dao.impl;


import com.mastercard.main.ChargeBackImpl;
import com.mastercard.model.MasterCardModel;
import com.recon.dao.ISettelmentDao;
import com.recon.model.CompareSetupBean;
import com.recon.model.FilterationBean;
import com.recon.model.Gl_bean;
import com.recon.model.Mastercard_chargeback;
import com.recon.model.Rupay_Gl_repo;
import com.recon.model.Rupay_gl_Lpcases;
import com.recon.model.Rupay_gl_autorev;
import com.recon.model.Rupay_sur_GlBean;
import com.recon.model.SettlementBean;
import com.recon.model.SettlementTypeBean;
import com.recon.util.GeneralUtil;
import com.recon.util.OracleConn;
import com.recon.util.Utility;
import com.recon.util.demo;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

@Component
public class SettlmentDaoImpl extends JdbcDaoSupport implements ISettelmentDao {
  public ArrayList<String> gettype(String tableName) {
	return null;}
  
  public List<SettlementTypeBean> getSettlmentType(String type, String tablename) {
    String query = "SELECT  * FROM " + tablename + " where remarks='" + type + "'";
    System.out.println(query);
    List<SettlementTypeBean> typeList = getJdbcTemplate().query(query, 
        (RowMapper)new BeanPropertyRowMapper(SettlementTypeBean.class));
    return typeList;
  }
  
  public ArrayList<SettlementTypeBean> getReconData(String tableName, String type, String date, String searchValue) {
    ArrayList<SettlementTypeBean> settlementTypeBeans;
    String result = null;
    String[] splitype = null;
    String formTablename = null;
    char c = tableName.charAt(0);
    if (Character.isDigit(c)) {
      result = formTablename;
      String getTable = "select filename from main_filesource where fileid=" + tableName;
      formTablename = (String)getJdbcTemplate().queryForObject(getTable, String.class);
      String stcat = (String)getJdbcTemplate().queryForObject(
          "SELECT FILE_CATEGORY FROM MAIN_FILESOURCE WHERE FILEID = ?", new Object[] { tableName }, String.class);
      tableName = "SETTLEMENT_" + stcat + "_" + formTablename;
    } 
    String[] split_table = tableName.split("_");
    String concat_table = String.valueOf(split_table[0]) + "_" + split_table[2];
    try {
      String query = "";
      settlementTypeBeans = null;
      if (concat_table.trim().equals("SETTLEMENT_SWITCH")) {
        if (split_table[1].equals("AMEX") || split_table[1].equals("RUPAY")) {
          query = "SELECT PAN,TERMID,TRACE,dcrs_remarks FROM " + tableName + " WHERE   dcrs_remarks='" + type + 
            "' " + 
            " AND to_date(filedate,'dd/mm/yy')= to_date('" + date + "','dd/mm/yy') ";
          if (!searchValue.equals(""))
            query = String.valueOf(query) + " AND (PAN LIKE '%" + searchValue + "%' OR TERMID LIKE '%" + searchValue + 
              "%'  OR TRACE LIKE '%" + searchValue + "%')"; 
        } else if (split_table[1].equals("ONUS")) {
          query = "SELECT PAN,TERMID,TRACE,dcrs_remarks FROM " + tableName + " WHERE   dcrs_remarks='" + type + 
            "' " + 
            " AND to_date(filedate,'dd/mm/yy')= to_date('" + date + "','dd/mm/yy') ";
          if (!searchValue.equals(""))
            query = String.valueOf(query) + " AND (PAN LIKE '%" + searchValue + "%' OR TERMID LIKE '%" + searchValue + 
              "%'  OR TRACE LIKE '%" + searchValue + "%')"; 
          System.out.println(query);
        } 
      } else if (concat_table.trim().equals("SETTLEMENT_CBS")) {
        if (split_table[1].equals("AMEX") || split_table[1].equals("RUPAY")) {
          query = "SELECT  foracid,CONTRA_ACCOUNT,Tran_Date,dcrs_remarks ,PARTICULARALS  FROM " + tableName + 
            " WHERE dcrs_remarks='" + type + "' " + 
            " AND to_date(filedate,'dd/mm/yy')= to_date('" + date + "','dd/mm/yy') ";
          if (!searchValue.equals(""))
            query = String.valueOf(query) + " AND( foracid LIKE '%" + searchValue + "%' OR CONTRA_ACCOUNT LIKE '%" + 
              searchValue + "%' " + "PARTICULARALS '%" + searchValue + "%')"; 
        } else if (split_table[1].equals("ONUS")) {
          query = "SELECT  ACCOUNT_NUMBER,CONTRA_ACCOUNT,TranDate,TRAN_PARTICULAR,dcrs_remarks  FROM " + 
            tableName + " WHERE dcrs_remarks='" + type + "' " + 
            " AND to_date(filedate,'dd/mm/yy')= to_date('" + date + "','dd/mm/yy') ";
          if (!searchValue.equals(""))
            query = String.valueOf(query) + " AND( ACCOUNT_NUMBER LIKE '%" + searchValue + "%' OR CONTRA_ACCOUNT LIKE '%" + 
              searchValue + "%' " + " OR TRACE LIKE '%" + searchValue + 
              "%' OR TRAN_PARTICULAR LIKE '%" + searchValue + "%')"; 
          this.logger.info(query);
        } 
      } 
      settlementTypeBeans = (ArrayList<SettlementTypeBean>)getJdbcTemplate().query(query, 
          (RowMapper)new BeanPropertyRowMapper(SettlementTypeBean.class));
    } catch (Exception ex) {
      ex.printStackTrace();
      settlementTypeBeans = null;
    } 
    return settlementTypeBeans;
  }
  
  public String getFileName(String stfileId) {
    String fileName = "";
    try {
      fileName = (String)getJdbcTemplate().queryForObject("SELECT FILENAME FROM MAIN_FILESOURCE WHERE FILEID = ?", 
          new Object[] { stfileId }, String.class);
    } catch (Exception e) {
      System.out.println("Exception in getFileName");
    } 
    return fileName;
  }
  
  public List<List<String>> getReconData1(String stFileId, String dcrs_remarks, String date, String searchValue) {
    ResultSet rset = null;
    PreparedStatement pstmt = null;
    try {
      String GET_FILENAME = "SELECT FILENAME FROM MAIN_FILESOURCE WHERE FILEID = ?";
      String fileName = (String)getJdbcTemplate().queryForObject(GET_FILENAME, new Object[] { stFileId }, String.class);
      String stcategory = (String)getJdbcTemplate().queryForObject(
          "SELECT FILE_CATEGORY FROM MAIN_FILESOURCE WHERE FILEID = ?", new Object[] { stFileId }, String.class);
      String stTableName = "SETTLEMENT_" + stcategory + "_" + fileName;
      String stsubcategory = (String)getJdbcTemplate().queryForObject(
          "SELECT FILE_SUBCATEGORY FROM MAIN_FILESOURCE WHERE FILEID = ?", new Object[] { stFileId }, String.class);
      if (stcategory.equals("NFS") || stcategory.equals("CASHNET"))
        stTableName = "SETTLEMENT_" + stcategory + "_" + stsubcategory.substring(0, 3) + "_" + fileName; 
      List<String> Column_list = getColumnList(stTableName);
      List<String> cols = getColumnList(stTableName);
      String columns = "";
      for (int i = 0; i < Column_list.size(); i++) {
        if (i == Column_list.size() - 1) {
          columns = String.valueOf(columns) + (String)Column_list.get(i);
        } else {
          columns = String.valueOf(columns) + (String)Column_list.get(i) + ",";
        } 
      } 
      String GET_DATA = "";
      if (dcrs_remarks.contains("-UNRECON")) {
        GET_DATA = "SELECT " + columns + " FROM " + stTableName + " WHERE (DCRS_REMARKS LIKE '%" + dcrs_remarks + 
          "%' OR DCRS_REMARKS LIKE '%GENERATED%') AND TO_DATE(FILEDATE,'DD/MM/YY') = TO_DATE('" + date + 
          "','DD/MM/YY')";
      } else {
        GET_DATA = "SELECT " + columns + " FROM " + stTableName + " WHERE DCRS_REMARKS = '" + dcrs_remarks + 
          "' AND TO_DATE(FILEDATE,'DD/MM/YY') = TO_DATE('" + date + "','DD/MM/YY')";
      } 
      if (stcategory.equals("NFS") && (dcrs_remarks.equalsIgnoreCase("MATCHED_UNSUCCESSFUL") || 
        dcrs_remarks.equalsIgnoreCase("NFS-UPI") || dcrs_remarks.equalsIgnoreCase("NFS-DFS") || 
        dcrs_remarks.equalsIgnoreCase("NFS-JCB")))
        GET_DATA = "SELECT " + columns + " FROM " + stTableName + " WHERE DCRS_REMARKS like '" + dcrs_remarks + 
          "%' AND TO_DATE(FILEDATE,'DD/MM/YY') = TO_DATE('" + date + "','DD/MM/YY')"; 
      List<List<String>> DATA = new ArrayList<>();
      DATA.add(Column_list);
      pstmt = getConnection().prepareStatement(GET_DATA);
      rset = pstmt.executeQuery();
      while (rset.next()) {
        List<String> DbData = new ArrayList<>();
        for (String colName : Column_list) {
          if (rset.getString(colName) == null) {
            DbData.add("");
            continue;
          } 
          DbData.add(rset.getString(colName));
        } 
        DATA.add(DbData);
      } 
      return DATA;
    } catch (Exception e) {
      System.out.println("Exception is " + e);
      return null;
    } finally {
      try {
        if (rset != null) {
          pstmt.close();
          rset.close();
        } 
      } catch (Exception e) {
        System.out.println("Exception in closing rset");
      } 
    } 
  }
  
  public ArrayList<String> getColumnList(String tableName) {
	return null;}
  
  public int getReconDataCount(String table, String type, String date, String searchValue) {
    String query = "";
    int count = 0;
    String[] split_table = table.split("_");
    String concat_table = String.valueOf(split_table[0]) + "_" + split_table[2];
    try {
      if (concat_table.equals("SETTLEMENT_SWITCH")) {
        query = "SELECT count(*) FROM " + table + " WHERE   dcrs_remarks='" + type + "' " + 
          " AND to_date(filedate,'dd/mm/yy')= to_date('" + date + "','dd/mm/yy') ";
        if (!searchValue.equals(""))
          query = String.valueOf(query) + " AND (PAN LIKE '%" + searchValue + "%' OR TERMID LIKE '%" + searchValue + 
            "%'  OR TRACE LIKE '%" + searchValue + "%')"; 
        this.logger.info(query);
      } else if (concat_table.trim().equals("SETTLEMENT_CBS")) {
        query = "SELECT  count(*)  FROM " + table + " WHERE dcrs_remarks='" + type + "' " + 
          " AND to_date(filedate,'dd/mm/yy')= to_date('" + date + "','dd/mm/yy') ";
        if (!searchValue.equals(""))
          query = String.valueOf(query) + " AND( ACCOUNT_NUMBER LIKE '%" + searchValue + "%' OR CONTRA_ACCOUNT LIKE '%" + 
            searchValue + "%' " + " OR TRACE LIKE '%" + searchValue + "%')"; 
        this.logger.info(query);
      } 
      count = ((Integer)getJdbcTemplate().queryForObject(query, Integer.class)).intValue();
    } catch (Exception ex) {
      ex.printStackTrace();
    } 
    return count;
  }
  
  public ArrayList<SettlementTypeBean> getChngReconData(String table, String type, String date, String searchValue, int jtStartIndex, int jtPageSize) {
    ArrayList<SettlementTypeBean> settlementTypeBeans;
    String[] split_table = table.split("_");
    String concat_table = String.valueOf(split_table[0]) + "_" + split_table[2];
    try {
      String query = "";
      settlementTypeBeans = null;
      if (concat_table.trim().equals("SETTLEMENT_SWITCH")) {
        if (split_table[1].equals("AMEX") || split_table[1].equals("RUPAY")) {
          query = "SELECT * FROM ( SELECT ROWNUM,PAN,TERMID,TRACE,FileDate,dcrs_remarks , '" + table + 
            "' as SETLTBL, ROW_NUMBER() OVER( ORDER BY PAN) AS RN FROM " + table + 
            " WHERE   dcrs_remarks='" + type + "' " + 
            " AND to_date(filedate,'dd/mm/yy')= to_date('" + date + "','dd/mm/yy') ";
          if (!searchValue.equals(""))
            query = String.valueOf(query) + " AND (PAN LIKE '%" + searchValue + "%' OR TERMID LIKE '%" + searchValue + 
              "%'  OR TRACE LIKE '%" + searchValue + "%')"; 
          query = String.valueOf(query) + ") where RN between " + (jtStartIndex + 1) + " and " + (jtPageSize + jtStartIndex);
          this.logger.info(query);
        } else if (split_table[1].equals("ONUS")) {
          query = "SELECT * FROM ( SELECT ROWNUM,PAN,TERMID,TRACE,FileDate,dcrs_remarks , '" + table + 
            "' as SETLTBL, ROW_NUMBER() OVER( ORDER BY PAN) AS RN FROM " + table + 
            " WHERE   dcrs_remarks='" + type + "' " + 
            " AND to_date(filedate,'dd/mm/yy')= to_date('" + date + "','dd/mm/yy') ";
          if (!searchValue.equals(""))
            query = String.valueOf(query) + " AND (PAN LIKE '%" + searchValue + "%' OR TERMID LIKE '%" + searchValue + 
              "%'  OR TRACE LIKE '%" + searchValue + "%')"; 
          query = String.valueOf(query) + ") where RN between " + (jtStartIndex + 1) + " and " + (jtPageSize + jtStartIndex);
          this.logger.info(query);
        } 
      } else if (concat_table.trim().equals("SETTLEMENT_CBS")) {
        if (split_table[1].equals("AMEX") || split_table[1].equals("RUPAY")) {
          query = "SELECT * FROM ( SELECT  foracid,CONTRA_ACCOUNT,Tran_Date,dcrs_remarks,PARTICULARALS ,'" + 
            table + "' as SETLTBL, ROW_NUMBER() OVER( ORDER BY foracid ) AS RN  FROM " + table + 
            " WHERE dcrs_remarks='" + type + "' " + 
            " AND to_date(filedate,'dd/mm/yy')= to_date('" + date + "','dd/mm/yy') ";
          if (!searchValue.equals(""))
            query = String.valueOf(query) + " AND( foracid LIKE '%" + searchValue + "%' OR CONTRA_ACCOUNT LIKE '%" + 
              searchValue + "%' OR PARTICULARALS LIKE '%" + searchValue + "%')"; 
          query = String.valueOf(query) + ") where RN between " + (jtStartIndex + 1) + " and " + (jtPageSize + jtStartIndex);
          this.logger.info(query);
        } else if (split_table[1].equals("ONUS")) {
          query = "SELECT * FROM ( SELECT  ACCOUNT_NUMBER,CONTRA_ACCOUNT,TranDate,FileDate,TRAN_PARTICULAR,remarks ,'" + 
            table + "' as SETLTBL, ROW_NUMBER() OVER( ORDER BY ACCOUNT_NUMBER ) AS RN  FROM " + table + 
            " WHERE remarks='" + type + "' " + 
            " AND to_date(filedate,'dd/mm/yy')= to_date('" + date + "','dd/mm/yy') ";
          if (!searchValue.equals(""))
            query = String.valueOf(query) + " AND( ACCOUNT_NUMBER LIKE '%" + searchValue + "%' OR CONTRA_ACCOUNT LIKE '%" + 
              searchValue + "%' " + " OR TRACE LIKE '%" + searchValue + 
              "%' OR TRAN_PARTICULAR LIKE '%" + searchValue + "%')"; 
          query = String.valueOf(query) + ") where RN between " + (jtStartIndex + 1) + " and " + (jtPageSize + jtStartIndex);
          this.logger.info(query);
        } 
      } 
      settlementTypeBeans = (ArrayList<SettlementTypeBean>)getJdbcTemplate().query(query, 
          (RowMapper)new BeanPropertyRowMapper(SettlementTypeBean.class));
    } catch (Exception ex) {
      ex.printStackTrace();
      settlementTypeBeans = null;
    } 
    return settlementTypeBeans;
  }
  
  public void manualReconToSettlement(String table_Name, String stFile_date) throws Exception {}
  
  public int updateRecord(SettlementTypeBean settlementTypeBean) {
    try {
      int result = 0;
      String query = "update " + settlementTypeBean.getSetltbl() + " set remarks='" + 
        settlementTypeBean.getrEMARKS() + "' WHERE 1=1 AND remarks='ONUS-RECON'" + 
        "\tAND to_date(filedate,'dd/mm/yy')= to_date('" + settlementTypeBean.getFileDate() + 
        "','dd/mm/yy') ";
      if (settlementTypeBean.getSetltbl().trim().equalsIgnoreCase("settlement_switch")) {
        query = String.valueOf(query) + " AND pan='" + settlementTypeBean.getPan() + "' " + "AND TERMID ='" + 
          settlementTypeBean.gettERMID() + "'" + " AND TRACE ='" + settlementTypeBean.gettRACE() + "'";
      } else if (settlementTypeBean.getSetltbl().trim().equalsIgnoreCase("settlement_cbs")) {
        query = String.valueOf(query) + " AND ACCOUNT_NUMBER='" + settlementTypeBean.getaCCOUNT_NUMBER() + "' " + 
          "AND CONTRA_ACCOUNT ='" + settlementTypeBean.getcONTRA_ACCOUNT() + "'" + " AND TRANDATE ='" + 
          settlementTypeBean.gettRANDATE() + "'" + " AND TRAN_PARTICULAR ='" + 
          settlementTypeBean.gettRAN_PARTICULAR() + "'";
      } 
      System.out.println("query" + query);
      result = getJdbcTemplate().update(query);
      if (result > 0)
        return 1; 
      return 0;
    } catch (Exception ex) {
      ex.printStackTrace();
      return 0;
    } 
  }
  
  public Boolean checkfileprocessed(SettlementBean settlementbeanObj) {
    String GET_FILES = "";
    if (settlementbeanObj.getStsubCategory().equalsIgnoreCase("DOMESTIC") || 
      settlementbeanObj.getStsubCategory().equalsIgnoreCase("ISSUER")) {
      GET_FILES = "SELECT FILEID FROM  main_filesource WHERE FILE_CATEGORY = '" + settlementbeanObj.getCategory() + 
        "' AND (FILE_SUBCATEGORY = '" + settlementbeanObj.getStsubCategory() + "' " + 
        " OR FILE_SUBCATEGORY = 'SURCHARGE')";
    } else {
      GET_FILES = "SELECT FILEID FROM main_filesource WHERE FILE_CATEGORY = '" + settlementbeanObj.getCategory() + 
        "' AND FILE_SUBCATEGORY = '" + settlementbeanObj.getStsubCategory() + "'";
    } 
    List<String> fileids = getJdbcTemplate().query(GET_FILES, new Object[] {}, new RowMapper<String>() {
		@Override
		public String mapRow(ResultSet rs, int row) throws SQLException {
			return rs.getString("FILEID");
		}
	});
    Iterator<String> iterator = fileids.iterator();
    if (iterator.hasNext()) {
      String fileid = iterator.next();
      String CHECK_IT = "SELECT COUNT(*) FROM  main_file_upload_dtls WHERE FILEID = '" + fileid + 
        "' AND FILEDATE=STR_TO_DATE('" + settlementbeanObj.getDatepicker() + "','%Y/%m/%d')AND FILTER_FLAG = (SELECT FILTERATION FROM  main_filesource WHERE FILEID = '" + 
        fileid + "') AND KNOCKOFF_FLAG = (SELECT KNOCKOFF FROM main_filesource WHERE FILEID = '" + fileid + 
        "')" + "AND COMAPRE_FLAG = 'Y'";
      try {
        int count = ((Integer)getJdbcTemplate().queryForObject(CHECK_IT, new Object[0], Integer.class)).intValue();
        System.out.println(String.valueOf(CHECK_IT) + " " + count);
        if (count == 0)
          return Boolean.valueOf(false); 
        return Boolean.valueOf(true);
      } catch (Exception e) {
        return Boolean.valueOf(false);
      } 
    } 
    return Boolean.valueOf(true);
  }
  
  public Boolean checkfileprocessedCTC(SettlementBean settlementbeanObj) {
	    String CHECK_IT = "SELECT COUNT(*) as count FROM settlement_nfs_iss_c2c_cbs  WHERE FILEDATE = DATE_FORMAT('" + settlementbeanObj.getDatepicker() + "','%Y/%m/%d')";
    System.out.println(CHECK_IT);
    int count = ((Integer)getJdbcTemplate().queryForObject(CHECK_IT, new Object[0], Integer.class)).intValue();
    if (count > 0)
      return Boolean.valueOf(true); 
    return Boolean.valueOf(false);
  }

  public Boolean checkfileprocessedCTC4(SettlementBean settlementbeanObj) {
	    String CHECK_IT = "select sum(count) from (SELECT COUNT(*) as count FROM settlement_dfs_cbs whERE filedate = STR_to_date('"+settlementbeanObj.getDatepicker()+"','%Y/%m/%d')  union all SELECT COUNT(*) as count FROM settlement_dfs_dfs WHERE filedate = STR_to_date('"+settlementbeanObj.getDatepicker()+"','%Y/%m/%d') ) a";
    System.out.println(CHECK_IT);
    int count = ((Integer)getJdbcTemplate().queryForObject(CHECK_IT, new Object[0], Integer.class)).intValue();
    if (count > 0)
      return Boolean.valueOf(true); 
    return Boolean.valueOf(false);
  }

  public Boolean checkfileprocessedCTC3(SettlementBean settlementbeanObj) {
	    String CHECK_IT = "select sum(count) from (SELECT COUNT(*) as count FROM settlement_jcb_cbs whERE filedate = STR_to_date('"+settlementbeanObj.getDatepicker()+"','%Y/%m/%d')  union all SELECT COUNT(*) as count FROM settlement_jcb_jcb WHERE filedate = STR_to_date('"+settlementbeanObj.getDatepicker()+"','%Y/%m/%d') ) a";
    System.out.println(CHECK_IT);
    int count = ((Integer)getJdbcTemplate().queryForObject(CHECK_IT, new Object[0], Integer.class)).intValue();
    if (count > 0)
    	
      return Boolean.valueOf(true); 
    return Boolean.valueOf(false);
  }
  
  public void generate_Reports(SettlementBean settlementBeanObj) throws Exception {}
  
  public boolean checkNcreateFolder(SettlementBean settlementBeanObj, List<String> stFileNames) {
    try {
      File directory = new File(settlementBeanObj.getStPath());
      if (!directory.exists())
        directory.mkdirs(); 
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
      Date date = sdf.parse(settlementBeanObj.getDatepicker());
      sdf = new SimpleDateFormat("dd-MMM-yyyy");
      String stnewDate = sdf.format(date);
      String stnewPath = String.valueOf(settlementBeanObj.getStPath()) + File.separator + stnewDate;
      directory = new File(stnewPath);
      if (!directory.exists())
        directory.mkdirs(); 
      this.logger.info(directory.listFiles() + " size is " + (directory.listFiles()).length);
      stnewPath = String.valueOf(settlementBeanObj.getStPath()) + File.separator + stnewDate + File.separator + 
        settlementBeanObj.getStMergerCategory();
      if (directory.listFiles() != null && (directory.listFiles()).length > 0) {
        stnewPath = String.valueOf(stnewPath) + "_" + ((directory.listFiles()).length + 1);
        this.logger.info("stnewPath is " + stnewPath);
      } else {
        stnewPath = String.valueOf(stnewPath) + "_" + (directory.listFiles()).length;
        this.logger.info("stnewPath is " + stnewPath);
      } 
      settlementBeanObj.setStPath(stnewPath);
      directory = new File(stnewPath);
      if (!directory.exists())
        directory.mkdirs(); 
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } 
    return true;
  }
  
  public void DeleteFiles(String stpath) {
    try {
      this.logger.info("Inside delete foleder path is " + stpath);
      File directory = new File(stpath);
      if (directory.exists()) {
        FileUtils.forceDelete(directory);
        this.logger.debug(Boolean.valueOf(directory.exists()));
      } 
    } catch (Exception e) {
      System.out.println("Exception e" + e);
    } 
  }
  
  public void generate_ipm(String outputString2) throws Exception {}
  
  public boolean generateCTF(SettlementBean settlementBean, List<String> files) throws IOException {
    String path = String.valueOf(settlementBean.getStPath()) + File.separator + "Bankaway.ctf";
    String path2 = String.valueOf(settlementBean.getStPath()) + File.separator;
    BufferedWriter output = null;
    int row_lenth_1 = 168;
    int row_lenth_2 = 45;
    int row_lenth_3 = 21;
    int count_1 = 0;
    int count_2 = 0;
    int count_3 = 0;
    String julian_date = null;
    int result_count = 0;
    String result_count_val = null;
    String sun_val = null;
    int main_count = 0;
    ArrayList<String> arr_test = new ArrayList<>();
    ArrayList<String> arr_test2 = new ArrayList<>();
    ArrayList<SettlementTypeBean> arr = new ArrayList<>();
    String julian_date1 = null;
    try {
      OracleConn conn = new OracleConn();
      Connection con = conn.getconn();
      File file = new File(path);
      output = new BufferedWriter(new FileWriter(file));
      String get_bankrepo = "select * from SETTLEMENT_CARDTOCARD_BANKREPO where DCRS_REMARKS='CARDTOCARD_BANKWAY_MATCED' and TO_CHAR (filedate, 'dd/MM/yyyy') =  TO_CHAR (TO_DATE ('" + 
        settlementBean.getDatepicker() + "', 'dd/MM/YYYY'), 'dd/MM/YYYY')";
      PreparedStatement st = con.prepareStatement(get_bankrepo);
      ResultSet rs = st.executeQuery();
      while (rs.next()) {
        SettlementTypeBean sb = new SettlementTypeBean();
        String visa_card_no = rs.getString("VISA_CARD_NO");
        String mobile_no = rs.getString("MOBILE_NO");
        String amount1 = rs.getString("AMOUNT");
        String amount = amount1.replaceAll("[,.]", "");
        String sol_id = rs.getString("SOL_ID");
        String debit_acc = rs.getString("DEBIT_ACC");
        String acc_name = rs.getString("ACC_NAME");
        String payment_id = rs.getString("PAYMENT_ID");
        String channel = rs.getString("CHANNEL");
        String date_time = rs.getString("DATE_TIME");
        String file_date = rs.getString("FILEDATE");
        long random_number = Utility.generateRandom();
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
        Date dateinput = inputFormat.parse(rs.getString("FILEDATE"));
        SimpleDateFormat sdfoffsite1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
        DateFormat outputFormat_new = new SimpleDateFormat("ddMMyyyy");
        String outputString_new = outputFormat_new.format(dateinput);
        Date datecisb = sdfoffsite1.parse(file_date);
        sdfoffsite1 = new SimpleDateFormat("MM/dd/yyyy");
        System.out.println(sdfoffsite1.format(datecisb));
        String dataenew = sdfoffsite1.format(datecisb);
        int julian_dt = Utility.convertToJulian(outputString_new);
        int julian_dt1 = Utility.convertToJulian2(outputString_new);
        julian_date = String.valueOf(julian_dt);
        if (String.valueOf(julian_date).length() <= 1)
          julian_date = "000" + julian_date; 
        if (String.valueOf(julian_date).length() == 2)
          julian_date = "00" + julian_date; 
        if (String.valueOf(julian_date).length() == 3)
          julian_date = "0" + julian_date; 
        julian_date1 = String.valueOf(julian_dt1);
        String[] split_dt = dataenew.split("/");
        System.out.println(String.valueOf(split_dt[0]) + split_dt[1]);
        String amount_val = Utility.appnd_zeros(amount);
        System.out.println("12 digit Amount" + amount_val);
        String get_acc_name = Utility.appnd_space(acc_name);
        arr_test.add("06");
        arr_test.add("20");
        arr_test.add(visa_card_no);
        arr_test.add("000");
        arr_test.add("   ");
        arr_test.add(Utility.get_mod("7421426" + julian_date + random_number));
        arr_test.add("00000000");
        arr_test.add(String.valueOf(split_dt[0]) + split_dt[1]);
        arr_test.add("000000000000");
        arr_test.add("   ");
        arr_test.add(amount_val);
        arr_test.add("356");
        arr_test.add(get_acc_name);
        arr_test.add("VISAMONEYTXFR");
        arr_test.add("IN ");
        arr_test.add("6012");
        arr_test.add("00000");
        arr_test.add("   ");
        arr_test.add(" ");
        arr_test.add(" ");
        arr_test.add("1");
        arr_test.add("00");
        arr_test.add("9");
        arr_test.add(" ");
        arr_test.add("      ");
        arr_test.add(" ");
        arr_test.add(" ");
        arr_test.add(" ");
        arr_test.add(" ");
        arr_test.add("0");
        arr_test.add(julian_date1);
        arr_test.add("0");
        arr_test.add("06");
        arr_test.add("21");
        String sub_str = visa_card_no.substring(0, 6);
        String concat = String.valueOf(sub_str) + "4214260000";
        arr_test.add(concat);
        arr_test.add("00");
        arr_test.add(" ");
        arr_test.add("PYMNT ID NO-");
        arr_test.add(" ");
        arr_test.add(payment_id);
        arr_test.add("                                                         ");
        arr_test.add("000000000000");
        arr_test.add(" ");
        arr_test.add("             ");
        arr_test.add("0");
        arr_test.add("                           ");
        arr_test.add("000000000");
        arr_test.add("  ");
        arr_test.add("06");
        arr_test.add("23");
        arr_test.add("            ");
        arr_test.add("CRCP");
      } 
      FileWriter writer = new FileWriter(file);
      BufferedWriter bw = new BufferedWriter(writer);
      String bind_rec = "";
      String bind_2 = "";
      String bind_rec1 = "";
      String bind_3 = "";
      int counter_value = 0;
      for (String str : arr_test) {
        counter_value++;
        bw.append(str);
        count_1++;
        System.out.println("Count ::" + count_1);
        if (str.equals(julian_date1)) {
          bind_rec = "";
          bind_rec = String.valueOf(bind_rec) + str;
        } 
        count_3++;
        if (!bind_rec.equals("") && count_3 == 2)
          bind_2 = String.valueOf(bind_rec) + "0"; 
        if (str.equals("000000000")) {
          bind_rec1 = "";
          bind_rec1 = String.valueOf(bind_rec1) + str;
          System.out.println("Bind 1 :: " + bind_rec1);
        } 
        count_3++;
        if (!bind_rec1.equals("") && count_3 == 2)
          bind_3 = String.valueOf(bind_rec1) + "  "; 
        if (bind_2.equals(String.valueOf(julian_date1) + "0")) {
          bw.append("\n");
          main_count++;
          bind_2 = "";
          count_3 = 0;
          bind_rec = "";
          count_1 = 0;
          continue;
        } 
        if (bind_3.equals("000000000  ")) {
          bw.append("\n");
          main_count++;
          bind_3 = "";
          count_3 = 0;
          bind_rec1 = "";
          bind_2 = "";
          count_3 = 0;
          bind_rec = "";
          count_1 = 0;
          continue;
        } 
        if (str.equals("CRCP")) {
          System.out.println("Counter value-->>" + counter_value);
          System.out.println("Array value-->>" + arr_test.size());
          if (counter_value != arr_test.size()) {
            bw.append("\n");
          } else {
            System.out.println("Inside else");
          } 
          main_count++;
          bind_3 = "";
          count_3 = 0;
          bind_rec1 = "";
          bind_2 = "";
          count_3 = 0;
          bind_rec = "";
          count_1 = 0;
        } 
      } 
      String get_rec_count = "select count(*) as count,sum(to_number(replace((replace(os1.amount,',')),'.'))) as total from SETTLEMENT_CARDTOCARD_BANKREPO os1";
      PreparedStatement pst = con.prepareStatement(get_rec_count);
      ResultSet rst = pst.executeQuery();
      while (rst.next()) {
        sun_val = rst.getString("total");
        System.out.println("Sum" + sun_val);
      } 
      bw.close();
      writer.close();
      System.out.println("CTF file created");
      double nol = 990.0D;
      File file1 = new File(path);
      Scanner scanner = new Scanner(file1);
      int count = 0;
      int count_lines = 0;
      while (scanner.hasNextLine()) {
        scanner.nextLine();
        count++;
      } 
      System.out.println("Lines in the file: " + count);
      double temp = count / nol;
      int temp1 = (int)temp;
      int nof = 0;
      if (temp1 == temp) {
        nof = temp1;
      } else {
        nof = temp1 + 1;
      } 
      System.out.println("No. of files to be generated :" + nof);
      int new_count = 0;
      int total_sum = 0;
      int total_rows = 0;
      FileInputStream fstream = new FileInputStream(path);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      for (int j = 1; j <= nof; j++) {
        boolean newLineForLastFile = false;
        total_rows = 0;
        total_sum = 0;
        new_count = 0;
        result_count = 0;
        FileWriter fstream1 = new FileWriter(String.valueOf(path2) + j + ".ctf");
        BufferedWriter out = new BufferedWriter(fstream1);
        for (int i = 1; i <= nol; i++) {
          String strLine = br.readLine();
          if (strLine != null) {
            total_rows++;
            out.write(strLine);
            if (i != nol) {
              newLineForLastFile = true;
              out.newLine();
              if (new_count == 3) {
                result_count++;
                new_count = 0;
              } 
              try {
                new_count++;
                String get_amount = strLine.substring(77, 88);
                int get_tot = Integer.parseInt(get_amount);
                total_sum += get_tot;
              } catch (Exception e) {
                e.printStackTrace();
              } 
            } else {
              System.out.println("Inside else of i != nol");
              newLineForLastFile = false;
            } 
          } else {
            result_count++;
            break;
          } 
        } 
        if (j != nof || !newLineForLastFile) {
          result_count++;
          out.newLine();
          System.out.println("Inside no of lines" + nof);
        } 
        System.out.println("Total Sum::" + total_sum);
        total_rows++;
        System.out.println("Main Count" + total_rows);
        String count_val = Integer.toString(total_rows);
        String main_cnt = Utility.auto_append(count_val);
        int count_6 = Integer.parseInt(main_cnt);
        int main_count2 = count_6 + 1;
        String count_val2 = Integer.toString(main_count2);
        String main_cnt2 = Utility.auto_append(count_val2);
        System.out.println("Total count ::" + result_count);
        String val = Integer.toString(result_count);
        result_count_val = Utility.auto_append(val);
        int get_row1 = result_count + 1;
        String val2 = Integer.toString(get_row1);
        String row_count = Utility.auto_append3(val2);
        int get_count = Integer.parseInt(row_count);
        int get_row2 = get_row1 + 1;
        String get_rw = Integer.toString(get_row2);
        String row_count2 = Utility.auto_append3(get_rw);
        String tot_val = Integer.toString(total_sum);
        String get_total = Utility.auto_append2(tot_val);
        out.append("9100426365" + julian_date1 + "000000000000000" + result_count_val + "000000" + main_cnt + 
            "000000" + "00000001" + row_count + "000000000000000000" + get_total + "000000000000000" + 
            "00000000000000" + "0000000000000000" + "0000000");
        out.newLine();
        out.append("9200426365" + julian_date1 + "000000000000000" + result_count_val + "000000" + main_cnt2 + 
            "000000" + "00000000" + row_count2 + "000000000000000000" + get_total + "000000000000000" + 
            "00000000000000" + "0000000000000000" + "0000000");
        out.close();
        files.add(String.valueOf(path2) + j + ".ctf");
      } 
      in.close();
    } catch (ParseException e) {
      e.printStackTrace();
      return false;
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
      return false;
    } catch (SQLException e1) {
      e1.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Failed to CTF file created");
    } 
    output.close();
    return true;
  }
  
  public List<Mastercard_chargeback> getMastercardchargeback(String arnNo) throws Exception {
	return null;}
  
  public int Savechargeback(String microfilm, String ref_id, String settlement_amount, String settlement_currency, String txn_amount, String txn_currency, String reason, String documentation, String remarks) throws Exception {
    String query = "insert into MASTERCARD_CHARGE values( '" + microfilm + "','" + ref_id + "','" + 
      settlement_amount + "','" + settlement_currency + "','" + txn_amount + "'" + ",'" + txn_currency + 
      "','" + reason + "','" + documentation + "','" + remarks + "')";
    return getJdbcTemplate().update(query);
  }
  
  public List<List<Mastercard_chargeback>> GenerateReportChargebk() throws Exception {
    List<List<Mastercard_chargeback>> Data = new ArrayList<>();
    List<Mastercard_chargeback> TTUM_Data = new ArrayList<>();
    List<String> ExcelHeaders = new ArrayList<>();
    List<Mastercard_chargeback> Excel_header = new ArrayList<>();
    try {
      String GET_DATA = "select * from mastercard_charge";
      System.out.println(GET_DATA);
      Connection conn = getConnection();
      PreparedStatement pstmt = conn.prepareStatement(GET_DATA);
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        Mastercard_chargeback generateBean = new Mastercard_chargeback();
        generateBean.setMicrofilm(rset.getString("MICROFILM"));
        generateBean.setRef_id(rset.getString("REF_ID"));
        generateBean.setSettlement_amount(rset.getString("SETTLEMENT_AMOUNT"));
        generateBean.setSettlement_currency(rset.getString("SETTLEMENT_CURRENCY"));
        generateBean.setTxn_amount(rset.getString("TXN_AMOUNT"));
        generateBean.setTxn_currency(rset.getString("TXN_CURRENCY"));
        generateBean.setReason(rset.getString("REASON"));
        generateBean.setDocumentation(rset.getString("DOCUMENTATION"));
        generateBean.setRemarks(rset.getString("REMARKS"));
        TTUM_Data.add(generateBean);
      } 
      Data.add(Excel_header);
      Data.add(TTUM_Data);
    } catch (Exception e) {
      System.out.println("EXCEPTION IN generateTTUMForAMEX " + e);
      this.logger.error(e.getMessage());
    } 
    return Data;
  }
  
  public String generateChargBk(String arn1, String reason, String arn_date) throws Exception {
    String filename = null;
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ChargeBackImpl chr = new ChargeBackImpl();
    String filepath = System.getProperty("catalina.home");
    StringBuilder sb = new StringBuilder();
    sb.append(
        " SELECT rw.pan, rw.processing_code, rw.amount_recon,   to_char(to_date(rw.DATE_VAL,'DDMMRRRR'),'MM/DD/RRRR') DATE_VAL  , rw.data_code,  rw.card_seq_num, rw.funcation_code, rw.msg_res_code, rw.card_acc_code,  rw.aquierer_ref_no, rw.fi_id_code, rw.approval_code, rw.service_code, rw.card_acc_id_code,  rw.currency_code_tran, rw.currency_code_recon, rw.tran_dest_id_code,  rw.tran_org_id_code, rw.card_iss_ref_data, rw.recv_inst_idcode,  rw.terminal_type, rw.elec_com_indic, rw.processing_mode,  rw.currency_exponent, rw.business_act, rw.settlement_ind,  rw.card_accp_name_loc, ch.ref_id, ch.settlement_amount,  ch.settlement_currency, ch.txn_amount, ch.txn_currency, ch.reason,  ch.documentation, ch.remarks  FROM mastercard_pos_rawdata rw JOIN mastercard_charge ch  ON rw.aquierer_ref_no = ch.microfilm  WHERE ch.microfilm in ( ");
    try {
      File f = new File(filepath);
      if (!f.isDirectory())
        if (f.mkdir())
          System.out.println("directory created");  
      OracleConn conn = new OracleConn();
      con = conn.getconn();
      String[] arns = { arn1 };
      int arn;
      for (arn = 0; arn < arns.length; arn++) {
        if (arn + 1 == arns.length) {
          sb.append(" ? ");
        } else {
          sb.append(" ?, ");
        } 
      } 
      sb.append(" ) ");
      pstmt = con.prepareStatement(sb.toString());
      for (arn = 0; arn < arns.length; arn++)
        pstmt.setString(arn + 1, arns[arn]); 
      rs = pstmt.executeQuery();
      filename = chr.createFile(new MasterCardModel(reason, arn_date, "Filename", filepath), rs);
      System.out.println("filename:::::::::::::::::::::::::::::::::::::::::::::::::::::" + filename);
    } catch (Exception e) {
      e.printStackTrace();
      return "Failed";
    } 
    return filename;
  }
  
  public List<Gl_bean> getMastercardGet_glbalance(String filedate) throws Exception {
    this.logger.info("***** SourceDAoImpl.getSubcategories Start ****");
    List<Gl_bean> SubCategories = new ArrayList<>();
    String sql = null;
    String sql_query = null;
    String sql_query2 = null;
    String sql_autorev = null;
    String sql_query_autorev = null;
    String sql_query2_autorev = null;
    String sql_lpcase = null;
    String sql_query_lpcase = null;
    String sql_settle = null;
    String sql_query_settle = null;
    String sql_settlement = null;
    String sql_query_settlement = null;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
    Date date1 = sdf.parse(filedate);
    sdf = new SimpleDateFormat("yyMMdd");
    System.out.println(sdf.format(date1));
    String dt = sdf.format(date1);
    Calendar c = Calendar.getInstance();
    try {
      c.setTime(sdf.parse(sdf.format(date1)));
    } catch (ParseException e) {
      e.printStackTrace();
    } 
    c.add(5, 1);
    String newDate = sdf.format(c.getTime());
    System.out.println("Date after Addition: " + newDate);
    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MMM/yyyy");
    Date date12 = sdf1.parse(filedate);
    System.out.println(sdf1.format(date12));
    Calendar c1 = Calendar.getInstance();
    try {
      c1.setTime(sdf1.parse(sdf1.format(date12)));
      c1.add(5, 1);
      String newDate12 = sdf1.format(c1.getTime());
      System.out.println("Date after Addition: " + newDate12);
      sdf = new SimpleDateFormat("dd-MM-YYYY");
      System.out.println(sdf.format(date1));
      String dt1 = sdf.format(date1);
      String sql1 = "select sum(os1.AMOUNT_equiv) as settlement_amount from SETTLEMENT_RUPAY_switch_bk os1 where os1.FILEDATE=? and os1.DCRS_REMARKS='RUPAY_DOM-MATCHED-2'";
      String sql2 = "select sum(os1.AMOUNT_SETTLEMENT) from SETTLEMENT_RUPAY_RUPAY_bk os1 where (os1.DATE_SETTLEMENT=?) and os1.DCRS_REMARKS='RUPAY_DOM-UNRECON-2' and os1.FILEDATE=? and os1.TXNFUNCTION_CODE='200'";
      String sql3 = "select sum(os1.DIFF_AMOUNT) from SETTLEMENT_RUPAY_switch_bk os1 where os1.FILEDATE=? and os1.DCRS_REMARKS like '%RUPAY_SUR%'";
      String sql4 = "select sum(os1.AMOUNT_SETTLEMENT ) from SETTLEMENT_RUPAY_RUPAY_bk os1 where   os1.DCRS_REMARKS='RUPAY_DOM-UNRECON-2' and os1.FILEDATE=? and os1.TXNFUNCTION_CODE ='200' and os1.DATE_SETTLEMENT=?";
      String sql5 = "select sum(os1.AMOUNT_EQUIV) from SETTLEMENT_RUPAY_switch_bk os1 where os1.FILEDATE=? and os1.DCRS_REMARKS='RUPAY_DOM-UNRECON-2'";
      String sql6 = "select sum((replace(os1.AMOUNT,',','' ))) from SETTLEMENT_RUPAY_CBS_bk os1 where os1.FILEDATE=? and os1.DCRS_REMARKS like '%RUPAY_DOM-UNRECON-1%'  and os1.E='C' and  os1.PSTD_USER_ID='CDCIADM'";
      String sql7 = "select sum((replace(os1.AMOUNT,',','' ))) from SETTLEMENT_RUPAY_CBS_bk os1 where os1.FILEDATE=? and os1.DCRS_REMARKS like '%RUPAY_DOM-UNRECON-1%'  and os1.E='D' and  os1.PSTD_USER_ID='CDCIADM' AND OS1.VALUE_DATE=?";
      String settlenmnt_match = (String)getJdbcTemplate().queryForObject(sql1, new Object[] { filedate }, String.class);
      String nxtdt = (String)getJdbcTemplate().queryForObject(sql2, new Object[] { newDate, filedate }, String.class);
      String surcharge = (String)getJdbcTemplate().queryForObject(sql3, new Object[] { filedate }, String.class);
      String lp_case = (String)getJdbcTemplate().queryForObject(sql4, new Object[] { newDate12, dt }, String.class);
      String switch_unrecon = (String)getJdbcTemplate().queryForObject(sql5, new Object[] { filedate }, String.class);
      String cbs_unrecon = (String)getJdbcTemplate().queryForObject(sql6, new Object[] { filedate }, String.class);
      String auto_rev = (String)getJdbcTemplate().queryForObject(sql7, new Object[] { newDate12, dt1 }, String.class);
      String countval = "select count(*) from RUPAY_GL_CHARGEBK os1 where os1.SURHCRAGE_AMOUNT=? and os1.filedate=?";
      String status = "select count(*) from SETTLEMENT_RUPAY_CBS_bk os1 where os1.FILEDATE=?  AND replace(os1.AMOUNT,',','' ) LIKE ? and os1.E=?";
      String surchargecount = (String)getJdbcTemplate().queryForObject(countval, 
          new Object[] { surcharge, filedate }, String.class);
      String surchargecount1 = (String)getJdbcTemplate().queryForObject(status, 
          new Object[] { filedate, "%" + surcharge + "%", "C" }, String.class);
      if (surchargecount.equalsIgnoreCase("0")) {
        sql = "INSERT into RUPAY_GL_CHARGEBK (SURHCRAGE_AMOUNT, FILEDATE, STATUS) VALUES (?, ?, ?)";
        getJdbcTemplate().update(sql, new Object[] { surcharge, filedate, "N" });
      } else {
        sql = "update RUPAY_GL_CHARGEBK os1 set os1.SURHCRAGE_AMOUNT=?,os1.FILEDATE=?,os1.status=? where  os1.SURHCRAGE_AMOUNT=? and os1.filedate=?";
        getJdbcTemplate().update(sql, new Object[] { surcharge, filedate, "N", surcharge, filedate });
      } 
      if (!surchargecount1.equals("0")) {
        sql_query = "update RUPAY_GL_CHARGEBK os1 set os1.status=? where  os1.SURHCRAGE_AMOUNT=? and os1.filedate=?";
        getJdbcTemplate().update(sql_query, new Object[] { "Y", surcharge, filedate });
      } 
      String countval1_autorev = "select count(*) from RUPAY_GL_autorev os1 where os1.AMOUNT=? and os1.filedate=?";
      String status_autorev = "select count(*) from SETTLEMENT_RUPAY_CBS_bk os1 where os1.FILEDATE=?  AND replace(os1.AMOUNT,',','' ) LIKE ? and os1.E=?";
      String surchargecount_autorev = (String)getJdbcTemplate().queryForObject(countval1_autorev, 
          new Object[] { auto_rev, filedate }, String.class);
      String surchargecount1_autorev = (String)getJdbcTemplate().queryForObject(status_autorev, 
          new Object[] { filedate, "%" + auto_rev + "%", "C" }, String.class);
      if (surchargecount_autorev.equalsIgnoreCase("0")) {
        sql_autorev = "INSERT into RUPAY_GL_autorev (AMOUNT, FILEDATE, STATUS) VALUES (?, ?, ?)";
        getJdbcTemplate().update(sql_autorev, new Object[] { auto_rev, filedate, "N" });
      } else {
        sql_autorev = "update RUPAY_GL_autorev os1 set os1.AMOUNT=?,os1.FILEDATE=?,os1.status=? where  os1.AMOUNT=? and os1.filedate=?";
        getJdbcTemplate().update(sql_autorev, new Object[] { auto_rev, filedate, "N", auto_rev, filedate });
      } 
      if (!surchargecount1_autorev.equals("0")) {
        sql_query_autorev = "update RUPAY_GL_autorev os1 set os1.status=? where  os1.AMOUNT=? and os1.filedate=?";
        getJdbcTemplate().update(sql_query_autorev, new Object[] { "Y", auto_rev, filedate });
      } 
      String countval1_lpcases = "select count(*) from RUPAY_GL_LPCASES os1 where os1.AMOUNT=? and os1.filedate=?";
      String status_lpcase = "select count(*) from SETTLEMENT_RUPAY_CBS_bk os1 where os1.FILEDATE=?  AND replace(os1.AMOUNT,',','' ) LIKE ? and os1.E=?";
      String surchargecount_lpcase = (String)getJdbcTemplate().queryForObject(countval1_lpcases, 
          new Object[] { lp_case, filedate }, String.class);
      String surchargecount1_lpcase = (String)getJdbcTemplate().queryForObject(status_lpcase, 
          new Object[] { filedate, "%" + lp_case + "%", "C" }, String.class);
      if (surchargecount_lpcase.equalsIgnoreCase("0")) {
        sql_lpcase = "INSERT into RUPAY_GL_LPCASES (AMOUNT, FILEDATE, STATUS) VALUES (?, ?, ?)";
        getJdbcTemplate().update(sql_lpcase, new Object[] { lp_case, filedate, "N" });
      } else {
        sql_lpcase = "update RUPAY_GL_LPCASES os1 set os1.AMOUNT=?,os1.FILEDATE=?,os1.status=? where  os1.AMOUNT=? and os1.filedate=?";
        getJdbcTemplate().update(sql_lpcase, new Object[] { lp_case, filedate, "N", lp_case, filedate });
      } 
      if (!surchargecount1_autorev.equals("0")) {
        sql_query_lpcase = "update RUPAY_GL_LPCASES os1 set os1.status=? where  os1.AMOUNT=? and os1.filedate=?";
        getJdbcTemplate().update(sql_query_lpcase, new Object[] { "Y", lp_case, filedate });
      } 
      Gl_bean e = new Gl_bean();
      e.setSettlement_matched(settlenmnt_match);
      e.setNxtdate(nxtdt);
      e.setSurcharge(surcharge);
      e.setLpcase(lp_case);
      e.setSwitch_unrecon(switch_unrecon);
      e.setCbs_unrecon(cbs_unrecon);
      SubCategories.add(e);
    } catch (ParseException e) {
      e.printStackTrace();
    } 
    return SubCategories;
  }
  
  public List<Rupay_sur_GlBean> getRupaysurchargelist(String filedate) throws Exception {
    String query = "select * from RUPAY_GL_CHARGEBK os1 where os1.STATUS='N'";
    System.out.println(query);
    List<Rupay_sur_GlBean> typeList = getJdbcTemplate().query(query, 
        (RowMapper)new BeanPropertyRowMapper(Rupay_sur_GlBean.class));
    return typeList;
  }
  
  public List<Rupay_gl_autorev> getRupayAutorevlist(String filedate) throws Exception {
    String query = "select * from RUPAY_GL_autorev os1 where os1.STATUS='N'";
    System.out.println(query);
    List<Rupay_gl_autorev> typeList = getJdbcTemplate().query(query, 
        (RowMapper)new BeanPropertyRowMapper(Rupay_gl_autorev.class));
    return typeList;
  }
  
  public List<Rupay_gl_Lpcases> getRupayLpcaselist(String filedate) throws Exception {
    String query = "select * from RUPAY_GL_LPCASES os1 where os1.STATUS='N'";
    System.out.println(query);
    List<Rupay_gl_Lpcases> typeList = getJdbcTemplate().query(query, 
        (RowMapper)new BeanPropertyRowMapper(Rupay_gl_Lpcases.class));
    return typeList;
  }
  
  public int SaveGl(String closingbal, String settlementid, String diff, String cbs_unrecon, String switch_unrecon, String nobase2, String settlementmatch, String nxtdate, String surcharge1, String settlementTotal, String lpcase2, String surcharge, String surch_total, String autorev, String autorev_total, String lpcase, String lpcasetotal, String finaltotal, String dateval) throws Exception {
    String query = "insert into rupay_gl_report values( '" + closingbal + "','" + settlementid + "','" + diff + 
      "','" + cbs_unrecon + "','" + switch_unrecon + "'" + ",'" + nobase2 + "','" + settlementmatch + "','" + 
      nxtdate + "','" + lpcase2 + "','" + surcharge1 + "','" + settlementTotal + "','" + surcharge + "','" + 
      surch_total + "','" + autorev + "'," + "'" + autorev_total + "','" + lpcase + "','" + lpcasetotal + 
      "','" + finaltotal + "','" + dateval + "')";
    return getJdbcTemplate().update(query);
  }
  
  public List<List<Rupay_Gl_repo>> GenerateGL(String dateval) throws Exception {
    List<List<Rupay_Gl_repo>> Data = new ArrayList<>();
    List<Rupay_Gl_repo> TTUM_Data = new ArrayList<>();
    List<String> ExcelHeaders = new ArrayList<>();
    List<Rupay_Gl_repo> Excel_header = new ArrayList<>();
    try {
      String GET_DATA = null;
      GET_DATA = "select * from rupay_gl_report os1 where os1.FILEDATE='" + dateval + "'";
      System.out.println(GET_DATA);
      Connection conn = getConnection();
      PreparedStatement pstmt = conn.prepareStatement(GET_DATA);
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        Rupay_Gl_repo generateBean = new Rupay_Gl_repo();
        generateBean.setClosingbal(rset.getString("CLOSINGBAL"));
        generateBean.setSettlementid(rset.getString("SETTLEMENTID"));
        generateBean.setDiff(rset.getString("DIFF"));
        generateBean.setCbs_unrecon(rset.getString("CBS_UNRECON"));
        generateBean.setSwitch_unrecon(rset.getString("SWITCH_UNRECON"));
        generateBean.setNobase2(rset.getString("NOBASE2"));
        generateBean.setSettlementmatch(rset.getString("SETTLEMENTMATCH"));
        generateBean.setNxtdate(rset.getString("NXTDATE"));
        generateBean.setSurcharge1(rset.getString("SURCHARGE1"));
        generateBean.setLpcase2(rset.getString("LPCASE2"));
        generateBean.setSettlementtotal(rset.getString("SETTLEMENTTOTAL"));
        generateBean.setSurcharge(rset.getString("SURCHARGE"));
        generateBean.setSurch_total(rset.getString("SURCH_TOTAL"));
        generateBean.setAutorev(rset.getString("AUTOREV"));
        generateBean.setAutorev_total(rset.getString("AUTOREV_TOTAL"));
        generateBean.setLpcase(rset.getString("LPCASE"));
        generateBean.setLpcasetotal(rset.getString("LPCASETOTAL"));
        generateBean.setFinaltotal(rset.getString("FINALTOTAL"));
        TTUM_Data.add(generateBean);
      } 
      Data.add(Excel_header);
      Data.add(TTUM_Data);
    } catch (Exception e) {
      System.out.println("EXCEPTION IN generateTTUMForAMEX " + e);
      this.logger.error(e.getMessage());
    } 
    return Data;
  }
  
  public String getSettlemntAmount(String settlementDate, String settlementAmount) throws Exception {
    String sql_settlement = null;
    String sql_query_settlement = null;
    String amount_val = null;
    try {
      String status_settlement = "select count(*) from SETTLEMENT_RUPAY_CBS_bk os1 where os1.FILEDATE=?  AND replace(os1.AMOUNT,',','' ) LIKE ? and os1.E=?";
      int surchargecount1_settlement = ((Integer)getJdbcTemplate().queryForObject(status_settlement, 
          new Object[] { settlementDate, "%" + settlementAmount + "%", "D" }, Integer.class)).intValue();
      String countval1_settlement = "select count(*) from RUPAY_GL_SETTLEMENT os1 where os1.AMOUNT=? and os1.filedate=?";
      String surchargecount_settlement = (String)getJdbcTemplate().queryForObject(countval1_settlement, 
          new Object[] { settlementAmount, settlementDate }, String.class);
      String query = "select count(*) from SETTLEMENT_RUPAY_CBS_bk os1 where os1.FILEDATE='" + settlementDate + 
        "'  AND replace(os1.AMOUNT,',','' ) LIKE '%" + settlementAmount + "%' and os1.E='D'";
      if (surchargecount_settlement.equalsIgnoreCase("0")) {
        sql_settlement = "INSERT into RUPAY_GL_SETTLEMENT (AMOUNT, FILEDATE, STATUS) VALUES (?, ?, ?)";
        getJdbcTemplate().update(sql_settlement, new Object[] { settlementAmount, settlementDate, "N" });
      } else {
        sql_settlement = "update RUPAY_GL_SETTLEMENT os1 set os1.AMOUNT=?,os1.FILEDATE=?,os1.status=? where  os1.AMOUNT=? and os1.filedate=?";
        getJdbcTemplate().update(sql_settlement, 
            new Object[] { settlementAmount, settlementDate, "N", amount_val, settlementDate });
      } 
      if (surchargecount1_settlement != 0) {
        sql_query_settlement = "update RUPAY_GL_SETTLEMENT os1 set os1.status=? where  os1.AMOUNT=? and os1.filedate=?";
        getJdbcTemplate().update(sql_query_settlement, new Object[] { "Y", settlementAmount, settlementDate });
        return "Data already exist";
      } 
    } catch (Exception e) {
      e.printStackTrace();
      return "Failed";
    } 
    return "Not exist";
  }
  
  public ArrayList<String> getFisdomColumnList(String tableName) {
	return null;}
  
  public boolean checkNcreateIccwFolder(CompareSetupBean settlementBeanObj, List<String> stFileNames) {
    try {
      File directory = new File(settlementBeanObj.getStPath());
      if (!directory.exists())
        directory.mkdirs(); 
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      Date date = sdf.parse(settlementBeanObj.getFileDate());
      sdf = new SimpleDateFormat("dd-MMM-yyyy");
      String stnewDate = sdf.format(date);
      String stnewPath = String.valueOf(settlementBeanObj.getStPath()) + File.separator + stnewDate;
      directory = new File(stnewPath);
      if (!directory.exists())
        directory.mkdirs(); 
      this.logger.info(directory.listFiles() + " size is " + (directory.listFiles()).length);
      stnewPath = String.valueOf(settlementBeanObj.getStPath()) + File.separator + stnewDate + File.separator + 
        settlementBeanObj.getStMergerCategory();
      if (directory.listFiles() != null && (directory.listFiles()).length > 0) {
        stnewPath = String.valueOf(stnewPath) + "_" + ((directory.listFiles()).length + 1);
        this.logger.info("stnewPath is " + stnewPath);
      } else {
        stnewPath = String.valueOf(stnewPath) + "_" + (directory.listFiles()).length;
        this.logger.info("stnewPath is " + stnewPath);
      } 
      settlementBeanObj.setStPath(stnewPath);
      directory = new File(stnewPath);
      if (!directory.exists())
        directory.mkdirs(); 
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } 
    return true;
  }
  
  public void generate_Reports_ICCW(CompareSetupBean settlementBeanObj) throws Exception {}
}
