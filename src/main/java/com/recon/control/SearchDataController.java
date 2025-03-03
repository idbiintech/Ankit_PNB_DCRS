package com.recon.control;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.recon.service.NFSUnmatchTTUMService;
import com.recon.util.CSRFToken;
import com.recon.util.SearchData;

@Controller
public class SearchDataController {
	

	@Autowired 
	NFSUnmatchTTUMService nfsTTUMService;
	private static final Logger logger = Logger.getLogger(SearchDataController.class);
	
	/*
	 * @RequestMapping(value = "searchData", method = RequestMethod.GET) public
	 * ModelAndView seeRule(ModelAndView modelAndView) {
	 * modelAndView.setViewName("SearchData"); return modelAndView;
	 * 
	 * }
	 */
	
	@RequestMapping(value = "searchData", method = RequestMethod.GET)
	public ModelAndView FullRefundTTUMGet(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
		logger.info("***** searchData.Get Start ****");
		
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);

		modelAndView.setViewName("searchData");
		
		logger.info("***** searchData.searchData GET End ****");
		return modelAndView;
	}
	
	
	@RequestMapping(value = "/searchData", method = RequestMethod.POST)
	@ResponseBody
	public List<SearchData> searchViewFile(@RequestParam("category")String category,String stSubCategory,String rrn_no,String filedate) throws SQLException, Exception {
		 System.out.println("data "+ category+ "  "+stSubCategory +" " + rrn_no+" "+filedate);
		 
		 return  nfsTTUMService.searchData(category, rrn_no, filedate);

	}
	
	

}
