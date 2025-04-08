function Process() {
	//alert("HELLO1");
	debugger;
//	var cat = document.getElementById("filename").value;
	var datepicker = document.getElementById("datepicker").value;
	var CSRFToken = $('[name=CSRFToken]').val();
	var data = new FormData();

	if (ValidateData()) {

		$.ajax({

			type: 'POST',
			url: 'iccwrunProcess.do',
			//enctype: 'multipart/form-data',
			//data: data,
			async: true,
			//processData: false,
			//contentType: false,
			beforeSend: function() {
				showLoader();
			},
			complete: function(data) {

				hideLoader();

			},

			data: { category: cat, filedate: datepicker, CSRFToken: CSRFToken },
			success: function(response) {

				alert(response);



			},

			error: function() {

				alert("Error Occured");

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

function ValidateData() {
	//alert("Inside ValidateData() ");
	//var category = document.getElementById("filename").value;
	var datepicker = document.getElementById("datepicker").value;


	debugger;
	if (category == "") {
		alert("Please select category ");
		return false;
	}
	if (datepicker == "") {
		alert("Please select date for processing");
		return false;
	}

	return true;

}