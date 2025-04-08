<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<meta http-equiv="X-UA-Compatible" content="IE=10">
<link href="css/jquery-ui.min.css" media="all" rel="stylesheet"
	type="text/css" />
<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setDateHeader("Expires", 0);
response.setHeader("Pragma", "no-cache");
response.setHeader("X-Frame-Options", "deny");
%>

<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/manualCompare.js"></script>

<!-- <script>
	$(document).ready(function() {
		$("#upi_NPCIUpload").addClass('active');
	});
</script>
 -->

<style>
button {
	background: linear-gradient(45deg, pink, blue);
	border: none;
	color: white;
	padding: 10px 26px;
	font-size: 16px;
	border-radius: 20px;
	cursor: pointer;
	transaction: background 0.3s ease;
}

button:hover {
	background: linear-gradient(30deg, #c2eaba, blue);
}

label {
	color: ManualUpload;
	font-weight: bold;
	font-size: 16px;
	display: block;
}

#toast {
	visibility: hidden;
	min-width: 350px;
	background-color: black;
	color: white;
	text-align: center;
	border-radius: 5px;
	padding: 20px;
	position: fixed;
	z-index: 1;
	left: 50%;
	top: 30px;
	transform: translateX(-50%);
	opacity: 0;
	transition: opacity 0.5s, top 0.5s;
	display: flex;
	align-items: center;
	justify-content: center;
}

#toast i {
	margin-right: 10px;
	font-size: 20px;
}

#toast.show {
	visibility: visible;
	opacity: 1;
	top: 50px;
}
</style>
<script type="text/javascript">
	$(document).ready(function() {

		//alert("click");

		$("#datepicker").datepicker({
			dateFormat : "yy/mm/dd",
			maxDate : 0
		});
	});

	/* 
	 function showToast() {
	 var toast = document.getElementById("toast");
	 toast.className = "show";
	 setTimeout(function() {
	 toast.className = toast.className.replace("show", "");
	 }, 3000); // Toast stays visible for 3 seconds
	 }
	 */
</script>


<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1 style="color: purple; text-align: center; font-weight: bold;">
			File Upload
			<!-- <small>Version 2.0</small> -->
		</h1>

		<!-- 	<ol class="breadcrumb" id="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">File Upload</li>
		</ol>
 -->
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
			<!-- left column -->
			<div class="col-md-4"></div>
			<div class="col-md-4">
				<!-- general form elements -->
				<div class="box box-prima">

					<form:form id="uploadform" commandName="CompareSetupBean"
						method="POST" action="manualUploadFile.do"
						enctype="multipart/form-data">
						<div class="box-body">
							<div class="form-group">
								<label for="exampleInputEmail1">FILE NAME</label>
								<!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
								<input type="hidden" name="CSRFToken" id="CSRFToken"
									value="${CSRFToken }">
								<form:select class="form-control" path="filename" id="filename"
									onchange="setfilename(this);">
									<form:option value="0">SELECT</form:option>
									<c:forEach var="configfilelist" items="${configBeanlist}">
										<form:option id="${configfilelist.stFileName}"
											value="${configfilelist.stFileName}">${configfilelist.stFileName}</form:option>

									</c:forEach>
									<%-- <form:option id="ctf" value="CTF" >CTF</form:option> --%>
								</form:select>
								<input type="hidden" id="headerlist" value="">

								<form:hidden path="stFileName" id="stFileName" />
							</div>
							<div class="form-group" id="trfileType" style="display: none">
								<label for="exampleInputEmail1">FILE TYPE</label>
								<!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->


								<form:select class="form-control" path="fileType" id="fileType">

									<form:option value="ONLINE">ONLINE</form:option>
									<form:option value="MANUAL">MANUAL</form:option>

								</form:select>
							</div>

							<div class="form-group" id="trcategory" style="display: none">
								<label for="exampleInputEmail1">CATEGORY</label>
								<!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->


								<form:select class="form-control" path="category" id="category">

									<option value="">SELECT</option>
									<option value="NFS">NFS</option>
									<option value="RUPAY">RUPAY</option>

									<option value="VISA_ACQ">VISA ACQUIRER</option>

									<option value="VISA">VISA ISSUER</option>

									<option value="MASTERCARD">MASTERCARD</option>
									<option value="QSPARC">QSPARC</option>

									<option value="ICD">ICD</option>
									<option value="DFS">DFS</option>
									<option value="JCB">JCB</option>

									<option value="CARDTOCARD">CARD TO CARD</option>

									<!-- 	d				<option value="POS">ONUS POS</option> -->

									<!-- <option value="POS">POS</option> -->
								</form:select>
							</div>

							<div class="form-group" id="trsubcat" style="display: none">
								<label for="exampleInputEmail1">SUBCATEGORY</label>
								<!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->


								<form:select class="form-control" path="stSubCategory"
									id="stSubCategory">
									<form:option value="-">SELECT</form:option>
									<form:option value="ACQUIRER">ACQUIRER</form:option>
									<form:option value="ISSUER">ISSUER</form:option>
									<form:option value="ACQUIRER_DOM">ACQUIRER DOM</form:option>
									<form:option value="ISSUER_POS">ISSUER POS</form:option>
									<option value="CARDTOCARD">CARD TO CARD</option>
								</form:select>
							</div>




						



							<div class="form-group" id="trsubcatid" style="display: none">
								<label for="exampleInputEmail1">DOM/INT</label>
							

								<form:select class="form-control" path="stSubCategoryid"
									id="stSubCategoryid">
									<form:option value="-">SELECT</form:option>
									<form:option value="DOMESTIC">DOMESTIC</form:option>
									<form:option value="INTERNATIONAL">INTERNATIONAL</form:option>
								</form:select>
							</div>



							<div class="form-group">
								<label>DATE</label>
								<form:input path="fileDate" class="form-control"
									readonly="readonly" id="datepicker" value="SELECT DATE" />
							</div>
							<div class="form-group">
								<label for="exampleInputFile"></label> <input type="file"
									name="file" id="dataFile1" title="Upload File" />
								</td>
								<!-- <p class="help-block">Example block-level help text here.</p> -->
							</div>

						</div>
						<!-- /.box-body -->

						<div class="box-footer">
							<!-- <button type="button" value="UPLOAD" id="upload"
								onclick="return processFileUpload();" class="btn btn-primary">Upload</button> -->
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<button type="button" value="UPLOAD" id="upload"
								onclick="return processFileUpload();">Upload</button>



							<!-- <div id="toast">
    <i class="fas fa-check-circle"></i>
    <span>This is a toast notification!</span>
