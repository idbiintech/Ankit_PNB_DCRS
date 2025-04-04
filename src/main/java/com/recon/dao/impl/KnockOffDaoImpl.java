package com.recon.dao.impl;

import static com.recon.util.GeneralUtil.GET_COLS;
import static com.recon.util.GeneralUtil.GET_FILE_HEADERS;
import static com.recon.util.GeneralUtil.GET_FILE_ID;
import static com.recon.util.GeneralUtil.GET_KNOCKOFF_CRITERIA;
import static com.recon.util.GeneralUtil.GET_REVERSAL_DETAILS;
import static com.recon.util.GeneralUtil.GET_REVERSAL_ID;
import static com.recon.util.GeneralUtil.GET_REVERSAL_PARAMS;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;

import com.recon.dao.KnockOffDao;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.util.demo;

import oracle.jdbc.OracleTypes;

@Component
public class KnockOffDaoImpl extends JdbcDaoSupport implements KnockOffDao {

	//private DBConnection dbconn = new DBConnection();
//	private Connection conn;
	private PreparedStatement pstmt;
	private String UPDATE_RECORDS = "";
	private String INSERT_RECORDS = "";
	private String SETTLEMENT_INSERT = "";
	//private String INSERT2_RECORDS = "";
	private String GET1_DUPLICATES1 = "";
	private String GET1_DUPLICATES2 = "";
	private String PART1_DUPLICATES="";
	private String PART2_DUPLICATES = "";
	private String GET2_DUPLICATES1 = "";
	private String GET2_DUPLICATES2 = "";
	

private static class ReversalParameterMaster implements RowMapper<KnockOffBean> {
@Override
public KnockOffBean mapRow(ResultSet rs, int rowNum) throws SQLException {
	/*while(rs.next())
	{*/
	//	logger.info("header is "+rs.getString("HEADER"));
		KnockOffBean knockOffBean = new KnockOffBean();
	
	knockOffBean.setStReversal_header(rs.getString("HEADER"));
	knockOffBean.setStReversal_value(rs.getString("VALUE"));
	//filterBean.setStSearch_padding(rs.getString("PADDING"));
	//filterBean.setStsearch_charpos(rs.getString("CHARPOSITION"));
	//filterBean.setStsearch_Startcharpos(rs.getString("START_CHARPOSITION"));
	//filterBean.setStsearch_Endcharpos(rs.getString("END_CHARPOSITION"));
	
	//search_params.add(filterBean);
//	}
	return knockOffBean;
	
	
}
}


/*public void getReconRecords(FilterationBean filterbean,String stFile_date) throws Exception
{
	logger.info("***** KnockoffDaoImpl.getReconRecords Start ****");
	String GET_RECON_RECORDS = "";
	String INSERT_QUERY ="";
	String SETTLEMENT_INSERT = "";
//	String TTUM_GENERATED_RECORDS = "";
//	String INSERT_TTUM_RECORDS = "";
	try
	{
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { filterbean.getStFile_Name(),filterbean.getStCategory(),filterbean.getStSubCategory()},Integer.class);
		logger.info("file_id=="+file_id);
		
		String stFile_headers = "SEG_TRAN_ID,"+getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class).trim();
		logger.info("stFile_headers=="+stFile_headers);
		
		if(filterbean.getStCategory().equals("RUPAY") || filterbean.getStCategory().equals("VISA"))
		{
			if(filterbean.getStSubCategory().equalsIgnoreCase("SURCHARGE") && !filterbean.getStFile_Name().equalsIgnoreCase("SWITCH"))
			{
			
				GET_RECON_RECORDS = "SELECT * FROM SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()
						+" WHERE DCRS_REMARKS LIKE '%"+filterbean.getStMerger_Category()+"-UNRECON-3%'" +
						//" AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY')=TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
						" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = " +
						"TO_CHAR(TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+stFile_date+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1,'DD/MM/YYYY')";
				
				GET_RECON_RECORDS = "SELECT * FROM SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()
						+" WHERE (DCRS_REMARKS LIKE '%"+filterbean.getStMerger_Category()+"-UNRECON-3%' OR DCRS_REMARKS LIKE '%"+
						filterbean.getStMerger_Category()+"-UNRECON-GENERATED-TTUM-3%')" +
						//" AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY')=TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
						" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = " +
						"TO_CHAR(TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+stFile_date+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1,'DD/MM/YYYY')";
			}
			else
			{
				GET_RECON_RECORDS = "SELECT * FROM SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()
						+" WHERE DCRS_REMARKS LIKE '%"+filterbean.getStMerger_Category()+"-UNRECON-1%'" +
						//" AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY')=TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
						" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = " +
						"TO_CHAR(TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+stFile_date+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1,'DD/MM/YYYY')";
				
				GET_RECON_RECORDS = "SELECT * FROM SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()
						+" WHERE (DCRS_REMARKS LIKE '%"+filterbean.getStMerger_Category()+"-UNRECON-1%' OR DCRS_REMARKS LIKE '%"+
						filterbean.getStMerger_Category()+"-UNRECON-GENERATED-TTUM-1%')" +
						//" AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY')=TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
						" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = " +
						"TO_CHAR(TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+stFile_date+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1,'DD/MM/YYYY')";
			}
			
			//CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS
			TTUM_GENERATED_RECORDS = "SELECT SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY'),DCRS_REMARKS,"
						+stFile_headers+" FROM SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()
						+" WHERE DCRS_REMARKS LIKE '%UNRECON-1-GENERATED-TTUM%'"+
						" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = " +
						"TO_CHAR(TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+stFile_date+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1,'DD/MM/YYYY')";
			
		}
		else
		{
			GET_RECON_RECORDS = "SELECT * FROM SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()
				+" WHERE DCRS_REMARKS LIKE '%"+filterbean.getStMerger_Category()+"-UNRECON%'" +
				//" AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY')=TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
				" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = " +
				"TO_CHAR(TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+stFile_date+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1,'DD/MM/YYYY')";			
		}
		
		logger.info("GET RECON RECORDS QUERY "+GET_RECON_RECORDS);
		Connection conn = getConnection();
		PreparedStatement pstmt = conn.prepareStatement(GET_RECON_RECORDS);
		ResultSet rset = pstmt.executeQuery();
		
		PreparedStatement settlement_pstmt = conn.prepareStatement(GET_RECON_RECORDS);
		ResultSet settlement_rset = settlement_pstmt.executeQuery();
		
		if(filterbean.getStSubCategory().equals("-"))
		{
			INSERT_QUERY = "INSERT INTO "+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()
				+" (CREATEDDATE,CREATEDBY,FILEDATE,"+stFile_headers+") VALUES (SYSDATE,'INT5779',TO_DATE('"+stFile_date+"','DD/MM/YYYY')";
			SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+filterbean.getStFile_Name()+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+stFile_headers
				+") VALUES(SYSDATE,'INT5779',TO_DATE('"+stFile_date+"','DD/MM/YYYY'),'"+filterbean.getStCategory()+"_"+filterbean.getStSubCategory()+"'";
		}
		else
		
			INSERT_QUERY = "INSERT INTO "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()
					+" (CREATEDDATE,CREATEDBY,FILEDATE,"+stFile_headers+") VALUES (SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+stFile_date+"','DD/MM/YYYY')";
			SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+stFile_headers
					+") VALUES(SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+stFile_date+"','DD/MM/YYYY'),'"+filterbean.getStMerger_Category()+"'";
		
			INSERT_TTUM_RECORDS = "INSERT INTO SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()+"(CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+stFile_headers
					+") "+TTUM_GENERATED_RECORDS;
		
		String[] col_names = stFile_headers.split(",");

		for(int i=0;i<col_names.length;i++)
		{
			INSERT_QUERY = INSERT_QUERY + ",?";
			SETTLEMENT_INSERT = SETTLEMENT_INSERT+",?";
		}
		INSERT_QUERY = INSERT_QUERY + ")";
		SETTLEMENT_INSERT = SETTLEMENT_INSERT + ")";
		logger.info("INSERT RECON RECORDS QUERY "+INSERT_QUERY);
		logger.info("SETTLEMENT INSERT RECON RECORDS QUERY "+SETTLEMENT_INSERT);
		//logger.info("INSERT TTUM RECORDS QUERY "+INSERT_TTUM_RECORDS);

		logger.info("START TIME FOR INSERTING RECON RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
		insertBatch(INSERT_QUERY, rset, col_names, conn);
		logger.info("END TIME FOR INSERTING RECON RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
		
		logger.info("START TIME FOR INSERTING RECON RECORDS IN SETTLEMENT TABLE AS ONUS "+new java.sql.Timestamp(new java.util.Date().getTime()));
		insertBatch(SETTLEMENT_INSERT, settlement_rset, col_names, conn);
		logger.info("END TIME FOR INSERTING RECON RECORDS IN SETTLEMENT TABLE AS ONUS "+new java.sql.Timestamp(new java.util.Date().getTime()));
		
		logger.info("START TIME FOR INSERTING RECON RECORDS IN SETTLEMENT TABLE AS TTUM "+new java.sql.Timestamp(new java.util.Date().getTime()));
		getJdbcTemplate().execute(INSERT_TTUM_RECORDS);
		logger.info("END TIME FOR INSERTING RECON RECORDS IN SETTLEMENT TABLE AS TTUM "+new java.sql.Timestamp(new java.util.Date().getTime()));
		

	}
	catch(Exception e)
	{
		demo.logSQLException(e, "KnockoffDaoImpl.getReconRecords");
		 logger.error(" error in KnockoffDaoImpl.getReconRecords", new Exception("KnockoffDaoImpl.getReconRecords",e));
		 throw e;
		//logger.info("Exception in getReconRecords "+e);
	}
	
	logger.info("***** KnockoffDaoImpl.getReconRecords Start ****");
}
*/





public boolean getReconRecords(FilterationBean filterbean,String stFile_date) throws ParseException, Exception {
	try {
		logger.info("*****  GET RECON RECORDS.getrecords Start ****");
		
		String response = null;
		Map<String, Object> inParams = new HashMap<String, Object>();

		inParams.put("I_CATEGORY", filterbean.getStCategory());
		inParams.put("I_SUBCATEGORY",filterbean.getStSubCategory() );
		inParams.put("I_MERGER_CATEGORY", filterbean.getStMerger_Category());
		inParams.put("I_FILE_DATE",stFile_date);
		inParams.put("I_FILE_NAME",filterbean.getStFile_Name() );
		inParams.put("I_SEG_TRAN_ID",filterbean.getInseg_tran_id());
		inParams.put("I_ENTRY_BY", filterbean.getStEntry_by());
		
		
		getrecords getrecords = new getrecords(getJdbcTemplate());
		Map<String, Object> outParams = getrecords.execute(inParams);

		logger.info("***** GETTING BACK DATA UNRECON CALL GET_RECON_RECORDS() ****");
		logger.info("***** GET RECON RECORDS.getrecords End ****");
		
		if (outParams.get("ERROR_MESSAGE") != null && !filterbean.getStCategory().equalsIgnoreCase("CARDTOCARD")) {
		

					return false;
				}else {
					
					return true;
				}
			

		
		
	} catch (Exception e) {
		demo.logSQLException(e, "KnockoffDaoImpl.getReconRecords");
		logger.error(" error in  KnockoffDaoImpl.getReconRecords", new Exception(" KnockoffDaoImpl.getReconRecords",e));
		return false;
	}
	
}

class getrecords extends StoredProcedure {
	private static final String procName = "GET_RECON_RECORDS";

