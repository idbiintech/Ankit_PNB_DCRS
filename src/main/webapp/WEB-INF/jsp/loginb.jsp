<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<%
response.setHeader("Strict-Transport-Security", "max-age=63072000; includeSubDomains; preload");
response.setHeader("Content-Security-Policy", "none");
//response.setHeader("Content-Security-Policy", "default-src 'none'; script-src 'self' 'unsafe-inline' ; connect-src 'self' 'unsafe-inline'; img-src 'self' ; style-src 'self' 'unsafe-inline' ;base-uri 'self' ;form-action 'self' ;");
response.setHeader("Referrer-Policy", "same-origin");
response.setHeader("X-Content-Type-Options", "nosniff");
response.setHeader("X-XSS-Protection", "1; mode=block");
//response.setHeader("Content-Type", "application/font-woff2"); // this line download loginprocess.do

response.setHeader("Cache-Control", "no-store");
response.setHeader("X-Frame-Options", "DENY");
response.setHeader("Pragma", "no-cache");
//response.setHeader("X-XSS-Protection", "0");

response.setHeader("Server", "Apache");
response.setHeader("X-FRAME-OPTIONS", "DENY");
response.setHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
response.setHeader("Access-Control-Allow-Methods", "POST");
response.setHeader("Access-Control-Max-Age", "1728000");
response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
response.setHeader("Expires", "0");
%> 
<%-- <%
response.setHeader("Strict-Transport-Security", "max-age=63072000; includeSubDomains; preload");
response.setHeader("Content-Security-Policy", "none");
//response.setHeader("Content-Security-Policy", "default-src 'none'; script-src 'self' 'unsafe-inline' ; connect-src 'self' ; img-src 'self' ; style-src 'self' ;base-uri 'self' ;form-action 'self';");
response.setHeader("Referrer-Policy", "same-origin");
response.setHeader("X-Content-Type-Options", "nosniff");
response.setHeader("X-XSS-Protection", "1; mode=block");
//response.setHeader("Content-Type", "application/font-woff2"); // this line download loginprocess.do

response.setHeader("Cache-Control", "no-store");
response.setHeader("X-Frame-Options", "DENY");
response.setHeader("Pragma", "no-cache");
//response.setHeader("X-XSS-Protection", "0");

response.setHeader("Server", "Apache");
response.setHeader("X-FRAME-OPTIONS", "DENY");
response.setHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
response.setHeader("Access-Control-Allow-Methods", "POST");
response.setHeader("Access-Control-Max-Age", "1728000");
response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
response.setHeader("Expires", "0");
%> --%>
<meta charset="UTF-8">
<title>Login</title>
<meta
	content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no'
	name='viewport'>
<meta http-equiv="X-UA-Compatible" content="IE=10,edge">

<link href="font-awesome-4.7.0/css/font-awesome.min.css"
	rel="stylesheet" type="text/css" />

<script src="plugins/jQuery/jQuery-2.1.3.min.js"></script>
<script src="js/jquery-ui.js" type="text/javascript"></script>
<script type="text/javascript" src="js/captcha.js"></script>
<script type="text/javascript" src="js/login.js"></script>
<script type="text/javascript" src="js/wrapping/pbkdf2.js"></script>
<script type="text/javascript" src="js/wrapping/aes.js"></script>
<script type="text/javascript" src="js/wrapping/AesUtil.js"></script>
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet"
	type="text/css" />
<!-- Font Awesome Icons -->
<link
	href="https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css"
	rel="stylesheet" type="text/css" />
<!-- Theme style -->
<link href="./dist/css/main.css" rel="stylesheet" type="text/css" />
<!-- iCheck -->
<link href="./plugins/iCheck/square/blue.css" rel="stylesheet"
	type="text/css" />

<script type="text/javascript">
	document.cookie = "username=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
</script>

<style>
form {
	height: 700px;
	width: 400px;
	background-color: rgbargba(0, 0, 0, 0);
	position: absolute;
	transform: translate(-50%, -50%);
	top: 50%;
	left: 80%;
	border-radius: 40px;
	backdrop-filter: blur(0px);
	border: 1px solid rgba(255, 255, 255, 01);
	border-color: #c2eaba;
	box-shadow: 0 0 30px rgba(8, 7, 16, 0.6);
	padding: 50px 35px;
}

form * {
	font-family: 'Poppings', sans-serif;
	color: white;
	letter-spacing: 0.5px;
	outline: none;
	border: none;
}

