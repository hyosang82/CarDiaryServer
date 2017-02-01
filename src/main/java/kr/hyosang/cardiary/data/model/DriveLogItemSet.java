package kr.hyosang.cardiary.data.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import kr.hyosang.cardiary.data.model.json.DriveLogItem;
import kr.hyosang.cardiary.util.Util;

public class DriveLogItemSet {
	private Map<Long, DriveLogItem> filterMap = new TreeMap<Long, DriveLogItem>();
	
	public void addLogData(String logdata) {
		StringTokenizer st = new StringTokenizer(logdata, "$");
		double lat, lng, alt, spd;
		long ts = 0;
		
		while(st.hasMoreTokens()) {
			lat = lng = alt = spd = 0.0f;
			
			String item = st.nextToken();
			String [] elems = item.split("\\|");
			
			lat = Util.parseDouble(elems[0], 0);
			lng = Util.parseDouble(elems[1], 0);
			alt = Util.parseDouble(elems[2], 0);
			spd = Util.parseDouble(elems[3], 0);
			ts = Util.parseLong(elems[4], 0);
			
			if((lat == 0) || (lng == 0) || (ts == 0)) {
				continue;
			}
			
			filterMap.put(ts, new DriveLogItem(ts, lng, lat, alt, spd));
		}
	}
	
	public void addLogData(Collection<DriveLogItem> list) {
		for(DriveLogItem item : list) {
			filterMap.put(item.mTimestamp, item);
		}
	}
	
	public String asString() {
		StringBuffer sb = new StringBuffer();
		for(DriveLogItem item : filterMap.values()) {
			sb.append(String.valueOf(item.mLati)).append("|")
			.append(String.valueOf(item.mLongi)).append("|")
			.append(String.valueOf(item.mAlti)).append("|")
			.append(String.valueOf(item.mSpeed)).append("|")
			.append(String.valueOf(item.mTimestamp))
			.append("$");
		}
		
		return sb.toString();
	}
	
	public List<DriveLogItem> asList() {
		List<DriveLogItem> list = new ArrayList<DriveLogItem>();
		list.addAll(filterMap.values());
		
		return list;
	}

}