	getrecords(JdbcTemplate JdbcTemplate) {
		super(JdbcTemplate, procName);
		setFunction(false);
		
		
		declareParameter(new SqlParameter("I_CATEGORY" ,OracleTypes.VARCHAR));
		declareParameter(new SqlParameter("I_SUBCATEGORY",OracleTypes.VARCHAR));
		declareParameter(new SqlParameter("I_MERGER_CATEGORY",OracleTypes.VARCHAR));
		declareParameter(new SqlParameter("I_FILE_DATE",OracleTypes.VARCHAR));
		declareParameter(new SqlParameter("I_FILE_NAME",OracleTypes.VARCHAR));
		declareParameter(new SqlParameter("I_SEG_TRAN_ID",OracleTypes.VARCHAR));
		declareParameter(new SqlParameter("I_ENTRY_BY",OracleTypes.VARCHAR));
		declareParameter(new SqlOutParameter("ERROR_CODE", OracleTypes.VARCHAR));
		declareParameter(new SqlOutParameter("ERROR_MESSAGE", OracleTypes.VARCHAR));
		compile();
	}
}

public void knockoffRecords(KnockOffBean knockOffBean) throws Exception
{
	logger.info("***** KnockoffDaoImpl.knockoffRecords Start ****");
	List<KnockOffBean> reversal_params = new ArrayList<>();
	//reversal_params = knockOffBean.getReversal_params();
	String query = "", condition = "";
			
	String temp_param;
	String columns = "";
	String tableCols = "SEG_TRAN_ID NUMBER,CREATEDDATE DATE,FILEDATE DATE, CREATEDBY VARCHAR2(100 BYTE)";
	int tableExist = 0;
	List<KnockOffBean> insert_cols = new ArrayList<>();
	//int reversal_id;
	List<Integer> reversalid = new ArrayList<Integer>();
	String select_cols = "";
	try
	{
		
		
		

		//1. GET AND UPDATE TRANSACTIONS TO BE CONSIDERED FOR KNOCKOFF COMPARISON
			// Get File id from db
				int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { knockOffBean.getStFile_Name(),knockOffBean.getStCategory(),knockOffBean.getStSubCategory() },Integer.class);
				logger.info("file Id is "+file_id);

				if(knockOffBean.getStSubCategory().equals("-"))
				{
					//FOR TAKING KNOCKOFF IN LOOPS
					reversalid = getJdbcTemplate().query(GET_REVERSAL_ID,new Object[]{ (file_id), knockOffBean.getStCategory()},new ReversalId());
					// Get Id from compare Master as it is unique in all compare tables
					
				}
				else
				{
					reversalid = getJdbcTemplate().query(GET_REVERSAL_ID,new Object[]{ (file_id), knockOffBean.getStCategory()+"_"+knockOffBean.getStSubCategory()},new ReversalId());
				}
				logger.info("REVERSAL id is "+reversalid);
				
	//looping for MULTIPLE KNOCKOFF CONDITIONS
				String DELETE_FROM_CLASS1 = "";
				String DELETE_FROM_CLASS2 = "";
				
				for(int Reversal_id : reversalid)
				{
					String duplicate1_condition = "";
					// GET REVERSAL_DETAILS
					List<KnockOffBean> reversal_details = getJdbcTemplate().query(GET_REVERSAL_DETAILS, new Object[] { Reversal_id , file_id}, new ReversalParameterMaster());
					logger.info("reversal_details  is "+reversal_details);
					//PREPARING PROPER CONDITION for duplicate1 
	 
					for(int i = 0; i<reversal_details.size();i++){
						KnockOffBean knockoffBeanObj = new KnockOffBean();
						knockoffBeanObj = reversal_details.get(i);
						temp_param = knockoffBeanObj.getStReversal_header();

						duplicate1_condition = duplicate1_condition + "("+knockoffBeanObj.getStReversal_header()+" = '"+knockoffBeanObj.getStReversal_value()+"'";

						for(int j= (i+1); j <reversal_details.size(); j++)
						{
							if(temp_param.equals(reversal_details.get(j).getStReversal_header()))
							{
								duplicate1_condition = duplicate1_condition + " OR " + reversal_details.get(j).getStReversal_header()+" = '"+reversal_details.get(j).getStReversal_value()+"'";
								i = j;
							}

						}
						if(i != (reversal_details.size())-1)
						{
							duplicate1_condition = duplicate1_condition +" ) AND ";
						}
						else
							duplicate1_condition = duplicate1_condition +")";

					}

					//********************ends here****************************		

					//2. GET THE CONDITION FROM REVERSAL_PARAMETER TABLE -- eg  records with msgtype 430 or 410 criteria
					// i.e with which header and value we have to compare

					//GET REVERSAL PARAMETER
					reversal_params = getJdbcTemplate().query(GET_REVERSAL_PARAMS, new Object[] { Reversal_id , file_id}, new ReversalParameterMaster());
					logger.info("reversal_params  is "+reversal_params);

					String condition1 = "";
					for(int i = 0; i<reversal_params.size();i++){
						KnockOffBean knockoffBeanObj = new KnockOffBean();
						knockoffBeanObj = reversal_params.get(i);
						temp_param = knockoffBeanObj.getStReversal_header();			
						condition1 = condition1 + "(OS2."+knockoffBeanObj.getStReversal_header()+" = '"+knockoffBeanObj.getStReversal_value()+"'";

						for(int j= (i+1); j <reversal_params.size(); j++)
						{
							if(temp_param.equals(reversal_params.get(j).getStReversal_header()))
							{
								condition1 = condition1 + " OR OS2." + reversal_params.get(j).getStReversal_header()+" = '"+reversal_params.get(j).getStReversal_value()+"'";
								i = j;
							}

						}
						if(i != (reversal_params.size())-1)
						{
							condition1 = condition1 +" ) AND ";
						}
						else
							condition1 = condition1 +")";
					}
					logger.info("condition is "+condition1);

					//3. GET PARAMETERS for appending in duplicate2 query for main comparison
					List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { Reversal_id , file_id}, new KnockOffCriteriaMaster());
					logger.info("knockoff_Criteria is "+knockoff_Criteria);
					
					condition = getCondition(knockoff_Criteria); //knockoff condition is perpared
					
					//add not null CRITERIA HERE
					String NULLcondition = checkNULLcondition(knockoff_Criteria);					
					logger.info("NULLcondition is "+NULLcondition);	
					
					List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {knockOffBean.getStMergeCategory()+"_"+knockOffBean.getStFile_Name()},new ColumnMapper());
					logger.info("TABLE_COLS is "+TABLE_COLS);	
					
