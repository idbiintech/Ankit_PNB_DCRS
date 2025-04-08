package com.recon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;
import com.recon.model.NFSSettlementBean;
import com.recon.service.ICompareConfigService;

public class ReadNUploadSUSPECTFiles {

	@Autowired
	ICompareConfigService icompareConfigService;

	String partid;
	private int Part_id;
	private static final String O_ERROR_MESSAGE = "o_error_message";
	public static final String CATALINA_HOME = "catalina.home";
	public static final String OUTPUT_FOLDER = System.getProperty("catalina.home") + File.separator + "OUTPUT_FOLDER";

	@Autowired
	Utility utility;

	public boolean uploadCBSData(String fileName, String filepath) {

		String[] filenameSplit = fileName.split("_");

		try {
			boolean uploaded = false;
			System.out.println(fileName);
			if (filenameSplit[0].contains("CBS702"))// ISSUER
			{
				System.out.println("Entered CBS File is Issuer");
				ReadNUploadCBSIssuer readIssuer = new ReadNUploadCBSIssuer();
				uploaded = readIssuer.uploadCBSData(fileName, filepath);
			} else if (filenameSplit[0].contains("CBS703"))// ACQUIRER
			{
				System.out.println("Entered CBS File is Acquirer");
				ReadNUploadCBSAcquirer readacquirer = new ReadNUploadCBSAcquirer();
				uploaded = readacquirer.uploadCBSData(fileName, filepath);

			} else if (filenameSplit[0].equalsIgnoreCase("CBSC43") || filenameSplit[0].equalsIgnoreCase("CBS43"))// ONUS
			{
				System.out.println("Entered CBS File is ONUS");
				ReadNUploadCBSOnus readOnus = new ReadNUploadCBSOnus();
				uploaded = readOnus.Read_CBSData(fileName, filepath);

			} else {
				System.out.println("Entered File is Wrong");
				return false;
			}
			return true;

		} catch (Exception e) {

			System.out.println("Error Occured");
			e.printStackTrace();

			return false;
		}

	}

	public static void main(String[] args) {

		ReadNUploadSUSPECTFiles readcbs = new ReadNUploadSUSPECTFiles();

		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter file path: ");
		System.out.flush();
		String filename = scanner.nextLine();
		File file = new File(filename);
		// Subcategory IS NOT NEEDED IN CASE OF AS SINGLE FILE IS USED FOR BOTH
		/*
		 * System.out.println("Enter Sub Category "); System.out.flush(); String
		 * stSubCategory = scanner.nextLine();
		 */

		// System.out.println(file.getName());

		/*
		 * File f = new File("\\\\10.143.11.50\\led\\DCRS\\AMEXCBS");
		 * if(!(f.exists())) {
		 * 
		 * if(f.mkdir()) {
		 * 
		 * System.out.println("directory created"); } }
		 */

		/*
		 * if(file.renameTo(new File("\\\\10.143.11.50\\led\\DCRS\\AMEXCBS\\" +
		 * file.getName()))) {
		 * 
		 * System.out.println("File Moved Successfully");
		 * 
		 * readcbs.uploadCBSData(file.getName());
		 * 
		 * System.out.println("Process Completed");
		 * 
		 * }else {
		 * 
		 * System.out.println("Error Occured while moving file"); }
		 */

		if (readcbs.uploadCBSData(file.getName(), file.getPath())) {
			System.out.println("File uploaded successfully");
		} else
			System.out.println("File uploading failed");

	}

	public boolean uploadCBSData(CompareSetupBean setupBean, Connection connection, MultipartFile file,
			FileSourceBean sourceBean) {
		System.out.println("uploadCBSData method called");
		try {
			boolean uploaded = false;

			System.out.println("Entered CBS File IS " + file.getOriginalFilename());
			return uploadISSData(setupBean, connection, file, sourceBean);

		} catch (Exception e) {

			System.out.println("Error Occured");
			e.printStackTrace();

			return false;
		}

	}

	// iccw cbs data

	public boolean iccwuploadCBSData(CompareSetupBean setupBean, Connection connection, MultipartFile file,
			FileSourceBean sourceBean) {
		try {
			boolean uploaded = false;
			System.out.println("Entered CBS File IS " + file.getOriginalFilename());
			return iccwuploadcbsData(setupBean, connection, file, sourceBean);
		} catch (Exception e) {
			System.out.println("Error Occured");
			e.printStackTrace();
			return false;
		}

	}

