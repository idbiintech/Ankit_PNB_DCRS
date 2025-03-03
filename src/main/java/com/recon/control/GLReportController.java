package com.recon.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.recon.dao.GLReportdao;
import com.recon.model.GLRemitterListBean;
import com.recon.model.GLRemitterReportBean;
import com.recon.model.LoginBean;
import com.recon.service.ISourceService;


@Controller
public class GLReportController {
	
	@Autowired
	GLReportdao glReportdao;
	
	@Autowired ISourceService iSourceService;
	
	@RequestMapping(value="NFSGLReport" , method = RequestMethod.GET)
	public ModelAndView getTxteport(ModelAndView modelAndView,HttpServletRequest request)throws Exception {

		GLRemitterReportBean beanObj = new GLRemitterReportBean();
		
		List<String> subcat = iSourceService.getSubcategories("NFS");

		modelAndView.addObject("reportBean",beanObj);
		modelAndView.addObject("subcategory",subcat );
		//modelAndView.setViewName("GLReport");
		modelAndView.setViewName("NFSGLReport");
		
		return modelAndView;
	}
	
	@RequestMapping(value="NFSGLReport" , method = RequestMethod.POST)
	public ModelAndView NFSGLReport(ModelAndView modelAndView,HttpServletRequest request) {

		System.out.println("Inside NFSGLReport Post");
		String fileName = request.getParameter("fileName");
		System.out.println("Filename selected is "+fileName);
		String subCategory = request.getParameter("stSubCategory");
		System.out.println("subcategory selected is "+subCategory);
		GLRemitterReportBean beanObj = new GLRemitterReportBean();
		
		modelAndView.addObject("reportBean",beanObj);
		modelAndView.addObject("fileName",fileName);
		modelAndView.addObject("stSubCategory",subCategory);
		modelAndView.setViewName("GLReport");
		
		return modelAndView;
	}
	
	 @RequestMapping(value = "viewNFSGLReport", method = RequestMethod.POST)
	 public  ModelAndView viewGLReport (HttpServletRequest req,
			 ModelAndView modelAndView,
			 HttpSession httpSession)
	 {
		 List<GLRemitterReportBean> beanObjList = new ArrayList<GLRemitterReportBean>();
		System.out.println("Inside viewGLReport");
		GLRemitterListBean beanList = new GLRemitterListBean();
		
		String fileName = req.getParameter("fileName");
		System.out.println("filename is "+fileName);
		String fildate = req.getParameter("fromDate");
		System.out.println("fdate is "+fildate);
		String subCategory = req.getParameter("stSubCategory");
		System.out.println("subcategory selected is "+subCategory);
		String checkerFlag = "Y";
		
		if(subCategory.equalsIgnoreCase("ISSUER"))
		{
			beanObjList = glReportdao.getNFSIssGLData(fildate, fileName);

			beanList.setGlRemitterBean(beanObjList);

			checkerFlag = glReportdao.getNFSIssCheckerFlag(fildate, fileName);
		}
		else if(subCategory.equalsIgnoreCase("ACQUIRER"))
		{
			beanObjList = glReportdao.getNFSIssGLData(fildate, fileName);

			beanList.setGlRemitterBean(beanObjList);

			checkerFlag = glReportdao.getNFSIssCheckerFlag(fildate, fileName);
		}
		
		beanList.setCheckerFlag(checkerFlag);
		beanList.setFileDate(fildate);
		beanList.setListSize(beanObjList.size());
		beanList.setFileName(fileName);
		
		
		modelAndView.addObject("beanObjList",beanList);
		modelAndView.addObject("fileName",fileName);
//		modelAndView.addObject("tranDate",fildate);
//		modelAndView.addObject("checkerFlag",checkerFlag);
		 modelAndView.setViewName("GLReport");
			return modelAndView;
	 }

