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
//	var subcategory = document.getElementById("stSubCategory").value;
	var fileDate = document.getElementById("datepicker").value;
//	var fileType = document.getElementById("fileType").value;
	var fileSelected = document.getElementById("fileName").value;
	var userfile = document.getElementById("dataFile1");
	var dataFile= document.getElementById("dataFile1").value;
	var leng= dataFile.length - 3;
	var fileExten = dataFile.substring(leng,dataFile.length);
	debugger;
	
	if(fileSelected == "0")
	{

		$("#alert_msg").append("Please select File");

			//	document.getElementById("breadcrumb").style.display = 'none';

				$("#alert_msg").modal('show');

				setTimeout(function() {

					$("#alert_msg").modal('hide');
					//document.getElementById("breadcrumb").style.display = '';
					$("#alert_msg").empty();
				}, 2500);
		return false;
	}
	if(fileDate == "" && fileSelected != "TAD")
	{

		$("#alert_msg").append("Please select Date");

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


		$("#alert_msg").append("Please select Upload File");

			//	document.getElementById("breadcrumb").style.display = 'none';

				$("#alert_msg").modal('show');

				setTimeout(function() {

					$("#alert_msg").modal('hide');
					//document.getElementById("breadcrumb").style.display = '';
					$("#alert_msg").empty();
				}, 2500);
		return false;
	}
	if(fileSelected == "TAD" && fileExten != 'txt')
	{

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
	if(fileExten == 'txt')
	{
	
		$("#alert_msg").append("Upload proper file format");

			//	document.getElementById("breadcrumb").style.display = 'none';

				$("#alert_msg").modal('show');

				setTimeout(function() {

					$("#alert_msg").modal('hide');
					//document.getElementById("breadcrumb").style.display = '';
					$("#alert_msg").empty();
				}, 2500);
		return false;
	}
	if(fileSelected == "TAD" && userfile.files.length >1)
	{
		
		$("#alert_msg").append("Please upload Single file");

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
//		var  stSubCategory =document.getElementById("stSubCategory").value;
		//var fileType = document.getElementById("fileType").value;
	//	alert(fileDate);
		var fileDate = document.getElementById("datepicker").value;
		
		
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
//			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('fileDate',fileDate);
		//	oMyForm.append('fileType',fileType);
			$.ajax({
				type : "POST",
				url : "MastercardFileUpload.do",
				enctype:"multipart/form-data",
				data :oMyForm ,

				processData : false,
				contentType : false,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					
					hideLoader();

				},
				success : function(response) {
					debugger;
					hideLoader();
					
						$("#success_msg").append(response);

					

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');

						$("#success_msg").empty();
						

					}, 2500);
					/*document.getElementById("fileName").value="0";
					// alert("2");
//					document.getElementById("stSubCategory").value="-";
					// alert("4");
					document.getElementById("dataFile1").value="";
					//document.getElementById("fileType").value="0";
					// alert("8");
					document.getElementById("datepicker").value="";
					document.getElementById("date").style.display = 'none';*/

				},
				
				error : function(err) {
					hideLoader();
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