					int count = 0;
					for(String cols : TABLE_COLS)
					{
						if(count == 0)
							{
								select_cols = cols;
								count++;
							}
						else
							select_cols = select_cols+","+cols;
					}



					//GET_DUPLICATES1 = "SELECT * FROM "+knockOffBean.getStCategory() +"_"+ knockOffBean.getStFile_Name()+" OS1 WHERE KNOCKOFF_FLAG = 'N'";
					GET1_DUPLICATES1 = "SELECT "+select_cols+" FROM "+knockOffBean.getStMergeCategory() +"_"+ knockOffBean.getStFile_Name()+" OS1 ";
					//added BY INT5779 ON 06TH MARCH 2018
					String CHECK_COUNT = "SELECT    COUNT(*) FROM "+knockOffBean.getStMergeCategory() +"_"+ knockOffBean.getStFile_Name()+" OS1 ";
					GET2_DUPLICATES1 = "SELECT "+select_cols+" FROM "+knockOffBean.getStMergeCategory() +"_"+ knockOffBean.getStFile_Name()+" OS2 ";
					
					DELETE_FROM_CLASS1 = "DELETE   FROM "+knockOffBean.getStMergeCategory()+"_"+knockOffBean.getStFile_Name()+" OS1 ";
					DELETE_FROM_CLASS2 = "DELETE  FROM "+knockOffBean.getStMergeCategory() +"_"+ knockOffBean.getStFile_Name()+" OS2 ";
					logger.info("duplicate condition1 : "+duplicate1_condition);

