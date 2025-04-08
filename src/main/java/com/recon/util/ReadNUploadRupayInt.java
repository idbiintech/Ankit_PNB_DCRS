package com.recon.util;


import com.recon.model.CompareSetupBean;
import com.recon.util.OracleConn;
import com.recon.util.RupayHeaderUtil;
import com.recon.util.RupayUtilBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadNUploadRupayInt {
  Connection con;
  
  int file_count = 0;
  
  int upload_count = 0;
  
  Statement st;
  
  ResultSet rs;
  
  int part_id;
  
  String man_flag = "N", upload_flag = "Y";
  
  String insert = "INSERT  INTO RUPAY_RUPAY_RAWDATA (MTI,Function_Code ,Record_Number,Member_Institution_ID_Code,Unique_File_Name,Date_Settlement,Product_Code,Settlement_BIN,File_Category,Version_Number,Entire_File_Reject_Indicator,File_Reject_Reason_Code,Transactions_Count,Run_Total_Amount,Acquirer_Institution_ID_code,Amount_Settlement,Amount_Transaction,Approval_Code,Acquirer_Reference_Data,Case_Number,Currency_Code_Settlement,Currency_Code_Transaction,Conversion_Rate_Settlement,Card_Acceptor_Addi_Addr,Card_Acceptor_Terminal_ID,Card_Acceptor_Zip_Code,DateandTime_Local_Transaction,TXNFunction_Code ,Late_Presentment_Indicator,TXNMTI,Primary_Account_Number,TXNRecord_Number,RGCS_Received_date,Settlement_DR_CR_Indicator,Txn_Desti_Insti_ID_code,Txn_Origin_Insti_ID_code,Card_Holder_UID,Amount_Billing,Currency_Code_Billing,Conversion_Rate_billing,Message_Reason_Code,Fee_DR_CR_Indicator1,Fee_amount1,Fee_Currency1,Fee_Type_Code1,Interchange_Category1,Fee_DR_CR_Indicator2,Fee_amount2,Fee_Currency2,Fee_Type_Code2,Interchange_Category2,Fee_DR_CR_Indicator3,Fee_amount3,Fee_Currency3,Fee_Type_Code3,Interchange_Category3,Fee_DR_CR_Indicator4,Fee_amount4,Fee_Currency4,Fee_Type_Code4,Interchange_Category4,Fee_DR_CR_Indicator5,Fee_amount5,Fee_Currency5,Fee_Type_Code5,Interchange_Category5,Trl_FUNCTION_CODE,Trl_RECORD_NUMBER,flag,FILEDATE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'))";
  
  String update = "update RUPAY_RUPAY_RAWDATA  set Trl_FUNCTION_CODE = ? , TRL_RECORD_NUMBER= ?,TRANSACTIONS_COUNT=? where to_char(CREATEDDATE,'dd-mm-yy')=to_char(sysdate,'dd-mm-yy')";
  
  String trl_nFunCd = null, trl_nRecNum = null, transactions_count = null;
  
  public boolean uploadCBSData(String fileName, String filedate, String filepath) {
    String[] filenameSplit = fileName.split("_");
    DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    Date filedt = null;
    String fileDate = "";
    String flag = "";
    try {
      String getFile_count = "Select FILE_COUNT from Main_fileSource where FILE_CATEGORY ='RUPAY' and filename='RUPAY'  and FILE_SUBCATEGORY = 'INTERNATIONAL' ";
      try {
        OracleConn conn = new OracleConn();
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        System.out.println("SELECT CASE WHEN exists (" + getFile_count + ") then (" + getFile_count + ") else 0 end as FLAG from dual  ");
        this.rs = this.st.executeQuery("SELECT CASE WHEN exists (" + getFile_count + ") then (" + getFile_count + ") else 0 end as FLAG from dual  ");
        while (this.rs.next())
          this.file_count = this.rs.getInt(1); 
      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println(ex.toString());
      } finally {
        this.rs.close();
        this.con.close();
        this.st.close();
      } 
      String getupld_count = "Select FILE_COUNT from main_file_upload_dtls where CATEGORY ='RUPAY'  and FILE_SUBCATEGORY = 'INTERNATIONAL' and filedate = to_date('" + filedate + "','dd/mm/yyyy')  ";
      try {
        OracleConn conn = new OracleConn();
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        this.rs = this.st.executeQuery(getupld_count);
        while (this.rs.next())
          this.upload_count = this.rs.getInt(1); 
      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println(ex.toString());
      } finally {
        this.rs.close();
        this.con.close();
        this.st.close();
      } 
      if (this.file_count > this.upload_count) {
        System.out.println(fileName);
        filedt = format.parse(filedate);
        flag = "UPLOAD_FLAG";
        System.out.println(filedt);
        format = new SimpleDateFormat("dd/MM/yyyy");
        fileDate = format.format(filedt);
        CompareSetupBean setupBean = new CompareSetupBean();
        setupBean.setCategory("RUPAY");
        setupBean.setFileDate(fileDate);
        setupBean.setStFileName("RUPAY");
        setupBean.setInFileId(8);
        OracleConn con = new OracleConn();
        if (uploadData(con.getconn(), fileName, fileDate, filepath))
          if (getFileCount(setupBean) > 0) {
            updateFlag(flag, setupBean);
          } else {
            updatefile(setupBean);
          }  
        return true;
      } 
      System.out.println("Upload Count exceed");
      return false;
    } catch (Exception e) {
      System.out.println("Erro Occured");
      e.printStackTrace();
      return false;
    } 
  }
  
  public boolean updatefile(CompareSetupBean setupBean) {
    try {
      OracleConn conn = new OracleConn();
      String query = " insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG,file_count) values (" + 
        setupBean.getInFileId() + ",to_date('" + setupBean.getFileDate() + "','dd/mm/yyyy'),'AUTOMATION',sysdate,'" + setupBean.getCategory() + "','INTERNATIONAL'" + 
        ",'Y','N','N','N','N','N',1)";
      this.con = conn.getconn();
      this.st = this.con.createStatement();
      this.st.executeUpdate(query);
      return true;
    } catch (Exception ex) {
      System.out.println(ex);
      ex.printStackTrace();
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
      String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'RUPAY' AND FILE_SUBCATEGORY = 'INTERNATIONAL' and FILE_CATEGORY='RUPAY'  ";
      this.con = conn.getconn();
      this.st = this.con.createStatement();
      ResultSet rs = this.st.executeQuery(switchList);
      int rowupdate = 0;
      while (rs.next()) {
        String query = "Update MAIN_FILE_UPLOAD_DTLS set " + flag + " ='Y',FILE_COUNT = " + (this.upload_count + 1) + "  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('" + setupBean.getFileDate() + "','dd/mm/yyyy'),'dd/mm/yyyy') " + 
          " AND CATEGORY = 'RUPAY'    AND   FileId = " + rs.getString("FILEID") + " ";
        this.con = conn.getconn();
        this.st = this.con.createStatement();
        rowupdate = this.st.executeUpdate(query);
      } 
      if (rowupdate > 0)
        return true; 
      return false;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    } finally {
      try {
        this.st.close();
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
      String query = "Select count (*) from MAIN_FILE_UPLOAD_DTLS  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('" + 
        setupBean.getFileDate() + 
        "','dd/mm/yyyy'),'dd/mm/yyyy') " + 
        " AND upper(CATEGORY) = '" + setupBean.getCategory().toUpperCase() + "' AND FILE_SUBCATEGORY='INTERNATIONAL' AND FileId = " + setupBean.getInFileId() + " ";
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
      } 
    } 
  }
  
  public boolean uploadData(Connection con, String filename, String filedate, String filepath) {
    int feesize = 1;
    try {
      FileInputStream fis = new FileInputStream(filepath);
      PreparedStatement ps = con.prepareStatement(this.insert);
      PreparedStatement updtps = con.prepareStatement(this.update);
      Pattern TAG_REGEX = Pattern.compile(">(.+?)</");
      Pattern node_REGEX = Pattern.compile("<(.+?)>");
      BufferedReader br = new BufferedReader(new InputStreamReader(fis));
      String thisLine = null;
      int count = 1;
      String hdr = "", trl = "";
      RupayUtilBean utilBean = new RupayUtilBean();
      RupayHeaderUtil headerUtil = new RupayHeaderUtil();
      System.out.println("Process started" + (new Date()).getTime());
      while ((thisLine = br.readLine()) != null) {
        Matcher nodeMatcher = node_REGEX.matcher(thisLine);
        nodeMatcher.find();
        if (!nodeMatcher.group(1).equalsIgnoreCase("Txn")) {
          if (nodeMatcher.group(1).equalsIgnoreCase("Hdr")) {
            hdr = "hdr";
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("/Hdr")) {
            hdr = "";
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nDtTmFlGen")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnDtTmFlGen(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nMemInstCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnMemInstCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nUnFlNm")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnUnFlNm(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nProdCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnProdCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nSetBIN")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnSetBIN(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFlCatg")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnFlCatg(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nVerNum")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnVerNum(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nAcqInstCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnAcqInstCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nAmtSet")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            double amtTxn = Double.parseDouble(matcher.group(1));
            amtTxn /= 100.0D;
            System.out.println("AMTTXN " + amtTxn);
            utilBean.setnAmtTxn(String.valueOf(amtTxn));
            utilBean.setnAmtSet(String.valueOf(amtTxn));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nAmtTxn")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            double amtTxn = Double.parseDouble(matcher.group(1));
            amtTxn /= 100.0D;
            System.out.println("AMTTXN " + amtTxn);
            utilBean.setnAmtTxn(String.valueOf(amtTxn));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nApprvlCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnApprvlCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nARD")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnARD(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdSet")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCcyCdSet(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdTxn")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCcyCdTxn(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nConvRtSet")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnConvRtSet(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpAddAdrs")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCrdAcpAddAdrs(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcptTrmId")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCrdAcptTrmId(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpZipCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCrdAcpZipCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nDtSet")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            if (hdr.equalsIgnoreCase("hdr")) {
              headerUtil.setnDtSet(matcher.group(1));
              continue;
            } 
            utilBean.setnDtSet(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nDtTmLcTxn")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnDtTmLcTxn(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFunCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            if (hdr.equalsIgnoreCase("hdr")) {
              headerUtil.setnFunCd(matcher.group(1));
              continue;
            } 
            if (hdr.equalsIgnoreCase("Trl")) {
              this.trl_nFunCd = matcher.group(1);
              System.out.println(this.trl_nFunCd);
              continue;
            } 
            utilBean.setnFunCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nLtPrsntInd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnLtPrsntInd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nMTI")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            if (hdr.equalsIgnoreCase("hdr")) {
              headerUtil.setnMTI(matcher.group(1));
              continue;
            } 
            utilBean.setnMTI(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nPAN")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnPAN(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nRecNum")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            if (hdr.equalsIgnoreCase("hdr")) {
              headerUtil.setnRecNum(matcher.group(1));
              continue;
            } 
            if (hdr.equalsIgnoreCase("Trl")) {
              headerUtil.setTrl_nRecNum(matcher.group(1));
              this.trl_nRecNum = matcher.group(1);
              System.out.println(this.trl_nRecNum);
              continue;
            } 
            utilBean.setnRecNum(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nRGCSRcvdDt")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnRGCSRcvdDt(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nSetDCInd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnSetDCInd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nTxnDesInstCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnTxnDesInstCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nTxnOrgInstCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnTxnOrgInstCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nUID")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnUID(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFeeDCInd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnFeeDCInd1(matcher.group(1));
                continue;
              case 2:
                System.out.println("setnFeeDCInd2");
                utilBean.setnFeeDCInd2(matcher.group(1));
                continue;
              case 3:
                System.out.println("setnFeeDCInd3");
                utilBean.setnFeeDCInd3(matcher.group(1));
                continue;
              case 4:
                System.out.println("setnFeeDCInd4");
                utilBean.setnFeeDCInd4(matcher.group(1));
                continue;
              case 5:
                System.out.println("setnFeeDCInd5");
                utilBean.setnFeeDCInd5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFeeAmt")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnFeeAmt1(matcher.group(1));
                continue;
              case 2:
                System.out.println("setnFeeAmt2");
                utilBean.setnFeeAmt2(matcher.group(1));
                continue;
              case 3:
                System.out.println("setnFeeAmt3");
                utilBean.setnFeeAmt3(matcher.group(1));
                continue;
              case 4:
                System.out.println("setnFeeAmt4");
                utilBean.setnFeeAmt4(matcher.group(1));
                continue;
              case 5:
                System.out.println("setnFeeAmt5");
                utilBean.setFeeAmt5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFeeCcy")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnFeeCcy1(matcher.group(1));
                continue;
              case 2:
                System.out.println("nFeeCcy2");
                utilBean.setnFeeCcy2(matcher.group(1));
                continue;
              case 3:
                System.out.println("nFeeCcy3");
                utilBean.setnFeeCcy3(matcher.group(1));
                continue;
              case 4:
                System.out.println("nFeeCcy4");
                utilBean.setnFeeCcy4(matcher.group(1));
                continue;
              case 5:
                System.out.println("nFeeCcy5");
                utilBean.setnFeeCcy5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFeeTpCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnFeeTpCd1(matcher.group(1));
                continue;
              case 2:
                utilBean.setnFeeTpCd2(matcher.group(1));
                continue;
              case 3:
                utilBean.setnFeeTpCd3(matcher.group(1));
                continue;
              case 4:
                utilBean.setnFeeTpCd4(matcher.group(1));
                continue;
              case 5:
                utilBean.setnFeeTpCd5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nIntrchngCtg")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnIntrchngCtg1(matcher.group(1));
                continue;
              case 2:
                utilBean.setnIntrchngCtg2(matcher.group(1));
                continue;
              case 3:
                utilBean.setnIntrchngCtg3(matcher.group(1));
                continue;
              case 4:
                utilBean.setnIntrchngCtg4(matcher.group(1));
                continue;
              case 5:
                utilBean.setnIntrchngCtg5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("/Fee")) {
            feesize++;
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCaseNum")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCaseNum(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nContNum")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnContNum(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFulParInd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnFulParInd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nProcCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnProdCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nAmtBil")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnAmtBil(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdBil")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCcyCdBil(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nConvRtBil")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnConvRtBil(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nMsgRsnCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnMsgRsnCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nRnTtlAmt")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnRnTtlAmt(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nTxnCnt")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnTxnCnt(matcher.group(1));
            this.transactions_count = matcher.group(1);
            System.out.println(this.transactions_count);
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("Trl")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            hdr = "Trl";
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("/Trl")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            hdr = "";
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("/Txn")) {
            feesize = 1;
            ps.setString(1, headerUtil.getnMTI());
            ps.setString(2, headerUtil.getnFunCd());
            ps.setString(3, headerUtil.getnRecNum());
            ps.setString(4, headerUtil.getnMemInstCd());
            ps.setString(5, headerUtil.getnUnFlNm());
            ps.setString(6, headerUtil.getnDtSet());
            ps.setString(7, headerUtil.getnProdCd());
            ps.setString(8, headerUtil.getnSetBIN());
            ps.setString(9, headerUtil.getnFlCatg());
            ps.setString(10, headerUtil.getnVerNum());
            ps.setString(11, (String)null);
            ps.setString(12, (String)null);
            ps.setString(13, headerUtil.getnTxnCnt());
            ps.setString(14, headerUtil.getnRnTtlAmt());
            ps.setString(15, utilBean.getnAcqInstCd());
            ps.setString(16, utilBean.getnAmtSet());
            ps.setString(17, utilBean.getnAmtTxn());
            ps.setString(18, utilBean.getnApprvlCd());
            ps.setString(19, utilBean.getnARD());
            ps.setString(20, utilBean.getnCaseNum());
            ps.setString(21, utilBean.getnCcyCdSet());
            ps.setString(22, utilBean.getnCcyCdTxn());
            ps.setString(23, utilBean.getnConvRtSet());
            ps.setString(24, utilBean.getnCrdAcpAddAdrs());
            ps.setString(25, utilBean.getnCrdAcptTrmId());
            ps.setString(26, utilBean.getnCrdAcpZipCd());
            ps.setString(27, utilBean.getnDtTmLcTxn());
            ps.setString(28, utilBean.getnFunCd());
            ps.setString(29, utilBean.getnLtPrsntInd());
            ps.setString(30, utilBean.getnMTI());
            ps.setString(31, utilBean.getnPAN());
            ps.setString(32, utilBean.getnRecNum());
            ps.setString(33, utilBean.getnRGCSRcvdDt());
            ps.setString(34, utilBean.getnSetDCInd());
            ps.setString(35, utilBean.getnTxnDesInstCd());
            ps.setString(36, utilBean.getnTxnOrgInstCd());
            ps.setString(37, utilBean.getnUID());
            ps.setString(38, utilBean.getnAmtBil());
            ps.setString(39, utilBean.getnCcyCdBil());
            ps.setString(40, utilBean.getnConvRtBil());
            ps.setString(41, utilBean.getnMsgRsnCd());
            ps.setString(42, utilBean.getnFeeDCInd1());
            ps.setString(43, utilBean.getnFeeAmt1());
            ps.setString(44, utilBean.getnFeeCcy1());
            ps.setString(45, utilBean.getnFeeTpCd1());
            ps.setString(46, utilBean.getnIntrchngCtg1());
            ps.setString(47, utilBean.getnFeeDCInd2());
            ps.setString(48, utilBean.getnFeeAmt2());
            ps.setString(49, utilBean.getnFeeCcy2());
            ps.setString(50, utilBean.getnFeeTpCd2());
            ps.setString(51, utilBean.getnIntrchngCtg2());
            ps.setString(52, utilBean.getnFeeDCInd3());
            ps.setString(53, utilBean.getnFeeAmt3());
            ps.setString(54, utilBean.getnFeeCcy3());
            ps.setString(55, utilBean.getnFeeTpCd3());
            ps.setString(56, utilBean.getnIntrchngCtg3());
            ps.setString(57, utilBean.getnFeeDCInd4());
            ps.setString(58, utilBean.getnFeeAmt4());
            ps.setString(59, utilBean.getnFeeCcy4());
            ps.setString(60, utilBean.getnFeeTpCd4());
            ps.setString(61, utilBean.getnIntrchngCtg4());
            ps.setString(62, utilBean.getnFeeDCInd5());
            ps.setString(63, utilBean.getFeeAmt5());
            ps.setString(64, utilBean.getnFeeCcy5());
            ps.setString(65, utilBean.getnFeeTpCd5());
            ps.setString(66, utilBean.getnIntrchngCtg5());
            ps.setString(67, headerUtil.getTrl_nFunCd());
            ps.setString(68, headerUtil.getTrl_nRecNum());
            ps.setString(69, "I");
            ps.setString(70, filedate);
            ps.addBatch();
            utilBean = new RupayUtilBean();
            count++;
            if (count == 20000) {
              count = 1;
              ps.executeBatch();
              System.out.println("Executed batch");
              count++;
            } 
          } 
        } 
      } 
      ps.executeBatch();
      updtps.setString(1, this.trl_nFunCd);
      System.out.println(this.trl_nFunCd);
      updtps.setString(2, this.trl_nRecNum);
      System.out.println(this.trl_nRecNum);
      updtps.setString(3, this.transactions_count);
      System.out.println(this.transactions_count);
      System.out.println(this.update);
      updtps.executeUpdate();
      System.out.println("Process ended" + (new Date()).getTime());
      br.close();
      ps.close();
      updtps.close();
      con.close();
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    } 
  }
  
  public String chkFlag(String flag, CompareSetupBean setupBean) {
    try {
      ResultSet rs = null;
      String flg = "";
      OracleConn conn = new OracleConn();
      String query = "SELECT " + 
        flag + 
        " FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('" + 
        setupBean.getFileDate() + 
        "','dd/mm/yyyy'),'dd/mm/yyyy')  " + " AND CATEGORY = '" + 
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
  
  public static void main(String[] args) {
    com.recon.util.ReadNUploadRupayInt readcbs = new com.recon.util.ReadNUploadRupayInt();
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter a file path: ");
    System.out.flush();
    String filename = scanner.nextLine();
    System.out.print("Enter a file date in (dd/MM/yyyy) format: ");
    String filedate = scanner.nextLine();
    File file = new File(filename);
    System.out.println(file.getName());
    readcbs.uploadCBSData(file.getName(), filedate, file.getPath());
    System.out.println("Process Completed");
  }
}
