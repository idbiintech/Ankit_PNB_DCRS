package com.recon.util;

import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.multipart.MultipartFile;

public class ReadUCOSwitchFile extends JdbcDaoSupport {
	public static final String OUTPUT_FOLDER = String.valueOf(System.getProperty("catalina.home")) + File.separator
			+ "OUTPUT_FOLDER";

	private static final int CHUNK_SIZE = 1000;

	PlatformTransactionManager transactionManager;

	Connection con;

	Statement st;

	public void setTransactionManager() {
		this.logger.info("***** ReadSwitchFile.setTransactionManager Start ****");
		try {
			ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext();
			classPathXmlApplicationContext = new ClassPathXmlApplicationContext("/resources/bean.xml");
			this.logger.info("in settransactionManager");
			this.transactionManager = (PlatformTransactionManager) classPathXmlApplicationContext
					.getBean("transactionManager");
			this.logger.info(" settransactionManager completed");
			this.logger.info("***** ReadSwitchFile.setTransactionManager End ****");
			classPathXmlApplicationContext.close();
		} catch (Exception ex) {
			this.logger.error(" error in ReadSwitchFile.setTransactionManager",
					new Exception("ReadSwitchFile.setTransactionManager", ex));
		}
	}

	public HashMap<String, Object> uploadATMSwitchData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {
		this.logger.info("uploadATMSwitchData method Called " + setupBean.getFILEDATE());
		HashMap<String, Object> output = new HashMap<>();
		String thisline = null;
		int sr_no = 1, batchNumber = 0, batchSize = 40000, batchNumber1 = 0, batchcount = 0;
		int lineNumber = 0, feesize = 1;
		int count = 1;
		int countt = 0;
		int beforCount = 0;
		String InsertQuery = "INSERT INTO   switch_atm_rawdata (DATE_TIME, REC_TYPE, AUTH_PPD,  TERM_LN, TERM_FIID, TERM_TERM_ID,  CRD_LN, CRD_FIID,CRD_PAN, CRD_MBR_NUM, BRCH_ID, REGN_ID, USER_FLD1X, TYP_CDE, TYP, RTE_STAT,ORIGINATOR, RESPONDER, ENTRY_TIME, EXIT_TIME, RE_ENTRY_TIME, TRAN_DATE, TRAN_TIM, POST_DAT, ACQ_ICHG_SETL_DAT, ISS_ICHG_SETL_DAT, SEQ_NUM, TERM_TYP, TIM_OFST, ACQ_INST_ID_NUM, RCV_INST_ID_NUM, TRAN_CDE, FROM_ACCT, USER_FLD1, TO_ACCT, MULT_ACCT, AMT_1, AMT_2, AMT_3, DEP_BAL_CR, DEP_TYP, RESP_CDE, TERM_NAME_LOC, TERM_OWNER_NAME, TERM_CITY, TERM_ST, TERM_CNTRY, ORIG_OSEQ_NUM, ORIG_OTRAN_DAT, ORIG_OTRAN_TIM, ORIG_B24_POST, ORIG_CRNCY_CDE,  MULT_CRNCY_AUTH_CRNCY_CDE, MULT_CRNCY_AUTH_CONV_RATE, MULT_CRNCY_SETL_CRNCY_CDE, MULT_CRNCY_SETL_CONV_RATE, MULT_CRNCY_CONV_DAT_TIM, RVSL_RSN, PIN_OFST, SHRG_GRP, DEST_ORDER, AUTH_ID_RESP, REFR_IMP_IND, REFR_AVAIL_IMP, REFR_LEDG_IMP, REFR_HLD_AMT_IMP, REFR_CAF_REFR_IND, REFR_USER_FLD3, DEP_SETL_IMP_FLG, ADJ_SETL_IMP_FLG, REFR_IND, USER_FLD4, FRWD_INST_ID_NUM, CRD_ACCPT_ID_NUM, CRD_ISS_ID_NUM, USER_FLD6,DCRS_REMARKS,FILEDATE,FILENAME) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?)";
		String update_query = "INSERT INTO  switch_data_validation(FILENAME,FILEDATE,COUNT,FILETYPE) VALUES(?,?,?,?)";
		try {
			con.setAutoCommit(false);
			int batchCount = 0;
			PreparedStatement ps = con.prepareStatement(InsertQuery);
			PreparedStatement updatps = con.prepareStatement(update_query);
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			while ((thisline = br.readLine()) != null) {
				// System.out.println("Data "+ thisLine);
				if (thisline.contains("FTTLF")) {
					break;
				}

				if (count == 1 && thisline.trim().length() == 1045) {
					String pat = "[^\\w ]";
					thisline = thisline.trim().replaceAll(pat, " ");
					thisline = thisline.trim().replaceAll(thisline.substring(3, 157), "");
				}

				if (thisline.trim().length() == 0 || thisline.trim().length() == 5 || thisline.trim().length() == 176
						|| thisline.trim().length() == 91 || thisline.trim().length() == 92
						|| thisline.trim().length() == 106) {
					continue;
				} else {

					if (thisline.trim().length() == 891 || thisline.trim().length() == 1284
							|| thisline.trim().length() == 896 || thisline.trim().length() == 1024
							|| thisline.trim().length() == 904) {

						// Assuming `ps` is the PreparedStatement object
						ps.setString(1, thisline.substring(14, 33).trim()); // DateTime
						ps.setString(2, thisline.substring(33, 35).trim()); // RecType
						ps.setString(3, thisline.substring(35, 39).trim()); // AuthPpd
						ps.setString(4, thisline.substring(39, 43).trim()); // TermLn
						ps.setString(5, thisline.substring(43, 47).trim()); // TermFiid
						ps.setString(6, thisline.substring(47, 63).trim()); // TermTermId
						ps.setString(7, thisline.substring(63, 67).trim()); // CrdLn
						ps.setString(8, thisline.substring(67, 71).trim()); // CrdFiid
						ps.setString(9, thisline.substring(71, 77).trim()); // CrdPan
						ps.setString(10, thisline.substring(77, 82).trim()); // CrdMbrNum
						ps.setString(11, thisline.substring(82, 92).trim()); // BrchId
						ps.setString(12, thisline.substring(92, 102).trim());// RegnId
						ps.setString(13, thisline.substring(102, 112).trim());// UserFld1x
						ps.setString(14, thisline.substring(112, 118).trim());// TypCde
						ps.setString(15, thisline.substring(118, 128).trim());// Typ
						ps.setString(16, thisline.substring(128, 138).trim());// RteStat
						ps.setString(17, thisline.substring(138, 148).trim());// Originator
						ps.setString(18, thisline.substring(148, 158).trim());// Responder
						ps.setString(19, thisline.substring(158, 168).trim());// EntryTime
						ps.setString(20, thisline.substring(168, 178).trim());// ExitTime
						ps.setString(21, thisline.substring(178, 188).trim());// ReEntryTime
						ps.setString(22, thisline.substring(188, 198).trim());// TranDate
						ps.setString(23, thisline.substring(198, 208).trim());// TranTim
						ps.setString(24, thisline.substring(208, 218).trim());// PostDat
						ps.setString(25, thisline.substring(218, 228).trim());// AcqIchgSetlDat
						ps.setString(26, thisline.substring(228, 238).trim());// IssIchgSetlDat
						ps.setString(27, thisline.substring(238, 248).trim());// TermTyp
						ps.setString(28, thisline.substring(248, 258).trim());// TimOfst
						ps.setString(29, thisline.substring(258, 268).trim());// AcqInstIdNum
						ps.setString(30, thisline.substring(268, 278).trim());// RcvInstIdNum
						ps.setString(31, thisline.substring(278, 288).trim());// TranCde
						ps.setString(32, thisline.substring(288, 298).trim());// FromAcct
						ps.setString(33, thisline.substring(298, 308).trim());// UserFld1
						ps.setString(34, thisline.substring(308, 318).trim());// ToAcct
						ps.setString(35, thisline.substring(318, 328).trim());// MultAcct
						ps.setString(36, thisline.substring(328, 338).trim());// Amt1
						ps.setString(37, thisline.substring(338, 348).trim());// Amt2
						ps.setString(38, thisline.substring(348, 358).trim());// Amt3
						ps.setString(39, thisline.substring(358, 368).trim());// DepBalCr
						ps.setString(40, thisline.substring(368, 378).trim());// DepTyp
						ps.setString(41, thisline.substring(378, 388).trim());// RespCde
						ps.setString(42, thisline.substring(388, 398).trim());// TermNameLoc
						ps.setString(43, thisline.substring(398, 408).trim());// TermOwnerName
						ps.setString(44, thisline.substring(408, 418).trim());// TermCity
						ps.setString(45, thisline.substring(418, 428).trim());// TermSt
						ps.setString(46, thisline.substring(428, 438).trim());// TermCntry
						ps.setString(47, thisline.substring(438, 448).trim());// OrigOseqNum
						ps.setString(48, thisline.substring(448, 458).trim());// OrigOtranDat
						ps.setString(49, thisline.substring(458, 468).trim());// OrigOtranTim
						ps.setString(50, thisline.substring(468, 478).trim());// OrigB24Post
						ps.setString(51, thisline.substring(478, 488).trim());// OrigCrncyCde
						ps.setString(52, thisline.substring(488, 498).trim());// MultCrncyAuthCrncyCde
						ps.setString(53, thisline.substring(498, 508).trim());// MultCrncyAuthConvRate
						ps.setString(54, thisline.substring(508, 518).trim());// MultCrncySetlCrncyCde
						ps.setString(55, thisline.substring(518, 528).trim());// MultCrncySetlConvRate
						ps.setString(56, thisline.substring(528, 538).trim());// MultCrncyConvDatTim
						ps.setString(57, thisline.substring(538, 548).trim());// RvslRsn
						ps.setString(58, thisline.substring(548, 558).trim());// PinOfst
						ps.setString(59, thisline.substring(558, 568).trim());// ShrgGrp
						ps.setString(60, thisline.substring(568, 578).trim());// DestOrder
						ps.setString(61, thisline.substring(578, 588).trim());// AuthIdResp
						ps.setString(62, thisline.substring(588, 598).trim());// RefrImpInd
						ps.setString(63, thisline.substring(598, 608).trim());// RefrAvailImp
						ps.setString(64, thisline.substring(608, 618).trim());// RefrLedgImp
						ps.setString(65, thisline.substring(618, 628).trim());// RefrHldAmtImp
						ps.setString(66, thisline.substring(628, 638).trim());// RefrCafRefrInd
						ps.setString(67, thisline.substring(638, 648).trim());// RefrUserFld3
						ps.setString(68, thisline.substring(648, 658).trim());// DepSetlImpFlg
						ps.setString(69, thisline.substring(658, 668).trim());// AdjSetlImpFlg
						ps.setString(70, thisline.substring(668, 678).trim());// RefrInd
						ps.setString(71, thisline.substring(678, 688).trim());// UserFld4
						ps.setString(72, thisline.substring(688, 698).trim());// FrwdInstIdNum
						ps.setString(73, thisline.substring(698, 708).trim());// CrdAccptIdNum
						ps.setString(74, thisline.substring(708, 718).trim());// CrdIssIdNum
						ps.setString(75, thisline.substring(718, 728).trim());// UserFld6
						ps.setString(76, thisline.substring(728, 738).trim());// DcrsRemarks
						ps.setString(77, setupBean.getFileDate()); // FileDate
						ps.setString(78, file.getOriginalFilename()); // FileName
						thisline = null;
						count++;
						ps.addBatch();
						continue;
					}
				}
				if (++batchcount % batchSize == 0) {
					batchNumber++;
					System.out.println("Batch Executed is " + batchNumber);
					ps.executeBatch();
					System.gc();
					con.commit();
					continue;
				}
				batchNumber++;
				System.out.println("Batch Executed is " + batchNumber + "k " + beforCount);
				ps.executeBatch();
				System.gc();
				con.commit();
			}
			this.logger.info("Executed Batch SuccessFully");
			System.gc();
			ps.executeBatch();
			con.commit();
			if (count > 0) {
				updatps.setString(1, setupBean.getP_FILE_NAME());
				updatps.setString(2, setupBean.getFileDate());
				System.out.println("beforCount " + count);
				updatps.setString(3, String.valueOf(count));
				updatps.setString(4, "ATM");
				updatps.execute();
				con.commit();
				this.logger.info("update Batch Completed");
			}
			this.logger.info("Executed Batch Completed");
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "File Uploaded and Record count is " + count);
			this.logger.info("Completed " + countt);
			con.close();
			return output;
		} catch (Exception e) {
			this.logger.info("Exception in lineNumber " + count);
			this.logger.info("Exception in ReadUCOATMSwitchData " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Issue at Line Number " + lineNumber);
			return output;
		}
	}

