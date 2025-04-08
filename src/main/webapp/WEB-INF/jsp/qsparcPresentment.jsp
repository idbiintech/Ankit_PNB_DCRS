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

<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<link href="css/jquery-ui.min.css" media="all" rel="stylesheet"	type="text/css" />
<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<script type="text/javascript">
$(document).ready(function() {
	 $("#datepicker").datepicker({dateFormat:"yy/mm/dd", maxDate:0});
 });

window.history.forward();
function noBack() { window.history.forward(); }

function processRupayAdjustUpload() {
debugger;
	var frm = $('#uploadform');

	var fileDate = document.getElementById("datepicker").value;
	var cycle = document.getElementById("cycle").value;
	var network = document.getElementById("network").value;
	var userfile = document.getElementById("dataFile1");
	var subcate = document.getElementById("subcate").value;

	var oMyForm = new FormData();
	oMyForm.append('file', userfile.files[0])
	oMyForm.append('fileDate', fileDate);
	oMyForm.append('cycle', cycle);
	oMyForm.append('network', network);
	oMyForm.append('subcate', subcate);
	

	if (validateData()) {
		$.ajax({
			type : "POST",
			url : "rupayPresentmentUpload.do",
			enctype : "multipart/form-data",
			data : oMyForm,

			processData : false,
			contentType : false,
			// type : 'POST',
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

				}, 2500);

			},
			error : function() {
				$("#error_msg").empty();
				$("#error_msg").append("Unable To Upload");

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
function validateData() {
	debugger;
	var fileDate = document.getElementById("datepicker").value;
	var file = document.getElementById("dataFile1").value;
	var cycle = document.getElementById("cycle").value;
	var leng = file.length - 4;

	var network = document.getElementById("network").value;
	var subcate = document.getElementById("subcate").value;
	var fileExten = file.substring(leng, file.length);
	if (fileExten != '.csv') {
		$("#alert_msg").empty();
		$("#alert_msg").append("Please upload csv File");

	//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
			$("#alert_msg").empty();
		}, 2500);
		return false;
	}

	if (fileDate == "") {
		$("#alert_msg").empty();
		$("#alert_msg").append("Kindly select date");

	//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
			$("#alert_msg").empty();
		}, 2500);
		return false;
	}

	if (file == "") {
		$("#alert_msg").empty();
		$("#alert_msg").append("Kindly select file");

	//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
			$("#alert_msg").empty();
		}, 2500);
		return false;
	}
	if (cycle == "0") {
		$("#alert_msg").empty();
		$("#alert_msg").append("Please select cycle");

	//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
			$("#alert_msg").empty();
		}, 2500);
		return false;
	}

	if (network == "") {
		$("#alert_msg").empty();
		$("#alert_msg").append("Please select network");

	//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
			$("#alert_msg").empty();
		}, 2500);
		return false;
	}
	if (network == 'RUPAY' && subcate == "0") {
		$("#alert_msg").empty();
		$("#alert_msg").append("Please select Subcategory");

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
function showLoader(location) {

	$("#Loader").show();
}
function hideLoader(location) {

	$("#Loader").hide();
}

function getFields(e) {
	var network = document.getElementById("network").value;

	if (network == "RUPAY") {
		document.getElementById("subCat").style.display = '';
	} else {
		document.getElementById("subCat").style.display = '';
	}

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
			QSPARC PRESENTMENT FILE UPLOAD
			<!-- <small>Version 2.0</small> -->
		</h1>
<!-- 		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">QSPARC Presentment File Upload</li>
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
					
<form:form id="uploadform"  action="rupayPresentmentUpload.do" method="POST"  enctype="multipart/form-data" >

					<div class="box-body" id="subcat">
					 <div class="form-group" id="Date" style="display:${display}" >
							<label>File Date</label> <input
								class="form-control" name="fileDate" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
					 </div>	 
					 
					 <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Cycle</label> 
							<select class="form-control" path="cycle" id="cycle">
								<option value="0">--SELECT--</option>
									 <option value="1">1</option>
									<option value="2">2</option> 
									<option value="3">3</option>
									<option value="4">4</option>
							</select>
						</div>
						
					<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Cetegory</label> 
							<select class="form-control" path="network" id="network" onchange="getFields(this)">
								<option value="0">--SELECT--</option>
									
									<option value="QSPARC">QSPARC</option> 
							</select>
						</div>	
					 		 <div class="form-group" id="filetype" >
							<label for="exampleInputEmail1" >ONLINE/OFFLINE</label> 
							<select class="form-control" path="filetype" id="filetype">
								<option value="0">--SELECT--</option>
									 <option value="OFFLINE">OFFLINE</option>
									<option value="ONLINE">ONLINE</option> 
							</select>
						</div>	
					 <div class="form-group" id="subCat" >
							<label for="exampleInputEmail1" >Network</label> 
							<select class="form-control" path="subcate" id="subcate">
								<option value="0">--SELECT--</option>
									 <option value="DOMESTIC">DOMESTIC</option>
									<option value="INTERNATIONAL">INTERNATIONAL</option> 
							</select>
						</div>	
					 
						
					 <!-- <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="file" id="file" title="Upload File" />
                    </div> -->
      
                     <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="file" id="dataFile1" title="Upload File" /></td>
                    </div>
                    
                    </div>
					<div class="box-footer" style="text-align: center">
						<button type="button" onclick="processRupayAdjustUpload();" >Process</button>
			
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
			style="font-size: 14px; font-weight: bold; border-radius: 3px;color: white; text-align: center; background: yellowgreen; margin-top: -630px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="success_msg" class="success_msg">
			<i class="fa fa-check" style="color: white;"></i>
				
			
		</div>
		
		
			<div
			style="font-size: 14px; font-weight: bold; color: white;border-radius: 3px; text-align: center; background: red; margin-top: -630px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="error_msg" class="error_msg">
			<i class="fa fa-close" style="color: white;"></i>
		</div>
		<div
			style="font-size: 14px; font-weight: bold; color: black;border-radius: 3px; text-align: center; background: #FFFF9E; margin-top: -630px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
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