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

<!-- <script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script> -->
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/IccwmanualCompare.js"></script>
<script type="text/javascript" src="js/uploadfilesjs.js"></script>
<!-- <link href="/RetailSmart/Jquery/jquery.multiselect.css" rel="stylesheet"/>
 -->

<meta name="_csrf" content="${CSRFToken}" />
<meta name="_csrf_header" content="CSRFToken" />

<!-- <script>
	var $j = jQuery.noConflict(true);
</script> -->
<script type="text/javascript">
	$(document).ready(function() {
		$("#datepicker").datepicker({
			dateFormat : "dd/mm/yy",
			maxDate : 0
		});

		$('#filename').on('change', function() {
			debugger;
			/* alert("value is" + $('#filename').val()); */
			if ($('#filename').val() == 'NPCI') {
				$("#div1").hide();
				$("#div2").show();
			}
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

transaction: background 0.3s ease;
}
label{
color: purple;  font-weight: bold;font-size: 16px;display:block;
}
button:hover{

background:linear-gradient(30deg, #c2eaba, blue);
}
</style>
<!--  -->
<form:form id="uploadform" action="IcccManualUploadFile.do"
	method="POST" commandName="CompareSetupBean"
	enctype="multipart/form-data">

	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
					<h1 style="color: purple; text-align: center; font-weight: bold;">
				ICCW FILE UPLOAD
				<!-- <small>Version 2.0</small> -->
			</h1>
			<ol class="breadcrumb">
				<li><a href="#"> Home</a></li>
				<li class="active">ICCW File Upload</li>
			</ol>
		</section>

		<!-- Main content -->


		<section class="content">


			<div class="row">
				<!-- left column -->
				<div class="col-md-4"></div>
				<div class="col-md-4 animated slideInDown">
					<!-- general form elements -->
					<div class="box box-primary">


						<div class="box-body">

							<div class="form-group">
								<input type="hidden" name="CSRFToken" id="CSRFToken"
									value="${CSRFToken }"> <label for="exampleInputFile">
									Upload Files</label>
								<form:select class="form-control" path="filename" id="filename" onchange="getSubCategory(this)">
									<!-- onchange="setfilename(this);" -->

									<option value="category">--SELECT--</option>
									<option value="SWITCH">SWITCH</option>
									<option value="CBS">CBS</option>
									<!-- <option value="NPCI">NPCI</option>
									<option value="NTSL">NTSL</option> -->

								</form:select>
							</div>
							
							
							 <div class="form-group" style="display: none" path="fileCategory" id="fileCategory">
							<label for="exampleInputEmail1" >File Type</label> 
							<form:select class="form-control" path="fileType" id="fileType">
								<option value="0">--Select --</option>
									<option value="CBS">REGULAR CBS</option>
									 <option value="REVERSAL">REVERSAL CBS</option>
							</form:select>
						</div>
							</div>

							<div class="form-group">
								<label for="exampleInputPassword1">Date</label>
								<form:input path="fileDate" class="form-control" type="text"
									readonly="readonly" id="datepicker" placeholder="dd/mm/yyyy"
									autocomplete="off" />
							</div>
							<div class="form-group">
								<label for="exampleInputFile">File Upload</label> <input
									type="file" name="files" id="dataFile1" title="Upload File"
									multiple="multiple" />
								</td>
							</div>

							<div class="box-body">
								<table id="fileTable">
								</table>
							</div>

							<div id="div1" class="box-footer" style="text-align: center;" >
								<button type="button" value="UPLOAD" id="upload"
									onclick="return processFileUpload();" >Upload</button>
							</div>

							<div class="progress" id="loading" style="display: none">
								<div
									class="progress-bar progress-bar-info progress-bar-striped active"
									role="progressbar" aria-valuenow="20" aria-valuemin="0"
									aria-valuemax="100" style="width: 1%;" id="myBar">
									<!-- <span>20%</span> -->
									<span id="progress"></span>
								</div>
							</div>

							<!--style="display: none"  -->
							<div id="div2" style="display: none" class="box-footer">
								<button type="button" id="upload" class="button"
									onclick="loadData()">LOAD FILES</button>
								<button type="button" id="upload" class="button"
									onclick="npciuploadData()">UPLOAD ALL FILES</button>
								<!-- <span id="uploadSpin"></span> -->

							</div>
</form:form>
<div class="box-footer" style="display: none">
	<input type="text" id="dummy" value="012">
</div>

</div>
<!-- /.box-body -->

<div class="box-body">
	<table id="fileTable">
	</table>
	<br>

	<section></section>

	<div class="progress" id="loading" style="display: none">
		<div
			class="progress-bar progress-bar-info progress-bar-striped active"
			role="progressbar" aria-valuenow="20" aria-valuemin="0"
			aria-valuemax="100" style="width: 1%;" id="myBar">
			<!-- <span>20%</span> -->
			<span id="progress"></span>
		</div>
	</div>
</div>



</div>
<!-- /.box -->



</div>
<!--/.col (left) -->

</div>
<!-- /.row -->
</section>

</div>

<div align="center" id="Loader"
	style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">
<img style="margin-left: 100px; margin-top: -200px;"
		src="images/g4.gif" alt="loader">


	</body>
	</html>