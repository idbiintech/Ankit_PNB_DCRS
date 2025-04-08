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

function ValidateData()
{
	var category = document.getElementById("rectyp").value;
	//var subcategory = document.getElementById("stSubCategory").value;
	var fileSelected = document.getElementById("fileName").value;
	/*var timePeriod = document.getElementById("timePeriod").value;
	
	if(timePeriod == "Daily")
	{*/	
		var fileDate = document.getElementById("dailypicker").value;
		var  cycle =document.getElementById("cycle").value;
	/*}
	else
	{
		var fileDate = document.getElementById("monthpicker").value;
		cycle = "0";
	}*/
	debugger;
	if(category == "")
	{
		alert("Please select category ");
		return false;
	}
	if(fileSelected == "0" || fileSelected == " ")
	{
		alert("Please select file");
		return false;
	}
	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}
	//if(timePeriod == "Daily" && cycle == "0")
	if(fileSelected == "NTSL-NFS" && cycle == "0")
	{
		alert("Please Select cycle");
		return false;
	}

	return true;
	
}

function processSettVoucher() {

		var frm = $('#reportform');
		
		
		var filename = document.getElementById("fileName").value;
		var category = document.getElementById("rectyp").value;
		/*var userfile = document.getElementById("dataFile1");
		alert("file is "+userfile);*/
		//var CSRFToken = $('[name=CSRFToken]').val();
		
		//var  stSubCategory =document.getElementById("stSubCategory").value;
		/*var fileDate = document.getElementById("datepicker").value;
		alert("file date "+fileDate);*/
			var datepicker = document.getElementById("dailypicker").value;
			if(filename == "NTSL-NFS")
			{
				var  cycle =document.getElementById("cycle").value;
			}
			else
			{
				var  cycle = "1";
			}
			
		var oMyForm = new FormData();
		
		
		//oMyForm.append('file',userfile.files[0])
		oMyForm.append('fileName', filename);
		oMyForm.append('category', category);
		oMyForm.append('stSubCategory',"-");
		oMyForm.append('datepicker',datepicker);
		oMyForm.append('cycle',cycle);
		if(ValidateData())  {
		$.ajax({

			type:'POST',
			url :'NFSSettVoucherValidation.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			
			data:{fileDate:datepicker,stSubCategory:"-",cycle:cycle,filename:filename},
			success:function(response){
				
				if(response == "success")
				{
					alert("Reports are getting downloaded. Please Wait");
					document.getElementById("reportform").submit();
				}
				else
				{
					alert(response);
				}
				

			},error: function(){
				alert("Error Occured");

			},
			complete : function(data) {

				hideLoader();

			},
		});
		
		}
			
	}

function processCoopTTUM() {

	var frm = $('#reportform');
	
	
	var filename = document.getElementById("fileName").value;
	var category = document.getElementById("rectyp").value;
		var datepicker = document.getElementById("dailypicker").value;
		if(filename == "NTSL-NFS")
		{
			var  cycle =document.getElementById("cycle").value;
		}
		else
		{
			var  cycle = "1";
		}
		
	var oMyForm = new FormData();
	
	
	//oMyForm.append('file',userfile.files[0])
	oMyForm.append('fileName', filename);
	oMyForm.append('category', category);
	oMyForm.append('stSubCategory',"-");
	oMyForm.append('datepicker',datepicker);
	oMyForm.append('cycle',cycle);
	if(ValidateData())  {
	$.ajax({

		type:'POST',
		url :'NFSCoopValidation.do',
		async: true,
		beforeSend : function() {
			showLoader();
		},
		
		data:{fileDate:datepicker,stSubCategory:"-",cycle:cycle,filename:filename},
		success:function(response){
			
			if(response == "success")
			{
				alert("Reports are getting downloaded. Please Wait");
				document.getElementById("reportform").submit();
			}
			else
			{
				alert(response);
			}
			

		},error: function(){
			alert("Error Occured");

		},
		complete : function(data) {

			hideLoader();

		},
	});
	
	}
		
}

