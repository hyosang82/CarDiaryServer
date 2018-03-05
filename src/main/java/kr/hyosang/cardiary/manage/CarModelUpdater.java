package kr.hyosang.cardiary.manage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import kr.hyosang.cardiary.data.model.CarModel;

public class CarModelUpdater extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String modellist = req.getParameter("modellist");
		
		Pattern p = Pattern.compile("^(.*)\\^(.*)\\^(.*)\\^(.*)\\^(.*)$", Pattern.MULTILINE);
		Matcher m = p.matcher(modellist);
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Logger logger = Logger.getLogger("CarModelUpdater");
		
		int mnfCnt, modelCnt, dModelCnt, yearCnt, gradeCnt;
		mnfCnt = modelCnt = dModelCnt = yearCnt = gradeCnt = 0;		
		
		while(m.find()) {
			String mnf = m.group(1).trim();
			String model = m.group(2).trim();
			String detailedModel = m.group(3).trim();
			String yeartype = m.group(4).trim();
			String grade = m.group(5).trim();
			
			
			//제조사 입력
			Entity mnfEntity = queryEntity(CarModel.DEF_MANUFATURE, mnf, null);
			
			if(mnfEntity == null) {
				CarModel mdl = CarModel.createManufactureEntity(mnf);
				
				ds.put(mdl.getEntity());
				mnfEntity = mdl.getEntity();
				
				mnfCnt++;
				logger.log(Level.FINE, "제조사 추가 : " + mnf);
			}
			
			//모델 입력
			Entity mdlEntity = queryEntity(CarModel.DEF_MODEL, model, mnfEntity.getKey());
			if(mdlEntity == null) {
				CarModel mdl = CarModel.createModelEntity(model, mnfEntity.getKey());
				mdlEntity = mdl.getEntity();
				ds.put(mdlEntity);
				
				modelCnt++;
				logger.log(Level.FINE, "모델 추가 : " + model);
			}
			
			//상세모델 입력
			Entity subMdlEntity = queryEntity(CarModel.DEF_SUBMODEL, detailedModel, mdlEntity.getKey());
			if(subMdlEntity == null) {
				CarModel mdl = CarModel.createSubModelEntity(detailedModel, mdlEntity.getKey());
				subMdlEntity = mdl.getEntity();
				ds.put(subMdlEntity);
				
				dModelCnt++;
				logger.log(Level.FINE, "상세모델 추가 : " + detailedModel);
			}
			
			//연식 입력
			Entity yearEntity = queryEntity(CarModel.DEF_YEAR, yeartype, subMdlEntity.getKey());
			if(yearEntity == null) {
				CarModel mdl = CarModel.createYearEntity(yeartype, subMdlEntity.getKey());
				yearEntity = mdl.getEntity();
				ds.put(yearEntity);
				
				yearCnt++;
				logger.log(Level.FINE, "연식 입력 : " + yeartype);
			}
			
			//등급 입력
			Entity gradeEntity = queryEntity(CarModel.DEF_GRADE, grade, yearEntity.getKey());
			if(gradeEntity == null) {
				CarModel mdl = CarModel.createGradeEntity(grade, yearEntity.getKey());
				gradeEntity = mdl.getEntity();
				ds.put(gradeEntity);
				
				gradeCnt++;
				logger.log(Level.FINE, "등급 입력 : " + grade);
			}
		}
		
		String result = String.format("제조사 : %d건\n모델 : %d건\n세부모델 : %d건\n연식 : %d건\n등급 : %d건", mnfCnt, modelCnt, dModelCnt, yearCnt, gradeCnt);
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().println(result);
		
	}
	
	private Entity queryEntity(String type, String label, Key ancestor) {
		Filter typeFilter = new FilterPredicate(CarModel.KEY_TYPE, FilterOperator.EQUAL, type);
		Filter labelFilter = new FilterPredicate(CarModel.KEY_LABEL, FilterOperator.EQUAL, label);
		Filter filter = CompositeFilterOperator.and(typeFilter, labelFilter);
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q;
		if(ancestor == null) {
			q = new Query(CarModel.KIND).setFilter(filter);
		}else {
			q = new Query(CarModel.KIND, ancestor).setFilter(filter);
		}
		
		PreparedQuery pq = ds.prepare(q);
		
		for(Entity e : pq.asIterable()) {
			return e;
		}
		
		return null;
	}
}
