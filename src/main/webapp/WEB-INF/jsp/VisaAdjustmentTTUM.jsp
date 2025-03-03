<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 <meta http-equiv="X-UA-Compatible" content="IE=10"> 
 <link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->
<%
response.setHeader("Cache-Control","no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
response.setHeader("Pragma","no-cache");
response.setHeader("X-Frame-Options","deny");
%>

<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/VisaAdjustmentTTUM.js"></script>





<script type="text/javascript">
$(document).ready(function() {
	
	//alert("click");
  
    $("#datepicker").datepicker({dateFormat:"dd/mm/yy", maxDate:0});
    });



</script>
<style>
button{
background: linear-gradient(45deg, pink, blue);
border: none;
color: white;
padding: 10px 26px;
font-size: 16px;
border-radius: 20px;

cursor: pointer;
text-transform: uppercase;
transaction: background 0.3s ease;
}
button:hover{
background:linear-gradient(30deg, #c2eaba, blue);
}
label{
color: purple;  font-weight: bold;font-size: 16px;display:block;
}
</style>

<div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          	<h1 style="color: purple; text-align: center; font-weight: bold;">
           Visa ADJUSTMENT TTUM
            <!-- <small>Version 2.0</small> -->
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"> Home</a></li>
            <li class="active">Visa Adjustment TTUM</li>
          </ol>
        </section>

        <!-- Main content -->
        <section class="content">
          <div class="row">
            <!-- left column -->
            <div class="col-md-4"></div>
            <div class="col-md-4">
              <!-- general form elements -->
              <div class="box box-primary">
                <!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> --><!-- /.box-header -->
                <!-- form start -->
                <form:form id="uploadform"  action="DownloadVisaAdjustmentTTUM.do" method="POST" commandName="compareSetupBean" enctype="multipart/form-data" >
                  <div class="box-body">
                    <div class="form-group">
                      <label for="exampleInputEmail1">TTUM Type</label>
                      <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
                      <input type="hidden" name="CSRFToken" id="CSRFToken" value ="${CSRFToken }"> 
                      <form:select class="form-control" path="filename" id="filename" >
					<form:option value="0" >---Select---</form:option>
						<%-- <c:forEach var="configfilelist" items="${configBeanlist}">
							<form:option id="${configfilelist.stFileName}" value="${configfilelist.stFileName}" >${configfilelist.stFileName}</form:option>
							
							</c:forEach> --%>
							<option>Prearb Acceptance</option>
							<option>Net Original Credit</option>
							<option>Refund Reversal</option>
							<%-- <form:option id="ctf" value="CTF" >CTF</form:option> --%>
						</form:select> <!-- <img alt="" src="images/listbtn.png" title="Last Uploaded File" onclick="getfiledetails();" style="vertical-align:middle; height: 20px; width: 20px;"> --> 
						<%-- <input type="hidden" id="headerlist" value="">
						
						<form:hidden path="stFileName" id="stFileName"/> --%>
                      
                      
					  <!-- <select class="form-control" id="fileuploadTTUM" style="" placeholder="Select Your Favorite" data-search="true">
						<option value="Dispute">Dispute</option>
						<option value="opt2">option 2</option>
						<option value="opt3">option 3</option>
					  </select> -->
                    </div>
                    
       
                    <div class="form-group">
                      <label for="exampleInputPassword1">Date</label>
                      <form:input path="fileDate" class="form-control" readonly="readonly" id="datepicker"  placeholder="dd/mm/yyyy"/>
                    </div>
                    
                    
                  </div><!-- /.box-body -->

                  <div class="box-footer" style="text-align: center">
                    <!-- <button type="submit"  class="btn btn-primary">Download</button> -->
                    <input type="submit" value = "Download">
                  </div>
                  <div class="box-footer" style="display: none">
                    <input type="text" id="dummy" value="012">
                  </div>
                </form:form>
              </div><!-- /.box -->

              

            </div><!--/.col (left) -->
           
          </div>   <!-- /.row -->
        </section>
      </div><!-- /.content-wrapper -->
<div align="center" id="Loader"
	style="background-color: #ffffff; position: absolute; opacity: 0.9; z-index: 0; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

<img style="margin-left: 100px; margin-top: -60px;"
		src="images/g4.gif" alt="loader">


</div>
