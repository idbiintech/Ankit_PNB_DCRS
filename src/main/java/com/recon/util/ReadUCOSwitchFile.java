/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.recon.util;

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

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;

/**
 *
 * @author int6261
 */
public class ReadUCOSwitchFile extends JdbcDaoSupport {

	public static final String OUTPUT_FOLDER = System.getProperty("catalina.home") + File.separator + "OUTPUT_FOLDER";

	PlatformTransactionManager transactionManager;
	Connection con;
	Statement st;

	public void setTransactionManager() {
		logger.info("***** ReadSwitchFile.setTransactionManager Start ****");
		try {

			ApplicationContext context = new ClassPathXmlApplicationContext();
			context = new ClassPathXmlApplicationContext("/resources/bean.xml");

			logger.info("in settransactionManager");
			transactionManager = (PlatformTransactionManager) context.getBean("transactionManager");
			logger.info(" settransactionManager completed");

			logger.info("***** ReadSwitchFile.setTransactionManager End ****");

			((ClassPathXmlApplicationContext) context).close();
		} catch (Exception ex) {

			logger.error(" error in ReadSwitchFile.setTransactionManager",
					new Exception("ReadSwitchFile.setTransactionManager", ex));
		}

	}

	public HashMap<String, Object> uploadATMSwitchData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {
		logger.info("uploadATMSwitchData method Called " + setupBean.getFILEDATE());
		HashMap<String, Object> output = new HashMap<String, Object>();
		String thisline = null;
		int lineNumber = 0, feesize = 1;
		int sr_no = 1, batchNumber = 0, batchSize = 100000, batchNumber1 = 0, batchcount = 0;
		int count = 1;
		int countt = 0;
		String InsertQuery = "INSERT INTO SWITCH_ATM_RAWDATA (DATE_TIME, REC_TYPE, AUTH_PPD,  TERM_LN, TERM_FIID, TERM_TERM_ID,  CRD_LN, CRD_FIID,CRD_PAN, CRD_MBR_NUM, BRCH_ID, REGN_ID, USER_FLD1X, TYP_CDE, TYP, RTE_STAT,ORIGINATOR, RESPONDER, ENTRY_TIME, EXIT_TIME, RE_ENTRY_TIME, TRAN_DATE, TRAN_TIM, POST_DAT, ACQ_ICHG_SETL_DAT, ISS_ICHG_SETL_DAT, SEQ_NUM, TERM_TYP, TIM_OFST, ACQ_INST_ID_NUM, RCV_INST_ID_NUM, TRAN_CDE, FROM_ACCT, USER_FLD1, TO_ACCT, MULT_ACCT, AMT_1, AMT_2, AMT_3, DEP_BAL_CR, DEP_TYP, RESP_CDE, TERM_NAME_LOC, TERM_OWNER_NAME, TERM_CITY, TERM_ST, TERM_CNTRY, ORIG_OSEQ_NUM, ORIG_OTRAN_DAT, ORIG_OTRAN_TIM, ORIG_B24_POST, ORIG_CRNCY_CDE,  MULT_CRNCY_AUTH_CRNCY_CDE, MULT_CRNCY_AUTH_CONV_RATE, MULT_CRNCY_SETL_CRNCY_CDE, MULT_CRNCY_SETL_CONV_RATE, MULT_CRNCY_CONV_DAT_TIM, RVSL_RSN, PIN_OFST, SHRG_GRP, DEST_ORDER, AUTH_ID_RESP, REFR_IMP_IND, REFR_AVAIL_IMP, REFR_LEDG_IMP, REFR_HLD_AMT_IMP, REFR_CAF_REFR_IND, REFR_USER_FLD3, DEP_SETL_IMP_FLG, ADJ_SETL_IMP_FLG, REFR_IND, USER_FLD4, FRWD_INST_ID_NUM, CRD_ACCPT_ID_NUM, CRD_ISS_ID_NUM, USER_FLD6,DCRS_REMARKS,FILEDATE,FILENAME)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,TO_DATE(?,'DD-MM-YY'),?)";
		String InsertQuery2 = "INSERT INTO SWITCH_ATM_RAWDATA (DATE_TIME, REC_TYPE, AUTH_PPD,  TERM_LN, TERM_FIID, TERM_TERM_ID,  CRD_LN, CRD_FIID,CRD_PAN, CRD_MBR_NUM, BRCH_ID, REGN_ID, USER_FLD1X, TYP_CDE, TYP, RTE_STAT,ORIGINATOR, RESPONDER, ENTRY_TIME, EXIT_TIME, RE_ENTRY_TIME, TRAN_DATE, TRAN_TIM, POST_DAT, ACQ_ICHG_SETL_DAT, ISS_ICHG_SETL_DAT, SEQ_NUM, TERM_TYP, TIM_OFST, ACQ_INST_ID_NUM, RCV_INST_ID_NUM, TRAN_CDE, FROM_ACCT, USER_FLD1, TO_ACCT, MULT_ACCT, AMT_1, AMT_2, AMT_3, DEP_BAL_CR, DEP_TYP, RESP_CDE, TERM_NAME_LOC, TERM_OWNER_NAME, TERM_CITY, TERM_ST, TERM_CNTRY, ORIG_OSEQ_NUM, ORIG_OTRAN_DAT, ORIG_OTRAN_TIM, ORIG_B24_POST, ORIG_CRNCY_CDE,  MULT_CRNCY_AUTH_CRNCY_CDE, MULT_CRNCY_AUTH_CONV_RATE, MULT_CRNCY_SETL_CRNCY_CDE, MULT_CRNCY_SETL_CONV_RATE, MULT_CRNCY_CONV_DAT_TIM, RVSL_RSN, PIN_OFST, SHRG_GRP, DEST_ORDER, AUTH_ID_RESP, REFR_IMP_IND, REFR_AVAIL_IMP, REFR_LEDG_IMP, REFR_HLD_AMT_IMP, REFR_CAF_REFR_IND, REFR_USER_FLD3, DEP_SETL_IMP_FLG, ADJ_SETL_IMP_FLG, REFR_IND, USER_FLD4, FRWD_INST_ID_NUM, CRD_ACCPT_ID_NUM, CRD_ISS_ID_NUM, USER_FLD6,DCRS_REMARKS,FILEDATE,FILENAME)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,TO_DATE(?,'DD-MM-YY'),?)";
		String InsertQuery3 = "INSERT INTO SWITCH_ATM_RAWDATA (DATE_TIME, REC_TYPE, AUTH_PPD,  TERM_LN, TERM_FIID, TERM_TERM_ID,  CRD_LN, CRD_FIID,CRD_PAN, CRD_MBR_NUM, BRCH_ID, REGN_ID, USER_FLD1X, TYP_CDE, TYP, RTE_STAT,ORIGINATOR, RESPONDER, ENTRY_TIME, EXIT_TIME, RE_ENTRY_TIME, TRAN_DATE, TRAN_TIM, POST_DAT, ACQ_ICHG_SETL_DAT, ISS_ICHG_SETL_DAT, SEQ_NUM, TERM_TYP, TIM_OFST, ACQ_INST_ID_NUM, RCV_INST_ID_NUM, TRAN_CDE, FROM_ACCT, USER_FLD1, TO_ACCT, MULT_ACCT, AMT_1, AMT_2, AMT_3, DEP_BAL_CR, DEP_TYP, RESP_CDE, TERM_NAME_LOC, TERM_OWNER_NAME, TERM_CITY, TERM_ST, TERM_CNTRY, ORIG_OSEQ_NUM, ORIG_OTRAN_DAT, ORIG_OTRAN_TIM, ORIG_B24_POST, ORIG_CRNCY_CDE,  MULT_CRNCY_AUTH_CRNCY_CDE, MULT_CRNCY_AUTH_CONV_RATE, MULT_CRNCY_SETL_CRNCY_CDE, MULT_CRNCY_SETL_CONV_RATE, MULT_CRNCY_CONV_DAT_TIM, RVSL_RSN, PIN_OFST, SHRG_GRP, DEST_ORDER, AUTH_ID_RESP, REFR_IMP_IND, REFR_AVAIL_IMP, REFR_LEDG_IMP, REFR_HLD_AMT_IMP, REFR_CAF_REFR_IND, REFR_USER_FLD3, DEP_SETL_IMP_FLG, ADJ_SETL_IMP_FLG, REFR_IND, USER_FLD4, FRWD_INST_ID_NUM, CRD_ACCPT_ID_NUM, CRD_ISS_ID_NUM, USER_FLD6,DCRS_REMARKS,FILEDATE,FILENAME)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,TO_DATE(?,'DD-MM-YY'),?)";

		String update_query = "INSERT INTO SWITCH_DATA_VALIDATION(FILENAME,FILEDATE,COUNT,FILETYPE) VALUES(?,?,?,?)";

		String secondRead = "", THIRDRead = "", TRAN_CDE_R = "", DATE_TIME = "", REC_TYPE = "", AUTH_PPD = "",
				RTE_STAT = "", TERM = "", TERM_LN = "", TERM_FIID = "", TERM_TERM_ID = "", CRD = "", CRD_LN = "",
				CRD_FIID = "", CRD_PAN = "", CRD_MBR_NUM = "", BRCH_ID = "", REGN_ID = "", USER_FLD1X = "", AUTHX = "",
				TYP_CDE = "", TYP = "", ORIGINATOR = "", RESPONDER = "", ENTRY_TIME = "", EXIT_TIME = "",
				RE_ENTRY_TIME = "", TRAN_DATE = "", TRAN_TIM = "", POST_DAT = "", ACQ_ICHG_SETL_DAT = "",
				ISS_ICHG_SETL_DAT = "", SEQ_NUM = "", TERM_TYP = "", TIM_OFST = "", ACQ_INST_ID_NUM = "",
				RCV_INST_ID_NUM = "", TRAN_CDE = "", FROM_ACCT = "", USER_FLD1 = "", TO_ACCT = "", MULT_ACCT = "",
				AMT_1 = "", AMT_2 = "", AMT_3 = "", DEP_BAL_CR = "", DEP_TYP = "", RESP_CDE = "", TERM_NAME_LOC = "",
				TERM_OWNER_NAME = "", TERM_CITY = "", TERM_ST = "", TERM_CNTRY = "", ORIG = "", ORIG_OSEQ_NUM = "",
				ORIG_OTRAN_DAT = "", ORIG_OTRAN_TIM = "", ORIG_B24_POST = "", ORIG_CRNCY_CDE = "", USER_FLD2 = "",
				MULT_CRNCY = "", MULT_CRNCY_AUTH_CRNCY_CDE = "", MULT_CRNCY_AUTH_CONV_RATE = "",
				MULT_CRNCY_SETL_CRNCY_CDE = "", MULT_CRNCY_SETL_CONV_RATE = "", MULT_CRNCY_CONV_DAT_TIM = "",
				RVSL_RSN = "", PIN_OFST = "", SHRG_GRP = "", DEST_ORDER = "", AUTH_ID_RESP = "", REFR_IMP_IND = "",
				REFR_AVAIL_IMP = "", REFR_LEDG_IMP = "", REFR_HLD_AMT_IMP = "", REFR_CAF_REFR_IND = "",
				REFR_USER_FLD3 = "", DEP_SETL_IMP_FLG = "", ADJ_SETL_IMP_FLG = "", REFR_IND = "", REFR_IND_PBF1 = "",
				REFR_IND_PBF2 = "", REFR_IND_PBF3 = "", REFR_IND_PBF4 = "", USER_FLD4 = "", FRWD_INST_ID_NUM = "",
				CRD_ACCPT_ID_NUM = "", CRD_ISS_ID_NUM = "", USER_FLD6 = "", FILEDATE = "", FILENAME = "";

		try {
			con.setAutoCommit(false);
			PreparedStatement updatps = con.prepareStatement(update_query);

			PreparedStatement ps = con.prepareStatement(InsertQuery);
			PreparedStatement ps2 = con.prepareStatement(InsertQuery2);
			PreparedStatement ps3 = con.prepareStatement(InsertQuery3);

			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));

