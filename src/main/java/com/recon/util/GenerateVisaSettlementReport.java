package com.recon.util;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.servlet.view.document.AbstractExcelView;


@SuppressWarnings("unchecked")

public class GenerateVisaSettlementReport extends AbstractExcelView {
	
	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("Inside Daily Excel Download");
		List<Object> Data = (List<Object>) map.get("data");
		String name = (String) map.get("ReportName");
		List<Object> monthly_Data = new ArrayList<Object>();
		List<String> Excel_Headers = new ArrayList<String>();
		if(Data!= null && Data.size()>0)
		{
			Excel_Headers = (List<String>) Data.get(0);
		}
		
		System.out.println("Got columns list");
		if(Data!= null &&  Data.size()>1)
		{
			monthly_Data = (List<Object>) Data.get(1);
		}
		
		
      
        
		System.out.println("Got the data");
		String filename = "VISA-SETTLEMENT_REPORT";
		if(name != null && !name.equalsIgnoreCase(""))
		{
			filename = name;
		}
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename="+filename+".xls");
		
		OutputStream outStream = response.getOutputStream();
		
        HSSFSheet sheet = workbook.createSheet("Report");  
    
    
    //CellStyle style = workbook.createCellStyle();
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
	HSSFRow header = sheet.createRow(0);
	
	for(int i =0 ;i < Excel_Headers.size(); i++)
	{
		header.createCell(i).setCellValue(Excel_Headers.get(i));
		header.getCell(i).setCellStyle(calculatedHeader);
	}
	HSSFRow rowEntry;
	//ADDING REMAINING DATA
	for(int i =0; i< monthly_Data.size() ; i++)
	{
		rowEntry = sheet.createRow(i+1);
		Map<String, String> map_data =  (Map<String, String>) monthly_Data.get(i);
		if(map_data.size()>0)
		{

			for(int m= 0 ;m < Excel_Headers.size() ; m++)
			{
				//for making font bold
					rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers.get(m)));
			}
		}

	}
	
	workbook.write(outStream);
	outStream.close();
	
    
	response.getOutputStream().flush();
	
	}
	

}
