function seerule(e) {

	document.getElementById("fileValue").value = e;

	window.open("../DebitCard_Recon/SeeRule.do", 'SeeRule', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
}


function showLoader(location) {

	$("#Loader").show();
}

function hideLoader(location) {

	$("#Loader").hide();
}


function getSubCategory(e) {
	debugger;


	var fileType = document.getElementById("fileName").value
	console.log('filetype ', fileType);

	document.getElementById("ccSubCategory").style.display = "none";
	document.getElementById("epSubCategory").style.display = "none";
	if (fileType == "EP") {
		document.getElementById("epSubCategory").style.display = "";
	} else if (fileType == "CC") {
		document.getElementById("ccSubCategory").style.display = "";
	}
}

function ValidateData() {
	var fileDate = document.getElementById("datepicker").value;
	var dataFile = document.getElementById("dataFile1").value;
	var leng = dataFile.length - 3;
	var fileExten = dataFile.substring(leng, dataFile.length);
	var fileName = document.getElementById("fileName").value;
	var fileType = document.getElementById("fileType").value;

	debugger;

	if (fileDate == "") {
					$("#alert_msg").empty();
		$("#alert_msg").append("Please Select Date!!");

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');

			$("#alert_msg").empty();
		}, 2500);
		return false;
	}
	if (dataFile == "") {
			$("#alert_msg").empty();
		$("#alert_msg").append("Please Upload File!!");

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');

			$("#alert_msg").empty();
		}, 2500);
		return false;
	}
	if (fileExten != 'TXT' && fileExten != 'txt') {
			$("#alert_msg").empty();
		$("#alert_msg").append("Please Select Text format!!");

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');

			$("#alert_msg").empty();
		}, 2500);
		return false;
	}
	if (fileType == "") {
			$("#alert_msg").empty();
		$("#alert_msg").append("Please Select File Type!!");

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');

			$("#alert_msg").empty();
		}, 2500);
		return false;
	}
	
	if (fileName == "0") {
			$("#alert_msg").empty();
		$("#alert_msg").append("Please select file name from drop down");

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');

			$("#alert_msg").empty();
		}, 2500);
		return false;
	}

	return true;

}

function FileUpload() {
	debugger;
	var frm = $('#uploadform');
	var userfile = document.getElementById("dataFile1");
	var fileDate = document.getElementById("datepicker").value;
	var fileName = document.getElementById("fileName").value;
	var fileType = document.getElementById("fileType").value;

	if (ValidateData()) {

		var oMyForm = new FormData();
		oMyForm.append('file', userfile.files[0])
		oMyForm.append('fileDate', fileDate);
		oMyForm.append('fileName', fileName);
		oMyForm.append('fileType', fileType);
		$.ajax({
			type: "POST",
			url: "VisaEPUpload.do",
			enctype: "multipart/form-data",
			data: oMyForm,

			processData: false,
			contentType: false,
			//type : 'POST',
			beforeSend: function() {
				showLoader();
			},
			complete: function(data) {
				document.getElementById("upload").disabled = "";
				hideLoader();

			},
			success: function(response) {
				debugger;
				hideLoader();
			$("#success_msg").empty();
				$("#success_msg").append(response);

				//	document.getElementById("breadcrumb").style.display = 'none';

				$("#success_msg").modal('show');

				setTimeout(function() {

					$("#success_msg").modal('hide');

					$("#success_msg").empty();
					//document.getElementById("breadcrumb").style.display = '';

				}, 10000);
			},

			error: function(err) {
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