</div>
<button onclick="showToast()">Show Toast</button>

 -->


							<!-- 			<button type="button" value="UPLOAD" id="upload"
								onclick="return processFileUpload();">Upload</button>
									<button type="button" value="UPLOAD" id="upload"
								onclick="return processFileUpload();">Upload</button>
<!-- 
							<button type="button" id="NpciFiles" onclick="getNpciUploadedFiles()"
							>View Uploaded Files</button> -->
						</div>
						<div class="box-footer" style="display: none">
							<input type="text" id="dummy" value="012">
						</div>


					</form:form>
					
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->

		</div>
		<div
			style="font-size: 14px; font-weight: bold; border-radius: 3px; color: white; text-align: center; background: yellowgreen; margin-top: -350px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="success_msg" class="success_msg">
			<i class="fa fa-check" style="color: white;"></i>&nbsp;&nbsp;&nbsp;&nbsp;

			<button type="button" class="close" data-dismiss="modal"
				style="margin-top: -25px; margin-right: 20px;">&times;</button>
		</div>


		<div
			style="font-size: 14px; font-weight: bold; color: white; border-radius: 3px; text-align: center; background: red; margin-top: -350px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="error_msg" class="error_msg">
			<i class="fa fa-times-circle" style="color: white;"></i>&nbsp;&nbsp;&nbsp;&nbsp;
			<button type="button" class="close" data-dismiss="modal"
				style="margin-top: -25px; margin-right: 20px;">&times;</button>
		</div>
		<div
			style="font-size: 14px; font-weight: bold; color: black; border-radius: 3px; text-align: center; background: #FFFF9E; margin-top: -350px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="alert_msg" class="alert_msg">
			<i class="fa fa-warning" style="color: black;"></i>&nbsp;&nbsp;&nbsp;&nbsp;
		</div>
		<div
			style="font-size: 14px; font-weight: bold; color: black; border-radius: 3px; text-align: center; background: #FFFF9E; margin-top: -590px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="alert_msg2" class="alert_msg2">
			<i class="fa fa-warning" style="color: black;"></i>&nbsp;&nbsp;&nbsp;&nbsp;
		</div>
		<!-- /.row -->
	</section>
</div>
<!-- /.content-wrapper -->

<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

	<img style="margin-left: 100px; margin-top: -60px;" src="images/g4.gif"
		alt="loader">


</div>
