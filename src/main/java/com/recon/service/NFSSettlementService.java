package com.recon.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.AddNewSolBean;
import com.recon.model.CompareSetupBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.RupayUploadBean;

public interface NFSSettlementService {
	
	public HashMap<String, Object> validatePrevFileUpload(NFSSettlementBean beanObj);
	
	public HashMap<String, Object> validatePrevFileUploadSUS(NFSSettlementBean beanObj);
	
	public HashMap<String, Object> uploadDFSRawData(NFSSettlementBean banObj,MultipartFile file);
	
	//public HashMap<String, Object> validateNTSLUpload(NFSSettlementBean beanObj);
	
	public HashMap<String, Object> uploadNTSLFile(NFSSettlementBean beanObj,MultipartFile file);
	public HashMap<String, Object> uploadSUSPECTFile(NFSSettlementBean beanObj,MultipartFile file);
	
	public HashMap<String,Object> ValidateDailySettProcess(NFSSettlementBean beanObj);
	
	public HashMap<String,Object> checkNFSMonthlyProcess(NFSSettlementBean beanObj);
	
	public HashMap<String, Object> uploadMonthlyNTSLFile(NFSSettlementBean beanObj,MultipartFile file);
	
	public HashMap<String, Object> checkMonthlyNTSLUploaded(NFSSettlementBean beanObj);
	
	public HashMap<String,Object> ValidateDailyInterchangeProcess(NFSSettlementBean beanObj);
	
	public HashMap<String,Object> ValidateForSettVoucher(NFSSettlementBean beanObj);
	
	public boolean checkSettVoucherProcess(NFSSettlementBean beanObj);

	HashMap<String,Object> ValidateForAdjTTUM(NFSSettlementBean beanObj);
	
	HashMap<String,Object> ValidateForAdjPENALTYTTUM(NFSSettlementBean beanObj);
	HashMap<String,Object> ValidateForAdjTTUMICD(NFSSettlementBean beanObj);
	
	boolean checkAdjTTUMProcess(NFSSettlementBean beanObj);
	
	boolean checkAdjTTUMProcessICD(NFSSettlementBean beanObj);
	
	public HashMap<String,Object> ValidateCooperativeBank(NFSSettlementBean beanObj);
	
	public boolean checkCoopTTUMProcess(NFSSettlementBean beanObj);
	
	public HashMap<String,Object> validateSettDifference(NFSSettlementBean beanObj);
	
	boolean addCooperativeBank(String bankName, String accNumber);
	
	List<String> getNodalData(String state)throws Exception;
	
	boolean SaveNodalDetails(AddNewSolBean beanObj);
	
	HashMap<String,Object> CheckSettlementProcess(NFSSettlementBean beanObj);
	
	HashMap<String,Object> ValidateOtherSettProcess(NFSSettlementBean beanObj);
	
	//ICCW 
	public HashMap<String, Object> iccwuploadNTSLFile(NFSSettlementBean beanObj,MultipartFile file,CompareSetupBean setupBean);

	public boolean checkIccwFileUpload(CompareSetupBean setupBean);

	HashMap<String, Object> ValidateForAdjTTUMTEXT(NFSSettlementBean beanObj);
	HashMap<String, Object> ValidateForAdjTTUMTEXTICD(NFSSettlementBean beanObj);
	HashMap<String, Object> ValidateForrcon(NFSSettlementBean beanObj);

	boolean checkAdjTTUMProcessREPORT(NFSSettlementBean beanObj);

	boolean checkAdjTTUMProcessREPORTICD(NFSSettlementBean beanObj);

	boolean checkAdjTTUMProcessREPORTSETTL(NFSSettlementBean beanObj);
	
	boolean checkSettlementRupay(RupayUploadBean beanObj);
	boolean checkSettlementRupay2(RupayUploadBean beanObj);
	boolean checkSettlementVisa(RupayUploadBean beanObj);

	boolean checkSettlementRupayINT(RupayUploadBean beanObj);
	boolean checkSettlementQsparc(RupayUploadBean beanObj);
	boolean checkSettlementQsparcINT(RupayUploadBean beanObj);


	boolean checkAdjTTUMProcessSETTL(NFSSettlementBean beanObj);

	HashMap<String, Object> ValidateForAdjTTUMSETTL(NFSSettlementBean beanObj);

	HashMap<String, Object> ValidateForAdjTTUMTEXTSETTL(NFSSettlementBean beanObj);



}
