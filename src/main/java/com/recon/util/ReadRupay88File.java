package com.recon.util;


import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;
import com.recon.util.RupayHeaderUtil;
import com.recon.util.RupayUtilBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

public class ReadRupay88File {
  private static final Logger logger = Logger.getLogger(com.recon.util.ReadRupay88File.class);
  
  public boolean readData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    try {
      logger.info("***** ReadRupay.readData Start ****");
      boolean uploaded = false;
      logger.info(setupBean.getStSubCategory());
      if (setupBean.getStSubCategoryid().equalsIgnoreCase("DOMESTIC")) {
        logger.info("Entered CBS File is DOMESTIC");
        uploaded = uploadDomesticData(setupBean, con, file, sourceBean);
      } else if (setupBean.getStSubCategoryid().equalsIgnoreCase("INTERNATIONAL")) {
        logger.info("Entered CBS File is INTERNATIONAL");
        uploaded = uploadInternationalData(setupBean, con, file, sourceBean);
      } else {
        logger.info("Entered File is Wrong");
        return false;
      } 
      logger.info("***** ReadRupay.readData End ****");
      return true;
    } catch (Exception e) {
      logger.error(" error in ReadRupay.readData", new Exception("ReadRupay.readData", e));
      return false;
    } 
  }
  
  public boolean uploadDomesticData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    logger.info("***** ReadRupay.uploadDomesticData Start ****");
    String insert = "";
    if (setupBean.getCategory().equalsIgnoreCase("QSPARC")) {
      insert = "INSERT  INTO  qsparc_88_rawdata (MTI,Function_Code ,Record_Number,Member_Institution_ID_Code,Unique_File_Name,Date_Settlement,Product_Code,Settlement_BIN,File_Category,Version_Number,Entire_File_Reject_Indicator,File_Reject_Reason_Code,Transactions_Count,Run_Total_Amount,Acquirer_Institution_ID_code,Amount_Settlement,Amount_Transaction,Approval_Code,Acquirer_Reference_Data,Case_Number,Currency_Code_Settlement,Currency_Code_Transaction,Conversion_Rate_Settlement,Card_Acceptor_Addi_Addr,Card_Acceptor_Terminal_ID,Card_Acceptor_Zip_Code,DateandTime_Local_Transaction,TXNFunction_Code ,Late_Presentment_Indicator,TXNMTI,Primary_Account_Number,TXNRecord_Number,RGCS_Received_date,Settlement_DR_CR_Indicator,Txn_Desti_Insti_ID_code,Txn_Origin_Insti_ID_code,Card_Holder_UID,Amount_Billing,Currency_Code_Billing,Conversion_Rate_billing,Message_Reason_Code,Fee_DR_CR_Indicator1,Fee_amount1,Fee_Currency1,Fee_Type_Code1,Interchange_Category1,Fee_DR_CR_Indicator2,Fee_amount2,Fee_Currency2,Fee_Type_Code2,Interchange_Category2,Fee_DR_CR_Indicator3,Fee_amount3,Fee_Currency3,Fee_Type_Code3,Interchange_Category3,Fee_DR_CR_Indicator4,Fee_amount4,Fee_Currency4,Fee_Type_Code4,Interchange_Category4,Fee_DR_CR_Indicator5,Fee_amount5,Fee_Currency5,Fee_Type_Code5,Interchange_Category5,Trl_FUNCTION_CODE,Trl_RECORD_NUMBER,flag,FILEDATE,PAN,CREATEDDATE,RRN,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,str_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?)";
    } else {
      insert = "INSERT  INTO   rupay_88_rawdata (MTI,Function_Code ,Record_Number,Member_Institution_ID_Code,Unique_File_Name,Date_Settlement,Product_Code,Settlement_BIN,File_Category,Version_Number,Entire_File_Reject_Indicator,File_Reject_Reason_Code,Transactions_Count,Run_Total_Amount,Acquirer_Institution_ID_code,Amount_Settlement,Amount_Transaction,Approval_Code,Acquirer_Reference_Data,Case_Number,Currency_Code_Settlement,Currency_Code_Transaction,Conversion_Rate_Settlement,Card_Acceptor_Addi_Addr,Card_Acceptor_Terminal_ID,Card_Acceptor_Zip_Code,DateandTime_Local_Transaction,TXNFunction_Code ,Late_Presentment_Indicator,TXNMTI,Primary_Account_Number,TXNRecord_Number,RGCS_Received_date,Settlement_DR_CR_Indicator,Txn_Desti_Insti_ID_code,Txn_Origin_Insti_ID_code,Card_Holder_UID,Amount_Billing,Currency_Code_Billing,Conversion_Rate_billing,Message_Reason_Code,Fee_DR_CR_Indicator1,Fee_amount1,Fee_Currency1,Fee_Type_Code1,Interchange_Category1,Fee_DR_CR_Indicator2,Fee_amount2,Fee_Currency2,Fee_Type_Code2,Interchange_Category2,Fee_DR_CR_Indicator3,Fee_amount3,Fee_Currency3,Fee_Type_Code3,Interchange_Category3,Fee_DR_CR_Indicator4,Fee_amount4,Fee_Currency4,Fee_Type_Code4,Interchange_Category4,Fee_DR_CR_Indicator5,Fee_amount5,Fee_Currency5,Fee_Type_Code5,Interchange_Category5,Trl_FUNCTION_CODE,Trl_RECORD_NUMBER,flag,FILEDATE,PAN,CREATEDDATE,RRN,FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,str_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?)";
    } 
    String trl_nFunCd = null, trl_nRecNum = null, transactions_count = null;
    int feesize = 1;
    try {
      PreparedStatement ps = con.prepareStatement(insert);
      Pattern TAG_REGEX = Pattern.compile(">(.+?)</");
      Pattern node_REGEX = Pattern.compile("<(.+?)>");
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      String thisLine = null;
      int count = 1;
      String hdr = "", trl = "";
      RupayUtilBean utilBean = new RupayUtilBean();
      RupayHeaderUtil headerUtil = new RupayHeaderUtil();
      logger.info("Process started" + (new Date()).getTime());
      while ((thisLine = br.readLine()) != null) {
        Matcher nodeMatcher = node_REGEX.matcher(thisLine);
        nodeMatcher.find();
        if (!nodeMatcher.group(1).equalsIgnoreCase("Txn")) {
          if (nodeMatcher.group(1).equalsIgnoreCase("Hdr")) {
            hdr = "hdr";
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("/Hdr")) {
            hdr = "";
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nDtTmFlGen")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnDtTmFlGen(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nMemInstCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnMemInstCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nUnFlNm")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnUnFlNm(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nProdCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnProdCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nSetBIN")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnSetBIN(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFlCatg")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnFlCatg(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nVerNum")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnVerNum(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nAcqInstCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnAcqInstCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nAmtSet")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            double amtSet = Integer.parseInt(matcher.group(1));
            amtSet /= 100.0D;
            utilBean.setnAmtSet(String.valueOf(amtSet));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nAmtTxn")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            double amtTxn = Double.parseDouble(matcher.group(1));
            amtTxn /= 100.0D;
            utilBean.setnAmtTxn(String.valueOf(amtTxn));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nApprvlCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnApprvlCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nRRN")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnARD(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdSet")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCcyCdSet(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdTxn")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCcyCdTxn(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nConvRtSet")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnConvRtSet(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpAddAdrs")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCrdAcpAddAdrs(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcptTrmId")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCrdAcptTrmId(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpZipCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCrdAcpZipCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nDtSet")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            if (hdr.equalsIgnoreCase("hdr")) {
              headerUtil.setnDtSet(matcher.group(1));
              continue;
            } 
            utilBean.setnDtSet(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nDtTmLcTxn")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnDtTmLcTxn(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFunCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            if (hdr.equalsIgnoreCase("hdr")) {
              headerUtil.setnFunCd(matcher.group(1));
              continue;
            } 
            if (hdr.equalsIgnoreCase("Trl")) {
              trl_nFunCd = matcher.group(1);
              logger.info(trl_nFunCd);
              continue;
            } 
            utilBean.setnFunCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nLtPrsntInd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnLtPrsntInd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nMTI")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            if (hdr.equalsIgnoreCase("hdr")) {
              headerUtil.setnMTI(matcher.group(1));
              continue;
            } 
            utilBean.setnMTI(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nPAN")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnPAN(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nRecNum")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            if (hdr.equalsIgnoreCase("hdr")) {
              headerUtil.setnRecNum(matcher.group(1));
              continue;
            } 
            if (hdr.equalsIgnoreCase("Trl")) {
              headerUtil.setTrl_nRecNum(matcher.group(1));
              trl_nRecNum = matcher.group(1);
              continue;
            } 
            utilBean.setnRecNum(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nRGCSRcvdDt")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnRGCSRcvdDt(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nSetDCInd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnSetDCInd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nTxnDesInstCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnTxnDesInstCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpBussCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCrdAcpBussCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpNm")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCrdAcpNm(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nTxnOrgInstCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnTxnOrgInstCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nUID")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnUID(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFeeDCInd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnFeeDCInd1(matcher.group(1));
                continue;
              case 2:
                utilBean.setnFeeDCInd2(matcher.group(1));
                continue;
              case 3:
                utilBean.setnFeeDCInd3(matcher.group(1));
                continue;
              case 4:
                utilBean.setnFeeDCInd4(matcher.group(1));
                continue;
              case 5:
                utilBean.setnFeeDCInd5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFeeAmt")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnFeeAmt1(matcher.group(1));
                continue;
              case 2:
                utilBean.setnFeeAmt2(matcher.group(1));
                continue;
              case 3:
                utilBean.setnFeeAmt3(matcher.group(1));
                continue;
              case 4:
                utilBean.setnFeeAmt4(matcher.group(1));
                continue;
              case 5:
                utilBean.setFeeAmt5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFeeCcy")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnFeeCcy1(matcher.group(1));
                continue;
              case 2:
                utilBean.setnFeeCcy2(matcher.group(1));
                continue;
              case 3:
                utilBean.setnFeeCcy3(matcher.group(1));
                continue;
              case 4:
                utilBean.setnFeeCcy4(matcher.group(1));
                continue;
              case 5:
                utilBean.setnFeeCcy5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFeeTpCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnFeeTpCd1(matcher.group(1));
                continue;
              case 2:
                utilBean.setnFeeTpCd2(matcher.group(1));
                continue;
              case 3:
                utilBean.setnFeeTpCd3(matcher.group(1));
                continue;
              case 4:
                utilBean.setnFeeTpCd4(matcher.group(1));
                continue;
              case 5:
                utilBean.setnFeeTpCd5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nIntrchngCtg")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnIntrchngCtg1(matcher.group(1));
                continue;
              case 2:
                utilBean.setnIntrchngCtg2(matcher.group(1));
                continue;
              case 3:
                utilBean.setnIntrchngCtg3(matcher.group(1));
                continue;
              case 4:
                utilBean.setnIntrchngCtg4(matcher.group(1));
                continue;
              case 5:
                utilBean.setnIntrchngCtg5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("/Fee")) {
            feesize++;
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCaseNum")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCaseNum(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nContNum")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnContNum(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFulParInd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnFulParInd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nProcCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnProdCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nAmtBil")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnAmtBil(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdBil")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCcyCdBil(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nConvRtBil")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnConvRtBil(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nMsgRsnCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnMsgRsnCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nRnTtlAmt")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnRnTtlAmt(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nTxnCnt")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnTxnCnt(matcher.group(1));
            transactions_count = matcher.group(1);
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("Trl")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            hdr = "Trl";
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("/Trl")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            hdr = "";
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("/Txn")) {
            feesize = 1;
            ps.setString(1, headerUtil.getnMTI());
            ps.setString(2, headerUtil.getnFunCd());
            ps.setString(3, headerUtil.getnRecNum());
            ps.setString(4, headerUtil.getnMemInstCd());
            ps.setString(5, headerUtil.getnUnFlNm());
            ps.setString(6, headerUtil.getnDtSet());
            ps.setString(7, headerUtil.getnProdCd());
            ps.setString(8, headerUtil.getnSetBIN());
            ps.setString(9, headerUtil.getnFlCatg());
            ps.setString(10, headerUtil.getnVerNum());
            ps.setString(11, (String)null);
            ps.setString(12, (String)null);
            ps.setString(13, headerUtil.getnTxnCnt());
            ps.setString(14, headerUtil.getnRnTtlAmt());
            ps.setString(15, utilBean.getnAcqInstCd());
            ps.setString(16, utilBean.getnAmtSet());
            ps.setString(17, utilBean.getnAmtTxn());
            ps.setString(18, utilBean.getnApprvlCd());
            ps.setString(19, utilBean.getnARD());
            ps.setString(20, utilBean.getnCaseNum());
            ps.setString(21, utilBean.getnCcyCdSet());
            ps.setString(22, utilBean.getnCcyCdTxn());
            ps.setString(23, utilBean.getnConvRtSet());
            ps.setString(24, utilBean.getnCrdAcpAddAdrs());
            ps.setString(25, utilBean.getnCrdAcptTrmId());
            ps.setString(26, utilBean.getnCrdAcpZipCd());
            ps.setString(27, utilBean.getnDtTmLcTxn());
            ps.setString(28, utilBean.getnFunCd());
            ps.setString(29, utilBean.getnLtPrsntInd());
            ps.setString(30, utilBean.getnMTI());
            String pan = utilBean.getnPAN().trim();
            String Update_Pan = "";
            if (pan.length() <= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
              Update_Pan = String.valueOf(pan.substring(0, 6)) + "XXXXXX" + pan.substring(pan.length() - 4);
            } else if (pan.length() >= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
              Update_Pan = String.valueOf(pan.substring(0, 6)) + "XXXXXXXXX" + pan.substring(pan.length() - 4);
            } else {
              Update_Pan = null;
            } 
            ps.setString(31, Update_Pan);
            ps.setString(32, utilBean.getnRecNum());
            ps.setString(33, utilBean.getnRGCSRcvdDt());
            ps.setString(34, utilBean.getnSetDCInd());
            ps.setString(35, utilBean.getnTxnDesInstCd());
            ps.setString(36, utilBean.getnTxnOrgInstCd());
            ps.setString(37, utilBean.getnUID());
            ps.setString(38, utilBean.getnAmtBil());
            ps.setString(39, utilBean.getnCcyCdBil());
            ps.setString(40, utilBean.getnConvRtBil());
            ps.setString(41, utilBean.getnMsgRsnCd());
            ps.setString(42, utilBean.getnFeeDCInd1());
            ps.setString(43, utilBean.getnFeeAmt1());
            ps.setString(44, utilBean.getnFeeCcy1());
            ps.setString(45, utilBean.getnFeeTpCd1());
            ps.setString(46, utilBean.getnIntrchngCtg1());
            ps.setString(47, utilBean.getnFeeDCInd2());
            ps.setString(48, utilBean.getnFeeAmt2());
            ps.setString(49, utilBean.getnFeeCcy2());
            ps.setString(50, utilBean.getnFeeTpCd2());
            ps.setString(51, utilBean.getnIntrchngCtg2());
            ps.setString(52, utilBean.getnFeeDCInd3());
            ps.setString(53, utilBean.getnFeeAmt3());
            ps.setString(54, utilBean.getnFeeCcy3());
            ps.setString(55, utilBean.getnFeeTpCd3());
            ps.setString(56, utilBean.getnIntrchngCtg3());
            ps.setString(57, utilBean.getnFeeDCInd4());
            ps.setString(58, utilBean.getnFeeAmt4());
            ps.setString(59, utilBean.getnFeeCcy4());
            ps.setString(60, utilBean.getnFeeTpCd4());
            ps.setString(61, utilBean.getnIntrchngCtg4());
            ps.setString(62, utilBean.getnFeeDCInd5());
            ps.setString(63, utilBean.getFeeAmt5());
            ps.setString(64, utilBean.getnFeeCcy5());
            ps.setString(65, utilBean.getnFeeTpCd5());
            ps.setString(66, utilBean.getnIntrchngCtg5());
            ps.setString(67, headerUtil.getTrl_nFunCd());
            ps.setString(68, headerUtil.getTrl_nRecNum());
            ps.setString(69, "D");
            ps.setString(70, setupBean.getFileDate());
            ps.setString(71, pan);
            ps.setString(72, utilBean.getnARD());
            ps.setString(73, file.getOriginalFilename());
            ps.addBatch();
            utilBean = new RupayUtilBean();
            count++;
            if (count == 10000) {
              count = 1;
              ps.executeBatch();
              logger.info("Executed batch");
              count++;
            } 
          } 
        } 
      } 
      ps.executeBatch();
      br.close();
      ps.close();
      con.close();
      logger.info("***** ReadRupay.uploadDomesticData End ****");
      return true;
    } catch (Exception ex) {
      logger.error(" error in ReadRupay.uploadDomesticData ", new Exception(" ReadRupay.uploadDomesticData ", ex));
      return false;
    } 
  }
  
  public boolean uploadInternationalData(CompareSetupBean setupBean, Connection con, MultipartFile file, FileSourceBean sourceBean) {
    logger.info("***** ReadRupay.uploadInternationalData Start ****");
    String insert = "";
    if (setupBean.getCategory().equalsIgnoreCase("QSPARC")) {
      insert = "INSERT  INTO qsparc_int_88_rawdata (MTI,Function_Code ,Record_Number,Member_Institution_ID_Code,Unique_File_Name,Date_Settlement,Product_Code,Settlement_BIN,File_Category,Version_Number,Entire_File_Reject_Indicator,File_Reject_Reason_Code,Transactions_Count,Run_Total_Amount,Acquirer_Institution_ID_code,Amount_Settlement,Amount_Transaction,Approval_Code,Acquirer_Reference_Data,Case_Number,Currency_Code_Settlement,Currency_Code_Transaction,Conversion_Rate_Settlement,Card_Acceptor_Addi_Addr,Card_Acceptor_Terminal_ID,Card_Acceptor_Zip_Code,DateandTime_Local_Transaction,TXNFunction_Code ,Late_Presentment_Indicator,TXNMTI,Primary_Account_Number,TXNRecord_Number,RGCS_Received_date,Settlement_DR_CR_Indicator,Txn_Desti_Insti_ID_code,Txn_Origin_Insti_ID_code,Card_Holder_UID,Amount_Billing,Currency_Code_Billing,Conversion_Rate_billing,Message_Reason_Code,Fee_DR_CR_Indicator1,Fee_amount1,Fee_Currency1,Fee_Type_Code1,Interchange_Category1,Fee_DR_CR_Indicator2,Fee_amount2,Fee_Currency2,Fee_Type_Code2,Interchange_Category2,Fee_DR_CR_Indicator3,Fee_amount3,Fee_Currency3,Fee_Type_Code3,Interchange_Category3,Fee_DR_CR_Indicator4,Fee_amount4,Fee_Currency4,Fee_Type_Code4,Interchange_Category4,Fee_DR_CR_Indicator5,Fee_amount5,Fee_Currency5,Fee_Type_Code5,Interchange_Category5,Trl_FUNCTION_CODE,Trl_RECORD_NUMBER,flag,FILEDATE,PAN, FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,str_to_date(?,'%Y/%m/%d'),?,?)";
    } else {
      insert = "INSERT  INTO  rupay_88_int_rawdata (MTI,Function_Code ,Record_Number,Member_Institution_ID_Code,Unique_File_Name,Date_Settlement,Product_Code,Settlement_BIN,File_Category,Version_Number,Entire_File_Reject_Indicator,File_Reject_Reason_Code,Transactions_Count,Run_Total_Amount,Acquirer_Institution_ID_code,Amount_Settlement,Amount_Transaction,Approval_Code,Acquirer_Reference_Data,Case_Number,Currency_Code_Settlement,Currency_Code_Transaction,Conversion_Rate_Settlement,Card_Acceptor_Addi_Addr,Card_Acceptor_Terminal_ID,Card_Acceptor_Zip_Code,DateandTime_Local_Transaction,TXNFunction_Code ,Late_Presentment_Indicator,TXNMTI,Primary_Account_Number,TXNRecord_Number,RGCS_Received_date,Settlement_DR_CR_Indicator,Txn_Desti_Insti_ID_code,Txn_Origin_Insti_ID_code,Card_Holder_UID,Amount_Billing,Currency_Code_Billing,Conversion_Rate_billing,Message_Reason_Code,Fee_DR_CR_Indicator1,Fee_amount1,Fee_Currency1,Fee_Type_Code1,Interchange_Category1,Fee_DR_CR_Indicator2,Fee_amount2,Fee_Currency2,Fee_Type_Code2,Interchange_Category2,Fee_DR_CR_Indicator3,Fee_amount3,Fee_Currency3,Fee_Type_Code3,Interchange_Category3,Fee_DR_CR_Indicator4,Fee_amount4,Fee_Currency4,Fee_Type_Code4,Interchange_Category4,Fee_DR_CR_Indicator5,Fee_amount5,Fee_Currency5,Fee_Type_Code5,Interchange_Category5,Trl_FUNCTION_CODE,Trl_RECORD_NUMBER,flag,FILEDATE,PAN, FILENAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,str_to_date(?,'%Y/%m/%d'),?,?)";
    } 
    logger.info("insert==" + insert);
    String trl_nFunCd = null, trl_nRecNum = null, transactions_count = null;
    int feesize = 1;
    try {
      PreparedStatement ps = con.prepareStatement(insert);
      Pattern TAG_REGEX = Pattern.compile(">(.+?)</");
      Pattern node_REGEX = Pattern.compile("<(.+?)>");
      BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
      String thisLine = null;
      int count = 1;
      String hdr = "", trl = "";
      RupayUtilBean utilBean = new RupayUtilBean();
      RupayHeaderUtil headerUtil = new RupayHeaderUtil();
      logger.info("Process started" + (new Date()).getTime());
      while ((thisLine = br.readLine()) != null) {
        Matcher nodeMatcher = node_REGEX.matcher(thisLine);
        nodeMatcher.find();
        if (!nodeMatcher.group(1).equalsIgnoreCase("Txn")) {
          if (nodeMatcher.group(1).equalsIgnoreCase("Hdr")) {
            hdr = "hdr";
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("/Hdr")) {
            hdr = "";
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nDtTmFlGen")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnDtTmFlGen(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nMemInstCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnMemInstCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nUnFlNm")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnUnFlNm(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nProdCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnProdCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nSetBIN")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnSetBIN(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFlCatg")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnFlCatg(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nVerNum")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnVerNum(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nAcqInstCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnAcqInstCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nAmtSet")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            double amtTxn = Double.parseDouble(matcher.group(1));
            amtTxn /= 100.0D;
            utilBean.setnAmtTxn(String.valueOf(amtTxn));
            utilBean.setnAmtSet(String.valueOf(amtTxn));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nAmtTxn")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            double amtTxn = Double.parseDouble(matcher.group(1));
            amtTxn /= 100.0D;
            utilBean.setnAmtTxn(String.valueOf(amtTxn));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nApprvlCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnApprvlCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nRRN")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnARD(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdSet")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCcyCdSet(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdTxn")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCcyCdTxn(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nConvRtSet")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnConvRtSet(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpAddAdrs")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCrdAcpAddAdrs(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcptTrmId")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCrdAcptTrmId(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpZipCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCrdAcpZipCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nDtSet")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            if (hdr.equalsIgnoreCase("hdr")) {
              headerUtil.setnDtSet(matcher.group(1));
              continue;
            } 
            utilBean.setnDtSet(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nDtTmLcTxn")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnDtTmLcTxn(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFunCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            if (hdr.equalsIgnoreCase("hdr")) {
              headerUtil.setnFunCd(matcher.group(1));
              continue;
            } 
            if (hdr.equalsIgnoreCase("Trl")) {
              trl_nFunCd = matcher.group(1);
              logger.info(trl_nFunCd);
              continue;
            } 
            utilBean.setnFunCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nLtPrsntInd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnLtPrsntInd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nMTI")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            if (hdr.equalsIgnoreCase("hdr")) {
              headerUtil.setnMTI(matcher.group(1));
              continue;
            } 
            utilBean.setnMTI(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nPAN")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnPAN(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nRecNum")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            if (hdr.equalsIgnoreCase("hdr")) {
              headerUtil.setnRecNum(matcher.group(1));
              continue;
            } 
            if (hdr.equalsIgnoreCase("Trl")) {
              headerUtil.setTrl_nRecNum(matcher.group(1));
              trl_nRecNum = matcher.group(1);
              logger.info(trl_nRecNum);
              continue;
            } 
            utilBean.setnRecNum(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nRGCSRcvdDt")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnRGCSRcvdDt(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nSetDCInd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnSetDCInd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nTxnDesInstCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnTxnDesInstCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpBussCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCrdAcpBussCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpNm")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCrdAcpNm(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nTxnOrgInstCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnTxnOrgInstCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nUID")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnUID(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFeeDCInd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnFeeDCInd1(matcher.group(1));
                continue;
              case 2:
                logger.info("setnFeeDCInd2");
                utilBean.setnFeeDCInd2(matcher.group(1));
                continue;
              case 3:
                logger.info("setnFeeDCInd3");
                utilBean.setnFeeDCInd3(matcher.group(1));
                continue;
              case 4:
                logger.info("setnFeeDCInd4");
                utilBean.setnFeeDCInd4(matcher.group(1));
                continue;
              case 5:
                logger.info("setnFeeDCInd5");
                utilBean.setnFeeDCInd5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFeeAmt")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnFeeAmt1(matcher.group(1));
                continue;
              case 2:
                logger.info("setnFeeAmt2");
                utilBean.setnFeeAmt2(matcher.group(1));
                continue;
              case 3:
                logger.info("setnFeeAmt3");
                utilBean.setnFeeAmt3(matcher.group(1));
                continue;
              case 4:
                logger.info("setnFeeAmt4");
                utilBean.setnFeeAmt4(matcher.group(1));
                continue;
              case 5:
                logger.info("setnFeeAmt5");
                utilBean.setFeeAmt5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFeeCcy")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnFeeCcy1(matcher.group(1));
                continue;
              case 2:
                logger.info("nFeeCcy2");
                utilBean.setnFeeCcy2(matcher.group(1));
                continue;
              case 3:
                logger.info("nFeeCcy3");
                utilBean.setnFeeCcy3(matcher.group(1));
                continue;
              case 4:
                logger.info("nFeeCcy4");
                utilBean.setnFeeCcy4(matcher.group(1));
                continue;
              case 5:
                logger.info("nFeeCcy5");
                utilBean.setnFeeCcy5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFeeTpCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnFeeTpCd1(matcher.group(1));
                continue;
              case 2:
                utilBean.setnFeeTpCd2(matcher.group(1));
                continue;
              case 3:
                utilBean.setnFeeTpCd3(matcher.group(1));
                continue;
              case 4:
                utilBean.setnFeeTpCd4(matcher.group(1));
                continue;
              case 5:
                utilBean.setnFeeTpCd5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nIntrchngCtg")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            switch (feesize) {
              case 1:
                utilBean.setnIntrchngCtg1(matcher.group(1));
                continue;
              case 2:
                utilBean.setnIntrchngCtg2(matcher.group(1));
                continue;
              case 3:
                utilBean.setnIntrchngCtg3(matcher.group(1));
                continue;
              case 4:
                utilBean.setnIntrchngCtg4(matcher.group(1));
                continue;
              case 5:
                utilBean.setnIntrchngCtg5(matcher.group(1));
                continue;
            } 
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("/Fee")) {
            feesize++;
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCaseNum")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCaseNum(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nContNum")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnContNum(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nFulParInd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnFulParInd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nProcCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnProdCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nAmtBil")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnAmtBil(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdBil")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnCcyCdBil(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nConvRtBil")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnConvRtBil(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nMsgRsnCd")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            utilBean.setnMsgRsnCd(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nRnTtlAmt")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnRnTtlAmt(matcher.group(1));
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("nTxnCnt")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            headerUtil.setnTxnCnt(matcher.group(1));
            transactions_count = matcher.group(1);
            logger.info(transactions_count);
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("Trl")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            hdr = "Trl";
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("/Trl")) {
            Matcher matcher = TAG_REGEX.matcher(thisLine);
            matcher.find();
            hdr = "";
            continue;
          } 
          if (nodeMatcher.group(1).equalsIgnoreCase("/Txn")) {
            feesize = 1;
            ps.setString(1, headerUtil.getnMTI());
            ps.setString(2, headerUtil.getnFunCd());
            ps.setString(3, headerUtil.getnRecNum());
            ps.setString(4, headerUtil.getnMemInstCd());
            ps.setString(5, headerUtil.getnUnFlNm());
            ps.setString(6, headerUtil.getnDtSet());
            ps.setString(7, headerUtil.getnProdCd());
            ps.setString(8, headerUtil.getnSetBIN());
            ps.setString(9, headerUtil.getnFlCatg());
            ps.setString(10, headerUtil.getnVerNum());
            ps.setString(11, (String)null);
            ps.setString(12, (String)null);
            ps.setString(13, headerUtil.getnTxnCnt());
            ps.setString(14, headerUtil.getnRnTtlAmt());
            ps.setString(15, utilBean.getnAcqInstCd());
            ps.setString(16, utilBean.getnAmtSet());
            ps.setString(17, utilBean.getnAmtTxn());
            ps.setString(18, utilBean.getnApprvlCd());
            ps.setString(19, utilBean.getnARD());
            ps.setString(20, utilBean.getnCaseNum());
            ps.setString(21, utilBean.getnCcyCdSet());
            ps.setString(22, utilBean.getnCcyCdTxn());
            ps.setString(23, utilBean.getnConvRtSet());
            ps.setString(24, utilBean.getnCrdAcpAddAdrs());
            ps.setString(25, utilBean.getnCrdAcptTrmId());
            ps.setString(26, utilBean.getnCrdAcpZipCd());
            ps.setString(27, utilBean.getnDtTmLcTxn());
            ps.setString(28, utilBean.getnFunCd());
            ps.setString(29, utilBean.getnLtPrsntInd());
            ps.setString(30, utilBean.getnMTI());
            String pan = utilBean.getnPAN().trim();
            String Update_Pan = "";
            if (pan.length() <= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
              Update_Pan = String.valueOf(pan.substring(0, 6)) + "XXXXXX" + pan.substring(pan.length() - 4);
            } else if (pan.length() >= 16 && pan != null && pan.trim() != "" && pan.length() > 0) {
              Update_Pan = String.valueOf(pan.substring(0, 6)) + "XXXXXXXXX" + pan.substring(pan.length() - 4);
            } else {
              Update_Pan = null;
            } 
            ps.setString(31, Update_Pan);
            ps.setString(32, utilBean.getnRecNum());
            ps.setString(33, utilBean.getnRGCSRcvdDt());
            ps.setString(34, utilBean.getnSetDCInd());
            ps.setString(35, utilBean.getnTxnDesInstCd());
            ps.setString(36, utilBean.getnTxnOrgInstCd());
            ps.setString(37, utilBean.getnUID());
            ps.setString(38, utilBean.getnAmtBil());
            ps.setString(39, utilBean.getnCcyCdBil());
            ps.setString(40, utilBean.getnConvRtBil());
            ps.setString(41, utilBean.getnMsgRsnCd());
            ps.setString(42, utilBean.getnFeeDCInd1());
            ps.setString(43, utilBean.getnFeeAmt1());
            ps.setString(44, utilBean.getnFeeCcy1());
            ps.setString(45, utilBean.getnFeeTpCd1());
            ps.setString(46, utilBean.getnIntrchngCtg1());
            ps.setString(47, utilBean.getnFeeDCInd2());
            ps.setString(48, utilBean.getnFeeAmt2());
            ps.setString(49, utilBean.getnFeeCcy2());
            ps.setString(50, utilBean.getnFeeTpCd2());
            ps.setString(51, utilBean.getnIntrchngCtg2());
            ps.setString(52, utilBean.getnFeeDCInd3());
            ps.setString(53, utilBean.getnFeeAmt3());
            ps.setString(54, utilBean.getnFeeCcy3());
            ps.setString(55, utilBean.getnFeeTpCd3());
            ps.setString(56, utilBean.getnIntrchngCtg3());
            ps.setString(57, utilBean.getnFeeDCInd4());
            ps.setString(58, utilBean.getnFeeAmt4());
            ps.setString(59, utilBean.getnFeeCcy4());
            ps.setString(60, utilBean.getnFeeTpCd4());
            ps.setString(61, utilBean.getnIntrchngCtg4());
            ps.setString(62, utilBean.getnFeeDCInd5());
            ps.setString(63, utilBean.getFeeAmt5());
            ps.setString(64, utilBean.getnFeeCcy5());
            ps.setString(65, utilBean.getnFeeTpCd5());
            ps.setString(66, utilBean.getnIntrchngCtg5());
            ps.setString(67, headerUtil.getTrl_nFunCd());
            ps.setString(68, headerUtil.getTrl_nRecNum());
            ps.setString(69, "I");
            ps.setString(70, setupBean.getFileDate());
            ps.setString(71, pan);
            ps.setString(72, file.getOriginalFilename());
            ps.addBatch();
            utilBean = new RupayUtilBean();
            count++;
            if (count == 20000) {
              count = 1;
              ps.executeBatch();
              logger.info("Executed batch");
              count++;
            } 
          } 
        } 
      } 
      ps.executeBatch();
      logger.info(trl_nFunCd);
      logger.info(trl_nRecNum);
      logger.info(transactions_count);
      logger.info("Process ended" + (new Date()).getTime());
      br.close();
      ps.close();
      con.close();
      logger.info("***** ReadRupay.uploadInternationalData End ****");
      return true;
    } catch (Exception ex) {
      logger.error(" error in ReadRupay.uploadInternationalData", new Exception("ReadRupay.uploadInternationalData", ex));
      return false;
    } 
  }
}
