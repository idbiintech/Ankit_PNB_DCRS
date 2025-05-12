package com.recon.control;

import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.IReconProcessService;
import com.recon.service.ISourceService;
import com.recon.service.NFSSettlementService;
import com.recon.service.RupayTTUMService;
import com.recon.service.SettlementTTUMService;
import com.recon.util.CSRFToken;
import com.recon.util.FileDetailsJson;
import com.recon.util.GenerateUCOTTUM;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SettlementTTUMController {
	private static final Logger logger = Logger.getLogger(com.recon.control.SettlementTTUMController.class);

	public static final String OUTPUT_FOLDER = String.valueOf(System.getProperty("catalina.home")) + File.separator
			+ "TTUM";

	@Autowired
	SettlementTTUMService SETTLTTUMSERVICE;

	@Autowired
	RupayTTUMService rupayTTUMService;

	@Autowired
	NFSSettlementService nfsSettlementService;

	@Autowired
	IReconProcessService reconProcess;

	@Autowired
	ISourceService iSourceService;

	@RequestMapping(value = { "ICDSettlementTTUM" }, method = { RequestMethod.GET })
	public ModelAndView ICDSettlementTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** ICDSettlementTTUM.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		String display = "";
		logger.info("in GetHeaderList" + category);
		List<String> subcat = this.iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("CARDTOCARD")
				|| category.equals("WCC"))
			display = "none";
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
		modelAndView.setViewName("ICDSettlementTTUM");
		logger.info("***** NFSSettlementController.nfsFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "NFSSettlementTTUM" }, method = { RequestMethod.GET })
	public ModelAndView NFSSettlementTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** nfsFileUpload.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("nfsFileUpload GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		List<String> subcat = this.iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("CARDTOCARD")
				|| category.equals("WCC"))
			display = "none";
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
		modelAndView.setViewName("NFSSettlementTTUM");
		logger.info("***** NFSSettlementController.nfsFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "JCBSettlementTTUM" }, method = { RequestMethod.GET })
	public ModelAndView JCBSettlementTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** nfsFileUpload.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("nfsFileUpload GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		List<String> subcat = this.iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("CARDTOCARD")
				|| category.equals("WCC"))
			display = "none";
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
		modelAndView.setViewName("JCBSettlementTTUM");
		logger.info("***** NFSSettlementController.nfsFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "DFSSettlementTTUM" }, method = { RequestMethod.GET })
	public ModelAndView DFSSettlementTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** nfsFileUpload.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("nfsFileUpload GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		List<String> subcat = this.iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("CARDTOCARD")
				|| category.equals("WCC"))
			display = "none";
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
		modelAndView.setViewName("DFSSettlementTTUM");
		logger.info("***** NFSSettlementController.nfsFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "NFSMonthlySettValidation" }, method = { RequestMethod.POST })
	@ResponseBody
	public String NFSMonthlyValidation(String fileDate, String stSubCategory, String timePeriod, String cycle,
			String filename, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes,
			Model model) throws Exception {
		logger.info("***** NFSSettlementProcess.Post Start ****");
		String lastDate = fileDate;
		logger.info("NFSMonthlyValidation POST");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("filename is " + filename);
		nfsSettlementBean.setFileName(filename);
		nfsSettlementBean.setStSubCategory(stSubCategory);
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		if (timePeriod != null && timePeriod.equalsIgnoreCase("Monthly")) {
			if (stSubCategory.equalsIgnoreCase("ACQUIRER")) {
				logger.info("File date is " + fileDate);
				fileDate = "01/" + fileDate;
				logger.info("Filedate is " + fileDate);
				nfsSettlementBean.setDatepicker(fileDate);
				nfsSettlementBean.setStSubCategory(stSubCategory);
				nfsSettlementBean.setCategory("MONTHLY_SETTLEMENT");
				HashMap<String, Object> hashMap = this.SETTLTTUMSERVICE.validateMonthlySettlement(nfsSettlementBean);
				if (hashMap != null && ((Boolean) hashMap.get("result")).booleanValue())
					return "success";
				if (hashMap == null)
					return "Error";
				if (!hashMap.get("msg").toString().equalsIgnoreCase("")) {
					System.out.println(hashMap.get("msg").toString());
					return hashMap.get("msg").toString();
				}
				return "Problem Occurred!";
			}
			return "Development is in Progress!!!";
		}
		logger.info("File date is " + fileDate);
		nfsSettlementBean.setDatepicker(fileDate);
		nfsSettlementBean.setStSubCategory(stSubCategory);
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
		HashMap<String, Object> result = this.SETTLTTUMSERVICE.validateDailyInterchange(nfsSettlementBean);
		if (result != null && ((Boolean) result.get("result")).booleanValue())
			return "success";
		if (result == null)
			return "Error";
		if (!result.get("msg").toString().equalsIgnoreCase("")) {
			System.out.println(result.get("msg").toString());
			return result.get("msg").toString();
		}
		return "Problem Occurred!";
	}

	@RequestMapping(value = { "DownloadSettlementTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadSettlementTTUM(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpServletResponse response, HttpSession httpSession,
			RedirectAttributes redirectAttributes, Model model) throws Exception {
		logger.info("***** DownloadSettlementTTUM.POST Start ****");
		logger.info("NFSSettlement POST");
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
		List<Object> Excel_data = new ArrayList();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		if (nfsSettlementBean.getTimePeriod().equalsIgnoreCase("MONTHLY")) {
			nfsSettlementBean.setCategory("MONTHLY_SETTLEMENT");
			if (nfsSettlementBean.getDatepicker().contains(","))
				;
			nfsSettlementBean.setDatepicker(nfsSettlementBean.getDatepicker().replace(",", ""));
			nfsSettlementBean.setDatepicker("01/" + nfsSettlementBean.getDatepicker());
			boolean executed = this.SETTLTTUMSERVICE.runNFSMonthlyTTUM(nfsSettlementBean);
			if (executed)
				Excel_data = this.SETTLTTUMSERVICE.getMonthlyTTUMData(nfsSettlementBean);
			String fileName = "NFS_MONTHLY_TTUM.txt";
			String stPath = System.getProperty("java.io.tmpdir");
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker(), "NFS");
			obj.generateMultipleTTUMFiles(stPath, fileName, 2, Excel_data);
			logger.info("File is created");
			File file = new File(String.valueOf(stPath) + File.separator + fileName);
			logger.info("path of zip file " + stPath + File.separator + fileName);
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			logger.info("before downloading zip file ");
			response.setContentType("application/txt");
			logger.info("download completed");
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
			response.setHeader(headerKey, headerValue);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
			response.flushBuffer();
		} else {
			if (nfsSettlementBean.getDatepicker().contains(","))
				;
			nfsSettlementBean.setDatepicker(nfsSettlementBean.getDatepicker().replace(",", ""));
			boolean CheckProcess = this.SETTLTTUMSERVICE.checkDailyInterchangeTTUMProcess(nfsSettlementBean);
			if (!CheckProcess)
				this.SETTLTTUMSERVICE.runDailyInterchangeTTUM(nfsSettlementBean);
			Excel_data = this.SETTLTTUMSERVICE.getDailyInterchangeTTUMData(nfsSettlementBean);
			String fileName = "NFS_Daily_TTUM_" + nfsSettlementBean.getCycle() + ".txt";
			String stPath = System.getProperty("java.io.tmpdir");
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker(), "NFS");
			obj.generateTTUMFile(stPath, fileName, Excel_data);
			logger.info("File is created");
			File file = new File(String.valueOf(stPath) + File.separator + fileName);
			logger.info("path of zip file " + stPath + File.separator + fileName);
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			logger.info("before downloading zip file ");
			response.setContentType("application/txt");
			logger.info("download completed");
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
			response.setHeader(headerKey, headerValue);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
			response.flushBuffer();
		}
	}

	@RequestMapping(value = { "NFSSettVoucher" }, method = { RequestMethod.GET })
	public ModelAndView NFSSettlementVoucher(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NFSSettVoucher.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NFSSettVoucher GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
		modelAndView.setViewName("GenerateNFSSettVoucher");
		logger.info("***** NFSSettlementController.NFSSettlement GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "NFSSettVoucherValidation" }, method = { RequestMethod.POST })
	@ResponseBody
	public String NFSSettVoucherValidation(String fileDate, String stSubCategory, String timePeriod, String cycle,
			String filename, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes,
			Model model) throws Exception {
		HashMap<String, Object> result;
		logger.info("***** NFSSettVoucherValidation.Post Start ****");
		String lastDate = fileDate;
		logger.info("NFSSettVoucherValidation POST");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
		logger.info("filename is " + filename);
		nfsSettlementBean.setFileName(filename);
		nfsSettlementBean.setStSubCategory(stSubCategory);
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		nfsSettlementBean.setCycle(Integer.parseInt(cycle));
		logger.info("cycle is " + cycle);
		nfsSettlementBean.setDatepicker(fileDate);
		if (nfsSettlementBean.getFileName().contains("PBGB")) {
			result = this.nfsSettlementService.CheckSettlementProcess(nfsSettlementBean);
		} else {
			result = this.nfsSettlementService.ValidateForSettVoucher(nfsSettlementBean);
		}
		if (result != null && ((Boolean) result.get("result")).booleanValue())
			return "success";
		if (result == null)
			return "Error";
		if (!result.get("msg").toString().equalsIgnoreCase("")) {
			System.out.println(result.get("msg").toString());
			return result.get("msg").toString();
		}
		return "Problem Occurred!";
	}

	@RequestMapping(value = { "DownloadSettVoucher" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadSettVoucher(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpServletResponse response, HttpSession httpSession,
			RedirectAttributes redirectAttributes, Model model) throws Exception {
		logger.info("***** DownloadSettlementreport.POST Start ****");
		logger.info("NFSSettlement POST");
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
		if (!nfsSettlementBean.getFileName().equalsIgnoreCase("NTSL-NFS"))
			nfsSettlementBean.setCycle(1);
		List<Object> Excel_data = new ArrayList();
		List<Object> TTUMData = new ArrayList();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		logger.info("File Name is " + nfsSettlementBean.getFileName());
		boolean executed = false;
		if (nfsSettlementBean.getDatepicker().contains(","))
			;
		nfsSettlementBean.setDatepicker(nfsSettlementBean.getDatepicker().replace(",", ""));
		boolean checkProcFlag = this.nfsSettlementService.checkSettVoucherProcess(nfsSettlementBean);
		executed = this.SETTLTTUMSERVICE.runSettlementVoucher(nfsSettlementBean);
		String fileName = "SETTLEMENT_VOUCHER_" + nfsSettlementBean.getCycle() + ".txt";
		String stPath = System.getProperty("java.io.tmpdir");
		logger.info("TEMP_DIR" + stPath);
		GenerateUCOTTUM obj = new GenerateUCOTTUM();
		stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker(), nfsSettlementBean.getCategory());
		obj.generateTTUMFile(stPath, fileName, TTUMData);
		logger.info("File is created");
		List<String> Column_list = new ArrayList<>();
		Column_list.add("ACCOUNT_NUMBER");
		Column_list.add("PART_TRAN_TYPE");
		Column_list.add("TRANSACTION_AMOUNT");
		Column_list.add("TRANSACTION_PARTICULAR");
		Excel_data.add(Column_list);
		List<Object> SettData = new ArrayList();
		SettData.add(TTUMData);
		Excel_data.add(SettData);
		fileName = "SETTLEMENT_VOUCHER_" + nfsSettlementBean.getCycle() + ".xls";
		String zipName = "SETTLEMENT_VOUCHER_" + nfsSettlementBean.getCycle() + ".zip";
		obj.generateExcelTTUM(stPath, fileName, Excel_data, "REFUND", zipName);
		logger.info("File is created");
		File file = new File(String.valueOf(stPath) + File.separator + zipName);
		logger.info("path of zip file " + stPath + File.separator + zipName);
		FileInputStream inputstream = new FileInputStream(file);
		response.setContentLength((int) file.length());
		logger.info("before downloading zip file ");
		response.setContentType("application/txt");
		logger.info("download completed");
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
		response.setHeader(headerKey, headerValue);
		ServletOutputStream servletOutputStream = response.getOutputStream();
		IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
		response.flushBuffer();
	}

	@RequestMapping(value = { "NFSAdjustmentTTUM" }, method = { RequestMethod.GET })
	public ModelAndView NFSAdjustmentTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NFSSettlement.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NFSSettlement GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
		modelAndView.setViewName("GenerateNFSAdjustmentTTUM");
		logger.info("***** NFSSettlementController.NFSSettlement GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "ICCWAdjustmentTTUM" }, method = { RequestMethod.GET })
	public ModelAndView ICCWAdjustmentTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NFSSettlement.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NFSSettlement GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
		modelAndView.setViewName("ICCWAdjustmentTTUM");
		logger.info("***** NFSSettlementController.NFSSettlement GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "ICDAdjustmentTTUM" }, method = { RequestMethod.GET })
	public ModelAndView ICDAdjustmentTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** NFSSettlement.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NFSSettlement GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
		modelAndView.setViewName("GenerateICDAdjustmentTTUM");
		logger.info("***** NFSSettlementController.NFSSettlement GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "NFSJCBDFSSETTL" }, method = { RequestMethod.POST })
	@ResponseBody
	public String NFSJCBDFSSETTL(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model)
			throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** NFSAdjTTUMValidation.Post Start ****" + nfsSettlementBean.getCategory());
		logger.info("ADjtype is " + nfsSettlementBean.getAdjType() + " category " + nfsSettlementBean.getAdjCategory()
				+ "cycle " + nfsSettlementBean.getCycle());
		if (nfsSettlementBean.getCategory().contains("JCB")) {
			HashMap<String, Object> result = this.nfsSettlementService.ValidateForAdjTTUMSETTL(nfsSettlementBean);
			if (result != null && ((Boolean) result.get("result")).booleanValue()) {
				output2 = this.SETTLTTUMSERVICE.AdjTTUMProcSETTLJCB(nfsSettlementBean);
			} else {
				return result.get("msg").toString();
			}
			if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
				return "Processing Completed Successfully! \n Please download Report";
			return output2.get("msg").toString();
		}
		if (nfsSettlementBean.getCategory().contains("DFS")) {
			HashMap<String, Object> result = this.nfsSettlementService.ValidateForAdjTTUMSETTL(nfsSettlementBean);
			if (result != null && ((Boolean) result.get("result")).booleanValue()) {
				output2 = this.SETTLTTUMSERVICE.AdjTTUMProcSETTLDFS(nfsSettlementBean);
			} else {
				return result.get("msg").toString();
			}
			if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
				return "Processing Completed Successfully! \n Please download Report";
			return output2.get("msg").toString();
		}
		if (nfsSettlementBean.getAdjCategory().contains("REPORT_SETTL")
				|| nfsSettlementBean.getAdjCategory().contains("REPORT_STTL")) {
			HashMap<String, Object> result = this.nfsSettlementService.ValidateForAdjTTUMSETTL(nfsSettlementBean);
			if (result != null && ((Boolean) result.get("result")).booleanValue()) {
				if (nfsSettlementBean.getAdjCategory().contains("ICD")) {
					output2 = this.SETTLTTUMSERVICE.AdjTTUMProcSETTLICD(nfsSettlementBean);
				} else if (nfsSettlementBean.getAdjCategory().contains("JCB")) {
					output2 = this.SETTLTTUMSERVICE.AdjTTUMProcSETTLJCB(nfsSettlementBean);
				} else if (nfsSettlementBean.getAdjCategory().contains("DFS")) {
					output2 = this.SETTLTTUMSERVICE.AdjTTUMProcSETTLDFS(nfsSettlementBean);
				} else {
					output2 = this.SETTLTTUMSERVICE.runAdjTTUMSETTL(nfsSettlementBean);
				}
			} else {
				return result.get("msg").toString();
			}
			if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
				return "Processing Completed Successfully! \n Please download Report";
			return output2.get("msg").toString();
		}
		HashMap<String, Object> result2 = this.nfsSettlementService.ValidateForAdjTTUMSETTL(nfsSettlementBean);
		if (result2 != null && ((Boolean) result2.get("result")).booleanValue()) {
			if (nfsSettlementBean.getAdjCategory().contains("ICD")) {
				output2 = this.SETTLTTUMSERVICE.AdjTTUMProcSETTLnewICD(nfsSettlementBean);
			} else {
				output2 = this.SETTLTTUMSERVICE.runAdjTTUMSETTL(nfsSettlementBean);
			}
		} else {
			return result2.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
			return "Processing Completed Successfully! \n Please download Report";
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "ICDAdjustmentProcessSETTL" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ICDAdjustmentProcessSETTL(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model)
			throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** ICDAdjustmentProcessSETTL.Post Start ****");
		logger.info("ADjtype is " + nfsSettlementBean.getAdjType() + " category " + nfsSettlementBean.getAdjCategory());
		nfsSettlementBean.setCategory("NFS_ADJUSTMENT");
		if (nfsSettlementBean.getAdjCategory().contains("REPORT_SETTL")
				|| nfsSettlementBean.getAdjCategory().contains("REPORT_STTL")) {
			HashMap<String, Object> result = this.nfsSettlementService.ValidateForAdjTTUMSETTL(nfsSettlementBean);
			if (result != null && ((Boolean) result.get("result")).booleanValue()) {
				output2 = this.SETTLTTUMSERVICE.runAdjTTUMSETTL(nfsSettlementBean);
			} else {
				return result.get("msg").toString();
			}
			if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
				return "Processing Completed Successfully! \n Please download Report";
			return output2.get("msg").toString();
		}
		HashMap<String, Object> result2 = this.nfsSettlementService.ValidateForAdjTTUMTEXTSETTL(nfsSettlementBean);
		if (result2 != null && ((Boolean) result2.get("result")).booleanValue()) {
			output2 = this.SETTLTTUMSERVICE.runAdjTTUMSETTLnew(nfsSettlementBean);
		} else {
			return result2.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
			return "Processing Completed Successfully! \n Please download Report";
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "NFSAdjustmentProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String NFSAdjTTUMValidation(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model)
			throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** NFSAdjTTUMValidation.Post Start ****" + nfsSettlementBean.getCategory()+ " "+ nfsSettlementBean.getFileName());
		logger.info("ADjtype is " + nfsSettlementBean.getAdjType() + " category " + nfsSettlementBean.getAdjCategory());
		if (nfsSettlementBean.getAdjType().contains("ACUIRER PENALTY")) {
			HashMap<String, Object> result = this.nfsSettlementService.ValidateForAdjPENALTYTTUM(nfsSettlementBean);
			if (result != null && ((Boolean) result.get("result")).booleanValue()) {
				output2 = this.SETTLTTUMSERVICE.ANKITACQPRNALTITTUM(nfsSettlementBean);
			} else {
				return result.get("msg").toString();
			}
			if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
				return output2.get("msg").toString();
			return output2.get("msg").toString();
		}

		if (nfsSettlementBean.getAdjCategory().contains("REPORT")) {

			HashMap<String, Object> result = this.nfsSettlementService.ValidateForAdjTTUM(nfsSettlementBean);
			if (result != null && ((Boolean) result.get("result")).booleanValue()) {
				if (nfsSettlementBean.getFileName().contains("ICCW")) {

					output2 = this.SETTLTTUMSERVICE.ANKITTREPORTSnewICCW(nfsSettlementBean);
				} else {
					output2 = this.SETTLTTUMSERVICE.ANKITTREPORTSnew(nfsSettlementBean);
				}

			} else {
				return result.get("msg").toString();
			}
			if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
				return output2.get("msg").toString();
			return output2.get("msg").toString();
		}
		HashMap<String, Object> result2 = this.nfsSettlementService.ValidateForAdjTTUM(nfsSettlementBean);
		if (result2 != null && ((Boolean) result2.get("result")).booleanValue()) {
			if (nfsSettlementBean.getCategory().contains("ICCW")) {

				output2 = this.SETTLTTUMSERVICE.ANKITTREPORTSnewICCW(nfsSettlementBean);
			} else {
				output2 = this.SETTLTTUMSERVICE.ANKITTREPORTSnew(nfsSettlementBean);
			}
		} else {
			return result2.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
			return output2.get("msg").toString();
		return output2.get("msg").toString();
	}
	@RequestMapping(value = { "VISAAdjustmentProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String VISAAdjustmentProcess(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model)
			throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** VISAAdjustmentProcess.Post Start ****" + nfsSettlementBean.getCategory()+ " "+ nfsSettlementBean.getFileName());
		logger.info("ADjtype is " + nfsSettlementBean.getAdjType() + " category " + nfsSettlementBean.getAdjCategory());


		if (nfsSettlementBean.getAdjCategory().contains("REPORT")) {

			HashMap<String, Object> result = this.nfsSettlementService.ValidateForAdjTTUMVISA(nfsSettlementBean);
			if (result != null && ((Boolean) result.get("result")).booleanValue()) {
				if (nfsSettlementBean.getFileName().contains("VISA")) {

					output2 = this.SETTLTTUMSERVICE.ANKITTREPORTSnewVISA(nfsSettlementBean);
					
					
				} else {
					output2 = this.SETTLTTUMSERVICE.ANKITTREPORTSnew(nfsSettlementBean);
				}

			} else {
				return result.get("msg").toString();
			}
			if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
				return output2.get("msg").toString();
			return output2.get("msg").toString();
		}
		HashMap<String, Object> result2 = this.nfsSettlementService.ValidateForAdjTTUM(nfsSettlementBean);
		if (result2 != null && ((Boolean) result2.get("result")).booleanValue()) {
			if (nfsSettlementBean.getCategory().contains("ICCW")) {

				output2 = this.SETTLTTUMSERVICE.ANKITTREPORTSnewICCW(nfsSettlementBean);
			} else {
				output2 = this.SETTLTTUMSERVICE.ANKITTREPORTSnew(nfsSettlementBean);
			}
		} else {
			return result2.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
			return output2.get("msg").toString();
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "ICCWAdjustmentProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ICCWAdjustmentProcess(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model)
			throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** NFSAdjTTUMValidation.Post Start ****" + nfsSettlementBean.getCategory());
		logger.info("ADjtype is " + nfsSettlementBean.getAdjType() + " category " + nfsSettlementBean.getAdjCategory());
		if (nfsSettlementBean.getAdjType().contains("ACUIRER PENALTY")) {
			HashMap<String, Object> result = this.nfsSettlementService.ValidateForAdjPENALTYTTUM(nfsSettlementBean);
			if (result != null && ((Boolean) result.get("result")).booleanValue()) {
				output2 = this.SETTLTTUMSERVICE.ANKITACQPRNALTITTUM(nfsSettlementBean);
			} else {
				return result.get("msg").toString();
			}
			if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
				return output2.get("msg").toString();
			return output2.get("msg").toString();
		}
		if (nfsSettlementBean.getAdjCategory().contains("REPORT")) {
			HashMap<String, Object> result = this.nfsSettlementService.ValidateForAdjTTUM(nfsSettlementBean);
			if (result != null && ((Boolean) result.get("result")).booleanValue()) {
				output2 = this.SETTLTTUMSERVICE.ANKITTREPORTSnew(nfsSettlementBean);
			} else {
				return result.get("msg").toString();
			}
			if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
				return output2.get("msg").toString();
			return output2.get("msg").toString();
		}
		HashMap<String, Object> result2 = this.nfsSettlementService.ValidateForAdjTTUM(nfsSettlementBean);
		if (result2 != null && ((Boolean) result2.get("result")).booleanValue()) {
			output2 = this.SETTLTTUMSERVICE.ANKITTREPORTSnew(nfsSettlementBean);
		} else {
			return result2.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
			return output2.get("msg").toString();
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "ValidateDownloadAdjTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ValidateDownloadAdjTTUM(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model)
			throws Exception {
		logger.info("***** DownloadSettlementreport.POST Start ****");
		logger.info("DownloadAdjTTUM POST category " + nfsSettlementBean.getAdjCategory());
		nfsSettlementBean.setCategory("NFS");
		List<Object> Excel_data = new ArrayList();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		logger.info("File Name is " + nfsSettlementBean.getFileName());
		boolean executed = false;
		return "success";
	}

	@RequestMapping(value = { "ICDAdjustmentProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ICDAdjustmentProcess(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model)
			throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** ICDAdjustmentProcess.Post Start ****");
		logger.info("ADjtype is " + nfsSettlementBean.getAdjType() + " category " + nfsSettlementBean.getAdjCategory());
		nfsSettlementBean.setCategory("NFS_ADJUSTMENT");
		if (nfsSettlementBean.getAdjCategory().contains("REPORT")) {
			HashMap<String, Object> result = this.nfsSettlementService.ValidateForAdjTTUMICD(nfsSettlementBean);
			if (result != null && ((Boolean) result.get("result")).booleanValue()) {
				output2 = this.SETTLTTUMSERVICE.ANKITREPORSnewICD(nfsSettlementBean);
			} else {
				return result.get("msg").toString();
			}
			if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
				return output2.get("msg").toString();
			return output2.get("msg").toString();
		}
		HashMap<String, Object> result2 = this.nfsSettlementService.ValidateForAdjTTUMTEXTICD(nfsSettlementBean);
		if (result2 != null && ((Boolean) result2.get("result")).booleanValue()) {
			output2 = this.SETTLTTUMSERVICE.ANKITTTUMnewICD(nfsSettlementBean);
		} else {
			return result2.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
			return output2.get("msg").toString();
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "ValidateDownloadAdjTTUMICD" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ValidateDownloadAdjTTUMICD(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model)
			throws Exception {
		logger.info("***** ValidateDownloadAdjTTUMICD.POST Start ****");
		logger.info("DownloadAdjTTUM POST category " + nfsSettlementBean.getAdjCategory());
		List<Object> Excel_data = new ArrayList();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		logger.info("File Name is " + nfsSettlementBean.getFileName());
		boolean executed = false;
		if (nfsSettlementBean.getAdjCategory().contains("REPORT")) {
			boolean bool = this.nfsSettlementService.checkAdjTTUMProcessREPORTICD(nfsSettlementBean);
			if (bool)
				return "success";
			return "Adjustment TTUM REPORT is not processed.\n Please process TTUM REPORT ";
		}
		boolean checkProcFlag = this.nfsSettlementService.checkAdjTTUMProcessICD(nfsSettlementBean);
		if (checkProcFlag)
			return "success";
		return "Adjustment TTUM is not processed.\n Please process TTUM";
	}

	@RequestMapping(value = { "DownloadAdjTTUMICD" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadAdjTTUMICD(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpServletResponse response, HttpSession httpSession,
			RedirectAttributes redirectAttributes, Model model) throws Exception {
		logger.info("***** DownloadSettlementreport.POST Start ****");
		String fileName = "";
		String zipName = "";
		logger.info("DownloadAdjTTUMICD POST");
		if (nfsSettlementBean.getAdjCategory().contains("REPORT")) {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is " + Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			logger.info("File Name is " + nfsSettlementBean.getFileName());
			boolean executed = false;
			TTUMData = this.SETTLTTUMSERVICE.getAdjTTUMICD(nfsSettlementBean);
			System.out.println("nfsSettlementBean.getStSubCategory()" + nfsSettlementBean.getStSubCategory());
			fileName = "ICD_ADJUSTMENT_" + nfsSettlementBean.getAdjType() + "_TTUM_" + nfsSettlementBean.getDatepicker()
					+ ".txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker(), nfsSettlementBean.getCategory());
			obj.generateADJTTUMFiles(stPath, fileName, 1, TTUMData2, "NFS", nfsSettlementBean);
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			Column_list.add("SR_NO");
			Column_list.add("DR/CR");
			Column_list.add("ACQ_BANK");
			Column_list.add("TXNDATE");
			Column_list.add("TXNTIME");
			Column_list.add("RRN");
			Column_list.add("CARD_NO");
			Column_list.add("ADJAMOUNT");
			Column_list.add("ACCOUNTNO");
			Column_list.add("NARRATION");
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			System.out.println("filename in nfs ttum is " + fileName);
			fileName = "ICD_ADJUSTMENT_" + nfsSettlementBean.getAdjType() + "_TTUM_" + nfsSettlementBean.getDatepicker()
					+ ".xls";
			zipName = "ICD_ADJUSTMENT_" + nfsSettlementBean.getAdjType() + "_TTUM_" + nfsSettlementBean.getDatepicker()
					+ ".zip";

			obj.generateExcelTTUM(stPath, fileName, Excel_data, "ICDADJUSTMENT"+nfsSettlementBean.getAdjType()+"_" + nfsSettlementBean.getDatepicker(),
					zipName);
			logger.info("File is created");
			File file = new File(String.valueOf(stPath) + File.separator + fileName);
			logger.info("path of zip file " + stPath + File.separator + fileName);
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			logger.info("before downloading zip file ");
			response.setContentType("application/txt");
			logger.info("download completed");
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
			response.setHeader(headerKey, headerValue);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
			response.flushBuffer();
		} else {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is " + Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			logger.info("File Name is " + nfsSettlementBean.getFileName());
			boolean executed = false;
			TTUMData = this.SETTLTTUMSERVICE.getDailyNFSTTUMDataICD(nfsSettlementBean);
			System.out.println("nfsSettlementBean.getStSubCategory()" + nfsSettlementBean.getStSubCategory());
			if (nfsSettlementBean.getAdjType().equalsIgnoreCase("PRE-ARBITRATION DECLINED")) {
				fileName = "ICDPARTIALPREARBITRATIONACCEPTANCETTUM"
						+ nfsSettlementBean.getDatepicker().replaceAll("-", "") + "_val.txt";
			} else if (nfsSettlementBean.getAdjType().equalsIgnoreCase("RE-PRESENTMENT RAISE")) {
				fileName = "ICDPARTIALCHARGEBACKACCEPTANCETTUM" + nfsSettlementBean.getDatepicker().replaceAll("-", "")
						+ "_val.txt";
			} else {
				fileName = "ICD" + nfsSettlementBean.getAdjType().replaceAll("\\s", "").replaceAll("-", "") + "TTUM"
						+ nfsSettlementBean.getDatepicker().replaceAll("-", "") + "_val.txt";
			}
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker(), nfsSettlementBean.getCategory());
			obj.generateADJTTUMFiles(stPath, fileName, 1, TTUMData, "NFS", nfsSettlementBean);
			logger.info("File is created");
			logger.info("File is created");
			if (nfsSettlementBean.getAdjType().equalsIgnoreCase("PRE-ARBITRATION DECLINED")) {
				zipName = "ICDPARTIALPREARBITRATIONACCEPTANCETTUM"
						+ nfsSettlementBean.getDatepicker().replaceAll("-", "") + "_val.txt";
			} else if (nfsSettlementBean.getAdjType().equalsIgnoreCase("RE-PRESENTMENT RAISE")) {
				zipName = "ICDPARTIALCHARGEBACKACCEPTANCETTUM" + nfsSettlementBean.getDatepicker().replaceAll("-", "")
						+ "_val.txt";
			} else {
				zipName = "ICD" + nfsSettlementBean.getAdjType().replaceAll("\\s", "").replaceAll("-", "") + "TTUM"
						+ nfsSettlementBean.getDatepicker().replaceAll("-", "") + "_val.txt";
			}
			File file = new File(String.valueOf(stPath) + File.separator + zipName);
			logger.info("path of zip file " + stPath + File.separator + zipName);
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			logger.info("before downloading zip file ");
			response.setContentType("application/txt");
			logger.info("download completed");
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
			response.setHeader(headerKey, headerValue);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
			response.flushBuffer();
		}
	}

	@RequestMapping(value = { "ValidateDownloadAdjTTUMSETTLICD" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ValidateDownloadAdjTTUMSETTLICD(
			@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean, HttpServletRequest request,
			HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) throws Exception {
		logger.info("***** ValidateDownloadAdjTTUMSETTLICD.POST Start ****");
		logger.info("ValidateDownloadAdjTTUMSETTLICD POST category " + nfsSettlementBean.getAdjCategory());
		List<Object> Excel_data = new ArrayList();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		boolean executed = false;
		if (nfsSettlementBean.getAdjCategory().contains("REPORT_SETTL")) {
			boolean bool = this.nfsSettlementService.checkAdjTTUMProcessREPORTSETTL(nfsSettlementBean);
			if (bool)
				return "success";
			return "Adjustment TTUM REPORT is not processed.\n Please process TTUM REPORT ";
		}
		boolean checkProcFlag = this.nfsSettlementService.checkAdjTTUMProcessSETTL(nfsSettlementBean);
		if (checkProcFlag)
			return "success";
		return "Adjustment TTUM is not processed.\n Please process TTUM";
	}

	@RequestMapping(value = { "ValidateDownloadAdjTTUMSETTL" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ValidateDownloadAdjTTUMSETTL(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model)
			throws Exception {
		logger.info("***** ValidateDownloadAdjTTUMSETTLICD.POST Start ****" + nfsSettlementBean.getCategory());
		logger.info("ValidateDownloadAdjTTUMSETTLICD POST category " + nfsSettlementBean.getAdjCategory());
		List<Object> Excel_data = new ArrayList();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		boolean executed = false;
		return "success";
	}

	@RequestMapping(value = { "rollbackTTUMReport" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMReport(@RequestParam("filedate") String filedate, @RequestParam("adjType") String adjType,
			@RequestParam("adjCategory") String adjCategory) throws ParseException, Exception {
		NFSSettlementBean beandata = new NFSSettlementBean();
		beandata.setAdjCategory(adjType);
		beandata.setAdjType(adjType);
		beandata.setDatepicker(filedate);
		boolean result = false;
		logger.info("rollbackTTUMReport " + adjType + " " + filedate + " " + adjCategory);
		if (adjCategory.contains("REPORT")) {
			
			result = this.SETTLTTUMSERVICE.rollBackNFSADJ(beandata);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}
		result = this.SETTLTTUMSERVICE.rollBackNFSADJ(beandata);
		if (result)
			return "SUCCESSFULLY ROLLBACK";
		return "ALREADY ROLLBACK";
	}
	@RequestMapping(value = { "rollbackTTUMReportICCW" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMReportICCW(@RequestParam("filedate") String filedate, @RequestParam("adjType") String adjType,
			@RequestParam("adjCategory") String adjCategory) throws ParseException, Exception {
		NFSSettlementBean beandata = new NFSSettlementBean();
		beandata.setAdjCategory(adjType);
		beandata.setAdjType(adjType);
		beandata.setDatepicker(filedate);
		boolean result = false;
		logger.info("rollbackTTUMReport " + adjType + " " + filedate + " " + adjCategory);
		if (adjCategory.contains("REPORT")) {
			
			result = this.SETTLTTUMSERVICE.rollBackNFSADJICCW(beandata);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}
		result = this.SETTLTTUMSERVICE.rollBackNFSADJICCW(beandata);
		if (result)
			return "SUCCESSFULLY ROLLBACK";
		return "ALREADY ROLLBACK";
	}
	@RequestMapping(value = { "rollbackTTUMReportVISA" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMReportVISA(@RequestParam("filedate") String filedate, @RequestParam("adjType") String adjType,
			@RequestParam("adjCategory") String adjCategory) throws ParseException, Exception {
		NFSSettlementBean beandata = new NFSSettlementBean();
		beandata.setAdjCategory(adjType);
		beandata.setAdjType(adjType);
		beandata.setDatepicker(filedate);
		boolean result = false;
		logger.info("rollbackTTUMReport " + adjType + " " + filedate + " " + adjCategory);
		if (adjCategory.contains("REPORT")) {
			
			result = this.SETTLTTUMSERVICE.rollBackNFSADJVISA(beandata);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}
		result = this.SETTLTTUMSERVICE.rollBackNFSADJVISA(beandata);
		if (result)
			return "SUCCESSFULLY ROLLBACK";
		return "ALREADY ROLLBACK";
	}
	@RequestMapping(value = { "rollbackTTUMReportICD" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMReportICD(@RequestParam("filedate") String filedate,
			@RequestParam("adjType") String adjType, @RequestParam("adjCategory") String adjCategory)
			throws ParseException, Exception {
		logger.info("rollbackTTUMReport " + adjType + " " + filedate + " " + adjCategory);
		if (adjCategory.contains("REPORT")) {
			String str = this.SETTLTTUMSERVICE.TTUMRollbackReportICD(filedate, adjType);
			return str;
		}
		String value = this.SETTLTTUMSERVICE.TTUMRollbackICD(filedate, adjType);
		return value;
	}

	@RequestMapping(value = { "rollbackTTUMReportSETTL" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMReportSETTL(@RequestParam("filedate") String filedate,
			@RequestParam("cycle") String cycle, @RequestParam("adjCategory") String adjCategory)
			throws ParseException, Exception {
		NFSSettlementBean beandata = new NFSSettlementBean();
		beandata.setCycle(Integer.valueOf(cycle).intValue());
		beandata.setDatepicker(filedate);
		beandata.setAdjCategory(adjCategory);
		logger.info("rollbackTTUMReport  " + filedate + " " + adjCategory + " cycle " + cycle);
		boolean result = false;
		if (adjCategory.contains("REPORT_STTL_ICD")) {
			String value = this.SETTLTTUMSERVICE.TTUMRollbackReportSETTLICD(filedate);
			return value;
		}
		if (adjCategory.contains("REPORT_STTL_JCB") || adjCategory.contains("TTUM_STTL_JCB")) {
			result = this.SETTLTTUMSERVICE.TTUMRollbackReportSETTLJCB(beandata);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}
		if (adjCategory.contains("REPORT_STTL_DFS") || adjCategory.contains("TTUM_STTL_DFS")) {
			result = this.SETTLTTUMSERVICE.TTUMRollbackReportSETTLDFS(beandata);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}
		if (adjCategory.equalsIgnoreCase("REPORT_SETTL")) {
			result = this.SETTLTTUMSERVICE.TTUMRollbackReportSETTL(beandata);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}
		if (adjCategory.equalsIgnoreCase("TTUM_STTL_ICD")) {
			String value = this.SETTLTTUMSERVICE.TTUMRollbackSETTLICD(filedate);
			return value;
		} else {
			result = this.SETTLTTUMSERVICE.TTUMRollbackReportSETTL(beandata);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}

	}

	@RequestMapping(value = { "rollbackTTUMReportSETTLJCBDFS" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMReportSETTLJCBDFS(@RequestParam("filedate") String filedate,
			@RequestParam("adjCategory") String adjCategory) throws ParseException, Exception {
		NFSSettlementBean beandata = new NFSSettlementBean();
		beandata.setDatepicker(filedate);
		beandata.setAdjCategory(adjCategory);
		logger.info("rollbackTTUMReport  " + filedate + " " + adjCategory);
		boolean result = false;
		if (adjCategory.contains("REPORT_STTL_ICD")) {
			String value = this.SETTLTTUMSERVICE.TTUMRollbackReportSETTLICD(filedate);
			return value;
			
		}
		if (adjCategory.contains("REPORT_STTL_JCB") || adjCategory.contains("TTUM_STTL_JCB")) {
			result = this.SETTLTTUMSERVICE.TTUMRollbackReportSETTLJCB(beandata);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
			
			
		}
		if (adjCategory.contains("REPORT_STTL_DFS") || adjCategory.contains("TTUM_STTL_DFS")) {
			result = this.SETTLTTUMSERVICE.TTUMRollbackReportSETTLDFS(beandata);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
			
		}
		if (adjCategory.equalsIgnoreCase("REPORT_SETTL")) {
			result = this.SETTLTTUMSERVICE.TTUMRollbackReportSETTL(beandata);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}
		if (adjCategory.equalsIgnoreCase("TTUM_STTL_ICD")) {
			String value = this.SETTLTTUMSERVICE.TTUMRollbackSETTLICD(filedate);
			return value;
		}
		result = this.SETTLTTUMSERVICE.TTUMRollbackReportSETTL(beandata);
		if (result)
			return "SUCCESSFULLY ROLLBACK";
		return "ALREADY ROLLBACK";
	}

	@RequestMapping(value = { "DownloadAdjTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadAdjTTUM(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpServletResponse response, HttpSession httpSession,
			RedirectAttributes redirectAttributes, Model model) throws Exception {
		logger.info("***** DownloadSettlementreport.POST Start ****" + nfsSettlementBean.getAdjType());
		String fileName = "";
		String zipName = "";
		logger.info("DownloadAdjTTUM POST");
		if (nfsSettlementBean.getAdjCategory().contains("REPORT")) {
		
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is " + Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			logger.info("File Name is " + nfsSettlementBean.getFileName());
			boolean executed = false;
			if (nfsSettlementBean.getDatepicker().contains(","))
				;
			nfsSettlementBean.setDatepicker(nfsSettlementBean.getDatepicker());
			if(nfsSettlementBean.getDatepicker().contains("ICCW")) {
				TTUMData = this.SETTLTTUMSERVICE.getAdjTTUMICCW(nfsSettlementBean);
					
			}else {
				TTUMData = this.SETTLTTUMSERVICE.getAdjTTUM(nfsSettlementBean);
				
			}
			
		System.out.println("nfsSettlementBean.getStSubCategory()" + nfsSettlementBean.getStSubCategory());
			fileName = "NFS_ADJUSTMENT_" + nfsSettlementBean.getAdjType() + "_TTUM_"
					+ nfsSettlementBean.getDatepicker().replaceAll("/", "") + ".txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker().replaceAll("/", ""),
					nfsSettlementBean.getCategory());
			obj.generateADJTTUMFiles(stPath, fileName, 1, TTUMData2, "NFS", nfsSettlementBean);
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			if (nfsSettlementBean.getAdjType().equalsIgnoreCase("ACUIRER PENALTY")) {
				Column_list.add("SR_NO");
				Column_list.add("TXNUID");
				Column_list.add("U_ID");
				Column_list.add("ADJDATE");
				Column_list.add("ADJTYPE");
				Column_list.add("ACQ");
				Column_list.add("ISR");
				Column_list.add("RESPONSE");
				Column_list.add("TXNDATE");
				Column_list.add("TXNTIME");
				Column_list.add("RRN");
				Column_list.add("ATMID");
				Column_list.add("CARDNO");
				Column_list.add("CHBDATE");
				Column_list.add("CHBREF");
				Column_list.add("TXNAMOUNT");
				Column_list.add("ADJAMOUNT");
				Column_list.add("ACQFEE");
				Column_list.add("ISSFEE");
				Column_list.add("ISSFEESW");
				Column_list.add("NPCIFEE");
				Column_list.add("ACQFEETAX");
				Column_list.add("ISSFEETAX");
				Column_list.add("NPCITAX");
				Column_list.add("ADJREF");
				Column_list.add("BANKADJREF");
				Column_list.add("REASONDESC");
				Column_list.add("PINCODE");
				Column_list.add("ATMLOCATION");
				Column_list.add("FCQM");
				Column_list.add("ADJSETTLEMENTDATE");
				Column_list.add("CUSTOMERPENALTY");
				Column_list.add("ADJTIME");
				Column_list.add("CYCLE");
				Column_list.add("TAT_EXPIRY_DATE");
				Column_list.add("ACQSTLAMOUNT");
				Column_list.add("ACQCC");
				Column_list.add("PAN_ENTRY_MODE");
				Column_list.add("SERVICE_CODE");
				Column_list.add("CARD_DAT_INPUT_CAPABILITY");
				Column_list.add("MCC_CODE");
				Column_list.add("CREATEDDATE");
				Column_list.add("CREATEDBY");
				Column_list.add("FILEDATE");
				Column_list.add("TXNTYPE");
				Column_list.add("FILENAME");
				Column_list.add("ACCOUNT_NO");
				Column_list.add("ATM_ID");
				Column_list.add("ATM_CRM");
				Column_list.add("MSP_FINAL");
				Column_list.add("CASH_VENDOR");
				Column_list.add("TYP");
				Column_list.add("DR_CR");
				Column_list.add("NARRATION");
			}
			Column_list.add("SR_NO");
			Column_list.add("DR/CR");
			Column_list.add("ACQ_BANK");
			Column_list.add("TXNDATE");
			Column_list.add("TXNTIME");
			Column_list.add("RRN");
			Column_list.add("CARD_NO");
			Column_list.add("ADJAMOUNT");
			Column_list.add("ACCOUNTNO");
			Column_list.add("NARRATION");
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			System.out.println("filename in nfs ttum is " + fileName);
			fileName = "NFS_ADJUSTMENT_" + nfsSettlementBean.getAdjType() + "_TTUM_"
					+ nfsSettlementBean.getDatepicker().replaceAll("/", "") + ".xls";
			zipName = "NFS_ADJUSTMENT_" + nfsSettlementBean.getAdjType() + "_TTUM_"
					+ nfsSettlementBean.getDatepicker().replaceAll("/", "") + ".zip";
			obj.generateExcelTTUM(stPath, fileName, Excel_data,
					"NFS_" + nfsSettlementBean.getDatepicker().replaceAll("/", "-"), zipName);
			logger.info("File is created");
			File file = new File(String.valueOf(stPath) + File.separator + fileName);
			logger.info("path of zip file " + stPath + File.separator + fileName);
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			response.setContentType("application/txt");
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
			response.setHeader(headerKey, headerValue);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
			response.flushBuffer();
		} else {

			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is " + Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			logger.info("File Name is " + nfsSettlementBean.getFileName());
			boolean executed = false;
			TTUMData = this.SETTLTTUMSERVICE.getDailyNFSTTUMData(nfsSettlementBean);
			System.out.println("nfsSettlementBean.getStSubCategory()" + nfsSettlementBean.getStSubCategory());
			if (nfsSettlementBean.getAdjType().equalsIgnoreCase("PRE-ARBITRATION DECLINED")) {
				fileName = "NFSPARTIALPREARBITRATIONACCEPTANCETTUM"
						+ nfsSettlementBean.getDatepicker().replaceAll("/", "") + "_val.txt";
			} else if (nfsSettlementBean.getAdjType().equalsIgnoreCase("RE-PRESENTMENT RAISE")) {
				fileName = "NFSPARTIALCHARGEBACKACCEPTANCETTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "")
						+ "_val.txt";
			} else {
				fileName = "NFS" + nfsSettlementBean.getAdjType().replaceAll("\\s", "").replaceAll("-", "") + "TTUM"
						+ nfsSettlementBean.getDatepicker().replaceAll("/", "") + "_val.txt";
			}
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker().replaceAll("/", ""),
					nfsSettlementBean.getCategory());
			obj.generateADJTTUMFiles(stPath, fileName, 1, TTUMData, "NFS", nfsSettlementBean);
			logger.info("File is created");
			logger.info("File is created");
			if (nfsSettlementBean.getAdjType().equalsIgnoreCase("PRE-ARBITRATION DECLINED")) {
				zipName = "NFSPARTIALPREARBITRATIONACCEPTANCETTUM"
						+ nfsSettlementBean.getDatepicker().replaceAll("/", "") + "_val.txt";
			} else if (nfsSettlementBean.getAdjType().equalsIgnoreCase("RE-PRESENTMENT RAISE")) {
				zipName = "NFSPARTIALCHARGEBACKACCEPTANCETTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "")
						+ "_val.txt";
			} else {
				zipName = "NFS" + nfsSettlementBean.getAdjType().replaceAll("\\s", "").replaceAll("-", "") + "TTUM"
						+ nfsSettlementBean.getDatepicker().replaceAll("/", "") + "_val.txt";
			}
			File file = new File(String.valueOf(stPath) + File.separator + zipName);
			logger.info("path of zip file " + stPath + File.separator + zipName);
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			logger.info("before downloading zip file ");
			response.setContentType("application/txt");
			logger.info("download completed");
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
			response.setHeader(headerKey, headerValue);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
			response.flushBuffer();
		}
	}

	@RequestMapping(value = { "DownloadAdjTTUMVISANEW" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadAdjTTUMVISANEW(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpServletResponse response, HttpSession httpSession,
			RedirectAttributes redirectAttributes, Model model) throws Exception {
		logger.info("***** DownloadAdjTTUMVISANEW.POST Start ****" + nfsSettlementBean.getAdjType() + "ADS "+  nfsSettlementBean.getAdjCategory());
		String fileName = "";
		String zipName = "";
		logger.info("DownloadAdjTTUMVISANEW POST");
		if (nfsSettlementBean.getAdjCategory().contains("REPORT")) {
		
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is " + Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			logger.info("File Name is " + nfsSettlementBean.getFileName());
			boolean executed = false;
			if (nfsSettlementBean.getDatepicker().contains(","))
				;
			nfsSettlementBean.setDatepicker(nfsSettlementBean.getDatepicker());
			if(nfsSettlementBean.getFileName().contains("ICCW")) {
				TTUMData = this.SETTLTTUMSERVICE.getAdjTTUMICCW(nfsSettlementBean);
					
			}else if(nfsSettlementBean.getFileName().contains("VISA")) {
				TTUMData = this.SETTLTTUMSERVICE.getAdjTTUMVISA(nfsSettlementBean);
					
			}else {
				TTUMData = this.SETTLTTUMSERVICE.getAdjTTUM(nfsSettlementBean);
				
			}
			
		System.out.println("nfsSettlementBean.getStSubCategory()" + nfsSettlementBean.getStSubCategory());
			fileName = "VISA_ADJUSTMENT_" + nfsSettlementBean.getAdjType() + "_TTUM_"
					+ nfsSettlementBean.getDatepicker().replaceAll("/", "") + ".txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker().replaceAll("/", ""),
					nfsSettlementBean.getCategory());
			obj.generateADJTTUMFiles(stPath, fileName, 1, TTUMData2, "NFS", nfsSettlementBean);
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();

				Column_list.add("SR_NO");
				Column_list.add("card_no");
				Column_list.add("AC_NO");
				Column_list.add("AUTH_CODE");
				Column_list.add("ARN");
				Column_list.add("CBK_RSDT");
				Column_list.add("Tran_date");
				Column_list.add("Amount");
				Column_list.add("Reject_date");
				Column_list.add("DR_CR");
				Column_list.add("TYPE");
				Column_list.add("UID");
				Column_list.add("NARRATION");
				
			System.out.println("filename in nfs ttum is " + fileName);
			fileName = "VISA_ADJUSTMENT_" + nfsSettlementBean.getAdjType() + "_TTUM_"
					+ nfsSettlementBean.getDatepicker().replaceAll("/", "") + ".xls";
			zipName = "VISA_ADJUSTMENT_" + nfsSettlementBean.getAdjType() + "_TTUM_"
					+ nfsSettlementBean.getDatepicker().replaceAll("/", "") + ".zip";
			obj.generateExcelTTUM(stPath, fileName, Excel_data,
					"VISA ADJUSTMENT "+nfsSettlementBean.getAdjType() +"_" + nfsSettlementBean.getDatepicker().replaceAll("/", "-"), zipName);
			logger.info("File is created");
			File file = new File(String.valueOf(stPath) + File.separator + fileName);
			logger.info("path of zip file " + stPath + File.separator + fileName);
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			response.setContentType("application/txt");
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
			response.setHeader(headerKey, headerValue);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
			response.flushBuffer();
		} else {

			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is " + Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			logger.info("File Name is " + nfsSettlementBean.getFileName());
			boolean executed = false;
			TTUMData = this.SETTLTTUMSERVICE.getDailyVISATTUMData(nfsSettlementBean);
			System.out.println("nfsSettlementBean.getStSubCategory()" + nfsSettlementBean.getStSubCategory());
		
				fileName = "VISA" + nfsSettlementBean.getAdjType().replaceAll("\\s", "").replaceAll("-", "") + "TTUM"
						+ nfsSettlementBean.getDatepicker().replaceAll("/", "") + "_val.txt";
			
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker().replaceAll("/", ""),
					nfsSettlementBean.getCategory());
			obj.generateADJTTUMFiles(stPath, fileName, 1, TTUMData, "NFS", nfsSettlementBean);
			logger.info("File is created");
			logger.info("File is created");

				zipName = "VISA" + nfsSettlementBean.getAdjType().replaceAll("\\s", "").replaceAll("-", "") + "TTUM"
						+ nfsSettlementBean.getDatepicker().replaceAll("/", "") + "_val.txt";
			
			File file = new File(String.valueOf(stPath) + File.separator + zipName);
			logger.info("path of zip file " + stPath + File.separator + zipName);
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			logger.info("before downloading zip file ");
			response.setContentType("application/txt");
			logger.info("download completed");
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
			response.setHeader(headerKey, headerValue);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
			response.flushBuffer();
		}
	}

	@RequestMapping(value = { "DownloadAdjTTUMSETTL" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadAdjTTUMSETTL(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpServletResponse response, HttpSession httpSession,
			RedirectAttributes redirectAttributes, Model model) throws Exception {
		logger.info("***** DownloadSettlementreport.POST Start ****" + nfsSettlementBean.getAdjCategory());
		String fileName = "";
		String zipName = "";
		logger.info("DownloadAdjTTUM POST");
		if (nfsSettlementBean.getAdjCategory().contains("REPORT_STTL_ICD")
				|| nfsSettlementBean.getAdjCategory().contains("REPORT_SETTL")
				|| nfsSettlementBean.getAdjCategory().contains("REPORT_STTL_JCB")
				|| nfsSettlementBean.getAdjCategory().contains("REPORT_STTL_DFS")) {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is " + Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			TTUMData = this.SETTLTTUMSERVICE.getAdjTTUMSETTL(nfsSettlementBean);
			System.out.println("nfsSettlementBean.getStSubCategory()" + nfsSettlementBean.getStSubCategory());
			fileName = "NFSSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + ".txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker().replaceAll("/", "-"),
					nfsSettlementBean.getCategory());
			obj.generateADJTTUMFiles(stPath, fileName, 1, TTUMData2, "NFS", nfsSettlementBean);
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			if (nfsSettlementBean.getAdjCategory().contains("JCB")
					|| nfsSettlementBean.getAdjCategory().contains("DFS")) {
				Column_list.add("SR_NO");
				Column_list.add("GL_ACCOUNT");
				Column_list.add("INR");
				Column_list.add("SIX_DIG_GL");
				Column_list.add("D_C");
				Column_list.add("NO_OF_TXNS");
				Column_list.add("DEBIT");
				Column_list.add("CREDIT");
				Column_list.add("NARRATION");
				Column_list.add("REMARKS");
				Column_list.add("PARTICULARS");
				Column_list.add("FILEDATE");
			} else {
				Column_list.add("SR_NO");
				Column_list.add("GL_ACCOUNT");
				Column_list.add("INR");
				Column_list.add("SIX_DIG_GL");
				Column_list.add("D_C");
				Column_list.add("NO_OF_TXNS");
				Column_list.add("DEBIT");
				Column_list.add("CREDIT");
				Column_list.add("NARRATION");
				Column_list.add("REMARKS");
				Column_list.add("PARTICULARS");
				Column_list.add("CYCLE");
				Column_list.add("FILEDATE");
			}
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
		
			if (nfsSettlementBean.getAdjCategory().contains("ICD")) {
				fileName = "ICDSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + ".xls";
				zipName = "ICDSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + ".zip";
			} else if (nfsSettlementBean.getAdjCategory().contains("JCB")) {
				fileName = "JCBSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + ".xls";
				zipName = "JCBSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + ".zip";
			} else if (nfsSettlementBean.getAdjCategory().contains("DFS")) {
				fileName = "DFSSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + ".xls";
				zipName = "DFSSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + ".zip";
			} else {
				fileName = "NFSSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + ".xls";
				zipName = "NFSSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + ".zip";
			}
			
			if (nfsSettlementBean.getAdjCategory().equalsIgnoreCase("REPORT_SETTL")) { 
				nfsSettlementBean.setAdjCategory("NFS");
				
			}else if (nfsSettlementBean.getAdjCategory().contains("ICD")) { 
				
				nfsSettlementBean.setAdjCategory("ICD");
				
			}else if (nfsSettlementBean.getAdjCategory().contains("JCB")) { 
				
				nfsSettlementBean.setAdjCategory("JCB");
				
			}else if (nfsSettlementBean.getAdjCategory().contains("DFS")) { 
				
				nfsSettlementBean.setAdjCategory("DFS");
				
			}
			
			obj.generateExcelTTUMSettlement(stPath, fileName, Excel_data, nfsSettlementBean.getAdjCategory() + "_"
					+ nfsSettlementBean.getDatepicker().replaceAll("/", "-")+"_"+nfsSettlementBean.getCycle(), zipName);
			logger.info("File is created");
			File file = new File(String.valueOf(stPath) + File.separator + fileName);
			logger.info("path of zip file " + stPath + File.separator + fileName);
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			logger.info("before downloading zip file ");
			response.setContentType("application/txt");
			logger.info("download completed");
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
			response.setHeader(headerKey, headerValue);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
			response.flushBuffer();
		} else {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is " + Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			logger.info("File Name is " + nfsSettlementBean.getFileName());
			TTUMData = this.SETTLTTUMSERVICE.getDailyNFSTTUMDataSETTL(nfsSettlementBean);
			if (nfsSettlementBean.getAdjCategory().contains("ICD")) {
				fileName = "ICDSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + "_val.txt";
			} else if (nfsSettlementBean.getAdjCategory().contains("JCB")) {
				fileName = "JCBSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + "_val.txt";
			} else if (nfsSettlementBean.getAdjCategory().contains("DFS")) {
				fileName = "DFSSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + "_val.txt";
			} else {
				fileName = "NFSSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + "_val.txt";
			}
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker().replaceAll("/", "-"),
					nfsSettlementBean.getCategory());
			obj.generateADJTTUMFiles(stPath, fileName, 1, TTUMData, "NFS", nfsSettlementBean);
			if (nfsSettlementBean.getAdjCategory().contains("ICD")) {
				zipName = "ICDSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + "_val.txt";
			} else if (nfsSettlementBean.getAdjCategory().contains("JCB")) {
				zipName = "JCBSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + "_val.txt";
			} else if (nfsSettlementBean.getAdjCategory().contains("DFS")) {
				zipName = "DFSSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + "_val.txt";
			} else {
				zipName = "NFSSETTLEMENTTTUM" + nfsSettlementBean.getDatepicker().replaceAll("/", "-") + "_val.txt";
			}
			File file = new File(String.valueOf(stPath) + File.separator + zipName);
			logger.info("path of zip file " + stPath + File.separator + zipName);
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			response.setContentType("application/txt");
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
			response.setHeader(headerKey, headerValue);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
			response.flushBuffer();
		}
	}

	@RequestMapping(value = { "CooperativeTTUM" }, method = { RequestMethod.GET })
	public ModelAndView CooperativeTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** CooperativeTTUM.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("CooperativeTTUM GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
		modelAndView.setViewName("GenerateNFSCooperativeTTUM");
		logger.info("***** NFSSettlementController.CooperativeTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "NFSCoopValidation" }, method = { RequestMethod.POST })
	@ResponseBody
	public String NFSCoopValidation(String fileDate, String stSubCategory, String timePeriod, String cycle,
			String filename, HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes,
			Model model) throws Exception {
		logger.info("***** NFSCoopValidation.Post Start ****");
		String lastDate = fileDate;
		logger.info("NFSCoopValidation POST");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
		logger.info("filename is " + filename);
		nfsSettlementBean.setFileName(filename);
		nfsSettlementBean.setStSubCategory(stSubCategory);
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		nfsSettlementBean.setCycle(Integer.parseInt(cycle));
		nfsSettlementBean.setDatepicker(fileDate);
		HashMap<String, Object> result = this.nfsSettlementService.ValidateCooperativeBank(nfsSettlementBean);
		if (result != null && ((Boolean) result.get("result")).booleanValue())
			return "success";
		if (result == null)
			return "Error";
		if (!result.get("msg").toString().equalsIgnoreCase("")) {
			System.out.println(result.get("msg").toString());
			return result.get("msg").toString();
		}
		return "Problem Occurred!";
	}

	@RequestMapping(value = { "DownloadCoopTTUM" }, method = { RequestMethod.POST })
	public String DownloadCoopTTUM(@ModelAttribute("nfsSettlementBean") NFSSettlementBean nfsSettlementBean,
			HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model)
			throws Exception {
		logger.info("***** DownloadCoopTTUM.POST Start ****");
		logger.info("DownloadCoopTTUM POST");
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
		List<Object> Excel_data = new ArrayList();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		logger.info("File Name is " + nfsSettlementBean.getFileName());
		boolean executed = false;
		if (nfsSettlementBean.getDatepicker().contains(","))
			;
		nfsSettlementBean.setDatepicker(nfsSettlementBean.getDatepicker().replace(",", ""));
		boolean checkProcFlag = this.nfsSettlementService.checkCoopTTUMProcess(nfsSettlementBean);
		executed = this.SETTLTTUMSERVICE.runCooperativeTTUM(nfsSettlementBean);
		if (executed)
			Excel_data = this.SETTLTTUMSERVICE.getCooperativeTTUM(nfsSettlementBean);
		model.addAttribute("ReportName", "Co-Operative_Bank_TTUM");
		model.addAttribute("data", Excel_data);
		logger.info("***** NFSSettlementController.DownloadCoopTTUM Daily POST End ****");
		return "GenerateNFSDailyReport";
	}

	@RequestMapping(value = { "DownloadReversalTTUM" }, method = { RequestMethod.GET })
	public ModelAndView DownloadReversalTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** DownloadReversalTTUM.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("DownloadReversalTTUM GET");
		modelAndView.addObject("category", category);
		modelAndView.addObject("nfsSettlementBean", nfsSettlementBean);
		modelAndView.setViewName("NFSLateReversalTTUM");
		logger.info("***** NFSSettlementController.DownloadReversalTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "NFSLateReversalProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String NFSLateReversalProcess(String category, String datepicker, HttpServletRequest request)
			throws Exception {
		logger.info("***** NFSLateReversalValidate.Post Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NFSLateReversalValidate POST");
		boolean execute = false;
		nfsSettlementBean.setCategory(category);
		nfsSettlementBean.setDatepicker(datepicker);
		String msg = this.SETTLTTUMSERVICE.ValidateLateReversalFile(nfsSettlementBean);
		if (msg != null && msg.equalsIgnoreCase("success")) {
			execute = this.SETTLTTUMSERVICE.runLateReversalTTUM(nfsSettlementBean);
		} else {
			return msg;
		}
		if (execute)
			return "Processing completed!";
		return "Processing Failed";
	}

	@RequestMapping(value = { "NFSLateReversalValidate" }, method = { RequestMethod.POST })
	@ResponseBody
	public String NFSLateReversalValidate(String category, String datepicker, HttpServletRequest request)
			throws Exception {
		logger.info("***** NFSLateReversalValidate.Post Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NFSLateReversalValidate POST");
		nfsSettlementBean.setCategory(category);
		nfsSettlementBean.setDatepicker(datepicker);
		String msg = this.SETTLTTUMSERVICE.checkReversalTTUMProcess(nfsSettlementBean);
		return msg;
	}

	@RequestMapping(value = { "DownloadReversalTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadReversalTTUMPost(String category, String datepicker, HttpServletResponse response,
			HttpServletRequest request, HttpSession httpSession, Model model) throws Exception {
		logger.info("***** DownloadReversalTTUM.Post Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("DownloadReversalTTUM POST");
		List<Object> Excel_data = new ArrayList();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		nfsSettlementBean.setCategory(category);
		nfsSettlementBean.setDatepicker(datepicker);
		Excel_data = this.SETTLTTUMSERVICE.getLateReversalTTUMData(nfsSettlementBean);
		String fileName = "LATE_REVERSAL_TTUM.txt";
		String stPath = System.getProperty("java.io.tmpdir");
		GenerateUCOTTUM obj = new GenerateUCOTTUM();
		stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker(), nfsSettlementBean.getCategory());
		obj.generateTTUMFile(stPath, fileName, Excel_data);
		logger.info("File is created");
		File file = new File(String.valueOf(stPath) + File.separator + fileName);
		logger.info("path of zip file " + stPath + File.separator + fileName);
		FileInputStream inputstream = new FileInputStream(file);
		response.setContentLength((int) file.length());
		logger.info("before downloading zip file ");
		response.setContentType("application/txt");
		logger.info("download completed");
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
		response.setHeader(headerKey, headerValue);
		ServletOutputStream servletOutputStream = response.getOutputStream();
		IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
		response.flushBuffer();
	}

	@RequestMapping(value = { "GenerateFinacleTTUM" }, method = { RequestMethod.GET })
	public ModelAndView GenerateFinacleTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateFinacleTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = this.iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateFinacleTTUM");
		logger.info("***** GenerateFinacleTTUM.GenerateFinacleTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "ProcessFinacleTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ProcessFinacleTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** ProcessFinacleTTUM.ProcessFinacleTTUM post Start ****");
		logger.info("ProcessFinacleTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("filedate is " + beanObj.getLocalDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		HashMap<String, Object> output = this.rupayTTUMService.checkrunProcessFinacleTTUM(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue()) {
			output2 = this.rupayTTUMService.runProcessFinacleTTUM(beanObj);
		} else {
			return output.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue()) {
			System.out.println("msg " + output2.get("msg"));
			if (output2.get("msg") == null)
				return "PROCESS SUCCESSFULLY!";
			return "PROCESS NOT COMPLETED";
		}
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "checkFinacleTTUMProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkFinacleTTUMProcess(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		try {
			logger.info("TTUMController: checkFinacleTTUMProcess: Entry");
			return "success";
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return "Exception";
		}
	}

	@RequestMapping(value = { "DownloadFInacleTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadFInacleTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** DownloadFInacleTTUM.DownloadFInacleTTUM post Start ****");
		logger.info("DownloadFInacleTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		String fileName = "", zipName = "";
		if (beanObj.getTTUMCategory().contains("REPORT")) {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			boolean executed = false;
			TTUMData = this.SETTLTTUMSERVICE.getFinacleTTUM(beanObj);
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			Map<String, String> table_Data = new HashMap<>();
			Column_list.add("RESPONSE");
			Column_list.add("ATMID");
			Column_list.add("CARDNO");
			Column_list.add("ACC_NO");
			Column_list.add("RRN");
			Column_list.add("SEQ_NUM");
			Column_list.add("TXNDATE");
			Column_list.add("TXNTIME");
			Column_list.add("TXNAMOUNT");
			Column_list.add("ADJAMOUNT");
			Column_list.add("MSP_FINAL");
			Column_list.add("ADJ_TYPE");
			Column_list.add("CASH_VENDOR");
			Column_list.add("FILEDATE");
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			System.out.println("filename in nfs ttum is " + fileName);
			fileName = "FINACLE_TTUM" + beanObj.getLocalDate() + ".xls";
			zipName = "FINACLE_TTUM" + beanObj.getLocalDate() + ".zip";
			obj.generateExcelTTUM(stPath, fileName, Excel_data, "REFUND", zipName);
			logger.info("File is created");
			File file = new File(String.valueOf(stPath) + File.separator + fileName);
			logger.info("path of zip file " + stPath + File.separator + fileName);
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			logger.info("before downloading zip file ");
			response.setContentType("application/txt");
			logger.info("download completed");
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
			response.setHeader(headerKey, headerValue);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
			response.flushBuffer();
		} else {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			logger.info("File Name is " + beanObj.getFileName());
			boolean executed = false;
			TTUMData = this.SETTLTTUMSERVICE.getVISATTUMNFSRUPAY(beanObj);
			System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
			fileName = "FINACLETTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
			obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData, "VISA", beanObj);
			logger.info("File is created");
			logger.info("File is created");
			zipName = "FINACLETTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
			File file = new File(String.valueOf(stPath) + File.separator + zipName);
			logger.info("path of zip file " + stPath + File.separator + zipName);
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			logger.info("before downloading zip file ");
			response.setContentType("application/txt");
			logger.info("download completed");
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new Object[] { file.getName() });
			response.setHeader(headerKey, headerValue);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputstream, (OutputStream) servletOutputStream);
			response.flushBuffer();
		}
	}

	@RequestMapping(value = { "rollbackFinacleTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackFinacleTTUM(@RequestParam("filedate") String filedate) throws ParseException, Exception {
		logger.info("rollbackFinacleTTUM  " + filedate);
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		beanObj.setLocalDate(filedate);
		beanObj.setFileDate(filedate);
		HashMap<String, Object> output2 = null;
		HashMap<String, Object> output = this.rupayTTUMService.checkrunProcessFinacleTTUM(beanObj);
		if (((Boolean) output.get("result")).booleanValue())
			return "Already Rollback Processed";
		boolean checkProcFlag = this.SETTLTTUMSERVICE.rollBackFinacleTTUM(beanObj);
		if (checkProcFlag)
			return "Rollback Completed";
		return "Failed";
	}
}
