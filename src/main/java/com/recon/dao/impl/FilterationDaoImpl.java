package com.recon.dao.impl;


import static com.recon.model.FilterationBean.FILENAME;
import static com.recon.model.FilterationBean.FILE_CATEGORY;
import static com.recon.util.GeneralUtil.ADD_RECORDS;
import static com.recon.util.GeneralUtil.ADD_SEG_RECORD;
import static com.recon.util.GeneralUtil.GET_COLS;
import static com.recon.util.GeneralUtil.GET_COMPARE_ID;
import static com.recon.util.GeneralUtil.GET_FILE_HEADERS;
import static com.recon.util.GeneralUtil.GET_FILE_ID;
import static com.recon.util.GeneralUtil.GET_MANUAL_PARAM;
import static com.recon.util.GeneralUtil.GET_MAN_ID;
import static com.recon.util.GeneralUtil.GET_MAN_MATCHING;
import static com.recon.util.GeneralUtil.GET_SEARCH_PARAMS;
import static com.recon.util.GeneralUtil.GET_SEG_TRAN_ID;
import static com.recon.util.GeneralUtil.GET_SETTLEMENT_ID;
import static com.recon.util.GeneralUtil.GET_SETTLEMENT_PARAM;
import static com.recon.util.GeneralUtil.GET_TABLE_NAME;
import static com.recon.util.GeneralUtil.GET_TRN_ID;
import static com.recon.util.GeneralUtil.GET_UPDATE_COLS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;

import com.recon.dao.FilterationDao;
import com.recon.model.CompareBean;
import com.recon.model.FilterationBean;
import com.recon.model.ManualFileBean;
//import static com.recon.util.GeneralUtil.GET_STATUS;
//import com.pf.dao.impl.LoanDaoImpl.LoanMasterMapper;
//import com.pf.model.LoanBean;
import com.recon.model.SettlementBean;
import com.recon.util.demo;

import oracle.jdbc.OracleTypes;

@Component
public class FilterationDaoImpl extends JdbcDaoSupport implements FilterationDao {

	/**
	 * normal jdbc connection as we cant get whole resultset using jdbctemplate
	 */
	//private DBConnection dbconn = new DBConnection();
	//private Connection conn;
	private PreparedStatement pstmt;
	
	/**
	 * output field constants
	 */
	private static final String O_FILES_CUR = "o_role_cur";
	private static final String O_ERROR_CODE = "o_error_code";
	private static final String O_ERROR_MESSAGE = "o_error_message";
	private static String INSERT_QUERY = "INSERT_QUERY";


	@Override
	public List<FilterationBean> getaddedFiles()throws Exception
	{
		Map<String, Object> inParams = new HashMap<String, Object>();
		
		ViewAddedFiles viewAddedFiles = new ViewAddedFiles(getJdbcTemplate());
		Map<String, Object> outParams = viewAddedFiles.execute(inParams);

		if (Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0 && outParams.get(O_ERROR_MESSAGE) != null) {
			throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
		}
		
		
		return viewAddedFiles.getFile_list();
	}


	private class ViewAddedFiles extends StoredProcedure {
		private static final String view_files_proc = "VIEW_FILES";
		List<FilterationBean> files_list = new ArrayList<FilterationBean>();

