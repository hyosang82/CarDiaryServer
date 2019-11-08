package kr.hyosang.cardiary.data.rest;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import kr.hyosang.cardiary.data.model.MyUser;
import kr.hyosang.cardiary.manage.Vehicle;
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
    private static final Pattern ptnVehicleRoutesYearMonth = Pattern.compile("^\\/vehicle\\/(.*)\\/routes\\/([0-9]+)\\/([0-9]+)$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI().substring(5);
        Key user = AuthService.userFromSession();
        Object responseJson = new Object();

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

        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Type", "application/json; charset=utf-8");
        resp.getWriter().write((new Gson()).toJson(responseJson));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
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
         */

    }

}
