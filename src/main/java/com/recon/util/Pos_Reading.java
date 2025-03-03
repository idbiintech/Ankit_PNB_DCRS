package com.recon.util;


import com.recon.model.CompareSetupBean;
import com.recon.service.ReadImpData;
import com.recon.service.impl.ReadImpDataImpl;
import com.recon.service.impl.To_List_Impl;
import com.recon.util.OracleConn;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

public class Pos_Reading {
  int count = 0;
  
  Connection con;
  
  Statement st;
  
  int part_id;
  
  String man_flag = "N", upload_flag = "Y";
  
  String value = null;
  
  private static final Logger logger = Logger.getLogger(com.recon.util.Pos_Reading.class);
  
  public boolean uploadPOSData(MultipartFile file, String fileName, CompareSetupBean setupBean2) throws ParseException {
    String[] filenameSplit = fileName.split("_");
    DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    String filedt = null;
    String fileDate = "";
    String flag = "";
    String new_date = "";
    try {
      System.out.println(fileName);
      filedt = setupBean2.getFileDate();
      flag = "UPLOAD_FLAG";
      this.upload_flag = "Y";
      this.part_id = 1;
      CompareSetupBean setupBean = new CompareSetupBean();
      setupBean.setCategory("MASTERCARD");
      setupBean.setFileDate(filedt);
      setupBean.setStFileName("POS");
      setupBean.setInFileId(35);
      System.out.println("datew " + setupBean2.getFileDate());
      OracleConn conn = new OracleConn();
      if (uploadData(file, conn.getconn(), fileName, setupBean2.getFileDate()))
        System.out.println("datewwe " + setupBean.getFileDate()); 
      return true;
    } catch (Exception e) {
      System.out.println("Erro Occured");
      e.printStackTrace();
      logger.error(e.getMessage());
      return false;
    } 
  }
  
  public boolean updatefile(CompareSetupBean setupBean) {
    try {
      int rowupdate = 0;
      int rowupdate1 = 0;
      OracleConn conn = new OracleConn();
      String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'POS' and FILE_CATEGORY='MASTERCARD'   ";
      this.con = conn.getconn();
      this.st = this.con.createStatement();
      ResultSet rs = this.st.executeQuery(switchList);
      while (rs.next()) {
        String query = " insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) values (" + 
          rs.getString("FILEID") + ",to_date('" + setupBean.getFileDate() + "','dd-mm-yy'),'AUTOMATION',sysdate,'" + setupBean.getCategory() + "','" + rs.getString("file_subcategory") + "'" + 
          ",'" + this.upload_flag + "','N','N','N','N','" + this.man_flag + "')";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        this.st.executeUpdate(query);
        this.count++;
        String insert_count = "update Main_File_Upload_Dtls set file_count='" + this.count + "'  WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + setupBean.getFileDate() + "' ,'DD-MM-YY') " + 
          " AND CATEGORY = 'MASTERCARD' AND FileId = '" + setupBean.getInFileId() + "'  ";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        this.st.executeUpdate(insert_count);
        String count = "SELECT file_count from Main_File_Upload_Dtls WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + setupBean.getFileDate() + "' ,'DD-MM-YY') " + 
          " AND CATEGORY = 'MASTERCARD' AND FileId = '" + setupBean.getInFileId() + "'   ";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        ResultSet rs1 = this.st.executeQuery(count);
        while (rs1.next()) {
          this.value = rs1.getString("file_count");
          if (this.value.equals("6")) {
            String query1 = "Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='Y'  WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + setupBean.getFileDate() + "' ,'DD-MM-YY') " + 
              " AND CATEGORY = 'MASTERCARD' AND FileId = " + rs.getString("FILEID") + " ";
            this.con = conn.getconn();
            this.st = this.con.createStatement();
            rowupdate = this.st.executeUpdate(query1);
            continue;
          } 
          String query2 = "Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='Y'  WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + setupBean.getFileDate() + "' ,'DD-MM-YY') " + 
            " AND CATEGORY = 'MASTERCARD' AND FileId = " + rs.getString("FILEID") + " ";
          this.con = conn.getconn();
          this.st = this.con.createStatement();
          rowupdate1 = this.st.executeUpdate(query2);
        } 
      } 
      return true;
    } catch (Exception ex) {
      System.out.println(ex);
      ex.printStackTrace();
      logger.error(ex.getMessage());
      return false;
    } finally {
      try {
        this.con.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } 
    } 
  }
  
