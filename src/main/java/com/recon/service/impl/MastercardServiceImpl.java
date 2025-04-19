package com.recon.service.impl;


import com.recon.model.MastercardUploadBean;
import com.recon.service.MastercardService;
import com.recon.util.ReadATMMasterRawdata;
import com.recon.util.ReadMastercard461Rawdata;
import com.recon.util.ReadMastercardInvoiceRawdata;
import com.recon.util.ReadMastercardTADFile;
import com.recon.util.ReadingMastercard140File;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.web.multipart.MultipartFile;

public class MastercardServiceImpl extends JdbcDaoSupport implements MastercardService {
  private static final String O_ERROR_MESSAGE = "o_error_message";
  
  public HashMap<String, Object> TADfileReading(MultipartFile file, MastercardUploadBean beanObj) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      ReadMastercardTADFile readFile = new ReadMastercardTADFile();
      output = readFile.readTADFile(file, getConnection(), beanObj);
    } catch (Exception e) {
      System.out.println("Exception in fileReading " + e);
    } 
    return output;
  }
  
  public HashMap<String, Object> TAfileReading(MultipartFile file, MastercardUploadBean beanObj) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      ReadMastercardTADFile readFile = new ReadMastercardTADFile();
      output = readFile.readTADFile(file, getConnection(), beanObj);
    } catch (Exception e) {
      System.out.println("Exception in fileReading " + e);
    } 
    return output;
  }
  
  public HashMap<String, Object> checkFileUpload(MastercardUploadBean beanObj, String file_name) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      String qy = "";
      if (beanObj.getFileName().equalsIgnoreCase("461")) {
        qy = "SELECT COUNT(*) FROM  mastercard_461_rawdata where FILEDATE= STR_to_date('" + beanObj.getFileDate() + "','%Y/%m/%d') and FILENAME = '" + file_name + "'";
      } else if (beanObj.getFileName().equalsIgnoreCase("INVOICE")) {
        qy = "SELECT COUNT(*) FROM  mastercard_invoice_rawtada where FILEDATE=  STR_to_date('" + beanObj.getFileDate() + "','%Y/%m/%d')";
      }else if (beanObj.getFileName().equalsIgnoreCase("INVOICETXT")) {
          qy = "SELECT COUNT(*) FROM  mastercard_invoicetxt_rawdata where FILEDATE=  STR_to_date('" + beanObj.getFileDate() + "','%Y/%m/%d') and FILENAME='"+file_name+"'";
        } else if (beanObj.getFileName().equalsIgnoreCase("INVOICEPDF")) {
        qy = "SELECT COUNT(*) FROM mastercard_invoicepdf_rawtada where FILEDATE =  STR_to_date('" + beanObj.getFileDate() + "','%Y/%m/%d') and FILENAME='"+file_name+"'";
      } else if (beanObj.getFileName().equalsIgnoreCase("ATMMASTER")) {
        qy = "SELECT COUNT(*) FROM ubi_atm_master where FILEDATE=  STR_to_date('" + beanObj.getFileDate() + "','%Y/%m/%d')";
      } else {
        qy = "SELECT COUNT(*) FROM  mastercard_settlement_rawdata where FILEDATE  =  STR_to_date('" + beanObj.getFileDate() + "','%Y/%m/%d')  and FILE_NAME = '" + file_name + "'";
      } 
      System.out.println("qy " + qy);
      int uploadCount = ((Integer)getJdbcTemplate().queryForObject(qy, 
          new Object[0], Integer.class)).intValue();
      if (uploadCount > 0) {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "File is already Uploaded");
      } else {
        output.put("result", Boolean.valueOf(true));
      } 
    } catch (Exception e) {
      System.out.println("Exception in MastercardServiceImpl " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occured while Validating");
    } 
    return output;
  }
  
  public HashMap<String, Object> checkFileUploadVISA(String file_name) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      String qy = "select 'VISA'||visa_seq.nextval AS SEQ from dual";
      String uploadCount = (String)getJdbcTemplate().queryForObject(qy, 
          new Object[0], String.class);
      output.put("result", Boolean.valueOf(true));
      output.put("msg", uploadCount);
    } catch (Exception e) {
      System.out.println("Exception in MastercardServiceImpl " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occured while Validating");
    } 
    return output;
  }
  
  public HashMap<String, Object> checkFileUploadE057(MastercardUploadBean beanObj, String file_name) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      String qy = "";
      qy = "SELECT COUNT(*) FROM mastercard_t057_rawtada where FILEDATE=STR_to_date('" + beanObj.getFileDate() + "','%Y/%m/%d') and FILENAME = '" + file_name + "'";
      System.out.println("qy " + qy);
      int uploadCount = ((Integer)getJdbcTemplate().queryForObject(qy, 
          new Object[0], Integer.class)).intValue();
      if (uploadCount > 0) {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "File is already Uploaded");
      } else {
        output.put("result", Boolean.valueOf(true));
      } 
    } catch (Exception e) {
      System.out.println("Exception in MastercardServiceImpl " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occured while Validating");
    } 
    return output;
  }
  
  public HashMap<String, Object> file140Reading(MultipartFile file, MastercardUploadBean beanObj) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      if (beanObj.getFileName().equalsIgnoreCase("140")) {
        ReadingMastercard140File readFile = new ReadingMastercard140File();
        output = readFile.read140File(file, getConnection(), beanObj);
      } else if (beanObj.getFileName().equalsIgnoreCase("INVOICE")) {
        ReadMastercardInvoiceRawdata readFile = new ReadMastercardInvoiceRawdata();
        output = readFile.readINVOICEFile(file, getConnection(), beanObj);
      } else if (beanObj.getFileName().equalsIgnoreCase("INVOICETXT")) {
          ReadMastercardInvoiceRawdata readFile = new ReadMastercardInvoiceRawdata();
          output = readFile.readINVOICETXTFile(file, getConnection(), beanObj);
        } else if (beanObj.getFileName().equalsIgnoreCase("INVOICEPDF")) {
        ReadMastercardInvoiceRawdata readFile = new ReadMastercardInvoiceRawdata();
        output = readFile.readINVOICEFilePDF(file, getConnection(), beanObj);
      } else if (beanObj.getFileName().equalsIgnoreCase("ATMMASTER")) {
        ReadATMMasterRawdata readFile = new ReadATMMasterRawdata();
        output = readFile.readATMMaster(file, getConnection(), beanObj);
      } else if (beanObj.getFileName().equalsIgnoreCase("T057")) {
        ReadMastercard461Rawdata readFile = new ReadMastercard461Rawdata();
        output = readFile.readT057File(file, getConnection(), beanObj);
      } else {
        ReadMastercard461Rawdata readFile = new ReadMastercard461Rawdata();
        output = readFile.read461File(file, getConnection(), beanObj);
      } 
    } catch (Exception e) {
      System.out.println("Exception in fileReading " + e);
    } 
    return output;
  }
  
  public HashMap<String, Object> validationRawFilesForProcessing(MastercardUploadBean beanObj) {
    HashMap<String, Object> output = new HashMap<>();
    int checkPrevDaySettProcess = 0;
    try {
      int checkSettProcess = ((Integer)getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM mastercard_settlement_all_report WHERE SETTLEMENT_DATE = TO_DATE(?,'DD/MM/YYYY') AND FILE_TYPE = ?", 
          new Object[] { beanObj.getFileDate(), beanObj.getFileType() }, Integer.class)).intValue();
      if (checkSettProcess == 0) {
        int checkOverAllSettProcess = ((Integer)getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM mastercard_settlement_all_report where FILE_TYPE = ?", 
            new Object[] { beanObj.getFileType() }, Integer.class)).intValue();
        checkOverAllSettProcess = 0;
        if (checkOverAllSettProcess > 0) {
          checkPrevDaySettProcess = ((Integer)getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM mastercard_settlement_all_report WHERE settlement_Date = TO_DATE(?,'DD/MM/YYYY')-1 and FILE_TYPE = ?", 
              new Object[] { beanObj.getFileDate(), beanObj.getFileType() }, Integer.class)).intValue();
        } else {
          checkPrevDaySettProcess = 1;
        } 
        if (checkPrevDaySettProcess > 0) {
          String getRawCount = "SELECT COUNT(*) FROM MAIN_FILE_UPLOAD_DTLS WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') AND FILEID IN (SELECT FILEID FROM MAIN_FILESOURCE WHERE FILENAME IN('ATM','POS') AND FILE_CATEGORY = 'MASTERCARD')";
          int rawUploadCount = ((Integer)getJdbcTemplate().queryForObject(getRawCount, new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
          if (rawUploadCount > 0) {
            getRawCount = "SELECT COUNT(*) FROM MAIN_FILE_UPLOAD_DTLS WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') AND FILEID IN (SELECT FILEID FROM MAIN_FILESOURCE WHERE FILENAME IN('ATM','POS') AND FILE_CATEGORY = 'MASTERCARD') AND COMAPRE_FLAG = 'Y'";
            rawUploadCount = ((Integer)getJdbcTemplate().queryForObject(getRawCount, new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
            if (rawUploadCount > 0) {
              output.put("result", Boolean.valueOf(true));
            } else {
              output.put("result", Boolean.valueOf(false));
              output.put("msg", "Recon is not processed for selected date!");
            } 
          } else {
            output.put("result", Boolean.valueOf(false));
            output.put("msg", "Raw Files are not uploaded!");
          } 
        } else {
          output.put("result", Boolean.valueOf(false));
          output.put("msg", "Previous day settlement is not processed");
        } 
      } else {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "Settlement is already processed!");
      } 
    } catch (Exception e) {
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occurred while validating!");
    } 
    return output;
  }
  
  public HashMap<String, Object> validationForProcessing(MastercardUploadBean beanObj) {
    HashMap<String, Object> output = new HashMap<>();
    int FileId140Count = 0;
    try {
      if (beanObj.getFileType().equalsIgnoreCase("DOMESTIC")) {
        FileId140Count = ((Integer)getJdbcTemplate().queryForObject("SELECT count(*) FROM  mastercard_settlement_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')", new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
      } else {
        FileId140Count = ((Integer)getJdbcTemplate().queryForObject("SELECT count(*) FROM  mastercard_int_settlement_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')", new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
      } 
      System.out.println("FileId140Count " + FileId140Count);
      if (FileId140Count > 0) {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "TTUM IS ALREADY PROCESSED!");
      } else {
        output.put("result", Boolean.valueOf(true));
      } 
    } catch (Exception e) {
      this.logger.info("Exception in validationForProcessing" + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception occured while validating 461 and 140 files");
    } 
    return output;
  }
  
  public HashMap<String, Object> validationForProcessing_OLD_01112023(MastercardUploadBean beanObj) {
    HashMap<String, Object> output = new HashMap<>();
    int FileId140Count = 0;
    try {
      int tadUploaded = ((Integer)getJdbcTemplate().queryForObject("select COUNT(*) from mastercard_tad_data where filedate = to_date(?,'DD/MON/YYYY') ", 
          new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
      if (tadUploaded > 0) {
        int tadFileIdCount = ((Integer)getJdbcTemplate().queryForObject("select count(file_id) from (select distinct file_id from mastercard_tad_data where filedate = to_date(?,'DD/MON/YYYY') AND FILE_ID != 'TOTAL')", 
            new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
        int recon_dates_count = ((Integer)getJdbcTemplate().queryForObject("select count(recon_date) from (select distinct recon_date from mastercard_tad_data where settlement_date = TO_DATE(?,'DD/MM/YYYY') AND FILE_TYPE = ?  and recon_date is not null  )", 
            new Object[] { beanObj.getFileDate(), beanObj.getFileType() }, Integer.class)).intValue();
        if (recon_dates_count == 2) {
          FileId140Count = ((Integer)getJdbcTemplate().queryForObject("select count(file_id) from (select DISTINCT FILE_ID from mastercard_tad_data where filedate = to_date(?,'DD/MON/YYYY') AND FILE_ID != 'TOTAL' and file_id in (select file_id from mastercard_140_rawdata where settlement_date =to_date(?,'DD/MM/YYYY')))", 
              new Object[] { beanObj.getFileDate(), beanObj.getFileDate() }, Integer.class)).intValue();
        } else {
          FileId140Count = ((Integer)getJdbcTemplate().queryForObject("select count(file_id) from (select DISTINCT FILE_ID from mastercard_tad_data where filedate = to_date(?,'DD/MON/YYYY') AND FILE_ID != 'TOTAL' and file_id in (select file_id from mastercard_140_rawdata where settlement_date between (select TO_CHAR(to_date(min(recon_date),'DD/MM/YY')+1,'DD/MON/YYYY') from mastercard_tad_data where settlement_date = TO_DATE(?,'DD/MM/YYYY') AND FILE_TYPE = ?)  AND (SELECT TO_CHAR(TO_DATE(MAX(RECON_DATE),'DD/MM/YY'),'DD/MON/YYYY') FROM MASTERCARD_TAD_DATA where settlement_date = TO_DATE(?,'DD/MM/YYYY') AND FILE_TYPE = ?)))", 
              
              new Object[] { beanObj.getFileDate(), beanObj.getFileDate(), beanObj.getFileType(), beanObj.getFileDate(), beanObj.getFileType() }, Integer.class)).intValue();
        } 
        if (FileId140Count != tadFileIdCount) {
          output.put("result", Boolean.valueOf(false));
          output.put("msg", "All 140 Files are not uploaded");
        } else if (recon_dates_count == 2) {
          int file461Data = ((Integer)getJdbcTemplate().queryForObject("SELECT count(*) FROM MASTERCARD_461_RAWDATA WHERE SETTLEMENT_DATE = ?", new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
          if (file461Data == 0) {
            output.put("result", Boolean.valueOf(false));
            output.put("msg", "461 File is not uploaded");
          } else {
            output.put("result", Boolean.valueOf(true));
          } 
        } else {
          int file461DateCount = ((Integer)getJdbcTemplate().queryForObject("SELECT count(settlement_date) from ( select distinct settlement_date FROM MASTERCARD_461_RAWDATA WHERE SETTLEMENT_DATE between (select TO_CHAR(to_date(min(recon_date),'DD/MM/YY')+1,'DD/MON/YYYY') from mastercard_tad_data where settlement_date = TO_DATE(?,'DD/MM/YYYY') AND FILE_TYPE = ?) AND (SELECT TO_CHAR(TO_DATE(MAX(RECON_DATE),'DD/MM/YY'),'DD/MON/YYYY') FROM MASTERCARD_TAD_DATA where settlement_date = TO_DATE(?,'DD/MM/YYYY') AND FILE_TYPE = ?) )", 
              
              new Object[] { beanObj.getFileDate(), beanObj.getFileType(), beanObj.getFileDate(), beanObj.getFileType() }, Integer.class)).intValue();
          if (file461DateCount != recon_dates_count) {
            output.put("result", Boolean.valueOf(false));
            output.put("msg", "All dates 461 File are not uploaded");
          } else {
            output.put("result", Boolean.valueOf(true));
          } 
        } 
      } else {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "TAD file is not uploaded for selected date.");
      } 
    } catch (Exception e) {
      this.logger.info("Exception in validationForProcessing" + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception occured while validating 461 and 140 files");
    } 
    return output;
  }
  
  public boolean mastercardSettlmentPro(MastercardUploadBean beanObj) {
    Map<String, Object> inParams = new HashMap<>();
    Map<String, Object> outParams = new HashMap<>();
    try {
      mastercardSettlmentPro rollBackexe = new mastercardSettlmentPro( getJdbcTemplate());
      inParams.put("v_filedate", beanObj.getFileDate());
      outParams = rollBackexe.execute(inParams);
      this.logger.info("OUT PARAM IS " + outParams.toString());
      if (outParams != null && outParams.get("msg") != null)
        return false; 
      return true;
    } catch (Exception e) {
      this.logger.info("Exception is " + e);
      return false;
    } 
  }

	private class mastercardSettlmentPro extends StoredProcedure {
		private static final String insert_proc = "MC_DOM_SETTLEMENT_TTUM";

		public mastercardSettlmentPro(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
  public boolean mastercardSettlmentProINT(MastercardUploadBean beanObj) {
    Map<String, Object> inParams = new HashMap<>();
    Map<String, Object> outParams = new HashMap<>();
    try {
      mastercardSettlmentProINT rollBackexe = new mastercardSettlmentProINT( getJdbcTemplate());
      inParams.put("v_filedate", beanObj.getFileDate());
      outParams = rollBackexe.execute(inParams);
      this.logger.info("OUT PARAM IS " + outParams.toString());
      if (outParams != null && outParams.get("msg") != null)
        return false; 
      return true;
    } catch (Exception e) {
      this.logger.info("Exception is " + e);
      return false;
    } 
  }

	private class mastercardSettlmentProINT extends StoredProcedure {
		private static final String insert_proc = "MC_INT_SETTLEMENT_TTUM";

		public mastercardSettlmentProINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
  
  public boolean processSettlement_OLD_01112023(MastercardUploadBean beanObj) {
	return false;}
  
  public HashMap<String, Object> validateForReportDownload(MastercardUploadBean beanObj) {
	return null;}
  
  public List<Object> getSettlementData(MastercardUploadBean beanObj) {
	return null;}
  
  public ArrayList<String> getColumnList(String tableName) {
	return null;}
  
  public HashMap<String, Object> validationForSettlementTTUMProcess(MastercardUploadBean beanObj) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      int checkSettAlreadyProc = ((Integer)getJdbcTemplate().queryForObject("select COUNT(*) from MASTERCARD_SETTLEMENT_TTUM where filedate = ? AND FILE_TYPE = ?", 
          new Object[] { beanObj.getFileDate(), beanObj.getFileType() }, Integer.class)).intValue();
      if (checkSettAlreadyProc == 0) {
        int checkSettProcess = ((Integer)getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM MASTERCARD_SETTLEMENT_REPORT WHERE settlement_Date = TO_DATE(?,'DD/MM/YYYY') and type = ?", 
            new Object[] { beanObj.getFileDate(), beanObj.getFileType() }, Integer.class)).intValue();
        if (checkSettProcess > 0) {
          output.put("result", Boolean.valueOf(true));
        } else {
          output.put("result", Boolean.valueOf(false));
          output.put("msg", "Settlement is not processed for selected date.");
        } 
      } else {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "TTUM is already processed for selected date");
      } 
    } catch (Exception e) {
      this.logger.info("Exception " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occurred while validation");
    } 
    return output;
  }
  
  public boolean processSettlementTTUM(MastercardUploadBean beanObj) {
    Map<String, Object> inParams = new HashMap<>();
    Map<String, Object> outParams = new HashMap<>();
    try {
      MastercardSettlementTTUMProc rollBackexe = new MastercardSettlementTTUMProc( getJdbcTemplate());
      inParams.put("SETT_DATE", beanObj.getFileDate());
      inParams.put("FILETYPE", beanObj.getFileType());
      inParams.put("USER_ID", beanObj.getCreatedBy());
      outParams = rollBackexe.execute(inParams);
      if (outParams != null && outParams.get("msg") != null)
        return false; 
    } catch (Exception e) {
      this.logger.info("Exception is " + e);
      return false;
    } 
    return true;
  }
  private class MastercardSettlementTTUMProc extends StoredProcedure{
		private static final String insert_proc = "MASTERCARD_TTUM_GENERATION";
		public MastercardSettlementTTUMProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("SETT_DATE", Types.VARCHAR));
			declareParameter(new SqlParameter("FILETYPE", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
  
  public List<Object> getSettlementTTUMData(MastercardUploadBean beanObj) {
	return null;}
  
  public void DeleteFiles(String stpath) {
    try {
      File directory = new File(stpath);
      if (directory.exists()) {
        FileUtils.forceDelete(directory);
        this.logger.debug(Boolean.valueOf(directory.exists()));
      } 
    } catch (Exception e) {
      System.out.println("Exception e" + e);
    } 
  }
  
  public void generateSettlementReport(MastercardUploadBean settlementBeanObj) {}
  
  public boolean checkNcreateFolder(MastercardUploadBean settlementBeanObj, List<String> stFileNames) {
    try {
      File directory = new File(settlementBeanObj.getStPath());
      if (!directory.exists())
        directory.mkdirs(); 
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
      Date date = sdf.parse(settlementBeanObj.getFileDate());
      sdf = new SimpleDateFormat("dd-MMM-yyyy");
      String stnewDate = sdf.format(date);
      String stnewPath = String.valueOf(settlementBeanObj.getStPath()) + File.separator + stnewDate;
      directory = new File(stnewPath);
      if (!directory.exists())
        directory.mkdirs(); 
      this.logger.info(directory.listFiles() + " size is " + (directory.listFiles()).length);
      stnewPath = String.valueOf(settlementBeanObj.getStPath()) + File.separator + stnewDate + File.separator + settlementBeanObj.getFileType();
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
}
