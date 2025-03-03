package com.recon.util;


import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class GenerateMCACQATMReportINT extends AbstractExcelView {
  protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook1, HttpServletRequest request, HttpServletResponse response) throws Exception {
    System.out.println("Inside Excel Download");
    List<Object> Data = (List<Object>)map.get("Monthly_data");
    String name = (String)map.get("ReportName");
    List<String> Excel_Headers = new ArrayList<>();
    List<Object> monthly_Data = new ArrayList();
    List<String> Excel_Headers2 = new ArrayList<>();
    List<Object> sum_Data = new ArrayList();
    List<String> Excel_Headers3 = new ArrayList<>();
    List<Object> sum_Data2 = new ArrayList();
    List<String> Excel_Headers4 = new ArrayList<>();
    List<Object> sum_Data3 = new ArrayList();
    List<String> Excel_Headers5 = new ArrayList<>();
    List<Object> sum_Data4 = new ArrayList();
    List<String> Excel_Headers6 = new ArrayList<>();
    List<Object> sum_Data5 = new ArrayList();
    List<String> Excel_Headers7 = new ArrayList<>();
    List<Object> sum_Data6 = new ArrayList();
    List<String> Excel_Headers8 = new ArrayList<>();
    List<Object> sum_Data7 = new ArrayList();
    List<String> Excel_Headers9 = new ArrayList<>();
    List<Object> sum_Data8 = new ArrayList();
    List<String> Excel_Headers10 = new ArrayList<>();
    List<Object> sum_Data9 = new ArrayList();
    String formattedString = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
    System.out.println("Got columns list");
    System.out.println("Got the data");
    String filename = "MCACQDOM_RECON_REPORT";
    if (name != null && !name.equalsIgnoreCase(""))
      filename = name; 
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-disposition", "attachment; filename=" + filename + ".xls");
    ServletOutputStream servletOutputStream = response.getOutputStream();
    SXSSFWorkbook wb = new SXSSFWorkbook();
    SXSSFWorkbook workbook = new SXSSFWorkbook();
    SXSSFSheet sheet = workbook.createSheet("MASTERCARD_ACQ_INT_MATCHED");
    Font font = workbook.createFont();
    font.setFontName("Arial");
    font.setColor(IndexedColors.BLACK.getIndex());
    CellStyle calculatedHeader = workbook.createCellStyle();
    calculatedHeader.setFillForegroundColor(IndexedColors.RED.getIndex());
    calculatedHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    Font calculatedfont = workbook.createFont();
    calculatedfont.setFontName("Arial");
    calculatedfont.setBold(true);
    calculatedHeader.setFillBackgroundColor(IndexedColors.RED.getIndex());
    calculatedHeader.setFont(calculatedfont);
    CellStyle numberStyle = workbook.createCellStyle();
    numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
    SXSSFRow header = sheet.createRow(0);
    if (Data != null && Data.size() > 0)
      Excel_Headers = (List<String>)Data.get(0); 
    if (Data != null && Data.size() > 1)
      monthly_Data = (List<Object>)Data.get(1); 
    for (int i = 0; i < Excel_Headers.size(); i++) {
      header.createCell(i).setCellValue(Excel_Headers.get(i));
      header.getCell(i).setCellStyle(calculatedHeader);
    } 
    int j;
    for (j = 0; j < monthly_Data.size(); j++) {
      SXSSFRow rowEntry = sheet.createRow(j + 1);
      Map<String, String> map_data = (Map<String, String>)monthly_Data.get(j);
      if (map_data.size() > 0)
        for (int m = 0; m < Excel_Headers.size(); m++)
          rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers.get(m)));  
    } 
    sheet = workbook.createSheet("MASTERCARD_ACQ_INT_UNRECON");
    numberStyle = workbook.createCellStyle();
    numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
    header = sheet.createRow(0);
    if (Data != null && Data.size() > 2)
      Excel_Headers2 = (List<String>)Data.get(2); 
    if (Data != null && Data.size() > 3)
      sum_Data = (List<Object>)Data.get(3); 
    for (j = 0; j < Excel_Headers2.size(); j++) {
      header.createCell(j).setCellValue(Excel_Headers2.get(j));
      header.getCell(j).setCellStyle(calculatedHeader);
    } 
    for (j = 0; j < sum_Data.size(); j++) {
      SXSSFRow rowEntry = sheet.createRow(j + 1);
      Map<String, String> map_data = (Map<String, String>)sum_Data.get(j);
      if (map_data.size() > 0)
        for (int m = 0; m < Excel_Headers2.size(); m++)
          rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers2.get(m)));  
    } 
    sheet = workbook.createSheet("MASTERCARD_ACQ_INT_MATCHED-CBS");
    numberStyle = workbook.createCellStyle();
    numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
    header = sheet.createRow(0);
    if (Data != null && Data.size() > 2)
      Excel_Headers3 = (List<String>)Data.get(4); 
    if (Data != null && Data.size() > 3)
      sum_Data2 = (List<Object>)Data.get(5); 
    for (j = 0; j < Excel_Headers3.size(); j++) {
      header.createCell(j).setCellValue(Excel_Headers3.get(j));
      header.getCell(j).setCellStyle(calculatedHeader);
    } 
    for (j = 0; j < sum_Data2.size(); j++) {
      SXSSFRow rowEntry = sheet.createRow(j + 1);
      Map<String, String> map_data = (Map<String, String>)sum_Data2.get(j);
      if (map_data.size() > 0)
        for (int m = 0; m < Excel_Headers3.size(); m++)
          rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers3.get(m)));  
    } 
    sheet = workbook.createSheet("MASTERCARD_ACQ_INT_KNOCKOFF");
    numberStyle = workbook.createCellStyle();
    numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
    header = sheet.createRow(0);
    if (Data != null && Data.size() > 2)
      Excel_Headers4 = (List<String>)Data.get(6); 
    if (Data != null && Data.size() > 3)
      sum_Data3 = (List<Object>)Data.get(7); 
    for (j = 0; j < Excel_Headers4.size(); j++) {
      header.createCell(j).setCellValue(Excel_Headers4.get(j));
      header.getCell(j).setCellStyle(calculatedHeader);
    } 
    for (j = 0; j < sum_Data3.size(); j++) {
      SXSSFRow rowEntry = sheet.createRow(j + 1);
      Map<String, String> map_data = (Map<String, String>)sum_Data3.get(j);
      if (map_data.size() > 0)
        for (int m = 0; m < Excel_Headers4.size(); m++)
          rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers4.get(m)));  
    } 
    sheet = workbook.createSheet("MASTERCARD_ACQ_INT_UNRECON-CBS");
    numberStyle = workbook.createCellStyle();
    numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
    header = sheet.createRow(0);
    if (Data != null && Data.size() > 2)
      Excel_Headers5 = (List<String>)Data.get(8); 
    if (Data != null && Data.size() > 3)
      sum_Data4 = (List<Object>)Data.get(9); 
    for (j = 0; j < Excel_Headers5.size(); j++) {
      header.createCell(j).setCellValue(Excel_Headers5.get(j));
      header.getCell(j).setCellStyle(calculatedHeader);
    } 
    for (j = 0; j < sum_Data4.size(); j++) {
      SXSSFRow rowEntry = sheet.createRow(j + 1);
      Map<String, String> map_data = (Map<String, String>)sum_Data4.get(j);
      if (map_data.size() > 0)
        for (int m = 0; m < Excel_Headers5.size(); m++)
          rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers5.get(m)));  
    } 
    sheet = workbook.createSheet("INT_CBS_DOM_ATM_CROSS_MATCHED-CBS");
    numberStyle = workbook.createCellStyle();
    numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
    header = sheet.createRow(0);
    if (Data != null && Data.size() > 2)
      Excel_Headers6 = (List<String>)Data.get(10); 
    if (Data != null && Data.size() > 3)
      sum_Data5 = (List<Object>)Data.get(11); 
    for (j = 0; j < Excel_Headers6.size(); j++) {
      header.createCell(j).setCellValue(Excel_Headers6.get(j));
      header.getCell(j).setCellStyle(calculatedHeader);
    } 
    for (j = 0; j < sum_Data5.size(); j++) {
      SXSSFRow rowEntry = sheet.createRow(j + 1);
      Map<String, String> map_data = (Map<String, String>)sum_Data5.get(j);
      if (map_data.size() > 0)
        for (int m = 0; m < Excel_Headers6.size(); m++)
          rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers6.get(m)));  
    } 
    workbook.write((OutputStream)servletOutputStream);
    servletOutputStream.close();
    response.getOutputStream().flush();
  }
}
