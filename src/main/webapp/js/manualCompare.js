function setfilename(e) {
	debugger;
	//alert(e.value);
	// || e.value=="VISA"
	if (e.value == "SWITCH") {

		document.getElementById("trfileType").style.display = 'none';
		document.getElementById("trcategory").style.display = 'none';
		document.getElementById("trsubcat").style.display = 'none';

	} else if (e.value == "CBS") {

		/*
		 * document.getElementById("trfiltrfileTypeeType").style.display='';
		 * document.getElementById("trsubcat").style.display='';
		 * document.getElementById("trcategory").style.display='';
		 */
		document.getElementById("trfileType").style.display = 'none';
		document.getElementById("trsubcat").style.display = 'none';
		document.getElementById("trcategory").style.display = 'none';
	} else if (e.value == "NFS") {

		document.getElementById("trfileType").style.display = '';
		document.getElementById("trcategory").style.display = '';
		document.getElementById("trsubcat").style.display = '';

	} else if (e.value == "ICD") {

		document.getElementById("trfileType").style.display = '';
		document.getElementById("trcategory").style.display = '';
		document.getElementById("trsubcat").style.display = '';

	} else if (e.value == "DFS") {

		document.getElementById("trfileType").style.display = '';
		document.getElementById("trcategory").style.display = '';
		document.getElementById("trsubcat").style.display = '';

	} else if (e.value == "JCB") {

		document.getElementById("trfileType").style.display = '';
		document.getElementById("trcategory").style.display = '';
		document.getElementById("trsubcat").style.display = '';

	} else if (e.value == "RUPAY") {

		document.getElementById("trfileType").style.display = '';
		document.getElementById("trcategory").style.display = '';
		document.getElementById("trsubcat").style.display = '';
		document.getElementById("trsubcatid").style.display = '';

	} else if (e.value == "QSPARC") {

		document.getElementById("trfileType").style.display = '';
		document.getElementById("trcategory").style.display = '';
		document.getElementById("trsubcat").style.display = '';
		document.getElementById("trsubcatid").style.display = '';

	} else if (e.value == "POS") {
		document.getElementById("trsubcat").style.display = '';
		document.getElementById("trfileType").style.display = '';
		document.getElementById("trcategory").style.display = '';

	} else if (e.value == "DOM") {
		document.getElementById("trsubcat").style.display = '';
		document.getElementById("trfileType").style.display = '';
		document.getElementById("trcategory").style.display = '';

	} else {

		document.getElementById("trfileType").style.display = '';
		document.getElementById("trcategory").style.display = '';

	}

}

