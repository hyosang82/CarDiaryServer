package kr.hyosang.cardiary.manage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.hyosang.cardiary.data.model.Fuel;
import kr.hyosang.cardiary.data.model.Vehicle;
import kr.hyosang.cardiary.data.model.json.daum.KeySerializer;
import kr.hyosang.cardiary.util.Util;

public class FuelServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		String action = uri.substring("/Fuel/".length());

		String respJson = "";

		if ("AddRecord".equals(action)) {
			String vin = req.getHeader("VIN");
			if(Util.isEmpty(vin)) {
				vin = (String) req.getParameter("inputVehicle");
			}
			
			long odo = Util.parseLong((String) req.getParameter("inputOdo"), 0);
			long unitPrice = Util.parseLong((String) req.getParameter("inputPrice"), 0);
			long totalPrice = Util.parseLong((String) req.getParameter("inputTotalPrice"), 0);
			double vol = Util.parseDouble((String) req.getParameter("inputVolume"), 0);
			boolean full = "Y".equals((String)req.getParameter("inputIsFull"));
			long ts = Util.parseLong((String) req.getParameter("inputDate"), 0);
			String stn = (String) req.getParameter("inputStation");
			
			String result;
			
			if(Util.isEmpty(vin)) {
				resp.sendError(500, "VIN is null");
				return;
			}
			
			Vehicle v = Vehicle.getByVin(vin);
			if(v != null) {
				saveFuelRecord(v, odo, unitPrice, totalPrice, vol, full, stn, ts);
			
				calcEfficient(KeyFactory.stringToKey(v.mEncodedKey));
				
				result = "Success";
				
			}else {
				result = "Cannot find VIN : " + vin;
			}
			
			Map<String, String> res = new HashMap<String, String>();
			res.put("result", result);
			
			Gson gson = new Gson();
			respJson = gson.toJson(res);
		}else if("Record".equals(action)) {
			String vehicleKey = (String) req.getParameter("vehicle_key");
			String year = (String) req.getParameter("year");
			
			Key vKey = KeyFactory.stringToKey(vehicleKey);
			int yr = Util.parseInt(year, 0);
			
			respJson = getRecordList(vKey, yr); 
		}
		
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(respJson);
	}
	
	private String getRecordList(Key vehicleKey, int year) {
		List<Fuel> list = Fuel.getList(vehicleKey, year);
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Key.class, new KeySerializer()).create();
		return gson.toJson(list);
	}
	
	private void saveFuelRecord(Vehicle v, long odo, long unitPrice, long totalPrice, double volume, boolean isFull, String station, long timestamp) {
		Fuel f = new Fuel(timestamp, isFull, odo, station, totalPrice, unitPrice, volume);
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ds.put(f.asNewEntity(KeyFactory.stringToKey(v.mEncodedKey)));
	}
	
	public void calcEfficient(Key vehicleKey) {
		List<Fuel> list = Fuel.getAllList(vehicleKey);
		int size = list.size();
		Fuel first = list.get(size-1);
		long lastOdo = first.odo;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		if(first.accuEfficient != 0 || first.efficient != 0) {
			first.accuEfficient = first.efficient = 0;
			ds.put(first.asUpdateEntity());
		}
		
		long fullDist = 0;
		double fullGas = first.volume;
		double accuEfficient = 0;
		
		for(int i=size-2;i>=0;i--) {
			Fuel item = list.get(i);
			
			long dist = item.odo - lastOdo;
			double thisGas = item.volume;
			double thisEfficient = (double)dist / thisGas;
			
			if(item.isFull) {
				accuEfficient = (double)fullDist / fullGas;
			}else {
				accuEfficient = 0.0f;
			}
			
			fullDist += dist;
			fullGas += item.volume;
			
			if((item.accuEfficient != accuEfficient) || (item.efficient != thisEfficient)) {
				item.accuEfficient = (Double.isNaN(accuEfficient) ? 0 : accuEfficient);
				item.efficient = (Double.isNaN(thisEfficient) ? 0 : thisEfficient);
				
				ds.put(item.asUpdateEntity());
			}
			
			lastOdo = item.odo;
		}
	}
}