					if(!duplicate1_condition.equals(""))
					{
						GET1_DUPLICATES1 = GET1_DUPLICATES1 + " WHERE "+duplicate1_condition;
						DELETE_FROM_CLASS1 = DELETE_FROM_CLASS1+ " WHERE "+duplicate1_condition;
						CHECK_COUNT = CHECK_COUNT + " WHERE "+duplicate1_condition;
					}
							
					//create duplicate2 query
					GET1_DUPLICATES2 = "SELECT "+select_cols+" FROM "+ knockOffBean.getStMergeCategory()+"_"+knockOffBean.getStFile_Name()+" OS2 ";
					GET2_DUPLICATES2 = "SELECT "+select_cols+" FROM "+ knockOffBean.getStMergeCategory()+"_"+knockOffBean.getStFile_Name()+" OS1 ";
					String DELETE_QUERY1 = "SELECT * FROM "+knockOffBean.getStMergeCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF OS2 ";
					String DELETE_QUERY2 = "SELECT * FROM "+knockOffBean.getStMergeCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF OS1 ";

					// we have to update both 210 and 430 or 410 records at the same time
					if(!condition1.equals(""))
					{
						GET1_DUPLICATES2 = GET1_DUPLICATES2+" WHERE "+condition1; 
						GET2_DUPLICATES1 = GET2_DUPLICATES1 + " WHERE "+condition1;
						DELETE_FROM_CLASS2 = DELETE_FROM_CLASS2+" WHERE "+condition1;								
						DELETE_QUERY1 = DELETE_QUERY1+ " WHERE "+condition1;
					}

