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

<script type="text/javascript" src="js/GenerateEODExcel.js"></script>

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
			EOD Excel Process
			<!-- <small>Version 2.0</small> -->
		</h1>
		<!-- 	<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">CARD TO CARD TTUM</li>
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
					<form:form id="reportform" action="DownloadEODExcel.do"
						method="POST" commandName="unmatchedTTUMBean">



						<div class="box-body" id="subcat">



							<div class="form-group">
								<label for="exampleInputEmail1">Category</label> <select
									class="form-control" name="TTUMCategory" id="TTUMCategory">
									<option value="-">--Select --</option>
									<option value="VISA_REFUND_GL">VISA REFUND
										5165005139970</option>
									<option value="VISA_ACQ_CHARGEBACK_DOM_GL">VISA ACQUIRER
										CHARGEBACK DOMESTIC 5165005139934</option>
										<option value="VISA_REME_CHARGEBACK_DOM_GL">VISA REME
										CHARGEBACK DOMESTIC 5165005139933</option>
										<option value="VISA_ISS_POOL_GL">VISA ISSUER POOL 5165005139931</option>
										<option value="VISA_INT_BENE_CHARGEBACK_GL">VISA INTERNATIONAL BENE CHARGEBACK 5165005139938</option>
										<option value="VISA_ACQ_INT_POOL_GL">VISA ACQUIRER CHARGEBACK INT POOL GL 5165005139936</option>
										<option value="VISA_ACQ_DOM_POOL_GL">VISA ACQUIRER CHARGEBACK DOM POOL GL 5165005139932</option>
									<option value="NFS_ISS_GL">NFS ISSUER PAYBLE
										5165005139948</option>



								</select>
							</div>

							<div class="form-group">
								<label for="exampleInputEmail1">Opening Balance</label> <input
									type="text" class="form-control" id="OpeningBalance"
									placeholder="">
							</div>
							<div class="form-group">
								<label for="exampleInputEmail1">Closing Balance</label> <input
									type="text" class="form-control" id="ClosingBalance"
									placeholder="">
							</div>
							<div class="form-group" id="Date">
								<input type="text" id="rectyp" value="${category}"
									style="display: none"> <label
									for="exampleInputPassword1"> Date</label> <input
									class="form-control" readonly="readonly" id="localDate"
									name="localDate" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
							</div>
						</div>

						<div class="box-footer" style="text-align: center">
							<!-- <input type="submit" class="btn btn-primary" name="Process" id="Process" value="Process" onclick="return ValidateData();"> -->
							<button type="button" onclick="Process();">process</button>

							<button type="button" onclick="Download();">download</button>



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
		style="font-size: 14px; font-weight: bold; border-radius: 3px; color: white; text-align: center; background: yellowgreen; margin-top: -490px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
		id="success_msg" class="success_msg">
		<i class="fa fa-check" style="color: white;"></i>


	</div>


	<div
		style="font-size: 14px; font-weight: bold; color: white; border-radius: 3px; text-align: center; background: red; margin-top: -490px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
		id="error_msg" class="error_msg">
		<i class="fa fa-close" style="color: white;"></i>
	</div>
	<div
		style="font-size: 14px; font-weight: bold; color: black; border-radius: 3px; text-align: center; background: #FFFF9E; margin-top: -400px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
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