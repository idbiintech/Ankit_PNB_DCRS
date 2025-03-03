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

<script type="text/javascript" src="js/reconProcess.js"></script>


<style>
button {
	background: linear-gradient(45deg, pink, blue);
	border: none;
	color: white;
	padding: 10px 26px;
	font-size: 16px;
	border-radius: 6px;
	cursor: pointer;
	text-transform: uppercase;
	transaction: background 0.3s ease;
}

button:hover {
	background: linear-gradient(to right, purple 50%, purple 10%);
}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		debugger;
		//$('#dollar_field').hide();

		$("#datepicker").datepicker({
			dateFormat : "dd-mm-y",
			maxDate : 0
		});
	});

	window.history.forward();
	function noBack() {
		window.history.forward();
	}
	function Rollback() {
		debugger;
		var filedate = document.getElementById("datepicker").value;
		var subCat = document.getElementById("stSubCategory").value;
		var Cat = document.getElementById("rectyp").value;

		//var form = document.getElementById("microAtmReport");
		//var oMyForm = new FormData();
		//oMyForm.append('datepicker', datepicker);
		if (Validation()) {
			$.ajax({
				type : 'POST',
				url : 'rollbackCTC.do',
				async : true,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					hideLoader();
				},
				data : {
					filedate : filedate,
					subCat : subCat,
					Cat : Cat

				},
				success : function(response) {

					$("#success_msg").append(response);

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');
						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);
				},
				error : function() {
			
					$("#error_msg").append("Error Occured");

					//document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);
				},
			});
		}
	}
	function Validation() {
		var datepicker = document.getElementById("datepicker").value;
		if (datepicker == "") {
			alert("Please select date");
			return false;
		}
		return true;
	}
</script>



<style>
.gbutton {
	background: linear-gradient(45deg, pink, blue);
	border: none;
	color: white;
	padding: 12px 26px;
	font-size: 16px;
	border-radius: 20px;
	cursor: pointer;
	text-transform: uppercase;
	transaction: background 0.3s ease;
}

.gbutton:hover {
	background: linear-gradient(30deg, #c2eaba, blue);
}

label {
	color: purple;
	font-weight: bold;
	font-size: 16px;
	text-transform: uppercase;
	display: block;
}
</style>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1 style="color: purple; text-align: center; font-weight: bold;">
			${category} RECON PROCESS
			<!-- <small>Version 2.0</small> -->
		</h1>
<!-- 		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Recon Process</li>
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


					<div class="box-body" id="subcat">

						<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" onchange="CallDollar()">Sub
								Category</label> <input type="text" id="rectyp" value="${category}"
								style="display: none"> <select class="form-control"
								name="stSubCategory" id="stSubCategory"
								onchange="setfilename(this);">
								<option value="-">--SELECT--</option>
								<c:forEach var="subcat" items="${subcategory}">
									<option value="${subcat}">${subcat}</option>
								</c:forEach>
							</select>

						</div>
						<input type="hidden" name="CSRFToken" id="CSRFToken"
							value="${CSRFToken }">
						<div class="form-group" id="dollor_div" style="display: none;">
							<label for="exampleInputEmail1">Dollar Value</label> <input
								type="text" type="hidden" class="form-control"
								name="dollar_field" id="dollar_val"
								onkeypress="return Validate(event);">

						</div>


						<div class="form-group" id="trsubcatid" style="display: none">
							<label for="exampleInputEmail1">TYPE</label>
							<!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->

							<select class="form-control" name="TTUM_TYPE" id="TTUM_TYPE">
								<option value="-">--Select --
									</optio>
								<option value="ATM">ATM</option>
								<option value="POS">POS</option>
							</select>
						</div>




						<div class="form-group">
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="fileDate" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
							<!-- <img alt="" src="images/listbtn.png" title="Last Uploaded File" onclick="getupldfiledetails();" style="vertical-align:middle; height: 20px; width: 20px;"> -->


						</div>

					</div>


					<div class="box-footer" style="text-align: center">
						<a onclick="Process();" class="gbutton">Process</a>&nbsp;&nbsp;&nbsp;
						<a onclick="Rollback();" class="gbutton">RollBack</a>
					</div>





				</div>
				
				<div id="processTbl"></div>
				<%-- </form> --%>
			</div>
			<!-- /.box -->



		</div>


	</section>
			
		
		<div
			style="font-size: 14px; font-weight: bold; border-radius: 3px;color: white; text-align: center; background: yellowgreen; margin-top: -300px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="success_msg" class="success_msg">
			<i class="fa fa-check" style="color: white;"></i>
			<button type="button" class="close"  data-dismiss="modal" style="margin-top:-20px; margin-right: 20px;">&times;</button> 
				
			 
		</div>
		
		
			<div
			style="font-size: 14px; font-weight: bold; color: white;border-radius: 3px; text-align: center; background: red; margin-top: -300px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="error_msg" class="error_msg">
			<i class="fa fa-close" style="color: white;"></i>
			<button type="button" class="close"data-dismiss="modal" style="margin-top:-20px; margin-right: 20px;">&times;</button>  
		</div>
		<div
			style="font-size: 14px; font-weight: bold; color: black;border-radius: 3px; text-align: center; background: #FFFF9E; margin-top: -300px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="alert_msg" class="alert_msg">
			<i class="fa fa-warning" style="color: black;"></i>
		</div>
</div>
<!-- /.content-wrapper -->

<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

	<img style="margin-left: 100px; margin-top: -60px;" src="images/g4.gif"
		alt="loader">


</div>
<script>
	function CallDollar() {
		debugger;
		alert("sas");
	}
</script>