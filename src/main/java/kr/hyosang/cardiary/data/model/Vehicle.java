package kr.hyosang.cardiary.data.model;

import java.util.logging.Logger;

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
import com.google.gson.annotations.SerializedName;


public class Vehicle {
	public static final String KIND = "VehicleInfo";
	
	public static final String KEY_MODEL_NAME = "model_name";
	public static final String KEY_YEAR_TYPE = "year_type";
	public static final String KEY_VIN = "vin";
	public static final String KEY_PLATE = "plate";
	public static final String KEY_ORDER = "order";
	
	@SerializedName("encoded_key") public String mEncodedKey;
	@SerializedName("model_name")  public String mModelName;
	@SerializedName("year_type")   public String mYeartype;
	@SerializedName("vin")         public String mVin;
	@SerializedName("plate")       public String mPlate;
	@SerializedName("order")       public int mOrder = 0;
	
	public static Vehicle getByKey(Key key) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(KIND, key);
		PreparedQuery pq = ds.prepare(q);
		
		try {
			return new Vehicle(pq.asSingleEntity());
		}catch(Exception e) {
		}
		
		return null;
	}
	
	public static Vehicle getByVin(String vin) {
		Query q = new Query(KIND);
		q.setFilter(new FilterPredicate(KEY_VIN, FilterOperator.EQUAL, vin));
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = ds.prepare(q);
		try {
			return new Vehicle(pq.asSingleEntity());
		}catch(Exception e) {
			Logger.getLogger(KIND).severe(e.getMessage());
		}
		
		return null;
	}
	
	public static boolean isOwner(Key userKey, String vin) {
		Query q = new Query(KIND, userKey);
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = ds.prepare(q);
		
		for(Entity e : pq.asIterable()) {
			if(vin.equals(e.getProperty(KEY_VIN))) {
				return true;
			}
		}
		
		return false;
	}
	
	public Vehicle() {
		
	}
	
	public Vehicle(Entity e) {
		mEncodedKey = KeyFactory.keyToString(e.getKey());
		mModelName = (String) e.getProperty(KEY_MODEL_NAME);
		mYeartype = (String) e.getProperty(KEY_YEAR_TYPE);
		mVin = (String) e.getProperty(KEY_VIN);
		mPlate = (String) e.getProperty(KEY_PLATE);
		try {
			mOrder = (int) (long) e.getProperty(KEY_ORDER);
		}catch(NullPointerException ee) {
		}
	}
	
	private Entity setEntity(Entity e) {
		e.setProperty(KEY_MODEL_NAME, mModelName);
		e.setProperty(KEY_YEAR_TYPE, mYeartype);
		e.setProperty(KEY_VIN, mVin);
		e.setProperty(KEY_PLATE, mPlate);
		e.setProperty(KEY_ORDER, mOrder);
		
		return e;		
	}
	
	public Entity getAsEntity(Key parent) {
		Entity e = new Entity(KIND, parent);
		return setEntity(e);
	}
	
	public Entity getAsUpdateEntity() {
		Entity e = new Entity(KeyFactory.stringToKey(mEncodedKey));
		return setEntity(e);
	}
			

}