function uploadFile(id, filename) {
	// alert("HELLO1");
	debugger;
	var rectyp = document.getElementById("rectyp").value;
	var subCat = document.getElementById("stSubCategory").value;
	var subCatid = document.getElementById("stSubCategoryid").value;
	var dollar_val = document.getElementById("dollar_val").value;
	var datepicker = document.getElementById("datepicker").value;
	var CSRFToken = $('[name=CSRFToken]').val();
	// alert("DONE");
	if (ValidateData()) {

		$.ajax({

			type: 'POST',
			url: 'manualUploadFile.do',
			async: true,
			beforeSend: function() {
				showLoader();
			},
			complete: function(data) {

				hideLoader();

			},

			data: {
				category: rectyp,
				filedate: datepicker,
				subCat: subCat,
				subCatid: subCatid,
				dollar_val: dollar_val,
				CSRFToken: $('[name=CSRFToken]').val()
			},
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

// datepicker fileList dataFile
function getfiledetails() {

	window
		.open(
			"../DebitCard_Recon/GetUplodedFile.do",
			'window',
			'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');

}
function validateupload() {
	debugger;
	var datepicker = document.getElementById("datepicker").value;
	var filelist = document.getElementById("filename").value;
	var dataFile = document.getElementById("dataFile1").value;
	var category = document.getElementById("category").value;



	if (filelist == "ATM") {

		var stSubCategory = document.getElementById("stSubCategory").value;
		if (stSubCategory == "ISSUER_POS" || stSubCategory == "ISSUER" || stSubCategory == "ACQUIRER") {


			$("#alert_msg2").empty();
			$("#alert_msg2").append("Please Select ISSUER_ATM in Sub Category");

			//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg2").modal('show');

			setTimeout(function() {

				$("#alert_msg2").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg2").empty();
			}, 2500);
			return false;

		}
	}
	else if (filelist == "POS") {
		if (stSubCategory == "ISSUER_ATM" || stSubCategory == "ISSUER" || stSubCategory == "ACQUIRER") {


			$("#alert_msg2").empty();
			$("#alert_msg2").append("Please Select ISSUER_POS in Sub Category");

			//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg2").modal('show');

			setTimeout(function() {

				$("#alert_msg2").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg2").empty();
			}, 2500);
			return false;

		}
	}



	var msg = "";

	if (datepicker == "") {

		msg = msg + "Please Select Date for File.\n";


	}
	if (filelist == "0") {

		msg = msg + "Please Select File Name.\n";
	}
	if (dataFile == "") {

		msg = msg + "Please Upload File.\n";
	}
	if (filelist != 'SWITCH' && filelist != 'CBS' && filelist != 'VISA') {
		if (category == "") {

			msg = msg + "Please select category.\n "
		}
	}

	if (msg != "") {


		$("#alert_msg").empty();
		$("#alert_msg").append("Please Select Details");

		//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
			$("#alert_msg").empty();
		}, 2500);


		return false;

	} else {

		document.getElementById("upload").disabled = "disabled";

		return true;
	}

}

function getSubCategory(e) {
	debugger;

	// alert("HELLO");
	// alert("category is "+category);
	var filename = document.getElementById("filename").value;
	var category = e.value;
	if (category != ""
		&& (category != "ONUS" && category != "AMEX"
			&& category != "CARDTOCARD" && category == "VISA"
			&& category == "NFS" && filename != "REV_REPORT")) {
		document.getElementById("trsubcat").style.display = "";
		$.ajax({

			type: 'POST',
			url: 'getSubCategorydetails.do',
			data: {
				category: category
			},
			success: function(response) {

				// var length =response.Subcategories.length;

				var length = response.subcategories.length;

				var compareFile1 = document.getElementById("stSubCategory");

				var rowcount = compareFile1.childNodes.length;

				for (var j = 1; j <= rowcount; j++) {
					compareFile1.removeChild(compareFile1.lastChild);
					// compareFile2.removeChild(compareFile2.lastChild);
				}

				var option1 = document.createElement("option");
				option1.value = "-";
				option1.text = "--Select--";
				var opt1 = document.createElement("option");
				opt1.value = "-";
				opt1.text = "--Select--";
				compareFile1.appendChild(option1);
				// compareFile2.appendChild(opt1)

				for (var i = 0; i < length; i++) {

					var option = document.createElement("option");
					option.value = response.subcategories[i];
					option.text = response.subcategories[i];
					// alert("check this "+option.text);
					if (option.text != "SURCHARGE")
						compareFile1.appendChild(option);
				}
				/*
				 * for(var i =0;i<length;i++ ) {
				 * 
				 * var option= document.createElement("option") option.value =
				 * response.Records[i].inFileId; option.text=
				 * response.Records[i].stFileName;
				 * compareFile2.appendChild(option) }
				 */

				// document.getElementById("trbtn").style.display="none";
				// document.getElementById("stCategorynew").disabled="disabled";
				// document.getElementById("SubCat").disabled="disabled";
				// displayContent();
			},
			error: function() {

				alert("Error Occured");

			},

		});
	} else {
		// alert("INSIDE ELSE");
		document.getElementById("trsubcat").style.display = "none";
		// document.getElementById("stSubCategory").value="-";
		$('#trsubcat').val('-');
		$('#stSubCategory').val('-');
		// alert("check it "+document.getElementById("stSubCategory").value);
		/*
		 * var subcate = $("#stSubCategory").val("-"); alert("subcate
		 * is.............. "+subcate);
		 */

		// alert("document.getElementById().value
		// "+document.getElementById("stSubCategory").value );
		// getFilesdata();
		// alert("Please Select Category.");
	}

}

var uploaddate;
function processFileUpload() {
	debugger;

	var frm = $('#uploadform');

	var filename = document.getElementById("filename").value;
	var fileType = document.getElementById("fileType").value;
	var category = document.getElementById("category").value;
	var userfile = document.getElementById("dataFile1");
	var CSRFToken = $('[name=CSRFToken]').val();

	var stSubCategory = document.getElementById("stSubCategory").value;
	var stSubCategoryid = document.getElementById("stSubCategoryid").value;
	var fileDate = document.getElementById("datepicker").value;
	var oMyForm = new FormData();


	oMyForm.append('file', userfile.files[0])
	oMyForm.append('filename', filename);
	oMyForm.append('fileType', fileType);
	oMyForm.append('category', category);
	oMyForm.append('stSubCategory', stSubCategory);
	oMyForm.append('stSubCategoryid', stSubCategoryid);
	oMyForm.append('fileDate', fileDate);
	oMyForm.append('CSRFToken', CSRFToken);

	if (validateupload()) {

			$.ajax({
				type: "POST",
				/* timeout : 500000, */
				url: "manualUploadFile.do",
				enctype: "multipart/form-data",
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

					alert(response);

				},

				error: function(err) {

					alert(err);


				}
			});

		//}
	}

}

function getNpciUploadedFiles() {
	// alert("rabi");
	$.ajax({
		url: 'getNpciFiles.do',
		type: 'POST',
		async: false,
		data: {

			"CSRFToken": $("meta[name='_csrf']").attr("content")

		},
		success: function(data) {

			// console.log("rabuuu");
			console.log(data);

			$('.datarow').remove();
			var obj = data;
			// console.log("rabuuu 1");
			console.log(data);
			displayNpciRecord(data);

		},
		error: function(xhr) {
			alert(xhr.responseText);
		}
	});

}

function displayNpciRecord(data) {
	var obj = data;
	// console.log(data) ;
	if (!$.trim(obj)) {
		// document.getElementById("emptyResponse").style.visibility =
		// "visible";
		alert("No Data Found for NPCI!!");
	} else {

		var tableBody = "";
		for (var i in data) {
			var tableRow = "";
			tableRow += "<td >" + (data[i].count) + "</td>";
			tableRow += "<td >" + (data[i].file_name) + "</td>";
			tableRow += "<td >" + (data[i].filedate) + "</td>";
			tableRow += "<td >" + (data[i].network) + "</td>";
			tableBody = tableBody + "<tr id='datarow' class='datarow'>"
				+ tableRow + "</tr>";
		}
		$('#viewNpciFiles').append(tableBody);
		$('#viewNpciFiles').show();
	}
}
