package com.recon.util;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


@SuppressWarnings("unchecked")
public class GenerateCTCACQMonthlyReport extends AbstractExcelView {
	
	@SuppressWarnings({ "resource", "unused" })
	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook1, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
	System.out.println("Inside Excel Download");
	List<Object> Data = (List<Object>) map.get("Monthly_data");
	String name = (String) map.get("ReportName");
	List<String> Excel_Headers  = new ArrayList<String>();
	List<Object> monthly_Data = new ArrayList<Object>();
	List<String> Excel_Headers2  = new ArrayList<String>();
	List<Object> sum_Data = new ArrayList<Object>();
	List<String> Excel_Headers3  = new ArrayList<String>();
	List<Object> sum_Data2 = new ArrayList<Object>();
	List<String> Excel_Headers4  = new ArrayList<String>();
	List<Object> sum_Data3 = new ArrayList<Object>();
	List<String> Excel_Headers5  = new ArrayList<String>();
	List<Object> sum_Data4 = new ArrayList<Object>();
	List<String> Excel_Headers6  = new ArrayList<String>();
	List<Object> sum_Data5 = new ArrayList<Object>();
	List<String> Excel_Headers7  = new ArrayList<String>();
	List<Object> sum_Data6 = new ArrayList<Object>();
	List<String> Excel_Headers8  = new ArrayList<String>();
	List<Object> sum_Data7 = new ArrayList<Object>();
	List<String> Excel_Headers9  = new ArrayList<String>();
	List<Object> sum_Data8 = new ArrayList<Object>();


	String formattedString = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
	
	System.out.println("Got columns list");
	
	System.out.println("Got the data");
	String filename = "CTC_ACQ_RECON_REPORT";	
	if(name != null && !name.equalsIgnoreCase("")) {
		filename = name;
	}
	response.setContentType("application/vnd.ms-excel");
	response.setHeader("Content-disposition", "attachment; filename="+filename+".xls");
	OutputStream outStream = response.getOutputStream();
	SXSSFWorkbook wb = new SXSSFWorkbook();
	SXSSFWorkbook workbook = new SXSSFWorkbook();
	SXSSFSheet sheet = workbook.createSheet("NFS-ACQ-C2C-CBS-MATCHED-1");  
    Font font = workbook.createFont();
    font.setFontName("Arial");
    font.setColor(IndexedColors.BLACK.getIndex());
    CellStyle calculatedHeader = workbook.createCellStyle();
    calculatedHeader.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
    calculatedHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    Font calculatedfont = workbook.createFont();
    calculatedfont.setFontName("Arial");
    calculatedfont.setBold(true);
    calculatedHeader.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
    calculatedHeader.setFont(calculatedfont);
    CellStyle numberStyle = workbook.createCellStyle();
	numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
	// create header row
	SXSSFRow header = sheet.createRow(0);
    
	if(Data != null && Data.size()>0 ) {
		Excel_Headers = (List<String>) Data.get(0);
	}
	if(Data != null && Data.size() >1 ) {
		monthly_Data = (List<Object>) Data.get(1);
	}
	for(int i =0 ;i < Excel_Headers.size(); i++) {
		header.createCell(i).setCellValue(Excel_Headers.get(i));
		header.getCell(i).setCellStyle(calculatedHeader);
	}
	SXSSFRow rowEntry;
	//ADDING REMAINING DATA
	for(int i =0; i< monthly_Data.size() ; i++) {
		rowEntry = sheet.createRow(i+1);
		Map<String, String> map_data =  (Map<String, String>) monthly_Data.get(i);
		if(map_data.size()>0) {
			for(int m= 0 ;m < Excel_Headers.size() ; m++) {
				rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers.get(m)));
			}
		}
	}
	//workbook1.write(outStream);
	sheet = (SXSSFSheet) workbook.createSheet("NFS-ACQ-C2C-CBS-UNRECON-1");
	numberStyle = workbook.createCellStyle();
	numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
	// create header row
	 header = sheet.createRow(0);
	 if(Data!= null && Data.size() >2) {
		 Excel_Headers2 = (List<String>) Data.get(2);
	 }
	 if(Data!= null && Data.size() >3) {
		 sum_Data = (List<Object>) Data.get(3);
	 }
	for(int i =0 ;i < Excel_Headers2.size(); i++) {
		header.createCell(i).setCellValue(Excel_Headers2.get(i));
		header.getCell(i).setCellStyle(calculatedHeader);
	}
	//ADDING REMAINING DATA
	for(int i =0; i< sum_Data.size() ; i++) {
		rowEntry = sheet.createRow(i+1);
		Map<String, String> map_data =  (Map<String, String>) sum_Data.get(i);
		if(map_data.size()>0) {
			for(int m= 0 ;m < Excel_Headers2.size() ; m++) {
				rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers2.get(m)));
			}
		}
	}
	
	
	sheet = (SXSSFSheet) workbook.createSheet("NFS-ACQ-C2C-SWITCH-UNRECON-1");
	numberStyle = workbook.createCellStyle();
	numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
	// create header row
	 header = sheet.createRow(0);
	 if(Data!= null && Data.size() >2) {
		 Excel_Headers3 = (List<String>) Data.get(4);
	 }
	 if(Data!= null && Data.size() >3) {
		 sum_Data2 = (List<Object>) Data.get(5);
	 }
	for(int i =0 ;i < Excel_Headers3.size(); i++) {
		header.createCell(i).setCellValue(Excel_Headers3.get(i));
		header.getCell(i).setCellStyle(calculatedHeader);
	}
	//ADDING REMAINING DATA
	for(int i =0; i< sum_Data2.size() ; i++) {
		rowEntry = sheet.createRow(i+1);
		Map<String, String> map_data =  (Map<String, String>) sum_Data2.get(i);
		if(map_data.size()>0) {
			for(int m= 0 ;m < Excel_Headers3.size() ; m++) {
				rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers3.get(m)));
			}
		}
	}
