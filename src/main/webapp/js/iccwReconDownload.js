$(document).ready(function(){

$('#downloadiccwBtn').click(function(){
$('#downloadForm')[0].action="iccwDownload.do";
$('#downloadForm').submit();
});

$('#iccwSummary').click(function(){
debugger
$('#downloadForm')[0].action = "#";
$('#downloadForm').submit();
});

$('#iccwReport').click(function(){
debugger
$('#downloadForm')[0].action = "#";
$('#downloadForm').submit();
});
})