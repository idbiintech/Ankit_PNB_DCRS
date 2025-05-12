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

		debugger;

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

		return true;

	}

	function Process() {
		debugger;
		var frm = $('#reportform');
		var OpeningBalance = document.getElementById("OpeningBalance").value;
		var ClosingBalance = document.getElementById("ClosingBalance").value;
		var TTUMCategory = document.getElementById("TTUMCategory").value;
		var localDate = document.getElementById("localDate").value;

		if (ValidateData()) {

			var oMyForm = new FormData();
			oMyForm.append('OpeningBalance', OpeningBalance);
			oMyForm.append('ClosingBalance', ClosingBalance);
			oMyForm.append('TTUMCategory', TTUMCategory);
			oMyForm.append('localDate', localDate);
			$.ajax({
				type : "POST",
				url : "GenerateEODExcel.do",
				data : oMyForm,

				processData : false,
				contentType : false,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					document.getElementById("upload").disabled = "";
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
				},

				error : function(err) {
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
		var OpeningBalance = document.getElementById("OpeningBalance").value;
		var ClosingBalance = document.getElementById("ClosingBalance").value;
		var TTUMCategory = document.getElementById("TTUMCategory").value;
		var localDate = document.getElementById("localDate").value;

	

		if (ValidateData()) {

			var oMyForm = new FormData();
			oMyForm.append('OpeningBalance', OpeningBalance);
			oMyForm.append('ClosingBalance', ClosingBalance);
			oMyForm.append('TTUMCategory', TTUMCategory);
			oMyForm.append('localDate', localDate);
	
			$.ajax({
				type : "POST",
				url : "checkTTUMProcessed.do",
				data : oMyForm,

				processData : false,
				contentType : false,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					document.getElementById("upload").disabled = "";
					hideLoader();

				},
				success : function(response) {
					if (response == "success") {

						$("#success_msg").append(
								"Reports are getting downloaded. Please Wait");

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
				error : function(err) {
					hideLoader();
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

	

	$(document).ready(function() {
		debugger;
		//$('#dollar_field').hide();
		$("#localDate").datepicker({
			dateFormat : "yy/mm/dd",
			maxDate : 0
		});

	});

	window.history.forward();
	function noBack() {
		window.history.forward();
	}