<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
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
<form:form action="DeleteUser.do" method="post" commandName="user">
	<table  style="width: 100%;   text-align:center">
		<tr>
			<th style="padding-top: 15px;  " align="center">
				This user and all assigned roles will be deleted.
			</th>
		</tr>
		<tr>
			<th  style="margin-left: 100px;padding-top:10px;margin-left: 30px;padding-bottom: 35px; text-align:center">
				Are you sure ?
				<form:hidden path="user_id" />
			</th>
		</tr>
		<tr>
			<td align="center" class="footerBtns">
				<input type="button"class="buttn" name="delete" id="delete" value="Delete">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" class="buttn"name="cancel" id="cancel" value="Cancel">
			</td>
		</tr>
	</table>
</form:form>