					if(!duplicate1_condition.equals(""))
					{
						GET2_DUPLICATES2 = GET2_DUPLICATES2 +" WHERE "+ duplicate1_condition;
						DELETE_QUERY2 = DELETE_QUERY2 + " WHERE "+duplicate1_condition;
					}
					if(!condition.equals(""))
					{
						//CHANGES MADE BY INT5779 AS ON09 MARCH FOR TESTING KNOCKOFF
						GET1_DUPLICATES2 = GET1_DUPLICATES2 + " AND ("+condition+" AND "+NULLcondition+")";
						GET2_DUPLICATES2 = GET2_DUPLICATES2 + " AND ("+condition +" AND "+NULLcondition+" )";
						DELETE_QUERY1 = DELETE_QUERY1 +" AND ("+condition+" AND "+NULLcondition+" )";
						DELETE_QUERY2 = DELETE_QUERY2 +" AND ("+condition+" AND "+NULLcondition+" )";

					}
					// QUERY FOR GETTING DUPLICATES IN TABLE
					// we have to take 2 queries coz we have to insert both the records eg 210 and 410 or 430 from onus
					PART1_DUPLICATES = GET1_DUPLICATES1 + " AND (trunc(OS1.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( "+GET1_DUPLICATES2+" AND  trunc(OS2.CREATEDDATE) = trunc(SYSDATE))";
					//added BY INT5779 ON 06TH MARCH 2018 
					CHECK_COUNT = CHECK_COUNT + " AND (trunc(OS1.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( "+GET1_DUPLICATES2+" AND trunc(OS2.CREATEDDATE) = trunc(SYSDATE))";
					
					PART2_DUPLICATES = GET2_DUPLICATES1 + " AND (trunc(OS2.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( "+GET2_DUPLICATES2+" AND trunc(OS1.CREATEDDATE) = trunc(SYSDATE))";
					/*DELETE_FROM_CLASS1 = DELETE_FROM_CLASS1+" AND (OS1.CREATEDDATE = trunc(SYSDATE)) AND EXISTS ( "+GET1_DUPLICATES2+" AND trunc(OS2.CREATEDDATE) = trunc(SYSDATE))";
					DELETE_FROM_CLASS2 = DELETE_FROM_CLASS2+ " AND (trunc(OS2.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( "
										+GET2_DUPLICATES2+" AND TO_CHAR(OS1.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY'))";*/
					
					DELETE_FROM_CLASS1 = DELETE_FROM_CLASS1+" AND (trunc(OS1.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( "+DELETE_QUERY1+" AND trunc(OS2.CREATEDDATE) = trunc(SYSDATE))";
					DELETE_FROM_CLASS2 = DELETE_FROM_CLASS2 +" AND (trunc(OS2.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( "+DELETE_QUERY2+" AND trunc(OS1.CREATEDDATE) = trunc(SYSDATE))";
					logger.info("KNOCKED OFF QUERY IS : "+PART1_DUPLICATES);
					logger.info("PART2 QUERY IS "+PART2_DUPLICATES);
					logger.info("DELETE QUERY IS "+DELETE_FROM_CLASS1);
					logger.info("DELETE QUERY IS "+DELETE_FROM_CLASS2);
					
					//ADDED BY INT5779 TO CHECK THE COUNT OF KNOCKOFF DATA....
					/**
					 * IF THE COUNT IS 0 THEN DO NOT FIRE DELETE QUERY 
					 */
					logger.info("CHECK_COUNT query is "+CHECK_COUNT);
					
					int check_count = getJdbcTemplate().queryForObject(CHECK_COUNT,new Object[]{},Integer.class);
					logger.info("knockoff data count is "+check_count);
					
