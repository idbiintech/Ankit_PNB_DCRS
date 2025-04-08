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

<link href="css/jquery-ui.min.css" media="all" rel="stylesheet"
	type="text/css" />

<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<!--  <script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" />
<link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" />   -->


<style>
button{
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
button:hover{

background: linear-gradient(to right, purple 50%, purple 10%);
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();
  
    $("#datepicker").datepicker({dateFormat:"dd-mm-y", maxDate:0});
    });
    

window.history.forward();
function noBack() { window.history.forward(); }
function Rollback() {
	debugger;
	var filedate = document.getElementById("datepicker").value;
	var subCat = document.getElementById("stSubCategory").value;
	var Cat = document.getElementById("rectyp").value;

	//var form = document.getElementById("microAtmReport");
	//var oMyForm = new FormData();
	//oMyForm.append('datepicker', datepicker);
	if (Validation()) {
		$.ajax({
			type : 'POST',
			url : 'rollbackCTC.do',
			async : true,
			beforeSend : function() {
				showLoader();
			},
			complete : function(data) {
				hideLoader();
			},
			data : {
				filedate : filedate,
				subCat : subCat,
				Cat : Cat
			
			},
			success : function(response) {
				alert(response);
			},
			error : function() {
				alert("Error Occured");
			},
		});
	}
}
function Validation() {
	var datepicker = document.getElementById("datepicker").value;
	if (datepicker == "") {
		alert("Please select date");
		return false;
	}
	return true;
}
function getupldfiledetails(){
	
	
	window.open("../DebitCard_Recon/GetUplodedFile.do" , 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');

	
	
}

function Validate(event) {
	debugger;
	//alert("jjh");
    var regex = new RegExp("^[0-9-!@#$%*?.]");
    var key = String.fromCharCode(event.charCode ? event.which : event.charCode);
    if (!regex.test(key)) {
        event.preventDefault();
        alert("Only decimal values are allowed");
        return false;
    }
}

function setFileParam() {
	
	
	var rectyp = document.getElementById("rectyp").value
	var filedate = document.getElementById("datepicker").value;
	var tblFiledtl = document.getElementById("tblFiledtl");
	
	if(rectyp !="" && filedate!= ""){
		
		document.getElementById("lbltype").innerHTML = "Selected Recon Type: <b>" + rectyp+"</b>";
		document.getElementById("lbldate").innerHTML= "Selected File Date : <b>" +filedate+"</b>";
		
		  
		
	$.ajax({
		 
		 type:'POST',
		 url :'Filedetails.do',
		 data:{category:'ONUS'},
		 success:function(response){
			 
			
			// response = $.parseJSON(response);
			 var length =response.Records.length;
			 var rec_set_id = response.Records["0"].rec_set_id;
			 for(var i =0;i<length;i++ ) {
				
				 var fileid = response.Records[i].inFileId;
				 var filename= response.Records[i].stFileName;
				 var display ="";
				 if(i>0){
				 	
					 display = 'none';
				 }
				 var $row = $('<tr id="row'+i+'" class="even" style="display: '+display+'"  />');
				 tblFiledtl.style.display="";
				$('#tblFiledtl').append($row);

				$row.append('<td align="center" class="lD"><label>'+filename+' File</label></td>');
				$row.append('<td align="center" class="lD"><input type="file"  id='+filename+'   name='+filename+'" ></td>');
				$row.append('<td align="center" class="lD"><input type="button" id= btn'+fileid+' name = btn'+fileid+' value="See Rule" onclick=" seerule('+fileid+');" ></td>');
				$row.append('<td align="center" class="lD"><input type="button" id= run'+fileid+'  name = run'+fileid+' value="Run" onclick=" run('+fileid+','+filename+');" ></td>');
			 }
			 
			 document.getElementById("rec_set_id").value= rec_set_id;

		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });
	
	 document.getElementById("filedtl").style.display="";
	 document.getElementById("tbltypdtl").style.display="";
	 document.getElementById("tbltype").style.display="none";
	 document.getElementById("next1").style.display="none";
	}else{
		
		alert("Please Select Category" );
		
	}

	
	
}

function run(id,filename) {
	
	//alert("HELLO");
	debugger;
	var rectyp = document.getElementById("rectyp").value;
	var filedate = document.getElementById("datepicker").value;
	var file = document.getElementById(filename.id).value;
	var count = document.getElementById("count").value;	
	
	var data = new FormData();
	jQuery.each(jQuery('#'+filename.id)[0].files, function(i, file) {
	    data.append('file', file[0]);
	    data.append('category', rectyp);
	    data.append('FileId', id);
	    data.append('Filename', filename.id);
	    data.append('CSRFToken', $('#CSRFToken').val());
	  
	});
	
	$.ajax({
		 
		 type:'POST',
		 url :'runProcess.do',
		 enctype: 'multipart/form-data',
		 data:data,
		 processData: false,
		 success:function(response){
			 if(rectyp=="ONUS") {
				 if(filename=='SWITCH') {
					 alert("All process completed. Please process CBS File");
					 document.getElementById("row"+count).style.display="none";
					 count = count+1;
					 document.getElementById("row"+count).style.display="";
					 
				 }else if(filename=='CBS') {
					 
					 alert("All process completed.Please proceed to Compare");
					 document.getElementById("compare").style.display="";
					 
				 }
										 
				 }
		 	}
		 });
	
	
	
	
	
	
}

function Process() {
	//alert("HELLO1");

	var ttumCetegory= document.getElementById("ttumCetegory").value;
	var subCategory= document.getElementById("subCategory").value;
	var datepicker = document.getElementById("datepicker").value;

	//alert("DONE");
	if(ValidateData())
	{
		
		$.ajax({

			type:'POST',
			url :'runProcessMC.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			complete : function(data) {

				hideLoader();

			},

			data:{ttumCetegory:ttumCetegory,subCategory:subCategory,datepicker:datepicker},
			success:function(response){

				alert(response);
				/* document.getElementById("stSubCategory").value="-";
				 document.getElementById("dollar_val").value="";
				 document.getElementById("datepicker").value="";*/
				

			},error: function(){

				alert("Error Occured");

			},

		});
	}
	
	
	
	
}


function seerule(e) {
	
	document.getElementById("fileValue").value=e;
	
	window.open("../DebitCard_Recon/SeeRule.do" , 'SeeRule', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
}


function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}
//ADDED BY INT5779 FOR GETTING SUB CATEGORIES DYNAMICALLY
function getSubCategory()
{
	debugger;
	
//	alert("HELLO");
	var category  = document.getElementById("rectyp").value;
	if(category != "MASTERCARD")
		{
		document.getElementById("dollar").style.display="none";
		}
	else{
		document.getElementById("dollar").style.display="";
	}
	//alert("category is "+category);
	if(category!="" && (category != "ONUS" && category != "AMEX" && category == "CARDTOCARD" && category != "WCC")) { 
		document.getElementById("trsubcat").style.display="";
		$.ajax({
			 
			 type:'POST',
			 url :'getSubCategorydetails.do',
			 data:{category:category},
			 success:function(response){
				 
				
				 var length =response.Subcategories.length;
				
				
				 var compareFile1 = document.getElementById("stSubCategory");
				
		 
				 var rowcount = compareFile1.childNodes.length;
					
					for(var j =1;j<=rowcount;j++ )
					{
						compareFile1.removeChild(compareFile1.lastChild);
						//compareFile2.removeChild(compareFile2.lastChild);
					}
				
				 var option1= document.createElement("option");
				 option1.value="-";
				 option1.text="--Select--";
				 var opt1= document.createElement("option");
				 opt1.value="-";
				 opt1.text="--Select--";
				 compareFile1.appendChild(option1);
				//compareFile2.appendChild(opt1)
				 
				 for(var i =0;i<length;i++ ) {
					
					 var option= document.createElement("option");
					  option.value = response.Subcategories[i];
					 option.text= response.Subcategories[i];
					 //alert("check this "+option.text);
					 if(option.text != "SURCHARGE")
					 compareFile1.appendChild(option);
				 }
				
				 displayContent();
							 

			 },error: function(){
				
				 alert("Error Occured");
				 
			 },
			 
		 });
		}else {
			//alert("INSIDE ELSE");
			document.getElementById("trsubcat").style.display="none";
			//document.getElementById("stSubCategory").value="-";
			$('#trsubcat').val('-');
			$('#stSubCategory').val('-');
			
			
		}
	
}


function ValidateData()
{

	var ttumCetegory = document.getElementById("ttumCetegory").value;
	var subCategory = document.getElementById("subCategory").value;
	var datepicker = document.getElementById("datepicker").value;
	
	debugger;
	if(ttumCetegory == "")
	{
		alert("Please select ttumCategory ");
		return false;
	}
	if((subcategory == "" || subcategory == "-"))
	{
			alert("Please select subcategory for "+subCategory);
			return false;
		
		
		
	}
	if(datepicker == "")
	{
		alert("Please select date for processing");
		return false;
	}
	
	return true;
	
}
</script>



<style>
.gbutton{
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
.gbutton:hover{

background: linear-gradient(to right, purple 50%, purple 10%);
}

label{
color: purple;  font-weight: bold;font-size: 16px;text-transform: uppercase;display:block;
}


</style>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		   		<h1 style="color: purple; text-align: center; font-weight: bold;">
			${category} RECON PROCESS
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Recon Process</li>
		</ol>
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
			<!-- left column -->
			<div class="col-md-4"></div>
			<div class="col-md-4">
				<!-- general form elements -->
				<div class="box box-prima">
					<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>

					<div class="box-body" id="subcat">
	                     <div class="form-group">
							<label for="exampleInputEmail1">Sub Category</label> <select
								class="form-control" name="subCategory" id="subCategory">
								<option value="-">- S E L E C T -</option>
								<option value="INTERNATIONAL">INTERNATIONAL</option>
								<option value="DOMESTIC">DOMESTIC</option>




							</select>
						</div>
							<div class="form-group">
							<label for="exampleInputEmail1">Settlement Category</label> <select
								class="form-control" name="ttumCetegory" id="ttumCetegory">
								<option value="-">- S E L E C T -</option>
								<option value="ATM">ATM</option>
								<option value="POS">POS</option>




							</select>
						</div>
						



						

						<div class="form-group">
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="fileDate" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
							<!-- <img alt="" src="images/listbtn.png" title="Last Uploaded File" onclick="getupldfiledetails();" style="vertical-align:middle; height: 20px; width: 20px;"> -->


						</div>
						
						</div>

					
					<!-- /.box-body -->

					<div class="box-footer" style="text-align: center">
						<a onclick="Process();" class="gbutton">Process</a>&nbsp;&nbsp;&nbsp;
						<a onclick="Rollback();"class="gbutton" >RollBack</a>
					</div>
							
						
				
					
					
					</div>
					<div id="processTbl"></div>
					<%-- </form> --%>
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->

		</div>
		<!-- /.row -->
	</section>
</div>
<!-- /.content-wrapper -->
<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

<img style="margin-left: 100px; margin-top: -60px;"
		src="images/g4.gif" alt="loader">


</div>
<script>
function CallDollar()
{
	debugger;
	alert("sas");
	}
</script>