	public boolean uploadONUSData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {

		int flag = 1, batch = 0;

		InputStream fis = null;
		boolean readdata = false;

		String thisLine = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			System.out.println("Reading data " + new Date().toString());

			String insert = "INSERT INTO CBS_RAWDATA "
					+ "(ACCOUNT_NUMBER,TRANDATE,VALUEDATE,TRAN_ID,TRAN_PARTICULAR,TRAN_RMKS,PART_TRAN_TYPE,TRAN_PARTICULAR1,TRAN_AMT,BALANCE,PSTD_USER_ID,CONTRA_ACCOUNT,ENTRY_DATE,VFD_DATE,REF_NUM,TRAN_PARTICULAR_2,ORG_ACCT,Part_id,FILEDATE,CREATEDDATE)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'dd/mm/yyyy')),sysdate)";

			PreparedStatement ps = con.prepareStatement(insert);

			int insrt = 0;

			while ((thisLine = br.readLine()) != null) {

				String[] splitarray = null;

				if (thisLine.contains("ACCOUNT NUMBER|TRAN DATE|")) {

					readdata = true;

				}
				if (!(thisLine.contains("ACCOUNT NUMBER|TRAN DATE|")) && readdata) {

					int srl = 1;

					ps.setString(15, null);
					ps.setString(16, null);

					splitarray = thisLine.split(Pattern.quote("|"));// Pattern.quote(ftpBean.getDataSeparator())

					for (int i = 0; i < splitarray.length; i++) {

						String value = splitarray[i];
						if (!value.trim().equalsIgnoreCase("")) {

							// 2 valuedate
							// 4 tran_particular

							/*
							 * System.out.println(splitarray[4]); if(i==2) {
							 * value = value +" "+
							 * splitarray[4].substring(19,27);
							 * ps.setString(srl,value.trim());
							 * 
							 * } else {
							 * 
							 * ps.setString(srl,value.trim()); }
							 */

							ps.setString(srl, value.trim());

							++srl;
						} else {

							ps.setString(srl, null);
							// System.out.println(srl+"null");
							++srl;
						}

					}
					/**** comment 15 and 16 for online file ****/

					ps.setString(17, null);
					ps.setInt(18, Part_id);
					ps.setString(19, setupBean.getFileDate());

					// System.out.println(insert);

					ps.addBatch();
					flag++;

					if (flag == 20000) {
						flag = 1;

						ps.executeBatch();
						System.out.println("Executed batch is " + batch);
						batch++;
					}

				}

			}
			ps.executeBatch();
			br.close();
			ps.close();
			System.out.println("Reading data " + new Date().toString());
			return true;

		} catch (Exception ex) {

			ex.printStackTrace();

			System.out.println("Exception" + ex);
			return false;
		}
	}

	public boolean uploadACQData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {

		int flag = 1, batch = 0;

		InputStream fis = null;
		boolean readdata = false;

		String thisLine = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			System.out.println("Reading data " + new Date().toString());

			String insert = "INSERT INTO CBS_AMEX_RAWDATA "
					+ "(FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,pstd_user_id ,ENTRY_DATE,VFD_DATE,PARTICULARALS2,Part_id,FILEDATE)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'dd/mm/yyyy')))";

			PreparedStatement ps = con.prepareStatement(insert);

			int insrt = 0;

			while ((thisLine = br.readLine()) != null) {

				String[] splitarray = null;

				if (thisLine.contains("------")) {

					readdata = true;

				}
				if (!(thisLine.contains("-----")) && readdata) {

					int srl = 1;
					// System.out.println(thisLine);

					splitarray = thisLine.split(Pattern.quote("|"));// Pattern.quote(ftpBean.getDataSeparator())

					for (int i = 0; i < splitarray.length; i++) {

						String value = splitarray[i];
						if (!value.equalsIgnoreCase("")) {

							ps.setString(srl, value.trim());

							++srl;
						} else {

							ps.setString(srl, null);
							// System.out.println(srl+"null");
							++srl;
						}

					}
					/**** comment 15 and 16 for online file ****/

					ps.setInt(16, Part_id);
					ps.setString(17, setupBean.getFileDate());

					// System.out.println(insert);

					ps.addBatch();
					flag++;

					if (flag == 20000) {
						flag = 1;

						ps.executeBatch();
						System.out.println("Executed batch is " + batch);
						batch++;
					}

					// insrt = ps.executeUpdate();

				}

			}
			ps.executeBatch();
			br.close();
			ps.close();
			System.out.println("Reading data " + new Date().toString());
			return true;

		} catch (Exception ex) {

			System.out.println("error occurred");
			ex.printStackTrace();
			return false;

		}

	}

	/*
	 * public boolean uploadISSData(CompareSetupBean setupBean, Connection con,
	 * MultipartFile file, FileSourceBean sourceBean) {
	 * 
	 * String filePath = ""; Resource resource = new
	 * ClassPathResource("/resources/switchpr.asc"); String stLine = null; int
	 * lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0; boolean
	 * batchExecuted = false;
	 * 
	 * String InsertQuery =
	 * "INSERT INTO CBS_UCO_RAWDATA_TEMP(SOLID, TRAN_AMOUNT, PART_TRAN_TYPE, CARD_NUMBER, RRN, ACC_NUMBER, SYSDT, SYS_TIME, CMD, TRAN_TYPE, DEVICE_ID, TRAN_ID, TRAN_DATE, FEE, SRV_TAX, SRV_CHRG, VISAIND, CREATEDBY, FILEDATE, BANK_NAME) "
	 * +
	 * "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'),'UCO')"
	 * ;
	 * 
	 * String InsertQuery =
	 * "INSERT INTO CBS_RUPAY_RAWDATA(FORACID,ORG_ACCT,REMARKS,FPAN, AMOUNT, TRAN_DATE, TRAN_TIME, E, PARTICULARALS, REF_NO, TRAN_ID, CREATEDBY, FILEDATE) "
	 * +
	 * "VALUES(?, ?, ?, ibkl_encrypt_decrypt.ibkl_set_encrypt_val(?), ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'))"
	 * ;
	 * 
	 * String delQuery = "delete from CBS_UCO_RAWDATA_TEMP"; try {
	 * 
	 * String fileNameWithExt = file.getOriginalFilename(); int lastDotIndex =
	 * fileNameWithExt.lastIndexOf("."); String fileNameWithoutExt = "";
	 * if(lastDotIndex > 0){ fileNameWithoutExt = fileNameWithExt.substring(0,
	 * lastDotIndex); } File folder = new File(OUTPUT_FOLDER);
	 * 
	 * if(folder.exists()){ folder.delete(); } folder.mkdir();
	 * 
	 * File file1 = new File(folder, fileNameWithoutExt);
	 * 
	 * try{ if(file1.exists()){ file1.delete(); } file1.createNewFile();
	 * }catch(Exception e){ System.out.println(e); }
	 * 
	 * String newPath = OUTPUT_FOLDER + File.separator + fileNameWithoutExt;
	 * 
	 * System.out.println("File to be write at path: "+newPath);
	 * 
	 * PgpHelper helper = new PgpHelper();
	 * 
	 * InputStream keyIn = resource.getInputStream(); FileOutputStream out = new
	 * FileOutputStream(newPath);
	 * 
	 * helper.decryptFile(file.getInputStream(), out, keyIn,
	 * "Atmcell@#12345".toCharArray()); //InputStream in =
	 * helper.decryptFile(file.getInputStream(), keyIn,
	 * "Atmcell@#12345".toCharArray()); PreparedStatement delpst =
	 * con.prepareStatement(delQuery); delpst.execute();
	 * 
	 * File outFile = new File(newPath);
	 * 
	 * System.out.println("File reading from Path: "+newPath);
	 * System.out.println("File reading : "+outFile); System.out.println(
	 * "File in tomcat "+outFile); // BufferedReader br = new BufferedReader(new
	 * InputStreamReader(file.getInputStream())); BufferedReader br = new
	 * BufferedReader(new FileReader(outFile)); try{ //BufferedReader br = new
	 * BufferedReader(new InputStreamReader(in));
	 * 
	 * PreparedStatement ps = con.prepareStatement(InsertQuery);
	 * 
	 * while ((stLine = br.readLine()) != null) {
	 * 
	 * if(stLine.startsWith("999  ,H") || stLine.startsWith("999  ,F")){
	 * continue; } lineNumber++; batchExecuted = false; sr_no = 1;
	 * 
	 * String[] splitData = stLine.split("\\,");
	 * 
	 * for (int i = 0; i <= (splitData.length - 1); i++) { if(i == 5){
	 * if(splitData[i].trim().equalsIgnoreCase(setupBean.getFileDate())){ return
	 * false; } } if ( i != 3 && i != 8 && i != 7 && i != 11 && i != 13 && i !=
	 * 14 && i != 15 ) { // System.out.println("i "+i+" data "+splitData[i-1]);
	 * if(i ==2){ String cardNumber = formatCardNumber(splitData[i].trim());
	 * ps.setString(sr_no++,cardNumber);
	 * ps.setString(sr_no++,splitData[i].trim()); //ps.setString(sr_no++,
	 * splitData[i].trim().substring(0,6)+"XXXXXX"+splitData[i].trim().substring
	 * (splitData[i].trim().length() - 4)); // ps.setString(sr_no++,
	 * splitData[i].trim().substring(0,6)+"XXXXXX"+splitData[i].trim().substring
	 * (12,16)); }else{ ps.setString(sr_no++, splitData[i].trim()); } } }
	 * 
	 * // System.out.println(sr_no); ps.setString(sr_no++,
	 * setupBean.getCreatedBy());
	 * 
	 * ps.setString(sr_no++, setupBean.getFileDate());
	 * 
	 * ps.addBatch(); batchSize++;
	 * 
	 * if (batchSize == 10000) { batchNumber++; System.out.println(
	 * "Batch Executed is " + batchNumber); ps.executeBatch(); batchSize = 0;
	 * batchExecuted = true; }
	 * 
	 * }
	 * 
	 * if (!batchExecuted) { batchNumber++; System.out.println(
	 * "Batch Executed is " + batchNumber); ps.executeBatch(); }
	 * 
	 * br.close(); ps.close(); System.out.println("Reading data " + new
	 * Date().toString()); } catch(IOException e){ System.out.println(
	 * "exception for converting is "+e); } return true;
	 * 
	 * } catch (Exception e) { System.out.println("Issue at line " + stLine);
	 * System.out.println("Exception in uploadISSData " + e); return false; }
	 * 
	 * }
	 */

	public boolean uploadISSData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {

		System.out.println("uploadISSData method called");
		String filePath = "";
		Resource resource = new ClassPathResource("/resources/switchpr.asc");
		String stLine = null;
		int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0;
		boolean batchExecuted = false;

		/*
		 * String InsertQuery =
		 * "INSERT INTO CBS_UCO_RAWDATA_TEMP(SOLID, TRAN_AMOUNT, PART_TRAN_TYPE, CARD_NUMBER, RRN, ACC_NUMBER, SYSDT, SYS_TIME, CMD, TRAN_TYPE, DEVICE_ID, TRAN_ID, TRAN_DATE, FEE, SRV_TAX, SRV_CHRG, VISAIND, CREATEDBY, FILEDATE, BANK_NAME) "
		 * +
		 * "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'),'UCO')"
		 * ;
		 */

		String InsertQuery = "INSERT INTO  UBI_CBS_RAWDATA (BUSINESS_DATE,CARD_NO,TRACE_NO,AC_NO,TRAN_DATE,ATM_ID,TYPE,AMOUNT,FEE,BR_CODE,ISS_SOL_ID,MCC,REMARKS,NETWORK,POSTED_DATE,GL_ACCOUNT,TR_NO,DCRS_REMARKS,FILEDATE,FILENAME,CREATEDDATE) VALUES(TO_DATE(?,'DD/MM/YYYY'),?,?,?,TO_DATE(?,'DD/MM/YYYY'),?,?,?,?,?,?,?,?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?,?,?,TO_DATE(?,'DD/MM/YYYY'))";
		String delQuery = "delete from CBS_UCO_RAWDATA_TEMP";
		try {

			String fileNameWithExt = file.getOriginalFilename();
			int lastDotIndex = fileNameWithExt.lastIndexOf(".");
			String fileNameWithoutExt = "";
			if (lastDotIndex > 0) {
				fileNameWithoutExt = fileNameWithExt.substring(0, lastDotIndex);
			}
			File folder = new File(OUTPUT_FOLDER);

			if (folder.exists()) {
				folder.delete();
			}
			folder.mkdir();

			File file1 = new File(folder, fileNameWithoutExt);

			try {
				if (file1.exists()) {
					file1.delete();
				}
				file1.createNewFile();
			} catch (Exception e) {
				System.out.println(e);
			}

			String newPath = OUTPUT_FOLDER + File.separator + fileNameWithoutExt;

			System.out.println("File to be write at path: " + newPath);

//			PgpHelper helper = new PgpHelper();

			InputStream keyIn = resource.getInputStream();
			FileOutputStream out = new FileOutputStream(newPath);

			// helper.decryptFile(file.getInputStream(), out, keyIn,
			// "Atmcell@#12345".toCharArray());
			// InputStream in = helper.decryptFile(file.getInputStream(), keyIn,
			// "Atmcell@#12345".toCharArray());
			PreparedStatement delpst = con.prepareStatement(delQuery);
			delpst.execute();

			File outFile = new File(newPath);

			System.out.println("File reading from Path: " + newPath);
			System.out.println("File reading : " + outFile);
			System.out.println("File in tomcat " + outFile);
			// BufferedReader br = new BufferedReader(new
			// InputStreamReader(file.getInputStream()));
			BufferedReader br = new BufferedReader(new FileReader(outFile));
			try {
				// BufferedReader br = new BufferedReader(new
				// InputStreamReader(in));

				PreparedStatement ps = con.prepareStatement(InsertQuery);

				while ((stLine = br.readLine()) != null) {

					if (stLine.startsWith("999  ,H") || stLine.startsWith("999  ,F")) {
						continue;
					}
					lineNumber++;
					batchExecuted = false;
					sr_no = 1;

					String[] splitData = stLine.split("\\,");

					for (int i = 0; i <= (splitData.length - 1); i++) {
						/*
						 * if(i == 5){
						 * if(splitData[i].trim().equalsIgnoreCase(setupBean.
						 * getFileDate())){ return false; } }
						 */
						if (i != 8 && i != 7 && i != 11 && i != 15) {
							// System.out.println("i "+i+" data
							// "+splitData[i-1]);
							if (i == 2) {
								String cardNumber = formatCardNumber(splitData[i].trim());
								ps.setString(sr_no++, cardNumber);
								ps.setString(sr_no++, splitData[i].trim());
								// ps.setString(sr_no++,
								// splitData[i].trim().substring(0,6)+"XXXXXX"+splitData[i].trim().substring(splitData[i].trim().length()
								// - 4));
								// ps.setString(sr_no++,
								// splitData[i].trim().substring(0,6)+"XXXXXX"+splitData[i].trim().substring(12,16));
							} else {
								ps.setString(sr_no++, splitData[i].trim());
							}
						}
					}

					// System.out.println(sr_no);
					ps.setString(sr_no++, setupBean.getCreatedBy());
					ps.setString(sr_no++, file.getOriginalFilename());
					ps.setString(sr_no++, setupBean.getFileDate());

					ps.addBatch();
					batchSize++;

					if (batchSize == 10000) {
						batchNumber++;
						System.out.println("Batch Executed is " + batchNumber);
						ps.executeBatch();
						batchSize = 0;
						batchExecuted = true;
					}

				}

				if (!batchExecuted) {
					batchNumber++;
					System.out.println("Batch Executed is " + batchNumber);
					ps.executeBatch();
				}

				br.close();
				ps.close();
				System.out.println("Reading data " + new Date().toString());
			} catch (IOException e) {
				System.out.println("exception for converting is " + e);
			}
			return true;

		} catch (Exception e) {
			System.out.println("Issue at line " + stLine);
			System.out.println("Exception in uploadISSData " + e);
			return false;
		}

	}

	private String formatCardNumber(String cardNumber) {
		// TODO Auto-generated method stub
		int totalLength = cardNumber.length();
		int firstSixDigitLength = Math.min(totalLength, 6);
		int lastFourDigitLength = Math.min(totalLength - 10, 4);

		String firstSixDigits = cardNumber.substring(0, firstSixDigitLength);
		String midX = "XXXXXX";
		String lastFourDigits = cardNumber.substring(totalLength - lastFourDigitLength);

		StringBuilder xx = new StringBuilder();
		for (int i = 0; i < totalLength - 10; i++) {
			xx.append("X");
		}
		String formatedCardNumber = firstSixDigits + xx + lastFourDigits;
		return formatedCardNumber;
	}

	public boolean uploadNewISSData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {

		String stLine = null;
		int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0, startLine = 1;
		boolean batchExecuted = false;

		String InsertQuery = "INSERT INTO CBS_UCO_RAWDATA(SOLID, TRAN_AMOUNT, PART_TRAN_TYPE, CARD_NUMBER, RRN, ACC_NUMBER, SYSDT, SYS_TIME, CMD, TRAN_TYPE, DEVICE_ID, TRAN_ID, TRAN_DATE, FEE, SRV_TAX, SRV_CHRG, VISAIND, CREATEDBY, FILEDATE, BANK_NAME) "
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'),'UCO')";

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			PreparedStatement ps = con.prepareStatement(InsertQuery);

			while ((stLine = br.readLine()) != null) {
				if (startLine >= 12) {
					if (!stLine.trim().contains("-----------------------------") && !stLine.trim().equals("")) {
						stLine = stLine.substring(10);

						lineNumber++;
						batchExecuted = false;
						sr_no = 1;

						String[] splitData = stLine.split("\\|");

						for (int i = 1; i <= splitData.length; i++) {
							if (i != 13) {
								// System.out.println("i "+i+" data
								// "+splitData[i-1]);
								ps.setString(sr_no++, splitData[i - 1]);
							}
						}

						// System.out.println(sr_no);
						ps.setString(sr_no++, setupBean.getCreatedBy());
						ps.setString(sr_no++, setupBean.getFileDate());

						ps.addBatch();
						batchSize++;

						if (batchSize == 10000) {
							batchNumber++;
							System.out.println("Batch Executed is " + batchNumber);
							ps.executeBatch();
							batchSize = 0;
							batchExecuted = true;
						}
					}
				} else {
					startLine++;
				}
			}

			if (!batchExecuted) {
				batchNumber++;
				System.out.println("Batch Executed is " + batchNumber);
				ps.executeBatch();
			}

			br.close();
			ps.close();
			System.out.println("Reading data " + new Date().toString());

			return true;

		} catch (Exception e) {
			System.out.println("Exception in uploadISSData " + e);
			return false;
		}

	}

	public HashMap<String, Object> uploadUbiCBSData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {
		// logger.info("uploadPOSSwitchData method Called
		// "+setupBean.getFILEDATE());

		HashMap<String, Object> output = new HashMap<String, Object>();
		String thisline = null;
		int lineNumber = 0, feesize = 1;
		int count = 0;
		int sr_no = 1, batchNumber = 0, batchSize = 10000;
		boolean batchExecuted = false;

		String InsertQuery = "INSERT INTO  ALL_CBS_RAWDATA (BUSINESS_DATE,CARD_NO,TRACE_NO,AC_NO,TRAN_DATE,ATM_ID,TYPE,AMOUNT,FEE,BR_CODE,ISS_SOL_ID,MCC,REMARKS,NETWORK,POSTED_DATE,GL_ACCOUNT,TR_NO,DCRS_REMARKS,FILEDATE,FILENAME,CREATEDDATE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'))";
		String update_query = "INSERT INTO SWITCH_DATA_VALIDATION(FILENAME,FILEDATE,COUNT,FILETYPE) VALUES(?,?,?,?)";

		try {
			con.setAutoCommit(false);
			int batchCount = 0;
			//if (icompareConfigService.chkFileupload2(setupBean)) {

				PreparedStatement ps = con.prepareStatement(InsertQuery);
				PreparedStatement updatps = con.prepareStatement(update_query);

				BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
				while ((thisline = br.readLine()) != null) {

					if (!thisline.toUpperCase().trim().contains("BUSINESS") && !thisline.trim().equals("")
							&& !thisline.trim().equals("H0000")) {
						// System.out.println("stLine "+ stLine);
						count++;
						batchExecuted = false;
						sr_no = 1;

						String[] splitData = thisline.split("\\|");

						// System.out.println("splitData "+ splitData[1]);

						for (int i = 1; i <= splitData.length; i++) {

							// System.out.println("i "+i+" data
							// "+splitData[i-1]);

							ps.setString(sr_no++, splitData[i - 1]);

						}

						// System.out.println(sr_no);

						ps.setString(18, "");
						ps.setString(19, setupBean.getFileDate());
						// System.out.println("getFileDate "+
						// setupBean.getFileDate()+"
						// "+setupBean.getCreatedBy());
						ps.setString(20, setupBean.getP_FILE_NAME());
						ps.setString(21, setupBean.getFileDate());

						ps.addBatch();
	

					}
					if (++batchCount % batchSize == 0) {

						batchNumber++;
						System.out.println("Batch Executed is " + batchNumber);
						ps.executeBatch();
						con.commit();
					}
				}
				ps.executeBatch();
				con.commit();
				if (count > 0) {
					updatps.setString(1, setupBean.getP_FILE_NAME());
					updatps.setString(2, setupBean.getFileDate());
					updatps.setString(3, String.valueOf(count));
					updatps.setString(4, "CBS");
					updatps.execute();
					con.commit();
					System.out.println("Executed Batch Completed" + "count " + count);

				}


		
				output.put("result", true);
				output.put("msg", "File Uploaded and Record count is " + count);
				return output;
			/*} else {
				System.out.println("FILE ALREADY UPLOADED" + "count " + lineNumber);

				output.put("result", true);
				output.put("msg", setupBean.getP_FILE_NAME() + " File Uploaded and Record count is ");
				return output;
			}*/
		} catch (Exception e) {
			System.out.println("Exception in ReadUCOATMSwitchData " + e);
			output.put("result", false);
			output.put("msg", "Issue at Line Number " + count);
			return output;
		}

	}
	public HashMap<String, Object> uploadSUSPECTData(NFSSettlementBean setupBean, Connection con, MultipartFile file) {
		// logger.info("uploadPOSSwitchData method Called
		// "+setupBean.getFILEDATE());

		HashMap<String, Object> output = new HashMap<String, Object>();
		String thisline = null;
		int lineNumber = 0, feesize = 1;
		int count = 1;
		int sr_no = 1, batchNumber = 0, batchSize = 1000;
		boolean batchExecuted = false;

		String InsertQuery = "INSERT INTO  SUSPECT_RAWDATA (RESP_CODE,ATM_ID,CARD_NO,ACC_NO,TXN_DATE,TXN_TIME,I_A_CATEGORY,RRN,TXN_AMT,FILENAME,FILEDATE) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		String update_query = "INSERT INTO SWITCH_DATA_VALIDATION(FILENAME,FILEDATE,COUNT,FILETYPE) VALUES(?,?,?,?)";
    
		try {
			con.setAutoCommit(false);
			int batchCount = 0;

			//if (icompareConfigService.chkFileupload2(setupBean)) {

				PreparedStatement ps = con.prepareStatement(InsertQuery);
				/*
				 * PreparedStatement updatps = con.prepareStatement(update_query);
				 */
				BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
				while ((thisline = br.readLine()) != null) {

					if (!thisline.trim().equals("")) {
						// System.out.println("stLine "+ stLine);
						count++;
						batchExecuted = false;
						sr_no = 1;

						String[] splitData = thisline.split("\\|");

						// System.out.println("splitData "+ splitData[1]);

						for (int i = 1; i <=splitData.length; i++) {

							// System.out.println("i "+i+" data
							// "+splitData[i-1]);

							ps.setString(sr_no++, splitData[i-1]);

						}

						// System.out.println(sr_no);

				
						// "+setupBean.getCreatedBy());
						ps.setString(10, setupBean.getFileName());
						ps.setString(11, setupBean.getDatepicker());
		

						ps.addBatch();
				

			
					}
					if (++batchCount % batchSize == 0) {

						batchNumber++;
						System.out.println("Batch Executed is " + batchNumber);
						ps.executeBatch();
						con.commit();
					}
				}
				ps.executeBatch();
				con.commit();
				/*
				 * if (count > 0) { updatps.setString(1, setupBean.getP_FILE_NAME());
				 * updatps.setString(2, setupBean.getFileDate()); updatps.setString(3,
				 * String.valueOf(count)); updatps.setString(4, "VISA"); updatps.execute();
				 * con.commit();
				 * 
				 * System.out.println("Executed Batch Completed" + "count " + lineNumber);
				 * 
				 * }
				 */


				output.put("result", true);
				output.put("msg", "File Uploaded and Record count is " + count);
				return output;
			/*} else {
				System.out.println("FILE ALREADY UPLOADED" + "count " + lineNumber);

				output.put("result", true);
				output.put("msg", setupBean.getP_FILE_NAME() + " File Uploaded and Record count is ");
				return output;
			}*/
		} catch (Exception e) {
			System.out.println("Exception in ReadUCOATMSwitchData " + e);
			output.put("result", false);
			output.put("msg", "Issue at Line Number " + count);
			return output;
		}

	}


	public boolean uploadPBGBData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {
		System.out.println("uploadPBGBData method called ");
		String stLine = null;
		int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0;
		boolean batchExecuted = false;

		String InsertQuery = "INSERT INTO  UBI_CBS_RAWDATA (BUSINESS_DATE,CARD_NO,TRACE_NO,AC_NO,TRAN_DATE,ATM_ID,TYPE,AMOUNT,FEE,BR_CODE,ISS_SOL_ID,MCC,REMARKS,NETWORK,POSTED_DATE,GL_ACCOUNT,TR_NO,DCRS_REMARKS,FILEDATE,FILENAME,CREATEDDATE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'),?,to_date(?,'dd/mm/yyyy'))";

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			PreparedStatement ps = con.prepareStatement(InsertQuery);

			while ((stLine = br.readLine()) != null) {

				if (!stLine.toUpperCase().trim().contains("BUSINESS") && !stLine.trim().equals("")
						&& !stLine.trim().equals("H0000")) {
					// System.out.println("stLine "+ stLine);
					lineNumber++;
					batchExecuted = false;
					sr_no = 1;

					String[] splitData = stLine.split("\\|");

					// System.out.println("splitData "+ splitData[1]);

					for (int i = 1; i <= splitData.length; i++) {

						// System.out.println("i "+i+" data "+splitData[i-1]);

						ps.setString(sr_no++, splitData[i - 1]);

					}

					// System.out.println(sr_no);

					ps.setString(18, "");
					ps.setString(19, setupBean.getFileDate());
					// System.out.println("getFileDate "+
					// setupBean.getFileDate()+" "+setupBean.getCreatedBy());
					ps.setString(20, setupBean.getP_FILE_NAME());
					ps.setString(21, setupBean.getFileDate());

					ps.addBatch();
					batchSize++;

					if (batchSize == 20000) {
						batchNumber++;
						System.out.println("Batch Executed is " + batchNumber);
						ps.executeBatch();
						batchSize = 0;
						batchExecuted = true;
					}
				}

			}
			if (!batchExecuted) {
				batchNumber++;
				System.out.println("Batch Executed is " + batchNumber);
				ps.executeBatch();
			}

			br.close();
			ps.close();
			System.out.println("Reading data " + new Date().toString());

			return true;

		} catch (Exception e) {
			System.out.println("Exception in uploadISSData " + e);
			return false;
		}

	}

	// Reading Of CBS file of ICCW

	public boolean iccwuploadcbsData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {

		String stLine = null;
		int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0;
		boolean batchExecuted = false;

		/*
		 * String InsertQuery =
		 * " INSERT INTO ICCW_CBS_DATA (SOL_ID ,AMOUNT,DRCR ,DR_ACC_NUM,TRACE_NUM ,CR_ACC_NUM,TXN_DATE,TXN_TIME,TYPE_1,TYPE_2,RRN,TXN_ID,"
		 * +
		 * "VALUE_DT ,CREATED_BY , FILEDATE  ) VALUES  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'))"
		 * ;
		 */

		String InsertQuery = " INSERT INTO ICCW_CBS_DATA_CUB (JOURNAL_NO, FROMACC, FROMACCNAME, FROMACCBR, TOACC, TOACCNAME, TOACCBR, TXNDATE, TXNAMOUNT, ATT1, ATT2, ATT3, ACC_TYPE, CREATED_BY , FILEDATE)"
				+ " VALUES  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'))";
		// String delQuery = "delete from CBS_UCO_RAWDATA_TEMP";
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			PreparedStatement ps = con.prepareStatement(InsertQuery);

			while ((stLine = br.readLine()) != null) {
				lineNumber++;
				batchExecuted = false;
				sr_no = 1;

				String[] splitData = stLine.split("\\|");

				if ((stLine.startsWith("JOURNAL_NO")) || (stLine.isEmpty()) || stLine.startsWith("Elapsed:")
						|| stLine.startsWith("FT")) {
					continue;
				}

				for (int i = 0; i <= splitData.length - 1; i++) {
					ps.setString(sr_no++, splitData[i]);
				}

				ps.setString(sr_no++, setupBean.getCreatedBy());
				ps.setString(sr_no++, setupBean.getFileDate());

				ps.addBatch();
				batchSize++;

				if (batchSize == 10000) {
					batchNumber++;
					System.out.println("Batch Executed is " + batchNumber);
					ps.executeBatch();
					batchSize = 0;
					batchExecuted = true;
				}

			}

			if (!batchExecuted) {
				batchNumber++;
				System.out.println("Batch Executed is " + batchNumber);
				ps.executeBatch();
			}

			br.close();
			ps.close();
			System.out.println("Reading data " + new Date().toString());

			return true;

		} catch (Exception e) {
			System.out.println("Issue at line " + stLine);
			System.out.println("Exception in uploadISSData " + e);
			return false;
		}

	}

	public boolean iccwuploadRevcbsData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {

		String stLine = null;
		int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0;
		boolean batchExecuted = false;

		/*
		 * String InsertQuery =
		 * " INSERT INTO ICCW_CBS_DATA (SOL_ID ,AMOUNT,DRCR ,DR_ACC_NUM,TRACE_NUM ,CR_ACC_NUM,TXN_DATE,TXN_TIME,TYPE_1,TYPE_2,RRN,TXN_ID,"
		 * +
		 * "VALUE_DT ,CREATED_BY , FILEDATE  ) VALUES  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'))"
		 * ;
		 */

		String InsertQuery = " INSERT INTO ICCW_CBS_DATA_CUB_REV (JOURNAL_NO, REV_JOURNAL, FROMACC, TOACC ,TXNAMOUNT, TXNDATE,  ACC_TYPE, CREATED_BY , FILEDATE)"
				+ " VALUES  (?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'))";
		// String delQuery = "delete from CBS_UCO_RAWDATA_TEMP";
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			PreparedStatement ps = con.prepareStatement(InsertQuery);

			while ((stLine = br.readLine()) != null) {
				lineNumber++;
				batchExecuted = false;
				sr_no = 1;

				String[] splitData = stLine.split("\\|");

				if ((stLine.startsWith("JOURNAL_NO")) || (stLine.isEmpty()) || stLine.startsWith("Elapsed:")
						|| stLine.startsWith("FT")) {
					continue;
				}

				for (int i = 0; i <= splitData.length - 1; i++) {
					ps.setString(sr_no++, splitData[i]);
				}

				ps.setString(sr_no++, setupBean.getCreatedBy());
				ps.setString(sr_no++, setupBean.getFileDate());

				ps.addBatch();
				batchSize++;

				if (batchSize == 10000) {
					batchNumber++;
					System.out.println("Batch Executed is " + batchNumber);
					ps.executeBatch();
					batchSize = 0;
					batchExecuted = true;
				}

			}

			if (!batchExecuted) {
				batchNumber++;
				System.out.println("Batch Executed is " + batchNumber);
				ps.executeBatch();
			}

			br.close();
			ps.close();
			System.out.println("Reading data " + new Date().toString());

			return true;

		} catch (Exception e) {
			System.out.println("Issue at line " + stLine);
			System.out.println("Exception in uploadISSData " + e);
			return false;
		}

	}

	public boolean uploadSwitchData(CompareSetupBean setupBean, Connection con, MultipartFile file,
			FileSourceBean sourceBean) {

		String stLine = null;
		int lineNumber = 0, sr_no = 1, batchNumber = 0, batchSize = 0;
		boolean batchExecuted = false;

		/*
		 * String InsertQuery =
		 * "INSERT INTO CBS_UCO_RAWDATA_TEMP(SOLID, TRAN_AMOUNT, PART_TRAN_TYPE, CARD_NUMBER, RRN, ACC_NUMBER, SYSDT, SYS_TIME, CMD, TRAN_TYPE, DEVICE_ID, TRAN_ID, TRAN_DATE, FEE, SRV_TAX, SRV_CHRG, VISAIND, CREATEDBY, FILEDATE, BANK_NAME) "
		 * +
		 * "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'),'UCO')"
		 * ;
		 */

		String InsertQuery = "INSERT INTO CBS_RUPAY_RAWDATA(REMARKS, AMOUNT, TRAN_DATE, TRAN_TIME, E, PARTICULARALS, REF_NO, TRAN_ID, CREATEDBY, FILEDATE) "
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'))";

		String delQuery = "delete from CBS_UCO_RAWDATA_TEMP";
		try {
			PreparedStatement delpst = con.prepareStatement(delQuery);
			delpst.execute();

			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			PreparedStatement ps = con.prepareStatement(InsertQuery);

			while ((stLine = br.readLine()) != null) {

				if (stLine.startsWith("999  ,H") || stLine.startsWith("999  ,F")) {
					continue;
				}
				lineNumber++;
				batchExecuted = false;
				sr_no = 1;

				String[] splitData = stLine.split("\\,");

				for (int i = 0; i <= (splitData.length - 1); i++) {
					if (i != 0 && i != 1 && i != 3 && i != 8 && i != 7 && i != 11 && i != 13 && i != 14 && i != 15) {
						// System.out.println("i "+i+" data "+splitData[i-1]);
						ps.setString(sr_no++, splitData[i].trim());
					}
				}

				// System.out.println(sr_no);
				ps.setString(sr_no++, setupBean.getCreatedBy());
				ps.setString(sr_no++, setupBean.getFileDate());

				ps.addBatch();
				batchSize++;

				if (batchSize == 10000) {
					batchNumber++;
					System.out.println("Batch Executed is " + batchNumber);
					ps.executeBatch();
					batchSize = 0;
					batchExecuted = true;
				}

			}

			if (!batchExecuted) {
				batchNumber++;
				System.out.println("Batch Executed is " + batchNumber);
				ps.executeBatch();
			}

			br.close();
			ps.close();
			System.out.println("Reading data " + new Date().toString());

			return true;

		} catch (Exception e) {
			System.out.println("Issue at line " + stLine);
			System.out.println("Exception in uploadISSData " + e);
			return false;
		}

	}
}
