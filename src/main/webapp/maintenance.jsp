<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="kr.hyosang.cardiary.util.Util" %>
<%@ page import="kr.hyosang.cardiary.Define" %>
<%@ include file="/include/head.jsp" %>

<%=getPageTitle("Maintenance", "Maintenance records")%>

<%
	UserService us = UserServiceFactory.getUserService();
	if(us.isUserLoggedIn() && Util.isUser()) {
%>

<script type="text/javascript" src="/js/common.js"></script>

<script type="text/javascript">
$(document).ready(function() {
	initControls();

	loadVehicleList();

	//select 초기값 셋팅
	var now = new Date();
	$("#queryYear").val(now.getFullYear());
	
	$("#inputParts").on("focus", showPartDialog);
	
	$("#newPartName").keydown(function(k) {
		if(k.keyCode == 13) {
			addNewPart();
		}
	});
	
	$("#garageSelect").change(function() {
		if($("#garageSelect option:selected").val() == "_USER_INPUT_") {
			$("#garageInput").attr("ReadOnly", false);
			$("#garageInput").val("");
		}else {
			$("#garageInput").attr("ReadOnly", true);
			$("#garageInput").val($("#garageSelect option:selected").text());
		}
	});
});

function initControls() {
	var obj = $("#queryYear");
	var now = new Date();
	for(var i=2013;i<=now.getFullYear();i++) {
		var opt = document.createElement("OPTION");
		opt.value = i;
		opt.text = i + "년";
		obj.append(opt);
	}
}

function initAddForm() {
	var v = $("#vehicleList :selected").text().trim();
	$("#vehicleName").val(v);
	
	//오늘 날짜로 기본 셋팅
	var dt = new Date();
	var dtStr = dt.getFullYear() + "-";
	dtStr += ((dt.getMonth() < 9) ? ("0" + (dt.getMonth() + 1)) : dt.getMonth()) + "-";
	dtStr += ((dt.getDate() < 10) ? ("0" + dt.getDate()) : dt.getDate());
	$("#inputDate").val(dtStr);
	
	//정비소 목록 조회
	$.ajax({
		url: "/Maintenance/GarageList",
		method: "POST",
		dataType: "json",
		success: function(json) {
			$("#garageSelect").empty();
			

			for(var k in json) {
				var opt = document.createElement("OPTION");
				opt.value = json[k].key;
				opt.text = json[k].name;
				$("#garageSelect").append(opt);
			}
			
			var opt = document.createElement("OPTION");
			opt.value = "_USER_INPUT_";
			opt.text = "직접 입력";
			opt.selected = true;
			$("#garageSelect").append(opt);
		}
	});
}


function loadVehicleList() {
	$.ajax({
		url: "/manage/VehicleList",
		method: "POST",
		dataType: "json",
		success: function(data) {
			var opts = $("#vehicleList");
			
			for(var k in data) {
				var o = document.createElement("OPTION");
				o.value = data[k].encoded_key;
				o.text = data[k].model_name;
				
				opts.append(o);
			}
			
			if(data.length > 0) {
				//로딩
				query();
			}
		}
	});
}

function showPartDialog() {
	$("#partDialog").modal("toggle");
	
	queryAllParts();
}

function addNewPart() {
	var partName = $("#newPartName").val();
	if(partName.length > 0) {
		if(confirm(partName + "을(를) 추가합니다.")) {
			$.ajax({
				url: "/Maintenance/AddPartItem",
				method: "POST",
				data: {
					part_name: partName
				},
				dataType: "json",
				success: function(json) {
					$("#newPartName").val("");
					alert(json.result);
					
					queryAllParts();
				}
			})
		}
	}
}

function queryAllParts() {
	$.ajax({
		url: "/Maintenance/AllParts",
		method: "POST",
		dataType: "json",
		success: function(json) {
			var tbody = $("#partItemList tbody");
			tbody.empty();
			
			if(json.length == 0) {
				var row = "<tr><td colspan=\"2\" class=\"text-center\">No Data</td></tr>";
				tbody.append(row);
			}else {
				for(var k in json) {
					var row = "<tr>";
					row += "<td><input class='form-controls' type='checkbox' name='part_check' value='" + json[k].key + "' data-nm='" + json[k].name + "' /></td>";
					row += "<td>" + json[k].name + "</td></tr>";
					tbody.append(row);
				}
			}
			
		}
	});
	
}

function applyParts() {
	var nm = "";
	var k = "";
	
	$("input[name=part_check]:checked").each(function(v) {
		var obj = $(this);
		nm += obj.attr("data-nm") + ",";
		k += obj.val() + "^";
	});	
	
	$("#inputPartKeys").val(k);
	$("#inputParts").val(nm);
	
	$("#partDialog").modal("toggle");
}

function save() {
	var vkey = $("#vehicleList").val(); 
	var dt = $("#inputDate").val();
	var odo = $("#inputOdo").val();
	var grg = $("#garageInput").val();
	var parts = $("#inputPartKeys").val();
	var price = $("#totalPrice").val();
	var memo = $("#memo").val();
	
	if(dt == "" || odo == "" || grg == "" || parts == "" || price == "") {
		alert("입력값 누락");
		return;
	}
	
	var param = {};
	param["v_key"] = vkey;
	param["date"] = dt;
	param["odo"] = odo;
	param["garage"] = grg;
	param["parts"] = parts;
	param["price"] = price;
	param["memo"] = memo;
	
	$.ajax({
		url: "/Maintenance/Add",
		method: "POST",
		data: param,
		dataType: "json",
		success: function(json) {
			alert(json.result);
			
			if(json.is_error == "0") {
				$("#addDialog").modal("toggle");
				
				$("#inputDate").val("");
				$("#inputOdo").val("");
				$("#garageInput").val("");
				$("#inputPartKeys").val("");
				$("#inputParts").val("");
				$("#totalPrice").val("");
				$("#memo").val("");
				
				query();
			}
		}
	});
}


function query() {
	var k = $("#vehicleList").val();
	var y = $("#queryYear").val();
	
	$.ajax({
		url: "/Maintenance/List",
		method: "POST",
		data: {
			key: k,
			year: y
		},
		dataType: "json",
		success: function(json) {
			var tbody = $("#mtList tbody");
			tbody.empty();
			
			if(json.length == 0) {
				var row = "<tr><td colspan=\"6\" class=\"text-center\">No Data</td></tr>";
				tbody.append(row);
			}else {
				for(var k in json) {
					var partsArr = json[k].parts;
					var partsStr = "";
					
					for(var j in partsArr) {
						partsStr += partsArr[j].name + "<br />";
					}
					
					//var row = "<tr onclick=\"showDetail('" + json[k].key + "');\" style=\"cursor:pointer;\">";
					var row = "<tr>";
					row += "<td>" + json[k].date + "</td>";
					row += "<td>" + json[k].garage + "</td>";
					row += "<td>" + json[k].odo + "km</td>";
					row += "<td>" + partsStr + "</td>";
					row += "<td>" + json[k].price + "원</td>";
					row += "<td>" + json[k].memo + "</td>";
					row += "</tr>";
					
					tbody.append(row);
				}				
			}
		}
	});
}

</script>

<div class="row">
	<div class="col-md-12">
		<div class="panel panel-default">
			<div class="panel-body">
				<div class="row">
					<div class="col-md-4">
						<select class="form-control" id="vehicleList">
							
						</select>
					</div>
					<div class="col-md-4">
						<select class="form-control" id="queryYear"></select>
					</div>
					<div class="col-md-4">
						<button class="btn btn-primary" onclick="query();">Query</button>
						<button class="btn btn-primary" data-toggle="modal" data-target="#addDialog" onclick="initAddForm();">Add</button>
					</div>
				</div>
				
				<div class="table-responsive" style="margin-top:30px;">
					<table class="table table-striped table-bordered table-hover" id="mtList">
						<thead>
							<tr>
								<td class="text-center">날짜</td>
								<td class="text-center">정비소</td>
								<td class="text-center">ODO</td>
								<td class="text-center">항목</td>
								<td class="text-center">총 금액</td>
								<td class="text-center">메모</td>
							</tr>
						</thead>
						<tbody>

						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="addDialog" tabindex="-1" role="dialog" aria-labelledby="addDialogLabel" aria-hidden="true" style="display:none">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
				<h4 class="modal-title" id="addDialogLabel">Add Maintenance</h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<div class="col-md-offset-1 col-md-10">
						<div class="form-group">
							<label>Vehicle</label>
							<input class="form-control" id="vehicleName" type="text" disabled />
						</div>
						<div class="form-group">
							<label>Date</label>
							<input class="form-control" id="inputDate" type="text" />
						</div>
						<div class="form-group">
							<label>ODO</label>
							<input class="form-control" id="inputOdo" type="text" />
						</div>
						<div class="form-group">
							<label>정비소</label>
							<select class="form-control" id="garageSelect">
							</select>
							<input class="form-control" id="garageInput" type="text" />
						</div>
						<div class="form-group">
							<label>항목</label>
							<input class="form-control" id="inputParts" type="text" readonly />
							<input type="hidden" id="inputPartKeys" />
						</div>
						<div class="form-group">
							<label>총 금액</label>
							<input class="form-control" id="totalPrice" type="text" />
						</div>
						<div class="form-group">
							<label>메모</label>
							<textarea id="memo" class="form-control" rows="3"></textarea>
						</div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				<button type="button" class="btn btn-primary" onclick="save();">Save</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="partDialog" tabindex="-1" role="dialog" aria-labelledby="partDialogLabel" aria-hidden="true" style="display:none">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
				<h4 class="modal-title" id="partDialogLabel">Parts</h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<div class="col-md-offset-1 col-md-10">
						<div class="table-responsive" style="margin-top:30px;">
							<table class="table table-striped table-bordered table-hover" id="partItemList">
								<thead>
									<tr>
										<td></td>
										<td>파츠명</td>
									</tr>
								</thead>
								<tbody>
		
								</tbody>
							</table>
						</div>
						
						<div class="form-group">
							<div class="col-md-10">
								<input class="form-control" id="newPartName" type="text" placeholder="항목 추가" />
							</div>
							<div class="col-md-2">
								<button class="btn btn-primary center-text" onclick="addNewPart();">추가</button>
							</div>
						</div>
						
					</div>
					
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="applyParts();">적용</button>
			</div>
		</div>
	</div>
</div>


<%
	}else {
		response.sendRedirect("/setup.jsp");
	}
%>

<%@ include file="/include/foot.jsp" %>