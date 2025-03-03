package com.recon.dao;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.UploadTTUMBean;

public interface TTUMDataUploadDao {
	
	public String readFile(UploadTTUMBean UploadBean,MultipartFile file) throws Exception;

}