  public boolean updateFlag(String flag, CompareSetupBean setupBean) {
    try {
      OracleConn conn = new OracleConn();
      String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'POS' and FILE_CATEGORY='MASTERCARD'  ";
      this.con = conn.getconn();
      this.st = this.con.createStatement();
      ResultSet rs = this.st.executeQuery(switchList);
      int rowupdate = 0;
      int rowupdate1 = 0;
      while (rs.next()) {
        int val = 0;
        String query = "Update MAIN_FILE_UPLOAD_DTLS set " + flag + " ='Y'  WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + setupBean.getFileDate() + "' ,'DD-MM-YY') " + 
          " AND CATEGORY = 'MASTERCARD' AND FileId = " + rs.getString("FILEID") + " ";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        rowupdate = this.st.executeUpdate(query);
        String count1 = "SELECT file_count from Main_File_Upload_Dtls WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + setupBean.getFileDate() + "' ,'DD-MM-YY') " + 
          " AND CATEGORY = 'MASTERCARD' AND FileId = '" + setupBean.getInFileId() + "'   ";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        ResultSet rs12 = this.st.executeQuery(count1);
        while (rs12.next()) {
          val = rs12.getInt("file_count");
          val++;
        } 
        String insert_count = "update Main_File_Upload_Dtls set file_count='" + val + "'  WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + setupBean.getFileDate() + "' ,'DD-MM-YY')  " + 
          " AND CATEGORY = 'MASTERCARD' AND FileId = '" + setupBean.getInFileId() + "'  ";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        this.st.executeUpdate(insert_count);
        String count = "SELECT file_count from Main_File_Upload_Dtls WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + setupBean.getFileDate() + "' ,'DD-MM-YY') " + 
          " AND CATEGORY = 'MASTERCARD' AND FileId = '" + setupBean.getInFileId() + "'   ";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        ResultSet rs1 = this.st.executeQuery(count);
        while (rs1.next()) {
          this.value = rs1.getString("file_count");
          if (this.value.equals("6")) {
            String query1 = "Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='Y'  WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + setupBean.getFileDate() + "' ,'DD-MM-YY') AND CATEGORY = 'MASTERCARD' AND FileId = " + rs.getString("FILEID") + " ";
            this.con = conn.getconn();
            this.st = this.con.createStatement();
            rowupdate = this.st.executeUpdate(query1);
            continue;
          } 
          String query2 = "Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG='Y'  WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + setupBean.getFileDate() + "' ,'DD-MM-YY') AND CATEGORY = 'MASTERCARD' AND FileId = " + rs.getString("FILEID") + " ";
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
      logger.error(ex.getMessage());
      return false;
    } finally {
      try {
        this.st.close();
        this.con.close();
      } catch (SQLException e) {
        e.printStackTrace();
        logger.error(e.getMessage());
      } 
    } 
  }
  
  private int getFileCount(CompareSetupBean setupBean) {
    try {
      int count = 0;
      OracleConn conn = new OracleConn();
      String query = "Select count (*) from MAIN_FILE_UPLOAD_DTLS  WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + setupBean.getFILEDATE() + "' ,'DD-MM-YY') AND CATEGORY = '" + 
        setupBean.getCategory() + 
        "' AND FileId = " + 
        setupBean.getInFileId();
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
        logger.error(e.getMessage());
      } 
    } 
  }
  
  public boolean uploadData(MultipartFile file, Connection con, String filename, String new_date) {
    try {
      System.setProperty("file.encoding", "latin1");
      Field charset = Charset.class.getDeclaredField("defaultCharset");
      charset.setAccessible(true);
      charset.set(null, null);
      StringBuilder sb = new StringBuilder();
      BufferedReader bfrd = new BufferedReader(new InputStreamReader(file.getInputStream()));
      System.out.println("ENCODING" + System.getProperty("file.decoding"));
      Scanner sc = new Scanner(bfrd);
      while (sc.hasNextLine()) {
        sc.useDelimiter(",//s");
        sb.append(sc.next());
      } 
      try {
        To_List_Impl to_List_Impl = new To_List_Impl();
        ReadImpData rid = ReadImpDataImpl.getInstance();
        ReadImpDataImpl jcsv = (ReadImpDataImpl)ReadImpDataImpl.getInstance();
        rid._read1251(to_List_Impl.to_block(sb.toString()), filename, new_date);
      } catch (NullPointerException ne) {
        ne.printStackTrace();
        System.out.println("Inside null pointer exception");
        System.out.println("Complete");
        return true;
      } catch (Exception e) {
        e.printStackTrace();
        logger.error(e.getMessage());
        System.exit(0);
      } 
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      logger.error(e.getMessage());
      System.exit(0);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      logger.error(e.getMessage());
      System.exit(0);
    } catch (IOException e) {
      e.printStackTrace();
      logger.error(e.getMessage());
      System.exit(0);
    } catch (SecurityException e1) {
      e1.printStackTrace();
      logger.error(e1.getMessage());
      System.exit(0);
    } catch (NoSuchFieldException e1) {
      e1.printStackTrace();
      logger.error(e1.getMessage());
      System.exit(0);
    } catch (IllegalAccessException e1) {
      e1.printStackTrace();
      logger.error(e1.getMessage());
      System.exit(0);
    } 
    return true;
  }
  
  public String chkFlag(String flag, CompareSetupBean setupBean) {
    try {
      ResultSet rs = null;
      String flg = "";
      OracleConn conn = new OracleConn();
      String query = "SELECT " + 
        flag + 
        " FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('" + 
        setupBean.getFileDate() + 
        "','mm/dd/yyyy'),'mm/dd/yyyy')  " + " AND CATEGORY = '" + 
        setupBean.getCategory() + "' AND FileId = " + 
        setupBean.getInFileId() + " ";
      query = " SELECT CASE WHEN exists (" + query + ") then (" + query + 
        ") else 'N' end as FLAG from dual";
      System.out.println(query);
      this.con = conn.getconn();
      this.st = this.con.createStatement();
      rs = this.st.executeQuery(query);
      while (rs.next())
        flg = rs.getString(1); 
      return flg;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    } finally {
      try {
        this.con.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } 
    } 
  }
  
  public static boolean read_method(CompareSetupBean setupBean, Connection conn, MultipartFile file) {
    try {
      com.recon.util.Pos_Reading readcbs = new com.recon.util.Pos_Reading();
      String filename = file.getOriginalFilename();
      System.out.println(file.getName());
      readcbs.uploadPOSData(file, filename, setupBean);
      System.out.println("Process Completed");
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } 
  }
}
