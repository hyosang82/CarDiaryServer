package kr.hyosang.cardiary.data.model;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import kr.hyosang.cardiary.Define;
import kr.hyosang.cardiary.data.model.json.DriveLogItem;
import kr.hyosang.cardiary.data.model.json.daum.Coord2Addr;
import kr.hyosang.cardiary.util.Util;

public class DriveLog {
	public static final String KIND = "DriveLog";
	
	public static final String KEY_TIMESTAMP = "timestamp";
	public static final String KEY_DEPARTURE = "departure";
	public static final String KEY_DESTINATION = "destination";
	public static final String KEY_DISTANCE = "distance";
	
	@SerializedName("key")         private Key mMyKey = null;
	@SerializedName("timestamp")   public long mTimestamp;
	@SerializedName("departure")   public String mDeparture = "";
	@SerializedName("destination") public String mDestination = "";
	@SerializedName("distance")    public double mDistance = 0.0f;
	
	
	public static DriveLog query(Key parent, long timestamp) {
		Query q = new Query(KIND, parent);
		q.setFilter(new FilterPredicate(KEY_TIMESTAMP, FilterOperator.EQUAL, timestamp));
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = ds.prepare(q);
		try {
			return new DriveLog(pq.asSingleEntity());
		}catch(Exception e){
			return null;
		}
	}
	
	public static DriveLog getByKey(Key me) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		try {
			return new DriveLog(ds.get(me));
		}catch(EntityNotFoundException e) {
		}
		
		return null;
	}
	
	public DriveLog(Entity e) {
		mMyKey = e.getKey();
		mTimestamp = (long) e.getProperty(KEY_TIMESTAMP);
		mDeparture = (String) e.getProperty(KEY_DEPARTURE);
		mDestination = (String) e.getProperty(KEY_DESTINATION);
		mDistance = (double) e.getProperty(KEY_DISTANCE);
	}
	
	public DriveLog(long timestamp) {
		mTimestamp = timestamp;
	}
	
	public Key getKey() {
		return mMyKey;
	}
	
	public Entity asNewEntity(Key parent) {
		Entity e = new Entity(KIND, parent);
		setEntityData(e);
		
		return e;
	}
	
	public Entity asUpdateEntity() {
		Entity e = new Entity(mMyKey);
		setEntityData(e);
		
		return e;
	}
	
	private void setEntityData(Entity e) {
		e.setProperty(KEY_TIMESTAMP, mTimestamp);
		e.setProperty(KEY_DEPARTURE, mDeparture);
		e.setProperty(KEY_DESTINATION, mDestination);
		e.setProperty(KEY_DISTANCE, mDistance);
	}
	
	public void updateDepDest() {
		if(mMyKey != null) {
			List<DriveLogItem> list = DriveLogData.queryList(mMyKey);
			if(list.size() > 0) {
				DriveLogItem first = list.get(0);
				DriveLogItem last = list.get(list.size() - 1);
				
				String urlFmt = "https://apis.daum.net/local/geo/coord2addr?apikey=" + Define.APIKEY_DAUM + "&format=fullname&inputCoordSystem=WGS84&output=json&latitude=%.6f&longitude=%.6f";
				Gson gson = new Gson();
				DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
				
				if(first != null) {
					String url = String.format(urlFmt, first.mLati, first.mLongi);
					String result = Util.getWebContent(url);
					
					Coord2Addr data = gson.fromJson(result, Coord2Addr.class);
					
					this.mDeparture = data.fullName;
					ds.put(asUpdateEntity());
				}
				
				if(last != null) {
					String url = String.format(urlFmt, last.mLati, last.mLongi);
					String result = Util.getWebContent(url);
					
					Coord2Addr data = gson.fromJson(result, Coord2Addr.class);
					
					this.mDestination = data.fullName;
					ds.put(asUpdateEntity());			
				}
			}
		}
	}
}
