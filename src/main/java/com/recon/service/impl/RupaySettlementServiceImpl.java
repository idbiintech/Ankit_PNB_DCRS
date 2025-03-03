package com.recon.service.impl;


import com.recon.dao.impl.RupaySettelementDaoImpl;
import com.recon.model.RupaySettlementBean;
import com.recon.model.RupayUploadBean;
import com.recon.service.RupaySettlementService;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RupaySettlementServiceImpl implements RupaySettlementService {
  @Autowired
  RupaySettelementDaoImpl rupayDao;
  
  private static final Logger logger = Logger.getLogger(com.recon.service.impl.RupaySettlementServiceImpl.class);
  
  public HashMap<String, Object> uploadExcelFile(RupaySettlementBean beanObj, MultipartFile file) throws Exception {
    HashMap<String, Object> mapObj = new HashMap<>();
    try {
      HSSFFormulaEvaluator hSSFFormulaEvaluator = null;
      Path tempDir = Files.createTempDirectory("", (FileAttribute<?>[])new FileAttribute[0]);
      File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
      file.transferTo(tempFile);
      Workbook workbook = WorkbookFactory.create(tempFile);
      Sheet sheet = workbook.getSheetAt(0);
      String fileName = file.getOriginalFilename();
      String extention = FilenameUtils.getExtension(fileName);
      FormulaEvaluator objFormulaEvaluator = null;
      if (extention.equals("xls")) {
        hSSFFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook)workbook);
      } else if (extention.equals("xlsm") || extention.equals("xlsx")) {
        XSSFFormulaEvaluator xSSFFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook)workbook);
      } else {
        hSSFFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook)workbook);
      } 
      int g = sheet.getPhysicalNumberOfRows();
      List<Row> rowlist = new ArrayList<>();
      String target = "";
      if (g == 0)
        target = "no record"; 
      for (int y = 0; y <= sheet.getLastRowNum(); y++) {
        Row xlsxRow1 = sheet.getRow(y);
        rowlist.add(xlsxRow1);
      } 
      String tempSettlementDate = "";
      String settlementDate = "";
      String tempBankName = "";
      String bankName = "";
      String tempMemberName = "";
      String memberName = "";
      String tempMemBankPid = "";
      String memBankPid = "";
      String bankType = "";
      String tempDRCR = "";
      String DRCR = "";
      String sumCr = "";
      String sumDr = "";
      String netSum = "";
      int count = 0;
      int srNo = 1;
      RupaySettlementBean data = null;
      List<RupaySettlementBean> dataList = new ArrayList<>();
      for (int i = 1; i < rowlist.size(); i++) {
        int j = 0;
        Row rw = rowlist.get(i);
        DataFormatter formatter = new DataFormatter();
        hSSFFormulaEvaluator.evaluate(rw.getCell(j));
        tempSettlementDate = formatter.formatCellValue(rw.getCell(j), (FormulaEvaluator)hSSFFormulaEvaluator);
        if (!tempSettlementDate.equals(""))
          settlementDate = tempSettlementDate; 
        hSSFFormulaEvaluator.evaluate(rw.getCell(j++));
        tempBankName = formatter.formatCellValue(rw.getCell(j++), (FormulaEvaluator)hSSFFormulaEvaluator);
        if (!tempBankName.equals(""))
          bankName = tempBankName; 
        hSSFFormulaEvaluator.evaluate(rw.getCell(j));
        tempMemberName = formatter.formatCellValue(rw.getCell(j), (FormulaEvaluator)hSSFFormulaEvaluator);
        if (!tempMemberName.equals(""))
          memberName = tempMemberName; 
        hSSFFormulaEvaluator.evaluate(rw.getCell(j++));
        tempMemBankPid = formatter.formatCellValue(rw.getCell(j++), (FormulaEvaluator)hSSFFormulaEvaluator);
        if (!tempMemBankPid.equals("") && !tempMemBankPid.equalsIgnoreCase("Total"))
          memBankPid = tempMemBankPid; 
        hSSFFormulaEvaluator.evaluate(rw.getCell(j));
        bankType = formatter.formatCellValue(rw.getCell(j), (FormulaEvaluator)hSSFFormulaEvaluator);
        hSSFFormulaEvaluator.evaluate(rw.getCell(j++));
        tempDRCR = formatter.formatCellValue(rw.getCell(j++), (FormulaEvaluator)hSSFFormulaEvaluator);
        if (!tempDRCR.equals("") && !tempDRCR.equalsIgnoreCase("Total"))
          DRCR = tempDRCR; 
        hSSFFormulaEvaluator.evaluate(rw.getCell(j));
        sumCr = formatter.formatCellValue(rw.getCell(j), (FormulaEvaluator)hSSFFormulaEvaluator);
        hSSFFormulaEvaluator.evaluate(rw.getCell(j++));
        sumDr = formatter.formatCellValue(rw.getCell(j++), (FormulaEvaluator)hSSFFormulaEvaluator);
        hSSFFormulaEvaluator.evaluate(rw.getCell(j));
        netSum = formatter.formatCellValue(rw.getCell(j), (FormulaEvaluator)hSSFFormulaEvaluator);
        count++;
        if (count == 4) {
          count = 0;
          data = new RupaySettlementBean();
          data.setSettlementDate(settlementDate);
          data.setBankName(bankName);
          data.setMemberName(memberName);
          data.setMemberBankPid(memBankPid);
          data.setDrcr(DRCR);
          data.setSumCr(sumCr);
          data.setSumDr(sumDr);
          data.setNetSum(netSum);
          data.setCycle(beanObj.getCycle());
          data.setSrNo(srNo++);
          dataList.add(data);
        } else if (bankName.equalsIgnoreCase("Total")) {
          data = new RupaySettlementBean();
          data.setSettlementDate(settlementDate);
          data.setBankName(bankName);
          data.setMemberName(memberName);
          data.setMemberBankPid("TOTAL");
          data.setDrcr(DRCR);
          data.setSumCr(sumCr);
          data.setSumDr(sumDr);
          data.setNetSum(netSum);
          data.setCycle(beanObj.getCycle());
          data.setSrNo(srNo++);
          dataList.add(data);
        } 
      } 
      String result = this.rupayDao.uploadRupaySettlementData(dataList, beanObj);
      if (result.equalsIgnoreCase("success")) {
        mapObj.put("result", Boolean.valueOf(true));
        this.rupayDao.updateFileSettlement(beanObj, dataList.size());
      } else {
        mapObj.put("entry", Boolean.valueOf(false));
      } 
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
      mapObj.put("result", Boolean.valueOf(false));
      mapObj.put("msg", e.getMessage());
    } 
    return mapObj;
  }
  
  public HashMap<String, Object> validatePrevFileUpload(RupaySettlementBean beanObj) {
    HashMap<String, Object> validate = this.rupayDao.validatePrevFileUpload(beanObj);
    return validate;
  }
  
  public void generateRupaySettlmentTTum(String settlementDate, HttpServletResponse response) {
    HashMap<String, List<RupaySettlementBean>> map = this.rupayDao.getTTUMData(settlementDate);
    RupaySettlementBean bean = null;
    FileWriter fw = null;
    try {
      Path tempDir = Files.createTempDirectory("", (FileAttribute<?>[])new FileAttribute[0]);
      String fileName = "RupaySettlementTTUM_" + settlementDate + ".txt";
      File tempFile = tempDir.resolve(fileName).toFile();
      tempFile.createNewFile();
      fw = new FileWriter(tempFile);
      PrintWriter out = new PrintWriter(fw);
      List<RupaySettlementBean> datListWithTotal = map.get("datListWithTotal");
      List<RupaySettlementBean> datListWithoutTotal = map.get("datListWithoutTotal");
      int i;
      for (i = 0; i < datListWithTotal.size(); i++) {
        bean = datListWithTotal.get(i);
        String settlmentDate = bean.getSettlementDate();
        String[] arr = settlmentDate.split("-");
        String formattedSettDate = " " + arr[0] + " " + arr[1] + " " + arr[2];
        String tmpDrcr = bean.getDrcr();
        String k = bean.getNetSum();
        String fomattedNetSum = formatNetAmount(k);
        if (bean.getMemberBankPid().equals("TOTAL") && 
          k.contains("-"))
          fomattedNetSum = "-" + fomattedNetSum; 
        String drOrCr = tmpDrcr.substring(0, 1);
        String identifier = "_" + bean.getCycle();
        Formatter creditLine = new Formatter();
        creditLine.format("%-16s", new Object[] { bean.getAccountNo() });
        creditLine.format("%3s", new Object[] { "INR999" });
        creditLine.format("%5s", new Object[] { "" });
        creditLine.format("%1s", new Object[] { drOrCr });
        creditLine.format("%16s", new Object[] { fomattedNetSum });
        creditLine.format("%14s", new Object[] { "RuPay sett for" });
        creditLine.format("%7s", new Object[] { formattedSettDate });
        creditLine.format("%2s", new Object[] { identifier });
        creditLine.format("%63s", new Object[] { "" });
        creditLine.format("%20s", new Object[] { fomattedNetSum });
        creditLine.format("%3s", new Object[] { "INR" });
        creditLine.format("%127s", new Object[] { "" });
        creditLine.format("%1s", new Object[] { "@" });
        creditLine.format(System.lineSeparator(), new Object[0]);
        fw.write(creditLine.toString());
        creditLine.close();
      } 
      for (i = 0; i < datListWithoutTotal.size(); i++) {
        bean = datListWithoutTotal.get(i);
        String settlmentDate = bean.getSettlementDate();
        String[] arr = settlmentDate.split("-");
        String formattedSettDate = " " + arr[0] + " " + arr[1] + " " + arr[2];
        String tmpDrcr = bean.getDrcr();
        String k = bean.getNetSum();
        String fomattedNetSum = formatNetAmount(k);
        if (bean.getMemberBankPid().equals("TOTAL") && 
          k.contains("-"))
          fomattedNetSum = "-" + fomattedNetSum; 
        String drOrCr = tmpDrcr.substring(0, 1);
        String identifier = "_" + bean.getCycle();
        Formatter creditLine = new Formatter();
        creditLine.format("%-16s", new Object[] { bean.getAccountNo() });
        creditLine.format("%3s", new Object[] { "INR999" });
        creditLine.format("%5s", new Object[] { "" });
        creditLine.format("%1s", new Object[] { drOrCr });
        creditLine.format("%16s", new Object[] { fomattedNetSum });
        creditLine.format("%14s", new Object[] { "RuPay sett for" });
        creditLine.format("%7s", new Object[] { formattedSettDate });
        creditLine.format("%2s", new Object[] { identifier });
        creditLine.format("%63s", new Object[] { "" });
        creditLine.format("%20s", new Object[] { fomattedNetSum });
        creditLine.format("%3s", new Object[] { "INR" });
        creditLine.format("%127s", new Object[] { "" });
        creditLine.format("%1s", new Object[] { "@" });
        creditLine.format(System.lineSeparator(), new Object[0]);
        fw.write(creditLine.toString());
        creditLine.close();
      } 
      try {
        fw.flush();
        fw.close();
      } catch (Exception e) {
        e.printStackTrace();
      } 
      InputStream inputStream = new BufferedInputStream(new FileInputStream(tempFile));
      String mimeType = URLConnection.guessContentTypeFromStream(inputStream);
      if (mimeType == null)
        mimeType = "application/octet-stream"; 
      response.setContentType(mimeType);
      response.setContentLength((int)tempFile.length());
      response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", new Object[] { tempFile.getName() }));
      FileCopyUtils.copy(inputStream, (OutputStream)response.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public static String formatNetAmount(String netAmount) {
    String orignal = netAmount;
    String newOne = "";
    String tmp = "";
    if (orignal.contains(".")) {
      if (orignal.contains("-")) {
        tmp = orignal.replace("-", "");
      } else {
        tmp = orignal;
      } 
      String[] arr = tmp.split("\\.");
      String format = String.format("%014d", new Object[] { Integer.valueOf(Integer.parseInt(arr[0])) });
      newOne = String.valueOf(format) + "." + arr[1];
    } else {
      String format = String.format("%014d", new Object[] { Integer.valueOf(Integer.parseInt(orignal)) });
      newOne = format;
      System.out.println("newOne   " + newOne);
    } 
    return newOne;
  }
  
  public boolean readFile(RupayUploadBean beanObj, MultipartFile file) {
    HashMap<String, Object> output = null;
    boolean flag = false;
    try {
      logger.info("File name is " + beanObj.getFileName());
      if (beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("CHARGEBACK")) {
        logger.info("Chargeback File name");
        flag = this.rupayDao.readRupayChargeback(beanObj, file);
        logger.info("ReadFlag " + flag);
      } else {
        logger.info("other File name");
        output = this.rupayDao.readRupayFiles(beanObj, file);
      } 
      if (output != null)
        flag = ((Boolean)output.get("result")).booleanValue(); 
      return flag;
    } catch (Exception e) {
      logger.info("Exception in ReadFile " + e);
      return false;
    } 
  }
  
  public boolean readIntFile(RupayUploadBean beanObj, MultipartFile file) {
    HashMap<String, Object> output = null;
    boolean flag = false;
    try {
      logger.info("File name is " + beanObj.getFileName());
      if (beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("CHARGEBACK")) {
        logger.info("Chargeback File name");
        flag = this.rupayDao.readRupayChargeback(beanObj, file);
        flag = ((Boolean)output.get("result")).booleanValue();
        logger.info("ReadFlag " + flag);
      } else {
        logger.info("other File name");
        output = this.rupayDao.readRupayIntFiles(beanObj, file);
        flag = ((Boolean)output.get("result")).booleanValue();
      } 
      return flag;
    } catch (Exception e) {
      logger.info("Exception in ReadFile " + e);
      return false;
    } 
  }
  
  public boolean checkFileUploaded(RupayUploadBean beanObj) {
    return this.rupayDao.checkFileUploaded(beanObj);
  }
  
  public HashMap<String, Object> validateRawfiles(RupayUploadBean beanObj) {
    HashMap<String, Object> output = this.rupayDao.validateRawfiles(beanObj);
    return output;
  }
  
  public HashMap<String, Object> validateSettlementFiles(RupayUploadBean beanObj) {
    HashMap<String, Object> output = this.rupayDao.validateSettlementFiles(beanObj);
    return output;
  }
  
  public HashMap<String, Object> processSettlementVisa(RupayUploadBean beanObj) {
    HashMap<String, Object> output = this.rupayDao.processSettlementVisa(beanObj);
    return output;
  }
  
  public HashMap<String, Object> processSettlementVisaACQ(RupayUploadBean beanObj) {
    HashMap<String, Object> output = this.rupayDao.processSettlementVisaACQ(beanObj);
    return output;
  }
  
  public HashMap<String, Object> processSettlementVisaINT(RupayUploadBean beanObj) {
    HashMap<String, Object> output = this.rupayDao.processSettlementVisaINT(beanObj);
    return output;
  }
  
  public HashMap<String, Object> processSettlementVisaINTACQ(RupayUploadBean beanObj) {
    HashMap<String, Object> output = this.rupayDao.processSettlementVisaINTACQ(beanObj);
    return output;
  }
  
  public HashMap<String, Object> processSettlementVisaRollback(RupayUploadBean beanObj) {
    HashMap<String, Object> output = this.rupayDao.processSettlementVisaRollback(beanObj);
    return output;
  }
  
  public HashMap<String, Object> processSettlementVisaRollbackACQ(RupayUploadBean beanObj) {
    HashMap<String, Object> output = this.rupayDao.processSettlementVisaRollbackACQ(beanObj);
    return output;
  }
  
  public HashMap<String, Object> processSettlementVisaRollbackINT(RupayUploadBean beanObj) {
    HashMap<String, Object> output = this.rupayDao.processSettlementVisaRollbackINT(beanObj);
    return output;
  }
  
  public HashMap<String, Object> rollbackUpdateDollarRate(RupayUploadBean beanObj) {
    HashMap<String, Object> output = this.rupayDao.rollbackUpdateDollarRate(beanObj);
    return output;
  }
  
  public HashMap<String, Object> processSettlementVisaRollbackINTACQ(RupayUploadBean beanObj) {
    HashMap<String, Object> output = this.rupayDao.processSettlementVisaRollbackINTACQ(beanObj);
    return output;
  }
  
  public boolean processSettlement(RupayUploadBean beanObj) {
    return this.rupayDao.processSettlement(beanObj);
  }
  
  public boolean processSettlementRRB(RupayUploadBean beanObj) {
    return this.rupayDao.processSettlementRRB(beanObj);
  }
  
  public boolean processSettlementVisa2(RupayUploadBean beanObj) {
    return this.rupayDao.processSettlementVisa2(beanObj);
  }
  
  public boolean processSettlementINT(RupayUploadBean beanObj) {
    return this.rupayDao.processSettlementINT(beanObj);
  }
  public boolean processSettlementRRBINT(RupayUploadBean beanObj) {
	    return this.rupayDao.processSettlementRRBINT(beanObj);
	  }
  
  public boolean processSettlementVISAINT(RupayUploadBean beanObj) {
    return this.rupayDao.processSettlementVISAINT(beanObj);
  }
  
  public boolean processSettlementQsparc(RupayUploadBean beanObj) {
    return this.rupayDao.processSettlementQsparc(beanObj);
  }
  
  public boolean processSettlementQsparcINT(RupayUploadBean beanObj) {
    return this.rupayDao.processSettlementQsparcINT(beanObj);
  }
  
  public boolean validateSettlementProcess(RupayUploadBean beanObj) {
    return this.rupayDao.validateSettlementProcess(beanObj);
  }
  
  public boolean validateSettlementProcessRRB(RupayUploadBean beanObj) {
    return this.rupayDao.validateSettlementProcessRRB(beanObj);
  }
  
  public boolean validateSettlementProcessVisa(RupayUploadBean beanObj) {
    return this.rupayDao.validateSettlementProcessVisa(beanObj);
  }
  
  public boolean InsertDollarRateVisa(RupayUploadBean beanObj) {
    return this.rupayDao.InsertDollarRateVisa(beanObj);
  }
  
  public boolean validateSettlementProcessVisa2(RupayUploadBean beanObj) {
    return this.rupayDao.validateSettlementProcessVisa2(beanObj);
  }
  
  public boolean validateSettlementProcessINT(RupayUploadBean beanObj) {
    return this.rupayDao.validateSettlementProcessINT(beanObj);
  }
  
  public boolean validateSettlementProcessRRBINT(RupayUploadBean beanObj) {
    return this.rupayDao.validateSettlementProcessRRBINT(beanObj);
  }
  
  public boolean validateSettlementProcessVISAINT(RupayUploadBean beanObj) {
    return this.rupayDao.validateSettlementProcessVISAINT(beanObj);
  }
  
  public boolean validateSettlementProcessqSPARC(RupayUploadBean beanObj) {
    return this.rupayDao.validateSettlementProcessQsparc(beanObj);
  }
  
  public boolean validateSettlementProcessQsparc(RupayUploadBean beanObj) {
    return this.rupayDao.validateSettlementProcessQsparc(beanObj);
  }
  
  public boolean validateSettlementProcessQsparcINT(RupayUploadBean beanObj) {
    return this.rupayDao.validateSettlementProcessQsparcINT(beanObj);
  }
  
  public List<Object> getSettlementData(RupayUploadBean beanObj) {
    return this.rupayDao.getSettlementData(beanObj);
  }
  
  public List<Object> getSettlementDataRRB(RupayUploadBean beanObj) {
    return this.rupayDao.getSettlementDataRRB(beanObj);
  }
  
  public List<Object> getSettlementDataVisa(RupayUploadBean beanObj) {
    return this.rupayDao.getSettlementDataVisa(beanObj);
  }
  
  public List<Object> getSettlementDataINT(RupayUploadBean beanObj) {
    return this.rupayDao.getSettlementDataINT(beanObj);
  }
  
  public List<Object> getSettlementDataQsparc(RupayUploadBean beanObj) {
    return this.rupayDao.getSettlementDataQsparc(beanObj);
  }
  
  public List<Object> getSettlementDataQsparcINT(RupayUploadBean beanObj) {
    return this.rupayDao.getSettlementDataQsparcINT(beanObj);
  }
  
  public Boolean validateSettlementTTUM(RupayUploadBean beanObj) {
    return this.rupayDao.validateSettlementTTUM(beanObj);
  }
  
  public Boolean visavalidateSettlementTTUM(RupayUploadBean beanObj) {
    return this.rupayDao.visavalidateSettlementTTUM(beanObj);
  }
  
  public boolean checkdata(RupayUploadBean beanObj) {
    return this.rupayDao.checkdata(beanObj).booleanValue();
  }
  
  public boolean visacheckdata(RupayUploadBean beanObj) {
    return this.rupayDao.visacheckdata(beanObj).booleanValue();
  }
  
  public String uploadPresentmentFile(RupayUploadBean beanObj, MultipartFile file) throws SQLException, Exception {
    return this.rupayDao.uploadPresentmentData(beanObj, file);
  }
  
  public String IntuploadPresentmentFile(RupayUploadBean beanObj, MultipartFile file) throws SQLException, Exception {
    return this.rupayDao.IntuploadPresentmentData(beanObj, file);
  }
  
  public boolean checkpresentmenetupload(RupayUploadBean beanObj) {
    return this.rupayDao.checkpresentmenetupload(beanObj).booleanValue();
  }
  
  public List<Object> getSettlementTTUMData(RupayUploadBean beanObj) {
    return this.rupayDao.getSettlementTTUMData(beanObj);
  }
  
  public List<Object> visagetSettlementTTUMData(RupayUploadBean beanObj) {
    return this.rupayDao.visagetSettlementTTUMData(beanObj);
  }
  
  public boolean processSettlementTTUM(RupayUploadBean beanObj) {
    return this.rupayDao.processSettlementTTUM(beanObj);
  }
  
  public boolean visaprocessSettlementTTUM(RupayUploadBean beanObj) {
    return this.rupayDao.visaprocessSettlementTTUM(beanObj);
  }
  
  public boolean IntvisaprocessSettlementTTUM(RupayUploadBean beanObj) {
    return this.rupayDao.IntvisaprocessSettlementTTUM(beanObj);
  }
  
  public boolean validateSettlementDiff(RupayUploadBean beanObj) {
    return this.rupayDao.validateSettlementDiff(beanObj);
  }
  
  public boolean processRectification(RupayUploadBean beanObj) {
    return this.rupayDao.processRectification(beanObj);
  }
  
  public HashMap<String, Object> validateDiffAmount(RupayUploadBean beanObj) {
    return this.rupayDao.validateDiffAmount(beanObj);
  }
  
  public boolean readNCMCFile(RupayUploadBean beanObj, MultipartFile file) {
    HashMap<String, Object> output = null;
    boolean flag = false;
    try {
      logger.info("File name is " + beanObj.getFileName());
      logger.info("other File name");
      output = this.rupayDao.readNCMCFiles(beanObj, file);
      if (output != null)
        flag = ((Boolean)output.get("result")).booleanValue(); 
      return flag;
    } catch (Exception e) {
      logger.info("Exception in ReadFile " + e);
      return false;
    } 
  }
  
  public boolean readNCMCINTFile(RupayUploadBean beanObj, MultipartFile file) {
    HashMap<String, Object> output = null;
    boolean flag = false;
    try {
      logger.info("File name is " + beanObj.getFileName());
      logger.info("other File name");
      output = this.rupayDao.readNCMCINTFiles(beanObj, file);
      if (output != null)
        flag = ((Boolean)output.get("result")).booleanValue(); 
      return flag;
    } catch (Exception e) {
      logger.info("Exception in ReadFile " + e);
      return false;
    } 
  }
  
  public boolean checkNCMCFileUploaded(RupayUploadBean beanObj) {
    return this.rupayDao.checkNCMCFileUploaded(beanObj);
  }
  
  public boolean validateQsparcTTUM(RupayUploadBean beanObj) {
    return this.rupayDao.validateQsparcTTUM(beanObj).booleanValue();
  }
  
  public List<Object> getQsparcTTUMData(RupayUploadBean beanObj) {
    return this.rupayDao.getQsparcTTUMData(beanObj);
  }
  
  public boolean processSettlementTTUMQsparc(RupayUploadBean beanObj) {
    return this.rupayDao.processSettlementTTUMQsparc(beanObj);
  }
  
  public boolean checkmoneyaddupload(RupayUploadBean beanObj) {
    return this.rupayDao.checkmoneyaddupload(beanObj).booleanValue();
  }
  
  public String uploadmoneyadd(RupayUploadBean beanObj, MultipartFile file) throws SQLException, Exception {
    return this.rupayDao.uploadmoneyadd(beanObj, file);
  }
  
  public boolean IRGCSprocessSettlementTTUM(RupayUploadBean beanObj) {
    return this.rupayDao.IRGCSprocessSettlementTTUM(beanObj);
  }
}