					//FOR 2ND LOOP APPLY NOT EXISTS IN THE QUERY 
					 
					/******INSERT THESE RECORDS IN NEW KNOCKOFF TABLE
					 * 
					 */
					//1. create new knockoff table if not created


					String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
					logger.info("stFile_headers== "+stFile_headers);
					columns = "SEG_TRAN_ID"+","+stFile_headers;
					if(knockOffBean.getStFile_Name().equals("CBS"))
					{
						stFile_headers = stFile_headers + ",MAN_CONTRA_ACCOUNT";
						columns = columns + ",MAN_CONTRA_ACCOUNT";
					}

					String[] col_names = stFile_headers.trim().split(",");

					for(int i=0 ;i <col_names.length; i++)
					{
						tableCols  = tableCols +","+ col_names[i]+" VARCHAR (100 BYTE) ";
					}


					//logger.info("table columns are "+tableCols);
					//logger.info("columns are "+columns);


					//CECKING WHETHER TABLE IS ALREADY PRESENT
					String CHECK_TABLE = "SELECT count (*) FROM tab WHERE upper(tname)  = '"+knockOffBean.getStMergeCategory().toUpperCase()+"_"+knockOffBean.getStFile_Name().toUpperCase()+"_KNOCKOFF'";
					logger.info("CHECK_TABLE== "+CHECK_TABLE);
					
					tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
					logger.info("table exists value is "+tableExist);
					
					if(tableExist == 0)
					{
						query = "CREATE TABLE "+knockOffBean.getStMergeCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF ("+tableCols+")";
						getJdbcTemplate().execute(query);
						logger.info("Table is Created using "+query);
					}

				 
					INSERT_RECORDS = " INSERT INTO "+knockOffBean.getStMergeCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF ("+select_cols+") ";
					String INSERT_QUERY1 = INSERT_RECORDS+" "+PART1_DUPLICATES;
					String INSERT_QUERY2 = INSERT_RECORDS+" "+PART2_DUPLICATES;
							 
					logger.info("INSERT QUERY 1 "+INSERT_QUERY1);
					logger.info("INSERT QUERY 2 "+INSERT_QUERY2);
					
					
					logger.info("START TIME FOR INSERTING KNOCKOFF RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
					getJdbcTemplate().execute(INSERT_QUERY1);
					getJdbcTemplate().execute(INSERT_QUERY2);
					
					String query1 ="INSERT INTO VISA_ISS_CBS_KNOCKOFF (CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,PSTD_USER_ID,ENTRY_DATE,VFD_DATE,PARTICULARALS2,ORG_ACCT,SYS_REF,MAN_CONTRA_ACCOUNT)  SELECT CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,PSTD_USER_ID,ENTRY_DATE,VFD_DATE,PARTICULARALS2,ORG_ACCT,SYS_REF,MAN_CONTRA_ACCOUNT FROM VISA_ISS_CBS OS1  WHERE (E = 'C') AND (trunc(OS1.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( SELECT CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,PSTD_USER_ID,ENTRY_DATE,VFD_DATE,PARTICULARALS2,ORG_ACCT,SYS_REF,MAN_CONTRA_ACCOUNT FROM VISA_ISS_CBS OS2  WHERE (OS2.E = 'D') AND (OS1.AMOUNT = OS2.AMOUNT AND OS1.REF_NO = OS2.REF_NO AND OS1.SYS_REF = OS2.SYS_REF AND  OS1.AMOUNT IS NOT NULL AND OS2.AMOUNT IS NOT NULL  AND OS1.REF_NO IS NOT NULL AND OS2.REF_NO IS NOT NULL  AND OS1.SYS_REF IS NOT NULL AND OS2.SYS_REF IS NOT NULL ) AND  trunc(OS2.CREATEDDATE) = trunc(SYSDATE))";


					String query2 = " INSERT INTO VISA_ISS_CBS_KNOCKOFF (CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,PSTD_USER_ID,ENTRY_DATE,VFD_DATE,PARTICULARALS2,ORG_ACCT,SYS_REF,MAN_CONTRA_ACCOUNT)  SELECT CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,PSTD_USER_ID,ENTRY_DATE,VFD_DATE,PARTICULARALS2,ORG_ACCT,SYS_REF,MAN_CONTRA_ACCOUNT FROM VISA_ISS_CBS OS2  WHERE (OS2.E = 'D') AND (trunc(OS2.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( SELECT CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,PSTD_USER_ID,ENTRY_DATE,VFD_DATE,PARTICULARALS2,ORG_ACCT,SYS_REF,MAN_CONTRA_ACCOUNT FROM VISA_ISS_CBS OS1  WHERE (E = 'C') AND (OS1.AMOUNT = OS2.AMOUNT AND OS1.REF_NO = OS2.REF_NO AND OS1.SYS_REF = OS2.SYS_REF AND  OS1.AMOUNT IS NOT NULL AND OS2.AMOUNT IS NOT NULL  AND OS1.REF_NO IS NOT NULL AND OS2.REF_NO IS NOT NULL  AND OS1.SYS_REF IS NOT NULL AND OS2.SYS_REF IS NOT NULL  ) AND trunc(OS1.CREATEDDATE) = trunc(SYSDATE))";
					
					
					logger.info("END TIME FOR INSERTING KNOCKOFF RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
					logger.info("KNOCKOFF COMPLETED");
					
					//delete RECORDS entrIES FROM CLASSIFICATION TABLE
					//added BY INT5779 ON 06TH MARCH 2018
					if(check_count > 0)
					{
						getJdbcTemplate().execute(DELETE_FROM_CLASS1);
						getJdbcTemplate().execute(DELETE_FROM_CLASS2);
					}
				}
				
				// changes by minakshi 10/08/2018 date format replace
				String SETTLEMENT_DATA = "SELECT "+select_cols+",'"+knockOffBean.getStMergeCategory()+"-KNOCKOFF' FROM "+knockOffBean.getStMergeCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF" +
						" WHERE FILEDATE='"+knockOffBean.getStFile_date()+"'";
				SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+" ("+select_cols+",DCRS_REMARKS) " +SETTLEMENT_DATA ;
				logger.info("SETTLEMENT INSERT QUERY IS "+SETTLEMENT_INSERT);
				getJdbcTemplate().execute(SETTLEMENT_INSERT);
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "KnockoffDaoImpl.knockoffRecords");
		logger.error(" error in KnockoffDaoImpl.knockoffRecords", new Exception("KnockoffDaoImpl.knockoffRecords",e));
		throw e;
	}
	logger.info("***** KnockoffDaoImpl.knockoffRecords End ****");
}

