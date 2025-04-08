<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setDateHeader("Expires", 0);
response.setHeader("Pragma", "no-cache");
response.setHeader("X-Frame-Options", "deny");
%>

<link href="css/jquery-ui.min.css" media="all" rel="stylesheet"
	type="text/css" />

<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->
<script type="text/javascript"
	src="js/ExtractVisaRawdata.js"></script>
<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<!--  <script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" />
<link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" />   -->
<!-- 
<script type="text/javascript" src="js/UnMatchedTTUMGeneration.js"></script> -->
<script type="text/javascript">
	$(document).ready(function() {
		debugger;
		//$('#dollar_field').hide();

		$("#localDate").datepicker({
			dateFormat : "yy/mm/dd",
			maxDate : 0
		});

	});

	window.history.forward();
	function noBack() {
		window.history.forward();
	}
</script>
<style>
button {
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

button:hover {
	background: linear-gradient(30deg, #c2eaba, blue);
}

label {
	color: purple;
	font-weight: bold;
	font-size: 16px;
	display: block;
}
</style>

<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1 style="color: purple; text-align: center; font-weight: bold;">
			Extract CBS RawData
			<!-- <small>Version 2.0</small> -->
		</h1>
		<!-- 		<ol class="breadcrumb">
			<li><a href="#" > Home</a></li>
			<li class="active">TTUM Generation </li>
		</ol> -->
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

					<form:form id="processform"
						action="DownloadExtractCbsRawdata.do" method="POST"
						commandName="unmatchedTTUMBean">

					

						<div class="form-group">
							<label for="exampleInputEmail1">GL Type</label> <select
								style="margin-left: 10px; width: 520px;" class="form-control"
								name="TTUMCategory" id="TTUMCategory">
								<option value="-">--Select --</option>
								<option value="FN100">FN100</option>
								<option value="5165005139924">5165005139924</option>
								<option value="5165005139926">5165005139926</option>
								<option value="5165005139931">5165005139931</option>
								<option value="5165005139932">5165005139932</option>
								<option value="5165005139933">5165005139933</option>
								<option value="5165005139934">5165005139934</option>
								<option value="5165005139936">5165005139936</option>
								<option value="5165005139938">5165005139938</option>
								<option value="5165005139948">5165005139948</option>
								<option value="5165005139949">5165005139949</option>
								<option value="5165005139950">5165005139950</option>
								<option value="5165005139951">5165005139951</option>
								<option value="5165005139954">5165005139954</option>
								<option value="5165005139968">5165005139968</option>
							
								<option value="5165005139970">5165005139970</option>
								<option value="5165005139971">5165005139971</option>
								<option value="5165005811356">5165005131356</option>
								<option value="5165005811354">5165005131356</option>
								<option value="5165005811355">5165005131355</option>
								<option value="5165005132281">5165005132281</option>
								<option value="5165005132550">5165005132550</option>
								<option value="5165005132549">5165005132549</option>
								
								


							</select>
						</div>
						
							<div class="box-body" id="subcat">

							<div class="form-group" id="ttumDate">
								<label for="exampleInputPassword1">Date</label> <input
									class="form-control" name="localDate" value="--SELECT--" readonly="readonly"
									id="localDate" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
							</div>
						</div>
		
						<div class="box-footer" style="text-align: center">
							<!-- 					<button type="button" onclick="Process();" >Process</button> -->
							<button type="button" onclick="Download();">DOWNLOAD</button>
							<!-- 	<button type="button" onclick="ReportRollback();">Rollback</button> -->

						</div>
						<div id="processTbl"></div>
					</form:form>
				</div>
			</div>
			<!-- /.box -->



		</div>
		<!--/.col (left) -->
	</section>

	<div
		style="font-size: 14px; font-weight: bold; border-radius: 3px; color: white; text-align: center; background: yellowgreen; margin-top: -320px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
		id="success_msg" class="success_msg">
		<i class="fa fa-check" style="color: white;"></i>
		<!-- 			<button type="button" class="close" data-dismiss="modal" style="margin-top:-20px; margin-right: 20px;">&times;</button>  -->


	</div>


	<div
		style="font-size: 14px; font-weight: bold; color: white; border-radius: 3px; text-align: center; background: red; margin-top: -320px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
		id="error_msg" class="error_msg">
		<i class="fa fa-close" style="color: white;"></i>
		<!-- 	<button type="button" class="close" data-dismiss="modal" style="margin-top:-20px; margin-right: 20px;">&times;</button>  -->
	</div>
	<div
		style="font-size: 14px; font-weight: bold; color: black; border-radius: 3px; text-align: center; background: #FFFF9E; margin-top: -300px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
		id="alert_msg" class="alert_msg">
		<i class="fa fa-warning" style="color: black;"></i>
	</div>
</div>
<!-- /.row -->

<!-- </div> -->
<!-- /.content-wrapper -->


<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

	<img style="margin-left: 100px; margin-top: -60px;" src="images/g4.gif"
		alt="loader">


</div>
