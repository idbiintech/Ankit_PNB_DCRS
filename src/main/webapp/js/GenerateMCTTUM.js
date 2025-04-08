$(document).ready(function() {
	debugger;

	$("#localDate").datepicker({
		dateFormat: "yy/mm/dd",
		maxDate: 0
	});

});

window.history.forward();
function noBack() {
	window.history.forward();
}
function seerule(e) {

	document.getElementById("fileValue").value = e;

	window
		.open(
			"../DebitCard_Recon/SeeRule.do",
			'SeeRule',
			'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
}

function showLoader(location) {

	$("#Loader").show();
}

function hideLoader(location) {

	$("#Loader").hide();
}

function ValidateData() {
	var localDate = document.getElementById("localDate").value;
	var ttumType = document.getElementById("typeOfTTUM").value;
	var stSubCategory = document.getElementById("stSubCategory").value;
	if (stSubCategory == "ISSUER") {
		ttumType = document.getElementById("acqtypeOfTTUM").value;
	} else {
		ttumType = document.getElementById("typeOfTTUM").value;

	}

	debugger;

	if (stSubCategory == "-") {

		$("#alert_msg").append("Please select Category");

		//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
			$("#alert_msg").empty();
		}, 2500);
		return false;
	}

	if (localDate == "") {


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

	if (ttumType == "0") {

		$("#alert_msg").append("Please select TTUM Type");

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

function Process() {
	debugger;
	var frm = $('#reportform');
	var category = document.getElementById("category").value;
	var stSubCategory = document.getElementById("stSubCategory").value;
	var ttumType = document.getElementById("typeOfTTUM").value;
	if (stSubCategory == "ISSUER") {
		ttumType = document.getElementById("acqtypeOfTTUM").value;
	} else {
		ttumType = document.getElementById("typeOfTTUM").value;

	}

	var localDate = document.getElementById("localDate").value;

	if (ValidateData()) {

		var oMyForm = new FormData();
		oMyForm.append('category', category);
		oMyForm.append('stSubCategory', stSubCategory);
		oMyForm.append('typeOfTTUM', ttumType);
		oMyForm.append('localDate', localDate);
		$.ajax({
			type: "POST",
			url: "GenerateUnmatchedTTUMMASTERCARD.do",
			data: oMyForm,

			processData: false,
			contentType: false,
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
				if (response == "Issue while processing!") {
					$("#error_msg").append("Unable To Process!!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
						$("#error_msg").empty();

						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);

				}
				$("#success_msg").append(response);



				$("#success_msg").modal('show');

				setTimeout(function() {

					$("#success_msg").modal('hide');

					$("#success_msg").empty();


				}, 2500);

			},

			error: function(err) {
				hideLoader();
				$("#error_msg").append("Unable To Process!!");

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

function Download() {
	debugger;
	var frm = $('#reportform');
	var category = document.getElementById("category").value;
	var localDate = document.getElementById("localDate").value;
	var stSubCategory = document.getElementById("stSubCategory").value;
	var ttumType = document.getElementById("typeOfTTUM").value;
		if (stSubCategory == "ISSUER") {
		ttumType = document.getElementById("acqtypeOfTTUM").value;
	} else {
		ttumType = document.getElementById("typeOfTTUM").value;

	}

	
	var TTUMCategory = document.getElementById("TTUMCategory").value;

	if (ValidateData()) {

		var oMyForm = new FormData();
		oMyForm.append('category', category);
		oMyForm.append('stSubCategory', stSubCategory);
		oMyForm.append('localDate', localDate);
		oMyForm.append('typeOfTTUM', ttumType);
		oMyForm.append('TTUMCategory', TTUMCategory);
		$.ajax({
			type: "POST",
			url: "checkTTUMProcessedMASTERCARD.do",
			data: oMyForm,

			processData: false,
			contentType: false,
			beforeSend: function() {
				showLoader();
			},
			complete: function(data) {
				document.getElementById("upload").disabled = "";
				hideLoader();

			},
			success: function(response) {
				if (response == "success") {

					$("#success_msg").append("Reports are getting downloaded. Please Wait");



					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');

						$("#success_msg").empty();


					}, 2500);
					document.getElementById("reportform").submit();
				} else {
					$("#success_msg").append(response);



					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');

						$("#success_msg").empty();


					}, 2500);
				}

			},
			error: function(err) {
				hideLoader();
				$("#error_msg").append("Unable To Downlaod!!");

				//	document.getElementById("breadcrumb").style.display = 'none';

				$("#error_msg").modal('show');

				setTimeout(function() {

					$("#error_msg").modal('hide');
					$("#error_msg").empty();

					//document.getElementById("breadcrumb").style.display = '';

				}, 2500);
			},
			complete: function(data) {

				hideLoader();

			},
		});

	}
}

function getFields(e) {
	debugger;
	var stSubCategory = document.getElementById("stSubCategory").value;

	if (stSubCategory == 'ISSUER_POS') {
		document.getElementById("issuerOpt").style.display = 'none';
		document.getElementById("acquirerOpt").style.display = '';
	} if (stSubCategory == 'ISSUER') {
		document.getElementById("issuerOpt").style.display = 'none';
		document.getElementById("acquirerOpt").style.display = '';
	} else {

		document.getElementById("issuerOpt").style.display = '';
		document.getElementById("acquirerOpt").style.display = 'none';
	}

}

function ReportRollback() {
	debugger;
	var datepicker = document.getElementById("localDate").value;
	var category = document.getElementById("category").value;
	var subCat = document.getElementById("stSubCategory").value;
	var typeOfTTUM = document.getElementById("typeOfTTUM").value;
	if (subCat == "ISSUER") {
		typeOfTTUM = document.getElementById("acqtypeOfTTUM").value;
	} else {
		typeOfTTUM = document.getElementById("typeOfTTUM").value;

	}


	//var form = document.getElementById("microAtmReport");
	//var oMyForm = new FormData();
	//oMyForm.append('datepicker', datepicker);
	if (Validation()) {
		$.ajax({
			type: 'POST',
			url: 'rollbackTTUMMASTERCARDADJ.do',
			async: true,
			beforeSend: function() {
				showLoader();
			},
			complete: function(data) {
				hideLoader();
			},
			data: {
				filedate: datepicker,

				typeOfTTUM: typeOfTTUM,
				subCat: subCat,
				category: category
			},
			success: function(response) {
				$("#success_msg").append(response);

				$("#success_msg").modal('show');

				setTimeout(function() {

					$("#success_msg").modal('hide');

					$("#success_msg").empty();

				}, 2500);
			},
			error: function() {
				$("#error_msg").append("Unable To RollBack!!");

				//	document.getElementById("breadcrumb").style.display = 'none';

				$("#error_msg").modal('show');

				setTimeout(function() {

					$("#error_msg").modal('hide');
					$("#error_msg").empty();

					//document.getElementById("breadcrumb").style.display = '';

				}, 2500);
			},
		});
	}
}
function Validation() {
	var datepicker = document.getElementById("localDate").value;
	if (datepicker == "") {

		$("#alert_msg").append("Please select date");

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