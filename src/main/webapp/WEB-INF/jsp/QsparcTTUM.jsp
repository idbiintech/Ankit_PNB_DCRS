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

<script type="text/javascript" src="js/RupaySettlementTTUM.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();
  
    $("#datepicker").datepicker({dateFormat:"dd/M/yy", maxDate:0});
    });
    

window.history.forward();
function noBack() { window.history.forward(); }


</script>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1 style ="text-align: center; ">
			Rupay Qsparc TTUM
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Rupay QsparcS TTUM</li>
		</ol>
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
		<div class="col-md-3"></div>
			<!-- left column -->
			<div class="col-md-6">
				<!-- general form elements -->
				<div class="box box-primary">
					<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>
<form:form id="processform"  action="DownloadQsparcTTUM.do" method="POST"  commandName="rupaySettlementBean" >
<div class="box-body">
					 <%-- <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1">Sub Category</label> 
							<select class="form-control" name="subcategory" id="subcategory">
								<option value="-">--Select --</option>
									<option value="DOMESTIC">Domestic</option>
									<option value="INTERNATIONAL">International</option>
							</select>
						</div> --%>
		
                    
                   <%--  <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Cycle</label> 
							<select class="form-control" name="cycle" id="cycle">
								<option value="0">--Select --</option>
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
							</select>
						</div> --%>
                    
                    <div class="form-group">
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="fileDate" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
						</div>
						
					 <!-- <div class="form-group" id="Date">
							<label for="exampleInputPassword1">Rectification Details</label> 
							<div>
							<label for="exampleInputPassword1">Add / Subtract</label> 
							<select class="form-control" name="sign" id="sign">
								<option value="0">--Select --</option>
									<option value="-">SUBTRACT (-)</option>
									<option value="+">ADD (+)</option>	
							</select>
							</div>
							<div>
							<label for="exampleInputPassword1">Amount</label> 
							<input class="form-control" type = "text" name="rectAmt" id="rectAmt" />
							</div>
					</div> -->
					</div>		
						
					<div class="box-footer" style="text-align: center">
						<a onclick="processSettlementQsparc();" class="btn btn-primary">Process</a>
						<a onclick="DownloadSettlementQsparc();" class="btn btn-info">Download</a>
						<!-- <a class="btn btn-success" onclick="Rectify();">Rectify Settlement </a> -->
					</div>
</form:form>
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->

		</div>
		<!-- /.row -->
<!-- /.content-wrapper -->
</section></div>

<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

<img style="margin-left: 100px; margin-top: -60px;"
		src="images/g4.gif" alt="loader">


</div>
<script>

</script>