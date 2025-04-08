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
	var subcategory = document.getElementById("stSubCategory").value;
	var fileDate = document.getElementById("datepicker").value;
//	var fileType = document.getElementById("fileType").value;
	var fileSelected = document.getElementById("fileName").value;
	var userfile = document.getElementById("dataFile1");
	var dataFile= document.getElementById("dataFile1").value;
	var leng= dataFile.length - 3;
	var fileExten = dataFile.substring(leng,dataFile.length);
	var cycle = document.getElementById("cycle").value;
	debugger;
	
	if(fileSelected == "0")
	{	$("#alert_msg").empty();
	
					$("#alert_msg").append("Please select file ");

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
	{	$("#alert_msg").empty();
	
					$("#alert_msg").append("Please Select file Date ");

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

					$("#alert_msg").append("Please Upload File.");

			//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
		return false;
	}
	if(fileExten != 'xlsx' && fileExten != 'xls')
	{	$("#alert_msg").empty();
	
					$("#alert_msg").append("Upload Text file format");

			//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
		return false;
	}
	
	if(subcategory == "-")
	{
	$("#alert_msg").empty();
					$("#alert_msg").append("Please select subcategory");

			//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
		return false;
	}
	if(subcategory == "INTERNATIONAL" && fileSelected == "SETTLEMENT")
	{	$("#alert_msg").empty();

					$("#alert_msg").append("Please don't upload Settement File in International");

			//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);

	}
	
	if(cycle == ''){
	$("#alert_msg").empty();
					$("#alert_msg").append("PLEASE SELECT CYCLE");

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

function processFileUpload() {
	debugger;
	var frm = $('#uploadform');
		var filename = document.getElementById("fileName").value;
		var userfile = document.getElementById("dataFile1");
		var cycle = document.getElementById("cycle").value;
		var fileDate = document.getElementById("datepicker").value;
		var subcategory = document.getElementById("stSubCategory").value;
		
		//alert(userfile.files.length);
		var files = [];
		for(var i= 0 ; i< userfile.files.length ; i++)
		{
			files[i] = userfile.files[i];
		}
		
		if(ValidateData())  {
			
			for(var i= 0 ; i< userfile.files.length ; i++)
			{
			
			var oMyForm = new FormData();
			oMyForm.append('file',files[i])
			oMyForm.append('fileName', filename);
			oMyForm.append('subcategory',subcategory);
			oMyForm.append('fileDate',fileDate);
			oMyForm.append('cycle',cycle);
			
		//	oMyForm.append('fileType',fileType);
			$.ajax({
				type : "POST",
				url : "RupayFileUpload.do",
				enctype:"multipart/form-data",
				data :oMyForm ,

				processData : false,
				contentType : false,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					document.getElementById("upload").disabled="";
					hideLoader();

				},
				success : function(response) {
					debugger;
					hideLoader();
					$("#success_msg").empty();
										$("#success_msg").append(response);

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');
						//document.getElementById("breadcrumb").style.display = '';
						$("#success_msg").empty();
					}, 2500);

				},
				
				error: function(err) {
					hideLoader();
				$("#error_msg").empty();
				$("#error_msg").append("Error Occured");

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
	
	
}


function FileNameChange(e)
{
	if(e.value == "TAD")
	{
		//document.getElementById("type").style.display = '';
		document.getElementById("date").style.display = 'none';
	}
	else
	{
		//document.getElementById("type").style.display = 'none';
		document.getElementById("date").style.display = '';
	}
	
}

