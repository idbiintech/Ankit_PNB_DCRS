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
<script type="text/javascript" src="js/GenerateRupayAdjustmentTTUM.js"></script>

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
			RUPAY ADJUSTMENT PROCESS
			<!-- <small>Version 2.0</small> -->
		</h1>
<!-- 		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">RUPAY Adjustment TTUM</li>
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
									<option value="-">--Select --</option>
									<option value="DOMESTIC">DOMESTIC</option>
									<option value="INTERNATIONAL">INTERNATIONAL</option>
								</select>
							</div>
							<div class="form-group">
								<label for="exampleInputEmail1">Category</label> <select
									class="form-control" name="cate" id="cate">
									<option value="-">--SELECT--</optio>
									
									<option value="RUPAY">RUPAY</option>
									<option value="RUPAY_INT">RUPAY_INT</option>
									<option value="RUPAY_INT_MFC">RUPAY_INT Member Fund Collection</option>
								</select>
							</div>
									<div class="form-group">
								<label for="exampleInputEmail1">TTUM TYPE</label> <select
									class="form-control" name="TTUM_TYPE" id="TTUM_TYPE">
									<option value="-">--SELECT --</optio>
									<option value="EXCEL">REPORT</option>
									<option value="TEXT">TTUM</option>
								</select>
							</div>
				
							<div class="form-group">
								<label for="exampleInputEmail1">Adjustment Type</label> <select
									class="form-control" name="adjType" id="adjType">
									<option value="-">--SELECT --</option>
									<option value="Refund">REFUND</option>
											<option value="RUPAY_OFFLINE"> OFFLINE REFUND</option>
								<!-- 	<option value="CHBK">Chargeback Raise</option> -->
									<option value="Chargeback Deemed Acceptance">CHARGEBACK DEEMED ACCEPTANCE</option>
									<option value="Partial Chargeback Acceptance">PARTIAL CHARGEBACK ACCEPTANCE</option>
									<option value="Chargeback Acceptance">CHARGEBACK ACCEPTANCE</option>
											<option value="Good Faith Acceptance">GOOD FAITH ACCEPTANCE</option>
												<option value="Good Faith Deemed Acceptance">GOOD FAITH DEEMED ACCEPTANCE</option>
													<option value="NPCI Fee Disbursement">NPCI Fee Disbursement</option>
													<option value="NPCI Fee Collection">NPCI Fee Collection</option>
													<option value="Pre-Arbitration Deemed Acceptance">Pre-Arbitration Deemed Acceptance</option>
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


</div></div>
<script>
	
</script>