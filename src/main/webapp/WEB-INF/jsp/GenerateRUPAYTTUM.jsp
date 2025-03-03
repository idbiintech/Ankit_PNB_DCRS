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

<script type="text/javascript">
	$(document).ready(function() {
		debugger;
		
		$("#localDate").datepicker({
			dateFormat : "yy/mm/dd",
			maxDate : 0
		});

	});

	window.history.forward();
	function noBack() {
		window.history.forward();
	}
	function seerule(e) {

		document.getElementById("fileValue").value = e;

		window
				.open(
						"../DebitCard_Recon/SeeRule.do",
						'SeeRule',
						'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
	}

	function showLoader(location) {

		$("#Loader").show();
	}

	function hideLoader(location) {

		$("#Loader").hide();
	}

	function ValidateData() {
		var localDate = document.getElementById("localDate").value;
		var ttumType = document.getElementById("typeOfTTUM").value;
		var stSubCategory = document.getElementById("stSubCategory").value;

		debugger;

		if (stSubCategory == "-") {
			$("#alert_msg").empty();
			$("#alert_msg").append("Please Select Sub Category");

			//document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				$("#alert_msg").empty();
				//document.getElementById("breadcrumb").style.display = '';

			}, 1500);
			return false;
		}

		if (localDate == "") {
			$("#alert_msg").empty();
			$("#alert_msg").append("Please Select Date ");

			//document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				$("#alert_msg").empty();
				//document.getElementById("breadcrumb").style.display = '';

			}, 1500);
			return false;
		}

		if (ttumType == "0") {
			$("#alert_msg").empty();
			$("#alert_msg").append("Please Select TTUM Type");

			//	document.getElementById("breadcrumb").style.display = 'none';

				$("#alert_msg").modal('show');

				setTimeout(function() {

					$("#alert_msg").modal('hide');
					$("#alert_msg").empty();
					//document.getElementById("breadcrumb").style.display = '';

				}, 1500);

			
			return false;
		}

		return true;

	}

	function Process() {
		debugger;
		var frm = $('#reportform');
		var category = document.getElementById("category").value;
		var ttumType = document.getElementById("typeOfTTUM").value;
		var localDate = document.getElementById("localDate").value;
		var stSubCategory = document.getElementById("stSubCategory").value;

		if (ValidateData()) {

			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory', stSubCategory);
			oMyForm.append('typeOfTTUM', ttumType);
			oMyForm.append('localDate', localDate);
			$.ajax({
				type : "POST",
				url : "GenerateUnmatchedTTUMRUPAY.do",
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

				//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');
						$("#success_msg").empty();
						//document.getElementById("breadcrumb").style.display = '';

					}, 250000);


				},

				error : function(err) {
					hideLoader();
					$("#error_msg").empty();
					$("#error_msg").append(err);

					//document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
						$("#error_msg").empty();
						//document.getElementById("breadcrumb").style.display = '';

					}, 250000);
				}
			});

		}

	}

	function Download() {
		debugger;
		var frm = $('#reportform');
		var category = document.getElementById("category").value;
		var localDate = document.getElementById("localDate").value;
		var stSubCategory = document.getElementById("stSubCategory").value;
		var ttumType = document.getElementById("typeOfTTUM").value;
		var TTUMCategory = document.getElementById("TTUMCategory").value;

		if (ValidateData()) {

			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory', stSubCategory);
			oMyForm.append('localDate', localDate);
			oMyForm.append('typeOfTTUM', ttumType);
			oMyForm.append('TTUMCategory', TTUMCategory);
			$.ajax({
				type : "POST",
				url : "checkTTUMProcessedRUPAY.do",
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
						$("#success_msg").append("Reports are getting downloaded. Please Wait");

						//	document.getElementById("breadcrumb").style.display = 'none';

						$("#success_msg").modal('show');

						setTimeout(function() {
							
							$("#success_msg").modal('hide');
			
							$("#success_msg").empty();
							//document.getElementById("breadcrumb").style.display = '';

						}, 2500);
						
						document.getElementById("reportform").submit();
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

	function getFields(e) {
		var stSubCategory = document.getElementById("stSubCategory").value;

		if (stSubCategory == 'DOMESTIC') {
			document.getElementById("issuerOpt").style.display = '';
			document.getElementById("acquirerOpt").style.display = 'none';
		} else {

			document.getElementById("issuerOpt").style.display = '';
			document.getElementById("acquirerOpt").style.display = 'none';
		}

	}

	function ReportRollback() {
		debugger;
		var datepicker = document.getElementById("localDate").value;
		var category = document.getElementById("category").value;
		var typeOfTTUM = document.getElementById("typeOfTTUM").value;
		var subCat =  document.getElementById("stSubCategory").value;
		//var form = document.getElementById("microAtmReport");
		//var oMyForm = new FormData();
		//oMyForm.append('datepicker', datepicker);
		if (Validation()) {
			$.ajax({
				type : 'POST',
				url : 'rollbackTTUMRUAPY.do',
				async : true,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					hideLoader();
				},
				data : {
					filedate : datepicker,

					typeOfTTUM : typeOfTTUM,
					subCat: subCat,
					category: category
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

					}, 250000);
					
				},
				error : function() {
					$("#error_msg").empty();
					$("#error_msg").append("Unable To Download!!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
						$("#error_msg").empty();

						//document.getElementById("breadcrumb").style.display = '';

					}, 250000);
				},
			});
		}
	}
	function Validation() {
		var datepicker = document.getElementById("localDate").value;
		if (datepicker == "") {
			$("#alert_msg").empty();
			$("#alert_msg").append("Please Select Date");

			//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				$("#alert_msg").empty();

				//document.getElementById("breadcrumb").style.display = '';

			}, 1500);
			return false;
		}
		return true;
	}
