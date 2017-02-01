package kr.hyosang.cardiary.data.model.json;

import com.google.gson.annotations.SerializedName;

public class DriveLogItem {
	@SerializedName("timestamp") public long mTimestamp;
	@SerializedName("longitude") public double mLongi;
	@SerializedName("latitude")  public double mLati;
	@SerializedName("altitude")  public double mAlti;
	@SerializedName("speed")     public double mSpeed;
	
	public DriveLogItem(long ts, double lng, double lat, double alt, double spd) {
		mTimestamp = ts;
		mLongi = lng;
		mLati = lat;
		mAlti = alt;
		mSpeed = spd;
	}
}
