package com.recon.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recon.dao.MisReportDao;
import com.recon.model.DownloadICCW;
import com.recon.model.DownloadICCW1;
import com.recon.model.DownloadICCW2;
import com.recon.model.GlReportModel;
import com.recon.model.RupayIntMisReportModel;
import com.recon.service.MisReportService;

@Service
public class MisReportServiceImpl implements MisReportService {
	@Autowired
	MisReportDao misReportDao;

	@Override
	public String rupayProcessMisReport(String fdate) {
		if (misReportDao.rupayProcessMisReport(fdate)) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}

	@Override
	public String rupayProcessDomMisReport(String fdate) {
		if (misReportDao.rupayProcessDomMisReport(fdate)) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}

	@Override
	public String nfsProcessPayMisReport(String fdate) {
		if (misReportDao.nfsProcessPayMisReport(fdate)) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}

	@Override
	public String nfsProcessRecMisReport(String fdate) {
		if (misReportDao.nfsProcessRecMisReport(fdate)) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}

	@Override
	public String visaProcessMisReport(String fdate) {
		if (misReportDao.visaProcessMisReport(fdate)) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
	
	


	@Override
	public String downloadRupayMisReport(String fdate, String tableName) {
		String sheetName = "";
		if (tableName.contains("RUP_INT_DAILY_MIS")) {
			com.recon.util.FILEPATH.INWARD = "RUPAY_INT_DAILY_MIS_REPORT.xlsx";
			sheetName = "rupay int daily mis report";
		} else if (tableName.contains("RUP_DOM_DAILY_MIS")) {
			com.recon.util.FILEPATH.INWARD = "RUPAY_DOM_DAILY_MIS_REPORT.xlsx";
			sheetName = "rupay dom daily mis report";
		} else if (tableName.contains("NFS_PAY_DAILY_MIS")) {
			com.recon.util.FILEPATH.INWARD = "NFS_PAY_DAILY_MIS_REPORT.xlsx";
			sheetName = "nfs pay daily mis report";
		} else if (tableName.contains("NFS_REC_DAILY_MIS")) {
			com.recon.util.FILEPATH.INWARD = "NFS_REC_DAILY_MIS_REPORT.xlsx";
			sheetName = "nfs rec daily mis report";
		} else if (tableName.contains("VISA_DAILY_MIS")) {
			com.recon.util.FILEPATH.INWARD = "VISA_DAILY_MIS_REPORT.xlsx";
			sheetName = "visa daily mis report";
		}

		// "IMPS_DAILY_MIS_REPORT_" + date.replace("-","") + ".xlsx"
		try {
			new MisToXlsx(misReportDao, fdate, com.recon.util.FILEPATH.INWARD, tableName, sheetName).GenerateBook();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return com.recon.util.FILEPATH.INWARD;
	}

	private class MisToXlsx {

		private MisReportDao dao;
		private String date;
		private String fileName;
		private String tableName;
		private String sheetName;

		MisToXlsx(MisReportDao dao, String fdate, String filename, String tableName, String sheetName) {
			this.dao = dao;
			this.date = fdate;
			this.fileName = filename;
			this.tableName = tableName;
		}

		void GenerateBook() throws Exception {

			OutputStream strm = new FileOutputStream(new File(fileName));
			SXSSFWorkbook book = new SXSSFWorkbook(1000);

			List<RupayIntMisReportModel> misList = dao.downloadRupayMisReport(date, tableName);
//			for (RupayIntMisReportModel rupayIntMisReportModel : misList) {
//				System.out.println(rupayIntMisReportModel.getREPORT_DATE());
//				System.out.println(rupayIntMisReportModel.getPARTICULARS());
//				System.out.println(rupayIntMisReportModel.getTXN_TYP());
//				System.out.println(rupayIntMisReportModel.getRESPONSE_CODE());
//				System.out.println(rupayIntMisReportModel.getCOUNT());
//				System.out.println(rupayIntMisReportModel.getAMOUNT());
//			}
			SXSSFSheet summSheet = book.createSheet("Mis Report");
			SXSSFRow summSheetheadrow = summSheet.createRow(0);

			summSheetheadrow.createCell(0).setCellValue("REPORT_DATE");
			summSheetheadrow.createCell(1).setCellValue("PARTICULARS");
			summSheetheadrow.createCell(2).setCellValue("TXN_TYPE");
			summSheetheadrow.createCell(3).setCellValue("RESPONSE_CODE");
			summSheetheadrow.createCell(4).setCellValue("COUNT");
			summSheetheadrow.createCell(5).setCellValue("AMOUNT");

			if (misList != null) {
				if (!misList.isEmpty()) {
					for (int rowdata = 0; rowdata < misList.size(); rowdata++) {
						SXSSFRow row = summSheet.createRow(rowdata + 1);
						row.createCell(0).setCellValue(misList.get(rowdata).getREPORT_DATE().substring(0, 10));
//						row.createCell(0).setCellValue(misList.get(rowdata).getREPORT_DATE());
						row.createCell(1).setCellValue(misList.get(rowdata).getPARTICULARS());
						row.createCell(2).setCellValue(misList.get(rowdata).getTXN_TYP());
						row.createCell(3).setCellValue(misList.get(rowdata).getRESPONSE_CODE());
						row.createCell(4).setCellValue(misList.get(rowdata).getCOUNT());
//						row.createCell(4).setCellValue(misList.get(rowdata).getCOUNT()==null?"NA":misList.get(rowdata).getCOUNT());						
						row.createCell(5).setCellValue(misList.get(rowdata).getAMOUNT());
					}
				}
			}

			misList = null;

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

	@Override
	public void RupayDomGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		misReportDao.RupayDomGlReportProcess(fdate, openingBalance, finalEodBalance);
	}
	
	@Override
	public void VisaDomGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		misReportDao.VisaDomGlReportProcess(fdate, openingBalance, finalEodBalance);
		
	}
	
	@Override
	public void VisaIntGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		misReportDao.VisaIntGlReportProcess(fdate, openingBalance, finalEodBalance);
		
	}
	
	@Override
	public void RupayMirrorGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		misReportDao.RupayMirrorGlReportProcess(fdate, openingBalance, finalEodBalance);
		
	}
	
