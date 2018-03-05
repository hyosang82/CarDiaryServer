package kr.hyosang.cardiary.manage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.hyosang.cardiary.data.model.Maintenance;
import kr.hyosang.cardiary.data.model.MaintenancePart;
import kr.hyosang.cardiary.data.model.MyUser;
import kr.hyosang.cardiary.data.model.PartItem;
import kr.hyosang.cardiary.data.model.Vehicle;
import kr.hyosang.cardiary.data.model.json.MaintenanceJson;
import kr.hyosang.cardiary.data.model.json.daum.KeySerializer;
import kr.hyosang.cardiary.util.Util;

public class MaintenanceServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		String action = uri.substring("/Maintenance/".length());

		String respJson = "";
		
		if("GarageList".equals(action)) {
			respJson = getGarageList();
		}else if("AddPartItem".equals(action)) {
			String partName = (String) req.getParameter("part_name");
			respJson = addNewPartItem(partName);
		}else if("AllParts".equals(action)) {
			respJson = queryAllParts();
		}else if("Add".equals(action)) {
			String headerVin = req.getHeader("VIN");
			String vehicleKey = req.getParameter("v_key");
			String dt = req.getParameter("date");
			long odo = Util.parseLong(req.getParameter("odo"), 0);
			String garage = req.getParameter("garage");
			String parts = req.getParameter("parts");
			long price = Util.parseLong(req.getParameter("price"), 0);
			String memo = req.getParameter("memo");
			
			respJson = add(headerVin, vehicleKey, dt, odo, garage, parts, price, memo);
		}else if("List".equals(action)) {
			String vKey = req.getParameter("key");
			String year = req.getParameter("year");
			
			respJson = getList(KeyFactory.stringToKey(vKey), year);
		}
		
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(respJson);
	}
	
	private String getGarageList() {
		Gson gson = new Gson();
		
		UserService us = UserServiceFactory.getUserService();
		User u = us.getCurrentUser();
		if(u != null) {
			List<Map<String, String>> list = Maintenance.getGarageList(MyUser.getUserKey(u.getEmail()));
			
			return gson.toJson(list);
		}
		
		return "[]";
	}
	
	private String addNewPartItem(String partName) {
		String result;
		
		if(PartItem.isExists(partName)) {
			result = "이미 등록되었습니다 : " + partName;
		}else {
			PartItem item = new PartItem(partName);
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			ds.put(item.asNewEntity());
			result = "등록되었습니다";
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", result);
		
		Gson gson = new Gson();
		return gson.toJson(map);		
	}
	
	private String queryAllParts() {
		List<PartItem> list = PartItem.getAllList();
		
		Gson gson = (new GsonBuilder()).registerTypeAdapter(Key.class, new KeySerializer()).create();
		return gson.toJson(list);
	}
	
	private String add(String headerVin, String vKey, String dt, long odo, String garage, String parts, long price, String memo) {
		String result;
		int is_error = 1;

		do {
			Key vehicleKey = null;
			UserService us = UserServiceFactory.getUserService();
			if(us.isUserLoggedIn()) {
				//로그인 상태면 vin 체크 (웹 접근)
				Key userKey = MyUser.getUserKey(us.getCurrentUser().getEmail());
				Vehicle v = Vehicle.getByKey(KeyFactory.stringToKey(vKey));
				if(!Vehicle.isOwner(userKey, v.mVin)) {
					result = "Vehicle: " + vKey + " is not your vehicle";
					break;
				}else {
					vehicleKey = KeyFactory.stringToKey(v.mEncodedKey);
				}
			}else {
				//headerVin으로 들어옴
				Vehicle v = Vehicle.getByVin(headerVin);
				if(v == null) {
					result = "Cannot found VIN: " + headerVin;
					break;
				}else {
					vehicleKey = KeyFactory.stringToKey(v.mEncodedKey);
				}
			}
			
			if(vehicleKey != null) {
				Maintenance m = new Maintenance(dt, garage, memo, odo, price);
				
				DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
				Key mtKey = ds.put(m.asNewEntity(vehicleKey));
				
				//parts 입력
				String [] partArr = parts.split("\\^");
				for(String part : partArr) {
					if(!Util.isEmpty(part)) {
						MaintenancePart p = new MaintenancePart(part);
						ds.put(p.asNewEntity(mtKey));
					}
				}
				
				result = "Success.";
				is_error = 0;
			}else {
				result = "Vehicle is not exists";
				break;
			}
		}while(false);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", result);
		map.put("is_error", String.valueOf(is_error));
		
		Gson gson = new Gson();
		return gson.toJson(map);
	}
	
	private String getList(Key vKey, String year) {
		long nextYr = ((long) Util.parseLong(year, 0)) + 1;
		
		Query q = new Query(Maintenance.KIND, vKey);
		q.setFilter(CompositeFilterOperator.and(
				new FilterPredicate(Maintenance.KEY_DATE, FilterOperator.GREATER_THAN, year),
				new FilterPredicate(Maintenance.KEY_DATE, FilterOperator.LESS_THAN, String.valueOf(nextYr))
				)
			);
		q.addSort(Maintenance.KEY_DATE);
		q.addSort(Maintenance.KEY_ODO);
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = ds.prepare(q);
		
		Map<String, PartItem> partMap = PartItem.getPartMap();
		List<MaintenanceJson> mtList = new ArrayList<MaintenanceJson>();
		for(Entity e : pq.asIterable()) { 
			mtList.add(MaintenanceJson.fromMaintenanceEntity(ds, e, partMap));
		}
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Key.class, new KeySerializer()).create();
		return gson.toJson(mtList);
	}
}
