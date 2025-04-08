function ValidateData() {
	var fileDate = document.getElementById("fdate").value;

	if (fileDate == "") {
		alert("Please select date for processing");
		return false;
	}
	return true;
}

function Process() {
	var fileDate = document.getElementById("fdate").value;

	if (ValidateData()) {

		$('#reportform').submit();

	}

}

function saveForm() {
	debugger;
	var filedate = document.getElementById("fileDate").value;
	var form = document.getElementById("saveform");
	var count = document.getElementById("listSize").value;

	/*for(var i =0 ; i<count ; i++)
	{
		var debit_amt = document.getElementById("debit_Amt"+i).value;
	}
	/*var credit_amt = document.getElementById("credit_Amt").value;
	var debit_amt = document.getElementById("debit_Amt").value;
	var balance = document.getElementById("balance").value;
	*/
	$('#saveform').submit();


}

function myFunction(index)
{
	debugger;
	
	
	var debit_amt = parseFloat(document.getElementById("debit_Amt"+index).value);
	var credit_amt = parseFloat(document.getElementById("credit_Amt"+index).value);
	
	if(index > 0)
	{
		var balance = parseFloat(document.getElementById("balance"+(index-1)).value);
	
	/*alert("debit amt "+debit_amt);
	alert("credit amt "+credit_amt);
	alert("balance "+balance);*/
	
	var bal1 = (balance + credit_amt) - debit_amt;
	
//	alert("new balance "+bal1);
	
	document.getElementById("balance"+index).value = bal1;
	}
	else
	{
		var balance = parseFloat(document.getElementById("balance"+(index)).value);
		
		if(debit_amt >0 && credit_amt == 0)
		{
			document.getElementById("balance"+index).value = debit_amt;
		}
		else if(credit_amt >0 && debit_amt ==0)
		{
			document.getElementById("balance"+index).value = credit_amt;
		}
		else
		{
			alert("Either credit or Debit amount should be 0");
		}
	}
	
	// mandatory logic
	var cr_dr_diff = debit_amt - credit_amt;
	
	document.getElementById("crdr_Diff"+index).value = cr_dr_diff;
	
}

function GLnames(e)
{
	//alert("subcat is "+e.value);
	if(e.value == "ISSUER")
	{
		document.getElementById("ACQGL").style.display = 'none';
		document.getElementById("ISSGL").style.display = '';
	}
	else
	{
		document.getElementById("ACQGL").style.display = '';
		document.getElementById("ISSGL").style.display = 'none';
	}
}


/*function saveForm()
{
	debugger;
	alert("Inside commit click");
	
	$('#saveform').submit();
}*/


$(document).ready(function() {
	//alert("asdgjagdj");
	/*$('#fdate').datepicker({
		dateFormat: "dd/M/yy"
	});*/
	
	 $("#fdate").datepicker({dateFormat:"dd-M-yy", maxDate:0});

	$('#tblStocks').dataTable({
		paging: false,
		ordering: false,
		info: false,
		searching: false, dom: 'Bfrtip',
		buttons: [
			'csv',
			'excel',
			'pdf']
	});


	$('.saveForm').click(function() {

		//alert("HELLO");


	});


	/*$('#tdate').datepicker({
		dateFormat: "dd-M-yy"
	});
*/

	$('#downloadIward').click(function() {
		alert("it will take 7 min to download this report!");
		$('#downloadIward').text('DOWNLOADING...');
		$('#downloadForm')[0].action = "inwardreport.do";
		$('#downloadForm').submit();

	});

	$('#downloadOutward').click(function() {
		$('#downloadOutward').text('DOWNLOADING...');
		$('#downloadForm')[0].action = "outwardreport.do";
		$('#downloadForm').submit();

	});
});