package kr.hyosang.cardiary.data.model.json;

import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gson.annotations.SerializedName;

import kr.hyosang.cardiary.data.model.MaintenancePart;
import kr.hyosang.cardiary.data.model.PartItem;

public class MaintenancePartJson {
	@SerializedName("sub_key")  public Key subKey;
	@SerializedName("part_key") public Key partKey;
	@SerializedName("name")     public String name;
	
	public MaintenancePartJson(MaintenancePart item, Map<String, PartItem> allMap) {
		PartItem info = allMap.get(item.itemKey);
		if(info != null) {
			name = info.name;
			partKey = info.myKey;
		}
		
		subKey = item.myKey;
	}

}
