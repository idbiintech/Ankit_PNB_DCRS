package com.recon.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;

import com.recon.dao.GLReportdao;
import com.recon.model.GLRemitterListBean;
import com.recon.model.GLRemitterReportBean;

import oracle.jdbc.internal.OracleTypes;

public class GLReportDaoImpl extends JdbcDaoSupport implements GLReportdao {

	public List<GLRemitterReportBean> getNFSIssGLData(String fileDate, String fileName)
	{
		System.out.println("Inside getGLRemitterData");
		GLRemitterReportBean beanObj = new GLRemitterReportBean();
		List<GLRemitterReportBean> obj = new ArrayList<GLRemitterReportBean>();
		String tableName = "gl_nfs_payable";
		try {
			
			if(fileName.equalsIgnoreCase("Credit_Adjustment"))
			{
				tableName = "gl_nfs_iss_crdadj";
			}
			else if(fileName.equalsIgnoreCase("Debit_Adjustment"))
			{
				tableName = "nfs_gl_dr_adj";
			}
			else if(fileName.equalsIgnoreCase("ChargeBack"))
			{
				tableName = "gl_nfs_cr_adj";
			}
			else if(fileName.equalsIgnoreCase("PreArbitration"))
			{
				tableName = "GL_NFS_ISS_pre_arb";
			}
			
			String getData = "select * from "+tableName+" where transaction_date = to_Date(?,'dd/mm/yyyy') ORDER by s_no asc";
			System.out.println("getData is "+getData);
			
			obj = (List<GLRemitterReportBean>) getJdbcTemplate().
					query(getData, new Object[] {fileDate},new ResultSetExtractor<List<GLRemitterReportBean>>(){
				public List<GLRemitterReportBean> extractData(ResultSet rs)throws SQLException {
					List<GLRemitterReportBean> datalst = new ArrayList<GLRemitterReportBean>();
					GLRemitterReportBean glReportBean;
					
					while (rs.next()) {
						glReportBean  = new GLRemitterReportBean();
						System.out.println("rs.getInt(\"S_NO\") "+rs.getInt("S_NO"));
						glReportBean.setSr_No(rs.getInt("S_NO"));
						glReportBean.setParticulars(rs.getString("PARTICULARS"));
						glReportBean.setCredit_Amt(rs.getString("CREDIT_AMT"));
						glReportBean.setDebit_Amt(rs.getString("DEBIT_AMT"));
						glReportBean.setBalance(rs.getString("BALANCE"));
						glReportBean.setCrdr_Diff(rs.getString("CREDIT_DEBIT_DIFF"));
						datalst.add(glReportBean);
					}
					return datalst;
				}
			});
			
			
		}
		catch(Exception e)
		{
			System.out.println("Exception is "+e);
		}
		
		return obj;
	}
	
	public String getNFSIssCheckerFlag(String fileDate, String fileName)
	{
		String tableName = "gl_nfs_payable";
		try
		{
			if(fileName.equalsIgnoreCase("Credit_Adjustment"))
			{
				tableName = "gl_nfs_iss_crdadj";
			}
			else if(fileName.equalsIgnoreCase("Debit_Adjustment"))
			{
				tableName = "nfs_gl_dr_adj";
			}
			else if(fileName.equalsIgnoreCase("ChargeBack"))
			{
				tableName = "gl_nfs_cr_adj";
			}
			else if(fileName.equalsIgnoreCase("PreArbitration"))
			{
				tableName = "GL_NFS_ISS_pre_arb";
			}
			
			String checkerFlag = getJdbcTemplate().queryForObject("select distinct checker_flag from "+tableName+" where transaction_date = "
					+ "to_Date(?,'dd/mm/yyyy')", new Object[] {fileDate},String.class);
			
			System.out.println("Count is "+checkerFlag);
			return checkerFlag;
		}
		catch(Exception e)
		{
			System.out.println("Exception in getCheckerFlag "+e);
			return "Exception Occured";
		}
		
	}
	