</script>


<style>
label{
color: purple;  font-weight: bold;font-size: 16px;text-transform: uppercase;display:block;
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
</style>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
					<h1 style="color: purple; text-align: center; font-weight: bold;">
			${category} TTUM
			<!-- <small>Version 2.0</small> -->
		</h1>
<!-- 		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">RUPAY TTUM</li>
		</ol> -->
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
			<div class="col-md-4"></div>
			<!-- left column -->
			<div class="col-md-4">
				<!-- general form elements -->
				<div class="box box-prima">
					<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>
					<form:form id="reportform" action="DownloadUnmatchedTTUMRUAPY.do"
						method="POST" commandName="unmatchedTTUMBean">



						<div class="box-body" id="subcat">

							<div class="form-group" style="display:${display}">
								<label for="exampleInputEmail1">Sub Category</label> <select
									class="form-control" name="stSubCategory" id="stSubCategory"
									onchange="getFields(this)">
									<option value="-">--Select --</option>
									<c:forEach var="subcat" items="${subcategory}">
										<option value="${subcat}">${subcat}</option>
									</c:forEach>
								</select>

							</div>

							<div class="form-group" id="issuerOpt" style="display: none">
								<label for="exampleInputEmail1">TTUM Type</label> <input
									type="text" name="category" id="category" value="${category}"
									style="display: none">
								<form:select class="form-control" path="typeOfTTUM"
									id="typeOfTTUM">
									<option value="0">--Select --</option>
									<option value="DECLINE">DECLINE</option>
									<option value="PROACTIVE">PROACTIVE</option>
									<option value="DROP">CUSTOMER DEBIT</option>
									<option value="LATE PRESENTMENT">LATE PRESENTMENT</option>
										<option value="OFFLINE PRESENTMENT">OFFLINE PRESENTMENT</option>
									<option value="SURCHARGED">SURCHARGE DEBIT</option>
									<option value="SURCHARGEC">SURCHARGE CREDIT</option>
								</form:select>
							</div>

							<div class="form-group" id="acquirerOpt" style="display: none">
								<label for="exampleInputEmail1">TTUM Type</label> <input
									type="text" name="category" id="category" value="${category}"
									style="display: none">
								<form:select class="form-control" path="acqtypeOfTTUM"
									id="acqtypeOfTTUM">
									<option value="0">--Select --</option>
									<option value="DECLINE">DECLINE</option>
									<option value="PROACTIVE">PROACTIVE</option>
									<option value="DROP">CUSTOMER DEBIT</option>
									<option value="LATE PRESENTMENT">LATE PRESENTMENT</option>
									<option value="OFFLINE PRESENTMENT">OFFLINE PRESENTMENT</option>
									<option value="SURCHARGED">SURCHARGE DEBIT</option>
									<option value="SURCHARGEC">SURCHARGE CREDIT</option>
								</form:select>
							</div>

							<div class="form-group">
								<label for="exampleInputEmail1">TTUM Category</label> <select
									class="form-control" name="TTUMCategory" id="TTUMCategory">
									<option value="-">--Select --</option>
									<option value="REPORT">REPORT</option>
									<option value="TTUM">TTUM</option>





								</select>
							</div>
							<div class="form-group" id="Date">
								<input type="text" id="rectyp" value="${category}"
									style="display: none"> <label
									for="exampleInputPassword1">Date</label> <input
									class="form-control" readonly="readonly" id="localDate"
									name="localDate" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
							</div>
						</div>

						<div class="box-footer" style="text-align: center">
							<!-- <input type="submit" class="btn btn-primary" name="Process" id="Process" value="Process" onclick="return ValidateData();"> -->
	 	<button type="button" onclick="Process();">Process</button>
	 	<button type="button" onclick="Download();" >Download</button>
	 	<button type="button" onclick="ReportRollback();">Rollback</button>
							
						</div>

					</form:form>
				</div>



			</div>
			<!--/.col (left) -->

		</div>
		<!-- /.row -->
		<!-- /.content-wrapper -->
	</section>
	<div
			style="font-size: 14px; font-weight: bold; border-radius: 3px;color: white; text-align: center; background: yellowgreen; margin-top: -480px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="success_msg" class="success_msg">
			<i class="fa fa-check" style="color: white;"></i>
				
			
		</div>
		
		
			<div
			style="font-size: 14px; font-weight: bold; color: white;border-radius: 3px; text-align: center; background: red; margin-top: -400px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="error_msg" class="error_msg">
			<i class="fa fa-close" style="color: white;"></i>
		</div>
		<div
			style="font-size: 14px; font-weight: bold; color: black;border-radius: 3px; text-align: center; background:#FFFF9E; margin-top: -400px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="alert_msg" class="alert_msg">
			<i class="fa fa-warning" style="color: black;"></i><span
				style="color: white; font-weight: bold; text-align: center;">     </span>
		</div>
</div>
<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

<img style="margin-left: 100px; margin-top: -60px;"
		src="images/g4.gif" alt="loader">


</div>
<script>
	
</script>