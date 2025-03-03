function ReportRollback() {
	debugger;
	var datepicker = document.getElementById("localDate").value;

	//var form = document.getElementById("microAtmReport");
	//var oMyForm = new FormData();
	//oMyForm.append('datepicker', datepicker);
	if (Validation()) {
		$.ajax({
			type: 'POST',
			url: 'rollbackTTUMVISANB.do',
			async: true,
			beforeSend: function() {
				showLoader();
			},
			complete: function(data) {
				hideLoader();
			},
			data: {
				filedate: datepicker

			},
			success: function(response) {

				$("#success_msg").append(response);

				//	document.getElementById("breadcrumb").style.display = 'none';

				$("#success_msg").modal('show');

			},
			error: function() {

				$("#error_msg").append("Unable To RollBack!!");

				//document.getElementById("breadcrumb").style.display = 'none';

				$("#error_msg").modal('show');

			},
		});
	}
}
function Validation() {
	var datepicker = document.getElementById("localDate").value;
	if (datepicker == "--SELECT--") {
			$("#alert_msg").empty();
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
function CallDollar() {
	debugger;
	alert("sas");
}

function ValidateData() {
	debugger;

	var ttumType = document.getElementById("TTUMCategory").value;

	var datepicker = document.getElementById("localDate").value;
	if (datepicker == "--SELECT--") {
			$("#alert_msg").empty();
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

	if (ttumType == "-") {
			$("#alert_msg").empty();
		$("#alert_msg").append("Please Select TTUM Type");

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

function Download() {
	debugger;


	var localDate = document.getElementById("localDate").value;
	var TTUMCategory = document.getElementById("TTUMCategory").value;

	if (ValidateData()) {

		var oMyForm = new FormData();

		oMyForm.append('localDate', localDate);
		oMyForm.append('TTUMCategory', TTUMCategory);
	
		$.ajax({
			type: "POST",
			url: "checkTTUMProcessedATMPOSCROSSROUNTING.do",
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
								$("#success_msg").empty();
					$("#success_msg").append(
						"Reports are getting downloaded. Please Wait");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');

						$("#success_msg").empty();
						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);
					document.getElementById("processform").submit();
				} else {
								$("#success_msg").empty();
					$("#success_msg").append(response);

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');

						$("#success_msg").empty();
						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);
				}

			},
			error: function(err) {
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
			complete: function(data) {

				hideLoader();

			},
		});

	}
}

function showLoader(location) {

	$("#Loader").show();
}

function hideLoader(location) {

	$("#Loader").hide();
}