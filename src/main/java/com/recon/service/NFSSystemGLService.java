package com.recon.service;
public interface NFSSystemGLService{

	void processNfsReportPayable(String fdate, String openingBal, String fundMovefr43Acc, String onlineFundMov,
			String finacleEODbal);

	String downloadNfsReportPayable(String fdate) throws Exception;

	void processNfsReportReceivable(String fdate, String openingBal, String onlineFundMov, String finacleEODbal);

	String downloadNfsReportReceivable(String date) throws Exception;
	
}