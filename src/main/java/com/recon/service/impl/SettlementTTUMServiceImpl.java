package com.recon.service.impl;

import com.google.gson.Gson;
import com.recon.model.MastercardUploadBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.RupayUploadBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.SettlementTTUMService;
import com.recon.util.OracleConn;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;

public class SettlementTTUMServiceImpl extends JdbcDaoSupport implements SettlementTTUMService {
	private static final Logger logger = Logger.getLogger(com.recon.service.impl.SettlementTTUMServiceImpl.class);

	private static final String O_ERROR_MESSAGE = "msg";

	public HashMap<String, Object> validateMonthlySettlement(NFSSettlementBean beanObj) {
		HashMap<String, Object> mapObj = new HashMap<>();
		try {
			int file_id = ((Integer) getJdbcTemplate().queryForObject(
					"SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?",
					new Object[] { beanObj.getFileName(), beanObj.getCategory(), beanObj.getStSubCategory() },
					Integer.class)).intValue();
			String checkProc = "SELECT COUNT(*) FROM MAIN_MONTHLY_NTSL_UPLOAD WHERE process_flag = 'Y' and FILEID = ? AND FILEDATE = ?";
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(checkProc,
					new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker() }, Integer.class)).intValue();
			if (checkCount > 0) {
				mapObj.put("result", Boolean.valueOf(true));
			} else {
				mapObj.put("result", Boolean.valueOf(false));
				mapObj.put("msg", "Settlement is not processed for selected Month");
			}
		} catch (Exception e) {
			logger.info("Exception in validateMonthlySettlement " + e);
			mapObj.put("result", Boolean.valueOf(false));
			mapObj.put("msg", "Exception Occurred");
		}
		return mapObj;
	}

	public boolean runNFSMonthlyTTUM(NFSSettlementBean beanObj) {
		return false;}

	public List<Object> getMonthlyTTUMData(NFSSettlementBean beanObj) {
		return null;}

	public ArrayList<String> getColumnList(String tableName) throws SQLException, Exception {
		OracleConn oracleConn = new OracleConn();
		oracleConn.createConnection();
		String query = "select * from " + tableName;
		ResultSet rs = oracleConn.executeQuery(query);
		if (rs == null)
			return null;
		ResultSetMetaData rsMetaData = rs.getMetaData();
		int numberOfColumns = rsMetaData.getColumnCount();
		ArrayList<String> typeList = new ArrayList<>();
		for (int i = 1; i < numberOfColumns + 1; i++) {
			String columnName = rsMetaData.getColumnName(i);
			typeList.add(columnName);
		}
		rs.close();
		oracleConn.CloseConnection();
		System.out.println(typeList);
		return typeList;
	}

	public HashMap<String, Object> validateDailyInterchange(NFSSettlementBean beanObj) {
		HashMap<String, Object> mapObj = new HashMap<>();
		try {
			int file_id = ((Integer) getJdbcTemplate().queryForObject(
					"SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?",
					new Object[] { beanObj.getFileName(), beanObj.getCategory(), beanObj.getStSubCategory() },
					Integer.class)).intValue();
			String checkProc = "select count(*) from MAIN_SETTLEMENT_FILE_UPLOAD where fileid = ? and filedate = ? and interchange_flag = 'Y'";
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(checkProc,
					new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker() }, Integer.class)).intValue();
			String getCycleCount = "select FILE_COUNT from main_filesource where FILEID = ?";
			int cycleCount = ((Integer) getJdbcTemplate().queryForObject(getCycleCount,
					new Object[] { Integer.valueOf(file_id) }, Integer.class)).intValue();
			logger.info("Cycle count is " + cycleCount);
			if (checkCount > 0 && checkCount == cycleCount) {
				mapObj.put("result", Boolean.valueOf(true));
			} else {
				mapObj.put("result", Boolean.valueOf(false));
				mapObj.put("msg", "Interchange Report is not processed for selected Date");
			}
		} catch (Exception e) {
			logger.info("Exception in validateDailyInterchange " + e);
			mapObj.put("result", Boolean.valueOf(false));
			mapObj.put("msg", "Exception Occurred");
		}
		return mapObj;
	}

	public boolean checkDailyInterchangeTTUMProcess(NFSSettlementBean beanObj) {
		try {
			int file_id = ((Integer) getJdbcTemplate().queryForObject(
					"SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?",
					new Object[] { beanObj.getFileName(), beanObj.getCategory(), beanObj.getStSubCategory() },
					Integer.class)).intValue();
			String checkCount = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE = ? AND TTUM_FLAG = 'Y'";
			int getCount = ((Integer) getJdbcTemplate().queryForObject(checkCount,
					new Object[] { Integer.valueOf(file_id), beanObj.getDatepicker() }, Integer.class)).intValue();
			if (getCount > 0)
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception occured in checkDailyInterchangeTTUMProcess " + e);
			return false;
		}
	}

	public boolean runDailyInterchangeTTUM(NFSSettlementBean beanObj) {
		return false;}

	public List<Object> getDailyInterchangeTTUMData(NFSSettlementBean beanObj) {
		return null;}

	public boolean runSettlementVoucher(NFSSettlementBean beanObj) {
		return false;}

	public List<Object> getSettlementVoucher(NFSSettlementBean beanObj) {
		return null;}

	public boolean runAdjTTUMnew(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams1 = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			AdjTTUMnew exe = new AdjTTUMnew( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			inParams.put("V_ADJTYPE", beanObj.getAdjType());
			outParams2 = exe.execute(inParams);
			logger.info("NFS_ADJ_REPORT settl " + outParams2.toString());
			if (outParams2 != null && outParams2.get("o_error_message") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams2.get("o_error_message"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class AdjTTUMnew extends StoredProcedure {
		private static final String insert_proc = "NFS_ADJ_TTUM_NEW";

		public AdjTTUMnew(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE",OracleTypes.INTEGER));
			declareParameter(new SqlParameter("V_ADJTYPE", Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			// OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runAdjTTUM(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams1 = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL" + beanObj.getAdjType().replaceAll("-", " "));
			String adjType = beanObj.getAdjType().replaceAll("-", " ");
			AdjTTUMProc exe = new AdjTTUMProc( getJdbcTemplate());
			System.out.println("date " + beanObj.getDatepicker());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			inParams.put("V_ADJTYPE", adjType);
			outParams2 = exe.execute(inParams);
			logger.info("NFS_ADJ_REPORT  " + outParams2.toString());
			if (outParams2 != null && outParams2.get("o_error_message") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams2.get("o_error_message"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class AdjTTUMProc extends StoredProcedure {
		private static final String insert_proc = "NFS_ADJ_REPORT_NEW";

		public AdjTTUMProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);

			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE",OracleTypes.INTEGER));
			declareParameter(new SqlParameter("V_ADJTYPE", Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			// OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> AdjTTUMProcSETTLnewICD(NFSSettlementBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams1 = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			AdjTTUMProcSETTLnewICD exe = new AdjTTUMProcSETTLnewICD( getJdbcTemplate());
			inParams.put("V_FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			outParams2 = exe.execute(inParams);
			logger.info("NFS_ADJ_REPORT settl " + outParams2.toString());
			if (outParams2 != null && outParams2.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams2.get("msg"));
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "UNABLE TO PROCESS! ");
			} else if (outParams2.get("msg") == "ALL") {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "ALLREDY PROCESSED! ");
			} else if (outParams2.get("msg") == "NOT") {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "FILE NOT PROCESSED! ");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "PROCESS SUCCESS! ");
			}
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "Exception  ");
			return output;
		}
		return output;
	}
	private class AdjTTUMProcSETTLnewICD extends StoredProcedure {
		private static final String insert_proc = "ICD_DAILY_SETTLEMENT_TTUM";

		public AdjTTUMProcSETTLnewICD(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			// declareParameter(new SqlParameter("V_ADJTYPE",  Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}


	public HashMap<String, Object> runAdjTTUMSETTLnew(NFSSettlementBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams1 = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			AdjTTUMProcSETTLnew exe = new AdjTTUMProcSETTLnew( getJdbcTemplate());
			inParams.put("V_FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			outParams2 = exe.execute(inParams);
			logger.info("NFS_ADJ_REPORT settl " + outParams2.toString());
			if (outParams2 != null && outParams2.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams2.get("msg"));
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "UNABLE TO PROCESS! ");
			} else if (outParams2.get("msg") == "ALL") {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "ALLREDY PROCESSED! ");
			} else if (outParams2.get("msg") == "NOT") {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "FILE NOT PROCESSED! ");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "PROCESS SUCCESS! ");
			}
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "Exception  ");
			return output;
		}
		return output;
	}
	private class AdjTTUMProcSETTLnew extends StoredProcedure {
		private static final String insert_proc = "NFS_DAILY_SETTLEMENT_TTUM";

		public AdjTTUMProcSETTLnew(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			// declareParameter(new SqlParameter("V_ADJTYPE",  Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> ANKITTREPORTSnew(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			System.out.println("date " + beanObj.getDatepicker() + " V_ADJTYPE " + beanObj.getAdjType());
			ANKITREPORSnew exe = new ANKITREPORSnew( getJdbcTemplate());
			inParams.put("V_FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			inParams.put("V_ADJTYPE", beanObj.getAdjType());
			outParams2 = exe.execute(inParams);
			String value = outParams2.toString();
			logger.info("NFS_ADJ_REPORT settl " + outParams2.toString() + "value " + value);
			if (value.contains("NOT")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "FILE NOT PROCESSED! ");
			} else if (value.contains("ALL")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "ALLREDY PROCESSED!");
			} else if (value.contains("SUCCESS")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "PROCESSED SUCCESS");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", outParams2.get("o_error_message"));
			}
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "Exception  ");
			return output;
		}
		return output;
	}


	private class ANKITREPORSnew extends StoredProcedure {
		private static final String insert_proc = "NFS_ADJ_REPORT_PNB";

		public ANKITREPORSnew(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			declareParameter(new SqlParameter("V_ADJTYPE", Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> ANKITTREPORTSnewICCW(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			System.out.println("date " + beanObj.getDatepicker() + " V_ADJTYPE " + beanObj.getAdjType());
			ANKITTREPORTSnewICCW exe = new ANKITTREPORTSnewICCW( getJdbcTemplate());
			inParams.put(" V_FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			inParams.put("V_ADJTYPE", beanObj.getAdjType());
			outParams2 = exe.execute(inParams);
			String value = outParams2.toString();
			logger.info("NFS_ADJ_REPORT settl " + outParams2.toString() + "value " + value);
			if (value.contains("NOT")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "FILE NOT PROCESSED! ");
			} else if (value.contains("ALL")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "ALLREDY PROCESSED!");
			} else if (value.contains("SUCCESS")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "PROCESSED SUCCESS");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", outParams2.get("o_error_message"));
			}
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "Exception  ");
			return output;
		}
		return output;
	}


	private class ANKITTREPORTSnewICCW extends StoredProcedure {
		private static final String insert_proc = "NFS_ADJ_ICCW_REPORT_PNB";

		public ANKITTREPORTSnewICCW(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			declareParameter(new SqlParameter("V_ADJTYPE", Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> ANKITACQPRNALTITTUM(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			System.out.println("date " + beanObj.getDatepicker() + " V_ADJTYPE " + beanObj.getAdjType());
			ANKITACQPRNALTITTUM exe = new ANKITACQPRNALTITTUM( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams2 = exe.execute(inParams);
			String value = outParams2.toString();
			logger.info("NFS_ADJ_REPORT settl " + outParams2.toString() + "value " + value);
			if (value.contains("NOT")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "FILE NOT PROCESSED! ");
			} else if (value.contains("ALL")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "ALLREDY PROCESSED! ");
			} else if (value.contains("SUCCESS")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "PROCESSED SUCCESS");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", outParams2.get("o_error_message"));
			}
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "Exception  ");
			return output;
		}
		return output;
	}

	private class ANKITACQPRNALTITTUM extends StoredProcedure {
		private static final String insert_proc = "NFS_ACQ_PENALITY_ADJUSTMENT_TTUM";

		public ANKITACQPRNALTITTUM(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));

			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> ANKITREPORSnewICD(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			System.out.println("date " + beanObj.getDatepicker() + " V_ADJTYPE " + beanObj.getAdjType());
			ANKITREPORSnewICD exe = new ANKITREPORSnewICD( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			inParams.put("V_ADJTYPE", beanObj.getAdjType());
			outParams2 = exe.execute(inParams);
			String value = outParams2.toString();
			logger.info("NFS_ADJ_REPORT settl " + outParams2.toString() + "value " + value);
			if (value.contains("NOT")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "FILE NOT PROCESSED! ");
			} else if (value.contains("ALL")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "ALLREDY PROCESSED! ");
			} else if (value.contains("SUCCESS")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "PROCESSED SUCCESS");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", outParams2.get("o_error_message"));
			}
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "Exception  ");
			return output;
		}
		return output;
	}
	private class ANKITREPORSnewICD extends StoredProcedure {
		private static final String insert_proc = "ICD_ADJ_REPORT_NEW";

		public ANKITREPORSnewICD(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			declareParameter(new SqlParameter("V_ADJTYPE",  Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}


	public HashMap<String, Object> ANKITTTUMnew(NFSSettlementBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams1 = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			System.out.println("date " + beanObj.getDatepicker() + " V_ADJTYPE " + beanObj.getAdjType());
			ANKITTTUMSnew exe = new ANKITTTUMSnew( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			inParams.put("V_ADJTYPE", beanObj.getAdjType());
			outParams2 = exe.execute(inParams);
			logger.info("NFS_ADJ_REPORT settl " + outParams2.toString());
			String value = outParams2.toString();
			if (value.contains("NOT")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "FILE NOT PROCESSED!");
			} else if (value.contains("ALL")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "ALLREDY PROCESSED!");
			} else if (value.contains("SUCCESS")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "PROCESSED SUCCESS");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", outParams2.get("o_error_message"));
			}
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "Exception");
			return output;
		}
		return output;
	}


	private class ANKITTTUMSnew extends StoredProcedure {
		private static final String insert_proc = "NFS_ADJ_TTUM_NEW";

		public ANKITTTUMSnew(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			declareParameter(new SqlParameter("V_ADJTYPE",  Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> ANKITTTUMnewICD(NFSSettlementBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams1 = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			System.out.println("date " + beanObj.getDatepicker() + " V_ADJTYPE " + beanObj.getAdjType());
			ANKITTTUMnewICD exe = new ANKITTTUMnewICD( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			inParams.put("V_ADJTYPE", beanObj.getAdjType());
			outParams2 = exe.execute(inParams);
			logger.info("NFS_ADJ_REPORT settl " + outParams2.toString());
			String value = outParams2.toString();
			if (value.contains("NOT")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "FILE NOT PROCESSED! ");
			} else if (value.contains("ALL")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "ALLREDY PROCESSED! ");
			} else if (value.contains("SUCCESS")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "PROCESSED SUCCESS");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", outParams2.get("o_error_message"));
			}
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "Exception  ");
			return output;
		}
		return output;
	}
	private class ANKITTTUMnewICD extends StoredProcedure {
		private static final String insert_proc = "ICD_ADJ_TTUM_NEW";

		public ANKITTTUMnewICD(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			declareParameter(new SqlParameter("V_ADJTYPE",  Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	
	public HashMap<String, Object> runAdjTTUMSETTL(NFSSettlementBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams1 = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			AdjTTUMProcSETTL exe = new AdjTTUMProcSETTL( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			inParams.put("V_CYCLE", Integer.valueOf(beanObj.getCycle()));
			outParams2 = exe.execute(inParams);
			logger.info("NFS_ADJ_REPORT settl " + outParams2.toString());
			String value = outParams2.toString();
			if (value.contains("NOT")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "FILE NOT PROCESSED! ");
			} else if (value.contains("ALL")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "ALLREDY PROCESSED! ");
			} else if (value.contains("SUCCESS")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "PROCESSED SUCCESS");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", outParams2.get("msg"));
			}
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "Exception  ");
			return output;
		}
		return output;
	}

	private class AdjTTUMProcSETTL extends StoredProcedure {
		private static final String insert_proc = "NFS_DAILY_SETTLEMENT_PNB";

		public AdjTTUMProcSETTL(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_CYCLE",  Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			// declareParameter(new SqlParameter("V_ADJTYPE",  Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> AdjTTUMProcSETTLICD(NFSSettlementBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams1 = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			AdjTTUMProcSETTLICD exe = new AdjTTUMProcSETTLICD( getJdbcTemplate());
			inParams.put("V_FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			outParams2 = exe.execute(inParams);
			logger.info("NFS_ADJ_REPORT settl " + outParams2.toString());
			String value = outParams2.toString();
			if (value.contains("NOT")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "FILE NOT PROCESSED! ");
			} else if (value.contains("ALL")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "ALLREDY PROCESSED! ");
			} else if (value.contains("SUCCESS")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "PROCESSED SUCCESS");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", outParams2.get("msg"));
			}
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "Exception  ");
			return output;
		}
		return output;
	}
	private class AdjTTUMProcSETTLICD extends StoredProcedure {
		private static final String insert_proc = "ICD_DAILY_SETTLEMENT";

		public AdjTTUMProcSETTLICD(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_CYCLE",  Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			// declareParameter(new SqlParameter("V_ADJTYPE",  Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> AdjTTUMProcSETTLDFS(NFSSettlementBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams1 = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			AdjTTUMProcSETTLDFS exe = new AdjTTUMProcSETTLDFS( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			outParams2 = exe.execute(inParams);
			logger.info("NFS_ADJ_REPORT settl " + outParams2.toString());
			String value = outParams2.toString();
			if (value.contains("NOT")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "FILE NOT PROCESSED! ");
			} else if (value.contains("ALL")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "ALLREDY PROCESSED! ");
			} else if (value.contains("SUCCESS")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "PROCESSED SUCCESS");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", outParams2.get("msg"));
			}
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "Exception  ");
			return output;
		}
		return output;
	}

	private class AdjTTUMProcSETTLDFS extends StoredProcedure {
		private static final String insert_proc = "DFS_DAILY_SETTLEMENT_PNB";

		public AdjTTUMProcSETTLDFS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));
		
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			// declareParameter(new SqlParameter("V_ADJTYPE",  Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> AdjTTUMProcSETTLJCB(NFSSettlementBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams1 = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			AdjTTUMProcSETTLJCB exe = new AdjTTUMProcSETTLJCB( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			outParams2 = exe.execute(inParams);
			logger.info("NFS_ADJ_REPORT settl " + outParams2.toString());
			String value = outParams2.toString();
			if (value.contains("NOT")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "FILE NOT PROCESSED! ");
			} else if (value.contains("ALL")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "ALLREDY PROCESSED! ");
			} else if (value.contains("SUCCESS")) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "PROCESSED SUCCESS");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", outParams2.get("msg"));
			}
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "Exception  ");
			return output;
		}
		return output;
	}
	private class AdjTTUMProcSETTLJCB extends StoredProcedure {
		private static final String insert_proc = "JCB_DAILY_SETTLEMENT_PNB";

		public AdjTTUMProcSETTLJCB(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			// declareParameter(new SqlParameter("V_ADJTYPE",  Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}

	public boolean adjTTUMProc(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams1 = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL" + beanObj.getAdjType().replaceAll("-", " "));
			String adjType = beanObj.getAdjType().replaceAll("-", " ");
			AdjTTUMProc2 exe = new AdjTTUMProc2( getJdbcTemplate());
			System.out.println("date " + beanObj.getDatepicker());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			inParams.put("V_ADJTYPE", adjType);
			outParams2 = exe.execute(inParams);
			logger.info("NFS_ADJ_REPORT  " + outParams2.toString());
			if (outParams2 != null && outParams2.get("o_error_message") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams2.get("o_error_message"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class AdjTTUMProc2 extends StoredProcedure {
		private static final String insert_proc = "NFS_ADJ_TTUM_NEW";

		public AdjTTUMProc2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);

			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			declareParameter(new SqlParameter("V_ADJTYPE",  Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean adjTTUMProcSETTL(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams1 = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM text");
			System.out.println("query " + beanObj.getDatepicker());
			beanObj.setDatepicker(beanObj.getDatepicker().replaceAll("/", "-"));
			AdjTTUMProc2SETTL exe = new AdjTTUMProc2SETTL( getJdbcTemplate());
			inParams.put("V_FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			outParams2 = exe.execute(inParams);
			Gson g = new Gson();
			logger.info("NFS_ADJ_TTUM  " + outParams2.toString());
			if (outParams2 != null && outParams2.get("o_error_message") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams2.get("o_error_message"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class AdjTTUMProc2SETTL extends StoredProcedure {
		private static final String insert_proc = "NFS_DAILY_SETTLEMENT_TTUM";

		public AdjTTUMProc2SETTL(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			// declareParameter(new SqlParameter("V_ADJTYPE",  Types.VARCHAR));
			// declareParameter(new SqlParameter("SUBCATE",
			//  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public List<Object> getDailyNFSTTUMDataRupay(RupayUploadBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			if( beanObj.getCycle().equalsIgnoreCase("5")) {
				getData = "SELECT  CONCAT(RPAD(GL_CODE,16,' '),RPAD(CONCAT('INR',SUBSTR(GL_CODE,1,6)),11,' '),CASE WHEN DEBIT>0 THEN 'D' ELSE 'C' END,\r\nLPAD(REPLACE(FORMAT(CASE WHEN DEBIT>0 THEN DEBIT ELSE CREDIT END, 2),',',''),17,'0') ,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' ')\r\n,LPAD(REMARKS,416,' '))TTUM FROM  rupay_pos_dom_settlement WHERE FILEDATE=STR_TO_DATE('"+beanObj.getFileDate() + "','%Y/%m/%d')  ";
				
			}else {
				
				getData = "SELECT  CONCAT(RPAD(GL_CODE,16,' '),RPAD(CONCAT('INR',SUBSTR(GL_CODE,1,6)),11,' '),CASE WHEN DEBIT>0 THEN 'D' ELSE 'C' END,\r\nLPAD(REPLACE(FORMAT(CASE WHEN DEBIT>0 THEN DEBIT ELSE CREDIT END, 2),',',''),17,'0') ,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' ')\r\n,LPAD(REMARKS,416,' '))TTUM FROM  rupay_pos_dom_settlement WHERE FILEDATE=STR_TO_DATE('"+beanObj.getFileDate() + "','%Y/%m/%d')  and cycle='" + beanObj.getCycle() + "'";
				
			}
		List<Object> DailyData = getJdbcTemplate().query(getData,
					new Object[] {  }, new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getDailyNFSTTUMDataRupayRRB(RupayUploadBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			getData = "SELECT CONCAT(RPAD(GL_CODE,16,' ') ,CONCAT('INR',516500),'  ',CASE when DEBIT>0 THEN 'D' WHEN CREDIT>0 THEN 'C' ELSE '' END\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DEBIT>0 THEN DEBIT ELSE CREDIT END, 2),',',''),18,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_dom_rrb_settlement WHERE FILEDATE=STR_TO_DATE('"
					+

					beanObj.getFileDate() + "','%Y/%m/%d')  and cycle='" + beanObj.getCycle() + "'";
			List<Object> DailyData = getJdbcTemplate().query(getData,
					new Object[] {  }, new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getDailyNFSTTUMDataVISA(RupayUploadBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
				if (beanObj.getTTUM_TYPE().equalsIgnoreCase("TTUM_ACQ")) {
					getData = "SELECT CONCAT(RPAD(GL_ACCOUNT,16,' ') ,INR,SIX_DIG_GL,'  ',D_C,LPAD(REPLACE(FORMAT(CASE WHEN D_C='D' THEN DEBIT ELSE CREDIT END, 2),',',''),17,'0'), RPAD(NARRATION,15,' '),REMARKS,LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(PARTICULARS,417,' ')) AS TTUM\r\nFROM visa_dom_acq_settlement_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
				} else {
					getData = "SELECT CONCAT(RPAD(GL_ACCOUNT,16,' ') ,INR,SIX_DIG_GL,'  ',D_C,LPAD(REPLACE(FORMAT(CASE WHEN D_C='D' THEN DEBIT ELSE CREDIT END, 2),',',''),17,'0'), RPAD(NARRATION,15,' '),REMARKS,LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(PARTICULARS,417,' ')) AS TTUM\r\nFROM visa_dom_iss_settlement_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
				}
			} else if (beanObj.getTTUM_TYPE().equalsIgnoreCase("TTUM_ACQ")  ) {
				getData = "SELECT CONCAT(RPAD(GL_ACCOUNT,16,' ') ,INR,SIX_DIG_GL,'  ',D_C,LPAD(REPLACE(FORMAT(CASE WHEN D_C='D' THEN DEBIT ELSE CREDIT END, 2),',',''),17,'0'),\r\nRPAD(NARRATION,15,' '),REMARKS,LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(PARTICULARS,417,' ')) AS TTUM\r\nFROM visa_int_acq_settlement_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
			} else {
				getData = "SELECT CONCAT(RPAD(GL_ACCOUNT,16,' ') ,INR,SIX_DIG_GL,'  ',D_C,LPAD(REPLACE(FORMAT(CASE WHEN D_C='D' THEN DEBIT ELSE CREDIT END, 2),',',''),17,'0'),\r\nRPAD(NARRATION,15,' '),REMARKS,LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(PARTICULARS,417,' ')) AS TTUM\r\nFROM visa_int_iss_settlement_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
			}
			System.out.println("query "+ getData);
			List<Object> DailyData = getJdbcTemplate().query(getData,
					new Object[] {    beanObj.getFileDate()}, new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getDailyNFSTTUMDataRupayINT(RupayUploadBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			getData = "SELECT CONCAT(RPAD(GL_CODE,16,' ') ,CONCAT('INR',SUBSTR(GL_CODE,1,6)),'  ',CASE WHEN DEBIT>0 THEN 'D' ELSE 'C' END\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DEBIT>0 THEN DEBIT ELSE CREDIT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,20,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,413,' ')\r\n)  AS TTUM FROM  rupay_pos_int_settlement  WHERE FILEDATE=STR_TO_DATE('"
					+

					beanObj.getFileDate() + "','%Y/%m/%d')";
			List<Object> DailyData = getJdbcTemplate().query(getData,
					new Object[] { }, new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getDailyNFSTTUMDataRupayQSPARC(RupayUploadBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			getData = "SELECT CONCAT(RPAD(GL_CODE,16,' ') ,CONCAT('INR',SUBSTR(GL_CODE,1,6)),'  ',CASE WHEN DEBIT>0 THEN 'D' ELSE 'C' END\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DEBIT>0 THEN DEBIT ELSE CREDIT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,20,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,413,' ')\r\n)  AS TTUM FROM   qsparc_pos_dom_settlement WHERE FILEDATE=STR_TO_DATE('"
					+

					beanObj.getFileDate() + "','%Y/%m/%d') and cycle='" + beanObj.getCycle() + "'";
			List<Object> DailyData = getJdbcTemplate().query(getData,
					new Object[] { }, new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getDailyNFSTTUMDataRupayQSPARCINT(RupayUploadBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			getData = "SELECT CONCAT(RPAD(GL_CODE,16,' ') ,CONCAT('INR',SUBSTR(GL_CODE,1,6)),'  ',CASE WHEN DEBIT>0 THEN 'D' ELSE 'C' END\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DEBIT>0 THEN DEBIT ELSE CREDIT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,20,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,413,' ')\r\n)  AS TTUM FROM  qsparc_pos_int_settlement WHERE FILEDATE=STR_TO_DATE('"
					+

					beanObj.getFileDate() + "','%Y/%m/%d') ";
			List<Object> DailyData = getJdbcTemplate().query(getData,
					new Object[] {  }, new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getDailyNFSTTUMDataRUPAY(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			if (beanObj.getCategory().contains("RUPAY")) {
				if (beanObj.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
					if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
						getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_decl_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
						List<Object> DailyData = getJdbcTemplate().query(getData,
								new Object[] { beanObj.getLocalDate() }, new ResultSetExtractor<List<Object>>() {
									public List<Object> extractData(ResultSet rs) throws SQLException {
										List<Object> beanList = new ArrayList<Object>();

										while (rs.next()) {
											Map<String, String> table_Data = new HashMap<String, String>();
											table_Data.put("TTUM", rs.getString("TTUM"));
											beanList.add(table_Data);
										}
										return beanList;
									}
								});

						return DailyData;
					}
					if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
						getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_proactiv_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
						List<Object> DailyData = getJdbcTemplate().query(getData,
								new Object[] { beanObj.getLocalDate() }, new ResultSetExtractor<List<Object>>() {
									public List<Object> extractData(ResultSet rs) throws SQLException {
										List<Object> beanList = new ArrayList<Object>();

										while (rs.next()) {
											Map<String, String> table_Data = new HashMap<String, String>();
											table_Data.put("TTUM", rs.getString("TTUM"));
											beanList.add(table_Data);
										}
										return beanList;
									}
								});

						return DailyData;
					}
					if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
						getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_drop_ttum WHERE FILEDATE=STR_TO_DATE('"
								+

								beanObj.getLocalDate() + "','%Y/%m/%d')";
						List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
								new ResultSetExtractor<List<Object>>() {
									public List<Object> extractData(ResultSet rs) throws SQLException {
										List<Object> beanList = new ArrayList<Object>();

										while (rs.next()) {
											Map<String, String> table_Data = new HashMap<String, String>();
											table_Data.put("TTUM", rs.getString("TTUM"));
											beanList.add(table_Data);
										}
										return beanList;
									}
								});

						return DailyData;
					}
					if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
						getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',CASE when DR_CR='DR' THEN 'D' WHEN DR_CR='CR' THEN 'C' ELSE '' END\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='DR' THEN SUR ELSE SUR END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_dom_dr_surch_ttum WHERE FILEDATE=STR_TO_DATE('"
								+

								beanObj.getLocalDate() + "','%Y/%m/%d')";
						List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
								new ResultSetExtractor<List<Object>>() {
									public List<Object> extractData(ResultSet rs) throws SQLException {
										List<Object> beanList = new ArrayList<Object>();

										while (rs.next()) {
											Map<String, String> table_Data = new HashMap<String, String>();
											table_Data.put("TTUM", rs.getString("TTUM"));
											beanList.add(table_Data);
										}
										return beanList;
									}
								});

						return DailyData;
					}
					if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
						getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',CASE when DR_CR='DR' THEN 'D' WHEN DR_CR='CR' THEN 'C' ELSE '' END\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='DR' THEN SUR ELSE SUR END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_dom_cr_surch_ttum WHERE FILEDATE=STR_TO_DATE('"
								+

								beanObj.getLocalDate() + "','%Y/%m/%d')";
						List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
								new ResultSetExtractor<List<Object>>() {
									public List<Object> extractData(ResultSet rs) throws SQLException {
										List<Object> beanList = new ArrayList<Object>();

										while (rs.next()) {
											Map<String, String> table_Data = new HashMap<String, String>();
											table_Data.put("TTUM", rs.getString("TTUM"));
											beanList.add(table_Data);
										}
										return beanList;
									}
								});

						return DailyData;
					}
					if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
						getData = "SELECT CONCAT(RPAD(CARD_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM dom_offline_pres_ttum WHERE FILEDATE=STR_TO_DATE('"
								+

								beanObj.getLocalDate() + "','%Y/%m/%d')";
						System.out.println(" getData " + getData);
						List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
								new ResultSetExtractor<List<Object>>() {
									public List<Object> extractData(ResultSet rs) throws SQLException {
										List<Object> beanList = new ArrayList<Object>();

										while (rs.next()) {
											Map<String, String> table_Data = new HashMap<String, String>();
											table_Data.put("TTUM", rs.getString("TTUM"));
											beanList.add(table_Data);
										}
										return beanList;
									}
								});

						return DailyData;
					}
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_dom_latepresentment WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_int_decl_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_int_ofline_pres_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
					System.out.println(" getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_proactiv_int_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_drop_int_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',CASE when DR_CR='DR' THEN 'D' WHEN DR_CR='CR' THEN 'C' ELSE '' END\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='DR' THEN SUR ELSE SUR END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_int_dr_surch_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',CASE when DR_CR='DR' THEN 'D' WHEN DR_CR='CR' THEN 'C' ELSE '' END\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='DR' THEN SUR ELSE SUR END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_int_cr_surch_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM rupay_int_latepresentment WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
					getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM QSPARC_DECL_DOM_TTUM \r\nWHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE(?,'DD-MM-YY')";
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM QSPARC_DOM_PROACTIV_TTUM  WHERE  to_date(filedate,'DD-MM-YY') = TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					getData = "SELECT CONCAT(RPAD(CARD_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM dom_offline_pres_ttum WHERE FILEDATE=STR_TO_DATE('"
							+

							beanObj.getFileDate() + "','%Y/%m/%d')\r\n" + "UNION ALL\r\n"
							+ "SELECT CONCAT(RPAD(CARD_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n"
							+ "  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n"
							+ ",RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n"
							+ ")  AS TTUM FROM qsp_dom_offline_pres_ttum WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
					getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CR' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(SUR*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM QSPARC_DOM_DR_SURCH_TTUM WHERE  TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
					getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CR' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(SUR*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM QSPARC_DOM_CR_SURCH_TTUM WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					getData = "SELECT CONCAT(RPAD(CARD_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM dom_offline_pres_ttum WHERE FILEDATE=STR_TO_DATE('"
							+

							beanObj.getFileDate() + "','%Y/%m/%d')\r\n" + "UNION ALL\r\n"
							+ "SELECT CONCAT(RPAD(CARD_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n"
							+ "  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n"
							+ ",RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n"
							+ ")  AS TTUM FROM qsp_dom_offline_pres_ttum WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM QSPARC_DOM_LATEPRESENTMENT WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
				getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_INT_DECL_TTUM WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE(?,'DD-MM-YY')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_PROACTIV_INT_TTUM WHERE  to_date(filedate,'DD-MM-YY') = TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_DROP_INT_TTUM \r\nWHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
				getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(SUR*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_INT_DR_SURCH_TTUM WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
				getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(SUR*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_INT_CR_SURCH_TTUM WHERE  TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			} else {
				getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM REUPAY_DOM_LATEPRESENTMENT \r\nWHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}

		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getDailyNFSTTUMDataMASTERCARD(UnMatchedTTUMBean beanObj) {List<Object> data = new ArrayList();
	try {
		String getData = null;
		if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER_DOM")) {
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_CREDIT")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM mc_acq_dom_atm_loro_credit_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_DEBIT")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM mc_acq_dom_atm_loro_debit_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CROSS")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR,\r\nLPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM \r\nFROM mc_acq_cross_recon_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
				getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(SUR*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_DOM_DR_SURCH_TTUM WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
				getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'CR' ELSE 'DR' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(SUR*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_DOM_CR_SURCH_TTUM WHERE  TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
				getData = "SELECT LPAD(AC_NO,15,'0') ||CASE WHEN DR_CR='CREDIT' THEN 'C' ELSE 'D' END ||  TO_DATE(FILEDATE,'DD-MM-YY') ||RPAD(SUBSTR(NARRATION,1,LENGTH(NARRATION) ),50,' ') || \r\n    CASE WHEN DR_CR='CREDIT' THEN LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') ELSE LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') END AS TTUM FROM DOM_OFFLINE_PRES_TTUM WHERE  TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')" + "    ";
				System.out.println(" getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM REUPAY_DOM_LATEPRESENTMENT \r\nWHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY')";
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		}
		if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER_INT")) {
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_CREDIT")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  \r\n,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' ')\r\n,LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM mc_acq_int_atm_loro_credit_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_DEBIT")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,\r\nLPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),\r\nLPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM \r\nFROM mc_acq_int_atm_loro_debit_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CROSS")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR,\r\nLPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM \r\nFROM mc_acq_cross_recon_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
				getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(SUR*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_DOM_DR_SURCH_TTUM WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
				getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'CR' ELSE 'DR' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(SUR*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_DOM_CR_SURCH_TTUM WHERE  TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
				getData = "SELECT LPAD(AC_NO,15,'0') ||CASE WHEN DR_CR='CREDIT' THEN 'C' ELSE 'D' END ||  TO_DATE(FILEDATE,'DD-MM-YY') ||RPAD(SUBSTR(NARRATION,1,LENGTH(NARRATION) ),50,' ') || \r\n    CASE WHEN DR_CR='CREDIT' THEN LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') ELSE LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') END AS TTUM FROM DOM_OFFLINE_PRES_TTUM WHERE  TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')" + "    ";
				System.out.println(" getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM REUPAY_DOM_LATEPRESENTMENT \r\nWHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY')";
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		}
		if (beanObj.getStSubCategory().equalsIgnoreCase("ISSUER")) {
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
				getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_DECL_TTUM WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE(?,'DD-MM-YY')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
				getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM FROM  mc_iss_pos_latepres_ttum WHERfiledate= str_to_date('"+beanObj.getLocalDate() + "','%Y/%m/%d')";		
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {  },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n"
						+ "  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n"
						+ ",RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' ')\r\n"
						+ ",LPAD(REMARKS,417,' ')\r\n"
						+ ")  AS TTUM FROM mastercard_proactive_pos_ttum   WHERE filedate= str_to_date('"+beanObj.getLocalDate() + "','%Y/%m/%d')";

				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM mc_iss_pos_drop_ttum    WHERE filedate= str_to_date('"+beanObj.getLocalDate() + "','%Y/%m/%d')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN SUR ELSE SUR END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM mc_dom_iss_dr_surch WHERE FILEDATE=STR_TO_DATE('"
						+

						beanObj.getLocalDate() + "','%Y/%m/%d')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ATMSURCHARGE")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN SUR ELSE SUR END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM mc_iss_atm_surcharge WHERE FILEDATE=STR_TO_DATE('"
						+

						beanObj.getLocalDate() + "','%Y/%m/%d')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN SUR ELSE SUR END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM mc_dom_iss_cr_surch WHERE FILEDATE=STR_TO_DATE('"
						+

						beanObj.getLocalDate() + "','%Y/%m/%d')";
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
				getData = "SELECT LPAD(AC_NO,15,'0') ||CASE WHEN DR_CR='CREDIT' THEN 'C' ELSE 'D' END ||  TO_DATE(FILEDATE,'DD-MM-YY') ||RPAD(SUBSTR(NARRATION,1,LENGTH(NARRATION) ),50,' ') || \r\n    CASE WHEN DR_CR='CREDIT' THEN LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') ELSE LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') END AS TTUM FROM  dom_offline_pres_ttum WHERE  filedate= str_to_date('"+beanObj.getLocalDate() + "','%Y/%m/%d')";
				System.out.println(" getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM REUPAY_DOM_LATEPRESENTMENT \r\nWHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY')";
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		}
		if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
			getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_INT_DECL_TTUM WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE(?,'DD-MM-YY')";
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		}
		if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
			getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_PROACTIV_INT_TTUM WHERE  to_date(filedate,'DD-MM-YY') = TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY')";
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		}
		if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
			getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_DROP_INT_TTUM \r\nWHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY')";
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		}
		if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
			getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(SUR*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_INT_DR_SURCH_TTUM WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY')";
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		}
		if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
			getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(SUR*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM RUPAY_INT_CR_SURCH_TTUM WHERE  TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY')";
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		} else {
			getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM REUPAY_DOM_LATEPRESENTMENT \r\nWHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY')";
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		}

	} catch (Exception e) {
		System.out.println("Exception in getDailyInterchangeTTUMData " + e);
		return null;
	}}

	public List<Object> getDailyNFSTTUMData(NFSSettlementBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			if (beanObj.getAdjType().equalsIgnoreCase("PENALTY")) {
				getData = "SELECT TTUM FROM nfs_adj_report WHERE  FILEDATE=STR_TO_DATE('"+ beanObj.getDatepicker()+"','%Y/%m/%d')  AND ADJTYPE like '%"
						+ beanObj.getAdjType() + "%'";
				System.out.println("Data " + getData);
				
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getAdjType().equalsIgnoreCase("ACUIRER PENALTY")) {
				getData = "SELECT AC_NO AS\"GL_ACCOUNT\",'INR',SUBSTR(AC_NO,1,6) AS\"SIX_DIGIT_AC_NO\",DR_CR, CONCAT(LPAD(FLOOR(amount), 14, '0'),'.', LPAD(SUBSTRING_INDEX(amount, '.', -1), 2, '0')) AS AMOUNT\r\n  ,NARRATION,CONCAT(date_format(date_format(FILEDATE,'%Y/%m/%d'),'%d-%m-%Y'),'/','I',IFNULL(CYCLE,0),'/','NFSF')'REMARKS' ,UUID_SHORT()AS UID FROM   nfs_adj_report WHERE FILEDATE=date_format(?,'%Y/%m/%d')  AND ADJTYPE=?";
				System.out.println("Data " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData,
						new Object[] { beanObj.getDatepicker(), beanObj.getAdjType() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			} else {
				
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR',SUBSTR(AC_NO,1,6),'  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,40,' '),REMARKS,LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(CONCAT('ADJ',UID) ,417,' ')) AS TTUM\r\nFROM  nfs_adj_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') AND ADJTYPE=?";
				System.out.println("Data " + getData);
				
				List<Object> DailyData = getJdbcTemplate().query(getData,
						new Object[] { beanObj.getDatepicker(), beanObj.getAdjType() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}

		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getDailyNFSTTUMDataICD(NFSSettlementBean beanObj) {List<Object> data = new ArrayList();
	try {
		String getData = null;
		if (beanObj.getAdjType().equalsIgnoreCase("PENALTY")) {
			getData = "SELECT TTUM FROM ICD_ADJUSTMENT_TTUM WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE(?,'DD-MM-YY')  AND ADJTYPE like '%"
					+ beanObj.getAdjType() + "%' ORDER BY SUBSTR(TTUM,35,12)";
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getDatepicker() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		} else {
			getData = "SELECT TTUM FROM ICD_ADJUSTMENT_TTUM WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE(?,'DD-MM-YY')  AND UPPER(ADJTYPE)=? ORDER BY SUBSTR(TTUM,35,12)";
			List<Object> DailyData = getJdbcTemplate().query(getData,
					new Object[] { beanObj.getDatepicker(), beanObj.getAdjType() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		}

	} catch (Exception e) {
		System.out.println("Exception in getDailyInterchangeTTUMData " + e);
		return null;
	}}

	public List<Object> getNFSRECONACQTTUM(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			beanObj.setLocalDate(beanObj.getLocalDate().replaceAll("/", "-"));
			String getData = null;
			System.out
					.println("beanObj.getStSubCategory().equalsIgnoreCase(\"ACQUIRER\") " + beanObj.getStSubCategory());
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,418,' ')) AS TTUM\r\nFROM nfs_acq_recon_ttums WHERE FILEDATE=STR_TO_DATE('"
						+

						beanObj.getLocalDate() + "','%Y/%m/%d')  AND TTUM_TYPE='" + beanObj.getTypeOfTTUM() + "'";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			} else {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,418,' ')) AS TTUM\r\nFROM nfs_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE(?,'%Y-%m-%d')  AND TTUM_TYPE=?";
				logger.info("query " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData,
						new Object[] { beanObj.getLocalDate(), beanObj.getTypeOfTTUM() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}

		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}
	public List<Object> getISSICCW(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
	
			String getData = null;
			System.out
					.println("beanObj.getStSubCategory().equalsIgnoreCase(\"ACQUIRER\") " + beanObj.getStSubCategory());
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\n"
							+ "RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,418,' ')) AS TTUM\r\n"
							+ "FROM nfs_iccw_acq_recon_ttums WHERE FILEDATE=STR_TO_DATE('"+beanObj.getLocalDate()+"','%Y/%m/%d')  AND TTUM_TYPE='LORO CREDIT'";

				}else {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\n"
							+ "RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,418,' ')) AS TTUM\r\n"
							+ "FROM nfs_iccw_acq_recon_ttums WHERE FILEDATE=STR_TO_DATE('"+beanObj.getLocalDate()+"','%Y/%m/%d')  AND TTUM_TYPE='LORO DEBIT'";

				}			System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			} else {
				
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\n"
							+ "RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,418,' ')) AS TTUM\r\n"
							+ "FROM nfs_iccw_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE('"+beanObj.getLocalDate()+"','%Y/%m/%d')  AND TTUM_TYPE='PROACTIVE'";

				}else {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\n"
							+ "RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,418,' ')) AS TTUM\r\n"
							+ "FROM nfs_iccw_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE('"+beanObj.getLocalDate()+"','%Y/%m/%d')  AND TTUM_TYPE='DROP'";

				}
				logger.info("query " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData,
						new Object[] {  },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}

		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getACQJCB(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			beanObj.setLocalDate(beanObj.getLocalDate().replaceAll("/", "-"));
			String getData = null;
			System.out
					.println("beanObj.getStSubCategory().equalsIgnoreCase(\"ACQUIRER\") " + beanObj.getStSubCategory());
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,418,' ')) AS TTUM\r\nFROM jcb_acq_recon_ttums WHERE FILEDATE=STR_TO_DATE('"
						+

						beanObj.getLocalDate() + "','%Y/%m/%d')  AND TTUM_TYPE='" + beanObj.getTypeOfTTUM() + "'";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			} else {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,418,' ')) AS TTUM\r\nFROM nfs_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')  AND TTUM_TYPE=?";
				logger.info("query " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData,
						new Object[] { beanObj.getLocalDate(), beanObj.getTypeOfTTUM() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}

		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}
	public List<Object> getACQDFS(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			beanObj.setLocalDate(beanObj.getLocalDate().replaceAll("/", "-"));
			String getData = null;
			System.out
					.println("beanObj.getStSubCategory().equalsIgnoreCase(\"ACQUIRER\") " + beanObj.getStSubCategory());
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,418,' ')) AS TTUM\r\nFROM dfs_acq_recon_ttums WHERE FILEDATE=STR_TO_DATE('"
						+

						beanObj.getLocalDate() + "','%Y/%m/%d')  AND TTUM_TYPE='" + beanObj.getTypeOfTTUM() + "'";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			} else {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,418,' ')) AS TTUM\r\nFROM nfs_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE(?,'%Y-%m-%d')  AND TTUM_TYPE=?";
				logger.info("query " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData,
						new Object[] { beanObj.getLocalDate(), beanObj.getTypeOfTTUM() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}

		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getICDTTUM(UnMatchedTTUMBean beanObj) {List<Object> data = new ArrayList();
	try {
		String getData = null;
		System.out.println("getICDTTUM " + beanObj.getStSubCategory());
		if (beanObj.getStSubCategory().equalsIgnoreCase("ISSUER")) {
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				getData = "SELECT LPAD(AC_NO,15,'0')|| DR_CR ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM ICD_ISS_RECON_PROACTIVE  \r\n WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				getData = "SELECT LPAD(AC_NO,15,'0')|| DR_CR ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM ICD_ISS_RECON_DROP  WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
						+ "' AND AMOUNT <> 0";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			getData = "SELECT RPAD(AC_NO,15,' ')||DR_CR||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD(NARRATION,49,' ')  ||LPAD((ABS(AMOUNT)||'00'),18,'0')  AS TTUM\r\nFROM NFS_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
					+ "' AND AMOUNT <> 0";
			System.out.println("getData " + getData);
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		}
		if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
			getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM ICD_ACQ_PROACTIVE   WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY')";
			System.out.println("getData " + getData);
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		}
		if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
			getData = "SELECT LPAD(AC_NO,15,'0')||CASE WHEN DR_CR= 'CREDIT' THEN 'C' ELSE 'D' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM ICD_ACQ_RECON_DROP_TTUM  WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY')";
			System.out.println("getData " + getData);
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		} else {

		}
		getData = "SELECT RPAD(AC_NO,15,' ')||DR_CR||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD(NARRATION,49,' ')  ||LPAD((ABS(AMOUNT)||'00'),18,'0')  AS TTUM\r\nFROM NFS_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
				+

				beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
				+ "' AND AMOUNT <> 0";
		System.out.println("getData " + getData);
		List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
				new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("TTUM", rs.getString("TTUM"));
							beanList.add(table_Data);
						}
						return beanList;
					}
				});

		return DailyData;
	} catch (Exception e) {
		System.out.println("Exception in getDailyInterchangeTTUMData " + e);
		return null;
	}}

	public List<Object> getVISATTUM(UnMatchedTTUMBean beanObj) {
		List<Object> data = new  ArrayList<Object>();
		try {
			String getData = null;
			if (beanObj.getStSubCategory().equalsIgnoreCase("ISS")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,413,' ')) AS TTUM\r\nFROM visa_iss_drop_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,413,' ')) AS TTUM\r\nFROM visa_iss_late_presentment_ttum_data WHERE FILEDATE=STR_TO_DATE(' "
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,413,' ')) AS TTUM\r\nFROM visa_iss_proactive_ttum_data WHERE FILEDATE=STR_TO_DATE(' "
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {  },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CREDIT SURCHARGE")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(SUR, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,413,' ')) AS TTUM\r\nFROM visa_iss_cr_surch_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DEBIT SURCHARGE")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(SUR, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,413,' ')) AS TTUM\r\nFROM visa_iss_dr_surch_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,413,' ')) AS TTUM\r\nFROM visa_iss_org_wdl_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,413,' ')) AS TTUM\r\nFROM visa_iss_dom_refund_ttum_data where FILEDATE=STR_TO_DATE(' "
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {  },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND_REV")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,413,' ')) AS TTUM\r\nFROM visa_iss_dom_refund_rev_ttum WHERE  FILEDATE=STR_TO_DATE(' "
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {  },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND_INT")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,413,' ')) AS TTUM\r\nFROM visa_iss_int_refund_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {  },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,413,' ')) AS TTUM\r\nFROM visa_iss_int_refund_rev_ttum WHERE FILEDATE=STR_TO_DATE('"
						+

						beanObj.getLocalDate() + "','%Y/%m/%d')";
				logger.info("query " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {  },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;	
			}
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQ DOM ATM")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,417,' ')\r\n)  AS TTUM FROM visa_acq_dom_atm_loro_credit_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,417,' ')\r\n)  AS TTUM FROM visa_acq_dom_atm_loro_debit_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(TRUNC(REPLACE (TO_CHAR(AMOUNT,99999999.99),'.','')),18,'0') AS TTUM \r\nFROM VISA_DOM_POS_LATE_PRESENTMENT_DATA  WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(AMOUNT,18,'0') AS TTUM\r\nFROM VISA_DOM_POS_ORG_WDL_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(TRUNC(REPLACE (TO_CHAR(REPLACE(AMOUNT,',',''),99999999.99),'.','')),18,'0') AS TTUM\r\nFROM VISA_DOM_POS_PROACTIVE_TTUM_DATA   WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CREDIT SURCHARGE")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(SUR*100,18,'0') AS TTUM \r\nFROM VISA_DOM_POS_CR_SURCH_TTUM_DATA  WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DEBIT SURCHARGE")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(SUR*100,18,'0') AS TTUM \r\nFROM VISA_DOM_POS_DR_SURCH_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || DR_CR || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(AMOUNT,18,'0') AS TTUM \r\nFROM VISA_DOM_POS_ORG_WDL_TTUM_DATA  WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				getData = "SELECT RPAD(AC_NO,15,' ')||DR_CR||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD(NARRATION,49,' ')  ||LPAD((ABS(AMOUNT)||'00'),18,'0')  AS TTUM\r\nFROM NFS_ISS_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE(?,'DD-MM-YY') AND TTUM_TYPE = ? AND AMOUNT <> 0";
				logger.info("query " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;	
			}
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQ INT ATM")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,417,' ')\r\n)  AS TTUM FROM visa_acq_int_atm_loro_debit_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {  },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
					getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,CONCAT('INR',516500),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,417,' ')\r\n)  AS TTUM FROM visa_acq_int_atm_loro_credit_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP CHARGES")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(TRUNC(REPLACE (TO_CHAR(AMOUNT,99999999.99),'.','')),18,'0') as TTUM \r\nFROM VISA_INT_POS_DROP_CHARGES_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(TRUNC(REPLACE (TO_CHAR(AMOUNT,99999999.99),'.','')),18,'0')  AS TTUM\r\nFROM VISA_INT_POS_LATE_PRESENTMENT_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT CHARGES")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(TRUNC(REPLACE (TO_CHAR(AMOUNT,99999999.99),'.','')),18,'0') AS TTUM \r\nFROM VISA_INT_POS_LATE_PRESENTMENT_CHARGES_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(AMOUNT,18,'0')  AS TTUM\r\nFROM VISA_INT_POS_ORG_WDL_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') ||  LPAD(TRUNC(REPLACE (TO_CHAR(REPLACE(AMOUNT,',',''),99999999.99),'.','')),18,'0')  AS TTUM\r\nFROM VISA_INT_POS_PROACTIVE_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE CHARGES")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') ||  LPAD(TRUNC(REPLACE (TO_CHAR(REPLACE(AMOUNT,',',''),99999999.99),'.','')),18,'0') AS TTUM\r\nFROM VISA_INT_POS_PROACTIVE_CHARGES_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CREDIT SURCHARGE")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(SUR*100,18,'0')  AS TTUM\r\nFROM VISA_INT_POS_CR_SURCH_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DEBIT SURCHARGE")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(SUR*100,18,'0') AS TTUM \r\nFROM VISA_INT_POS_DR_SURCH_TTUM_DATA WHERE  TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				getData = "SELECT RPAD(AC_NO,15,' ')||DR_CR||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD(NARRATION,49,' ')  ||LPAD((ABS(AMOUNT)||'00'),18,'0')  AS TTUM\r\nFROM NFS_ISS_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE(?,'DD-MM-YY') AND TTUM_TYPE = ? AND AMOUNT <> 0";
				logger.info("query " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;	
			}
			if (beanObj.getStSubCategory().equalsIgnoreCase("ISS INT ATM")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(TRUNC(REPLACE (TO_CHAR(AMOUNT,99999999.99),'.','')),18,'0') AS TTUM\r\nFROM VISA_INT_ATM_DROP_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP CHARGES")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') ||  LPAD(TRUNC(REPLACE (TO_CHAR(AMOUNT,99999999.99),'.','')),18,'0') AS TTUM\r\nFROM VISA_INT_ATM_DROP_CHARGES_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(AMOUNT,18,'0') AS TTUM\r\nFROM VISA_INT_ATM_LATE_PRESENTMENT_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(AMOUNT,18,'0') AS TTUM\r\nFROM VISA_INT_ATM_ORG_WDL_TTUM_DATA  WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(TRUNC(REPLACE (TO_CHAR(REPLACE(AMOUNT,',',''),99999999.99),'.','')),18,'0') AS TTUM\r\nFROM VISA_INT_ATM_PROACTIVE_TTUM_DATA   WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE CHARGES")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(TRUNC(REPLACE (TO_CHAR(REPLACE(AMOUNT,',',''),99999999.99),'.','')),18,'0') AS TTUM\r\nFROM VISA_INT_ATM_PROACTIVE_CHARGES_TTUM_DATA  WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CREDIT SURCHARGE")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(SUR*100,18,'0') AS TTUM\r\nFROM VISA_INT_ATM_CR_SURCH_TTUM_DATA   WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DEBIT SURCHARGE")) {
					getData = "SELECT LPAD(AC_NO,15,'0') || SUBSTR(DR_CR,1,1) || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(NARRATION,50,' ') || LPAD(SUR*100,18,'0') AS TTUM\r\nFROM VISA_INT_ATM_DR_SURCH_TTUM_DATA  WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					System.out.println("getData " + getData);
					List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();

									while (rs.next()) {
										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("TTUM", rs.getString("TTUM"));
										beanList.add(table_Data);
									}
									return beanList;
								}
							});

					return DailyData;	
				}
				getData = "SELECT RPAD(AC_NO,15,' ')||DR_CR||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD(NARRATION,49,' ')  ||LPAD((ABS(AMOUNT)||'00'),18,'0')  AS TTUM\r\nFROM NFS_ISS_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE(?,'DD-MM-YY') AND TTUM_TYPE = ? AND AMOUNT <> 0";
				logger.info("query " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;	
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				getData = "SELECT RPAD(AC_NO,15,' ')||DR_CR||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD(NARRATION,49,' ')  ||LPAD((ABS(AMOUNT)||'00'),18,'0')  AS TTUM\r\nFROM NFS_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
						+ "' AND AMOUNT <> 0";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;	
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				getData = "SELECT RPAD(AC_NO,15,' ')||DR_CR||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD(NARRATION,49,' ')  ||LPAD((ABS(AMOUNT)||'00'),18,'0')  AS TTUM\r\nFROM NFS_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
						+ "' AND AMOUNT <> 0";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;	
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				getData = "SELECT RPAD(AC_NO,15,' ')||DR_CR||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD(NARRATION,49,' ')  ||LPAD((ABS(AMOUNT)||'00'),18,'0')  AS TTUM\r\nFROM NFS_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
						+ "' AND AMOUNT <> 0";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;	
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				getData = "SELECT RPAD(AC_NO,15,' ')||DR_CR||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD(NARRATION,49,' ')  ||LPAD((ABS(AMOUNT)||'00'),18,'0')  AS TTUM\r\nFROM NFS_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
						+ "' AND AMOUNT <> 0";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;	
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				getData = "SELECT RPAD(AC_NO,15,' ')||DR_CR||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD(NARRATION,49,' ')  ||LPAD((ABS(AMOUNT)||'00'),18,'0')  AS TTUM\r\nFROM NFS_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
						+ "' AND AMOUNT <> 0";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;	
			}
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				getData = "SELECT RPAD(AC_NO,15,' ')||DR_CR||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD(NARRATION,49,' ')  ||LPAD((ABS(AMOUNT)||'00'),18,'0')  AS TTUM\r\nFROM NFS_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
						+ "' AND AMOUNT <> 0";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;	
			}else {
				
				getData = "SELECT RPAD(AC_NO,15,' ')||DR_CR||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD(NARRATION,49,' ')  ||LPAD((ABS(AMOUNT)||'00'),18,'0')  AS TTUM\r\nFROM NFS_ISS_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE(?,'DD-MM-YY') AND TTUM_TYPE = ? AND AMOUNT <> 0";
				logger.info("query " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;	
			}
			
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getVISATTUMNB(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			getData = "SELECT RPAD('519402230043000',15,' ')||'D'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('NEPAL BHUTAN'||' '||AUTHORIZATION_CODE||' '||REPLACE(TO_DATE(PURCHASE_DATE,'MMDD'),'-'),49,' ')  ||LPAD( TO_CHAR(DESTINATION_AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM SETTLEMENT_VISA_DOM_ISS_ATM_VISA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY') AND DESTINATION_AMOUNT <> 0 \r\n"
					+ "and dcrs_remarks='VISA-DOM-ISS-ATM-CROSS-MATCHED-2' and MERCHANT_COUNTRY_CODE IN('NP','BH','BT')\r\n"
					+ "UNION ALL\r\n"
					+ "SELECT RPAD('519402230042000',15,' ')||'C'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('NEPAL BHUTAN'||' '||AUTHORIZATION_CODE||' '||REPLACE(TO_DATE(PURCHASE_DATE,'MMDD'),'-'),49,' ')  ||LPAD( TO_CHAR(DESTINATION_AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\n"
					+ "FROM SETTLEMENT_VISA_DOM_ISS_ATM_VISA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY') AND DESTINATION_AMOUNT <> 0 \r\n"
					+ "and dcrs_remarks='VISA-DOM-ISS-ATM-CROSS-MATCHED-2' and MERCHANT_COUNTRY_CODE IN('NP','BH','BT')\r\n"
					+ "UNION ALL\r\n"
					+ "SELECT RPAD('519402230042000',15,' ')||'D'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('NEPAL BHUTAN'||' '||AUTHORIZATION_CODE||' '||REPLACE(TO_DATE(PURCHASE_DATE,'MMDD'),'-'),49,' ')  ||LPAD( TO_CHAR(DESTINATION_AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\n"
					+ "FROM SETTLEMENT_VISA_INT_ISS_ATM_VISA WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('"
					+ beanObj.getLocalDate() + "' ,'DD-MM-YY') \r\n"
					+ "and dcrs_remarks='VISA-INT-ISS-ATM-CROSS-MATCHED-2' and MERCHANT_COUNTRY_CODE IN('NP','BH','BT')\r\n"
					+ "UNION ALL\r\n"
					+ "SELECT RPAD('519402230043000',15,' ')||'C'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('NEPAL BHUTAN'||' '||AUTHORIZATION_CODE||' '||REPLACE(TO_DATE(PURCHASE_DATE,'MMDD'),'-'),49,' ')  ||LPAD( TO_CHAR(DESTINATION_AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\n"
					+ "FROM SETTLEMENT_VISA_INT_ISS_ATM_VISA WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('"
					+ beanObj.getLocalDate() + "' ,'DD-MM-YY') \r\n"
					+ "and dcrs_remarks='VISA-INT-ISS-ATM-CROSS-MATCHED-2' and MERCHANT_COUNTRY_CODE IN('NP','BH','BT')\r\n";
			logger.info("query " + getData);
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;	
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getVISATTUMCTCSETTLEMENTTTUM(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			getData = "SELECT RPAD('519402230234000',15,' ')||'D'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('NFS_C2C_ACQ_ISS'||' '||TRACE_NO||' '||REPLACE(TO_DATE(TRAN_DATE,'DD-MM-YY'),'-'),49,' ')  ||LPAD( TO_CHAR(REPLACE(AMOUNT,',')*100,'FM000000000090'),18,'0')  AS TTUM \r\n    FROM SETTLEMENT_NFS_ACQ_C2C_CBS  OS1 \r\n    where  TO_DATE(OS1.FILEDATE,'DD-MM-YY')  = TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY') \r\n"
					+ "    AND  EXISTS (  SELECT 1 FROM SETTLEMENT_NFS_ISS_C2C_CBS OS2 \r\n"
					+ "    where  TO_DATE(OS2.FILEDATE,'DD-MM-YY') =  TO_DATE('" + beanObj.getLocalDate()
					+ "' ,'DD-MM-YY') \r\n"
					+ "    AND  OS1.AMOUNT= OS2.AMOUNT   AND  OS1.TRACE_NO= OS2.TRACE_NO) and os1.dcrs_remarks='NFS-ACQ-C2C-CBS-MATCHED-2'\r\n"
					+ "    UNION ALL\r\n"
					+ "    SELECT RPAD('519408250431000',15,' ')||'C'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('NFS_C2C_ACQ_ISS'||' '||TRACE_NO||' '||REPLACE(TO_DATE(TRAN_DATE,'DD-MM-YY'),'-'),49,' ')  ||LPAD( TO_CHAR(REPLACE(AMOUNT,',')*100,'FM000000000090'),18,'0')  AS TTUM \r\n"
					+ "    FROM SETTLEMENT_NFS_ACQ_C2C_CBS  OS1 \r\n"
					+ "    where  TO_DATE(OS1.FILEDATE,'DD-MM-YY')  = TO_DATE('" + beanObj.getLocalDate()
					+ "','DD-MM-YY') \r\n" + "    AND  EXISTS (  SELECT 1 FROM SETTLEMENT_NFS_ISS_C2C_CBS OS2 \r\n"
					+ "    where  TO_DATE(OS2.FILEDATE,'DD-MM-YY') =  TO_DATE('" + beanObj.getLocalDate()
					+ "' ,'DD-MM-YY') \r\n"
					+ "    AND  OS1.AMOUNT= OS2.AMOUNT   AND  OS1.TRACE_NO= OS2.TRACE_NO) and os1.dcrs_remarks='NFS-ACQ-C2C-CBS-MATCHED-2';";
			logger.info("query " + getData);
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;	
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getVISATTUMNFSTOVISACROSSROUNTING(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			getData = "SELECT '519408250130000'||'D'||TO_CHAR(TO_DATE('" + beanObj.getLocalDate()
					+ "' ,'DD-MM-YY'),'DDMMYYYY') \r\n"
					+ "|| RPAD(' VISA_ACQVISA_TO_NFSCREDIT DT '|| TO_CHAR(TO_DATE('22-10-24' ,'DD-MM-YY'),'DDMMYYYY'),50,' ') \r\n"
					+ "||LPAD((TO_NUMBER(SUM(REPLACE(AMOUNT,',',''))) ||'00'),18,'0')  AS TTUM\r\n"
					+ "FROM SETTLEMENT_VISA_ACQ_ATM_CBS_NFS_ROUTING \r\n"
					+ "WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('" + beanObj.getLocalDate()
					+ "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-ACQ-ATM-MATCHED-2-ROUTING'\r\n" + "\r\n" + "UNION ALL\r\n"
					+ "\r\n" + "SELECT '519408250122000'||'C'||TO_CHAR(TO_DATE('" + beanObj.getLocalDate()
					+ "' ,'DD-MM-YY'),'DDMMYYYY') \r\n" + "|| RPAD(' VISA_ACQVISA_TO_NFSCREDIT DT '|| TO_CHAR(TO_DATE('"
					+ beanObj.getLocalDate() + "' ,'DD-MM-YY'),'DDMMYYYY'),50,' ') \r\n"
					+ "||LPAD((TO_NUMBER(SUM(REPLACE(AMOUNT,',',''))) ||'00'),18,'0')  AS TTUM\r\n"
					+ "FROM SETTLEMENT_VISA_ACQ_ATM_CBS_NFS_ROUTING \r\n"
					+ "WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('" + beanObj.getLocalDate()
					+ "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-ACQ-ATM-MATCHED-2-ROUTING'";
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;	
		
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getVISATTUMATMPOSCROSSROUNTING(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			if (beanObj.getTTUMCategory().equalsIgnoreCase("ATM_TTUM")) {
				getData = "SELECT RPAD('519402230042000',15,' ')||'D'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('ATM CROSS ROUTING'||' '||AUTHORIZATION_CODE||' '||REPLACE(TO_DATE(PURCHASE_DATE,'MMDD'),'-'),49,' ')  ||LPAD( TO_CHAR(DESTINATION_AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM SETTLEMENT_VISA_INT_ISS_ATM_VISA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY') AND DESTINATION_AMOUNT <> 0 \r\n"
						+ "and dcrs_remarks='VISA-INT-ISS-ATM-CROSS-MATCHED-2' and MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')\r\n"
						+ "UNION ALL\r\n"
						+ "SELECT RPAD('519402230043000',15,' ')||'C'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('ATM CROSS ROUTING'||' '||AUTHORIZATION_CODE||' '||REPLACE(TO_DATE(PURCHASE_DATE,'MMDD'),'-'),49,' ')  ||LPAD( TO_CHAR(DESTINATION_AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\n"
						+ "FROM SETTLEMENT_VISA_INT_ISS_ATM_VISA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY') AND DESTINATION_AMOUNT <> 0 \r\n"
						+ "and dcrs_remarks='VISA-INT-ISS-ATM-CROSS-MATCHED-2' and MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')\r\n"
						+ "UNION ALL\r\n"
						+ "SELECT RPAD('519402230043000',15,' ')||'D'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('ATM CROSS ROUTING'||' '||AUTHORIZATION_CODE||' '||REPLACE(TO_DATE(PURCHASE_DATE,'MMDD'),'-'),49,' ')  ||LPAD( TO_CHAR(DESTINATION_AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\n"
						+ "FROM SETTLEMENT_VISA_DOM_ISS_ATM_VISA WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "' ,'DD-MM-YY') \r\n"
						+ "and dcrs_remarks='VISA-DOM-ISS-ATM-CROSS-MATCHED-2' and MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')\r\n"
						+ "UNION ALL\r\n"
						+ "SELECT RPAD('519402230042000',15,' ')||'C'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('ATM CROSS ROUTING'||' '||AUTHORIZATION_CODE||' '||REPLACE(TO_DATE(PURCHASE_DATE,'MMDD'),'-'),49,' ')  ||LPAD( TO_CHAR(DESTINATION_AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\n"
						+ "FROM SETTLEMENT_VISA_DOM_ISS_ATM_VISA WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "' ,'DD-MM-YY') \r\n"
						+ "and dcrs_remarks='VISA-DOM-ISS-ATM-CROSS-MATCHED-2' and MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')";
			} else {
				getData = "SELECT RPAD('519402230044000',15,' ')||'D'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('POS CROSS ROUTING'||' '||AUTHORIZATION_CODE||' '||REPLACE(TO_DATE(PURCHASE_DATE,'MMDD'),'-'),49,' ')  ||LPAD( TO_CHAR(DESTINATION_AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\nFROM SETTLEMENT_VISA_INT_ISS_POS_VISA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY') AND DESTINATION_AMOUNT <> 0 \r\n"
						+ "and dcrs_remarks='VISA-INT-ISS-POS-CROSS-MATCHED-2' and MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')\r\n"
						+ "UNION ALL\r\n"
						+ "SELECT RPAD('519402230045000',15,' ')||'C'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('POS CROSS ROUTING'||' '||AUTHORIZATION_CODE||' '||REPLACE(TO_DATE(PURCHASE_DATE,'MMDD'),'-'),49,' ')  ||LPAD( TO_CHAR(DESTINATION_AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\n"
						+ "FROM SETTLEMENT_VISA_INT_ISS_POS_VISA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY') AND DESTINATION_AMOUNT <> 0 \r\n"
						+ "and dcrs_remarks='VISA-INT-ISS-POS-CROSS-MATCHED-2' and MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')\r\n"
						+ "UNION ALL\r\n"
						+ "SELECT RPAD('519402230045000',15,' ')||'D'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('POS CROSS ROUTING'||' '||AUTHORIZATION_CODE||' '||REPLACE(TO_DATE(PURCHASE_DATE,'MMDD'),'-'),49,' ')  ||LPAD( TO_CHAR(DESTINATION_AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\n"
						+ "FROM SETTLEMENT_VISA_DOM_ISS_POS_VISA WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "' ,'DD-MM-YY') \r\n"
						+ "and dcrs_remarks='VISA-DOM-ISS-POS-CROSS-MATCHED-2' and MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')\r\n"
						+ "UNION ALL\r\n"
						+ "SELECT RPAD('519402230044000',15,' ')||'C'||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD('POS CROSS ROUTING'||' '||AUTHORIZATION_CODE||' '||REPLACE(TO_DATE(PURCHASE_DATE,'MMDD'),'-'),49,' ')  ||LPAD( TO_CHAR(DESTINATION_AMOUNT*100,'FM000000000090'),18,'0')  AS TTUM\r\n"
						+ "FROM SETTLEMENT_VISA_DOM_ISS_POS_VISA WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "' ,'DD-MM-YY') \r\n"
						+ "and dcrs_remarks='VISA-DOM-ISS-POS-CROSS-MATCHED-2' and MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')";
			}
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;	
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getVISATTUMNFSRUPAY(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			getData = "SELECT LPAD(GL_ACCOUNT,15,'0')||CASE WHEN DR_CR='DEBIT'  THEN 'D' WHEN DR_CR='CREDIT'  then 'C'  ELSE '' END ||TO_CHAR(TO_DATE(SYSDATE,'DD-MM-RRRR') ,'DDMMYYYY')|| RPAD(NARRATION,50,' ')||\r\nLPAD(CASE WHEN DR_CR='DEBIT'  THEN TO_CHAR(replace(AMOUNT,',')*100,'FM000000000090') WHEN DR_CR='CREDIT' then TO_CHAR(replace(AMOUNT,',')*100,'FM000000000090')  ELSE '' END  ,18,'0')  AS TTUM\r\nFROM NFSISS_RUPAYINT_ROUT_TTUM \r\nWHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('"
					+

					beanObj.getLocalDate() + "' ,'DD-MM-YY')";
			logger.info("query " + getData);
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;	
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getMASTERCARDTTUM(MastercardUploadBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			if (beanObj.getFileType().equalsIgnoreCase("DOMESTIC")) {
				getData = "SELECT CONCAT(RPAD(GL_CODE,16,' ') ,CONCAT('INR',516500),'  ',CASE when DEBIT>0 THEN 'D' WHEN CREDIT>0 THEN 'C' ELSE '' END\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DEBIT>0 THEN DEBIT ELSE CREDIT END, 2),',',''),18,'0')\r\n,RPAD(PARTICULARS,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM mastercard_settlement_ttum WHERE FILEDATE=STR_TO_DATE('"
						+

						beanObj.getFileDate() + "','%Y/%m/%d')";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {  },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;	
			}else {
				getData = "SELECT CONCAT(RPAD(GL_CODE,16,' ') ,CONCAT('INR',516500),'  ',CASE when DEBIT>0 THEN 'D' WHEN CREDIT>0 THEN 'C' ELSE '' END\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DEBIT>0 THEN DEBIT ELSE CREDIT END, 2),',',''),18,'0')\r\n,RPAD(PARTICULARS,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM mastercard_int_settlement_ttum WHERE FILEDATE=STR_TO_DATE('"
						+

						beanObj.getFileDate() + "','%Y/%m/%d')";
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;	
			}
			
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getCTCTTUM(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				getData = "SELECT RPAD(AC_NO,15,' ')||DR_CR||TO_CHAR(SYSDATE,'DDMMYYYY')||' '|| RPAD(NARRATION,49,' ')  ||LPAD((ABS(AMOUNT)||'00'),18,'0')  AS TTUM\r\nFROM NFS_C2C_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
						+ "' AND AMOUNT <> 0";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}else {
				getData = "SELECT CONCAT(RPAD(AC_NO,16,' ') ,'INR','516500','  ',DR_CR,LPAD(REPLACE(FORMAT(AMOUNT, 2),',',''),17,'0'),\r\nRPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(UID,418,' ')) AS TTUM\r\nFROM c2c_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE(?,'%Y-%m-%d') AND TTUM_TYPE = ?";
				System.out.println("getData " + getData);
				List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getLocalDate().replaceAll("/","-") , beanObj.getTypeOfTTUM() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("TTUM", rs.getString("TTUM"));
									beanList.add(table_Data);
								}
								return beanList;
							}
						});

				return DailyData;
			}
			
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getDailyNFSTTUMDataSETTL(NFSSettlementBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			if (beanObj.getAdjCategory().contains("ICD")) {
				getData = "SELECT TTUM FROM ICD_SETTLEMENT_DAILY_TTUM WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY') ";
			} else if (beanObj.getAdjCategory().contains("DFS")) {
				getData = "SELECT CONCAT(RPAD(GL_ACCOUNT,16,' ') ,INR,SIX_DIG_GL,'  ',D_C,LPAD(REPLACE(FORMAT(CASE WHEN D_C='D' THEN DEBIT ELSE CREDIT END, 2),',',''),17,'0'),\r\nRPAD(NARRATION,15,' '),REMARKS,LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(PARTICULARS,417,' ')) AS TTUM\r\nFROM dfs_settlement_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')";
			} else if (beanObj.getAdjCategory().contains("JCB")) {
				getData = "SELECT CONCAT(RPAD(GL_ACCOUNT,16,' ') ,INR,SIX_DIG_GL,'  ',D_C,LPAD(REPLACE(FORMAT(CASE WHEN D_C='D' THEN DEBIT ELSE CREDIT END, 2),',',''),17,'0'),\r\nRPAD(NARRATION,15,' '),REMARKS,LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(PARTICULARS,417,' ')) AS TTUM\r\nFROM jcb_settlement_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') ";
			} else {
				getData = "SELECT CONCAT(RPAD(GL_ACCOUNT,16,' ') ,INR,SIX_DIG_GL,'  ',D_C,LPAD(REPLACE(FORMAT(CASE WHEN D_C='D' THEN DEBIT ELSE CREDIT END, 2),',',''),17,'0'),\r\nRPAD(NARRATION,15,' '),REMARKS,LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(PARTICULARS,417,' ')) AS TTUM\r\nFROM nfs_settlement_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')  AND CYCLE='"
						+

						beanObj.getCycle() + "'";
			}
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] {beanObj.getDatepicker()  },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getREEFUNDTTUMTEXT(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			if (beanObj.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
				if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
					getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,16,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,20,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,413,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') AND TYPE='RUPAY_TTUM' order by DR_CR";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("RUPAY_OFFLINE")) {
					getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,16,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,20,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,413,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') AND TYPE='RUPAY_OFFLINE' order by DR_CR";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
					getData = " SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_adjustment_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') and TTUM_TYPE='Rupay_CHB' ";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
					getData = " SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_adjustment_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') and TTUM_TYPE='Rupay_CHBD' ";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Partial Chargeback Acceptance")) {
					getData = " SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_adjustment_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') and TTUM_TYPE='Rupay_PCHB' ";
				}
			} else if (beanObj.getCategory().equalsIgnoreCase("RUPAY_INT_MFC")) {
				if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
					getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,16,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,20,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,413,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') AND TYPE='RUPAY_INT_MFC_TTUM' order by DR_CR";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("RUPAY_OFFLINE")) {
					getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,16,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,20,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,413,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') AND TYPE='RUPAY_OFFLINE' order by DR_CR";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
					getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') and TTUM_TYPE='Rupay_CHB'\r\n      ";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
					getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') and TTUM_TYPE='Rupay_CHBD' ";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Partial Chargeback Acceptance")) {
					getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') and TTUM_TYPE='Rupay_PCHB' ";
				}
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
				getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,16,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,20,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,413,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') AND TYPE='RUPAY_INT_TTUM' order by DR_CR";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("RUPAY_OFFLINE")) {
				getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,16,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,20,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,413,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') AND TYPE='RUPAY_OFFLINE' order by DR_CR";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
				getData = " SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_adjustment_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') and TTUM_TYPE='Rupay_CHB'\r\n      ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
				getData = " SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_adjustment_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') and TTUM_TYPE='Rupay_CHBD' ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Partial Chargeback Acceptance")) {
				getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_adjustment_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') and TTUM_TYPE='Rupay_PCHB' ";
			}
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getREEFUNDTTUMTEXTVISA(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
				getData = "SELECT LPAD(ACCOUNT_NO,15,'0') ||CASE WHEN DR_CR='D'  THEN 'D' ELSE 'C' END || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(SUBSTR(NARRATION,1,LENGTH(NARRATION) ),50,' ') || \r\n    CASE WHEN DR_CR='D' THEN LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') ELSE LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') END AS TTUM\r\n    FROM VISA_REFUND_TTUM\r\n    WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY') AND TTUM_TYPE='VISA_DOM_REFUND' ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
				getData = "SELECT LPAD(ACCOUNT_NO,15,'0') ||CASE WHEN DR_CR='C' THEN 'C' ELSE 'D' END || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(SUBSTR(NARRATION,1,LENGTH(NARRATION) ),50,' ') || \r\n    CASE WHEN DR_CR='C' THEN LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') ELSE LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') END AS TTUM\r\n    FROM RUPAY_ADJUSTMENT_TTUM\r\n    WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY') and TTUM_TYPE='Rupay_CHB'\r\n      ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
				getData = "SELECT LPAD(ACCOUNT_NO,15,'0') ||CASE WHEN DR_CR='C' THEN 'C' ELSE 'D' END || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(SUBSTR(NARRATION,1,LENGTH(NARRATION) ),50,' ') || \r\n    CASE WHEN DR_CR='C' THEN LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') ELSE LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') END AS TTUM\r\n    FROM RUPAY_ADJUSTMENT_TTUM  WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')and TTUM_TYPE='Rupay_CHBD' ";
			}
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getREEFUNDTTUMTEXTVISAINT(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
				getData = "SELECT LPAD(ACCOUNT_NO,15,'0') ||CASE WHEN DR_CR='D'  THEN 'D' ELSE 'C' END || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(SUBSTR(NARRATION,1,LENGTH(NARRATION) ),50,' ') || \r\n    CASE WHEN DR_CR='D' THEN LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') ELSE LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') END AS TTUM\r\n    FROM VISA_REFUND_TTUM    WHERE  to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')  AND TTUM_TYPE='VISA_INT_REFUND'";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
				getData = "SELECT LPAD(ACCOUNT_NO,15,'0') ||CASE WHEN DR_CR='C' THEN 'C' ELSE 'D' END || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(SUBSTR(NARRATION,1,LENGTH(NARRATION) ),50,' ') || \r\n    CASE WHEN DR_CR='C' THEN LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') ELSE LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') END AS TTUM\r\n    FROM RUPAY_ADJUSTMENT_TTUM\r\n    WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY') and TTUM_TYPE='Rupay_CHB'\r\n      ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
				getData = "SELECT LPAD(ACCOUNT_NO,15,'0') ||CASE WHEN DR_CR='C' THEN 'C' ELSE 'D' END || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(SUBSTR(NARRATION,1,LENGTH(NARRATION) ),50,' ') || \r\n    CASE WHEN DR_CR='C' THEN LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') ELSE LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') END AS TTUM\r\n    FROM RUPAY_ADJUSTMENT_TTUM  WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')and TTUM_TYPE='Rupay_CHBD' ";
			}
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public List<Object> getREEFUNDTTUMTEXTISS(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData = null;
			if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD")) {
				if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
					getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,16,' ') ,CONCAT('INR',516500),'  ',CASE when DR_CR= 'DEBIT' THEN 'D' WHEN  DR_CR= 'CREDIT' THEN 'C' ELSE '' END\r\n  ,LPAD(REPLACE(FORMAT(CASE when DR_CR= 'DEBIT' THEN AMOUNT WHEN  DR_CR= 'CREDIT' THEN AMOUNT ELSE '' END, 2),',',''),18,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM mc_dom_refund_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') and  REF_TYPE='DOMESTIC'";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
					getData = "SELECT LPAD(ACCOUNT_NO,15,'0') ||CASE WHEN DR_CR='C' THEN 'C' ELSE 'D' END || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(SUBSTR(NARRATION,1,LENGTH(NARRATION) ),50,' ') || \r\n    CASE WHEN DR_CR='C' THEN LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') ELSE LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') END AS TTUM\r\n    FROM rupay_adjustment_ttum\r\n    WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY') and TTUM_TYPE='QSPARC_CHB' \r\n      ";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
					getData = "SELECT LPAD(ACCOUNT_NO,15,'0') ||CASE WHEN DR_CR='C' THEN 'C' ELSE 'D' END || TO_CHAR(SYSDATE ,'DDMMYYYY')||RPAD(SUBSTR(NARRATION,1,LENGTH(NARRATION) ),50,' ') || \r\n    CASE WHEN DR_CR='C' THEN LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') ELSE LPAD( TO_CHAR(AMOUNT*100,'FM000000000090'),18,'0') END AS TTUM\r\n    FROM rupay_adjustment_ttum  WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')and TTUM_TYPE='QSPARC_CHBD'";
				}
			} else if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD_INT")) {
				if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
					getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,16,' ') ,CONCAT('INR',516500),'  ',CASE when DR_CR= 'DEBIT' THEN 'D' WHEN  DR_CR= 'CREDIT' THEN 'C' ELSE '' END\r\n  ,LPAD(REPLACE(FORMAT(CASE when DR_CR= 'DEBIT' THEN AMOUNT WHEN  DR_CR= 'CREDIT' THEN AMOUNT ELSE '' END, 2),',',''),18,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,417,' ')\r\n)  AS TTUM FROM mc_dom_refund_ttum WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') and REF_TYPE='INTERNATIONAL'";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
					getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')  and TTUM_TYPE='QSPARC_CHB' \r\n      ";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
					getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')  and TTUM_TYPE='QSPARC_CHBD'";
				}
			} else if (beanObj.getCategory().equalsIgnoreCase("RUPAY_INT")) {
				if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND"))
					getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,16,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,20,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,413,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') AND TYPE=''  AND TYPE='RUPAY_INT_TTUM' order by DR_CR";
			} else if (beanObj.getCategory().equalsIgnoreCase("RUPAY_INT_MFC")) {
				if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND"))
					getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,16,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,20,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,413,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') AND TYPE=''  AND TYPE='RUPAY_INT_MFC_TTUM' order by DR_CR";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
				getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,16,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),'  ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,20,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,413,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d') AND TYPE=''  AND TYPE='QSPARC_TTUM' order by DR_CR";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
				getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')  and TTUM_TYPE='QSPARC_CHB' order by DR_CR      ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
				getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')   and TTUM_TYPE='QSPARC_CHBD' order by DR_CR";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Partial Chargeback Acceptance")) {
				getData = "SELECT CONCAT(RPAD(ACCOUNT_NO,17,' ') ,CONCAT('INR',SUBSTR(ACCOUNT_NO,1,6)),' ',DR_CR\r\n  ,LPAD(REPLACE(FORMAT(CASE WHEN DR_CR='C' THEN AMOUNT ELSE AMOUNT END, 2),',',''),17,'0')\r\n,RPAD(NARRATION,30,' '),LPAD(DATE_FORMAT(FILEDATE, '%d-%m-%Y'),113,' '),LPAD(REMARKS,415,' ')\r\n)  AS TTUM FROM rupay_refund_report WHERE FILEDATE=STR_TO_DATE(?,'%Y/%m/%d')   and TTUM_TYPE='QSPARC_PCHB' order by DR_CR";
			}
			List<Object> DailyData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("TTUM", rs.getString("TTUM"));
								beanList.add(table_Data);
							}
							return beanList;
						}
					});

			return DailyData;
		} catch (Exception e) {
			System.out.println("Exception in getDailyInterchangeTTUMData " + e);
			return null;
		}
	}

	public String TTUMRollback(String filedate, String adjType) throws ParseException, Exception {
		try {
			Map<String, Object> inParams = new HashMap<>();
			int Count = 0;
			if (adjType.equalsIgnoreCase("PENALTY")) {
				String rollbackcount = "SELECT COUNT(*) as DATA_COUNT  FROM NFS_ADJUSTMENT_RAWDATA WHERE TO_DATE(FILEDATE,'DD-MM-YY') =TO_DATE('"
						+ filedate + "' ,'DD-MM-YY') AND \r\n"
						+ "    TO_DATE(CONVTODATE(ADJSETTLEMENTDATE),'DD-MM-YY')=TO_DATE('" + filedate
						+ "' ,'DD-MM-YY') AND ISR='UOB'   AND UPPER(adjtype)  ='%" + adjType + "%'";
				Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[0], Integer.class))
						.intValue();
			} else {
				String rollbackcount = "SELECT COUNT(*)  FROM NFS_ADJUSTMENT_TTUM WHERE to_date(FILEDATE,'dd-mm-yy')=to_date(TO_DATE(? ,'DD-MM-YY')) AND  ADJTYPE= ?";
				Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[] { filedate, adjType },
						Integer.class)).intValue();
			}
			System.out.println("rollbackcount  " + Count);
			if (Count == 0)
				return "ROLLBACK ALREADY PROCESSED";
			if (adjType.equalsIgnoreCase("PENALTY")) {
				String str = "DELETE FROM NFS_ADJUSTMENT_TTUM WHERE to_date(FILEDATE,'dd-mm-yy')=to_date(TO_DATE('"
						+ filedate + "' ,'DD-MM-YY'))  AND ADJTYPE LIKE '%" + adjType + "%'";
				getJdbcTemplate().execute(str);
				return "SUCCESS";
			}
			String deleteRollback = "DELETE FROM NFS_ADJUSTMENT_TTUM WHERE to_date(FILEDATE,'dd-mm-yy')=to_date(TO_DATE('"
					+ filedate + "' ,'DD-MM-YY'))  AND ADJTYPE = '" + adjType + "'";
			getJdbcTemplate().execute(deleteRollback);
			return "SUCCESS";
		} catch (Exception e) {
			return e.toString();
		}
	}

	public String TTUMRollbackICD(String filedate, String adjType) throws ParseException, Exception {
		try {
			Map<String, Object> inParams = new HashMap<>();
			int Count = 0;
			if (adjType.equalsIgnoreCase("PENALTY")) {
				String rollbackcount = "SELECT COUNT(*) as DATA_COUNT  FROM ICD_ADJUSTMENT_RAWDATA WHERE TO_DATE(FILEDATE,'DD-MM-YY') =TO_DATE('"
						+ filedate + "' ,'DD-MM-YY') AND \r\n"
						+ "    TO_DATE(CONVTODATE(ADJSETTLEMENTDATE),'DD-MM-YY')=TO_DATE('" + filedate
						+ "' ,'DD-MM-YY') AND ISR='UOB'   AND UPPER(adjtype)  ='%" + adjType + "%'";
				Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[0], Integer.class))
						.intValue();
			} else {
				String rollbackcount = "SELECT COUNT(*)  FROM ICD_ADJUSTMENT_TTUM WHERE to_date(FILEDATE,'dd-mm-yy')=to_date(TO_DATE(? ,'DD-MM-YY')) AND  ADJTYPE= ?";
				Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[] { filedate, adjType },
						Integer.class)).intValue();
			}
			System.out.println("rollbackcount  " + Count);
			if (Count == 0)
				return "ROLLBACK ALREADY PROCESSED";
			if (adjType.equalsIgnoreCase("PENALTY")) {
				String str = "DELETE FROM ICD_ADJUSTMENT_TTUM WHERE to_date(FILEDATE,'dd-mm-yy')=to_date(TO_DATE('"
						+ filedate + "' ,'DD-MM-YY'))  AND ADJTYPE LIKE '%" + adjType + "%'";
				getJdbcTemplate().execute(str);
				return "SUCCESS";
			}
			String deleteRollback = "DELETE FROM ICD_ADJUSTMENT_TTUM WHERE to_date(FILEDATE,'dd-mm-yy')=to_date(TO_DATE('"
					+ filedate + "' ,'DD-MM-YY'))  AND ADJTYPE = '" + adjType + "'";
			getJdbcTemplate().execute(deleteRollback);
			return "SUCCESS";
		} catch (Exception e) {
			return e.toString();
		}
	}

	public String TTUMRollbackSETTL(String filedate) throws ParseException, Exception {
		try {
			Map<String, Object> inParams = new HashMap<>();
			int Count = 0;
			String rollbackcount = "SELECT COUNT(*)  FROM NFS_SETTLEMENT_DAILY_TTUM WHERE to_date(FILEDATE,'dd-mm-yy')=to_date(TO_DATE(? ,'DD-MM-YY'))";
			Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[] { filedate },
					Integer.class)).intValue();
			System.out.println("rollbackcount  " + Count);
			if (Count == 0)
				return "ROLLBACK ALREADY PROCESSED";
			String deleteRollback = "DELETE FROM NFS_SETTLEMENT_DAILY_TTUM WHERE to_date(FILEDATE,'dd-mm-yy')=to_date(TO_DATE('"
					+ filedate + "' ,'DD-MM-YY'))";
			getJdbcTemplate().execute(deleteRollback);
			return "SUCCESS";
		} catch (Exception e) {
			return e.toString();
		}
	}

	public String TTUMRollbackSETTLICD(String filedate) throws ParseException, Exception {
		try {
			Map<String, Object> inParams = new HashMap<>();
			int Count = 0;
			String rollbackcount = "SELECT COUNT(*)  FROM ICD_SETTLEMENT_DAILY_TTUM WHERE to_date(FILEDATE,'dd-mm-yy')=to_date(TO_DATE(? ,'DD-MM-YY'))";
			Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[] { filedate },
					Integer.class)).intValue();
			System.out.println("rollbackcount  " + Count);
			if (Count == 0)
				return "ROLLBACK ALREADY PROCESSED";
			String deleteRollback = "DELETE FROM ICD_SETTLEMENT_DAILY_TTUM WHERE to_date(FILEDATE,'dd-mm-yy')=to_date(TO_DATE('"
					+ filedate + "' ,'DD-MM-YY'))";
			getJdbcTemplate().execute(deleteRollback);
			return "SUCCESS";
		} catch (Exception e) {
			return e.toString();
		}
	}

	public boolean TTUMRollbackReportSETTL(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			TTUMRollbackReportSETTL rollBackexe = new TTUMRollbackReportSETTL( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getDatepicker());
			inParams.put("V_CYCLE", Integer.valueOf(beanObj.getCycle()));
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class TTUMRollbackReportSETTL extends StoredProcedure {
		private static final String insert_proc = "NFS_DAILY_SETTLEMENT_PNB_ROLLBACK";

		public TTUMRollbackReportSETTL(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_CYCLE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean TTUMRollbackReportSETTLJCB(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			TTUMRollbackReportSETTLJCB rollBackexe = new TTUMRollbackReportSETTLJCB( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class TTUMRollbackReportSETTLJCB extends StoredProcedure {
		private static final String insert_proc = "JCB_DAILY_SETTLEMENT_PNB_ROLLBACK";

		public TTUMRollbackReportSETTLJCB(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackRupaySettl(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackRupaySettl rollBackexe = new rollBackRupaySettl( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("v_cycle", String.valueOf(beanObj.getCycle()));
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackRupaySettl extends StoredProcedure {
		private static final String insert_proc = "RUPAY_POS_SETTLEMENT_ROLLBACK";

		public rollBackRupaySettl(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));

			declareParameter(new SqlParameter("V_CYCLE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	
	public boolean rollBackRupaySettl2(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackRupaySettl2 rollBackexe = new rollBackRupaySettl2( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());

			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackRupaySettl2 extends StoredProcedure {
		private static final String insert_proc = "RUPAY_POS_SETTLEMENT_ROLLBACK_TEST";

		public rollBackRupaySettl2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));

		
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackRupaySettlRRB(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackRupaySettlRRB rollBackexe = new rollBackRupaySettlRRB( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("v_cycle", String.valueOf(beanObj.getCycle()));
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS")) {
				return true;
			}else {
			return false;
			}
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackRupaySettlRRB extends StoredProcedure {
		private static final String insert_proc = "RUPAY_POS_RRB_SETTLEMENT_ROLLBACK";

		public rollBackRupaySettlRRB(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));

			declareParameter(new SqlParameter("v_cycle",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackRupaySettlQsparc(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackRupaySettlQsparc rollBackexe = new rollBackRupaySettlQsparc( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("ENTERED_CYCLE", String.valueOf(beanObj.getCycle()));
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackRupaySettlQsparc extends StoredProcedure {
		private static final String insert_proc = "QSPARC_POS_SETTLEMENT_PNB_ROLLBACK";

		public rollBackRupaySettlQsparc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("ENTERED_CYCLE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackRupaySettlQsparcINT(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackRupaySettlQsparcINT rollBackexe = new rollBackRupaySettlQsparcINT( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackRupaySettlQsparcINT extends StoredProcedure {
		private static final String insert_proc = "QSPARC_INT_POS_SETTLEMENT_ROLLBACK";

		public rollBackRupaySettlQsparcINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackRupaySettlINT(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackRupaySettlINT rollBackexe = new rollBackRupaySettlINT( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackRupaySettlINT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_POS_SETTLEMENT_PNB_ROLLBACK";

		public rollBackRupaySettlINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public String TTUMRollbackReportSETTLICD(String filedate) throws ParseException, Exception {
		try {
			Map<String, Object> inParams = new HashMap<>();
			int Count = 0;
			String rollbackcount = "SELECT COUNT(*) FROM ICD_SETTLEMENT_REPORT WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY') ";
			Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[] { filedate },
					Integer.class)).intValue();
			System.out.println("rollbackcount  " + Count);
			if (Count == 0)
				return "ROLLBACK ALREADY PROCESSED";
			String deleteRollback = "DELETE FROM ICD_SETTLEMENT_REPORT WHERE to_date(filedate,'DD-MM-YY') = TO_DATE('"
					+ filedate + "','DD-MM-YY') ";
			getJdbcTemplate().execute(deleteRollback);
			return "SUCCESS";
		} catch (Exception e) {
			return e.toString();
		}
	}

	public boolean TTUMRollbackReportSETTLDFS(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			TTUMRollbackReportSETTLDFS rollBackexe = new TTUMRollbackReportSETTLDFS( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class TTUMRollbackReportSETTLDFS extends StoredProcedure {
		private static final String insert_proc = "DFS_DAILY_SETTLEMENT_PNB_ROLLBACK";

		public TTUMRollbackReportSETTLDFS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean TTUMRollbackReportSETTLMASTERCARD(String filedate) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			TTUMRollbackReportSETTLMASTERCARD rollBackexe = new TTUMRollbackReportSETTLMASTERCARD(
					getJdbcTemplate());
			inParams.put("V_FILEDATE", filedate);
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class TTUMRollbackReportSETTLMASTERCARD extends StoredProcedure {
		private static final String insert_proc = "MC_DOM_SETTLEMENT_ROLLBACK";

		public TTUMRollbackReportSETTLMASTERCARD(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	
	public boolean TTUMRollbackReportSETTLMASTERCARDINT(String filedate) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			TTUMRollbackReportSETTLMASTERCARDINT rollBackexe = new TTUMRollbackReportSETTLMASTERCARDINT(
					getJdbcTemplate());
			inParams.put("V_FILEDATE", filedate);
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class TTUMRollbackReportSETTLMASTERCARDINT extends StoredProcedure {
		private static final String insert_proc = "MC_INT_SETTLEMENT_TTUM_ROLLBACK";

		public TTUMRollbackReportSETTLMASTERCARDINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public String TTUMRollbackMCINT(String filedate) throws ParseException, Exception {
		try {
			Map<String, Object> inParams = new HashMap<>();
			int Count = 0;
			String rollbackcount = "SELECT COUNT(*) FROM MASTERCARD_INT_SETTLEMENT_TTUM WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY') ";
			Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[] { filedate },
					Integer.class)).intValue();
			System.out.println("rollbackcount  " + Count);
			if (Count == 0)
				return "ROLLBACK ALREADY PROCESSED";
			String deleteRollback = "DELETE FROM MASTERCARD_INT_SETTLEMENT_TTUM WHERE to_date(filedate,'DD-MM-YY') = TO_DATE('"
					+ filedate + "','DD-MM-YY') ";
			getJdbcTemplate().execute(deleteRollback);
			return "SUCCESS";
		} catch (Exception e) {
			return e.toString();
		}
	}

	public String TTUMRollbackMC(String filedate) throws ParseException, Exception {
		try {
			Map<String, Object> inParams = new HashMap<>();
			int Count = 0;
			String rollbackcount = "SELECT COUNT(*) FROM MASTERCARD_SETTLEMENT_TTUM WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY') ";
			Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[] { filedate },
					Integer.class)).intValue();
			System.out.println("rollbackcount  " + Count);
			if (Count == 0)
				return "ROLLBACK ALREADY PROCESSED";
			String deleteRollback = "DELETE FROM MASTERCARD_SETTLEMENT_TTUM WHERE to_date(filedate,'DD-MM-YY') = TO_DATE('"
					+ filedate + "','DD-MM-YY') ";
			getJdbcTemplate().execute(deleteRollback);
			return "SUCCESS";
		} catch (Exception e) {
			return e.toString();
		}
	}

	public String TTUMRollbackMCINTTTUM(String filedate) throws ParseException, Exception {
		try {
			Map<String, Object> inParams = new HashMap<>();
			int Count = 0;
			String rollbackcount = "SELECT COUNT(*) FROM MASTERCARD_INT_SETTLEMENT_TTUM WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY') ";
			Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[] { filedate },
					Integer.class)).intValue();
			System.out.println("rollbackcount  " + Count);
			if (Count == 0)
				return "ROLLBACK ALREADY PROCESSED";
			String deleteRollback = "DELETE FROM MASTERCARD_INT_SETTLEMENT_TTUM WHERE to_date(filedate,'DD-MM-YY') = TO_DATE('"
					+ filedate + "','DD-MM-YY') ";
			getJdbcTemplate().execute(deleteRollback);
			return "SUCCESS";
		} catch (Exception e) {
			return e.toString();
		}
	}

	public String TTUMROLLBACKRUPAY(String filedate) throws ParseException, Exception {
		try {
			Map<String, Object> inParams = new HashMap<>();
			int Count = 0;
			String rollbackcount = "SELECT COUNT(*) FROM RUPAY_DECL_TTUM WHERE to_date(filedate,'DD-MM-YY') = TO_DATE('"
					+ filedate + "','DD-MM-YY') ";
			System.out.println("rollbackcount " + rollbackcount);
			Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[0], Integer.class))
					.intValue();
			System.out.println("rollbackcount  " + Count);
			if (Count == 0)
				return "ROLLBACK ALREADY PROCESSED";
			String deleteRollback = "DELETE FROM RUPAY_DECL_TTUM WHERE to_date(filedate,'DD-MM-YY') = TO_DATE('"
					+ filedate + "','DD-MM-YY') ";
			getJdbcTemplate().execute(deleteRollback);
			String UPDATERollback = "  UPDATE settlement_rupay_dom_cbs SET dcrs_remarks='RUPAY_DOM_UNRECON-1' WHERE dcrs_remarks='DECLINE_TTUM_GENERATED' AND FILEDATE=TO_DATE('"
					+ filedate + "','DD-MM-YY')+2 and TO_DATE(TRAN_DATE,'DD-MM-YY')=TO_DATE('" + filedate
					+ "','DD-MM-YY')";
			getJdbcTemplate().execute(UPDATERollback);
			return "SUCCESS";
		} catch (Exception e) {
			return e.toString();
		}
	}

	public String TTUMRollbackReport(String filedate, String adjType) throws ParseException, Exception {
		try {
			Map<String, Object> inParams = new HashMap<>();
			int Count = 0;
			String rollbackcount = "";
			if (adjType.equalsIgnoreCase("PENALTY")) {
				rollbackcount = "SELECT COUNT(*) FROM NFS_ADJ_REPORT WHERE TO_DATE(FILEDATE ,'DD-MM-YY') = TO_DATE('"
						+ filedate + "' ,'DD-MM-YY') AND ADJTYPE LIKE '%" + adjType + "%'";
				Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[0], Integer.class))
						.intValue();
			} else if (adjType.equalsIgnoreCase("ACUIRER PENALTY")) {
				rollbackcount = "SELECT COUNT(*) FROM NFS_ACQ_PENALITY_TTUM WHERE TO_DATE(FILEDATE ,'DD-MM-YY') = TO_DATE('"
						+ filedate + "' ,'DD-MM-YY')";
				Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[0], Integer.class))
						.intValue();
			} else {
				rollbackcount = "SELECT COUNT(*) FROM NFS_ADJ_REPORT WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')  AND  ADJTYPE= ?";
				Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[] { filedate, adjType },
						Integer.class)).intValue();
			}
			System.out.println("rollbackcount  " + Count + rollbackcount);
			if (Count == 0)
				return "ROLLBACK ALREADY PROCESSED";
			if (adjType.equalsIgnoreCase("PENALTY")) {
				String deleteRollback = "DELETE FROM NFS_ADJ_REPORT WHERE to_date(filedate,'DD-MM-YY') = TO_DATE('"
						+ filedate + "','DD-MM-YY')  AND ADJTYPE LIKE '%" + adjType + "%'";
				getJdbcTemplate().execute(deleteRollback);
				String commmit = "commit";
				getJdbcTemplate().execute(commmit);
			} else if (adjType.equalsIgnoreCase("ACUIRER PENALTY")) {
				String deleteRollback = "DELETE FROM NFS_ACQ_PENALITY_TTUM WHERE to_date(filedate,'DD-MM-YY') = TO_DATE('"
						+ filedate + "','DD-MM-YY')";
				getJdbcTemplate().execute(deleteRollback);
				String commmit = "commit";
				getJdbcTemplate().execute(commmit);
			} else {
				String deleteRollback = "DELETE FROM NFS_ADJ_REPORT WHERE to_date(filedate,'DD-MM-YY') = TO_DATE('"
						+ filedate + "','DD-MM-YY')  AND ADJTYPE = '" + adjType + "'";
				getJdbcTemplate().execute(deleteRollback);
				String commmit = "commit";
				getJdbcTemplate().execute(commmit);
			}
			return "SUCCESS";
		} catch (Exception e) {
			return e.toString();
		}
	}

	public String TTUMRollbackReportICD(String filedate, String adjType) throws ParseException, Exception {
		try {
			Map<String, Object> inParams = new HashMap<>();
			int Count = 0;
			if (adjType.equalsIgnoreCase("PENALTY")) {
				String rollbackcount = "SELECT COUNT(*) FROM ICD_ADJ_REPORT WHERE TO_DATE(FILEDATE ,'DD-MM-YY') = TO_DATE('"
						+ filedate + "' ,'DD-MM-YY') AND ADJTYPE LIKE '%" + adjType + "%'";
				Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[0], Integer.class))
						.intValue();
			} else {
				String rollbackcount = "SELECT COUNT(*) FROM ICD_ADJ_REPORT WHERE to_date(filedate,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')  AND  ADJTYPE= ?";
				Count = ((Integer) getJdbcTemplate().queryForObject(rollbackcount, new Object[] { filedate, adjType },
						Integer.class)).intValue();
			}
			System.out.println("rollbackcount  " + Count);
			if (Count == 0)
				return "ROLLBACK ALREADY PROCESSED";
			if (adjType.equalsIgnoreCase("PENALTY")) {
				String deleteRollback = "DELETE FROM ICD_ADJ_REPORT WHERE to_date(filedate,'DD-MM-YY') = TO_DATE('"
						+ filedate + "','DD-MM-YY')  AND ADJTYPE LIKE '%" + adjType + "%'";
				getJdbcTemplate().execute(deleteRollback);
				String commmit = "commit";
				getJdbcTemplate().execute(commmit);
			} else {
				String deleteRollback = "DELETE FROM ICD_ADJ_REPORT  WHERE to_date(filedate,'DD-MM-YY') = TO_DATE('"
						+ filedate + "','DD-MM-YY')  AND ADJTYPE = '" + adjType + "'";
				getJdbcTemplate().execute(deleteRollback);
				String commmit = "commit";
				getJdbcTemplate().execute(commmit);
			}
			return "SUCCESS";
		} catch (Exception e) {
			return e.toString();
		}
	}

	public boolean rollBackRupayADJ(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackRupayADJ rollBackexe = new rollBackRupayADJ( getJdbcTemplate());
			inParams.put("filedt", beanObj.getDatepicker());
			if (beanObj.getStSubCategory().equalsIgnoreCase("RUPAY")) {
				if (beanObj.getAdjCategory().equalsIgnoreCase("Chargeback Acceptance")) {
					beanObj.setAdjCategory("Rupay_CHB");
				} else if (beanObj.getAdjCategory().equalsIgnoreCase("Partial Chargeback Acceptance")) {
					beanObj.setAdjCategory("Rupay_PCHB");
				} else if (beanObj.getAdjCategory().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
					beanObj.setAdjCategory("Rupay_CHBD");
				} else if (beanObj.getAdjCategory().equalsIgnoreCase("RUPAY_OFFLINE")) {
					beanObj.setAdjCategory("");
				} else if (beanObj.getAdjCategory().equalsIgnoreCase("Refund")) {
					beanObj.setAdjCategory("");
				}
			} else if (beanObj.getAdjCategory().equalsIgnoreCase("Chargeback Acceptance")) {
				beanObj.setAdjCategory("QSPARC_CHB");
			} else if (beanObj.getAdjCategory().equalsIgnoreCase("Partial Chargeback Acceptance")) {
				beanObj.setAdjCategory("QSPARC_PCHB");
			} else if (beanObj.getAdjCategory().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
				beanObj.setAdjCategory("QSPARC_CHBD");
			} else if (beanObj.getAdjCategory().equalsIgnoreCase("RUPAY_OFFLINE")) {
				beanObj.setAdjCategory("");
			} else if (beanObj.getAdjCategory().equalsIgnoreCase("Refund")) {
				beanObj.setAdjCategory("");
			}
			inParams.put("ADJTYPE", beanObj.getAdjCategory());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS" + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	
	private class rollBackRupayADJ extends StoredProcedure {
		private static final String insert_proc = "REFUND_TTUM_ROLLBACK";

		public rollBackRupayADJ(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("filedt",  Types.VARCHAR));
			declareParameter(new SqlParameter("ADJTYPE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackRupayRefund(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackRupayRefund rollBackexe = new rollBackRupayRefund( getJdbcTemplate());
			inParams.put("V_FILEDATE", beanObj.getDatepicker());
	inParams.put("V_TYPE", beanObj.getAdjCategory());
			
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS" + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackRupayRefund extends StoredProcedure {
		private static final String insert_proc = "MASTERCARD_DOM_REFUND_TTUM_ROLLBACK";

		public rollBackRupayRefund(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_TYPE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackNFSADJ(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackNFSADJ rollBackexe = new rollBackNFSADJ( getJdbcTemplate());
			inParams.put("filedt", beanObj.getDatepicker());
			inParams.put("V_ADJTYPE", beanObj.getAdjCategory());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS" + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	
	private class rollBackNFSADJ extends StoredProcedure {
		private static final String insert_proc = "NFS_ADJ_REPORT_ROLLBACK";

		public rollBackNFSADJ(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("filedt",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_ADJTYPE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackNFSADJICCW(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackNFSADJICCW rollBackexe = new rollBackNFSADJICCW( getJdbcTemplate());
			inParams.put("filedt", beanObj.getDatepicker());
			inParams.put("V_ADJTYPE", beanObj.getAdjCategory());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS" + outParams.get("msg"));
			String data = outParams.get("msg").toString();
			if (data.equalsIgnoreCase("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	
	private class rollBackNFSADJICCW extends StoredProcedure {
		private static final String insert_proc = "NFS_ADJ_ICCW_REPORT_PNB_ROLLBACK";

		public rollBackNFSADJICCW(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("filedt",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_ADJTYPE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}


	public String rollBackRupayADJVISA(String filedate, String subCate, String adjType)
			throws ParseException, Exception {
		try {
			Map<String, Object> inParams = new HashMap<>();
			int Count = 0;
			String checkUpload = "";
			int uploadedCount = 0;
			if (subCate.equalsIgnoreCase("DOMESTIC")) {
				if (adjType.equalsIgnoreCase("REFUND")) {
					checkUpload = "select count(*) from VISA_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date('"
							+ filedate + "','DD-MM-YY') AND TTUM_TYPE='VISA_DOM_REFUND'";
					uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0],
							Integer.class)).intValue();
				} else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
					System.out.println("filedate passing for validation check " + filedate);
					checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('"
							+ filedate + "','DD-MM-YY') and TTUM_TYPE='Rupay_CHB' ";
					uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0],
							Integer.class)).intValue();
				} else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
					System.out.println("filedate passing for validation check " + filedate);
					checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('"
							+ filedate + "','DD-MM-YY') and  TTUM_TYPE='Rupay_CHBD'  ";
					uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0],
							Integer.class)).intValue();
				}
			} else if (adjType.equalsIgnoreCase("REFUND")) {
				checkUpload = "select count(*) from VISA_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date('"
						+ filedate + "','DD-MM-YY') AND TTUM_TYPE='VISA_INT_REFUND'";
				uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class))
						.intValue();
			} else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
				System.out.println("filedate passing for validation check " + filedate);
				checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('"
						+ filedate + "','DD-MM-YY') and TTUM_TYPE='QSPARC_CHB'  ";
				uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class))
						.intValue();
			} else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
				System.out.println("filedate passing for validation check " + filedate);
				checkUpload = "select count(*) from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  =TO_date('"
						+ filedate + "','DD-MM-YY') and TTUM_TYPE ='QSPARC_CHBD' ";
				uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class))
						.intValue();
			}
			System.out.println("rollbackcount  " + uploadedCount + " " + checkUpload);
			if (uploadedCount == 0)
				return "ROLLBACK ALREADY PROCESSED";
			if (subCate.equalsIgnoreCase("DOMESTIC")) {
				if (adjType.equalsIgnoreCase("REFUND")) {
					checkUpload = " delete from VISA_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date('"
							+ filedate + "','DD-MM-YY') AND TTUM_TYPE='VISA_DOM_REFUND' ";
					uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0],
							Integer.class)).intValue();
				} else if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
					System.out.println("filedate passing for validation check " + filedate);
					checkUpload = "delete from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('"
							+ filedate + "','DD-MM-YY') and TTUM_TYPE='Rupay_CHB' ";
					uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0],
							Integer.class)).intValue();
				} else if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
					System.out.println("filedate passing for validation check " + filedate);
					checkUpload = "delete  from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('"
							+ filedate + "','DD-MM-YY') AND TTUM_TYPE='Rupay_CHBD'  ";
					uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0],
							Integer.class)).intValue();
				}
			} else {
				if (adjType.equalsIgnoreCase("REFUND")) {
					checkUpload = " delete from VISA_REFUND_TTUM  where to_DATE(filedate,'DD-MM-YY')  = TO_date('"
							+ filedate + "','DD-MM-YY') AND TTUM_TYPE='VISA_INT_REFUND' ";
					uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0],
							Integer.class)).intValue();
					String commmit = "commit";
					getJdbcTemplate().execute(commmit);
					return "SUCCESS";
				}
				if (adjType.equalsIgnoreCase("Chargeback Acceptance")) {
					System.out.println("filedate passing for validation check " + filedate);
					checkUpload = "delete from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  = TO_date('"
							+ filedate + "','DD-MM-YY') and TTUM_TYPE='QSPARC_CHB'";
					uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0],
							Integer.class)).intValue();
					String commmit = "commit";
					getJdbcTemplate().execute(commmit);
					return "SUCCESS";
				}
				if (adjType.equalsIgnoreCase("Chargeback Deemed Acceptance")) {
					System.out.println("filedate passing for validation check " + filedate);
					checkUpload = "delete from RUPAY_ADJUSTMENT_TTUM where to_DATE(filedate,'DD-MM-YY')  =TO_date('"
							+ filedate + "','DD-MM-YY') and TTUM_TYPE ='QSPARC_CHBD' ";
					uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0],
							Integer.class)).intValue();
					String commmit = "commit";
					getJdbcTemplate().execute(commmit);
					return "SUCCESS";
				}
			}
			return "SUCCESS";
		} catch (Exception e) {
			if (e.toString().contains("IncorrectResultSetColumnCountException"))
				return "SUCCESS";
			return e.toString();
		}
	}

	public List<Object> getAdjTTUM(NFSSettlementBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getFileName().contains("NFS"))
				if (beanObj.getAdjType().equalsIgnoreCase("PENALTY")) {
					getData1 = "SELECT * FROM  nfs_adj_report WHERE FILEDATE=  str_to_date('" + beanObj.getDatepicker()
							+ "','%Y/%m/%d')  AND ADJTYPE like'%" + beanObj.getAdjType() + "%' ";
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										// logger.info("count " + count);

										table_Data.put("SR_NO", String.valueOf(count));

										table_Data.put("DR/CR", rs.getString("DR_CR"));

										table_Data.put("ACQ_BANK", rs.getString("BANK_NAME"));
										table_Data.put("TXNDATE", rs.getString("TXN_DATE"));
										table_Data.put("TXNTIME", rs.getString("TXN_TIME"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("CARD_NO", rs.getString("CARDNO"));
										table_Data.put("ADJAMOUNT", rs.getString("AMOUNT"));
										table_Data.put("ACCOUNTNO", rs.getString("AC_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getAdjType().equalsIgnoreCase("ACUIRER PENALTY")) {
					getData1 = "SELECT TXNUID, U_ID, ADJDATE, ADJTYPE, ACQ, ISR, RESPONSE, TXNDATE, TXNTIME, RRN, ATMID, CARDNO, CHBDATE, CHBREF, TXNAMOUNT, ADJAMOUNT, ACQFEE, ISSFEE,ISSFEESW, NPCIFEE, ACQFEETAX, ISSFEETAX, NPCITAX, ADJREF, BANKADJREF, REASONDESC, PINCODE, ATMLOCATION, FCQM, ADJSETTLEMENTDATE, CUSTOMERPENALTY, ADJTIME, CYCLE, TAT_EXPIRY_DATE, ACQSTLAMOUNT, ACQCC, PAN_ENTRY_MODE, SERVICE_CODE, CARD_DAT_INPUT_CAPABILITY, MCC_CODE, CREATEDDATE, CREATEDBY, FILEDATE, TXNTYPE, FILENAME, ACCOUNT_NO, ATM_ID, ATM_CRM, MSP_FINAL, CASH_VENDOR, TYP, DR_CR ,NARRATION  FROM  nfs_acq_penality_ttum WHERE FILEDATE=  str_to_date('"
							+ beanObj.getDatepicker()
							+ "','%Y/%m/%d')  and  TXNAMOUNT >0 AND TXNAMOUNT IS NOT NULL AND  DR_CR = 'DEBIT'  UNION ALL SELECT TXNUID, U_ID, ADJDATE, ADJTYPE, ACQ, ISR, RESPONSE, TXNDATE, TXNTIME, RRN, ATMID, CARDNO, CHBDATE, CHBREF, TXNAMOUNT, ADJAMOUNT, ACQFEE, ISSFEE, ISSFEESW, NPCIFEE, ACQFEETAX, ISSFEETAX, NPCITAX, ADJREF, BANKADJREF, REASONDESC, PINCODE, ATMLOCATION, FCQM, ADJSETTLEMENTDATE, CUSTOMERPENALTY, ADJTIME, CYCLE, TAT_EXPIRY_DATE, ACQSTLAMOUNT, ACQCC, PAN_ENTRY_MODE, SERVICE_CODE, CARD_DAT_INPUT_CAPABILITY, MCC_CODE, CREATEDDATE, CREATEDBY, FILEDATE, TXNTYPE, FILENAME, ACCOUNT_NO, ATM_ID, ATM_CRM, MSP_FINAL, CASH_VENDOR, TYP, DR_CR ,NARRATION  FROM  nfs_acq_penality WHERE FILEDATE=  str_to_date('"
							+ beanObj.getDatepicker()
							+ "','%Y/%m/%d')  and  TXNAMOUNT >0 AND TXNAMOUNT IS NOT NULL AND  DR_CR = 'CREDIT'";
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										// logger.info("count " + count);

										table_Data.put("SR_NO", String.valueOf(count));
										table_Data.put("TXNUID", rs.getString("TXNUID"));
										table_Data.put("U_ID", rs.getString("U_ID"));
										table_Data.put("ADJDATE", rs.getString("ADJDATE"));
										table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));
										table_Data.put("ACQ", rs.getString("ACQ"));
										table_Data.put("ISR", rs.getString("ISR"));
										table_Data.put("RESPONSE", rs.getString("RESPONSE"));
										table_Data.put("TXNDATE", rs.getString("TXNDATE"));

										table_Data.put("TXNTIME", rs.getString("TXNTIME"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("ATMID", rs.getString("ATMID"));
										table_Data.put("CARDNO", rs.getString("CARDNO"));
										table_Data.put("CHBDATE", rs.getString("CHBDATE"));
										table_Data.put("CHBREF", rs.getString("CHBREF"));
										table_Data.put("TXNAMOUNT", rs.getString("TXNAMOUNT"));
										table_Data.put("ACQFEE", rs.getString("ACQFEE"));
										table_Data.put("ISSFEE", rs.getString("ISSFEE"));
										table_Data.put("ISSFEESW", rs.getString("ISSFEESW"));
										table_Data.put("NPCIFEE", rs.getString("NPCIFEE"));
										table_Data.put("ACQFEETAX", rs.getString("ACQFEETAX"));
										table_Data.put("ISSFEETAX", rs.getString("ISSFEETAX"));
										table_Data.put("NPCITAX", rs.getString("NPCITAX"));
										table_Data.put("ADJREF", rs.getString("ADJREF"));
										table_Data.put("BANKADJREF", rs.getString("BANKADJREF"));

										table_Data.put("REASONDESC", rs.getString("REASONDESC"));
										table_Data.put("PINCODE", rs.getString("PINCODE"));
										table_Data.put("ATMLOCATION", rs.getString("ATMLOCATION"));
										table_Data.put("FCQM", rs.getString("FCQM"));
										table_Data.put("ADJSETTLEMENTDATE", rs.getString("ADJSETTLEMENTDATE"));
										table_Data.put("CUSTOMERPENALTY", rs.getString("CUSTOMERPENALTY"));
										table_Data.put("ADJTIME", rs.getString("ADJTIME"));
										table_Data.put("CYCLE", rs.getString("CYCLE"));
										table_Data.put("TAT_EXPIRY_DATE", rs.getString("TAT_EXPIRY_DATE"));
										table_Data.put("ACQSTLAMOUNT", rs.getString("ACQSTLAMOUNT"));
										table_Data.put("ACQCC", rs.getString("ACQCC"));
										table_Data.put("PAN_ENTRY_MODE", rs.getString("PAN_ENTRY_MODE"));
										table_Data.put("SERVICE_CODE", rs.getString("SERVICE_CODE"));
										table_Data.put("CARD_DAT_INPUT_CAPABILITY",
												rs.getString("CARD_DAT_INPUT_CAPABILITY"));
										table_Data.put("MCC_CODE", rs.getString("MCC_CODE"));
										table_Data.put("CREATEDDATE", rs.getString("CREATEDDATE"));

										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("TXNTYPE", rs.getString("TXNTYPE"));
										table_Data.put("FILENAME", rs.getString("FILENAME"));
										table_Data.put("ACCOUNT_NO", rs.getString("ACCOUNT_NO"));
										table_Data.put("ATM_ID", rs.getString("ATM_ID"));
										table_Data.put("ATM_CRM", rs.getString("ATM_CRM"));
										table_Data.put("MSP_FINAL", rs.getString("MSP_FINAL"));
										table_Data.put("CASH_VENDOR", rs.getString("CASH_VENDOR"));
										table_Data.put("TYP", rs.getString("TYP"));
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else {
					
					getData1 = "SELECT * FROM  nfs_adj_report WHERE FILEDATE=  str_to_date('" + beanObj.getDatepicker()
							+ "','%Y/%m/%d')   AND ADJTYPE='" + beanObj.getAdjType() + "' ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										// logger.info("count " + count);

										table_Data.put("SR_NO", String.valueOf(count));

										table_Data.put("DR/CR", rs.getString("DR_CR"));

										table_Data.put("ACQ_BANK", rs.getString("BANK_NAME"));
										table_Data.put("TXNDATE", rs.getString("TXN_DATE"));
										table_Data.put("TXNTIME", rs.getString("TXN_TIME"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("CARD_NO", rs.getString("CARDNO"));
										table_Data.put("ADJAMOUNT", rs.getString("AMOUNT"));
										table_Data.put("ACCOUNTNO", rs.getString("AC_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				}
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}
	public List<Object> getAdjTTUMICCW(NFSSettlementBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getFileName().contains("NFS"))
				if (beanObj.getAdjType().equalsIgnoreCase("PENALTY")) {
					
					getData1 = "SELECT * FROM  nfs_adj_report WHERE FILEDATE=  str_to_date('" + beanObj.getDatepicker()
							+ "','%Y/%m/%d')  AND ADJTYPE like'%" + beanObj.getAdjType() +  " ICCW ATM"+"%' ";
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										// logger.info("count " + count);

										table_Data.put("SR_NO", String.valueOf(count));

										table_Data.put("DR/CR", rs.getString("DR_CR"));

										table_Data.put("ACQ_BANK", rs.getString("BANK_NAME"));
										table_Data.put("TXNDATE", rs.getString("TXN_DATE"));
										table_Data.put("TXNTIME", rs.getString("TXN_TIME"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("CARD_NO", rs.getString("CARDNO"));
										table_Data.put("ADJAMOUNT", rs.getString("AMOUNT"));
										table_Data.put("ACCOUNTNO", rs.getString("AC_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getAdjType().equalsIgnoreCase("ACUIRER PENALTY")) {
					getData1 = "SELECT TXNUID, U_ID, ADJDATE, ADJTYPE, ACQ, ISR, RESPONSE, TXNDATE, TXNTIME, RRN, ATMID, CARDNO, CHBDATE, CHBREF, TXNAMOUNT, ADJAMOUNT, ACQFEE, ISSFEE,ISSFEESW, NPCIFEE, ACQFEETAX, ISSFEETAX, NPCITAX, ADJREF, BANKADJREF, REASONDESC, PINCODE, ATMLOCATION, FCQM, ADJSETTLEMENTDATE, CUSTOMERPENALTY, ADJTIME, CYCLE, TAT_EXPIRY_DATE, ACQSTLAMOUNT, ACQCC, PAN_ENTRY_MODE, SERVICE_CODE, CARD_DAT_INPUT_CAPABILITY, MCC_CODE, CREATEDDATE, CREATEDBY, FILEDATE, TXNTYPE, FILENAME, ACCOUNT_NO, ATM_ID, ATM_CRM, MSP_FINAL, CASH_VENDOR, TYP, DR_CR ,NARRATION  FROM  nfs_acq_penality_ttum WHERE FILEDATE=  str_to_date('"
							+ beanObj.getDatepicker()
							+ "','%Y/%m/%d')  and  TXNAMOUNT >0 AND TXNAMOUNT IS NOT NULL AND  DR_CR = 'DEBIT'  UNION ALL SELECT TXNUID, U_ID, ADJDATE, ADJTYPE, ACQ, ISR, RESPONSE, TXNDATE, TXNTIME, RRN, ATMID, CARDNO, CHBDATE, CHBREF, TXNAMOUNT, ADJAMOUNT, ACQFEE, ISSFEE, ISSFEESW, NPCIFEE, ACQFEETAX, ISSFEETAX, NPCITAX, ADJREF, BANKADJREF, REASONDESC, PINCODE, ATMLOCATION, FCQM, ADJSETTLEMENTDATE, CUSTOMERPENALTY, ADJTIME, CYCLE, TAT_EXPIRY_DATE, ACQSTLAMOUNT, ACQCC, PAN_ENTRY_MODE, SERVICE_CODE, CARD_DAT_INPUT_CAPABILITY, MCC_CODE, CREATEDDATE, CREATEDBY, FILEDATE, TXNTYPE, FILENAME, ACCOUNT_NO, ATM_ID, ATM_CRM, MSP_FINAL, CASH_VENDOR, TYP, DR_CR ,NARRATION  FROM  nfs_acq_penality WHERE FILEDATE=  str_to_date('"
							+ beanObj.getDatepicker()
							+ "','%Y/%m/%d')  and  TXNAMOUNT >0 AND TXNAMOUNT IS NOT NULL AND  DR_CR = 'CREDIT' ";
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										// logger.info("count " + count);

										table_Data.put("SR_NO", String.valueOf(count));
										table_Data.put("TXNUID", rs.getString("TXNUID"));
										table_Data.put("U_ID", rs.getString("U_ID"));
										table_Data.put("ADJDATE", rs.getString("ADJDATE"));
										table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));
										table_Data.put("ACQ", rs.getString("ACQ"));
										table_Data.put("ISR", rs.getString("ISR"));
										table_Data.put("RESPONSE", rs.getString("RESPONSE"));
										table_Data.put("TXNDATE", rs.getString("TXNDATE"));

										table_Data.put("TXNTIME", rs.getString("TXNTIME"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("ATMID", rs.getString("ATMID"));
										table_Data.put("CARDNO", rs.getString("CARDNO"));
										table_Data.put("CHBDATE", rs.getString("CHBDATE"));
										table_Data.put("CHBREF", rs.getString("CHBREF"));
										table_Data.put("TXNAMOUNT", rs.getString("TXNAMOUNT"));
										table_Data.put("ACQFEE", rs.getString("ACQFEE"));
										table_Data.put("ISSFEE", rs.getString("ISSFEE"));
										table_Data.put("ISSFEESW", rs.getString("ISSFEESW"));
										table_Data.put("NPCIFEE", rs.getString("NPCIFEE"));
										table_Data.put("ACQFEETAX", rs.getString("ACQFEETAX"));
										table_Data.put("ISSFEETAX", rs.getString("ISSFEETAX"));
										table_Data.put("NPCITAX", rs.getString("NPCITAX"));
										table_Data.put("ADJREF", rs.getString("ADJREF"));
										table_Data.put("BANKADJREF", rs.getString("BANKADJREF"));

										table_Data.put("REASONDESC", rs.getString("REASONDESC"));
										table_Data.put("PINCODE", rs.getString("PINCODE"));
										table_Data.put("ATMLOCATION", rs.getString("ATMLOCATION"));
										table_Data.put("FCQM", rs.getString("FCQM"));
										table_Data.put("ADJSETTLEMENTDATE", rs.getString("ADJSETTLEMENTDATE"));
										table_Data.put("CUSTOMERPENALTY", rs.getString("CUSTOMERPENALTY"));
										table_Data.put("ADJTIME", rs.getString("ADJTIME"));
										table_Data.put("CYCLE", rs.getString("CYCLE"));
										table_Data.put("TAT_EXPIRY_DATE", rs.getString("TAT_EXPIRY_DATE"));
										table_Data.put("ACQSTLAMOUNT", rs.getString("ACQSTLAMOUNT"));
										table_Data.put("ACQCC", rs.getString("ACQCC"));
										table_Data.put("PAN_ENTRY_MODE", rs.getString("PAN_ENTRY_MODE"));
										table_Data.put("SERVICE_CODE", rs.getString("SERVICE_CODE"));
										table_Data.put("CARD_DAT_INPUT_CAPABILITY",
												rs.getString("CARD_DAT_INPUT_CAPABILITY"));
										table_Data.put("MCC_CODE", rs.getString("MCC_CODE"));
										table_Data.put("CREATEDDATE", rs.getString("CREATEDDATE"));

										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("TXNTYPE", rs.getString("TXNTYPE"));
										table_Data.put("FILENAME", rs.getString("FILENAME"));
										table_Data.put("ACCOUNT_NO", rs.getString("ACCOUNT_NO"));
										table_Data.put("ATM_ID", rs.getString("ATM_ID"));
										table_Data.put("ATM_CRM", rs.getString("ATM_CRM"));
										table_Data.put("MSP_FINAL", rs.getString("MSP_FINAL"));
										table_Data.put("CASH_VENDOR", rs.getString("CASH_VENDOR"));
										table_Data.put("TYP", rs.getString("TYP"));
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else {
			
					getData1 = "SELECT * FROM  nfs_adj_report WHERE FILEDATE=  str_to_date('" + beanObj.getDatepicker()
							+ "','%Y/%m/%d')   AND ADJTYPE='" + beanObj.getAdjType() + "+ ICCW ATM' ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										// logger.info("count " + count);

										table_Data.put("SR_NO", String.valueOf(count));

										table_Data.put("DR/CR", rs.getString("DR_CR"));

										table_Data.put("ACQ_BANK", rs.getString("BANK_NAME"));
										table_Data.put("TXNDATE", rs.getString("TXN_DATE"));
										table_Data.put("TXNTIME", rs.getString("TXN_TIME"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("CARD_NO", rs.getString("CARDNO"));
										table_Data.put("ADJAMOUNT", rs.getString("AMOUNT"));
										table_Data.put("ACCOUNTNO", rs.getString("AC_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				}
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getAdjTTUMICD(NFSSettlementBean beanObj) {List<Object> data = new ArrayList();
	try {
		String getData1 = null;
		List<Object> DailyData = new ArrayList();
		if (beanObj.getFileName().contains("ICD"))
			if (beanObj.getAdjType().equalsIgnoreCase("PENALTY")) {
				getData1 = "SELECT * FROM ICD_ADJ_REPORT WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+ beanObj.getDatepicker() + "','DD-MM-YY') AND ADJTYPE like'%" + beanObj.getAdjType()
						+ "%' ";
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									// logger.info("count " + count);

									table_Data.put("SR_NO", String.valueOf(count));

									table_Data.put("DR/CR", rs.getString("DR_CR"));

									table_Data.put("ACQ_BANK", rs.getString("BANK_NAME"));
									table_Data.put("TXNDATE", rs.getString("TXN_DATE"));
									table_Data.put("TXNTIME", rs.getString("TXN_TIME"));
									table_Data.put("RRN", rs.getString("RRN"));
									table_Data.put("CARD_NO", rs.getString("CARDNO"));
									table_Data.put("ADJAMOUNT", rs.getString("AMOUNT"));
									table_Data.put("ACCOUNTNO", rs.getString("AC_NO"));
									String maskCard = rs.getString("CARDNO");
									maskCard = org.apache.commons.lang.StringUtils.overlay(maskCard,
											org.apache.commons.lang.StringUtils.repeat("X", maskCard.length() - 2),
											6, maskCard.length() - 4);

									table_Data.put("NARRATION", rs.getString("NARRATION"));

									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else {
				getData1 = "SELECT * FROM ICD_ADJ_REPORT WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+ beanObj.getDatepicker() + "','DD-MM-YY')  AND ADJTYPE='" + beanObj.getAdjType() + "' ";
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									// logger.info("count " + count);

									table_Data.put("SR_NO", String.valueOf(count));

									table_Data.put("DR/CR", rs.getString("DR_CR"));

									table_Data.put("ACQ_BANK", rs.getString("BANK_NAME"));
									table_Data.put("TXNDATE", rs.getString("TXN_DATE"));
									table_Data.put("TXNTIME", rs.getString("TXN_TIME"));
									table_Data.put("RRN", rs.getString("RRN"));
									table_Data.put("CARD_NO", rs.getString("CARDNO"));
									table_Data.put("ADJAMOUNT", rs.getString("AMOUNT"));
									table_Data.put("ACCOUNTNO", rs.getString("AC_NO"));
									String maskCard = rs.getString("CARDNO");
									maskCard = org.apache.commons.lang.StringUtils.overlay(maskCard,
											org.apache.commons.lang.StringUtils.repeat("X", maskCard.length() - 2),
											6, maskCard.length() - 4);

									table_Data.put("NARRATION", rs.getString("NARRATION"));

									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			}
		data.add(DailyData);
		return data;
	} catch (Exception e) {
		System.out.println("Exception in getInterchangeData " + e);
		return null;
	}}

	public List<Object> getTTUMRUPAY(UnMatchedTTUMBean beanObj) {List<Object> data = new ArrayList();
	try {
		String getData1 = null;
		List<Object> DailyData = new ArrayList();
		if (beanObj.getCategory().contains("RUPAY")) {
			if (beanObj.getStSubCategory().contains("DOMESTIC")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
					getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from rupay_decl_ttum where FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										table_Data.put("DR_CR", rs.getString("DR_CR"));

										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from rupay_proactiv_ttum where FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										table_Data.put("DR_CR", rs.getString("DR_CR"));

										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});

				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from rupay_drop_ttum where  FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										table_Data.put("DR_CR", rs.getString("DR_CR"));

										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});

				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
					getData1 = "select BUSINESS_DATE, DR_CR, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, TRAN_TIME, CBS_AMOUNT, NPCI_AMOUNT, SUR, NARRATION  from  rupay_dom_dr_surch_ttum where FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
										table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
										table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

										table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
										table_Data.put("SUR", rs.getString("SUR"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
					getData1 = "select BUSINESS_DATE, DR_CR, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, TRAN_TIME, CBS_AMOUNT, NPCI_AMOUNT, SUR, NARRATION  from  rupay_dom_cr_surch_ttum where  FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
										table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
										table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

										table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
										table_Data.put("SUR", rs.getString("SUR"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});

				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION FROM dom_offline_pres_ttum where  FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										table_Data.put("DR_CR", rs.getString("DR_CR"));

										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else {
					getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from  rupay_dom_latepresentment where  FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										table_Data.put("DR_CR", rs.getString("DR_CR"));

										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				}
			} else {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
					getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from rupay_int_decl_ttum where FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										table_Data.put("DR_CR", rs.getString("DR_CR"));

										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from  rupay_proactiv_int_ttum where FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										table_Data.put("DR_CR", rs.getString("DR_CR"));

										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION FROM   rupay_int_ofline_pres_ttum where  FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										table_Data.put("DR_CR", rs.getString("DR_CR"));

										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from  rupay_drop_int_ttum where FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										table_Data.put("DR_CR", rs.getString("DR_CR"));

										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});

				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
					getData1 = "select BUSINESS_DATE, DR_CR, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, TRAN_TIME, CBS_AMOUNT, NPCI_AMOUNT, SUR, NARRATION  from  rupay_int_dr_surch_ttum where FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
								
										table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
										table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
										table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

										table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
										table_Data.put("SUR", rs.getString("SUR"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});

				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
					getData1 = "select BUSINESS_DATE, DR_CR, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, TRAN_TIME, CBS_AMOUNT, NPCI_AMOUNT, SUR, NARRATION  from  rupay_int_cr_surch_ttum where FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
									
										table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
										table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
										table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

										table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
										table_Data.put("SUR", rs.getString("SUR"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else {
					getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from rupay_int_latepresentment where FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();

										table_Data.put("DR_CR", rs.getString("DR_CR"));

										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});

				}
			}
		}
		data.add(DailyData);
		return data;
	} catch (Exception e) {
		System.out.println("Exception in getInterchangeData " + e);
		return null;
	}}

	public List<Object> getTTUMMASTERCARD(UnMatchedTTUMBean beanObj) {List<Object> data = new ArrayList();
	try {
		String getData1 = null;
		List<Object> DailyData = new ArrayList();
		if (beanObj.getStSubCategory().contains("ACQUIRER_DOM")) {
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_CREDIT")) {
				getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, AUTH_CODE, NARRATION, FILEDATE, REMARKS from mc_acq_dom_atm_loro_credit_ttum where filedate= str_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									table_Data.put("DR_CR", rs.getString("DR_CR"));

									table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));
									table_Data.put("FILEDATE", rs.getString("FILEDATE"));
									table_Data.put("REMARKS", rs.getString("REMARKS"));
									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_DEBIT")) {
				getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, AUTH_CODE, NARRATION, FILEDATE, REMARKS from mc_acq_dom_atm_loro_debit_ttum where filedate= str_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									table_Data.put("DR_CR", rs.getString("DR_CR"));

									table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));
									table_Data.put("FILEDATE", rs.getString("FILEDATE"));
									table_Data.put("REMARKS", rs.getString("REMARKS"));
									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else {
				getData1 = "select DR_CR, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, AUTH_CODE, NARRATION, FILEDATE, REMARKS from mc_acq_cross_recon_ttum where filedate= str_to_date(' "+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									table_Data.put("DR_CR", rs.getString("DR_CR"));

									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));
									table_Data.put("FILEDATE", rs.getString("FILEDATE"));
									table_Data.put("REMARKS", rs.getString("REMARKS"));
									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			}
		} else if (beanObj.getStSubCategory().contains("ACQUIRER_INT")) {
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_CREDIT")) {
				getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, AUTH_CODE, NARRATION, FILEDATE, REMARKS from mc_acq_int_atm_loro_credit_ttum where filedate= str_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									table_Data.put("DR_CR", rs.getString("DR_CR"));

									table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));
									table_Data.put("FILEDATE", rs.getString("FILEDATE"));
									table_Data.put("REMARKS", rs.getString("REMARKS"));
									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_DEBIT")) {
				getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, AUTH_CODE, NARRATION, FILEDATE, REMARKS from mc_acq_int_atm_loro_debit_ttum where filedate= str_to_date('"+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									table_Data.put("DR_CR", rs.getString("DR_CR"));

									table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));
									table_Data.put("FILEDATE", rs.getString("FILEDATE"));
									table_Data.put("REMARKS", rs.getString("REMARKS"));
									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else {
				getData1 = "select DR_CR, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, AUTH_CODE, NARRATION, FILEDATE, REMARKS from mc_acq_cross_recon_ttum where filedate= str_to_date(' "+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									table_Data.put("DR_CR", rs.getString("DR_CR"));

									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));
									table_Data.put("FILEDATE", rs.getString("FILEDATE"));
									table_Data.put("REMARKS", rs.getString("REMARKS"));
									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			}
		} else if (beanObj.getStSubCategory().contains("ISSUER")) {
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
				getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from rupay_decl_ttum where filedate= str_to_date('"
						+ beanObj.getFileDate() + "','%Y/%m/%d')";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									table_Data.put("DR_CR", rs.getString("DR_CR"));

									table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));

									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
				getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from mc_iss_pos_latepres_ttum where filedate= str_to_date('"+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									table_Data.put("DR_CR", rs.getString("DR_CR"));

									table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));

									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, TXN_TIME, TRACE_NO, AMOUNT, NARRATION from   mastercard_proactive_pos_ttum where filedate= str_to_date('"+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									table_Data.put("DR_CR", rs.getString("DR_CR"));

									table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));

									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				getData1 = " select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, TXN_TIME, TRACE_NO, AMOUNT, NARRATION from mc_iss_pos_drop_ttum where filedate= str_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									table_Data.put("DR_CR", rs.getString("DR_CR"));

									table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));

									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
				getData1 = "select BUSINESS_DATE, DR_CR, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, TRAN_TIME, CBS_AMOUNT, NPCI_AMOUNT, SUR, NARRATION  from  mc_dom_iss_dr_surch where filedate= str_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

									table_Data.put("DR_CR", rs.getString("DR_CR"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
									table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
									table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

									table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
									table_Data.put("SUR", rs.getString("SUR"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));
									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ATMSURCHARGE")) {
				getData1 = "select BUSINESS_DATE, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, TRAN_TIME, CBS_AMOUNT, NPCI_AMOUNT, SUR, NARRATION  from  mc_iss_atm_surcharge where filedate= str_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

							
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
									table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
									table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

									table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
									table_Data.put("SUR", rs.getString("SUR"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));
									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
				getData1 = "select BUSINESS_DATE, DR_CR, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, TRAN_TIME, CBS_AMOUNT, NPCI_AMOUNT, SUR, NARRATION  from  mc_dom_iss_cr_surch where  filedate= str_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

									table_Data.put("DR_CR", rs.getString("DR_CR"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
									table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
									table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

									table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
									table_Data.put("SUR", rs.getString("SUR"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));
									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
				getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION FROM DOM_OFFLINE_PRES_TTUM where  TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY')";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

									table_Data.put("DR_CR", rs.getString("DR_CR"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
									table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
									table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

									table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
									table_Data.put("SUR", rs.getString("SUR"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));
									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else {
				getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from REUPAY_DOM_LATEPRESENTMENT where  TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY')";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

									table_Data.put("DR_CR", rs.getString("DR_CR"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
									table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
									table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

									table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
									table_Data.put("SUR", rs.getString("SUR"));

									table_Data.put("NARRATION", rs.getString("NARRATION"));
									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			}
		} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
			getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from RUPAY_INT_DECL_TTUM where TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY')";
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					int count = 1, dataCount = 0, datasum = 0;

					while (rs.next()) {
						// logger.info("Inside rset");

						Map<String, String> table_Data = new HashMap<String, String>();
						table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

						table_Data.put("DR_CR", rs.getString("DR_CR"));
						table_Data.put("CARD_NO", rs.getString("CARD_NO"));
						table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
						table_Data.put("AC_NO", rs.getString("AC_NO"));
						table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
						table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
						table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

						table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
						table_Data.put("SUR", rs.getString("SUR"));

						table_Data.put("NARRATION", rs.getString("NARRATION"));
						count++;

						beanList.add(table_Data);

					}

					return beanList;
				}
			});
		} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
			getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from RUPAY_PROACTIV_INT_TTUM where TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY') ";
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					int count = 1, dataCount = 0, datasum = 0;

					while (rs.next()) {
						// logger.info("Inside rset");

						Map<String, String> table_Data = new HashMap<String, String>();
						table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

						table_Data.put("DR_CR", rs.getString("DR_CR"));
						table_Data.put("CARD_NO", rs.getString("CARD_NO"));
						table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
						table_Data.put("AC_NO", rs.getString("AC_NO"));
						table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
						table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
						table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

						table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
						table_Data.put("SUR", rs.getString("SUR"));

						table_Data.put("NARRATION", rs.getString("NARRATION"));
						count++;

						beanList.add(table_Data);

					}

					return beanList;
				}
			});
		} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
			getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from RUPAY_DROP_INT_TTUM where TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY')";
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					int count = 1, dataCount = 0, datasum = 0;

					while (rs.next()) {
						// logger.info("Inside rset");

						Map<String, String> table_Data = new HashMap<String, String>();
						table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

						table_Data.put("DR_CR", rs.getString("DR_CR"));
						table_Data.put("CARD_NO", rs.getString("CARD_NO"));
						table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
						table_Data.put("AC_NO", rs.getString("AC_NO"));
						table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
						table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
						table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

						table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
						table_Data.put("SUR", rs.getString("SUR"));

						table_Data.put("NARRATION", rs.getString("NARRATION"));
						count++;

						beanList.add(table_Data);

					}

					return beanList;
				}
			});
		} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
			getData1 = "select BUSINESS_DATE, DR_CR, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, TRAN_TIME, CBS_AMOUNT, NPCI_AMOUNT, SUR, NARRATION  from  RUPAY_INT_DR_SURCH_TTUM where TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY')";
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					int count = 1, dataCount = 0, datasum = 0;

					while (rs.next()) {
						// logger.info("Inside rset");

						Map<String, String> table_Data = new HashMap<String, String>();
						table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

						table_Data.put("DR_CR", rs.getString("DR_CR"));
						table_Data.put("CARD_NO", rs.getString("CARD_NO"));
						table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
						table_Data.put("AC_NO", rs.getString("AC_NO"));
						table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
						table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
						table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

						table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
						table_Data.put("SUR", rs.getString("SUR"));

						table_Data.put("NARRATION", rs.getString("NARRATION"));
						count++;

						beanList.add(table_Data);

					}

					return beanList;
				}
			});
		} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
			getData1 = "select BUSINESS_DATE, DR_CR, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, TRAN_TIME, CBS_AMOUNT, NPCI_AMOUNT, SUR, NARRATION  from  RUPAY_INT_CR_SURCH_TTUM where  TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY')";
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					int count = 1, dataCount = 0, datasum = 0;

					while (rs.next()) {
						// logger.info("Inside rset");

						Map<String, String> table_Data = new HashMap<String, String>();
						table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

						table_Data.put("DR_CR", rs.getString("DR_CR"));
						table_Data.put("CARD_NO", rs.getString("CARD_NO"));
						table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
						table_Data.put("AC_NO", rs.getString("AC_NO"));
						table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
						table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
						table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

						table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
						table_Data.put("SUR", rs.getString("SUR"));

						table_Data.put("NARRATION", rs.getString("NARRATION"));
						count++;

						beanList.add(table_Data);

					}

					return beanList;
				}
			});
		} else {
			getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION from REUPAY_DOM_LATEPRESENTMENT where  TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY')";
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					int count = 1, dataCount = 0, datasum = 0;

					while (rs.next()) {
						// logger.info("Inside rset");

						Map<String, String> table_Data = new HashMap<String, String>();
						table_Data.put("BUSINESS_DATE", rs.getString("BUSINESS_DATE"));

						table_Data.put("DR_CR", rs.getString("DR_CR"));
						table_Data.put("CARD_NO", rs.getString("CARD_NO"));
						table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
						table_Data.put("AC_NO", rs.getString("AC_NO"));
						table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
						table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
						table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));

						table_Data.put("NPCI_AMOUNT", rs.getString("NPCI_AMOUNT"));
						table_Data.put("SUR", rs.getString("SUR"));

						table_Data.put("NARRATION", rs.getString("NARRATION"));
						count++;

						beanList.add(table_Data);

					}

					return beanList;
				}
			});
		}
		data.add(DailyData);
		return data;
	} catch (Exception e) {
		System.out.println("Exception in getInterchangeData " + e);
		return null;
	}}

	public List<Object> getTTUMNFS(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
		
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getStSubCategory().equalsIgnoreCase("ISSUER")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData1 = "SELECT DR_CR,  DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, TXN_DATE, TXN_TIME, AMOUNT, RRN, NARRATION FROM nfs_iss_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
							+ "' order by CARD_NO , RRN , AMOUNT  ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
										table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					getData1 = "SELECT DR_CR ,DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, TXN_DATE, TXN_TIME, AMOUNT, RRN, NARRATION from nfs_iss_recon_ttums where FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
							+ "' order by CARD_NO , RRN , AMOUNT ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
										table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, TXN_DATE, TXN_TIME, AMOUNT, RRN, NARRATION FROM nfs_iss_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
							+ "' order by CARD_NO , RRN , AMOUNT ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
										table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				}
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
				getData1 = "SELECT DR_CR,  DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION FROM nfs_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate().replaceAll("/", "-") + "','%Y-%m-%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
						+ "'  order by CARD_NO , RRN , AMOUNT";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("TERMINAL_ID", rs.getString("TERMINAL_ID"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT MATM")) {
				getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION FROM nfs_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate() + "','%Y-%m-%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM() + "'  ";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("TERMINAL_ID", rs.getString("TERMINAL_ID"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
				getData1 = "SELECT DR_CR ,DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION FROM nfs_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate().replaceAll("/", "-") + "','%Y-%m-%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
						+ "' order by CARD_NO , RRN , AMOUNT";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("TERMINAL_ID", rs.getString("TERMINAL_ID"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			} else {
				getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION FROM nfs_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate() + "','%Y-%m-%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM() + "' ";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("TERMINAL_ID", rs.getString("TERMINAL_ID"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			}
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getTTUMICD(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getStSubCategory().equalsIgnoreCase("ISSUER")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, TXN_TIME, TRACE_NO, AMOUNT, NARRATION, FILEDATE\r\nFROM ICD_ISS_RECON_PROACTIVE  WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TXN_DATE", rs.getString("DATE_OF_TXN"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					getData1 = "SELECT DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION, TTUM_TYPE, FILEDATE\r\nFROM ICD_ISS_RECON_DROP  \r\nWHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY')";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, TXN_DATE, TXN_TIME, AMOUNT, RRN, NARRATION FROM NFS_ISS_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				}
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				getData1 = "SELECT DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DR_CR, DATE_OF_TXN, TXN_TIME, TRACE_NO, AMOUNT, NARRATION ,FILEDATE\r\nFROM ICD_ACQ_RECON_DROP_TTUM  WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("DR_CR", rs.getString("DR_CR"));
									table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("FILEDATE", rs.getString("FILEDATE"));
									table_Data.put("NARRATION", rs.getString("NARRATION"));

									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				getData1 = "SELECT DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION \r\nFROM ICD_ACQ_PROACTIVE   WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+

						beanObj.getLocalDate() + "','DD-MM-YY')";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("DR_CR", rs.getString("DR_CR"));
									table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("FILEDATE", rs.getString("FILEDATE"));
									table_Data.put("NARRATION", rs.getString("NARRATION"));

									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT ATM")) {
				getData1 = "SELECT DR_CR ,DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION FROM NFS_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
						+ "' AND AMOUNT <> 0";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("DR_CR", rs.getString("DR_CR"));
									table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("FILEDATE", rs.getString("FILEDATE"));
									table_Data.put("NARRATION", rs.getString("NARRATION"));

									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else {
				getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION FROM NFS_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
						+ "' AND AMOUNT <> 0";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("DR_CR", rs.getString("DR_CR"));
									table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("FILEDATE", rs.getString("FILEDATE"));
									table_Data.put("NARRATION", rs.getString("NARRATION"));

									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			}
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getTTUMJCB(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
				getData1 = "SELECT DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION, TTUM_TYPE, FILEDATE, UID FROM jcb_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
						+ "' ";
				logger.info("data get sql " + getData1);
			
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("TERMINAL_ID", rs.getString("TERMINAL_ID"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));
							table_Data.put("TTUM_TYPE", rs.getString("TTUM_TYPE"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("UID", rs.getString("UID"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			}else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
				getData1 = "SELECT DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION, TTUM_TYPE, FILEDATE, UID FROM jcb_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
						+ "' ";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
						
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("TERMINAL_ID", rs.getString("TERMINAL_ID"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));
							table_Data.put("TTUM_TYPE", rs.getString("TTUM_TYPE"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("UID", rs.getString("UID"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			}
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}
	public List<Object> getTTUMDFS(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
				getData1 = "SELECT DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION, TTUM_TYPE, FILEDATE, UID FROM dfs_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
						+ "' ";
				logger.info("data get sql " + getData1);
			
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("TERMINAL_ID", rs.getString("TERMINAL_ID"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));
							table_Data.put("TTUM_TYPE", rs.getString("TTUM_TYPE"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("UID", rs.getString("UID"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			}else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
				getData1 = "SELECT DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION, TTUM_TYPE, FILEDATE, UID FROM dfs_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
						+ "' ";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
						
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("TERMINAL_ID", rs.getString("TERMINAL_ID"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));
							table_Data.put("TTUM_TYPE", rs.getString("TTUM_TYPE"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("UID", rs.getString("UID"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			}
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}
	public List<Object> getTTUMICCW(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getStSubCategory().equalsIgnoreCase("ISSUER")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, TXN_DATE, TXN_TIME, AMOUNT, RRN, NARRATION, TTUM_TYPE, FILEDATE, UID\r\n"
							+ "FROM nfs_iccw_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE('"+beanObj.getLocalDate()+"','%Y/%m/%d')  AND TTUM_TYPE='PROACTIVE'";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
										table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, TXN_DATE, TXN_TIME, AMOUNT, RRN, NARRATION, TTUM_TYPE, FILEDATE, UID\r\n"
							+ "FROM nfs_iccw_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE('"+beanObj.getLocalDate()+"','%Y/%m/%d')  AND TTUM_TYPE='DROP'";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
										table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, TXN_DATE, TXN_TIME, AMOUNT, RRN, NARRATION FROM nfs_iss_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y-%m-%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
							+ "' order by CARD_NO , RRN , AMOUNT ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
										table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				}
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
				getData1 = "SELECT DR_CR,  DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION FROM nfs_iccw_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
						+ "'  order by CARD_NO , RRN , AMOUNT";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("TERMINAL_ID", rs.getString("TERMINAL_ID"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT MATM")) {
				getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION FROM nfs_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate() + "','%Y-%m-%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM() + "'  ";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("TERMINAL_ID", rs.getString("TERMINAL_ID"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
				getData1 = "SELECT DR_CR ,DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION FROM nfs_iccw_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
						+ "' order by CARD_NO , RRN , AMOUNT";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("TERMINAL_ID", rs.getString("TERMINAL_ID"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			} else {
				getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION FROM nfs_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate() + "','%Y-%m-%d') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM() + "' ";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("TERMINAL_ID", rs.getString("TERMINAL_ID"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			}
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getTTUMVISA(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getStSubCategory().equalsIgnoreCase("ISS")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData1 = "SELECT DR_CR,DISPUTE_DATE,BANK_NAME,CARD_NO,AC_NO,DATE_OF_TXN,AMOUNT,TRACE_NO,NARRATION,FILEDATE FROM visa_iss_proactive_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TXN_DATE", rs.getString("DATE_OF_TXN"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					getData1 = "SELECT DR_CR,DISPUTE_DATE,BANK_NAME,CARD_NO,AC_NO,DATE_OF_TXN,AMOUNT,AUTH_CODE,NARRATION,FILEDATE \r\nFROM visa_iss_drop_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					getData1 = "SELECT DR_CR,DISPUTE_DATE,BANK_NAME,CARD_NO,AC_NO,DATE_OF_TXN,AMOUNT,AUTH_CODE,NARRATION,FILEDATE \r\nFROM visa_iss_late_presentment_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")) {
					getData1 = "SELECT DR_CR,DISPUTE_DATE,BANK_NAME,CARD_NO,AC_NO,DATE_OF_TXN,AMOUNT,AUTH_CODE,NARRATION,FILEDATE \r\nFROM visa_iss_org_wdl_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DEBIT SURCHARGE")) {
					getData1 = "SELECT DR_CR, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, TRAN_TIME, PURCHASE_DATE, CBS_AMOUNT, VISA_AMOUNT, SUR, NARRATION, AUTH_CODE, SETTLEMENT_FLAG, TC, FILEDATE, UID\r\nFROM visa_iss_dr_surch_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("PURCHASE_DATE", rs.getString("PURCHASE_DATE"));

										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
										table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
										table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));
										table_Data.put("VISA_AMOUNT", rs.getString("VISA_AMOUNT"));
										table_Data.put("SUR", rs.getString("SUR"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));
										table_Data.put("SETTLEMENT_FLAG", rs.getString("SETTLEMENT_FLAG"));
										table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
										table_Data.put("TC", rs.getString("TC"));

										table_Data.put("UID", rs.getString("UID"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CREDIT SURCHARGE")) {
					getData1 = "SELECT DR_CR, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, TRAN_TIME, PURCHASE_DATE, CBS_AMOUNT, VISA_AMOUNT, SUR, NARRATION, AUTH_CODE, SETTLEMENT_FLAG, TC, FILEDATE, UID\r\nFROM visa_iss_cr_surch_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("PURCHASE_DATE", rs.getString("PURCHASE_DATE"));

										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TRAN_DATE", rs.getString("TRAN_DATE"));
										table_Data.put("TRAN_TIME", rs.getString("TRAN_TIME"));
										table_Data.put("CBS_AMOUNT", rs.getString("CBS_AMOUNT"));
										table_Data.put("VISA_AMOUNT", rs.getString("VISA_AMOUNT"));
										table_Data.put("SUR", rs.getString("SUR"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));
										table_Data.put("SETTLEMENT_FLAG", rs.getString("SETTLEMENT_FLAG"));
										table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
										table_Data.put("TC", rs.getString("TC"));

										table_Data.put("UID", rs.getString("UID"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, AUTH_CODE, NARRATION, ARN, SETTLEMENT_FLAG, \r\nTC, FILEDATE, UID FROM visa_iss_dom_refund_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));

										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));

										table_Data.put("ARN", rs.getString("ARN"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));
										table_Data.put("SETTLEMENT_FLAG", rs.getString("SETTLEMENT_FLAG"));
										table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
										table_Data.put("TC", rs.getString("TC"));

										table_Data.put("UID", rs.getString("UID"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND_REV")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, AUTH_CODE, NARRATION, ARN, SETTLEMENT_FLAG, \r\nTC, FILEDATE, UID FROM visa_iss_dom_refund_rev_ttum WHERE FILEDATE=STR_TO_DATE(' "
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));

										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));

										table_Data.put("ARN", rs.getString("ARN"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));
										table_Data.put("SETTLEMENT_FLAG", rs.getString("SETTLEMENT_FLAG"));
										table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
										table_Data.put("TC", rs.getString("TC"));

										table_Data.put("UID", rs.getString("UID"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND_INT")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, AUTH_CODE, NARRATION, ARN, SETTLEMENT_FLAG, \r\nTC, FILEDATE, UID FROM visa_iss_int_refund_ttum_data WHERE FILEDATE=STR_TO_DATE(' "
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));

										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));

										table_Data.put("ARN", rs.getString("ARN"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));
										table_Data.put("SETTLEMENT_FLAG", rs.getString("SETTLEMENT_FLAG"));
										table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
										table_Data.put("TC", rs.getString("TC"));

										table_Data.put("UID", rs.getString("UID"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});} else {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, AUTH_CODE, NARRATION, ARN, SETTLEMENT_FLAG, \r\nTC, FILEDATE, UID FROM visa_iss_int_refund_rev_ttum WHERE FILEDATE=STR_TO_DATE(' "
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));

										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));

										table_Data.put("ARN", rs.getString("ARN"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));
										table_Data.put("SETTLEMENT_FLAG", rs.getString("SETTLEMENT_FLAG"));
										table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
										table_Data.put("TC", rs.getString("TC"));

										table_Data.put("UID", rs.getString("UID"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});	}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ACQ DOM ATM")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, AUTH_CODE, NARRATION, FILEDATE, UID FROM visa_acq_dom_atm_loro_credit_ttum_data  where  filedate= STR_TO_DATE(' "
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("TRACE_NO", rs.getString("AUTH_CODE"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										table_Data.put("UID", rs.getString("UID"));
										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION, FILEDATE, UID  FROM visa_acq_dom_atm_loro_debit_ttum_data  where filedate= STR_TO_DATE(' "
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										table_Data.put("UID", rs.getString("UID"));
							count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND")) {
					getData1 = "SELECT DR_CR,DISPUTE_DATE,BANK_NAME,CARD_NO,AC_NO,DATE_OF_TXN,AMOUNT,AUTH_CODE,NARRATION,FILEDATE \r\nFROM VISA_DOM_POS_REFUND_TTUM_DATA where TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										table_Data.put("UID", rs.getString("UID"));
										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					getData1 = "SELECT DR_CR,DISPUTE_DATE,BANK_NAME,CARD_NO,AC_NO,DATE_OF_TXN,AMOUNT,AUTH_CODE,NARRATION,FILEDATE \r\nFROM VISA_DOM_POS_LATE_PRESENTMENT_DATA where TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										table_Data.put("UID", rs.getString("UID"));
										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")) {
					getData1 = "SELECT DR_CR,DISPUTE_DATE,BANK_NAME,CARD_NO,AC_NO,DATE_OF_TXN,AMOUNT,AUTH_CODE,NARRATION,FILEDATE \r\nFROM VISA_DOM_POS_ORG_WDL_TTUM_DATA where TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										table_Data.put("UID", rs.getString("UID"));
										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DEBIT SURCHARGE")) {
					getData1 = "SELECT DR_CR,BUSINESS_DATE,CARD_NO,TRACE_NO,AC_NO,TRAN_DATE,TRAN_TIME,CBS_AMOUNT,VISA_AMOUNT,SUR,NARRATION,FILEDATE \r\nFROM VISA_DOM_POS_DR_SURCH_TTUM_DATA where TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										table_Data.put("UID", rs.getString("UID"));
										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CREDIT SURCHARGE")) {
					getData1 = "SELECT DR_CR,BUSINESS_DATE,CARD_NO,TRACE_NO,AC_NO,TRAN_DATE,TRAN_TIME,CBS_AMOUNT,VISA_AMOUNT,SUR,NARRATION,FILEDATE \r\nFROM VISA_DOM_POS_CR_SURCH_TTUM_DATA where TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										table_Data.put("UID", rs.getString("UID"));
										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, TXN_DATE, TXN_TIME, AMOUNT, RRN, NARRATION FROM NFS_ISS_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
							+ "' AND AMOUNT <> 0";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										table_Data.put("UID", rs.getString("UID"));
										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ACQ INT ATM")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, AUTH_CODE, NARRATION, FILEDATE,ATM_ID, UID  FROM visa_acq_int_atm_loro_credit_ttum_data  where filedate= STR_TO_DATE(' "
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));
										table_Data.put("ATM_ID", rs.getString("ATM_ID"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										table_Data.put("UID", rs.getString("UID"));
							count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, AC_NO, DATE_OF_TXN, AMOUNT, TRACE_NO, NARRATION, FILEDATE, UID FROM visa_acq_int_atm_loro_debit_ttum_data  where filedate = STR_TO_DATE(' "
							+ beanObj.getLocalDate() + "','%Y/%m/%d') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										table_Data.put("UID", rs.getString("UID"));
				
										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND")) {
					getData1 = "SELECT DR_CR,DISPUTE_DATE,BANK_NAME,CARD_NO,AC_NO,DATE_OF_TXN,AMOUNT,AUTH_CODE,NARRATION,FILEDATE \r\nFROM VISA_INT_POS_REFUND_TTUM_DATA where TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+

							beanObj.getLocalDate() + "','DD-MM-YY') ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										table_Data.put("UID", rs.getString("UID"));
										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, TXN_DATE, TXN_TIME, AMOUNT, RRN, NARRATION FROM NFS_ISS_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
							+ "' AND AMOUNT <> 0";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
										table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("FILEDATE", rs.getString("FILEDATE"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));

										table_Data.put("UID", rs.getString("UID"));
										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				}
			} else {
				getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION FROM NFS_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
						+ "' AND AMOUNT <> 0";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("DR_CR", rs.getString("DR_CR"));
									table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
									table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
									table_Data.put("CARD_NO", rs.getString("CARD_NO"));
									table_Data.put("AC_NO", rs.getString("AC_NO"));
									table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
									table_Data.put("TRACE_NO", rs.getString("TRACE_NO"));
									table_Data.put("AMOUNT", rs.getString("AMOUNT"));
									table_Data.put("FILEDATE", rs.getString("FILEDATE"));
									table_Data.put("NARRATION", rs.getString("NARRATION"));

									table_Data.put("UID", rs.getString("UID"));
									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			}
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getTTUMVISANB(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			getData1 = "SELECT '519402230043000' AS ACCOUNT_NO,'D' AS DR_CR, CARD_NUMBER,AUTHORIZATION_CODE, FILEDATE, ARN, PURCHASE_DATE, DESTINATION_AMOUNT, MERCHANT_COUNTRY_CODE, SETTLEMENT_FLAG FROM SETTLEMENT_VISA_DOM_ISS_ATM_VISA \r\nWHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('"
					+ beanObj.getLocalDate()
					+ "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-DOM-ISS-ATM-CROSS-MATCHED-2' AND MERCHANT_COUNTRY_CODE IN('NP','BH','BT')\r\n"
					+ "UNION ALL\r\n"
					+ "SELECT '519402230042000' AS ACCOUNT_NO,'C' AS DR_CR, CARD_NUMBER,AUTHORIZATION_CODE, FILEDATE, ARN, PURCHASE_DATE, DESTINATION_AMOUNT, MERCHANT_COUNTRY_CODE, SETTLEMENT_FLAG FROM SETTLEMENT_VISA_DOM_ISS_ATM_VISA \r\n"
					+ "WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + beanObj.getLocalDate()
					+ "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-DOM-ISS-ATM-CROSS-MATCHED-2' AND MERCHANT_COUNTRY_CODE IN('NP','BH','BT')\r\n"
					+ "UNION ALL\r\n"
					+ "SELECT '519402230042000' AS ACCOUNT_NO,'D' AS DR_CR,CARD_NUMBER, AUTHORIZATION_CODE, FILEDATE, ARN, PURCHASE_DATE, DESTINATION_AMOUNT, MERCHANT_COUNTRY_CODE, SETTLEMENT_FLAG FROM SETTLEMENT_VISA_INT_ISS_ATM_VISA \r\n"
					+ "WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + beanObj.getLocalDate()
					+ "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-INT-ISS-ATM-CROSS-MATCHED-2'AND MERCHANT_COUNTRY_CODE IN('NP','BH','BT')\r\n"
					+ "UNION ALL\r\n"
					+ "SELECT '519402230043000' AS ACCOUNT_NO,'C' AS DR_CR,CARD_NUMBER, AUTHORIZATION_CODE, FILEDATE, ARN, PURCHASE_DATE, DESTINATION_AMOUNT, MERCHANT_COUNTRY_CODE, SETTLEMENT_FLAG FROM SETTLEMENT_VISA_INT_ISS_ATM_VISA \r\n"
					+ "WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + beanObj.getLocalDate()
					+ "','DD-MM-YY') AND DCRS_REMARKS='VISA-INT-ISS-ATM-CROSS-MATCHED-2'AND MERCHANT_COUNTRY_CODE IN('NP','BH','BT')\r\n";
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							int count = 1, dataCount = 0, datasum = 0;

							while (rs.next()) {
								// logger.info("Inside rset");

								Map<String, String> table_Data = new HashMap<String, String>();

								table_Data.put("TTUM", rs.getString("TTUM"));

							
								beanList.add(table_Data);

							}

							return beanList;
						}
					});
			data.add(DailyData);
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getTTUMCTCSETTLEMENT(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			getData1 = "(SELECT  'D' AS DR_CR ,'519402230234000' AS ACCOUNT_NO ,OS1.BUSINESS_DATE, OS1.CARD_NO, OS1.TRACE_NO, OS1.AC_NO, \r\n    OS1.TRAN_DATE, OS1.AMOUNT, OS1.POSTED_DATE, OS1.TR_NO, OS1.FILEDATE  FROM SETTLEMENT_NFS_ACQ_C2C_CBS  OS1 \r\n    where  TO_DATE(OS1.FILEDATE,'DD-MM-YY')  = TO_DATE('"
					+

					beanObj.getLocalDate() + "','DD-MM-YY') \r\n"
					+ "    AND  EXISTS (  SELECT 1 FROM SETTLEMENT_NFS_ISS_C2C_CBS OS2 \r\n"
					+ "    where  TO_DATE(OS2.FILEDATE,'DD-MM-YY') =  TO_DATE('" + beanObj.getLocalDate()
					+ "' ,'DD-MM-YY') \r\n"
					+ "    AND  OS1.AMOUNT= OS2.AMOUNT   AND  OS1.TRACE_NO= OS2.TRACE_NO) and os1.dcrs_remarks='NFS-ACQ-C2C-CBS-MATCHED-2')\r\n"
					+ "    \r\n" + "    UNION ALL\r\n" + "    \r\n"
					+ "    (SELECT  'C' AS DR_CR ,'519408250431000' AS ACCOUNT_NO ,OS1.BUSINESS_DATE, OS1.CARD_NO, OS1.TRACE_NO, OS1.AC_NO, \r\n"
					+ "    OS1.TRAN_DATE, OS1.AMOUNT, OS1.POSTED_DATE, OS1.TR_NO, OS1.FILEDATE  FROM SETTLEMENT_NFS_ACQ_C2C_CBS  OS1 \r\n"
					+ "    where TO_DATE(OS1.FILEDATE,'DD-MM-YY')  = TO_DATE('" + beanObj.getLocalDate()
					+ "','DD-MM-YY') \r\n" + "    AND  EXISTS (  SELECT 1 FROM SETTLEMENT_NFS_ISS_C2C_CBS OS2 \r\n"
					+ "    where  TO_DATE(OS2.FILEDATE,'DD-MM-YY') =  TO_DATE('" + beanObj.getLocalDate()
					+ "' ,'DD-MM-YY') \r\n"
					+ "    AND  OS1.AMOUNT= OS2.AMOUNT   AND  OS1.TRACE_NO= OS2.TRACE_NO) and os1.dcrs_remarks='NFS-ACQ-C2C-CBS-MATCHED-2')";
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							int count = 1, dataCount = 0, datasum = 0;

							while (rs.next()) {
								// logger.info("Inside rset");

								Map<String, String> table_Data = new HashMap<String, String>();

								table_Data.put("TTUM", rs.getString("TTUM"));

							
								beanList.add(table_Data);

							}

							return beanList;
						}
					});
			data.add(DailyData);
	
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getTTUMATMPOSCROSSROUNTING(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getTTUMCategory().equalsIgnoreCase("ATM_REPORT")) {
				getData1 = "SELECT '519402230042000' AS ACCOUNT_NO,'D' AS DR_CR, CARD_NUMBER,AUTHORIZATION_CODE, FILEDATE, ARN, PURCHASE_DATE, DESTINATION_AMOUNT, MERCHANT_COUNTRY_CODE, SETTLEMENT_FLAG FROM SETTLEMENT_VISA_INT_ISS_ATM_VISA \r\nWHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate()
						+ "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-INT-ISS-ATM-CROSS-MATCHED-2' AND MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')\r\n"
						+ "\r\n" + "UNION ALL\r\n"
						+ "SELECT '519402230043000' AS ACCOUNT_NO,'C' AS DR_CR, CARD_NUMBER,AUTHORIZATION_CODE, FILEDATE, ARN, PURCHASE_DATE, DESTINATION_AMOUNT, MERCHANT_COUNTRY_CODE, SETTLEMENT_FLAG FROM SETTLEMENT_VISA_INT_ISS_ATM_VISA \r\n"
						+ "WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + beanObj.getLocalDate()
						+ "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-INT-ISS-ATM-CROSS-MATCHED-2' AND MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')\r\n"
						+ "\r\n" + "UNION ALL\r\n" + "\r\n"
						+ "SELECT '519402230043000' AS ACCOUNT_NO,'D' AS DR_CR,CARD_NUMBER, AUTHORIZATION_CODE, FILEDATE, ARN, PURCHASE_DATE, DESTINATION_AMOUNT, MERCHANT_COUNTRY_CODE, SETTLEMENT_FLAG FROM SETTLEMENT_VISA_DOM_ISS_ATM_VISA \r\n"
						+ "WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + beanObj.getLocalDate()
						+ "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-DOM-ISS-ATM-CROSS-MATCHED-2'AND MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')\r\n"
						+ "UNION ALL\r\n"
						+ "SELECT '519402230042000' AS ACCOUNT_NO,'C' AS DR_CR,CARD_NUMBER, AUTHORIZATION_CODE, FILEDATE, ARN, PURCHASE_DATE, DESTINATION_AMOUNT, MERCHANT_COUNTRY_CODE, SETTLEMENT_FLAG FROM SETTLEMENT_VISA_DOM_ISS_ATM_VISA \r\n"
						+ "WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + beanObj.getLocalDate()
						+ "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-DOM-ISS-ATM-CROSS-MATCHED-2'AND MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')\r\n";
			} else {
				getData1 = "SELECT '519402230044000' AS ACCOUNT_NO,'D' AS DR_CR, CARD_NUMBER,AUTHORIZATION_CODE, FILEDATE, ARN, PURCHASE_DATE, DESTINATION_AMOUNT, MERCHANT_COUNTRY_CODE, SETTLEMENT_FLAG FROM SETTLEMENT_VISA_INT_ISS_POS_VISA \r\nWHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate()
						+ "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-INT-ISS-POS-CROSS-MATCHED-2' AND MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')\r\n"
						+ "\r\n" + "UNION ALL\r\n"
						+ "SELECT '519402230045000' AS ACCOUNT_NO,'C' AS DR_CR, CARD_NUMBER,AUTHORIZATION_CODE, FILEDATE, ARN, PURCHASE_DATE, DESTINATION_AMOUNT, MERCHANT_COUNTRY_CODE, SETTLEMENT_FLAG FROM SETTLEMENT_VISA_INT_ISS_POS_VISA \r\n"
						+ "WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + beanObj.getLocalDate()
						+ "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-INT-ISS-POS-CROSS-MATCHED-2' AND MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')\r\n"
						+ "\r\n" + "UNION ALL\r\n" + "\r\n"
						+ "SELECT '519402230045000' AS ACCOUNT_NO,'D' AS DR_CR,CARD_NUMBER, AUTHORIZATION_CODE, FILEDATE, ARN, PURCHASE_DATE, DESTINATION_AMOUNT, MERCHANT_COUNTRY_CODE, SETTLEMENT_FLAG FROM SETTLEMENT_VISA_DOM_ISS_POS_VISA \r\n"
						+ "WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + beanObj.getLocalDate()
						+ "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-DOM-ISS-POS-CROSS-MATCHED-2'AND MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')\r\n"
						+ "UNION ALL\r\n"
						+ "SELECT '519402230044000' AS ACCOUNT_NO,'C' AS DR_CR,CARD_NUMBER, AUTHORIZATION_CODE, FILEDATE, ARN, PURCHASE_DATE, DESTINATION_AMOUNT, MERCHANT_COUNTRY_CODE, SETTLEMENT_FLAG FROM SETTLEMENT_VISA_DOM_ISS_POS_VISA \r\n"
						+ "WHERE TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('" + beanObj.getLocalDate()
						+ "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-DOM-ISS-POS-CROSS-MATCHED-2'AND MERCHANT_COUNTRY_CODE NOT IN('NP','BH','BT')";
			}
			DailyData = getJdbcTemplate().query(getData1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							int count = 1, dataCount = 0, datasum = 0;

							while (rs.next()) {
								// logger.info("Inside rset");

								Map<String, String> table_Data = new HashMap<String, String>();

								table_Data.put("TTUM", rs.getString("TTUM"));

							
								beanList.add(table_Data);

							}

							return beanList;
						}
					});
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getTTUMNFSTOVISACROSSROUNTING(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			getData1 = "SELECT 'D' AS DR_CR,AC_NO,LPAD(TRACE_NO,6,0) || ' | ' || TRAN_DATE || ' | ' || CARD_NO AS PARTICULARS, TO_NUMBER('1') AS TXN_COUNT, \r\nTO_NUMBER(REPLACE(AMOUNT,',','')) AMOUNT ,\r\n'VISA_ACQVISA_TO_NFSCREDIT '|| LPAD(TRACE_NO,6,0) || ' | ' || TRAN_DATE || ' | ' || CARD_NO AS NARRATION\r\nFROM SETTLEMENT_VISA_ACQ_ATM_CBS_NFS_ROUTING WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
					+

					beanObj.getLocalDate() + "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-ACQ-ATM-MATCHED-2-ROUTING'"
					+ "UNION ALL\r\n" + "\r\n"
					+ "SELECT 'C' AS DR_CR,'519408250122000' AS AC_NO,'Settlement A/c / GL :-519408250122000-VISA RECIVABLE  ACCOUNT' AS PARTICULARS, TO_NUMBER(COUNT(*)) AS TXN_COUNT, \r\n"
					+ "TO_NUMBER(SUM(REPLACE(AMOUNT,',',''))) AS AMOUNT,\r\n"
					+ "'VISA_ACQVISA_TO_NFSCREDIT '||TO_DATE('" + beanObj.getLocalDate()
					+ "' ,'DD-MM-YY')  AS NARRATION\r\n"
					+ "FROM SETTLEMENT_VISA_ACQ_ATM_CBS_NFS_ROUTING WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
					+ beanObj.getLocalDate() + "' ,'DD-MM-YY') AND DCRS_REMARKS='VISA-ACQ-ATM-MATCHED-2-ROUTING'";
			DailyData = getJdbcTemplate().query(getData1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							int count = 1, dataCount = 0, datasum = 0;

							while (rs.next()) {
								// logger.info("Inside rset");

								Map<String, String> table_Data = new HashMap<String, String>();

								table_Data.put("DR_CR", rs.getString("DR_CR"));

								table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
								table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
								table_Data.put("CARD_NO", rs.getString("CARD_NO"));
								table_Data.put("AC_NO", rs.getString("AC_NO"));
								table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
								table_Data.put("AMOUNT", rs.getString("AMOUNT"));
								table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));

								table_Data.put("NARRATION", rs.getString("NARRATION"));
								table_Data.put("FILEDATE", rs.getString("FILEDATE"));
								table_Data.put("REMARKS", rs.getString("REMARKS"));
								count++;

								beanList.add(table_Data);

							}

							return beanList;
						}
					});
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getTTUMVISANFSRUPAY(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			getData1 = "select  DR_CR,POSTED_DATE, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, AMOUNT, GL_ACCOUNT, NARRATION from NFSISS_RUPAYINT_ROUT_TTUM where TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY')";
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							int count = 1, dataCount = 0, datasum = 0;

							while (rs.next()) {
								// logger.info("Inside rset");

								Map<String, String> table_Data = new HashMap<String, String>();

								table_Data.put("DR_CR", rs.getString("DR_CR"));

								table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
								table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
								table_Data.put("CARD_NO", rs.getString("CARD_NO"));
								table_Data.put("AC_NO", rs.getString("AC_NO"));
								table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
								table_Data.put("AMOUNT", rs.getString("AMOUNT"));
								table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));

								table_Data.put("NARRATION", rs.getString("NARRATION"));
								table_Data.put("FILEDATE", rs.getString("FILEDATE"));
								table_Data.put("REMARKS", rs.getString("REMARKS"));
								count++;

								beanList.add(table_Data);

							}

							return beanList;
						}
					});
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getFinacleTTUM(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			getData1 = "select RESPONSE, ATMID, CARDNO, ACC_NO, RRN, SEQ_NUM, TXNDATE, TXNTIME, TXNAMOUNT, ADJAMOUNT, MSP_FINAL, CASH_VENDOR, ADJ_TYPE,FILEDATE from  FINA_FINACLE_TTUM where TO_DATE(FILEDATE ,'DD-MM-YY')=TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY')";
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							int count = 1, dataCount = 0, datasum = 0;

							while (rs.next()) {
								// logger.info("Inside rset");

								Map<String, String> table_Data = new HashMap<String, String>();

								table_Data.put("DR_CR", rs.getString("DR_CR"));

								table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
								table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
								table_Data.put("CARD_NO", rs.getString("CARD_NO"));
								table_Data.put("AC_NO", rs.getString("AC_NO"));
								table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
								table_Data.put("AMOUNT", rs.getString("AMOUNT"));
								table_Data.put("AUTH_CODE", rs.getString("AUTH_CODE"));

								table_Data.put("NARRATION", rs.getString("NARRATION"));
								table_Data.put("FILEDATE", rs.getString("FILEDATE"));
								table_Data.put("REMARKS", rs.getString("REMARKS"));
								count++;

								beanList.add(table_Data);

							}

							return beanList;
						}
					});
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getTTUMMASTERCARD(MastercardUploadBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getFileType().equalsIgnoreCase("DOMESTIC")) {
				getData1 = "select GL_CODE, PARTICULARS, DR_COUNT, DEBIT, CR_COUNT, CREDIT from  mastercard_settlement_ttum WHERE FILEDATE=STR_TO_DATE('"
						+ beanObj.getFileDate() + "','%Y/%m/%d') ";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									table_Data.put("GL_CODE", rs.getString("GL_CODE"));

									table_Data.put("PARTICULARS", rs.getString("PARTICULARS"));
									table_Data.put("DR_COUNT", rs.getString("DR_COUNT"));
									table_Data.put("DEBIT", rs.getString("DEBIT"));
									table_Data.put("CR_COUNT", rs.getString("CR_COUNT"));
									table_Data.put("CREDIT", rs.getString("CREDIT"));
								
									count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			} else if (beanObj.getFileType().equalsIgnoreCase("INTERNATIONAL")) {
				getData1 = "select GL_CODE, PARTICULARS, DR_COUNT, DEBIT, CR_COUNT, CREDIT from  mastercard_int_settlement_ttum WHERE FILEDATE=STR_TO_DATE('"
						+ beanObj.getFileDate() + "','%Y/%m/%d')";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								int count = 1, dataCount = 0, datasum = 0;

								while (rs.next()) {
									// logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();

									table_Data.put("GL_CODE", rs.getString("GL_CODE"));

									table_Data.put("PARTICULARS", rs.getString("PARTICULARS"));
									table_Data.put("DR_COUNT", rs.getString("DR_COUNT"));
									table_Data.put("DEBIT", rs.getString("DEBIT"));
									table_Data.put("CR_COUNT", rs.getString("CR_COUNT"));
									table_Data.put("CREDIT", rs.getString("CREDIT"));
										count++;

									beanList.add(table_Data);

								}

								return beanList;
							}
						});
			}
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getTTUMCTC(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getStSubCategory().equalsIgnoreCase("ISSUER")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					getData1 = "SELECT DR_CR,DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, TXN_DATE, TXN_TIME, AMOUNT, RRN, NARRATION, UID FROM c2c_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate().replaceAll("/", "-") + "','%Y-%m-%d')  AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
							+ "'";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
										table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));
										table_Data.put("UID", rs.getString("UID"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					getData1 = "SELECT DR_CR,DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, TXN_DATE, TXN_TIME, AMOUNT, RRN, NARRATION, UID FROM c2c_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate().replaceAll("/", "-") + "','%Y-%m-%d')  AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
							+ "'";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
										table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));
										table_Data.put("UID", rs.getString("UID"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				} else {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, TXN_DATE, TXN_TIME, AMOUNT, RRN, NARRATION FROM NFS_C2C_ISS_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE ='" + beanObj.getTypeOfTTUM()
							+ "' ";
					logger.info("data get sql " + getData1);
					DailyData = getJdbcTemplate().query(getData1, new Object[] {},
							new ResultSetExtractor<List<Object>>() {
								public List<Object> extractData(ResultSet rs) throws SQLException {
									List<Object> beanList = new ArrayList<Object>();
									int count = 1, dataCount = 0, datasum = 0;

									while (rs.next()) {
										// logger.info("Inside rset");

										Map<String, String> table_Data = new HashMap<String, String>();
										table_Data.put("DR_CR", rs.getString("DR_CR"));
										table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
										table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
										table_Data.put("CARD_NO", rs.getString("CARD_NO"));
										table_Data.put("AC_NO", rs.getString("AC_NO"));
										table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
										table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
										table_Data.put("AMOUNT", rs.getString("AMOUNT"));
										table_Data.put("RRN", rs.getString("RRN"));
										table_Data.put("NARRATION", rs.getString("NARRATION"));
										table_Data.put("UID", rs.getString("UID"));

										count++;

										beanList.add(table_Data);

									}

									return beanList;
								}
							});
				}
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
				getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION FROM NFS_C2C_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
						+ "' AND AMOUNT <> 0";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));
							table_Data.put("UID", rs.getString("UID"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
				getData1 = "SELECT DR_CR, DISPUTE_DATE, BANKNAME, CARD_NO, AC_NO, DR_CR, TXN_DATE, TXN_TIME, AMOUNT, RRN, TERMINAL_ID, NARRATION FROM NFS_C2C_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getTypeOfTTUM() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
						+ "' AND AMOUNT <> 0";
				logger.info("data get sql " + getData1);
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						int count = 1, dataCount = 0, datasum = 0;

						while (rs.next()) {
							// logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));
							table_Data.put("BANK_NAME", rs.getString("BANKNAME"));
							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("AC_NO", rs.getString("AC_NO"));
							table_Data.put("TXN_DATE", rs.getString("TXN_DATE"));
							table_Data.put("TXN_TIME", rs.getString("TXN_TIME"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));
							table_Data.put("UID", rs.getString("UID"));

							count++;

							beanList.add(table_Data);

						}

						return beanList;
					}
				});
			}
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getAdjTTUMSETTL(final NFSSettlementBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getAdjCategory().contains("ICD")) {
				getData1 = "SELECT GL_CODE,PARTICULARS,DR_COUNT,DEBIT,CR_COUNT,CREDIT,NARRATION,FILEDATE FROM   icd_settlement_report WHERE to_date(filedate,'DD-MM-YY') = TO_DATE('"
						+ beanObj.getDatepicker() + "','DD-MM-YY') order by GL_CODE";
			} else if (beanObj.getAdjCategory().contains("JCB")) {
				getData1 = "SELECT GL_ACCOUNT, INR, SIX_DIG_GL, D_C, NO_OF_TXNS, DEBIT, CREDIT, NARRATION, REMARKS, FILEDATE, PARTICULARS FROM jcb_settlement_report WHERE FILEDATE=STR_TO_DATE('"
						+ beanObj.getDatepicker() + "','%Y/%m/%d') ";
			} else if (beanObj.getAdjCategory().contains("DFS")) {
				getData1 = "SELECT GL_ACCOUNT, INR, SIX_DIG_GL, D_C, NO_OF_TXNS, DEBIT, CREDIT, NARRATION, REMARKS, FILEDATE, PARTICULARS FROM dfs_settlement_report WHERE FILEDATE=STR_TO_DATE('"
						+ beanObj.getDatepicker() + "','%Y/%m/%d')  ";
			} else {
				getData1 = "SELECT GL_ACCOUNT, INR, SIX_DIG_GL, D_C, NO_OF_TXNS, DEBIT, CREDIT, NARRATION, REMARKS, FILEDATE, PARTICULARS, CYCLE FROM nfs_settlement_report WHERE FILEDATE=STR_TO_DATE('"
						+ beanObj.getDatepicker() + "','%Y/%m/%d')  AND CYCLE='" + beanObj.getCycle() + "'";
			}
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					int count = 1, dataCount = 0, datasum = 0;

					while (rs.next()) {
						// logger.info("Inside rset");

						Map<String, String> table_Data = new HashMap<String, String>();

						logger.info("count " + count);

						table_Data.put("SR_NO", String.valueOf(count));

						table_Data.put("GL_ACCOUNT", rs.getString("GL_ACCOUNT"));

						table_Data.put("INR", rs.getString("INR"));
						table_Data.put("SIX_DIG_GL", rs.getString("SIX_DIG_GL"));
						table_Data.put("D_C", rs.getString("D_C"));
						table_Data.put("NO_OF_TXNS", rs.getString("NO_OF_TXNS"));
						table_Data.put("DEBIT", rs.getString("DEBIT"));
						table_Data.put("CREDIT", rs.getString("CREDIT"));
						table_Data.put("REMARKS", rs.getString("REMARKS"));
						table_Data.put("PARTICULARS", rs.getString("PARTICULARS"));
						table_Data.put("NARRATION", rs.getString("NARRATION"));
						table_Data.put("FILEDATE", rs.getString("FILEDATE"));
						
					
							table_Data.put("CYCLE", String.valueOf(beanObj.getCycle()));
							
						
						
						
						/*
						 * String maskCard = rs.getString("CARDNO"); maskCard =
						 * org.apache.commons.lang.StringUtils.overlay(maskCard,
						 * org.apache.commons.lang.StringUtils.repeat("X", maskCard.length() - 2), 6,
						 * maskCard.length() - 4);
						 */
						// table_Data.put("NARRATION", rs.getString("NARRATION"));

						count++;

						beanList.add(table_Data);

					}

					return beanList;
				}
			});
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getREEFUNDTTUM(final UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
				if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
					getData1 = "SELECT  DISPUTE_DATE, BANK_NAME, CARD_NO, ACCOUNT_NO, TRANSECTION_DATE,DR_CR, AMOUNT, RRN, NARRATION FROM  rupay_refund_report WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') AND  TYPE='RUPAY_TTUM'";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("RUPAY_OFFLINE")) {
					System.out.println("filedate passing for validation check " + beanObj.getFileDate());
					getData1 = "SELECT  DISPUTE_DATE, BANK_NAME, CARD_NO, ACCOUNT_NO, TRANSECTION_DATE,DR_CR, AMOUNT, RRN, NARRATION FROM rupay_refund_report WHERE filedate = str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') AND  TYPE='RUPAY_OFFLINE'";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
					System.out.println("filedate passing for validation check " + beanObj.getFileDate());
					getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from  rupay_adjustment_ttum where filedate =str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') and TTUM_TYPE='Rupay_CHB' ";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
					System.out.println("filedate passing for validation check " + beanObj.getFileDate());
					getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from rupay_adjustment_ttum where filedate = str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') and TTUM_TYPE='Rupay_CHBD'  ";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Partial Chargeback Acceptance")) {
					System.out.println("filedate passing for validation check " + beanObj.getFileDate());
					getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from rupay_adjustment_ttum where filedate = str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') and TTUM_TYPE='Rupay_PCHB'  ";
				}
			} else if (beanObj.getCategory().equalsIgnoreCase("RUPAY_INT_MFC")) {
				if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
					getData1 = "SELECT  DISPUTE_DATE, BANK_NAME, CARD_NO, ACCOUNT_NO, TRANSECTION_DATE,DR_CR, AMOUNT, RRN, NARRATION FROM  rupay_refund_report WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') AND  TYPE='RUPAY_INT_MFC_TTUM'";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("RUPAY_OFFLINE")) {
					System.out.println("filedate passing for validation check " + beanObj.getFileDate());
					getData1 = "SELECT  DISPUTE_DATE, BANK_NAME, CARD_NO, ACCOUNT_NO, TRANSECTION_DATE,DR_CR, AMOUNT, RRN, NARRATION FROM rupay_refund_report WHERE filedate = str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') AND  TYPE='RUPAY_OFFLINE'";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
					System.out.println("filedate passing for validation check " + beanObj.getFileDate());
					getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from  rupay_adjustment_ttum where filedate =str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') and TTUM_TYPE='Rupay_CHB' ";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
					System.out.println("filedate passing for validation check " + beanObj.getFileDate());
					getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from rupay_adjustment_ttum where filedate = str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') and TTUM_TYPE='Rupay_CHBD'  ";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Partial Chargeback Acceptance")) {
					System.out.println("filedate passing for validation check " + beanObj.getFileDate());
					getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from rupay_adjustment_ttum where filedate = str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') and TTUM_TYPE='Rupay_PCHB'  ";
				}
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
				getData1 = "SELECT  DISPUTE_DATE, BANK_NAME, CARD_NO, ACCOUNT_NO, TRANSECTION_DATE,DR_CR, AMOUNT, RRN, NARRATION FROM  rupay_refund_report WHERE filedate= str_to_date('"
						+ beanObj.getFileDate() + "','%Y/%m/%d') AND  TYPE='RUPAY_INT_TTUM'";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("RUPAY_OFFLINE")) {
				System.out.println("filedate passing for validation check " + beanObj.getFileDate());
				getData1 = "SELECT  DISPUTE_DATE, BANK_NAME, CARD_NO, ACCOUNT_NO, TRANSECTION_DATE,DR_CR, AMOUNT, RRN, NARRATION FROM rupay_refund_report WHERE filedate = str_to_date('"
						+ beanObj.getFileDate() + "','%Y/%m/%d') AND  TYPE='RUPAY_OFFLINE'";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
				System.out.println("filedate passing for validation check " + beanObj.getFileDate());
				getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from  rupay_adjustment_ttum where filedate =str_to_date('"
						+ beanObj.getFileDate() + "','%Y/%m/%d') and TTUM_TYPE='Rupay_CHB' ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
				System.out.println("filedate passing for validation check " + beanObj.getFileDate());
				getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from rupay_adjustment_ttum where filedate = str_to_date('"
						+ beanObj.getFileDate() + "','%Y/%m/%d') and TTUM_TYPE='Rupay_CHBD'  ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Partial Chargeback Acceptance")) {
				System.out.println("filedate passing for validation check " + beanObj.getFileDate());
				getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from rupay_adjustment_ttum where filedate = str_to_date('"
						+ beanObj.getFileDate() + "','%Y/%m/%d') and TTUM_TYPE='Rupay_PCHB'  ";
			}
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					int count = 1, dataCount = 0, datasum = 0;

					while (rs.next()) {
						// logger.info("Inside rset");

						Map<String, String> table_Data = new HashMap<String, String>();

						// logger.info("count " + count);
						if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {

							table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));

							table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));

							table_Data.put("CARD_NO", rs.getString("CARD_NO"));
							table_Data.put("ACCOUNT_NO", rs.getString("ACCOUNT_NO"));
							table_Data.put("TRANSECTION_DATE", rs.getString("TRANSECTION_DATE"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));
						} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
							table_Data.put("ACCOUNT_NO", rs.getString("ACCOUNT_NO"));

							table_Data.put("TRANSACTION_DATE", rs.getString("TRANSACTION_DATE"));

							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("ACCOUNT_NO", rs.getString("ACCOUNT_NO"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));

						} else {
							table_Data.put("ACCOUNT_NO", rs.getString("ACCOUNT_NO"));

							table_Data.put("TRANSACTION_DATE", rs.getString("TRANSACTION_DATE"));

							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("ACCOUNT_NO", rs.getString("ACCOUNT_NO"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));
						}
						/*
						 * String maskCard = rs.getString("CARDNO"); maskCard =
						 * org.apache.commons.lang.StringUtils.overlay(maskCard,
						 * org.apache.commons.lang.StringUtils.repeat("X", maskCard.length() - 2), 6,
						 * maskCard.length() - 4);
						 */
						// table_Data.put("NARRATION", rs.getString("NARRATION"));

						// count++;

						beanList.add(table_Data);

					}

					return beanList;
				}
			});

			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getREEFUNDTTUMVISA(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
				getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, ACCOUNT_NO, DATE_OF_TXN,Amount, RRN, NARRATION from VISA_REFUND_TTUM where to_date(filedate,'DD-MM-YY') = TO_DATE('"
						+ beanObj.getFileDate() + "','DD-MM-YY') AND TTUM_TYPE='VISA_DOM_REFUND' ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
				System.out.println("filedate passing for validation check " + beanObj.getFileDate());
				getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from RUPAY_ADJUSTMENT_TTUM where to_date(filedate,'DD-MM-YY') = TO_DATE('"
						+ beanObj.getFileDate() + "','DD-MM-YY') and TTUM_TYPE='Rupay_CHB' ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
				System.out.println("filedate passing for validation check " + beanObj.getFileDate());
				getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from RUPAY_ADJUSTMENT_TTUM where to_date(filedate,'DD-MM-YY') = TO_DATE('"
						+ beanObj.getFileDate() + "','DD-MM-YY') and TTUM_TYPE='Rupay_CHBD'  ";
			}
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1,
					new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								logger.info("Inside rset");

								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
								table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
								table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
								table_Data.put("TRANSACTION_PARTICULAR",
										rs.getString("TRANSACTION_PARTICULAR"));
								table_Data.put("REMARKS", rs.getString("REMARKS"));
								table_Data.put("FILEDATE", rs.getString("FILEDATE"));
								table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));

								beanList.add(table_Data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getREEFUNDTTUMVISAINT(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
				getData1 = "select DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NO, ACCOUNT_NO, DATE_OF_TXN,Amount, RRN, NARRATION from VISA_REFUND_TTUM where to_date(filedate,'DD-MM-YY') = TO_DATE('"
						+ beanObj.getFileDate() + "','DD-MM-YY') AND TTUM_TYPE='VISA_INT_REFUND' ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
				System.out.println("filedate passing for validation check " + beanObj.getFileDate());
				getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from RUPAY_ADJUSTMENT_TTUM where to_date(filedate,'DD-MM-YY') = TO_DATE('"
						+ beanObj.getFileDate() + "','DD-MM-YY') and TTUM_TYPE='Rupay_CHB' ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
				System.out.println("filedate passing for validation check " + beanObj.getFileDate());
				getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from RUPAY_ADJUSTMENT_TTUM where to_date(filedate,'DD-MM-YY') = TO_DATE('"
						+ beanObj.getFileDate() + "','DD-MM-YY') and TTUM_TYPE='Rupay_CHBD'  ";
			}
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1,
					new Object[] {  },
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								logger.info("Inside rset");

								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
								table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
								table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
								table_Data.put("TRANSACTION_PARTICULAR",
										rs.getString("TRANSACTION_PARTICULAR"));
								table_Data.put("REMARKS", rs.getString("REMARKS"));
								table_Data.put("FILEDATE", rs.getString("FILEDATE"));
								table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));

								beanList.add(table_Data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getREEFUNDTTUMISS(final UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD")) {
				if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NUMBER, ACCOUNT_NO, DATE_OF_TXN, AMOUNT, RRN, NARRATION FROM  mc_dom_refund_ttum WHERE FILEDATE  =  STR_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') and REF_TYPE='DOMESTIC'";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
					System.out.println("filedate passing for validation check " + beanObj.getFileDate());
					getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from rupay_adjustment_ttum where to_date(filedate,'DD-MM-YY') = TO_DATE('"
							+ beanObj.getFileDate() + "','DD-MM-YY') and TTUM_TYPE='QSPARC_CHB' ";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
					System.out.println("filedate passing for validation check " + beanObj.getFileDate());
					getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from rupay_adjustment_ttum where to_date(filedate,'DD-MM-YY') = TO_DATE('"
							+ beanObj.getFileDate() + "','DD-MM-YY') and TTUM_TYPE='QSPARC_CHBD'  ";
				}
			} else if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD_INT")) {
				if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
					getData1 = "SELECT DR_CR, DISPUTE_DATE, BANK_NAME, CARD_NUMBER, ACCOUNT_NO, DATE_OF_TXN, AMOUNT, RRN, NARRATION FROM mc_dom_refund_ttum WHERE FILEDATE  =  STR_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') and REF_TYPE='INTERNATIONAL'";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
					System.out.println("filedate passing for validation check " + beanObj.getFileDate());
					getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from rupay_adjustment_ttum where to_date(filedate,'DD-MM-YY') = TO_DATE('"
							+ beanObj.getFileDate() + "','DD-MM-YY') and TTUM_TYPE='QSPARC_CHB' ";
				} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
					System.out.println("filedate passing for validation check " + beanObj.getFileDate());
					getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from rupay_adjustment_ttum where to_date(filedate,'DD-MM-YY') = TO_DATE('"
							+ beanObj.getFileDate() + "','DD-MM-YY') and TTUM_TYPE='QSPARC_CHBD'  ";
				}
			} else if (beanObj.getCategory().equalsIgnoreCase("RUPAY_INT")) {
				if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND"))
					getData1 = "SELECT  DISPUTE_DATE, BANK_NAME, CARD_NO, ACCOUNT_NO, TRANSECTION_DATE,DR_CR, AMOUNT, RRN, NARRATION FROM rupay_refund_report WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') AND  TYPE='RUPAY_INT_TTUM'";
			} else if (beanObj.getCategory().equalsIgnoreCase("RUPAY_INT_MFC")) {
				if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND"))
					getData1 = "SELECT  DISPUTE_DATE, BANK_NAME, CARD_NO, ACCOUNT_NO, TRANSECTION_DATE,DR_CR, AMOUNT, RRN, NARRATION FROM rupay_refund_report WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d') AND  TYPE='RUPAY_INT_MFC_TTUM'";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
				getData1 = "SELECT  DISPUTE_DATE, BANK_NAME, CARD_NO, ACCOUNT_NO, TRANSECTION_DATE,DR_CR, AMOUNT, RRN, NARRATION FROM rupay_refund_report WHERE filedate= str_to_date('"
						+ beanObj.getFileDate() + "','%Y/%m/%d') AND  TYPE='QSPARC_TTUM'";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
				System.out.println("filedate passing for validation check " + beanObj.getFileDate());
				getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from rupay_adjustment_ttum where filedate = str_to_date('"
						+ beanObj.getFileDate() + "','%Y/%m/%d')  and TTUM_TYPE='QSPARC_CHB' ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Deemed Acceptance")) {
				System.out.println("filedate passing for validation check " + beanObj.getFileDate());
				getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from rupay_adjustment_ttum where filedate= str_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d')  and TTUM_TYPE='QSPARC_CHBD'  ";
			} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Partial Chargeback Acceptance")) {
				System.out.println("filedate passing for validation check " + beanObj.getFileDate());
				getData1 = "SELECT ACCOUNT_NO,TRANSACTION_DATE, RRN, DR_CR, AMOUNT,NARRATION from rupay_adjustment_ttum where to_date(filedate,'DD-MM-YY') = str_to_date('"
						+ beanObj.getFileDate() + "','%Y/%m/%d')  and TTUM_TYPE='QSPARC_PCHB'  ";
			}
			logger.info("data get sql " + getData1);
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					int count = 1, dataCount = 0, datasum = 0;

					while (rs.next()) {
						// logger.info("Inside rset");

						Map<String, String> table_Data = new HashMap<String, String>();
						if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("REFUND")) {
							if (beanObj.getCategory().equalsIgnoreCase("MASTERCARD")
									|| beanObj.getCategory().equalsIgnoreCase("MASTERCARD_INT")) {
								table_Data.put("DR_CR", rs.getString("DR_CR"));

								table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));

								table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));
								table_Data.put("CARD_NUMBER", rs.getString("CARD_NUMBER"));
								table_Data.put("ACCOUNT_NO", rs.getString("ACCOUNT_NO"));
								table_Data.put("DATE_OF_TXN", rs.getString("DATE_OF_TXN"));
								table_Data.put("AMOUNT", rs.getString("AMOUNT"));
								table_Data.put("RRN", rs.getString("RRN"));
								table_Data.put("NARRATION", rs.getString("NARRATION"));
							} else {
								table_Data.put("DISPUTE_DATE", rs.getString("DISPUTE_DATE"));

								table_Data.put("BANK_NAME", rs.getString("BANK_NAME"));

								table_Data.put("CARD_NO", rs.getString("CARD_NO"));
								table_Data.put("ACCOUNT_NO", rs.getString("ACCOUNT_NO"));
								table_Data.put("TRANSECTION_DATE", rs.getString("TRANSECTION_DATE"));
								table_Data.put("DR_CR", rs.getString("DR_CR"));
								table_Data.put("AMOUNT", rs.getString("AMOUNT"));
								table_Data.put("RRN", rs.getString("RRN"));
								table_Data.put("NARRATION", rs.getString("NARRATION"));

							}

						} else if (beanObj.getAcqtypeOfTTUM().equalsIgnoreCase("Chargeback Acceptance")) {
							table_Data.put("ACCOUNT_NO", rs.getString("ACCOUNT_NO"));

							table_Data.put("TRANSACTION_DATE", rs.getString("TRANSACTION_DATE"));

							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("ACCOUNT_NO", rs.getString("ACCOUNT_NO"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));

						} else {
							table_Data.put("ACCOUNT_NO", rs.getString("ACCOUNT_NO"));

							table_Data.put("TRANSACTION_DATE", rs.getString("TRANSACTION_DATE"));

							table_Data.put("RRN", rs.getString("RRN"));
							table_Data.put("DR_CR", rs.getString("DR_CR"));
							table_Data.put("AMOUNT", rs.getString("AMOUNT"));
							table_Data.put("ACCOUNT_NO", rs.getString("ACCOUNT_NO"));
							table_Data.put("NARRATION", rs.getString("NARRATION"));
						}
						/*
						 * String maskCard = rs.getString("CARDNO"); maskCard =
						 * org.apache.commons.lang.StringUtils.overlay(maskCard,
						 * org.apache.commons.lang.StringUtils.repeat("X", maskCard.length() - 2), 6,
						 * maskCard.length() - 4);
						 */
						// table_Data.put("NARRATION", rs.getString("NARRATION"));

						count++;

						beanList.add(table_Data);

					}

					return beanList;
				}
			});

			data.add(DailyData);	
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> getPBGBAdjTTUM(NFSSettlementBean beanObj) {List<Object> data = new ArrayList<Object>();
	try {
		String getData1 = null;// ,getData2 = null;
		List<Object> DailyData = new ArrayList<Object>();

		if (beanObj.getFileName().contains("NFS")) {
			if (beanObj.getAdjType().equals("PENALTY") || beanObj.getAdjType().equals("FEE")) {
				getData1 = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
						+ "rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR,"
						+ "LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
						+ ",to_char(SYSDATE,'DD/MM/YYYY') AS FILEDATE,ADJTYPE"
						+ " FROM NFS_ADJUSTMENT_TTUM_TEMP WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = ? "
						+ "AND upper(adjtype) like '%" + beanObj.getAdjType() + "%' AND BANK = 'PBGB'";

				DailyData = getJdbcTemplate().query(getData1,
						new Object[] { beanObj.getDatepicker(), beanObj.getStSubCategory() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
									table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
									table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
									table_Data.put("TRANSACTION_PARTICULAR",
											rs.getString("TRANSACTION_PARTICULAR"));
									table_Data.put("REMARKS", rs.getString("REMARKS"));
									table_Data.put("FILEDATE", rs.getString("FILEDATE"));
									table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));

									beanList.add(table_Data);
								}
								return beanList;
							}
						});
			} else {
				getData1 = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
						+ "rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR,"
						+ "LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
						+ ",to_char(SYSDATE,'DD/MM/YYYY') AS FILEDATE,ADJTYPE"
						+ " FROM NFS_ADJUSTMENT_TTUM_TEMP WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = ? "
						+ "AND (ADJTYPE) = ? AND adjtype not like '%Penalty%' AND (BANK = 'PBGB')";

				DailyData = getJdbcTemplate().query(getData1,
						new Object[] { beanObj.getDatepicker(), beanObj.getStSubCategory(), beanObj.getAdjType() },
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();

								while (rs.next()) {
									logger.info("Inside rset");

									Map<String, String> table_Data = new HashMap<String, String>();
									table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
									table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
									table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
									table_Data.put("TRANSACTION_PARTICULAR",
											rs.getString("TRANSACTION_PARTICULAR"));
									table_Data.put("REMARKS", rs.getString("REMARKS"));
									table_Data.put("FILEDATE", rs.getString("FILEDATE"));
									table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));

									beanList.add(table_Data);
								}
								return beanList;
							}
						});
			}
			/*
			 * getData2 = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE," +
			 * "TRANSACTION_AMOUNT as TRANSACTION_AMOUNT," +
			 * "TRANSACTION_PARTICULAR as TRANSACTION_PARTICULAR," +
			 * "REFERENCE_NUMBER AS REMARKS" +
			 * ",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE,ADJTYPE"
			 * +
			 * " FROM NFS_ADJUSTMENT_TTUM_TEMP WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = ? "
			 * + "AND UPPER(ADJTYPE) IN ('CHARGEBACK ACCEPTANCE', 'CREDIT ADJUSTMENT'," +
			 * "'GOOD FAITH CHARGEBACK DEEMED ACCEPTANCE')";
			 */
		}

		data.add(DailyData);

		// ADDING REPORT 2 DATA

		/*
		 * List<Object> DailyData2= getJdbcTemplate().query(getData2, new Object[]
		 * {beanObj.getDatepicker(),beanObj.getStSubCategory()}, new
		 * ResultSetExtractor<List<Object>>(){ public List<Object> extractData(ResultSet
		 * rs)throws SQLException { List<Object> beanList = new ArrayList<Object>();
		 * 
		 * while (rs.next()) { Map<String, String> table_Data = new HashMap<String,
		 * String>(); table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
		 * table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
		 * table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
		 * table_Data.put("TRANSACTION_PARTICULAR",
		 * rs.getString("TRANSACTION_PARTICULAR")); table_Data.put("REMARKS",
		 * rs.getString("REMARKS")); table_Data.put("FILEDATE",
		 * rs.getString("FILEDATE")); table_Data.put("ADJTYPE",
		 * rs.getString("ADJTYPE"));
		 * 
		 * beanList.add(table_Data); } return beanList; } });
		 * 
		 * data.add(DailyData2);
		 */

		return data;

	} catch (Exception e) {
		System.out.println("Exception in getInterchangeData " + e);
		return null;

	}
}
	

	public boolean rollBackACQCTC(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackACQCTCPROC rollBackexe = new rollBackACQCTCPROC( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	class rollBackACQCTCPROC extends StoredProcedure {
		private static final String procName = "NFS_ADJ_TTUM_ROLLBACK";

		rollBackACQCTCPROC(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
		
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}
	}

	public boolean rollBackTTUMRUPAY(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMRUPAY rollBackexe = new rollBackTTUMRUPAY( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	class rollBackTTUMRUPAY extends StoredProcedure {
		private static final String procName = "RUPAY_DECLINE_TTUM_ROLLBACK";

		rollBackTTUMRUPAY(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));
		
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}
	}
	public boolean rollBackFinacleTTUM(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			int uploadedCount = 0;
			String checkUpload = " delete from FINA_FINACLE_TTUM   where to_DATE(filedate,'DD-MM-YY')  = TO_date('"
					+ beanObj.getFileDate() + "','DD-MM-YY') ";
			uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class))
					.intValue();
			System.out.println("uploadedCount " + uploadedCount);
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			if (e.toString().contains("IncorrectResultSetColumnCountException"))
				return true;
			return false;
		}
	}

	
	public HashMap<String, Object> rollBackTTUMVISAPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA rollBackexe = new rollBackTTUMVISA( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}
	private class rollBackTTUMVISA extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_ORG_WDL_TTUM_ROLLBACK";

		public rollBackTTUMVISA(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA2 rollBackexe = new rollBackTTUMVISA2( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}
	private class rollBackTTUMVISA2 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_PROACTIVE_TTUM_ROLLBACK";

		public rollBackTTUMVISA2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA3(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA3 rollBackexe = new rollBackTTUMVISA3( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}
	private class rollBackTTUMVISA3 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_DROP_TTUM_ROLLBACK";

		public rollBackTTUMVISA3(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA4(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA4 rollBackexe = new rollBackTTUMVISA4( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}
	
	private class rollBackTTUMVISA4 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_DR_SURCH_TTUM_ROLLBACK";

		public rollBackTTUMVISA4(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA5(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA5 rollBackexe = new rollBackTTUMVISA5( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}
	private class rollBackTTUMVISA5 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_CR_SURCH_TTUM_ROLLBACK";

		public rollBackTTUMVISA5(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}


	public HashMap<String, Object> rollBackTTUMVISA6(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA6 rollBackexe = new rollBackTTUMVISA6( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA6 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_LATE_PRESENTMENT_TTUM_ROLLBACK";

		public rollBackTTUMVISA6(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA7(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA7 rollBackexe = new rollBackTTUMVISA7( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA7 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_DOM_REFUND_TTUM_ROLLBACK";

		public rollBackTTUMVISA7(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> rollBackTTUMVISA8(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA8 rollBackexe = new rollBackTTUMVISA8( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}
	private class rollBackTTUMVISA8 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_DOM_REFUND_REV_TTUM_ROLLBACK";

		public rollBackTTUMVISA8(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}

	
	public HashMap<String, Object> rollBackTTUMVISA9(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA9 rollBackexe = new rollBackTTUMVISA9( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA9 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_INT_REFUND_TTUM_ROLLBACK";

		public rollBackTTUMVISA9(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA10(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA10 rollBackexe = new rollBackTTUMVISA10( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA10 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_INT_REFUND_REV_TTUM_ROLLBACK";

		public rollBackTTUMVISA10(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> rollBackTTUMVISA(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA rollBackexe = new rollBackTTUMVISA( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}


	

	private class rollBackTTUMVISAPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_ATM_ORG_WDL_TTUM_ROLLBACK";

		public rollBackTTUMVISAPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA2POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA2POS rollBackexe = new rollBackTTUMVISA2POS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA2POS extends StoredProcedure {
		private static final String insert_proc = "VISA_ACQ_DOM_ATM_LORO_CREDIT_TTUM_ROLLBACK";

		public rollBackTTUMVISA2POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> rollBackTTUMVISA3POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA3POS rollBackexe = new rollBackTTUMVISA3POS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	
	
	private class rollBackTTUMVISA3POS extends StoredProcedure {
		private static final String insert_proc = "VISA_ACQ_DOM_ATM_LORO_DEBIT_TTUM_ROLLBACK";

		public rollBackTTUMVISA3POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA4POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA4POS rollBackexe = new rollBackTTUMVISA4POS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA4POS extends StoredProcedure {
		private static final String insert_proc = "VISA_ACQ_INT_ATM_LORO_DEBIT_TTUM_ROLLBACK";

		public rollBackTTUMVISA4POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA5POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA5POS rollBackexe = new rollBackTTUMVISA5POS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}
	private class rollBackTTUMVISA5POS extends StoredProcedure {
		private static final String insert_proc = "VISA_ACQ_INT_ATM_LORO_CREDIT_TTUM_ROLLBACK";

		public rollBackTTUMVISA5POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> rollBackTTUMVISA6POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA6POS rollBackexe = new rollBackTTUMVISA6POS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}


	private class rollBackTTUMVISA6POS extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_POS_LATE_PRESENTMENT_TTUM_ROLLBACK";

		public rollBackTTUMVISA6POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA7POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA7POS rollBackexe = new rollBackTTUMVISA7POS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA7POS extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_POS_REFUND_TTUM_ROLLBACK";

		public rollBackTTUMVISA7POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISAINT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISAINT rollBackexe = new rollBackTTUMVISAINT( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}
	
	private class rollBackTTUMVISAINT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_ORG_WDL_TTUM_ROLLBACK";

		public rollBackTTUMVISAINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> rollBackTTUMVISA2INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA2INT rollBackexe = new rollBackTTUMVISA2INT( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA2INT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_PROACTIVE_TTUM_ROLLBACK";

		public rollBackTTUMVISA2INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> rollBackTTUMVISA3INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA3INT rollBackexe = new rollBackTTUMVISA3INT(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA3INT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_DROP_TTUM_ROLLBACK";

		public rollBackTTUMVISA3INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA4INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA4INT rollBackexe = new rollBackTTUMVISA4INT( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}
	private class rollBackTTUMVISA4INT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_DR_SURCH_TTUM_ROLLBACK";

		public rollBackTTUMVISA4INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}


	public HashMap<String, Object> rollBackTTUMVISA5INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA5INT rollBackexe = new rollBackTTUMVISA5INT( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}
	private class rollBackTTUMVISA5INT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_CR_SURCH_TTUM_ROLLBACK";

		public rollBackTTUMVISA5INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA6INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA6INT rollBackexe = new rollBackTTUMVISA6INT( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA6INT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_LATE_PRESENTMENT_TTUM_ROLLBACK";

		public rollBackTTUMVISA6INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}


	public HashMap<String, Object> rollBackTTUMVISA7INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA7INT rollBackexe = new rollBackTTUMVISA7INT( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}
	
	private class rollBackTTUMVISA7INT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_DROP_CHARGES_TTUM_ROLLBACK";

		public rollBackTTUMVISA7INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}


	public HashMap<String, Object> rollBackTTUMVISA8INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA8INT rollBackexe = new rollBackTTUMVISA8INT( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}


	private class rollBackTTUMVISA8INT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_PROACTIVE_CHARGES_TTUM_ROLLBACK";

		public rollBackTTUMVISA8INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> rollBackTTUMVISAINTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISAINTPOS rollBackexe = new rollBackTTUMVISAINTPOS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISAINTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_ORG_WDL_TTUM_ROLLBACK";

		public rollBackTTUMVISAINTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA2INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA2INTPOS rollBackexe = new rollBackTTUMVISA2INTPOS( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA2INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_PROACTIVE_TTUM_ROLLBACK";

		public rollBackTTUMVISA2INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA3INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA3INTPOS rollBackexe = new rollBackTTUMVISA3INTPOS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	

	private class rollBackTTUMVISA3INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_DROP_TTUM_ROLLBACK";

		public rollBackTTUMVISA3INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA4INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA4INTPOS rollBackexe = new rollBackTTUMVISA4INTPOS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}
	
	private class rollBackTTUMVISA4INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_DR_SURCH_TTUM_ROLLBACK";

		public rollBackTTUMVISA4INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}


	public HashMap<String, Object> rollBackTTUMVISA5INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA5INTPOS rollBackexe = new rollBackTTUMVISA5INTPOS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA5INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_CR_SURCH_TTUM_ROLLBACK";

		public rollBackTTUMVISA5INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA6INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA6INTPOS rollBackexe = new rollBackTTUMVISA6INTPOS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	
	private class rollBackTTUMVISA6INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_LATE_PRESENTMENT_TTUM_ROLLBACK";

		public rollBackTTUMVISA6INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA10INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA10INTPOS rollBackexe = new rollBackTTUMVISA10INTPOS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}
	private class rollBackTTUMVISA10INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_LATE_PRESENTMENT_CHARGES_TTUM_ROLLBACK";

		public rollBackTTUMVISA10INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}


	public HashMap<String, Object> rollBackTTUMVISA7INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA7INTPOS rollBackexe = new rollBackTTUMVISA7INTPOS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	
	private class rollBackTTUMVISA7INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_DROP_CHARGES_TTUM_ROLLBACK";

		public rollBackTTUMVISA7INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA8INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA8INTPOS rollBackexe = new rollBackTTUMVISA8INTPOS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA8INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_PROACTIVE_CHARGES_TTUM_ROLLBACK";

		public rollBackTTUMVISA8INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> rollBackTTUMVISA9INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			rollBackTTUMVISA9INTPOS rollBackexe = new rollBackTTUMVISA9INTPOS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("msg"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollBackTTUMVISA9INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_REFUND_TTUM_ROLLBACK";

		public rollBackTTUMVISA9INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMQSPARC(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMQSPARC rollBackexe = new rollBackTTUMQSPARC( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	
	
	private class rollBackTTUMQSPARC extends StoredProcedure {
		private static final String insert_proc = "QSPARC_DECLINE_TTUM_ROLLBACK";

		public rollBackTTUMQSPARC(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}


	public boolean rollBackTTUMRUPAYINT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMRUPAYINT rollBackexe = new rollBackTTUMRUPAYINT( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackTTUMRUPAYINT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_DECLINE_TTUM_ROLLBACK";

		public rollBackTTUMRUPAYINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMNFS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMNFS rollBackexe = new rollBackTTUMNFS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if (outParams.get("msg") != "SUCCESS")
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackTTUMNFS extends StoredProcedure {
		private static final String insert_proc = "NFS_ISS_RECON_TTUM_ROLLBACK";

		public rollBackTTUMNFS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}

	
	public boolean rollBackTTUMJCB(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMJCB rollBackexe = new rollBackTTUMJCB( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if (outParams.get("msg") != "SUCCESS")
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackTTUMJCB extends StoredProcedure {
		private static final String insert_proc = "JCB_ACQ_RECON_TTUM_ROLLBACK";

		public rollBackTTUMJCB(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}public boolean rollBackTTUMDFS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMDFS rollBackexe = new rollBackTTUMDFS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if (outParams.get("msg") != "SUCCESS")
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackTTUMDFS extends StoredProcedure {
		private static final String insert_proc = "DFS_ACQ_RECON_TTUM_ROLLBACK";

		public rollBackTTUMDFS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg",  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMCTC(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMCTC rollBackexe = new rollBackTTUMCTC( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if ((outParams != null && outParams.get("msg") != null) || outParams.get("msg") != "SUCCESS")
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMCTC extends StoredProcedure {
		private static final String insert_proc = "C2C_ACQ_RECON_TTUM_ROLLBACK";

		public rollBackTTUMCTC(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMICD(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMICD rollBackexe = new rollBackTTUMICD( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackTTUMICD extends StoredProcedure {
		private static final String insert_proc = "ICD_ISS_UNRECON_NPCI_ROLLBACK";

		public rollBackTTUMICD(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMICCW(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMICCW rollBackexe = new rollBackTTUMICCW( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			if (outParams.get("msg") == "SUCCESS") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackTTUMICCW extends StoredProcedure {
		private static final String insert_proc = "NFS_ICCW_ISS_RECON_TTUM_ROLLBACK";

		public rollBackTTUMICCW(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMICCW2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMICCW2 rollBackexe = new rollBackTTUMICCW2( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			
			if (outParams.get("msg") == "SUCCESS") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackTTUMICCW2 extends StoredProcedure {
		private static final String insert_proc = "NFS_ICCW_ACQ_RECON_TTUM_ROLLBACK";

		public rollBackTTUMICCW2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMICD2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMICD2 rollBackexe = new rollBackTTUMICD2( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if ((outParams != null && outParams.get("msg") != null) || outParams.get("msg") != "SUCCESS")
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMICD2 extends StoredProcedure {
		private static final String insert_proc = "ICD_ISS_UNRECON_CBS_ROLLBACK";

		public rollBackTTUMICD2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMICD3(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMICD3 rollBackexe = new rollBackTTUMICD3( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if ((outParams != null && outParams.get("msg") != null) || outParams.get("msg") != "SUCCESS")
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackTTUMICD3 extends StoredProcedure {
		private static final String insert_proc = "ICD_ACQ_UNRECON_NPCI_ROLLBACK";

		public rollBackTTUMICD3(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}


	public boolean rollBackTTUMICD4(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMICD4 rollBackexe = new rollBackTTUMICD4( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			if ((outParams != null && outParams.get("msg") != null) || outParams.get("msg") == "SUCCESS")
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackTTUMICD4 extends StoredProcedure {
		private static final String insert_proc = "ICD_ACQ_UNRECON_CBS_ROLLBACK";

		public rollBackTTUMICD4(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMCTC2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMCTC2 rollBackexe = new rollBackTTUMCTC2( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if ((outParams != null && outParams.get("msg") != null) || outParams.get("msg") != "SUCCESS")
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMCTC2 extends StoredProcedure {
		private static final String insert_proc = "C2C_ISS_RECON_TTUM_ROLLBACK";

		public rollBackTTUMCTC2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMNFSRUAPY(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			System.out.println("date" + beanObj.getLocalDate());
			rollBackTTUMNFSRUAPY rollBackexe = new rollBackTTUMNFSRUAPY( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if ((outParams != null && outParams.get("msg") != null) || outParams.get("msg") != "SUCCESS")
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMNFSRUAPY extends StoredProcedure {
		private static final String insert_proc = "NFSISS_RUPAYINT_ROLLBACK";

		public rollBackTTUMNFSRUAPY(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> mapSwitchDATA(String date) {
		Map<String, Object> inParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			mapSwitchDATA exe = new mapSwitchDATA( getJdbcTemplate());
			inParams.put("FILEDT", date);
			outParams2 = exe.execute(inParams);
			logger.info("map  " + outParams2.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("MSG", outParams2.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("MSG", "Exception  ");
			return output;
		}
		return output;
	}
	private class mapSwitchDATA extends StoredProcedure {
		private static final String insert_proc = "SWITCH_DATA_MAPPING_NEW";

		public mapSwitchDATA(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("MSG",  Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> mapSwitchDATA1(String date) {
		Map<String, Object> inParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			logger.info("runAdjTTUM EXCEL");
			mapSwitchDATA1 exe = new mapSwitchDATA1( getJdbcTemplate());
			inParams.put("FILEDT", date);
			outParams2 = exe.execute(inParams);
			logger.info("map  " + outParams2.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("MSG", outParams2.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception is " + e);
			output.put("result", Boolean.valueOf(true));
			output.put("MSG", "Exception  ");
			return output;
		}
		return output;
	}
	private class mapSwitchDATA1 extends StoredProcedure {
		private static final String insert_proc = "CBS_DATA_MAPPING_NEW";

		public mapSwitchDATA1(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));

			declareParameter(new SqlOutParameter("MSG",  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMNFS2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMNFS2 rollBackexe = new rollBackTTUMNFS2( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			String value = outParams.toString();
			if (value.contains("NOT"))
				return false;
			if (value.contains("ALL"))
				return false;
			if (value.contains("SUCCESS"))
				return true;
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMNFS2 extends StoredProcedure {
		private static final String insert_proc = "NFS_ACQ_RECON_TTUM_ROLLBACK";

		public rollBackTTUMNFS2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMRUPAY2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMRUPAY2 rollBackexe = new rollBackTTUMRUPAY2( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackTTUMRUPAY2 extends StoredProcedure {
		private static final String insert_proc = "RUPAY_PROACTIVE_TTUM_ROLLBACK";

		public rollBackTTUMRUPAY2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMMC2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMMC2 rollBackexe = new rollBackTTUMMC2( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	
	public boolean rollBackTTUMMC2INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMMC2INT rollBackexe = new rollBackTTUMMC2INT( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackTTUMMC2INT extends StoredProcedure {
		private static final String insert_proc = "MC_ACQ_INT_LORO_DEBIT_TTUM_ROLLBACK";

		public rollBackTTUMMC2INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMMC4INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMMC4INT rollBackexe = new rollBackTTUMMC4INT( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackTTUMMC4INT extends StoredProcedure {
		private static final String insert_proc = "MC_SURCHARGE_CR_ROLLBACK";

		public rollBackTTUMMC4INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	private class rollBackTTUMMC2 extends StoredProcedure {
		private static final String insert_proc = "MC_ACQ_DOM_LORO_CREDIT_TTUM_ROLLBACK";

		public rollBackTTUMMC2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMMC2POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMMC2POS rollBackexe = new rollBackTTUMMC2POS( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMMC2POS extends StoredProcedure {
		private static final String insert_proc = "MC_PROACTIV_POS_ROLLBACK";

		public rollBackTTUMMC2POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMMC3(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMMC3 rollBackexe = new rollBackTTUMMC3( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackTTUMMC3 extends StoredProcedure {
		private static final String insert_proc = "MC_ACQ_DOM_LORO_DEBIT_TTUM_ROLLBACK";

		public rollBackTTUMMC3(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMMC3CROSS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMMC3CROSS rollBackexe = new rollBackTTUMMC3CROSS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackTTUMMC3CROSS extends StoredProcedure {
		private static final String insert_proc = "RECON_MC_ACQ_CROSS_RECON_ROLLBACK";

		public rollBackTTUMMC3CROSS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMMC3INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMMC3INT rollBackexe = new rollBackTTUMMC3INT( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackTTUMMC3INT extends StoredProcedure {
		private static final String insert_proc = "MC_ACQ_INT_LORO_CREDIT_TTUM_ROLLBACK";

		public rollBackTTUMMC3INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	public boolean rollBackTTUMMC3POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMMC3POS rollBackexe = new rollBackTTUMMC3POS( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null &&  outParams.get("msg") == "SUCCESS") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMMC3POS extends StoredProcedure {
		private static final String insert_proc = "MASTERCARD_ISS_POS_DROP_ROLLBACK";

		public rollBackTTUMMC3POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMRUPAY2INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMRUPAY2INT rollBackexe = new rollBackTTUMRUPAY2INT( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMRUPAY2INT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_PROACTIVE_TTUM_ROLLBACK";

		public rollBackTTUMRUPAY2INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMQSPARC2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMQSPARC2 rollBackexe = new rollBackTTUMQSPARC2( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackTTUMQSPARC2 extends StoredProcedure {
		private static final String insert_proc = "QSPARC_DOM_PROACTIVE_TTUM_ROLLBACK";

		public rollBackTTUMQSPARC2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMQSPARC2INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMQSPARC2INT rollBackexe = new rollBackTTUMQSPARC2INT( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackTTUMQSPARC2INT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_PROACTIVE_TTUM_ROLLBACK";

		public rollBackTTUMQSPARC2INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMRUPAY3(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMRUPAY3 rollBackexe = new rollBackTTUMRUPAY3( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackTTUMRUPAY3 extends StoredProcedure {
		private static final String insert_proc = "RUPAY_DOM_DROP_TTUM_ROLLBACK";

		public rollBackTTUMRUPAY3(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMRUPAY3INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMRUPAY3INT rollBackexe = new rollBackTTUMRUPAY3INT( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackTTUMRUPAY3INT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_DROP_TTUM_ROLLBACK";

		public rollBackTTUMRUPAY3INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMQSPARC3(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMQSPARC3 rollBackexe = new rollBackTTUMQSPARC3( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMQSPARC3 extends StoredProcedure {
		private static final String insert_proc = "QSPARC_DOM_DROP_TTUM_ROLLBACK";

		public rollBackTTUMQSPARC3(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMQSPARC3INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMQSPARC3INT rollBackexe = new rollBackTTUMQSPARC3INT( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMQSPARC3INT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_DROP_TTUM_ROLLBACK";

		public rollBackTTUMQSPARC3INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMRUPAY6(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMRUPAY6 rollBackexe = new rollBackTTUMRUPAY6( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMRUPAY6 extends StoredProcedure {
		private static final String insert_proc = "POS_DOME_LATE_PRESM_TTUM_ROLLBACK";

		public rollBackTTUMRUPAY6(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMRUPAY6INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMRUPAY6 rollBackexe = new rollBackTTUMRUPAY6( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	

	private class rollBackTTUMRUPAY6INT extends StoredProcedure {
		private static final String insert_proc = "POS_INT_LATE_PRESM_TTUM_ROLLBACK";

		public rollBackTTUMRUPAY6INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}


	public boolean rollBackTTUMMC6POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMMC6POS rollBackexe = new rollBackTTUMMC6POS( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackTTUMMC6POS extends StoredProcedure {
		private static final String insert_proc = "MASTERCARD_POS_ISS_LATEPRE_ROLLBACK";

		public rollBackTTUMMC6POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMMC10POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMMC10POS rollBackexe = new rollBackTTUMMC10POS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackTTUMMC10POS extends StoredProcedure {
		private static final String insert_proc = "MASTERCARD_ISS_ATM_SURCH_ROLLBACK";

		public rollBackTTUMMC10POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}


	public boolean rollBackTTUMQSPARC6(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMQSPARC6 rollBackexe = new rollBackTTUMQSPARC6( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackTTUMQSPARC6 extends StoredProcedure {
		private static final String insert_proc = "QSPARC_DOME_LATE_PRESM_TTUM_ROLLBACK";

		public rollBackTTUMQSPARC6(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMRUPAY7(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMRUPAY7 rollBackexe = new rollBackTTUMRUPAY7( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMRUPAY7 extends StoredProcedure {
		private static final String insert_proc = "RUPAY_DOM_OFFLINE_PRES_TTUM_ROLLBACK";

		public rollBackTTUMRUPAY7(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMRUPAY7INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMRUPAY7INT rollBackexe = new rollBackTTUMRUPAY7INT( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMRUPAY7INT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_OFFLINE_PRES_TTUM_ROLLBACK";

		public rollBackTTUMRUPAY7INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMQSPARC7(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMQSPARC7 rollBackexe = new rollBackTTUMQSPARC7( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	

	private class rollBackTTUMQSPARC7 extends StoredProcedure {
		private static final String insert_proc = "QSPARC_DOM_OFFLINE_PRES_TTUM_ROLLBACK";

		public rollBackTTUMQSPARC7(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMRUPAY4(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMRUPAY4 rollBackexe = new rollBackTTUMRUPAY4( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	
	private class rollBackTTUMRUPAY4 extends StoredProcedure {
		private static final String insert_proc = "RUPAY_DOM_SURCH_ROLLBACK";

		public rollBackTTUMRUPAY4(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackTTUMMC4(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMMC4 rollBackexe = new rollBackTTUMMC4( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	
	private class rollBackTTUMMC4 extends StoredProcedure {
		private static final String insert_proc = "MASTERCARD_ISS_POS_SURCH_ROLLBACK";

		public rollBackTTUMMC4(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}


	public boolean rollBackTTUMMC4DR(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMMC4DR rollBackexe = new rollBackTTUMMC4DR( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackTTUMMC4DR extends StoredProcedure {
		private static final String insert_proc = "MC_SURCHARGE_DR_ROLLBACK";

		public rollBackTTUMMC4DR(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMQSPARC4(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMQSPARC4 rollBackexe = new rollBackTTUMQSPARC4( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	
	private class rollBackTTUMQSPARC4 extends StoredProcedure {
		private static final String insert_proc = "QSPARC_DOM_SURCH_ROLLBACK";

		public rollBackTTUMQSPARC4(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMRUPAY4INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackTTUMRUPAY4INT rollBackexe = new rollBackTTUMRUPAY4INT( getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackTTUMRUPAY4INT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_SURCH_ROLLBACK";

		public rollBackTTUMRUPAY4INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackTTUMRUPAY5(UnMatchedTTUMBean beanObj) {
		int Count = 0, uploadedCount = 0;
		try {
			String checkUpload = " delete from RUPAY_DOM_CR_SURCH_TTUM   where to_DATE(filedate,'DD-MM-YY')  = TO_date('"
					+ beanObj.getFileDate() + "','DD-MM-YY') ";
			uploadedCount = ((Integer) getJdbcTemplate().queryForObject(checkUpload, new Object[0], Integer.class))
					.intValue();
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			if (e.toString().contains("IncorrectResultSetColumnCountException"))
				return true;
			return false;
		}
	}

	
	public boolean rollBackACQRupay(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackACQRupay rollBackexe = new rollBackACQRupay( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackACQRupay extends StoredProcedure {
		private static final String insert_proc = "RUPAY_DOM_ROLLBACK";

		public rollBackACQRupay(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackINTRupay(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackINTRupay rollBackexe = new rollBackINTRupay( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackINTRupay extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_ROLLBACK";

		public rollBackINTRupay(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	
	public boolean rollBackQSPARC(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackQSPARC rollBackexe = new rollBackQSPARC( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackQSPARC extends StoredProcedure {
		private static final String insert_proc = "QSPARC_DOM_ISS_ROLLBACK";

		public rollBackQSPARC(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackNFSISS(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackNFSISS rollBackexe = new rollBackNFSISS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackNFSISS extends StoredProcedure {
		private static final String insert_proc = "NFS_ISS_ROLLBACK";

		public rollBackNFSISS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}


	public boolean rollBackICDISS(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackICDISS rollBackexe = new rollBackICDISS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	public boolean rollBackICDACQ(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackICDACQ rollBackexe = new rollBackICDACQ( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	
	private class rollBackICDACQ extends StoredProcedure {
		private static final String insert_proc = "ICD_ACQ_ROLLBACK";

		public rollBackICDACQ(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	private class rollBackICDISS extends StoredProcedure {
		private static final String insert_proc = "ICD_ISS_ROLLBACK";

		public rollBackICDISS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	

	public boolean rollBackICCWACQ(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackICCWACQ rollBackexe = new rollBackICCWACQ( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	public boolean rollBackICCWISS(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackICCWISS rollBackexe = new rollBackICCWISS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackICCWISS extends StoredProcedure {
		private static final String insert_proc = "NFS_ICCW_ISS_ROLLBACK";

		public rollBackICCWISS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	
	private class rollBackICCWACQ extends StoredProcedure {
		private static final String insert_proc = "NFS_ICCW_ACQ_ROLLBACK";

		public rollBackICCWACQ(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackNFSACQ(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackNFSACQ rollBackexe = new rollBackNFSACQ( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackNFSACQ extends StoredProcedure {
		private static final String insert_proc = "NFS_ACQ_ROLLBACK";

		public rollBackNFSACQ(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackNFSDFSACQ(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackNFDFSACQ rollBackexe = new rollBackNFDFSACQ( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackNFDFSACQ extends StoredProcedure {
		private static final String insert_proc = "RECON_DFS_PROC_ROLLBACK";

		public rollBackNFDFSACQ(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
		
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackNFSJCBACQ(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackNFSJCBACQ rollBackexe = new rollBackNFSJCBACQ( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackNFSJCBACQ extends StoredProcedure {
		private static final String insert_proc = "RECON_JCB_PROC_ROLLBACK";

		public rollBackNFSJCBACQ(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackVISAISSATM(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackVISAISSATM rollBackexe = new rollBackVISAISSATM( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	
	private class rollBackVISAISSATM extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_ROLLBACK";

		public rollBackVISAISSATM(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackVISACQ(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackVISACQ rollBackexe = new rollBackVISACQ( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	
	public boolean rollBackVISACQ1(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackVISACQ1 rollBackexe = new rollBackVISACQ1( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackVISACQ1 extends StoredProcedure {
		private static final String insert_proc = "VISA_ACQ_INT_ATM_ROLLBACK";

		public rollBackVISACQ1(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	private class rollBackVISACQ extends StoredProcedure {
		private static final String insert_proc = "VISA_ACQ_DOM_ATM_ROLLBACK";

		public rollBackVISACQ(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackVISAISSINTPOS(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackVISAISSINTPOS rollBackexe = new rollBackVISAISSINTPOS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	
	private class rollBackVISAISSINTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ISS_POS_ROLLBACK";

		public rollBackVISAISSINTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackVISAISSPOS(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackVISAISSPOS rollBackexe = new rollBackVISAISSPOS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackVISAISSPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_ISS_POS_ROLLBACK";

		public rollBackVISAISSPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean CROSS_RECON_VISA_POS_INT_DOM_ROLLBACK(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			CROSS_RECON_VISA_POS_INT_DOM_ROLLBACK rollBackexe = new CROSS_RECON_VISA_POS_INT_DOM_ROLLBACK(
					getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class CROSS_RECON_VISA_POS_INT_DOM_ROLLBACK extends StoredProcedure {
		private static final String insert_proc = "CROSS_RECON_VISA_POS_INT_DOM_ROLLBACK";

		public CROSS_RECON_VISA_POS_INT_DOM_ROLLBACK(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean CROSS_RECON_VISA_POS_DOM_INT_ROLLBACK(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			CROSS_RECON_VISA_POS_DOM_INT_ROLLBACK rollBackexe = new CROSS_RECON_VISA_POS_DOM_INT_ROLLBACK(
					getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class CROSS_RECON_VISA_POS_DOM_INT_ROLLBACK extends StoredProcedure {
		private static final String insert_proc = "CROSS_RECON_VISA_POS_DOM_INT_ROLLBACK";

		public CROSS_RECON_VISA_POS_DOM_INT_ROLLBACK(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean CROSS_RECON_VISA_ATM_DOM_INT_ROLLBACK(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			CROSS_RECON_VISA_ATM_DOM_INT_ROLLBACK rollBackexe = new CROSS_RECON_VISA_ATM_DOM_INT_ROLLBACK(
					getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class CROSS_RECON_VISA_ATM_DOM_INT_ROLLBACK extends StoredProcedure {
		private static final String insert_proc = "CROSS_RECON_VISA_ATM_DOM_INT_ROLLBACK";

		public CROSS_RECON_VISA_ATM_DOM_INT_ROLLBACK(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean CROSS_RECON_VISA_ATM_INT_DOM_ROLLBACK(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			CROSS_RECON_VISA_ATM_INT_DOM_ROLLBACK rollBackexe = new CROSS_RECON_VISA_ATM_INT_DOM_ROLLBACK(
					getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class CROSS_RECON_VISA_ATM_INT_DOM_ROLLBACK extends StoredProcedure {
		private static final String insert_proc = "CROSS_RECON_VISA_ATM_INT_DOM_ROLLBACK";

		public CROSS_RECON_VISA_ATM_INT_DOM_ROLLBACK(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	
	public boolean rollBackVISAISSINTATM(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackVISAISSINTATM rollBackexe = new rollBackVISAISSINTATM( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackVISAISSINTATM extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ISS_ATM_ROLLBACK";

		public rollBackVISAISSINTATM(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackMASTERCARDISSATM(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackMASTERCARDISSATM rollBackexe = new rollBackMASTERCARDISSATM( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackMASTERCARDISSATM extends StoredProcedure {
		private static final String insert_proc = "RECON_MC_ISS_POS_ROLLBACK";

		public rollBackMASTERCARDISSATM(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}


	public boolean rollBackMASTERCARDISSPOS(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackMASTERCARDISSPOS rollBackexe = new rollBackMASTERCARDISSPOS( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackMASTERCARDISSPOS extends StoredProcedure {
		private static final String insert_proc = "RECON_MC_ACQ_CROSS_RECON_ROLLBACK";

		public rollBackMASTERCARDISSPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackMASTERCARDACQATM(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackMASTERCARDACQATM rollBackexe = new rollBackMASTERCARDACQATM( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	

	public boolean rollBackMASTERCARDACQATMINT(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackMASTERCARDACQATMINT rollBackexe = new rollBackMASTERCARDACQATMINT( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackMASTERCARDACQATMINT extends StoredProcedure {
		private static final String insert_proc = "RECON_MC_ACQ_INT_ROLLBACK";

		public rollBackMASTERCARDACQATMINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	private class rollBackMASTERCARDACQATM extends StoredProcedure {
		private static final String insert_proc = "RECON_MC_ACQ_DOM_ROLLBACK";

		public rollBackMASTERCARDACQATM(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackINTVISA(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackINTVISA rollBackexe = new rollBackINTVISA( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	private class rollBackINTVISA extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_SETTLEMENT_ROLLBACK";

		public rollBackINTVISA(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean rollBackINTVISA2(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackINTVISA2 rollBackexe = new rollBackINTVISA2( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}


	private class rollBackINTVISA2 extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_SETTLEMENT_TTUM_ROLLBACK";

		public rollBackINTVISA2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}

	public boolean rollBackISSCTC(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackISSCTCPROC rollBackexe = new rollBackISSCTCPROC( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class rollBackISSCTCPROC extends StoredProcedure {
		private static final String insert_proc = "RECON_C2C_PROC_ROLLBACK";

		public rollBackISSCTCPROC(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",  Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public boolean runCooperativeTTUM(NFSSettlementBean beanObj) {
		return false;}

	public List<Object> getCooperativeTTUM(NFSSettlementBean beanObj) {
		return null;}

	public String ValidateLateReversalFile(NFSSettlementBean beanObj) {
		int prevCount = 0;
		try {
			int processCount = ((Integer) getJdbcTemplate().queryForObject(
					"select count(*) from NFS_LATE_REVERSAL_TTUM where filedate = ?",
					new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
			if (processCount == 0) {
				int dataCount = ((Integer) getJdbcTemplate().queryForObject(
						"SELECT COUNT(*) FROM NFS_REV_ACQ_REPORT WHERE FILEDATE = ?",
						new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
				logger.info("dataCount is " + dataCount);
				if (dataCount > 0) {
					int firsttime = ((Integer) getJdbcTemplate()
							.queryForObject("select count(*) FROM NFS_LATE_REVERSAL_TTUM", Integer.class)).intValue();
					if (firsttime > 0) {
						prevCount = ((Integer) getJdbcTemplate().queryForObject(
								"select count(*) from NFS_LATE_REVERSAL_TTUM where filedate = TO_DATE(?,'DD/MM/YYYY')-1",
								new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
					} else {
						prevCount = 1;
					}
					if (prevCount > 0)
						return "success";
					return "Previous date ttum is not generated";
				}
				return "File is not uploaded for Selected date!";
			}
			return "TTUM is already processed \n Please download report";
		} catch (Exception e) {
			logger.info("Exception in ValidateLateReversalFile" + e);
			return "Exception Occurred!";
		}
	}

	public List<Object> getLateReversalTTUMData(NFSSettlementBean beanObj) {
		return null;}

	public String checkReversalTTUMProcess(NFSSettlementBean beanObj) throws Exception {
		int getTTUMCount = ((Integer) getJdbcTemplate().queryForObject(
				"SELECT COUNT(*) FROM NFS_LATE_REVERSAL_TTUM WHERE FILEDATE = ?",
				new Object[] { beanObj.getDatepicker() }, Integer.class)).intValue();
		if (getTTUMCount > 0)
			return "success";
		return "Please process TTUM first";
	}

	public boolean runLateReversalTTUM(NFSSettlementBean beanObj) {return true;}

	public boolean runVisaAdjTTUM(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams1 = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<>();
		try {
			AdjVisaTTUMProc exe = new AdjVisaTTUMProc( getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			inParams.put("ADJTYPE", beanObj.getAdjType());
			inParams.put("SUBCATE", beanObj.getStSubCategory());
			outParams2 = exe.execute(inParams);
			if (outParams2 != null && outParams2.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams2.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
	private class AdjVisaTTUMProc extends StoredProcedure {
		private static final String insert_proc = "VISA_ADJ_TTUM";

		public AdjVisaTTUMProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",  Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",  Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.INTEGER));
			declareParameter(new SqlParameter("ADJTYPE",  Types.VARCHAR));
			declareParameter(new SqlParameter("SUBCATE",  Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,  Types.VARCHAR));
			compile();
		}

	}
	public List<Object> getVisaAdjTTUM(NFSSettlementBean beanObj) {
		return null;}

	public boolean NFSSETTLTTUM(NFSSettlementBean beanObj) throws SQLException {
		try {
			OracleConn oracObj = new OracleConn();
			Connection conn = oracObj.getconn();
			String ss = "{call NFS_DAILY_SETTLEMENT_TTUM}(?,?,?)}";
			CallableStatement st = conn.prepareCall(ss);
			st.setString(1, "23-04-24");
			st.setString(2, (String) null);
			st.setString(3, (String) null);
			st.execute();
			System.out.println("st.getString(4) ");
			st.close();
			conn.close();
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Object> ExtractVisaRawdata(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getTTUMCategory().contains("ACQUIRER")) {
				getData1 = "select * from visa_acq_rawdata WHERE  filedate = STR_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d')";
			} else {
				getData1 = "select * from visa_visa_rawdata WHERE  filedate = STR_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d')";
			}
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					int count = 1, dataCount = 0, datasum = 0;

					while (rs.next()) {
						// logger.info("Inside rset");

						Map<String, String> table_Data = new HashMap<String, String>();
						
						
						table_Data.put("TC",rs.getString("TC"));
								table_Data.put("TCR_CODE",rs.getString("TCR_CODE"));
								table_Data.put("CARD_NUMBER ",rs.getString("CARD_NUMBER"));
								table_Data.put("SOURCE_AMOUNT",rs.getString("SOURCE_AMOUNT"));
								table_Data.put("AUTHORIZATION_CODE",rs.getString("AUTHORIZATION_CODE"));
								table_Data.put("DCRS_SEQ_NO",rs.getString("DCRS_SEQ_NO"));
								table_Data.put("TRACE",rs.getString("TRACE"));
								table_Data.put("REFERENCE_NUMBER",rs.getString("REFERENCE_NUMBER"));
								table_Data.put("RESPONSE_CODE",rs.getString("RESPONSE_CODE"));
								table_Data.put("PART_ID",rs.getString("PART_ID"));
								table_Data.put("CREATEDDATE",rs.getString("CREATEDDATE"));
								table_Data.put("CREATEDBY",rs.getString("CREATEDBY"));
								table_Data.put("FILEDATE",rs.getString("FILEDATE"));
								table_Data.put("REQ_MSGTYPE",rs.getString("REQ_MSGTYPE"));
								table_Data.put("FLOOR_LIMIT_INDI",rs.getString("FLOOR_LIMIT_INDI"));
								table_Data.put("ARN",rs.getString("ARN"));
								table_Data.put("ACQUIRER_BUSI_ID",rs.getString("ACQUIRER_BUSI_ID"));
								table_Data.put("PURCHASE_DATE",rs.getString("PURCHASE_DATE"));
								table_Data.put("DESTINATION_AMOUNT",rs.getString("DESTINATION_AMOUNT"));
								table_Data.put("DESTINATION_CURR_CODE",rs.getString("DESTINATION_CURR_CODE"));
								table_Data.put("SOURCE_CURR_CODE",rs.getString("SOURCE_CURR_CODE"));
								table_Data.put("MERCHANT_NAME",rs.getString("MERCHANT_NAME"));
								table_Data.put("MERCHANT_CITY",rs.getString("MERCHANT_CITY"));
								table_Data.put("MERCHANT_COUNTRY_CODE",rs.getString("MERCHANT_COUNTRY_CODE"));
								table_Data.put("MERCHANT_CATEGORY_CODE",rs.getString("MERCHANT_CATEGORY_CODE"));
								table_Data.put("MERCHANT_ZIP_CODE",rs.getString("MERCHANT_ZIP_CODE"));
								table_Data.put("USAGE_CODE",rs.getString("USAGE_CODE"));
								table_Data.put("SETTLEMENT_FLAG",rs.getString("SETTLEMENT_FLAG"));
								table_Data.put("AUTH_CHARA_IND",rs.getString("AUTH_CHARA_IND"));
								table_Data.put("POS_TERMINAL_CAPABILITY",rs.getString("POS_TERMINAL_CAPABILITY"));
								table_Data.put("CARDHOLDER_ID_METHOD",rs.getString("CARDHOLDER_ID_METHOD"));
								table_Data.put("COLLECTION_ONLY_FLAG",rs.getString("COLLECTION_ONLY_FLAG"));
								table_Data.put("POS_ENTRY_MODE",rs.getString("POS_ENTRY_MODE"));
								table_Data.put("CENTRAL_PROCESS_DATE",rs.getString("CENTRAL_PROCESS_DATE"));
								table_Data.put("REIMBURSEMENT_ATTR",rs.getString("REIMBURSEMENT_ATTR"));
								table_Data.put("DESTINATION_BIN",rs.getString("DESTINATION_BIN"));
								table_Data.put("SOURCE_BIN",rs.getString("SOURCE_BIN"));
								table_Data.put("REASON_CODE",rs.getString("REASON_CODE"));
								table_Data.put("COUNTRY_CODE",rs.getString("COUNTRY_CODE"));
								table_Data.put("EVENT_DATE",rs.getString("EVENT_DATE"));
								table_Data.put("MESSAGE_TEXT",rs.getString("MESSAGE_TEXT"));
								table_Data.put("TRANSAC_IDENTIFIER",rs.getString("TRANSAC_IDENTIFIER"));
								table_Data.put("TRAN_ID",rs.getString("TRAN_ID"));
								table_Data.put("FPAN",rs.getString("FPAN"));
								table_Data.put("FILENAME",rs.getString("FILENAME"));
								table_Data.put("DCRS_REMARKS",rs.getString("DCRS_REMARKS"));
						
						
						
						
						
						beanList.add(table_Data);

					}

					return beanList;
				}
			});

			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> ExtractSwitchRawdata(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getTTUMCategory().equalsIgnoreCase("PTLF")) {
				getData1 = "select * from switch_rawdata WHERE  filedate = STR_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d') and upper(filename) like 'P%'";
			} else {
				getData1 = "select * from switch_rawdata WHERE  filedate = STR_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d') and upper(filename) like 'TX%'";
			}
			
			List<String> Column_list = new ArrayList<>();
			Column_list = new ArrayList<>();
			Column_list = getColumnList("switch_rawdata");

			final List<String> columns = Column_list;
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					while (rs.next()) {
						Map<String, String> data = new HashMap<String, String>();
						for (String column : columns) {
							data.put(column, rs.getString(column));
						}
						beanList.add(data);
					}
					return beanList;
				}
			});
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> ExtractCbsRawdata(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		String getData1 = null;
		List<Object> DailyData = new ArrayList();
		List<String> Column_list = new ArrayList<>();
		try {

			if (beanObj.getTTUMCategory().equalsIgnoreCase("FN100")) {
				getData1 = "select * from all_cbs_rawdata WHERE  filedate = STR_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d')";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("all_cbs_rawdata");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});

			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139924")) {
				getData1 = "select * from cbs_rupay_card_tran WHERE  filedate = STR_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d') and upper(filename) like '5165005139924%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_card_tran");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});

			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139926")) {
				getData1 = "select * from cbs_rupay_all_rawdata WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005139926%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("all_cbs_rawdata");
				data.add(Column_list);
				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});

			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139931")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005139931%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139932")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005139932%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139933")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005139933%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139934")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005139934%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139936")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005139936%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139938")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005139938%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139948")) {
				getData1 = "select * from cbs_nfs_iss_rawdata WHERE  filedate = STR_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d') and upper(filename) like '5165005139948%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_nfs_iss_rawdata");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005811354")) {
				getData1 = "select * from cbs_nfs_iss_rawdata WHERE  filedate = STR_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d') and upper(filename) like '5165005811354%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_nfs_iss_rawdata");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139949")) {
				getData1 = "select * from cbs_nfs_acq_rawdata WHERE  filedate = STR_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d') and upper(filename) like '5165005139949%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_nfs_acq_rawdata");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139950")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005139950%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139951")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005139951%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139954")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005139954%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139968")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005139968%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139970")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005139970%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005139971")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005139971%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("516500511355")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '516500511355%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005131356")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005131356%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005132281")) {
				getData1 = "select * from cbs_rupay_all_rawdata1 WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005132281%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata1");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005132550")) {
				getData1 = "select * from cbs_rupay_all_rawdata WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005132550%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else if (beanObj.getTTUMCategory().equalsIgnoreCase("5165005132549")) {
				getData1 = "select * from cbs_rupay_all_rawdata WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') and upper(filename) like '5165005132549%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("cbs_rupay_all_rawdata");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			} else {
				getData1 = "select * from switch_rawdata WHERE  filedate = STR_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d') and upper(filename) like 'TX%'";
				Column_list = new ArrayList<>();
				Column_list = getColumnList("switch_rawdata");

				final List<String> columns = Column_list;
				DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> data = new HashMap<String, String>();
							for (String column : columns) {
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
						}
						return beanList;
					}
				});
			}
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> ExtractMASTERCARDRawdata(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		try {
			String getData1 = null, tablename=null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getCategory().contains("MASTERCARD_ATM")) {
				getData1 = "select * from  mastercard_atm_rawdata WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				tablename ="mastercard_atm_rawdata";
			} else {
				getData1 = "select * from  mastercard_pos_rawdata WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				tablename ="mastercard_pos_rawdata";
			}
			List<String> Column_list = new ArrayList<>();
			Column_list = new ArrayList<>();
			Column_list = getColumnList(tablename);

			final List<String> columns = Column_list;
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					while (rs.next()) {
						Map<String, String> data = new HashMap<String, String>();
						for (String column : columns) {
							data.put(column, rs.getString(column));
						}
						beanList.add(data);
					}
					return beanList;
				}
			});
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> ExtractNFSRawdata(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		System.out.println("data " + beanObj.getCategory() + "f " + beanObj.getTTUMCategory());
		try {
			String getData1 = null, tablename=null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getCategory().contains("NFS")) {
				if (beanObj.getTTUMCategory().contains("ACQUIRER")) {
					getData1 = "SELECT  * FROM nfs_nfs_acq_rawdata WHERE filedate = STR_to_date('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')  AND CYCLE='" + beanObj.getCycle() + "'";
					tablename ="nfs_nfs_acq_rawdata";
				} else {
					getData1 = "SELECT  * FROM nfs_nfs_iss_rawdata WHERE  filedate = STR_to_date('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') AND CYCLE='" + beanObj.getCycle() + "' ";
					tablename ="nfs_nfs_iss_rawdata";
				}
			} else if (beanObj.getCategory().contains("DFS")) {
				getData1 = "SELECT * FROM  dfs_dfs_acq_rawdata WHERE  filedate = STR_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d')";
				tablename ="dfs_dfs_acq_rawdata";
			} else if (beanObj.getCategory().contains("JCB")) {
				getData1 = "SELECT  * FROM  jcb_jcb_acq_rawdata WHERE  filedate = STR_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				tablename ="jcb_jcb_acq_rawdata";
			} else if (beanObj.getCategory().contains("ICD")) {
				if (beanObj.getTTUMCategory().contains("ACQUIRER")) {
					getData1 = "SELECT * FROM  icd_icd_acq_rawdata WHERE  filedate = STR_to_date('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
					tablename ="icd_icd_acq_rawdata";
				} else {
					getData1 = "SELECT  * FROM  icd_icd_iss_rawdata WHERE filedate = STR_to_date('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
					tablename ="icd_icd_iss_rawdata";
				}
			}
			List<String> Column_list = new ArrayList<>();
			Column_list = new ArrayList<>();
			Column_list = getColumnList(tablename);

			final List<String> columns = Column_list;
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					while (rs.next()) {
						Map<String, String> data = new HashMap<String, String>();
						for (String column : columns) {
							data.put(column, rs.getString(column));
						}
						beanList.add(data);
					}
					return beanList;
				}
			});
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> ExtractRUPAYRawdata(UnMatchedTTUMBean beanObj) {
		List<Object> data = new ArrayList();
		System.out.println("data " + beanObj.getCategory() + "f " + beanObj.getTTUMCategory());
		try {
			String getData1 = null, tablename=null;
			List<Object> DailyData = new ArrayList();
			if (beanObj.getCategory().contains("RUPAY_INT")) {
				getData1 = "SELECT * from  rupay_rupay_int_rawdata WHERE  filedate = STR_to_date('"
						+beanObj.getLocalDate() + "','%Y/%m/%d')";
				tablename ="rupay_rupay_int_rawdata";
			} else if (beanObj.getCategory().contains("RUPAY_DOM")) {
				getData1 = "SELECT  * from rupay_rupay_rawdata WHERE  filedate = STR_to_date('"
						+beanObj.getLocalDate() + "','%Y/%m/%d')";
				tablename ="rupay_rupay_rawdata";
			} else if (beanObj.getCategory().contains("QSPARC_DOM")) {
				getData1 = "SELECT  * from  rupay_qsparc_rawdata  WHERE  filedate = STR_to_date('"
						+beanObj.getLocalDate() + "','%Y/%m/%d')";
				tablename ="rupay_qsparc_rawdata";
			}
			List<String> Column_list = new ArrayList<>();
			Column_list = new ArrayList<>();
			Column_list = getColumnList(tablename);

			final List<String> columns = Column_list;
			DailyData = getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					while (rs.next()) {
						Map<String, String> data = new HashMap<String, String>();
						for (String column : columns) {
							data.put(column, rs.getString(column));
						}
						beanList.add(data);
					}
					return beanList;
				}
			});
			data.add(DailyData);
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public boolean rollBackICCWDACQ(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackICCWACQ rollBackexe = new rollBackICCWACQ( getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}

	public boolean rollBackICCWDISS(NFSSettlementBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			rollBackICCWISS rollBackexe = new rollBackICCWISS(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
			logger.info("OUT PARAM IS " + outParams.get("msg"));
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return false;
		}
	}
}
