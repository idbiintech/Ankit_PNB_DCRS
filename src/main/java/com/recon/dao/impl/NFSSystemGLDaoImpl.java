package com.recon.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;

import com.recon.dao.NFSSystemGLDao;
import com.recon.model.NFSGLSummaryPayable;
import com.recon.model.NFSGLSummaryReceivable;
import com.recon.model.NFSPayableGLModel;
import com.recon.model.NFSReceivableGLModel;

import oracle.jdbc.OracleTypes;

@Component
public class NFSSystemGLDaoImpl implements NFSSystemGLDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void setNFSDataSource(DataSource reconDataSource) {
		this.jdbcTemplate = new JdbcTemplate(reconDataSource);
		// this.jdbcTemplate.setQueryTimeout(180);
	}

	@Override
	public void processNfsReportPayable(String date, String openingBal, String fundMovefr43Acc, String onlineFundMov,
			String finacleEODbal) {
		System.out.println("*** NFS PAYABLE PROCEDURE CALLING END START ***");

		NfsPayableGLProc glSummary = new NfsPayableGLProc(jdbcTemplate);

		//System.out.println("in the proccess report GL DAO");

		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", date);
		inParam.put("v_EODBAL", openingBal);
		inParam.put("v_FEODBAL", fundMovefr43Acc);
		inParam.put("v_REODBAL", onlineFundMov);
		inParam.put("v_OEODBAL", finacleEODbal);

		Map<String, Object> outParam = glSummary.execute(inParam);
		System.out.println("*** NFS PAYABLE PROCEDURE CALLING END ***");
	}

	private class NfsPayableGLProc extends StoredProcedure {
		private static final String procName = "NFS_PAYABLE_GL";

		NfsPayableGLProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_REODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_OEODBAL", OracleTypes.VARCHAR));
		}
	}

	@Override
	public List<NFSGLSummaryPayable> downloadNfsReportPayable(String date) throws Exception {
		date = date.replace('/', '-');
		String query = "select gl_date, particulars, debit_amt, credit_amt from GL_PAYABLE_NFS where gl_date = to_date('"
				+ date + "','dd-mm-yyyy')";
		System.out.println("NFS PAYABLE GL QUERY: " + query);

		List<NFSGLSummaryPayable> nfsglSummariesPay = jdbcTemplate.query(query, new RowMapper<NFSGLSummaryPayable>() {

			public NFSGLSummaryPayable mapRow(ResultSet rs, int row) throws SQLException {

				NFSGLSummaryPayable u = new NFSGLSummaryPayable();

				u.setGlDate(rs.getString(1));
				u.setParticulars(rs.getString(2));
				u.setDebitAmount(rs.getString(3));
				u.setCreditAmount(rs.getString(4));

				return u;
			}
		});

		System.out.println("total records: " + nfsglSummariesPay.size());
		return nfsglSummariesPay;
	}

	@Override
	public List<NFSPayableGLModel> withdrawalStlmntAmtPayable(String date) {

		String query = "select CYCLE,FILEDATE,CREATEDBY,NO_OF_TXNS,DEBIT,CREDIT,DESCRIPTION from NTSL_NFS_RAWDATA "
				+ "where TO_DATE(filedate,'DD-MM-YY')=TO_DATE('" + date + "','DD-MM-YYYY')-1"
				+ "and description in  ('Issuer WDL Transaction Amount','Issuer WDL Transaction Amount (Micro-ATM)')";

		System.out.println("PAYABLE WITHDRAWAL SETTLEMENT AMOUNT QUERY : " + query);
		List<NFSPayableGLModel> withdrawablestlAmtPayable = jdbcTemplate.query(query,
				new RowMapper<NFSPayableGLModel>() {

					public NFSPayableGLModel mapRow(ResultSet rs, int row) throws SQLException {

						NFSPayableGLModel u = new NFSPayableGLModel();

						u.setCYCLE(rs.getString(1));
						u.setFiledate(rs.getString(2));
						u.setCREATEDBY(rs.getString(3));
						u.setNO_OF_TXNS(rs.getString(4));
						u.setDEBIT(rs.getString(5));
						u.setCREDIT(rs.getString(6));
						u.setDESCRIPTION(rs.getString(7));
						return u;
					}
				});
		return withdrawablestlAmtPayable;
	}

	@Override
	public List<NFSPayableGLModel> lateReversalStlmntAmtPayable(String date) {
		String query = "select transtype,resp_code,cardno,RRN,stanno,ACQ,ISS,trasn_date,trans_time,atmid,settledate,"
				+ "requestamt,receivedamt,STATUS,dcrs_remarks,filedate,cycle,FPAN "
				+ "from nfs_rev_acq_report where iss='UCO' AND TO_DATE(filedate,'DD-MM-YY')=TO_DATE('" + date
				+ "','DD-MM-YYYY')-1";

		System.out.println("PAYABLE LATE REVERSAL SETTLEMENT AMOUNT QUERY : " + query);

		List<NFSPayableGLModel> lateReversalStAmytPostPay = jdbcTemplate.query(query,new RowMapper<NFSPayableGLModel>() {

					public NFSPayableGLModel mapRow(ResultSet rs, int row) throws SQLException {

						NFSPayableGLModel u = new NFSPayableGLModel();

						u.setTranstype(rs.getString(1));
						u.setResp_code(rs.getString(2));
						u.setCardno(rs.getString(3));
						u.setRRN(rs.getString(4));
						u.setStanno(rs.getString(5));
						u.setACQ(rs.getString(6));
						u.setISS(rs.getString(7));
						u.setTRAN_DATE(rs.getString(8));
						u.setTrans_time(rs.getString(9));
						u.setAtmid(rs.getString(10));
						u.setSettledate(rs.getString(11));
						u.setRequestamt(rs.getString(12));
						u.setReceivedamt(rs.getString(13));
						u.setSTATUS(rs.getString(14));
						u.setDcrs_remarks(rs.getString(15));
						u.setFiledate(rs.getString(16));
						u.setCYCLE(rs.getString(17));
						u.setFPAN(rs.getString(18));
						return u;
					}
				});
		return lateReversalStAmytPostPay;
	}

	@Override
	public List<NFSPayableGLModel> pbgbStlmntAmtPostedPayable(String date) {

		String query = "select sr_no,filedate,no_of_txns,DEBIT,CREDIT,DESCRIPTION,ttum_naration,account_no "
				+ "from PBGB_SETTLEMENT_REPORT where TO_DATE(filedate,'DD-MM-YY')=TO_DATE('" + date
				+ "','DD-MM-YYYY')-1 " + "and account_no = '17351015020023' ";

		System.out.println("PAYABLE PBGB SETTLEMENT AMOUNT POSTED QUERY : " + query);
		List<NFSPayableGLModel> withdrawablestlAmtPayable = jdbcTemplate.query(query,
				new RowMapper<NFSPayableGLModel>() {

					public NFSPayableGLModel mapRow(ResultSet rs, int row) throws SQLException {

						NFSPayableGLModel u = new NFSPayableGLModel();

						u.setSr_no(rs.getString(1));
						u.setFiledate(rs.getString(2));
						u.setNO_OF_TXNS(rs.getString(3));
						u.setDEBIT(rs.getString(4));
						u.setCREDIT(rs.getString(5));
						u.setDESCRIPTION(rs.getString(6));
						u.setTtum_naration(rs.getString(7));
						u.setACCOUNT_NUMBER(rs.getString(7));
						return u;
					}
				});
		return withdrawablestlAmtPayable;
	}

	@Override
	public List<NFSPayableGLModel> unreconciledTranPostPayable(String date) {

		String query = "select ACCOUNT_NUMBER,CURRENCY_CODE,PART_TRAN_TYPE,TRANSACTION_AMOUNT,REFERENCE_AMOUNT,TRAN_DATE,TRANSACTION_PARTICULAR "
				+ "FROM(select * from ttum_nfs_iss_nfs where TO_DATE(filedate,'DD-MM-YY')=TO_DATE('" + date
				+ "','DD-MM-YYYY')-1  and PART_TRAN_TYPE='D' " + "union "
				+ "select * from ttum_nfs_iss_cbs where TO_DATE(filedate,'DD-MM-YY')=TO_DATE('" + date
				+ "','DD-MM-YYYY')-1  and PART_TRAN_TYPE='D' " + "union "
				+ "select * from ttum_nfs_iss_switch  where TO_DATE(filedate,'DD-MM-YY')=TO_DATE('" + date
				+ "','DD-MM-YYYY')-1  and PART_TRAN_TYPE='D')";

		System.out.println("PAYABLE UNRECONCILED TRANSACTIONS POSTED QUERY : " + query);
		List<NFSPayableGLModel> unreconiledtranPostPay = jdbcTemplate.query(query, new RowMapper<NFSPayableGLModel>() {

			public NFSPayableGLModel mapRow(ResultSet rs, int row) throws SQLException {

				NFSPayableGLModel u = new NFSPayableGLModel();

				u.setACCOUNT_NUMBER(rs.getString(1));
				u.setCURRENCY_CODE(rs.getString(2));
				u.setPART_TRAN_TYPE(rs.getString(3));
				u.setTRANSACTION_AMOUNT(rs.getString(4));
				u.setREFERENCE_AMOUNT(rs.getString(5));
				u.setTRAN_DATE(rs.getString(6));
				u.setTRANSACTION_PARTICULAR(rs.getString(7));
				return u;
			}
		});
		return unreconiledtranPostPay;
	}

	@Override
	public void processNfsReportReceivable(String date, String openingBal, String onlineFundMov, String finacleEODbal) {

		System.out.println("*** NFS RECEIVABLE PROCEDURE CALLING END START ***");

		NfsReceivableGLProc glSummary = new NfsReceivableGLProc(jdbcTemplate);
		 
		Map<String, String> inParam = new HashMap<String, String>();
		inParam.put("v_GLDATE", date);
		inParam.put("v_EODBAL", openingBal);
		inParam.put("v_FEODBAL", onlineFundMov);
		inParam.put("v_MEODBAL", finacleEODbal);

		Map<String, Object> outParam = glSummary.execute(inParam);
		System.out.println("*** NFS RECEIVABLE PROCEDURE CALLING END ***");
	}

	private class NfsReceivableGLProc extends StoredProcedure {
		private static final String procName = "NFS_RECEIVABLE_GL";

		NfsReceivableGLProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, procName);
			declareParameter(new SqlParameter("v_GLDATE", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_EODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_FEODBAL", OracleTypes.VARCHAR));
			declareParameter(new SqlParameter("v_MEODBAL", OracleTypes.VARCHAR));
		}
	}

	//****************************************DOWNLOAD NFS REPORT GL RECEIVABLE***************************
	@Override
	public List<NFSGLSummaryReceivable> downloadNfsReportReceivable(String date) throws Exception {
		date = date.replace('/', '-');
		String query = "select gl_date, particulars, debit_amt, credit_amt from GL_RECEIVABLE_NFS where gl_date = to_date('"+ date + "','dd-mm-yyyy')";
		System.out.println("NFS RECEIVABLE GL QUERY: " + query);

		List<NFSGLSummaryReceivable> nfsglSummariesReceiv = jdbcTemplate.query(query, new RowMapper<NFSGLSummaryReceivable>() {

			public NFSGLSummaryReceivable mapRow(ResultSet rs, int row) throws SQLException {

				NFSGLSummaryReceivable u = new NFSGLSummaryReceivable();

				u.setGlDate(rs.getString(1));
				u.setParticulars(rs.getString(2));
				u.setDebitAmount(rs.getString(3));
				u.setCreditAmount(rs.getString(4));

				return u;
			}
		});

		System.out.println("total records: " + nfsglSummariesReceiv.size());
		return nfsglSummariesReceiv;
	}
    //****************************************WITHDRAWAL SETTLEMENT AMOUNT RECEIVABLE***************************
	@Override
	public List<NFSReceivableGLModel> withdrawalStlmntAmtReceivable(String date) {
		String query = "SELECT CYCLE,FILEDATE,CREATEDBY,NO_OF_TXNS,DEBIT,CREDIT,DESCRIPTION  FROM NTSL_NFS_RAWDATA "
				+ "WHERE TO_DATE(filedate,'DD-MM-YY')=TO_DATE('"+date+"','DD-MM-YYYY')-1 "
				+ "and description in  ('Acquirer WDL Transaction Amount','Acquirer WDL Transaction Amount (Micro-ATM)','Acquirer WDL Transaction Amount (CC)')";

		System.out.println("RECEIVABLE WITHDRAWAL SETTLEMENT AMOUNT QUERY : " + query);
		List<NFSReceivableGLModel> withdrawablestlAmtReceiv = jdbcTemplate.query(query,
				new RowMapper<NFSReceivableGLModel>() {

					public NFSReceivableGLModel mapRow(ResultSet rs, int row) throws SQLException {

						NFSReceivableGLModel u = new NFSReceivableGLModel();

						u.setCYCLE(rs.getString(1));
						u.setFiledate(rs.getString(2));
						u.setCREATEDBY(rs.getString(3));
						u.setNO_OF_TXNS(rs.getString(4));
						u.setDEBIT(rs.getString(5));
						u.setCREDIT(rs.getString(6));
						u.setDESCRIPTION(rs.getString(7));
						return u;
					}
				});
		return withdrawablestlAmtReceiv;
	}
	//****************************************LATE REVERSAL SETTLEMENT AMOUNT RECEIVABLE***************************
	@Override
	public List<NFSReceivableGLModel> lateReversalStlmntAmtReceivable(String date) {
			String query = "select transtype,resp_code,cardno,RRN,stanno,ACQ,ISS,trasn_date,trans_time,atmid,settledate,requestamt,receivedamt,STATUS,dcrs_remarks,filedate,cycle,FPAN "
					+ "from nfs_rev_acq_report where acq='UCO' AND TO_DATE(filedate,'DD-MM-YY')=TO_DATE('"+date+"','DD-MM-YYYY')-1";

			System.out.println("RECEIVABLE LATE REVERSAL SETTLEMENT AMOUNT QUERY : " + query);

			List<NFSReceivableGLModel> lateReversalStAmytPostReceiv = jdbcTemplate.query(query,
					new RowMapper<NFSReceivableGLModel>() {

						public NFSReceivableGLModel mapRow(ResultSet rs, int row) throws SQLException {

							NFSReceivableGLModel u = new NFSReceivableGLModel();

							u.setTranstype(rs.getString(1));
							u.setResp_code(rs.getString(2));
							u.setCardno(rs.getString(3));
							u.setRRN(rs.getString(4));
							u.setStanno(rs.getString(5));
							u.setACQ(rs.getString(6));
							u.setISS(rs.getString(7));
							u.setTRAN_DATE(rs.getString(8));
							u.setTrans_time(rs.getString(9));
							u.setAtmid(rs.getString(10));
							u.setSettledate(rs.getString(11));
							u.setRequestamt(rs.getString(12));
							u.setReceivedamt(rs.getString(13));
							u.setSTATUS(rs.getString(14));
							u.setDcrs_remarks(rs.getString(15));
							u.setFiledate(rs.getString(16));
							u.setCYCLE(rs.getString(17));
							u.setFPAN(rs.getString(18));
							return u;
						}
					});
			return lateReversalStAmytPostReceiv;
	}
	//****************************************JCB WITHDRAWAL SETTLEMENT AMOUNT RECEIVABLE***************************
	@Override
	public List<NFSReceivableGLModel> jcbWithdrawalStlmntAmtReceivable(String date) {
		String query = "SELECT CYCLE,SR_NO,FILEDATE,no_of_txns,DEBIT,CREDIT,DESCRIPTION,ACCOUNT_NO,ttum_naration FROM JCB_SETTLEMENT_REPORT "
				+ "WHERE TO_DATE(filedate,'DD-MM-YY')=TO_DATE('"+date+"','DD-MM-YYYY')-1"
				+ "and description in  ('Acquirer WDL UPI Transaction Amount','Acquirer WDL UPI Transaction Amount (Micro-ATM)',"
				+ " 'Acquirer WDL UPI Transaction Amount (CC)')";

		System.out.println("RECEIVABLE JCB WITHDRAWAL SETTLEMENT AMOUNT RECEIVABLE QUERY : " + query);
		List<NFSReceivableGLModel> jcbWithdrawalAmtReceiv = jdbcTemplate.query(query,
				new RowMapper<NFSReceivableGLModel>() {

					public NFSReceivableGLModel mapRow(ResultSet rs, int row) throws SQLException {

						NFSReceivableGLModel u = new NFSReceivableGLModel();

						u.setCYCLE(rs.getString(1));
						u.setSr_no(rs.getString(2));
						u.setFiledate(rs.getString(3));
						u.setNO_OF_TXNS(rs.getString(4));
						u.setDEBIT(rs.getString(5));
						u.setCREDIT(rs.getString(6));
						u.setDESCRIPTION(rs.getString(7));
						u.setACCOUNT_NUMBER(rs.getString(8));
						u.setTtum_naration(rs.getString(9));
						return u;
					}
				});
		return jcbWithdrawalAmtReceiv;
	}
    //=====================================DFS WITHDRAWAL SETTLEMENT AMOUNT RECEIVABLE=========================
	@Override
	public List<NFSReceivableGLModel> dfsWithdrawalStlmntAmtReceivable(String date) {
		String query = "SELECT CYCLE,SR_NO,FILEDATE,no_of_txns,DEBIT,CREDIT,DESCRIPTION,ACCOUNT_NO,ttum_naration  FROM DFS_settlement_report "
				+ "WHERE TO_DATE(filedate,'DD-MM-YY')=TO_DATE('"+date+"','DD-MM-YYYY')-1 "
				+ "and description in  ('Acquirer WDL Transaction Amount','Acquirer WDL Transaction Amount (Micro-ATM)','Acquirer WDL Transaction Amount (CC)')";

		System.out.println("RECEIVABLE DFS WITHDRAWAL SETTLEMENT AMOUNT RECEIVABLE QUERY : " + query);
		List<NFSReceivableGLModel> dfsWithdrawalAmtReceiv = jdbcTemplate.query(query,
				new RowMapper<NFSReceivableGLModel>() {

					public NFSReceivableGLModel mapRow(ResultSet rs, int row) throws SQLException {

						NFSReceivableGLModel u = new NFSReceivableGLModel();

						u.setCYCLE(rs.getString(1));
						u.setSr_no(rs.getString(2));
						u.setFiledate(rs.getString(3));
						u.setNO_OF_TXNS(rs.getString(4));
						u.setDEBIT(rs.getString(5));
						u.setCREDIT(rs.getString(6));
						u.setDESCRIPTION(rs.getString(7));
						u.setACCOUNT_NUMBER(rs.getString(8));
						u.setTtum_naration(rs.getString(9));
						return u;
					}
				});
		return dfsWithdrawalAmtReceiv;
		
		
	}
    //=============================NOT IN HOST TTUM RECEIVABLE===============================================================
	@Override
	public List<NFSReceivableGLModel> notInHostTtumReceivable(String date) {
		String query = "select MSGTYPE,PAN,TERMID,LOCAL_DATE,LOCAL_TIME,TRACE,AMOUNT,ACCEPTORNAME,RESPCODE,TERMLOC,AMOUNT_EQUIV,ISSUER,FILEDATE,DCRS_REMARKS,FPAN "
				+ "from switch_rawdata T1 where issuer in (select reference_number from  ttum_nfs_acq_nfs where tran_date = to_date('"+date+"','dd/mm/yyyy')-3 "
				+ "and part_tran_type = 'C') and dcrs_remarks = 'ATM' AND AMOUNT > 0 AND MSGTYPE ='0210' AND SUBSTR(PAN,1,6) NOT IN (SELECT BIN FROM UCO_BIN_MASTER WHERE BANK = 'UCO')";

		System.out.println("RECEIVABLE NOT IN HOST TTUM QUERY : " + query);

		List<NFSReceivableGLModel> notInHostTtumReceiv = jdbcTemplate.query(query,
				new RowMapper<NFSReceivableGLModel>() {

					public NFSReceivableGLModel mapRow(ResultSet rs, int row) throws SQLException {

						NFSReceivableGLModel u = new NFSReceivableGLModel();

						u.setMSGTYPE(rs.getString(1));
						u.setPAN(rs.getString(2));
						u.setTERMID(rs.getString(3));
						u.setLOCAL_DATE(rs.getString(4));
						u.setLOCAL_TIME(rs.getString(5));
						u.setTRACE(rs.getString(6));
						u.setAMOUNT(rs.getString(7));
						u.setACCEPTORNAME(rs.getString(8));
						u.setResp_code(rs.getString(9));
						u.setTERMLOC(rs.getString(10));
						u.setAMOUNT_EQUIV(rs.getString(11));
						u.setISSUER(rs.getString(12));
						u.setFiledate(rs.getString(13));
						u.setDcrs_remarks(rs.getString(14));
						u.setFPAN(rs.getString(15));
						return u;
					}
				});
		return notInHostTtumReceiv;
		
		
	}

}
