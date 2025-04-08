




<%@page import="com.recon.model.LoginBean"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/countdown/2.6.0/countdown.min.js"></script>
<script>
	$(document).ready(function() {
		startTime();
	});
	function checkTime(i) {
		if (i < 10) {
			i = "0" + i
		}
		; // add zero in front of numbers < 10
		return i;
	}
	function startTime() {

		var today = new Date();
		var h = today.getHours();
		var m = today.getMinutes();
		var s = today.getSeconds();
		m = checkTime(m);
		s = checkTime(s);

		document.getElementById('hours').innerHTML = h + ':' + m + ':' + s;
		var t = setTimeout(startTime, 500);
		if (h >= 6 && h <= 11) {

			$("#mrng").show();
			$("#evng").hide();
			$("#aftrn").hide();
			$('#mrng')
					.html(
							"Good Morning <i class='fa fa-sun-o' aria-hidden='true' style='font-size: 15px;color: #f9eb35;'></i>");
		} else if (h >= 11 && h <= 16) {

			$("#aftrn").show();
			$("#evng").hide();
			$("#mrng").hide();
			$('#aftrn')
					.html(
							"Good Afternoon <i class='fa fa-cloud' aria-hidden='true' style='font-size: 15px;color: #b3fbff;'></i>");
		} else if (h >= 17 && h <= 23) {

			$("#evng").show();
			$("#aftrn").hide();
			$("#mrng").hide();
			$('#evng')
					.html(
							"Good Evening <i class='fa fa-moon-o' aria-hidden='true' style='font-size: 15px;color: #fff;'></i>");
		} else if (h >= 0 && h <= 5) {
			$("#evng").show();
			$("#aftrn").hide();
			$("#mrng").hide();
			$('#evng')
					.html(
							"Good Evening <i class='fa fa-moon-o' aria-hidden='true' style='font-size: 15px;color: #fff;'></i>");
		}

		var dd = today.getDate();
		var mm = today.getMonth() + 1; //January is 0!
		var yyyy = today.getFullYear();

		if (dd < 10) {
			dd = '0' + dd
		}

		if (mm < 10) {
			mm = '0' + mm
		}
		document.getElementById('year').innerHTML = dd + '-' + mm + '-' + yyyy;

	}

	document.onkeydown = function(event) {

		event = (event || window.event);
		if (event.keyCode == 123) {

			return false;
		}
		if (event.keyCode == 116) {
			alert('F5 is Disabled');
			return false;
		}
	};

	window.history.forward();
	function noBack() {
		window.history.forward();

	}

	
	function userDetails(user_id){
		debugger;
		try{
			$('body').append('<div id="ProfileDiv"></div>');
			$('#ProfileDiv').dialog({
				autoOpen: false,
				modal: true,
				width : '650',

				title : 'Profile Details',
				height : 'auto',
				resizable : false,
				scrollable : false,
				draggable : false,
				position : {my : "center top", at :"center top+50" ,of : window},
				open :function(event, ui){
					$(this).load("UserLog.do?user_id="+user_id);
				},
				close : function(){
					$('#ProfileDiv').remove();
				},
				buttons: {
					"Close" : function(){
						$('#ProfileDiv').remove();
					}
				}
			});
			$('#ProfileDiv').dialog("open");
		}catch (e) {
			showMessage("Error", e.message);
		}
		
	}

/* 	document.addEventListener(
					'DOMContentLoaded',
					function() {
						const profileLink = document
								.getElementById('profile-logo');
						const dropdownMenu = document
								.getElementById('dropdown-menu');

						profileLink
								.addEventListener(
										'click',
										function(event) {

											event.preventDefault();
											dropdownMenu.style.display = dropdownMenu.style.display === 'block' ? 'none'
													: 'block';
										});
						document.addEventListener('click', function(event) {
							if (!event.target.matches('.profile-link')
									&& !event.target
											.matches('.dropdown-content')) {
								dropdownMenu.style.display = 'none';
							}

						});

					}); */
</script>



<style>