function ValidateDiffData()
{
	var category = document.getElementById("rectyp").value;
	//var subcategory = document.getElementById("stSubCategory").value;
	var fileSelected = document.getElementById("fileName").value;
	var rectified_amt  = document.getElementById("rectAmt").value;
	var sign = document.getElementById("sign").value;
	/*var timePeriod = document.getElementById("timePeriod").value;
	
	if(timePeriod == "Daily")
	{*/	
		var fileDate = document.getElementById("dailypicker").value;
		var  cycle =document.getElementById("cycle").value;
	/*}
	else
	{
		var fileDate = document.getElementById("monthpicker").value;
		cycle = "0";
	}*/
	debugger;
	if(category == "")
	{
		alert("Please select category ");
		return false;
	}
	if(fileSelected == "0" || fileSelected == " ")
	{
		alert("Please select file");
		return false;
	}
	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}
	if(fileSelected == "NTSL-NFS" && cycle == "0")
	{
		alert("Please Select cycle");
		return false;
	}
	if(rectified_amt >= 1)
	{
		alert("Amount cannot be greater than 1");
		return false;
	}
	if(sign == "0")
	{
			alert("Please select + or - from drop down");
			return false;
	}
	

	return true;
	
}

function Rectify()
{
	var frm = $('#reportform');
	var filename = document.getElementById("fileName").value;
	var category = document.getElementById("rectyp").value;
	var rectified_amt  = document.getElementById("rectAmt").value;
	var sign = document.getElementById("sign").value;
	rectified_amt = sign+rectified_amt;
	/*var userfile = document.getElementById("dataFile1");
	alert("file is "+userfile);*/
	//var CSRFToken = $('[name=CSRFToken]').val();
	
	//var  stSubCategory =document.getElementById("stSubCategory").value;
	/*var fileDate = document.getElementById("datepicker").value;
	alert("file date "+fileDate);*/
		var datepicker = document.getElementById("dailypicker").value;
		if(filename == "NTSL-NFS")
		{
			var  cycle =document.getElementById("cycle").value;
		}
		else
		{
			var  cycle = "1";
		}
		
	var oMyForm = new FormData();
	
	
	//oMyForm.append('file',userfile.files[0])
	oMyForm.append('fileName', filename);
	oMyForm.append('category', category);
	oMyForm.append('stSubCategory',"-");
	oMyForm.append('datepicker',datepicker);
	oMyForm.append('cycle',cycle);
	oMyForm.append('rectAmt',rectified_amt);
	if(ValidateDiffData())  {
	$.ajax({

		type:'POST',
		url :'SettlementRectify.do',
		async: true,
		beforeSend : function() {
			showLoader();
		},
		
		data:{category:category,datepicker:datepicker,stSubCategory:"-",cycle:cycle,fileName:filename,rectAmt:rectified_amt},
		success:function(response){
			debugger;
			hideLoader();

			alert (response); 
			document.getElementById("fileName").value="0";
			document.getElementById("rectyp").value=category;
			document.getElementById("cycle").value="0";
			document.getElementById("datepicker").value="";
			document.getElementById("cycles").style.display = 'none';
			document.getElementById("sign").value="0";
			document.getElementById("rectAmt").value="";
		},error: function(){
			alert("Error Occured");

		},
		complete : function(data) {

			hideLoader();

		},
	});
	
	}
		

	
}


function getCycle(e)
{
	//GET filename
	var fileName = document.getElementById("fileName").value;
//	alert("Filename is "+fileName);
	if(fileName == "0")
	{
		alert("Please select File first");
	}
	else
	{
		if(fileName == "NTSL-NFS")
		{
			if(e.value == "Daily")
			{
				document.getElementById("cycles").style.display = '';
				document.getElementById("Date").style.display = '';
				document.getElementById("Month").style.display = 'none';
				document.getElementById("subCate").style.display = 'none';

			}
			else
			{
				document.getElementById("cycles").style.display = 'none';
				document.getElementById("Date").style.display = 'none';
				document.getElementById("Month").style.display = '';
				document.getElementById("subCate").style.display = '';
			}
		}
		else
		{
			if(e.value == "Daily")
			{
				document.getElementById("cycles").value = "1";
				document.getElementById("cycles").style.display = 'none';
				document.getElementById("Date").style.display = '';
				document.getElementById("Month").style.display = 'none';
				document.getElementById("subCate").style.display = 'none';

			}
			else
			{
				document.getElementById("cycles").style.display = 'none';
				document.getElementById("Date").style.display = 'none';
				document.getElementById("Month").style.display = '';
				document.getElementById("subCate").style.display = '';
			}
			
		}
	}
}
function FileNameChange(e)
{
	if(e.value == "NTSL-NFS")
	{
		document.getElementById("cycles").style.display = '';
	}
	else
	{
	//document.getElementById("timePeriod").value = "0";
	//document.getElementById("timePeriod").value = "0";
	document.getElementById("cycles").style.display = 'none';
	//document.getElementById("Date").style.display = 'none';
	//document.getElementById("Month").style.display = 'none';
	//document.getElementById("subCate").style.display = 'none';
	}
}