	public HashMap<String, Object> uploadPOSSwitchData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {
		this.logger.info("uploadPOSSwitchData method Called " + setupBean.getFILEDATE());
		HashMap<String, Object> output = new HashMap<>();
		String thisline = null;
		int lineNumber = 0, feesize = 1;
		int count = 1, beforecount = 0;
		int sr_no = 1, batchNumber = 0, batchSize = 40000;
		String InsertQuery = "  INSERT INTO  switch_pos_rawdata (DATE_TIME,REC_TYP,CRD_LN,CRD_FIID,CRAD_NUM,CRD_MBR_NUM,RETL_KEY_IN,RDF_KEY,RDF_KEY_GRP,RDF_KEY_REGN,RDF_KEY_ID,TERM_ID,SHIFT_NUM,BATCH_NUM,"
				+ "TERM_IN,TERM_FIID,TERM_ID2,TERM_TIME,TERM_ID3,TKEY_RKEY_REC_FRMT,TKEY_RKEY_RETAILER_ID,TKEY_RKEY_CLERK_ID,DATA_FLAG,TYPE, "
				+ " RTE_STAT,ORIGINATOR,RESPONDER,ISS_CDE,ENTRY_TIME,EXIT_TIME,RE_ENTRY_TIME,TRAN_DATE,TRAN_TIM,POST_DAT,ACQ_ICHG_SETL_DAT,ISS_ICHG_SETL_DAT,SEQ_NUM,TERM_NAME_LOC,TERM_OWNER_NAME,TERM_CITY,"
				+ "TERM_ST,TERM_CNTRY_CDE,BRCH_ID,USER_FID,TERM_TIM_OFST,ACQ_INST_ID_NUM,RCV_INST_ID_NUM,TERM_TYPE,CLERK_ID,CTR_AUTH,CTR_AUTH_GRP,CTR_AUTH_USER_ID,RETL_SIC_CDE,ORIG,DEST,TRAN_CDE,CRD_TYPE,"
				+ " ACCT,RESP_CDE,AMOUNT_1,AMOUNT_2,EXPIRY_DATE,TRACK2,PIN_OFST,PRE_AUTH_SEQ_NUM,INVOICE_NUM,ORIG_INVOICE_NUM,AUTHORIZER,AUTH_IND,SHIFT_NUM_2,BATCH_SEQ_NUM,APPRV_CODE,APPRV_CODE_LENGTH,ICHG_RESP,PSEUDO_TERM_ID,RFRL_PHONE,DUMMY_1,"
				+ "DFT_CAPTURE_FLAG,SELT_FLAG,RVRL_CODE,REA_FOR_CHRGBCK,NUM_OF_CHRGBCK,PT_SRV_COND_CODE,PT_SRV_ENTRY_MODE,AUTH_IND2,ORIG_CRNCY_CODE,MULTI_CRNY_AUTH_CRNCY_CODE,MULTY_CRNCY_AUTH_CONV_RATE,MULTI_CRNCY_SETL_CRNCY_CODE,MULTI_CRNCY_SETL_CONV_RATE,MULTI_CRNCY_CONV_DAT_TIME,REFR_IMP_IND,REFR_AVAIL_CR,REFR_CR_LMT,REFR_CR_BAL,REFR_TTL,REFR_CUR,ADJ_SETL_IMPACT_FLAG,"
				+ "REFR_IND,FRWD_INST_ID_NUM,CRD_ACCPT_ID_NUM,CRD_ISS_ID_NUM,ORIG_MSG_TYPE,ORIG_TRAN_TIM,ORIG_TRAN_DATE,ORIG_SEQ_NUM,ORIG_B24_POST_DATE,EXCP_RSN_CODE,OVRRDE_FLAG, ADDR,ZIP,ADDR_VERFY_STAT,PIN_IND,PIN_TRIES,PRE_AUTH_TS,PRE_AUTH_HLDS_STAT,USER_FID2,USER_DATA_D_LEN,USER_DATA_D_INFO,DCRS_REMARKS,FILEDATE,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String update_query = "INSERT INTO  switch_data_validation(FILENAME,FILEDATE,COUNT,FILETYPE) VALUES(?,?,?,?)";
		String DATE_TIME = "", REC_TYP = "", CRD_LN = "", CRD_FIID = "", CRAD_NUM = "", CRD_MBR_NUM = "";
		String RETL_KEY_IN = "", RDF_KEY = "", RDF_KEY_GRP = "", RDF_KEY_REGN = "", RDF_KEY_ID = "", TERM_ID = "";
		String SHIFT_NUM = "", BATCH_NUM = "", TERM_IN = "", TERM_FIID = "", TERM_ID2 = "", TERM_TIME = "";
		String TERM_ID3 = "", TKEY_RKEY_REC_FRMT = "", TKEY_RKEY_RETAILER_ID = "", TKEY_RKEY_CLERK_ID = "";
		String DATA_FLAG = "", TYPE = "", RTE_STAT = "", ORIGINATOR = "", RESPONDER = "", ISS_CDE = "";
		String ENTRY_TIME = "", EXIT_TIME = "", RE_ENTRY_TIME = "", TRAN_DATE = "", TRAN_TIM = "", POST_DAT = "";
		String ACQ_ICHG_SETL_DAT = "", ISS_ICHG_SETL_DAT = "", SEQ_NUM = "", TERM_NAME_LOC = "", TERM_OWNER_NAME = "";
		String TERM_CITY = "", TERM_ST = "", TERM_CNTRY_CDE = "", BRCH_ID = "", USER_FID = "", TERM_TIM_OFST = "";
		String ACQ_INST_ID_NUM = "", RCV_INST_ID_NUM = "", TERM_TYPE = "", CLERK_ID = "", CTR_AUTH = "";
		String CTR_AUTH_GRP = "", CTR_AUTH_USER_ID = "", RETL_SIC_CDE = "", ORIG = "", DEST = "", TRAN_CDE = "";
		String CRD_TYPE = "", ACCT = "", RESP_CDE = "", AMOUNT_1 = "", AMOUNT_2 = "", EXPIRY_DATE = "", TRACK2 = "";
		String PIN_OFST = "", PRE_AUTH_SEQ_NUM = "", INVOICE_NUM = "", ORIG_INVOICE_NUM = "", AUTHORIZER = "";
		String AUTH_IND = "", SHIFT_NUM_2 = "", BATCH_SEQ_NUM = "", APPRV_CODE = "", APPRV_CODE_LENGTH = "";
		String ICHG_RESP = "", PSEUDO_TERM_ID = "", RFRL_PHONE = "", DUMMY_1 = "", DFT_CAPTURE_FLAG = "";
		String SELT_FLAG = "", RVRL_CODE = "", REA_FOR_CHRGBCK = "", NUM_OF_CHRGBCK = "", PT_SRV_COND_CODE = "";
		String PT_SRV_ENTRY_MODE = "", AUTH_IND2 = "", ORIG_CRNCY_CODE = "", MULTI_CRNY_AUTH_CRNCY_CODE = "";
		String MULTY_CRNCY_AUTH_CONV_RATE = "", MULTI_CRNCY_SETL_CRNCY_CODE = "", MULTI_CRNCY_SETL_CONV_RATE = "";
		String MULTI_CRNCY_CONV_DAT_TIME = "", REFR_IMP_IND = "", REFR_AVAIL_CR = "", REFR_CR_LMT = "";
		String REFR_CR_BAL = "", REFR_TTL = "", REFR_CUR = "", ADJ_SETL_IMPACT_FLAG = "", REFR_IND = "";
		String FRWD_INST_ID_NUM = "", CRD_ACCPT_ID_NUM = "", CRD_ISS_ID_NUM = "", ORIG_MSG_TYPE = "";
		String ORIG_TRAN_TIM = "", ORIG_TRAN_DATE = "", ORIG_SEQ_NUM = "", ORIG_B24_POST_DATE = "", EXCP_RSN_CODE = "";
		String OVRRDE_FLAG = "", ADDR = "", ZIP = "", ADDR_VERFY_STAT = "", PIN_IND = "", PIN_TRIES = "";
		String PRE_AUTH_TS = "", PRE_AUTH_HLDS_STAT = "", USER_FID2 = "", USER_DATA_D_LEN = "", USER_DATA_D_INFO = "";
		String DCRS_REMARKS = "", FILEDATE = "", FILENAME = "", Second_DATA = "", DATA_LINE = "", FIRST_DATA = "";
		try {
			con.setAutoCommit(false);
			int batchCount = 0;
			PreparedStatement ps = con.prepareStatement(InsertQuery);
			PreparedStatement updatps = con.prepareStatement(update_query);
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			while ((thisline = br.readLine()) != null) {
				if (count == 1 && thisline.trim().length() == 950) {
					String pat = "[^\\w ]";
					thisline = thisline.trim().replaceAll(pat, " ");
					thisline = thisline.trim().replaceAll(thisline.substring(5, 153), "");
				}
				if (count == 1 && thisline.trim().length() == 2774) {
					String pat = "[^\\w ]";
					thisline = thisline.trim().replaceAll(pat, " ");
					thisline = thisline.trim().replaceAll(thisline.substring(0, 150), "DD");
				}
				if (thisline.trim().length() == 0 || thisline.trim().length() == 192 || thisline.trim().length() == 5
						|| thisline.trim().length() == 241 || thisline.trim().length() == 848)
					continue;
				if (thisline.trim().length() == 898 || thisline.trim().length() == 802
						|| thisline.trim().length() == 2026) {
					FIRST_DATA = thisline.substring(8, 14).trim();
					if (FIRST_DATA.contains("1828DR")) {

						FILEDATE = setupBean.getFileDate();
						FILENAME = setupBean.getP_FILE_NAME();
						ps.setString(1, thisline.substring(14, 33).trim());
						ps.setString(2, thisline.substring(33, 35).trim());
						ps.setString(3, thisline.substring(35, 39).trim());
						ps.setString(4, thisline.substring(39, 43).trim());
						ps.setString(5, thisline.substring(43, 62).trim());
						ps.setString(6, thisline.substring(62, 65).trim());
						ps.setString(7, thisline.substring(65, 69).trim());
						ps.setString(8, thisline.substring(65, 69).trim());
						ps.setString(9, thisline.substring(73, 77).trim());
						ps.setString(10, thisline.substring(77, 81).trim());
						ps.setString(11, thisline.substring(81, 100).trim());
						ps.setString(12, thisline.substring(100, 116).trim());
						ps.setString(13, thisline.substring(116, 119).trim());
						ps.setString(14, thisline.substring(119, 122).trim());
						ps.setString(15, thisline.substring(122, 126).trim());
						ps.setString(16, thisline.substring(126, 130).trim());
						ps.setString(17, thisline.substring(130, 146).trim());
						ps.setString(18, thisline.substring(146, 154).trim());
						ps.setString(19, thisline.substring(154, 170).trim());
						ps.setString(20, thisline.substring(170, 171).trim());
						ps.setString(21, thisline.substring(171, 190).trim());
						ps.setString(22, thisline.substring(190, 196).trim());
						ps.setString(23, thisline.substring(196, 197).trim());
						ps.setString(24, thisline.substring(197, 201).trim());
						ps.setString(25, thisline.substring(201, 203).trim());
						ps.setString(26, thisline.substring(203, 204).trim());
						ps.setString(27, thisline.substring(204, 205).trim());
						ps.setString(28, thisline.substring(205, 207).trim());
						ps.setString(29, thisline.substring(207, 226).trim());
						ps.setString(30, thisline.substring(226, 245).trim());
						ps.setString(31, thisline.substring(245, 264).trim());
						ps.setString(32, thisline.substring(264, 270).trim());
						ps.setString(33, thisline.substring(270, 278).trim());
						ps.setString(34, thisline.substring(278, 284).trim());
						ps.setString(35, thisline.substring(284, 290).trim());
						ps.setString(36, thisline.substring(290, 296).trim());
						ps.setString(37, thisline.substring(296, 308).trim());
						ps.setString(38, thisline.substring(308, 333).trim());
						ps.setString(39, thisline.substring(333, 355).trim());
						ps.setString(40, thisline.substring(355, 368).trim());
						ps.setString(41, thisline.substring(368, 371).trim());
						ps.setString(42, thisline.substring(371, 373).trim());
						ps.setString(43, thisline.substring(373, 377).trim());
						ps.setString(44, thisline.substring(377, 380).trim());
						ps.setString(45, thisline.substring(380, 385).trim());
						ps.setString(46, thisline.substring(385, 396).trim());
						ps.setString(47, thisline.substring(396, 407).trim());
						ps.setString(48, thisline.substring(407, 409).trim());
						ps.setString(49, thisline.substring(409, 415).trim());
						ps.setString(50, thisline.substring(415, 427).trim());
						ps.setString(51, CTR_AUTH_GRP);
						ps.setString(52, CTR_AUTH_USER_ID);
						ps.setString(53, thisline.substring(427, 431).trim());
						ps.setString(54, thisline.substring(431, 435).trim());
						ps.setString(55, thisline.substring(435, 439).trim());
						ps.setString(56, thisline.substring(439, 445).trim());
						ps.setString(57, thisline.substring(445, 447).trim());
						ps.setString(58, thisline.substring(447, 466).trim());
						ps.setString(59, thisline.substring(466, 469).trim());
						ps.setString(60, thisline.substring(469, 488).trim());
						ps.setString(61, thisline.substring(488, 507).trim());
						ps.setString(62, thisline.substring(507, 511).trim());
						ps.setString(63, thisline.substring(511, 551).trim());
						ps.setString(64, thisline.substring(551, 567).trim());
						ps.setString(65, thisline.substring(567, 579).trim());
						ps.setString(66, thisline.substring(579, 589).trim());
						ps.setString(67, thisline.substring(589, 599).trim());
						ps.setString(68, thisline.substring(599, 615).trim());
						ps.setString(69, thisline.substring(615, 616).trim());
						ps.setString(70, thisline.substring(616, 619).trim());
						ps.setString(71, thisline.substring(619, 622).trim());
						ps.setString(72, thisline.substring(622, 630).trim());
						ps.setString(73, thisline.substring(630, 631).trim());
						ps.setString(74, thisline.substring(631, 639).trim());
						ps.setString(75, thisline.substring(639, 643).trim());
						ps.setString(76, thisline.substring(643, 663).trim());
						ps.setString(77, thisline.substring(663, 666).trim());
						ps.setString(78, thisline.substring(666, 667).trim());
						ps.setString(79, thisline.substring(667, 668).trim());
						ps.setString(80, thisline.substring(668, 670).trim());
						ps.setString(81, thisline.substring(670, 672).trim());
						ps.setString(82, thisline.substring(672, 673).trim());
						ps.setString(83, thisline.substring(673, 675).trim());
						ps.setString(84, thisline.substring(675, 678).trim());
						ps.setString(85, thisline.substring(678, 679).trim());
						ps.setString(86, thisline.substring(679, 682).trim());
						ps.setString(87, thisline.substring(682, 685).trim());
						ps.setString(88, thisline.substring(685, 693).trim());
						ps.setString(89, thisline.substring(693, 696).trim());
						ps.setString(90, thisline.substring(696, 704).trim());
						ps.setString(91, thisline.substring(704, 723).trim());
						ps.setString(92, thisline.substring(723, 724).trim());
						ps.setString(93, thisline.substring(724, 725).trim());
						ps.setString(94, thisline.substring(725, 726).trim());
						ps.setString(95, thisline.substring(726, 727).trim());
						ps.setString(96, thisline.substring(727, 728).trim());
						ps.setString(97, thisline.substring(728, 729).trim());
						ps.setString(98, thisline.substring(729, 730).trim());
						ps.setString(99, thisline.substring(730, 734).trim());
						ps.setString(100, thisline.substring(734, 750).trim());
						ps.setString(101, thisline.substring(750, 761).trim());
						ps.setString(102, thisline.substring(761, 772).trim());
						ps.setString(103, thisline.substring(772, 776).trim());
						ps.setString(104, thisline.substring(776, 784).trim());
						ps.setString(105, thisline.substring(784, 790).trim());
						ps.setString(106, thisline.substring(790, 802).trim());
						if (thisline.trim().length() == 802) {

							ps.setString(107, ORIG_B24_POST_DATE);
							ps.setString(108, EXCP_RSN_CODE);
							ps.setString(109, OVRRDE_FLAG);
							ps.setString(110, ADDR);
							ps.setString(111, ZIP);
							ps.setString(112, ADDR_VERFY_STAT);
							ps.setString(113, PIN_IND);
							ps.setString(114, PIN_TRIES);
							ps.setString(115, PRE_AUTH_TS);
							ps.setString(116, PRE_AUTH_HLDS_STAT);
							ps.setString(117, USER_FID2);
							ps.setString(118, USER_DATA_D_LEN);
							ps.setString(119, USER_DATA_D_INFO);

						} else {
							ps.setString(107, thisline.substring(802, 806).trim());
							ps.setString(108, thisline.substring(806, 809).trim());
							ps.setString(109, thisline.substring(809, 810).trim());
							ps.setString(110, thisline.substring(810, 830).trim());
							ps.setString(111, thisline.substring(830, 839).trim());
							ps.setString(112, thisline.substring(839, 840).trim());
							ps.setString(113, thisline.substring(840, 841).trim());
							ps.setString(114, thisline.substring(841, 842).trim());
							ps.setString(115, thisline.substring(842, 856).trim());
							ps.setString(116, thisline.substring(856, 857).trim());
							ps.setString(117, thisline.substring(857, 890).trim());
							ps.setString(118, thisline.substring(890, 894).trim());
							ps.setString(119, thisline.substring(894, 895).trim());

						}

						ps.setString(120, DCRS_REMARKS);
						ps.setString(121, FILEDATE);
						ps.setString(122, FILENAME);
						count++;
						beforecount++;
						ps.addBatch();
						continue;
					}
				} else {
					FIRST_DATA = thisline.substring(8, 14).trim();
					if (FIRST_DATA.contains("1828DR")) {
						FILEDATE = setupBean.getFileDate();
						FILENAME = setupBean.getP_FILE_NAME();
						ps.setString(1, thisline.substring(14, 33).trim());
						ps.setString(2, thisline.substring(33, 35).trim());
						ps.setString(3, thisline.substring(35, 39).trim());
						ps.setString(4, thisline.substring(39, 43).trim());
						ps.setString(5, thisline.substring(43, 62).trim());
						ps.setString(6, thisline.substring(62, 65).trim());
						ps.setString(7, thisline.substring(65, 69).trim());
						ps.setString(8, thisline.substring(65, 69).trim());
						ps.setString(9, thisline.substring(73, 77).trim());
						ps.setString(10, thisline.substring(77, 81).trim());
						ps.setString(11, thisline.substring(81, 100).trim());
						ps.setString(12, thisline.substring(100, 116).trim());
						ps.setString(13, thisline.substring(116, 119).trim());
						ps.setString(14, thisline.substring(119, 122).trim());
						ps.setString(15, thisline.substring(122, 126).trim());
						ps.setString(16, thisline.substring(126, 130).trim());
						ps.setString(17, thisline.substring(130, 146).trim());
						ps.setString(18, thisline.substring(146, 154).trim());
						ps.setString(19, thisline.substring(154, 170).trim());
						ps.setString(20, thisline.substring(170, 171).trim());
						ps.setString(21, thisline.substring(171, 190).trim());
						ps.setString(22, thisline.substring(190, 196).trim());
						ps.setString(23, thisline.substring(196, 197).trim());
						ps.setString(24, thisline.substring(197, 201).trim());
						ps.setString(25, thisline.substring(201, 203).trim());
						ps.setString(26, thisline.substring(203, 204).trim());
						ps.setString(27, thisline.substring(204, 205).trim());
						ps.setString(28, thisline.substring(205, 207).trim());
						ps.setString(29, thisline.substring(207, 226).trim());
						ps.setString(30, thisline.substring(226, 245).trim());
						ps.setString(31, thisline.substring(245, 264).trim());
						ps.setString(32, thisline.substring(264, 270).trim());
						ps.setString(33, thisline.substring(270, 278).trim());
						ps.setString(34, thisline.substring(278, 284).trim());
						ps.setString(35, thisline.substring(284, 290).trim());
						ps.setString(36, thisline.substring(290, 296).trim());
						ps.setString(37, thisline.substring(296, 308).trim());
						ps.setString(38, thisline.substring(308, 333).trim());
						ps.setString(39, thisline.substring(333, 355).trim());
						ps.setString(40, thisline.substring(355, 368).trim());
						ps.setString(41, thisline.substring(368, 371).trim());
						ps.setString(42, thisline.substring(371, 373).trim());
						ps.setString(43, thisline.substring(373, 377).trim());
						ps.setString(44, thisline.substring(377, 380).trim());
						ps.setString(45, thisline.substring(380, 385).trim());
						ps.setString(46, thisline.substring(385, 396).trim());
						ps.setString(47, thisline.substring(396, 407).trim());
						ps.setString(48, thisline.substring(407, 409).trim());
						ps.setString(49, thisline.substring(409, 415).trim());
						ps.setString(50, thisline.substring(415, 427).trim());
						ps.setString(51, CTR_AUTH_GRP);
						ps.setString(52, CTR_AUTH_USER_ID);
						ps.setString(53, thisline.substring(427, 431).trim());
						ps.setString(54, thisline.substring(431, 435).trim());
						ps.setString(55, thisline.substring(435, 439).trim());
						ps.setString(56, thisline.substring(439, 445).trim());
						ps.setString(57, thisline.substring(445, 447).trim());
						ps.setString(58, thisline.substring(447, 466).trim());
						ps.setString(59, thisline.substring(466, 469).trim());
						ps.setString(60, thisline.substring(469, 488).trim());
						ps.setString(61, thisline.substring(488, 507).trim());
						ps.setString(62, thisline.substring(507, 511).trim());
						ps.setString(63, thisline.substring(511, 551).trim());
						ps.setString(64, thisline.substring(551, 567).trim());
						ps.setString(65, thisline.substring(567, 579).trim());
						ps.setString(66, thisline.substring(579, 589).trim());
						ps.setString(67, thisline.substring(589, 599).trim());
						ps.setString(68, thisline.substring(599, 615).trim());
						ps.setString(69, thisline.substring(615, 616).trim());
						ps.setString(70, thisline.substring(616, 619).trim());
						ps.setString(71, thisline.substring(619, 622).trim());
						ps.setString(72, thisline.substring(622, 630).trim());
						ps.setString(73, thisline.substring(630, 631).trim());
						ps.setString(74, thisline.substring(631, 639).trim());
						ps.setString(75, thisline.substring(639, 643).trim());
						ps.setString(76, thisline.substring(643, 663).trim());
						ps.setString(77, thisline.substring(663, 666).trim());
						ps.setString(78, thisline.substring(666, 667).trim());
						ps.setString(79, thisline.substring(667, 668).trim());
						ps.setString(80, thisline.substring(668, 670).trim());
						ps.setString(81, thisline.substring(670, 672).trim());
						ps.setString(82, thisline.substring(672, 673).trim());
						ps.setString(83, thisline.substring(673, 675).trim());
						ps.setString(84, thisline.substring(675, 678).trim());
						ps.setString(85, thisline.substring(678, 679).trim());
						ps.setString(86, thisline.substring(679, 682).trim());
						ps.setString(87, thisline.substring(682, 685).trim());
						ps.setString(88, thisline.substring(685, 693).trim());
						ps.setString(89, thisline.substring(693, 696).trim());
						ps.setString(90, thisline.substring(696, 704).trim());
						ps.setString(91, thisline.substring(704, 723).trim());
						ps.setString(92, thisline.substring(723, 724).trim());
						ps.setString(93, thisline.substring(724, 725).trim());
						ps.setString(94, thisline.substring(725, 726).trim());
						ps.setString(95, thisline.substring(726, 727).trim());
						ps.setString(96, thisline.substring(727, 728).trim());
						ps.setString(97, thisline.substring(728, 729).trim());
						ps.setString(98, thisline.substring(729, 730).trim());
						ps.setString(99, thisline.substring(730, 734).trim());
						ps.setString(100, thisline.substring(734, 750).trim());
						ps.setString(101, thisline.substring(750, 761).trim());
						ps.setString(102, thisline.substring(761, 772).trim());
						ps.setString(103, thisline.substring(772, 776).trim());
						ps.setString(104, thisline.substring(776, 784).trim());
						ps.setString(105, thisline.substring(784, 790).trim());
						ps.setString(106, thisline.substring(790, 802).trim());

						ps.setString(107, thisline.substring(802, 806).trim());
						ps.setString(108, thisline.substring(806, 809).trim());
						ps.setString(109, thisline.substring(809, 810).trim());
						ps.setString(110, thisline.substring(810, 830).trim());
						ps.setString(111, thisline.substring(830, 839).trim());
						ps.setString(112, thisline.substring(839, 840).trim());
						ps.setString(113, thisline.substring(840, 841).trim());
						ps.setString(114, thisline.substring(841, 842).trim());
						ps.setString(115, thisline.substring(842, 856).trim());
						ps.setString(116, thisline.substring(856, 857).trim());
						ps.setString(117, thisline.substring(857, 890).trim());
						ps.setString(118, thisline.substring(890, 894).trim());
						ps.setString(119, thisline.substring(894, 895).trim());

						ps.setString(120, DCRS_REMARKS);
						ps.setString(121, FILEDATE);
						ps.setString(122, FILENAME);
						count++;
						beforecount++;
						ps.addBatch();
						continue;
					}
					Second_DATA = thisline.substring(1836, 1842).trim();
					if (Second_DATA.contains("1828DR")) {
						DATE_TIME = thisline.substring(1842, 1861).trim();
						REC_TYP = thisline.substring(1861, 1863).trim();
						CRD_LN = thisline.substring(1863, 1867).trim();
						CRD_FIID = thisline.substring(1867, 1871).trim();
						CRAD_NUM = thisline.substring(1871, 1890).trim();
						CRD_MBR_NUM = thisline.substring(1890, 1893).trim();
						RETL_KEY_IN = thisline.substring(1893, 1897).trim();
						RDF_KEY = thisline.substring(1897, 1901).trim();
						RDF_KEY_GRP = thisline.substring(1901, 1905).trim();
						RDF_KEY_REGN = thisline.substring(1905, 1909).trim();
						RDF_KEY_ID = thisline.substring(1909, 1928).trim();
						TERM_ID = thisline.substring(1928, 1944).trim();
						SHIFT_NUM = thisline.substring(1944, 1947).trim();
						BATCH_NUM = thisline.substring(1947, 1950).trim();
						TERM_IN = thisline.substring(1950, 1954).trim();
						TERM_FIID = thisline.substring(1954, 1958).trim();
						TERM_ID2 = thisline.substring(1958, 1974).trim();
						TERM_TIME = thisline.substring(1974, 1982).trim();
						TERM_ID3 = thisline.substring(1982, 1998).trim();
						TKEY_RKEY_REC_FRMT = thisline.substring(1998, 1999).trim();
						TKEY_RKEY_RETAILER_ID = thisline.substring(1999, 2018).trim();
						TKEY_RKEY_CLERK_ID = thisline.substring(2018, 2024).trim();
						DATA_FLAG = thisline.substring(2024, 2025).trim();
						TYPE = thisline.substring(2025, 2029).trim();
						RTE_STAT = thisline.substring(2029, 2031).trim();
						ORIGINATOR = thisline.substring(2031, 2032).trim();
						RESPONDER = thisline.substring(2032, 2033).trim();
						ISS_CDE = thisline.substring(2033, 2035).trim();
						ENTRY_TIME = thisline.substring(2035, 2054).trim();
						EXIT_TIME = thisline.substring(2054, 2073).trim();
						RE_ENTRY_TIME = thisline.substring(2073, 2092).trim();
						TRAN_DATE = thisline.substring(2092, 2098).trim();
						TRAN_TIM = thisline.substring(2098, 2106).trim();
						POST_DAT = thisline.substring(2106, 2112).trim();
						ACQ_ICHG_SETL_DAT = thisline.substring(2112, 2118).trim();
						ISS_ICHG_SETL_DAT = thisline.substring(2118, 2124).trim();
						SEQ_NUM = thisline.substring(2124, 2136).trim();
						TERM_NAME_LOC = thisline.substring(2136, 2161).trim();
						TERM_OWNER_NAME = thisline.substring(2161, 2183).trim();
						TERM_CITY = thisline.substring(2183, 2196).trim();
						TERM_ST = thisline.substring(2196, 2199).trim();
						TERM_CNTRY_CDE = thisline.substring(2199, 2201).trim();
						BRCH_ID = thisline.substring(2201, 2205).trim();
						USER_FID = thisline.substring(2205, 2208).trim();
						TERM_TIM_OFST = thisline.substring(2208, 2213).trim();
						ACQ_INST_ID_NUM = thisline.substring(2213, 2224).trim();
						RCV_INST_ID_NUM = thisline.substring(2224, 2235).trim();
						TERM_TYPE = thisline.substring(2235, 2237).trim();
						CLERK_ID = thisline.substring(2237, 2243).trim();
						CTR_AUTH = thisline.substring(2243, 2255).trim();
						RETL_SIC_CDE = thisline.substring(2255, 2259).trim();
						ORIG = thisline.substring(2259, 2263).trim();
						DEST = thisline.substring(2263, 2267).trim();
						TRAN_CDE = thisline.substring(2267, 2273).trim();
						CRD_TYPE = thisline.substring(2273, 2275).trim();
						ACCT = thisline.substring(2275, 2294).trim();
						RESP_CDE = thisline.substring(2294, 2297).trim();
						AMOUNT_1 = thisline.substring(2297, 2316).trim();
						AMOUNT_2 = thisline.substring(2316, 2335).trim();
						EXPIRY_DATE = thisline.substring(2335, 2339).trim();
						TRACK2 = thisline.substring(2339, 2379).trim();
						PIN_OFST = thisline.substring(2379, 2395).trim();
						PRE_AUTH_SEQ_NUM = thisline.substring(2395, 2407).trim();
						INVOICE_NUM = thisline.substring(2407, 2417).trim();
						ORIG_INVOICE_NUM = thisline.substring(2417, 2427).trim();
						AUTHORIZER = thisline.substring(2427, 2443).trim();
						AUTH_IND = thisline.substring(2443, 2444).trim();
						SHIFT_NUM_2 = thisline.substring(2444, 2447).trim();
						BATCH_SEQ_NUM = thisline.substring(2447, 2450).trim();
						APPRV_CODE = thisline.substring(2450, 2458).trim();
						APPRV_CODE_LENGTH = thisline.substring(2458, 2459).trim();
						ICHG_RESP = thisline.substring(2459, 2467).trim();
						PSEUDO_TERM_ID = thisline.substring(2467, 2471).trim();
						RFRL_PHONE = thisline.substring(2471, 2491).trim();
						DUMMY_1 = thisline.substring(2491, 2494).trim();
						DFT_CAPTURE_FLAG = thisline.substring(2494, 2495).trim();
						SELT_FLAG = thisline.substring(2495, 2496).trim();
						RVRL_CODE = thisline.substring(2496, 2498).trim();
						REA_FOR_CHRGBCK = thisline.substring(2498, 2500).trim();
						NUM_OF_CHRGBCK = thisline.substring(2500, 2501).trim();
						PT_SRV_COND_CODE = thisline.substring(2501, 2503).trim();
						PT_SRV_ENTRY_MODE = thisline.substring(2503, 2506).trim();
						AUTH_IND2 = thisline.substring(2506, 2507).trim();
						ORIG_CRNCY_CODE = thisline.substring(2507, 2510).trim();
						MULTI_CRNY_AUTH_CRNCY_CODE = thisline.substring(2510, 2513).trim();
						MULTY_CRNCY_AUTH_CONV_RATE = thisline.substring(2513, 2521).trim();
						MULTI_CRNCY_SETL_CRNCY_CODE = thisline.substring(2521, 2525).trim();
						MULTI_CRNCY_SETL_CONV_RATE = thisline.substring(2525, 2533).trim();
						MULTI_CRNCY_CONV_DAT_TIME = thisline.substring(2533, 2552).trim();
						REFR_IMP_IND = thisline.substring(2552, 2553).trim();
						REFR_AVAIL_CR = thisline.substring(2553, 2554).trim();
						REFR_CR_LMT = thisline.substring(2554, 2555).trim();
						REFR_CR_BAL = thisline.substring(2555, 2556).trim();
						REFR_TTL = thisline.substring(2556, 2557).trim();
						REFR_CUR = thisline.substring(2557, 2558).trim();
						ADJ_SETL_IMPACT_FLAG = thisline.substring(2558, 2559).trim();
						REFR_IND = thisline.substring(2559, 2563).trim();
						FRWD_INST_ID_NUM = thisline.substring(2563, 2579).trim();
						CRD_ACCPT_ID_NUM = thisline.substring(2579, 2590).trim();
						CRD_ISS_ID_NUM = thisline.substring(2590, 2601).trim();
						ORIG_MSG_TYPE = thisline.substring(2601, 2605).trim();
						ORIG_TRAN_TIM = thisline.substring(2605, 2613).trim();
						ORIG_TRAN_DATE = thisline.substring(2613, 2619).trim();
						ORIG_SEQ_NUM = thisline.substring(2619, 2631).trim();
						ORIG_B24_POST_DATE = thisline.substring(2631, 2635).trim();
						EXCP_RSN_CODE = thisline.substring(2635, 2638).trim();
						OVRRDE_FLAG = thisline.substring(2638, 2639).trim();
						ADDR = thisline.substring(2639, 2659).trim();
						ZIP = thisline.substring(2659, 2668).trim();
						ADDR_VERFY_STAT = thisline.substring(2668, 2669).trim();
						PIN_IND = thisline.substring(2669, 2670).trim();
						PIN_TRIES = thisline.substring(2670, 2671).trim();
						if (thisline.trim().length() == 2676) {
							PRE_AUTH_TS = "";
							PRE_AUTH_HLDS_STAT = "";
							USER_FID2 = "";
							USER_DATA_D_LEN = "";
							USER_DATA_D_INFO = "";
						} else if (thisline.trim().length() == 2726) {
							PRE_AUTH_TS = "";
							PRE_AUTH_HLDS_STAT = "";
							USER_FID2 = "";
							USER_DATA_D_LEN = "";
							USER_DATA_D_INFO = "";
						} else {
							PRE_AUTH_TS = thisline.substring(2671, 2785).trim();
							PRE_AUTH_HLDS_STAT = thisline.substring(2785, 2786).trim();
							USER_FID2 = thisline.substring(2786, 2787).trim();
							USER_DATA_D_LEN = thisline.substring(2787, 2791).trim();
							USER_DATA_D_INFO = thisline.substring(2791, 2792).trim();
						}
						FILEDATE = setupBean.getFileDate();
						FILENAME = setupBean.getP_FILE_NAME();
						ps.setString(1, DATE_TIME);
						ps.setString(2, REC_TYP);
						ps.setString(3, CRD_LN);
						ps.setString(4, CRD_FIID);
						ps.setString(5, CRAD_NUM);
						ps.setString(6, CRD_MBR_NUM);
						ps.setString(7, RETL_KEY_IN);
						ps.setString(8, RDF_KEY);
						ps.setString(9, RDF_KEY_GRP);
						ps.setString(10, RDF_KEY_REGN);
						ps.setString(11, RDF_KEY_ID);
						ps.setString(12, TERM_ID);
						ps.setString(13, SHIFT_NUM);
						ps.setString(14, BATCH_NUM);
						ps.setString(15, TERM_IN);
						ps.setString(16, TERM_FIID);
						ps.setString(17, TERM_ID2);
						ps.setString(18, TERM_TIME);
						ps.setString(19, TERM_ID3);
						ps.setString(20, TKEY_RKEY_REC_FRMT);
						ps.setString(21, TKEY_RKEY_RETAILER_ID);
						ps.setString(22, TKEY_RKEY_CLERK_ID);
						ps.setString(23, DATA_FLAG);
						ps.setString(24, TYPE);
						ps.setString(25, RTE_STAT);
						ps.setString(26, ORIGINATOR);
						ps.setString(27, RESPONDER);
						ps.setString(28, ISS_CDE);
						ps.setString(29, ENTRY_TIME);
						ps.setString(30, EXIT_TIME);
						ps.setString(31, RE_ENTRY_TIME);
						ps.setString(32, TRAN_DATE);
						ps.setString(33, TRAN_TIM);
						ps.setString(34, POST_DAT);
						ps.setString(35, ACQ_ICHG_SETL_DAT);
						ps.setString(36, ISS_ICHG_SETL_DAT);
						ps.setString(37, SEQ_NUM);
						ps.setString(38, TERM_NAME_LOC);
						ps.setString(39, TERM_OWNER_NAME);
						ps.setString(40, TERM_CITY);
						ps.setString(41, TERM_ST);
						ps.setString(42, TERM_CNTRY_CDE);
						ps.setString(43, BRCH_ID);
						ps.setString(44, USER_FID);
						ps.setString(45, TERM_TIM_OFST);
						ps.setString(46, ACQ_INST_ID_NUM);
						ps.setString(47, RCV_INST_ID_NUM);
						ps.setString(48, TERM_TYPE);
						ps.setString(49, CLERK_ID);
						ps.setString(50, CTR_AUTH);
						ps.setString(51, CTR_AUTH_GRP);
						ps.setString(52, CTR_AUTH_USER_ID);
						ps.setString(53, RETL_SIC_CDE);
						ps.setString(54, ORIG);
						ps.setString(55, DEST);
						ps.setString(56, TRAN_CDE);
						ps.setString(57, CRD_TYPE);
						ps.setString(58, ACCT);
						ps.setString(59, RESP_CDE);
						ps.setString(60, AMOUNT_1);
						ps.setString(61, AMOUNT_2);
						ps.setString(62, EXPIRY_DATE);
						ps.setString(63, TRACK2);
						ps.setString(64, PIN_OFST);
						ps.setString(65, PRE_AUTH_SEQ_NUM);
						ps.setString(66, INVOICE_NUM);
						ps.setString(67, ORIG_INVOICE_NUM);
						ps.setString(68, AUTHORIZER);
						ps.setString(69, AUTH_IND);
						ps.setString(70, SHIFT_NUM_2);
						ps.setString(71, BATCH_SEQ_NUM);
						ps.setString(72, APPRV_CODE);
						ps.setString(73, APPRV_CODE_LENGTH);
						ps.setString(74, ICHG_RESP);
						ps.setString(75, PSEUDO_TERM_ID);
						ps.setString(76, RFRL_PHONE);
						ps.setString(77, DUMMY_1);
						ps.setString(78, DFT_CAPTURE_FLAG);
						ps.setString(79, SELT_FLAG);
						ps.setString(80, RVRL_CODE);
						ps.setString(81, REA_FOR_CHRGBCK);
						ps.setString(82, NUM_OF_CHRGBCK);
						ps.setString(83, PT_SRV_COND_CODE);
						ps.setString(84, PT_SRV_ENTRY_MODE);
						ps.setString(85, AUTH_IND2);
						ps.setString(86, ORIG_CRNCY_CODE);
						ps.setString(87, MULTI_CRNY_AUTH_CRNCY_CODE);
						ps.setString(88, MULTY_CRNCY_AUTH_CONV_RATE);
						ps.setString(89, MULTI_CRNCY_SETL_CRNCY_CODE);
						ps.setString(90, MULTI_CRNCY_SETL_CONV_RATE);
						ps.setString(91, MULTI_CRNCY_CONV_DAT_TIME);
						ps.setString(92, REFR_IMP_IND);
						ps.setString(93, REFR_AVAIL_CR);
						ps.setString(94, REFR_CR_LMT);
						ps.setString(95, REFR_CR_BAL);
						ps.setString(96, REFR_TTL);
						ps.setString(97, REFR_CUR);
						ps.setString(98, ADJ_SETL_IMPACT_FLAG);
						ps.setString(99, REFR_IND);
						ps.setString(100, FRWD_INST_ID_NUM);
						ps.setString(101, CRD_ACCPT_ID_NUM);
						ps.setString(102, CRD_ISS_ID_NUM);
						ps.setString(103, ORIG_MSG_TYPE);
						ps.setString(104, ORIG_TRAN_TIM);
						ps.setString(105, ORIG_TRAN_DATE);
						ps.setString(106, ORIG_SEQ_NUM);
						ps.setString(107, ORIG_B24_POST_DATE);
						ps.setString(108, EXCP_RSN_CODE);
						ps.setString(109, OVRRDE_FLAG);
						ps.setString(110, ADDR);
						ps.setString(111, ZIP);
						ps.setString(112, ADDR_VERFY_STAT);
						ps.setString(113, PIN_IND);
						ps.setString(114, PIN_TRIES);
						ps.setString(115, PRE_AUTH_TS);
						ps.setString(116, PRE_AUTH_HLDS_STAT);
						ps.setString(117, USER_FID2);
						ps.setString(118, USER_DATA_D_LEN);
						ps.setString(119, USER_DATA_D_INFO);
						ps.setString(120, DCRS_REMARKS);
						ps.setString(121, FILEDATE);
						ps.setString(122, FILENAME);
						count++;
						beforecount++;
						ps.addBatch();
					}
				}
				if (++batchCount % batchSize == 0) {
					batchNumber++;
					System.out.println("Batch Executed is " + batchNumber);
					ps.executeBatch();
					con.commit();
				}
			}
			this.logger.info("Executed Batch SuccessFully");
			ps.executeBatch();
			con.commit();
			
			
			if (count > 0) {
				updatps.setString(1, FILENAME);
				updatps.setString(2, FILEDATE);
				updatps.setString(3, String.valueOf(beforecount));
				updatps.setString(4, "POS");
				updatps.execute();
				con.commit();
				this.logger.info("update Batch Completed");
			}
			output.put("result", Boolean.valueOf(true));
			output.put("msg", "File Uploaded and Record count is " + beforecount);
			this.logger.info("Completed process " + beforecount);
			con.close();
			return output;
		} catch (Exception e) {
			this.logger.info("Exception in lineNumber " + beforecount);
			this.logger.info("Exception in ReadUCOATMSwitchData " + e);
			output.put("result", Boolean.valueOf(false));
			output.put("msg", "Issue at Line Number " + beforecount);
			return output;
		}
	}

