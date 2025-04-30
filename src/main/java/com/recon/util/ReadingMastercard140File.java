package com.recon.util;


import com.recon.model.CompareSetupBean;
import com.recon.model.MastercardUploadBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import org.springframework.web.multipart.MultipartFile;

public class ReadingMastercard140File {
  public HashMap<String, Object> read464File(MultipartFile file, Connection conn, CompareSetupBean beanObj) {
    HashMap<String, Object> output = new HashMap<>();
    int lineNumber = 0, readedCount = 0;
    String line = null, service_level = null, file_type = null, file_id = null, service_id = null;
    int counter = 0, grandCounter = 0;
    boolean blockstarts = false, tableStarts = false, chargeback_table = false;
    HashMap<Integer, String> proc_code = new HashMap<>();
    HashMap<Integer, String> count = new HashMap<>();
    HashMap<Integer, String> recon_amt = new HashMap<>();
    HashMap<Integer, String> tran_fee = new HashMap<>();
    HashMap<Integer, String> amount_part_tran_type = new HashMap<>();
    HashMap<Integer, String> fee_part_tran_type = new HashMap<>();
    HashMap<Integer, String> function = new HashMap<>();
    String INSERT_DATA = "INSERT INTO MASTERCARD_140_RAWDATA(SETTLEMENT_DATE, FILE_ID, FILE_TYPE, FUNCTION, PROC_CODE, COUNTS, RECON_AMT, TRANS_FEE, SERVICE_ID, AMT_PART_TRAN_TYPE, FEE_PART_TRAN_TYPE, CREATEDBY, FILE_NAME) VALUES(TO_DATE(?,'DD/MON/YYYY'),?,?,?,?,?,?,?,?,?,?,?,?)";
    try {
      PreparedStatement pst = conn.prepareStatement(INSERT_DATA);
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      while ((line = br.readLine()) != null) {
        lineNumber++;
        System.out.println(line.trim());
        if (line.trim().contains("1IP727020-AA") || line.trim().contains("1IP727010-AA")) {
          blockstarts = true;
          continue;
        } 
        if (line.trim().contains("BUSINESS SERVICE ID SUBTOTAL"))
          blockstarts = true; 
        if (blockstarts) {
          readedCount++;
          if (line.contains(":")) {
            String[] values = line.split(":");
            if (values[0].trim().equalsIgnoreCase("BUSINESS SERVICE ID") || values[0].trim().equalsIgnoreCase("FILE ID") || values[0].trim().equalsIgnoreCase("BUSINESS SERVICE LEVEL")) {
              System.out.println(String.valueOf(values[0]) + " " + values[1]);
              if (values[0].trim().equalsIgnoreCase("BUSINESS SERVICE LEVEL")) {
                String[] subVal = values[1].trim().split("\\s");
                System.out.println(subVal[0].trim());
                service_level = subVal[0].trim();
              } else if (values[0].trim().equalsIgnoreCase("BUSINESS SERVICE ID")) {
                service_id = values[1].trim();
                file_type = values[1].substring(0, 4).trim();
                System.out.println("File Type is " + file_type);
                if (file_type.equalsIgnoreCase("356")) {
                  file_type = "AP";
                } else {
                  file_type = "US";
                } 
                System.out.println("Now File Type is " + file_type);
              } else {
                file_id = values[1].trim().replace("/", "");
              } 
            } 
          } else if (line.trim().startsWith("FIRST PRES.") || line.trim().contains("ORIG ") || line.trim().contains("FIRST C/B -F")) {
            tableStarts = true;
          } 
          if (tableStarts)
            if (line.startsWith("")) {
              if ((line.contains("ORIG") && (!line.contains("/") || line.contains("C/B"))) || (
                line.contains("RVSL") && !line.contains("/") && line.contains("ATM CASH"))) {
                counter++;
                String[] values = null;
                if (line.contains("RVSL") && !line.contains("/") && line.contains("ATM CASH")) {
                  values = line.trim().split("RVSL");
                } else {
                  values = line.trim().split("ORIG");
                } 
                if (values[0].contains("FIRST PRES.")) {
                  function.put(Integer.valueOf(counter), "PRES");
                  proc_code.put(Integer.valueOf(counter), values[0].trim().substring("FIRST PRES.".length()).trim());
                  chargeback_table = false;
                } else if (values[0].contains("SEC. PRES.-F")) {
                  function.put(Integer.valueOf(counter), "SEC PRES");
                  proc_code.put(Integer.valueOf(counter), values[0].trim().substring("SEC. PRES.-F".length()).trim());
                  chargeback_table = false;
                } else if (values[0].contains("C/B -F")) {
                  function.put(Integer.valueOf(counter), "CHARGEBACK");
                  proc_code.put(Integer.valueOf(counter), values[0].trim().substring("FIRST C/B -F".length()).trim());
                  chargeback_table = true;
                } else if (chargeback_table) {
                  function.put(Integer.valueOf(counter), "CHARGEBACK");
                  proc_code.put(Integer.valueOf(counter), values[0].trim());
                } else {
                  System.out.println(line.contains("RVSL"));
                  if (line.contains("RVSL") && !line.contains("/") && line.contains("ATM CASH")) {
                    function.put(Integer.valueOf(counter), "RVSL");
                  } else {
                    function.put(Integer.valueOf(counter), "PRES");
                  } 
                  proc_code.put(Integer.valueOf(counter), values[0].trim());
                } 
                count.put(Integer.valueOf(counter), values[1].substring(3, 14).trim());
                recon_amt.put(Integer.valueOf(counter), values[1].substring(14, 37).trim());
                amount_part_tran_type.put(Integer.valueOf(counter), values[1].substring(38, 39).trim());
                tran_fee.put(Integer.valueOf(counter), values[1].substring(50, 70).trim());
                fee_part_tran_type.put(Integer.valueOf(counter), values[1].substring(70, 71).trim());
                continue;
              } 
              if (service_level.equalsIgnoreCase(line.trim())) {
                for (int i = 1; i <= proc_code.size(); i++) {
                  pst.setString(1, beanObj.getFileDate());
                  pst.setString(2, file_id);
                  pst.setString(3, file_type);
                  pst.setString(4, function.get(Integer.valueOf(i)));
                  pst.setString(5, proc_code.get(Integer.valueOf(i)));
                  pst.setString(6, count.get(Integer.valueOf(i)));
                  pst.setString(7, recon_amt.get(Integer.valueOf(i)));
                  pst.setString(8, tran_fee.get(Integer.valueOf(i)));
                  pst.setString(9, service_id);
                  pst.setString(10, amount_part_tran_type.get(Integer.valueOf(i)));
                  pst.setString(11, fee_part_tran_type.get(Integer.valueOf(i)));
                  pst.setString(12, beanObj.getCreatedBy());
                  pst.setString(13, file.getOriginalFilename());
                  pst.execute();
                } 
                counter = 0;
                System.out.println("proc_code " + proc_code);
                System.out.println("counts" + count);
                System.out.println("recon_amt" + recon_amt);
                System.out.println("Amount part_tran_type" + amount_part_tran_type);
                System.out.println("tran_fee" + tran_fee);
                System.out.println("Fee part tran " + fee_part_tran_type);
                function = new HashMap<>();
                proc_code = new HashMap<>();
                count = new HashMap<>();
                recon_amt = new HashMap<>();
                amount_part_tran_type = new HashMap<>();
                tran_fee = new HashMap<>();
                fee_part_tran_type = new HashMap<>();
                tableStarts = false;
                continue;
              } 
              if (line.trim().contains("GRAND TOTAL")) {
                grandCounter++;
                System.out.println("GRAND TOTAL " + line.trim());
                System.out.println("amount is " + line.trim().substring(14, 40).trim());
                System.out.println("creit / debit is " + line.trim().substring(40, 41).trim());
                int sr_no = 1;
                pst.setString(sr_no++, beanObj.getFileDate());
                pst.setString(sr_no++, file_id);
                pst.setString(sr_no++, file_type);
                pst.setString(sr_no++, "");
                pst.setString(sr_no++, "GRAND TOTAL");
                pst.setString(sr_no++, null);
                pst.setString(sr_no++, line.trim().substring(14, 40).trim());
                pst.setString(sr_no++, line.trim().substring(44, 72).trim());
                pst.setString(sr_no++, service_id);
                pst.setString(sr_no++, line.trim().substring(40, 41).trim());
                pst.setString(sr_no++, line.trim().substring(72, 73).trim());
                pst.setString(sr_no++, beanObj.getCreatedBy());
                pst.setString(sr_no++, file.getOriginalFilename());
                pst.execute();
              } 
            }  
        } 
      } 
      output.put("result", Boolean.valueOf(true));
      output.put("msg", "File reading Completed.");
    } catch (Exception e) {
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "File reading Failed");
      System.out.println("Exception in read140File " + e);
      e.printStackTrace();
    } 
    return output;
  }
  
  public HashMap<String, Object> read140File(MultipartFile file, Connection conn, MastercardUploadBean beanObj) {
    HashMap<String, Object> output = new HashMap<>();
    int lineNumber = 0, readedCount = 0;
    String line = null,CURRENCY_CODE="", service_level = null, file_type = null, file_id = null, service_id = null, cycle = "", sett_date = "", GlobleSECRESS = "";
    int counter = 0, grandCounter = 0;
    boolean blockstarts = false, tableStarts = false, chargeback_table = false;
    HashMap<Integer, String> proc_code = new HashMap<>();
    HashMap<Integer, String> count = new HashMap<>();
    HashMap<Integer, String> recon_amt = new HashMap<>();
    HashMap<Integer, String> tran_fee = new HashMap<>();
    HashMap<Integer, String> amount_part_tran_type = new HashMap<>();
    HashMap<Integer, String> fee_part_tran_type = new HashMap<>();
    HashMap<Integer, String> function = new HashMap<>();
    String INSERT_DATA = "INSERT INTO mastercard_settlement_rawdata (SETTLEMENT_DATE, FILE_ID, FILE_TYPE, FUNCTIONS, PROC_CODE, COUNTS, RECON_AMT, TRANS_FEE, SERVICE_ID, AMT_PART_TRAN_TYPE, FEE_PART_TRAN_TYPE, CREATEDBY, FILE_NAME,cycle,FILEDATE,BUSSINESS_SERVICE_ID,CURRENCY_CODE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    try {
    	boolean firstpres= false, secondpres=false;
      PreparedStatement pst = conn.prepareStatement(INSERT_DATA);
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      while ((line = br.readLine()) != null) {
    	   if (line.trim().contains("CLEARING CYCLE")) {
               cycle = line.trim().substring(17, 18);
    	   }
    	   if (line.contains("CURRENCY CODE :")) {
    		   CURRENCY_CODE = line.substring(17, line.length());
    		   System.out.println("CURRENCY_CODE "+ CURRENCY_CODE);
    	   } 
        lineNumber++;
      //  System.out.println("cycle " + cycle );
        if (line.trim().contains("1IP727020-AA") || line.trim().contains("1IP727010-AA")) {
          blockstarts = true;
          continue;
        } 
        if (blockstarts) {
          readedCount++;
       
          
          if (line.contains(":")) {
            String[] values = line.split(":");
            if (values[0].trim().equalsIgnoreCase("BUSINESS SERVICE ID") || values[0].trim().equalsIgnoreCase("FILE ID") || values[0].trim().equalsIgnoreCase("BUSINESS SERVICE LEVEL"))
              if (values[0].trim().equalsIgnoreCase("BUSINESS SERVICE LEVEL")) {
                String[] subVal = values[1].trim().split("\\s");
                service_level = subVal[0].trim();
        
              } else if (values[0].trim().equalsIgnoreCase("BUSINESS SERVICE ID")) {
                service_id = values[1].trim();
                file_type = values[1].substring(0, 4).trim();
                if (file_type.equalsIgnoreCase("356")) {
                  file_type = "INR";
                } else {
                  file_type = "US";
                } 
              } else {
                file_id = values[1].trim().replace("/", "");
                sett_date = file_id.substring(3, 9);
              }  
          } else if (line.trim().startsWith("FIRST PRES.") || line.trim().startsWith("SEC. PRES.-F") || line.trim().contains("ORIG") || line.trim().contains("CREDIT     RVSL")|| line.trim().contains("PURCHASE   ORIG") || line.trim().contains("CREDIT     ORIG") || line.trim().contains("FIRST C/B -F") || line.trim().contains("FEE COLL-CSG")) {
            System.out.println("codefff " + line.trim());
            if(line.trim().startsWith("FIRST PRES.") ) {
            	
            	firstpres= true;
            	
            }else {
            	secondpres= true;
            }
            
            if (line.trim().startsWith("FIRST PRES.") || line.trim().startsWith("SEC. PRES.-F") || line.trim().startsWith("CREDIT     RVSL") ) {
              if (line.substring(70, 73).equalsIgnoreCase("356")) {
                file_type = "INR";
              } else {
                file_type = "US";
              } 
           //   System.out.println("cycle1 " + cycle );
              pst.setString(1, sett_date);
              pst.setString(2, file_id);
              pst.setString(3, line.substring(74, 77).trim());
              if(firstpres) {
                  pst.setString(4,"FIRST PRES.");  
            	  
              }else {
                  pst.setString(4, line.substring(0, 13).trim());
              }
 
              pst.setString(5, line.substring(0, 29).trim());
              pst.setString(6, line.substring(37, 43).trim());
              pst.setString(7, line.substring(53, 67).trim());
              pst.setString(8, line.substring(85, 99).trim());
              pst.setString(9, service_id);
              pst.setString(10, line.substring(67, 68).trim());
              pst.setString(11, line.substring(99, 100).trim());
              pst.setString(12, beanObj.getCreatedBy());
              pst.setString(13, file.getOriginalFilename());
              pst.setString(14, cycle);
              pst.setString(15, beanObj.getFileDate());
              pst.setString(16, service_level);
              pst.setString(17, CURRENCY_CODE);
              CURRENCY_CODE="";

              pst.execute();
            } 
            tableStarts = true;
            
          
          } 
          if (tableStarts) {
           // System.out.println("ORIG " + line.trim());
            if ((line.contains("ORIG") && (!line.contains("/") || line.contains("C/B"))) || (
              line.contains("RVSL") && !line.contains("/") && line.contains("ATM CASH"))) {
              counter++;
              String[] values = null;
              if (line.contains("RVSL") && !line.contains("/") && line.contains("ATM CASH")) {
                values = line.trim().split("RVSL");
              } else {
                values = line.trim().split("ORIG");
              } 
              if (values[0].contains("FIRST PRES.")) {
                System.out.println("sddf " + values[0].trim().substring("FIRST PRES.".length()).trim());
                function.put(Integer.valueOf(counter), "PRES");
                proc_code.put(Integer.valueOf(counter), values[0].trim());
                chargeback_table = false;
              } else if (values[0].contains("SEC. PRES.-F")) {
                GlobleSECRESS = "SEC PRES";
                function.put(Integer.valueOf(counter), "SEC PRES");
                proc_code.put(Integer.valueOf(counter), values[0].trim());
                chargeback_table = false;
              } else if (values[0].contains("C/B -F")) {
                  function.put(Integer.valueOf(counter), "CHARGEBACK");
                proc_code.put(Integer.valueOf(counter), values[0].trim());
                chargeback_table = true;
              } else if (chargeback_table) {
                function.put(Integer.valueOf(counter), "CHARGEBACK");
                proc_code.put(Integer.valueOf(counter), values[0].trim());
              } else {
               // System.out.println(line.contains("RVSL"));
                if (line.contains("RVSL") && !line.contains("/") && line.contains("ATM CASH")) {
                  function.put(Integer.valueOf(counter), "RVSL");
                } else {
                 // System.out.println("GlobleSECRESS " + GlobleSECRESS);
                  if (GlobleSECRESS.equalsIgnoreCase("SEC PRES")) {
                    function.put(Integer.valueOf(counter), "SEC PRES");
                  } else {
                    function.put(Integer.valueOf(counter), "PRES");
                  } 
                } 
                proc_code.put(Integer.valueOf(counter), values[0].trim());
              } 
              count.put(Integer.valueOf(counter), values[1].substring(3, 14).trim());
              recon_amt.put(Integer.valueOf(counter), values[1].substring(14, 37).trim());
              amount_part_tran_type.put(Integer.valueOf(counter), values[1].substring(38, 39).trim());
              tran_fee.put(Integer.valueOf(counter), values[1].substring(50, 70).trim());
              fee_part_tran_type.put(Integer.valueOf(counter), values[1].substring(70, 71).trim());
              continue;
            } 
            if (service_level.equalsIgnoreCase(line.trim())) {
            //  System.out.println("Last Line" + line);
              for (int i = 1; i <= proc_code.size(); i++) {
         
                pst.setString(1, sett_date);
                pst.setString(2, file_id);
                pst.setString(3, file_type);
                pst.setString(4, function.get(Integer.valueOf(i)));
                pst.setString(5, proc_code.get(Integer.valueOf(i)));
                pst.setString(6, count.get(Integer.valueOf(i)));
                pst.setString(7, recon_amt.get(Integer.valueOf(i)));
                pst.setString(8, tran_fee.get(Integer.valueOf(i)));
                pst.setString(9, service_id);
                pst.setString(10, amount_part_tran_type.get(Integer.valueOf(i)));
                pst.setString(11, fee_part_tran_type.get(Integer.valueOf(i)));
                pst.setString(12, beanObj.getCreatedBy());
                pst.setString(13, file.getOriginalFilename());
                pst.setString(14, cycle);
                pst.setString(15, beanObj.getFileDate());
                pst.setString(16, service_level);
                pst.setString(17, CURRENCY_CODE);
                CURRENCY_CODE="";
                pst.execute();
              } 
              counter = 0;
              System.out.println("proc_code " + proc_code);
              System.out.println("counts" + count);
              System.out.println("recon_amt" + recon_amt);
              System.out.println("Amount part_tran_type" + amount_part_tran_type);
              System.out.println("tran_fee" + tran_fee);
              System.out.println("Fee part tran " + fee_part_tran_type);
              function = new HashMap<>();
              proc_code = new HashMap<>();
              count = new HashMap<>();
              recon_amt = new HashMap<>();
              amount_part_tran_type = new HashMap<>();
              tran_fee = new HashMap<>();
              fee_part_tran_type = new HashMap<>();
              tableStarts = false;
              continue;
            } 
            if (line.trim().contains("GRAND TOTAL")) {
              grandCounter++;
              System.out.println("GRAND TOTAL " + line.trim());
              System.out.println("amount is " + line.trim().substring(14, 40).trim());
              System.out.println("creit / debit is " + line.trim().substring(40, 41).trim());
              int sr_no = 1;
              pst.setString(sr_no++, sett_date);
              pst.setString(sr_no++, file_id);
              pst.setString(sr_no++, file_type);
              pst.setString(sr_no++, "");
              pst.setString(sr_no++, "GRAND TOTAL");
              pst.setString(sr_no++, null);
              pst.setString(sr_no++, line.trim().substring(14, 40).trim());
              pst.setString(sr_no++, line.trim().substring(44, 72).trim());
              pst.setString(sr_no++, service_id);
              pst.setString(sr_no++, line.trim().substring(40, 41).trim());
              pst.setString(sr_no++, line.trim().substring(72, 73).trim());
              pst.setString(sr_no++, beanObj.getCreatedBy());
              pst.setString(sr_no++, file.getOriginalFilename());
              pst.setString(sr_no++, cycle);
              pst.setString(sr_no++, beanObj.getFileDate());
              pst.setString(sr_no++, service_level);
              pst.setString(sr_no++, CURRENCY_CODE);
              CURRENCY_CODE="";
              pst.execute();
            } 
          } 
        } 
      } 
      output.put("result", Boolean.valueOf(true));
      output.put("msg", "FILE UPLOADED SUCCESS!");
    } catch (Exception e) {
      output.put("result", Boolean.valueOf(false));
      output.put("msg", "FILE NOT UPOADED!");
      System.out.println("Exception in read140File " + e);
      e.printStackTrace();
    } 
    return output;
  }
}
