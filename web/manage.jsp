<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/include/head.jsp" %>

<%=getPageTitle("Manage", "Administrator management")%>

<%
	UserService us = UserServiceFactory.getUserService();
	if(us.isUserLoggedIn() && us.isUserAdmin()) {
%>

<script type="text/javascript">
function carmodelSubmit() {
	var f = document.forms["car_upload"];
	if(f.modellist.value == "") {
		alert("내용 없음");
		return false;
	}
	
	$.ajax({
		url: "/manage/CarModelUpdater",
		method: "POST",
		data: {
			modellist: f.modellist.value
		},
		success: function(data) {
			alert(data);
		}
	});
}
</script>

<div class="row">
	<div class="col-md-6">
		<div class="panel panel-default">
			<div class="panel-heading">Upload model database</div>
			<div class="panel-body">
				<form name="car_upload">
				<div class="form-group">
					<label>
						Model List (제조사^모델^세부모델^연식^등급)
					</label>
					<textarea class="form-control" rows="10" name="modellist"></textarea>
				</div>
				<button type="button" class="btn btn-default" onclick="carmodelSubmit();">Add Models</button>
				</form>
			</div>
		</div>
	</div>
</div>

<%
	}else {
%>
<div class="row">
	<div class="alert alert-danger">
		<strong>Error</strong> Not logged in or not admin user.
	</div>
	
</div>
<%

	}
%>

<%@ include file="/include/foot.jsp" %>