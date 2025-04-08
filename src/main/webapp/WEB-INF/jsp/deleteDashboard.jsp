<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@page import="com.recon.model.LoginBean"%>

<link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->
<link href="css/jquery-ui.min.css" media="all" rel="stylesheet"
	type="text/css" />

<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<script src="js/jquery-ui.min.js" type="text/javascript"></script>

<link href="js/datatable/jquery.dataTables.min.css" media="all"
	rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css"
	href="datatable/css/jquery.dataTables.css">
<link rel="stylesheet" type="text/css"
	href="datatable/css/buttons.dataTables.min.css">
<script src="js/jquery-ui.min.js" type="text/javascript"></script>
<script type="text/javascript"
	src="datatable/js/jquery.dataTables.js"></script>
<script type="text/javascript"
	src="datatable/js/jquery.dataTables.min.js"></script>
<script type="text/javascript"
	src="js/datatable/jquery.dataTables.min.js"></script>
	
		<link rel="stylesheet" href="/path/to/cdn/bootstrap.min.css" />
		<link rel="stylesheet" href="/path/to/cdn/bootstrap-icons.css" />
		<script src="/path/to/cdn/jquery.min.js"></script>
			<script src="/path/to/cdn/bootstrap.bundle.min.js"></script>
			<link rel="stylesheet" href="/path/to/bootstrap-toaster.css" />
			<script src="/path/to/bootstrap-toaster.min.js"></script>
<style>
<
link href ="datatable1 /jquery.dataTables.min.css " media ="all " rel 
	 ="stylesheet " type ="text /css " />
	/* Reset overflow value to hidden for all non-IE browsers. */ html>body div.tableContainer
	{
	overflow: hidden;
}

/* define width of table. IE browsers only                 */
div.tableContainer table {
	float: left;
	/* width: 740px */
}

/* define width of table. Add 16px to width for scrollbar.           */
/* All other non-IE browsers.                                        */
html>body div.tableContainer table {
	/* width: 756px */
	
}

/* set table header to a fixed position. WinIE 6.x only                                       */
/* In WinIE 6.x, any element with a position property set to relative and is a child of       */
/* an element that has an overflow property set, the relative value translates into fixed.    */
/* Ex: parent element DIV with a class of tableContainer has an overflow property set to auto */
thead.fixedHeader tr {
	position: relative;
}

/* set THEAD element to have block level attributes. All other non-IE browsers            */
/* this enables overflow to work on TBODY element. All other non-IE, non-Mozilla browsers */

/* make the TH elements pretty */
html>body tbody.scrollContent {
	display: block;
	height: 262px;
	overflow: auto;
	width: 100%
}

html>body thead.fixedHeader {
	display: table;
	overflow: auto;
	width: 100%
}

/* make TD elements pretty. Provide alternating classes for striping the table */
/* http://www.alistapart.com/articles/zebratables/                             */
tbody.scrollContent td, tbody.scrollContent tr.normalRow td {
	background:blue;
	border-bottom: none;
	border-left: none;
	border-right: 1px solid #CCC;
	border-top: 1px solid #DDD;
	padding: 2px 3px 3px 4px
}

tbody.scrollContent tr.alternateRow td {
	background:red;
	border-bottom: none;
	border-left: none;
	border-right: 1px solid #CCC;
	border-top: 1px solid #DDD;
	padding: 2px 3px 3px 4px
}

