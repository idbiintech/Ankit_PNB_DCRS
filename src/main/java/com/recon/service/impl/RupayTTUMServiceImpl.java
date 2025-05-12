package com.recon.service.impl;

import com.recon.model.RecordCount;
import com.recon.model.TTUMBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.RupayTTUMService;
import com.recon.util.GeneralUtil;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;

public class RupayTTUMServiceImpl extends JdbcDaoSupport implements RupayTTUMService {
	@Autowired
	GeneralUtil generalutil;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final Logger logger = Logger.getLogger(com.recon.service.impl.RupayTTUMServiceImpl.class);

	private static final String O_ERROR_MESSAGE = "o_error_message";

	public static final String OUTPUT_FOLDER = String.valueOf(System.getProperty("catalina.home")) + File.separator
			+ "FUNDING";

	public static final String OUTPUT_FOLDER1 = String.valueOf(System.getProperty("catalina.home")) + File.separator
			+ "TTUM1";

	public HashMap<String, Object> runTTUMProces(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProc rollBackexe = new UnmatchedTTUMProc(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class UnmatchedTTUMProc extends StoredProcedure {
		private static final String insert_proc = "NFS_ACQ_RECON_TTUM";

		public UnmatchedTTUMProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("MSG", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcesJCB(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcJCB rollBackexe = new UnmatchedTTUMProcJCB(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class UnmatchedTTUMProcJCB extends StoredProcedure {
		private static final String insert_proc = "JCB_ACQ_RECON_TTUM";

		public UnmatchedTTUMProcJCB(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("MSG", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcesDFS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcDFS rollBackexe = new UnmatchedTTUMProcDFS(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class UnmatchedTTUMProcDFS extends StoredProcedure {
		private static final String insert_proc = "DFS_ACQ_RECON_TTUM";

		public UnmatchedTTUMProcDFS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("MSG", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcesICD(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcesICD rollBackexe = new runTTUMProcesICD(getJdbcTemplate());
			inParams.put("V_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class runTTUMProcesICD extends StoredProcedure {
		private static final String insert_proc = "ICD_ISS_UNRECON_NPCI";

		public runTTUMProcesICD(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("MSG", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcesICCW(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcesICCW rollBackexe = new runTTUMProcesICCW(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class runTTUMProcesICCW extends StoredProcedure {
		private static final String insert_proc = "NFS_ICCW_ISS_RECON_TTUM";

		public runTTUMProcesICCW(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlParameter("V_TTUMTYPE", Types.VARCHAR));
			declareParameter(new SqlOutParameter("MSG", Types.VARCHAR));
			compile();
		}

	}
	
	public HashMap<String, Object> runTTUMProcesICCW2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcesICCW2 rollBackexe = new runTTUMProcesICCW2(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class runTTUMProcesICCW2 extends StoredProcedure {
		private static final String insert_proc = "NFS_ICCW_ACQ_RECON_TTUM";

		public runTTUMProcesICCW2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlParameter("V_TTUMTYPE", Types.VARCHAR));
			declareParameter(new SqlOutParameter("MSG", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcesNFSRUPAY(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcesNFSRUPAY rollBackexe = new runTTUMProcesNFSRUPAY(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class runTTUMProcesNFSRUPAY extends StoredProcedure {
		private static final String insert_proc = "NFSISS_RUPAYINT_TTUM";

		public runTTUMProcesNFSRUPAY(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("MSG", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runProcessFinacleTTUM(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runProcessFinacleTTUM rollBackexe = new runProcessFinacleTTUM(getJdbcTemplate());
			inParams.put("V_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class runProcessFinacleTTUM extends StoredProcedure {
		private static final String insert_proc = "FINACLE_TTUM";

		public runProcessFinacleTTUM(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("MSG", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcess2ICD(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcess2ICD rollBackexe = new runTTUMProcess2ICD(getJdbcTemplate());
			inParams.put("V_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class runTTUMProcess2ICD extends StoredProcedure {
		private static final String insert_proc = "ICD_ISS_UNRECON_CBS";

		public runTTUMProcess2ICD(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("MSG", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcess3ICD(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcess3ICD rollBackexe = new runTTUMProcess3ICD(getJdbcTemplate());
			inParams.put("V_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class runTTUMProcess3ICD extends StoredProcedure {
		private static final String insert_proc = "ICD_ACQ_UNRECON_CBS";

		public runTTUMProcess3ICD(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("MSG", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcess4ICD(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcess4ICD rollBackexe = new runTTUMProcess4ICD(getJdbcTemplate());
			inParams.put("V_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class runTTUMProcess4ICD extends StoredProcedure {
		private static final String insert_proc = "ICD_ACQ_UNRECON_NPCI";

		public runTTUMProcess4ICD(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("MSG", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcess2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProc2 rollBackexe = new UnmatchedTTUMProc2(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.get("MSG"));
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("MSG"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class UnmatchedTTUMProc2 extends StoredProcedure {
		private static final String insert_proc = "NFS_ISS_RECON_TTUM";

		public UnmatchedTTUMProc2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("MSG", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcess3(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProc3 rollBackexe = new UnmatchedTTUMProc3(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class UnmatchedTTUMProc3 extends StoredProcedure {
		private static final String insert_proc = "NFS_C2C_ACQ_RECON_TTUM";

		public UnmatchedTTUMProc3(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> processEODGLVISARefund(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			processEODGLVISARefund rollBackexe = new processEODGLVISARefund(getJdbcTemplate());
			inParams.put("V_GLDATE", beanObj.getLocalDate());
			inParams.put("V_EODBAL", beanObj.getOpeningBalance());
			
			inParams.put("V_FEODBAL", beanObj.getClosingBalance());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class processEODGLVISARefund extends StoredProcedure {
		private static final String insert_proc = "visa_refund_gl";

		public processEODGLVISARefund(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_GLDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_EODBAL", Types.VARCHAR));
			declareParameter(new SqlParameter("V_FEODBAL", Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> proccessVISAACQCHARGEBACKDOMGL(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			proccessVISAACQCHARGEBACKDOMGL rollBackexe = new proccessVISAACQCHARGEBACKDOMGL(getJdbcTemplate());
			inParams.put("V_GLDATE", beanObj.getLocalDate());
			inParams.put("V_EODBAL", beanObj.getOpeningBalance());
			
			inParams.put("V_FEODBAL", beanObj.getClosingBalance());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class proccessVISAACQCHARGEBACKDOMGL extends StoredProcedure {
		private static final String insert_proc = "visa_acq_dom_chbk_gl";

		public proccessVISAACQCHARGEBACKDOMGL(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_GLDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_EODBAL", Types.VARCHAR));
			declareParameter(new SqlParameter("V_FEODBAL", Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> proccessVISA_ACQ_INT_POOL_GL(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			VISA_ACQ_INT_POOL_GL rollBackexe = new VISA_ACQ_INT_POOL_GL(getJdbcTemplate());
			inParams.put("V_GLDATE", beanObj.getLocalDate());
			inParams.put("V_EODBAL", beanObj.getOpeningBalance());
			
			inParams.put("V_FEODBAL", beanObj.getClosingBalance());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class VISA_ACQ_INT_POOL_GL extends StoredProcedure {
		private static final String insert_proc = "visa_acq_int_pool_gl";

		public VISA_ACQ_INT_POOL_GL(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_GLDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_EODBAL", Types.VARCHAR));
			declareParameter(new SqlParameter("V_FEODBAL", Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> proccessVISA_ACQ_DOM_POOL_GL(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			VISA_ACQ_DOM_POOL_GL rollBackexe = new VISA_ACQ_DOM_POOL_GL(getJdbcTemplate());
			inParams.put("V_GLDATE", beanObj.getLocalDate());
			inParams.put("V_EODBAL", beanObj.getOpeningBalance());
			
			inParams.put("V_FEODBAL", beanObj.getClosingBalance());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class VISA_ACQ_DOM_POOL_GL extends StoredProcedure {
		private static final String insert_proc = "visa_acq_int_pool_gl";

		public VISA_ACQ_DOM_POOL_GL(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_GLDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_EODBAL", Types.VARCHAR));
			declareParameter(new SqlParameter("V_FEODBAL", Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> proccessVISA_INT_BENE_CHARGEBACK_GL(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			VISA_INT_BENE_CHARGEBACK_GL rollBackexe = new VISA_INT_BENE_CHARGEBACK_GL(getJdbcTemplate());
			inParams.put("V_GLDATE", beanObj.getLocalDate());
			inParams.put("V_EODBAL", beanObj.getOpeningBalance());
			
			inParams.put("V_FEODBAL", beanObj.getClosingBalance());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class VISA_INT_BENE_CHARGEBACK_GL extends StoredProcedure {
		private static final String insert_proc = "visa_bene_int_chbk_gl";

		public VISA_INT_BENE_CHARGEBACK_GL(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_GLDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_EODBAL", Types.VARCHAR));
			declareParameter(new SqlParameter("V_FEODBAL", Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> proccessVISA_ISS_POOL_GL(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			VISA_ISS_POOL_GL rollBackexe = new VISA_ISS_POOL_GL(getJdbcTemplate());
			inParams.put("V_GLDATE", beanObj.getLocalDate());
			inParams.put("V_EODBAL", beanObj.getOpeningBalance());
			
			inParams.put("V_FEODBAL", beanObj.getClosingBalance());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class VISA_ISS_POOL_GL extends StoredProcedure {
		private static final String insert_proc = "visa_iss_pool_gl";

		public VISA_ISS_POOL_GL(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_GLDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_EODBAL", Types.VARCHAR));
			declareParameter(new SqlParameter("V_FEODBAL", Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> proccessVISA_REME_CHARGEBACK_DOM_GL(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			VISA_REME_CHARGEBACK_DOM_GL rollBackexe = new VISA_REME_CHARGEBACK_DOM_GL(getJdbcTemplate());
			inParams.put("V_GLDATE", beanObj.getLocalDate());
			inParams.put("V_EODBAL", beanObj.getOpeningBalance());
			
			inParams.put("V_FEODBAL", beanObj.getClosingBalance());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class VISA_REME_CHARGEBACK_DOM_GL extends StoredProcedure {
		private static final String insert_proc = "visa_reme_dom_chbk_gl";

		public VISA_REME_CHARGEBACK_DOM_GL(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_GLDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_EODBAL", Types.VARCHAR));
			declareParameter(new SqlParameter("V_FEODBAL", Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> proccessNFSISSGL(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			proccessNFSISSGL rollBackexe = new proccessNFSISSGL(getJdbcTemplate());
			inParams.put("V_GLDATE", beanObj.getLocalDate());
			inParams.put("V_EODBAL", beanObj.getOpeningBalance());
			
			inParams.put("V_FEODBAL", beanObj.getClosingBalance());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class proccessNFSISSGL extends StoredProcedure {
		private static final String insert_proc = "nfs_issuer_gl";

		public proccessNFSISSGL(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("V_GLDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_EODBAL", Types.VARCHAR));
			declareParameter(new SqlParameter("V_FEODBAL", Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}
	public HashMap<String, Object> runTTUMProcess4(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProc4 rollBackexe = new UnmatchedTTUMProc4(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			inParams.put("V_TTUMTYPE", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class UnmatchedTTUMProc4 extends StoredProcedure {
		private static final String insert_proc = "C2C_ISS_RECON_TTUM";

		public UnmatchedTTUMProc4(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("V_TTUMTYPE", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcesVISA(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA rollBackexe = new UnmatchedTTUMProcVISA(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_DROP_TTUM";

		public UnmatchedTTUMProcVISA(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA2 rollBackexe = new UnmatchedTTUMProcVISA2(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA2 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_LATE_PRESENTMENT_TTUM";

		public UnmatchedTTUMProcVISA2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA3(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA3 rollBackexe = new UnmatchedTTUMProcVISA3(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA3 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_PROACTIVE_TTUM";

		public UnmatchedTTUMProcVISA3(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA4(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA4 rollBackexe = new UnmatchedTTUMProcVISA4(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA4 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_CR_SURCH_TTUM";

		public UnmatchedTTUMProcVISA4(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA5(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA5 rollBackexe = new UnmatchedTTUMProcVISA5(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA5 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_DR_SURCH_TTUM";

		public UnmatchedTTUMProcVISA5(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA6(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA6 rollBackexe = new UnmatchedTTUMProcVISA6(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA6 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_ORG_WDL_TTUM";

		public UnmatchedTTUMProcVISA6(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA7(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA7 rollBackexe = new UnmatchedTTUMProcVISA7(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA7 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_DOM_REFUND_TTUM";

		public UnmatchedTTUMProcVISA7(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA8(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA8 rollBackexe = new UnmatchedTTUMProcVISA8(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA8 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_DOM_REFUND_REV_TTUM";

		public UnmatchedTTUMProcVISA8(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA9(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA9 rollBackexe = new UnmatchedTTUMProcVISA9(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA9 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_INT_REFUND_TTUM";

		public UnmatchedTTUMProcVISA9(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA10(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA10 rollBackexe = new UnmatchedTTUMProcVISA10(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA10 extends StoredProcedure {
		private static final String insert_proc = "VISA_ISS_INT_REFUND_REV_TTUM";

		public UnmatchedTTUMProcVISA10(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcesVISAPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISAPOS rollBackexe = new UnmatchedTTUMProcVISAPOS(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISAPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_ACQ_DOM_ATM_LORO_DEBIT_TTUM";

		public UnmatchedTTUMProcVISAPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA2POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA2POS rollBackexe = new UnmatchedTTUMProcVISA2POS(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA2POS extends StoredProcedure {
		private static final String insert_proc = "VISA_ACQ_DOM_ATM_LORO_CREDIT_TTUM";

		public UnmatchedTTUMProcVISA2POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA3POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA3POS rollBackexe = new UnmatchedTTUMProcVISA3POS(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA3POS extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_POS_PROACTIVE_TTUM";

		public UnmatchedTTUMProcVISA3POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA4POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA4POS rollBackexe = new UnmatchedTTUMProcVISA4POS(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA4POS extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_POS_CR_SURCH_TTUM";

		public UnmatchedTTUMProcVISA4POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA5POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA5POS rollBackexe = new UnmatchedTTUMProcVISA5POS(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA5POS extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_POS_DR_SURCH_TTUM";

		public UnmatchedTTUMProcVISA5POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA6POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA6POS rollBackexe = new UnmatchedTTUMProcVISA6POS(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA6POS extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_POS_ORG_WDL_TTUM";

		public UnmatchedTTUMProcVISA6POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA7POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA7POS rollBackexe = new UnmatchedTTUMProcVISA7POS(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA7POS extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_POS_REFUND_TTUM";

		public UnmatchedTTUMProcVISA7POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcesVISAINT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISAINT rollBackexe = new UnmatchedTTUMProcVISAINT(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISAINT extends StoredProcedure {
		private static final String insert_proc = "VISA_ACQ_INT_ATM_LORO_DEBIT_TTUM";

		public UnmatchedTTUMProcVISAINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA2INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA2INT rollBackexe = new UnmatchedTTUMProcVISA2INT(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA2INT extends StoredProcedure {
		private static final String insert_proc = "VISA_ACQ_INT_ATM_LORO_CREDIT_TTUM";

		public UnmatchedTTUMProcVISA2INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA3INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA3INT rollBackexe = new UnmatchedTTUMProcVISA3INT(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA3INT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_PROACTIVE_TTUM";

		public UnmatchedTTUMProcVISA3INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA4INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA4INT rollBackexe = new UnmatchedTTUMProcVISA4INT(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA4INT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_CR_SURCH_TTUM";

		public UnmatchedTTUMProcVISA4INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA5INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA5INT rollBackexe = new UnmatchedTTUMProcVISA5INT(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA5INT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_DR_SURCH_TTUM";

		public UnmatchedTTUMProcVISA5INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA6INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA6INT rollBackexe = new UnmatchedTTUMProcVISA6INT(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA6INT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_PROACTIVE_CHARGES_TTUM";

		public UnmatchedTTUMProcVISA6INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA7INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA7INT rollBackexe = new UnmatchedTTUMProcVISA7INT(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA7INT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_DROP_CHARGES_TTUM";

		public UnmatchedTTUMProcVISA7INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA8INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA8INT rollBackexe = new UnmatchedTTUMProcVISA8INT(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA8INT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ATM_ORG_WDL_TTUM";

		public UnmatchedTTUMProcVISA8INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA9INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcessVISA9INTPOS rollBackexe = new runTTUMProcessVISA9INTPOS(getJdbcTemplate());
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

	private class runTTUMProcessVISA9INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_REFUND_TTUM";

		public runTTUMProcessVISA9INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcesVISAINTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISAINTPOS rollBackexe = new UnmatchedTTUMProcVISAINTPOS(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISAINTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_DROP_TTUM";

		public UnmatchedTTUMProcVISAINTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA2INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA2INTPOS rollBackexe = new UnmatchedTTUMProcVISA2INTPOS(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA2INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_LATE_PRESENTMENT_TTUM";

		public UnmatchedTTUMProcVISA2INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA10INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA10INTPOS rollBackexe = new UnmatchedTTUMProcVISA10INTPOS(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA10INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_LATE_PRESENTMENT_CHARGES_TTUM";

		public UnmatchedTTUMProcVISA10INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA3INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA3INTPOS rollBackexe = new UnmatchedTTUMProcVISA3INTPOS(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA3INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_PROACTIVE_TTUM";

		public UnmatchedTTUMProcVISA3INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA4INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA4INTPOS rollBackexe = new UnmatchedTTUMProcVISA4INTPOS(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
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

	private class UnmatchedTTUMProcVISA4INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_CR_SURCH_TTUM";

		public UnmatchedTTUMProcVISA4INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA5INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA5INTPOS rollBackexe = new UnmatchedTTUMProcVISA5INTPOS(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA5INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_DR_SURCH_TTUM";

		public UnmatchedTTUMProcVISA5INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA6INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA6INTPOS rollBackexe = new UnmatchedTTUMProcVISA6INTPOS(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA6INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_PROACTIVE_CHARGES_TTUM";

		public UnmatchedTTUMProcVISA6INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA7INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA7INTPOS rollBackexe = new UnmatchedTTUMProcVISA7INTPOS(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA7INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_DROP_CHARGES_TTUM";

		public UnmatchedTTUMProcVISA7INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> runTTUMProcessVISA8INTPOS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcVISA8INTPOS rollBackexe = new UnmatchedTTUMProcVISA8INTPOS(getJdbcTemplate());
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

	private class UnmatchedTTUMProcVISA8INTPOS extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_POS_ORG_WDL_TTUM";

		public UnmatchedTTUMProcVISA8INTPOS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessRUPAY(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getFileDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcRupay rollBackexe = new UnmatchedTTUMProcRupay(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcRupay extends StoredProcedure {
		private static final String insert_proc = "RUPAY_DECLINE_TTUM";

		public UnmatchedTTUMProcRupay(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessQSPARC(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getFileDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcQSPARC rollBackexe = new UnmatchedTTUMProcQSPARC(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcQSPARC extends StoredProcedure {
		private static final String insert_proc = "QSPARC_DECLINE_TTUM";

		public UnmatchedTTUMProcQSPARC(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessRUPAYINT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getFileDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcRupayINT rollBackexe = new UnmatchedTTUMProcRupayINT(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcRupayINT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_DECLINE_TTUM";

		public UnmatchedTTUMProcRupayINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessQSPARCINT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getFileDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcQSPARCINT rollBackexe = new UnmatchedTTUMProcQSPARCINT(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcQSPARCINT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_DECLINE_TTUM";

		public UnmatchedTTUMProcQSPARCINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessRUPAY2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcRupay2 rollBackexe = new UnmatchedTTUMProcRupay2(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcRupay2 extends StoredProcedure {
		private static final String insert_proc = "RUPAY_PROACTIVE_TTUM";

		public UnmatchedTTUMProcRupay2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessMC2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcessMC2 rollBackexe = new runTTUMProcessMC2(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class runTTUMProcessMC2 extends StoredProcedure {
		private static final String insert_proc = "MC_ACQ_DOM_LORO_CREDIT_TTUM ";

		public runTTUMProcessMC2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessMC2POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcessMC2POS rollBackexe = new runTTUMProcessMC2POS(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class runTTUMProcessMC2POS extends StoredProcedure {
		private static final String insert_proc = "MC_PROACTIV_POS_TTUM";

		public runTTUMProcessMC2POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessMC2POSINT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcessMC2POSINT rollBackexe = new runTTUMProcessMC2POSINT(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class runTTUMProcessMC2POSINT extends StoredProcedure {
		private static final String insert_proc = "MC_ACQ_INT_LORO_CREDIT_TTUM ";

		public runTTUMProcessMC2POSINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessMC3POSINT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcessMC3POSINT rollBackexe = new runTTUMProcessMC3POSINT(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class runTTUMProcessMC3POSINT extends StoredProcedure {
		private static final String insert_proc = "MC_ACQ_INT_LORO_DEBIT_TTUM";

		public runTTUMProcessMC3POSINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessMC3(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcessMC3 rollBackexe = new runTTUMProcessMC3(getJdbcTemplate());
			inParams.put("I_FILDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class runTTUMProcessMC3 extends StoredProcedure {
		private static final String insert_proc = "MC_ACQ_DOM_LORO_DEBIT_TTUM";

		public runTTUMProcessMC3(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessMC3POS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcessMC3POS rollBackexe = new runTTUMProcessMC3POS(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class runTTUMProcessMC3POS extends StoredProcedure {
		private static final String insert_proc = "MASTERCARD_ISS_POS_DROP_TTUM";

		public runTTUMProcessMC3POS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessRUPAY2INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcRupay2INT rollBackexe = new UnmatchedTTUMProcRupay2INT(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcRupay2INT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_PROACTIVE_TTUM";

		public UnmatchedTTUMProcRupay2INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessQSPARC2(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcQSPARC2 rollBackexe = new UnmatchedTTUMProcQSPARC2(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcQSPARC2 extends StoredProcedure {
		private static final String insert_proc = "QSPARC_DOM_PROACTIVE_TTUM";

		public UnmatchedTTUMProcQSPARC2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessQSPARC2INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcQSPARC2INT rollBackexe = new UnmatchedTTUMProcQSPARC2INT(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcQSPARC2INT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_PROACTIVE_TTUM";

		public UnmatchedTTUMProcQSPARC2INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessRUPAY3(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcRupay3 rollBackexe = new UnmatchedTTUMProcRupay3(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcRupay3 extends StoredProcedure {
		private static final String insert_proc = "RUPAY_DOM_DROP_TTUM";

		public UnmatchedTTUMProcRupay3(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessRUPAY3INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcRupay3INT rollBackexe = new UnmatchedTTUMProcRupay3INT(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcRupay3INT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_DROP_TTUM";

		public UnmatchedTTUMProcRupay3INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessQSPARC3(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcQSPARC3 rollBackexe = new UnmatchedTTUMProcQSPARC3(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcQSPARC3 extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_DROP_TTUM";

		public UnmatchedTTUMProcQSPARC3(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessQSPARC3INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcQSPARC3INT rollBackexe = new UnmatchedTTUMProcQSPARC3INT(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcQSPARC3INT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_DOM_DROP_TTUM";

		public UnmatchedTTUMProcQSPARC3INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessRUPAY5(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcRupay5 rollBackexe = new UnmatchedTTUMProcRupay5(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcRupay5 extends StoredProcedure {
		private static final String insert_proc = "POS_DOME_LATE_PRESM_TTUM";

		public UnmatchedTTUMProcRupay5(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessRUPAY5INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcessRUPAY5INT rollBackexe = new runTTUMProcessRUPAY5INT(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class runTTUMProcessRUPAY5INT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_OFFLINE_PRES_TTUM";

		public runTTUMProcessRUPAY5INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessMC5(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcessMC5 rollBackexe = new runTTUMProcessMC5(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class runTTUMProcessMC5 extends StoredProcedure {
		private static final String insert_proc = "MASTERCARD_POS_ISS_LATEPRE_TTUM";

		public runTTUMProcessMC5(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessQSPARC5(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcQSPARC5 rollBackexe = new UnmatchedTTUMProcQSPARC5(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcQSPARC5 extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_LATE_PRESM_TTUM";

		public UnmatchedTTUMProcQSPARC5(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessRUPAY6(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcRupay6 rollBackexe = new UnmatchedTTUMProcRupay6(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	public boolean runTTUMProcessRUPAY10(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcRupay10 rollBackexe = new UnmatchedTTUMProcRupay10(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	public boolean runTTUMProcessRUPAY6INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcRupay6INT rollBackexe = new UnmatchedTTUMProcRupay6INT(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcRupay6 extends StoredProcedure {
		private static final String insert_proc = "RUPAY_DOM_OFFLINE_PRES_TTUM";

		public UnmatchedTTUMProcRupay6(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	private class UnmatchedTTUMProcRupay10 extends StoredProcedure {
		private static final String insert_proc = "MASTERCARD_ISS_ATM_SURCH_TTUM";

		public UnmatchedTTUMProcRupay10(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	private class UnmatchedTTUMProcRupay6INT extends StoredProcedure {
		private static final String insert_proc = "POS_INT_LATE_PRESM_TTUM";

		public UnmatchedTTUMProcRupay6INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessQSPARC6(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcQSPARC6 rollBackexe = new UnmatchedTTUMProcQSPARC6(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcQSPARC6 extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_OFFLINE_PRES_TTUM";

		public UnmatchedTTUMProcQSPARC6(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessRUPAY4(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcRupay4 rollBackexe = new UnmatchedTTUMProcRupay4(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcRupay4 extends StoredProcedure {
		private static final String insert_proc = "RUPAY_DOM_SURCH";

		public UnmatchedTTUMProcRupay4(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean UnmatchedTTUMProcMC(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcMC rollBackexe = new UnmatchedTTUMProcMC(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcMC extends StoredProcedure {
		private static final String insert_proc = "MASTERCARD_ISS_POS_SURCH_TTUM";

		public UnmatchedTTUMProcMC(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean UnmatchedTTUMProcMCCROSS(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcMCCROSS rollBackexe = new UnmatchedTTUMProcMCCROSS(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcMCCROSS extends StoredProcedure {
		private static final String insert_proc = "RECON_MC_ACQ_CROSS_RECON_TTUM";

		public UnmatchedTTUMProcMCCROSS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessQSPARC4(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			runTTUMProcessQSPARC4 rollBackexe = new runTTUMProcessQSPARC4(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class runTTUMProcessQSPARC4 extends StoredProcedure {
		private static final String insert_proc = "RECON_MC_ACQ_CROSS_RECON_TTUM";

		public runTTUMProcessQSPARC4(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessRUPAY4INT(UnMatchedTTUMBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("filedate is" + beanObj.getTypeOfTTUM());
		System.out.println("localdt is " + beanObj.getLocalDate());
		try {
			System.out.println("date is " + beanObj.getLocalDate());
			UnmatchedTTUMProcRupay4INT rollBackexe = new UnmatchedTTUMProcRupay4INT(getJdbcTemplate());
			inParams.put("v_filedate", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			if (outParams != null && outParams.get("msg") == "FAILED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			if (outParams.get("msg") == "TTUM ALREADY PROCESSED") {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return false;
		}
	}

	private class UnmatchedTTUMProcRupay4INT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_SURCH";

		public UnmatchedTTUMProcRupay4INT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("v_filedate", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean runTTUMProcessQSPARC4INT(UnMatchedTTUMBean beanObj) {
		return false;
	}

	public boolean runInternationalTTUMProcess(UnMatchedTTUMBean beanObj) {
		return false;
	}

	public HashMap<String, Object> checkData(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String query = "";
		String icwQuery = "";
		try {
			if (beanObj.getCategory().equalsIgnoreCase("RUPAY")
					&& beanObj.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LP")) {
					query = "SELECT COUNT(*) FROM SETTLEMENT_RUPAY_DOM_CBS WHERE DCRS_REMARKS LIKE '%RUPAY_DOM_LATE_PRESENT%' AND FILEDATE = to_DATE('"
							+ beanObj.getLocalDate() + "','dd-MON-yyyy')  ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGE")) {
					query = "SELECT COUNT(*) FROM SETTLEMENT_RUPAY_DOM_CBS WHERE DCRS_REMARKS LIKE '%RUPAY_DOM_SURCHARGE%' AND FILEDATE = to_DATE('"
							+ beanObj.getLocalDate() + "','dd-MON-yyyy')  ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED")) {
					query = "SELECT COUNT(*) FROM SETTLEMENT_RUPAY_DOM_CBS WHERE DCRS_REMARKS LIKE '%RUPAY_DOM_FAILED%' AND FILEDATE = to_DATE('"
							+ beanObj.getLocalDate() + "','dd-MON-yyyy')  ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON2")) {
					query = "SELECT COUNT(*) FROM SETTLEMENT_RUPAY_DOM_CBS WHERE DCRS_REMARKS LIKE '%RUPAY_DOM_UNRECON%' AND FILEDATE = to_DATE('"
							+ beanObj.getLocalDate() + "','dd-MON-yyyy')  ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CBSWNET")) {
					query = "SELECT COUNT(*) FROM SETTLEMENT_RUPAY_DOM_CBS WHERE DCRS_REMARKS LIKE '%RUPAY_UNRECON_2%' AND FILEDATE = to_DATE('"
							+ beanObj.getLocalDate() + "','dd-MON-yyyy')  ";
				}
			} else if (beanObj.getCategory().equalsIgnoreCase("RUPAY")
					&& beanObj.getStSubCategory().equalsIgnoreCase("INTERNATIONAL")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LP")) {
					query = "SELECT COUNT(*) FROM SETTLEMENT_RUPAY_INT_CBS WHERE DCRS_REMARKS LIKE '%RUPAY_INT_LATE_PRESENT%' AND FILEDATE = to_DATE('"
							+ beanObj.getLocalDate() + "','dd-MON-yyyy')  ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGE")) {
					query = "SELECT COUNT(*) FROM SETTLEMENT_RUPAY_INT_CBS WHERE DCRS_REMARKS LIKE '%RUPAY_INT_SURCHARGE%' AND FILEDATE = to_DATE('"
							+ beanObj.getLocalDate() + "','dd-MON-yyyy')  ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED")) {
					query = "SELECT COUNT(*) FROM SETTLEMENT_RUPAY_INT_CBS WHERE DCRS_REMARKS LIKE '%RUPAY_INT_FAILED%' AND FILEDATE = to_DATE('"
							+ beanObj.getLocalDate() + "','dd-MON-yyyy')  ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON2")) {
					query = "SELECT COUNT(*) FROM SETTLEMENT_RUPAY_INT_CBS WHERE DCRS_REMARKS LIKE '%RUPAY_INT_UNRECON%' AND FILEDATE = to_DATE('"
							+ beanObj.getLocalDate() + "','dd-MON-yyyy')  ";
				}
			} else if (beanObj.getCategory().equalsIgnoreCase("NFS")) {
				if (beanObj.getStSubCategory().equals("ACQUIRER")) {
					String maxQUERY = "SELECT MAX(FILEDATE)  FROM SETTLEMENT_NFS_ISS_CBS";
					String date = (String) getJdbcTemplate().queryForObject(maxQUERY, (Object[]) new String[0],
							String.class);
					query = "select COUNT(*) from settlement_nfs_acq_nfs t1 where filedate = to_char(to_date('" + date
							+ "','RRRR-MM-DD hh24:mi:ss'), 'DD-MON-RRRR') and TRANSACTION_DATE = TO_CHAR(TO_DATE('"
							+ beanObj.getLocalDate() + "','DD/MM/YYYY'),'YYMMDD') "
							+ " and t1.dcrs_remarks = 'NFS-ACQ-UNRECON-2'";
				} else if (beanObj.getStSubCategory().equals("ISSUER")) {
					if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATEREV")) {
						query = " select COUNT(*)  from nfs_rev_acq_report T3 where ACQ != 'CUB'  AND to_date(trasn_date,'DD/MM/YYYY') = TO_DATE('"
								+ beanObj.getLocalDate() + "','DD/MM/YYYY') " + " AND NOT EXISTS( " + " select 1 "
								+ " from cbs_rupay_rawdata t1 , nfs_rev_acq_report t2 where to_date(t2.trasn_date,'dd/mm/yyyy') = TO_DATE('"
								+ beanObj.getLocalDate() + "','DD/MM/YYYY') " + "  and "
								+ " to_number(t1.amount) = to_number(t2.requestamt) and t1.REMARKS = t2.CARDNO  and t1.REF_NO = t2.RRN "
								+ "  and E = 'C' AND T2.ACQ !='CUB' " + " AND T1.FILEDATE BETWEEN TO_DATE('"
								+ beanObj.getLocalDate() + "','DD/MM/YYYY') " + " AND TO_DATE('"
								+ beanObj.getLocalDate() + "','DD/MM/YYYY')+5 "
								+ "  AND T3.RRN = T2.RRN AND T3.REQUESTAMT = T2.REQUESTAMT AND T3.FILEDATE = T2.FILEDATE and t3.CARDNO = t2.CARDNO "
								+ " ) ";
					} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED")) {
						String maxQUERY = "SELECT MAX(FILEDATE)  FROM SETTLEMENT_NFS_ISS_CBS";
						String date = (String) getJdbcTemplate().queryForObject(maxQUERY, (Object[]) new String[0],
								String.class);
						query = "select COUNT(*) from settlement_nfs_iss_cbs where filedate = TO_CHAR(TO_DATE('"
								+ beanObj.getLocalDate()
								+ "','DD-MON-YYYY'),'DD/MM/YYYY')  and DCRS_REMARKS = 'CBS-NFS-FAILED' AND TRAN_DATE = TO_CHAR(TO_DATE('"
								+ beanObj.getLocalDate() + "','DD-MON-YYYY'),'DD/MM/YYYY')  ";
						icwQuery = "select COUNT(*) from settlement_iss_iccw_switch where FILEDATE = TO_CHAR(TO_DATE('"
								+ beanObj.getLocalDate()
								+ "','DD-MON-YYYY'),'DD/MM/YYYY') AND dcrs_remarks = 'SWITCH-NPCI-FAILED'";
					} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON2")) {
						query = "select count(*) from settlement_nfs_iss_cbs a where a.filedate = TO_CHAR(TO_DATE('"
								+ beanObj.getLocalDate()
								+ "','DD-MON-YYYY'),'DD/MM/YYYY') and a.DCRS_REMARKS = 'CBS-SWITCH-NFS-UNMATCH' "
								+ " AND EXISTS (select 1 from settlement_nfs_iss_cbs C where C.filedate = TO_DATE('"
								+ beanObj.getLocalDate()
								+ "','DD-Mon-RRRR')+INTERVAL'3'DAY and C.dcrs_remarks = 'CBS-SWITCH-NFS-UNMATCH' and A.ref_no = C.ref_no and A.remarks = C.remarks and A.amount = C.amount) "
								+ " AND TRAN_DATE = TO_CHAR(TO_DATE('" + beanObj.getLocalDate()
								+ "','DD-MON-YYYY'),'DD/MM/YYYY')";
					}
				}
			} else if (beanObj.getCategory().equalsIgnoreCase("VISA")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATEREV")) {
					query = " select COUNT(*)  from nfs_rev_acq_report T3 where ACQ != 'CUB'  AND to_date(trasn_date,'DD/MM/YYYY') = TO_DATE('"
							+ beanObj.getLocalDate() + "','DD/MM/YYYY') " + " AND NOT EXISTS( " + " select 1 "
							+ " from cbs_rupay_rawdata t1 , nfs_rev_acq_report t2 where to_date(t2.trasn_date,'dd/mm/yyyy') = TO_DATE('"
							+ beanObj.getLocalDate() + "','DD/MM/YYYY') " + "  and "
							+ " to_number(t1.amount) = to_number(t2.requestamt) and t1.REMARKS = t2.CARDNO  and t1.REF_NO = t2.RRN "
							+ "  and E = 'C' AND T2.ACQ !='CUB' " + " AND T1.FILEDATE BETWEEN TO_DATE('"
							+ beanObj.getLocalDate() + "','DD/MM/YYYY') " + " AND TO_DATE('" + beanObj.getLocalDate()
							+ "','DD/MM/YYYY')+5 "
							+ "  AND T3.RRN = T2.RRN AND T3.REQUESTAMT = T2.REQUESTAMT AND T3.FILEDATE = T2.FILEDATE and t3.CARDNO = t2.CARDNO "
							+ " ) ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED")) {
					String maxQUERY = "SELECT MAX(FILEDATE)  FROM SETTLEMENT_VISA_ISS_CBS";
					String date = (String) getJdbcTemplate().queryForObject(maxQUERY, (Object[]) new String[0],
							String.class);
					query = "select COUNT(*) from settlement_visa_iss_cbs where filedate = to_char(to_date('" + date
							+ "','RRRR-MM-DD hh24:mi:ss'), 'DD-MON-RRRR')  and DCRS_REMARKS = 'VISA_SUR-FAILED' ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON2")) {
					String maxQUERY = "SELECT MAX(FILEDATE)  FROM SETTLEMENT_NFS_ISS_CBS";
					String date = (String) getJdbcTemplate().queryForObject(maxQUERY, (Object[]) new String[0],
							String.class);
					query = "select count(1)  from settlement_visa_iss_cbs T1 where T1.filedate =  '"
							+ beanObj.getLocalDate() + "' and T1.dcrs_remarks = 'VISA_SUR-UNRECON-3' and  exists "
							+ "(select 1 from settlement_visa_iss_cbs b where b.filedate = " + " TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-Mon-RRRR')+INTERVAL'10'DAY and b.dcrs_remarks "
							+ " = 'VISA_SUR-UNRECON-3' and T1.ref_no = b.ref_no and T1.remarks = b.remarks "
							+ " and T1.amount = b.amount) ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND")) {
					query = "select COUNT(*) from settlement_visa_iss_visa where DCRS_REMARKS = 'VISA_CBS_REFUND' AND FILEDATE = '"
							+ beanObj.getLocalDate() + "'  ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGE")) {
					query = "select COUNT(*) FROM SWITCH_RAwdata a , visa_VISA_RAWDATA B WHERE A.authnum = b.authorization_code and a.pan = b.card_NUMBER AND TO_CHAR(to_date(local_date, 'dd/mm/yyyy'),'MMDD') = purchase_date and  b.FILEDATE = '"
							+ beanObj.getLocalDate()
							+ "' AND b.TC = '05' AND b.DESTINATION_CURR_CODE = '356' AND b.SOURCE_CURR_CODE= '356' and a.amount != 0 and b.destination_amount != 0 "
							+ " and (b.destination_amount - a.amount) != 0 and (b.destination_amount - a.amount) > 0 ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CREDIT_SURCHARGE")) {
					query = "select COUNT(*) FROM SWITCH_RAwdata a , visa_VISA_RAWDATA B WHERE A.authnum = b.authorization_code and a.pan = b.card_NUMBER AND TO_CHAR(to_date(local_date, 'dd/mm/yyyy'),'MMDD') = purchase_date and  b.FILEDATE = '"
							+ beanObj.getLocalDate()
							+ "' AND b.TC = '05' AND b.DESTINATION_CURR_CODE = '356' AND b.SOURCE_CURR_CODE= '356' and a.amount != 0 and b.destination_amount != 0 "
							+ " and (a.amount_EQUIV - b.destination_amount) > 0 and b.destination_amount < a.amount_EQUIV ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("NET ORIGINAL CREDIT")) {
					query = "select COUNT(*) from settlement_visa_iss_visa where DCRS_REMARKS = 'VISA_ORIGINAL_CREDIT' AND FILEDATE = '"
							+ beanObj.getLocalDate() + "'  ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("WITHDRAWREVERSAL")) {
					query = "SELECT count(1) FROM SWITCH_RAwdata a , visa_VISA_RAWDATA B WHERE A.authnum = b.authorization_code and a.pan = b.card_NUMBER AND  /*-- to_char(to_date(local_date, 'dd/mm/yyyy'),'DDMM') = purchase_date /* to_date(local_date, 'dd/mm/yyyy') = to_date(purchase_date, 'mmdd') and */  b.FILEDATE = '"
							+ beanObj.getLocalDate()
							+ "' AND b.TC = '27' AND EXISTS (SELECT 1 FROM cbs_rupay_rawdata C WHERE A.ISSUER = C.REF_NO "
							+ " AND C.REMARKS = A.PAN AND A.AMOUNT = C.AMOUNT AND  SUBSTR(C.particularals,0,3) != 'CUB' AND C.E = 'D' AND NOT EXISTS ( "
							+ " SELECT 1 FROM cbs_rupay_rawdata D WHERE "
							+ " SUBSTR(D.particularals,0,3) != 'CUB' AND C.FILEDATE = D.FILEDATE AND C.REF_NO = D.REF_NO "
							+ " AND C.REMARKS = D.REMARKS AND C.AMOUNT = D.AMOUNT and d.E = 'C'))";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LP")) {
					query = "select count(1) FROM SWITCH_RAwdata a RIGHT JOIN visa_VISA_RAWDATA B ON A.authnum = b.authorization_code and a.pan = b.card_NUMBER WHERE  /*-- to_char(to_date(local_date, 'dd/mm/yyyy'),'DDMM') = purchase_date to_date(local_date, 'dd/mm/yyyy') = to_date(purchase_date, 'mmdd') and */  b.filedate = '"
							+ beanObj.getLocalDate() + "' and b.TC = '05' and exists ( "
							+ " SELECT 1 FROM TTUM_VISA_UNRECON_CBS c, SWITCH_RAWDATA d  WHERE c.reference_number = d.ISSUER AND c.account_number = d.acctnum "
							+ " AND C.FILEDATE BETWEEN  TO_DATE('" + beanObj.getLocalDate()
							+ "','DD-MON-RRRR') - INTERVAL'71'DAY AND  TO_DATE('" + beanObj.getLocalDate()
							+ "','DD-MON-RRRR') - INTERVAL'11'DAY AND "
							+ " d.authnum = b.authorization_code AND d.PAN = b.card_number " + " )";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON_SWITCH")) {
					query = "select count(1) from  CBS_RUPAY_RAWDATA D WHERE D.FILEDATE = '" + beanObj.getLocalDate()
							+ "' AND D.OTHH_CODE = '03224030040600032' " + "AND NOT EXISTS "
							+ " (SELECT 1 FROM CBS_RUPAY_RAWDATA F WHERE D.REF_NO = F.REF_NO AND D.AMOUNT = F.AMOUNT AND D.REMARKS = F.REMARKS AND F.E = 'C' )"
							+ "and NOT exists "
							+ " (select 1 from visa_visa_rawdata c where c.filedate between TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-Mon-RRRR') " + " AND TO_DATE('" + beanObj.getLocalDate()
							+ "','DD-Mon-RRRR')+INTERVAL'10'DAY AND  "
							+ " SUBSTR(D.TRAN_ID, 1,6) = C.AUTHORIZATION_CODE AND D.REMARKS = C.CARD_NUMBER ) "
							+ "  AND NOT EXISTS ( SELECT 1 FROM SWITCH_RAWDATA B WHERE FILEDATE = '"
							+ beanObj.getLocalDate() + "' AND B.ISSUER = D.REF_NO " + " AND B.PAN = D.REMARKS) ";
				}
			}
			int checkCount = 0;
			int icwCount = 0;
			System.out.println("COUNT QUERY " + query);
			try {
				if (beanObj.getCategory().equalsIgnoreCase("NFS")
						&& beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED"))
					icwCount = ((Integer) getJdbcTemplate().queryForObject(icwQuery, (Object[]) new String[0],
							Integer.class)).intValue();
				checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
						Integer.class)).intValue();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("DATA COUNT IS " + checkCount);
			if (beanObj.getCategory().equalsIgnoreCase("NFS")
					&& beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED")) {
				if (checkCount > 0 || icwCount > 0) {
					output.put("result", Boolean.valueOf(true));
					output.put("msg", "PROCESSING STARTED");
				} else {
					output.put("result", Boolean.valueOf(false));
					output.put("msg", "DATA NOT PRESENT FOR TTUM GENERATION");
				}
			} else if (checkCount > 0) {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "PROCESSING STARTED --");
			} else {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "DATA NOT PRESENT FOR TTUM GENERATION");
			}
			return output;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public HashMap<String, Object> checkTTUMProcessed(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String query = "";
		try {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				query = "SELECT COUNT(*) FROM nfs_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM() + "'";
			} else {
				query = "SELECT COUNT(*) FROM nfs_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM() + "'";
			}
			System.out.println("main _  QUERY" + query);
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			if (checkCount > 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is already Processed. Please download report");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is not processed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkTTUMProcessedJCB(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String query = "";
		try {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				query = "SELECT COUNT(*) FROM jcb_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM() + "'";
			} else {
				query = "SELECT COUNT(*) FROM nfs_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM() + "'";
			}
			System.out.println("main _  QUERY" + query);
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			if (checkCount > 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is already Processed. Please download report");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is not processed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkTTUMProcessedDFS(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String query = "";
		try {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				query = "SELECT COUNT(*) FROM dfs_acq_recon_ttums WHERE  FILEDATE=STR_TO_DATE('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM() + "'";
			} else {
				query = "SELECT COUNT(*) FROM nfs_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM() + "'";
			}
			System.out.println("main _  QUERY" + query);
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			if (checkCount > 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is already Processed. Please download report");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is not processed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkTTUMProcessedICD(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String query = "";
		try {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					query = "SELECT COUNT(*) FROM ICD_ACQ_PROACTIVE   WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY') ";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					query = "SELECT COUNT(*) FROM ICD_ACQ_RECON_DROP_TTUM  WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else {
					query = "SELECT COUNT(*) FROM NFS_ISS_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM()
							+ "'";
				}
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				query = "SELECT COUNT(*) FROM ICD_ISS_RECON_PROACTIVE  WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY')";
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				query = "SELECT COUNT(*) FROM ICD_ISS_RECON_DROP  WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY')";
			} else {
				query = "SELECT COUNT(*) FROM NFS_ISS_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY')";
			}
			System.out.println("main _  QUERY" + query);
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			if (checkCount > 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is already Processed. Please download report");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is not processed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkTTUMProcessedICCW(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String query = "";
		try {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
					query = "select Count(*) FROM nfs_iccw_acq_recon_ttums WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y%m/%d')  AND TTUM_TYPE='LORO CREDIT'";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
					query = "select Count(*)  FROM nfs_iccw_acq_recon_ttums WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')  AND TTUM_TYPE='LORO DEBIT'";
				} else {
					query = "select Count(*) FROM nfs_iccw_acq_recon_ttums WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')  AND TTUM_TYPE='LORO DEBIT'";
				}
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				query = "select Count(*)  FROM nfs_iccw_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d')  AND TTUM_TYPE='PROACTIVE'";
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				query = "select Count(*) FROM nfs_iccw_iss_recon_ttums WHERE FILEDATE=STR_TO_DATE('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d')  AND TTUM_TYPE='DROP'";
			} else {
				query = "SELECT COUNT(*) FROM nfs_iccw_iss_recon_ttums WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY')";
			}
			System.out.println("main _  QUERY" + query);
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			System.out.println("Count "+ checkCount);
			if (checkCount > 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is already Processed. Please download report");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is not processed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkTTUMProcessedNFSRUPAY(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String query = "";
		try {
			query = "SELECT COUNT(*) FROM NFSISS_RUPAYINT_ROUT_TTUM   WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY') ";
			System.out.println("main _  QUERY" + query);
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			if (checkCount > 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is already Processed. Please download report");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is not processed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkrunProcessFinacleTTUM(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String query = "";
		try {
			query = "SELECT COUNT(*) FROM FINA_FINACLE_TTUM   WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY')";
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			System.out.println("main _  QUERY " + query + " d " + checkCount);
			if (checkCount == 0) {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is not processed");
			} else {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is already Processed. Please download report");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkTTUMProcessedVISA(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String query = "";
		try {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ISS")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					query = "SELECT COUNT(*) FROM visa_iss_drop_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					query = "SELECT COUNT(*) FROM visa_iss_late_presentment_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					query = "SELECT COUNT(*) FROM visa_iss_proactive_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().contains("DEBIT SURCHARGE")) {
					query = "SELECT COUNT(*) FROM visa_iss_dr_surch_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CREDIT SURCHARGE")) {
					query = "SELECT COUNT(*) FROM visa_iss_cr_surch_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")) {
					query = "SELECT COUNT(*) FROM visa_iss_org_wdl_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND")) {
					query = "SELECT COUNT(*) FROM visa_iss_dom_refund_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND_REV")) {
					query = "SELECT COUNT(*) FROM visa_iss_dom_refund_rev_ttum WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND_INT")) {
					query = "SELECT COUNT(*) FROM visa_iss_int_refund_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else {
					query = "SELECT COUNT(*) FROM visa_iss_int_refund_rev_ttum WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ACQ DOM ATM")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
					query = "SELECT COUNT(*) FROM visa_acq_dom_atm_loro_debit_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
					query = "SELECT COUNT(*) FROM visa_acq_dom_atm_loro_credit_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					query = "SELECT COUNT(*) FROM VISA_DOM_POS_LATE_PRESENTMENT_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					query = "SELECT COUNT(*) FROM VISA_DOM_POS_PROACTIVE_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DEBIT SURCHARGE")) {
					query = "SELECT COUNT(*) FROM VISA_DOM_POS_DR_SURCH_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CREDIT SURCHARGE")) {
					query = "SELECT COUNT(*) FROM VISA_DOM_POS_CR_SURCH_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")) {
					query = "SELECT COUNT(*) FROM VISA_DOM_POS_ORG_WDL_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else {
					query = "SELECT COUNT(*) FROM VISA_DOM_POS_DROP_CHARGES_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ACQ INT ATM")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					query = "SELECT COUNT(*) FROM VISA_INT_POS_DROP_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND")) {
					query = "SELECT COUNT(*) FROM VISA_INT_POS_REFUND_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					query = "SELECT COUNT(*) FROM VISA_INT_POS_LATE_PRESENTMENT_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT CHARGES")) {
					query = "SELECT COUNT(*) FROM VISA_INT_POS_LATE_PRESENTMENT_CHARGES_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					query = "SELECT COUNT(*) FROM VISA_INT_POS_PROACTIVE_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DEBIT SURCHARGE")) {
					query = "SELECT COUNT(*) FROM VISA_INT_POS_DR_SURCH_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CREDIT SURCHARGE")) {
					query = "SELECT COUNT(*) FROM VISA_INT_POS_CR_SURCH_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")) {
					query = "SELECT COUNT(*) FROM VISA_INT_POS_ORG_WDL_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
					query = "SELECT COUNT(*) FROM visa_acq_int_atm_loro_debit_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
					query = "SELECT COUNT(*) FROM visa_acq_int_atm_loro_credit_ttum_data WHERE FILEDATE=STR_TO_DATE('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ISS INT ATM")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					query = "SELECT COUNT(*) FROM VISA_INT_ATM_DROP_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					query = "SELECT COUNT(*) FROM VISA_INT_ATM_LATE_PRESENTMENT_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					query = "SELECT COUNT(*) FROM VISA_INT_ATM_PROACTIVE_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DEBIT SURCHARGE")) {
					query = "SELECT COUNT(*) FROM VISA_INT_ATM_DR_SURCH_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CREDIT SURCHARGE")) {
					query = "SELECT COUNT(*) FROM VISA_INT_ATM_CR_SURCH_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")) {
					query = "SELECT COUNT(*) FROM VISA_INT_ATM_ORG_WDL_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE CHARGES")) {
					query = "SELECT COUNT(*) FROM VISA_INT_ATM_PROACTIVE_CHARGES_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP CHARGES")) {
					query = "SELECT COUNT(*) FROM VISA_INT_ATM_DROP_CHARGES_TTUM_DATA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
							+ beanObj.getLocalDate() + "','DD-MM-YY')";
				}
			} else {
				query = "SELECT COUNT(*) FROM NFS_ISS_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM() + "'";
			}
			System.out.println("main _  QUERY" + query);
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			if (checkCount > 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is already Processed. Please download report");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is not processed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkTTUMProcessedVISANB(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String query = "", query2 = "";
		try {
			query = "SELECT COUNT(*) FROM SETTLEMENT_VISA_INT_ISS_ATM_VISA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY')";
			query2 = "SELECT COUNT(*) FROM SETTLEMENT_VISA_DOM_ISS_ATM_VISA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY')";
			System.out.println("main _  QUERY" + query);
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			int checkCount2 = ((Integer) getJdbcTemplate().queryForObject(query2, (Object[]) new String[0],
					Integer.class)).intValue();
			if (checkCount > 0 && checkCount2 > 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is already Processed. Please download report");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is not processed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkTTUMProcessedCTCSETTLEMENT(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String query = "", query2 = "";
		try {
			query = "SELECT COUNT(*) FROM SETTLEMENT_NFS_ACQ_C2C_CBS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY')";
			query2 = "SELECT COUNT(*) FROM SETTLEMENT_NFS_ISS_C2C_CBS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
					+ beanObj.getLocalDate() + "','DD-MM-YY')";
			System.out.println("main _  QUERY" + query);
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			int checkCount2 = ((Integer) getJdbcTemplate().queryForObject(query2, (Object[]) new String[0],
					Integer.class)).intValue();
			if (checkCount > 0 && checkCount2 > 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is already Processed. Please download report");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is not processed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkTTUMProcessedATNPOSCROSSROUNTING(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String query = "", query2 = "";
		try {
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ATM")) {
				query = "SELECT COUNT(*) FROM SETTLEMENT_VISA_INT_ISS_ATM_VISA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY')";
				query2 = "SELECT COUNT(*) FROM SETTLEMENT_VISA_DOM_ISS_ATM_VISA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY')";
			} else {
				query = "SELECT COUNT(*) FROM SETTLEMENT_VISA_INT_ISS_POS_VISA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY')";
				query2 = "SELECT COUNT(*) FROM SETTLEMENT_VISA_DOM_ISS_POS_VISA WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY')";
			}
			System.out.println("main _  QUERY" + query);
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			int checkCount2 = ((Integer) getJdbcTemplate().queryForObject(query2, (Object[]) new String[0],
					Integer.class)).intValue();
			if (checkCount > 0 && checkCount2 > 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is already Processed. Please download report");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is not processed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkTTUMProcessedCTC(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String query = "";
		try {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				query = "SELECT COUNT(*) FROM NFS_C2C_ACQ_RECON_TTUMS WHERE TO_DATE(FILEDATE,'DD-MM-YY')=TO_DATE('"
						+ beanObj.getLocalDate() + "','DD-MM-YY') AND TTUM_TYPE = '" + beanObj.getTypeOfTTUM() + "'";
			} else {
				query = "SELECT COUNT(*) FROM c2c_iss_recon_ttums WHERE FILEDATE = DATE_FORMAT('"
						+ beanObj.getLocalDate().replaceAll("/", "-") + "','%Y-%m-%d')AND TTUM_TYPE = '"
						+ beanObj.getTypeOfTTUM() + "'";
			}
			System.out.println("main _  QUERY" + query);
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			if (checkCount > 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is already Processed. Please download report");
			} else {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is not processed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkTTUMProcessedRUPAY(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String fileDate = "";
		String sett_table = "";
		String fetch_condition = "";
		try {
			String query = "";
			if (beanObj.getCategory().contains("RUPAY")) {
				if (beanObj.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
					if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
						query = "SELECT count(*) FROM rupay_decl_ttum  where FILEDATE=STR_TO_DATE('"+ beanObj.getFileDate() + "','%Y/%m/%d')";
					} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
						query = "SELECT count(*) FROM rupay_proactiv_ttum  where FILEDATE=STR_TO_DATE('"
								+ beanObj.getFileDate() + "','%Y/%m/%d')";
					} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
						query = "SELECT count(*) FROM rupay_drop_ttum  where FILEDATE=STR_TO_DATE('"
								+ beanObj.getFileDate() + "','%Y/%m/%d')";
					} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
						query = "SELECT count(*) FROM rupay_dom_latepresentment  where FILEDATE=STR_TO_DATE('"
								+ beanObj.getFileDate() + "','%Y/%m/%d')";
					} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
						query = "SELECT count(*) FROM dom_offline_pres_ttum  where FILEDATE=STR_TO_DATE('"
								+ beanObj.getFileDate() + "','%Y/%m/%d')";
					} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
						query = "SELECT count(*) FROM rupay_dom_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
								+ beanObj.getFileDate() + "','%Y/%m/%d')";
					} else {
						query = "SELECT count(*) FROM rupay_dom_cr_surch_ttum  where FILEDATE=STR_TO_DATE('"
								+ beanObj.getFileDate() + "','%Y/%m/%d')";
					}
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
					query = "SELECT count(*) FROM rupay_int_decl_ttum  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					query = "SELECT count(*) FROM rupay_proactiv_int_ttum  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					query = "SELECT count(*) FROM rupay_drop_int_ttum  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					query = "SELECT count(*) FROM rupay_int_ofline_pres_ttum  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					query = "SELECT count(*) FROM rupay_int_latepresentment  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
					query = "SELECT count(*) FROM rupay_int_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else {
					query = "SELECT count(*) FROM rupay_int_cr_surch_ttum  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
					query = "SELECT count(*) FROM rupay_dom_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					query = "SELECT count(*) FROM rupay_dom_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					query = "SELECT count(*) FROM rupay_dom_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					query = "SELECT count(*) FROM rupay_dom_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					query = "SELECT count(*) FROM qsp_dom_offline_pres_ttum  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
					query = "SELECT count(*) FROM rupay_dom_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else {
					query = "SELECT count(*) FROM rupay_dom_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				}
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
				query = "SELECT count(*) FROM rupay_dom_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
						+ beanObj.getFileDate() + "','%Y/%m/%d')";
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				query = "SELECT count(*) FROM rupay_dom_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
						+ beanObj.getFileDate() + "','%Y/%m/%d')";
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				query = "SELECT count(*) FROM rupay_dom_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
						+ beanObj.getFileDate() + "','%Y/%m/%d')";
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
				query = "SELECT count(*) FROM rupay_dom_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
						+ beanObj.getFileDate() + "','%Y/%m/%d')";
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
				query = "SELECT count(*) FROM rupay_dom_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
						+ beanObj.getFileDate() + "','%Y/%m/%d')";
			} else {
				query = "SELECT count(*) FROM rupay_dom_dr_surch_ttum  where FILEDATE=STR_TO_DATE('"
						+ beanObj.getFileDate() + "','%Y/%m/%d')";
			}
			System.out.println("main _  QUERY" + query);
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			if (checkCount > 0) {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is already Processed. Please download report");
			} else {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is not processed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkTTUMProcessedMASTERCARD(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		String fileDate = "";
		String sett_table = "";
		String fetch_condition = "";
		try {
			String query = "";
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER_DOM")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_DEBIT")) {
					query = "SELECT count(*) FROM mc_acq_dom_atm_loro_debit_ttum  WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_CREDIT")) {
					query = "SELECT count(*) FROM mc_acq_dom_atm_loro_credit_ttum     WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CROSS")) {
					query = "SELECT count(*) FROM mc_acq_cross_recon_ttum    WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					query = "SELECT count(*) FROM MC_ISS_POS_LATEPRES_TTUM   WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
							+ beanObj.getFileDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					query = "SELECT count(*) FROM DOM_OFFLINE_PRES_TTUM   WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
							+ beanObj.getFileDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
					query = "SELECT count(*) FROM RUPAY_DOM_DR_SURCH_TTUM    WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
							+ beanObj.getFileDate() + "','DD-MM-YY')";
				} else {
					query = "SELECT count(*) FROM RUPAY_DOM_CR_SURCH_TTUM    WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
							+ beanObj.getFileDate() + "','DD-MM-YY')";
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER_INT")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_DEBIT")) {
					query = "SELECT count(*) FROM mc_acq_int_atm_loro_debit_ttum  WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_CREDIT")) {
					query = "SELECT count(*) FROM mc_acq_int_atm_loro_credit_ttum     WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CROSS")) {
					query = "SELECT count(*) FROM mc_acq_cross_recon_ttum    WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					query = "SELECT count(*) FROM MC_ISS_POS_LATEPRES_TTUM   WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
							+ beanObj.getFileDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					query = "SELECT count(*) FROM DOM_OFFLINE_PRES_TTUM   WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
							+ beanObj.getFileDate() + "','DD-MM-YY')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
					query = "SELECT count(*) FROM RUPAY_DOM_DR_SURCH_TTUM    WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
							+ beanObj.getFileDate() + "','DD-MM-YY')";
				} else {
					query = "SELECT count(*) FROM RUPAY_DOM_CR_SURCH_TTUM    WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
							+ beanObj.getFileDate() + "','DD-MM-YY')";
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ISSUER")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
					query = "SELECT count(*) FROM   rupay_decl_ttum WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					query = "SELECT count(*) FROM  mastercard_proactive_pos_ttum    WHERE  filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					query = "SELECT count(*) FROM mc_iss_pos_drop_ttum   WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					query = "SELECT count(*) FROM  mc_iss_pos_latepres_ttum  WHERE  filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					query = "SELECT count(*) FROM  dom_offline_pres_ttum  WHERE  filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().contains("SURCHARGED")) {
					query = "SELECT count(*) FROM mc_dom_iss_dr_surch    WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ATMSURCHARGE")) {
					query = "SELECT count(*) FROM mc_iss_atm_surcharge    WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				} else {
					query = "SELECT count(*) FROM mc_dom_iss_cr_surch    WHERE filedate= str_to_date('"
							+ beanObj.getFileDate() + "','%Y/%m/%d')";
				}
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
				query = "SELECT count(*) FROM RUPAY_INT_DECL_TTUM  WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+ beanObj.getFileDate() + "','DD-MM-YY')";
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				query = "SELECT count(*) FROM RUPAY_PROACTIV_INT_TTUM    WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+ beanObj.getFileDate() + "','DD-MM-YY')";
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				query = "SELECT count(*) FROM RUPAY_DROP_INT_TTUM  WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+ beanObj.getFileDate() + "','DD-MM-YY')";
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
				query = "SELECT count(*) FROM REUPAY_DOM_LATEPRESENTMENT   WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+ beanObj.getFileDate() + "','DD-MM-YY')";
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")) {
				query = "SELECT count(*) FROM RUPAY_INT_DR_SURCH_TTUM     WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+ beanObj.getFileDate() + "','DD-MM-YY')";
			} else {
				query = "SELECT count(*) FROM RUPAY_INT_CR_SURCH_TTUM     WHERE TO_DATE(FILEDATE,'DD-MM-YY')= TO_DATE('"
						+ beanObj.getFileDate() + "','DD-MM-YY')";
			}
			System.out.println("main _  QUERY" + query);
			int checkCount = ((Integer) getJdbcTemplate().queryForObject(query, (Object[]) new String[0],
					Integer.class)).intValue();
			if (checkCount > 0) {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "TTUM is already Processed. Please download report");
			} else {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "TTUM is not processed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in checkTTUMProcessed " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
		}
		return output;
	}

	public HashMap<String, Object> checkReconProcessed(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		try {
			String checkCompareCount = "SELECT COUNT(*) FROM main_file_upload_dtls where filedate = ? AND CATEGORY = ? and FILE_SUBCATEGORY = ? and comapre_flag = 'Y'";
			int compareCount = ((Integer) getJdbcTemplate().queryForObject(checkCompareCount,
					new Object[] { beanObj.getFileDate(), beanObj.getCategory(), beanObj.getStSubCategory() },
					Integer.class)).intValue();
			if (compareCount >= 3) {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "Recon is Processed");
			} else if (compareCount == 2 && beanObj.getStSubCategory().equalsIgnoreCase("INTERNATIONAL")) {
				output.put("result", Boolean.valueOf(true));
				output.put("msg", "Recon is Processed");
			} else {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "Recon is not processed!");
			}
		} catch (Exception e) {
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception occurred while checking recon processed!");
		}
		return output;
	}

	public String createTTUMFile(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public boolean checkAndMakeDirectory(UnMatchedTTUMBean beanObj) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			Date date = sdf.parse(beanObj.getFileDate());
			sdf = new SimpleDateFormat("dd-MM-yyyy");
			String stnewDate = sdf.format(date);
			String stPath = OUTPUT_FOLDER1;
			File folder = new File(stPath);
			if (folder.exists())
				folder.delete();
			folder.mkdir();
			logger.info("Path is " + beanObj.getStPath() + File.separator + beanObj.getCategory());
			File checkFile = new File(String.valueOf(beanObj.getStPath()) + File.separator + beanObj.getCategory());
			if (checkFile.exists())
				FileUtils.forceDelete(
						new File(String.valueOf(beanObj.getStPath()) + File.separator + beanObj.getCategory()));
			File directory = new File(String.valueOf(beanObj.getStPath()) + File.separator + beanObj.getCategory());
			if (!directory.exists())
				directory.mkdir();
			directory = new File(String.valueOf(beanObj.getStPath()) + File.separator + beanObj.getCategory()
					+ File.separator + "02-01-2024");
			if (!directory.exists())
				directory.mkdir();
			beanObj.setStPath(String.valueOf(beanObj.getStPath()) + File.separator + beanObj.getCategory()
					+ File.separator + "02-01-2024");
			return true;
		} catch (Exception e) {
			logger.info("Exception in checkAndMakeDirectory " + e);
			return false;
		}
	}

	public List<Object> getTTUMData(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public List<Object> getVisaTTUMData(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public List<Object> getSurchargedata(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public List<Object> getRupayTTUMData(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public List<Object> getIntRupayTTUMData(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public List<Object> getNIHTTUMData(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public HashMap<String, Object> checkTranReconDate(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String tableName = "";
		try {
			tableName = "SETTLEMENT_" + beanObj.getCategory() + "_" + beanObj.getStSubCategory().substring(0, 3) + "_";
			if (beanObj.getCategory().equalsIgnoreCase("RUPAY")
					&& beanObj.getStSubCategory().contentEquals("DOMESTIC")) {
				tableName = "SETTLEMENT_RUPAY_DOM_";
			} else if (beanObj.getCategory().equalsIgnoreCase("RUPAY")
					&& beanObj.getStSubCategory().contentEquals("INTERNATIONAL")) {
				tableName = "SETTLEMENT_RUPAY_INT_";
			}
			if (beanObj.getFileName() != null && !beanObj.getFileName().equalsIgnoreCase("")) {
				if (beanObj.getFileName().equalsIgnoreCase("NETWORK")) {
					tableName = tableName;
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON2")) {
					tableName = String.valueOf(tableName) + "CBS";
				} else {
					tableName = String.valueOf(tableName) + beanObj.getFileName();
				}
			} else if (beanObj.getCategory().equalsIgnoreCase("NFS")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATEREV")) {
					tableName = String.valueOf(tableName) + "NFS";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED")) {
					tableName = String.valueOf(tableName) + "CBS";
				} else {
					tableName = String.valueOf(tableName) + "SWITCH";
				}
			}
			logger.info("Table name is " + tableName);
			String checckCount = "select COUNT(*) FROM " + tableName + " WHERE FILEDATE >= '" + beanObj.getLocalDate()
					+ "'";
			int getCount = ((Integer) getJdbcTemplate().queryForObject(checckCount, new Object[0], Integer.class))
					.intValue();
			if (getCount > 0) {
				output.put("result", Boolean.valueOf(true));
			} else {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "Recon Process Date is Smaller than Local Date");
			}
		} catch (Exception e) {
			logger.info("Exception in checking recon and local date " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception in checking recon and local date ");
		}
		return output;
	}

	public void generateExcelTTUM(String stPath, String FileName, List<Object> ExcelData, String TTUMName,
			HttpServletResponse response, boolean ZipFolder) {
		List<String> files = new ArrayList<>();
		try {
			logger.info("Filename is " + FileName);
			List<Object> TTUMData = (List<Object>) ExcelData.get(1);
			List<String> Excel_Headers = (List<String>) ExcelData.get(0);
			OutputStream fileOut = new FileOutputStream(String.valueOf(stPath) + File.separator + FileName);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Report");
			HSSFRow header = sheet.createRow(0);
			for (int i = 0; i < TTUMData.size(); i++) {
				HSSFRow rowEntry = sheet.createRow(i + 1);
				Map<String, String> map_data = (Map<String, String>) TTUMData.get(i);
				if (map_data.size() > 0)
					for (int m = 0; m < Excel_Headers.size(); m++)
						rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers.get(m)));
			}
			workbook.write(fileOut);
			fileOut.close();
			File file = new File(stPath);
			String[] filelist = file.list();
			byte b;
			int j;
			String[] arrayOfString1;
			for (j = (arrayOfString1 = filelist).length, b = 0; b < j;) {
				String Names = arrayOfString1[b];
				logger.info("name is " + Names);
				files.add(String.valueOf(stPath) + File.separator + Names);
				b++;
			}
			FileOutputStream fos = new FileOutputStream(String.valueOf(stPath) + File.separator + "EXCEL_TTUMS.zip");
			ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
			try {
				for (String filespath : files) {
					File input = new File(filespath);
					FileInputStream fis = new FileInputStream(input);
					ZipEntry ze = new ZipEntry(input.getName());
					zipOut.putNextEntry(ze);
					byte[] tmp = new byte[4096];
					int size = 0;
					while ((size = fis.read(tmp)) != -1)
						zipOut.write(tmp, 0, size);
					zipOut.flush();
					fis.close();
				}
				zipOut.close();
			} catch (Exception fe) {
				System.out.println("Exception in zipping is " + fe);
			}
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);
		}
	}

	public void generateRupayExcelTTUM(String stPath, List<Object> ExcelData, String TTUMName,
			HttpServletResponse response) {
		List<String> files = new ArrayList<>();
		try {
			File file = new File(stPath);
			String[] filelist = file.list();
			byte b;
			int i;
			String[] arrayOfString1;
			for (i = (arrayOfString1 = filelist).length, b = 0; b < i;) {
				String Names = arrayOfString1[b];
				logger.info("name is " + Names);
				files.add(String.valueOf(stPath) + File.separator + Names);
				b++;
			}
			FileOutputStream fos = new FileOutputStream(String.valueOf(stPath) + File.separator + "EXCEL_TTUMS.zip");
			ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
			try {
				for (String filespath : files) {
					File input = new File(filespath);
					FileInputStream fis = new FileInputStream(input);
					ZipEntry ze = new ZipEntry(input.getName());
					zipOut.putNextEntry(ze);
					byte[] tmp = new byte[4096];
					int size = 0;
					while ((size = fis.read(tmp)) != -1)
						zipOut.write(tmp, 0, size);
					zipOut.flush();
					fis.close();
				}
				zipOut.close();
			} catch (Exception fe) {
				System.out.println("Exception in zipping is " + fe);
			}
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);
		}
	}

	public HashMap<String, Object> checkInternationalTTUMProcessed(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public List<Object> getInternationalTTUMData(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public void generateInternationalTTUMFile(String stPath, String FileName, List<Object> TTUMData) {
	}

	public HashMap<String, Object> checkNIHRecords(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public List<Object> getNIHReport(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public ArrayList<String> getDailyColumnList(String tableName) {
		return null;
	}

	public HashMap<String, Object> checkCardToCardTTUMProcessed(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public boolean runCardToCardTTUMProcess(UnMatchedTTUMBean beanObj) {
		return false;
	}

	public List<Object> getCardToCardTTUMData(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public HashMap<String, Object> checkInternationalUpload(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		try {
			String checkPresentment = "SELECT COUNT(*) FROM rupay_international_presentment WHERE FILEDATE = '"
					+ beanObj.getFileDate() + "'";
			int presentmentCount = ((Integer) getJdbcTemplate().queryForObject(checkPresentment, new Object[0],
					Integer.class)).intValue();
			if (presentmentCount == 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "Please upload Presentment file");
			} else {
				output.put("result", Boolean.valueOf(true));
			}
		} catch (Exception e) {
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating");
			logger.info("Exception in checkInternationalUpload " + e);
		}
		return output;
	}

	public boolean fundingcheckAndMakeDirectory(UnMatchedTTUMBean beanObj) {
		File folder = new File(OUTPUT_FOLDER);
		if (folder.exists())
			folder.delete();
		folder.mkdir();
		File file1 = new File(folder, "FUNDING");
		try {
			if (file1.exists())
				file1.delete();
			file1.createNewFile();
			String newPath = String.valueOf(OUTPUT_FOLDER) + File.separator + beanObj.getStSubCategory();
			System.out.println("path of new file is---------- " + newPath);
			return true;
		} catch (Exception e) {
			logger.info("Exception in checkAndMakeDirectory " + e);
			return false;
		}
	}

	public List<Object> getFundingdata(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public List<RecordCount> downloadRawdataSummary() {
		return null;
	}
}
