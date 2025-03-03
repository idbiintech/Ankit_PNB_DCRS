package com.recon.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;

import com.recon.dao.MisReportDao;
import com.recon.model.DownloadICCW;
import com.recon.model.DownloadICCW1;
import com.recon.model.DownloadICCW2;
import com.recon.model.GlReportModel;
import com.recon.model.GlSettlementModel;
import com.recon.model.RupayIntMisReportModel;

import oracle.jdbc.OracleTypes;

public class MisReportDaoImpl extends JdbcDaoSupport implements MisReportDao{

	@Override
	public boolean rupayProcessMisReport(String fdate) {
		System.out.println(fdate);
		String response = null;
		Map<String, Object> inParams = new HashMap<String, Object>();
		inParams.put("v_REPORTDATE", fdate);
		rupayProcessMisReport knockoffTTUM = new rupayProcessMisReport(getJdbcTemplate());
		Map<String, Object> outParams = knockoffTTUM.execute(inParams);
		return true;
	}
	
	class rupayProcessMisReport extends StoredProcedure {

		private static final String procName = "RUP_INT_DAILY_MIS_REPORT";

		rupayProcessMisReport(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("v_REPORTDATE", OracleTypes.VARCHAR));
			compile();
		}
	}
	
	@Override
	public boolean rupayProcessDomMisReport(String fdate) {
		System.out.println(fdate);
		String response = null;
		Map<String, Object> inParams = new HashMap<String, Object>();
		inParams.put("v_REPORTDATE", fdate);
		rupayProcessDomMisReport knockoffTTUM = new rupayProcessDomMisReport(getJdbcTemplate());
		Map<String, Object> outParams = knockoffTTUM.execute(inParams);
		return true;
	}
	
	class rupayProcessDomMisReport extends StoredProcedure {

		private static final String procName = "RUP_DOM_DAILY_MIS_REPORT";

		rupayProcessDomMisReport(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("v_REPORTDATE", OracleTypes.VARCHAR));
			compile();
		}
	}
	
	@Override
	public boolean nfsProcessPayMisReport(String fdate) {
		System.out.println(fdate);
		String response = null;
		Map<String, Object> inParams = new HashMap<String, Object>();
		inParams.put("v_REPORTDATE", fdate);
		nfsProcessPayMisReport knockoffTTUM = new nfsProcessPayMisReport(getJdbcTemplate());
		Map<String, Object> outParams = knockoffTTUM.execute(inParams);
		return true;
	}
	
	class nfsProcessPayMisReport extends StoredProcedure {

		private static final String procName = "NFS_PAY_DAILY_MIS_REPORT";

		nfsProcessPayMisReport(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("v_REPORTDATE", OracleTypes.VARCHAR));
			compile();
		}
	}


	@Override
	public boolean nfsProcessRecMisReport(String fdate) {
		System.out.println(fdate);
		String response = null;
		Map<String, Object> inParams = new HashMap<String, Object>();
		inParams.put("v_REPORTDATE", fdate);
		nfsProcessRecMisReport knockoffTTUM = new nfsProcessRecMisReport(getJdbcTemplate());
		Map<String, Object> outParams = knockoffTTUM.execute(inParams);
		return true;
	}
	
	class nfsProcessRecMisReport extends StoredProcedure {

		private static final String procName = "NFS_REC_DAILY_MIS_REPORT";

		nfsProcessRecMisReport(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("v_REPORTDATE", OracleTypes.VARCHAR));
			compile();
		}
	}
	
	@Override
	public boolean visaProcessMisReport(String fdate) {
		System.out.println(fdate);
		String response = null;
		Map<String, Object> inParams = new HashMap<String, Object>();
		inParams.put("v_REPORTDATE", fdate);
		visaProcessMisReport knockoffTTUM = new visaProcessMisReport(getJdbcTemplate());
		Map<String, Object> outParams = knockoffTTUM.execute(inParams);
		return true;
	}
	
	
	class visaProcessMisReport extends StoredProcedure {

		private static final String procName = "VISA_DAILY_MIS_REPORT";

		visaProcessMisReport(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("v_REPORTDATE", OracleTypes.VARCHAR));
			compile();
		}
	}
	
	
	@Override
	public List<RupayIntMisReportModel> downloadRupayMisReport(String date,String tableName) {
		MisReportDaoImpl obj = new MisReportDaoImpl();
		date = obj.parseDate(date);
		String query = "SELECT REPORT_DATE,PARTICULARS,TXN_TYP,RESPONSE_CODE,COUNT,AMOUNT FROM "+tableName+
				" WHERE REPORT_DATE = '"+date+"'";

		
		System.out.println(tableName+" Daily MIS Report Query: " + query);
		List<RupayIntMisReportModel> RupayIntMisReport = getJdbcTemplate().query(query, new RowMapper<RupayIntMisReportModel>() {

			public RupayIntMisReportModel mapRow(ResultSet rs, int row) throws SQLException {

				RupayIntMisReportModel u = new RupayIntMisReportModel();

				u.setREPORT_DATE(rs.getString(1));
				u.setPARTICULARS(rs.getString(2));
				u.setTXN_TYP(rs.getString(3));
				u.setRESPONSE_CODE(rs.getString(4));
				u.setCOUNT(rs.getString(5));
				u.setAMOUNT(rs.getString(6));

				return u;
			}
		});

		return RupayIntMisReport;
	}


	 public String parseDate(String date) {
		 String dateArray[] = date.split("-");
		 String Month="";
		 switch(dateArray[1]) {
		  case "01":
			  Month="Jan";
		    break;
		  case "02":
			  Month="Feb";
		    break;
		  case "03":
			  Month="Mar";
		    break;
		  case "04":
			  Month="Apr";
		    break;
		  case "05":
			  Month="May";
		    break;
		  case "06":
			  Month="Jun";
		    break;
		  case "07":
			  Month="Jul";
		    break;
		  case "08":
			  Month="Aug";
		    break;
		  case "09":
			  Month="Sep";
		    break;
		  case "10":
			  Month="Oct";
		    break;
		  case "11":
			  Month="Nov";
		    break;
		  case "12":
			  Month="Dec";
		    break;
		  default:
		    // code block
		}
		 date = dateArray[0]+"-"+Month+"-"+dateArray[2];
		 System.out.println("date :"+date);
		return date;
	     
	  }

	 
	 

	@Override
	public void RupayDomGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		RupayDomGlReportProcess procedure = new RupayDomGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
	}
	



	private class RupayDomGlReportProcess extends StoredProcedure {
		private static final String procName = "RUPAY_GL_DOMESTIC";

		RupayDomGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}
	
	

	@Override
	public void VisaDomGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		VisaDomGlReportProcess procedure = new VisaDomGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
		
	}
	
	private class VisaDomGlReportProcess extends StoredProcedure {
		private static final String procName = "VISA_DOMESTIC_PAYABLE_GL";

		VisaDomGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}
	
	@Override
	public void VisaIntGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		VisaIntGlReportProcess procedure = new VisaIntGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
	}

	private class VisaIntGlReportProcess extends StoredProcedure {
		private static final String procName = "VISA_INTERNATIONAL_PAYABLE_GL";

		VisaIntGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}
	
	@Override
	public void RupayMirrorGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		RupayMirrorGlReportProcess procedure = new RupayMirrorGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
		
	}
	
	private class RupayMirrorGlReportProcess extends StoredProcedure {
		private static final String procName = "RUPAY_GL_MIRROR";

		RupayMirrorGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}
	
	@Override
	public void NFSMirrorGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		NFSMirrorGlReportProcess procedure = new NFSMirrorGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
		
	}
	
	private class NFSMirrorGlReportProcess extends StoredProcedure {
		private static final String procName = "NFS_MIRROR_GL";

		NFSMirrorGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}
	
	@Override
	public void RupayMirrorIRGCSGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		RupayMirrorIRGCSGlReportProcess procedure = new RupayMirrorIRGCSGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
		
	}
	
	private class RupayMirrorIRGCSGlReportProcess extends StoredProcedure {
		private static final String procName = "RUPAY_GL_IRGCS_MIRROR";

		RupayMirrorIRGCSGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}

	
	@Override
	public void NfsAcqChargebackGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		System.out.println("fdate dao: "+fdate);
		NfsAcqChargebackGlReportProcess procedure = new NfsAcqChargebackGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
	}

	private class NfsAcqChargebackGlReportProcess extends StoredProcedure {
		private static final String procName = "NFS_ACQ_CHARGEBACK_GL";

		NfsAcqChargebackGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}

	@Override
	public void NfsAcqPrearbGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		NfsAcqPrearbGlReportProcess procedure = new NfsAcqPrearbGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
	}
	
	private class NfsAcqPrearbGlReportProcess extends StoredProcedure {
		private static final String procName = "NFS_ACQ_PREARB_GL";

		NfsAcqPrearbGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}
	
	@Override
	public void NfsAcqDebitGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		NfsAcqDebitGlReportProcess procedure = new NfsAcqDebitGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
		
	}
	
	private class NfsAcqDebitGlReportProcess extends StoredProcedure {
		private static final String procName = "NFS_ACQ_DR_ADJ_GL";

		NfsAcqDebitGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}
	
	@Override
	public void NfsAcqCreditGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		NfsAcqCreditGlReportProcess procedure = new NfsAcqCreditGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
		
	}
	
	private class NfsAcqCreditGlReportProcess extends StoredProcedure {
		private static final String procName = "NFS_ACQ_CRD_ADJ_GL";

		NfsAcqCreditGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}
	
	@Override
	public void NfsIssChargebackGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		System.out.println("inside NfsIssChargebackGlReportProcess");
		NfsIssChargebackGlReportProcess procedure = new NfsIssChargebackGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
	}
	
	private class NfsIssChargebackGlReportProcess extends StoredProcedure {
		private static final String procName = "NFS_ISS_CHARGEBACK_GL";

		NfsIssChargebackGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}


	@Override
	public void NfsISSCreditGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		System.out.println("inside NfsISSCreditGlReportProcess");
		NfsISSCreditGlReportProcess procedure = new NfsISSCreditGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
		
	}
	
	private class NfsISSCreditGlReportProcess extends StoredProcedure {
		private static final String procName = "NFS_ISS_CRD_ADJ_GL";

		NfsISSCreditGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}

	@Override
	public void NfsISSDebitGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		System.out.println("inside NfsISSDebitGlReportProcess");
		NfsISSDebitGlReportProcess procedure = new NfsISSDebitGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
	}

	private class NfsISSDebitGlReportProcess extends StoredProcedure {
		private static final String procName = "NFS_ISS_DR_ADJ_GL";

		NfsISSDebitGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}
	

	@Override
	public void NfsISSPrearbGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		System.out.println("inside NfsISSPrearbGlReportProcess");
		NfsISSPrearbGlReportProcess procedure = new NfsISSPrearbGlReportProcess(getJdbcTemplate());
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", fdate);
		inParam.put("v_EODBAL", openingBalance);
		inParam.put("v_FEODBAL", finalEodBalance);
		Map<String, Object> outParam = procedure.execute(inParam);
	}
	
	private class NfsISSPrearbGlReportProcess extends StoredProcedure {
		private static final String procName = "NFS_ISS_PREARB_GL";

		NfsISSPrearbGlReportProcess(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
		}
	}
	
	@Override
	public List<GlReportModel> downloadGlReport(String date,String tableName) {
		MisReportDaoImpl obj = new MisReportDaoImpl();
		date = obj.parseDate(date);
		String query = "select GL_DATE,PARTICULARS,DEBIT_AMT,CREDIT_AMT  from "+tableName+" where GL_DATE = '"
				+ date + "'";
		System.out.println("Summary query: "+query);
		List<GlReportModel> glReport = getJdbcTemplate().query(query, new RowMapper<GlReportModel>() {
			public GlReportModel mapRow(ResultSet rs, int row) throws SQLException {
				GlReportModel u = new GlReportModel();
				u.setGL_DATE(rs.getString(1));
				u.setPARTICULARS(rs.getString(2));
				u.setDEBIT_AMT(rs.getString(3));
				u.setCREDIT_AMT(rs.getString(4));
				return u;
			}
		});
		return glReport;
	}
	
	@Override
	public List<GlSettlementModel> downloadGlSettlement(String date,String accountNumber, String queryNumber) {
		String query="";
		System.out.println("queryNumber: "+queryNumber);
		switch(queryNumber) {
		  case "1":
//			  query = "SELECT FILEDATE,DEBIT,CREDIT,DESCRIPTION,TTUM_NARATION,ACCOUNT_NO FROM NFS_SETTLEMENT_REPORT WHERE ACCOUNT_NO='"+accountNumber+"' AND TO_CHAR(FILEDATE,'DD-MON-YYYY') =TO_CHAR(TO_DATE('"+date+"','DD-MM-YYYY')-1,'DD-MON-YYYY') AND DESCRIPTION LIKE '%PENALTY Charge%'";
			  query = "SELECT FILEDATE,DEBIT,CREDIT,DESCRIPTION,TTUM_NARATION,ACCOUNT_NO FROM NFS_SETTLEMENT_REPORT";
			  break;
		  case "2":
//			  query = "SELECT FILEDATE,DEBIT,CREDIT,DESCRIPTION,TTUM_NARATION,ACCOUNT_NO FROM NFS_SETTLEMENT_REPORT WHERE ACCOUNT_NO='"+accountNumber+"' AND TO_CHAR(FILEDATE,'DD-MON-YYYY') =TO_CHAR(TO_DATE('"+date+"','DD-MM-YYYY')-1,'DD-MON-YYYY') AND DESCRIPTION LIKE '%Re-Presentment Raise%'";
			  query = "SELECT FILEDATE,DEBIT,CREDIT,DESCRIPTION,TTUM_NARATION,ACCOUNT_NO FROM NFS_SETTLEMENT_REPORT";
			  break;
		  default:
		    // code block
		}
		
		System.out.println("Settlement query: "+query);
		List<GlSettlementModel> glSettlement = getJdbcTemplate().query(query, new RowMapper<GlSettlementModel>() {
			public GlSettlementModel mapRow(ResultSet rs, int row) throws SQLException {
				GlSettlementModel u = new GlSettlementModel();
				u.setFILEDATE(rs.getString(1));
				u.setDEBIT(rs.getString(2));
				u.setCREDIT(rs.getString(3));
				u.setDESCRIPTION(rs.getString(4));
				u.setTTUM_NARATION(rs.getString(5));
				u.setACCOUNT_NO(rs.getNString(6));
				return u;
			}
		});

		return glSettlement;

	}


	@Override
	public List<DownloadICCW> iccwdownload() {
		List<DownloadICCW> download1 = new ArrayList<DownloadICCW>();
		String query = "select * from ICCW_MASTER_SWITCH_NPCI";
		System.out.println("query: "+query);
		download1 = getJdbcTemplate().query(query, new RowMapper<DownloadICCW>() {

		@Override
		public DownloadICCW mapRow(ResultSet rs, int arg1) throws SQLException {

		DownloadICCW e = new DownloadICCW();
		e.setUPI_TRAN_TYP(rs.getString(1));
		e.setUPI_SWITCHTOCBSPSTDT(rs.getString(2));
		e.setUPI_SWITCHTOCBSPSTTM(rs.getString(3));
		e.setUPI_SENDER_ACQ_ID(rs.getString(4));
		e.setUPI_RECEIVER_ACQ_ID(rs.getString(5));
		e.setUPI_CBS_RRN(rs.getString(6));
		e.setUPI_UPI_RRN(rs.getString(7));
		e.setUPI_TXN_ID(rs.getString(8));
		e.setUPI_TXN_AMT(rs.getString(9));
		e.setUPI_TRAN_DATE(rs.getString(10));
		e.setUPI_TRAN_TIME(rs.getString(11));
		e.setUPI_CBS_RESP_CODE(rs.getString(12));
		e.setUPI_UPI_RESP_CODE(rs.getString(13));
		e.setUPI_ORIG_CURR_CODE(rs.getString(14));
		e.setUPI_UPI_MESSAGE_TYPE(rs.getString(15));
		e.setUPI_DEBIT_CREDIT_FLAG(rs.getString(16));
		e.setUPI_POST_ACCOUNT_NUM_1(rs.getString(17));
		e.setUPI_REMARKS(rs.getString(18));
		e.setUPI_CUST_REF_NUM(rs.getString(19));
		e.setUPI_POST_ACCOUNT_NUM_2(rs.getString(20));
		e.setIFSC_CD(rs.getString(21));
		e.setMCC_CD(rs.getString(22));
		e.setINITIATE_CD(rs.getString(23));
		e.setPURPOSE_CD(rs.getString(24));
		e.setFILEDATE(rs.getString(25));
		e.setCREATED_BY(rs.getString(26));
		e.setREMARK(rs.getString(27));
		return e;

		}
		});
		return download1;
	}


	@Override
	public List<DownloadICCW1> iccwdownload1() {
		List<DownloadICCW1> download1 = new ArrayList<DownloadICCW1>();
		String query = "select * from ICCW_MASTER_CBS_NPCI";
		System.out.println("query: "+query);
		download1 = getJdbcTemplate().query(query, new RowMapper<DownloadICCW1>() {

		@Override
		public DownloadICCW1 mapRow(ResultSet rs, int arg1) throws SQLException {

		DownloadICCW1 e = new DownloadICCW1();
		e.setSOL_ID(rs.getString(1));
		e.setAMOUNT(rs.getString(2));
		e.setDRCR(rs.getString(3));
		e.setDR_ACC_NUM(rs.getString(4));
		e.setTRACE_NUM(rs.getString(5));
		e.setCR_ACC_NUM(rs.getString(6));
		e.setTXN_DATE(rs.getString(7));
		e.setTXN_TIME(rs.getString(8));
		e.setTYPE_1(rs.getString(9));
		e.setTYPE_2(rs.getString(10));
		e.setRRN(rs.getString(11));
		e.setTXN_ID(rs.getString(12));
		e.setVALUE_DT(rs.getString(13));
		e.setCREATED_BY(rs.getString(14));
		e.setFILEDATE(rs.getString(15));
		e.setREMARK(rs.getString(16));
		e.setUPI_TXN_ID(rs.getString(17));
		return e;

		}
		});
		return download1;
	}


	@Override
	public List<DownloadICCW2> iccwdownload2() {
		List<DownloadICCW2> download2 = new ArrayList<DownloadICCW2>();
		String query = "select * from ICCW_MASTER_NPCI_SWITCH";
		System.out.println("query: "+query);
		download2 = getJdbcTemplate().query(query, new RowMapper<DownloadICCW2>() {

		@Override
		public DownloadICCW2 mapRow(ResultSet rs, int arg1) throws SQLException {

		DownloadICCW2 e = new DownloadICCW2();
		e.setPARTICIPANT_ID(rs.getString(1));
		e.setTRAN_TYPE(rs.getString(2));
		e.setUPI_TRAN_ID(rs.getString(3));
		e.setRRN(rs.getString(4));
		e.setRESP_CODE(rs.getString(5));
		e.setTRAN_DATE(rs.getString(6));
		e.setTRAN_TIME(rs.getString(7));
		e.setAMOUNT(rs.getString(8));
		e.setINITIATE_CODE(rs.getString(9));
		e.setMAPPER_ID(rs.getString(10));
		e.setBLANKX(rs.getString(11));
		e.setPURPOSE_CODE(rs.getString(12));
		e.setPAYER_PSP_CODE(rs.getString(13));
		e.setPAYER_MCC(rs.getString(14));
		e.setMERCHANT_CATEGORY_CODE(rs.getString(15));
		e.setPAYEE_VPA(rs.getString(16));
		e.setMCC_CODE(rs.getString(17));
		e.setUMN(rs.getString(18));
		e.setACQ_BANK(rs.getString(19));
		e.setACQ_IFSC(rs.getString(20));
		e.setPAYEE_PSP_CODE(rs.getString(21));
		e.setACQ_ACC_NUMBER(rs.getString(22));
		e.setISS_BANK(rs.getString(23));
		e.setISS_IFSC(rs.getString(24));
		e.setPAYER_VPA(rs.getString(25));
		e.setISS_ACC_NUMBER(rs.getString(26));
		e.setINITIATION_MODE(rs.getString(27));
		e.setINITIATION_MODE_1(rs.getString(28));
		e.setATTRIBUTE_9(rs.getString(29));
		e.setATTRIBUTE_10(rs.getString(30));
		e.setCREATED_BY(rs.getString(31));
		e.setFILEDATE(rs.getString(32));
		e.setCYCLE(rs.getString(33));
		e.setREMARK(rs.getString(34));
		return e;
		}
		});
		return download2;
	}


	






	





	










	





	
}
