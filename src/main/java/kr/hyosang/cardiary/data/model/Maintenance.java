package kr.hyosang.cardiary.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;

public class Maintenance {
	public static final String KIND = "Maintenance";
	
	public static final String KEY_DATE = "date";
	public static final String KEY_GARAGE = "garage";
	public static final String KEY_MEMO = "memo";
	public static final String KEY_ODO = "odo";
	public static final String KEY_PRICE = "price";
	
	public String date;
	public String garage;
	public String memo;
	public long odo;
	public long price;
	
	public Maintenance(Entity e) {
		date = (String) e.getProperty(KEY_DATE);
		garage = (String) e.getProperty(KEY_GARAGE);
		memo = (String) e.getProperty(KEY_MEMO);
		odo = (long) e.getProperty(KEY_ODO);
		price = (long) e.getProperty(KEY_PRICE);
	}
	
	public Maintenance(String dt, String grg, String mmo, long odokm, long prc) {
		date = dt;
		garage = grg;
		memo = mmo;
		odo = odokm;
		price = prc;
	}
	
	public static List<Map<String, String>> getGarageList(Key userKey) {
		Query q = new Query(KIND, userKey);
		q.addProjection(new PropertyProjection(KEY_GARAGE, String.class));
		q.setDistinct(true);
		
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = ds.prepare(q);
		for(Entity e : pq.asIterable()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("key", KeyFactory.keyToString(e.getKey()));
			map.put("name", (String) e.getProperty(KEY_GARAGE));
			list.add(map);
		}
		
		return list;
	}
	
	public Entity asNewEntity(Key parent) {
		Entity e = new Entity(KIND, parent);
		
		e.setProperty(KEY_DATE, date);
		e.setProperty(KEY_GARAGE, garage);
		e.setProperty(KEY_MEMO, memo);
		e.setProperty(KEY_ODO, odo);
		e.setProperty(KEY_PRICE, price);
		
		return e;
	}
}
