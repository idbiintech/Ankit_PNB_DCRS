package com.recon.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.CompareSetupBean;
import com.recon.model.MastercardUploadBean;

public class ReadMastercardInvoiceRawdata {

	public HashMap<String, Object> readINVOICEPDFFile(MultipartFile file, Connection conn, CompareSetupBean beanObj) {
		HashMap<String, Object> output = new HashMap<String, Object>();
		String line = null;
		boolean DomesticData = false, IssuerData = false, AcquirerData = false, storeData = false,
				reversal_records = false;
		int lineIdentifier = 0, startingLine = 0, reversal_start = 0, exception_start = 0;
		int lineNumber = 0, iss_srl_no = 1, acq_srl_no = 1, batchNumber = 0;
		String pan = null, thisline = "";
		int line_read = 1, batchSize = 0;
		boolean Exception_audit_records = false;
		String MSG_TYPE_IND = "", SWITCH_SERIAL_NUM = "", PROCESSOR_ACQ_OR_ISS = "", PROCESSOR_ID = "",
				TRANSACTION_DATE = "", TRANSACTION_TIME = "", PAN_LENGTH = "", PAN = "", PROCESSING_CODE = "",
				TRACE_NUMBER = "", MERCHANT_TYPE = "", POS_ENTRY = "", REFERENCE_NUMBER = "",
				ACQUIRER_INSTITUTION_ID = "", TERMINAL_ID = "", RESPONSE_CODE = "", BRAND = "", ADVICE_REASON_CODE = "",
				INTERACURRENCY_AGREEMENT_CODE = "", AUTHORIZATION_ID = "", CURRENCY_CODE = "", IMPLIED_DECIMAL = "",
				COMPLETED_AMT_TRANS = "", COMPLETED_AMT_INDICATOR = "", CASH_BACK_AMT = "", CASH_BACK_INDICATOR = "",
				ACCESS_FEE = "", ACCESS_FEE_INDICATOR = "", CURRENCY_CODE_SETTLEMENT = "",
				IMPLIED_DECIMAL_SETTLEMENT = "", CONVERSION_RATE_SETTLEMENT = "", COMPLETED_AMT_SETTLEMENT = "",
				COMPLETED_AMT_SETTLEMENT_INDICATOR = "", INTERCHANGE_FEE = "", INTERCHANGE_FEE_INDICATOR = "",
				SERVICE_LEVEL_INDICATOR = "", RESPONSE_CODE_2 = "", FILLER = "", POSITIVE_ID_INDICATOR = "",
				ATM_SURCHARGE_FREE_PROGRAM_ID = "", CROSS_BORDER_INDICATOR = "", CROSS_BORDER_CURRENCY_INDICATOR = "",
				VISA_ISA_FEE_INDICATOR = "", REQUEST_ATM_TRANS_LOCAL = "", FILLER_ADJ = "",
				TRACE_NUMBER_ADJUSTMENT_TRANS = "", RECON_ACTIVITY = "";
		String INSERT_DATA = "INSERT INTO MASTERCARD_ATM_RAWDATA (MSG_TYPE_IND, SWITCH_SERIAL_NUM, PROCESSOR_ACQ_OR_ISS, PROCESSOR_ID, TRANSACTION_DATE, TRANSACTION_TIME, PAN_LENGTH, PAN, PROCESSING_CODE, TRACE_NUMBER, MERCHANT_TYPE, POS_ENTRY, REFERENCE_NUMBER, ACQUIRER_INSTITUTION_ID, TERMINAL_ID, RESPONSE_CODE, BRAND, ADVICE_REASON_CODE, INTERACURRENCY_AGREEMENT_CODE, AUTHORIZATION_ID, CURRENCY_CODE, IMPLIED_DECIMAL, COMPLETED_AMT_TRANS, COMPLETED_AMT_INDICATOR, CASH_BACK_AMT, CASH_BACK_INDICATOR, ACCESS_FEE, ACCESS_FEE_INDICATOR, CURRENCY_CODE_SETTLEMENT, IMPLIED_DECIMAL_SETTLEMENT, CONVERSION_RATE_SETTLEMENT, COMPLETED_AMT_SETTLEMENT, COMPLETED_AMT_SETTLEMENT_INDICATOR, INTERCHANGE_FEE, INTERCHANGE_FEE_INDICATOR, SERVICE_LEVEL_INDICATOR, RESPONSE_CODE_2, FILLER, POSITIVE_ID_INDICATOR, ATM_SURCHARGE_FREE_PROGRAM_ID, CROSS_BORDER_INDICATOR, CROSS_BORDER_CURRENCY_INDICATOR, VISA_ISA_FEE_INDICATOR, REQUEST_ATM_TRANS_LOCAL, FILLER_ADJ, TRACE_NUMBER_ADJUSTMENT_TRANS, RECON_ACTIVITY, FILEDATE, FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		System.out.println("insert QUERY " + INSERT_DATA);
		try {

			PreparedStatement ps = conn.prepareStatement(INSERT_DATA);

			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));

			while ((thisline = br.readLine()) != null) {
				if (lineNumber > 2 && thisline.trim().startsWith("NREC") || thisline.trim().startsWith("FREC")) {

					// System.out.println("thisline.substring(1, 4).trim() "+ thisline.substring(0,
					// 4).trim());//
					MSG_TYPE_IND = thisline.substring(0, 4).trim();
					SWITCH_SERIAL_NUM = thisline.substring(4, 17).trim();
					PROCESSOR_ACQ_OR_ISS = thisline.substring(13, 14).trim();
					PROCESSOR_ID = thisline.substring(14, 18).trim();
					TRANSACTION_DATE = thisline.substring(18, 24).trim();
					TRANSACTION_TIME = thisline.substring(24, 30).trim();
					PAN_LENGTH = thisline.substring(30, 32).trim();
					PAN = thisline.substring(32, 51).trim();
					// demmy1 = thisline.substring(48, 51).trim();
					PROCESSING_CODE = thisline.substring(51, 57).trim();
					TRACE_NUMBER = thisline.substring(57, 63).trim();
					MERCHANT_TYPE = thisline.substring(63, 67).trim();
					POS_ENTRY = thisline.substring(67, 70).trim();
					REFERENCE_NUMBER = thisline.substring(70, 82).trim();
					ACQUIRER_INSTITUTION_ID = thisline.substring(82, 92).trim();
					TERMINAL_ID = thisline.substring(92, 102).trim();
					RESPONSE_CODE = thisline.substring(102, 104).trim();
					BRAND = thisline.substring(104, 107).trim();
					ADVICE_REASON_CODE = thisline.substring(107, 114).trim();
					INTERACURRENCY_AGREEMENT_CODE = thisline.substring(114, 118).trim();
					AUTHORIZATION_ID = thisline.substring(118, 124).trim();
					CURRENCY_CODE = thisline.substring(124, 127).trim();
					IMPLIED_DECIMAL = thisline.substring(127, 128).trim();
					COMPLETED_AMT_TRANS = thisline.substring(128, 140).trim();
					COMPLETED_AMT_INDICATOR = thisline.substring(140, 141).trim();
					CASH_BACK_AMT = thisline.substring(141, 153).trim();
					CASH_BACK_INDICATOR = thisline.substring(153, 154).trim();
					ACCESS_FEE = thisline.substring(154, 162).trim();
					ACCESS_FEE_INDICATOR = thisline.substring(162, 163).trim();
					CURRENCY_CODE_SETTLEMENT = thisline.substring(163, 166).trim();
					IMPLIED_DECIMAL_SETTLEMENT = thisline.substring(166, 167).trim();
					CONVERSION_RATE_SETTLEMENT = thisline.substring(167, 175).trim();
					COMPLETED_AMT_SETTLEMENT = thisline.substring(175, 187).trim();
					COMPLETED_AMT_SETTLEMENT_INDICATOR = thisline.substring(187, 188).trim();
					INTERCHANGE_FEE = thisline.substring(188, 198).trim();
					INTERCHANGE_FEE_INDICATOR = thisline.substring(198, 199).trim();
					SERVICE_LEVEL_INDICATOR = thisline.substring(199, 201).trim();
					RESPONSE_CODE_2 = thisline.substring(201, 203).trim();
					FILLER = thisline.substring(205, 214).trim();
					POSITIVE_ID_INDICATOR = thisline.substring(214, 215).trim();
					ATM_SURCHARGE_FREE_PROGRAM_ID = thisline.substring(215, 216).trim();
					CROSS_BORDER_INDICATOR = thisline.substring(216, 217).trim();
					CROSS_BORDER_CURRENCY_INDICATOR = thisline.substring(217, 218).trim();
					VISA_ISA_FEE_INDICATOR = thisline.substring(218, 219).trim();
					REQUEST_ATM_TRANS_LOCAL = thisline.substring(219, 231).trim();
					FILLER_ADJ = thisline.substring(231, 243).trim();
					TRACE_NUMBER_ADJUSTMENT_TRANS = thisline.substring(243, 249).trim();
					RECON_ACTIVITY = "";

					lineNumber++;
					ps.setString(1, MSG_TYPE_IND);
					ps.setString(2, SWITCH_SERIAL_NUM);
					ps.setString(3, PROCESSOR_ACQ_OR_ISS);
					ps.setString(4, PROCESSOR_ID);
					ps.setString(5, TRANSACTION_DATE);
					ps.setString(6, TRANSACTION_TIME);
					ps.setString(7, PAN_LENGTH);
					ps.setString(8, PAN);
					ps.setString(9, PROCESSING_CODE);
					ps.setString(10, TRACE_NUMBER);
					ps.setString(11, MERCHANT_TYPE);
					ps.setString(12, POS_ENTRY);
					ps.setString(13, REFERENCE_NUMBER);
					ps.setString(14, ACQUIRER_INSTITUTION_ID);
					ps.setString(15, TERMINAL_ID);
					ps.setString(16, RESPONSE_CODE);
					ps.setString(17, BRAND);
					ps.setString(18, ADVICE_REASON_CODE);
					ps.setString(19, INTERACURRENCY_AGREEMENT_CODE);
					ps.setString(20, AUTHORIZATION_ID);
					ps.setString(21, CURRENCY_CODE);
					ps.setString(22, IMPLIED_DECIMAL);
					ps.setString(23, COMPLETED_AMT_TRANS);
					ps.setString(24, COMPLETED_AMT_INDICATOR);
					ps.setString(25, CASH_BACK_AMT);
					ps.setString(26, CASH_BACK_INDICATOR);
					ps.setString(27, ACCESS_FEE);
					ps.setString(28, ACCESS_FEE_INDICATOR);
					ps.setString(29, CURRENCY_CODE_SETTLEMENT);
					ps.setString(30, IMPLIED_DECIMAL_SETTLEMENT);
					ps.setString(31, CONVERSION_RATE_SETTLEMENT);
					ps.setString(32, COMPLETED_AMT_SETTLEMENT);
					ps.setString(33, COMPLETED_AMT_SETTLEMENT_INDICATOR);
					ps.setString(34, INTERCHANGE_FEE);
					ps.setString(35, INTERCHANGE_FEE_INDICATOR);
					ps.setString(36, SERVICE_LEVEL_INDICATOR);
					ps.setString(37, RESPONSE_CODE_2);
					ps.setString(38, FILLER);
					ps.setString(39, POSITIVE_ID_INDICATOR);
					ps.setString(40, ATM_SURCHARGE_FREE_PROGRAM_ID);
					ps.setString(41, CROSS_BORDER_INDICATOR);
					ps.setString(42, CROSS_BORDER_CURRENCY_INDICATOR);
					ps.setString(43, VISA_ISA_FEE_INDICATOR);
					ps.setString(44, REQUEST_ATM_TRANS_LOCAL);
					ps.setString(45, FILLER_ADJ);
					ps.setString(46, TRACE_NUMBER_ADJUSTMENT_TRANS);
					ps.setString(47, RECON_ACTIVITY);
					ps.setString(48, beanObj.getFileDate());
					ps.setString(49, file.getOriginalFilename());

					ps.addBatch();

					// logger.info("Executed Batch SuccessFully " + count);

					if (batchSize == 1000) {
						batchNumber++;
						System.out.println("Batch Executed is " + batchNumber);
						ps.executeBatch();
						batchNumber = 0;
						batchSize = 0;
					}
				}
			}

			ps.executeBatch();
			ps.close();

			conn.close();

			output.put("result", true);

