package com.recon.dao.impl;

import static com.recon.util.GeneralUtil.CHECK_IP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;

import com.recon.dao.LoginDao;
import com.recon.model.LoginBean;
import com.recon.model.ProcessDtlBean;
import com.recon.util.demo;

import oracle.jdbc.OracleTypes;

@Component
@SuppressWarnings({ "unchecked" })
public class LoginDaoImpl extends JdbcDaoSupport implements LoginDao {

	/**
	 * Input Field Constants.
	 */
	private static final String I_USER_ID = "i_user_id";
	private static final String I_IP_ADDRESS = "i_ip_address";
	private static final String I_SESSION_ID = "i_session_id";
	ResultSet rs;
	PreparedStatement ps;
	Connection con;

	/**
	 * Output Field Constants
	 */
	private static final String O_LOGIN_CUR = "o_login_cur";
	private static final String O_LOGIN_COUNT = "o_login_count";
	private static final String O_ERROR_CODE = "o_error_code";
	private static final String O_ERROR_MESSAGE = "o_error_message";

	private static final String O_USER_CUR = "o_user_cur";
	private static final String O_USER_COUNT = "o_user_count";

	StringBuilder error_string = new StringBuilder();

	@Override
	public LoginBean getUserDetail(LoginBean loginBean) throws Exception {
		try {
			logger.info("***** LoginDaoImpl.getUserDetail Start ****");

			/*
			 * Map<String, Object> inParams = new HashMap<String, Object>();
			 * inParams.put(I_USER_ID, loginBean.getUser_id()); //
			 * inParams.put(I_IP_ADDRESS, InetAddress.getLocalHost().getHostAddress());
			 * inParams.put(I_IP_ADDRESS, loginBean.getIp_address());
			 * inParams.put(I_SESSION_ID, loginBean.getSession_id()); String getPASSWORD =
			 * ""; LoginDetailProc loginDetailProc = new LoginDetailProc(getJdbcTemplate());
			 * Map<String, Object> outParams = loginDetailProc.execute(inParams);
			 * 
			 * logger.info("outParams.get(O_ERROR_CODE)==" + outParams.get(O_ERROR_CODE));
			 * logger.info("outParams.get(O_ERROR_MESSAGE)==" +
			 * outParams.get(O_ERROR_MESSAGE));
			 * 
			 * if (outParams.get(O_ERROR_CODE) != null &&
			 * Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0) { throw
			 * new Exception("Invalid User Contact To Administrator"); }
			 * 
			 * logger.info("outParams.get(O_LOGIN_CUR)==" + outParams.get(O_LOGIN_CUR));
			 * logger.info("outParams.get(O_LOGIN_COUNT)==" + outParams.get(O_LOGIN_COUNT));
			 */
			String gerUser = "select count(*) from login_master where UPPER(user_id) = UPPER('" + loginBean.getUser_id()
					+ "')";

			int gerUsers = getJdbcTemplate().queryForObject(gerUser, new Object[] {}, Integer.class);

			if (gerUsers == 0) {

				// throw new Exception("Invalid User Name and/or Password.");

				throw new Exception("USER NOT ADDED!!");
			}
			String getUserType = getJdbcTemplate()
					.queryForObject("SELECT USER_TYPE FROM login_master WHERE UPPER(user_id) = UPPER('"
							+ loginBean.getUser_id() + "') ", new Object[] {}, String.class);
			String getUserName = getJdbcTemplate()
					.queryForObject("SELECT USER_NAME FROM login_master WHERE UPPER(user_id) = UPPER('"
							+ loginBean.getUser_id() + "') ", new Object[] {}, String.class);

			System.out.println("usertype "+ getUserType + getUserName);
			/*
			 * getPASSWORD = getJdbcTemplate().queryForObject(
			 * "SELECT PASSWORD FROM login_master WHERE UPPER(user_id) = UPPER('" +
			 * loginBean.getUser_id() + "')", new Object[] {}, String.class); if
			 * (!BCrypt.checkpw(loginBean.getPassword(), getPASSWORD)) {
			 * 
			 * throw new Exception("INCORRECT PASSWORD!!"); }
			 */
	
			/*
			 * int gerUsers2 = getJdbcTemplate().
			 * queryForObject("select count(*) from login_master where UPPER(user_id) = UPPER('"
			 * +loginBean.getUser_id()+"') AND PASSWORD ='"+loginBean.getPassword()+"'", new
			 * Object[] {},Integer.class); System.out.println("gerUsers2 "+ gerUsers2);
			 */

			/*
			 * String getuserlogin = getJdbcTemplate().queryForObject(
			 * "SELECT count(*) FROM login_master WHERE UPPER(user_id) = UPPER('" +
			 * loginBean.getUser_id() + "')", new Object[] {}, String.class);
			 * 
			 * if (!BCrypt.checkpw(loginBean.getPassword(), getPASSWORD)) { throw new
			 * Exception("INCORRECT PASSWORD!!"); }
			 */
	
			/*
			 * if (Integer.parseInt(String.valueOf(outParams.get(O_LOGIN_COUNT))) > 0) {
			 * error_string.setLength(0);
			 * error_string.append("User already logged in.<br/>"); error_string.
			 * append("Click <a id='endSession' style='color:teal;border-bottom: 1px dotted orange; cursor: pointer;text-decoration:none; margin-top:150px;'>here</a> to begin a new session."
			 * ); throw new Exception(error_string.toString()); }
			 */

	
			logger.info("***** LoginDaoImpl.getUserDetail End ****" + getUserType);
			loginBean.setUser_type(getUserType);

			loginBean.setUser_name(getUserName);
			loginBean.setUser_id( loginBean.getUser_id());
		} catch (Exception e) {
			// demo.logSQLException(e, "LoginDaoImpl.getUserDetail");
			//logger.error(" error in LoginDaoImpl.getUserDetail", new
			// Exception("LoginDaoImpl.getUserDetail",e));
			System.out.println("e "+e.toString());
			throw new Exception("Invalid Username And Password!!");
		
		}
		System.out.println("user type " + loginBean.getUser_type());
		return loginBean;
	}

