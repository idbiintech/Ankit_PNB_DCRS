package com.recon.dao;

import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;


public interface KnockOffDao {
	
	
	public void knockoffRecords(KnockOffBean knockOffBean)throws Exception;

	public boolean getReconRecords(FilterationBean knockOffBean,String stFile_date)throws Exception;
}
