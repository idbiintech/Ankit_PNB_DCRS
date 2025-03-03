package com.recon.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.recon.model.CompareBean;
import com.recon.model.CompareSetupBean;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.model.LoginBean;
import com.recon.model.ManualFileBean;
import com.recon.service.CompareRupayService;
import com.recon.service.CompareService;
import com.recon.service.FilterationService;
import com.recon.service.ICompareConfigService;
import com.recon.service.IReconProcessService;
import com.recon.service.ManualFileService;
import com.recon.service.ManualRupayFileService;
import com.recon.service.SettlementService;



@Controller
public class ManualFileController {

	@Autowired
	ManualFileService manualFileService;
		
	@Autowired
	FilterationService filterService;
	
	
	@Autowired
	SettlementService settlementService;

	@Autowired 
	ManualRupayFileService manualRupayFileService;
	
	@Autowired
	CompareRupayService compareRupayService;
	
	@Autowired
	CompareService compareService;
	
	@Autowired
	ICompareConfigService icompareConfigService;
	
	@Autowired IReconProcessService reconProcess;
	
	private static final String ERROR_MSG = "error_msg";
	private static final String SUCCESS_MSG = "success_msg";
	private static final Logger logger = Logger.getLogger(ManualFileController.class);
	
	
	
	
@RequestMapping(value="ManualFileProcess", method= RequestMethod.GET)
public String getManualFileProcess(Model model,ManualFileBean manualFileBean)
{
	System.out.println("heyeeeee");
	logger.info("***********Inside manualFileProcess");
	model.addAttribute("CompareBean",manualFileBean);
	return "ManualFile";
}

@RequestMapping(value="manualFileProcess", method= RequestMethod.POST)
@ResponseBody
//public String ManualFileProcess(@ModelAttribute("manualFileBean")ManualFileBean manualFileBean,HttpSession httpsession,RedirectAttributes redirectAttributes)
public String ManualFileProcess(@RequestParam("cat")String category ,@RequestParam("Subcat")String subCategory, @RequestParam("Filedate")String fileDate,
		@RequestParam("selectedFile")String stSelectedFile,@RequestParam("ManFile")String ManFile,HttpSession httpsession)
{
	//System.out.println("HELLOOOOOO");
	//List<String> tables = new ArrayList<>();
	ManualFileBean manualFileBean = new ManualFileBean();
	try
	{
		String Createdby = ((LoginBean) httpsession.getAttribute("loginBean")).getUser_id();
		manualFileBean.setStCategory(category);
		manualFileBean.setStSubCategory(subCategory);
		manualFileBean.setStFile_date(fileDate);
		manualFileBean.setStFileSelected(stSelectedFile);
		manualFileBean.setManFile(ManFile);
		manualFileBean.setStManualFile("CBS");
		manualFileBean.setStFileName("SWITCH");
		manualFileBean.setStEntryBy(Createdby);
		List<Integer> rec_set_id;
		logger.info("Inside manualFileProcess");	
		
		CompareSetupBean
		setupBean = new CompareSetupBean();
		
		setupBean.setCompareLvl("2");
		setupBean.setCompareFile1(Integer.parseInt(manualFileBean.getStFileSelected()));
		setupBean.setCompareFile2(Integer.parseInt(manualFileBean.getManFile()));
		setupBean.setFileDate(manualFileBean.getStFile_date());
		
		//boolean result = true;
		boolean alreadyProcessed = icompareConfigService.CheckAlreadyProcessed(setupBean);
		if(!alreadyProcessed)
		{
			boolean result = icompareConfigService.chkCompareFiles(setupBean);

			if(result){
				if(manualFileBean.getStCategory().equals("ONUS") || manualFileBean.getStCategory().equals("AMEX"))
				{
					String stCategory = manualFileBean.getStCategory();
					if(!manualFileBean.getStSubCategory().equals("-"))
					{				
						stCategory = stCategory+"_"+manualFileBean.getStSubCategory();
						manualFileBean.setStMergerCategory(manualFileBean.getStCategory()+"_"+manualFileBean.getStSubCategory().substring(0, 3));
					}
					else
					{
						manualFileBean.setStMergerCategory(manualFileBean.getStCategory());
					}
					//1. Filteration process

					//2. kNOCKOFF PROCESS

					//3. compare process
					//for manual file consider table category_filename
					//tables.add("ONUS_MANUALCBS");
					// for other file consider recon_categoery_filename
					//	tables.add("ONUS_SWITCH");
					//tables.add("ONUS_MANUALCBS");//manual file
					//	tables.add("ONUS_CBS");
					//CREATE MANUAL RAW TABLE
					//	manualFileService.compareManualFile(tables,manualFileBean.getStFile_date());
					manualFileService.compareManualFile(manualFileBean);
					// FILTER MANUAL RAW TABLE AND INSERT THE RECORDS IN NEW ONUS TABLE
					FilterationBean filterBean = new FilterationBean();
					filterBean.setStCategory(manualFileBean.getStCategory());
					filterBean.setStMerger_Category(manualFileBean.getStMergerCategory());
					filterBean.setStSubCategory(manualFileBean.getStSubCategory());
					filterBean.setStFile_date(manualFileBean.getStFile_date());
					filterBean.setStFile_Name(manualFileBean.getStManualFile());
					filterBean.setStEntry_by(((LoginBean) httpsession.getAttribute("loginBean")).getUser_id());
					manualFileService.FilterRecords(filterBean);
					System.out.println("COMPLETED FILTERATION.............................................................................................");

					//PERFORM CATEGORIZATION OF CIA-GL AND RECON AND INSERT CIA-GL IN SETTLEMENT TABLE

					System.out.println("CATEGORIZATION STARTS HERE.............................................................................................");
					//PERFORM CATEGORIZATION OF CIA-GL RECORDS
					//String fileName = "ONUS_MANUAL_CBS";
					String fileName = manualFileBean.getStCategory()+"_MANUAL_"+manualFileBean.getStManualFile();
					//Move Manual Recon records to Settlement table
					if(fileName.contains("MANUAL"))
						//sush
						settlementService.manualReconToSettlement(manualFileBean);

					System.out.println("CATEGORIZATION ANALYSED.............................................................................................");	
					// UPDATE ACTION TAKEN ENTRIES
					manualFileService.updateActionTakenTTUMRecord(manualFileBean);

					//PERFORM KNOCKOFF ON MANUAL RECON TRANSACTIONC AND ONLINE RECON TRANSACTIONS
					KnockOffBean knockOffBean = new KnockOffBean();
					String a[] = fileName.split("_");
					knockOffBean.setStCategory(a[0]);
					knockOffBean.setStFile_Name(a[2]);
					knockOffBean.setStFile_date(manualFileBean.getStFile_date());
					knockOffBean.setStSubCategory(manualFileBean.getStSubCategory());
					knockOffBean.setStMergeCategory(manualFileBean.getStMergerCategory());
					knockOffBean.setStEntry_by(Createdby);
					manualFileService.getReconRecords(knockOffBean);

					KnockOffBean knockoffbeanObj = new KnockOffBean();
					knockoffbeanObj.setStCategory(manualFileBean.getStCategory());
					knockoffbeanObj.setStSubCategory(manualFileBean.getStSubCategory());
					knockoffbeanObj.setStFile_Name("SWITCH");
					knockoffbeanObj.setStFile_date(manualFileBean.getStFile_date());
					knockoffbeanObj.setStMergeCategory(manualFileBean.getStMergerCategory());
					knockOffBean.setStEntry_by(Createdby);
					manualFileService.getReconRecords(knockoffbeanObj);

					System.out.println("GET RECON RECORDS ANALYSED.............................................................................................");

					// USE KNOCKOFF LOGIC OF ONLINE FILE
					manualFileService.KnockOffRecords(knockOffBean);

					//USE COMPARE LOGIC OF ONLINE FILE
					List<String> table_list = new ArrayList<>();
					/*table_list.add("ONUS_SWITCH");
				table_list.add("ONUS_CBS");*/
					table_list.add(manualFileBean.getStManualFile());
					table_list.add(manualFileBean.getStFileName());

					rec_set_id = compareService.getRec_set_id(stCategory);

					for(int j= 0 ;j<rec_set_id.size() ; j++)
					{

						List<List<String>> tables_data = compareRupayService.getTableName(rec_set_id.get(j),stCategory);
						List<String> tables = tables_data.get(0);
						

						int movedData= manualFileService.moveData(tables,manualFileBean,rec_set_id.get(j));

						System.out.println("MOVE DATA ANALYSED.............................................................................................");

						if(movedData==1)
						{
							// compare logic
							System.out.println("COMPARE LOGIC");
							manualFileService.updateMatchedRecords(tables,manualFileBean,rec_set_id.get(j));
							
							//remove duplicates 
							CompareBean compareBeanObj = new CompareBean();
							compareBeanObj.setStCategory(manualFileBean.getStCategory());
							compareBeanObj.setStMergeCategory(manualFileBean.getStMergerCategory());
							compareBeanObj.setStSubCategory(manualFileBean.getStSubCategory());
							compareBeanObj.setStFile_date(manualFileBean.getStFile_date());
							
							compareService.removeDuplicates(tables, compareBeanObj, rec_set_id.get(j));
							
							System.out.println("COMPARE ANALYSED.............................................................................................");
							//System.out.println("completed matching of records");
							manualFileService.moveToRecon(tables,manualFileBean);

						}
						manualFileService.clearTables(tables, manualFileBean);
						
						if(j == 0)
						{
							manualFileService.TTUMRecords(tables, manualFileBean, rec_set_id.get(j));
						}
										
					}
				}
				else //if(manualFileBean.getStCategory().equals("RUPAY"))
				{

					boolean filter_done = false;

					//1. CATEGORIZATION OF RECORDS IN RUPAY-DOM
					FilterationBean filterBean = new FilterationBean();
					filterBean.setStEntry_by(((LoginBean) httpsession.getAttribute("loginBean")).getUser_id());
					String stCategory = manualFileBean.getStCategory();

					filterBean.setStCategory(manualFileBean.getStCategory());
					filterBean.setStSubCategory(manualFileBean.getStSubCategory());

					filterBean.setStFile_Name(manualFileBean.getStManualFile());
					System.out.println(manualFileBean.getStSubCategory()+manualFileBean.getStCategory());
					if(!manualFileBean.getStSubCategory().equals("-"))
					{				
						stCategory = stCategory+"_"+manualFileBean.getStSubCategory();
						filterBean.setStMerger_Category(manualFileBean.getStCategory()+"_"+manualFileBean.getStSubCategory().substring(0, 3));
						manualFileBean.setStMergerCategory(manualFileBean.getStCategory()+"_"+manualFileBean.getStSubCategory().substring(0, 3));
					}
					else
					{
						filterBean.setStMerger_Category(manualFileBean.getStCategory());
						manualFileBean.setStMergerCategory(manualFileBean.getStCategory());
					}
					//---------- GET ALL SUB CATEGORIES FROM DATABASE I.E FROM MAIN_RECON_SEQUENCE TABLE
					String msg = null;
					List<CompareSetupBean> compareSetupBeans = reconProcess.getFileList(manualFileBean.getStCategory(), manualFileBean.getStFile_date(),manualFileBean.getStSubCategory());
					if(compareSetupBeans !=null){
						for(CompareSetupBean setupbeanObj : compareSetupBeans)
						{	
							msg = manualRupayFileService.chkFileUpload(manualFileBean.getStCategory(), setupbeanObj, manualFileBean.getStFile_date(),manualFileBean.getStSubCategory());

							if(msg == null)
							{
								//setting data in bean
								setupbeanObj.setCategory(manualFileBean.getStCategory());
								setupbeanObj.setEntry_by(filterBean.getStEntry_by());
								setupbeanObj.setFileDate(manualFileBean.getStFile_date());
								if(!setupbeanObj.getStSubCategory().equals("-"))
								{
									setupbeanObj.setStMerger_Category(setupbeanObj.getCategory()+"_"+setupbeanObj.getStSubCategory().substring(0, 3));
								}
								//done


								//SET filterbean as per setup bean
								FilterationBean filterObj = new FilterationBean();
								filterObj.setStCategory(setupbeanObj.getCategory());
								filterObj.setStSubCategory(setupbeanObj.getStSubCategory());
								filterObj.setStEntry_by(setupbeanObj.getEntry_by());
								filterObj.setStFile_date(setupbeanObj.getFileDate());
								filterObj.setStFile_Name(setupbeanObj.getStFileName());
								filterObj.setStMerger_Category(setupbeanObj.getStMerger_Category());

								filter_done = true;
								//Perform filteration for man records in raw file with part_id = 2
								System.out.println("File to be processed is of date "+manualFileBean.getStFile_date());
								filterBean.setStFile_date(manualFileBean.getStFile_date());
								//CHECK WHETHER FILTERATION IS TO BE DONE.....
								String stFilter_Status = filterService.getStatus(filterObj,"FILTERATION");
								if(stFilter_Status.equals("Y"))
								{
									//FilterationBean filterObj = new FilterationBean();

									//1. get all search parameters from main_compare_details
									List<FilterationBean> search_params = filterService.getSearchParams(filterObj);
									filterObj.setSearch_params(search_params);



									//2. Generate seg_tran_id
									int seg_tran_id = filterService.getseg_tran_id();

									filterObj.setInseg_tran_id(seg_tran_id);

									//3. Make an entry in SEGREGATE TABLE 
									int entry_done = filterService.addEntry(filterObj);
									//System.out.println("hello : "+entry_done);

									if(entry_done == 1)
									{

										//4. Get data from RAW table using search parameters and then insert those records in table
										manualRupayFileService.FilterRecords(filterObj);

										//5. update status in main_seg_txn table
										filterService.updateseg_txn(filterObj);

									}



									//GETTING MAN CONTRA_ACCOUNT FOR 78000010021 ACC
									/*if(setupbeanObj.getStFileName().equals("CBS"))
								{*/
									List<String> table_list = new ArrayList<>();
									table_list.add(manualFileBean.getStFileName());
									table_list.add(manualFileBean.getStManualFile());

									manualRupayFileService.compareManualFile(table_list, setupbeanObj);




									//CATEGORIZE RECORDS FOR CIA-GL RECORDS
									//clonning of manualfilebean object
									ManualFileBean manualBeanObj = (ManualFileBean)manualFileBean.clone();
									manualBeanObj.setStSubCategory(setupbeanObj.getStSubCategory());
									manualBeanObj.setStMergerCategory(setupbeanObj.getStMerger_Category());
									settlementService.manualReconToSettlement(manualBeanObj);

									//Get recon records for Previous date for cbs file
									/*	filterObj.setStCategory(setupbeanObj.getCategory());
									filterObj.setStSubCategory(setupbeanObj.getStSubCategory());
									filterObj.setStFile_Name(setupbeanObj.getStFileName());
									filterObj.setStFile_date(manualFileBean.getStFile_date());
									filterObj.setStMerger_Category(setupbeanObj.getStMerger_Category());*/

									filterBean.setStFile_Name(manualFileBean.getStManualFile());
									manualRupayFileService.getReconRecords(filterObj);


									// 21NOV2017 WE NEED TO KNOCKOFF THESE RECORDS WITH FILTERED RECORDS
									manualRupayFileService.KnockOffRecords(filterObj);

									//}

								}
								//perform knockoff on cbs records if flag is Y
								/*if()
							{
								filterBean.setStFile_Name(manualFileBean.getStManualFile());
								manualRupayFileService.getReconRecords(filterBean);
							}*/


							}
						}
					}


					//block1//

					/*System.out.println("File to be processed is of date "+manualFileBean.getStFile_date());
				filterBean.setStFile_date(manualFileBean.getStFile_date());
			//CHECK WHETHER FILTERATION IS TO BE DONE.....
				String stFilter_Status = filterService.getStatus(filterBean,"FILTERATION");
				if(stFilter_Status.equals("Y"))
				{
					//1. get all search parameters from main_compare_details
					List<FilterationBean> search_params = filterService.getSearchParams(filterBean);
					filterBean.setSearch_params(search_params);



					//2. Generate seg_tran_id
					int seg_tran_id = filterService.getseg_tran_id();

					filterBean.setInseg_tran_id(seg_tran_id);

					//3. Make an entry in SEGREGATE TABLE 
					int entry_done = filterService.addEntry(filterBean);
					//System.out.println("hello : "+entry_done);

					if(entry_done == 1)
					{

						//4. Get data from RAW table using search parameters and then insert those records in table
						manualRupayFileService.FilterRecords(filterBean);

						//5. update status in main_seg_txn table
						filterService.updateseg_txn(filterBean);

					}

				}*/

					//block1 end//

					//Block 2//

					/*if(filter_done)
				{
					//2. MATCH SOL78000010021 RECORDS AND UPDATE MAN CONTRA ACCOUNT FIELD
					List<String> table_list = new ArrayList<>();
					table_list.add(manualFileBean.getStFileName());
					table_list.add(manualFileBean.getStManualFile());

					manualRupayFileService.compareManualFile(table_list, filterBean);
				}*/
					//3. CATEGORIZATION OF CIA GL RECORDS

					//4. UPDATE MATCHED REVERSAL ENTRIES I.E ACTION TAKEN ENTRIES

					//5. MOVE PREVIOUS DAY RECON RECORDS IN FILTERATION TABLE
					/*filterBean.setStFile_Name(manualFileBean.getStManualFile());
					manualRupayFileService.getReconRecords(filterBean);*/

					//	surcharge records

					/*filterBean.setStSubCategory("SURCHARGE");
					filterBean.setStMerger_Category(filterBean.getStCategory()+"_"+filterBean.getStSubCategory().substring(0, 3));
					manualRupayFileService.getReconRecords(filterBean);*/ 
					// 21NOV2017 WE NEED TO KNOCKOFF THESE RECORDS WITH FILTERED RECORDS
					//manualRupayFileService.KnockOffRecords(filterBean);


/*
					if(filterBean.getStCategory().equals("RUPAY"))
					{

						if(manualFileBean.getStSubCategory().equalsIgnoreCase("DOMESTIC"))
						{
							filterBean.setStSubCategory("DOMESTIC");
						}
						else
						{
							filterBean.getStSubCategory().equalsIgnoreCase("INTERNATIONAL");
						}
						filterBean.setStMerger_Category(filterBean.getStCategory()+"_"+filterBean.getStSubCategory().substring(0, 3));

					}
					else if(filterBean.getStCategory().equalsIgnoreCase("VISA"))
					{	*/
						
						/*if(manualFileBean.getStSubCategory().equalsIgnoreCase("ISSUER"))
						filterBean.setStSubCategory("ISSUER");*/
						//filterBean.setStMerger_Category(manualFileBean.getStMergerCategory());

				//	}





					//6. PERFORM KNOCKOFF ON THESE RECORDS
					/*filterBean.setStFile_Name(manualFileBean.getStManualFile());
					manualRupayFileService.KnockOffRecords(filterBean);*/

					/*filterBean.setStFile_Name(manualFileBean.getStFileSelected());
					manualRupayFileService.KnockOffRecords(filterBean);*/

					//7. PERFORM RECON PROCEDURE ON THESE RECORDS
					//List<Integer> rec_set_id;
					CompareBean compareBean = new CompareBean();
					compareBean.setStCategory(manualFileBean.getStCategory());
					compareBean.setStSubCategory(manualFileBean.getStSubCategory());
					//compareBean.setStMergeCategory(filterBean.getStMerger_Category());
					compareBean.setStMergeCategory(manualFileBean.getStMergerCategory());
					compareBean.setStFile_date(manualFileBean.getStFile_date());
					compareBean.setStEntryBy(filterBean.getStEntry_by());

					/*if(filterBean.getStSubCategory().equals("DOMESTIC"))
					{
						 rec_set_id = compareRupayService.getRec_domesticset_id(stCategory);
					}
					else*/
					{
						rec_set_id = compareRupayService.getRec_set_id(stCategory);
					}



					for(int j = 0; j<rec_set_id.size(); j++ )
					{
						System.out.println("****************************************************************** REC ID IS ************************ "+rec_set_id.get(j));
						if(j == 2)
						{
							compareBean.setStSubCategory("SURCHARGE");

							compareBean.setStMergeCategory(compareBean.getStCategory()+"_"+compareBean.getStSubCategory().substring(0, 3));
						}
						int inRec_Set_id = rec_set_id.get(j);
						List<List<String>> tables_data = compareRupayService.getTableName(inRec_Set_id,stCategory);
						List<String> compare_tables = tables_data.get(0);
						List<String> Tables_subcate = tables_data.get(1);

						compareBean.setStTable1_SubCategory(Tables_subcate.get(0));
						compareBean.setStTable2_SubCategory(Tables_subcate.get(1));

						/*compareBean.setStTable1_SubCategory("DOMESTIC");
						compareBean.setStTable2_SubCategory("DOMESTIC");
						if((compare_tables.get(1).equals("CBS")) && compareBean.getStSubCategory().equals("SURCHARGE"))
						{
							compareBean.setStTable2_SubCategory("SURCHARGE");
						}
						else if(compare_tables.get(0).equals("CBS") && compareBean.getStSubCategory().equals("SURCHARGE"))
						{
							compareBean.setStTable1_SubCategory("SURCHARGE");
						}*/

						/*if(compareBean.getStCategory().equals("RUPAY") &&(compareBean.getStSubCategory().equals("DOMESTIC") || compareBean.getStSubCategory().equals("SURCHARGE")))
						{
							compareBean.setStTable1_SubCategory("DOMESTIC");
							compareBean.setStTable2_SubCategory("DOMESTIC");
						}
						else if(compareBean.getStSubCategory().equals("INTERNATIONAL"))
						{
							compareBean.setStTable1_SubCategory("INTERNATIONAL");
							compareBean.setStTable2_SubCategory("INTERNATIONAL");						
						}
						else if(compareBean.getStCategory().equals("VISA")&&(compareBean.getStSubCategory().equals("ISSUER")||compareBean.getStSubCategory().equals("SURCHARGE")))
						{
							compareBean.setStTable1_SubCategory("ISSUER");
							compareBean.setStTable2_SubCategory("ISSUER");
						}
						else if(compareBean.getStCategory().equals("VISA") && compareBean.getStSubCategory().equals("ACQUIRER"))
						{
							compareBean.setStTable1_SubCategory("ACQUIRER");
							compareBean.setStTable2_SubCategory("ACQUIRER");
						}


						if(compare_tables.get(1).equals("CBS") && compareBean.getStSubCategory().equals("SURCHARGE"))
						{
							compareBean.setStTable2_SubCategory("SURCHARGE");
						}
						else if(compare_tables.get(0).equals("CBS") && compareBean.getStSubCategory().equals("SURCHARGE"))
						{
							compareBean.setStTable1_SubCategory("SURCHARGE");
						}
*/

						//FOR RUPAY FILE WE HAVE TO TAKE PREVIOUS DAY RECON RECORDS FROM SETTLEMENT TABLE AND ADD THEM DIRECTLY IN TEMP TABLE AS NO FILTERATION IS TO BE DONE
						int i = 0;
						/*for(int k = 0 ;k<compare_tables.size() ; k++)
						{
							i = 0;
							filterBean.setStFile_Name(compare_tables.get(k));
							String stStatus = filterService.getStatus(filterBean,"FILTERATION");
							if(stStatus.equals("N"))
							{
								filterBean.setInRec_Set_Id(inRec_Set_id);
								manualRupayFileService.moveUnFilteredData(filterBean);
								i = 1;

							}
							else
							{
								List<String> final_tables = new ArrayList<>();
								final_tables.add(compare_tables.get(k));*/
						i = manualRupayFileService.moveData(compare_tables,compareBean,inRec_Set_id);
						
						//remove duplicate entries
						//compareRupayService.removeDuplicates(compare_tables, compareBean, inRec_Set_id);


						/*	}
						}
						 */


						//int  i = compareRupayService.moveData(compare_tables,compareBean,inRec_Set_id);
						if(i==1)
						{
							//ALTER THE MATCHED TABLE AND SETTLEMENT TABLE
							compareRupayService.alterMatchedandSettlementTables(compareBean,inRec_Set_id);
							//compareRupayService.updateMatchedRecords(compare_tables, compareBean,inRec_Set_id);
							if(inRec_Set_id == 1)
							{
								//remove duplicate entries
								compareRupayService.removeDuplicates(compare_tables, compareBean, inRec_Set_id);
							}
							manualRupayFileService.moveToRecon(compare_tables,compareBean,inRec_Set_id);

							//	compareRupayService.TTUMRecords(tables,compareBean);
							
							//RESPONSE CODE
							if(j == 0)
							{
								compareRupayService.TTUMRecords(compare_tables, compareBean, rec_set_id.get(j));
							}

						}
					}
					manualRupayFileService.CleanTables(compareBean);
					
				}
				setupBean.setInFileId(Integer.parseInt(manualFileBean.getStFileSelected()));
				setupBean.setCategory(manualFileBean.getStCategory());
				setupBean.setStSubCategory(manualFileBean.getStSubCategory());
				icompareConfigService.updateFlag("MANUALCOMPARE_FLAG", setupBean);
				setupBean.setInFileId(Integer.parseInt(manualFileBean.getManFile()));
				icompareConfigService.updateFlag("MANUALCOMPARE_FLAG", setupBean);

				return "Manual file Compared Successfully.";
			}else {

				return "Files are Not Uploaded for respective date";

			}	
		}
		else
		{
			return "Files are already processed";
		}
		
		
	}
	catch(Exception e)
	{
		e.printStackTrace();
		System.out.println("Exception in ManualFileProcess is "+e);
		return "Error Occured";
	}
	//return "ManualFile";
	//return "redirect:ManualFileProcess.do";
}

	

}
