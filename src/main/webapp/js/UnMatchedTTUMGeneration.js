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

function ValidateData() {
	var fileDate = document.getElementById("datepicker").value;

	var ttumType = document.getElementById("typeOfTTUM").value;
	var stSubCategory = document.getElementById("stSubCategory").value;
	debugger;


	if (stSubCategory == "-") {
$("#alert_msg").empty();
		$("#alert_msg").append("Please Select Sub Category");

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

function ValidateCombination() {
	var category = document.getElementById("category").value;

	var ttumType = document.getElementById("typeOfTTUM").value;
	var localDate = document.getElementById("localDate").value;
	var fileDate = document.getElementById("datepicker").value;
	debugger;
	//if(fileName == "CBS" && ttumType == "LP")  "CBSWNET"

	// validation for NFS
	if (category == "NFS") {
		alert("For NFS please select Switch in FileName");
		return false;
	}

	if (ttumType != "REVERSAL" && localDate == "") {
		alert("Please select Tran Data ");
		return false;
	}






	return true;

}

function Process() {
	debugger;
	var frm = $('#reportform');
	var category = document.getElementById("category").value;
	var stSubCategory = document.getElementById("stSubCategory").value;
	var fileDate = document.getElementById("datepicker").value;

	var ttumType = document.getElementById("typeOfTTUM").value;
	var localDate = document.getElementById("localDate").value;

	if (category == 'RUPAY') {
		if (ValidateData()) {

			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory', stSubCategory);
			oMyForm.append('fileDate', fileDate);

			oMyForm.append('typeOfTTUM', ttumType);
			oMyForm.append('localDate', localDate);
			$.ajax({
				type: "POST",
				url: "GenerateUnmatchedTTUM.do",
				data: oMyForm,


				processData: false,
				contentType: false,
				beforeSend: function() {
					showLoader();
				},
				complete: function(data) {
					document.getElementById("DownloadUnmatchedTTUM").disabled = "";
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

					}, 2500);

				},

				error: function(err) {
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

				}
			});

		}
	} else if (category = 'VISA') {
		if (ValidateData()) {

			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory', stSubCategory);
			oMyForm.append('fileDate', fileDate);

			oMyForm.append('typeOfTTUM', ttumType);
			oMyForm.append('localDate', localDate);
			$.ajax({
				type: "POST",
				url: "GenerateUnmatchedTTUMVISA.do",
				data: oMyForm,

				processData: false,
				contentType: false,
				beforeSend: function() {
					showLoader();
				},
				complete: function(data) {
					document.getElementById("DownloadUnmatchedTTUMVISA").disabled = "";
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

					}, 2500);
				},

				error: function(err) {
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
				}
			});

		}
	}


}

function Download() {
	debugger;
	var frm = $('#reportform');
	var category = document.getElementById("category").value;
	var stSubCategory = document.getElementById("stSubCategory").value;
	var fileDate = document.getElementById("datepicker").value;
	var TTUMCategory = document.getElementById("TTUMCategory").value;
	var ttumType = document.getElementById("typeOfTTUM").value;
	var localDate = document.getElementById("localDate").value;

	if (category == 'RUPAY') {
		if (ValidateData()) {

			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory', stSubCategory);
			oMyForm.append('fileDate', fileDate);
			oMyForm.append('TTUMCategory', TTUMCategory);

			oMyForm.append('typeOfTTUM', ttumType);
			oMyForm.append('localDate', localDate);
			$.ajax({
				type: "POST",
				url: "checkTTUMProcessed.do",
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
						$("#success_msg").append("Reports are getting downloaded. Please Wait");

						//	document.getElementById("breadcrumb").style.display = 'none';

						$("#success_msg").modal('show');

						setTimeout(function() {

							$("#success_msg").modal('hide');

							$("#success_msg").empty();
							//document.getElementById("breadcrumb").style.display = '';

						}, 2500);
						document.getElementById("processform").submit();
					}
					else {
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
	} else if (category == 'VISA') {
		if (ValidateData()) {

			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory', stSubCategory);
			oMyForm.append('fileDate', fileDate);

			oMyForm.append('typeOfTTUM', ttumType);
			oMyForm.append('localDate', localDate);
			$.ajax({
				type: "POST",
				url: "checkTTUMProcessedVISA.do",
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
						$("#success_msg").append("Reports are getting downloaded. Please Wait");

						//	document.getElementById("breadcrumb").style.display = 'none';

						$("#success_msg").modal('show');

						setTimeout(function() {

							$("#success_msg").modal('hide');

							$("#success_msg").empty();
							//document.getElementById("breadcrumb").style.display = '';

						}, 2500);
						document.getElementById("processform").submit();
					}
					else {
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
}

function getFields(e) {
	debugger;
	//GET filename
	var typeOfTTUM = document.getElementById("typeOfTTUM").value;


	if (typeOfTTUM == "0") {
		alert("Please select TTUM Type");
	}
	else {
		if (typeOfTTUM != "REVERSAL") {
			document.getElementById("ttumDate").style.display = '';
			document.getElementById("date").style.display = 'none';
		}
		else {
			document.getElementById("ttumDate").style.display = 'none';
			document.getElementById("date").style.display = '';
		}


	}
	
	

}

	function CallDollar() {
		debugger;
		var stSubCategory = document.getElementById("stSubCategory").value;

		if (stSubCategory == "ACQUIRER ATM") {
			document.getElementById("typeOfTTUMACQ").style.display = '';
		document.getElementById("typeOfTTUM").style.display = 'none';
		} else {
					document.getElementById("typeOfTTUM").style.display = '';
			document.getElementById("typeOfTTUMACQ").style.display = 'none';
		}
	}