package com.recon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.recon.dao.TTUMDataUploadDao;
import com.recon.model.UploadTTUMBean;
import com.recon.service.TTUMDataUploadService;

@Component
public class TTUMDataUploadServiceImpl implements TTUMDataUploadService
{
	@Autowired
	TTUMDataUploadDao ttumDao;

	@Override
	public String readFile(UploadTTUMBean UploadBean,MultipartFile file) throws Exception
	{
		return ttumDao.readFile(UploadBean,file);
	}

}
