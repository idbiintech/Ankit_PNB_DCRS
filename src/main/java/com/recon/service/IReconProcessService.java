package com.recon.service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import com.recon.model.CompareBean;
import com.recon.model.CompareSetupBean;

public interface IReconProcessService {
	
	public String chkFileUpload(String Category,String filedate, List<CompareSetupBean> compareSetupBeans, String subCat) throws Exception;
	
	public List<CompareSetupBean> getFileList(String category,String filedate,String subcat) throws Exception;

	public List<CompareSetupBean> getFileList2(String category,String filedate,String subcat, String type) throws Exception;

	public String validateFile(String category,List<CompareSetupBean> compareSetupBeans,String filedate) throws Exception;

	public boolean processFile(String category,List<CompareSetupBean> compareSetupBeans, String filedate,String Createdby,String subCat) throws Exception;
	
	public boolean compareFiles(String category, String filedate ,CompareBean setupBeans,String subcat,String dollar_val)throws Exception ;

	public CompareSetupBean chkStatus(List<CompareSetupBean> compareSetupBeans,
			String category, String filedate) throws Exception;

	HashMap<String, Object> checkRupayIntRecon(String fileDate,String cetegory);
	
	HashMap<String, Object> processRupayIntRecon(String fileDate, String entryBy);
	
	HashMap<String, Object> checkCardtoCardRecon(String filedate, String subCat);
	
	boolean CardtoCardACQPRC(String category,String filedate, String entry_by) throws ParseException, Exception ;
	
	boolean VISACROSSPROCPOSINTDOM(String category,String filedate, String entry_by) throws ParseException, Exception ;
	boolean VISACROSSPROCATMINTDOM(String category,String filedate, String entry_by) throws ParseException, Exception ;
	boolean VISACROSSPROCPOSDOMINT(String category,String filedate, String entry_by) throws ParseException, Exception ;
	boolean VISACROSSPROCATMDOMINT(String category,String filedate, String entry_by) throws ParseException, Exception ;
	
	HashMap<String, Object> checkCardtoCardRawFiles(String filedate);
	
	HashMap<String, Object> checkCardtoCardPrevRecon(String filedate);
	
	//iccwprocessFile

	public boolean iccwprocessFile(String category,  String filedate,String Createdby) throws Exception;

	public boolean checkRecon(String filedate);

	public boolean checkFileUp(String filedate);

	String validateFile1(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception;

	String validateFile2(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception;
	String validateFile3(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception;
	String validateFile4(String category, List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception;

	String validateFile1CTC(String category, List<CompareSetupBean> compareSetupBeans, String filedate)
			throws Exception;

	String validateFile1CTC2(String category, List<CompareSetupBean> compareSetupBeans, String filedate)
			throws Exception;
	
	String validateFile1CTC3(String category, List<CompareSetupBean> compareSetupBeans, String filedate)
			throws Exception;

	String validateFile1CTC4(String category, List<CompareSetupBean> compareSetupBeans, String filedate)
			throws Exception;

	boolean CardtoCardISSPRC(String category, String filedate, String entry_by) throws ParseException, Exception;

	boolean CardtoCardACQClassify(String category, String filedate, String entry_by) throws ParseException, Exception;
	boolean CardtoCardISSClassify(String category, String filedate, String entry_by) throws ParseException, Exception;

}
