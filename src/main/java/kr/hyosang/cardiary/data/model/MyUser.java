package kr.hyosang.cardiary.data.model;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;

public class MyUser {
	public static final String KIND = "MyUser";
	
	public static final String KEY_USER_ID = "user_id";
	
	private static Entity getUserEntity(String id) {
		Query q = new Query(KIND);
		q.setFilter(new FilterPredicate(KEY_USER_ID, FilterOperator.EQUAL, id));
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = ds.prepare(q);

		if(pq.countEntities(FetchOptions.Builder.withDefaults()) == 1) {
			return pq.asSingleEntity();
		}else {
			return null;
		}
	}
	
	public static MyUser getUser(String id) {
		Entity e = getUserEntity(id);
		if(e != null) {
			return new MyUser(e);
		}
		
		return null;
	}
	
	public static Key getUserKey(String id) {
		Entity e = getUserEntity(id);
		if(e != null) {
			return e.getKey();
		}
		
		return null;
	}
	
	public String mUserId;
	
	public MyUser(Entity e) {
		mUserId = (String) e.getProperty(KEY_USER_ID);
	}
	
	public MyUser(User u) {
		mUserId = u.getEmail();
	}
	
	public Entity intoEntity(Entity e) {
		e.setProperty(KEY_USER_ID, mUserId);
		
		return e;
	}
}
