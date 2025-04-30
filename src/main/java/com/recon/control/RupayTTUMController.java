package com.recon.control;

import com.recon.model.LoginBean;
import com.recon.model.RecordCount;
import com.recon.model.SettlementBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.ISourceService;
import com.recon.service.NFSUnmatchTTUMService;
import com.recon.service.RupayTTUMService;
import com.recon.service.SettlementTTUMService;
import com.recon.util.FileDetailsJson;
import com.recon.util.GenerateUCOTTUM;
import com.recon.util.ViewFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class RupayTTUMController {
	private static final Logger logger = Logger.getLogger(com.recon.control.RupayTTUMController.class);

	private static final String ERROR_MSG = "error_msg";

	public static final String OUTPUT_FOLDER = String.valueOf(System.getProperty("catalina.home")) + File.separator
			+ "FUNDING";

	public static final String OUTPUT_FOLDER1 = String.valueOf(System.getProperty("catalina.home")) + File.separator
			+ "TTUM1";

	@Autowired
	SettlementTTUMService SETTLTTUMSERVICE;

	@Autowired
	NFSUnmatchTTUMService nfsTTUMService;

	@Autowired
	ISourceService iSourceService;

	@Autowired
	RupayTTUMService rupayTTUMService;

	@RequestMapping(value = { "GenerateUnmatchedTTUM" }, method = { RequestMethod.GET })
	public ModelAndView GenerateUnmatchedTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateUnmatchedTTUM");
		logger.info("***** RupayTTUMController.GenerateUnmatchedTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateUnmatchedTTUMVISA" }, method = { RequestMethod.GET })
	public ModelAndView GenerateUnmatchedTTUMVISA(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateUnmatchedTTUMVISA");
		logger.info("***** RupayTTUMController.GenerateUnmatchedTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateUnmatchedTTUMVISAACQ" }, method = { RequestMethod.GET })
	public ModelAndView GenerateUnmatchedTTUMVISAACQ(ModelAndView modelAndView,
			@RequestParam("category") String category, HttpServletRequest request) throws Exception {
		logger.info("***** GenerateUnmatchedTTUMVISAACQ Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateUnmatchedTTUMVISAACQ");
		logger.info("***** RupayTTUMController.GenerateUnmatchedTTUMVISAACQ GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateTTUMNEPALORBHUTAN" }, method = { RequestMethod.GET })
	public ModelAndView GenerateTTUMNEPALORBHUTAN(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateTTUMNEPALORBHUTAN Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateTTUMNEPALORBHUTAN");
		logger.info("***** GenerateTTUMNEPALORBHUTAN.GenerateTTUMNEPALORBHUTAN GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GETNTSLRAWMATCHING" }, method = { RequestMethod.GET })
	public ModelAndView GETNTSLRAWMATCHING(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateTTUMNEPALORBHUTAN Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GetNtslrawMatching");
		logger.info("***** GenerateTTUMNEPALORBHUTAN.GenerateTTUMNEPALORBHUTAN GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateTTUMATMPOSCROSSROUNTING" }, method = { RequestMethod.GET })
	public ModelAndView GenerateTTUMATMPOSCROSSROUNTING(ModelAndView modelAndView,
			@RequestParam("category") String category, HttpServletRequest request) throws Exception {
		logger.info("***** GenerateTTUMATMPOSCROSSROUNTING Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateTTUMATMPOSCROSSROUNTING");
		logger.info("***** GenerateTTUMATMPOSCROSSROUNTING.GenerateTTUMATMPOSCROSSROUNTING GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateNFSACQTOVIAROUTING" }, method = { RequestMethod.GET })
	public ModelAndView GenerateNFSACQTOVIAROUTING(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateNFSACQTOVIAROUTING Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateNFSACQTOVIAROUTING");
		logger.info("***** GenerateNFSACQTOVIAROUTING.GenerateNFSACQTOVIAROUTING GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateNTSLMATICHING" }, method = { RequestMethod.GET })
	public ModelAndView GenerateNTSLMATICHING(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateNTSLMATICHING Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateNTSLMATICHING");
		logger.info("***** GenerateNFSACQTOVIAROUTING.GenerateNFSACQTOVIAROUTING GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateUnmatchedTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public String GenerateUnmatchedTTUMPost1(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** GenerateUnmatchedTTUM.GenerateUnmatchedTTUM post Start ****");
		logger.info("GenerateUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " localDate is " + beanObj.getLocalDate());
		logger.info("filedate is " + beanObj.getFileDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		System.out.println("st sub category for domestic tum is " + beanObj.getStSubCategory());
		System.out.println("FILENAme is " + beanObj.getFileName());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessed(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue()) {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				output2 = rupayTTUMService.runTTUMProces(beanObj);
			} else {
				output2 = rupayTTUMService.runTTUMProcess2(beanObj);
			}
		} else {
			return output.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue()) {
			System.out.println("msg " + output2.get("msg"));
			String value = output2.get("msg").toString();
			if (value.contains("NOT"))
				return "File Not Uploaded";
			if (value.contains("ALL"))
				return "TTUM ALLREADY PROCESS";
			if (value.contains("SUCCESS"))
				return "PROCESS SUCCESSFULLY!";
			return output2.get("msg").toString();
		}
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "GenerateUnmatchedTTUMJCB" }, method = { RequestMethod.POST })
	@ResponseBody
	public String GenerateUnmatchedTTUMJCB(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** GenerateUnmatchedTTUM.GenerateUnmatchedTTUM post Start ****");
		logger.info("GenerateUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " localDate is " + beanObj.getLocalDate());
		logger.info("filedate is " + beanObj.getFileDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		System.out.println("st sub category for domestic tum is " + beanObj.getStSubCategory());
		System.out.println("FILENAme is " + beanObj.getFileName());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedJCB(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue()) {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				output2 = rupayTTUMService.runTTUMProcesJCB(beanObj);
			} else {
				output2 = rupayTTUMService.runTTUMProcess2(beanObj);
			}
		} else {
			return output.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue()) {
			System.out.println("msg " + output2.get("msg"));
			String value = output2.get("msg").toString();
			if (value.contains("NOT"))
				return "File Not Uploaded";
			if (value.contains("ALL"))
				return "TTUM ALLREADY PROCESS";
			if (value.contains("SUCCESS"))
				return "PROCESS SUCCESSFULLY!";
			return output2.get("msg").toString();
		}
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "GenerateUnmatchedTTUMDFS" }, method = { RequestMethod.POST })
	@ResponseBody
	public String GenerateUnmatchedTTUMDFS(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** GenerateUnmatchedTTUM.GenerateUnmatchedTTUM post Start ****");
		logger.info("GenerateUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " localDate is " + beanObj.getLocalDate());
		logger.info("filedate is " + beanObj.getFileDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		System.out.println("st sub category for domestic tum is " + beanObj.getStSubCategory());
		System.out.println("FILENAme is " + beanObj.getFileName());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedDFS(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue()) {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				output2 = rupayTTUMService.runTTUMProcesDFS(beanObj);
			} else {
				output2 = rupayTTUMService.runTTUMProcess2(beanObj);
			}
		} else {
			return output.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue()) {
			System.out.println("msg " + output2.get("msg"));
			String value = output2.get("msg").toString();
			if (value.contains("NOT"))
				return "File Not Uploaded";
			if (value.contains("ALL"))
				return "TTUM ALLREADY PROCESS";
			if (value.contains("SUCCESS"))
				return "PROCESS SUCCESSFULLY!";
			return output2.get("msg").toString();
		}
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "GenerateUnmatchedTTUMICD" }, method = { RequestMethod.POST })
	@ResponseBody
	public String GenerateUnmatchedTTUMICD(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** GenerateUnmatchedTTUMICD.GenerateUnmatchedTTUM post Start ****");
		logger.info("GenerateUnmatchedTTUMICD POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " localDate is " + beanObj.getLocalDate());
		logger.info("filedate is " + beanObj.getFileDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		System.out.println("st sub category for domestic tum is " + beanObj.getStSubCategory());
		System.out.println("FILENAme is " + beanObj.getFileName());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedICD(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue()) {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ISSUER")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					output2 = rupayTTUMService.runTTUMProcesICD(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					output2 = rupayTTUMService.runTTUMProcess2ICD(beanObj);
				} else {
					output2 = rupayTTUMService.runTTUMProces(beanObj);
				}
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				output2 = rupayTTUMService.runTTUMProcess4ICD(beanObj);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				output2 = rupayTTUMService.runTTUMProcess3ICD(beanObj);
			} else {
				output2 = rupayTTUMService.runTTUMProces(beanObj);
			}
		} else {
			return output.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue()) {
			System.out.println("msg " + output2.get("msg"));
			String value = output2.get("msg").toString();
			if (value.contains("NOT"))
				return "File Not Uploaded";
			if (value.contains("ALL"))
				return "TTUM ALLREADY PROCESS";
			if (value.contains("SUCCESS"))
				return "PROCESS SUCCESSFULLY!";
			return output2.get("msg").toString();
		}
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "GenerateUnmatchedTTUMICCW" }, method = { RequestMethod.POST })
	@ResponseBody
	public String GenerateUnmatchedTTUMICCW(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** GenerateUnmatchedTTUMICCW.GenerateUnmatchedTTUM post Start ****");
		logger.info("GenerateUnmatchedTTUMICCW POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " localDate is " + beanObj.getLocalDate());
		logger.info("filedate is " + beanObj.getFileDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		System.out.println("st sub category for domestic tum is " + beanObj.getStSubCategory());
		System.out.println("FILENAme is " + beanObj.getFileName());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedICCW(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue()) {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ISSUER")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					output2 = rupayTTUMService.runTTUMProcesICCW(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					output2 = rupayTTUMService.runTTUMProcesICCW(beanObj);
				} else {
					output2 = rupayTTUMService.runTTUMProces(beanObj);
				}
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
				output2 = rupayTTUMService.runTTUMProcesICCW2(beanObj);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
				output2 = rupayTTUMService.runTTUMProcesICCW2(beanObj);
			} else {
				output2 = rupayTTUMService.runTTUMProces(beanObj);
			}
		} else {
			return output.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue()) {
			System.out.println("msg " + output2.get("msg"));
			String value = output2.get("msg").toString();
			if (value.contains("NOT"))
				return "File Not Uploaded";
			if (value.contains("ALL"))
				return "TTUM ALLREADY PROCESS";
			if (value.contains("SUCCESS"))
				return "PROCESS SUCCESSFULLY!";
			return output2.get("msg").toString();
		}
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "GenerateUnmatchedTTUMNFSRUPAY" }, method = { RequestMethod.POST })
	@ResponseBody
	public String GenerateUnmatchedTTUMNFSRUPAY(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** GenerateUnmatchedTTUMICD.GenerateUnmatchedTTUMNFSRUPAY post Start ****");
		logger.info("GenerateUnmatchedTTUMNFSRUPAY POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("filedate is " + beanObj.getLocalDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedNFSRUPAY(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue()) {
			output2 = rupayTTUMService.runTTUMProcesNFSRUPAY(beanObj);
		} else {
			return output.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue()) {
			System.out.println("msg " + output2.get("msg"));
			String value = output2.get("msg").toString();
			if (value.contains("NOT"))
				return "File Not Uploaded";
			if (value.contains("ALL"))
				return "TTUM ALLREADY PROCESS";
			if (value.contains("SUCCESS"))
				return "PROCESS SUCCESSFULLY!";
			return output2.get("msg").toString();
		}
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "GenerateUnmatchedTTUMVISA" }, method = { RequestMethod.POST })
	@ResponseBody
	public String GenerateUnmatchedTTUMVISA(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.GenerateUnmatchedTTUM post Start ****");
		logger.info("GenerateUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " localDate is " + beanObj.getLocalDate());
		logger.info("filedate is " + beanObj.getFileDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		System.out.println("st sub category for domestic tum is " + beanObj.getStSubCategory());
		System.out.println("FILENAme is " + beanObj.getFileName());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedVISA(beanObj);
		HashMap<String, Object> output2 = null;
		if (output != null && ((Boolean) output.get("result")).booleanValue()) {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ISS")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					output2 = rupayTTUMService.runTTUMProcesVISA(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					output2 = rupayTTUMService.runTTUMProcessVISA2(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					output2 = rupayTTUMService.runTTUMProcessVISA3(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CREDIT SURCHARGE")) {
					output2 = rupayTTUMService.runTTUMProcessVISA4(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DEBIT SURCHARGE")) {
					output2 = rupayTTUMService.runTTUMProcessVISA5(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")) {
					output2 = rupayTTUMService.runTTUMProcessVISA6(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND")) {
					output2 = rupayTTUMService.runTTUMProcessVISA7(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND_REV")) {
					output2 = rupayTTUMService.runTTUMProcessVISA8(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("REFUND_INT")) {
					output2 = rupayTTUMService.runTTUMProcessVISA9(beanObj);
				} else {
					output2 = rupayTTUMService.runTTUMProcessVISA10(beanObj);
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ACQ DOM ATM")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
					output2 = rupayTTUMService.runTTUMProcesVISAPOS(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
					output2 = rupayTTUMService.runTTUMProcessVISA2POS(beanObj);
				} else {
					output2 = rupayTTUMService.runTTUMProcessVISA3POS(beanObj);
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ACQ INT ATM")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
					output2 = rupayTTUMService.runTTUMProcesVISAINT(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
					output2 = rupayTTUMService.runTTUMProcessVISA2INT(beanObj);
				} else {
					output2 = rupayTTUMService.runTTUMProcessVISA8INT(beanObj);
				}
			}
		} else {
			return output.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue()) {
			System.out.println("msg " + output2.get("msg"));
			String value = output2.get("msg").toString();
			if (value.contains("NOT"))
				return "Recon Not Processed!!";
			if (value.contains("ALL"))
				return "TTUM ALLREADY PROCESS";
			if (value.contains("SUCCESS"))
				return "PROCESS SUCCESSFULLY!";
			return output2.get("msg").toString();
		}
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "GenerateNTSLRAWMATCHINGDATA" }, method = { RequestMethod.POST })
	@ResponseBody
	public String GenerateNTSLRAWMATCHINGDATA(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.GenerateUnmatchedTTUM post Start ****");
		logger.info("GenerateUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " localDate is " + beanObj.getLocalDate());
		logger.info("filedate is " + beanObj.getFileDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		System.out.println("st sub category for domestic tum is " + beanObj.getStSubCategory());
		System.out.println("FILENAme is " + beanObj.getFileName());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;

		HashMap<String, Object> output2 = null;

		output2 = rupayTTUMService.runTTUMProcesVISA(beanObj);

		return output2.get("msg").toString();
	}

	@RequestMapping(value = "ntslRawMatching", method = RequestMethod.POST)
	@ResponseBody
	public List<ViewFiles> ntslRawMatching(@RequestParam String cycle, @RequestParam String localDate,
			@RequestParam String ModuleType) throws SQLException, Exception {
		System.out.println("Data " + localDate + " " + cycle + " " + ModuleType);

		return nfsTTUMService.searchViewFile1(cycle, localDate, ModuleType);

	}

	@RequestMapping(value = { "GenerateUnmatchedTTUMCTC" }, method = { RequestMethod.POST })
	@ResponseBody
	public String GenerateUnmatchedTTUMPost(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		HashMap<String, Object> output2;
		logger.info("***** GenerateUnmatchedTTUM.GenerateUnmatchedTTUM post Start ****");
		logger.info("GenerateUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " localDate is " + beanObj.getLocalDate());
		logger.info("filedate is " + beanObj.getFileDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		System.out.println("st sub category for domestic tum is " + beanObj.getStSubCategory());
		System.out.println("FILENAme is " + beanObj.getFileName());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedCTC(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue()) {
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				output2 = rupayTTUMService.runTTUMProcess3(beanObj);
			} else {
				output2 = rupayTTUMService.runTTUMProcess4(beanObj);
			}
		} else {
			return output.get("msg").toString();
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
			return "Processing Completed Successfully! \n Please download Report";
		return output2.get("msg").toString();
	}

	@RequestMapping(value = { "checkTTUMProcessedCTC" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkTTUMProcessedCTC(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		try {
			logger.info("checkTTUMProcessedCTC: checkTTUMProcessed: Entry");
			HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedCTC(beanObj);
			if (output != null && ((Boolean) output.get("result")).booleanValue())
				return "TTUM is not processed for selected date";
			return "success";
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return "Exception";
		}
	}

	@RequestMapping(value = { "DownloadUnmatchedTTUMCTC" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadUnmatchedTTUMCTC(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {

		logger.info("***** GenerateUnmatchedTTUM.DownloadUnmatchedTTUM post Start ****" + beanObj.getStSubCategory()
				+ " " + beanObj.getTypeOfTTUM());
		logger.info("DownloadUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		String fileName = "", zipName = "";
		if (!beanObj.getStSubCategory().equalsIgnoreCase("ISSUER"))
			beanObj.setTypeOfTTUM(beanObj.getAcqtypeOfTTUM());

		if (beanObj.getTTUMCategory().contains("REPORT")) {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			logger.info("File Name is " + beanObj.getFileName());
			boolean executed = false;
			TTUMData = SETTLTTUMSERVICE.getTTUMCTC(beanObj);
			fileName = "CTC_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate() + ".txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", "-"), beanObj.getCategory());
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			Map<String, String> table_Data = new HashMap<>();
			if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				Column_list.add("DR_CR");
				Column_list.add("DISPUTE_DATE");
				Column_list.add("BANK_NAME");
				Column_list.add("CARD_NO");
				Column_list.add("AC_NO");
				Column_list.add("DR_CR");
				Column_list.add("TXN_DATE");
				Column_list.add("TXN_TIME");
				Column_list.add("AMOUNT");
				Column_list.add("RRN");
				Column_list.add("TERMINAL_ID");
				Column_list.add("NARRATION");
			} else {
				Column_list.add("DR_CR");
				Column_list.add("DISPUTE_DATE");
				Column_list.add("BANK_NAME");
				Column_list.add("CARD_NO");
				Column_list.add("AC_NO");
				Column_list.add("TXN_DATE");
				Column_list.add("TXN_TIME");
				Column_list.add("AMOUNT");
				Column_list.add("RRN");
				Column_list.add("NARRATION");
				Column_list.add("UID");
				Column_list.add("FILEDATE");
				Column_list.add("TTUM_TYPE");
			}
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			System.out.println("filename in nfs ttum is " + fileName);
			fileName = "CTC_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "-")
					+ ".xls";
			zipName = "CTC_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "-")
					+ ".zip";
			obj.generateExcelTTUM(stPath, fileName, Excel_data,
					"CTC " + beanObj.getTypeOfTTUM() + " TTUM_" + beanObj.getLocalDate().replaceAll("/", "-"), zipName);
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
			TTUMData = SETTLTTUMSERVICE.getCTCTTUM(beanObj);
			System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
			fileName = "CTC_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "-")
					+ ".VAL";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", "-"), beanObj.getCategory());
			obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData, "CTC", beanObj);
			logger.info("File is created");
			logger.info("File is created");
			zipName = "CTC_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "-")
					+ ".VAL";
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

	@RequestMapping(value = { "rollbackTTUMCTC" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMCTC(@RequestParam("filedate") String filedate,
			@RequestParam("typeOfTTUM") String typeOfTTUM, @RequestParam("subCat") String subcat)
			throws ParseException, Exception {
		boolean checkProcFlag;
		logger.info("rollbackTTUMCTC  " + filedate + " " + typeOfTTUM);
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		beanObj.setLocalDate(filedate);
		beanObj.setTypeOfTTUM(typeOfTTUM);
		beanObj.setStSubCategory(subcat);
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedCTC(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue())
			return String.valueOf(typeOfTTUM) + " TTUM is Already Rollback!";
		if (subcat.contains("ISSUER")) {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMCTC2(beanObj);
		} else {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMCTC(beanObj);
		}
		if (checkProcFlag)
			return String.valueOf(subcat) + "_" + typeOfTTUM + " TTUM Rollback Completed";
		return String.valueOf(subcat) + "_" + typeOfTTUM + " TTUM Failed";
	}

	@RequestMapping(value = { "rollbackTTUMICD" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMICD(@RequestParam("filedate") String filedate,
			@RequestParam("typeOfTTUM") String typeOfTTUM, @RequestParam("subCat") String subcat)
			throws ParseException, Exception {
		boolean checkProcFlag;
		logger.info("rollbackTTUMICD  " + filedate + " " + typeOfTTUM);
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		beanObj.setLocalDate(filedate);
		beanObj.setTypeOfTTUM(typeOfTTUM);
		beanObj.setStSubCategory(subcat);
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedICD(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue())
			return String.valueOf(typeOfTTUM) + " TTUM is Already Rollback!";
		if (subcat.contains("ISSUER")) {
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMICD(beanObj);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMICD2(beanObj);
			} else {
				checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMCTC2(beanObj);
			}
		} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMICD3(beanObj);
		} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMICD4(beanObj);
		} else {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMCTC2(beanObj);
		}
		if (checkProcFlag)
			return String.valueOf(subcat) + "_" + typeOfTTUM + " TTUM Rollback Completed";
		return String.valueOf(subcat) + "_" + typeOfTTUM + " TTUM Failed";
	}

	@RequestMapping(value = { "rollbackTTUMICCW" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMICCW(@RequestParam("filedate") String filedate,
			@RequestParam("typeOfTTUM") String typeOfTTUM, @RequestParam("subCat") String subcat)
			throws ParseException, Exception {
		boolean checkProcFlag;
		logger.info("rollbackTTUMICD  " + filedate + " " + typeOfTTUM);
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		beanObj.setLocalDate(filedate);
		beanObj.setTypeOfTTUM(typeOfTTUM);
		beanObj.setStSubCategory(subcat);
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedICCW(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue())
			return String.valueOf(typeOfTTUM) + " TTUM is Already Rollback!";

		if (subcat.contains("ISSUER")) {
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMICCW(beanObj);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMICCW(beanObj);
			} else {
				checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMCTC2(beanObj);
			}
		} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO DEBIT")) {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMICCW2(beanObj);
		} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LORO CREDIT")) {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMICCW2(beanObj);
		} else {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMCTC2(beanObj);
		}
		if (checkProcFlag)
			return String.valueOf(subcat) + "_" + typeOfTTUM + " TTUM Rollback Completed";
		return String.valueOf(subcat) + "_" + typeOfTTUM + " TTUM Failed";
	}

	@RequestMapping(value = { "rollbackTTUMNFSRUPAY" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMNFSRUPAY(@RequestParam("filedate") String filedate) throws ParseException, Exception {
		logger.info("rollbackTTUMNFSRUPAY  " + filedate);
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		beanObj.setLocalDate(filedate);
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedNFSRUPAY(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue())
			return " TTUM is Already Rollback!";
		boolean checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMNFSRUAPY(beanObj);
		if (checkProcFlag)
			return "TTUM Rollback Completed";
		return " TTUM Failed";
	}

	@RequestMapping(value = { "rollbackTTUMNFS" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMNFS(@RequestParam("filedate") String filedate,
			@RequestParam("typeOfTTUM") String typeOfTTUM, @RequestParam("subCat") String subcat)
			throws ParseException, Exception {
		boolean checkProcFlag;
		logger.info("rollbackTTUMReport  " + filedate + " " + typeOfTTUM);
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		beanObj.setLocalDate(filedate);
		beanObj.setTypeOfTTUM(typeOfTTUM);
		beanObj.setStSubCategory(subcat);
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessed(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue())
			return "TTUM is Already Rollback";
		if (subcat.contains("ISSUER")) {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMNFS(beanObj);
		} else {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMNFS2(beanObj);
		}
		if (checkProcFlag)
			return "TTUM Rollback Completed";
		return String.valueOf(subcat) + "_" + typeOfTTUM + "Failed";
	}

	@RequestMapping(value = { "rollbackTTUMJCB" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMJCB(@RequestParam("filedate") String filedate,
			@RequestParam("typeOfTTUM") String typeOfTTUM, @RequestParam("subCat") String subcat)
			throws ParseException, Exception {
		boolean checkProcFlag;
		logger.info("rollbackTTUMReport  " + filedate + " " + typeOfTTUM);
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		beanObj.setLocalDate(filedate);
		beanObj.setTypeOfTTUM(typeOfTTUM);
		beanObj.setStSubCategory(subcat);
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedJCB(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue())
			return "TTUM is Already Rollback";
		if (subcat.contains("ISSUER")) {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMJCB(beanObj);
		} else {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMNFS2(beanObj);
		}
		if (checkProcFlag)
			return "TTUM Rollback Completed";
		return String.valueOf(subcat) + "_" + typeOfTTUM + "Failed";
	}

	@RequestMapping(value = { "rollbackTTUMDFS" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMDFS(@RequestParam("filedate") String filedate,
			@RequestParam("typeOfTTUM") String typeOfTTUM, @RequestParam("subCat") String subcat)
			throws ParseException, Exception {
		boolean checkProcFlag;
		logger.info("rollbackTTUMReport  " + filedate + " " + typeOfTTUM);
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		beanObj.setLocalDate(filedate);
		beanObj.setTypeOfTTUM(typeOfTTUM);
		beanObj.setStSubCategory(subcat);
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedDFS(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue())
			return "TTUM is Already Rollback";
		if (subcat.contains("ACQUIRER")) {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMDFS(beanObj);
		} else {
			checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMNFS2(beanObj);
		}
		if (checkProcFlag)
			return "TTUM Rollback Completed";
		return String.valueOf(subcat) + "_" + typeOfTTUM + "Failed";
	}

	@RequestMapping(value = { "checkTTUMProcessed" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkTTUMProcessed(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		try {
			logger.info("RupayTTUMController: checkTTUMProcessed: Entry");
			return "success";
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return "Exception";
		}
	}

	@RequestMapping(value = { "checkTTUMProcessedICD" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkTTUMProcessedICD(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		return "success";
	}

	@RequestMapping(value = { "checkTTUMProcessedVISA" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkTTUMProcessedVISA(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		try {
			logger.info("TTUMController: checkTTUMProcessed: Entry");
			return "success";
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return "Exception";
		}
	}

	@RequestMapping(value = { "checkTTUMProcessedVISANB" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkTTUMProcessedVISANB(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		try {
			logger.info("TTUMController: checkTTUMProcessedVISANB: Entry");
			return "success";
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return "Exception";
		}
	}

	@RequestMapping(value = { "checkTTUMProcessedATMPOSCROSSROUNTING" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkTTUMProcessedATMPOSCROSSROUNTING(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		return "success";
	}

	@RequestMapping(value = { "checkTTUMNFSTOVISACROSSROUNTING" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkTTUMNFSTOVISACROSSROUNTING(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		return "success";
	}

	@RequestMapping(value = { "checkTTUMProcessedVISANFSRUPAY" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkTTUMProcessedVISANFSRUPAY(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		try {
			logger.info("TTUMController: checkTTUMProcessedVISANFSRUPAY: Entry");
			HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedNFSRUPAY(beanObj);
			if (output != null && ((Boolean) output.get("result")).booleanValue())
				return "TTUM is not processed for selected date";
			return "success";
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return "Exception";
		}
	}

	@RequestMapping(value = { "DownloadUnmatchedTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadUnmatchedTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.DownloadUnmatchedTTUM post Start ****" + beanObj.getCategory());
		logger.info("DownloadUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		String fileName = "", zipName = "";
		if (!beanObj.getStSubCategory().equalsIgnoreCase("ISSUER"))
			beanObj.setTypeOfTTUM(beanObj.getAcqtypeOfTTUM());
		if (beanObj.getTTUMCategory().contains("REPORT")) {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			logger.info("File Name is " + beanObj.getFileName());
			boolean executed = false;
			if (beanObj.getCategory().equalsIgnoreCase("ICD")) {
				TTUMData = SETTLTTUMSERVICE.getTTUMICD(beanObj);
			} else if (beanObj.getCategory().equalsIgnoreCase("JCB")) {
				TTUMData = SETTLTTUMSERVICE.getTTUMJCB(beanObj);
			} else if (beanObj.getCategory().equalsIgnoreCase("ICCW")) {
				TTUMData = SETTLTTUMSERVICE.getTTUMICCW(beanObj);
			} else if (beanObj.getCategory().equalsIgnoreCase("DFS")) {
				TTUMData = SETTLTTUMSERVICE.getTTUMDFS(beanObj);
			} else {
				TTUMData = SETTLTTUMSERVICE.getTTUMNFS(beanObj);
			}
			System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
			fileName = "NFS_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "")
					+ ".txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", ""), beanObj.getCategory());
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			Map<String, String> table_Data = new HashMap<>();
			if (beanObj.getCategory().equalsIgnoreCase("ICD")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					Column_list.add("DISPUTE_DATE");
					Column_list.add("BANKNAME");
					Column_list.add("CARD_NO");
					Column_list.add("AC_NO");
					Column_list.add("DR_CR");
					Column_list.add("TXN_DATE");
					Column_list.add("TXN_TIME");
					Column_list.add("AMOUNT");
					Column_list.add("RRN");
					Column_list.add("TERMINAL_ID");
					Column_list.add("TTUM_TYPE");
					Column_list.add("FILEDATE");
					Column_list.add("NARRATION");
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					Column_list.add("DR_CR");
					Column_list.add("DISPUTE_DATE");
					Column_list.add("BANK_NAME");
					Column_list.add("CARD_NO");
					Column_list.add("AC_NO");
					Column_list.add("TXN_DATE");
					Column_list.add("TXN_TIME");
					Column_list.add("AMOUNT");
					Column_list.add("TRACE_NO");
					Column_list.add("FILEDATE");
					Column_list.add("NARRATION");
				} else {
					Column_list.add("DR_CR");
					Column_list.add("DISPUTE_DATE");
					Column_list.add("BANK_NAME");
					Column_list.add("CARD_NO");
					Column_list.add("AC_NO");
					Column_list.add("TXN_DATE");
					Column_list.add("TXN_TIME");
					Column_list.add("AMOUNT");
					Column_list.add("RRN");
					Column_list.add("NARRATION");
				}
			} else if (beanObj.getCategory().equalsIgnoreCase("JCB") || beanObj.getCategory().equalsIgnoreCase("DFS")) {

				Column_list.add("DISPUTE_DATE");
				Column_list.add("BANK_NAME");
				Column_list.add("CARD_NO");
				Column_list.add("AC_NO");
				Column_list.add("DR_CR");
				Column_list.add("TXN_DATE");
				Column_list.add("TXN_TIME");
				Column_list.add("AMOUNT");
				Column_list.add("RRN");
				Column_list.add("TERMINAL_ID");
				Column_list.add("NARRATION");
				Column_list.add("TTUM_TYPE");
				Column_list.add("FILEDATE");
				Column_list.add("UID");

			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER")) {
				Column_list.add("DR_CR");
				Column_list.add("DISPUTE_DATE");
				Column_list.add("BANK_NAME");
				Column_list.add("CARD_NO");
				Column_list.add("AC_NO");
				Column_list.add("DR_CR");
				Column_list.add("TXN_DATE");
				Column_list.add("TXN_TIME");
				Column_list.add("AMOUNT");
				Column_list.add("RRN");
				Column_list.add("TERMINAL_ID");
				Column_list.add("NARRATION");
			} else {
				Column_list.add("DR_CR");
				Column_list.add("DISPUTE_DATE");
				Column_list.add("BANK_NAME");
				Column_list.add("CARD_NO");
				Column_list.add("AC_NO");
				Column_list.add("TXN_DATE");
				Column_list.add("TXN_TIME");
				Column_list.add("AMOUNT");
				Column_list.add("RRN");
				Column_list.add("NARRATION");
			}
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			System.out.println("filename in nfs ttum is " + fileName);
			if (beanObj.getTypeOfTTUM().contains("DROP")) {

				beanObj.setTypeOfTTUM("CUSTOMER DEBIT");
			}

			if (beanObj.getCategory().equalsIgnoreCase("ICD")) {
				fileName = "ICD_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "")
						+ ".xls";
				zipName = "ICD_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "")
						+ ".zip";
			} else if (beanObj.getCategory().equalsIgnoreCase("JCB")) {
				fileName = "JCB_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "")
						+ ".xls";
				zipName = "JCB_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "")
						+ ".zip";
			} else if (beanObj.getCategory().equalsIgnoreCase("DFS")) {
				fileName = "DFS_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "")
						+ ".xls";
				zipName = "DFS_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "")
						+ ".zip";
			} else if (beanObj.getCategory().equalsIgnoreCase("ICCW")) {
				fileName = "ICCW_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "")
						+ ".xls";
				zipName = "ICCW_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "")
						+ ".zip";
			} else {
				fileName = "NFS_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "")
						+ ".xls";
				zipName = "NFS_" + beanObj.getTypeOfTTUM() + "_TTUM_" + beanObj.getLocalDate().replaceAll("/", "")
						+ ".zip";
			}
			obj.generateExcelTTUM(stPath, fileName, Excel_data,
					beanObj.getCategory() + " TTUM_" + beanObj.getLocalDate().replaceAll("/", "-"), zipName);
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
			if (beanObj.getCategory().equalsIgnoreCase("ICD")) {
				TTUMData = SETTLTTUMSERVICE.getICDTTUM(beanObj);
			} else if (beanObj.getCategory().equalsIgnoreCase("JCB")) {
				TTUMData = SETTLTTUMSERVICE.getACQJCB(beanObj);
			} else if (beanObj.getCategory().equalsIgnoreCase("DFS")) {
				TTUMData = SETTLTTUMSERVICE.getACQDFS(beanObj);
			} else if (beanObj.getCategory().equalsIgnoreCase("ICCW")) {
				TTUMData = SETTLTTUMSERVICE.getISSICCW(beanObj);
			} else {
				TTUMData = SETTLTTUMSERVICE.getNFSRECONACQTTUM(beanObj);
			}
			System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());

			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", ""), beanObj.getCategory());

			obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData, "NFS", beanObj);

			if (beanObj.getTypeOfTTUM().contains("DROP")) {

				beanObj.setTypeOfTTUM("CUSTOMER DEBIT");
			}

			if (beanObj.getCategory().equalsIgnoreCase("ICD")) {
				fileName = "ICD" + beanObj.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
						+ beanObj.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			} else if (beanObj.getCategory().equalsIgnoreCase("JCB")) {
				fileName = "JCB" + beanObj.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
						+ beanObj.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			} else if (beanObj.getCategory().equalsIgnoreCase("DFS")) {
				fileName = "DFS" + beanObj.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
						+ beanObj.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			} else {
				fileName = "NFS" + beanObj.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
						+ beanObj.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			}
			if (beanObj.getCategory().equalsIgnoreCase("ICD")) {
				zipName = "ICD" + beanObj.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
						+ beanObj.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			} else if (beanObj.getCategory().equalsIgnoreCase("JCB")) {
				zipName = "JCB" + beanObj.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
						+ beanObj.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			} else if (beanObj.getCategory().equalsIgnoreCase("DFS")) {
				zipName = "DFS" + beanObj.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
						+ beanObj.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			} else {
				zipName = "NFS" + beanObj.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
						+ beanObj.getLocalDate().replaceAll("/", "") + "_VAL.txt";
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

	@RequestMapping(value = { "DownloadUnmatchedTTUMVISA" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadUnmatchedTTUMVISA(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.DownloadUnmatchedTTUM post Start ****");
		logger.info("DownloadUnmatchedTTUM POST" + beanObj.getTTUMCategory());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " " + beanObj.getTypeOfTTUM() + " " + beanObj.getStSubCategory()
				+ " " + beanObj.getCategory() + "  " + beanObj.getLocalDate());
		String fileName = "", zipName = "";
		if (beanObj.getTTUMCategory().contains("REPORT")) {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			logger.info("File Name is " + beanObj.getFileName());
			boolean executed = false;
			TTUMData = SETTLTTUMSERVICE.getTTUMVISA(beanObj);
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", "-"), beanObj.getCategory());
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			Map<String, String> table_Data = new HashMap<>();
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")
					|| beanObj.getTypeOfTTUM().equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")
					|| beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")
					|| beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT CHARGES")
					|| beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP CHARGES")) {
				Column_list.add("DR_CR");
				Column_list.add("DISPUTE_DATE");
				Column_list.add("BANK_NAME");
				Column_list.add("CARD_NO");
				Column_list.add("AC_NO");
				Column_list.add("DATE_OF_TXN");
				Column_list.add("AUTH_CODE");
				Column_list.add("AMOUNT");
				Column_list.add("FILEDATE");
				Column_list.add("NARRATION");
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("CREDIT SURCHARGE")
					|| beanObj.getTypeOfTTUM().equalsIgnoreCase("DEBIT SURCHARGE")) {
				Column_list.add("DR_CR");
				Column_list.add("PURCHASE_DATE");
				Column_list.add("CARD_NO");
				Column_list.add("TRACE_NO");
				Column_list.add("AC_NO");
				Column_list.add("TRAN_DATE");
				Column_list.add("TRAN_TIME");
				Column_list.add("CBS_AMOUNT");
				Column_list.add("VISA_AMOUNT");
				Column_list.add("SUR");
				Column_list.add("AUTH_CODE");
				Column_list.add("SETTLEMENT_FLAG");
				Column_list.add("TC");
				Column_list.add("FILEDATE");
				Column_list.add("NARRATION");
			} else if (beanObj.getTypeOfTTUM().contains("PROACTIVE")) {
				Column_list.add("DR_CR");
				Column_list.add("DISPUTE_DATE");
				Column_list.add("BANK_NAME");
				Column_list.add("CARD_NO");
				Column_list.add("AC_NO");
				Column_list.add("TXN_DATE");
				Column_list.add("TRACE_NO");
				Column_list.add("AMOUNT");
				Column_list.add("FILEDATE");
				Column_list.add("NARRATION");
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("ACQ INT ATM")) {

				if (beanObj.getTypeOfTTUM().contains("LORO CREDIT")) {
					Column_list.add("DR_CR");
					Column_list.add("DISPUTE_DATE");
					Column_list.add("BANK_NAME");
					Column_list.add("CARD_NO");
					Column_list.add("AC_NO");
					Column_list.add("DATE_OF_TXN");
					Column_list.add("AUTH_CODE");
					Column_list.add("ATM_ID");
					Column_list.add("AMOUNT");
					Column_list.add("FILEDATE");
					Column_list.add("NARRATION");
					Column_list.add("UID");

				} else {
					Column_list.add("DR_CR");
					Column_list.add("DISPUTE_DATE");
					Column_list.add("BANK_NAME");
					Column_list.add("CARD_NO");
					Column_list.add("AC_NO");
					Column_list.add("DATE_OF_TXN");
					Column_list.add("TRACE_NO");
					Column_list.add("AMOUNT");
					Column_list.add("FILEDATE");
					Column_list.add("NARRATION");
					Column_list.add("UID");
				}
			} else if (beanObj.getTypeOfTTUM().contains("LORO DEBIT")
					|| beanObj.getTypeOfTTUM().contains("LORO CREDIT")) {
				Column_list.add("DR_CR");
				Column_list.add("DISPUTE_DATE");
				Column_list.add("BANK_NAME");
				Column_list.add("CARD_NO");
				Column_list.add("AC_NO");
				Column_list.add("DATE_OF_TXN");
				Column_list.add("TRACE_NO");
				Column_list.add("AMOUNT");
				Column_list.add("FILEDATE");
				Column_list.add("NARRATION");
				Column_list.add("UID");
			} else {
				Column_list.add("DR_CR");
				Column_list.add("DISPUTE_DATE");
				Column_list.add("BANK_NAME");
				Column_list.add("CARD_NO");
				Column_list.add("AC_NO");
				Column_list.add("DATE_OF_TXN");
				Column_list.add("AUTH_CODE");
				Column_list.add("AMOUNT");
				Column_list.add("ARN");
				Column_list.add("UID");
				Column_list.add("TC");
				Column_list.add("SETTLEMENT_FLAG");
				Column_list.add("FILEDATE");
				Column_list.add("NARRATION");
			}
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			System.out.println("filename in nfs ttum is " + fileName);
			fileName = "VISA_" + beanObj.getStSubCategory().replaceAll("\\s", "") + beanObj.getTypeOfTTUM() + "_TTUM_"
					+ beanObj.getLocalDate().replaceAll("/", "-") + ".xls";
			zipName = "VISA_" + beanObj.getStSubCategory().replaceAll("\\s", "") + beanObj.getTypeOfTTUM() + "_TTUM_"
					+ beanObj.getLocalDate().replaceAll("/", "-") + ".zip";
			obj.generateExcelTTUM(stPath, fileName, Excel_data,
					"VISA" + beanObj.getStSubCategory().replaceAll("\\s", "") + "TTUM_"
							+ beanObj.getLocalDate().replaceAll("/", "-"),
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
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			logger.info("File Name is " + beanObj.getFileName());
			boolean executed = false;
			TTUMData = SETTLTTUMSERVICE.getVISATTUM(beanObj);
			System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
			fileName = "VISA" + beanObj.getStSubCategory().replaceAll("\\s", "")
					+ beanObj.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
					+ beanObj.getLocalDate().replaceAll("/", "-") + "_VAL.txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", "-"), beanObj.getCategory());
			obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData, "VISA", beanObj);
			logger.info("File is created");
			logger.info("File is created");
			zipName = "VISA" + beanObj.getStSubCategory().replaceAll("\\s", "")
					+ beanObj.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
					+ beanObj.getLocalDate().replaceAll("/", "-") + "_VAL.txt";

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

	public void DownloadUnmatchedTTUMVISA2(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.DownloadUnmatchedTTUM post Start ****");
		logger.info("DownloadUnmatchedTTUM POST" + beanObj.getTTUMCategory());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " " + beanObj.getTypeOfTTUM() + " " + beanObj.getStSubCategory()
				+ " " + beanObj.getCategory() + "  " + beanObj.getLocalDate());
		String fileName = "", fileName2 = "", zipName = "";
		if (beanObj.getTTUMCategory().contains("REPORT")) {
			List<Object> Excel_data = new ArrayList();
			List<Object> Excel_data2 = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			logger.info("File Name is " + beanObj.getFileName());
			boolean executed = false;
			SettlementBean SettlementBean = new SettlementBean();
			SettlementBean.setDatepicker(beanObj.getLocalDate());
			TTUMData = nfsTTUMService.downloadDhanaReport(SettlementBean);
			TTUMData2 = nfsTTUMService.downloadDhanaReport2(SettlementBean);
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", "-"), beanObj.getCategory());
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			Map<String, String> table_Data = new HashMap<>();
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("ATM_ID");
			Column_list.add("TYPE");
			Column_list.add("AMOUNT");
			Column_list.add("BR_CODE");
			Column_list.add("ISS_SOL_ID");
			Column_list.add("REMARKS");
			Column_list.add("DCRS_REMARKS");
			Column_list.add("FILEDATE");
			Column_list.add("CREATEDDATE");
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			Excel_data2.add(Column_list);
			Excel_data2.add(TTUMData2);
			System.out.println("filename in nfs ttum is " + fileName);
			fileName = "VISA_" + beanObj.getStSubCategory().replaceAll("\\s", "") + beanObj.getTypeOfTTUM() + "_TTUM_"
					+ beanObj.getLocalDate().replaceAll("/", "-") + ".xls";
			fileName2 = "VISA2_" + beanObj.getStSubCategory().replaceAll("\\s", "") + beanObj.getTypeOfTTUM() + "_TTUM_"
					+ beanObj.getLocalDate().replaceAll("/", "-") + ".xls";
			zipName = "VISA_" + beanObj.getStSubCategory().replaceAll("\\s", "") + beanObj.getTypeOfTTUM() + "_TTUM_"
					+ beanObj.getLocalDate().replaceAll("/", "-") + ".zip";
			for (int i = 1; i <= 2; i++)
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
		} else {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			logger.info("File Name is " + beanObj.getFileName());
			boolean executed = false;
			TTUMData = SETTLTTUMSERVICE.getVISATTUM(beanObj);
			System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
			fileName = "VISA" + beanObj.getStSubCategory().replaceAll("\\s", "")
					+ beanObj.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
					+ beanObj.getLocalDate().replaceAll("/", "-") + "_VAL.txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate().replaceAll("/", "-"), beanObj.getCategory());
			obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData, "VISA", beanObj);
			logger.info("File is created");
			logger.info("File is created");
			zipName = "VISA" + beanObj.getStSubCategory().replaceAll("\\s", "")
					+ beanObj.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
					+ beanObj.getLocalDate().replaceAll("/", "-") + "_VAL.txt";
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

	@RequestMapping(value = { "DownloadUnmatchedTTUMVISANB" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadUnmatchedTTUMVISANB(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.DownloadUnmatchedTTUM post Start ****");
		logger.info("DownloadUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " " + beanObj.getTypeOfTTUM() + " " + beanObj.getStSubCategory()
				+ " " + beanObj.getCategory() + "  " + beanObj.getLocalDate());
		String fileName = "", zipName = "";
		if (beanObj.getTTUMCategory().contains("REPORT")) {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			logger.info("File Name is " + beanObj.getFileName());
			boolean executed = false;
			TTUMData = SETTLTTUMSERVICE.getTTUMVISANB(beanObj);
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			Map<String, String> table_Data = new HashMap<>();
			Column_list.add("DR_CR");
			Column_list.add("ACCOUNT_NO");
			Column_list.add("CARD_NUMBER");
			Column_list.add("AUTHORIZATION_CODE");
			Column_list.add("FILEDATE");
			Column_list.add("ARN");
			Column_list.add("PURCHASE_DATE");
			Column_list.add("DESTINATION_AMOUNT");
			Column_list.add("MERCHANT_COUNTRY_CODE");
			Column_list.add("SETTLEMENT_FLAG");
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			System.out.println("filename in nfs ttum is " + fileName);
			fileName = "VISA_NB_TTUM" + beanObj.getLocalDate() + ".xls";
			zipName = "VISA_NB_TTUM" + beanObj.getLocalDate() + ".zip";
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
			TTUMData = SETTLTTUMSERVICE.getVISATTUMNB(beanObj);
			System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
			fileName = "VISANBTTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
			obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData, "VISA", beanObj);
			logger.info("File is created");
			logger.info("File is created");
			zipName = "VISANBTTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
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

	@RequestMapping(value = { "DownloadUnmatchedTTUMATMPOSCROSSROUNTING" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadUnmatchedTTUMATMPOSCROSSROUNTING(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.DownloadUnmatchedTTUMATMPOSCROSSROUNTING post Start ****"
				+ beanObj.getTTUMCategory());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		String fileName = "", zipName = "";
		if (beanObj.getTTUMCategory().contains("REPORT")) {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			logger.info("File Name is " + beanObj.getFileName());
			boolean executed = false;
			TTUMData = SETTLTTUMSERVICE.getTTUMATMPOSCROSSROUNTING(beanObj);
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			Map<String, String> table_Data = new HashMap<>();
			Column_list.add("DR_CR");
			Column_list.add("ACCOUNT_NO");
			Column_list.add("CARD_NUMBER");
			Column_list.add("AUTHORIZATION_CODE");
			Column_list.add("FILEDATE");
			Column_list.add("ARN");
			Column_list.add("PURCHASE_DATE");
			Column_list.add("DESTINATION_AMOUNT");
			Column_list.add("MERCHANT_COUNTRY_CODE");
			Column_list.add("SETTLEMENT_FLAG");
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			System.out.println("filename in nfs ttum is " + fileName);
			if (beanObj.getTTUMCategory().equalsIgnoreCase("ATM_REPORT")) {
				fileName = "ATMCROSSROUTINGTTUM" + beanObj.getLocalDate() + ".xls";
				zipName = "ATMCROSSROUTINGTTUM" + beanObj.getLocalDate() + ".zip";
			} else {
				fileName = "POSCROSSROUTINGTTUM" + beanObj.getLocalDate() + ".xls";
				zipName = "POSCROSSROUTINGTTUM" + beanObj.getLocalDate() + ".zip";
			}
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
			TTUMData = SETTLTTUMSERVICE.getVISATTUMATMPOSCROSSROUNTING(beanObj);
			System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
			if (beanObj.getTTUMCategory().equalsIgnoreCase("ATM_TTUM")) {
				fileName = "ATMCROSSROUTINGTTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
			} else {
				fileName = "POSCROSSROUTINGTTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
			}
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
			obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData, "VISA", beanObj);
			if (beanObj.getTTUMCategory().equalsIgnoreCase("ATM_TTUM")) {
				zipName = "ATMCROSSROUTINGTTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
			} else {
				zipName = "POSCROSSROUTINGTTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
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

	@RequestMapping(value = { "DownloadNFSTOVISACROSSROUNTING" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadNFSTOVISACROSSROUNTING(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** DownloadNFSTOVISACROSSROUNTING.DownloadNFSTOVISACROSSROUNTING post Start ****"
				+ beanObj.getTTUMCategory());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		String fileName = "", zipName = "";
		if (beanObj.getTTUMCategory().contains("REPORT")) {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			logger.info("Created by is " + Createdby);
			beanObj.setCreatedBy(Createdby);
			logger.info("File Name is " + beanObj.getFileName());
			boolean executed = false;
			TTUMData = SETTLTTUMSERVICE.getTTUMNFSTOVISACROSSROUNTING(beanObj);
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			Map<String, String> table_Data = new HashMap<>();
			Column_list.add("DR_CR");
			Column_list.add("AC_NO");
			Column_list.add("PARTICULARS");
			Column_list.add("AMOUNT");
			Column_list.add("NARRATION");
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			System.out.println("filename in nfs ttum is " + fileName);
			if (beanObj.getTTUMCategory().equalsIgnoreCase("ATM_REPORT")) {
				fileName = "NFSTOVISACROSSROUTINGTTUM" + beanObj.getLocalDate() + ".xls";
				zipName = "NFSTOVISACROSSROUTINGTTUM" + beanObj.getLocalDate() + ".zip";
			} else {
				fileName = "NFSTOVISACROSSROUTINGTTUM" + beanObj.getLocalDate() + ".xls";
				zipName = "NFSTOVISACROSSROUTINGTTUM" + beanObj.getLocalDate() + ".zip";
			}
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
			TTUMData = SETTLTTUMSERVICE.getVISATTUMNFSTOVISACROSSROUNTING(beanObj);
			System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
			fileName = "NFSTOVISACROSSROUTINGTTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
			obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData, "VISA", beanObj);
			zipName = "NFSTOVISACROSSROUTINGTTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
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

	@RequestMapping(value = { "DownloadUnmatchedTTUMVISANFSRUPAY" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadUnmatchedTTUMVISANFSRUPAY(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.DownloadUnmatchedTTUMVISANFSRUPAY post Start ****");
		logger.info("DownloadUnmatchedTTUMVISANFSRUPAY POST");
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
			TTUMData = SETTLTTUMSERVICE.getTTUMVISANFSRUPAY(beanObj);
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			Map<String, String> table_Data = new HashMap<>();
			Column_list.add("DR_CR");
			Column_list.add("POSTED_DATE");
			Column_list.add("CARD_NO");
			Column_list.add("TRACE_NO");
			Column_list.add("AC_NO");
			Column_list.add("TRAN_DATE");
			Column_list.add("AMOUNT");
			Column_list.add("GL_ACCOUNT");
			Column_list.add("NARRATION");
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			System.out.println("filename in nfs ttum is " + fileName);
			fileName = "NFS_RUPAY_ROUTE_TTUM" + beanObj.getLocalDate() + ".xls";
			zipName = "NFS_RUPAY_ROUTE_TTUM" + beanObj.getLocalDate() + ".zip";
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
			TTUMData = SETTLTTUMSERVICE.getVISATTUMNFSRUPAY(beanObj);
			System.out.println("nfsSettlementBean.getStSubCategory()" + beanObj.getStSubCategory());
			fileName = "NFSRUPAYROUTETTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getLocalDate(), beanObj.getCategory());
			obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData, "VISA", beanObj);
			logger.info("File is created");
			logger.info("File is created");
			zipName = "NFSRUPAYROUTETTUM" + beanObj.getLocalDate().replaceAll("-", "") + "_VAL.txt";
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

	@RequestMapping(value = { "GenerateUnmatchedTTUMRUPAY" }, method = { RequestMethod.POST })
	@ResponseBody
	public String GenerateUnmatchedTTUMPostRUPAY(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.GenerateUnmatchedTTUM post Start ****");
		logger.info("GenerateUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " localDate is " + beanObj.getLocalDate());
		logger.info("filedate is " + beanObj.getFileDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		System.out.println("st sub category for domestic tum is " + beanObj.getStSubCategory());
		System.out.println("FILENAme is " + beanObj.getCategory());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		beanObj.setFileDate(beanObj.getLocalDate());
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedRUPAY(beanObj);
		System.out.println("CHECK WHETHER RECON IS PROCESSED OR NOT");
		if (output != null && !((Boolean) output.get("result")).booleanValue()) {
			if (beanObj.getCategory().contains("RUPAY")) {
				if (beanObj.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
					if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
						executed = rupayTTUMService.runTTUMProcessRUPAY(beanObj);
					} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
						executed = rupayTTUMService.runTTUMProcessRUPAY2(beanObj);
					} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
						executed = rupayTTUMService.runTTUMProcessRUPAY3(beanObj);
					} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
						executed = rupayTTUMService.runTTUMProcessRUPAY5(beanObj);
					} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
						executed = rupayTTUMService.runTTUMProcessRUPAY6(beanObj);
					} else {
						executed = rupayTTUMService.runTTUMProcessRUPAY4(beanObj);
					}
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
					executed = rupayTTUMService.runTTUMProcessRUPAYINT(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					executed = rupayTTUMService.runTTUMProcessRUPAY2INT(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					executed = rupayTTUMService.runTTUMProcessRUPAY3INT(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					executed = rupayTTUMService.runTTUMProcessRUPAY5INT(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					executed = rupayTTUMService.runTTUMProcessRUPAY6INT(beanObj);
				} else {
					executed = rupayTTUMService.runTTUMProcessRUPAY4INT(beanObj);
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
				if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
					executed = rupayTTUMService.runTTUMProcessQSPARC(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
					executed = rupayTTUMService.runTTUMProcessQSPARC2(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
					executed = rupayTTUMService.runTTUMProcessQSPARC3(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
					executed = rupayTTUMService.runTTUMProcessQSPARC5(beanObj);
				} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					executed = rupayTTUMService.runTTUMProcessQSPARC6(beanObj);
				} else {
					executed = rupayTTUMService.runTTUMProcessQSPARC4(beanObj);
				}
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DECLINE")) {
				executed = rupayTTUMService.runTTUMProcessQSPARCINT(beanObj);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("PROACTIVE")) {
				executed = rupayTTUMService.runTTUMProcessQSPARC2INT(beanObj);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("DROP")) {
				executed = rupayTTUMService.runTTUMProcessQSPARC3INT(beanObj);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")) {
				executed = rupayTTUMService.runTTUMProcessQSPARC5(beanObj);
			} else if (beanObj.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
				executed = rupayTTUMService.runTTUMProcessQSPARC5(beanObj);
			} else {
				executed = rupayTTUMService.runTTUMProcessQSPARC4INT(beanObj);
			}
			if (executed)
				return "Processing Completed Successfully! \n Please download Report";
			return "Issue while processing!";
		}
		if (output != null)
			return output.get("msg").toString();
		return "Issue while validating TTUM Process";
	}

	@RequestMapping(value = { "checkTTUMProcessedRUPAY" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkTTUMProcessedRUPAY(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		try {
			logger.info("RupayTTUMController: checkTTUMProcessed: Entry");
			beanObj.setFileDate(beanObj.getLocalDate());
			return "success";
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return "Exception";
		}
	}

	@RequestMapping(value = { "DownloadUnmatchedTTUMRUAPY" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadUnmatchedTTUMRUAPY(@ModelAttribute("nfsSettlementBean") UnMatchedTTUMBean nfsSettlementBean,
			HttpServletRequest request, HttpServletResponse response, HttpSession httpSession,
			RedirectAttributes redirectAttributes, Model model) throws Exception {
		logger.info("***** DownloadSettlementreport.POST Start ****");
		String fileName = "";
		String zipName = "";
		logger.info("DownloadUnmatchedTTUMRUAPY POST" + nfsSettlementBean.getTTUMCategory());
		if (nfsSettlementBean.getTTUMCategory().contains("REPORT")) {
			List<Object> Excel_data = new ArrayList();
			List<Object> TTUMData = new ArrayList();
			List<Object> TTUMData2 = new ArrayList();
			List<Object> PBGB_TTUMData = new ArrayList();
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is " + Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			logger.info("File Name is " + nfsSettlementBean.getCategory());
			boolean executed = false;
			nfsSettlementBean.getCategory().replaceAll("QSPARC,", "");
			TTUMData = SETTLTTUMSERVICE.getTTUMRUPAY(nfsSettlementBean);
			System.out.println("nfsSettlementBean.getStSubCategory()" + nfsSettlementBean.getStSubCategory());
			if (nfsSettlementBean.getCategory().contains("RUPAY")) {
				fileName = "RUPAY" + nfsSettlementBean.getTypeOfTTUM() + "TTUM"
						+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			} else {
				fileName = "QSPARC" + nfsSettlementBean.getTypeOfTTUM() + "TTUM"
						+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			}
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getLocalDate().replaceAll("/", ""),
					nfsSettlementBean.getCategory());
			logger.info("File is created");
			List<String> Column_list = new ArrayList<>();
			Map<String, String> table_Data = new HashMap<>();
			if (nfsSettlementBean.getCategory().contains("RUPAY")) {
				if (nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("DECLINE")
						|| nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")
						|| nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
					Column_list.add("DR_CR");
					Column_list.add("DISPUTE_DATE");
					Column_list.add("BANK_NAME");
					Column_list.add("CARD_NO");
					Column_list.add("AC_NO");
					Column_list.add("DATE_OF_TXN");
					Column_list.add("AMOUNT");
					Column_list.add("TRACE_NO");
					Column_list.add("NARRATION");
				} else if (nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("SURCHARGED")
						|| nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("SURCHARGEC")) {
					if (nfsSettlementBean.getStSubCategory().contains("DOMESTIC")) {
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
					} else {
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
					}
				} else {
					Column_list.add("DR_CR");
					Column_list.add("DISPUTE_DATE");
					Column_list.add("BANK_NAME");
					Column_list.add("CARD_NO");
					Column_list.add("AC_NO");
					Column_list.add("DATE_OF_TXN");
					Column_list.add("AMOUNT");
					Column_list.add("TRACE_NO");
					Column_list.add("NARRATION");
				}
			} else if (nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("DECLINE")
					|| nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("LATE PRESENTMENT")
					|| nfsSettlementBean.getTypeOfTTUM().equalsIgnoreCase("OFFLINE PRESENTMENT")) {
				Column_list.add("DR_CR");
				Column_list.add("DISPUTE_DATE");
				Column_list.add("BANK_NAME");
				Column_list.add("CARD_NO");
				Column_list.add("AC_NO");
				Column_list.add("DATE_OF_TXN");
				Column_list.add("AMOUNT");
				Column_list.add("TRACE_NO");
				Column_list.add("NARRATION");
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
			System.out.println("filename in nfs ttum is " + fileName);
			if (nfsSettlementBean.getCategory().contains("RUPAY")) {
				if (nfsSettlementBean.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
					fileName = "RUPAY_DOM_" + nfsSettlementBean.getTypeOfTTUM() + "_TTUM_"
							+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + ".xls";
					zipName = "RUPAY_DOM_" + nfsSettlementBean.getTypeOfTTUM() + "_TTUM_"
							+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + ".zip";
				} else {
					fileName = "RUPAY_INT_" + nfsSettlementBean.getTypeOfTTUM() + "_TTUM_"
							+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + ".xls";
					zipName = "RUPAY_INT" + nfsSettlementBean.getTypeOfTTUM() + "_TTUM_"
							+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + ".zip";
				}
			} else if (nfsSettlementBean.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
				fileName = "QSPARC_DOM_" + nfsSettlementBean.getTypeOfTTUM() + "_TTUM_"
						+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + ".xls";
				zipName = "QSPARC_DOM_" + nfsSettlementBean.getTypeOfTTUM() + "_TTUM_"
						+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + ".zip";
			} else {
				fileName = "QSPARC_INT_" + nfsSettlementBean.getTypeOfTTUM() + "_TTUM_"
						+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + ".xls";
				zipName = "QSPARC_INT" + nfsSettlementBean.getTypeOfTTUM() + "_TTUM_"
						+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + ".zip";
			}
			if (nfsSettlementBean.getCategory().contains("RUPAY")) {
				obj.generateExcelTTUM(stPath, fileName, Excel_data,
						nfsSettlementBean.getStSubCategory() + " " + nfsSettlementBean.getTypeOfTTUM() + " TTUM_"
								+ nfsSettlementBean.getLocalDate().replaceAll("/", "-"),
						zipName);
			} else {
				obj.generateExcelTTUM(stPath, fileName, Excel_data,
						nfsSettlementBean.getStSubCategory() + " " + nfsSettlementBean.getTypeOfTTUM() + " TTUM_"
								+ nfsSettlementBean.getLocalDate().replaceAll("/", "-"),
						zipName);
			}
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
			TTUMData = SETTLTTUMSERVICE.getDailyNFSTTUMDataRUPAY(nfsSettlementBean);
			if (nfsSettlementBean.getCategory().contains("RUPAY")) {
				if (nfsSettlementBean.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
					fileName = "RUPAYDOM" + nfsSettlementBean.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
							+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + "_VAL.txt";
				} else {
					fileName = "RUPAYINT" + nfsSettlementBean.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
							+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + "_VAL.txt";
				}
			} else if (nfsSettlementBean.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
				fileName = "QSPARCDOM" + nfsSettlementBean.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
						+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			} else {
				fileName = "QSPARCINT" + nfsSettlementBean.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
						+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			}
			String stPath = OUTPUT_FOLDER;
			logger.info("TEMP_DIR" + stPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getLocalDate().replaceAll("/", ""),
					nfsSettlementBean.getCategory());
			obj.generateADJTTUMFilesRUPAYRUFUND(stPath, fileName, 1, TTUMData, "NFS", nfsSettlementBean);
			logger.info("File is created");
			logger.info("File is created");
			if (nfsSettlementBean.getCategory().contains("RUPAY")) {
				if (nfsSettlementBean.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
					zipName = "RUPAYDOM" + nfsSettlementBean.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
							+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + "_VAL.txt";
				} else {
					zipName = "RUPAYINT" + nfsSettlementBean.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
							+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + "_VAL.txt";
				}
			} else if (nfsSettlementBean.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
				zipName = "QSPARCDOM" + nfsSettlementBean.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
						+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + "_VAL.txt";
			} else {
				zipName = "QSPARCINT" + nfsSettlementBean.getTypeOfTTUM().replaceAll("\\s", "") + "TTUM"
						+ nfsSettlementBean.getLocalDate().replaceAll("/", "") + "_VAL.txt";
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

	@RequestMapping(value = { "rollbackTTUMRUAPY" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMRUAPY(@RequestParam("filedate") String filedate,
			@RequestParam("typeOfTTUM") String typeOfTTUM, @RequestParam("subCat") String subCat,
			@RequestParam("category") String category) throws ParseException, Exception {

		logger.info("rollbackTTUMReport  " + filedate + " " + typeOfTTUM);
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		beanObj.setFileDate(filedate);
		beanObj.setTypeOfTTUM(typeOfTTUM);
		beanObj.setStSubCategory(subCat);
		beanObj.setCategory(category);
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedRUPAY(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue()) {
			boolean checkProcFlag;
			if (beanObj.getCategory().contains("RUPAY")) {
				if (beanObj.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
					if (typeOfTTUM.contains("DECLINE")) {
						checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY(beanObj);
					} else if (typeOfTTUM.contains("PROACTIVE")) {
						checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY2(beanObj);
					} else if (typeOfTTUM.contains("DROP")) {
						checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY3(beanObj);
					} else if (typeOfTTUM.contains("SURCHARGED")) {
						checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY4(beanObj);
					} else if (typeOfTTUM.contains("SURCHARGEC")) {
						checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY4(beanObj);
					} else if (typeOfTTUM.contains("LATE PRESENTMENT")) {
						checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY6(beanObj);
					} else {
						checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY7(beanObj);
					}
				} else if (typeOfTTUM.contains("DECLINE")) {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAYINT(beanObj);
				} else if (typeOfTTUM.contains("PROACTIVE")) {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY2INT(beanObj);
				} else if (typeOfTTUM.contains("DROP")) {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY3INT(beanObj);
				} else if (typeOfTTUM.contains("SURCHARGED")) {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY4INT(beanObj);
				} else if (typeOfTTUM.contains("SURCHARGEC")) {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY4INT(beanObj);
				} else if (typeOfTTUM.contains("LATE PRESENTMENT")) {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY6INT(beanObj);
				} else {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY7INT(beanObj);
				}
			} else if (beanObj.getStSubCategory().equalsIgnoreCase("DOMESTIC")) {
				if (typeOfTTUM.contains("DECLINE")) {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMQSPARC(beanObj);
				} else if (typeOfTTUM.contains("PROACTIVE")) {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMQSPARC2(beanObj);
				} else if (typeOfTTUM.contains("DROP")) {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMQSPARC3(beanObj);
				} else if (typeOfTTUM.contains("SURCHARGED")) {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMQSPARC4(beanObj);
				} else if (typeOfTTUM.contains("SURCHARGEC")) {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMQSPARC4(beanObj);
				} else if (typeOfTTUM.contains("LATE PRESENTMENT")) {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMQSPARC6(beanObj);
				} else {
					checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMQSPARC7(beanObj);
				}
			} else if (typeOfTTUM.contains("DECLINE")) {
				checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAYINT(beanObj);
			} else if (typeOfTTUM.contains("PROACTIVE")) {
				checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY2INT(beanObj);
			} else if (typeOfTTUM.contains("DROP")) {
				checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY3INT(beanObj);
			} else if (typeOfTTUM.contains("SURCHARGED")) {
				checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY4INT(beanObj);
			} else if (typeOfTTUM.contains("SURCHARGEC")) {
				checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY4INT(beanObj);
			} else {
				checkProcFlag = SETTLTTUMSERVICE.rollBackTTUMRUPAY6(beanObj);
			}
			if (checkProcFlag)
				return String.valueOf(typeOfTTUM) + " TTUM Rollback Completed";
			return String.valueOf(typeOfTTUM) + " TTUM Failed";
		}
		return String.valueOf(typeOfTTUM) + " TTUM is Already Rollback!";
	}

	@RequestMapping(value = { "rollbackTTUMVISA" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMVISA(@RequestParam("filedate") String filedate,
			@RequestParam("typeOfTTUM") String typeOfTTUM, @RequestParam("subCat") String subCat,
			@RequestParam("category") String category) throws ParseException, Exception {
		logger.info("rollbackTTUMReport  " + filedate + " " + typeOfTTUM);
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		beanObj.setFileDate(filedate);
		beanObj.setLocalDate(filedate);
		beanObj.setTypeOfTTUM(typeOfTTUM);
		beanObj.setStSubCategory(subCat);
		beanObj.setCategory(category);
		HashMap<String, Object> output2 = null;
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedVISA(beanObj);
		if (output != null && ((Boolean) output.get("result")).booleanValue())
			return String.valueOf(typeOfTTUM) + " TTUM is Already Rollback!";
		if (beanObj.getStSubCategory().equalsIgnoreCase("ISS")) {
			if (typeOfTTUM.equalsIgnoreCase("ORIGINAL WITHDRAWL REVERSAL")) {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA(beanObj);
			} else if (typeOfTTUM.equalsIgnoreCase("PROACTIVE")) {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA2(beanObj);
			} else if (typeOfTTUM.equalsIgnoreCase("DROP")) {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA3(beanObj);
			} else if (typeOfTTUM.equalsIgnoreCase("DEBIT SURCHARGE")) {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA4(beanObj);
			} else if (typeOfTTUM.equalsIgnoreCase("CREDIT SURCHARGE")) {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA5(beanObj);
			} else if (typeOfTTUM.equalsIgnoreCase("LATE PRESENTMENT")) {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA6(beanObj);
			} else if (typeOfTTUM.equalsIgnoreCase("REFUND")) {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA7(beanObj);
			} else if (typeOfTTUM.equalsIgnoreCase("REFUND_REV")) {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA8(beanObj);
			} else if (typeOfTTUM.equalsIgnoreCase("REFUND_INT")) {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA9(beanObj);
			} else {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA10(beanObj);
			}
		} else if (beanObj.getStSubCategory().equalsIgnoreCase("ACQ DOM ATM")) {
			if (typeOfTTUM.equalsIgnoreCase("LORO DEBIT")) {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA3POS(beanObj);
			} else if (typeOfTTUM.equalsIgnoreCase("LORO CREDIT")) {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA2POS(beanObj);
			} else {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA5INT(beanObj);
			}
		} else if (beanObj.getStSubCategory().equalsIgnoreCase("ACQ INT ATM")) {
			if (typeOfTTUM.equalsIgnoreCase("LORO DEBIT")) {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA4POS(beanObj);
			} else if (typeOfTTUM.equalsIgnoreCase("LORO CREDIT")) {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA5POS(beanObj);
			} else {
				output2 = SETTLTTUMSERVICE.rollBackTTUMVISA5INT(beanObj);
			}
		} else {
			boolean bool = SETTLTTUMSERVICE.rollBackTTUMRUPAY6(beanObj);
		}
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
			return "success";
		return "TTUM is not processed for selected date";
	}

	@RequestMapping(value = { "rollbackTTUMVISANB" }, method = { RequestMethod.POST })
	@ResponseBody
	public String rollbackTTUMVISANB(@RequestParam("filedate") String filedate) throws ParseException, Exception {
		logger.info("rollbackTTUMReport  " + filedate);
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		HashMap<String, Object> output2 = null;
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessedVISANB(beanObj);
		output2 = SETTLTTUMSERVICE.rollBackTTUMVISA5(beanObj);
		if (output2 != null && ((Boolean) output2.get("result")).booleanValue())
			return "success";
		return "TTUM is not processed for selected date";
	}

	@RequestMapping(value = { "GenerateNFSTTUM" }, method = { RequestMethod.GET })
	public ModelAndView GenerateNFSTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateNFSTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateNFSTTUM");
		logger.info("***** RupayTTUMController.GenerateNFSTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateJCBTTUM" }, method = { RequestMethod.GET })
	public ModelAndView GenerateJCBTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateNFSTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateJCBTTUM");
		logger.info("***** RupayTTUMController.GenerateNFSTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateDFSTTUM" }, method = { RequestMethod.GET })
	public ModelAndView GenerateDFSTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateNFSTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateDFSTTUM");
		logger.info("***** RupayTTUMController.GenerateNFSTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateICDTTUM" }, method = { RequestMethod.GET })
	public ModelAndView GenerateICDTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateICDTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateICDTTUM");
		logger.info("***** RupayTTUMController.GenerateNFSTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateICCWTTUM" }, method = { RequestMethod.GET })
	public ModelAndView GenerateICCWTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateICCWTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateICCWTTUM");
		logger.info("***** GenerateICCWTTUM.GenerateICCWTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "PGPDecryptPage" }, method = { RequestMethod.GET })
	public ModelAndView PGPDecryptPage(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateNFSRUPAYTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("PGPDecryptPage");
		System.out.println("start");
		try {
			if (category.equalsIgnoreCase("NFS")) {
				Process process = Runtime.getRuntime()
						.exec("\\\\10.0.174.26\\idbi intech\\NFSPGPTool\\PGPEncryption.exe");
			} else {
				Process process = Runtime.getRuntime()
						.exec("\\\\10.0.174.26\\idbi intech\\RUPAYPGPTool\\PGPEncryption.exe");
			}
		} catch (Exception e) {
			System.out.println("error" + e);
			return modelAndView;
		}
		System.out.println("end");
		logger.info("***** RupayTTUMController.GenerateNFSRUPAYTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateNFSRUPAYTTUM" }, method = { RequestMethod.GET })
	public ModelAndView GenerateNFSRUPAYTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateNFSRUPAYTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateNFSRUPAYTTUM");
		logger.info("***** RupayTTUMController.GenerateNFSRUPAYTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "GenerateRUPAYTTUM" }, method = { RequestMethod.GET })
	public ModelAndView GenerateRUPAYTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateNFSTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateRUPAYTTUM");
		logger.info("***** RupayTTUMController.GenerateNFSTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "RupayInternationalTTUM" }, method = { RequestMethod.GET })
	public ModelAndView RupayInternationalTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** RupayInternationalTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateRupayInternationalTTUM");
		logger.info("***** RupayTTUMController.GenerateUnmatchedTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "ProcessRupayInternationalTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ProcessRupayInternationalTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** ProcessRupayInternationalTTUM post Start ****");
		logger.info("ProcessRupayInternationalTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " localDate is " + beanObj.getLocalDate());
		logger.info("filedate is " + beanObj.getFileDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		HashMap<String, Object> output = rupayTTUMService.checkInternationalTTUMProcessed(beanObj);
		if (output != null && !((Boolean) output.get("result")).booleanValue()) {
			if (beanObj.getTypeOfTTUM().equalsIgnoreCase("MEMBERFUND")) {
				output = rupayTTUMService.checkInternationalUpload(beanObj);
			} else {
				output.put("result", Boolean.valueOf(true));
			}
			if (output != null && ((Boolean) output.get("result")).booleanValue()) {
				executed = rupayTTUMService.runInternationalTTUMProcess(beanObj);
			} else {
				return output.get("msg").toString();
			}
			if (executed)
				return "Processing Completed Successfully! \n Please download Report";
			return "Issue while processing!";
		}
		if (output != null)
			return output.get("msg").toString();
		return "Issue while validating TTUM Process";
	}

	@RequestMapping(value = { "checkIntTTUMProcessed" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkIntTTUMProcessed(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		try {
			logger.info("RupayTTUMController: checkIntTTUMProcessed: Entry");
			HashMap<String, Object> output = rupayTTUMService.checkInternationalTTUMProcessed(beanObj);
			if (output != null && ((Boolean) output.get("result")).booleanValue())
				return "success";
			return "TTUM is not processed for selected date";
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return "Exception";
		}
	}

	@RequestMapping(value = { "DownloadInternationalTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadInternationalTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.DownloadInternationalTTUM post Start ****");
		logger.info("DownloadInternationalTTUM POST");
		List<Object> TTUMData = new ArrayList();
		String stnewDate = null;
		String fileName = "TTUM";
		if ((beanObj.getFileDate() == null || beanObj.getFileDate().equalsIgnoreCase(""))
				&& beanObj.getLocalDate() != null)
			beanObj.setFileDate(beanObj.getLocalDate());
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		Date date = sdf.parse(beanObj.getFileDate());
		List<Object> Excel_data = new ArrayList();
		sdf = new SimpleDateFormat("dd-MM-yyyy");
		stnewDate = sdf.format(date);
		String TEMP_DIR = System.getProperty("java.io.tmpdir");
		logger.info("new date is " + stnewDate);
		logger.info("TEMP_DIR" + TEMP_DIR);
		logger.info("1 " + (TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/") ? 0 : 1));
		if (!TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/")) {
			logger.info(Boolean.valueOf(!TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/")));
			TEMP_DIR = String.valueOf(TEMP_DIR) + File.separator;
		}
		String stpath = TEMP_DIR;
		beanObj.setStPath(stpath);
		logger.info("Path is " + stpath);
		boolean directoryCreated = rupayTTUMService.checkAndMakeDirectory(beanObj);
		if (directoryCreated) {
			stpath = String.valueOf(stpath) + beanObj.getCategory() + File.separator + stnewDate;
			logger.info("new path is " + stpath);
			logger.info("filedate is " + beanObj.getFileDate());
			TTUMData = rupayTTUMService.getInternationalTTUMData(beanObj);
			fileName = "RUPAY_" + beanObj.getTypeOfTTUM() + "_TTUM.txt";
			rupayTTUMService.generateInternationalTTUMFile(stpath, fileName, TTUMData);
		}
		File file = new File(String.valueOf(stpath) + File.separator + fileName);
		logger.info("path of zip file " + stpath + File.separator + fileName);
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

	@RequestMapping(value = { "NIHReport" }, method = { RequestMethod.GET })
	public ModelAndView NIHReport(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("NIHReportDownload");
		logger.info("***** RupayTTUMController.GenerateUnmatchedTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "checkNIHData" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkNIHData(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj, FileDetailsJson dataJson,
			ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		try {
			logger.info("RupayTTUMController: checkNIHData: Entry");
			HashMap<String, Object> output = rupayTTUMService.checkNIHRecords(beanObj);
			if (output != null && ((Boolean) output.get("result")).booleanValue())
				return "success";
			return output.get("msg").toString();
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return "Exception";
		}
	}

	@RequestMapping(value = { "DownloadNIHReport" }, method = { RequestMethod.POST })
	public String DownloadNIHReport(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession, Model model)
			throws Exception {
		logger.info("***** DownloadNIHReport.POST Start ****");
		logger.info("NFSSettlement POST");
		List<Object> Excel_data = new ArrayList();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		beanObj.setCreatedBy(Createdby);
		Excel_data = rupayTTUMService.getNIHReport(beanObj);
		model.addAttribute("ReportName", String.valueOf(beanObj.getTypeOfTTUM()) + "_NIH_" + beanObj.getLocalDate());
		model.addAttribute("data", Excel_data);
		logger.info("***** NFSSettlementController.NFSSettlementProcess Daily POST End ****");
		return "GenerateNFSDailyReport";
	}

	@RequestMapping(value = { "GenerateCardtoCardTTUM" }, method = { RequestMethod.GET })
	public ModelAndView GenerateCardtoCardTTUM(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** GenerateCardtoCardTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
		String display = "";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean();
		logger.info("in GetHeaderList" + category);
		beanObj.setCategory(category);
		subcat = iSourceService.getSubcategories(category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateCardToCardTTUM");
		logger.info("***** RupayTTUMController.GenerateCardtoCardTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "ProcessCardToCardTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public String ProcessCardToCardTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** ProcessCardToCardTTUM post Start ****");
		logger.info("ProcessCardToCardTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby + " localDate is " + beanObj.getLocalDate());
		logger.info("filedate is " + beanObj.getFileDate() + " ttum type is " + beanObj.getTypeOfTTUM());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		HashMap<String, Object> output = rupayTTUMService.checkCardToCardTTUMProcessed(beanObj);
		if (output != null && !((Boolean) output.get("result")).booleanValue()) {
			executed = rupayTTUMService.runCardToCardTTUMProcess(beanObj);
			if (executed)
				return "Processing Completed Successfully! \n Please download Report";
			return "Issue while processing!";
		}
		if (output != null)
			return output.get("msg").toString();
		return "Issue while validating TTUM Process";
	}

	@RequestMapping(value = { "checkCTCTTUMProcessed" }, method = { RequestMethod.POST })
	@ResponseBody
	public String checkCtCTTUMProcessed(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView, HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		try {
			logger.info("RupayTTUMController: checkCtCTTUMProcessed: Entry");
			HashMap<String, Object> output = rupayTTUMService.checkCardToCardTTUMProcessed(beanObj);
			if (output != null && ((Boolean) output.get("result")).booleanValue())
				return "success";
			return "TTUM is not processed for selected date";
		} catch (Exception e) {
			logger.info("Exception is " + e);
			return "Exception";
		}
	}

	@RequestMapping(value = { "DownloadCardToCardTTUM" }, method = { RequestMethod.POST })
	@ResponseBody
	public void DownloadCardToCardTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.DownloadInternationalTTUM post Start ****");
		logger.info("DownloadInternationalTTUM POST");
		List<Object> TTUMData = new ArrayList();
		String stnewDate = null;
		String fileName = "TTUM";
		if ((beanObj.getFileDate() == null || beanObj.getFileDate().equalsIgnoreCase(""))
				&& beanObj.getLocalDate() != null)
			beanObj.setFileDate(beanObj.getLocalDate());
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		Date date = sdf.parse(beanObj.getFileDate());
		List<Object> Excel_data = new ArrayList();
		sdf = new SimpleDateFormat("dd-MM-yyyy");
		stnewDate = sdf.format(date);
		String TEMP_DIR = System.getProperty("java.io.tmpdir");
		logger.info("new date is " + stnewDate);
		logger.info("TEMP_DIR" + TEMP_DIR);
		logger.info("1 " + (TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/") ? 0 : 1));
		if (!TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/")) {
			logger.info(Boolean.valueOf(!TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/")));
			TEMP_DIR = String.valueOf(TEMP_DIR) + File.separator;
		}
		String stpath = TEMP_DIR;
		beanObj.setStPath(stpath);
		logger.info("Path is " + stpath);
		boolean directoryCreated = rupayTTUMService.checkAndMakeDirectory(beanObj);
		if (directoryCreated) {
			stpath = String.valueOf(stpath) + beanObj.getCategory() + File.separator + stnewDate;
			logger.info("new path is " + stpath);
			logger.info("filedate is " + beanObj.getFileDate());
			TTUMData = rupayTTUMService.getCardToCardTTUMData(beanObj);
			fileName = "CARDTOCARD_" + beanObj.getTypeOfTTUM() + "_TTUM.txt";
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			obj.generateDRMTTUM(stpath, fileName, TTUMData, "CARDTOCARD");
		}
		File file = new File(String.valueOf(stpath) + File.separator + fileName);
		logger.info("path of zip file " + stpath + File.separator + fileName);
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

	@RequestMapping(value = { "downloadfunding" }, method = { RequestMethod.POST })
	@ResponseBody
	public void downloadfundingpost(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) throws Exception {
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is " + Createdby);
		List<Object> TTUMData = new ArrayList();
		beanObj.setCreatedBy(Createdby);
		String fileName = "";
		String fileNameCsv = "";
		System.out.println("SUBCATEGORY IS " + beanObj.getStSubCategory());
		List<Object> Excel_data = new ArrayList();
		String TEMP_DIR = System.getProperty("java.io.tmpdir");
		String stnewDate = null;
		logger.info("new date is " + stnewDate);
		logger.info("TEMP_DIR" + TEMP_DIR);
		logger.info("1 " + (TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/") ? 0 : 1));
		if (!TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/")) {
			logger.info(Boolean.valueOf(!TEMP_DIR.substring(TEMP_DIR.length() - 1).equalsIgnoreCase("\\/")));
			TEMP_DIR = String.valueOf(TEMP_DIR) + File.separator;
		}
		String newPath = String.valueOf(OUTPUT_FOLDER) + File.separator + beanObj.getStSubCategory();
		beanObj.setStPath(newPath);
		logger.info("Path is " + newPath);
		fileName = String.valueOf(beanObj.getStSubCategory()) + "_FUNDING_TTUM.txt";
		File folder = new File(newPath);
		if (folder.exists())
			folder.delete();
		folder.mkdir();
		File file1 = new File(folder, fileName);
		try {
			if (file1.exists())
				file1.delete();
			file1.createNewFile();
			System.out.println("path of new file is---------- " + newPath);
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			if (beanObj.getStSubCategory().equalsIgnoreCase("ATMACQ")
					|| beanObj.getStSubCategory().equalsIgnoreCase("ATMISS")
					|| beanObj.getStSubCategory().equalsIgnoreCase("ECOMPOS")) {
				TTUMData = rupayTTUMService.getFundingdata(beanObj);
				System.out.println("filename is" + fileName);
				List<Object> DRMData = (List<Object>) TTUMData.get(0);
				obj.generateMultipleDRMTTUMFiles(newPath, fileName, 1, TTUMData, "fund");
			}
			logger.info("File is created");
			System.out.println("newPath is" + newPath);
			File file = new File(String.valueOf(newPath) + File.separator + fileName);
			logger.info("path of zip file " + newPath + File.separator + fileName);
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
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception in DownloadUnmatchedTTUM " + e);
		}
	}

	@RequestMapping(value = { "/getNpciFiles.do" }, method = { RequestMethod.POST })
	@ResponseBody
	public List<RecordCount> getNpciFiles(HttpSession session, HttpServletResponse response, HttpServletRequest req)
			throws Exception {
		return rupayTTUMService.downloadRawdataSummary();
	}
}
