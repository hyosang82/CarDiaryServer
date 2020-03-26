package kr.hyosang.cardiary.rest;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import kr.hyosang.cardiary.Const;
import kr.hyosang.cardiary.data.model.MyUser;
import kr.hyosang.cardiary.exception.UnknownAuthorizationHeaderException;

import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;


@WebServlet(name = "restwrapper", description = "RestWrapper", urlPatterns = "/rest/*")
public class RestWrapper extends HttpServlet {
    private static final Logger log = Logger.getLogger(RestWrapper.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


    }

    private void getUser(HttpServletRequest request) throws Exception {
        String authHeader = request.getHeader(Const.HTTP_HEADER_AUTH);
        if(authHeader != null && authHeader.length() > 0) {
            if(authHeader.startsWith("Bearer ")) {
                String auth = authHeader.substring(7);
            }else {
                log.warning("Unknown authorization header: " + authHeader);
                throw new UnknownAuthorizationHeaderException(authHeader);
            }
        }else {
            //GAE 자체 인증수단 확인 (웹페이지의 경우)
            UserService us = UserServiceFactory.getUserService();
            User user = us.getCurrentUser();

            if(MyUser.getUser(user.getEmail()) == null) {
                MyUser mu = new MyUser(user);

                DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
                Entity e = new Entity(MyUser.KIND);

                e = mu.intoEntity(e);

                ds.put(e);
            }

        }
    }
}
