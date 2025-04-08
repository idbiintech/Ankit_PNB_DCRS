package com.recon.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.recon.model.MastercardTTUMBean;
import com.recon.service.MastercardTTUMService;
@Component
public class MastercardTTUMServiceImpl extends JdbcDaoSupport implements MastercardTTUMService
{
	private static final Logger logger = Logger.getLogger(MastercardTTUMServiceImpl.class);
	@Override
	public boolean checkAndMakeDirectory(MastercardTTUMBean beanObj) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			java.util.Date date = sdf.parse(beanObj.getFileDate());

			sdf = new SimpleDateFormat("dd-MM-yyyy");

			String stnewDate = sdf.format(date);

			// 1. Delete folder
			logger.info("Path is " + beanObj.getStPath() + File.separator + beanObj.getCategory());
			File checkFile = new File(beanObj.getStPath() + File.separator + beanObj.getCategory());
			if (checkFile.exists())
				FileUtils.forceDelete(new File(beanObj.getStPath() + File.separator + beanObj.getCategory()));

			// 2. check whether category folder is there or not
			File directory = new File(beanObj.getStPath() + File.separator + beanObj.getCategory());
			if (!directory.exists()) {
				directory.mkdir();
			}
			directory = new File(
					beanObj.getStPath() + File.separator + beanObj.getCategory() + File.separator + stnewDate);

			if (!directory.exists()) {
				directory.mkdir();
			}

			beanObj.setStPath(
					beanObj.getStPath() + File.separator + beanObj.getCategory() + File.separator + stnewDate);
 
