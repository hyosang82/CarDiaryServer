package kr.hyosang.cardiary.data.model;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;

import kr.hyosang.cardiary.data.model.json.DriveLogItem;

public class DriveLogData {
	public static final String KIND = "DriveLogData";
	
	public static final String KEY_LOGDATA = "logdata";
	
	public String mLogdata;
	
	public static List<DriveLogItem> queryList(Key logKey) {
		return queryList(logKey, null);
	}
	
	public static List<DriveLogItem> queryList(Key logKey, List<Key> logKeySet) {
		Query q = new Query(KIND, logKey);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = ds.prepare(q);

		DriveLogItemSet itemset = new DriveLogItemSet();
		
		for(Entity e : pq.asIterable()) {
			itemset.addLogData(((Text) e.getProperty(KEY_LOGDATA)).getValue());
			
			if(logKeySet != null) {
				logKeySet.add(e.getKey());
			}
		}
		
		return itemset.asList();
	}
	
	public Entity asNewEntity(Key parent) {
		Entity e = new Entity(KIND, parent);
		e.setProperty(KEY_LOGDATA, new Text(mLogdata));
		
		return e;
	}
	
	public Entity asUpdateEntity(Key thisKey) {
		Entity e = new Entity(thisKey);
		e.setProperty(KEY_LOGDATA, new Text(mLogdata));
		
		return e;
		
	}
}
