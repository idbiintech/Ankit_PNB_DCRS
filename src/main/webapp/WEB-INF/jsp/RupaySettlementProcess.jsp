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

<script type="text/javascript" src="js/RupaySettlementProcess.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();
  
    $("#datepicker").datepicker({dateFormat:"yy/mm/dd", maxDate:0});
    });
    

window.history.forward();
function noBack() { window.history.forward(); }


function ReportRollback() {
	debugger;
	var subcategory = document.getElementById("subcategory").value;
	var fileDate = document.getElementById("datepicker").value;
	var cycle = document.getElementById("cycle").value;
	
	//var form = document.getElementById("microAtmReport");
	//var oMyForm = new FormData();
	//oMyForm.append('datepicker', datepicker);
	if (Validation()) {
		$.ajax({
			type : 'POST',
			url : 'rollbackRUPAYSETTL.do',
			async : true,
			beforeSend : function() {
				showLoader();
			},
			complete : function(data) {
				hideLoader();
			},
			data : {
				
				fileDate : fileDate,
				subcategory : subcategory,
				cycle : cycle
				
			},
			success : function(response) {
		
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
function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}

function Validation() {
	var datepicker = document.getElementById("datepicker").value;
	if (datepicker == "") {

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
			RUPAY SETTLEMENT PROCESS
			<!-- <small>Version 2.0</small> -->
		</h1>
<!-- 		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Rupay Settlement Process</li>
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
<form:form id="processform"  action="DownloadSettlement.do" method="POST"  commandName="rupaySettlementBean" >
				<div class="box-body">
					 <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1">Sub Category</label> 
							<select class="form-control" name="subcategory" id="subcategory">
								<option value="-">--Select --</option>
									<option value="DOMESTIC">DOMESTIC</option>
									<option value="INTERNATIONAL">INTERNATIONAL</option>
										<option value="RRB_DOMESTIC">RRB DOMESTIC</option>
									<option value="RRB_INTERNATIONAL">RRB INTERNATIONAL</option>
							</select>
						</div>
		
		
                    
                    <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Cycle</label> 
							<select class="form-control" name="cycle" id="cycle">
								<option value="0">--Select --</option>
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
									
									<option value="5">All Cycle Report</option>
							</select>
						</div>
						       
                    <div class="form-group" >
							<label for="exampleInputEmail1">TTUM TYPE</label> <select
									class="form-control" name="TTUM_TYPE" id="TTUM_TYPE">
							<option value="-">--Select --</optio>
									<option value="EXCEL">REPORT</option>
									<option value="TEXT">TTUM</option>
							</select>
						</div>
                    
                    
                    <div class="form-group">
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="fileDate" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
						</div>	
						
						
						</div>
						
					<div class="box-footer" style="text-align: center">
						<button type="button" onclick="processSettlement();" >Process</button>
							<button type="button" onclick="DownloadSettlement();" >download</button>
								<button type="button" onclick="ReportRollback();" >rollback</button>
						
					</div>
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
			style="font-size: 14px; font-weight: bold; border-radius: 3px;color: white; text-align: center; background: yellowgreen; margin-top: -490px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="success_msg" class="success_msg">
			<i class="fa fa-check" style="color: white;"></i>
				
			
		</div>
		
		
			<div
			style="font-size: 14px; font-weight: bold; color: white;border-radius: 3px; text-align: center; background: red; margin-top: -490px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="error_msg" class="error_msg">
			<i class="fa fa-close" style="color: white;"></i>
		</div>
		<div
			style="font-size: 14px; font-weight: bold; color: black;border-radius: 3px; text-align: center; background:#FFFF9E; margin-top: -490px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="alert_msg" class="alert_msg">
			<i class="fa fa-warning" style="color: black;"></i><span
				style="color: white; font-weight: bold; text-align: center;">     </span>
		</div></div>

<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

<img style="margin-left: 100px; margin-top: -60px;"
		src="images/g4.gif" alt="loader">


</div>
<script>

</script>