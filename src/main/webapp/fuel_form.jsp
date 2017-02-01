<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.PreparedQuery" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="kr.hyosang.cardiary.util.Util" %>
<%@ page import="kr.hyosang.cardiary.Define" %>
<%@ page import="kr.hyosang.cardiary.data.model.MyUser" %>
<%@ page import="kr.hyosang.cardiary.data.model.Vehicle" %>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>New Fuel Record</title>
	<!-- Bootstrap Styles-->
    <link href="assets/css/bootstrap.css" rel="stylesheet" />
     <!-- FontAwesome Styles-->
    <link href="assets/css/font-awesome.css" rel="stylesheet" />
    <!-- Custom Styles-->
    <link href="assets/css/custom-styles.css" rel="stylesheet" />
     <!-- Google Fonts-->
   <link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css' />
   
   <!-- jQuery Js -->
    <script src="assets/js/jquery-1.10.2.js"></script>
    <script type="text/javascript">
    
    </script>
</head>
<body>
<%
	String user = request.getParameter("user");
    Key key = null;
    String keyStr = "";
    ArrayList<Vehicle> list = new ArrayList<Vehicle>();
    if(!Util.isEmpty(user)) {
    	key = MyUser.getUserKey(user);
    	keyStr = KeyFactory.keyToString(key);
    	
    	Query q = new Query(Vehicle.KIND, key);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = ds.prepare(q);
		
		for(Entity e : pq.asIterable()) {
			Vehicle v = new Vehicle(e);
			
			list.add(v);
		}
    }
	
%>
    <div id="wrapper">
		<script type="text/javascript" src="/js/common.js"></script>
		
		<script type="text/javascript">
		$(document).ready(function() {
			$("#unitPrice").on("keyup", calculate);
			$("#totalPrice").on("keyup", calculate);
			$("#liters").on("keyup", calculate);
			
			if(navigator.geolocation) {
				navigator.geolocation.getCurrentPosition(setPosition);
			}
		});
		
		function setPosition(pos) {
			var str = pos.coords.latitude + "|" + pos.coords.longitude;
			$("#station").val(str);
		}
		
		function calculate() {
			var up = $("#unitPrice");
			var tp = $("#totalPrice");
			var l = $("#liters");
			
			if(up.is(":focus") || l.is(":focus")) {
				//총금액 산출
				var iup = parseInt(up.val(), 10);
				var il = parseFloat(l.val());
				var itp = iup * il;
				if(!isNaN(itp)) {
					tp.val(itp);
				}
			}else if(tp.is(":focus")) {
				var iup = parseInt(up.val(), 10);
				var itp = parseInt(tp.val(), 10);
				var il = itp / iup;
				if(!isNaN(il)) {
					il = Math.floor(il * 100) / 100;
					l.val(il);
				}
			}
			
		}
		
		function save() {
			var vin = $("#vehicle").val();
			var odo = $("#odo").val();
			var up = $("#unitPrice").val();
			var tp = $("#totalPrice").val();
			var liter = $("#liters").val();
			var full = $("#isfull").prop("checked") ? "Y" : "N";
			var ts = (new Date()).getTime();
			
			if(vin == "" || odo == "" || up == "" || tp == "" || liter == "") {
				alert("Fill form.");
			}else {
				$.ajax({
					url: "/Fuel/AddRecord",
					method: "POST",
					data: {
						inputVehicle: vin,
						inputOdo: odo,
						inputPrice: up,
						inputTotalPrice: tp,
						inputVolume: liter,
						inputIsFull: full,
						inputDate: ts,
						inputStation: $("#station").val()
					},
					success: function(data) {
						alert("OK");
						window.close();
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
							<div class="form-group">
								<label>Vehicle</label>
								<select id="vehicle" class="form-control">
<%
	for(Vehicle v : list) {
%>
									<option value="<%=v.mVin%>"><%=v.mModelName%></option>
<%
	}
%>

								</select>
							</div>
							<div class="form-group">
								<label>ODO</label>
								<input class="form-control" id="odo" type="text" />
							</div>
							<div class="form-group">
								<label>Station</label>
								<input class="form-control" id="station" type="text" />
							</div>
							<div class="form-group">
								<label>WON/Liter</label>
								<input class="form-control" id="unitPrice" type="text" />
							</div>
							<div class="form-group">
								<label>Total price</label>
								<input class="form-control" id="totalPrice" type="text" />
							</div>
							<div class="form-group">
								<label>Liter</label>
								<input class="form-control" id="liters" type="text" />
							</div>
							<div class="form-group">
								<div class="checkbox">
									<label>
										<input id="isfull" type="checkbox" /> Full
									</label>
								</div>
							</div>

							<button class="btn btn_primary" style="width:100%;" onclick="save();">Save</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>