	@Override
	public void NFSMirrorGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		misReportDao.NFSMirrorGlReportProcess(fdate, openingBalance, finalEodBalance);
		
	}
	
	@Override
	public void RupayMirrorIRGCSGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		misReportDao.RupayMirrorIRGCSGlReportProcess(fdate, openingBalance, finalEodBalance);
		
	}
	
	@Override
	public void NfsAcqChargebackGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {	
		misReportDao.NfsAcqChargebackGlReportProcess(fdate, openingBalance, finalEodBalance) ;
		}
		
	@Override
	public void NfsAcqPrearbGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		misReportDao.NfsAcqPrearbGlReportProcess(fdate, openingBalance, finalEodBalance) ;
		
	}
	
	@Override
	public void NfsAcqDebitGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		misReportDao.NfsAcqDebitGlReportProcess(fdate, openingBalance, finalEodBalance) ;
		
	}
	@Override
	public void NfsAcqCreditGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		misReportDao.NfsAcqCreditGlReportProcess(fdate, openingBalance, finalEodBalance) ;
		
	}

	@Override
	public void NfsIssChargebackGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		misReportDao.NfsIssChargebackGlReportProcess(fdate, openingBalance, finalEodBalance) ;
		
	}
	
	@Override
	public void NfsISSCreditGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		misReportDao.NfsISSCreditGlReportProcess(fdate, openingBalance, finalEodBalance) ;
		
	}
	
	@Override
	public void NfsISSDebitGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		misReportDao.NfsISSDebitGlReportProcess(fdate, openingBalance, finalEodBalance) ;
		
	}
	
	@Override
	public void NfsISSPrearbGlReportProcess(String fdate, String openingBalance, String finalEodBalance) {
		misReportDao.NfsISSPrearbGlReportProcess(fdate, openingBalance, finalEodBalance) ;
		
	}

	@Override
	public String downloadGlReport(String date, String tableName, String accountNumber) throws Exception {
		System.out.println("tableName: " + tableName);
		if ("GL_ACQ_CHARGEBACK".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "NFS_ACQ_CHARGEBACK.xlsx";
		} else if ("GL_ACQ_PREARB".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "NFS_ACQ_PREARB.xlsx";
		} else if ("GL_ACQ_DR_ADJ".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "NFS_ACQ_DEBIT.xlsx";
		} else if ("GL_ACQ_CRD_ADJ".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "NFS_ACQ_CREDIT.xlsx";
		} else if ("GL_ISS_CHARGEBACK".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "NFS_ISS_CHARGEBACK.xlsx";
		}else if ("GL_ISS_CRD_ADJ".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "NFS_ISS_CREDIT.xlsx";
		}else if ("GL_ISS_DR_ADJ".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "NFS_ISS_DEBIT.xlsx";
		}else if ("GL_ISS_PREARB".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "NFS_ISS_PREARB.xlsx";
		}else if ("GL_DOMESTIC_RUPAY".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "GL_DOMESTIC_RUPAY.xlsx";
		}else if ("GL_VISA_DOMESTIC_PAYABLE".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "GL_VISA_DOMESTIC_PAYABLE.xlsx";
		}else if ("GL_VISA_INTERNATIONAL_PAYABLE".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "GL_VISA_INTERNATIONAL_PAYABLE.xlsx";
		}else if ("GL_MIRROR_RUPAY".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "GL_MIRROR_RUPAY.xlsx";
		}else if ("GL_NFS_MIRROR".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "GL_NFS_MIRROR.xlsx";
		}else if ("GL_IRGCS_MIRROR_RUPAY".equals(tableName)) {
			com.recon.util.FILEPATH.INWARD = "GL_IRGCS_MIRROR_RUPAY.xlsx";
		}
		// "IMPS_DAILY_MIS_REPORT_" + date.replace("-","") + ".xlsx"
		new GLToXlsx(misReportDao, date, com.recon.util.FILEPATH.INWARD, tableName, accountNumber).GenerateBook();
		return com.recon.util.FILEPATH.INWARD;
	}

	private class GLToXlsx {

		private MisReportDao dao;
		private String date;
		private String fileName;
		private String tableName;
		private String accountNumber;

		GLToXlsx(MisReportDao dao, String fdate, String filename, String tableName, String accountNumber) {
			this.dao = dao;
			this.date = fdate;
			this.fileName = filename;
			this.tableName = tableName;
			this.accountNumber = accountNumber;
		}

		void GenerateBook() throws Exception {

			OutputStream strm = new FileOutputStream(new File(fileName));
			SXSSFWorkbook book = new SXSSFWorkbook(1000);

			List<GlReportModel> GlList = dao.downloadGlReport(date, tableName);
			SXSSFSheet summSheet = book.createSheet("Summary");
			SXSSFRow summSheetheadrow = summSheet.createRow(0);

			summSheetheadrow.createCell(0).setCellValue("GL_DATE");
			summSheetheadrow.createCell(1).setCellValue("PARTICULARS");
			summSheetheadrow.createCell(2).setCellValue("DEBIT_AMT");
			summSheetheadrow.createCell(3).setCellValue("CREDIT_AMT");

			if (GlList != null) {
				if (!GlList.isEmpty()) {
					for (int rowdata = 0; rowdata < GlList.size(); rowdata++) {
						SXSSFRow row = summSheet.createRow(rowdata + 1);
						row.createCell(0).setCellValue(GlList.get(rowdata).getGL_DATE().substring(0, 10));
						row.createCell(1).setCellValue(GlList.get(rowdata).getPARTICULARS());
						row.createCell(2).setCellValue(GlList.get(rowdata).getDEBIT_AMT());
						row.createCell(3).setCellValue(GlList.get(rowdata).getCREDIT_AMT());
					}
				}
			}

			GlList = null;
//			String QueryNumber = "1";
//			List<GlSettlementModel> GlListSettlement = dao.downloadGlSettlement(date, accountNumber,QueryNumber);
//			SXSSFSheet summSheet2 = book.createSheet("Settlement");
//			SXSSFRow summSheetheadrow2 = summSheet2.createRow(0);
//			summSheetheadrow2.createCell(0).setCellValue("FILEDATE");
//			summSheetheadrow2.createCell(1).setCellValue("DEBIT");
//			summSheetheadrow2.createCell(2).setCellValue("CREDIT");
//			summSheetheadrow2.createCell(3).setCellValue("DESCRIPTION");
//			summSheetheadrow2.createCell(4).setCellValue("TTUM_NARATION");
//			summSheetheadrow2.createCell(5).setCellValue("ACCOUNT_NO");
//			
//
//			if (GlListSettlement != null) {
//				if (!GlListSettlement.isEmpty()) {
//					for (int rowdata = 0; rowdata < GlListSettlement.size(); rowdata++) {
//						SXSSFRow row = summSheet2.createRow(rowdata + 1);
//						row.createCell(0).setCellValue(GlListSettlement.get(rowdata).getFILEDATE());
//						row.createCell(1).setCellValue(GlListSettlement.get(rowdata).getDEBIT());
//						row.createCell(2).setCellValue(GlListSettlement.get(rowdata).getCREDIT());
//						row.createCell(3).setCellValue(GlListSettlement.get(rowdata).getDESCRIPTION());
//						row.createCell(4).setCellValue(GlListSettlement.get(rowdata).getTTUM_NARATION());
//						row.createCell(5).setCellValue(GlListSettlement.get(rowdata).getACCOUNT_NO());
//					}
//				}
//			}
//			
//			QueryNumber = "2";
//			List<GlSettlementModel> GlListSettlement2 = dao.downloadGlSettlement(date, accountNumber,QueryNumber);
//			System.out.println("GlListSettlement2: "+GlListSettlement2.size());
//			int count = GlListSettlement.size() + 1;
//			if (GlListSettlement2 != null) {
//				if (!GlListSettlement2.isEmpty()) {
//					for (int rowdata =0; rowdata < GlListSettlement2.size(); rowdata++) {
//						SXSSFRow row = summSheet2.createRow(count);
//						row.createCell(0).setCellValue(GlListSettlement2.get(rowdata).getFILEDATE());
//						row.createCell(1).setCellValue(GlListSettlement2.get(rowdata).getDEBIT());
//						row.createCell(2).setCellValue(GlListSettlement2.get(rowdata).getCREDIT());
//						row.createCell(3).setCellValue(GlListSettlement2.get(rowdata).getDESCRIPTION());
//						row.createCell(4).setCellValue(GlListSettlement2.get(rowdata).getTTUM_NARATION());
//						row.createCell(5).setCellValue(GlListSettlement2.get(rowdata).getACCOUNT_NO());
//						count++;
//						
//					}
//				}
//			}
//			
//			GlListSettlement = null;

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
/*--------------------------------------------------------------------------------------------------------*/
	@Override
	public String generateICCWReport(HttpServletResponse response, String bhimModul) {
		String date = (String) java.time.LocalDate.now().toString();
		String fileName = "ICCW_REPORTS" + date + ".xlsx;";
		try {
			new ICCWReportToXlsx(misReportDao, date, fileName).GenrateBook();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileName;
	}

	private class ICCWReportToXlsx {

		private MisReportDao dao;
		private String date;
		private String fileName;

		ICCWReportToXlsx(MisReportDao dao, String fdate, String filename) {
			this.dao = dao;
			this.date = fdate;
			this.fileName = filename.replaceAll("/", "-");
		}

		void GenrateBook() throws Exception {

			OutputStream strm = null;
			SXSSFWorkbook book = new SXSSFWorkbook(1000);

			List<DownloadICCW> downloadICCW = dao.iccwdownload();
			SXSSFSheet summSheet = book.createSheet("ICCW_MASTER_SWITCH_NPCI");
			SXSSFRow summSheetheadrow = summSheet.createRow(0);

			summSheetheadrow.createCell(0).setCellValue("UPI_TRAN_TYP");
			summSheetheadrow.createCell(1).setCellValue("UPI_SWITCHTOCBSPSTDT");
			summSheetheadrow.createCell(2).setCellValue("UPI_SWITCHTOCBSPSTTM");
			summSheetheadrow.createCell(3).setCellValue("UPI_SENDER_ACQ_ID");
			summSheetheadrow.createCell(4).setCellValue("UPI_RECEIVER_ACQ_ID");
			summSheetheadrow.createCell(5).setCellValue("UPI_CBS_RRN");
			summSheetheadrow.createCell(6).setCellValue("UPI_UPI_RRN");
			summSheetheadrow.createCell(7).setCellValue("UPI_TXN_ID");
			summSheetheadrow.createCell(8).setCellValue("UPI_TXN_AMT");
			summSheetheadrow.createCell(9).setCellValue("UPI_TRAN_DATE");
			summSheetheadrow.createCell(10).setCellValue("UPI_TRAN_TIME");
			summSheetheadrow.createCell(11).setCellValue("UPI_CBS_RESP_CODE");
			summSheetheadrow.createCell(12).setCellValue("UPI_UPI_RESP_CODE");
			summSheetheadrow.createCell(13).setCellValue("UPI_ORIG_CURR_CODE");
			summSheetheadrow.createCell(14).setCellValue("UPI_UPI_MESSAGE_TYPE");
			summSheetheadrow.createCell(15).setCellValue("UPI_DEBIT_CREDIT_FLAG");
			summSheetheadrow.createCell(16).setCellValue("UPI_POST_ACCOUNT_NUM_1");
			summSheetheadrow.createCell(17).setCellValue("UPI_REMARKS");
			summSheetheadrow.createCell(18).setCellValue("UPI_CUST_REF_NUM");
			summSheetheadrow.createCell(19).setCellValue("UPI_POST_ACCOUNT_NUM_2");
			summSheetheadrow.createCell(20).setCellValue("IFSC_CD");
			summSheetheadrow.createCell(21).setCellValue("MCC_CD");
			summSheetheadrow.createCell(22).setCellValue("INITIATE_CD");
			summSheetheadrow.createCell(23).setCellValue("PURPOSE_CD");
			summSheetheadrow.createCell(24).setCellValue("FILEDATE");
			summSheetheadrow.createCell(25).setCellValue("CREATED_BY");
			summSheetheadrow.createCell(26).setCellValue("REMARK");
			if (downloadICCW != null) {
				if (!downloadICCW.isEmpty()) {
					for (int rowdata = 0; rowdata < downloadICCW.size(); rowdata++) {
						SXSSFRow row = summSheet.createRow(rowdata + 1);
						row.createCell(0).setCellValue(downloadICCW.get(rowdata).getUPI_TRAN_TYP());
						row.createCell(1).setCellValue(downloadICCW.get(rowdata).getUPI_SWITCHTOCBSPSTDT());
						row.createCell(2).setCellValue(downloadICCW.get(rowdata).getUPI_SWITCHTOCBSPSTTM());
						row.createCell(3).setCellValue(downloadICCW.get(rowdata).getUPI_SENDER_ACQ_ID());
						row.createCell(4).setCellValue(downloadICCW.get(rowdata).getUPI_RECEIVER_ACQ_ID());
						row.createCell(5).setCellValue(downloadICCW.get(rowdata).getUPI_CBS_RRN());
						row.createCell(6).setCellValue(downloadICCW.get(rowdata).getUPI_UPI_RRN());
						row.createCell(7).setCellValue(downloadICCW.get(rowdata).getUPI_TXN_ID());
						row.createCell(8).setCellValue(downloadICCW.get(rowdata).getUPI_TXN_AMT());
						row.createCell(9).setCellValue(downloadICCW.get(rowdata).getUPI_TRAN_DATE());
						row.createCell(10).setCellValue(downloadICCW.get(rowdata).getUPI_TRAN_TIME());
						row.createCell(11).setCellValue(downloadICCW.get(rowdata).getUPI_CBS_RESP_CODE());
						row.createCell(12).setCellValue(downloadICCW.get(rowdata).getUPI_UPI_RESP_CODE());
						row.createCell(13).setCellValue(downloadICCW.get(rowdata).getUPI_ORIG_CURR_CODE());
						row.createCell(14).setCellValue(downloadICCW.get(rowdata).getUPI_UPI_MESSAGE_TYPE());
						row.createCell(15).setCellValue(downloadICCW.get(rowdata).getUPI_DEBIT_CREDIT_FLAG());
						row.createCell(16).setCellValue(downloadICCW.get(rowdata).getUPI_POST_ACCOUNT_NUM_1());
						row.createCell(17).setCellValue(downloadICCW.get(rowdata).getUPI_REMARKS());
						row.createCell(18).setCellValue(downloadICCW.get(rowdata).getUPI_CUST_REF_NUM());
						row.createCell(19).setCellValue(downloadICCW.get(rowdata).getUPI_POST_ACCOUNT_NUM_2());
						row.createCell(20).setCellValue(downloadICCW.get(rowdata).getIFSC_CD());
						row.createCell(21).setCellValue(downloadICCW.get(rowdata).getMCC_CD());
						row.createCell(22).setCellValue(downloadICCW.get(rowdata).getINITIATE_CD());
						row.createCell(23).setCellValue(downloadICCW.get(rowdata).getPURPOSE_CD());
						row.createCell(24).setCellValue(downloadICCW.get(rowdata).getFILEDATE());
						row.createCell(25).setCellValue(downloadICCW.get(rowdata).getCREATED_BY());
						row.createCell(26).setCellValue(downloadICCW.get(rowdata).getREMARK());
					}
				}
			}

			downloadICCW = null;
			List<DownloadICCW1> downloadICCW1 = dao.iccwdownload1();
			SXSSFSheet summSheet1 = book.createSheet("ICCW_MASTER_CBS_NPCI");
			SXSSFRow summSheetheadrow1 = summSheet1.createRow(0);

			summSheetheadrow1.createCell(0).setCellValue("SOL_ID");
			summSheetheadrow1.createCell(1).setCellValue("AMOUNT");
			summSheetheadrow1.createCell(2).setCellValue("DRCR");
			summSheetheadrow1.createCell(3).setCellValue("DR_ACC_NUM");
			summSheetheadrow1.createCell(4).setCellValue("TRACE_NUM");
			summSheetheadrow1.createCell(5).setCellValue("CR_ACC_NUM");
			summSheetheadrow1.createCell(6).setCellValue("TXN_DATE");
			summSheetheadrow1.createCell(7).setCellValue("TXN_TIME");
			summSheetheadrow1.createCell(8).setCellValue("TYPE_1");
			summSheetheadrow1.createCell(9).setCellValue("TYPE_2");
			summSheetheadrow1.createCell(10).setCellValue("RRN");
			summSheetheadrow1.createCell(11).setCellValue("TXN_ID");
			summSheetheadrow1.createCell(12).setCellValue("VALUE_DT");
			summSheetheadrow1.createCell(13).setCellValue("CREATED_BY");
			summSheetheadrow1.createCell(14).setCellValue("FILEDATE");
			summSheetheadrow1.createCell(15).setCellValue("REMARK");
			summSheetheadrow1.createCell(16).setCellValue("UPI_TXN_ID");
			if (downloadICCW1 != null) {
				if (!downloadICCW1.isEmpty()) {
					for (int rowdata = 0; rowdata < downloadICCW1.size(); rowdata++) {
						SXSSFRow row1 = summSheet1.createRow(rowdata + 1);
						row1.createCell(0).setCellValue(downloadICCW1.get(rowdata).getSOL_ID());
						row1.createCell(1).setCellValue(downloadICCW1.get(rowdata).getAMOUNT());
						row1.createCell(2).setCellValue(downloadICCW1.get(rowdata).getDRCR());
						row1.createCell(3).setCellValue(downloadICCW1.get(rowdata).getDR_ACC_NUM());
						row1.createCell(4).setCellValue(downloadICCW1.get(rowdata).getTRACE_NUM());
						row1.createCell(5).setCellValue(downloadICCW1.get(rowdata).getCR_ACC_NUM());
						row1.createCell(6).setCellValue(downloadICCW1.get(rowdata).getTXN_DATE());
						row1.createCell(7).setCellValue(downloadICCW1.get(rowdata).getTXN_TIME());
						row1.createCell(8).setCellValue(downloadICCW1.get(rowdata).getTYPE_1());
						row1.createCell(9).setCellValue(downloadICCW1.get(rowdata).getTYPE_2());
						row1.createCell(10).setCellValue(downloadICCW1.get(rowdata).getRRN());
						row1.createCell(11).setCellValue(downloadICCW1.get(rowdata).getTXN_ID());
						row1.createCell(12).setCellValue(downloadICCW1.get(rowdata).getVALUE_DT());
						row1.createCell(13).setCellValue(downloadICCW1.get(rowdata).getCREATED_BY());
						row1.createCell(14).setCellValue(downloadICCW1.get(rowdata).getFILEDATE());
						row1.createCell(15).setCellValue(downloadICCW1.get(rowdata).getREMARK());
						row1.createCell(16).setCellValue(downloadICCW1.get(rowdata).getUPI_TXN_ID());
					}
				}
			}
			downloadICCW1 = null;
			List<DownloadICCW2> downloadICCW2 = dao.iccwdownload2();
			SXSSFSheet summSheet2 = book.createSheet("ICCW_MASTER_NPCI_SWITCH");
			SXSSFRow summSheetheadrow2 = summSheet2.createRow(0);
			
			summSheetheadrow2.createCell(0).setCellValue("PARTICIPANT_ID");
			summSheetheadrow2.createCell(1).setCellValue("TRAN_TYPE");
			summSheetheadrow2.createCell(2).setCellValue("UPI_TRAN_ID");
			summSheetheadrow2.createCell(3).setCellValue("RRN");
			summSheetheadrow2.createCell(4).setCellValue("RESP_CODE");
			summSheetheadrow2.createCell(5).setCellValue("TRAN_DATE");
			summSheetheadrow2.createCell(6).setCellValue("TRAN_TIME");
			summSheetheadrow2.createCell(7).setCellValue("AMOUNT");
			summSheetheadrow2.createCell(8).setCellValue("INITIATE_CODE");
			summSheetheadrow2.createCell(9).setCellValue("MAPPER_ID");
			summSheetheadrow2.createCell(10).setCellValue("BLANKX");
			summSheetheadrow2.createCell(11).setCellValue("PURPOSE_CODE");
			summSheetheadrow2.createCell(12).setCellValue("PAYER_PSP_CODE");
			summSheetheadrow2.createCell(13).setCellValue("PAYER_MCC");
			summSheetheadrow2.createCell(14).setCellValue("MERCHANT_CATEGORY_CODE");
			summSheetheadrow2.createCell(15).setCellValue("PAYEE_VPA");
			summSheetheadrow2.createCell(16).setCellValue("MCC_CODE");
			summSheetheadrow2.createCell(17).setCellValue("UMN");
			summSheetheadrow2.createCell(18).setCellValue("ACQ_BANK");
			summSheetheadrow2.createCell(19).setCellValue("ACQ_IFSC");
			summSheetheadrow2.createCell(20).setCellValue("PAYEE_PSP_CODE");
			summSheetheadrow2.createCell(21).setCellValue("ACQ_ACC_NUMBER");
			summSheetheadrow2.createCell(22).setCellValue("ISS_BANK");
			summSheetheadrow2.createCell(23).setCellValue("ISS_IFSC");
			summSheetheadrow2.createCell(24).setCellValue("PAYER_VPA");
			summSheetheadrow2.createCell(25).setCellValue("ISS_ACC_NUMBER");
			summSheetheadrow2.createCell(26).setCellValue("INITIATION_MODE");
			summSheetheadrow2.createCell(27).setCellValue("INITIATION_MODE_1");
			summSheetheadrow2.createCell(28).setCellValue("ATTRIBUTE_9");
			summSheetheadrow2.createCell(29).setCellValue("ATTRIBUTE_10");
			summSheetheadrow2.createCell(30).setCellValue("CREATED_BY");
			summSheetheadrow2.createCell(31).setCellValue("FILEDATE");
			summSheetheadrow2.createCell(32).setCellValue("CYCLE");
			summSheetheadrow2.createCell(33).setCellValue("REMARK");
			if (downloadICCW2 != null) {
				if (!downloadICCW2.isEmpty()) {
					for (int rowdata = 0; rowdata < downloadICCW2.size(); rowdata++) {
						SXSSFRow row2 = summSheet2.createRow(rowdata + 1);
						row2.createCell(0).setCellValue(downloadICCW2.get(rowdata).getPARTICIPANT_ID());
						row2.createCell(1).setCellValue(downloadICCW2.get(rowdata).getTRAN_TYPE());
						row2.createCell(2).setCellValue(downloadICCW2.get(rowdata).getUPI_TRAN_ID());
						row2.createCell(3).setCellValue(downloadICCW2.get(rowdata).getRRN());
						row2.createCell(4).setCellValue(downloadICCW2.get(rowdata).getRESP_CODE());
						row2.createCell(5).setCellValue(downloadICCW2.get(rowdata).getTRAN_DATE());
						row2.createCell(6).setCellValue(downloadICCW2.get(rowdata).getTRAN_TIME());
						row2.createCell(7).setCellValue(downloadICCW2.get(rowdata).getAMOUNT());
						row2.createCell(8).setCellValue(downloadICCW2.get(rowdata).getINITIATE_CODE());
						row2.createCell(9).setCellValue(downloadICCW2.get(rowdata).getMAPPER_ID());
						row2.createCell(10).setCellValue(downloadICCW2.get(rowdata).getBLANKX());
						row2.createCell(11).setCellValue(downloadICCW2.get(rowdata).getPURPOSE_CODE());
						row2.createCell(12).setCellValue(downloadICCW2.get(rowdata).getPAYER_PSP_CODE());
						row2.createCell(13).setCellValue(downloadICCW2.get(rowdata).getPAYER_MCC());
						row2.createCell(14).setCellValue(downloadICCW2.get(rowdata).getMERCHANT_CATEGORY_CODE());
						row2.createCell(15).setCellValue(downloadICCW2.get(rowdata).getPAYEE_VPA());
						row2.createCell(16).setCellValue(downloadICCW2.get(rowdata).getMCC_CODE());
						row2.createCell(17).setCellValue(downloadICCW2.get(rowdata).getUMN());
						row2.createCell(18).setCellValue(downloadICCW2.get(rowdata).getACQ_BANK());
						row2.createCell(19).setCellValue(downloadICCW2.get(rowdata).getACQ_IFSC());
						row2.createCell(20).setCellValue(downloadICCW2.get(rowdata).getPAYEE_PSP_CODE());
						row2.createCell(21).setCellValue(downloadICCW2.get(rowdata).getACQ_ACC_NUMBER());
						row2.createCell(22).setCellValue(downloadICCW2.get(rowdata).getISS_BANK());
						row2.createCell(23).setCellValue(downloadICCW2.get(rowdata).getISS_IFSC());
						row2.createCell(24).setCellValue(downloadICCW2.get(rowdata).getPAYER_VPA());
						row2.createCell(25).setCellValue(downloadICCW2.get(rowdata).getISS_ACC_NUMBER());
						row2.createCell(26).setCellValue(downloadICCW2.get(rowdata).getINITIATION_MODE());
						row2.createCell(27).setCellValue(downloadICCW2.get(rowdata).getINITIATION_MODE_1());
						row2.createCell(28).setCellValue(downloadICCW2.get(rowdata).getATTRIBUTE_9());
						row2.createCell(29).setCellValue(downloadICCW2.get(rowdata).getATTRIBUTE_10());
						row2.createCell(30).setCellValue(downloadICCW2.get(rowdata).getCREATED_BY());
						row2.createCell(31).setCellValue(downloadICCW2.get(rowdata).getFILEDATE());
						row2.createCell(32).setCellValue(downloadICCW2.get(rowdata).getCYCLE());
						row2.createCell(33).setCellValue(downloadICCW2.get(rowdata).getREMARK());
					}
				}
			}
			downloadICCW2 = null;
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
