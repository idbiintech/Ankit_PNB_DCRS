package com.recon.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.recon.dao.TTUMDataUploadDao;
import com.recon.model.UploadTTUMBean;
import com.recon.util.demo;

import oracle.jdbc.OracleTypes;


@Component
public class TTUMDataUploadDaoImpl extends JdbcDaoSupport implements TTUMDataUploadDao{

	@Override
	public String readFile(UploadTTUMBean UploadBean, MultipartFile file) throws Exception {
		logger.info("***** TTUMDataUploadDaoImpl.readFile Start ****");
		String msg="";

		logger.info("readFile");
		try
		{
			int count =0;
			HSSFWorkbook wb  = new HSSFWorkbook(file.getInputStream());
			//Sheet sheet = book.getSheetAt(0);
			/*Iterator<Row> rowIterator = sheet.iterator();*/
			
	         HSSFSheet sheet = wb.getSheetAt(0);
			 Row row = (Row)sheet.getRow(1);
	         int noOfColumns = sheet.getRow(3).getLastCellNum();
			List<List<String>> Data = new ArrayList<>();
			List<String> header = new ArrayList();
			
	         
			
			//while loop for taking first row of EXCEL containing the headers
			//int loop_count=1;
			//int loop = 0;
			
			for(int i =1;i<= sheet.getLastRowNum();i++) {
	        	 
	        	 row = (Row)sheet.getRow(i);
	        	if(row == null){
	        		break;
	        	}
	        	 
	        	 logger.info(row.getCell(2).getStringCellValue());
	        	 final SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
	        	 Object trace = 0 ;
	        	 
	        	 
	        	 String pan = row.getCell(0).getStringCellValue().replace("'", "");
	        	 
	        	 if (row.getCell(1).getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC) {
						
	        		 trace = row.getCell(1).getNumericCellValue();
					} 
					else 
					 if(row.getCell(1).getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING) {
						 
						 trace =row.getCell(1).getStringCellValue();
					 }
	        	 
	        	 
    			 
    			 String localdate  = row.getCell(2).getStringCellValue().replace("'", "");
	        	 
	        	
    			
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
						try {
						    Date varDate=dateFormat.parse(localdate.toString());
						    dateFormat=new SimpleDateFormat("MM/dd/yyyy");
						    logger.info("Date :"+dateFormat.format(varDate));
						   localdate = dateFormat.format(varDate);
						}catch (Exception e) {
						    // TODO: handle exception
						    e.printStackTrace();
						}
						 
						 Object amount_equiv = 0 ;
						 
						 if(row.getCell(3).getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING){
							
							 amount_equiv = row.getCell(3).getStringCellValue();
						}else if(row.getCell(3).getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC){
							
							 amount_equiv = row.getCell(3).getNumericCellValue();
						}
						
		 
		    
		    
		   
	        	String sql = "insert into TEMP_UPLOAD_TTUMDATA  (pan,trace,local_date,amount_equiv)"
	        			+ " values('"+pan+"','"+trace+"','"+localdate+"','"+amount_equiv+"')";
	        	
	        	  getJdbcTemplate().execute(sql);
	        	 
					 
				}
			
//			Call sp DISPUTE_TTUM_GENRATION
			
			
			String response = null;
			Map<String, Object> inParams = new HashMap<String, Object>();
System.out.println("CALL PROC DISPUTE_TTUM_GENRATION("+UploadBean.getStDate()+", "+UploadBean.getStCategory()+", "+UploadBean.getStMergerCategory()+" , " + UploadBean.getEntry_by()+" ) ");
			inParams.put("I_TRAN_DATE",UploadBean.getStDate());
			inParams.put("I_CATEGORY", UploadBean.getStCategory());
			inParams.put("I_MERGER_CATEGORY",UploadBean.getStMergerCategory() );
			inParams.put("I_ENTRY_BY", UploadBean.getEntry_by());
			
			
			DisputeGeneration acqclassificaton = new DisputeGeneration(getJdbcTemplate());
			Map<String, Object> outParams = acqclassificaton.execute(inParams);
			
			if (outParams.get("ERROR_MESSAGE") != null) {
				System.out.println(outParams.get("ERROR_MESSAGE") .toString());
						msg = (String) outParams.get("ERROR_MESSAGE") ;
					}else {
						System.out.println("Success");
						msg= "Success";
					}
			
			logger.info("***** TTUMDataUploadDaoImpl.readFile End ****");
			return msg	    ;	
		}
				
			
		
		catch(Exception e)
		{
			demo.logSQLException(e, "TTUMDataUploadDaoImpl.readFile");
			e.printStackTrace();
			logger.error(" error in TTUMDataUploadDaoImpl.readFile", new Exception("TTUMDataUploadDaoImpl.readFile",e));
			return "Exception Occured";
		}
		
	
	}
	
	
	
