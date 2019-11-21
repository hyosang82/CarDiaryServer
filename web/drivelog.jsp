<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="kr.hyosang.cardiary.util.Util" %>
<%@ page import="kr.hyosang.cardiary.Define" %>
<%@ include file="/include/head.jsp" %>

<%=getPageTitle("Drive Log", "Driving log")%>

<%
	UserService us = UserServiceFactory.getUserService();
	if(us.isUserLoggedIn() && Util.isUser()) {
%>

<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript" src="http://openapi.map.naver.com/openapi/v2/maps.js?clientId=<%=Define.NAVER_CLIENT_ID%>"></script>

<script type="text/javascript">
var CHK_MASK_DEP = 0x0001;
var CHK_MASK_DEST = 0x0010;
var CHK_MASK_DIST = 0x0100;

var mapObj;
var pointList;
var selMarker;
var pathObj;
var currentKey;
var checkMask;
var updateParam;


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
	
	obj = $("#queryMonth");
	for(var i=1;i<=12;i++) {
		var opt = document.createElement("OPTION");
		opt.value = i;
		opt.text = i + "월";
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
	var m = $("#queryMonth").val();
	
	$.ajax({
		url: "/DriveLog/LogList",
		method: "POST",
		data: {
			vehicle_key: k,
			year: y,
			month: m
		},
		dataType: "json",
		success: function(json) {
			var tbody = $("#driveList tbody");
			
			tbody.empty();
			
			if(json.length == 0) {
				var row = "<tr><td colspan=\"5\" class=\"text-center\">No Data</td></tr>";
				tbody.append(row);
			}else {
				var idx = 1;
				
				for(var k in json) {
					var dt = getDisplayDateArray(json[k].timestamp);
					var dtTxt = dt[0] + "년 " + dt[1] + "월 " + dt[2] + "일";
					dtTxt += " " + dt[3] + ":" + dt[4] + ":" + dt[5];
					
					var row = "<tr onclick=\"showDetail('" + json[k].key + "');\" style=\"cursor:pointer;\">";
					row += "<td>" + (idx++) + "</td>";
					row += "<td>" + dtTxt + "</td>";
					row += "<td>" + json[k].departure + "</td>";
					row += "<td>" + json[k].destination + "</td>";
					row += "<td>" + (Math.floor(json[k].distance) / 1000) + "km</td>";
					
					tbody.append(row);
				}				
			}
		}
	});
}

function showDetail(key) {
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
			
			if(pointList.length == 0) {
				var row = "<tr><td colspan=\"6\" class=\"text-center\">No Data</td></tr>";
				tbody.append(row);
			}else {
				var no = 1;
				for(var k in pointList) {
					var dt = getDisplayDateArray(pointList[k].timestamp);
					var dtTxt = dt[1] + "/" + dt[2] + " ";
					dtTxt += dt[3] + ":" + dt[4] + ":" + dt[5];
					
					var spd = Math.floor(pointList[k].speed * 10) / 10;
					
					var row = "<tr onclick=\"moveToPoint(" + k + ");\" style=\"cursor:pointer;\">";
					row += "<td>" + no++ + "</td>";
					row += "<td>" + dtTxt + "</td>";
					row += "<td>" + pointList[k].longitude + "</td>";
					row += "<td>" + pointList[k].latitude + "</td>";
					row += "<td>" + pointList[k].altitude + "</td>";
					row += "<td>" + spd + "</td>";
					
					tbody.append(row);
				}
			
				drawLines(pointList);
			
				startCheck(json.departure, json.destination, json.distance);
			}
		}
	});
	
	currentKey = key;
}

