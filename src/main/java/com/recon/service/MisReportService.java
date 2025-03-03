package com.recon.service;

import javax.servlet.http.HttpServletResponse;

public interface MisReportService {

	String rupayProcessMisReport(String fdate2);

	String downloadRupayMisReport(String fdate,String tableName);

	String rupayProcessDomMisReport(String fdate2);

	String nfsProcessPayMisReport(String fdate2);

	String nfsProcessRecMisReport(String fdate2);

	String visaProcessMisReport(String fdate2);

	void RupayDomGlReportProcess(String fdate, String openingBalance, String finalEodBalance);
	
	public String downloadGlReport(String fdate,String tableName,String accountNumber)throws Exception;

	String generateICCWReport(HttpServletResponse response, String bhimModul);

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
