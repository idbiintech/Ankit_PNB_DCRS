package com.recon.service.impl;


import com.recon.model.NFSSettlementBean;
import com.recon.model.RupayUploadBean;
import com.recon.model.VisaUploadBean;
import com.recon.service.VisaSettlementService;
import com.recon.util.ReadVisa706EPFiles;
import com.recon.util.ReadVisa733EPFiles;
import com.recon.util.ReadVisa745EPFiles;
import com.recon.util.ReadVisaEPDomCCFiles;
import com.recon.util.ReadVisaEPFiles;
import com.recon.util.ReadVisaEPIntCCFiles;
import com.recon.util.ReadVisaJVFIle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.web.multipart.MultipartFile;

public class VisaSettlementServiceImpl extends JdbcDaoSupport implements VisaSettlementService {
  private static final String O_ERROR_MESSAGE = "o_error_message";
  
  public HashMap<String, Object> checkFileAlreadyUpload(String fileDate, String filename) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      String checkUpload = "";
      if (filename.contains("733F")) {
        checkUpload = "SELECT COUNT(*) as count FROM visa_ep733f_rawdata WHERE filedate = STR_to_date('" + fileDate + "','%Y/%m/%d')";
      } else if (filename.contains("745")) {
        checkUpload = "SELECT COUNT(*) as count FROM visa_ep745_rawdata WHERE filedate = STR_to_date('" + fileDate + "','%Y/%m/%d')";
      } else if (filename.contains("706")) {
          checkUpload = "SELECT COUNT(*) as count FROM visa_ep706_rawdata WHERE filedate = STR_to_date('" + fileDate + "','%Y/%m/%d')";
        } else {
        checkUpload = "select sum(count) from (\r\nSELECT COUNT(*) as count FROM visa_ep_vss120_rawdata WHERE filedate = STR_to_date('" + 
          fileDate + 
          "','%Y/%m/%d') and filename='" + filename + "' union all\r\n" + 
          "SELECT COUNT(*) as count FROM visa_ep_vss130_rawdata WHERE filedate =STR_to_date('" + fileDate + 
          "','%Y/%m/%d') and filename='" + filename + "' union all\r\n" + 
          "SELECT COUNT(*) as count FROM visa_ep_vss140_rawdata WHERE filedate =STR_to_date('" + fileDate + 
          "','%Y/%m/%d') and filename='" + filename + "' union all\r\n" + 
          "SELECT COUNT(*) as count FROM visa_ep_vss110_rawdata WHERE filedate= STR_to_date('" + fileDate + 
          "','%Y/%m/%d') and filename='" + filename + "' union all\r\n" + 
          "SELECT COUNT(*) as count FROM visa_ep_vss900_rawdata  WHERE filedate = STR_to_date('" + fileDate + 
          "','%Y/%m/%d') and filename='" + filename + "' union all\r\n" + 
          "SELECT COUNT(*) as count FROM visa_ep_vss210_rawdata WHERE filedate = STR_to_date('" + fileDate + 
          "','%Y/%m/%d') and filename='" + filename + "') a";
      } 
      System.out.println("checkUpload " + checkUpload);
      int checkUploadCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class)).intValue();
      if (checkUploadCount > 0) {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "File is already Uploaded !");
      } else {
        output.put("result", Boolean.valueOf(true));
      } 
      return output;
    } catch (Exception e) {
      this.logger.info("Exception in VisaSettlementServiceImpl : checkFileAlreadyUpload " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occurred while validating File Upload !");
      return output;
    } 
  }
  
  public Boolean visavalidateSettlementTTUM(RupayUploadBean beanObj) {
    try {
      String checkSettlementTTUM = "";
      System.out.println("inside the validatesettlement");
      if (beanObj.getSubcategory().toUpperCase().equalsIgnoreCase("DOMESTIC")) {
        checkSettlementTTUM = "Select count(*) from visa_settlement_ttum WHERE (FILEDATE = TO_CHAR(TO_DATE(?, 'DD/MON/YYYY'), 'DD-MM-YY') OR FILEDATE = TO_CHAR(TO_DATE(?, 'DD/MON/YYYY'), 'DD-MON-YY'))  ";
      } else {
        checkSettlementTTUM = "Select count(*) from visa_settlement_ttum_int WHERE (FILEDATE = TO_CHAR(TO_DATE(?, 'DD/MON/YYYY'), 'DD-MM-YY') OR FILEDATE = TO_CHAR(TO_DATE(?, 'DD/MON/YYYY'), 'DD-MON-YY'))  ";
      } 
      int getCountTTUM = ((Integer)getJdbcTemplate().queryForObject(checkSettlementTTUM, 
          new Object[] { beanObj.getFileDate(), beanObj.getFileDate() }, Integer.class)).intValue();
      if (getCountTTUM > 0)
        return Boolean.valueOf(true); 
      return Boolean.valueOf(false);
    } catch (Exception e) {
      this.logger.info("Exception in validateSettlementTTUM " + e);
      return Boolean.valueOf(false);
    } 
  }
  
  public List<Object> getSettlementDataVisa(RupayUploadBean beanObj) {
	  List<Object> data = new ArrayList();
	    final List<String> cols = new ArrayList<>();
	    cols.add("GL_ACCOUNT");
	    cols.add("INR");
	    cols.add("SIX_DIG_GL");
	    cols.add("D_C");
	    cols.add("NO_OF_TXNS");
	    cols.add("DEBIT");
	    cols.add("CREDIT");
	    cols.add("NARRATION");
	    cols.add("FILEDATE");
	    cols.add("PARTICULARS");
	    cols.add("REMARKS");
	    String getData = "";
	    if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
	      if (beanObj.getTTUM_TYPE().equalsIgnoreCase("EXCEL_ACQ")) {
	        getData = "SELECT GL_ACCOUNT, INR, SIX_DIG_GL, D_C, NO_OF_TXNS, DEBIT, CREDIT, NARRATION, FILEDATE, PARTICULARS, REMARKS \r\nFROM visa_dom_acq_settlement_report WHERE FILEDATE = STR_TO_DATE(? ,'%Y/%m/%d')";
	      } else {
	        getData = "SELECT GL_ACCOUNT, INR, SIX_DIG_GL, D_C, NO_OF_TXNS, DEBIT, CREDIT, NARRATION, FILEDATE, PARTICULARS, REMARKS \r\nFROM visa_dom_iss_settlement_report WHERE FILEDATE = STR_TO_DATE(? ,'%Y/%m/%d')";
	      } 
	    } else if (beanObj.getTTUM_TYPE().equalsIgnoreCase("EXCEL_ACQ")) {
	      getData = "SELECT GL_ACCOUNT, INR, SIX_DIG_GL, D_C, NO_OF_TXNS, DEBIT, CREDIT, NARRATION, FILEDATE, PARTICULARS, REMARKS \r\nFROM visa_int_acq_settlement_report WHERE FILEDATE = STR_TO_DATE(? ,'%Y/%m/%d')";
	    } else {
	      getData = "SELECT GL_ACCOUNT, INR, SIX_DIG_GL, D_C, NO_OF_TXNS, DEBIT, CREDIT, NARRATION, FILEDATE, PARTICULARS, REMARKS \r\nFROM visa_int_iss_settlement_report WHERE FILEDATE = STR_TO_DATE(? ,'%Y/%m/%d')";
	    } 
	    List<Object> settlementData = getJdbcTemplate().query(getData,
				new Object[] { beanObj.getFileDate() },
				new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							Map<String, String> table_Data = new HashMap<String, String>();
							for (String column : cols) {
								table_Data.put(column, rs.getString(column));
							}
							beanList.add(table_Data);
						}
						return beanList;
					}
				});

		data.add(cols);
		data.add(settlementData);
	    return data;
	    }
  
  public HashMap<String, Object> checkCcFileAlreadyUpload(String fileDate, String subcate) {
    HashMap<String, Object> output = new HashMap<>();
    String checkUpload = "";
    try {
      if (subcate.equals("DOMESTIC")) {
        checkUpload = "SELECT COUNT(*) FROM VISA_DOM_EP_RAWDATA  WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = ?";
      } else if (subcate.equals("INTERNATIONAL")) {
        checkUpload = "SELECT COUNT(*) FROM VISA_INT_EP_RAWDATA  WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = ?";
      } 
      int checkUploadCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[] { fileDate, subcate }, Integer.class)).intValue();
      if (checkUploadCount > 0) {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "File is already Uploaded !");
      } else {
        output.put("result", Boolean.valueOf(true));
      } 
      return output;
    } catch (Exception e) {
      this.logger.info("Exception in VisaSettlementServiceImpl : checkFileAlreadyUpload " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occurred while validating File Upload !");
      return output;
    } 
  }
  
  public boolean uploadFile(VisaUploadBean beanObj, MultipartFile file) {
    try {
      if (file.getOriginalFilename().contains("733F")) {
        ReadVisa733EPFiles readVisa733EPFiles = new ReadVisa733EPFiles();
        Connection connection = getConnection();
        readVisa733EPFiles.fileupload(beanObj, file, connection);
        return true;
      } 
      if (file.getOriginalFilename().contains("745")) {
        ReadVisa745EPFiles readVisa745EPFiles = new ReadVisa745EPFiles();
        Connection connection = getConnection();
        readVisa745EPFiles.fileupload(beanObj, file, connection);
        return true;
      } 
      if (file.getOriginalFilename().contains("706")) {
        ReadVisa706EPFiles readVisa706EPFiles = new ReadVisa706EPFiles();
        Connection connection = getConnection();
        readVisa706EPFiles.fileupload(beanObj, file, connection);
        return true;
      } 
      ReadVisaEPFiles readObj = new ReadVisaEPFiles();
      Connection conn = getConnection();
      readObj.fileupload(beanObj, file, conn);
      return true;
    } catch (Exception e) {
      this.logger.info("Exception in VisaSettlementServiceImpl : uploadFile " + e);
      return false;
    } 
  }
  
  public boolean uploadCCFile(VisaUploadBean beanObj, MultipartFile file) {
    try {
      if (beanObj.getFileType().equals("DOMESTIC")) {
        ReadVisaEPDomCCFiles readObj = new ReadVisaEPDomCCFiles();
        Connection conn = getConnection();
        readObj.fileupload(beanObj, file, conn);
        return true;
      } 
      if (beanObj.getFileType().equals("INTERNATIONAL")) {
        ReadVisaEPIntCCFiles readObj = new ReadVisaEPIntCCFiles();
        Connection conn = getConnection();
        readObj.fileupload(beanObj, file, conn);
        return true;
      } 
    } catch (Exception e) {
      this.logger.info("Exception in VisaSettlementServiceImpl : uploadFile " + e);
      return false;
    } 
    return false;
  }
  
  public boolean checkFileUpload(VisaUploadBean beanObj) {
    try {
      String checkFileUpload = "SELECT COUNT(*) FROM VISA_EP_RAWDATA WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') AND SUBCATEGORY = 'DOMESTIC'";
      int domeUploadCount = ((Integer)getJdbcTemplate().queryForObject(checkFileUpload, 
          new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
      checkFileUpload = "SELECT COUNT(*) FROM VISA_EP_RAWDATA WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') AND SUBCATEGORY = 'INTERNATIONAL'";
      int interUploadCount = ((Integer)getJdbcTemplate().queryForObject(checkFileUpload, 
          new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
      if (domeUploadCount > 0 && interUploadCount > 0)
        return true; 
      return false;
    } catch (Exception e) {
      this.logger.info("Exception in check File Upload " + e);
      return false;
    } 
  }
  
  public HashMap<String, Object> checkSettlementProcess(VisaUploadBean beanObj) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      String checkSettlementProcess = "SELECT COUNT(*)  FROM VISA_SETTLEMENT_REPORT_temp WHERE filedate = to_date(?,'DD/MM/YYYY')";
      int settlementCount = ((Integer)getJdbcTemplate().queryForObject(checkSettlementProcess, 
          new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
      if (settlementCount == 0) {
        output.put("result", Boolean.valueOf(true));
      } else {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "Settlement is already processed for selected date");
      } 
    } catch (Exception e) {
      this.logger.info("Exception while checking settlement Process" + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception while validating settlement process");
    } 
    return output;
  }
  
  public boolean runVisaSettlement(VisaUploadBean beanObj) {
	return false;}
  
  public List<Object> getSettlementData(VisaUploadBean beanObj) {
	return null;}
  
  public ArrayList<String> getColumnList(String tableName) {
	return null;
}
  
  public HashMap<String, Object> checkJVUploaded(VisaUploadBean beanObj) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      String checkFileUploaded = "SELECT COUNT(*) FROM VISA_SETTLEMENT_REPORT WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY')";
      int getUploadCount = ((Integer)getJdbcTemplate().queryForObject(checkFileUploaded, 
          new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
      if (getUploadCount == 0) {
        output.put("result", Boolean.valueOf(true));
      } else {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "JV File is already uploaded");
      } 
    } catch (Exception e) {
      this.logger.info("Exception in checkJVUploaded " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Excception in checkJVUploaded");
    } 
    return output;
  }
  
  public boolean readJVFile(VisaUploadBean beanObj, MultipartFile file) {
    ReadVisaJVFIle readJV = new ReadVisaJVFIle();
    return readJV.readVisaJVFile(beanObj, file, getConnection());
  }
  
  public HashMap<String, Object> CheckTTUMProcessed(VisaUploadBean beanObj) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      String checkFileUploaded = "SELECT COUNT(*) FROM VISA_SETTLEMENT_TTUM WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY')";
      int getUploadCount = ((Integer)getJdbcTemplate().queryForObject(checkFileUploaded, 
          new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
      if (getUploadCount == 0) {
        output.put("result", Boolean.valueOf(true));
      } else {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "TTUM is already Processed");
      } 
    } catch (Exception e) {
      this.logger.info("Exception in CheckTTUMProcessed " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception in CheckTTUMProcessed");
    } 
    return output;
  }
  
  public boolean runVisaSettlementTTUM(VisaUploadBean beanObj) {
	return false;}
  
  public List<Object> getSettlementTTUMData(VisaUploadBean beanObj) {
	return null;}
  
  public ArrayList<String> getColumnTTUMList(String tableName) {
	return null;}
  
  public HashMap<String, Object> checkAdjustmentFileAlreadyUpload(String fileDate, String subcate) {
    HashMap<String, Object> output = new HashMap<>();
    try {
      String checkUpload = "SELECT COUNT(*) FROM visa_adjustment  WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY')";
      int checkUploadCount = ((Integer)getJdbcTemplate().queryForObject(checkUpload, new Object[] { fileDate }, Integer.class)).intValue();
      if (checkUploadCount > 0) {
        output.put("result", Boolean.valueOf(false));
        output.put("msg", "File is already Uploaded !");
      } else {
        output.put("result", Boolean.valueOf(true));
      } 
      return output;
    } catch (Exception e) {
      this.logger.info("Exception in VisaSettlementServiceImpl : checkFileAlreadyUpload " + e);
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "Exception Occurred while validating File Upload !");
      return output;
    } 
  }
  
  public boolean uploadAwaitingFile(VisaUploadBean visaUploadBean, MultipartFile file) {
    int lineNumber = 0, readingLine = 0, batchNumber = 0, startPos = 0, batchSize = 0;
    boolean batchExecuted = false;
    Connection conn = getConnection();
    String insertQuery = "INSERT INTO visa_adjustment (ROL_Case, FU_DATE, CASE_STATUS, DC, DAYS_TO_ACT, AMOUNT, ACCOUNT_NUMBER, TOKEN, MEBMER_CASE, ARN, USER_TYPE, LAST_ACTION, FRAUD_CLS, MCC_CODE, MOTO_IND, MERCHANT_NAME, NETWORK_ID, JR, IND, FILEDATE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    try {
      PreparedStatement ps = conn.prepareStatement(insertQuery);
      conn.setAutoCommit(false);
      Workbook wb = null;
      int sr_no = 1;
      Sheet sheet = null;
      FormulaEvaluator formulaEvaluator = null;
      int extn = file.getOriginalFilename().indexOf(".");
      XSSFWorkbook xSSFWorkbook = new XSSFWorkbook(file.getInputStream());
      sheet = xSSFWorkbook.getSheetAt(0);
      int lastRow = sheet.getLastRowNum();
      lineNumber = lastRow;
      for (int rowNumber = 0; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
        if (rowNumber != 0) {
          Row currentRow = sheet.getRow(rowNumber);
          int cellIdx = 0;
          sr_no = 1;
          DataFormatter dataFormatter = new DataFormatter();
          for (cellIdx = 0; cellIdx < currentRow.getLastCellNum(); cellIdx++) {
            try {
              Cell currentCell = currentRow.getCell(cellIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
              if (currentRow.getCell(cellIdx).getCellType() == 1 || 
                currentRow.getCell(cellIdx).getCellType() == 0 || 
                currentRow.getCell(cellIdx).getCellType() == 1 || 
                currentRow.getCell(cellIdx).getCellType() == 3)
                switch (cellIdx) {
                  case 0:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 1:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 2:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 3:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 4:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 5:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 6:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 7:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 8:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 9:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 10:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 11:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 12:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 13:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 14:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 15:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 16:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 17:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                  case 18:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      ps.setString(sr_no++, visaUploadBean.getFileDate());
                      ps.addBatch();
                      batchSize++;
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    ps.setString(sr_no++, visaUploadBean.getFileDate());
                    ps.addBatch();
                    batchSize++;
                    break;
                  case 19:
                    if (currentCell.getCellType() == 0) {
                      int value = Integer.valueOf((int)currentCell.getNumericCellValue()).intValue();
                      System.out.println(value);
                      ps.setString(sr_no++, String.valueOf(value));
                      break;
                    } 
                    System.out.println(currentCell.getStringCellValue());
                    ps.setString(sr_no++, currentCell.getStringCellValue());
                    break;
                }  
            } catch (Exception e) {
              System.out.println(e);
            } 
          } 
          if (batchSize == 50) {
            batchNumber++;
            System.out.println("Batch executed is " + batchNumber);
            ps.executeBatch();
            batchSize = 0;
            batchExecuted = true;
          } 
        } 
      } 
      if (!batchExecuted) {
        batchNumber++;
        ps.executeBatch();
        System.out.println("Batch executed is " + batchNumber);
      } 
      conn.commit();
      return true;
    } catch (Exception e) {
      System.out.println(e);
      return false;
    } 
  }
  
  public HashMap<String, Object> ValidateForAdjTTUM(NFSSettlementBean beanObj) {
    HashMap<String, Object> result = new HashMap<>();
    int rawCount = 0;
    List<String> subcategories = new ArrayList<>();
    int uploadCount = 0;
    int file_id = 0;
    int checkAdjCount = 0;
    int reconVISACount = 0;
    String CASE_STATUS = "";
    if (beanObj.getAdjType().equalsIgnoreCase("Chargeback Acceptance")) {
      CASE_STATUS = "Consumer Dispute - Accepted Full by Acq";
    } else if (beanObj.getAdjType().equalsIgnoreCase("Re-Presentment Raise")) {
      CASE_STATUS = "Consumer Dispute - Declined by Acq";
    } 
    try {
      String checkAdjFileUploaded = "SELECT COUNT(*) FROM visa_adjustment WHERE FILEDATE = ? ";
      uploadCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjFileUploaded, 
          new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
      System.out.println("Already Process count " + uploadCount);
      if (uploadCount > 0) {
        String checkAdjquery = "SELECT COUNT(*) FROM visa_adjustment WHERE FILEDATE = ? and CASE_STATUS = ?";
        checkAdjCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjquery, 
            new Object[] { beanObj.getDatepicker(), CASE_STATUS }, Integer.class)).intValue();
        System.out.println("Already Process count " + checkAdjCount);
        if (checkAdjCount > 0) {
          String reconVISAQuery = "SELECT COUNT(*) FROM VISA_ADJUSTMENT_TTUM_TEMPA WHERE FILEDATE = ? and adjtype = ?";
          reconVISACount = ((Integer)getJdbcTemplate().queryForObject(reconVISAQuery, 
              new Object[] { beanObj.getDatepicker(), CASE_STATUS }, Integer.class)).intValue();
          System.out.println("Already Process count " + checkAdjCount);
          if (reconVISACount == 0) {
            result.put("result", Boolean.valueOf(true));
          } else {
            result.put("result", Boolean.valueOf(false));
            result.put("msg", "Adjustment already processed.");
          } 
        } else {
          result.put("result", Boolean.valueOf(false));
          result.put("msg", "Adjustment Type is not present for Selected Date!");
        } 
      } else {
        result.put("result", Boolean.valueOf(false));
        result.put("msg", "Adjustment File is not uploaded for Selected Date!");
      } 
    } catch (Exception e) {
      System.out.println("Exception in ValidateDailySettProcess " + e);
      result.put("result", Boolean.valueOf(false));
      result.put("msg", "Exception Occurred");
    } 
    return result;
  }
  
  public boolean checkAdjTTUMProcess(NFSSettlementBean beanObj) {
    int uploadCount = 0;
    int file_id = 0;
    int checkAdjCount = 0;
    int reconVISACount = 0;
    String CASE_STATUS = "";
    if (beanObj.getAdjType().equalsIgnoreCase("Chargeback Acceptance")) {
      CASE_STATUS = "Consumer Dispute - Accepted Full by Acq";
    } else if (beanObj.getAdjType().equalsIgnoreCase("Re-Presentment Raise")) {
      CASE_STATUS = "Consumer Dispute - Declined by Acq";
    } 
    try {
      int procCount = 0;
      String checkAdjquery = "SELECT COUNT(*) FROM VISA_ADJUSTMENT_TTUM_TEMPA WHERE FILEDATE = ? and adjtype = ?";
      procCount = ((Integer)getJdbcTemplate().queryForObject(checkAdjquery, 
          new Object[] { beanObj.getDatepicker(), CASE_STATUS }, Integer.class)).intValue();
      System.out.println("Already Process count " + procCount);
      this.logger.info("Already Process count " + procCount);
      if (procCount > 0)
        return true; 
      return false;
    } catch (Exception e) {
      this.logger.info("Exception occurred in checkSettVoucherProcess " + e);
      return false;
    } 
  }
}
