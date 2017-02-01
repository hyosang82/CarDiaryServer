package kr.hyosang.cardiary.manage;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import kr.hyosang.cardiary.data.model.MyUser;

public class SignIn extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UserService us = UserServiceFactory.getUserService();
		User user = us.getCurrentUser();
		
		if(MyUser.getUser(user.getEmail()) == null) {
			MyUser mu = new MyUser(user);
			
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			Entity e = new Entity(MyUser.KIND);
			
			e = mu.intoEntity(e);
			
			ds.put(e);
			
			resp.getWriter().print("OK");
		}else {
			resp.getWriter().print("Already signed up");
		}
	}

}
