<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript" src="js/admin.usermanager.js"></script>


<style>

form{
border-radius:20px;
}
table{
height: 300px;

}
.buttn {
	background:white;

	
	padding: 12px 22px;
	border:1px;
	font-size: 14px;
	border-radius: 20px;
	border-style:solid;
	border-color:  #c2eaba;

	cursor: pointer;
	transaction: background 0.3s ease;
}

.buttn:hover {
	background:linear-gradient(45deg, yellow, blue);
}
</style>

<c:choose>
<c:when test="${not empty user}" >


<form:form action="EditRole.do" method="post" commandName="user">
<table align="center" cellpadding="2" cellspacing="0" border="0" width="100%">
	<tr class="evenRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;User Id</th>
		<th align="right" style="border-left: 0px">:&nbsp;</th>
		<td align="left"><form:input path="user_id" class="emply_cd" /></td>
	</tr>
	<tr class="oddRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;User Name</th>
		<th align="right" style="border-left: 0px">:&nbsp;</th>
		<td align="left"><form:input path="user_name" maxlength="50" class="emp_nm"/></td>
	</tr>
	<tr class="oddRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;User Type</th>
		<th align="right" style="border-left: 0px">:&nbsp;</th>
		<td align="left"><form:input path="user_type" maxlength="50" class="emp_nm"/></td>
	</tr>
	<%-- <tr class="evenRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;User Type</th>
		<th align="right" style="border-left: 0px">:&nbsp;</th>
		<td align="left"><form:input path="user_type" maxlength="25" class="emp_nm"/></td>
	</tr> --%>
	<tr class="oddRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;User Status</th>
		<th align="right" style="border-left: 0px">:&nbsp;</th>
		<td align="left">
		<%-- <form:input path="user_status"/> --%>
			<form:select path="user_status"  style="width: 180px;">
				<form:option value="">- S T A T U S -</form:option>
				<form:option value="A">Active</form:option>
				<form:option value="I">Inactive</form:option>
			</form:select>
		</td>
	</tr>
	<tr class="evenRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Last Login</th>
		<th align="right" style="border-left: 0px">:&nbsp;</th>
		<td align="left"><form:input path="last_login" readonly="true" class="date-long" /></td>
	</tr>
	<tr>
		<td align="center" colspan="3" class="footerBtns">

				<input type="button"  class="buttn"id="modifyRole" name="modifyRole" value="Modify Role"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button"  class="buttn"id="cancel" name="cancel" value="Cancel"> 
		</td>
	</tr>
</table>
</form:form>
</c:when>
	<c:otherwise>
		<center><br/><b>No Record Found.</b></center>
	</c:otherwise>
</c:choose>