package com.recon.util;


import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;
import com.recon.util.ReadRupay88File;
import com.recon.util.RupayHeaderUtil;
import com.recon.util.RupayUtilBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

public class ReadRupay {
	private static final Logger logger = Logger.getLogger(com.recon.util.ReadRupay.class);

	public boolean readData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
		try {
			logger.info("***** ReadRupay.readData Start ****");
			boolean uploaded = false;
			logger.info(String.valueOf(setupBean.getStSubCategoryid()) + setupBean.getStSubCategory());
			logger.info("Entered CBS File is " + file.getOriginalFilename() + "  " + setupBean.getCategory());
			if (setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")) {
				if (file.getOriginalFilename().contains("xml")) {
					logger.info("Entered CBS File is DOMESTIC");
					uploaded = uploadDomesticData(setupBean, con, file, sourceBean);
				} else if (file.getOriginalFilename().startsWith("86")
						&& setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")
						&& setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
					uploaded = uploadKerelaRupayDomesticDataDAT(setupBean, con, file, sourceBean);
				} else if (file.getOriginalFilename().startsWith("88")
						&& setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")
						&& setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
					uploaded = uploadKerelaRupayDomesticDataDAT(setupBean, con, file, sourceBean);
					ReadRupay88File readRupay = new ReadRupay88File();
					uploaded = readRupay.readData(setupBean, con, file, sourceBean);
				} else if (file.getOriginalFilename().startsWith("86")
						&& setupBean.getCategory().equalsIgnoreCase("QSPARC")
						&& setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")) {
					uploaded = uploadKerelaQSPARCDomesticDataDAT(setupBean, con, file, sourceBean);
				} else {
					uploaded = uploadDomesticData(setupBean, con, file, sourceBean);
				}
			} else if (setupBean.getStSubCategoryid().equalsIgnoreCase("INTERNATIONAL")
					&& setupBean.getCategory().equalsIgnoreCase("QSPARC")) {
				logger.info("Entered CBS File is INTERNATIONAL");
				if (file.getOriginalFilename().startsWith("01") || file.getOriginalFilename().startsWith("05")
						|| setupBean.getP_FILE_NAME().startsWith("03") || setupBean.getP_FILE_NAME().startsWith("02")) {
					uploaded = uploadDomesticData(setupBean, con, file, sourceBean);
				} else {
					uploaded = uploadKerelaQSPARCINTDataDAT(setupBean, con, file, sourceBean);
				}
			} else if (setupBean.getStSubCategoryid().equalsIgnoreCase("INTERNATIONAL")
					&& setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
				logger.info("Entered CBS File is INTERNATIONAL");
				if (file.getOriginalFilename().startsWith("01") || file.getOriginalFilename().startsWith("05")
						|| setupBean.getP_FILE_NAME().startsWith("03") || setupBean.getP_FILE_NAME().startsWith("02")) {
					uploaded = uploadDomesticData(setupBean, con, file, sourceBean);
				} else {
					uploaded = uploadInternationalData(setupBean, con, file, sourceBean);
				}
			} else {
				logger.info("Entered File is Wrong");
				return false;
			}
			logger.info("***** ReadRupay.readData End ****");
			return uploaded;
		} catch (Exception e) {
			logger.error(" error in ReadRupay.readData", new Exception("ReadRupay.readData", e));
			return false;
		}
	}

	public boolean uploadDomesticData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) throws Exception {
		logger.info("***** ReadRupay.uploadDomesticData Start ****");
		String thisLine = null;
		int lineNumber = 1;
		con.setAutoCommit(false);
		String tablename = "";
		if (setupBean.getStSubCategoryid().equalsIgnoreCase("INTERNATIONAL")
				&& setupBean.getCategory().equalsIgnoreCase("RUPAY")) {
			tablename = "rupay_rupay_int_rawdata";
		} else if (setupBean.getCategory().equalsIgnoreCase("QSPARC")
				&& setupBean.getStSubCategoryid().equalsIgnoreCase("INTERNATIONAL")) {
			tablename = "rupay_qsparc_int_rawdata";
		} else if (setupBean.getCategory().equalsIgnoreCase("QSPARC")
				&& setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")) {
			tablename = "rupay_qsparc_rawdata";
		} else {
			tablename = "rupay_rupay_rawdata";
		}
		String insert = "INSERT  INTO " + tablename
				+ " (MTI,Function_Code ,Record_Number,Member_Institution_ID_Code,Unique_File_Name,Date_Settlement,Product_Code,Settlement_BIN,File_Category,Version_Number,"
				+ "Entire_File_Reject_Indicator,File_Reject_Reason_Code,Transactions_Count,Run_Total_Amount,"
				+ "Acquirer_Institution_ID_code,Amount_Settlement,Amount_Transaction,Approval_Code,Acquirer_Reference_Data,Case_Number,Currency_Code_Settlement,Currency_Code_Transaction,Conversion_Rate_Settlement,"
				+ "Card_Acceptor_Addi_Addr,Card_Acceptor_Terminal_ID,Card_Acceptor_Zip_Code,DateandTime_Local_Transaction,TXNFunction_Code ,Late_Presentment_Indicator,TXNMTI,Primary_Account_Number,TXNRecord_Number,"
				+ "RGCS_Received_date,Settlement_DR_CR_Indicator,Txn_Desti_Insti_ID_code,Txn_Origin_Insti_ID_code,Card_Holder_UID,Amount_Billing,Currency_Code_Billing,Conversion_Rate_billing,Message_Reason_Code,"
				+ "Fee_DR_CR_Indicator1,Fee_amount1,Fee_Currency1,Fee_Type_Code1,Interchange_Category1,Fee_DR_CR_Indicator2,Fee_amount2,Fee_Currency2,Fee_Type_Code2,Interchange_Category2,Fee_DR_CR_Indicator3,Fee_amount3,"
				+ "Fee_Currency3,Fee_Type_Code3,Interchange_Category3,Fee_DR_CR_Indicator4,Fee_amount4,Fee_Currency4,Fee_Type_Code4,Interchange_Category4,Fee_DR_CR_Indicator5,Fee_amount5,"
				+ "Fee_Currency5,Fee_Type_Code5,Interchange_Category5,Trl_FUNCTION_CODE,Trl_RECORD_NUMBER,flag,FILEDATE,PAN,CREATEDDATE,RRN , FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,str_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?)";
		String trl_nFunCd = null, trl_nRecNum = null, transactions_count = null;
		logger.info("query " + insert);
		int feesize = 1;
		try {
			PreparedStatement ps = con.prepareStatement(insert);
			Pattern TAG_REGEX = Pattern.compile(">(.+?)</");
			Pattern node_REGEX = Pattern.compile("<(.+?)>");
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			int count = 1;
			String hdr = "", trl = "";
			RupayUtilBean utilBean = new RupayUtilBean();
			RupayHeaderUtil headerUtil = new RupayHeaderUtil();
			logger.info("Process started" + (new Date()).getTime());
			while ((thisLine = br.readLine()) != null) {
				lineNumber++;
				Matcher nodeMatcher = node_REGEX.matcher(thisLine);
				nodeMatcher.find();
				if (!nodeMatcher.group(1).equalsIgnoreCase("Txn")) {
					if (nodeMatcher.group(1).equalsIgnoreCase("Hdr")) {
						hdr = "hdr";
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("/Hdr")) {
						hdr = "";
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nDtTmFlGen")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						headerUtil.setnDtTmFlGen(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nMemInstCd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						headerUtil.setnMemInstCd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nUnFlNm")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						headerUtil.setnUnFlNm(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nProdCd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						headerUtil.setnProdCd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nSetBIN")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						headerUtil.setnSetBIN(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nFlCatg")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						headerUtil.setnFlCatg(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nVerNum")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						headerUtil.setnVerNum(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nAcqInstCd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnAcqInstCd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nAmtSet")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						double amtSet = Integer.parseInt(matcher.group(1));
						amtSet /= 100.0D;
						utilBean.setnAmtSet(String.valueOf(amtSet));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nAmtTxn")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						double amtTxn = Double.parseDouble(matcher.group(1));
						amtTxn /= 100.0D;
						utilBean.setnAmtTxn(String.valueOf(amtTxn));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nApprvlCd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnApprvlCd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nARD")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnARD(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdSet")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnCcyCdSet(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdTxn")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnCcyCdTxn(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nConvRtSet")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnConvRtSet(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpAddAdrs")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnCrdAcpAddAdrs(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcptTrmId")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnCrdAcptTrmId(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpZipCd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnCrdAcpZipCd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nDtSet")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						if (hdr.equalsIgnoreCase("hdr")) {
							headerUtil.setnDtSet(matcher.group(1));
							continue;
						}
						utilBean.setnDtSet(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nDtTmLcTxn")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnDtTmLcTxn(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nFunCd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						if (hdr.equalsIgnoreCase("hdr")) {
							headerUtil.setnFunCd(matcher.group(1));
							continue;
						}
						if (hdr.equalsIgnoreCase("Trl")) {
							trl_nFunCd = matcher.group(1);
							logger.info(trl_nFunCd);
							continue;
						}
						utilBean.setnFunCd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nLtPrsntInd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnLtPrsntInd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nMTI")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						if (hdr.equalsIgnoreCase("hdr")) {
							headerUtil.setnMTI(matcher.group(1));
							continue;
						}
						utilBean.setnMTI(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nPAN")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnPAN(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nRecNum")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						if (hdr.equalsIgnoreCase("hdr")) {
							headerUtil.setnRecNum(matcher.group(1));
							continue;
						}
						if (hdr.equalsIgnoreCase("Trl")) {
							headerUtil.setTrl_nRecNum(matcher.group(1));
							trl_nRecNum = matcher.group(1);
							continue;
						}
						utilBean.setnRecNum(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nRGCSRcvdDt")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnRGCSRcvdDt(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nSetDCInd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnSetDCInd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nTxnDesInstCd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnTxnDesInstCd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpBussCd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnCrdAcpBussCd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpNm")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnCrdAcpNm(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nTxnOrgInstCd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnTxnOrgInstCd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nUID")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnUID(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nFeeDCInd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						switch (feesize) {
						case 1:
							utilBean.setnFeeDCInd1(matcher.group(1));
							continue;
						case 2:
							utilBean.setnFeeDCInd2(matcher.group(1));
							continue;
						case 3:
							utilBean.setnFeeDCInd3(matcher.group(1));
							continue;
						case 4:
							utilBean.setnFeeDCInd4(matcher.group(1));
							continue;
						case 5:
							utilBean.setnFeeDCInd5(matcher.group(1));
							continue;
						}
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nFeeAmt")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						switch (feesize) {
						case 1:
							utilBean.setnFeeAmt1(matcher.group(1));
							continue;
						case 2:
							utilBean.setnFeeAmt2(matcher.group(1));
							continue;
						case 3:
							utilBean.setnFeeAmt3(matcher.group(1));
							continue;
						case 4:
							utilBean.setnFeeAmt4(matcher.group(1));
							continue;
						case 5:
							utilBean.setFeeAmt5(matcher.group(1));
							continue;
						}
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nFeeCcy")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						switch (feesize) {
						case 1:
							utilBean.setnFeeCcy1(matcher.group(1));
							continue;
						case 2:
							utilBean.setnFeeCcy2(matcher.group(1));
							continue;
						case 3:
							utilBean.setnFeeCcy3(matcher.group(1));
							continue;
						case 4:
							utilBean.setnFeeCcy4(matcher.group(1));
							continue;
						case 5:
							utilBean.setnFeeCcy5(matcher.group(1));
							continue;
						}
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nFeeTpCd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						switch (feesize) {
						case 1:
							utilBean.setnFeeTpCd1(matcher.group(1));
							continue;
						case 2:
							utilBean.setnFeeTpCd2(matcher.group(1));
							continue;
						case 3:
							utilBean.setnFeeTpCd3(matcher.group(1));
							continue;
						case 4:
							utilBean.setnFeeTpCd4(matcher.group(1));
							continue;
						case 5:
							utilBean.setnFeeTpCd5(matcher.group(1));
							continue;
						}
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nIntrchngCtg")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						switch (feesize) {
						case 1:
							utilBean.setnIntrchngCtg1(matcher.group(1));
							continue;
						case 2:
							utilBean.setnIntrchngCtg2(matcher.group(1));
							continue;
						case 3:
							utilBean.setnIntrchngCtg3(matcher.group(1));
							continue;
						case 4:
							utilBean.setnIntrchngCtg4(matcher.group(1));
							continue;
						case 5:
							utilBean.setnIntrchngCtg5(matcher.group(1));
							continue;
						}
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("/Fee")) {
						feesize++;
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nCaseNum")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnCaseNum(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nContNum")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnContNum(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nFulParInd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnFulParInd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nProcCd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnProdCd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nAmtBil")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnAmtBil(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdBil")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnCcyCdBil(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nConvRtBil")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnConvRtBil(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nMsgRsnCd")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						utilBean.setnMsgRsnCd(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nRnTtlAmt")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						headerUtil.setnRnTtlAmt(matcher.group(1));
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("nTxnCnt")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						headerUtil.setnTxnCnt(matcher.group(1));
						transactions_count = matcher.group(1);
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("Trl")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						hdr = "Trl";
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("/Trl")) {
						Matcher matcher = TAG_REGEX.matcher(thisLine);
						matcher.find();
						hdr = "";
						continue;
					}
					if (nodeMatcher.group(1).equalsIgnoreCase("/Txn")) {
						feesize = 1;
						ps.setString(1, headerUtil.getnMTI());
						ps.setString(2, headerUtil.getnFunCd());
						ps.setString(3, headerUtil.getnRecNum());
						ps.setString(4, headerUtil.getnMemInstCd());
						ps.setString(5, headerUtil.getnUnFlNm());
						ps.setString(6, headerUtil.getnDtSet());
						ps.setString(7, headerUtil.getnProdCd());
						ps.setString(8, headerUtil.getnSetBIN());
						ps.setString(9, headerUtil.getnFlCatg());
						ps.setString(10, headerUtil.getnVerNum());
						ps.setString(11, (String) null);
						ps.setString(12, (String) null);
						ps.setString(13, headerUtil.getnTxnCnt());
						ps.setString(14, headerUtil.getnRnTtlAmt());
						ps.setString(15, utilBean.getnAcqInstCd());
						ps.setString(16, utilBean.getnAmtSet());
						ps.setString(17, utilBean.getnAmtTxn());
						ps.setString(18, utilBean.getnApprvlCd());
						ps.setString(19, utilBean.getnARD());
						ps.setString(20, utilBean.getnCaseNum());
						ps.setString(21, utilBean.getnCcyCdSet());
						ps.setString(22, utilBean.getnCcyCdTxn());
						ps.setString(23, utilBean.getnConvRtSet());
						ps.setString(24, utilBean.getnCrdAcpAddAdrs());
						ps.setString(25, utilBean.getnCrdAcptTrmId());
						ps.setString(26, utilBean.getnCrdAcpZipCd());
						ps.setString(27, utilBean.getnDtTmLcTxn());
						ps.setString(28, utilBean.getnFunCd());
						ps.setString(29, utilBean.getnLtPrsntInd());
						ps.setString(30, utilBean.getnMTI());
						String pan = utilBean.getnPAN().trim();
						String Update_Pan = "";
						if (pan.length() <= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
							Update_Pan = String.valueOf(pan.substring(0, 6)) + "XXXXXX"
									+ pan.substring(pan.length() - 4);
						} else if (pan.length() >= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
							Update_Pan = String.valueOf(pan.substring(0, 6)) + "XXXXXXXXX"
									+ pan.substring(pan.length() - 4);
						} else {
							Update_Pan = null;
						}
						ps.setString(31, Update_Pan);
						ps.setString(32, utilBean.getnRecNum());
						ps.setString(33, utilBean.getnRGCSRcvdDt());
						ps.setString(34, utilBean.getnSetDCInd());
						ps.setString(35, utilBean.getnTxnDesInstCd());
						ps.setString(36, utilBean.getnTxnOrgInstCd());
						ps.setString(37, utilBean.getnUID());
						ps.setString(38, utilBean.getnAmtBil());
						ps.setString(39, utilBean.getnCcyCdBil());
						ps.setString(40, utilBean.getnConvRtBil());
						ps.setString(41, utilBean.getnMsgRsnCd());
						ps.setString(42, utilBean.getnFeeDCInd1());
						ps.setString(43, utilBean.getnFeeAmt1());
						ps.setString(44, utilBean.getnFeeCcy1());
						ps.setString(45, utilBean.getnFeeTpCd1());
						ps.setString(46, utilBean.getnIntrchngCtg1());
						ps.setString(47, utilBean.getnFeeDCInd2());
						ps.setString(48, utilBean.getnFeeAmt2());
						ps.setString(49, utilBean.getnFeeCcy2());
						ps.setString(50, utilBean.getnFeeTpCd2());
						ps.setString(51, utilBean.getnIntrchngCtg2());
						ps.setString(52, utilBean.getnFeeDCInd3());
						ps.setString(53, utilBean.getnFeeAmt3());
						ps.setString(54, utilBean.getnFeeCcy3());
						ps.setString(55, utilBean.getnFeeTpCd3());
						ps.setString(56, utilBean.getnIntrchngCtg3());
						ps.setString(57, utilBean.getnFeeDCInd4());
						ps.setString(58, utilBean.getnFeeAmt4());
						ps.setString(59, utilBean.getnFeeCcy4());
						ps.setString(60, utilBean.getnFeeTpCd4());
						ps.setString(61, utilBean.getnIntrchngCtg4());
						ps.setString(62, utilBean.getnFeeDCInd5());
						ps.setString(63, utilBean.getFeeAmt5());
						ps.setString(64, utilBean.getnFeeCcy5());
						ps.setString(65, utilBean.getnFeeTpCd5());
						ps.setString(66, utilBean.getnIntrchngCtg5());
						ps.setString(67, headerUtil.getTrl_nFunCd());
						ps.setString(68, headerUtil.getTrl_nRecNum());
						ps.setString(69, "D");
						ps.setString(70, setupBean.getFileDate());
						ps.setString(71, pan);
						if (utilBean.getnARD() != null) {
							String nARD = utilBean.getnARD();
							ps.setString(72, nARD.substring(6, 18));
						} else {
							ps.setString(72, "");
						}
						ps.setString(73, file.getOriginalFilename());
						ps.addBatch();
						utilBean = new RupayUtilBean();
						count++;
						if (count == 30000) {
							count = 1;
							ps.executeBatch();
							logger.info("Executed batch");
							count++;
						}
					}
				}
			}
			ps.executeBatch();
			con.commit();
			logger.info("Process ended" + (new Date()).getTime());
			br.close();
			ps.close();
			con.close();
			logger.info("***** ReadRupay.uploadDomesticData End ****");
			return true;
		} catch (Exception ex) {
			con.rollback();
			logger.info("Exception at line " + lineNumber);
			logger.info("Exception is " + ex);
			logger.error(" error in ReadRupay.uploadDomesticData ",
					new Exception(" ReadRupay.uploadDomesticData ", ex));
			return false;
		}
	}

	public boolean uploadInternationalData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {
		logger.info("***** ReadRupay.upload  86 INT Start ****");
		String thisLine = null;
		int lineNumber = 0, feesize = 1;
		HashMap<String, Object> output = new HashMap<>();
		String insert = "insert into rupay_rupay_86_int_rawdata(MESSAGE_TYPE ,PRODUCT_ID ,TRANSACTION_TYPE ,FROM_ACCOUNT_TYPE ,TO_ACCOUNT_TYPE ,ACTION_CODE ,RESPONSE_CODE ,PAN_NUMBER ,APPROVAL_NUMBER ,RETRIVAL_REFERENCE ,TRANSACTION_DATE ,TRANSACTION_TIME ,MERCHANT_CATAGORY_CODE ,CARD_ACCEPTOR_ID ,CARD_ACCEPTOR_TERMINAL_ID ,CARD_ACCEPTOR_TERMINAL_LOCATION ,ACQUIRER_ID ,TRANSACTION_CURRENCY_CODE ,TRANSACTION_AMOUNT ,CARD_HOLDER_BILLING_CURRENCY ,CARD_HOLDER_BILLING_AMOUNT ,PAN_ENTRY_MODE ,PAN_ENTRY_CAPABILITY ,PIN_CAPTURE_CODE ,ACQUIRER_COUNTRY_CODE ,ADDITIONAL_AMOUNT ,RUPAY_PRODUCT ,CVD2_MATCH_RESULT ,cvdICVDMatchResult ,RECCURING_PAYMENT_INDICATOR ,ECI_INDICATOR ,ICS1_RESULT_CODE ,FRAUD_SCORE ,EMI_AMOUNT ,ARQC_AUTHORIZATION ,TRANSACTION_ID ,LOYALTY_POINT ,ICS2_RESULT_CODE ,CUST_MOBILE_NUMBER ,IMAGE_CODE ,PERSONAL_PHASE ,UID_NUMBER ,CARD_DATA_INPUT_CAPABILITY ,CARD_HOLDER_AUTH_CAPABILITY ,CARD_CAPTURE_CAPABILITY ,TERMINAL_OPERATING_ENVIROMENT ,CARD_PRESENT_DATA ,CARD_HOLDER_PRESENT_DATA ,CARD_DATA_INPUT_MODE ,CARD_HOLDER_AUTH_MODE ,CARD_HOLDER_AUTH_ENTITY ,CARD_DATA_OP_CAPABILITY ,TERMINAL_DATA_OP_CAPABILITY ,PIN_CAPTURE_CAPABILITY ,ZIP_CODE ,ADVICE_REASON_CODE ,IT_PAN ,INTRAUTHNW ,OTP_INDICATOR ,ICS_TXN_ID ,NW_DATA ,SERVICE_CODE ,CURRENCY_CODE_ACTUAL_TXN_AMT ,ACTUAL_TXN_AMT ,FILEDATE ,FILENAME) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String messageType = "", productID = "", transactionType = "", fromAccountType = "", toAccountType = "";
		String actionCode = "", responseCode = "", panNumber = "", approvalNumber = "", retrievalReference = "";
		String transactionDate = "", transactionTime = "", merchantCategoryCode = "", cardAcceptorID = "";
		String cardAcceptorTerminalID = "", cardAcceptorTerminalLocation = "", acquirerID = "";
		String transactionCurrencyCode = "", transactionAmount = "", cardHolderBillingCurrency = "";
		String cardHolderBillingAmount = "", panEntryMode = "", pinEntryCapability = "", pinCaptureCode = "";
		String acquirerCountryCode = "", additionalAmount = "", ruPayProduct = "", cvd2Matchresult = "";
		String cvdICVDMatchResult = "", recurringPaymentIndicator = "", eciIndicator = "", ics1ResultCode = "";
		String fraudScore = "", emiAmount = "", arqcAuthorization = "", transactionID = "", loyaltyPoint = "";
		String ics2ResultCode = "", custMobileNumber = "", imageCode = "", personalPhase = "", uidNumber = "";
		String cardDatainputCapability = "", cardholderAuthCapability = "", cardCaptureCapability = "";
		String terminaloperatingEnvironment = "", cardholderPresentData = "", cardPresentData = "";
		String cardDataInputMode = "", cardHolderAuthMode = "", cardholderAuthEntity = "", cardDataOPCapability = "";
		String terminalDataOpCapability = "", pinCaptureCapability = "", zipCode = "", adviceReasoncode = "";
		String itPAN = "", intrauthnw = "", otpIndicator = "", icsTxnID = "", nwData = "", serviceCode = "";
		String currencyCodeActualTransactionAmount = "", actualTransactionAmount = "";
		try {
			PreparedStatement ps = con.prepareStatement(insert);
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			int count = 1;
			while ((thisLine = br.readLine()) != null) {
				if (thisLine.startsWith("01")) {
					thisLine.replaceAll("\\s", "");
					messageType = thisLine.substring(0, 2).trim();
					productID = thisLine.substring(2, 5).trim();
					transactionType = thisLine.substring(5, 7).trim();
					fromAccountType = thisLine.substring(7, 9).trim();
					toAccountType = thisLine.substring(9, 11).trim();
					actionCode = thisLine.substring(11, 12).trim();
					responseCode = thisLine.substring(12, 14).trim();
					panNumber = thisLine.substring(14, 33).trim();
					approvalNumber = thisLine.substring(33, 39).trim();
					retrievalReference = thisLine.substring(39, 51).trim();
					transactionDate = thisLine.substring(51, 58).trim();
					transactionTime = thisLine.substring(58, 64).trim();
					merchantCategoryCode = thisLine.substring(64, 68).trim();
					cardAcceptorID = thisLine.substring(68, 83).trim();
					cardAcceptorTerminalID = thisLine.substring(83, 91).trim();
					cardAcceptorTerminalLocation = thisLine.substring(91, 131).trim();
					acquirerID = thisLine.substring(131, 142).trim();
					transactionCurrencyCode = thisLine.substring(142, 145).trim();
					transactionAmount = thisLine.substring(145, 160).trim();
					cardHolderBillingCurrency = thisLine.substring(160, 163).trim();
					cardHolderBillingAmount = thisLine.substring(163, 178).trim();
					panEntryMode = thisLine.substring(178, 180).trim();
					pinEntryCapability = thisLine.substring(180, 181).trim();
					pinCaptureCode = thisLine.substring(181, 183).trim();
					acquirerCountryCode = thisLine.substring(183, 186).trim();
					additionalAmount = thisLine.substring(186, 201).trim();
					ruPayProduct = thisLine.substring(201, 206).trim();
					cvd2Matchresult = thisLine.substring(206, 207).trim();
					cvdICVDMatchResult = thisLine.substring(207, 208).trim();
					recurringPaymentIndicator = thisLine.substring(208, 210).trim();
					eciIndicator = thisLine.substring(210, 212).trim();
					ics1ResultCode = thisLine.substring(212, 214).trim();
					fraudScore = thisLine.substring(214, 219).trim();
					emiAmount = thisLine.substring(219, 245).trim();
					arqcAuthorization = thisLine.substring(245, 246).trim();
					transactionID = thisLine.substring(246, 276).trim();
					loyaltyPoint = thisLine.substring(276, 282).trim();
					ics2ResultCode = thisLine.substring(282, 283).trim();
					custMobileNumber = thisLine.substring(283, 295).trim();
					imageCode = thisLine.substring(295, 300).trim();
					personalPhase = thisLine.substring(300, 305).trim();
					uidNumber = thisLine.substring(305, 317).trim();
					cardDatainputCapability = thisLine.substring(317, 318).trim();
					cardholderAuthCapability = thisLine.substring(318, 319).trim();
					cardCaptureCapability = thisLine.substring(319, 320).trim();
					terminaloperatingEnvironment = thisLine.substring(320, 321).trim();
					cardholderPresentData = thisLine.substring(321, 322).trim();
					cardPresentData = thisLine.substring(322, 323).trim();
					cardDataInputMode = thisLine.substring(323, 324).trim();
					cardHolderAuthMode = thisLine.substring(324, 325).trim();
					cardholderAuthEntity = thisLine.substring(325, 326).trim();
					cardDataOPCapability = thisLine.substring(326, 327).trim();
					terminalDataOpCapability = thisLine.substring(327, 328).trim();
					pinCaptureCapability = thisLine.substring(328, 329).trim();
					zipCode = thisLine.substring(329, 338).trim();
					adviceReasoncode = thisLine.substring(338, 345).trim();
					itPAN = thisLine.substring(345, 355).trim();
					intrauthnw = thisLine.substring(355, 370).trim();
					otpIndicator = thisLine.substring(370, 371).trim();
					icsTxnID = thisLine.substring(371, 386).trim();
					nwData = thisLine.substring(386, 398).trim();
					serviceCode = thisLine.substring(398, 401).trim();
					currencyCodeActualTransactionAmount = thisLine.substring(401, 404).trim();
					actualTransactionAmount = thisLine.substring(404, 419).trim();
					lineNumber++;
					ps.setString(1, messageType);
					ps.setString(2, productID);
					ps.setString(3, transactionType);
					ps.setString(4, fromAccountType);
					ps.setString(5, toAccountType);
					ps.setString(6, actionCode);
					ps.setString(7, responseCode);
					ps.setString(8, panNumber);
					ps.setString(9, approvalNumber);
					ps.setString(10, retrievalReference);
					ps.setString(11, transactionDate);
					ps.setString(12, transactionTime);
					ps.setString(13, merchantCategoryCode);
					ps.setString(14, cardAcceptorID);
					ps.setString(15, cardAcceptorTerminalID);
					ps.setString(16, cardAcceptorTerminalLocation);
					ps.setString(17, acquirerID);
					ps.setString(18, transactionCurrencyCode);
					ps.setString(19, transactionAmount);
					ps.setString(20, cardHolderBillingCurrency);
					ps.setString(21, cardHolderBillingAmount);
					ps.setString(22, panEntryMode);
					ps.setString(23, pinEntryCapability);
					ps.setString(24, pinCaptureCode);
					ps.setString(25, acquirerCountryCode);
					ps.setString(26, additionalAmount);
					ps.setString(27, ruPayProduct);
					ps.setString(28, cvd2Matchresult);
					ps.setString(29, cvdICVDMatchResult);
					ps.setString(30, recurringPaymentIndicator);
					ps.setString(31, eciIndicator);
					ps.setString(32, ics1ResultCode);
					ps.setString(33, fraudScore);
					ps.setString(34, emiAmount);
					ps.setString(35, arqcAuthorization);
					ps.setString(36, transactionID);
					ps.setString(37, loyaltyPoint);
					ps.setString(38, ics2ResultCode);
					ps.setString(39, custMobileNumber);
					ps.setString(40, imageCode);
					ps.setString(41, personalPhase);
					ps.setString(42, uidNumber);
					ps.setString(43, cardDatainputCapability);
					ps.setString(44, cardholderAuthCapability);
					ps.setString(45, cardCaptureCapability);
					ps.setString(46, terminaloperatingEnvironment);
					ps.setString(47, cardholderPresentData);
					ps.setString(48, cardPresentData);
					ps.setString(49, cardDataInputMode);
					ps.setString(50, cardHolderAuthMode);
					ps.setString(51, cardholderAuthEntity);
					ps.setString(52, cardDataOPCapability);
					ps.setString(53, terminalDataOpCapability);
					ps.setString(54, pinCaptureCapability);
					ps.setString(55, zipCode);
					ps.setString(56, adviceReasoncode);
					ps.setString(57, itPAN);
					ps.setString(58, intrauthnw);
					ps.setString(59, otpIndicator);
					ps.setString(60, icsTxnID);
					ps.setString(61, nwData);
					ps.setString(62, serviceCode);
					ps.setString(63, currencyCodeActualTransactionAmount);
					ps.setString(64, actualTransactionAmount);
					ps.setString(65, setupBean.getFileDate());
					ps.setString(66, file.getOriginalFilename());
					ps.addBatch();
					count++;
					messageType = "";
					productID = "";
					transactionType = "";
					fromAccountType = "";
					toAccountType = "";
					actionCode = "";
					responseCode = "";
					panNumber = "";
					approvalNumber = "";
					retrievalReference = "";
					transactionDate = "";
					transactionTime = "";
					merchantCategoryCode = "";
					cardAcceptorID = "";
					cardAcceptorTerminalID = "";
					cardAcceptorTerminalLocation = "";
					acquirerID = "";
					transactionCurrencyCode = "";
					transactionAmount = "";
					cardHolderBillingCurrency = "";
					cardHolderBillingAmount = "";
					panEntryMode = "";
					pinEntryCapability = "";
					pinCaptureCode = "";
					acquirerCountryCode = "";
					additionalAmount = "";
					ruPayProduct = "";
					cvd2Matchresult = "";
					cvdICVDMatchResult = "";
					recurringPaymentIndicator = "";
					eciIndicator = "";
					ics1ResultCode = "";
					fraudScore = "";
					emiAmount = "";
					arqcAuthorization = "";
					transactionID = "";
					loyaltyPoint = "";
					ics2ResultCode = "";
					custMobileNumber = "";
					imageCode = "";
					personalPhase = "";
					uidNumber = "";
					cardDatainputCapability = "";
					cardholderAuthCapability = "";
					cardCaptureCapability = "";
					terminaloperatingEnvironment = "";
					cardholderPresentData = "";
					cardPresentData = "";
					cardDataInputMode = "";
					cardHolderAuthMode = "";
					cardholderAuthEntity = "";
					cardDataOPCapability = "";
					terminalDataOpCapability = "";
					pinCaptureCapability = "";
					zipCode = "";
					adviceReasoncode = "";
					itPAN = "";
					intrauthnw = "";
					otpIndicator = "";
					icsTxnID = "";
					nwData = "";
					serviceCode = "";
					currencyCodeActualTransactionAmount = "";
					actualTransactionAmount = "";
				}
			}
			logger.info("Executed Batch Successfully");
			ps.executeBatch();
			ps.close();
			br.close();
			con.close();
			logger.info("***** ReadRupay.uploadUtkarshRupayDomesticData End ****");
			return true;
		} catch (Exception e) {
			logger.info("Exception at line  " + thisLine);
			logger.info("Exception at line number " + lineNumber);
			logger.info("Error msg " + e.getMessage());
			return false;
		}
	}

	public HashMap<String, Object> readDATData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {
		HashMap<String, Object> output = new HashMap<>();
		try {
			logger.info("***** ReadRupay.readData Start ****");
			boolean uploaded = false;
			logger.info(setupBean.getStSubCategory());
			if (setupBean.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
				logger.info("Entered CBS File is DOMESTIC");
			} else if (setupBean.getStSubCategory().equalsIgnoreCase("INTERNATIONAL")) {
				logger.info("Entered CBS File is INTERNATIONAL");
				uploaded = uploadInternationalData(setupBean, con, file, sourceBean);
			} else {
				logger.info("Entered File is Wrong");
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "File uploaded is incorrect");
				return output;
			}
			logger.info("***** ReadRupay.readData End ****");
			return output;
		} catch (Exception e) {
			logger.error(" error in ReadRupay.readData", new Exception("ReadRupay.readData", e));
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception Occurred");
			return output;
		}
	}

	public boolean uploadKerelaQSPARCDomesticDataDAT(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {
		logger.info("***** ReadRupay.upload  86 Start ****");
		String thisLine = null;
		int lineNumber = 0, feesize = 1;
		HashMap<String, Object> output = new HashMap<>();
		String insert = "insert into qsparc_86_rawdata(MESSAGE_TYPE ,PRODUCT_ID ,TRANSACTION_TYPE ,FROM_ACCOUNT_TYPE ,TO_ACCOUNT_TYPE ,ACTION_CODE ,RESPONSE_CODE ,PAN_NUMBER ,APPROVAL_NUMBER ,RETRIVAL_REFERENCE ,TRANSACTION_DATE ,TRANSACTION_TIME ,MERCHANT_CATAGORY_CODE ,CARD_ACCEPTOR_ID ,CARD_ACCEPTOR_TERMINAL_ID ,CARD_ACCEPTOR_TERMINAL_LOCATION ,ACQUIRER_ID ,TRANSACTION_CURRENCY_CODE ,TRANSACTION_AMOUNT ,CARD_HOLDER_BILLING_CURRENCY ,CARD_HOLDER_BILLING_AMOUNT ,PAN_ENTRY_MODE ,PAN_ENTRY_CAPABILITY ,PIN_CAPTURE_CODE ,ACQUIRER_COUNTRY_CODE ,ADDITIONAL_AMOUNT ,RUPAY_PRODUCT ,CVD2_MATCH_RESULT ,cvdICVDMatchResult ,RECCURING_PAYMENT_INDICATOR ,ECI_INDICATOR ,ICS1_RESULT_CODE ,FRAUD_SCORE ,EMI_AMOUNT ,ARQC_AUTHORIZATION ,TRANSACTION_ID ,LOYALTY_POINT ,ICS2_RESULT_CODE ,CUST_MOBILE_NUMBER ,IMAGE_CODE ,PERSONAL_PHASE ,UID_NUMBER ,CARD_DATA_INPUT_CAPABILITY ,CARD_HOLDER_AUTH_CAPABILITY ,CARD_CAPTURE_CAPABILITY ,TERMINAL_OPERATING_ENVIROMENT ,CARD_PRESENT_DATA ,CARD_HOLDER_PRESENT_DATA ,CARD_DATA_INPUT_MODE ,CARD_HOLDER_AUTH_MODE ,CARD_HOLDER_AUTH_ENTITY ,CARD_DATA_OP_CAPABILITY ,TERMINAL_DATA_OP_CAPABILITY ,PIN_CAPTURE_CAPABILITY ,ZIP_CODE ,ADVICE_REASON_CODE ,IT_PAN ,INTRAUTHNW ,OTP_INDICATOR ,ICS_TXN_ID ,NW_DATA ,SERVICE_CODE ,CURRENCY_CODE_ACTUAL_TXN_AMT ,ACTUAL_TXN_AMT ,FILEDATE ,FILENAME) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String messageType = "", productID = "", transactionType = "", fromAccountType = "", toAccountType = "";
		String actionCode = "", responseCode = "", panNumber = "", approvalNumber = "", retrievalReference = "";
		String transactionDate = "", transactionTime = "", merchantCategoryCode = "", cardAcceptorID = "";
		String cardAcceptorTerminalID = "", cardAcceptorTerminalLocation = "", acquirerID = "";
		String transactionCurrencyCode = "", transactionAmount = "", cardHolderBillingCurrency = "";
		String cardHolderBillingAmount = "", panEntryMode = "", pinEntryCapability = "", pinCaptureCode = "";
		String acquirerCountryCode = "", additionalAmount = "", ruPayProduct = "", cvd2Matchresult = "";
		String cvdICVDMatchResult = "", recurringPaymentIndicator = "", eciIndicator = "", ics1ResultCode = "";
		String fraudScore = "", emiAmount = "", arqcAuthorization = "", transactionID = "", loyaltyPoint = "";
		String ics2ResultCode = "", custMobileNumber = "", imageCode = "", personalPhase = "", uidNumber = "";
		String cardDatainputCapability = "", cardholderAuthCapability = "", cardCaptureCapability = "";
		String terminaloperatingEnvironment = "", cardholderPresentData = "", cardPresentData = "";
		String cardDataInputMode = "", cardHolderAuthMode = "", cardholderAuthEntity = "", cardDataOPCapability = "";
		String terminalDataOpCapability = "", pinCaptureCapability = "", zipCode = "", adviceReasoncode = "";
		String itPAN = "", intrauthnw = "", otpIndicator = "", icsTxnID = "", nwData = "", serviceCode = "";
		String currencyCodeActualTransactionAmount = "", actualTransactionAmount = "";
		try {
			PreparedStatement ps = con.prepareStatement(insert);
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			int count = 1;
			while ((thisLine = br.readLine()) != null) {
				if (thisLine.startsWith("01")) {
					thisLine.replaceAll("\\s", "");
					messageType = thisLine.substring(0, 2).trim();
					productID = thisLine.substring(2, 5).trim();
					transactionType = thisLine.substring(5, 7).trim();
					fromAccountType = thisLine.substring(7, 9).trim();
					toAccountType = thisLine.substring(9, 11).trim();
					actionCode = thisLine.substring(11, 12).trim();
					responseCode = thisLine.substring(12, 14).trim();
					panNumber = thisLine.substring(14, 33).trim();
					approvalNumber = thisLine.substring(33, 39).trim();
					retrievalReference = thisLine.substring(39, 51).trim();
					transactionDate = thisLine.substring(51, 58).trim();
					transactionTime = thisLine.substring(58, 64).trim();
					merchantCategoryCode = thisLine.substring(64, 68).trim();
					cardAcceptorID = thisLine.substring(68, 83).trim();
					cardAcceptorTerminalID = thisLine.substring(83, 91).trim();
					cardAcceptorTerminalLocation = thisLine.substring(91, 131).trim();
					acquirerID = thisLine.substring(131, 142).trim();
					transactionCurrencyCode = thisLine.substring(142, 145).trim();
					transactionAmount = thisLine.substring(145, 160).trim();
					cardHolderBillingCurrency = thisLine.substring(160, 163).trim();
					cardHolderBillingAmount = thisLine.substring(163, 178).trim();
					panEntryMode = thisLine.substring(178, 180).trim();
					pinEntryCapability = thisLine.substring(180, 181).trim();
					pinCaptureCode = thisLine.substring(181, 183).trim();
					acquirerCountryCode = thisLine.substring(183, 186).trim();
					additionalAmount = thisLine.substring(186, 201).trim();
					ruPayProduct = thisLine.substring(201, 206).trim();
					cvd2Matchresult = thisLine.substring(206, 207).trim();
					cvdICVDMatchResult = thisLine.substring(207, 208).trim();
					recurringPaymentIndicator = thisLine.substring(208, 210).trim();
					eciIndicator = thisLine.substring(210, 212).trim();
					ics1ResultCode = thisLine.substring(212, 214).trim();
					fraudScore = thisLine.substring(214, 219).trim();
					emiAmount = thisLine.substring(219, 245).trim();
					arqcAuthorization = thisLine.substring(245, 246).trim();
					transactionID = thisLine.substring(246, 276).trim();
					loyaltyPoint = thisLine.substring(276, 282).trim();
					ics2ResultCode = thisLine.substring(282, 283).trim();
					custMobileNumber = thisLine.substring(283, 295).trim();
					imageCode = thisLine.substring(295, 300).trim();
					personalPhase = thisLine.substring(300, 305).trim();
					uidNumber = thisLine.substring(305, 317).trim();
					cardDatainputCapability = thisLine.substring(317, 318).trim();
					cardholderAuthCapability = thisLine.substring(318, 319).trim();
					cardCaptureCapability = thisLine.substring(319, 320).trim();
					terminaloperatingEnvironment = thisLine.substring(320, 321).trim();
					cardholderPresentData = thisLine.substring(321, 322).trim();
					cardPresentData = thisLine.substring(322, 323).trim();
					cardDataInputMode = thisLine.substring(323, 324).trim();
					cardHolderAuthMode = thisLine.substring(324, 325).trim();
					cardholderAuthEntity = thisLine.substring(325, 326).trim();
					cardDataOPCapability = thisLine.substring(326, 327).trim();
					terminalDataOpCapability = thisLine.substring(327, 328).trim();
					pinCaptureCapability = thisLine.substring(328, 329).trim();
					zipCode = thisLine.substring(329, 338).trim();
					adviceReasoncode = thisLine.substring(338, 345).trim();
					itPAN = thisLine.substring(345, 355).trim();
					intrauthnw = thisLine.substring(355, 370).trim();
					otpIndicator = thisLine.substring(370, 371).trim();
					icsTxnID = thisLine.substring(371, 386).trim();
					nwData = thisLine.substring(386, 398).trim();
					serviceCode = thisLine.substring(398, 401).trim();
					currencyCodeActualTransactionAmount = thisLine.substring(401, 404).trim();
					actualTransactionAmount = thisLine.substring(404, 419).trim();
					lineNumber++;
					ps.setString(1, messageType);
					ps.setString(2, productID);
					ps.setString(3, transactionType);
					ps.setString(4, fromAccountType);
					ps.setString(5, toAccountType);
					ps.setString(6, actionCode);
					ps.setString(7, responseCode);
					ps.setString(8, panNumber);
					ps.setString(9, approvalNumber);
					ps.setString(10, retrievalReference);
					ps.setString(11, transactionDate);
					ps.setString(12, transactionTime);
					ps.setString(13, merchantCategoryCode);
					ps.setString(14, cardAcceptorID);
					ps.setString(15, cardAcceptorTerminalID);
					ps.setString(16, cardAcceptorTerminalLocation);
					ps.setString(17, acquirerID);
					ps.setString(18, transactionCurrencyCode);
					ps.setString(19, transactionAmount);
					ps.setString(20, cardHolderBillingCurrency);
					ps.setString(21, cardHolderBillingAmount);
					ps.setString(22, panEntryMode);
					ps.setString(23, pinEntryCapability);
					ps.setString(24, pinCaptureCode);
					ps.setString(25, acquirerCountryCode);
					ps.setString(26, additionalAmount);
					ps.setString(27, ruPayProduct);
					ps.setString(28, cvd2Matchresult);
					ps.setString(29, cvdICVDMatchResult);
					ps.setString(30, recurringPaymentIndicator);
					ps.setString(31, eciIndicator);
					ps.setString(32, ics1ResultCode);
					ps.setString(33, fraudScore);
					ps.setString(34, emiAmount);
					ps.setString(35, arqcAuthorization);
					ps.setString(36, transactionID);
					ps.setString(37, loyaltyPoint);
					ps.setString(38, ics2ResultCode);
					ps.setString(39, custMobileNumber);
					ps.setString(40, imageCode);
					ps.setString(41, personalPhase);
					ps.setString(42, uidNumber);
					ps.setString(43, cardDatainputCapability);
					ps.setString(44, cardholderAuthCapability);
					ps.setString(45, cardCaptureCapability);
					ps.setString(46, terminaloperatingEnvironment);
					ps.setString(47, cardholderPresentData);
					ps.setString(48, cardPresentData);
					ps.setString(49, cardDataInputMode);
					ps.setString(50, cardHolderAuthMode);
					ps.setString(51, cardholderAuthEntity);
					ps.setString(52, cardDataOPCapability);
					ps.setString(53, terminalDataOpCapability);
					ps.setString(54, pinCaptureCapability);
					ps.setString(55, zipCode);
					ps.setString(56, adviceReasoncode);
					ps.setString(57, itPAN);
					ps.setString(58, intrauthnw);
					ps.setString(59, otpIndicator);
					ps.setString(60, icsTxnID);
					ps.setString(61, nwData);
					ps.setString(62, serviceCode);
					ps.setString(63, currencyCodeActualTransactionAmount);
					ps.setString(64, actualTransactionAmount);
					ps.setString(65, setupBean.getFileDate());
					ps.setString(66, file.getOriginalFilename());
					ps.addBatch();
					count++;
					messageType = "";
					productID = "";
					transactionType = "";
					fromAccountType = "";
					toAccountType = "";
					actionCode = "";
					responseCode = "";
					panNumber = "";
					approvalNumber = "";
					retrievalReference = "";
					transactionDate = "";
					transactionTime = "";
					merchantCategoryCode = "";
					cardAcceptorID = "";
					cardAcceptorTerminalID = "";
					cardAcceptorTerminalLocation = "";
					acquirerID = "";
					transactionCurrencyCode = "";
					transactionAmount = "";
					cardHolderBillingCurrency = "";
					cardHolderBillingAmount = "";
					panEntryMode = "";
					pinEntryCapability = "";
					pinCaptureCode = "";
					acquirerCountryCode = "";
					additionalAmount = "";
					ruPayProduct = "";
					cvd2Matchresult = "";
					cvdICVDMatchResult = "";
					recurringPaymentIndicator = "";
					eciIndicator = "";
					ics1ResultCode = "";
					fraudScore = "";
					emiAmount = "";
					arqcAuthorization = "";
					transactionID = "";
					loyaltyPoint = "";
					ics2ResultCode = "";
					custMobileNumber = "";
					imageCode = "";
					personalPhase = "";
					uidNumber = "";
					cardDatainputCapability = "";
					cardholderAuthCapability = "";
					cardCaptureCapability = "";
					terminaloperatingEnvironment = "";
					cardholderPresentData = "";
					cardPresentData = "";
					cardDataInputMode = "";
					cardHolderAuthMode = "";
					cardholderAuthEntity = "";
					cardDataOPCapability = "";
					terminalDataOpCapability = "";
					pinCaptureCapability = "";
					zipCode = "";
					adviceReasoncode = "";
					itPAN = "";
					intrauthnw = "";
					otpIndicator = "";
					icsTxnID = "";
					nwData = "";
					serviceCode = "";
					currencyCodeActualTransactionAmount = "";
					actualTransactionAmount = "";
				}
			}
			logger.info("Executed Batch Successfully");
			ps.executeBatch();
			ps.close();
			br.close();
			con.close();
			logger.info("***** ReadRupay.uploadUtkarshRupayDomesticData End ****");
			return true;
		} catch (Exception e) {
			logger.info("Exception at line  " + thisLine);
			logger.info("Exception at line number " + lineNumber);
			logger.info("Error msg " + e.getMessage());
			return false;
		}
	}

	public boolean uploadKerelaQSPARCINTDataDAT(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {
		logger.info("***** ReadRupay.upload  86 Start ****");
		String thisLine = null;
		int lineNumber = 0, feesize = 1;
		HashMap<String, Object> output = new HashMap<>();
		String insert = "insert into  qsparc_int_86_rawdata(MESSAGE_TYPE ,PRODUCT_ID ,TRANSACTION_TYPE ,FROM_ACCOUNT_TYPE ,TO_ACCOUNT_TYPE ,ACTION_CODE ,RESPONSE_CODE ,PAN_NUMBER ,APPROVAL_NUMBER ,RETRIVAL_REFERENCE ,TRANSACTION_DATE ,TRANSACTION_TIME ,MERCHANT_CATAGORY_CODE ,CARD_ACCEPTOR_ID ,CARD_ACCEPTOR_TERMINAL_ID ,CARD_ACCEPTOR_TERMINAL_LOCATION ,ACQUIRER_ID ,TRANSACTION_CURRENCY_CODE ,TRANSACTION_AMOUNT ,CARD_HOLDER_BILLING_CURRENCY ,CARD_HOLDER_BILLING_AMOUNT ,PAN_ENTRY_MODE ,PAN_ENTRY_CAPABILITY ,PIN_CAPTURE_CODE ,ACQUIRER_COUNTRY_CODE ,ADDITIONAL_AMOUNT ,RUPAY_PRODUCT ,CVD2_MATCH_RESULT ,cvdICVDMatchResult ,RECCURING_PAYMENT_INDICATOR ,ECI_INDICATOR ,ICS1_RESULT_CODE ,FRAUD_SCORE ,EMI_AMOUNT ,ARQC_AUTHORIZATION ,TRANSACTION_ID ,LOYALTY_POINT ,ICS2_RESULT_CODE ,CUST_MOBILE_NUMBER ,IMAGE_CODE ,PERSONAL_PHASE ,UID_NUMBER ,CARD_DATA_INPUT_CAPABILITY ,CARD_HOLDER_AUTH_CAPABILITY ,CARD_CAPTURE_CAPABILITY ,TERMINAL_OPERATING_ENVIROMENT ,CARD_PRESENT_DATA ,CARD_HOLDER_PRESENT_DATA ,CARD_DATA_INPUT_MODE ,CARD_HOLDER_AUTH_MODE ,CARD_HOLDER_AUTH_ENTITY ,CARD_DATA_OP_CAPABILITY ,TERMINAL_DATA_OP_CAPABILITY ,PIN_CAPTURE_CAPABILITY ,ZIP_CODE ,ADVICE_REASON_CODE ,IT_PAN ,INTRAUTHNW ,OTP_INDICATOR ,ICS_TXN_ID ,NW_DATA ,SERVICE_CODE ,CURRENCY_CODE_ACTUAL_TXN_AMT ,ACTUAL_TXN_AMT ,FILEDATE ,FILENAME) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String messageType = "", productID = "", transactionType = "", fromAccountType = "", toAccountType = "";
		String actionCode = "", responseCode = "", panNumber = "", approvalNumber = "", retrievalReference = "";
		String transactionDate = "", transactionTime = "", merchantCategoryCode = "", cardAcceptorID = "";
		String cardAcceptorTerminalID = "", cardAcceptorTerminalLocation = "", acquirerID = "";
		String transactionCurrencyCode = "", transactionAmount = "", cardHolderBillingCurrency = "";
		String cardHolderBillingAmount = "", panEntryMode = "", pinEntryCapability = "", pinCaptureCode = "";
		String acquirerCountryCode = "", additionalAmount = "", ruPayProduct = "", cvd2Matchresult = "";
		String cvdICVDMatchResult = "", recurringPaymentIndicator = "", eciIndicator = "", ics1ResultCode = "";
		String fraudScore = "", emiAmount = "", arqcAuthorization = "", transactionID = "", loyaltyPoint = "";
		String ics2ResultCode = "", custMobileNumber = "", imageCode = "", personalPhase = "", uidNumber = "";
		String cardDatainputCapability = "", cardholderAuthCapability = "", cardCaptureCapability = "";
		String terminaloperatingEnvironment = "", cardholderPresentData = "", cardPresentData = "";
		String cardDataInputMode = "", cardHolderAuthMode = "", cardholderAuthEntity = "", cardDataOPCapability = "";
		String terminalDataOpCapability = "", pinCaptureCapability = "", zipCode = "", adviceReasoncode = "";
		String itPAN = "", intrauthnw = "", otpIndicator = "", icsTxnID = "", nwData = "", serviceCode = "";
		String currencyCodeActualTransactionAmount = "", actualTransactionAmount = "";
		try {
			PreparedStatement ps = con.prepareStatement(insert);
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			int count = 1;
			while ((thisLine = br.readLine()) != null) {
				if (thisLine.startsWith("01")) {
					thisLine.replaceAll("\\s", "");
					messageType = thisLine.substring(0, 2).trim();
					productID = thisLine.substring(2, 5).trim();
					transactionType = thisLine.substring(5, 7).trim();
					fromAccountType = thisLine.substring(7, 9).trim();
					toAccountType = thisLine.substring(9, 11).trim();
					actionCode = thisLine.substring(11, 12).trim();
					responseCode = thisLine.substring(12, 14).trim();
					panNumber = thisLine.substring(14, 33).trim();
					approvalNumber = thisLine.substring(33, 39).trim();
					retrievalReference = thisLine.substring(39, 51).trim();
					transactionDate = thisLine.substring(51, 58).trim();
					transactionTime = thisLine.substring(58, 64).trim();
					merchantCategoryCode = thisLine.substring(64, 68).trim();
					cardAcceptorID = thisLine.substring(68, 83).trim();
					cardAcceptorTerminalID = thisLine.substring(83, 91).trim();
					cardAcceptorTerminalLocation = thisLine.substring(91, 131).trim();
					acquirerID = thisLine.substring(131, 142).trim();
					transactionCurrencyCode = thisLine.substring(142, 145).trim();
					transactionAmount = thisLine.substring(145, 160).trim();
					cardHolderBillingCurrency = thisLine.substring(160, 163).trim();
					cardHolderBillingAmount = thisLine.substring(163, 178).trim();
					panEntryMode = thisLine.substring(178, 180).trim();
					pinEntryCapability = thisLine.substring(180, 181).trim();
					pinCaptureCode = thisLine.substring(181, 183).trim();
					acquirerCountryCode = thisLine.substring(183, 186).trim();
					additionalAmount = thisLine.substring(186, 201).trim();
					ruPayProduct = thisLine.substring(201, 206).trim();
					cvd2Matchresult = thisLine.substring(206, 207).trim();
					cvdICVDMatchResult = thisLine.substring(207, 208).trim();
					recurringPaymentIndicator = thisLine.substring(208, 210).trim();
					eciIndicator = thisLine.substring(210, 212).trim();
					ics1ResultCode = thisLine.substring(212, 214).trim();
					fraudScore = thisLine.substring(214, 219).trim();
					emiAmount = thisLine.substring(219, 245).trim();
					arqcAuthorization = thisLine.substring(245, 246).trim();
					transactionID = thisLine.substring(246, 276).trim();
					loyaltyPoint = thisLine.substring(276, 282).trim();
					ics2ResultCode = thisLine.substring(282, 283).trim();
					custMobileNumber = thisLine.substring(283, 295).trim();
					imageCode = thisLine.substring(295, 300).trim();
					personalPhase = thisLine.substring(300, 305).trim();
					uidNumber = thisLine.substring(305, 317).trim();
					cardDatainputCapability = thisLine.substring(317, 318).trim();
					cardholderAuthCapability = thisLine.substring(318, 319).trim();
					cardCaptureCapability = thisLine.substring(319, 320).trim();
					terminaloperatingEnvironment = thisLine.substring(320, 321).trim();
					cardholderPresentData = thisLine.substring(321, 322).trim();
					cardPresentData = thisLine.substring(322, 323).trim();
					cardDataInputMode = thisLine.substring(323, 324).trim();
					cardHolderAuthMode = thisLine.substring(324, 325).trim();
					cardholderAuthEntity = thisLine.substring(325, 326).trim();
					cardDataOPCapability = thisLine.substring(326, 327).trim();
					terminalDataOpCapability = thisLine.substring(327, 328).trim();
					pinCaptureCapability = thisLine.substring(328, 329).trim();
					zipCode = thisLine.substring(329, 338).trim();
					adviceReasoncode = thisLine.substring(338, 345).trim();
					itPAN = thisLine.substring(345, 355).trim();
					intrauthnw = thisLine.substring(355, 370).trim();
					otpIndicator = thisLine.substring(370, 371).trim();
					icsTxnID = thisLine.substring(371, 386).trim();
					nwData = thisLine.substring(386, 398).trim();
					serviceCode = thisLine.substring(398, 401).trim();
					currencyCodeActualTransactionAmount = thisLine.substring(401, 404).trim();
					actualTransactionAmount = thisLine.substring(404, 419).trim();
					lineNumber++;
					ps.setString(1, messageType);
					ps.setString(2, productID);
					ps.setString(3, transactionType);
					ps.setString(4, fromAccountType);
					ps.setString(5, toAccountType);
					ps.setString(6, actionCode);
					ps.setString(7, responseCode);
					ps.setString(8, panNumber);
					ps.setString(9, approvalNumber);
					ps.setString(10, retrievalReference);
					ps.setString(11, transactionDate);
					ps.setString(12, transactionTime);
					ps.setString(13, merchantCategoryCode);
					ps.setString(14, cardAcceptorID);
					ps.setString(15, cardAcceptorTerminalID);
					ps.setString(16, cardAcceptorTerminalLocation);
					ps.setString(17, acquirerID);
					ps.setString(18, transactionCurrencyCode);
					ps.setString(19, transactionAmount);
					ps.setString(20, cardHolderBillingCurrency);
					ps.setString(21, cardHolderBillingAmount);
					ps.setString(22, panEntryMode);
					ps.setString(23, pinEntryCapability);
					ps.setString(24, pinCaptureCode);
					ps.setString(25, acquirerCountryCode);
					ps.setString(26, additionalAmount);
					ps.setString(27, ruPayProduct);
					ps.setString(28, cvd2Matchresult);
					ps.setString(29, cvdICVDMatchResult);
					ps.setString(30, recurringPaymentIndicator);
					ps.setString(31, eciIndicator);
					ps.setString(32, ics1ResultCode);
					ps.setString(33, fraudScore);
					ps.setString(34, emiAmount);
					ps.setString(35, arqcAuthorization);
					ps.setString(36, transactionID);
					ps.setString(37, loyaltyPoint);
					ps.setString(38, ics2ResultCode);
					ps.setString(39, custMobileNumber);
					ps.setString(40, imageCode);
					ps.setString(41, personalPhase);
					ps.setString(42, uidNumber);
					ps.setString(43, cardDatainputCapability);
					ps.setString(44, cardholderAuthCapability);
					ps.setString(45, cardCaptureCapability);
					ps.setString(46, terminaloperatingEnvironment);
					ps.setString(47, cardholderPresentData);
					ps.setString(48, cardPresentData);
					ps.setString(49, cardDataInputMode);
					ps.setString(50, cardHolderAuthMode);
					ps.setString(51, cardholderAuthEntity);
					ps.setString(52, cardDataOPCapability);
					ps.setString(53, terminalDataOpCapability);
					ps.setString(54, pinCaptureCapability);
					ps.setString(55, zipCode);
					ps.setString(56, adviceReasoncode);
					ps.setString(57, itPAN);
					ps.setString(58, intrauthnw);
					ps.setString(59, otpIndicator);
					ps.setString(60, icsTxnID);
					ps.setString(61, nwData);
					ps.setString(62, serviceCode);
					ps.setString(63, currencyCodeActualTransactionAmount);
					ps.setString(64, actualTransactionAmount);
					ps.setString(65, setupBean.getFileDate());
					ps.setString(66, file.getOriginalFilename());
					ps.addBatch();
					count++;
					messageType = "";
					productID = "";
					transactionType = "";
					fromAccountType = "";
					toAccountType = "";
					actionCode = "";
					responseCode = "";
					panNumber = "";
					approvalNumber = "";
					retrievalReference = "";
					transactionDate = "";
					transactionTime = "";
					merchantCategoryCode = "";
					cardAcceptorID = "";
					cardAcceptorTerminalID = "";
					cardAcceptorTerminalLocation = "";
					acquirerID = "";
					transactionCurrencyCode = "";
					transactionAmount = "";
					cardHolderBillingCurrency = "";
					cardHolderBillingAmount = "";
					panEntryMode = "";
					pinEntryCapability = "";
					pinCaptureCode = "";
					acquirerCountryCode = "";
					additionalAmount = "";
					ruPayProduct = "";
					cvd2Matchresult = "";
					cvdICVDMatchResult = "";
					recurringPaymentIndicator = "";
					eciIndicator = "";
					ics1ResultCode = "";
					fraudScore = "";
					emiAmount = "";
					arqcAuthorization = "";
					transactionID = "";
					loyaltyPoint = "";
					ics2ResultCode = "";
					custMobileNumber = "";
					imageCode = "";
					personalPhase = "";
					uidNumber = "";
					cardDatainputCapability = "";
					cardholderAuthCapability = "";
					cardCaptureCapability = "";
					terminaloperatingEnvironment = "";
					cardholderPresentData = "";
					cardPresentData = "";
					cardDataInputMode = "";
					cardHolderAuthMode = "";
					cardholderAuthEntity = "";
					cardDataOPCapability = "";
					terminalDataOpCapability = "";
					pinCaptureCapability = "";
					zipCode = "";
					adviceReasoncode = "";
					itPAN = "";
					intrauthnw = "";
					otpIndicator = "";
					icsTxnID = "";
					nwData = "";
					serviceCode = "";
					currencyCodeActualTransactionAmount = "";
					actualTransactionAmount = "";
				}
			}
			logger.info("Executed Batch Successfully");
			ps.executeBatch();
			ps.close();
			br.close();
			con.close();
			logger.info("***** ReadRupay.uploadUtkarshRupayDomesticData End ****");
			return true;
		} catch (Exception e) {
			logger.info("Exception at line  " + thisLine);
			logger.info("Exception at line number " + lineNumber);
			logger.info("Error msg " + e.getMessage());
			return false;
		}
	}

	public boolean uploadKerelaRupayDomesticDataDAT(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {
		logger.info("***** ReadRupay.upload  86 Start ****");
		String thisLine = null;
		int lineNumber = 0, feesize = 1;
		HashMap<String, Object> output = new HashMap<>();
		String insert = "insert into rupay_rupay_86_rawdata(MESSAGE_TYPE ,PRODUCT_ID ,TRANSACTION_TYPE ,FROM_ACCOUNT_TYPE ,TO_ACCOUNT_TYPE ,ACTION_CODE ,RESPONSE_CODE ,PAN_NUMBER ,APPROVAL_NUMBER ,RETRIVAL_REFERENCE ,TRANSACTION_DATE ,TRANSACTION_TIME ,MERCHANT_CATAGORY_CODE ,CARD_ACCEPTOR_ID ,CARD_ACCEPTOR_TERMINAL_ID ,CARD_ACCEPTOR_TERMINAL_LOCATION ,ACQUIRER_ID ,TRANSACTION_CURRENCY_CODE ,TRANSACTION_AMOUNT ,CARD_HOLDER_BILLING_CURRENCY ,CARD_HOLDER_BILLING_AMOUNT ,PAN_ENTRY_MODE ,PAN_ENTRY_CAPABILITY ,PIN_CAPTURE_CODE ,ACQUIRER_COUNTRY_CODE ,ADDITIONAL_AMOUNT ,RUPAY_PRODUCT ,CVD2_MATCH_RESULT ,cvdICVDMatchResult ,RECCURING_PAYMENT_INDICATOR ,ECI_INDICATOR ,ICS1_RESULT_CODE ,FRAUD_SCORE ,EMI_AMOUNT ,ARQC_AUTHORIZATION ,TRANSACTION_ID ,LOYALTY_POINT ,ICS2_RESULT_CODE ,CUST_MOBILE_NUMBER ,IMAGE_CODE ,PERSONAL_PHASE ,UID_NUMBER ,CARD_DATA_INPUT_CAPABILITY ,CARD_HOLDER_AUTH_CAPABILITY ,CARD_CAPTURE_CAPABILITY ,TERMINAL_OPERATING_ENVIROMENT ,CARD_PRESENT_DATA ,CARD_HOLDER_PRESENT_DATA ,CARD_DATA_INPUT_MODE ,CARD_HOLDER_AUTH_MODE ,CARD_HOLDER_AUTH_ENTITY ,CARD_DATA_OP_CAPABILITY ,TERMINAL_DATA_OP_CAPABILITY ,PIN_CAPTURE_CAPABILITY ,ZIP_CODE ,ADVICE_REASON_CODE ,IT_PAN ,INTRAUTHNW ,OTP_INDICATOR ,ICS_TXN_ID ,NW_DATA ,SERVICE_CODE ,CURRENCY_CODE_ACTUAL_TXN_AMT ,ACTUAL_TXN_AMT ,FILEDATE ,FILENAME) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String messageType = "", productID = "", transactionType = "", fromAccountType = "", toAccountType = "";
		String actionCode = "", responseCode = "", panNumber = "", approvalNumber = "", retrievalReference = "";
		String transactionDate = "", transactionTime = "", merchantCategoryCode = "", cardAcceptorID = "";
		String cardAcceptorTerminalID = "", cardAcceptorTerminalLocation = "", acquirerID = "";
		String transactionCurrencyCode = "", transactionAmount = "", cardHolderBillingCurrency = "";
		String cardHolderBillingAmount = "", panEntryMode = "", pinEntryCapability = "", pinCaptureCode = "";
		String acquirerCountryCode = "", additionalAmount = "", ruPayProduct = "", cvd2Matchresult = "";
		String cvdICVDMatchResult = "", recurringPaymentIndicator = "", eciIndicator = "", ics1ResultCode = "";
		String fraudScore = "", emiAmount = "", arqcAuthorization = "", transactionID = "", loyaltyPoint = "";
		String ics2ResultCode = "", custMobileNumber = "", imageCode = "", personalPhase = "", uidNumber = "";
		String cardDatainputCapability = "", cardholderAuthCapability = "", cardCaptureCapability = "";
		String terminaloperatingEnvironment = "", cardholderPresentData = "", cardPresentData = "";
		String cardDataInputMode = "", cardHolderAuthMode = "", cardholderAuthEntity = "", cardDataOPCapability = "";
		String terminalDataOpCapability = "", pinCaptureCapability = "", zipCode = "", adviceReasoncode = "";
		String itPAN = "", intrauthnw = "", otpIndicator = "", icsTxnID = "", nwData = "", serviceCode = "";
		String currencyCodeActualTransactionAmount = "", actualTransactionAmount = "";
		try {
			PreparedStatement ps = con.prepareStatement(insert);
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			int count = 1;
			while ((thisLine = br.readLine()) != null) {
				if (thisLine.startsWith("01")) {
					thisLine.replaceAll("\\s", "");
					messageType = thisLine.substring(0, 2).trim();
					productID = thisLine.substring(2, 5).trim();
					transactionType = thisLine.substring(5, 7).trim();
					fromAccountType = thisLine.substring(7, 9).trim();
					toAccountType = thisLine.substring(9, 11).trim();
					actionCode = thisLine.substring(11, 12).trim();
					responseCode = thisLine.substring(12, 14).trim();
					panNumber = thisLine.substring(14, 33).trim();
					approvalNumber = thisLine.substring(33, 39).trim();
					retrievalReference = thisLine.substring(39, 51).trim();
					transactionDate = thisLine.substring(51, 58).trim();
					transactionTime = thisLine.substring(58, 64).trim();
					merchantCategoryCode = thisLine.substring(64, 68).trim();
					cardAcceptorID = thisLine.substring(68, 83).trim();
					cardAcceptorTerminalID = thisLine.substring(83, 91).trim();
					cardAcceptorTerminalLocation = thisLine.substring(91, 131).trim();
					acquirerID = thisLine.substring(131, 142).trim();
					transactionCurrencyCode = thisLine.substring(142, 145).trim();
					transactionAmount = thisLine.substring(145, 160).trim();
					cardHolderBillingCurrency = thisLine.substring(160, 163).trim();
					cardHolderBillingAmount = thisLine.substring(163, 178).trim();
					panEntryMode = thisLine.substring(178, 180).trim();
					pinEntryCapability = thisLine.substring(180, 181).trim();
					pinCaptureCode = thisLine.substring(181, 183).trim();
					acquirerCountryCode = thisLine.substring(183, 186).trim();
					additionalAmount = thisLine.substring(186, 201).trim();
					ruPayProduct = thisLine.substring(201, 206).trim();
					cvd2Matchresult = thisLine.substring(206, 207).trim();
					cvdICVDMatchResult = thisLine.substring(207, 208).trim();
					recurringPaymentIndicator = thisLine.substring(208, 210).trim();
					eciIndicator = thisLine.substring(210, 212).trim();
					ics1ResultCode = thisLine.substring(212, 214).trim();
					fraudScore = thisLine.substring(214, 219).trim();
					emiAmount = thisLine.substring(219, 245).trim();
					arqcAuthorization = thisLine.substring(245, 246).trim();
					transactionID = thisLine.substring(246, 276).trim();
					loyaltyPoint = thisLine.substring(276, 282).trim();
					ics2ResultCode = thisLine.substring(282, 283).trim();
					custMobileNumber = thisLine.substring(283, 295).trim();
					imageCode = thisLine.substring(295, 300).trim();
					personalPhase = thisLine.substring(300, 305).trim();
					uidNumber = thisLine.substring(305, 317).trim();
					cardDatainputCapability = thisLine.substring(317, 318).trim();
					cardholderAuthCapability = thisLine.substring(318, 319).trim();
					cardCaptureCapability = thisLine.substring(319, 320).trim();
					terminaloperatingEnvironment = thisLine.substring(320, 321).trim();
					cardholderPresentData = thisLine.substring(321, 322).trim();
					cardPresentData = thisLine.substring(322, 323).trim();
					cardDataInputMode = thisLine.substring(323, 324).trim();
					cardHolderAuthMode = thisLine.substring(324, 325).trim();
					cardholderAuthEntity = thisLine.substring(325, 326).trim();
					cardDataOPCapability = thisLine.substring(326, 327).trim();
					terminalDataOpCapability = thisLine.substring(327, 328).trim();
					pinCaptureCapability = thisLine.substring(328, 329).trim();
					zipCode = thisLine.substring(329, 338).trim();
					adviceReasoncode = thisLine.substring(338, 345).trim();
					itPAN = thisLine.substring(345, 355).trim();
					intrauthnw = thisLine.substring(355, 370).trim();
					otpIndicator = thisLine.substring(370, 371).trim();
					icsTxnID = thisLine.substring(371, 386).trim();
					nwData = thisLine.substring(386, 398).trim();
					serviceCode = thisLine.substring(398, 401).trim();
					currencyCodeActualTransactionAmount = thisLine.substring(401, 404).trim();
					actualTransactionAmount = thisLine.substring(404, 419).trim();
					lineNumber++;
					ps.setString(1, messageType);
					ps.setString(2, productID);
					ps.setString(3, transactionType);
					ps.setString(4, fromAccountType);
					ps.setString(5, toAccountType);
					ps.setString(6, actionCode);
					ps.setString(7, responseCode);
					ps.setString(8, panNumber);
					ps.setString(9, approvalNumber);
					ps.setString(10, retrievalReference);
					ps.setString(11, transactionDate);
					ps.setString(12, transactionTime);
					ps.setString(13, merchantCategoryCode);
					ps.setString(14, cardAcceptorID);
					ps.setString(15, cardAcceptorTerminalID);
					ps.setString(16, cardAcceptorTerminalLocation);
					ps.setString(17, acquirerID);
					ps.setString(18, transactionCurrencyCode);
					ps.setString(19, transactionAmount);
					ps.setString(20, cardHolderBillingCurrency);
					ps.setString(21, cardHolderBillingAmount);
					ps.setString(22, panEntryMode);
					ps.setString(23, pinEntryCapability);
					ps.setString(24, pinCaptureCode);
					ps.setString(25, acquirerCountryCode);
					ps.setString(26, additionalAmount);
					ps.setString(27, ruPayProduct);
					ps.setString(28, cvd2Matchresult);
					ps.setString(29, cvdICVDMatchResult);
					ps.setString(30, recurringPaymentIndicator);
					ps.setString(31, eciIndicator);
					ps.setString(32, ics1ResultCode);
					ps.setString(33, fraudScore);
					ps.setString(34, emiAmount);
					ps.setString(35, arqcAuthorization);
					ps.setString(36, transactionID);
					ps.setString(37, loyaltyPoint);
					ps.setString(38, ics2ResultCode);
					ps.setString(39, custMobileNumber);
					ps.setString(40, imageCode);
					ps.setString(41, personalPhase);
					ps.setString(42, uidNumber);
					ps.setString(43, cardDatainputCapability);
					ps.setString(44, cardholderAuthCapability);
					ps.setString(45, cardCaptureCapability);
					ps.setString(46, terminaloperatingEnvironment);
					ps.setString(47, cardholderPresentData);
					ps.setString(48, cardPresentData);
					ps.setString(49, cardDataInputMode);
					ps.setString(50, cardHolderAuthMode);
					ps.setString(51, cardholderAuthEntity);
					ps.setString(52, cardDataOPCapability);
					ps.setString(53, terminalDataOpCapability);
					ps.setString(54, pinCaptureCapability);
					ps.setString(55, zipCode);
					ps.setString(56, adviceReasoncode);
					ps.setString(57, itPAN);
					ps.setString(58, intrauthnw);
					ps.setString(59, otpIndicator);
					ps.setString(60, icsTxnID);
					ps.setString(61, nwData);
					ps.setString(62, serviceCode);
					ps.setString(63, currencyCodeActualTransactionAmount);
					ps.setString(64, actualTransactionAmount);
					ps.setString(65, setupBean.getFileDate());
					ps.setString(66, file.getOriginalFilename());
					ps.addBatch();
					count++;
					messageType = "";
					productID = "";
					transactionType = "";
					fromAccountType = "";
					toAccountType = "";
					actionCode = "";
					responseCode = "";
					panNumber = "";
					approvalNumber = "";
					retrievalReference = "";
					transactionDate = "";
					transactionTime = "";
					merchantCategoryCode = "";
					cardAcceptorID = "";
					cardAcceptorTerminalID = "";
					cardAcceptorTerminalLocation = "";
					acquirerID = "";
					transactionCurrencyCode = "";
					transactionAmount = "";
					cardHolderBillingCurrency = "";
					cardHolderBillingAmount = "";
					panEntryMode = "";
					pinEntryCapability = "";
					pinCaptureCode = "";
					acquirerCountryCode = "";
					additionalAmount = "";
					ruPayProduct = "";
					cvd2Matchresult = "";
					cvdICVDMatchResult = "";
					recurringPaymentIndicator = "";
					eciIndicator = "";
					ics1ResultCode = "";
					fraudScore = "";
					emiAmount = "";
					arqcAuthorization = "";
					transactionID = "";
					loyaltyPoint = "";
					ics2ResultCode = "";
					custMobileNumber = "";
					imageCode = "";
					personalPhase = "";
					uidNumber = "";
					cardDatainputCapability = "";
					cardholderAuthCapability = "";
					cardCaptureCapability = "";
					terminaloperatingEnvironment = "";
					cardholderPresentData = "";
					cardPresentData = "";
					cardDataInputMode = "";
					cardHolderAuthMode = "";
					cardholderAuthEntity = "";
					cardDataOPCapability = "";
					terminalDataOpCapability = "";
					pinCaptureCapability = "";
					zipCode = "";
					adviceReasoncode = "";
					itPAN = "";
					intrauthnw = "";
					otpIndicator = "";
					icsTxnID = "";
					nwData = "";
					serviceCode = "";
					currencyCodeActualTransactionAmount = "";
					actualTransactionAmount = "";
				}
			}
			logger.info("Executed Batch Successfully");
			ps.executeBatch();
			ps.close();
			br.close();
			con.close();
			logger.info("***** ReadRupay.uploadUtkarshRupayDomesticData End ****");
			return true;
		} catch (Exception e) {
			logger.info("Exception at line  " + thisLine);
			logger.info("Exception at line number " + lineNumber);
			logger.info("Error msg " + e.getMessage());
			return false;
		}
	}
}
