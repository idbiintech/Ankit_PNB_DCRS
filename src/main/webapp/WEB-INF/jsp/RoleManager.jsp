<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript" src="js/admin.usermanager.js"></script>
<style>
.button {
	background:white;

	
	padding: 7px 20px;
	border:1px;
	font-size: 14px;
	border-radius: 20px;
	border-style:solid;
	border-color:  #c2eaba;

	cursor: pointer;
	transaction: background 0.3s ease;
}

.button:hover {
	background:linear-gradient(45deg, yellow, blue);
}

.leftSolid {
	color: white;
	text-align: center;
	font-weight: bold;
	font-size: 16px;
   background: linear-gradient(10deg, #c2eaba, blue);
	border-radius: 8px;
}

.leftDotted {
	color:  black;
	text-align: center;

	font-size: 16px;

background-color:white;
	border-radius: 8px;
}
</style>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
	<h1 style="color: purple; text-align: center; font-weight: bold;">
		Role Manager
		<!-- <small>Version 2.0</small> -->
	</h1>
	<!-- <ol class="breadcrumb">
		<li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
		<li class="active">Role Manager</li>
	</ol> -->
	</section>

	<section class="content"> <!-- <div class="jtable-main-container">
	<div class="jtable-title">
		<div class="jtable-title-text">USER MANAGER</div>
		<div class="jtable-toolbar">
			<span id="add_user" class="jtable-toolbar-item jtable-toolbar-item-add-record" style="">
				<span class="jtable-toolbar-item-icon"></span>
				<span class="jtable-toolbar-item-text"><b>Add User</b></span>
			</span>
		</div>
	</div>
</div> --> <section class="content">
	<div class="row">
		<div class="col-md-7"></div>
		<!-- left column -->
		<div class="col-md-11">
			<!-- general form elements -->
			<div class="box box-prima" style=" margin-left:50px;">
				<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
				<!-- /.box-header -->
				<!-- form start -->
				<%-- <form role="form"> --%>
				<div class="box-body" >
<!-- 					<a class="button" id="add_user"
						style="float: right; margin-bottom: 10px;"><i
						aria-hidden="true"></i> Add User</a> -->
					<table align="center" cellpadding="2" cellspacing="0" border="0"
						width="100%" class="table table-bordered scrollTable">
						<tr class="footerBtns">
							<th class="leftSolid">User Id</th>
							<th class="leftSolid">User Name</th>
						<!-- 	 <th class="leftSolid">User Type</th>  -->
							<th class="leftSolid">User Status</th>
							<th class="leftSolid">User Role</th>
							<th class="leftSolid" colspan="2">Assign Role To User</th>
						</tr>
						<c:forEach var="user" items="${userList}" varStatus="loop">
							<c:set var="rowClass" value="oddRow" />
							<c:if test="${loop.count %2 eq 0}">
								<c:set var="rowClass" value="evenRow" />
							</c:if>
							<tr class="${rowClass}">
								<td align="center" style="   background: white;"><a
									onclick="userLog('${user.user_id}')" title="View User Log"
									style="border-bottom: 1px dotted blue; cursor: pointer;"
									class="button"><c:out value="${user.user_id}" /></a></td>
								<td align="center" class="leftDotted"><c:out
										value="${user.user_name}" /></td>
								<%-- <td align="center" class="leftDotted"><c:out value="${user.user_type}" /></td> --%>
								<td align="center" class="leftDotted" style="color: black;">

									<c:choose>
										<c:when test="${user.user_status == 'A' }">Active</c:when>
										<c:when test="${user.user_status == 'I' }" >Inactive</c:when>
										<c:otherwise>-</c:otherwise>
									</c:choose>
												<td align="center" class="leftDotted"><c:out
										value="${user.user_type}" /></td>
								<td align="center" class="leftDotted"><c:out
										value="${user.last_login}" /></td>
								<td align="center" class="leftDotted"><img
									alt="Edit Record" id="edit${loop.count}"
									onclick="editRole('${user.user_id}')" src="images/edit.png" height="20px"
									title="Edit Record" style="cursor: pointer;"></td>
								<%-- <td align="center" class="leftDotted"><img
									alt="Delete Record" id="delete${loop.count}"
									onclick="deleteUser('${user.user_id}')" src="images/delete.png"
									title="Delete Record" style="cursor: pointer;"></td> --%>
							</tr>
						</c:forEach>
						<c:if test="${empty userList}">
							<tr>
								<td colspan="7" align="center" class="oddRow">No Data
									Found..</td>
							</tr>
						</c:if>
						<tr>
							<td class="footerBtns" colspan="7" align="center">&nbsp; <input
								type="button" class="button" id="live_user" name="live_user"
								value="Assigned User Details">
								<!-- 
								<input
								type="button" class="button" id="live_user" name="live_user"
								value="Assigned Role To User"> -->
							</td>
						</tr>
					</table>
				</div>
				<!-- /.box-body -->


				<%-- </form> --%>
			</div>
			<!-- /.box -->



		</div>
		<!--/.col (left) -->

	</div>
	<!-- /.row --> </section>
</div>
