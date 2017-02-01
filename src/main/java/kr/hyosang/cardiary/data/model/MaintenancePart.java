package kr.hyosang.cardiary.data.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.gson.annotations.SerializedName;

public class MaintenancePart {
	public static final String KIND = "MaintenancePart";
	
	public static final String KEY_ITEM_KEY = "item_key";
	
	@SerializedName("key")      public Key myKey;
	@SerializedName("item_key") public String itemKey;
	
	public MaintenancePart(Entity e) {
		this.myKey = e.getKey();
		this.itemKey = (String) e.getProperty(KEY_ITEM_KEY);
	}
	
	public MaintenancePart(String itemKey) {
		this.itemKey = itemKey;
	}
	
	public Entity asNewEntity(Key parent) {
		Entity e = new Entity(KIND, parent);
		e.setProperty(KEY_ITEM_KEY, itemKey);
		
		return e;
	}
		

}
