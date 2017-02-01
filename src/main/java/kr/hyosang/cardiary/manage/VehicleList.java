package kr.hyosang.cardiary.manage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

import kr.hyosang.cardiary.data.model.MyUser;
import kr.hyosang.cardiary.data.model.Vehicle;
import kr.hyosang.cardiary.util.Util;

public class VehicleList extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Vehicle> list = new ArrayList<Vehicle>();
		
		UserService us = UserServiceFactory.getUserService();
		if(us.isUserLoggedIn()) {
			String email = us.getCurrentUser().getEmail();
			if(Util.isUser()) {
				Key parent = MyUser.getUserKey(email);
				
				Query q = new Query(Vehicle.KIND, parent);
				q.addSort(Vehicle.KEY_ORDER, SortDirection.ASCENDING);
				DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
				PreparedQuery pq = ds.prepare(q);
				
				for(Entity e : pq.asIterable()) {
					Vehicle v = new Vehicle(e);
					
					list.add(v);
				}
			}
		}
		
		resp.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();
		resp.getWriter().write(gson.toJson(list));
	}
}
