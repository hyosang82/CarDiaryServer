package kr.hyosang.cardiary.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.annotations.SerializedName;

public class PartItem {
	public static final String KIND = "PartItem";
	
	public static final String KEY_NAME = "name";
	
	@SerializedName("key")  public Key myKey;
	@SerializedName("name") public String name;
	
	public PartItem(String nm) {
		name = nm;
	}
	
	public PartItem(Entity e) {
		myKey = e.getKey();
		name = (String) e.getProperty(KEY_NAME);
	}
	
	public static boolean isExists(String name) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(KIND);
		q.setFilter(new FilterPredicate(KEY_NAME, FilterOperator.EQUAL, name));
		
		PreparedQuery pq = ds.prepare(q);
		if(pq.countEntities(FetchOptions.Builder.withDefaults()) > 0) {
			return true;
		}else {
			return false;
		}
	}
	
	public static List<PartItem> getAllList() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(KIND);
		q.addSort(KEY_NAME, SortDirection.ASCENDING);
		PreparedQuery pq = ds.prepare(q);
		
		List<PartItem> list = new ArrayList<PartItem>();
		for(Entity e : pq.asIterable()) {
			list.add(new PartItem(e));
		}
		
		return list;
	}
	
	public static Map<String, PartItem> getPartMap() {
		Query q = new Query(KIND);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = ds.prepare(q);
		
		Map<String, PartItem> allMap = new HashMap<String, PartItem>();
		for(Entity e : pq.asIterable()) {
			allMap.put(KeyFactory.keyToString(e.getKey()), new PartItem(e));
		}
		
		return allMap;
	}
	
	public Entity asNewEntity() {
		Entity e = new Entity(KIND);
		e.setProperty(KEY_NAME, name);
		
		return e;
	}
}
