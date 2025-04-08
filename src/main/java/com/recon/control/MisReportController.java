package com.recon.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.model.CompareSetupBean;
import com.recon.service.ISettelmentService;
import com.recon.service.MisReportService;
import com.recon.util.CSRFToken;

@Controller
public class MisReportController {
	private static final Logger logger = Logger.getLogger(ManualKnockoffController.class);
	
	public static final String ICCW_FOLDER = System.getProperty("catalina.home") + File.separator + "ICCW";
	
	private static final String ERROR_MSG = "error_msg";

	@Autowired
	MisReportService misReportService;
	
	@Autowired
	ISettelmentService isettelmentservice;
	
	
	
	@RequestMapping(value = "RupayIntMisReport", method = RequestMethod.GET)
	public ModelAndView RupayIntMisReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** RupayINTMISBackView.Get Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("RupayINTMISBackView GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("RupayIntMisReport");
		logger.info("***** manulRollBackView.Get End ****");
		return modelAndView;

	}

	@RequestMapping(value = "RupayDomMisReport", method = RequestMethod.GET)
	public ModelAndView RupayDomMisReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** RupayDOMMISBackView.Get Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("RupayDOMMISBackView GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("RupayDomMisReport");
		logger.info("***** manulRollBackView.Get End ****");
		return modelAndView;

	}
	
	@RequestMapping(value = "NfsPayMisReport", method = RequestMethod.GET)
	public ModelAndView NfsPayMisReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NfsPayMisReportView.Get Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("NfsPayMisReportView GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("NfsPayMisReport");
		logger.info("***** manulRollBackView.Get End ****");
		return modelAndView;

	}
	
	@RequestMapping(value = "NfsRecMisReport", method = RequestMethod.GET)
	public ModelAndView NfsRecMisReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NfsPayMisReportView.Get Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("NfsPayMisReportView GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("NfsRecMisReport");
		logger.info("***** manulRollBackView.Get End ****");
		return modelAndView;

	}
	

	@RequestMapping(value = "VisaMisReport", method = RequestMethod.GET)
	public ModelAndView VisaMisReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NfsPayMisReportView.Get Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("NfsPayMisReportView GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("VisaMisReport");
		logger.info("***** manulRollBackView.Get End ****");
		return modelAndView;

	}
	
	
	@RequestMapping(value = "RupayDomGlReport", method = RequestMethod.GET)
	public ModelAndView RupayDomGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** RupayDomGlReport.Get Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("RupayDomGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("RupayDomGlReport");
		logger.info("***** RupayDomGlReport.Get End ****");
		return modelAndView;

	}
	
	@RequestMapping(value = "VisaDomGlReport", method = RequestMethod.GET)
	public ModelAndView VisaDomGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** VisaDomGlReport.Get Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("VisaDomGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("VisaDomGlReport");
		logger.info("***** VisaDomGlReport.Get End ****");
		return modelAndView;

	}
	
	

	@RequestMapping(value = "NFSACQChargebackGlReport", method = RequestMethod.GET)
	public ModelAndView NFSACQChargebackGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NFSACQChargebackGlReport.Get Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("NFSACQChargebackGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("NfsAcqChargebackGlReports");
		logger.info("***** NFSACQChargebackGlReport.Get End ****");
		return modelAndView;

	}

	@RequestMapping(value = "NFSACQPrearbGlReport", method = RequestMethod.GET)
	public ModelAndView NFSACQPrearbGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NFSACQPrearbGlReport.Get Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("NFSACQPrearbGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("NFSACQPrearbGlReport");
		logger.info("***** NFSACQPrearbGlReport.Get End ****");
		return modelAndView;

	}
	
	@RequestMapping(value = "NFSACQDebitGlReport", method = RequestMethod.GET)
	public ModelAndView NFSACQDebitGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NFSACQDebitGlReport.Get Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("NFSACQPrearbGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("NFSACQDebitGlReport");
		logger.info("***** NFSACQDebitGlReport.Get End ****");
		return modelAndView;

	}
	
