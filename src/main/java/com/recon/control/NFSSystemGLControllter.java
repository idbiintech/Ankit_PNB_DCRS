package com.recon.control;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.recon.service.NFSSystemGLService;

@Controller
public class NFSSystemGLControllter {
	@Autowired
	NFSSystemGLService nfsPayableGLService;

	private static final String JSP_PAGE_PAYABLE = "nfsPayableGLpage";
	private static final String JSP_PAGE_RECEIVABLE = "nfsReceivableGLpage";
	private static final String INVALID_INPUT_DOWNLOAD = "Please enter Date";
	private static final String INVALID_OPENING_BALANCE = "Please enter opening balace";
	private static final String INVALID_FUND_BALANCE = "Please enter fund Amount";
	private static final String INVALID_EOD_BALANCE = "Please enter EOD Amount";
	private static final String SUCCESS = "SUCCESS";

	// ============================FOR NFS PAYABLE GL -BY INT10850====================
	@RequestMapping(value = "nfsPayableGLpage", method = RequestMethod.GET)
	public ModelAndView nfsPayableGL(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
		modelAndView.setViewName("nfsPayableGLpage");
		return modelAndView;

	}

	@RequestMapping(value = "processNFSReportPayable", method = RequestMethod.POST)
	public String processNFSReportPayable(ModelAndView modelAndView, HttpSession session, HttpServletResponse response,
			HttpServletRequest req, Model model, @RequestParam("fdate") String fdate,
			@RequestParam("openingBal") String openingBal, @RequestParam("fundMovefr43Acc") String fundMovefr43Acc,
			@RequestParam("onlineFundMov") String onlineFundMov, @RequestParam("finacleEODbal") String finacleEODbal) {
		try {
			if (fdate == null || fdate.isEmpty()) {
				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return JSP_PAGE_PAYABLE;
			} else if (openingBal == null || openingBal.isEmpty()) {
				model.addAttribute("message", INVALID_OPENING_BALANCE);

				return JSP_PAGE_PAYABLE;
			} else if (finacleEODbal == null || finacleEODbal.isEmpty()) {
				model.addAttribute("message", INVALID_EOD_BALANCE);
				return JSP_PAGE_PAYABLE;
			}

			String month = fdate.substring(0, 2);
			String day = fdate.substring(3, 5);
			String year = fdate.substring(6);
			fdate = day + "-" + month + "-" + year;

			System.out.println("Date                : " + fdate);
			System.out.println("OPENING BALANCE     : " + openingBal);
			System.out.println("FINACLE EOD BALANCE : " + fundMovefr43Acc);
			System.out.println("OPENING BALANCE     : " + onlineFundMov);
			System.out.println("FINACLE EOD BALANCE : " + finacleEODbal);

			nfsPayableGLService.processNfsReportPayable(fdate, openingBal, fundMovefr43Acc, onlineFundMov, finacleEODbal);
			downloadNfsReportPayable(session, response, req, model, fdate, openingBal, fundMovefr43Acc, onlineFundMov,
					finacleEODbal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("message", SUCCESS);
		modelAndView.setViewName("nfsSystemGLpage");
		return "nfsSystemGLpage.jsp";
	}

	@RequestMapping(value = "downloadNFSReportPayable", method = RequestMethod.POST)
	// @ResponseBody
	public String downloadNfsReportPayable(HttpSession session, HttpServletResponse response, HttpServletRequest req,
			Model model, @RequestParam("fdate") String fdate, @RequestParam("openingBal") String openingBal,
			@RequestParam("fundMovefr43Acc") String fundMovefr43Acc,
			@RequestParam("onlineFundMov") String onlineFundMov, @RequestParam("finacleEODbal") String finacleEODbal) {

		ServletOutputStream sou = null;
		String fileName = null;
		try {
			if (fdate == null || fdate.isEmpty()) {
				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return JSP_PAGE_PAYABLE;
			}
			if (fdate.contains("/")) {
				String month = fdate.substring(0, 2);
				String day = fdate.substring(3, 5);
				String year = fdate.substring(6);
				fdate = day + "-" + month + "-" + year;
			}
			System.out.println("Date : " + fdate);

			fileName = nfsPayableGLService.downloadNfsReportPayable(fdate);
			fileName = fileName.replace('/', '-');
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
		model.addAttribute("message", SUCCESS);
		return JSP_PAGE_PAYABLE;
	}
	// ============================FOR NFS RECEIVABLE GL -BY INT10850==================
		@RequestMapping(value = "nfsReceivableGLpage", method = RequestMethod.GET)
		public ModelAndView nfsReceiableGL(ModelAndView modelAndView, HttpServletRequest request) throws Exception {
			modelAndView.setViewName("nfsReceivableGLpage");
			return modelAndView;

		}
	//=================================NFS FOR RECEIVABLE PROCESS==============
	@RequestMapping(value = "processNFSReportReceivable", method = RequestMethod.POST)
	public String processNFSReportReceivable(ModelAndView modelAndView, HttpSession session, HttpServletResponse response,
			HttpServletRequest req, Model model, @RequestParam("fdate") String fdate,
			@RequestParam("openingBal") String openingBal,@RequestParam("onlineFundMov") String onlineFundMov, @RequestParam("finacleEODbal") String finacleEODbal) {
		try {
			if (fdate == null || fdate.isEmpty()) {
				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return JSP_PAGE_RECEIVABLE;
				
			} else if (openingBal == null || openingBal.isEmpty()) {
				model.addAttribute("message", INVALID_OPENING_BALANCE);
				return JSP_PAGE_RECEIVABLE;
				
			} else if (onlineFundMov == null || onlineFundMov.isEmpty()) {
				model.addAttribute("message", INVALID_FUND_BALANCE);
				return JSP_PAGE_RECEIVABLE;
				
			} else if (finacleEODbal == null || finacleEODbal.isEmpty()) {
				model.addAttribute("message", INVALID_EOD_BALANCE);
				return JSP_PAGE_RECEIVABLE;
			}
			String month = fdate.substring(0, 2);
			String day = fdate.substring(3, 5);
			String year = fdate.substring(6);
			fdate = day + "-" + month + "-" + year;

			System.out.println("Date                : " + fdate);
			System.out.println("OPENING BALANCE     : " + openingBal);
			System.out.println("OPENING BALANCE     : " + onlineFundMov);
			System.out.println("FINACLE EOD BALANCE : " + finacleEODbal);

			nfsPayableGLService.processNfsReportReceivable(fdate, openingBal, onlineFundMov, finacleEODbal);
			downloadNfsReportReceivable(session, response, req, model, fdate, openingBal, onlineFundMov,finacleEODbal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("message", SUCCESS);
		modelAndView.setViewName("nfsSystemGLpage");
		return JSP_PAGE_RECEIVABLE;
	}
	//===============METHOD FOR NFS GL DOWNLOAD================================
	@RequestMapping(value = "downloadNFSReportReceivable", method = RequestMethod.POST)
	// @ResponseBody
	public String downloadNfsReportReceivable(HttpSession session, HttpServletResponse response, HttpServletRequest req,
			Model model, @RequestParam("fdate") String fdate, @RequestParam("openingBal") String openingBal,@RequestParam("onlineFundMov") String onlineFundMov, 
			@RequestParam("finacleEODbal") String finacleEODbal) {

		ServletOutputStream sou = null;
		String fileName = null;
		try {
			if (fdate == null || fdate.isEmpty()) {
				model.addAttribute("message", INVALID_INPUT_DOWNLOAD);
				return JSP_PAGE_RECEIVABLE;
			}
			if (fdate.contains("/")) {
				String month = fdate.substring(0, 2);
				String day = fdate.substring(3, 5);
				String year = fdate.substring(6);
				fdate = day + "-" + month + "-" + year;
			}
			System.out.println("Date : " + fdate);

			fileName = nfsPayableGLService.downloadNfsReportReceivable(fdate);
			fileName = fileName.replace('/', '-');
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
		model.addAttribute("message", SUCCESS);
		return JSP_PAGE_RECEIVABLE;
	}
}
