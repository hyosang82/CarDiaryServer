package kr.hyosang.cardiary.data.model.json.daum;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class Coord2Addr {
	public String type;
	public String code;
	public String name;
	public String fullName;
	public String regionId;
	public String name0;
	public String code1;
	public String name1;
	public String code2;
	public String name2;
	public String code3;
	public String name3;
	public double x;
	public double y;

	@JsonProperty("documents")
	private void unpackNested(List<Object> docs) {
		if(docs.size() > 0) {
			Map<String, Object> o = (Map<String, Object>)docs.get(0);
			Map<String, Object> addr = (Map<String, Object>) o.get("address");

			fullName = addr.get("region_1depth_name") + " " + addr.get("region_2depth_name") + " " + addr.get("region_3depth_name");
		}
	}

}
