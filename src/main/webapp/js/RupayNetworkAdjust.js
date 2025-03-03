function processRupayAdjustUpload() {

	var frm = $('#uploadform');

	var fileDate = document.getElementById("datepicker").value;
	var cycle = document.getElementById("cycle").value;
	var network = document.getElementById("network").value;
	var userfile = document.getElementById("dataFile1");
	var subcate = document.getElementById("subcate").value;

	var oMyForm = new FormData();
	oMyForm.append('file', userfile.files[0])
	oMyForm.append('fileDate', fileDate);
	oMyForm.append('cycle', cycle);
	oMyForm.append('network', network);
	oMyForm.append('subcate', subcate);

	if (validateData()) {
		$.ajax({
			type: "POST",
			url: "rupayAdjustmentFileUpload.do",
			enctype: "multipart/form-data",
			data: oMyForm,

			processData: false,
			contentType: false,
			// type : 'POST',
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


			},
			error: function() {
			$("#error_msg").empty();

				$("#error_msg").append("Error Occured");

				//document.getElementById("breadcrumb").style.display = 'none';

				$("#error_msg").modal('show');


			},
			complete: function(data) {

				hideLoader();

			},
		});

	}
}
function validateData() {
	debugger;
	var fileDate = document.getElementById("datepicker").value;
	var file = document.getElementById("dataFile1").value;
	var cycle = document.getElementById("cycle").value;
	var leng = file.length - 4;

	var network = document.getElementById("network").value;
	var subcate = document.getElementById("subcate").value;
	var fileExten = file.substring(leng, file.length);
	if (fileExten != '.csv') {
			$("#alert_msg").empty();
		$("#alert_msg").append("Please upload csv File");

		//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
			$("#alert_msg").empty();
		}, 2500);

		return false;
	}

	if (fileDate == "--Select --") {
			$("#alert_msg").empty();
		$("#alert_msg").append("Kindly select date");

		//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
			$("#alert_msg").empty();
		}, 2500);

		return false;
	}

	if (file == "") {
			$("#alert_msg").empty();
		$("#alert_msg").append("Kindly select file");

		//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
			$("#alert_msg").empty();
		}, 2500);

		return false;
	}
	if (cycle == "0") {
			$("#alert_msg").empty();
		$("#alert_msg").append("Please select cycle");

		//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
			$("#alert_msg").empty();
		}, 2500);

		return false;
	}

	if (network == "") {
			$("#alert_msg").empty();
		$("#alert_msg").append("Please select network");

		//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
			$("#alert_msg").empty();
		}, 2500);

		return false;
	}
	if (network == 'RUPAY' && subcate == "0") {
			$("#alert_msg").empty();
		$("#alert_msg").append("Please select Subcategory");

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
function showLoader(location) {

	$("#Loader").show();
}
function hideLoader(location) {

	$("#Loader").hide();
}

function getFields(e) {
	var network = document.getElementById("network").value;

	if (network == "RUPAY") {
		document.getElementById("subCat").style.display = '';
	} else if (network == "QSPARC") {
		document.getElementById("subCat").style.display = '';
	}
	else {
		document.getElementById("subCat").style.display = 'none';
	}

}