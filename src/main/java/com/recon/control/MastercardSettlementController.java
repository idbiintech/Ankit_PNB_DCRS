package com.recon.control;

import com.recon.model.LoginBean;
import com.recon.model.MastercardUploadBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.ISourceService;
import com.recon.service.MastercardService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MastercardSettlementController {
	public static final String OUTPUT_FOLDER = String.valueOf(System.getProperty("catalina.home")) + File.separator
			+ "FUNDING";

	private static final Logger logger = Logger.getLogger(com.recon.control.MastercardSettlementController.class);

	@Autowired
	SettlementTTUMService SETTLTTUMSERVICE;

	@Autowired
	ISourceService iSourceService;

	@Autowired
	RupayTTUMService rupayTTUMService;

	@Autowired
	MastercardService mastercardService;

	public static final String CATALINA_HOME = "catalina.home";

	@RequestMapping(value = { "MastercardT057Upload" }, method = { RequestMethod.GET })
	public ModelAndView MastercardT057Upload(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
		logger.info("***** MastercardFileUpload.Get Start ****");
		logger.info("MastercardFileUpload GET");
		MastercardUploadBean beanObj = new MastercardUploadBean();
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.setViewName("MastercardT057Upload");
		modelAndView.addObject("mastercardUploadBean", beanObj);
		logger.info("***** MastercardSettlementController.MastercardFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "MastercardINVOICEUpload" }, method = { RequestMethod.GET })
	public ModelAndView MastercardINVOICEUpload(ModelAndView modelAndView, HttpServletRequest request)
			throws Exception {
		logger.info("***** MastercardFileUpload.Get Start ****");
		logger.info("MastercardFileUpload GET");
		MastercardUploadBean beanObj = new MastercardUploadBean();
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.setViewName("MastercardINVOICEUpload");
		modelAndView.addObject("mastercardUploadBean", beanObj);
		logger.info("***** MastercardSettlementController.MastercardFileUpload GET End ****");
		return modelAndView;
	}
	@RequestMapping(value = { "EODFileUpload" }, method = { RequestMethod.GET })
	public ModelAndView EODFileUpload(ModelAndView modelAndView, HttpServletRequest request)
			throws Exception {
		logger.info("***** EODFileUpload.Get Start ****");
		logger.info("EODFileUpload GET");
		MastercardUploadBean beanObj = new MastercardUploadBean();
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.setViewName("EODFileUpload");
		modelAndView.addObject("mastercardUploadBean", beanObj);
		logger.info("***** MastercardSettlementController.MastercardFileUpload GET End ****");
		return modelAndView;
	}
	@RequestMapping(value = { "MastercardPDFINVOICEUpload" }, method = { RequestMethod.GET })
	public ModelAndView MastercardPDFINVOICEUpload(ModelAndView modelAndView, HttpServletRequest request)
			throws Exception {
		logger.info("***** MastercardFileUpload.Get Start ****");
		logger.info("MastercardFileUpload GET");
		MastercardUploadBean beanObj = new MastercardUploadBean();
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.setViewName("MastercardPDFINVOICEUpload");
		modelAndView.addObject("mastercardUploadBean", beanObj);
		logger.info("***** MastercardSettlementController.MastercardFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "ATMMASTERUpload" }, method = { RequestMethod.GET })
	public ModelAndView ATMMASTERUpload(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
		logger.info("***** ATMMASTERUpload.Get Start ****");
		logger.info("ATMMASTERUpload GET");
		MastercardUploadBean beanObj = new MastercardUploadBean();
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.setViewName("ATMMASTERUpload");
		modelAndView.addObject("mastercardUploadBean", beanObj);
		logger.info("***** ATMMASTERUpload.ATMMASTERUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "MastercardFileUpload" }, method = { RequestMethod.GET })
	public ModelAndView tadFileUpload(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
		logger.info("***** MastercardFileUpload.Get Start ****");
		logger.info("MastercardFileUpload GET");
		MastercardUploadBean beanObj = new MastercardUploadBean();
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.setViewName("MastercardFileUpload");
		modelAndView.addObject("mastercardUploadBean", beanObj);
		logger.info("***** MastercardSettlementController.MastercardFileUpload GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "MastercardFileUpload" }, method = { RequestMethod.POST })
	@ResponseBody
	public String tadFileUploadPost(@ModelAttribute("mastercardUploadBean") MastercardUploadBean beanObj,
			@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpSession httpSession)
			throws Exception {
		try {
			HashMap<String, Object> validations;
			logger.info("***** MastercardFileUpload.post Start ****");
			logger.info("MastercardFileUpload POST");
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			System.out.println("File name is " + beanObj.getFileName());
			if (beanObj.getFileName().equalsIgnoreCase("TAD")) {
				HashMap<String, Object> outputs = this.mastercardService.TADfileReading(file, beanObj);
				if (outputs != null) {
					logger.info("***** MastercardSettlementController.MastercardFileUpload POST End ****");
					return outputs.get("msg").toString();
				}
				return "Problem Occurred while Uploading";
			}
			if (beanObj.getFileName().equalsIgnoreCase("TA")) {
				HashMap<String, Object> outputs = this.mastercardService.TAfileReading(file, beanObj);
				if (outputs != null) {
					logger.info("***** MastercardSettlementController.MastercardFileUpload POST End ****");
					return outputs.get("msg").toString();
				}
				return "Problem Occurred while Uploading";
			}
			if (beanObj.getFileName().equalsIgnoreCase("140") || beanObj.getFileName().equalsIgnoreCase("461")
					|| beanObj.getFileName().equalsIgnoreCase("INVOICEPDF")
					|| beanObj.getFileName().equalsIgnoreCase("INVOICE")
					|| beanObj.getFileName().equalsIgnoreCase("INVOICETXT")
					|| beanObj.getFileName().equalsIgnoreCase("EODTXT")
					|| beanObj.getFileName().equalsIgnoreCase("ATMMASTER")) {
				validations = this.mastercardService.checkFileUpload(beanObj, file.getOriginalFilename());
			} else {
				validations = this.mastercardService.checkFileUploadE057(beanObj, file.getOriginalFilename());
			}
			if (validations != null && ((Boolean) validations.get("result")).booleanValue()) {
				HashMap<String, Object> outputs = this.mastercardService.file140Reading(file, beanObj);
				if (outputs != null) {
					logger.info("***** MastercardSettlementController.Mastercard140FileUploadPost POST End ****");
					System.out.println("master count" + outputs.get("msg").toString());
					return outputs.get("msg").toString();
				}
				return "Problem Occurred while Uploading";
			}
			return validations.get("msg").toString();
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return "Exception";
		}
	}

	@RequestMapping(value = { "GenerateMASTERCARDTTUM" }, method = { RequestMethod.GET })
	public ModelAndView GenerateMASTERCARDTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateMASTERCARDTTUM Start Get method  ****");
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
		modelAndView.setViewName("GenerateMCTTUM");
		logger.info("***** GenerateMASTERCARDTTUM.GenerateMASTERCARDTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateUnmatchedTTUMMASTERCARD" }, method = { RequestMethod.POST })
	@ResponseBody
	public String GenerateUnmatchedTTUMMASTERCARD(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.GenerateUnmatchedTTUMMASTERCARD post Start ****");
		logger.info("GenerateUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " localDate is " + beanObj.getLocalDate());
		logger.info("filedate is " + beanObj.getFileDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		System.out.println("st sub category for domestic tum is " + beanObj.getStSubCategory());
		System.out.println("FILENAme is " + beanObj.getCategory());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		beanObj.setFileDate(beanObj.getLocalDate());
		HashMap<String, Object> output = this.rupayTTUMService.checkTTUMProcessedMASTERCARD(beanObj);
		System.out.println("CHECK WHETHER RECON IS PROCESSED OR NOT");
		if (output != null && !((Boolean) output.get("result")).booleanValue()) {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER_DOM")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("")) {
					executed = this.rupayTTUMService.runTTUMProcessRUPAY(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_CREDIT")) {
					executed = this.rupayTTUMService.runTTUMProcessMC2(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_DEBIT")) {
					executed = this.rupayTTUMService.runTTUMProcessMC3(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CROSS")) {
					executed = this.rupayTTUMService.UnmatchedTTUMProcMCCROSS(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					executed = this.rupayTTUMService.runTTUMProcessRUPAY6(beanObj);
				} else {
					executed = this.rupayTTUMService.UnmatchedTTUMProcMCCROSS(beanObj);
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER_INT")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("")) {
					executed = this.rupayTTUMService.runTTUMProcessRUPAY(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_CREDIT")) {
					executed = this.rupayTTUMService.runTTUMProcessMC2POSINT(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO_DEBIT")) {
					executed = this.rupayTTUMService.runTTUMProcessMC3POSINT(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CROSS")) {
					executed = this.rupayTTUMService.UnmatchedTTUMProcMCCROSS(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					executed = this.rupayTTUMService.runTTUMProcessRUPAY6(beanObj);
				} else {
					executed = this.rupayTTUMService.UnmatchedTTUMProcMCCROSS(beanObj);
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ISSUER")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
					executed = this.rupayTTUMService.runTTUMProcessQSPARC4(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					executed = this.rupayTTUMService.runTTUMProcessMC2POS(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					executed = this.rupayTTUMService.runTTUMProcessMC3POS(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					executed = this.rupayTTUMService.runTTUMProcessMC5(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					executed = this.rupayTTUMService.runTTUMProcessRUPAY6(beanObj);
				}  else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ATMSURCHARGE")) {
					executed = this.rupayTTUMService.runTTUMProcessRUPAY10(beanObj);
				} else {
					executed = this.rupayTTUMService.UnmatchedTTUMProcMC(beanObj);
				}
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
				executed = this.rupayTTUMService.runTTUMProcessRUPAYINT(beanObj);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				executed = this.rupayTTUMService.runTTUMProcessRUPAY2INT(beanObj);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				executed = this.rupayTTUMService.runTTUMProcessRUPAY3INT(beanObj);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
				executed = this.rupayTTUMService.runTTUMProcessRUPAY5(beanObj);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
				executed = this.rupayTTUMService.runTTUMProcessRUPAY5(beanObj);
			} else {
				executed = this.rupayTTUMService.runTTUMProcessRUPAY4INT(beanObj);
			}
			if (executed)
				return "Processing Completed Successfully! \n Please download Report";
			return "Issue while processing!";
		}
		if (output != null)
			return output.get("msg").toString();
		return "Issue while validating TTUM Process";
	}

	@RequestMapping(value = { "checkTTUMProcessedMASTERCARD" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkTTUMProcessedMASTERCARD(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		try {
			logger.info("checkTTUMProcessedMASTERCARD: checkTTUMProcessed: Entry");
			beanObj.setFileDate(beanObj.getLocalDate());
			return "success";
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return "Exception";
		}
	}

	@RequestMapping(value = { "DownloadUnmatchedTTUMMASTERCARD" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadUnmatchedTTUMMASTERCARD(
			@ModelAttribute("nfsSettlementBean") UnMatchedTTUMBean nfsSettlementBean, HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model)
			throws Exception {
		String fileName = "";
		String zipName = "";
		logger.info("DownloadUnmatchedTTUMMASTERCARD POST" + nfsSettlementBean.getTTUMCategory() + " "
				+ nfsSettlementBean.getStSubCategory() + " " + nfsSettlementBean.getTypeOfTTUM() + " "
				+ nfsSettlementBean.getAcqtypeOfTTUM());
		if (nfsSettlementBean.getStSubCategory().equalsIgnoreCase("ISSUER"))
			nfsSettlementBean.setTypeOfTTUM(nfsSettlementBean.getAcqtypeOfTTUM());
		if (nfsSettlementBean.getTTUMCategory().contains("REPORT")) {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			nfsSettlementBean.setCreatedBy(Createdby);
			boolean executed = false;
			TTUMData = this.SETTLTTUMSERVICE.getTTUMMASTERCARD(nfsSettlementBean);
			if (nfsSettlementBean.getCategory().contains("RUPAY")) {
				fileName = "RUPAY" + nfsSettlementBean.getTypeOfTTUM() + "TTUM"
						+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			} else {
				fileName = "QSPARC" + nfsSettlementBean.getTypeOfTTUM() + "TTUM"
						+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			}
			String stPath = OUTPUT_FOLDER;
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getLocalDate().replaceAll("/", ""),
					nfsSettlementBean.getCategory());
			List<String> Column_list = new ArrayList<>();
			Map<String, String> table_Data = new HashMap<>();
			if (nfsSettlementBean.getStSubCategory().contains("ACQUIRER_DOM")
					|| nfsSettlementBean.getStSubCategory().contains("ACQUIRER_INT")) {
				if (nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("LORO_DEBIT")
						|| nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("LORO_CREDIT")) {
					Column_list.add("DR_CR");
					Column_list.add("DISPUTE_DATE");
					Column_list.add("BANK_NAME");
					Column_list.add("CARD_NO");
					Column_list.add("AC_NO");
					Column_list.add("DATE_OF_TXN");
					Column_list.add("AMOUNT");
					Column_list.add("AUTH_CODE");
					Column_list.add("NARRATION");
					Column_list.add("FILEDATE");
					Column_list.add("REMARKS");
				} else if (nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")
						|| nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
					Column_list.add("BUSINESS_DATE");
					Column_list.add("DR_CR");
					Column_list.add("CARD_NO");
					Column_list.add("TRACE_NO");
					Column_list.add("AC_NO");
					Column_list.add("TRAN_DATE");
					Column_list.add("TRAN_TIME");
					Column_list.add("CBS_AMOUNT");
					Column_list.add("NPCI_AMOUNT");
					Column_list.add("SUR");
					Column_list.add("NARRATION");
				} else if (nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("CROSS")) {
					Column_list.add("DR_CR");
					Column_list.add("BANK_NAME");
					Column_list.add("CARD_NO");
					Column_list.add("AC_NO");
					Column_list.add("DATE_OF_TXN");
					Column_list.add("AMOUNT");
					Column_list.add("AUTH_CODE");
					Column_list.add("REMARKS");
					Column_list.add("FILEDATE");
					Column_list.add("NARRATION");
				} else {
					Column_list.add("DR_CR");
					Column_list.add("DISPUTE_DATE");
					Column_list.add("BANK_NAME");
					Column_list.add("CARD_NO");
					Column_list.add("AC_NO");
					Column_list.add("DATE_OF_TXN");
					Column_list.add("TXN_TIME");
					Column_list.add("AMOUNT");
					Column_list.add("TRACE_NO");
					Column_list.add("NARRATION");
				}
			} else if (nfsSettlementBean.getTypeOfTTUM().contains("SURCHARGED")
					|| nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
				Column_list.add("BUSINESS_DATE");
				Column_list.add("DR_CR");
				Column_list.add("CARD_NO");
				Column_list.add("TRACE_NO");
				Column_list.add("AC_NO");
				Column_list.add("TRAN_DATE");
				Column_list.add("TRAN_TIME");
				Column_list.add("CBS_AMOUNT");
				Column_list.add("NPCI_AMOUNT");
				Column_list.add("SUR");
				Column_list.add("NARRATION");
			}  else if (nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("ATMSURCHARGE")
					) {
				Column_list.add("BUSINESS_DATE");
		
				Column_list.add("CARD_NO");
				Column_list.add("TRACE_NO");
				Column_list.add("AC_NO");
				Column_list.add("TRAN_DATE");
				Column_list.add("TRAN_TIME");
				Column_list.add("CBS_AMOUNT");
				Column_list.add("NPCI_AMOUNT");
				Column_list.add("SUR");
				Column_list.add("NARRATION");
			} else {
				Column_list.add("DR_CR");
				Column_list.add("DISPUTE_DATE");
				Column_list.add("BANK_NAME");
				Column_list.add("CARD_NO");
				Column_list.add("AC_NO");
				Column_list.add("DATE_OF_TXN");
				Column_list.add("TXN_TIME");
				Column_list.add("AMOUNT");
				Column_list.add("TRACE_NO");
				Column_list.add("NARRATION");
			}
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			fileName = "MC" + nfsSettlementBean.getStSubCategory().replaceAll("-", "")
					+ nfsSettlementBean.getTypeOfTTUM() + "_TTUM_"
					+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + ".xls";
			zipName = "MC" + nfsSettlementBean.getStSubCategory().replaceAll("-", "")
					+ nfsSettlementBean.getTypeOfTTUM() + "_TTUM_"
					+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + ".zip";
			if (nfsSettlementBean.getCategory().contains("RUPAY"))
				obj.generateExcelTTUM(stPath, fileName, Excel_data,
						"RUPAY"+nfsSettlementBean.getTypeOfTTUM() +" TTUM_" + nfsSettlementBean.getLocalDate().replaceAll("/", "-"), zipName);
			if (nfsSettlementBean.getCategory().contains("MASTERCARD")) {
				obj.generateExcelTTUM(stPath, fileName, Excel_data,
						"MASTERCARD"+nfsSettlementBean.getTypeOfTTUM() +" TTUM_" + nfsSettlementBean.getLocalDate().replaceAll("/", "-"), zipName);
			} else {
				obj.generateExcelTTUM(stPath, fileName, Excel_data, nfsSettlementBean.getCategory() +nfsSettlementBean.getTypeOfTTUM() + " TTUM_"
						+ nfsSettlementBean.getLocalDate().replaceAll("/", "-"), zipName);
			}
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
			nfsSettlementBean.setCreatedBy(Createdby);
			boolean executed = false;
			TTUMData = this.SETTLTTUMSERVICE.getDailyNFSTTUMDataMASTERCARD(nfsSettlementBean);
			fileName = "MC" + nfsSettlementBean.getStSubCategory().replaceAll("-", "")
					+ nfsSettlementBean.getTypeOfTTUM() + "TTUM" + nfsSettlementBean.getLocalDate().replaceAll("/", "")
					+ "_VAL.txt";
			String stPath = OUTPUT_FOLDER;
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getLocalDate().replaceAll("/", ""),
					nfsSettlementBean.getCategory());
			obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData, "MC", nfsSettlementBean);
			zipName = "MC" + nfsSettlementBean.getStSubCategory().replaceAll("-", "")
					+ nfsSettlementBean.getTypeOfTTUM() + "TTUM" + nfsSettlementBean.getLocalDate().replaceAll("/", "")
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
	}

	@RequestMapping(value = { "rollbackTTUMMASTERCARDADJ" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMMASTERCARDADJ(@RequestParam("filedate") String filedate,
			@RequestParam("typeOfTTUM") String typeOfTTUM, @RequestParam("subCat") String subCat,
			@RequestParam("category") String category) throws ParseException, Exception {
		logger.info("rollbackTTUMMASTERCARDADJ  " + filedate + " " + typeOfTTUM);
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		beanObj.setFileDate(filedate);
		beanObj.setTypeOfTTUM(typeOfTTUM);
		beanObj.setStSubCategory(subCat);
		beanObj.setCategory(category);
		HashMap<String, Object> output = this.rupayTTUMService.checkTTUMProcessedMASTERCARD(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue()) {
			boolean checkProcFlag;
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER_DOM")) {
				if (typeOfTTUM.contains("DECLINE")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY(beanObj);
				} else if (typeOfTTUM.contains("LORO_CREDIT")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMMC2(beanObj);
				} else if (typeOfTTUM.contains("LORO_DEBIT")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMMC3(beanObj);
				} else if (typeOfTTUM.contains("CROSS")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMMC3CROSS(beanObj);
				} else if (typeOfTTUM.contains("SURCHARGED")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY4(beanObj);
				} else if (typeOfTTUM.contains("SURCHARGEC")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY4(beanObj);
				} else if (typeOfTTUM.contains("LATE PRESENTMENT")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY6(beanObj);
				} else {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY7(beanObj);
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER_INT")) {
				if (typeOfTTUM.contains("DECLINE")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY(beanObj);
				} else if (typeOfTTUM.contains("LORO_CREDIT")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMMC3INT(beanObj);
				} else if (typeOfTTUM.contains("LORO_DEBIT")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMMC2INT(beanObj);
				} else if (typeOfTTUM.contains("CROSS")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMMC3CROSS(beanObj);
				} else if (typeOfTTUM.contains("SURCHARGEC")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY4(beanObj);
				} else if (typeOfTTUM.contains("LATE PRESENTMENT")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY6(beanObj);
				} else {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY7(beanObj);
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ISSUER")) {
				if (typeOfTTUM.contains("DECLINE")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY(beanObj);
				} else if (typeOfTTUM.contains("PROACTIVE")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMMC2POS(beanObj);
				} else if (typeOfTTUM.contains("DROP")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMMC3POS(beanObj);
				} else if (typeOfTTUM.contains("SURCHARGED")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMMC4(beanObj);
				} else if (typeOfTTUM.contains("SURCHARGEC")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMMC4(beanObj);
				} else if (typeOfTTUM.contains("LATE PRESENTMENT")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMMC6POS(beanObj);
				}  else if (typeOfTTUM.contains("ATMSURCHARGE")) {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMMC10POS(beanObj);
				}else {
					checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY7(beanObj);
				}
			} else if (typeOfTTUM.contains("DECLINE")) {
				checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAYINT(beanObj);
			} else if (typeOfTTUM.contains("PROACTIVE")) {
				checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY2INT(beanObj);
			} else if (typeOfTTUM.contains("DROP")) {
				checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY3INT(beanObj);
			} else if (typeOfTTUM.contains("SURCHARGED")) {
				checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY4INT(beanObj);
			} else if (typeOfTTUM.contains("SURCHARGEC")) {
				checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY4INT(beanObj);
			} else {
				checkProcFlag = this.SETTLTTUMSERVICE.rollBackTTUMRUPAY6(beanObj);
			}
			if (checkProcFlag)
				return String.valueOf(typeOfTTUM) + " TTUM Rollback Completed";
			return String.valueOf(typeOfTTUM) + " TTUM Failed";
		}
		return String.valueOf(typeOfTTUM) + " TTUM is Already Rollback!";
	}

	@RequestMapping(value = { "MastercardSettlementProcess" }, method = { RequestMethod.GET })
	public ModelAndView getSettlementPage(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
		logger.info("***** MastercardSettlementProcess.Get Start ****");
		logger.info("MastercardFileUpload GET");
		MastercardUploadBean beanObj = new MastercardUploadBean();
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.setViewName("MastercardSettlementProcess");
		modelAndView.addObject("mastercardUploadBean", beanObj);
		logger.info("***** MastercardSettlementController.MastercardSettlementProcess GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "MastercardSettlementProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String MastercardSettlementProcess(@ModelAttribute("mastercardUploadBean") MastercardUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** MastercardSettlementProcess.post Start ****");
		logger.info("MastercardFileUpload POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		System.out.println("File name is " + beanObj.getFileName());
		boolean executionFlag = false;
		HashMap<String, Object> output = this.mastercardService.validationForProcessing(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue()) {
			if (beanObj.getFileType().equalsIgnoreCase("DOMESTIC")) {
				executionFlag = this.mastercardService.mastercardSettlmentPro(beanObj);
			} else {
				executionFlag = this.mastercardService.mastercardSettlmentProINT(beanObj);
			}
			if (executionFlag)
				return "Processing Completed \n Please download reports";
			return "Processing Failed";
		}
		return output.get("msg").toString();
	}

	@RequestMapping(value = { "ValidateMastercardSettlement" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ValidateMastercardSettlement(@ModelAttribute("mastercardUploadBean") MastercardUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** ValidateMastercardSettlement.post Start ****");
		logger.info("ValidateMastercardSettlement POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		System.out.println("File name is " + beanObj.getFileName());
		return "success";
	}

	public static final String TTUM_FOLDER = System.getProperty("catalina.home");

	@RequestMapping(value = { "MastercardSettlementReport" }, method = { RequestMethod.POST })
	@ResponseBody
	public void MastercardSettlementReport(@ModelAttribute("mastercardUploadBean") MastercardUploadBean beanObj,
			RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response,
			HttpSession httpSession, Model model) throws Exception {
		logger.info("***** MastercardSettlementReport.post Start ****");
		logger.info("MastercardFileUpload POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		String fileName = "", zipName = "";
		System.out.println("File name is " + beanObj.getFileName());
		if (beanObj.getTtumCetegory().contains("REPORT")) {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			logger.info("File Name is " + beanObj.getFileName());
			boolean executed = false;
			TTUMData = this.SETTLTTUMSERVICE.getTTUMMASTERCARD(beanObj);
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getFileDate().replaceAll("/", ""), "MASTERCARD");
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			Map<String, String> table_Data = new HashMap<>();
			if (beanObj.getFileType().equalsIgnoreCase("DOMESTIC")) {
				Column_list.add("GL_CODE");
				Column_list.add("PARTICULARS");
				Column_list.add("DR_COUNT");
				Column_list.add("DEBIT");
				Column_list.add("CR_COUNT");
				Column_list.add("CREDIT");
			} else {
				Column_list.add("GL_CODE");
				Column_list.add("PARTICULARS");
				Column_list.add("DR_COUNT");
				Column_list.add("DEBIT");
				Column_list.add("CR_COUNT");
				Column_list.add("CREDIT");
			}
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			System.out.println("filename in nfs ttum is " + fileName);
			if (beanObj.getFileType().equalsIgnoreCase("DOMESTIC")) {
				fileName = "MASTERCARDDOMTTUM" + beanObj.getFileDate().replaceAll("/", "") + ".xls";
				zipName = "MASTERCARDDOMTTUM" + beanObj.getFileDate().replaceAll("/", "") + ".zip";
			} else {
				fileName = "MASTERCARDINTTTUM" + beanObj.getFileDate().replaceAll("/", "") + ".xls";
				zipName = "MASTERCARDINTTTUM" + beanObj.getFileDate().replaceAll("/", "") + ".zip";
			}
			obj.generateExcelTTUMSettlement(stPath, fileName, Excel_data,
					"Mastercard_" + beanObj.getFileDate().replaceAll("/", "-"), zipName);
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
			TTUMData = this.SETTLTTUMSERVICE.getMASTERCARDTTUM(beanObj);
			if (beanObj.getFileType().equalsIgnoreCase("DOMESTIC")) {
				fileName = "MASTERCARDDOMTTUM" + beanObj.getFileDate().replaceAll("/", "") + "_VAL.txt";
			} else {
				fileName = "MASTERCARDINTTTUM" + beanObj.getFileDate().replaceAll("/", "") + "_VAL.txt";
			}
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getFileDate().replaceAll("/", ""), "MASTERCARD");
			obj.generateADJTTUMFilesMASTERCARDRUFUND(stPath, fileName, 1, TTUMData, "MASTERCARD", beanObj);
			if (beanObj.getFileType().equalsIgnoreCase("DOMESTIC")) {
				zipName = "MASTERCARDDOMTTUM" + beanObj.getFileDate().replaceAll("/", "") + "_VAL.txt";
			} else {
				zipName = "MASTERCARDINTTTUM" + beanObj.getFileDate().replaceAll("/", "") + "_VAL.txt";
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

	@RequestMapping(value = { "rollbackTTUMMASTERCARD" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMMASTERCARD(@RequestParam("filedate") String filedate, @RequestParam("cet") String cet,
			@RequestParam("ttumCetegory") String ttumCetegory) throws ParseException, Exception {
		logger.info("rollbackTTUMMASTERCARD  " + filedate + " " + ttumCetegory + " " + cet);
		boolean result = false;
		if (cet.equalsIgnoreCase("DOMESTIC")) {
			if (ttumCetegory.contains("REPORT")) {
				result = SETTLTTUMSERVICE.TTUMRollbackReportSETTLMASTERCARD(filedate);
				if (result) {
					return "RollBack Success";

				} else {
					return "Failed";
				}
			} else {
				result = SETTLTTUMSERVICE.TTUMRollbackReportSETTLMASTERCARD(filedate);
				if (result) {
					return "RollBack Success";

				} else {
					return "Failed";
				}
			}

		} else {
			if (ttumCetegory.contains("REPORT")) {
				result = SETTLTTUMSERVICE.TTUMRollbackReportSETTLMASTERCARDINT(filedate);
				if (result) {
					return "RollBack Success";

				} else {
					return "Failed";
				}
			} else {
				result = SETTLTTUMSERVICE.TTUMRollbackReportSETTLMASTERCARDINT(filedate);
				if (result) {
					return "RollBack Success";

				} else {
					return "Failed";
				}
			}

		}

	}

	@RequestMapping(value = { "MastercardSettlementTTUM" }, method = { RequestMethod.GET })
	public ModelAndView MastercardSettlementTTUM(ModelAndView modelAndView, HttpServletRequest request)
			throws Exception {
		logger.info("***** MastercardSettlementController MastercardSettlementTTUM.Get Start ****");
		logger.info("MastercardFileUpload GET");
		MastercardUploadBean beanObj = new MastercardUploadBean();
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.setViewName("MastercardSettlementTTUM");
		modelAndView.addObject("mastercardUploadBean", beanObj);
		logger.info("***** MastercardSettlementController.MastercardSettlementTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "MastercardSettlementTTUMProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String MastercardSettlementTTUMProcess(@ModelAttribute("mastercardUploadBean") MastercardUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** MastercardSettlementTTUMProcess.post Start ****");
		logger.info("MastercardFileUpload POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		System.out.println("File name is " + beanObj.getFileName());
		boolean executionFlag = false;
		HashMap<String, Object> output = this.mastercardService.validationForSettlementTTUMProcess(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue()) {
			executionFlag = this.mastercardService.processSettlementTTUM(beanObj);
			if (executionFlag)
				return "Processing Completed \n Please download reports";
			return "Problem while processing";
		}
		return output.get("msg").toString();
	}

	@RequestMapping(value = { "ValidateMastercardSettlementTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ValidateMastercardSettlementTTUM(@ModelAttribute("mastercardUploadBean") MastercardUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** ValidateMastercardSettlement.post Start ****");
		logger.info("ValidateMastercardSettlement POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		System.out.println("File name is " + beanObj.getFileName());
		HashMap<String, Object> output = this.mastercardService.validationForSettlementTTUMProcess(beanObj);
		if (output != null && !((Boolean) output.get("result")).booleanValue()
				&& output.get("msg").toString().equalsIgnoreCase("TTUM is already processed for selected date"))
			return "success";
		return "TTUM is not processed for selected date";
	}

	@RequestMapping(value = { "MastercardTTUMDownload" }, method = { RequestMethod.POST })
	public String MastercardTTUMDownload(@ModelAttribute("mastercardUploadBean") MastercardUploadBean beanObj,
			HttpServletRequest request, HttpSession httpSession, Model model) throws Exception {
		logger.info("***** MastercardTTUMDownload.post Start ****");
		logger.info("MastercardTTUMDownload POST");
		logger.info("File Type is " + beanObj.getFileType() + " Date selected " + beanObj.getFileDate());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		System.out.println("File name is " + beanObj.getFileName());
		List<Object> Excel_data = new ArrayList();
		Excel_data = this.mastercardService.getSettlementTTUMData(beanObj);
		model.addAttribute("ReportName", "Mastercard_Settlement_TTUM");
		model.addAttribute("data", Excel_data);
		logger.info("***** MastercardSettlementController.MastercardTTUMDownload Daily POST End ****");
		return "GenerateMastercardSettlementReport";
	}
}
