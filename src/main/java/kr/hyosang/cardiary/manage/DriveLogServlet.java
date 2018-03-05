package kr.hyosang.cardiary.manage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.hyosang.cardiary.data.model.DriveLog;
import kr.hyosang.cardiary.data.model.DriveLogData;
import kr.hyosang.cardiary.data.model.DriveLogItemSet;
import kr.hyosang.cardiary.data.model.Vehicle;
import kr.hyosang.cardiary.data.model.json.DriveLogItem;
import kr.hyosang.cardiary.data.model.json.daum.KeySerializer;
import kr.hyosang.cardiary.util.Util;

public class DriveLogServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		String action = uri.substring("/DriveLog/".length());
		
		String respJson = "";
		
		if("LogList".equals(action)) {
			//로그 항목 리스트
			String vehicleKey = (String) req.getParameter("vehicle_key");
			String year = (String) req.getParameter("year");
			String month = (String) req.getParameter("month");
			
			respJson = getLogList(vehicleKey, year, month);
		}else if("LogDetail".equals(action)) {
			//로그 상세
			String key = (String) req.getParameter("log_key");
			respJson = getLogDetail(key);
		}else if("UpdateInfo".equals(action)) {
			//로그 시작지역/도착지역/거리 정보 업데이트
			String key = (String) req.getParameter("key");
			String dep = (String) req.getParameter("departure");
			String dest = (String) req.getParameter("destination");
			double dist = Util.parseDouble((String) req.getParameter("distance"), 0);
			
			DriveLog item = DriveLog.getByKey(KeyFactory.stringToKey(key));
			if(item != null) {
				if(!Util.isEmpty(dep)) {
					item.mDeparture = dep;
				}
				
				if(!Util.isEmpty(dest)) {
					item.mDestination = dest;
				}
				
				if(dist != 0) {
					item.mDistance = dist;
				}
				
				DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
				ds.put(item.asUpdateEntity());
			}
		}else if("UploadLog".equals(action)) {
			String vin = req.getHeader("VIN");
			String time_key = (String) req.getParameter("time_key");
			String log_data = (String) req.getParameter("log_data");
			String timestamp = (String) req.getParameter("timestamp");
			if(uploadLog(vin, Util.parseLong(time_key, 0), log_data)) {
				resp.setStatus(200);
			}else {
				resp.setStatus(406);
			}
		}else if("MergeWithPrevious".equals(action)) {
			String key = (String) req.getParameter("key");
			Key logKey = KeyFactory.stringToKey(key);
			
			respJson = mergeWithPrevious(logKey);
		}
		
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(respJson);
	}
	
	
	private String getLogList(String vehicleKey, String y, String m) {
		long [] range = Util.getDateRange(y, m);
		
		Query q = new Query(DriveLog.KIND, KeyFactory.stringToKey(vehicleKey));
		
		q.setFilter(CompositeFilterOperator.and(
				new FilterPredicate(DriveLog.KEY_TIMESTAMP, FilterOperator.GREATER_THAN_OR_EQUAL, range[0]),
				new FilterPredicate(DriveLog.KEY_TIMESTAMP, FilterOperator.LESS_THAN_OR_EQUAL, range[1])
				));
		q.addSort(DriveLog.KEY_TIMESTAMP, SortDirection.ASCENDING);
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = ds.prepare(q);
		
		List<DriveLog> logList = new ArrayList<DriveLog>();
		for(Entity e : pq.asIterable()) {
			logList.add(new DriveLog(e));
		}
		
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Key.class, new KeySerializer());
		
		return gson.create().toJson(logList);
	}
	
	public String getLogDetail(String key) {
		Key keyObj = KeyFactory.stringToKey(key);
		DriveLog master = DriveLog.getByKey(keyObj);
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(master != null) {
			List<DriveLogItem> list = DriveLogData.queryList(keyObj);
			
			returnMap.put("key", key);
			returnMap.put("departure", master.mDeparture);
			returnMap.put("destination", master.mDestination);
			returnMap.put("distance", master.mDistance);
			returnMap.put("points", list);
			
		}else {
			returnMap.put("error", "No such drive log key");
		}
	
		Gson gson = new Gson();
		return gson.toJson(returnMap);
	}
	
	public boolean uploadLog(String vin, long timeKey, String logData) {
		Logger logger = Logger.getLogger("Uploader");
		if(Util.isEmpty(vin)) {
			logger.severe("No VIN");
			return false;
		}
		
		if(timeKey == 0) {
			logger.severe("No Timestamp key");
			return false;
		}
		
		if(Util.isEmpty(logData)) {
			logger.severe("No log data");
			return false;
		}
		
		//차량 검색
		Vehicle v = Vehicle.getByVin(vin);
		if(v == null) {
			logger.severe("No VIN data : " + vin);
			return false;
		}
		
		//차량에 대해 로그 검색
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Key vehicleKey = KeyFactory.stringToKey(v.mEncodedKey);
		DriveLog logParent = DriveLog.query(vehicleKey, timeKey);
		if(logParent == null) {
			//새로 등록
			logParent = new DriveLog(timeKey);
			Entity e = logParent.asNewEntity(vehicleKey);
			ds.put(e);
			logParent = new DriveLog(e);
		}else {
			//이어서 등록
			logParent.mDestination = "";
			logParent.mDistance = 0;
			ds.put(logParent.asUpdateEntity());
		}
		
		//로그 데이터 저장
		DriveLogItemSet logSet = new DriveLogItemSet();
		Query q = new Query(DriveLogData.KIND, logParent.getKey());
		PreparedQuery pq = ds.prepare(q);
		for(Entity e : pq.asIterable()) {
			logSet.addLogData(((Text) e.getProperty(DriveLogData.KEY_LOGDATA)).getValue());
			
			ds.delete(e.getKey());
		}
		
		logSet.addLogData(logData);
		
		DriveLogData log = new DriveLogData();
		log.mLogdata = logSet.asString();
		
		ds.put(log.asNewEntity(logParent.getKey()));
		
		//역 지오코딩
		logParent.updateDepDest();
		
		return true;
	}
	
	private String mergeWithPrevious(Key logKey) {
		DriveLog logParent = DriveLog.getByKey(logKey);
		
		//병합 대상 로그항목 검색
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		long start = logParent.mTimestamp - (24 * 60 * 60 * 1000);	//전 24시간동안의 데이터 중
		Query q = new Query(DriveLog.KIND);
		q.setFilter(CompositeFilterOperator.and(
				new FilterPredicate(DriveLog.KEY_TIMESTAMP, FilterOperator.GREATER_THAN_OR_EQUAL, start),
				new FilterPredicate(DriveLog.KEY_TIMESTAMP, FilterOperator.LESS_THAN, logParent.mTimestamp)
				));
		q.addSort(DriveLog.KEY_TIMESTAMP, SortDirection.DESCENDING);
		
		PreparedQuery pq = ds.prepare(q);
		
		Map<String, String> jsonMap = new HashMap<String, String>();
		
		Iterator<Entity> iter = pq.asIterable().iterator();
		if(iter.hasNext()) {
			Entity targetParentEntity = iter.next();
			
			//삭제할 키들
			List<Key> deleteKeys = new ArrayList<Key>();
			
			//옮길 데이터 추출
			List<DriveLogItem> list = DriveLogData.queryList(logKey, deleteKeys);
			
			//타겟 데이터
			List<Key> targetKeys = new ArrayList<Key>();
			List<DriveLogItem> targetList = DriveLogData.queryList(targetParentEntity.getKey(), targetKeys);
			
			//병합
			DriveLogItemSet logSet = new DriveLogItemSet();
			logSet.addLogData(targetList);
			logSet.addLogData(list);
			
			//저장
			if(targetKeys.size() == 1) {
				//하나면 업데이트로 저장
				DriveLogData data = new DriveLogData();
				data.mLogdata = logSet.asString();
				Entity ee = data.asUpdateEntity(targetKeys.get(0));
				ds.put(ee);
			}else {
				//그 외에는 새로운 엔티티로 저장
				DriveLogData data = new DriveLogData();
				data.mLogdata = logSet.asString();
				Entity ee = data.asNewEntity(targetParentEntity.getKey());
				ds.put(ee);
				
				//기존 데이터는 삭제처리
				deleteKeys.addAll(targetKeys);
			}
			
			//삭제
			deleteKeys.add(logKey);
			ds.delete(deleteKeys);
			
			//기존 지오코딩 데이터 삭제
			DriveLog targetLog = new DriveLog(targetParentEntity);
			targetLog.mDestination = targetLog.mDeparture = "";
			targetLog.mDistance = 0;
			ds.put(targetLog.asUpdateEntity());
			
			jsonMap.put("newKey", KeyFactory.keyToString(targetParentEntity.getKey()));
		}
		
		Gson gson = new Gson();
		return gson.toJson(jsonMap);	
	}
}