	class DisputeGeneration extends StoredProcedure {
		private static final String procName = "DISPUTE_TTUM_GENRATION";

		DisputeGeneration(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);
			
			
			declareParameter(new SqlParameter("I_TRAN_DATE",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_MERGER_CATEGORY",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",	OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE", OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE", OracleTypes.VARCHAR));
			compile();
		}
	}
	
	
/*	@Override
	public String readFile(UploadTTUMBean UploadBean,MultipartFile file)
	{
		logger.info("readFile");
		try
		{
			Workbook book = new HSSFWorkbook(file.getInputStream());
			Sheet sheet = book.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			List<String> row = new ArrayList<>();
			List<List<String>> Data = new ArrayList<>();
			List<String> header = new ArrayList();
			
			//while loop for taking first row of EXCEL containing the headers
			//int loop_count=1;
			//int loop = 0;
			for(int loop = 1; loop<=3; loop++){
				Iterator<Row> rowIterator = sheet.iterator();
				header.clear();
				if(loop == 2 || loop == 3){				
					rowIterator = sheet.iterator();
					}
			while (rowIterator.hasNext()) {
				//	NPSFileUploadBean bean = new NPSFileUploadBean();
				
					Row currentRow = rowIterator.next();
					Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = currentRow
							.iterator();
					while (cellIterator.hasNext()) {
						

						org.apache.poi.ss.usermodel.Cell currentCell = cellIterator
								.next();
						if (currentCell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC) {
							header.add(currentCell.getNumericCellValue());
						} 
						else 
						 if(currentCell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING) {
							 if(loop==1)
							 {
						    if(currentCell.getStringCellValue().equalsIgnoreCase("pan"))
						    {
							header.add("remarks");
						    }
						    else if(currentCell.getStringCellValue().equalsIgnoreCase("trace"))
						    {
							header.add("substr(ref_no,2,6)");
						    }
						    if(currentCell.getStringCellValue().equalsIgnoreCase("local_date"))
						    {
							header.add("TO_CHAR(to_date(tran_date ,'dd-mm-yyyy'),'dd-mm-yyyy')");
						    }
						    if(currentCell.getStringCellValue().equalsIgnoreCase("amount_equiv"))
						    {
							header.add("trunc(TO_NUMBER(REPLACE(AMOUNT,',','')))");
						    }}
						     if(loop==2 || loop==3){
						    	header.add(currentCell.getStringCellValue());
								//header.add(currentCell.getDateCellValue());
							
							 }
							 
						} 
					}
					break;
				}
				
			
			//while loop for fetching actual data
			while (rowIterator.hasNext()) {
				//	NPSFileUploadBean bean = new NPSFileUploadBean();
				List<String> FileData = new ArrayList<>();
				Row currentRow = rowIterator.next();
				Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = currentRow.iterator();
				int count=1;
				while (cellIterator.hasNext()) {

					
					org.apache.poi.ss.usermodel.Cell currentCell = cellIterator.next();
					if(count==3)
					{
						logger.info(currentCell.toString());
						String strDate=currentCell.toString();
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
						try {
						    java.util.Date varDate=dateFormat.parse(currentCell.toString());
						    dateFormat=new SimpleDateFormat("MM/dd/yyyy");
						    logger.info("Date :"+dateFormat.format(varDate));
						    FileData.add(dateFormat.format(varDate));
						}catch (Exception e) {
						    // TODO: handle exception
						    e.printStackTrace();
						}
						
					}
					
					currentCell.setCellType(Cell.CELL_TYPE_STRING);
					
					currentCell.setCellType(Cell.CELL_TYPE_NUMERIC);

					SimpleDateFormat datetemp = new SimpleDateFormat("yyyy-MM-dd");
					Date cellValue = datetemp.parse("1994-01-01 12:00");
					cell.setCellValue(cellValue);
					if (currentCell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC) 
					{
						//FileData.add(""+currentCell.getNumericCellValue());
						header.add(currentCell.getNumericCellValue());
						logger.info((currentCell.getNumericCellValue()));
					} 
					else if(currentCell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING)
					{
						if(count!=3)
						{
						FileData.add(currentCell.getStringCellValue());
						logger.info(currentCell.getStringCellValue());
						count++;
						}
						else{
						count++;
						}
					}
					else if (currentCell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING) {
						//header.add(currentCell.getStringCellValue());
						logger.info(currentCell.getStringCellValue());
					} else {
						//header.add(currentCell.getDateCellValue());
						logger.info(currentCell.getDateCellValue());
					}
					//Data.add(FileData);
				}
				count = 1;
				Data.add(FileData);
				//break;
			}
			String UPDATE_QUERY = "UPDATE SETTLEMENT_"+UploadBean.getStCategory()+"_"+UploadBean.getStSelectedFile()+" SET DCRS_REMARKS = '"+UploadBean.getStMergerCategory()+"-UNRECON-2"
					+"' WHERE TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DD/MM/YYYY') = '"+UploadBean.getStDate()+"'" ;
					
					for(String columns : header)
					{
						UPDATE_QUERY = UPDATE_QUERY+" AND "+columns +" = ?";
					}
					logger.info("UPADTE QUERY IS "+UPDATE_QUERY);
			ArrayList<String> Update_queries = new ArrayList<>();
			int count1=0;
			String value = "";
					for(List<String> TTUMdata : Data)
					{
						//loop++;
						String UPDATE_QUERY = "";
						//if(UploadBean.getStSelectedFile().equalsIgnoreCase("SWITCH"))
						if(loop == 2)
						{		
							value="SWITCH";
							int chkExistCnt = getJdbcTemplate().queryForObject(EXIST_CBS_TTUM, new Object[] {UploadBean.getStDate(),TTUMdata.get(0),TTUMdata.get(1),TTUMdata.get(2),TTUMdata.get(3) },Integer.class);
							
							if(chkExistCnt == 0){
							UPDATE_QUERY = "UPDATE SETTLEMENT_"+UploadBean.getStCategory()+"_"+value+" SET DCRS_REMARKS = '"+UploadBean.getStMergerCategory()
									+"-UNRECON-GENERATE-TTUM-2' WHERE DCRS_REMARKS like '%"+UploadBean.getStMergerCategory()+"-UNRECON-"+loop
									+"%' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+UploadBean.getStDate()+"'" ;
							}
						}
						else if(loop == 1)
						{
							value="CBS";
							UPDATE_QUERY = "UPDATE SETTLEMENT_"+UploadBean.getStCategory()+"_"+value+" SET DCRS_REMARKS = '"+UploadBean.getStMergerCategory()
									+"-UNRECON-GENERATE-TTUM-1' WHERE DCRS_REMARKS like '%"+UploadBean.getStMergerCategory()+"-UNRECON-"+loop+"%' " +
									"AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+UploadBean.getStDate()+"'" ;							
						}
						
						else if(loop == 3){
							int chkExistCBS = getJdbcTemplate().queryForObject(EXIST_CBS_TTUM, new Object[] {UploadBean.getStDate(),TTUMdata.get(0),TTUMdata.get(1),TTUMdata.get(2),TTUMdata.get(3) },Integer.class);
							if(chkExistCBS == 0){
								int chkExistSwitch = getJdbcTemplate().queryForObject(EXIST_SWITCH_TTUM, new Object[] {UploadBean.getStDate(),TTUMdata.get(0),TTUMdata.get(1),TTUMdata.get(2),TTUMdata.get(3) },Integer.class);
									if(chkExistSwitch == 0){
										String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = UPPER('TEMP_"+UploadBean.getStCategory()
												+"_DISPUTE')";
										int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
										
										if(tableExist == 0)
										{
											String CREATE_QUERY = "CREATE TABLE TEMP_"+UploadBean.getStCategory()+"_DISPUTE"+" "
													+ "(pan  VARCHAR (100 BYTE),trace VARCHAR (100 BYTE),local_date VARCHAR (100 BYTE),amount_equiv  VARCHAR (100 BYTE),filedate VARCHAR (100BYTE))";
											
											getJdbcTemplate().execute(CREATE_QUERY);
										}
										
										String INSERT_QUERY = "INSERT INTO TEMP_"+UploadBean.getStCategory()+"_DISPUTE (PAN,TRACE,LOCAL_DATE,AMOUNT_EQUIV,FILEDATE) "
												+ "VALUES('"+TTUMdata.get(0)+"','"+TTUMdata.get(1)+"','"+TTUMdata.get(2)+"','"+TTUMdata.get(3)+"','"+UploadBean.getStDate()+"')";
										
										getJdbcTemplate().execute(INSERT_QUERY);
									}
								
							}
						}
						
						
						int count = 0;
						if(UPDATE_QUERY != ""){
						for(String columns : header)
						{   
							if(columns.equalsIgnoreCase("substr(ref_no,2,6)"))
							{	
								UPDATE_QUERY = UPDATE_QUERY+" AND "+columns +" = substr('"+TTUMdata.get(count)+"',2,6)";
								count++;
							
							}
							else if(columns.equalsIgnoreCase("TO_CHAR(to_date(tran_date ,'dd-mm-yyyy'),'dd-mm-yyyy')"))
							{
								UPDATE_QUERY = UPDATE_QUERY+" AND "+columns +" = TO_CHAR(to_date('"+TTUMdata.get(count)+"','mm/dd/yyyy'),'dd-mm-yyyy')";
								count++;
							}
							else if(columns.equalsIgnoreCase("trunc(TO_NUMBER(REPLACE(amount,',','')))"))
							{
								UPDATE_QUERY = UPDATE_QUERY+" AND "+columns +" = trunc(TO_NUMBER(REPLACE('"+TTUMdata.get(count)+"',',','')))";
								count++;
							}
							else{
								if(columns.equalsIgnoreCase("amount_equiv"))
								{
									UPDATE_QUERY = UPDATE_QUERY+" AND "+columns +" = trunc('"+TTUMdata.get(count)+"')";
									count++;
								}else{
								UPDATE_QUERY = UPDATE_QUERY+" AND "+columns +" = '"+TTUMdata.get(count)+"'";
								count++;
								}
							}
							
						}
						
						}
						//header.clear();
						logger.info("UPDaTE QUERY IS "+UPDATE_QUERY);
						getJdbcTemplate().execute(UPDATE_QUERY);
						
					}
						if(UPDATE_QUERY != ""){
						Update_queries.add(UPDATE_QUERY);
						}
						if(count1 == 10)
						{
							String[] query = new String[Update_queries.size()]; 
							query = Update_queries.toArray(query);
							getJdbcTemplate().batchUpdate(query);
							Update_queries.clear();
							count1 = 1;

						}
						count1++;
					}
					if(Update_queries.size()!=0){
					String[] query = new String[Update_queries.size()]; 
				    query = Update_queries.toArray(query);
					getJdbcTemplate().batchUpdate(query);
					Update_queries.clear();
					}
					
					Data.clear();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.info("Exception in TTUMDataUploadDaoImpl "+e);
			return "Exception Occured";
		}
		return "Success";
	}*/


}
