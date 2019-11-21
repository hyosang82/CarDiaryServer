<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="kr.hyosang.cardiary.util.Util" %>
<%@ include file="/include/head.jsp" %>

<%=getPageTitle("Setup", "Configure user info")%>

<%
	UserService us = UserServiceFactory.getUserService();
	if(us.isUserLoggedIn()) {
%>

<script type="text/javascript">
var orderArr = [];
$(document).ready(function() {
	$("#carManufacture").change(onSelectChanged);
	$("#carModel").change(onSelectChanged);
	$("#carSubModel").change(onSelectChanged);
	$("#carYear").change(onSelectChanged);
	$("#carGrade").change(onSelectChanged);
	
	loadVehicleList();
});

function signup() {
	$.ajax({
		url: "/manage/SignIn",
		method: "POST",
		success: function(data) {
			if(data == "OK") {
				document.location.reload();
			}else {
				alert(data);
			}
		}
	});
}

function loadModels(depth, key) {
	var param = {};
	
	if(depth > 0) {
		param["parent_key"] = key;
	}
	param["depth"] = depth;
	
	$.ajax({
		url: "/manage/CarModelList",
		method:"POST",
		data: param, 
		dataType: "json",
		success: function(data) {
			var objid;
			if(data.depth == 0) {
				objid = "#carManufacture";
			}else if(data.depth == 1) {
				objid = "#carModel";
			}else if(data.depth == 2) {
				objid = "#carSubModel";
			}else if(data.depth == 3) {
				objid = "#carYear";
			}else if(data.depth == 4) {
				objid = "#carGrade";
			}else {
				return;
			}
			
			while($(objid + " option").length > 0) {
				$(objid + " option:last").remove();
			}
			for(var i in data.list) {
				var opt = document.createElement("OPTION");
				opt.value = data.list[i].key;
				opt.text = data.list[i].label;
				
				$(objid).append(opt);
			}
			
			var opt = document.createElement("OPTION");
			opt.text = "SELECT";
			$(objid).prepend(opt);
			
			$(objid).attr("disabled", false);
		}
	});
	
	
	switch(parseInt(depth, 10)) {
	case 0:
		$("#carModel option:eq(0)").attr("selected", "true");
		$("#carModel").attr("disabled", true);
	case 1:
		$("#carSubModel option:eq(0)").attr("selected", "true");
		$("#carSubModel").attr("disabled", true);
	case 2:
		$("#carYear option:eq(0)").attr("selected", "true");
		$("#carYear").attr("disabled", true);
	case 3:
		$("#carGrade option:eq(0)").attr("selected", "true");
		$("#carGrade").attr("disabled", true);
	}
}

function saveVehicle() {
	var mdl = $("#carModelName").val();
	var year = $("#carYearTxt").val();
	var vin = $("#carVin").val();
	var plate = $("#carPlate").val();
	
	if(mdl == "") {
		alert("Input model name");
		return;
	}else if(year == "") {
		alert("Input year type");
		return;
	}else if(vin == "") {
		alert("Input VIN");
		return;
	}else {
		var param = {};
		param["model_name"] = mdl;
		param["yeartype"] = year;
		param["vin"] = vin;
		param["plate"] = plate;
		
		$.ajax({
			url: "/manage/AddVehicle",
			method: "POST",
			data: param,
			dataType: "json",
			success: function(data) {
				if(data.message == "") {
					$("#addDialog").modal("toggle");
					loadVehicleList();
				}else {
					alert(data.message);
				}
			}
		});
	}
	
}

function onSelectChanged() {
	if($(this).attr("id") == "carGrade") {
		var mdl1 = $("#carModel option:selected").text();
		var mdl2 = $("#carSubModel option:selected").text();
		var modelName = $("#carManufacture option:selected").text();
		
		if(mdl2.indexOf(mdl1) >= 0) {
			modelName += " " + mdl2;
		}else {
			modelName += " " + mdl1 + " " + mdl2;
		}
		modelName += " " + $("#carGrade option:selected").text();
		var modelYear = $("#carYear option:selected").text();
		
		$("#carModelName").val(modelName);
		$("#carYearTxt").val(modelYear);
	}else {
		var depth = $(this).attr("data-nextdepth");
		var val = $(this).val();
		loadModels(depth, val);
	}
}

function loadVehicleList() {
	$.ajax({
		url: "/manage/VehicleList",
		method: "POST",
		dataType: "json",
		success: function(data) {
			orderArr = data;
			
			drawTable(data);
			
		}
	});
}

function drawTable(listData) {
	var tbody = $("#vehicleList tbody");
	
	tbody.empty();
	
	var no = 1;
	if(listData.length == 0) {
		var row = "<tr><td colspan=\"5\" class=\"text-center\">No Data</td></tr>";
		tbody.append(row);
	}else {
		for(var k in listData) {
			var row = "<tr><td>" + no++ + "</td>";
			row += "<td>" + listData[k].model_name + "</td>";
			row += "<td>" + listData[k].vin + "</td>";
			row += "<td>" + listData[k].plate + "</td>";
			row += "<td>";
			if(k > 0) {
				row += "<button type='button' class='btn btn-default'><span class='glyphicon glyphicon-arrow-up' onclick=\"moveUp('" + listData[k].encoded_key + "');\" /></button>";
			}
			
			if(k < listData.length-1) {
				row += "<button type='button' class='btn btn-default'><span class='glyphicon glyphicon-arrow-down' onclick=\"moveDown('" + listData[k].encoded_key + "');\" /></button>";
			}
			row += "</td>";
			row += "</tr>";
			tbody.append(row);
		}		
	}
}

function moveUp(key) {
	move(key, -1);
}

function moveDown(key) {
	move(key, 1);
}

function move(key, dir) {
	//find key
	var idx = -1;
	for(var k in orderArr) {
		if(orderArr[k].encoded_key == key) {
			idx = k;
			break;
		}
	}
	
	if(idx != -1) {
		idx = Number(idx);
		if((dir < 0) && (idx > 0)) {
			//up
			var tmp = orderArr[idx];
			orderArr[idx] = orderArr[idx-1];
			orderArr[idx-1] = tmp;
		}else if((dir > 0) && (idx < orderArr.length-1)) {
			//down
			var tmp = orderArr[idx];
			orderArr[idx] = orderArr[idx+1];
			orderArr[idx+1] = tmp;
		}else {
			return;
		}
		
		drawTable(orderArr);
	}
}

function saveOrder() {
	var order = "";
	for(var k in orderArr) {
		order += orderArr[k].encoded_key + "^";
	}
	
	$.ajax({
		url: "/Vehicle/Reorder",
		method: "POST",
		data: {
			key_order: order
		},
		success: function(data) {
			alert("Reordering saved");
			loadVehicleList();
		}
	});
}
</script>

<div class="row">
	<div class="col-md-12">
	
<%
		if(Util.isUser()) {
%>
		<div class="panel panel-default">
			<div class="panel-heading">Registered Vehicles</div>
			<div class="panel-body">
				<div class="table-responsive">
					<table class="table table-striped table-bordered table-hover" id="vehicleList">
						<thead>
							<tr>
								<td class="text-center">#</td>
								<td class="text-center">차종</td>
								<td class="text-center">차대번호</td>
								<td class="text-center">번호판</td>
								<td class="text-center">순서변경<button class="btn btn-sm btn-default" onclick="saveOrder();">저장</button></td>
							</tr>
						</thead>
						<tbody>

						</tbody>
					</table>
				</div>
				<div class="row text-center">
					<button class="btn btn-primary" data-toggle="modal" data-target="#addDialog" onclick="loadModels(0, '');">Add</button>
					<div class="modal fade" id="addDialog" tabindex="-1" role="dialog" aria-labelledby="addDialogLabel" aria-hidden="true" style="display:none">
						<div class="modal-dialog">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
									<h4 class="modal-title" id="addDialogLabel">Add vehicle</h4>
								</div>
								<div class="modal-body">
									<div class="row">
										<div class="col-md-offset-1 col-md-10">
											<div class="form-group">
												<label>제조사/모델</label>
												<select class="form-control" id="carManufacture" data-nextdepth="1"></select>
												<select class="form-control" id="carModel" data-nextdepth="2"></select>
												<select class="form-control" id="carSubModel" data-nextdepth="3"></select>
												<select class="form-control" id="carYear" data-nextdepth="4"></select>
												<select class="form-control" id="carGrade"></select>
											</div>
											<div class="form-group">
												<label>차종명</label>
												<input class="form-control" id="carModelName" type="text" disabled />
											</div>
											<div class="form-group">
												<label>연식</label>
												<input class="form-control" id="carYearTxt" type="text" disabled />
											</div>
											<div class="form-group">
												<label>VIN</label>
												<input class="form-control" id="carVin" type="text" />
											</div>
											<div class="form-group">
												<label>Plate</label>
												<input class="form-control" id="carPlate" type="text" />
											</div>
										</div>
									</div>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
									<button type="button" class="btn btn-primary" onclick="saveVehicle();">Save</button>
								</div>
							</div>
						
						
						</div>
					</div>
				</div>
				
			</div>
		</div>
<%
		}else {
%>

		<div class="row">
			<div class="alert alert-info">
				Not signed up yet.
			</div>
			<div class="form-group text-right">
				<button class="btn btn-primary btn-lg" onclick="signup();">Sign up</button>
			</div>
		</div>

<%
		}
%>
	</div>
</div>

<%
	}else {
%>
<div class="row">
	<div class="alert alert-danger">
		<strong>Error</strong> Not logged in.
	</div>
	
</div>
<%

	}
%>

<%@ include file="/include/foot.jsp" %>

