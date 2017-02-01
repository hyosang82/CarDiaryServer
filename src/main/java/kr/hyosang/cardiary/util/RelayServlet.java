package kr.hyosang.cardiary.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;


public class RelayServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		String action = uri.substring("/Relay/".length());
		
		Logger logger = Logger.getLogger("Servlet");
		
		String respJson = "";
		
		if("ReverseGeocode".equals(action)) {
			//reverse geocoding
			String type = (String) req.getParameter("type");
			double lat = Util.parseDouble((String) req.getParameter("lat"), 0);
			double lng = Util.parseDouble((String) req.getParameter("lng"), 0);
			
			String addr = "";
			if((lat != 0) && (lng != 0)) {
				addr = Util.reverseGeocode(lat, lng);
			}
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("type", type);
			map.put("result", addr);
			
			Gson gson = new Gson();
			
			respJson = gson.toJson(map);
		}
		
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(respJson);
	}
	
}
