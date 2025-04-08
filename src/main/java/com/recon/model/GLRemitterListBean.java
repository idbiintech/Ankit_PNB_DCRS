package com.recon.model;

import java.util.List;

public class GLRemitterListBean {
	
	private List<GLRemitterReportBean> glRemitterBean;
	
	private String checkerFlag;
	
	private String fileDate;
	
	private int listSize;
	
	private String stSubCategory;
	
	private String fileName;
	
	
	
	

	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getStSubCategory() {
		return stSubCategory;
	}

	public void setStSubCategory(String stSubCategory) {
		this.stSubCategory = stSubCategory;
	}

	public int getListSize() {
		return listSize;
	}

	public void setListSize(int listSize) {
		this.listSize = listSize;
	}

	public String getFileDate() {
		return fileDate;
	}

	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}

	public List<GLRemitterReportBean> getGlRemitterBean() {
		return glRemitterBean;
	}

	public void setGlRemitterBean(List<GLRemitterReportBean> glRemitterBean) {
		this.glRemitterBean = glRemitterBean;
	}

	public String getCheckerFlag() {
		return checkerFlag;
	}

	public void setCheckerFlag(String checkerFlag) {
		this.checkerFlag = checkerFlag;
	}
	
	
	

}