			return true;
		} catch (Exception e) {
			logger.info("Exception in checkAndMakeDirectory " + e);
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public List<Object> getMastercardTTUMData(MastercardTTUMBean beanObj) {
		logger.info("***** MastercardTTUMServiceImpl.getMastercardTTUMData Start ****");
		String GET_DATA=null;
		List<Object> data = new ArrayList<Object>();
		 
		try
		{
		 
			 if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD") && beanObj.getStSubCategory().equalsIgnoreCase("ISSUER") 
					  && beanObj.getInRec_Set_Id().equalsIgnoreCase("SURCHARGE")){
				 
				/*  GET_DATA ="select * from ( select '32' AS SNO1,'135'AS SNO2,'491' AS SNO3 ,'1' AS DRCR,'24' AS ACCTYPE, '30045200655' AS ACCTNO,'32' AS BRCODE," 
							+" 'MDS-MDS-Dom Pos-DEBITS-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'ddmmyy') AS perticulers,TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99))- TRIM(B.AMOUNT) as surchargAmt"  
							+" from "
							+" (select * from MASTERCARD_POS_RAWDATA where  CURRENCY_CODE_TRAN=356 and   CURRENCY_CODE_RECON=356 AND PROCESSING_CODE='000000' AND "
							+" to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') )  A, "
							+" (select * from switch_rawdata  WHERE  TXNSRC='MAS' AND  TXNDEST='HOS' AND "
							+" to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')"
							+" and respcode='00') B where A.PAN= B.PAN AND TRIM(B.ISSUER)=TRIM(A.RETRV_REF_NO)"
							+" AND A.APPROVAL_CODE=B.AUTHNUM) where surchargAmt >0  "
							+" UNION ALL  "
							+" select * from ( select '32' AS SNO1,'135'AS SNO2,'491' AS SNO3 ,'2' AS DRCR,'N/A' AS ACCTYPE, "
							+" 'N/A' AS ACCTNO,'N/A' AS BRCODE,A.APPROVAL_CODE||'-'||B.NETWORK||'-'|| REPLACE(substr(A.CARD_ACCP_NAME_LOC,"
							+" 1,instr(A.CARD_ACCP_NAME_LOC || ',',',')-1),'-','')||'-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'ddmmyy') AS perticulers ,"
							+" TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99))- TRIM(B.AMOUNT) as surchargAmt  from"
							+" (select * from MASTERCARD_POS_RAWDATA where  CURRENCY_CODE_TRAN=356 and   CURRENCY_CODE_RECON=356 AND PROCESSING_CODE='000000' AND "
							+" to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')) A, "
							+" (select * from switch_rawdata  WHERE  TXNSRC='MAS' AND "
							+" TXNDEST='HOS' AND to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') "
							+" and respcode='00') B where A.PAN= B.PAN AND "
							+" TRIM(B.ISSUER)=TRIM(A.RETRV_REF_NO) AND A.APPROVAL_CODE=B.AUTHNUM) where surchargAmt >0";*/
				 
				 /* GET_DATA ="select * from ( select '32' AS SNO1,'135'AS SNO2,'491' AS SNO3 ,'1' AS DRCR,'24' AS ACCTYPE, '30045200655' AS ACCTNO,'32' AS BRCODE," 
							+" 'MDS-MDS-Dom Pos-DEBITS-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'ddmmyy') AS perticulers,TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99))- TRIM(B.AMOUNT) as surchargAmt"  
							+" from "
							+" (select * from MASTERCARD_POS_RAWDATA where  CURRENCY_CODE_TRAN=356 and   CURRENCY_CODE_RECON=356 AND PROCESSING_CODE='000000' AND "
							+" to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') )  A, "
							+" (select * from switch_rawdata  WHERE  TXNSRC='MAS' AND  TXNDEST='HOS' AND "
							+" to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')"
							+" and respcode='00') B where A.PAN= B.PAN AND TRIM(B.ISSUER)=TRIM(A.RETRV_REF_NO)"
							+" AND A.APPROVAL_CODE=B.AUTHNUM) where surchargAmt >0  "
							+" UNION ALL  "
							+" select * from ( select '32' AS SNO1,'135'AS SNO2,'491' AS SNO3 ,'2' AS DRCR,'01' AS ACCTYPE, "
							+" B.ACCTNUM AS ACCTNO,B.BRANCHCODE AS BRCODE,A.APPROVAL_CODE||'-'||B.NETWORK||'-'|| REPLACE(substr(A.CARD_ACCP_NAME_LOC,"
							+" 1,instr(A.CARD_ACCP_NAME_LOC || ',',',')-1),'-','')||'-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'ddmmyy') AS perticulers ,"
							+" TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99))- TRIM(B.AMOUNT) as surchargAmt  from"
							+" (select * from MASTERCARD_POS_RAWDATA where  CURRENCY_CODE_TRAN=356 and   CURRENCY_CODE_RECON=356 AND PROCESSING_CODE='000000' AND "
							+" to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')) A, "
							+" (select * from switch_rawdata  WHERE  TXNSRC='MAS' AND "
							+" TXNDEST='HOS' AND to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') "
							+" and respcode='00') B where A.PAN= B.PAN AND "
							+" TRIM(B.ISSUER)=TRIM(A.RETRV_REF_NO) AND A.APPROVAL_CODE=B.AUTHNUM) where surchargAmt >0";*/
				 GET_DATA ="select * from ( select '32' AS SNO1,'135'AS SNO2,'491' AS SNO3 ,'1' AS DRCR,'24' AS ACCTYPE, '30045200655' AS ACCTNO,'32' AS BRCODE," 
							+" 'MDS-MDS-Dom Pos-DEBITS-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'ddmmyy') AS perticulers,TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99))- TRIM(B.AMOUNT) as surchargAmt"  
							+" from "
							+" (select * from MASTERCARD_POS_RAWDATA where  CURRENCY_CODE_TRAN=356 and   CURRENCY_CODE_RECON=356 AND PROCESSING_CODE='000000' AND "
							+" to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') )  A, "
							+" (select * from switch_rawdata  WHERE  TXNSRC='MAS' AND  TXNDEST='HOS' AND "
							+" to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')"
							+" and respcode='00') B where A.PAN= B.PAN "
							+" AND A.APPROVAL_CODE=B.AUTHNUM) where surchargAmt >0  "
							+" UNION ALL  "
							+" select * from ( select '32' AS SNO1,'135'AS SNO2,'491' AS SNO3 ,'2' AS DRCR,'01' AS ACCTYPE, "
							+" B.ACCTNUM AS ACCTNO,B.BRANCHCODE AS BRCODE,A.APPROVAL_CODE||'-'||B.NETWORK||'-'|| REPLACE(substr(A.CARD_ACCP_NAME_LOC,"
							+" 1,instr(A.CARD_ACCP_NAME_LOC || ',',',')-1),'-','')||'-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'ddmmyy') AS perticulers ,"
							+" TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99))- TRIM(B.AMOUNT) as surchargAmt  from"
							+" (select * from MASTERCARD_POS_RAWDATA where  CURRENCY_CODE_TRAN=356 and   CURRENCY_CODE_RECON=356 AND PROCESSING_CODE='000000' AND "
							+" to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')) A, "
							+" (select * from switch_rawdata  WHERE  TXNSRC='MAS' AND "
							+" TXNDEST='HOS' AND to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') "
							+" and respcode='00') B where A.PAN= B.PAN  "
							+"  AND A.APPROVAL_CODE=B.AUTHNUM) where surchargAmt >0";
					// remove TRIM(B.ISSUER)=TRIM(A.RETRV_REF_NO) in query 
					logger.info(GET_DATA);
				
					List<Object> ttumData = getJdbcTemplate().query(GET_DATA, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										
										table_Data.put("SNO1",rs.getString("SNO1"));
										table_Data.put("SNO2",rs.getString("SNO2"));
										table_Data.put("SNO3",rs.getString("SNO3"));
										table_Data.put("DRCR",rs.getString("DRCR"));
										table_Data.put("ACCTYPE",rs.getString("ACCTYPE"));
										table_Data.put("ACCTNO",rs.getString("ACCTNO"));
										table_Data.put("BRCODE",rs.getString("BRCODE"));
										table_Data.put("PERTICULERS",rs.getString("PERTICULERS"));
										table_Data.put("surchargAmt",rs.getString("surchargAmt"));
										
										beanList.add(table_Data);
									}
									return beanList;
								}
							});
				 
					data.add(ttumData);
			}else if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD") && beanObj.getStSubCategory().equalsIgnoreCase("ISSUER") 
						  && beanObj.getInRec_Set_Id().equalsIgnoreCase("DECLINED")){
				

				String getdomData = null;
				/*getdomData = "select 'N/A' AS ACC_TYPE,'N/A' AS ACC_NO,'1' AS DRCR, AMOUNT,NETWORK||' '|| 'CHB DECLINED DR REV-'||''||ISSUER||'-'||REPLACE(FILEDATE,'-','') AS perticulers    "
							+ " from switch_rawdata WHERE   RESPCODE='91' AND TXNSRC='MAS' AND TXNDEST IN ('MAS','HOS') AND FILEDATE=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') "
							+ " UNION ALL "
							+ "  select 'N/A' AS ACC_TYPE,'N/A' AS ACC_NO,'2' AS DRCR, AMOUNT,NETWORK||' '|| 'CHB DECLINED DR REV-'||''||ISSUER||'-'||REPLACE(FILEDATE,'-','') AS perticulers   "
							+ " from switch_rawdata WHERE   RESPCODE='91' AND TXNSRC='MAS' AND TXNDEST IN ('MAS','HOS') AND FILEDATE=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')" ;
				*/
				
			/*	getdomData = "SELECT * FROM (" 
						+ " select distinct 'N/A' AS ACC_TYPE,'N/A' AS ACC_NO,'1' AS DRCR, AMOUNT_EQUIV  AS AMOUNT,NETWORK||' '|| 'CHB DECLINED DR REV-'||''||ISSUER||'-'||REPLACE(A.FILEDATE,'-','') AS perticulers" 
						+ " from "
						+ " (select * from switch_rawdata WHERE   RESPCODE<>'00' AND TXNSRC='MAS' AND TXNDEST IN ('MAS','HOS') AND "
						+ " to_char(to_date(FILEDATE,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy'))  A, "
						+ " (select * from cbs_rupay_rawdata WHERE   E='D'  AND "
						+ " to_char(to_date(FILEDATE,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy'))  B"
						+ " where A.ISSUER= B.REF_NO AND TRIM(A.PAN)=TRIM(B.REMARKS))"  
						+ " UNION ALL"
						+ " SELECT * FROM ( "
						+ " select distinct 'N/A' AS ACC_TYPE,'N/A' AS ACC_NO,'2' AS DRCR, AMOUNT_EQUIV  AS AMOUNT,NETWORK||' '|| 'CHB DECLINED DR REV-'||''||ISSUER||'-'||REPLACE(A.FILEDATE,'-','') AS perticulers" 
						+ " from "
						+ " (select * from switch_rawdata WHERE   RESPCODE<>'00' AND TXNSRC='MAS' AND TXNDEST IN ('MAS','HOS') AND "
						+ " to_char(to_date(FILEDATE,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy'))  A, "
						+ "(select * from cbs_rupay_rawdata WHERE   E='D'  AND "
						+ " to_char(to_date(FILEDATE,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy'))  B"
						+ " where A.ISSUER= B.REF_NO AND TRIM(A.PAN)=TRIM(B.REMARKS))";*/
				/*getdomData = "select distinct 'N/A' AS ACC_TYPE,'N/A' AS ACC_NO,'1' AS DRCR, AMOUNT_EQUIV  AS AMOUNT,"
							+" NETWORK||' '|| 'CHB DECLINED DR REV-'||''||ISSUER||'-'||REPLACE(FILEDATE,'-','') AS perticulers"
							+" from MASTERCARD_ISS_CBS_SWITCH_MATCHED_DECLINED"
							+" where to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')"
							+" UNION ALL"
							+" select distinct 'N/A' AS ACC_TYPE,'N/A' AS ACC_NO,'2' AS DRCR, AMOUNT_EQUIV  AS AMOUNT,"
							+" NETWORK||' '|| 'CHB DECLINED DR REV-'||''||ISSUER||'-'||REPLACE(FILEDATE,'-','') AS perticulers" 
							+" from MASTERCARD_ISS_CBS_SWITCH_MATCHED_DECLINED "
							+" where to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')";
				//22-01-2024
				*/
				/*getdomData = "select distinct 'N/A' AS ACC_TYPE,'N/A' AS ACC_NO,'1' AS DRCR, AMOUNT_EQUIV  AS AMOUNT,"
						+"  NETWORK||'-'||AUTHNUM ||'-'||'MDS DECLINED DR REV-'||''||ISSUER||'-'||REPLACE(FILEDATE,'-','') AS perticulers"
						+" from MASTERCARD_ISS_CBS_SWITCH_MATCHED_DECLINED"
						+" where to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')"
						+" UNION ALL"
						+"  select   '24' AS ACC_TYPE,'30045100655' AS ACC_NO,'2' AS DRCR, AMOUNT_EQUIV  AS AMOUNT,"
						+"  NETWORK||'-'||AUTHNUM ||'-'||'MDS DECLINED DR REV-'||''||ISSUER||'-'||REPLACE(FILEDATE,'-','') AS perticulers " 
						+" from MASTERCARD_ISS_CBS_SWITCH_MATCHED_DECLINED "
						+" where to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')";
			*/
				getdomData = "select distinct '01' AS ACC_TYPE,ACCTNUM AS ACC_NO,'1' AS DRCR, AMOUNT_EQUIV  AS AMOUNT,"
						+"  NETWORK||'-'||AUTHNUM ||'-'||'MDS DECLINED DR REV-'||''||ISSUER||'-'||REPLACE(FILEDATE,'-','') AS perticulers"
						+" from MASTERCARD_ISS_CBS_SWITCH_MATCHED_DECLINED"
						+" where to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')"
						+" UNION ALL"
						+"  select   '24' AS ACC_TYPE,'30045100655' AS ACC_NO,'2' AS DRCR, AMOUNT_EQUIV  AS AMOUNT,"
						+"  NETWORK||'-'||AUTHNUM ||'-'||'MDS DECLINED DR REV-'||''||ISSUER||'-'||REPLACE(FILEDATE,'-','') AS perticulers " 
						+" from MASTERCARD_ISS_CBS_SWITCH_MATCHED_DECLINED "
						+" where to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')";
			
				logger.info("Getdata query is " + getdomData);
			
				List<Object> DailyData = getJdbcTemplate().query(getdomData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("ACC_TYPE", rs.getString("ACC_TYPE"));
									table_Data.put("ACC_NO", rs.getString("ACC_NO"));
									table_Data.put("DRCR", rs.getString("DRCR"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("perticulers", rs.getString("perticulers"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
			}else if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD") && beanObj.getStSubCategory().equalsIgnoreCase("ISSUER") 
					  && beanObj.getInRec_Set_Id().equalsIgnoreCase("REFUND")){

				String getdomData = null;
				/*getdomData = "select '01' AS ACCTYPE,'N/A' AS ACCOUNTNO,'1' AS DRCR, TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99)) AS REFUND , "
							+" A.DATE_VAL || '-MDS Refund'||'-'||TRIM(REPLACE(substr(A.CARD_ACCP_NAME_LOC,1,instr(A.CARD_ACCP_NAME_LOC || ',',',')-1),'-',''))"
							+" ||'-'||DECODE(APPROVAL_CODE,'','000000',APPROVAL_CODE) ||'-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'dd-mm-yy') AS perticulers" 
							+" from MASTERCARD_POS_RAWDATA A  "
							+" WHERE PROCESSING_CODE in('200000') "
							+" AND to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')"
							+" UNION ALL "
							+" select '24' AS ACCTYPE,'30045200655' AS ACCOUNTNO,'2' AS DRCR, TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99)) AS REFUND ,"
							+" A.DATE_VAL || '-MDS Refund'||'-'||TRIM(REPLACE(substr(A.CARD_ACCP_NAME_LOC,1,instr(A.CARD_ACCP_NAME_LOC || ',',',')-1),'-',''))"
							+" ||'-'||DECODE(APPROVAL_CODE,'','000000',APPROVAL_CODE) ||'-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'dd-mm-yy') AS perticulers" 
							+" from MASTERCARD_POS_RAWDATA A  "
							+" WHERE PROCESSING_CODE in('200000') "
							+" AND to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')";*/
				
				/*getdomData = "select * from ("
						+" select '01' AS ACCTYPE,B.ACCTNUM AS ACCOUNTNO,'1' AS DRCR, TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99)) AS REFUND , "
						+" A.DATE_VAL || '-MDS Refund'||'-'||TRIM(REPLACE(substr(A.CARD_ACCP_NAME_LOC,1,instr(A.CARD_ACCP_NAME_LOC || ',',',')-1),'-','')) ||'-'||DECODE(A.APPROVAL_CODE,'','000000',A.APPROVAL_CODE) ||'-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'dd-mm-yy') AS perticulers " 
						+" from( select * from MASTERCARD_POS_RAWDATA    WHERE PROCESSING_CODE in('200000')  AND to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') ) A, "
						+" (select * from switch_rawdata  WHERE  TXNSRC='MAS' AND  TXNDEST='HOS' AND  "
						+" to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')) B "
						+" where A.PAN= B.PAN  "
						+" AND A.APPROVAL_CODE=B.AUTHNUM)"
						//+" AND to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')"
						+" UNION ALL "
						+" select * from ( select '24' AS ACCTYPE,'30045200655' AS ACCOUNTNO,'1' AS DRCR, TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99)) AS REFUND , "
						+" A.DATE_VAL || '-MDS Refund'||'-'||TRIM(REPLACE(substr(A.CARD_ACCP_NAME_LOC,1,instr(A.CARD_ACCP_NAME_LOC || ',',',')-1),'-','')) ||'-'||DECODE(A.APPROVAL_CODE,'','000000',A.APPROVAL_CODE) ||'-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'dd-mm-yy') AS perticulers " 
						+" from( select * from MASTERCARD_POS_RAWDATA    WHERE PROCESSING_CODE in('200000')  AND to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') ) A, "
						+" (select * from switch_rawdata  WHERE  TXNSRC='MAS' AND  TXNDEST='HOS' AND  "
						+" to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')) B "
						+" where A.PAN= B.PAN  "
						+" AND A.APPROVAL_CODE=B.AUTHNUM)";*/
				
					//	+" AND to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')";
					
				getdomData ="select '01' AS ACCTYPE,nVl(A.ACCOUNTNO,'N/A') AS ACCOUNTNO,'1' AS DRCR, TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99)) AS REFUND ,"
						+" A.DATE_VAL || '-MDS Refund'||'-'||TRIM(REPLACE(substr(A.CARD_ACCP_NAME_LOC,1,instr(A.CARD_ACCP_NAME_LOC || ',',',')-1),'-','')) ||'-'||DECODE(A.APPROVAL_CODE,'','000000',A.APPROVAL_CODE) ||'-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'dd-mm-yy') AS perticulers " 
						+" FROM mAstErcard_iss_refund_ttum a "
						+" whErE to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('29/12/2023','dd/mm/yyyy'),'dd-mm-yy') "
						+" UNION ALL "
						+" select '24' AS ACCTYPE,'30045200655' AS ACCOUNTNO,'2' AS DRCR, TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99)) AS REFUND ,  "
						+" A.DATE_VAL || '-MDS Refund'||'-'||TRIM(REPLACE(substr(A.CARD_ACCP_NAME_LOC,1,instr(A.CARD_ACCP_NAME_LOC || ',',',')-1),'-','')) ||'-'||DECODE(A.APPROVAL_CODE,'','000000',A.APPROVAL_CODE) ||'-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'dd-mm-yy') AS perticulers "
						+" FROM mAstErcard_iss_refund_ttum a "
						+" whErE to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('29/12/2023','dd/mm/yyyy'),'dd-mm-yy')";
				logger.info("Getdata query is " + getdomData);
			
				List<Object> DailyData = getJdbcTemplate().query(getdomData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("ACCTYPE", rs.getString("ACCTYPE"));
									table_Data.put("ACCOUNTNO", rs.getString("ACCOUNTNO"));
									table_Data.put("DRCR", rs.getString("DRCR"));
									table_Data.put("REFUND", rs.getString("REFUND"));
									table_Data.put("perticulers", rs.getString("perticulers"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
			
			}else if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD") && beanObj.getStSubCategory().equalsIgnoreCase("ISSUER") 
					  && beanObj.getInRec_Set_Id().equalsIgnoreCase("UNRECON")){
				String getdomData = null;
				 
				/*getdomData = "SELECT * FROM (" 
						+ " select distinct 'N/A' AS ACC_TYPE,'N/A' AS ACC_NO,'1' AS DRCR, AMOUNT_EQUIV  AS AMOUNT,NETWORK||' '|| 'CHB DECLINED DR REV-'||''||ISSUER||'-'||REPLACE(A.FILEDATE,'-','') AS perticulers" 
						+ " from "
						+ " (select * from switch_rawdata WHERE   RESPCODE<>'00' AND TXNSRC='MAS' AND TXNDEST IN ('MAS','HOS') AND "
						+ " to_char(to_date(FILEDATE,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy'))  A, "
						+ " (select * from cbs_rupay_rawdata WHERE   E='D'  AND "
						+ " to_char(to_date(FILEDATE,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy'))  B"
						+ " where A.ISSUER= B.REF_NO AND TRIM(A.PAN)=TRIM(B.REMARKS))"  
						+ " UNION ALL"
						+ " SELECT * FROM ( "
						+ " select distinct 'N/A' AS ACC_TYPE,'N/A' AS ACC_NO,'2' AS DRCR, AMOUNT_EQUIV  AS AMOUNT,NETWORK||' '|| 'CHB DECLINED DR REV-'||''||ISSUER||'-'||REPLACE(A.FILEDATE,'-','') AS perticulers" 
						+ " from "
						+ " (select * from switch_rawdata WHERE   RESPCODE<>'00' AND TXNSRC='MAS' AND TXNDEST IN ('MAS','HOS') AND "
						+ " to_char(to_date(FILEDATE,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy'))  A, "
						+ "(select * from cbs_rupay_rawdata WHERE   E='D'  AND "
						+ " to_char(to_date(FILEDATE,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy'))  B"
						+ " where A.ISSUER= B.REF_NO AND TRIM(A.PAN)=TRIM(B.REMARKS))";*/
				
				getdomData = " select distinct '01' AS ACC_TYPE,ACCTNUM AS ACC_NO,'1' AS DRCR, AMOUNT_EQUIV  AS AMOUNT,NETWORK||' '|| 'CHB UNRECON DR REV-'||''|| "
							+" AUTHNUM||'-'|| ISSUER||'-'||REPLACE(FILEDATE,'-','') AS perticulers "
							+" from SETTLEMENT_mastercard_ISS_switch1 "
							+" where  to_char(to_date(FILEDATE,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy')+30,'dd-mm-yy') " 
							+" AND to_char(to_date(LOCAL_DATE,'dd/mm/yyyy'),'dd/mm/yyyy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
							+" UNION ALL "
							+" select distinct '24' AS ACC_TYPE,'30045000655' AS ACC_NO,'2' AS DRCR, AMOUNT_EQUIV  AS AMOUNT,NETWORK||' '|| 'MDS UNRECON DR REV-'||''|| "
							+" AUTHNUM||'-'||ISSUER||'-'||REPLACE( FILEDATE,'-','') AS perticulers "
							+" from SETTLEMENT_mastercard_ISS_switch1 "
							+" where  to_char(to_date(FILEDATE,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy')+30,'dd-mm-yy')"
							+" AND to_char(to_date(LOCAL_DATE,'dd/mm/yyyy'),'dd/mm/yyyy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') ";
				
				logger.info("Getdata query is " + getdomData);
			
				List<Object> DailyData = getJdbcTemplate().query(getdomData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("ACC_TYPE", rs.getString("ACC_TYPE"));
									table_Data.put("ACC_NO", rs.getString("ACC_NO"));
									table_Data.put("DRCR", rs.getString("DRCR"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("perticulers", rs.getString("perticulers"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
			
			
			}else if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD") && beanObj.getStSubCategory().equalsIgnoreCase("ISSUER") 
					  && beanObj.getInRec_Set_Id().equalsIgnoreCase("FEE")){
				String getdomData = null;
				 
				getdomData = "select '01' AS ACCTYPE,'N/A' AS ACCOUNTNO,'2' AS DRCR, TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99)) AS REFUND ,"
							+" DATE_ACTION || '-MDS Claimed-'||DECODE(APPROVAL_CODE,'','000000')||'-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'ddmmyyyy') AS perticulers "
							+" from MASTERCARD_POS_RAWDATA A  "
							+" WHERE MSGTYPE='1740' AND to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') "
							+" union all   "
							+" select '24' AS ACCTYPE,'30045200655' AS ACCOUNTNO,'1' AS DRCR, TRIM(to_char(A.AMOUNT_RECON/100,99999999999999.99)) AS REFUND ,"
							+" DATE_ACTION || '-MDS Claimed-'||DECODE(APPROVAL_CODE,'','000000')||'-'|| to_char(to_date(A.FILEDATE,'dd/mm/yyyy'),'ddmmyyyy') AS perticulers "
							+" from MASTERCARD_POS_RAWDATA A  "
							+" WHERE MSGTYPE='1740' AND to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')";
				
				logger.info("Getdata query is " + getdomData);
			
				List<Object> DailyData = getJdbcTemplate().query(getdomData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("ACCTYPE", rs.getString("ACCTYPE"));
									table_Data.put("ACCOUNTNO", rs.getString("ACCOUNTNO"));
									table_Data.put("DRCR", rs.getString("DRCR"));
									table_Data.put("REFUND", rs.getString("REFUND"));
									table_Data.put("perticulers", rs.getString("perticulers"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
			
			
			}else if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD") && beanObj.getStSubCategory().equalsIgnoreCase("ISSUER") 
					  && beanObj.getInRec_Set_Id().equalsIgnoreCase("REPRESENTMENT")){

				String getdomData = null;
				 
				getdomData = "select distinct '24' AS ACCTYPE,'30045100655' AS ACCOUNTNO,'4' AS DRCR, AMOUNT ,"
						+" REF_NO||'-'||DATE_VAL ||'-'||APPROVAL_CODE ||'-'||'Pos-Rp-'||REPLACE(FILEDATE,'-','')||'~C'||REF_NO AS perticulers "
						+" from   MASTERCARD_ISS_CHARGEBACK_REPRESENTED"
						+" WHERE  to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') "
						+" UNION ALL"
						+" select distinct '24' AS ACC_TYPE,'30045100655' AS ACC_NO,'1' AS DRCR, AMOUNT  AS AMOUNT,"
						+" REF_NO||'-'|| 'Db Mds Iss Impl a/c '||REPLACE(FILEDATE,'-','')  AS perticulers"
						+" from   MASTERCARD_ISS_CHARGEBACK_REPRESENTED"
						+" WHERE  to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') ";
				
				logger.info("Getdata query is " + getdomData);
			
				List<Object> DailyData = getJdbcTemplate().query(getdomData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("ACCTYPE", rs.getString("ACCTYPE"));
									table_Data.put("ACCOUNTNO", rs.getString("ACCOUNTNO"));
									table_Data.put("DRCR", rs.getString("DRCR"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("perticulers", rs.getString("perticulers"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
			} else if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD") && beanObj.getStSubCategory().equalsIgnoreCase("ISSUER") 
					  && beanObj.getInRec_Set_Id().equalsIgnoreCase("CHARGEBACKRAISE")){

				String getdomData = null;
				 
				getdomData = "select distinct '24' AS ACCTYPE,'30045100655' AS ACCOUNTNO,'3' AS DRCR, AMOUNT ,"
						+" REF_NO||'-'||DATE_VAL ||'-'||APPROVAL_CODE ||'-'||'Pos-C-'||REPLACE(FILEDATE,'-','')||'~M'||REF_NO AS perticulers "
						+" from   MASTERCARD_ISS_CHARGEBACK_REPRESENTED"
						+" WHERE  to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') "
						+" UNION ALL"
						+" select distinct '24' AS ACC_TYPE,'30045100655' AS ACC_NO,'2' AS DRCR, AMOUNT,"
						+" REF_NO||'-'|| 'MDS Db Iss Impl a/c'||REPLACE(FILEDATE,'-','')  AS perticulers"
						+" from   MASTERCARD_ISS_CHARGEBACK_REPRESENTED"
						+" WHERE  to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') ";
				 
				logger.info("Getdata query is " + getdomData);
			
				List<Object> DailyData = getJdbcTemplate().query(getdomData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("ACCTYPE", rs.getString("ACCTYPE"));
									table_Data.put("ACCOUNTNO", rs.getString("ACCOUNTNO"));
									table_Data.put("DRCR", rs.getString("DRCR"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("perticulers", rs.getString("perticulers"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
			} else if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD") && beanObj.getStSubCategory().equalsIgnoreCase("ISSUER") 
					  && beanObj.getInRec_Set_Id().equalsIgnoreCase("LATEPRESENTMENT")){

				String getdomData = null;
				 
				getdomData = "select distinct '24' AS ACCTYPE,'30045100655' AS ACCOUNTNO,'4' AS DRCR, AMOUNT ,"
						+" REF_NO||'-'||DATE_VAL ||'-'||APPROVAL_CODE ||'-'||'Pos-Rp-'||REPLACE(FILEDATE,'-','')||'~C'||REF_NO AS perticulers "
						+" from   MASTERCARD_ISS_CHARGEBACK_REPRESENTED"
						+" WHERE  to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') "
						+" UNION ALL"
						+" select distinct '24' AS ACC_TYPE,'30045100655' AS ACC_NO,'1' AS DRCR, AMOUNT  AS AMOUNT,"
						+" REF_NO||'-'|| 'Db Mds Iss Impl a/c '||REPLACE(FILEDATE,'-','')  AS perticulers"
						+" from   MASTERCARD_ISS_CHARGEBACK_REPRESENTED"
						+" WHERE  to_char(to_date(filedate,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') ";
				
				logger.info("Getdata query is " + getdomData);
			
				List<Object> DailyData = getJdbcTemplate().query(getdomData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("ACCTYPE", rs.getString("ACCTYPE"));
									table_Data.put("ACCOUNTNO", rs.getString("ACCOUNTNO"));
									table_Data.put("DRCR", rs.getString("DRCR"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("perticulers", rs.getString("perticulers"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
			}else if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD") && beanObj.getStSubCategory().equalsIgnoreCase("ISSUER") 
					  && beanObj.getInRec_Set_Id().equalsIgnoreCase("UNRECON2")){

				String getdomData = null;
				 
				getdomData = "select  '01' AS ACCTYPE,ACCT1 AS ACCOUNTNO,'1' AS DRCR, AMOUNT , 'MDS UNRECON-'||''|| "
						+" substr(tran_id,0,6)||'-'|| REF_NO||'-'||REPLACE(FILEDATE,'-','') AS perticulers "
						+" from MASTERCARD_ISS_UMNATCHED_CBS_POS_TTUM "
						+" where  to_char(to_date(FILEDATE,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy') "
						+" UNION ALL "
						+" select  '24' AS ACCTYPE,'30045000655' AS ACCOUNTNO,'2' AS DRCR, AMOUNT ,'MDS UNRECON-'||''|| "
						+" substr(tran_id,0,6)||'-'||REF_NO||'-'||REPLACE( FILEDATE,'-','') AS perticulers "
						+" from MASTERCARD_ISS_UMNATCHED_CBS_POS_TTUM "
						+" where  to_char(to_date(FILEDATE,'dd/mm/yyyy'),'dd-mm-yy')=to_char(to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy'),'dd-mm-yy')";
				
				logger.info("Getdata query is " + getdomData);
			
				List<Object> DailyData = getJdbcTemplate().query(getdomData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("ACCTYPE", rs.getString("ACCTYPE"));
									table_Data.put("ACCOUNTNO", rs.getString("ACCOUNTNO"));
									table_Data.put("DRCR", rs.getString("DRCR"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("perticulers", rs.getString("perticulers"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
			}
			logger.info("***** MastercardTTUMServiceImpl.getMastercardTTUMData End ****");
			
			return data;

		} catch (Exception e) {
			System.out.println("Exception in MastercardTTUMServiceImpl.getMastercardTTUMData " + e);
			return null;

		}
	}
	@Override
	public void generateExcelTTUM(String stPath, String FileName, List<Object> ExcelData, String TTUMName,
			HttpServletResponse response, boolean ZipFolder) {


		StringBuffer lineData;
		List<String> files = new ArrayList<>();
		FileInputStream fis;
		try {
			logger.info("Filename is " + FileName);
			List<Object> TTUMData = (List<Object>) ExcelData.get(1);
			List<String> Excel_Headers = (List<String>) ExcelData.get(0);

			/*
			 * File file = new File(stPath+File.separator+FileName);
			 * if(file.exists()) { FileUtils.forceDelete(file); }
			 * file.createNewFile();
			 */

			OutputStream fileOut = new FileOutputStream(stPath + File.separator + FileName);

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Report");

			// create header row
			HSSFRow header = sheet.createRow(0);

			for (int i = 0; i < Excel_Headers.size(); i++) {
				header.createCell(i).setCellValue(Excel_Headers.get(i));
			}

			HSSFRow rowEntry;

			for (int i = 0; i < TTUMData.size(); i++) {
				rowEntry = sheet.createRow(i + 1);
				Map<String, String> map_data = (Map<String, String>) TTUMData.get(i);
				//logger.info("map_data is " + map_data);
				if (map_data.size() > 0) {

					for (int m = 0; m < Excel_Headers.size(); m++) {

						rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers.get(m)));
					}
				}

			}

			workbook.write(fileOut);
			fileOut.close();

			File file = new File(stPath);
			String[] filelist = file.list();

			for (String Names : filelist) {
				logger.info("name is " + Names);
				files.add(stPath + File.separator + Names);
			}
			FileOutputStream fos = new FileOutputStream(stPath + File.separator + "EXCEL_TTUMS.zip");
			ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
			try {
				for (String filespath : files) {
					File input = new File(filespath);
					fis = new FileInputStream(input);
					ZipEntry ze = new ZipEntry(input.getName());
					// System.out.println("Zipping the file: "+input.getName());
					zipOut.putNextEntry(ze);
					byte[] tmp = new byte[4 * 1024];
					int size = 0;
					while ((size = fis.read(tmp)) != -1) {
						zipOut.write(tmp, 0, size);
					}
					zipOut.flush();
					fis.close();
				}
				zipOut.close();
				// System.out.println("Done... Zipped the files...");
			} catch (Exception fe) {
				System.out.println("Exception in zipping is " + fe);
			}
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);

		}
		
	}
	@Override
	public List<Object> getMastercardEODTTUM(MastercardTTUMBean beanObj) 
	{
		logger.info("***** MastercardTTUMServiceImpl.getMastercardEODTTUM Start ****");
		String GET_DATA=null;
		List<Object> data = new ArrayList<Object>();
		 
		try
		{
			if( beanObj.getCategory().equalsIgnoreCase("ACQUIRER") )
			{
				GET_DATA ="select DATA from VW_ATM_ACQUIRING_FUNDING";
				logger.info(GET_DATA);
				
				List<Object> ttumData = getJdbcTemplate().query(GET_DATA, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("DATA",rs.getString("DATA"));
									beanList.add(table_Data);
									}
									return beanList;
								}
							});
				 data.add(ttumData);
			}else if( beanObj.getCategory().equalsIgnoreCase("ISSUER") ){

				String getdomData = "select DATA from VW_ATM_ISSUING_FUNDING"
						+ " UNION ALL "
						+ " select DATA from VW_POS_ISSUING_FUNDING";
				logger.info("Getdata query is " + getdomData);
			
				List<Object> DailyData = getJdbcTemplate().query(getdomData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("DATA", rs.getString("DATA"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
			}
			logger.info("***** MastercardTTUMServiceImpl.getMastercardEODTTUM End ****");
			return data;
		} catch (Exception e) {
			System.out.println("Exception in MastercardTTUMServiceImpl.getMastercardEODTTUM " + e);
			return null;
		}
	
	}
	
 }
