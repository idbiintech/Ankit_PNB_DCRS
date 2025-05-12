package com.recon.service.impl;

import com.recon.model.SettlementBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.NFSUnmatchTTUMService;
import com.recon.util.OracleConn;
import com.recon.util.SearchData;
import com.recon.util.ViewFiles;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;

public class NFSUnmatchTTUMServiceImpl extends JdbcDaoSupport implements NFSUnmatchTTUMService {
	private static final Logger logger = Logger.getLogger(com.recon.service.impl.NFSUnmatchTTUMServiceImpl.class);

	private static final String O_ERROR_MESSAGE = "O_ERROR_MESSAGE";

	private static final String O_FILE_TYPE = "O_FILE_TYPE";

	private static final String O_FILE_COUNT = "O_FILE_COUNT";

	private static final String O_FILES_UPLOADED = "O_FILES_UPLOADED";

	private static final String O_REMARK = "O_REMARK";

	public boolean deleteViewFile(String type, String fromDate, String username, String filename, String cycle)
			throws Exception, SQLException {
		return false;
	}

	public int checkAdjustmentTTUMProcessed(String adjtype, String localDate) {
		String query = "select count(*) from nfs_adjustment_ttum where filedate = str_to_date('" + localDate
				+ "', '%Y/%m/%d') and adjtype = '" + adjtype + "'";
		int count = ((Integer) getJdbcTemplate().queryForObject(query, new Object[0], Integer.class)).intValue();
		return count;
	}

	public int rollbackAdjustmentTTUM(String adjtype, String localDate) {
		if (adjtype.equalsIgnoreCase("CREDIT ADJUSTMENT")) {
			String str = "select count(*) from nfs_adjustment_ttum where filedate = str_to_date('" + localDate
					+ "', '%Y/%m/%d') and adjtype like 'CREDIT%'";
			int i = ((Integer) getJdbcTemplate().queryForObject(str, new Object[0], Integer.class)).intValue();
			if (i > 0) {
				String deleteQuery = "delete from nfs_adjustment_ttum where filedate = str_to_date('" + localDate
						+ "', '%Y/%m/%d') and adjtype like 'CREDIT%'";
				getJdbcTemplate().execute(deleteQuery);
				return 0;
			}
			return 1;
		}
		if (adjtype.equalsIgnoreCase("CHARGEBACK ACCEPTANCE")) {
			String str = "select count(*) from nfs_adjustment_ttum where filedate = str_to_date('" + localDate
					+ "', '%Y/%m/%d') and adjtype like 'CHARGEBACK%'";
			int i = ((Integer) getJdbcTemplate().queryForObject(str, new Object[0], Integer.class)).intValue();
			if (i > 0) {
				String deleteQuery = "delete from nfs_adjustment_ttum where filedate = str_to_date('" + localDate
						+ "', '%Y/%m/%d') and adjtype like 'CHARGEBACK%'";
				getJdbcTemplate().execute(deleteQuery);
				return 0;
			}
			return 1;
		}
		if (adjtype.equalsIgnoreCase("PRE ARBITRATION ACCEPT")) {
			String str = "select count(*) from nfs_adjustment_ttum where filedate = str_to_date('" + localDate
					+ "', '%Y/%m/%d') and adjtype like 'PRE ARB%'";
			int i = ((Integer) getJdbcTemplate().queryForObject(str, new Object[0], Integer.class)).intValue();
			if (i > 0) {
				String deleteQuery = "delete from nfs_adjustment_ttum where filedate = str_to_date('" + localDate
						+ "', '%Y/%m/%d') and adjtype like 'PRE ARB%'";
				getJdbcTemplate().execute(deleteQuery);
				return 0;
			}
			return 1;
		}
		if (adjtype.equalsIgnoreCase("NRP IN FAVOR OF ISSUER")) {
			String str = "select count(*) from nfs_adjustment_ttum where filedate = str_to_date('" + localDate
					+ "', '%Y/%m/%d') and adjtype like '%NRP%'";
			int i = ((Integer) getJdbcTemplate().queryForObject(str, new Object[0], Integer.class)).intValue();
			if (i > 0) {
				String deleteQuery = "delete from nfs_adjustment_ttum where filedate = str_to_date('" + localDate
						+ "', '%Y/%m/%d') and adjtype like '%NRP%'";
				getJdbcTemplate().execute(deleteQuery);
				return 0;
			}
			return 1;
		}
		String query = "select count(*) from nfs_adjustment_ttum where filedate = str_to_date('" + localDate
				+ "', '%Y/%m/%d') and adjtype like 'ARBITRATION%'";
		int count = ((Integer) getJdbcTemplate().queryForObject(query, new Object[0], Integer.class)).intValue();
		if (count > 0) {
			String deleteQuery = "delete from nfs_adjustment_ttum where filedate = str_to_date('" + localDate
					+ "', '%Y/%m/%d') and adjtype like 'ARBITRATION%'";
			getJdbcTemplate().execute(deleteQuery);
			return 0;
		}
		return 1;
	}

	public HashMap<String, Object> checkTTUMProcessed(UnMatchedTTUMBean beanObj) {
		return null;
	}

