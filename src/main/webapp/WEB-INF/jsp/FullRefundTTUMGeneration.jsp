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

<script type="text/javascript" src="js/FullRefundTTUMGeneration.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		debugger;
		//$('#dollar_field').hide();

		$("#datepicker").datepicker({
			dateFormat : "dd-M-yy",
			maxDate : 0
		});

	});

	window.history.forward();
	function noBack() {
		window.history.forward();
	}
</script>
<style>
label{
color: purple;  font-weight: bold;font-size: 16px;display:block;
}

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

background:linear-gradient(30deg, #c2eaba, blue);
}

</style>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1 style="color: purple; text-align: center; font-weight: bold;">
			REFUND TTUM GENERATION
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Refund TTUM Generation</li>
		</ol>
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
					<form:form id="reportform" action="DownloadFullRefundTTUM.do"
						method="POST" commandName="refundTTUMBean">


						<div class="box-body" id="subcat">

							<div class="form-group" style="display:${display}">
								<label for="exampleInputEmail1">Network :</label>
								<form:select class="form-control" path="category" id="category">
									<option value="0">--Select --</option>
									<option value="RUPAY">Rupay</option>
									<!-- <option value="MASTERCARD">Mastercard</option> -->
									<option value="VISA">Visa</option>
								</form:select>
							</div>

							<%--   <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Network :</label> 
							<form:select class="form-control" path="fileName" id="fileName">
								<option value="0">--Select --</option>
									<option value="RUPAY">Rupay</option>
									<option value="MASTERCARD">Mastercard</option>
							</form:select>
						</div> --%>

							<div class="form-group">
								<label for="exampleInputPassword1">Date</label> <input
									class="form-control" path="fileDate" name="fileDate"
									readonly="readonly" id="datepicker" placeholder="dd/mm/yyyy"
									title="dd/mm/yyyy" />
							</div>
						</div>

						<div class="box-footer" style="text-align: center">
						<!-- 	<button type="button" onclick="getCount();">Count/Amount</button> -->
							<button type="button" onclick="process();">process</button>

							<button type="button" onclick="downloadReports();">download
								text</button>

							<form:form id="excelform" action="DownloadExcelTTUM.do"
								method="POST" commandName="refundTTUMBean">
							</form:form>
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
</div>
<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

<img style="margin-left: 100px; margin-top: -60px;"
		src="images/g4.gif" alt="loader">


</div>
<script>
	
</script>