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

<link href="css/jquery-ui.min.css" media="all" rel="stylesheet"
	type="text/css" />

<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<!--  <script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" />
<link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" />   -->

<!-- <script type="text/javascript" src="js/searchData.js"></script>
 -->

<style>
button {
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

button:hover {
	background: linear-gradient(to right, purple 50%, purple 10%);
}
</style>
<script type="text/javascript">  $(document).ready(function() {

	//alert("click");

	$("#datepicker").datepicker({
		dateFormat : "dd-mm-y",
		maxDate : 0
	});
	
	$("#close").click(function(){
		location.reload();
	});
});

function getsearchdata() {
	debugger;
var category = document.getElementById("category").value;
var stSubCategory = document.getElementById("stSubCategory").value  ;
var rrn_no = document.getElementById("rrn_no").value  ;

var filedate= document.getElementById("datepicker").value;

if(category!=="" && stSubCategory!=="" && rrn_no!=="" && filedate!=="") {
	$.ajax({
			 
			 type:'POST',
			 url :'searchData.do',
			 data:{category:category,stSubCategory:stSubCategory,rrn_no:rrn_no,filedate:filedate},
			 beforeSend : function() {
					showLoader();
				},
				complete : function(data) {

					hideLoader();

				},success:function(response){
				 
				console.log(response);
				
				var res= JSON.parse(response);
				var tbl = document.getElementById("searchdata");
				var headerlength = res.excelheaders.length;
				var datalngth = res.data.length;
				var rowcount = datalngth/headerlength;
				var datacount=0;
				
				var count = $('#searchdata tr').length;
				for (var i=0;i<count;i++) {
					
					tbl.removeChild(tbl.lastChild);
				}
				
				
				/*var tbody =document.createElement('tbody');
				t*/
				
				var tr = document.createElement('tr');
				
				var td= document.createElement('th');
				 tr.appendChild(td);
				 
				for(var i=0;i<res.excelheaders.length; i++) {
					
					
					var td= document.createElement('th');
					var text = document.createTextNode(res.excelheaders[i]);
					td.appendChild(text);
					 tr.appendChild(td);
					
				}
				tbl.appendChild(tr);
				var tr1 =null; ;
				for(var record=0;record<rowcount;record++) {
				for(var i=0;i<res.excelheaders.length; i++) {
				
					if(i==0) {
					 tr1 = document.createElement('tr');
					 var td= document.createElement('td');
					 tr1.appendChild(td);
					 
					}
					var td= document.createElement('td');
					
					var text = document.createTextNode(res.data[datacount]);
					td.appendChild(text);
					 tr1.appendChild(td);
					 datacount=datacount+1;
					
				}
					
				    tbl.appendChild(tr1);
				}
				   
				    
				    
				    //td.appendChild(text2);
				    tbl.style.display="";
				    
				   
				  
				    
				// response = $.parseJSON(response);
				
	
			 },error: function(){
					$("#error_msg").empty();
				 alert("Error Occured");
				 $("#error_msg").append("Unable To Fetch Reconds!!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
						//document.getElementById("breadcrumb").style.display = '';
						$("#error_msg").empty();
					}, 2500);
				 
			 },
			 
		 });

}else {
	$("#alert_msg").empty();

	$("#alert_msg").append("Please Fill The Important Feild To Get Result!!");

	//	document.getElementById("breadcrumb").style.display = 'none';

	$("#alert_msg").modal('show');

	setTimeout(function() {

		$("#alert_msg").modal('hide');
		//document.getElementById("breadcrumb").style.display = '';
		$("#alert_msg").empty();
	}, 2500);
	
	
}

}



function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}



</script>



<style>
button {
	background: linear-gradient(45deg, pink, blue);
	border: none;
	color: white;
	padding: 12px 26px;
	font-size: 16px;
	border-radius: 20px;
	cursor: pointer;
	text-transform: uppercase;
	transaction: background 0.3s ease;
}

