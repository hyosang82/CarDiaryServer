package kr.hyosang.cardiary.data.model.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import kr.hyosang.cardiary.data.model.Maintenance;
import kr.hyosang.cardiary.data.model.MaintenancePart;
import kr.hyosang.cardiary.data.model.PartItem;

public class MaintenanceJson extends Maintenance {
	public List<MaintenancePartJson> parts = new ArrayList<MaintenancePartJson>();
	
	private MaintenanceJson(Entity e) {
		super(e);
	}
	
	public static MaintenanceJson fromMaintenanceEntity(DatastoreService ds, Entity e, Map<String, PartItem> partMap) {
		MaintenanceJson obj = new MaintenanceJson(e);
		
		Query q = new Query(MaintenancePart.KIND, e.getKey());
		PreparedQuery pq = ds.prepare(q);
		for(Entity ee : pq.asIterable()) {
			MaintenancePart p = new MaintenancePart(ee);//partMap.get((String) ee.getProperty(MaintenancePart.KEY_ITEM_KEY));
			if(p != null) {
				obj.parts.add(new MaintenancePartJson(p, partMap));
			}
		}
		
		return obj;
	}
	
	

}
