/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recon.util;

import java.io.FileInputStream;
import java.io.InputStream;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;


public class OracleConn implements java.io.Serializable {

    private Connection myConnection;
    private String login;
    private String password;
    private String database;
    private String driver;
    private String server;
    private String port;
    private String oracleServer;
    private String url;
    private Hashtable settings;

    public void createConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        System.out.println("New Connection");

        myConnection = DriverManager.getConnection(url, login, password);
        //myConnection = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=ON)(FAILOVER=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=dcrs-scan)(PORT=1621)))(CONNECT_DATA=(SERVICE_NAME=DCRS)(SERVER=DEDICATED)))", "debitcard_recon", "dcrs");

        myConnection.setAutoCommit(true);
        myConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    }

    public Connection getconn() {
        return this.myConnection;
    }

    public void RollBack() throws SQLException {
        myConnection.rollback();
        System.out.println("RollBack Process Called");
    }

    public void CloseConnection() throws SQLException {
        myConnection.close();
        System.out.println("Connection to conv Closed");
    }

	/*
	 * public void ConManagerOrcl(String server, String port, String database,
	 * String login, String password) throws ClassNotFoundException, SQLException {
	 * //setDefaultDriver(); this.server = url; this.port = port; this.database =
	 * database; this.login = login; this.password = password; createConnection();
	 * 
	 * }
	 */
    public Properties loadPropertiesFile() throws Exception
    {
    	//TAKING DB CONNECTION USING JDBC PROPERTY FILE
    	Properties prop = new Properties();
    	InputStream in = new FileInputStream("jdbc.properties");
    	prop.load(in);
    	in.close();
    	return prop;
    }

    public OracleConn() throws ClassNotFoundException, SQLException {

    	
    	
        /*live server */
            database = "DCRS";
            //CUB UAT db
//            url="jdbc:oracle:thin:@192.168.137.37:1522:irecondb";                       
//            login = "debitcard_recon";
//            password = "debitcardrecon"; 
            
            
            //production local jdbc:oracle:thin:@192.168.137.10:1522:irecondb
            //  url="jdbc:oracle:thin:@172.17.7.208:1522:irecondb"; 
            //url="jdbc:oracle:thin:@172.17.6.13:1522:irecondb"; 
            
            //production main  
      //   url="@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=CARDSRECNPR-scan.unionbankofindia.co.in)(PORT=1621))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=DEBITCRD)))"; 
      
   //  url="jdbc:mysql://10.161.79.12:3511/debitcard_recon_pnb"; 
     url="jdbc:mysql://103.108.12.208:3511/debitcard_recon_pnb";
    // url="jdbc:mysql://10.161.76.21:3511/debitcard_recon_pnb"; 
             
            login = "reconuser";	
      password = "r1e2c3o4n5";
     // password = "Un1ted@PNB#!";
       //password = "Recon@123";
//            
            //uco uat db
            /*url="jdbc:oracle:thin:@172.19.143.60:1621:DCRS";
            login = "DEBITCARD_RECON"; 
            password = "DEBITCARD_RECON";*/
            
            //uco prod db
//           url="jdbc:oracle:thin:@172.19.247.162:1621:DCRS";
//          //url="jdbc:oracle:thin:@172.19.118.29:1621:DCRS";   // DR DB DETAILS
//             login = "DEBITCARD_RECON";
//            password = "debitcard_recon";
            
           /* url="jdbc:oracle:thin:@172.19.247.162:1621:orcl";
            login = "DEBITCARD_RECON";
            password = "debitcard_recon";*/
            
          
        createConnection();

    }

    public ResultSet executeQuery(String query) throws SQLException {
        Statement stmnt;
        //stmnt = myConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        stmnt = myConnection.createStatement();
        ResultSet rsltSt;
        rsltSt = stmnt.executeQuery(query);

       // System.out.print("Usertpe=" + rsltSt.getString("UserType"));
        return rsltSt;
    }

    public Vector executeQueryVector(String query) throws SQLException {
        Vector vectMain = new Vector(20, 10);
        Statement stmnt;
        //stmnt = myConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        stmnt = myConnection.createStatement();
        ResultSet rs;
        rs = stmnt.executeQuery(query);
        ResultSetMetaData rsMetaData = rs.getMetaData();

        int numberOfColumns = rsMetaData.getColumnCount();
        while (rs.next()) {
            Vector vectRec = new Vector(20, 10);
            for (int i = 1; i <= numberOfColumns; i++) {
                String str = rs.getString(i);
                vectRec.add(str);
            }
            vectMain.add(vectRec);
        }

        rs.close();
        stmnt.close();
        rs = null;
        stmnt = null;

        return vectMain;
    }

    public int executeUpdate(String query) throws SQLException {
        Statement stmnt;
        stmnt = myConnection.createStatement();
        return stmnt.executeUpdate(query);
    }

    public void resetAutoCommit() throws SQLException {
        myConnection.commit();
        myConnection.setAutoCommit(true);
    }

    public static void main(String args[]) {
        try {
            OracleConn conn = new OracleConn();
            conn.createConnection();
            //conn.resetAutoCommit();
            System.out.println(" Connection is made on conv");

        } catch (Exception ex) {
            System.out.println("Exception in Main--> " + ex.getMessage());
        }

    }

    public OracleConn(String db_nm) throws ClassNotFoundException, SQLException {
        /*live server */
        database = "tomhawk";
        login = "conv";
        password = "testconv";
        server = "10.144.136.147";
        port = "1521";


        OracleConn conn = new OracleConn();


    }
}
