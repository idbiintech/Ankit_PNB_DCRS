package com.recon.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.VisaUploadBean;

public class ReadVisaEPDomCCFiles {

	static String description = "";
	static String description1 = "";
	static int vss120_record_no =0;
	
	public void fileupload(VisaUploadBean beanObj,MultipartFile file,Connection conn) throws SQLException {

		String line = null;
		boolean vss_110 = false,vss_120 = false,vss_130 = false, vss_140 = false,vss_210 = false;
		boolean international = false,domestic = false;
		boolean vss_110_dom_starts = false, vss_120_dom_starts = false,vss_110_int_starts = false, vss_130_dom_starts = false,issuer = false, acquirer = false;
		boolean vss_120_int_starts = false, vss_130_int_starts = false, vss_140_int_starts = false, vss_210_int_starts = false;
		int sr_no =0;
		
		/*if(beanObj.getFileType().equalsIgnoreCase("DOMESTIC"))
		{
			domestic = true;
			international = false;
		}
		else
		{
			domestic = false;
			international = true;
		}*/
		try
		{
			System.out.println("reading file EP_150621");
			//File file = new File("C:\\\\Users\\\\int8624\\\\Desktop\\\\HTML READING\\EP_150621.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));

			String Insertvss120 = "INSERT INTO VISA_DOM_EP_RAWDATA(MSGTYPE,SUBCATEGORY, CREATEDBY, ISS_ACQ, FILEDATE, DESCRIPTION, SUB_DESCRIPTION, TXN_COUNT, CLEARING_AMT, CLEARING_AMT_SIGN, INT_VAL_CR, INT_VAL_DR, RECORD_COUNT) "+
						"VALUES(?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'), ?, ?, ?, ?, ?, ?, ?, ?)";
			
			String InsertVSS110 = "INSERT INTO VISA_DOM_EP_RAWDATA(MSGTYPE,SUBCATEGORY, CREATEDBY, ISS_ACQ, FILEDATE,DESCRIPTION, SUB_DESCRIPTION, TXN_COUNT, CREDIT_AMT, DEBIT_AMT, TOTAL_AMT, TOTAL_AMT_SIGN) "+
						"VALUES(?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?,?,?,?,?,?,?)";
			
			String InsertVSS130 = "INSERT INTO VISA_DOM_EP_RAWDATA(MSGTYPE, SUBCATEGORY, CREATEDBY, ISS_ACQ, FILEDATE, DESCRIPTION, SUB_DESCRIPTION, TXN_COUNT, INT_AMT, INT_AMT_SIGN, REIMB_AMT_CR, REIMB_AMT_DR) "+
						"VALUES(?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?,?,?,?,?,?,?)";
			
			String InsertVSS140 = "INSERT INTO VISA_DOM_EP_RAWDATA(MSGTYPE, SUBCATEGORY, CREATEDBY, ISS_ACQ, FILEDATE, DESCRIPTION, SUB_DESCRIPTION, TXN_COUNT, INT_AMT, INT_AMT_SIGN, VISA_CHARGES_DR, VISA_CHARGES_CR) "+
						"VALUES(?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?,?,?,?,?,?,?)";
			
			String InsertVSS210 = "INSERT INTO VISA_DOM_EP_RAWDATA(MSGTYPE, SUBCATEGORY, CREATEDBY, ISS_ACQ, FILEDATE, DESCRIPTION, SETT_CURR_INT_AMNT, SETT_CURR_INT_AMNT_SIGN, SETT_CURR_CONV_FEE, CLR_CURR_INT_AMNT, CLR_CURR_INT_AMNT_SIGN, CLR_CURR_CONV_FEE, CLR_CURR_OPT_ISS_FEE, CLR_CURR_OPT_ISS_FEE_SIGN) "+
						"VALUES(?,?,?,?,TO_DATE(?,'DD/MM/YYYY'), ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			while((line = br.readLine()) != null)
			{	
				if(!line.trim().equalsIgnoreCase(""))
				{
					//System.out.println(line);
					if(line.contains(":"))
					{
						String[] values = line.split(":");
						//System.out.println(values[0].trim());
						if(values[0].trim().equalsIgnoreCase("REPORT ID"))
						{
							//System.out.println("Hie");
							vss_120_dom_starts = false;
							vss_120 = false;
							domestic = false;
							international = false;
							vss_130 = false;
							vss_130_dom_starts = false;
							vss_130_int_starts = false;
							vss_120_int_starts = false;
							//vss_140_int_starts = false;
							vss_210_int_starts = false;
							description = "";
							description1 = "";
						}
						if(values[1].trim().startsWith("VSS"))
						{
							//System.out.println(values[1].trim());
							//System.out.println(values[1].trim().substring(4, 7));
							if(values[1].trim().substring(4, 7).equalsIgnoreCase("110") && !values[1].trim().contains("VSS-110-M"))
							{
								vss_110 = true;
								vss_120 = false;
								vss_130 = false;
								vss_140 = false;
								vss_210 = false;
							}
							else if(values[1].trim().substring(4, 7).equalsIgnoreCase("120") && !values[1].trim().contains("VSS-120-M"))
							{
								vss_110 = false;
								vss_120 = true;
								vss_130 = false;
								vss_140 = false;
								vss_210 = false;
							}
							else if(values[1].trim().substring(4, 7).equalsIgnoreCase("130") && !values[1].trim().contains("VSS-130-M"))
							{
								vss_110 = false;
								vss_120 = false;
								vss_130 = true;
								vss_140 = false;
								vss_210 = false;
							}
							else if(values[1].trim().substring(4, 7).equalsIgnoreCase("140") && !values[1].trim().contains("VSS-140-M"))
							{
								vss_110 = false;
								vss_120 = false;
								vss_130 = false;
								vss_140 = true;
								vss_210 = false;
							}
							else if(values[1].trim().substring(4, 7).equalsIgnoreCase("210") && !values[1].trim().contains("VSS-210-M"))
							{
								vss_110 = false;
								vss_120 = false;
								vss_130 = false;
								vss_140 = false;
								vss_210 = true;
							}
						}
						else if(values[0].trim().equalsIgnoreCase("REPORTING FOR"))
						{
//							if(values[1].trim().startsWith("1000184102"))
							if(values[1].trim().contains("INDIA AREA NET"))
							{
								domestic = true;
								international = false;
							}
//							else if(values[1].trim().startsWith("1000184100"))
							if(values[1].trim().contains("INTERNATIONAL SETTLEMENT SERVICE"))
							{
								domestic = false;
								international = true;
							}
						}
					}
					else
					{
						if(domestic)
						{
							if(vss_110 && domestic)
							{
								///reading for VSS 110 DOMESTIC
								if(line.trim().startsWith("INTERCHANGE VALUE"))
								{
									description = line.trim();
									//System.out.println(" ********** "+description+ " ************** ");
									vss_110_dom_starts = true;
									continue;
								}
								else if(line.trim().startsWith("*** END OF VSS-110 REPORT ***"))
								{
									vss_110_dom_starts = false;
									vss_110 = false;
									domestic = false;
									continue;
								}

								if(vss_110_dom_starts)
								{
									PreparedStatement ps110 = conn.prepareStatement(InsertVSS110);
									sr_no = 1;
									ps110.setString(sr_no++, "110");
									ps110.setString(sr_no++ , "DOMESTIC");
									ps110.setString(sr_no++ , beanObj.getCreatedBy());
									if(issuer)
										ps110.setString(sr_no++ , "ISSUER");
									else
										ps110.setString(sr_no++ , "ACQUIRER");
									ps110.setString(sr_no++ , beanObj.getFileDate());
									
									System.out.println("sr no is "+sr_no);
									readVSS110(line, sr_no, ps110);
								}

							}
							else if(vss_120 && domestic)
							{
								
								
								if(line.trim().equalsIgnoreCase("ACQUIRER TRANSACTIONS"))
								{
									System.out.println("ACQUIRER TRANSACTIONS-------------------------");
									
									issuer = false;
									acquirer = true;
									description = line.trim();
									vss_120_dom_starts = true;
									continue;
								}
								else if(line.trim().equalsIgnoreCase("ISSUER TRANSACTIONS"))
								{
									System.out.println("ISSUER TRANSACTIONS-------------------------");
									issuer = true;
									acquirer = false;
									description = line.trim();
									vss_120_dom_starts = true;
									continue;
								}
								else if(line.trim().equalsIgnoreCase("***  END OF VSS-120 REPORT  ***"))
								{
									vss_120_dom_starts = false;
									vss_120 = false;
									domestic = false;
									continue;
								}

								if(vss_120_dom_starts)
								{
									PreparedStatement ps120 = conn.prepareStatement(Insertvss120);
									sr_no = 1;
									ps120.setString(sr_no++, "120");
									ps120.setString(sr_no++ , "DOMESTIC");
									ps120.setString(sr_no++ , beanObj.getCreatedBy());
									if(issuer)
										ps120.setString(sr_no++ , "ISSUER");
									else
										ps120.setString(sr_no++ , "ACQUIRER");
									ps120.setString(sr_no++ , beanObj.getFileDate());
									
									readVSS120(line, sr_no, ps120);

								}
							}
							else if(vss_130 && domestic)
							{
								if(line.trim().equalsIgnoreCase("ACQUIRER TRANSACTIONS"))
								{
									vss_130_dom_starts = true;
									acquirer = true;
									issuer = false;
									continue;
								}
								else if(line.trim().equalsIgnoreCase("ISSUER TRANSACTIONS"))
								{
									vss_130_dom_starts = true;
									acquirer = false;
									issuer = true;
									continue;
								}
								else if(line.trim().contains("AMOUNT               CREDITS                DEBITS"))
								{
									vss_130_dom_starts = true;
									/*acquirer = false;
									issuer = false; */
									continue;
								}
								else if(line.trim().equalsIgnoreCase("*** END OF VSS-130 REPORT ***"))
								{
									vss_130_dom_starts = false;
									vss_130 = false;
									domestic = false;
									continue;
								}

								if(vss_130_dom_starts)
								{
									
									PreparedStatement ps130 = conn.prepareStatement(InsertVSS130);
									sr_no = 1;
									ps130.setString(sr_no++, "130");
									ps130.setString(sr_no++ , "DOMESTIC");
									ps130.setString(sr_no++ , beanObj.getCreatedBy());
									if(issuer)
									{
										System.out.println("SUBCATEGORY IS ISSUER");
										ps130.setString(sr_no++ , "ISSUER");
									}
									else
									{
										System.out.println("SUBCATEGORY IS ACQUIRER");
										ps130.setString(sr_no++ , "ACQUIRER");
									}
									ps130.setString(sr_no++ , beanObj.getFileDate());
									
									
									
									if(acquirer)
									{
										if(line.substring(53, 132).trim().equalsIgnoreCase(""))
										{
											if(line.trim().contains("ORIGINAL WITHDRAWAL") || line.trim().contains("INDIA") )
											{
												if(line.trim().contains("INDIA") && !description1.contains("INDIA"))
												{
													description1 = description1 +" "+ line.trim();
												}
												else
													description1 = description +" "+ line.trim();
											}
											else if (line.trim().equalsIgnoreCase("VISA A.P.") || line.trim().equalsIgnoreCase("ORIGINAL") )
											{
												continue;
											}
											else
											{
												description = line.trim();
											}
										}
										else
										{
											System.out.println(" ********** "+description1+ " ************** ");

											System.out.println("sub description is : "+line.substring(0, 37).trim());
											System.out.println("count is : "+line.substring(53, 63).trim());
											System.out.println("Interchange amount : "+line.substring(63, 84).trim());
											System.out.println("Interchange amount sign : "+line.substring(84, 87).trim());
											System.out.println("Reimbursement amount CR : "+line.substring(87,110).trim());
											System.out.println("Reimbursement amount DR : "+line.substring(110, line.length()).trim());
											
											ps130.setString(sr_no++, description1);
											ps130.setString(sr_no++, line.substring(0, 37).trim());
											ps130.setString(sr_no++, line.substring(53, 63).trim());
											ps130.setString(sr_no++, line.substring(63, 84).trim());
											ps130.setString(sr_no++, line.substring(84, 87).trim());
											ps130.setString(sr_no++, line.substring(87,110).trim());
											ps130.setString(sr_no++, line.substring(110, line.length()).trim());
											ps130.execute();
											
										}
									}
									else if(issuer)
									{
										if(line.substring(53, 132).trim().equalsIgnoreCase(""))
										{
											if (line.trim().equalsIgnoreCase("VISA A.P.") || line.trim().equalsIgnoreCase("ORIGINAL") )
											{
												continue;
											}
											else if(line.trim().contains("INDIA") )
											{
												if(line.trim().contains("INDIA"))
												{
													if(!description1.contains("INDIA"))
													{
														description1 = description1 +" "+ line.trim();
													}
													else if(description1.contains("NEPAL"))
													{
														description1 = description1.replace("NEPAL", "INDIA");
													}
													else
														description1 = description+" "+line.trim();

												}
												else
													description1 = description +" "+ line.trim();
											}
											else
											{
												description1 = line.trim();
											}
										}
										else
										{
											System.out.println(" ********** "+description1+ " ************** ");

											System.out.println("sub description is : "+line.substring(0, 39).trim());
											System.out.println("count is : "+line.substring(39, 63).trim());
											System.out.println("Interchange amount : "+line.substring(63, 84).trim());
											System.out.println("Interchange amount sign : "+line.substring(84, 87).trim());
											System.out.println("Reimbursement amount CR : "+line.substring(87,110).trim());
											System.out.println("Reimbursement amount DR : "+line.substring(110, line.length()).trim());
											
											ps130.setString(sr_no++, description1);
											ps130.setString(sr_no++, line.substring(0, 39).trim());
											ps130.setString(sr_no++, line.substring(39, 63).trim());
											ps130.setString(sr_no++, line.substring(63, 84).trim());
											ps130.setString(sr_no++, line.substring(84, 87).trim());
											ps130.setString(sr_no++, line.substring(87,110).trim());
											ps130.setString(sr_no++, line.substring(110, line.length()).trim());
											ps130.execute();
											
											ps130.close();

										}
									}
									else
									{
										if(line.substring(53, 132).trim().equalsIgnoreCase(""))
										{
											if (line.trim().equalsIgnoreCase("VISA A.P.") || line.trim().equalsIgnoreCase("ORIGINAL") )
											{
												continue;
											}
											else if(line.trim().contains("INDIA") )
											{
												if(line.trim().contains("INDIA"))
												{
													if(!description1.contains("INDIA"))
													{
														description1 = description1 +" "+ line.trim();
													}
													else if(description1.contains("NEPAL"))
													{
														description1 = description1.replace("NEPAL", "INDIA");
													}
													else
														description1 = description+" "+line.trim();

												}
												else
													description1 = description +" "+ line.trim();
											}
											else
											{
												description1 = description1 +" "+ line.trim();
											}
										}
										else
										{
											System.out.println(" ********** "+description1+ " ************** ");

											System.out.println("sub description is : "+line.substring(0, 39).trim());
											System.out.println("count is : "+line.substring(39, 63).trim());
											System.out.println("Interchange amount : "+line.substring(63, 84).trim());
											System.out.println("Interchange amount sign : "+line.substring(84, 87).trim());
											System.out.println("Reimbursement amount CR : "+line.substring(87,110).trim());
											System.out.println("Reimbursement amount DR : "+line.substring(110, line.length()).trim());
											
											ps130.setString(sr_no++, description1);
											ps130.setString(sr_no++, line.substring(0, 39).trim());
											ps130.setString(sr_no++, line.substring(39, 63).trim());
											ps130.setString(sr_no++, line.substring(63, 84).trim());
											ps130.setString(sr_no++, line.substring(84, 87).trim());
											ps130.setString(sr_no++, line.substring(87,110).trim());
											ps130.setString(sr_no++, line.substring(110, line.length()).trim());
											ps130.execute();
											
											ps130.close();

										}
									}
								}
							}
							
						}
						else //INTERNATIONAL
						{
							if(vss_110 && international)
							{
								///reading for VSS 110 DOMESTIC
								if(line.trim().startsWith("INTERCHANGE VALUE"))
								{
									description = line.trim();
									//System.out.println(" ********** "+description+ " ************** ");
									vss_110_int_starts = true;
									continue;
								}
								else if(line.trim().startsWith("*** END OF VSS-110 REPORT ***"))
								{
									vss_110_int_starts = false;
									vss_110 = false;
									international = false;
									continue;
								}

								if(vss_110_int_starts)
								{
									PreparedStatement ps110 = conn.prepareStatement(InsertVSS110);
									sr_no = 1;
									ps110.setString(sr_no++, "110");
									ps110.setString(sr_no++ , "INTERNATIONAL");
									ps110.setString(sr_no++ , beanObj.getCreatedBy());
									if(issuer)
										ps110.setString(sr_no++ , "ISSUER");
									else
										ps110.setString(sr_no++ , "ACQUIRER");
									ps110.setString(sr_no++ , beanObj.getFileDate());
									
									System.out.println("sr no is "+sr_no);
									
									readVSS110(line, sr_no, ps110); 
								}

							}
							else if(vss_140 && international )
							{
								if(line.trim().equalsIgnoreCase("ISSUER TRANSACTIONS"))
								{
									System.out.println("ISSUER TRANSACTIONS-------------------------");
									issuer = true;
									acquirer = false;
									description = line.trim();
									vss_140_int_starts = true;
									continue;
								}
								else if(line.trim().equalsIgnoreCase("*** END OF VSS-140 REPORT ***"))
								{
									vss_140_int_starts = false;
									vss_140 = false;
									international = false;
									continue;
								}
								
								if(vss_140_int_starts)
								{
									PreparedStatement ps140 = conn.prepareStatement(InsertVSS140);
									sr_no = 1;
									ps140.setString(sr_no++, "140");
									ps140.setString(sr_no++ , "INTERNATIONAL");
									ps140.setString(sr_no++ , beanObj.getCreatedBy());
									if(issuer)
										ps140.setString(sr_no++ , "ISSUER");
									else
										ps140.setString(sr_no++ , "ACQUIRER");
									ps140.setString(sr_no++ , beanObj.getFileDate());
									
									System.out.println("sr no is "+sr_no);
									
									readVSS140(line, sr_no, ps140);
								}
							}
							else if(vss_210 && international)
							{
								if(line.trim().equalsIgnoreCase("ISSUER TRANSACTIONS"))
								{
									System.out.println("ISSUER TRANSACTIONS-------------------------");
									issuer = true;
									acquirer = false;
									description = line.trim();
									vss_210_int_starts = true;
									continue;
								}
								else if(line.trim().equalsIgnoreCase("*** END OF VSS-210 REPORT ***"))
								{
									vss_210_int_starts = false;
									vss_210 = false;
									international = false;
									continue;
								}
								
								if(vss_210_int_starts)
								{
									PreparedStatement ps210 = conn.prepareStatement(InsertVSS210);
									sr_no = 1;
									ps210.setString(sr_no++, "210");
									ps210.setString(sr_no++ , "INTERNATIONAL");
									ps210.setString(sr_no++ , beanObj.getCreatedBy());
									if(issuer)
										ps210.setString(sr_no++ , "ISSUER");
									else
										ps210.setString(sr_no++ , "ACQUIRER");
									ps210.setString(sr_no++ , beanObj.getFileDate());
									
									System.out.println("sr no is "+sr_no);
									
									readVSS210(line, sr_no, ps210);
								}
							}
							else if(vss_120 && international)
							{
								if(line.trim().equalsIgnoreCase("ACQUIRER TRANSACTIONS"))
								{
									System.out.println("ACQUIRER TRANSACTIONS-------------------------");
									issuer = false;
									acquirer = true;
									description = line.trim();
									vss_120_int_starts = true;
									continue;
								}
								else if(line.trim().equalsIgnoreCase("ISSUER TRANSACTIONS"))
								{
									System.out.println("ISSUER TRANSACTIONS-------------------------");
									issuer = true;
									acquirer = false;
									description = line.trim();
									vss_120_int_starts = true;
									continue;
								}
								else if(line.trim().equalsIgnoreCase("***  END OF VSS-120 REPORT  ***"))
								{
									vss_120_int_starts = false;
									vss_120 = false;
									international = false;
									continue;
								}

								if(vss_120_int_starts)
								{

									PreparedStatement ps120 = conn.prepareStatement(Insertvss120);
									sr_no = 1;
									ps120.setString(sr_no++, "120");
									ps120.setString(sr_no++ , "INTERNATIONAL");
									ps120.setString(sr_no++ , beanObj.getCreatedBy());
									if(issuer)
										ps120.setString(sr_no++ , "ISSUER");
									else
										ps120.setString(sr_no++ , "ACQUIRER");
									ps120.setString(sr_no++ , beanObj.getFileDate());
									
									readInternationalVSS120(line, sr_no, ps120);

								}
							}
							else if(vss_130 && international)
							{
								if(line.trim().equalsIgnoreCase("ACQUIRER TRANSACTIONS"))
								{
									vss_130_int_starts = true;
									acquirer = true;
									issuer = false;
									continue;
								}
								else if(line.trim().equalsIgnoreCase("ISSUER TRANSACTIONS"))
								{
									vss_130_int_starts = true;
									acquirer = false;
									issuer = true;
									continue;
								}
								else if(line.trim().contains("AMOUNT               CREDITS                DEBITS"))
								{
									vss_130_int_starts = true;
									/*acquirer = false;
									issuer = false; */
									continue;
								}
								else if(line.trim().equalsIgnoreCase("*** END OF VSS-130 REPORT ***"))
								{
									vss_130_int_starts = false;
									vss_130 = false;
									international = false;
									continue;
								}

								if(vss_130_int_starts)
								{
									PreparedStatement ps130 = conn.prepareStatement(InsertVSS130);
									sr_no = 1;
									ps130.setString(sr_no++, "130");
									ps130.setString(sr_no++ , "INTERNATIONAL");
									ps130.setString(sr_no++ , beanObj.getCreatedBy());
									if(issuer)
										ps130.setString(sr_no++ , "ISSUER");
									else
										ps130.setString(sr_no++ , "ACQUIRER");
									ps130.setString(sr_no++ , beanObj.getFileDate());
									
									System.out.println("sr no is "+sr_no);
									
									
									if(acquirer)
									{
										if(line.substring(53, 132).trim().equalsIgnoreCase(""))
										{
											if(line.trim().contains("ORIGINAL WITHDRAWAL") || line.trim().contains("INDIA") )
											{
												if(line.trim().contains("INDIA") && !description1.contains("INDIA"))
												{
													description1 = description1 +" "+ line.trim();
												}
												else
													description1 = description +" "+ line.trim();
											}
											else if (line.trim().equalsIgnoreCase("VISA A.P.") || line.trim().equalsIgnoreCase("ORIGINAL") )
											{
												continue;
											}
											else
											{
												description = line.trim();
											}
										}
										else
										{
											System.out.println(" ********** "+description1+ " ************** ");

											System.out.println("sub description is : "+line.substring(0, 37).trim());
											System.out.println("count is : "+line.substring(53, 63).trim());
											System.out.println("Interchange amount : "+line.substring(63, 84).trim());
											System.out.println("Interchange amount sign : "+line.substring(84, 87).trim());
											System.out.println("Reimbursement amount CR : "+line.substring(87,110).trim());
											System.out.println("Reimbursement amount DR : "+line.substring(110, line.length()).trim());
											
											ps130.setString(sr_no++, description1);
											ps130.setString(sr_no++, line.substring(0, 37).trim());
											ps130.setString(sr_no++, line.substring(53, 63).trim());
											ps130.setString(sr_no++, line.substring(63, 84).trim());
											ps130.setString(sr_no++, line.substring(84, 87).trim());
											ps130.setString(sr_no++, line.substring(87,110).trim());
											ps130.setString(sr_no++, line.substring(110, line.length()).trim());
											ps130.execute();
										}
									}
									else if(issuer)
									{
										if(line.substring(53, 132).trim().equalsIgnoreCase(""))
										{
											if (line.trim().equalsIgnoreCase("VISA A.P.") || line.trim().equalsIgnoreCase("ORIGINAL") )
											{
												continue;
											}
											else if(line.trim().contains("INDIA") )
											{
												if(line.trim().contains("INDIA"))
												{
													if(!description1.contains("INDIA"))
													{
														description1 = description1 +" "+ line.trim();
													}
													else if(description1.contains("NEPAL"))
													{
														description1 = description1.replace("NEPAL", "INDIA");
													}
													else
														description1 = description+" "+line.trim();

												}
												else
													description1 = description +" "+ line.trim();
											}
											else
											{
												description1 = line.trim();
											}
										}
										else
										{
											System.out.println(" ********** "+description1+ " ************** ");

											System.out.println("sub description is : "+line.substring(0, 39).trim());
											System.out.println("count is : "+line.substring(39, 63).trim());
											System.out.println("Interchange amount : "+line.substring(63, 84).trim());
											System.out.println("Interchange amount sign : "+line.substring(84, 87).trim());
											System.out.println("Reimbursement amount CR : "+line.substring(87,110).trim());
											System.out.println("Reimbursement amount DR : "+line.substring(110, line.length()).trim());
											
											ps130.setString(sr_no++, description1);
											ps130.setString(sr_no++, line.substring(0, 39).trim());
											ps130.setString(sr_no++, line.substring(39, 63).trim());
											ps130.setString(sr_no++, line.substring(63, 84).trim());
											ps130.setString(sr_no++, line.substring(84, 87).trim());
											ps130.setString(sr_no++, line.substring(87,110).trim());
											ps130.setString(sr_no++, line.substring(110, line.length()).trim());
											ps130.execute();

										}
									}
									else
									{
										if(line.substring(53, 132).trim().equalsIgnoreCase(""))
										{
											if (line.trim().equalsIgnoreCase("VISA A.P.") || line.trim().equalsIgnoreCase("ORIGINAL") )
											{
												continue;
											}
											else if(line.trim().contains("INDIA") )
											{
												if(line.trim().contains("INDIA"))
												{
													if(!description1.contains("INDIA"))
													{
														description1 = description1 +" "+ line.trim();
													}
													else if(description1.contains("NEPAL"))
													{
														description1 = description1.replace("NEPAL", "INDIA");
													}
													else
														description1 = description+" "+line.trim();

												}
												else
													description1 = description +" "+ line.trim();
											}
											else
											{
												description1 = description1 +" "+ line.trim();
											}
										}
										else
										{
											System.out.println(" ********** "+description1+ " ************** ");

											System.out.println("sub description is : "+line.substring(0, 39).trim());
											System.out.println("count is : "+line.substring(39, 63).trim());
											System.out.println("Interchange amount : "+line.substring(63, 84).trim());
											System.out.println("Interchange amount sign : "+line.substring(84, 87).trim());
											System.out.println("Reimbursement amount CR : "+line.substring(87,110).trim());
											System.out.println("Reimbursement amount DR : "+line.substring(110, line.length()).trim());
											
											ps130.setString(sr_no++, description1);
											ps130.setString(sr_no++, line.substring(0, 39).trim());
											ps130.setString(sr_no++, line.substring(39, 63).trim());
											ps130.setString(sr_no++, line.substring(63, 84).trim());
											ps130.setString(sr_no++, line.substring(84, 87).trim());
											ps130.setString(sr_no++, line.substring(87,110).trim());
											ps130.setString(sr_no++, line.substring(110, line.length()).trim());
											ps130.execute();

										}
									}
								}
							}
						}
					}
				}
				else
				{
					if(!vss_110 && !vss_120)
					{
						description= "";
						description1= "";
					}
				}
			}	
		
		}
		catch(Exception e)
		{
			System.out.println("Exception in reading visa file "+e);
		}
	
	}
	
