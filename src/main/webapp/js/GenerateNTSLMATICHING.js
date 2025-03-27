
function Process() {
	debugger;


	var cycle = document.getElementById("cycle").value;
	var ModuleType = document.getElementById("ModuleType").value;
	var localDate = document.getElementById("localDate").value;


	if (Validation()) {

		$.ajax({
			type: "POST",
			url: "ntslRawMatching.do",
			data: {
				cycle: cycle,
				localDate: localDate,
				ModuleType: ModuleType
			},
			processData: false,
			contentType: false,
			beforeSend: function() {
				showLoader();
			},
			complete: function(data) {

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

function Process() {
		debugger;
		showLoader();
			var cycle = document.getElementById("cycle").value;
	var ModuleType = document.getElementById("ModuleType").value;
	var localDate = document.getElementById("localDate").value;

		$.ajax({
			url : 'ntslRawMatching.do',
			type : 'POST',
			/* processData : false,
			contentType : false, */
			data : {
				cycle : cycle,
				localDate : localDate,
				ModuleType: ModuleType
			},
			success : function(response) {
				debugger;
				hideLoader();
				var count = 0;
				console.log(response);
				
		
		
				$.each(response,
				function(i, item) {
					$('<tr>').html(
						"<td>" + response[count].diff_AMOUNT
								+ "</td><td>"
								+ response[count].diff_COUNT
								+ "</td><td>"
								+ response[count].ntsl_FILENAME
								+ "</td><td>"
								+ response[count].ntsl_TXN_AMOUNT
								+ "</td><td>"
								+ response[count].ntsl_TXN_COUNT
								+ "</td><td>"
								+ response[count].raw_FILENAME
							
								+ "</td><td>"
								+ response[count].raw_TXN_AMOUNT
								+ "</td><td>"
								+ response[count].raw_TXN_COUNT
								+ "</td>").appendTo(
						"#atmListBody");
					count++;
				});
				$("#atmTableDiv").show();
				$("#atmTable").DataTable({
					retrieve : true,
					"scrollCollapse" : false,
					"info" : true,
					"paging" : true
				}).columns.adjust().draw();
				$("#filter").show();
			},
			error : function(error) {
				alert("Error: FAILED TO FETCH DETAILS");
			}
		});
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

function showLoader(location) {

	$("#Loader").show();
}

function hideLoader(location) {

	$("#Loader").hide();
}