		/**
		 * @return the role_list
		 */
		public List<FilterationBean> getFile_list() {
			return files_list;
		}

		
		public ViewAddedFiles(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, view_files_proc);
			setFunction(false);
			declareParameter(new SqlOutParameter(O_FILES_CUR, OracleTypes.CURSOR, new FileMapper()));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, OracleTypes.VARCHAR));
			compile();
		}

		private class FileMapper implements RowCallbackHandler {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				FilterationBean filterbean = new FilterationBean();
				filterbean.setStFilesAvail(rs.getString(FILE_CATEGORY)+"-"+rs.getString(FILENAME));
				files_list.add(filterbean);
			}
		}
	}

	
	@Override
	public List<FilterationBean> getSearchParams(FilterationBean filterBean)throws Exception
	{
		logger.info("***** FilterationDaoImpl.getSearchParams Start ****");
		int compare_id ;
		//1. Get File id from db
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { filterBean.getStFile_Name(),filterBean.getStCategory(),filterBean.getStSubCategory() },Integer.class);
		logger.info("file Id is == "+file_id);
		
		if(!filterBean.getStSubCategory().equals("-"))
		{
		//2. Get Id from compare Master as it is unique in all compare tables
			compare_id = getJdbcTemplate().queryForObject(GET_COMPARE_ID, new Object[] { (file_id), filterBean.getStCategory()+"_"+filterBean.getStSubCategory()},Integer.class);
		}
		else
			compare_id = getJdbcTemplate().queryForObject(GET_COMPARE_ID, new Object[] { (file_id), filterBean.getStCategory()},Integer.class);
		logger.info("Compare id is== "+compare_id);
		
		//logger.info("file id "+file_id+"compare id "+compare_id);
		
		//3. Get All search Parameters from compare_details table
		List<FilterationBean> search_params = getJdbcTemplate().query(GET_SEARCH_PARAMS, new Object[] { compare_id , file_id}, new SearchParameterMaster());
		//logger.info("SEARCH PARAMETERS ARE "+GET_SEARCH_PARAMS);
		//logger.info("search list size is "+search_params.size());
		
		logger.info("***** FilterationDaoImpl.getSearchParams End ****");
		
		return search_params;
		
	}
	
	private static class SearchParameterMaster implements RowMapper<FilterationBean> {

		@Override
		public FilterationBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			//logger.info("row num is "+rowNum);
		//	logger.info("header is "+rs.getString("FILE_HEADER"));
			/*while(rs.next())
			{*/
		//		logger.info("header is "+rs.getString("FILE_HEADER"));
			FilterationBean filterBean = new FilterationBean();
			
			filterBean.setStSearch_header(rs.getString("FILE_HEADER"));
			filterBean.setStSearch_pattern(rs.getString("SEARCH_PATTERN"));
			filterBean.setStSearch_padding(rs.getString("PADDING"));
			//filterBean.setStsearch_charpos(rs.getString("CHARPOSITION"));
			filterBean.setStsearch_Startcharpos(rs.getString("START_CHARPOSITION"));
			filterBean.setStsearch_Endcharpos(rs.getString("END_CHARPOSITION"));
			filterBean.setStSearch_Condition(rs.getString("CONDITION"));
			
			//search_params.add(filterBean);
		//	}
			return filterBean;
			
			
		}
	}
	
	@Override
	public int getseg_tran_id()throws Exception
	{
		logger.info("***** FilterationDaoImpl.getseg_tran_id Start ****");
		
		PreparedStatement ps;
		try
		{
		int seg_tran_id =0;
		//getting null pointer exception if table is blank
		//seg_tran_id = getJdbcTemplate().queryForObject(GET_SEG_TRAN_ID, new Object[] { },Integer.class);
		
		//conn =dbconn.getCon();
		 ps = getConnection().prepareStatement(GET_SEG_TRAN_ID);
		ResultSet rset = ps.executeQuery();
		if(rset.next())
		{
			seg_tran_id = rset.getInt("SEG_TRAN_ID");
		}
		seg_tran_id = seg_tran_id+1;
		
		logger.info("seg_tran_id== "+seg_tran_id);
		
		logger.info("***** FilterationDaoImpl.getseg_tran_id End ****");
		
		return seg_tran_id;
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "FilterationDaoImpl.getseg_tran_id");
			logger.error(" error in FilterationDaoImpl.getseg_tran_id", new Exception("FilterationDaoImpl.getseg_tran_id",e));
			throw new Exception("SEG TRAN ID NOT FOUND");
		}
		/*finally
		{
			if(conn !=null)
			{
				conn.close();
				
			}
		}*/
	}
	
	@Override
	public int addEntry(final FilterationBean filterBean)throws Exception
	{
		 logger.info("***** FilterationDaoImpl.addEntry Start ****");
		final int compare_id;
		//List<FilterationBean> segregate_list = new ArrayList<>();
		//1. Get File id from db
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { filterBean.getStFile_Name(),filterBean.getStCategory(),filterBean.getStSubCategory()},Integer.class);
		//logger.info("file Id is "+file_id);
				
		if(!filterBean.getStSubCategory().equals("-"))
		{
		//2. Get Id from compare Master as it is unique in all compare tables
			compare_id = getJdbcTemplate().queryForObject(GET_COMPARE_ID, new Object[] { (file_id), filterBean.getStCategory()+"_"+filterBean.getStSubCategory()},Integer.class);
		//logger.info("Compare id is "+compare_id);
		}
		else
			 compare_id = getJdbcTemplate().queryForObject(GET_COMPARE_ID, new Object[] { (file_id), filterBean.getStCategory()},Integer.class);
		//inserting data in segregate_transaction table
		int i =getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = conn.prepareStatement(ADD_SEG_RECORD);
				ps.setInt(1, filterBean.getInseg_tran_id());
				ps.setInt(2, compare_id);
				ps.setTimestamp(3, new java.sql.Timestamp(new java.util.Date().getTime()));
				ps.setTimestamp(4, new java.sql.Timestamp(new java.util.Date().getTime()));
				ps.setString(5, filterBean.getStEntry_by());
				return ps;
			}
		});
		
		//logger.info("i is "+i);
		
		 logger.info("***** FilterationDaoImpl.addEntry End ****");
	
		return i;
		
	}
	
	
	@Override
	public int filterRecords(FilterationBean filterbean)throws Exception
	{
		logger.info("***** FilterationDaoImpl.filterRecords Start ****");
		
		String tableCols = "CREATEDDATE DATE, CREATEDBY VARCHAR2(100 BYTE),FILEDATE DATE, SEG_TRAN_ID NUMBER";
		
	 
		String settlement_col = "CREATEDDATE, CREATEDBY,FILEDATE,SEG_TRAN_ID,DCRS_REMARKS";
		String SETTLEMENT_INSERT = "";
		try
		{
			
		List<FilterationBean> search_params = new ArrayList<>();
		search_params = filterbean.getSearch_params();
		String query = "", condition = "";
		String Table_name = "";
	
		
		//1. GET THE HEADERS FROM DB
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { filterbean.getStFile_Name(),filterbean.getStCategory(),filterbean.getStSubCategory() },Integer.class);
 
		logger.info("file_id=="+file_id);
		
		String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);		
		logger.info("stFile_headers=="+stFile_headers);
		
		//ADDING CODE FOR GETTING RAW TABLE NAME FROM DB
		Table_name = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{file_id}, String.class);
		logger.info("Table_name=="+Table_name);
		
		//CHECK WHETHER TABLE IS PRESENT ----- IF NOT THEN CREATE IT
		String CHECK_CATEGORY_TABLE = "SELECT count (*) FROM tab WHERE tname  = '"+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+"'";
		logger.info("CHECK_CATEGORY_TABLE=="+CHECK_CATEGORY_TABLE);
		
		int tableExist = getJdbcTemplate().queryForObject(CHECK_CATEGORY_TABLE, new Object[] { },Integer.class);
		logger.info("tableExist=="+tableExist);
		
		String[] columns = stFile_headers.split(",");
		
		if(tableExist == 0)
		{
			for(int m = 0 ;m <columns.length; m++)
			{
				tableCols = tableCols +","+ columns[m]+" VARCHAR (100 BYTE) ";
			}
			
			//create MAN RAW table
			String Create_query = "CREATE TABLE "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+" ("+tableCols+")";
			logger.info("CREATE QUERY IS "+Create_query);
			getJdbcTemplate().execute(Create_query);
		 
			
		}
		//CHECK FOR SETTLEMENT TABLE
 
		String CHECK_SETTLEMENT = "SELECT count (*) FROM tab WHERE tname  = 'SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()+"'";
		logger.info("CHECK_SETTLEMENT=="+CHECK_SETTLEMENT);
		
		
		
		int tableSettlementExist = getJdbcTemplate().queryForObject(CHECK_SETTLEMENT, new Object[] { },Integer.class);
		logger.info("tableSettlementExist=="+tableSettlementExist);
		
		settlement_col = settlement_col+","+stFile_headers;
	
		String[] settlement_cols =stFile_headers.split(",");
	 
		
		if(tableSettlementExist == 0)
		{
			String settle_col = "";
			if(filterbean.getStFile_Name().equals("CBS"))
			{
				settle_col = "CREATEDDATE DATE, CREATEDBY VARCHAR2(100 BYTE)," +
						"FILEDATE DATE,DCRS_REMARKS VARCHAR(100 BYTE),SEG_TRAN_ID NUMBER, MAN_CONTRA_ACCOUNT VARCHAR(100 BYTE)";
			}
			else
			{
				settle_col = "CREATEDDATE DATE, CREATEDBY VARCHAR2(100 BYTE),FILEDATE DATE,DCRS_REMARKS VARCHAR(100 BYTE),SEG_TRAN_ID NUMBER";
			}
			for(int m = 0 ;m <settlement_cols.length; m++)
			{
				settle_col = settle_col +","+ settlement_cols[m]+" VARCHAR (100 BYTE) ";
			}
			
			
			
			//create MAN RAW table
			String Create_query = "CREATE TABLE SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()+" ("+settle_col+")";
			logger.info("Create_query=="+Create_query);
			getJdbcTemplate().execute(Create_query);
		}
		
		//PREPARING PROPER CONDITION
		String temp_param;
		for(int i = 0; i<search_params.size();i++){
			FilterationBean filterBeanObj = new FilterationBean();
			filterBeanObj = search_params.get(i);
			temp_param = filterBeanObj.getStSearch_header().trim();
			if((filterBeanObj.getStSearch_padding().trim()).equals("Y"))
			{
				if((filterBeanObj.getStSearch_Condition().trim()).equals("="))
				{
					condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
						filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+"'"+filterBeanObj.getStSearch_pattern().trim()+"' ";
				}
				else if((filterBeanObj.getStSearch_Condition().trim()).equalsIgnoreCase("like"))
				{
					condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
							filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+
							"'%"+filterBeanObj.getStSearch_pattern().trim()+"%' ";
				}
				else
				{
					if(i == (search_params.size()-1))
					{
						condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
									filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
					}
					else
					{
						condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
								filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
				}
			}
			else
			{
				if(filterBeanObj.getStSearch_Condition().equals("="))
				{
					condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" '"+
								filterBeanObj.getStSearch_pattern().trim()+"'";
				}
				else if(filterBeanObj.getStSearch_Condition().equalsIgnoreCase("like"))
				{
					condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" "+
								"'%"+filterBeanObj.getStSearch_pattern().trim()+"%'";
				}
				else
				{
					if(i == (search_params.size()-1))
					{
						condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
					}
					else
					{
						condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
				}
				
			}
			
			for(int j= (i+1); j <search_params.size(); j++)
			{
				if(temp_param.equals(search_params.get(j).getStSearch_header()))
				{
						
					if(search_params.get(j).getStSearch_padding().equals("Y"))
					{
						if((search_params.get(j).getStSearch_Condition().trim()).equals("="))
						{
							condition = condition + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
								search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+ 
								"'"+search_params.get(j).getStSearch_pattern().trim()+"'";
						}
						else if((search_params.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
						{
							condition = condition + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
									search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+
									"'%"+search_params.get(j).getStSearch_pattern().trim()+"%'";
						}
						else
						{
							if(j==(search_params.size()-1))
							{	
								 condition = condition + ", '"+search_params.get(j).getStSearch_pattern().trim()+"')";
							}
							else
							{
								condition = condition + ", '"+search_params.get(j).getStSearch_pattern().trim()+"' ";
							}
							
						}
					}
					else
					{
						if((search_params.get(j).getStSearch_Condition().trim()).equals("="))
						{
							condition = condition + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
										search_params.get(j).getStSearch_Condition().trim()+" '"+search_params.get(j).getStSearch_pattern().trim()+"'";
						}
						else if((search_params.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
						{
							condition = condition + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
									search_params.get(j).getStSearch_Condition().trim()+" "+
									"'%"+search_params.get(j).getStSearch_pattern().trim()+"%'";
							
						}
						else
						{
							if(j==(search_params.size()-1))
							{
								condition = condition + " , '" +search_params.get(j).getStSearch_pattern().trim()+"')";
							}
							else
							{
								condition = condition + " , '" +search_params.get(j).getStSearch_pattern().trim()+"' ";
							}
							
						}
					}
						i = j;
				}
				
			}
			if(i != (search_params.size())-1)
			{
				if(search_params.get(i).getStSearch_Condition().equals("!="))
				{
					condition = condition + " ) ) AND ";
				}
				else
					condition = condition +" ) AND ";
			}
			else
			{
				condition = condition +")";
			}
			
		}
		//logger.info("condition=="+condition);
		String insrt_query = "";
		String settlement_query = "";
		if(filterbean.getStFile_Name().equals("CBS") && (filterbean.getStMerger_Category().equals("RUPAY_DOM")||filterbean.getStMerger_Category().equals("VISA_ISS")))
		{
			if(stFile_headers.contains("SEG_TRAN_ID")){
				insrt_query = "insert into tmp_"+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+ "  (CREATEDDATE,CREATEDBY,FILEDATE,"+stFile_headers+")  " +
						" select SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY')," +
						"'"+filterbean.getInseg_tran_id()+"',"+stFile_headers+" from "+Table_name+" where FILEDATE = '"+filterbean.getStFile_date()+"'";

				logger.info("IF SEG_TRAN_ID IS PRESENT =="+insrt_query);
			}else{		
				insrt_query = "insert into tmp_"+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+ "  (CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,"+stFile_headers+")  " +
						" select SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY')," +
						"'"+filterbean.getInseg_tran_id()+"',"+stFile_headers+" from "+Table_name+" where FILEDATE = '"+filterbean.getStFile_date()+"'";
				logger.info("IF SEG_TRAN_ID IS not PRESENT =="+insrt_query);
			}
			logger.info("GET RAW DATA IN TMP "+insrt_query);
			getJdbcTemplate().execute(insrt_query);
		
		
		query = "SELECT SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY')," +
				"'"+filterbean.getInseg_tran_id()+"',"+stFile_headers+" FROM tmp_"+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name();  //"+Table_name;
		
		 settlement_query = " SELECT SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY')," +
				"'"+filterbean.getInseg_tran_id()+"','"+filterbean.getStMerger_Category()+"',"+stFile_headers+" FROM  tmp_"+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name();    //+Table_name;
		}
		else
		{
			query = "SELECT SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY')," +
					"'"+filterbean.getInseg_tran_id()+"',"+stFile_headers+" FROM "+Table_name;  //"+Table_name;
			
			 settlement_query = " SELECT SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY')," +
					"'"+filterbean.getInseg_tran_id()+"','"+filterbean.getStMerger_Category()+"',"+stFile_headers+" FROM  "+Table_name;  
		}
		
		if(!condition.equals(""))
		{
 
			query = query + " WHERE "+condition +" AND FILEDATE = '"+filterbean.getStFile_date()+"'";
			settlement_query = settlement_query +  " WHERE "+condition +" AND FILEDATE = '"+filterbean.getStFile_date()+"'";		
			//query = query + " WHERE "+condition;
			
		}
		else
		{
			// changes by minakshi 10/08/2018 date format replace
			
			query = query + " WHERE  CREATEDDATE = '"+filterbean.getStFile_date()+"'";
			settlement_query = settlement_query + " WHERE CREATEDDATE = '"+filterbean.getStFile_date()+"'";
					
		}
					

		//3. Insert the data in batch obtained from RAW table into category_filename Table
			ADD_RECORDS = " INSERT INTO "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()
					+" (CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,"+stFile_headers+") "+query;
			 
					
			SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()+" ("+settlement_col + ") "+
								settlement_query;		
			 

		
			
			logger.info("start time FOR INSERTING RAW DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
	
				
			logger.info("ADD RECORDS QUERY  is "+ADD_RECORDS);
			getJdbcTemplate().execute(ADD_RECORDS);
			logger.info("SETTLEMENT INSERT QUERY "+SETTLEMENT_INSERT);
			getJdbcTemplate().execute(SETTLEMENT_INSERT);
			
			
			 
			logger.info("End time FOR INSERTING RAW DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
		
			//minakshi changes for ttum knockoff 26/06/2018
			if(filterbean.getStFile_Name().equals("CBS") && filterbean.getStMerger_Category().equals("RUPAY_DOM")){
			boolean result = false;
			
			result = KnockoffTTUMdata(filterbean.getStCategory(), filterbean.getStSubCategory(), filterbean.getStFile_date(),filterbean.getStEntry_by(),filterbean.getInRec_Set_Id() );
			logger.info("result== "+result);
			
			} else if(filterbean.getStFile_Name().equals("CBS") && filterbean.getStMerger_Category().equals("VISA_ISS")){
				boolean result = false;
				
				System.out.println ("**********************************************************************************************************************"+filterbean.getStCategory());
				result = KnockoffVISATTUMdata(filterbean.getStCategory(), filterbean.getStSubCategory(), filterbean.getStFile_date(),filterbean.getStEntry_by(),filterbean.getInRec_Set_Id() );
				logger.info("result== "+result);
				} 
			
			
			if(filterbean.getStFile_Name().equals("CBS"))
			{
				CategorizeProxyAccount(filterbean);
			}
			
			
			String trunc_query = "truncate table tmp_"+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+"";
			
			logger.info("********************TABLE IS TRUNCATING WATCH**********************************");
			logger.info(trunc_query);
			logger.info("******************************************************");
			
	 
			getJdbcTemplate().execute(trunc_query);	
		 
			
					
			logger.info("***** FilterationDaoImpl.filterRecords End ****");
			
			
			return 1;
		
		}
		catch(Exception e)
		{
			//throw new Exception(e);
			demo.logSQLException(e, "FilterationDaoImpl.filterRecords");
			 logger.error(" error in FilterationDaoImpl.filterRecords", new Exception("FilterationDaoImpl.filterRecords",e));
			 //throw e;
			//logger.info("EXCEPTION IN FILTERATION "+e);
			return 0;
		}
		
		
	}
	
	
	public boolean KnockoffTTUMdata(String category,String subCat,String filedate,String entry_by,int rec_set_id) throws ParseException, Exception {
		try {
			logger.info("***** FilterationDaoImpl.KnockoffTTUMdata Start ****");
			String response = null;
			Map<String, Object> inParams = new HashMap<String, Object>();

			inParams.put("I_CATEGORY", category);
			inParams.put("I_SUBCATEGORY",subCat.substring(0, 3) );
			inParams.put("I_REC_SET_ID",rec_set_id );
			inParams.put("I_FILE_DATE",filedate);
			
			KnockoffTTUM knockoffTTUM = new KnockoffTTUM(getJdbcTemplate());
			Map<String, Object> outParams = knockoffTTUM.execute(inParams);

			logger.info("CALL RUPAY_TTUM_KNOCKOFF_PROC() "+outParams.get("msg1"));
			logger.info("***** FilterationDaoImpl.KnockoffTTUMdata End ****");
			
			if (outParams.get("ERROR_MESSAGE") != null) {
						return false;
					}else {
						return true;
					}
			
		} catch (Exception e) {
			demo.logSQLException(e, "FilterationDaoImpl.KnockoffTTUMdata");
			logger.error(" error in  FilterationDaoImpl.KnockoffTTUMdata", new Exception(" FilterationDaoImpl.KnockoffTTUMdata",e));
			return false;
		}
		
	}
	
	class KnockoffTTUM extends StoredProcedure {
		private static final String procName = "Rupay_TTUM_Knockoff_Proc";

		KnockoffTTUM(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);
			
			
			
			declareParameter(new SqlParameter("I_CATEGORY",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_REC_SET_ID",OracleTypes.INTEGER));
			declareParameter(new SqlParameter("I_FILE_DATE",OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE", OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE", OracleTypes.VARCHAR));
			compile();
		}
	}
	
	
	
	// Knockoff of VISA _ISSUER DISPUTE TTUM
	
	
	public boolean KnockoffVISATTUMdata(String category,String subCat,String filedate,String entry_by,int rec_set_id) throws ParseException, Exception {
		try {
			logger.info("***** FilterationDaoImpl.KnockoffVISATTUMdata Start ****");
			String response = null;
			Map<String, Object> inParams = new HashMap<String, Object>();
			
			inParams.put("I_FILE_DATE",filedate);
			inParams.put("I_CATEGORY", category);
			inParams.put("I_SUBCATEGORY",subCat.substring(0, 3) );
			inParams.put("I_REC_SET_ID",rec_set_id );
			
			
			
			
			KnockoffvisaTTUM knockoffTTUM = new KnockoffvisaTTUM(getJdbcTemplate());
			Map<String, Object> outParams = knockoffTTUM.execute(inParams);

			//logger.info("outParams msg1"+outParams.get("msg1"));
			logger.info("***** FilterationDaoImpl.KnockoffVISATTUMdata End ****");
			
			if (outParams.get("ERROR_MESSAGE") != null) {
			

						return false;
					}else {
						
						return true;
					}
				

			
			
		} catch (Exception e) {
			demo.logSQLException(e, "FilterationDaoImpl.KnockoffTTUMdata");
			logger.error(" error in  FilterationDaoImpl.KnockoffTTUMdata", new Exception(" FilterationDaoImpl.KnockoffTTUMdata",e));
			return false;
		}
		
	}
	
	class KnockoffvisaTTUM extends StoredProcedure {
		private static final String procName = "VISA_TTUM_Knockoff_Proc";

		KnockoffvisaTTUM(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);
			
			
			declareParameter(new SqlParameter("I_FILE_DATE",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_REC_SET_ID",OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter("ERROR_CODE", OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE", OracleTypes.VARCHAR));
			compile();
		}
	}
	
	
	
	
	/*@Override
	public int filterRecords(FilterationBean filterbean)throws Exception
	{
		try
		{
			logger.info("START time FOR INSERTING RAW DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { filterbean.getStFile_Name(),filterbean.getStCategory(),filterbean.getStSubCategory() },Integer.class);
			Map<String,Object> inParams = new HashMap<>();
			inParams.put("I_FILE_NAME", filterbean.getStFile_Name());
			inParams.put("I_FILE_ID", file_id);
			inParams.put("I_FILE_DATE", filterbean.getStFile_date());
			inParams.put("I_CATEGORY", filterbean.getStCategory());
			inParams.put("I_SUBCATEGORY", filterbean.getStSubCategory());
			inParams.put("I_MERGER_CATEGORY", filterbean.getStMerger_Category());
			inParams.put("I_SEG_TRAN_ID", filterbean.getInseg_tran_id());
			inParams.put("I_ENTRY_BY", filterbean.getStEntry_by());

			ExecuteInsert execInsert = new ExecuteInsert(getJdbcTemplate());
			Map<String, Object> outParams = execInsert.execute(inParams);
			logger.info("end time FOR INSERTING RAW DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			if (outParams.get("ERROR_MESSAGE") != null) {

				return 0;
			}
			else{
				//GETTING CONTRA ACCOUNT FROM SWITCH FOR PROXY ACCOUNTS
				if(filterbean.getStFile_Name().equals("CBS"))
				{
					CategorizeProxyAccount(filterbean);
				}
				return 1;
			}
			

		}
		catch(Exception e)
		{
			return 0;
		}
	}
	
	private class ExecuteInsert extends StoredProcedure{
		private static final String insert_proc = "GENERIC_CLASSIFICATION";
		public ExecuteInsert(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILE_NAME",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_FILE_ID",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_FILE_DATE",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_MERGER_CATEGORY",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_SEG_TRAN_ID",OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY",OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, OracleTypes.VARCHAR));
			compile();
		}
		
	}
	*/
	
	public void insertproxyBatch(final String QUERY, final ResultSet rset,final String[] columns, final String task)throws Exception
	{
		PreparedStatement ps;
		Connection con = null;
		
	
		int flag = 1;
		int batch = 1;
		
		try {
			
			con = getConnection();
			
			ps = con.prepareStatement(QUERY);
			
			if(task.equals("MATCH"))
			{
				
				while(rset.next())
				{

					flag++;
					ps.setString(1, rset.getString("ACCTNUM"));
					/*if(value == columns.length)
					 value = 3;*/

					for(int i = 1 ; i <= columns.length; i++)
					{
						/*logger.info("column name is "+columns[i-1].trim());
						logger.info("column value is "+rset.getString(columns[i-1].trim()));*/
						ps.setString((i+1), rset.getString(columns[i-1].trim()));


						//	value++;
						//	logger.info("i is "+i);
					}
					
					ps.addBatch();

					if(flag == 100)
					{
						//logger.info("******** FLAG IS "+flag);
						flag = 1;

						ps.executeBatch();
						//logger.info("Executed batch is "+batch);
						batch++;
					}
				}
				ps.executeBatch();
			}
			else
			{
			
				
				while(rset.next())
				{

					flag++;

					for(int i = 1 ; i <= columns.length; i++)
					{
						ps.setString((i), rset.getString(columns[i-1].trim()));


						//	value++;
						//	logger.info("i is "+i);
					}
					//logger.info("hey");
					ps.addBatch();

					if(flag == 500)
					{
						//logger.info("******** FLAG IS "+flag);
						flag = 1;

						ps.executeBatch();
						//logger.info("Executed batch is "+batch);
						batch++;
					}
				}
				ps.executeBatch();
			}
			//logger.info("completed insertion");
			
			
		} catch (DataAccessException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(con != null)
				{
					con.close();
				}
			}
			catch(Exception e)
			{
				demo.logSQLException(e, "FilterationDaoImpl.insertproxyBatch");
				throw new Exception("Exception in insert batch");
			}
		}
	}
	
	/*public void insertBatch(String QUERY, final ResultSet rset,final String[] columns,final int seg_tran_id){
		PreparedStatement ps;
		Connection con;
		//logger.info("query is 123 "+ADD_RECORDS);
		//logger.info("coulmn size is "+columns.length);
//		trns_srl = Integer.parseInt(new SimpleDateFormat("dd").format(cur_dt)) * Integer.parseInt(new SimpleDateFormat("MM").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("yyyy").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("ss").format(cur_dt));
		
		//for(int i=0; i<loadBeanList.size(); i += batchSize)
		int flag = 1;
		int batch = 1;
		
		try {
			
			con = getConnection();
			
			ps = con.prepareStatement(QUERY);
			
			//logger.info("value is "+rset.getString("MSGTYPE"));
			//int batch = 1;
			//int value = 3;
			while(rset.next())
			{
				flag++;
				ps.setInt(1, seg_tran_id);
				 if(value == columns.length)
					 value = 3;
				
				for(int i = 1 ; i <= columns.length; i++)
				{
					
					
					
					
				//	logger.info("column name is "+columns[i-1].trim());
				//	logger.info("column value is "+rset.getString(columns[i-1]).trim());
					ps.setString((i+1), rset.getString(columns[i-1].trim()));
				//	value++;
				//	logger.info("i is "+i);
				}
				ps.addBatch();
				
				if(flag == 20000)
				{
					flag = 1;
				
					ps.executeBatch();
					logger.info("Executed batch is "+batch);
					batch++;
				}
			}
			ps.executeBatch();
			
			
		} catch (DataAccessException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	/*public void insertBatch(String QUERY, final ResultSet rset,final String[] columns,final int seg_tran_id){
		PreparedStatement ps;
		Connection con;
		//logger.info("query is 123 "+ADD_RECORDS);
		//logger.info("coulmn size is "+columns.length);
//		trns_srl = Integer.parseInt(new SimpleDateFormat("dd").format(cur_dt)) * Integer.parseInt(new SimpleDateFormat("MM").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("yyyy").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("ss").format(cur_dt));
		
		//for(int i=0; i<loadBeanList.size(); i += batchSize)
		int flag = 1;
		int batch = 1;
		String values=null;
		try {
			
			con = getConnection();
			
			ps = con.prepareStatement(QUERY);
			
			//logger.info("value is "+rset.getString("MSGTYPE"));
			//int batch = 1;
			//int value = 3;
			while(rset.next())
			{
				flag++;
				ps.setInt(1, seg_tran_id);
				 if(value == columns.length)
					 value = 3;
				
				for(int i = 1 ; i <= columns.length; i++)
				{
					//logger.info("column name is "+columns[i-1].trim());
					if((rset.getString(columns[i-1].trim()))==null)
					{
						 values="";
					}
					else
					{
						 values=rset.getString(columns[i-1].trim());
					}
					logger.info(i);
					logger.info("column name is "+columns[i-1].trim());
					logger.info("column value is "+rset.getString(columns[i-1]));
					ps.setString((i+1), values.trim());
				//	value++;
				//	logger.info("i is "+i);
				}
				ps.addBatch();
				
				if(flag == 500)
				{
					flag = 1;
				
					ps.executeBatch();
					//logger.info("Executed batch is "+batch);
					batch++;
				}
			}
			ps.executeBatch();
			
			
		} catch (DataAccessException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	public int updateseg_txn(FilterationBean filterbean)throws Exception
	{
		logger.info("***** FilterationDaoImpl.updateseg_txn Start ****");
		int updated = 0;
	
		try
		{
			//updated  = getJdbcTemplate().queryForObject(UPDATE_SEG_RECORD, new Object[]{ filterbean.getInseg_tran_id() },Integer.class);
			String query="UPDATE MAIN_SEGREGATE_TRANSACTION SET STATUS = 'Y' WHERE SEG_TRAN_ID = "+filterbean.getInseg_tran_id();
			logger.info("query=="+query);
			
			updated =  getJdbcTemplate().update(query);
			
			logger.info("updated== "+updated);
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "FilterationDaoImpl.updateseg_txn");
			logger.error(" error in FilterationDaoImpl.updateseg_txn", new Exception("FilterationDaoImpl.updateseg_txn",e));
			throw e;
		}
		logger.info("***** FilterationDaoImpl.updateseg_txn End ****");
		return updated;
	}
	
	@Override
	public int getTrnId(FilterationBean filterBeanObj)throws Exception
	{
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { filterBeanObj.getStFile_Name(),filterBeanObj.getStCategory(),filterBeanObj.getStSubCategory()},Integer.class);
		//logger.info("file Id is "+file_id);
		
	//	logger.info("check the time here "+new java.sql.Timestamp(new java.util.Date().getTime()));
		Date date = new Date();
		//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	//	logger.info((dateFormat.format(new Date()))); 
		
		GET_TRN_ID = GET_TRN_ID + " AND FILTERSTATUS = 'N'";
	//	logger.info("TRN QUERY IS "+GET_TRN_ID);
		
		int trnid = getJdbcTemplate().queryForObject(GET_TRN_ID, new Object[]{file_id,dateFormat.format(new Date())},Integer.class);
		return trnid;
	}
	
	/*public static void main(String args[])
	{
		logger.info("HIE");
		FilterationBean filterBean = new FilterationBean();
		filterBean.setStCategory("ONUS");
		filterBean.setStFile_Name("CBS");
		FilterationDaoImpl obj = new FilterationDaoImpl();
		obj.getTrnId(filterBean);
	}*/
	
	@Override
	public String getStatus(FilterationBean filterBean,String stProcess) throws Exception
	{
		logger.info("***** FilterationDaoImpl.getStatus Start ****");	
		try
		{
		
			String GET_STATUS = "SELECT "+stProcess+" FROM MAIN_FILESOURCE WHERE FILEID = ?";
			
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { filterBean.getStFile_Name(),filterBean.getStCategory(),filterBean.getStSubCategory() },Integer.class);
			
			String chFilter_Status = getJdbcTemplate().queryForObject(GET_STATUS, new Object[] {file_id}, String.class);
			
			logger.info("chFilter_Status=="+chFilter_Status);
			
			logger.info("***** FilterationDaoImpl.getStatus End ****");	
			
			return chFilter_Status;
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "FilterationDaoImpl.getStatus");
			logger.error(" error in FilterationDaoImpl.getStatus", new Exception("FilterationDaoImpl.getStatus",e));
			return "N";
			
		}
		
	}
	
	
	public void CategorizeProxyAccount(FilterationBean filterBean) throws Exception
	{
		logger.info("***** FilterationDaoImpl.CategorizeProxyAccount Start ****");	
		
		String table1_condition = "";
		String table2_condition = "";
		String condition = "";
		String temp_param= "";
		String GET_FROM_RAW = "";
		int table1_file_id = 0;
		try
		{
			
			//ALTER TABLE FOR MAN_CONTRA_ACCOUNT
			
		//	String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'ONUS_CBS'";
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = '"+filterBean.getStMerger_Category()+"_CBS'";
			int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			
			logger.info("CHECK TABLE QUERY IS "+CHECK_TABLE);
			if(tableExist == 1)
			{
				boolean CheckForCol = false;
				//now check for the field MAN_CONTRA_ACCOUNT IF NOT PRESENT THEN ALTER TABLE
				List<String> Columns = getJdbcTemplate().query(GET_COLS, new Object[]{filterBean.getStMerger_Category()+"_CBS"}, new ColumnsMapper());
				
				for(int i = 0 ;i<Columns.size(); i++)
				{
					if(Columns.get(i).equalsIgnoreCase("MAN_CONTRA_ACCOUNT"))
					{
						CheckForCol = true;
					}
				}
				
				if(!CheckForCol)
				{
					String ALTER_QUERY = "ALTER TABLE "+filterBean.getStMerger_Category()+"_CBS ADD MAN_CONTRA_ACCOUNT VARCHAR2(100 BYTE)";
					getJdbcTemplate().execute(ALTER_QUERY);
				}
			}
			
			if(filterBean.getStSubCategory().equals("SURCHARGE"))
			{
				if(filterBean.getStCategory().equals("RUPAY"))
				{
					table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { "SWITCH",filterBean.getStCategory(),"DOMESTIC"},Integer.class);
				}
				else if(filterBean.getStCategory().equals("VISA"))
				{
					table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { "SWITCH",filterBean.getStCategory(),"ISSUER"},Integer.class);
				}
					
			}
			else
			{	
				table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { "SWITCH",filterBean.getStCategory(),filterBean.getStSubCategory() },Integer.class);
			}
			int table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { "CBS",filterBean.getStCategory(),filterBean.getStSubCategory() },Integer.class);
			
			String stRaw_TableName = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{table2_file_id}, String.class);
			
			/*if(filterBean.getStCategory().equals("ONUS")) {
				
				if(table2_file_id==1) {
					
					stRaw_TableName = "TMP_ONUS_SWITCH";
					
				} if(table2_file_id==2) {
					
					stRaw_TableName="TMP_ONUS_CBS;
				}
			}*/
			
			logger.info("raw table name is "+stRaw_TableName);
			
			String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);
			
			
			//stRaw_TableName = 	stRaw_TableName.equals( "CBS_RUPAY_RAWDATA" ) ? "TMP_RUPAY_DOM_CBS" : "CBS_RUPAY_RAWDATA" ;  
	 
		
			List<Integer> id_list = getJdbcTemplate().query(GET_MAN_ID , new Object[] {table2_file_id}, new ManualId());
			
			
			
			

			for(int i =0; i<id_list.size() ; i++)
			{
				condition = "";
				logger.info("man id is "+id_list.get(i));
				List<ManualFileBean> manual_filter = getJdbcTemplate().query(GET_MANUAL_PARAM, new Object[] {table2_file_id, id_list.get(i)}, new ManualParameterMaster());
				
				//GET CONDITION FOR SPLITING THE MANUAL RECORDS
				for(int j = 0; j<manual_filter.size();j++){
					ManualFileBean manualFileBeanObj = new ManualFileBean();
					manualFileBeanObj = manual_filter.get(j);
					 
					temp_param = manualFileBeanObj.getStFile_header();
					if((manualFileBeanObj.getStPadding().trim()).equals("Y"))
					{
						if((manualFileBeanObj.getStCondition().trim()).equals("="))
						{
							condition = condition + "(SUBSTR(t2."+manualFileBeanObj.getStFile_header().trim()+","+manualFileBeanObj.getStStart_charpos()+","+manualFileBeanObj.getStChar_size()+") "+manualFileBeanObj.getStCondition().trim()+"'"+manualFileBeanObj.getStSearch_Pattern().trim()+"' ";
						}
						else if((manualFileBeanObj.getStCondition().trim()).equalsIgnoreCase("like"))
						{
							condition = condition + "(SUBSTR(t2."+manualFileBeanObj.getStFile_header().trim()+","+manualFileBeanObj.getStStart_charpos()+","+
									manualFileBeanObj.getStChar_size()+") "+manualFileBeanObj.getStCondition().trim()+
									"'%"+manualFileBeanObj.getStSearch_Pattern().trim()+"%' ";
						}
						else
						{
							condition = condition + "(SUBSTR(t2."+manualFileBeanObj.getStFile_header().trim()+","+manualFileBeanObj.getStStart_charpos()+","+
									manualFileBeanObj.getStChar_size()+") "+"NOT IN ('"+manualFileBeanObj.getStSearch_Pattern().trim()+"' ";					
						}
					}
					else
					{
						if(manualFileBeanObj.getStCondition().equals("="))
						{
							condition = condition + "(t2."+manualFileBeanObj.getStFile_header().trim()+" "+manualFileBeanObj.getStCondition().trim()+" '"+
									manualFileBeanObj.getStSearch_Pattern().trim()+"'";
						}
						else if(manualFileBeanObj.getStCondition().equalsIgnoreCase("like"))
						{
							condition = condition + "(t2."+manualFileBeanObj.getStFile_header().trim()+" "+manualFileBeanObj.getStCondition().trim()+" "+
										"'%"+manualFileBeanObj.getStSearch_Pattern().trim()+"%'";
						}
						else
						{
							condition = condition + "(t2."+manualFileBeanObj.getStFile_header().trim()+" NOT IN ('"+manualFileBeanObj.getStSearch_Pattern().trim()+"' ";
						}
						
					}
					
					for(int k= (j+1); k <manual_filter.size(); k++)
					{
						//logger.info("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
						if(temp_param.equals(manual_filter.get(k).getStFile_header()))
						{
								
							if(manual_filter.get(k).getStPadding().equals("Y"))
							{
								if((manual_filter.get(k).getStCondition().trim()).equals("="))
								{
									condition = condition + " OR SUBSTR(t2." + manual_filter.get(k).getStFile_header().trim()+" , "+manual_filter.get(k).getStStart_charpos()+","+
											manual_filter.get(k).getStChar_size()+") "+manual_filter.get(k).getStCondition().trim()+ 
										"'"+manual_filter.get(k).getStSearch_Pattern().trim()+"'";
								}
								else if((manual_filter.get(k).getStCondition().trim()).equalsIgnoreCase("like"))
								{
									condition = condition + " OR SUBSTR(t2." + manual_filter.get(k).getStFile_header().trim()+" , "+manual_filter.get(k).getStStart_charpos()+","+
											manual_filter.get(k).getStChar_size()+") "+manual_filter.get(k).getStCondition().trim()+
											"'%"+manual_filter.get(k).getStSearch_Pattern().trim()+"%'";
								}
								else
								{
									if(j==(manual_filter.size()-1))
									{	
										/*condition = condition + " OR SUBSTR(" + search_params.get(j).getStSearch_header()+" , "+search_params.get(j).getStsearch_Startcharpos()+","+
											search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition()+ search_params.get(j).getStSearch_pattern();*/
										condition = condition + ", '"+manual_filter.get(k).getStSearch_Pattern().trim()+"')";
									}
									else
									{
										condition = condition + ", '"+manual_filter.get(k).getStSearch_Pattern().trim()+"' ";
									}
									
								}
							}
							else
							{
								if((manual_filter.get(j).getStCondition().trim()).equals("="))
								{
									condition = condition + " OR t2." + manual_filter.get(k).getStFile_header()+" "+
											manual_filter.get(k).getStCondition().trim()+" '"+manual_filter.get(k).getStSearch_Pattern().trim()+"'";
								}
								else if((manual_filter.get(k).getStCondition().trim()).equalsIgnoreCase("like"))
								{
									condition = condition + " OR t2." + manual_filter.get(k).getStFile_header()+" "+
											manual_filter.get(k).getStCondition().trim()+" "+
											"'%"+manual_filter.get(k).getStSearch_Pattern().trim()+"%'";
									
								}
								else
								{
									if(j==(manual_filter.size()-1))
									{
										condition = condition + " , '" +manual_filter.get(k).getStSearch_Pattern().trim()+"')";
									}
									else
									{
										condition = condition + " , '" +manual_filter.get(k).getStSearch_Pattern().trim()+"' ";
									}
									
								}
							}
							
							
								j = k;
						}
						
					}
					//logger.info("i value is "+i);
					if(j != (manual_filter.size())-1)
					{
						condition = condition +" ) AND ";
					}
					else
						condition = condition +")";
					
					
					
				//	logger.info("condition is "+condition);
				}
				
				
				logger.info("DELETE ALREADY PRESENT CONTRA ACCOUNT RECORDS--------------------------------------------");
				//String DELETE_QUERY = "DELETE FROM "+filterBean.getStMerger_Category()+"_CBS" +" t2 WHERE "+condition;
				//NEW QUERY FOR DELETION
				//Changes done by sushant as per mail on 11/mar/2019 
				String DELETE_QUERY = "DELETE   FROM "+filterBean.getStMerger_Category()+"_CBS" +" t2 WHERE EXISTS (";
				
				
			
				String SETTLEMENT_DELETE_QUERY = "DELETE    FROM SETTLEMENT_"+filterBean.getStCategory()+"_CBS t2 WHERE DCRS_REMARKS = '" 
						+filterBean.getStMerger_Category()+"'"+" AND t2.FILEDATE = '"+filterBean.getStFile_date()
						+ "' AND EXISTS (";
				
				String stdelete_Condtion = "";
				
		
				
				
				if(!condition.equals(""))
				{
					condition = condition +" AND ";
									
				}
				
				
				//CONDITION FOR FILE ID 1
				List<ManualFileBean> manual_filter2 = getJdbcTemplate().query(GET_MANUAL_PARAM, new Object[] {table1_file_id, id_list.get(i)}, new ManualParameterMaster());
				String condition1 = "";
				for(int j = 0; j<manual_filter2.size();)
				{
					ManualFileBean manualFileBeanObj = new ManualFileBean();
					manualFileBeanObj = manual_filter2.get(j);
					//temp_param = filterBeanObj.getStSearch_header().trim();
					temp_param = manualFileBeanObj.getStFile_header();
					if((manualFileBeanObj.getStPadding().trim()).equals("Y"))
					{
						if((manualFileBeanObj.getStCondition().trim()).equals("="))
						{
							condition = condition + "(SUBSTR(t1."+manualFileBeanObj.getStFile_header()+","+manualFileBeanObj.getStStart_charpos()+","+
									manualFileBeanObj.getStChar_size()+") "+manualFileBeanObj.getStCondition().trim()+"'"+manualFileBeanObj.getStSearch_Pattern().trim()+"' ";
						}
						else if((manualFileBeanObj.getStCondition().trim()).equalsIgnoreCase("like"))
						{
							condition = condition + "(SUBSTR(t1."+manualFileBeanObj.getStFile_header()+","+manualFileBeanObj.getStStart_charpos()+","+
									manualFileBeanObj.getStChar_size()+") "+manualFileBeanObj.getStCondition().trim()+
									"'%"+manualFileBeanObj.getStSearch_Pattern().trim()+"%' ";
						}
						else
						{
							condition = condition + "(SUBSTR(t1."+manualFileBeanObj.getStFile_header()+","+manualFileBeanObj.getStStart_charpos()+","+
									manualFileBeanObj.getStChar_size()+") "+"NOT IN ('"+manualFileBeanObj.getStSearch_Pattern().trim()+"' ";					
						}
					}
					else
					{
						if(manualFileBeanObj.getStCondition().equals("="))
						{
							condition = condition + "(t1."+manualFileBeanObj.getStFile_header()+" "+manualFileBeanObj.getStCondition().trim()+" '"+
									manualFileBeanObj.getStSearch_Pattern().trim()+"'";
						}
						else if(manualFileBeanObj.getStCondition().equalsIgnoreCase("like"))
						{
							condition = condition + "(t1."+manualFileBeanObj.getStFile_header()+" "+manualFileBeanObj.getStCondition().trim()+" "+
										"'%"+manualFileBeanObj.getStSearch_Pattern().trim()+"%'";
						}
						else
						{
							condition = condition + "(t1."+manualFileBeanObj.getStFile_header()+" "+" NOT IN ('"+manualFileBeanObj.getStSearch_Pattern().trim()+"' ";
						}
						
					}
					
					for(int k = (j+1); k <manual_filter2.size();k++)
					{
						if((temp_param.equals(manual_filter2.get(k).getStFile_header()))
							&& manualFileBeanObj.getStCondition().equals(manual_filter2.get(k).getStCondition()))
						{
							if(manual_filter2.get(k).getStPadding().equals("Y"))
							{
								if((manual_filter2.get(k).getStCondition().trim()).equals("="))
								{
									condition = condition + " OR SUBSTR(t1." + manual_filter2.get(k).getStFile_header()+" , "+manual_filter2.get(k).getStStart_charpos()+","+
											manual_filter2.get(k).getStChar_size()+") "+manual_filter2.get(k).getStCondition().trim()+ 
										"'"+manual_filter2.get(k).getStSearch_Pattern().trim()+"'";
								}
								else if((manual_filter2.get(k).getStCondition().trim()).equalsIgnoreCase("like"))
								{
									condition = condition + " OR SUBSTR(t1." + manual_filter2.get(k).getStFile_header()+" , "+manual_filter2.get(k).getStStart_charpos()+","+
											manual_filter2.get(k).getStChar_size()+") "+manual_filter2.get(k).getStCondition().trim()+
											"'%"+manual_filter2.get(k).getStSearch_Pattern().trim()+"%'";
								}
								else
								{
									if(j==(manual_filter2.size()-1))
									{	
									
										condition = condition + ", '"+manual_filter2.get(k).getStSearch_Pattern().trim()+"')";
									}
									else
									{
										condition = condition + ", '"+manual_filter2.get(k).getStSearch_Pattern().trim()+"' ";
									}
									
								}
							}
							else
							{
								if((manual_filter2.get(j).getStCondition().trim()).equals("="))
								{
									condition = condition + " OR t1." + manual_filter2.get(k).getStFile_header()+" "+
											manual_filter2.get(k).getStCondition().trim()+" '"+manual_filter2.get(k).getStSearch_Pattern().trim()+"'";
								}
								else if((manual_filter2.get(k).getStCondition().trim()).equalsIgnoreCase("like"))
								{
									condition = condition + " OR t1." + manual_filter2.get(k).getStFile_header()+" "+
											manual_filter2.get(k).getStCondition().trim()+" "+
											"'%"+manual_filter2.get(k).getStSearch_Pattern().trim()+"%'";
									
								}
								else
								{
									if(j==(manual_filter2.size()-1))
									{
										condition = condition + " , '" +manual_filter2.get(k).getStSearch_Pattern().trim()+"')";
									}
									else
									{
										condition = condition + " , '" +manual_filter2.get(k).getStSearch_Pattern().trim()+"' ";
									}
									
								}
							}
							manual_filter2.remove(k);
							k = k-1;
							
						}
					}
					
					if((manual_filter2.get(j).getStCondition().trim()).equals("!="))
					{
						condition = condition + " ) ) AND  ";
					}
					else
						condition = condition + " ) AND ";
					
				//	logger.info("condition is "+condition);
					if(!condition1.equals(""))
					{
							condition = condition + condition1 + " ) AND";
					}
					condition1 = "";
					manual_filter2.remove(j);
				}
				
				
				
				
				
				
				if(!condition.equals(""))
				{
					stdelete_Condtion = condition ;
					condition = condition + " t2.PART_ID = 1 AND ";
					
				}
				else
				{
					condition = " t2.PART_ID = 1 AND ";
				}
			
				
				logger.info("condition=="+condition);
				logger.info("stdelete_Condtion=="+stdelete_Condtion);
				
				//PREPARE COMPARE CONDITION
				List<CompareBean> match_Headers1 = getJdbcTemplate().query(GET_MAN_MATCHING , new Object[]{id_list.get(i),table1_file_id},new MatchParameterMaster1());
				List<CompareBean> match_Headers2 = getJdbcTemplate().query(GET_MAN_MATCHING , new Object[]{id_list.get(i),table2_file_id},new MatchParameterMaster2());
			
				//prepare compare condition
				for(int l = 0; l<match_Headers1.size() ; l++)
				{
					//logger.info("DATAT TYPE IS "+match_Headers1.get(l).getStMatchTable1_Datatype());
					//CHECKING PADDING FOR TABLE 1
					
					
					if(match_Headers1.get(l).getStMatchTable1_Padding().trim().equals("Y"))
					{
						if(match_Headers1.get(l).getStMatchTable1_Datatype() != null)
						{
							if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("NUMBER"))
							{
								table1_condition = "TO_NUMBER(SUBSTR(REPLACE(t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+",',','')"+","
										+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+	match_Headers1.get(l).getStMatchTable1_charSize()+"))";
							}
							else if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("DATE"))
							{
								table1_condition = "TO_CHAR(TO_DATE(SUBSTR( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(l).getStMatchTable1_charSize()+")"+", ' "+match_Headers1.get(l).getStMatchTable1_DatePattern()+" '),'DD/MM/YYYY')";
							}
							else if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("TIME"))
							{
								//check whether the column consists of :
								String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(l).getStMatchTable1_charSize()+" ) FROM "+filterBean.getStMerger_Category()+"_SWITCH" 
										+" WHERE  SUBSTR( "+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(l).getStMatchTable1_charSize()+" ) IS NOT NULL";
								logger.info("CHECK FORMAT IS "+CHECK_FORMAT);
								boolean is_colon = false;
								Connection con = getConnection();
								PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
								ResultSet rs = ps.executeQuery();
								if(rs.next())
								{
									if(rs.getString(1).contains(":"))
									{
										is_colon = true;
									}
										
								}
								
								if(is_colon)
								{
									table1_condition = "REPLACE( SUBSTR( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(l).getStMatchTable1_charSize()+")"+" , ':')";
									
								}
								else
								{
									/*table1_condition = " LPAD( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(i).getStMatchTable1_charSize()+")"+","+6+",'0')";*/
									table1_condition = " SUBSTR( LPAD(t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+",6,'0')"+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(l).getStMatchTable1_charSize()+")";
									
								}
								
								
							}
						}
						else
						{
							table1_condition = "SUBSTR( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(l).getStMatchTable1_charSize()+")";
		
						}	
					}
					else
					{	
						//logger.info("DATATYPE IN ELSE IS "+match_Headers1.get(l).getStMatchTable1_Datatype());
						if(match_Headers1.get(l).getStMatchTable1_Datatype()!=null)
						{
							if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("NUMBER"))
							{
								//table1_condition = " TO_NUMBER( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'9999999999.99')";
								table1_condition = " TO_NUMBER( REPLACE(t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+",',',''))";
							}
							else if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("DATE"))
							{
								table1_condition = " TO_CHAR(TO_DATE( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+",'"+match_Headers1.get(l).getStMatchTable1_DatePattern()+"'),'DD/MM/YYYY')";						
							}
							else if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("TIME"))
							{
								//check whether the column consists of :
								String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(l).getStMatchTable1_header().trim()+" FROM "+filterBean.getStMerger_Category()+
										"_SWITCH WHERE "+match_Headers1.get(l).getStMatchTable1_header().trim()+" IS NOT NULL";
								logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
								boolean is_colon = false;
								Connection con = getConnection();
								PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
								ResultSet rs = ps.executeQuery();
								if(rs.next())
								{
									if(rs.getString(1).contains(":"))
									{
										is_colon = true;
									}
										
								}
								
								if(is_colon)
								{
									table1_condition = "REPLACE( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+" , ':')";
									
								}
								else
								{
									table1_condition = " LPAD( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+","+6+",'0')";
									
								}
								
							}
							
						}
						else
						{
							table1_condition = " t1."+match_Headers1.get(l).getStMatchTable1_header().trim();
		
						}	
					}
					
					logger.info("table1_condition=="+table1_condition);
					//CHECKING PADDING FOR TABLE 2
					//logger.info("i value is "+i);
					//logger.info("match headers is "+match_Headers2.get(l).getStMatchTable2_header());
					//logger.info("padding in match headers 2 is "+match_Headers2.get(l).getStMatchTable2_Padding());
					if(match_Headers2.get(l).getStMatchTable2_Padding().trim().equals("Y"))
					{
					//	logger.info("DATATYPE OF 2 IN IF IS "+match_Headers1.get(l).getStMatchTable1_Datatype());
						if(match_Headers2.get(l).getStMatchTable2_Datatype()!=null)
						{
							if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("NUMBER"))
							{
								table2_condition = " TO_NUMBER(SUBSTR(REPLACE(t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+",',','')"+","
												+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+ match_Headers2.get(l).getStMatchTable2_charSize()+"))";
							}
							else if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("DATE"))
							{
								table2_condition = " TO_CHAR(TO_DATE( SUBSTR(  t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+","+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(l).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(l).getStMatchTable2_DatePattern()+"'),'DD/MM/YYYY')";							
							}
							else if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("TIME"))
							{
								//check whether the column consists of :
								String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(l).getStMatchTable2_header().trim()+","+
								match_Headers2.get(l).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(l).getStMatchTable2_charSize()+" ) FROM "
										+filterBean.getStMerger_Category()+"_CBS WHERE SUBSTR( "+match_Headers2.get(l).getStMatchTable2_header().trim()+","+
								match_Headers2.get(l).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(l).getStMatchTable2_charSize()+" ) IS NOT NULL";
								logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
								boolean is_colon = false;
								Connection con = getConnection();
								PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
								ResultSet rs = ps.executeQuery();
								if(rs.next())
								{
									if(rs.getString(1).contains(":"))
									{
										is_colon = true;
									}
										
								}
								
								if(is_colon)
								{
									//ABC
									table2_condition = "REPLACE( SUBSTR( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+","+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(l).getStMatchTable2_charSize()+")"+" , ':')";
									
								}
								else
								{
							
									table2_condition = " SUBSTR( LPAD(t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+",6,'0')"+","+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(l).getStMatchTable2_charSize()+")";
									
								}
								
							}
							
						}
						else
						{
							table2_condition = " SUBSTR( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+","+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(l).getStMatchTable2_charSize()+")";
		
						}			
						
					}
					else
					{
						//logger.info("datatype OF 2 IN is "+match_Headers2.get(l).getStMatchTable2_Datatype());
						if(match_Headers2.get(l).getStMatchTable2_Datatype()!=null)
						{
							if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("NUMBER"))
							{
								//table2_condition = " TO_NUMBER( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'9999999999.99')";
								table2_condition = " TO_NUMBER( REPLACE(t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+",',',''))";
							}
							else if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("DATE"))
							{
								table2_condition = " TO_CHAR(TO_DATE( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+",'"+match_Headers2.get(l).getStMatchTable2_DatePattern()+"'),'DD/MM/YYYY')";							
							}
							else if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("TIME"))
							{
								//check whether the column consists of :
								String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(l).getStMatchTable2_header().trim()+" FROM "
										+filterBean.getStMerger_Category()+
										"_CBS WHERE "+match_Headers2.get(l).getStMatchTable2_header().trim()+" IS NOT NULL";
								logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
								boolean is_colon = false;
								Connection con = getConnection();
								PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
								ResultSet rs = ps.executeQuery();
								if(rs.next())
								{
									if(rs.getString(1).contains(":"))
									{
										is_colon = true;
									}
										
								}
								
								if(is_colon)
								{
									table2_condition = "REPLACE( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+" , ':')";
									
								}
								else
								{
									table2_condition = "LPAD( t2."+match_Headers2.get(l).getStMatchTable2_header()+","+6+",'0')";
									
								}
								
							}
						}
						else
						{
							table2_condition = " t2."+match_Headers2.get(l).getStMatchTable2_header();
		
						}		
					
					}
					
					// PREPARING ACTUAL CONDITION OF BOTH TABLES
					logger.info("MATCH_HEADERS1 SIZE IS "+match_Headers1.size());
					if(l==(match_Headers1.size()-1))
					{
						
						//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header();
						condition = condition + table1_condition + " = "+table2_condition;
						stdelete_Condtion = stdelete_Condtion + table1_condition + " = "+table2_condition;
						
					}
					else
					{
						//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header()+" AND ";
						condition = condition +" ("+ table1_condition +" = "+table2_condition +") AND ";
						stdelete_Condtion = stdelete_Condtion + "( "+table1_condition + " = "+table2_condition +" ) AND ";
					
					}
					
					
				}
				
				logger.info("table2_condition=="+table2_condition);
				String sel_query = "";
				
				if(!condition.equals(""))
				{
					if(filterBean.getStSubCategory().equals("SURCHARGE") && filterBean.getStCategory().equals("RUPAY"))
					{
					GET_FROM_RAW = "SELECT /*+ USE_HASH(t2,t1) */  DISTINCT * FROM "+stRaw_TableName+" t2 , SETTLEMENT_"+filterBean.getStCategory()+"_SWITCH t1 WHERE "+condition
								+" AND T2.FILEDATE = T1.FILEDATE and T2.FILEDATE = '" +filterBean.getStFile_date()+"' " +
										" AND T1.DCRS_REMARKS = 'RUPAY_SUR' AND T1.MSGTYPE NOT IN ('430' , '410')";
				
						sel_query = " SETTLEMENT_"+filterBean.getStCategory()+"_SWITCH t1 WHERE "+stdelete_Condtion
								+" AND T1.MSGTYPE NOT IN ('430' , '410') AND T1.DCRS_REMARKS = 'RUPAY_SUR' ) AND T2.FILEDATE = '" +filterBean.getStFile_date()+"' ";
						//Changes done by sushant as per mail on 11/mar/2019 
						/*DELETE_QUERY = DELETE_QUERY +  "SELECT * FROM "+ sel_query;
						
					SETTLEMENT_DELETE_QUERY = SETTLEMENT_DELETE_QUERY + " SELECT * FROM SETTLEMENT_"+filterBean.getStCategory()+"_SWITCH t1 WHERE T1.DCRS_REMARKS = 'RUPAY_SUR' AND "+stdelete_Condtion
								+" AND T1.MSGTYPE NOT IN ('430' , '410')) AND T2.FILEDATE = '" +filterBean.getStFile_date()+"' ";*/
						
					}
					else if(filterBean.getStCategory().equals("VISA") ) // && filterBean.getStSubCategory().equals("SURCHARGE")
					{
						
						GET_FROM_RAW = "SELECT /*+ USE_HASH(t2,t1) */  DISTINCT * FROM "+stRaw_TableName+" t2 , SETTLEMENT_"+filterBean.getStCategory()+"_SWITCH t1 WHERE "+condition
								+" AND T2.FILEDATE = '" +filterBean.getStFile_date()+"' AND T1.FILEDATE ='"+filterBean.getStFile_date()
								+"' AND T1.DCRS_REMARKS = 'VISA_SUR' AND T1.MSGTYPE NOT IN ('430' , '410')";
						
						sel_query = "  SETTLEMENT_"+filterBean.getStCategory()+"_SWITCH t1 WHERE "+stdelete_Condtion
						+" AND T1.MSGTYPE NOT IN ('430' , '410')  AND T1.DCRS_REMARKS = 'VISA_SUR') AND T2.FILEDATE = '" +filterBean.getStFile_date()+"'";
						//Changes done by sushant as per mail on 11/mar/2019 
						/*DELETE_QUERY = DELETE_QUERY +  " SELECT * FROM "+sel_query;
						
						SETTLEMENT_DELETE_QUERY = SETTLEMENT_DELETE_QUERY + " SELECT * FROM SETTLEMENT_"+filterBean.getStCategory()+"_SWITCH t1 WHERE T1.DCRS_REMARKS = 'VISA_SUR'  AND "+stdelete_Condtion
								+" AND T1.MSGTYPE NOT IN ('430' , '410')) AND T2.FILEDATE = '" +filterBean.getStFile_date()+"'";*/
					
					}
					else
					{
					
						GET_FROM_RAW = "SELECT /*+ USE_HASH(t2,t1) */  DISTINCT * FROM "+stRaw_TableName+" t2 , SETTLEMENT_"+filterBean.getStCategory()+"_SWITCH t1 WHERE "+condition
								+" AND T2.FILEDATE =  T1.FILEDATE and T2.FILEDATE = '" +filterBean.getStFile_date()+"'  "
								+"  AND T1.DCRS_REMARKS = '"+filterBean.getStMerger_Category()+"' AND T1.MSGTYPE NOT IN ('430' , '410')";
						
						sel_query =  "  SETTLEMENT_"+filterBean.getStCategory()+"_SWITCH t1 WHERE  t1.FILEDATE =  to_date('"+filterBean.getStFile_date()+"','DD-MON-RRRR')-1 AND "+stdelete_Condtion
								+" AND T1.MSGTYPE NOT IN ('430' , '410')) AND T2.FILEDATE = '" +filterBean.getStFile_date()+"'";
						
						//Changes done by sushant as per mail on 11/mar/2019 
						
						DELETE_QUERY = DELETE_QUERY + " SELECT * FROM "+sel_query;
						
						SETTLEMENT_DELETE_QUERY = SETTLEMENT_DELETE_QUERY + " SELECT * FROM SETTLEMENT_"+filterBean.getStCategory()+"_SWITCH t1 WHERE "+stdelete_Condtion
								+" AND T1.DCRS_REMARKS = '"+filterBean.getStMerger_Category()+"' AND T1.MSGTYPE NOT IN ('430' , '410')) AND T2.FILEDATE = '" +filterBean.getStFile_date()+"'";
					
						String checkDel =  " SELECT count(1) FROM "+filterBean.getStMerger_Category()+"_CBS" +" t2 WHERE EXISTS ( SELECT 1 FROM "+sel_query;
						logger.info("checkDel IS "+checkDel);
						
						int dataCount = getJdbcTemplate().queryForObject(checkDel, Integer.class); 
						
						
						logger.info("DELETE QUERY IS "+DELETE_QUERY);
						logger.info("SETTLEMENT DELETE QUERY IS "+SETTLEMENT_DELETE_QUERY);
						if(dataCount!=0){
						//getJdbcTemplate().execute(DELETE_QUERY);
						//getJdbcTemplate().execute(SETTLEMENT_DELETE_QUERY);
						dataCount = 0;
						}
						
					}
					

					
					/*+ dynamic_sampling(4) */ 
					
					
					//logger.info("DELETE QUERY IS "+DELETE_QUERY);
					//logger.info("SETTLEMENT DELETE QUERY IS "+SETTLEMENT_DELETE_QUERY);
					
					
				}
				else
				{
					if(filterBean.getStSubCategory().equals("SURCHARGE") && filterBean.getStCategory().equals("RUPAY"))
					{
				 
						
						GET_FROM_RAW = "SELECT /*+ USE_HASH(t2,t1) */  DISTINCT * FROM "+stRaw_TableName+" t2 , SETTLEMENT_"+filterBean.getStCategory()+"_SWITCH t1 WHERE "+condition
								+" AND T2.FILEDATE = '" +filterBean.getStFile_date()+"' AND T1.DCRS_REMARKS = 'RUPAY_SUR' AND T1.MSGTYPE NOT IN ('430' , '410')";
						
					}
					else if(filterBean.getStCategory().equals("VISA") && filterBean.getStSubCategory().equals("SURCHARGE"))
					{
						/*GET_FROM_RAW = "SELECT * FROM "+stRaw_TableName+" t2 , "+filterBean.getStCategory()+"_ISS_SWITCH t1 WHERE "+condition
								+" AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '" +filterBean.getStFile_date()+"'";*/ //COMMENTED ON 06 FEB 2018 FOR FETCHING THE DATA FROM SETTLEMENT TABLE
						//"' AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND t1.MSGTYPE = '210'" ;
						// " AND TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = '03/05/2017'";
						/*GET_FROM_RAW = "SELECT * FROM "+stManFile_Name+"_DATA t2 , "+stFile1_Name+"_DATA t1 WHERE "+condition
							   +" AND (TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = '29/04/2017' OR TO_CHAR(T2.NEXT_TRAN_DATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')) AND TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = '29/04/2017'";*/
						//			" AND TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
						
						//DELETE_QUERY = DELETE_QUERY+" SELECT * FROM "+filterBean.getStCategory()+"ISS_SWITCH t1 ";
						
						GET_FROM_RAW = "SELECT /*+ USE_HASH(t2,t1) */  DISTINCT * FROM "+stRaw_TableName+" t2 , SETTLEMENT_"+filterBean.getStCategory()+"_SWITCH t1 WHERE "+condition
								+" AND T2.FILEDATE = T1.FILEDATE and T2.FILEDATE = '" +filterBean.getStFile_date()+"'  "
								+" AND T1.DCRS_REMARKS = 'VISA_SUR' AND T1.MSGTYPE NOT IN ('430' , '410')";
					
					}
					else
					{
						/*GET_FROM_RAW = "SELECT * FROM "+stRaw_TableName+" t2 , "+filterBean.getStMerger_Category()+"_SWITCH t1 WHERE" 
								+" TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '" +filterBean.getStFile_date()+"'";*/
						// "' AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND t1.MSGTYPE = '210'" ;
						
					//	DELETE_QUERY = DELETE_QUERY+" SELECT * FROM "+filterBean.getStMerger_Category()+"_SWITCH t1 ";
						
						GET_FROM_RAW = "SELECT /*+ USE_HASH(t2,t1) */  DISTINCT * FROM "+stRaw_TableName+" t2 , SETTLEMENT_"+filterBean.getStCategory()+"_SWITCH t1 WHERE" 
								+" T2.FILEDATE =  T1.FILEDATE and  T2.FILEDATE = '" +filterBean.getStFile_date()+"' "
								+" AND T1.DCRS_REMARKS = '"+filterBean.getStMerger_Category()+"' AND T1.MSGTYPE NOT IN ('430' , '410')";
					}
				}
				
				logger.info("GETTING RECORDS QUERY "+GET_FROM_RAW);
				
				
				
				pstmt = getConnection().prepareStatement(GET_FROM_RAW);
				ResultSet MatchedRecords = pstmt.executeQuery();
				
				PreparedStatement settlement_pstmt = getConnection().prepareStatement(GET_FROM_RAW);
				ResultSet settlement_set = settlement_pstmt.executeQuery();
				
		//----------------------------- INSERT THE OBTAINED RECORDS WITH CONTRA_ACCOUNT INT MANUAL TABLE--------------------------------------------------------------
				
				//2. Insert the matched records in the table with man contra account
				
				String INSERT_QUERY = "INSERT INTO "+filterBean.getStMerger_Category()+"_CBS (MAN_CONTRA_ACCOUNT,CREATEDDATE,CREATEDBY,FILEDATE,"+stFile_headers + ") VALUES (?,SYSDATE,'"+filterBean.getStEntry_by()+"',TO_DATE('"+filterBean.getStFile_date()+"','DD/MM/YYYY')";
				String SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+filterBean.getStCategory()+"_CBS (MAN_CONTRA_ACCOUNT,CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+stFile_headers+") " +
											"VALUES(?,SYSDATE,'"+filterBean.getStEntry_by()+"',TO_DATE('"+filterBean.getStFile_date()+"','DD/MM/YYYY'),'"+filterBean.getStMerger_Category()+"'";
				String[] col_names = stFile_headers.split(",");
				for(int loop = 0 ; loop < col_names.length; loop++)
				{
					INSERT_QUERY = INSERT_QUERY + ",?";
					SETTLEMENT_INSERT = SETTLEMENT_INSERT+",?";
					
				}
				
				INSERT_QUERY = INSERT_QUERY + ")";
				SETTLEMENT_INSERT = SETTLEMENT_INSERT +")";
				
				logger.info("INSERT QUERY IS "+INSERT_QUERY);
				logger.info("SETTLEMENT_INSERT IS "+SETTLEMENT_INSERT);
				
				String task = "MATCH";
				insertproxyBatch(INSERT_QUERY, MatchedRecords, col_names, task);
				insertproxyBatch(SETTLEMENT_INSERT, settlement_set, col_names, task);
				logger.info("COMPLETED inserting in manual raw table");
				
				pstmt = null;
				MatchedRecords = null;
				
			}
			
			logger.info("***** FilterationDaoImpl.CategorizeProxyAccount End ****");	
			
			//CALLING CIA GL CATEGORIZATION
		//	manualReconToSettlement(filterBean);
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "FilterationDaoImpl.CategorizeProxyAccount");
			logger.error(" error in FilterationDaoImpl.CategorizeProxyAccount", new Exception("FilterationDaoImpl.CategorizeProxyAccount",e));
			throw e;
		}
		
	}
	private static class ColumnsMapper implements RowMapper<String> {

		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			String stColumns = rs.getString("COLUMN_NAME");
			
			return stColumns;
			
		}
	}
	
	private static class ManualId implements RowMapper<Integer> {

		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			int man_id = rs.getInt("MAN_ID");
			
			return man_id;
		}
	}	
	private static class ManualParameterMaster implements RowMapper<ManualFileBean> {

		@Override
		public ManualFileBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			ManualFileBean manualFileBeanObj = new ManualFileBean();
			
			manualFileBeanObj.setStFile_header(rs.getString("FILE_HEADER"));
			manualFileBeanObj.setStPadding(rs.getString("PADDING"));
			manualFileBeanObj.setStStart_charpos(rs.getString("START_CHARPOSITION"));
			manualFileBeanObj.setStChar_size(rs.getString("CHARSIZE"));
			manualFileBeanObj.setStCondition(rs.getString("CONDITION"));
			manualFileBeanObj.setStSearch_Pattern(rs.getString("SEARCH_PATTERN"));
			
			return manualFileBeanObj;
		}
	}
	private static class MatchParameterMaster1 implements RowMapper<CompareBean> {

		@Override
		public CompareBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			CompareBean compareBeanObj = new CompareBean();
			compareBeanObj.setStMatchTable1_header(rs.getString("FILE_HEADER"));
			compareBeanObj.setStMatchTable1_Padding(rs.getString("PADDING"));
			compareBeanObj.setStMatchTable1_startcharpos(rs.getString("START_CHARPOSITION"));
			compareBeanObj.setStMatchTable1_charSize(rs.getString("CHARSIZE"));
			compareBeanObj.setStMatchTable1_DatePattern(rs.getString("DATA_PATTERN"));
			compareBeanObj.setStMatchTable1_Datatype(rs.getString("DATATYPE"));
			//logger.info("data type in MatchParameterMaster1 "+rs.getString("DATA_PATTERN"));
		
			
			return compareBeanObj;
			
			
		}
	}

	private static class MatchParameterMaster2 implements RowMapper<CompareBean> {

		@Override
		public CompareBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			CompareBean compareBeanObj = new CompareBean();
			compareBeanObj.setStMatchTable2_header(rs.getString("FILE_HEADER"));
			compareBeanObj.setStMatchTable2_Padding(rs.getString("PADDING"));
			compareBeanObj.setStMatchTable2_startcharpos(rs.getString("START_CHARPOSITION"));
			compareBeanObj.setStMatchTable2_charSize(rs.getString("CHARSIZE"));
			compareBeanObj.setStMatchTable2_DatePattern(rs.getString("DATA_PATTERN"));
			//logger.info("data type in MatchParameterMaster2 "+rs.getString("DATA_PATTERN"));
			compareBeanObj.setStMatchTable2_Datatype(rs.getString("DATATYPE"));
		
			
			return compareBeanObj;
			
			
		}
	}
	
	public void CIA_GL_classsification(FilterationBean filterbeanObj)throws Exception
	{
		 logger.info("***** FilterationDaoImpl.CIA_GL_classsification Start ****");
			//String[] a = table_Name.split("_");
			String table_cols = "CREATEDDATE DATE,CREATEDBY VARCHAR(100 BYTE),FILEDATE DATE, DCR_TRAN_ID NUMBER, DCRS_REMARKS VARCHAR(100 BYTE)";
			String CREATE_QUERY = "";
			String CHECK_TABLE = "";
			String temp_param = "";
			String condition = "";
			PreparedStatement pstmt = null;
			Connection conn = null;
			ResultSet rset = null;
			
			
			try
			{

				//check if table exist if not then create it 
				int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {"CBS",filterbeanObj.getStCategory(),filterbeanObj.getStSubCategory() },Integer.class);
				logger.info("File id is "+file_id);

				String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
				//String Update_cols = stFile_headers;
				stFile_headers = "MAN_CONTRA_ACCOUNT,"+stFile_headers;
				logger.info("stFile_headers== "+stFile_headers);
				
				CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'SETTLEMENT_"+filterbeanObj.getStCategory()+"_CBS'";
				int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);

				logger.info("CHECK TABLE QUERY IS "+CHECK_TABLE);
				logger.info("tableExist== "+tableExist);
				//get connection
				conn = getConnection();
				if(tableExist == 0)
				{
					String[] cols = stFile_headers.split(",");
					for(int i = 0 ; i < cols.length ; i++)
					{
						table_cols = table_cols + "," + cols[i] + " VARCHAR(100 BYTE)";
					}

					CREATE_QUERY = "CREATE TABLE SETTLEMENT_"+filterbeanObj.getStCategory()+"_CBS ("+table_cols+")";
					logger.info("CREATE TABLE QUERY IS "+CREATE_QUERY);
					pstmt = conn.prepareStatement(CREATE_QUERY);
					rset = pstmt.executeQuery();

					pstmt = null;
					rset = null;

				}
				else if(tableExist == 1 )
				{
					boolean CheckForCol = false;
					//now check for the field MAN_CONTRA_ACCOUNT IF NOT PRESENT THEN ALTER TABLE
					List<String> Columns = getJdbcTemplate().query(GET_COLS, new Object[]{"SETTLEMENT_"+filterbeanObj.getStCategory()+"_CBS"}, new ColumnsMapper());
					logger.info("Columns== "+Columns);
					
					for(int i = 0 ;i<Columns.size(); i++)
					{
						if(Columns.get(i).equalsIgnoreCase("MAN_CONTRA_ACCOUNT"))
						{
							CheckForCol = true;
						}
					}

					if(!CheckForCol)
					{
						try{
							String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+filterbeanObj.getStCategory()+"_CBS ADD MAN_CONTRA_ACCOUNT VARCHAR2(100 BYTE)";
							getJdbcTemplate().execute(ALTER_QUERY);
							logger.info("ALTER_QUERY== "+ALTER_QUERY);
						}
						catch(UncategorizedSQLException sqlException)
						{
							demo.logSQLException(sqlException, "FilterationDaoImpl.CIA_GL_classsification");
							logger.error(" error in FilterationDaoImpl.CIA_GL_classsification", new Exception("FilterationDaoImpl.CIA_GL_classsification",sqlException));
							//logger.info("SQLEXCEPTION IS "+sqlException);
						}
					}
				}


				//------------------------------------------ FILTER MANUAL RECON RECORDS USING CRITEIRA PROVIDED IN DOC----------------------------------------------------------


				List<SettlementBean> settlement_id = getJdbcTemplate().query(GET_SETTLEMENT_ID , new Object[] {file_id}, new SettlementId());
				logger.info("settlement_id== "+settlement_id);
				
				for(int id = 0 ; id < settlement_id.size() ; id++)
				{
					condition = "";
					logger.info("SETTLEMENT ID IS "+settlement_id.get(id).getInSettlement_id());
					List<FilterationBean> search_params = getJdbcTemplate().query(GET_SETTLEMENT_PARAM, new Object[] {file_id,settlement_id.get(id).getInSettlement_id()}, new SettlementMaster());
					logger.info("search_params== "+search_params);
					//	logger.info("got the search params"+search_params.size());
					for(int i = 0; i<search_params.size();i++)
					{
						FilterationBean filterBeanObj = new FilterationBean();
						filterBeanObj = search_params.get(i);
						temp_param = filterBeanObj.getStSearch_header().trim();
						//changes made by INT5779 for issue in ONUS as proxy transactions were received while testing
						if(filterBeanObj.getStSearch_pattern().equalsIgnoreCase("NULL"))
						{
							if(filterBeanObj.getStSearch_Condition().equalsIgnoreCase("="))
								condition = condition +" ("+ filterBeanObj.getStSearch_header()+" IS NULL "; 
							else
								condition = condition + " ("+ filterBeanObj.getStSearch_header()+" IS NOT NULL ";
						}
						else
						{
							if((filterBeanObj.getStSearch_padding().trim()).equals("Y"))
							{


								if((filterBeanObj.getStSearch_Condition().trim()).equalsIgnoreCase("like"))
								{
									condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
											filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+
											"'%"+filterBeanObj.getStSearch_pattern().trim()+"%' ";
								}
								else if((filterBeanObj.getStSearch_Condition().trim()).equals("!="))
								{
									if(i == (search_params.size()-1))
									{
										condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
												filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";					
									}
									else
									{

										condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
												filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
									}
								}
								else
								{
									if(filterBeanObj.getStSearch_Datatype()!=null)
									{
										if(filterBeanObj.getStSearch_Datatype().equals("NUMBER"))
										{
											condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
													filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+filterBeanObj.getStSearch_pattern().trim();
										}
									}
									else
									{
										condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
												filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+"'"+filterBeanObj.getStSearch_pattern().trim()+"' ";
									}

								}


							}
							else
							{
								if(filterBeanObj.getStSearch_Condition().equalsIgnoreCase("like"))
								{
									condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" "+
											"'%"+filterBeanObj.getStSearch_pattern().trim()+"%'";
								}
								else if((filterBeanObj.getStSearch_Condition().trim()).equals("!="))
								{
									if(i == (search_params.size()-1))
									{
										condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
									}
									else
									{
										condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
									}
								}
								else
								{
									if(filterBeanObj.getStSearch_Datatype()!=null)
									{
										if(filterBeanObj.getStSearch_Datatype().equals("NUMBER"))
										{	
											condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+
													filterBeanObj.getStSearch_pattern().trim();
										}
									}
									else
									{
										condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" '"+
												filterBeanObj.getStSearch_pattern().trim()+"'";
									}
								}


							}

							for(int j= (i+1); j <search_params.size(); j++)
							{
								//logger.info("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
								if(temp_param.equals(search_params.get(j).getStSearch_header()))
								{

									if(search_params.get(j).getStSearch_padding().equals("Y"))
									{ 
										if((search_params.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
										{
											condition = condition + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
													search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+
													"'%"+search_params.get(j).getStSearch_pattern().trim()+"%'";
										}
										else if((filterBeanObj.getStSearch_Condition().trim()).equals("!="))
										{
											if(j==(search_params.size()-1))
											{	
												/*condition = condition + " OR SUBSTR(" + search_params.get(j).getStSearch_header()+" , "+search_params.get(j).getStsearch_Startcharpos()+","+
											search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition()+ search_params.get(j).getStSearch_pattern();*/
												condition = condition + ", '"+search_params.get(j).getStSearch_pattern().trim()+"')";
											}
											else
											{
												condition = condition + ", '"+search_params.get(j).getStSearch_pattern().trim()+"' ";
											}

										}
										else
										{
											if(filterBeanObj.getStSearch_Datatype() != null)
											{
												if(filterBeanObj.getStSearch_Datatype().equals("NUMBER"))
												{		
													condition = condition + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
															search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+ 
															search_params.get(j).getStSearch_pattern().trim();
												}
											}
											else
											{
												condition = condition + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
														search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+ 
														"'"+search_params.get(j).getStSearch_pattern().trim()+"'";
											}

										}
									}
									else
									{
										if((search_params.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
										{
											condition = condition + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
													search_params.get(j).getStSearch_Condition().trim()+" "+
													"'%"+search_params.get(j).getStSearch_pattern().trim()+"%'";

										}
										else if((filterBeanObj.getStSearch_Condition().trim()).equals("!="))
										{
											if(j==(search_params.size()-1))
											{
												condition = condition + " , '" +search_params.get(j).getStSearch_pattern().trim()+"')";
											}
											else
											{
												condition = condition + " , '" +search_params.get(j).getStSearch_pattern().trim()+"' ";
											}

										}
										else
										{
											logger.info("check the datatype "+filterBeanObj.getStSearch_Datatype());
											if(filterBeanObj.getStSearch_Datatype() != null)
											{
												if(filterBeanObj.getStSearch_Datatype().equals("NUMBER"))
												{		
													condition = condition + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
															search_params.get(j).getStSearch_Condition().trim()+search_params.get(j).getStSearch_pattern().trim();
												}
											}
											else
											{
												condition = condition + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
														search_params.get(j).getStSearch_Condition().trim()+" '"+search_params.get(j).getStSearch_pattern().trim()+"'";
											}

										}
									}


									i = j;

								}

							}
						}
						//logger.info("i value is "+i);
						if(i != (search_params.size())-1)
						{
							if(filterBeanObj.getStSearch_Condition().equals("!="))
							{
								condition = condition +" ) ) AND ";
							}
							else 
							{
								condition = condition +" ) AND ";
							}
						}
						else
							condition = condition +")";

						//	logger.info("condition is "+condition);
					}
					
					logger.info("condition== "+condition);

					//logger.info("Condition "+condition);
					String RECON_CATEGORIZATION = "";
					String DELETE_QUERY = "";
					String stTableName = "";

				
					{
						stTableName = filterbeanObj.getStMerger_Category()+"_CBS";
					}

					//REMOVING BATCH 
					String select_cols = "";
					List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {filterbeanObj.getStMerger_Category()+"_CBS"},new RowMapper<String>(){
						@Override
						public String mapRow(ResultSet rs,int rowcount)throws SQLException
						{
							return rs.getString("COLUMN_NAME");
						}

					});
					
					logger.info("TABLE_COLS== "+TABLE_COLS);
					
					int count1 = 0;
					for(String cols : TABLE_COLS)
					{
						if(count1 == 0)
						{
							select_cols = cols;
							count1++;
						}
						else
							select_cols = select_cols+","+cols;
					}

					RECON_CATEGORIZATION = "SELECT '"+filterbeanObj.getStMerger_Category()+"-CIA-GL',"+select_cols+" FROM "+stTableName+ " OS1 " ;
					DELETE_QUERY = "DELETE FROM "+stTableName + " OS1 ";

					if(!condition.equals(""))
					{
						RECON_CATEGORIZATION = RECON_CATEGORIZATION +" WHERE "+ condition + " AND TO_CHAR(CREATEDDATE ,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
								" AND FILEDATE = '"+filterbeanObj.getStFile_date()+"'" ;


						DELETE_QUERY = DELETE_QUERY +" WHERE "+ condition + " AND TO_CHAR(CREATEDDATE ,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
								" AND FILEDATE = '"+filterbeanObj.getStFile_date()+"'" ;


					}
					else
					{
						RECON_CATEGORIZATION = RECON_CATEGORIZATION +" WHERE TO_CHAR(CREATEDDATE ,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
								" AND FILEDATE = '"+filterbeanObj.getStFile_date()+"'";
						DELETE_QUERY = DELETE_QUERY +" WHERE TO_CHAR(CREATEDDATE ,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
								" AND FILEDATE = '"+filterbeanObj.getStFile_date()+"'";
					}

					

					logger.info("recon categorization QUERY IS "+RECON_CATEGORIZATION);
					logger.info("DELETE_QUERY== "+DELETE_QUERY);

					String INSERT_CIA_GL = "INSERT INTO SETTLEMENT_"+filterbeanObj.getStCategory()+"_CBS (DCRS_REMARKS,"+select_cols+") "
							+RECON_CATEGORIZATION;
					logger.info("INSERT QUERY IS "+INSERT_CIA_GL);
					getJdbcTemplate().execute(INSERT_CIA_GL);

				
					//DELETE THE RECORDS FROM ONUS TABLE AFTER INSERTION IN SETTLEMENT TABLE
					logger.info("DELETING RECON CATEGORIZED RECORDS------------------------------------------------------");
					logger.info("DELETE QUERY IS "+DELETE_QUERY);
					getJdbcTemplate().execute(DELETE_QUERY);
					logger.info("COMPLETED DELETION--------------------------------------------------------------------");



				}

				logger.info("COMPLETED CIA GL--------------------------------------------------------------------------------------------------------------------------------------------------");
				if(filterbeanObj.getStCategory().equals("ONUS"))
				{
					logger.info("------------------------------------------ CLASSIFYING CASH DEPOSIT TRANSACTIONS -----------------------------------------------------------------------------------");
				String select_cols = "";
				List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {filterbeanObj.getStMerger_Category()+"_CBS"},new RowMapper<String>(){
					@Override
					public String mapRow(ResultSet rs,int rowcount)throws SQLException
					{
						return rs.getString("COLUMN_NAME");
					}

				});
				logger.info("TABLE_COLS== "+TABLE_COLS);
				
				int count1 = 0;
				for(String cols : TABLE_COLS)
				{
					if(count1 == 0)
					{
						select_cols = cols;
						count1++;
					}
					else
						select_cols = select_cols+","+cols;
				}
				String DEPOSITE_ACC = "UPDATE SETTLEMENT_"+filterbeanObj.getStMerger_Category()+"_CBS SET DCRS_REMARKS = 'ONUS-CASH-DEPOSIT' WHERE (SUBSTR(TRAN_PARTICULAR,1,2) = 'BN' OR SUBSTR(TRAN_PARTICULAR,1,2) = 'BA') AND PART_TRAN_TYPE = 'D' AND "+ 
						" (TRAN_RMKS = '8887770000000000' OR SUBSTR(TRIM(TRAN_RMKS),1,6) ='458139'  OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458110' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='549749' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='652255' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458134' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='421426' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='421491' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='468822' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458136' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458105' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458135' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='652262' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458159' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='409386' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='400815' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='403839' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='421490' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='485069' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='400815' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='403839' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='409386' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='421490' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='421662' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='428675' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='432090' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='432093' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='432094' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='432098' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='432099' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='437891' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458112' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458118' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458140' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458777' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458778' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470900' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470901' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470902' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470903' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470904' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470905' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470906' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470907' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='472255' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='472256' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='472257' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='472258' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='472259' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='508545' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='510557' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='517899' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='521163' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='546522' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='547101' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='555936' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='601794' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='607030' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='607409' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='607419' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='607459' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='607480' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='608008' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='608070' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='608071' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='652154' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='652201' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='559558' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='652860' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='485068') " +
						" AND FILEDATE = '"+filterbeanObj.getStFile_date()+"'";
				String DELETE_FROM_CLASS = "DELETE FROM "+filterbeanObj.getStMerger_Category()+"_CBS WHERE (SUBSTR(TRAN_PARTICULAR,1,2) = 'BN' OR SUBSTR(TRAN_PARTICULAR,1,2) = 'BA') AND PART_TRAN_TYPE = 'D' AND " 
										+"(TRAN_RMKS = '8887770000000000' OR SUBSTR(TRIM(TRAN_RMKS),1,6) ='458139'  OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458110' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='549749' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='652255' " +
										"OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458134' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='421426' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='421491' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='468822' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458136' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458105' " +
										"OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458135' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='652262' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458159' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='409386' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='400815' " +
										"OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='403839' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='421490' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='485069' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='400815' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='403839' " +
										"OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='409386' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='421490' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='421662' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='428675' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='432090' " +
										"OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='432093' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='432094' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='432098' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='432099' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='437891' " +
										"OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458112' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458118' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458140' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458777' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='458778' " +
										"OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470900' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470901' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470902' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470903' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470904' " +
										"OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470905' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470906' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='470907' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='472255' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='472256' " +
										"OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='472257' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='472258' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='472259' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='508545' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='510557' " +
										"OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='517899' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='521163' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='546522' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='547101' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='555936' " +
										"OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='601794' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='607030' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='607409' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='607419' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='607459' " +
										"OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='607480' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='608008' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='608070' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='608071' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='652154' " +
										"OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='652201' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='559558' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='652860' OR SUBSTR(TRIM(TRAN_RMKS) , 1,6) ='485068')" +
										" AND FILEDATE = '"+filterbeanObj.getStFile_date()+"'";	
				logger.info("DEPOSIT ACC CLASS "+DEPOSITE_ACC);
				logger.info("DELETING DEPOSIT TRANSACTIONS "+DELETE_FROM_CLASS);
				getJdbcTemplate().execute(DEPOSITE_ACC);
				getJdbcTemplate().execute(DELETE_FROM_CLASS);
				
				DEPOSITE_ACC = "UPDATE SETTLEMENT_"+filterbeanObj.getStMerger_Category()+"_CBS SET DCRS_REMARKS = 'ONUS-CASH-DEPOSIT' WHERE (SUBSTR(TRAN_PARTICULAR,1,2) = 'BN' OR SUBSTR(TRAN_PARTICULAR,1,2) = 'BA') AND"+ 
						" PART_TRAN_TYPE = 'C' AND (SUBSTR(TRAN_RMKS,1,1) = 'S' OR SUBSTR(TRAN_RMKS,1,1) = 'C') AND FILEDATE = '"+filterbeanObj.getStFile_date()+"'";
				
				DELETE_FROM_CLASS = "DELETE FROM "+filterbeanObj.getStMerger_Category()+"_CBS WHERE (SUBSTR(TRAN_PARTICULAR,1,2) = 'BN' OR SUBSTR(TRAN_PARTICULAR,1,2) = 'BA') AND " 
						+" PART_TRAN_TYPE = 'C' AND (SUBSTR(TRAN_RMKS,1,1) = 'S' OR SUBSTR(TRAN_RMKS,1,1) = 'C') AND FILEDATE = '"+filterbeanObj.getStFile_date()+"'";	
				
				logger.info("DEPOSIT ACC CLASS "+DEPOSITE_ACC);
				logger.info("DELETING DEPOSIT TRANSACTIONS "+DELETE_FROM_CLASS);
				
				getJdbcTemplate().execute(DEPOSITE_ACC);
				getJdbcTemplate().execute(DELETE_FROM_CLASS);
				

			}
			 logger.info("---------------------------------------ACTION TAKEN STARTS---------------------------------------------------------");
			 updateActionTakenTTUMRecord(filterbeanObj);
			// logger.info("---------------------------------------ENDS HERE-------------------------------------------------------------------");
			}
			catch(Exception e)
			{
				demo.logSQLException(e, "FilterationDaoImpl.CIA_GL_classsification");
				logger.error(" error in FilterationDaoImpl.CIA_GL_classsification", new Exception("FilterationDaoImpl.CIA_GL_classsification",e));
				throw e;
			}
			
			logger.info("***** FilterationDaoImpl.CIA_GL_classsification End ****");

		}