			while ((thisline = br.readLine()) != null) {
				//System.out.println("length "+thisline.trim().length());
				//System.out.println("data  "+thisline.trim());
				if (count == 1 && thisline.trim().length() == 3211) {
					// System.out.println("count");
					String pat = "[^\\w ]";
					thisline = thisline.trim().replaceAll(pat, " ");

					thisline = thisline.trim().replaceAll(thisline.substring(5, 153), "");

				}

				if (thisline.trim().length() == 0 || thisline.trim().length() == 5 || thisline.trim().length() == 176
						|| thisline.trim().length() == 91) {

					continue;
				} else {

					if (thisline.trim().length() != 891 || thisline.trim().length() != 1284
							|| thisline.trim().length() != 896) {
						DATE_TIME = thisline.substring(14, 33).trim();
						REC_TYPE = thisline.substring(33, 35).trim();
						AUTH_PPD = thisline.substring(35, 39).trim();
						// TERM = thisline.substring(35, 39).trim();
						TERM_LN = thisline.substring(39, 43).trim();
						TERM_FIID = thisline.substring(43, 47).trim();
						TERM_TERM_ID = thisline.substring(47, 63).trim();
						// CRD = thisline.substring(47, 62).trim();
						CRD_LN = thisline.substring(63, 67).trim();
						CRD_FIID = thisline.substring(67, 71).trim();
						CRD_PAN = thisline.substring(71, 90).trim();
						CRD_MBR_NUM = thisline.substring(90, 93).trim();
						BRCH_ID = thisline.substring(93, 97).trim();
						REGN_ID = thisline.substring(97, 101).trim();
						USER_FLD1X = thisline.substring(101, 103).trim();
						// AUTHX = thisline.substring(87, 89).trim();
						TYP_CDE = thisline.substring(103, 105).trim();
						TYP = thisline.substring(105, 109).trim();
						RTE_STAT = thisline.substring(109, 111).trim();
						ORIGINATOR = thisline.substring(111, 112).trim();
						RESPONDER = thisline.substring(112, 113).trim();
						ENTRY_TIME = thisline.substring(113, 132).trim();
						EXIT_TIME = thisline.substring(132, 151).trim();
						RE_ENTRY_TIME = thisline.substring(151, 170).trim();
						TRAN_DATE = thisline.substring(170, 176).trim();
						TRAN_TIM = thisline.substring(176, 184).trim();
						POST_DAT = thisline.substring(184, 190).trim();
						ACQ_ICHG_SETL_DAT = thisline.substring(190, 196).trim();
						ISS_ICHG_SETL_DAT = thisline.substring(196, 202).trim();
						SEQ_NUM = thisline.substring(202, 214).trim();
						TERM_TYP = thisline.substring(214, 216).trim();
						TIM_OFST = thisline.substring(216, 221).trim();
						ACQ_INST_ID_NUM = thisline.substring(221, 232).trim();
						RCV_INST_ID_NUM = thisline.substring(232, 243).trim();
						TRAN_CDE = thisline.substring(243, 249).trim();
						// TRAN_CDE_R = thisline.substring(249, 255).trim();

						FROM_ACCT = thisline.substring(249, 268).trim();// ccc
						USER_FLD1 = thisline.substring(268, 269).trim();
						TO_ACCT = thisline.substring(269, 288).trim();
						MULT_ACCT = thisline.substring(288, 289).trim();
						AMT_1 = thisline.substring(289, 308).trim();
						AMT_2 = thisline.substring(308, 327).trim();
						AMT_3 = thisline.substring(327, 346).trim();
						DEP_BAL_CR = thisline.substring(346, 356).trim();
						DEP_TYP = thisline.substring(356, 357).trim();
						RESP_CDE = thisline.substring(357, 360).trim();
						/// need to add 4
						TERM_NAME_LOC = thisline.substring(360, 385).trim();
						TERM_OWNER_NAME = thisline.substring(385, 407).trim();
						TERM_CITY = thisline.substring(407, 420).trim();
						TERM_ST = thisline.substring(420, 423).trim();
						TERM_CNTRY = thisline.substring(423, 425).trim();
						// ORIG = thisline.substring(427, 455).trim();
						ORIG_OSEQ_NUM = thisline.substring(425, 437).trim();
						ORIG_OTRAN_DAT = thisline.substring(437, 441).trim();
						ORIG_OTRAN_TIM = thisline.substring(441, 449).trim();
						ORIG_B24_POST = thisline.substring(449, 453).trim();
						ORIG_CRNCY_CDE = thisline.substring(453, 456).trim();
						/* USER_FLD2 = thisline.substring(456, 497).trim(); */
						// MULT_CRNCY = thisline.substring(497, 538).trim(); //////not
						// sure
						MULT_CRNCY_AUTH_CRNCY_CDE = thisline.substring(456, 459).trim();
						MULT_CRNCY_AUTH_CONV_RATE = thisline.substring(459, 467).trim();
						MULT_CRNCY_SETL_CRNCY_CDE = thisline.substring(467, 470).trim();
						MULT_CRNCY_SETL_CONV_RATE = thisline.substring(470, 478).trim();
						MULT_CRNCY_CONV_DAT_TIM = thisline.substring(478, 497).trim();
						RVSL_RSN = thisline.substring(497, 499).trim();
						PIN_OFST = thisline.substring(499, 515).trim();///
						SHRG_GRP = thisline.substring(515, 516).trim();
						DEST_ORDER = thisline.substring(516, 517).trim();
						AUTH_ID_RESP = thisline.substring(517, 523).trim();
						REFR_IMP_IND = thisline.substring(523, 524).trim();
						REFR_AVAIL_IMP = thisline.substring(524, 526).trim();
						REFR_LEDG_IMP = thisline.substring(526, 528).trim();
						REFR_HLD_AMT_IMP = thisline.substring(528, 530).trim();
						REFR_CAF_REFR_IND = thisline.substring(530, 531).trim();
						REFR_USER_FLD3 = thisline.substring(531, 532).trim();
						DEP_SETL_IMP_FLG = thisline.substring(532, 533).trim();
						ADJ_SETL_IMP_FLG = thisline.substring(533, 534).trim();
						REFR_IND = thisline.substring(534, 538).trim();
						/*
						 * REFR_IND_PBF1 = thisline.substring(520, 521).trim(); REFR_IND_PBF2 =
						 * thisline.substring(521, 522).trim(); REFR_IND_PBF3 = thisline.substring(522,
						 * 523).trim(); REFR_IND_PBF4 = thisline.substring(523, 524).trim();
						 */
						USER_FLD4 = thisline.substring(538, 554).trim();
						FRWD_INST_ID_NUM = thisline.substring(554, 565).trim();
						CRD_ACCPT_ID_NUM = thisline.substring(565, 576).trim();
						CRD_ISS_ID_NUM = thisline.substring(576, 587).trim();
						USER_FLD6 = thisline.substring(587, 588).trim();
						FILEDATE = setupBean.getFileDate();
						FILENAME = setupBean.getP_FILE_NAME();

						ps.setString(1, DATE_TIME);
						ps.setString(2, REC_TYPE);
						ps.setString(3, AUTH_PPD);
						// ps.setString(4, TERM);
						ps.setString(4, TERM_LN);
						ps.setString(5, TERM_FIID);
						ps.setString(6, TERM_TERM_ID);
						// ps.setString(8, CRD);
						ps.setString(7, CRD_LN);
						ps.setString(8, CRD_FIID);
						ps.setString(9, CRD_PAN);
						ps.setString(10, CRD_MBR_NUM);
						ps.setString(11, BRCH_ID);
						ps.setString(12, REGN_ID);
						ps.setString(13, USER_FLD1X);
						// ps.setString(16, AUTHX);
						ps.setString(14, TYP_CDE);
						ps.setString(15, TYP);
						ps.setString(16, RTE_STAT);
						ps.setString(17, ORIGINATOR);
						ps.setString(18, RESPONDER);
						ps.setString(19, ENTRY_TIME);
						ps.setString(20, EXIT_TIME);
						ps.setString(21, RE_ENTRY_TIME);
						ps.setString(22, TRAN_DATE);
						ps.setString(23, TRAN_TIM);
						ps.setString(24, POST_DAT);
						ps.setString(25, ACQ_ICHG_SETL_DAT);
						ps.setString(26, ISS_ICHG_SETL_DAT);
						ps.setString(27, SEQ_NUM);
						ps.setString(28, TERM_TYP);
						ps.setString(29, TIM_OFST);
						ps.setString(30, ACQ_INST_ID_NUM);
						ps.setString(31, RCV_INST_ID_NUM);
						ps.setString(32, TRAN_CDE);
						// ps.setString(33, TRAN_CDE_R);

						ps.setString(33, FROM_ACCT);
						ps.setString(34, USER_FLD1);
						ps.setString(35, TO_ACCT);
						ps.setString(36, MULT_ACCT);
						ps.setString(37, AMT_1);
						ps.setString(38, AMT_2);
						ps.setString(39, AMT_3);
						ps.setString(40, DEP_BAL_CR);
						ps.setString(41, DEP_TYP);
						ps.setString(42, RESP_CDE);
						ps.setString(43, TERM_NAME_LOC);
						ps.setString(44, TERM_OWNER_NAME);
						ps.setString(45, TERM_CITY);
						ps.setString(46, TERM_ST);
						ps.setString(47, TERM_CNTRY);
						/* ps.setString(49, ORIG); */
						ps.setString(48, ORIG_OSEQ_NUM);
						ps.setString(49, ORIG_OTRAN_DAT);
						ps.setString(50, ORIG_OTRAN_TIM);
						ps.setString(51, ORIG_B24_POST);
						ps.setString(52, ORIG_CRNCY_CDE);
						/* ps.setString(55, USER_FLD2); */
						// ps.setString(56, MULT_CRNCY);
						ps.setString(53, MULT_CRNCY_AUTH_CRNCY_CDE);
						ps.setString(54, MULT_CRNCY_AUTH_CONV_RATE);
						ps.setString(55, MULT_CRNCY_SETL_CRNCY_CDE);
						ps.setString(56, MULT_CRNCY_SETL_CONV_RATE);
						ps.setString(57, MULT_CRNCY_CONV_DAT_TIM);
						ps.setString(58, RVSL_RSN);
						ps.setString(59, PIN_OFST);
						ps.setString(60, SHRG_GRP);
						ps.setString(61, DEST_ORDER);
						ps.setString(62, AUTH_ID_RESP);
						ps.setString(63, REFR_IMP_IND);
						ps.setString(64, REFR_AVAIL_IMP);
						ps.setString(65, REFR_LEDG_IMP);
						ps.setString(66, REFR_HLD_AMT_IMP);
						ps.setString(67, REFR_CAF_REFR_IND);
						ps.setString(68, REFR_USER_FLD3);
						ps.setString(69, DEP_SETL_IMP_FLG);
						ps.setString(70, ADJ_SETL_IMP_FLG);
						ps.setString(71, REFR_IND);
						ps.setString(72, USER_FLD4);
						ps.setString(73, FRWD_INST_ID_NUM);
						ps.setString(74, CRD_ACCPT_ID_NUM);
						ps.setString(75, CRD_ISS_ID_NUM);
						ps.setString(76, USER_FLD6);
						ps.setString(77, "");
						ps.setString(78, FILEDATE);
						ps.setString(79, FILENAME);
						// System.out.println("qq ");

						ps.addBatch();

						// count++;

					}

					// System.out.println("thisline.trim().length() "+ thisline.trim().length());
					if (thisline.trim().length() == 891 || thisline.trim().length() == 1284
							|| thisline.trim().length() == 896) {

						DATE_TIME = thisline.substring(14, 33).trim();
						REC_TYPE = thisline.substring(33, 35).trim();
						AUTH_PPD = thisline.substring(35, 39).trim();
						// TERM = thisline.substring(35, 39).trim();
						TERM_LN = thisline.substring(39, 43).trim();
						TERM_FIID = thisline.substring(43, 47).trim();
						TERM_TERM_ID = thisline.substring(47, 63).trim();
						// CRD = thisline.substring(47, 62).trim();
						CRD_LN = thisline.substring(63, 67).trim();
						CRD_FIID = thisline.substring(67, 71).trim();
						CRD_PAN = thisline.substring(71, 90).trim();
						CRD_MBR_NUM = thisline.substring(90, 93).trim();
						BRCH_ID = thisline.substring(93, 97).trim();
						REGN_ID = thisline.substring(97, 101).trim();
						USER_FLD1X = thisline.substring(101, 103).trim();
						// AUTHX = thisline.substring(87, 89).trim();
						TYP_CDE = thisline.substring(103, 105).trim();
						TYP = thisline.substring(105, 109).trim();
						RTE_STAT = thisline.substring(109, 111).trim();
						ORIGINATOR = thisline.substring(111, 112).trim();
						RESPONDER = thisline.substring(112, 113).trim();
						ENTRY_TIME = thisline.substring(113, 132).trim();
						EXIT_TIME = thisline.substring(132, 151).trim();
						RE_ENTRY_TIME = thisline.substring(151, 170).trim();
						TRAN_DATE = thisline.substring(170, 176).trim();
						TRAN_TIM = thisline.substring(176, 184).trim();
						POST_DAT = thisline.substring(184, 190).trim();
						ACQ_ICHG_SETL_DAT = thisline.substring(190, 196).trim();
						ISS_ICHG_SETL_DAT = thisline.substring(196, 202).trim();
						SEQ_NUM = thisline.substring(202, 214).trim();
						TERM_TYP = thisline.substring(214, 216).trim();
						TIM_OFST = thisline.substring(216, 221).trim();
						ACQ_INST_ID_NUM = thisline.substring(221, 232).trim();
						RCV_INST_ID_NUM = thisline.substring(232, 243).trim();
						TRAN_CDE = thisline.substring(243, 249).trim();
						// TRAN_CDE_R = thisline.substring(249, 255).trim();

						FROM_ACCT = thisline.substring(249, 268).trim();// ccc
						USER_FLD1 = thisline.substring(268, 269).trim();
						TO_ACCT = thisline.substring(269, 288).trim();
						MULT_ACCT = thisline.substring(288, 289).trim();
						AMT_1 = thisline.substring(289, 308).trim();
						AMT_2 = thisline.substring(308, 327).trim();
						AMT_3 = thisline.substring(327, 346).trim();
						DEP_BAL_CR = thisline.substring(346, 356).trim();
						DEP_TYP = thisline.substring(356, 357).trim();
						RESP_CDE = thisline.substring(357, 360).trim();
						/// need to add 4
						TERM_NAME_LOC = thisline.substring(360, 385).trim();
						TERM_OWNER_NAME = thisline.substring(385, 407).trim();
						TERM_CITY = thisline.substring(407, 420).trim();
						TERM_ST = thisline.substring(420, 423).trim();
						TERM_CNTRY = thisline.substring(423, 425).trim();
						// ORIG = thisline.substring(427, 455).trim();
						ORIG_OSEQ_NUM = thisline.substring(425, 437).trim();
						ORIG_OTRAN_DAT = thisline.substring(437, 441).trim();
						ORIG_OTRAN_TIM = thisline.substring(441, 449).trim();
						ORIG_B24_POST = thisline.substring(449, 453).trim();
						ORIG_CRNCY_CDE = thisline.substring(453, 456).trim();
						/* USER_FLD2 = thisline.substring(456, 497).trim(); */
						// MULT_CRNCY = thisline.substring(497, 538).trim(); //////not
						// sure
						MULT_CRNCY_AUTH_CRNCY_CDE = thisline.substring(456, 459).trim();
						MULT_CRNCY_AUTH_CONV_RATE = thisline.substring(459, 467).trim();
						MULT_CRNCY_SETL_CRNCY_CDE = thisline.substring(467, 470).trim();
						MULT_CRNCY_SETL_CONV_RATE = thisline.substring(470, 478).trim();
						MULT_CRNCY_CONV_DAT_TIM = thisline.substring(478, 497).trim();
						RVSL_RSN = thisline.substring(497, 499).trim();
						PIN_OFST = thisline.substring(499, 515).trim();///
						SHRG_GRP = thisline.substring(515, 516).trim();
						DEST_ORDER = thisline.substring(516, 517).trim();
						AUTH_ID_RESP = thisline.substring(517, 523).trim();
						REFR_IMP_IND = thisline.substring(523, 524).trim();
						REFR_AVAIL_IMP = thisline.substring(524, 526).trim();
						REFR_LEDG_IMP = thisline.substring(526, 528).trim();
						REFR_HLD_AMT_IMP = thisline.substring(528, 530).trim();
						REFR_CAF_REFR_IND = thisline.substring(530, 531).trim();
						REFR_USER_FLD3 = thisline.substring(531, 532).trim();
						DEP_SETL_IMP_FLG = thisline.substring(532, 533).trim();
						ADJ_SETL_IMP_FLG = thisline.substring(533, 534).trim();
						REFR_IND = thisline.substring(534, 538).trim();
						/*
						 * REFR_IND_PBF1 = thisline.substring(520, 521).trim(); REFR_IND_PBF2 =
						 * thisline.substring(521, 522).trim(); REFR_IND_PBF3 = thisline.substring(522,
						 * 523).trim(); REFR_IND_PBF4 = thisline.substring(523, 524).trim();
						 */
						USER_FLD4 = thisline.substring(538, 554).trim();
						FRWD_INST_ID_NUM = thisline.substring(554, 565).trim();
						CRD_ACCPT_ID_NUM = thisline.substring(565, 576).trim();
						CRD_ISS_ID_NUM = thisline.substring(576, 587).trim();
						USER_FLD6 = thisline.substring(587, 588).trim();
						FILEDATE = setupBean.getFileDate();
						FILENAME = setupBean.getP_FILE_NAME();

						ps.setString(1, DATE_TIME);
						ps.setString(2, REC_TYPE);
						ps.setString(3, AUTH_PPD);
						// ps.setString(4, TERM);
						ps.setString(4, TERM_LN);
						ps.setString(5, TERM_FIID);
						ps.setString(6, TERM_TERM_ID);
						// ps.setString(8, CRD);
						ps.setString(7, CRD_LN);
						ps.setString(8, CRD_FIID);
						ps.setString(9, CRD_PAN);
						ps.setString(10, CRD_MBR_NUM);
						ps.setString(11, BRCH_ID);
						ps.setString(12, REGN_ID);
						ps.setString(13, USER_FLD1X);
						// ps.setString(16, AUTHX);
						ps.setString(14, TYP_CDE);
						ps.setString(15, TYP);
						ps.setString(16, RTE_STAT);
						ps.setString(17, ORIGINATOR);
						ps.setString(18, RESPONDER);
						ps.setString(19, ENTRY_TIME);
						ps.setString(20, EXIT_TIME);
						ps.setString(21, RE_ENTRY_TIME);
						ps.setString(22, TRAN_DATE);
						ps.setString(23, TRAN_TIM);
						ps.setString(24, POST_DAT);
						ps.setString(25, ACQ_ICHG_SETL_DAT);
						ps.setString(26, ISS_ICHG_SETL_DAT);
						ps.setString(27, SEQ_NUM);
						ps.setString(28, TERM_TYP);
						ps.setString(29, TIM_OFST);
						ps.setString(30, ACQ_INST_ID_NUM);
						ps.setString(31, RCV_INST_ID_NUM);
						ps.setString(32, TRAN_CDE);
						// ps.setString(33, TRAN_CDE_R);

						ps.setString(33, FROM_ACCT);
						ps.setString(34, USER_FLD1);
						ps.setString(35, TO_ACCT);
						ps.setString(36, MULT_ACCT);
						ps.setString(37, AMT_1);
						ps.setString(38, AMT_2);
						ps.setString(39, AMT_3);
						ps.setString(40, DEP_BAL_CR);
						ps.setString(41, DEP_TYP);
						ps.setString(42, RESP_CDE);
						ps.setString(43, TERM_NAME_LOC);
						ps.setString(44, TERM_OWNER_NAME);
						ps.setString(45, TERM_CITY);
						ps.setString(46, TERM_ST);
						ps.setString(47, TERM_CNTRY);
						/* ps.setString(49, ORIG); */
						ps.setString(48, ORIG_OSEQ_NUM);
						ps.setString(49, ORIG_OTRAN_DAT);
						ps.setString(50, ORIG_OTRAN_TIM);
						ps.setString(51, ORIG_B24_POST);
						ps.setString(52, ORIG_CRNCY_CDE);
						/* ps.setString(55, USER_FLD2); */
						// ps.setString(56, MULT_CRNCY);
						ps.setString(53, MULT_CRNCY_AUTH_CRNCY_CDE);
						ps.setString(54, MULT_CRNCY_AUTH_CONV_RATE);
						ps.setString(55, MULT_CRNCY_SETL_CRNCY_CDE);
						ps.setString(56, MULT_CRNCY_SETL_CONV_RATE);
						ps.setString(57, MULT_CRNCY_CONV_DAT_TIM);
						ps.setString(58, RVSL_RSN);
						ps.setString(59, PIN_OFST);
						ps.setString(60, SHRG_GRP);
						ps.setString(61, DEST_ORDER);
						ps.setString(62, AUTH_ID_RESP);
						ps.setString(63, REFR_IMP_IND);
						ps.setString(64, REFR_AVAIL_IMP);
						ps.setString(65, REFR_LEDG_IMP);
						ps.setString(66, REFR_HLD_AMT_IMP);
						ps.setString(67, REFR_CAF_REFR_IND);
						ps.setString(68, REFR_USER_FLD3);
						ps.setString(69, DEP_SETL_IMP_FLG);
						ps.setString(70, ADJ_SETL_IMP_FLG);
						ps.setString(71, REFR_IND);
						ps.setString(72, USER_FLD4);
						ps.setString(73, FRWD_INST_ID_NUM);
						ps.setString(74, CRD_ACCPT_ID_NUM);
						ps.setString(75, CRD_ISS_ID_NUM);
						ps.setString(76, USER_FLD6);
						ps.setString(77, "");
						ps.setString(78, FILEDATE);
						ps.setString(79, FILENAME);
						// System.out.println("qq ");

						ps.addBatch();

						// count++;
						continue;
					}
					/*
					 * secondRead = thisline.substring(1094, 1100).trim();
					 * 
					 * // System.out.println("secondRead "+ secondRead);//1093 if
					 * (secondRead.equalsIgnoreCase("1086DR")) { // System.out.println("d");
					 * DATE_TIME = thisline.substring(1100, 1119).trim(); REC_TYPE =
					 * thisline.substring(1119, 1121).trim(); // System.out.println("DATE_TIME "+
					 * thisline.substring(1100, // 1119).trim()); // System.out.println("");
					 * AUTH_PPD = thisline.substring(1121, 1125).trim(); // TERM =
					 * thisline.substring(35, 39).trim(); TERM_LN = thisline.substring(1125,
					 * 1129).trim(); TERM_FIID = thisline.substring(1129, 1133).trim(); TERM_TERM_ID
					 * = thisline.substring(1133, 1149).trim(); // CRD = thisline.substring(47,
					 * 62).trim(); CRD_LN = thisline.substring(1149, 1153).trim(); CRD_FIID =
					 * thisline.substring(1153, 1157).trim(); CRD_PAN = thisline.substring(1157,
					 * 1176).trim(); CRD_MBR_NUM = thisline.substring(1176, 1179).trim(); BRCH_ID =
					 * thisline.substring(1179, 1183).trim(); REGN_ID = thisline.substring(1183,
					 * 1187).trim(); USER_FLD1X = thisline.substring(1187, 1189).trim(); // AUTHX =
					 * thisline.substring(87, 89).trim(); TYP_CDE = thisline.substring(1189,
					 * 1191).trim(); TYP = thisline.substring(1191, 1195).trim(); RTE_STAT =
					 * thisline.substring(1195, 1197).trim(); ORIGINATOR = thisline.substring(1197,
					 * 1198).trim(); RESPONDER = thisline.substring(1198, 1199).trim(); ENTRY_TIME =
					 * thisline.substring(1199, 1218).trim(); EXIT_TIME = thisline.substring(1218,
					 * 1237).trim(); RE_ENTRY_TIME = thisline.substring(1237, 1256).trim();
					 * TRAN_DATE = thisline.substring(1256, 1262).trim(); TRAN_TIM =
					 * thisline.substring(1262, 1270).trim();
					 * 
					 * POST_DAT = thisline.substring(1270, 1276).trim(); ACQ_ICHG_SETL_DAT =
					 * thisline.substring(1276, 1282).trim(); ISS_ICHG_SETL_DAT =
					 * thisline.substring(1282, 1288).trim(); SEQ_NUM = thisline.substring(1288,
					 * 1300).trim(); TERM_TYP = thisline.substring(1300, 1302).trim(); TIM_OFST =
					 * thisline.substring(1302, 1307).trim(); ACQ_INST_ID_NUM =
					 * thisline.substring(1307, 1318).trim(); RCV_INST_ID_NUM =
					 * thisline.substring(1318, 1329).trim(); TRAN_CDE = thisline.substring(1329,
					 * 1335).trim(); // TRAN_CDE_R = thisline.substring(249, 255).trim();
					 * 
					 * FROM_ACCT = thisline.substring(1335, 1354).trim();// ccc USER_FLD1 =
					 * thisline.substring(1354, 1355).trim(); TO_ACCT = thisline.substring(1355,
					 * 1374).trim(); MULT_ACCT = thisline.substring(1374, 1375).trim(); AMT_1 =
					 * thisline.substring(1375, 1394).trim(); AMT_2 = thisline.substring(1394,
					 * 1413).trim(); AMT_3 = thisline.substring(1413, 1432).trim(); DEP_BAL_CR =
					 * thisline.substring(1432, 1442).trim(); DEP_TYP = thisline.substring(1442,
					 * 1443).trim(); RESP_CDE = thisline.substring(1443, 1446).trim(); /// need to
					 * add 4 TERM_NAME_LOC = thisline.substring(1446, 1471).trim(); TERM_OWNER_NAME
					 * = thisline.substring(1471, 1493).trim(); TERM_CITY = thisline.substring(1493,
					 * 1506).trim(); TERM_ST = thisline.substring(1506, 1509).trim(); TERM_CNTRY =
					 * thisline.substring(1509, 1511).trim(); // ORIG = thisline.substring(427,
					 * 455).trim(); ORIG_OSEQ_NUM = thisline.substring(1511, 1523).trim();
					 * ORIG_OTRAN_DAT = thisline.substring(1523, 1527).trim(); ORIG_OTRAN_TIM =
					 * thisline.substring(1527, 1535).trim(); ORIG_B24_POST =
					 * thisline.substring(1535, 1539).trim(); ORIG_CRNCY_CDE =
					 * thisline.substring(1539, 1542).trim(); USER_FLD2 = thisline.substring(456,
					 * 497).trim(); // MULT_CRNCY = thisline.substring(497, 538).trim(); //
					 * //////not // sure MULT_CRNCY_AUTH_CRNCY_CDE = thisline.substring(1542,
					 * 1545).trim(); MULT_CRNCY_AUTH_CONV_RATE = thisline.substring(1545,
					 * 1553).trim(); MULT_CRNCY_SETL_CRNCY_CDE = thisline.substring(1553,
					 * 1556).trim(); MULT_CRNCY_SETL_CONV_RATE = thisline.substring(1556,
					 * 1564).trim(); MULT_CRNCY_CONV_DAT_TIM = thisline.substring(1564,
					 * 1583).trim(); RVSL_RSN = thisline.substring(1583, 1585).trim(); PIN_OFST =
					 * thisline.substring(1585, 1601).trim();/// SHRG_GRP = thisline.substring(1601,
					 * 1602).trim(); DEST_ORDER = thisline.substring(1602, 1603).trim();
					 * AUTH_ID_RESP = thisline.substring(1603, 1609).trim(); REFR_IMP_IND =
					 * thisline.substring(1609, 1610).trim(); REFR_AVAIL_IMP =
					 * thisline.substring(1610, 1612).trim(); REFR_LEDG_IMP =
					 * thisline.substring(1612, 1614).trim(); REFR_HLD_AMT_IMP =
					 * thisline.substring(1614, 1616).trim(); REFR_CAF_REFR_IND =
					 * thisline.substring(1616, 1617).trim(); REFR_USER_FLD3 =
					 * thisline.substring(1617, 1618).trim(); DEP_SETL_IMP_FLG =
					 * thisline.substring(1618, 1619).trim(); ADJ_SETL_IMP_FLG =
					 * thisline.substring(1619, 1620).trim(); REFR_IND = thisline.substring(1620,
					 * 1624).trim();
					 * 
					 * REFR_IND_PBF1 = thisline.substring(520, 521).trim(); REFR_IND_PBF2 =
					 * thisline.substring(521, 522).trim(); REFR_IND_PBF3 = thisline.substring(522,
					 * 523).trim(); REFR_IND_PBF4 = thisline.substring(523, 524).trim();
					 * 
					 * USER_FLD4 = thisline.substring(1624, 1640).trim(); FRWD_INST_ID_NUM =
					 * thisline.substring(1640, 1651).trim(); CRD_ACCPT_ID_NUM =
					 * thisline.substring(1651, 1662).trim(); CRD_ISS_ID_NUM =
					 * thisline.substring(1662, 1673).trim(); USER_FLD6 = thisline.substring(1673,
					 * 1674).trim() + "SD"; FILEDATE = setupBean.getFileDate(); FILENAME =
					 * setupBean.getP_FILE_NAME(); // lineNumber++;
					 * 
					 * ps.setString(1, DATE_TIME); ps.setString(2, REC_TYPE); ps.setString(3,
					 * AUTH_PPD); // ps.setString(4, TERM); ps.setString(4, TERM_LN);
					 * ps.setString(5, TERM_FIID); ps.setString(6, TERM_TERM_ID); // ps.setString(8,
					 * CRD); ps.setString(7, CRD_LN); ps.setString(8, CRD_FIID); ps.setString(9,
					 * CRD_PAN); ps.setString(10, CRD_MBR_NUM); ps.setString(11, BRCH_ID);
					 * ps.setString(12, REGN_ID); ps.setString(13, USER_FLD1X); // ps.setString(16,
					 * AUTHX); ps.setString(14, TYP_CDE); ps.setString(15, TYP); ps.setString(16,
					 * RTE_STAT); ps.setString(17, ORIGINATOR); ps.setString(18, RESPONDER);
					 * ps.setString(19, ENTRY_TIME); ps.setString(20, EXIT_TIME); ps.setString(21,
					 * RE_ENTRY_TIME); ps.setString(22, TRAN_DATE); ps.setString(23, TRAN_TIM);
					 * ps.setString(24, POST_DAT); ps.setString(25, ACQ_ICHG_SETL_DAT);
					 * ps.setString(26, ISS_ICHG_SETL_DAT); ps.setString(27, SEQ_NUM);
					 * ps.setString(28, TERM_TYP); ps.setString(29, TIM_OFST); ps.setString(30,
					 * ACQ_INST_ID_NUM); ps.setString(31, RCV_INST_ID_NUM); ps.setString(32,
					 * TRAN_CDE); // ps.setString(33, TRAN_CDE_R);
					 * 
					 * ps.setString(33, FROM_ACCT); ps.setString(34, USER_FLD1); ps.setString(35,
					 * TO_ACCT); ps.setString(36, MULT_ACCT); ps.setString(37, AMT_1);
					 * ps.setString(38, AMT_2); ps.setString(39, AMT_3); ps.setString(40,
					 * DEP_BAL_CR); ps.setString(41, DEP_TYP); ps.setString(42, RESP_CDE);
					 * ps.setString(43, TERM_NAME_LOC); ps.setString(44, TERM_OWNER_NAME);
					 * ps.setString(45, TERM_CITY); ps.setString(46, TERM_ST); ps.setString(47,
					 * TERM_CNTRY); ps.setString(49, ORIG); ps.setString(48, ORIG_OSEQ_NUM);
					 * ps.setString(49, ORIG_OTRAN_DAT); ps.setString(50, ORIG_OTRAN_TIM);
					 * ps.setString(51, ORIG_B24_POST); ps.setString(52, ORIG_CRNCY_CDE);
					 * ps.setString(55, USER_FLD2); // ps.setString(56, MULT_CRNCY);
					 * ps.setString(53, MULT_CRNCY_AUTH_CRNCY_CDE); ps.setString(54,
					 * MULT_CRNCY_AUTH_CONV_RATE); ps.setString(55, MULT_CRNCY_SETL_CRNCY_CDE);
					 * ps.setString(56, MULT_CRNCY_SETL_CONV_RATE); ps.setString(57,
					 * MULT_CRNCY_CONV_DAT_TIM); ps.setString(58, RVSL_RSN); ps.setString(59,
					 * PIN_OFST); ps.setString(60, SHRG_GRP); ps.setString(61, DEST_ORDER);
					 * ps.setString(62, AUTH_ID_RESP); ps.setString(63, REFR_IMP_IND);
					 * ps.setString(64, REFR_AVAIL_IMP); ps.setString(65, REFR_LEDG_IMP);
					 * ps.setString(66, REFR_HLD_AMT_IMP); ps.setString(67, REFR_CAF_REFR_IND);
					 * ps.setString(68, REFR_USER_FLD3); ps.setString(69, DEP_SETL_IMP_FLG);
					 * ps.setString(70, ADJ_SETL_IMP_FLG); ps.setString(71, REFR_IND);
					 * 
					 * ps.setString(76, REFR_IND_PBF1); ps.setString(77, REFR_IND_PBF2);
					 * ps.setString(78, REFR_IND_PBF3); ps.setString(79, REFR_IND_PBF4);
					 * 
					 * ps.setString(72, USER_FLD4); ps.setString(73, FRWD_INST_ID_NUM);
					 * ps.setString(74, CRD_ACCPT_ID_NUM); ps.setString(75, CRD_ISS_ID_NUM);
					 * ps.setString(76, USER_FLD6); ps.setString(77, ""); ps.setString(78,
					 * FILEDATE); ps.setString(79, FILENAME);
					 * 
					 * ps.addBatch();
					 * 
					 * // System.out.println("excuu ds "); // count++;
					 * 
					 * }
					 * 
					 * if (thisline.trim().length() == 1977 || thisline.trim().length() == 1982) {
					 * 
					 * DATE_TIME = thisline.substring(14, 33).trim(); REC_TYPE =
					 * thisline.substring(33, 35).trim(); AUTH_PPD = thisline.substring(35,
					 * 39).trim(); // TERM = thisline.substring(35, 39).trim(); TERM_LN =
					 * thisline.substring(39, 43).trim(); TERM_FIID = thisline.substring(43,
					 * 47).trim(); TERM_TERM_ID = thisline.substring(47, 63).trim(); // CRD =
					 * thisline.substring(47, 62).trim(); CRD_LN = thisline.substring(63,
					 * 67).trim(); CRD_FIID = thisline.substring(67, 71).trim(); CRD_PAN =
					 * thisline.substring(71, 90).trim(); CRD_MBR_NUM = thisline.substring(90,
					 * 93).trim(); BRCH_ID = thisline.substring(93, 97).trim(); REGN_ID =
					 * thisline.substring(97, 101).trim(); USER_FLD1X = thisline.substring(101,
					 * 103).trim(); // AUTHX = thisline.substring(87, 89).trim(); TYP_CDE =
					 * thisline.substring(103, 105).trim(); TYP = thisline.substring(105,
					 * 109).trim(); RTE_STAT = thisline.substring(109, 111).trim(); ORIGINATOR =
					 * thisline.substring(111, 112).trim(); RESPONDER = thisline.substring(112,
					 * 113).trim(); ENTRY_TIME = thisline.substring(113, 132).trim(); EXIT_TIME =
					 * thisline.substring(132, 151).trim(); RE_ENTRY_TIME = thisline.substring(151,
					 * 170).trim(); TRAN_DATE = thisline.substring(170, 176).trim(); TRAN_TIM =
					 * thisline.substring(176, 184).trim(); POST_DAT = thisline.substring(184,
					 * 190).trim(); ACQ_ICHG_SETL_DAT = thisline.substring(190, 196).trim();
					 * ISS_ICHG_SETL_DAT = thisline.substring(196, 202).trim(); SEQ_NUM =
					 * thisline.substring(202, 214).trim(); TERM_TYP = thisline.substring(214,
					 * 216).trim(); TIM_OFST = thisline.substring(216, 221).trim(); ACQ_INST_ID_NUM
					 * = thisline.substring(221, 232).trim(); RCV_INST_ID_NUM =
					 * thisline.substring(232, 243).trim(); TRAN_CDE = thisline.substring(243,
					 * 249).trim(); // TRAN_CDE_R = thisline.substring(249, 255).trim();
					 * 
					 * FROM_ACCT = thisline.substring(249, 268).trim();// ccc USER_FLD1 =
					 * thisline.substring(268, 269).trim(); TO_ACCT = thisline.substring(269,
					 * 288).trim(); MULT_ACCT = thisline.substring(288, 289).trim(); AMT_1 =
					 * thisline.substring(289, 308).trim(); AMT_2 = thisline.substring(308,
					 * 327).trim(); AMT_3 = thisline.substring(327, 346).trim(); DEP_BAL_CR =
					 * thisline.substring(346, 356).trim(); DEP_TYP = thisline.substring(356,
					 * 357).trim(); RESP_CDE = thisline.substring(357, 360).trim(); /// need to add
					 * 4 TERM_NAME_LOC = thisline.substring(360, 385).trim(); TERM_OWNER_NAME =
					 * thisline.substring(385, 407).trim(); TERM_CITY = thisline.substring(407,
					 * 420).trim(); TERM_ST = thisline.substring(420, 423).trim(); TERM_CNTRY =
					 * thisline.substring(423, 425).trim(); // ORIG = thisline.substring(427,
					 * 455).trim(); ORIG_OSEQ_NUM = thisline.substring(425, 437).trim();
					 * ORIG_OTRAN_DAT = thisline.substring(437, 441).trim(); ORIG_OTRAN_TIM =
					 * thisline.substring(441, 449).trim(); ORIG_B24_POST = thisline.substring(449,
					 * 453).trim(); ORIG_CRNCY_CDE = thisline.substring(453, 456).trim(); USER_FLD2
					 * = thisline.substring(456, 497).trim(); // MULT_CRNCY =
					 * thisline.substring(497, 538).trim(); //////not // sure
					 * MULT_CRNCY_AUTH_CRNCY_CDE = thisline.substring(456, 459).trim();
					 * MULT_CRNCY_AUTH_CONV_RATE = thisline.substring(459, 467).trim();
					 * MULT_CRNCY_SETL_CRNCY_CDE = thisline.substring(467, 470).trim();
					 * MULT_CRNCY_SETL_CONV_RATE = thisline.substring(470, 478).trim();
					 * MULT_CRNCY_CONV_DAT_TIM = thisline.substring(478, 497).trim(); RVSL_RSN =
					 * thisline.substring(497, 499).trim(); PIN_OFST = thisline.substring(499,
					 * 515).trim();/// SHRG_GRP = thisline.substring(515, 516).trim(); DEST_ORDER =
					 * thisline.substring(516, 517).trim(); AUTH_ID_RESP = thisline.substring(517,
					 * 523).trim(); REFR_IMP_IND = thisline.substring(523, 524).trim();
					 * REFR_AVAIL_IMP = thisline.substring(524, 526).trim(); REFR_LEDG_IMP =
					 * thisline.substring(526, 528).trim(); REFR_HLD_AMT_IMP =
					 * thisline.substring(528, 530).trim(); REFR_CAF_REFR_IND =
					 * thisline.substring(530, 531).trim(); REFR_USER_FLD3 = thisline.substring(531,
					 * 532).trim(); DEP_SETL_IMP_FLG = thisline.substring(532, 533).trim();
					 * ADJ_SETL_IMP_FLG = thisline.substring(533, 534).trim(); REFR_IND =
					 * thisline.substring(534, 538).trim();
					 * 
					 * REFR_IND_PBF1 = thisline.substring(520, 521).trim(); REFR_IND_PBF2 =
					 * thisline.substring(521, 522).trim(); REFR_IND_PBF3 = thisline.substring(522,
					 * 523).trim(); REFR_IND_PBF4 = thisline.substring(523, 524).trim();
					 * 
					 * USER_FLD4 = thisline.substring(538, 554).trim(); FRWD_INST_ID_NUM =
					 * thisline.substring(554, 565).trim(); CRD_ACCPT_ID_NUM =
					 * thisline.substring(565, 576).trim(); CRD_ISS_ID_NUM = thisline.substring(576,
					 * 587).trim(); USER_FLD6 = thisline.substring(587, 588).trim(); FILEDATE =
					 * setupBean.getFileDate(); FILENAME = setupBean.getP_FILE_NAME();
					 * 
					 * ps.setString(1, DATE_TIME); ps.setString(2, REC_TYPE); ps.setString(3,
					 * AUTH_PPD); // ps.setString(4, TERM); ps.setString(4, TERM_LN);
					 * ps.setString(5, TERM_FIID); ps.setString(6, TERM_TERM_ID); // ps.setString(8,
					 * CRD); ps.setString(7, CRD_LN); ps.setString(8, CRD_FIID); ps.setString(9,
					 * CRD_PAN); ps.setString(10, CRD_MBR_NUM); ps.setString(11, BRCH_ID);
					 * ps.setString(12, REGN_ID); ps.setString(13, USER_FLD1X); // ps.setString(16,
					 * AUTHX); ps.setString(14, TYP_CDE); ps.setString(15, TYP); ps.setString(16,
					 * RTE_STAT); ps.setString(17, ORIGINATOR); ps.setString(18, RESPONDER);
					 * ps.setString(19, ENTRY_TIME); ps.setString(20, EXIT_TIME); ps.setString(21,
					 * RE_ENTRY_TIME); ps.setString(22, TRAN_DATE); ps.setString(23, TRAN_TIM);
					 * ps.setString(24, POST_DAT); ps.setString(25, ACQ_ICHG_SETL_DAT);
					 * ps.setString(26, ISS_ICHG_SETL_DAT); ps.setString(27, SEQ_NUM);
					 * ps.setString(28, TERM_TYP); ps.setString(29, TIM_OFST); ps.setString(30,
					 * ACQ_INST_ID_NUM); ps.setString(31, RCV_INST_ID_NUM); ps.setString(32,
					 * TRAN_CDE); // ps.setString(33, TRAN_CDE_R);
					 * 
					 * ps.setString(33, FROM_ACCT); ps.setString(34, USER_FLD1); ps.setString(35,
					 * TO_ACCT); ps.setString(36, MULT_ACCT); ps.setString(37, AMT_1);
					 * ps.setString(38, AMT_2); ps.setString(39, AMT_3); ps.setString(40,
					 * DEP_BAL_CR); ps.setString(41, DEP_TYP); ps.setString(42, RESP_CDE);
					 * ps.setString(43, TERM_NAME_LOC); ps.setString(44, TERM_OWNER_NAME);
					 * ps.setString(45, TERM_CITY); ps.setString(46, TERM_ST); ps.setString(47,
					 * TERM_CNTRY); ps.setString(49, ORIG); ps.setString(48, ORIG_OSEQ_NUM);
					 * ps.setString(49, ORIG_OTRAN_DAT); ps.setString(50, ORIG_OTRAN_TIM);
					 * ps.setString(51, ORIG_B24_POST); ps.setString(52, ORIG_CRNCY_CDE);
					 * ps.setString(55, USER_FLD2); // ps.setString(56, MULT_CRNCY);
					 * ps.setString(53, MULT_CRNCY_AUTH_CRNCY_CDE); ps.setString(54,
					 * MULT_CRNCY_AUTH_CONV_RATE); ps.setString(55, MULT_CRNCY_SETL_CRNCY_CDE);
					 * ps.setString(56, MULT_CRNCY_SETL_CONV_RATE); ps.setString(57,
					 * MULT_CRNCY_CONV_DAT_TIM); ps.setString(58, RVSL_RSN); ps.setString(59,
					 * PIN_OFST); ps.setString(60, SHRG_GRP); ps.setString(61, DEST_ORDER);
					 * ps.setString(62, AUTH_ID_RESP); ps.setString(63, REFR_IMP_IND);
					 * ps.setString(64, REFR_AVAIL_IMP); ps.setString(65, REFR_LEDG_IMP);
					 * ps.setString(66, REFR_HLD_AMT_IMP); ps.setString(67, REFR_CAF_REFR_IND);
					 * ps.setString(68, REFR_USER_FLD3); ps.setString(69, DEP_SETL_IMP_FLG);
					 * ps.setString(70, ADJ_SETL_IMP_FLG); ps.setString(71, REFR_IND);
					 * ps.setString(72, USER_FLD4); ps.setString(73, FRWD_INST_ID_NUM);
					 * ps.setString(74, CRD_ACCPT_ID_NUM); ps.setString(75, CRD_ISS_ID_NUM);
					 * ps.setString(76, USER_FLD6); ps.setString(77, ""); ps.setString(78,
					 * FILEDATE); ps.setString(79, FILENAME); // System.out.println("qq ");
					 * 
					 * ps.addBatch();
					 * 
					 * // count++;
					 * 
					 * secondRead = thisline.substring(1094, 1100).trim();
					 * 
					 * DATE_TIME = thisline.substring(1100, 1119).trim(); REC_TYPE =
					 * thisline.substring(1119, 1121).trim(); // System.out.println("DATE_TIME "+
					 * thisline.substring(1100, // 1119).trim()); // System.out.println("");
					 * AUTH_PPD = thisline.substring(1121, 1125).trim(); // TERM =
					 * thisline.substring(35, 39).trim(); TERM_LN = thisline.substring(1125,
					 * 1129).trim(); TERM_FIID = thisline.substring(1129, 1133).trim(); TERM_TERM_ID
					 * = thisline.substring(1133, 1149).trim(); // CRD = thisline.substring(47,
					 * 62).trim(); CRD_LN = thisline.substring(1149, 1153).trim(); CRD_FIID =
					 * thisline.substring(1153, 1157).trim(); CRD_PAN = thisline.substring(1157,
					 * 1176).trim(); CRD_MBR_NUM = thisline.substring(1176, 1179).trim(); BRCH_ID =
					 * thisline.substring(1179, 1183).trim(); REGN_ID = thisline.substring(1183,
					 * 1187).trim(); USER_FLD1X = thisline.substring(1187, 1189).trim(); // AUTHX =
					 * thisline.substring(87, 89).trim(); TYP_CDE = thisline.substring(1189,
					 * 1191).trim(); TYP = thisline.substring(1191, 1195).trim(); RTE_STAT =
					 * thisline.substring(1195, 1197).trim(); ORIGINATOR = thisline.substring(1197,
					 * 1198).trim(); RESPONDER = thisline.substring(1198, 1199).trim(); ENTRY_TIME =
					 * thisline.substring(1199, 1218).trim(); EXIT_TIME = thisline.substring(1218,
					 * 1237).trim(); RE_ENTRY_TIME = thisline.substring(1237, 1256).trim();
					 * TRAN_DATE = thisline.substring(1256, 1262).trim(); TRAN_TIM =
					 * thisline.substring(1262, 1270).trim();
					 * 
					 * POST_DAT = thisline.substring(1270, 1276).trim(); ACQ_ICHG_SETL_DAT =
					 * thisline.substring(1276, 1282).trim(); ISS_ICHG_SETL_DAT =
					 * thisline.substring(1282, 1288).trim(); SEQ_NUM = thisline.substring(1288,
					 * 1300).trim(); TERM_TYP = thisline.substring(1300, 1302).trim(); TIM_OFST =
					 * thisline.substring(1302, 1307).trim(); ACQ_INST_ID_NUM =
					 * thisline.substring(1307, 1318).trim(); RCV_INST_ID_NUM =
					 * thisline.substring(1318, 1329).trim(); TRAN_CDE = thisline.substring(1329,
					 * 1335).trim(); // TRAN_CDE_R = thisline.substring(249, 255).trim();
					 * 
					 * FROM_ACCT = thisline.substring(1335, 1354).trim();// ccc USER_FLD1 =
					 * thisline.substring(1354, 1355).trim(); TO_ACCT = thisline.substring(1355,
					 * 1374).trim(); MULT_ACCT = thisline.substring(1374, 1375).trim(); AMT_1 =
					 * thisline.substring(1375, 1394).trim(); AMT_2 = thisline.substring(1394,
					 * 1413).trim(); AMT_3 = thisline.substring(1413, 1432).trim(); DEP_BAL_CR =
					 * thisline.substring(1432, 1442).trim(); DEP_TYP = thisline.substring(1442,
					 * 1443).trim(); RESP_CDE = thisline.substring(1443, 1446).trim(); /// need to
					 * add 4 TERM_NAME_LOC = thisline.substring(1446, 1471).trim(); TERM_OWNER_NAME
					 * = thisline.substring(1471, 1493).trim(); TERM_CITY = thisline.substring(1493,
					 * 1506).trim(); TERM_ST = thisline.substring(1506, 1509).trim(); TERM_CNTRY =
					 * thisline.substring(1509, 1511).trim(); // ORIG = thisline.substring(427,
					 * 455).trim(); ORIG_OSEQ_NUM = thisline.substring(1511, 1523).trim();
					 * ORIG_OTRAN_DAT = thisline.substring(1523, 1527).trim(); ORIG_OTRAN_TIM =
					 * thisline.substring(1527, 1535).trim(); ORIG_B24_POST =
					 * thisline.substring(1535, 1539).trim(); ORIG_CRNCY_CDE =
					 * thisline.substring(1539, 1542).trim(); USER_FLD2 = thisline.substring(456,
					 * 497).trim(); // MULT_CRNCY = thisline.substring(497, 538).trim(); //
					 * //////not // sure MULT_CRNCY_AUTH_CRNCY_CDE = thisline.substring(1542,
					 * 1545).trim(); MULT_CRNCY_AUTH_CONV_RATE = thisline.substring(1545,
					 * 1553).trim(); MULT_CRNCY_SETL_CRNCY_CDE = thisline.substring(1553,
					 * 1556).trim(); MULT_CRNCY_SETL_CONV_RATE = thisline.substring(1556,
					 * 1564).trim(); MULT_CRNCY_CONV_DAT_TIM = thisline.substring(1564,
					 * 1583).trim(); RVSL_RSN = thisline.substring(1583, 1585).trim(); PIN_OFST =
					 * thisline.substring(1585, 1601).trim();/// SHRG_GRP = thisline.substring(1601,
					 * 1602).trim(); DEST_ORDER = thisline.substring(1602, 1603).trim();
					 * AUTH_ID_RESP = thisline.substring(1603, 1609).trim(); REFR_IMP_IND =
					 * thisline.substring(1609, 1610).trim(); REFR_AVAIL_IMP =
					 * thisline.substring(1610, 1612).trim(); REFR_LEDG_IMP =
					 * thisline.substring(1612, 1614).trim(); REFR_HLD_AMT_IMP =
					 * thisline.substring(1614, 1616).trim(); REFR_CAF_REFR_IND =
					 * thisline.substring(1616, 1617).trim(); REFR_USER_FLD3 =
					 * thisline.substring(1617, 1618).trim(); DEP_SETL_IMP_FLG =
					 * thisline.substring(1618, 1619).trim(); ADJ_SETL_IMP_FLG =
					 * thisline.substring(1619, 1620).trim(); REFR_IND = thisline.substring(1620,
					 * 1624).trim();
					 * 
					 * REFR_IND_PBF1 = thisline.substring(520, 521).trim(); REFR_IND_PBF2 =
					 * thisline.substring(521, 522).trim(); REFR_IND_PBF3 = thisline.substring(522,
					 * 523).trim(); REFR_IND_PBF4 = thisline.substring(523, 524).trim();
					 * 
					 * USER_FLD4 = thisline.substring(1624, 1640).trim(); FRWD_INST_ID_NUM =
					 * thisline.substring(1640, 1651).trim(); CRD_ACCPT_ID_NUM =
					 * thisline.substring(1651, 1662).trim(); CRD_ISS_ID_NUM =
					 * thisline.substring(1662, 1673).trim(); USER_FLD6 = thisline.substring(1673,
					 * 1674).trim() + "SD"; FILEDATE = setupBean.getFileDate(); FILENAME =
					 * setupBean.getP_FILE_NAME(); // lineNumber++;
					 * 
					 * ps.setString(1, DATE_TIME); ps.setString(2, REC_TYPE); ps.setString(3,
					 * AUTH_PPD); // ps.setString(4, TERM); ps.setString(4, TERM_LN);
					 * ps.setString(5, TERM_FIID); ps.setString(6, TERM_TERM_ID); // ps.setString(8,
					 * CRD); ps.setString(7, CRD_LN); ps.setString(8, CRD_FIID); ps.setString(9,
					 * CRD_PAN); ps.setString(10, CRD_MBR_NUM); ps.setString(11, BRCH_ID);
					 * ps.setString(12, REGN_ID); ps.setString(13, USER_FLD1X); // ps.setString(16,
					 * AUTHX); ps.setString(14, TYP_CDE); ps.setString(15, TYP); ps.setString(16,
					 * RTE_STAT); ps.setString(17, ORIGINATOR); ps.setString(18, RESPONDER);
					 * ps.setString(19, ENTRY_TIME); ps.setString(20, EXIT_TIME); ps.setString(21,
					 * RE_ENTRY_TIME); ps.setString(22, TRAN_DATE); ps.setString(23, TRAN_TIM);
					 * ps.setString(24, POST_DAT); ps.setString(25, ACQ_ICHG_SETL_DAT);
					 * ps.setString(26, ISS_ICHG_SETL_DAT); ps.setString(27, SEQ_NUM);
					 * ps.setString(28, TERM_TYP); ps.setString(29, TIM_OFST); ps.setString(30,
					 * ACQ_INST_ID_NUM); ps.setString(31, RCV_INST_ID_NUM); ps.setString(32,
					 * TRAN_CDE); // ps.setString(33, TRAN_CDE_R);
					 * 
					 * ps.setString(33, FROM_ACCT); ps.setString(34, USER_FLD1); ps.setString(35,
					 * TO_ACCT); ps.setString(36, MULT_ACCT); ps.setString(37, AMT_1);
					 * ps.setString(38, AMT_2); ps.setString(39, AMT_3); ps.setString(40,
					 * DEP_BAL_CR); ps.setString(41, DEP_TYP); ps.setString(42, RESP_CDE);
					 * ps.setString(43, TERM_NAME_LOC); ps.setString(44, TERM_OWNER_NAME);
					 * ps.setString(45, TERM_CITY); ps.setString(46, TERM_ST); ps.setString(47,
					 * TERM_CNTRY); ps.setString(49, ORIG); ps.setString(48, ORIG_OSEQ_NUM);
					 * ps.setString(49, ORIG_OTRAN_DAT); ps.setString(50, ORIG_OTRAN_TIM);
					 * ps.setString(51, ORIG_B24_POST); ps.setString(52, ORIG_CRNCY_CDE);
					 * ps.setString(55, USER_FLD2); // ps.setString(56, MULT_CRNCY);
					 * ps.setString(53, MULT_CRNCY_AUTH_CRNCY_CDE); ps.setString(54,
					 * MULT_CRNCY_AUTH_CONV_RATE); ps.setString(55, MULT_CRNCY_SETL_CRNCY_CDE);
					 * ps.setString(56, MULT_CRNCY_SETL_CONV_RATE); ps.setString(57,
					 * MULT_CRNCY_CONV_DAT_TIM); ps.setString(58, RVSL_RSN); ps.setString(59,
					 * PIN_OFST); ps.setString(60, SHRG_GRP); ps.setString(61, DEST_ORDER);
					 * ps.setString(62, AUTH_ID_RESP); ps.setString(63, REFR_IMP_IND);
					 * ps.setString(64, REFR_AVAIL_IMP); ps.setString(65, REFR_LEDG_IMP);
					 * ps.setString(66, REFR_HLD_AMT_IMP); ps.setString(67, REFR_CAF_REFR_IND);
					 * ps.setString(68, REFR_USER_FLD3); ps.setString(69, DEP_SETL_IMP_FLG);
					 * ps.setString(70, ADJ_SETL_IMP_FLG); ps.setString(71, REFR_IND);
					 * 
					 * ps.setString(76, REFR_IND_PBF1); ps.setString(77, REFR_IND_PBF2);
					 * ps.setString(78, REFR_IND_PBF3); ps.setString(79, REFR_IND_PBF4);
					 * 
					 * ps.setString(72, USER_FLD4); ps.setString(73, FRWD_INST_ID_NUM);
					 * ps.setString(74, CRD_ACCPT_ID_NUM); ps.setString(75, CRD_ISS_ID_NUM);
					 * ps.setString(76, USER_FLD6); ps.setString(77, ""); ps.setString(78,
					 * FILEDATE); ps.setString(79, FILENAME);
					 * 
					 * ps.addBatch();
					 * 
					 * // count++; continue; } THIRDRead = thisline.substring(2180, 2186).trim();
					 * 
					 * // System.out.println("THIRDRead "+ THIRDRead);//1093 if
					 * (THIRDRead.equalsIgnoreCase("1086DR")) { // System.out.println("d3");
					 * DATE_TIME = thisline.substring(2186, 2205).trim(); REC_TYPE =
					 * thisline.substring(2205, 2207).trim(); // System.out.println("DATE_TIME "+
					 * thisline.substring(1100, // 1119).trim()); // System.out.println("");
					 * AUTH_PPD = thisline.substring(2207, 2211).trim(); // TERM =
					 * thisline.substring(35, 39).trim(); TERM_LN = thisline.substring(2211,
					 * 2215).trim(); TERM_FIID = thisline.substring(2215, 2219).trim(); TERM_TERM_ID
					 * = thisline.substring(2219, 2235).trim(); // CRD = thisline.substring(47,
					 * 62).trim(); CRD_LN = thisline.substring(2235, 2239).trim(); CRD_FIID =
					 * thisline.substring(2239, 2243).trim(); CRD_PAN = thisline.substring(2243,
					 * 2262).trim(); CRD_MBR_NUM = thisline.substring(2262, 2265).trim(); BRCH_ID =
					 * thisline.substring(2265, 2269).trim(); REGN_ID = thisline.substring(2269,
					 * 2273).trim(); USER_FLD1X = thisline.substring(2273, 2275).trim(); // AUTHX =
					 * thisline.substring(87, 89).trim(); TYP_CDE = thisline.substring(2275,
					 * 2277).trim(); TYP = thisline.substring(2277, 2281).trim(); RTE_STAT =
					 * thisline.substring(2281, 2283).trim(); ORIGINATOR = thisline.substring(2283,
					 * 2284).trim(); RESPONDER = thisline.substring(2284, 2285).trim(); ENTRY_TIME =
					 * thisline.substring(2285, 2304).trim(); EXIT_TIME = thisline.substring(2304,
					 * 2323).trim(); RE_ENTRY_TIME = thisline.substring(2323, 2342).trim();
					 * TRAN_DATE = thisline.substring(2342, 2348).trim(); TRAN_TIM =
					 * thisline.substring(2348, 2356).trim(); POST_DAT = thisline.substring(2356,
					 * 2362).trim(); ACQ_ICHG_SETL_DAT = thisline.substring(2362, 2368).trim();
					 * ISS_ICHG_SETL_DAT = thisline.substring(2368, 2374).trim(); SEQ_NUM =
					 * thisline.substring(2374, 2386).trim(); TERM_TYP = thisline.substring(2386,
					 * 2388).trim(); TIM_OFST = thisline.substring(2388, 2394).trim();
					 * ACQ_INST_ID_NUM = thisline.substring(2394, 2404).trim(); RCV_INST_ID_NUM =
					 * thisline.substring(2404, 2415).trim(); TRAN_CDE = thisline.substring(2415,
					 * 2421).trim(); // TRAN_CDE_R = thisline.substring(249, 255).trim();
					 * 
					 * FROM_ACCT = thisline.substring(2421, 2440).trim();// ccc USER_FLD1 =
					 * thisline.substring(2440, 2441).trim(); TO_ACCT = thisline.substring(2441,
					 * 2460).trim(); MULT_ACCT = thisline.substring(2460, 2461).trim(); AMT_1 =
					 * thisline.substring(2461, 2480).trim(); AMT_2 = thisline.substring(2480,
					 * 2499).trim(); AMT_3 = thisline.substring(2499, 2518).trim(); DEP_BAL_CR =
					 * thisline.substring(2518, 2528).trim(); DEP_TYP = thisline.substring(2528,
					 * 2529).trim(); RESP_CDE = thisline.substring(2529, 2532).trim(); /// need to
					 * add 4 TERM_NAME_LOC = thisline.substring(2532, 2557).trim(); TERM_OWNER_NAME
					 * = thisline.substring(2557, 2579).trim(); TERM_CITY = thisline.substring(2579,
					 * 2592).trim(); TERM_ST = thisline.substring(2592, 2595).trim(); TERM_CNTRY =
					 * thisline.substring(2595, 2597).trim(); // ORIG = thisline.substring(427,
					 * 455).trim(); ORIG_OSEQ_NUM = thisline.substring(2597, 2609).trim();
					 * ORIG_OTRAN_DAT = thisline.substring(2607, 2614).trim(); ORIG_OTRAN_TIM =
					 * thisline.substring(2614, 2622).trim(); ORIG_B24_POST =
					 * thisline.substring(2622, 2625).trim(); ORIG_CRNCY_CDE =
					 * thisline.substring(2625, 2628).trim(); USER_FLD2 = thisline.substring(456,
					 * 497).trim(); // MULT_CRNCY = thisline.substring(497, 538).trim(); //
					 * //////not // sure MULT_CRNCY_AUTH_CRNCY_CDE = thisline.substring(2628,
					 * 2631).trim(); MULT_CRNCY_AUTH_CONV_RATE = thisline.substring(2631,
					 * 2639).trim(); MULT_CRNCY_SETL_CRNCY_CDE = thisline.substring(2639,
					 * 2642).trim(); MULT_CRNCY_SETL_CONV_RATE = thisline.substring(2642,
					 * 2650).trim(); MULT_CRNCY_CONV_DAT_TIM = thisline.substring(2650,
					 * 2669).trim(); RVSL_RSN = thisline.substring(2669, 2671).trim(); PIN_OFST =
					 * thisline.substring(2671, 2687).trim();/// SHRG_GRP = thisline.substring(2687,
					 * 2688).trim(); DEST_ORDER = thisline.substring(2688, 2690).trim();
					 * AUTH_ID_RESP = thisline.substring(2689, 2695).trim(); REFR_IMP_IND =
					 * thisline.substring(2695, 2696).trim(); REFR_AVAIL_IMP =
					 * thisline.substring(2696, 2698).trim(); REFR_LEDG_IMP =
					 * thisline.substring(2698, 2700).trim(); REFR_HLD_AMT_IMP =
					 * thisline.substring(2700, 2702).trim(); REFR_CAF_REFR_IND =
					 * thisline.substring(2702, 2703).trim(); REFR_USER_FLD3 =
					 * thisline.substring(2703, 2704).trim(); DEP_SETL_IMP_FLG =
					 * thisline.substring(2704, 2705).trim(); ADJ_SETL_IMP_FLG =
					 * thisline.substring(2705, 2706).trim(); REFR_IND = thisline.substring(2706,
					 * 2710).trim();
					 * 
					 * REFR_IND_PBF1 = thisline.substring(520, 521).trim(); REFR_IND_PBF2 =
					 * thisline.substring(521, 522).trim(); REFR_IND_PBF3 = thisline.substring(522,
					 * 523).trim(); REFR_IND_PBF4 = thisline.substring(523, 524).trim();
					 * 
					 * USER_FLD4 = thisline.substring(2710, 2726).trim(); FRWD_INST_ID_NUM =
					 * thisline.substring(2726, 2737).trim(); CRD_ACCPT_ID_NUM =
					 * thisline.substring(2737, 2748).trim(); CRD_ISS_ID_NUM =
					 * thisline.substring(2748, 2759).trim(); USER_FLD6 = thisline.substring(2759,
					 * 2760).trim() + "TD"; FILEDATE = setupBean.getFileDate(); FILENAME =
					 * setupBean.getP_FILE_NAME(); // lineNumber++;
					 * 
					 * ps3.setString(1, DATE_TIME); ps3.setString(2, REC_TYPE); ps3.setString(3,
					 * AUTH_PPD); // ps3.setString(4, TERM); ps3.setString(4, TERM_LN);
					 * ps3.setString(5, TERM_FIID); ps3.setString(6, TERM_TERM_ID); //
					 * ps3.setString(8, CRD); ps3.setString(7, CRD_LN); ps3.setString(8, CRD_FIID);
					 * ps3.setString(9, CRD_PAN); ps3.setString(10, CRD_MBR_NUM); ps3.setString(11,
					 * BRCH_ID); ps3.setString(12, REGN_ID); ps3.setString(13, USER_FLD1X); //
					 * ps3.setString(16, AUTHX); ps3.setString(14, TYP_CDE); ps3.setString(15, TYP);
					 * ps3.setString(16, RTE_STAT); ps3.setString(17, ORIGINATOR); ps3.setString(18,
					 * RESPONDER); ps3.setString(19, ENTRY_TIME); ps3.setString(20, EXIT_TIME);
					 * ps3.setString(21, RE_ENTRY_TIME); ps3.setString(22, TRAN_DATE);
					 * ps3.setString(23, TRAN_TIM); ps3.setString(24, POST_DAT); ps3.setString(25,
					 * ACQ_ICHG_SETL_DAT); ps3.setString(26, ISS_ICHG_SETL_DAT); ps3.setString(27,
					 * SEQ_NUM); ps3.setString(28, TERM_TYP); ps3.setString(29, TIM_OFST);
					 * ps3.setString(30, ACQ_INST_ID_NUM); ps3.setString(31, RCV_INST_ID_NUM);
					 * ps3.setString(32, TRAN_CDE); // ps3.setString(33, TRAN_CDE_R);
					 * 
					 * ps3.setString(33, FROM_ACCT); ps3.setString(34, USER_FLD1); ps3.setString(35,
					 * TO_ACCT); ps3.setString(36, MULT_ACCT); ps3.setString(37, AMT_1);
					 * ps3.setString(38, AMT_2); ps3.setString(39, AMT_3); ps3.setString(40,
					 * DEP_BAL_CR); ps3.setString(41, DEP_TYP); ps3.setString(42, RESP_CDE);
					 * ps3.setString(43, TERM_NAME_LOC); ps3.setString(44, TERM_OWNER_NAME);
					 * ps3.setString(45, TERM_CITY); ps3.setString(46, TERM_ST); ps3.setString(47,
					 * TERM_CNTRY); ps3.setString(49, ORIG); ps3.setString(48, ORIG_OSEQ_NUM);
					 * ps3.setString(49, ORIG_OTRAN_DAT); ps3.setString(50, ORIG_OTRAN_TIM);
					 * ps3.setString(51, ORIG_B24_POST); ps3.setString(52, ORIG_CRNCY_CDE);
					 * ps3.setString(55, USER_FLD2); // ps3.setString(56, MULT_CRNCY);
					 * ps3.setString(53, MULT_CRNCY_AUTH_CRNCY_CDE); ps3.setString(54,
					 * MULT_CRNCY_AUTH_CONV_RATE); ps3.setString(55, MULT_CRNCY_SETL_CRNCY_CDE);
					 * ps3.setString(56, MULT_CRNCY_SETL_CONV_RATE); ps3.setString(57,
					 * MULT_CRNCY_CONV_DAT_TIM); ps3.setString(58, RVSL_RSN); ps3.setString(59,
					 * PIN_OFST); ps3.setString(60, SHRG_GRP); ps3.setString(61, DEST_ORDER);
					 * ps3.setString(62, AUTH_ID_RESP); ps3.setString(63, REFR_IMP_IND);
					 * ps3.setString(64, REFR_AVAIL_IMP); ps3.setString(65, REFR_LEDG_IMP);
					 * ps3.setString(66, REFR_HLD_AMT_IMP); ps3.setString(67, REFR_CAF_REFR_IND);
					 * ps3.setString(68, REFR_USER_FLD3); ps3.setString(69, DEP_SETL_IMP_FLG);
					 * ps3.setString(70, ADJ_SETL_IMP_FLG); ps3.setString(71, REFR_IND);
					 * 
					 * ps3.setString(76, REFR_IND_PBF1); ps3.setString(77, REFR_IND_PBF2);
					 * ps3.setString(78, REFR_IND_PBF3); ps3.setString(79, REFR_IND_PBF4);
					 * 
					 * ps3.setString(72, USER_FLD4); ps3.setString(73, FRWD_INST_ID_NUM);
					 * ps3.setString(74, CRD_ACCPT_ID_NUM); ps3.setString(75, CRD_ISS_ID_NUM);
					 * ps3.setString(76, USER_FLD6); ps3.setString(77, ""); ps3.setString(78,
					 * FILEDATE); ps3.setString(79, FILENAME);
					 * 
					 * ps3.addBatch(); count++; // System.out.println("excuu ds 2");
					 * 
					 * }
					 */

				}
				if (++batchcount % batchSize == 0) {

					batchNumber++;
					System.out.println("Batch Executed is " + batchNumber);
					ps.executeBatch();

					ps3.executeBatch();
					con.commit();
				}

			}

