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
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/AdjustmentTTUM.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		debugger;
		//$('#dollar_field').hide();
		$("#dailypicker").datepicker({
			dateFormat : "yy/mm/dd",
			maxDate : 0
		});

	});

	window.history.forward();
	function noBack() {
		window.history.forward();
	}

	function DownloadAdjTTUM() {

		var frm = $('#reportform');

		//var timePeriod = document.getElementById("timePeriod").value;

		var filename = document.getElementById("fileName").value;
		var category = document.getElementById("rectyp").value;
		//var stSubCategory = document.getElementById("stSubCategory").value;
		var adjType = document.getElementById("adjType").value;
		var adjCategory = document.getElementById("adjCategory").value;
		/*var userfile = document.getElementById("dataFile1");
		alert("file is "+userfile);*/
		//var CSRFToken = $('[name=CSRFToken]').val();
		//var  stSubCategory =document.getElementById("stSubCategory").value;
		/*var fileDate = document.getElementById("datepicker").value;
		alert("file date "+fileDate);*/

		var datepicker = document.getElementById("dailypicker").value;
		/*if(filename == "NTSL-NFS")
		{
			var  cycle =document.getElementById("cycle").value;
		}
		else
		{
			var  cycle = "1";
		}*/

		var oMyForm = new FormData();

		//oMyForm.append('file',userfile.files[0])
		oMyForm.append('fileName', filename);
		oMyForm.append('category', category);
		//oMyForm.append('stSubCategory', stSubCategory);
		oMyForm.append('datepicker', datepicker);
		oMyForm.append('adjType', adjType);
		oMyForm.append('adjCategory', adjCategory);
		//oMyForm.append('cycle',cycle);

		if (ValidateData()) {
			$.ajax({

				type : 'POST',
				url : 'ValidateDownloadAdjTTUM.do',
				async : true,
				beforeSend : function() {
					showLoader();
				},

				data : {
					datepicker : datepicker,

					fileName : filename,
					adjType : adjType,
					adjCategory : adjCategory
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

							//document.getElementById("breadcrumb").style.display = '';

						}, 2500);

						document.getElementById("reportform").submit();
						/*window.location.reload(true);	*/} else {
						$("#error_msg").empty();

						$("#error_msg").append(response);

						//	document.getElementById("breadcrumb").style.display = 'none';

						$("#error_msg").modal('show');

						setTimeout(function() {

							$("#error_msg").modal('hide');
							$("#error_msg").empty();

							//document.getElementById("breadcrumb").style.display = '';

						}, 2500);
					}

				},
				error : function() {
					alert("Error Occured");
					$("#error_msg").empty();

					$("#error_msg").append("Error Occured");

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
					//window.location.reload(true);

				},
			});

		}

	}

	function processAdjTTUM() {

		var frm = $('#reportform');

		//var timePeriod = document.getElementById("timePeriod").value;

		var filename = document.getElementById("fileName").value;
		var category = document.getElementById("rectyp").value;
		//	var stSubCategory = document.getElementById("stSubCategory").value;
		var adjType = document.getElementById("adjType").value;
		var adjCategory = document.getElementById("adjCategory").value;
		/*var userfile = document.getElementById("dataFile1");
		alert("file is "+userfile);*/
		//var CSRFToken = $('[name=CSRFToken]').val();
		//var  stSubCategory =document.getElementById("stSubCategory").value;
		/*var fileDate = document.getElementById("datepicker").value;
		alert("file date "+fileDate);*/

		var datepicker = document.getElementById("dailypicker").value;
		/*if(filename == "NTSL-NFS")
		{
			var  cycle =document.getElementById("cycle").value;
		}
		else
		{
			var  cycle = "1";
		}*/

		var oMyForm = new FormData();

		//oMyForm.append('file',userfile.files[0])
		oMyForm.append('fileName', filename);
		oMyForm.append('category', category);
		//oMyForm.append('stSubCategory', stSubCategory);
		oMyForm.append('datepicker', datepicker);
		oMyForm.append('adjType', adjType);
		oMyForm.append('adjCategory', adjCategory);

		//oMyForm.append('cycle',cycle);

		if (ValidateData()) {
			$.ajax({

				type : 'POST',
				url : 'VISAAdjustmentProcess.do',
				async : true,
				beforeSend : function() {
					showLoader();
				},

				data : {
					datepicker : datepicker,

					fileName : filename,
					category : category,
					adjType : adjType,
					adjCategory : adjCategory
				},
				success : function(response) {

					/* 	if(response == "PROCESSED SUCCESS" || response == "ALLREDY PROCESSED!"  ){
							
						} */
					$("#success_msg").empty();

					$("#success_msg").append(response);

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');
						$("#success_msg").empty();

						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);

				},
				error : function() {
					$("#error_msg").empty();

					$("#error_msg").append("Error Occured");

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

	function showLoader(location) {

		$("#Loader").show();
	}

	function hideLoader(location) {

		$("#Loader").hide();
	}

	function ValidateData() {
		var category = document.getElementById("rectyp").value;
		//	var subCategory = document.getElementById("stSubCategory").value;
		//var subcategory = document.getElementById("stSubCategory").value;
		var fileSelected = document.getElementById("fileName").value;

		var adjType = document.getElementById("adjType").value;
		var adjCategory = document.getElementById("adjCategory").value;
		/*var timePeriod = document.getElementById("timePeriod").value;
		
		if(timePeriod == "Daily")
		{*/
		var fileDate = document.getElementById("dailypicker").value;
		//var  cycle =document.getElementById("cycle").value;
		/*}
		else
		{
			var fileDate = document.getElementById("monthpicker").value;
			cycle = "0";
		}*/

		debugger;




		if (adjType == "") {
			$("#alert_msg").empty();
			$("#alert_msg").append("Please select Adjustment Type");

			//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
			return false;
		}
		if (adjCategory == "") {
			$("#alert_msg").empty();
			$("#alert_msg").append("Please select adjCategory");

			//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');

				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
			return false;
		}
		//if(timePeriod == "Daily" && cycle == "0")
		/*if(fileSelected == "NTSL-NFS" && cycle == "0")
		{
			alert("Please Select cycle");
			return false;
		}*/
		return true;

	}
	function ReportRollback() {
		debugger;
		var datepicker = document.getElementById("dailypicker").value;
		var adjType = document.getElementById("adjType").value;
		var adjCategory = document.getElementById("adjCategory").value;
		//var form = document.getElementById("microAtmReport");
		//var oMyForm = new FormData();
		//oMyForm.append('datepicker', datepicker);
		if (Validation()) {
			$.ajax({
				type : 'POST',
				url : 'rollbackTTUMReportVISA.do',
				async : true,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					hideLoader();
				},
				data : {
					filedate : datepicker,
					adjType : adjType,
					adjCategory : adjCategory
				},
				success : function(response) {
					$("#success_msg").empty();
					$("#success_msg").append(response);

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');

						$("#success_msg").empty();
						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);

				},
				error : function() {
					$("#error_msg").empty();
					$("#error_msg").append("Error Occured");

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
		var datepicker = document.getElementById("dailypicker").value;
		if (datepicker == "") {
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
			VISA ADJUSTMENT PROCCESS
			<!-- <small>Version 2.0</small> -->
		</h1>
		<!-- 	<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Adjustment TTUM</li>
		</ol> -->
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
			<div class="col-md-4"></div>
			<!-- left column -->
			<div class="col-md-4">
				<!-- general form elements -->
				<div class="box box-primary">
					<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>
					<form:form id="reportform" action="DownloadAdjTTUMVISANEW.do"
						method="POST" commandName="nfsSettlementBean"
						enctype="multipart/form-data">



						<div class="box-body" id="subcat">

							<div class="form-group" style="display:${display}">
								<label for="exampleInputEmail1">Ctegory : </label> <input
									type="text" id="rectyp" value="${category}"
									style="display: none"> <select class="form-control"
									name="fileName" id="fileName">
									<option value="0">--Select --</option>
									<!-- <option value="NTSL-DFS">DFS</option>
									<option value="NTSL-JCB-UPI">JCB-UPI</option> -->
									<option value="VISA">VISA</option>

								</select>
							</div>




							<div class="form-group">
								<label for="exampleInputEmail1">Adjustment Type</label> <select
									class="form-control" name="adjType" id="adjType">
									<option value="-">--Select --</option>
									<option value="CHBK_REJECT">CHARGEBACK
										REJECT</option>


									<option value="CHBK_ACPT">CHARGEBACK ACCEPTE</option>
									



								</select>
							</div>
							<div class="form-group">
								<label for="exampleInputEmail1">Adjustment Category</label> <select
									class="form-control" name="adjCategory" id="adjCategory">
									<option value="-">--Select --</option>
									<option value="REPORT">ADJUSTMENT REPORT</option>
									<option value="TTUM">ADJUSTMENT TTUM</option>




								</select>
							</div>

							<!-- <div class="form-group" id ="cycles" style="display:none">
							<label for="exampleInputEmail1" >Cycle</label> 
							<select class="form-control" name="cycle" id="cycle">
								<option value="0">--Select --</option>
									<option value="1">1</option>
									<option value="2">2</option>	
							</select>
						</div>
                     -->

							<!--  <div class="form-group" id="Month" style="display:none" >
							<label for="exampleInputPassword1">Month</label> <input
								class="form-control" name="datepicker" readonly="readonly"
								id="monthpicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
						</div> -->

							<div class="form-group" id="Date">
								<label for="exampleInputPassword1">Date</label> <input
									class="form-control" name="datepicker" readonly="readonly"
									id="dailypicker" placeholder="dd/mm/yy" title="dd/mm/yy" />
							</div>


						</div>

						<!--  <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="file" id="dataFile1" title="Upload File" /></td>
                      <p class="help-block">Example block-level help text here.</p>
                    </div>
                     -->
						<!--  <div class="box-footer">
                  		  <button type="button" value="UPLOAD" id="upload" onclick="return processFileUpload();" class="btn btn-primary">Upload</button>
                 		 </div>

						</div> -->







						<div class="box-footer" style="text-align: center">
							<button type="button" onclick="processAdjTTUM();">Process</button>
							<button type="button" onclick="DownloadAdjTTUM();">Dowload</button>

							<button type="button" onclick="ReportRollback();">Rollback</button>
							<!-- 	<button type="button" id ="Skip" class="btn btn-danger" onclick="skipSettlement();">Skip Settlement</button> -->
							<!-- 	<a onclick="processSettlement();" class="btn btn-primary">Process</a>
						<a onclick="skipSettlement();" class="btn btn-primary">Skip Settlement</a> -->

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
		style="font-size: 14px; font-weight: bold; border-radius: 3px; color: white; text-align: center; background: yellowgreen; margin-top: -480px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
		id="success_msg" class="success_msg">
		<i class="fa fa-check" style="color: white;"></i>


	</div>


	<div
		style="font-size: 14px; font-weight: bold; color: white; border-radius: 3px; text-align: center; background: red; margin-top: -480px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
		id="error_msg" class="error_msg">
		<i class="fa fa-close" style="color: white;"></i>
	</div>
	<div
		style="font-size: 14px; font-weight: bold; color: black; border-radius: 3px; text-align: center; background: #FFFF9E; margin-top: -480px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
		id="alert_msg" class="alert_msg">
		<i class="fa fa-warning" style="color: black;"></i><span
			style="color: white; font-weight: bold; text-align: center;">
		</span>
	</div>
</div>
<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

	<img style="margin-left: 100px; margin-top: -60px;" src="images/g4.gif"
		alt="loader">


</div>
<script>
	
</script>