/*public void manualReconToSettlement(FilterationBean filterbeanObj)throws Exception
{
		
		//String[] a = table_Name.split("_");
		String table_cols = "CREATEDDATE DATE,CREATEDBY VARCHAR(100 BYTE),FILEDATE DATE, DCR_TRAN_ID NUMBER, DCRS_REMARKS VARCHAR(100 BYTE)";
		String CREATE_QUERY = "";
		String CHECK_TABLE = "";
		String temp_param = "";
		String condition = "";
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rset = null;
		
		
		try
		{
			
			//check if table exist if not then create it 
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {"CBS",filterbeanObj.getStCategory(),filterbeanObj.getStSubCategory() },Integer.class);
			logger.info("File id is "+file_id);
			
			String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
			//String Update_cols = stFile_headers;
			stFile_headers = "MAN_CONTRA_ACCOUNT,"+stFile_headers;
			
			CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'SETTLEMENT_"+filterbeanObj.getStCategory()+"_CBS'";
			int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			
			logger.info("CHECK TABLE QUERY IS "+CHECK_TABLE);
			//get connection
			conn = getConnection();
			if(tableExist == 0)
			{
				String[] cols = stFile_headers.split(",");
				for(int i = 0 ; i < cols.length ; i++)
				{
					table_cols = table_cols + "," + cols[i] + " VARCHAR(100 BYTE)";
				}

				CREATE_QUERY = "CREATE TABLE SETTLEMENT_"+filterbeanObj.getStCategory()+"_CBS ("+table_cols+")";
				logger.info("CREATE TABLE QUERY IS "+CREATE_QUERY);
				pstmt = conn.prepareStatement(CREATE_QUERY);
				rset = pstmt.executeQuery();
				
				pstmt = null;
				rset = null;

			}
			else if(tableExist == 1 )
			{
				boolean CheckForCol = false;
				//now check for the field MAN_CONTRA_ACCOUNT IF NOT PRESENT THEN ALTER TABLE
				List<String> Columns = getJdbcTemplate().query(GET_COLS, new Object[]{"SETTLEMENT_"+filterbeanObj.getStCategory()+"_CBS"}, new ColumnsMapper());
				
				for(int i = 0 ;i<Columns.size(); i++)
				{
					if(Columns.get(i).equalsIgnoreCase("MAN_CONTRA_ACCOUNT"))
					{
						CheckForCol = true;
					}
				}
				
				if(!CheckForCol)
				{
					try{
						String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+filterbeanObj.getStCategory()+"_CBS ADD MAN_CONTRA_ACCOUNT VARCHAR2(100 BYTE)";
						getJdbcTemplate().execute(ALTER_QUERY);
					}
					catch(UncategorizedSQLException sqlException)
					{
						logger.info("SQLEXCEPTION IS "+sqlException);
					}
				}
			}
			
			
	//------------------------------------------ FILTER MANUAL RECON RECORDS USING CRITEIRA PROVIDED IN DOC----------------------------------------------------------
		    
		    
		    List<SettlementBean> settlement_id = getJdbcTemplate().query(GET_SETTLEMENT_ID , new Object[] {file_id}, new SettlementId());
		    
		 for(int id = 0 ; id < settlement_id.size() ; id++)
		 {
			 condition = "";
			 logger.info("SETTLEMENT ID IS "+settlement_id.get(id).getInSettlement_id());
			 List<FilterationBean> search_params = getJdbcTemplate().query(GET_SETTLEMENT_PARAM, new Object[] {file_id,settlement_id.get(id).getInSettlement_id()}, new SettlementMaster());
		    
		//	logger.info("got the search params"+search_params.size());
			for(int i = 0; i<search_params.size();i++)
			{
				FilterationBean filterBeanObj = new FilterationBean();
				filterBeanObj = search_params.get(i);
				temp_param = filterBeanObj.getStSearch_header().trim();
				//changes made by INT5779 for issue in ONUS as proxy transactions were received while testing
				if(filterBeanObj.getStSearch_pattern().equalsIgnoreCase("NULL"))
				{
					if(filterBeanObj.getStSearch_Condition().equalsIgnoreCase("="))
						condition = condition +" ("+ filterBeanObj.getStSearch_header()+" IS NULL "; 
					else
						condition = condition + " ("+ filterBeanObj.getStSearch_header()+" IS NOT NULL ";
				}
				else
				{
					if((filterBeanObj.getStSearch_padding().trim()).equals("Y"))
					{


						if((filterBeanObj.getStSearch_Condition().trim()).equalsIgnoreCase("like"))
						{
							condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
									filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+
									"'%"+filterBeanObj.getStSearch_pattern().trim()+"%' ";
						}
						else if((filterBeanObj.getStSearch_Condition().trim()).equals("!="))
						{
							if(i == (search_params.size()-1))
							{
								condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
										filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";					
							}
							else
							{

								condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
										filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
							}
						}
						else
						{
							if(filterBeanObj.getStSearch_Datatype()!=null)
							{
								if(filterBeanObj.getStSearch_Datatype().equals("NUMBER"))
								{
									condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
											filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+filterBeanObj.getStSearch_pattern().trim();
								}
							}
							else
							{
								condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
										filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+"'"+filterBeanObj.getStSearch_pattern().trim()+"' ";
							}

						}


					}
					else
					{
						if(filterBeanObj.getStSearch_Condition().equalsIgnoreCase("like"))
						{
							condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" "+
									"'%"+filterBeanObj.getStSearch_pattern().trim()+"%'";
						}
						else if((filterBeanObj.getStSearch_Condition().trim()).equals("!="))
						{
							if(i == (search_params.size()-1))
							{
								condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
							}
							else
							{
								condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
							}
						}
						else
						{
							if(filterBeanObj.getStSearch_Datatype()!=null)
							{
								if(filterBeanObj.getStSearch_Datatype().equals("NUMBER"))
								{	
									condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+
											filterBeanObj.getStSearch_pattern().trim();
								}
							}
							else
							{
								condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" '"+
										filterBeanObj.getStSearch_pattern().trim()+"'";
							}
						}


					}

					for(int j= (i+1); j <search_params.size(); j++)
					{
						//logger.info("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
						if(temp_param.equals(search_params.get(j).getStSearch_header()))
						{

							if(search_params.get(j).getStSearch_padding().equals("Y"))
							{ 
								if((search_params.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
								{
									condition = condition + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
											search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+
											"'%"+search_params.get(j).getStSearch_pattern().trim()+"%'";
								}
								else if((filterBeanObj.getStSearch_Condition().trim()).equals("!="))
								{
									if(j==(search_params.size()-1))
									{	
										condition = condition + " OR SUBSTR(" + search_params.get(j).getStSearch_header()+" , "+search_params.get(j).getStsearch_Startcharpos()+","+
										search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition()+ search_params.get(j).getStSearch_pattern();
										condition = condition + ", '"+search_params.get(j).getStSearch_pattern().trim()+"')";
									}
									else
									{
										condition = condition + ", '"+search_params.get(j).getStSearch_pattern().trim()+"' ";
									}

								}
								else
								{
									if(filterBeanObj.getStSearch_Datatype() != null)
									{
										if(filterBeanObj.getStSearch_Datatype().equals("NUMBER"))
										{		
											condition = condition + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
													search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+ 
													search_params.get(j).getStSearch_pattern().trim();
										}
									}
									else
									{
										condition = condition + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
												search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+ 
												"'"+search_params.get(j).getStSearch_pattern().trim()+"'";
									}

								}
							}
							else
							{
								if((search_params.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
								{
									condition = condition + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
											search_params.get(j).getStSearch_Condition().trim()+" "+
											"'%"+search_params.get(j).getStSearch_pattern().trim()+"%'";

								}
								else if((filterBeanObj.getStSearch_Condition().trim()).equals("!="))
								{
									if(j==(search_params.size()-1))
									{
										condition = condition + " , '" +search_params.get(j).getStSearch_pattern().trim()+"')";
									}
									else
									{
										condition = condition + " , '" +search_params.get(j).getStSearch_pattern().trim()+"' ";
									}

								}
								else
								{
									logger.info("check the datatype "+filterBeanObj.getStSearch_Datatype());
									if(filterBeanObj.getStSearch_Datatype() != null)
									{
										if(filterBeanObj.getStSearch_Datatype().equals("NUMBER"))
										{		
											condition = condition + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
													search_params.get(j).getStSearch_Condition().trim()+search_params.get(j).getStSearch_pattern().trim();
										}
									}
									else
									{
										condition = condition + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
												search_params.get(j).getStSearch_Condition().trim()+" '"+search_params.get(j).getStSearch_pattern().trim()+"'";
									}

								}
							}


							i = j;

						}

					}
				}
				//logger.info("i value is "+i);
				if(i != (search_params.size())-1)
				{
					if(filterBeanObj.getStSearch_Condition().equals("!="))
					{
						condition = condition +" ) ) AND ";
					}
					else 
					{
						condition = condition +" ) AND ";
					}
				}
				else
					condition = condition +")";
				
			//	logger.info("condition is "+condition);
			}
			
			//logger.info("Condition "+condition);
			String RECON_CATEGORIZATION = "";
			String DELETE_QUERY = "";
			String stTableName = "";
			
			if(manualFileBeanObj.getStCategory().equals("ONUS") || manualFileBeanObj.getStCategory().equals("AMEX"))
			{
				stTableName = manualFileBeanObj.getStCategory()+"_MANUAL_"+manualFileBeanObj.getStManualFile();
			}
			else
			{
				stTableName = filterbeanObj.getStMerger_Category()+"_CBS";
			}
			
			RECON_CATEGORIZATION = "SELECT * FROM "+stTableName+ " OS1 " ;
			DELETE_QUERY = "DELETE FROM "+stTableName + " OS1 ";
		
			if(!condition.equals(""))
			{
				RECON_CATEGORIZATION = RECON_CATEGORIZATION +" WHERE "+ condition + " AND TO_CHAR(CREATEDDATE ,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
						" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+filterbeanObj.getStFile_date()+"'" ;
						
				
				DELETE_QUERY = DELETE_QUERY +" WHERE "+ condition + " AND TO_CHAR(CREATEDDATE ,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
						" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+filterbeanObj.getStFile_date()+"'" ;
						
				
			}
			else
			{
				RECON_CATEGORIZATION = RECON_CATEGORIZATION +" WHERE TO_CHAR(CREATEDDATE ,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
						" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+filterbeanObj.getStFile_date()+"'";
				DELETE_QUERY = DELETE_QUERY +" WHERE TO_CHAR(CREATEDDATE ,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
						" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+filterbeanObj.getStFile_date()+"'";
			}
			
			//String CHECK_SETTLEMENT = "SELECT * FROM SETTLEMENT_"+a[2] +" OS2 ";
			//NO NEED OF KNOCKOFF CONDITION AS WE ARE DELETING THE RECORDS THE SAME TIME
			int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), a[0]},Integer.class);
			List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
			
			//String knockoffCond = getKnockOffCondition(knockoff_Criteria);
			
			//logger.info("check here"+knockoffCond);
			
			if(!knockoffCond.equals(""))
			{
				CHECK_SETTLEMENT = CHECK_SETTLEMENT + " WHERE "+knockoffCond+" AND TO_CHAR(OS2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
			}
			
			//RECON_CATEGORIZATION = RECON_CATEGORIZATION + "("+ CHECK_SETTLEMENT +")";
			//DELETE_QUERY = DELETE_QUERY + "("+CHECK_SETTLEMENT + ")";
			
			logger.info("recon categorization QUERY IS "+RECON_CATEGORIZATION);
			
			pstmt = conn.prepareStatement(RECON_CATEGORIZATION);
			ResultSet CIA_GL_RECORDS = pstmt.executeQuery();
			
			
			
			String INSERT_RECORDS = "INSERT INTO SETTLEMENT_"+filterbeanObj.getStCategory()+"_CBS (CREATEDDATE, CREATEDBY,FILEDATE, DCRS_REMARKS, "+stFile_headers+")" +
								" VALUES (SYSDATE, '"+filterbeanObj.getStEntry_by()+"',TO_DATE('"+filterbeanObj.getStFile_date()+"','DD/MM/YYYY'),'"+filterbeanObj.getStMerger_Category()+"-CIA-GL'";
			String[] tab_cols = stFile_headers.split(",");
			for(int count = 0 ; count <tab_cols.length ; count++)
			{
				INSERT_RECORDS = INSERT_RECORDS + ",?";
			}
			
			INSERT_RECORDS = INSERT_RECORDS + ")";
			
			logger.info("INSERT QUERY IS "+INSERT_RECORDS);
			
			insertBatch(INSERT_RECORDS, CIA_GL_RECORDS, tab_cols, conn);
			
			//DELETE THE RECORDS FROM ONUS TABLE AFTER INSERTION IN SETTLEMENT TABLE
			logger.info("DELETING RECON CATEGORIZED RECORDS------------------------------------------------------");
			logger.info("DELETE QUERY IS "+DELETE_QUERY);
			getJdbcTemplate().execute(DELETE_QUERY);
			logger.info("COMPLETED DELETION--------------------------------------------------------------------");
			
			
			
			
			String DELETE_QUERY = "DELETE FROM "+table_Name +" WHERE ";
			
			for(int count = 0 ; count <tab_cols.length ; count++)
			{
				if(count == (tab_cols.length-1))
				{
					DELETE_QUERY = DELETE_QUERY + "NVL("+tab_cols[count] + ",'!null!') = ? ";
				}
				else
					DELETE_QUERY = DELETE_QUERY + "NVL("+tab_cols[count] + ",'!null!') = ? AND ";
			}
			logger.info("DELETE QUERY IS "+DELETE_QUERY);
			
			PreparedStatement delete_statement = conn.prepareStatement(RECON_CATEGORIZATION);
			ResultSet delete_set = delete_statement.executeQuery();
			
			deleteBatch(DELETE_QUERY, delete_set, tab_cols, conn);
			
			
		 }
		 
		 logger.info("COMPLETED CIA GL--------------------------------------------------------------------------------------------------------------------------------------------------");
		 //insert remaining records from recon table in ONLINE ONUS table
		 String REMAINING_RECON_RECORDS = "SELECT * FROM "+table_Name +" OS1 WHERE NOT EXISTS " +
		 								" ( SELECT * FROM SETTLEMENT_"+a[3]+" OS2 WHERE ";
		 
			int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), a[1]},Integer.class);
			List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
			
			String knockoffCond = getKnockOffCondition(knockoff_Criteria);
			
			REMAINING_RECON_RECORDS = REMAINING_RECON_RECORDS + knockoffCond + ")";
	
		 
		 //INSERTING REMAINING RECORDS IN CATEGORY TABLE IN CASE OF ONUS AND AMEX
		 String stTableName = "";
		 
		 if (manualFileBeanObj.getStCategory().equals("ONUS") || manualFileBeanObj.getStCategory().equals("AMEX"))
		 {

			 stTableName = manualFileBeanObj.getStCategory()+"_MANUAL_"+manualFileBeanObj.getStManualFile();
			 
			 String REMAINING_RECON_RECORDS = "SELECT * FROM "+stTableName+" WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
					 " AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"'";
			 logger.info("REMANING RECON RECORDS "+REMAINING_RECON_RECORDS);

			 PreparedStatement remainingStatement = conn.prepareStatement(REMAINING_RECON_RECORDS);
			 ResultSet remaining_set = remainingStatement.executeQuery();


			 String INSERT_REMAINING_RECORDS = "INSERT INTO "+manualFileBeanObj.getStMergerCategory()+"_"+manualFileBeanObj.getStManualFile()+" (CREATEDDATE, CREATEDBY,FILEDATE,"+stFile_headers
					 +") VALUES(SYSDATE, 'INT5779',TO_DATE('"+manualFileBeanObj.getStFile_date()+"','DD/MM/YYYY')";
			 String[] tab_cols = stFile_headers.split(",");
			 for(int count = 0 ; count < tab_cols.length; count++)
			 {
				 INSERT_REMAINING_RECORDS = INSERT_REMAINING_RECORDS + ",?";
			 }
			 INSERT_REMAINING_RECORDS = INSERT_REMAINING_RECORDS + ")";

			 logger.info("INSERT REMAINING RECORDS "+INSERT_REMAINING_RECORDS);

			 insertBatch(INSERT_REMAINING_RECORDS, remaining_set, tab_cols, conn);

			 logger.info("COMPLETED INSERTING IN ONLINE ONUS TABLE!!!!!!");
		 }
		 
		 // TRUNCATE MANUAL ONUS TABLE
		// String TRUNCATE_QUERY = "TRUNCATE TABLE "+ manualFileBeanObj.getStCategory()+"_MANUAL_"+manualFileBeanObj.getStManualFile();
		// getJdbcTemplate().execute(TRUNCATE_QUERY);
		 
		// logger.info("TRUNCATED TABLE "+manualFileBeanObj.getStCategory()+"_MANUAL_"+manualFileBeanObj.getStManualFile());
		 
		 
	//------------------------------------------AS ON 22 JUNE 2017 NO NEED TO UPDATE RAW TABLE-----------------------------------------	 
	//**********************************************************update raw table***************************	 
		PreparedStatement psrecon = conn.prepareStatement(REMAINING_RECON_RECORDS);
			ResultSet rs = psrecon.executeQuery();
			//get Raw table name
			String stRaw_Table = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{file_id}, String.class);
			String UPDATE_QUERY ="UPDATE "+stRaw_Table.toUpperCase()+" SET NEXT_TRAN_DATE = TO_CHAR(SYSDATE,'DD/MON/YYYY') WHERE ( ";
			//logger.info("cols lenght is "+col_names.length);
			logger.info("update columns are "+Update_cols);
			String[] col_names = Update_cols.split(",");
			for(int i=0; i<col_names.length ; i++)
			{
				if(i==(col_names.length-1))
					UPDATE_QUERY = UPDATE_QUERY + "NVL("+col_names[i] + ",'!null!') = ?";
				else
					UPDATE_QUERY = UPDATE_QUERY + "NVL("+ col_names[i] + ",'!null!') = ? AND ";
			}
			
			UPDATE_QUERY = UPDATE_QUERY + ")";
			
			logger.info("UPDATE QUERY "+UPDATE_QUERY);
			logger.info("start time FOR UPDATING RAW TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
			deleteBatch(UPDATE_QUERY, rs, col_names,conn);//IT IS UPDATE QUERY FOR RAW TABLE
			//updateBatch(UPDATE_QUERY, rs, tab_cols);
			logger.info("End time FOR UPDATING RAW TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
		 
		 logger.info("---------------------------------------ACTION TAKEN STARTS---------------------------------------------------------");
		 updateActionTakenTTUMRecord(filterbeanObj);
		 logger.info("---------------------------------------ENDS HERE-------------------------------------------------------------------");
		}
		catch(Exception e)
		{
			logger.info("Exception is "+e);
		}
		

	}*/
	
