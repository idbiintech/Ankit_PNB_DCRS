package com.recon.util;

import com.recon.model.MastercardUploadBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.RupayUploadBean;
import com.recon.model.UnMatchedTTUMBean;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFCreationHelper;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class GenerateUCOTTUM extends JdbcDaoSupport {
	private static final Logger logger = Logger.getLogger(com.recon.util.GenerateUCOTTUM.class);

	public static final String OUTPUT_FOLDER = String.valueOf(System.getProperty("catalina.home")) + File.separator
			+ "TTUM";

	public void generateMastercardEODTTUM(String stPath, String FileName, int fileCount, List<Object> TTUMData,
			String TTUMName) {
		List<String> files = new ArrayList<>();
		System.out.println("inside the next ");
		try {
			for (int j = 1; j <= 1; j++) {
				List<Object> lstData = (ArrayList) TTUMData.get(j - 1);
				String newFileName = FileName;
				File file = new File(String.valueOf(stPath) + File.separator + newFileName);
				if (file.exists())
					FileUtils.forceDelete(file);
				file.createNewFile();
				BufferedWriter out = new BufferedWriter(
						new FileWriter(String.valueOf(stPath) + File.separator + newFileName, true));
				int startLine = 0;
				System.out.println("lstData : " + lstData.size());
				if (lstData.size() > 0) {
					for (int i = 0; i < lstData.size(); i++) {
						Map<String, String> table_Data = (Map<String, String>) lstData.get(i);
						if (startLine > 0)
							out.write("\n");
						startLine++;
						StringBuffer lineData = new StringBuffer();
						String b = "";
						if (TTUMName.equalsIgnoreCase("ACQUIRER") || TTUMName.equalsIgnoreCase("ISSUER"))
							lineData.append(table_Data.get("DATA"));
						out.write(lineData.toString());
					}
				} else {
					StringBuffer lineData = new StringBuffer();
					lineData.append("~~ NO DATA FOUND FOR SELECTED CRITERIA ~~");
					out.write(lineData.toString());
				}
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			logger.info("Exception in generateMultipleDRMTTUMFiles " + e);
		}
	}

	public String checkAndMakeDirectory(String fileDate, String category) {
		String stnewDate = "";
		try {
			String stPath = OUTPUT_FOLDER;
			File folder = new File(stPath);
			if (folder.exists())
				folder.delete();
			folder.mkdir();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
				stnewDate = fileDate;
			} catch (Exception excp) {
				logger.info("Exception while parsing Date" + excp);
			}
			logger.info("Path is " + stPath + File.separator + category);
			logger.info("Path is " + stPath + category);
			File checkFile = new File(String.valueOf(stPath) + File.separator + category);
			if (checkFile.exists())
				try {
					System.out.println("Herrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
					FileUtils.deleteDirectory(new File(String.valueOf(stPath) + File.separator + category));
					System.out.println("1111111111111111111111111111111111111111111111");
				} catch (Exception e) {
					System.out.println("Inside the  is ==============================");
					e.printStackTrace();
				}
			File directory = new File(String.valueOf(stPath) + File.separator + category);
			if (!directory.exists()) {
				System.out.println("inside the if ");
				directory.mkdir();
			}
			directory = new File(String.valueOf(stPath) + File.separator + category + File.separator + fileDate);
			if (!directory.exists())
				directory.mkdir();
			System.out.println("inside the return");
			return String.valueOf(stPath) + File.separator + category + File.separator + fileDate;
		} catch (Exception e) {
			logger.info("Exception in checkAndMakeDirectory " + e);
			return "Exception Occured";
		}
	}

	public void generateMastercardTTUMFiles(String stPath, String FileName, int fileCount, List<Object> TTUMData,
			String TTUMName) {
		List<String> files = new ArrayList<>();
		System.out.println("inside the next ");
		try {
			for (int j = 1; j <= 1; j++) {
				List<Object> lstData = (ArrayList) TTUMData.get(j - 1);
				String newFileName = FileName;
				File file = new File(String.valueOf(stPath) + File.separator + newFileName);
				if (file.exists())
					FileUtils.forceDelete(file);
				file.createNewFile();
				BufferedWriter out = new BufferedWriter(
						new FileWriter(String.valueOf(stPath) + File.separator + newFileName, true));
				int startLine = 0;
				System.out.println("lstData : " + lstData.size());
				if (lstData.size() > 0) {
					for (int i = 0; i < lstData.size(); i++) {
						Map<String, String> table_Data = (Map<String, String>) lstData.get(i);
						if (startLine > 0)
							out.write("\n");
						startLine++;
						StringBuffer lineData = new StringBuffer();
						String b = "";
						if (TTUMName.equalsIgnoreCase("DECLINED")) {
							lineData.append(String.valueOf(table_Data.get("ACC_TYPE")) + "~"
									+ (String) table_Data.get("ACC_NO") + "~" + (String) table_Data.get("DRCR"));
							lineData.append("~" + (String) table_Data.get("AMOUNT") + "~"
									+ (String) table_Data.get("perticulers"));
						}
						if (TTUMName.equalsIgnoreCase("REFUND")) {
							lineData.append(String.valueOf(table_Data.get("ACCTYPE")) + "~"
									+ (String) table_Data.get("ACCOUNTNO") + "~" + (String) table_Data.get("DRCR"));
							lineData.append("~" + (String) table_Data.get("REFUND") + "~"
									+ (String) table_Data.get("perticulers"));
						}
						if (TTUMName.equalsIgnoreCase("UNRECON")) {
							lineData.append(String.valueOf(table_Data.get("ACC_TYPE")) + "~"
									+ (String) table_Data.get("ACC_NO") + "~" + (String) table_Data.get("DRCR"));
							lineData.append("~" + (String) table_Data.get("AMOUNT") + "~"
									+ (String) table_Data.get("perticulers"));
						}
						if (TTUMName.equalsIgnoreCase("FEE")) {
							lineData.append(String.valueOf(table_Data.get("ACCTYPE")) + "~"
									+ (String) table_Data.get("ACCOUNTNO") + "~" + (String) table_Data.get("DRCR"));
							lineData.append("~" + (String) table_Data.get("REFUND") + "~"
									+ (String) table_Data.get("perticulers"));
						}
						if (TTUMName.equalsIgnoreCase("REPRESENTMENT")
								|| TTUMName.equalsIgnoreCase("CHARGEBACKRAISE")) {
							lineData.append(String.valueOf(table_Data.get("ACCTYPE")) + "~"
									+ (String) table_Data.get("ACCOUNTNO") + "~" + (String) table_Data.get("DRCR"));
							lineData.append("~" + (String) table_Data.get("AMOUNT") + "~"
									+ (String) table_Data.get("perticulers"));
						}
						if (TTUMName.equalsIgnoreCase("LATEPRESENTMENT")) {
							lineData.append(String.valueOf(table_Data.get("ACCTYPE")) + "~"
									+ (String) table_Data.get("ACCOUNTNO") + "~" + (String) table_Data.get("DRCR"));
							lineData.append("~" + (String) table_Data.get("AMOUNT") + "~"
									+ (String) table_Data.get("perticulers"));
						}
						if (TTUMName.equalsIgnoreCase("UNRECON2")) {
							lineData.append(String.valueOf(table_Data.get("ACCTYPE")) + "~"
									+ (String) table_Data.get("ACCOUNTNO") + "~" + (String) table_Data.get("DRCR"));
							lineData.append("~" + (String) table_Data.get("AMOUNT") + "~"
									+ (String) table_Data.get("perticulers"));
						}
						out.write(lineData.toString());
					}
				} else {
					StringBuffer lineData = new StringBuffer();
					lineData.append("~~ NO DATA FOUND FOR SELECTED CRITERIA ~~");
					out.write(lineData.toString());
				}
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			logger.info("Exception in generateMultipleDRMTTUMFiles " + e);
		}
	}

	public void generateMastercardTTUMFiles_20112023(String stPath, String FileName, int fileCount,
			List<Object> TTUMData, String TTUMName) {
		List<String> files = new ArrayList<>();
		System.out.println("inside the next ");
		try {
			for (int j = 1; j <= 1; j++) {
				List<Object> lstData = (ArrayList) TTUMData.get(j - 1);
				String newFileName = FileName.replaceAll(".txt", "_" + j + ".txt");
				File file = new File(String.valueOf(stPath) + File.separator + newFileName);
				if (file.exists())
					FileUtils.forceDelete(file);
				file.createNewFile();
				BufferedWriter out = new BufferedWriter(
						new FileWriter(String.valueOf(stPath) + File.separator + newFileName, true));
				int startLine = 0;
				for (int i = 0; i < lstData.size(); i++) {
					Map<String, String> table_Data = (Map<String, String>) lstData.get(i);
					if (startLine > 0)
						out.write("\n");
					startLine++;
					StringBuffer lineData = new StringBuffer();
					String b = "";
					if (TTUMName.equalsIgnoreCase("DECLINED")) {
						lineData.append(String.valueOf(table_Data.get("ACC_TYPE")) + "~"
								+ (String) table_Data.get("ACC_NO") + "~" + (String) table_Data.get("DRCR"));
						lineData.append("~" + (String) table_Data.get("AMOUNT") + "~"
								+ ((table_Data.get("perticulers") == null) ? "" : table_Data.get("perticulers")));
					}
					out.write(lineData.toString());
				}
				out.flush();
				out.close();
				files.add(String.valueOf(stPath) + File.separator + newFileName);
			}
		} catch (Exception e) {
			logger.info("Exception in generateMultipleDRMTTUMFiles " + e);
		}
	}

	public void generateMastercardReportFiles(String stPath, String FileName, int fileCount, List<Object> TTUMData,
			String TTUMName) {
		List<String> files = new ArrayList<>();
		System.out.println("inside the next ");
		try {
			for (int j = 1; j <= 1; j++) {
				List<Object> lstData = (ArrayList) TTUMData.get(j - 1);
				String newFileName = FileName;
				File file = new File(String.valueOf(stPath) + File.separator + newFileName);
				if (file.exists())
					FileUtils.forceDelete(file);
				file.createNewFile();
				BufferedWriter out = new BufferedWriter(
						new FileWriter(String.valueOf(stPath) + File.separator + newFileName, true));
				int startLine = 0;
				for (int i = 0; i < lstData.size(); i++) {
					Map<String, String> table_Data = (Map<String, String>) lstData.get(i);
					if (startLine > 0)
						out.write("\n");
					startLine++;
					StringBuffer lineData = new StringBuffer();
					String b = "";
					if (TTUMName.equalsIgnoreCase("1")) {
						lineData.append(String.valueOf(table_Data.get("PAN")) + "|"
								+ (String) table_Data.get("PROCESSING_CODE") + "|" + (String) table_Data.get("AMOUNT"));
						lineData.append("|" + (String) table_Data.get("AMOUNT_RECON") + "|"
								+ (String) table_Data.get("CONV_RATE_RECON"));
						lineData.append("|" + (String) table_Data.get("DATE_VAL") + "|"
								+ (String) table_Data.get("EXPIRE_DATE"));
						lineData.append("|" + (String) table_Data.get("DATA_CODE") + "|"
								+ (String) table_Data.get("CARD_SEQ_NUM"));
						lineData.append("|" + (String) table_Data.get("FUNCATION_CODE") + "|"
								+ (String) table_Data.get("MSG_RES_CODE"));
						lineData.append("|" + (String) table_Data.get("CARD_ACC_CODE") + "|"
								+ (String) table_Data.get("AMOUNT_ORG"));
						lineData.append("|" + (String) table_Data.get("AQUIERER_REF_NO") + "|"
								+ (String) table_Data.get("FI_ID_CODE"));
						lineData.append("|" + (String) table_Data.get("RETRV_REF_NO") + "|"
								+ (String) table_Data.get("APPROVAL_CODE"));
						lineData.append("|" + (String) table_Data.get("SERVICE_CODE") + "|"
								+ (String) table_Data.get("CARD_ACC_TERM_ID"));
						lineData.append("|" + (String) table_Data.get("CARD_ACC_ID_CODE") + "|"
								+ (String) table_Data.get("ADDITIONAL_DATA"));
						lineData.append("|" + (String) table_Data.get("CURRENCY_CODE_TRAN") + "|"
								+ (String) table_Data.get("CURRENCY_CODE_RECON"));
						lineData.append("|" + (String) table_Data.get("TRAN_LIFECYCLE_ID") + "|"
								+ (String) table_Data.get("TRAN_DEST_ID_CODE"));
						lineData.append("|" + (String) table_Data.get("TRAN_ORG_ID_CODE") + "|"
								+ (String) table_Data.get("CURRENCY_CODE_TRAN"));
						lineData.append("|" + (String) table_Data.get("CARD_ISS_REF_DATA") + "|"
								+ (String) table_Data.get("RECV_INST_IDCODE"));
						lineData.append("|" + (String) table_Data.get("TERMINAL_TYPE") + "|"
								+ (String) table_Data.get("ELEC_COM_INDIC"));
						lineData.append("|" + (String) table_Data.get("PROCESSING_MODE") + "|"
								+ (String) table_Data.get("CURRENCY_EXPONENT"));
						lineData.append("|" + (String) table_Data.get("BUSINESS_ACT") + "|"
								+ (String) table_Data.get("SETTLEMENT_IND"));
						lineData.append("|" + (String) table_Data.get("CARD_ACCP_NAME_LOC") + "|"
								+ (String) table_Data.get("HEADER_TYPE"));
						lineData.append("|" + (String) table_Data.get("HEADER_TYPE") + "|"
								+ (String) table_Data.get("FILE_NAME"));
						lineData.append(
								"|" + (String) table_Data.get("FILEDATE") + "|" + (String) table_Data.get("FPAN"));
					}
					out.write(lineData.toString());
				}
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			logger.info("Exception in generateMultipleDRMTTUMFiles " + e);
		}
	}

	public void generateTTUMFile(String stPath, String FileName, List<Object> TTUMData) {
		try {
			File file = new File(String.valueOf(stPath) + File.separator + FileName);
			if (file.exists())
				FileUtils.forceDelete(file);
			file.createNewFile();
			BufferedWriter out = new BufferedWriter(
					new FileWriter(String.valueOf(stPath) + File.separator + FileName, true));
			int startLine = 0;
			for (int i = 0; i < TTUMData.size(); i++) {
				Map<String, String> table_Data = (Map<String, String>) TTUMData.get(i);
				if (startLine > 0)
					out.write("\n");
				startLine++;
				StringBuffer lineData = new StringBuffer();
				if (FileName.contains("VISA")) {
					lineData.append(
							String.valueOf(table_Data.get("SR_NO")) + "~" + (String) table_Data.get("ACCOUNT_NUMBER")
									+ "~" + (String) table_Data.get("PART_TRAN_TYPE") + "~");
					lineData.append(String.valueOf(table_Data.get("TRANSACTION_AMOUNT")) + "~"
							+ (String) table_Data.get("TRANSACTION_PARTICULAR"));
				} else {
					lineData.append(String.valueOf(table_Data.get("SR_NO")) + "~"
							+ ((String) table_Data.get("ACCOUNT_NUMBER")).trim() + "~"
							+ ((String) table_Data.get("PART_TRAN_TYPE")).trim() + "~");
					lineData.append(String.valueOf(((String) table_Data.get("TRANSACTION_AMOUNT")).trim()) + "~"
							+ ((String) table_Data.get("TRANSACTION_PARTICULAR")).trim());
				}
				out.write(lineData.toString());
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);
		}
	}

	public void generateMultipleTTUMFiles(String stPath, String FileName, int fileCount, List<Object> TTUMData) {
		try {
			for (int j = 1; j <= fileCount; j++) {
				List<Object> lstData = (ArrayList) TTUMData.get(j - 1);
				String newFileName = FileName.replaceAll(".txt", "_" + j + ".txt");
				File file = new File(String.valueOf(stPath) + File.separator + newFileName);
				if (file.exists())
					FileUtils.forceDelete(file);
				file.createNewFile();
				BufferedWriter out = new BufferedWriter(
						new FileWriter(String.valueOf(stPath) + File.separator + newFileName, true));
				int startLine = 0;
				for (int i = 0; i < lstData.size(); i++) {
					Map<String, String> table_Data = (Map<String, String>) lstData.get(i);
					if (startLine > 0)
						out.write("\n");
					startLine++;
					StringBuffer lineData = new StringBuffer();
					lineData.append(String.valueOf(table_Data.get("ACCOUNT_NUMBER")) + "  " + "INR1735" + "    "
							+ (String) table_Data.get("PART_TRAN_TYPE"));
					lineData.append(String.valueOf(table_Data.get("TRANSACTION_AMOUNT"))
							+ (String) table_Data.get("TRANSACTION_PARTICULAR"));
					lineData.append(
							"                                                                                                       ");
					lineData.append(table_Data.get("FILEDATE"));
					out.write(lineData.toString());
				}
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			logger.info("Exception in generateMultipleTTUMFiles " + e);
		}
	}

	public void generateCsvSurcharge(String stPath, String FileName, int fileCount, List<Object> TTUMData,
			String TTUMName) {
		try {
			for (int j = 1; j <= fileCount; j++) {
				List<Object> lstData = (ArrayList) TTUMData.get(j - 1);
				String newFileName = FileName.replaceAll(".txt", "_" + j + ".txt");
				File file = new File(String.valueOf(stPath) + File.separator + newFileName);
				if (file.exists())
					FileUtils.forceDelete(file);
				file.createNewFile();
				BufferedWriter out = new BufferedWriter(
						new FileWriter(String.valueOf(stPath) + File.separator + newFileName, true));
				int startLine = 0;
				for (int i = 0; i < lstData.size(); i++) {
					Map<String, String> table_Data = (Map<String, String>) lstData.get(i);
					if (startLine > 0)
						out.write("\n");
					startLine++;
					StringBuffer lineData = new StringBuffer();
					lineData.append(String.valueOf(table_Data.get("PREDEFINED")) + ","
							+ (String) table_Data.get("PREDEFINED_1") + "," + (String) table_Data.get("PREDEFINED_2")
							+ "," + (String) table_Data.get("SERVICE_OUTLET") + ","
							+ (String) table_Data.get("PART_TRAN_TYPE") + ","
							+ (String) table_Data.get("ACCOUNT_NUMBER") + ","
							+ (String) table_Data.get("REFERENCE_NUMBER") + ",");
					lineData.append((table_Data.get("ACQUIRER_REFERENCE_DATA") == null) ? ""
							: (String.valueOf(table_Data.get("ACQUIRER_REFERENCE_DATA")) + ","));
					lineData.append(String.valueOf(table_Data.get("TRANSACTION_PARTICULAR")) + ","
							+ (String) table_Data.get("TRANSACTION_AMOUNT"));
					out.write(lineData.toString());
				}
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			logger.info("Exception in generateCSV " + e);
		}
	}

	public void generateADJTTUMFilesRUPAYRUFUND(String stPath, String FileName, int fileCount, List<Object> TTUMData,
			String TTUMName, UnMatchedTTUMBean beanObj) {
		try {
			File file = new File(String.valueOf(stPath) + File.separator + FileName);
			if (file.exists())
				FileUtils.forceDelete(file);
			file.createNewFile();
			BufferedWriter out = new BufferedWriter(
					new FileWriter(String.valueOf(stPath) + File.separator + FileName, true));
			int startLine = 0;
			for (int i = 0; i < TTUMData.size(); i++) {
				Map<String, String> table_Data = (Map<String, String>) TTUMData.get(i);
				if (startLine > 0)
					out.write("\n");
				startLine++;
				StringBuffer lineData = new StringBuffer();
				lineData.append(table_Data.get("TTUM"));
				out.write(lineData.toString());
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);
		}
	}

	public void generateADJTTUMFilesMASTERCARDRUFUND(String stPath, String FileName, int fileCount,
			List<Object> TTUMData, String TTUMName, MastercardUploadBean beanObj) {
		try {
			File file = new File(String.valueOf(stPath) + File.separator + FileName);
			if (file.exists())
				FileUtils.forceDelete(file);
			file.createNewFile();
			BufferedWriter out = new BufferedWriter(
					new FileWriter(String.valueOf(stPath) + File.separator + FileName, true));
			int startLine = 0;
			for (int i = 0; i < TTUMData.size(); i++) {
				Map<String, String> table_Data = (Map<String, String>) TTUMData.get(i);
				if (startLine > 0)
					out.write("\n");
				startLine++;
				StringBuffer lineData = new StringBuffer();
				lineData.append(table_Data.get("TTUM"));
				out.write(lineData.toString());
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);
		}
	}

	public void generateADJTTUMFiles(String stPath, String FileName, int fileCount, List<Object> TTUMData,
			String TTUMName, NFSSettlementBean beanObj) {
		try {
			File file = new File(String.valueOf(stPath) + File.separator + FileName);
			if (file.exists())
				FileUtils.forceDelete(file);
			file.createNewFile();
			BufferedWriter out = new BufferedWriter(
					new FileWriter(String.valueOf(stPath) + File.separator + FileName, true));
			int startLine = 0;
			for (int i = 0; i < TTUMData.size(); i++) {
				Map<String, String> table_Data = (Map<String, String>) TTUMData.get(i);
				if (startLine > 0)
					out.write("\n");
				startLine++;
				StringBuffer lineData = new StringBuffer();
				lineData.append(table_Data.get("TTUM"));
				out.write(lineData.toString());
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);
		}
	}

	public void generateNFSRECONACQTTUM(String stPath, String FileName, int fileCount, List<Object> TTUMData,
			String TTUMName, UnMatchedTTUMBean beanObj) {
		try {
			File file = new File(String.valueOf(stPath) + File.separator + FileName);
			if (file.exists())
				FileUtils.forceDelete(file);
			file.createNewFile();
			BufferedWriter out = new BufferedWriter(
					new FileWriter(String.valueOf(stPath) + File.separator + FileName, true));
			int startLine = 0;
			for (int i = 0; i < TTUMData.size(); i++) {
				Map<String, String> table_Data = (Map<String, String>) TTUMData.get(i);
				if (startLine > 0)
					out.write("\n");
				startLine++;
				StringBuffer lineData = new StringBuffer();
				lineData.append(table_Data.get("TTUM"));
				out.write(lineData.toString());
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);
		}
	}

	public void generateADJTTUMFilesRupay(String stPath, String FileName, int fileCount, List<Object> TTUMData,
			String TTUMName, RupayUploadBean beanObj) {
		try {
			File file = new File(String.valueOf(stPath) + File.separator + FileName);
			if (file.exists())
				FileUtils.forceDelete(file);
			file.createNewFile();
			BufferedWriter out = new BufferedWriter(
					new FileWriter(String.valueOf(stPath) + File.separator + FileName, true));
			int startLine = 0;
			for (int i = 0; i < TTUMData.size(); i++) {
				Map<String, String> table_Data = (Map<String, String>) TTUMData.get(i);
				if (startLine > 0)
					out.write("\n");
				startLine++;
				StringBuffer lineData = new StringBuffer();
				lineData.append(table_Data.get("TTUM"));
				out.write(lineData.toString());
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);
		}
	}

	public void generateREFUNDTTUM(String stPath, String FileName, int fileCount, List<Object> TTUMData,
			String TTUMName, UnMatchedTTUMBean beanObj) {
		try {
			File file = new File(String.valueOf(stPath) + File.separator + FileName);
			if (file.exists())
				FileUtils.forceDelete(file);
			file.createNewFile();
			BufferedWriter out = new BufferedWriter(
					new FileWriter(String.valueOf(stPath) + File.separator + FileName, true));
			int startLine = 0;
			for (int i = 0; i < TTUMData.size(); i++) {
				Map<String, String> table_Data = (Map<String, String>) TTUMData.get(i);
				if (startLine > 0)
					out.write("\n");
				startLine++;
				StringBuffer lineData = new StringBuffer();
				lineData.append(table_Data.get("TTUM"));
				out.write(lineData.toString());
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);
		}
	}

	public void generateMultipleDRMTTUMFiles(String stPath, String FileName, int fileCount, List<Object> TTUMData,
			String TTUMName) {
		List<String> files = new ArrayList<>();
		System.out.println("inside the next ");
		try {
			for (int j = 1; j <= 1; j++) {
				List<Object> lstData = (ArrayList) TTUMData.get(j - 1);
				String newFileName = FileName;
				File file = new File(String.valueOf(stPath) + File.separator + newFileName);
				if (file.exists())
					FileUtils.forceDelete(file);
				file.createNewFile();
				BufferedWriter out = new BufferedWriter(
						new FileWriter(String.valueOf(stPath) + File.separator + newFileName, true));
				int startLine = 0;
				for (int i = 0; i < lstData.size(); i++) {
					Map<String, String> table_Data = (Map<String, String>) lstData.get(i);
					if (startLine > 0)
						out.write("\n");
					startLine++;
					StringBuffer lineData = new StringBuffer();
					String b = "";
					if (TTUMName.equalsIgnoreCase("RUPAY")) {
						if (((String) table_Data.get("TRANSACTION_PARTICULAR")).contains("RPY")) {
							lineData.append(String.valueOf(table_Data.get("SERVICE_OUTLET")) + "~"
									+ (String) table_Data.get("ACCOUNT_NUMBER") + "~"
									+ (String) table_Data.get("PART_TRAN_TYPE"));
							lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~"
									+ ((table_Data.get("TRANSACTION_PARTICULAR") == null) ? ""
											: table_Data.get("TRANSACTION_PARTICULAR")));
						} else if (((String) table_Data.get("TRANSACTION_PARTICULAR")).contains("RUP DECD")
								|| ((String) table_Data.get("TRANSACTION_PARTICULAR")).contains("RUP UNRECON2")) {
							String sksir = table_Data.get("FILEDATE");
							SimpleDateFormat indate = new SimpleDateFormat("yyyy-mm-dd");
							Date indate1 = indate.parse(sksir);
							SimpleDateFormat outdate = new SimpleDateFormat("dd-mm-yyyy");
							String outdate1 = outdate.format(indate1);
							System.out.println("date is passing saurav sir" + outdate1);
							lineData.append(String.valueOf(table_Data.get("SERVICE_OUTLET")) + "~" +

									(String) table_Data.get("ACCOUNT_NUMBER") + "~"
									+ (String) table_Data.get("PART_TRAN_TYPE"));
							lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~" + outdate1 + "_"
									+ ((table_Data.get("TRANSACTION_PARTICULAR") == null) ? ""
											: table_Data.get("TRANSACTION_PARTICULAR")));
						} else if (((String) table_Data.get("TRANSACTION_PARTICULAR"))
								.equalsIgnoreCase("DR Rup ISS CHBK RAISE IMPL A/C")) {
							lineData.append(String.valueOf(table_Data.get("ACC_TYPE")) + "~"
									+ (String) table_Data.get("ACCOUNT_NUMBER") + "~"
									+ (String) table_Data.get("PART_TRAN_TYPE"));
							lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~" + (

							(table_Data.get("TRANSACTION_PARTICULAR") == null) ? ""
									: table_Data.get("TRANSACTION_PARTICULAR")) + "_"
									+ (String) table_Data.get("FILEDATE"));
						} else if (((String) table_Data.get("TRANSACTION_PARTICULAR"))
								.equalsIgnoreCase("RUP ECOM CHBK ACPT")) {
							lineData.append(String.valueOf(table_Data.get("ACC_TYPE")) + "~"
									+ (String) table_Data.get("ACCOUNT_NUMBER") + "~"
									+ (String) table_Data.get("PART_TRAN_TYPE"));
							lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~" + (

							(table_Data.get("DISP_DATE") == null) ? "" : table_Data.get("DISP_DATE")) + "_" + (

							(table_Data.get("TRANSACTION_PARTICULAR") == null) ? ""
									: table_Data.get("TRANSACTION_PARTICULAR")) + "_"
									+ ((table_Data.get("REMARKS") == null) ? "" : table_Data.get("REMARKS")) + "_"
									+ (String) table_Data.get("FILEDATE"));
						} else if (((String) table_Data.get("TRANSACTION_PARTICULAR")).contains("CHBK RAISE")
								|| ((String) table_Data.get("TRANSACTION_PARTICULAR")).equalsIgnoreCase("CHB ACPT")
								|| ((String) table_Data.get("TRANSACTION_PARTICULAR")).equalsIgnoreCase("CHBK REP")) {
							lineData.append(String.valueOf(table_Data.get("ACC_TYPE")) + "~"
									+ (String) table_Data.get("ACCOUNT_NUMBER") + "~"
									+ (String) table_Data.get("PART_TRAN_TYPE"));
							lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~" + (

							(table_Data.get("DISP_DATE") == null) ? "" : table_Data.get("DISP_DATE")) + "_"
									+ ((table_Data.get("TRANSACTION_PARTICULAR") == null) ? ""
											: table_Data.get("TRANSACTION_PARTICULAR"))
									+ "_" + ((table_Data.get("REMARKS") == null) ? "" : table_Data.get("REMARKS")) + "_"
									+ (String) table_Data.get("FILEDATE") + "~"
									+ ((table_Data.get("UNQ_ID") == null) ? "" : table_Data.get("UNQ_ID")));
						} else if (((String) table_Data.get("TRANSACTION_PARTICULAR"))
								.equalsIgnoreCase("DR Rup ISS CHBK RAISE IMPL A/C")
								|| ((String) table_Data.get("TRANSACTION_PARTICULAR"))
										.equalsIgnoreCase("DR Rup Iss CHBK REP Impl A/C")
								|| ((String) table_Data.get("TRANSACTION_PARTICULAR"))
										.equalsIgnoreCase("RUP ECOM VOID REFUND")) {
							lineData.append(String.valueOf(table_Data.get("ACC_TYPE")) + "~"
									+ (String) table_Data.get("ACCOUNT_NUMBER") + "~"
									+ (String) table_Data.get("PART_TRAN_TYPE"));
							lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~"
									+ (String) table_Data.get("TRANSACTION_PARTICULAR") +

									"_" + (String) table_Data.get("DISP_DATE"));
						} else if (!((String) table_Data.get("ACC_TYPE")).equalsIgnoreCase(null)) {
							lineData.append(String.valueOf(table_Data.get("ACC_TYPE")) + "~"
									+ (String) table_Data.get("ACCOUNT_NUMBER") + "~"
									+ (String) table_Data.get("PART_TRAN_TYPE"));
							lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~"
									+ ((table_Data.get("TRANSACTION_PARTICULAR") == null) ? ""
											: table_Data.get("TRANSACTION_PARTICULAR"))
									+ "_" + ((table_Data.get("REMARKS") == null) ? "" : table_Data.get("REMARKS")) + "_"
									+ (String) table_Data.get("DISP_DATE"));
						} else if (((String) table_Data.get("TRANSACTION_PARTICULAR"))
								.equalsIgnoreCase("RUP ECOM CHBK ACPT")
								|| ((String) table_Data.get("TRANSACTION_PARTICULAR"))
										.equalsIgnoreCase("RUP ECOM PRE ARB ACP")
								|| ((String) table_Data.get("TRANSACTION_PARTICULAR"))
										.equalsIgnoreCase("RUP ECOM VOID REFUND CR")) {
							lineData.append(String.valueOf(table_Data.get("ACC_TYPE")) + "~"
									+ (String) table_Data.get("ACCOUNT_NUMBER") + "~"
									+ (String) table_Data.get("PART_TRAN_TYPE"));
							lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + " "
									+ (String) table_Data.get("DISP_DATE") + " "
									+ ((table_Data.get("TRANSACTION_PARTICULAR") == null) ? ""
											: table_Data.get("TRANSACTION_PARTICULAR"))
									+ " " + (String) table_Data.get("REMARKS") + "-"
									+ (String) table_Data.get("FILEDATE"));
						} else if (((String) table_Data.get("TRANSACTION_PARTICULAR"))
								.equalsIgnoreCase("RUP ECOM CHB ACPT")
								|| ((String) table_Data.get("TRANSACTION_PARTICULAR"))
										.equalsIgnoreCase("RUP ECOM PRE ARB ACPT")) {
							lineData.append(String.valueOf(table_Data.get("ACC_TYPE")) + "~"
									+ (String) table_Data.get("ACCOUNT_NUMBER") + "~"
									+ (String) table_Data.get("PART_TRAN_TYPE"));
							lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~"
									+ (String) table_Data.get("DISP_DATE") + " "
									+ ((table_Data.get("TRANSACTION_PARTICULAR") == null) ? ""
											: table_Data.get("TRANSACTION_PARTICULAR"))
									+ " " + (String) table_Data.get("REMARKS") + "-"
									+ (String) table_Data.get("TXN_SETTLEMENT_DATE") + "~"
									+ (String) table_Data.get("UNQ_ID"));
						} else {
							lineData.append(String.valueOf(table_Data.get("ACC_TYPE")) + "~"
									+ (String) table_Data.get("ACCOUNT_NUMBER") + "~"
									+ (String) table_Data.get("PART_TRAN_TYPE"));
							lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~"
									+ ((table_Data.get("TRANSACTION_PARTICULAR") == null) ? ""
											: table_Data.get("TRANSACTION_PARTICULAR"))
									+ "_" + (String) table_Data.get("DISP_DATE"));
						}
					} else if (TTUMName.equalsIgnoreCase("NFS")) {
						if (FileName.contains("NFS-EOD")) {
							lineData.append(String.valueOf(table_Data.get("ACCCOUNT_TYPE_1")) + "~"
									+ (String) table_Data.get("ACCOUNT_NUMBER") + "~"
									+ (String) table_Data.get("PART_TRAN_TYPE"));
							lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~"
									+ ((table_Data.get("TRANSACTION_PARTICULAR") == null) ? ""
											: table_Data.get("TRANSACTION_PARTICULAR"))
									+ "_" + (String) table_Data.get("ADJTYPE") + "_C"
									+ (String) table_Data.get("REMARKS"));
							System.out.println("FILEDATE CHECKING" + (String) table_Data.get("TRANSACTION_PARTICULAR"));
						} else {
							lineData.append(String.valueOf(table_Data.get("DR/CR")) + "~"
									+ (String) table_Data.get("ACQ_BANK") + "~" + (String) table_Data.get("TXNDATE"));
							lineData.append("~" + (String) table_Data.get("RRN") + "~"
									+ ((table_Data.get("CARD_NO") == null) ? "" : table_Data.get("CARD_NO")));
							lineData.append(String.valueOf(table_Data.get("ADJAMOUNT")) + "~"
									+ (String) table_Data.get("ACCOUNTNO") + "~"
									+ (String) table_Data.get("NARRATION"));
							if (!((String) table_Data.get("ACCCOUNT_TYPE_1")).equalsIgnoreCase("24")) {
								lineData.append(String.valueOf(table_Data.get("ACCCOUNT_TYPE_1")) + "~"
										+ (String) table_Data.get("ACCOUNT_NUMBER") + "~"
										+ (String) table_Data.get("PART_TRAN_TYPE"));
								lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~"
										+ ((table_Data.get("TRANSACTION_PARTICULAR") == null) ? ""
												: table_Data.get("TRANSACTION_PARTICULAR")));
							} else {
								lineData.append(String.valueOf(table_Data.get("ACCCOUNT_TYPE_1")) + "~"
										+ (String) table_Data.get("ACCOUNT_NUMBER") + "~"
										+ (String) table_Data.get("PART_TRAN_TYPE"));
								lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~"
										+ ((table_Data.get("TRANSACTION_PARTICULAR") == null) ? ""
												: table_Data.get("TRANSACTION_PARTICULAR")));
							}
						}
					} else if (TTUMName.equalsIgnoreCase("fund")) {
						lineData.append(table_Data.get("data"));
					} else {
						lineData.append(String.valueOf(table_Data.get("SERVICE_OUTLET")) + "~"
								+ (String) table_Data.get("ACCOUNT_NUMBER") + "~"
								+ (String) table_Data.get("PART_TRAN_TYPE"));
						lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~"
								+ ((table_Data.get("TRANSACTION_PARTICULAR") == null) ? ""
										: table_Data.get("TRANSACTION_PARTICULAR")));
					}
					out.write(lineData.toString());
				}
				out.flush();
				out.close();
				files.add(String.valueOf(stPath) + File.separator + newFileName);
			}
		} catch (Exception e) {
			logger.info("Exception in generateMultipleDRMTTUMFiles " + e);
		}
	}

	public void generateDRMTTUM(String stPath, String FileName, List<Object> TTUMData, String TTUMName) {
		try {
			File file = new File(String.valueOf(stPath) + File.separator + FileName);
			if (file.exists())
				FileUtils.forceDelete(file);
			file.createNewFile();
			BufferedWriter out = new BufferedWriter(
					new FileWriter(String.valueOf(stPath) + File.separator + FileName, true));
			int startLine = 0;
			for (int i = 0; i < TTUMData.size(); i++) {
				Map<String, String> table_Data = (Map<String, String>) TTUMData.get(i);
				if (startLine > 0)
					out.write("\n");
				startLine++;
				StringBuffer lineData = new StringBuffer();
				lineData.append(String.valueOf(table_Data.get("SERVICE_OUTLET")) + "~"
						+ (String) table_Data.get("ACCOUNT_NUMBER") + "~" + (String) table_Data.get("PART_TRAN_TYPE"));
				lineData.append("~" + (String) table_Data.get("TRANSACTION_AMOUNT") + "~"
						+ (String) table_Data.get("TRANSACTION_PARTICULAR") + "~" + TTUMName);
				out.write(lineData.toString());
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);
		}
	}

	public void generateExcelTTUM(String stPath, String FileName, List<Object> ExcelData, String date, String zipName) {

		StringBuffer lineData;
		List<String> files = new ArrayList<>();
		FileInputStream fis;
		try {

			logger.info("Filename is " + FileName);
			List<Object> TTUMData = (List<Object>) ExcelData.get(1);
			System.out.println("TTUM DATA IS NULL??");
			List<String> Excel_Headers = (List<String>) ExcelData.get(0);
			String data[] = date.split("_");

			String filenames = data[0];

			String filedate = data[1];
			// if(TTUMData.size() > 0 ) {

			List<Object> Data;

			OutputStream fileOut = new FileOutputStream(stPath + File.separator + FileName);

			SXSSFWorkbook workbook = new SXSSFWorkbook();
			System.out.println("user dir " + System.getProperty("user.dir"));
			FileInputStream fiss = new FileInputStream("src/main/webapp/dist/img/logo_recon.png");
			byte[] bytes = org.apache.poi.util.IOUtils.toByteArray(fiss);
			int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			fiss.close();
			SXSSFCreationHelper helper = (SXSSFCreationHelper) workbook.getCreationHelper();
			// create sheet
			double newWidth = 700.0;
			double newheight = 300.0;
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
			int originalwidth = image.getWidth();
			int originalHeight = image.getHeight();

			double widthFactor = newWidth / originalwidth;
			double heightFactory = newheight / originalHeight;

			for (int record_count = 0; record_count < TTUMData.size(); record_count++) {
				Data = (List<Object>) TTUMData.get(record_count);
				SXSSFSheet sheet = workbook.createSheet("Report" + record_count);
				Drawing drawing = sheet.createDrawingPatriarch();
				// add a picture shape
				XSSFClientAnchor anchor = (XSSFClientAnchor) helper.createClientAnchor();
				// set top-left corner of the picture,
				// subsequent call of Picture#resize() will operate relative to it
				anchor.setCol1(0);
				anchor.setRow1(0);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				// auto-size picture relative to its top-left cor
				/*
				 * Row row =sheet.createRow(rawIndex); row.createCell(0);
				 * row.createCell(record_count)
				 */
				System.out.println(" heightFactory " + heightFactory + "widthFactor " + widthFactor);
				pict.resize(widthFactor, heightFactory);

				SXSSFRow titlerow = sheet.createRow(0);
				SXSSFCell titleCell = titlerow.createCell(0);
				titlerow.setHeightInPoints(80);

				titleCell.setCellValue(filenames + "_Report_Voucher_Dated_" + filedate + "");

				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
				CellStyle titleStyle = workbook.createCellStyle();
				org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
				titleFont.setBold(true);
				titleFont.setColor(IndexedColors.RED.getIndex());
				titleFont.setFontHeightInPoints((short) 15);
				titleStyle.setFont(titleFont);
				titleStyle.setAlignment(HorizontalAlignment.CENTER);
				titleCell.setCellStyle(titleStyle);

				/*
				 * HSSFRow titlerow2 = sheet.RconRreateRow(1); HSSFCell titleCell2 =
				 * titlerow2.createCell(1); titlerow2.setHeightInPoints(20);
				 * 
				 * titleCell2.setCellValue("EXCEL NAME - "+FileName.replace(".xls", "")); //
				 * sheet.addMergedRegion(new CellRangeAddress(0,0,0,13)); HSSFCellStyle
				 * titleStyle2 = workbook.createCellStyle(); HSSFFont titleFont2 =
				 * workbook.createFont(); titleFont2.setBold(true);
				 * titleFont2.setColor(IndexedColors.BLACK.getIndex());
				 * titleFont2.setFontHeightInPoints((short) 12);
				 * titleStyle2.setFont(titleFont2);
				 * titleStyle2.setAlignment(HorizontalAlignment.CENTER);
				 * titleCell2.setCellStyle(titleStyle2);
				 * 
				 * 
				 */
				SXSSFRow header = sheet.createRow(1);

				CellStyle headerStyle = workbook.createCellStyle();
				headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
				headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

				// create header row

				for (int i = 0; i < Excel_Headers.size(); i++) {

					SXSSFCell headerCell = header.createCell(i);

					headerCell.setCellValue(Excel_Headers.get(i));

					headerCell.setCellStyle(headerStyle);

				}

				SXSSFRow rowEntry;

				for (int i = 0; i < Data.size(); i++) {
					rowEntry = sheet.createRow(i + 2);
					Map<String, String> map_data = (Map<String, String>) Data.get(i);
					if (map_data.size() > 0) {

						for (int m = 0; m < Excel_Headers.size(); m++) {

							rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers.get(m)));
						}
					}

				}

			}

			workbook.write(fileOut);
			fileOut.close();

			File file = new File(stPath);
			String[] filelist = file.list();

			for (String Names : filelist) {
				// logger.info("Files name is " + Names);
				files.add(stPath + File.separator + Names);
			}
			logger.info("Before zipping all files zipname is " + zipName);
			// FileOutputStream fos = new
			// FileOutputStream(stPath+File.separator+ "EXCEL_TTUMS.zip");
			FileOutputStream fos = new FileOutputStream(stPath + File.separator + zipName);
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
				fos.close();
				System.out.println("1............Done... Zipped the files...");
			} catch (Exception fe) {
				System.out.println("Exception in zipping is " + fe);
			}
			System.out.println("Zipping completed..............");
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);

		}

	}

	public void generateExcelTTUMSettlement(String stPath, String FileName, List<Object> ExcelData, String date,
			String zipName) {

		StringBuffer lineData;
		List<String> files = new ArrayList<>();
		FileInputStream fis;
		try {

			logger.info("Filename is " + FileName);
			List<Object> TTUMData = (List<Object>) ExcelData.get(1);
			System.out.println("TTUM DATA IS NULL??");
			List<String> Excel_Headers = (List<String>) ExcelData.get(0);
			String data[] = date.split("_");
			String cycle = "";
			String filenames = data[0];

			String filedate = data[1];
			if (filenames.equalsIgnoreCase("NFS")) {
				cycle = data[2];
			}
			// if(TTUMData.size() > 0 ) {

			List<Object> Data;

			OutputStream fileOut = new FileOutputStream(stPath + File.separator + FileName);

			SXSSFWorkbook workbook = new SXSSFWorkbook();
			System.out.println("user dir " + System.getProperty("user.dir"));
			FileInputStream fiss = new FileInputStream("src/main/webapp/dist/img/logo_recon.png");
			byte[] bytes = org.apache.poi.util.IOUtils.toByteArray(fiss);
			int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			fiss.close();
			SXSSFCreationHelper helper = (SXSSFCreationHelper) workbook.getCreationHelper();
			// create sheet
			double newWidth = 700.0;
			double newheight = 300.0;
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
			int originalwidth = image.getWidth();
			int originalHeight = image.getHeight();

			double widthFactor = newWidth / originalwidth;
			double heightFactory = newheight / originalHeight;

			for (int record_count = 0; record_count < TTUMData.size(); record_count++) {
				Data = (List<Object>) TTUMData.get(record_count);
				SXSSFSheet sheet = workbook.createSheet("Report" + record_count);
				Drawing drawing = sheet.createDrawingPatriarch();
				// add a picture shape
				XSSFClientAnchor anchor = (XSSFClientAnchor) helper.createClientAnchor();
				// set top-left corner of the picture,
				// subsequent call of Picture#resize() will operate relative to it
				anchor.setCol1(0);
				anchor.setRow1(0);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				// auto-size picture relative to its top-left cor
				/*
				 * Row row =sheet.createRow(rawIndex); row.createCell(0);
				 * row.createCell(record_count)
				 */
				System.out.println(" heightFactory " + heightFactory + "widthFactor " + widthFactor);
				pict.resize(widthFactor, heightFactory);

				SXSSFRow titlerow = sheet.createRow(0);
				SXSSFCell titleCell = titlerow.createCell(0);
				titlerow.setHeightInPoints(80);

				
				if (filenames.equalsIgnoreCase("NFS")) {
					titleCell.setCellValue(filenames + "_Report_Voucher_Dated_" + filedate + "_Settlement_" + cycle + "C");
				} else {
					titleCell.setCellValue(filenames + "_Report_Voucher_Dated_" + filedate + "_Settlement");
				}
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
				CellStyle titleStyle = workbook.createCellStyle();
				org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
				titleFont.setBold(true);
				titleFont.setColor(IndexedColors.RED.getIndex());
				titleFont.setFontHeightInPoints((short) 15);
				titleStyle.setFont(titleFont);
				titleStyle.setAlignment(HorizontalAlignment.CENTER);
				titleCell.setCellStyle(titleStyle);

				/*
				 * HSSFRow titlerow2 = sheet.RconRreateRow(1); HSSFCell titleCell2 =
				 * titlerow2.createCell(1); titlerow2.setHeightInPoints(20);
				 * 
				 * titleCell2.setCellValue("EXCEL NAME - "+FileName.replace(".xls", "")); //
				 * sheet.addMergedRegion(new CellRangeAddress(0,0,0,13)); HSSFCellStyle
				 * titleStyle2 = workbook.createCellStyle(); HSSFFont titleFont2 =
				 * workbook.createFont(); titleFont2.setBold(true);
				 * titleFont2.setColor(IndexedColors.BLACK.getIndex());
				 * titleFont2.setFontHeightInPoints((short) 12);
				 * titleStyle2.setFont(titleFont2);
				 * titleStyle2.setAlignment(HorizontalAlignment.CENTER);
				 * titleCell2.setCellStyle(titleStyle2);
				 * 
				 * 
				 */
				SXSSFRow header = sheet.createRow(1);

				CellStyle headerStyle = workbook.createCellStyle();
				headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
				headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

				// create header row

				for (int i = 0; i < Excel_Headers.size(); i++) {

					SXSSFCell headerCell = header.createCell(i);

					headerCell.setCellValue(Excel_Headers.get(i));

					headerCell.setCellStyle(headerStyle);

				}

				SXSSFRow rowEntry;

				for (int i = 0; i < Data.size(); i++) {
					rowEntry = sheet.createRow(i + 2);
					Map<String, String> map_data = (Map<String, String>) Data.get(i);
					if (map_data.size() > 0) {

						for (int m = 0; m < Excel_Headers.size(); m++) {

							rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers.get(m)));
						}
					}

				}

			}

			workbook.write(fileOut);
			fileOut.close();

			File file = new File(stPath);
			String[] filelist = file.list();

			for (String Names : filelist) {
				// logger.info("Files name is " + Names);
				files.add(stPath + File.separator + Names);
			}
			logger.info("Before zipping all files zipname is " + zipName);
			// FileOutputStream fos = new
			// FileOutputStream(stPath+File.separator+ "EXCEL_TTUMS.zip");
			FileOutputStream fos = new FileOutputStream(stPath + File.separator + zipName);
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
				fos.close();
				System.out.println("1............Done... Zipped the files...");
			} catch (Exception fe) {
				System.out.println("Exception in zipping is " + fe);
			}
			System.out.println("Zipping completed..............");
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);

		}

	}

	public void generateExcelTTUMReport(String stPath, String FileName, List<Object> ExcelData, String TTUMName,
			String zipName) {
		List<String> files = new ArrayList<>();
		try {
			logger.info("Filename is " + FileName);
			List<Object> TTUMData = (List<Object>) ExcelData.get(1);
			System.out.println("TTUM DATA IS NULL??");
			List<String> Excel_Headers = (List<String>) ExcelData.get(0);
			OutputStream fileOut = new FileOutputStream(String.valueOf(stPath) + File.separator + FileName);
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			for (int record_count = 0; record_count < TTUMData.size(); record_count++) {
				List<Object> Data = (List<Object>) TTUMData.get(record_count);
				SXSSFSheet sheet = workbook.createSheet("Report" + record_count);
				SXSSFRow titlerow = sheet.createRow(0);
				SXSSFCell titleCell = titlerow.createCell(0);
				titlerow.setHeightInPoints(40.0F);
				titleCell.setCellValue("PUNJAB NATIONAL BANK");
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
				CellStyle titleStyle = workbook.createCellStyle();
				Font titleFont = workbook.createFont();
				titleFont.setBold(true);
				titleFont.setColor(IndexedColors.RED.getIndex());
				titleFont.setFontHeightInPoints((short) 15);
				titleStyle.setFont(titleFont);
				titleStyle.setAlignment(HorizontalAlignment.CENTER);
				titleCell.setCellStyle(titleStyle);
				SXSSFRow header = sheet.createRow(1);
				for (int j = 0; j < Excel_Headers.size(); j++) {
					SXSSFCell headerCell = header.createCell(j);
					headerCell.setCellValue(Excel_Headers.get(j));
				}
				for (int k = 0; k < Data.size(); k++) {
					SXSSFRow rowEntry = sheet.createRow(k + 2);
					Map<String, String> map_data = (Map<String, String>) Data.get(k);
					if (map_data.size() > 0)
						for (int m = 0; m < Excel_Headers.size(); m++)
							rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers.get(m)));
				}
			}
			workbook.write(fileOut);
			fileOut.close();
			File file = new File(stPath);
			String[] filelist = file.list();
			byte b;
			int i;
			String[] arrayOfString1;
			for (i = (arrayOfString1 = filelist).length, b = 0; b < i;) {
				String Names = arrayOfString1[b];
				files.add(String.valueOf(stPath) + File.separator + Names);
				b++;
			}
			logger.info("Before zipping all files zipname is " + zipName);
			FileOutputStream fos = new FileOutputStream(
					String.valueOf(stPath) + File.separator + zipName + File.separator);
			ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
			try {
				for (String filespath : files) {
					File input = new File(filespath);
					FileInputStream fis = new FileInputStream(input);
					ZipEntry ze = new ZipEntry(input.getName());
					System.out.println("Zipping the file: " + input.getName());
					zipOut.putNextEntry(ze);
					byte[] tmp = new byte[4096];
					int size = 0;
					while ((size = fis.read(tmp)) != -1)
						zipOut.write(tmp, 0, size);
					zipOut.flush();
					fis.close();
				}
				zipOut.close();
				fos.close();
				System.out.println("1............Done... Zipped the files...");
			} catch (Exception fe) {
				System.out.println("Exception in zipping is " + fe);
			}
			System.out.println("Zipping completed..............");
		} catch (Exception e) {
			logger.info("Exception in generateTTUMFile " + e);
		}
	}
}
