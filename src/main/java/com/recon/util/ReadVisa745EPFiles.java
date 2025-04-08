package com.recon.util;

import com.recon.model.VisaUploadBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.web.multipart.MultipartFile;

public class ReadVisa745EPFiles {
	static String description = "";

	static String description1 = "";

	static int vss120_record_no = 0;

	public void fileupload(VisaUploadBean beanObj, MultipartFile file, Connection conn) throws SQLException {
		String line = null;
		int count = 0;
		boolean vss_110 = false, vss_120 = false, vss_130 = false, vss_140 = false, vss_210 = false;
		boolean international = false, domestic = false;
		boolean vss_110_dom_starts = false, vss_120_dom_starts = false, vss_110_int_starts = false;
		boolean vss_130_dom_starts = false, issuer = false, acquirer = false;
		boolean vss_120_int_starts = false, vss_130_int_starts = false, vss_140_int_starts = false;
		boolean vss_210_int_starts = false;
		int sr_no = 0;
		try {
			System.out.println("reading file EP_745");
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			String Insert745 = "INSERT INTO  visa_ep745_rawdata (    REPORT_ID,   FUNDS_XFR,   PROCESSOR,   AFFILIATE,   SRE,   ONLINE_SETTLMNT_DATE,   REPORT_DATE,   REPORT_TIME,   VSS_PROCESSING_DATE,   SETT_DATE,   SETT_TIME,   CARD_NUMBER,   RETEIVAL_REF_NUMBER,   TRACE_NUMBER,   ISSUER_ID,   TRAN_TYPE,   PROCESS_CODE,   REAS_CODE,   POS_CON,   RESP_CODE,   TRANSACTION_AMOUNT,   CURR_CODE,   SETTLEMENT_AMOUNT,   DR_CR,   CA_ID,   LOCATION,   TR_ID,   FEE_JURIS,   ROUTING,   FEE_LEVEL,   SETTLEMENT_AMOUNT2,   SETTLEMENT_AMOUNT2_DR_CR,   FILENAME,   FILEDATE  ,code ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String FILEDATE = "", FILENAME = "", PAGE_NO = "", REPORT_ID = "", UNDS_XFR = "", ONLINE_SETTLMNT_DATE = "";
			String PROCESSOR = "", REPORT_DATE = "", AFFILIATE = "", REPORT_TIME = "", SRE = "";
			String VSS_PROCESSING_DATE = "", ACCT_NUMBER = "", ACQ_REF_NO = "", PURCHASE_CODE = "", DESPUT_TYPE = "";
			String SOURCE_CURR_CODE = "", CA_ID = "", LOCATION = "", TR_ID = "", FEE_JURIS = "", ROUTING = "";
			String FEE_LEVEL = "", SETTLEMENT_AMOUNT2 = "", SETTLEMENT_AMOUNT2_DR_CR = "", AUTHORIZATION_CODE = "";
			String POS_ENTRY_MODE = "", CENTRAL_PROC_DATE = "", CARD_ACCEPTOR_ID = "", REIMBURSEMENT_ATTRIBUTE = "";
			String NETWORK_IDEN_CODE = "", DISPUTE_CONDITION = "", VROL_FINANCIAL_ID = "", VROL_CASE_NUMBER = "";
			String SETT_DATE = "", SETT_TIME = "", CARD_NUMBER = "", RETRIVAL_REF_NMBER = "", TRAACE_NUMBER = "";
			String SPEND_QUAL_INDICATOR = "", DISPUTE_FINANCIAL_REASON = "", SETTLEMENT_FLAG = "", ISSUER_ID = "";
			String TRAN_TYPE = "", PROCESS_CODE = "", REAS_CODE = "", POS_CON = "", RESP_CODE = "";
			String TRANSACTION_AMOUNT = "", CURR_CODE = "", SETTLEMENT_AMOUNT = "", DR_CR = "", Code = "";
			PreparedStatement ps745 = conn.prepareStatement(Insert745);
			boolean Flag255 = false;
			while ((line = br.readLine()) != null) {
				if (!line.trim().equalsIgnoreCase("")) {
					if (line.trim().contains("REPORT ID"))
						REPORT_ID = line.trim().substring(12, 19);
					if (line.trim().contains("UNDS XFR")) {
						UNDS_XFR = line.substring(12, 22);
						ONLINE_SETTLMNT_DATE = line.substring(125, line.length());
					} else if (line.trim().contains("PROCESSOR")) {
						PROCESSOR = line.trim().substring(12, 22);
						REPORT_DATE = line.substring(125, line.length());
						DESPUT_TYPE = line.trim().substring(50, 88);
					} else if (line.trim().contains("AFFILIATE")) {
						AFFILIATE = line.trim().substring(12, 22);
						REPORT_TIME = line.substring(124, line.length());
					} else if (line.trim().contains("SRE")) {
						SRE = line.trim().substring(12, 22);
						VSS_PROCESSING_DATE = line.substring(125, line.length());
						continue;
					}
					if ((DESPUT_TYPE.contains("ACQUIRER TRANSACTION") && line.trim().startsWith("255"))
							|| line.trim().startsWith("38") || line.trim().startsWith("47")
							|| line.trim().startsWith("12") || line.trim().startsWith("07")
							|| line.trim().startsWith("10") || line.trim().startsWith("18")
							|| line.trim().startsWith("02") || line.trim().startsWith("21")
							|| line.trim().startsWith("16") || line.trim().startsWith("15")
							|| line.trim().startsWith("38") || line.trim().startsWith("14")
							|| line.trim().startsWith("13") || line.trim().startsWith("04")) {
						Code = line.trim().substring(0, 3);

						if (line.trim().length() != 117 && line.trim().length() != 92)
							if (line.trim().length() != 86) {
								if (line.trim().startsWith("18") ||line.trim().startsWith("38") || line.trim().startsWith("21")
										|| line.trim().startsWith("47") || line.trim().startsWith("15")
										|| line.trim().startsWith("16") || line.trim().startsWith("14")
										|| line.trim().startsWith("12") || line.trim().startsWith("04")
										|| line.trim().startsWith("02") || line.trim().startsWith("10")
										|| line.trim().startsWith("07") || line.trim().startsWith("10")
										|| line.trim().startsWith("07") || line.trim().startsWith("13")) {
									System.out.println("data " + line.trim() + " length " + line.trim().length());
									SETT_DATE = line.trim().substring(3, 9);
									SETT_TIME = line.trim().substring(9, 18);
									CARD_NUMBER = line.trim().substring(18, 38);
									RETRIVAL_REF_NMBER = line.trim().substring(38, 51);
									TRAACE_NUMBER = line.trim().substring(51, 58);
									ISSUER_ID = line.trim().substring(58, 65);
									TRAN_TYPE = line.trim().substring(70, 75);
									PROCESS_CODE = line.trim().substring(75, 82);
									REAS_CODE = line.trim().substring(86, 91);
									POS_CON = line.trim().substring(91, 94);
									RESP_CODE = line.trim().substring(95, 99);
									TRANSACTION_AMOUNT = line.trim().substring(99, 112);
									CURR_CODE = line.trim().substring(112, 116);
									SETTLEMENT_AMOUNT = line.trim().substring(117, 128);

									if (line.trim().length() == 128 || line.trim().length() == 129) {
										DR_CR = "NA";
									} else {
										DR_CR = line.trim().substring(128, 130);
									}
								} else {
									System.out.println("data " + line.trim() + " length " + line.length());
									SETT_DATE = line.trim().substring(3, 10);
									SETT_TIME = line.trim().substring(10, 19);
									CARD_NUMBER = line.trim().substring(19, 39);
									RETRIVAL_REF_NMBER = line.trim().substring(39, 52);
									TRAACE_NUMBER = line.trim().substring(52, 59);
									ISSUER_ID = line.trim().substring(59, 66);
									TRAN_TYPE = line.trim().substring(71, 76);
									PROCESS_CODE = line.trim().substring(76, 83);
									REAS_CODE = line.trim().substring(87, 92);
									POS_CON = line.trim().substring(92, 95);
									RESP_CODE = line.trim().substring(96, 99);
									TRANSACTION_AMOUNT = line.trim().substring(100, 113);
									CURR_CODE = line.trim().substring(113, 117);
									SETTLEMENT_AMOUNT = line.trim().substring(118, 129);
									DR_CR = line.trim().substring(129, 131);
								}
								Flag255 = true;
							}
					}
					if (line.trim().startsWith("18") && DESPUT_TYPE.contains("ADJUSTMENT"))
						if (line.trim().length() != 117) {
							SETT_DATE = line.trim().substring(3, 9);
							SETT_TIME = line.trim().substring(9, 18);
							CARD_NUMBER = line.trim().substring(18, 38);
							RETRIVAL_REF_NMBER = line.trim().substring(38, 51);
							TRAACE_NUMBER = line.trim().substring(51, 58);
							ISSUER_ID = line.trim().substring(58, 65);
							TRAN_TYPE = line.trim().substring(70, 75);
							PROCESS_CODE = line.trim().substring(75, 82);
							REAS_CODE = line.trim().substring(86, 91);
							POS_CON = line.trim().substring(91, 94);
							RESP_CODE = line.trim().substring(95, 99);
							TRANSACTION_AMOUNT = line.trim().substring(99, 112);
							CURR_CODE = line.trim().substring(112, 116);
							SETTLEMENT_AMOUNT = line.trim().substring(117, 128);
							if (line.trim().length() == 128) {
								DR_CR = "NA";
							} else {
								DR_CR = line.trim().substring(128, 130);
							}
							Flag255 = true;
						}
					if (Flag255) {
						count++;
						System.out.println("dd  " + line.trim().length());
						if (line.trim().length() == 77 || line.trim().length() == 78 || line.trim().length() == 36)
							continue;
						if (count == 2) {
							System.out.println("d  " + line.trim().length());
							System.out.println("lenght  " + line.trim());
							if (line.trim().length() == 71) {
								CA_ID = line.trim().substring(7, 15);
								LOCATION = line.trim().substring(16, 71);
							} else {
								CA_ID = line.trim().substring(22, 30);
								LOCATION = line.trim().substring(32, 86);
							}
						} else if (line.trim().startsWith("0000")) {
							CA_ID = line.trim().substring(22, 30);
							LOCATION = line.trim().substring(32, 86);
						}
						if (line.trim().startsWith("USAGE")) {
							TR_ID = line.trim().substring(26, 31);
							continue;
						}
						if (line.trim().startsWith("TR ID") && DESPUT_TYPE.contains("ADJUSTMENT")) {
							TR_ID = line.trim().substring(7, 22);
							continue;
						}
						if (line.trim().startsWith("FEE JURIS")) {
							FEE_JURIS = line.trim().substring(10, 36);
							ROUTING = line.trim().substring(44, 74);
							FEE_LEVEL = line.trim().substring(84, 105);
							SETTLEMENT_AMOUNT2 = line.trim().substring(106, 125);
							SETTLEMENT_AMOUNT2_DR_CR = line.trim().substring(125, 127);
							ps745.setString(1, REPORT_ID);
							ps745.setString(2, UNDS_XFR);
							ps745.setString(3, PROCESSOR);
							ps745.setString(4, AFFILIATE);
							ps745.setString(5, SRE);
							ps745.setString(6, ONLINE_SETTLMNT_DATE);
							ps745.setString(7, REPORT_DATE);
							ps745.setString(8, REPORT_TIME);
							ps745.setString(9, VSS_PROCESSING_DATE);
							ps745.setString(10, SETT_DATE);
							ps745.setString(11, SETT_TIME);
							ps745.setString(12, CARD_NUMBER);
							ps745.setString(13, RETRIVAL_REF_NMBER);
							ps745.setString(14, TRAACE_NUMBER);
							ps745.setString(15, ISSUER_ID);
							ps745.setString(16, TRAN_TYPE);
							ps745.setString(17, PROCESS_CODE);
							ps745.setString(18, REAS_CODE);
							ps745.setString(19, POS_CON);
							ps745.setString(20, RESP_CODE);
							ps745.setString(21, TRANSACTION_AMOUNT);
							ps745.setString(22, CURR_CODE);
							ps745.setString(23, SETTLEMENT_AMOUNT);
							ps745.setString(24, DR_CR);
							ps745.setString(25, CA_ID);
							ps745.setString(26, LOCATION);
							ps745.setString(27, TR_ID);
							ps745.setString(28, FEE_JURIS);
							ps745.setString(29, ROUTING);
							ps745.setString(30, FEE_LEVEL);
							ps745.setString(31, SETTLEMENT_AMOUNT2);
							ps745.setString(32, SETTLEMENT_AMOUNT2_DR_CR);
							ps745.setString(33, file.getOriginalFilename());
							ps745.setString(34, beanObj.getFileDate());
							ps745.setString(35, Code);
							ps745.execute();
							FILEDATE = "";
							FILENAME = "";
							Flag255 = false;
							count = 0;
						}
					}
				}
			}
			System.out.println("SuccessFull Executed " + count);
		} catch (Exception e) {
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
		} else if (!line.startsWith("VISA INTERNATIONAL")) {
			System.out.println(" ********** " + description + " ************** ");
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
