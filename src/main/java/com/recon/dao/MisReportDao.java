package com.recon.dao;

import java.util.List;

import com.recon.model.DownloadICCW;
import com.recon.model.DownloadICCW1;
import com.recon.model.DownloadICCW2;
import com.recon.model.GlReportModel;
import com.recon.model.GlSettlementModel;
import com.recon.model.RupayIntMisReportModel;

public interface MisReportDao {

	boolean rupayProcessMisReport(String fdate);

	List<RupayIntMisReportModel> downloadRupayMisReport(String date,String tableName);

	boolean rupayProcessDomMisReport(String fdate);

	boolean nfsProcessPayMisReport(String fdate);

	boolean nfsProcessRecMisReport(String fdate);

	boolean visaProcessMisReport(String fdate);

	void RupayDomGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	public List<GlReportModel> downloadGlReport(String date,String tableName);

	public List<GlSettlementModel> downloadGlSettlement(String date,String accountNumber, String queryNumber);

	List<DownloadICCW> iccwdownload();
	
	List<DownloadICCW1> iccwdownload1();
	
	List<DownloadICCW2> iccwdownload2();

	void NfsAcqChargebackGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	void NfsAcqPrearbGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	void NfsAcqDebitGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	void NfsAcqCreditGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	void NfsIssChargebackGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	void NfsISSCreditGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	void NfsISSDebitGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	void NfsISSPrearbGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	void VisaDomGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	void VisaIntGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	void RupayMirrorGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	void NFSMirrorGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	void RupayMirrorIRGCSGlReportProcess(String fdate, String openingBalance, String finalEodBalance);

	

}
