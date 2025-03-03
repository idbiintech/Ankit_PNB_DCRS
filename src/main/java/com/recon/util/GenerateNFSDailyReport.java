package com.recon.util;


import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class GenerateNFSDailyReport extends AbstractExcelView {
  protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
    System.out.println("Inside Daily Excel Download");
    List<Object> Data = (List<Object>)map.get("data");
    String name = (String)map.get("ReportName");
    List<Object> monthly_Data = new ArrayList();
    List<String> Excel_Headers = new ArrayList<>();
    if (Data != null && Data.size() > 0)
      Excel_Headers = (List<String>)Data.get(0); 
    System.out.println("Got columns list");
    if (Data != null && Data.size() > 1)
      monthly_Data = (List<Object>)Data.get(1); 
    System.out.println("Got the data");
    String filename = "SETTLEMENT_REPORT";
    if (name != null && !name.equalsIgnoreCase(""))
      filename = name; 
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-disposition", "attachment; filename=" + filename + ".xls");
    ServletOutputStream servletOutputStream = response.getOutputStream();
    workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("Report");
    HSSFFont hSSFFont1 = workbook.createFont();
    hSSFFont1.setFontName("Arial");
    hSSFFont1.setColor(IndexedColors.BLACK.getIndex());
    HSSFCellStyle hSSFCellStyle1 = workbook.createCellStyle();
    hSSFCellStyle1.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
    hSSFCellStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    HSSFFont hSSFFont2 = workbook.createFont();
    hSSFFont2.setFontName("Arial");
    hSSFFont2.setBold(true);
    hSSFCellStyle1.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
    hSSFCellStyle1.setFont((Font)hSSFFont2);
    HSSFCellStyle hSSFCellStyle2 = workbook.createCellStyle();
    hSSFCellStyle2.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
    HSSFRow header = sheet.createRow(0);
    for (int i = 0; i < Excel_Headers.size(); i++) {
      header.createCell(i).setCellValue(Excel_Headers.get(i));
      header.getCell(i).setCellStyle((CellStyle)hSSFCellStyle1);
    } 
    for (int j = 0; j < monthly_Data.size(); j++) {
      HSSFRow rowEntry = sheet.createRow(j + 1);
      Map<String, String> map_data = (Map<String, String>)monthly_Data.get(j);
      if (map_data.size() > 0)
        for (int m = 0; m < Excel_Headers.size(); m++) {
          if (((String)Excel_Headers.get(m)).equalsIgnoreCase("DESCRIPTION") && (((String)map_data.get(Excel_Headers.get(m))).equalsIgnoreCase("Final Settlement Amount") || (
            (String)map_data.get(Excel_Headers.get(m))).equalsIgnoreCase("Net Adjusted Amount") || ((String)map_data.get(Excel_Headers.get(m))).equalsIgnoreCase("Issuer/ Acquirer Sub Totals") || (
            (String)map_data.get(Excel_Headers.get(m))).equalsIgnoreCase("Settlement Amount") || ((String)map_data.get(Excel_Headers.get(m))).equalsIgnoreCase("Adjustments") || (
            (String)map_data.get(Excel_Headers.get(m))).equalsIgnoreCase("Penalty"))) {
            HSSFCell hSSFCell = rowEntry.createCell(m);
            hSSFCell.setCellValue(map_data.get(Excel_Headers.get(m)));
            hSSFCell.setCellStyle((CellStyle)hSSFCellStyle1);
          } else {
            rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers.get(m)));
          } 
        }  
    } 
    workbook.write((OutputStream)servletOutputStream);
    servletOutputStream.close();
    response.getOutputStream().flush();
  }
}
