package com.recon.dao;

import java.util.List;

import com.recon.model.GLRemitterListBean;
import com.recon.model.GLRemitterReportBean;


public interface GLReportdao {

	//GLReportBean getRemitterData(String fileDate);
	
	List<GLRemitterReportBean> getNFSIssGLData(String fileDate,String fileName);
	
	String getNFSIssCheckerFlag(String fileDate, String fileName);
	
	boolean saveNFSIssGLData(GLRemitterListBean beanObjLst,String fileName,  String userId);
	
	List<GLRemitterReportBean> getNFSAcqGLData(String fileDate,String fileName);

	String getNFSAcqCheckerFlag(String fileDate, String fileName);
	
	boolean saveNFSAcqGLData(GLRemitterListBean beanObjLst,String fileName,  String userId);
	
	/***** VISA METHODS *******/
	List<GLRemitterReportBean> getVisaGLData(String fileDate,String subCategory);
	
	String getVisaCheckerFlag(String fileDate,String subCategory);
	
	boolean saveVisaGLData(GLRemitterListBean beanObjLst, String userId);
}
