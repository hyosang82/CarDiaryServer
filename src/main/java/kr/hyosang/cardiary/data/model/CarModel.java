package kr.hyosang.cardiary.data.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class CarModel {
	public static final String KIND = "CarModel";
	
	public static final String KEY_TYPE = "entity_type";
	public static final String KEY_LABEL = "label";
	
	public static final String DEF_MANUFATURE = "MNF";
	public static final String DEF_MODEL = "MDL";
	public static final String DEF_SUBMODEL = "SMDL";
	public static final String DEF_YEAR = "YER";
	public static final String DEF_GRADE = "GRD";
	
	private Entity mEntity = new Entity(KIND);
	
	public CarModel(String type, String label) {
		mEntity = new Entity(KIND);
		mEntity.setProperty(KEY_TYPE, type);
		mEntity.setProperty(KEY_LABEL, label);
	}
	
	public CarModel(String type, String label, Key ancestor) {
		mEntity = new Entity(KIND, ancestor);
		mEntity.setProperty(KEY_TYPE, type);
		mEntity.setProperty(KEY_LABEL, label);
	}
	
	public static CarModel createManufactureEntity(String mnf) {
		return new CarModel(DEF_MANUFATURE, mnf);	
	}
	
	public static CarModel createModelEntity(String mdl, Key ancestor) {
		return new CarModel(DEF_MODEL, mdl, ancestor);
	}
	
	public static CarModel createSubModelEntity(String smdl, Key ancestor) {
		return new CarModel(DEF_SUBMODEL, smdl, ancestor);
	}
	
	public static CarModel createYearEntity(String yr, Key ancestor) {
		return new CarModel(DEF_YEAR, yr, ancestor);
	}
	
	public static CarModel createGradeEntity(String gd, Key ancestor) {
		return new CarModel(DEF_GRADE, gd, ancestor);
	}
	
	public Entity getEntity() {
		return mEntity;
	}
	
	public String getKeyString() {
		return KeyFactory.keyToString(mEntity.getKey());
	}

}