button:hover {
	background: linear-gradient(30deg, #c2eaba, blue);
}

label {
	color: purple;
	font-weight: bold;
	font-size: 16px;
	text-transform: uppercase;
	display: block;
}
.col-md-3{
margin-left:200px;}
.box-footer{
margin-left:450px;
}
</style>

<!-- Main content -->


<!-- Right side column. Contains the navbar and content of the page -->
<div class="content-wrapper">

	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1 style="margin-left:-400px;">
			RRN SEARCH
			<!-- <small>Version 2.0</small> -->
		</h1>
		<!-- <ol class="breadcrumb">
            <li><a href="#"> Home</a></li>
            <li class="active">RRN SEARCH</li>
          </ol> -->
	</section>

	<!-- Main content -->
	<section class="content">
		<!-- <div class="row"> -->
		<!-- left column -->
	
		<!-- general form elements -->
		<div class="box box-primary" style="width:1200px; " >
			<!-- <div class="box-header">
                  <h3 class="box-title">Cashnet</h3>
                </div> -->
			<!-- /.box-header -->
			<!-- form start -->
			<form role="form">
				<div class="box-body">
					<div class="row">

						<div class="col-md-3">
							<div class="form-group">
								<label for="exampleInputEmail1">TYPE</label>
								<!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
								<select class="form-control" id="category" style=""
									placeholder="Select Your Favorite" data-search="true"
							>
									<option value="">SELECT</option>
									<option value="NFS_ISSUER">NFS ISSUER</option>
									<option value="NFS_ACQUIRER">NFS ACQUIRER</option>
									<option value=RUPAY_DOM>RUPAY DOM</option>
									<option value="QSPARC_DOM">QSPARC DOM</option>
									<option value="ICD_ISS">ICD ISSUER</option>
									<option value="ICD_ACQ">ICD ACQUIRER</option>
									<option value="DFS_ACQ">DFS ACQUIRER</option>
									<option value="JCB_ACQ">JCB ACQUIRER</option>
									<option value="MASTERCARD_ISS">MASTERCARD ISSUIRER</option>
									<option value="MASTERCARD_ACQ">MASTERCARD ACQUIRER</option>
									<option value="VISA_ACQ">VISA ACQUIRER</option>
									<option value="VISA_ISS">VISA ISSUER</option>
							
									
								</select>
							</div>
						</div>
						<div class="col-md-3" id="trsubcat">
							<div class="form-group">
								<label for="exampleInputEmail1">SUB-Category</label>
								<!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
								<select class="form-control" id="stSubCategory" style=""
									placeholder="Select Your Favorite" data-search="true">
									<option value="Dispute">SELECT</option>
									<option value="opt2">ISSUER</option>
									<option value="opt3">ACQUIRER</option>
										<option value="opt2">DOMESTIC</option>
									<option value="opt3">INTERNATIONAL</option>
								</select>
							</div>
						</div>

					</div>
					<div class="row">
						<div class="col-md-3">
							<div class="form-group">
								<label for="exampleInputPassword1">RRN No.</label> <input
									type="text" class="form-control" id="rrn_no"
									placeholder="">
							</div>
						</div>
						<div class="col-md-3">
					


							<div class="form-group">
								<label>DATE</label>
								<input  class="form-control"
									readonly="readonly" id="datepicker" value="SELECT DATE" />
							</div>
						</div>
						<!-- 					<div class="col-md-3">
					<div class="form-group">
                      <label for="exampleInputPassword1">Amount</label>
                      <input type="text" class="form-control" id="exampleInputPassword1" placeholder="">
                    </div>
					</div> -->
					</div>
					<!-- <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" id="exampleInputFile">
                      <p class="help-block">Example block-level help text here.</p>
                    </div> -->
					<!-- <div class="checkbox">
                      <label>
                        <input type="checkbox"> Check me out
                      </label>
                    </div> -->
				</div>
				<!-- /.box-body -->

				<div class="box-footer">
					<button type="button" onclick="getsearchdata()">Search</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<button type="button" id="close" >Cancel</button>
				</div>
			</form>

		</div>
		
		
		<!-- /.box -->

<%-- 		<div class="box box-primary">
			<div class="box-body">
				<div class="box-header">
					<h3 class="box-title">Search Result</h3>
				</div>
				<!-- /.box-header -->
				<!-- form start -->
				<form role="form">
					<table id="searchdata" style="display: none;"
						class="table table-bordered searchtable" rule='box'>
						<tbody class="table-responsive"   >
                	
                	
                	
                	</tbody>

					</table>

				</form>
			</div>
		</div>
 --%>


		<!-- </div> -->
		<!--/.col (left) -->

		<!-- </div> -->
		<!-- /.row -->
	</section>
	<!-- /.content-wrapper -->


	<div
		style="font-size: 14px; font-weight: bold; border-radius: 3px; color: white; text-align: center; background: yellowgreen; margin-top: -320px;  margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
		id="success_msg" class="success_msg">
		<i class="fa fa-check" style="color: white;"></i>
		<button type="button" class="close" data-dismiss="modal"
			style="margin-top: -20px; margin-right: 20px;">&times;</button>


	</div>


	<div
		style="font-size: 14px; font-weight: bold; color: white; border-radius: 3px; text-align: center; background: red; margin-top: -320px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
		id="error_msg" class="error_msg">
		<i class="fa fa-close" style="color: white;"></i>
		<button type="button" class="close" data-dismiss="modal"
			style="margin-top: -20px; margin-right: 20px;">&times;</button>
	</div>
	<div
		style="font-size: 14px; font-weight: bold; color: black; border-radius: 3px; text-align: center; background: #FFFF9E;  margin-top: -320px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
		id="alert_msg" class="alert_msg">
		<i class="fa fa-warning" style="color: black;"></i>
	</div>
</div>
<!-- /.content-wrapper -->

<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

	<img style="margin-left: 100px; margin-top: -60px;" src="images/g4.gif"
		alt="loader">


</div>
<script>
	function CallDollar() {
		debugger;
		alert("sas");
	}
</script>