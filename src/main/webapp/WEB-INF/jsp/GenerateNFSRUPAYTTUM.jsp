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
<!-- 
<script type="text/javascript" src="js/UnMatchedTTUMGeneration.js"></script> -->
<script type="text/javascript">
	$(document).ready(function() {
		debugger;
		//$('#dollar_field').hide();


		$("#localDate").datepicker({
			dateFormat : "dd-mm-y",
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
		NFS RUPAY ROUTE TTUM
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

					<form:form id="processform" action="DownloadUnmatchedTTUMVISANFSRUPAY.do"
						method="POST" commandName="unmatchedTTUMBean">

						<div class="box-body" id="subcat">

							<div class="form-group" id="ttumDate">
								<label for="exampleInputPassword1">Date</label> <input 
									class="form-control" name="localDate" value="--SELECT--"readonly="readonly"
									id="localDate" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
							</div>
						</div>
						
									<div class="form-group">
								<label for="exampleInputEmail1">TTUM Category</label> <select style=" margin-left:10px; width:500px;"
									class="form-control" name="TTUMCategory" id="TTUMCategory">
									<option value="-">--SELECT--</option>
									<option value="REPORT">REPORT</option>
									<option value="TTUM">TTUM</option>





								</select>
							</div>


						<div class="box-footer" style="text-align: center">
							<button type="button" onclick="Process();" >Process</button> 
							<button type="button" onclick="Download();">DOWNLOAD</button>
						<button type="button" onclick="ReportRollback();">Rollback</button> 

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
	style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">
	<img style="margin-left: 100px; margin-top: -200px;"
		src="images/g4.gif" alt="loader">

</div>
<script>
	function ReportRollback() {
		debugger;
		var datepicker = document.getElementById("localDate").value;
		
		//var form = document.getElementById("microAtmReport");
		//var oMyForm = new FormData();
		//oMyForm.append('datepicker', datepicker);
		if (Validation()) {
			$.ajax({
				type : 'POST',
				url : 'rollbackTTUMVISANB.do',
				async : true,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					hideLoader();
				},
				data : {
					filedate : datepicker

				},
				success : function(response) {
					$("#success_msg").empty();
					$("#success_msg").append(response);

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

				},
				error : function() {
					$("#error_msg").empty();
					$("#error_msg").append("Unable To RollBack!!");

					//document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

				},
			});
		}
	}
	function Validation() {
		var datepicker = document.getElementById("localDate").value;
		if (datepicker == "--SELECT--") {
			$("#alert_msg").empty();
			$("#alert_msg").append("Please select date");

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
	function CallDollar() {
		debugger;
		alert("sas");
	}
	

	function ValidateData() {
		debugger;

		var ttumType = document.getElementById("TTUMCategory").value;

		
		var datepicker = document.getElementById("localDate").value;
		if (datepicker == "--SELECT--") {
			$("#alert_msg").empty();
			$("#alert_msg").append("Please select date");

		//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
	       $("#alert_msg").empty();
			}, 2500);
			return false;
		}
		
		if (ttumType == "-") {
			$("#alert_msg").empty();
			$("#alert_msg").append("Please Select TTUM Type");

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
	function Process() {
		debugger;
		var frm = $('#reportform');
	
		var ttumType = document.getElementById("TTUMCategory").value;
		var localDate = document.getElementById("localDate").value;



		if (ValidateData()) {

			var oMyForm = new FormData();

			oMyForm.append('typeOfTTUM', ttumType);
			oMyForm.append('localDate', localDate);
			$.ajax({
				type : "POST",
				url : "GenerateUnmatchedTTUMNFSRUPAY.do",
				data : oMyForm,

				processData : false,
				contentType : false,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					document.getElementById("upload").disabled = "";
					hideLoader();

				},
				success : function(response) {
					debugger;
					hideLoader();

					$("#success_msg").empty();
					$("#success_msg").append(response);

				

				$("#success_msg").modal('show');

				setTimeout(function() {

					$("#success_msg").modal('hide');
					$("#success_msg").empty();
				}, 2500);
				


				},

				error : function(err) {
					hideLoader();
					$("#error_msg").empty();
					$("#error_msg").append("Unable To Process!!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
				
						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);
				

				}
			});

		}

	}

	function Download() 
	{
		debugger;
		var frm = $('#reportform');

		var localDate = document.getElementById("localDate").value;
		var TTUMCategory = document.getElementById("TTUMCategory").value;
		if (ValidateData()) {

			var oMyForm = new FormData();

			oMyForm.append('localDate', localDate);
			oMyForm.append('TTUMCategory', TTUMCategory);
			$.ajax({
				type : "POST",
				url : "checkTTUMProcessedVISANB.do",
				data : oMyForm,

				processData : false,
				contentType : false,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					document.getElementById("upload").disabled = "";
					hideLoader();

				},
				success : function(response) {
					if (response == "success") {
						$("#success_msg").empty();
						$("#success_msg").append(
								"Reports are getting downloaded. Please Wait");

						//	document.getElementById("breadcrumb").style.display = 'none';

						$("#success_msg").modal('show');

						setTimeout(function() {

							$("#success_msg").modal('hide');

							$("#success_msg").empty();
							//document.getElementById("breadcrumb").style.display = '';

						}, 2500);
						document.getElementById("processform").submit();
					} else {
						$("#success_msg").empty();
						$("#success_msg").append(response);

						//	document.getElementById("breadcrumb").style.display = 'none';

						$("#success_msg").modal('show');

						setTimeout(function() {

							$("#success_msg").modal('hide');

							$("#success_msg").empty();
							//document.getElementById("breadcrumb").style.display = '';

						}, 2500);
					}

				},
				error : function(err) {
					hideLoader();
					$("#error_msg").empty();
					$("#error_msg").append("Unable To Download!!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
						$("#error_msg").empty();

						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);
				},
				complete : function(data) {

					hideLoader();

				},
			});

		}
	}
	

	function ReportRollback() {
		debugger;

		var datepicker = document.getElementById("localDate").value;

		if (Validation()) {
			$.ajax({
				type : 'POST',
				url : 'rollbackTTUMNFSRUPAY.do',
				async : true,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					hideLoader();
				},
				data : {
					filedate : datepicker
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
		var datepicker = document.getElementById("localDate").value;
		if (datepicker == "--SELECT--") {
			$("#alert_msg").empty();
			$("#alert_msg").append("Please select Date");

			//	document.getElementById("breadcrumb").style.display = 'none';

				$("#alert_msg").modal('show');

				setTimeout(function() {

					$("#alert_msg").modal('hide');
					//document.getElementById("breadcrumb").style.display = '';
					$("#alert_msg").empty();
				}, 1500);
			return false;
		}
		return true;
	}

	function showLoader(location) {

		$("#Loader").show();
	}

	function hideLoader(location) {

		$("#Loader").hide();
	}

</script>