private class ColumnMapper implements RowMapper<String>
{
	@Override
	public String mapRow(ResultSet rset, int row)throws SQLException
	{
		return rset.getString("COLUMN_NAME");
	}
}
 

private static class KnockOffCriteriaMaster implements RowMapper<KnockOffBean> {

	@Override
	public KnockOffBean mapRow(ResultSet rs, int rowNum) throws SQLException {
	 
		KnockOffBean knockOffBean = new KnockOffBean();
		knockOffBean.setStReversal_header(rs.getString("HEADER"));
		knockOffBean.setStReversal_padding(rs.getString("PADDING"));
		knockOffBean.setStReversal_charpos(rs.getString("START_CHARPOSITION"));
		knockOffBean.setStReversal_charsize(rs.getString("CHAR_SIZE"));
		knockOffBean.setStReversal_value(rs.getString("HEADER_VALUE"));
		knockOffBean.setStReversal_condition(rs.getString("CONDITION"));
		return knockOffBean;


	}
}
private static class ReversalId implements RowMapper<Integer> {

	@Override
	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {

		int i = Integer.parseInt(rs.getString("REVERSAL_ID"));
		return i;
		
	}
}
public String getCondition(List<KnockOffBean> knockoff_Criteria)
{
	logger.info("***** KnockoffDaoImpl.getCondition Start ****");
	
	String select_parameters = "", condition = "", update_condition="" ;
	List<KnockOffBean> Update_Headers = new ArrayList<>();
	for(int i = 0 ;i<knockoff_Criteria.size();i++)
	{
		if(i == (knockoff_Criteria.size()-1))
		{
			if(knockoff_Criteria.get(i).getStReversal_value()!=null)
			{
				if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
				{
					condition = condition + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() + " "+knockoff_Criteria.get(i).getStReversal_value();
					/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+ " "+knockoff_Criteria.get(i).getStReversal_value();*/
					/*update_condition = update_condition +"SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") "+ knockoff_Criteria.get(i).getStReversal_condition()+
							knockoff_Criteria.get(i).getStReversal_value();*/
					select_parameters = select_parameters+ " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header();
				}
				else
				{
					condition = condition +"OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
								+" "+knockoff_Criteria.get(i).getStReversal_value();
					/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+ " "+knockoff_Criteria.get(i).getStReversal_value();*/
					/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?";*/
					select_parameters = select_parameters + "OS1."+ knockoff_Criteria.get(i).getStReversal_header();
				}
			}
			else
			{
				Update_Headers.add(knockoff_Criteria.get(i));
				if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
				{
					condition = condition + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+" SUBSTR( OS2."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")";
					update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") = ?";
					select_parameters = select_parameters + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header();
				}
				else
				{
					condition = condition +"OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition() +" OS2."+knockoff_Criteria.get(i).getStReversal_header();
					update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?";
					select_parameters = select_parameters + "OS1."+ knockoff_Criteria.get(i).getStReversal_header();
				}
			
				
			}
		}
		else
		{
			if(knockoff_Criteria.get(i).getStReversal_value()!=null)
			{
				if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
				{
					condition = condition + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() + " "+knockoff_Criteria.get(i).getStReversal_value()+" AND ";
					/*update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()
							+") = ?"+" AND ";*/
					select_parameters = select_parameters + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
					+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header()+" , ";
				}
				else
				{
					condition = condition +"OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
								+" "+knockoff_Criteria.get(i).getStReversal_value()+" AND ";
					/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?"+" AND ";*/
					select_parameters = select_parameters + "OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" , ";
				}
			}
			else
			{
				Update_Headers.add(knockoff_Criteria.get(i));
				if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
				{
					condition = condition + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+" SUBSTR( OS2."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AND ";
					update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()
							+") = ? AND ";
					select_parameters = select_parameters + " SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()
							+") AS "+knockoff_Criteria.get(i).getStReversal_header()+" , ";
				}
				else
				{
					condition = condition +"OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
							+" OS2."+knockoff_Criteria.get(i).getStReversal_header()+" AND ";
					update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ? AND ";
					select_parameters = select_parameters + "OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" , ";
				}
			
				
			}
			
		}
		
	}
	logger.info("condition== "+condition);
	logger.info("update_condition== "+update_condition);
	logger.info("update_condition== "+update_condition);
	logger.info("***** KnockoffDaoImpl.getCondition End ****");
	
	return condition;
}

