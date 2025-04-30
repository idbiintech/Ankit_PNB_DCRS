package com.recon.util;


import com.recon.model.NFSSettlementBean;
import com.recon.util.OracleConn;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.multipart.MultipartFile;

public class ReadNFSNTSLFile {
  @Autowired
  private JdbcTemplate getJdbcTemplate;
  
  private static final Logger logger = Logger.getLogger(com.recon.util.ReadNFSNTSLFile.class);
  
  public HashMap<String, Object> fileupload(NFSSettlementBean beanObj, MultipartFile file, Connection con) throws SQLException {
    int response = 0;
    String tableName = null;
    int totalcount = 0;
    HashMap<String, Object> mapObj = new HashMap<>();
    String getTableName = "SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILENAME = ? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?";
    PreparedStatement pstmt = con.prepareStatement(getTableName);
    pstmt.setString(1, beanObj.getFileName());
    pstmt.setString(2, beanObj.getCategory());
    pstmt.setString(3, beanObj.getStSubCategory());
    ResultSet rs = pstmt.executeQuery();
    long start = System.currentTimeMillis();
    int bankCount = 0;
    int count = 1, cellCount = 4;
    String bankName = null;
    String Ignoredescription = null;
    boolean idbiRecords = true;
    while (rs.next())
      tableName = rs.getString("TABLENAME"); 
    String sql = "INSERT INTO " + tableName + 
      "(DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,date_format(?,'%d/%m/Y%'),?,SYSDATE(),?,?,?)";
    logger.info("sql insert query " + sql);
    String CoopBank_sql = "INSERT INTO COOP_NTSL_NFS_RAWDATA (BANK_NAME,DESCRIPTION,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO) VALUES(?,?,?,?,?,date_format(?,'%d/%m/%Y'),?,SYSDATE(),?)";
    PreparedStatement coop_ps = con.prepareStatement(CoopBank_sql);
    PreparedStatement ps = con.prepareStatement(sql);
    int srl_no = 1;
    try {
      Path tempDir = Files.createTempDirectory("", (FileAttribute<?>[])new FileAttribute[0]);
      File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
      file.transferTo(tempFile);
      String content = Jsoup.parse(tempFile, "UTF-8").toString();
      Document html = Jsoup.parse(content);
      System.out.println("query is:" + sql + " " + beanObj.getFileName());
      if (content != null) {
        Elements contents = html.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags ****************");
        for (Element a : contents) {
          Elements thContents = a.getElementsByTag("th");
          Elements tdContents = a.getElementsByTag("td");
          for (Element b : thContents) {
            if (b.text().startsWith("Daily Settlement Statement")) {
              System.out.println(thContents.text());
              bankName = b.text();
              bankCount++;
            } 
            if (b.text().startsWith("Description"))
              for (Element c : tdContents) {
                if (idbiRecords) {
                  if (count == 1 && c.text().equalsIgnoreCase(""))
                    continue; 
                  if (count == 1) {
                    if (!c.text().equalsIgnoreCase(Ignoredescription)) {
                      if (totalcount == 0) {
                        Ignoredescription = c.text();
                        ps.setString(count, c.text());
                        totalcount++;
                      } else {
                        ps.setString(count, c.text());
                        totalcount++;
                      } 
                      count++;
                    } 
                  } else {
                    ps.setString(count, c.text());
                    count++;
                  } 
                  if (count == cellCount + 1) {
                    ps.setInt(5, beanObj.getCycle());
                    ps.setString(6, beanObj.getDatepicker());
                    ps.setString(7, beanObj.getCreatedBy());
                    ps.setInt(8, srl_no++);
                    ps.setString(9, bankName);
                    ps.setString(10, file.getOriginalFilename());
                    ps.addBatch();
                    count = 1;
                  } 
                } 
              }  
          } 
        } 
        coop_ps.executeBatch();
        ps.executeBatch();
        con.close();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcount));
      } 
      FileUtils.forceDelete(tempFile);
      logger.info("File exists? " + tempFile.exists());
    } catch (Exception e) {
      e.printStackTrace();
      mapObj.put("result", Boolean.valueOf(false));
      mapObj.put("count", Integer.valueOf(totalcount));
      try {
        con.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      } 
    } 
    return mapObj;
  }
  
  public HashMap<String, Object> iccwfileupload(NFSSettlementBean beanObj, MultipartFile file, Connection con) throws SQLException {
    String cycles = " ";
    int cy = Integer.parseInt(cycles);
    int totalcountforDATA2 = 0;
    int response = 0;
    String tableName = null;
    int totalcount = 0;
    HashMap<String, Object> mapObj = new HashMap<>();
    long start = System.currentTimeMillis();
    int bankCount = 0;
    int count = 1, cellCount = 4;
    String bankName = null;
    String Ignoredescription = null;
    boolean idbiRecords = true;
    String sql = "INSERT INTO  ICCW_NTSL_RAWDATA (DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO) VALUES(?,?,?,?,?,date_format(?,'dd-mm-yy'),?,SYSDATE,?)";
    PreparedStatement ps = con.prepareStatement(sql);
    int srl_no = 1;
    try {
      Path tempDir = Files.createTempDirectory("", (FileAttribute<?>[])new FileAttribute[0]);
      File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
      file.transferTo(tempFile);
      String content = Jsoup.parse(tempFile, "UTF-8").toString();
      Document html = Jsoup.parse(content);
      System.out.println("query is:" + sql);
      if (content != null) {
        Elements contents = html.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags ****************");
        for (Element a : contents) {
          Elements thContents = a.getElementsByTag("th");
          Elements tdContents = a.getElementsByTag("td");
          for (Element b : thContents) {
            if (b.text().startsWith("Daily Settlement Statement")) {
              System.out.println(thContents.text());
              bankName = b.text();
              bankCount++;
            } 
            for (Element c : tdContents) {
              if (c.text().contains("Description") || c.text().contains("Ref. No") || 
                c.text().contains("Debit") || c.text().contains("Credit"))
                continue; 
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && c.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (c.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = c.text();
                      ps.setString(count, c.text());
                      totalcount++;
                    } else {
                      ps.setString(count, c.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps.setString(count, c.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps.setInt(5, cy);
                  ps.setString(6, "01-08-24");
                  ps.setString(7, "01-08-24");
                  ps.setInt(8, srl_no++);
                  ps.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(count));
      } 
      FileUtils.forceDelete(tempFile);
      logger.info("File exists? " + tempFile.exists());
    } catch (Exception e) {
      e.printStackTrace();
      mapObj.put("result", Boolean.valueOf(false));
      mapObj.put("count", Integer.valueOf(count));
      try {
        con.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      } 
    } 
    return mapObj;
  }
  
  public HashMap<String, Object> fileupload1(NFSSettlementBean beanObj, MultipartFile file, Connection con) throws SQLException {
	    int response = 0;
	    String tableName = null;
	    int totalcount = 0, totalcountb = 0, countf = 0;
	    int countfTD = 0;
	    HashMap<String, Object> mapObj = new HashMap<>();
	    long start = System.currentTimeMillis();
	    int bankCount = 0, bankCount2 = 0, bankCount3 = 0;
	    int count = 1, cellCount = 4, countb = 1;
	    String bankName = null;
	    String Ignoredescription = null, Ignoredescription2 = null, discriptionF = null, discriptionF2 = null;
	    String AdjSubTotal = null, noOfTxn = null, AdjCreditAmountD = null, AdjCreditAmountC = null;
	    String finalAmount2 = null, finalAmount = null;
	    boolean idbiRecords = true, idbiRecords2 = true;
	    String sql1 = "";
	    if (beanObj.getFileName().contains("ICD")) {
	      sql1 = "INSERT INTO ntsl_icd_rawdata (DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
	      logger.info("sql insert query " + sql1);
	      sql1 = "INSERT INTO ntsl_icd_rawdata(DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
	    } else if (beanObj.getFileName().contains("NTSL-DFS")) {
	      sql1 = "INSERT INTO ntsl_dfs_rawdata (DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
	      logger.info("sql insert query " + sql1);
	      sql1 = "INSERT INTO ntsl_dfs_rawdata(DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
	    } else if (beanObj.getFileName().contains("NTSL-JCB")) {
	      sql1 = "INSERT INTO ntsl_jcb_rawdata (DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
	      logger.info("sql insert query " + sql1);
	      sql1 = "INSERT INTO ntsl_jcb_rawdata(DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
	    } else {
	      sql1 = "INSERT INTO ntsl_nfs_rawdata (DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
	      logger.info("sql insert query " + sql1);
	      sql1 = "INSERT INTO ntsl_nfs_rawdata(DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,?,?,?,?)";
	    } 
	    int srl_no = 1;
	    try {
	      OracleConn oracleConn = new OracleConn();
	      Connection conn = oracleConn.getconn();
	      PreparedStatement ps = conn.prepareStatement(sql1);
	      PreparedStatement psF = conn.prepareStatement(sql1);
	      PreparedStatement ps2 = conn.prepareStatement(sql1);
	      Path tempDir = Files.createTempDirectory("", (FileAttribute<?>[])new FileAttribute[0]);
			File tempFile = File.createTempFile("upload-", ".html");
			file.transferTo(tempFile);
			String bankname ="";

			Document doc = Jsoup.parse(tempFile, "UTF-8");
			Elements rows = doc.select("tbody tr");

			for (Element row : rows) {
				Elements cells2 = row.select("th");
				Elements cells = row.select("td");

				if (cells2.text().contains("Daily Settlement")) {
					bankname = cells2.text();
					//System.out.println("bankname " + bankname);
				}
				//System.out.println("ss " + cells2.text());
				if (cells.size() >= 4) {
					count++;
					//System.out.println("cells.get(0).text() " + cells.get(0).text());
			
					ps.setString(1,cells.get(0).text());
					ps.setString(2,cells.get(1).text());
					ps.setString(3,cells.get(2).text());
					ps.setString(4,cells.get(3).text());
					ps.setString(5,String.valueOf(beanObj.getCycle()));
					ps.setString(6,beanObj.getDatepicker());
					ps.setString(7,"INT12016");
					ps.setString(8,beanObj.getDatepicker());
					ps.setString(9,String.valueOf(count));
					ps.setString(10,bankname); // TODO: extract from file
					ps.setString(11,file.getOriginalFilename());
					ps.addBatch();
					
				}
			}
	        ps.executeBatch();
	   
	        long end = System.currentTimeMillis();
	        logger.info("start and end diff" + (start - end));
	        mapObj.put("result", Boolean.valueOf(true));
	        mapObj.put("count", Integer.valueOf(count));
	      
	  
	      bankCount = 0;
	      idbiRecords = true;
	      Ignoredescription = null;
	      totalcount = 0;
	      FileUtils.forceDelete(tempFile);
	      logger.info("File exists? " + tempFile.exists());
	    } catch (Exception e) {
	      e.printStackTrace();
	      mapObj.put("result", Boolean.valueOf(false));
	      mapObj.put("count", Integer.valueOf(count));
	      try {
	        con.rollback();
	      } catch (SQLException ex) {
	        ex.printStackTrace();
	      } 
	    } 
	    return mapObj;
	  }
  public HashMap<String, Object> fileuploadDFS(NFSSettlementBean beanObj, MultipartFile file, Connection con) throws SQLException {
    int response = 0;
    String tableName = null;
    int totalcount = 0, totalcountb = 0, countf = 0, loopCount = 0;
    int countfTD = 0;
    HashMap<String, Object> mapObj = new HashMap<>();
    long start = System.currentTimeMillis();
    int bankCount = 0, bankCount2 = 0, bankCount3 = 0;
    int count = 1, cellCount = 4, countb = 1;
    String bankName = null;
    String Ignoredescription = null, Ignoredescription2 = null, discriptionF = null, discriptionF2 = null;
    String AdjSubTotal = null, noOfTxn = null, AdjCreditAmountD = null, AdjCreditAmountC = null;
    String finalAmount2 = null, finalAmount = null;
    boolean idbiRecords = true, idbiRecords2 = true;
    String sql1 = "";
    if (beanObj.getFileName().contains("NTSL-DFS")) {
      sql1 = "INSERT INTO ntsl_dfs_rawdata (DESCRIPTIONS,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
      logger.info("sql insert query " + sql1);
      sql1 = "INSERT INTO ntsl_dfs_rawdata(DESCRIPTIONS,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
    } else {
      sql1 = "INSERT INTO ntsl_nfs_rawdata (DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,ORG_FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
      logger.info("sql insert query " + sql1);
      sql1 = "INSERT INTO ntsl_nfs_rawdata(DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,ORG_FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
    } 
    PreparedStatement ps = con.prepareStatement(sql1);
    PreparedStatement psF = con.prepareStatement(sql1);
    PreparedStatement ps2 = con.prepareStatement(sql1);
    int srl_no = 1;
    try {
      Path tempDir = Files.createTempDirectory("", (FileAttribute<?>[])new FileAttribute[0]);
      File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
      file.transferTo(tempFile);
      String content = Jsoup.parse(tempFile, "UTF-8").toString();
      int adjcount = 0, adjcount3 = 0, adjcount2 = 0, addbatchcount = 0, addbatchcount2 = 0, addbatchcount3 = 0;
      int addbatchcount4 = 0;
      Document html = Jsoup.parse(content);
      if (content != null) {
        Elements contents = html.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags ****************");
        for (Element a : contents) {
          Elements thContents = a.getElementsByTag("th");
          Elements tdContents = a.getElementsByTag("td");
          for (Element b : thContents) {
            if (b.text().startsWith("Daily Settlement Statement")) {
              System.out.println(thContents.text());
              bankName = b.text();
              bankCount++;
            } 
            if (b.text().startsWith("Final Settlement Amount Including Sub-Member Bank") || 
              b.text().startsWith("Final Settlement Amount Including Sub Member Bank"))
              bankCount2++; 
            System.out.println("a d " + b.text() + " count " + countf);
            if (countf == 30)
              discriptionF = b.text(); 
            if (countf == 33)
              finalAmount = b.text(); 
            if (countf == 33 && 
              discriptionF != null) {
              psF.setString(1, discriptionF);
              psF.setString(2, "");
              System.out.println("finalAmount " + finalAmount + " " + b.text() + " bi " + finalAmount + 
                  " " + discriptionF);
              if (!discriptionF.equalsIgnoreCase("Description")) {
                psF.setString(3, "");
                psF.setString(4, finalAmount);
                psF.setInt(5, beanObj.getCycle());
                psF.setString(6, beanObj.getDatepicker());
                psF.setString(7, beanObj.getCreatedBy());
                psF.setInt(8, srl_no++);
                psF.setString(9, bankName);
                psF.setString(10, file.getOriginalFilename());
                psF.addBatch();
                psF.executeBatch();
                noOfTxn = "";
                AdjCreditAmountD = "";
                AdjCreditAmountC = "";
              } 
            } 
            countf++;
            System.out.println("loopCount " + loopCount);
            for (Element c : tdContents) {
              if (c.text().contains("Description") || c.text().contains("Ref. No") || 
                c.text().contains("Debit") || c.text().contains("Credit"))
                continue; 
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && c.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (c.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = c.text();
                      ps.setString(count, c.text());
                      totalcount++;
                    } else {
                      ps.setString(count, c.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps.setString(count, c.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps.setInt(5, 2);
                  ps.setString(6, beanObj.getDatepicker());
                  ps.setString(7, beanObj.getCreatedBy());
                  ps.setInt(8, srl_no++);
                  ps.setString(9, bankName);
                  ps.setString(10, file.getOriginalFilename());
                  ps.addBatch();
                  count = 1;
                } 
              } 
            } 
            loopCount++;
          } 
          ps.executeBatch();
          long end = System.currentTimeMillis();
          logger.info("start and end diff" + (start - end));
          mapObj.put("result", Boolean.valueOf(true));
          mapObj.put("count", Integer.valueOf(totalcount));
        } 
        int totalcountforDATA = totalcount;
        bankCount = 0;
        idbiRecords = true;
        Ignoredescription = null;
        totalcount = 0;
        FileUtils.forceDelete(tempFile);
        logger.info("File exists? " + tempFile.exists());
      } 
    } catch (Exception e) {
      e.printStackTrace();
      mapObj.put("result", Boolean.valueOf(false));
      mapObj.put("count", Integer.valueOf(totalcount));
      try {
        con.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      } 
    } 
    return mapObj;
  }
  
  public HashMap<String, Object> fileuploadJCB(NFSSettlementBean beanObj, MultipartFile file, Connection con) throws SQLException {
    int response = 0;
    String tableName = null;
    int totalcount = 0, totalcountb = 0, countf = 0;
    int countfTD = 0;
    HashMap<String, Object> mapObj = new HashMap<>();
    long start = System.currentTimeMillis();
    int bankCount = 0, bankCount2 = 0, bankCount3 = 0;
    int count = 1, cellCount = 4, countb = 1;
    String bankName = null;
    String Ignoredescription = null, Ignoredescription2 = null, discriptionF = null, discriptionF2 = null;
    String AdjSubTotal = null, noOfTxn = null, AdjCreditAmountD = null, AdjCreditAmountC = null;
    String finalAmount2 = null, finalAmount = null;
    boolean idbiRecords = true, idbiRecords2 = true;
    String sql1 = "";
    if (beanObj.getFileName().contains("NTSL-JCB")) {
      sql1 = "INSERT INTO ntsl_jcb_rawdata (DESCRIPTIONS,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
      logger.info("sql insert query " + sql1);
      sql1 = "INSERT INTO ntsl_jcb_rawdata(DESCRIPTIONS,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
    } else {
      sql1 = "INSERT INTO ntsl_nfs_rawdata (DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
      logger.info("sql insert query " + sql1);
      sql1 = "INSERT INTO ntsl_nfs_rawdata(DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
    } 
    PreparedStatement ps = con.prepareStatement(sql1);
    PreparedStatement psF = con.prepareStatement(sql1);
    PreparedStatement ps2 = con.prepareStatement(sql1);
    int srl_no = 1;
    try {
      Path tempDir = Files.createTempDirectory("", (FileAttribute<?>[])new FileAttribute[0]);
      File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
      file.transferTo(tempFile);
      String content = Jsoup.parse(tempFile, "UTF-8").toString();
      int adjcount = 0, adjcount3 = 0, adjcount2 = 0, addbatchcount = 0, addbatchcount2 = 0, addbatchcount3 = 0;
      int addbatchcount4 = 0;
      Document html = Jsoup.parse(content);
      if (content != null) {
        Elements contents = html.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags ****************");
        for (Element a : contents) {
          Elements thContents = a.getElementsByTag("th");
          Elements tdContents = a.getElementsByTag("td");
          for (Element b : thContents) {
            if (b.text().startsWith("Daily Settlement Statement")) {
              System.out.println(thContents.text());
              bankName = b.text();
              bankCount++;
            } 
            if (b.text().startsWith("Final Settlement Amount Including Sub-Member Bank") || 
              b.text().startsWith("Final Settlement Amount Including Sub Member Bank"))
              bankCount2++; 
            if (countf == 30)
              discriptionF = b.text(); 
            if (countf == 33)
              finalAmount = b.text(); 
            if (countf == 33 && 
              discriptionF != null) {
              psF.setString(1, discriptionF);
              psF.setString(2, "");
              System.out.println("finalAmount " + finalAmount + " " + b.text() + " bi " + finalAmount + 
                  " " + discriptionF);
              if (!discriptionF.equalsIgnoreCase("Description")) {
                psF.setString(3, "");
                psF.setString(4, finalAmount);
                psF.setInt(5, beanObj.getCycle());
                psF.setString(6, beanObj.getDatepicker());
                psF.setString(7, beanObj.getCreatedBy());
                psF.setInt(8, srl_no++);
                psF.setString(9, bankName);
                psF.setString(10, file.getOriginalFilename());
                psF.addBatch();
                psF.executeBatch();
                noOfTxn = "";
                AdjCreditAmountD = "";
                AdjCreditAmountC = "";
              } 
            } 
            countf++;
            for (Element c : tdContents) {
              if (c.text().contains("Description") || c.text().contains("Ref. No") || 
                c.text().contains("Debit") || c.text().contains("Credit"))
                continue; 
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && c.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (c.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = c.text();
                      ps.setString(count, c.text());
                      totalcount++;
                    } else {
                      ps.setString(count, c.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps.setString(count, c.text());
                  Ignoredescription = c.text();
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps.setInt(5, 2);
                  ps.setString(6, beanObj.getDatepicker());
                  ps.setString(7, beanObj.getCreatedBy());
                  ps.setInt(8, srl_no++);
                  ps.setString(9, bankName);
                  ps.setString(10, file.getOriginalFilename());
                  ps.addBatch();
                  count = 1;
                } 
              } 
            } 
            idbiRecords = true;
          } 
        } 
        ps.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcount));
      } 
      int totalcountforDATA = totalcount;
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      FileUtils.forceDelete(tempFile);
      logger.info("File exists? " + tempFile.exists());
    } catch (Exception e) {
      e.printStackTrace();
      mapObj.put("result", Boolean.valueOf(false));
      mapObj.put("count", Integer.valueOf(totalcount));
      try {
        con.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      } 
    } 
    return mapObj;
  }
  
  public HashMap<String, Object> fileuploadICD(NFSSettlementBean beanObj, MultipartFile file, Connection con) throws SQLException {
    int response = 0;
    String tableName = null;
    int totalcount = 0, totalcountb = 0, countf = 0;
    int countfTD = 0;
    HashMap<String, Object> mapObj = new HashMap<>();
    long start = System.currentTimeMillis();
    int bankCount = 0, bankCount2 = 0, bankCount3 = 0;
    int count = 1, cellCount = 4, countb = 1;
    String bankName = null;
    String Ignoredescription = null, Ignoredescription2 = null, discriptionF = null, discriptionF2 = null;
    String AdjSubTotal = null, noOfTxn = null, AdjCreditAmountD = null, AdjCreditAmountC = null;
    String finalAmount2 = null, finalAmount = null;
    boolean idbiRecords = true, idbiRecords2 = true;
    String sql1 = "";
    if (beanObj.getFileName().contains("ICD")) {
      sql1 = "INSERT INTO ntsl_icd_awdata (DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
      logger.info("sql insert query " + sql1);
      sql1 = "INSERT INTO ntsl_icd_rawdata(DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
    } else {
      sql1 = "INSERT INTO ntsl_nfs_rawdata (DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
      logger.info("sql insert query " + sql1);
      sql1 = "INSERT INTO ntsl_nfs_rawdata(DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,date_format(?,'dd-mm-yy'),?,SYSDATE(),?,?,?)";
    } 
   

    PreparedStatement ps = con.prepareStatement(sql1);
    PreparedStatement psF = con.prepareStatement(sql1);
    PreparedStatement ps2 = con.prepareStatement(sql1);
    int srl_no = 1;
    try {
      Path tempDir = Files.createTempDirectory("", (FileAttribute<?>[])new FileAttribute[0]);
      File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
      file.transferTo(tempFile);
      String content = Jsoup.parse(tempFile, "UTF-8").toString();
      int adjcount = 0, adjcount3 = 0, adjcount2 = 0, addbatchcount = 0, addbatchcount2 = 0, addbatchcount3 = 0;
      int addbatchcount4 = 0;
      Document html = Jsoup.parse(content);
      if (content != null) {
        Elements contents = html.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags ****************");
        for (Element a : contents) {
          Elements thContents = a.getElementsByTag("th");
          Elements tdContents = a.getElementsByTag("td");
          for (Element b : thContents) {
            if (b.text().startsWith("Daily Settlement Statement")) {
              System.out.println(thContents.text());
              bankName = b.text();
              bankCount++;
            } 
            if (b.text().startsWith("Final Settlement Amount Including Sub-Member Bank") || 
              b.text().startsWith("Final Settlement Amount Including Sub Member Bank"))
              bankCount2++; 
            System.out.println("a d " + b.text() + " count " + countf);
            if (countf == 30)
              discriptionF = b.text(); 
            if (countf == 33)
              finalAmount = b.text(); 
            if (countf == 33 && 
              discriptionF != null) {
              psF.setString(1, discriptionF);
              psF.setString(2, "");
              System.out.println("finalAmount " + finalAmount + " " + b.text() + " bi " + finalAmount + 
                  " " + discriptionF);
              if (!discriptionF.equalsIgnoreCase("Description")) {
                psF.setString(3, "");
                psF.setString(4, finalAmount);
                psF.setInt(5, beanObj.getCycle());
                psF.setString(6, beanObj.getDatepicker());
                psF.setString(7, beanObj.getCreatedBy());
                psF.setInt(8, srl_no++);
                psF.setString(9, bankName);
                psF.setString(10, file.getOriginalFilename());
                psF.addBatch();
                psF.executeBatch();
                noOfTxn = "";
                AdjCreditAmountD = "";
                AdjCreditAmountC = "";
              } 
            } 
            countf++;
            for (Element c : tdContents) {
              if (c.text().contains("Description") || c.text().contains("Ref. No") || 
                c.text().contains("Debit") || c.text().contains("Credit"))
                continue; 
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && c.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (c.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = c.text();
                      ps.setString(count, c.text());
                      totalcount++;
                    } else {
                      ps.setString(count, c.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps.setString(count, c.text());
                  Ignoredescription = c.text();
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps.setInt(5, 2);
                  ps.setString(6, beanObj.getDatepicker());
                  ps.setString(7, beanObj.getCreatedBy());
                  ps.setInt(8, srl_no++);
                  ps.setString(9, bankName);
                  ps.setString(10, file.getOriginalFilename());
                  ps.addBatch();
                  count = 1;
                } 
              } 
            } 
            idbiRecords = true;
          } 
        } 
        ps.executeBatch();
   
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcount));
      } 
      int totalcountforDATA = totalcount;
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      FileUtils.forceDelete(tempFile);
      logger.info("File exists? " + tempFile.exists());
    } catch (Exception e) {
      e.printStackTrace();
      mapObj.put("result", Boolean.valueOf(false));
      mapObj.put("count", Integer.valueOf(totalcount));
      try {
        con.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      } 
    } 
    return mapObj;
  }
}
