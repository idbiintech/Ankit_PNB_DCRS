
function setfilename(e) {
	debugger;


	if (e.value == "SWITCH") {


		document.getElementById("trfileType").style.display = 'none';
		document.getElementById("trcategory").style.display = 'none';
		document.getElementById("trsubcat").style.display = 'none';

	} else if (e.value == "CBS") {


		document.getElementById("trfileType").style.display = 'none';
		document.getElementById("trsubcat").style.display = 'none';
		document.getElementById("trcategory").style.display = 'none';
	} else {

		document.getElementById("trfileType").style.display = '';
		document.getElementById("trcategory").style.display = '';


	}


}




function uploadFile(id, filename) {

	var fileName = document.getElementById("fileType").value;
	var rectyp = document.getElementById("rectyp").value;
	var subCat = document.getElementById("stSubCategory").value;
	var dollar_val = document.getElementById("dollar_val").value;
	var datepicker = document.getElementById("datepicker").value;
	var CSRFToken = $('[name=CSRFToken]').val();
	//alert("DONE");
	if (ValidateData()) {

		$.ajax({

			type: 'POST',
			url: 'IcccManualUploadFile.do',
			async: true,
			beforeSend: function() {
				showLoader();
			},
			complete: function(data) {

				hideLoader();

			},

			data: {fileName: fileName, category: rectyp, filedate: datepicker, subCat: subCat, dollar_val: dollar_val, CSRFToken: $('[name=CSRFToken]').val() },
			success: function(response) {



			}, error: function() {

				alert("Error Occured");

			},

		});
	}
}

function validateupload() {


	debugger;
	var datepicker = document.getElementById("datepicker").value;
	var filelist = document.getElementById("filename").value;
	var dataFile = document.getElementById("dataFile1").value;
	var msg = "";

	if (datepicker == "") {

		msg = msg + "Please Select Date for File.\n";
	} if (filelist == "0") {

		msg = msg + "Please Select File Name.\n";
	} if (dataFile == "") {

		msg = msg + "Please Upload File.\n";
	}


	if (msg != "") {

		alert(msg);
		return false;
	} else {

		document.getElementById("upload").disabled = "disabled";



		return true;
	}


}

function showLoader(location) {

	$("#Loader").show();
}

function hideLoader(location) {

	$("#Loader").hide();
}

var uploaddate;

function processFileUpload() {
	
	debugger;
	var frm = $('#uploadform');
	var oMyForm = new FormData();
	var userfile = document.getElementById("dataFile1");
	var CSRFToken = $('[name=CSRFToken]').val();
	var filename = document.getElementById("filename").value;
	var fileDate = document.getElementById("datepicker").value;
	var fileType = document.getElementById("fileType").value;



	oMyForm.append('files', userfile.files[0])
	oMyForm.append('filename', filename);
	oMyForm.append('fileDate', fileDate);
	oMyForm.append('CSRFToken', CSRFToken);
	oMyForm.append('fileType', fileType);
	//console.log("data is"+data)
	if (validateupload()) {
		$.ajax({
			type: "POST",
			url: "IcccManualUploadFile.do",
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
				alert(response);
				hideLoader();


			},

			error: function(err) {
				alert(err);
			}
		});

	}

}

$("#ingredient_file").on("change", function(e) {
	var file = $(this)[0].files[0];
	var upload = new Upload(file);
	upload.doUpload();
});



var filesArray = [];
var index = 0;

function loadData() {
	var $file = $('#dataFile1');
	/*alert("ASDFGHJKL");*/
	debugger;
	for (var inx = 0; inx < $file[0].files.length; inx++) {
		index++;
		filesArray.push($file[0].files[inx]);
		$('#fileTable').append(
			$('<tr/>').append($('<td/>').append($file[0].files[inx].name))
				.append(
					$('<td/>').append(
						'&nbsp; <a href="#" onclick="removeFile(this,'
						+ (index) + ')" >  X </a>')));
	}
	$('#dataFile1').val('');
}

function removeFile(row, indexP) {
	index = index - 1;
	$(row.parentElement.parentElement).remove();
	filesArray.splice(indexP, 1);

}

function npciuploadData() {
	debugger;
	alert("inside upload function");
	$('#loading').show();
	size = filesArray.length;
	var perhead = (100 / size);
	var upload = new Upload("IcccManualUploadFile.do");
	$('#progress').text('Please wait');
	start = perhead;
	for (var i in filesArray) {
		upload.doUpload(filesArray[i], $("meta[name='_csrf']").attr("content"))
			.done(
				function(data) {

					alert("data" + data)
					if (data.indexOf("ERROR") != -1) {
						alert(data);
						window.location.reload();
						return;
					} else {
						$('#progress').text(
							Math.round(Number(start))
							+ '% Complete');
						move(start);
						start += perhead;

						if (start >= 100 + perhead - 1) {
							alert('All File uploaded Successfully !!');
							window.location.reload();
						}
					}
				}).fail(function(jqXHR, textStatus, errorThrown) {
					alert("this is the alert" + textStatus + " : " + errorThrown);
				});

	}

}

