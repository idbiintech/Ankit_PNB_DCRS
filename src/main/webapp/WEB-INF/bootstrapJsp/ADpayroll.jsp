<%--
    Document   : IRECON 2.0
    Created on : Oct 15, 2024, 10:37:02 AM
    Author     : ANKIT KESARWANI (INT12016)
--%>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%          response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
            response.setHeader("Pragma", "no-cache"); //HTTP 1.0
%>

<%
response.setHeader("Cache-Control","no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
response.setHeader("Pragma","no-cache");
response.setHeader("X-Frame-Options","deny");
%>
  
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>i-Recon</title>
    <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>
    <meta http-equiv="X-UA-Compatible" content="IE=10"> 

    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
    <!-- Font Awesome Icons -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
    <!-- Ionicons -->
    <!-- <link href="http://code.ionicframework.com/ionicons/2.0.0/css/ionicons.min.css" rel="stylesheet" type="text/css" /> -->
    <!-- Daterange picker -->
    <link href="plugins/daterangepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css" />
    <!-- Theme style -->
    <link href="dist/css/main.css" rel="stylesheet" type="text/css" />
    <link href="dist/css/animatia.css" rel="stylesheet" type="text/css" />
    
    <!-- AdminLTE Skins. Choose a skin from the css/skins 
         folder instead of downloading all of them to reduce the load. -->
    <link href="dist/css/skins/skin-blue.css" rel="stylesheet" type="text/css" />

    
    <!-- <link href="css/style.css" rel="stylesheet" type="text/css" media="all" /> -->
	<!-- 	<link href="css/dropdown.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/dropdown.vertical.rtl.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/default.ultimate.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/fields.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/form.css" media="all" rel="stylesheet" type="text/css" /> -->

		<link href="css/jquery-ui.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/jtable.css" media="all" rel="stylesheet" type="text/css" />
		<!-- <link href="css/datatables.min.css" rel="stylesheet" type="text/css" />
		<link href="https://cdn.datatables.net/select/1.2.7/css/select.dataTables.min.css" rel="stylesheet" type="text/css" /> -->
		<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/dt/dt-1.10.18/af-2.3.0/b-1.5.2/cr-1.5.0/fc-3.2.5/fh-3.1.4/kt-2.4.0/r-2.2.2/rg-1.0.3/rr-1.2.4/sc-1.5.0/sl-1.2.6/datatables.min.css"/>
 

		
		<script src="plugins/jQuery/jQuery-2.1.3.min.js"></script>
		<script type="text/javascript" src="https://cdn.datatables.net/v/dt/dt-1.10.18/af-2.3.0/b-1.5.2/cr-1.5.0/fc-3.2.5/fh-3.1.4/kt-2.4.0/r-2.2.2/rg-1.0.3/rr-1.2.4/sc-1.5.0/sl-1.2.6/datatables.min.js"></script>
		<script src="js/commonScript.js" type="text/javascript"></script> 

		<script src="js/jquery-ui.js" type="text/javascript"></script>
		<script src="plugins/slimScroll/jquery.slimscroll.min.js" type="text/javascript"></script>	
		<script type="text/javascript" src="js/wrapping/pbkdf2.js"></script> 
		<script type="text/javascript" src="js/wrapping/aes.js"></script>
		<script type="text/javascript" src="js/wrapping/AesUtil.js"></script>
		

    <style >
     button{
background: linear-gradient(45deg, pink, blue);
border: none;
color: white;
padding: 5px 10px;
font-size: 16px;
border-radius: 9px;

cursor: pointer;
text-transform: uppercase;
transaction: background 0.3s ease;
}
button:hover{

background: linear-gradient(to right, purple 50%, purple 10%);
}
    .ghg{

border-radius: 40px;

}</style>
  </head>
  <body class="skin-blue fixed">
    <div class="wrapper">
        <tiles:insertAttribute name="header" />
        <tiles:insertAttribute name="ADSidebar" />
        <tiles:insertAttribute name="body" />
        <tiles:insertAttribute name="footer" />
    </div><!-- ./wrapper -->

<!-- Modal -->

<!-- <div id="whatsNew" class="modal fade whatsnewModal" data-backdrop="static" role="dialog">
  <div class="modal-dialog animated zoomIn" style="width: 30%; ">

    Modal content


	<img class="ghg" src="./dist/img/g5.gif" height="500px" width="650px" >
  



  </div>
</div> -->
    

    <script src="bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
   
    <!-- FastClick -->
    <script src='plugins/fastclick/fastclick.min.js'></script>
    <!-- AdminLTE App -->
    <script src="dist/js/app.min.js" type="text/javascript"></script>
    <!-- Sparkline -->
    <script src="plugins/sparkline/jquery.sparkline.min.js" type="text/javascript"></script>

    <script src="plugins/iCheck/icheck.min.js" type="text/javascript"></script>

    
    
    
  

  </body>
</html>
