package com.recon.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.recon.model.CompareBean;
import com.recon.model.CompareSetupBean;
import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.service.CompareService;
import com.recon.service.FilterationService;
import com.recon.service.ICompareConfigService;
import com.recon.service.IReconProcessService;
import com.recon.service.ISourceService;
import com.recon.service.SettlementTTUMService;
import com.recon.util.CSRFToken;
import com.recon.util.FileDetailsJson;
import com.recon.util.demo;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ReconProcess {
	private static final String ERROR_MSG = "error_msg";

	private static final String SUCCESS_MSG = "success_msg";

	private static final Logger logger = Logger.getLogger(com.recon.control.ReconProcess.class);

	@Autowired
	ICompareConfigService icompareConfigService;

	@Autowired
	CompareService compareService;

	@Autowired
	FilterationService filterationService;

	@Autowired
	IReconProcessService reconProcess;

	@Autowired
	ISourceService iSourceService;

	@Autowired
	SettlementTTUMService SETTLTTUMSERVICE;

	@RequestMapping(value = { "ReconProcess" }, method = { RequestMethod.GET })
	public ModelAndView ReconProcess1(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** ReconProcess.ReconProcess1 Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<>();
		String display = "";
		logger.info("RECON PROCESS GET");
		List<String> subcat = new ArrayList<>();
		logger.info("in GetHeaderList" + category);
		subcat = this.iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC"))
			display = "none";
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.setViewName("ReconProcess");
		logger.info("***** ReconProcess.ReconProcess1 End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "ReconProcessMC" }, method = { RequestMethod.GET })
	public ModelAndView ReconProcessMC(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** ReconProcess.ReconProcess1 Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<>();
		String display = "";
		logger.info("RECON PROCESS GET");
		List<String> subcat = new ArrayList<>();
		logger.info("in GetHeaderList" + category);
		subcat = this.iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC"))
			display = "none";
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.setViewName("ReconProcessMC");
		logger.info("***** ReconProcess.ReconProcess1 End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "VISACrossReconProcess" }, method = { RequestMethod.GET })
	public ModelAndView VISACrossReconProcess(ModelAndView modelAndView, @RequestParam("category") String category,
			HttpServletRequest request) throws Exception {
		logger.info("***** ReconProcess.ReconProcess1 Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<>();
		String display = "";
		logger.info("RECON PROCESS GET");
		List<String> subcat = new ArrayList<>();
		logger.info("in GetHeaderList" + category);
		subcat = this.iSourceService.getSubcategories(category);
		if (category.equals("ONUS") || category.equals("AMEX") || category.equals("WCC"))
			display = "none";
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory", subcat);
		modelAndView.addObject("display", display);
		modelAndView.setViewName("VISACrossReconProcess");
		logger.info("***** ReconProcess.ReconProcess1 End ****");
		return modelAndView;
	}

	@RequestMapping(value = { "SeeRule" }, method = { RequestMethod.GET })
	public ModelAndView seeRule(ModelAndView modelAndView) {
		modelAndView.setViewName("SeeRule");
		return modelAndView;
	}

	@RequestMapping(value = { "/Filedetails" }, method = { RequestMethod.POST })
	@ResponseBody
	public FileDetailsJson GetHeaderList(@RequestParam("category") String category, FileDetailsJson dataJson)
			throws Exception {
		logger.info("***** ReconProcess.GetHeaderList Start ****");
		try {
			JSONObject objJSON = new JSONObject();
			List<CompareSetupBean> setupBeans = new ArrayList<>();
			logger.info("in GetHeaderList" + category);
			HashMap<String, Object> JSONROOT = new HashMap<>();
			Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
			setupBeans = this.icompareConfigService.getFileList(category);
			JSONROOT.put("Records", setupBeans);
			String jsonArray = gson.toJson(JSONROOT);
			dataJson.setParams("OK", setupBeans, 0);
			objJSON.put("setupBeans", setupBeans);
			logger.info("***** ReconProcess.GetHeaderList End ****");
		} catch (Exception e) {
			demo.logSQLException(e, "ReconProcess.GetHeaderList");
			logger.error(" error in ReconProcess.GetHeaderList", new Exception("ReconProcess.GetHeaderList", e));
			dataJson.setParams("ERROR", e.getMessage());
		}
		return dataJson;
	}

	@RequestMapping(value = { "reconRollBack" }, method = { RequestMethod.POST })
	@ResponseBody
	public String reconRollBack(@RequestParam("filedate") String filedate, @RequestParam("subCat") String subCat,
			@RequestParam("Cat") String Cat) throws ParseException, Exception {
		NFSSettlementBean beanObj = new NFSSettlementBean();
		logger.info("rollbackCTC  " + filedate + " " + subCat + Cat);
		beanObj.setStSubCategory(subCat);
		beanObj.setDatepicker(filedate);
		beanObj.setCategory(Cat);
		String msg = "MSG not Define";
		System.out.println("data " + subCat + " " + beanObj.getCategory());
		CompareBean compareBean = new CompareBean();
		compareBean.setStSubCategory(subCat);
		compareBean.setStEntryBy("INT12016");
		List<CompareSetupBean> compareSetupBeans = null;
		if (!beanObj.getCategory().equalsIgnoreCase("VISACROSS")) {
			compareSetupBeans = this.reconProcess.getFileList(Cat, filedate, subCat);
			if (beanObj.getCategory().equalsIgnoreCase("CARDTOCARD")) {
				if (this.reconProcess.validateFile1CTC2(subCat, compareSetupBeans, filedate) == null) {
					if (subCat.contains("ACQUIRER") && beanObj.getCategory().equalsIgnoreCase("CARDTOCARD")) {
						boolean bool = this.SETTLTTUMSERVICE.rollBackACQCTC(beanObj);
						if (bool)
							return "Rollback Completed";
						return "Failed";
					}

					if (subCat.contains("ISSUER") && beanObj.getCategory().equalsIgnoreCase("CARDTOCARD")) {
						boolean bool = this.SETTLTTUMSERVICE.rollBackISSCTC(beanObj);
						if (bool)
							return "Rollback Completed";
						return "Failed";
					}
					boolean checkProcFlag = this.SETTLTTUMSERVICE.rollBackISSCTC(beanObj);
					if (checkProcFlag)
						return "Rollback Completed";
					return "Failed";
				} else {

				}
				msg = "Allready Rollback";
				return msg;
			} else if (beanObj.getCategory().equalsIgnoreCase("JCB")) {

				if (this.reconProcess.validateFile1CTC3(subCat, compareSetupBeans, filedate) != null) {
					if (subCat.contains("ACQUIRER") && beanObj.getCategory().equalsIgnoreCase("JCB")) {
						boolean bool = this.SETTLTTUMSERVICE.rollBackNFSJCBACQ(beanObj);
						if (bool)
							return "Rollback Completed";
						return "Failed";
					}

				} else {
					msg = "Allready Rollback";
					return msg;
				}

			} else if (beanObj.getCategory().equalsIgnoreCase("DFS")) {

				if (this.reconProcess.validateFile1CTC4(subCat, compareSetupBeans, filedate) != null) {
					if (subCat.contains("ACQUIRER") && beanObj.getCategory().equalsIgnoreCase("DFS")) {
						boolean bool = this.SETTLTTUMSERVICE.rollBackNFSDFSACQ(beanObj);
						if (bool)
							return "Rollback Completed";
						return "Failed";
					}

				} else {
					msg = "Allready Rollback";
					return msg;

				}

			}
			if (this.reconProcess.validateFile1(Cat, compareSetupBeans, filedate) == "Recon is already processed") {
				if (subCat.contains("DOMESTIC") && beanObj.getCategory().equalsIgnoreCase("RUPAY")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackACQRupay(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("INTERNATIONAL") && beanObj.getCategory().equalsIgnoreCase("RUPAY")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackINTRupay(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("DOMESTIC") && beanObj.getCategory().equalsIgnoreCase("QSPARC")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackQSPARC(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ISSUER") && beanObj.getCategory().equalsIgnoreCase("ICD")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackICDISS(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ACQUIRER") && beanObj.getCategory().equalsIgnoreCase("ICD")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackICDACQ(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ACQUIRER") && beanObj.getCategory().equalsIgnoreCase("ICCW")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackICCWDACQ(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ISSUER") && beanObj.getCategory().equalsIgnoreCase("ICCW")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackICCWDISS(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ISSUER") && beanObj.getCategory().equalsIgnoreCase("NFS")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackNFSISS(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ACQUIRER") && beanObj.getCategory().equalsIgnoreCase("NFS")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackNFSACQ(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ACQUIRER") && beanObj.getCategory().equalsIgnoreCase("JCB")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackNFSJCBACQ(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ACQUIRER") && beanObj.getCategory().equalsIgnoreCase("DFS")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackNFSDFSACQ(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.equalsIgnoreCase("ISS") && beanObj.getCategory().equalsIgnoreCase("VISA")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackVISAISSATM(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ISS INT POS") && beanObj.getCategory().equalsIgnoreCase("VISA")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackVISAISSINTPOS(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ACQ DOM ATM") && beanObj.getCategory().equalsIgnoreCase("VISA_ACQ")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackVISACQ(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ACQ INT ATM") && beanObj.getCategory().equalsIgnoreCase("VISA_ACQ")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackVISACQ1(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ISS DOM POS") && beanObj.getCategory().equalsIgnoreCase("VISA")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackVISAISSPOS(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ISS INT ATM") && beanObj.getCategory().equalsIgnoreCase("VISA")) {
					boolean bool = this.SETTLTTUMSERVICE.rollBackVISAISSINTATM(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ISSUER") && beanObj.getCategory().equalsIgnoreCase("MASTERCARD")) {
					System.out.println("ISSUER");
					boolean bool = this.SETTLTTUMSERVICE.rollBackMASTERCARDISSATM(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("MC_INT_CBS_DOM_ATM_CROSS MC_CROSS")
						&& beanObj.getCategory().equalsIgnoreCase("MC_CROSS")) {
					System.out.println("ISSUER_POS ");
					boolean bool = this.SETTLTTUMSERVICE.rollBackMASTERCARDISSPOS(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ACQUIRER_DOM") && beanObj.getCategory().equalsIgnoreCase("MASTERCARD")) {
					System.out.println("ISSUER_POS ");
					boolean bool = this.SETTLTTUMSERVICE.rollBackMASTERCARDACQATM(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				if (subCat.contains("ACQUIRER_INT") && beanObj.getCategory().equalsIgnoreCase("MASTERCARD")) {
					System.out.println("ISSUER_POS ");
					boolean bool = this.SETTLTTUMSERVICE.rollBackMASTERCARDACQATMINT(beanObj);
					if (bool)
						return "Rollback Completed";
					return "Failed";
				}
				boolean checkProcFlag = this.SETTLTTUMSERVICE.rollBackVISAISSATM(beanObj);
				if (checkProcFlag)
					return "Rollback Completed";
				return "Failed";
			}
			msg = "Allready Rollback";
			return msg;
		}
		if (this.reconProcess.validateFile4(subCat, compareSetupBeans, filedate) != null) {
			if (subCat.equalsIgnoreCase("POS INT CBS DOM VISA")) {
				logger.info("Inside cross loop");
				boolean checkProcFlag = this.SETTLTTUMSERVICE.CROSS_RECON_VISA_POS_INT_DOM_ROLLBACK(beanObj);
				if (checkProcFlag)
					return "Rollback is processed";
				return "Issue while processing recon";
			}
			if (subCat.equalsIgnoreCase("ATM INT CBS DOM VISA")) {
				logger.info("Inside cross loop");
				boolean checkProcFlag = this.SETTLTTUMSERVICE.CROSS_RECON_VISA_ATM_INT_DOM_ROLLBACK(beanObj);
				if (checkProcFlag)
					return "Rollback is processed";
				return "Issue while processing recon";
			}
			if (subCat.equalsIgnoreCase("POS DOM CBS INT VISA")) {
				logger.info("Inside cross loop");
				boolean checkProcFlag = this.SETTLTTUMSERVICE.CROSS_RECON_VISA_POS_DOM_INT_ROLLBACK(beanObj);
				if (checkProcFlag)
					return "Rollback is processed";
				return "Issue while processing recon";
			}
			if (subCat.equalsIgnoreCase("ATM DOM CBS INT VISA")) {
				logger.info("Inside cross loop");
				boolean checkProcFlag = this.SETTLTTUMSERVICE.CROSS_RECON_VISA_ATM_DOM_INT_ROLLBACK(beanObj);
				if (checkProcFlag)
					return "Rollback is processed";
				return "Issue while processing recon";
			}
		} else {
			msg = "CROSS RECON Allready processed!!";
			logger.info(msg);
		}
		return msg;
	}

	@RequestMapping(value = { "runProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String runProcess(@RequestParam("category") String category, FileDetailsJson dataJson,
			ModelAndView modelAndView, CompareSetupBean setupBean, HttpSession httpSession,
			@RequestParam("filedate") String filedate, @RequestParam("subCat") String subCat,
			@RequestParam("dollar_val") String dollar_val) {
		String msg = null;
		try {
			logger.info("***** ReconProcess.runProcess Start ****" + dollar_val + subCat);
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			List<CompareSetupBean> compareSetupBeans = null;
			if (category.equalsIgnoreCase("MASTERCARD")) {
				compareSetupBeans = this.reconProcess.getFileList2(category, filedate, subCat, dollar_val);
			} else if (!category.equalsIgnoreCase("VISACROSS")) {
				compareSetupBeans = this.reconProcess.getFileList(category, filedate, subCat);
			}
			CompareBean compareBean = new CompareBean();
			compareBean.setStSubCategory(dollar_val);
			compareBean.setStEntryBy(Createdby);
			if (compareSetupBeans != null || category.equalsIgnoreCase("MC_CROSS")) {
				if (category.equalsIgnoreCase("MC_CROSS")) {
					logger.info("Inside CardtoCardRecon loop");
					boolean result = this.reconProcess.VISACROSSPROCPOSINTDOM(category, filedate,
							compareBean.getStEntryBy());
					if (result)
						return "Recon is processed";
					return "Issue while processing recon";
				}
				if (category.equalsIgnoreCase("CARDTOCARD")) {
					if (this.reconProcess.validateFile1CTC(subCat, compareSetupBeans, filedate) == null) {
						if (subCat.equalsIgnoreCase("ACQUIRER")) {
							logger.info("Inside CardtoCardRecon loop");
							if (this.reconProcess.CardtoCardACQClassify(category, filedate,
									compareBean.getStEntryBy())) {
								boolean bool = this.reconProcess.CardtoCardACQPRC(category, filedate,
										compareBean.getStEntryBy());
								if (bool)
									return "Recon is processed";
								return "Issue while processing recon";
							}
							return "ACQ Classify not proccess";
						}
						logger.info("Inside CardtoCardRecon loop");
						boolean result = this.reconProcess.CardtoCardISSPRC(category, filedate,
								compareBean.getStEntryBy());
						if (result)
							return "Recon is processed";
						return "Issue while processing recon";
					}else {
						
						msg = "Recon is proceed";

					}
					
					logger.info(msg);
				} else if (category.equalsIgnoreCase("JCB")) {
					if (this.reconProcess.validateFile1CTC3(subCat, compareSetupBeans, filedate) == null) {
						if (subCat.equalsIgnoreCase("ACQUIRER")) {
							if (this.reconProcess.processFile(category, compareSetupBeans, filedate, Createdby,
									subCat)) {
								msg = "All process are completed. Please go-to settlement to see the result.";
							} else {
								msg = "Recon Process Not Completed";
								logger.info(msg);
							}
						}
						return msg;

					} else {

						msg = "Already Processed";
						return msg;
					}

				} else if (category.equalsIgnoreCase("DFS")) {
					if (this.reconProcess.validateFile1CTC4(subCat, compareSetupBeans, filedate) == null) {
						if (subCat.equalsIgnoreCase("ACQUIRER")) {
							if (this.reconProcess.processFile(category, compareSetupBeans, filedate, Createdby,
									subCat)) {
								msg = "All process are completed. Please go-to settlement to see the result.";
							} else {
								msg = "Recon Process Not Completed";
								logger.info(msg);
							}
						} else {

							return msg;
						}

					} else {

						msg = "Already Processed";
						return msg;
					}

				} else if (!category.equalsIgnoreCase("RUPAY") || !subCat.equalsIgnoreCase("INTERNATIONAL")) {
					msg = this.reconProcess.chkFileUpload(category, filedate, compareSetupBeans, subCat);
				}
				logger.info("msg for chkFileUpload is " + msg);
				String error_msg = "";
				if (msg == null) {
					error_msg = this.reconProcess.validateFile1(category, compareSetupBeans, filedate);
					if (error_msg == null) {
						if (!category.equalsIgnoreCase("CARDTOCARD")) {
	
							if (category.equalsIgnoreCase("JCB") && subCat.equalsIgnoreCase("ACQUIRER")) {
								if (this.reconProcess.processFile(category, compareSetupBeans, filedate, Createdby,
										subCat)) {
									msg = "All process are completed. Please go-to settlement to see the result.";
								} else {
									msg = "Recon Process Not Completed";
									logger.info(msg);
								}
								logger.info(msg);
							} else {
								
								if (category.equalsIgnoreCase("MASTERCARD") && !subCat.equalsIgnoreCase("ISSUER")) {
									if (this.reconProcess.processFile(category, compareSetupBeans, filedate, Createdby,
											subCat)) {
										msg = "All process are completed. Please go-to settlement to see the result.";
									} else {
										msg = "Recon Process Not Completed";
										
									}
									logger.info(msg);
								}else {
									if (this.reconProcess.processFile(category, compareSetupBeans, filedate, Createdby,
											subCat)) {
										if (this.reconProcess.compareFiles(category, filedate, compareBean, subCat,
												dollar_val)) {
											logger.info("RECON COMPLETED AT " + new Timestamp((new Date()).getTime()));
											msg = "All process are completed. Please go-to settlement to see the result.";
										} else {
											msg = "Recon Process Not Completed";
											logger.info(msg);
										}
									} else {
										msg = "classification not completed";
										logger.info(msg);
									}
									
								}
								
								
								
							}
						} else {
							if (category.equalsIgnoreCase("RUPAY") && subCat.equalsIgnoreCase("INTERNATIONAL")) {
								logger.info("Inside international loop");
								HashMap<String, Object> output = this.reconProcess.checkRupayIntRecon(filedate,
										category);
								if (output != null && !((Boolean) output.get("result")).booleanValue()) {
									output = this.reconProcess.processRupayIntRecon(filedate, Createdby);
									if (output != null && ((Boolean) output.get("result")).booleanValue())
										return "Recon Processing completed";
									return "Recon is not processed";
								}
								return output.get("msg").toString();
							}
							msg = "Previous File Not processed.";
							logger.info(msg);
							if (category.equalsIgnoreCase("QSPARC") && subCat.equalsIgnoreCase("INTERNATIONAL")) {
								logger.info("Inside international loop");
								HashMap<String, Object> output = this.reconProcess.checkRupayIntRecon(filedate,
										category);
								if (output != null && !((Boolean) output.get("result")).booleanValue()) {
									output = this.reconProcess.processRupayIntRecon(filedate, Createdby);
									if (output != null && ((Boolean) output.get("result")).booleanValue())
										return "Recon Processing completed";
									return "Recon is not processed";
								}
								return output.get("msg").toString();
							}
							msg = "Previous File Not processed.";
							logger.info(msg);
						}
					} else {
						msg = error_msg;
						return msg;
					}
				} else {
					return msg;
				}
				logger.info("***** ReconProcess.runProcess End ****");
				return msg;
			}
			logger.info("Files are Not Configure For Selected Category");
			return "Files are Not Configure For Selected Category";
		} catch (Exception e) {
			try {
				demo.logSQLException(e, "ReconProcess.runProcess");
			} catch (SQLException e1) {
				logger.error(" error in ReconProcess.runProcess", new Exception("ReconProcess.runProcess", e));
			}
			logger.error(" error in ReconProcess.runProcess", new Exception("ReconProcess.runProcess", e));
			dataJson.setParams("ERROR", e.getMessage());
			return "Exception";
		}
	}

	@RequestMapping(value = { "runVISACrossProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String runVISACrossProcess(@RequestParam("category") String category, FileDetailsJson dataJson,
			ModelAndView modelAndView, CompareSetupBean setupBean, HttpSession httpSession,
			@RequestParam("filedate") String filedate, @RequestParam("subCat") String subCat,
			@RequestParam("dollar_val") String dollar_val) {
		String msg = null;
		try {
			List<CompareSetupBean> compareSetupBeans;
			logger.info("***** ReconProcess.runProcess Start ****" + dollar_val + subCat);
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			if (category.equalsIgnoreCase("MASTERCARD")) {
				compareSetupBeans = this.reconProcess.getFileList2(category, filedate, subCat, dollar_val);
			} else {
				compareSetupBeans = this.reconProcess.getFileList(category, filedate, subCat);
			}
			CompareBean compareBean = new CompareBean();
			compareBean.setStSubCategory(dollar_val);
			compareBean.setStEntryBy(Createdby);
			if (compareSetupBeans != null) {
				if (category.equalsIgnoreCase("CARDTOCARD")) {
					if (this.reconProcess.validateFile1CTC(subCat, compareSetupBeans, filedate) == null) {
						if (subCat.equalsIgnoreCase("ACQUIRER")) {
							logger.info("Inside CardtoCardRecon loop");
							if (this.reconProcess.CardtoCardACQClassify(category, filedate,
									compareBean.getStEntryBy())) {
								boolean result = this.reconProcess.CardtoCardACQPRC(category, filedate,
										compareBean.getStEntryBy());
								if (result)
									return "Recon is processed";
								return "Issue while processing recon";
							}
							return "ACQ Classify not proccess";
						}
						logger.info("Inside CardtoCardRecon loop");
						if (this.reconProcess.CardtoCardISSClassify(category, filedate, compareBean.getStEntryBy())) {
							boolean result = this.reconProcess.CardtoCardISSPRC(category, filedate,
									compareBean.getStEntryBy());
							if (result)
								return "Recon is processed";
							return "Issue while processing recon";
						}
						return "ISS Classify not proccess";
					}
					msg = "Previous File Not processed.";
					logger.info(msg);
				}
				if (!category.equalsIgnoreCase("RUPAY") || !subCat.equalsIgnoreCase("INTERNATIONAL"))
					msg = this.reconProcess.chkFileUpload(category, filedate, compareSetupBeans, subCat);
				logger.info("msg for chkFileUpload is " + msg);
				String error_msg = "";
				if (msg == null) {
					error_msg = this.reconProcess.validateFile1(category, compareSetupBeans, filedate);
					if (error_msg == null) {
						if (!category.equalsIgnoreCase("CARDTOCARD")) {
							if (this.reconProcess.processFile(category, compareSetupBeans, filedate, Createdby,
									subCat)) {
								if (this.reconProcess.compareFiles(category, filedate, compareBean, subCat,
										dollar_val)) {
									logger.info("RECON COMPLETED AT " + new Timestamp((new Date()).getTime()));
									msg = "All process are completed. Please go-to settlement to see the result.";
								} else {
									msg = "Recon Process Not Completed";
									logger.info(msg);
								}
							} else {
								msg = "classification not completed";
								logger.info(msg);
							}
						} else {
							if (category.equalsIgnoreCase("RUPAY") && subCat.equalsIgnoreCase("INTERNATIONAL")) {
								logger.info("Inside international loop");
								HashMap<String, Object> output = this.reconProcess.checkRupayIntRecon(filedate,
										category);
								if (output != null && !((Boolean) output.get("result")).booleanValue()) {
									output = this.reconProcess.processRupayIntRecon(filedate, Createdby);
									if (output != null && ((Boolean) output.get("result")).booleanValue())
										return "Recon Processing completed";
									return "Recon is not processed";
								}
								return output.get("msg").toString();
							}
							msg = "Previous File Not processed.";
							logger.info(msg);
							if (category.equalsIgnoreCase("QSPARC") && subCat.equalsIgnoreCase("INTERNATIONAL")) {
								logger.info("Inside international loop");
								HashMap<String, Object> output = this.reconProcess.checkRupayIntRecon(filedate,
										category);
								if (output != null && !((Boolean) output.get("result")).booleanValue()) {
									output = this.reconProcess.processRupayIntRecon(filedate, Createdby);
									if (output != null && ((Boolean) output.get("result")).booleanValue())
										return "Recon Processing completed";
									return "Recon is not processed";
								}
								return output.get("msg").toString();
							}
							msg = "Previous File Not processed.";
							logger.info(msg);
						}
					} else {
						msg = error_msg;
						return msg;
					}
				} else {
					return msg;
				}
				logger.info("***** ReconProcess.runProcess End ****");
				return msg;
			}
			logger.info("Files are Not Configure For Selected Category");
			return "Files are Not Configure For Selected Category";
		} catch (Exception e) {
			try {
				demo.logSQLException(e, "ReconProcess.runProcess");
			} catch (SQLException e1) {
				logger.error(" error in ReconProcess.runProcess", new Exception("ReconProcess.runProcess", e));
			}
			logger.error(" error in ReconProcess.runProcess", new Exception("ReconProcess.runProcess", e));
			dataJson.setParams("ERROR", e.getMessage());
			return "Exception";
		}
	}

	@RequestMapping(value = { "CheckStatus" }, method = { RequestMethod.POST })
	@ResponseBody
	public FileDetailsJson CheckStatus(@RequestParam("category") String category, FileDetailsJson dataJson,
			ModelAndView modelAndView, CompareSetupBean setupBean, HttpSession httpSession,
			@RequestParam("filedate") String filedate, @RequestParam("subcat") String subcat) {
		logger.info("***** ReconProcess.CheckStatus Start ****");
		try {
			CompareSetupBean bean = null;
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			List<CompareSetupBean> compareSetupBeans = this.reconProcess.getFileList(category, filedate, subcat);
			if (compareSetupBeans != null) {
				bean = this.reconProcess.chkStatus(compareSetupBeans, category, filedate);
				JSONObject objJSON = new JSONObject();
				logger.info("in GetHeaderList" + category);
				HashMap<String, Object> JSONROOT = new HashMap<>();
				Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
				JSONROOT.put("Records", bean);
				String jsonArray = gson.toJson(JSONROOT);
				dataJson.setParams("OK", bean, 0);
				objJSON.put("setupBeans", setupBean);
			}
			logger.info("***** ReconProcess.CheckStatus End ****");
		} catch (Exception e) {
			try {
				demo.logSQLException(e, "ReconProcess.CheckStatus");
			} catch (SQLException e1) {
				logger.error(" error in ReconProcess.CheckStatus", new Exception("ReconProcess.CheckStatus", e));
			}
			logger.error(" error in ReconProcess.CheckStatus", new Exception("ReconProcess.CheckStatus", e));
			dataJson.setParams("ERROR", e.getMessage());
		}
		return dataJson;
	}

	@RequestMapping(value = { "iccwrunProcess" }, method = { RequestMethod.POST })
	@ResponseBody
	public String iccwrunProcess(@RequestParam("category") String category, @RequestParam("filedate") String filedate,
			ModelAndView modelAndView, CompareSetupBean setupBean, HttpSession httpSession) {
		System.out.println("inside controller");
		String msg = null;
		try {
			logger.info("***** ReconProcess.runProcess Start ****");
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			CompareBean compareBean = new CompareBean();
			compareBean.setStEntryBy(Createdby);
			System.out.println("our recon category is " + category);
			boolean checkFileUp = this.reconProcess.checkFileUp(filedate);
			if (checkFileUp) {
				boolean checkRecon = this.reconProcess.checkRecon(filedate);
				if (!checkRecon) {
					if (this.reconProcess.iccwprocessFile(category, filedate, Createdby)) {
						logger.info("RECON COMPLETED AT " + new Timestamp((new Date()).getTime()));
						msg = "All process are completed. Please go-to settlement to see the result.";
					} else {
						msg = "Recon Process Completed";
						logger.info(msg);
					}
				} else {
					msg = "Recon Already completed for the selected date";
				}
			} else {
				msg = "File is not Uploaded";
			}
		} catch (Exception e) {
			System.out.println(e);
			msg = "Exception occured " + e;
			return msg;
		}
		return msg;
	}
}
