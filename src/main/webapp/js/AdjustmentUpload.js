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
	var fileDate = document.getElementById("datepicker").value;
	
	var fileSelected = document.getElementById("fileName").value;
	//var  cycle =document.getElementById("cycle").value;
	var dataFile= document.getElementById("dataFile1").value;
	var leng= dataFile.length - 4;
	var fileExten = dataFile.substring(leng,dataFile.length);
	debugger;
	/*if(category == "")
	{
		alert("Please select category ");
		return false;
	}*/
	if(fileSelected == "0")
	{	$("#alert_msg").empty();

		$("#alert_msg").append("Please select file!!");

		//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
		return false;
	}
	if(fileDate == "")
	{
			$("#alert_msg").empty();
		$("#alert_msg").append("Please select date for processing");

		//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
		return false;
	}

	if(dataFile==""){
	$("#alert_msg").empty();
	$("#alert_msg").append("Upload Excel file format");

		//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
		return false;
	}
	if(fileExten != '.xls' && fileExten != 'xlsx')
	{
		$("#alert_msg").empty();
		$("#alert_msg").append("Upload Excel file format");

		//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
		return false;
	}
	return true;

}

function processAdjFileUpload() {
	debugger;
	var frm = $('#uploadform');
	
	
		var filename = document.getElementById("fileName").value;
		var category = document.getElementById("rectyp").value;
		var userfile = document.getElementById("dataFile1");
		var fileDate = document.getElementById("datepicker").value;
		var oMyForm = new FormData();
		oMyForm.append('file',userfile.files[0])
		oMyForm.append('filename', filename);
		oMyForm.append('category', category);
		oMyForm.append('datepicker',fileDate);
		if(ValidateData())  {
			$.ajax({
				type : "POST",
				url : "AdjustmentFileUpload.do",
				enctype:"multipart/form-data",
				data :oMyForm ,

				processData : false,
				contentType : false,
				//type : 'POST',
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
			
					hideLoader();

				},
				success : function(response) {
					debugger;
					hideLoader();
					if(response =="File Uploaded Successfully"){
						$("#success_msg").empty();
						$("#success_msg").append(response);

					

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');

						$("#success_msg").empty();
						

					}, 2500);	
					}else{
						
							$("#error_msg").empty();
						$("#error_msg").append(response);

					

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');

						$("#error_msg").empty();
						

					}, 2500);
						
					}

					

				},
				
				error : function(err) {
					hideLoader();
						$("#error_msg").empty();
					$("#error_msg").append("Unable To Upload!!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
						$("#error_msg").empty();

						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);
				}
			});

		}
	
	
}

function getCycle(e)
{
	//GET filename
	var fileName = document.getElementById("fileName").value;
	
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
	document.getElementById("timePeriod").value = "0";
	//document.getElementById("timePeriod").value = "0";
	document.getElementById("cycles").style.display = 'none';
	document.getElementById("Date").style.display = 'none';
	document.getElementById("Month").style.display = 'none';
	document.getElementById("subCate").style.display = 'none';
}
