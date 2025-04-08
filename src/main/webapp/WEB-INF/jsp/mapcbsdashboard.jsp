<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<link href="css/jquery-ui.min.css" media="all" rel="stylesheet"
	type="text/css" />

<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

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
    background: linear-gradient(45deg, pink, blue);

}

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
background:linear-gradient(30deg, #c2eaba, blue);
}
label{
color: purple; text-align: center; font-weight: bold;font-size: 16px;text-transform: uppercase;display:block;
}
option{
color: purple; text-align: center; font-weight: bold;font-size: 14px;text-transform: uppercase;
}
select{
color: purple; text-align: center; font-weight: bold;font-size: 14px;text-transform: uppercase;
}
</style>


<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1 style="color: purple; text-align: center; font-weight: bold;">
			MAP CBS DATA
			<!-- <small>Version 2.0</small> -->
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
				<div class="col-md-3"  style="width: 300px;"></div>
			<div class="col-md-6" >
				<form action="" method="">
					<div class="box" style="width: 700px; margin-left: 150px; height: auto; background-color: white;  font-weight: bold; ">
	
						
						
									<div class="box-body" align="center">

						
							
							<label>          DATE &nbsp;</label> <input class="form-control" type="text"  value="SELECT DATE"id="startDatePicker"
								name="fromDate" autocomplete="off" style=" width:300px;height:40px ; text-align: center; font-weight: bold;font-size: 14px;text-transform: uppercase;">&nbsp;&nbsp;&nbsp;

							
						</div>
									<div class="box-body" align="center">

						
							<button type="button" 
								 id="searchByATMID">PROCESS</button> <!-- onclick="searchByATMID()" -->
								&nbsp;&nbsp;
						 	<button type="button"  id="refersh">Refresh</button>
						</div>
						
						<!-- /.box-header -->
						<div class="box-body" id="atmTableDiv">
							<table class="table table-bordered scrollTable" id="atmTable">
								<thead>
									<tr>
										<th style="color: white;">FILE TYPE</th>
										<th style="color: white;">FILE COUNT</th>
										<th style="color: white;">FILE UPLOADED</th>
										<th style="color: white;">REMARK</th>
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
			style="font-size: 14px; font-weight: bold; border-radius: 3px;color: white; text-align: center; background: yellowgreen; margin-top: -220px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="success_msg" class="success_msg">
			<i class="fa fa-check" style="color: white;"></i>
				<button type="button" class="close" data-dismiss="modal" style="margin-top:-20px; margin-right: 20px;">&times;</button> 
			
		</div>
		
		
			<div
			style="font-size: 14px; font-weight: bold; color: white;border-radius: 3px; text-align: center; background: red; margin-top: -220px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="error_msg" class="error_msg">
			<i class="fa fa-close" style="color: white;"></i>
			<button type="button" class="close" data-dismiss="modal" style="margin-top:-20px; margin-right: 20px;">&times;</button> 
		</div>
		<div
			style="font-size: 14px; font-weight: bold; color: black;border-radius: 3px; text-align: center; background: #FFFF9E; margin-top: -220px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="alert_msg" class="alert_msg">
			<i class="fa fa-warning" style="color: black;"></i><span
				style="color: white; font-weight: bold; text-align: center;">     </span>
		</div>
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
		
		$("#searchByATMID").click(function(){
			$("#atmTable").DataTable().clear().destroy();
			searchByATMID();
		});
		
		$("#refersh").click(function(){
			location.reload();
		});
		
	});

	function searchByATMID() {
		debugger;
		

		var Date = $("#startDatePicker").val();

	if(ValidateData()){
		showLoader();
		$.ajax({
			url : 'mapcbsD.do',
			type : 'POST',
			/* processData : false,
			contentType : false, */
			data : {
			
				Date : Date
			
			},
			success : function(response) {
				debugger;
				hideLoader();
		
				$("#success_msg").empty();
					$("#success_msg").append(response);

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

				console.log(response);

			},
			error : function(error) {
				$("#error_msg").empty();
				$("#error_msg").append(error);

				//	document.getElementById("breadcrumb").style.display = 'none';

				$("#error_msg").modal('show');
			}
		});
	}
	}
	 function ValidateData() {

			var fileDate = document.getElementById("startDatePicker").value;
			
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
			}else{
				return true;
			}
			


		}
	
	function showLoader(location) {
		$("#Loader").show();
	}

	function hideLoader(location) {
		$("#Loader").hide();
	}
</script>
