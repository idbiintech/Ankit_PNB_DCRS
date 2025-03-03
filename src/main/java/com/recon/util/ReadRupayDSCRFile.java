package com.recon.util;


import com.recon.model.RupayUploadBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.web.multipart.MultipartFile;

public class ReadRupayDSCRFile extends JdbcDaoSupport {
  private static final Logger logger = Logger.getLogger(com.recon.util.ReadRupayDSCRFile.class);
  
  SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");
  
  SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
  
  public HashMap<String, Object> fileupload(RupayUploadBean beanObj, MultipartFile file, Connection con) throws SQLException {
    logger.info("reached inside fileupload2 method");
    HashMap<String, Object> mapObj = new HashMap<>();
    String settlement_date = "", bankname = "", settlement_bin = "", iss_bin = "", inward = "", status = "", txncycle = "", txntype = "", channel = "", txn_count = "", txn_ccy = "", txn_amt_cr = "", txn_amt_dr = "", set_ccy = "", set_amt_dr = "", set_amt_cr = "", int_fee_amt_dr = "", int_fee_amt_cr = "", mem_inc_fee_amt_dr = "", mem_inc_fee_amt_cr = "", cus_com_dr = "", cus_com_cr = "", oth_fee_amt_dr = "", oth_fee_amt_cr = "", oth_fee_gst_dr = "", oth_fee_gst_cr = "", final_sum_cr = "", final_sum_dr = "", final_net = "";
    String query = "insert into rupay_dscr_rawdata(BANK_NAME, SETT_BIN, ISS_BIN, INWARD_OUTWARD, STATUS, TXNCYCLE, TXNTYPE, CHANNEL, TXN_COUNT, TXN_CCY, TXN_AMT_DR, TXN_AMT_CR, SET_CCY, SET_AMT_DR, SET_AMT_CR, INT_FEE_DR, INT_FEE_CR, MEM_INC_FEE_DR, MEM_INC_FEE_CR, CUS_COMPEN_DR, CUS_COMPEN_CR, OTH_FEE_AMT_DR, OTH_FEE_AMT_CR, OTH_FEE_GST_DR, OTH_FEE_GST_CR, FINAL_SUM_CR, FINAL_SUM_DR, FINAL_NET, FILEDATE, CREATEDBY, CYCLE, FILENAME, SETTLEMENT_DATE) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, str_to_date(?,'%Y/%m/%d'), ?, ?, ?, ?)";
    int cellIdx = 0;
    boolean totalFlag = false;
    try {
      XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
      XSSFSheet sheet = wb.getSheetAt(0);
      XSSFFormulaEvaluator xSSFFormulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
      PreparedStatement ps = con.prepareStatement(query);
      for (int rowNumber = 0; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
        if (rowNumber != 0) {
          XSSFRow xSSFRow = sheet.getRow(rowNumber);
          cellIdx = 0;
          if (xSSFRow != null)
            for (cellIdx = 0; cellIdx < xSSFRow.getLastCellNum(); cellIdx++) {
              try {
                Cell currentCell = xSSFRow.getCell(cellIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (xSSFRow.getCell(cellIdx).getCellType() == 1 || xSSFRow.getCell(cellIdx).getCellType() == 0 || xSSFRow.getCell(cellIdx).getCellType() == 3)
                  switch (cellIdx) {
                    case 0:
                      if (currentCell.getStringCellValue().equals("") || currentCell.getStringCellValue().isEmpty())
                        break; 
                      settlement_date = currentCell.getStringCellValue();
                      break;
                    case 1:
                      if (currentCell.getStringCellValue().equals("") || currentCell.getStringCellValue().isEmpty());
                      break;
                    case 2:
                      if (currentCell.getStringCellValue().equals("") || currentCell.getStringCellValue().isEmpty())
                        break; 
                      bankname = currentCell.getStringCellValue();
                      break;
                    case 3:
                      if (currentCell.getStringCellValue().equals("") || currentCell.getStringCellValue().isEmpty())
                        break; 
                      settlement_bin = currentCell.getStringCellValue();
                      break;
                    case 4:
                      try {
                        if (currentCell.getNumericCellValue() == 0.0D)
                          break; 
                      } catch (IllegalStateException e) {
                        iss_bin = currentCell.getStringCellValue();
                        break;
                      } 
                      if (currentCell.getCellType() == 0) {
                        iss_bin = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                        break;
                      } 
                      iss_bin = currentCell.getStringCellValue();
                      break;
                    case 5:
                      if (currentCell.getStringCellValue().equals("Total")) {
                        inward = currentCell.getStringCellValue();
                        totalFlag = true;
                        break;
                      } 
                      if (currentCell.getStringCellValue().equalsIgnoreCase("INWARD") || currentCell.getStringCellValue().equalsIgnoreCase("OUTWARD") || currentCell.getStringCellValue().equalsIgnoreCase("INWARD GST")) {
                        inward = currentCell.getStringCellValue();
                        totalFlag = true;
                      } 
                      break;
                    case 6:
                      if (currentCell.getStringCellValue().isEmpty() || currentCell.getStringCellValue().equals(""))
                        break; 
                      status = currentCell.getStringCellValue();
                      break;
                    case 7:
                      if (currentCell.getStringCellValue().isEmpty() || currentCell.getStringCellValue().equals(""))
                        break; 
                      txncycle = currentCell.getStringCellValue();
                      break;
                    case 8:
                      if (currentCell.getStringCellValue().isEmpty() || currentCell.getStringCellValue().equals(""))
                        break; 
                      txntype = currentCell.getStringCellValue();
                      break;
                    case 9:
                      if (currentCell.getStringCellValue().isEmpty() || currentCell.getStringCellValue().equals(""))
                        break; 
                      channel = currentCell.getStringCellValue();
                      break;
                    case 10:
                      if (currentCell.getCellType() == 1) {
                        txn_count = currentCell.getStringCellValue();
                        break;
                      } 
                      txn_count = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 11:
                      if (currentCell.getStringCellValue().isEmpty() || currentCell.getStringCellValue().equals(""))
                        break; 
                      if (currentCell.getCellType() == 1) {
                        txn_ccy = currentCell.getStringCellValue();
                        break;
                      } 
                      txn_ccy = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 12:
                      if (currentCell.getCellType() == 1) {
                        txn_amt_dr = currentCell.getStringCellValue();
                        break;
                      } 
                      txn_amt_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 13:
                      if (currentCell.getCellType() == 1) {
                        txn_amt_cr = currentCell.getStringCellValue();
                        break;
                      } 
                      txn_amt_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 14:
                      if (currentCell.getCellType() == 1) {
                        set_ccy = currentCell.getStringCellValue();
                        break;
                      } 
                      set_ccy = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 15:
                      if (currentCell.getCellType() == 1) {
                        set_amt_dr = currentCell.getStringCellValue();
                        break;
                      } 
                      set_amt_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 16:
                      if (currentCell.getCellType() == 1) {
                        set_amt_cr = currentCell.getStringCellValue();
                        break;
                      } 
                      set_amt_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 17:
                      if (currentCell.getCellType() == 1) {
                        int_fee_amt_dr = currentCell.getStringCellValue();
                        break;
                      } 
                      int_fee_amt_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 18:
                      if (currentCell.getCellType() == 1) {
                        int_fee_amt_cr = currentCell.getStringCellValue();
                        break;
                      } 
                      int_fee_amt_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 19:
                      if (currentCell.getCellType() == 1) {
                        mem_inc_fee_amt_dr = currentCell.getStringCellValue();
                        break;
                      } 
                      mem_inc_fee_amt_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 20:
                      if (currentCell.getCellType() == 1) {
                        mem_inc_fee_amt_cr = currentCell.getStringCellValue();
                        break;
                      } 
                      mem_inc_fee_amt_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 21:
                      if (currentCell.getCellType() == 1) {
                        cus_com_dr = currentCell.getStringCellValue();
                        break;
                      } 
                      cus_com_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 22:
                      if (currentCell.getCellType() == 1) {
                        cus_com_cr = currentCell.getStringCellValue();
                        break;
                      } 
                      cus_com_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 23:
                      if (currentCell.getCellType() == 1) {
                        oth_fee_amt_dr = currentCell.getStringCellValue();
                        break;
                      } 
                      oth_fee_amt_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 24:
                      if (currentCell.getCellType() == 1) {
                        oth_fee_amt_cr = currentCell.getStringCellValue();
                        break;
                      } 
                      oth_fee_amt_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 25:
                      if (currentCell.getCellType() == 1) {
                        oth_fee_gst_dr = currentCell.getStringCellValue();
                        break;
                      } 
                      oth_fee_gst_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 26:
                      if (currentCell.getCellType() == 1) {
                        oth_fee_gst_cr = currentCell.getStringCellValue();
                        break;
                      } 
                      oth_fee_gst_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 27:
                      if (currentCell.getCellType() == 1) {
                        final_sum_cr = currentCell.getStringCellValue();
                        break;
                      } 
                      final_sum_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 28:
                      if (currentCell.getCellType() == 1) {
                        final_sum_dr = currentCell.getStringCellValue();
                        break;
                      } 
                      final_sum_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 29:
                      if (currentCell.getCellType() == 1) {
                        final_net = currentCell.getStringCellValue();
                        ps.setString(1, bankname);
                        ps.setString(2, settlement_bin);
                        ps.setString(3, iss_bin);
                        ps.setString(4, inward);
                        ps.setString(5, status);
                        ps.setString(6, txncycle);
                        ps.setString(7, txntype);
                        ps.setString(8, channel);
                        ps.setString(9, txn_count);
                        ps.setString(10, txn_ccy);
                        ps.setString(11, txn_amt_dr);
                        ps.setString(12, txn_amt_cr);
                        ps.setString(13, set_ccy);
                        ps.setString(14, set_amt_dr);
                        ps.setString(15, set_amt_cr);
                        ps.setString(16, int_fee_amt_dr);
                        ps.setString(17, int_fee_amt_cr);
                        ps.setString(18, mem_inc_fee_amt_dr);
                        ps.setString(19, mem_inc_fee_amt_cr);
                        ps.setString(20, cus_com_dr);
                        ps.setString(21, cus_com_cr);
                        ps.setString(22, oth_fee_amt_dr);
                        ps.setString(23, oth_fee_amt_cr);
                        ps.setString(24, oth_fee_gst_dr);
                        ps.setString(25, oth_fee_gst_cr);
                        ps.setString(26, final_sum_cr);
                        ps.setString(27, final_sum_dr);
                        ps.setString(28, final_net);
                        ps.setString(29, beanObj.getFileDate());
                        ps.setString(30, beanObj.getCreatedBy());
                        ps.setString(31, beanObj.getCycle());
                        ps.setString(32, file.getOriginalFilename());
                        ps.setString(33, settlement_date);
                        ps.addBatch();
                        inward = "";
                        status = "";
                        txncycle = "";
                        txntype = "";
                        channel = "";
                        break;
                      } 
                      final_net = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      ps.setString(1, bankname);
                      ps.setString(2, settlement_bin);
                      ps.setString(3, iss_bin);
                      ps.setString(4, inward);
                      ps.setString(5, status);
                      ps.setString(6, txncycle);
                      ps.setString(7, txntype);
                      ps.setString(8, channel);
                      ps.setString(9, txn_count);
                      ps.setString(10, txn_ccy);
                      ps.setString(11, txn_amt_dr);
                      ps.setString(12, txn_amt_cr);
                      ps.setString(13, set_ccy);
                      ps.setString(14, set_amt_dr);
                      ps.setString(15, set_amt_cr);
                      ps.setString(16, int_fee_amt_dr);
                      ps.setString(17, int_fee_amt_cr);
                      ps.setString(18, mem_inc_fee_amt_dr);
                      ps.setString(19, mem_inc_fee_amt_cr);
                      ps.setString(20, cus_com_dr);
                      ps.setString(21, cus_com_cr);
                      ps.setString(22, oth_fee_amt_dr);
                      ps.setString(23, oth_fee_amt_cr);
                      ps.setString(24, oth_fee_gst_dr);
                      ps.setString(25, oth_fee_gst_cr);
                      ps.setString(26, final_sum_cr);
                      ps.setString(27, final_sum_dr);
                      ps.setString(28, final_net);
                      ps.setString(29, beanObj.getFileDate());
                      ps.setString(30, beanObj.getCreatedBy());
                      ps.setString(31, beanObj.getCycle());
                      ps.setString(32, file.getOriginalFilename());
                      ps.setString(33, settlement_date);
                      ps.addBatch();
                      inward = "";
                      status = "";
                      txncycle = "";
                      txntype = "";
                      channel = "";
                      break;
                  }  
              } catch (Exception e) {
                logger.error(e);
                e.printStackTrace();
              } 
            }  
        } 
      } 
      ps.executeBatch();
      ps.close();
      mapObj.put("result", Boolean.valueOf(true));
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e);
      mapObj.put("result", Boolean.valueOf(false));
    } 
    return mapObj;
  }
  
