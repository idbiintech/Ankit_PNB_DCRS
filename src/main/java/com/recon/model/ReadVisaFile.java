package com.recon.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.recon.util.OracleConn;

public class ReadVisaFile {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private static final Logger logger = Logger.getLogger(ReadVisaFile.class);

	public static final String OUTPUT_FOLDER = System.getProperty("catalina.home") + File.separator + "OUTPUT_FOLDER";

	public boolean readData(CompareSetupBean setupBean, Connection conn,
			MultipartFile file, FileSourceBean sourceBean) {
		logger.info("***** ReadVisaFile.readData Start ****");
			InputStream fis = null;

			String stTable_Name = "", dummyrrn="", DATE2 ="";
			String Mcuntrycode = "IN";						String MName ="UBI TT",location="", MCC="6011";
			ArrayList<String> Amount_translation = new ArrayList<String>();
			Resource resource = new ClassPathResource("/resources/switchpr.asc");
			
			TCRFile tcrFileObj = new TCRFile();
			
		
			BufferedReader br = null ;
			String thisLine = null;  
			int lineNumber=0;
			
			try
			{
				
				String fileNameWithExt =  file.getOriginalFilename();
				int lastDotIndex = fileNameWithExt.lastIndexOf(".");
				String fileNameWithoutExt = "";
				if(lastDotIndex > 0){
					fileNameWithoutExt = fileNameWithExt.substring(0, lastDotIndex);
				}
				File folder = new File(OUTPUT_FOLDER);
				
				if(folder.exists()){
					folder.delete();
				}
				folder.mkdir();
				
				File file1 = new File(folder, fileNameWithoutExt);
				
				try{
					if(file1.exists()){
						file1.delete();
					}
					file1.createNewFile();
				}catch(Exception e){
					System.out.println(e);
				}
				
				String newPath = OUTPUT_FOLDER + File.separator + fileNameWithoutExt;
				
				System.out.println("File to be write at path: "+newPath);
			//	PgpHelper helper = new PgpHelper();
				
			//	InputStream keyIn = resource.getInputStream();
				//FileOutputStream out = new FileOutputStream(newPath);
				
				//helper.decryptFile(file.getInputStream(), out, keyIn, "Atmcell@#12345".toCharArray());
				//InputStream in = helper.decryptFileWithInputStram(file.getInputStream(), out, keyIn, "Atmcell@#12345".toCharArray());  
				File outFile = new File(newPath);
				
				System.out.println("File reading from Path: "+newPath);
				System.out.println("File reading : "+outFile);
				//PreparedStatement delpst = con.prepareStatement(delQuery);
				//delpst.execute();

		//	BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
				// br = new BufferedReader(new FileReader(outFile));
				
				
				br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			
				logger.info("Reading data "+new Date().toString());
				//CHECK WHETHER TABLE IS ALREADY PRESENT 
				String CHECK_TABLE="",update_TABLE="";
			/*	if(setupBean.getStSubCategory().equals("-") && setupBean.getCategory().equals("WCC")){
					CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'WCC_INPUT_FILE'";
				}else{*/
					CHECK_TABLE = "SELECT count(*) FROM tab WHERE tname  = 'VISA_INPUT_FILE'";
			
				logger.info("CHECK_TABLE=="+CHECK_TABLE);
				PreparedStatement pstmt1 = conn.prepareStatement(CHECK_TABLE);
				update_TABLE = "update visa_acq_raWDATA set MERCHANT_NAME=?,MERCHANT_COUNTRY_CODE=?,MERCHANT_CATEGORY_CODE=?, where DCRS_SEQ_NO=?";
				
				logger.info("update_TABLE=="+update_TABLE);
				PreparedStatement pstmt4 = conn.prepareStatement(update_TABLE);
				ResultSet rset1 = pstmt1.executeQuery();
				int isPresent = 0;
				
				if(rset1.next())
				{
					//logger.info("table is present ? "+rset1.getInt(1));
					isPresent = rset1.getInt(1);
				}
				
				if(isPresent == 0)
				{

					//IF NOT THEN CREATE IT
					//CREATE VISA INPUT TABLE
					String CREATE_QUERY = "CREATE TABLE VISA_INPUT_FILE (TC VARCHAR2(100 BYTE), TCR_CODE VARCHAR2(100 BYTE)," +
							" STRING varchar(1000), DCRS_SEQ_NO varchar(100), FILEDATE DATE)";
				
					logger.info("CREATE_QUERY=="+CREATE_QUERY);
					PreparedStatement pstmt = conn.prepareStatement(CREATE_QUERY);
					//pstmt.executeQuery();

				}
				
				//CREATE RAW TABLE FOR VISA
					//GET TABLE NAME FROM MAIN FILESOURCES
				/*
				 * String GET_RAW_TABLE = ""; if(setupBean.getStSubCategory().equals("-") &&
				 * setupBean.getCategory().equals("WCC")){ GET_RAW_TABLE =
				 * "SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILENAME = 'VISA' AND FILE_CATEGORY = 'VISA' AND FILE_SUBCATEGORY = 'ISS DOM POS'"
				 * ; }else{ GET_RAW_TABLE =
				 * "SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILENAME = 'VISA' AND FILE_CATEGORY = 'VISA' AND FILE_SUBCATEGORY = 'ISS DOM POS'"
				 * ; } logger.info("GET_RAW_TABLE=="+GET_RAW_TABLE); pstmt1 =
				 * conn.prepareStatement(GET_RAW_TABLE); rset1 = pstmt1.executeQuery();
				 * 
				 * if(rset1.next()) {
				 * //logger.info("TABLE NAME IS "+rset1.getString("TABLENAME")); stTable_Name =
				 * rset1.getString("TABLENAME"); }
				 */
				String stline=br.readLine();
				
			
				String DATA_INSERT_05 = "INSERT INTO VISA_VISA_RAWDATA (TC,TCR_CODE,DCRS_SEQ_NO,FILEDATE,PART_ID,CARD_NUMBER," +
						"Floor_Limit_indi,ARN,Acquirer_Busi_ID,Purchase_Date,Destination_Amount,Destination_Curr_Code,SOURCE_AMOUNT,Source_Curr_Code,Merchant_Name,Merchant_City," +
						"Merchant_Country_Code,Merchant_Category_Code,Merchant_ZIP_Code,Usage_Code,Reason_Code,Settlement_Flag,Auth_Chara_Ind,AUTHORIZATION_CODE," +
						"POS_Terminal_Capability,Cardholder_ID_Method," +
						"Collection_Only_Flag,POS_Entry_Mode,Central_Process_Date,Reimbursement_Attr,FPAN,TRAN_ID,FILENAME) " +
				" VALUES(?,?,?,str_to_date(?,'%d-%m-%y'),?," +
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
			//	logger.info("DATA_INSERT_05=="+DATA_INSERT_05);
				
				String DATA_INSERT_10 = "INSERT INTO VISA_VISA_RAWDATA (TC,TCR_CODE,DCRS_SEQ_NO,FILEDATE,PART_ID,Destination_BIN,Source_BIN,Reason_Code,Country_Code,Event_Date," +
						"CARD_NUMBER,Destination_Amount,Destination_Curr_Code,SOURCE_AMOUNT,Source_Curr_Code,Message_Text,Settlement_Flag,Transac_Identifier,Central_Process_Date,Reimbursement_Attr,FPAN,FILENAME) " +
				" VALUES(?,?,?,str_to_date(?,'%d-%m-%y'),?," +
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
				//logger.info("DATA_INSERT_10=="+DATA_INSERT_10);
				
				PreparedStatement read_stmt_05 =conn.prepareStatement(DATA_INSERT_05);
				
				PreparedStatement read_stmt_10 = conn.prepareStatement(DATA_INSERT_10);
				
				String INSERT_33 = "INSERT INTO VISA_ACQ_RAWDATA (TC,TCR_CODE,CARD_NUMBER,SOURCE_AMOUNT,TRACE,REFERENCE_NUMBER,RESPONSE_CODE,DCRS_SEQ_NO,FILEDATE,PART_ID,REQ_MSGTYPE,FPAN,SOURCE_CURR_CODE,DESTINATION_CURR_CODE,DESTINATION_AMOUNT,TRANSAC_IDENTIFIER,MERCHANT_COUNTRY_CODE,MERCHANT_NAME,MERCHANT_CATEGORY_CODE,PURCHASE_DATE)" +
						" VALUES(?,?,?,?,?,?,?,?,str_to_date(?,'%d-%m-%y'),?,?,?,?,?,?,?,?,?,?,?)";
				
				//logger.info("INSERT_33=="+INSERT_33);
				PreparedStatement pstmt33 = conn.prepareStatement(INSERT_33);
				
				int count = 0,count1=0;
				int batch = 1 ;
				float amt = 0.00f;
				Integer updatedseqcheckInteger=null;
				String INSERT_QUERY="";
			/*	if(setupBean.getStSubCategory().equals("-")){
					INSERT_QUERY = "INSERT INTO WCC_INPUT_FILE (TC, TCR_CODE, STRING,DCRS_SEQ_NO,FILEDATE) VALUES(?,?,?,?,TO_DATE(?,'DD/MM/YYYY'))";
				}else{*/
					INSERT_QUERY = "INSERT INTO VISA_INPUT_FILE (TC, TCR_CODE, STRING,DCRS_SEQ_NO,FILEDATE) VALUES(?,?,?,?,str_to_date(?,'%d-%m-%y'))";
				
				logger.info("INSERT_QUERY=="+INSERT_QUERY);
				pstmt1 = conn.prepareStatement(INSERT_QUERY);
				
				/*
				 * String Seq_num = "select 'VISA'||visa_seq.nextval AS SEQ from dual";
				 * PreparedStatement pstmt2 = conn.prepareStatement(Seq_num); String seq = "";
				 */
				
				//GETTING ALL CHARACTER , DIGIT AND SIGN FROM TABLE
	String seq="";
				List<String> stRawData = new ArrayList<String>(); 
					while(stline != null)
					{
						count1++;
						if(count1==6) {
							
							location =stline.substring(75,114);
						}
						stRawData.clear();
						boolean containsVISATable = false;
						//logger.info("count is "+count);
						String check_TCR = "";
						String ACC_NO = "";
						String SOURCE_AMT = "";
						String DATE = "";
						String AUTH_CODE = "";
						String TRACE = "";
				
						String RESPONSE_CODE = "";
						String REFERENCE_NUMBER = "";
						String CARD_NUMBER = "";
						//ADDED BY INT8624 FOR ADDING FULL PAN
						String FPAN = "";
						String REQ_MSG_TYPE = "";
						String TRAN_ID = "";
						String CutCODE ="";
					
						String transacITe ="";
						//String TRAN_AMOUNT = "";
						
						List<String> Data_Elements = new ArrayList<String>();
						String TC = stline.substring(0, 2);
						String TCR_Code = stline.substring(2,4);
					//	logger.info("TCR_Code "+ TCR_Code);
						
						//generating SEQ number
						
						
						
						//INSERT THE DATA IN INPUT TABLE
						
						if(TC.equals("05") || TC.equals("06") || TC.equals("07")|| TC.equals("25") || TC.equals("27"))
						{
							if(TCR_Code.equals("00")||TCR_Code.equals("20"))
							{
								Data_Elements = tcrFileObj.TCR050Format();
							}
							//Added by int8624 on 28 May
							else if(TCR_Code.equalsIgnoreCase("05"))
							{
								Data_Elements = tcrFileObj.TCR0505Format();
							}
						}
						else if(TC.equals("10"))
						{
							if(TCR_Code.equals("00"))
							{
								Data_Elements = tcrFileObj.TCR10FeeCollectionFormat();
							}
						}
						else if(TC.equals("20"))
						{
							if(TCR_Code.equals("00"))
							{
								Data_Elements = tcrFileObj.TCR20FundDisbursement();
							}
						}
						else if(TC.equals("33"))//VISA ACQUIRER TRANSACTIONS
						{
							//REMOVE THE HEADER FROM THIS STRING AND THEN STRING
							String stLine = stline.substring(34 , stline.length());
						//	logger.info("stLine 34 "+ stLine +" stline.length() "+ stline.length());
							//ADDED BY INT5779 FOR VISA TABLE ISSUE
							
							if(stLine.contains("VISA TABLE"))
							{
								containsVISATable = true;
							}
							else{
								//logger.info("NOW STRING IS "+stLine);
								check_TCR = stLine.substring(3, 6);
								//logger.info("check TCR "+check_TCR);
								if(check_TCR.equals("200"))
								{
									Data_Elements = tcrFileObj.V22200();
								}else 	if(check_TCR.equals("210"))
								{
									Data_Elements = tcrFileObj.V22210();
								}else 	if(check_TCR.equals("220"))
								{
									Data_Elements = tcrFileObj.V22220();
								}
								
							}
						}
						if(!containsVISATable)
						{
							//READ CARD NUMBER , AMOUNT , AND AUTH CODE FROM INPUT STRING AND INSERT IT IN NEW RAW TABLE
							
							
							
							
							
							for(int i= 0 ;i<Data_Elements.size(); i++)
							{
								if(TC.equals("05")||TC.equals("06")||TC.equals("07")||TC.equals("25")||TC.equals("27"))
								{
									if(TCR_Code.equals("00")||TCR_Code.equals("20"))
									{

										String[] DE =  Data_Elements.get(i).split("\\|");
										String stData = "";

										//if(((DE[0].trim()).equals("Account Number")) || ((DE[0].trim()).equals("Source Amount"))|| ((DE[0].trim()).equals("Authorization Code")));
										/*if(DE[0].trim().equalsIgnoreCase("ACCOUNT NUMBER") ||
											DE[0].trim().equalsIgnoreCase("SOURCE AMOUNT") ||
											DE[0].trim().equalsIgnoreCase("Authorization Code"))
									{*/
										//logger.info("DE[0] "+DE[0]);
										//	logger.info("DE LENGTH IS "+DE.length);
										if(DE.length == 3)
										{
											int ststart_Pos = Integer.parseInt(DE[1].trim());
											int stEnd_pos = Integer.parseInt(DE[2].trim());
											/*logger.info("start positon "+ststart_Pos);
									logger.info("End Position "+stEnd_pos);
									logger.info("stHeader : "+DE[0]+" Value : "+stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));*/

											if(DE[0].equalsIgnoreCase("ACCOUNT NUMBER"))
											{
												//logger.info(DE[0]);
												stData = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
												stRawData.add(stData);
											}
											else if(DE[0].equalsIgnoreCase("SOURCE AMOUNT") || DE[0].equalsIgnoreCase("DESTINATION AMOUNT")) {
												//logger.info(DE[0]);
												SOURCE_AMT = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
												stData = String.valueOf((Float.parseFloat(SOURCE_AMT)/100));
												stRawData.add(stData);
												//logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
											}
											else //if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
												if(DE[0].equalsIgnoreCase("Floor Limit Indicator") ||
														DE[0].equalsIgnoreCase("Acquirer Reference Number") ||
														DE[0].equalsIgnoreCase("Acquirer's Business ID") ||
														DE[0].equalsIgnoreCase("Purchase Date") ||
														//DE[0].equalsIgnoreCase("Destination Amount") || 
														DE[0].equalsIgnoreCase("Destination Currency Code") ||
														DE[0].equalsIgnoreCase("Source Currency Code") ||
														DE[0].equalsIgnoreCase("Merchant Name") ||
														DE[0].equalsIgnoreCase("Merchant City") ||
														DE[0].equalsIgnoreCase("Merchant Country Code") ||
														DE[0].equalsIgnoreCase("Merchant Category Code") ||
														DE[0].equalsIgnoreCase("Merchant ZIP Code") ||
														DE[0].equalsIgnoreCase("Usage Code") ||
														DE[0].equalsIgnoreCase("Reason Code") ||
														DE[0].equalsIgnoreCase("Settlement Flag") ||
														DE[0].equalsIgnoreCase("Authorization Characteristics Indicator") ||
														DE[0].equalsIgnoreCase("POS Terminal Capability") ||
														DE[0].equalsIgnoreCase("Cardholder ID Method") ||
														DE[0].equalsIgnoreCase("Collection-Only Flag") ||
														DE[0].equalsIgnoreCase("POS Entry Mode") ||
														DE[0].equalsIgnoreCase("Central Processing Date") ||
														DE[0].equalsIgnoreCase("Reimbursement Attribute") ||
														DE[0].equalsIgnoreCase("Transaction Identifier")||
														DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))


												{
													//logger.info(DE[0]);
													//AUTH_CODE = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
													stData = stline.substring((ststart_Pos-1), stEnd_pos).trim();
													stRawData.add(stData);
													if(DE[0].equalsIgnoreCase("Collection-Only Flag"))
													{
														//logger.info("Collection-Only Flag "+stData);
													}
												}



										}
										else if(DE.length == 2)
										{
											int ststart_pos =  Integer.parseInt(DE[1].trim());
											if(DE[0].equalsIgnoreCase("ACCOUNT NUMBER"))
											{
												//logger.info(DE[0]);
												stData = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
												stRawData.add(stData);
											}
											else if(DE[0].equalsIgnoreCase("SOURCE AMOUNT") || DE[0].equalsIgnoreCase("DESTINATION AMOUNT")) {
												try
												{
													//	logger.info(DE[0]);
													SOURCE_AMT = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
													stData = String.valueOf((Float.parseFloat(SOURCE_AMT)/100));
													stRawData.add(stData);
												}
												catch(Exception e)
												{
													logger.info("Exception in visa issuer on line "+stline);
												}
											}
											else //if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
												if(DE[0].equalsIgnoreCase("Floor Limit Indicator") ||
														DE[0].equalsIgnoreCase("Acquirer Reference Number") ||
														DE[0].equalsIgnoreCase("Acquirer's Business ID") ||
														DE[0].equalsIgnoreCase("Purchase Date") ||
														//DE[0].equalsIgnoreCase("Destination Amount") || 
														DE[0].equalsIgnoreCase("Destination Currency Code") ||
														DE[0].equalsIgnoreCase("Source Currency Code") ||
														DE[0].equalsIgnoreCase("Merchant Name") ||
														DE[0].equalsIgnoreCase("Merchant City") ||
														DE[0].equalsIgnoreCase("Merchant Country Code") ||
														DE[0].equalsIgnoreCase("Merchant Category Code") ||
														DE[0].equalsIgnoreCase("Merchant ZIP Code") ||
														DE[0].equalsIgnoreCase("Usage Code") ||
														DE[0].equalsIgnoreCase("Reason Code") ||
														DE[0].equalsIgnoreCase("Settlement Flag") ||
														DE[0].equalsIgnoreCase("Authorization Characteristics Indicator") ||
														DE[0].equalsIgnoreCase("POS Terminal Capability") ||
														DE[0].equalsIgnoreCase("Cardholder ID Method") ||
														DE[0].equalsIgnoreCase("Collection-Only Flag") ||
														DE[0].equalsIgnoreCase("POS Entry Mode") ||
														DE[0].equalsIgnoreCase("Central Processing Date") ||
														DE[0].equalsIgnoreCase("Reimbursement Attribute") ||
														DE[0].equalsIgnoreCase("Transaction Identifier")||
														DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))

												{
													//logger.info(DE[0]);
													//AUTH_CODE = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
													stData = stline.substring((ststart_pos-1), (ststart_pos)).trim();
													stRawData.add(stData);
													if(DE[0].equalsIgnoreCase("Collection-Only Flag"))
													{
														//logger.info("Collection-Only Flag "+stData);
													}
												}

										}
										//}
										/*if(!stData.equals(""))	
									stRawData.add(stData);	*/
									}
									//ADD TRAN ID ON 28 MAY BY INT 8624
									else if(TCR_Code.equalsIgnoreCase("05"))
									{
										String[] DE =  Data_Elements.get(i).split("\\|");
										//TRAN_ID = stline.substring(4, 19);
										TRAN_ID = stline.substring(Integer.parseInt(DE[1]), Integer.parseInt(DE[2]));
									//	logger.info("Tran_id "+TRAN_ID);
									}
								}
								else if(TC.equals("10")||TC.equals("20"))
								{

									if(TCR_Code.equals("00"))
									{

										String[] DE =  Data_Elements.get(i).split("\\|");
										String stData = "";

										//if(((DE[0].trim()).equals("Account Number")) || ((DE[0].trim()).equals("Source Amount"))|| ((DE[0].trim()).equals("Authorization Code")));
										/*if(DE[0].trim().equalsIgnoreCase("ACCOUNT NUMBER") ||
											DE[0].trim().equalsIgnoreCase("SOURCE AMOUNT") ||
											DE[0].trim().equalsIgnoreCase("Authorization Code"))
									{*/
										//logger.info("DE[0] "+DE[0]);
										//	logger.info("DE LENGTH IS "+DE.length);
										if(DE.length == 3)
										{
											int ststart_Pos = Integer.parseInt(DE[1].trim());
											int stEnd_pos = Integer.parseInt(DE[2].trim());
											/*logger.info("start positon "+ststart_Pos);
									logger.info("End Position "+stEnd_pos);
									logger.info("stHeader : "+DE[0]+" Value : "+stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));*/

											if(DE[0].equalsIgnoreCase("ACCOUNT NUMBER"))
											{
												stData = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
												stRawData.add(stData);
											}
											else if(DE[0].equalsIgnoreCase("SOURCE AMOUNT") || DE[0].equalsIgnoreCase("DESTINATION AMOUNT")) {

												SOURCE_AMT = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
												stData = String.valueOf((Float.parseFloat(SOURCE_AMT)/100));
												stRawData.add(stData);
												//logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
											}
											else //if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
												if( 	DE[0].equalsIgnoreCase("Destination BIN") ||
														DE[0].equalsIgnoreCase("Source BIN") ||
														DE[0].equalsIgnoreCase("Reason Code") ||
														DE[0].equalsIgnoreCase("Country Code") ||
														DE[0].equalsIgnoreCase("Event Date (MMDD)") ||
														DE[0].equalsIgnoreCase("Account Number") ||
														//	DE[0].equalsIgnoreCase("Destination Amount") ||
														DE[0].equalsIgnoreCase("Destination Currency Code") ||
														DE[0].equalsIgnoreCase("Source Amount")||
														DE[0].equalsIgnoreCase("Source Currency Code") ||
														DE[0].equalsIgnoreCase("Settlement Flag") ||
														DE[0].equalsIgnoreCase("Transaction Identifier") ||
														DE[0].equalsIgnoreCase("Central Processing Date (YDDD)") ||
														DE[0].equalsIgnoreCase("Reimbursement Attribute") ||
														DE[0].equalsIgnoreCase("Message Text"))


												{
													//AUTH_CODE = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
													stData = stline.substring((ststart_Pos-1), stEnd_pos).trim();
													stRawData.add(stData);
												}



										}
										else if(DE.length == 2)
										{
											int ststart_pos =  Integer.parseInt(DE[1].trim());
											if(DE[0].equalsIgnoreCase("ACCOUNT NUMBER"))
											{
												stData = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
												stRawData.add(stData);
											}
											else if(DE[0].equalsIgnoreCase("SOURCE AMOUNT") || DE[0].equalsIgnoreCase("DESTINATION AMOUNT")) {
												try
												{
													SOURCE_AMT = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
													stData = String.valueOf((Float.parseFloat(SOURCE_AMT)/100));
													stRawData.add(stData);
												}
												catch(Exception e)
												{
													logger.info("Exception in visa issuer on line "+stline);
												}
											}
											else //if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
												if(	DE[0].equalsIgnoreCase("Destination BIN") ||
														DE[0].equalsIgnoreCase("Source BIN") ||
														DE[0].equalsIgnoreCase("Reason Code") ||
														DE[0].equalsIgnoreCase("Country Code") ||
														DE[0].equalsIgnoreCase("Event Date (MMDD)") ||
														DE[0].equalsIgnoreCase("Account Number") ||
														//DE[0].equalsIgnoreCase("Destination Amount") ||
														DE[0].equalsIgnoreCase("Destination Currency Code") ||
														DE[0].equalsIgnoreCase("Source Amount")||
														DE[0].equalsIgnoreCase("Source Currency Code") ||
														DE[0].equalsIgnoreCase("Settlement Flag") ||
														DE[0].equalsIgnoreCase("Transaction Identifier") ||
														DE[0].equalsIgnoreCase("Central Processing Date (YDDD)") ||
														DE[0].equalsIgnoreCase("Reimbursement Attribute") ||
														DE[0].equalsIgnoreCase("Message Text"))

												{
													//AUTH_CODE = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
													stData = stline.substring((ststart_pos-1), (ststart_pos)).trim();
													logger.info("stData "+ stData);
													stRawData.add(stData);
												}

										}
										//}
										/*if(!stData.equals(""))	
									stRawData.add(stData);	*/
									}

								}
								else if(TC.equals("33"))
								{
									String stLine = stline.substring(34 , stline.length());
									if(check_TCR.equals("200"))
									{
										//GET VARIABLES FOR DECIMAL AND SIGN
										String sign = "";
										String last_Digit = "";
										String[] DE =  Data_Elements.get(i).split("\\|");
										//System.out.println("data "+ DE[0]);
										if(DE[0].equalsIgnoreCase("Trace Number")||
												DE[0].equalsIgnoreCase("Response Code")	||
												DE[0].equalsIgnoreCase("Retrieval Reference Number")||
												DE[0].equalsIgnoreCase("Card Number")||
												DE[0].equalsIgnoreCase("Transaction Amount")||
												DE[0].equalsIgnoreCase("Request Message Type") ||
												DE[0].equalsIgnoreCase("Currency Code")||
												DE[0].equalsIgnoreCase("Transaction Identifier") || 	DE[0].equalsIgnoreCase("Settlement Date"))
										{
											if(DE.length == 3)
											{
												int ststart_Pos = Integer.parseInt(DE[1].trim());
												int stEnd_pos = Integer.parseInt(DE[2].trim());
												/*
												 * logger.info("start positon "+ststart_Pos);
												 * logger.info("End Position "+stEnd_pos);
												 * logger.info("stHeader : "+DE[0]+" Value : "+stline.substring((
												 * ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));
												 */
												
												if(DE[0].equalsIgnoreCase("Trace Number"))
												{
													TRACE = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
													TRACE = stLine.substring((ststart_Pos-1), stEnd_pos).trim();
												}
												else if(DE[0].equalsIgnoreCase("Currency Code"))
												{
													CutCODE = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
											//logger.info("CutCODE "+ CutCODE);
												}
												else if(DE[0].equalsIgnoreCase("Transaction Identifier"))
												{
													transacITe = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
										//	logger.info("Transaction Identifier "+ transacITe);
												}
												
												else if(DE[0].equalsIgnoreCase("Request Message Type"))
												{
													REQ_MSG_TYPE = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
												}
												else if(DE[0].equalsIgnoreCase("Response Code")) 
												{
													RESPONSE_CODE = stLine.substring((ststart_Pos-1), stEnd_pos).trim();
												//logger.info("responce  "+SOURCE_AMT+" check this "+RESPONSE_CODE);
												}
												else if(DE[0].equalsIgnoreCase("Retrieval Reference Number"))
												{
													REFERENCE_NUMBER = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
												}
												else if(DE[0].equalsIgnoreCase("Card Number"))
												{
													//System.out.println("Card Number "+  stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim());
													String pan= stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
													String Update_Pan="";		
													if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
								         				  // System.out.println(pan);
								         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
								         				   
								         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
								         				   
								         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
								         				   
								         			   } else {
								         				   
								         				   Update_Pan =null;
								         			   }
													
													CARD_NUMBER = Update_Pan;
													FPAN = pan;
												}
												else if(DE[0].equalsIgnoreCase("Transaction Amount"))
												{
													
														SOURCE_AMT =  stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
							                         //.out.println("amount "+SOURCE_AMT);
												}
												else if(DE[0].equalsIgnoreCase("Settlement Date"))
												{
													
														DATE =  stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
							                           // System.out.println("DATE "+DATE);
												}


											}
											else if(DE.length == 2)
											{
												int ststart_Pos =  Integer.parseInt(DE[1].trim());
												if(DE[0].equalsIgnoreCase("Trace Number"))
												{
													//TRACE = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
													TRACE = stLine.substring((ststart_Pos-1), ststart_Pos).trim();
												}
												else if(DE[0].equalsIgnoreCase("Response Code")) {

													RESPONSE_CODE = stLine.substring((ststart_Pos-1), ststart_Pos).trim();
												//logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
												}
												else if(DE[0].equalsIgnoreCase("Request Message Type"))
												{
													REQ_MSG_TYPE = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
												}
												else if(DE[0].equalsIgnoreCase("Retrieval Reference Number"))
												{
													REFERENCE_NUMBER = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
												}
												else if(DE[0].equalsIgnoreCase("Card Number"))
												{
													String pan = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
													
													String Update_Pan="";		
													if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
								         				  // System.out.println(pan);
								         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
								         				   
								         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
								         				   
								         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
								         				   
								         			   } else {
								         				   
								         				   Update_Pan =null;
								         			   }
													
													CARD_NUMBER = Update_Pan;
													FPAN = pan;
												}
									
												else if(DE[0].equalsIgnoreCase("Transaction Amount"))
												{
													SOURCE_AMT = stLine.substring((ststart_Pos-1), (ststart_Pos)).replaceAll("^0*","").trim();
//System.out.println("amount "+ SOURCE_AMT);
													for(int j =0; j< Amount_translation.size() ; j++ )
													{
														String[] data =  Amount_translation.get(j).split("\\|");
														if(SOURCE_AMT.contains(data[0]))
														{
															if(data[2].equals("-"))
															{
																SOURCE_AMT = data[2]+SOURCE_AMT.replace(data[0], data[1]);
															}
															else
															{
																SOURCE_AMT = SOURCE_AMT.replace(data[0], data[1]);
															}
														}

													}

													amt = (Float.parseFloat(SOURCE_AMT)/100);
												}else if(DE[0].equalsIgnoreCase("Settlement Date"))
												{
													
													DATE = stLine.substring((ststart_Pos-1), (ststart_Pos)).replaceAll("^0*","").trim();
						                            //System.out.println("Settlement Date "+DATE);
											}

											}
										}
									}				else 	if(check_TCR.equals("210"))
									{
										//GET VARIABLES FOR DECIMAL AND SIGN
										String sign = "";
										String last_Digit = "";
										String[] DE =  Data_Elements.get(i).split("\\|");
										//System.out.println("data "+ DE[0]);
										/*
										 * if(DE[0].equalsIgnoreCase("Trace Number")||
										 * DE[0].equalsIgnoreCase("Response Code") ||
										 * DE[0].equalsIgnoreCase("Retrieval Reference Number")||
										 * DE[0].equalsIgnoreCase("Card Number")||
										 * DE[0].equalsIgnoreCase("Transaction Amount")||
										 * DE[0].equalsIgnoreCase("Request Message Type") ||
										 * DE[0].equalsIgnoreCase("Currency Code")||
										 * DE[0].equalsIgnoreCase("Transaction Identifier")) {
										 */
											if(DE.length == 3)
											{
												int ststart_Pos = Integer.parseInt(DE[1].trim());
												int stEnd_pos = Integer.parseInt(DE[2].trim());
												/*
												 * logger.info("start positon "+ststart_Pos);
												 * logger.info("End Position "+stEnd_pos);
												 * logger.info("stHeader : "+DE[0]+" Value : "+stline.substring((
												 * ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));
												 */
												
												if(DE[0].equalsIgnoreCase("purchase Date"))
												{
												;
													//Mcuntrycode x= stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
													DATE2 = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","");
													//logger.info("DATE2 "+ DATE2);
												}
												
												
						
												
												

											}
											else if(DE.length == 2)
											{
												int ststart_Pos =  Integer.parseInt(DE[1].trim());
												if(DE[0].equalsIgnoreCase("Trace Number"))
												{
													//TRACE = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
													TRACE = stLine.substring((ststart_Pos-1), ststart_Pos).trim();
												}
												else if(DE[0].equalsIgnoreCase("Response Code")) {

													RESPONSE_CODE = stLine.substring((ststart_Pos-1), ststart_Pos).trim();
												//logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
												}
												else if(DE[0].equalsIgnoreCase("Request Message Type"))
												{
													REQ_MSG_TYPE = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
												}
												else if(DE[0].equalsIgnoreCase("Retrieval Reference Number"))
												{
													REFERENCE_NUMBER = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
												}
												else if(DE[0].equalsIgnoreCase("Card Number"))
												{
													String pan = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
													
													String Update_Pan="";		
													if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
								         				  // System.out.println(pan);
								         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
								         				   
								         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
								         				   
								         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
								         				   
								         			   } else {
								         				   
								         				   Update_Pan =null;
								         			   }
													
													CARD_NUMBER = Update_Pan;
													FPAN = pan;
												}
									
												else if(DE[0].equalsIgnoreCase("Transaction Amount"))
												{
													SOURCE_AMT = stLine.substring((ststart_Pos-1), (ststart_Pos)).replaceAll("^0*","").trim();
//System.out.println("amount "+ SOURCE_AMT);
													for(int j =0; j< Amount_translation.size() ; j++ )
													{
														String[] data =  Amount_translation.get(j).split("\\|");
														if(SOURCE_AMT.contains(data[0]))
														{
															if(data[2].equals("-"))
															{
																SOURCE_AMT = data[2]+SOURCE_AMT.replace(data[0], data[1]);
															}
															else
															{
																SOURCE_AMT = SOURCE_AMT.replace(data[0], data[1]);
															}
														}

													}

													amt = (Float.parseFloat(SOURCE_AMT)/100);
												}

											}
										
									}
									else 	if(check_TCR.equals("220"))
									{
										//GET VARIABLES FOR DECIMAL AND SIGN
										String sign = "";
										String last_Digit = "";
										String[] DE =  Data_Elements.get(i).split("\\|");
										//System.out.println("data "+ DE[0]);
										/*
										 * if(DE[0].equalsIgnoreCase("Trace Number")||
										 * DE[0].equalsIgnoreCase("Response Code") ||
										 * DE[0].equalsIgnoreCase("Retrieval Reference Number")||
										 * DE[0].equalsIgnoreCase("Card Number")||
										 * DE[0].equalsIgnoreCase("Transaction Amount")||
										 * DE[0].equalsIgnoreCase("Request Message Type") ||
										 * DE[0].equalsIgnoreCase("Currency Code")||
										 * DE[0].equalsIgnoreCase("Transaction Identifier")) {
										 */
											if(DE.length == 3)
											{
												int ststart_Pos = Integer.parseInt(DE[1].trim());
												int stEnd_pos = Integer.parseInt(DE[2].trim());
												/*
												 * logger.info("start positon "+ststart_Pos);
												 * logger.info("End Position "+stEnd_pos);
												 * logger.info("stHeader : "+DE[0]+" Value : "+stline.substring((
												 * ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));
												 */
												
												if(DE[0].equalsIgnoreCase("Forwarding Institution Country Code"))
												{
												;
													//Mcuntrycode = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
													Mcuntrycode = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","");
													//logger.info("Forwarding Institution Country Code "+ Mcuntrycode);
												}
												else if(DE[0].equalsIgnoreCase("Geo ZIP Code ZIP Five"))
												{
									
													MName =stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","");
													//logger.info("Geo ZIP Code ZIP Five "+ MName);
												}	else if(DE[0].equalsIgnoreCase("MERCHANT_CATEGORY_CODE"))
												{
									
													MCC =stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","");
										//		logger.info("MERCHANT_CATEGORY_CODE "+ MCC);
												}
											
						
												
												

											}
											else if(DE.length == 2)
											{
												int ststart_Pos =  Integer.parseInt(DE[1].trim());
												if(DE[0].equalsIgnoreCase("Trace Number"))
												{
													//TRACE = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
													TRACE = stLine.substring((ststart_Pos-1), ststart_Pos).trim();
												}
												else if(DE[0].equalsIgnoreCase("Response Code")) {

													RESPONSE_CODE = stLine.substring((ststart_Pos-1), ststart_Pos).trim();
												//logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
												}
												else if(DE[0].equalsIgnoreCase("Request Message Type"))
												{
													REQ_MSG_TYPE = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
												}
												else if(DE[0].equalsIgnoreCase("Geo ZIP Code ZIP Five"))
												{
													REFERENCE_NUMBER = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
													logger.info("REFERENCE_NUMBER "+ REFERENCE_NUMBER);
												}
												else if(DE[0].equalsIgnoreCase("Card Number"))
												{
													String pan = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
													
													String Update_Pan="";		
													if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
								         				  // System.out.println(pan);
								         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
								         				   
								         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
								         				   
								         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
								         				   
								         			   } else {
								         				   
								         				   Update_Pan =null;
								         			   }
													
													CARD_NUMBER = Update_Pan;
													FPAN = pan;
												}
									
												else if(DE[0].equalsIgnoreCase("Transaction Amount"))
												{
													SOURCE_AMT = stLine.substring((ststart_Pos-1), (ststart_Pos)).replaceAll("^0*","").trim();
//System.out.println("amount "+ SOURCE_AMT);
													for(int j =0; j< Amount_translation.size() ; j++ )
													{
														String[] data =  Amount_translation.get(j).split("\\|");
														if(SOURCE_AMT.contains(data[0]))
														{
															if(data[2].equals("-"))
															{
																SOURCE_AMT = data[2]+SOURCE_AMT.replace(data[0], data[1]);
															}
															else
															{
																SOURCE_AMT = SOURCE_AMT.replace(data[0], data[1]);
															}
														}

													}

													amt = (Float.parseFloat(SOURCE_AMT)/100);
												}

											}
										
									}


								}
							}
						}
				
						if(TC.equals("05")||TC.equals("06")||TC.equals("07")||TC.equals("25")||TC.equals("27"))
						{
							count++;
							if(TCR_Code.equals("00")||TCR_Code.equals("20"))
							{
//								ResultSet rset2 =pstmt2.executeQuery();
//								if(rset2.next())
//								{
//									seq = rset2.getString("SEQ");
//								}
								seq="VISA"+lineNumber;
							}
							
							//Insertint in input table first
							pstmt1.setString(1, TC);
							pstmt1.setString(2, TCR_Code);
							pstmt1.setString(3, stline);
							pstmt1.setString(4, seq+"");
							/*pstmt1.setString(5, "28/09/2017");*/
							pstmt1.setString(5, setupBean.getFileDate());

							pstmt1.addBatch();

							if(TCR_Code.equals("00")||TCR_Code.equals("20"))
							{
								read_stmt_05.setString(1, TC);
								read_stmt_05.setString(2, TCR_Code);
								/*read_stmt.setString(3, ACC_NO);
								read_stmt.setString(4, amt+"");
								read_stmt.setString(5, AUTH_CODE);*/
								read_stmt_05.setString(3, seq+"");
								/*read_stmt.setString(7, "28/09/2017");*/
								read_stmt_05.setString(4, setupBean.getFileDate());
								read_stmt_05.setString(5, "1");
								int index = 6;
								for(int m = 0;m<stRawData.size();m++)
								{
									
									if(m == 0) { 
										
										String pan =stRawData.get(m);
										
										String Update_Pan="";		
										if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
					         				  // System.out.println(pan);
					         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
					         				   
					         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
					         				   
					         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
					         				   
					         			   } else {
					         				   
					         				   Update_Pan =null;
					         			   }
										
										FPAN = pan;
										read_stmt_05.setString(index, Update_Pan);
									}else {
									read_stmt_05.setString(index, stRawData.get(m));
									}
									
									index++;
								}
								
								read_stmt_05.setString(31, FPAN);
								//read_stmt_05.addBatch();  //commented by int8624 on 28 may
							}
							//ADD ELSE IF CONDITION AND ADD BATCH HERE IN8624 on 28 May
							else if(TCR_Code.equalsIgnoreCase("05") || (TC.equals("06")&& TCR_Code.equalsIgnoreCase("25")))
							{
							
								read_stmt_05.setString(32, TRAN_ID);
								read_stmt_05.setString(33, file.getOriginalFilename());
							//	logger.info("read_stmt_05  "+ TRAN_ID);
								read_stmt_05.addBatch();
							}
						}
						else if(TC.equals("10")||TC.equals("20"))
						{

							count++;
							if(TCR_Code.equals("00"))
							{
								/*
								 * ResultSet rset2 =pstmt2.executeQuery(); if(rset2.next()) { seq =
								 * rset2.getString("SEQ"); }
								 */
								seq="VISA"+lineNumber;
										
							}
							
							//Insertint in input table first
							pstmt1.setString(1, TC);
							pstmt1.setString(2, TCR_Code);
							pstmt1.setString(3, stline);
							pstmt1.setString(4, seq+"");
							/*pstmt1.setString(5, "28/09/2017");*/
							pstmt1.setString(5, setupBean.getFileDate());

							pstmt1.addBatch();

							if(TCR_Code.equals("00"))
							{
								read_stmt_10.setString(1, TC);
								read_stmt_10.setString(2, TCR_Code);
								/*read_stmt.setString(3, ACC_NO);
								read_stmt.setString(4, amt+"");
								read_stmt.setString(5, AUTH_CODE);*/
								read_stmt_10.setString(3, seq+"");
								/*read_stmt.setString(7, "28/09/2017");*/
								read_stmt_10.setString(4, setupBean.getFileDate());
								read_stmt_10.setString(5, "1");
								int index = 6;
								for(int m = 0;m<stRawData.size();m++)
								{
									//read_stmt_10.setString(index, stRawData.get(m));
									
									if(m == 0) { 
										
										String pan =stRawData.get(m);
										
										String Update_Pan="";		
										if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
					         				  // System.out.println(pan);
					         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
					         				   
					         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
					         				   
					         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
					         				   
					         			   } else {
					         				   
					         				   Update_Pan =null;
					         			   }
										
										
										read_stmt_10.setString(index, Update_Pan);
									}else {
										read_stmt_10.setString(index, stRawData.get(m));
									}
									
									index++;
								}
								System.out.println("index is "+index);
								read_stmt_10.setString(21, FPAN);
								read_stmt_10.setString(22, file.getOriginalFilename());
								read_stmt_10.addBatch();
							}

							/*if(count == 10000)
							{
								count = 1;
								read_stmt_10.executeBatch();
								pstmt1.executeBatch();
								logger.info("Exceuted batch "+batch);
								batch++;

							}*/
						
						}
						else if(TC.equals("33"))
						{
							count++;					
							//if(check_TCR.equals("200"))
							if(check_TCR.equals("200") || containsVISATable)
							{
								/*
								 * ResultSet rset2 =pstmt2.executeQuery(); if(rset2.next()) { seq =
								 * rset2.getString("SEQ"); }
								 */
								seq="VISA"+lineNumber;
							}
							if(check_TCR.equals("220") || containsVISATable)
							{
								/*
								 * ResultSet rset2 =pstmt2.executeQuery(); if(rset2.next()) { seq =
								 * rset2.getString("SEQ"); }
								 */
								seq="VISA"+lineNumber;
							}
							
							
							
							
							 
							//Insertint in input table first
							if(check_TCR.equals("200")){
							pstmt1.setString(1, TC);
							pstmt1.setString(2, check_TCR);
							pstmt1.setString(3, stline);
							pstmt1.setString(4, seq+"");
							/*pstmt1.setString(5, "28/09/2017");*/
							pstmt1.setString(5, setupBean.getFileDate());
							

							pstmt1.addBatch();
							}
							if(!containsVISATable && check_TCR.equals("200")  )
							{

								pstmt33.setString(1, TC);
								pstmt33.setString(2, check_TCR);
								
								if(CARD_NUMBER != null && CARD_NUMBER.trim()!="") {
								String pan = CARD_NUMBER;
								
								String Update_Pan="";		
								if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
			         				  // System.out.println(pan);
			         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
			         				   
			         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
			         				   
			         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
			         				   
			         			   } else {
			         				   
			         				   Update_Pan =null;
			         			   }
								
								if(FPAN.equalsIgnoreCase(""))
									FPAN = pan;
								
								pstmt33.setString(3, Update_Pan);
								} else  {
									
									pstmt33.setString(3, CARD_NUMBER);
									
								}
								pstmt33.setString(4, SOURCE_AMT);
								pstmt33.setString(5, TRACE);
								pstmt33.setString(6, REFERENCE_NUMBER);
								pstmt33.setString(7, RESPONSE_CODE);
								pstmt33.setString(8, seq);
								//pstmt33.setString(9, "28/09/2017");
								pstmt33.setString(9, setupBean.getFileDate());
								pstmt33.setString(10, "1");
								if(check_TCR.equals("220")) {
									pstmt33.setString(11, "220");
									
								}else {
									pstmt33.setString(11, REQ_MSG_TYPE);
									
								}
								
								dummyrrn = REFERENCE_NUMBER;
								pstmt33.setString(12, FPAN);
								pstmt33.setString(13, CutCODE);
								pstmt33.setString(14, CutCODE);
								pstmt33.setString(15, SOURCE_AMT);
								pstmt33.setString(16, transacITe);
								pstmt33.setString(17, Mcuntrycode);
								pstmt33.setString(18, MName);
								pstmt33.setString(19, MCC);
								pstmt33.setString(20, DATE2);
//logger.info("FPAN "+ FPAN);
								pstmt33.addBatch();
								dummyrrn = REFERENCE_NUMBER;
								
								
							} else if (!containsVISATable && check_TCR.equals("220")) {

								try {
									//System.out.println("sq "+seq +" rn " + dummyrrn);
									Integer getNewSeq = Integer.valueOf(seq.substring(4, seq.length()));
									//System.out.println("exist sqe "+ getNewSeq);
									getNewSeq = getNewSeq - 1;
									//System.out.println("getNewSeq " + getNewSeq);
									seq = "VISA" + getNewSeq;
									//System.out.println("updatedseq  " + seq);

							//	System.out.println("data  " + MName +" "+ MCC +" " +" "+ Mcuntrycode );

									conn.prepareStatement("update visa_acq_raWDATA set MERCHANT_COUNTRY_CODE='" + Mcuntrycode + "',MERCHANT_CATEGORY_CODE='" + MCC
											+ "', where str_to_date(FILEDATE,'DD-MM-YY')=str_to_date('"+ setupBean.getFileDate()+"','DD-MM-YY')  and REFERENCE_NUMBER='" + dummyrrn + "'");
									
								} catch (Exception e) {
									logger.info("Exception while updating rawdata " + e);
								}

							}
						
						}
						if(count == 100000)
						{
							count = 1;
							//INSERTING ISSUER DATA
							//System.out.println("pstmt1.executeBatch()");
							pstmt1.executeBatch();
							//System.out.println("read_stmt_05.executeBatch()");
							read_stmt_05.executeBatch();
							//System.out.println("read_stmt_10.executeBatch()");
							read_stmt_10.executeBatch();
							//INSERTIN TC 33 DATA
							//System.out.println("pstmt1.executeBatch();");
							pstmt1.executeBatch();
						//	System.out.println("pstmt33.executeBatch();;");
							pstmt33.executeBatch();
							logger.info("EXECUTED BATCH "+batch);
							batch++;
							
							
							
						}
						
						stline = br.readLine();
					}
					
						
				//System.out.println("**********read_stmt_05.executeBatch");
				read_stmt_05.executeBatch();
				//System.out.println("********read_stmt_10.executeBatch");
				read_stmt_10.executeBatch();
				//System.out.println("pstmt1.executeBatch()");
				pstmt1.executeBatch();
				//System.out.println("pstmt33.executeBatch()");
				pstmt33.executeBatch();
				
				logger.info("Completed Reading");
				
				/****** ENCRYPTING FPAN *****/
		/*		try
				{
					String checkUploadCount = "select NVL(file_count,0) as FILECOUNT from MAIN_FILE_UPLOAD_DTLS WHERE FILEID IN (select fileid from main_filesource where filename = 'VISA' AND FILE_CATEGORY = 'VISA' "+
										"AND FILE_SUBCATEGORY = 'ISSUER') and filedate = TO_DATE(?,'DD/MM/YYYY')";
					PreparedStatement encryptps = conn.prepareStatement(checkUploadCount);
					encryptps.setString(1, setupBean.getFileDate());
					ResultSet rs = encryptps.executeQuery();
					int uploadCount = 0;
					while(rs.next())
					{
						uploadCount = rs.getInt("FILECOUNT");
					}
					logger.info("Upload Count is "+uploadCount);
					rs.close();
					encryptps.close();
					
					if(uploadCount == 1)
					{
						logger.info("Inside Updation ");
						String encryptPan = "update visa_visa_rawdata OS1 set FPAN = (select ibkl_encrypt_decrypt.ibkl_set_encrypt_val(FPAN) enc from VISA_VISA_RAWDATA OS2 "
								+ "WHERE OS1.DCRS_SEQ_NO = OS2.DCRS_SEQ_NO) WHERE FILEDATE = ?";
						String encryptPan = "update visa_visa_rawdata set fpan = ibkl_encrypt_decrypt.ibkl_set_encrypt_val(FPAN) "+
								"where filedate = TO_DATE(?,'DD/MM/YYYY')";
						logger.info("Excryption query id "+encryptPan);
						encryptps = conn.prepareStatement(encryptPan);
						encryptps.setString(1, setupBean.getFileDate());
						logger.info("Updation "+encryptps.execute());
					}
					
				}
				catch(Exception e)
				{
					logger.info("Exception while updating rawdata "+e);
				}*/
				conn.close();
				logger.info("***** ReadVisaFile.readData End ****");
				
			}
			catch(Exception e)
			{
				logger.error(" error in ReadVisaFile.readData", new Exception("ReadVisaFile.readData",e));
				e.printStackTrace();
				return false;
			}
			finally
			{
				
				
				if(br != null)
				{
					try
					{
						br.close();
					}
					catch(Exception e)
					{
						logger.error(" error in ReadVisaFile.readData", new Exception("ReadVisaFile.readData",e));
					}
				}
			}
			return true;}

	public static void main(String args[]) {
		try {
			OracleConn oracon = new OracleConn();
			// readFile("\\D:\\",oracon.getconn());
		} catch (Exception e) {

		}
	}

}
