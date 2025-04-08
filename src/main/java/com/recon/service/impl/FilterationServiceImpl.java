package com.recon.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.FilterationDao;
import com.recon.dao.KnockOffDao;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.service.FilterationService;

@Component
public class FilterationServiceImpl implements FilterationService{

	@Autowired
	FilterationDao filterdao;
	
	@Autowired
	KnockOffDao knockoffDao;

	
	@Override
	public List<FilterationBean> getaddedFiles()throws Exception
	{
		return filterdao.getaddedFiles();
	}
	
	@Override
	public List<FilterationBean> getSearchParams(FilterationBean filterBean)throws Exception
	{
		return filterdao.getSearchParams(filterBean);
	}
	
	@Override
	public int getseg_tran_id()throws Exception
	{
		return filterdao.getseg_tran_id();
	}
	
	@Override
	public int addEntry(FilterationBean filterBean)throws Exception
	{
		return filterdao.addEntry(filterBean);
	}
	
	@Override
	public int filterRecords(FilterationBean filterBean)throws Exception
	{
		return filterdao.filterRecords(filterBean);
	}
	
	@Override
	public int updateseg_txn(FilterationBean filterbean)throws Exception
	{
		return filterdao.updateseg_txn(filterbean);
		
	}
	
	@Override
	public int getTrnId(FilterationBean filterBeanObj)throws Exception
	{
		return filterdao.getTrnId(filterBeanObj);
	}
	
	public void knockoffRecords(KnockOffBean knockOffBean)throws Exception
	{
		knockoffDao.knockoffRecords(knockOffBean);
	}
	
	@Override
	public boolean getReconRecords(FilterationBean filterationBean,String stFile_date)throws Exception
	{
		return knockoffDao.getReconRecords(filterationBean,stFile_date);
	}

	@Override
	public String getStatus(FilterationBean filterBean, String stProcess) throws Exception {
		//filter
		return filterdao.getStatus( filterBean, stProcess); 
	}
	
	@Override
	public void CIA_GL_classsification(FilterationBean filterbeanObj)throws Exception
	{
		filterdao.CIA_GL_classsification(filterbeanObj);
	}

	@Override
	public boolean cardlesstxn(FilterationBean filterBean) {
		
		return filterdao.cardlesstxn( filterBean);
		
	}
}

