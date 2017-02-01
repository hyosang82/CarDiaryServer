package kr.hyosang.cardiary.manage;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import kr.hyosang.cardiary.data.model.Vehicle;

public class VehicleServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		String action = uri.substring("/Vehicle/".length());

		String respJson = "";
		
		if("Reorder".equals(action)) {
			String keys = (String) req.getParameter("key_order");
			String [] keyArr = keys.split("\\^");
			reorder(keyArr);
		}

		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(respJson);
	}
	
	private void reorder(String [] keys) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		int ordering = 1;
		
		for(String key : keys) {
			try {
				Entity e = ds.get(KeyFactory.stringToKey(key));
				Vehicle v = new Vehicle(e);
				v.mOrder = ordering;
				ds.put(v.getAsUpdateEntity());
				ordering++;
			}catch(EntityNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}


