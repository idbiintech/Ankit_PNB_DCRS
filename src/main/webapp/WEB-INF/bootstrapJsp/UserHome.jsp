

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
<style>
/* Reset overflow value to hidden for all non-IE browsers. */
html>body div.tableContainer {
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
	height: 600px;
	overflow: auto;
	width: 100%;
	'
}

html>body thead.fixedHeader {
	display: table;
	overflow: auto;
	width: 100%
}

/* make TD elements pretty. Provide alternating classes for striping the table */
/* http://www.alistapart.com/articles/zebratables/                             */
tbody.scrollContent td, tbody.scrollContent tr.normalRow td {
	background: #FFF;
	border-bottom: none;
	border-left: none;
	border-right: 1px solid #CCC;
	border-top: 1px solid #DDD;
	padding: 2px 3px 3px 4px
}

tbody.scrollContent tr.alternateRow td {
	background: #EEE;
	border-bottom: none;
	border-left: none;
	border-right: 1px solid #CCC;
	border-top: 1px solid #DDD;
	padding: 2px 3px 3px 4px
}

.scrollTable thead tr th {
	background: linear-gradient(10deg, #c2eaba, blue);
	border-radius: 9px;
}

.col-md-6 {
	margin-top: 35px;
}

.toast {
	visibility: hidden;
	min-height: 250px;
	background-color: #333;
	color: #fff;
	text-align: center;
	border-radius: 2px;
	padding: 16px;
	position: fixed;
	z-index: 1;
	right: 30px;
	top: 30px;
	font-size: 17px;
}

.box-title {
	font-size: 17px;
	color: black;
}

.leftSolid {
	color: white;
	text-align: center;
	font-weight: bold;
	font-size: 16px;
	background: black;
	border-radius: 8px;
}

.leftDotted {
	color: pink;
	text-align: center;
	font-weight: bold;
	font-size: 16px;
	background: black;
	border-radius: 8px;
}
</style>


<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1 style="color: purple; text-align: center; font-weight: bold;">
			DASHBOARD
			<!-- <small>Version 2.0</small> -->
		</h1>
		<%-- 		<ol class="breadcrumb">
			<li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
			<li class="active">Dashboard ${getUsertype}</li>
		</ol>  --%>
	</section>

	<!-- Main content -->
	<section class="content">
		<!-- Info boxes -->
		<div class="row">
			<div class="col-md-6">
				<div class="box">
					<div class="box-header">
						<h3 class="box-title">UPLOAD STATUS</h3>
					</div>
					<!-- /.box-header -->
					<div class="box-body">
						<table class="table table-bordered scrollTable">
							<thead class="fixedHeader">
								<tr>
									<th class="leftSolid">Network File</th>
									<th class="leftSolid" style="width: 190px; text-align: center;">CBS</th>
									<th class="leftSolid" style="width: 190px; text-align: center;">Switch</th>
									<th class="leftSolid" style="width: 190px; text-align: center;">Networks</th>



								</tr>
							</thead>
							<tbody class="scrollContent">
								<c:forEach var="UploadBean" items="${UploadBean}">
									<tr>
										<td style="width: 190px; text-align: center;"><span
											class="badge bg-green">${UploadBean.switch_date}</span></td>
										<td style="width: 190px; text-align: center;"><span
											class="badge bg-green">${UploadBean.cbs_date}</span></td>
										<td style="width: 210px; text-align: center;"><span
											class="badge bg-green">${UploadBean.network_date}</span></td>
										<td style="width: 190px; text-align: center;">${UploadBean.category}${UploadBean.subCategory }</td>



									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
					<!-- /.box-body -->

				</div>
				<!-- /.box -->
			</div>

			<div class="col-md-6">
				<div class="box">
					<div class="box-header">
						<h3 class="box-title">RECON STATUS</h3>
					</div>
					<!-- /.box-header -->
					<div class="box-body">
						<table class="table table-bordered scrollTable">
							<thead class="fixedHeader">
								<tr>
									<th class="leftSolid" style="width: 190px; text-align: center;">Switch</th>
									<th class="leftSolid" style="width: 190px; text-align: center;">CBS</th>
									<th class="leftSolid">Network File</th>
									<th class="leftSolid" style="width: 190px; text-align: center;">Networks</th>



								</tr>
							</thead>
							<tbody class="scrollContent">
								<c:forEach var="CompareBean" items="${CompareBean}">
									<tr>
										<td style="width: 210px; text-align: center;"><span
											class="badge bg-green">${CompareBean.switch_date}</span></td>
										<td style="width: 210px; text-align: center;"><span
											class="badge bg-green">${CompareBean.cbs_date}</span></td>
										<td style="width: 210px; text-align: center;"><span
											class="badge bg-green">${CompareBean.network_date}</span></td>
										<td style="width: 190px; text-align: center;">${CompareBean.category}${CompareBean.subCategory }</td>



									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
					<!-- /.box-body -->

				</div>
				<!-- /.box -->
			</div>
		</div>



	</section>
	<div
		style="font-size: 14px; font-weight: bold; border-radius: 3px; color: white; text-align: center; background: yellowgreen; position: fixed; margin-top: -810px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
		id="success_msg" class="success_msg">
		<i class="fa fa-check" style="color: white;"></i>&nbsp;&nbsp;&nbsp;&nbsp;

		<!-- 	<button type="button" class="close" data-dismiss="modal"
		style="margin-top: -25px; margin-right: 20px;">&times;</button> -->
	</div>

	<!-- /.content -->
</div>
<!-- /.content-wrapper -->


<!-- <div
	style="font-size: 11px; font-weight: bold; color: #fff; text-align: center; margin-top: 750px; margin-left: 1150px;">
	Designed & Developed by<a href="http://www.idbiintech.com/"> IDBI
		INTECH LTD</a>
</div>
<div
	style="font-size: 11px; font-weight: bold; color: #fff; text-align: center; margin-top: 0px; margin-left: 1150px;">Version
	2.0</div> -->

<script type="text/javascript">
	/*  $(function() {
	 var visit=GetCookie("COOKIE1");

	 if (visit==null){
	 alert("First Time");
	 }
	 var expire=new Date();
	 expire=new Date(expire.getTime()+7776000000);
	 document.cookie="COOKIE1=here; expires="+expire;
	 }); 
	
	 function GetCookie(cname) {
	 var name = cname + "=";
	 var decodedCookie = decodeURIComponent(document.cookie);
	 var ca = decodedCookie.split(';');
	 for(var i = 0; i <ca.length; i++) {
	 var c = ca[i];
	 while (c.charAt(0) == ' ') {
	 c = c.substring(1);
	 }
	 if (c.indexOf(name) == 0) {
	 return c.substring(name.length, c.length);
	 }
	 }
	 return null;
	 } */

	$(document).ready(
			function() {
				debugger;
				var getUsertype = "${getUsertype}";

				if (getUsertype == "ADMIN") {

					$("#success_msg").append("Welcome To Admin Dashboard!!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');

						//document.getElementById("breadcrumb").style.display = '';

					}, 3500);
				} else if (getUsertype == "USER") {

					$("#success_msg").append(
							"You Do Not Have Access For Admin Home!!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');

						//document.getElementById("breadcrumb").style.display = '';

					}, 3500);
				} else {

					$("#success_msg").append("Welcome To IRECON !!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');

						//document.getElementById("breadcrumb").style.display = '';

					}, 3500);

				}

			});

	$(document).ready(function() {

		$("#whatsNew").modal('show');
		setTimeout(function() {

			$("#whatsNew").modal('hide');
		}, 1500);

	});
</script>