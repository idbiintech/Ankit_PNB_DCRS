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


function processSettlement() {


	debugger;
	var frm = $('#processform');
	var subcategory = document.getElementById("subcategory").value;
	var fileDate = document.getElementById("datepicker").value;
	var cycle = document.getElementById("cycle").value;

	if (ValidateData()) {
		var oMyForm = new FormData();
		oMyForm.append('subcategory', subcategory);
		oMyForm.append('fileDate', fileDate);
		oMyForm.append('cycle', cycle);
		$.ajax({
			type: "POST",
			url: "RupaySettlementProcess.do",
			data: oMyForm,

			processData: false,
			contentType: false,
			beforeSend: function() {
				showLoader();
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

function DownloadSettlement() {
	debugger;
	var frm = $('#processform');
	var subcategory = document.getElementById("subcategory").value;
	var fileDate = document.getElementById("datepicker").value;
	var cycle = document.getElementById("cycle").value;

	if (ValidateData()) {
		var oMyForm = new FormData();
		oMyForm.append('subcategory', subcategory);
		oMyForm.append('fileDate', fileDate);
		oMyForm.append('cycle', cycle);
		$.ajax({
			type: "POST",
			url: "ValidateRupaySettlement.do",
			data: oMyForm,

			processData: false,
			contentType: false,
			beforeSend: function() {
				showLoader();
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
function ValidateData()
{
	var subcategory = document.getElementById("subcategory").value;
	var fileDate = document.getElementById("datepicker").value;
	var cycle = document.getElementById("cycle").value;
	debugger;
	
	if(fileDate == "")
	{		$("#alert_msg").empty();
		$("#alert_msg").append("Please select date!!");

		//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
		return false;
	}
	if(subcategory=="-"){

		$("#alert_msg").empty();
		$("#alert_msg").append("Please select Subcategory!!");

		//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
		return false;
	}
	if(cycle == "0")
	{
				$("#alert_msg").empty();
		$("#alert_msg").append("Please select Cycle!!");

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