private static class SettlementId implements RowMapper<SettlementBean> {

	@Override
	public SettlementBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		SettlementBean settlementBean = new SettlementBean();
		settlementBean.setInSettlement_id(rs.getInt("ID"));
		return settlementBean;
	}
}
private static class SettlementMaster implements RowMapper<FilterationBean> {

	@Override
	public FilterationBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		//logger.info("row num is "+rowNum);
	//	logger.info("header is "+rs.getString("FILE_HEADER"));
		/*while(rs.next())
		{*/
			//logger.info("header is "+rs.getString("TABLE_HEADER"));
		FilterationBean filterBean = new FilterationBean();
		
		filterBean.setStSearch_header(rs.getString("TABLE_HEADER"));
		filterBean.setStSearch_pattern(rs.getString("PATTERN"));
		filterBean.setStSearch_padding(rs.getString("PADDING"));
		//filterBean.setStsearch_charpos(rs.getString("CHARPOSITION"));
		filterBean.setStsearch_Startcharpos(rs.getString("START_CHARPOS"));
		filterBean.setStsearch_Endcharpos(rs.getString("CHARSIZE"));
		filterBean.setStSearch_Condition(rs.getString("CONDITION"));
		filterBean.setStSearch_Datatype(rs.getString("DATATYPE"));
		
