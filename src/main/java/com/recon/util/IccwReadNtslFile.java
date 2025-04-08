package com.recon.util;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.NFSSettlementBean;

public class IccwReadNtslFile {
	
	private static final Logger logger = Logger.getLogger(ReadDFSandJCBNTSLFile_bk.class);
	
	 public HashMap<String, Object> iccwfileupload(NFSSettlementBean beanObj,MultipartFile file,Connection con) 
			 throws SQLException {
		 
		 System.out.println("inside reading classsssss");
		 int response = 0;String tableName = null;
         int totalcount = 0;
         HashMap<String, Object> mapObj = new HashMap<String, Object>();
//         PreparedStatement pstmt = con.prepareStatement(getTableName);
//         pstmt.setString(1, beanObj.getFileName());
//         pstmt.setString(2, beanObj.getCategory());
//         pstmt.setString(3, beanObj.getStSubCategory());
//         ResultSet rs = pstmt.executeQuery();
         long start = System.currentTimeMillis();
         
         int bankCount = 0;
			  int count = 1,cellCount = 4;
			  String bankName= null;
			 String Ignoredescription = null;
			boolean idbiRecords = true;
		 String sql = "INSERT INTO ICCW_NTSL_RAWDATA (DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO) VALUES(?,?,?,?,?,to_date(?,'dd/mm/yyyy'),?,SYSDATE,?)";
         PreparedStatement ps = con.prepareStatement(sql);
         
         int srl_no = 1;
         try {
      	   Path tempDir = Files.createTempDirectory(""); 
       	  File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
       	  file.transferTo(tempFile);
       	  String content = Jsoup.parse(tempFile,"UTF-8").toString(); 
			  org.jsoup.nodes.Document html = Jsoup.parse(content);
			  System.out.println("query is:"+ sql );
			  if (content != null) 
			  { 
				  Elements contents = html.getElementsByTag("tbody");

				  System.out.println("********************** Reading tbody tags ****************");
				  
				  OUTER:  for(Element a : contents)
				  {
					  //code starts from here
					  Elements thContents = a.getElementsByTag("th");
					  Elements tdContents = a.getElementsByTag("td");
					  for(Element b : thContents)
					  {
						  if(b.text().startsWith("Daily Settlement Statement"))
						  {
							  // System.out.println(thContents.text());
							  bankName = b.text();
							  bankCount++;
						  }
						  /****** Reading main fields****************/
						  for(Element c : tdContents)
						  {
							  if(bankCount == 1 && idbiRecords)
							  {
								  // INSERT IN RAW TABLE
								  if(count == 1 && c.text().equalsIgnoreCase(""))
								  {
									  continue;
								  }
								  else 
								  {
									  if(count == 1)
									  {
										  if(c.text().equalsIgnoreCase(Ignoredescription))
										  {
											  idbiRecords = false;
										  }
										  else
										  {

											  if(totalcount == 0)
											  {
												  Ignoredescription = c.text();
												  ps.setString(count, c.text());
												  totalcount++;
											  }
											  else
											  {
												  ps.setString(count, c.text());
												  totalcount++;
											  }
											  count++;
										  }
									  }
									  else
									  {
										 ps.setString(count, c.text());
										 count++;
									  }

								  }

								  if(count == cellCount+1)
								  {
									  ps.setInt(5, beanObj.getCycle());
									  ps.setString(6, beanObj.getDatepicker());
									  ps.setString(7, beanObj.getCreatedBy());
									  ps.setInt(8, srl_no++);
									  ps.addBatch();
									  count = 1;
								  }

							  }
						  }
					  }



				  }
				// coop_ps.executeBatch();
				 ps.executeBatch();
               //con.commit();
               con.close();
               long end = System.currentTimeMillis();
              logger.info("start and end diff" + (start - end));
               mapObj.put("result", true);
               mapObj.put("count", totalcount);
			  }
			//delete the file from temp folder
			  FileUtils.forceDelete(tempFile);
				logger.info("File exists? "+tempFile.exists());
         } catch (Exception e) {
                e.printStackTrace();
                mapObj.put("result", false);
                mapObj.put("count", totalcount);
                try {
                      con.rollback();
                } catch (SQLException ex) {
                      ex.printStackTrace();
                }
         }
         return mapObj;
  }
}