	@RequestMapping(value = "NFSACQCreditGlReport", method = RequestMethod.GET)
	public ModelAndView NFSACQCreditGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NFSACQCreditGlReport.Get Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("NFSACQCreditGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("NFSACQCreditGlReport");
		logger.info("***** NFSACQCreditGlReport.Get End ****");
		return modelAndView;

	}
	
	@RequestMapping(value = "NFSISSChargebackGlReport", method = RequestMethod.GET)
	public ModelAndView NFSISSChargebackGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NFSISSChargebackGlReport.Get Start ****");
		System.out.println("NFSISSChargebackGlReport");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("NFSISSChargebackGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("NFSISSChargebackGlReport");
		logger.info("***** NFSISSChargebackGlReport.Get End ****");
		return modelAndView;

	}
	
	@RequestMapping(value = "NFSISSCreditGlReport", method = RequestMethod.GET)
	public ModelAndView NFSISSCreditGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NFSISSCreditGlReport.Get Start ****");
		System.out.println("NFSISSCreditGlReport");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("NFSISSCreditGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("NFSISSCreditGlReport");
		logger.info("***** NFSISSCreditGlReport.Get End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "NFSISSDebitGlReport", method = RequestMethod.GET)
	public ModelAndView NFSISSDebitGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NFSISSDebitGlReport.Get Start ****");
		System.out.println("NFSISSDebitGlReport");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("NFSISSDebitGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("NFSISSDebitGlReport");
		logger.info("***** NFSISSDebitGlReport.Get End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "NFSISSPrearbGlReport", method = RequestMethod.GET)
	public ModelAndView NFSISSPrearbGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NFSISSPrearbGlReport.Get Start ****");
		System.out.println("NFSISSPrearbGlReport");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("NFSISSPrearbGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("NFSISSPrearbGlReport");
		logger.info("***** NFSISSPrearbGlReport.Get End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "VisaIntGlReport", method = RequestMethod.GET)
	public ModelAndView VisaIntGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** VisaIntGlReport.Get Start ****");
		System.out.println("VisaIntGlReport");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("VisaIntGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("VisaIntGlReport");
		logger.info("***** VisaIntGlReport.Get End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "RupayMirrorGlReport", method = RequestMethod.GET)
	public ModelAndView RupayMirrorGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** RupayMirrorGlReport.Get Start ****");
		System.out.println("RupayMirrorGlReport");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("RupayMirrorGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("RupayMirrorGlReport");
		logger.info("***** RupayMirrorGlReport.Get End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "NFSMirrorGlReport", method = RequestMethod.GET)
	public ModelAndView NFSMirrorGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NFSMirrorGlReport.Get Start ****");
		System.out.println("NFSMirrorGlReport");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("NFSMirrorGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("NFSMirrorGlReport");
		logger.info("***** NFSMirrorGlReport.Get End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "RupayIRGCSMirrorGlReport", method = RequestMethod.GET)
	public ModelAndView RupayIRGCSMirrorGlReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** RupayIRGCSMirrorGlReport.Get Start ****");
		System.out.println("RupayIRGCSMirrorGlReport");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		String display = "";
		logger.info("RupayIRGCSMirrorGlReport GET");
//         List<String> subcat = iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC")) {
			display = "none";
		}
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
//		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display", display);
		modelAndView.setViewName("RupayIRGCSMirrorGlReport");
		logger.info("***** RupayIRGCSMirrorGlReport.Get End ****");
		return modelAndView;
	}

	@RequestMapping(value = "/RupayMisReportProcess.do", method = RequestMethod.POST)
	public String RupayMisReportProcess(HttpSession session, HttpServletResponse response, HttpServletRequest req,
			Model model, @RequestParam("fromDate") String fdate) {
		System.out.println("fdate: "+fdate);
		String tableName="RUP_INT_DAILY_MIS";
		ServletOutputStream sou = null;
		String fdate2="";
		String fileName = null;
		String[] split = fdate.split("-");
//		fdate2 = split[0]+split[1]+split[2].substring(2);
		fdate2 = split[0]+split[1]+split[2];
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
//				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return "RupayIntMisReport";
			} else {
				String result =	misReportService.rupayProcessMisReport(fdate2);
//				model.addAttribute("message", SUCCESS);
				try {
					
						fileName = misReportService.downloadRupayMisReport(fdate,tableName);
						// model.addAttribute("message", SUCCESS);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return "RupayIntMisReport";
	}
	
	@RequestMapping(value = "/RupayDomMisReportProcess.do", method = RequestMethod.POST)
	public String RupayDomMisReportProcess(HttpSession session, HttpServletResponse response, HttpServletRequest req,
			Model model, @RequestParam("fromDate") String fdate) {
		String tableName="RUP_DOM_DAILY_MIS";
		System.out.println("fdate: "+fdate);
		ServletOutputStream sou = null;
		String fdate2="";
		String fileName = null;
		String[] split = fdate.split("-");
//		fdate2 = split[0]+split[1]+split[2].substring(2);
		fdate2 = split[0]+split[1]+split[2];
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
//				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return "RupayDomMisReport";
			} else {
				String result =	misReportService.rupayProcessDomMisReport(fdate2);
//				model.addAttribute("message", SUCCESS);
				try {
					
						fileName = misReportService.downloadRupayMisReport(fdate,tableName);
						// model.addAttribute("message", SUCCESS);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return "RupayDomMisReport";
	}
	
	@RequestMapping(value = "/NfsPayMisReportProcess.do", method = RequestMethod.POST)
	public String NfsPayMisReportProcess(HttpSession session, HttpServletResponse response, HttpServletRequest req,
			Model model, @RequestParam("fromDate") String fdate) {
		String tableName="NFS_PAY_DAILY_MIS";
		System.out.println("fdate: "+fdate);
		ServletOutputStream sou = null;
		String fdate2="";
		String fileName = null;
		String[] split = fdate.split("-");
//		fdate2 = split[0]+split[1]+split[2].substring(2);
		fdate2 = split[0]+split[1]+split[2];
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
//				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return "NfsPayMisReport";
			} else {
				String result =	misReportService.nfsProcessPayMisReport(fdate2);
//				model.addAttribute("message", SUCCESS);
				try {
					
						fileName = misReportService.downloadRupayMisReport(fdate,tableName);
						// model.addAttribute("message", SUCCESS);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return "NfsPayMisReport";
	}
	
	
	@RequestMapping(value = "/NfsRecMisReportProcess.do", method = RequestMethod.POST)
	public String NfsRecMisReportProcess(HttpSession session, HttpServletResponse response, HttpServletRequest req,
			Model model, @RequestParam("fromDate") String fdate) {
		String tableName="NFS_REC_DAILY_MIS";
		System.out.println("fdate: "+fdate);
		ServletOutputStream sou = null;
		String fdate2="";
		String fileName = null;
		String[] split = fdate.split("-");
//		fdate2 = split[0]+split[1]+split[2].substring(2);
		fdate2 = split[0]+split[1]+split[2];
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
//				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return "NfsRecMisReport";
			} else {
				String result =	misReportService.nfsProcessRecMisReport(fdate2);
//				model.addAttribute("message", SUCCESS);
				try {
					
						fileName = misReportService.downloadRupayMisReport(fdate,tableName);
						// model.addAttribute("message", SUCCESS);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return "NfsRecMisReport";
	}

	@RequestMapping(value = "/VisaMisReportProcess.do", method = RequestMethod.POST)
	public String VisaMisReportProcess(HttpSession session, HttpServletResponse response, HttpServletRequest req,
			Model model, @RequestParam("fromDate") String fdate) {
		String tableName="VISA_DAILY_MIS";
		System.out.println("fdate: "+fdate);
		ServletOutputStream sou = null;
		String fdate2="";
		String fileName = null;
		String[] split = fdate.split("-");
//		fdate2 = split[0]+split[1]+split[2].substring(2);
		fdate2 = split[0]+split[1]+split[2];
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
//				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return "VisaMisReport";
			} else {
				String result =	misReportService.visaProcessMisReport(fdate2);
//				model.addAttribute("message", SUCCESS);
				try {
					
						fileName = misReportService.downloadRupayMisReport(fdate,tableName);
						// model.addAttribute("message", SUCCESS);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return "VisaMisReport";
	}

	
	@RequestMapping(value = "/RupaydownloadMisReport.do", method = RequestMethod.POST)
	public String RupaydownloadMisReport(HttpSession session, HttpServletResponse response, HttpServletRequest req,
			Model model, @RequestParam("fromDate") String fdate) {
		System.out.println("fdate: "+fdate);
		String tableName="RUP_INT_DAILY_MIS";
		ServletOutputStream sou = null;
		String fileName = null;
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "RupayIntMisReport";
			} else {
				try {
					
						fileName = misReportService.downloadRupayMisReport(fdate,tableName);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return "RupayIntMisReport";
	}
	
	@RequestMapping(value = "/RupayDomdownloadMisReport.do", method = RequestMethod.POST)
	public String RupayDomdownloadMisReport(HttpSession session, HttpServletResponse response, HttpServletRequest req,
			Model model, @RequestParam("fromDate") String fdate) {
		System.out.println("fdate: "+fdate);
		String tableName="RUP_DOM_DAILY_MIS";
		ServletOutputStream sou = null;
		String fileName = null;
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "RupayDomMisReport";
			} else {
				try {
					
						fileName = misReportService.downloadRupayMisReport(fdate,tableName);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return "RupayDomMisReport";
	}
	
	@RequestMapping(value = "/NfsPaydownloadMisReport.do", method = RequestMethod.POST)
	public String NfsPaydownloadMisReport(HttpSession session, HttpServletResponse response, HttpServletRequest req,
			Model model, @RequestParam("fromDate") String fdate) {
		System.out.println("fdate: "+fdate);
		String tableName="NFS_PAY_DAILY_MIS";
		ServletOutputStream sou = null;
		String fileName = null;
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NfsPayMisReport";
			} else {
				try {
					
						fileName = misReportService.downloadRupayMisReport(fdate,tableName);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return "NfsPayMisReport";
	}
	                          
	@RequestMapping(value = "/NfsRecdownloadMisReport.do", method = RequestMethod.POST)
	public String NfsRecdownloadMisReport(HttpSession session, HttpServletResponse response, HttpServletRequest req,
			Model model, @RequestParam("fromDate") String fdate) {
		System.out.println("fdate: "+fdate);
		String tableName="NFS_REC_DAILY_MIS";
		ServletOutputStream sou = null;
		String fileName = null;
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NfsRecMisReport";
			} else {
				try {
					
						fileName = misReportService.downloadRupayMisReport(fdate,tableName);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return "NfsRecMisReport";
	}
	
	@RequestMapping(value = "/VisadownloadMisReport.do", method = RequestMethod.POST)
	public String VisadownloadMisReport(HttpSession session, HttpServletResponse response, HttpServletRequest req,
			Model model, @RequestParam("fromDate") String fdate) {
		System.out.println("fdate: "+fdate);
		String tableName="VISA_DAILY_MIS";
		ServletOutputStream sou = null;
		String fileName = null;
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "VisaMisReport";
			} else {
				try {
					
						fileName = misReportService.downloadRupayMisReport(fdate,tableName);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return "VisaMisReport";
	}

	@RequestMapping(value = "RupayDomGlReportProcess.do", method = RequestMethod.POST)
	public String RupayDomGlReportProcess(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_DOMESTIC_RUPAY";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
//				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return "RupayDomGlReport";
			} else {
				misReportService.RupayDomGlReportProcess(fdate,openingBalance,finalEodBalance);
//				model.addAttribute("message", SUCCESS);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
//						model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
						return "RupayDomGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "RupayDomGlReport";
	}
	
	@RequestMapping(value = "downloadRupayDomGlReport.do", method = RequestMethod.POST)
	public String downloadRupayDomGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_DOMESTIC_RUPAY";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
//				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return "RupayDomGlReport";
			} else {
//				model.addAttribute("message", SUCCESS);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
//						model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
						return "RupayDomGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "RupayDomGlReport";
	}
	
	@RequestMapping(value = "VisaDomGlReportProcess.do", method = RequestMethod.POST)
	public String VisaDomGlReportProcess(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_VISA_DOMESTIC_PAYABLE";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
//				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return "VisaDomGlReport";
			} else {
				misReportService.VisaDomGlReportProcess(fdate,openingBalance,finalEodBalance);
//				model.addAttribute("message", SUCCESS);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
//						model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
						return "VisaDomGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "VisaDomGlReport";
	}
	
	@RequestMapping(value = "downloadVisaDomGlReport.do", method = RequestMethod.POST)
	public String downloadVisaDomGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_VISA_DOMESTIC_PAYABLE";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
//				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return "VisaDomGlReport";
			} else {
//				model.addAttribute("message", SUCCESS);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
//						model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
						return "VisaDomGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "VisaDomGlReport";
	}
	
	
	@RequestMapping(value = "VisaIntGlReportProcess.do", method = RequestMethod.POST)
	public String VisaIntGlReportProcess(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_VISA_INTERNATIONAL_PAYABLE";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
//				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return "VisaIntGlReport";
			} else {
				misReportService.VisaIntGlReportProcess(fdate,openingBalance,finalEodBalance);
//				model.addAttribute("message", SUCCESS);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
//						model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
						return "VisaIntGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "VisaIntGlReport";
	}

	@RequestMapping(value = "downloadVisaIntGlReport.do", method = RequestMethod.POST)
	public String downloadVisaIntGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_VISA_INTERNATIONAL_PAYABLE";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
//				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return "VisaIntGlReport";
			} else {
//				model.addAttribute("message", SUCCESS);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
//						model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
						return "VisaIntGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "VisaIntGlReport";
	}


	/*@RequestMapping(value = "iccwDownload", method = RequestMethod.POST)
	@ResponseBody
	public String iccwDownload(Model model, HttpServletResponse response, CompareSetupBean setupBean,
			HttpSession httpSession) {
		ServletOutputStream sou = null;
		String fileName = "";
		try {

			String BhimModul = "Bhim";
			fileName = misReportService.generateICCWReport(response, BhimModul);

			model.addAttribute("message", "DONE");
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
			FileInputStream ins = new FileInputStream(new File(fileName));
			sou = response.getOutputStream();
			sou.write(IOUtils.toByteArray(ins));
			ins.close();
			sou.flush();
			sou.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				new File(fileName).delete();
				sou.flush();
				sou.close();
				System.gc();
			} catch (Exception e2) {
			}
		}
		return null;
	}*/
	
	
	@RequestMapping(value = "iccwDownload", method = RequestMethod.POST)
	@ResponseBody
	public void iccwDownload(Model model, HttpServletResponse response, CompareSetupBean SettlementBean,
			HttpSession httpSession, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {

		//String TEMP_DIR = System.getProperty("java.io.tmpdir");
		String TEMP_DIR = ICCW_FOLDER;
		ServletContext context = request.getServletContext();
		// SettlementBean settlementBean = new SettlementBean();
		/*
		 * settlementBean.setCategory(category);
		 * settlementBean.setStsubCategory(subCat);
		 * settlementBean.setDatepicker(filedate);
		 */

		/*
		 * String userHome1 = System.getProperty("user.home"); userHome1 =
		 * userHome1+"\\Desktop\\Reports"; String
		 * userHome=userHome1.replace("/", "\\");
		 * 
		 * SettlementBean.setStPath(userHome);
		 */

		// settlementBean.setStPath(stPath);
		try {
			logger.info("INSIDE DOWNLOAD REPORTS");
			// DELETING FILES FROM DRIVE
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			java.util.Date date = sdf.parse(SettlementBean.getFileDate());

			sdf = new SimpleDateFormat("dd-MM-yyyy");

			String stnewDate = sdf.format(date);
			// isettelmentservice.DeleteFiles(userHome+"\\"+stnewDate);
			//String stpath = SettlementBean.getStPath() + "\\"+SettlementBean.getCategory();
			//INT 8624
			String stpath = TEMP_DIR+File.separator+"ICCW";
			//String stpath = TTUM_FOLDER+File.separator+SettlementBean.getCategory();
			System.out.println("stpath is "+stpath);
			isettelmentservice.DeleteFiles(stpath);
			SettlementBean.setStPath(stpath);
			
			//String stpath = SettlementBean.getStPath() + "\\" + stnewDate+"\\"+SettlementBean.getCategory();

			// 1.CHECK WHETHER RECON HAS BEEN PROCESSED FOR THE SELECTED DATE
			// System.out.println("HELLO");
			/*if (!SettlementBean.getStsubCategory().equals("-")) {
				SettlementBean.setStMergerCategory("ICCW_SWITCH");
			} else*/
				SettlementBean
						.setStMergerCategory("ICCW");

			/*if (SettlementBean.getCategory().equals("CARDTOCARD")) {
				String path = SettlementBean.getStPath();
				File myFile = new File(SettlementBean.getStPath() +File.separator);
				System.out.println("myFile"+myFile);
				// boolean val=iSourceService.generateCTF(SettlementBean);
			}*/
			isettelmentservice.generate_Reports_ICCW(SettlementBean);

			// added by int5779 for downloading zip

			String stFilename = SettlementBean.getStMergerCategory();
			// File file = new
			// File(userHome+"\\"+stnewDate+"\\"+stFilename+".zip");
			File file = new File(SettlementBean.getStPath() +File.separator +"REPORTS.zip");
			logger.info("path of zip file "+SettlementBean.getStPath() +File.separator +"REPORTS.zip");
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			//response.setContentType(context.getMimeType(stpath +"\\"+ "Reports.zip"));
			//response.setContentType("application/zip");
			logger.info("before downloading zip file ");
			response.setContentType("application/zip");
			logger.info("download completed");
			
			/** Set Response header */
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					file.getName());
			response.setHeader(headerKey, headerValue);

			/** Write response. */
			OutputStream outStream = response.getOutputStream();
			IOUtils.copy(inputstream, outStream);
			//isettelmentservice.DeleteFiles(stpath);
			response.flushBuffer();

			// DELETING FILES FROM DRIVE
			// isettelmentservice.DeleteFiles(userHome+"\\"+stnewDate);
			// isettelmentservice.DeleteFiles(SettlementBean.getStPath()+"\\"+stnewDate);
			java.util.Date varDate = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			try {
				varDate = dateFormat.parse(SettlementBean.getFileDate());
				dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				System.out.println("Date :" + dateFormat.format(varDate));
				SettlementBean.setFileDate(dateFormat.format(varDate));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			//isettelmentservice.DeleteFiles(stpath);

		} catch (Exception e) {
			logger.info("Exception in downloadReports " + e);
			System.out.println("Exception in downloadReports " + e);
			// return e.getMessage();
			redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
		}

	}
	
	@RequestMapping(value = "NfsAcqChargebackGlProcess.do", method = RequestMethod.POST)
	public String NfsAcqChargebackGlProcess(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ACQ_CHARGEBACK";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NfsAcqChargebackGlReports";
			} else {
				System.out.println("fdate: "+fdate);
				misReportService.NfsAcqChargebackGlReportProcess(fdate,openingBalance,finalEodBalance);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NfsAcqChargebackGlReports";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NfsAcqChargebackGlReports";
	}
	
	
	@RequestMapping(value = "downloadNfsAcqChargebackGl.do", method = RequestMethod.POST)
	public String downloadNfsAcqChargebackGl(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ACQ_CHARGEBACK";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NfsAcqChargebackGlReports";
			} else {
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NfsAcqChargebackGlReports";
					} else {
						
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NfsAcqChargebackGlReports";
	}
	
	@RequestMapping(value = "NfsAcqPrearbGlReportProcess.do", method = RequestMethod.POST)
	public String NFSACQPrearbGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ACQ_PREARB";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSACQPrearbGlReport";
			} else {
				misReportService.NfsAcqPrearbGlReportProcess(fdate,openingBalance,finalEodBalance);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSACQPrearbGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSACQPrearbGlReport";
	}
	
	@RequestMapping(value = "downloadNfsAcqPrearbGlReport.do", method = RequestMethod.POST)
	public String downloadNfsAcqPrearbGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ACQ_PREARB";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSACQPrearbGlReport";
			} else {
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSACQPrearbGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSACQPrearbGlReport";
	}
	
	@RequestMapping(value = "NfsAcqDebitGlReportProcess.do", method = RequestMethod.POST)
	public String NfsAcqDebitGlReportProcess(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ACQ_DR_ADJ";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSACQDebitGlReport";
			} else {
				misReportService.NfsAcqDebitGlReportProcess(fdate,openingBalance,finalEodBalance);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSACQDebitGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSACQDebitGlReport";
	}
	
	@RequestMapping(value = "downloadNfsAcqDebitGlReport.do", method = RequestMethod.POST)
	public String downloadNfsAcqDebitGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ACQ_DR_ADJ";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSACQDebitGlReport";
			} else {
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSACQDebitGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSACQDebitGlReport";
	}
	
	@RequestMapping(value = "NfsAcqCreditGlReportProcess.do", method = RequestMethod.POST)
	public String NfsAcqCreditGlReportProcess(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ACQ_CRD_ADJ";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSACQCreditGlReport";
			} else {
				misReportService.NfsAcqCreditGlReportProcess(fdate,openingBalance,finalEodBalance);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSACQCreditGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSACQCreditGlReport";
	}
	
	
	@RequestMapping(value = "downloadNfsAcqCreditGlReport.do", method = RequestMethod.POST)
	public String downloadNfsAcqCreditGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ACQ_CRD_ADJ";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSACQCreditGlReport";
			} else {
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSACQCreditGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSACQCreditGlReport";
	}
	
	
	
	@RequestMapping(value = "NfsIssChargebackGlReportProcess.do", method = RequestMethod.POST)
	public String NfsIssChargebackGlReportProcess(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ISS_CHARGEBACK";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSISSChargebackGlReport";
			} else {
				misReportService.NfsIssChargebackGlReportProcess(fdate,openingBalance,finalEodBalance);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSISSChargebackGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSISSChargebackGlReport";
	}
	
	@RequestMapping(value = "downloadNfsIssChargebackGlReport.do", method = RequestMethod.POST)
	public String downloadNfsIssChargebackGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ISS_CHARGEBACK";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSISSChargebackGlReport";
			} else {
				
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSISSChargebackGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSISSChargebackGlReport";
	}
	
	@RequestMapping(value = "NfsISSCreditGlReportProcess.do", method = RequestMethod.POST)
	public String NfsISSCreditGlReportProcess(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ISS_CRD_ADJ";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSISSCreditGlReport";
			} else {
				misReportService.NfsISSCreditGlReportProcess(fdate,openingBalance,finalEodBalance);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSISSCreditGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSISSCreditGlReport";
	}
	
	
	@RequestMapping(value = "downloadNfsISSCreditGlReport.do", method = RequestMethod.POST)
	public String downloadNfsISSCreditGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ISS_CRD_ADJ";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSISSCreditGlReport";
			} else {
				
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSISSCreditGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSISSCreditGlReport";
	}
	
	@RequestMapping(value = "NfsISSDebitGlReportProcess.do", method = RequestMethod.POST)
	public String NfsISSDebitGlReportProcess(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ISS_DR_ADJ";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSISSDebitGlReport";
			} else {
				misReportService.NfsISSDebitGlReportProcess(fdate,openingBalance,finalEodBalance);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSISSDebitGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSISSDebitGlReport";
	}
	
	@RequestMapping(value = "downloadNfsISSDebitGlReport.do", method = RequestMethod.POST)
	public String downloadNfsISSDebitGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ISS_DR_ADJ";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSISSDebitGlReport";
			} else {
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSISSDebitGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSISSDebitGlReport";
	}
	
	@RequestMapping(value = "NfsISSPrearbGlReportProcess.do", method = RequestMethod.POST)
	public String NfsISSPrearbGlReportProcess(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ISS_PREARB";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSISSPrearbGlReport";
			} else {
				misReportService.NfsISSPrearbGlReportProcess(fdate,openingBalance,finalEodBalance);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSISSPrearbGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSISSPrearbGlReport";
	}
	
	@RequestMapping(value = "downloadNfsISSPrearbGlReport.do", method = RequestMethod.POST)
	public String downloadNfsISSPrearbGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_ISS_PREARB";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSISSPrearbGlReport";
			} else {
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSISSPrearbGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSISSPrearbGlReport";
	}
	
	@RequestMapping(value = "RupayMirrorGlReportProcess.do", method = RequestMethod.POST)
	public String RupayMirrorGlReportProcess(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_MIRROR_RUPAY";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "RupayMirrorGlReport";
			} else {
				misReportService.RupayMirrorGlReportProcess(fdate,openingBalance,finalEodBalance);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "RupayMirrorGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "RupayMirrorGlReport";
	}
	
	@RequestMapping(value = "downloadRupayMirrorGlReport.do", method = RequestMethod.POST)
	public String downloadRupayMirrorGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_MIRROR_RUPAY";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "RupayMirrorGlReport";
			} else {
				misReportService.RupayMirrorGlReportProcess(fdate,openingBalance,finalEodBalance);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "RupayMirrorGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "RupayMirrorGlReport";
	}
	
	@RequestMapping(value = "NFSMirrorGlReportProcess.do", method = RequestMethod.POST)
	public String NFSMirrorGlReportProcess(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_NFS_MIRROR";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSMirrorGlReport";
			} else {
				misReportService.NFSMirrorGlReportProcess(fdate,openingBalance,finalEodBalance);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSMirrorGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSMirrorGlReport";
	}
	
	@RequestMapping(value = "downloadNFSMirrorGlReport.do", method = RequestMethod.POST)
	public String downloadNFSMirrorGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_NFS_MIRROR";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "NFSMirrorGlReport";
			} else {
				misReportService.NFSMirrorGlReportProcess(fdate,openingBalance,finalEodBalance);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "NFSMirrorGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "NFSMirrorGlReport";
	}
	
	@RequestMapping(value = "RupayMirrorIRGCSGlReportProcess.do", method = RequestMethod.POST)
	public String RupayMirrorIRGCSGlReportProcess(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_IRGCS_MIRROR_RUPAY";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "RupayIRGCSMirrorGlReport";
			} else {
				misReportService.RupayMirrorIRGCSGlReportProcess(fdate,openingBalance,finalEodBalance);
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "RupayIRGCSMirrorGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "RupayIRGCSMirrorGlReport";
	}
	
	@RequestMapping(value = "downloadRupayIRGCSMirrorGlReport.do", method = RequestMethod.POST)
	public String downloadRupayIRGCSMirrorGlReport(@RequestParam String fdate, Model model, HttpServletResponse response,
			@RequestParam("openingBalance") String openingBalance,
			@RequestParam("finalEodBalance") String finalEodBalance) {
		System.out.println("openingBalance: "+openingBalance);
		System.out.println("finalEodBalance: "+finalEodBalance);
		ServletOutputStream sou = null;
		String fileName = null;
		String[] split = fdate.split("-");
		String tableName ="GL_IRGCS_MIRROR_RUPAY";
		String accountNumber="";
		try {
			if (fdate == null || fdate.trim().isEmpty()) {
				return "RupayIRGCSMirrorGlReport";
			} else {
				try {
					if (fdate == null || fdate.trim().isEmpty()) {
						return "RupayIRGCSMirrorGlReport";
					} else {
						fileName = misReportService.downloadGlReport(fdate,tableName,accountNumber);
						model.addAttribute("message", "DONE");
						response.setContentType("application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
						FileInputStream ins = new FileInputStream(new File(fileName));
						sou = response.getOutputStream();
						sou.write(IOUtils.toByteArray(ins));
						ins.close();
						sou.flush();
						sou.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						new File(fileName).delete();
						sou.flush();
						sou.close();
						System.gc();
					} catch (Exception e2) {
					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "RupayIRGCSMirrorGlReport";
	}
}