//ADDING NEW CONDITION FOR CHECKING NOT NULL AS PER SAMEER
public String checkNULLcondition(List<KnockOffBean> knockoff_Criteria) throws Exception
{
	String condition = "";
	try
	{
		
		int i = 0;
		for(KnockOffBean beanObj : knockoff_Criteria)
		{
			if(i == 0)
			{
				condition = " OS1."+beanObj.getStReversal_header()+" IS NOT NULL AND OS2."+beanObj.getStReversal_header()+" IS NOT NULL ";
			}
			else
				condition = condition + " AND OS1."+beanObj.getStReversal_header()+" IS NOT NULL AND OS2."+beanObj.getStReversal_header()+" IS NOT NULL ";
			
			i++;
		}
		logger.info("IS NOT NULL CONDITION IS "+condition);
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "KnockoffDaoImpl.checkNULLcondition");
		logger.error(" error in KnockoffDaoImpl.checkNULLcondition", new Exception("KnockoffDaoImpl.checkNULLcondition",e));
		throw e;
	}
	return condition;
	
}







public String getLoop2Condition(List<KnockOffBean> knockoff_Criteria,String alias)
{
	
	String select_parameters = "", condition = "", update_condition="" ;
	List<KnockOffBean> Update_Headers = new ArrayList<>();
	for(int i = 0 ;i<knockoff_Criteria.size();i++)
	{
		if(i == (knockoff_Criteria.size()-1))
		{
			if(knockoff_Criteria.get(i).getStReversal_value()!=null)
			{
				if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
				{
					condition = condition + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() + " "+knockoff_Criteria.get(i).getStReversal_value();
					/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+ " "+knockoff_Criteria.get(i).getStReversal_value();*/
					/*update_condition = update_condition +"SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") "+ knockoff_Criteria.get(i).getStReversal_condition()+
							knockoff_Criteria.get(i).getStReversal_value();*/
					select_parameters = select_parameters+ " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header();
				}
				else
				{
					condition = condition +"T1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
								+" "+knockoff_Criteria.get(i).getStReversal_value();
					/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+ " "+knockoff_Criteria.get(i).getStReversal_value();*/
					/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?";*/
					select_parameters = select_parameters + "T1."+ knockoff_Criteria.get(i).getStReversal_header();
				}
			}
			else
			{
				Update_Headers.add(knockoff_Criteria.get(i));
				if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
				{
					condition = condition + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+" SUBSTR( "+alias+"."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")";
					/*update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") = ?";
					select_parameters = select_parameters + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header();*/
				}
				else
				{
					condition = condition +"T1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition() +" "+alias+"."+knockoff_Criteria.get(i).getStReversal_header();
					/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?";
					select_parameters = select_parameters + "T1."+ knockoff_Criteria.get(i).getStReversal_header();*/
				}
			
				
			}
		}
		else
		{
			if(knockoff_Criteria.get(i).getStReversal_value()!=null)
			{
				if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
				{
					condition = condition + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() + " "+knockoff_Criteria.get(i).getStReversal_value()+" AND ";
					/*update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()
							+") = ?"+" AND ";*/
					/*select_parameters = select_parameters + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
					+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header()+" , ";*/
				}
				else
				{
					condition = condition +"T1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
								+" "+knockoff_Criteria.get(i).getStReversal_value()+" AND ";
					/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?"+" AND ";*/
					//select_parameters = select_parameters + "T1."+ knockoff_Criteria.get(i).getStReversal_header()+" , ";
				}
			}
			else
			{
				Update_Headers.add(knockoff_Criteria.get(i));
				if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
				{
					condition = condition + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+" SUBSTR( "+alias+"."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AND ";
					/*update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()
							+") = ? AND ";
					select_parameters = select_parameters + " SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()
							+") AS "+knockoff_Criteria.get(i).getStReversal_header()+" , ";*/
				}
				else
				{
					condition = condition +"T1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
							+" "+alias+"."+knockoff_Criteria.get(i).getStReversal_header()+" AND ";
			
				}
			
				
			}
			
		}
		
	}
	return condition;
}
}
