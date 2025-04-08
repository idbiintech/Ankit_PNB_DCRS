<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">


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
<!-- <link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" /> -->
<!-- <link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/DownloadReports.js"></script>
<!-- <script src="js/jquery.jtable.js" type="text/javascript"></script> -->
<!-- <script type="text/javascript" src="js/jquery.fancyform.js"></script> -->

<!-- <script type="text/javascript" src="js/jquery.ui.datepicker.js"></script> -->
<script type="text/javascript" src="js/commonScript.js"></script>

<script type="text/javascript">
	$(document).ready(function() {

		//alert("click");

		$("#datepicker").datepicker({
			dateFormat : "yy/mm/dd",
			maxDate : 0
		});
	});
</script>
<!-- 
<script type="text/ecmascript" src="js/jquery-1.9.1.js"></script> -->

<!-- This is the localization file of the grid controlling messages, labels, etc.
    <!-- We support more than 40 localizations -->








<!-- <script type="text/javascript" src="js/jquery-1.12.4.js"></script> -->

<title>Download Reports</title>

<style>
label {
	color: purple;
	font-weight: bold;
	font-size: 16px;
	text-transform: uppercase;
	display: block;
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
	background: linear-gradient(30deg, #c2eaba, blue);
}
</style>
</head>
<body>

	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
		<h1 style="color: purple; text-align: center; font-weight: bold;">
			DOWNLOAD REPORTS
			<!-- <small>Version 2.0</small> -->
		</h1>
		<!--           <ol class="breadcrumb">
            <li><a href="#"> Home</a></li>
            <li class="active">Download Reports</li>
          </ol> --> </section>

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
					<form:form name="reportform" id="reportform"
						action="DownloadReports1.do" method="POST"
						commandName="SettlementBean">
						<form role="form">

							<div class="box-body">
								<div class="form-group">
									<label for="exampleInputEmail1">SubCategory</label>
									<!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
									<form:select path="stsubCategory" class="form-control"
										id="stsubCategory"  	onchange="setfilename(this);" style="" placeholder="Select Your Favorite"
										data-search="true">
										<option value="-">--SELECT--</option>
										<c:forEach var="subcat" items="${subcategory}">
											<form:option value="${subcat}">${subcat}</form:option>
										</c:forEach>
									</form:select>
									<input type="hidden" name="CSRFToken" id="CSRFToken"
										value="${CSRFToken }"> <input type="text" id="rectyp"
										value="${category}" style="display: none" >
									<%-- <input type="text"  style="display: none" name="CSRFToken" id="CSRFToken" value ="${CSRFToken }">  --%>
									<form:input path="category" id="category" name="category"
										value="${category}" style="display: none" />
								</div>
								
								
								
						<div class="form-group" id="trsubcatid" style="display: none">
							<label for="exampleInputEmail1">TYPE</label>
							<!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->

							<select class="form-control" name="report" id="report">
								<option value="-">--Select --
									</optio>
								<option value="N1">NFS-ACQ-CBS-MATCHED-2</option>
								<option value="N2">NFS-ACQ-CBS-MATCHED-1</option>
								<option value="N3">NFS-ACQ-CBS-UNRECON-1</option>
								<option value="N4">NFS-ACQ-CBS-UNRECON-1</option>
								<option value="N5">NFS-ACQ-SWITCH-UNRECON-1</option>
								<option value="N6">NFS-ACQ-MATCHED-2</option>
								<option value="N7">NFS-ACQ-CBS-UNRECON-2</option>
								<option value="N8">NFS-ACQ-UNRECON-2</option>
								<option value="N9">NFS-ACQ-LATE-REVERSAL-MATCHED</option>
								<option value="N10">UNMATCHED</option>
								
							</select>
						</div>
							<div class="form-group" id="trsubcatidRupay" style="display: none">
							<label for="exampleInputEmail1">TYPE</label>
							<!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->

							<select class="form-control" name="report" id="report">
								<option value="-">--Select --
									</optio>
								<option value="N1">RUPAY_DOM-KNOCKOFF_CBS</option>
								<option value="N2">RUPAY_DOM-MATCHED-1_CBS</option>
								<option value="N3">RUPAY_DOM-UNRECON-1_CBS</option>
								<option value="N4">RUPAY_DOM-MATCHED-2_CBS</option>
								<option value="N5">RUPAY_DOM-UNRECON-2_CBS</option>
								<option value="N6">RUPAY_DOM-UNRECON-1_SWITCH</option>
								<option value="N7">RUPAY_DOM-MATCHED-2_RUPAY</option>
								<option value="N8">RUPAY_DOM-UNRECON-2_RUPAY</option>
						
								
							</select>
						</div>
								
								<div class="form-group">
									<label for="exampleInputPassword1">Date</label>
									<form:input path="datepicker" readonly="readonly"
										value="--SELECT--" id="datepicker" placeholder="dd-mm-yy"
										class="form-control" />
									<form:input type="hidden" path="stPath" value="D:\\Reports"
										name="path" id="path" readonly="true" />
								</div>


								<!-- <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" id="exampleInputFile">
                      <p class="help-block">Example block-level help text here.</p>
                    </div> -->
								<!-- <div class="checkbox">
                      <label>
                        <input type="checkbox"> Check me out
                      </label>
                    </div> -->
							</div>
							<!-- /.box-body -->

							<div class="box-footer" style="text-align: center">
								<button type="button" onclick="Process();">Download</button>
							</div>

						</form>
					</form:form>
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->

		</div>
		<!-- /.row --> </section>
		<div
			style="font-size: 14px; font-weight: bold; border-radius: 3px; color: white; text-align: center; background: yellowgreen; margin-top: -320px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="success_msg" class="success_msg">
			<i class="fa fa-check" style="color: white;"></i>


		</div>


		<div
			style="font-size: 14px; font-weight: bold; color: white; border-radius: 3px; text-align: center; background: red; margin-top: -320px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="error_msg" class="error_msg">
			<i class="fa fa-close" style="color: white;"></i>
		</div>
		<div
			style="font-size: 14px; font-weight: bold; color: black; border-radius: 3px; text-align: center; background: #FFFF9E; margin-top: -320px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="alert_msg" class="alert_msg">
			<i class="fa fa-warning" style="color: black;"></i><span
				style="color: white; font-weight: bold; text-align: center;">
			</span>
		</div>
	</div>
	<!-- /.content-wrapper -->
	<div align="center" id="Loader"
		style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

		<img style="margin-left: 100px; margin-top: -60px;"
			src="images/g4.gif" alt="loader">


	</div>

</body>
</html>