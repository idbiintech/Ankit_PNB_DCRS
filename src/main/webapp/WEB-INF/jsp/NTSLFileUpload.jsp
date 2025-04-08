<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	
	<%
response.setHeader("Cache-Control","no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
response.setHeader("Pragma","no-cache");
response.setHeader("X-Frame-Options","deny");
%>


<link href="css/jquery-ui.min.css" media="all" rel="stylesheet"	type="text/css" />

<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<!--  <script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" />
<link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" />   -->

<script type="text/javascript" src="js/NTSLUpload.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();
  
     $("#datepicker").datepicker({dateFormat:"yy/mm/dd", maxDate:0});
     $("#monthpicker").datepicker({
    	 dateFormat:"MM yy", 
    		changeMonth: true,
    	changeYear: true,
    	showButtonPanel: true,
    	/* onClose: function(){
    		var iMonth = $('#ui-datpicker-div .ui-datepicker-month :selected').val();
    		var iYear = $('#ui-datepicker-div .ui-datepicker-year :selected').val();
    		$(this).datepicker('setDate',new Date(iYear,iMonth,1));
    	} */
    	onClose: function(dateText, inst){
        	$(this).datepicker('setDate',new Date(inst.selectedYear, inst.selectedMonth,1));
        }
    	//dateFormat:"dd-M-yy", maxDate:0
    	});
   
   
/* $("#monthlyDate").datepicker({
	dateFormat:"MM yy", 
	changeMonth: true,
changeYear: true,
//showButtonPanel: true,

onClose: function(){
	var iMonth = $('#ui-datpicker-div .ui-datepicker-month :selected').val();
	var iYear = $('#ui-datepicker-div .ui-datepicker-year :selected').val();
	$(this).datepicker('setDate',new Date(iYear,iMonth,1));
}
	}); */
});

window.history.forward();
function noBack() { window.history.forward(); }


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
background:linear-gradient(30deg,  #c2eaba, blue);
}
label{
color: purple;  font-weight: bold;font-size: 16px;display:block;
}
</style>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
				<h1 style="color: purple; text-align: center; font-weight: bold;">
			NTSL FILE UPLOAD
			<!-- <small>Version 2.0</small> -->
		</h1>
<!-- 		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">NTSL File Upload</li>
		</ol> -->
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
		<div class="col-md-4"></div>
			<!-- left column -->
			<div class="col-md-4">
				<!-- general form elements -->
				<div class="box box-primary">
					<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>
<form:form id="uploadform"  action="NTSLFileUpload.do" method="POST"  commandName="nfsSettlementBean" enctype="multipart/form-data" >

					<div class="box-body" id="subcat">
					
					
		
                    
                    <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >NTSL of</label> 
							<%-- <input type="text" id="rectyp" value="${category}" style="display: none">  --%>
							<form:select class="form-control" path="fileName" id="fileName" onchange="FileNameChange(this)">
								<option value="0">--Select --</option>
						
									<option value="NTSL-NFS">NFS</option>
							</form:select>
						</div>
						
						 <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Time Period</label> 
							<input type="text" id="rectyp" value="${category}" style="display: none"> 
							<form:select class="form-control" path="timePeriod" id="timePeriod" onchange="getCycle(this)">
							<!-- <select class="form-control" name="timePeriod" id="timePeriod" onchange="getCycle(this)"> -->
								<option value="0">--Select --</option>
									<option value="Daily">Daily</option>
									<option value="Monthly">Monthly</option>	
							</form:select>
						</div>
						 <div class="form-group" id ="subCate" style="display:none">
							<label for="exampleInputEmail1">Sub Category</label> 
							<input type="text" id="rectyp" value="${category}" style="display: none"> 
							<select class="form-control" name="stSubCategory" id="stSubCategory">
								<option value="-">--Select --</option>
								<c:forEach var="subcat" items="${subcategory}">
									<option value="${subcat}">${subcat}</option>
								</c:forEach>
							</select>
						</div>
					 <!--  <div class="form-group" id="cycles" style="display:none">
							<label for="exampleInputEmail1" >Cycle</label> 
							<select class="form-control" name="cycle" id="cycle">
								<option value="0">--Select --</option>
									<option value="1">1</option>
									<option value="2">2</option>	
							</select>
						</div>  -->
                    
                   <div class="form-group" id="Month" style="display:none" >
							<label for="exampleInputPassword1">Month</label> <input
								class="form-control" name="fileDate" readonly="readonly"
								id="monthpicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
					</div>
                    
                     <div class="form-group" id="Date" style="display:none" >
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="fileDate" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
					</div>	 
						
						 <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="dataFile1" id="dataFile1" title="Upload File" multiple="multiple"/></td>
                      <!-- <p class="help-block">Example block-level help text here.</p> -->
                    </div>
                    
						<!--  <div class="box-footer">
                  		  <button type="button" value="UPLOAD" id="upload" onclick="return processFileUpload();" class="btn btn-primary">Upload</button>
                 		 </div>

						</div> -->
					
					
					   </div>
                    

							

					<div class="box-footer" style="text-align: center">
						<!-- <a onclick="Process();" class="btn btn-primary">Process</a> -->
						<button type="button" onclick="processNTSLFileUpload();" >UPLOAD</button>
						
					</div>
					<!-- <div id="processTbl"></div> -->
</form:form>
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->

		</div>
		<!-- /.row -->
<!-- /.content-wrapper -->
</section>
    <div
			style="font-size: 14px; font-weight: bold; border-radius: 3px;color: white; text-align: center; background: yellowgreen; margin-top: -470px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="success_msg" class="success_msg">
			<i class="fa fa-check" style="color: white;"></i>
				
			
		</div>
		
		
			<div
			style="font-size: 14px; font-weight: bold; color: white;border-radius: 3px; text-align: center; background: red; margin-top: -470px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="error_msg" class="error_msg">
			<i class="fa fa-close" style="color: white;"></i>
		</div>
		<div
			style="font-size: 14px; font-weight: bold; color: black;border-radius: 3px; text-align: center; background:#FFFF9E; margin-top: -380px; margin-left: 1270px; width: 350px; padding: 15px 25px; display: none;"
			id="alert_msg" class="alert_msg">
			<i class="fa fa-warning" style="color: black;"></i><span
				style="color: white; font-weight: bold; text-align: center;">     </span>
		</div>
</div>
<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

<img style="margin-left: 100px; margin-top: -60px;"
		src="images/g4.gif" alt="loader">


</div>
<script>

</script>