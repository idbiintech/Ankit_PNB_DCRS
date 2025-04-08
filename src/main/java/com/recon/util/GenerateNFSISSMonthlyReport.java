package com.recon.util;


import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

public class GenerateNFSISSMonthlyReport extends AbstractExcelView {
  protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook1, HttpServletRequest request, HttpServletResponse response) throws Exception {
    System.out.println("Inside Excel Download");
    @SuppressWarnings("unchecked")
	List<Object> Data = (List<Object>)map.get("Monthly_data");
    String name = (String)map.get("ReportName");
    String formattedString = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
    String filename = (name != null && !name.equalsIgnoreCase("")) ? name : "NFS_SETTLEMENT_REPORT";
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");
    ServletOutputStream servletOutputStream = response.getOutputStream();
    SXSSFWorkbook workbook = new SXSSFWorkbook();
    int maxRowCount = 1000000;
    createSheet(workbook, "NFS-ISS-CBS-MATCHED-1", Data, 0, 1, maxRowCount);
    createSheet(workbook, "NFS-ISS-CBS-UNRECON-1", Data, 2, 3, maxRowCount);
    createSheet(workbook, "NFS-ISS-CBS-MATCHED-2", Data, 4, 5, maxRowCount);
    createSheet(workbook, "NFS-ISS-CBS-UNRECON-2", Data, 6, 7, maxRowCount);
    createSheet(workbook, "NFS-ISS-MATCHED-2", Data, 8, 9, maxRowCount);
    createSheet(workbook, "NFS-ISS-UNRECON-2", Data, 10, 11, maxRowCount);
    createSheet(workbook, "LATE-REVERSAL-MATCHED", Data, 12, 13, maxRowCount);
    createSheet(workbook, "NFS-REV-MATCHED", Data, 14, 15, maxRowCount);
    createSheet(workbook, "NFS-REV-UNMATCHED", Data, 16, 17, maxRowCount);
    workbook.write((OutputStream)servletOutputStream);
    servletOutputStream.close();
    response.getOutputStream().flush();
    System.out.println("Completed Excel generation");
  }
  
  private void createSheet(SXSSFWorkbook workbook, String sheetName, List<Object> data, int headerIndex, int dataIndex, int maxRowCount) {
    @SuppressWarnings("unchecked")
	List<String> headers = (List<String>)data.get(headerIndex);
    @SuppressWarnings("unchecked")
	List<Object> dataList = (List<Object>)data.get(dataIndex);
    SXSSFSheet sheet = workbook.createSheet(sheetName);
    CellStyle headerStyle = createHeaderStyle(workbook);
    createHeaderRow(sheet, headers, headerStyle);
    int rowCount = 1;
    for (int i = 0; i < dataList.size(); i++) {
      if (rowCount > maxRowCount) {
        sheet = workbook.createSheet(String.valueOf(sheetName) + "-part" + (i / maxRowCount + 1));
        createHeaderRow(sheet, headers, headerStyle);
        rowCount = 1;
      } 
      SXSSFRow rowEntry = sheet.createRow(rowCount++);
      @SuppressWarnings("unchecked")
	Map<String, String> rowData = (Map<String, String>)dataList.get(i);
      populateDataRow(rowEntry, rowData, headers);
    } 
  }
  
  private void createHeaderRow(SXSSFSheet sheet, List<String> headers, CellStyle headerStyle) {
    SXSSFRow headerRow = sheet.createRow(0);
    for (int i = 0; i < headers.size(); i++) {
      headerRow.createCell(i).setCellValue(headers.get(i));
      headerRow.getCell(i).setCellStyle(headerStyle);
    } 
  }
  
  private void populateDataRow(SXSSFRow rowEntry, Map<String, String> rowData, List<String> headers) {
    for (int m = 0; m < headers.size(); m++) {
      String cellValue = rowData.get(headers.get(m));
      if (cellValue != null)
        rowEntry.createCell(m).setCellValue(cellValue); 
    } 
  }
  
  private CellStyle createHeaderStyle(SXSSFWorkbook workbook) {
    Font font = workbook.createFont();
    font.setFontName("Arial");
    font.setBold(true);
    font.setColor(IndexedColors.BLACK.getIndex());
    CellStyle style = workbook.createCellStyle();
    style.setFont(font);
    style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    return style;
  }
}