	public static void readVSS110(String line,int sr_no , PreparedStatement ps)throws SQLException
	{
		if(line.substring(53, 132).trim().equalsIgnoreCase(""))
		{
			description = line.trim();
			
		}
		else
		{
			System.out.println(" ********** "+description+ " ************** ");
			System.out.println("Sub Description : "+line.substring(0, 37).trim());
			System.out.println("count : "+line.substring(38, 53).trim());
			System.out.println("credit amount : "+line.substring(53, 79).trim());
			System.out.println("Debit amount : "+line.substring(79, 104).trim());
			System.out.println("Total amount : "+line.substring(105, 129).trim());
			System.out.println("Sign : "+line.substring(129, 132).trim());
			
			System.out.println("sr no is "+sr_no);
			ps.setString(sr_no++ , description);
			ps.setString(sr_no++ , line.substring(0, 37).trim());
			ps.setString(sr_no++ , line.substring(38, 53).trim());
			ps.setString(sr_no++ , line.substring(53, 79).trim());
			ps.setString(sr_no++ , line.substring(79, 104).trim());
			ps.setString(sr_no++ , line.substring(105, 129).trim());
			System.out.println("sr no is "+sr_no);
			ps.setString(sr_no++ , line.substring(129, 132).trim());
			System.out.println("sr no is "+sr_no);
			
			ps.execute();
		}
		
		
	}
	public static void readVSS120(String line, int sr_no, PreparedStatement ps120)throws SQLException
	{
		
		if(line.substring(53, 132).trim().equalsIgnoreCase(""))
		{
			description = line.trim();
			//System.out.println(" ********** "+description+ " ************** ");
		}
		else
		{
			System.out.println(" ********** "+description+ " ************** ");
			vss120_record_no++;
			
			ps120.setString(sr_no++, description);
			
			if(line.substring(0, 37).trim().equalsIgnoreCase("ORIGINAL WITHDRAWAL"))
			{
				System.out.println("sub description is : "+line.substring(0, 37).trim());
				System.out.println("count is : "+line.substring(53, 69).trim());
				System.out.println("clearing amount : "+line.substring(69, 88).trim());
				System.out.println("clearing amount sign : "+line.substring(88, 91).trim());
				System.out.println("Interchange Value CR : "+line.substring(93,113).trim());
				
				System.out.println("sr_no is "+sr_no);
				ps120.setString(sr_no++ , line.substring(0, 37).trim());
				ps120.setString(sr_no++ , line.substring(53, 69).trim());
				ps120.setString(sr_no++ , line.substring(69, 88).trim());
				ps120.setString(sr_no++ , line.substring(88, 91).trim());
				ps120.setString(sr_no++ , line.substring(93,113).trim());
				ps120.setString(sr_no++ , "0");
				ps120.setInt(sr_no++ , vss120_record_no);
				System.out.println("sr_no is "+sr_no);
				ps120.execute();
				
			}
			else
			{
				System.out.println("sub description is : "+line.substring(0, 37).trim());
				System.out.println("count is : "+line.substring(53, 69).trim());
				System.out.println("clearing amount : "+line.substring(69, 88).trim());
				System.out.println("clearing amount sign : "+line.substring(88, 91).trim());
				System.out.println("Interchange Value CR : "+line.substring(93,113).trim());
				System.out.println("Interchange Value DR : "+line.substring(114, line.length()).trim());
				
				System.out.println("sr_no is "+sr_no);
				ps120.setString(sr_no++ , line.substring(0, 37).trim());
				ps120.setString(sr_no++ , line.substring(53, 69).trim());
				ps120.setString(sr_no++ , line.substring(69, 88).trim());
				ps120.setString(sr_no++ , line.substring(88, 91).trim());
				ps120.setString(sr_no++ , line.substring(93,113).trim());
				ps120.setString(sr_no++ , line.substring(114, line.length()).trim());
				ps120.setInt(sr_no++ , vss120_record_no);
				System.out.println("sr_no is "+sr_no);
				ps120.execute();
			}
		}
	
	}
	public static void readInternationalVSS120(String line, int sr_no, PreparedStatement ps120)throws SQLException
	{
		
		if(line.substring(53, 132).trim().equalsIgnoreCase(""))
		{
			description = line.trim();
			//System.out.println(" ********** "+description+ " ************** ");
		}
		else
		{
			System.out.println(" ********** "+description+ " ************** ");
			vss120_record_no++;
			
			ps120.setString(sr_no++, description);
			
			if(line.substring(0, 37).trim().equalsIgnoreCase("ORIGINAL WITHDRAWAL"))
			{
				System.out.println("sub description is : "+line.substring(0, 37).trim());
				System.out.println("count is : "+line.substring(53, 69).trim());
				System.out.println("clearing amount : "+line.substring(69, 87).trim());
				System.out.println("clearing amount sign : "+line.substring(87, 91).trim());
				System.out.println("Interchange Value CR : "+line.substring(93,113).trim());
				
				System.out.println("sr_no is "+sr_no);
				ps120.setString(sr_no++ , line.substring(0, 37).trim());
				ps120.setString(sr_no++ , line.substring(53, 69).trim());
				ps120.setString(sr_no++ , line.substring(69, 87).trim());
				ps120.setString(sr_no++ , line.substring(87, 91).trim());
				ps120.setString(sr_no++ , line.substring(93,113).trim());
				ps120.setString(sr_no++ , "0");
				ps120.setInt(sr_no++ , vss120_record_no);
				System.out.println("sr_no is "+sr_no);
				ps120.execute();
				
			}
			else
			{
				System.out.println("sub description is : "+line.substring(0, 37).trim());
				System.out.println("count is : "+line.substring(53, 69).trim());
				System.out.println("clearing amount : "+line.substring(69, 88).trim());
				System.out.println("clearing amount sign : "+line.substring(88, 91).trim());
				System.out.println("Interchange Value CR : "+line.substring(93,113).trim());
				System.out.println("Interchange Value DR : "+line.substring(114, line.length()).trim());
				
				System.out.println("sr_no is "+sr_no);
				ps120.setString(sr_no++ , line.substring(0, 37).trim());
				ps120.setString(sr_no++ , line.substring(53, 69).trim());
				ps120.setString(sr_no++ , line.substring(69, 87).trim());
				ps120.setString(sr_no++ , line.substring(87, 91).trim());
				ps120.setString(sr_no++ , line.substring(93,113).trim());
				ps120.setString(sr_no++ , line.substring(114, line.length()).trim());
				ps120.setInt(sr_no++ , vss120_record_no);
				System.out.println("sr_no is "+sr_no);
				ps120.execute();
			}
		}
	
	}
	public static void readVSS140(String line, int sr_no, PreparedStatement ps)throws SQLException
	{

		if(line.substring(53, line.length()).trim().equalsIgnoreCase(""))
		{
			description = line.trim();
			//System.out.println(" ********** "+description+ " ************** ");
		}
		else
		{
			System.out.println(" ********** "+description+ " ************** ");
			
				System.out.println("sub description is : "+line.substring(0, 60).trim());
				System.out.println("count is : "+line.substring(60, 68).trim());
				System.out.println("Interchange amount : "+line.substring(69, 87).trim());
				System.out.println("Interchange amount sign : "+line.substring(87, 91).trim());
				System.out.println("Visa Charges CR : "+line.substring(93,113).trim());
				System.out.println("Visa Charges DR : "+line.substring(114, line.length()).trim());
				
				ps.setString(sr_no++ , description);
				ps.setString(sr_no++ , line.substring(0, 60).trim());
				ps.setString(sr_no++ , line.substring(60, 68).trim());
				ps.setString(sr_no++ , line.substring(69, 87).trim());
				ps.setString(sr_no++ , line.substring(87, 91).trim());
				ps.setString(sr_no++ , line.substring(93,113).trim());
				ps.setString(sr_no++ , line.substring(114, line.length()).trim());
				ps.execute();
			
		}
	
	}
	public static void readVSS210(String line, int sr_no, PreparedStatement ps)throws SQLException
	{

		if(line.substring(53, line.length()).trim().equalsIgnoreCase("") && !line.startsWith("VISA INTERNATIONAL"))
		{
			description = line.trim();
			//System.out.println(" ********** "+description+ " ************** ");
		}
		else if(!line.startsWith("VISA INTERNATIONAL"))
		{
			System.out.println(" ********** "+description+ " ************** ");
			
				//System.out.println("sub description is : "+line.substring(0, 60).trim());
				System.out.println("sett_curr Interchange amount : "+line.substring(30, 49).trim());
				System.out.println("sett_curr Interchange amount sign : "+line.substring(49, 52).trim());
				System.out.println("sett_curr Conversion Fee : "+line.substring(53, 70).trim());
				System.out.println("clr curr Interchange amount : "+line.substring(71,91).trim());
				System.out.println("clr curr Interchange amount sign : "+line.substring(91, 94));
				System.out.println("clr curr Conversion Fee : "+line.substring(95, 111).trim());
				System.out.println("clr curr Opt Issuer Fee : "+line.substring(111, 129).trim());
				System.out.println("clr curr Opt Issuer Fee Sign : "+line.substring(129, line.length()).trim());
				
				System.out.println("sr_no is "+sr_no);
				ps.setString(sr_no++, description);
				ps.setString(sr_no++, line.substring(30, 49).trim());
				ps.setString(sr_no++, line.substring(49, 52).trim());
				ps.setString(sr_no++, line.substring(53, 70).trim());
				ps.setString(sr_no++, line.substring(71,91).trim());
				ps.setString(sr_no++, line.substring(91, 94).trim());
				ps.setString(sr_no++, line.substring(95, 111).trim());
				ps.setString(sr_no++, line.substring(111, 129).trim());
				System.out.println("sr_no is "+sr_no);
				ps.setString(sr_no++, line.substring(129, line.length()).trim());
				System.out.println("sr_no is "+sr_no);
				ps.execute();
			
		}
	
	}
}
