package com.recon.control;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.recon.model.ConfigurationBean;
import com.recon.service.ConfigurationService;

@Controller
public class ConfigurationController {

	@Autowired
	ConfigurationService configurationService;
	
	
/*
 * Compare Configuration
 * 
 */
@RequestMapping(value="CompareConfiguration" , method = RequestMethod.GET)
public String CompareConfiguration(Model model, ConfigurationBean configBean)
{
	List<ConfigurationBean> comp_dtl_list = new ArrayList<ConfigurationBean>();
	/** Add new Objects if less or none are available. */
	for (int i = comp_dtl_list.size() + 1; i <= 1; i++) {
		comp_dtl_list.add(new ConfigurationBean());
	}
	
	configBean.setComp_dtl_list(comp_dtl_list);
	
	model.addAttribute("ConfigBean",configBean);
	model.addAttribute("comp_list",comp_dtl_list);
	return "ConfigurType";
}


	
}
