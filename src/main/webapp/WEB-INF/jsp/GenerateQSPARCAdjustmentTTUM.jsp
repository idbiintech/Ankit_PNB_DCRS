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
		var adjType = document.getElementById("adjType").value;
		var fileDate = document.getElementById("dailypicker").value;

		debugger;
		if (fileDate == "") {
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
		return true;

	}



	function DownloadAdjTTUM() {
	debugger;
		var frm = $('#reportform');

		var adjType = document.getElementById("adjType").value;
		var fileDate = document.getElementById("dailypicker").value;
		var subcate = document.getElementById("stSubCategory").value;
		var cate = document.getElementById("cate").value;
			var TTUM_TYPE = document.getElementById("TTUM_TYPE").value;

		var oMyForm = new FormData();

		oMyForm.append('fileDate', fileDate);
		oMyForm.append('adjType', adjType);
		oMyForm.append('subcate', subcate);
		oMyForm.append('cate', cate);
	oMyForm.append('TTUM_TYPE', TTUM_TYPE);
		if (ValidateData()) {
			$.ajax({

				type: 'POST',
				url: 'ValidateDownloadRupayAdjTTUM.do',
				async: true,
				beforeSend: function() {
					showLoader();
				},

				data: {
					fileDate: fileDate,
					adjType: adjType,
					subcate: subcate,
					cate: cate,
					TTUM_TYPE: TTUM_TYPE
				},
				success: function(response) {

					// response = "success";
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
				error: function() {
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
				complete: function(data) {

					hideLoader();

				},
			});

		}

	}

	function processAdjTTUM() {

		var frm = $('#reportform');

		var adjType = document.getElementById("adjType").value;
		var fileDate = document.getElementById("dailypicker").value;
		var subcate = document.getElementById("stSubCategory").value;
		var cate = document.getElementById("cate").value;

		var oMyForm = new FormData();

		oMyForm.append('fileDate', fileDate);
		oMyForm.append('adjType', adjType);
		oMyForm.append('subcate', subcate);
		oMyForm.append('cate', cate);



		if (ValidateData()) {
			$.ajax({

				type: 'POST',
				url: 'RupayAdjustmentProcess.do',
				async: true,
				beforeSend: function() {
					showLoader();
				},

				data: {
					fileDate: fileDate,
					adjType: adjType,
					subcate: subcate,
					cate: cate
	
				},
				success: function(response) {
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
				error: function() {
					$("#error_msg").empty();
					$("#error_msg").append("Unable To Upload!!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
						$("#error_msg").empty();

						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);

				},
				complete: function(data) {

					hideLoader();

				},
			});

		}

	}
	


	function ReportRollback() {
		debugger;
		var subcategory = document.getElementById("cate").value;
		var fileDate = document.getElementById("dailypicker").value;
		var ADJTYPE = document.getElementById("adjType").value;
		
		//var form = document.getElementById("microAtmReport");
		//var oMyForm = new FormData();
		//oMyForm.append('datepicker', datepicker);
		if (Validation()) {
			$.ajax({
				type : 'POST',
				url : 'rollbackRUPAYADJ.do',
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
					ADJTYPE : ADJTYPE
					
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
					$("#error_msg").append("Unable To Download!!");

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
		return true;
	}

	function skipSettlement() {

		var frm = $('#reportform');

		var timePeriod = document.getElementById("timePeriod").value;

		var filename = document.getElementById("fileName").value;
		var category = document.getElementById("rectyp").value;

		/*
		 * var userfile = document.getElementById("dataFile1"); alert("file is
		 * "+userfile);
		 */
		// var CSRFToken = $('[name=CSRFToken]').val();
		var stSubCategory = document.getElementById("stSubCategory").value;
		/*
		 * var fileDate = document.getElementById("datepicker").value; alert("file
		 * date "+fileDate);
		 */
		if (timePeriod == "Daily") {
			var datepicker = document.getElementById("dailypicker").value;
			var cycle = document.getElementById("cycle").value;
		} else {
			var datepicker = document.getElementById("monthpicker").value;
			cycle = "0";
		}

		var oMyForm = new FormData();

		// oMyForm.append('file',userfile.files[0])
		oMyForm.append('fileName', filename);
		oMyForm.append('category', category);
		oMyForm.append('stSubCategory', stSubCategory);
		oMyForm.append('datepicker', datepicker);
		oMyForm.append('cycle', cycle);

		if (ValidateNTSLData()) {
			$.ajax({

				type: 'POST',
				url: 'SkipSettlement.do',
				async: true,
				beforeSend: function() {
					showLoader();
				},

				data: {
					fileDate: datepicker,
					stSubCategory: stSubCategory,
					timePeriod: timePeriod,
					cycle: cycle,
					filename: filename
				},
				success: function(response) {
					debugger;
					hideLoader();

					alert(response);
					document.getElementById("fileName").value = "0";
					// alert("2");
					document.getElementById("rectyp").value = category;
					// alert("3");
					document.getElementById("stSubCategory").value = "-";
					// alert("4");
					document.getElementById("cycle").value = "0";
					document.getElementById("datepicker").value = "";
					document.getElementById("monthpicker").value = "";

					document.getElementById("Month").style.display = 'none';
					document.getElementById("subCate").style.display = 'none';
					document.getElementById("cycles").style.display = 'none';
					document.getElementById("Date").style.display = 'none';

				},
				error: function() {
					alert("Error Occured");

				},
				complete: function(data) {

					hideLoader();

				},
			});

		}

	}

	/*
	 * function getCycle(e) { if(e.value == "Daily") {
	 * document.getElementById("cycles").style.display = '';
	 * document.getElementById("Date").style.display = '';
	 * document.getElementById("Skip").style.display = '';
	 * document.getElementById("Month").style.display = 'none';
	 * document.getElementById("subCate").style.display = 'none'; } else {
	 * document.getElementById("cycles").style.display = 'none';
	 * document.getElementById("Date").style.display = 'none';
	 * document.getElementById("Skip").style.display = 'none';
	 * document.getElementById("Month").style.display = '';
	 * document.getElementById("subCate").style.display = '';
	 *  }
	 * 
	 *  }
	 */
	function getCycle(e) {
		// GET filename
		var fileName = document.getElementById("fileName").value;
		// alert("Filename is "+fileName);
		if (fileName == "0") {
			alert("Please select File first");
		} else {
			if (fileName == "NTSL-NFS") {
				if (e.value == "Daily") {
					document.getElementById("cycles").style.display = '';
					document.getElementById("Date").style.display = '';
					document.getElementById("Month").style.display = 'none';
					document.getElementById("subCate").style.display = 'none';

				} else {
					document.getElementById("cycles").style.display = 'none';
					document.getElementById("Date").style.display = 'none';
					document.getElementById("Month").style.display = '';
					document.getElementById("subCate").style.display = '';
				}
			} else {
				if (e.value == "Daily") {
					document.getElementById("cycles").value = "1";
					document.getElementById("cycles").style.display = 'none';
					document.getElementById("Date").style.display = '';
					document.getElementById("Month").style.display = 'none';
					document.getElementById("subCate").style.display = 'none';

				} else {
					document.getElementById("cycles").style.display = 'none';
					document.getElementById("Date").style.display = 'none';
					document.getElementById("Month").style.display = '';
					document.getElementById("subCate").style.display = '';
				}

			}
		}
	}
	function FileNameChange(e) {
		if (e.value == "NTSL-NFS") {
			document.getElementById("cycles").style.display = '';
		} else {
			// document.getElementById("timePeriod").value = "0";
			// document.getElementById("timePeriod").value = "0";
			document.getElementById("cycles").style.display = 'none';
			// document.getElementById("Date").style.display = 'none';
			// document.getElementById("Month").style.display = 'none';
			// document.getElementById("subCate").style.display = 'none';
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


background:linear-gradient(30deg, #c2eaba, blue);
}
label{
color: purple;  font-weight: bold;font-size: 16px;text-transform: uppercase;display:block;
}
</style>

<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
					<h1 style="color: purple; text-align: center; font-weight: bold;">
			QSPARC ADJUSTMENT PROCESS
			<!-- <small>Version 2.0</small> -->
		</h1>
		<!-- <ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">QSPARC Adjustment TTUM</li>
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
					<form id="reportform" action="DownloadRupayAdjTTUM.do"
						method="POST" enctype="multipart/form-data">



						<div class="box-body" id="subcat">

							<div class="form-group">
								<label for="exampleInputEmail1">Sub Category</label> <select
									class="form-control" name="stSubCategory" id="stSubCategory">
									<option value="-">--SELECT--</option>
									<option value="DOMESTIC">DOMESTIC</option>
									<option value="INTERNATIONAL">INTERNATIONAL</option>
								</select>
							</div>
							<div class="form-group">
								<label for="exampleInputEmail1">Category</label> <select
									class="form-control" name="cate" id="cate">
									<option value="-">--SELECT--</optio>
									<option value="QSPARC">QSPARC</option>
									
								</select>
							</div>
									<div class="form-group">
								<label for="exampleInputEmail1">TTUM TYPE</label> <select
									class="form-control" name="TTUM_TYPE" id="TTUM_TYPE">
									<option value="-">--SELECT-</optio>
									<option value="EXCEL">REPORT</option>
									<option value="TEXT">TTUM</option>
								</select>
							</div>
				
							<div class="form-group">
								<label for="exampleInputEmail1">Adjustment Type</label> <select
									class="form-control" name="adjType" id="adjType">
									<option value="-">--SELECT--</option>
									<option value="Refund">REFUND</option>
								<!-- 	<option value="CHBK">Chargeback Raise</option> -->
									<option value="Chargeback Deemed Acceptance">CHARGEBACK DEEMED ACCEPTANCE</option>
									<option value="Partial Chargeback Acceptance">PARTIAL CHARGEBACK ACCEPTANCE</option>
									<option value="Chargeback Acceptance">CHARGEBACK ACCEPTANCE</option>
		<!-- 			<option value="PREARBACC">PreArbitration Acceptance</option>
									<option value="VOID">Void</option> -->
									<!-- ABC  FEES CHANGE TO CHBK CHBKPRESENT
									<option value="RPR">RePresentment Raise</option>
									
									<option value="PRERJ">PreArbitration Declined</option> -->



								</select>
							</div>

							<div class="form-group" id="Date">
								<label for="exampleInputPassword1">Date</label> <input
									class="form-control" name="fileDate" readonly="readonly"
									id="dailypicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
							</div>
							<!-- <div class="form-group">
							<label for="exampleInputPassword1">To Date</label> <input
								class="form-control" name="toDate" readonly="readonly"
								id="toDate" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
					</div> -->

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
							<button type="button" 
								onclick="processAdjTTUM();">Process</button>
							<button type="button"
								onclick="DownloadAdjTTUM();">Dowload Report</button>
								<button type="button"
								onclick="ReportRollback();">Roll Back</button>
								
								
							<!-- 	<button type="button" id ="Skip" class="btn btn-danger" onclick="skipSettlement();">Skip Settlement</button> -->
							<!-- 	<a onclick="processSettlement();" class="btn btn-primary">Process</a>
						<a onclick="skipSettlement();" class="btn btn-primary">Skip Settlement</a> -->

						</div>
						<!-- <div id="processTbl"></div> -->
					</form>
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->

		</div>
		<!-- /.row -->
		<!-- /.content-wrapper -->
	</section>
	
	    <div
			style="font-size: 14px; font-weight: bold; border-radius: 3px;color: white; text-align: center; background: yellowgreen; margin-top: -570px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="success_msg" class="success_msg">
			<i class="fa fa-check" style="color: white;"></i>
				
			
		</div>
		
		
			<div
			style="font-size: 14px; font-weight: bold; color: white;border-radius: 3px; text-align: center; background: red; margin-top: -570px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="error_msg" class="error_msg">
			<i class="fa fa-close" style="color: white;"></i>
		</div>
		<div
			style="font-size: 14px; font-weight: bold; color: black;border-radius: 3px; text-align: center; background:#FFFF9E; margin-top: -570px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
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