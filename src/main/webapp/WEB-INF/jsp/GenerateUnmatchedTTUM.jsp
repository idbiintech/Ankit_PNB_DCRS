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

<script type="text/javascript" src="js/UnMatchedTTUMGeneration.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();
  
    $("#datepicker").datepicker({dateFormat:"dd-M-yy", maxDate:0});
    
    $("#localDate").datepicker({dateFormat:"dd-M-yy", maxDate:0});
    
    });
    


    

window.history.forward();
function noBack() { window.history.forward(); }


</script>
<style>
button{
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
button:hover{

background: linear-gradient(to right, purple 50%, purple 10%);
}
label{
color: purple;  font-weight: bold;font-size: 16px;display:block;
}
</style>

<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
				<h1 style="color: purple; text-align: center; font-weight: bold;">
			${category} TTUM GENERATION
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">TTUM Generation T1</li>
		</ol>
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
			<!-- left column -->
			<div class="col-md-4"></div>
			<div class="col-md-4">
				<!-- general form elements -->
				<div class="box box-primary">
					<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>

<form:form id="processform"  action="DownloadUnmatchedTTUM.do" method="POST"  commandName="unmatchedTTUMBean" >

					<div class="box-body" id="subcat">

					<div class="form-group" style="display:none">
							<label for="exampleInputEmail1" onchange="CallDollar()">Category</label> 
							<form:input class="form-control" type="text" path="category" id="category" 
							value="${unmatchedTTUMBean.category}" readOnly="true"/>
						</div>
						
						<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" onchange="CallDollar()">Sub Category</label> 
							<select class="form-control"
								name="stSubCategory" id="stSubCategory">
								<option value="-">--Select --</option>
								<c:forEach var="subcat" items="${subcategory}">
									<option value="${subcat}">${subcat}</option>
								</c:forEach>
							</select>

						</div>
			<%-- 			
                        
						 <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >File Name</label> 
							<form:select class="form-control" path="fileName" id="fileName">
								<option value="0">--Select --</option>
									<option value="CBS">CBS/Host</option>
									<option value="NETWORK">Network</option>	
									<option value="SWITCH">Switch</option>
							</form:select>
						</div>  --%>
						
						
						<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >TTUM Type</label> 
							<input type="text" id="category" value="${category}" style="display: none"> 
							<form:select class="form-control" path="typeOfTTUM" id="typeOfTTUM" onchange="getFields(this)">
								<option value="0">--Select --</option>
									<option value="DROP">DROP</option>
									<option value="LATE PRESENTMENT">LATE PRESENTMENT</option>
									<option value="PROACTIVE">PROACTIVE</option>	
									<option value="DEBIT SURCHARGE">DEBIT SURCHARGE</option>
									<option value="CREDIT SURCHARGE">CREDIT SURCHARGE</option>
												</form:select>
						</div>
						
						<div class="form-group" id ="date" style="display:none">
							<label for="exampleInputPassword1">Recon Date</label> 
							<input class="form-control" name="fileDate" readonly="readonly"	id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
						</div>

						<div class="form-group" id="ttumDate" style="display:none">
							<input class="form-control" name="localDate" readonly="readonly" id="localDate" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
						</div>
						</div>
						
					<!-- </div> -->
					<!-- /.box-body -->

					<div class="box-footer" style="text-align: center">
						<button type="button" onclick="Process();" >Process</button>
						<button type="button" onclick="Download();" >DOWNLOAD</button>
						
					</div>
					<div id="processTbl"></div>
</form:form>
</div>
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->
</section>
		</div>
		<!-- /.row -->
	
<!-- </div> -->
<!-- /.content-wrapper -->

<div align="center" id="Loader"
	style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">
<img style="margin-left: 100px; margin-top: -200px;"
		src="images/g4.gif" alt="loader">

</div>
<script>
function CallDollar()
{
	debugger;
	alert("sas");
	}
</script>