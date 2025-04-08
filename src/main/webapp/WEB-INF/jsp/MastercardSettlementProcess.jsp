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

<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<!--  <script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" />
<link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" />   -->
<style>
label {
	color: purple;
	font-weight: bold;
	font-size: 16px;
	display: block;
}
</style>
<script type="text/javascript" src="js/MastercardSettlement.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		debugger;
		//$('#dollar_field').hide();

		$("#datepicker").datepicker({
			dateFormat : "yy/mm/dd",
			maxDate : 0
		});

	});

	window.history.forward();

	function noBack() {
		window.history.forward();
	}
	
	
	
	function ReportRollback() {
		debugger;
		var datepicker = document.getElementById("datepicker").value;
		var cet = document.getElementById("fileType").value;
		var ttumCetegory = document.getElementById("ttumCetegory").value;
		//var form = document.getElementById("microAtmReport");
		//var oMyForm = new FormData();
		//oMyForm.append('datepicker', datepicker);
		if (Validation()) {
			$.ajax({
				type : 'POST',
				url : 'rollbackTTUMMASTERCARD.do',
				async : true,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					hideLoader();
				},
				data : {
					filedate : datepicker,
					cet : cet,
					ttumCetegory : ttumCetegory
				},
				success : function(response) {
					$("#success_msg").empty();
					$("#success_msg").append(response);

				

				$("#success_msg").modal('show');

				setTimeout(function() {

					$("#success_msg").modal('hide');

					$("#success_msg").empty();
					

				}, 2500);
				},
				error : function() {
					$("#error_msg").empty();
					$("#error_msg").append("Unable To RollBack!!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
						$("#error_msg").empty();

						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);
				},
			});
		}
	}
	function Validation() {
		var datepicker = document.getElementById("datepicker").value;
		if (datepicker == "") {
			$("#alert_msg").empty();
			$("#alert_msg").append("Please select Date");

			//	document.getElementById("breadcrumb").style.display = 'none';

				$("#alert_msg").modal('show');

				setTimeout(function() {

					$("#alert_msg").modal('hide');
					//document.getElementById("breadcrumb").style.display = '';
					$("#alert_msg").empty();
				}, 2500);
			return false;
		}
		return true;
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
			MASTERCARD SETTLEMENT PROCESS
			<!-- <small>Version 2.0</small> -->
		</h1>
<!-- 		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Mastercard Settlement Process</li>
		</ol> -->
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
			<!-- left column -->
			<div class="col-md-4"></div>
			<div class="col-md-4">
				<!-- general form elements -->
				<div class="box box-prima">
					<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>
					<form:form id="reportform" action="MastercardSettlementReport.do"
						method="POST" commandName="mastercardUploadBean"
						enctype="multipart/form-data">

						<div class="form-group" id="date" style="display:${display}">
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" path="fileDate" name="fileDate"
								readonly="readonly" id="datepicker" placeholder="dd/mm/yyyy"
								title="dd/mm/yyyy" />
						</div>

						<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1">File Type</label>
							<form:select class="form-control" path="fileType" id="fileType">
								<option value="0"> SELECT</option>
								<option value="INTERNATIONAL">INTERNATIONAL</option>
								<option value="DOMESTIC">DOMESTIC</option>
							</form:select>
						</div>
						<div class="form-group">
							<label for="exampleInputEmail1">Settlement Category</label> <select
								class="form-control" name="ttumCetegory" id="ttumCetegory">
								<option value="-"> SELECT </option>
								<option value="REPORT">SETTLEMENT REPORT</option>
								<option value="TTUM">SETTLEMENT TTUM</option>




							</select>
						</div>
						<div class="box-footer">
							<button type="button" onclick="processSettlement();">Process</button>&nbsp;&nbsp;&nbsp;&nbsp;
							<button type="button" onclick="DownloadSettlement();">Download</button>&nbsp;&nbsp;&nbsp;&nbsp;
							<button type="button" onclick="ReportRollback();">Rollback</button>
						</div>
						<!-- <div id="processTbl"></div> -->
					</form:form>
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->

		</div>
		<!-- /.row -->
		<!-- /.content-wrapper -->
	</section>
	<div
			style="font-size: 14px; font-weight: bold; border-radius: 3px;color: white; text-align: center; background: yellowgreen; margin-top: -390px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="success_msg" class="success_msg">
			<i class="fa fa-check" style="color: white;"></i>
				
			
		</div>
		
		
			<div
			style="font-size: 14px; font-weight: bold; color: white;border-radius: 3px; text-align: center; background: red; margin-top: -390px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="error_msg" class="error_msg">
			<i class="fa fa-close" style="color: white;"></i>
		</div>
		<div
			style="font-size: 14px; font-weight: bold; color: black;border-radius: 3px; text-align: center; background:#FFFF9E; margin-top: -390px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="alert_msg" class="alert_msg">
			<i class="fa fa-warning" style="color: black;"></i><span
				style="color: white; font-weight: bold; text-align: center;">     </span>
		</div>
	
</div>
<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

	<img style="margin-left: 100px; margin-top: -60px;" src="images/g4.gif"
		alt="loader">


</div>
<script>
	
</script>