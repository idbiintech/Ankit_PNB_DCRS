package com.recon.model;

public class GLRemitterReportBean {

	private String particulars;
	private String credit_Amt;
	private String debit_Amt;
	private String transaction_Date;
	private String balance;
	private int	sr_No;
	private String stSubCategory;
	private String crdr_Diff;
	
	
	
	public String getCrdr_Diff() {
		return crdr_Diff;
	}
	public void setCrdr_Diff(String crdr_Diff) {
		this.crdr_Diff = crdr_Diff;
	}
	public String getStSubCategory() {
		return stSubCategory;
	}
	public void setStSubCategory(String stSubCategory) {
		this.stSubCategory = stSubCategory;
	}
	public int getSr_No() {
		return sr_No;
	}
	public void setSr_No(int sr_No) {
		this.sr_No = sr_No;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getParticulars() {
		return particulars;
	}
	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}
	public String getCredit_Amt() {
		return credit_Amt;
	}
	public void setCredit_Amt(String credit_Amt) {
		this.credit_Amt = credit_Amt;
	}
	public String getDebit_Amt() {
		return debit_Amt;
	}
	public void setDebit_Amt(String debit_Amt) {
		this.debit_Amt = debit_Amt;
	}
	public String getTransaction_Date() {
		return transaction_Date;
	}
	public void setTransaction_Date(String transaction_Date) {
		this.transaction_Date = transaction_Date;
	}
	
	
	
	
}