	private class LoginDetailProc extends StoredProcedure {
		private static final String view_login_proc = "VIEW_USER_LOGIN_DETAIL";

		public LoginDetailProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, view_login_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, OracleTypes.VARCHAR));
			declareParameter(new SqlParameter(I_IP_ADDRESS, OracleTypes.VARCHAR));
			declareParameter(new SqlParameter(I_SESSION_ID, OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter(O_LOGIN_CUR, OracleTypes.CURSOR, new LoginDetailMapper()));
			declareParameter(new SqlOutParameter(O_LOGIN_COUNT, OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, OracleTypes.VARCHAR));
			compile();
		}

		private class LoginDetailMapper implements RowMapper<LoginBean> {

			@Override
			public LoginBean mapRow(ResultSet rs, int rowNum) throws SQLException {
				LoginBean loginBean = new LoginBean();

				loginBean.setUser_id(rs.getString("user_id"));
				loginBean.setUser_name(rs.getString("user_name"));
				// loginBean.setUser_type(rs.getString("user_type"));
				loginBean.setUser_status(rs.getString("user_status"));
				loginBean.setLast_login(rs.getString("last_login"));
				loginBean.setEntry_dt(rs.getString("entry_dt"));
				loginBean.setEntry_by(rs.getString("entry_by"));
				loginBean.setUpdt_dt(rs.getString("updt_dt"));
				loginBean.setUpdt_by(rs.getString("updt_by"));

				return loginBean;
			}
		}
	}

	@Override
	public void invalidateUser(LoginBean loginBean) throws Exception {
		try {
			//logger.info("***** LoginDaoImpl.validateUser End ****"+ loginBean.getUser_id());
			Map<String, Object> inParams = new HashMap<String, Object>();
		//	System.out.println(" loginBean.getUser_id()"+  loginBean.getUser_id() );
			inParams.put(I_USER_ID, loginBean.getUser_id());

			InvalidateUserProc invalidateUserProc = new InvalidateUserProc(getJdbcTemplate());
			Map<String, Object> outParams = invalidateUserProc.execute(inParams);
			System.out.println("outParams " + outParams);
			if (outParams.get(O_ERROR_CODE) != null
					&& Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0) {
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}

			logger.info("***** LoginDaoImpl.validateUser End ****");

		} catch (Exception e) {
			demo.logSQLException(e, "LoginDaoImpl.validateUser");
			logger.error(" error in LoginDaoImpl.validateUser", new Exception("LoginDaoImpl.validateUser", e));
			throw e;
		}

	}

	private class InvalidateUserProc extends StoredProcedure {
		private static final String invalidate_user_proc = "LOG_OUT";

		public InvalidateUserProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, invalidate_user_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, OracleTypes.VARCHAR));
			compile();
		}
	}

	@Override
	public boolean checkIp(LoginBean loginBean) throws Exception {
		try {
			// Map<String, Object> inParams = new HashMap<String, Object>();
			// inParams.put(I_EMPLY_CD, provisionalInterestBean.getEmply_cd());

			int i = getJdbcTemplate().queryForObject(CHECK_IP, new Object[] { loginBean.getIp_address() },
					Integer.class);

			if (i == 1) {

				return true;
			} else
				return false;

		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	public Map<String, Object> getAllSession(LoginBean loginBean) throws Exception {
		try {
			Map<String, Object> session_map = new HashMap<>();
			List<LoginBean> user_details = new ArrayList<>();
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_IP_ADDRESS, loginBean.getIp_address());

			ViewAllSessions viewAllSessions = new ViewAllSessions(getJdbcTemplate());

			Map<String, Object> outParams = viewAllSessions.execute(inParams);

			if (outParams.get(O_ERROR_MESSAGE) != null
					&& Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0) {
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}

			user_details = viewAllSessions.getUser_list();
			session_map.put("USER_LIST", user_details);

			return session_map;
		} catch (Exception e) {
			throw e;
		}
	}

	private class ViewAllSessions extends StoredProcedure {
		private static final String view_all_sessions_proc = "VIEW_ALL_SESSIONS";

		List<LoginBean> user_list = new ArrayList<LoginBean>();

		/**
		 * @return the trans_list
		 */
		public List<LoginBean> getUser_list() {
			return user_list;
		}

		public ViewAllSessions(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, view_all_sessions_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_IP_ADDRESS, OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter(O_USER_CUR, OracleTypes.CURSOR, new SessionDetails()));
			// declareParameter(new SqlOutParameter(O_USER_COUNT, OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, OracleTypes.VARCHAR));
			compile();
		}

		private class SessionDetails implements RowCallbackHandler {

			@Override
			public void processRow(ResultSet rs) throws SQLException {

				LoginBean loginBean = new LoginBean();
				loginBean.setUser_id(rs.getString("user_id"));
				loginBean.setIn_time(rs.getString("in_time"));
				loginBean.setIp_address(rs.getString("ip_address"));
				/*
				 * ContributionBean.setEmply_cd(rs.getInt(ContributionValidationBean.EMPLY_CD));
				 * ContributionBean.setOffc_cd(rs.getInt(ContributionValidationBean.OFFC_CD));
				 * ContributionBean.setEffctv_dt(rs.getString(ContributionValidationBean.
				 * EFFCTV_DT));
				 * ContributionBean.setRecords(rs.getInt(ContributionValidationBean.RECORDS));
				 * ContributionBean.setEmp_amt(rs.getBigDecimal(ContributionValidationBean.
				 * EMP_AMT));
				 * ContributionBean.setBnk_amt(rs.getBigDecimal(ContributionValidationBean.
				 * BNK_AMT));
				 * ContributionBean.setAdd_amt(rs.getBigDecimal(ContributionValidationBean.
				 * ADD_AMT));
				 * ContributionBean.setRec_amt(rs.getBigDecimal(ContributionValidationBean.
				 * REC_AMT));
				 * ContributionBean.setLoan_amt(rs.getBigDecimal(ContributionValidationBean.
				 * LOAN_AMT));
				 */

				user_list.add(loginBean);

			}

		}
	}

	@Override
	public void closeSession(LoginBean loginBean) throws Exception {
		try {
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_USER_ID, loginBean.getUser_id());

			CloseSessionProc closeSessionProc = new CloseSessionProc(getJdbcTemplate());
			Map<String, Object> outParams = closeSessionProc.execute(inParams);
			if (outParams.get(O_ERROR_CODE) != null
					&& Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0) {
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}
		} catch (Exception e) {
			throw e;
		}

	}

	private class CloseSessionProc extends StoredProcedure {
		private static final String invalidate_user_proc = "CLOSE_SESSION";

		public CloseSessionProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, invalidate_user_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, OracleTypes.VARCHAR));
			compile();
		}
	}
	public String getUSerDetails(String userid) {
		
		
		String getUserType = getJdbcTemplate()
				.queryForObject("SELECT USER_TYPE FROM login_master WHERE UPPER(user_id) = UPPER('"
						+ userid+ "') ", new Object[] {}, String.class);
		
		return getUserType;
		
	}
	@SuppressWarnings("null")
	@Override
	public List<ProcessDtlBean> getProcessdtls(String flag) {

		try {
			logger.info("getProcessdtls "+ flag);
			List<ProcessDtlBean> processDtlBeans = new ArrayList<>();

			 String query = "";
			 if(flag.equalsIgnoreCase("UPLOAD_FLAG")) {
				 query="SELECT CONCAT(mfud.CATEGORY, ' ', mfud.FILE_SUBCATEGORY) AS NETWORK,\n"
				 		+ "    IFNULL(MAX(CASE WHEN mfs.FILENAME LIKE '%SWITCH%' THEN mfud.FILEDATE END),MAX(mfud.FILEDATE)) AS SWITCH,\n"
				 		+ "    IFNULL(MAX(CASE WHEN mfs.FILENAME LIKE '%CBS%' THEN mfud.FILEDATE END),MAX(mfud.FILEDATE)) AS CBS,\n"
				 		+ "    MAX(mfud.FILEDATE) AS NETWORK_FILE\n"
				 		+ "FROM main_file_upload_dtls mfud\n"
				 		+ "JOIN main_filesource mfs\n"
				 		+ "    ON mfud.FILEID = mfs.FILEID\n"
				 		+ "GROUP BY NETWORK";
			 }else {
				 query="SELECT \n"
				 		+ "    CONCAT(mfud.CATEGORY, ' ', mfud.FILE_SUBCATEGORY) AS NETWORK,\n"
				 		+ "    IFNULL(MAX(CASE WHEN mfs.FILENAME LIKE '%SWITCH%' THEN mfud.FILEDATE END),MAX(mfud.FILEDATE)) AS SWITCH,\n"
				 		+ "    IFNULL(MAX(CASE WHEN mfs.FILENAME LIKE '%CBS%' THEN mfud.FILEDATE END),MAX(mfud.FILEDATE)) AS CBS,\n"
				 		+ "    MAX(mfud.FILEDATE) AS NETWORK_FILE\n"
				 		+ "FROM main_file_upload_dtls mfud\n"
				 		+ "JOIN main_filesource mfs \n"
				 		+ "    ON mfud.FILEID = mfs.FILEID\n"
				 		+ "    AND mfud.COMAPRE_FLAG='Y'\n"
				 		+ "GROUP BY NETWORK";
			 }
      
			 this.logger.info("query  " + query);
      
      
	con = getConnection();
			// System.out.println("query"+query);
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			String category = "";
			String subcat = "";
			ProcessDtlBean data = null;
			while (rs.next()) {

				// System.out.println(rs.getString(1)+rs.getString(2)+rs.getString(3)+rs.getString(4)+rs.getString(5));
				// subcat = rs.getString(2)=="-"?null:rs.getString(2);

				if (category.contains(rs.getString(1))) {

					data.setCategory(rs.getString(1));

					if (!(rs.getString(2).equals("NA"))) {
						data.setCbs_date(rs.getString(2));

					}
					if (!(rs.getString(3).equals("NA"))) {

						data.setSwitch_date(rs.getString(3));

					}
					if (!(rs.getString(4).equals("NA"))) {

						data.setNetwork_date(rs.getString(4));
					}

				} else {

					/* System.out.println(rs.getString(5)); */
					category = category + rs.getString(1);
					if (data == null) {

						data = new ProcessDtlBean();
						data.setCategory(rs.getString(1));

						if (!(rs.getString(2).equals("NA"))) {
							data.setCbs_date(rs.getString(2));

						}
						if (!(rs.getString(3).equals("NA"))) {

							data.setSwitch_date(rs.getString(3));

						}
						if (!(rs.getString(4).equals("NA"))) {

							data.setNetwork_date(rs.getString(4));
						}

					} else {

						processDtlBeans.add(data);
						data = new ProcessDtlBean();
						data.setCategory(rs.getString(1));

						if (!(rs.getString(2).equals("NA"))) {
							data.setCbs_date(rs.getString(2));

						}
						if (!(rs.getString(3).equals("NA"))) {

							data.setSwitch_date(rs.getString(3));

						}
						if (!(rs.getString(4).equals("NA"))) {

							data.setNetwork_date(rs.getString(4));
						}

					}
				}

			}

			processDtlBeans.add(data);
			logger.info("Success  " );
			return processDtlBeans;
		} catch (Exception ex) {

			logger.error(ex);
			ex.printStackTrace();
			return null;
		}
	}

}
