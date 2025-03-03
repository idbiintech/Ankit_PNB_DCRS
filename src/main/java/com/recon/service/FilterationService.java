package com.recon.service;


import java.util.List;

import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;


public interface FilterationService {
	
	public List<FilterationBean> getaddedFiles()throws Exception;
	
	/**
	 * get search parameters from db
	 */
	public List<FilterationBean> getSearchParams(FilterationBean filterBean)throws Exception;
	
	/**
	 * generate Seg_tran_id
	 */
	public int getseg_tran_id()throws Exception;
	
	/**
	 * ENTRY IN SEGREGATE TABLE
	 */
	public int addEntry(FilterationBean filterBean)throws Exception;
	
	/**
	 *  Insert data in category_filename table
	 */
	public int filterRecords(FilterationBean filterBean)throws Exception;
	/**
	 * updating seg table 
	 */
	public int updateseg_txn(FilterationBean filterbean)throws Exception;
	
	/**
	 * 
	 * get trn Id for validation
	 * @param filterBeanObj
	 * @return
	 * @throws Exception
	 */
	public int getTrnId(FilterationBean filterBeanObj)throws Exception;
	
	/**
	 * auto reversal knock off
	 * 
	 */
	public void knockoffRecords(KnockOffBean knockOffBean)throws Exception;
	
	public boolean getReconRecords(FilterationBean filterBean,String stFile_date)throws Exception;
	
	public String getStatus(FilterationBean filterBean,String stProcess) throws Exception;
	
	public void CIA_GL_classsification(FilterationBean filterbeanObj)throws Exception;

	public boolean cardlesstxn(FilterationBean filterBean);
}
