package com.recon.util;


import com.recon.model.RupayUploadBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.web.multipart.MultipartFile;

public class ReadRupayIntDSCRFile {
  private static final Logger logger = Logger.getLogger(com.recon.util.ReadRupayIntDSCRFile.class);
  
  SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");
  
  SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
  
  public HashMap<String, Object> fileupload(RupayUploadBean beanObj, Connection con, MultipartFile file) throws Exception {
    HashMap<String, Object> mapObj = new HashMap<>();
    String set_ccy_2 = "", status_txn = "", productname = "", bankname1 = "", bankname = "", settlement_bin = "";
    String iss_bin = "", inward = "", status = "", txncycle = "", txntype = "", channel = "", txn_count = "";
    String txn_ccy = "", txn_amt_cr = "", txn_amt_dr = "", set_ccy = "", set_amt_dr = "", set_amt_cr = "";
    String int_fee_amt_dr = "", int_fee_amt_cr = "", mem_inc_fee_amt_dr = "", mem_inc_fee_amt_cr = "";
    String cus_com_dr = "", cus_com_cr = "", oth_fee_amt_dr = "", oth_fee_amt_cr = "", oth_fee_gst_dr = "";
    String oth_fee_gst_cr = "", final_sum_cr = "", final_sum_dr = "", final_net = "", final_net2 = "";
    String final_nettotat = "";
    String query = "insert into irgcs_rupay_int_dsr_rawdata (bank_name ,SETT_BIN , ISS_BIN , INWARD_OUTWARD , STATUS_APP ,TXNCYCLE,TXNTYPE,CHANNEL , TXN_COUNT , TXN_CCY , TXN_AMT_DR , TXN_AMT_CR , BILL_AMT_CR,BILL_AMT_DR,SET_CCY ,SET_AMT_DR , SET_AMT_CR , INT_FEE_DR , INT_FEE_CR ,MEM_INC_FEE_DR , MEM_INC_FEE_CR , OTH_FEE_AMT_DR , OTH_FEE_AMT_CR ,OTH_FEE_GST_DR , OTH_FEE_GST_CR, FINAL_SUM_CR , FINAL_SUM_DR , FINAL_NET , FILEDATE , CREATEDBY,PRODUCT_NAME,TXN_FLAG,FILENAME,CYCLE) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    int cellIdx = 0;
    int count = 0;
    boolean totalFlag = false;
    try {
      HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
      HSSFSheet sheet = wb.getSheetAt(0);
      HSSFFormulaEvaluator hSSFFormulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
      PreparedStatement ps = con.prepareStatement(query);
      for (int rowNumber = 0; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
        if (rowNumber != 0) {
          HSSFRow hSSFRow = sheet.getRow(rowNumber);
          cellIdx = 0;
          if (hSSFRow != null)
            for (cellIdx = 0; cellIdx < hSSFRow.getLastCellNum(); cellIdx++) {
              count++;
              try {
                Cell currentCell = hSSFRow.getCell(cellIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (currentCell.getCellType() == 1)
                  if (currentCell.getStringCellValue().equals("A") || 
                    currentCell.getStringCellValue().equals("D") || 
                    currentCell.getStringCellValue().equals("NA"))
                    status_txn = currentCell.getStringCellValue();  
                if (currentCell.getCellType() == 1 && 
                  count == 1 && (
                  currentCell.getStringCellValue().contains("DCI-") || 
                  currentCell.getStringCellValue().contains("JCB-") || 
                  currentCell.getStringCellValue().contains("Total")))
                  productname = currentCell.getStringCellValue(); 
                if (count == 2)
                  bankname1 = currentCell.getStringCellValue(); 
                if (hSSFRow.getCell(cellIdx).getCellType() == 1 || 
                  hSSFRow.getCell(cellIdx).getCellType() == 0 || 
                  hSSFRow.getCell(cellIdx).getCellType() == 3)
                  switch (cellIdx) {
                    case 0:
                      if (currentCell.getStringCellValue().equals("") || 
                        currentCell.getStringCellValue().isEmpty());
                      break;
                    case 1:
                      if (currentCell.getStringCellValue().equals("") || 
                        currentCell.getStringCellValue().isEmpty());
                      break;
                    case 2:
                      if (currentCell.getStringCellValue().equals("") || 
                        currentCell.getStringCellValue().isEmpty())
                        break; 
                      bankname = currentCell.getStringCellValue();
                      break;
                    case 3:
                      if (currentCell.getStringCellValue().equals("") || 
                        currentCell.getStringCellValue().isEmpty())
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
                      } else {
                        if (currentCell.getStringCellValue().equalsIgnoreCase("INWARD") || 
                          currentCell.getStringCellValue().equalsIgnoreCase("OUTWARD")) {
                          inward = currentCell.getStringCellValue();
                          totalFlag = true;
                        } 
                        break;
                      } 
                    case 281:
                      if (currentCell.getStringCellValue().equalsIgnoreCase("UNION BANK") || 
                        currentCell.getStringCellValue().equalsIgnoreCase("TOTAL"))
                        break; 
                    case 282:
                      if (currentCell.getStringCellValue().equalsIgnoreCase("DCI-") || 
                        currentCell.getStringCellValue().equalsIgnoreCase("TOTAL") || 
                        currentCell.getStringCellValue().equalsIgnoreCase("JCB-"))
                        break; 
                    case 6:
                      if (!currentCell.getStringCellValue().isEmpty() || 
                        !currentCell.getStringCellValue().isEmpty()) {
                        status = currentCell.getStringCellValue();
                        break;
                      } 
                      if (currentCell.getStringCellValue().isEmpty() || 
                        currentCell.getStringCellValue().isEmpty())
                        break; 
                    case 7:
                      if (currentCell.getStringCellValue().isEmpty() || 
                        currentCell.getStringCellValue().isEmpty())
                        break; 
                      txncycle = currentCell.getStringCellValue();
                      break;
                    case 8:
                      if (currentCell.getStringCellValue().isEmpty() || 
                        currentCell.getStringCellValue().isEmpty())
                        break; 
                      txntype = currentCell.getStringCellValue();
                      break;
                    case 9:
                      if (currentCell.getStringCellValue().isEmpty() || 
                        currentCell.getStringCellValue().isEmpty())
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
                      if (currentCell.getStringCellValue().isEmpty() || 
                        currentCell.getStringCellValue().isEmpty())
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
                      if (currentCell.getCellType() == 1)
                        set_ccy = currentCell.getStringCellValue(); 
                      set_ccy = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 141:
                      if (currentCell.getCellType() == 1)
                        set_ccy_2 = currentCell.getStringCellValue(); 
                      set_ccy_2 = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 15:
                      if (currentCell.getCellType() == 1)
                        set_amt_dr = currentCell.getStringCellValue(); 
                      set_amt_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 16:
                      if (currentCell.getCellType() == 1)
                        set_amt_cr = currentCell.getStringCellValue(); 
                      set_amt_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 17:
                      if (currentCell.getCellType() == 1)
                        int_fee_amt_dr = currentCell.getStringCellValue(); 
                      int_fee_amt_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 18:
                      if (currentCell.getCellType() == 1)
                        int_fee_amt_cr = currentCell.getStringCellValue(); 
                      int_fee_amt_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 19:
                      if (currentCell.getCellType() == 1)
                        mem_inc_fee_amt_dr = currentCell.getStringCellValue(); 
                      mem_inc_fee_amt_dr = 
                        NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 20:
                      if (currentCell.getCellType() == 1)
                        mem_inc_fee_amt_cr = currentCell.getStringCellValue(); 
                      mem_inc_fee_amt_cr = 
                        NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 21:
                      if (currentCell.getCellType() == 1)
                        cus_com_dr = currentCell.getStringCellValue(); 
                      cus_com_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 22:
                      if (currentCell.getCellType() == 1)
                        cus_com_cr = currentCell.getStringCellValue(); 
                      cus_com_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 23:
                      if (currentCell.getCellType() == 1)
                        oth_fee_amt_dr = currentCell.getStringCellValue(); 
                      oth_fee_amt_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 24:
                      if (currentCell.getCellType() == 1)
                        oth_fee_amt_cr = currentCell.getStringCellValue(); 
                      oth_fee_amt_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 25:
                      if (currentCell.getCellType() == 1)
                        oth_fee_gst_dr = currentCell.getStringCellValue(); 
                      oth_fee_gst_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 26:
                      if (currentCell.getCellType() == 1)
                        oth_fee_gst_cr = currentCell.getStringCellValue(); 
                      oth_fee_gst_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 27:
                      if (currentCell.getCellType() == 1)
                        final_sum_cr = currentCell.getStringCellValue(); 
                      final_sum_cr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 28:
                      if (currentCell.getCellType() == 1)
                        final_sum_dr = currentCell.getStringCellValue(); 
                      final_sum_dr = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 29:
                      if (currentCell.getCellType() == 1)
                        final_sum_dr = currentCell.getStringCellValue(); 
                      final_net = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      break;
                    case 30:
                      if (currentCell.getCellType() == 1) {
                        final_nettotat = currentCell.getStringCellValue();
                        System.out.println("final_nettotat s " + final_nettotat);
                        ps.setString(1, bankname1);
                        ps.setString(2, bankname);
                        ps.setString(3, settlement_bin);
                        ps.setString(4, iss_bin);
                        ps.setString(5, status_txn);
                        System.out.println("data " + iss_bin);
                        if (iss_bin.equalsIgnoreCase("INWARD GST")) {
                          ps.setString(6, "Presentment");
                        } else {
                          ps.setString(6, status);
                        } 
                        ps.setString(7, txntype);
                        ps.setString(8, channel);
                        ps.setString(9, txn_amt_dr);
                        ps.setString(10, txn_count);
                        ps.setString(11, txn_amt_cr);
                        ps.setString(12, set_amt_cr);
                        ps.setString(13, set_amt_dr);
                        ps.setString(14, set_ccy);
                        ps.setString(15, txn_ccy);
                        ps.setString(16, int_fee_amt_cr);
                        ps.setString(17, mem_inc_fee_amt_dr);
                        ps.setString(18, mem_inc_fee_amt_cr);
                        ps.setString(19, cus_com_dr);
                        ps.setString(20, mem_inc_fee_amt_cr);
                        ps.setString(21, oth_fee_amt_dr);
                        ps.setString(22, oth_fee_amt_cr);
                        ps.setString(23, oth_fee_gst_dr);
                        ps.setString(24, oth_fee_gst_cr);
                        ps.setString(25, final_sum_cr);
                        ps.setString(26, final_sum_dr);
                        ps.setString(27, final_net);
                        ps.setString(28, final_nettotat);
                        ps.setString(29, beanObj.getFileDate());
                        ps.setString(30, beanObj.getCreatedBy());
                        ps.setString(31, productname);
                        ps.setString(32, txncycle);
                        ps.setString(33, file.getOriginalFilename());
                        ps.setString(34, beanObj.getCycle());
                        ps.addBatch();
                        ps.addBatch();
                        break;
                      } 
                      final_nettotat = NumberToTextConverter.toText(currentCell.getNumericCellValue());
                      ps.setString(1, bankname1);
                      ps.setString(2, bankname);
                      ps.setString(3, settlement_bin);
                      ps.setString(4, iss_bin);
                      ps.setString(5, status_txn);
                      System.out.println("data " + iss_bin);
                      if (iss_bin.equalsIgnoreCase("INWARD GST")) {
                        ps.setString(6, "Presentment");
                      } else {
                        ps.setString(6, status);
                      } 
                      ps.setString(7, txntype);
                      ps.setString(8, channel);
                      ps.setString(9, txn_amt_dr);
                      ps.setString(10, txn_count);
                      ps.setString(11, txn_amt_cr);
                      ps.setString(12, set_amt_cr);
                      ps.setString(13, set_amt_dr);
                      ps.setString(14, set_ccy);
                      ps.setString(15, txn_ccy);
                      ps.setString(16, int_fee_amt_cr);
                      ps.setString(17, mem_inc_fee_amt_dr);
                      ps.setString(18, mem_inc_fee_amt_cr);
                      ps.setString(19, cus_com_dr);
                      ps.setString(20, mem_inc_fee_amt_cr);
                      ps.setString(21, oth_fee_amt_dr);
                      ps.setString(22, oth_fee_amt_cr);
                      ps.setString(23, oth_fee_gst_dr);
                      ps.setString(24, oth_fee_gst_cr);
                      ps.setString(25, final_sum_cr);
                      ps.setString(26, final_sum_dr);
                      ps.setString(27, final_net);
                      ps.setString(28, final_nettotat);
                      ps.setString(29, beanObj.getFileDate());
                      ps.setString(30, beanObj.getCreatedBy());
                      ps.setString(31, productname);
                      ps.setString(32, txncycle);
                      ps.setString(33, file.getOriginalFilename());
                      ps.setString(34, beanObj.getCycle());
                      ps.addBatch();
                      break;
                  }  
              } catch (Exception e) {
                e.printStackTrace();
              } 
            }  
          count = 0;
        } 
      } 
      ps.executeBatch();
      ps.close();
      mapObj.put("result", Boolean.valueOf(true));
    } catch (Exception e) {
      e.printStackTrace();
      mapObj.put("result", Boolean.valueOf(false));
    } 
    return mapObj;
  }
}