.profile-container {
	position: relative;
	display: inline-block;
}

.dropdownmenu {
	display: none; /* Hidden by default */
	position: absolute;
	right: 0;
	background-color: white;
	border: 1px solid #ccc;
	border-radius: 5px;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
	min-width: 150px;
	z-index: 1;
	padding: 10px 0;
	list-style: none;
	margin: 10px 0 0;
}

.dropdown-menu  a {
	text-decoration: none;
	color: #333;
	display: block;
}

.dropdown-menu a:hover {
	background-color: #f1f1f1;
}

.card {
	background-color: #fff;
	background-clip: border-box;
	border-radius: 7px;
	box-shadow: 0 0 10px rgba(96, 134, 181, .3);
	margin-top: 0px;
	text-align: right;
}

.card_body {
	text-align: right;
	font-size: 0;
	padding: 1.25rem;
}

.card_title {
	font-size: 2rem;
	margin: 0 0 .75rem;
}

.countdown_section {
	text-align: right;
	display: inline-block;
	margin-left: 4px;
}

.countdown_section:first-child {
	margin-left: 0;
}

.countdown_section .value {
	margin-top: 3px;
	background-color: black;
	border-radius: 20px;
	font-size: 14px;
	color: #c2eaba;
	line-height: 20px;
	text-align: center;
	height: 20px;
	width: 100px;
}

.countdown_label {
	font-size: 9px;
	color: white;
	font-weight: bold;
	padding-top: 7px;
	text-align: center;
}

.imgg {
	margin-top: 3px;
	border-radius: 20px;
}

.dropdown{
position: relative;
display: inline-block;
}

.profile-link{
text-decoration: none;
color: #333;
cursor: pointer;
}
.dropdown-content{
display: none;
position: absolute;
background-color: #f9f9f9;
min-width: 160px;
box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);

z-index: 1;
}
.dropdown-content{
color: black;
padding: 12px 16px;
text-decoration:none;
}
.dropdown-content a:hover{
background-color:#f1f1f1;
}
</style>

<header class="main-header">




	<nav class="navbar navbar-static-top" role="navigation"
		style="height: 30px;">

		<!-- Logo -->

		<a href="#" class="logo"><img class="imgg" src="dist/img/pnbl_ogo.png"
			height="58px " width="274px"></a>
		<div class="card_body">
			<span class="hidden-xs"><i class="fa fa-calendar"
				aria-hidden="true"></i> <span id="today"></span> &nbsp;&nbsp;&nbsp;
				<i class="fa fa-clock-o" aria-hidden="true"></i> <span id="txt"></span></span>

			<div class="countdown_section">
				<div id="year" class="value">00</div>
				<div class="countdown_label">Date</div>
			</div>

			<div class="countdown_section">
				<div id="hours" class="value">00</div>
				<div class="countdown_label">Time</div>
			</div>

			<div class="countdown_section" style="">
				<div id="evng" class="value" style="width: 130px;">00</div>
				<div id="aftrn" class="value" style="width: 150px;">00</div>
				<div id="mrng" class="value" style="width: 150px;">00</div>



				<div class="countdown_label"
					style="font-weight: bold; font-size: 9px; color: black;">
					<a
									onclick="userDetails('<%=((LoginBean) request.getSession().getAttribute("loginBean")).getUser_id()%>')" title="View Profile Details"
									style="font-weight:bold ; color:black;font-size: 10px; cursor: pointer;"> <%=((LoginBean) request.getSession().getAttribute("loginBean")).getUser_name()%> </a></div>
			</div>
<!-- 			<div class="dropdown" style="">

				<a href="#" id="profile-logo" class="profile-link"> <img
					src="dist/images/maleprofile.png" class="user-image"
					alt="User Image" style="margin-top: -30px; height: 30px;" />
				</a>
				<div id="dropdown-menu" class="dropdown-content"></div>

				<a href="#">A</a> <a href="#">A</a> <a href="#">A</a>
			</div> -->
		</div>




	</nav>
</header>

