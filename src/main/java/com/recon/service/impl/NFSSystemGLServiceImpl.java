package com.recon.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recon.dao.NFSSystemGLDao;
import com.recon.model.NFSGLSummaryPayable;
import com.recon.model.NFSGLSummaryReceivable;
import com.recon.model.NFSPayableGLModel;
import com.recon.model.NFSReceivableGLModel;
import com.recon.service.NFSSystemGLService;

@Service
public class NFSSystemGLServiceImpl implements NFSSystemGLService {

	@Autowired
	NFSSystemGLDao nFSPayableGLDao;

	@Override
	public void processNfsReportPayable(String date, String openingBal, String fundMovefr43Acc, String onlineFundMov,
			String finacleEODbal) {

		nFSPayableGLDao.processNfsReportPayable(date, openingBal, fundMovefr43Acc, onlineFundMov, finacleEODbal);
	}

	@Override
	public String downloadNfsReportPayable(String date) throws Exception {
		String fileName = "NFS_PAYABLE_GL_SUMMARY_" + date + ".xlsx;";

		new NFSPAYABLEXlsx(nFSPayableGLDao, date, fileName).GenrateBook();
		return fileName;
	}

	private class NFSPAYABLEXlsx {

		private NFSSystemGLDao nFSPayableGLDao;
		private String date;
		private String fileName;

		NFSPAYABLEXlsx(NFSSystemGLDao nFSPayableGLDao, String fdate, String filename) {
			this.nFSPayableGLDao = nFSPayableGLDao;
			this.date = fdate;
			this.fileName = filename.replaceAll("/", "-");
		}