			/*
			 * if (batchSize == 30000) { batchNumber++; System.out.println(
			 * "Batch1 Executed is " + batchNumber); ps.executeBatch();
			 * 
			 * 
			 * batchSize = 0; }
			 */

			logger.info("Executed Batch SuccessFully");

			ps.executeBatch();
			// ps1.executeBatch();

			ps3.executeBatch();
			con.commit();

			if (count > 0) {
				updatps.setString(1, FILENAME);
				updatps.setString(2, FILEDATE);
				updatps.setString(3, String.valueOf(count));
				updatps.setString(4, "ATM");
				updatps.execute();
				con.commit();
				logger.info("update Batch Completed");
			}

			logger.info("Executed Batch Completed");

			output.put("result", true);
			output.put("msg", "File Uploaded and Record count is " + countt);
			logger.info("Completed " + countt);

			return output;

		} catch (Exception e) {
			logger.info("Exception in lineNumber " + countt);
			logger.info("Exception in ReadUCOATMSwitchData " + e);

			output.put("result", false);
			// output.put("msg", "File Uploaded and Record count is " + count);
			output.put("msg", "UNABLE TO UPLOAD " + lineNumber);
			return output;
		}
	}

	public HashMap<String, Object> uploadPOSSwitchData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {
		logger.info("uploadPOSSwitchData method Called " + setupBean.getFILEDATE());
		HashMap<String, Object> output = new HashMap<String, Object>();
		String thisline = null;
		int lineNumber = 0, feesize = 1;
		int count = 1;

		int sr_no = 1, batchNumber = 0, batchSize = 3000;
		String InsertQuery = "  INSERT INTO SWITCH_POS_RAWDATA (DATE_TIME,REC_TYP,CRD_LN,CRD_FIID,CRAD_NUM,CRD_MBR_NUM,RETL_KEY_IN,RDF_KEY,RDF_KEY_GRP,RDF_KEY_REGN,RDF_KEY_ID,TERM_ID,SHIFT_NUM,BATCH_NUM,TERM_IN,TERM_FIID,TERM_ID2,TERM_TIME,TERM_ID3,TKEY_RKEY_REC_FRMT,TKEY_RKEY_RETAILER_ID,TKEY_RKEY_CLERK_ID,DATA_FLAG,TYPE,  RTE_STAT,ORIGINATOR,RESPONDER,ISS_CDE,ENTRY_TIME,EXIT_TIME,RE_ENTRY_TIME,TRAN_DATE,TRAN_TIM,POST_DAT,ACQ_ICHG_SETL_DAT,ISS_ICHG_SETL_DAT,SEQ_NUM,TERM_NAME_LOC,TERM_OWNER_NAME,TERM_CITY,TERM_ST,TERM_CNTRY_CDE,BRCH_ID,USER_FID,TERM_TIM_OFST,ACQ_INST_ID_NUM,RCV_INST_ID_NUM,TERM_TYPE,CLERK_ID,CTR_AUTH,CTR_AUTH_GRP,CTR_AUTH_USER_ID,RETL_SIC_CDE,ORIG,DEST,TRAN_CDE,CRD_TYPE, ACCT,RESP_CDE,AMOUNT_1,AMOUNT_2,EXPIRY_DATE,TRACK2,PIN_OFST,PRE_AUTH_SEQ_NUM,INVOICE_NUM,ORIG_INVOICE_NUM,AUTHORIZER,AUTH_IND,SHIFT_NUM_2,BATCH_SEQ_NUM,APPRV_CODE,APPRV_CODE_LENGTH,ICHG_RESP,PSEUDO_TERM_ID,RFRL_PHONE,DUMMY_1,DFT_CAPTURE_FLAG,SELT_FLAG,RVRL_CODE,REA_FOR_CHRGBCK,NUM_OF_CHRGBCK,PT_SRV_COND_CODE,PT_SRV_ENTRY_MODE,AUTH_IND2,ORIG_CRNCY_CODE,MULTI_CRNY_AUTH_CRNCY_CODE,MULTY_CRNCY_AUTH_CONV_RATE,MULTI_CRNCY_SETL_CRNCY_CODE,MULTI_CRNCY_SETL_CONV_RATE,MULTI_CRNCY_CONV_DAT_TIME,REFR_IMP_IND,REFR_AVAIL_CR,REFR_CR_LMT,REFR_CR_BAL,REFR_TTL,REFR_CUR,ADJ_SETL_IMPACT_FLAG,REFR_IND,FRWD_INST_ID_NUM,CRD_ACCPT_ID_NUM,CRD_ISS_ID_NUM,ORIG_MSG_TYPE,ORIG_TRAN_TIM,ORIG_TRAN_DATE,ORIG_SEQ_NUM,ORIG_B24_POST_DATE,EXCP_RSN_CODE,OVRRDE_FLAG, ADDR,ZIP,ADDR_VERFY_STAT,PIN_IND,PIN_TRIES,PRE_AUTH_TS,PRE_AUTH_HLDS_STAT,USER_FID2,USER_DATA_D_LEN,USER_DATA_D_INFO,DCRS_REMARKS,FILEDATE,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		String update_query = "INSERT INTO SWITCH_DATA_VALIDATION(FILENAME,FILEDATE,COUNT,FILETYPE) VALUES(?,?,?,?)";

		String DATE_TIME = "", REC_TYP = "", CRD_LN = "", CRD_FIID = "", CRAD_NUM = "", CRD_MBR_NUM = "",
				RETL_KEY_IN = "", RDF_KEY = "", RDF_KEY_GRP = "", RDF_KEY_REGN = "", RDF_KEY_ID = "", TERM_ID = "",
				SHIFT_NUM = "", BATCH_NUM = "", TERM_IN = "", TERM_FIID = "", TERM_ID2 = "", TERM_TIME = "",
				TERM_ID3 = "", TKEY_RKEY_REC_FRMT = "", TKEY_RKEY_RETAILER_ID = "", TKEY_RKEY_CLERK_ID = "",
				DATA_FLAG = "", TYPE = "", RTE_STAT = "", ORIGINATOR = "", RESPONDER = "", ISS_CDE = "",
				ENTRY_TIME = "", EXIT_TIME = "", RE_ENTRY_TIME = "", TRAN_DATE = "", TRAN_TIM = "", POST_DAT = "",
				ACQ_ICHG_SETL_DAT = "", ISS_ICHG_SETL_DAT = "", SEQ_NUM = "", TERM_NAME_LOC = "", TERM_OWNER_NAME = "",
				TERM_CITY = "", TERM_ST = "", TERM_CNTRY_CDE = "", BRCH_ID = "", USER_FID = "", TERM_TIM_OFST = "",
				ACQ_INST_ID_NUM = "", RCV_INST_ID_NUM = "", TERM_TYPE = "", CLERK_ID = "", CTR_AUTH = "",
				CTR_AUTH_GRP = "", CTR_AUTH_USER_ID = "", RETL_SIC_CDE = "", ORIG = "", DEST = "", TRAN_CDE = "",
				CRD_TYPE = "", ACCT = "", RESP_CDE = "", AMOUNT_1 = "", AMOUNT_2 = "", EXPIRY_DATE = "", TRACK2 = "",
				PIN_OFST = "", PRE_AUTH_SEQ_NUM = "", INVOICE_NUM = "", ORIG_INVOICE_NUM = "", AUTHORIZER = "",
				AUTH_IND = "", SHIFT_NUM_2 = "", BATCH_SEQ_NUM = "", APPRV_CODE = "", APPRV_CODE_LENGTH = "",
				ICHG_RESP = "", PSEUDO_TERM_ID = "", RFRL_PHONE = "", DUMMY_1 = "", DFT_CAPTURE_FLAG = "",
				SELT_FLAG = "", RVRL_CODE = "", REA_FOR_CHRGBCK = "", NUM_OF_CHRGBCK = "", PT_SRV_COND_CODE = "",
				PT_SRV_ENTRY_MODE = "", AUTH_IND2 = "", ORIG_CRNCY_CODE = "", MULTI_CRNY_AUTH_CRNCY_CODE = "",
				MULTY_CRNCY_AUTH_CONV_RATE = "", MULTI_CRNCY_SETL_CRNCY_CODE = "", MULTI_CRNCY_SETL_CONV_RATE = "",
				MULTI_CRNCY_CONV_DAT_TIME = "", REFR_IMP_IND = "", REFR_AVAIL_CR = "", REFR_CR_LMT = "",
				REFR_CR_BAL = "", REFR_TTL = "", REFR_CUR = "", ADJ_SETL_IMPACT_FLAG = "", REFR_IND = "",
				FRWD_INST_ID_NUM = "", CRD_ACCPT_ID_NUM = "", CRD_ISS_ID_NUM = "", ORIG_MSG_TYPE = "",
				ORIG_TRAN_TIM = "", ORIG_TRAN_DATE = "", ORIG_SEQ_NUM = "", ORIG_B24_POST_DATE = "", EXCP_RSN_CODE = "",
				OVRRDE_FLAG = "", ADDR = "", ZIP = "", ADDR_VERFY_STAT = "", PIN_IND = "", PIN_TRIES = "",
				PRE_AUTH_TS = "", PRE_AUTH_HLDS_STAT = "", USER_FID2 = "", USER_DATA_D_LEN = "", USER_DATA_D_INFO = "",
				DCRS_REMARKS = "", FILEDATE = "", FILENAME = "", Second_DATA = "", DATA_LINE = "", FIRST_DATA = "";
		try {
			con.setAutoCommit(false);
			int batchCount = 0;
			PreparedStatement ps = con.prepareStatement(InsertQuery);
			PreparedStatement updatps = con.prepareStatement(update_query);

			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));

			while ((thisline = br.readLine()) != null) {

				// System.out.println("count"+ count);

				// count++;
				// System.out.println("data "+ thisline.trim().substring(0,50));

				//
				if (count == 1 && thisline.trim().length() == 2824) {

					String pat = "[^\\w ]";
					thisline = thisline.trim().replaceAll(pat, " ");

					thisline = thisline.trim().replaceAll(thisline.substring(5, 153), "");

				}

				if (count == 1 && thisline.trim().length() == 2874) {

					String pat = "[^\\w ]";
					thisline = thisline.trim().replaceAll(pat, " ");
					// System.out.println("data2s " + thisline.substring(0, 150));
					thisline = thisline.trim().replaceAll(thisline.substring(0, 150), "DD");

					// System.out.println("replace valuew " + thisline.trim());
				}
				// System.out.println("length " + thisline.trim().length());
				if (thisline.trim().length() == 0 || thisline.trim().length() == 192 || thisline.trim().length() == 5
						|| thisline.trim().length() == 241 || thisline.trim().length() == 848) {

					continue;
				} else {

					if (thisline.trim().length() == 898 || thisline.trim().length() == 802
							|| thisline.trim().length() == 2026) {
						FIRST_DATA = thisline.substring(8, 14).trim();
						if (FIRST_DATA.contains("1828DR")) {
							DATE_TIME = thisline.substring(14, 33).trim();
							REC_TYP = thisline.substring(33, 35).trim();
							CRD_LN = thisline.substring(35, 39).trim();
							CRD_FIID = thisline.substring(39, 43).trim();
							CRAD_NUM = thisline.substring(43, 62).trim();
							CRD_MBR_NUM = thisline.substring(62, 65).trim();
							RETL_KEY_IN = thisline.substring(65, 69).trim();
							RDF_KEY = thisline.substring(69, 73).trim();
							RDF_KEY_GRP = thisline.substring(73, 77).trim();
							RDF_KEY_REGN = thisline.substring(77, 81).trim();
							RDF_KEY_ID = thisline.substring(81, 100).trim();
							TERM_ID = thisline.substring(100, 116).trim();
							SHIFT_NUM = thisline.substring(116, 119).trim();
							BATCH_NUM = thisline.substring(119, 122).trim();
							TERM_IN = thisline.substring(122, 126).trim();
							TERM_FIID = thisline.substring(126, 130).trim();
							TERM_ID2 = thisline.substring(130, 146).trim();
							TERM_TIME = thisline.substring(146, 154).trim();
							TERM_ID3 = thisline.substring(154, 170).trim();
							TKEY_RKEY_REC_FRMT = thisline.substring(170, 171).trim();
							TKEY_RKEY_RETAILER_ID = thisline.substring(171, 190).trim();
							TKEY_RKEY_CLERK_ID = thisline.substring(190, 196).trim();
							DATA_FLAG = thisline.substring(196, 197).trim();
							TYPE = thisline.substring(197, 201).trim();
							RTE_STAT = thisline.substring(201, 203).trim();
							ORIGINATOR = thisline.substring(203, 204).trim();
							RESPONDER = thisline.substring(204, 205).trim();
							ISS_CDE = thisline.substring(205, 207).trim();
							ENTRY_TIME = thisline.substring(207, 226).trim();
							EXIT_TIME = thisline.substring(226, 245).trim();
							RE_ENTRY_TIME = thisline.substring(245, 264).trim();
							TRAN_DATE = thisline.substring(264, 270).trim();
							TRAN_TIM = thisline.substring(270, 278).trim();
							POST_DAT = thisline.substring(278, 284).trim();
							ACQ_ICHG_SETL_DAT = thisline.substring(284, 290).trim();
							ISS_ICHG_SETL_DAT = thisline.substring(290, 296).trim();
							SEQ_NUM = thisline.substring(296, 308).trim();
							// DUMMY_8 = thisline.substring(308, 333).trim();
							TERM_NAME_LOC = thisline.substring(308, 333).trim();
							TERM_OWNER_NAME = thisline.substring(333, 355).trim();
							// DUMMY_9 = thisline.substring(377, 380).trim();
							TERM_CITY = thisline.substring(355, 368).trim();
							TERM_ST = thisline.substring(368, 371).trim();
							TERM_CNTRY_CDE = thisline.substring(371, 373).trim();
							BRCH_ID = thisline.substring(373, 377).trim();
							USER_FID = thisline.substring(377, 380).trim();
							TERM_TIM_OFST = thisline.substring(380, 385).trim();
							ACQ_INST_ID_NUM = thisline.substring(385, 396).trim();
							RCV_INST_ID_NUM = thisline.substring(396, 407).trim();
							TERM_TYPE = thisline.substring(407, 409).trim();
							CLERK_ID = thisline.substring(409, 415).trim();
							CTR_AUTH = thisline.substring(415, 427).trim();
							// CTR_AUTH_GRP = thisline.substring(427, 431).trim();
							// CTR_AUTH_USER_ID = thisline.substring(431, 439).trim();
							RETL_SIC_CDE = thisline.substring(427, 431).trim();
							ORIG = thisline.substring(431, 435).trim();
							DEST = thisline.substring(435, 439).trim();
							// DUMMY_11 = thisline.substring(438, 439).trim();
							TRAN_CDE = thisline.substring(439, 445).trim();
							CRD_TYPE = thisline.substring(445, 447).trim();
							ACCT = thisline.substring(447, 466).trim();
							RESP_CDE = thisline.substring(466, 469).trim();
							AMOUNT_1 = thisline.substring(469, 488).trim();
							AMOUNT_2 = thisline.substring(488, 507).trim();
							EXPIRY_DATE = thisline.substring(507, 511).trim();
							TRACK2 = thisline.substring(511, 551).trim();

							PIN_OFST = thisline.substring(551, 567).trim();
							PRE_AUTH_SEQ_NUM = thisline.substring(567, 579).trim();
							INVOICE_NUM = thisline.substring(579, 589).trim();
							ORIG_INVOICE_NUM = thisline.substring(589, 599).trim();
							AUTHORIZER = thisline.substring(599, 615).trim();
							AUTH_IND = thisline.substring(615, 616).trim();
							SHIFT_NUM_2 = thisline.substring(616, 619).trim();
							BATCH_SEQ_NUM = thisline.substring(619, 622).trim();
							APPRV_CODE = thisline.substring(622, 630).trim();
							APPRV_CODE_LENGTH = thisline.substring(630, 631).trim();
							ICHG_RESP = thisline.substring(631, 639).trim();
							PSEUDO_TERM_ID = thisline.substring(639, 643).trim();
							RFRL_PHONE = thisline.substring(643, 663).trim();
							DUMMY_1 = thisline.substring(663, 666).trim();
							DFT_CAPTURE_FLAG = thisline.substring(666, 667).trim();
							SELT_FLAG = thisline.substring(667, 668).trim();
							RVRL_CODE = thisline.substring(668, 670).trim();
							REA_FOR_CHRGBCK = thisline.substring(670, 672).trim();
							NUM_OF_CHRGBCK = thisline.substring(672, 673).trim();
							PT_SRV_COND_CODE = thisline.substring(673, 675).trim();
							PT_SRV_ENTRY_MODE = thisline.substring(675, 678).trim();
							AUTH_IND2 = thisline.substring(678, 679).trim();
							ORIG_CRNCY_CODE = thisline.substring(679, 682).trim();
							MULTI_CRNY_AUTH_CRNCY_CODE = thisline.substring(682, 685).trim();
							MULTY_CRNCY_AUTH_CONV_RATE = thisline.substring(685, 693).trim();
							MULTI_CRNCY_SETL_CRNCY_CODE = thisline.substring(693, 696).trim();
							MULTI_CRNCY_SETL_CONV_RATE = thisline.substring(696, 704).trim();
							MULTI_CRNCY_CONV_DAT_TIME = thisline.substring(704, 723).trim();
							REFR_IMP_IND = thisline.substring(723, 724).trim();
							REFR_AVAIL_CR = thisline.substring(724, 725).trim();
							REFR_CR_LMT = thisline.substring(725, 726).trim();
							REFR_CR_BAL = thisline.substring(726, 727).trim();
							REFR_TTL = thisline.substring(727, 728).trim();
							REFR_CUR = thisline.substring(728, 729).trim();
							ADJ_SETL_IMPACT_FLAG = thisline.substring(729, 730).trim();
							REFR_IND = thisline.substring(730, 734).trim();
							FRWD_INST_ID_NUM = thisline.substring(734, 750).trim();
							CRD_ACCPT_ID_NUM = thisline.substring(750, 761).trim();
							CRD_ISS_ID_NUM = thisline.substring(761, 772).trim();
							ORIG_MSG_TYPE = thisline.substring(772, 776).trim();
							ORIG_TRAN_TIM = thisline.substring(776, 784).trim();
							ORIG_TRAN_DATE = thisline.substring(784, 790).trim();
							ORIG_SEQ_NUM = thisline.substring(790, 802).trim();
							ORIG_B24_POST_DATE = thisline.substring(802, 806).trim();
							EXCP_RSN_CODE = thisline.substring(806, 809).trim();
							OVRRDE_FLAG = thisline.substring(809, 810).trim();
							ADDR = thisline.substring(810, 830).trim();
							ZIP = thisline.substring(830, 839).trim();
							ADDR_VERFY_STAT = thisline.substring(839, 840).trim();
							PIN_IND = thisline.substring(840, 841).trim();
							PIN_TRIES = thisline.substring(841, 842).trim();
							PRE_AUTH_TS = thisline.substring(842, 856).trim();
							PRE_AUTH_HLDS_STAT = thisline.substring(856, 857).trim();
							USER_FID2 = thisline.substring(857, 890).trim();
							USER_DATA_D_LEN = thisline.substring(890, 894).trim();
							USER_DATA_D_INFO = thisline.substring(894, 895).trim();

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

							ps.addBatch();

							// logger.info("Executed Batch SuccessFully " + count);

							continue;

						}

					} else {

						// logger.info("length "+ thisline.trim().length() +" "+ thisline.substring(8,
						// 14).trim() );
						FIRST_DATA = thisline.substring(8, 14).trim();

						if (FIRST_DATA.contains("1828DR")) {
							DATE_TIME = thisline.substring(14, 33).trim();
							REC_TYP = thisline.substring(33, 35).trim();
							CRD_LN = thisline.substring(35, 39).trim();
							CRD_FIID = thisline.substring(39, 43).trim();
							CRAD_NUM = thisline.substring(43, 62).trim();
							CRD_MBR_NUM = thisline.substring(62, 65).trim();
							RETL_KEY_IN = thisline.substring(65, 69).trim();
							RDF_KEY = thisline.substring(69, 73).trim();
							RDF_KEY_GRP = thisline.substring(73, 77).trim();
							RDF_KEY_REGN = thisline.substring(77, 81).trim();
							RDF_KEY_ID = thisline.substring(81, 100).trim();
							TERM_ID = thisline.substring(100, 116).trim();
							SHIFT_NUM = thisline.substring(116, 119).trim();
							BATCH_NUM = thisline.substring(119, 122).trim();
							TERM_IN = thisline.substring(122, 126).trim();
							TERM_FIID = thisline.substring(126, 130).trim();
							TERM_ID2 = thisline.substring(130, 146).trim();
							TERM_TIME = thisline.substring(146, 154).trim();
							TERM_ID3 = thisline.substring(154, 170).trim();
							TKEY_RKEY_REC_FRMT = thisline.substring(170, 171).trim();
							TKEY_RKEY_RETAILER_ID = thisline.substring(171, 190).trim();
							TKEY_RKEY_CLERK_ID = thisline.substring(190, 196).trim();
							DATA_FLAG = thisline.substring(196, 197).trim();
							TYPE = thisline.substring(197, 201).trim();
							RTE_STAT = thisline.substring(201, 203).trim();
							ORIGINATOR = thisline.substring(203, 204).trim();
							RESPONDER = thisline.substring(204, 205).trim();
							ISS_CDE = thisline.substring(205, 207).trim();
							ENTRY_TIME = thisline.substring(207, 226).trim();
							EXIT_TIME = thisline.substring(226, 245).trim();
							RE_ENTRY_TIME = thisline.substring(245, 264).trim();
							TRAN_DATE = thisline.substring(264, 270).trim();
							TRAN_TIM = thisline.substring(270, 278).trim();
							POST_DAT = thisline.substring(278, 284).trim();
							ACQ_ICHG_SETL_DAT = thisline.substring(284, 290).trim();
							ISS_ICHG_SETL_DAT = thisline.substring(290, 296).trim();
							SEQ_NUM = thisline.substring(296, 308).trim();
							// DUMMY_8 = thisline.substring(308, 333).trim();
							TERM_NAME_LOC = thisline.substring(308, 333).trim();
							TERM_OWNER_NAME = thisline.substring(333, 355).trim();
							// DUMMY_9 = thisline.substring(377, 380).trim();
							TERM_CITY = thisline.substring(355, 368).trim();
							TERM_ST = thisline.substring(368, 371).trim();
							TERM_CNTRY_CDE = thisline.substring(371, 373).trim();
							BRCH_ID = thisline.substring(373, 377).trim();
							USER_FID = thisline.substring(377, 380).trim();
							TERM_TIM_OFST = thisline.substring(380, 385).trim();
							ACQ_INST_ID_NUM = thisline.substring(385, 396).trim();
							RCV_INST_ID_NUM = thisline.substring(396, 407).trim();
							TERM_TYPE = thisline.substring(407, 409).trim();
							CLERK_ID = thisline.substring(409, 415).trim();
							CTR_AUTH = thisline.substring(415, 427).trim();
							// CTR_AUTH_GRP = thisline.substring(427, 431).trim();
							// CTR_AUTH_USER_ID = thisline.substring(431, 439).trim();
							RETL_SIC_CDE = thisline.substring(427, 431).trim();
							ORIG = thisline.substring(431, 435).trim();
							DEST = thisline.substring(435, 439).trim();
							// DUMMY_11 = thisline.substring(438, 439).trim();
							TRAN_CDE = thisline.substring(439, 445).trim();
							CRD_TYPE = thisline.substring(445, 447).trim();
							ACCT = thisline.substring(447, 466).trim();
							RESP_CDE = thisline.substring(466, 469).trim();
							AMOUNT_1 = thisline.substring(469, 488).trim();
							AMOUNT_2 = thisline.substring(488, 507).trim();
							EXPIRY_DATE = thisline.substring(507, 511).trim();
							TRACK2 = thisline.substring(511, 551).trim();

							PIN_OFST = thisline.substring(551, 567).trim();
							PRE_AUTH_SEQ_NUM = thisline.substring(567, 579).trim();
							INVOICE_NUM = thisline.substring(579, 589).trim();
							ORIG_INVOICE_NUM = thisline.substring(589, 599).trim();
							AUTHORIZER = thisline.substring(599, 615).trim();
							AUTH_IND = thisline.substring(615, 616).trim();
							SHIFT_NUM_2 = thisline.substring(616, 619).trim();
							BATCH_SEQ_NUM = thisline.substring(619, 622).trim();
							APPRV_CODE = thisline.substring(622, 630).trim();
							APPRV_CODE_LENGTH = thisline.substring(630, 631).trim();
							ICHG_RESP = thisline.substring(631, 639).trim();
							PSEUDO_TERM_ID = thisline.substring(639, 643).trim();
							RFRL_PHONE = thisline.substring(643, 663).trim();
							DUMMY_1 = thisline.substring(663, 666).trim();
							DFT_CAPTURE_FLAG = thisline.substring(666, 667).trim();
							SELT_FLAG = thisline.substring(667, 668).trim();
							RVRL_CODE = thisline.substring(668, 670).trim();
							REA_FOR_CHRGBCK = thisline.substring(670, 672).trim();
							NUM_OF_CHRGBCK = thisline.substring(672, 673).trim();
							PT_SRV_COND_CODE = thisline.substring(673, 675).trim();
							PT_SRV_ENTRY_MODE = thisline.substring(675, 678).trim();
							AUTH_IND2 = thisline.substring(678, 679).trim();
							ORIG_CRNCY_CODE = thisline.substring(679, 682).trim();
							MULTI_CRNY_AUTH_CRNCY_CODE = thisline.substring(682, 685).trim();
							MULTY_CRNCY_AUTH_CONV_RATE = thisline.substring(685, 693).trim();
							MULTI_CRNCY_SETL_CRNCY_CODE = thisline.substring(693, 696).trim();
							MULTI_CRNCY_SETL_CONV_RATE = thisline.substring(696, 704).trim();
							MULTI_CRNCY_CONV_DAT_TIME = thisline.substring(704, 723).trim();
							REFR_IMP_IND = thisline.substring(723, 724).trim();
							REFR_AVAIL_CR = thisline.substring(724, 725).trim();
							REFR_CR_LMT = thisline.substring(725, 726).trim();
							REFR_CR_BAL = thisline.substring(726, 727).trim();
							REFR_TTL = thisline.substring(727, 728).trim();
							REFR_CUR = thisline.substring(728, 729).trim();
							ADJ_SETL_IMPACT_FLAG = thisline.substring(729, 730).trim();
							REFR_IND = thisline.substring(730, 734).trim();
							FRWD_INST_ID_NUM = thisline.substring(734, 750).trim();
							CRD_ACCPT_ID_NUM = thisline.substring(750, 761).trim();
							CRD_ISS_ID_NUM = thisline.substring(761, 772).trim();
							ORIG_MSG_TYPE = thisline.substring(772, 776).trim();
							ORIG_TRAN_TIM = thisline.substring(776, 784).trim();
							ORIG_TRAN_DATE = thisline.substring(784, 790).trim();
							ORIG_SEQ_NUM = thisline.substring(790, 802).trim();
							ORIG_B24_POST_DATE = thisline.substring(802, 806).trim();
							EXCP_RSN_CODE = thisline.substring(806, 809).trim();
							OVRRDE_FLAG = thisline.substring(809, 810).trim();
							ADDR = thisline.substring(810, 830).trim();
							ZIP = thisline.substring(830, 839).trim();
							ADDR_VERFY_STAT = thisline.substring(839, 840).trim();
							PIN_IND = thisline.substring(840, 841).trim();
							PIN_TRIES = thisline.substring(841, 842).trim();
							PRE_AUTH_TS = thisline.substring(842, 856).trim();
							PRE_AUTH_HLDS_STAT = thisline.substring(856, 857).trim();
							USER_FID2 = thisline.substring(857, 890).trim();
							USER_DATA_D_LEN = thisline.substring(890, 894).trim();
							USER_DATA_D_INFO = thisline.substring(894, 895).trim();

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

							ps.addBatch();

							// logger.info("Executed Batch SuccessFully " + count);

						}

						Second_DATA = thisline.substring(1836, 1842).trim();
						// System.out.println("Second_DATA "+ Second_DATA);//1093
						if (Second_DATA.contains("1828DR")) {
							// System.out.println("d2");
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
							// DUMMY_8 = thisline.substring(308, 333).trim();
							TERM_NAME_LOC = thisline.substring(2136, 2161).trim();
							TERM_OWNER_NAME = thisline.substring(2161, 2183).trim();
							// DUMMY_9 = thisline.substring(377, 380).trim();
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
							// CTR_AUTH_GRP = thisline.substring(2255, 2259).trim();
							// CTR_AUTH_USER_ID = thisline.substring(2259, 2267).trim();
							RETL_SIC_CDE = thisline.substring(2255, 2259).trim();
							ORIG = thisline.substring(2259, 2263).trim();
							DEST = thisline.substring(2263, 2267).trim();
							// DUMMY_11 = thisline.substring(438, 439).trim();
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

							// count2++;
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

							ps.addBatch();

							// System.out.println("excuu ds 2");

						}

					}

				}

				if (++batchCount % batchSize == 0) {

					batchNumber++;
					System.out.println("Batch Executed is " + batchNumber);
					ps.executeBatch();
					con.commit();
				}

			}

			logger.info("Executed Batch SuccessFully");

			ps.executeBatch();
			con.commit();
			if (count > 0) {
				updatps.setString(1, FILENAME);
				updatps.setString(2, FILEDATE);
				updatps.setString(3, String.valueOf(count));
				updatps.setString(4, "POS");
				updatps.execute();
				con.commit();
				logger.info("update Batch Completed");
			}

			output.put("result", true);

			output.put("msg", "File Uploaded and Record count is " + count);
			logger.info("Completed process " + count);
			return output;

		} catch (Exception e) {

			logger.info("Exception in lineNumber " + count);
			logger.info("Exception in ReadUCOATMSwitchData " + e);
			output.put("result", false);
			output.put("msg", "Issue at Line Number " + lineNumber);
			return output;
		}

	}

	// ICCW File Reading Switch

	public boolean uploadSwitchData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {
		logger.info("uploadSwitchData method ak");

		String stLine = null;
		int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0;
		boolean batchExecuted = false;

		/*
		 * String InsertQuery =
		 * "INSERT INTO CBS_UCO_RAWDATA_TEMP(SOLID, TRAN_AMOUNT, PART_TRAN_TYPE, CARD_NUMBER, RRN, ACC_NUMBER, SYSDT, SYS_TIME, CMD, TRAN_TYPE, DEVICE_ID, TRAN_ID, TRAN_DATE, FEE, SRV_TAX, SRV_CHRG, VISAIND, CREATEDBY, FILEDATE, BANK_NAME) "
		 * +
		 * "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'),'UCO')"
		 * ;
		 */

		// String InsertQuery = "INSERT INTO SWITCH_RAWDATA(PAN,FPAN, ACCOUNTNO,
		// ACCTNUM, BRANCHCODE, MSGTYPE, AMOUNT, ISS_CURRENCY_CODE, LOCAL_DATE,
		// LOCAL_TIME, ACQUIRER, PAN2, TERMID, TRACE, ISSUER, AUTHNUM, RESPCODE,
		// TXNSRC, TXNDEST, ACQ_CURRENCY_CODE, AMOUNT_EQUIV, NETWORK,
		// MERCHANT_TYPE, CREATEDBY, FILEDATE) "
		// + "VALUES(?, ibkl_encrypt_decrypt.ibkl_set_encrypt_val(?), ?, ?, ?,
		// ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?,?,?,?, ?,
		// TO_DATE(?,'DD/MM/YYYY'))";

		String InsertQuery = "INSERT INTO SWITCH_RAWDATA(PAN,FPAN, ACCOUNTNO, ACCTNUM, BRANCHCODE, MSGTYPE, AMOUNT, ISS_CURRENCY_CODE, LOCAL_DATE, LOCAL_TIME, ACQUIRER, PAN2, TERMID, TRACE, ISSUER, AUTHNUM, RESPCODE, TXNSRC, TXNDEST,CODE_1,CODE_2, ACQ_CURRENCY_CODE, AMOUNT_EQUIV, NETWORK, MERCHANT_TYPE, CREATEDBY,FILE_NAME, FILEDATE) "
				+ "VALUES(?, ibkl_encrypt_decrypt.ibkl_set_encrypt_val(?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?,?,?, ?, ?, ?,?,?,?, ?, ? , TO_DATE(?,'DD/MM/YYYY'))";

		// String InsertQuery = "INSERT INTO SWITCH_RAWDATA(PAN,FPAN, ACCOUNTNO,
		// ACCTNUM, BRANCHCODE, MSGTYPE, AMOUNT, ISS_CURRENCY_CODE, LOCAL_DATE,
		// LOCAL_TIME, ACQUIRER, PAN2, TERMID, TRACE, ISSUER, AUTHNUM, RESPCODE,
		// TXNSRC, TXNDEST,CODE_1, CODE_2 , ACQ_CURRENCY_CODE, AMOUNT_EQUIV,
		// NETWORK, MERCHANT_TYPE, CREATEDBY, FILEDATE) "
		// + "VALUES(?, ibkl_encrypt_decrypt.ibkl_set_encrypt_val(?), ?,?,?,?,
		// ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?,
		// TO_DATE(?,'DD/MM/YYYY'))";

		String delQuery = "delete from CBS_UCO_RAWDATA_TEMP";
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
			// PgpHelper helper = new PgpHelper();

			FileOutputStream out = new FileOutputStream(newPath);

			// InputStream in =
			// helper.decryptFileWithInputStram(file.getInputStream(), out,
			// keyIn, "Atmcell@#12345".toCharArray());
			File outFile = new File(newPath);

			System.out.println("File reading from Path: " + newPath);
			System.out.println("File reading : " + outFile);
			PreparedStatement delpst = con.prepareStatement(delQuery);
			delpst.execute();

			// BufferedReader br = new BufferedReader(new
			// InputStreamReader(file.getInputStream()));
			BufferedReader br = new BufferedReader(new FileReader(outFile));

			try {
				// BufferedReader br = new BufferedReader(new
				// InputStreamReader(in));

				PreparedStatement ps = con.prepareStatement(InsertQuery);

				while ((stLine = br.readLine()) != null) {

					/*
					 * if(stLine.startsWith("999  ,H") || stLine.startsWith( "999  ,F")){ continue;
					 * }
					 */
					lineNumber++;
					batchExecuted = false;
					sr_no = 1;

					String[] splitData = stLine.split("\\^");

					for (int i = 0; i <= (splitData.length - 1); i++) {
						// if (i != 4 && i != 11 && i != 14 && i != 19 && i !=
						// 20 && i != 23 && i != 24 && i != 25 && i != 26 && i
						// != 27 && i != 28 && i != 31) {
						// if (i != 6 && i != 13 && i != 16 && i != 21 && i !=
						// 22 && i != 25 && i != 26 && i != 27 && i != 28 && i
						// != 29 && i != 30 && i != 33) {
						if (i != 6 && i != 13 && i != 16 && i != 25 && i != 26 && i != 27 && i != 28 && i != 29
								&& i != 30 && i != 33) {
							// System.out.println("i "+i+" data
							// "+splitData[i-1]);
							if (i == 0) {
								String cardNumber = formatCardNumber(splitData[i].trim());
								ps.setString(sr_no++, cardNumber);
								ps.setString(sr_no++, splitData[i].trim());
							} else {
								ps.setString(sr_no++, splitData[i].trim());
							}
						}
					}

					// System.out.println(sr_no);
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
				System.out.println("Reading data " + new Date().toString());
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
		// TODO Auto-generated method stub
		int totalLength = cardNumber.length();
		int firstSixDigitLength = Math.min(totalLength, 6);
		int lastFourDigitLength = Math.min(totalLength - 10, 4);

		String firstSixDigits = cardNumber.substring(0, firstSixDigitLength);
		String midX = "XXXXXX";
		String lastFourDigits = cardNumber.substring(totalLength - lastFourDigitLength);

		StringBuilder xx = new StringBuilder();
		for (int i = 0; i < totalLength - 10; i++) {
			xx.append("X");
		}

		String formatedCardNumber = firstSixDigits + xx + lastFourDigits;
		return formatedCardNumber;
	}
}
