package com.recon.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.model.CompareSetupBean;
import com.recon.model.GenerateTTUMBean;
import com.recon.model.UploadTTUMBean;
import com.recon.service.GenerateRupayTTUMService;
import com.recon.service.GenerateTTUMService;
import com.recon.service.ISourceService;
import com.recon.service.TTUMDataUploadService;
import com.recon.util.ReadTextFile;
import com.recon.util.Read_successfull_tran;

@Controller
public class TTUMDataUploadController {
	
	@Autowired
	TTUMDataUploadService ttumDataService;
	
	@Autowired
	GenerateRupayTTUMService generaterupayTTUMService;
	

	@Autowired
	ISourceService iSourceService;
	
	@Autowired
	GenerateTTUMService generateTTUMservice;
	


	
	@RequestMapping(value = "UploadTTUM", method = RequestMethod.GET)
	public ModelAndView getUploadTTUMPage(Model model,@RequestParam("category")String category, UploadTTUMBean UploadTTUMBean,ModelAndView modelAndView) throws Exception
	{
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>(); 
		
		System.out.println("RECON PROCESS GET");
		
		
		 List<String> subcat = new ArrayList<>();
		 
		 System.out.println("in GetHeaderList"+category);
		 
	     subcat = iSourceService.getSubcategories(category);
		
	 	model.addAttribute("UploadTTUMBean",UploadTTUMBean);
	     modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory",subcat );
		modelAndView.setViewName("UploadTTUM");
		return modelAndView;


		
		
	}

@RequestMapping(value = "UploadTTUM", method = RequestMethod.POST)
public String handelExcelUpload(UploadTTUMBean UploadTTUMBean , RedirectAttributes redirect,
		@RequestParam("FileUpload") MultipartFile file, Model model,HttpSession httpSession) {

	boolean result=false;
	try {
		
		if(UploadTTUMBean.getStCategory().equalsIgnoreCase("CARDTOCARD"))
		{
			
			if(UploadTTUMBean.getStSelectedFile1().equalsIgnoreCase("SUCCESS"))
			{
				Read_successfull_tran successtran=new Read_successfull_tran();
				result=successtran.read_success(UploadTTUMBean, file);
				List<List<GenerateTTUMBean>> generatettumObj=null;
				String table_name = UploadTTUMBean.getStSelectedFile();
				if(result)
				{
					GenerateTTUMBean ttumBean = new GenerateTTUMBean();
					ttumBean.setStCategory(UploadTTUMBean.getStCategory());
					ttumBean.setStSubCategory(UploadTTUMBean.getStSubCategory());
					ttumBean.setStMerger_Category(UploadTTUMBean.getStMergerCategory());
					ttumBean.setStFile_Name(UploadTTUMBean.getStSelectedFile1());
					ttumBean.setStDate(UploadTTUMBean.getStDate());
					ttumBean.setInRec_Set_Id(UploadTTUMBean.getInRec_Set_Id());
					List<List<GenerateTTUMBean>> Data = new ArrayList<List<GenerateTTUMBean>>();

			generatettumObj = generateTTUMservice.generateTTUMForCARDTOCARD(ttumBean);
			model.addAttribute("generate_ttum",generatettumObj);

			return "GenerateCardtocard_ttum";
				}
				
			}else if(UploadTTUMBean.getStSelectedFile1().equalsIgnoreCase("FAIL"))
			{
				List<List<GenerateTTUMBean>> generatettumObj=null;
			
				ReadTextFile failtran=new ReadTextFile();
				result=failtran.read_fail(UploadTTUMBean, file);
				if(result)
				{
					GenerateTTUMBean ttumBean = new GenerateTTUMBean();
					ttumBean.setStCategory(UploadTTUMBean.getStCategory());
					ttumBean.setStSubCategory(UploadTTUMBean.getStSubCategory());
					ttumBean.setStMerger_Category(UploadTTUMBean.getStMergerCategory());
					ttumBean.setStFile_Name(UploadTTUMBean.getStSelectedFile1());
					ttumBean.setStDate(UploadTTUMBean.getStDate());
					ttumBean.setInRec_Set_Id(UploadTTUMBean.getInRec_Set_Id());
					List<List<GenerateTTUMBean>> Data = new ArrayList<List<GenerateTTUMBean>>();

			generatettumObj = generateTTUMservice.generateTTUMForCARDTOCARD(ttumBean);
			model.addAttribute("generate_ttum",generatettumObj);

			return "GenerateCardtocard_ttum";
			}
		}
			}
		else{
		System.out.println("Upload TTUTUM entry");
		//npsfileUploadBean.setEntry_by(((LoginBean) httpSession.getAttribute("loginBean")).getUser_id());
		if(!UploadTTUMBean.getStSubCategory().equals("-"))
		{
			UploadTTUMBean.setStMergerCategory(UploadTTUMBean.getStCategory()+"_"+UploadTTUMBean.getStSubCategory().substring(0, 3));
		}
		else
			UploadTTUMBean.setStMergerCategory(UploadTTUMBean.getStCategory());

		if (!file.isEmpty()) {
			int nameLen = file.getOriginalFilename().length();
			System.out.println("File path is "+file.getOriginalFilename());
			System.out.println("File apth is "+file);
			if (file.getOriginalFilename().substring((nameLen - 3), nameLen).equalsIgnoreCase("XLS")) {
				String msg =ttumDataService.readFile(UploadTTUMBean,file);
					
					if(msg == "Success") {
					
					GenerateTTUMBean ttumBean = new GenerateTTUMBean();
					ttumBean.setStCategory(UploadTTUMBean.getStCategory());
					ttumBean.setStSubCategory(UploadTTUMBean.getStSubCategory());
					ttumBean.setStMerger_Category(UploadTTUMBean.getStMergerCategory());
					ttumBean.setStFile_Name(UploadTTUMBean.getStSelectedFile());
					ttumBean.setStDate(UploadTTUMBean.getStDate());
					ttumBean.setInRec_Set_Id(UploadTTUMBean.getInRec_Set_Id());
					
					List<List<GenerateTTUMBean>> Data = new ArrayList<List<GenerateTTUMBean>>();
					if(ttumBean.getStFile_Name().equals("dispute")){
						if(UploadTTUMBean.getStCategory().equals("VISA")){
							Data = generaterupayTTUMService.generateVisaDisputeTTUM(ttumBean);
						}else{
							Data = generaterupayTTUMService.generateDisputeTTUM(ttumBean);	
						}
						model.addAttribute("generate_ttum", Data);
						return "RupayDisputeTTUMExcel";
					}else{
						Data = generaterupayTTUMService.generateSwitchTTUM(ttumBean,ttumBean.getInRec_Set_Id());//take it from jsp
						model.addAttribute("generate_ttum", Data);
						return "generateRupayTTUMExcel";
					}
					
					
				}
				else
				{
					throw new Exception("Error Occured");
					
				}
				//redirect.addFlashAttribute("success_msg","Records are inserted successfully");
			} else {
				redirect.addFlashAttribute("error_msg",
						"Invaild file please upload .xls file only");
			}
		}

		
	} } catch (Exception e) {
		redirect.addFlashAttribute("error_msg",
				e.getMessage());
	}
	model.addAttribute("UploadTTUMBean",new UploadTTUMBean());
	return "UploadTTUM";

}

}