	@Override
	public List<ViewFiles> searchSwitchViewFile1(String type, String fromDate) throws Exception, SQLException {

		final List<ViewFiles> bean = new ArrayList<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			String getData ="";
			if(type.equalsIgnoreCase("TX")) {
				getData = "    SELECT filename, count FROM switch_data_validation WHERE FILEDATE=STR_TO_DATE('"+fromDate + "','%Y/%m/%d') and filename like '%" + type + "%' and Filetype in ('ATM')";

			}else {
				getData = "    SELECT filename, count FROM switch_data_validation WHERE FILEDATE=STR_TO_DATE('"+fromDate + "','%Y/%m/%d') and filename like '%" + type + "%' and Filetype in ('POS')";
	
			}
	
			System.out.println("getData " + getData);
			List<ViewFiles> DailyData = getJdbcTemplate().query(getData, new Object[] {},
					new ResultSetExtractor<List<ViewFiles>>() {
						public List<ViewFiles> extractData(ResultSet rs) throws SQLException {
							List<ViewFiles> beanList = new ArrayList<>();
							ViewFiles e = new ViewFiles();

							while (rs.next()) {

								System.out.println("filename " + rs.getString("FILENAME"));
								System.out.println("count " + rs.getString("count"));
								e.setFilename(rs.getString("FILENAME"));
								e.setFilecount(rs.getString("count"));

								beanList.add(e);
							}

							System.out.println("beanList " + beanList.toString());
							return beanList;

						}
					});

			return DailyData;

		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return bean;
		}

	}


	@Override
	public List<ViewFiles> searchCBSViewFile1(String type, String fromDate) throws Exception, SQLException {

		final List<ViewFiles> bean = new ArrayList<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {

			String getData = "    SELECT filename, count FROM switch_data_validation WHERE FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d') and filename like '%" + type + "%' and Filetype in ('CBS')";

			System.out.println("getData " + getData);
			List<ViewFiles> DailyData = getJdbcTemplate().query(getData, new Object[] {},
					new ResultSetExtractor<List<ViewFiles>>() {
						public List<ViewFiles> extractData(ResultSet rs) throws SQLException {
							List<ViewFiles> beanList = new ArrayList<>();
							ViewFiles e = new ViewFiles();

							while (rs.next()) {

								System.out.println("filename " + rs.getString("FILENAME"));
								System.out.println("count " + rs.getString("count"));
								e.setFilename(rs.getString("FILENAME"));
								e.setFilecount(rs.getString("count"));

								bean.add(e);
							}

							// System.out.println("beanList "+beanList.toString());
							return bean;

						}
					});

			return bean;

		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return bean;
		}

	}

	public List<ViewFiles> searchEODExcelFile(String type, String fromDate) throws Exception, SQLException {

		final List<ViewFiles> bean = new ArrayList<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			String getData1="";
			if(type.equalsIgnoreCase("VISA_REFUND_GL")) {
				
				
				getData1 = "    SELECT REMARKS, BALANCE, DEBIT_Count, Debit_Amount,CREDIT_Count, CREDIT_AMOUNT  FROM gl_visa_refund";

			}
			if (type.equalsIgnoreCase("VISA_REFUND_GL")) {
				getData1 = "    SELECT REMARKS, BALANCE, DEBIT_Count, Debit_Amount,CREDIT_Count, CREDIT_AMOUNT  FROM gl_visa_refund";
				
			} else if (type.equalsIgnoreCase("VISA_ACQ_CHARGEBACK_DOM_GL")) {
				getData1 = "    SELECT REMARKS, BALANCE, DEBIT_Count, Debit_Amount,CREDIT_Count, CREDIT_AMOUNT  FROM gl_visa_acq_dom_chbk";
				
			}  else if (type.equalsIgnoreCase("VISA_REME_CHARGEBACK_DOM_GL")) {
				getData1 = "    SELECT REMARKS, BALANCE, DEBIT_Count, Debit_Amount,CREDIT_Count, CREDIT_AMOUNT  FROM gl_visa_reme_dom_chbk";
				
			}  else if (type.equalsIgnoreCase("VISA_ISS_POOL_GL")) {
				getData1 = "    SELECT REMARKS, BALANCE, DEBIT_Count, Debit_Amount,CREDIT_Count, CREDIT_AMOUNT  FROM gl_visa_iss_pool";
				
			}  else if (type.equalsIgnoreCase("VISA_INT_BENE_CHARGEBACK_GL")) {
				getData1 = "    SELECT REMARKS, BALANCE, DEBIT_Count, Debit_Amount,CREDIT_Count, CREDIT_AMOUNT  FROM gl_visa_bane_int_chbk";
				
				
			}  else if (type.equalsIgnoreCase("VISA_ACQ_INT_POOL_GL")) {
				getData1 = "    SELECT REMARKS, BALANCE, DEBIT_Count, Debit_Amount,CREDIT_Count, CREDIT_AMOUNT  FROM gl_visa_acq_int_pool";
				
			} else if (type.equalsIgnoreCase("VISA_ACQ_DOM_POOL_GL")) {
				getData1 = "    SELECT REMARKS, BALANCE, DEBIT_Count, Debit_Amount,CREDIT_Count, CREDIT_AMOUNT  FROM gl_visa_acq_dom_pool";
				
			} else if (type.equalsIgnoreCase("NFS_ISS_GL")) {
				getData1 = "    SELECT REMARKS, BALANCE, DEBIT_Count, Debit_Amount,CREDIT_Count, CREDIT_AMOUNT  FROM ";
				
			}else {
				getData1 = "    SELECT REMARKS, BALANCE, DEBIT_Count, Debit_Amount,CREDIT_Count, CREDIT_AMOUNT  FROM gl_visa_refund";
				
			}
		
			System.out.println("getData " + getData1);
			List<ViewFiles> DailyData = getJdbcTemplate().query(getData1, new Object[] {},
					new ResultSetExtractor<List<ViewFiles>>() {
						public List<ViewFiles> extractData(ResultSet rs) throws SQLException {
							List<ViewFiles> beanList = new ArrayList<>();
							ViewFiles e = new ViewFiles();

							while (rs.next()) {

								System.out.println("remaark " + rs.getString("REMARKS"));
						
								e.setRemark(rs.getString("REMARKS"));
								
								e.setCredit_amount(rs.getString("CREDIT_AMOUNT"));
								e.setCredit_count(rs.getString("CREDIT_Count"));
								e.setDebit_Amount(rs.getString("Debit_Amount"));
								e.setDebit_count(rs.getString("DEBIT_Count"));
								e.setBalance(rs.getString("BALANCE"));
								

								bean.add(e);
							}

							// System.out.println("beanList "+beanList.toString());
							return bean;

						}
					});

			return bean;

		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return bean;
		}

	}

	@Override
	public List<ViewFiles> searchRowDataViewFile1(String type, String fromDate) throws Exception, SQLException {
		List<Object> data = new ArrayList<Object>();
		final List<ViewFiles> bean = new ArrayList<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {

			String getData = "";

			if (type.equalsIgnoreCase("VISA_SWITCH")) {
				getData = "    SELECT filename, count FROM switch_data_validation WHERE FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d')  and  Filetype in ('VISA')";

			} else if (type.equalsIgnoreCase("MC_POS")) {
				getData = "SELECT FILE_NAME as FILENAME,COUNT(*) as count FROM mastercard_pos_rawdata FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d') GROUP BY FILE_NAME";

			} else if (type.equalsIgnoreCase("MC_ATM")) {
				getData = "SELECT FILENAME as FILENAME,COUNT(*) as count FROM  mastercard_atm_rawdata WHERE FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d') GROUP BY FILENAME";

			} else if (type.equalsIgnoreCase("MC_TA")) {
				getData = "SELECT FILENAME as FILENAME,COUNT(*) as count FROM  mastercard_ta_rawdata WHERE FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d') GROUP BY FILENAME";

			}else if (type.equalsIgnoreCase("VISA_ACQ")) {
				getData = "SELECT FILENAME as FILENAME,COUNT(*) as count FROM visa_acq_rawdata WHERE FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d') GROUP BY FILENAME";

			} else if (type.equalsIgnoreCase("VISA_ISS")) {
				getData = "SELECT FILENAME as FILENAME,COUNT(*) as count FROM visa_visa_rawdata  WHERE FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d') GROUP BY FILENAME";

			}  else if (type.equalsIgnoreCase("ICD_ACQ")) {
				getData = "SELECT FILENAME as FILENAME,COUNT(*) as count FROM  icd_icd_acq_rawdata WHERE FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d') GROUP BY FILENAME";

			} else if (type.equalsIgnoreCase("ICD_ISS")) {
				getData = "SELECT FILENAME as FILENAME,COUNT(*) as count FROM icd_icd_iss_rawdata WHERE FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d') GROUP BY FILENAME";

			} else if (type.equalsIgnoreCase("DFS_ISS")) {
				getData = "SELECT FILENAME as FILENAME,COUNT(*) as count FROM dfs_dfs_iss_rawdata WHERE FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d') GROUP BY FILENAME";

			} else if (type.equalsIgnoreCase("DFS_ACQ")) {
				getData = "SELECT FILENAME as FILENAME,COUNT(*) as count FROM  dfs_dfs_acq_rawdata WHERE FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d') GROUP BY FILENAME";

			} else if (type.equalsIgnoreCase("JCB_ACQ")) {
				getData = "SELECT FILENAME as FILENAME,COUNT(*) as count FROM jcb_jcb_acq_rawdata WHERE FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d') GROUP BY FILENAME";

			} else if (type.equalsIgnoreCase("JCB_ISS")) {
				getData = "SELECT FILENAME as FILENAME,COUNT(*) as count FROM  jcb_jcb_iss_rawdata WHERE FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d') GROUP BY FILENAME";

			}else {
				getData = "SELECT FILENAME as FILENAME,COUNT(*) as count FROM visa_visa_rawdata WHERE FILEDATE=STR_TO_DATE('"+fromDate+"','%Y/%m/%d') GROUP BY FILENAME";

			}
			System.out.println("getData " + getData);
			List<ViewFiles> DailyData = getJdbcTemplate().query(getData, new Object[] {},
					new ResultSetExtractor<List<ViewFiles>>() {
						public List<ViewFiles> extractData(ResultSet rs) throws SQLException {
							List<ViewFiles> beanList = new ArrayList<>();
							ViewFiles e = new ViewFiles();

							while (rs.next()) {

								System.out.println("filename " + rs.getString("FILENAME"));
								System.out.println("count " + rs.getString("count"));
								e.setFilename(rs.getString("FILENAME"));
								e.setFilecount(rs.getString("count"));

								bean.add(e);
							}

							// System.out.println("beanList "+beanList.toString());
							return bean;

						}
					});

			return bean;

			/*
			 * for(Object obj : data) { if(obj instanceof ViewFiles) { bean.add((ViewFiles)
			 * obj); } }
			 * 
			 * for(ViewFiles vf : bean) { System.out.println("data d "+ vf); } return bean;
			 */


		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return null;
		}

	}


	public HashMap<String, Object> checkReconDateAndTTUMDataPresent(UnMatchedTTUMBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		String query = "Select count(1) from ";
		try {
			query = String.valueOf(query) + "settlement_nfs_" + beanObj.getStSubCategory().substring(0, 3).toLowerCase()
					+ "_cbs where filedate >= str_to_date('" + beanObj.getLocalDate() + "','%Y/%m/%d') ";
			logger.info("query is " + query);
			int recordCount = ((Integer) getJdbcTemplate().queryForObject(query, new Object[0], Integer.class))
					.intValue();
			if (recordCount > 0) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("FAILED")) {
					query = "select count(1) from settlement_nfs_iss_cbs where filedate = (select max(filedate) from settlement_nfs_iss_cbs)  and dcrs_remarks  like '%NFS-ISS-UNRECON-2%' and respcode = '00' and str_to_date(substring(tran_date,1,8),'%d%m%Y') = str_to_date('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON")) {
					query = "select count(1) from settlement_nfs_iss_switch where filedate = (select max(filedate) from settlement_nfs_iss_switch)  and dcrs_remarks  = 'NFS-ISS-UNRECON-2' and respcode = '00'  and str_to_date(local_date,'%y%m%d') = str_to_date('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATEREV")) {
					query = "select count(1) from nfs_rev_acq_report t3 where acq != 'DLB' and str_to_date(t3.trasn_date ,'%d-%m-%Y') = str_to_date('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d')" + "and not exists( " + " select 1 "
							+ " from cbs_rupay_rawdata t1 , nfs_rev_acq_report t2 where "
							+ " str_to_date(t3.trasn_date ,'%d-%m-%Y') = str_to_date('" + beanObj.getLocalDate()
							+ "','%Y/%m/%d') " + " and cast(t1.amount as unsigned) = cast(t2.requestamt as unsigned) "
							+ "  and t1.remarks = t2.cardno " + " and t1.ref_no = t2.rrn "
							+ " and e = '200' AND t2.acq !='DLB' " + " AND t1.filedate BETWEEN str_to_date('"
							+ beanObj.getLocalDate() + "','%Y/%m/%d') " + " AND str_to_date('" + beanObj.getLocalDate()
							+ "','%Y/%m/%d')+interval 5 day "
							+ " and t3.rrn = t2.rrn and t3.requestamt = t2.requestamt and t3.filedate = t2.filedate "
							+ " and t3.cardno = t2.cardno )";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("NIH")) {
					query = "select count(1) from settlement_nfs_acq_nfs t1 where filedate =  (select max(filedate) from settlement_nfs_acq_nfs)  and str_to_date(transaction_date,'%y%m%d') = str_to_date('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d') " + " and t1.dcrs_remarks = 'NFS-ACQ-UNRECON-2'";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("RUPAYONUS")) {
					query = "select count(1) from settlement_nfs_iss_cbs where dcrs_remarks = 'NFS-RUPAY-ONUS' and str_to_date(substring(tran_date,1,8),'%d%m%Y') = str_to_date('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ACQFAILED")) {
					query = "select count(1) from settlement_nfs_acq_cbs where filedate = (select max(filedate) from settlement_nfs_acq_cbs)  and dcrs_remarks  like '%NFS-ACQ-UNRECON-2 (%' and respcode = '00' and str_to_date(substring(tran_date,1,8),'%d%m%Y') = str_to_date('"
							+

							beanObj.getLocalDate() + "','%Y/%m/%d')";
				}
				logger.info("query is " + query);
				recordCount = ((Integer) getJdbcTemplate().queryForObject(query, new Object[0], Integer.class))
						.intValue();
				if (recordCount > 0) {
					output.put("result", Boolean.valueOf(true));
				} else {
					output.put("result", Boolean.valueOf(false));
					output.put("msg", "No records present for processing");
				}
			} else {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "Tran date is greater than recon date");
			}
		} catch (Exception e) {
			logger.info("Exception while checking records " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while checking records ");
		}
		return output;
	}

	public List<Object> getNFSTTUMData(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public void generateExcelTTUM(String stPath, String FileName, List<Object> ExcelData, String zipName,
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
			for (int i = 0; i < Excel_Headers.size(); i++)
				header.createCell(i).setCellValue(Excel_Headers.get(i));
			for (int j = 0; j < TTUMData.size(); j++) {
				HSSFRow rowEntry = sheet.createRow(j + 1);
				Map<String, String> map_data = (Map<String, String>) TTUMData.get(j);
				if (map_data.size() > 0)
					for (int m = 0; m < Excel_Headers.size(); m++)
						rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers.get(m)));
			}
			workbook.write(fileOut);
			fileOut.close();
			File file = new File(stPath);
			String[] filelist = file.list();
			byte b;
			int k;
			String[] arrayOfString1;
			for (k = (arrayOfString1 = filelist).length, b = 0; b < k;) {
				String Names = arrayOfString1[b];
				logger.info("name is " + Names);
				files.add(String.valueOf(stPath) + File.separator + Names);
				b++;
			}
			FileOutputStream fos = new FileOutputStream(String.valueOf(stPath) + File.separator + zipName + ".zip");
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

	public boolean checkAndMakeDirectory(UnMatchedTTUMBean beanObj) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yy/mm/dd");
			Date date = sdf.parse(beanObj.getLocalDate());
			sdf = new SimpleDateFormat("dd-MM-yyyy");
			String stnewDate = sdf.format(date);
			logger.info("Path is " + beanObj.getStPath() + File.separator + beanObj.getCategory());
			File checkFile = new File(String.valueOf(beanObj.getStPath()) + File.separator + beanObj.getCategory());
			if (checkFile.exists())
				FileUtils.forceDelete(
						new File(String.valueOf(beanObj.getStPath()) + File.separator + beanObj.getCategory()));
			File directory = new File(String.valueOf(beanObj.getStPath()) + File.separator + beanObj.getCategory());
			if (!directory.exists())
				directory.mkdir();
			directory = new File(String.valueOf(beanObj.getStPath()) + File.separator + beanObj.getCategory()
					+ File.separator + stnewDate);
			if (!directory.exists())
				directory.mkdir();
			beanObj.setStPath(String.valueOf(beanObj.getStPath()) + File.separator + beanObj.getCategory()
					+ File.separator + stnewDate);
			return true;
		} catch (Exception e) {
			logger.info("Exception in checkAndMakeDirectory " + e);
			return false;
		}
	}

	public Boolean NFSTtumRollback(UnMatchedTTUMBean beanObj) {
		String deleteQuery = null;
		String updateQuery = null;
		try {
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("FAILED")) {
				deleteQuery = "delete from ttum_nfs_iss_cbs  WHERE tran_date = str_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d')";
				getJdbcTemplate().execute(deleteQuery);
				updateQuery = "update settlement_nfs_iss_cbs set dcrs_remarks = 'NFS-ISS-UNRECON-2 (' where dcrs_remarks = 'NFS-ISS-GENERATED-TTUM-2' and str_to_date(substring(tran_date,1,8),'%d%m%Y') = str_to_date('"
						+

						beanObj.getLocalDate()
						+ "','%Y/%m/%d') -- and filedate = (select max(filedate) from settlement_nfs_iss_cbs)";
				getJdbcTemplate().execute(updateQuery);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON")) {
				deleteQuery = "delete from ttum_nfs_iss_switch  WHERE tran_date = str_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d')";
				getJdbcTemplate().execute(deleteQuery);
				updateQuery = "update settlement_nfs_iss_switch set dcrs_remarks = 'NFS-ISS-UNRECON-2' where dcrs_remarks = 'NFS-ISS-GENERATED-TTUM-2' and str_to_date(local_date,'%y%m%d') = str_to_date('"
						+

						beanObj.getLocalDate()
						+ "','%Y/%m/%d') -- and filedate = (select max(filedate) from settlement_nfs_iss_switch)";
				getJdbcTemplate().execute(updateQuery);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATEREV")) {
				updateQuery = "update nfs_rev_acq_report t3 set dcrs_remarks = 'UNMATCHED' WHERE  acq != 'DLB'  AND str_to_date(trasn_date ,'%d-%m-%Y') = str_to_date('"
						+ beanObj.getLocalDate() + "','%Y/%m/%d')"
						+ " and rrn in (select acquirer_reference_data from ttum_nfs_iss_nfs"
						+ " where tran_date = str_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d') and part_tran_type = 'C')";
				getJdbcTemplate().execute(updateQuery);
				deleteQuery = "delete from ttum_nfs_iss_nfs  WHERE tran_date = str_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d')";
				getJdbcTemplate().execute(deleteQuery);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("NIH")) {
				deleteQuery = "delete from ttum_nfs_acq_nfs  WHERE tran_date = str_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d')";
				getJdbcTemplate().execute(deleteQuery);
				updateQuery = "update settlement_nfs_acq_nfs set dcrs_remarks = 'NFS-ACQ-UNRECON-2' where dcrs_remarks = 'NFS-ACQ-GENERATED-TTUM-2' and  str_to_date(transaction_date,'%y%m%d')  = str_to_date('"
						+

						beanObj.getLocalDate()
						+ "','%Y/%m/%d') -- and filedate = (select max(filedate) from settlement_nfs_acq_nfs)";
				getJdbcTemplate().execute(updateQuery);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("RUPAYONUS")) {
				deleteQuery = "delete from ttum_nfs_onus_cbs  WHERE tran_date = str_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d')";
				getJdbcTemplate().execute(deleteQuery);
				updateQuery = "update settlement_nfs_iss_cbs set dcrs_remarks = 'NFS-RUPAY-ONUS' where dcrs_remarks = 'NFS-RUPAY-POS-GENERATED-TTUM' and  str_to_date(substring(tran_date,1,8),'%d%m%Y')  = str_to_date('"
						+

						beanObj.getLocalDate()
						+ "','%Y/%m/%d') -- and filedate = (select max(filedate) from settlement_nfs_acq_nfs)";
				getJdbcTemplate().execute(updateQuery);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ACQFAILED")) {
				deleteQuery = "delete from ttum_nfs_acq_cbs  WHERE tran_date = str_to_date('" + beanObj.getLocalDate()
						+ "','%Y/%m/%d')";
				getJdbcTemplate().execute(deleteQuery);
				updateQuery = "update settlement_nfs_acq_cbs set dcrs_remarks = 'NFS-ACQ-UNRECON-2 (' where dcrs_remarks = 'NFS-ACQ-GENERATED-TTUM-2' and str_to_date(substring(tran_date,1,8),'%d%m%Y') = str_to_date('"
						+

						beanObj.getLocalDate()
						+ "','%Y/%m/%d') -- and filedate = (select max(filedate) from settlement_nfs_acq_cbs)";
				getJdbcTemplate().execute(updateQuery);
			}
		} catch (Exception e) {
			logger.info("Exception in NFSSettVoucherRollback " + e);
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}

	public List<Object> getAdjustmentTTUMReport(String adjtype, String localDate) {
		return null;
	}

	public List<Object> downloadRupayDhanaReport(SettlementBean settlementBean) {
		List<Object> data = new ArrayList<Object>();
		try {
			String getInterchange1 = "select * from    settlement_rupay_dom_cbs WHERE  FILEDATE =STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d')  and dcrs_remarks='RUPAY_DOM_KNOCKOFF'";
			String getInterchange2 = "select * from settlement_rupay_dom_cbs WHERE  FILEDATE =STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks='RUPAY_DOM_MATCHED-1' ";
			String getInterchange3 = "select * from settlement_rupay_dom_cbs WHERE  FILEDATE = STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks='RUPAY_DOM_UNRECON-1'";
			String getInterchange4 = "select * from settlement_rupay_dom_cbs WHERE  FILEDATE =STR_TO_DATE('"
					+ settlementBean.getDatepicker()
					+ "','%Y/%m/%d') and dcrs_remarks  IN('RUPAY_DOM_MATCHED-2','CBS_RAW_MATCH' )";
			String getInterchange5 = "select * from settlement_rupay_dom_cbs WHERE  FILEDATE = STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks='RUPAY_DOM_UNRECON-2' ";
			String getInterchange7 = "select  * from  settlement_rupay_dom_rupay WHERE  FILEDATE =STR_TO_DATE('" +

					settlementBean.getDatepicker()
					+ "','%Y/%m/%d') and dcrs_remarks IN('RUPAY_DOM_MATCHED-2','CBS_RAW_MATCH' )";
			String getInterchange8 = "select  * from  settlement_rupay_dom_rupay WHERE  FILEDATE = STR_TO_DATE('" +

					settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks='RUPAY_DOM_UNRECON-2'";
			List<String> Column_list = new ArrayList<String>();
			System.out.println("getInterchange1 " + getInterchange1);
			Column_list = new ArrayList<String>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("TMP_ID");
			data.add(Column_list);
			final List<Object> beanList = new ArrayList<Object>();
			final List<String> columns2 = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns2) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 2");

			Column_list = new ArrayList<String>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("TMP_ID");
			data.add(Column_list);
			final List<String> columns3 = Column_list;
			final List<Object> beanList3 = new ArrayList<Object>();
			List<Object> DailyData3 = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns3) {
									data.put(column, rs.getString(column));
								}
								beanList3.add(data);
							}
							return beanList3;
						}
					});
			data.add(DailyData3);

			System.out.println("query 3");
			Column_list = new ArrayList<String>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("TMP_ID");
			data.add(Column_list);
			final List<String> columns4 = Column_list;
			final List<Object> beanList4 = new ArrayList<Object>();
			List<Object> DailyData4 = getJdbcTemplate().query(getInterchange3, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns4) {
									data.put(column, rs.getString(column));
								}
								beanList4.add(data);
							}
							return beanList4;
						}
					});
			data.add(DailyData4);
			System.out.println("query 4");

			Column_list = new ArrayList<String>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("TMP_ID");
			data.add(Column_list);
			final List<String> columns5 = Column_list;
			final List<Object> beanList5 = new ArrayList<Object>();
			List<Object> DailyData5 = getJdbcTemplate().query(getInterchange4, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns5) {
									data.put(column, rs.getString(column));
								}
								beanList5.add(data);
							}
							return beanList5;
						}
					});
			data.add(DailyData5);
			System.out.println("query 5");

			Column_list = new ArrayList<String>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("TMP_ID");
			data.add(Column_list);
			final List<String> columns7 = Column_list;
			final List<Object> beanList7 = new ArrayList<Object>();
			List<Object> DailyData7 = getJdbcTemplate().query(getInterchange5, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns7) {
									data.put(column, rs.getString(column));
								}
								beanList7.add(data);
							}
							return beanList7;
						}
					});
			data.add(DailyData7);
			System.out.println("query 7");

			Column_list = new ArrayList<String>();
			Column_list.add("MTI");
			Column_list.add("FUNCTION_CODE");
			Column_list.add("RECORD_NUMBER");
			Column_list.add("MEMBER_INSTITUTION_ID_CODE");
			Column_list.add("UNIQUE_FILE_NAME");
			Column_list.add("DATE_SETTLEMENT");
			Column_list.add("PRODUCT_CODE");
			Column_list.add("SETTLEMENT_BIN");
			Column_list.add("FILE_CATEGORY");
			Column_list.add("VERSION_NUMBER");
			Column_list.add("ENTIRE_FILE_REJECT_INDICATOR");
			Column_list.add("FILE_REJECT_REASON_CODE");
			Column_list.add("TRANSACTIONS_COUNT");
			Column_list.add("RUN_TOTAL_AMOUNT");
			Column_list.add("ACQUIRER_INSTITUTION_ID_CODE");
			Column_list.add("AMOUNT_SETTLEMENT");
			Column_list.add("AMOUNT_TRANSACTION");
			Column_list.add("APPROVAL_CODE");
			Column_list.add("ACQUIRER_REFERENCE_DATA");
			Column_list.add("CASE_NUMBER");
			Column_list.add("CURRENCY_CODE_SETTLEMENT");
			Column_list.add("CURRENCY_CODE_TRANSACTION");
			Column_list.add("CONVERSION_RATE_SETTLEMENT");
			Column_list.add("CARD_ACCEPTOR_ADDI_ADDR");
			Column_list.add("CARD_ACCEPTOR_TERMINAL_ID");
			Column_list.add("CARD_ACCEPTOR_ZIP_CODE");
			Column_list.add("DATEANDTIME_LOCAL_TRANSACTION");
			Column_list.add("TXNFUNCTION_CODE");
			Column_list.add("LATE_PRESENTMENT_INDICATOR");
			Column_list.add("TXNMTI");
			Column_list.add("PRIMARY_ACCOUNT_NUMBER");
			Column_list.add("TXNRECORD_NUMBER");
			Column_list.add("RGCS_RECEIVED_DATE");
			Column_list.add("SETTLEMENT_DR_CR_INDICATOR");
			Column_list.add("TXN_DESTI_INSTI_ID_CODE");
			Column_list.add("TXN_ORIGIN_INSTI_ID_CODE");
			Column_list.add("CARD_HOLDER_UID");
			Column_list.add("AMOUNT_BILLING");
			Column_list.add("CURRENCY_CODE_BILLING");
			Column_list.add("CONVERSION_RATE_BILLING");
			Column_list.add("MESSAGE_REASON_CODE");
			Column_list.add("FEE_DR_CR_INDICATOR1");
			Column_list.add("FEE_AMOUNT1");
			Column_list.add("FEE_CURRENCY1");
			Column_list.add("FEE_TYPE_CODE1");
			Column_list.add("INTERCHANGE_CATEGORY1");
			Column_list.add("FEE_DR_CR_INDICATOR2");
			Column_list.add("FEE_AMOUNT2");
			Column_list.add("FEE_CURRENCY2");
			Column_list.add("FEE_TYPE_CODE2");
			Column_list.add("INTERCHANGE_CATEGORY2");
			Column_list.add("FEE_DR_CR_INDICATOR3");
			Column_list.add("FEE_AMOUNT3");
			Column_list.add("FEE_CURRENCY3");
			Column_list.add("FEE_TYPE_CODE3");
			Column_list.add("INTERCHANGE_CATEGORY3");
			Column_list.add("FEE_DR_CR_INDICATOR4");
			Column_list.add("FEE_AMOUNT4");
			Column_list.add("FEE_CURRENCY4");
			Column_list.add("FEE_TYPE_CODE4");
			Column_list.add("INTERCHANGE_CATEGORY4");
			Column_list.add("FEE_DR_CR_INDICATOR5");
			Column_list.add("FEE_AMOUNT5");
			Column_list.add("FEE_CURRENCY5");
			Column_list.add("FEE_TYPE_CODE5");
			Column_list.add("INTERCHANGE_CATEGORY5");
			Column_list.add("FLAG");
			Column_list.add("TRL_FUNCTION_CODE");
			Column_list.add("TRL_RECORD_NUMBER");
			Column_list.add("PART_ID");
			Column_list.add("DCRS_TRAN_NO");
			Column_list.add("NEXT_TRAN_DATE");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("PAN");
			Column_list.add("RRN");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns8 = Column_list;
			final List<Object> beanList8 = new ArrayList<Object>();
			List<Object> DailyData8 = getJdbcTemplate().query(getInterchange7, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns8) {
									data.put(column, rs.getString(column));
								}
								beanList8.add(data);
							}
							return beanList8;
						}
					});
			data.add(DailyData8);
			System.out.println("query 8");

			Column_list = new ArrayList<String>();
			Column_list.add("MTI");
			Column_list.add("FUNCTION_CODE");
			Column_list.add("RECORD_NUMBER");
			Column_list.add("MEMBER_INSTITUTION_ID_CODE");
			Column_list.add("UNIQUE_FILE_NAME");
			Column_list.add("DATE_SETTLEMENT");
			Column_list.add("PRODUCT_CODE");
			Column_list.add("SETTLEMENT_BIN");
			Column_list.add("FILE_CATEGORY");
			Column_list.add("VERSION_NUMBER");
			Column_list.add("ENTIRE_FILE_REJECT_INDICATOR");
			Column_list.add("FILE_REJECT_REASON_CODE");
			Column_list.add("TRANSACTIONS_COUNT");
			Column_list.add("RUN_TOTAL_AMOUNT");
			Column_list.add("ACQUIRER_INSTITUTION_ID_CODE");
			Column_list.add("AMOUNT_SETTLEMENT");
			Column_list.add("AMOUNT_TRANSACTION");
			Column_list.add("APPROVAL_CODE");
			Column_list.add("ACQUIRER_REFERENCE_DATA");
			Column_list.add("CASE_NUMBER");
			Column_list.add("CURRENCY_CODE_SETTLEMENT");
			Column_list.add("CURRENCY_CODE_TRANSACTION");
			Column_list.add("CONVERSION_RATE_SETTLEMENT");
			Column_list.add("CARD_ACCEPTOR_ADDI_ADDR");
			Column_list.add("CARD_ACCEPTOR_TERMINAL_ID");
			Column_list.add("CARD_ACCEPTOR_ZIP_CODE");
			Column_list.add("DATEANDTIME_LOCAL_TRANSACTION");
			Column_list.add("TXNFUNCTION_CODE");
			Column_list.add("LATE_PRESENTMENT_INDICATOR");
			Column_list.add("TXNMTI");
			Column_list.add("PRIMARY_ACCOUNT_NUMBER");
			Column_list.add("TXNRECORD_NUMBER");
			Column_list.add("RGCS_RECEIVED_DATE");
			Column_list.add("SETTLEMENT_DR_CR_INDICATOR");
			Column_list.add("TXN_DESTI_INSTI_ID_CODE");
			Column_list.add("TXN_ORIGIN_INSTI_ID_CODE");
			Column_list.add("CARD_HOLDER_UID");
			Column_list.add("AMOUNT_BILLING");
			Column_list.add("CURRENCY_CODE_BILLING");
			Column_list.add("CONVERSION_RATE_BILLING");
			Column_list.add("MESSAGE_REASON_CODE");
			Column_list.add("FEE_DR_CR_INDICATOR1");
			Column_list.add("FEE_AMOUNT1");
			Column_list.add("FEE_CURRENCY1");
			Column_list.add("FEE_TYPE_CODE1");
			Column_list.add("INTERCHANGE_CATEGORY1");
			Column_list.add("FEE_DR_CR_INDICATOR2");
			Column_list.add("FEE_AMOUNT2");
			Column_list.add("FEE_CURRENCY2");
			Column_list.add("FEE_TYPE_CODE2");
			Column_list.add("INTERCHANGE_CATEGORY2");
			Column_list.add("FEE_DR_CR_INDICATOR3");
			Column_list.add("FEE_AMOUNT3");
			Column_list.add("FEE_CURRENCY3");
			Column_list.add("FEE_TYPE_CODE3");
			Column_list.add("INTERCHANGE_CATEGORY3");
			Column_list.add("FEE_DR_CR_INDICATOR4");
			Column_list.add("FEE_AMOUNT4");
			Column_list.add("FEE_CURRENCY4");
			Column_list.add("FEE_TYPE_CODE4");
			Column_list.add("INTERCHANGE_CATEGORY4");
			Column_list.add("FEE_DR_CR_INDICATOR5");
			Column_list.add("FEE_AMOUNT5");
			Column_list.add("FEE_CURRENCY5");
			Column_list.add("FEE_TYPE_CODE5");
			Column_list.add("INTERCHANGE_CATEGORY5");
			Column_list.add("FLAG");
			Column_list.add("TRL_FUNCTION_CODE");
			Column_list.add("TRL_RECORD_NUMBER");
			Column_list.add("PART_ID");
			Column_list.add("DCRS_TRAN_NO");
			Column_list.add("NEXT_TRAN_DATE");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("PAN");
			Column_list.add("RRN");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns9 = Column_list;
			final List<Object> beanList9 = new ArrayList<Object>();
			List<Object> DailyData9 = getJdbcTemplate().query(getInterchange8, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns9) {
									data.put(column, rs.getString(column));
								}
								beanList9.add(data);
							}
							return beanList9;
						}
					});
			data.add(DailyData9);
			System.out.println("query 9");

			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> downloadRupayDhanaReportINT(SettlementBean settlementBean) {
		List<Object> data = new ArrayList();
		try {
			logger.info("download Issuer Dhana Report category  " + settlementBean.getCategory() + "and subCegory "
					+ settlementBean.getStsubCategory());
			if (settlementBean.getCategory().equalsIgnoreCase("RUPAY")) {
				String getInterchange1 = "select * from   settlement_rupay_int_cbs WHERE  FILEDATE =  STR_TO_DATE('"
						+ settlementBean.getDatepicker() + "','%Y/%m/%d')  and dcrs_remarks='RUPAY_INT_KNOCKOFF'";
				String getInterchange2 = "select * from settlement_rupay_int_cbs WHERE  FILEDATE = STR_TO_DATE('"
						+ settlementBean.getDatepicker() + "','%Y/%m/%d')  and dcrs_remarks='RUPAY_INT_MATCHED-1'";
				String getInterchange3 = "select * from settlement_rupay_int_cbs WHERE  FILEDATE =  STR_TO_DATE('"
						+ settlementBean.getDatepicker() + "','%Y/%m/%d')  and dcrs_remarks='RUPAY_INT_UNRECON-1'";
				String getInterchange4 = "select * from settlement_rupay_int_cbs WHERE  FILEDATE =  STR_TO_DATE('"
						+ settlementBean.getDatepicker()
						+ "','%Y/%m/%d')  and dcrs_remarks  IN('RUPAY_INT_MATCHED-2','CBS_RAW_MATCH' )";
				String getInterchange5 = "select * from settlement_rupay_int_cbs WHERE  FILEDATE = STR_TO_DATE('"
						+ settlementBean.getDatepicker() + "','%Y/%m/%d')  and dcrs_remarks='RUPAY_INT_UNRECON-2' ";
				String getInterchange6 = "select * from settlement_rupay_int_switch WHERE  FILEDATE = STR_TO_DATE('"
						+ settlementBean.getDatepicker() + "','%Y/%m/%d')  and dcrs_remarks='RUPAY_INT_UNRECON-1'";
				String getInterchange7 = "select  FUNCTION_CODE,  MEMBER_INSTITUTION_ID_CODE, UNIQUE_FILE_NAME, DATE_SETTLEMENT, PRODUCT_CODE, SETTLEMENT_BIN, FILE_CATEGORY, VERSION_NUMBER,  ACQUIRER_INSTITUTION_ID_CODE, AMOUNT_SETTLEMENT, AMOUNT_TRANSACTION, APPROVAL_CODE, ACQUIRER_REFERENCE_DATA,  CURRENCY_CODE_SETTLEMENT, CURRENCY_CODE_TRANSACTION, CONVERSION_RATE_SETTLEMENT, CARD_ACCEPTOR_ADDI_ADDR, CARD_ACCEPTOR_TERMINAL_ID, CARD_ACCEPTOR_ZIP_CODE, DATEANDTIME_LOCAL_TRANSACTION, TXNFUNCTION_CODE, LATE_PRESENTMENT_INDICATOR, TXNMTI, PRIMARY_ACCOUNT_NUMBER, TXNRECORD_NUMBER, RGCS_RECEIVED_DATE, SETTLEMENT_DR_CR_INDICATOR, TXN_DESTI_INSTI_ID_CODE, TXN_ORIGIN_INSTI_ID_CODE,  FEE_DR_CR_INDICATOR1, FEE_AMOUNT1, FEE_CURRENCY1, FEE_TYPE_CODE1, INTERCHANGE_CATEGORY1, FEE_DR_CR_INDICATOR2, FEE_AMOUNT2, FEE_CURRENCY2, FEE_TYPE_CODE2, FLAG,  CREATEDDATE, CREATEDBY, DCRS_REMARKS, FILEDATE, PAN, RRN, FILENAME from settlement_rupay_int_rupay WHERE  FILEDATE =  STR_TO_DATE('"
						+

						settlementBean.getDatepicker()
						+ "','%Y/%m/%d')  and dcrs_remarks  IN('RUPAY_INT_MATCHED-2','CBS_RAW_MATCH' )";
				String getInterchange8 = "select  FUNCTION_CODE,  MEMBER_INSTITUTION_ID_CODE, UNIQUE_FILE_NAME, DATE_SETTLEMENT, PRODUCT_CODE, SETTLEMENT_BIN, FILE_CATEGORY, VERSION_NUMBER,  ACQUIRER_INSTITUTION_ID_CODE, AMOUNT_SETTLEMENT, AMOUNT_TRANSACTION, APPROVAL_CODE, ACQUIRER_REFERENCE_DATA,  CURRENCY_CODE_SETTLEMENT, CURRENCY_CODE_TRANSACTION, CONVERSION_RATE_SETTLEMENT, CARD_ACCEPTOR_ADDI_ADDR, CARD_ACCEPTOR_TERMINAL_ID, CARD_ACCEPTOR_ZIP_CODE, DATEANDTIME_LOCAL_TRANSACTION, TXNFUNCTION_CODE, LATE_PRESENTMENT_INDICATOR, TXNMTI, PRIMARY_ACCOUNT_NUMBER, TXNRECORD_NUMBER, RGCS_RECEIVED_DATE, SETTLEMENT_DR_CR_INDICATOR, TXN_DESTI_INSTI_ID_CODE, TXN_ORIGIN_INSTI_ID_CODE,  FEE_DR_CR_INDICATOR1, FEE_AMOUNT1, FEE_CURRENCY1, FEE_TYPE_CODE1, INTERCHANGE_CATEGORY1, FEE_DR_CR_INDICATOR2, FEE_AMOUNT2, FEE_CURRENCY2, FEE_TYPE_CODE2, FLAG,  CREATEDDATE, CREATEDBY, DCRS_REMARKS, FILEDATE, PAN, RRN, FILENAME from settlement_rupay_int_rupay WHERE  FILEDATE =  STR_TO_DATE('"
						+

						settlementBean.getDatepicker() + "','%Y/%m/%d')  and dcrs_remarks='RUPAY_INT_UNRECON-2'";
				logger.info("query 7 " + getInterchange7);
				List<String> Column_list = new ArrayList<>();
				Column_list = getColumnList("settlement_rupay_int_cbs");
				data.add(Column_list);
				final List<String> columns = Column_list;
				List<Object> DailyData = getJdbcTemplate().query(getInterchange1, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
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
				logger.info("query1");
				Column_list = new ArrayList<>();
				Column_list = getColumnList("settlement_rupay_int_cbs");
				data.add(Column_list);
				final List<String> columns2 = Column_list;
				DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								while (rs.next()) {
									Map<String, String> data = new HashMap<String, String>();
									for (String column : columns2) {
										data.put(column, rs.getString(column));
									}
									beanList.add(data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
				logger.info("query2");
				Column_list = new ArrayList<>();
				Column_list = getColumnList("settlement_rupay_int_cbs");
				data.add(Column_list);
				final List<String> columns3 = Column_list;
				DailyData = getJdbcTemplate().query(getInterchange3, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								while (rs.next()) {
									Map<String, String> data = new HashMap<String, String>();
									for (String column : columns3) {
										data.put(column, rs.getString(column));
									}
									beanList.add(data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
				logger.info("query3");
				Column_list = new ArrayList<>();
				Column_list = getColumnList("settlement_rupay_int_cbs");
				data.add(Column_list);
				final List<String> columns4 = Column_list;
				DailyData = getJdbcTemplate().query(getInterchange4, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								while (rs.next()) {
									Map<String, String> data = new HashMap<String, String>();
									for (String column : columns4) {
										data.put(column, rs.getString(column));
									}
									beanList.add(data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
				logger.info("query4");
				Column_list = new ArrayList<>();
				Column_list = getColumnList("settlement_rupay_int_cbs");
				data.add(Column_list);
				final List<String> columns5 = Column_list;
				DailyData = getJdbcTemplate().query(getInterchange5, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								while (rs.next()) {
									Map<String, String> data = new HashMap<String, String>();
									for (String column : columns5) {
										data.put(column, rs.getString(column));
									}
									beanList.add(data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
				logger.info("query5");
				Column_list = new ArrayList<>();
				Column_list = getColumnList("settlement_rupay_int_switch");
				data.add(Column_list);
				final List<String> columns6 = Column_list;
				DailyData = getJdbcTemplate().query(getInterchange6, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								while (rs.next()) {
									Map<String, String> data = new HashMap<String, String>();
									for (String column : columns6) {
										data.put(column, rs.getString(column));
									}
									beanList.add(data);
								}
								return beanList;
							}
						});
				data.add(DailyData);

				logger.info("query6");
				Column_list = new ArrayList<>();
				Column_list.add("FUNCTION_CODE");
				Column_list.add("MEMBER_INSTITUTION_ID_CODE");
				Column_list.add("UNIQUE_FILE_NAME");
				Column_list.add("DATE_SETTLEMENT");
				Column_list.add("PRODUCT_CODE");
				Column_list.add("SETTLEMENT_BIN");
				Column_list.add("FILE_CATEGORY");
				Column_list.add("VERSION_NUMBER");
				Column_list.add("ACQUIRER_INSTITUTION_ID_CODE");
				Column_list.add("AMOUNT_SETTLEMENT");
				Column_list.add("AMOUNT_TRANSACTION");
				Column_list.add("APPROVAL_CODE");
				Column_list.add("ACQUIRER_REFERENCE_DATA");
				Column_list.add("CURRENCY_CODE_SETTLEMENT");
				Column_list.add("CURRENCY_CODE_TRANSACTION");
				Column_list.add("CONVERSION_RATE_SETTLEMENT");
				Column_list.add("CARD_ACCEPTOR_ADDI_ADDR");
				Column_list.add("CARD_ACCEPTOR_TERMINAL_ID");
				Column_list.add("CARD_ACCEPTOR_ZIP_CODE");
				Column_list.add("DATEANDTIME_LOCAL_TRANSACTION");
				Column_list.add("TXNFUNCTION_CODE");
				Column_list.add("LATE_PRESENTMENT_INDICATOR");
				Column_list.add("TXNMTI");
				Column_list.add("PRIMARY_ACCOUNT_NUMBER");
				Column_list.add("TXNRECORD_NUMBER");
				Column_list.add("RGCS_RECEIVED_DATE");
				Column_list.add("SETTLEMENT_DR_CR_INDICATOR");
				Column_list.add("TXN_DESTI_INSTI_ID_CODE");
				Column_list.add("TXN_ORIGIN_INSTI_ID_CODE");
				Column_list.add("FEE_DR_CR_INDICATOR1");
				Column_list.add("FEE_AMOUNT1");
				Column_list.add("FEE_CURRENCY1");
				Column_list.add("FEE_TYPE_CODE1");
				Column_list.add("INTERCHANGE_CATEGORY1");
				Column_list.add("FEE_DR_CR_INDICATOR2");
				Column_list.add("FEE_AMOUNT2");
				Column_list.add("FEE_CURRENCY2");
				Column_list.add("FEE_TYPE_CODE2");
				Column_list.add("FLAG");
				Column_list.add("CREATEDDATE");
				Column_list.add("CREATEDBY");
				Column_list.add("DCRS_REMARKS");
				Column_list.add("FILEDATE");
				Column_list.add("PAN");
				Column_list.add("RRN");
				Column_list.add("FILENAME");
				data.add(Column_list);
				final List<String> columns7 = Column_list;
				DailyData = getJdbcTemplate().query(getInterchange7, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								while (rs.next()) {
									Map<String, String> data = new HashMap<String, String>();
									for (String column : columns7) {
										data.put(column, rs.getString(column));
									}
									beanList.add(data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
				logger.info("query7");
				Column_list = new ArrayList<>();
				Column_list.add("FUNCTION_CODE");
				Column_list.add("MEMBER_INSTITUTION_ID_CODE");
				Column_list.add("UNIQUE_FILE_NAME");
				Column_list.add("DATE_SETTLEMENT");
				Column_list.add("PRODUCT_CODE");
				Column_list.add("SETTLEMENT_BIN");
				Column_list.add("FILE_CATEGORY");
				Column_list.add("VERSION_NUMBER");
				Column_list.add("ACQUIRER_INSTITUTION_ID_CODE");
				Column_list.add("AMOUNT_SETTLEMENT");
				Column_list.add("AMOUNT_TRANSACTION");
				Column_list.add("APPROVAL_CODE");
				Column_list.add("ACQUIRER_REFERENCE_DATA");
				Column_list.add("CURRENCY_CODE_SETTLEMENT");
				Column_list.add("CURRENCY_CODE_TRANSACTION");
				Column_list.add("CONVERSION_RATE_SETTLEMENT");
				Column_list.add("CARD_ACCEPTOR_ADDI_ADDR");
				Column_list.add("CARD_ACCEPTOR_TERMINAL_ID");
				Column_list.add("CARD_ACCEPTOR_ZIP_CODE");
				Column_list.add("DATEANDTIME_LOCAL_TRANSACTION");
				Column_list.add("TXNFUNCTION_CODE");
				Column_list.add("LATE_PRESENTMENT_INDICATOR");
				Column_list.add("TXNMTI");
				Column_list.add("PRIMARY_ACCOUNT_NUMBER");
				Column_list.add("TXNRECORD_NUMBER");
				Column_list.add("RGCS_RECEIVED_DATE");
				Column_list.add("SETTLEMENT_DR_CR_INDICATOR");
				Column_list.add("TXN_DESTI_INSTI_ID_CODE");
				Column_list.add("TXN_ORIGIN_INSTI_ID_CODE");
				Column_list.add("FEE_DR_CR_INDICATOR1");
				Column_list.add("FEE_AMOUNT1");
				Column_list.add("FEE_CURRENCY1");
				Column_list.add("FEE_TYPE_CODE1");
				Column_list.add("INTERCHANGE_CATEGORY1");
				Column_list.add("FEE_DR_CR_INDICATOR2");
				Column_list.add("FEE_AMOUNT2");
				Column_list.add("FEE_CURRENCY2");
				Column_list.add("FEE_TYPE_CODE2");
				Column_list.add("FLAG");
				Column_list.add("CREATEDDATE");
				Column_list.add("CREATEDBY");
				Column_list.add("DCRS_REMARKS");
				Column_list.add("FILEDATE");
				Column_list.add("PAN");
				Column_list.add("RRN");
				Column_list.add("FILENAME");
				data.add(Column_list);
				final List<String> columns8 = Column_list;
				DailyData = getJdbcTemplate().query(getInterchange8, new Object[] {},
						new ResultSetExtractor<List<Object>>() {
							public List<Object> extractData(ResultSet rs) throws SQLException {
								List<Object> beanList = new ArrayList<Object>();
								while (rs.next()) {
									Map<String, String> data = new HashMap<String, String>();
									for (String column : columns8) {
										data.put(column, rs.getString(column));
									}
									beanList.add(data);
								}
								return beanList;
							}
						});
				data.add(DailyData);
				logger.info("query8");
			}
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> downloadQSPARCDOMReport(SettlementBean settlementBean) {
		return null;
	}

	public List<Object> downloadICDISSReport(SettlementBean settlementBean) {
		return null;
	}

	public List<Object> downloadICCWISSReport(SettlementBean settlementBean) {
		ArrayList<Object> data = new ArrayList<Object>();
		try {
			String getInterchange5 = "SELECT BUSINESS_DATE, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, FEE, BR_CODE, ISS_SOL_ID, MCC, REMARKS, NETWORK, POSTED_DATE, GL_ACCOUNT, TR_NO, DCRS_REMARKS, FILEDATE, FILENAME, CREATEDDATE\r\nfrom settlement_nfs_iccw_iss_cbs where FILEDATE =STR_TO_DATE( '"
					+ settlementBean.getDatepicker() + "' ,'%Y/%m/%d') and DCRS_REMARKS = 'NFS-ICCW-ISS-MATCHED-2'";
			String getInterchange6 = "SELECT BUSINESS_DATE, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, FEE, BR_CODE, ISS_SOL_ID, MCC, REMARKS, NETWORK, POSTED_DATE, GL_ACCOUNT, TR_NO, DCRS_REMARKS, FILEDATE, FILENAME, CREATEDDATE\r\nfrom settlement_nfs_iccw_iss_cbs where FILEDATE =STR_TO_DATE( '"
					+ settlementBean.getDatepicker() + "' ,'%Y/%m/%d') and DCRS_REMARKS = 'NFS-ICCW-ISS-UNRECON-2'";
			String getInterchange9 = "SELECT CARDHOLDER_BILL_SERV_FEE, PARTICIPANT_ID, TRANSACTION_TYPE, FROM_ACCOUNT_TYPE, TO_ACCOUNT_TYPE, TXN_SERIAL_NO, RESPONSE_CODE, PAN_NUMBER, MEMBER_NUMBER, APPROVAL_NUMBER, SYS_TRACE_AUDIT_NO, TRANSACTION_DATE, TRANSACTION_TIME, MERCHANT_CATEGORY_CD, CARD_ACC_SETTLE_DT, CARD_ACC_ID, CARD_ACC_TERMINAL_ID, CARD_ACC_TERMINAL_LOC, ACQUIRER_ID, NETWORK_ID, ACCOUNT_1_NUMBER, ACCOUNT_1_BRANCH_ID, ACCOUNT_2_NUMBER, ACCOUNT_2_BRANCH_ID, TXN_CURRENCY_CODE, TXN_AMOUNT, ACTUAL_TXN_AMT, TXN_ACTIVITY_FEE, ISS_SETTLE_CURRENCY_CD, ISS_SETTLE_AMNT, ISS_SETTLE_FEE, ISS_SETTLE_PROCESS_FEE, CARDHOLDER_BILL_CURRNCY_C, CARDHOLDER_BILL_AMOUNT, CARDHOLDER_BILL_ACT_FEE, CARDHOLDER_BILL_PROCESS_F, CARDHOLDER_BILL_SERV_FEE, TXN_ISS_CONVERSION_RT, TXN_CARDHOLDER_CONV_RT, PART_ID, DCRS_TRAN_NO, NEXT_TRAN_DATE, CREATEDDATE, CREATEDBY, FILEDATE, DCRS_REMARKS, FPAN, CYCLE, FILENAME\r\nfrom settlement_nfs_iccw_iss_nfs where FILEDATE =STR_TO_DATE( '"
					+ settlementBean.getDatepicker() + "' ,'%Y/%m/%d') and DCRS_REMARKS = 'NFS-ICCW-ISS-MATCHED-2'";
			String getInterchange10 = "SELECT CARDHOLDER_BILL_SERV_FEE, PARTICIPANT_ID, TRANSACTION_TYPE, FROM_ACCOUNT_TYPE, TO_ACCOUNT_TYPE, TXN_SERIAL_NO, RESPONSE_CODE, PAN_NUMBER, MEMBER_NUMBER, APPROVAL_NUMBER, SYS_TRACE_AUDIT_NO, TRANSACTION_DATE, TRANSACTION_TIME, MERCHANT_CATEGORY_CD, CARD_ACC_SETTLE_DT, CARD_ACC_ID, CARD_ACC_TERMINAL_ID, CARD_ACC_TERMINAL_LOC, ACQUIRER_ID, NETWORK_ID, ACCOUNT_1_NUMBER, ACCOUNT_1_BRANCH_ID, ACCOUNT_2_NUMBER, ACCOUNT_2_BRANCH_ID, TXN_CURRENCY_CODE, TXN_AMOUNT, ACTUAL_TXN_AMT, TXN_ACTIVITY_FEE, ISS_SETTLE_CURRENCY_CD, ISS_SETTLE_AMNT, ISS_SETTLE_FEE, ISS_SETTLE_PROCESS_FEE, CARDHOLDER_BILL_CURRNCY_C, CARDHOLDER_BILL_AMOUNT, CARDHOLDER_BILL_ACT_FEE, CARDHOLDER_BILL_PROCESS_F, CARDHOLDER_BILL_SERV_FEE, TXN_ISS_CONVERSION_RT, TXN_CARDHOLDER_CONV_RT, PART_ID, DCRS_TRAN_NO, NEXT_TRAN_DATE, CREATEDDATE, CREATEDBY, FILEDATE, DCRS_REMARKS, FPAN, CYCLE, FILENAME\r\nfrom settlement_nfs_iccw_iss_nfs where FILEDATE =STR_TO_DATE( '"
					+ settlementBean.getDatepicker() + "' ,'%Y/%m/%d') and DCRS_REMARKS = 'NFS-ICCW-ISS-UNRECON-2'";
			List<String> Column_list = new ArrayList<>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			data.add(Column_list);
			final List<String> columns6 = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange5, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns6) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 5 ");

			Column_list = new ArrayList<>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			data.add(Column_list);
			final List<String> columns7 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange6, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns7) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			Column_list = new ArrayList<>();
			Column_list.add("CARDHOLDER_BILL_SERV_FEE");
			Column_list.add("PARTICIPANT_ID");
			Column_list.add("TRANSACTION_TYPE");
			Column_list.add("FROM_ACCOUNT_TYPE");
			Column_list.add("TO_ACCOUNT_TYPE");
			Column_list.add("TXN_SERIAL_NO");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PAN_NUMBER");
			Column_list.add("MEMBER_NUMBER");
			Column_list.add("APPROVAL_NUMBER");
			Column_list.add("SYS_TRACE_AUDIT_NO");
			Column_list.add("TRANSACTION_DATE");
			Column_list.add("TRANSACTION_TIME");
			Column_list.add("MERCHANT_CATEGORY_CD");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("CARD_ACC_ID");
			Column_list.add("CARD_ACC_TERMINAL_ID");
			Column_list.add("CARD_ACC_TERMINAL_LOC");
			Column_list.add("ACQUIRER_ID");
			Column_list.add("NETWORK_ID");
			Column_list.add("ACCOUNT_1_NUMBER");
			Column_list.add("ACCOUNT_1_BRANCH_ID");
			Column_list.add("ACCOUNT_2_NUMBER");
			Column_list.add("ACCOUNT_2_BRANCH_ID");
			Column_list.add("TXN_CURRENCY_CODE");
			Column_list.add("TXN_AMOUNT");
			Column_list.add("ACTUAL_TXN_AMT");
			Column_list.add("TXN_ACTIVITY_FEE");
			Column_list.add("ISS_SETTLE_CURRENCY_CD");
			Column_list.add("ISS_SETTLE_AMNT");
			Column_list.add("ISS_SETTLE_FEE");
			Column_list.add("ISS_SETTLE_PROCESS_FEE");
			Column_list.add("PART_ID");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("FILEDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FPAN");
			Column_list.add("CYCLE");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns10 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange9, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns10) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 9 ");

			Column_list = new ArrayList<>();
			Column_list.add("CARDHOLDER_BILL_SERV_FEE");
			Column_list.add("PARTICIPANT_ID");
			Column_list.add("TRANSACTION_TYPE");
			Column_list.add("FROM_ACCOUNT_TYPE");
			Column_list.add("TO_ACCOUNT_TYPE");
			Column_list.add("TXN_SERIAL_NO");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PAN_NUMBER");
			Column_list.add("MEMBER_NUMBER");
			Column_list.add("APPROVAL_NUMBER");
			Column_list.add("SYS_TRACE_AUDIT_NO");
			Column_list.add("TRANSACTION_DATE");
			Column_list.add("TRANSACTION_TIME");
			Column_list.add("MERCHANT_CATEGORY_CD");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("CARD_ACC_ID");
			Column_list.add("CARD_ACC_TERMINAL_ID");
			Column_list.add("CARD_ACC_TERMINAL_LOC");
			Column_list.add("ACQUIRER_ID");
			Column_list.add("NETWORK_ID");
			Column_list.add("ACCOUNT_1_NUMBER");
			Column_list.add("ACCOUNT_1_BRANCH_ID");
			Column_list.add("ACCOUNT_2_NUMBER");
			Column_list.add("ACCOUNT_2_BRANCH_ID");
			Column_list.add("TXN_CURRENCY_CODE");
			Column_list.add("TXN_AMOUNT");
			Column_list.add("ACTUAL_TXN_AMT");
			Column_list.add("TXN_ACTIVITY_FEE");
			Column_list.add("ISS_SETTLE_CURRENCY_CD");
			Column_list.add("ISS_SETTLE_AMNT");
			Column_list.add("ISS_SETTLE_FEE");
			Column_list.add("ISS_SETTLE_PROCESS_FEE");
			Column_list.add("PART_ID");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("FILEDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FPAN");
			Column_list.add("CYCLE");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns11 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange10, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns11) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 10 ");

			System.out.println("Success ");
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> downloadICDACQReport(SettlementBean settlementBean) {
		return null;
	}

	public List<Object> downloadICCWACQReport(SettlementBean settlementBean) {
		ArrayList<Object> data = new ArrayList<Object>();
		try {
			String getInterchange1 = "SELECT BUSINESS_DATE, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, FEE, BR_CODE, ISS_SOL_ID, MCC, REMARKS, NETWORK, POSTED_DATE, GL_ACCOUNT, TR_NO, DCRS_REMARKS, FILEDATE, FILENAME, CREATEDDATE FROM settlement_nfs_iccw_acq_cbs where FILEDATE =STR_TO_DATE( '"
					+ settlementBean.getDatepicker() + "' ,'%Y/%m/%d')and DCRS_REMARKS = 'NFS-ICCW-ACQ-MATCHED-2'";
			String getInterchange2 = "SELECT BUSINESS_DATE, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, FEE, BR_CODE, ISS_SOL_ID, MCC, REMARKS, NETWORK, POSTED_DATE, GL_ACCOUNT, TR_NO, DCRS_REMARKS, FILEDATE, FILENAME, CREATEDDATE FROM settlement_nfs_iccw_acq_cbs where FILEDATE =STR_TO_DATE( '"
					+ settlementBean.getDatepicker() + "' ,'%Y/%m/%d')and DCRS_REMARKS = 'NFS-ICCW-ACQ-UNRECON-2'";
			String getInterchange3 = "SELECT PARTICIPANT_ID, TRANSACTION_TYPE, FROM_ACCOUNT_TYPE, TO_ACCOUNT_TYPE, TXN_SERIAL_NO, RESPONSE_CODE, PAN_NUMBER, MEMBER_NUMBER, APPROVAL_NUMBER, SYS_TRACE_AUDIT_NO, TRANSACTION_DATE, TRANSACTION_TIME, MERCHANT_CATEGORY_CD, CARD_ACC_SETTLE_DT, CARD_ACC_ID, CARD_ACC_TERMINAL_ID, CARD_ACC_TERMINAL_LOC, ACQUIRER_ID, ACQ_SETTLE_DATE, TXN_CURRENCY_CODE, TXN_AMOUNT, ACTUAL_TXN_AMT, TXN_ACTIVITY_FEE, ACQ_SETTLE_CURRENCY_CD, ACQ_SETTLE_AMNT, ACQ_SETTLE_FEE, ACQ_SETTLE_PROCESS_FEE, TXN_ACQ_CONV_RATE, PART_ID, DCRS_TRAN_NO, NEXT_TRAN_DATE, CREATEDDATE, CREATEDBY, FILEDATE, DCRS_REMARKS, CYCLE, FPAN, FILENAME, TRACE FROM settlement_nfs_iccw_acq_nfs where FILEDATE =STR_TO_DATE( '"
					+

					settlementBean.getDatepicker() + "' ,'%Y/%m/%d')and DCRS_REMARKS = 'NFS-ICCW-ACQ-MATCHED-2'";
			String getInterchange4 = "SELECT PARTICIPANT_ID, TRANSACTION_TYPE, FROM_ACCOUNT_TYPE, TO_ACCOUNT_TYPE, TXN_SERIAL_NO, RESPONSE_CODE, PAN_NUMBER, MEMBER_NUMBER, APPROVAL_NUMBER, SYS_TRACE_AUDIT_NO, TRANSACTION_DATE, TRANSACTION_TIME, MERCHANT_CATEGORY_CD, CARD_ACC_SETTLE_DT, CARD_ACC_ID, CARD_ACC_TERMINAL_ID, CARD_ACC_TERMINAL_LOC, ACQUIRER_ID, ACQ_SETTLE_DATE, TXN_CURRENCY_CODE, TXN_AMOUNT, ACTUAL_TXN_AMT, TXN_ACTIVITY_FEE, ACQ_SETTLE_CURRENCY_CD, ACQ_SETTLE_AMNT, ACQ_SETTLE_FEE, ACQ_SETTLE_PROCESS_FEE, TXN_ACQ_CONV_RATE, PART_ID, DCRS_TRAN_NO, NEXT_TRAN_DATE, CREATEDDATE, CREATEDBY, FILEDATE, DCRS_REMARKS, CYCLE, FPAN, FILENAME, TRACE FROM settlement_nfs_iccw_acq_nfs where FILEDATE =STR_TO_DATE( '"
					+

					settlementBean.getDatepicker() + "' ,'%Y/%m/%d')and DCRS_REMARKS = 'NFS-ICCW-ACQ-UNRECON-2'";
			List<String> Column_list = new ArrayList<>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");

			Column_list.add("CREATEDDATE");
			data.add(Column_list);
			final List<String> columns6 = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns6) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 5 ");

			Column_list = new ArrayList<>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");

			Column_list.add("CREATEDDATE");
			data.add(Column_list);
			final List<String> columns7 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns7) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			Column_list = new ArrayList<>();
			Column_list.add("PARTICIPANT_ID");
			Column_list.add("TRANSACTION_TYPE");
			Column_list.add("FROM_ACCOUNT_TYPE");
			Column_list.add("TO_ACCOUNT_TYPE");
			Column_list.add("TXN_SERIAL_NO");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PAN_NUMBER");
			Column_list.add("MEMBER_NUMBER");
			Column_list.add("APPROVAL_NUMBER");
			Column_list.add("SYS_TRACE_AUDIT_NO");
			Column_list.add("TRANSACTION_DATE");
			Column_list.add("TRANSACTION_TIME");
			Column_list.add("MERCHANT_CATEGORY_CD");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("CARD_ACC_ID");
			Column_list.add("CARD_ACC_TERMINAL_ID");
			Column_list.add("CARD_ACC_TERMINAL_LOC");
			Column_list.add("ACQUIRER_ID");
			Column_list.add("ACQ_SETTLE_DATE");
			Column_list.add("TXN_CURRENCY_CODE");
			Column_list.add("TXN_AMOUNT");
			Column_list.add("ACTUAL_TXN_AMT");
			Column_list.add("TXN_ACTIVITY_FEE");
			Column_list.add("ACQ_SETTLE_CURRENCY_CD");
			Column_list.add("ACQ_SETTLE_AMNT");
			Column_list.add("ACQ_SETTLE_FEE");
			Column_list.add("ACQ_SETTLE_PROCESS_FEE");
			Column_list.add("TXN_ACQ_CONV_RATE");
			Column_list.add("PART_ID");
			Column_list.add("DCRS_TRAN_NO");
			Column_list.add("NEXT_TRAN_DATE");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("FILEDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("CYCLE");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			Column_list.add("TRACE");
			data.add(Column_list);
			final List<String> columns10 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange3, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns10) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 9 ");

			Column_list = new ArrayList<>();
			Column_list.add("PARTICIPANT_ID");
			Column_list.add("TRANSACTION_TYPE");
			Column_list.add("FROM_ACCOUNT_TYPE");
			Column_list.add("TO_ACCOUNT_TYPE");
			Column_list.add("TXN_SERIAL_NO");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PAN_NUMBER");
			Column_list.add("MEMBER_NUMBER");
			Column_list.add("APPROVAL_NUMBER");
			Column_list.add("SYS_TRACE_AUDIT_NO");
			Column_list.add("TRANSACTION_DATE");
			Column_list.add("TRANSACTION_TIME");
			Column_list.add("MERCHANT_CATEGORY_CD");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("CARD_ACC_ID");
			Column_list.add("CARD_ACC_TERMINAL_ID");
			Column_list.add("CARD_ACC_TERMINAL_LOC");
			Column_list.add("ACQUIRER_ID");
			Column_list.add("ACQ_SETTLE_DATE");
			Column_list.add("TXN_CURRENCY_CODE");
			Column_list.add("TXN_AMOUNT");
			Column_list.add("ACTUAL_TXN_AMT");
			Column_list.add("TXN_ACTIVITY_FEE");
			Column_list.add("ACQ_SETTLE_CURRENCY_CD");
			Column_list.add("ACQ_SETTLE_AMNT");
			Column_list.add("ACQ_SETTLE_FEE");
			Column_list.add("ACQ_SETTLE_PROCESS_FEE");
			Column_list.add("TXN_ACQ_CONV_RATE");
			Column_list.add("PART_ID");
			Column_list.add("DCRS_TRAN_NO");
			Column_list.add("NEXT_TRAN_DATE");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("FILEDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("CYCLE");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			Column_list.add("TRACE");
			data.add(Column_list);
			final List<String> columns11 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange4, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns11) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 10 ");

			System.out.println("Success ");
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}

	}

	public List<Object> downloadMCISSPOSReport(SettlementBean settlementBean) {
		List<Object> data = new ArrayList();
		try {
			String getInterchange1 = "select * from settlement_mastercard_iss_pos where FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker()
					+ "','%Y/%m/%d') AND DCRS_REMARKS IN('MASTERCARD_ISS_POS_MATCHED-2','CBS_RAW_MATCH' )";
			String getInterchange2 = "select * from settlement_mastercard_iss_pos where FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks ='MASTERCARD_ISS_POS_UNRECON-2'";
			String getInterchange3 = "select * from settlement_mastercard_iss_pos_cbs where FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker()
					+ "','%Y/%m/%d') and dcrs_remarks  IN('MASTERCARD_ISS_POS_MATCHED-2','CBS_RAW_MATCH' )";
			String getInterchange4 = "select * from settlement_mastercard_iss_pos_cbs where FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks ='MASTERCARD_ISS_POS_UNRECON-2'";
			String getInterchange5 = "select * from settlement_mastercard_iss_pos_cbs where FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks ='MASTERCARD_ISS_POS_UNRECON-1'";
			String getInterchange6 = "select * from settlement_mastercard_iss_pos_cbs where FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks ='MASTERCARD_ISS_POS_KNOCKOFF'";
			List<String> Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_iss_pos");
			data.add(Column_list);
			final List<String> columns = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
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
			System.out.println("query 1");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_iss_pos");
			data.add(Column_list);
			final List<String> columns2 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns2) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 2");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_iss_pos_cbs");
			data.add(Column_list);
			final List<String> columns3 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange3, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns3) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 3");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_iss_pos_cbs");
			data.add(Column_list);
			final List<String> columns4 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange4, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns4) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 4");
			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_iss_pos_cbs");
			data.add(Column_list);
			final List<String> columns5 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange5, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns5) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 5");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_iss_pos_cbs");
			data.add(Column_list);
			final List<String> columns6 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange6, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns6) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 6");
			data.add(DailyData);
			System.out.println("Success ");
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> downloadMCISSATMReport(SettlementBean settlementBean) {
		List<Object> data = new ArrayList();
		try {
			String getInterchange1 = "select * from  settlement_mastercard_iss_cbs where  FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d')  AND DCRS_REMARKS= 'MASTERCARD_ISS_MATCHED-2'";
			String getInterchange2 = "select * from settlement_mastercard_iss_cbs where  FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d')  and dcrs_remarks ='MASTERCARD_ISS_UNRECON-2'";
			String getInterchange3 = "select * from settlement_mastercard_iss_cbs where FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d')  and dcrs_remarks ='MASTERCARD_ISS_UNRECON-1' ";
			String getInterchange4 = "select * from settlement_mastercard_iss_cbs where FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d')  and dcrs_remarks ='MASTERCARD_ISS_KNOCKOFF'";
			String getInterchange5 = "select * from settlement_mastercard_iss_atm  where  FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d')  and dcrs_remarks ='MASTERCARD_ISS_MATCHED-2'";
			String getInterchange6 = "select * from settlement_mastercard_iss_atm  where  FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d')  and dcrs_remarks ='MASTERCARD_ISS_UNRECON-2'";
			List<String> Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_iss_cbs");
			data.add(Column_list);
			final List<String> columns = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
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
			System.out.println("query 1");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_iss_cbs");
			data.add(Column_list);
			final List<String> columns2 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns2) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 2");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_iss_cbs");
			data.add(Column_list);
			final List<String> columns3 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange3, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns3) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 3");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_iss_cbs");
			data.add(Column_list);
			final List<String> columns4 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange4, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns4) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 4");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_iss_atm");
			data.add(Column_list);
			final List<String> columns5 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange5, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns5) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 5");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_iss_atm");
			data.add(Column_list);
			final List<String> columns6 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange6, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns6) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 6");

			System.out.println("Success ");
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> downloadMCISSATMReportACQ(SettlementBean settlementBean) {
		List<Object> data = new ArrayList();
		try {
			String getInterchange1 = "select * from   settlement_mastercard_acq_atm where  FILEDATE  =  STR_to_date('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d')  AND DCRS_REMARKS= 'MASTERCARD_ACQ_MATCHED'";
			String getInterchange2 = "select * from settlement_mastercard_acq_atm where  FILEDATE  =  STR_to_date('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks ='MASTERCARD_ACQ_UNRECON'";
			String getInterchange3 = "select * from settlement_mastercard_acq_cbs  where FILEDATE  =  STR_to_date('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks ='MASTERCARD_ACQ_MATCHED' ";
			String getInterchange4 = "select * from settlement_mastercard_acq_cbs where FILEDATE  =  STR_to_date('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks ='MASTERCARD_ACQ_UNRECON'";
			String getInterchange5 = "select * from settlement_mastercard_acq_cbs  where  FILEDATE  =  STR_to_date('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks ='MASTERCARD_ACQ_KNOCKOFF'";
			String getInterchange6 = "select * from settlement_mastercard_acq_atm where filedate=STR_to_date('"
					+ settlementBean.getDatepicker()
					+ "','%Y/%m/%d') and dcrs_remarks ='INT_CBS_DOM_ATM_CROSS_MATCHED' ";
			List<String> Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_acq_atm");
			data.add(Column_list);
			final List<String> columns = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
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
			System.out.println("query 1");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_acq_atm");
			data.add(Column_list);
			final List<String> columns2 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns2) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 2");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_acq_cbs");
			data.add(Column_list);
			final List<String> columns3 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange3, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns3) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 3");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_acq_cbs");
			data.add(Column_list);
			final List<String> columns4 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange4, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns4) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 4");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_acq_cbs");
			data.add(Column_list);
			final List<String> columns5 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange5, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns5) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 5");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_acq_atm");
			data.add(Column_list);
			final List<String> columns6 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange6, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns6) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 6");

			System.out.println("Success ");
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> downloadMCISSATMReportACQINT(SettlementBean settlementBean) {
		List<Object> data = new ArrayList();
		try {
			String getInterchange1 = "select * from settlement_mastercard_acq_int_atm where filedate=STR_to_date('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks ='MASTERCARD_ACQ_INT_MATCHED'";
			String getInterchange2 = "select * from settlement_mastercard_acq_int_atm where filedate=STR_to_date('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks ='MASTERCARD_ACQ_INT_UNRECON'";
			String getInterchange3 = "select * from settlement_mastercard_acq_int_cbs where filedate=STR_to_date('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks ='MASTERCARD_ACQ_INT_MATCHED'";
			String getInterchange4 = "select * from settlement_mastercard_acq_int_cbs where filedate=STR_to_date('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks ='MASTERCARD_ACQ_INT_KNOCKOFF'";
			String getInterchange5 = "select * from settlement_mastercard_acq_int_cbs where filedate=STR_to_date('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') and dcrs_remarks ='MASTERCARD_ACQ_INT_UNRECON'";
			String getInterchange6 = "select * from settlement_mastercard_acq_int_cbs where filedate=STR_to_date('"
					+ settlementBean.getDatepicker()
					+ "','%Y/%m/%d') and dcrs_remarks ='INT_CBS_DOM_ATM_CROSS_MATCHED'";
			List<String> Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_acq_int_atm");
			data.add(Column_list);
			final List<String> columns = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
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
			System.out.println("query 1");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_acq_int_atm");
			data.add(Column_list);
			final List<String> columns2 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns2) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 2");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_acq_int_cbs");
			data.add(Column_list);
			final List<String> columns3 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange3, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns3) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 3");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_acq_int_cbs");
			data.add(Column_list);
			final List<String> columns4 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange4, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns4) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 4");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_acq_int_cbs");
			data.add(Column_list);
			final List<String> columns5 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange5, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns5) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 5");
			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_mastercard_acq_int_cbs");
			data.add(Column_list);
			final List<String> columns6 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange6, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns6) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 6");

			System.out.println("Success ");
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> downloadVISAISSINTPOSReport(SettlementBean settlementBean) {
		return null;
	}

	public List<Object> downloadVISAISSINTATMReport(SettlementBean settlementBean) {
		List<Object> data = new ArrayList();
		try {
			String getInterchange1 = "SELECT CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, TRAN_TIME, PURCHASE_DATE, CBS_AMOUNT, VISA_AMOUNT, SUR, AUTH_CODE, \r\nSETTLEMENT_FLAG, TC, DCRS_REMARKS, FILEDATE FROM visa_iss_surcharge WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d')";
			String getInterchange2 = "SELECT CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, BR_CODE, ISS_SOL_ID, MCC, REMARKS, NETWORK, POSTED_DATE, GL_ACCOUNT, \r\nTR_NO, DCRS_REMARKS, FILEDATE, FILENAME, CREATEDDATE, TMP_ID, AUTH_CODE \r\nFROM settlement_visa_iss_cbs WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ISS-MATCHED-1'";
			String getInterchange3 = "SELECT CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, BR_CODE, ISS_SOL_ID, MCC, REMARKS, NETWORK, POSTED_DATE, GL_ACCOUNT, \r\nTR_NO, DCRS_REMARKS, FILEDATE, FILENAME, CREATEDDATE, TMP_ID, AUTH_CODE \r\nFROM settlement_visa_iss_cbs WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ISS-UNRECON-1'";
			String getInterchange4 = "SELECT CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, BR_CODE, ISS_SOL_ID, MCC, REMARKS, NETWORK, POSTED_DATE, GL_ACCOUNT, \r\nTR_NO, DCRS_REMARKS, FILEDATE, FILENAME, CREATEDDATE, TMP_ID, AUTH_CODE \r\nFROM settlement_visa_iss_cbs WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ISS-UNRECON-2'";
			String getInterchange4A = "SELECT CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, BR_CODE, ISS_SOL_ID, MCC, REMARKS, NETWORK, POSTED_DATE, GL_ACCOUNT, \r\nTR_NO, DCRS_REMARKS, FILEDATE, FILENAME, CREATEDDATE, TMP_ID, AUTH_CODE \r\nFROM settlement_visa_iss_cbs WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS LIKE 'VISA-ISS-MATCHED-2%'";
			String getInterchange5 = "SELECT DATE_TIME, REC_TYPE, AUTH_PPD, TERM_LN, TERM_FIID, TERM_TERM_ID, CRD_LN, CRD_FIID, CRD_PAN, CRD_MBR_NUM, BRCH_ID, REGN_ID, USER_FLD1X, TYP_CDE, TYP, RTE_STAT, ORIGINATOR, RESPONDER, \r\nENTRY_TIME, EXIT_TIME, RE_ENTRY_TIME, TRAN_DATE, TRAN_TIM, POST_DAT, ACQ_ICHG_SETL_DAT, ISS_ICHG_SETL_DAT, SEQ_NUM, TERM_TYP, TIM_OFST, ACQ_INST_ID_NUM, RCV_INST_ID_NUM, TRAN_CDE, FROM_ACCT, \r\nUSER_FLD1, TO_ACCT, MULT_ACCT, AMT_1, AMT_2, AMT_3, DEP_BAL_CR, DEP_TYP, RESP_CDE, TERM_NAME_LOC, TERM_OWNER_NAME, TERM_CITY, TERM_ST, TERM_CNTRY, ORIG_OSEQ_NUM, ORIG_OTRAN_DAT, ORIG_OTRAN_TIM, \r\nORIG_B24_POST, ORIG_CRNCY_CDE, MULT_CRNCY_AUTH_CRNCY_CDE, MULT_CRNCY_AUTH_CONV_RATE, MULT_CRNCY_SETL_CRNCY_CDE, MULT_CRNCY_SETL_CONV_RATE, MULT_CRNCY_CONV_DAT_TIM, RVSL_RSN, PIN_OFST, SHRG_GRP, \r\nDEST_ORDER, AUTH_ID_RESP, REFR_IMP_IND, REFR_AVAIL_IMP, REFR_LEDG_IMP, REFR_HLD_AMT_IMP, REFR_CAF_REFR_IND, REFR_USER_FLD3, DEP_SETL_IMP_FLG, ADJ_SETL_IMP_FLG, REFR_IND, USER_FLD4, FRWD_INST_ID_NUM, \r\nCRD_ACCPT_ID_NUM, CRD_ISS_ID_NUM, USER_FLD6, DCRS_REMARKS, FILEDATE, FILENAME, MULTI_CRNY_AUTH_CRNCY_CODE, PT_SRV_COND_CODE, APPRV_CDE, REC_TYP \r\nFROM settlement_visa_iss_switch WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ISS-UNRECON-1'";
			String getInterchange6 = "SELECT DATE_TIME, REC_TYPE, AUTH_PPD, TERM_LN, TERM_FIID, TERM_TERM_ID, CRD_LN, CRD_FIID, CRD_PAN, CRD_MBR_NUM, BRCH_ID, REGN_ID, USER_FLD1X, TYP_CDE, TYP, RTE_STAT, ORIGINATOR, RESPONDER, \r\nENTRY_TIME, EXIT_TIME, RE_ENTRY_TIME, TRAN_DATE, TRAN_TIM, POST_DAT, ACQ_ICHG_SETL_DAT, ISS_ICHG_SETL_DAT, SEQ_NUM, TERM_TYP, TIM_OFST, ACQ_INST_ID_NUM, RCV_INST_ID_NUM, TRAN_CDE, FROM_ACCT, \r\nUSER_FLD1, TO_ACCT, MULT_ACCT, AMT_1, AMT_2, AMT_3, DEP_BAL_CR, DEP_TYP, RESP_CDE, TERM_NAME_LOC, TERM_OWNER_NAME, TERM_CITY, TERM_ST, TERM_CNTRY, ORIG_OSEQ_NUM, ORIG_OTRAN_DAT, ORIG_OTRAN_TIM, \r\nORIG_B24_POST, ORIG_CRNCY_CDE, MULT_CRNCY_AUTH_CRNCY_CDE, MULT_CRNCY_AUTH_CONV_RATE, MULT_CRNCY_SETL_CRNCY_CDE, MULT_CRNCY_SETL_CONV_RATE, MULT_CRNCY_CONV_DAT_TIM, RVSL_RSN, PIN_OFST, SHRG_GRP, \r\nDEST_ORDER, AUTH_ID_RESP, REFR_IMP_IND, REFR_AVAIL_IMP, REFR_LEDG_IMP, REFR_HLD_AMT_IMP, REFR_CAF_REFR_IND, REFR_USER_FLD3, DEP_SETL_IMP_FLG, ADJ_SETL_IMP_FLG, REFR_IND, USER_FLD4, FRWD_INST_ID_NUM, \r\nCRD_ACCPT_ID_NUM, CRD_ISS_ID_NUM, USER_FLD6, DCRS_REMARKS, FILEDATE, FILENAME, MULTI_CRNY_AUTH_CRNCY_CODE, PT_SRV_COND_CODE, APPRV_CDE, REC_TYP \r\nFROM settlement_visa_iss_switch WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ISS-MATCHED-1'";
			String getInterchange7 = "SELECT TC, TCR_CODE, CARD_NUMBER, SOURCE_AMOUNT, AUTHORIZATION_CODE, DCRS_SEQ_NO, PART_ID, CREATEDDATE, FLOOR_LIMIT_INDI, ARN, ACQUIRER_BUSI_ID, \r\nPURCHASE_DATE, DESTINATION_AMOUNT, DESTINATION_CURR_CODE, SOURCE_CURR_CODE, MERCHANT_NAME, MERCHANT_CITY, MERCHANT_COUNTRY_CODE, \r\nMERCHANT_CATEGORY_CODE, MERCHANT_ZIP_CODE, USAGE_CODE, SETTLEMENT_FLAG, AUTH_CHARA_IND, POS_TERMINAL_CAPABILITY, CARDHOLDER_ID_METHOD, \r\nCOLLECTION_ONLY_FLAG, POS_ENTRY_MODE, CENTRAL_PROCESS_DATE, REIMBURSEMENT_ATTR, REASON_CODE, TRAN_ID, FPAN, FILENAME, DCRS_REMARKS, FILEDATE \r\nFROM settlement_visa_iss_visa WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS LIKE 'VISA-ISS-MATCHED-2%'";
			String getInterchange8 = "SELECT TC, TCR_CODE, CARD_NUMBER, SOURCE_AMOUNT, AUTHORIZATION_CODE, DCRS_SEQ_NO, PART_ID, CREATEDDATE, FLOOR_LIMIT_INDI, ARN, ACQUIRER_BUSI_ID, \r\nPURCHASE_DATE, DESTINATION_AMOUNT, DESTINATION_CURR_CODE, SOURCE_CURR_CODE, MERCHANT_NAME, MERCHANT_CITY, MERCHANT_COUNTRY_CODE, \r\nMERCHANT_CATEGORY_CODE, MERCHANT_ZIP_CODE, USAGE_CODE, SETTLEMENT_FLAG, AUTH_CHARA_IND, POS_TERMINAL_CAPABILITY, CARDHOLDER_ID_METHOD, \r\nCOLLECTION_ONLY_FLAG, POS_ENTRY_MODE, CENTRAL_PROCESS_DATE, REIMBURSEMENT_ATTR, REASON_CODE, TRAN_ID, FPAN, FILENAME, DCRS_REMARKS, FILEDATE\r\nFROM settlement_visa_iss_visa WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ISS-UNRECON-2'";
			String getInterchange9 = "SELECT TC, TCR_CODE, CARD_NUMBER, SOURCE_AMOUNT, AUTHORIZATION_CODE, DCRS_SEQ_NO, PART_ID, CREATEDDATE, FLOOR_LIMIT_INDI, ARN, ACQUIRER_BUSI_ID, \r\nPURCHASE_DATE, DESTINATION_AMOUNT, DESTINATION_CURR_CODE, SOURCE_CURR_CODE, MERCHANT_NAME, MERCHANT_CITY, MERCHANT_COUNTRY_CODE, \r\nMERCHANT_CATEGORY_CODE, MERCHANT_ZIP_CODE, USAGE_CODE, SETTLEMENT_FLAG, AUTH_CHARA_IND, POS_TERMINAL_CAPABILITY, CARDHOLDER_ID_METHOD, \r\nCOLLECTION_ONLY_FLAG, POS_ENTRY_MODE, CENTRAL_PROCESS_DATE, REIMBURSEMENT_ATTR, REASON_CODE, TRAN_ID, FPAN, FILENAME, DCRS_REMARKS, FILEDATE\r\nFROM settlement_visa_iss_visa WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='ORG-WDL-REVERSAL-MATCHED'";
			String getInterchange10 = "SELECT TC, TCR_CODE, CARD_NUMBER, SOURCE_AMOUNT, AUTHORIZATION_CODE, DCRS_SEQ_NO, PART_ID, CREATEDDATE, FLOOR_LIMIT_INDI, ARN, ACQUIRER_BUSI_ID, \r\nPURCHASE_DATE, DESTINATION_AMOUNT, DESTINATION_CURR_CODE, SOURCE_CURR_CODE, MERCHANT_NAME, MERCHANT_CITY, MERCHANT_COUNTRY_CODE, \r\nMERCHANT_CATEGORY_CODE, MERCHANT_ZIP_CODE, USAGE_CODE, SETTLEMENT_FLAG, AUTH_CHARA_IND, POS_TERMINAL_CAPABILITY, CARDHOLDER_ID_METHOD, \r\nCOLLECTION_ONLY_FLAG, POS_ENTRY_MODE, CENTRAL_PROCESS_DATE, REIMBURSEMENT_ATTR, REASON_CODE, TRAN_ID, FPAN, FILENAME, DCRS_REMARKS, ORG_FILEDATE, FILEDATE\r\nFROM settlement_visa_iss_visa_orgwdl WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='MATCHED'";
			String getInterchange11 = "SELECT TC, TCR_CODE, CARD_NUMBER, SOURCE_AMOUNT, AUTHORIZATION_CODE, DCRS_SEQ_NO, PART_ID, CREATEDDATE, FLOOR_LIMIT_INDI, ARN, ACQUIRER_BUSI_ID, \r\nPURCHASE_DATE, DESTINATION_AMOUNT, DESTINATION_CURR_CODE, SOURCE_CURR_CODE, MERCHANT_NAME, MERCHANT_CITY, MERCHANT_COUNTRY_CODE, \r\nMERCHANT_CATEGORY_CODE, MERCHANT_ZIP_CODE, USAGE_CODE, SETTLEMENT_FLAG, AUTH_CHARA_IND, POS_TERMINAL_CAPABILITY, CARDHOLDER_ID_METHOD, \r\nCOLLECTION_ONLY_FLAG, POS_ENTRY_MODE, CENTRAL_PROCESS_DATE, REIMBURSEMENT_ATTR, REASON_CODE, TRAN_ID, FPAN, FILENAME, DCRS_REMARKS, ORG_FILEDATE, FILEDATE\r\nFROM settlement_visa_iss_visa_orgwdl WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='UNMATCHED'";
			List<String> Column_list = new ArrayList<>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("TRAN_TIME");
			Column_list.add("PURCHASE_DATE");
			Column_list.add("CBS_AMOUNT");
			Column_list.add("VISA_AMOUNT");
			Column_list.add("SUR");
			Column_list.add("AUTH_CODE");
			Column_list.add("SETTLEMENT_FLAG");
			Column_list.add("TC");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			data.add(Column_list);
			final List<String> columns = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
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
			System.out.println("query 1");

			Column_list = new ArrayList<>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("TMP_ID");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("AUTH_CODE");
			data.add(Column_list);
			final List<String> columns2 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns2) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 2");

			Column_list = new ArrayList<>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("TMP_ID");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("AUTH_CODE");
			data.add(Column_list);
			final List<String> columns3 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange3, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns3) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 3");

			Column_list = new ArrayList<>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("TMP_ID");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("AUTH_CODE");
			data.add(Column_list);
			final List<String> columns4 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange4, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns4) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 4");

			Column_list = new ArrayList<>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("TMP_ID");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("AUTH_CODE");
			data.add(Column_list);
			final List<String> columns4A = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange4A, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns4A) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 4A");

			Column_list = new ArrayList<>();
			Column_list.add("DATE_TIME");
			Column_list.add("REC_TYPE");
			Column_list.add("AUTH_PPD");
			Column_list.add("TERM_LN");
			Column_list.add("TERM_FIID");
			Column_list.add("TERM_TERM_ID");
			Column_list.add("CRD_LN");
			Column_list.add("CRD_FIID");
			Column_list.add("CRD_PAN");
			Column_list.add("CRD_MBR_NUM");
			Column_list.add("BRCH_ID");
			Column_list.add("REGN_ID");
			Column_list.add("USER_FLD1X");
			Column_list.add("TYP_CDE");
			Column_list.add("TYP");
			Column_list.add("RTE_STAT");
			Column_list.add("ORIGINATOR");
			Column_list.add("RESPONDER");
			Column_list.add("ENTRY_TIME");
			Column_list.add("EXIT_TIME");
			Column_list.add("RE_ENTRY_TIME");
			Column_list.add("TRAN_DATE");
			Column_list.add("TRAN_TIM");
			Column_list.add("POST_DAT");
			Column_list.add("ACQ_ICHG_SETL_DAT");
			Column_list.add("ISS_ICHG_SETL_DAT");
			Column_list.add("SEQ_NUM");
			Column_list.add("TERM_TYP");
			Column_list.add("TIM_OFST");
			Column_list.add("ACQ_INST_ID_NUM");
			Column_list.add("RCV_INST_ID_NUM");
			Column_list.add("TRAN_CDE");
			Column_list.add("FROM_ACCT");
			Column_list.add("USER_FLD1");
			Column_list.add("TO_ACCT");
			Column_list.add("MULT_ACCT");
			Column_list.add("AMT_1");
			Column_list.add("AMT_2");
			Column_list.add("AMT_3");
			Column_list.add("DEP_BAL_CR");
			Column_list.add("DEP_TYP");
			Column_list.add("RESP_CDE");
			Column_list.add("TERM_NAME_LOC");
			Column_list.add("TERM_OWNER_NAME");
			Column_list.add("TERM_CITY");
			Column_list.add("TERM_ST");
			Column_list.add("TERM_CNTRY");
			Column_list.add("ORIG_OSEQ_NUM");
			Column_list.add("ORIG_OTRAN_DAT");
			Column_list.add("ORIG_OTRAN_TIM");
			Column_list.add("ORIG_B24_POST");
			Column_list.add("ORIG_CRNCY_CDE");
			Column_list.add("MULT_CRNCY_AUTH_CRNCY_CDE");
			Column_list.add("MULT_CRNCY_AUTH_CONV_RATE");
			Column_list.add("MULT_CRNCY_SETL_CRNCY_CDE");
			Column_list.add("MULT_CRNCY_SETL_CONV_RATE");
			Column_list.add("MULT_CRNCY_CONV_DAT_TIM");
			Column_list.add("RVSL_RSN");
			Column_list.add("PIN_OFST");
			Column_list.add("SHRG_GRP");
			Column_list.add("DEST_ORDER");
			Column_list.add("AUTH_ID_RESP");
			Column_list.add("REFR_IMP_IND");
			Column_list.add("REFR_AVAIL_IMP");
			Column_list.add("REFR_LEDG_IMP");
			Column_list.add("REFR_HLD_AMT_IMP");
			Column_list.add("REFR_CAF_REFR_IND");
			Column_list.add("REFR_USER_FLD3");
			Column_list.add("DEP_SETL_IMP_FLG");
			Column_list.add("ADJ_SETL_IMP_FLG");
			Column_list.add("REFR_IND");
			Column_list.add("USER_FLD4");
			Column_list.add("FRWD_INST_ID_NUM");
			Column_list.add("CRD_ACCPT_ID_NUM");
			Column_list.add("CRD_ISS_ID_NUM");
			Column_list.add("USER_FLD6");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("MULTI_CRNY_AUTH_CRNCY_CODE");
			Column_list.add("PT_SRV_COND_CODE");
			Column_list.add("APPRV_CDE");
			Column_list.add("REC_TYP");
			data.add(Column_list);
			final List<String> columns5 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange5, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns5) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 5");

			Column_list = new ArrayList<>();
			Column_list.add("DATE_TIME");
			Column_list.add("REC_TYPE");
			Column_list.add("AUTH_PPD");
			Column_list.add("TERM_LN");
			Column_list.add("TERM_FIID");
			Column_list.add("TERM_TERM_ID");
			Column_list.add("CRD_LN");
			Column_list.add("CRD_FIID");
			Column_list.add("CRD_PAN");
			Column_list.add("CRD_MBR_NUM");
			Column_list.add("BRCH_ID");
			Column_list.add("REGN_ID");
			Column_list.add("USER_FLD1X");
			Column_list.add("TYP_CDE");
			Column_list.add("TYP");
			Column_list.add("RTE_STAT");
			Column_list.add("ORIGINATOR");
			Column_list.add("RESPONDER");
			Column_list.add("ENTRY_TIME");
			Column_list.add("EXIT_TIME");
			Column_list.add("RE_ENTRY_TIME");
			Column_list.add("TRAN_DATE");
			Column_list.add("TRAN_TIM");
			Column_list.add("POST_DAT");
			Column_list.add("ACQ_ICHG_SETL_DAT");
			Column_list.add("ISS_ICHG_SETL_DAT");
			Column_list.add("SEQ_NUM");
			Column_list.add("TERM_TYP");
			Column_list.add("TIM_OFST");
			Column_list.add("ACQ_INST_ID_NUM");
			Column_list.add("RCV_INST_ID_NUM");
			Column_list.add("TRAN_CDE");
			Column_list.add("FROM_ACCT");
			Column_list.add("USER_FLD1");
			Column_list.add("TO_ACCT");
			Column_list.add("MULT_ACCT");
			Column_list.add("AMT_1");
			Column_list.add("AMT_2");
			Column_list.add("AMT_3");
			Column_list.add("DEP_BAL_CR");
			Column_list.add("DEP_TYP");
			Column_list.add("RESP_CDE");
			Column_list.add("TERM_NAME_LOC");
			Column_list.add("TERM_OWNER_NAME");
			Column_list.add("TERM_CITY");
			Column_list.add("TERM_ST");
			Column_list.add("TERM_CNTRY");
			Column_list.add("ORIG_OSEQ_NUM");
			Column_list.add("ORIG_OTRAN_DAT");
			Column_list.add("ORIG_OTRAN_TIM");
			Column_list.add("ORIG_B24_POST");
			Column_list.add("ORIG_CRNCY_CDE");
			Column_list.add("MULT_CRNCY_AUTH_CRNCY_CDE");
			Column_list.add("MULT_CRNCY_AUTH_CONV_RATE");
			Column_list.add("MULT_CRNCY_SETL_CRNCY_CDE");
			Column_list.add("MULT_CRNCY_SETL_CONV_RATE");
			Column_list.add("MULT_CRNCY_CONV_DAT_TIM");
			Column_list.add("RVSL_RSN");
			Column_list.add("PIN_OFST");
			Column_list.add("SHRG_GRP");
			Column_list.add("DEST_ORDER");
			Column_list.add("AUTH_ID_RESP");
			Column_list.add("REFR_IMP_IND");
			Column_list.add("REFR_AVAIL_IMP");
			Column_list.add("REFR_LEDG_IMP");
			Column_list.add("REFR_HLD_AMT_IMP");
			Column_list.add("REFR_CAF_REFR_IND");
			Column_list.add("REFR_USER_FLD3");
			Column_list.add("DEP_SETL_IMP_FLG");
			Column_list.add("ADJ_SETL_IMP_FLG");
			Column_list.add("REFR_IND");
			Column_list.add("USER_FLD4");
			Column_list.add("FRWD_INST_ID_NUM");
			Column_list.add("CRD_ACCPT_ID_NUM");
			Column_list.add("CRD_ISS_ID_NUM");
			Column_list.add("USER_FLD6");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("MULTI_CRNY_AUTH_CRNCY_CODE");
			Column_list.add("PT_SRV_COND_CODE");
			Column_list.add("APPRV_CDE");
			Column_list.add("REC_TYP");
			data.add(Column_list);
			final List<String> columns6 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange6, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns6) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 6");

			Column_list = new ArrayList<>();
			Column_list.add("TC");
			Column_list.add("TCR_CODE");
			Column_list.add("CARD_NUMBER");
			Column_list.add("SOURCE_AMOUNT");
			Column_list.add("AUTHORIZATION_CODE");
			Column_list.add("DCRS_SEQ_NO");
			Column_list.add("PART_ID");
			Column_list.add("CREATEDDATE");
			Column_list.add("FLOOR_LIMIT_INDI");
			Column_list.add("ARN");
			Column_list.add("ACQUIRER_BUSI_ID");
			Column_list.add("PURCHASE_DATE");
			Column_list.add("DESTINATION_AMOUNT");
			Column_list.add("DESTINATION_CURR_CODE");
			Column_list.add("SOURCE_CURR_CODE");
			Column_list.add("MERCHANT_NAME");
			Column_list.add("MERCHANT_CITY");
			Column_list.add("MERCHANT_COUNTRY_CODE");
			Column_list.add("MERCHANT_CATEGORY_CODE");
			Column_list.add("MERCHANT_ZIP_CODE");
			Column_list.add("USAGE_CODE");
			Column_list.add("SETTLEMENT_FLAG");
			Column_list.add("AUTH_CHARA_IND");
			Column_list.add("POS_TERMINAL_CAPABILITY");
			Column_list.add("CARDHOLDER_ID_METHOD");
			Column_list.add("COLLECTION_ONLY_FLAG");
			Column_list.add("POS_ENTRY_MODE");
			Column_list.add("CENTRAL_PROCESS_DATE");
			Column_list.add("REIMBURSEMENT_ATTR");
			Column_list.add("REASON_CODE");
			Column_list.add("TRAN_ID");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			data.add(Column_list);
			final List<String> columns7 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange7, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns7) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 7");

			Column_list = new ArrayList<>();
			Column_list.add("TC");
			Column_list.add("TCR_CODE");
			Column_list.add("CARD_NUMBER");
			Column_list.add("SOURCE_AMOUNT");
			Column_list.add("AUTHORIZATION_CODE");
			Column_list.add("DCRS_SEQ_NO");
			Column_list.add("PART_ID");
			Column_list.add("CREATEDDATE");
			Column_list.add("FLOOR_LIMIT_INDI");
			Column_list.add("ARN");
			Column_list.add("ACQUIRER_BUSI_ID");
			Column_list.add("PURCHASE_DATE");
			Column_list.add("DESTINATION_AMOUNT");
			Column_list.add("DESTINATION_CURR_CODE");
			Column_list.add("SOURCE_CURR_CODE");
			Column_list.add("MERCHANT_NAME");
			Column_list.add("MERCHANT_CITY");
			Column_list.add("MERCHANT_COUNTRY_CODE");
			Column_list.add("MERCHANT_CATEGORY_CODE");
			Column_list.add("MERCHANT_ZIP_CODE");
			Column_list.add("USAGE_CODE");
			Column_list.add("SETTLEMENT_FLAG");
			Column_list.add("AUTH_CHARA_IND");
			Column_list.add("POS_TERMINAL_CAPABILITY");
			Column_list.add("CARDHOLDER_ID_METHOD");
			Column_list.add("COLLECTION_ONLY_FLAG");
			Column_list.add("POS_ENTRY_MODE");
			Column_list.add("CENTRAL_PROCESS_DATE");
			Column_list.add("REIMBURSEMENT_ATTR");
			Column_list.add("REASON_CODE");
			Column_list.add("TRAN_ID");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			data.add(Column_list);
			final List<String> columns8 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange8, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns8) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 8");

			Column_list = new ArrayList<>();
			Column_list.add("TC");
			Column_list.add("TCR_CODE");
			Column_list.add("CARD_NUMBER");
			Column_list.add("SOURCE_AMOUNT");
			Column_list.add("AUTHORIZATION_CODE");
			Column_list.add("DCRS_SEQ_NO");
			Column_list.add("PART_ID");
			Column_list.add("CREATEDDATE");
			Column_list.add("FLOOR_LIMIT_INDI");
			Column_list.add("ARN");
			Column_list.add("ACQUIRER_BUSI_ID");
			Column_list.add("PURCHASE_DATE");
			Column_list.add("DESTINATION_AMOUNT");
			Column_list.add("DESTINATION_CURR_CODE");
			Column_list.add("SOURCE_CURR_CODE");
			Column_list.add("MERCHANT_NAME");
			Column_list.add("MERCHANT_CITY");
			Column_list.add("MERCHANT_COUNTRY_CODE");
			Column_list.add("MERCHANT_CATEGORY_CODE");
			Column_list.add("MERCHANT_ZIP_CODE");
			Column_list.add("USAGE_CODE");
			Column_list.add("SETTLEMENT_FLAG");
			Column_list.add("AUTH_CHARA_IND");
			Column_list.add("POS_TERMINAL_CAPABILITY");
			Column_list.add("CARDHOLDER_ID_METHOD");
			Column_list.add("COLLECTION_ONLY_FLAG");
			Column_list.add("POS_ENTRY_MODE");
			Column_list.add("CENTRAL_PROCESS_DATE");
			Column_list.add("REIMBURSEMENT_ATTR");
			Column_list.add("REASON_CODE");
			Column_list.add("TRAN_ID");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			data.add(Column_list);
			final List<String> columns9 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange9, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns9) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 9");

			Column_list = new ArrayList<>();
			Column_list.add("TC");
			Column_list.add("TCR_CODE");
			Column_list.add("CARD_NUMBER");
			Column_list.add("SOURCE_AMOUNT");
			Column_list.add("AUTHORIZATION_CODE");
			Column_list.add("DCRS_SEQ_NO");
			Column_list.add("PART_ID");
			Column_list.add("CREATEDDATE");
			Column_list.add("FLOOR_LIMIT_INDI");
			Column_list.add("ARN");
			Column_list.add("ACQUIRER_BUSI_ID");
			Column_list.add("PURCHASE_DATE");
			Column_list.add("DESTINATION_AMOUNT");
			Column_list.add("DESTINATION_CURR_CODE");
			Column_list.add("SOURCE_CURR_CODE");
			Column_list.add("MERCHANT_NAME");
			Column_list.add("MERCHANT_CITY");
			Column_list.add("MERCHANT_COUNTRY_CODE");
			Column_list.add("MERCHANT_CATEGORY_CODE");
			Column_list.add("MERCHANT_ZIP_CODE");
			Column_list.add("USAGE_CODE");
			Column_list.add("SETTLEMENT_FLAG");
			Column_list.add("AUTH_CHARA_IND");
			Column_list.add("POS_TERMINAL_CAPABILITY");
			Column_list.add("CARDHOLDER_ID_METHOD");
			Column_list.add("COLLECTION_ONLY_FLAG");
			Column_list.add("POS_ENTRY_MODE");
			Column_list.add("CENTRAL_PROCESS_DATE");
			Column_list.add("REIMBURSEMENT_ATTR");
			Column_list.add("REASON_CODE");
			Column_list.add("TRAN_ID");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			data.add(Column_list);
			final List<String> columns10 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange10, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns10) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 10");

			Column_list = new ArrayList<>();
			Column_list.add("TC");
			Column_list.add("TCR_CODE");
			Column_list.add("CARD_NUMBER");
			Column_list.add("SOURCE_AMOUNT");
			Column_list.add("AUTHORIZATION_CODE");
			Column_list.add("DCRS_SEQ_NO");
			Column_list.add("PART_ID");
			Column_list.add("CREATEDDATE");
			Column_list.add("FLOOR_LIMIT_INDI");
			Column_list.add("ARN");
			Column_list.add("ACQUIRER_BUSI_ID");
			Column_list.add("PURCHASE_DATE");
			Column_list.add("DESTINATION_AMOUNT");
			Column_list.add("DESTINATION_CURR_CODE");
			Column_list.add("SOURCE_CURR_CODE");
			Column_list.add("MERCHANT_NAME");
			Column_list.add("MERCHANT_CITY");
			Column_list.add("MERCHANT_COUNTRY_CODE");
			Column_list.add("MERCHANT_CATEGORY_CODE");
			Column_list.add("MERCHANT_ZIP_CODE");
			Column_list.add("USAGE_CODE");
			Column_list.add("SETTLEMENT_FLAG");
			Column_list.add("AUTH_CHARA_IND");
			Column_list.add("POS_TERMINAL_CAPABILITY");
			Column_list.add("CARDHOLDER_ID_METHOD");
			Column_list.add("COLLECTION_ONLY_FLAG");
			Column_list.add("POS_ENTRY_MODE");
			Column_list.add("CENTRAL_PROCESS_DATE");
			Column_list.add("REIMBURSEMENT_ATTR");
			Column_list.add("REASON_CODE");
			Column_list.add("TRAN_ID");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			data.add(Column_list);
			final List<String> columns11 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange11, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns11) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 11");

			System.out.println("Success ");
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> downloadVISAISSDOMPOSReport(SettlementBean settlementBean) {
		return null;
	}

	public List<Object> downloadVISAISSDOMATMReport(SettlementBean settlementBean) {
		return null;
	}

	public List<Object> downloadVISAACUIRERATMReport(SettlementBean settlementBean) {
		List<Object> data = new ArrayList();
		try {
			String getInterchange1 = "SELECT * FROM settlement_visa_acq_dom_atm_switch WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='SWITCH_KNOCKOFF'";
			String getInterchange2 = "SELECT * FROM settlement_visa_acq_dom_atm_switch WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-DOM-ATM-UNRECON-1'";
			String getInterchange3 = "SELECT * FROM settlement_visa_acq_dom_atm_cbs WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-DOM-ATM-MATCHED-1'";
			String getInterchange4 = "SELECT * FROM settlement_visa_acq_dom_atm_cbs WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-DOM-ATM-UNRECON-1'";
			String getInterchange5 = "SELECT * FROM settlement_visa_acq_dom_atm_cbs WHERE FILEDATE =DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='ACQ-CBS-KNOCKOFF'";
			String getInterchange6 = "SELECT * FROM settlement_visa_acq_dom_atm_cbs WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-DOM-ATM-MATCHED-2'";
			String getInterchange7 = "SELECT * FROM settlement_visa_acq_dom_atm_cbs WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-DOM-ATM-UNRECON-2'";
			String getInterchange8 = "SELECT * FROM settlement_visa_acq_dom_atm_visa WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-DOM-ATM-MATCHED-2'";
			String getInterchange9 = "SELECT * FROM settlement_visa_acq_dom_atm_visa WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-DOM-ATM-UNRECON-2'";
			String getInterchange10 = "SELECT * FROM settlement_visa_acq_dom_atm_visa WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='ORG-WDL-REVERSAL-MATCHED'";
			List<String> Column_list = new ArrayList<>();
			Column_list.add("DATE_TIME");
			Column_list.add("REC_TYPE");
			Column_list.add("AUTH_PPD");
			Column_list.add("TERM_LN");
			Column_list.add("TERM_FIID");
			Column_list.add("TERM_TERM_ID");
			Column_list.add("CRD_LN");
			Column_list.add("CRD_FIID");
			Column_list.add("CRD_PAN");
			Column_list.add("CRD_MBR_NUM");
			Column_list.add("BRCH_ID");
			Column_list.add("REGN_ID");
			Column_list.add("USER_FLD1X");
			Column_list.add("TYP_CDE");
			Column_list.add("TYP");
			Column_list.add("RTE_STAT");
			Column_list.add("ORIGINATOR");
			Column_list.add("RESPONDER");
			Column_list.add("ENTRY_TIME");
			Column_list.add("EXIT_TIME");
			Column_list.add("RE_ENTRY_TIME");
			Column_list.add("TRAN_DATE");
			Column_list.add("TRAN_TIM");
			Column_list.add("POST_DAT");
			Column_list.add("ACQ_ICHG_SETL_DAT");
			Column_list.add("ISS_ICHG_SETL_DAT");
			Column_list.add("SEQ_NUM");
			Column_list.add("TERM_TYP");
			Column_list.add("TIM_OFST");
			Column_list.add("ACQ_INST_ID_NUM");
			Column_list.add("RCV_INST_ID_NUM");
			Column_list.add("TRAN_CDE");
			Column_list.add("FROM_ACCT");
			Column_list.add("USER_FLD1");
			Column_list.add("TO_ACCT");
			Column_list.add("MULT_ACCT");
			Column_list.add("AMT_1");
			Column_list.add("AMT_2");
			Column_list.add("AMT_3");
			Column_list.add("DEP_BAL_CR");
			Column_list.add("DEP_TYP");
			Column_list.add("RESP_CDE");
			Column_list.add("TERM_NAME_LOC");
			Column_list.add("TERM_OWNER_NAME");
			Column_list.add("TERM_CITY");
			Column_list.add("TERM_ST");
			Column_list.add("TERM_CNTRY");
			Column_list.add("ORIG_OSEQ_NUM");
			Column_list.add("ORIG_OTRAN_DAT");
			Column_list.add("ORIG_OTRAN_TIM");
			Column_list.add("ORIG_B24_POST");
			Column_list.add("ORIG_CRNCY_CDE");
			Column_list.add("MULT_CRNCY_AUTH_CRNCY_CDE");
			Column_list.add("MULT_CRNCY_AUTH_CONV_RATE");
			Column_list.add("MULT_CRNCY_SETL_CRNCY_CDE");
			Column_list.add("MULT_CRNCY_SETL_CONV_RATE");
			Column_list.add("MULT_CRNCY_CONV_DAT_TIM");
			Column_list.add("RVSL_RSN");
			Column_list.add("PIN_OFST");
			Column_list.add("SHRG_GRP");
			Column_list.add("DEST_ORDER");
			Column_list.add("AUTH_ID_RESP");
			Column_list.add("REFR_IMP_IND");
			Column_list.add("REFR_AVAIL_IMP");
			Column_list.add("REFR_LEDG_IMP");
			Column_list.add("REFR_HLD_AMT_IMP");
			Column_list.add("REFR_CAF_REFR_IND");
			Column_list.add("REFR_USER_FLD3");
			Column_list.add("DEP_SETL_IMP_FLG");
			Column_list.add("ADJ_SETL_IMP_FLG");
			Column_list.add("REFR_IND");
			Column_list.add("USER_FLD4");
			Column_list.add("FRWD_INST_ID_NUM");
			Column_list.add("CRD_ACCPT_ID_NUM");
			Column_list.add("CRD_ISS_ID_NUM");
			Column_list.add("USER_FLD6");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("MULTI_CRNY_AUTH_CRNCY_CODE");
			Column_list.add("PT_SRV_COND_CODE");
			Column_list.add("APPRV_CDE");
			Column_list.add("REC_TYP");
			data.add(Column_list);
			final List<String> columns = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
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
			System.out.println("query 1");

			Column_list = new ArrayList<>();
			Column_list.add("DATE_TIME");
			Column_list.add("REC_TYPE");
			Column_list.add("AUTH_PPD");
			Column_list.add("TERM_LN");
			Column_list.add("TERM_FIID");
			Column_list.add("TERM_TERM_ID");
			Column_list.add("CRD_LN");
			Column_list.add("CRD_FIID");
			Column_list.add("CRD_PAN");
			Column_list.add("CRD_MBR_NUM");
			Column_list.add("BRCH_ID");
			Column_list.add("REGN_ID");
			Column_list.add("USER_FLD1X");
			Column_list.add("TYP_CDE");
			Column_list.add("TYP");
			Column_list.add("RTE_STAT");
			Column_list.add("ORIGINATOR");
			Column_list.add("RESPONDER");
			Column_list.add("ENTRY_TIME");
			Column_list.add("EXIT_TIME");
			Column_list.add("RE_ENTRY_TIME");
			Column_list.add("TRAN_DATE");
			Column_list.add("TRAN_TIM");
			Column_list.add("POST_DAT");
			Column_list.add("ACQ_ICHG_SETL_DAT");
			Column_list.add("ISS_ICHG_SETL_DAT");
			Column_list.add("SEQ_NUM");
			Column_list.add("TERM_TYP");
			Column_list.add("TIM_OFST");
			Column_list.add("ACQ_INST_ID_NUM");
			Column_list.add("RCV_INST_ID_NUM");
			Column_list.add("TRAN_CDE");
			Column_list.add("FROM_ACCT");
			Column_list.add("USER_FLD1");
			Column_list.add("TO_ACCT");
			Column_list.add("MULT_ACCT");
			Column_list.add("AMT_1");
			Column_list.add("AMT_2");
			Column_list.add("AMT_3");
			Column_list.add("DEP_BAL_CR");
			Column_list.add("DEP_TYP");
			Column_list.add("RESP_CDE");
			Column_list.add("TERM_NAME_LOC");
			Column_list.add("TERM_OWNER_NAME");
			Column_list.add("TERM_CITY");
			Column_list.add("TERM_ST");
			Column_list.add("TERM_CNTRY");
			Column_list.add("ORIG_OSEQ_NUM");
			Column_list.add("ORIG_OTRAN_DAT");
			Column_list.add("ORIG_OTRAN_TIM");
			Column_list.add("ORIG_B24_POST");
			Column_list.add("ORIG_CRNCY_CDE");
			Column_list.add("MULT_CRNCY_AUTH_CRNCY_CDE");
			Column_list.add("MULT_CRNCY_AUTH_CONV_RATE");
			Column_list.add("MULT_CRNCY_SETL_CRNCY_CDE");
			Column_list.add("MULT_CRNCY_SETL_CONV_RATE");
			Column_list.add("MULT_CRNCY_CONV_DAT_TIM");
			Column_list.add("RVSL_RSN");
			Column_list.add("PIN_OFST");
			Column_list.add("SHRG_GRP");
			Column_list.add("DEST_ORDER");
			Column_list.add("AUTH_ID_RESP");
			Column_list.add("REFR_IMP_IND");
			Column_list.add("REFR_AVAIL_IMP");
			Column_list.add("REFR_LEDG_IMP");
			Column_list.add("REFR_HLD_AMT_IMP");
			Column_list.add("REFR_CAF_REFR_IND");
			Column_list.add("REFR_USER_FLD3");
			Column_list.add("DEP_SETL_IMP_FLG");
			Column_list.add("ADJ_SETL_IMP_FLG");
			Column_list.add("REFR_IND");
			Column_list.add("USER_FLD4");
			Column_list.add("FRWD_INST_ID_NUM");
			Column_list.add("CRD_ACCPT_ID_NUM");
			Column_list.add("CRD_ISS_ID_NUM");
			Column_list.add("USER_FLD6");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("MULTI_CRNY_AUTH_CRNCY_CODE");
			Column_list.add("PT_SRV_COND_CODE");
			Column_list.add("APPRV_CDE");
			Column_list.add("REC_TYP");
			data.add(Column_list);
			final List<String> columns2 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns2) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 2");

			Column_list = new ArrayList<>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("TMP_ID");
			Column_list.add("AUTH_CODE");
			data.add(Column_list);
			final List<String> columns3 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange3, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns3) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 3");

			Column_list = new ArrayList<>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("TMP_ID");
			Column_list.add("AUTH_CODE");
			data.add(Column_list);
			final List<String> columns4 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange4, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns4) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 4");

			Column_list = new ArrayList<>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("TMP_ID");
			Column_list.add("AUTH_CODE");
			data.add(Column_list);
			final List<String> columns5 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange5, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns5) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 5");

			Column_list = new ArrayList<>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("TMP_ID");
			Column_list.add("AUTH_CODE");
			data.add(Column_list);
			final List<String> columns6 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange6, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns6) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 6");

			Column_list = new ArrayList<>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			Column_list.add("TMP_ID");
			Column_list.add("AUTH_CODE");
			data.add(Column_list);
			final List<String> columns7 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange7, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns7) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 7");

			Column_list = new ArrayList<>();
			Column_list.add("TC");
			Column_list.add("TCR_CODE");
			Column_list.add("CARD_NUMBER");
			Column_list.add("SOURCE_AMOUNT");
			Column_list.add("AUTHORIZATION_CODE");
			Column_list.add("DCRS_SEQ_NO");
			Column_list.add("TRACE");
			Column_list.add("REFERENCE_NUMBER");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PART_ID");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("FILEDATE");
			Column_list.add("REQ_MSGTYPE");
			Column_list.add("FLOOR_LIMIT_INDI");
			Column_list.add("ARN");
			Column_list.add("ACQUIRER_BUSI_ID");
			Column_list.add("PURCHASE_DATE");
			Column_list.add("DESTINATION_AMOUNT");
			Column_list.add("DESTINATION_CURR_CODE");
			Column_list.add("SOURCE_CURR_CODE");
			Column_list.add("MERCHANT_NAME");
			Column_list.add("MERCHANT_CITY");
			Column_list.add("MERCHANT_COUNTRY_CODE");
			Column_list.add("MERCHANT_CATEGORY_CODE");
			Column_list.add("MERCHANT_ZIP_CODE");
			Column_list.add("USAGE_CODE");
			Column_list.add("SETTLEMENT_FLAG");
			Column_list.add("AUTH_CHARA_IND");
			Column_list.add("POS_TERMINAL_CAPABILITY");
			Column_list.add("CARDHOLDER_ID_METHOD");
			Column_list.add("COLLECTION_ONLY_FLAG");
			Column_list.add("POS_ENTRY_MODE");
			Column_list.add("CENTRAL_PROCESS_DATE");
			Column_list.add("REIMBURSEMENT_ATTR");
			Column_list.add("DESTINATION_BIN");
			Column_list.add("SOURCE_BIN");
			Column_list.add("REASON_CODE");
			Column_list.add("COUNTRY_CODE");
			Column_list.add("EVENT_DATE");
			Column_list.add("MESSAGE_TEXT");
			Column_list.add("TRANSAC_IDENTIFIER");
			Column_list.add("TRAN_ID");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("ORG_FILEDATE");
			data.add(Column_list);
			final List<String> columns8 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange8, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns8) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 8");

			Column_list = new ArrayList<>();
			Column_list.add("TC");
			Column_list.add("TCR_CODE");
			Column_list.add("CARD_NUMBER");
			Column_list.add("SOURCE_AMOUNT");
			Column_list.add("AUTHORIZATION_CODE");
			Column_list.add("DCRS_SEQ_NO");
			Column_list.add("TRACE");
			Column_list.add("REFERENCE_NUMBER");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PART_ID");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("FILEDATE");
			Column_list.add("REQ_MSGTYPE");
			Column_list.add("FLOOR_LIMIT_INDI");
			Column_list.add("ARN");
			Column_list.add("ACQUIRER_BUSI_ID");
			Column_list.add("PURCHASE_DATE");
			Column_list.add("DESTINATION_AMOUNT");
			Column_list.add("DESTINATION_CURR_CODE");
			Column_list.add("SOURCE_CURR_CODE");
			Column_list.add("MERCHANT_NAME");
			Column_list.add("MERCHANT_CITY");
			Column_list.add("MERCHANT_COUNTRY_CODE");
			Column_list.add("MERCHANT_CATEGORY_CODE");
			Column_list.add("MERCHANT_ZIP_CODE");
			Column_list.add("USAGE_CODE");
			Column_list.add("SETTLEMENT_FLAG");
			Column_list.add("AUTH_CHARA_IND");
			Column_list.add("POS_TERMINAL_CAPABILITY");
			Column_list.add("CARDHOLDER_ID_METHOD");
			Column_list.add("COLLECTION_ONLY_FLAG");
			Column_list.add("POS_ENTRY_MODE");
			Column_list.add("CENTRAL_PROCESS_DATE");
			Column_list.add("REIMBURSEMENT_ATTR");
			Column_list.add("DESTINATION_BIN");
			Column_list.add("SOURCE_BIN");
			Column_list.add("REASON_CODE");
			Column_list.add("COUNTRY_CODE");
			Column_list.add("EVENT_DATE");
			Column_list.add("MESSAGE_TEXT");
			Column_list.add("TRANSAC_IDENTIFIER");
			Column_list.add("TRAN_ID");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("ORG_FILEDATE");
			data.add(Column_list);
			final List<String> columns9 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange9, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns9) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 9");

			Column_list = new ArrayList<>();
			Column_list.add("TC");
			Column_list.add("TCR_CODE");
			Column_list.add("CARD_NUMBER");
			Column_list.add("SOURCE_AMOUNT");
			Column_list.add("AUTHORIZATION_CODE");
			Column_list.add("DCRS_SEQ_NO");
			Column_list.add("TRACE");
			Column_list.add("REFERENCE_NUMBER");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PART_ID");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("FILEDATE");
			Column_list.add("REQ_MSGTYPE");
			Column_list.add("FLOOR_LIMIT_INDI");
			Column_list.add("ARN");
			Column_list.add("ACQUIRER_BUSI_ID");
			Column_list.add("PURCHASE_DATE");
			Column_list.add("DESTINATION_AMOUNT");
			Column_list.add("DESTINATION_CURR_CODE");
			Column_list.add("SOURCE_CURR_CODE");
			Column_list.add("MERCHANT_NAME");
			Column_list.add("MERCHANT_CITY");
			Column_list.add("MERCHANT_COUNTRY_CODE");
			Column_list.add("MERCHANT_CATEGORY_CODE");
			Column_list.add("MERCHANT_ZIP_CODE");
			Column_list.add("USAGE_CODE");
			Column_list.add("SETTLEMENT_FLAG");
			Column_list.add("AUTH_CHARA_IND");
			Column_list.add("POS_TERMINAL_CAPABILITY");
			Column_list.add("CARDHOLDER_ID_METHOD");
			Column_list.add("COLLECTION_ONLY_FLAG");
			Column_list.add("POS_ENTRY_MODE");
			Column_list.add("CENTRAL_PROCESS_DATE");
			Column_list.add("REIMBURSEMENT_ATTR");
			Column_list.add("DESTINATION_BIN");
			Column_list.add("SOURCE_BIN");
			Column_list.add("REASON_CODE");
			Column_list.add("COUNTRY_CODE");
			Column_list.add("EVENT_DATE");
			Column_list.add("MESSAGE_TEXT");
			Column_list.add("TRANSAC_IDENTIFIER");
			Column_list.add("TRAN_ID");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("ORG_FILEDATE");
			data.add(Column_list);
			final List<String> columns10 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange10, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns10) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 10");

			System.out.println("Success ");
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> downloadVISAACUIRERATMINTReport(SettlementBean settlementBean) {
		List<Object> data = new ArrayList();
		try {
			String getInterchange1 = "SELECT * FROM settlement_visa_acq_int_atm_switch WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='SWITCH_KNOCKOFF'";
			String getInterchange2 = "SELECT * FROM settlement_visa_acq_int_atm_switch WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-DOM-ATM-UNRECON-1'";
			String getInterchange3 = "SELECT * FROM settlement_visa_acq_int_atm_cbs WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-INT-ATM-MATCHED-1'";
			String getInterchange4 = "SELECT * FROM settlement_visa_acq_int_atm_cbs WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-INT-ATM-UNRECON-1'";
			String getInterchange5 = "SELECT * FROM settlement_visa_acq_int_atm_cbs WHERE FILEDATE= DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='ACQ-INT-ATM-CBS-KNOCKOFF'";
			String getInterchange6 = "SELECT * FROM settlement_visa_acq_int_atm_cbs WHERE FILEDATE =DATE_FORMAT('"
					+ settlementBean.getDatepicker()
					+ "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-INT-ATM-MATCHED-2-CAA'";
			String getInterchange7 = "SELECT * FROM settlement_visa_acq_int_atm_cbs WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-INT-ATM-UNRECON-2'";
			String getInterchange8 = "SELECT * FROM settlement_visa_acq_int_atm_visa WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker()
					+ "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-INT-ATM-MATCHED-2-CAA'";
			String getInterchange9 = "SELECT * FROM settlement_visa_acq_int_atm_visa WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='VISA-ACQ-INT-ATM-UNRECON-2'";
			String getInterchange10 = "SELECT * FROM settlement_visa_acq_int_atm_visa WHERE FILEDATE=DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='ORG-WDL-REVERSAL-MATCHED'";
			List<String> Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_visa_acq_int_atm_switch");
			data.add(Column_list);
			final List<String> columns = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
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
			System.out.println("query 1");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_visa_acq_int_atm_switch");
			data.add(Column_list);
			final List<String> columns2 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns2) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 2");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_visa_acq_int_atm_cbs");
			data.add(Column_list);
			final List<String> columns3 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange3, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns3) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 3");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_visa_acq_int_atm_cbs");
			data.add(Column_list);
			final List<String> columns4 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange4, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns4) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 4");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_visa_acq_int_atm_cbs");
			data.add(Column_list);
			final List<String> columns5 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange5, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns5) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 5");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_visa_acq_int_atm_cbs");
			data.add(Column_list);
			final List<String> columns6 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange6, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns6) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 6");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_visa_acq_int_atm_cbs");
			data.add(Column_list);
			final List<String> columns7 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange7, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns7) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 7");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_visa_acq_int_atm_visa");
			data.add(Column_list);
			final List<String> columns8 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange8, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns8) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 8");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_visa_acq_int_atm_visa");
			data.add(Column_list);
			final List<String> columns9 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange9, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns9) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 9");

			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_visa_acq_int_atm_visa");
			data.add(Column_list);
			final List<String> columns10 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange10, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns10) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 10");

			System.out.println("Success ");
			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> downloadJCBACQReport(SettlementBean settlementBean) {
		List<Object> data = new ArrayList<Object>();
		try {
			String getInterchange2 = "SELECT * FROM settlement_jcb_cbs WHERE FILEDATE = STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='JCB-MATCHED-2'";
			String getInterchange3 = "SELECT * FROM settlement_jcb_cbs WHERE FILEDATE = STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='JCB-UNRECON-2'";
			String getInterchange4 = "SELECT *FROM settlement_jcb_jcb WHERE FILEDATE = STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='JCB-MATCHED-2'";
			String getInterchange5 = "SELECT *FROM settlement_jcb_jcb WHERE FILEDATE = STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='JCB-UNRECON-2'";
			List<String> Column_list = new ArrayList<String>();
			Column_list = new ArrayList<String>();

			Column_list = getColumnList("settlement_jcb_cbs");
			data.add(Column_list);
			final List<Object> beanList = new ArrayList<Object>();
			final List<String> columns2 = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns2) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 2");

			Column_list = new ArrayList<String>();

			Column_list = getColumnList("settlement_jcb_cbs");
			data.add(Column_list);
			final List<String> columns3 = Column_list;
			final List<Object> beanList3 = new ArrayList<Object>();
			List<Object> DailyData3 = getJdbcTemplate().query(getInterchange3, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns3) {
									data.put(column, rs.getString(column));
								}
								beanList3.add(data);
							}
							return beanList3;
						}
					});
			data.add(DailyData3);
			System.gc();
			System.out.println("query 3");
			Column_list = new ArrayList<String>();
			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_jcb_jcb");
			data.add(Column_list);
			final List<String> columns4 = Column_list;
			final List<Object> beanList4 = new ArrayList<Object>();
			List<Object> DailyData4 = getJdbcTemplate().query(getInterchange4, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns4) {
									data.put(column, rs.getString(column));
								}
								beanList4.add(data);
							}
							return beanList4;
						}
					});
			data.add(DailyData4);
			System.out.println("query 4");

			System.gc();
			Column_list = new ArrayList<String>();
			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_jcb_jcb");
			data.add(Column_list);
			final List<String> columns5 = Column_list;
			final List<Object> beanList5 = new ArrayList<Object>();
			List<Object> DailyData5 = getJdbcTemplate().query(getInterchange5, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns5) {
									data.put(column, rs.getString(column));
								}
								beanList5.add(data);
							}
							return beanList5;
						}
					});
			data.add(DailyData5);

			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}

	}

	public List<Object> downloadDFSACQReport(SettlementBean settlementBean) {
		List<Object> data = new ArrayList<Object>();
		try {
			String getInterchange2 = "SELECT * FROM settlement_dfs_cbs WHERE FILEDATE = STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS = 'DFS-MATCHED-2'";
			String getInterchange3 = "SELECT * FROM settlement_dfs_cbs WHERE FILEDATE = STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS = 'DFS-UNRECON-2'";
			String getInterchange4 = "SELECT * FROM settlement_dfs_dfs WHERE FILEDATE = STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS = 'DFS-MATCHED-2'";
			String getInterchange5 = "SELECT * FROM settlement_dfs_dfs WHERE FILEDATE = STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS = 'DFS-UNRECON-2'";
			List<String> Column_list = new ArrayList<String>();
			Column_list = new ArrayList<String>();
			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_dfs_cbs");
			data.add(Column_list);
			final List<Object> beanList = new ArrayList<Object>();
			final List<String> columns2 = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns2) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 2");

			Column_list = new ArrayList<String>();
			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_dfs_cbs");
			data.add(Column_list);
			final List<String> columns3 = Column_list;
			final List<Object> beanList3 = new ArrayList<Object>();
			List<Object> DailyData3 = getJdbcTemplate().query(getInterchange3, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns3) {
									data.put(column, rs.getString(column));
								}
								beanList3.add(data);
							}
							return beanList3;
						}
					});
			data.add(DailyData3);
			System.gc();
			System.out.println("query 3");
			Column_list = new ArrayList<String>();
			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_dfs_dfs");
			data.add(Column_list);
			final List<String> columns4 = Column_list;
			final List<Object> beanList4 = new ArrayList<Object>();
			List<Object> DailyData4 = getJdbcTemplate().query(getInterchange4, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns4) {
									data.put(column, rs.getString(column));
								}
								beanList4.add(data);
							}
							return beanList4;
						}
					});
			data.add(DailyData4);
			System.out.println("query 4");

			System.gc();
			Column_list = new ArrayList<String>();
			Column_list = new ArrayList<>();
			Column_list = getColumnList("settlement_dfs_dfs");
			data.add(Column_list);
			final List<String> columns5 = Column_list;
			final List<Object> beanList5 = new ArrayList<Object>();
			List<Object> DailyData5 = getJdbcTemplate().query(getInterchange5, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns5) {
									data.put(column, rs.getString(column));
								}
								beanList5.add(data);
							}
							return beanList5;
						}
					});
			data.add(DailyData5);

			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}

	}

	@Override
	public List<Object> downloadIssuerDhanaReport(SettlementBean settlementBean) {
		List<Object> data = new ArrayList<Object>();
		try {
			String getInterchange2 = "SELECT CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, BR_CODE, ISS_SOL_ID, REMARKS, NETWORK, DCRS_REMARKS, FILEDATE, FILENAME\r\nFROM settlement_nfs_iss_cbs WHERE FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='NFS-ISS-CBS-MATCHED-1'";
			String getInterchange3 = "SELECT CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, BR_CODE, ISS_SOL_ID, REMARKS, NETWORK, DCRS_REMARKS, FILEDATE, FILENAME\r\nFROM settlement_nfs_iss_cbs WHERE FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='NFS-ISS-CBS-UNRECON-1'\r\n"
					+ "AND DATE_FORMAT(STR_TO_DATE(TRAN_DATE, '%d-%m-%Y'), '%Y-%m-%d') > DATE_ADD(STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d'), INTERVAL-6 DAY)";
			String getInterchange4 = "SELECT CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, BR_CODE, ISS_SOL_ID, REMARKS, NETWORK, DCRS_REMARKS, FILEDATE, FILENAME\r\nFROM settlement_nfs_iss_cbs WHERE FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS LIKE 'NFS-ISS-CBS-MATCHED-2%'";
			String getInterchange5 = "SELECT CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, BR_CODE, ISS_SOL_ID, REMARKS, NETWORK, DCRS_REMARKS, FILEDATE, FILENAME\r\nFROM settlement_nfs_iss_cbs WHERE FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='NFS-ISS-CBS-UNRECON-2'\r\n"
					+ "AND DATE_FORMAT(STR_TO_DATE(TRAN_DATE, '%d-%m-%Y'), '%Y-%m-%d') > DATE_ADD(STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d'), INTERVAL-6 DAY) ";
			String getInterchange7 = "SELECT PARTICIPANT_ID, TRANSACTION_TYPE, FROM_ACCOUNT_TYPE, TO_ACCOUNT_TYPE, TXN_SERIAL_NO, RESPONSE_CODE, PAN_NUMBER, MEMBER_NUMBER, APPROVAL_NUMBER, SYS_TRACE_AUDIT_NO,\r\nTRANSACTION_DATE, TRANSACTION_TIME, MERCHANT_CATEGORY_CD, CARD_ACC_SETTLE_DT, CARD_ACC_ID, CARD_ACC_TERMINAL_ID, CARD_ACC_TERMINAL_LOC, ACQUIRER_ID, NETWORK_ID,\r\nACCOUNT_1_NUMBER, TXN_CURRENCY_CODE, TXN_AMOUNT, ACTUAL_TXN_AMT, TXN_ACTIVITY_FEE, ISS_SETTLE_CURRENCY_CD, ISS_SETTLE_AMNT, ISS_SETTLE_FEE, ISS_SETTLE_PROCESS_FEE,\r\nPART_ID, FPAN, CYCLE, FILEDATE, DCRS_REMARKS, FILENAME FROM settlement_nfs_iss_nfs WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker()
					+ "','%Y/%m/%d') AND DCRS_REMARKS LIKE 'NFS-ISS-MATCHED-2%' AND DATE_FORMAT(STR_TO_DATE(TRANSACTION_DATE, '%y%m%d'), '%Y-%m-%d')> DATE_ADD(STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d'), INTERVAL-6 DAY)";
			String getInterchange8 = "SELECT PARTICIPANT_ID, TRANSACTION_TYPE, FROM_ACCOUNT_TYPE, TO_ACCOUNT_TYPE, TXN_SERIAL_NO, RESPONSE_CODE, PAN_NUMBER, MEMBER_NUMBER, APPROVAL_NUMBER, SYS_TRACE_AUDIT_NO,\r\nTRANSACTION_DATE, TRANSACTION_TIME, MERCHANT_CATEGORY_CD, CARD_ACC_SETTLE_DT, CARD_ACC_ID, CARD_ACC_TERMINAL_ID, CARD_ACC_TERMINAL_LOC, ACQUIRER_ID, NETWORK_ID,\r\nACCOUNT_1_NUMBER, TXN_CURRENCY_CODE, TXN_AMOUNT, ACTUAL_TXN_AMT, TXN_ACTIVITY_FEE, ISS_SETTLE_CURRENCY_CD, ISS_SETTLE_AMNT, ISS_SETTLE_FEE, ISS_SETTLE_PROCESS_FEE,\r\nPART_ID, FPAN, CYCLE, FILEDATE, DCRS_REMARKS, FILENAME\r\nFROM settlement_nfs_iss_nfs WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker()
					+ "','%Y/%m/%d') AND DCRS_REMARKS='NFS-ISS-UNRECON-2' AND DATE_FORMAT(STR_TO_DATE(TRANSACTION_DATE, '%y%m%d'), '%Y-%m-%d')> DATE_ADD(STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d'), INTERVAL-6 DAY)";
			String getInterchange9 = "SELECT PARTICIPANT_ID, TRANSACTION_TYPE, FROM_ACCOUNT_TYPE, TO_ACCOUNT_TYPE, TXN_SERIAL_NO, RESPONSE_CODE, PAN_NUMBER, MEMBER_NUMBER, APPROVAL_NUMBER, SYS_TRACE_AUDIT_NO,\r\nTRANSACTION_DATE, TRANSACTION_TIME, MERCHANT_CATEGORY_CD, CARD_ACC_SETTLE_DT, CARD_ACC_ID, CARD_ACC_TERMINAL_ID, CARD_ACC_TERMINAL_LOC, ACQUIRER_ID, NETWORK_ID,\r\nACCOUNT_1_NUMBER, TXN_CURRENCY_CODE, TXN_AMOUNT, ACTUAL_TXN_AMT, TXN_ACTIVITY_FEE, ISS_SETTLE_CURRENCY_CD, ISS_SETTLE_AMNT, ISS_SETTLE_FEE, ISS_SETTLE_PROCESS_FEE,\r\nPART_ID, FPAN, CYCLE, FILEDATE, DCRS_REMARKS, FILENAME\r\nFROM settlement_nfs_iss_nfs WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='LATE-REVERSAL-MATCHED'";
			String getInterchange10 = "SELECT TRANSTYPE, RESP_CODE, CARDNO, RRN, STANNO, ACQ, ISS, TRASN_DATE, TRANS_TIME, ATMID, SETTLEDATE, REQUESTAMT, RECEIVEDAMT, STATUS,\r\nCYCLE, FPAN, CREATEDDATE, DCRS_REMARKS, FILEDATE FROM settlement_nfs_iss_rev_report WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='MATCHED'";
			String getInterchange11 = "SELECT TRANSTYPE, RESP_CODE, CARDNO, RRN, STANNO, ACQ, ISS, TRASN_DATE, TRANS_TIME, ATMID, SETTLEDATE, REQUESTAMT, RECEIVEDAMT, STATUS,\r\nCYCLE, FPAN, CREATEDDATE, DCRS_REMARKS, FILEDATE FROM settlement_nfs_iss_rev_report WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='UNMATCHED'";
			List<String> Column_list = new ArrayList<String>();
			Column_list = new ArrayList<String>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<Object> beanList = new ArrayList<Object>();
			final List<String> columns2 = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns2) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 2");

			Column_list = new ArrayList<String>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns3 = Column_list;
			final List<Object> beanList3 = new ArrayList<Object>();
			List<Object> DailyData3 = getJdbcTemplate().query(getInterchange3, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns3) {
									data.put(column, rs.getString(column));
								}
								beanList3.add(data);
							}
							return beanList3;
						}
					});
			data.add(DailyData3);
			System.gc();
			System.out.println("query 3");
			Column_list = new ArrayList<String>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns4 = Column_list;
			final List<Object> beanList4 = new ArrayList<Object>();
			List<Object> DailyData4 = getJdbcTemplate().query(getInterchange4, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns4) {
									data.put(column, rs.getString(column));
								}
								beanList4.add(data);
							}
							return beanList4;
						}
					});
			data.add(DailyData4);
			System.out.println("query 4");

			System.gc();
			Column_list = new ArrayList<String>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns5 = Column_list;
			final List<Object> beanList5 = new ArrayList<Object>();
			List<Object> DailyData5 = getJdbcTemplate().query(getInterchange5, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns5) {
									data.put(column, rs.getString(column));
								}
								beanList5.add(data);
							}
							return beanList5;
						}
					});
			data.add(DailyData5);
			System.out.println("query 5");

			System.gc();
			Column_list = new ArrayList<String>();
			Column_list.add("PARTICIPANT_ID");
			Column_list.add("TRANSACTION_TYPE");
			Column_list.add("FROM_ACCOUNT_TYPE");
			Column_list.add("TO_ACCOUNT_TYPE");
			Column_list.add("TXN_SERIAL_NO");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PAN_NUMBER");
			Column_list.add("MEMBER_NUMBER");
			Column_list.add("APPROVAL_NUMBER");
			Column_list.add("SYS_TRACE_AUDIT_NO");
			Column_list.add("TRANSACTION_DATE");
			Column_list.add("TRANSACTION_TIME");
			Column_list.add("MERCHANT_CATEGORY_CD");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("CARD_ACC_ID");
			Column_list.add("CARD_ACC_TERMINAL_ID");
			Column_list.add("CARD_ACC_TERMINAL_LOC");
			Column_list.add("ACQUIRER_ID");
			Column_list.add("NETWORK_ID");
			Column_list.add("ACCOUNT_1_NUMBER");
			Column_list.add("TXN_CURRENCY_CODE");
			Column_list.add("TXN_AMOUNT");
			Column_list.add("ACTUAL_TXN_AMT");
			Column_list.add("TXN_ACTIVITY_FEE");
			Column_list.add("ISS_SETTLE_CURRENCY_CD");
			Column_list.add("ISS_SETTLE_AMNT");
			Column_list.add("ISS_SETTLE_FEE");
			Column_list.add("ISS_SETTLE_PROCESS_FEE");
			Column_list.add("PART_ID");
			Column_list.add("FPAN");
			Column_list.add("CYCLE");
			Column_list.add("FILEDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns7 = Column_list;
			final List<Object> beanList7 = new ArrayList<Object>();
			List<Object> DailyData7 = getJdbcTemplate().query(getInterchange7, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns7) {
									data.put(column, rs.getString(column));
								}
								beanList7.add(data);
							}
							return beanList7;
						}
					});
			data.add(DailyData7);
			System.out.println("query 7");

			Column_list = new ArrayList<String>();
			Column_list.add("PARTICIPANT_ID");
			Column_list.add("TRANSACTION_TYPE");
			Column_list.add("FROM_ACCOUNT_TYPE");
			Column_list.add("TO_ACCOUNT_TYPE");
			Column_list.add("TXN_SERIAL_NO");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PAN_NUMBER");
			Column_list.add("MEMBER_NUMBER");
			Column_list.add("APPROVAL_NUMBER");
			Column_list.add("SYS_TRACE_AUDIT_NO");
			Column_list.add("TRANSACTION_DATE");
			Column_list.add("TRANSACTION_TIME");
			Column_list.add("MERCHANT_CATEGORY_CD");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("CARD_ACC_ID");
			Column_list.add("CARD_ACC_TERMINAL_ID");
			Column_list.add("CARD_ACC_TERMINAL_LOC");
			Column_list.add("ACQUIRER_ID");
			Column_list.add("NETWORK_ID");
			Column_list.add("ACCOUNT_1_NUMBER");
			Column_list.add("TXN_CURRENCY_CODE");
			Column_list.add("TXN_AMOUNT");
			Column_list.add("ACTUAL_TXN_AMT");
			Column_list.add("TXN_ACTIVITY_FEE");
			Column_list.add("ISS_SETTLE_CURRENCY_CD");
			Column_list.add("ISS_SETTLE_AMNT");
			Column_list.add("ISS_SETTLE_FEE");
			Column_list.add("ISS_SETTLE_PROCESS_FEE");
			Column_list.add("PART_ID");
			Column_list.add("FPAN");
			Column_list.add("CYCLE");
			Column_list.add("FILEDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns8 = Column_list;
			final List<Object> beanList8 = new ArrayList<Object>();
			List<Object> DailyData8 = getJdbcTemplate().query(getInterchange8, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns8) {
									data.put(column, rs.getString(column));
								}
								beanList8.add(data);
							}
							return beanList8;
						}
					});
			data.add(DailyData8);
			System.out.println("query 8");

			System.gc();
			Column_list = new ArrayList<String>();
			Column_list.add("PARTICIPANT_ID");
			Column_list.add("TRANSACTION_TYPE");
			Column_list.add("FROM_ACCOUNT_TYPE");
			Column_list.add("TO_ACCOUNT_TYPE");
			Column_list.add("TXN_SERIAL_NO");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PAN_NUMBER");
			Column_list.add("MEMBER_NUMBER");
			Column_list.add("APPROVAL_NUMBER");
			Column_list.add("SYS_TRACE_AUDIT_NO");
			Column_list.add("TRANSACTION_DATE");
			Column_list.add("TRANSACTION_TIME");
			Column_list.add("MERCHANT_CATEGORY_CD");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("CARD_ACC_ID");
			Column_list.add("CARD_ACC_TERMINAL_ID");
			Column_list.add("CARD_ACC_TERMINAL_LOC");
			Column_list.add("ACQUIRER_ID");
			Column_list.add("NETWORK_ID");
			Column_list.add("ACCOUNT_1_NUMBER");
			Column_list.add("TXN_CURRENCY_CODE");
			Column_list.add("TXN_AMOUNT");
			Column_list.add("ACTUAL_TXN_AMT");
			Column_list.add("TXN_ACTIVITY_FEE");
			Column_list.add("ISS_SETTLE_CURRENCY_CD");
			Column_list.add("ISS_SETTLE_AMNT");
			Column_list.add("ISS_SETTLE_FEE");
			Column_list.add("ISS_SETTLE_PROCESS_FEE");
			Column_list.add("PART_ID");
			Column_list.add("FPAN");
			Column_list.add("CYCLE");
			Column_list.add("FILEDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns9 = Column_list;
			final List<Object> beanList9 = new ArrayList<Object>();
			List<Object> DailyData9 = getJdbcTemplate().query(getInterchange9, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns9) {
									data.put(column, rs.getString(column));
								}
								beanList9.add(data);
							}
							return beanList9;
						}
					});
			data.add(DailyData9);
			System.out.println("query 9");

			Column_list = new ArrayList<String>();
			Column_list.add("TRANSTYPE");
			Column_list.add("RESP_CODE");
			Column_list.add("CARDNO");
			Column_list.add("RRN");
			Column_list.add("STANNO");
			Column_list.add("ACQ");
			Column_list.add("ISS");
			Column_list.add("TRASN_DATE");
			Column_list.add("TRANS_TIME");
			Column_list.add("ATMID");
			Column_list.add("SETTLEDATE");
			Column_list.add("REQUESTAMT");
			Column_list.add("RECEIVEDAMT");
			Column_list.add("STATUS");
			Column_list.add("CYCLE");
			Column_list.add("FPAN");
			Column_list.add("CREATEDDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			data.add(Column_list);
			final List<String> columns10 = Column_list;
			final List<Object> beanList10 = new ArrayList<Object>();
			List<Object> DailyData10 = getJdbcTemplate().query(getInterchange10, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns10) {
									data.put(column, rs.getString(column));
								}
								beanList10.add(data);
							}
							return beanList10;
						}
					});
			data.add(DailyData10);
			System.out.println("query 10");

			Column_list = new ArrayList<String>();
			Column_list.add("TRANSTYPE");
			Column_list.add("RESP_CODE");
			Column_list.add("CARDNO");
			Column_list.add("RRN");
			Column_list.add("STANNO");
			Column_list.add("ACQ");
			Column_list.add("ISS");
			Column_list.add("TRASN_DATE");
			Column_list.add("TRANS_TIME");
			Column_list.add("ATMID");
			Column_list.add("SETTLEDATE");
			Column_list.add("REQUESTAMT");
			Column_list.add("RECEIVEDAMT");
			Column_list.add("STATUS");
			Column_list.add("CYCLE");
			Column_list.add("FPAN");
			Column_list.add("CREATEDDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			data.add(Column_list);
			final List<String> columns11 = Column_list;
			final List<Object> beanList11 = new ArrayList<Object>();
			List<Object> DailyData11 = getJdbcTemplate().query(getInterchange11, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {

							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns11) {
									data.put(column, rs.getString(column));
								}
								beanList11.add(data);
							}
							return beanList11;
						}
					});
			data.add(DailyData11);
			System.out.println("query 11");

			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> downloadIssuerDhanaReport2(SettlementBean settlementBean) {
		return null;
	}

	@Override
	public List<ViewFiles> searchViewFile1(String type, String fromDate, String ModuleType)
			throws Exception, SQLException {
		List<ViewFiles> bean = new ArrayList<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			UnmatchedTTUMProc1 rollBackexe = new UnmatchedTTUMProc1(getJdbcTemplate());
			inParams.put("I_FILEDATE", fromDate);
			inParams.put("ENTERED_CYCLE", type);
			inParams.put("MODULE_TYPE", ModuleType);
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.toString());
			System.out.println("outParams h" + outParams.entrySet());
			String data = null;
			for (Map.Entry<String, Object> entry : outParams.entrySet()) {
				data = entry.getValue().toString();
				break;
			}
			System.out.println("data " + data.replaceAll("NTSL_FILENAME", "").replaceAll("NTSL_TXN_COUNT", "").replaceAll("NTSL_TXN_AMOUNT", "").replaceAll("RAW_FILENAME", "").replaceAll("RAW_TXT_COUNT", "").replaceAll("RAW_TXT_AMOUNT", "").replaceAll("DIFF_COUNT", "").replaceAll("DIFF_AMOUNT", ""));
			data =  data.replaceAll("NTSL_FILENAME", "").replaceAll("NTSL_TXN_COUNT", "").replaceAll("NTSL_TXN_AMOUNT", "").replaceAll("RAW_FILENAME", "").replaceAll("RAW_TXN_COUNT", "").replaceAll("RAW_TXN_AMOUNT", "").replaceAll("DIFF_COUNT", "").replaceAll("DIFF_AMOUNT", "").replace(",", "").replaceAll("[}\\]]", "");
			String[] datas = data.split("=");

			for (int i = 0; i < datas.length; i++) {
				System.out.println(i + "--" + datas[i]);
			}
			ViewFiles e = new ViewFiles();
			e.setDIFF_AMOUNT(datas[8]);
			e.setDIFF_COUNT(datas[7]);
			e.setNTSL_FILENAME(datas[1]);
			e.setNTSL_TXN_AMOUNT(datas[3]);
			e.setNTSL_TXN_COUNT(datas[2]);
			e.setRAW_FILENAME(datas[4]);
			e.setRAW_TXN_AMOUNT(datas[6]);
			e.setRAW_TXN_COUNT(datas[5]);
			bean.add(e);