		void GenrateBook() throws Exception {

			OutputStream strm = null;
			SXSSFWorkbook book = new SXSSFWorkbook(1000);
			{
				List<NFSGLSummaryPayable> nfsglSummaryPay = nFSPayableGLDao.downloadNfsReportPayable(date);
				SXSSFSheet summSheet = book.createSheet("NFS PAYABLE GL SUMMARY");
				SXSSFRow summSheetheadrow = summSheet.createRow(0);

				summSheetheadrow.createCell(0).setCellValue("GL_DATE");
				summSheetheadrow.createCell(1).setCellValue("PARTICULARS");
				summSheetheadrow.createCell(2).setCellValue("DEBIT_AMT");
				summSheetheadrow.createCell(3).setCellValue("CREDIT_AMT");

				if (nfsglSummaryPay != null) {
					if (!nfsglSummaryPay.isEmpty()) {
						for (int rowdata = 0; rowdata < nfsglSummaryPay.size(); rowdata++) {
							SXSSFRow row = summSheet.createRow(rowdata + 1);
							row.createCell(0).setCellValue(nfsglSummaryPay.get(rowdata).getGlDate().substring(0, 10));
							row.createCell(1).setCellValue(nfsglSummaryPay.get(rowdata).getParticulars());
							row.createCell(2).setCellValue(nfsglSummaryPay.get(rowdata).getDebitAmount());
							row.createCell(3).setCellValue(nfsglSummaryPay.get(rowdata).getCreditAmount());
						}
					}
				}

				nfsglSummaryPay = null;
			}
			// ****************************WITHDRAWAL SETTLEMENT AMOUNT**************

			{
				List<NFSPayableGLModel> withdrawalSetAmtPay = nFSPayableGLDao.withdrawalStlmntAmtPayable(date);
				SXSSFSheet summSheet = book.createSheet("WITHDRAWAL SETTLEMENT AMT");
				SXSSFRow summSheetheadrow = summSheet.createRow(0);

				summSheetheadrow.createCell(0).setCellValue("CYCLE");
				summSheetheadrow.createCell(1).setCellValue("FILEDATE");
				summSheetheadrow.createCell(2).setCellValue("CREATDED_BY");
				summSheetheadrow.createCell(3).setCellValue("NO_OF_TXNS");
				summSheetheadrow.createCell(4).setCellValue("DEBIT");
				summSheetheadrow.createCell(5).setCellValue("CREDIT");
				summSheetheadrow.createCell(6).setCellValue("DESCRIPTION");

				if (withdrawalSetAmtPay != null) {
					if (!withdrawalSetAmtPay.isEmpty()) {
						for (int rowdata = 0; rowdata < withdrawalSetAmtPay.size(); rowdata++) {
							SXSSFRow row = summSheet.createRow(rowdata + 1);
							row.createCell(0).setCellValue(withdrawalSetAmtPay.get(rowdata).getCYCLE());
							row.createCell(1).setCellValue(withdrawalSetAmtPay.get(rowdata).getFiledate());
							row.createCell(2).setCellValue(withdrawalSetAmtPay.get(rowdata).getCREATEDBY());
							row.createCell(3).setCellValue(withdrawalSetAmtPay.get(rowdata).getNO_OF_TXNS());
							row.createCell(4).setCellValue(withdrawalSetAmtPay.get(rowdata).getDEBIT());
							row.createCell(5).setCellValue(withdrawalSetAmtPay.get(rowdata).getCREDIT());
							row.createCell(6).setCellValue(withdrawalSetAmtPay.get(rowdata).getDESCRIPTION());
						}
					}
				}

				withdrawalSetAmtPay = null;
			}
			// ********************************LATE REVERSAL SETTLEMENT AMOUNT**********************
			{
				List<NFSPayableGLModel> lateReversalSetAmtPay = nFSPayableGLDao.lateReversalStlmntAmtPayable(date);
				SXSSFSheet summSheet = book.createSheet("LATE REVERSAL SETTLEMENT AMT");
				SXSSFRow summSheetheadrow = summSheet.createRow(0);

				summSheetheadrow.createCell(0).setCellValue("TRANSTYPE");
				summSheetheadrow.createCell(1).setCellValue("RESP_CODE");
				summSheetheadrow.createCell(2).setCellValue("CARD_NO");
				summSheetheadrow.createCell(3).setCellValue("RRN");
				summSheetheadrow.createCell(4).setCellValue("STAN_NO");
				summSheetheadrow.createCell(5).setCellValue("ACQ");
				summSheetheadrow.createCell(6).setCellValue("ISS");
				summSheetheadrow.createCell(7).setCellValue("TRANS_DATE");
				summSheetheadrow.createCell(8).setCellValue("TRANS_TIME");
				summSheetheadrow.createCell(9).setCellValue("ATM_ID");
				summSheetheadrow.createCell(10).setCellValue("SETTLE_DATE");
				summSheetheadrow.createCell(11).setCellValue("REQUEST_AMT");
				summSheetheadrow.createCell(12).setCellValue("RECEIVED_AMT");
				summSheetheadrow.createCell(13).setCellValue("STATUS");
				summSheetheadrow.createCell(14).setCellValue("DCRS_REMARKS");
				summSheetheadrow.createCell(15).setCellValue("FILE_DATE");
				summSheetheadrow.createCell(16).setCellValue("CYCLE");
				summSheetheadrow.createCell(17).setCellValue("FPAN");

				if (lateReversalSetAmtPay != null) {
					if (!lateReversalSetAmtPay.isEmpty()) {
						for (int rowdata = 0; rowdata < lateReversalSetAmtPay.size(); rowdata++) {
							SXSSFRow row = summSheet.createRow(rowdata + 1);
							row.createCell(0).setCellValue(lateReversalSetAmtPay.get(rowdata).getTranstype());
							row.createCell(1).setCellValue(lateReversalSetAmtPay.get(rowdata).getResp_code());
							row.createCell(2).setCellValue(lateReversalSetAmtPay.get(rowdata).getCardno());
							row.createCell(3).setCellValue(lateReversalSetAmtPay.get(rowdata).getRRN());
							row.createCell(4).setCellValue(lateReversalSetAmtPay.get(rowdata).getStanno());
							row.createCell(0).setCellValue(lateReversalSetAmtPay.get(rowdata).getACQ());
							row.createCell(1).setCellValue(lateReversalSetAmtPay.get(rowdata).getISS());
							row.createCell(2).setCellValue(lateReversalSetAmtPay.get(rowdata).getTRAN_DATE());
							row.createCell(3).setCellValue(lateReversalSetAmtPay.get(rowdata).getTrans_time());
							row.createCell(4).setCellValue(lateReversalSetAmtPay.get(rowdata).getAtmid());
							row.createCell(0).setCellValue(lateReversalSetAmtPay.get(rowdata).getSettledate());
							row.createCell(1).setCellValue(lateReversalSetAmtPay.get(rowdata).getRequestamt());
							row.createCell(2).setCellValue(lateReversalSetAmtPay.get(rowdata).getReceivedamt());
							row.createCell(3).setCellValue(lateReversalSetAmtPay.get(rowdata).getSTATUS());
							row.createCell(4).setCellValue(lateReversalSetAmtPay.get(rowdata).getDcrs_remarks());
							row.createCell(0)
									.setCellValue(lateReversalSetAmtPay.get(rowdata).getFiledate().substring(0, 10));
							row.createCell(1).setCellValue(lateReversalSetAmtPay.get(rowdata).getCYCLE());
							row.createCell(2).setCellValue(lateReversalSetAmtPay.get(rowdata).getFPAN());

						}
					}
				}

				lateReversalSetAmtPay = null;
			}
			// *****************************PBGB SETTLEMENT AMOUNT POSTED****************			
			{
				List<NFSPayableGLModel> pbgbstlmntPostPay = nFSPayableGLDao.pbgbStlmntAmtPostedPayable(date);
				SXSSFSheet summSheet = book.createSheet("PBGB SETTLEMENT AMT POSTED");
				SXSSFRow summSheetheadrow = summSheet.createRow(0);

				summSheetheadrow.createCell(0).setCellValue("SR_NO");
				summSheetheadrow.createCell(1).setCellValue("FILE_DATE");
				summSheetheadrow.createCell(2).setCellValue("NO_OF_TXNS");
				summSheetheadrow.createCell(3).setCellValue("DEBIT");
				summSheetheadrow.createCell(4).setCellValue("CREDIT");
				summSheetheadrow.createCell(5).setCellValue("DESCRIPTION");
				summSheetheadrow.createCell(6).setCellValue("TTUM_NARRATION");
				summSheetheadrow.createCell(7).setCellValue("ACCOUNT_NUMBER");

				if (pbgbstlmntPostPay != null) {
					if (!pbgbstlmntPostPay.isEmpty()) {
						for (int rowdata = 0; rowdata < pbgbstlmntPostPay.size(); rowdata++) {
							SXSSFRow row = summSheet.createRow(rowdata + 1);

							row.createCell(0).setCellValue(pbgbstlmntPostPay.get(rowdata).getSr_no());
							row.createCell(1)
									.setCellValue(pbgbstlmntPostPay.get(rowdata).getFiledate().substring(0, 10));
							row.createCell(2).setCellValue(pbgbstlmntPostPay.get(rowdata).getNO_OF_TXNS());
							row.createCell(3).setCellValue(pbgbstlmntPostPay.get(rowdata).getDEBIT());
							row.createCell(4).setCellValue(pbgbstlmntPostPay.get(rowdata).getCREDIT());
							row.createCell(5).setCellValue(pbgbstlmntPostPay.get(rowdata).getDESCRIPTION());
							row.createCell(6).setCellValue(pbgbstlmntPostPay.get(rowdata).getTtum_naration());
							row.createCell(7).setCellValue(pbgbstlmntPostPay.get(rowdata).getACCOUNT_NUMBER());
						}
					}
				}

				pbgbstlmntPostPay = null;
			}
			// ****************************UNRECONCILED TRANSACTIONS POSTED****************************
			{
				List<NFSPayableGLModel> unreconcilTranPostPayable = nFSPayableGLDao.unreconciledTranPostPayable(date);
				SXSSFSheet summSheet = book.createSheet("UNRECONCILED TRANSACTIONS POSTED");
				SXSSFRow summSheetheadrow = summSheet.createRow(0);

				summSheetheadrow.createCell(0).setCellValue("ACCOUNT_NUMBER");
				summSheetheadrow.createCell(1).setCellValue("CURRENCY_CODE");
				summSheetheadrow.createCell(2).setCellValue("PART_TRAN_TYPE");
				summSheetheadrow.createCell(3).setCellValue("TRANSACTION AMOUNT");
				summSheetheadrow.createCell(4).setCellValue("REFERENCE AMOUNT");
				summSheetheadrow.createCell(5).setCellValue("TRANS_DATE");
				summSheetheadrow.createCell(6).setCellValue("TRANSACTION_PARTICULAR");

				if (unreconcilTranPostPayable != null) {
					if (!unreconcilTranPostPayable.isEmpty()) {
						for (int rowdata = 0; rowdata < unreconcilTranPostPayable.size(); rowdata++) {
							SXSSFRow row = summSheet.createRow(rowdata + 1);

							row.createCell(0).setCellValue(unreconcilTranPostPayable.get(rowdata).getACCOUNT_NUMBER());
							row.createCell(1).setCellValue(unreconcilTranPostPayable.get(rowdata).getCURRENCY_CODE());
							row.createCell(2).setCellValue(unreconcilTranPostPayable.get(rowdata).getPART_TRAN_TYPE());
							row.createCell(3)
									.setCellValue(unreconcilTranPostPayable.get(rowdata).getTRANSACTION_AMOUNT());
							row.createCell(4)
									.setCellValue(unreconcilTranPostPayable.get(rowdata).getREFERENCE_AMOUNT());
							row.createCell(5).setCellValue(unreconcilTranPostPayable.get(rowdata).getTRAN_DATE());
							row.createCell(6)
									.setCellValue(unreconcilTranPostPayable.get(rowdata).getTRANSACTION_PARTICULAR());
						}
					}
				}

				unreconcilTranPostPayable = null;
			}
			try {

				strm = new FileOutputStream(new File(fileName));
				book.write(strm);
				book.close();
				strm.flush();
				strm.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				System.gc();
				try {
					strm.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// =================METHODS FOR NFS RECEIVABLE GL=======================
	@Override
	public void processNfsReportReceivable(String date, String openingBal, String onlineFundMov, String finacleEODbal) {
		nFSPayableGLDao.processNfsReportReceivable(date, openingBal, onlineFundMov, finacleEODbal);
	}

	@Override
	public String downloadNfsReportReceivable(String date) throws Exception {
		String fileName = "NFS_RECEIVABLE_GL_SUMMARY_" + date + ".xlsx;";

		new NFSRECEIVABLEXlsx(nFSPayableGLDao, date, fileName).GenrateBook();
		return fileName;
	}

	private class NFSRECEIVABLEXlsx {

		private NFSSystemGLDao nFSPayableGLDao;
		private String date;
		private String fileName;

		NFSRECEIVABLEXlsx(NFSSystemGLDao nFSSystemGLDao, String fdate, String filename) {
			this.nFSPayableGLDao = nFSSystemGLDao;
			this.date = fdate;
			this.fileName = filename.replaceAll("/", "-");
		}

		void GenrateBook() throws Exception {

			OutputStream strm = null;
			SXSSFWorkbook book = new SXSSFWorkbook(1000);
			{
				List<NFSGLSummaryReceivable> nfsglSummaryReceiv = nFSPayableGLDao.downloadNfsReportReceivable(date);
				SXSSFSheet summSheet = book.createSheet("NFS RECEIVABLE GL SUMMARY");
				SXSSFRow summSheetheadrow = summSheet.createRow(0);

				summSheetheadrow.createCell(0).setCellValue("GL_DATE");
				summSheetheadrow.createCell(1).setCellValue("PARTICULARS");
				summSheetheadrow.createCell(2).setCellValue("DEBIT_AMT");
				summSheetheadrow.createCell(3).setCellValue("CREDIT_AMT");

				if (nfsglSummaryReceiv != null) {
					if (!nfsglSummaryReceiv.isEmpty()) {
						for (int rowdata = 0; rowdata < nfsglSummaryReceiv.size(); rowdata++) {
							SXSSFRow row = summSheet.createRow(rowdata + 1);
							row.createCell(0)
									.setCellValue(nfsglSummaryReceiv.get(rowdata).getGlDate().substring(0, 10));
							row.createCell(1).setCellValue(nfsglSummaryReceiv.get(rowdata).getParticulars());
							row.createCell(2).setCellValue(nfsglSummaryReceiv.get(rowdata).getDebitAmount());
							row.createCell(3).setCellValue(nfsglSummaryReceiv.get(rowdata).getCreditAmount());
						}
					}
				}

				nfsglSummaryReceiv = null;
			}
			// ****************************WITHDRAWAL SETTLEMENT AMOUNT FOR RECEIVABLE********************

			{
				List<NFSReceivableGLModel> withdrawalSetAmtReceiv = nFSPayableGLDao.withdrawalStlmntAmtReceivable(date);
				SXSSFSheet summSheet = book.createSheet("WITHDRAWAL SETTLEMENT AMT SUMMARY");
				SXSSFRow summSheetheadrow = summSheet.createRow(0);

				summSheetheadrow.createCell(0).setCellValue("CYCLE");
				summSheetheadrow.createCell(1).setCellValue("FILEDATE");
				summSheetheadrow.createCell(2).setCellValue("CREATDED_BY");
				summSheetheadrow.createCell(3).setCellValue("NO_OF_TXNS");
				summSheetheadrow.createCell(4).setCellValue("DEBIT");
				summSheetheadrow.createCell(5).setCellValue("CREDIT");
				summSheetheadrow.createCell(6).setCellValue("DESCRIPTION");

				if (withdrawalSetAmtReceiv != null) {
					if (!withdrawalSetAmtReceiv.isEmpty()) {
						for (int rowdata = 0; rowdata < withdrawalSetAmtReceiv.size(); rowdata++) {
							SXSSFRow row = summSheet.createRow(rowdata + 1);
							row.createCell(0).setCellValue(withdrawalSetAmtReceiv.get(rowdata).getCYCLE());
							row.createCell(1).setCellValue(withdrawalSetAmtReceiv.get(rowdata).getFiledate());
							row.createCell(2).setCellValue(withdrawalSetAmtReceiv.get(rowdata).getCREATEDBY());
							row.createCell(3).setCellValue(withdrawalSetAmtReceiv.get(rowdata).getNO_OF_TXNS());
							row.createCell(4).setCellValue(withdrawalSetAmtReceiv.get(rowdata).getDEBIT());
							row.createCell(5).setCellValue(withdrawalSetAmtReceiv.get(rowdata).getCREDIT());
							row.createCell(6).setCellValue(withdrawalSetAmtReceiv.get(rowdata).getDESCRIPTION());
						}
					}
				}

				withdrawalSetAmtReceiv = null;
			}
			// ********************************LATE REVERSAL SETTLEMENT AMOUNT FOR RECEIVABLE******************
			{
				List<NFSReceivableGLModel> lateReversalSetAmtReceiv = nFSPayableGLDao
						.lateReversalStlmntAmtReceivable(date);
				SXSSFSheet summSheet = book.createSheet("LATE REVERSAL SETTL AMT");
				SXSSFRow summSheetheadrow = summSheet.createRow(0);

				summSheetheadrow.createCell(0).setCellValue("TRANSTYPE");
				summSheetheadrow.createCell(1).setCellValue("RESP_CODE");
				summSheetheadrow.createCell(2).setCellValue("CARD_NO");
				summSheetheadrow.createCell(3).setCellValue("RRN");
				summSheetheadrow.createCell(4).setCellValue("STAN_NO");
				summSheetheadrow.createCell(5).setCellValue("ACQ");
				summSheetheadrow.createCell(6).setCellValue("ISS");
				summSheetheadrow.createCell(7).setCellValue("TRANS_DATE");
				summSheetheadrow.createCell(8).setCellValue("TRANS_TIME");
				summSheetheadrow.createCell(9).setCellValue("ATM_ID");
				summSheetheadrow.createCell(10).setCellValue("SETTLE_DATE");
				summSheetheadrow.createCell(11).setCellValue("REQUEST_AMT");
				summSheetheadrow.createCell(12).setCellValue("RECEIVED_AMT");
				summSheetheadrow.createCell(13).setCellValue("STATUS");
				summSheetheadrow.createCell(14).setCellValue("DCRS_REMARKS");
				summSheetheadrow.createCell(15).setCellValue("FILE_DATE");
				summSheetheadrow.createCell(16).setCellValue("CYCLE");
				summSheetheadrow.createCell(17).setCellValue("FPAN");

				if (lateReversalSetAmtReceiv != null) {
					if (!lateReversalSetAmtReceiv.isEmpty()) {
						for (int rowdata = 0; rowdata < lateReversalSetAmtReceiv.size(); rowdata++) {
							SXSSFRow row = summSheet.createRow(rowdata + 1);
							row.createCell(0).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getTranstype());
							row.createCell(1).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getResp_code());
							row.createCell(2).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getCardno());
							row.createCell(3).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getRRN());
							row.createCell(4).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getStanno());
							row.createCell(0).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getACQ());
							row.createCell(1).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getISS());
							row.createCell(2).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getTRAN_DATE());
							row.createCell(3).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getTrans_time());
							row.createCell(4).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getAtmid());
							row.createCell(0).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getSettledate());
							row.createCell(1).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getRequestamt());
							row.createCell(2).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getReceivedamt());
							row.createCell(3).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getSTATUS());
							row.createCell(4).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getDcrs_remarks());
							row.createCell(0)
									.setCellValue(lateReversalSetAmtReceiv.get(rowdata).getFiledate().substring(0, 10));
							row.createCell(1).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getCYCLE());
							row.createCell(2).setCellValue(lateReversalSetAmtReceiv.get(rowdata).getFPAN());

						}
					}
				}

				lateReversalSetAmtReceiv = null;
			}
			// *****************************JCB WITHDRAWAL SETTLEMENT AMOUNT FOR RECEIVABLE**********
			{
				List<NFSReceivableGLModel> jcbWithdraStlmntAmtReceiv = nFSPayableGLDao
						.jcbWithdrawalStlmntAmtReceivable(date);
				SXSSFSheet summSheet = book.createSheet("JCB SETTLEMENT AMT");
				SXSSFRow summSheetheadrow = summSheet.createRow(0);

				summSheetheadrow.createCell(0).setCellValue("SR_NO");
				summSheetheadrow.createCell(1).setCellValue("FILE_DATE");
				summSheetheadrow.createCell(2).setCellValue("NO_OF_TXNS");
				summSheetheadrow.createCell(3).setCellValue("DEBIT");
				summSheetheadrow.createCell(4).setCellValue("CREDIT");
				summSheetheadrow.createCell(5).setCellValue("DESCRIPTION");
				summSheetheadrow.createCell(6).setCellValue("ACCOUNT_NUMBER");
				summSheetheadrow.createCell(7).setCellValue("TTUM_NARRATION");

				if (jcbWithdraStlmntAmtReceiv != null) {
					if (!jcbWithdraStlmntAmtReceiv.isEmpty()) {
						for (int rowdata = 0; rowdata < jcbWithdraStlmntAmtReceiv.size(); rowdata++) {
							SXSSFRow row = summSheet.createRow(rowdata + 1);

							row.createCell(0).setCellValue(jcbWithdraStlmntAmtReceiv.get(rowdata).getSr_no());
							row.createCell(1).setCellValue(
									jcbWithdraStlmntAmtReceiv.get(rowdata).getFiledate().substring(0, 10));
							row.createCell(2).setCellValue(jcbWithdraStlmntAmtReceiv.get(rowdata).getNO_OF_TXNS());
							row.createCell(3).setCellValue(jcbWithdraStlmntAmtReceiv.get(rowdata).getDEBIT());
							row.createCell(4).setCellValue(jcbWithdraStlmntAmtReceiv.get(rowdata).getCREDIT());
							row.createCell(5).setCellValue(jcbWithdraStlmntAmtReceiv.get(rowdata).getDESCRIPTION());
							row.createCell(6).setCellValue(jcbWithdraStlmntAmtReceiv.get(rowdata).getACCOUNT_NUMBER());
							row.createCell(7).setCellValue(jcbWithdraStlmntAmtReceiv.get(rowdata).getTtum_naration());

						}
					}
				}

				jcbWithdraStlmntAmtReceiv = null;
			}
			// ****************************DFS WITHDRAWAL SETTLEMENT AMOUNT FOR RECEIVABLE**************************
			{
				List<NFSReceivableGLModel> dfsWithdrawalStlmntAmtRecei = nFSPayableGLDao
						.dfsWithdrawalStlmntAmtReceivable(date);
				SXSSFSheet summSheet = book.createSheet("DFS WITHDRAWAL SETTLEMENT");
				SXSSFRow summSheetheadrow = summSheet.createRow(0);

				summSheetheadrow.createCell(0).setCellValue("ACCOUNT_NUMBER");
				summSheetheadrow.createCell(1).setCellValue("CURRENCY_CODE");
				summSheetheadrow.createCell(2).setCellValue("PART_TRAN_TYPE");
				summSheetheadrow.createCell(3).setCellValue("TRANSACTION AMOUNT");
				summSheetheadrow.createCell(4).setCellValue("REFERENCE AMOUNT");
				summSheetheadrow.createCell(5).setCellValue("TRANS_DATE");
				summSheetheadrow.createCell(6).setCellValue("TRANSACTION_PARTICULAR");

				if (dfsWithdrawalStlmntAmtRecei != null) {
					if (!dfsWithdrawalStlmntAmtRecei.isEmpty()) {
						for (int rowdata = 0; rowdata < dfsWithdrawalStlmntAmtRecei.size(); rowdata++) {
							SXSSFRow row = summSheet.createRow(rowdata + 1);

							row.createCell(0)
									.setCellValue(dfsWithdrawalStlmntAmtRecei.get(rowdata).getACCOUNT_NUMBER());
							row.createCell(1).setCellValue(dfsWithdrawalStlmntAmtRecei.get(rowdata).getCURRENCY_CODE());
							row.createCell(2)
									.setCellValue(dfsWithdrawalStlmntAmtRecei.get(rowdata).getPART_TRAN_TYPE());
							row.createCell(3)
									.setCellValue(dfsWithdrawalStlmntAmtRecei.get(rowdata).getTRANSACTION_AMOUNT());
							row.createCell(4)
									.setCellValue(dfsWithdrawalStlmntAmtRecei.get(rowdata).getREFERENCE_AMOUNT());
							row.createCell(5).setCellValue(dfsWithdrawalStlmntAmtRecei.get(rowdata).getTRAN_DATE());
							row.createCell(6)
									.setCellValue(dfsWithdrawalStlmntAmtRecei.get(rowdata).getTRANSACTION_PARTICULAR());
						}
					}
				}

				dfsWithdrawalStlmntAmtRecei = null;
			}
			// *******************************NOT IN HOST TTUM FOR RECEIVABLE*****************************

			{
				List<NFSReceivableGLModel> notInHostTtumReceiv = nFSPayableGLDao.notInHostTtumReceivable(date);
				SXSSFSheet summSheet = book.createSheet("NOT IN HOST TTUM");
				SXSSFRow summSheetheadrow = summSheet.createRow(0);

				summSheetheadrow.createCell(0).setCellValue("MSGTYPE");
				summSheetheadrow.createCell(1).setCellValue("PAN");
				summSheetheadrow.createCell(2).setCellValue("TERMID");
				summSheetheadrow.createCell(3).setCellValue("LOCAL_DATE");
				summSheetheadrow.createCell(4).setCellValue("LOCAL_TIME");
				summSheetheadrow.createCell(5).setCellValue("TRACE");
				summSheetheadrow.createCell(6).setCellValue("AMOUNT");
				summSheetheadrow.createCell(7).setCellValue("ACCEPTOR_NAME");
				summSheetheadrow.createCell(8).setCellValue("RESP_CODE");
				summSheetheadrow.createCell(9).setCellValue("TERM_LOC");
				summSheetheadrow.createCell(10).setCellValue("AMOUNT_EQUIV");
				summSheetheadrow.createCell(11).setCellValue("ISSUER");
				summSheetheadrow.createCell(12).setCellValue("FILE_DATE");
				summSheetheadrow.createCell(13).setCellValue("DCRS_REMARKS");
				summSheetheadrow.createCell(14).setCellValue("FPAN");

				if (notInHostTtumReceiv != null) {
					if (!notInHostTtumReceiv.isEmpty()) {
						for (int rowdata = 0; rowdata < notInHostTtumReceiv.size(); rowdata++) {
							SXSSFRow row = summSheet.createRow(rowdata + 1);

							row.createCell(0).setCellValue(notInHostTtumReceiv.get(rowdata).getMSGTYPE());
							row.createCell(1).setCellValue(notInHostTtumReceiv.get(rowdata).getPAN());
							row.createCell(2).setCellValue(notInHostTtumReceiv.get(rowdata).getTERMID());
							row.createCell(3).setCellValue(notInHostTtumReceiv.get(rowdata).getLOCAL_DATE());
							row.createCell(4).setCellValue(notInHostTtumReceiv.get(rowdata).getLOCAL_TIME());
							row.createCell(5).setCellValue(notInHostTtumReceiv.get(rowdata).getTRACE());
							row.createCell(6).setCellValue(notInHostTtumReceiv.get(rowdata).getAMOUNT());
							row.createCell(7).setCellValue(notInHostTtumReceiv.get(rowdata).getACCEPTORNAME());
							row.createCell(8).setCellValue(notInHostTtumReceiv.get(rowdata).getResp_code());
							row.createCell(9).setCellValue(notInHostTtumReceiv.get(rowdata).getTERMLOC());
							row.createCell(10).setCellValue(notInHostTtumReceiv.get(rowdata).getAMOUNT_EQUIV());
							row.createCell(11).setCellValue(notInHostTtumReceiv.get(rowdata).getISSUER());
							row.createCell(12).setCellValue(notInHostTtumReceiv.get(rowdata).getFiledate());
							row.createCell(13).setCellValue(notInHostTtumReceiv.get(rowdata).getDcrs_remarks());
							row.createCell(14).setCellValue(notInHostTtumReceiv.get(rowdata).getFPAN());

						}
					}
				}

				notInHostTtumReceiv = null;
			}

			try {

				strm = new FileOutputStream(new File(fileName));
				book.write(strm);
				book.close();
				strm.flush();
				strm.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				System.gc();
				try {
					strm.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}