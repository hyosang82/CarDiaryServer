package kr.hyosang.cardiary.manage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

import kr.hyosang.cardiary.data.model.MyUser;
import kr.hyosang.cardiary.data.model.Vehicle;
import kr.hyosang.cardiary.util.Util;

public class AddVehicle extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String model = (String) req.getParameter("model_name");
		String year = (String) req.getParameter("yeartype");
		String vin = (String) req.getParameter("vin");
		String plate = (String) req.getParameter("plate");
		
		String errMsg = "";
		
		if(Util.isEmpty(model)) {
			errMsg = "No model name";
		}else if(Util.isEmpty(year)) {
			errMsg = "No model year type";
		}else if(Util.isEmpty(vin)) {
			errMsg = "No VIN";
		}else if(Util.isEmpty(plate)) {
			errMsg = "No plate info";
		}else {
			UserService us = UserServiceFactory.getUserService();
			if(us.isUserLoggedIn()) {
				Key userKey = MyUser.getUserKey(us.getCurrentUser().getEmail());
				if(userKey != null) {
					//insert new entity
					Vehicle v = new Vehicle();
					v.mModelName = model;
					v.mYeartype = year;
					v.mVin = vin;
					v.mPlate = plate;
					
					Entity e = v.getAsEntity(userKey);
					DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
					ds.put(e);
				}else {
					errMsg = "Not signed up yet";
				}
			}else {
				errMsg = "Not signed in";
			}
		}
		
		Map<String, String> res = new HashMap<String, String>();
		res.put("message", errMsg);
		
		Gson gson = new Gson();
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(gson.toJson(res));
	}
}
