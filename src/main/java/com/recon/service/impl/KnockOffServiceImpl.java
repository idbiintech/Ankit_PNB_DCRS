package com.recon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.KnockOffDao;
import com.recon.model.KnockOffBean;
import com.recon.service.KnockOffService;

@Component
public class KnockOffServiceImpl implements KnockOffService{

	@Autowired
	KnockOffDao knockoffDao;
	
	
	@Override
	public void updateAutoReversal_Trans(KnockOffBean knockOffBean)throws Exception
	{
		knockoffDao.knockoffRecords(knockOffBean);
	}

}