			output.put("msg", "File Uploaded and Record count is " + lineNumber);
			System.out.println("completed read464File " + lineNumber);
			return output;

		} catch (Exception e) {
			output.put("result", false);
			output.put("msg", "File reading Failed due to Line Number " + lineNumber);
			System.out.println("Exception in read464File " + e + lineNumber);
		}
		return output;
	}

	public HashMap<String, Object> read464File(MultipartFile file, Connection conn, CompareSetupBean beanObj) {
		HashMap<String, Object> output = new HashMap<String, Object>();
		String line = null;
		boolean DomesticData = false, IssuerData = false, AcquirerData = false, storeData = false,
				reversal_records = false;
		int lineIdentifier = 0, startingLine = 0, reversal_start = 0, exception_start = 0;
		int lineNumber = 0, iss_srl_no = 1, acq_srl_no = 1, batchNumber = 0;
		String pan = null, thisline = "";
		int line_read = 1, batchSize = 0;
		boolean Exception_audit_records = false;
		String MSG_TYPE_IND = "", SWITCH_SERIAL_NUM = "", PROCESSOR_ACQ_OR_ISS = "", PROCESSOR_ID = "",
				TRANSACTION_DATE = "", TRANSACTION_TIME = "", PAN_LENGTH = "", PAN = "", PROCESSING_CODE = "",
				TRACE_NUMBER = "", MERCHANT_TYPE = "", POS_ENTRY = "", REFERENCE_NUMBER = "",
				ACQUIRER_INSTITUTION_ID = "", TERMINAL_ID = "", RESPONSE_CODE = "", BRAND = "", ADVICE_REASON_CODE = "",
				INTERACURRENCY_AGREEMENT_CODE = "", AUTHORIZATION_ID = "", CURRENCY_CODE = "", IMPLIED_DECIMAL = "",
				COMPLETED_AMT_TRANS = "", COMPLETED_AMT_INDICATOR = "", CASH_BACK_AMT = "", CASH_BACK_INDICATOR = "",
				ACCESS_FEE = "", ACCESS_FEE_INDICATOR = "", CURRENCY_CODE_SETTLEMENT = "",
				IMPLIED_DECIMAL_SETTLEMENT = "", CONVERSION_RATE_SETTLEMENT = "", COMPLETED_AMT_SETTLEMENT = "",
				COMPLETED_AMT_SETTLEMENT_INDICATOR = "", INTERCHANGE_FEE = "", INTERCHANGE_FEE_INDICATOR = "",
				SERVICE_LEVEL_INDICATOR = "", RESPONSE_CODE_2 = "", FILLER = "", POSITIVE_ID_INDICATOR = "",
				ATM_SURCHARGE_FREE_PROGRAM_ID = "", CROSS_BORDER_INDICATOR = "", CROSS_BORDER_CURRENCY_INDICATOR = "",
				VISA_ISA_FEE_INDICATOR = "", REQUEST_ATM_TRANS_LOCAL = "", FILLER_ADJ = "",
				TRACE_NUMBER_ADJUSTMENT_TRANS = "", RECON_ACTIVITY = "";
		String INSERT_DATA = "INSERT INTO MASTERCARD_ATM_RAWDATA (MSG_TYPE_IND, SWITCH_SERIAL_NUM, PROCESSOR_ACQ_OR_ISS, PROCESSOR_ID, TRANSACTION_DATE, TRANSACTION_TIME, PAN_LENGTH, PAN, PROCESSING_CODE, TRACE_NUMBER, MERCHANT_TYPE, POS_ENTRY, REFERENCE_NUMBER, ACQUIRER_INSTITUTION_ID, TERMINAL_ID, RESPONSE_CODE, BRAND, ADVICE_REASON_CODE, INTERACURRENCY_AGREEMENT_CODE, AUTHORIZATION_ID, CURRENCY_CODE, IMPLIED_DECIMAL, COMPLETED_AMT_TRANS, COMPLETED_AMT_INDICATOR, CASH_BACK_AMT, CASH_BACK_INDICATOR, ACCESS_FEE, ACCESS_FEE_INDICATOR, CURRENCY_CODE_SETTLEMENT, IMPLIED_DECIMAL_SETTLEMENT, CONVERSION_RATE_SETTLEMENT, COMPLETED_AMT_SETTLEMENT, COMPLETED_AMT_SETTLEMENT_INDICATOR, INTERCHANGE_FEE, INTERCHANGE_FEE_INDICATOR, SERVICE_LEVEL_INDICATOR, RESPONSE_CODE_2, FILLER, POSITIVE_ID_INDICATOR, ATM_SURCHARGE_FREE_PROGRAM_ID, CROSS_BORDER_INDICATOR, CROSS_BORDER_CURRENCY_INDICATOR, VISA_ISA_FEE_INDICATOR, REQUEST_ATM_TRANS_LOCAL, FILLER_ADJ, TRACE_NUMBER_ADJUSTMENT_TRANS, RECON_ACTIVITY, FILEDATE, FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		System.out.println("insert QUERY " + INSERT_DATA);
		try {

			PreparedStatement ps = conn.prepareStatement(INSERT_DATA);

			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));

			while ((thisline = br.readLine()) != null) {
				if (lineNumber > 2 && thisline.trim().startsWith("NREC") || thisline.trim().startsWith("FREC")) {

					// System.out.println("thisline.substring(1, 4).trim() "+ thisline.substring(0,
					// 4).trim());
					MSG_TYPE_IND = thisline.substring(0, 4).trim();
					SWITCH_SERIAL_NUM = thisline.substring(4, 17).trim();
					PROCESSOR_ACQ_OR_ISS = thisline.substring(13, 14).trim();
					PROCESSOR_ID = thisline.substring(14, 18).trim();
					TRANSACTION_DATE = thisline.substring(18, 24).trim();
					TRANSACTION_TIME = thisline.substring(24, 30).trim();
					PAN_LENGTH = thisline.substring(30, 32).trim();
					PAN = thisline.substring(32, 51).trim();
					// demmy1 = thisline.substring(48, 51).trim();
					PROCESSING_CODE = thisline.substring(51, 57).trim();
					TRACE_NUMBER = thisline.substring(57, 63).trim();
					MERCHANT_TYPE = thisline.substring(63, 67).trim();
					POS_ENTRY = thisline.substring(67, 70).trim();
					REFERENCE_NUMBER = thisline.substring(70, 82).trim();
					ACQUIRER_INSTITUTION_ID = thisline.substring(82, 92).trim();
					TERMINAL_ID = thisline.substring(92, 102).trim();
					RESPONSE_CODE = thisline.substring(102, 104).trim();
					BRAND = thisline.substring(104, 107).trim();
					ADVICE_REASON_CODE = thisline.substring(107, 114).trim();
					INTERACURRENCY_AGREEMENT_CODE = thisline.substring(114, 118).trim();
					AUTHORIZATION_ID = thisline.substring(118, 124).trim();
					CURRENCY_CODE = thisline.substring(124, 127).trim();
					IMPLIED_DECIMAL = thisline.substring(127, 128).trim();
					COMPLETED_AMT_TRANS = thisline.substring(128, 140).trim();
					COMPLETED_AMT_INDICATOR = thisline.substring(140, 141).trim();
					CASH_BACK_AMT = thisline.substring(141, 153).trim();
					CASH_BACK_INDICATOR = thisline.substring(153, 154).trim();
					ACCESS_FEE = thisline.substring(154, 162).trim();
					ACCESS_FEE_INDICATOR = thisline.substring(162, 163).trim();
					CURRENCY_CODE_SETTLEMENT = thisline.substring(163, 166).trim();
					IMPLIED_DECIMAL_SETTLEMENT = thisline.substring(166, 167).trim();
					CONVERSION_RATE_SETTLEMENT = thisline.substring(167, 175).trim();
					COMPLETED_AMT_SETTLEMENT = thisline.substring(175, 187).trim();
					COMPLETED_AMT_SETTLEMENT_INDICATOR = thisline.substring(187, 188).trim();
					INTERCHANGE_FEE = thisline.substring(188, 198).trim();
					INTERCHANGE_FEE_INDICATOR = thisline.substring(198, 199).trim();
					SERVICE_LEVEL_INDICATOR = thisline.substring(199, 201).trim();
					RESPONSE_CODE_2 = thisline.substring(201, 203).trim();
					FILLER = thisline.substring(205, 214).trim();
					POSITIVE_ID_INDICATOR = thisline.substring(214, 215).trim();
					ATM_SURCHARGE_FREE_PROGRAM_ID = thisline.substring(215, 216).trim();
					CROSS_BORDER_INDICATOR = thisline.substring(216, 217).trim();
					CROSS_BORDER_CURRENCY_INDICATOR = thisline.substring(217, 218).trim();
					VISA_ISA_FEE_INDICATOR = thisline.substring(218, 219).trim();
					REQUEST_ATM_TRANS_LOCAL = thisline.substring(219, 231).trim();
					FILLER_ADJ = thisline.substring(231, 243).trim();
					TRACE_NUMBER_ADJUSTMENT_TRANS = thisline.substring(243, 249).trim();
					RECON_ACTIVITY = "";

					lineNumber++;
					ps.setString(1, MSG_TYPE_IND);
					ps.setString(2, SWITCH_SERIAL_NUM);
					ps.setString(3, PROCESSOR_ACQ_OR_ISS);
					ps.setString(4, PROCESSOR_ID);
					ps.setString(5, TRANSACTION_DATE);
					ps.setString(6, TRANSACTION_TIME);
					ps.setString(7, PAN_LENGTH);
					ps.setString(8, PAN);
					ps.setString(9, PROCESSING_CODE);
					ps.setString(10, TRACE_NUMBER);
					ps.setString(11, MERCHANT_TYPE);
					ps.setString(12, POS_ENTRY);
					ps.setString(13, REFERENCE_NUMBER);
					ps.setString(14, ACQUIRER_INSTITUTION_ID);
					ps.setString(15, TERMINAL_ID);
					ps.setString(16, RESPONSE_CODE);
					ps.setString(17, BRAND);
					ps.setString(18, ADVICE_REASON_CODE);
					ps.setString(19, INTERACURRENCY_AGREEMENT_CODE);
					ps.setString(20, AUTHORIZATION_ID);
					ps.setString(21, CURRENCY_CODE);
					ps.setString(22, IMPLIED_DECIMAL);
					ps.setString(23, COMPLETED_AMT_TRANS);
					ps.setString(24, COMPLETED_AMT_INDICATOR);
					ps.setString(25, CASH_BACK_AMT);
					ps.setString(26, CASH_BACK_INDICATOR);
					ps.setString(27, ACCESS_FEE);
					ps.setString(28, ACCESS_FEE_INDICATOR);
					ps.setString(29, CURRENCY_CODE_SETTLEMENT);
					ps.setString(30, IMPLIED_DECIMAL_SETTLEMENT);
					ps.setString(31, CONVERSION_RATE_SETTLEMENT);
					ps.setString(32, COMPLETED_AMT_SETTLEMENT);
					ps.setString(33, COMPLETED_AMT_SETTLEMENT_INDICATOR);
					ps.setString(34, INTERCHANGE_FEE);
					ps.setString(35, INTERCHANGE_FEE_INDICATOR);
					ps.setString(36, SERVICE_LEVEL_INDICATOR);
					ps.setString(37, RESPONSE_CODE_2);
					ps.setString(38, FILLER);
					ps.setString(39, POSITIVE_ID_INDICATOR);
					ps.setString(40, ATM_SURCHARGE_FREE_PROGRAM_ID);
					ps.setString(41, CROSS_BORDER_INDICATOR);
					ps.setString(42, CROSS_BORDER_CURRENCY_INDICATOR);
					ps.setString(43, VISA_ISA_FEE_INDICATOR);
					ps.setString(44, REQUEST_ATM_TRANS_LOCAL);
					ps.setString(45, FILLER_ADJ);
					ps.setString(46, TRACE_NUMBER_ADJUSTMENT_TRANS);
					ps.setString(47, RECON_ACTIVITY);
					ps.setString(48, beanObj.getFileDate());
					ps.setString(49, file.getOriginalFilename());

					ps.addBatch();

					// logger.info("Executed Batch SuccessFully " + count);

					if (batchSize == 1000) {
						batchNumber++;
						System.out.println("Batch Executed is " + batchNumber);
						ps.executeBatch();
						batchNumber = 0;
						batchSize = 0;
					}
				}
			}

			ps.executeBatch();
			ps.close();

			conn.close();

			output.put("result", true);

			output.put("msg", "File Uploaded and Record count is " + lineNumber);
			System.out.println("completed read464File " + lineNumber);
			return output;

		} catch (Exception e) {
			output.put("result", false);
			output.put("msg", "File reading Failed due to Line Number " + lineNumber);
			System.out.println("Exception in read464File " + e + lineNumber);
		}
		return output;
	}

	public HashMap<String, Object> read461File2(MultipartFile file, Connection conn, CompareSetupBean beanObj) {
		HashMap<String, Object> output = new HashMap<String, Object>();
		String line = null;
		boolean DomesticData = false, IssuerData = false, AcquirerData = false, storeData = false,
				reversal_records = false;
		int lineIdentifier = 0, startingLine = 0, reversal_start = 0, exception_start = 0;
		int lineNumber = 0, iss_srl_no = 1, acq_srl_no = 1;
		String pan = null;
		int line_read = 1;
		boolean Exception_audit_records = false;

		String INSERT_DATA = "INSERT INTO MASTERCARD_461_RAWDATA(SRL_NO, PROC_CODE, NUM_APPROVED, NUM_DENIALS, RECON_AMOUNT, FINANCIAL_COUNT, NON_FINANCIAL_COUNT, NON_BILL_COUNT, TRANS_FEE, "
				+ "TRANS_TYPE, SUBCAT_ISS_ACQ, AMT_PART_TRAN_TYPE, FEE_PART_TRAN_TYPE, CREATEDBY, SETTLEMENT_DATE, FILENAME,FILEDATE) "
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";

		String INSERT_REVERSAL = "INSERT INTO MASTERCARD_461_REVERSAL (PAN,  ADV_REG_CODE, ORIG_AMOUNT, RESP_CODE, AUTH_ID, SWITCH_SERIAL_NO, TRACE, AMOUNT_FEE, AMT_CR_DR, CREATEDBY, FILETYPE, ISS_ACQ, SETTLEMENT_DATE,FILENAME,FILEDATE)"
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?, ?,?,?)";

		String INSERT_EXCEPTION = "INSERT INTO MASTERCARD_461_REVERSAL (PAN, BRAND, ADV_REG_CODE, SWITCH_SERIAL_NO, TRACE, REF_NO, ORIG_AMOUNT, RESP_CODE, AUTH_ID, TERMINAL_ID, AMOUNT_FEE, AMT_CR_DR, CREATEDBY, FILETYPE, ISS_ACQ, SETTLEMENT_DATE,FILENAME,FILEDATE)"
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		System.out.println("insert QUERY " + INSERT_DATA + " and  " + INSERT_REVERSAL + " and  " + INSERT_EXCEPTION);
		try {
			PreparedStatement pst = conn.prepareStatement(INSERT_DATA);
			PreparedStatement rev_pst = conn.prepareStatement(INSERT_REVERSAL);
			PreparedStatement rev_pst1 = conn.prepareStatement(INSERT_EXCEPTION);
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			while ((line = br.readLine()) != null) {
				lineNumber++;

				System.out.println(lineNumber);
				String[] values = line.split(":");
				if (line.trim().startsWith("ACQUIRING PROCESSOR")) {
					IssuerData = false;
					AcquirerData = true;
				} else if (line.trim().startsWith("ISSUING PROCESSOR")) {
					AcquirerData = false;
					IssuerData = true;
				}
				if (values[0].trim().equals("CURRENCY")) {
					// lineIdentifier++;
					if (values[1].trim().equalsIgnoreCase("356   Indian Rupee")) {
						System.out.println("inr ");
						lineIdentifier++;
						DomesticData = true;
						continue;
					} else if (values[1].trim().equalsIgnoreCase("840   U.S. Dollar")) {
						DomesticData = true;
						System.out.println(" U.S");
						continue;
					}

				}

				if (line.contains("R E V E R S A L S   R E P O R T")) {
					lineIdentifier = 1;
					reversal_records = true;
					continue;
				}
				if (lineIdentifier == 1 && reversal_records) {
					// System.out.println("LIne is "+line );
					if (line.contains("A C Q U I R I N G   P R O C E S S O R")) {
						IssuerData = false;
						AcquirerData = true;
						lineIdentifier++;
						DomesticData = true;
						continue;
					} else if (line.contains(" I S S U I N G      P R O C E S S O R")) {
						AcquirerData = false;
						IssuerData = true;
						lineIdentifier++;
						DomesticData = true;
						continue;
					}

				}
				if (line.trim().startsWith("E X C E P T I O N   A U D I T   R E P O R T")) {
					lineIdentifier++;
					Exception_audit_records = true;
					continue;
				}

				if (lineIdentifier == 1 && Exception_audit_records) {
					if (line.contains("A C Q U I R I N G   P R O C E S S O R")) {
						IssuerData = false;
						AcquirerData = true;
						lineIdentifier++;
						DomesticData = true;
						continue;
					} else if (line.contains(" I S S U I N G      P R O C E S S O R")) {
						AcquirerData = false;
						IssuerData = true;
						lineIdentifier++;
						DomesticData = true;
						continue;
					}
				}

				if (lineIdentifier == 1) {
					lineIdentifier++;
					continue;
				} else if (lineIdentifier == 2 && DomesticData) {
					if (IssuerData) {
						if (line.startsWith("0 DESCRIPTION") && line.contains("-----------") && !storeData) {
							storeData = true;
							startingLine++;
							continue;
						} else if (reversal_records && (reversal_start == 0 || reversal_start < 2)) {
							System.out.println("----------ISSUER REVERSAL------------------");
							System.out.println("Reversal records starts " + lineNumber);
							storeData = true;
							reversal_start++;
							continue;
						} else if (Exception_audit_records && (exception_start == 0 || exception_start < 2)) {
							System.out.println("------------- ISSUER EXCEPTION RECORDS ------------");
							storeData = true;
							exception_start++;
							continue;
						}

						if (storeData) {
							if (reversal_records && reversal_start == 2) {
								if (line.equalsIgnoreCase(
										"                                                                                             REPRESENTMENT     PRE-AUTH              ")) {
									reversal_records = false;
									reversal_start = 0;
									IssuerData = false;
									lineIdentifier = 0;
									continue;
								} else {

									// System.out.println(line);
									// code to separate data
									if (!line.trim().equals("")) {

										if (line.startsWith(" PAN")) {
											System.out.println("Pan "
													+ line.substring(11, line.trim().length()).replaceAll("^0*", ""));
											rev_pst.setString(1,
													line.substring(11, line.length()).trim().replaceAll("^0*", ""));
											continue;
										} else if (line.trim().startsWith("ADV REASON")
												|| line.trim().startsWith("RESP CODE")
												|| line.contains("----------------------")
												|| line.trim().startsWith("PROCESSOR"))
											continue;
										else if (line.contains("1SWCHD353")
												|| line.trim().contains("I S S U I N G   P R O C E S S O R")
												|| line.trim().startsWith("R E V E R S A L S   R E P O R T")
												|| line.contains(":"))
											continue;
										else {
											if (line_read == 1) {
												System.out
														.println("ADv code " + line.substring(0, 11).replace(" ", ""));
												System.out.println("Amount " + line.substring(123, 131));
												rev_pst.setString(2, line.substring(0, 11).replace(" ", "").trim());
												rev_pst.setString(3, line.substring(123, 131).trim());
												line_read++;
											} else if (line_read == 2) {
												System.out.println("resp code " + line.substring(0, 4));
												System.out.println("Auth " + line.substring(5, 13));
												System.out.println("switch serial " + line.substring(13, 30));
												System.out.println("trace " + line.substring(30, 45));
												System.out.println("fee and amount " + line.substring(123, 131));
												System.out.println("Amount sign " + line.substring(131, 132));
												line_read = 1;
												rev_pst.setString(4, line.substring(0, 4).trim());
												rev_pst.setString(5, line.substring(5, 13).trim());
												rev_pst.setString(6, line.substring(13, 30).trim());
												rev_pst.setString(7, line.substring(30, 45).trim());
												rev_pst.setString(8, line.substring(123, 131).trim());
												rev_pst.setString(9, line.substring(131, 132).trim());
												rev_pst.setString(10, beanObj.getCreatedBy().trim());
												rev_pst.setString(11, "AP");
												rev_pst.setString(12, "ISS");
												rev_pst.setString(13, beanObj.getFileDate());
												rev_pst.setString(14, file.getOriginalFilename());
												rev_pst.setString(15, beanObj.getFileDate());
												rev_pst.execute();

											}
										}
									}
								}

							} else if (Exception_audit_records && exception_start == 2) {

								if (line.contains(":") || line.trim().startsWith("CNTL#")
										|| line.trim().contains("I S S U I N G   P R O C E S S O R")
										|| line.trim().startsWith("E X C E P T I O N   A U D I T   R E P O R T"))
									continue;
								else if (line.contains("1SWCHD53") || line.equalsIgnoreCase(
										"0                                                                           REVERSED                       REPRESENTMENT             ")) {
									Exception_audit_records = false;
									reversal_start = 0;
									lineIdentifier = 0;
									exception_start = 0;
									continue;
								} else {

									System.out.println("Line " + line);
									if (line.startsWith("0PAN")) {
										System.out.println("pan " + line.substring(9, 31).trim().replaceAll("^0*", ""));
										System.out.println("Brand " + line.substring(124, line.length()).trim());
										rev_pst1.setString(1, line.substring(9, 31).trim().replaceAll("^0*", ""));
										rev_pst1.setString(2, line.substring(124, line.length()).trim());
									} else if (line.startsWith("  ADV REASON") && line.contains("SWCH DATE")) {
										System.out.println(
												"Adv reg code " + line.substring(16, 28).trim().replace(" ", ""));
										System.out.println(
												"switch serial num " + line.substring(121, line.length()).trim());

										rev_pst1.setString(3, line.substring(16, 28).trim().replace(" ", ""));
										rev_pst1.setString(4, line.substring(121, line.length()).trim());

									} else if (line.startsWith("  TRACE NO")) {
										System.out.println("trace num " + line.substring(16, 28).trim());
										rev_pst1.setString(5, line.substring(16, 28).trim());

									} else if (line.startsWith("  REFERENCE NO")) {
										System.out.println("ref no " + line.substring(16, 31).trim());
										System.out.println("orig amt " + line.substring(117, line.length()).trim());

										rev_pst1.setString(6, line.substring(16, 31).trim());

									} else if (line.startsWith("  RESP CODE-A")) {
										System.out.println("Response code " + line.substring(14, 17));
										System.out.println("Auth " + line.substring(18, 30).trim());
										System.out.println("Terminal id " + line.substring(44, 60).trim());

										rev_pst1.setString(8, line.substring(14, 17));
										rev_pst1.setString(9, line.substring(18, 30).trim());
										rev_pst1.setString(10, line.substring(44, 60).trim());

									} else if (line.startsWith("  ADV REASON -A")) {
										System.out.println("Fee amt " + line.substring(116, 131).trim());
										System.out.println("amt dr cr " + line.substring(131, line.length()).trim());

										rev_pst1.setString(7,
												(Integer.parseInt(line.substring(116, 128).trim()) - 10) + "");
										rev_pst1.setString(11, line.substring(116, 131).trim());
										rev_pst1.setString(12, line.substring(131, line.length()).trim());
										rev_pst1.setString(13, beanObj.getCreatedBy());
										rev_pst1.setString(14, "AP");
										rev_pst1.setString(15, "ISS");
										rev_pst1.setString(16, beanObj.getFileDate());
										rev_pst1.setString(17, file.getOriginalFilename());
										rev_pst1.setString(18, beanObj.getFileDate());
										rev_pst1.execute();
									}
								}
							} else {
								if (startingLine < 4) {
									startingLine++;
									continue;
								}

								if (startingLine == 4) {

									if (line.trim().startsWith("TOTALS")) {
										startingLine = 0;
										lineIdentifier = 0;
										storeData = false;
									} else if (line.contains("---------------------------------------------"))
										continue;
									else {
										if (line.trim().length() > 50) {
											System.out.println(
													"************************ ISSUER DATA ************************");
											/*
											 * (SRL_NO, PROC_CODE, NUM_APPROVED, NUM_DENIALS, RECON_AMOUNT,
											 * FINANCIAL_COUNT, NON_FINANCIAL_COUNT, NON_BILL_COUNT, TRANS_FEE, " +
											 * "TRANS_TYPE, SUBCAT_ISS_ACC, AMT_PART_TRAN_TYPE, FEE_PART_TRAN_TYPE,
											 * CREATEDBY, SETTLEMENT_DATE)
											 */
											pst.setInt(1, iss_srl_no);
											pst.setString(2, line.substring(0, 20).trim());
											pst.setString(3, line.substring(21, 32).trim());
											pst.setString(4, line.substring(33, 42).trim());
											pst.setString(5, line.substring(43, 60).trim());
											pst.setString(6, line.substring(64, 73).trim());
											pst.setString(7, line.substring(84, 93).trim());
											pst.setString(8, line.substring(94, 103).trim());
											pst.setString(9, line.substring(104, 124).trim());
											pst.setString(10, "AP");
											pst.setString(11, "ISSUER");
											pst.setString(12, line.substring(60, 63).trim());
											pst.setString(13, line.substring(125, line.length()).trim());
											pst.setString(14, beanObj.getCreatedBy());
											pst.setString(15, beanObj.getFileDate());
											pst.setString(16, file.getOriginalFilename());
											pst.setString(17, beanObj.getFileDate());
											pst.execute();
											iss_srl_no++;

										}
									}
								}
							}
						} else {
							lineIdentifier = 0;
						}
					} else if (AcquirerData) {

						if (line.startsWith("0 DESCRIPTION") && line.contains("-----------") && !storeData) {
							storeData = true;
							startingLine++;
							continue;
						} else if (reversal_records && (reversal_start == 0 || reversal_start < 2)) {
							System.out.println("----------ACQUIRER REVERSAL------------------");
							System.out.println("Reversal records starts " + lineNumber);
							storeData = true;
							reversal_start++;
							continue;
						} else if (Exception_audit_records && (exception_start == 0 || exception_start < 2)) {
							System.out.println("------------- Acquirer EXCEPTION RECORDS ------------");
							storeData = true;
							exception_start++;
							continue;
						}

						if (storeData) {
							if (reversal_records && reversal_start == 2) {
								if (line.equalsIgnoreCase(
										"                                                                                             REPRESENTMENT     PRE-AUTH              ")) {
									reversal_records = false;
									reversal_start = 0;
									AcquirerData = false;
									lineIdentifier = 0;
									continue;
								} else {

									// System.out.println(line);
									// code to separate data
									if (!line.trim().equals("")) {

										if (line.startsWith(" PAN")) {
											System.out.println("Pan "
													+ line.substring(11, line.trim().length()).replaceAll("^0*", ""));
											rev_pst.setString(1,
													line.substring(11, line.length()).trim().replaceAll("^0*", ""));
											continue;
										} else if (line.trim().startsWith("ADV REASON")
												|| line.trim().startsWith("RESP CODE")
												|| line.contains("----------------------"))
											continue;
										else if (line.contains("1SWCHD353")
												|| line.trim().contains("A C Q U I R I N G   P R O C E S S O R")
												|| line.trim().startsWith("R E V E R S A L S   R E P O R T")
												|| line.contains(":"))
											continue;
										else {
											if (line_read == 1) {
												System.out
														.println("ADv code " + line.substring(0, 11).replace(" ", ""));
												System.out.println("Amount " + line.substring(123, 131));
												rev_pst.setString(2, line.substring(0, 11).replace(" ", "").trim());
												rev_pst.setString(3, line.substring(123, 131).trim());
												line_read++;
											} else if (line_read == 2) {
												System.out.println("resp code " + line.substring(0, 4));
												System.out.println("Auth " + line.substring(5, 13));
												System.out.println("switch serial " + line.substring(13, 30));
												System.out.println("trace " + line.substring(30, 45));
												System.out.println("fee and amount " + line.substring(123, 131));
												System.out.println("Amount sign " + line.substring(131, 132));
												line_read = 1;
												rev_pst.setString(4, line.substring(0, 4).trim());
												rev_pst.setString(5, line.substring(5, 13).trim());
												rev_pst.setString(6, line.substring(13, 30).trim());
												rev_pst.setString(7, line.substring(30, 45).trim());
												rev_pst.setString(8, line.substring(123, 131).trim());
												rev_pst.setString(9, line.substring(131, 132).trim());
												rev_pst.setString(10, beanObj.getCreatedBy().trim());
												rev_pst.setString(11, "AP");
												rev_pst.setString(12, "ACQ");
												rev_pst.setString(13, beanObj.getFileDate());
												rev_pst.setString(14, file.getOriginalFilename());
												rev_pst.setString(15, beanObj.getFileDate());
												rev_pst.execute();
											}
										}
									}
								}

							} else if (Exception_audit_records && exception_start == 2) {

								if (line.contains(":") || line.trim().startsWith("CNTL#")
										|| line.trim().contains("A C Q U I R I N G   P R O C E S S O R")
										|| line.trim().startsWith("E X C E P T I O N   A U D I T   R E P O R T"))
									continue;
								else if (line.contains("1SWCHD53") || line.equalsIgnoreCase(
										"0                                                                           REVERSED                       REPRESENTMENT             ")) {
									Exception_audit_records = false;
									reversal_start = 0;
									lineIdentifier = 0;
									exception_start = 0;
									continue;
								} else {

									// System.out.println("Line "+line);
									if (line.startsWith("0PAN")) {
										System.out.println("pan " + line.substring(9, 31).trim().replaceAll("^0*", ""));
										System.out.println("Brand " + line.substring(124, line.length()).trim());
										rev_pst1.setString(1, line.substring(9, 31).trim().replaceAll("^0*", ""));
										rev_pst1.setString(2, line.substring(124, line.length()).trim());
									} else if (line.startsWith("  ADV REASON") && line.contains("SWCH DATE")) {
										System.out.println(
												"Adv reg code " + line.substring(16, 28).trim().replace(" ", ""));
										System.out.println(
												"switch serial num " + line.substring(121, line.length()).trim());

										rev_pst1.setString(3, line.substring(16, 28).trim().replace(" ", ""));
										rev_pst1.setString(4, line.substring(121, line.length()).trim());

									} else if (line.startsWith("  TRACE NO")) {
										System.out.println("trace num " + line.substring(16, 28).trim());
										rev_pst1.setString(5, line.substring(16, 28).trim());
									} else if (line.startsWith("  REFERENCE NO")) {
										System.out.println("ref no " + line.substring(16, 31).trim());
										System.out.println("orig amt " + line.substring(117, line.length()).trim());

										rev_pst1.setString(6, line.substring(16, 31).trim());
										rev_pst1.setString(7, line.substring(117, line.length()).trim());

									} else if (line.startsWith("  RESP CODE-A")) {
										System.out.println("Response code " + line.substring(14, 17));
										System.out.println("Auth " + line.substring(18, 30).trim());
										System.out.println("Terminal id " + line.substring(44, 60).trim());

										rev_pst1.setString(8, line.substring(14, 17));
										rev_pst1.setString(9, line.substring(18, 30).trim());
										rev_pst1.setString(10, line.substring(44, 60).trim());

									} else if (line.startsWith("  ADV REASON -A")) {
										System.out.println("Fee amt " + line.substring(116, 131).trim());
										System.out.println("amt dr cr " + line.substring(131, line.length()).trim());

										rev_pst1.setString(11, line.substring(116, 131).trim());
										rev_pst1.setString(12, line.substring(131, line.length()).trim());
										rev_pst1.setString(13, beanObj.getCreatedBy());
										rev_pst1.setString(14, "AP");
										rev_pst1.setString(15, "ACQ");
										rev_pst1.setString(16, beanObj.getFileDate());
										rev_pst1.setString(17, file.getOriginalFilename());
										rev_pst1.setString(18, beanObj.getFileDate());
										rev_pst1.execute();
									}
								}
							} else {
								if (startingLine < 4) {
									startingLine++;
									if (startingLine == 4 && line.startsWith("0 GRAND TOTALS")) {
										storeData = false;
									}

									continue;
								}

								if (startingLine == 4) {
									if (line.trim().startsWith("1SWCHD363") || line.startsWith("0 GRAND TOTALS")) {
										startingLine = 0;
										lineIdentifier = 0;
										storeData = false;
									} else if (line.contains("---------------------------------------------"))
										continue;
									else {
										if (line.trim().length() > 50) {
											pst.setInt(1, acq_srl_no);
											pst.setString(2, line.substring(0, 20).trim());
											pst.setString(3, line.substring(21, 32).trim());
											pst.setString(4, line.substring(33, 42).trim());
											pst.setString(5, line.substring(43, 60).trim());
											pst.setString(6, line.substring(64, 73).trim());
											pst.setString(7, line.substring(84, 93).trim());
											pst.setString(8, line.substring(94, 103).trim());
											pst.setString(9, line.substring(104, 124).trim());
											pst.setString(10, "AP");
											pst.setString(11, "ACQUIRER");
											pst.setString(12, line.substring(60, 63).trim());
											pst.setString(13, line.substring(125, line.length()).trim());
											pst.setString(14, beanObj.getCreatedBy());
											pst.setString(15, beanObj.getFileDate());
											pst.setString(16, file.getOriginalFilename());
											pst.setString(17, beanObj.getFileDate());

											pst.execute();
											acq_srl_no++;
											/*
											 * System.out.
											 * println("************************ ACQUIRER DATA ************************"
											 * ); System.out.println(line); System.out.println(line.substring(0,
											 * 20).trim()); System.out.println(line.substring(21, 32).trim());
											 * System.out.println(line.substring(33, 42).trim());
											 * System.out.println(line.substring(43, 61).trim());
											 * System.out.println(line.substring(62, 73).trim());
											 * System.out.println(line.substring(74, 83).trim());
											 * System.out.println(line.substring(84, 93).trim());
											 * System.out.println(line.substring(94, 103).trim());
											 * System.out.println(line.substring(104, 124).trim());
											 * System.out.println(line.substring(125, line.length()).trim());
											 */
										}

									}
								}
							}
						} else {
							lineIdentifier = 0;
						}

					}

				}

			}
			System.out.println("completed ");
			output.put("result", true);
			output.put("msg", "File reading Completed.");

		} catch (Exception e) {
			output.put("result", false);
			output.put("msg", "File reading Failed due to Line Number " + lineNumber);
			System.out.println("Exception in read140File " + e);
		}
		return output;
	}

	public HashMap<String, Object> read461File(MultipartFile file, Connection conn, MastercardUploadBean beanObj) {
		HashMap<String, Object> output = new HashMap<String, Object>();
		String line = null, DESCRIPSTION = "", currency = "", settldate = "", DECRIPTION = "";
		boolean DomesticData = false, IssuerData = false, AcquirerData = false, storeData = false,
				reversal_records = false;
		int lineIdentifier = 0, startingLine = 0, reversal_start = 0, exception_start = 0;
		int lineNumber = 0, iss_srl_no = 1, acq_srl_no = 1, line_read1 = 1;
		String pan = null;
		int line_read = 1;
		boolean Exception_audit_records = false;

		String INSERT_DATA = "INSERT INTO MASTERCARD_461_RAWDATA(SRL_NO, DESCRIPSTION,PROC_CODE, NUM_APPROVED, NUM_DENIALS, RECON_AMOUNT, FINANCIAL_COUNT, NON_FINANCIAL_COUNT, NON_BILL_COUNT, TRANS_FEE, TRANS_TYPE, SUBCAT_ISS_ACQ, AMT_PART_TRAN_TYPE, FEE_PART_TRAN_TYPE, CREATEDBY, SETTLEMENT_DATE, FILENAME,FILEDATE) "
				+ "VALUES(?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";

		String INSERT_REVERSAL = "INSERT INTO MASTERCARD_461_REVERSAL2( PAN,ADV_REG_CODE,LOCAL_DATE,SWITCH_SERIAL_NO,INST_ID,NEW_TRACE_NO,PROC_CODE,REF_NO,TRANS_CURR,ORG_AMOUNT,REV_AMOUNT,RESP_CODE,ORG_SERIAL_NO,ORG_TRAS_NO,LOCAL_TIME,PROC_ID,BRAND,CONVERSION_RATE,SETTL_CURR,NEW_AMOUNT,SETTL_AMOUNT,AMT_CR_DR,CREATED_BY,FILETYPE,ISS_ACQ,SETTLEMENT_DATE,FILENAME,FILEDATE) VAlues (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String INSERT_TOTAL = " INSERT INTO MASTERCARD_461_TOTAL(  DESCRIPTION,AUTO_REVERSALS,ADJUSTMENTS,CHARGEBACKS,REVERSE_CHARGEBACKS,REPRESENTMENTS,REPRESENTMENTS_REVERSALS,TOTAL,TXTTYPE,FILEDATE,FILENAME)VALUES(?,?,?,?,?,?,?,?,?,?,?)";

		String INSERT_EXCEPTION = " INSERT INTO MASTERCARD_461_EXCEPTION(  PAN,USER_GROUP,USER_NUM,BRAND,GCMS_DT_TM,CURR_DATE,CURR_TIME,CURRENCY,ADV_REASON,SWITCH_DATE,SWITCH_TIME,ORG_SWITCH_SER,SWITCH_SERIAL,STLL_DATE,PROC_CODE,CONV_RATE,TRACE_NO,ORG_DATE,ORG_TIME,ORG_AMOUNT,REFERENCE_NO,ACQ_REC_DATE,ACQ_INST_NAME,NEW_AMOUNT,RESP_CODE_A,TERMINAL_ID,     ACQ_LOC,REV_DR_AMOUNT,RESP_CODE_I_,ISS_INST_ID,ACQ_INST_ID,REV_CR_AMOUNT,ADV_REASON_A,ISS_PROC_ID,ACQ_PROC_ID,STLL_AMOUNT,TXNTYPE,ADV_REASON_I,ORG_ISS_ID,ORG_ACQ_ID,ISS_ACQ,STLMT_CURRENCY,SETTLMENT_DATE,FILEDATE,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement pst = conn.prepareStatement(INSERT_DATA);
			PreparedStatement rev_pst = conn.prepareStatement(INSERT_REVERSAL);
			PreparedStatement rev_pst1 = conn.prepareStatement(INSERT_EXCEPTION);
			PreparedStatement total1 = conn.prepareStatement(INSERT_TOTAL);
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			String DECRIPTION1 = "", AUTO_REVERSALS = "", ADJUSTMENTS = "", CHARGEBACKS = "", TXTTYPE = "",
					REVERSE_CHARGEBACKS = "", REPRESENTMENTS = "", REPRESENTMENTS_REVERSALS = "", TOTAL = "";

			while ((line = br.readLine()) != null) {
				lineNumber++;
				// System.out.println("ffff "+ line.trim());
				if (line.startsWith("                      AUTO REVERSALS")) {

					if (line.contains("NON-FIN")) {
						TXTTYPE = "REVERSAL";
					} else {
						TXTTYPE = "EXCEPTION";
					}

					line_read1++;
					continue;
				}
				if (line_read1 == 2) {
					// System.out.println("ffffew "+ line.trim());
					DECRIPTION1 = line.substring(0, 21).trim();
					AUTO_REVERSALS = line.substring(30, 37).trim();
					ADJUSTMENTS = line.substring(48, 53).trim();
					CHARGEBACKS = line.substring(65, 69).trim();
					REVERSE_CHARGEBACKS = line.substring(80, 85).trim();
					REPRESENTMENTS = line.substring(98, 104).trim();
					REPRESENTMENTS_REVERSALS = line.substring(115, 121).trim();
					TOTAL = line.substring(131, 133).trim();

					total1.setString(1, DECRIPTION1);
					total1.setString(2, AUTO_REVERSALS);
					total1.setString(3, ADJUSTMENTS);
					total1.setString(4, CHARGEBACKS);
					total1.setString(5, REVERSE_CHARGEBACKS);
					total1.setString(6, REPRESENTMENTS);
					total1.setString(7, REPRESENTMENTS_REVERSALS);
					total1.setString(8, TOTAL);
					total1.setString(9, TXTTYPE);
					total1.setString(10, beanObj.getFileDate());
					total1.setString(11, file.getOriginalFilename());
					total1.execute();

					if (line.contains("TOTAL")) {
						DECRIPTION1 = line.substring(0, 21).trim();
						AUTO_REVERSALS = line.substring(30, 37).trim();
						ADJUSTMENTS = line.substring(48, 53).trim();
						CHARGEBACKS = line.substring(65, 69).trim();
						REVERSE_CHARGEBACKS = line.substring(80, 85).trim();
						REPRESENTMENTS = line.substring(98, 104).trim();
						REPRESENTMENTS_REVERSALS = line.substring(115, 121).trim();
						TOTAL = line.substring(131, 133).trim();

						total1.setString(1, DECRIPTION1);
						total1.setString(2, AUTO_REVERSALS);
						total1.setString(3, ADJUSTMENTS);
						total1.setString(4, CHARGEBACKS);
						total1.setString(5, REVERSE_CHARGEBACKS);
						total1.setString(6, REPRESENTMENTS);
						total1.setString(7, REPRESENTMENTS_REVERSALS);
						total1.setString(8, TOTAL);
						total1.setString(9, TXTTYPE);
						total1.setString(10, beanObj.getFileDate());
						total1.setString(11, file.getOriginalFilename());
						total1.execute();
						line_read1 = 1;

					}

				} else {

					// System.out.println("ffffe "+ line.trim());

				}

				if (line_read1 == 2) {
					System.out.println("ffff3 " + line.trim());
					DECRIPTION1 = line.substring(0, 21).trim();
					AUTO_REVERSALS = line.substring(30, 37).trim();
					ADJUSTMENTS = line.substring(48, 53).trim();
					CHARGEBACKS = line.substring(65, 69).trim();
					REVERSE_CHARGEBACKS = line.substring(80, 85).trim();
					REPRESENTMENTS = line.substring(98, 104).trim();
					REPRESENTMENTS_REVERSALS = line.substring(115, 121).trim();
					TOTAL = line.substring(131, 133).trim();

				} else if (line_read1 == 4 && !line.contains("1SWCHD61")) {
					System.out.println("ffff4 " + line.trim());
					DECRIPTION1 = line.substring(0, 21).trim();
					AUTO_REVERSALS = line.substring(30, 37).trim();
					ADJUSTMENTS = line.substring(48, 53).trim();
					CHARGEBACKS = line.substring(65, 69).trim();
					REVERSE_CHARGEBACKS = line.substring(80, 85).trim();
					REPRESENTMENTS = line.substring(98, 104).trim();
					REPRESENTMENTS_REVERSALS = line.substring(115, 121).trim();
					TOTAL = line.substring(131, 133).trim();

				} else if (line_read1 == 5 && !line.contains("1SWCHD61")) {
					System.out.println("ffff5 " + line.trim());
					DECRIPTION1 = line.substring(0, 21).trim();
					AUTO_REVERSALS = line.substring(30, 37).trim();
					ADJUSTMENTS = line.substring(48, 53).trim();
					CHARGEBACKS = line.substring(65, 69).trim();
					REVERSE_CHARGEBACKS = line.substring(80, 85).trim();
					REPRESENTMENTS = line.substring(98, 104).trim();
					REPRESENTMENTS_REVERSALS = line.substring(115, 121).trim();
					TOTAL = line.substring(131, 133).trim();

				} else if (line_read1 == 6 && !line.contains("1SWCHD61")) {
					DECRIPTION1 = line.substring(0, 21).trim();
					AUTO_REVERSALS = line.substring(30, 37).trim();
					ADJUSTMENTS = line.substring(48, 53).trim();
					CHARGEBACKS = line.substring(65, 69).trim();
					REVERSE_CHARGEBACKS = line.substring(80, 85).trim();
					REPRESENTMENTS = line.substring(98, 104).trim();
					REPRESENTMENTS_REVERSALS = line.substring(115, 121).trim();
					TOTAL = line.substring(131, 133).trim();

				}
//System.out.println("ffff "+DECRIPTION1 + " "+  AUTO_REVERSALS+ " " + TOTAL);
				if (line.contains(
						"0                                                                           REVERSED                       REPRESENTMENT")) {

				}

				if (line.trim().contains("WORK OF")) {

					settldate = line.substring(123, 132).replaceAll("/", "-");

				}
				if (line.trim().contains("CIRRUS")) {

					DESCRIPSTION = "CIRRUS";
				} else if (line.trim().contains("INTERCHANGE")) {

					DESCRIPSTION = "INTERCHANGE";
				} else if (line.trim().contains("VISA")) {

					DESCRIPSTION = "VISA";
				} else if (line.trim().contains("MASTERCARD")) {

					DESCRIPSTION = "MASTERCARD";
				} else if (line.trim().contains("MAESTRO ATM")) {

					DESCRIPSTION = "MAESTRO ATM";
				} else if (line.trim().contains("GRAND TOTALS")) {

					DESCRIPSTION = "GRAND TOTALS";
				} else if (line.trim().startsWith("MC") || line.trim().startsWith("MS") || line.trim().startsWith("VI")
						|| line.trim().startsWith("CI")) {

					DESCRIPSTION = line.trim();
				}

				if (DESCRIPSTION.contains("MASTERCARD") && line.trim().contains("EXCEPTION ITEMS")) {

					DESCRIPSTION = "MASTERCARD";
				} else if (DESCRIPSTION.contains("MAESTRO ATM") && line.trim().contains("EXCEPTION ITEMS")) {

					DESCRIPSTION = "MAESTRO ATM";
				}
				// System.out.println("decription "+DESCRIPSTION);

				String[] values = line.split(":");
				if (line.trim().startsWith("ACQUIRING PROCESSOR")) {
					IssuerData = false;
					// System.out.println("acq "+ line.trim());
					AcquirerData = true;
				} else if (line.trim().startsWith("ISSUING PROCESSOR")) {
					AcquirerData = false;
					IssuerData = true;
				}
				if (values[0].trim().equals("CURRENCY")) {
					// lineIdentifier++;
					if (values[1].trim().equalsIgnoreCase("356   Indian Rupee")) {
						currency = "INR";
						// System.out.println("INR");
						lineIdentifier++;
						DomesticData = true;
						continue;
					} else if (values[1].trim().equalsIgnoreCase("840   U.S. Dollar")) {
						currency = "US";
						// System.out.println("US");
						lineIdentifier++;
						DomesticData = true;
						continue;
					}

				}

				if (line.contains("R E V E R S A L S   R E P O R T")) {
					lineIdentifier = 1;
					reversal_records = true;
					continue;
				}
				if (lineIdentifier == 1 && reversal_records) {
					// System.out.println("LIne is "+line );
					if (line.contains("A C Q U I R I N G   P R O C E S S O R")) {
						IssuerData = false;
						AcquirerData = true;

						lineIdentifier++;
						DomesticData = true;
						continue;
					} else if (line.contains(" I S S U I N G      P R O C E S S O R")) {
						AcquirerData = false;
						IssuerData = true;
						lineIdentifier++;
						DomesticData = true;
						continue;
					}

				}

				if (line.trim().startsWith("E X C E P T I O N   A U D I T   R E P O R T")) {
					lineIdentifier++;
					Exception_audit_records = true;
					continue;
				}

				if (lineIdentifier == 1 && Exception_audit_records) {
					if (line.contains("A C Q U I R I N G   P R O C E S S O R")) {
						IssuerData = false;
						AcquirerData = true;
						lineIdentifier++;
						DomesticData = true;
						continue;
					} else if (line.contains(" I S S U I N G      P R O C E S S O R")) {
						AcquirerData = false;
						IssuerData = true;
						lineIdentifier++;
						DomesticData = true;
						continue;
					}
				}

				if (lineIdentifier == 1) {
					lineIdentifier++;
					continue;
				} else if (lineIdentifier == 2 && DomesticData) {
					if (IssuerData) {
						// System.out.println("IssuerData "+ line.trim());
						if (line.startsWith("0 DESCRIPTION") && line.contains("-----------") && !storeData) {
							storeData = true;
							startingLine++;
							continue;
						} else if (reversal_records && (reversal_start == 0 || reversal_start < 2)) {
							System.out.println("----------ISSUER REVERSAL------------------");
							System.out.println("Reversal records starts " + lineNumber);
							storeData = true;
							reversal_start++;
							continue;
						} else if (Exception_audit_records && (exception_start == 0 || exception_start < 2)) {
							System.out.println("------------- ISSUER EXCEPTION RECORDS ------------");
							storeData = true;
							exception_start++;
							continue;
						}

						if (storeData) {
							if (reversal_records && reversal_start == 2) {
								if (line.equalsIgnoreCase(
										"                                                                                             REPRESENTMENT     PRE-AUTH              ")) {
									reversal_records = false;
									reversal_start = 0;
									IssuerData = false;
									lineIdentifier = 0;
									continue;
								} else {

									// System.out.println(line);
									// code to separate data
									if (!line.trim().equals("")) {

										if (line.startsWith(" PAN")) {
											System.out.println("Pan "
													+ line.substring(11, line.trim().length()).replaceAll("^0*", ""));
											rev_pst.setString(1,
													line.substring(11, line.length()).trim().replaceAll("^0*", ""));
											continue;
										} else if (line.trim().startsWith("ADV REASON")
												|| line.trim().startsWith("RESP CODE")
												|| line.contains("----------------------")
												|| line.trim().startsWith("PROCESSOR"))
											continue;
										else if (line.contains("1SWCHD353")
												|| line.trim().contains("I S S U I N G   P R O C E S S O R")
												|| line.trim().startsWith("R E V E R S A L S   R E P O R T")
												|| line.contains(":"))
											continue;
										else {
											if (line_read == 1) {
												System.out
														.println("ADv code " + line.substring(0, 11).replace(" ", ""));
												System.out.println("LOCAL DATE " + line.substring(51, 57));
												System.out.println("switch serial " + line.substring(13, 30));
												System.out.println("INST ID " + line.substring(63, 74));
												System.out.println("new trace no " + line.substring(30, 45));
												System.out.println("PROC CODE " + line.substring(74, 81));
												System.out.println("refrence no " + line.substring(83, 97));
												System.out.println("trans curr " + line.substring(98, 101));
												System.out.println("org Amount " + line.substring(108, 117));
												System.out.println("rev AMT " + line.substring(123, 131));

												rev_pst.setString(2, line.substring(123, 131).trim());
												rev_pst.setString(3, line.substring(51, 57).trim());
												rev_pst.setString(4, line.substring(13, 30).trim());
												rev_pst.setString(5, line.substring(63, 74).trim());
												rev_pst.setString(6, line.substring(30, 45).trim());
												rev_pst.setString(7, line.substring(74, 81).trim());
												rev_pst.setString(8, line.substring(83, 97).trim());
												rev_pst.setString(9, line.substring(98, 101).trim());
												rev_pst.setString(10, line.substring(108, 117).trim());

												rev_pst.setString(11, line.substring(123, 131).trim());

												line_read++;
											} else if (line_read == 2) {

												System.out.println("resp code " + line.substring(0, 4));
												System.out.println("Auth " + line.substring(5, 13));
												System.out.println("org switch serial " + line.substring(13, 30));

												System.out.println("org trace no " + line.substring(30, 45));
												System.out.println("LOCAL time " + line.substring(51, 58));
												System.out.println("PROC ID" + line.substring(63, 74));
												System.out.println("brand " + line.substring(74, 78));
												System.out.println("conversion rate " + line.substring(83, 93));
												System.out.println("settl curr " + line.substring(98, 101));
												System.out.println("new amt " + line.substring(108, 117));
												System.out.println("settl amt " + line.substring(123, 131));
												System.out.println("Amount sign " + line.substring(131, 132));

												rev_pst.setString(12, line.substring(0, 4).trim());
												rev_pst.setString(13, line.substring(13, 30).trim());
												rev_pst.setString(14, line.substring(30, 45).trim());
												rev_pst.setString(15, line.substring(51, 58).trim());
												rev_pst.setString(16, line.substring(63, 74).trim());
												rev_pst.setString(17, line.substring(74, 78).trim());
												rev_pst.setString(18, line.substring(83, 93).trim());
												rev_pst.setString(19, line.substring(98, 101).trim());
												rev_pst.setString(20, line.substring(108, 117).trim());
												rev_pst.setString(21, line.substring(123, 131).trim());
												rev_pst.setString(22, line.substring(131, 132).trim());

												rev_pst.setString(23, beanObj.getCreatedBy().trim());
												if (line.substring(98, 101).contains("356")) {
													rev_pst.setString(24, "INR");

												} else {
													rev_pst.setString(24, "US");
												}
												rev_pst.setString(25, "ISS");
												rev_pst.setString(26, settldate);
												rev_pst.setString(27, file.getOriginalFilename());
												rev_pst.setString(28, beanObj.getFileDate());

												rev_pst.execute();

											} else if (line_read == 6) {

												System.out.println("resp code6  " + line.trim());
												line_read = 1;

												rev_pst.setString(12, line.substring(0, 4).trim());
												rev_pst.setString(13, line.substring(13, 30).trim());
												rev_pst.setString(14, line.substring(30, 45).trim());
												rev_pst.setString(15, line.substring(51, 58).trim());
												rev_pst.setString(16, line.substring(63, 74).trim());
												rev_pst.setString(17, line.substring(74, 78).trim());
												rev_pst.setString(18, line.substring(83, 93).trim());
												rev_pst.setString(19, line.substring(98, 101).trim());
												rev_pst.setString(20, line.substring(108, 117).trim());
												rev_pst.setString(21, line.substring(123, 131).trim());
												rev_pst.setString(22, line.substring(131, 132).trim());

												rev_pst.setString(23, beanObj.getCreatedBy().trim());
												if (line.substring(98, 101).contains("356")) {
													rev_pst.setString(24, "INR");

												} else {
													rev_pst.setString(24, "US");
												}
												rev_pst.setString(25, "ISS");
												rev_pst.setString(26, settldate);
												rev_pst.setString(27, file.getOriginalFilename());
												rev_pst.setString(28, beanObj.getFileDate());
												rev_pst.execute();

											}
										}
									}
								}

							} else if (Exception_audit_records && exception_start == 2) {

								if (line.contains(":") || line.trim().startsWith("CNTL#")
										|| line.trim().contains("I S S U I N G   P R O C E S S O R")
										|| line.trim().startsWith("E X C E P T I O N   A U D I T   R E P O R T"))
									continue;
								else if (line.contains("1SWCHD53") || line.equalsIgnoreCase(
										"0                                                                           REVERSED                       REPRESENTMENT             ")) {
									Exception_audit_records = false;
									reversal_start = 0;
									lineIdentifier = 0;
									exception_start = 0;
									continue;
								} else {

									System.out.println("Line " + line);
									if (line.startsWith("0PAN")) {
										System.out.println("pan " + line.substring(9, 31).trim().replaceAll("^0*", ""));
										System.out.println("USER GROUP " + line.substring(46, 51).trim());
										System.out.println("USER NUM " + line.substring(74, 80).trim());

										System.out.println("Brand " + line.substring(124, line.length()).trim());

										rev_pst1.setString(1, line.substring(9, 31).trim().replaceAll("^0*", ""));
										rev_pst1.setString(2, line.substring(46, 51).trim());
										rev_pst1.setString(3, line.substring(74, 80).trim());
										rev_pst1.setString(4, line.substring(124, line.length()).trim());
									} else if (line.startsWith("  GCMS DT/TM")) {
										System.out.println("GCMS DT/TM " + line.substring(16, 28).trim());
										System.out.println("CURR_DATE " + line.substring(46, 51).trim());
										System.out.println("CURR_TIME " + line.substring(74, 87).trim());
										System.out.println("CURRENCY " + line.substring(127, 131).trim());

										rev_pst1.setString(5, line.substring(16, 28).trim());
										rev_pst1.setString(6, line.substring(46, 51).trim());
										rev_pst1.setString(7, line.substring(74, 87).trim());
										rev_pst1.setString(8, line.substring(127, 131).trim());

									} else if (line.startsWith("  ADV REASON  ")) {
										System.out.println(
												"switch serial no " + line.substring(16, 28).trim().replace(" ", ""));

										System.out.println("SWITCH_DATE " + line.substring(46, 51).trim());
										System.out.println("SWITCH_TIME " + line.substring(74, 87).trim());
										System.out.println("ORG SWCH SER " + line.substring(122, 131).trim());
										rev_pst1.setString(9, line.substring(16, 28).trim());
										rev_pst1.setString(10, line.substring(46, 51).trim());
										rev_pst1.setString(11, line.substring(74, 87).trim());
										rev_pst1.setString(12, line.substring(127, 131).trim());

									} else if (line.startsWith("  SWCH SERIAL")) {
										System.out
												.println(" TRACE NO" + line.substring(16, 28).trim().replace(" ", ""));

										System.out.println("STLLM_DATE " + line.substring(46, 51).trim());
										System.out.println("PROC_CODE " + line.substring(74, 87).trim());
										System.out.println("CONV RATE " + line.substring(122, 131).trim());
										rev_pst1.setString(13, line.substring(16, 28).trim());
										rev_pst1.setString(14, line.substring(46, 51).trim());
										rev_pst1.setString(15, line.substring(74, 87).trim());
										rev_pst1.setString(16, line.substring(122, 131).trim());

									} else if (line.startsWith("  TRACE NO")) {
										System.out
												.println(" TRACE NO" + line.substring(16, 28).trim().replace(" ", ""));

										System.out.println("ORG DATE " + line.substring(46, 51).trim());
										System.out.println("ORG TIME " + line.substring(74, 87).trim());
										System.out.println("ORG AMT " + line.substring(115, 131).trim());
										rev_pst1.setString(17, line.substring(16, 28).trim());
										rev_pst1.setString(18, line.substring(46, 51).trim());
										rev_pst1.setString(19, line.substring(74, 87).trim());
										rev_pst1.setString(20, line.substring(115, 131).trim());

									} else if (line.startsWith("  REFERENCE NO")) {
										System.out.println(
												"REFERENCE NO" + line.substring(16, 28).trim().replace(" ", ""));

										System.out.println("ACQ REC DATE " + line.substring(46, 51).trim());
										System.out.println("ACQ INST NAME " + line.substring(74, 104).trim());
										System.out.println("NEW AMT " + line.substring(115, 131).trim());
										rev_pst1.setString(21, line.substring(16, 28).trim());
										rev_pst1.setString(22, line.substring(46, 51).trim());
										rev_pst1.setString(23, line.substring(74, 104).trim());
										rev_pst1.setString(24, line.substring(115, 131).trim());

									} else if (line.startsWith("  RESP CODE-A")) {
										System.out.println(
												"RESP CODE-A" + line.substring(14, 24).trim().replace(" ", ""));

										System.out.println("TERMINAL ID " + line.substring(46, 57).trim());
										System.out.println("ACQ LOC " + line.substring(74, 104).trim());
										System.out.println("REV DR AMT " + line.substring(115, 131).trim());
										rev_pst1.setString(25, line.substring(16, 28).trim());
										rev_pst1.setString(26, line.substring(46, 57).trim());
										rev_pst1.setString(27, line.substring(74, 104).trim());
										rev_pst1.setString(28, line.substring(115, 131).trim());

									} else if (line.startsWith("  RESP CODE-I")) {
										System.out.println(
												"RESP CODE-A" + line.substring(14, 24).trim().replace(" ", ""));

										System.out.println("ISS INST ID " + line.substring(46, 57).trim());
										System.out.println("ACQ INST ID " + line.substring(74, 104).trim());
										System.out.println("REV CR AMT " + line.substring(115, 131).trim());
										rev_pst1.setString(29, line.substring(14, 24).trim());
										rev_pst1.setString(30, line.substring(46, 57).trim());
										rev_pst1.setString(31, line.substring(74, 104).trim());
										rev_pst1.setString(32, line.substring(115, 131).trim());

									} else if (line.startsWith("  ADV REASON -A")) {
										System.out.println(
												"ADV REASON -A" + line.substring(14, 24).trim().replace(" ", ""));

										System.out.println("ISS PROC ID " + line.substring(46, 57).trim());
										System.out.println("ACQ PROC ID" + line.substring(74, 104).trim());
										System.out.println("STLMT AMT" + line.substring(115, 131).trim());
										System.out.println("TXTTYEPE" + line.substring(131, 132).trim());
										rev_pst1.setString(33, line.substring(14, 24).trim());
										rev_pst1.setString(34, line.substring(46, 57).trim());
										rev_pst1.setString(35, line.substring(74, 104).trim());
										rev_pst1.setString(36, line.substring(115, 131).trim());
										rev_pst1.setString(37, line.substring(131, 132).trim());
									} else if (line.startsWith("  ADV REASON -I")) {
										System.out.println(
												"ADV REASON -I" + line.substring(14, 24).trim().replace(" ", ""));

										System.out.println("ORG ISS  ID " + line.substring(46, 57).trim());
										System.out.println("ORG ACQ ID" + line.substring(74, 104).trim());
										System.out.println("STLMT CURRENCY" + line.substring(115, 131).trim());
										rev_pst1.setString(38, line.substring(14, 24).trim());
										rev_pst1.setString(39, line.substring(46, 57).trim());
										rev_pst1.setString(40, line.substring(74, 104).trim());
										rev_pst1.setString(41, line.substring(115, 131).trim());
										rev_pst1.setString(42, "ISS");
										rev_pst1.setString(43, settldate);

										rev_pst1.setString(44, beanObj.getFileDate());
										rev_pst1.setString(45, file.getOriginalFilename());
										rev_pst1.execute();
									}
								}
							} else {
								if (startingLine < 4) {
									startingLine++;
									if (startingLine == 4 && line.startsWith("0 GRAND TOTALS")) {
										storeData = false;
									}

									continue;
								}
								// System.out.println("ankit "+ startingLine);
								if (startingLine == 4 || startingLine == 5) {

									if (line.trim().startsWith("1SWCHD363") || line.startsWith("0 GRAND TOTALS")) {
										startingLine = 0;
										lineIdentifier = 0;
										storeData = false;
									} else if (line.contains("---------------------------------------------"))
										continue;
									else {
										// System.out.println("lin2 "+ line.trim());
										if (line.trim().length() > 50) {
											System.out.println(
													"************************ ISSUER DATA ************************");
											/*
											 * (SRL_NO, PROC_CODE, NUM_APPROVED, NUM_DENIALS, RECON_AMOUNT,
											 * FINANCIAL_COUNT, NON_FINANCIAL_COUNT, NON_BILL_COUNT, TRANS_FEE, " +
											 * "TRANS_TYPE, SUBCAT_ISS_ACC, AMT_PART_TRAN_TYPE, FEE_PART_TRAN_TYPE,
											 * CREATEDBY, SETTLEMENT_DATE)
											 */

											pst.setInt(1, iss_srl_no);
											pst.setString(2, DESCRIPSTION);
											pst.setString(3, line.substring(0, 20).trim());
											pst.setString(4, line.substring(21, 32).trim());
											pst.setString(5, line.substring(33, 42).trim());
											pst.setString(6, line.substring(43, 60).trim());
											pst.setString(7, line.substring(64, 73).trim());
											pst.setString(8, line.substring(84, 93).trim());
											pst.setString(9, line.substring(94, 103).trim());
											pst.setString(10, line.substring(104, 124).trim());
											pst.setString(11, currency);
											pst.setString(12, "ISSUER");
											pst.setString(13, line.substring(60, 63).trim());
											pst.setString(14, line.substring(125, line.length()).trim());
											pst.setString(15, beanObj.getCreatedBy());
											pst.setString(16, settldate);
											pst.setString(17, file.getOriginalFilename());
											pst.setString(18, beanObj.getFileDate());

											pst.execute();
											iss_srl_no++;

										}
									}
								}
							}
						} else {
							lineIdentifier = 0;
						}
					} else if (AcquirerData) {

						if (line.startsWith("0 DESCRIPTION") && line.contains("-----------") && !storeData) {
							storeData = true;
							startingLine++;
							continue;
						} else if (reversal_records && (reversal_start == 0 || reversal_start < 2)) {
							System.out.println("----------ACQUIRER REVERSAL------------------");
							System.out.println("Reversal records starts " + lineNumber);
							storeData = true;
							reversal_start++;
							continue;
						} else if (Exception_audit_records && (exception_start == 0 || exception_start < 2)) {
							System.out.println("------------- Acquirer EXCEPTION RECORDS ------------");
							storeData = true;
							exception_start++;
							continue;
						}

						if (storeData) {
							if (reversal_records && reversal_start == 2) {
								if (line.equalsIgnoreCase(
										"                                                                                             REPRESENTMENT     PRE-AUTH              ")) {
									reversal_records = false;
									reversal_start = 0;
									AcquirerData = false;
									lineIdentifier = 0;
									continue;
								} else {

									// System.out.println("Line2 "+line);
									if (!line.trim().equals("")) {

										if (line.startsWith(" PAN")) {
											System.out.println("Pan "
													+ line.substring(11, line.trim().length()).replaceAll("^0*", ""));
											rev_pst.setString(1,
													line.substring(11, line.length()).trim().replaceAll("^0*", ""));
											continue;
										} else if (line.trim().startsWith("ADV REASON")
												|| line.trim().startsWith("RESP CODE")
												|| line.contains("----------------------"))
											continue;
										else if (line.contains("1SWCHD353")
												|| line.trim().contains("A C Q U I R I N G   P R O C E S S O R")
												|| line.trim().startsWith("R E V E R S A L S   R E P O R T")
												|| line.contains(":"))
											continue;
										else {
											if (line_read == 1) {
												/*
												 * System.out .println("ADv code " + line.substring(0, 11).replace(" ",
												 * "")); System.out.println("LOCAL DATE " + line.substring(51, 57));
												 * System.out.println("switch serial " + line.substring(13, 30));
												 * System.out.println("INST ID " + line.substring(63, 74));
												 * System.out.println("new trace no " + line.substring(30, 45));
												 * System.out.println("PROC CODE " + line.substring(74, 81));
												 * System.out.println("refrence no " + line.substring(83, 97));
												 * System.out.println("trans curr " + line.substring(98, 101));
												 * System.out.println("org Amount " + line.substring(108, 117));
												 * System.out.println("rev AMT " + line.substring(123, 131));
												 * 
												 */ rev_pst.setString(2, line.substring(0, 11).replace(" ", "").trim());
												rev_pst.setString(3, line.substring(51, 57).trim());
												rev_pst.setString(4, line.substring(13, 30).trim());
												rev_pst.setString(5, line.substring(63, 74).trim());
												rev_pst.setString(6, line.substring(30, 45).trim());
												rev_pst.setString(7, line.substring(74, 81).trim());
												rev_pst.setString(8, line.substring(83, 97).trim());
												rev_pst.setString(9, line.substring(98, 101).trim());
												rev_pst.setString(10, line.substring(108, 117).trim());

												rev_pst.setString(11, line.substring(123, 131).trim());

												line_read++;
											} else if (line_read == 2) {

												/*
												 * System.out.println("resp code " + line.substring(0, 4));
												 * System.out.println("Auth " + line.substring(5, 13));
												 * System.out.println("org switch serial " + line.substring(13, 30));
												 * 
												 * System.out.println("org trace no " + line.substring(30, 45));
												 * System.out.println("LOCAL time " + line.substring(51, 58));
												 * System.out.println("PROC ID" + line.substring(63, 74));
												 * System.out.println("brand " + line.substring(74, 78));
												 * System.out.println("conversion rate " + line.substring(83, 93));
												 * System.out.println("settl curr " + line.substring(98, 101));
												 * System.out.println("new amt " + line.substring(108, 117));
												 * System.out.println("settl amt " + line.substring(123, 131));
												 * System.out.println("Amount sign " + line.substring(131, 132));
												 */ line_read = 1;

												rev_pst.setString(12, line.substring(0, 4).trim());
												rev_pst.setString(13, line.substring(13, 30).trim());
												rev_pst.setString(14, line.substring(30, 45).trim());
												rev_pst.setString(15, line.substring(51, 58).trim());
												rev_pst.setString(16, line.substring(63, 74).trim());
												rev_pst.setString(17, line.substring(74, 78).trim());
												rev_pst.setString(18, line.substring(83, 93).trim());
												rev_pst.setString(19, line.substring(98, 101).trim());
												rev_pst.setString(20, line.substring(108, 117).trim());
												rev_pst.setString(21, line.substring(123, 131).trim());
												rev_pst.setString(22, line.substring(131, 132).trim());

												rev_pst.setString(23, beanObj.getCreatedBy().trim());
												if (line.substring(98, 101).contains("356")) {
													rev_pst.setString(24, "INR");

												} else {
													rev_pst.setString(24, "US");
												}
												rev_pst.setString(25, "ACQ");
												rev_pst.setString(26, settldate);
												rev_pst.setString(27, file.getOriginalFilename());
												rev_pst.setString(28, beanObj.getFileDate());
												rev_pst.execute();

											}
										}
									}
								}

							} else if (Exception_audit_records && exception_start == 2) {

								if (line.contains(":") || line.trim().startsWith("CNTL#")
										|| line.trim().contains("A C Q U I R I N G   P R O C E S S O R")
										|| line.trim().startsWith("E X C E P T I O N   A U D I T   R E P O R T"))
									continue;
								else if (line.contains("1SWCHD53") || line.equalsIgnoreCase(
										"0                                                                           REVERSED                       REPRESENTMENT             ")) {
									Exception_audit_records = false;
									reversal_start = 0;
									lineIdentifier = 0;
									exception_start = 0;
									continue;
								} else {

									System.out.println("Line " + line);
									if (line.startsWith("0PAN")) {
										System.out.println("pan " + line.substring(9, 31).trim().replaceAll("^0*", ""));
										System.out.println("USER GROUP " + line.substring(46, 51).trim());
										System.out.println("USER NUM " + line.substring(74, 80).trim());

										System.out.println("Brand " + line.substring(124, line.length()).trim());

										rev_pst1.setString(1, line.substring(9, 31).trim().replaceAll("^0*", ""));
										rev_pst1.setString(2, line.substring(46, 51).trim());
										rev_pst1.setString(3, line.substring(74, 80).trim());
										rev_pst1.setString(4, line.substring(124, line.length()).trim());
									} else if (line.startsWith("  GCMS DT/TM")) {
										System.out.println("GCMS DT/TM " + line.substring(16, 28).trim());
										System.out.println("CURR_DATE " + line.substring(46, 51).trim());
										System.out.println("CURR_TIME " + line.substring(74, 87).trim());
										System.out.println("CURRENCY " + line.substring(127, 131).trim());

										rev_pst1.setString(5, line.substring(16, 28).trim());
										rev_pst1.setString(6, line.substring(46, 51).trim());
										rev_pst1.setString(7, line.substring(74, 87).trim());
										rev_pst1.setString(8, line.substring(127, 131).trim());

									} else if (line.startsWith("  ADV REASON  ")) {
										System.out.println(
												"switch serial no " + line.substring(16, 28).trim().replace(" ", ""));

										System.out.println("SWITCH_DATE " + line.substring(46, 51).trim());
										System.out.println("SWITCH_TIME " + line.substring(74, 87).trim());
										System.out.println("ORG SWCH SER " + line.substring(122, 131).trim());
										rev_pst1.setString(9, line.substring(16, 28).trim());
										rev_pst1.setString(10, line.substring(46, 51).trim());
										rev_pst1.setString(11, line.substring(74, 87).trim());
										rev_pst1.setString(12, line.substring(127, 131).trim());

									} else if (line.startsWith("  SWCH SERIAL")) {
										System.out
												.println(" TRACE NO" + line.substring(16, 28).trim().replace(" ", ""));

										System.out.println("STLLM_DATE " + line.substring(46, 51).trim());
										System.out.println("PROC_CODE " + line.substring(74, 87).trim());
										System.out.println("CONV RATE " + line.substring(122, 131).trim());
										rev_pst1.setString(13, line.substring(16, 28).trim());
										rev_pst1.setString(14, line.substring(46, 51).trim());
										rev_pst1.setString(15, line.substring(74, 87).trim());
										rev_pst1.setString(16, line.substring(122, 131).trim());

									} else if (line.startsWith("  TRACE NO")) {
										System.out
												.println(" TRACE NO" + line.substring(16, 28).trim().replace(" ", ""));

										System.out.println("ORG DATE " + line.substring(46, 51).trim());
										System.out.println("ORG TIME " + line.substring(74, 87).trim());
										System.out.println("ORG AMT " + line.substring(115, 131).trim());
										rev_pst1.setString(17, line.substring(16, 28).trim());
										rev_pst1.setString(18, line.substring(46, 51).trim());
										rev_pst1.setString(19, line.substring(74, 87).trim());
										rev_pst1.setString(20, line.substring(115, 131).trim());

									} else if (line.startsWith("  REFERENCE NO")) {
										System.out.println(
												"REFERENCE NO" + line.substring(16, 28).trim().replace(" ", ""));

										System.out.println("ACQ REC DATE " + line.substring(46, 51).trim());
										System.out.println("ACQ INST NAME " + line.substring(74, 104).trim());
										System.out.println("NEW AMT " + line.substring(115, 131).trim());
										rev_pst1.setString(21, line.substring(16, 28).trim());
										rev_pst1.setString(22, line.substring(46, 51).trim());
										rev_pst1.setString(23, line.substring(74, 104).trim());
										rev_pst1.setString(24, line.substring(115, 131).trim());

									} else if (line.startsWith("  RESP CODE-A")) {
										System.out.println(
												"RESP CODE-A" + line.substring(14, 24).trim().replace(" ", ""));

										System.out.println("TERMINAL ID " + line.substring(46, 57).trim());
										System.out.println("ACQ LOC " + line.substring(74, 104).trim());
										System.out.println("REV DR AMT " + line.substring(115, 131).trim());
										rev_pst1.setString(25, line.substring(16, 28).trim());
										rev_pst1.setString(26, line.substring(46, 57).trim());
										rev_pst1.setString(27, line.substring(74, 104).trim());
										rev_pst1.setString(28, line.substring(115, 131).trim());

									} else if (line.startsWith("  RESP CODE-I")) {
										System.out.println(
												"RESP CODE-A" + line.substring(14, 24).trim().replace(" ", ""));

										System.out.println("ISS INST ID " + line.substring(46, 57).trim());
										System.out.println("ACQ INST ID " + line.substring(74, 104).trim());
										System.out.println("REV CR AMT " + line.substring(115, 131).trim());
										rev_pst1.setString(29, line.substring(14, 24).trim());
										rev_pst1.setString(30, line.substring(46, 57).trim());
										rev_pst1.setString(31, line.substring(74, 104).trim());
										rev_pst1.setString(32, line.substring(115, 131).trim());

									} else if (line.startsWith("  ADV REASON -A")) {
										System.out.println(
												"ADV REASON -A" + line.substring(14, 24).trim().replace(" ", ""));

										System.out.println("ISS PROC ID " + line.substring(46, 57).trim());
										System.out.println("ACQ PROC ID" + line.substring(74, 104).trim());
										System.out.println("STLMT AMT" + line.substring(115, 131).trim());
										System.out.println("TXTTYEPE" + line.substring(131, 132).trim());
										rev_pst1.setString(33, line.substring(14, 24).trim());
										rev_pst1.setString(34, line.substring(46, 57).trim());
										rev_pst1.setString(35, line.substring(74, 104).trim());
										rev_pst1.setString(36, line.substring(115, 131).trim());
										rev_pst1.setString(37, line.substring(131, 132).trim());
									} else if (line.startsWith("  ADV REASON -I")) {
										System.out.println(
												"ADV REASON -I" + line.substring(14, 24).trim().replace(" ", ""));

										System.out.println("ORG ISS  ID " + line.substring(46, 57).trim());
										System.out.println("ORG ACQ ID" + line.substring(74, 104).trim());
										System.out.println("STLMT CURRENCY" + line.substring(115, 131).trim());
										rev_pst1.setString(38, line.substring(14, 24).trim());
										rev_pst1.setString(39, line.substring(46, 57).trim());
										rev_pst1.setString(40, line.substring(74, 104).trim());
										rev_pst1.setString(41, line.substring(115, 131).trim());
										rev_pst1.setString(42, "ACQ");
										rev_pst1.setString(43, settldate);

										rev_pst1.setString(44, beanObj.getFileDate());
										rev_pst1.setString(45, file.getOriginalFilename());
										rev_pst1.execute();
									}
								}
							} else {
								if (startingLine < 4) {
									startingLine++;
									if (startingLine == 4 && line.startsWith("0 GRAND TOTALS")) {
										storeData = false;
									}

									continue;
								}

								if (startingLine == 4 || startingLine == 5) {
									if (line.trim().startsWith("1SWCHD363") || line.startsWith("0 GRAND TOTALS")) {
										startingLine = 0;
										lineIdentifier = 0;
										storeData = false;
									} else if (line.contains("---------------------------------------------"))
										continue;
									else {

										if (line.trim().length() > 50) {

											// System.out.println("line "+ line.trim());
											pst.setInt(1, acq_srl_no);
											pst.setString(2, DESCRIPSTION);
											pst.setString(3, line.substring(0, 20).trim());
											pst.setString(4, line.substring(21, 32).trim());
											pst.setString(5, line.substring(33, 42).trim());
											pst.setString(6, line.substring(43, 60).trim());
											pst.setString(7, line.substring(64, 73).trim());
											pst.setString(8, line.substring(84, 93).trim());
											pst.setString(9, line.substring(94, 103).trim());
											pst.setString(10, line.substring(104, 124).trim());
											pst.setString(11, currency);
											pst.setString(12, "ACQUIRER");
											pst.setString(13, line.substring(60, 63).trim());
											pst.setString(14, line.substring(125, line.length()).trim());
											pst.setString(15, beanObj.getCreatedBy());
											pst.setString(16, settldate);
											pst.setString(17, file.getOriginalFilename());
											pst.setString(18, beanObj.getFileDate());
											pst.execute();
											acq_srl_no++;
											/*
											 * System.out.
											 * println("************************ ACQUIRER DATA ************************"
											 * ); System.out.println(line); System.out.println(line.substring(0,
											 * 20).trim()); System.out.println(line.substring(21, 32).trim());
											 * System.out.println(line.substring(33, 42).trim());
											 * System.out.println(line.substring(43, 61).trim());
											 * System.out.println(line.substring(62, 73).trim());
											 * System.out.println(line.substring(74, 83).trim());
											 * System.out.println(line.substring(84, 93).trim());
											 * System.out.println(line.substring(94, 103).trim());
											 * System.out.println(line.substring(104, 124).trim());
											 * System.out.println(line.substring(125, line.length()).trim());
											 */
										}

									}
								}
							}
						} else {
							lineIdentifier = 0;
						}

					}

				}

			}

			output.put("result", true);
			output.put("msg", "FILE UPLOADED SUCCESSFULLY!");

		} catch (Exception e) {
			output.put("result", false);
			output.put("msg", "FILE NOT UPLOADED! " + lineNumber);
			System.out.println("Exception in read140File " + e);
		}
		return output;
	}

	public HashMap<String, Object> readINVOICEFile(MultipartFile file, Connection conn, MastercardUploadBean beanObj) {

		System.out.println("Inside the rupayAdjustmentFileUpload");
		HashMap<String, Object> output = new HashMap<String, Object>();
		int totalCount = 0, rowCount = 0;
		String res = "";
		String row = "";
		String line = "";
		int sr_no = 0;
		List<String> columnName = new ArrayList<>();
		List<List<String>> Data = new ArrayList<List<String>>();
		try {
			BufferedReader csvReader1 = new BufferedReader(new InputStreamReader(file.getInputStream()));

			String tableName = "";

			String INSERT_QUERY = "INSERT INTO  MASTERCARD_INVOICE_RAWTADA   (DOCUMENT_TYPE,INVOICE_NUMBER,CURRENCY,BILLING_CYCLE_DATE,INVOICE_ICA,ACTIVITY_ICA,BILLABLE_ICA,COLLECTION_METHOD,SERVICE_CODE,SERVICE_CODE_DESCRIPTION,PERIOD_START_DATE,PERIOD_END_DATE,ORIGINAL_INVOICE_NUMBER,EVENT_ID,EVENT_DESCRIPTION,AFFILLIATE,UOM,QUANTITY_AMOUNT,RATE,CHARGE,TAX_CHARGE,TOTTAL_CHARGE,VAT_CHARGE,VAT_CURRENCY,VAT_CODE,VAT_RATE,SBF_EXPLANATORY_TEXT,FILEDATE,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String INSERT_QUERY2 = "VALUES (TO_DATE(?,'DD-MM-YY')";
			Workbook wb = new XSSFWorkbook(file.getInputStream());
			Sheet sheet = wb.getSheetAt(0);
			sr_no = 0;
			FormulaEvaluator formulaEvaluate = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
			PreparedStatement ps = conn.prepareStatement(INSERT_QUERY);
			for (org.apache.poi.ss.usermodel.Row r : sheet) {
				int cellCount = 1;
				rowCount++;
				List<String> fileData = new ArrayList<>();

				for (org.apache.poi.ss.usermodel.Cell c : r) {
					// System.out.println("cell count in string issdf " + cellCount + " value " +
					// c.toString());
					switch (formulaEvaluate.evaluateInCell((org.apache.poi.ss.usermodel.Cell) c).getCellType()) {

					case Cell.CELL_TYPE_STRING:
						if (rowCount == 1) {
							columnName.add(c.getStringCellValue());
						} else {
							// ps.setString(cellCount, c.getStringCellValue());

							fileData.add(c.getStringCellValue());
						}
						System.out.println("cell count in string is " + cellCount + " value " + c.getStringCellValue());
						cellCount++;
						break;
					case Cell.CELL_TYPE_NUMERIC:
						String digit = c.getNumericCellValue() + "";
						if (digit.contains("E")) {
							digit = c.getNumericCellValue() + "";
							double d = Double.parseDouble(digit);
							BigDecimal bd = new BigDecimal(d);
							/*
							 * digit = bd.round(new MathContext(15)).toPlainString();
							 * System.out.println(digit);
							 */
							String tryDigit = bd + "";
							int indexOfDot = tryDigit.indexOf(".");
							int secondDigit = Integer.parseInt(tryDigit.substring(indexOfDot + 1, indexOfDot + 2));
							if (secondDigit > 5) {
								digit = tryDigit.substring(0, indexOfDot + 3);
							} else {
								BigDecimal db = bd.setScale(1, RoundingMode.HALF_UP);
								digit = db.toPlainString();
							}

						}
						if (rowCount > 1) {
							if (digit.contains(".0")) {
								// ps.setString(cellCount, digit.replace(".0", ""));

								fileData.add(digit.replace(".0", ""));
							} else {
								// ps.setString(cellCount, digit);

								fileData.add(digit);
							}
						}

						// System.out.println("Cell count is " + cellCount + " value " +
						// digit.replace(".0", ""));

						cellCount++;
						break;
					default:
						// System.out.println("Here it is " + cellCount + " value is " +
						// c.getStringCellValue());
						cellCount++;
						break;

					}
				}

				sr_no = 0;
				if (rowCount > 1)
					Data.add(fileData);
			}

			// System.out.println("column name is " + columnName);

			/*
			 * for(String col : columnName) { INSERT_QUERY = INSERT_QUERY
			 * +", "+col.replace(" ", "_"); INSERT_QUERY2 = INSERT_QUERY2+" , ?"; }
			 * INSERT_QUERY = INSERT_QUERY+" ) "; INSERT_QUERY2 = INSERT_QUERY2 + " ) ";
			 */

			System.out.println("Data " + Data.toString());
			for (List<String> fileDatas : Data) {
				sr_no = 1;
				for (String col : fileDatas) {
					System.out.println(sr_no);
					System.out.println("col " + col);
					ps.setString(sr_no++, col);

					ps.setString(28, beanObj.getFileDate());
					ps.setString(29, file.getOriginalFilename());
					// System.out.println("sr_no s " + sr_no);

				}
				ps.execute();
				System.out.println("Inserted");
			}

			System.out.println("Completed inserting ");
			output.put("result", true);
			output.put("msg", "FILE UPLOADED SUCCESSFULLY!");

		} catch (Exception e) {
			output.put("result", false);
			output.put("msg", "FILE NOT UPLOADED! ");
			System.out.println("Exception in read140File " + e);
		}
		return output;

	}
	public HashMap<String, Object> readINVOICETXTFile(MultipartFile file, Connection conn, MastercardUploadBean beanObj) {

		System.out.println("Inside the rupayAdjustmentFileUpload");
		HashMap<String, Object> output = new HashMap<String, Object>();
		int totalCount = 0, rowCount = 0;
		String res = "";
		String row = "";
		String line = "";
		int sr_no = 0;
		List<String> columnName = new ArrayList<>();
		List<List<String>> Data = new ArrayList<List<String>>();
		try {
		
			String tableName = "";

			String INSERT_QUERY = "INSERT INTO  mastercard_invoicetxt_rawdata   (Billable_ICA,Service_Code,Total_for_invoice,Total_for_invoice_exchange_rate,FILEDATE,FILENAME,exchange_rate) VALUES(?,?,?,?,?,?,?)";
	
			
			String thisline="", Billable_ICA="", Service_Code="", total_invoice_for="", total_invoice_for_EXCHANGE="", exchange_rate="";
			PreparedStatement ps = conn.prepareStatement(INSERT_QUERY);
				BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			while ((thisline = br.readLine()) != null) {
				
			
			if(thisline.contains("Billable ICA:")) {
				Billable_ICA= thisline.substring(74, thisline.length());
				System.out.println("Billable_ICA "+ Billable_ICA);
			}else if(thisline.contains("Service Code")) {
				Service_Code= thisline.substring(14, 19);
				System.out.println("Service Code "+ Service_Code);
			}else if(thisline.contains(" Total for Invoice: ")) {
				total_invoice_for= thisline.substring(80, 107);
				System.out.println("total_invoice_for "+ total_invoice_for);
			}else if(thisline.contains("Total for Invoice in INR at exchange rate of")) {
				total_invoice_for_EXCHANGE= thisline.substring(80, 107);
				exchange_rate=thisline.substring(46, 53);
				System.out.println("Total for Invoice in INR at exchange rate of"+ total_invoice_for_EXCHANGE+ " "+ exchange_rate);
			

				ps.setString(1, Billable_ICA);
				ps.setString(2, Service_Code);
				ps.setString(3, total_invoice_for);
				ps.setString(4, total_invoice_for_EXCHANGE);
	
				ps.setString(5, beanObj.getFileDate());
				ps.setString(6, file.getOriginalFilename());
				ps.setString(7, exchange_rate);
				ps.execute();}
				
			
		
				
			}
			System.out.println("Success");
	

			System.out.println("Completed inserting ");
			output.put("result", true);
			output.put("msg", "FILE UPLOADED SUCCESSFULLY!");

		} catch (Exception e) {
			output.put("result", false);
			output.put("msg", "FILE NOT UPLOADED! ");
			System.out.println("Exception in read140File " + e);
		}
		return output;

	}

	public HashMap<String, Object> readINVOICEFilePDF(MultipartFile file, Connection conn,
			MastercardUploadBean beanObj) {

		System.out.println("Inside the rupayAdjustmentFileUpload");
		HashMap<String, Object> output = new HashMap<String, Object>();
		int totalCount = 0, rowCount = 0;
		String res = "", totalamount = "", TotalCharge = "", charge = "", rate = "";
		String row = "";
		String line = "";
		boolean ExchangeFlage = false;
		int sr_no = 0, count = 1;
		List<String> columnName = new ArrayList<>();
		List<List<String>> Data = new ArrayList<List<String>>();
		try {

			String INSERT_QUERY = "INSERT INTO    mastercard_invoicepdf_rawtada  (RATE,CHARGE,TOTALCHARGE,FILEDATE,FILENAME) VALUES(?,?,?,?,?)";

			BufferedReader csvReader1 = new BufferedReader(new InputStreamReader(file.getInputStream()));
			PreparedStatement ps = conn.prepareStatement(INSERT_QUERY);
			try (PDDocument document = PDDocument.load(file.getInputStream())) {
				PDFTextStripper pdfTextStripper = new PDFTextStripper();

				String text = pdfTextStripper.getText(document);
				String billing_Date = "";

				String[] lines = text.split(System.lineSeparator());
				for (String textline : lines) {

					System.out.println("textwww  " + textline);
					if (textline.contains("Billing Date: ")) {

						billing_Date = textline.substring(13, 24);
						System.out.println("billing_Date " + billing_Date);
						continue;

					}
					if (textline.contains("Exchange Rate Local Currency")) {

						System.out.println("Rate " + textline.substring(16, 28));
						rate = textline.substring(16, 28);
						ExchangeFlage = true;
						continue;
					}
					if (ExchangeFlage) {
						System.out.println("ExchangeFlage " + textline.substring(16, 28));
						rate=  textline.substring(16, 28);
						ExchangeFlage = false;

						continue;
					}
					if (textline.startsWith("Total Billing Activity")) {

						TotalCharge = textline.substring(22, 32);
						System.out.println("Total " + TotalCharge);

						ps.setString(1, rate);
						ps.setString(2, charge);
						ps.setString(3, TotalCharge);
						ps.setString(4, beanObj.getFileDate());
						ps.setString(5, file.getOriginalFilename());
						// System.out.println(" System.out.println(\"textwww \"+ textline); " +
						// totalamount);
						ps.execute();
						continue;

					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("Completed inserting ");
			output.put("result", true);
			output.put("msg", "FILE UPLOADED SUCCESSFULLY!");

		} catch (Exception e) {
			output.put("result", false);
			output.put("msg", "FILE NOT UPLOADED! ");
			System.out.println("Exception in read140File " + e);
		}
		return output;

	}
}