label {
	font-weight: bold;
	display: block;
	margin-top: 30px;
	font-size: 16px;
}

input {
	display: block;
	height: 50px;
	width: 100%;
	background-color: black;
	border-radius: 10px;
	padding: 0 10px;
	marging-top: 8px;
	font-size: 14px;
	font-weight: bold;
}

::placeholder {
	color: #e5e5e5;
}

.button {
	background-image: linear-gradient(to right, purple 0%, pink 51%, blue 100%);
	font-weight: bold;
	text-align: center;
	margin: 80px;
	margin-top: 50px;
	background-size: 200% auto;
	width: 50%;
	bottom: 10px;
	color: white;
	padding: 17px 45px;
	border-radius: 9px;
	box-shadow: 0 0 10px #eee;
	font-weight: bold;
	transition: 0.2s;
	display: block;

}

.button:hover {
	background-position: right center;
	color: #fff;
	text-decoration: none;
}

.imge {
	border-radius: 20px;
	opacity: 3;
}

.idbi {
	margin-top: 20px;
	margin-left: 20px;
}
</style>

</head>
<body class="login-page">
	<img class="idbi" src="./dist/img/logo_recon.png" height="80px"
		width="150px">
	<form:form action="Login.do" method="post" commandName="login"
		id="login">
		<img class="imge" src="./dist/img/pnb_logo.png" height="90px">
		<label for="username">USER ID</label>
		<form:input  autocomplete="off" path="user_id" id="user_id" maxlength="15"
			placeholder="User ID" />
		<label for="password">PASSWORD</label>
		<form:password path="password" id="password" maxlength="15"
			placeholder="Password" />
		<form:input path="processType" id="processType" style="display:none"
			type="password" />
		<strong
			style="font-size: 11px; color: white; margin-left: 0px; margin-top: 10px; display: flex; align-items: center;">
			<input type="checkbox" onclick="myFunction()"
			style="width: 22px; height: 18px; margin-top: 0px;">Show
			Password
		</strong>
	
		<input autocomplete="off" maxlength="10" style="margin-top:30px; display: flex;  "type="text" placeholder="Captcha" id="cpatchaTextBox"
			 />

				<a style="width: 100px; height: 18px; margin-left: 5px; margin-top:15px;display: flex;  color: teal; " href="#" onclick="createCaptcha()">Reload &nbsp; <i class="fa fa-refresh" style="margin-top:2px;"></i></a>
					<div id="captcha" class="form_div" style="width: 100px; height: 18px; margin-left: 100px; margin-top:-17px;font-size :20px; display: flex;  "></div>

		

		<!-- 				<input type="checkbox" onclick="myFunction()"
			style="width: 22px; height: 18px; margin-bottom:-14px;" >
		<span style="font-size: 12px; color:white; margin-left:30px; font-weight:bold;">Show Password</span> -->
		<!--        <a onclick="loginSelectionShow()" class="button" id="signIn">Sign In</a> -->
		<button class="button" id="signIn" onclick="loginSelectionShow()">Sign
			In</button>
		<div id="forget_pass" class="forget_pass"
			style="color: teal; cursor: pointer; font-size: 11px; font-weight: bold; text-align: center; text-decoration: none; margin-top: -60px; display: none;">
			Forget Password<a href="#" onclick="loginforgetpass()"> Click
				Here</a>
		</div>
		<div
			style="font-size: 11px; font-weight: bold; color: black; text-align: center; background: white; margin-top: -780px; padding: 20px 30px; display: none;"
			id="calladd_msg" class="calladd_msg">
			<span style="color: black;">Contact to Administrator</span>

		</div>
		<div
			style="font-size: 11px; font-weight: bold; color: black; text-align: center; background: white; margin-top: -780px; padding: 20px 30px; display: none;"
			id="calladd_msg1" class="calladd_msg1">
			<span style="color: black;"></span>

		</div>
		<c:if test="${error_msg != null }">

			<script>
				var err = "${error_msg}";
			</script>
			<div
				style="font-size: 11px; font-weight: bold; color: black; text-align: center; margin-top: -780px; background: white; padding: 20px 30px; display: none;"
				align="center" id="errorMsg" class="errorMsg">
				<c:out value="${error_msg}" escapeXml="false" />
			</div>

		</c:if>


		<c:if test="${success_msg != null}">
			<script>
				var sss = "${success_msg}";
			</script>
			<div
				style="font-size: 11px; font-weight: bold; color: black; text-align: center; background: white; margin-top: -780px; padding: 20px 30px; display: none;"
				id="success_msg" class="success_msg">
				<c:out value="${success_msg}" escapeXml="false" />
			</div>

		</c:if>


	</form:form>

	<div
		style="font-size: 11px; font-weight: bold; color: #fff; text-align: center; margin-top: 800px; margin-left: 1480px; position:fixed;">
		Designed & Developed by<a href="http://www.idbiintech.com/"> IDBI
			INTECH LTD</a>
	</div>
	<div
		style="font-size: 11px; font-weight: bold; color: #fff; text-align: center; margin-top: 820px; margin-left: 1560px;  position:fixed;">Version
		2.0</div>


	<script src="./plugins/jQuery/jQuery-2.1.3.min.js"></script>

	<script src="./bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
	<!-- iCheck -->
	<script src="./plugins/iCheck/icheck.min.js" type="text/javascript"></script>

	<script>
		function loginSelectionShow() {
			event.preventDefault();
			debugger
			if (document.getElementById("cpatchaTextBox").value == code) {

	
				if (isValidLogin()) {
					var form = document.getElementById("login");
					form.submit();

				}
			} else {
				$("#calladd_msg1").empty();
				$("#calladd_msg1").append("INVALID CAPTCHA PLEASE TRY AGAIN!!");
				$("#calladd_msg1").modal('show');
				setTimeout(function() {

					$("#calladd_msg1").modal('hide');
					$("#calladd_msg1").empty();
				}, 2500);
		
				createCaptcha();
			}
			
			
		}
		function loginforgetpass() {

			$("#calladd_msg").modal('show');
			setTimeout(function() {

				$("#calladd_msg").modal('hide');
			}, 2000);
		}
		function ADShow() {

			window.location.reload();
		}
		function submitform(processType) {

			document.getElementById("processType").value = processType;
			var form = document.getElementById("login");

			form.submit();

		}

		$("#loginPage").keyup(function(event) {
			if (event.keyCode === 13) {
				$("#signIn").click();
			}
		});

		$(document).ready(function() {
			
			function reloadCaptcha(){
				createCaptcha();
				setTimeout(reloadCaptcha, 100000);
			}
			
			reloadCaptcha();
		
		
			$("#success_msg").modal('show');

			/* 		var success_msg = document.getElementById("success_msg").value;
			 */
			setTimeout(function() {

				$("#success_msg").modal('hide');
			},1500);

		});

		$(document).ready(function() {
			createCaptcha();

			$("#errorMsg").modal('show');

			var errormsg = document.getElementById("errorMsg").value;

			setTimeout(function() {

				$("#errorMsg").modal('hide');
				if (errormsg == "Error") {

				} else {
					$("#forget_pass").modal('show');
				}

			}, 1500);

			//}

		});

		function myFunction() {
			
			var x = document.getElementById("password");
			if (x.type === "password") {
				x.type = "text";

			} else {
				x.type = "password";
			}
		}

		var code;
		function createCaptcha() {
			//clear the contents of captcha div first 
			document.getElementById('captcha').innerHTML = "";
			var charsArray = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@!#$%^&*";
			var lengthOtp = 6;
			var captcha = [];
			for (var i = 0; i < lengthOtp; i++) {
				//below code will not allow Repetition of Characters
				var index = Math.floor(Math.random() * charsArray.length + 1); //get the next character from the array
				if (captcha.indexOf(charsArray[index]) == -1)
					captcha.push(charsArray[index]);
				else
					i--;
			}
			var canv = document.createElement("canvas");
			canv.id = "captcha";
			canv.width = 100;
			canv.textBaseline="bottom";
			canv.height = 25;
			var ctx = canv.getContext("2d");
			ctx.font = "25px Georgia";
			ctx.strokeStyle = "white";
			ctx.strokeText(captcha.join(""), 0, 18);
			//storing captcha so that can validate you can save it somewhere else according to your specific requirements
			code = captcha.join("");
			document.getElementById("captcha").appendChild(canv); 
	
			// adds the canvas to the body element
		}
		function validateCaptcha() {
			event.preventDefault();
			debugger
			if (document.getElementById("cpatchaTextBox").value == code) {
	
			} else {
				alert("Invalid Captcha. try Again");
				createCaptcha();
			}
		}
	</script>
</body>

</html>