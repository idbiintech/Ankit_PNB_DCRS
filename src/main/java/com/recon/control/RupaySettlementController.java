package com.recon.control;

import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.RupaySettlementBean;
import com.recon.model.RupayUploadBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.ISourceService;
import com.recon.service.NFSSettlementService;
import com.recon.service.RupaySettlementService;
import com.recon.service.SettlementTTUMService;
import com.recon.util.GeneralUtil;
import com.recon.util.GenerateUCOTTUM;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RupaySettlementController {
	@Autowired
	RupaySettlementService rupaySettlementService;

	@Autowired
	ISourceService iSourceService;

	@Autowired
	NFSSettlementService nfsSettlementService;

	@Autowired
	SettlementTTUMService SETTLTTUMSERVICE;

	@Autowired
	GeneralUtil generalutil;

	private static final Logger logger = Logger.getLogger(com.recon.control.RupaySettlementController.class);

	private static final String ERROR_MSG = "error_msg";

	public static final String OUTPUT_FOLDER = String.valueOf(System.getProperty("catalina.home")) + File.separator
			+ "TTUM";

	@RequestMapping(value = { "rupSettlementFilUpload" }, method = { RequestMethod.GET })
	public String rupsettlementFilUpload(HttpServletRequest request) throws Exception {
		logger.info("***** settlementFilUpload Start get method  ****");
		return "RupaySettlementFileUpload";
	}

	@RequestMapping(value = { "postUploadRupSettlFile" }, method = { RequestMethod.POST })
	@ResponseBody
	public String postUploadRupSettlFile(@ModelAttribute("rupaySettlementBean") RupaySettlementBean rupSettlementBean,
			ModelAndView modelAndView, HttpServletRequest request, @RequestParam("file") MultipartFile file,
			String stSubCategory, String datepicker, String cycle, HttpSession httpSession) throws Exception {
		logger.info("***** settlementFilUpload Start ****");
		HashMap<String, Object> output = null;
		try {
			String createdBy = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			String fileName = file.getOriginalFilename();
			if (fileName.contains("@")) {
				String[] fileArr = fileName.split("@");
				String[] fileCyleArr = fileArr[1].split("\\.");
				int fileCycle = Integer.parseInt(fileCyleArr[0]);
				int selectedCycle = rupSettlementBean.getCycle();
				if (fileCycle != selectedCycle)
					return "File uploaded and Cycle Selected are different";
			} else {
				return "File format should be like ......FileName@cycleNumber.Extention";
			}
			rupSettlementBean.setCreatedBy(createdBy);
			rupSettlementBean.setCategory("RUPAY_SETTLEMENT");
			rupSettlementBean.setStSubCategory(stSubCategory);
			rupSettlementBean.setFileName("RUPAY_SETTLEMENT");
			HashMap<String, Object> result = this.rupaySettlementService.validatePrevFileUpload(rupSettlementBean);
			if (result != null && !((Boolean) result.get("result")).booleanValue()) {
				output = this.rupaySettlementService.uploadExcelFile(rupSettlementBean, file);
				logger.info("***** RupayettlementController.RupayFileUpload POST End ****");
				if (((Boolean) output.get("result")).booleanValue())
					return "File Uploaded Successfully ";
				return "Error while Uploading file";
			}
			return result.get("msg").toString();
		} catch (Exception e) {
			logger.info("Exception in RupaySettlementController " + e);
			if (output != null && !((Boolean) output.get("result")).booleanValue())
				return "Error Occured  ";
			return "Error Occurred in reading";
		}
	}

	@RequestMapping(value = { "rupSettlemenTtum" }, method = { RequestMethod.GET })
	public String rupSettlemenTtum(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("***** rupSettlemenTtum Start get method  ****");
		logger.info("***** getRupSettlttum Start post method  ****");
		return "RupaySettlementTtum";
	}

	@RequestMapping(value = { "getRupSettlttum" }, method = { RequestMethod.POST })
	public void getRupSettlttum(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("***** getRupSettlttum Start post method  ****");
		String settlementDate = request.getParameter("fileDate");
		this.rupaySettlementService.generateRupaySettlmentTTum(settlementDate, response);
	}

	@RequestMapping(value = { "RupayFileUpload" }, method = { RequestMethod.GET })
	public ModelAndView nfsFileUploadGet(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** RupayFileUpload.Get Start ****");
		RupayUploadBean rupaySettlementBean = new RupayUploadBean();
		logger.info("RupayFileUpload GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		modelAndView.addObject("category", category);
		modelAndView.addObject("rupaySettlementBean", rupaySettlementBean);
		modelAndView.setViewName("RupayFileUpload");
		logger.info("***** RupaySettlementController.RupayFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "RupayFileUpload" }, method = { RequestMethod.POST })
	@ResponseBody
	public String RupayFileUploadPost(@ModelAttribute("rupaySettlementBean") RupayUploadBean rupayBean,
			HttpServletRequest request, @RequestParam("file") MultipartFile file, HttpSession httpSession, Model model,
			ModelAndView modelAndView, RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** RupayFileUpload.post Start ****");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		rupayBean.setCreatedBy(Createdby);
		logger.info("Subcategory is " + rupayBean.getSubcategory());
		if (rupayBean.getFileName().equalsIgnoreCase("CHARGEBACK")) {
			logger.info("Cycle is " + file.getOriginalFilename().substring(2, 3));
			rupayBean.setCycle(file.getOriginalFilename().substring(2, 3));
		} else if (file.getOriginalFilename().contains("-")) {
			String[] fileName = file.getOriginalFilename().split("\\.");
			logger.info("Cycle is " + fileName[0].substring(fileName[0].length() - 1, fileName[0].length()));
			rupayBean.setCycle(fileName[0].substring(fileName[0].length() - 1, fileName[0].length()));
		} else {
			return "Invalid file uploaded";
		}
		System.out.println("filename is" + rupayBean.getFileName());
		if (this.rupaySettlementService.checkFileUploaded(rupayBean))
			return "File is already uploaded!";
		Boolean readFlag = Boolean.valueOf(false);
		if (rupayBean.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
			readFlag = Boolean.valueOf(this.rupaySettlementService.readFile(rupayBean, file));
		} else {
			readFlag = Boolean.valueOf(this.rupaySettlementService.readIntFile(rupayBean, file));
		}
		if (readFlag.booleanValue())
			return "File Uploaded Successfully";
		return "Issue while uploading file";
	}

	@RequestMapping(value = { "RupaySettlementProcess" }, method = { RequestMethod.GET })
	public ModelAndView RupaySettlementProcessGet(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** RupayFileUpload.Get Start ****");
		RupayUploadBean rupaySettlementBean = new RupayUploadBean();
		logger.info("RupayFileUpload GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		modelAndView.addObject("category", category);
		modelAndView.addObject("rupaySettlementBean", rupaySettlementBean);
		modelAndView.setViewName("RupaySettlementProcess");
		logger.info("***** RupaySettlementController.RupayFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "RupaySettlementProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String RupaySettlementProcessPost(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** RupaySettlementController.RupaySettlementProcess post Start ****");
		logger.info("RupaySettlementProcess POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		boolean checkProcess = false;
		boolean checkProcFlag = false;
		if (beanObj.getSubcategory().equalsIgnoreCase("RRB_DOMESTIC")
				|| beanObj.getSubcategory().equalsIgnoreCase("RRB_INTERNATIONAL")) {
			if (beanObj.getSubcategory().equalsIgnoreCase("RRB_DOMESTIC")) {
				checkProcess = this.rupaySettlementService.validateSettlementProcessRRB(beanObj);
			} else {
				checkProcess = this.rupaySettlementService.validateSettlementProcessRRBINT(beanObj);
			}
			if (checkProcess)
				return "Settlement for selected date is already processed";
			boolean executeFlag = false;
			if (beanObj.getSubcategory().equalsIgnoreCase("RRB_DOMESTIC")) {
				executeFlag = this.rupaySettlementService.processSettlementRRB(beanObj);
			} else {
				executeFlag = this.rupaySettlementService.processSettlementRRBINT(beanObj);
			}
			if (executeFlag)
				return "Settlement Processing completed";
			return "Settlement Processing Failed";
		}
		if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC") && beanObj.getCycle().equalsIgnoreCase("5")) {
			checkProcFlag = this.nfsSettlementService.checkSettlementRupay2(beanObj);
		} else if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
			checkProcFlag = this.nfsSettlementService.checkSettlementRupay(beanObj);

		} else {
			checkProcFlag = this.nfsSettlementService.checkSettlementRupayINT(beanObj);
		}
		if (checkProcFlag) {

			if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC") && beanObj.getCycle().equalsIgnoreCase("5")) {
				checkProcess = this.rupaySettlementService.validateSettlementProcess2(beanObj);
			} else if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
				checkProcess = this.rupaySettlementService.validateSettlementProcess(beanObj);
			} else {

				checkProcess = this.rupaySettlementService.validateSettlementProcessINT(beanObj);
			}
			if (checkProcess)
				return "Settlement for selected date is already processed";
			boolean executeFlag = false;
			if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC") && beanObj.getCycle().equalsIgnoreCase("5")) {
				executeFlag = this.rupaySettlementService.processSettlement2(beanObj);
			} else if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
				executeFlag = this.rupaySettlementService.processSettlement(beanObj);
			} else {
				executeFlag = this.rupaySettlementService.processSettlementINT(beanObj);
			}
			if (executeFlag)
				return "Settlement Processing completed";
			return "Settlement Processing Failed";
		}
		return "Settlement File is not uploaded";
	}

	@RequestMapping(value = { "ValidateRupaySettlement" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ValidateRupaySettlement(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** RupaySettlementController.ValidateRupaySettlement post Start ****");
		logger.info("ValidateRupaySettlement POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		System.out.println("File name is " + beanObj.getFileName());
		boolean checkFlag = false;
		return "success";
	}

	@RequestMapping(value = { "DownloadSettlement" }, method = { RequestMethod.POST })
	public String DownloadSettlement(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model)
			throws Exception {
		logger.info("***** RupaySettlementController.DownloadSettlement post Start ****");
		logger.info("DownloadSettlement POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		String fileName = "", zipName = "";
		System.out.println("File name is " + beanObj.getFileName());
		boolean executionFlag = false;
		List<Object> Excel_data = new ArrayList();
		if (beanObj.getSubcategory().equalsIgnoreCase("RRB_DOMESTIC")) {
			if (beanObj.getTTUM_TYPE().contains("EXCEL")) {
				Excel_data = this.rupaySettlementService.getSettlementDataRRB(beanObj);
			} else {
				List<Object> TTUMData = new ArrayList();
				logger.info("Created by is " + Createdby);
				beanObj.setCreatedBy(Createdby);
				logger.info("File Name is " + beanObj.getFileName());
				boolean executed = false;
				TTUMData = this.SETTLTTUMSERVICE.getDailyNFSTTUMDataRupayRRB(beanObj);
				fileName = "RUPAY_RRB_SETTLEMENT" + beanObj.getCycle() + "TTUM"
						+ beanObj.getFileDate().replaceAll("/", "") + beanObj.getCycle() + "_VAL.txt";
				String stPath = OUTPUT_FOLDER;
				logger.info("TEMP_DIR" + stPath);
				GenerateUCOTTUM obj = new GenerateUCOTTUM();
				stPath = obj.checkAndMakeDirectory(beanObj.getFileDate().replaceAll("/", ""), beanObj.getSubcategory());
				obj.generateADJTTUMFilesRupay(stPath, fileName, 1, TTUMData, "RUPAY", beanObj);
				logger.info("File is created");
				logger.info("File is created");
				zipName = "RUPAY_RRB_SETTLEMENT" + beanObj.getCycle() + "TTUM"
						+ beanObj.getFileDate().replaceAll("/", "") + beanObj.getCycle() + "_VAL.txt";
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
		} else if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
			if (beanObj.getTTUM_TYPE().contains("EXCEL")) {
				Excel_data = this.rupaySettlementService.getSettlementData(beanObj);
			} else {
				List<Object> TTUMData = new ArrayList();
				logger.info("Created by is " + Createdby);
				beanObj.setCreatedBy(Createdby);
				logger.info("File Name is " + beanObj.getFileName());
				boolean executed = false;
				TTUMData = this.SETTLTTUMSERVICE.getDailyNFSTTUMDataRupay(beanObj);
				fileName = "RUPAYSETTLEMENT" + beanObj.getCycle() + "TTUM" + beanObj.getFileDate().replaceAll("/", "")
						+ beanObj.getCycle() + "_VAL.txt";
				String stPath = OUTPUT_FOLDER;
				logger.info("TEMP_DIR" + stPath);
				GenerateUCOTTUM obj = new GenerateUCOTTUM();
				stPath = obj.checkAndMakeDirectory(beanObj.getFileDate().replaceAll("/", ""), beanObj.getSubcategory());
				obj.generateADJTTUMFilesRupay(stPath, fileName, 1, TTUMData, "RUPAY", beanObj);
				logger.info("File is created");
				logger.info("File is created");
				zipName = "RUPAYSETTLEMENT" + beanObj.getCycle() + "TTUM" + beanObj.getFileDate().replaceAll("/", "")
						+ beanObj.getCycle() + "_VAL.txt";
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
		} else if (beanObj.getSubcategory().equalsIgnoreCase("RRB_INTERNATIONAL")) {
			if (beanObj.getTTUM_TYPE().contains("EXCEL")) {
				Excel_data = this.rupaySettlementService.getSettlementDataINT(beanObj);
			} else {
				List<Object> TTUMData = new ArrayList();
				logger.info("Created by is " + Createdby);
				beanObj.setCreatedBy(Createdby);
				logger.info("File Name is " + beanObj.getFileName());
				boolean executed = false;
				TTUMData = this.SETTLTTUMSERVICE.getDailyNFSTTUMDataRupayINT(beanObj);
				fileName = "RUPAYSETTLEMENTINTTTUM" + beanObj.getFileDate().replaceAll("/", "-") + beanObj.getCycle()
						+ "_VAL.txt";
				String stPath = OUTPUT_FOLDER;
				logger.info("TEMP_DIR" + stPath);
				GenerateUCOTTUM obj = new GenerateUCOTTUM();
				stPath = obj.checkAndMakeDirectory(beanObj.getFileDate().replaceAll("/", "-"),
						beanObj.getSubcategory());
				obj.generateADJTTUMFilesRupay(stPath, fileName, 1, TTUMData, "RUPAY", beanObj);
				logger.info("File is created");
				logger.info("File is created");
				zipName = "RUPAYSETTLEMENTINTTTUM" + beanObj.getFileDate().replaceAll("/", "-") + beanObj.getCycle()
						+ "_VAL.txt";
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
		} else if (beanObj.getTTUM_TYPE().contains("EXCEL")) {
			Excel_data = this.rupaySettlementService.getSettlementDataINT(beanObj);
		} else {
			List<Object> TTUMData = new ArrayList();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			logger.info("File Name is " + beanObj.getFileName());
			boolean executed = false;
			TTUMData = this.SETTLTTUMSERVICE.getDailyNFSTTUMDataRupayINT(beanObj);
			fileName = "RUPAYSETTLEMENTINTTTUM" + beanObj.getFileDate().replaceAll("/", "-") + beanObj.getCycle()
					+ "_VAL.txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getFileDate().replaceAll("/", "-"), beanObj.getSubcategory());
			obj.generateADJTTUMFilesRupay(stPath, fileName, 1, TTUMData, "RUPAY", beanObj);
			logger.info("File is created");
			logger.info("File is created");
			zipName = "RUPAYSETTLEMENTINTTTUM" + beanObj.getFileDate().replaceAll("/", "-") + beanObj.getCycle()
					+ "_VAL.txt";
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
		model.addAttribute("ReportName", "Rupay_Settlement_" + beanObj.getSubcategory() + "_Report_"
				+ beanObj.getFileDate().replaceAll("/", "-") + beanObj.getCycle());
		model.addAttribute("data", Excel_data);
		logger.info("***** RupaySettlementController.DownloadSettlement POST End ****");
		return "GenerateRupaySettlementReport";
	}

	@RequestMapping(value = { "rollbackRUPAYSETTL" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackRUPAYSETTL(@RequestParam("fileDate") String fileDate,
			@RequestParam("subcategory") String subcategory, @RequestParam("cycle") String cycle)
			throws ParseException, Exception {
		NFSSettlementBean beanData = new NFSSettlementBean();
		beanData.setCycle(Integer.valueOf(cycle).intValue());
		beanData.setStSubCategory(subcategory);
		beanData.setDatepicker(fileDate);
		boolean result = false;
		logger.info("rollbackRUPAYSETTL " + fileDate + " " + subcategory + " " + cycle);
		if(subcategory.equalsIgnoreCase("DOMESTIC") && cycle.equalsIgnoreCase("5") ) {
			result = this.SETTLTTUMSERVICE.rollBackRupaySettl2(beanData);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}else
		if (subcategory.equalsIgnoreCase("DOMESTIC")) {
			result = this.SETTLTTUMSERVICE.rollBackRupaySettl(beanData);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}
		if (subcategory.equalsIgnoreCase("RRB_DOMESTIC")) {
			result = this.SETTLTTUMSERVICE.rollBackRupaySettlRRB(beanData);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}
		if (subcategory.equalsIgnoreCase("RRB_INTERNATIONAL")) {
			result = this.SETTLTTUMSERVICE.rollBackRupaySettlINT(beanData);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}
		result = this.SETTLTTUMSERVICE.rollBackRupaySettlINT(beanData);
		if (result)
			return "SUCCESSFULLY ROLLBACK";
		return "ALREADY ROLLBACK";
	}

	@RequestMapping(value = { "UpdateDollerRate" }, method = { RequestMethod.GET })
	public ModelAndView UpdateDollerRate(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** UpdateDollerRate.Get Start ****");
		RupayUploadBean rupaySettlementBean = new RupayUploadBean();
		logger.info("UpdateDollerRate GET");
		logger.info("in UpdateDollerRate" + category);
		modelAndView.addObject("category", category);
		modelAndView.addObject("rupaySettlementBean", rupaySettlementBean);
		modelAndView.setViewName("UpdateDollerRate");
		logger.info("***** RupaySettlementController.RupayFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "VisaSettlementProcess" }, method = { RequestMethod.GET })
	public ModelAndView VisaSettlementProcess(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** RupayFileUpload.Get Start ****");
		RupayUploadBean rupaySettlementBean = new RupayUploadBean();
		logger.info("RupayFileUpload GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		modelAndView.addObject("category", category);
		modelAndView.addObject("rupaySettlementBean", rupaySettlementBean);
		modelAndView.setViewName("VisaSettlementProcess");
		logger.info("***** RupaySettlementController.RupayFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "VisaSettlementProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String VisaSettlementProcessPost(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** VisaSettlementProcess.VisaSettlementProcess post Start ****" + beanObj.getFileType());
		logger.info("RupaySettlementProcess POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		System.out.println("File name is " + beanObj.getSubcategory());
		boolean checkProcess = false;
		boolean checkProcFlag = false;
		boolean executeFlag = false;
		HashMap<String, Object> output2 = null;
		if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
			checkProcess = this.rupaySettlementService.validateSettlementProcessVisa(beanObj);
		} else {
			checkProcess = this.rupaySettlementService.validateSettlementProcessVISAINT(beanObj);
		}
		if (checkProcess) {
			if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
				if (beanObj.getFileType().equalsIgnoreCase("ACQUIRER")) {
					output2 = this.rupaySettlementService.processSettlementVisaACQ(beanObj);
				} else {
					output2 = this.rupaySettlementService.processSettlementVisa(beanObj);
				}
			} else if (beanObj.getFileType().equalsIgnoreCase("ACQUIRER")) {
				output2 = this.rupaySettlementService.processSettlementVisaINTACQ(beanObj);
			} else {
				output2 = this.rupaySettlementService.processSettlementVisaINT(beanObj);
			}
			if (output2 != null && ((Boolean) output2.get("result")).booleanValue()) {
				System.out.println("msg " + output2.get("msg"));
				if (output2.get("msg").toString() != null) {
					String value = output2.get("msg").toString();
					if (value.contains("NOT"))
						return "File Not Uploaded";
					if (value.contains("ALL"))
						return "TTUM ALLREADY PROCESS";
					if (value.contains("SUCCESS"))
						return "PROCESS SUCCESSFULLY!";
					return output2.get("msg").toString();
				}
				return "Process Not Completed!!";
			}
			return output2.get("msg").toString();
		}
		return "Settlement for selected date and cycle is already processed";
	}

	@RequestMapping(value = { "UpdateDollerRate" }, method = { RequestMethod.POST })
	@ResponseBody
	public String UpdateDollerRate(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** VisaSettlementProcess.VisaSettlementProcess post Start ****" + beanObj.getFileType());
		logger.info("RupaySettlementProcess POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		boolean checkProcess = false;
		boolean checkProcFlag = false;
		boolean executeFlag = false;
		HashMap<String, Object> output2 = null;
		checkProcess = this.rupaySettlementService.InsertDollarRateVisa(beanObj);
		if (checkProcess) {
			System.out.println(" s");
			return "Success";
		}
		System.out.println(" w");
		return "Failed";
	}

	@RequestMapping(value = { "ValidateVisaSettlement" }, method = { RequestMethod.POST })
	@ResponseBody
	public String VisaSettlementValidate(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** RupaySettlementController.ValidateRupaySettlement post Start ****");
		logger.info("ValidateRupaySettlement POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		System.out.println("File name is " + beanObj.getFileName());
		boolean checkFlag = false;
		return "success";
	}

	@RequestMapping(value = { "rollbackVisaSETTL" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackVisaSETTL(@RequestParam("fileDate") String fileDate,
			@RequestParam("subcategory") String subcategory, @RequestParam("TTUM_TYPE") String cycle,
			@RequestParam("issacqtype") String issacqtype) throws ParseException, Exception {
		RupayUploadBean beanObj = new RupayUploadBean();
		beanObj.setTTUM_TYPE("");
		beanObj.setFileDate(fileDate);
		beanObj.setSubcategory(subcategory);
		beanObj.setFileType(issacqtype);
		boolean checkFlag = false;
		boolean checkFlag2 = false;
		logger.info("visa " + fileDate + " " + subcategory + " " + cycle + " " + issacqtype);
		HashMap<String, Object> output2 = null;
		if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
			checkFlag = this.rupaySettlementService.validateSettlementProcessVisa(beanObj);
		} else {
			checkFlag = this.rupaySettlementService.validateSettlementProcessVISAINT(beanObj);
		}
		if (checkFlag)
			return "Data not Present For Roll Back";
		if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
			if (beanObj.getFileType().equalsIgnoreCase("ACQUIRER")) {
				output2 = this.rupaySettlementService.processSettlementVisaRollbackACQ(beanObj);
			} else {
				output2 = this.rupaySettlementService.processSettlementVisaRollback(beanObj);
			}
		} else if (beanObj.getFileType().equalsIgnoreCase("ACQUIRER")) {
			output2 = this.rupaySettlementService.processSettlementVisaRollbackINTACQ(beanObj);
		} else {
			output2 = this.rupaySettlementService.processSettlementVisaRollbackINT(beanObj);
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue()) {
			System.out.println("msg " + output2.get("msg"));
			String value = output2.get("msg").toString();
			if (value.contains("NOT"))
				return "File Not Uploaded";
			if (value.contains("FAILED"))
				return "TTUM ALLREADY Rollback";
			if (value.contains("SUCCESS"))
				return "Rollback SUCCESSFULLY!";
			return output2.get("msg").toString();
		}
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "rollbackUpdateDollarRate" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackUpdateDollarRate(@RequestParam("fileDate") String fileDate,
			@RequestParam("issacqtype") String issacqtype) throws ParseException, Exception {
		RupayUploadBean beanObj = new RupayUploadBean();
		beanObj.setFileDate(fileDate);
		beanObj.setFileType(issacqtype);
		boolean checkFlag = false;
		boolean checkFlag2 = false;
		logger.info("visa " + fileDate + " " + issacqtype);
		HashMap<String, Object> output2 = null;
		output2 = this.rupaySettlementService.rollbackUpdateDollarRate(beanObj);
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue()) {
			System.out.println("msg " + output2.get("msg"));
			String value = output2.get("msg").toString();
			if (value.contains("NOT"))
				return "File Not Uploaded";
			if (value.contains("FAILED"))
				return "TTUM ALLREADY Rollback";
			if (value.contains("SUCCESS"))
				return "Rollback SUCCESSFULLY!";
			return output2.get("msg").toString();
		}
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "rollbackRUPAYSETTLQsparc" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackRUPAYSETTLQsparc(@RequestParam("fileDate") String fileDate,
			@RequestParam("subcategory") String subcategory, @RequestParam("cycle") String cycle)
			throws ParseException, Exception {
		NFSSettlementBean beanData = new NFSSettlementBean();
		beanData.setCycle(Integer.valueOf(cycle).intValue());
		beanData.setStSubCategory(subcategory);
		beanData.setDatepicker(fileDate);
		boolean result = false;
		logger.info("rollbackRUPAYSETTLQsparc " + fileDate + " " + subcategory + " " + cycle);
		if (subcategory.contains("DOMESTIC")) {
			result = this.SETTLTTUMSERVICE.rollBackRupaySettlQsparc(beanData);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}
		result = this.SETTLTTUMSERVICE.rollBackRupaySettlQsparcINT(beanData);
		if (result)
			return "SUCCESSFULLY ROLLBACK";
		return "ALREADY ROLLBACK";
	}

	@RequestMapping(value = { "rollbackRUPAYADJ" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackRUPAYADJ(@RequestParam("fileDate") String fileDate,
			@RequestParam("subcategory") String subcategory, @RequestParam("ADJTYPE") String ADJTYPE)
			throws ParseException, Exception {
		NFSSettlementBean beanData = new NFSSettlementBean();
		beanData.setAdjCategory(ADJTYPE);
		beanData.setStSubCategory(subcategory);
		beanData.setDatepicker(fileDate);
		boolean result = false;
		logger.info("rollbackRUPAYSETTL " + fileDate + " " + subcategory + " " + ADJTYPE);
		if (ADJTYPE.equalsIgnoreCase("REFUND")) {
			result = this.SETTLTTUMSERVICE.rollBackRupayRefund(beanData);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}
		if (subcategory.contains("RUPAY")) {
			result = this.SETTLTTUMSERVICE.rollBackRupayADJ(beanData);
			if (result)
				return "SUCCESSFULLY ROLLBACK";
			return "ALREADY ROLLBACK";
		}
		result = this.SETTLTTUMSERVICE.rollBackRupayADJ(beanData);
		if (result)
			return "SUCCESSFULLY ROLLBACK";
		return "ALREADY ROLLBACK";
	}

	@RequestMapping(value = { "QsparcSettlementProcess" }, method = { RequestMethod.GET })
	public ModelAndView QsparcSettlementProcessGet(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** RupayFileUpload.Get Start ****");
		RupayUploadBean rupaySettlementBean = new RupayUploadBean();
		logger.info("RupayFileUpload GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		modelAndView.addObject("category", category);
		modelAndView.addObject("rupaySettlementBean", rupaySettlementBean);
		modelAndView.setViewName("QsparcSettlementProcess");
		logger.info("***** RupaySettlementController.RupayFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "QsparcSettlementProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String QsparcSettlementProcessPost(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** RupaySettlementController.RupaySettlementProcess post Start ****");
		logger.info("RupaySettlementProcess POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		System.out.println("File name is " + beanObj.getFileName());
		boolean checkProcess = false;
		boolean checkProcFlag = false;
		if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
			checkProcFlag = this.nfsSettlementService.checkSettlementQsparc(beanObj);
		} else {
			checkProcFlag = this.nfsSettlementService.checkSettlementQsparcINT(beanObj);
		}
		if (checkProcFlag) {
			if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
				checkProcess = this.rupaySettlementService.validateSettlementProcessQsparc(beanObj);
			} else {
				checkProcess = this.rupaySettlementService.validateSettlementProcessQsparcINT(beanObj);
			}
			if (checkProcess)
				return "Settlement for selected date and cycle is already processed";
			boolean executeFlag = false;
			if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
				executeFlag = this.rupaySettlementService.processSettlementQsparc(beanObj);
			} else {
				executeFlag = this.rupaySettlementService.processSettlementQsparcINT(beanObj);
			}
			if (executeFlag)
				return "Settlement Processing completed";
			return "Settlement Processing Failed";
		}
		return "Settlement File is not uploaded";
	}

	@RequestMapping(value = { "ValidateQsparcSettlement" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ValidateQsparcSettlement(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** RupaySettlementController.ValidateRupaySettlement post Start ****");
		logger.info("ValidateRupaySettlement POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		System.out.println("File name is " + beanObj.getFileName());
		boolean checkFlag = false;
		if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
			checkFlag = this.rupaySettlementService.validateSettlementProcessQsparc(beanObj);
		} else {
			checkFlag = this.rupaySettlementService.validateSettlementProcessQsparcINT(beanObj);
		}
		if (checkFlag)
			return "success";
		return "Settlement is not processed for selected date and cycle";
	}

	@RequestMapping(value = { "DownloadSettlementQsparc" }, method = { RequestMethod.POST })
	public String DownloadSettlementQsparc(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model)
			throws Exception {
		logger.info("***** RupaySettlementController.DownloadSettlement post Start ****");
		logger.info("DownloadSettlement POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		String fileName = "";
		String zipName = "";
		System.out.println("File name is " + beanObj.getFileName());
		boolean executionFlag = false;
		List<Object> Excel_data = new ArrayList();
		if (beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
			if (beanObj.getTTUM_TYPE().contains("EXCEL")) {
				Excel_data = this.rupaySettlementService.getSettlementDataQsparc(beanObj);
			} else {
				List<Object> TTUMData = new ArrayList();
				logger.info("Created by is " + Createdby);
				beanObj.setCreatedBy(Createdby);
				logger.info("File Name is " + beanObj.getFileName());
				boolean executed = false;
				TTUMData = this.SETTLTTUMSERVICE.getDailyNFSTTUMDataRupayQSPARC(beanObj);
				fileName = "RUPAYQSPARCSETTLEMENTTTUM" + beanObj.getFileDate().replaceAll("/", "") + beanObj.getCycle()
						+ "_VAL.txt";
				String stPath = OUTPUT_FOLDER;
				logger.info("TEMP_DIR" + stPath);
				GenerateUCOTTUM obj = new GenerateUCOTTUM();
				stPath = obj.checkAndMakeDirectory(beanObj.getFileDate().replaceAll("/", ""), beanObj.getSubcategory());
				obj.generateADJTTUMFilesRupay(stPath, fileName, 1, TTUMData, "RUPAY", beanObj);
				logger.info("File is created");
				logger.info("File is created");
				zipName = "RUPAYQSPARCSETTLEMENTTTUM" + beanObj.getFileDate().replaceAll("/", "") + beanObj.getCycle()
						+ "_VAL.txt";
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
		} else if (beanObj.getTTUM_TYPE().contains("EXCEL")) {
			Excel_data = this.rupaySettlementService.getSettlementDataQsparcINT(beanObj);
		} else {
			List<Object> TTUMData = new ArrayList();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			logger.info("File Name is " + beanObj.getFileName());
			boolean executed = false;
			TTUMData = this.SETTLTTUMSERVICE.getDailyNFSTTUMDataRupayQSPARCINT(beanObj);
			fileName = "RUPAYQSPARCSETTLEMENTINTTTUM" + beanObj.getFileDate().replaceAll("/", "") + beanObj.getCycle()
					+ "_VAL.txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getFileDate().replaceAll("/", ""), beanObj.getSubcategory());
			obj.generateADJTTUMFilesRupay(stPath, fileName, 1, TTUMData, "RUPAY_QSPARC", beanObj);
			logger.info("File is created");
			logger.info("File is created");
			zipName = "RUPAYQSPARCSETTLEMENTINTTTUM" + beanObj.getFileDate().replaceAll("/", "") + beanObj.getCycle()
					+ "_VAL.txt";
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
		model.addAttribute("ReportName", "QSPARC_Settlement_" + beanObj.getSubcategory() + "_Report_"
				+ beanObj.getFileDate().replaceAll("/", "") + beanObj.getCycle());
		model.addAttribute("data", Excel_data);
		logger.info("***** RupaySettlementController.DownloadSettlement POST End ****");
		return "GenerateRupaySettlementReport";
	}

	@RequestMapping(value = { "RupaySettlementProcessTTUM" }, method = { RequestMethod.GET })
	public ModelAndView RupaySettlementTTUMGet(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("HELLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
		RupayUploadBean rupaySettlementBean = new RupayUploadBean();
		logger.info("RupaySettlementTTUM GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		modelAndView.addObject("category", category);
		modelAndView.addObject("rupaySettlementBean", rupaySettlementBean);
		modelAndView.setViewName("RupaySettlementProcessTTUM");
		logger.info("***** RupaySettlementController.RupaySettlementTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "FundingTtum" }, method = { RequestMethod.GET })
	public ModelAndView FundingTtumGet(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** FundingTtum.Get Start ****");
		RupayUploadBean rupaySettlementBean = new RupayUploadBean();
		logger.info("FundingTtum GET");
		String display = "";
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		modelAndView.addObject("rupaySettlementBean", rupaySettlementBean);
		modelAndView.setViewName("FundingTtum");
		logger.info("***** RupaySettlementController.FundingTtum GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "RupaySettlementTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public String RupaySettlementTTUMPost(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** RupaySettlementController.RupaySettlementTTUMPost post Start ****");
		logger.info("RupaySettlementTTUMPost POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		System.out.println("inside the ttum processs");
		String ddate = this.generalutil.eoddate(beanObj.getFileDate());
		beanObj.setCreatedBy(Createdby);
		boolean executionFlag = false;
		logger.info("Subcategory is " + beanObj.getSubcategory());
		System.out.println("eod started here ");
		boolean datapresent = this.rupaySettlementService.checkdata(beanObj);
		if (datapresent) {
			boolean checkTTUMProcess = this.rupaySettlementService.validateSettlementTTUM(beanObj).booleanValue();
			if (!checkTTUMProcess) {
				if (beanObj.getSubcategory().equals("DOMESTIC")) {
					if (this.rupaySettlementService.processSettlementTTUM(beanObj))
						return "Settlement is Completed Successfully! \n Please download Reports";
					return "Issue while processing TTUM";
				}
				if (this.rupaySettlementService.IRGCSprocessSettlementTTUM(beanObj))
					return "Settlement is Completed Successfully! \n Please download Reports";
				return "Issue while processing TTUM";
			}
			return "Settlement already processed please download the report";
		}
		return "Settlement file is not uploaded";
	}

	@ResponseBody
	@RequestMapping(value = { "ValidateSettlementTTUM" }, method = { RequestMethod.POST })
	public String ValidateSettlementTTUM(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** RupaySettlementController.ValidateSettlementTTUM post Start ****");
		logger.info("ValidateSettlementTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		System.out.println("inside download validate");
		beanObj.setCreatedBy(Createdby);
		Boolean checkFlag = this.rupaySettlementService.validateSettlementTTUM(beanObj);
		if (checkFlag.booleanValue())
			return "success";
		return "Settlement TTUM is not processed for selected date and cycle";
	}

	@RequestMapping(value = { "DownloadRupaySettlementTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadSettlementTTUM(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession, Model model)
			throws Exception {
		logger.info("***** RupaySettlementController.DownloadSettlement post Start ****");
		logger.info("DownloadSettlement POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		System.out.println("inside the download");
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		boolean executionFlag = false;
		List<Object> Excel_data = new ArrayList();
		List<Object> TTUMData = new ArrayList();
		TTUMData = this.rupaySettlementService.getSettlementTTUMData(beanObj);
		String fileName = "RUPAY_SETTLEMENT_TTUM1.txt";
		String stPath = System.getProperty("java.io.tmpdir");
		logger.info("TEMP_DIR" + stPath);
		GenerateUCOTTUM obj = new GenerateUCOTTUM();
		stPath = obj.checkAndMakeDirectory(beanObj.getFileDate(), "RUPAY");
		List<Object> TTUM_data = (List<Object>) TTUMData.get(0);
		obj.generateTTUMFile(stPath, fileName, TTUM_data);
		logger.info("File is created");
		List<String> Column_list = new ArrayList<>();
		Column_list.add("SR_NO");
		Column_list.add("ACCOUNT_NUMBER");
		Column_list.add("PART_TRAN_TYPE");
		Column_list.add("DRCR");
		Column_list.add("TRANSACTION_AMOUNT");
		Column_list.add("TRANSACTION_PARTICULAR");
		Excel_data.add(Column_list);
		Excel_data.add(TTUMData);
		fileName = "RUPAY_SETTLEMENT_TTUM.xls";
		String zipName = "Rupay_Settlement.zip";
		obj.generateExcelTTUM(stPath, fileName, Excel_data, "SETTLEMENT", zipName);
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

	@RequestMapping(value = { "RupaySettlementRectify" }, method = { RequestMethod.POST })
	@ResponseBody
	public String SettlementRectify(@ModelAttribute("nfsSettlementBean") RupayUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model)
			throws Exception {
		logger.info("***** SettlementRectify.post Start ****");
		HashMap<String, Object> output = null;
		try {
			logger.info("SettlementRectify : Post");
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			boolean result = this.rupaySettlementService.validateSettlementDiff(beanObj);
			if (result) {
				output = this.rupaySettlementService.validateDiffAmount(beanObj);
				if (output != null && ((Boolean) output.get("result")).booleanValue()) {
					boolean rectify_flag = this.rupaySettlementService.processRectification(beanObj);
					if (rectify_flag)
						return "Amount is Rectified !";
					return "Issue while rectification !";
				}
				return output.get("msg").toString();
			}
			return "No data for rectification !";
		} catch (Exception e) {
			logger.info("Exception in NFSSettlementController " + e);
			return "Error Occurred in getting Difference";
		}
	}

	@RequestMapping(value = { "NCMCFileUpload" }, method = { RequestMethod.GET })
	public ModelAndView NCMCFileUploadGet(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** RupayFileUpload.Get Start ****");
		RupayUploadBean rupaySettlementBean = new RupayUploadBean();
		logger.info("RupayFileUpload GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		modelAndView.addObject("category", category);
		modelAndView.addObject("rupaySettlementBean", rupaySettlementBean);
		modelAndView.setViewName("NCMCFileUpload");
		logger.info("***** RupaySettlementController.RupayFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "NCMCFileUpload" }, method = { RequestMethod.POST })
	@ResponseBody
	public String NCMCFileUploadPost(@ModelAttribute("rupaySettlementBean") RupayUploadBean rupayBean,
			HttpServletRequest request, @RequestParam("file") MultipartFile file, HttpSession httpSession, Model model,
			ModelAndView modelAndView, RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** RupayFileUpload.post Start ****");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		rupayBean.setCreatedBy(Createdby);
		logger.info("Subcategory is " + rupayBean.getSubcategory());
		if (file.getOriginalFilename().contains("-")) {
			String[] fileName = file.getOriginalFilename().split("\\.");
			logger.info("Cycle is " + fileName[0].substring(fileName[0].length() - 1, fileName[0].length()));
			rupayBean.setCycle(fileName[0].substring(fileName[0].length() - 1, fileName[0].length()));
		}
		if (this.rupaySettlementService.checkNCMCFileUploaded(rupayBean))
			return "File is already uploaded!";
		Boolean readFlag = Boolean.valueOf(false);
		if (rupayBean.getSubcategory().equalsIgnoreCase("DOMESTIC")) {
			readFlag = Boolean.valueOf(this.rupaySettlementService.readNCMCFile(rupayBean, file));
		} else if (rupayBean.getSubcategory().equalsIgnoreCase("INTERNATIONAL")) {
			readFlag = Boolean.valueOf(this.rupaySettlementService.readNCMCINTFile(rupayBean, file));
		}
		if (readFlag.booleanValue())
			return "File Uploaded Successfully";
		return "Issue while uploading file";
	}

	@RequestMapping(value = { "QsparcTTUM" }, method = { RequestMethod.GET })
	public ModelAndView RupayQsparcTTUMGet(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("QPARc QPARc QPARc QPARc QPARc QPARc QPARc QPARc QPARc ");
		RupayUploadBean rupaySettlementBean = new RupayUploadBean();
		modelAndView.addObject("category", category);
		modelAndView.addObject("rupaySettlementBean", rupaySettlementBean);
		modelAndView.setViewName("QsparcTTUM");
		logger.info("***** RupaySettlementController.RupaySettlementTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "PresentmentFileUpload" }, method = { RequestMethod.GET })
	public ModelAndView presentmentFileUploadGet(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** RupayFileUpload.Get Start ****");
		RupayUploadBean rupaySettlementBean = new RupayUploadBean();
		logger.info("RupayFileUpload GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		modelAndView.addObject("category", category);
		modelAndView.addObject("rupaySettlementBean", rupaySettlementBean);
		modelAndView.setViewName("PresentmentFileUpload");
		logger.info("***** RupaySettlementController.PresentmentFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "PresentmentFileUpload" }, method = { RequestMethod.POST })
	@ResponseBody
	public String PresentmentFileUploadPost(@ModelAttribute("rupaySettlementBean") RupayUploadBean rupayBean,
			HttpServletRequest request, @RequestParam("file") MultipartFile file, HttpSession httpSession, Model model,
			ModelAndView modelAndView, RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** RupayFileUpload.post Start ****");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		rupayBean.setCreatedBy(Createdby);
		logger.info("Subcategory is " + rupayBean.getSubcategory());
		logger.info(file.getOriginalFilename());
		logger.info(rupayBean.getCycle());
		boolean okupload = false;
		if (rupayBean.getSubcategory().equals("DOMESTIC"))
			okupload = this.rupaySettlementService.checkpresentmenetupload(rupayBean);
		String response = "";
		if (file.getOriginalFilename().contains("IRGCS")) {
			response = this.rupaySettlementService.IntuploadPresentmentFile(rupayBean, file);
		} else if (okupload) {
			response = this.rupaySettlementService.uploadPresentmentFile(rupayBean, file);
		} else {
			response = "file is already uploaded";
		}
		return response;
	}

	@RequestMapping(value = { "RupayQsparcTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public String RupayQspracPost(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		beanObj.setCreatedBy(Createdby);
		boolean executionFlag = false;
		logger.info("Subcategory is " + beanObj.getSubcategory());
		System.out.println("inside the Qsparc");
		boolean checkTTUMProcess = this.rupaySettlementService.validateQsparcTTUM(beanObj);
		if (!checkTTUMProcess) {
			if (this.rupaySettlementService.processSettlementTTUMQsparc(beanObj))
				return "QSPARC is Completed Successfully! \n Please download Reports";
			return "Issue while processing TTUM";
		}
		return "Settlement is not processed for selected date and cycle";
	}

	@RequestMapping(value = { "VisaDownloadRupaySettlementTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public void VisaDownloadSettlementTTUM(@ModelAttribute("mastercardUploadBean") RupayUploadBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession, Model model)
			throws Exception {
		logger.info("***** RupaySettlementController.DownloadSettlement post Start ****");
		logger.info("DownloadSettlement POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		System.out.println("inside the download");
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		boolean executionFlag = false;
		List<Object> Excel_data = new ArrayList();
		List<Object> TTUMData = new ArrayList();
		TTUMData = this.rupaySettlementService.visagetSettlementTTUMData(beanObj);
		String fileName = "VISA_SETTLEMENT_TTUM1.txt";
		String stPath = OUTPUT_FOLDER;
		logger.info("TEMP_DIR" + stPath);
		GenerateUCOTTUM obj = new GenerateUCOTTUM();
		stPath = obj.checkAndMakeDirectory(beanObj.getFileDate(), "VISA");
		List<Object> TTUM_data = (List<Object>) TTUMData.get(0);
		System.out.println("FILENAME IN TTUM GENERATION IS" + fileName);
		obj.generateTTUMFile(stPath, fileName, TTUM_data);
		logger.info("File is created");
		List<String> Column_list = new ArrayList<>();
		Column_list.add("SR_NO");
		Column_list.add("ACCOUNT_NUMBER");
		Column_list.add("PART_TRAN_TYPE");
		Column_list.add("TRANSACTION_AMOUNT");
		Column_list.add("TRANSACTION_PARTICULAR");
		Excel_data.add(Column_list);
		Excel_data.add(TTUMData);
		fileName = "VISA_SETTLEMENT_TTUM.xls";
		String zipName = "VISA_Settlement.zip";
		obj.generateExcelTTUM(stPath, fileName, Excel_data, "SETTLEMENT", zipName);
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

	@RequestMapping(value = { "moneyaddupload" }, method = { RequestMethod.GET })
	public ModelAndView moneyadduploadget(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** moneyaddupload.Get Start ****");
		RupayUploadBean rupaySettlementBean = new RupayUploadBean();
		logger.info("RupayFileUpload GET");
		String display = "";
		logger.info("in GetHeaderList" + category);
		modelAndView.addObject("category", category);
		modelAndView.addObject("rupaySettlementBean", rupaySettlementBean);
		modelAndView.setViewName("moneyaddupload");
		logger.info("***** RupaySettlementController.moneyaddupload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "moneyaddupload" }, method = { RequestMethod.POST })
	@ResponseBody
	public String moneyadduploadpost(@ModelAttribute("rupaySettlementBean") RupayUploadBean rupayBean,
			HttpServletRequest request, @RequestParam("file") MultipartFile file, HttpSession httpSession, Model model,
			ModelAndView modelAndView, RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** moneyaddupload.post Start ****");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		rupayBean.setCreatedBy(Createdby);
		logger.info("Subcategory is " + rupayBean.getSubcategory());
		logger.info(file.getOriginalFilename());
		logger.info(rupayBean.getCycle());
		boolean okupload = false;
		if (rupayBean.getSubcategory().equals("DOMESTIC"))
			okupload = this.rupaySettlementService.checkmoneyaddupload(rupayBean);
		String response = "";
		if (okupload) {
			response = this.rupaySettlementService.uploadmoneyadd(rupayBean, file);
		} else {
			response = "file is already uploaded";
		}
		return response;
	}
}
