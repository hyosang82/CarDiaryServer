package kr.hyosang.cardiary.manage;

import java.io.IOException;
import java.net.URLEncoder;
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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;

import kr.hyosang.cardiary.data.model.CarModel;
import kr.hyosang.cardiary.util.Util;

public class CarModelList extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String d = (String) req.getParameter("depth");
		String parent = (String) req.getParameter("parent_key");
		int depth = Util.parseInt(d, 0);
		
		String type;
		switch(depth) {
		case 1: type = "MDL"; d = "1"; break;
		case 2: type = "SMDL"; d = "2"; break;
		case 3: type = "YER"; d = "3"; break;
		case 4: type = "GRD"; d = "4"; break;
		
		default:
		case 0: type = "MNF"; d = "0"; break;
		}
		
		Query q;
		
		if(parent != null) {
			Key k = KeyFactory.stringToKey(parent);
			q = new Query(CarModel.KIND, k);
		}else {
			q = new Query(CarModel.KIND);
		}
		
		Filter f = new FilterPredicate(CarModel.KEY_TYPE, FilterOperator.EQUAL, type);
		q.setFilter(f);
		q.addSort(CarModel.KEY_LABEL);
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = ds.prepare(q);
		
		List<Map<String, String>> modelList = new ArrayList<Map<String, String>>();
		
		for(Entity e : pq.asIterable()) {
			Map<String, String> item = new HashMap<String, String>();
			item.put("key", KeyFactory.keyToString(e.getKey()));
			item.put("label", ((String) e.getProperty(CarModel.KEY_LABEL)));
			
			modelList.add(item);
		}
		
		Map<String, Object> mainObj = new HashMap<String, Object>();
		mainObj.put("depth", d);
		mainObj.put("list", modelList);
		
		Gson gson = new Gson();
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(gson.toJson(mainObj));
	}
}
