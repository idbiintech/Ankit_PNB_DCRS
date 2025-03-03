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
	var TTUM_TYPE = document.getElementById("TTUM_TYPE").value;
	var issacqtype = document.getElementById("issacqtype").value;
	
	//var form = document.getElementById("microAtmReport");
	//var oMyForm = new FormData();
	//oMyForm.append('datepicker', datepicker);
	if (Validation()) {
		$.ajax({
			type : 'POST',
			url : 'rollbackVisaSETTL.do',
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
				issacqtype : issacqtype,
				TTUM_TYPE : TTUM_TYPE
				
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

				}, 2009500);
			},
			error : function() {
				$("#success_msg").empty();
				$("#error_msg").append("Unable To Process!!");

				//	document.getElementById("breadcrumb").style.display = 'none';

				$("#error_msg").modal('show');

				setTimeout(function() {

					$("#error_msg").modal('hide');
					$("#error_msg").empty();

					//document.getElementById("breadcrumb").style.display = '';

				}, 2599900);
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
		$("#alert_msg").empty();
		$("#alert_msg").append("Please select date!!");

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


function seerule(e) {
	
	document.getElementById("fileValue").value=e;
	
	window.open("../DebitCard_Recon/SeeRule.do" , 'SeeRule', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
}


function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}

function ValidateData()
{
	var subcategory = document.getElementById("subcategory").value;
	var fileDate = document.getElementById("datepicker").value;
	var TTUM_TYPE = document.getElementById("TTUM_TYPE").value;
	debugger;
	
	if(fileDate == "")
	{
		$("#alert_msg").empty();
		$("#alert_msg").append("Please select Date!!");

		//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
		return false;
	}
	if(subcategory=="-"){
		$("#alert_msg").empty();

		$("#alert_msg").append("Please select Subcategory!!");

		//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
		return false;
	}
	if(TTUM_TYPE == "-")
	{	$("#alert_msg").empty();
	
		$("#alert_msg").append("Please select TTUM_TYPE!!");

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

function processSettlement(){


	debugger;
	var frm = $('#processform');
	var subcategory = document.getElementById("subcategory").value;
	var fileDate = document.getElementById("datepicker").value;
	var TTUM_TYPE = document.getElementById("TTUM_TYPE").value;
	var fileType = document.getElementById("issacqtype").value;

	if(ValidateData())  {
		var oMyForm = new FormData();
		oMyForm.append('subcategory', subcategory);
		oMyForm.append('fileDate',fileDate);
		oMyForm.append('TTUM_TYPE',TTUM_TYPE);
		oMyForm.append('fileType',fileType);
		$.ajax({
			type : "POST",
			url : "VisaSettlementProcess.do",
			data :oMyForm ,

			processData : false,
			contentType : false,
			beforeSend : function() {
				showLoader();
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

				}, 2555500);
				/*document.getElementById("subcategory").value="-";
				document.getElementById("datepicker").value="";
				document.getElementById("cycle").value = "0";*/
			},

			error : function(err) {
				hideLoader();
				$("#error_msg").empty();
				$("#error_msg").append("Unable To Process!!");

				//	document.getElementById("breadcrumb").style.display = 'none';

				$("#error_msg").modal('show');

				setTimeout(function() {

					$("#error_msg").modal('hide');
					$("#error_msg").empty();

					//document.getElementById("breadcrumb").style.display = '';

				}, 2555500);
			},
			complete : function(data) {

				hideLoader();

			},
		});
	}
}

	function DownloadSettlement() {
		debugger;
		var frm = $('#processform');
		var subcategory = document.getElementById("subcategory").value;
		var fileDate = document.getElementById("datepicker").value;
		var TTUM_TYPE = document.getElementById("TTUM_TYPE").value;
		var fileType = document.getElementById("issacqtype").value;
		
		if(ValidateData())  {
			var oMyForm = new FormData();
			oMyForm.append('subcategory', subcategory);
			oMyForm.append('fileDate',fileDate);
			oMyForm.append('TTUM_TYPE',TTUM_TYPE);
			oMyForm.append('fileType',fileType);
			$.ajax({
				type : "POST",
				url : "ValidateVisaSettlement.do",
				data :oMyForm ,

				processData : false,
				contentType : false,
				beforeSend : function() {
					showLoader();
				},
				success : function(response) {
					if(response == "success")
					{	$("#success_msg").empty();
						$("#success_msg").append("Reports are getting downloaded. Please Wait");

						//	document.getElementById("breadcrumb").style.display = 'none';

						$("#success_msg").modal('show');

						setTimeout(function() {

							$("#success_msg").modal('hide');

							$("#success_msg").empty();
							//document.getElementById("breadcrumb").style.display = '';

						}, 2500);
						document.getElementById("processform").submit();
					}
					else
					{

						$("#error_msg").empty();
						$("#error_msg").append(response);

						//	document.getElementById("breadcrumb").style.display = 'none';

						$("#error_msg").modal('show');

						setTimeout(function() {

							$("#error_msg").modal('hide');

							$("#error_msg").empty();
							//document.getElementById("breadcrumb").style.display = '';

						}, 2555500);
						document.getElementById("subcategory").value="-";
						document.getElementById("datepicker").value="";
						document.getElementById("TTUM_TYPE").value="=-";
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





</script>
<style>
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


background:linear-gradient(30deg,  #c2eaba, blue);
}
</style>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
				<h1 style="color: purple; text-align: center; font-weight: bold;">
			VISA SETTLEMENT PROCESS
			<!-- <small>Version 2.0</small> -->
		</h1>
<!-- 		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Visa Settlement Process</li>
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
<form:form id="processform"  action="DownloadSettlementVisa.do" method="POST"  commandName="rupaySettlementBean" >
				<div class="box-body">
					 <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1">Sub Category</label> 
							<select class="form-control" name="subcategory" id="subcategory">
								<option value="-">--Select --</option>
									<option value="DOMESTIC">DOMESTIC</option>
									<option value="INTERNATIONAL">INTERNATIONAL</option>
							</select>
						</div>
		
		
                    
                  <%--   <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Cycle</label> 
							<select class="form-control" name="cycle" id="cycle">
								<option value="0">--Select --</option>
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
							</select>
						</div> --%>
						       
                    <div class="form-group" >
							<label for="exampleInputEmail1">TTUM TYPE</label> <select
									class="form-control" name="TTUM_TYPE" id="TTUM_TYPE">
							<option value="-">--Select --</optio>
									<option value="EXCEL_ACQ">ACQUIRER REPORT</option>
									<option value="EXCEL_ISS">ISSUER REPORT</option>
									<option value="TTUM_ACQ">ACQUIRER TTUM</option>
									<option value="TTUM_ISS">ISSUER TTUM</option>
							</select>
						</div>
						
						  <div class="form-group" >
							<label for="exampleInputEmail1">Category</label> <select
									class="form-control" name="issacqtype" id="issacqtype">
							<option value="-">--Select --</optio>
									<option value="ACQUIRER">ACQUIRER</option>
									<option value="ISSUER">ISSUER</option>
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
					<button type="button" onclick="DownloadSettlement();" >Download</button>
					<button type="button" onclick="ReportRollback();" >RollBack</button>
						
					</div>
</form:form>
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->

		</div>
		<!-- /.row -->
<!-- /.content-wrapper -->
</section>  <div
			style="font-size: 14px; font-weight: bold; border-radius: 3px;color: white; text-align: center; background: yellowgreen; margin-top: -440px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="success_msg" class="success_msg">
			<i class="fa fa-check" style="color: white;"></i>
				
			
		</div>
		
		
			<div
			style="font-size: 14px; font-weight: bold; color: white;border-radius: 3px; text-align: center; background: red; margin-top: -440px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="error_msg" class="error_msg">
			<i class="fa fa-close" style="color: white;"></i>
		</div>
		<div
			style="font-size: 14px; font-weight: bold; color: black;border-radius: 3px; text-align: center; background:#FFFF9E; margin-top: -440px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
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