//	
//	
//	
	sheet = (SXSSFSheet) workbook.createSheet("NFS-ACQ-C2C-MATCHED-2");
	numberStyle = workbook.createCellStyle();
	numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
	// create header row
	 header = sheet.createRow(0);
	 if(Data!= null && Data.size() >2) {
		 Excel_Headers4 = (List<String>) Data.get(6);
	 }
	 if(Data!= null && Data.size() >3) {
		 sum_Data3 = (List<Object>) Data.get(7);
	 }
	for(int i =0 ;i < Excel_Headers4.size(); i++) {
		header.createCell(i).setCellValue(Excel_Headers4.get(i));
		header.getCell(i).setCellStyle(calculatedHeader);
	}
	//ADDING REMAINING DATA
	for(int i =0; i< sum_Data3.size() ; i++) {
		rowEntry = sheet.createRow(i+1);
		Map<String, String> map_data =  (Map<String, String>) sum_Data3.get(i);
		if(map_data.size()>0) {
			for(int m= 0 ;m < Excel_Headers4.size() ; m++) {
				rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers4.get(m)));
			}
		}
	}
//
	sheet = (SXSSFSheet) workbook.createSheet("NFS-ACQ-C2C-CBS-UNRECON-2");
	numberStyle = workbook.createCellStyle();
	numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
	// create header row
	 header = sheet.createRow(0);
	 if(Data!= null && Data.size() >2) {
		 Excel_Headers5 = (List<String>) Data.get(8);
	 }
	 if(Data!= null && Data.size() >3) {
		 sum_Data4 = (List<Object>) Data.get(9);
	 }
	for(int i =0 ;i < Excel_Headers5.size(); i++) {
		header.createCell(i).setCellValue(Excel_Headers5.get(i));
		header.getCell(i).setCellStyle(calculatedHeader);
	}
	//ADDING REMAINING DATA
	for(int i =0; i< sum_Data4.size() ; i++) {
		rowEntry = sheet.createRow(i+1);
		Map<String, String> map_data =  (Map<String, String>) sum_Data4.get(i);
		if(map_data.size()>0) {
			for(int m= 0 ;m < Excel_Headers5.size() ; m++) {
				rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers5.get(m)));
			}
		}
	}
//	
	sheet = (SXSSFSheet) workbook.createSheet("NFS-ACQ-C2C-UNRECON-2");
	numberStyle = workbook.createCellStyle();
	numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
	// create header row
	 header = sheet.createRow(0);
	 if(Data!= null && Data.size() >2) {
		 Excel_Headers6 = (List<String>) Data.get(10);
	 }
	 if(Data!= null && Data.size() >3) {
		 sum_Data5 = (List<Object>) Data.get(11);
	 }
	for(int i =0 ;i < Excel_Headers6.size(); i++) {
		header.createCell(i).setCellValue(Excel_Headers6.get(i));
		header.getCell(i).setCellStyle(calculatedHeader);
	}
	//ADDING REMAINING DATA
	for(int i =0; i< sum_Data5.size() ; i++) {
		rowEntry = sheet.createRow(i+1);
		Map<String, String> map_data =  (Map<String, String>) sum_Data5.get(i);
		if(map_data.size()>0) {
			for(int m= 0 ;m < Excel_Headers6.size() ; m++) {
				rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers6.get(m)));
			}
		}
	}
//	
	
	workbook.write(outStream);
	outStream.close();
	response.getOutputStream().flush();
	}
}
