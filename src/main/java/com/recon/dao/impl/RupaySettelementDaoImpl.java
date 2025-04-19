package com.recon.dao.impl;

import com.recon.dao.RupaySettelementDao;
import com.recon.model.RupaySettlementBean;
import com.recon.model.RupayUploadBean;
import com.recon.util.GeneralUtil;
import com.recon.util.OracleConn;
import com.recon.util.ReadNCMCDSCRFile;
import com.recon.util.ReadRupayBillingReport;
import com.recon.util.ReadRupayDSCRFile;
import com.recon.util.ReadRupayIntBillingReport;
import com.recon.util.ReadRupayIntDSCRFile;
import com.recon.util.ReadRupayIntInterchangeReport;
import com.recon.util.ReadRupayInterchangeReport;
import com.recon.util.ReadRupaySettlementFile;
import com.recon.util.RupayHeaderUtil;
import com.recon.util.RupayUtilBean;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class RupaySettelementDaoImpl extends JdbcDaoSupport implements RupaySettelementDao {
	private static final String O_ERROR_MESSAGE = "o_error_message";

	@Autowired
	GeneralUtil generalUtil;

	public String uploadRupaySettlementData(List<RupaySettlementBean> list, RupaySettlementBean beanObj) {
		RupaySettlementBean bean = null;
		String result = "";
		int maxSrNo = 0;
		PreparedStatement ps = null;
		Connection con = null;
		ResultSet rs = null;
		String sql = "";
		int[] inserted = null;
		try {
			sql = "insert into RUPAY_SETTLEMENT_DATA values(?,?,?,?,?,?,?,?,?,?,sysdate,?,to_date('"
					+ beanObj.getDatepicker() + "','dd/mm/yyyy'))";
			con = getConnection();
			con.setAutoCommit(false);
			ps = con.prepareStatement(sql);
			for (int i = 0; i < list.size(); i++) {
				bean = list.get(i);
				ps.setString(1, bean.getSettlementDate());
				ps.setString(2, bean.getBankName());
				ps.setString(3, bean.getMemberName());
				ps.setString(4, bean.getMemberBankPid());
				ps.setString(5, bean.getDrcr());
				ps.setString(6, bean.getSumCr());
				ps.setString(7, bean.getSumDr());
				ps.setString(8, bean.getNetSum());
				ps.setInt(9, bean.getCycle());
				ps.setString(10, beanObj.getCreatedBy());
				ps.setInt(11, bean.getSrNo());
				ps.addBatch();
			}
			long start = System.currentTimeMillis();
			System.out.println("  start insert batch" + start);
			inserted = ps.executeBatch();
			System.out.println("  executin batch" + inserted);
			long end = System.currentTimeMillis();
			System.out.println("  end insert batch " + end);
			con.commit();
			result = "success";
		} catch (Exception e) {
			result = "failed";
			e.printStackTrace();
			try {
				con.rollback();
			} catch (Exception exception) {
			}
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (con != null)
					con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	public HashMap<String, Object> validatePrevFileUpload(RupaySettlementBean beanObj) {
		HashMap<String, Object> validate = new HashMap<>();
		try {
			int file_id = ((Integer) getJdbcTemplate().queryForObject(
					"SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?",
					new Object[] { beanObj.getFileName(), beanObj.getCategory(), beanObj.getStSubCategory() },
					Integer.class)).intValue();
			System.out.println("File id is " + file_id);
			String checkTotalCount = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE CATEGORY = ? AND FILE_SUBCATEGORY = ? AND FILEID = ?";
			int totalCount = ((Integer) getJdbcTemplate().queryForObject(checkTotalCount,
					new Object[] { beanObj.getCategory(), beanObj.getStSubCategory(), Integer.valueOf(file_id) },
					Integer.class)).intValue();
			if (totalCount > 0) {
				String checkForSameDate = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND CYCLE =? AND FILEDATE =  STR_TO_DATE(?,'%d%b%Y')";
				int dataCount = ((Integer) getJdbcTemplate().queryForObject(checkForSameDate, new Object[] {
						Integer.valueOf(file_id), Integer.valueOf(beanObj.getCycle()), beanObj.getDatepicker() },
						Integer.class)).intValue();
				if (dataCount > 0) {
					validate.put("result", Boolean.valueOf(true));
					validate.put("msg", "File for selected date is already uploaded !!!");
				} else {
					validate.put("result", Boolean.valueOf(false));
				}
			} else {
				System.out.println("Its first time upload");
				validate.put("result", Boolean.valueOf(false));
			}
		} catch (Exception e) {
			System.out.println("Exception in RupaySettlementServiceImpl: validatePrevFileUpload " + e);
			validate.put("result", Boolean.valueOf(true));
			validate.put("msg", "Exception Occured!!");
		}
		return validate;
	}

	public HashMap<String, Object> updateFileSettlement(RupaySettlementBean beanObj, int count) {
		HashMap<String, Object> mapObj = new HashMap<>();
		int file_id = ((Integer) getJdbcTemplate().queryForObject(
				"SELECT FILEID FROM  main_filesource WHERE upper(FILENAME)=? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?",
				new Object[] { beanObj.getFileName(), beanObj.getCategory(), beanObj.getStSubCategory() },
				Integer.class)).intValue();
		System.out.println("File id is " + file_id);
		String insertData = "INSERT INTO MAIN_SETTLEMENT_FILE_UPLOAD(FILEID, FILEDATE, UPLOADBY, UPLOADDATE, CATEGORY, UPLOAD_FLAG, FILE_SUBCATEGORY,CYCLE,PROCESS_FLAG,FILE_COUNT) VALUES('"
				+ file_id + "',STR_TO_DATE('" + beanObj.getDatepicker() + "','%d%b%Y'),'" + beanObj.getCreatedBy()
				+ "',sysdate,'" + beanObj.getCategory() + "','Y','" + beanObj.getStSubCategory() + "'," + "'"
				+ beanObj.getCycle() + "','N',1)";
		System.out.println("insertData======");
		System.out.println(insertData);
		getJdbcTemplate().execute(insertData);
		mapObj.put("entry", Boolean.valueOf(true));
		return mapObj;
	}

	public HashMap<String, List<RupaySettlementBean>> getTTUMData(String settlementDate) {
		RupaySettlementBean bean = null;
		HashMap<String, List<RupaySettlementBean>> map = new HashMap<>();
		List<RupaySettlementBean> datListWithoutTotal = new ArrayList<>();
		List<RupaySettlementBean> datListWithTotal = new ArrayList<>();
		try {
			String sql = "SELECT a.BANK_PID,a.ACCOUNT_NO,b.MEMBER_BANK_PID,b.NET_SUM,b.DRCR,b.SETTLEMENT_DATE,b.CYCLE FROM MASTER_DATA_RUPAY a inner join RUPAY_SETTLEMENT_DATA b  on a.BANK_PID=b.MEMBER_BANK_PID where b.MEMBER_BANK_PID !='TOTAL' and b.SETTLEMENT_DATE='"
					+

					settlementDate + "' order by b.SR_NO asc";
			datListWithoutTotal = getJdbcTemplate().query(sql,
					(RowMapper) new BeanPropertyRowMapper(RupaySettlementBean.class));
			String sqlTotal = "SELECT a.BANK_PID,a.ACCOUNT_NO,b.MEMBER_BANK_PID,b.NET_SUM,b.DRCR,b.SETTLEMENT_DATE,b.CYCLE FROM MASTER_DATA_RUPAY a inner join RUPAY_SETTLEMENT_DATA b  on a.BANK_PID=b.MEMBER_BANK_PID where b.MEMBER_BANK_PID ='TOTAL' and b.SETTLEMENT_DATE='"
					+

					settlementDate + "' order by b.CYCLE asc";
			datListWithTotal = getJdbcTemplate().query(sqlTotal,
					(RowMapper) new BeanPropertyRowMapper(RupaySettlementBean.class));
			map.put("datListWithoutTotal", datListWithoutTotal);
			map.put("datListWithTotal", datListWithTotal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public boolean readRupayChargeback(RupayUploadBean beanObj, MultipartFile file) {
		this.logger.info("***** ReadRupay.uploadDomesticData Start ****");
		String insert = "INSERT  INTO RUPAY_CHARGEBACK_RAWDATA (MTI,Function_Code ,Record_Number,Member_Institution_ID_Code,Unique_File_Name,Date_Settlement,Product_Code,Settlement_BIN,File_Category,Version_Number,Entire_File_Reject_Indicator,File_Reject_Reason_Code,Transactions_Count,Run_Total_Amount,Acquirer_Institution_ID_code,Amount_Settlement,Amount_Transaction,Approval_Code,Acquirer_Reference_Data,Case_Number,Currency_Code_Settlement,Currency_Code_Transaction,Conversion_Rate_Settlement,Card_Acceptor_Addi_Addr,Card_Acceptor_Terminal_ID,Card_Acceptor_Zip_Code,DateandTime_Local_Transaction,TXNFunction_Code ,Late_Presentment_Indicator,TXNMTI,Primary_Account_Number,TXNRecord_Number,RGCS_Received_date,Settlement_DR_CR_Indicator,Txn_Desti_Insti_ID_code,Txn_Origin_Insti_ID_code,Card_Holder_UID,Amount_Billing,Currency_Code_Billing,Conversion_Rate_billing,Message_Reason_Code,Fee_DR_CR_Indicator1,Fee_amount1,Fee_Currency1,Fee_Type_Code1,Interchange_Category1,Fee_DR_CR_Indicator2,Fee_amount2,Fee_Currency2,Fee_Type_Code2,Interchange_Category2,Fee_DR_CR_Indicator3,Fee_amount3,Fee_Currency3,Fee_Type_Code3,Interchange_Category3,Fee_DR_CR_Indicator4,Fee_amount4,Fee_Currency4,Fee_Type_Code4,Interchange_Category4,Fee_DR_CR_Indicator5,Fee_amount5,Fee_Currency5,Fee_Type_Code5,Interchange_Category5,Trl_FUNCTION_CODE,Trl_RECORD_NUMBER,flag,FILEDATE,PAN,CREATEDDATE,createdby,CYCLE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'),?,SYSDATE,?,?)";
		String update = "update RUPAY_CHARGEBACK_RAWDATA  set Trl_FUNCTION_CODE = ? , TRL_RECORD_NUMBER= ?,TRANSACTIONS_COUNT=? where to_char(CREATEDDATE,'dd-mm-yy')=to_char(sysdate,'dd-mm-yy')";
		String trl_nFunCd = null, trl_nRecNum = null, transactions_count = null;
		int feesize = 1;
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(insert);
			PreparedStatement updtps = con.prepareStatement(update);
			Pattern TAG_REGEX = Pattern.compile(">(.+?)</");
			Pattern node_REGEX = Pattern.compile("<(.+?)>");
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			String thisLine = null;
			int count = 1;
			String hdr = "", trl = "";
			RupayUtilBean utilBean = new RupayUtilBean();
			RupayHeaderUtil headerUtil = new RupayHeaderUtil();
			this.logger.info("Process started" + (new Date()).getTime());
			while ((thisLine = br.readLine()) != null) {
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
						System.out.println(matcher.group(1));
						System.out.println("count ::> " + count);
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
							this.logger.info(trl_nFunCd);
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
							this.logger.info(trl_nRecNum);
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
							this.logger.info("setnFeeDCInd2");
							utilBean.setnFeeDCInd2(matcher.group(1));
							continue;
						case 3:
							this.logger.info("setnFeeDCInd3");
							utilBean.setnFeeDCInd3(matcher.group(1));
							continue;
						case 4:
							this.logger.info("setnFeeDCInd4");
							utilBean.setnFeeDCInd4(matcher.group(1));
							continue;
						case 5:
							this.logger.info("setnFeeDCInd5");
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
							this.logger.info("setnFeeAmt2");
							utilBean.setnFeeAmt2(matcher.group(1));
							continue;
						case 3:
							this.logger.info("setnFeeAmt3");
							utilBean.setnFeeAmt3(matcher.group(1));
							continue;
						case 4:
							this.logger.info("setnFeeAmt4");
							utilBean.setnFeeAmt4(matcher.group(1));
							continue;
						case 5:
							this.logger.info("setnFeeAmt5");
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
							this.logger.info("nFeeCcy2");
							utilBean.setnFeeCcy2(matcher.group(1));
							continue;
						case 3:
							this.logger.info("nFeeCcy3");
							utilBean.setnFeeCcy3(matcher.group(1));
							continue;
						case 4:
							this.logger.info("nFeeCcy4");
							utilBean.setnFeeCcy4(matcher.group(1));
							continue;
						case 5:
							this.logger.info("nFeeCcy5");
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
						this.logger.info(transactions_count);
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
						ps.setString(70, beanObj.getFileDate());
						ps.setString(71, pan);
						ps.setString(72, beanObj.getCreatedBy());
						ps.setString(73, beanObj.getCycle());
						ps.addBatch();
						utilBean = new RupayUtilBean();
						count++;
						if (count == 10000) {
							count = 1;
							ps.executeBatch();
							this.logger.info("Executed batch");
							count++;
						}
					}
				}
			}
			ps.executeBatch();
			updtps.setString(1, trl_nFunCd);
			this.logger.info(trl_nFunCd);
			updtps.setString(2, trl_nRecNum);
			this.logger.info(trl_nRecNum);
			updtps.setString(3, transactions_count);
			this.logger.info(transactions_count);
			this.logger.info(update);
			updtps.executeUpdate();
			this.logger.info("Process ended" + (new Date()).getTime());
			br.close();
			ps.close();
			updtps.close();
			con.close();
			this.logger.info("***** ReadRupay.uploadDomesticData End ****");
			return true;
		} catch (Exception ex) {
			this.logger.error(" error in ReadRupay.uploadDomesticData ",
					new Exception(" ReadRupay.uploadDomesticData ", ex));
			return false;
		}
	}

	public HashMap<String, Object> readRupayFiles(RupayUploadBean beanObj, MultipartFile file) {
		HashMap<String, Object> output = null;
		try {
			if (beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("DSCR")) {
				ReadRupayDSCRFile fileRead = new ReadRupayDSCRFile();
				output = fileRead.fileupload(beanObj, file, getConnection());
			} else if (beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("INTERCHANGE")) {
				ReadRupayInterchangeReport fileRead = new ReadRupayInterchangeReport();
				output = fileRead.fileupload(beanObj, file, getConnection());
			} else if (beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("SETTLEMENT")) {
				ReadRupaySettlementFile fileRead = new ReadRupaySettlementFile();
				output = fileRead.fileupload(beanObj, file, getConnection());
			} else {
				this.logger.info("Interchange File name");
				ReadRupayBillingReport fileRead = new ReadRupayBillingReport();
				output = fileRead.fileupload(beanObj, file, getConnection());
				fileRead.PBGBfileupload(beanObj, file, getConnection());
			}
			return output;
		} catch (Exception e) {
			System.out.println("EXception in readDSCR file " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("count", Integer.valueOf(0));
			return output;
		}
	}

	public HashMap<String, Object> readRupayIntFiles(RupayUploadBean beanObj, MultipartFile file) {
		HashMap<String, Object> output = null;
		try {
			if (beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("DSCR")) {
				ReadRupayIntDSCRFile fileRead = new ReadRupayIntDSCRFile();
				output = fileRead.fileupload(beanObj, getConnection(), file);
			} else if (beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("INTERCHANGE")) {
				ReadRupayIntInterchangeReport fileRead = new ReadRupayIntInterchangeReport();
				output = fileRead.fileupload(beanObj, file, getConnection());
			} else {
				this.logger.info("Interchange File name");
				ReadRupayIntBillingReport fileRead = new ReadRupayIntBillingReport();
				output = fileRead.fileupload(beanObj, file, getConnection());
			}
			return output;
		} catch (Exception e) {
			System.out.println("EXception in readDSCR file " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("count", Integer.valueOf(0));
			return output;
		}
	}

	public boolean checkFileUploaded(RupayUploadBean beanObj) {
		String tableName = null;
		try {
			if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC") && beanObj.getFileName().equals("DSCR")) {
				tableName = "rupay_dscr_rawdata";
			} else if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")
					&& beanObj.getFileName().equals("BILLING")) {
				tableName = "rupay_billing_rawdata";
			} else if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")
					&& beanObj.getFileName().equals("SETTLEMENT")) {
				tableName = "rupay_settlement_rawdata";
			} else if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")
					&& beanObj.getFileName().equals("INTERCHANGE")) {
				tableName = "rupay_interchange_rawdata";
			} else if (beanObj.getSubcategory().equalsIgnoreCase("INTERNATIONAL")
					&& beanObj.getFileName().equals("DSCR")) {
				tableName = "irgcs_rupay_int_dsr_rawdata";
			}
			int recordCount = ((Integer) getJdbcTemplate()
					.queryForObject(
							"SELECT COUNT(*) FROM " + tableName + " WHERE FILEDATE= STR_to_date('"
									+ beanObj.getFileDate() + "','%Y/%m/%d') AND CYCLE = '" + beanObj.getCycle() + "' ",
							new Object[0], Integer.class)).intValue();
			System.out.println(" countr " + recordCount);
			if (recordCount > 0)
				return true;
			return false;
		} catch (Exception e) {
			this.logger.info("Exception in checkFileUploaded " + e);
			return false;
		}
	}

	public HashMap<String, Object> validateRawfiles(RupayUploadBean beanObj) {
		return null;
	}

	public HashMap<String, Object> validateSettlementFiles(RupayUploadBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		try {
			int checkBilling = 0, checkInterchange = 0, checkDSCR = 0, checkNCMCDSCR = 0;
			if (beanObj.getSubcategory().equalsIgnoreCase("Domestic")) {
				checkBilling = ((Integer) getJdbcTemplate().queryForObject(
						"select count(*) from rupay_billing_rawdata where filedate = ? and cycle = ?",
						new Object[] { beanObj.getFileDate(), beanObj.getCycle() }, Integer.class)).intValue();
				checkDSCR = ((Integer) getJdbcTemplate().queryForObject(
						"select count(*) from rupay_dscr_rawdata where filedate = ? and cycle = ?",
						new Object[] { beanObj.getFileDate(), beanObj.getCycle() }, Integer.class)).intValue();
				checkNCMCDSCR = ((Integer) getJdbcTemplate().queryForObject(
						"select count(*) from ncmc_dscr_rawdata where filedate = ? and cycle = ?",
						new Object[] { beanObj.getFileDate(), beanObj.getCycle() }, Integer.class)).intValue();
			} else {
				checkBilling = ((Integer) getJdbcTemplate().queryForObject(
						"select count(*) from rupay_int_billing_rawdata where filedate = ? and cycle = ?",
						new Object[] { beanObj.getFileDate(), beanObj.getCycle() }, Integer.class)).intValue();
				checkDSCR = ((Integer) getJdbcTemplate().queryForObject(
						"select count(*) from rupay_int_dscr_rawdata where filedate = ? and cycle = ?",
						new Object[] { beanObj.getFileDate(), beanObj.getCycle() }, Integer.class)).intValue();
				checkNCMCDSCR = 1;
			}
			if (checkBilling == 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "NPCI Billing file is not uploaded for selected Date and cycle");
			} else if (checkDSCR == 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "Rupay DSCR file is not uploaded for selected Date and cycle");
			} else if (checkNCMCDSCR == 0) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "NCMC DSCR file is not uploaded for selected Date and cycle");
			} else {
				output.put("result", Boolean.valueOf(true));
			}
		} catch (Exception e) {
			this.logger.info("Exception while validating chargeback and rest 4 files");
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception while validating Chargeback and rest 4 files");
		}
		return output;
	}

	public boolean processSettlement(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			RupaySettlementProc rollBackexe = new RupaySettlementProc(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			inParams.put("Entered_cycle", beanObj.getCycle());
			outParams = rollBackexe.execute(inParams);
			this.logger.info("rollBackexe " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private class RupaySettlementProc extends StoredProcedure {
		private static final String insert_proc = "RUPAY_POS_SETTLEMENT";

		public RupaySettlementProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("Entered_cycle", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	public boolean processSettlement2(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			RupaySettlementProc2 rollBackexe = new RupaySettlementProc2(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			inParams.put("Entered_cycle", beanObj.getCycle());
			outParams = rollBackexe.execute(inParams);
			this.logger.info("rollBackexe " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private class RupaySettlementProc2 extends StoredProcedure {
		private static final String insert_proc = "RUPAY_POS_SETTLEMENT_TEST";

		public RupaySettlementProc2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
		 	declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("Entered_cycle", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean processSettlementRRB(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			RupaySettlementProcRRB rollBackexe = new RupaySettlementProcRRB(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			inParams.put("Entered_cycle", beanObj.getCycle());
			outParams = rollBackexe.execute(inParams);
			this.logger.info("rollBackexe " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private class RupaySettlementProcRRB extends StoredProcedure {
		private static final String insert_proc = "RUPAY_RRB_SETTLEMENT";

		public RupaySettlementProcRRB(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("Entered_cycle", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> processSettlementVisa(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		try {
			VisaSettlementProc rollBackexe = new VisaSettlementProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", "INT12016");
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.get("o_error_message"));
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			this.logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class VisaSettlementProc extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_ISS_SETTLEMENT_PROC";

		public VisaSettlementProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> processSettlementVisaACQ(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		try {
			processSettlementVisaACQ rollBackexe = new processSettlementVisaACQ(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", "INT12016");
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.get("o_error_message"));
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			this.logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class processSettlementVisaACQ extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_ACQ_SETTLEMENT_PROC";

		public processSettlementVisaACQ(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> processSettlementVisaINT(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		try {
			VisaSettlementProcINT rollBackexe = new VisaSettlementProcINT(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", "INT12016");
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.get("o_error_message"));
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			this.logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class VisaSettlementProcINT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ISS_SETTLEMENT_PROC";

		public VisaSettlementProcINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> processSettlementVisaINTACQ(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		try {
			processSettlementVisaINTACQ rollBackexe = new processSettlementVisaINTACQ(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", "INT12016");
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.get("o_error_message"));
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			this.logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class processSettlementVisaINTACQ extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ACQ_SETTLEMENT_PROC";

		public processSettlementVisaINTACQ(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> processSettlementVisaRollback(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		try {
			processSettlementVisaRollback rollBackexe = new processSettlementVisaRollback(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", "INT12016");
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.get("o_error_message"));
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			this.logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class processSettlementVisaRollback extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_ISS_SETTLEMENT_PROC_ROLLBACK";

		public processSettlementVisaRollback(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> processSettlementVisaRollbackACQ(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		try {
			processSettlementVisaRollbackACQ rollBackexe = new processSettlementVisaRollbackACQ(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", "INT12016");
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.get("o_error_message"));
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			this.logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class processSettlementVisaRollbackACQ extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_ACQ_SETTLEMENT_PROC_ROLLBACK";

		public processSettlementVisaRollbackACQ(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> processSettlementVisaRollbackINT(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		try {
			processSettlementVisaRollbackINT rollBackexe = new processSettlementVisaRollbackINT(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", "INT12016");
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.get("o_error_message"));
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			this.logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class processSettlementVisaRollbackINT extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ISS_SETTLEMENT_PROC_ROLLBACK";

		public processSettlementVisaRollbackINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> rollbackUpdateDollarRate(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		try {
			rollbackUpdateDollarRate rollBackexe = new rollbackUpdateDollarRate(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.get("o_error_message"));
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			this.logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class rollbackUpdateDollarRate extends StoredProcedure {
		private static final String insert_proc = "US_DOLLAR_RATE_ROLLBACK";

		public rollbackUpdateDollarRate(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> processSettlementVisaRollbackINTACQ(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		HashMap<String, Object> output = new HashMap<>();
		try {
			processSettlementVisaRollbackINTACQ rollBackexe = new processSettlementVisaRollbackINTACQ(
					getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", "INT12016");
			outParams = rollBackexe.execute(inParams);
			System.out.println("outParams " + outParams.get("o_error_message"));
			System.out.println("outParams " + outParams.toString());
			output.put("result", Boolean.valueOf(true));
			output.put("msg", outParams.get("o_error_message"));
		} catch (Exception e) {
			this.logger.info("Exception in runTTUMProcess " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", e.toString());
			return output;
		}
		return output;
	}

	private class processSettlementVisaRollbackINTACQ extends StoredProcedure {
		private static final String insert_proc = "VISA_INT_ACQ_SETTLEMENT_PROC_ROLLBACK";

		public processSettlementVisaRollbackINTACQ(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean processSettlementVisa2(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			VisaSettlementProc2 rollBackexe = new VisaSettlementProc2(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			inParams.put("Entered_cycle", beanObj.getCycle());
			outParams = rollBackexe.execute(inParams);
			this.logger.info("rollBackexe " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private class VisaSettlementProc2 extends StoredProcedure {
		private static final String insert_proc = "VISA_DOM_SETTLEMENT_TTUM_PROC";

		public VisaSettlementProc2(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("Entered_cycle", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean processSettlementINT(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			RupaySettlementProcINT rollBackexe = new RupaySettlementProcINT(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			this.logger.info("rollBackexe " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private class RupaySettlementProcINT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_POS_SETTLEMENT_PNB";

		public RupaySettlementProcINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			// declareParameter(new SqlParameter("CATEGORY", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	public boolean processSettlementRRBINT(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			processSettlementRRBINT rollBackexe = new processSettlementRRBINT(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			this.logger.info("rollBackexe " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private class processSettlementRRBINT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_INT_POS_RRB_SETTLEMENT_PNB";

		public processSettlementRRBINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));

			// declareParameter(new SqlParameter("CATEGORY", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	public boolean processSettlementVISAINT(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			RupaySettlementProcVISAINT rollBackexe = new RupaySettlementProcVISAINT(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			inParams.put("USER_ID", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			this.logger.info("rollBackexe " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private class RupaySettlementProcVISAINT extends StoredProcedure {
		private static final String insert_proc = "RUPAY_POS_SETTLEMENT";

		public RupaySettlementProcVISAINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean processSettlementQsparc(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			RupaySettlementProcQsparc rollBackexe = new RupaySettlementProcQsparc(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			inParams.put("Entered_cycle", Integer.valueOf(beanObj.getCycle()));
			outParams = rollBackexe.execute(inParams);
			this.logger.info("rollBackexe " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private class RupaySettlementProcQsparc extends StoredProcedure {
		private static final String insert_proc = "QSPARC_POS_SETTLEMENT_PNB";

		public RupaySettlementProcQsparc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlParameter("Entered_cycle", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean processSettlementQsparcINT(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			RupaySettlementProcQsparcINT rollBackexe = new RupaySettlementProcQsparcINT(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			this.logger.info("rollBackexe " + outParams.toString());
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private class RupaySettlementProcQsparcINT extends StoredProcedure {
		private static final String insert_proc = "QSPARC_INT_POS_SETTLEMENT";

		public RupaySettlementProcQsparcINT(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE", Types.VARCHAR));
			// declareParameter(new SqlParameter("CATEGORY", Types.VARCHAR));

			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean validateSettlementProcess(RupayUploadBean beanObj) {
		try {
			String checkProcess = "select count(*) from  rupay_pos_dom_settlement where  FILEDATE =str_to_date(?,'%Y/%m/%d') and cycle=?";
			int processCount = ((Integer) getJdbcTemplate().queryForObject(checkProcess,
					new Object[] { beanObj.getFileDate(), beanObj.getCycle() }, Integer.class)).intValue();
			this.logger.info("query " + checkProcess + " count " + processCount + " filedate " + beanObj.getFileDate()
					+ " cycle " + beanObj.getCycle());
			if (processCount > 0)
				return true;
			return false;
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementProcess " + e);
			return false;
		}
	}
	public boolean validateSettlementProcess2(RupayUploadBean beanObj) {
		try {
			String checkProcess = "select count(*) from  rupay_pos_dom_settlement where  FILEDATE =str_to_date(?,'%Y/%m/%d')";
			int processCount = ((Integer) getJdbcTemplate().queryForObject(checkProcess,
					new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
			this.logger.info("query " + checkProcess + " count " + processCount + " filedate " + beanObj.getFileDate()
					+ " cycle " + beanObj.getCycle());
			if (processCount > 0)
				return true;
			return false;
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementProcess " + e);
			return false;
		}
	}
	public boolean validateSettlementProcessRRB(RupayUploadBean beanObj) {
		try {
			String checkProcess = "select count(*) from  rupay_dom_rrb_settlement where  FILEDATE =str_to_date(?,'%Y/%m/%d') and cycle=?";
			int processCount = ((Integer) getJdbcTemplate().queryForObject(checkProcess,
					new Object[] { beanObj.getFileDate(), beanObj.getCycle() }, Integer.class)).intValue();
			this.logger.info("query " + checkProcess + " count " + processCount + " filedate " + beanObj.getFileDate()
					+ " cycle " + beanObj.getCycle());
			if (processCount > 0)
				return true;
			return false;
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementProcess " + e);
			return false;
		}
	}

	public boolean validateSettlementProcessVisa(RupayUploadBean beanObj) {
		try {
			String checkProcess = "";
			if (beanObj.getFileType().equalsIgnoreCase("ACQUIRER")) {
				checkProcess = "select count(*) from visa_dom_acq_settlement_report where FILEDATE = str_to_date(?,'%Y/%m/%d') ";
			} else {
				checkProcess = "select count(*) from visa_dom_iss_settlement_report where  FILEDATE = str_to_date(?,'%Y/%m/%d') ";
			}
			int processCount = ((Integer) getJdbcTemplate().queryForObject(checkProcess,
					new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
			this.logger.info("query " + checkProcess + " count " + processCount);
			if (processCount == 0)
				return true;
			return false;
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementProcess " + e);
			return false;
		}
	}

	public boolean InsertDollarRateVisa(RupayUploadBean beanObj) {
		try {
			String checkProcess = "";
			String update_query = "INSERT INTO us_dollar_rate (FILEDATE,DOLLAR_RATE) VALUES (STR_TO_DATE(?,'%Y/%m/%d'),?)";
			OracleConn oraclecon = new OracleConn();
			Connection con = oraclecon.getconn();
			PreparedStatement ps = con.prepareStatement(update_query);
			ps.setString(1, beanObj.getFileDate());
			ps.setString(2, beanObj.getFileType());
			ps.execute();
			con.close();
			System.out.println("Success");
			return true;
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementProcess " + e);
			return false;
		}
	}

	public boolean validateSettlementProcessVisa2(RupayUploadBean beanObj) {
		try {
			String checkProcess = "select count(*) from VISA_SETTLEMENT_TTUM where  TO_DATE(FILEDATE,'DD-MM-YY')  = TO_DATE(? ,'DD-MM-YY')";
			int processCount = ((Integer) getJdbcTemplate().queryForObject(checkProcess,
					new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
			this.logger.info("query " + checkProcess + " count " + processCount);
			if (processCount == 0)
				return true;
			return false;
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementProcess " + e);
			return false;
		}
	}

	public boolean validateSettlementProcessINT(RupayUploadBean beanObj) {
		try {
			String checkProcess = "select count(*) from  rupay_pos_int_settlement where  FILEDATE  =str_to_date(?,'%Y/%m/%d')";
			int processCount = ((Integer) getJdbcTemplate().queryForObject(checkProcess,
					new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
			this.logger.info("query " + checkProcess + " count " + processCount);
			if (processCount > 0)
				return true;
			return false;
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementProcess " + e);
			return false;
		}
	}

	public boolean validateSettlementProcessRRBINT(RupayUploadBean beanObj) {
		try {
			String checkProcess = "select count(*) from  rupay_pos_int_settlement where  FILEDATE  =str_to_date(?,'%Y/%m/%d')";
			int processCount = ((Integer) getJdbcTemplate().queryForObject(checkProcess,
					new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
			this.logger.info("query " + checkProcess + " count " + processCount);
			if (processCount > 0)
				return true;
			return false;
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementProcess " + e);
			return false;
		}
	}

	public boolean validateSettlementProcessVISAINT(RupayUploadBean beanObj) {
		try {
			String checkProcess = "";
			if (beanObj.getFileType().equalsIgnoreCase("ACQUIRER")) {
				checkProcess = "select count(*) from visa_int_acq_settlement_report  where  FILEDATE =str_to_date(?,'%Y/%m/%d') ";
			} else {
				checkProcess = "select count(*) from visa_int_iss_settlement_report  where  FILEDATE  =str_to_date(?,'%Y/%m/%d') ";
			}
			int processCount = ((Integer) getJdbcTemplate().queryForObject(checkProcess,
					new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
			this.logger.info("query " + checkProcess + " count " + processCount);
			if (processCount == 0)
				return true;
			return false;
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementProcess " + e);
			return false;
		}
	}

	public boolean validateSettlementProcessQsparc(RupayUploadBean beanObj) {
		try {
			String checkProcess = "select count(*) from  qsparc_pos_dom_settlement where FILEDATE = STR_to_date(?,'%Y/%m/%d') and cycle=?";
			int processCount = ((Integer) getJdbcTemplate().queryForObject(checkProcess,
					new Object[] { beanObj.getFileDate(), beanObj.getCycle() }, Integer.class)).intValue();
			this.logger.info("query " + checkProcess + " count " + processCount);
			if (processCount > 0)
				return true;
			return false;
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementProcess " + e);
			return false;
		}
	}

	public boolean validateSettlementProcessQsparcINT(RupayUploadBean beanObj) {
		try {
			String checkProcess = "select count(*) from  qsparc_pos_int_settlement  where  FILEDATE  = STR_to_date(?,'%Y/%m/%d')";
			int processCount = ((Integer) getJdbcTemplate().queryForObject(checkProcess,
					new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
			this.logger.info("query " + checkProcess + " count " + processCount);
			if (processCount > 0)
				return true;
			return false;
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementProcess " + e);
			return false;
		}
	}

	public List<Object> getSettlementData(RupayUploadBean beanObj) {
		List<Object> data = new ArrayList();
		final List<String> cols = new ArrayList<>();
		cols.add("GL_CODE");
		cols.add("PARTICULARS");
		cols.add("DB_COUNT");
		cols.add("DEBIT");
		cols.add("CR_COUNT");
		cols.add("CREDIT");
		cols.add("NARRATION");
		String getData="";
		if( beanObj.getCycle().equalsIgnoreCase("5")) {
			
		getData = "select GL_CODE, PARTICULARS, DB_COUNT, DEBIT, CR_COUNT, CREDIT, NARRATION from rupay_pos_dom_settlement where filedate =STR_TO_DATE(?,'%Y/%m/%d') ";
			
		}else {
			
		getData = "select GL_CODE, PARTICULARS, DB_COUNT, DEBIT, CR_COUNT, CREDIT, NARRATION from rupay_pos_dom_settlement where filedate =STR_TO_DATE(?,'%Y/%m/%d')and cycle='"+ beanObj.getCycle()+"' ";
			
		}
		
	List<Object> settlementData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
				new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							Map<String, String> table_Data = new HashMap<String, String>();
							for (String column : cols) {
								table_Data.put(column, rs.getString(column));
							}
							beanList.add(table_Data);
						}
						return beanList;
					}
				});

		data.add(cols);
		data.add(settlementData);
		return data;
	}

	public List<Object> getSettlementDataRRB(RupayUploadBean beanObj) {
		List<Object> data = new ArrayList();
		final List<String> cols = new ArrayList<>();
		cols.add("GL_CODE");
		cols.add("PARTICULARS");
		cols.add("DB_COUNT");
		cols.add("DEBIT");
		cols.add("CR_COUNT");
		cols.add("CREDIT");
		cols.add("NARRATION");
		String getData = "select GL_CODE, PARTICULARS, DB_COUNT, DEBIT, CR_COUNT, CREDIT, NARRATION from rupay_dom_rrb_settlement where filedate =STR_TO_DATE(?,'%Y/%m/%d')and cycle=? ";
		List<Object> settlementData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() , beanObj.getCycle()},
				new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							Map<String, String> table_Data = new HashMap<String, String>();
							for (String column : cols) {
								table_Data.put(column, rs.getString(column));
							}
							beanList.add(table_Data);
						}
						return beanList;
					}
				});

		data.add(cols);
		data.add(settlementData);
		return data;
	}

	public List<Object> getSettlementDataVisa(RupayUploadBean beanObj) {
		List<Object> data = new ArrayList();
		final List<String> cols = new ArrayList<>();
		cols.add("BUSINESS_DATE");
		cols.add("DR_CR");
		cols.add("ACCOUNT");
		cols.add("PARTICULAR");
		cols.add("NARRATION");
		cols.add("DR_CNT");
		cols.add("DR_AMOUNT");
		cols.add("CR_CNT");
		cols.add("CR_AMOUNT");
		cols.add("FILEDATE");
		String getData = "";
		if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
			getData = "SELECT BUSINESS_DATE, DR_CR, ACCOUNT, PARTICULAR, NARRATION, DR_CNT, DR_AMOUNT, CR_CNT, CR_AMOUNT, FILEDATE\r\nFROM VISA_DOM_SETTLEMENT_REPORT WHERE TO_DATE(FILEDATE ,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')";
		} else {
			getData = "SELECT BUSINESS_DATE, DR_CR, ACCOUNT, PARTICULAR, NARRATION, DR_CNT, DR_AMOUNT, CR_CNT, CR_AMOUNT, FILEDATE\r\nFROM VISA_INT_SETTLEMENT_REPORT WHERE TO_DATE(FILEDATE ,'DD-MM-YY') = TO_DATE(?,'DD-MM-YY')";
		}
		List<Object> settlementData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
				new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							Map<String, String> table_Data = new HashMap<String, String>();
							for (String column : cols) {
								table_Data.put(column, rs.getString(column));
							}
							beanList.add(table_Data);
						}
						return beanList;
					}
				});

		data.add(cols);
		data.add(settlementData);
		return data;
	}

	public List<Object> getSettlementDataINT(RupayUploadBean beanObj) {
		List<Object> data = new ArrayList();
		final List<String> cols = new ArrayList<>();
		cols.add("GL_CODE");
		cols.add("PARTICULARS");
		cols.add("DB_COUNT");
		cols.add("DEBIT");
		cols.add("CR_COUNT");
		cols.add("CREDIT");
		cols.add("NARRATION");
		String getData = "SELECT GL_CODE, PARTICULARS, DB_COUNT, DEBIT, CR_COUNT, CREDIT, NARRATION FROM   rupay_pos_int_settlement where filedate= STR_TO_DATE(?,'%Y/%m/%d')";
		List<Object> settlementData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
				new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							Map<String, String> table_Data = new HashMap<String, String>();
							for (String column : cols) {
								table_Data.put(column, rs.getString(column));
							}
							beanList.add(table_Data);
						}
						return beanList;
					}
				});

		data.add(cols);
		data.add(settlementData);
		return data;
	}

	public List<Object> getSettlementDataQsparc(RupayUploadBean beanObj) {
		List<Object> data = new ArrayList();
		final List<String> cols = new ArrayList<>();
		cols.add("GL_CODE");
		cols.add("PARTICULARS");
		cols.add("DB_COUNT");
		cols.add("DEBIT");
		cols.add("CR_COUNT");
		cols.add("CREDIT");
		cols.add("NARRATION");
		String getData = "SELECT GL_CODE, PARTICULARS, DB_COUNT, DEBIT, CR_COUNT, CREDIT, NARRATION FROM qsparc_pos_dom_settlement where filedate= STR_to_date(?,'%Y/%m/%d') and cycle=? ";
		List<Object> settlementData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate(),  beanObj.getCycle() },
				new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							Map<String, String> table_Data = new HashMap<String, String>();
							for (String column : cols) {
								table_Data.put(column, rs.getString(column));
							}
							beanList.add(table_Data);
						}
						return beanList;
					}
				});

		data.add(cols);
		data.add(settlementData);
		return data;
	}

	public List<Object> getSettlementDataQsparcINT(RupayUploadBean beanObj) {
		List<Object> data = new ArrayList();
		final List<String> cols = new ArrayList<>();
		cols.add("GL_CODE");
		cols.add("PARTICULARS");
		cols.add("DB_COUNT");
		cols.add("DEBIT");
		cols.add("CR_COUNT");
		cols.add("CREDIT");
		cols.add("NARRATION");
		String getData = "SELECT GL_CODE, PARTICULARS, DB_COUNT, DEBIT, CR_COUNT, CREDIT, NARRATION FROM  qsparc_pos_int_settlement WHERE filedate = STR_to_date(?,'%Y/%m/%d')";
		List<Object> settlementData = getJdbcTemplate().query(getData, new Object[] { beanObj.getFileDate() },
				new ResultSetExtractor<List<Object>>() {
					public List<Object> extractData(ResultSet rs) throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							Map<String, String> table_Data = new HashMap<String, String>();
							for (String column : cols) {
								table_Data.put(column, rs.getString(column));
							}
							beanList.add(table_Data);
						}
						return beanList;
					}
				});

		data.add(cols);
		data.add(settlementData);
		return data;
	}

	public ArrayList<String> getColumnList(String tableName) {
		return null;
	}

	public ArrayList<String> getTTUMColumnList(String tableName) {
		return null;
	}

	public Boolean validateSettlementTTUM(RupayUploadBean beanObj) {
		try {
			int getCountTTUM;
			String checkSettlementTTUM = "";
			String ddate = this.generalUtil.eoddate(beanObj.getFileDate());
			if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
				checkSettlementTTUM = "Select count(*) from RUPAY_SETTLEMENT_TTUM WHERE FILEDATE = to_date(?,'DD/MM/YYYY') ";
				getCountTTUM = ((Integer) getJdbcTemplate().queryForObject(checkSettlementTTUM,
						new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
			} else {
				checkSettlementTTUM = "Select count(*) from  RUPAY_SETTLEMENT_TTUM_INT  WHERE FILEDATE = to_date(?,'DD/MM/YYYY') ";
				getCountTTUM = ((Integer) getJdbcTemplate().queryForObject(checkSettlementTTUM, new Object[] { ddate },
						Integer.class)).intValue();
			}
			System.out.println("inside the validate_settlement 22");
			if (getCountTTUM > 0)
				return Boolean.valueOf(true);
			return Boolean.valueOf(false);
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementTTUM " + e);
			return Boolean.valueOf(false);
		}
	}

	public Boolean visavalidateSettlementTTUM(RupayUploadBean beanObj) {
		try {
			String checkSettlementTTUM = "";
			System.out.println("inside the validatesettlement");
			if (beanObj.getSubcategory().toUpperCase().equalsIgnoreCase("DOMESTIC")) {
				checkSettlementTTUM = "Select count(*) from VISA_SETTLEMENT_TTUM WHERE (FILEDATE = TO_CHAR(TO_DATE(?, 'DD/MON/YYYY'), 'DD-MM-YY') OR FILEDATE = TO_CHAR(TO_DATE(?, 'DD/MON/YYYY'), 'DD-MON-YY'))  ";
			} else {
				checkSettlementTTUM = "Select count(*) from visa_settlement_ttum_int WHERE (FILEDATE = TO_CHAR(TO_DATE(?, 'DD/MON/YYYY'), 'DD-MM-YY') OR FILEDATE = TO_CHAR(TO_DATE(?, 'DD/MON/YYYY'), 'DD-MON-YY'))  ";
			}
			int getCountTTUM = ((Integer) getJdbcTemplate().queryForObject(checkSettlementTTUM,
					new Object[] { beanObj.getFileDate(), beanObj.getFileDate() }, Integer.class)).intValue();
			if (getCountTTUM > 0)
				return Boolean.valueOf(true);
			return Boolean.valueOf(false);
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementTTUM " + e);
			return Boolean.valueOf(false);
		}
	}

	public Boolean checkdata(RupayUploadBean beanObj) {
		try {
			int getCountTTUM;
			System.out.println("INSIDE CHECK DATA");
			String ddate = this.generalUtil.eoddate(beanObj.getFileDate());
			System.out.println("DDATE IS " + ddate);
			String checkSettlementTTUM = "";
			System.out.println("tablename CHECK DATA");
			System.out.println("inside the validatesettlement  11");
			if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
				checkSettlementTTUM = "Select count(*) from RUPAY_dscr_rawDATA WHERE FILEDATE = to_date(?,'DD/MM/YYYY') ";
				getCountTTUM = ((Integer) getJdbcTemplate().queryForObject(checkSettlementTTUM,
						new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
			} else {
				checkSettlementTTUM = "Select count(*) from IRGCS_RUPAY_INT_DSR_RAWDATA WHERE FILEDATE = to_date(?,'DD/MM/YYYY') ";
				getCountTTUM = ((Integer) getJdbcTemplate().queryForObject(checkSettlementTTUM, new Object[] { ddate },
						Integer.class)).intValue();
			}
			if (getCountTTUM > 0)
				return Boolean.valueOf(true);
			return Boolean.valueOf(false);
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementTTUM " + e);
			return Boolean.valueOf(false);
		}
	}

	public Boolean visacheckdata(RupayUploadBean beanObj) {
		try {
			System.out.println("inside the visa validatesettlement");
			String newDate = getDate(beanObj.getFileDate());
			String checkSettlementTTUM = "Select count(*) from visa_ep_rawdata WHERE FILEDATE = TO_DATE('" + newDate
					+ "', 'DD-MM-YY') ";
			System.out.println("EP Rawdata Count is quer :" + checkSettlementTTUM);
			int getCountTTUM = ((Integer) getJdbcTemplate().queryForObject(checkSettlementTTUM, new Object[0],
					Integer.class)).intValue();
			if (getCountTTUM > 0)
				return Boolean.valueOf(true);
			return Boolean.valueOf(false);
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementTTUM " + e);
			return Boolean.valueOf(false);
		}
	}

	public String getDate(String date) {
		String monthName = "";
		String[] num = date.split("/");
		System.out.println(num[1]);
		String localDate = String.valueOf(num[0]) + "-" + num[1].toUpperCase() + "-" + num[2].substring(2, 4);
		String str1;
		switch ((str1 = num[1].toUpperCase()).hashCode()) {
		case 65027:
			if (!str1.equals("APR"))
				break;
			monthName = "04";
			break;
		case 65171:
			if (!str1.equals("AUG"))
				break;
			monthName = "08";
			break;
		case 67554:
			if (!str1.equals("DEC"))
				break;
			monthName = "12";
			break;
		case 69475:
			if (!str1.equals("FEB"))
				break;
			monthName = "02";
			break;
		case 73207:
			if (!str1.equals("JAN"))
				break;
			monthName = "01";
			break;
		case 73825:
			if (!str1.equals("JUL"))
				break;
			monthName = "07";
			break;
		case 73827:
			if (!str1.equals("JUN"))
				break;
			monthName = "06";
			break;
		case 76094:
			if (!str1.equals("MAR"))
				break;
			monthName = "03";
			break;
		case 76101:
			if (!str1.equals("MAY"))
				break;
			monthName = "05";
			break;
		case 77493:
			if (!str1.equals("NOV"))
				break;
			monthName = "11";
			break;
		case 78080:
			if (!str1.equals("OCT"))
				break;
			monthName = "10";
			break;
		case 81982:
			if (!str1.equals("SEP"))
				break;
			monthName = "09";
			break;
		}
		String newDate = String.valueOf(num[0]) + "-" + monthName + "-" + num[2].substring(2, 4);
		System.out.println(newDate);
		System.out.println("localDate " + localDate);
		return newDate;
	}

	public Boolean checkpresentmenetupload(RupayUploadBean beanObj) {
		return null;
	}

	public List<Object> getSettlementTTUMData(RupayUploadBean beanObj) {
		return null;
	}

	public List<Object> visagetSettlementTTUMData(RupayUploadBean beanObj) {
		return null;
	}

	public boolean processSettlementTTUM(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("inside the processsssssssssss");
		try {
			RupaySettlementTTUMProc rollBackexe = new RupaySettlementTTUMProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			this.logger.info("Exception in processSettlementTTUM " + e);
			return false;
		}
	}

	private class RupaySettlementTTUMProc extends StoredProcedure {
		// private static final String insert_proc = "RUPAY_TTUM_PROCESS_NEW";
		private static final String insert_proc = "RUPAY_EOD_TTUM_PROCESS_NEW";

		public RupaySettlementTTUMProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("USER_ID", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("SUBCATEGORY", Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean visaprocessSettlementTTUM(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("inside the visa processsssssssssss");
		try {
			VisaSettlementTTUMProc rollBackexe = new VisaSettlementTTUMProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			this.logger.info("Exception in processSettlementTTUM " + e);
			return false;
		}
	}

	private class VisaSettlementTTUMProc extends StoredProcedure {
		// private static final String insert_proc = "RUPAY_TTUM_PROCESS_NEW";
		private static final String insert_proc = "VISA_EOD_TTUM_PROCESS_NEW";

		public VisaSettlementTTUMProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean IntvisaprocessSettlementTTUM(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("inside the visa processsssssssssss");
		try {
			IntVisaSettlementTTUMProc rollBackexe = new IntVisaSettlementTTUMProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			this.logger.info("Exception in processSettlementTTUM " + e);
			return false;
		}
	}

	private class IntVisaSettlementTTUMProc extends StoredProcedure {
		// private static final String insert_proc = "RUPAY_TTUM_PROCESS_NEW";
		private static final String insert_proc = "VISA_EOD_TTUM_PROCESS_NEW_INT";

		public IntVisaSettlementTTUMProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public boolean processRectification(RupayUploadBean beanObj) {
		try {
			String updateQuery = "update rupay_settlement_report set final_sett_amt = (select final_net from rupay_dscr_rawdata WHERE  UPPER(ISS_BIN) = 'TOTAL' AND CYCLE = '"
					+ beanObj.getCycle() + "' " + "\tand subcategOry ='" + beanObj.getSubcategory()
					+ "' and filedate = to_date('" + beanObj.getFileDate() + "','DD/MM/YYYY'))"
					+ " where filedate = to_date('" + beanObj.getFileDate() + "','DD/MM/YYYY') " + "AND CYCLE = "
					+ beanObj.getCycle() + " AND ACQ_ISS_BIN = 'TOTAL' and subcategory = '" + beanObj.getSubcategory()
					+ "'";
			getJdbcTemplate().execute(updateQuery);
			String updateTable = "update rupay_settlement_report_diff set rectified_flag = 'Y' WHERE FILEDATE = to_date('"
					+ beanObj.getFileDate() + "','DD/MM/YYYY')  AND CYCLE = " + beanObj.getCycle()
					+ " AND SUBCATEGORY = '" + beanObj.getSubcategory() + "'";
			getJdbcTemplate().execute(updateTable);
			return true;
		} catch (Exception e) {
			this.logger.info("Exception in processRectification " + e);
			return false;
		}
	}

	public boolean validateSettlementDiff(RupayUploadBean beanObj) {
		try {
			String checkSettlementTTUM = "select COUNT(*) from rupay_settlement_report_diff where FILEDATE = TO_DATE(?,'DD/MM/YYYY') AND RECTIFIED_FLAG = 'N' AND CYCLE = ? and subcategory = ?";
			int getDiffCount = ((Integer) getJdbcTemplate().queryForObject(checkSettlementTTUM,
					new Object[] { beanObj.getFileDate(), beanObj.getCycle(), beanObj.getSubcategory() },
					Integer.class)).intValue();
			if (getDiffCount > 0)
				return true;
			return false;
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementTTUM " + e);
			return false;
		}
	}

	public HashMap<String, Object> validateDiffAmount(RupayUploadBean beanObj) {
		HashMap<String, Object> output = new HashMap<>();
		try {
			String getDiffQuery = "select t1.FINAL_SETT_AMT-final_net from rupay_settlement_report t1, rupay_dscr_rawdata t2 where t1.acq_iss_bin = 'TOTAL' and UPPER(t2.ISS_BIN) = 'TOTAL' AND t2.CYCLE = ? and t1.CYCLE = ? and t2.subcategOry = ? and t1.subcategOry =? and T1.FILEDATE = to_date(?,'DD/MM/YYYY') AND T1.FILEDATE = T2.FILEDATE";
			double getDiffAmt = ((Double) getJdbcTemplate()
					.queryForObject(
							getDiffQuery, new Object[] { beanObj.getCycle(), beanObj.getCycle(),
									beanObj.getSubcategory(), beanObj.getSubcategory(), beanObj.getFileDate() },
							Double.class)).doubleValue();
			this.logger.info("diff amt entered is " + beanObj.getRectAmt().substring(1));
			if (getDiffAmt > 0.0D) {
				if (getDiffAmt != Double.parseDouble(beanObj.getRectAmt().substring(1))) {
					output.put("result", Boolean.valueOf(false));
					output.put("msg", "Entered amount and difference amount are not same");
				} else {
					output.put("result", Boolean.valueOf(true));
				}
			} else if (getDiffAmt != Double.parseDouble(beanObj.getRectAmt())) {
				output.put("result", Boolean.valueOf(false));
				output.put("msg", "Entered amount and difference amount are not same");
			} else {
				output.put("result", Boolean.valueOf(true));
			}
		} catch (Exception e) {
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Exception occured while checking difference Amount");
		}
		return output;
	}

	public boolean checkNCMCFileUploaded(RupayUploadBean beanObj) {
		String tableName = null;
		try {
			if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
				tableName = "qsparc_dscr_rawdata";
			} else if (beanObj.getSubcategory().equalsIgnoreCase("INTERNATIONAL")) {
				tableName = "irgcs_qsparc_int_dsr_rawdata";
			} else {
				tableName = "NCMC_INT_" + beanObj.getFileName() + "_RAWDATA";
			}
			int recordCount = ((Integer) getJdbcTemplate().queryForObject(
					"SELECT COUNT(*) FROM " + tableName + " WHERE FILEDATE = str_to_date(?,'%Y/%m/%d') AND CYCLE = ?",
					new Object[] { beanObj.getFileDate(), beanObj.getCycle() }, Integer.class)).intValue();
			if (recordCount > 0)
				return true;
			return false;
		} catch (Exception e) {
			this.logger.info("Exception in checkFileUploaded " + e);
			return false;
		}
	}

	public HashMap<String, Object> readNCMCFiles(RupayUploadBean beanObj, MultipartFile file) {
		HashMap<String, Object> output = null;
		try {
			if (beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("DSCR")) {
				ReadNCMCDSCRFile fileRead = new ReadNCMCDSCRFile();
				output = fileRead.fileupload(beanObj, file, getConnection());
			}
			return output;
		} catch (Exception e) {
			System.out.println("EXception in readDSCR file " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("count", Integer.valueOf(0));
			return output;
		}
	}

	public HashMap<String, Object> readNCMCINTFiles(RupayUploadBean beanObj, MultipartFile file) {
		HashMap<String, Object> output = null;
		try {
			if (beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("DSCR")) {
				ReadNCMCDSCRFile fileRead = new ReadNCMCDSCRFile();
				output = fileRead.fileuploadINT(beanObj, file, getConnection());
			}
			return output;
		} catch (Exception e) {
			System.out.println("EXception in readDSCR file " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("count", Integer.valueOf(0));
			return output;
		}
	}

	public Boolean validateQsparcTTUM(RupayUploadBean beanObj) {
		try {
			String checkSettlementTTUM = "Select count(*) from qsparc_rupay_ttum WHERE FILEDATE = to_date(?,'DD/MM/YYYY')";
			int getCountTTUM = ((Integer) getJdbcTemplate().queryForObject(checkSettlementTTUM,
					new Object[] { beanObj.getFileDate() }, Integer.class)).intValue();
			if (getCountTTUM > 0)
				return Boolean.valueOf(true);
			return Boolean.valueOf(false);
		} catch (Exception e) {
			this.logger.info("Exception in validateSettlementTTUM " + e);
			return Boolean.valueOf(false);
		}
	}

	public boolean processSettlementTTUMQsparc(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		try {
			RupayQsparcTTUMProc rollBackexe = new RupayQsparcTTUMProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			this.logger.info("Exception in processSettlementTTUM " + e);
			return false;
		}
	}

	private class RupayQsparcTTUMProc extends StoredProcedure {
		private static final String insert_proc = "RUPAY_QSPARC_TTUM_PROCESS";

		public RupayQsparcTTUMProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("USER_ID", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("SUBCATEGORY", Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public List<Object> getQsparcTTUMData(RupayUploadBean beanObj) {
		return null;
	}

	public String IntuploadPresentmentData(RupayUploadBean beanObj, MultipartFile file)
			throws IOException, Exception, SQLException {
		int sr_no = 1, batchSize = 0, rowCount = 0, batchNumber = 0;
		OracleConn oracObj = new OracleConn();
		Connection conn = oracObj.getconn();
		String query = "insert into INTPRESENTMENT_DATA(Report_Date, Presentment_Raise_Date, Presentment_Settlement_Date, Case_Number, Function_Code_and_Description, FunCdDesc, Scheme_Name, Transaction_Flag, Primary_Account_Number, Date_Local_Transaction, Transaction_Settlement_Date, RRN, Processing_Code, Currency_Code_Transaction, E_Commerce_Indicator, Amount_Transaction, Amount_Additional, Currency_Code_Settlement, Settlement_Amount, Settlement_Amount_Additional, Settlement_Amount_Presentment , Approval_Code, Originator_Point, POS_Entry_Mode, POS_Condition_Code, ACQParticipantCode, Acquirer_Institution_ID_code, Tran_Inst_ID_code, Acquirer_Name_and_Country, AcqInstCode, ACQDir ,ISSParticipantCode, Issuer_Institution_ID_code, Trans_Dest_Inst_ID_code, Issuer_Name_and_Country, IssInstCode, ISSDir, Card_Acceptor_Terminal_ID, Card_Acceptor_Name, Card_Acceptor_Location_address, Card_Acceptor_Country_Code, Card_Acceptor_Business_Code, Card_Acceptor_ID_Code, Card_Acceptor_State_Name, Card_Acceptor_City, aged, MTI ,FILEDATE ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?) ";
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			PreparedStatement ps = conn.prepareStatement(query);
			conn.setAutoCommit(false);
			char quote = '"';
			char BLANK = ' ';
			while ((line = br.readLine()) != null) {
				if (line.contains("Report Date") || line.contains("Presentment Raise Date")
						|| line.contains("End of Report") || line.contains("END OF REPORT"))
					continue;
				sr_no = 1;
				String[] tempArr = line.split("\\,", -1);
				ps.setString(sr_no++, (tempArr[0] != null) ? tempArr[0].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[1] != null) ? tempArr[1].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[2] != null) ? tempArr[2].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[3] != null) ? tempArr[3].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[4] != null) ? tempArr[4].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[5] != null) ? tempArr[5].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[6] != null) ? tempArr[6].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[7] != null) ? tempArr[7].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[8] != null) ? tempArr[8].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[9] != null) ? tempArr[9].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[10] != null) ? tempArr[10].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[11] != null) ? tempArr[11].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[12] != null) ? tempArr[12].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[13] != null) ? tempArr[13].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[14] != null) ? tempArr[14].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[15] != null) ? tempArr[15].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[16] != null) ? tempArr[16].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[17] != null) ? tempArr[17].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[18] != null) ? tempArr[18].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[19] != null) ? tempArr[19].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[20] != null) ? tempArr[20].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[21] != null) ? tempArr[21].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[22] != null) ? tempArr[22].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[23] != null) ? tempArr[23].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[24] != null) ? tempArr[24].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[25] != null) ? tempArr[25].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[26] != null) ? tempArr[26].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[27] != null) ? tempArr[27].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[28] != null) ? tempArr[28].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[29] != null) ? tempArr[29].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[30] != null) ? tempArr[30].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[31] != null) ? tempArr[31].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[32] != null) ? tempArr[32].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[33] != null) ? tempArr[33].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[34] != null) ? tempArr[34].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[35] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[36] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[37] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[38] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[39] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[40] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[41] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[42] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[43] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[44] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[45] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[46] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, beanObj.getFileDate());
				ps.addBatch();
				rowCount++;
				batchSize++;
				if (batchSize == 10000) {
					batchNumber++;
					System.out.println("Batch Executed " + batchNumber);
					batchSize = 0;
					ps.executeBatch();
				}
			}
			if (batchSize > 0) {
				batchNumber++;
				System.out.println("Batch Executed " + batchNumber);
				batchSize = 0;
				ps.executeBatch();
			}
			conn.commit();
			br.close();
			return "Total Record Inserted " + rowCount;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Line is" + line);
			return "Data not inserted. Error at line " + rowCount;
		}
	}

	public String uploadPresentmentData(RupayUploadBean beanObj, MultipartFile file)
			throws IOException, Exception, SQLException {
		int sr_no = 1, batchSize = 0, rowCount = 0, batchNumber = 0;
		OracleConn oracObj = new OracleConn();
		Connection conn = oracObj.getconn();
		System.out.println("filename is " + file.getOriginalFilename());
		int ind = file.getOriginalFilename().indexOf(".");
		String cycle = file.getOriginalFilename().substring(ind - 1, ind);
		String query = "insert into Presentment_data(Report_Date, Presentment_Raise_Date, Presentment_Settlement_Date, Function_Code_and_Description, PAN, Local_Transaction, RRN, Processing_Code, Currency_Code, E_Commerce_Indicator, Amount_Transaction, Amount_Additional, Settlement_Amount_Transaction, Settlement_Amount_Additional, Approval_Code, Originator_Point, POS_Entry_Mode, POS_Condition_Code, Acquirer_Institution_ID_code, Transaction_Originator_Institution_ID_code, Acquirer_Name_and_Country, Issuer_Institution_ID_code, Transaction_Destination_Institution_ID_code, Issuer_Name_and_Country, Card_Type, Card_Brand, Card_Acceptor_Terminal_ID, Card_Acceptor_Name, Card_Acceptor_Location, Card_Acceptor_Country_Code, Card_Acceptor_Business_Code, Card_Acceptor_ID_Code, Card_Acceptor_State_Name, Card_Acceptor_City, Days_Aged, MTI, FileDate,cycle) values(?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			PreparedStatement ps = conn.prepareStatement(query);
			conn.setAutoCommit(false);
			char quote = '"';
			char BLANK = ' ';
			while ((line = br.readLine()) != null) {
				if (line.contains("Report Date") || line.contains("Presentment Raise Date")
						|| line.contains("End of Report") || line.contains("END OF REPORT"))
					continue;
				sr_no = 1;
				String[] tempArr = line.split("\\,", -1);
				ps.setString(sr_no++, (tempArr[0] != null) ? tempArr[0].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[1] != null) ? tempArr[1].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[2] != null) ? tempArr[2].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[3] != null) ? tempArr[3].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[4] != null) ? tempArr[4].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[5] != null) ? tempArr[5].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[6] != null) ? tempArr[6].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[7] != null) ? tempArr[7].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[8] != null) ? tempArr[8].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[9] != null) ? tempArr[9].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[10] != null) ? tempArr[10].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[11] != null) ? tempArr[11].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[12] != null) ? tempArr[12].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[13] != null) ? tempArr[13].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[14] != null) ? tempArr[14].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[15] != null) ? tempArr[15].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[16] != null) ? tempArr[16].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[17] != null) ? tempArr[17].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[18] != null) ? tempArr[18].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[19] != null) ? tempArr[19].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[20] != null) ? tempArr[20].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[21] != null) ? tempArr[21].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[22] != null) ? tempArr[22].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[23] != null) ? tempArr[23].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[24] != null) ? tempArr[24].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[25] != null) ? tempArr[25].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[26] != null) ? tempArr[26].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[27] != null) ? tempArr[27].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[28] != null) ? tempArr[28].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[29] != null) ? tempArr[29].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[30] != null) ? tempArr[30].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[31] != null) ? tempArr[31].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[32] != null) ? tempArr[32].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[33] != null) ? tempArr[33].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[34] != null) ? tempArr[34].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[35] != null) ? tempArr[35].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, beanObj.getFileDate());
				ps.setString(sr_no++, cycle);
				ps.addBatch();
				rowCount++;
				batchSize++;
				if (batchSize == 10000) {
					batchNumber++;
					System.out.println("Batch Executed " + batchNumber);
					batchSize = 0;
					ps.executeBatch();
				}
			}
			if (batchSize > 0) {
				batchNumber++;
				System.out.println("Batch Executed " + batchNumber);
				batchSize = 0;
				ps.executeBatch();
			}
			conn.commit();
			br.close();
			return "Total Record Inserted " + rowCount;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Line is" + line);
			return "Data not inserted. Error at line " + rowCount;
		}
	}

	public Boolean checkmoneyaddupload(RupayUploadBean beanObj) {
		String sk = this.generalUtil.moneyadddate(beanObj.getFileDate());
		System.out.println("date from frontend is " + beanObj.getFileDate());
		System.out.println("date from moneyaddfunction is     " + sk);
		try {
			System.out.println("inside the money add validatesettlement");
			String checkSettlementTTUM = "Select count(*) from moneyaddreport WHERE FILEDATE = ? and CYCLE = ? ";
			int getCountTTUM = ((Integer) getJdbcTemplate().queryForObject(checkSettlementTTUM,
					new Object[] { sk, beanObj.getCycle() }, Integer.class)).intValue();
			if (getCountTTUM > 0)
				return Boolean.valueOf(false);
			return Boolean.valueOf(true);
		} catch (Exception e) {
			this.logger.info("Exception in VALIDATING money add  " + e);
			return Boolean.valueOf(false);
		}
	}

	public String uploadmoneyadd(RupayUploadBean beanObj, MultipartFile file)
			throws IOException, Exception, SQLException {
		int sr_no = 1, batchSize = 0, rowCount = 0, batchNumber = 0;
		String skdate = this.generalUtil.moneyadddate(beanObj.getFileDate());
		OracleConn oracObj = new OracleConn();
		Connection conn = oracObj.getconn();
		String query = "INSERT INTO MONEYADDREPORT (MTI,FUNCTION_CODE,MASKED_PAN,TRANSACTION_LOCAL_DATE_AND_TIME,RETRIEVAL_REFERENCE_NUMBER,TRANSACTION_TYPE,ACQUIRER_INSTITUTION_ID_CODE,APPROVAL_CODE,CARD_ACCEPTOR_TERMINAL_ID,TRANSACTION_AMOUNT,TRANSACTION_ORIGINATOR_INSTITUTION_ID_CODE,TRANSACTION_DESTINATION_INSTITUTION_ID_CODE,SETTLEMENT_DATE,SETTLEMENT_AMOUNT,SETTLEMENT_DR_CR,PROCESSING_FEETPCD,PROCESSING_FEEAMT,PROCESSING_FEEDCIND,ASSESSMENT_FEETPCD,ASSESSMENT_FEEAMT,ASSESSMENT_FEEDCIND,INTRCHNGCTG_FEETPCD,INTRCHNGCTG_FEEAMT,INTRCHNGCTG_FEEDCIND,INTRCHNGCTG_CODE,TOKENIZATION_INDICATOR,FILEDATE,CYCLE)   VALUES  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			PreparedStatement ps = conn.prepareStatement(query);
			conn.setAutoCommit(false);
			char quote = '"';
			char BLANK = ' ';
			while ((line = br.readLine()) != null) {
				if (line.contains("Report Date") || line.contains("Presentment Raise Date")
						|| line.contains("End of Report") || line.contains("END OF REPORT") || line.contains("MTI"))
					continue;
				sr_no = 1;
				String[] tempArr = line.split("\\,", -1);
				System.out.println("data in line is" + tempArr);
				ps.setString(sr_no++, (tempArr[0] != null) ? tempArr[0].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[1] != null) ? tempArr[1].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[2] != null) ? tempArr[2].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[3] != null) ? tempArr[3].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[4] != null) ? tempArr[4].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[5] != null) ? tempArr[5].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[6] != null) ? tempArr[6].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[7] != null) ? tempArr[7].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[8] != null) ? tempArr[8].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[9] != null) ? tempArr[9].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[10] != null) ? tempArr[10].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[11] != null) ? tempArr[11].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[12] != null) ? tempArr[12].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[13] != null) ? tempArr[13].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[14] != null) ? tempArr[14].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[15] != null) ? tempArr[15].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[16] != null) ? tempArr[16].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[17] != null) ? tempArr[17].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[18] != null) ? tempArr[18].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[19] != null) ? tempArr[19].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[20] != null) ? tempArr[20].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[21] != null) ? tempArr[21].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[22] != null) ? tempArr[22].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[23] != null) ? tempArr[23].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[24] != null) ? tempArr[24].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, (tempArr[25] != null) ? tempArr[25].replace(quote, BLANK).trim() : "");
				ps.setString(sr_no++, skdate);
				ps.setString(sr_no++, beanObj.getCycle());
				ps.addBatch();
				rowCount++;
				batchSize++;
				if (batchSize == 10000) {
					batchNumber++;
					System.out.println("Batch Executed " + batchNumber);
					batchSize = 0;
					ps.executeBatch();
				}
			}
			if (batchSize > 0) {
				batchNumber++;
				System.out.println("Batch Executed " + batchNumber);
				batchSize = 0;
				ps.executeBatch();
			}
			conn.commit();
			br.close();
			return "Total Record Inserted " + rowCount;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Line is" + line);
			return "Data not inserted. Error at line " + rowCount;
		}
	}

	public boolean IRGCSprocessSettlementTTUM(RupayUploadBean beanObj) {
		Map<String, Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<>();
		System.out.println("inside the IRGCS  processsssssssssss");
		try {
			RupaySettlementTTUMProcIRGCS rollBackexe = new RupaySettlementTTUMProcIRGCS(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			outParams = rollBackexe.execute(inParams);
			if (outParams != null && outParams.get("msg") != null)
				return false;
			return true;
		} catch (Exception e) {
			this.logger.info("Exception in processSettlementTTUM " + e);
			return false;
		}
	}

	private class RupaySettlementTTUMProcIRGCS extends StoredProcedure {
		// private static final String insert_proc = "RUPAY_TTUM_PROCESS_NEW";

		private static final String insert_proc = "IRGCS_RUPAY_EOD_TTUM_PROCESS_NEW";

		public RupaySettlementTTUMProcIRGCS(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("USER_ID", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("ENTERED_CYCLE", Types.VARCHAR));
			// declareParameter(new
			// SqlParameter("SUBCATEGORY", Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
}
