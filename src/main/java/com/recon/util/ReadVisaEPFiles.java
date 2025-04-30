package com.recon.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.VisaUploadBean;

public class ReadVisaEPFiles {

	static String description = "";
	static String description1 = "";
	static int vss120_record_no = 0;

	public void fileupload(VisaUploadBean beanObj, MultipartFile file, Connection conn) throws SQLException {

		String line = null;
		boolean vss_110 = false, vss_120 = false, vss_130 = false, vss_140 = false, vss_210 = false;
		boolean international = false, domestic = false;
		boolean vss_110_dom_starts = false, vss_120_dom_starts = false, vss_110_int_starts = false,
				vss_130_dom_starts = false, issuer = false, acquirer = false;
		boolean vss_120_int_starts = false, vss_130_int_starts = false, vss_140_int_starts = false,
				vss_210_int_starts = false;
		int sr_no = 0;
		String VSS = "";

		/*
		 * if(beanObj.getFileType().equalsIgnoreCase("DOMESTIC")) { domestic = true;
		 * international = false; } else { domestic = false; international = true; }
		 */
		try {
			System.out.println("reading file EP_150621");
			// File file = new File("C:\\\\Users\\\\int8624\\\\Desktop\\\\HTML
			// READING\\EP_150621.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));

			String InsertVSS120 = "INSERT INTO visa_ep_vss120_rawdata (REPORT_ID,REPORT_DESCRIPTION, PAGE,REPORTING_FOR,REPORTING_FOR_DESCRIPTION,PROC_DATE,ROLLUP_TO,ROLLUP_TO_DESCRIPTION,REPORT_DATE,FUNDS_XFER_ENTITY,FUNDS_XFER_ENTITY_DESCRIPTION, SETTLEMENT_CURRENCY,CLEARING_CURRENCY,DESCRIPTION,SUB_DESCRIPTION,TABLE_ID,COUNT,CLEARING_AMOUNT,INTERCHANGE_CREDIT_AMOUNT,INTERCHANGE_DEBIT_AMOUNT,TRANSACTION_TYPE,FILEDATE,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			String InsertVSS110 = "INSERT INTO  visa_ep_vss110_rawdata (REPORT_ID,REPORT_DESCRIPTION, PAGE,REPORTING_FOR,REPORTING_FOR_DESCRIPTION,PROC_DATE,ROLLUP_TO,ROLLUP_TO_DESCRIPTION,REPORT_DATE,FUNDS_XFER_ENTITY,FUNDS_XFER_ENTITY_DESCRIPTION, SETTLEMENT_CURRENCY,DESCRIPTION,SUB_DESCRIPTION,COUNT,CREDIT_AMOUNT,  DEBIT_AMOUNT,TOTAL_AMOUNT,FILEDATE, FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			String InsertVSS130 = "INSERT INTO  visa_ep_vss130_rawdata (REPORT_ID,REPORT_DESCRIPTION, PAGE,REPORTING_FOR,REPORTING_FOR_DESCRIPTION,PROC_DATE,ROLLUP_TO,ROLLUP_TO_DESCRIPTION,REPORT_DATE,FUNDS_XFER_ENTITY,FUNDS_XFER_ENTITY_DESCRIPTION, SETTLEMENT_CURRENCY,CLEARING_CURRENCY,DESCRIPTION ,DESCRIPTION2,DESCRIPTION3 ,SUB_DESCRIPTION,COUNT,INTERCHANGE_AMOUNT,  REIMBURSEMENT_CREDIT_AMOUNT,REIMBURSEMENT_DEBIT_AMOUNT,TRANSACTIONS_TYPE,FILEDATE, FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String InsertVSS140 = "INSERT INTO  visa_ep_vss140_rawdata(REPORT_ID,REPORT_DESCRIPTION, PAGE,REPORTING_FOR,REPORTING_FOR_DESCRIPTION,PROC_DATE,ROLLUP_TO,ROLLUP_TO_DESCRIPTION,REPORT_DATE,FUNDS_XFER_ENTITY,FUNDS_XFER_ENTITY_DESCRIPTION, SETTLEMENT_CURRENCY,CLEARING_CURRENCY,DESCRIPTION,DESCRIPTION2,SUB_DESCRIPTION,COUNT,CLEARING_AMOUNT,  VISA_CHARGE_CREDIT,VISA_CHARGE_DEBIT,BUSINESS_MODE,FILEDATE,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String InsertVSS210 = "INSERT INTO  visa_ep_vss210_rawdata (REPORT_ID,REPORT_DESCRIPTION, PAGE,REPORTING_FOR,REPORTING_FOR_DESCRIPTION,PROC_DATE,ROLLUP_TO,ROLLUP_TO_DESCRIPTION,REPORT_DATE,FUNDS_XFER_ENTITY,FUNDS_XFER_ENTITY_DESCRIPTION, SETTLEMENT_CURRENCY,CLEARING_CURRENCY,DESCRIPTION,DESCRIPTION2,SUB_DESCRIPTION,INTERCHANGE_AMOUNT,CONVERSION_FEE,  INTERCHANGE_AMOUNT2,CONVERSION_FEE2,OPT_ISS_FEE,TRANSACTION_TYPE,FILEDATE,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			String InsertVSS900 = "INSERT INTO visa_ep_vss900_rawdata (REPORT_ID,REPORT_DESCRIPTION, PAGE,REPORTING_FOR,REPORTING_FOR_DESCRIPTION,PROC_DATE,ROLLUP_TO,ROLLUP_TO_DESCRIPTION,REPORT_DATE,FUNDS_XFER_ENTITY,FUNDS_XFER_ENTITY_DESCRIPTION, SETTLEMENT_CURRENCY,CLEARING_CURRENCY,DESCRIPTION,SUB_DESCRIPTION,COUNT,CLEARING_AMOUNT,  TOTAL_COUNT,TOTAL_CLEARING_AMOUNT,BUSINESS_MODE,FILEDATE,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			System.out.println("table query " + InsertVSS120);
			String desc = "", desc2 = "", desc3 = "", desc4 = "";
			String REPORT_ID = "", REPORT_DESCRIPTION = "", PAGE = "", REPORTING_FOR = "",
					REPORTING_FOR_DESCRIPTION = "", PROC_DATE = "", ROLLUP_TO = "", ROLLUP_TO_DESCRIPTION = "",
					REPORT_DATE = "", FUNDS_XFER_ENTITY = "", FUNDS_XFER_ENTITY_DESCRIPTION = "",
					SETTLEMENT_CURRENCY = "", DESCRIPTION = "", DESCRIPTION2 = "", DESCRIPTION3 = "",
					SUB_DESCRIPTION = "", TOTAL_ACQUIRER = "", TOTAL_ISSUER = "", TOTAL_TYPE = "", COUNT = "",
					CREDIT_AMOUNT = "", DEBIT_AMOUNT = "", TOTAL_AMOUNT = "", CLEARING_CURRENCY = "",
					TRANSACTIONS_TYPE = "", CLEARING_AMOUNT = "", INTERCHANGE_CREDIT_AMOUNT = "",
					INTERCHANGE_DEBIT_AMOUNT = "", INTERCHANGE_AMOUNT = "", REIMBURSEMENT_CREDIT_AMOUNT = "",
					REIMBURSEMENT_DEBIT_AMOUNT = "", BUSINESS_MODE = "", TOTAL_CLEARING_CURRENCY = "",
					TOTAL_CLEARING_AMOUNT = "", TOTAL_COUNT = "", VISA_CHARGE_CREDIT = "", VISA_CHARGE_DEBIT = "",
					OPT_ISS_FEE = "", CONVERSION_FEE = "", INTERCHANGE_AMOUNT2 = "", CONVERSION_FEE2 = "",
					TABLE_ID = "";

			int count = 0;
			PreparedStatement PS110 = conn.prepareStatement(InsertVSS110);
			PreparedStatement PS120 = conn.prepareStatement(InsertVSS120);
			PreparedStatement PS130 = conn.prepareStatement(InsertVSS130);
			PreparedStatement PS140 = conn.prepareStatement(InsertVSS140);
			PreparedStatement PS210 = conn.prepareStatement(InsertVSS210);
			PreparedStatement PS900 = conn.prepareStatement(InsertVSS900);

			while ((line = br.readLine()) != null) {
				System.out.println("data " + line);
				if (!line.trim().equalsIgnoreCase("")) {

				}

				if (line.contains(":")) {
					String[] values = line.split(":");
					if (values[1].trim().startsWith("VSS")) {
						VSS = values[1].trim();
					}
					// System.out.println("VSS " + VSS);

					if (VSS.contains("VSS-110") || VSS.contains("VSS-110-M") || VSS.contains("VSS-120")
							|| VSS.contains("VSS-120-M") || VSS.contains("VSS-130") || VSS.contains("VSS-130-M")
							|| VSS.contains("VSS-140") || VSS.contains("VSS-140-M") || VSS.contains("VSS-210")
							|| VSS.contains("VSS-210-M")) {

						if (values[0].trim().equalsIgnoreCase("REPORT ID")) {

							REPORT_ID = values[1].substring(0, 16).trim();
							REPORT_DESCRIPTION = values[1].substring(48, 89).trim();
							PAGE = values[2].trim();

						} else if (values[0].trim().equalsIgnoreCase("REPORTING FOR")) {

							if (REPORT_ID.contains("VSS-110-M") || REPORT_ID.contains("VSS-120-M")
									|| REPORT_ID.contains("VSS-130-M") || REPORT_ID.contains("VSS-140-M")
									|| REPORT_ID.contains("VSS-210-M")) {

								REPORTING_FOR = values[1].substring(0, 16).trim();
								REPORTING_FOR_DESCRIPTION = values[1].substring(48, 73).trim();
								REPORT_DATE = values[2].trim();
							} else {
								REPORTING_FOR = values[1].substring(0, 16).trim();
								REPORTING_FOR_DESCRIPTION = values[1].substring(48, 73).trim();
								PROC_DATE = values[2].trim();

							}

						} else if (values[0].trim().equalsIgnoreCase("ROLLUP TO")) {

							if (REPORT_ID.contains("VSS-110-M") || REPORT_ID.contains("VSS-120-M")
									|| REPORT_ID.contains("VSS-130-M") || REPORT_ID.contains("VSS-140-M")
									|| REPORT_ID.contains("VSS-210-M")) {

								ROLLUP_TO = values[1].substring(0, 20).trim();
								ROLLUP_TO_DESCRIPTION = values[1].substring(46, 89).trim();

							} else {

								ROLLUP_TO = values[1].substring(0, 20).trim();
								ROLLUP_TO_DESCRIPTION = values[1].substring(46, 89).trim();
								REPORT_DATE = values[2].trim();

							}
						} else if (values[0].trim().equalsIgnoreCase("FUNDS XFER ENTITY")) {
							FUNDS_XFER_ENTITY = values[1].substring(0, 12).trim();
							FUNDS_XFER_ENTITY_DESCRIPTION = values[1].substring(40, 89).trim();

						} else if (values[0].trim().equalsIgnoreCase("SETTLEMENT CURRENCY")) {
							SETTLEMENT_CURRENCY = values[1].substring(0, 12).trim();
							// System.out.println("SETTLEMENT_CURRENCY" + SETTLEMENT_CURRENCY);
//
						} else if (values[0].trim().equalsIgnoreCase("CLEARING CURRENCY")) {
							CLEARING_CURRENCY = values[1].substring(0, 12).trim();
							// System.out.println("SETTLEMENT_CURRENCY" + SETTLEMENT_CURRENCY);
//
						}
					} else if (VSS.contains("VSS-900") || VSS.contains("VSS-900-M") || VSS.contains("VSS-900-SM")) {
						if (values[0].trim().equalsIgnoreCase("REPORT ID")) {

							REPORT_ID = values[1].substring(0, 16).trim();
							REPORT_DESCRIPTION = values[1].substring(48, 89).trim();
							PAGE = values[2].trim();

						} else if (values[0].trim().equalsIgnoreCase("REPORTING FOR")) {

							if (REPORT_ID.contains("VSS-900-M") || REPORT_ID.contains("VSS-900-SM")) {

								REPORTING_FOR = values[1].substring(0, 16).trim();
								REPORTING_FOR_DESCRIPTION = values[1].substring(48, 73).trim();
								REPORT_DATE = values[2].trim();
							} else {
								REPORTING_FOR = values[1].substring(0, 16).trim();
								REPORTING_FOR_DESCRIPTION = values[1].substring(48, 73).trim();
								PROC_DATE = values[2].trim();

							}

						} else if (values[0].trim().equalsIgnoreCase("CLEARING CURRENCY")) {
							CLEARING_CURRENCY = values[1].substring(0, 12).trim();
							// System.out.println("SETTLEMENT_CURRENCY" + SETTLEMENT_CURRENCY);
//
						} else if (values[0].trim().equalsIgnoreCase("BUSINESS MODE")) {
							BUSINESS_MODE = values[1].substring(0, 17).trim();
							// System.out.println("SETTLEMENT_CURRENCY" + SETTLEMENT_CURRENCY);
//
						} else if (values[0].trim().equalsIgnoreCase("TOTAL CLEARING CURRENCY")) {
							TOTAL_CLEARING_CURRENCY = values[1].substring(0, 12).trim();
							// System.out.println("SETTLEMENT_CURRENCY" + SETTLEMENT_CURRENCY);
//
						}
					}

				}

				if (VSS.contains("VSS-110") || VSS.contains("VSS-110-M")) {
					if (line.trim().contains("INTERCHANGE VALUE")) {

						DESCRIPTION = "INTERCHANGE VALUE";
					} else if (line.trim().contains("REIMBURSEMENT FEES")) {

						DESCRIPTION = "REIMBURSEMENT FEES";
					} else if (line.trim().contains("VISA CHARGES")) {

						DESCRIPTION = "VISA CHARGES";
					} else if (line.substring(1, 7).trim().contains("TOTAL")) {

						DESCRIPTION = "TOTAL";
					}

					// System.out.println("INTERCHANGE VALUE " + line.trim());
					if (line.trim().contains("TOTAL ACQUIRER") || line.trim().contains("TOTAL ISSUER")
							|| line.trim().contains("TOTAL OTHER") || line.trim().contains("TOTAL INTERCHANGE VALUE")
							|| line.trim().contains("TOTAL REIMBURSEMENT FEES")
							|| line.trim().contains("TOTAL VISA CHARGES")
							|| line.trim().contains("NET SETTLEMENT AMOUNT")) {

						if (DESCRIPTION.contains("INTERCHANGE VALUE") || DESCRIPTION.contains("VISA CHARGES")
								|| DESCRIPTION.contains("REIMBURSEMENT FEES") || DESCRIPTION.contains("TOTAL")) {
							// System.out.println("TOTAL ACQUIRER " + line.trim() + " " + line.length());
							SUB_DESCRIPTION = line.substring(4, 30).trim();
							COUNT = line.substring(44, 52).trim();
							CREDIT_AMOUNT = line.substring(65, 78).trim();
							DEBIT_AMOUNT = line.substring(89, line.length()).trim();
							TOTAL_AMOUNT = line.substring(113, 133).trim();
							/*
							 * System.out.println("all data  " + " " + SUB_DESCRIPTION + "COUNT  " + COUNT +
							 * " CREDIT_AMOUNT " + CREDIT_AMOUNT + " DEBIT_AMOUNT" + DEBIT_AMOUNT +
							 * " TOTAL_AMOUNT " + TOTAL_AMOUNT);
							 */
							PS110.setString(1, REPORT_ID);
							PS110.setString(2, REPORT_DESCRIPTION);
							PS110.setString(3, PAGE);
							PS110.setString(4, REPORTING_FOR);
							PS110.setString(5, REPORTING_FOR_DESCRIPTION);
							PS110.setString(6, PROC_DATE);
							PS110.setString(7, ROLLUP_TO);
							PS110.setString(8, ROLLUP_TO_DESCRIPTION);
							PS110.setString(9, REPORT_DATE);
							PS110.setString(10, FUNDS_XFER_ENTITY);
							PS110.setString(11, FUNDS_XFER_ENTITY_DESCRIPTION);
							PS110.setString(12, SETTLEMENT_CURRENCY);
							PS110.setString(13, DESCRIPTION);
							PS110.setString(14, SUB_DESCRIPTION);
							PS110.setString(15, COUNT);
							PS110.setString(16, CREDIT_AMOUNT);

							PS110.setString(17, DEBIT_AMOUNT);
							PS110.setString(18, TOTAL_AMOUNT);
							PS110.setString(19, beanObj.getFileDate());
							PS110.setString(20, file.getOriginalFilename());

							PS110.addBatch();

							SUB_DESCRIPTION = "";
							COUNT = "";
							CREDIT_AMOUNT = "";
							DEBIT_AMOUNT = "";
							TOTAL_AMOUNT = "";
						}

					} else if (line.contains("END OF")) {
						count++;
						// System.out.println("COMPLETED " + VSS + " count " + count);
						PS110.executeBatch();

						// System.out.println("SUCCESSFULLY INSERTED " +VSS);

					}
				} else if (VSS.contains("VSS-120") || VSS.contains("VSS-120-M")) {

					if (line.trim().contains("ISSUER TRANSACTIONS") || line.trim().contains("ACQUIRER TRANSACTIONS")
							|| line.trim().contains("OTHER TRANSACTIONS")) {

						TRANSACTIONS_TYPE = line.trim();
					} else if (line.trim().startsWith("ORIGINAL DEBIT") || line.trim().startsWith("ORIGINAL CREDIT")
							|| line.trim().startsWith("PURCHASE") || line.trim().startsWith("MANUAL CASH")
							|| line.trim().startsWith("QUASI-CASH") || line.trim().startsWith("MERCHANDISE CREDIT")
							|| line.trim().startsWith("ATM CASH")) {

						DESCRIPTION = line.trim();

					}

					if (line.trim().contains("NET   MANUAL CASH") || line.trim().contains("NET MANUAL CASH")
							|| line.trim().contains("TOTAL MANUAL CASH") || line.trim().contains("ORIGINAL ADVANCE")
							|| line.trim().contains("TOTAL ORIGINAL CREDIT") || line.trim().contains("ORIGINAL")
							|| line.trim().contains("ORIGINAL SALE")
							|| line.trim().contains("ORIGINAL SALE        RVRSL") || line.trim().contains("DISPUTE FIN")
							|| line.trim().contains("DISPUTE RESP FIN") || line.trim().contains("TOTAL PURCHASE")
							|| line.trim().contains("NET   PURCHASE")
							|| line.trim().contains("TOTAL ISSUER INTERCHANGE")
							|| line.trim().contains("NET   ISSUER INTERCHANGE")
							|| line.trim().contains("TOTAL QUASI-CASH") || line.trim().contains("NET   QUASI-CASH")
							|| line.trim().contains("QUASI-CASH") || line.trim().contains("ORIGINAL")
							|| line.trim().contains("ORIGINAL             RVRSL")
							|| line.trim().contains("TOTAL MERCHANDISE CREDIT")
							|| line.trim().contains("NET   MERCHANDISE CREDIT")
							|| line.trim().contains("ORIGINAL WITHDRAWAL")
							|| line.trim().contains("TOTAL ORIGINAL WITHDRAWAL")
							|| line.trim().contains("ORIGINAL WITHDRAWAL  RVRSL")
							|| line.trim().contains("TOTAL ATM CASH") || line.trim().contains("NET   ATM CASH")
							|| line.trim().contains("TOTAL ISSUER INTERCHANGE")
							|| line.trim().contains("NET   ISSUER INTERCHANGE")
							|| line.trim().contains("NET   FEE COLLECT RC")
							|| line.trim().contains("TOTAL FEE COLLECT RC") || line.trim().contains("FEE COLLECT RC")
							|| line.trim().contains("CREDIT ADJUSTMENT") || line.trim().contains("DEBIT ADJUSTMENT")
							|| line.trim().contains("TOTAL OTHER INTERCHANGE") || line.trim().startsWith("A0")
							|| line.trim().startsWith("A1")) {

						if (DESCRIPTION.contains("MANUAL CASH") || DESCRIPTION.contains("ORIGINAL DEBIT")
								|| DESCRIPTION.contains("ORIGINAL CREDIT") || DESCRIPTION.contains("PURCHASE")
								|| DESCRIPTION.contains("ATM CASH") || DESCRIPTION.contains("QUASI-CASH")
								|| DESCRIPTION.contains("MERCHANDISE CREDIT")) {

							// System.out.println("ORIGINAL SALE" + line.trim());

							if (line.trim().startsWith("A0") && DESCRIPTION.contains("PURCHASE")) {
								System.out.println("SUB_DESCRIPTION " + SUB_DESCRIPTION);
								if (SUB_DESCRIPTION.trim().equalsIgnoreCase("ORIGINAL WITHDRAWAL")) {

									SUB_DESCRIPTION = "ORIGINAL WITHDRAWAL";

								} else if (SUB_DESCRIPTION.equalsIgnoreCase("ORIGINAL WITHDRAWAL  RVRSL")) {

									SUB_DESCRIPTION = "ORIGINAL WITHDRAWAL  RVRSL";

								} else if (SUB_DESCRIPTION.equalsIgnoreCase("ORIGINAL SALE        RVRSL")) {

									SUB_DESCRIPTION = "ORIGINAL SALE        RVRSL";

								} else {
									SUB_DESCRIPTION = "ORIGINAL SALE";
								}
							} else // System.out.println("ORIGINAL SALE" + line.trim());

							if (line.trim().startsWith("A1") && DESCRIPTION.contains("PURCHASE")) {
								System.out.println("SUB_DESCRIPTION " + SUB_DESCRIPTION);
								if (SUB_DESCRIPTION.trim().equalsIgnoreCase("ORIGINAL WITHDRAWAL")) {

									SUB_DESCRIPTION = "ORIGINAL WITHDRAWAL";

								} else if (SUB_DESCRIPTION.equalsIgnoreCase("ORIGINAL WITHDRAWAL  RVRSL")) {

									SUB_DESCRIPTION = "ORIGINAL WITHDRAWAL  RVRSL";

								} else if (SUB_DESCRIPTION.equalsIgnoreCase("ORIGINAL SALE        RVRSL")) {

									SUB_DESCRIPTION = "ORIGINAL SALE        RVRSL";

								} else {
									SUB_DESCRIPTION = "ORIGINAL SALE";
								}
							} else if (line.trim().startsWith("A0") && DESCRIPTION.contains("ATM CASH")) {
								System.out.println("SUB_DESCRIPTION " + SUB_DESCRIPTION);
								if (SUB_DESCRIPTION.trim().equalsIgnoreCase("ORIGINAL WITHDRAWAL")) {

									SUB_DESCRIPTION = "ORIGINAL WITHDRAWAL";

								}  else if (SUB_DESCRIPTION.equalsIgnoreCase("ORIGINAL SALE        RVRSL")) {

									SUB_DESCRIPTION = "ORIGINAL SALE        RVRSL";

								}else if (SUB_DESCRIPTION.equalsIgnoreCase("ORIGINAL WITHDRAWAL  RVRSL")) {

									SUB_DESCRIPTION = "ORIGINAL WITHDRAWAL  RVRSL";

								} else {
									SUB_DESCRIPTION = "ORIGINAL SALE";
								}
							} else if (line.trim().startsWith("A1") && DESCRIPTION.equalsIgnoreCase("ATM CASH")) {
								System.out.println("SUB_DESCRIPTION2  " + SUB_DESCRIPTION);
								if (SUB_DESCRIPTION.equalsIgnoreCase("ORIGINAL WITHDRAWAL")) {

									SUB_DESCRIPTION = "ORIGINAL WITHDRAWAL";

								}  else if (SUB_DESCRIPTION.equalsIgnoreCase("ORIGINAL SALE        RVRSL")) {

									SUB_DESCRIPTION = "ORIGINAL SALE        RVRSL";

								}else if (SUB_DESCRIPTION.equalsIgnoreCase("ORIGINAL WITHDRAWAL  RVRSL")) {

									SUB_DESCRIPTION = "ORIGINAL WITHDRAWAL  RVRSL";

								} else {
									SUB_DESCRIPTION = "ORIGINAL SALE";
								}

							} else {
								SUB_DESCRIPTION = line.substring(1, 33).trim();
							}

							TABLE_ID = line.substring(37, 49).trim();
							COUNT = line.substring(58, 69).trim();
							CLEARING_AMOUNT = line.substring(69, 93).trim();
							INTERCHANGE_CREDIT_AMOUNT = line.substring(98, 113).trim();
							INTERCHANGE_DEBIT_AMOUNT = line.substring(117, 133).trim();

							/*
							 * System.out.println("all data  " + " " + SUB_DESCRIPTION + "COUNT  " + COUNT +
							 * " CREDIT_AMOUNT " + INTERCHANGE_DEBIT_AMOUNT + " DEBIT_AMOUNT" +
							 * CLEARING_AMOUNT + " TOTAL_AMOUNT " + INTERCHANGE_CREDIT_AMOUNT);
							 */
							PS120.setString(1, REPORT_ID);
							PS120.setString(2, REPORT_DESCRIPTION);
							PS120.setString(3, PAGE);
							PS120.setString(4, REPORTING_FOR);
							PS120.setString(5, REPORTING_FOR_DESCRIPTION);
							PS120.setString(6, PROC_DATE);
							PS120.setString(7, ROLLUP_TO);
							PS120.setString(8, ROLLUP_TO_DESCRIPTION);
							PS120.setString(9, REPORT_DATE);
							PS120.setString(10, FUNDS_XFER_ENTITY);
							PS120.setString(11, FUNDS_XFER_ENTITY_DESCRIPTION);
							PS120.setString(12, SETTLEMENT_CURRENCY);
							PS120.setString(13, CLEARING_CURRENCY);
							PS120.setString(14, DESCRIPTION);
							PS120.setString(15, SUB_DESCRIPTION);
							PS120.setString(16, TABLE_ID);
							PS120.setString(17, COUNT);
							PS120.setString(18, CLEARING_AMOUNT);
							PS120.setString(19, INTERCHANGE_CREDIT_AMOUNT);
							PS120.setString(20, INTERCHANGE_DEBIT_AMOUNT);
							PS120.setString(21, TRANSACTIONS_TYPE);
							PS120.setString(22, beanObj.getFileDate());
							PS120.setString(23, file.getOriginalFilename());

							PS120.addBatch();

							COUNT = "";
							CREDIT_AMOUNT = "";
							DEBIT_AMOUNT = "";
							TOTAL_AMOUNT = "";
						}

					} else if (line.contains("END OF") && VSS.contains("120")) {
						count++;
						// System.out.println("COMPLETED " + VSS + " count " + count);
						PS120.executeBatch();

					}

				} else if (VSS.contains("VSS-130") || VSS.contains("VSS-130-M")) {

					if (line.trim().contains("ISSUER TRANSACTIONS") || line.trim().contains("ACQUIRER TRANSACTIONS")) {

						TRANSACTIONS_TYPE = line.trim();
					} else if (line.trim().startsWith("MANUAL CASH") || line.trim().startsWith("PURCHASE")
							|| line.trim().startsWith("QUASI-CASH") || line.trim().startsWith("MERCHANDISE CREDIT")
							|| line.trim().startsWith("ORIGINAL CREDIT") || line.trim().startsWith("QUASI-CASH CREDIT")
							|| line.trim().startsWith("ATM CASH") || line.trim().startsWith("ATM DECLINE")
							|| line.trim().startsWith("ATM BALANCE INQUIRY")) {
						DESCRIPTION = line.substring(1, 33).trim();

					} else if (line.trim().startsWith("ORIGINAL ADVANCE") || line.trim().startsWith("ORIGINAL")
							|| line.trim().startsWith("ORIGINAL SALE")
							|| line.trim().startsWith("ORIGINAL SALE          RVRSL")
							|| line.trim().startsWith("ORIGINAL WITHDRAWAL    RVRSL")
							|| line.trim().startsWith("DISPUTE FIN") || line.trim().startsWith("DISPUTE RESP FIN")
							|| line.trim().startsWith("DISPUTE FIN")
							|| line.trim().startsWith("DISPUTE RESP FIN       RVRSL")

							|| line.trim().startsWith("ORIGINAL WITHDRAWAL")
							|| line.trim().startsWith("CREDIT ADJUSTMENT")
							|| line.trim().startsWith("DEBIT ADJUSTMENT")) {
						// DESCRIPTION = DESCRIPTION+line.substring(1, 33).trim();
						// System.out.println("data "+line.substring(7, 33) );
						DESCRIPTION2 = line.substring(1, 33).trim();
						// DESCRIPTION = DESCRIPTION + " " + line.substring(1, 33).trim();
					}

					if (line.trim().startsWith("VISA INTERNATIONAL") || line.trim().startsWith("VISA A.P.")) {
						// System.out.println("data2 "+line.substring(10, 33) );
						DESCRIPTION3 = line.substring(1, 33).trim();
					}

					if (line.trim().contains("STANDARD") || line.trim().contains("CHNINT PRGM AP")
							|| line.trim().contains("INDIA - NEPAL") || line.trim().contains("BHUTAN - INDIA")
							|| line.trim().contains("NEPAL - INDIA") || line.trim().contains("TUR SPRPREM CARD")
							|| line.trim().contains("ATM AF") || line.trim().contains("AP CLS")
							|| line.trim().contains("CONS CVMR") || line.trim().contains("AP GLD")
							|| line.trim().contains("AP ALT") || line.trim().contains("SPR PREMIUM CARD")
							|| line.trim().contains("AP SIG") || line.trim().contains("TOTAL NEPAL - INDIA")
							|| line.trim().contains("INDIA -") || line.trim().contains("BUSINESS DB")
							|| line.trim().contains("BUSINESS DB T1") || line.trim().contains("EIRF/NAT DB T1")
							|| line.trim().contains("EIRF/NAT SET DB") || line.trim().contains("EIRF/NAT SETT CB")
							|| line.trim().contains("FUEL DB") || line.trim().contains("FUEL DB T1")
							|| line.trim().contains("GOV DB T1") || line.trim().contains("GOVERNMENT DB")
							|| line.trim().contains("IN STRATEGIC 2") || line.trim().contains("IN STRATEGIC 22")
							|| line.trim().contains("IN STRATEGIC 21") || line.trim().contains("IN STRTG 2 T1")
							|| line.trim().contains("INSURANCE DB") || line.trim().contains("INSURANCE DB T1")
							|| line.trim().contains("MUTUAL FND DB T1") || line.trim().contains("MUTUAL FUNDS DB")
							|| line.trim().contains("NATL DB T1") || line.trim().contains("NATL SETTLED DB")
							|| line.trim().contains("PLATINUM DB") || line.trim().contains("PLATINUM DB T1")
							|| line.trim().contains("RAILWAY DB") || line.trim().contains("RAILWAY DB T1")
							|| line.trim().contains("SCHOOLS DB") || line.trim().contains("SCHOOLS DB T1")
							|| line.trim().contains("SIGNATURE DB") || line.trim().contains("SIGNATURE DB T1")
							|| line.trim().contains("SMALL MERCH") || line.trim().contains("SMALL MERCH T1")
							|| line.trim().contains("STRTG MERCH") || line.trim().contains("TOTAL INDIA - INDIA")
							|| line.trim().contains("TOTAL VISA A.P.") || line.trim().contains("GOVERNMENT DB")
							|| line.trim().contains("ATM NATL DB") || line.trim().contains("ATM NATL CR")
							|| line.trim().contains("DISP FIN") || line.trim().contains("DISP RESP FIN")
							|| line.trim().contains("DISP RESP RVRSL") || line.trim().contains("TOTAL ATM CASH")
							|| line.trim().contains("NET   ATM CASH") || line.trim().contains("ATM BALANCE INQ")
							|| line.trim().contains("TOTAL ATM BALANCE INQUIRY")
							|| line.trim().contains("NET   ATM BALANCE INQUIRY")
							|| line.trim().contains("TOTAL ISSUER REIMB FEES")
							|| line.trim().contains("NET   ISSUER REIMB FEES")
							|| line.trim().contains("DISBURSE OC NNS") || line.trim().contains("NET   ORIGINAL CREDIT")
							|| line.trim().contains("ORIGINAL CREDIT") || line.trim().contains("TOTAL ORIGINAL CREDIT")
							|| line.trim().contains("TOTAL QUASI-CASH CREDIT")
							|| line.trim().contains("NET   MANUAL CASH") || line.trim().contains("NET   MANUAL CASH")
							|| line.trim().contains("MANUAL CASH") || line.trim().contains("NET   QUASI-CASH CREDIT")
							|| line.trim().contains("NET   MERCHANDISE CREDIT")
							|| line.trim().contains("TOTAL MERCHANDISE CREDIT") || line.trim().contains("CANADA - A.P.")
							|| line.trim().contains("E.U. - A.P.") || line.trim().contains("C.E.M.E.A - A.P.")
							|| line.trim().contains("U.S.A. - A.P.") || line.trim().contains("AUST - INDIA")
							|| line.trim().contains("HONGKONG - INDIA") || line.trim().contains("SNGPORE - INDIA")
							|| line.trim().contains("TOTAL PURCHASE") || line.trim().contains("NET   PURCHASE")
							|| line.trim().contains("JAPAN - INDIA") || line.trim().contains("S.KOREA - INDIA")
							|| line.trim().contains("N ZEALND - INDIA") || line.trim().contains("PHLIPPNE - INDIA")
							|| line.trim().contains("THAILAND - INDIA") || line.trim().contains("MYANMAR - INDIA")
							|| line.trim().contains("TOTAL CURRENCY CONVERSION FEES")
							|| line.trim().contains("NET   CURRENCY CONVERSION FEES")
							|| line.trim().contains("BNGLDESH - INDIA") || line.trim().contains("BHUTAN - INDIA")
							|| line.trim().contains("L.A.C. - A.P.") || line.trim().contains("INSURANCE DB")
							|| line.trim().contains("SRILANKA - INDIA") || line.trim().contains("P.REP.CH - INDIA")
							|| line.trim().contains("FIJI - INDIA") || line.trim().contains("INDNESIA - INDIA")
							|| line.trim().contains("MACAU_CN - INDIA") || line.trim().contains("PHLIPPNE - INDIA")
							|| line.trim().contains("VIETNAM - INDIA") || line.trim().contains("NEPAL - INDIA")
							|| line.trim().contains("INDNESIA - INDIA") || line.trim().contains("MALDIVE - INDIA")
							|| line.trim().contains("MONGOLIA - INDIA") || line.trim().contains("TAIWAN - INDIA")
							|| line.trim().contains("CAMBODIA - INDIA") || line.trim().contains("PAPUA - INDIA")
							|| line.trim().contains("- INDIA") || line.trim().contains("- A.P.")
							|| line.trim().contains("CONS CVMR") || line.trim().contains("PREMIUM CARD")
							|| line.trim().contains("EEAINT CNPCNDBPP") || line.trim().contains("EEAINT CN DBPP")
							|| line.trim().contains("SPR PREMIUM ALT") || line.trim().contains("PREMIUM ALT")
							|| line.trim().contains("NONPREMIUM ALT") || line.trim().contains("NON PREMIUM CARD")
							|| line.trim().contains("BUS ALT") || line.trim().contains("AP ATM DECLINE")
							|| line.trim().contains("ATM AF JPN") || line.trim().contains("ATM NO AF")
							|| line.trim().contains("ATM DCC NO AF") || line.trim().contains("ATM AF")
							|| line.trim().contains("ATM DCC AF") || line.trim().startsWith("TUR SPRPRM DGD")
							|| line.trim().contains("AP ALT GLD") || line.trim().contains("ATM DCC AF")
							|| line.trim().contains("AP ATM BAL INQ") || line.trim().contains("AP SIG")
							|| line.trim().contains("AP CLS") || line.trim().contains("AP ALT CLS")
							|| line.trim().contains("ATM DECLINE") || line.trim().contains("AP ATM BAL INQ")
							|| line.trim().contains("AP GLD") || line.trim().contains("AP ALT PLT")
							|| line.trim().contains("AP ALT SIG") || line.trim().contains("AP PLT")
							|| line.trim().contains("AP ALT GLD") || line.trim().contains("VISA PART PRGM")
							|| line.trim().contains("SPR PREMIUM DGD") || line.trim().contains("SPR PREMIUM CARD")
							|| line.trim().contains("ATM TRAVEL") || line.trim().contains("AP ATM TRAVEL")
							|| line.trim().contains("ATM DCC AF") || line.trim().contains("NONPREMIUM ALT")
							|| line.trim().contains("BAL INQ DECLINE") || line.trim().contains("PREMIUM DGD")
							|| line.trim().contains("AP BUS") || line.trim().contains("TUR NONPREM CARD")
							|| line.trim().contains("TUR NONPREM DGD") || line.trim().contains("ORIGINAL CREDIT")
							|| line.trim().contains("TUR PREM DGD") || line.trim().contains("AP STD PLT")
							|| line.trim().contains("BUS DGD") || line.trim().contains("AP STD")
							|| line.trim().contains("TUR PREM CARD") || line.trim().contains("ATM AF JPN")
							|| line.trim().contains("COMCL-BUS") || line.trim().startsWith("CREDIT ADJUSTMENT")
							|| line.trim().startsWith("TOTAL ORIGINAL SALE RVRSL")
							|| line.trim().startsWith("AP ORIG CREDIT") || line.trim().startsWith("TUR NONPREM ALT")) {
						if (DESCRIPTION.contains("MANUAL CASH") || DESCRIPTION.contains("PURCHASE")
								|| DESCRIPTION.contains("ATM CASH") || DESCRIPTION.contains("QUASI-CASH")
								|| DESCRIPTION.contains("MERCHANDISE CREDIT")
								|| DESCRIPTION.contains("QUASI-CASH CREDIT") || DESCRIPTION.contains("ORIGINAL CREDIT")
								|| DESCRIPTION.contains("ORIGINAL") || DESCRIPTION.contains("ORIGINAL SALE")
								|| DESCRIPTION.contains("ORIGINAL SALE          RVRSL")
								|| DESCRIPTION.contains("ORIGINAL WITHDRAWAL    RVRSL")
								|| DESCRIPTION.contains("DISPUTE FIN") || DESCRIPTION.contains("DISPUTE RESP FIN")
								|| DESCRIPTION.contains("ATM BALANCE INQUIRY") || DESCRIPTION.contains("DISPUTE FIN")
								|| DESCRIPTION.contains("DISPUTE RESP FIN       RVRSL")
								|| DESCRIPTION.contains("ATM DECLINE") || DESCRIPTION.contains("ATM DECLINE")) {

							// System.out.println("ORIGINAL SALE " + line.trim());
							SUB_DESCRIPTION = line.substring(1, 33).trim();
							COUNT = line.substring(49, 63).trim();
							INTERCHANGE_AMOUNT = line.substring(70, 87).trim();
							VISA_CHARGE_CREDIT = line.substring(92, 112).trim();
							VISA_CHARGE_DEBIT = line.substring(116, 133).trim();
							/*
							 * System.out.println("all data  " + " " + SUB_DESCRIPTION + "COUNT  " + COUNT +
							 * " CREDIT_AMOUNT " + INTERCHANGE_AMOUNT + " DEBIT_AMOUNT" +
							 * REIMBURSEMENT_CREDIT_AMOUNT + " TOTAL_AMOUNT " + REIMBURSEMENT_DEBIT_AMOUNT);
							 */
							PS130.setString(1, REPORT_ID);
							PS130.setString(2, REPORT_DESCRIPTION);
							PS130.setString(3, PAGE);
							PS130.setString(4, REPORTING_FOR);
							PS130.setString(5, REPORTING_FOR_DESCRIPTION);
							PS130.setString(6, PROC_DATE);
							PS130.setString(7, ROLLUP_TO);
							PS130.setString(8, ROLLUP_TO_DESCRIPTION);
							PS130.setString(9, REPORT_DATE);
							PS130.setString(10, FUNDS_XFER_ENTITY);
							PS130.setString(11, FUNDS_XFER_ENTITY_DESCRIPTION);
							PS130.setString(12, SETTLEMENT_CURRENCY);
							PS130.setString(13, CLEARING_CURRENCY);
							PS130.setString(14, DESCRIPTION);
							PS130.setString(15, DESCRIPTION2);
							PS130.setString(16, DESCRIPTION3);
							PS130.setString(17, SUB_DESCRIPTION);
							PS130.setString(18, COUNT);
							PS130.setString(19, INTERCHANGE_AMOUNT);
							PS130.setString(20, VISA_CHARGE_CREDIT);
							PS130.setString(21, VISA_CHARGE_DEBIT);
							PS130.setString(22, TRANSACTIONS_TYPE);

							PS130.setString(23, beanObj.getFileDate());
							PS130.setString(24, file.getOriginalFilename());

							PS130.addBatch();

							SUB_DESCRIPTION = "";
							COUNT = "";
							CREDIT_AMOUNT = "";
							DEBIT_AMOUNT = "";
							TOTAL_AMOUNT = "";
						}

					} else if (line.contains("END OF") || VSS.contains("130")) {
						count++;
						// System.out.println("COMPLETED " + VSS + " count " + count);
						PS130.executeBatch();

						// System.out.println("SUCCESSFULLY INSERTED ");

					}

				} else if (VSS.contains("VSS-140") || VSS.contains("VSS-140-M")) {

					if (line.trim().contains("ISSUER TRANSACTIONS") || line.trim().contains("ACQUIRER TRANSACTIONS")) {

						TRANSACTIONS_TYPE = line.trim();
					} else if (line.trim().startsWith("MANUAL CASH") || line.trim().startsWith("PURCHASE")
							|| line.trim().startsWith("QUASI-CASH") || line.trim().startsWith("MERCHANDISE CREDIT")
							|| line.trim().startsWith("QUASI-CASH CREDIT") || line.trim().startsWith("ATM CASH")) {
						DESCRIPTION = line.trim();

					} else if (line.trim().startsWith("ORIGINAL ADVANCE") || line.trim().startsWith("ORIGINAL CREDIT")
							|| line.trim().startsWith("ORIGINAL") || line.trim().startsWith("ORIGINAL SALE")
							|| line.trim().startsWith("ORIGINAL SALE          RVRSL")
							|| line.trim().startsWith("ORIGINAL WITHDRAWAL    RVRSL")
							|| line.trim().startsWith("ORIGINAL WITHDRAWAL") || line.trim().startsWith("DISPUTE FIN")
							|| line.trim().startsWith("DISPUTE RESP FIN")
							|| line.trim().startsWith("ATM BALANCE INQUIRY") || line.trim().startsWith("DISPUTE FIN")
							|| line.trim().startsWith("DISPUTE RESP FIN       RVRSL")
							|| line.trim().startsWith("CURRENCY CONVERSION FEES")
							|| line.trim().startsWith("INTERNATIONAL SERVICE ASSESSMENT")
							|| line.trim().contains("ORIGINAL") || line.trim().contains("CREDIT ADJUSTMENT")) {
						DESCRIPTION2 = line.substring(1, 33).trim();
					}

					if (line.trim().contains("NEPAL - INDIA") || line.trim().contains("BHUTAN - INDIA")
							|| line.trim().contains("AP CLS") || line.trim().contains("AP GLD")
							|| line.trim().contains("AP ALT") || line.trim().startsWith("TUR SPRPRM DGD")
							|| line.trim().contains("AP SIG") || line.trim().contains("A.P. - E.U.")
							|| line.trim().contains("A.P. - CANADA") || line.trim().contains("A.P. - L.A.C")
							|| line.trim().contains("A.P. - C.E.M.E.A.") || line.trim().contains("A.P. - U.S.A.")
							|| line.trim().contains("AP BUS") || line.trim().contains("TOTAL NEPAL - INDIA")
							|| line.trim().contains("INDIA -") || line.trim().contains("BUSINESS DB")
							|| line.trim().contains("BUSINESS DB T1") || line.trim().contains("EIRF/NAT DB T1")
							|| line.trim().contains("EIRF/NAT SET DB") || line.trim().contains("EIRF/NAT SETT CB")
							|| line.trim().contains("FUEL DB") || line.trim().contains("FUEL DB T1")
							|| line.trim().contains("GOV DB T1") || line.trim().contains("GOVERNMENT DB")
							|| line.trim().contains("IN STRATEGIC 2") || line.trim().contains("IN STRATEGIC 22")
							|| line.trim().contains("IN STRATEGIC 21") || line.trim().contains("IN STRTG 2 T1")
							|| line.trim().contains("INSURANCE DB") || line.trim().contains("INSURANCE DB T1")
							|| line.trim().contains("MUTUAL FND DB T1") || line.trim().contains("MUTUAL FUNDS DB")
							|| line.trim().contains("NATL DB T1") || line.trim().contains("NATL SETTLED DB")
							|| line.trim().contains("PLATINUM DB") || line.trim().contains("PLATINUM DB T1")
							|| line.trim().contains("RAILWAY DB") || line.trim().contains("RAILWAY DB T1")
							|| line.trim().contains("SCHOOLS DB") || line.trim().contains("MYANMAR - INDIA")
							|| line.trim().contains("SCHOOLS DB T1") || line.trim().contains("SIGNATURE DB")
							|| line.trim().contains("SIGNATURE DB T1") || line.trim().contains("SMALL MERCH")
							|| line.trim().contains("SMALL MERCH T1") || line.trim().contains("STRTG MERCH")
							|| line.trim().contains("TOTAL INDIA - INDIA") || line.trim().contains("TOTAL VISA A.P.")
							|| line.trim().contains("GOVERNMENT DB") || line.trim().contains("ATM NATL DB")
							|| line.trim().contains("ATM NATL CR") || line.trim().contains("DISP FIN")
							|| line.trim().contains("DISP RESP FIN") || line.trim().contains("DISP RESP RVRSL")
							|| line.trim().contains("TOTAL ATM CASH") || line.trim().contains("NET   ATM CASH")
							|| line.trim().contains("ATM BALANCE INQ")
							|| line.trim().contains("TOTAL ATM BALANCE INQUIRY")
							|| line.trim().contains("NET   ATM BALANCE INQUIRY")
							|| line.trim().contains("TOTAL ISSUER REIMB FEES")
							|| line.trim().contains("NET   ISSUER REIMB FEES")
							|| line.trim().contains("DISBURSE OC NNS") || line.trim().contains("NET   ORIGINAL CREDIT")
							|| line.trim().contains("TOTAL ORIGINAL CREDIT")
							|| line.trim().contains("TOTAL QUASI-CASH CREDIT")
							|| line.trim().contains("TOTAL QUASI-CASH") || line.trim().contains("NET   QUASI-CASH")
							|| line.trim().contains("NET   QUASI-CASH CREDIT")
							|| line.trim().contains("NET   MERCHANDISE CREDIT")
							|| line.trim().contains("TOTAL MERCHANDISE CREDIT") || line.trim().contains("CANADA - A.P.")
							|| line.trim().contains("E.U. - A.P.") || line.trim().contains("C.E.M.E.A - A.P.")
							|| line.trim().contains("U.S.A. - A.P.") || line.trim().contains("AUST - INDIA")
							|| line.trim().contains("HONGKONG - INDIA") || line.trim().contains("SNGPORE - INDIA")
							|| line.trim().contains("TOTAL PURCHASE") || line.trim().contains("NET   PURCHASE")
							|| line.trim().contains("JAPAN - INDIA") || line.trim().contains("S.KOREA - INDIA")
							|| line.trim().contains("N ZEALND - INDIA") || line.trim().contains("PHLIPPNE - INDIA")
							|| line.trim().contains("THAILAND - INDIA")
							|| line.trim().contains("TOTAL CURRENCY CONVERSION FEES")
							|| line.trim().contains("NET   CURRENCY CONVERSION FEES")
							|| line.trim().contains("BNGLDESH - INDIA") || line.trim().contains("L.A.C. - A.P.")
							|| line.trim().contains("INSURANCE DB") || line.trim().contains("SRILANKA - INDIA")
							|| line.trim().contains("P.REP.CH - INDIA") || line.trim().contains("FIJI - INDIA")
							|| line.trim().contains("INDNESIA - INDIA") || line.trim().contains("MACAU_CN - INDIA")
							|| line.trim().contains("PHLIPPNE - INDIA") || line.trim().contains("VIETNAM - INDIA")
							|| line.trim().contains("NEPAL - INDIA") || line.trim().contains("INDNESIA - INDIA")
							|| line.trim().contains("MALDIVE - INDIA") || line.trim().contains("MONGOLIA - INDIA")
							|| line.trim().contains("TAIWAN - INDIA") || line.trim().contains("CAMBODIA - INDIA")
							|| line.trim().contains("PAPUA - INDIA") || line.trim().contains("- INDIA")
							|| line.trim().contains("- A.P.") || line.trim().contains("LAOS - INDIA")

							|| line.trim().contains("PREMIUM CARD") || line.trim().contains("EEAINT CNPCNDBPP")
							|| line.trim().contains("EEAINT CN DBPP") || line.trim().contains("SPR PREMIUM ALT")
							|| line.trim().contains("PREMIUM ALT") || line.trim().contains("NONPREMIUM ALT")
							|| line.trim().contains("NON PREMIUM CARD") || line.trim().contains("BUS ALT")
							|| line.trim().contains("AP ATM DECLINE") || line.trim().contains("ATM AF JPN")
							|| line.trim().contains("ATM NO AF") || line.trim().contains("ATM DCC NO AF")
							|| line.trim().contains("ATM AF") || line.trim().contains("ATM DCC AF")
							|| line.trim().contains("AP ALT GLD") || line.trim().contains("ATM DCC AF")
							|| line.trim().contains("AP ATM BAL INQ") || line.trim().contains("AP SIG")
							|| line.trim().contains("AP CLS") || line.trim().contains("AP ALT CLS")
							|| line.trim().contains("ATM DECLINE") || line.trim().contains("AP ATM BAL INQ")
							|| line.trim().contains("AP GLD") || line.trim().contains("AP ALT PLT")
							|| line.trim().contains("AP ALT SIG") || line.trim().contains("AP PLT")
							|| line.trim().contains("AP ALT GLD") || line.trim().contains("VISA PART PRGM")
							|| line.trim().contains("SPR PREMIUM DGD") || line.trim().contains("SPR PREMIUM CARD")
							|| line.trim().contains("ATM TRAVEL") || line.trim().contains("AP ATM TRAVEL")
							|| line.trim().contains("ATM DCC AF") || line.trim().contains("NONPREMIUM ALT")
							|| line.trim().contains("PREMIUM DGD") || line.trim().contains("TUR NONPREM CARD")
							|| line.trim().contains("TUR NONPREM DGD") || line.trim().contains("ORIGINAL CREDIT")
							|| line.trim().contains("TUR PREM DGD") || line.trim().contains("AP STD PLT")
							|| line.trim().contains("BUS DGD") || line.trim().contains("AP STD")
							|| line.trim().contains("TUR PREM CARD") || line.trim().contains("COMCL-BUS")
							|| line.trim().startsWith("CREDIT ADJUSTMENT")
							|| line.trim().startsWith("TOTAL ORIGINAL SALE RVRSL")
							|| line.trim().startsWith("AP ORIG CREDIT") || line.trim().startsWith("TUR NONPREM ALT")) {
						if (DESCRIPTION.contains("MANUAL CASH") || DESCRIPTION.contains("PURCHASE")
								|| DESCRIPTION.contains("ATM CASH") || DESCRIPTION.contains("QUASI-CASH")
								|| DESCRIPTION.contains("MERCHANDISE CREDIT")
								|| DESCRIPTION.contains("QUASI-CASH CREDIT") || DESCRIPTION.contains("ORIGINAL CREDIT")
								|| DESCRIPTION.contains("ORIGINAL") || DESCRIPTION.contains("ORIGINAL SALE")
								|| DESCRIPTION.contains("ORIGINAL SALE          RVRSL")
								|| DESCRIPTION.contains("ORIGINAL WITHDRAWAL    RVRSL")
								|| DESCRIPTION.contains("DISPUTE FIN") || DESCRIPTION.contains("DISPUTE RESP FIN")
								|| DESCRIPTION.contains("ATM BALANCE INQUIRY") || DESCRIPTION.contains("DISPUTE FIN")
								|| DESCRIPTION2.contains("DISPUTE RESP FIN       RVRSL")
								|| DESCRIPTION2.contains("PURCHASE") || DESCRIPTION2.contains("ATM CASH")
								|| DESCRIPTION2.contains("QUASI-CASH") || DESCRIPTION2.contains("MERCHANDISE CREDIT")
								|| DESCRIPTION2.contains("QUASI-CASH CREDIT")
								|| DESCRIPTION2.contains("ORIGINAL CREDIT") || DESCRIPTION2.contains("ORIGINAL")
								|| DESCRIPTION2.contains("ORIGINAL SALE")
								|| DESCRIPTION2.contains("ORIGINAL SALE          RVRSL")
								|| DESCRIPTION2.contains("ORIGINAL WITHDRAWAL    RVRSL")
								|| DESCRIPTION2.contains("DISPUTE FIN") || DESCRIPTION2.contains("DISPUTE RESP FIN")
								|| DESCRIPTION2.contains("ATM BALANCE INQUIRY") || DESCRIPTION2.contains("DISPUTE FIN")
								|| DESCRIPTION2.contains("DISPUTE RESP FIN       RVRSL")) {

							// System.out.println("ORIGINAL SALE " + line.trim());
							SUB_DESCRIPTION = line.substring(1, 33).trim();
							COUNT = line.substring(45, 68).trim();
							INTERCHANGE_AMOUNT = line.substring(70, 87).trim();
							REIMBURSEMENT_CREDIT_AMOUNT = line.substring(92, 112).trim();
							REIMBURSEMENT_DEBIT_AMOUNT = line.substring(116, 133).trim();
							/*
							 * System.out.println("all data  " + " " + SUB_DESCRIPTION + "COUNT  " + COUNT +
							 * " CREDIT_AMOUNT " + INTERCHANGE_AMOUNT + " DEBIT_AMOUNT" +
							 * REIMBURSEMENT_CREDIT_AMOUNT + " TOTAL_AMOUNT " + REIMBURSEMENT_DEBIT_AMOUNT);
							 */
							PS140.setString(1, REPORT_ID);
							PS140.setString(2, REPORT_DESCRIPTION);
							PS140.setString(3, PAGE);
							PS140.setString(4, REPORTING_FOR);
							PS140.setString(5, REPORTING_FOR_DESCRIPTION);
							PS140.setString(6, PROC_DATE);
							PS140.setString(7, ROLLUP_TO);
							PS140.setString(8, ROLLUP_TO_DESCRIPTION);
							PS140.setString(9, REPORT_DATE);
							PS140.setString(10, FUNDS_XFER_ENTITY);
							PS140.setString(11, FUNDS_XFER_ENTITY_DESCRIPTION);
							PS140.setString(12, SETTLEMENT_CURRENCY);
							PS140.setString(13, CLEARING_CURRENCY);
							PS140.setString(14, DESCRIPTION);
							PS140.setString(15, DESCRIPTION2);
							PS140.setString(16, SUB_DESCRIPTION);
							PS140.setString(17, COUNT);
							PS140.setString(18, INTERCHANGE_AMOUNT);
							PS140.setString(19, REIMBURSEMENT_CREDIT_AMOUNT);
							PS140.setString(20, REIMBURSEMENT_DEBIT_AMOUNT);
							PS140.setString(21, TRANSACTIONS_TYPE);
							PS140.setString(22, beanObj.getFileDate());
							PS140.setString(23, file.getOriginalFilename());

							PS140.addBatch();

							SUB_DESCRIPTION = "";
							COUNT = "";
							CREDIT_AMOUNT = "";
							DEBIT_AMOUNT = "";
							TOTAL_AMOUNT = "";
						}

					} else if (line.contains("END OF") || VSS.contains("140")) {
						count++;
						// System.out.println("COMPLETED " + VSS + " count " + count);
						PS140.executeBatch();

						// System.out.println("SUCCESSFULLY INSERTED ");

					}

				} else if (VSS.contains("VSS-210") || VSS.contains("VSS-210-M")) {

					if (line.trim().contains("ISSUER TRANSACTIONS") || line.trim().contains("ACQUIRER TRANSACTIONS")) {

						TRANSACTIONS_TYPE = line.trim();
					} else if (line.trim().startsWith("MANUAL CASH") || line.trim().startsWith("PURCHASE")
							|| line.trim().startsWith("QUASI-CASH") || line.trim().startsWith("MERCHANDISE CREDIT")
							|| line.trim().startsWith("QUASI-CASH CREDIT") || line.trim().startsWith("ATM CASH")) {
						DESCRIPTION = line.trim();

					} else if (line.trim().startsWith("ORIGINAL ADVANCE") || line.trim().startsWith("ORIGINAL CREDIT")
							|| line.trim().startsWith("ORIGINAL") || line.trim().startsWith("CREDIT ADJUSTMENT")
							|| line.trim().startsWith("ORIGINAL SALE")
							|| line.trim().startsWith("ORIGINAL SALE          RVRSL")
							|| line.trim().startsWith("ORIGINAL WITHDRAWAL    RVRSL")
							|| line.trim().startsWith("DISPUTE FIN") || line.trim().startsWith("DISPUTE RESP FIN")
							|| line.trim().startsWith("ATM BALANCE INQUIRY") || line.trim().startsWith("DISPUTE FIN")
							|| line.trim().startsWith("DISPUTE RESP FIN       RVRSL")) {
						// DESCRIPTION = DESCRIPTION + " " +line.substring(1, 33).trim();
						DESCRIPTION2 = line.substring(1, 33).trim();
					}
					// System.out.println("DESCRIPTION " + DESCRIPTION);

					if (line.trim().contains("VISA INTERNATIONAL")
							|| line.trim().contains("TOTAL CURRENCY CONVERSION FEES")
							|| line.trim().contains("TOTAL MANUAL CASH")
							|| line.trim().contains("TOTAL MERCHANDISE CREDIT")
							|| line.trim().contains("BHUTAN - INDIA") || line.trim().contains("U.S.A. - A.P.")
							|| line.trim().contains("AUST - INDIA") || line.trim().contains("HONGKONG - INDIA")
							|| line.trim().contains("SNGPORE - INDIA") || line.trim().contains("TOTAL PURCHASE")
							|| line.trim().contains("NET   PURCHASE") || line.trim().contains("JAPAN - INDIA")
							|| line.trim().contains("S.KOREA - INDIA") || line.trim().contains("N ZEALND - INDIA")
							|| line.trim().contains("PHLIPPNE - INDIA") || line.trim().contains("THAILAND - INDIA")
							|| line.trim().contains("TOTAL CURRENCY CONVERSION FEES")
							|| line.trim().contains("NET   CURRENCY CONVERSION FEES")
							|| line.trim().contains("NET   MANUAL CASH") || line.trim().contains("BNGLDESH - INDIA")
							|| line.trim().contains("L.A.C. - A.P.") || line.trim().contains("C.E.M.E.A - A.P.")
							|| line.trim().contains("INSURANCE DB") || line.trim().contains("SRILANKA - INDIA")
							|| line.trim().contains("P.REP.CH - INDIA") || line.trim().contains("FIJI - INDIA")
							|| line.trim().contains("INDNESIA - INDIA") || line.trim().contains("MACAU_CN - INDIA")
							|| line.trim().contains("PHLIPPNE - INDIA") || line.trim().contains("VIETNAM - INDIA")
							|| line.trim().contains("NEPAL - INDIA") || line.trim().contains("INDNESIA - INDIA")
							|| line.trim().contains("MALDIVE - INDIA") || line.trim().contains("MONGOLIA - INDIA")
							|| line.trim().contains("TAIWAN - INDIA") || line.trim().contains("CAMBODIA - INDIA")
							|| line.trim().contains("PAPUA - INDIA") || line.trim().contains("- INDIA")
							|| line.trim().contains("- A.P.") || line.trim().contains("TOTAL INDIA - INDIA")
							|| line.trim().contains("TOTAL VISA A.P.") || line.trim().contains("GOVERNMENT DB")
							|| line.trim().contains("ATM NATL DB") || line.trim().contains("ATM NATL CR")
							|| line.trim().contains("DISP FIN") || line.trim().contains("DISP RESP FIN")
							|| line.trim().contains("DISP RESP RVRSL") || line.trim().contains("TOTAL ATM CASH")
							|| line.trim().contains("NET   ATM CASH") || line.trim().contains("ATM BALANCE INQ")
							|| line.trim().contains("TOTAL ATM BALANCE INQUIRY")
							|| line.trim().contains("NET   ATM BALANCE INQUIRY")
							|| line.trim().contains("TOTAL ISSUER REIMB FEES")
							|| line.trim().contains("NET   ISSUER REIMB FEES")
							|| line.trim().contains("DISBURSE OC NNS") || line.trim().contains("NET   ORIGINAL CREDIT")
							|| line.trim().contains("TOTAL ORIGINAL CREDIT")
							|| line.trim().contains("TOTAL QUASI-CASH CREDIT")
							|| line.trim().contains("NET   QUASI-CASH CREDIT")
							|| line.trim().contains("NET   MERCHANDISE CREDIT")
							|| line.trim().contains("TOTAL MERCHANDISE CREDIT") || line.trim().contains("NEPAL - INDIA")
							|| line.trim().contains("AP CLS") || line.trim().contains("AP SIG")
							|| line.trim().contains("TOTAL NEPAL - INDIA") || line.trim().contains("INDIA - INDIA")
							|| line.trim().contains("BUSINESS DB") || line.trim().contains("BUSINESS DB T1")
							|| line.trim().contains("EIRF/NAT DB T1") || line.trim().contains("EIRF/NAT SET DB")
							|| line.trim().contains("EIRF/NAT SETT CB") || line.trim().contains("FUEL DB")
							|| line.trim().contains("FUEL DB T1") || line.trim().contains("GOV DB T1")
							|| line.trim().contains("GOVERNMENT DB") || line.trim().contains("IN STRATEGIC 2")
							|| line.trim().contains("IN STRATEGIC 22") || line.trim().contains("IN STRATEGIC 21")
							|| line.trim().contains("IN STRTG 2 T1") || line.trim().contains("INSURANCE DB")
							|| line.trim().contains("INSURANCE DB T1") || line.trim().contains("MUTUAL FND DB T1")
							|| line.trim().contains("MUTUAL FUNDS DB") || line.trim().contains("NATL DB T1")
							|| line.trim().contains("NATL SETTLED DB") || line.trim().contains("PLATINUM DB")
							|| line.trim().contains("PLATINUM DB T1") || line.trim().contains("RAILWAY DB")
							|| line.trim().contains("RAILWAY DB T1") || line.trim().contains("SCHOOLS DB")
							|| line.trim().contains("SCHOOLS DB T1") || line.trim().contains("SIGNATURE DB")
							|| line.trim().contains("SIGNATURE DB T1") || line.trim().contains("SMALL MERCH")
							|| line.trim().contains("SMALL MERCH T1") || line.trim().contains("STRTG MERCH")
							|| line.trim().contains("TOTAL INDIA - INDIA") || line.trim().contains("TOTAL VISA A.P.")
							|| line.trim().contains("GOVERNMENT DB") || line.trim().contains("ATM NATL DB")
							|| line.trim().contains("ATM NATL CR") || line.trim().contains("DISP FIN")
							|| line.trim().contains("DISP RESP FIN") || line.trim().contains("DISP RESP RVRSL")
							|| line.trim().contains("TOTAL ATM CASH") || line.trim().contains("NET   ATM CASH")
							|| line.trim().contains("ATM BALANCE INQ")
							|| line.trim().contains("TOTAL ATM BALANCE INQUIRY")
							|| line.trim().contains("NET   ATM BALANCE INQUIRY")
							|| line.trim().contains("TOTAL ISSUER REIMB FEES")
							|| line.trim().contains("NET   ISSUER REIMB FEES")
							|| line.trim().contains("DISBURSE OC NNS") || line.trim().contains("NET   ORIGINAL CREDIT")
							|| line.trim().contains("TOTAL ORIGINAL CREDIT")
							|| line.trim().contains("TOTAL QUASI-CASH CREDIT")
							|| line.trim().contains("NET   QUASI-CASH CREDIT")
							|| line.trim().contains("NET   MERCHANDISE CREDIT")
							|| line.trim().contains("TOTAL MERCHANDISE CREDIT")

					) {
						if (DESCRIPTION.contains("PURCHASE") || DESCRIPTION.contains("ATM CASH")
								|| DESCRIPTION.contains("QUASI-CASH") || DESCRIPTION.contains("MERCHANDISE CREDIT")
								|| DESCRIPTION.contains("QUASI-CASH CREDIT") || DESCRIPTION.contains("ORIGINAL CREDIT")
								|| DESCRIPTION.contains("ORIGINAL") || DESCRIPTION.contains("ORIGINAL SALE")
								|| DESCRIPTION.contains("ORIGINAL SALE          RVRSL")
								|| DESCRIPTION.contains("ORIGINAL WITHDRAWAL    RVRSL")
								|| DESCRIPTION.contains("DISPUTE FIN") || DESCRIPTION.contains("DISPUTE RESP FIN")
								|| DESCRIPTION.contains("ATM BALANCE INQUIRY") || DESCRIPTION.contains("DISPUTE FIN")
								|| DESCRIPTION2.contains("DISPUTE RESP FIN       RVRSL")
								|| DESCRIPTION2.contains("PURCHASE") || DESCRIPTION2.contains("ATM CASH")
								|| DESCRIPTION2.contains("QUASI-CASH") || DESCRIPTION2.contains("MERCHANDISE CREDIT")
								|| DESCRIPTION2.contains("QUASI-CASH CREDIT")
								|| DESCRIPTION2.contains("ORIGINAL CREDIT") || DESCRIPTION2.contains("ORIGINAL")
								|| DESCRIPTION2.contains("ORIGINAL SALE")
								|| DESCRIPTION2.contains("ORIGINAL SALE          RVRSL")
								|| DESCRIPTION2.contains("ORIGINAL WITHDRAWAL    RVRSL")
								|| DESCRIPTION2.contains("DISPUTE FIN") || DESCRIPTION2.contains("DISPUTE RESP FIN")
								|| DESCRIPTION2.contains("ATM BALANCE INQUIRY") || DESCRIPTION2.contains("DISPUTE FIN")
								|| DESCRIPTION2.contains("DISPUTE RESP FIN       RVRSL")) {

							// System.out.println("ORIGINAL SALE " + line.trim());
							SUB_DESCRIPTION = line.substring(1, 33).trim();
							INTERCHANGE_AMOUNT = line.substring(33, 53).trim();
							CONVERSION_FEE = line.substring(54, 70).trim();
							INTERCHANGE_AMOUNT2 = line.substring(73, 94).trim();
							CONVERSION_FEE2 = line.substring(97, 112).trim();
							OPT_ISS_FEE = line.substring(114, 132).trim();

							/*
							 * System.out.println("all data  " + " " + SUB_DESCRIPTION + "COUNT  " + COUNT +
							 * " CREDIT_AMOUNT " + INTERCHANGE_AMOUNT + " DEBIT_AMOUNT" +
							 * REIMBURSEMENT_CREDIT_AMOUNT + " TOTAL_AMOUNT " + REIMBURSEMENT_DEBIT_AMOUNT);
							 */
							PS210.setString(1, REPORT_ID);
							PS210.setString(2, REPORT_DESCRIPTION);
							PS210.setString(3, PAGE);
							PS210.setString(4, REPORTING_FOR);
							PS210.setString(5, REPORTING_FOR_DESCRIPTION);
							PS210.setString(6, PROC_DATE);
							PS210.setString(7, ROLLUP_TO);
							PS210.setString(8, ROLLUP_TO_DESCRIPTION);
							PS210.setString(9, REPORT_DATE);
							PS210.setString(10, FUNDS_XFER_ENTITY);
							PS210.setString(11, FUNDS_XFER_ENTITY_DESCRIPTION);
							PS210.setString(12, SETTLEMENT_CURRENCY);
							PS210.setString(13, CLEARING_CURRENCY);
							PS210.setString(14, DESCRIPTION);
							PS210.setString(15, DESCRIPTION2);
							PS210.setString(16, SUB_DESCRIPTION);
							PS210.setString(17, INTERCHANGE_AMOUNT);
							PS210.setString(18, CONVERSION_FEE);
							PS210.setString(19, INTERCHANGE_AMOUNT2);
							PS210.setString(20, CONVERSION_FEE2);
							PS210.setString(21, OPT_ISS_FEE);
							PS210.setString(22, TRANSACTIONS_TYPE);
							PS210.setString(23, beanObj.getFileDate());
							PS210.setString(24, file.getOriginalFilename());

							PS210.addBatch();

							SUB_DESCRIPTION = "";
							COUNT = "";
							CREDIT_AMOUNT = "";
							DEBIT_AMOUNT = "";
							TOTAL_AMOUNT = "";
						}

					} else if (line.contains("END OF") || VSS.contains("210")) {
						count++;
						// System.out.println("COMPLETED " + VSS + " count " + count);
						PS210.executeBatch();

						// System.out.println("SUCCESSFULLY INSERTED " +VSS);

					}

				} else if (VSS.contains("VSS-900") || VSS.contains("VSS-900-M") || VSS.contains("VSS-900-SM")) {

					if (line.trim().startsWith("ATM CASH") || line.trim().startsWith("POS AUTHORIZATION")
							|| line.trim().startsWith("POS AUTH REVERSAL") || line.trim().startsWith("POS DECLINE")
							|| line.trim().startsWith("ATM BALANCE INQUIRY") || line.trim().startsWith("ATM DECLINE")
							|| line.trim().startsWith("TOTAL ISSUER TRANSACTIONS")
							|| line.trim().startsWith("TOTAL " + REPORTING_FOR)
							|| line.trim().startsWith("TOTAL ACQUIRER TRANSACTIONS")) {

						DESCRIPTION = line.trim();

					} else if (line.trim().startsWith("FINANCIAL TRANSACTIONS")
							|| line.trim().startsWith("NON-FINANCIAL TRANSACTIONS")
							|| line.trim().startsWith("NON-FINANCIAL TRANSACTIONS")) {
						/*
						 * if(DESCRIPTION.contains("FINANCIAL TRANSACTIONS") &&
						 * DESCRIPTION.contains("NON-FINANCIAL TRANSACTIONS")) {
						 * 
						 * 
						 * }else {
						 */
						DESCRIPTION2 = line.substring(1, 33).trim();
						// }

					}
					// System.out.println("DESCRIPTION " + DESCRIPTION);

					if (line.trim().contains("RECEIVED FROM VISA") || line.trim().contains("TOTAL NON-FINANCIAL")
							|| line.trim().contains("TOTAL TRANSACTIONS")
							|| line.trim().contains("TOTAL SENT TO SETTLEMENT") || line.trim().contains("SENT TO VISA")

					) {
						if (line.trim().startsWith("ATM CASH") || DESCRIPTION.startsWith("POS AUTHORIZATION")
								|| DESCRIPTION.startsWith("POS AUTH REVERSAL") || DESCRIPTION.startsWith("POS DECLINE")
								|| DESCRIPTION.startsWith("ATM BALANCE INQUIRY")
								|| DESCRIPTION.startsWith("TOTAL ACQUIRER TRANSACTIONS")
								|| DESCRIPTION.startsWith("ATM DECLINE")
								|| DESCRIPTION.startsWith("TOTAL ISSUER TRANSACTIONS")
								|| DESCRIPTION.contains("TOTAL " + REPORTING_FOR)) {

							// System.out.println("ORIGINAL SALE " + line.trim());
							SUB_DESCRIPTION = line.substring(1, 33).trim();
							COUNT = line.substring(53, 66).trim();
							CLEARING_AMOUNT = line.substring(70, 90).trim();
							TOTAL_COUNT = line.substring(93, 107).trim();
							TOTAL_CLEARING_AMOUNT = line.substring(109, 133).trim();

							/*
							 * System.out.println("all data  " + " " + SUB_DESCRIPTION + "COUNT  " + COUNT +
							 * " CREDIT_AMOUNT " + CLEARING_AMOUNT + " DEBIT_AMOUNT" + TOTAL_COUNT +
							 * " TOTAL_AMOUNT " + TOTAL_CLEARING_AMOUNT);
							 */
							PS900.setString(1, REPORT_ID);
							PS900.setString(2, REPORT_DESCRIPTION);
							PS900.setString(3, PAGE);
							PS900.setString(4, REPORTING_FOR);
							PS900.setString(5, REPORTING_FOR_DESCRIPTION);
							PS900.setString(6, PROC_DATE);
							PS900.setString(7, ROLLUP_TO);
							PS900.setString(8, ROLLUP_TO_DESCRIPTION);
							PS900.setString(9, REPORT_DATE);
							PS900.setString(10, FUNDS_XFER_ENTITY);
							PS900.setString(11, FUNDS_XFER_ENTITY_DESCRIPTION);
							PS900.setString(12, SETTLEMENT_CURRENCY);
							PS900.setString(13, CLEARING_CURRENCY);
							PS900.setString(14, DESCRIPTION);
							PS900.setString(15, SUB_DESCRIPTION);
							PS900.setString(16, COUNT);
							PS900.setString(17, CLEARING_AMOUNT);
							PS900.setString(18, TOTAL_COUNT);
							PS900.setString(19, TOTAL_CLEARING_AMOUNT);
							PS900.setString(20, BUSINESS_MODE);
							PS900.setString(21, beanObj.getFileDate());
							PS900.setString(22, file.getOriginalFilename());

							PS900.addBatch();
							DESCRIPTION = DESCRIPTION.replaceAll("FINANCIAL TRANSACTIONS", "")
									.replaceAll("NON-FINANCIAL TRANSACTIONS", "");
							SUB_DESCRIPTION = "";
							COUNT = "";
							CREDIT_AMOUNT = "";
							DEBIT_AMOUNT = "";
							TOTAL_AMOUNT = "";
						}

					} else if (line.contains("END OF") || VSS.contains("900")) {
						count++;
						// System.out.println("COMPLETED " + VSS + " count " + count);
						PS900.executeBatch();

						// System.out.println("SUCCESSFULLY INSERTED " +VSS);

					}

				}
			}
			System.out.println("SuccessFully INSERTED ");

			conn.close();
		} catch (Exception e) {
			System.out.println("VSS " + VSS);

			System.out.println("Exception in reading visa file " + e);
		}

	}

	public static void readVSS110(String line, int sr_no, PreparedStatement ps) throws SQLException {
		if (line.substring(53, 132).trim().equalsIgnoreCase("")) {
			description = line.trim();

		} else {
			System.out.println(" ********** " + description + " ************** ");
			System.out.println("Sub Description : " + line.substring(0, 37).trim());
			System.out.println("count : " + line.substring(38, 53).trim());
			System.out.println("credit amount : " + line.substring(53, 79).trim());
			System.out.println("Debit amount : " + line.substring(79, 104).trim());
			System.out.println("Total amount : " + line.substring(105, 129).trim());
			System.out.println("Sign : " + line.substring(129, 132).trim());

			System.out.println("sr no is " + sr_no);
			ps.setString(sr_no++, description);
			ps.setString(sr_no++, line.substring(0, 37).trim());
			ps.setString(sr_no++, line.substring(38, 53).trim());
			ps.setString(sr_no++, line.substring(53, 79).trim());
			ps.setString(sr_no++, line.substring(79, 104).trim());
			ps.setString(sr_no++, line.substring(105, 129).trim());
			System.out.println("sr no is " + sr_no);
			ps.setString(sr_no++, line.substring(129, 132).trim());
			System.out.println("sr no is " + sr_no);

			ps.execute();
			ps.close();
		}

	}

	public static void readVSS120(String line, int sr_no, PreparedStatement ps120) throws SQLException {

		if (line.substring(53, 132).trim().equalsIgnoreCase("")) {
			description = line.trim();
			// System.out.println(" ********** "+description+ " ************** ");
		} else {
			System.out.println(" ********** " + description + " ************** ");
			vss120_record_no++;

			ps120.setString(sr_no++, description);

			if (line.substring(0, 37).trim().equalsIgnoreCase("ORIGINAL WITHDRAWAL")) {
				System.out.println("sub description is : " + line.substring(0, 37).trim());
				System.out.println("count is : " + line.substring(53, 69).trim());
				System.out.println("clearing amount : " + line.substring(69, 88).trim());
				System.out.println("clearing amount sign : " + line.substring(88, 91).trim());
				System.out.println("Interchange Value CR : " + line.substring(93, 113).trim());

				System.out.println("sr_no is " + sr_no);
				ps120.setString(sr_no++, line.substring(0, 37).trim());
				ps120.setString(sr_no++, line.substring(53, 69).trim());
				ps120.setString(sr_no++, line.substring(69, 88).trim());
				ps120.setString(sr_no++, line.substring(88, 91).trim());
				ps120.setString(sr_no++, line.substring(93, 113).trim());
				ps120.setString(sr_no++, "0");
				ps120.setInt(sr_no++, vss120_record_no);
				System.out.println("sr_no is " + sr_no);
				ps120.execute();

				ps120.close();

			} else {
				System.out.println("sub description is : " + line.substring(0, 37).trim());
				System.out.println("count is : " + line.substring(53, 69).trim());
				System.out.println("clearing amount : " + line.substring(69, 88).trim());
				System.out.println("clearing amount sign : " + line.substring(88, 91).trim());
				System.out.println("Interchange Value CR : " + line.substring(93, 113).trim());
				System.out.println("Interchange Value DR : " + line.substring(114, line.length()).trim());

				System.out.println("sr_no is " + sr_no);
				ps120.setString(sr_no++, line.substring(0, 37).trim());
				ps120.setString(sr_no++, line.substring(53, 69).trim());
				ps120.setString(sr_no++, line.substring(69, 88).trim());
				ps120.setString(sr_no++, line.substring(88, 91).trim());
				ps120.setString(sr_no++, line.substring(93, 113).trim());
				ps120.setString(sr_no++, line.substring(114, line.length()).trim());
				ps120.setInt(sr_no++, vss120_record_no);
				System.out.println("sr_no is " + sr_no);
				ps120.execute();

				ps120.close();
			}
		}

	}

	public static void readInternationalVSS120(String line, int sr_no, PreparedStatement ps120) throws SQLException {

		if (line.substring(53, 132).trim().equalsIgnoreCase("")) {
			description = line.trim();
			// System.out.println(" ********** "+description+ " ************** ");
		} else {
			System.out.println(" ********** " + description + " ************** ");
			vss120_record_no++;

			ps120.setString(sr_no++, description);

			if (line.substring(0, 37).trim().equalsIgnoreCase("ORIGINAL WITHDRAWAL")) {
				System.out.println("sub description is : " + line.substring(0, 37).trim());
				System.out.println("count is : " + line.substring(53, 69).trim());
				System.out.println("clearing amount : " + line.substring(69, 87).trim());
				System.out.println("clearing amount sign : " + line.substring(87, 91).trim());
				System.out.println("Interchange Value CR : " + line.substring(93, 113).trim());

				System.out.println("sr_no is " + sr_no);
				ps120.setString(sr_no++, line.substring(0, 37).trim());
				ps120.setString(sr_no++, line.substring(53, 69).trim());
				ps120.setString(sr_no++, line.substring(69, 87).trim());
				ps120.setString(sr_no++, line.substring(87, 91).trim());
				ps120.setString(sr_no++, line.substring(93, 113).trim());
				ps120.setString(sr_no++, "0");
				ps120.setInt(sr_no++, vss120_record_no);
				System.out.println("sr_no is " + sr_no);
				ps120.execute();

				ps120.close();
			} else {
				System.out.println("sub description is : " + line.substring(0, 37).trim());
				System.out.println("count is : " + line.substring(53, 69).trim());
				System.out.println("clearing amount : " + line.substring(69, 88).trim());
				System.out.println("clearing amount sign : " + line.substring(88, 91).trim());
				System.out.println("Interchange Value CR : " + line.substring(93, 113).trim());
				System.out.println("Interchange Value DR : " + line.substring(114, line.length()).trim());

				System.out.println("sr_no is " + sr_no);
				ps120.setString(sr_no++, line.substring(0, 37).trim());
				ps120.setString(sr_no++, line.substring(53, 69).trim());
				ps120.setString(sr_no++, line.substring(69, 87).trim());
				ps120.setString(sr_no++, line.substring(87, 91).trim());
				ps120.setString(sr_no++, line.substring(93, 113).trim());
				ps120.setString(sr_no++, line.substring(114, line.length()).trim());
				ps120.setInt(sr_no++, vss120_record_no);
				System.out.println("sr_no is " + sr_no);
				ps120.execute();

				ps120.close();
			}
		}

	}

	public static void readVSS140(String line, int sr_no, PreparedStatement ps) throws SQLException {

		if (line.substring(53, line.length()).trim().equalsIgnoreCase("")) {
			description = line.trim();
			// System.out.println(" ********** "+description+ " ************** ");
		} else {
			System.out.println(" ********** " + description + " ************** ");

			System.out.println("sub description is : " + line.substring(0, 60).trim());
			System.out.println("count is : " + line.substring(60, 68).trim());
			System.out.println("Interchange amount : " + line.substring(69, 87).trim());
			System.out.println("Interchange amount sign : " + line.substring(87, 91).trim());
			System.out.println("Visa Charges CR : " + line.substring(93, 113).trim());
			System.out.println("Visa Charges DR : " + line.substring(114, line.length()).trim());

			ps.setString(sr_no++, description);
			ps.setString(sr_no++, line.substring(0, 60).trim());
			ps.setString(sr_no++, line.substring(60, 68).trim());
			ps.setString(sr_no++, line.substring(69, 87).trim());
			ps.setString(sr_no++, line.substring(87, 91).trim());
			ps.setString(sr_no++, line.substring(93, 113).trim());
			ps.setString(sr_no++, line.substring(114, line.length()).trim());
			ps.execute();

			ps.close();

		}

	}

	public static void readVSS210(String line, int sr_no, PreparedStatement ps) throws SQLException {

		if (line.substring(53, line.length()).trim().equalsIgnoreCase("") && !line.startsWith("VISA INTERNATIONAL")) {
			description = line.trim();
			// System.out.println(" ********** "+description+ " ************** ");
		} else if (!line.startsWith("VISA INTERNATIONAL")) {
			System.out.println(" ********** " + description + " ************** ");

			// System.out.println("sub description is : "+line.substring(0, 60).trim());
			System.out.println("sett_curr Interchange amount : " + line.substring(30, 49).trim());
			System.out.println("sett_curr Interchange amount sign : " + line.substring(49, 52).trim());
			System.out.println("sett_curr Conversion Fee : " + line.substring(53, 70).trim());
			System.out.println("clr curr Interchange amount : " + line.substring(71, 91).trim());
			System.out.println("clr curr Interchange amount sign : " + line.substring(91, 94));
			System.out.println("clr curr Conversion Fee : " + line.substring(95, 111).trim());
			System.out.println("clr curr Opt Issuer Fee : " + line.substring(111, 129).trim());
			System.out.println("clr curr Opt Issuer Fee Sign : " + line.substring(129, line.length()).trim());

			System.out.println("sr_no is " + sr_no);
			ps.setString(sr_no++, description);
			ps.setString(sr_no++, line.substring(30, 49).trim());
			ps.setString(sr_no++, line.substring(49, 52).trim());
			ps.setString(sr_no++, line.substring(53, 70).trim());
			ps.setString(sr_no++, line.substring(71, 91).trim());
			ps.setString(sr_no++, line.substring(91, 94).trim());
			ps.setString(sr_no++, line.substring(95, 111).trim());
			ps.setString(sr_no++, line.substring(111, 129).trim());
			System.out.println("sr_no is " + sr_no);
			ps.setString(sr_no++, line.substring(129, line.length()).trim());
			System.out.println("sr_no is " + sr_no);
			ps.execute();

			ps.close();

		}

	}
}
