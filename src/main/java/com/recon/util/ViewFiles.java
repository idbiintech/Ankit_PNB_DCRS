package com.recon.util;

public class ViewFiles {
	private String filetype;
	private String filecount;
	private String filename;
	private String remark;
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getFiletype() {
		return filetype;
	}
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
	public String getFilecount() {
		return filecount;
	}
	public void setFilecount(String filecount) {
		this.filecount = filecount;
	}
	public String getFilename() {
		return filename;
	}

	
	public void setFilename(String filename) {
		this.filename = filename;
	}


	private String NTSL_FILENAME;
	private String NTSL_TXN_COUNT;
	private String NTSL_TXN_AMOUNT;
	private String RAW_FILENAME;
	private String RAW_TXN_AMOUNT;
	private String RAW_TXN_COUNT;
	private String DIFF_COUNT;
	
	private String DIFF_AMOUNT;

	public String getNTSL_FILENAME() {
		return NTSL_FILENAME;
	}

	public void setNTSL_FILENAME(String nTSL_FILENAME) {
		NTSL_FILENAME = nTSL_FILENAME;
	}

	public String getNTSL_TXN_COUNT() {
		return NTSL_TXN_COUNT;
	}

	public void setNTSL_TXN_COUNT(String nTSL_TXN_COUNT) {
		NTSL_TXN_COUNT = nTSL_TXN_COUNT;
	}

	public String getNTSL_TXN_AMOUNT() {
		return NTSL_TXN_AMOUNT;
	}

	public void setNTSL_TXN_AMOUNT(String nTSL_TXN_AMOUNT) {
		NTSL_TXN_AMOUNT = nTSL_TXN_AMOUNT;
	}

	public String getRAW_FILENAME() {
		return RAW_FILENAME;
	}

	public void setRAW_FILENAME(String rAW_FILENAME) {
		RAW_FILENAME = rAW_FILENAME;
	}

	public String getRAW_TXN_AMOUNT() {
		return RAW_TXN_AMOUNT;
	}

	public void setRAW_TXN_AMOUNT(String rAW_TXN_AMOUNT) {
		RAW_TXN_AMOUNT = rAW_TXN_AMOUNT;
	}

	public String getRAW_TXN_COUNT() {
		return RAW_TXN_COUNT;
	}

	public void setRAW_TXN_COUNT(String rAW_TXN_COUNT) {
		RAW_TXN_COUNT = rAW_TXN_COUNT;
	}

	public String getDIFF_COUNT() {
		return DIFF_COUNT;
	}

	public void setDIFF_COUNT(String dIFF_COUNT) {
		DIFF_COUNT = dIFF_COUNT;
	}

	public String getDIFF_AMOUNT() {
		return DIFF_AMOUNT;
	}

	public void setDIFF_AMOUNT(String dIFF_AMOUNT) {
		DIFF_AMOUNT = dIFF_AMOUNT;
	}
	
	
	
	
}