		//search_params.add(filterBean);
	//	}
		return filterBean;
		
		
	}
}	
public void updateActionTakenTTUMRecord(FilterationBean filterBeanObj)throws Exception
{
	logger.info("***** FilterationDaoImpl.updateActionTakenTTUMRecord Start ****");
	int tableExist  = 0;
	Connection conn ;
	try
	{
		//String a[] = stTable_Name.split("_"); 
		//1.Get File ID from db
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { "CBS",filterBeanObj.getStCategory(),filterBeanObj.getStSubCategory() },Integer.class);
		logger.info("file id is "+file_id);
		
		String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
		logger.info("file headers are "+stFile_headers);
		
	/*	String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'MANUAL_ACTION_"+a[1].toUpperCase()+"'";
		//logger.info("check table "+CHECK_TABLE);
		tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		
		
		if(tableExist == 0)
		{
			//String columns = "CREATEDDATE, CREATEDBY, MAN_CONTRA_ACCOUNT"+stFile_headers;
			String tab_cols = "CREATEDDATE DATE, CREATEDBY VARCHAR(100 BYTE),FILEDATE DATE,MAN_CONTRA_ACCOUNT VARCHAR(100 BYTE)";
			
			String[] col_names = stFile_headers.trim().split(",");
			for(int i=0 ;i <col_names.length; i++)
			{
				tab_cols = tab_cols +","+ col_names[i]+" VARCHAR (100 BYTE) ";
			}
			
			
			
			CREATE_RECON_TABLE = "CREATE TABLE MANUAL_ACTION_"+a[1].toUpperCase() +"( "+tab_cols +" )";
			getJdbcTemplate().execute(CREATE_RECON_TABLE);
			logger.info("create table query "+CREATE_RECON_TABLE);
		}*/
		
		List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {filterBeanObj.getStMerger_Category()+"_CBS"},new ColumnsMapper());
		logger.info("TABLE_COLS== "+TABLE_COLS);
		String select_cols = "";
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
		
		String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_"+filterBeanObj.getStCategory()+"_CBS'";
		logger.info("CHECK_TABLE== "+CHECK_TABLE);
		tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		logger.info("tableExist== "+tableExist);
		if(tableExist == 1)
		{
			String ACTION_RECORDS = "";
			/*if(manualFileBeanObj.getStCategory().equals("ONUS"))
			{
				ACTION_RECORDS = "SELECT * FROM "+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" T1, TTUM_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile().toUpperCase()+" T2 WHERE T1.TRAN_PARTICULAR = T2.TRANSACTION_PARTICULARS" +
						" AND T1.TRAN_RMKS = T2.REMARKS AND T2.PART_TRAN_TYPE = 'D'";
				logger.info("ACTION RECORDS "+ACTION_RECORDS);
			}
			else
			{
				ACTION_RECORDS = "SELECT * FROM "+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" T1, TTUM_"
						+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile().toUpperCase()+" T2 WHERE T1.PARTICULARALS = T2.TRANSACTION_PARTICULARS" +
						" AND T1.REMARKS = T2.REMARKS AND T2.PART_TRAN_TYPE = 'D'";
				
						
			}
			*/
			
			//2. update these records in SETTLEMENT TABLE AS ACTION TAKEN ENTRIES
			/*String UPDATE_RECORDS = "UPDATE SETTLEMENT_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" SET DCRS_REMARKS = '"
					+manualFileBeanObj.getStMergerCategory()+"-ACTION-TAKEN' WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"' AND" +
						" (DCRS_REMARKS LIKE '%"+manualFileBeanObj.getStCategory()+"-UNRECON%'  " +
								"OR DCRS_REMARKS = '%"+manualFileBeanObj.getStCategory()+"-UNRECON-GENERATED-TTUM%' )";*/
			
			// WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%' ";
			
			//CHANGES MADE BY INT5779 ON 29 JAN 2018
			
/*			SELECT * FROM SETTLEMENT_AMEX_CBS WHERE REMARKS = '376535555331009' AND  AMOUNT = '10,000.00' AND DCRS_REMARKS LIKE '%AMEX-UNRECON%' AND
					 TO_CHAR(FILEDATE,'DD/MM/YYYY') = '13/01/2018' AND SUBSTR(PARTICULARALS,1,8) = 'ID007501' AND TO_NUMBER(SUBSTR(REF_NO,1,10)) = '195790';
					 
*/
			if(filterBeanObj.getStCategory().equals("AMEX"))
			{
							
				//inserting the classfied manual records in settlement table 
				/*String INSERT_RECORDS = "INSERT INTO SETTLEMENT_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+stFile_headers
						+") VALUES (SYSDATE,'INT5779',TO_DATE('"+manualFileBeanObj.getStFile_date()+"','DD/MM/YYYY'),'MAN-MATCHED'";
				String[] col_names = stFile_headers.split(",");

				for(int i=0;i<col_names.length;i++)
				{
					INSERT_RECORDS = INSERT_RECORDS+",?";
				}

				INSERT_RECORDS = INSERT_RECORDS + ")";
				
				logger.info("INSERT QUERY IS "+INSERT_RECORDS);

				insertBatch(INSERT_RECORDS, manual_rset, col_names, "");
*/				

				ACTION_RECORDS = "SELECT 'MAN-MATCHED',"+select_cols+" FROM "+filterBeanObj.getStCategory()+"_CBS t1 " +
						"WHERE EXISTS ( SELECT * FROM TTUM_AMEX_CBS t2 WHERE "+
								" TRIM(SUBSTR(T1.PARTICULARALS,0,20)) = TRIM(REPLACE(SUBSTR(T2.TRANSACTION_PARTICULARS,0,20),'-',' '))" +
								" AND T1.REMARKS = T2.REMARKS AND T2.PART_TRAN_TYPE = 'D')";
				logger.info("ACTION RECORDS "+ACTION_RECORDS);
				String INSERT_RECORDS = "INSERT INTO SETTLEMENT_"+filterBeanObj.getStCategory()+"_CBS (DCRS_REMARKS,"+select_cols+") "
							+ACTION_RECORDS;
				
				logger.info("ACTION_RECORDS QUERY IS "+ACTION_RECORDS);
				
				getJdbcTemplate().execute(INSERT_RECORDS);
				
				String DELETE_QUERY = "DELETE FROM "+filterBeanObj.getStMerger_Category()+"_CBS t1 " +
						"WHERE EXISTS ( SELECT * FROM TTUM_AMEX_CBS t2 WHERE "+
						" TRIM(SUBSTR(T1.PARTICULARALS,0,20)) = TRIM(REPLACE(SUBSTR(T2.TRANSACTION_PARTICULARS,0,20),'-',' '))" +
						" AND T1.REMARKS = T2.REMARKS AND T2.PART_TRAN_TYPE = 'D')";
				
				logger.info("DELETE QUERY IS "+DELETE_QUERY);
				
				getJdbcTemplate().execute(DELETE_QUERY);
				
				//UPDATE_RECORDS = UPDATE_RECORDS +" WHERE REMARKS = '' AND AMOUNT = ? AND SUBSTR(PARTICULARALS,1,8) = ? AND TO_NUMBER(SUBSTR(REF_NO,1,10)) = ? ";
				String UPDATE_RECORDS = "UPDATE SETTLEMENT_"+filterBeanObj.getStCategory()+"_CBS t1 SET DCRS_REMARKS = '"
						+filterBeanObj.getStMerger_Category()+"-ACTION-TAKEN' WHERE FILEDATE = '"+filterBeanObj.getStFile_date()+"' AND" +
							" (DCRS_REMARKS LIKE '%"+filterBeanObj.getStCategory()+"-UNRECON%'  " +
									"OR DCRS_REMARKS = '%"+filterBeanObj.getStCategory()+"-UNRECON-GENERATED-TTUM%' ) AND EXISTS (SELECT * FROM SETTLEMENT_"+
									filterBeanObj.getStCategory()+"_CBS"
										+" t2 , TTUM_AMEX_CBS T3 WHERE T2.DCRS_REMARKS = 'MAN-MATCHED' AND " +
										" (TRIM(REPLACE(SUBSTR(T3.TRANSACTION_PARTICULARS,0,20),'-',' ')) = TRIM(SUBSTR(T2.PARTICULARALS,0,20)) AND T2.REMARKS = T3.REMARKS AND T3.PART_TRAN_TYPE = 'D') " +
										" AND TO_NUMBER(T1.AMOUNT,'999999999.99') = TO_NUMBER(T2.AMOUNT,'999999999.99')"+  
										" AND SUBSTR(T1.PARTICULARALS,1,8) = SUBSTR(PARTICULARALS,5,8) " +
										//" AND TO_NUMBER(SUBSTR(T1.REF_NO,1,10)) = TO_NUMBER(SUBSTR(T2.REF_NO,1,10)))";
										" AND T1.REMARKS = T2.REF_NO AND TO_NUMBER(SUBSTR(T1.REF_NO,1,10)) = TO_NUMBER(SUBSTR(T3.TRANSACTION_PARTICULARS,-6,6)) " +
										"AND T2.FILEDATE ='"+filterBeanObj.getStFile_date()+"')";
				
				getJdbcTemplate().execute(UPDATE_RECORDS);
				
				logger.info("UPDATE ACTION TAKEN QUERY "+UPDATE_RECORDS);
			}
			else if(filterBeanObj.getStCategory().equalsIgnoreCase("ONUS"))	 	
			{
				String UPDATE_RECORDS = "UPDATE SETTLEMENT_"+filterBeanObj.getStCategory()+"_CBS SET DCRS_REMARKS = '"
						+filterBeanObj.getStMerger_Category()+"-ACTION-TAKEN' WHERE FILEDATE = '"+filterBeanObj.getStFile_date()+"' AND" +
							" (DCRS_REMARKS LIKE '%"+filterBeanObj.getStCategory()+"-UNRECON%'  " +
									"OR DCRS_REMARKS = '%"+filterBeanObj.getStCategory()+"-UNRECON-GENERATED-TTUM%' )";
				ACTION_RECORDS = "SELECT * FROM "+filterBeanObj.getStCategory()+"_CBS T1, TTUM_"+filterBeanObj.getStCategory()+"_CBS T2 WHERE T1.TRAN_PARTICULAR = T2.TRANSACTION_PARTICULARS" +
						" AND T1.TRAN_RMKS = T2.REMARKS AND T2.PART_TRAN_TYPE = 'D'";
				logger.info("ACTION RECORDS "+ACTION_RECORDS);

				conn = getConnection();
				PreparedStatement action_statement = conn.prepareStatement(ACTION_RECORDS);
				ResultSet action_rset = action_statement.executeQuery();
				
				//select COLUMNS FROM TABLE FOR WHERE CONDITION
				//		List<Manual> Update_cols_value = getJdbcTemplate().query(GET_UPDATE_COLS,new Object[]{},new ColumnsValueMapper());
				List<ManualFileBean> Update_cols = getJdbcTemplate().query(GET_UPDATE_COLS,new Object[]{},new ColumnMapper());

				String[] stUpdate_Cols = new String[Update_cols.size()];
				String[] stDelete_cols = new String[Update_cols.size()];
				for(int i = 0 ;i <Update_cols.size() ; i++)
				{
					UPDATE_RECORDS = UPDATE_RECORDS + " AND "+Update_cols.get(i).getStUpdate_Column() +" = ? ";

				}
				for(int i=0;i<Update_cols.size() ; i++)
				{
					stUpdate_Cols[i] = Update_cols.get(i).getStUpdateCol_Value();
					//stUpdate_Cols[i] = Update_cols.get(i).getStUpdate_Column();
					stDelete_cols[i] = Update_cols.get(i).getStUpdate_Column();
				}
				logger.info("UPDATE_RECORDS QUERY "+UPDATE_RECORDS);
				updateBatch(UPDATE_RECORDS, action_rset, stUpdate_Cols);

				//INSERT IN SETTLEMENT TABLE AND DELETE FROM ONUS_MANUAL_CBS TABLE
				PreparedStatement manual_statement = conn.prepareStatement(ACTION_RECORDS);
				ResultSet manual_rset = manual_statement.executeQuery();

				PreparedStatement delete_statement = conn.prepareStatement(ACTION_RECORDS);
				ResultSet delete_rset = delete_statement.executeQuery();



				
					stFile_headers = stFile_headers+",MAN_CONTRA_ACCOUNT";
				

				String INSERT_RECORDS = "INSERT INTO SETTLEMENT_"+filterBeanObj.getStCategory()+"_CBS (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+stFile_headers
						+") VALUES (SYSDATE,'"+filterBeanObj.getStEntry_by()+"',TO_DATE('"+filterBeanObj.getStFile_date()+"','DD/MM/YYYY'),'MAN-MATCHED'";
				String[] col_names = stFile_headers.split(",");

				for(int i=0;i<col_names.length;i++)
				{
					INSERT_RECORDS = INSERT_RECORDS+",?";
				}

				INSERT_RECORDS = INSERT_RECORDS + ")";

				String DELETE_RECORDS = "DELETE FROM "+filterBeanObj.getStCategory()+"_CBS WHERE ";

				for(int i = 0 ;i <Update_cols.size() ; i++)
				{
					if(i == Update_cols.size()-1)
						DELETE_RECORDS = DELETE_RECORDS +Update_cols.get(i).getStUpdate_Column() +" = ? ";
					else
						DELETE_RECORDS = DELETE_RECORDS + Update_cols.get(i).getStUpdate_Column() +" = ? AND ";

				}

				logger.info("DELETE QUERY IS "+DELETE_RECORDS);
				logger.info("INSERT QUERY IS "+INSERT_RECORDS);

				insertproxyBatch(INSERT_RECORDS, manual_rset, col_names, "");
				logger.info("insert task completed------------------------------------------------------------------------------");
				insertproxyBatch(DELETE_RECORDS, delete_rset, stDelete_cols, "");
				logger.info("COMPLETED ACTION ON TTUM ENTRIES!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

			}
		}
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "FilterationDaoImpl.updateActionTakenTTUMRecord");
		logger.error(" error in FilterationDaoImpl.updateActionTakenTTUMRecord", new Exception("FilterationDaoImpl.updateActionTakenTTUMRecord",e));
		throw e;
	}
	
	logger.info("***** FilterationDaoImpl.updateActionTakenTTUMRecord End ****");
	
}
public void updateBatch(final String QUERY, final ResultSet rset,final String[] columns){
	PreparedStatement ps;
	Connection con;
	//logger.info("query is 123 "+QUERY);
	//logger.info("coulmn size is "+columns.length);
//	trns_srl = Integer.parseInt(new SimpleDateFormat("dd").format(cur_dt)) * Integer.parseInt(new SimpleDateFormat("MM").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("yyyy").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("ss").format(cur_dt));
	
	//for(int i=0; i<loadBeanList.size(); i += batchSize)
	int flag = 1;
	int batch = 1;
	
	try {
		
		con = getConnection();
		
		ps = con.prepareStatement(QUERY);
		
		//logger.info("value is "+rset.getString("MSGTYPE"));
		//int batch = 1;
		//int value = 3;
		while(rset.next())
		{
			flag++;
			//ps.setInt(1, seg_tran_id);
			 /*if(value == columns.length)
				 value = 3;*/
			
			for(int i = 1 ; i <= columns.length; i++)
			{
				logger.info("column name is "+columns[i-1].trim()+"column value is "+rset.getString(columns[i-1].trim()));
				//logger.info("column value is "+rset.getString(columns[i-1].trim()));
				if(rset.getString(columns[i-1].trim()) == null)	
				{	
					ps.setString((i), "!null!");
					//logger.info(i+" = "+"'!null!'");
				
				}
				else
				{
					ps.setString((i), rset.getString(columns[i-1].trim()));
					//logger.info(i+" = "+rset.getString(columns[i-1].trim()));
				}
				
			//	value++;
			//	logger.info("i is "+i);
			}
			ps.addBatch();
			
			if(flag == 100)
			{
				//logger.info("******** FLAG IS "+flag);
				flag = 1;
			
				ps.executeBatch();
				logger.info("Executed batch is "+batch);
				batch++;
			}
		}
		ps.executeBatch();
		logger.info("completed insertion");
		
		
	} catch (DataAccessException | SQLException e) {
		logger.info("INSERT BATCH EXCEPTION "+e);
	}
}

