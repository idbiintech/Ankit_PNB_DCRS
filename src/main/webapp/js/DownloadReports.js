function getSubCategory() {
	debugger;

	//alert("HELLO");
	var category = document.getElementById("category").value;
	//	alert("category is "+category);
	if (category != "" && (category != "ONUS" && category != "AMEX" && category != "CARDTOCARD" && category != "WCC" && category != "FISDOM")) {
		document.getElementById("trsubcat").style.display = "";
		$.ajax({

			type: 'POST',
			url: 'getSubCategorydetails.do',
			data: { category: category },
			success: function(response) {


				var length = response.Subcategories.length;


				var compareFile1 = document.getElementById("SubCat");


				var rowcount = compareFile1.childNodes.length;

				for (var j = 1; j <= rowcount; j++) {
					compareFile1.removeChild(compareFile1.lastChild);
					//compareFile2.removeChild(compareFile2.lastChild);
				}

				var option1 = document.createElement("option");
				option1.value = "-";
				option1.text = "--Select--";
				var opt1 = document.createElement("option");
				opt1.value = "-";
				opt1.text = "--Select--";
				compareFile1.appendChild(option1);
				//compareFile2.appendChild(opt1)

				for (var i = 0; i < length; i++) {

					var option = document.createElement("option");
					option.value = response.Subcategories[i];
					option.text = response.Subcategories[i];
					//alert("check this "+option.text);
					if (option.text != "SURCHARGE")
						compareFile1.appendChild(option);
				}
				/* for(var i =0;i<length;i++ ) {
						
					 var option= document.createElement("option")
					  option.value = response.Records[i].inFileId;
					 option.text= response.Records[i].stFileName;
						compareFile2.appendChild(option)
				 }*/

				//document.getElementById("trbtn").style.display="none";
				// document.getElementById("stCategorynew").disabled="disabled";
				//document.getElementById("SubCat").disabled="disabled";
				displayContent();


			}, error: function() {

				alert("Error Occured");

			},

		});
	} else {
		//alert("INSIDE ELSE");
		document.getElementById("trsubcat").style.display = "none";
		//document.getElementById("stSubCategory").value="-";
		$('#trsubcat').val('-');
		$('#SubCat').val('-');
		//alert("check it "+document.getElementById("stSubCategory").value);
		/*var subcate = $("#stSubCategory").val("-");
		alert("subcate is.............. "+subcate);*/


		//	alert("document.getElementById().value "+document.getElementById("stSubCategory").value );
		//getFilesdata();

		//alert("Please Select Category.");

	}

}

function setfilename(e) {
	debugger;
	//alert(e.value);
	// || e.value=="VISA"
	var stsubCategory = document.getElementById("stsubCategory").value;
		var rectyp = document.getElementById("rectyp").value;
/*	if (stsubCategory == "ACQUIRER" && rectyp == "NFS" ) {



		document.getElementById("trsubcatid").style.display = '';

	}
	if (stsubCategory == "DOMESTIC" && rectyp == "RUPAY" ) {



		document.getElementById("trsubcatidRupay").style.display = '';

	}*/
}
function Process() {
	debugger;
	//alert("Inside process()");
	/*var subcateg = document.getElementById("SubCat").value;
	form.submit();*/

	//alert("HELLO1");
	var category = document.getElementById("category").value;
	var subCat = document.getElementById("stsubCategory").value;
	var report = document.getElementById("report").value;
	var datepicker = document.getElementById("datepicker").value;
	var path = document.getElementById("path").value;
	var form = document.getElementById("reportform");
	var CSRFToken = $('[name=CSRFToken]').val();
	//alert("DONE");
	if (ValidateData()) {

		$.ajax({

			type: 'POST',
			url: 'checkfileprocessed.do',
			async: true,
			beforeSend: function() {
				showLoader();
			},
			/*complete : function(data) {

				hideLoader();

			},*/

			data: { report: report,  category: category, filedate: datepicker, subCat: subCat, path: path, CSRFToken: $('[name=CSRFToken]').val() },
			success: function(response) {





				//alert(response);
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
					//alert("success");

					form.submit();
					showLoader();
				}
				else {

                 $("#error_msg").empty();
					$("#error_msg").append(response);

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
		$("#error_msg").empty();

						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);

				}



			}, error: function() {
                 $("#error_msg").empty();
				
				$("#error_msg").append("Error Occured");

					//document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

					setTimeout(function() {

						$("#error_msg").modal('hide');
						//document.getElementById("breadcrumb").style.display = '';
		$("#error_msg").empty();
					}, 2500);

			},
			complete: function(data) {

				hideLoader();


			},
		});




		//showLoader();
		//form.submit();
		//alert("inside if");
		/*$.ajax({

			type:'POST',
			url :'DownloadReports.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			complete : function(data) {

				hideLoader();

			},

			data:{category:category,filedate:datepicker,subCat:subCat,path:path},
			success:function(response){

				alert(response);

			},error: function(){

				alert("Error Occured");

			},

		});*/
	}



}
function ValidateData() {
	//	alert("Inside ValidateData() ");
	var category = document.getElementById("category").value;
	var subcategory = document.getElementById("stsubCategory").value;
	var datepicker = document.getElementById("datepicker").value;
	/*	//alert("subcategory "+subcategory);
		alert("done");
		alert("category "+category);
		alert("subcategory "+subcategory);
		alert("date "+datepicker);*/

	debugger;
	if (category == "") {
		$("#alert_msg").empty();
		$("#alert_msg").append("Please select category ");

		//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
	$("#alert_msg").empty();
		}, 1500);

		return false;
	}
	if ((subcategory == "" || subcategory == "-")) {
		if (category !== "ONUS" && category !== "AMEX" && category !== "CARDTOCARD" && category !== "WCC" && category !== "FISDOM") {
	$("#alert_msg").empty();
			$("#alert_msg").append("Please select subcategory for " + category);

			//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
	$("#alert_msg").empty();
			}, 1500);
			
			return false;
		}
		/*else
		{
			document.getElementById("stSubCategory").value = "-";
			alert("1. "+document.getElementById("stSubCategory").value);
			var subcate = document.getElementById("stSubCategory").value;
			alert("check subcate "+subcate);
		}*/

	}
	if (datepicker == "--SELECT--") {
			$("#alert_msg").empty();
		$("#alert_msg").append("Please select date for processing");

		//	document.getElementById("breadcrumb").style.display = 'none';

		$("#alert_msg").modal('show');

		setTimeout(function() {

			$("#alert_msg").modal('hide');
			//document.getElementById("breadcrumb").style.display = '';
	$("#alert_msg").empty();
		}, 1500);
		return false;
	}

	return true;

}
function Success(location) {

	$("#success").show();
}


function showLoader(location) {

	$("#Loader").show();
}

function hideLoader(location) {

	$("#Loader").hide();
}