function drawLines(list) {
	var icon = new nhn.api.map.Icon("/image/pin_spot2.png",
			new nhn.api.map.Size(28, 37),
			new nhn.api.map.Size(14, 37));
	var listLatLng = [];
	
	var idx = 0;
	
	for(var k in list) {
		var point = new nhn.api.map.LatLng(list[k].latitude, list[k].longitude);
		idx++;
		
		listLatLng.push(point);
	}
	
	mapObj.setBound(listLatLng);
	
	pathObj = new nhn.api.map.Polyline(listLatLng);
	pathObj.setStyle({
		strokeColor: "#FF0000",
		strokeWidth: 5,
		strokeOpacity: 1,
		strokeStyle: "solid"
	});
	mapObj.addOverlay(pathObj);
}

function moveToPoint(idx) {
	var pt = new nhn.api.map.LatLng(pointList[idx].latitude, pointList[idx].longitude);
	mapObj.setCenter(pt);
	
	selMarker.setPoint(pt);
	selMarker.setVisible(true);
}

function startCheck(dep, dest, dist) {
	checkMask = 0;
	
	if(dep == "") {
		checkMask |= CHK_MASK_DEP;
	}
	
	if(dest == "") {
		checkMask |= CHK_MASK_DEST;
	}
	
	if(dist == 0) {
		checkMask |= CHK_MASK_DIST;
	}
	
	updateParam = {};
	
	check();
}

function check() {
	if((checkMask & CHK_MASK_DEP) != 0) {
		checkMask = checkMask^CHK_MASK_DEP;
		checkGeocode(0, pointList[0].latitude, pointList[0].longitude);
	}else if((checkMask & CHK_MASK_DEST) != 0) {
		checkMask = checkMask^CHK_MASK_DEST;
		var idx = pointList.length - 1;
		checkGeocode(1, pointList[idx].latitude, pointList[idx].longitude);
	}else {
		//데이터 셋팅 후 업데이트 처리
		updateParam["key"] = currentKey;
		updateParam["distance"] = pathObj.getLength();
		
		$.ajax({
			url: "/DriveLog/UpdateInfo",
			data: updateParam,
			method: "POST"
		});
	}
}

function checkCallback(json) {
	if(json.type == "0") {
		updateParam["departure"] = json.result;		
	}else if(json.type == "1") {
		updateParam["destination"] = json.result;
	}
	
	check();
}

function checkGeocode(t, lat, lng) {
	var apiurl = "/Relay/ReverseGeocode"
	
	$.ajax({
		url: apiurl,
		method: "POST",
		data: {
			type: t,
			lat: lat,
			lng: lng
		},
		dataType: "json",
		success: checkCallback
	});
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

function mergePrevious() {
	if(confirm("이전 로그와 병합합니다.")) {
		$.ajax({
			url: "/DriveLog/MergeWithPrevious",
			method: "POST",
			dataType: "json",
			data: {
				key: currentKey
			},
			success: function(json) {
				if(json.newKey) {
					//현재화면 새로 고침
					showDetail(json.newKey);
					
					//목록도 새로 고침
					query();
				}
			}
		});
	}
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
					<div class="col-md-2">
						<select class="form-control" id="queryYear"></select>
					</div>
					<div class="col-md-2">
						<select class="form-control" id="queryMonth"></select>
					</div>
					<div class="col-md-4">
						<button class="btn btn-primary" onclick="query();">Query</button>
					</div>
				</div>
				
				<div class="table-responsive" style="margin-top:30px;">
					<table class="table table-striped table-bordered table-hover" id="driveList">
						<thead>
							<tr>
								<td class="text-center">#</td>
								<td class="text-center">날짜/시간</td>
								<td class="text-center">출발지</td>
								<td class="text-center">도착지</td>
								<td class="text-center">거리</td>
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
										<td class="text-center">#</td>
										<td class="text-center">시간</td>
										<td class="text-center">경도</td>
										<td class="text-center">위도</td>
										<td class="text-center">고도</td>
										<td class="text-center">속도</td>
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
				<button type="button" class="btn btn-primary" onclick="mergePrevious();">Merge to Previous</button>
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