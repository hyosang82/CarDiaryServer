package kr.hyosang.cardiary.data.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.repackaged.org.codehaus.jackson.JsonFactory;
import com.google.appengine.repackaged.org.codehaus.jackson.JsonGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.hyosang.cardiary.data.model.DriveLog;
import kr.hyosang.cardiary.data.model.MyUser;
import kr.hyosang.cardiary.data.model.Vehicle;
import kr.hyosang.cardiary.data.model.json.RestBase;
import kr.hyosang.cardiary.data.model.json.daum.KeySerializer;
import kr.hyosang.cardiary.util.AuthService;
import kr.hyosang.cardiary.util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestServlet extends HttpServlet {
    private static final String uriPrefix = "/rest";
    private static final Pattern ptnVehicleRoutesYearMonth = Pattern.compile("^\\/vehicle\\/(.*)\\/route\\/([0-9]+)\\/([0-9]+)$");
    private static final Pattern ptnVehicleRouteItem = Pattern.compile("^\\/vehicle\\/(.*)\\/route\\/([^\\/]+)$");

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object responseJson = new Object();

        try {
            String uri = req.getRequestURI().substring(uriPrefix.length());
            Key user = AuthService.userFromToken("12312312345"/*req.getHeader("X-Token")*/).getKey();

            if("/vehicle/list".equals(uri)) {
                //vehicle list
                Query q = new Query(kr.hyosang.cardiary.data.model.Vehicle.KIND, user);
                q.addSort(kr.hyosang.cardiary.data.model.Vehicle.KEY_ORDER, Query.SortDirection.ASCENDING);
                DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
                PreparedQuery pq = ds.prepare(q);

                List<Vehicle> list = new ArrayList<>();
                for(Entity e : pq.asIterable()) {
                    kr.hyosang.cardiary.data.model.Vehicle v = new Vehicle(e);

                    list.add(v);
                }

                responseJson = list;
            }else {
                Matcher m = ptnVehicleRoutesYearMonth.matcher(uri);
                if(m.matches()) {
                    String vkey = m.group(1);
                    int year = Integer.parseInt(m.group(2), 10);
                    int month = Integer.parseInt(m.group(3), 10);

                    long [] range = Util.getDateRange(m.group(2), m.group(3));

                    Query q = new Query(DriveLog.KIND, KeyFactory.stringToKey(vkey));

                    q.setFilter(Query.CompositeFilterOperator.and(
                            new Query.FilterPredicate(DriveLog.KEY_TIMESTAMP, Query.FilterOperator.GREATER_THAN_OR_EQUAL, range[0]),
                            new Query.FilterPredicate(DriveLog.KEY_TIMESTAMP, Query.FilterOperator.LESS_THAN_OR_EQUAL, range[1])
                    ));
                    q.addSort(DriveLog.KEY_TIMESTAMP, Query.SortDirection.ASCENDING);

                    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
                    PreparedQuery pq = ds.prepare(q);

                    List<DriveLog> logList = new ArrayList<DriveLog>();
                    for(Entity e : pq.asIterable()) {
                        logList.add(new DriveLog(e));
                    }

                    responseJson = logList;
                }else {
                    m = ptnVehicleRouteItem.matcher(uri);
                    if(m.matches()) {
                        responseJson = DriveLog.getByKey(KeyFactory.stringToKey(m.group(2)));
                    }
                }
            }
        }catch(Exception e) {
            responseJson = new RestBase(-1, e.getMessage());
        }

        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Type", "application/json; charset=utf-8");
        resp.getWriter().write(new ObjectMapper().writeValueAsString(responseJson));
    }

    /*
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if("/vehicles".equals(uri)) {
            //vehicle list
            responseJson = Vehicle.vehicleList(user);
        }else {
            Matcher m = ptnVehicleRoutesYearMonth.matcher(uri);
            if(m.matches()) {
                String vehicle = m.group(1);
                int year = Integer.parseInt(m.group(2), 10);
                int month  = Integer.parseInt(m.group(3), 10);

                resp.getWriter().println("Route list on year " + vehicle + ", year = " + year + ", month = " + month);

            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String action = uri.substring("/Vehicle/".length());

        String respJson = "";

        if("Reorder".equals(action)) {
            String keys = (String) req.getParameter("key_order");
            String [] keyArr = keys.split("\\^");
            reorder(keyArr);
        }

        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(respJson);
    }
    */

}
