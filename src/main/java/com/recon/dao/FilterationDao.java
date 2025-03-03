package com.recon.dao;

import java.util.List;

import com.recon.model.FilterationBean;

public interface FilterationDao {

	
	public List<FilterationBean> getaddedFiles()throws Exception;
	
	public List<FilterationBean> getSearchParams(FilterationBean filterBean)throws Exception;
	
	public int getseg_tran_id()throws Exception;
	
	public int addEntry(FilterationBean filterBean)throws Exception;
	
	public int filterRecords(FilterationBean filterBean)throws Exception;
	
	public int updateseg_txn(FilterationBean filterbean)throws Exception;
	
	public int getTrnId(FilterationBean filterBeanObj)throws Exception;

	public String getStatus(FilterationBean filterBean, String stProcess) throws Exception;
	
	public void CIA_GL_classsification(FilterationBean filterbeanObj)throws Exception;

	public boolean cardlesstxn(FilterationBean filterBean);

	
	
}
