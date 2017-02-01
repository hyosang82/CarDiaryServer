package kr.hyosang.cardiary.data.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.annotations.SerializedName;

public class Fuel {
	public static final String KIND = "Fuel";
	
	public static final String KEY_ACCU_EFFICIENT = "accu_efficient";
	public static final String KEY_TIMESTAMP = "timestamp";
	public static final String KEY_EFFICIENT = "efficient";
	public static final String KEY_IS_FULL = "full";
	public static final String KEY_ODO = "odo";
	public static final String KEY_STATION  = "station";
	public static final String KEY_TOTAL_PRICE = "total_price";
	public static final String KEY_UNIT_PRICE = "unit_price";
	public static final String KEY_VOLUME = "volume";
	
	@SerializedName("key")            private Key myKey = null;
	@SerializedName("timestamp")      public long timestamp;
	@SerializedName("accu_efficient") public double accuEfficient = 0.0f;
	@SerializedName("efficient")      public double efficient = 0.0f;
	@SerializedName("is_full")        public boolean isFull = false;
	@SerializedName("odo")            public long odo;
	@SerializedName("station")        public String station;
	@SerializedName("total_price")    public long totalPrice;
	@SerializedName("unit_price")     public long unitPrice;
	@SerializedName("volume")         public double volume;
	
	public Fuel(long ts, boolean bFull, long odo, String stn, long totalPrc, long unitPrc, double vol) {
		this.timestamp = ts;
		this.isFull = bFull;
		this.odo = odo;
		this.station = stn;
		this.totalPrice = totalPrc;
		this.unitPrice = unitPrc;
		this.volume = vol;
	}
	
	public Fuel(Entity e) {
		myKey = e.getKey();
		timestamp = (long) e.getProperty(KEY_TIMESTAMP);
		accuEfficient = (double) e.getProperty(KEY_ACCU_EFFICIENT);
		efficient = (double) e.getProperty(KEY_EFFICIENT);
		isFull = (boolean) e.getProperty(KEY_IS_FULL);
		odo = (long) e.getProperty(KEY_ODO);
		station = (String) e.getProperty(KEY_STATION);
		totalPrice = (long) e.getProperty(KEY_TOTAL_PRICE);
		unitPrice = (long) e.getProperty(KEY_UNIT_PRICE);
		volume = (double) e.getProperty(KEY_VOLUME);
	}
	
	public static List<Fuel> getAllList(Key vehicleKey) {
		return getList(vehicleKey, 0);
	}
	
	public static List<Fuel> getList(Key vehicleKey, int year) {
		Query q = new Query(KIND, vehicleKey);
		
		if(year > 1900) {
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("KST"));
			c.set(year, 0, 1, 0, 0, 0);
			c.set(Calendar.MILLISECOND, 0);
			long start = c.getTimeInMillis();
			c.add(Calendar.YEAR, 1);
			long end = c.getTimeInMillis();
			
			q.setFilter(CompositeFilterOperator.and(
					new FilterPredicate(KEY_TIMESTAMP, FilterOperator.GREATER_THAN_OR_EQUAL, start),
					new FilterPredicate(KEY_TIMESTAMP, FilterOperator.LESS_THAN, end)
					));
		}
		
		q.addSort(KEY_TIMESTAMP, SortDirection.DESCENDING);
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		List<Fuel> list = new ArrayList<Fuel>();
		
		PreparedQuery pq = ds.prepare(q);
		for(Entity e : pq.asIterable()) {
			list.add(new Fuel(e));
		}
		
		return list;
	}
	
	public Entity asNewEntity(Key parentKey) {
		Entity e = new Entity(KIND, parentKey);
		return setEntityProperties(e);
	}
	
	public Entity asUpdateEntity() {
		Entity e = new Entity(myKey);
		return setEntityProperties(e);
	}
	
	private Entity setEntityProperties(Entity e) {
		e.setProperty(KEY_TIMESTAMP, timestamp);
		e.setProperty(KEY_ODO, odo);
		e.setProperty(KEY_IS_FULL, isFull);
		e.setProperty(KEY_UNIT_PRICE, unitPrice);
		e.setProperty(KEY_TOTAL_PRICE, totalPrice);
		e.setProperty(KEY_VOLUME, volume);
		e.setProperty(KEY_STATION, station);
		e.setProperty(KEY_EFFICIENT, efficient);
		e.setProperty(KEY_ACCU_EFFICIENT, accuEfficient);
		
		return e;
	}
	
	public Key getKey() {
		return myKey;
	}

}
