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
	var fileType = document.getElementById("fileType").value;
	
	if(fileDate == "")
	{
		alert("Please Select file Date ");
		return false;
	}
	if(fileType == '0')
	{
		alert("Please select file Type");
		return false;
	}
	
	return true;

}

function processSettlement() {
	debugger;
	var frm = $('#reportform');
	var filename = document.getElementById("fileType").value;
	var fileDate = document.getElementById("datepicker").value;

	if(ValidateData())  {
		var oMyForm = new FormData();
		oMyForm.append('fileType', filename);
		oMyForm.append('fileDate',fileDate);
		$.ajax({
			type : "POST",
			url : "MastercardSettlementTTUMProcess.do",
			data :oMyForm ,

			processData : false,
			contentType : false,
			beforeSend : function() {
				showLoader();
			},
			success : function(response) {
				debugger;
				hideLoader();

				alert (response); 
				document.getElementById("fileType").value="0";
				document.getElementById("datepicker").value="";
			},

			error : function(err) {
				hideLoader();
				alert("Error Occurred");
			},
			complete : function(data) {

				hideLoader();

			},
		});
	}


}

function DownloadSettlement() {
	debugger;
	var frm = $('#reportform');
	var filename = document.getElementById("fileType").value;
	var fileDate = document.getElementById("datepicker").value;

	if(ValidateData())  {
		var oMyForm = new FormData();
		oMyForm.append('fileType', filename);
		oMyForm.append('fileDate',fileDate);
		$.ajax({
			type : "POST",
			url : "ValidateMastercardSettlementTTUM.do",
			data :oMyForm ,

			processData : false,
			contentType : false,
			beforeSend : function() {
				showLoader();
			},
			success : function(response) {
				if(response == "success")
				{
					alert("Reports are getting downloaded. Please Wait");
					document.getElementById("reportform").submit();
				}
				else
				{
					alert (response); 
					document.getElementById("fileType").value="0";
					document.getElementById("datepicker").value="";
				}
			},

			error : function(err) {
				hideLoader();
				alert("Error Occurred");
			},
			complete : function(data) {

				hideLoader();

			},
		});
	}


}

