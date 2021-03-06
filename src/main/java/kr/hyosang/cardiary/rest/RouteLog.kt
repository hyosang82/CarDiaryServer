package kr.hyosang.cardiary.rest

import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Query
import kr.hyosang.cardiary.data.model.DriveLog
import kr.hyosang.cardiary.data.model.json.RouteLogListResponse
import kr.hyosang.cardiary.exception.CDBaseException
import kr.hyosang.cardiary.exception.InvalidOwnershipException
import kr.hyosang.cardiary.exception.InvalidParameterValueException
import kr.hyosang.cardiary.service.AuthService
import kr.hyosang.cardiary.service.RouteLogService
import kr.hyosang.cardiary.util.Util
import java.util.regex.Pattern
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "routeLogList", urlPatterns = ["/routelog/list/*"])
class RouteLogList: RestBaseServlet() {
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        try {
            val user = AuthService.getCurrentUserWithRequest(req)

            with(Pattern.compile("^\\/(.*)\\/([0-9]+)\\/([0-9]+)$").matcher(req?.pathInfo)) {
                if (matches()) {
                    val vKey = group(1)
                    val year = group(2)
                    val month = group(3)

                    if (AuthService.checkVehicleOwnership(user.key!!, KeyFactory.stringToKey(vKey))) {
                        val range = Util.getDateRange(year, month)
                        val pq = DatastoreServiceFactory.getDatastoreService().prepare(
                            Query(DriveLog.KIND, KeyFactory.stringToKey(vKey)).apply {
                                setFilter(
                                    Query.CompositeFilterOperator.and(
                                        Query.FilterPredicate(
                                            DriveLog.KEY_TIMESTAMP,
                                            Query.FilterOperator.GREATER_THAN_OR_EQUAL,
                                            range[0]
                                        ),
                                        Query.FilterPredicate(
                                            DriveLog.KEY_TIMESTAMP,
                                            Query.FilterOperator.LESS_THAN_OR_EQUAL,
                                            range[1]
                                        )
                                    )
                                )
                                addSort(DriveLog.KEY_TIMESTAMP, Query.SortDirection.ASCENDING)
                            }
                        )

                        val result = RouteLogListResponse(0,0)//(year, month)

                        for (e: Entity in pq.asIterable()) {
                            result.routeList.add(DriveLog(e))
                        }

                        sendResponse(resp, result)
                    } else {
                        throw InvalidOwnershipException("Not user vehicle")
                    }
                } else {
                    send404(resp)
                }
            }
        }catch(e: Exception) {
            sendError(resp, e)
        }
    }
}

@WebServlet(name = "routeLogDetail", urlPatterns = ["/routelog/detail/*"])
class RouteLogDetail: RestBaseServlet() {
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        try {
            AuthService.getCurrentUserWithRequest(req)?.let { user ->
                with(Pattern.compile("^\\/(.*)$").matcher(req?.pathInfo)) {
                    if (matches()) {
                        val routeKey = group(1)

                        val rEntity =
                            DatastoreServiceFactory.getDatastoreService().get(KeyFactory.stringToKey(routeKey))
                        val vKey = rEntity.parent

                        if (AuthService.checkVehicleOwnership(user.key!!, vKey)) {
                            sendResponse(resp, RouteLogService().getLogDetail(rEntity.key))
                        } else {
                            throw InvalidOwnershipException("Not your route log data")
                        }
                    } else {
                        send404(resp)
                    }
                }
            }
        }catch(e: Exception) {
            sendError(resp, e)
        }
    }
}