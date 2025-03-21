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
      sql1 = "INSERT INTO ntsl_nfs_rawdata(DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO,BANKNAME,FILENAME) VALUES(?,?,?,?,?,STR_to_date(?,'%Y/%m/%d'),?,SYSDATE(),?,?,?)";
    } 
    int srl_no = 1;
    try {
      OracleConn oracleConn = new OracleConn();
      Connection conn = oracleConn.getconn();
      PreparedStatement ps = conn.prepareStatement(sql1);
      PreparedStatement psF = conn.prepareStatement(sql1);
      PreparedStatement ps2 = conn.prepareStatement(sql1);
      Path tempDir = Files.createTempDirectory("", (FileAttribute<?>[])new FileAttribute[0]);
      File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
      file.transferTo(tempFile);
      String content = Jsoup.parse(tempFile, "UTF-8").toString();
      String content2 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content3 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content4 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content5 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content6 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content7 = Jsoup.parse(tempFile, "UTF-8").toString();
      System.out.println("");
      String content8 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content9 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content10 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content11 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content12 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content13 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content14 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content15 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content16 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content17 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content18 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content19 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content20 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content21 = Jsoup.parse(tempFile, "UTF-8").toString();
      String content22 = Jsoup.parse(tempFile, "UTF-8").toString();
      int adjcount = 0, adjcount3 = 0, adjcount2 = 0, addbatchcount = 0, addbatchcount2 = 0, addbatchcount3 = 0;
      int addbatchcount4 = 0;
      Document html = Jsoup.parse(content);
      Document html2 = Jsoup.parse(content2);
      Document html3 = Jsoup.parse(content3);
      Document html4 = Jsoup.parse(content4);
      Document html5 = Jsoup.parse(content5);
      Document html6 = Jsoup.parse(content6);
      Document html7 = Jsoup.parse(content7);
      Document html8 = Jsoup.parse(content8);
      Document html9 = Jsoup.parse(content9);
      Document html10 = Jsoup.parse(content10);
      Document html11 = Jsoup.parse(content11);
      Document html12 = Jsoup.parse(content12);
      Document html13 = Jsoup.parse(content13);
      Document html14 = Jsoup.parse(content14);
      Document html15 = Jsoup.parse(content15);
      Document html16 = Jsoup.parse(content16);
      Document html17 = Jsoup.parse(content17);
      Document html18 = Jsoup.parse(content18);
      Document html19 = Jsoup.parse(content19);
      Document html20 = Jsoup.parse(content20);
      Document html21 = Jsoup.parse(content21);
      Document html22 = Jsoup.parse(content22);
      if (content != null) {
        Elements contents = html.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags ****************");
        System.out.println("All data " + contents.toString() + "html.getElementsByTag(\"tbody\") " + html.getElementsByTag("tbody"));
        for (Element a : contents) {
          Elements thContents = a.getElementsByTag("th");
          Elements tdContents = a.getElementsByTag("td");
          for (Element b : thContents) {
            if (b.text().startsWith("Daily Settlement Statement")) {
              bankName = b.text();
              bankCount++;
            } 
            if (b.text().startsWith("Dispute Adjustments"))
              bankCount2++; 
            if (b.text().startsWith("Final Settlement Amount Including Sub-Member Bank") || 
              b.text().startsWith("Final Settlement Amount Including Sub Member Bank"))
              bankCount2++; 
            if (b.text().startsWith("Description")) {
              System.out.println(thContents.text());
              bankCount3++;
            } 
            if (b.text().startsWith("Summary For Micro ATM") || 
              b.text().contains("Summary For Micro ATM"))
              break; 
            System.out.println("a d " + b.text() + " count " + countf);
            if (countf == 27) {
              discriptionF = b.text();
              System.out.println("discriptionF " + discriptionF + 
                  " " + b.text());
            } 
            if (countf == 7)
              finalAmount = b.text(); 
            if (countf == 176)
              discriptionF = b.text(); 
            if (countf == 178)
              finalAmount = b.text(); 
            if (countf == 144)
              discriptionF = b.text(); 
            if (countf == 178)
              finalAmount = b.text(); 
            if (countf == 14)
              AdjSubTotal = b.text(); 
            if (countf == 15)
              noOfTxn = b.text(); 
            if (countf == 16)
              AdjCreditAmountD = b.text(); 
            if (countf == 17)
              AdjCreditAmountC = b.text(); 
            if (AdjSubTotal != null)
              if (countf == 17) {
                System.out.println("Totaljnjn " + AdjCreditAmountC + " " + AdjCreditAmountD);
                if (addbatchcount2 == 0 && 
                  AdjCreditAmountD != "Description") {
                  psF.setString(1, "Adjustment Sub Totals");
                  psF.setString(2, noOfTxn);
                  psF.setString(3, AdjCreditAmountD);
                  psF.setString(4, AdjCreditAmountC);
                  psF.setInt(5, beanObj.getCycle());
                  psF.setString(6, beanObj.getDatepicker());
                  psF.setString(7, beanObj.getCreatedBy());
                  psF.setInt(8, srl_no++);
                  psF.setString(9, bankName);
                  psF.setString(10, file.getOriginalFilename());
                  psF.addBatch();
                  psF.executeBatch();
                  AdjSubTotal = "";
                  noOfTxn = "";
                  AdjCreditAmountD = "";
                  AdjCreditAmountC = "";
                  addbatchcount2++;
                } 
              }  
            if ((countf == 178 || countf == 27) && 
              discriptionF != null) {
              psF.setString(1, discriptionF);
              psF.setString(2, "");
              System.out.println("finalAmount " + finalAmount + " " + b.text() + " bi " + 
                  finalAmount);
              psF.setString(3, finalAmount);
              psF.setString(4, "");
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
            countf++;
            for (Element c : tdContents) {
              if (bankCount == 1 && (bankCount2 == 1 || bankCount2 == 2) && idbiRecords) {
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
      if (content2 != null) {
        Elements contents = html2.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents2 = x.getElementsByTag("th");
          Elements tdContents2 = x.getElementsByTag("td");
          for (Element y : thContents2) {
            if (y.text().startsWith("Daily Settlement Statement")) {
              bankName = y.text();
              bankCount++;
            } 
            if (y.text().startsWith("Dispute Adjustments"))
              bankCount2++; 
            if (y.text().startsWith("Summary For Micro ATM") || 
              y.text().contains("Summary For Micro ATM"))
              break; 
            for (Element z : tdContents2) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && z.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (z.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = z.text();
                      ps2.setString(count, z.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, z.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, z.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA + totalcount));
      } 
      int totalcountforDATA2 = totalcount;
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content3 != null) {
        Elements contents = html3.getElementsByTag("tbody");
        for (Element x : contents) {
          Elements thContents3 = x.getElementsByTag("th");
          Elements tdContents3 = x.getElementsByTag("td");
          for (Element e : thContents3) {
            if (e.text().startsWith(
                "Daily Settlement Statement for Punjab National Bank")) {
              bankName = e.text();
              bankCount++;
            } 
            if (e.text().startsWith("Dispute Adjustments"))
              bankCount2++; 
            if (e.text().startsWith("Summary For Micro ATM") || 
              e.text().contains("Summary For Micro ATM"))
              break; 
            for (Element f : tdContents3) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && f.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (f.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = f.text();
                      ps2.setString(count, f.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, f.text());
                      System.out.println("f " + f.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, f.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      int totalcountforDATA3 = totalcount;
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content4 != null) {
        Elements contents = html4.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents4 = x.getElementsByTag("th");
          Elements tdContents4 = x.getElementsByTag("td");
          for (Element g : thContents4) {
            if (g.text().startsWith("Daily Settlement Statement for Punjab National Bank UBN (Erstwhile United Bank Of India)")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Dispute Adjustments"))
              bankCount2++; 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents4) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content4 != null) {
        Elements contents = html4.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents4 = x.getElementsByTag("th");
          Elements tdContents4 = x.getElementsByTag("td");
          for (Element g : thContents4) {
            if (g.text().startsWith(
                "Daily Settlement Statement for India Post Payments Bank Limited-IPO")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Dispute Adjustments"))
              bankCount2++; 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents4) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  h.text();
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content5 != null) {
        Elements contents = html5.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents5 = x.getElementsByTag("th");
          Elements tdContents5 = x.getElementsByTag("td");
          for (Element g : thContents5) {
            if (g.text().startsWith("Daily Settlement Statement for Tripura Gramin Bank")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Dispute Adjustments"))
              bankCount2++; 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents5) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content6 != null) {
        Elements contents = html6.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents6 = x.getElementsByTag("th");
          Elements tdContents6 = x.getElementsByTag("td");
          for (Element g : thContents6) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Manipur Rural Bank")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Dispute Adjustments"))
              bankCount2++; 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents6) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content7 != null) {
        Elements contents = html7.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents7 = x.getElementsByTag("th");
          Elements tdContents7 = x.getElementsByTag("td");
          for (Element g : thContents7) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Punjab National Bank OBT (Erstwhile Oriental Bank Of commerce)")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Dispute Adjustments"))
              bankCount2++; 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents7) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content8 != null) {
        Elements contents = html8.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Dakshin Bihar Gramin Bank (Erstwhile Madhya Bihar Gramin Bank)")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      int finalcount = 0;
      if (content9 != null) {
        Elements contents = html9.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element j : contents) {
          Elements thContents9 = j.getElementsByTag("th");
          Elements tdContents9 = j.getElementsByTag("td");
          for (Element l : thContents9) {
            if (l.text().startsWith("Final Settlement Amount Including Sub-Member Bank"))
              if (bankCount == 0) {
                bankName = l.text();
                bankCount++;
              } else {
                break;
              }  
            if (l.text().startsWith("Dispute Adjustments"))
              bankCount2++; 
            if (l.text().startsWith("Summary For Micro ATM") || 
              l.text().contains("Summary For Micro ATM"))
              break; 
            for (Element m : tdContents9) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && m.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (m.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = m.text();
                      if (bankName.equalsIgnoreCase(
                          "Final Settlement Amount Including Sub-Member Bank") && 
                        finalcount == 0)
                        ps2.setString(count, bankName); 
                      totalcount++;
                    } else {
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  if (bankName.equalsIgnoreCase(
                      "Final Settlement Amount Including Sub-Member Bank") && 
                    finalcount == 0)
                    ps2.setString(count, m.text()); 
                  count++;
                } 
                if (count == cellCount + 1) {
                  if (bankName.equalsIgnoreCase("Final Settlement Amount Including Sub-Member Bank") && 
                    finalcount == 0) {
                    ps2.setInt(5, beanObj.getCycle());
                    ps2.setString(6, beanObj.getDatepicker());
                    ps2.setString(7, beanObj.getCreatedBy());
                    ps2.setInt(8, srl_no++);
                    ps2.setString(9, "");
                    ps2.setString(10, file.getOriginalFilename());
                    ps2.addBatch();
                    finalcount++;
                  } 
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content10 != null) {
        Elements contents = html10.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Punjab National Bank (Erstwhile Oriental Bank Of commerce)")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content11 != null) {
        Elements contents = html11.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Punjab National Bank Acquirer")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content12 != null) {
        Elements contents = html12.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Punjab National Bank (Erstwhile United Bank Of India)")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content13 != null) {
        Elements contents = html13.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Punjab National Bank (Erstwhile United Bank Of India)")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content14 != null) {
        Elements contents = html14.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Punjab National Bank - Credit Card")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content15 != null) {
        Elements contents = html15.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Prathama U.P. Gramin Bank(Erstwhile Sarva U.P. Gramin Bank)")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content16 != null) {
        Elements contents = html16.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Himachal Pradesh Gramin Bank")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content17 != null) {
        Elements contents = html17.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Sarva Haryana Gramin Bank")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content18 != null) {
        Elements contents = html18.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Assam Gramin Vikash Bank (Erstwhile Langpi Dehangi Rural Bank)")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content19 != null) {
        Elements contents = html19.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Bangiya Gramin Vikash Bank as")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content20 != null) {
        Elements contents = html20.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Punjab National Bank (Erstwhile United Bank Of India) -Credit Card")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content21 != null) {
        Elements contents = html21.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Punjab Gramin Bank")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      bankCount = 0;
      idbiRecords = true;
      Ignoredescription = null;
      totalcount = 0;
      if (content22 != null) {
        Elements contents = html22.getElementsByTag("tbody");
        System.out.println("********************** Reading tbody tags x ****************");
        for (Element x : contents) {
          Elements thContents8 = x.getElementsByTag("th");
          Elements tdContents8 = x.getElementsByTag("td");
          for (Element g : thContents8) {
            if (g.text().startsWith(
                "Daily Settlement Statement for Assam Gramin Vikash Bank as")) {
              bankName = g.text();
              bankCount++;
            } 
            if (g.text().startsWith("Summary For Micro ATM") || 
              g.text().contains("Summary For Micro ATM"))
              break; 
            for (Element h : tdContents8) {
              if (bankCount == 1 && idbiRecords) {
                if (count == 1 && h.text().equalsIgnoreCase(""))
                  continue; 
                if (count == 1) {
                  if (h.text().equalsIgnoreCase(Ignoredescription)) {
                    idbiRecords = false;
                  } else {
                    if (totalcount == 0) {
                      Ignoredescription = h.text();
                      ps2.setString(count, h.text());
                      totalcount++;
                    } else {
                      ps2.setString(count, h.text());
                      totalcount++;
                    } 
                    count++;
                  } 
                } else {
                  ps2.setString(count, h.text());
                  count++;
                } 
                if (count == cellCount + 1) {
                  ps2.setInt(5, beanObj.getCycle());
                  ps2.setString(6, beanObj.getDatepicker());
                  ps2.setString(7, beanObj.getCreatedBy());
                  ps2.setInt(8, srl_no++);
                  ps2.setString(9, bankName);
                  ps2.setString(10, file.getOriginalFilename());
                  ps2.addBatch();
                  count = 1;
                } 
              } 
            } 
          } 
        } 
        ps2.executeBatch();
        long end = System.currentTimeMillis();
        logger.info("start and end diff" + (start - end));
        mapObj.put("result", Boolean.valueOf(true));
        mapObj.put("count", Integer.valueOf(totalcountforDATA2 + totalcount));
      } 
      FileUtils.forceDelete(tempFile);
      logger.info("File exists? " + tempFile.exists());
    } catch (Exception e) {
      e.printStackTrace();
      logger.info("Exception" + e);
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