.scrollTable thead tr th {
   background: linear-gradient(10deg, #c2eaba, blue);
  border-radius: 8px;
  text-align: center;

}

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
button:hover{
background:linear-gradient(30deg, #c2eaba, blue);
}
label{
color: purple; text-align: left; font-weight: bold;font-size: 16px;display:block;
}
.leftSolid{
color: white; text-align: center; font-weight: bold;font-size: 16px;  background:white;
  border-radius: 8px;
}
.leftSolid{
color: white; text-align: center; font-weight: bold;font-size: 16px; background: white;
  border-radius: 8px;
}
.leftDotted{
color: black; text-align: center; font-weight: bold;font-size: 16px;  background: whitw;
  border-radius: 8px;
}

</style>


<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1 style="color: purple; text-align: center; font-weight: bold;">
			View Dashboard
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
			<li class="active">View Dashboard</li>
		</ol>
	</section>

	<!-- Main content -->
	<section class="content-header">
		<!-- Info boxes -->
		<div class="row">
				<div class="col-md-3"  style="width: 300px;"></div>
			<div class="col-md-4" >
				<form action="" method="">
					<div class="box" style="width: 500px; height: auto; background-color: white;margin-left: 250px; font-weight: bold; ">
						<div class="box-body" align="left">

							<label  >FILE TYPE</label> 
							<select class="form-control" id="txntype" name="txntype"  style=" width:480px;height:40px ; text-align: left;font-size: 14px;">
								<option value="0" >SELECT</option>
								<option value="SWITCH" align="left">SWITCH</option>
								<option value="CBS" align="left">CBS</option>
								<option value="RUPAY DSCR" align="left">RUPAY DSCR</option>
								<option value="RUPAY ALL DISPUTE" align="left">RUPAY ALL DISPUTE</option>
								<option value="RUPAY INTERNATIONAL" align="left">RUPAY INTERNATIONAL</option>
								<option value="RUPAY DOMESTIC" align="left">RUPAY DOMESTIC</option>
								<option value="RUPAY QSPARC" align="left">RUPAY QSPARC</option>
								<option value="NFS ISS" align="left">NFS ISSUER</option>
								<option value="NFS ACQ" align="left">NFS ACQUIRER</option>
								<option value="NFS NTSL" align="left">NFS NTSL</option>
									<option value="NFS ADJUSTMENT" align="left">NFS ADJUSTMENT</option>
								<option value="NFS LATE REV" align="left">NFS LATE REVERSAL</option>
							</select>
							&nbsp;&nbsp;&nbsp;
							
							
						
						</div>
						
									<div class="box-body" align="left">

						
							
							<label>DATE &nbsp;</label> <input type="text"  class="form-control"value="SELECT DATE"id="startDatePicker"
								name="fromDate" autocomplete="off" style=" width:480px;height:40px ; text-align: left;font-size: 14px;">&nbsp;&nbsp;&nbsp;

							
						</div><div class="box-body" align="center">


							<label>User ID </label> <input class="form-control"style=" width:480px;height:40px ; text-align: left;font-size: 14px;" type="text" id="text2"
								name="fromDate" autocomplete="off"
								value="<%=((LoginBean) request.getSession().getAttribute("loginBean")).getUser_id()%>">
							&nbsp;&nbsp;


						</div>
						<div class="box-body" align="center">

							<label>File Name</label> <input class="form-control"style=" width:480px;height:40px ; text-align: left;font-size: 14px;" type="text" id="text3"
								name="fromDate" autocomplete="off">&nbsp;&nbsp;

						</div>
						<div class="box-body" align="center">

							<label>Cycle</label> <input class="form-control"style=" width:480px;height:40px ; text-align: left;font-size: 14px;" "type="text" id="text4"
								name="fromDate" autocomplete="off">&nbsp;&nbsp;

						</div>
						
									<div class="box-body" align="center">

						
							<button type="button" 
								id="searchByATMID">Delete</button>
							<!-- onclick="searchByATMID()" -->
						 &nbsp;&nbsp; &nbsp;&nbsp;
							<button type="button" id="refersh">Refresh</button>
						</div>
						
						<!-- /.box-header -->
						<div class="box-body" id="atmTableDiv" style="width: 1000px; height: auto; background-color: white;margin-left: -250px; font-weight: bold; ">
							<table class="table table-bordered scrollTable" id="atmTable">
								<thead>
									<tr>
											<th class="leftSolid">File Type</th>
										<th class="leftSolid">File Count</th>
										<th class="leftSolid">File Uploaded</th>
										<th class="leftSolid">Remark</th>
									</tr>
								</thead>
								<tbody id="atmListBody">
								</tbody>
							</table>
						</div>

			
					</div>
					<!-- /.box -->
				</form>
			</div>
		</div>


	</section>
	<!-- /.content -->
</div>
<!-- /.content-wrapper -->
<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

<img style="margin-left: 100px; margin-top: -60px;"
		src="images/g4.gif" alt="loader">


</div>
<script>


	$(document).ready(function() {
		$("#startDatePicker").datepicker({
			dateFormat : "yy/mm/dd"
		});
		/* $("#whatsNew").modal('show'); */
		$("#atmTableDiv").hide();
		$("#filter").hide();
		$("#downloadExcel").hide();

		$("#searchByATMID").click(function() {
			$("#atmTable").DataTable().clear().destroy();
			searchByATMID();
		});

		$("#refersh").click(function() {
			location.reload();
		});

	});
	function searchByATMID() {
		debugger;
		showLoader();
		var type = $("#txntype").val();
		var fromDate = $("#startDatePicker").val();
		var username = $("#text2").val();
		var filename = $("#text3").val();
		var cycle = $("#text4").val();

		$.ajax({
			url : 'deleteViewFile.do',
			type : 'POST',
			/* processData : false,
			contentType : false, */
			data : {
				type : type,
				fromDate : fromDate,
				username : username,
				filename : filename,
				cycle : cycle
			},
			success : function(response) {
				debugger;
				hideLoader();
				if (response == "00") {
					alert("Successfully Deleted File!");
				} else if (response == "01") {
					alert("Please Enter Username, Date and File Type");
				} else if (response == "02") {
					alert("unable to delete file ");
				}

				var count = 0;
				console.log(response);

			},
			error : function(error) {
				alert("Error: FAILED TO FETCH DETAILS");
			}
		});
	}

	function showLoader(location) {
		$("#Loader").show();
	}
	function hideLoader(location) {
		$("#Loader").hide();
	}
</script>