	public boolean saveNFSIssGLData(GLRemitterListBean beanObjLst, String fileName, final String userId)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		String tableName = "gl_nfs_payable";
		try
		{
			if(fileName.equalsIgnoreCase("Credit_Adjustment"))
			{
				tableName = "gl_nfs_iss_crdadj";
			}
			else if(fileName.equalsIgnoreCase("Debit_Adjustment"))
			{
				tableName = "nfs_gl_dr_adj";
			}
			else if(fileName.equalsIgnoreCase("ChargeBack"))
			{
				tableName = "gl_nfs_cr_adj";
			}
			else if(fileName.equalsIgnoreCase("PreArbitration"))
			{
				tableName = "GL_NFS_ISS_pre_arb";
			}
			
			final String fileDate = beanObjLst.getFileDate();
			
			String saveData = null;
			/*for(GLRemitterReportBean beanObj : beanObjLst.getGlRemitterBean())
			{
				saveData = "update gl_nfs_payable set debit_amt = '"+beanObj.getDebit_Amt()+"',"
						+ " credit_amt = '"+beanObj.getCredit_Amt()+"', "
								+ " checker_flag = 'Y' where s_no = '"+beanObj.getSr_No()
						+"' and"
						+ " transaction_date = to_date('"+fileDate+"','dd/mm/yyyy')";
				
				System.out.println("Query is "+saveData);
				
				getJdbcTemplate().execute(saveData);
			}*/
			
			int[][] updateCounts = getJdbcTemplate().batchUpdate(
					" update   "+tableName+"  SET CHECKER_FLAG='Y'  , CREDIT_AMT =?  , "
							+ "DEBIT_AMT =? , BALANCE=?, CHECKER_DATE =sysdate ,CHECKER_ID=?, credit_debit_diff = ?"
							+ "  where TRANSACTION_DATE=to_date(? ,'dd-mon-yyyy') and  S_NO =?",
					beanObjLst.getGlRemitterBean(), 500, new ParameterizedPreparedStatementSetter<GLRemitterReportBean>() {
						public void setValues(PreparedStatement ps, GLRemitterReportBean argument) throws SQLException {
							ps.setString(1, argument.getCredit_Amt());
							ps.setString(2, argument.getDebit_Amt());
							ps.setString(3, argument.getBalance());
							ps.setString(4, userId);
							ps.setString(5, argument.getCrdr_Diff());
							ps.setString(6, fileDate);
							ps.setInt(7, argument.getSr_No());
						}
					});
			
			/// call update procedure 
			updateNFSGLBal updateBal = new updateNFSGLBal(getJdbcTemplate());
			inParams.put("T_DATE", fileDate);
			//outParams = updateBal.execute(inParams);

			if(outParams !=null && outParams.get("resp") != null)
			{
				System.out.println("OUT PARAM IS "+outParams.get("resp"));
			}
			
		}
		catch(Exception e)
		{
			System.out.println("Exception in saveRemitterData "+e);
			return false;
		}
		return true;
	}
	
	/************************* NFS ACQ *********************************/
	public List<GLRemitterReportBean> getNFSAcqGLData(String fileDate, String fileName)
	{
		System.out.println("Inside getGLRemitterData");
		GLRemitterReportBean beanObj = new GLRemitterReportBean();
		List<GLRemitterReportBean> obj = new ArrayList<GLRemitterReportBean>();
		String tableName = "gl_nfs_payable";
		try {
			
			if(fileName.equalsIgnoreCase("Credit_Adjustment"))
			{
				tableName = "GL_NFS_ACQ_CRD_ADJ_ARB";
			}
			else if(fileName.equalsIgnoreCase("PreArbitration"))
			{
				tableName = "GL_NFS_ACQ_PREARB_ARB";
			}
			else if(fileName.equalsIgnoreCase("ChargeBack"))
			{
				tableName = "GL_NFS_ACQ_chargeback";
			}
			
			String getData = "select * from "+tableName+" where transaction_date = to_Date(?,'dd/mm/yyyy') ORDER by s_no asc";
			System.out.println("getData is "+getData);
			
			obj = (List<GLRemitterReportBean>) getJdbcTemplate().
					query(getData, new Object[] {fileDate},new ResultSetExtractor<List<GLRemitterReportBean>>(){
				public List<GLRemitterReportBean> extractData(ResultSet rs)throws SQLException {
					List<GLRemitterReportBean> datalst = new ArrayList<GLRemitterReportBean>();
					GLRemitterReportBean glReportBean;
					
					while (rs.next()) {
						glReportBean  = new GLRemitterReportBean();
						System.out.println("rs.getInt(\"S_NO\") "+rs.getInt("S_NO"));
						glReportBean.setSr_No(rs.getInt("S_NO"));
						glReportBean.setParticulars(rs.getString("PARTICULARS"));
						glReportBean.setCredit_Amt(rs.getString("CREDIT_AMT"));
						glReportBean.setDebit_Amt(rs.getString("DEBIT_AMT"));
						glReportBean.setBalance(rs.getString("BALANCE"));
						glReportBean.setCrdr_Diff(rs.getString("CREDIT_DEBIT_DIFF"));
						datalst.add(glReportBean);
					}
					return datalst;
				}
			});
			
			
		}
		catch(Exception e)
		{
			System.out.println("Exception is "+e);
		}
		
		return obj;
	}
	
	public String getNFSAcqCheckerFlag(String fileDate, String fileName)
	{
		String tableName = "gl_nfs_payable";
		try
		{
			if(fileName.equalsIgnoreCase("Credit_Adjustment"))
			{
				tableName = "GL_NFS_ACQ_CRD_ADJ_ARB";
			}
			else if(fileName.equalsIgnoreCase("PreArbitration"))
			{
				tableName = "GL_NFS_ACQ_PREARB_ARB";
			}
			else if(fileName.equalsIgnoreCase("ChargeBack"))
			{
				tableName = "GL_NFS_ACQ_chargeback";
			}
			String checkerFlag = getJdbcTemplate().queryForObject("select distinct checker_flag from "+tableName+" where transaction_date = "
					+ "to_Date(?,'dd/mm/yyyy')", new Object[] {fileDate},String.class);
			
			System.out.println("Count is "+checkerFlag);
			return checkerFlag;
		}
		catch(Exception e)
		{
			System.out.println("Exception in getCheckerFlag "+e);
			return "Exception Occured";
		}
		
	}
	
	public boolean saveNFSAcqGLData(GLRemitterListBean beanObjLst, String fileName, final String userId)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		String tableName = "gl_nfs_payable";
		try
		{
			if(fileName.equalsIgnoreCase("Credit_Adjustment"))
			{
				tableName = "GL_NFS_ACQ_CRD_ADJ_ARB";
			}
			else if(fileName.equalsIgnoreCase("PreArbitration"))
			{
				tableName = "GL_NFS_ACQ_PREARB_ARB";
			}
			else if(fileName.equalsIgnoreCase("ChargeBack"))
			{
				tableName = "GL_NFS_ACQ_chargeback";
			}
			
			final String fileDate = beanObjLst.getFileDate();
			
			String saveData = null;
			int[][] updateCounts = getJdbcTemplate().batchUpdate(
					" update   "+tableName+"  SET CHECKER_FLAG='Y'  , CREDIT_AMT =?  , "
							+ "DEBIT_AMT =? , BALANCE=?, CHECKER_DATE =sysdate ,CHECKER_ID=?, credit_debit_diff = ?"
							+ "  where TRANSACTION_DATE=to_date(? ,'dd-mon-yyyy') and  S_NO =?",
					beanObjLst.getGlRemitterBean(), 500, new ParameterizedPreparedStatementSetter<GLRemitterReportBean>() {
						public void setValues(PreparedStatement ps, GLRemitterReportBean argument) throws SQLException {
							ps.setString(1, argument.getCredit_Amt());
							ps.setString(2, argument.getDebit_Amt());
							ps.setString(3, argument.getBalance());
							ps.setString(4, userId);
							ps.setString(5, argument.getCrdr_Diff());
							ps.setString(6, fileDate);
							ps.setInt(7, argument.getSr_No());
						}
					});
			
			/// call update procedure 
			updateNFSGLBal updateBal = new updateNFSGLBal(getJdbcTemplate());
			inParams.put("T_DATE", fileDate);
			//outParams = updateBal.execute(inParams);

			if(outParams !=null && outParams.get("resp") != null)
			{
				System.out.println("OUT PARAM IS "+outParams.get("resp"));
			}
			
		}
		catch(Exception e)
		{
			System.out.println("Exception in saveRemitterData "+e);
			return false;
		}
		return true;
	}

	/************* VISA CODING *******************************/
	public List<GLRemitterReportBean> getVisaGLData(String fileDate,String subCategory)
	{
		System.out.println("Inside getGLRemitterData");
		GLRemitterReportBean beanObj = new GLRemitterReportBean();
		List<GLRemitterReportBean> obj = new ArrayList<GLRemitterReportBean>();
		String tableName = "";
		try {
			if(subCategory.equalsIgnoreCase("DOMESTIC"))
			{
				tableName = "gl_visa_domestic";
			}
			else
			{
				tableName = "gl_visa_international";
			}
								
			String getData = "select * from "+tableName+" where transaction_date = to_Date(?,'dd/mm/yyyy') ORDER by s_no asc";
			System.out.println("getData is "+getData);
			
			obj = (List<GLRemitterReportBean>) getJdbcTemplate().
					query(getData, new Object[] {fileDate},new ResultSetExtractor<List<GLRemitterReportBean>>(){
				public List<GLRemitterReportBean> extractData(ResultSet rs)throws SQLException {
					List<GLRemitterReportBean> datalst = new ArrayList<GLRemitterReportBean>();
					GLRemitterReportBean glReportBean;
					
					while (rs.next()) {
						glReportBean  = new GLRemitterReportBean();
						System.out.println("rs.getInt(\"S_NO\") "+rs.getInt("S_NO"));
						glReportBean.setSr_No(rs.getInt("S_NO"));
						glReportBean.setParticulars(rs.getString("PARTICULARS"));
						glReportBean.setCredit_Amt(rs.getString("CREDIT_AMT"));
						glReportBean.setDebit_Amt(rs.getString("DEBIT_AMT"));
						glReportBean.setBalance(rs.getString("BALANCE"));
						glReportBean.setCrdr_Diff(rs.getString("CREDIT_DEBIT_DIFF"));
						datalst.add(glReportBean);
					}
					return datalst;
				}
			});
			
			
		}
		catch(Exception e)
		{
			System.out.println("Exception is "+e);
		}
		
		return obj;
	}
	
	public String getVisaCheckerFlag(String fileDate,String subCategory)
	{
		String tableName = "";
		try
		{
			if(subCategory.equalsIgnoreCase("DOMESTIC"))
			{
				tableName = "gl_visa_domestic";
			}
			else
			{
				tableName = "gl_visa_international";
			}
			
			String checkerFlag = getJdbcTemplate().queryForObject("select distinct checker_flag from "+tableName+" where transaction_date = "
					+ "to_Date(?,'dd/mm/yyyy')", new Object[] {fileDate},String.class);
			
			System.out.println("Count is "+checkerFlag);
			return checkerFlag;
		}
		catch(Exception e)
		{
			System.out.println("Exception in getCheckerFlag "+e);
			return "Exception Occured";
		}
		
	}
	
	public boolean saveVisaGLData(GLRemitterListBean beanObjLst, final String userId)
	{
		String tableName = "";
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try
		{
			if(beanObjLst.getStSubCategory().equalsIgnoreCase("DOMESTIC"))
			{
				tableName = "gl_visa_domestic";
			}
			else
			{
				tableName = "gl_visa_international";
			}
			
			final String fileDate = beanObjLst.getFileDate();
			
			String saveData = null;
			/*for(GLRemitterReportBean beanObj : beanObjLst.getGlRemitterBean())
			{
				saveData = "update "+tableName+" set debit_amt = '"+beanObj.getDebit_Amt()+"',"
						+ " credit_amt = '"+beanObj.getCredit_Amt()+"', "
								+ " checker_flag = 'Y' where s_no = '"+beanObj.getSr_No()
						+"' and"
						+ " transaction_date = to_date('"+fileDate+"','dd/mm/yyyy')";
				
				System.out.println("Query is "+saveData);
				
				getJdbcTemplate().execute(saveData);
			}*/
			
			int[][] updateCounts = getJdbcTemplate().batchUpdate(
					" update   gl_remitter  SET CHECKER_FLAG='Y'  , CREDIT_AMT =?  , "
							+ "DEBIT_AMT =? , BALANCE=?, CHECKER_DATE =sysdate ,CHECKER_ID=?, credit_debit_diff = ?"
							+ "  where TRANSACTION_DATE=to_date(? ,'dd-mon-yyyy') and  S_NO =?",
					beanObjLst.getGlRemitterBean(), 500, new ParameterizedPreparedStatementSetter<GLRemitterReportBean>() {
						public void setValues(PreparedStatement ps, GLRemitterReportBean argument) throws SQLException {
							ps.setString(1, argument.getCredit_Amt());
							ps.setString(2, argument.getDebit_Amt());
							ps.setString(3, argument.getBalance());
							ps.setString(4, userId);
							ps.setString(5, argument.getCrdr_Diff());
							ps.setString(6, fileDate);
							ps.setInt(7, argument.getSr_No());
						}
					});
			
			/// call update procedure 
			try
			{
				if(beanObjLst.getStSubCategory().equalsIgnoreCase("DOMESTIC"))
				{
					updateVisaDomesticBal updateBal = new updateVisaDomesticBal(getJdbcTemplate());
					inParams.put("T_DATE", fileDate);
					//outParams = updateBal.execute(inParams);

					if(outParams !=null && outParams.get("resp") != null)
					{
						System.out.println("OUT PARAM IS "+outParams.get("resp"));
					}
				}
				else
				{
					updateVisaInternationalBal updateBal = new updateVisaInternationalBal(getJdbcTemplate());
					inParams.put("T_DATE", fileDate);
					//outParams = updateBal.execute(inParams);

					if(outParams !=null && outParams.get("resp") != null)
					{
						System.out.println("OUT PARAM IS "+outParams.get("resp"));
					}
				
				}
			}
			catch(Exception sqlexp)
			{
				System.out.println("Exception in updating balance of remitter "+sqlexp);
			}
			
			
		}
		catch(Exception e)
		{
			System.out.println("Exception in saveRemitterData "+e);
			return false;
		}
		return true;
	}
	
	private class updateVisaDomesticBal extends StoredProcedure{
		private static final String proc = "gl_visa_domestic_cal_proc";
		public updateVisaDomesticBal(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,proc);
			declareParameter(new SqlParameter("T_DATE",OracleTypes.VARCHAR));
			compile();
		}

	}
	
	private class updateVisaInternationalBal extends StoredProcedure{
		private static final String proc = "gl_visa_international_cal_proc";
		public updateVisaInternationalBal(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,proc);
			declareParameter(new SqlParameter("T_DATE",OracleTypes.VARCHAR));
			compile();
		}

	}
	
	private class updateNFSGLBal extends StoredProcedure{
		private static final String proc = "gl_nfs_payable_cal_proc";
		public updateNFSGLBal(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,proc);
			declareParameter(new SqlParameter("T_DATE",OracleTypes.VARCHAR));
			compile();
		}

	}
}
