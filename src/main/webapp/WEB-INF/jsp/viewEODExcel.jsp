<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>



<link href="css/jquery-ui.min.css" media="all" rel="stylesheet"
	type="text/css" />
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
<script type="text/javascript" src="datatable/js/jquery.dataTables.js"></script>
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
	background: blue;
	border-bottom: none;
	border-left: none;
	border-right: 1px solid #CCC;
	border-top: 1px solid #DDD;
	padding: 2px 3px 3px 4px
}

tbody.scrollContent tr.alternateRow td {
	background: red;
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
	color: linear-gradient(45deg, pink, blue);
	text-align: left;
	font-weight: bold;
	font-size: 16px;
	display: block;
}

.leftSolid {
	color:linear-gradient(30deg, #c2eaba, blue);

	text-align: center;
	font-weight: bold;
	font-size: 16px;
	background: white;
	border-radius: 8px;
}

.leftSolid {
	color: white;
	text-align: center;
	font-weight: bold;
	font-size: 16px;
	background: white;
	border-radius: 8px;
}

.leftDotted {
	color: black;
	text-align: center;
	font-size: 13px;
	background: white;
	border-radius: 8px;
}
</style>


<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1 style="color: purple; text-align: center; font-weight: bold;">
			View EOD Excel Data
			<!-- <small>Versio\Bn 2.0</small> -->
		</h1>
		<!-- 		<ol class="breadcrumb">
			<li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
			<li class="active">View Dashboard</li>
		</ol> -->
	</section>

	<!-- Main content -->
	<section class="content-header">
		<!-- Info boxes -->
		<div class="row">
			<div class="col-md-3" style="width: 300px;"></div>
			<div class="col-md-4">
				<form action="" method="">
					<div class="box"
						style="width: 500px; height: auto; background-color: white; margin-left: 250px; font-weight: bold;">
						<div class="box-body" align="left">

							<label>FILE TYPE</label> <select class="form-control"
								id="txntype" name="txntype"
								style="width: 480px; height: 40px; text-align: left; font-size: 14px;">
								<option value="0">SELECT</option>
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





							</select> &nbsp;&nbsp;&nbsp;



						</div>

						<div class="box-body" align="left">



							<label>DATE &nbsp;</label> <input type="text"
								class="form-control" value="SELECT DATE" id="startDatePicker"
								name="fromDate" autocomplete="off"
								style="width: 480px; height: 40px; text-align: left; font-size: 14px;">&nbsp;&nbsp;&nbsp;


						</div>
						<div class="box-body" align="center">


							<button type="button" id="searchByATMID">View</button>
							<!-- onclick="searchByATMID()" -->
							&nbsp;&nbsp;
							<button type="button" id="refersh">Refresh</button>
						</div>

						<!-- /.box-header -->
						<div class="box-body" id="atmTableDiv"
							style="width: 1000px; height: auto; background-color: white; margin-left: -250px; font-weight: bold;">
							<table class="table table-bordered scrollTable" id="atmTable">
								<thead>
									<tr>

										<th class="leftSolid">REMARKS</th>
										<th class="leftSolid">BALANCE</th>
										<th class="leftSolid">DEBIT COUNT</th>
										<th class="leftSolid">DEBIT AMOUNT</th>
										<th class="leftSolid">CREDIT COUNT</th>
										<th class="leftSolid">CREDIT AMOUNT</th>

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
	<div
		style="font-size: 14px; font-weight: bold; border-radius: 3px; color: white; text-align: center; background: yellowgreen; margin-top: -490px; position: fixed; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
		id="success_msg" class="success_msg">
		<i class="fa fa-check" style="color: white;"></i>


	</div>


	<div
		style="font-size: 14px; font-weight: bold; color: white; border-radius: 3px; text-align: center; background: red; margin-top: -340px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
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
	<!-- /.content -->
</div>
<!-- /.content-wrapper -->
<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

	<img style="margin-left: 100px; margin-top: -60px;" src="images/g4.gif"
		alt="loader">


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
		
		$("#searchByATMID").click(function(){
			$("#atmTable").DataTable().clear().destroy();
			searchByATMID();
		});
		
		$("#refersh").click(function(){
			location.reload();
		});
		
	});
	 function ValidateData() {

			var fileDate = document.getElementById("startDatePicker").value;
			var txntype = document.getElementById("txntype").value;
			debugger;

			if (fileDate == "SELECT DATE") {
				$("#alert_msg").empty();
				$("#alert_msg").append("Please select date");

				//	document.getElementById("breadcrumb").style.display = 'none';

				$("#alert_msg").modal('show');

				setTimeout(function() {

					$("#alert_msg").modal('hide');
					//document.getElementById("breadcrumb").style.display = '';
					$("#alert_msg").empty();
				}, 2000);
				return false;
			}else if (txntype == "0") {
				$("#alert_msg").empty();
				$("#alert_msg").append("Please select File type");

				//	document.getElementById("breadcrumb").style.display = 'none';

				$("#alert_msg").modal('show');

				setTimeout(function() {

					$("#alert_msg").modal('hide');
					//document.getElementById("breadcrumb").style.display = '';
					$("#alert_msg").empty();
				}, 2000);
				return false;
			}else{
				return true;
			}
			


		}
	
	function searchByATMID() {
		debugger;
	

		var fromDate = $("#startDatePicker").val();
		var txntype = document.getElementById("txntype").value;
		if(ValidateData()){
		showLoader();
		$.ajax({
			url : 'searchEODExcelFile.do',
			type : 'POST',
			/* processData : false,
			contentType : false, */
			data : {
				txntype: txntype,
				fromDate : fromDate
			},
			success : function(response) {
				debugger;
				hideLoader();
				
	
				var count = 0;
				console.log(response);
console.log(response.length)
				
				if(response.length == 0){
					
					$("#error_msg").append("FAILED TO FETCH DETAILS");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
					
						$("#error_msg").empty();
						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);
				
				}else{

					$("#success_msg").append("Data Fetched Successfully!!");

				//	document.getElementById("breadcrumb").style.display = 'none';

				$("#success_msg").modal('show');

				setTimeout(function() {

					$("#success_msg").modal('hide');
				
					$("#success_msg").empty();
					//document.getElementById("breadcrumb").style.display = '';

				}, 2500);
					var atmList = response.size;
					var str = response[0].filename;
					var newstr = str.replaceAll(",", "<br>");
					$.each(response,
					function(i, item) {
						$('<tr>').html(
							"<td  class='leftDotted'>"
									+ response[count].remark
									+ "</td>
									<td  class='leftDotted'>"
									+ response[count].balance
									+ "</td>
									<td  class='leftDotted'>"
									+ response[count].credit_count
									+ "</td>
									<td  class='leftDotted'>"
									+ response[count].credit_amount
									+ "</td><td  class='leftDotted'>"
									+ response[count].debit_count
									+ "</td><td  class='leftDotted'>"
									+ response[count].debit_amount
									+ "</td>").appendTo(
							"#atmListBody");
						count++;
					});
					$("#atmTableDiv").show();
					$("#atmTable").DataTable({
						retrieve : true,
						"scrollCollapse" : false,
						"info" : true,
						"paging" : true
					}).columns.adjust().draw();
					$("#filter").show();
				}
			},
			error : function(error) {
				
				$("#error_msg").append("Error: FAILED TO FETCH DETAILS");

				//document.getElementById("breadcrumb").style.display = 'none';

				$("#error_msg").modal('show');

				setTimeout(function() {

					$("#error_msg").modal('hide');
					//document.getElementById("breadcrumb").style.display = '';
					$("#error_msg").empty();
				}, 2500);
	
			}
		});
		}
	}
	
	function showLoader(location) {
		$("#Loader").show();
	}

	function hideLoader(location) {
		$("#Loader").hide();
	}
</script>
