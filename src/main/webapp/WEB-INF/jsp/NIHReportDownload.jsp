<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	
	<%
response.setHeader("Cache-Control","no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
response.setHeader("Pragma","no-cache");
response.setHeader("X-Frame-Options","deny");
%>

<link href="css/jquery-ui.min.css" media="all" rel="stylesheet"
	type="text/css" />

<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<!--  <script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" />
<link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" />   -->

<script type="text/javascript" src="js/NIHReportDownload.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();
  
    $("#datepicker").datepicker({dateFormat:"dd-M-yy", maxDate:0});
    
    $("#localDate").datepicker({dateFormat:"dd-M-yy", maxDate:0});
    
    });
    


    

window.history.forward();
function noBack() { window.history.forward(); }


</script>
<style>
label{
color: purple;  font-weight: bold;font-size: 16px;display:block;
}

button{
background: linear-gradient(45deg, pink, blue);
border: none;
color: white;
padding: 10px 26px;
font-size: 16px;
border-radius: 20px;

cursor: pointer;
text-transform: uppercase;
transaction: background 0.3s ease;
}
button:hover{

background:linear-gradient(30deg, #c2eaba, blue);
}
label{
color: purple;  font-weight: bold;font-size: 16px;display:block;
}
</style>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
						<h1 style="color: purple; text-align: center; font-weight: bold;">
			NIH REPORT
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">NIH Report</li>
		</ol>
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
			<!-- left column -->
			<div class="col-md-4"></div>
			<div class="col-md-4">
				<!-- general form elements -->
				<div class="box box-primary">
					<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>

<form:form id="processform"  action="DownloadNIHReport.do" method="POST"  commandName="unmatchedTTUMBean" >

					<div class="box-body" id="subcat">
					
						<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >TTUM Type</label> 
							<input type="text" id="category" value="${category}" style="display: none"> 
							<form:select class="form-control" path="typeOfTTUM" id="typeOfTTUM" onchange="getFields(this)">
								<option value="0">--Select --</option>
									<option value="PROCESSED">Processed</option>
									<option value="UNRECON">UNRECON</option>	
							</form:select>
						</div>
						
						<!-- <div class="form-group" id ="date" style="display:none">
							<label for="exampleInputPassword1">Recon Date</label> 
							<input class="form-control" name="fileDate" readonly="readonly"	id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
						</div> -->

						<div class="form-group" id="ttumDate" style="display:${display}">
							<label for="exampleInputPassword1">Tran Date</label> 
							<input class="form-control" name="localDate" readonly="readonly" id="localDate" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
						</div>
						</div>
						
					<!-- </div> -->
					<!-- /.box-body -->

					<div class="box-footer" style="text-align: center">
						<!-- <a onclick="Process();" class="btn btn-primary">Process</a> -->
						<button type="button" onclick="Download();" >Download</button>
	
					</div>
					<div id="processTbl"></div>
</form:form>
</div>
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->
</section>
		</div>
		<!-- /.row -->
	
<!-- </div> -->
<!-- /.content-wrapper -->

<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

<img style="margin-left: 100px; margin-top: -60px;"
		src="images/g4.gif" alt="loader">


</div>
<script>
function CallDollar()
{
	debugger;
	alert("sas");
	}
</script>