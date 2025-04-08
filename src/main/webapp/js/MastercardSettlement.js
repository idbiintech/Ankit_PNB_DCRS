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
	{			$("#alert_msg").empty();
				$("#alert_msg").append("Please select File Date");

			//	document.getElementById("breadcrumb").style.display = 'none';

				$("#alert_msg").modal('show');

				setTimeout(function() {

					$("#alert_msg").modal('hide');
					//document.getElementById("breadcrumb").style.display = '';
					$("#alert_msg").empty();
				}, 2500);
		return false;
	}
	if(fileType == '0')
	{			$("#alert_msg").empty();

		$("#alert_msg").append("Please select File Type");

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
			url : "MastercardSettlementProcess.do",
			data :oMyForm ,

			processData : false,
			contentType : false,
			beforeSend : function() {
				showLoader();
			},
			success : function(response) {
				debugger;
				hideLoader();
			$("#success_msg").empty();
				
						$("#success_msg").append(response);

					

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');

						$("#success_msg").empty();
						

					}, 2500);
				
			},

			error : function(err) {
				hideLoader();
							$("#error_msg").empty();
				$("#error_msg").append("Unable To Process!!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
						$("#error_msg").empty();

						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);
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
	var ttumCetegory = document.getElementById("ttumCetegory").value;

	if(ValidateData())  {
		var oMyForm = new FormData();
		oMyForm.append('fileType', filename);
		oMyForm.append('fileDate',fileDate);
		oMyForm.append('ttumCetegory',ttumCetegory);
		$.ajax({
			type : "POST",
			url : "ValidateMastercardSettlement.do",
			data :oMyForm ,

			processData : false,
			contentType : false,
			beforeSend : function() {
				showLoader();
			},
			success : function(response) {
				if(response == "success")
				{
				$("#success_msg").empty();
					
						$("#success_msg").append("Reports are getting downloaded. Please Wait");

					

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');

						$("#success_msg").empty();
						

					}, 2500);
					document.getElementById("reportform").submit();
				}
				else
				{
		
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
				$("#error_msg").append("Unable To Download!!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
						$("#error_msg").empty();

						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);
			},
			complete : function(data) {

				hideLoader();

			},
		});
	}


}

