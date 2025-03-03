package com.recon.model;

public class RupayIntMisReportModel {
	private String REPORT_DATE    ;
	private String PARTICULARS    ;
	private String TXN_TYP        ;
	private String RESPONSE_CODE  ;
	private String COUNT          ;
	private String AMOUNT         ;
	public String getREPORT_DATE() {
		return REPORT_DATE;
	}
	public void setREPORT_DATE(String rEPORT_DATE) {
		REPORT_DATE = rEPORT_DATE;
	}
	public String getPARTICULARS() {
		return PARTICULARS;
	}
	public void setPARTICULARS(String pARTICULARS) {
		PARTICULARS = pARTICULARS;
	}
	public String getTXN_TYP() {
		return TXN_TYP;
	}
	public void setTXN_TYP(String tXN_TYP) {
		TXN_TYP = tXN_TYP;
	}
	public String getRESPONSE_CODE() {
		return RESPONSE_CODE;
	}
	public void setRESPONSE_CODE(String rESPONSE_CODE) {
		RESPONSE_CODE = rESPONSE_CODE;
	}
	public String getCOUNT() {
		return COUNT;
	}
	public void setCOUNT(String cOUNT) {
		COUNT = cOUNT;
	}
	public String getAMOUNT() {
		return AMOUNT;
	}
	public void setAMOUNT(String aMOUNT) {
		AMOUNT = aMOUNT;
	}
}
