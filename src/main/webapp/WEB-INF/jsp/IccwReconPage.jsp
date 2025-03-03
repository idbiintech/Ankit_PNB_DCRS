
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setDateHeader("Expires", 0);
response.setHeader("Pragma", "no-cache");
response.setHeader("X-Frame-Options", "deny");
%>

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<!-- <script type="text/javascript" src="js/IccwmanualCompare.js"></script>
<script type="text/javascript" src="js/uploadfilesjs.js"></script> -->
<script type="text/javascript" src="js/iccwreconProcess.js"></script>


<%-- <meta name="_csrf" content="${CSRFToken}" />
<meta name="_csrf_header" content="CSRFToken" /> --%>

<!-- <script>
	var $j = jQuery.noConflict(true);
</script> -->
<script type="text/javascript">
	$(document).ready(function() {
		$("#datepicker").datepicker({
			dateFormat : "dd/mm/yy",
			maxDate : 0
		});

	});
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

background:linear-gradient(30deg, yellow, blue);
}
label{
color: purple;  font-weight: bold;font-size: 16px;display:block;
}
</style>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
<h1 style="color: purple; text-align: center; font-weight: bold;">
ICCW RECON PROCESS</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Recon Process</li>
		</ol>
	</section>

	<section class="content">
		<div class="row">
			<!-- left column -->
			<div class="col-md-4"></div>
			<div class="col-md-4">
				<!-- general form elements -->
				<div class="box box-prima">


					<div class="box-body">



						<div class="form-group">
							<input type="hidden" name="CSRFToken" id="CSRFToken"
								value="${CSRFToken }"> <label for="exampleInputFile">
								Process </label> 
							 <select class="form-control" value="filename"
								id="filename" name="filename">
								onchange="setfilename(this);"

								<option value="category">--SELECT--</option>
								<option value="ACQ">ACQUIRER</option>
								<option value="ISS">ISSUER</option>
							</select>
						</div>

						<div class="form-group">
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="fileDate" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />


						</div>

					</div>



					<div class="box-footer" style="text-align: center">
					<button type="button" onclick="Process();" >UPLOAD</button>
	
					</div>
				</div>
				<div id="processTbl"></div>
				<%-- </form> --%>
			</div>
			<!-- /.box -->




		</div>
</div>
<div align="center" id="Loader"
	style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">
<img style="margin-left: 100px; margin-top: -200px;"
		src="images/g4.gif" alt="loader">

</div>
<script>
	function CallDollar() {
		debugger;
		alert("sas");
	}
</script>