//			System.out.println("datas[1]--"+type);
//			System.out.println("datas[2]--"+datas[2].substring(0, 3));
//			System.out.println("datas[3]--"+datas[3].substring(0, datas[3].length()-8));
//			System.out.println("datas[4]--"+datas[4].substring(0, datas[4].length()-2));

			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return bean;
			}
//			System.out.println("inside this");
			return bean;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return bean;
		}

	}

	private class UnmatchedTTUMProc1 extends StoredProcedure {
		private static final String insert_proc = "DEBITCARDS_NTSL_RAW_TXN_COUNT_AMOUNT_MATCHING";

		public UnmatchedTTUMProc1(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("ENTERED_CYCLE", Types.VARCHAR));
			declareParameter(new SqlParameter("MODULE_TYPE", Types.VARCHAR));
			compile();
		}

	}

	public List<SearchData> searchData(String category, String rrn_no, String fromDate) throws Exception, SQLException {
		return null;
	}

	@Override
	public List<ViewFiles> searchViewFile(String type, String fromDate) throws Exception, SQLException {
		List<ViewFiles> bean = new ArrayList<>();
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			UnmatchedTTUMProc1s rollBackexe = new UnmatchedTTUMProc1s(getJdbcTemplate());
			inParams.put("FILEDT", fromDate);
			inParams.put("I_FILETYPE", type);
			outParams = rollBackexe.execute(inParams);
			String data = null;
			for (Map.Entry<String, Object> entry : outParams.entrySet()) {
				data = entry.getValue().toString();
				break;
			}
			System.out.println("data "+ data);
			data= data.replaceAll("FILE_TYPE", "").replaceAll("FILE_COUNT", "").replaceAll("FILES_UPLOADED", "").replaceAll("REMARK", "").replaceAll(",","");
			
			String[] datas = data.split("=");
			
		for(int i=0;i<datas.length;i++) {
		System.out.println(i+"--"+datas[i]);
		}
			ViewFiles e = new ViewFiles();
			e.setFiletype(datas[1]);
			e.setFilecount(datas[2]);
			e.setFilename(datas[3]);
			e.setRemark(datas[4].substring(0,17));
			bean.add(e);
//			System.out.println("datas[1]--"+type);
//			System.out.println("datas[2]--"+datas[2].substring(0, 3));
//			System.out.println("datas[3]--"+datas[3].substring(0, datas[3].length()-8));
//			System.out.println("datas[4]--"+datas[4].substring(0, datas[4].length()-2));

			if (outParams != null && outParams.get("msg") != null) {
				logger.info("OUT PARAM IS " + outParams.get("msg"));
				return bean;
			}
//			System.out.println("inside this");
			return bean;
		} catch (Exception e) {
			logger.info("Exception in runTTUMProcess " + e);
			return bean;
		}

	}

	private class UnmatchedTTUMProc1s extends StoredProcedure {
		private static final String insert_proc = "FILE_UPLOAD_VIEW";

		public UnmatchedTTUMProc1s(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("I_FILETYPE", Types.VARCHAR));
			compile();
		}

	}

	public List<Object> excelMicroAtmTTUMDownload(SettlementBean settlementBean) {
		return null;
	}

	public List<Object> excelMicroAtmReportDownload(SettlementBean settlementBean) {
		return null;
	}

	public List<Object> downloadDhanaReportCTCACQ(SettlementBean settlementBean) {
		return null;
	}

	public List<Object> downloadDhanaReportCTCISS(SettlementBean settlementBean) {
		List<Object> data = new ArrayList();
		try {
			String getInterchange1 = "select BUSINESS_DATE, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, FEE, BR_CODE, ISS_SOL_ID, MCC, REMARKS, NETWORK, POSTED_DATE, GL_ACCOUNT, TR_NO, DCRS_REMARKS, FILEDATE, FILENAME from settlement_nfs_iss_c2c_cbs\r\nwhere FILEDATE =STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='NFS-ISS-C2C-MATCHED-2'";
			System.out.println("downloadDhanaReport  acq getInterchange1  " + getInterchange1);
			List<String> Column_list = new ArrayList<>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
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
			System.out.println("getInterchange1 " + getInterchange1);

			String getInterchange2 = "select BUSINESS_DATE, CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, FEE, BR_CODE, ISS_SOL_ID, MCC, REMARKS, NETWORK, POSTED_DATE, GL_ACCOUNT, TR_NO, DCRS_REMARKS, FILEDATE, FILENAME from settlement_nfs_iss_c2c_cbs \r\nwhere FILEDATE =STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='NFS-ISS-C2C-UNRECON-2'";
			Column_list = new ArrayList<>();
			Column_list.add("BUSINESS_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("FEE");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("MCC");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("POSTED_DATE");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("TR_NO");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns2 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns2) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("getInterchange2 " + getInterchange2);

			String getInterchange4 = "SELECT PARTICIPANT_ID, TRANSACTION_TYPE, FROM_ACCOUNT_TYPE, TO_ACCOUNT_TYPE, TXN_SERIAL_NO, RESPONSE_CODE, PAN_NUMBER, MEMBER_NUMBER, APPROVAL_NUMBER,\r\nSYS_TRACE_AUDIT_NO, TRANSACTION_DATE, TRANSACTION_TIME, MERCHANT_CATEGORY_CD, CARD_ACC_SETTLE_DT, CARD_ACC_ID, CARD_ACC_TERMINAL_ID, CARD_ACC_TERMINAL_LOC, \r\nACQUIRER_ID, NETWORK_ID, ACCOUNT_1_NUMBER, ACCOUNT_1_BRANCH_ID, ACCOUNT_2_NUMBER, ACCOUNT_2_BRANCH_ID, TXN_CURRENCY_CODE, TXN_AMOUNT, ACTUAL_TXN_AMT,\r\nTXN_ACTIVITY_FEE, ISS_SETTLE_CURRENCY_CD, ISS_SETTLE_AMNT, ISS_SETTLE_FEE, ISS_SETTLE_PROCESS_FEE, PART_ID, DCRS_TRAN_NO, NEXT_TRAN_DATE, CREATEDDATE, \r\nCREATEDBY, FILEDATE, DCRS_REMARKS, FPAN, CYCLE, FILENAME from settlement_nfs_iss_c2c_nfs \r\nwhere FILEDATE =STR_TO_DATE('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='NFS-ISS-C2C-MATCHED-2'";
			Column_list = new ArrayList<>();
			Column_list.add("PARTICIPANT_ID");
			Column_list.add("TRANSACTION_TYPE");
			Column_list.add("FROM_ACCOUNT_TYPE");
			Column_list.add("TO_ACCOUNT_TYPE");
			Column_list.add("TXN_SERIAL_NO");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PAN_NUMBER");
			Column_list.add("MEMBER_NUMBER");
			Column_list.add("APPROVAL_NUMBER");
			Column_list.add("SYS_TRACE_AUDIT_NO");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("TRANSACTION_DATE");
			Column_list.add("TRANSACTION_TIME");
			Column_list.add("MERCHANT_CATEGORY_CD");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("CARD_ACC_ID");
			Column_list.add("CARD_ACC_TERMINAL_ID");
			Column_list.add("CARD_ACC_TERMINAL_LOC");
			Column_list.add("ACQUIRER_ID");
			Column_list.add("NETWORK_ID");
			Column_list.add("ACCOUNT_1_NUMBER");
			Column_list.add("ACCOUNT_1_BRANCH_ID");
			Column_list.add("ACCOUNT_2_NUMBER");
			Column_list.add("ACCOUNT_2_BRANCH_ID");
			Column_list.add("TXN_CURRENCY_CODE");
			Column_list.add("TXN_AMOUNT");
			Column_list.add("ACTUAL_TXN_AMT");
			Column_list.add("TXN_ACTIVITY_FEE");
			Column_list.add("ISS_SETTLE_CURRENCY_CD");
			Column_list.add("ISS_SETTLE_AMNT");
			Column_list.add("ISS_SETTLE_FEE");
			Column_list.add("ISS_SETTLE_PROCESS_FEE");
			Column_list.add("PART_ID");
			Column_list.add("DCRS_TRAN_NO");
			Column_list.add("NEXT_TRAN_DATE");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("FILEDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("CYCLE");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns4 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange4, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns4) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("getInterchange4 " + getInterchange4);

			String getInterchange6 = "SELECT PARTICIPANT_ID, TRANSACTION_TYPE, FROM_ACCOUNT_TYPE, TO_ACCOUNT_TYPE, TXN_SERIAL_NO, RESPONSE_CODE, PAN_NUMBER, MEMBER_NUMBER, APPROVAL_NUMBER,\r\nSYS_TRACE_AUDIT_NO, TRANSACTION_DATE, TRANSACTION_TIME, MERCHANT_CATEGORY_CD, CARD_ACC_SETTLE_DT, CARD_ACC_ID, CARD_ACC_TERMINAL_ID, CARD_ACC_TERMINAL_LOC, \r\nACQUIRER_ID, NETWORK_ID, ACCOUNT_1_NUMBER, ACCOUNT_1_BRANCH_ID, ACCOUNT_2_NUMBER, ACCOUNT_2_BRANCH_ID, TXN_CURRENCY_CODE, TXN_AMOUNT, ACTUAL_TXN_AMT,\r\nTXN_ACTIVITY_FEE, ISS_SETTLE_CURRENCY_CD, ISS_SETTLE_AMNT, ISS_SETTLE_FEE, ISS_SETTLE_PROCESS_FEE, PART_ID, DCRS_TRAN_NO, NEXT_TRAN_DATE, CREATEDDATE, \r\nCREATEDBY, FILEDATE, DCRS_REMARKS, FPAN, CYCLE, FILENAME from settlement_nfs_iss_c2c_nfs \r\nwhere FILEDATE =STR_TO_DATE('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='NFS-ISS-C2C-UNRECON-2'";
			Column_list = new ArrayList<>();
			Column_list.add("PARTICIPANT_ID");
			Column_list.add("TRANSACTION_TYPE");
			Column_list.add("FROM_ACCOUNT_TYPE");
			Column_list.add("TO_ACCOUNT_TYPE");
			Column_list.add("TXN_SERIAL_NO");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PAN_NUMBER");
			Column_list.add("MEMBER_NUMBER");
			Column_list.add("APPROVAL_NUMBER");
			Column_list.add("SYS_TRACE_AUDIT_NO");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("TRANSACTION_DATE");
			Column_list.add("TRANSACTION_TIME");
			Column_list.add("MERCHANT_CATEGORY_CD");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("CARD_ACC_ID");
			Column_list.add("CARD_ACC_TERMINAL_ID");
			Column_list.add("CARD_ACC_TERMINAL_LOC");
			Column_list.add("ACQUIRER_ID");
			Column_list.add("NETWORK_ID");
			Column_list.add("ACCOUNT_1_NUMBER");
			Column_list.add("ACCOUNT_1_BRANCH_ID");
			Column_list.add("ACCOUNT_2_NUMBER");
			Column_list.add("ACCOUNT_2_BRANCH_ID");
			Column_list.add("TXN_CURRENCY_CODE");
			Column_list.add("TXN_AMOUNT");
			Column_list.add("ACTUAL_TXN_AMT");
			Column_list.add("TXN_ACTIVITY_FEE");
			Column_list.add("ISS_SETTLE_CURRENCY_CD");
			Column_list.add("ISS_SETTLE_AMNT");
			Column_list.add("ISS_SETTLE_FEE");
			Column_list.add("ISS_SETTLE_PROCESS_FEE");
			Column_list.add("PART_ID");
			Column_list.add("DCRS_TRAN_NO");
			Column_list.add("NEXT_TRAN_DATE");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("FILEDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("CYCLE");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns6 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange6, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns6) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("getInterchange6 " + getInterchange6);

			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

	public List<Object> downloadDhanaReport(SettlementBean settlementBean) {
		List<Object> data = new ArrayList<Object>();
		try {
			
			String getInterchange1 = "SELECT CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, BR_CODE, ISS_SOL_ID, REMARKS, NETWORK, DCRS_REMARKS, FILEDATE, FILENAME, CREATEDDATE\r\nFROM settlement_nfs_acq_cbs WHERE FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS LIKE 'NFS-ACQ-CBS-MATCHED-2%'";
			System.out.println("downloadDhanaReport  acq getInterchange1  " + getInterchange1);
			List<String> Column_list = new ArrayList<>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			data.add(Column_list);
			final List<String> columns = Column_list;
			List<Object> DailyData = getJdbcTemplate().query(getInterchange1, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
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
			System.out.println("query 1");
			String getInterchange2 = "SELECT CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, BR_CODE, ISS_SOL_ID, REMARKS, NETWORK, DCRS_REMARKS, FILEDATE, FILENAME, CREATEDDATE FROM settlement_nfs_acq_cbs WHERE FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='NFS-ACQ-CBS-MATCHED-1'";
			Column_list = new ArrayList<>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			data.add(Column_list);
			final List<String> columns2 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange2, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns2) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 2");

			String getInterchange3 = "SELECT CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, BR_CODE, ISS_SOL_ID, REMARKS, NETWORK, DCRS_REMARKS, FILEDATE, FILENAME, CREATEDDATE FROM settlement_nfs_acq_cbs WHERE FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='NFS-ACQ-CBS-UNRECON-1'";
			Column_list = new ArrayList<>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			data.add(Column_list);
			final List<String> columns3 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange3, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns3) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 3");

			System.gc();
			String getInterchange5 = "SELECT PARTICIPANT_ID,TRANSACTION_TYPE,FROM_ACCOUNT_TYPE,TO_ACCOUNT_TYPE,TXN_SERIAL_NO,RESPONSE_CODE,PAN_NUMBER,MEMBER_NUMBER,APPROVAL_NUMBER,SYS_TRACE_AUDIT_NO,TRANSACTION_DATE,TRANSACTION_TIME,MERCHANT_CATEGORY_CD,CARD_ACC_SETTLE_DT,\r\nCARD_ACC_ID,CARD_ACC_TERMINAL_ID,CARD_ACC_TERMINAL_LOC,ACQUIRER_ID,ACQ_SETTLE_DATE,TXN_CURRENCY_CODE,TXN_AMOUNT,ACTUAL_TXN_AMT,TXN_ACTIVITY_FEE,ACQ_SETTLE_CURRENCY_CD,ACQ_SETTLE_AMNT,ACQ_SETTLE_FEE,ACQ_SETTLE_PROCESS_FEE,TXN_ACQ_CONV_RATE,\r\nPART_ID,DCRS_TRAN_NO,NEXT_TRAN_DATE,CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,CYCLE,FPAN,FILENAME FROM \r\n settlement_nfs_acq_nfs WHERE FILEDATE= STR_TO_DATE('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND  DCRS_REMARKS = 'NFS-ACQ-MATCHED-2' ";
			Column_list = new ArrayList<>();
			Column_list.add("PARTICIPANT_ID");
			Column_list.add("TRANSACTION_TYPE");
			Column_list.add("FROM_ACCOUNT_TYPE");
			Column_list.add("TO_ACCOUNT_TYPE");
			Column_list.add("TXN_SERIAL_NO");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PAN_NUMBER");
			Column_list.add("MEMBER_NUMBER");
			Column_list.add("APPROVAL_NUMBER");
			Column_list.add("SYS_TRACE_AUDIT_NO");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("TRANSACTION_DATE");
			Column_list.add("TRANSACTION_TIME");
			Column_list.add("MERCHANT_CATEGORY_CD");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("CARD_ACC_ID");
			Column_list.add("CARD_ACC_TERMINAL_ID");
			Column_list.add("CARD_ACC_TERMINAL_LOC");
			Column_list.add("ACQUIRER_ID");
			Column_list.add("ACQ_SETTLE_DATE");
			Column_list.add("TXN_CURRENCY_CODE");
			Column_list.add("TXN_AMOUNT");
			Column_list.add("ACTUAL_TXN_AMT");
			Column_list.add("TXN_ACTIVITY_FEE");
			Column_list.add("ACQ_SETTLE_CURRENCY_CD");
			Column_list.add("ACQ_SETTLE_AMNT");
			Column_list.add("ACQ_SETTLE_FEE");
			Column_list.add("ACQ_SETTLE_PROCESS_FEE");
			Column_list.add("TXN_ACQ_CONV_RATE");
			Column_list.add("PART_ID");
			Column_list.add("DCRS_TRAN_NO");
			Column_list.add("NEXT_TRAN_DATE");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("FILEDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("CYCLE");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns5 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange5, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns5) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 5");

			System.gc();
			String getInterchange6 = "SELECT CARD_NO, TRACE_NO, AC_NO, TRAN_DATE, ATM_ID, TYPE, AMOUNT, BR_CODE, ISS_SOL_ID, REMARKS, NETWORK, DCRS_REMARKS, FILEDATE, FILENAME, CREATEDDATE FROM settlement_nfs_acq_cbs WHERE FILEDATE = DATE_FORMAT('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='NFS-ACQ-CBS-UNRECON-2'";
			Column_list = new ArrayList<>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("REMARKS");
			Column_list.add("NETWORK");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("FILENAME");
			Column_list.add("CREATEDDATE");
			data.add(Column_list);
			final List<String> columns6 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange6, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns6) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 6");

			String getInterchange7 = "SELECT PARTICIPANT_ID,TRANSACTION_TYPE,FROM_ACCOUNT_TYPE,TO_ACCOUNT_TYPE,TXN_SERIAL_NO,RESPONSE_CODE,PAN_NUMBER,MEMBER_NUMBER,APPROVAL_NUMBER,SYS_TRACE_AUDIT_NO,TRANSACTION_DATE,TRANSACTION_TIME,MERCHANT_CATEGORY_CD,CARD_ACC_SETTLE_DT,\r\nCARD_ACC_ID,CARD_ACC_TERMINAL_ID,CARD_ACC_TERMINAL_LOC,ACQUIRER_ID,ACQ_SETTLE_DATE,TXN_CURRENCY_CODE,TXN_AMOUNT,ACTUAL_TXN_AMT,TXN_ACTIVITY_FEE,ACQ_SETTLE_CURRENCY_CD,ACQ_SETTLE_AMNT,ACQ_SETTLE_FEE,ACQ_SETTLE_PROCESS_FEE,TXN_ACQ_CONV_RATE,\r\nPART_ID,DCRS_TRAN_NO,NEXT_TRAN_DATE,CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,CYCLE,FPAN,FILENAME FROM \r\n settlement_nfs_acq_nfs WHERE FILEDATE=STR_TO_DATE('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='NFS-ACQ-UNRECON-2'";
			Column_list = new ArrayList<>();
			Column_list.add("PARTICIPANT_ID");
			Column_list.add("TRANSACTION_TYPE");
			Column_list.add("FROM_ACCOUNT_TYPE");
			Column_list.add("TO_ACCOUNT_TYPE");
			Column_list.add("TXN_SERIAL_NO");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PAN_NUMBER");
			Column_list.add("MEMBER_NUMBER");
			Column_list.add("APPROVAL_NUMBER");
			Column_list.add("SYS_TRACE_AUDIT_NO");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("TRANSACTION_DATE");
			Column_list.add("TRANSACTION_TIME");
			Column_list.add("MERCHANT_CATEGORY_CD");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("CARD_ACC_ID");
			Column_list.add("CARD_ACC_TERMINAL_ID");
			Column_list.add("CARD_ACC_TERMINAL_LOC");
			Column_list.add("ACQUIRER_ID");
			Column_list.add("ACQ_SETTLE_DATE");
			Column_list.add("TXN_CURRENCY_CODE");
			Column_list.add("TXN_AMOUNT");
			Column_list.add("ACTUAL_TXN_AMT");
			Column_list.add("TXN_ACTIVITY_FEE");
			Column_list.add("ACQ_SETTLE_CURRENCY_CD");
			Column_list.add("ACQ_SETTLE_AMNT");
			Column_list.add("ACQ_SETTLE_FEE");
			Column_list.add("ACQ_SETTLE_PROCESS_FEE");
			Column_list.add("TXN_ACQ_CONV_RATE");
			Column_list.add("PART_ID");
			Column_list.add("DCRS_TRAN_NO");
			Column_list.add("NEXT_TRAN_DATE");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("FILEDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("CYCLE");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns7 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange7, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns7) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 7");

			String getInterchange7A = "SELECT PARTICIPANT_ID, TRANSACTION_TYPE, FROM_ACCOUNT_TYPE, TO_ACCOUNT_TYPE, TXN_SERIAL_NO, RESPONSE_CODE, PAN_NUMBER, MEMBER_NUMBER, \r\nAPPROVAL_NUMBER, SYS_TRACE_AUDIT_NO, TRANSACTION_DATE, TRANSACTION_TIME, MERCHANT_CATEGORY_CD, CARD_ACC_SETTLE_DT, CARD_ACC_ID, CARD_ACC_TERMINAL_ID, \r\nCARD_ACC_TERMINAL_LOC, ACQUIRER_ID, ACQ_SETTLE_DATE, TXN_CURRENCY_CODE, TXN_AMOUNT, ACTUAL_TXN_AMT, TXN_ACTIVITY_FEE, ACQ_SETTLE_CURRENCY_CD, ACQ_SETTLE_AMNT, \r\nACQ_SETTLE_FEE, ACQ_SETTLE_PROCESS_FEE, TXN_ACQ_CONV_RATE, PART_ID, DCRS_TRAN_NO, NEXT_TRAN_DATE, CREATEDDATE, CREATEDBY, FILEDATE, DCRS_REMARKS, CYCLE, FPAN, FILENAME\r\nFROM settlement_nfs_acq_nfs WHERE FILEDATE = DATE_FORMAT('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='LATE-REVERSAL-MATCHED'";
			Column_list = new ArrayList<>();
			Column_list.add("PARTICIPANT_ID");
			Column_list.add("TRANSACTION_TYPE");
			Column_list.add("FROM_ACCOUNT_TYPE");
			Column_list.add("TO_ACCOUNT_TYPE");
			Column_list.add("TXN_SERIAL_NO");
			Column_list.add("RESPONSE_CODE");
			Column_list.add("PAN_NUMBER");
			Column_list.add("MEMBER_NUMBER");
			Column_list.add("APPROVAL_NUMBER");
			Column_list.add("SYS_TRACE_AUDIT_NO");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("TRANSACTION_DATE");
			Column_list.add("TRANSACTION_TIME");
			Column_list.add("MERCHANT_CATEGORY_CD");
			Column_list.add("CARD_ACC_SETTLE_DT");
			Column_list.add("CARD_ACC_ID");
			Column_list.add("CARD_ACC_TERMINAL_ID");
			Column_list.add("CARD_ACC_TERMINAL_LOC");
			Column_list.add("ACQUIRER_ID");
			Column_list.add("ACQ_SETTLE_DATE");
			Column_list.add("TXN_CURRENCY_CODE");
			Column_list.add("TXN_AMOUNT");
			Column_list.add("ACTUAL_TXN_AMT");
			Column_list.add("TXN_ACTIVITY_FEE");
			Column_list.add("ACQ_SETTLE_CURRENCY_CD");
			Column_list.add("ACQ_SETTLE_AMNT");
			Column_list.add("ACQ_SETTLE_FEE");
			Column_list.add("ACQ_SETTLE_PROCESS_FEE");
			Column_list.add("TXN_ACQ_CONV_RATE");
			Column_list.add("PART_ID");
			Column_list.add("DCRS_TRAN_NO");
			Column_list.add("NEXT_TRAN_DATE");
			Column_list.add("CREATEDDATE");
			Column_list.add("CREATEDBY");
			Column_list.add("FILEDATE");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("CYCLE");
			Column_list.add("FPAN");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns8 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange7A, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns8) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 8");

			String getInterchange8 = "SELECT TRANSTYPE,RESP_CODE,CARDNO,RRN,STANNO,ACQ,ISS,TRASN_DATE,TRANS_TIME,ATMID,SETTLEDATE,REQUESTAMT,RECEIVEDAMT,STATUS,DCRS_REMARKS,FILEDATE,CYCLE,MERCHANT_TYPE,FPAN,CREATEDDATE,FILENAME \r\nFROM    settlement_nfs_acq_rev_report WHERE FILEDATE= STR_TO_DATE('"
					+

					settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='MATCHED'";
			Column_list = new ArrayList<>();
			Column_list.add("TRANSTYPE");
			Column_list.add("RESP_CODE");
			Column_list.add("CARDNO");
			Column_list.add("RRN");
			Column_list.add("STANNO");
			Column_list.add("ACQ");
			Column_list.add("ISS");
			Column_list.add("TRASN_DATE");
			Column_list.add("TRANS_TIME");
			Column_list.add("ATMID");
			Column_list.add("SETTLEDATE");
			Column_list.add("REQUESTAMT");
			Column_list.add("RECEIVEDAMT");
			Column_list.add("STATUS");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("CYCLE");
			Column_list.add("MERCHANT_TYPE");
			Column_list.add("FPAN");
			Column_list.add("CREATEDDATE");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns9 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange8, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns9) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 9");

			String getInterchange9 = "SELECT TRANSTYPE,RESP_CODE,CARDNO,RRN,STANNO,ACQ,ISS,TRASN_DATE,TRANS_TIME,ATMID,SETTLEDATE,REQUESTAMT,RECEIVEDAMT,STATUS,DCRS_REMARKS,FILEDATE,CYCLE,MERCHANT_TYPE,FPAN,CREATEDDATE,FILENAME FROM settlement_nfs_acq_rev_report WHERE FILEDATE= STR_TO_DATE('"
					+ settlementBean.getDatepicker() + "','%Y/%m/%d') AND DCRS_REMARKS='UNMATCHED'";
			Column_list = new ArrayList<>();
			Column_list.add("TRANSTYPE");
			Column_list.add("RESP_CODE");
			Column_list.add("CARDNO");
			Column_list.add("RRN");
			Column_list.add("STANNO");
			Column_list.add("ACQ");
			Column_list.add("ISS");
			Column_list.add("TRASN_DATE");
			Column_list.add("TRANS_TIME");
			Column_list.add("ATMID");
			Column_list.add("SETTLEDATE");
			Column_list.add("REQUESTAMT");
			Column_list.add("RECEIVEDAMT");
			Column_list.add("STATUS");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("CYCLE");
			Column_list.add("MERCHANT_TYPE");
			Column_list.add("FPAN");
			Column_list.add("CREATEDDATE");
			Column_list.add("FILENAME");
			data.add(Column_list);
			final List<String> columns10 = Column_list;
			DailyData = getJdbcTemplate().query(getInterchange9, new Object[] {},
					new ResultSetExtractor<List<Object>>() {
						public List<Object> extractData(ResultSet rs) throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> data = new HashMap<String, String>();
								for (String column : columns10) {
									data.put(column, rs.getString(column));
								}
								beanList.add(data);
							}
							return beanList;
						}
					});
			data.add(DailyData);
			System.out.println("query 10");

			return data;
		} catch (Exception e) {
			System.out.println("Exception in getInterchangeData " + e);
			return null;
		}
	}

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

	public List<Object> getUnreconAcqOffusTTUMReport(UnMatchedTTUMBean beanObj, String TTUMTYPE) {
		return null;
	}

	public List<String> getNFSVoucher(UnMatchedTTUMBean beanObj) {
		return null;
	}

	public List<Object> downloadDhanaReport2(SettlementBean beanObj) {
		return null;
	}

	public List<Object> downloadDhanaReport3(SettlementBean beanObj) {
		return null;
	}

	public List<Object> downloadIssuerDhanaReport3(SettlementBean settlementBean) {
		return null;
	}

	public List<Object> downloadRupayDhanaReport2(SettlementBean settlementBean) {
		return null;
	}

	@Override
	public boolean runTTUMProcess(UnMatchedTTUMBean beanObj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean runTTUMProcess(String adjtype, String localDate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean increaseViewFile(String type, String fromDate, String username, String cycle)
			throws Exception, SQLException {
		// TODO Auto-generated method stub
		return false;
	}
}
