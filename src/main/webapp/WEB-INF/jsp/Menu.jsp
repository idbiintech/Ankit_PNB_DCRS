<%--
    
    Author     : INT12016
--%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%          response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
            response.setHeader("Pragma", "no-cache"); //HTTP 1.0
%>
  
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>i-Recon</title>
    <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>

    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
    <!-- Font Awesome Icons -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
    <!-- Ionicons -->
    <!-- <link href="http://code.ionicframework.com/ionicons/2.0.0/css/ionicons.min.css" rel="stylesheet" type="text/css" /> -->
    <!-- Daterange picker -->
    <link href="plugins/daterangepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css" />
    <!-- Theme style -->
    <link href="dist/css/main.css" rel="stylesheet" type="text/css" />
    <!-- AdminLTE Skins. Choose a skin from the css/skins 
         folder instead of downloading all of them to reduce the load. -->
    <link href="dist/css/skins/skin-lightgreen.css" rel="stylesheet" type="text/css" />

    <script src="plugins/jQuery/jQuery-2.1.3.min.js"></script>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>

    <![endif]-->
  </head>
  <body class="skin-lightgreen fixed">
    <div class="wrapper">
        <tiles:insertAttribute name="header" />
        <tiles:insertAttribute name="USERsidebar" />
        <tiles:insertAttribute name="body" />
       
    </div><!-- ./wrapper -->

     <input type="hidden" id="CSRFToken" name="CSRFToken" value="${CSRFToken }" />

    <script src="bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
    <!-- FastClick -->
    <script src='plugins/fastclick/fastclick.min.js'></script>
    <!-- AdminLTE App -->
    <script src="dist/js/app.min.js" type="text/javascript"></script>
    <!-- Sparkline -->
    <script src="plugins/sparkline/jquery.sparkline.min.js" type="text/javascript"></script>
    <!-- daterangepicker -->
    <!-- <script src="plugins/daterangepicker/daterangepicker.js" type="text/javascript"></script> -->
    <!-- datepicker -->
    <!-- <script src="plugins/datepicker/bootstrap-datepicker.js" type="text/javascript"></script> -->
    <!-- iCheck -->
    <script src="plugins/iCheck/icheck.min.js" type="text/javascript"></script>
    <!-- SlimScroll 1.3.0 -->
    <script src="plugins/slimScroll/jquery.slimscroll.min.js" type="text/javascript"></script>
<!-- <div id ="toast" class="toast" style=" visibility: hidden; min-width: 250px; background-color: #333; color: #fff; text-align: center; border-radius: 2px; padding: 16px; z-indext: 1; right: 30px; top: 30px; font-size: 17px;">Sign In Successfully!</div>
 -->

  </body>
  <!-- 
  <script type="text/javascript">
  
  window.addEventListener('load', function()){
	  
	  if(localStorage.getItem('loggedIn') == 'true'){
		  
		  showToast();
		  localStorage.removeItem('loggedIn');
	  }
  });
  function showToast(){
	  var toast = document.getElementById("toast");
	  toast.className ="toast show";
	  setTimeout(function() { toast.className = toast.className.replace("show",""); }, 3000);
	  
  }
  </script> -->
</html>