	 @RequestMapping(value = "saveNFSGLform", method = RequestMethod.POST)
	 @ResponseBody
	 public  ModelAndView saveGLform (@ModelAttribute("reportBean") GLRemitterListBean beanObj, HttpServletRequest request,
			 ModelAndView modelAndView,
			 HttpSession httpSession)
	 {
		 String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		 System.out.println("Created by is "+Createdby);
		 String fileName = request.getParameter("fileName");
			System.out.println("filename is "+fileName);
			
			String subCategory = request.getParameter("stSubCategory");
			System.out.println("subcategory selected is "+subCategory);
//		 System.out.println("bean Obj list size is "+beanObj.getGlRemitterBean().size());
		 
		 List<GLRemitterReportBean> beanLst = new ArrayList<GLRemitterReportBean>();
		 for(int i = 0 ; i < beanObj.getListSize() ;i ++)
		 {
			 GLRemitterReportBean bean = new GLRemitterReportBean();
			 bean.setSr_No(Integer.parseInt(request.getParameter("sr_No" + i)));
			 bean.setParticulars(request.getParameter("particulars" + i));
			 bean.setDebit_Amt(request.getParameter("debit_Amt" + i));
			 bean.setCredit_Amt(request.getParameter("credit_Amt" + i));
			 bean.setBalance(request.getParameter("balance" + i));
			 bean.setCrdr_Diff(request.getParameter("crdr_Diff"+i));
			 beanLst.add(bean);
		 }
		 beanObj.setGlRemitterBean(beanLst);
		 
		 if(subCategory.equalsIgnoreCase("ISSUER"))
		 {
			 boolean updateFlag = glReportdao.saveNFSIssGLData(beanObj, fileName, Createdby);

			 System.out.println("Update flag is "+updateFlag);
		 }
		 else if(subCategory.equalsIgnoreCase("ACQUIRER"))
		 {
			 boolean updateFlag = glReportdao.saveNFSAcqGLData(beanObj, fileName, Createdby);

			 System.out.println("Update flag is "+updateFlag);
		 }
		 modelAndView.addObject("reportBean",beanObj);
		 modelAndView.addObject("msg","Y");
			modelAndView.setViewName("NFSGLReport");
			
			return modelAndView;
	 }
	 
/******************** VISA GL REPORT LOGIC ****************/	 
	 @RequestMapping(value="VISAGLReport" , method = RequestMethod.GET)
		public ModelAndView VISAGLReport(ModelAndView modelAndView,HttpServletRequest request) {

		 GLRemitterListBean beanObj = new GLRemitterListBean();
			
			modelAndView.addObject("reportBean",beanObj);
			modelAndView.setViewName("VisaGLReport");
			
			return modelAndView;
		}
	 
	 @RequestMapping(value = "viewVISAGLReport", method = RequestMethod.POST)
	 public  ModelAndView viewVISAGLReport (HttpServletRequest req,
			 ModelAndView modelAndView,
			 HttpSession httpSession)
	 {
		 List<GLRemitterReportBean> beanObjList = new ArrayList<GLRemitterReportBean>();
		System.out.println("Inside viewGLReport subcategory is "+req.getParameter("stSubCategory"));
		GLRemitterListBean beanList = new GLRemitterListBean();
		// call all procedures and keep data in variables
		String fildate = req.getParameter("fromDate");
		System.out.println("fdate is "+fildate);
		String subCategory = req.getParameter("stSubCategory");
		
		beanObjList = glReportdao.getVisaGLData(fildate, subCategory);
		
		beanList.setGlRemitterBean(beanObjList);
		
		String checkerFlag = glReportdao.getVisaCheckerFlag(fildate, subCategory);
		
		beanList.setCheckerFlag(checkerFlag);
		beanList.setFileDate(fildate);
		beanList.setListSize(beanObjList.size());
		beanList.setStSubCategory(req.getParameter("stSubCategory"));
		
		
		modelAndView.addObject("beanObjList",beanList);
//		modelAndView.addObject("tranDate",fildate);
//		modelAndView.addObject("checkerFlag",checkerFlag);
		 modelAndView.setViewName("VisaGLReport");
			return modelAndView;
	 }
		
		 @RequestMapping(value = "saveVISAGLform", method = RequestMethod.POST)
		 @ResponseBody
		 public  ModelAndView saveVISAGLform (@ModelAttribute("reportBean") GLRemitterListBean beanObj, HttpServletRequest request,
				 ModelAndView modelAndView,
				 HttpSession httpSession)
		 {
			 String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			 System.out.println("Created by is "+Createdby);
				
			 System.out.println("check subcategory "+beanObj.getStSubCategory());
//			 System.out.println("bean Obj list size is "+beanObj.getGlRemitterBean().size());
			 
			 List<GLRemitterReportBean> beanLst = new ArrayList<GLRemitterReportBean>();
			 for(int i = 0 ; i < beanObj.getListSize() ;i ++)
			 {
				 GLRemitterReportBean bean = new GLRemitterReportBean();
				 bean.setSr_No(Integer.parseInt(request.getParameter("sr_No" + i)));
				 bean.setParticulars(request.getParameter("particulars" + i));
				 bean.setDebit_Amt(request.getParameter("debit_Amt" + i));
				 bean.setCredit_Amt(request.getParameter("credit_Amt" + i));
				 bean.setBalance(request.getParameter("balance" + i));
				 bean.setCrdr_Diff(request.getParameter("crdr_Diff"+i));
				 beanLst.add(bean);
			 }
			 beanObj.setGlRemitterBean(beanLst);
			 
			 boolean updateFlag = glReportdao.saveVisaGLData(beanObj, Createdby);
			 
			 System.out.println("Update flag is "+updateFlag);
			 
			 modelAndView.addObject("reportBean",beanObj);
				modelAndView.setViewName("VisaGLReport");
				
				return modelAndView;
		 }
	
}
