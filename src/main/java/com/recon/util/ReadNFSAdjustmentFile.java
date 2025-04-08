package com.recon.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.NFSSettlementBean;

public class ReadNFSAdjustmentFile {
	
	private static final Logger logger = Logger.getLogger(ReadNFSAdjustmentFile.class);
	SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");
	SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
	
    public HashMap<String, Object> fileupload(NFSSettlementBean beanObj,MultipartFile file,Connection con) throws SQLException {
           int response = 0;String tableName = null;
           int totalcount = 0;
           HashMap<String, Object> mapObj = new HashMap<>();
   		String getTableName = "SELECT TABLENAME FROM  main_filesource WHERE FILENAME = ? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?";
   		PreparedStatement pstmt = con.prepareStatement(getTableName);
   		pstmt.setString(1, beanObj.getFileName());
   		pstmt.setString(2, beanObj.getCategory());
   		pstmt.setString(3, beanObj.getStSubCategory());
   		ResultSet rs = pstmt.executeQuery();
   		String description = null;
   		double no_of_txns = 0.0D, debit = 0.0D, credit = 0.0D;
   		int count = 0;
   		String sql = "";
   		while (rs.next())
   			tableName = rs.getString("TABLENAME");
   		System.out.println(
   				"tableName " + tableName + "file name " + beanObj.getFileName() + " " + file.getOriginalFilename());
   		if (beanObj.getCreatedBy().equalsIgnoreCase("ICD")) {
   			sql = "INSERT INTO icd_adjustment_rawdata (Txnuid,TxnType,u_id,Adjdate,Adjtype,ACQ,ISR,Response,TxnDate,TxnTime,RRN,ATMID,CardNo,ChbDate,ChbRef,TxnAmount,AdjAmount,ACQFee,ISSFee,ISSFeeSW,NpciFee,AcqFeeTax,IssFeeTax,NpciTAX,AdjRef,BankAdjRef,AdjProof,ReasonDesc,Pincode,ATMLocation,MultiDisputeGroup,FCQM,AdjSettlementDate,CustomerPenalty,AdjTime,Cycle,TAT_Expiry_Date,AcqSTLAmount,AcqCC,Pan_Entry_Mode,Service_Code,Card_Dat_Input_Capability,MCC_Code ,CREATEDBY,FILEDATE,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
   		} else if (beanObj.getCreatedBy().equalsIgnoreCase("NTSL-DFS")) {
   			sql = "INSERT INTO dfs_adjustment_rawdata (Txnuid,TxnType,u_id,Adjdate,Adjtype,ACQ,ISR,Response,TxnDate,TxnTime,RRN,ATMID,CardNo,ChbDate,ChbRef,TxnAmount,AdjAmount,ACQFee,ISSFee,ISSFeeSW,NpciFee,AcqFeeTax,IssFeeTax,NpciTAX,AdjRef,BankAdjRef,AdjProof,ReasonDesc,Pincode,ATMLocation,MultiDisputeGroup,FCQM,AdjSettlementDate,CustomerPenalty,AdjTime,Cycle,TAT_Expiry_Date,AcqSTLAmount,AcqCC,Pan_Entry_Mode,Service_Code,Card_Dat_Input_Capability,MCC_Code ,CREATEDBY,FILEDATE,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
   		} else {
   			sql = "INSERT INTO nfs_adjustment_rawdata (Txnuid,TxnType,u_id,Adjdate,Adjtype,ACQ,ISR,Response,TxnDate,TxnTime,RRN,ATMID,CardNo,ChbDate,ChbRef,TxnAmount,AdjAmount,ACQFee,ISSFee,ISSFeeSW,NpciFee,AcqFeeTax,IssFeeTax,NpciTAX,AdjRef,BankAdjRef,AdjProof,ReasonDesc,Pincode,ATMLocation,MultiDisputeGroup,FCQM,AdjSettlementDate,CustomerPenalty,AdjTime,Cycle,TAT_Expiry_Date,AcqSTLAmount,AcqCC,Pan_Entry_Mode,Service_Code,Card_Dat_Input_Capability,MCC_Code,CREATEDBY,FILEDATE,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
   		}

          // Connection con = getConnection();
           try {
                  long start = System.currentTimeMillis();
                  System.out.println("start");
                  con.setAutoCommit(false);
                  PreparedStatement ps = con.prepareStatement(sql);
                  InputStream inputStream = file.getInputStream();
                 /* HSSFWorkbook wb = new HSSFWorkbook(inputStream);
                  HSSFSheet sheet = wb.getSheetAt(0);*/
                  FormulaEvaluator formulaEvaluate = null;
                  Workbook wb = null;
         			Sheet sheet = null;
                  int extn = file.getOriginalFilename().indexOf(".");
                  if (file.getOriginalFilename().substring(extn)
  						.equalsIgnoreCase(".XLS")) {
  			 
  				//	book = new HSSFWorkbook(file.getInputStream()) ;
  					wb = new HSSFWorkbook(file.getInputStream());
  					sheet = wb.getSheetAt(0);
  					formulaEvaluate = new HSSFFormulaEvaluator((HSSFWorkbook) wb);
  				
  				} else if (file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLSX")){
  					 
  					// book = new XSSFWorkbook(file.getInputStream());
  					wb = new XSSFWorkbook(file.getInputStream());
  					sheet = wb.getSheetAt(0);
  					formulaEvaluate = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
  				}
                  boolean isAdjDate = false;
                  List<Integer> DateCol = new ArrayList<Integer>();
                  DateCol.add(4);
                  DateCol.add(9);
                  DateCol.add(14);
                  DateCol.add(33);
                 // FormulaEvaluator formulaEvaluate = wb.getCreationHelper().createFormulaEvaluator();
    OUTER:   for (Row r : sheet) {
                      
                      //  logger.info("r.getRowNum() "+r.getRowNum());
                        if (r.getRowNum() > 0 ) {
                               int cellCount = 1;
                               for (Cell c : r) {
                            	   if(cellCount <44){
                                      switch(formulaEvaluate.evaluateInCell(c).getCellType())
                                      {
                                        
                                      case Cell.CELL_TYPE_STRING:
                                    	  //removing ' from data and checkig adjustment settlement date
                                    	  if(cellCount == 33)
                             			  {
                             				//  logger.info("Its AdSettlementDate Column "+c.getStringCellValue().substring(0,2));
                             				/*  if(!c.getStringCellValue().substring(0,2).equalsIgnoreCase(beanObj.getDatepicker().substring(0, 2)))
                             				  {
                             					  logger.info("Dates present in adjsettlement column is different");
                             					  mapObj.put("result", false);
                             	                  mapObj.put("count", -1);
                             	                  return mapObj;
                             				  }*/
                             			  }
                                    	//  System.out.println(c.getStringCellValue().replace("'", ""));
                                    	  ps.setString(cellCount, c.getStringCellValue().replace("'", ""));
                                    	  cellCount++;
                                    	  break;
                                      case Cell.CELL_TYPE_NUMERIC:
                                    	  if(DateCol.contains(cellCount))
                                    	  {
                                    		 // logger.info("Its Date");
                                			  String digit = c.getNumericCellValue() +"";
                                 			 // Date javaDate= DateUtil.getJavaDate(Double.parseDouble(digit));
                                 			//  System.out.println(javaDate);
                                 			 // System.out.println(new SimpleDateFormat("dd/MM/yyyy").format(javaDate));
                                 		//	  digit = new SimpleDateFormat("dd/MM/yyyy").format(javaDate)+"";
                                 			 // System.out.println("digit is"+digit);
                                 			  ps.setString(cellCount, digit);
                                 			  //checking whether adjSettlementDate is same as selected filedate
                                 			 /* if(cellCount == 33)
                                 			  {
                                 				  logger.info("Its AdSettlementDate Column "+digit.substring(0,2));
                                 				  if(digit.substring(0,2) != beanObj.getDatepicker().substring(0, 2))
                                 				  {
                                 					  logger.info("Dates present in adjsettlement column is different");
                                 					  mapObj.put("result", false);
                                 	                  mapObj.put("count", "AdjSettlementDate has different dates");
                                 	                  return mapObj;
                                 				  }
                                 			  }
                                			  */
                                		  }
                                    	  else
                                    	  {
                                    		  String digit = c.getNumericCellValue() +"";
                                    		  if(digit.contains("E"))
                                    		  {
                                    			  digit = c.getNumericCellValue() +"";
                                    			  double d = Double.parseDouble(digit);
                                    			//  System.out.println(digit);
                                    			  BigDecimal bd = new BigDecimal(d);
                                    			//  System.out.println("Bigdecimal is "+bd);
                                    			  /* digit = bd.round(new MathContext(15)).toPlainString();
                                           	   System.out.println(digit);
                                    			   */
                                    			  String tryDigit = bd+"";
                                    			  int indexOfDot = tryDigit.indexOf(".");
                                    			  int secondDigit = Integer.parseInt(tryDigit.substring(indexOfDot+1, indexOfDot+2));
                                    			//  System.out.println(secondDigit);
                                    			  if(secondDigit > 5)
                                    			  {
                                    				  digit = tryDigit.substring(0,indexOfDot+3);                                        	   
                                    			  }
                                    			  else
                                    			  {
                                    				  BigDecimal db = bd.setScale(1, RoundingMode.HALF_UP);
                                    				//  System.out.println("digit is " + db);
                                    				  digit = db.toPlainString();
                                    			  }

                                    		  }
                                    		 // System.out.println("digit "+digit);
                                    		  ps.setString(cellCount, digit);
                                    	  }
                                    	  cellCount++;
                                		  break;
                                		  
                                      }
                                    	  
                                    	  //ps.setString(cellCount, c.getStringCellValue());
                                      
                               }
                            	   
                               }
                               totalcount++;
                               ps.setString(cellCount++, "INT12016");
                             ///  logger.info("cellcount is "+cellCount);
                               ps.setString(cellCount, beanObj.getDatepicker()); 
                               ps.setString(46, file.getOriginalFilename()); 
                               
                               ps.addBatch();
                               
                        }
                  }
                  ps.executeBatch();
                  con.commit();
                  
                  long end = System.currentTimeMillis();
                  System.out.println("start and end diff" + (start - end));
                  System.out.println(" table  insert");
                  
                  //
        
                  
                 // response = ADDBACIDSTAT();
              /*   try
                 {
                	 //update card number with encrypted card number
                	 String cycle = file.getOriginalFilename();
                	 String[] fileDetails = cycle.split("\\_");
                	 cycle = fileDetails[1].substring(0,2);

                	 String updateQuery = "UPDATE NFS_ADJUSTMENT_RAWDATA A SET CARDNO = (select ibkl_encrypt_decrypt.ibkl_set_encrypt_val(b.cardno) enc from nfs_adjustment_rawdata B "+
                			 "where b.filedate = '"+beanObj.getDatepicker()+"'"// AND b.CYCLE = '1C' "
                			 + " and A.RRN = B.RRN and a.cardno = b.cardno and a.adjtype = b.adjtype) "
                			 + "WHERE a.filedate = '"+beanObj.getDatepicker()+"'";
                	 String updateQuery = "update nfs_adjustment_rawdata set cardno = ibkl_encrypt_decrypt.ibkl_set_encrypt_val(cardno) "+
                			 "where filedate = '"+beanObj.getDatepicker()+"'";
                	 //" a.CYCLE = '"+cycle+"'";
                	 logger.info("Update Query "+updateQuery);
                	 PreparedStatement update_pst = con.prepareStatement(updateQuery);
                	 update_pst.execute();
                 }
                 catch(Exception excep)
                 {
                	 logger.info("Exception while encrypting card numbers "+ excep);
                 }*/
                  con.close();
                  System.out.println("Data Inserted");
                  mapObj.put("result", true);
                  mapObj.put("count", totalcount);
           } catch (Exception e) {
                  e.printStackTrace();
                  mapObj.put("result", false);
                  mapObj.put("count", totalcount);
                  try {
                        con.rollback();
                  } catch (SQLException ex) {
                        ex.printStackTrace();
                  }
           }
           return mapObj;
    }



}