private static class ColumnMapper implements RowMapper<ManualFileBean> {

	@Override
	public ManualFileBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		ManualFileBean manFileBeanObj = new ManualFileBean();
		
		manFileBeanObj.setStUpdate_Column(rs.getString("FILE_HEADER"));
		manFileBeanObj.setStUpdateCol_Value(rs.getString("COLUMN_NAME").replace(" ", "_"));
		//String stColumns = rs.getString("FILE_HEADER").replace(" ", "_");
		return manFileBeanObj;
		//return stColumns;
		
	}
}


@Override
public boolean cardlesstxn(FilterationBean filterBean) {
	
	
		
		try {
			return Classifydata(filterBean.getStCategory(), filterBean.getStFile_Name(), filterBean.getStFile_date(),filterBean.getStEntry_by() );
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		
	
	
}

public boolean Classifydata(String category,String i_filename ,String filedate,String entry_by) throws ParseException, Exception {
	try {
		logger.info("***** ReconProcessDaoImpl.ISSClassifydata Start ****");
		
		String response = null;
		Map<String, Object> inParams = new HashMap<String, Object>();

		inParams.put("I_CATEGORY", category);
		inParams.put("I_FILENAME ",i_filename  );
		inParams.put("I_FILE_DATE",filedate);
		inParams.put("I_ENTRY_BY", entry_by);
		
		
		CardlessClassificaton acqclassificaton = new CardlessClassificaton(getJdbcTemplate());
		Map<String, Object> outParams = acqclassificaton.execute(inParams);

		//logger.info("outParams msg1"+outParams.get("msg1"));
		logger.info("***** ReconProcessDaoImpl.CardlessClassificaton End ****");
		
		if (outParams.get("ERROR_MESSAGE") != null) {
		

					return false;
				}else {
					
					return true;
				}
			

		
		
	} catch (Exception e) {
		demo.logSQLException(e, "ReconProcessDaoImpl.ISSClassifydata");
		logger.error(" error in  ReconProcessDaoImpl.ISSClassifydata", new Exception(" ReconProcessDaoImpl.ISSClassifydata",e));
		return false;
	}
	
}

class CardlessClassificaton extends StoredProcedure {
	private static final String procName = "CARDLESSCLASSIFICATON";

	CardlessClassificaton(JdbcTemplate JdbcTemplate) {
		super(JdbcTemplate, procName);
		setFunction(false);
		
		
		declareParameter(new SqlParameter("I_FILE_DATE",OracleTypes.VARCHAR));
		declareParameter(new SqlParameter("I_CATEGORY",OracleTypes.VARCHAR));
		declareParameter(new SqlParameter("I_FILENAME ",OracleTypes.VARCHAR));
		declareParameter(new SqlParameter("I_ENTRY_BY",	OracleTypes.VARCHAR));
		declareParameter(new SqlOutParameter("ERROR_CODE", OracleTypes.VARCHAR));
		declareParameter(new SqlOutParameter("ERROR_MESSAGE", OracleTypes.VARCHAR));
		compile();
	}
}



}
