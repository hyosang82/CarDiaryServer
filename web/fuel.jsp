<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="kr.hyosang.cardiary.util.Util" %>
<%@ page import="kr.hyosang.cardiary.Define" %>
<%@ include file="/include/head.jsp" %>

<%=getPageTitle("Fuel", "Fuel records")%>

<%
	UserService us = UserServiceFactory.getUserService();
	if(us.isUserLoggedIn() && Util.isUser()) {
%>

<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript" src="http://openapi.map.naver.com/openapi/v2/maps.js?clientId=<%=Define.NAVER_CLIENT_ID%>"></script>

<script type="text/javascript">
var mapObj;
var selMarker;

$(document).ready(function() {
	initControls();

	loadVehicleList();
	initMap();
	
	//select 초기값 셋팅
	var now = new Date();
	$("#queryYear").val(now.getFullYear());
	$("#queryMonth").val(now.getMonth() + 1);
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

function query() {
	var k = $("#vehicleList").val();
	var y = $("#queryYear").val();
	
	$.ajax({
		url: "/Fuel/Record",
		method: "POST",
		data: {
			vehicle_key: k,
			year: y
		},
		dataType: "json",
		success: function(json) {
			var tbody = $("#recordList tbody");
			tbody.empty();
			
			if(json.length == 0) {
				var row = "<tr><td colspan=\"8\" class=\"text-center\">No Data</td></tr>";
				tbody.append(row);
			}else {
				for(var k in json) {
					var dt = getDisplayDateArray(json[k].timestamp);
					var dtTxt = dt[0] + "년 " + dt[1] + "월 " + dt[2] + "일";
					dtTxt += " " + dt[3] + ":" + dt[4];
					
					var effi = Math.floor(json[k].efficient * 10) / 10;
					var aeffi = Math.floor(json[k].accu_efficient * 10) / 10;
					if(aeffi == 0) {
						aeffi = "-";
					}else {
						aeffi = aeffi + "km/L";
					}
					
					var row = "<tr onclick=\"showDetail('" + json[k].key + "');\" style=\"cursor:pointer;\">";
					row += "<td>" + dtTxt + "</td>";
					row += "<td>" + json[k].odo + "km</td>";
					row += "<td>" + json[k].volume + "L</td>";
					row += "<td>" + (json[k].is_full ? "Y" : "N") + "</td>";
					row += "<td>" + json[k].unit_price + "원</td>";
					row += "<td>" + json[k].total_price + "원</td>";
					row += "<td>" + effi + "km/L</td>";
					row += "<td>" + aeffi + "</td>"
					row += "</tr>";
					
					tbody.append(row);
				}				
			}
		}
	});
}

function showDetail(key) {
	/*
	$("#detailDialog").modal("show");
	
	setTimeout(function() {
		var w = $("#map").width();
		var h = $(window).height();
		
		if(w > (h * 0.6)) {
			h = h * 0.6;
		}else {
			h = w;
		}
		
		mapObj.setSize(new nhn.api.map.Size(w, h));
		$("#pointListArea").height(h);
	}, 500);
	
	$.ajax({
		url: "/DriveLog/LogDetail",
		data: {
			log_key: key
		},
		method: "POST",
		dataType: "json",
		success:function(json) {
			var tbody = $("#pointList tbody");
			
			tbody.empty();
			if(pathObj) {
				mapObj.removeOverlay(pathObj);
			}
			selMarker.setVisible(false);
			
			pointList = json.points;
			
			for(var k in pointList) {
				var dt = getDisplayDateArray(pointList[k].timestamp);
				var dtTxt = dt[1] + "/" + dt[2] + " ";
				dtTxt += dt[3] + ":" + dt[4] + ":" + dt[5];
				
				var row = "<tr onclick=\"moveToPoint(" + k + ");\" style=\"cursor:pointer;\">";
				row += "<td>" + k + "</td>";
				row += "<td>" + dtTxt + "</td>";
				row += "<td>" + pointList[k].longitude + "</td>";
				row += "<td>" + pointList[k].latitude + "</td>";
				row += "<td>" + pointList[k].altitude + "</td>";
				row += "<td>" + pointList[k].speed + "</td>";
				
				tbody.append(row);
			}
			
			drawLines(pointList);
			
			startCheck(json.departure, json.destination, json.distance);
		}
	});
	
	currentKey = key;
	*/
}


function initMap() {
	var w = $("#map").width();
	
	mapObj = new nhn.api.map.Map(document.getElementById("map"), {
		
	});
	
	var icon = new nhn.api.map.Icon("/image/pin_spot2.png",
			new nhn.api.map.Size(28, 37),
			new nhn.api.map.Size(14, 37));
	
	selMarker = new nhn.api.map.Marker(icon, {});
	selMarker.setVisible(false);
	mapObj.addOverlay(selMarker);
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
					</div>
				</div>
				
				<div class="table-responsive" style="margin-top:30px;">
					<table class="table table-striped table-bordered table-hover" id="recordList">
						<thead>
							<tr>
								<td>날짜/시간</td>
								<td>ODO</td>
								<td>주유량</td>
								<td>가득</td>
								<td>리터당 가격</td>
								<td>총 가격</td>
								<td>현재 연비</td>
								<td>누적 연비</td>
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

<div class="modal fade" id="detailDialog" tabindex="-1" role="dialog" aria-labelledby="detailDialogLabel" aria-hidden="true" style="display:none">
	<div class="modal-dialog" style="width:80%;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
				<h4 class="modal-title" id="detailDialogLabel">Log detail</h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<div class="col-md-8">
						<div id="map" style="width:100%;"></div>
					</div>
					<div class="col-md-4" style="overflow-y:scroll;" id="pointListArea">
						<div class="table-responsive">
							<table class="table table-striped table-bordered table-hover" id="pointList">
								<thead>
									<tr>
										<td>#</td>
										<td>시간</td>
										<td>경도</td>
										<td>위도</td>
										<td>고도</td>
										<td>속도</td>
									</tr>
								</thead>
								<tbody>
		
								</tbody>
							</table>
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

<%
	}else {
		response.sendRedirect("/setup.jsp");
	}
%>

<%@ include file="/include/foot.jsp" %>