	public boolean uploadSwitchData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {
		this.logger.info("uploadSwitchData method ak");
		String stLine = null;
		int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0;
		boolean batchExecuted = false;
		String InsertQuery = "INSERT INTO SWITCH_RAWDATA(PAN,FPAN, ACCOUNTNO, ACCTNUM, BRANCHCODE, MSGTYPE, AMOUNT, ISS_CURRENCY_CODE, LOCAL_DATE, LOCAL_TIME, ACQUIRER, PAN2, TERMID, TRACE, ISSUER, AUTHNUM, RESPCODE, TXNSRC, TXNDEST,CODE_1,CODE_2, ACQ_CURRENCY_CODE, AMOUNT_EQUIV, NETWORK, MERCHANT_TYPE, CREATEDBY,FILE_NAME, FILEDATE) VALUES(?, ibkl_encrypt_decrypt.ibkl_set_encrypt_val(?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?,?,?, ?, ?, ?,?,?,?, ?, ? , TO_DATE(?,'DD/MM/YYYY'))";
		String delQuery = "delete from CBS_UCO_RAWDATA_TEMP";
		try {
			String fileNameWithExt = file.getOriginalFilename();
			int lastDotIndex = fileNameWithExt.lastIndexOf(".");
			String fileNameWithoutExt = "";
			if (lastDotIndex > 0)
				fileNameWithoutExt = fileNameWithExt.substring(0, lastDotIndex);
			File folder = new File(OUTPUT_FOLDER);
			if (folder.exists())
				folder.delete();
			folder.mkdir();
			File file1 = new File(folder, fileNameWithoutExt);
			try {
				if (file1.exists())
					file1.delete();
				file1.createNewFile();
			} catch (Exception e) {
				System.out.println(e);
			}
			String newPath = String.valueOf(OUTPUT_FOLDER) + File.separator + fileNameWithoutExt;
			System.out.println("File to be write at path: " + newPath);
			FileOutputStream out = new FileOutputStream(newPath);
			File outFile = new File(newPath);
			System.out.println("File reading from Path: " + newPath);
			System.out.println("File reading : " + outFile);
			PreparedStatement delpst = con.prepareStatement(delQuery);
			delpst.execute();
			BufferedReader br = new BufferedReader(new FileReader(outFile));
			try {
				PreparedStatement ps = con.prepareStatement(InsertQuery);
				while ((stLine = br.readLine()) != null) {
					lineNumber++;
					batchExecuted = false;
					sr_no = 1;
					String[] splitData = stLine.split("\\^");
					for (int i = 0; i <= splitData.length - 1; i++) {
						if (i != 6 && i != 13 && i != 16 && i != 25 && i != 26 && i != 27 && i != 28 && i != 29
								&& i != 30 && i != 33)
							if (i == 0) {
								String cardNumber = formatCardNumber(splitData[i].trim());
								ps.setString(sr_no++, cardNumber);
								ps.setString(sr_no++, splitData[i].trim());
							} else {
								ps.setString(sr_no++, splitData[i].trim());
							}
					}
					ps.setString(sr_no++, setupBean.getCreatedBy());
					ps.setString(sr_no++, file.getOriginalFilename());
					ps.setString(sr_no++, setupBean.getFileDate());
					ps.addBatch();
					batchSize++;
					if (batchSize == 10000) {
						batchNumber++;
						System.out.println("Batch Executed is " + batchNumber);
						ps.executeBatch();
						batchSize = 0;
						batchExecuted = true;
					}
				}
				if (!batchExecuted) {
					batchNumber++;
					System.out.println("Batch Executed is " + batchNumber);
					ps.executeBatch();
				}
				br.close();
				ps.close();
				System.out.println("Reading data " + (new Date()).toString());
			} catch (IOException e) {
				System.out.println("exception for converting is " + e);
			}
			return true;
		} catch (Exception e) {
			System.out.println("Issue at line " + stLine);
			System.out.println("Exception in uploadISSData " + e);
			return false;
		}
	}

	private String formatCardNumber(String cardNumber) {
		int totalLength = cardNumber.length();
		int firstSixDigitLength = Math.min(totalLength, 6);
		int lastFourDigitLength = Math.min(totalLength - 10, 4);
		String firstSixDigits = cardNumber.substring(0, firstSixDigitLength);
		String midX = "XXXXXX";
		String lastFourDigits = cardNumber.substring(totalLength - lastFourDigitLength);
		StringBuilder xx = new StringBuilder();
		for (int i = 0; i < totalLength - 10; i++)
			xx.append("X");
		String formatedCardNumber = String.valueOf(firstSixDigits) + xx + lastFourDigits;
		return formatedCardNumber;
	}
}
