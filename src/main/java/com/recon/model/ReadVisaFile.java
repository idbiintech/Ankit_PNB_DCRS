package com.recon.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.recon.util.OracleConn;

public class ReadVisaFile {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private static final Logger logger = Logger.getLogger(ReadVisaFile.class);

	public static final String OUTPUT_FOLDER = System.getProperty("catalina.home") + File.separator + "OUTPUT_FOLDER";

	public boolean readData(CompareSetupBean setupBean, Connection conn, MultipartFile file,
			FileSourceBean sourceBean) {
		logger.info("***** ReadVisaFile.readData Start ****");
		InputStream fis = null;
		String check_TCR = "";

		String SOURCE_AMT = "NA";
		String DATE = "";
		String REQ_MSGTYPE = "";
		String TRACE = "NA";
		String RESPONSE_CODE = "NA";
		String REFERENCE_NUMBER = "NA";
		String CARD_NUMBER = "NA";
		String FPAN = "NA";
		String REQ_MSG_TYPE = "";
		String TRAN_ID = "";
		String CutCODE = "NA";
		String transacITe = "NA", POS_ENTRY_MODE = "";
		String stTable_Name = "", dummyrrn = "NA", DATE2 = "NA", MESSAGE_TEXT = "NA";
		String Mcuntrycode = "NA", ARN = "", MName = "NA", location = "", MCC = "NA";
		ArrayList<String> Amount_translation = new ArrayList<String>();
		Resource resource = new ClassPathResource("/resources/switchpr.asc");

		TCRFile tcrFileObj = new TCRFile();

		BufferedReader br = null;
		String thisLine = null, destination_code = null;
		int lineNumber = 0;

		try {

			String fileNameWithExt = file.getOriginalFilename();
			int lastDotIndex = fileNameWithExt.lastIndexOf(".");
			String fileNameWithoutExt = "";
			if (lastDotIndex > 0) {
				fileNameWithoutExt = fileNameWithExt.substring(0, lastDotIndex);
			}
			File folder = new File(OUTPUT_FOLDER);

			if (folder.exists()) {
				folder.delete();
			}
			folder.mkdir();

			File file1 = new File(folder, fileNameWithoutExt);

			try {
				if (file1.exists()) {
					file1.delete();
				}
				file1.createNewFile();
			} catch (Exception e) {
				System.out.println(e);
			}

			String newPath = OUTPUT_FOLDER + File.separator + fileNameWithoutExt;

			System.out.println("File to be write at path: " + newPath);

			File outFile = new File(newPath);

			System.out.println("File reading from Path: " + newPath);
			System.out.println("File reading : " + outFile);

			br = new BufferedReader(new InputStreamReader(file.getInputStream()));

			logger.info("Reading data " + new Date().toString());

			String CHECK_TABLE = "", update_TABLE = "";

			CHECK_TABLE = "SELECT count(*) FROM tab WHERE tname  = 'VISA_INPUT_FILE'";

			logger.info("CHECK_TABLE==" + CHECK_TABLE);
			PreparedStatement pstmt1 = conn.prepareStatement(CHECK_TABLE);
			update_TABLE = "update  visa_acq_rawdata set MERCHANT_NAME=?,MERCHANT_COUNTRY_CODE=?,MERCHANT_CATEGORY_CODE=?, where DCRS_SEQ_NO=?";

			logger.info("update_TABLE==" + update_TABLE);
			PreparedStatement pstmt4 = conn.prepareStatement(update_TABLE);
			ResultSet rset1 = pstmt1.executeQuery();
			int isPresent = 0;

			if (rset1.next()) {

				isPresent = rset1.getInt(1);
			}

			if (isPresent == 0) {

				String CREATE_QUERY = "CREATE TABLE  visa_input_file (TC VARCHAR2(100 BYTE), TCR_CODE VARCHAR2(100 BYTE), STRING varchar(1000), DCRS_SEQ_NO varchar(100), FILEDATE DATE)";

				logger.info("CREATE_QUERY==" + CREATE_QUERY);
				PreparedStatement pstmt = conn.prepareStatement(CREATE_QUERY);

			}

			String stline = br.readLine();

			String DATA_INSERT_05 = "INSERT INTO visa_visa_rawdata (TC,TCR_CODE,DCRS_SEQ_NO,FILEDATE,PART_ID,CARD_NUMBER,Floor_Limit_indi,ARN,Acquirer_Busi_ID,Purchase_Date,Destination_Amount,Destination_Curr_Code,SOURCE_AMOUNT,Source_Curr_Code,Merchant_Name,Merchant_City,Merchant_Country_Code,Merchant_Category_Code,Merchant_ZIP_Code,Usage_Code,Reason_Code,Settlement_Flag,Auth_Chara_Ind,AUTHORIZATION_CODE,POS_Terminal_Capability,Cardholder_ID_Method,Collection_Only_Flag,POS_Entry_Mode,Central_Process_Date,Reimbursement_Attr,FPAN,TRAN_ID,FILENAME)  VALUES(?,?,?,str_to_date(?,'%Y/%m/%d'),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String DATA_INSERT_10 = "INSERT INTO  visa_visa_rawdata (TC,TCR_CODE,DCRS_SEQ_NO,FILEDATE,PART_ID,Destination_BIN,Source_BIN,Reason_Code,Country_Code,Event_Date,CARD_NUMBER,Destination_Amount,Destination_Curr_Code,SOURCE_AMOUNT,Source_Curr_Code,Message_Text,Settlement_Flag,Transac_Identifier,Central_Process_Date,Reimbursement_Attr,FPAN,FILENAME)  VALUES(?,?,?,str_to_date(?,'%Y/%m/%d'),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			PreparedStatement read_stmt_05 = conn.prepareStatement(DATA_INSERT_05);

			PreparedStatement read_stmt_10 = conn.prepareStatement(DATA_INSERT_10);

			String INSERT_33 = "INSERT INTO  visa_acq_rawdata (TC,TCR_CODE,CARD_NUMBER,SOURCE_AMOUNT,TRACE,REFERENCE_NUMBER,RESPONSE_CODE,DCRS_SEQ_NO,FILEDATE,PART_ID,FPAN,SOURCE_CURR_CODE,DESTINATION_CURR_CODE,DESTINATION_AMOUNT,TRANSAC_IDENTIFIER,MERCHANT_COUNTRY_CODE,MERCHANT_NAME,MERCHANT_CATEGORY_CODE,PURCHASE_DATE,MESSAGE_TEXT,FILENAME, REQ_MSGTYPE, POS_ENTRY_MODE,ARN) VALUES(?,?,?,?,?,?,?,?,str_to_date(?,'%Y/%m/%d'),?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?)";

			PreparedStatement pstmt33 = conn.prepareStatement(INSERT_33);

			int count = 0, count1 = 0;
			int batch = 1;
			float amt = 0.00f;
			Integer updatedseqcheckInteger = null;
			String INSERT_QUERY = "";

			INSERT_QUERY = "INSERT INTO  visa_input_file (TC, TCR_CODE, STRING,DCRS_SEQ_NO,FILEDATE) VALUES(?,?,?,?,str_to_date(?,'%Y/%m/%d'))";

			logger.info("INSERT_QUERY==" + INSERT_QUERY);
			pstmt1 = conn.prepareStatement(INSERT_QUERY);

			String seq = "";
			List<String> stRawData = new ArrayList<String>();
			while (stline != null) {
				count1++;

				if (count1 == 6) {

					location = stline.substring(75, 114);
				}
				stRawData.clear();
				boolean containsVISATable = false;

				List<String> Data_Elements = new ArrayList<String>();
				String TC = stline.substring(0, 2);
				String TCR_Code = stline.substring(2, 4);

				if (TC.equals("05") || TC.equals("06") || TC.equals("07") || TC.equals("25") || TC.equals("27")) {
					if (TCR_Code.equals("00") || TCR_Code.equals("20")) {
						Data_Elements = tcrFileObj.TCR050Format();
					}

					else if (TCR_Code.equalsIgnoreCase("05")) {
						Data_Elements = tcrFileObj.TCR0505Format();
					}
				} else if (TC.equals("10")) {
					if (TCR_Code.equals("00")) {
						Data_Elements = tcrFileObj.TCR10FeeCollectionFormat();
					}
				} else if (TC.equals("20")) {
					if (TCR_Code.equals("00")) {
						Data_Elements = tcrFileObj.TCR20FundDisbursement();
					}
				} else if (TC.equals("33"))// VISA ACQUIRER TRANSACTIONS
				{
					String stLine = stline.substring(34, stline.length());
					if (stLine.contains("VISA TABLE")) {
						containsVISATable = true;
					} else {
						check_TCR = stLine.substring(3, 6);
						if (check_TCR.equals("200")) {
							Data_Elements = tcrFileObj.V22200();
						} else if (check_TCR.equals("210")) {
							Data_Elements = tcrFileObj.V22210();
						} else if (check_TCR.equals("220")) {
							Data_Elements = tcrFileObj.V22220();
						} else if (check_TCR.equals("260")) {
							destination_code = stline.trim().substring(71, 74);
						}
					}
				}
				if (!containsVISATable) {

					for (int i = 0; i < Data_Elements.size(); i++) {
						if (TC.equals("05") || TC.equals("06") || TC.equals("07") || TC.equals("25")
								|| TC.equals("27")) {
							if (TCR_Code.equals("00") || TCR_Code.equals("20")) {

								String[] DE = Data_Elements.get(i).split("\\|");
								String stData = "";
								if (DE.length == 3) {
									int ststart_Pos = Integer.parseInt(DE[1].trim());
									int stEnd_pos = Integer.parseInt(DE[2].trim());

									if (DE[0].equalsIgnoreCase("ACCOUNT NUMBER")) {

										stData = stline.substring((ststart_Pos - 1), stEnd_pos).replaceAll("^0*", "")
												.trim();
										stRawData.add(stData);
									} else if (DE[0].equalsIgnoreCase("SOURCE AMOUNT")
											|| DE[0].equalsIgnoreCase("DESTINATION AMOUNT")) {

										SOURCE_AMT = stline.substring((ststart_Pos - 1), stEnd_pos)
												.replaceAll("^0*", "").trim();
										stData = String.valueOf((Float.parseFloat(SOURCE_AMT) / 100));
										stRawData.add(stData);

									} else if (DE[0].equalsIgnoreCase("Floor Limit Indicator")
											|| DE[0].equalsIgnoreCase("Acquirer Reference Number")
											|| DE[0].equalsIgnoreCase("Acquirer's Business ID")
											|| DE[0].equalsIgnoreCase("Purchase Date") ||
											// DE[0].equalsIgnoreCase("Destination Amount") ||
											DE[0].equalsIgnoreCase("Destination Currency Code")
											|| DE[0].equalsIgnoreCase("Source Currency Code")
											|| DE[0].equalsIgnoreCase("Merchant Name")
											|| DE[0].equalsIgnoreCase("Merchant City")
											|| DE[0].equalsIgnoreCase("Merchant Country Code")
											|| DE[0].equalsIgnoreCase("Merchant Category Code")
											|| DE[0].equalsIgnoreCase("Merchant ZIP Code")
											|| DE[0].equalsIgnoreCase("Usage Code")
											|| DE[0].equalsIgnoreCase("Reason Code")
											|| DE[0].equalsIgnoreCase("Settlement Flag")
											|| DE[0].equalsIgnoreCase("Authorization Characteristics Indicator")
											|| DE[0].equalsIgnoreCase("POS Terminal Capability")
											|| DE[0].equalsIgnoreCase("Cardholder ID Method")
											|| DE[0].equalsIgnoreCase("Collection-Only Flag")
											|| DE[0].equalsIgnoreCase("POS Entry Mode")
											|| DE[0].equalsIgnoreCase("Central Processing Date")
											|| DE[0].equalsIgnoreCase("Reimbursement Attribute")
											|| DE[0].equalsIgnoreCase("Transaction Identifier")
											|| DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))

									{

										stData = stline.substring((ststart_Pos - 1), stEnd_pos).trim();
										stRawData.add(stData);
										if (DE[0].equalsIgnoreCase("Collection-Only Flag")) {

										}
									}

								} else if (DE.length == 2) {
									int ststart_pos = Integer.parseInt(DE[1].trim());
									if (DE[0].equalsIgnoreCase("ACCOUNT NUMBER")) {

										stData = stline.substring((ststart_pos - 1), (ststart_pos))
												.replaceAll("^0*", "").trim();
										stRawData.add(stData);
									} else if (DE[0].equalsIgnoreCase("SOURCE AMOUNT")
											|| DE[0].equalsIgnoreCase("DESTINATION AMOUNT")) {
										try {

											SOURCE_AMT = stline.substring((ststart_pos - 1), (ststart_pos))
													.replaceAll("^0*", "").trim();
											stData = String.valueOf((Float.parseFloat(SOURCE_AMT) / 100));
											stRawData.add(stData);
										} catch (Exception e) {
											logger.info("Exception in visa issuer on line " + stline);
										}
									} else if (DE[0].equalsIgnoreCase("Floor Limit Indicator")
											|| DE[0].equalsIgnoreCase("Acquirer Reference Number")
											|| DE[0].equalsIgnoreCase("Acquirer's Business ID")
											|| DE[0].equalsIgnoreCase("Purchase Date") ||

											DE[0].equalsIgnoreCase("Destination Currency Code")
											|| DE[0].equalsIgnoreCase("Source Currency Code")
											|| DE[0].equalsIgnoreCase("Merchant Name")
											|| DE[0].equalsIgnoreCase("Merchant City")
											|| DE[0].equalsIgnoreCase("Merchant Country Code")
											|| DE[0].equalsIgnoreCase("Merchant Category Code")
											|| DE[0].equalsIgnoreCase("Merchant ZIP Code")
											|| DE[0].equalsIgnoreCase("Usage Code")
											|| DE[0].equalsIgnoreCase("Reason Code")
											|| DE[0].equalsIgnoreCase("Settlement Flag")
											|| DE[0].equalsIgnoreCase("Authorization Characteristics Indicator")
											|| DE[0].equalsIgnoreCase("POS Terminal Capability")
											|| DE[0].equalsIgnoreCase("Cardholder ID Method")
											|| DE[0].equalsIgnoreCase("Collection-Only Flag")
											|| DE[0].equalsIgnoreCase("POS Entry Mode")
											|| DE[0].equalsIgnoreCase("Central Processing Date")
											|| DE[0].equalsIgnoreCase("Reimbursement Attribute")
											|| DE[0].equalsIgnoreCase("Transaction Identifier")
											|| DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))

									{

										stData = stline.substring((ststart_pos - 1), (ststart_pos)).trim();
										stRawData.add(stData);
										if (DE[0].equalsIgnoreCase("Collection-Only Flag")) {

										}
									}

								}

							}

							else if (TCR_Code.equalsIgnoreCase("05")) {
								String[] DE = Data_Elements.get(i).split("\\|");

								TRAN_ID = stline.substring(Integer.parseInt(DE[1]), Integer.parseInt(DE[2]));

							}
						} else if (TC.equals("10") || TC.equals("20")) {

							if (TCR_Code.equals("00")) {

								String[] DE = Data_Elements.get(i).split("\\|");
								String stData = "";

								if (DE.length == 3) {
									int ststart_Pos = Integer.parseInt(DE[1].trim());
									int stEnd_pos = Integer.parseInt(DE[2].trim());
									/*
									 * logger.info("start positon "+ststart_Pos);
									 * logger.info("End Position "+stEnd_pos);
									 * logger.info("stHeader : "+DE[0]+" Value : "+stline.substring((ststart_Pos-1),
									 * stEnd_pos).replaceAll("^0*",""));
									 */

									if (DE[0].equalsIgnoreCase("ACCOUNT NUMBER")) {
										stData = stline.substring((ststart_Pos - 1), stEnd_pos).replaceAll("^0*", "")
												.trim();
										stRawData.add(stData);
									} else if (DE[0].equalsIgnoreCase("SOURCE AMOUNT")
											|| DE[0].equalsIgnoreCase("DESTINATION AMOUNT")) {

										SOURCE_AMT = stline.substring((ststart_Pos - 1), stEnd_pos)
												.replaceAll("^0*", "").trim();
										stData = String.valueOf((Float.parseFloat(SOURCE_AMT) / 100));
										stRawData.add(stData);
										// logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
									} else // if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
									if (DE[0].equalsIgnoreCase("Destination BIN")
											|| DE[0].equalsIgnoreCase("Source BIN")
											|| DE[0].equalsIgnoreCase("Reason Code")
											|| DE[0].equalsIgnoreCase("Country Code")
											|| DE[0].equalsIgnoreCase("Event Date (MMDD)")
											|| DE[0].equalsIgnoreCase("Account Number") ||
											// DE[0].equalsIgnoreCase("Destination Amount") ||
											DE[0].equalsIgnoreCase("Destination Currency Code")
											|| DE[0].equalsIgnoreCase("Source Amount")
											|| DE[0].equalsIgnoreCase("Source Currency Code")
											|| DE[0].equalsIgnoreCase("Settlement Flag")
											|| DE[0].equalsIgnoreCase("Transaction Identifier")
											|| DE[0].equalsIgnoreCase("Central Processing Date (YDDD)")
											|| DE[0].equalsIgnoreCase("Reimbursement Attribute")
											|| DE[0].equalsIgnoreCase("Message Text"))

									{
										// AUTH_CODE = stline.substring((ststart_Pos-1),
										// stEnd_pos).replaceAll("^0*","").trim();
										stData = stline.substring((ststart_Pos - 1), stEnd_pos).trim();
										stRawData.add(stData);
									}

								} else if (DE.length == 2) {
									int ststart_pos = Integer.parseInt(DE[1].trim());
									if (DE[0].equalsIgnoreCase("ACCOUNT NUMBER")) {
										stData = stline.substring((ststart_pos - 1), (ststart_pos))
												.replaceAll("^0*", "").trim();
										stRawData.add(stData);
									} else if (DE[0].equalsIgnoreCase("SOURCE AMOUNT")
											|| DE[0].equalsIgnoreCase("DESTINATION AMOUNT")) {
										try {
											SOURCE_AMT = stline.substring((ststart_pos - 1), (ststart_pos))
													.replaceAll("^0*", "").trim();
											stData = String.valueOf((Float.parseFloat(SOURCE_AMT) / 100));
											stRawData.add(stData);
										} catch (Exception e) {
											logger.info("Exception in visa issuer on line " + stline);
										}
									} else // if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
									if (DE[0].equalsIgnoreCase("Destination BIN")
											|| DE[0].equalsIgnoreCase("Source BIN")
											|| DE[0].equalsIgnoreCase("Reason Code")
											|| DE[0].equalsIgnoreCase("Country Code")
											|| DE[0].equalsIgnoreCase("Event Date (MMDD)")
											|| DE[0].equalsIgnoreCase("Account Number") ||
											// DE[0].equalsIgnoreCase("Destination Amount") ||
											DE[0].equalsIgnoreCase("Destination Currency Code")
											|| DE[0].equalsIgnoreCase("Source Amount")
											|| DE[0].equalsIgnoreCase("Source Currency Code")
											|| DE[0].equalsIgnoreCase("Settlement Flag")
											|| DE[0].equalsIgnoreCase("Transaction Identifier")
											|| DE[0].equalsIgnoreCase("Central Processing Date (YDDD)")
											|| DE[0].equalsIgnoreCase("Reimbursement Attribute")
											|| DE[0].equalsIgnoreCase("Message Text"))

									{
										// AUTH_CODE = stline.substring((ststart_pos-1),
										// (ststart_pos)).replaceAll("^0*","").trim();
										stData = stline.substring((ststart_pos - 1), (ststart_pos)).trim();
										logger.info("stData " + stData);
										stRawData.add(stData);
									}

								}

							}

						} else if (TC.equals("33")) {
							String stLine = stline.substring(34, stline.length());
							if (check_TCR.equals("200")) {
								// GET VARIABLES FOR DECIMAL AND SIGN
								String sign = "";
								String last_Digit = "";
								String[] DE = Data_Elements.get(i).split("\\|");
								// System.out.println("data "+ DE[0]);
								if (DE[0].equalsIgnoreCase("Trace Number") || DE[0].equalsIgnoreCase("Response Code")
										|| DE[0].equalsIgnoreCase("Retrieval Reference Number")
										|| DE[0].equalsIgnoreCase("Card Number")
										|| DE[0].equalsIgnoreCase("Transaction Amount")
										|| DE[0].equalsIgnoreCase("Request Message Type")
										|| DE[0].equalsIgnoreCase("Currency Code")
										|| DE[0].equalsIgnoreCase("Transaction Identifier")
										|| DE[0].equalsIgnoreCase("Settlement Date")) {
									if (DE.length == 3) {
										int ststart_Pos = Integer.parseInt(DE[1].trim());
										int stEnd_pos = Integer.parseInt(DE[2].trim());

										if (DE[0].equalsIgnoreCase("Trace Number")) {
											TRACE = stLine.substring((ststart_Pos - 1), stEnd_pos).replaceAll("^0*", "")
													.trim();
											TRACE = stLine.substring((ststart_Pos - 1), stEnd_pos).trim();
										} else if (DE[0].equalsIgnoreCase("Currency Code")) {
											CutCODE = stLine.substring((ststart_Pos - 1), stEnd_pos)
													.replaceAll("^0*", "").trim();
											// logger.info("CutCODE "+ CutCODE);
										} else if (DE[0].equalsIgnoreCase("Transaction Identifier")) {
											transacITe = stLine.substring((ststart_Pos - 1), stEnd_pos)
													.replaceAll("^0*", "").trim();
											// logger.info("Transaction Identifier "+ transacITe);
										}

										else if (DE[0].equalsIgnoreCase("Request Message Type")) {
											REQ_MSG_TYPE = stLine.substring((ststart_Pos - 1), stEnd_pos)
													.replaceAll("^0*", "").trim();
										} else if (DE[0].equalsIgnoreCase("Response Code")) {
											RESPONSE_CODE = stLine.substring((ststart_Pos - 1), stEnd_pos).trim();
											// logger.info("responce "+SOURCE_AMT+" check this "+RESPONSE_CODE);
										} else if (DE[0].equalsIgnoreCase("Retrieval Reference Number")) {
											REFERENCE_NUMBER = stLine.substring((ststart_Pos - 1), stEnd_pos)
													.replaceAll("^0*", "").trim();
										} else if (DE[0].equalsIgnoreCase("Card Number")) {
											// System.out.println("Card Number "+ stLine.substring((ststart_Pos-1),
											// stEnd_pos).replaceAll("^0*","").trim());
											String pan = stLine.substring((ststart_Pos - 1), stEnd_pos)
													.replaceAll("^0*", "").trim();
											String Update_Pan = "";
											if (pan.length() <= 16 && pan != null && pan.trim() != ""
													&& pan.length() > 0) {
												// System.out.println(pan);
												Update_Pan = pan.substring(0, 6) + "XXXXXX"
														+ pan.substring(pan.length() - 4);

											} else if (pan.length() >= 16 && pan != null && pan.trim() != ""
													&& pan.length() > 0) {

												Update_Pan = pan.substring(0, 6) + "XXXXXXXXX"
														+ pan.substring(pan.length() - 4);

											} else {

												Update_Pan = null;
											}

											CARD_NUMBER = Update_Pan;
											FPAN = pan;
										} else if (DE[0].equalsIgnoreCase("Transaction Amount")) {

											SOURCE_AMT = stLine.substring((ststart_Pos - 1), stEnd_pos)
													.replaceAll("^0*", "").trim();
											// .out.println("amount "+SOURCE_AMT);
										} else if (DE[0].equalsIgnoreCase("Settlement Date")) {

											DATE = stLine.substring((ststart_Pos - 1), stEnd_pos).replaceAll("^0*", "")
													.trim();
											// System.out.println("DATE "+DATE);
										}

									} else if (DE.length == 2) {
										int ststart_Pos = Integer.parseInt(DE[1].trim());
										if (DE[0].equalsIgnoreCase("Trace Number")) {
											// TRACE = stLine.substring((ststart_Pos-1),
											// ststart_Pos).replaceAll("^0*","").trim();
											TRACE = stLine.substring((ststart_Pos - 1), ststart_Pos).trim();
										} else if (DE[0].equalsIgnoreCase("Response Code")) {

											RESPONSE_CODE = stLine.substring((ststart_Pos - 1), ststart_Pos).trim();
											// logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
										} else if (DE[0].equalsIgnoreCase("Request Message Type")) {
											REQ_MSG_TYPE = stLine.substring((ststart_Pos - 1), ststart_Pos)
													.replaceAll("^0*", "").trim();
										} else if (DE[0].equalsIgnoreCase("Retrieval Reference Number")) {
											REFERENCE_NUMBER = stLine.substring((ststart_Pos - 1), ststart_Pos)
													.replaceAll("^0*", "").trim();
										} else if (DE[0].equalsIgnoreCase("Card Number")) {
											String pan = stLine.substring((ststart_Pos - 1), ststart_Pos)
													.replaceAll("^0*", "").trim();

											String Update_Pan = "";
											if (pan.length() <= 16 && pan != null && pan.trim() != ""
													&& pan.length() > 0) {
												// System.out.println(pan);
												Update_Pan = pan.substring(0, 6) + "XXXXXX"
														+ pan.substring(pan.length() - 4);

											} else if (pan.length() >= 16 && pan != null && pan.trim() != ""
													&& pan.length() > 0) {

												Update_Pan = pan.substring(0, 6) + "XXXXXXXXX"
														+ pan.substring(pan.length() - 4);

											} else {

												Update_Pan = null;
											}

											CARD_NUMBER = Update_Pan;
											FPAN = pan;
										}

										else if (DE[0].equalsIgnoreCase("Transaction Amount")) {
											SOURCE_AMT = stLine.substring((ststart_Pos - 1), (ststart_Pos))
													.replaceAll("^0*", "").trim();
//System.out.println("amount "+ SOURCE_AMT);
											for (int j = 0; j < Amount_translation.size(); j++) {
												String[] data = Amount_translation.get(j).split("\\|");
												if (SOURCE_AMT.contains(data[0])) {
													if (data[2].equals("-")) {
														SOURCE_AMT = data[2] + SOURCE_AMT.replace(data[0], data[1]);
													} else {
														SOURCE_AMT = SOURCE_AMT.replace(data[0], data[1]);
													}
												}

											}

											amt = (Float.parseFloat(SOURCE_AMT) / 100);
										} else if (DE[0].equalsIgnoreCase("Settlement Date")) {

											DATE = stLine.substring((ststart_Pos - 1), (ststart_Pos))
													.replaceAll("^0*", "").trim();
											// System.out.println("Settlement Date "+DATE);
										}

									}
								}
							} else if (check_TCR.equals("210")) {
								// GET VARIABLES FOR DECIMAL AND SIGN
								String sign = "";
								String last_Digit = "";
								String[] DE = Data_Elements.get(i).split("\\|");

								if (DE.length == 3) {
									int ststart_Pos = Integer.parseInt(DE[1].trim());
									int stEnd_pos = Integer.parseInt(DE[2].trim());

									if (DE[0].equalsIgnoreCase("purchase Date")) {
										;
										// Mcuntrycode x= stLine.substring((ststart_Pos-1),
										// stEnd_pos).replaceAll("^0*","").trim();
										DATE2 = stline.substring((ststart_Pos - 1), stEnd_pos).replaceAll("^0*", "");
										// logger.info("DATE2 "+ DATE2);
									}

								} else if (DE.length == 2) {
									int ststart_Pos = Integer.parseInt(DE[1].trim());
									if (DE[0].equalsIgnoreCase("Trace Number")) {
										// TRACE = stLine.substring((ststart_Pos-1),
										// ststart_Pos).replaceAll("^0*","").trim();
										TRACE = stLine.substring((ststart_Pos - 1), ststart_Pos).trim();
									} else if (DE[0].equalsIgnoreCase("Response Code")) {

										RESPONSE_CODE = stLine.substring((ststart_Pos - 1), ststart_Pos).trim();
										// logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
									} else if (DE[0].equalsIgnoreCase("Request Message Type")) {
										REQ_MSG_TYPE = stLine.substring((ststart_Pos - 1), ststart_Pos)
												.replaceAll("^0*", "").trim();
									} else if (DE[0].equalsIgnoreCase("Retrieval Reference Number")) {
										REFERENCE_NUMBER = stLine.substring((ststart_Pos - 1), ststart_Pos)
												.replaceAll("^0*", "").trim();
									} else if (DE[0].equalsIgnoreCase("Card Number")) {
										String pan = stLine.substring((ststart_Pos - 1), ststart_Pos)
												.replaceAll("^0*", "").trim();

										String Update_Pan = "";
										if (pan.length() <= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
											// System.out.println(pan);
											Update_Pan = pan.substring(0, 6) + "XXXXXX"
													+ pan.substring(pan.length() - 4);

										} else if (pan.length() >= 16 && pan != null && pan.trim() != ""
												&& pan.length() > 0) {

											Update_Pan = pan.substring(0, 6) + "XXXXXXXXX"
													+ pan.substring(pan.length() - 4);

										} else {

											Update_Pan = null;
										}

										CARD_NUMBER = Update_Pan;
										FPAN = pan;
									}

									else if (DE[0].equalsIgnoreCase("Transaction Amount")) {
										SOURCE_AMT = stLine.substring((ststart_Pos - 1), (ststart_Pos))
												.replaceAll("^0*", "").trim();
//System.out.println("amount "+ SOURCE_AMT);
										for (int j = 0; j < Amount_translation.size(); j++) {
											String[] data = Amount_translation.get(j).split("\\|");
											if (SOURCE_AMT.contains(data[0])) {
												if (data[2].equals("-")) {
													SOURCE_AMT = data[2] + SOURCE_AMT.replace(data[0], data[1]);
												} else {
													SOURCE_AMT = SOURCE_AMT.replace(data[0], data[1]);
												}
											}

										}

										amt = (Float.parseFloat(SOURCE_AMT) / 100);
									}

								}

							} else if (check_TCR.equals("220")) {
								// GET VARIABLES FOR DECIMAL AND SIGN
								String sign = "";
								String last_Digit = "";
								String[] DE = Data_Elements.get(i).split("\\|");

								if (DE.length == 3) {
									int ststart_Pos = Integer.parseInt(DE[1].trim());
									int stEnd_pos = Integer.parseInt(DE[2].trim());

									if (DE[0].equalsIgnoreCase("Forwarding Institution Country Code")) {
										;
										// Mcuntrycode = stLine.substring((ststart_Pos-1),
										// stEnd_pos).replaceAll("^0*","").trim();
										Mcuntrycode = stline.substring((ststart_Pos - 1), stEnd_pos).replaceAll("^0*",
												"");
										// logger.info("Forwarding Institution Country Code "+ Mcuntrycode);
									} else if (DE[0].equalsIgnoreCase("Geo ZIP Code ZIP Five")) {

										MName = stline.substring((ststart_Pos - 1), stEnd_pos).replaceAll("^0*", "");
										// logger.info("Geo ZIP Code ZIP Five "+ MName);
									} else if (DE[0].equalsIgnoreCase("MERCHANT_CATEGORY_CODE")) {

										MCC = stline.substring((ststart_Pos - 1), stEnd_pos).replaceAll("^0*", "");
										// logger.info("MERCHANT_CATEGORY_CODE "+ MCC);
									}

								} else if (DE.length == 2) {
									int ststart_Pos = Integer.parseInt(DE[1].trim());
									if (DE[0].equalsIgnoreCase("Trace Number")) {
										// TRACE = stLine.substring((ststart_Pos-1),
										// ststart_Pos).replaceAll("^0*","").trim();
										TRACE = stLine.substring((ststart_Pos - 1), ststart_Pos).trim();
									} else if (DE[0].equalsIgnoreCase("Response Code")) {

										RESPONSE_CODE = stLine.substring((ststart_Pos - 1), ststart_Pos).trim();
										// logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
									} else if (DE[0].equalsIgnoreCase("Request Message Type")) {
										REQ_MSG_TYPE = stLine.substring((ststart_Pos - 1), ststart_Pos)
												.replaceAll("^0*", "").trim();
									} else if (DE[0].equalsIgnoreCase("Geo ZIP Code ZIP Five")) {
										REFERENCE_NUMBER = stLine.substring((ststart_Pos - 1), ststart_Pos)
												.replaceAll("^0*", "").trim();
										logger.info("REFERENCE_NUMBER " + REFERENCE_NUMBER);
									} else if (DE[0].equalsIgnoreCase("Card Number")) {
										String pan = stLine.substring((ststart_Pos - 1), ststart_Pos)
												.replaceAll("^0*", "").trim();

										String Update_Pan = "";
										if (pan.length() <= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
											// System.out.println(pan);
											Update_Pan = pan.substring(0, 6) + "XXXXXX"
													+ pan.substring(pan.length() - 4);

										} else if (pan.length() >= 16 && pan != null && pan.trim() != ""
												&& pan.length() > 0) {

											Update_Pan = pan.substring(0, 6) + "XXXXXXXXX"
													+ pan.substring(pan.length() - 4);

										} else {

											Update_Pan = null;
										}

										CARD_NUMBER = Update_Pan;
										FPAN = pan;
									}

									else if (DE[0].equalsIgnoreCase("Transaction Amount")) {
										SOURCE_AMT = stLine.substring((ststart_Pos - 1), (ststart_Pos))
												.replaceAll("^0*", "").trim();
//System.out.println("amount "+ SOURCE_AMT);
										for (int j = 0; j < Amount_translation.size(); j++) {
											String[] data = Amount_translation.get(j).split("\\|");
											if (SOURCE_AMT.contains(data[0])) {
												if (data[2].equals("-")) {
													SOURCE_AMT = data[2] + SOURCE_AMT.replace(data[0], data[1]);
												} else {
													SOURCE_AMT = SOURCE_AMT.replace(data[0], data[1]);
												}
											}

										}

										amt = (Float.parseFloat(SOURCE_AMT) / 100);
									}

								}

							}

						}
					}
				}

				if (TC.equals("05") || TC.equals("06") || TC.equals("07") || TC.equals("25") || TC.equals("27")) {
					count++;
					if (TCR_Code.equals("00") || TCR_Code.equals("20")) {
						seq = "VISA" + lineNumber;
					}

					// Insertint in input table first
					pstmt1.setString(1, TC);
					pstmt1.setString(2, TCR_Code);
					pstmt1.setString(3, stline);
					pstmt1.setString(4, seq + "");

					pstmt1.setString(5, setupBean.getFileDate());

					pstmt1.addBatch();

					if (TCR_Code.equals("00") || TCR_Code.equals("20")) {
						read_stmt_05.setString(1, TC);
						read_stmt_05.setString(2, TCR_Code);

						read_stmt_05.setString(3, seq + "");
						/* read_stmt.setString(7, "28/09/2017"); */
						read_stmt_05.setString(4, setupBean.getFileDate());
						read_stmt_05.setString(5, "1");
						int index = 6;
						for (int m = 0; m < stRawData.size(); m++) {

							if (m == 0) {

								String pan = stRawData.get(m);

								String Update_Pan = "";
								if (pan.length() <= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
									// System.out.println(pan);
									Update_Pan = pan.substring(0, 6) + "XXXXXX" + pan.substring(pan.length() - 4);

								} else if (pan.length() >= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {

									Update_Pan = pan.substring(0, 6) + "XXXXXXXXX" + pan.substring(pan.length() - 4);

								} else {

									Update_Pan = null;
								}

								FPAN = pan;
								read_stmt_05.setString(index, Update_Pan);
							} else {
								read_stmt_05.setString(index, stRawData.get(m));
							}

							index++;
						}

						read_stmt_05.setString(31, FPAN);
						// read_stmt_05.addBatch(); //commented by int8624 on 28 may
					}
					// ADD ELSE IF CONDITION AND ADD BATCH HERE IN8624 on 28 May
					else if (TCR_Code.equalsIgnoreCase("05") || (TC.equals("06") && TCR_Code.equalsIgnoreCase("25"))) {

						read_stmt_05.setString(32, TRAN_ID);
						read_stmt_05.setString(33, file.getOriginalFilename());
						// logger.info("read_stmt_05 "+ TRAN_ID);
						read_stmt_05.addBatch();
					}
				} else if (TC.equals("10") || TC.equals("20")) {

					count++;
					if (TCR_Code.equals("00")) {
						/*
						 * ResultSet rset2 =pstmt2.executeQuery(); if(rset2.next()) { seq =
						 * rset2.getString("SEQ"); }
						 */
						seq = "VISA" + lineNumber;

					}

					// Insertint in input table first
					pstmt1.setString(1, TC);
					pstmt1.setString(2, TCR_Code);
					pstmt1.setString(3, stline);
					pstmt1.setString(4, seq + "");
					/* pstmt1.setString(5, "28/09/2017"); */
					pstmt1.setString(5, setupBean.getFileDate());

					pstmt1.addBatch();

					if (TCR_Code.equals("00")) {
						read_stmt_10.setString(1, TC);
						read_stmt_10.setString(2, TCR_Code);
						/*
						 * read_stmt.setString(3, ACC_NO); read_stmt.setString(4, amt+"");
						 * read_stmt.setString(5, AUTH_CODE);
						 */
						read_stmt_10.setString(3, seq + "");
						/* read_stmt.setString(7, "28/09/2017"); */
						read_stmt_10.setString(4, setupBean.getFileDate());
						read_stmt_10.setString(5, "1");
						int index = 6;
						for (int m = 0; m < stRawData.size(); m++) {
							// read_stmt_10.setString(index, stRawData.get(m));

							if (m == 0) {

								String pan = stRawData.get(m);

								String Update_Pan = "";
								if (pan.length() <= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
									// System.out.println(pan);
									Update_Pan = pan.substring(0, 6) + "XXXXXX" + pan.substring(pan.length() - 4);

								} else if (pan.length() >= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {

									Update_Pan = pan.substring(0, 6) + "XXXXXXXXX" + pan.substring(pan.length() - 4);

								} else {

									Update_Pan = null;
								}

								read_stmt_10.setString(index, Update_Pan);
							} else {
								read_stmt_10.setString(index, stRawData.get(m));
							}

							index++;
						}
						System.out.println("index is " + index);
						read_stmt_10.setString(21, FPAN);
						read_stmt_10.setString(22, file.getOriginalFilename());
						read_stmt_10.addBatch();
					}

				} else if (TC.equals("33")) {
					count++;
					// if(check_TCR.equals("200"))
					if (check_TCR.equals("200") || containsVISATable) {

						seq = "VISA" + lineNumber;
					}
					if (check_TCR.equals("220") || containsVISATable) {

						seq = "VISA" + lineNumber;
					}

					// Insertint in input table first
					if (check_TCR.equals("200")) {
						pstmt1.setString(1, TC);
						pstmt1.setString(2, check_TCR);
						pstmt1.setString(3, stline);
						pstmt1.setString(4, seq + "");
						/* pstmt1.setString(5, "28/09/2017"); */
						pstmt1.setString(5, setupBean.getFileDate());

						pstmt1.addBatch();
					}
					if (!containsVISATable) {

						String stLine2 = stline.substring(34, 40);
						REQ_MSG_TYPE = stLine2;
						if (stLine2.contains("V22220")) {
							MESSAGE_TEXT = stline.trim().substring(52, 60);
							MName = stline.trim().substring(60, 113);
							Mcuntrycode = stline.trim().substring(113, 115);
							POS_ENTRY_MODE = stline.trim().substring(42, 44);
							MCC = stline.trim().substring(48, 52);
							CutCODE = stline.trim().substring(129, 132);
						}
						stLine2.contains("V22210");
						if (stLine2.contains("V22200")) {
							TRACE = stline.trim().substring(102, 108);
							REFERENCE_NUMBER = stline.trim().substring(96, 108);
							SOURCE_AMT = stline.trim().substring(150, 160);
							FPAN = stline.trim().substring(130, 146);
							transacITe = stline.trim().substring(78, 92);
							ARN = stline.trim().substring(96, 119);
							REQ_MSGTYPE = stline.trim().substring(119, 122);
						}
						if (stLine2.contains("V22260"))
							destination_code = stline.trim().substring(71, 74);
						if (stLine2.contains("V22281")) {
							pstmt33.setString(1, TC);
							pstmt33.setString(2, check_TCR);
							CARD_NUMBER = FPAN;
							String pan = CARD_NUMBER;
							String Update_Pan = "";
							pstmt33.setString(3, CARD_NUMBER);
							pstmt33.setString(4, SOURCE_AMT);
							pstmt33.setString(5, TRACE);
							pstmt33.setString(6, REFERENCE_NUMBER);
							pstmt33.setString(7, RESPONSE_CODE);
							pstmt33.setString(8, seq);
							pstmt33.setString(9, setupBean.getFileDate());
							pstmt33.setString(10, "1");
							dummyrrn = REFERENCE_NUMBER;
							pstmt33.setString(11, FPAN);
							pstmt33.setString(12, CutCODE);
							pstmt33.setString(13, destination_code);
							pstmt33.setString(14, SOURCE_AMT);
							pstmt33.setString(15, transacITe);
							pstmt33.setString(16, Mcuntrycode);
							pstmt33.setString(17, MName);
							pstmt33.setString(18, MCC);
							pstmt33.setString(19, DATE2);
							pstmt33.setString(20, MESSAGE_TEXT);
							pstmt33.setString(21, file.getOriginalFilename());
							pstmt33.setString(22, REQ_MSGTYPE);
							pstmt33.setString(23, POS_ENTRY_MODE);
							pstmt33.setString(24, ARN);
							pstmt33.addBatch();
							dummyrrn = REFERENCE_NUMBER;
						}

					} else if (!containsVISATable && check_TCR.equals("220")) {
					}

				}
				if (count == 100000) {
					count = 1;

					pstmt1.executeBatch();

					read_stmt_05.executeBatch();

					read_stmt_10.executeBatch();

					pstmt1.executeBatch();

					pstmt33.executeBatch();
					logger.info("EXECUTED BATCH " + batch);
					batch++;

				}

				stline = br.readLine();
			}

			read_stmt_05.executeBatch();

			read_stmt_10.executeBatch();
			pstmt1.executeBatch();

			pstmt33.executeBatch();

			logger.info("Completed Reading");

			conn.close();
			logger.info("***** ReadVisaFile.readData End ****");

		} catch (Exception e) {
			logger.error(" error in ReadVisaFile.readData", new Exception("ReadVisaFile.readData", e));
			e.printStackTrace();
			return false;
		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					logger.error(" error in ReadVisaFile.readData", new Exception("ReadVisaFile.readData", e));
				}
			}
		}
		return true;
	}

	public static void main(String args[]) {
		try {
			OracleConn oracon = new OracleConn();
			// readFile("\\D:\\",oracon.getconn());
		} catch (Exception e) {

		}
	}

}
