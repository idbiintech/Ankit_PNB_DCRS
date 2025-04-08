function seerule(e) {
	
	document.getElementById("fileValue").value=e;
	
	window.open("../DebitCard_Recon/SeeRule.do" , 'SeeRule', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
}


function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}

function ValidateData()
{
	var localDate = document.getElementById("localDate").value;
	var ttumType = document.getElementById("typeOfTTUM").value;
	var  stSubCategory =document.getElementById("stSubCategory").value;
	
	if(stSubCategory=="ACQUIRER"){
		var ttumType = document.getElementById("acqtypeOfTTUM").value;
		
	}
	debugger;
	
	if(stSubCategory == "-")
	{		$("#alert_msg").empty();

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
	
	if(localDate == "" )
	{		$("#alert_msg").empty();

		$("#alert_msg").append("Please select Date!!");

		//	document.getElementById("breadcrumb").style.display = 'none';

			$("#alert_msg").modal('show');

			setTimeout(function() {

				$("#alert_msg").modal('hide');
				//document.getElementById("breadcrumb").style.display = '';
				$("#alert_msg").empty();
			}, 2500);
		return false;
	}
	
	if(ttumType == "0")
	{
		$("#alert_msg").empty();
		$("#alert_msg").append("Please select TTUM Type!!");

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
		var ttumType = document.getElementById("typeOfTTUM").value;
		var localDate = document.getElementById("localDate").value;
		var  stSubCategory =document.getElementById("stSubCategory").value;
		
		if(stSubCategory == 'ACQUIRER')
		{	
			ttumType = document.getElementById("acqtypeOfTTUM").value;
		}
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('typeOfTTUM',ttumType);
			oMyForm.append('localDate',localDate);
			$.ajax({
				type : "POST",
				url : "GenerateUnmatchedTTUM.do",
				data :oMyForm ,

				processData : false,
				contentType : false,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					document.getElementById("upload").disabled="";
					hideLoader();

				},
				success : function(response) {
					debugger;
				hideLoader();
			
							$("#success_msg").append(response);

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

		

				},
				
				error : function(err) {
					hideLoader();
			
			$("#error_msg").append("Unable To Process!!");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#error_msg").modal('show');

	
				
				}
			});
			
		}
	
	
}

function Download() {
	debugger;
	var frm = $('#reportform');
		var category = document.getElementById("category").value;
		var localDate = document.getElementById("localDate").value;
		var  stSubCategory =document.getElementById("stSubCategory").value;	
		var ttumType = document.getElementById("typeOfTTUM").value;
		var TTUMCategory = document.getElementById("TTUMCategory").value;
				
		if(stSubCategory == 'ACQUIRER')
		{	
			ttumType = document.getElementById("acqtypeOfTTUM").value;
		}
				
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('localDate',localDate);
			oMyForm.append('typeOfTTUM',ttumType);
			oMyForm.append('TTUMCategory', TTUMCategory);
			$.ajax({
				type : "POST",
				url : "checkTTUMProcessed.do",
				data :oMyForm ,

				processData : false,
				contentType : false,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
					document.getElementById("upload").disabled="";
					hideLoader();

				},
				success : function(response) {
							$("#success_msg").empty();
					if(response == "success")
					{
						
							$("#success_msg").append("Reports are getting downloaded. Please Wait");

					//	document.getElementById("breadcrumb").style.display = 'none';

					$("#success_msg").modal('show');

					setTimeout(function() {

						$("#success_msg").modal('hide');

						$("#success_msg").empty();
						//document.getElementById("breadcrumb").style.display = '';

					}, 2500);
						document.getElementById("reportform").submit();
					}
					else
					{
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
				error : function(err) {
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
				complete : function(data) {

					hideLoader();

				},
			});
			
		}
}
	
function getFields(e)
{
	var  stSubCategory =document.getElementById("stSubCategory").value;		
	
	if(stSubCategory == 'ISSUER')
	{
		document.getElementById("issuerOpt").style.display = '';
		document.getElementById("acquirerOpt").style.display = 'none';
	}
	else
	{
		document.getElementById("issuerOpt").style.display = 'none';
		document.getElementById("acquirerOpt").style.display = '';
	}

}
