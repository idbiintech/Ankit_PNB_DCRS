package com.recon.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.recon.model.MastercardTTUMBean;

public interface MastercardTTUMService {

	//public List<String> getSubcategories(String category) throws Exception;
	public boolean checkAndMakeDirectory(MastercardTTUMBean beanObj);

	public List<Object> getMastercardTTUMData(MastercardTTUMBean beanObj);

	public void generateExcelTTUM(String stpath, String fileName, List<Object> excel_data, String fileName2,
			HttpServletResponse response, boolean b);

	//public List<Object> getMastercardTTUM(MastercardTTUMBean beanObj);

	List<Object> getMastercardEODTTUM(MastercardTTUMBean beanObj);
}
