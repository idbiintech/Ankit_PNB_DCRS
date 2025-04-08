package com.recon.dao.impl;


import com.recon.dao.GenerateRupayTTUMDao;
import com.recon.model.CompareBean;
import com.recon.model.GenerateTTUMBean;
import com.recon.model.KnockOffBean;
import com.recon.util.demo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

@Component
public class GenerateRupayTTUMDaoImpl extends JdbcDaoSupport implements GenerateRupayTTUMDao {
  public void getTTUMSwitchRecords(GenerateTTUMBean generateTTUMBeanObj) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.getTTUMSwitchRecords Start ****");
    try {
      if (generateTTUMBeanObj.getStDate() != null && !generateTTUMBeanObj.getStDate().equals("")) {
        String TTUM_RECORDS = "UPDATE SETTLEMENT_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + 
          " SET DCRS_REMARKS = '" + generateTTUMBeanObj.getStMerger_Category() + 
          "-UNRECON-GENERATE-TTUM-1' WHERE DCRS_REMARKS = '" + 
          generateTTUMBeanObj.getStMerger_Category() + 
          "-UNRECON-1' AND TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStDate() + "'" + 
          " AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStFile_Date() + "'";
        this.logger.info("TTUM_RECORDS " + TTUM_RECORDS);
        getJdbcTemplate().execute(TTUM_RECORDS);
      } else {
        String TTUM_RECORDS = "UPDATE SETTLEMENT_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + 
          " SET DCRS_REMARKS = '" + generateTTUMBeanObj.getStMerger_Category() + 
          "-UNRECON-GENERATE-TTUM-1' WHERE DCRS_REMARKS = '" + 
          generateTTUMBeanObj.getStMerger_Category() + 
          
          "-UNRECON-1' AND TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DD/MM/YYYY') = TO_CHAR(" + generateTTUMBeanObj.getStFile_Date() + "-4,'DD/MM/YYYY')" + 
          " AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStFile_Date() + "' ";
        this.logger.info("TTUM_RECORDS " + TTUM_RECORDS);
        getJdbcTemplate().execute(TTUM_RECORDS);
      } 
      this.logger.info("***** GenerateRupayTTUMDaoImpl.getTTUMSwitchRecords End ****");
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.getTTUMSwitchRecords");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.getTTUMSwitchRecords", new Exception("GenerateRupayTTUMDaoImpl.getTTUMSwitchRecords", e));
      throw e;
    } 
  }
  
  public List<List<GenerateTTUMBean>> generateSwitchTTUM(GenerateTTUMBean generateTTUMBean, int inRec_Set_Id) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.generateSwitchTTUM Start ****");
    List<GenerateTTUMBean> ttum_data = new ArrayList<>();
    List<GenerateTTUMBean> Excel_headers = new ArrayList<>();
    List<String> ExcelHeaders = new ArrayList<>();
    List<String> ExcelHeaders1 = new ArrayList<>();
    List<List<GenerateTTUMBean>> Total_Data = new ArrayList<>();
    String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
    String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
    String stAction = "";
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try {
      Date varDate = null;
      SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
      try {
        varDate = dateFormat.parse(generateTTUMBean.getStDate());
        dateFormat = new SimpleDateFormat("ddMMyy");
        this.logger.info("Date :" + dateFormat.format(varDate));
      } catch (Exception e) {
        demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.generateSwitchTTUM");
        this.logger.error(" error in GenerateRupayTTUMDaoImpl.generateSwitchTTUM", new Exception("GenerateRupayTTUMDaoImpl.generateSwitchTTUM", e));
        throw e;
      } 
      if (generateTTUMBean.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
        this.logger.info("*** In DOMESTIC ***");
        generateTTUMBean.setStGLAccount("99937200010660");
        String GET_TTUM_RECORDS = "SELECT ACCTNUM,AMOUNT,PAN,TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DDMMYY') AS LOCAL_DATE,SUBSTR(TRACE,-6,6) AS TRACE , ACCEPTORNAME  FROM SETTLEMENT_" + 
          generateTTUMBean.getStCategory() + "_" + generateTTUMBean.getStFile_Name() + 
          " WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM-2%'";
        this.logger.info("GET_TTUM_RECORDS==" + GET_TTUM_RECORDS);
        conn = getConnection();
        pstmt = conn.prepareStatement(GET_TTUM_RECORDS);
        rset = pstmt.executeQuery();
        ExcelHeaders.add("ACCOUNT NUMBER");
        ExcelHeaders.add("CURRENCY CODE");
        ExcelHeaders.add("SERVICE OUTLET");
        ExcelHeaders.add("PART TRAN TYPE");
        ExcelHeaders.add("TRANSACTION AMOUNT");
        ExcelHeaders.add("TRANSACTION PARTICULARS");
        ExcelHeaders.add("REFERENCE CURRENCY CODE");
        ExcelHeaders.add("REFERENCE AMOUNT");
        ExcelHeaders.add("REMARKS");
        ExcelHeaders.add("REFERENCE NUMBER");
        ExcelHeaders.add("ACCOUNT REPORT CODE");
        ExcelHeaders1.add("ACCOUNT_NUMBER");
        ExcelHeaders1.add("CURRENCY_CODE");
        ExcelHeaders1.add("SERVICE_OUTLET");
        ExcelHeaders1.add("PART_TRAN_TYPE");
        ExcelHeaders1.add("TRANSACTION_AMOUNT");
        ExcelHeaders1.add("TRANSACTION_PARTICULARS");
        ExcelHeaders1.add("REFERENCE_CURRENCY_CODE");
        ExcelHeaders1.add("REFERENCE_AMOUNT");
        ExcelHeaders1.add("REMARKS");
        ExcelHeaders1.add("REFERENCE_NUMBER");
        ExcelHeaders1.add("ACCOUNT_REPORT_CODE");
        generateTTUMBean.setStExcelHeader(ExcelHeaders);
        Excel_headers.add(generateTTUMBean);
        int count = 0;
        while (rset.next()) {
          count++;
          GenerateTTUMBean generateTTUMBeanObj = new GenerateTTUMBean();
          if (inRec_Set_Id == 1 || inRec_Set_Id == 3) {
            generateTTUMBeanObj.setStDebitAcc(rset.getString("ACCTNUM").replaceAll(" ", " ").replaceAll("^0*", ""));
            generateTTUMBeanObj.setStCreditAcc("99937200010660");
            generateTTUMBeanObj.setStAmount(rset.getString("AMOUNT"));
            String stTran_particulars = "DR-RPAY-" + rset.getString("LOCAL_DATE") + "-" + rset.getString("TRACE") + "-" + rset.getString("ACCEPTORNAME").replaceAll("'", "");
            generateTTUMBeanObj.setStDate(rset.getString("LOCAL_DATE"));
            generateTTUMBeanObj.setStTran_particulars(stTran_particulars);
            generateTTUMBeanObj.setStCard_Number(rset.getString("PAN"));
            String remark = (String)getJdbcTemplate().queryForObject("select 'RPYD'||'" + dateFormat.format(varDate) + "'||ttum_seq.nextval from dual", new Object[0], String.class);
            generateTTUMBeanObj.setStRemark(remark);
          } else if (inRec_Set_Id == 2) {
            generateTTUMBeanObj.setStCreditAcc(rset.getString("ACCTNUM").replaceAll(" ", " ").replaceAll("^0*", ""));
            generateTTUMBeanObj.setStDebitAcc("99937200010660");
            generateTTUMBeanObj.setStAmount(rset.getString("AMOUNT"));
            String stTran_particulars = "REV-T10-RPAY-" + rset.getString("LOCAL_DATE") + "-" + rset.getString("TRACE") + "-" + rset.getString("ACCEPTORNAME").replaceAll("'", "");
            generateTTUMBeanObj.setStDate(rset.getString("LOCAL_DATE"));
            generateTTUMBeanObj.setStTran_particulars(stTran_particulars);
            generateTTUMBeanObj.setStCard_Number(rset.getString("PAN"));
            String remark = (String)getJdbcTemplate().queryForObject("select 'RPYD'||'" + dateFormat.format(varDate) + "'||ttum_seq.nextval from dual", new Object[0], String.class);
            generateTTUMBeanObj.setStRemark(remark);
          } 
          ttum_data.add(generateTTUMBeanObj);
        } 
        Total_Data.add(Excel_headers);
        Total_Data.add(ttum_data);
      } else if (generateTTUMBean.getStSubCategory().equalsIgnoreCase("INTERNATIONAL")) {
        generateTTUMBean.setStGLAccount("99937200010663");
        String GET_TTUM_RECORDS = "SELECT PAN,ACCTNUM,AMOUNT,ISSUER,TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DDMMYY') AS LOCAL_DATE,SUBSTR(TRACE,-6,6) AS TRACE,ACCEPTORNAME FROM SETTLEMENT_" + 
          generateTTUMBean.getStCategory() + "_" + generateTTUMBean.getStFile_Name() + " WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%'";
        conn = getConnection();
        pstmt = conn.prepareStatement(GET_TTUM_RECORDS);
        rset = pstmt.executeQuery();
        ExcelHeaders.add("ACCOUNT NUMBER");
        ExcelHeaders.add("CURRENCY CODE");
        ExcelHeaders.add("SERVICE OUTLET");
        ExcelHeaders.add("PART TRAN TYPE");
        ExcelHeaders.add("TRANSACTION AMOUNT");
        ExcelHeaders.add("TRANSACTION PARTICULARS");
        ExcelHeaders.add("REFERENCE CURRENCY CODE");
        ExcelHeaders.add("REFERENCE AMOUNT");
        ExcelHeaders.add("REMARKS");
        ExcelHeaders.add("REFERENCE NUMBER");
        ExcelHeaders.add("ACCOUNT REPORT CODE");
        generateTTUMBean.setStExcelHeader(ExcelHeaders);
        Excel_headers.add(generateTTUMBean);
        while (rset.next()) {
          GenerateTTUMBean generateTTUMBeanObj = new GenerateTTUMBean();
          if (inRec_Set_Id == 1 || inRec_Set_Id == 3) {
            generateTTUMBeanObj.setStDebitAcc(rset.getString("ACCTNUM").replaceAll(" ", " ").replaceAll("^0*", ""));
            generateTTUMBeanObj.setStCreditAcc("99937200010663");
            generateTTUMBeanObj.setStAmount(rset.getString("AMOUNT"));
            String stTran_particulars = "REV-RPAY-" + rset.getString("LOCAL_DATE") + "/" + rset.getString("TRACE") + "-" + rset.getString("ACCEPTORNAME");
            String stRecords_Date = rset.getString("LOCAL_DATE");
            generateTTUMBeanObj.setStTran_particulars(stTran_particulars);
            generateTTUMBeanObj.setStCard_Number(rset.getString("PAN"));
            String remark = (String)getJdbcTemplate().queryForObject("select 'RPYI'||'" + dateFormat.format(varDate) + "'||ttum_seq.nextval from dual", new Object[0], String.class);
            generateTTUMBeanObj.setStRemark(remark);
            generateTTUMBeanObj.setAccount_repo("");
          } else if (inRec_Set_Id == 2) {
            generateTTUMBeanObj.setStCreditAcc(rset.getString("ACCTNUM").replaceAll(" ", " ").replaceAll("^0*", ""));
            generateTTUMBeanObj.setStDebitAcc("99937200010660");
            generateTTUMBeanObj.setStAmount(rset.getString("AMOUNT"));
            String stTran_particulars = "REV-T10-RPAY-" + rset.getString("LOCAL_DATE") + "/" + rset.getString("TRACE") + "-" + rset.getString("ACCEPTORNAME");
            String stRecords_Date = rset.getString("LOCAL_DATE");
            generateTTUMBeanObj.setStTran_particulars(stTran_particulars);
            generateTTUMBeanObj.setStCard_Number(rset.getString("PAN"));
            String remark = (String)getJdbcTemplate().queryForObject("select 'RPYI'||'" + dateFormat.format(varDate) + "'||ttum_seq.nextval from dual", new Object[0], String.class);
            generateTTUMBeanObj.setStRemark(remark);
            generateTTUMBeanObj.setAccount_repo("");
          } 
          ttum_data.add(generateTTUMBeanObj);
        } 
        Total_Data.add(Excel_headers);
        Total_Data.add(ttum_data);
      } 
      for (int i = 0; i < ExcelHeaders1.size(); i++) {
        table_cols = String.valueOf(table_cols) + "," + (String)ExcelHeaders1.get(i) + " VARCHAR (100 BYTE)";
        insert_cols = String.valueOf(insert_cols) + "," + (String)ExcelHeaders1.get(i);
      } 
      String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_" + generateTTUMBean.getStMerger_Category() + 
        "_" + generateTTUMBean.getStFile_Name() + "'";
      this.logger.info("CHECK_TABLE==" + CHECK_TABLE);
      int tableExist = ((Integer)getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[0], Integer.class)).intValue();
      this.logger.info("tableExist==" + tableExist);
      if (tableExist == 0) {
        String CREATE_QUERY = "CREATE TABLE TTUM_" + generateTTUMBean.getStMerger_Category() + 
          "_" + generateTTUMBean.getStFile_Name() + " (" + table_cols + ")";
        this.logger.info("CREATE_QUERY==" + CREATE_QUERY);
        getJdbcTemplate().execute(CREATE_QUERY);
      } 
      int incount = 0;
      for (int j = 0; j < ttum_data.size(); j++) {
        incount++;
        GenerateTTUMBean beanObj = new GenerateTTUMBean();
        beanObj = ttum_data.get(j);
        String INSERT_QUERY = "INSERT INTO TTUM_" + generateTTUMBean.getStMerger_Category() + 
          "_" + generateTTUMBean.getStFile_Name() + 
          "(" + insert_cols + ") VALUES ('" + generateTTUMBean.getStMerger_Category() + 
          "-UNRECON-TTUM-" + inRec_Set_Id + 
          "',SYSDATE,'" + generateTTUMBean.getStEntry_by() + "',TO_DATE('" + beanObj.getStDate() + "','DDMMYY'),'" + beanObj.getStDebitAcc() + "','INR','999','D','" + 
          beanObj.getStAmount() + "','" + beanObj.getStTran_particulars() + "','" + beanObj.getStCard_Number() + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "','" + beanObj.getAccount_repo() + "')";
        this.logger.info("INSERT_QUERY==" + INSERT_QUERY);
        getJdbcTemplate().execute(INSERT_QUERY);
        INSERT_QUERY = "INSERT INTO TTUM_" + generateTTUMBean.getStMerger_Category() + 
          "_" + generateTTUMBean.getStFile_Name() + 
          "(" + insert_cols + ") VALUES ('" + generateTTUMBean.getStMerger_Category() + 
          "-UNRECON-TTUM-" + inRec_Set_Id + 
          "',SYSDATE,'" + generateTTUMBean.getStEntry_by() + "',TO_DATE('" + beanObj.getStDate() + "','DDMMYY'),'" + beanObj.getStCreditAcc() + "','INR','999','C','" + 
          beanObj.getStAmount() + "','" + beanObj.getStTran_particulars() + "','" + beanObj.getStCard_Number() + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "','" + beanObj.getAccount_repo() + "')";
        this.logger.info("INSERT_QUERY==" + INSERT_QUERY);
        getJdbcTemplate().execute(INSERT_QUERY);
      } 
      String UPDATE_RECORDS = "UPDATE SETTLEMENT_" + generateTTUMBean.getStCategory() + "_" + generateTTUMBean.getStFile_Name() + 
        " SET DCRS_REMARKS = '" + generateTTUMBean.getStMerger_Category() + "-UNRECON-GENERATED-TTUM-" + inRec_Set_Id + "'" + 
        " WHERE DCRS_REMARKS = '" + generateTTUMBean.getStMerger_Category() + "-UNRECON-GENERATE-TTUM-" + inRec_Set_Id + "'";
      this.logger.info("UPDATE_RECORDS==" + UPDATE_RECORDS);
      getJdbcTemplate().execute(UPDATE_RECORDS);
      this.logger.info("***** GenerateRupayTTUMDaoImpl.generateSwitchTTUM End ****");
      return Total_Data;
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.generateSwitchTTUM");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.generateSwitchTTUM", new Exception("GenerateRupayTTUMDaoImpl.generateSwitchTTUM", e));
      throw e;
    } finally {
      if (rset != null)
        rset.close(); 
      if (pstmt != null)
        pstmt.close(); 
      if (conn != null)
        conn.close(); 
    } 
  }
  
  public void TTUMRecords(GenerateTTUMBean generateTTUMBeanObj) throws Exception {}
  
  public void getFailedRecords(String QUERY, int inUpdate_File_Id, GenerateTTUMBean generateTTUMBeanObj) throws Exception {}
  
  public List<List<GenerateTTUMBean>> generateCBSTTUM(GenerateTTUMBean generateTTUMBeanObj, List<GenerateTTUMBean> Diff_Data) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.generateCBSTTUM Start ****");
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    String stAction = "";
    List<List<GenerateTTUMBean>> Data = new ArrayList<>();
    List<GenerateTTUMBean> ttum_data = new ArrayList<>();
    List<GenerateTTUMBean> Excel_headers = new ArrayList<>();
    List<String> Headers = new ArrayList<>();
    String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
    String insert_cols = "DCRS_REMARKS,CREATEDDATE, CREATEDBY,RECORDS_DATE";
    try {
      Headers.add("ACCOUNT NUMBER");
      Headers.add("CURRENCY CODE");
      Headers.add("SERVICE OUTLET");
      Headers.add("PART TRAN TYPE");
      Headers.add("TRANSACTION AMOUNT");
      Headers.add("TRANSACTION PARTICULARS");
      Headers.add("REFERENCE CURRENCY CODE");
      Headers.add("REFERENCE AMOUNT");
      Headers.add("REMARKS");
      Headers.add("REFERENCE NUMBER");
      Headers.add("ACCOUNT REPORT CODE");
      generateTTUMBeanObj.setStExcelHeader(Headers);
      Excel_headers.add(generateTTUMBeanObj);
      String GET_TTUM_DATA = "SELECT E,CONTRA_ACCOUNT,FORACID,TO_NUMBER(AMOUNT,999999999.99) AS AMOUNT, TO_CHAR(TO_DATE(VALUE_DATE,'DD/MM/YYYY'),'DDMMYY') AS VALUE_DATE,SUBSTR(REF_NO,2,6) AS REF_NO, REMARKS,PARTICULARALS FROM SETTLEMENT_" + 
        generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + 
        " WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM-" + generateTTUMBeanObj.getInRec_Set_Id() + "%' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStFile_Date() + "'";
      this.logger.info("GET_TTUM_dATA " + GET_TTUM_DATA);
      conn = getConnection();
      pstmt = conn.prepareStatement(GET_TTUM_DATA);
      rset = pstmt.executeQuery();
      while (rset.next()) {
        stAction = rset.getString("E");
        GenerateTTUMBean generateTTUMBean = new GenerateTTUMBean();
        if (stAction.equals("C")) {
          generateTTUMBean.setStDebitAcc(rset.getString("FORACID"));
          generateTTUMBean.setStCreditAcc(rset.getString("CONTRA_ACCOUNT"));
          generateTTUMBean.setStAmount(rset.getString("AMOUNT"));
          String stTran_particular = "REV-T2-RPAY-" + rset.getString("VALUE_DATE") + "-" + rset.getString("REF_NO") + "-" + rset.getString("PARTICULARALS");
          generateTTUMBean.setStDate(rset.getString("VALUE_DATE"));
          generateTTUMBean.setStTran_particulars(stTran_particular);
          generateTTUMBean.setStCard_Number(rset.getString("REMARKS"));
          String remark = (String)getJdbcTemplate().queryForObject("select 'DCRM'||ttum_seq.nextval from dual", new Object[0], String.class);
          generateTTUMBean.setStRemark(remark);
          ttum_data.add(generateTTUMBean);
          Diff_Data.add(generateTTUMBean);
          continue;
        } 
        if (stAction.equals("D")) {
          generateTTUMBean.setStCreditAcc(rset.getString("FORACID"));
          generateTTUMBean.setStDebitAcc(rset.getString("CONTRA_ACCOUNT"));
          generateTTUMBean.setStAmount(rset.getString("AMOUNT"));
          String stTran_particular = "ADR-RPAY-" + rset.getString("VALUE_DATE") + "-" + rset.getString("REF_NO") + "-" + rset.getString("PARTICULARALS");
          generateTTUMBean.setStDate(rset.getString("VALUE_DATE"));
          generateTTUMBean.setStTran_particulars(stTran_particular);
          generateTTUMBean.setStCard_Number(rset.getString("REMARKS"));
          String remark = (String)getJdbcTemplate().queryForObject("select 'DCRM'||ttum_seq.nextval from dual", new Object[0], String.class);
          generateTTUMBean.setStRemark(remark);
          ttum_data.add(generateTTUMBean);
          Diff_Data.add(generateTTUMBean);
        } 
      } 
      Data.add(Excel_headers);
      Data.add(Diff_Data);
      for (int i = 0; i < Headers.size(); i++) {
        table_cols = String.valueOf(table_cols) + "," + (String)Headers.get(i) + " VARCHAR (100 BYTE)";
        insert_cols = String.valueOf(insert_cols) + "," + (String)Headers.get(i);
      } 
      String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
        "_" + generateTTUMBeanObj.getStFile_Name() + "'";
      this.logger.info("CHECK_TABLE==" + CHECK_TABLE);
      int tableExist = ((Integer)getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[0], Integer.class)).intValue();
      this.logger.info("tableExist==" + tableExist);
      if (tableExist == 0) {
        String CREATE_QUERY = "CREATE TABLE TTUM_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "_" + generateTTUMBeanObj.getStFile_Name() + " (" + table_cols + ")";
        this.logger.info("CREATE_QUERY==" + CREATE_QUERY);
        getJdbcTemplate().execute(CREATE_QUERY);
      } 
      for (int j = 0; j < ttum_data.size(); j++) {
        GenerateTTUMBean beanObj = new GenerateTTUMBean();
        beanObj = ttum_data.get(j);
        String INSERT_QUERY = "INSERT INTO TTUM_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "_" + generateTTUMBeanObj.getStFile_Name() + 
          "(" + insert_cols + ") VALUES ('" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-TTUM-" + generateTTUMBeanObj.getInRec_Set_Id() + 
          "',SYSDATE,'" + generateTTUMBeanObj.getStEntry_by() + "',TO_DATE('" + beanObj.getStDate() + "','DDMMYY'),'" + beanObj.getStDebitAcc() + "','INR','999','D','" + 
          beanObj.getStAmount() + "','" + beanObj.getStTran_particulars() + "','" + beanObj.getStCard_Number() + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "')";
        this.logger.info("INSERT_QUERY==" + INSERT_QUERY);
        getJdbcTemplate().execute(INSERT_QUERY);
        INSERT_QUERY = "INSERT INTO TTUM_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "_" + generateTTUMBeanObj.getStFile_Name() + 
          "(" + insert_cols + ") VALUES ('" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-TTUM-" + generateTTUMBeanObj.getInRec_Set_Id() + 
          "',SYSDATE,'" + generateTTUMBeanObj.getStEntry_by() + "',TO_DATE('" + beanObj.getStDate() + "','DDMMYY'),'" + beanObj.getStCreditAcc() + "','INR','999','C','" + 
          beanObj.getStAmount() + "','" + beanObj.getStTran_particulars() + "','" + beanObj.getStCard_Number() + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "')";
        this.logger.info("INSERT_QUERY==" + INSERT_QUERY);
        getJdbcTemplate().execute(INSERT_QUERY);
      } 
      String UPDATE_RECORDS = "UPDATE SETTLEMENT_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + 
        " SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'GENERATE','GENERATED')" + 
        " WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%'";
      this.logger.info("UPDATE_RECORDS==" + UPDATE_RECORDS);
      getJdbcTemplate().execute(UPDATE_RECORDS);
      this.logger.info("***** GenerateRupayTTUMDaoImpl.generateCBSTTUM End ****");
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.generateCBSTTUM");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.generateCBSTTUM", new Exception("GenerateRupayTTUMDaoImpl.generateCBSTTUM", e));
      throw e;
    } finally {
      if (rset != null)
        rset.close(); 
      if (pstmt != null)
        pstmt.close(); 
      if (conn != null)
        conn.close(); 
    } 
    return Data;
  }
  
  public List<List<GenerateTTUMBean>> generateCBSSurchargeTTUM(GenerateTTUMBean generateTTUMBeanObj) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.generateCBSSurchargeTTUM Start ****");
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    String stAction = "";
    List<List<GenerateTTUMBean>> Data = new ArrayList<>();
    List<GenerateTTUMBean> ttum_data = new ArrayList<>();
    List<GenerateTTUMBean> Excel_headers = new ArrayList<>();
    List<String> Headers = new ArrayList<>();
    String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE)";
    String insert_cols = "DCRS_REMARKS,CREATEDDATE, CREATEDBY";
    try {
      Headers.add("ACCOUNT_NUMBER");
      Headers.add("CURRENCY_CODE");
      Headers.add("SERVICE_OUTLET");
      Headers.add("PART_TRAN_TYPE");
      Headers.add("TRANSACTION_AMOUNT");
      Headers.add("TRANSACTION_PARTICULARS");
      Headers.add("REFERENCE_CURRENCY_CODE");
      Headers.add("REFERENCE_AMOUNT");
      Headers.add("REMARKS");
      Headers.add("REFERENCE_NUMBER");
      Headers.add("ACCOUNT_REPORT_CODE");
      generateTTUMBeanObj.setStExcelHeader(Headers);
      Excel_headers.add(generateTTUMBeanObj);
      String dateDiff = " Select to_date(MAX(FILEDATE),'DD/MM/RRRR')  - to_date('" + generateTTUMBeanObj.getStDate() + "', 'DD/MM/RRRR' )  from settlement_rupay_cbs where dcrs_remarks like '%" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + "%' ";
      conn = getConnection();
      pstmt = conn.prepareStatement(dateDiff);
      rset = pstmt.executeQuery();
      int datediff = 0;
      if (rset.next())
        datediff = rset.getInt(1); 
      conn = null;
      pstmt = null;
      rset = null;
      String GET_TTUM_DATA = "";
      if (datediff > 3) {
        GET_TTUM_DATA = "SELECT FORACID,TO_NUMBER(AMOUNT,999999999.99) AS AMOUNT,CONTRA_ACCOUNT, TO_CHAR(TO_DATE(VALUE_DATE,'DD/MM/YYYY'),'DDMMYY') AS VALUE_DATE,SUBSTR(REF_NO,2,6) AS REF_NO, REMARKS,PARTICULARALS FROM SETTLEMENT_" + 
          generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + "_BK" + 
          " WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStDate() + "'";
        this.logger.info("GET_TTUM_dATA " + GET_TTUM_DATA);
      } else {
        GET_TTUM_DATA = "SELECT FORACID,TO_NUMBER(AMOUNT,999999999.99) AS AMOUNT,CONTRA_ACCOUNT, TO_CHAR(TO_DATE(VALUE_DATE,'DD/MM/YYYY'),'DDMMYY') AS VALUE_DATE,SUBSTR(REF_NO,2,6) AS REF_NO, REMARKS,PARTICULARALS FROM SETTLEMENT_" + 
          generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + 
          " WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStDate() + "'";
        this.logger.info("GET_TTUM_dATA " + GET_TTUM_DATA);
      } 
      conn = getConnection();
      pstmt = conn.prepareStatement(GET_TTUM_DATA);
      rset = pstmt.executeQuery();
      while (rset.next()) {
        GenerateTTUMBean generateTTUMBean = new GenerateTTUMBean();
        generateTTUMBean.setStDebitAcc(rset.getString("FORACID"));
        generateTTUMBean.setStCreditAcc(rset.getString("CONTRA_ACCOUNT"));
        generateTTUMBean.setStAmount(rset.getString("AMOUNT"));
        String stDRTran_particular = "RPAY-SUR-" + rset.getString("VALUE_DATE") + "-" + rset.getString("REF_NO") + "-" + rset.getString("PARTICULARALS");
        generateTTUMBean.setStTran_particulars(stDRTran_particular);
        generateTTUMBean.setStCard_Number(rset.getString("REMARKS"));
        String remark = (String)getJdbcTemplate().queryForObject("select 'DCRM'||ttum_seq.nextval from dual", new Object[0], String.class);
        generateTTUMBean.setStRemark(remark);
        ttum_data.add(generateTTUMBean);
      } 
      Data.add(Excel_headers);
      Data.add(ttum_data);
      String UPDATE_RECORDS = "";
      if (datediff > 3) {
        UPDATE_RECORDS = "UPDATE SETTLEMENT_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + "_BK " + 
          " SET DCRS_REMARKS = '" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-GENERATED-TTUM-3'" + 
          " WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStDate() + "'";
      } else {
        UPDATE_RECORDS = "UPDATE SETTLEMENT_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + 
          " SET DCRS_REMARKS = '" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-GENERATED-TTUM-3'" + 
          " WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStDate() + "'";
      } 
      this.logger.info("UPDATE_RECORDS==" + UPDATE_RECORDS);
      getJdbcTemplate().execute(UPDATE_RECORDS);
      for (int i = 0; i < Headers.size(); i++) {
        table_cols = String.valueOf(table_cols) + "," + (String)Headers.get(i) + " VARCHAR (100 BYTE)";
        if (i + 1 == Headers.size()) {
          insert_cols = String.valueOf(insert_cols) + (String)Headers.get(i);
        } else {
          insert_cols = String.valueOf(insert_cols) + (String)Headers.get(i) + ",";
        } 
      } 
      String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
        "_" + generateTTUMBeanObj.getStFile_Name() + "'";
      int tableExist = ((Integer)getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[0], Integer.class)).intValue();
      this.logger.info("CHECK_TABLE==" + CHECK_TABLE);
      this.logger.info("tableExist==" + tableExist);
      if (tableExist == 0) {
        String CREATE_QUERY = "CREATE TABLE TTUM_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "_" + generateTTUMBeanObj.getStFile_Name() + " (" + table_cols + ")";
        this.logger.info("CREATE_QUERY==" + CREATE_QUERY);
        getJdbcTemplate().execute(CREATE_QUERY);
      } 
      for (int j = 0; j < ttum_data.size(); j++) {
        GenerateTTUMBean beanObj = new GenerateTTUMBean();
        beanObj = ttum_data.get(j);
        String INSERT_QUERY = " INSERT INTO ttum_rupay_sur_cbs              (dcrs_remarks, createddate, createdby,               account_number, currency_code, service_outlet, part_tran_type,               transaction_amount, transaction_particulars,               reference_currency_code, reference_amount, remarks              )  VALUES ('" + 
          
          generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-TTUM-3" + 
          "',SYSDATE,'" + generateTTUMBeanObj.getStEntry_by() + "','" + beanObj.getStDebitAcc() + "','INR','999','D','" + 
          beanObj.getStAmount() + "','" + beanObj.getStTran_particulars() + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "' ) ";
        this.logger.info("insert_cols==" + insert_cols);
        this.logger.info("INSERT_QUERY==" + INSERT_QUERY);
        getJdbcTemplate().execute(INSERT_QUERY);
        String[] tran_part = beanObj.getStTran_particulars().split("-");
        String stCRTran_Particular = "REV-RPAY-SURCHARGE-" + tran_part[2] + "-" + tran_part[3];
        INSERT_QUERY = " INSERT INTO ttum_rupay_sur_cbs              (dcrs_remarks, createddate, createdby,               account_number, currency_code, service_outlet, part_tran_type,               transaction_amount, transaction_particulars,               reference_currency_code, reference_amount, remarks              ) VALUES ('" + 
          
          generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-TTUM-3" + 
          "',SYSDATE,'" + generateTTUMBeanObj.getStEntry_by() + "','" + beanObj.getStCreditAcc() + "','INR','999','C','" + 
          beanObj.getStAmount() + "','" + stCRTran_Particular + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "' ) ";
        this.logger.info("INSERT_QUERY==" + INSERT_QUERY);
        getJdbcTemplate().execute(INSERT_QUERY);
      } 
      this.logger.info("***** GenerateRupayTTUMDaoImpl.generateCBSSurchargeTTUM End ****");
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.generateCBSSurchargeTTUM");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.generateCBSSurchargeTTUM", new Exception("GenerateRupayTTUMDaoImpl.generateCBSSurchargeTTUM", e));
      throw e;
    } finally {
      if (rset != null)
        rset.close(); 
      if (pstmt != null)
        pstmt.close(); 
      if (conn != null)
        conn.close(); 
    } 
    return Data;
  }
  
  public void TTUM_forDPart(GenerateTTUMBean generatettumBeanObj) throws Exception {}
  
  public String getCondition(List<KnockOffBean> knockoff_Criteria) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.getCondition Start ****");
    String select_parameters = "", condition = "", update_condition = "";
    List<KnockOffBean> Update_Headers = new ArrayList<>();
    try {
      for (int i = 0; i < knockoff_Criteria.size(); i++) {
        if (i == knockoff_Criteria.size() - 1) {
          if (((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_value() != null) {
            if (((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_padding().equals("Y")) {
              condition = String.valueOf(condition) + " SUBSTR( OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charpos() + 
                "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charsize() + ")" + 
                " " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_condition() + " " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_value();
              select_parameters = String.valueOf(select_parameters) + " SUBSTR( OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charpos() + 
                "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charsize() + ") AS " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header();
            } else {
              condition = String.valueOf(condition) + "OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + " " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_condition() + 
                " " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_value();
              select_parameters = String.valueOf(select_parameters) + "OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header();
            } 
          } else {
            Update_Headers.add(knockoff_Criteria.get(i));
            if (((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_padding().equals("Y")) {
              condition = String.valueOf(condition) + " SUBSTR( OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charpos() + 
                "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charsize() + ")" + 
                " " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_condition() + 
                " SUBSTR( OS2." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charpos() + 
                "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charsize() + ")";
              update_condition = String.valueOf(update_condition) + " SUBSTR(" + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charpos() + 
                "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charsize() + ") = ?";
              select_parameters = String.valueOf(select_parameters) + " SUBSTR( OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charpos() + 
                "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charsize() + ") AS " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header();
            } else {
              condition = String.valueOf(condition) + "OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + " " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_condition() + " OS2." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header();
              update_condition = String.valueOf(update_condition) + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + " = ?";
              select_parameters = String.valueOf(select_parameters) + "OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header();
            } 
          } 
        } else if (((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_value() != null) {
          if (((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_padding().equals("Y")) {
            condition = String.valueOf(condition) + " SUBSTR( OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charpos() + 
              "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charsize() + ")" + 
              " " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_condition() + " " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_value() + " AND ";
            select_parameters = String.valueOf(select_parameters) + " SUBSTR( OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charpos() + 
              "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charsize() + ") AS " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + " , ";
          } else {
            condition = String.valueOf(condition) + "OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + " " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_condition() + 
              " " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_value() + " AND ";
            select_parameters = String.valueOf(select_parameters) + "OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + " , ";
          } 
        } else {
          Update_Headers.add(knockoff_Criteria.get(i));
          if (((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_padding().equals("Y")) {
            condition = String.valueOf(condition) + " SUBSTR( OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charpos() + 
              "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charsize() + ")" + 
              " " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_condition() + 
              " SUBSTR( OS2." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charpos() + 
              "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charsize() + ") AND ";
            update_condition = String.valueOf(update_condition) + " SUBSTR(" + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charpos() + 
              "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charsize() + 
              ") = ? AND ";
            select_parameters = String.valueOf(select_parameters) + " SUBSTR(" + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charpos() + 
              "," + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_charsize() + 
              ") AS " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + " , ";
          } else {
            condition = String.valueOf(condition) + "OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + " " + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_condition() + 
              " OS2." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + " AND ";
            update_condition = String.valueOf(update_condition) + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + " = ? AND ";
            select_parameters = String.valueOf(select_parameters) + "OS1." + ((KnockOffBean)knockoff_Criteria.get(i)).getStReversal_header() + " , ";
          } 
        } 
        this.logger.info("condition==" + condition);
        this.logger.info("update_condition==" + update_condition);
        this.logger.info("select_parameters==" + select_parameters);
        this.logger.info("***** GenerateRupayTTUMDaoImpl.getCondition End ****");
      } 
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.getCondition");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.getCondition", new Exception("GenerateRupayTTUMDaoImpl.getCondition", e));
      throw e;
    } 
    return condition;
  }
  
  public List<GenerateTTUMBean> getCandDdifference(GenerateTTUMBean generateTTUMBeanObj) throws Exception {
	return null;}
  
  public void getReportCRecords(GenerateTTUMBean generateTTUMBeanObj) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.getReportCRecords Start ****");
    String GET_TTUM_RECORDS = "";
    String table_cols = " CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE)";
    String insert_cols = "CREATEDDATE, CREATEDBY";
    try {
      if (generateTTUMBeanObj.getStDate() != null && !generateTTUMBeanObj.getStDate().equals("")) {
        GET_TTUM_RECORDS = "UPDATE SETTLEMENT_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + 
          " SET DCRS_REMARKS = '" + generateTTUMBeanObj.getStMerger_Category() + 
          "-UNRECON-GENERATE-TTUM-" + generateTTUMBeanObj.getInRec_Set_Id() + 
          "' WHERE DCRS_REMARKS = '" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-2' AND TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DD/MM/YYYY') between '" + generateTTUMBeanObj.getStStart_Date() + "' AND '" + generateTTUMBeanObj.getStEnd_Date() + "'" + 
          " AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStDate() + "'";
      } else {
        GET_TTUM_RECORDS = "UPDATE SETTLEMENT_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + 
          " SET DCRS_REMARKS = '" + generateTTUMBeanObj.getStMerger_Category() + 
          "-UNRECON-GENERATE-TTUM-" + generateTTUMBeanObj.getInRec_Set_Id() + 
          "' WHERE DCRS_REMARKS = '" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-2' AND TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DD/MM/YYYY') = TO_CHAR(TO_DATE('" + generateTTUMBeanObj.getStFile_Date() + "'-10,'DD/MM/YYY'),'DD/MM/YYYY')" + 
          " AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStFile_Date() + "'";
      } 
      this.logger.info("GET_TTUM_RECORDS IN getReportCRecords " + GET_TTUM_RECORDS);
      getJdbcTemplate().execute(GET_TTUM_RECORDS);
      this.logger.info("***** GenerateRupayTTUMDaoImpl.getReportCRecords End ****");
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.getReportCRecords");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.getReportCRecords", new Exception("GenerateRupayTTUMDaoImpl.getReportCRecords", e));
      throw e;
    } 
  }
  
  public void getRupayTTUMRecords(GenerateTTUMBean generateTTUMBeanObj) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.getRupayTTUMRecords Start ****");
    String UPDATE_QUERY = "";
    try {
      String strDate = generateTTUMBeanObj.getStDate();
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
      Date varDate = null;
      try {
        varDate = dateFormat.parse(strDate);
        dateFormat = new SimpleDateFormat("yymmdd");
        this.logger.info("Date :" + dateFormat.format(varDate));
      } catch (Exception e) {
        e.printStackTrace();
      } 
      if (generateTTUMBeanObj.getStDate() != null && !generateTTUMBeanObj.getStDate().equals("")) {
        UPDATE_QUERY = "UPDATE SETTLEMENT_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + 
          " SET DCRS_REMARKS = '" + generateTTUMBeanObj.getStMerger_Category() + 
          "-UNRECON-GENERATE-TTUM-2' WHERE DCRS_REMARKS = '" + generateTTUMBeanObj.getStMerger_Category() + 
          "-UNRECON-2' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStDate() + "' AND ";
        if (generateTTUMBeanObj.getStMerger_Category().equals("RUPAY_DOM")) {
          UPDATE_QUERY = String.valueOf(UPDATE_QUERY) + " (TXNFUNCTION_CODE = '200' OR TXNFUNCTION_CODE = '262') ";
        } else {
          UPDATE_QUERY = String.valueOf(UPDATE_QUERY) + " (TXNFUNCTION_CODE = '200' OR TXNFUNCTION_CODE = '263') ";
        } 
        if (generateTTUMBeanObj.getStFunctionCode().equals("200"))
          UPDATE_QUERY = String.valueOf(UPDATE_QUERY) + " and DATE_SETTLEMENT = " + dateFormat.format(varDate); 
      } else {
        UPDATE_QUERY = "UPDATE SETTLEMENT_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + 
          " SET DCRS_REMARKS = '" + generateTTUMBeanObj.getStMerger_Category() + 
          "-UNRECON-GENERATE-TTUM-2' WHERE DCRS_REMARKS = '" + generateTTUMBeanObj.getStMerger_Category() + 
          "-UNRECON-2' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStFile_Date() + "' AND ";
        if (generateTTUMBeanObj.getStMerger_Category().equals("RUPAY_DOM")) {
          UPDATE_QUERY = String.valueOf(UPDATE_QUERY) + " (TXNFUNCTION_CODE = '200' OR TXNFUNCTION_CODE = '262') ";
        } else {
          UPDATE_QUERY = String.valueOf(UPDATE_QUERY) + " (TXNFUNCTION_CODE = '200' OR TXNFUNCTION_CODE = '263') ";
        } 
        if (generateTTUMBeanObj.getStFunctionCode().equals("200"))
          UPDATE_QUERY = String.valueOf(UPDATE_QUERY) + " and DATE_SETTLEMENT = " + varDate; 
      } 
      this.logger.info("UPDATE_QUERY==" + UPDATE_QUERY);
      getJdbcTemplate().execute(UPDATE_QUERY);
      this.logger.info("***** GenerateRupayTTUMDaoImpl.getRupayTTUMRecords End ****");
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.getRupayTTUMRecords");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.getRupayTTUMRecords", new Exception("GenerateRupayTTUMDaoImpl.getRupayTTUMRecords", e));
      throw e;
    } 
  }
  
  public List<List<GenerateTTUMBean>> GenerateRupayTTUM(GenerateTTUMBean generateTTUMBean, int inRec_Set_Id) throws Exception {
	return null;}
  
  public boolean IssurttumAlreadyGenrated(GenerateTTUMBean generatettumBean) throws Exception {
    String datediff = " Select to_date(MAX(FILEDATE),'DD/MM/RRRR')  - to_date('" + generatettumBean.getStDate() + "', 'DD/MM/RRRR' )  from settlement_rupay_SWITCH where dcrs_remarks like '%" + generatettumBean.getStSubCategory().substring(0, 3) + "%' ";
    System.out.println(datediff);
    int dateDiff = ((Integer)getJdbcTemplate().queryForObject(datediff, Integer.class)).intValue();
    String CHECK_QUERY = "";
    if (dateDiff > 3) {
      CHECK_QUERY = "SELECT COUNT(1) from SETTLEMENT_RUPAY_SWITCH_BK   WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generatettumBean.getStDate() + "' AND " + 
        " DCRS_REMARKS = '" + generatettumBean.getStMerger_Category() + "-UNRECON-GENERATED-TTUM-3' ";
    } else {
      CHECK_QUERY = "SELECT COUNT(1) from SETTLEMENT_RUPAY_SWITCH  WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generatettumBean.getStDate() + "' AND " + 
        " DCRS_REMARKS = '" + generatettumBean.getStMerger_Category() + "-UNRECON-GENERATED-TTUM-3' ";
    } 
    this.logger.info("***** CHECK_QUERY  **** " + CHECK_QUERY);
    int count = ((Integer)getJdbcTemplate().queryForObject(CHECK_QUERY, Integer.class)).intValue();
    this.logger.info("***** CHECK_QUERY COUNT  **** " + count);
    if (count > 0)
      return true; 
    return false;
  }
  
  public boolean cleanAlreadyProcessedSURTTUMRecords(GenerateTTUMBean generatettumBean) throws Exception {
    String datediff = " Select to_date(MAX(FILEDATE),'DD/MM/RRRR')  - to_date('" + generatettumBean.getStDate() + "', 'DD/MM/RRRR' )  from SETTLEMENT_RUPAY_SWITCH where dcrs_remarks like '%" + generatettumBean.getStSubCategory().substring(0, 3) + "%' ";
    int dateDiff = ((Integer)getJdbcTemplate().queryForObject(datediff, Integer.class)).intValue();
    String UPDATE_QUERY = "";
    System.out.println("dateDiff ::> " + dateDiff);
    if (dateDiff > 3) {
      UPDATE_QUERY = "UPDATE SETTLEMENT_RUPAY_SWITCH_BK SET DCRS_REMARKS = '" + generatettumBean.getStCategory() + "_" + generatettumBean.getStSubCategory().substring(0, 3) + 
        "-UNRECON-GENERATE-TTUM-" + '\003' + "' WHERE  TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generatettumBean.getStDate() + "' AND " + 
        " DCRS_REMARKS = '" + generatettumBean.getStMerger_Category() + "-UNRECON-GENERATED-TTUM-" + '\003' + "' ";
    } else {
      UPDATE_QUERY = "UPDATE SETTLEMENT_RUPAY_SWITCH SET DCRS_REMARKS = '" + generatettumBean.getStCategory() + "_" + generatettumBean.getStSubCategory().substring(0, 3) + 
        "-UNRECON-GENERATE-TTUM-" + '\003' + "' WHERE  TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generatettumBean.getStDate() + "' AND " + 
        " DCRS_REMARKS = '" + generatettumBean.getStMerger_Category() + "-UNRECON-GENERATED-TTUM-" + '\003' + "' ";
    } 
    this.logger.info("***** UPDATE_QUERY  **** " + UPDATE_QUERY);
    int updatedCount = getJdbcTemplate().update(UPDATE_QUERY);
    this.logger.info("***** UPDATE_COUNT  **** " + updatedCount);
    String DELETE_TTUM_QUERY = "DELETE FROM TTUM_" + generatettumBean.getStMerger_Category() + 
      "_" + generatettumBean.getStFile_Name() + 
      " where RECORDS_DATE = TO_DATE('" + generatettumBean.getStDate() + "','DD/MM/YYYY')";
    this.logger.info("*****OLD DELETE_TTUM_QUERY  **** " + DELETE_TTUM_QUERY);
    getJdbcTemplate().update(DELETE_TTUM_QUERY);
    String DELETE_TRACK_QUERY = "DELETE FROM MAIN_TRACK_TXN where FILEDATE = TO_DATE('" + 
      generatettumBean.getStDate() + "','DD/MM/YYYY')";
    this.logger.info("*****OLD DELETE_TTUM_QUERY  **** " + DELETE_TRACK_QUERY);
    getJdbcTemplate().update(DELETE_TRACK_QUERY);
    return true;
  }
  
  public void getReportERecords(GenerateTTUMBean generatettumBeanObj) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.getReportERecords Start ****");
    String UPDATE_QUERY = "";
    try {
      String datediff = " Select to_date(MAX(FILEDATE),'DD/MM/RRRR')  - to_date('" + generatettumBeanObj.getStDate() + "', 'DD/MM/RRRR' )  from SETTLEMENT_" + generatettumBeanObj.getStCategory() + "_" + generatettumBeanObj.getStFile_Name() + " where dcrs_remarks like '%" + generatettumBeanObj.getStSubCategory().substring(0, 3) + "%' ";
      int dateDiff = getJdbcTemplate().queryForInt(datediff);
      System.out.println("dateDiff ::> " + dateDiff);
      if (dateDiff > 3) {
        UPDATE_QUERY = "UPDATE SETTLEMENT_" + generatettumBeanObj.getStCategory() + "_" + generatettumBeanObj.getStFile_Name() + "_BK " + 
          " SET DCRS_REMARKS = '" + generatettumBeanObj.getStCategory() + "_" + generatettumBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-GENERATE-TTUM-3' WHERE DCRS_REMARKS = '" + generatettumBeanObj.getStCategory() + "_" + generatettumBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-3' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generatettumBeanObj.getStDate() + "'";
      } else {
        UPDATE_QUERY = "UPDATE SETTLEMENT_" + generatettumBeanObj.getStCategory() + "_" + generatettumBeanObj.getStFile_Name() + 
          " SET DCRS_REMARKS = '" + generatettumBeanObj.getStCategory() + "_" + generatettumBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-GENERATE-TTUM-3' WHERE DCRS_REMARKS = '" + generatettumBeanObj.getStCategory() + "_" + generatettumBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-3' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generatettumBeanObj.getStDate() + "'";
      } 
      this.logger.info("UPDATE_QUERY==" + UPDATE_QUERY);
      getJdbcTemplate().execute(UPDATE_QUERY);
      this.logger.info("***** GenerateRupayTTUMDaoImpl.getReportERecords End ****");
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.getReportERecords");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.getReportERecords", new Exception("GenerateRupayTTUMDaoImpl.getReportERecords", e));
      throw e;
    } 
  }
  
  public void getSurchargeRecords(GenerateTTUMBean generateTTUMBeanObj) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.getSurchargeRecords Start ****");
    String UPDATE_QUERY = "";
    try {
      if (generateTTUMBeanObj.getStDate() != null && !generateTTUMBeanObj.getStDate().equals("")) {
        String datediff = " Select to_date(MAX(FILEDATE),'DD/MM/RRRR')  - to_date('" + generateTTUMBeanObj.getStDate() + "', 'DD/MM/RRRR' )  from settlement_rupay_cbs where dcrs_remarks like '%" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + "%' ";
        int dateDiff = getJdbcTemplate().queryForInt(datediff);
        if (dateDiff > 3) {
          UPDATE_QUERY = "UPDATE SETTLEMENT_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + "_BK " + 
            " SET DCRS_REMARKS = '" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
            "-UNRECON-GENERATE-TTUM-3' WHERE DCRS_REMARKS = '" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
            "-UNRECON-3'" + 
            " AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStDate() + "'";
        } else {
          UPDATE_QUERY = "UPDATE SETTLEMENT_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + 
            " SET DCRS_REMARKS = '" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
            "-UNRECON-GENERATE-TTUM-3' WHERE DCRS_REMARKS = '" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
            "-UNRECON-3'" + 
            " AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStDate() + "'";
        } 
      } else {
        UPDATE_QUERY = "UPDATE SETTLEMENT_" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStFile_Name() + "_BK " + 
          " SET DCRS_REMARKS = '" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-GENERATE-TTUM-3' WHERE DCRS_REMARKS ='" + generateTTUMBeanObj.getStCategory() + "_" + generateTTUMBeanObj.getStSubCategory().substring(0, 3) + 
          "-UNRECON-3' AND " + 
          "VALUE_DATE = TO_CHAR(SYSDATE-10,'DD/MM/YYYY')";
      } 
      this.logger.info("UPDATE_QUERY==" + UPDATE_QUERY);
      getJdbcTemplate().execute(UPDATE_QUERY);
      this.logger.info("***** GenerateRupayTTUMDaoImpl.getSurchargeRecords End ****");
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.getSurchargeRecords");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.getSurchargeRecords", new Exception("GenerateRupayTTUMDaoImpl.getSurchargeRecords", e));
      throw e;
    } 
  }
  
  public List<List<GenerateTTUMBean>> getMatchedRecordsTTUM(GenerateTTUMBean generateTTUMbean) throws Exception {
	return null;}
  
  public List<List<GenerateTTUMBean>> getMatchedIntTxn(GenerateTTUMBean generateTTUMBeanObj, int inRec_Set_Id) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.getMatchedIntTxn Start ****");
    List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
    List<List<GenerateTTUMBean>> Data = new ArrayList<>();
    List<GenerateTTUMBean> TTUM_D_Data = new ArrayList<>();
    List<GenerateTTUMBean> TTUM_C_Data = new ArrayList<>();
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try {
      String GET_DATA = "SELECT PAN,TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DDMMYY') AS LOCAL_DATE,SUBSTR(TRACE,-6,6) AS TRACE,DIFF_AMOUNT,ACCEPTORNAME FROM SETTLEMENT_RUPAY_SWITCH WHERE RELAX_PARAM = 'Y' AND DCRS_REMARKS = '" + 
        generateTTUMBeanObj.getStMerger_Category() + "_" + 
        "MATCHED-" + inRec_Set_Id + "'";
      this.logger.info("GET_DATA==" + GET_DATA);
      conn = getConnection();
      pstmt = conn.prepareStatement(GET_DATA);
      rset = pstmt.executeQuery();
      float inCredit_Amt = 0.0F;
      float inDebit_Amt = 0.0F;
      String stTran_parti1 = "";
      String stInt_Remarks = "";
      String stTran_Parti2 = "";
      String stInt_Remarks2 = "";
      while (rset.next()) {
        int inAmt = Integer.parseInt(rset.getString("DIFF_AMOUNT"));
        if (inAmt > 0) {
          GenerateTTUMBean generatettumBean = new GenerateTTUMBean();
          generatettumBean.setStDebitAcc("99987750010154");
          generatettumBean.setStAmount(rset.getString("DIFF_AMOUNT"));
          inCredit_Amt += Float.parseFloat(rset.getString("DIFF_AMOUNT"));
          String stTran_Particular = "RPAY-EXCH-" + rset.getString("LOCAL_DATE") + "-" + rset.getString("TRACE") + "-" + rset.getString("ACCEPTORNAME");
          stTran_parti1 = stTran_Particular;
          generatettumBean.setStTran_particulars(stTran_Particular);
          generatettumBean.setStCard_Number(rset.getString("PAN"));
          String remark = (String)getJdbcTemplate().queryForObject("select 'DCRM'||ttum_seq.nextval from dual", new Object[0], String.class);
          stInt_Remarks = remark;
          generatettumBean.setStRemark(remark);
          TTUM_D_Data.add(generatettumBean);
          continue;
        } 
        if (inAmt < 0) {
          GenerateTTUMBean generatettumBean = new GenerateTTUMBean();
          generatettumBean.setStCreditAcc("99987750010154");
          generatettumBean.setStAmount(rset.getString("DIFF_AMOUNT"));
          inDebit_Amt += Float.parseFloat(rset.getString("DIFF_AMOUNT"));
          String stTran_Particular = "RPAY-EXCH-" + rset.getString("LOCAL_DATE") + "-" + rset.getString("TRACE") + "-" + rset.getString("ACCEPTORNAME");
          stTran_Parti2 = stTran_Particular;
          generatettumBean.setStTran_particulars(stTran_Particular);
          generatettumBean.setStCard_Number(rset.getString("PAN"));
          String remark = (String)getJdbcTemplate().queryForObject("select 'DCRM'||ttum_seq.nextval from dual", new Object[0], String.class);
          stInt_Remarks2 = remark;
          generatettumBean.setStRemark(remark);
          TTUM_C_Data.add(generatettumBean);
        } 
      } 
      if (inCredit_Amt > 0.0F) {
        GenerateTTUMBean generateTTUMBean = new GenerateTTUMBean();
        generateTTUMBean.setStCreditAcc("99937200010663");
        generateTTUMBean.setStTran_particulars(stTran_parti1);
        generateTTUMBean.setStRemark(stInt_Remarks);
        generateTTUMBean.setStCard_Number("");
        TTUM_C_Data.add(generateTTUMBean);
      } 
      if (inDebit_Amt > 0.0F) {
        GenerateTTUMBean generateTTUMBean = new GenerateTTUMBean();
        generateTTUMBean.setStDebitAcc("99937200010663");
        generateTTUMBean.setStTran_particulars(stTran_Parti2);
        generateTTUMBean.setStRemark(stInt_Remarks2);
        generateTTUMBean.setStCard_Number("");
        TTUM_D_Data.add(generateTTUMBean);
      } 
      Data.add(TTUM_C_Data);
      Data.add(TTUM_D_Data);
      this.logger.info("***** GenerateRupayTTUMDaoImpl.getMatchedIntTxn End ****");
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.getMatchedIntTxn");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.getMatchedIntTxn", new Exception("GenerateRupayTTUMDaoImpl.getMatchedIntTxn", e));
      throw e;
    } finally {
      if (rset != null)
        rset.close(); 
      if (pstmt != null)
        pstmt.close(); 
      if (conn != null)
        conn.close(); 
    } 
    return Data;
  }
  
  public List<List<GenerateTTUMBean>> LevyCharges(List<List<GenerateTTUMBean>> Data, GenerateTTUMBean generatettumBeanObj) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.LevyCharges Start ****");
    List<List<GenerateTTUMBean>> TTUM_Data = new ArrayList<>();
    List<GenerateTTUMBean> TTUM_D_Data = new ArrayList<>();
    List<GenerateTTUMBean> TTUM_C_Data = new ArrayList<>();
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try {
      TTUM_C_Data = Data.get(0);
      TTUM_D_Data = Data.get(1);
      String GET_DATA = "SELECT T2.PAN,T2.ACCTNUM,TO_CHAR(TO_DATE(T2.LOCAL_DATE,'MM/DD/YYYY'),'DDMMYY') AS LOCAL_DATE,SUBSTR(T2.TRACE,-6,6) AS TRACE,T2.ACCEPTORNAME,T1.AMOUNT_SETTLEMENT,T1.AMOUNT_TRANSACTION FROM SETTLEMENT_" + 
        generatettumBeanObj.getStCategory() + "_" + 
        generatettumBeanObj.getStFile_Name() + " T1 , SETTLEMENT_" + generatettumBeanObj.getStCategory() + "_SWITCH T2 " + 
        " WHERE T1.DCRS_REMARKS = '" + generatettumBeanObj.getStMerger_Category() + "-MATCHED-" + generatettumBeanObj.getInRec_Set_Id() + 
        "' AND T2.DCRS_REMARKS = '" + generatettumBeanObj.getStMerger_Category() + "-MATCHED-" + generatettumBeanObj.getInRec_Set_Id() + "'" + 
        " AND T2.PAN = T1.PRIMARY_ACCOUNT_NUMBER AND T2.AUTHNUM = T1.APPROVAL_CODE AND T2.ISSUER = T1.ACQUIRER_REFERENCE_DATA";
      this.logger.info("GET_DATA==" + GET_DATA);
      conn = getConnection();
      pstmt = conn.prepareStatement(GET_DATA);
      rset = pstmt.executeQuery();
      String GET_RATE = "SELECT RATE FROM MAIN_ACCOUNTING_TABLE WHERE CATEGORY = '" + generatettumBeanObj.getStCategory() + "_" + generatettumBeanObj.getStSubCategory() + "'";
      this.logger.info("GET_RATE==" + GET_RATE);
      String Rate = (String)getJdbcTemplate().queryForObject(GET_RATE, new Object[0], String.class);
      this.logger.info("Rate==" + Rate);
      while (rset.next()) {
        GenerateTTUMBean generatettumBean = new GenerateTTUMBean();
        float debit_amt = Float.parseFloat(rset.getString("AMOUNT_SETTLEMENT")) + Float.parseFloat(rset.getString("AMOUNT_SETTLEMENT")) * 
          Float.parseFloat(Rate) - Float.parseFloat(rset.getString("AMOUNT_TRANSACTION"));
        this.logger.info("debit amount is " + debit_amt);
        generatettumBean.setStCreditAcc("99987750010154");
        generatettumBean.setStDebitAcc(rset.getString("ACCTNUM").replaceAll(" ", " ").replaceAll("^0*", ""));
        generatettumBean.setStCard_Number(rset.getString("PAN"));
        generatettumBean.setStAmount((new StringBuilder(String.valueOf(debit_amt))).toString());
        String stTran_Particular = "DR-RPAY-MARKUP" + rset.getString("LOCAL_DATE") + "-" + rset.getString("TRACE") + "-" + rset.getString("ACCEPTORNAME");
        generatettumBean.setStTran_particulars(stTran_Particular);
        String remark = (String)getJdbcTemplate().queryForObject("select 'DCRM'||ttum_seq.nextval from dual", new Object[0], String.class);
        generatettumBean.setStRemark(remark);
        TTUM_C_Data.add(generatettumBean);
      } 
      TTUM_Data.add(TTUM_C_Data);
      TTUM_Data.add(TTUM_D_Data);
      this.logger.info("***** GenerateRupayTTUMDaoImpl.LevyCharges End ****");
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.LevyCharges");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.LevyCharges", new Exception("GenerateRupayTTUMDaoImpl.LevyCharges", e));
      throw e;
    } finally {
      if (rset != null)
        rset.close(); 
      if (pstmt != null)
        pstmt.close(); 
      if (conn != null)
        conn.close(); 
    } 
    return Data;
  }
  
  public String getLatestFileDate(GenerateTTUMBean generateTTUMBean) {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.getLatestFileDate Start ****");
    String stFileDate = "";
    String GET_FILEDATE = "";
    if (generateTTUMBean.getStCategory().equals("CASHNET")) {
      if (generateTTUMBean.getStFile_Name().equals("CBS")) {
        GET_FILEDATE = "SELECT TO_CHAR(MAX(FILEDATE),'DD/MM/YYYY') FROM SETTLEMENT_" + generateTTUMBean.getStCategory() + "_" + generateTTUMBean.getStSubCategory().substring(0, 3) + "_" + generateTTUMBean.getStSelectedFile();
        stFileDate = (String)getJdbcTemplate().queryForObject(GET_FILEDATE, new Object[0], String.class);
      } 
    } else {
      GET_FILEDATE = "SELECT TO_CHAR(MAX(FILEDATE),'DD/MM/YYYY') FROM SETTLEMENT_" + generateTTUMBean.getStCategory() + "_" + generateTTUMBean.getStSelectedFile();
      stFileDate = (String)getJdbcTemplate().queryForObject(GET_FILEDATE, new Object[0], String.class);
    } 
    this.logger.info("GET_FILEDATE==" + GET_FILEDATE);
    this.logger.info("stFileDate==" + stFileDate);
    this.logger.info("***** GenerateRupayTTUMDaoImpl.getLatestFileDate End ****");
    return stFileDate;
  }
  
  public void getFailedCBSRecords(GenerateTTUMBean generateTTUMBeanObj) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.getFailedCBSRecords Start ****");
    String UPDATE_QUERY = "UPDATE GLBL_CBS_UNMATCH SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATE-TTUM') WHERE DCRS_REMARKS LIKE '%(%'  AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" + 
      
      generateTTUMBeanObj.getStFile_Date() + "' AND " + 
      "TO_CHAR(TO_DATE(VALUE_DATE,'DD-MM-YYYY'),'DD/MM/YYYY') = '" + generateTTUMBeanObj.getStDate() + "' ";
    this.logger.info("UPDATE_QUERY==" + UPDATE_QUERY);
    try {
      getJdbcTemplate().execute(UPDATE_QUERY);
      this.logger.info("***** GenerateRupayTTUMDaoImpl.getFailedCBSRecords End ****");
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.getFailedCBSRecords");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.getFailedCBSRecords", new Exception("GenerateRupayTTUMDaoImpl.getFailedCBSRecords", e));
      throw e;
    } 
  }
  
  public List<List<GenerateTTUMBean>> generateDisputeTTUM(GenerateTTUMBean ttumBean) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.generateDisputeTTUM Start ****");
    List<GenerateTTUMBean> ttum_data = new ArrayList<>();
    List<GenerateTTUMBean> ttum_data_cbs = new ArrayList<>();
    List<GenerateTTUMBean> ttum_switch = new ArrayList<>();
    List<GenerateTTUMBean> ttum_cbs = new ArrayList<>();
    List<GenerateTTUMBean> ttum_others = new ArrayList<>();
    List<GenerateTTUMBean> Excel_headers = new ArrayList<>();
    List<String> ExcelHeaders = new ArrayList<>();
    List<String> ExcelHeaders1 = new ArrayList<>();
    List<List<GenerateTTUMBean>> Total_Data = new ArrayList<>();
    String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
    String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
    String stAction = "";
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    PreparedStatement pstmt1 = null;
    ResultSet rset1 = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    PreparedStatement ps1 = null;
    ResultSet rs1 = null;
    PreparedStatement ps2 = null;
    ResultSet rs2 = null;
    try {
      Date varDate = null;
      SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
      try {
        varDate = dateFormat.parse(ttumBean.getStDate());
        dateFormat = new SimpleDateFormat("ddMMyy");
        this.logger.info("Date :" + dateFormat.format(varDate));
      } catch (Exception e) {
        demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.generateDisputeTTUM");
        this.logger.error(" error in GenerateRupayTTUMDaoImpl.generateDisputeTTUM", new Exception("GenerateRupayTTUMDaoImpl.generateDisputeTTUM", e));
        throw e;
      } 
      this.logger.info("INSIDE generateSwitchTTUM");
      if (ttumBean.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
        ttumBean.setStGLAccount("99937200010660");
        String GET_TTUM_RECORDS = "SELECT ACCTNUM,AMOUNT,PAN,TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DDMMYY') AS LOCAL_DATE,SUBSTR(TRACE,-6,6) AS TRACE , ACCEPTORNAME,filedate  FROM SETTLEMENT_" + 
          ttumBean.getStCategory() + "_Switch" + 
          " WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM-2%'";
        conn = getConnection();
        pstmt = conn.prepareStatement(GET_TTUM_RECORDS);
        rset = pstmt.executeQuery();
        String GET_TTUM_RECORDS_CBS = "SELECT E,CONTRA_ACCOUNT,FORACID,TO_NUMBER(AMOUNT,999999999.99) AS AMOUNT, TO_CHAR(TO_DATE(VALUE_DATE,'DD/MM/YYYY'),'DDMMYY') AS VALUE_DATE,SUBSTR(REF_NO,2,6) AS REF_NO, REMARKS,PARTICULARALS,filedate FROM GLBL_CBS_UNMATCH  WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM-1%'";
        this.logger.info("GET_TTUM_RECORDS_CBS " + GET_TTUM_RECORDS_CBS);
        pstmt1 = conn.prepareStatement(GET_TTUM_RECORDS_CBS);
        rset1 = pstmt1.executeQuery();
        ExcelHeaders.add("ACCOUNT NUMBER");
        ExcelHeaders.add("CURRENCY CODE");
        ExcelHeaders.add("SERVICE OUTLET");
        ExcelHeaders.add("PART TRAN TYPE");
        ExcelHeaders.add("TRANSACTION AMOUNT");
        ExcelHeaders.add("TRANSACTION PARTICULARS");
        ExcelHeaders.add("REFERENCE NUMBER");
        ExcelHeaders.add("REFERENCE CURRENCY CODE");
        ExcelHeaders.add("REFERENCE AMOUNT");
        ExcelHeaders.add("REMARKS");
        ExcelHeaders.add("REPORT CODE");
        ExcelHeaders1.add("ACCOUNT_NUMBER");
        ExcelHeaders1.add("CURRENCY_CODE");
        ExcelHeaders1.add("SERVICE_OUTLET");
        ExcelHeaders1.add("PART_TRAN_TYPE");
        ExcelHeaders1.add("TRANSACTION_AMOUNT");
        ExcelHeaders1.add("TRANSACTION_PARTICULARS");
        ExcelHeaders1.add("REFERENCE_NUMBER");
        ExcelHeaders1.add("REFERENCE_CURRENCY_CODE");
        ExcelHeaders1.add("REFERENCE_AMOUNT");
        ExcelHeaders1.add("REMARKS");
        ExcelHeaders1.add("ACCOUNT_REPORT_CODE");
        ExcelHeaders1.add("FILEDATE");
        ttumBean.setStExcelHeader(ExcelHeaders);
        Excel_headers.add(ttumBean);
        int m = 0;
        int count1 = 0;
        while (rset.next()) {
          m++;
          GenerateTTUMBean generateTTUMBeanObj = new GenerateTTUMBean();
          generateTTUMBeanObj.setStCreditAcc(rset.getString("ACCTNUM").replaceAll(" ", " ").replaceAll("^0*", ""));
          generateTTUMBeanObj.setStDebitAcc("99937200010660");
          generateTTUMBeanObj.setStAmount(rset.getString("AMOUNT"));
          String stTran_particulars = "REV-RPAY-" + rset.getString("LOCAL_DATE") + "-" + rset.getString("ACCEPTORNAME").replaceAll("'", "");
          generateTTUMBeanObj.setStDate(rset.getString("LOCAL_DATE"));
          generateTTUMBeanObj.setStTran_particulars(stTran_particulars);
          String remark = (String)getJdbcTemplate().queryForObject("select 'RPYD'||'" + dateFormat.format(varDate) + "'||ttum_seq.nextval from dual", new Object[0], String.class);
          generateTTUMBeanObj.setStCard_Number(remark);
          generateTTUMBeanObj.setStRemark(String.valueOf(rset.getString("PAN")) + "/" + rset.getString("TRACE"));
          generateTTUMBeanObj.setFiledate(rset.getString("filedate"));
          ttum_data.add(generateTTUMBeanObj);
        } 
        while (rset1.next()) {
          stAction = rset1.getString("E");
          GenerateTTUMBean generateTTUMBean1 = new GenerateTTUMBean();
          String card_num = rset1.getString("REMARKS");
          if (stAction.equals("C")) {
            String stAccNum = rset1.getString("CONTRA_ACCOUNT");
            if (stAccNum.contains("78000010021")) {
              String get_man_acc = "select distinct t1.CONTRA_ACCOUNT,t2.ACCTNUM,t1.FILEDATE,t1.TRAN_DATE,t1.AMOUNT,t2.AMOUNT_EQUIV,substr(t1.REF_NO,2,6),substr(t2.TRACE,2,6) from cbs_rupay_rawdata t1 inner join switch_rawdata t2 on t1.REMARKS = t2.PAN and t1.FILEDATE = t2.FILEDATE and trunc(TO_NUMBER(REPLACE(T1.AMOUNT,',',''))) = trunc(TO_NUMBER(t2.AMOUNT_EQUIV)) and substr(t1.REF_NO,2,6) =substr(t2.TRACE,2,6)  and t2.ACCTNUM is not null where t1.CONTRA_ACCOUNT = '" + 
                
                stAccNum + "' and t2.PAN = '" + card_num + "' order by t1.FILEDATE";
              PreparedStatement pstmt_con = conn.prepareStatement(get_man_acc);
              ResultSet rset_con = pstmt_con.executeQuery();
              while (rset_con.next())
                generateTTUMBean1.setStCreditAcc(rset_con.getString("ACCTNUM").replaceAll(" ", " ").replaceAll("^0*", "")); 
            } else {
              generateTTUMBean1.setStCreditAcc(rset1.getString("CONTRA_ACCOUNT"));
            } 
            generateTTUMBean1.setStDebitAcc(rset1.getString("FORACID"));
            generateTTUMBean1.setStAmount(rset1.getString("AMOUNT"));
            String stTran_particular = "REV-RPAY-" + rset1.getString("VALUE_DATE") + "-" + rset1.getString("PARTICULARALS").replace("'", "");
            generateTTUMBean1.setStDate(rset1.getString("VALUE_DATE"));
            generateTTUMBean1.setStTran_particulars(stTran_particular);
            String remark = (String)getJdbcTemplate().queryForObject("select 'RPYD'||'" + dateFormat.format(varDate) + "'||ttum_seq.nextval from dual", new Object[0], String.class);
            generateTTUMBean1.setStCard_Number(remark);
            generateTTUMBean1.setStRemark(String.valueOf(rset1.getString("REMARKS")) + "/" + rset1.getString("REF_NO"));
            generateTTUMBean1.setFiledate(rset1.getString("filedate"));
            ttum_data_cbs.add(generateTTUMBean1);
            continue;
          } 
          if (stAction.equals("D")) {
            String stAccNum = rset1.getString("CONTRA_ACCOUNT");
            if (stAccNum.contains("78000010021")) {
              String get_man_acc = "select distinct t1.CONTRA_ACCOUNT,t2.ACCTNUM,t1.FILEDATE,t1.TRAN_DATE,t1.AMOUNT,t2.AMOUNT_EQUIV,substr(t1.REF_NO,2,6),substr(t2.TRACE,2,6) from cbs_rupay_rawdata t1 inner join switch_rawdata t2 on t1.REMARKS = t2.PAN and t1.FILEDATE = t2.FILEDATE and trunc(TO_NUMBER(REPLACE(T1.AMOUNT,',',''))) = trunc(TO_NUMBER(t2.AMOUNT_EQUIV)) and substr(t1.REF_NO,2,6) =substr(t2.TRACE,2,6)  and t2.ACCTNUM is not null where t1.CONTRA_ACCOUNT = '" + 
                
                stAccNum + "' and t2.PAN = '" + card_num + "' order by t1.FILEDATE";
              PreparedStatement pstmt_con = conn.prepareStatement(get_man_acc);
              ResultSet rset_con = pstmt_con.executeQuery();
              while (rset_con.next())
                generateTTUMBean1.setStDebitAcc(rset_con.getString("ACCTNUM").replaceAll(" ", " ").replaceAll("^0*", "")); 
            } else {
              generateTTUMBean1.setStDebitAcc(rset1.getString("CONTRA_ACCOUNT"));
            } 
            generateTTUMBean1.setStCreditAcc(rset1.getString("FORACID"));
            generateTTUMBean1.setStAmount(rset1.getString("AMOUNT"));
            String stTran_particular = "ADR-RPAY-" + rset1.getString("VALUE_DATE") + "-" + rset1.getString("PARTICULARALS");
            generateTTUMBean1.setStDate(rset1.getString("VALUE_DATE"));
            generateTTUMBean1.setStTran_particulars(stTran_particular);
            String remark = (String)getJdbcTemplate().queryForObject("select 'RPYD'||'" + dateFormat.format(varDate) + "'||ttum_seq.nextval from dual", new Object[0], String.class);
            generateTTUMBean1.setStCard_Number(remark);
            generateTTUMBean1.setStRemark(String.valueOf(rset1.getString("REMARKS")) + "/" + rset1.getString("REF_NO"));
            generateTTUMBean1.setFiledate(rset1.getString("filedate"));
            ttum_data_cbs.add(generateTTUMBean1);
          } 
        } 
      } else if (ttumBean.getStSubCategory().equalsIgnoreCase("INTERNATIONAL")) {
        ttumBean.setStGLAccount("99937200010663");
        String GET_TTUM_RECORDS = "SELECT PAN,ACCTNUM,AMOUNT,ISSUER,TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DDMMYY') AS LOCAL_DATE,SUBSTR(TRACE,-6,6) AS TRACE,ACCEPTORNAME FROM SETTLEMENT_" + 
          ttumBean.getStCategory() + "_" + ttumBean.getStFile_Name() + " WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%'";
        conn = getConnection();
        pstmt = conn.prepareStatement(GET_TTUM_RECORDS);
        rset = pstmt.executeQuery();
        ExcelHeaders.add("ACCOUNT NUMBER");
        ExcelHeaders.add("CURRENCY CODE");
        ExcelHeaders.add("SERVICE OUTLET");
        ExcelHeaders.add("PART TRAN TYPE");
        ExcelHeaders.add("TRANSACTION AMOUNT");
        ExcelHeaders.add("TRANSACTION PARTICULARS");
        ExcelHeaders.add("REFERENCE NUMBER");
        ExcelHeaders.add("REFERENCE CURRENCY CODE");
        ExcelHeaders.add("REFERENCE AMOUNT");
        ExcelHeaders.add("REMARKS");
        ExcelHeaders.add("REPORT CODE");
        ttumBean.setStExcelHeader(ExcelHeaders);
        Excel_headers.add(ttumBean);
        while (rset.next()) {
          GenerateTTUMBean generateTTUMBeanObj = new GenerateTTUMBean();
          generateTTUMBeanObj.setStCreditAcc(rset.getString("ACCTNUM").replaceAll(" ", " ").replaceAll("^0*", ""));
          generateTTUMBeanObj.setStDebitAcc("99937200010660");
          generateTTUMBeanObj.setStAmount(rset.getString("AMOUNT"));
          String stTran_particulars = "REV-RPAY-" + rset.getString("LOCAL_DATE") + "/" + rset.getString("TRACE") + "-" + rset.getString("ACCEPTORNAME");
          String stRecords_Date = rset.getString("LOCAL_DATE");
          generateTTUMBeanObj.setStTran_particulars(stTran_particulars);
          generateTTUMBeanObj.setStCard_Number(rset.getString("PAN"));
          String remark = (String)getJdbcTemplate().queryForObject("select 'RPYI'||'" + dateFormat.format(varDate) + "'||ttum_seq.nextval from dual", new Object[0], String.class);
          generateTTUMBeanObj.setStRemark(remark);
          generateTTUMBeanObj.setAccount_repo("");
          ttum_data.add(generateTTUMBeanObj);
        } 
        Total_Data.add(Excel_headers);
        Total_Data.add(ttum_data);
      } 
      for (int i = 0; i < ExcelHeaders1.size(); i++) {
        table_cols = String.valueOf(table_cols) + "," + (String)ExcelHeaders1.get(i) + " VARCHAR (100 BYTE)";
        insert_cols = String.valueOf(insert_cols) + "," + (String)ExcelHeaders1.get(i);
      } 
      String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = UPPER('TTUM_" + ttumBean.getStMerger_Category() + 
        "_" + ttumBean.getStFile_Name() + "')";
      int tableExist = ((Integer)getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[0], Integer.class)).intValue();
      this.logger.info("CHECK_TABLE==" + CHECK_TABLE);
      this.logger.info("tableExist==" + tableExist);
      if (tableExist == 0) {
        String CREATE_QUERY = "CREATE TABLE TTUM_" + ttumBean.getStMerger_Category() + 
          "_" + ttumBean.getStFile_Name() + " (" + table_cols + ")";
        this.logger.info("CREATE_QUERY==" + CREATE_QUERY);
        getJdbcTemplate().execute(CREATE_QUERY);
      } 
      String query1 = "truncate table TTUM_" + ttumBean.getStMerger_Category() + "_" + ttumBean.getStFile_Name();
      this.logger.info(query1);
      getJdbcTemplate().execute(query1);
      int incount = 0;
      int count = 0;
      for (int j = 0; j < ttum_data.size(); j++) {
        incount++;
        GenerateTTUMBean beanObj = new GenerateTTUMBean();
        beanObj = ttum_data.get(j);
        String INSERT_QUERY = "INSERT INTO TTUM_" + ttumBean.getStMerger_Category() + 
          "_" + ttumBean.getStFile_Name() + 
          "(" + insert_cols + ") VALUES ('" + ttumBean.getStMerger_Category() + 
          "-UNRECON-TTUM-2" + 
          "',SYSDATE,'" + ttumBean.getStEntry_by() + "',TO_DATE('" + beanObj.getStDate() + "','DDMMYY'),'" + beanObj.getStDebitAcc() + "','INR','999','D','" + 
          beanObj.getStAmount() + "','" + beanObj.getStTran_particulars() + "','" + beanObj.getStCard_Number() + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "','" + beanObj.getAccount_repo() + "','" + beanObj.getFiledate() + "')";
        this.logger.info("INSERT_QUERY==" + INSERT_QUERY);
        getJdbcTemplate().execute(INSERT_QUERY);
        INSERT_QUERY = "INSERT INTO TTUM_" + ttumBean.getStMerger_Category() + 
          "_" + ttumBean.getStFile_Name() + 
          "(" + insert_cols + ") VALUES ('" + ttumBean.getStMerger_Category() + 
          "-UNRECON-TTUM-2" + 
          "',SYSDATE,'" + ttumBean.getStEntry_by() + "',TO_DATE('" + beanObj.getStDate() + "','DDMMYY'),'" + beanObj.getStCreditAcc().trim() + "','INR','999','C','" + 
          beanObj.getStAmount() + "','" + beanObj.getStTran_particulars() + "','" + beanObj.getStCard_Number() + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "','" + beanObj.getAccount_repo() + "','" + beanObj.getFiledate() + "')";
        this.logger.info("INSERT_QUERY==" + INSERT_QUERY);
        getJdbcTemplate().execute(INSERT_QUERY);
      } 
      int incount1 = 0;
      for (int k = 0; k < ttum_data_cbs.size(); k++) {
        incount1++;
        GenerateTTUMBean beanObj = new GenerateTTUMBean();
        beanObj = ttum_data_cbs.get(k);
        String INSERT_QUERY = "INSERT INTO TTUM_" + ttumBean.getStMerger_Category() + 
          "_" + ttumBean.getStFile_Name() + 
          "(" + insert_cols + ") VALUES ('" + ttumBean.getStMerger_Category() + 
          "-UNRECON-TTUM-1" + 
          "',SYSDATE,'" + ttumBean.getStEntry_by() + "',TO_DATE('" + beanObj.getStDate() + "','DDMMYY'),'" + beanObj.getStDebitAcc() + "','INR','999','D','" + 
          beanObj.getStAmount() + "','" + beanObj.getStTran_particulars() + "','" + beanObj.getStCard_Number() + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "','" + beanObj.getAccount_repo() + "','" + beanObj.getFiledate() + "')";
        this.logger.info("INSERT_QUERY==" + INSERT_QUERY);
        getJdbcTemplate().execute(INSERT_QUERY);
        INSERT_QUERY = "INSERT INTO TTUM_" + ttumBean.getStMerger_Category() + 
          "_" + ttumBean.getStFile_Name() + 
          "(" + insert_cols + ") VALUES ('" + ttumBean.getStMerger_Category() + 
          "-UNRECON-TTUM-1" + 
          "',SYSDATE,'" + ttumBean.getStEntry_by() + "',TO_DATE('" + beanObj.getStDate() + "','DDMMYY'),'" + beanObj.getStCreditAcc().trim() + "','INR','999','C','" + 
          beanObj.getStAmount() + "','" + beanObj.getStTran_particulars() + "','" + beanObj.getStCard_Number() + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "','" + beanObj.getAccount_repo() + "','" + beanObj.getFiledate() + "')";
        this.logger.info("INSERT_QUERY==" + INSERT_QUERY);
        getJdbcTemplate().execute(INSERT_QUERY);
      } 
      String GET_TTUM_SWITCH = "SELECT ACCOUNT_NUMBER,PART_TRAN_TYPE,TRANSACTION_AMOUNT,TRANSACTION_PARTICULARS,REMARKS,REFERENCE_NUMBER FROM TTUM_RUPAY_DOM_DISPUTE WHERE DCRS_REMARKS LIKE '%UNRECON-TTUM-2%' and to_char(to_date(substr(filedate,1,10),'yyyy-mm-dd'),'dd/mm/yyyy') = '" + 
        ttumBean.getStDate() + "' order by REFERENCE_NUMBER,TRANSACTION_AMOUNT,ACCOUNT_NUMBER";
      this.logger.info("GET_TTUM_SWITCH " + GET_TTUM_SWITCH);
      conn = getConnection();
      ps = conn.prepareStatement(GET_TTUM_SWITCH);
      rs = ps.executeQuery();
      while (rs.next()) {
        count++;
        GenerateTTUMBean generateTTUMBeanObj = new GenerateTTUMBean();
        generateTTUMBeanObj.setStCreditAcc(rs.getString("ACCOUNT_NUMBER").replaceAll(" ", " ").replaceAll("^0*", ""));
        generateTTUMBeanObj.setStDebitAcc("99937200010660");
        generateTTUMBeanObj.setStAmount(rs.getString("TRANSACTION_AMOUNT"));
        generateTTUMBeanObj.setStTran_particulars(rs.getString("TRANSACTION_PARTICULARS"));
        generateTTUMBeanObj.setStCard_Number(rs.getString("REFERENCE_NUMBER"));
        generateTTUMBeanObj.setStRemark(rs.getString("REMARKS"));
        generateTTUMBeanObj.setPart_tran_type(rs.getString("PART_TRAN_TYPE"));
        ttum_switch.add(generateTTUMBeanObj);
      } 
      String GET_TTUM_CBS = "SELECT ACCOUNT_NUMBER,PART_TRAN_TYPE,TRANSACTION_AMOUNT,TRANSACTION_PARTICULARS,REMARKS,REFERENCE_NUMBER FROM TTUM_RUPAY_DOM_DISPUTE WHERE DCRS_REMARKS LIKE '%UNRECON-TTUM-1%' and to_char(to_date(substr(filedate,1,10),'yyyy-mm-dd'),'dd/mm/yyyy') = '" + 
        ttumBean.getStDate() + "' order by REFERENCE_NUMBER,TRANSACTION_AMOUNT,ACCOUNT_NUMBER";
      this.logger.info("GET_TTUM_RECORDS_CBS " + GET_TTUM_CBS);
      ps1 = conn.prepareStatement(GET_TTUM_CBS);
      rs1 = ps1.executeQuery();
      while (rs1.next()) {
        GenerateTTUMBean generateTTUMBean1 = new GenerateTTUMBean();
        generateTTUMBean1.setStDebitAcc("99937200010660");
        generateTTUMBean1.setStCreditAcc(rs1.getString("ACCOUNT_NUMBER"));
        generateTTUMBean1.setStAmount(rs1.getString("TRANSACTION_AMOUNT"));
        String particular = rs1.getString("TRANSACTION_PARTICULARS");
        if (particular.contains("RPAY-POS"))
          particular = particular.replace(particular.substring(15, 24), ""); 
        generateTTUMBean1.setStTran_particulars(particular);
        generateTTUMBean1.setStCard_Number(rs1.getString("REFERENCE_NUMBER"));
        generateTTUMBean1.setStRemark(rs1.getString("REMARKS"));
        generateTTUMBean1.setPart_tran_type(rs1.getString("PART_TRAN_TYPE"));
        ttum_cbs.add(generateTTUMBean1);
      } 
      String GET_TTUM_OTHER = "SELECT PAN,TRACE,LOCAL_DATE,AMOUNT_EQUIV,TXNFUNCTION_CODE,DATE_SETTLEMENT,AMOUNT_SETTLEMENT,SWITCH_REMARKS,CBS_REMARKS FROM TEMP_RUPAY_DISPUTE1 where filedate = to_date('" + ttumBean.getStDate() + "','dd/mm/yyyy')";
      this.logger.info("GET_TTUM_RECORDS_OTHER " + GET_TTUM_OTHER);
      ps2 = conn.prepareStatement(GET_TTUM_OTHER);
      rs2 = ps2.executeQuery();
      while (rs2.next()) {
        GenerateTTUMBean generateTTUMBean1 = new GenerateTTUMBean();
        generateTTUMBean1.setPan(rs2.getString("pan"));
        generateTTUMBean1.setTrace(rs2.getString("trace"));
        generateTTUMBean1.setLocal_date(rs2.getString("local_date"));
        generateTTUMBean1.setAmount_equiv(rs2.getString("amount_equiv"));
        generateTTUMBean1.setFuncation_code(rs2.getString("TXNFUNCTION_CODE"));
        generateTTUMBean1.setSettlement_date(rs2.getString("DATE_SETTLEMENT"));
        generateTTUMBean1.setSettlement_amount(rs2.getString("AMOUNT_SETTLEMENT"));
        generateTTUMBean1.setSwitch_remarks(rs2.getString("SWITCH_REMARKS"));
        generateTTUMBean1.setCbs_remarks(rs2.getString("CBS_REMARKS"));
        ttum_others.add(generateTTUMBean1);
      } 
      Total_Data.add(Excel_headers);
      Total_Data.add(null);
      Total_Data.add(2, ttum_switch);
      Total_Data.add(3, ttum_cbs);
      Total_Data.add(4, ttum_others);
      String value = "";
      for (int loop = 1; loop <= 2; loop++) {
        if (loop == 1) {
          value = "CBS";
        } else {
          value = "SWITCH";
        } 
        String UPDATE_RECORDS = "UPDATE SETTLEMENT_" + ttumBean.getStCategory() + "_" + value + 
          " SET DCRS_REMARKS = '" + ttumBean.getStMerger_Category() + "-UNRECON-GENERATED-TTUM-" + loop + "'" + 
          " WHERE DCRS_REMARKS = '" + ttumBean.getStMerger_Category() + "-UNRECON-GENERATE-TTUM-" + loop + "'";
        this.logger.info("UPDATE_RECORDS==" + UPDATE_RECORDS);
        getJdbcTemplate().execute(UPDATE_RECORDS);
      } 
      String CHECK_TABLE1 = "SELECT count (*) FROM tab WHERE tname  = UPPER('TTUM_" + ttumBean.getStMerger_Category() + 
        "_" + ttumBean.getStFile_Name() + "_BKUP')";
      int tableExist1 = ((Integer)getJdbcTemplate().queryForObject(CHECK_TABLE1, new Object[0], Integer.class)).intValue();
      if (tableExist1 == 0) {
        String CREATE_QUERY1 = "CREATE TABLE TTUM_" + ttumBean.getStMerger_Category() + 
          "_" + ttumBean.getStFile_Name() + "_BKUP (" + table_cols + ",MOVED_ON DATE)";
        getJdbcTemplate().execute(CREATE_QUERY1);
      } 
      String query = "insert into TTUM_" + ttumBean.getStMerger_Category() + "_" + ttumBean.getStFile_Name() + "_BKUP (" + insert_cols + ",MOVED_ON) select " + insert_cols + ",sysdate from TTUM_" + ttumBean.getStMerger_Category() + 
        "_" + ttumBean.getStFile_Name();
      this.logger.info(query);
      getJdbcTemplate().execute(query);
      String query2 = "truncate table TTUM_" + ttumBean.getStMerger_Category() + "_" + ttumBean.getStFile_Name();
      this.logger.info(query2);
      getJdbcTemplate().execute(query2);
      this.logger.info("***** GenerateRupayTTUMDaoImpl.generateDisputeTTUM End ****");
      return Total_Data;
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.generateDisputeTTUM");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.generateDisputeTTUM", new Exception("GenerateRupayTTUMDaoImpl.generateDisputeTTUM", e));
      return Total_Data;
    } finally {
      if (rset != null)
        rset.close(); 
      if (pstmt != null)
        pstmt.close(); 
      if (rset1 != null)
        rset1.close(); 
      if (pstmt1 != null)
        pstmt1.close(); 
      if (rs != null)
        rs.close(); 
      if (ps != null)
        ps.close(); 
      if (rs1 != null)
        rs1.close(); 
      if (ps1 != null)
        ps1.close(); 
      if (rs2 != null)
        rs2.close(); 
      if (ps2 != null)
        ps2.close(); 
      if (conn != null)
        conn.close(); 
    } 
  }
  
  public List<List<GenerateTTUMBean>> generateVisaDisputeTTUM(GenerateTTUMBean ttumBean) throws Exception {
    this.logger.info("***** GenerateRupayTTUMDaoImpl.generateVisaDisputeTTUM Start ****");
    List<GenerateTTUMBean> ttum_data = new ArrayList<>();
    List<GenerateTTUMBean> ttum_data_cbs = new ArrayList<>();
    List<GenerateTTUMBean> ttum_switch = new ArrayList<>();
    List<GenerateTTUMBean> ttum_cbs = new ArrayList<>();
    List<GenerateTTUMBean> ttum_others = new ArrayList<>();
    List<GenerateTTUMBean> Excel_headers = new ArrayList<>();
    List<String> ExcelHeaders = new ArrayList<>();
    List<String> ExcelHeaders1 = new ArrayList<>();
    List<List<GenerateTTUMBean>> Total_Data = new ArrayList<>();
    String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
    String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
    String stAction = "";
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    PreparedStatement pstmt1 = null;
    ResultSet rset1 = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    PreparedStatement ps1 = null;
    ResultSet rs1 = null;
    PreparedStatement ps2 = null;
    ResultSet rs2 = null;
    try {
      this.logger.info("INSIDE generateSwitchTTUM");
      if (ttumBean.getStSubCategory().equalsIgnoreCase("ISSUER")) {
        this.logger.info("*** In ISSUER ***");
        ttumBean.setStGLAccount("99937200010089");
        String GET_TTUM_RECORDS = "SELECT trim(ltrim(ACCTNUM,'0')) ACCTNUM,AMOUNT,PAN,TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DDMMYY') AS LOCAL_DATE,SUBSTR(TRACE,-6,6) AS TRACE , ACCEPTORNAME,filedate  FROM SETTLEMENT_" + 
          ttumBean.getStCategory() + "_Switch" + 
          " WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM-2%'";
        this.logger.info("GET_TTUM_RECORDS==" + GET_TTUM_RECORDS);
        conn = getConnection();
        pstmt = conn.prepareStatement(GET_TTUM_RECORDS);
        rset = pstmt.executeQuery();
        String GET_TTUM_RECORDS_CBS = "SELECT E,CONTRA_ACCOUNT,FORACID,TO_NUMBER(AMOUNT,999999999.99) AS AMOUNT, TO_CHAR(TO_DATE(VALUE_DATE,'DD/MM/YYYY'),'DDMMYY') AS VALUE_DATE,SUBSTR(REF_NO,2,6) AS TRACE, REMARKS,PARTICULARALS,filedate,SUBSTR(REF_NO,2,6) FROM SETTLEMENT_" + 
          ttumBean.getStCategory() + "_CBS" + 
          " WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM-1%'";
        this.logger.info("GET_TTUM_RECORDS_CBS " + GET_TTUM_RECORDS_CBS);
        pstmt1 = conn.prepareStatement(GET_TTUM_RECORDS_CBS);
        rset1 = pstmt1.executeQuery();
        ExcelHeaders.add("ACCOUNT NUMBER");
        ExcelHeaders.add("CURRENCY CODE");
        ExcelHeaders.add("SERVICE OUTLET");
        ExcelHeaders.add("PART TRAN TYPE");
        ExcelHeaders.add("TRANSACTION AMOUNT");
        ExcelHeaders.add("TRANSACTION PARTICULARS");
        ExcelHeaders.add("REFERENCE NUMBER");
        ExcelHeaders.add("REFERENCE CURRENCY CODE");
        ExcelHeaders.add("REFERENCE AMOUNT");
        ExcelHeaders.add("REMARKS");
        ExcelHeaders.add("REPORT CODE");
        ExcelHeaders1.add("ACCOUNT_NUMBER");
        ExcelHeaders1.add("CURRENCY_CODE");
        ExcelHeaders1.add("SERVICE_OUTLET");
        ExcelHeaders1.add("PART_TRAN_TYPE");
        ExcelHeaders1.add("TRANSACTION_AMOUNT");
        ExcelHeaders1.add("TRANSACTION_PARTICULARS");
        ExcelHeaders1.add("REFERENCE_NUMBER");
        ExcelHeaders1.add("REFERENCE_CURRENCY_CODE");
        ExcelHeaders1.add("REFERENCE_AMOUNT");
        ExcelHeaders1.add("REMARKS");
        ExcelHeaders1.add("ACCOUNT_REPORT_CODE");
        ExcelHeaders1.add("FILEDATE");
        ttumBean.setStExcelHeader(ExcelHeaders);
        Excel_headers.add(ttumBean);
        int m = 0;
        int count1 = 0;
        while (rset.next()) {
          m++;
          GenerateTTUMBean generateTTUMBeanObj = new GenerateTTUMBean();
          generateTTUMBeanObj.setStCreditAcc(rset.getString("ACCTNUM").replaceAll(" ", ""));
          generateTTUMBeanObj.setStDebitAcc("99937200010089");
          generateTTUMBeanObj.setStAmount(rset.getString("AMOUNT"));
          String stTran_particulars = "REV/VISA/" + rset.getString("LOCAL_DATE") + "/" + rset.getString("ACCEPTORNAME").replaceAll("'", "");
          generateTTUMBeanObj.setStDate(rset.getString("LOCAL_DATE"));
          generateTTUMBeanObj.setStTran_particulars(stTran_particulars);
          generateTTUMBeanObj.setTrace(rset.getString("TRACE"));
          String remark = (String)getJdbcTemplate().queryForObject("select 'VISI'||to_char(sysdate,'ddmmyy')||ttum_seq.nextval from dual", new Object[0], String.class);
          generateTTUMBeanObj.setStCard_Number(remark);
          generateTTUMBeanObj.setStRemark(rset.getString("PAN"));
          generateTTUMBeanObj.setFiledate(rset.getString("filedate"));
          ttum_data.add(generateTTUMBeanObj);
        } 
        while (rset1.next()) {
          stAction = rset1.getString("E");
          GenerateTTUMBean generateTTUMBean1 = new GenerateTTUMBean();
          if (stAction.equals("C")) {
            generateTTUMBean1.setStDebitAcc(rset1.getString("FORACID"));
            generateTTUMBean1.setStCreditAcc(rset1.getString("CONTRA_ACCOUNT"));
            generateTTUMBean1.setStAmount(rset1.getString("AMOUNT"));
            String stTran_particular = "REV/VISA/" + rset1.getString("VALUE_DATE") + "/" + rset1.getString("PARTICULARALS");
            generateTTUMBean1.setStDate(rset1.getString("VALUE_DATE"));
            generateTTUMBean1.setStTran_particulars(stTran_particular);
            generateTTUMBean1.setTrace(rset1.getString("TRACE"));
            String remark = (String)getJdbcTemplate().queryForObject("select 'VISI'||to_char(sysdate,'ddmmyy')||ttum_seq.nextval from dual", new Object[0], String.class);
            generateTTUMBean1.setStCard_Number(remark);
            generateTTUMBean1.setStRemark(rset1.getString("REMARKS"));
            generateTTUMBean1.setFiledate(rset1.getString("filedate"));
            ttum_data_cbs.add(generateTTUMBean1);
          } 
        } 
      } 
      for (int i = 0; i < ExcelHeaders1.size(); i++) {
        table_cols = String.valueOf(table_cols) + "," + (String)ExcelHeaders1.get(i) + " VARCHAR (100 BYTE)";
        insert_cols = String.valueOf(insert_cols) + "," + (String)ExcelHeaders1.get(i);
      } 
      String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = UPPER('temp_TTUM_" + ttumBean.getStMerger_Category() + "_" + ttumBean.getStFile_Name() + "')";
      int tableExist = ((Integer)getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[0], Integer.class)).intValue();
      this.logger.info("CHECK_TABLE==" + CHECK_TABLE);
      this.logger.info("tableExist==" + tableExist);
      if (tableExist == 0) {
        String CREATE_QUERY = "CREATE TABLE temp_TTUM_" + ttumBean.getStMerger_Category() + 
          "_" + ttumBean.getStFile_Name() + " (" + table_cols + ")";
        this.logger.info("CREATE_QUERY==" + CREATE_QUERY);
        getJdbcTemplate().execute(CREATE_QUERY);
      } 
      int incount = 0;
      int count = 0;
      for (int j = 0; j < ttum_data.size(); j++) {
        incount++;
        GenerateTTUMBean beanObj = new GenerateTTUMBean();
        beanObj = ttum_data.get(j);
        String INSERT_QUERY = "INSERT INTO temp_TTUM_" + ttumBean.getStMerger_Category() + 
          "_SWITCH" + 
          "(" + insert_cols + ") VALUES ('" + ttumBean.getStMerger_Category() + 
          "-UNRECON-TTUM-2" + 
          "',SYSDATE,'" + ttumBean.getStEntry_by() + "',TO_DATE('" + beanObj.getStDate() + "','DDMMYY'),'" + beanObj.getStDebitAcc() + "','INR','999','D','" + 
          beanObj.getStAmount() + "','" + beanObj.getStTran_particulars() + "','" + beanObj.getStCard_Number() + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "/" + beanObj.getTrace() + "','" + beanObj.getAccount_repo() + "',to_char(TO_DATE('" + ttumBean.getStDate() + "','dd/mm/yyyy'),'dd/mm/yyyy'))";
        this.logger.info("INSERT_QUERY==" + INSERT_QUERY);
        getJdbcTemplate().execute(INSERT_QUERY);
        this.logger.info("INSERT_QUERY" + INSERT_QUERY);
        INSERT_QUERY = "INSERT INTO temp_TTUM_" + ttumBean.getStMerger_Category() + 
          "_SWITCH" + 
          "(" + insert_cols + ") VALUES ('" + ttumBean.getStMerger_Category() + 
          "-UNRECON-TTUM-2" + 
          "',SYSDATE,'" + ttumBean.getStEntry_by() + "',TO_DATE('" + beanObj.getStDate() + "','DDMMYY'),'" + beanObj.getStCreditAcc() + "','INR','999','C','" + 
          beanObj.getStAmount() + "','" + beanObj.getStTran_particulars() + "','" + beanObj.getStCard_Number() + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "/" + beanObj.getTrace() + "','" + beanObj.getAccount_repo() + "',to_char(TO_DATE('" + ttumBean.getStDate() + "','dd/mm/yyyy'),'dd/mm/yyyy'))";
        getJdbcTemplate().execute(INSERT_QUERY);
        this.logger.info("INSERT_QUERY" + INSERT_QUERY);
      } 
      int incount1 = 0;
      for (int k = 0; k < ttum_data_cbs.size(); k++) {
        incount1++;
        GenerateTTUMBean beanObj = new GenerateTTUMBean();
        beanObj = ttum_data_cbs.get(k);
        String INSERT_QUERY = "INSERT INTO temp_TTUM_" + ttumBean.getStMerger_Category() + 
          "_CBS" + 
          "(" + insert_cols + ") VALUES ('" + ttumBean.getStMerger_Category() + 
          "-UNRECON-TTUM-1" + 
          "',SYSDATE,'" + ttumBean.getStEntry_by() + "',TO_DATE('" + beanObj.getStDate() + "','DDMMYY'),'" + beanObj.getStDebitAcc() + "','INR','999','D','" + 
          beanObj.getStAmount() + "','" + beanObj.getStTran_particulars() + "','" + beanObj.getStCard_Number() + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "/" + beanObj.getTrace() + "','" + beanObj.getAccount_repo() + "',to_char(TO_DATE('" + ttumBean.getStDate() + "','dd/mm/yyyy'),'dd/mm/yyyy'))";
        this.logger.info("INSERT_QUERY==" + INSERT_QUERY);
        getJdbcTemplate().execute(INSERT_QUERY);
        INSERT_QUERY = "INSERT INTO temp_TTUM_" + ttumBean.getStMerger_Category() + 
          "_CBS" + 
          "(" + insert_cols + ") VALUES ('" + ttumBean.getStMerger_Category() + 
          "-UNRECON-TTUM-1" + 
          "',SYSDATE,'" + ttumBean.getStEntry_by() + "',TO_DATE('" + beanObj.getStDate() + "','DDMMYY'),'" + beanObj.getStCreditAcc() + "','INR','999','C','" + 
          beanObj.getStAmount() + "','" + beanObj.getStTran_particulars() + "','" + beanObj.getStCard_Number() + "','INR','" + beanObj.getStAmount() + "','" + 
          beanObj.getStRemark() + "/" + beanObj.getTrace() + "','" + beanObj.getAccount_repo() + "',to_char(TO_DATE('" + ttumBean.getStDate() + "','dd/mm/yyyy'),'dd/mm/yyyy'))";
        this.logger.info("INSERT_QUERY==" + INSERT_QUERY);
        getJdbcTemplate().execute(INSERT_QUERY);
      } 
      String GET_TTUM_SWITCH = "SELECT ACCOUNT_NUMBER,PART_TRAN_TYPE,TRANSACTION_AMOUNT,TRANSACTION_PARTICULARS,REMARKS,REFERENCE_NUMBER FROM temp_TTUM_VISA_ISS_SWITCH WHERE DCRS_REMARKS LIKE '%UNRECON-TTUM-2%' and filedate = '" + 
        ttumBean.getStDate() + "' order by REFERENCE_NUMBER,TRANSACTION_AMOUNT,ACCOUNT_NUMBER";
      this.logger.info("GET_TTUM_SWITCH==" + GET_TTUM_SWITCH);
      conn = getConnection();
      ps = conn.prepareStatement(GET_TTUM_SWITCH);
      rs = ps.executeQuery();
      while (rs.next()) {
        count++;
        GenerateTTUMBean generateTTUMBeanObj = new GenerateTTUMBean();
        generateTTUMBeanObj.setStCreditAcc(rs.getString("ACCOUNT_NUMBER").replaceAll(" ", " ").replaceAll("^0*", ""));
        generateTTUMBeanObj.setStDebitAcc("99937200010089");
        generateTTUMBeanObj.setStAmount(rs.getString("TRANSACTION_AMOUNT"));
        generateTTUMBeanObj.setStTran_particulars(rs.getString("TRANSACTION_PARTICULARS"));
        generateTTUMBeanObj.setStCard_Number(rs.getString("REFERENCE_NUMBER"));
        generateTTUMBeanObj.setStRemark(rs.getString("REMARKS"));
        generateTTUMBeanObj.setPart_tran_type(rs.getString("PART_TRAN_TYPE"));
        ttum_switch.add(generateTTUMBeanObj);
      } 
      this.logger.info("ttum_switch.size()" + ttum_switch.size());
      String GET_TTUM_CBS = "SELECT ACCOUNT_NUMBER,PART_TRAN_TYPE,TRANSACTION_AMOUNT,TRANSACTION_PARTICULARS,REMARKS,REFERENCE_NUMBER FROM temp_TTUM_VISA_ISS_CBS WHERE DCRS_REMARKS LIKE '%UNRECON-TTUM-1%' and filedate = '" + 
        ttumBean.getStDate() + "' order by REFERENCE_NUMBER,TRANSACTION_AMOUNT,ACCOUNT_NUMBER";
      this.logger.info("GET_TTUM_RECORDS_CBS " + GET_TTUM_CBS);
      ps1 = conn.prepareStatement(GET_TTUM_CBS);
      rs1 = ps1.executeQuery();
      while (rs1.next()) {
        GenerateTTUMBean generateTTUMBean1 = new GenerateTTUMBean();
        generateTTUMBean1.setStDebitAcc("99937200010089");
        generateTTUMBean1.setStCreditAcc(rs1.getString("ACCOUNT_NUMBER"));
        generateTTUMBean1.setStAmount(rs1.getString("TRANSACTION_AMOUNT"));
        String particular = rs1.getString("TRANSACTION_PARTICULARS");
        if (particular.contains("RPAY-POS"))
          particular = particular.replace(particular.substring(15, 24), ""); 
        generateTTUMBean1.setStTran_particulars(particular);
        generateTTUMBean1.setStCard_Number(rs1.getString("REFERENCE_NUMBER"));
        generateTTUMBean1.setStRemark(rs1.getString("REMARKS"));
        generateTTUMBean1.setPart_tran_type(rs1.getString("PART_TRAN_TYPE"));
        ttum_cbs.add(generateTTUMBean1);
      } 
      this.logger.info("ttum_cbs.size()" + ttum_cbs.size());
      String GET_TTUM_OTHER = "SELECT PAN,TRACE,LOCAL_DATE,AMOUNT_EQUIV FROM TEMP_VISA_DISPUTE where filedate = to_char(to_date('" + ttumBean.getStDate() + "','dd/mm/yyyy'),'dd-mon-yyyy')";
      this.logger.info("GET_TTUM_RECORDS_OTHER " + GET_TTUM_OTHER);
      ps2 = conn.prepareStatement(GET_TTUM_OTHER);
      rs2 = ps2.executeQuery();
      while (rs2.next()) {
        GenerateTTUMBean generateTTUMBean1 = new GenerateTTUMBean();
        generateTTUMBean1.setPan(rs2.getString("pan"));
        generateTTUMBean1.setTrace(rs2.getString("trace"));
        generateTTUMBean1.setLocal_date(rs2.getString("local_date"));
        generateTTUMBean1.setAmount_equiv(rs2.getString("amount_equiv"));
        ttum_others.add(generateTTUMBean1);
      } 
      Total_Data.add(Excel_headers);
      Total_Data.add(null);
      Total_Data.add(2, ttum_switch);
      Total_Data.add(3, ttum_cbs);
      Total_Data.add(4, ttum_others);
      String value = "";
      for (int loop = 1; loop <= 2; loop++) {
        if (loop == 1) {
          value = "CBS";
        } else {
          value = "SWITCH";
        } 
        String UPDATE_RECORDS = "UPDATE SETTLEMENT_" + ttumBean.getStCategory() + "_" + value + 
          " SET DCRS_REMARKS = '" + ttumBean.getStMerger_Category() + "-UNRECON-GENERATED-TTUM-" + loop + "'" + 
          " WHERE DCRS_REMARKS = '" + ttumBean.getStMerger_Category() + "-UNRECON-GENERATE-TTUM-" + loop + "'";
        String insert_Main_ttum = "insert into TTUM_" + ttumBean.getStMerger_Category() + "_" + value + 
          "(select * from temp_TTUM_" + ttumBean.getStMerger_Category() + "_" + value + ")";
        String truncate_temp_ttum = "truncate table temp_TTUM_" + ttumBean.getStMerger_Category() + "_" + value;
        this.logger.info("UPDATE_RECORDS " + UPDATE_RECORDS);
        this.logger.info("insert_Main_ttum " + insert_Main_ttum);
        this.logger.info("truncate_temp_ttum " + truncate_temp_ttum);
        getJdbcTemplate().execute(UPDATE_RECORDS);
        getJdbcTemplate().update(insert_Main_ttum);
        getJdbcTemplate().update(truncate_temp_ttum);
        this.logger.info("***** GenerateRupayTTUMDaoImpl.generateVisaDisputeTTUM End ****");
      } 
      return Total_Data;
    } catch (Exception e) {
      demo.logSQLException(e, "GenerateRupayTTUMDaoImpl.generateVisaDisputeTTUM");
      this.logger.error(" error in GenerateRupayTTUMDaoImpl.generateVisaDisputeTTUM", new Exception("GenerateRupayTTUMDaoImpl.generateVisaDisputeTTUM", e));
      return Total_Data;
    } finally {
      if (rset != null)
        rset.close(); 
      if (pstmt != null)
        pstmt.close(); 
      if (rset1 != null)
        rset1.close(); 
      if (pstmt1 != null)
        pstmt1.close(); 
      if (rs != null)
        rs.close(); 
      if (ps != null)
        ps.close(); 
      if (rs1 != null)
        rs1.close(); 
      if (ps1 != null)
        ps1.close(); 
      if (rs2 != null)
        rs2.close(); 
      if (ps2 != null)
        ps2.close(); 
      if (conn != null)
        conn.close(); 
    } 
  }
}