  public HashMap<String, Object> PBGBfileupload(RupayUploadBean beanObj, MultipartFile file, Connection con) throws SQLException {
    String bank_name = null, sett_bin = null, acq_iss_bin = null, in_out = null, status = null, tran_cycle = null;
    int totalcount = 0;
    HashMap<String, Object> mapObj = new HashMap<>();
    Workbook wb = null;
    Sheet sheet = null;
    FormulaEvaluator formulaEvaluate = null;
    int extn = file.getOriginalFilename().indexOf(".");
    logger.info("extension is " + extn);
    boolean Idbi_Block = false;
    int read_line = 0;
    boolean reading_line = false;
    boolean total_encounter = false, stop_reading = false, last_line = false;
    int final_line = 0;
    String sql = "INSERT INTO RUPAY_PBGB_DSCR_RAWDATA(BANK_NAME,SETT_BIN, ISS_BIN, INWARD_OUTWARD, TXN_COUNT, TXN_CCY, TXN_AMT_DR, TXN_AMT_CR, SETT_CURR, SET_AMT_DR, SET_AMT_CR, INT_FEE_DR, INT_FEE_CR, MEM_INC_FEE_DR, MEM_INC_FEE_CR, CUS_COMPEN_DR, CUS_COMPEN_CR, OTH_FEE_AMT_DR, OTH_FEE_AMT_CR, OTH_FEE_GST_DR, OTH_FEE_GST_CR, FINAL_SUM_CR, FINAL_SUM_DR, FINAL_NET, FILEDATE, CREATEDBY, CYCLE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,TO_DATE(?,'DD/MON/YYYY'),?,?)";
    try {
      XSSFFormulaEvaluator xSSFFormulaEvaluator = null;
      long start = System.currentTimeMillis();
      System.out.println("start");
      con.setAutoCommit(false);
      PreparedStatement ps = con.prepareStatement(sql);
      if (file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLS")) {
        XSSFWorkbook xSSFWorkbook = new XSSFWorkbook(file.getInputStream());
        sheet = xSSFWorkbook.getSheetAt(0);
        xSSFFormulaEvaluator = new XSSFFormulaEvaluator(xSSFWorkbook);
      } else if (file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLSX")) {
        XSSFWorkbook xSSFWorkbook = new XSSFWorkbook(file.getInputStream());
        sheet = xSSFWorkbook.getSheetAt(0);
        xSSFFormulaEvaluator = new XSSFFormulaEvaluator(xSSFWorkbook);
      } 
      label98: for (Row r : sheet) {
        totalcount++;
        if (r.getRowNum() > 0) {
          if (read_line == 1)
            read_line++; 
          reading_line = false;
          int cellCount = 1;
          if (!stop_reading) {
            for (Cell c : r) {
              switch (xSSFFormulaEvaluator.evaluateInCell(c).getCellType()) {
                case 1:
                  if (c.getStringCellValue().equalsIgnoreCase("THE NAINITAL BANK LTD AS ISS")) {
                    bank_name = c.getStringCellValue();
                    Idbi_Block = true;
                    read_line++;
                    logger.info("cell count is " + cellCount + " value " + c.getStringCellValue());
                    cellCount++;
                    continue;
                  } 
                  if (total_encounter && final_line > 0) {
                    logger.info("in 1st Total Encounter loop");
                    if (final_line == 4 && cellCount == 1) {
                      ps.setString(cellCount++, bank_name);
                      ps.setString(cellCount++, c.getStringCellValue());
                      stop_reading = true;
                      last_line = true;
                      continue;
                    } 
                    if (final_line == 4 && cellCount > 1) {
                      ps.setString(cellCount++, c.getStringCellValue());
                      continue;
                    } 
                    final_line++;
                    continue label98;
                  } 
                  if (Idbi_Block) {
                    if (read_line == 1) {
                      if (cellCount == 2) {
                        sett_bin = c.getStringCellValue();
                        logger.info("cell count is " + cellCount + " value " + c.getStringCellValue());
                        cellCount++;
                        continue;
                      } 
                      continue label98;
                    } 
                    if (c.getStringCellValue().equalsIgnoreCase("Total")) {
                      if (cellCount == 1) {
                        reading_line = true;
                        if (total_encounter) {
                          final_line++;
                          continue label98;
                        } 
                        ps.setString(cellCount++, bank_name);
                        ps.setString(cellCount++, sett_bin);
                        ps.setString(cellCount++, acq_iss_bin);
                        ps.setString(cellCount, c.getStringCellValue());
                        logger.info("cell count is " + cellCount + " value " + 
                            c.getStringCellValue());
                        cellCount++;
                        total_encounter = true;
                      } 
                      continue;
                    } 
                    if (total_encounter) {
                      logger.info("in Total Encounter loop");
                      logger.info("cell count is " + cellCount + " value " + c.getStringCellValue());
                      ps.setString(cellCount++, c.getStringCellValue());
                      continue;
                    } 
                    continue label98;
                  } 
                  if (reading_line) {
                    ps.setString(cellCount, c.getStringCellValue());
                    logger.info("cell count is " + cellCount + " value " + c.getStringCellValue());
                    cellCount++;
                    continue;
                  } 
                  if (cellCount >= 3)
                    continue label98; 
                case 0:
                  if (Idbi_Block) {
                    String digit = (new StringBuilder(String.valueOf(c.getNumericCellValue()))).toString();
                    if (digit.contains("E")) {
						digit = c.getNumericCellValue() + "";
						double d = Double.parseDouble(digit);
						BigDecimal bd = new BigDecimal(d);
						/*
						 * digit = bd.round(new MathContext(15)).toPlainString();
						 * System.out.println(digit);
						 */
						String tryDigit = bd + "";
						int indexOfDot = tryDigit.indexOf(".");
						int secondDigit = Integer
								.parseInt(tryDigit.substring(indexOfDot + 1, indexOfDot + 2));
						if (secondDigit > 5) {
							digit = tryDigit.substring(0, indexOfDot + 3);
						} else {
							BigDecimal db = bd.setScale(1, RoundingMode.HALF_UP);
							digit = db.toPlainString();
						}

					} 
                    if (read_line == 1) {
                      if (cellCount == 3) {
                        acq_iss_bin = digit;
                        cellCount++;
                        continue;
                      } 
                      continue label98;
                    } 
                    if (reading_line || last_line) {
                      System.out.println("Cell count is " + cellCount + " and Data is " + digit);
                      ps.setString(cellCount, digit);
                      cellCount++;
                      continue;
                    } 
                    if (total_encounter) {
                      System.out
                        .println("Cell count is " + cellCount + " and acq_iss_bin is " + digit);
                      acq_iss_bin = digit;
                      total_encounter = false;
                    } 
                  } 
              } 
            } 
            if (reading_line || last_line) {
              System.out.println("Before inserting data");
              logger.info("Cell count is " + cellCount);
              ps.setString(cellCount++, beanObj.getFileDate());
              ps.setString(cellCount++, beanObj.getCreatedBy());
              ps.setString(cellCount++, beanObj.getCycle());
              ps.execute();
            } 
            logger.info("cellcount is " + cellCount);
          } 
        } 
      } 
      con.commit();
      con.close();
      long end = System.currentTimeMillis();
      System.out.println("start and end diff" + (start - end));
      System.out.println(" table  insert");
      System.out.println("Data Inserted");
      mapObj.put("result", Boolean.valueOf(true));
      mapObj.put("count", Integer.valueOf(totalcount));
    } catch (Exception e) {
      e.printStackTrace();
      mapObj.put("result", Boolean.valueOf(false));
      mapObj.put("count", Integer.valueOf(totalcount));
      try {
        con.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      } 
    } 
    return mapObj;
  }
}
