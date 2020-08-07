package kr.hyosang.cardiary.rest

import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.users.UserService
import kr.hyosang.cardiary.data.model.DriveLog
import kr.hyosang.cardiary.data.model.json.RouteLogListResponse
import kr.hyosang.cardiary.exception.InvalidOwnershipException
import kr.hyosang.cardiary.exception.InvalidParameterValueException
import kr.hyosang.cardiary.service.AuthService
import kr.hyosang.cardiary.service.RouteLogService
import kr.hyosang.cardiary.util.Util
import java.util.regex.Pattern
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "routeMapper", urlPatterns = ["/routes/*"])
class RouteMapper: RestBaseServlet() {
    private val PtrnMonthList = Pattern.compile("^\\/(.*?)\\/([0-9]+)\\/([0-9]+)$")

    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        try {
            val user = AuthService.getCurrentUserWithRequest(req)

            req?.requestURI?.let { uri ->
                if(uri.startsWith("/routes")) {
                    val path1 = uri.substring(7)
                    if(path1.startsWith("/vehicle")) {
                        val path2 = path1.substring(8)

                        with(PtrnMonthList.matcher(path2)) {
                            if(matches()) {
                                val vKey = group(1)
                                val year = group(2)
                                val month = group(3)

                                val y = Integer.parseInt(year, 10)
                                val m = Integer.parseInt(month, 10)

                                if (AuthService.checkVehicleOwnership(user.key!!, KeyFactory.stringToKey(vKey))) {
                                    sendResponse(resp, getRouteList(vKey, y, m))
                                } else {
                                    throw InvalidOwnershipException("Not user vehicle")
                                }
                            }else {
                                throw InvalidParameterValueException("REST URL Invalid", path2);
                            }
                        }
                    }else {
                        val routeId = path1.substring(1)
                        val routeKey = KeyFactory.stringToKey(routeId)
                        if(AuthService.checkRouteOwnership(user.key!!, routeKey)) {
                            val rEntity = DatastoreServiceFactory.getDatastoreService().get(routeKey)

                            sendResponse(resp, RouteLogService().getLogDetail(rEntity.key))
                        }else {
                            throw InvalidOwnershipException("Not your route log data")
                        }
                    }
                }
            }
        }catch(e: Exception) {
            sendError(resp, e)
        }
    }

    private fun getRouteList(vehicleKey: String, year: Int, month: Int) : RouteLogListResponse {
        val range = Util.getDateRange(year, month)
        val pq = DatastoreServiceFactory.getDatastoreService().prepare(
            Query(DriveLog.KIND, KeyFactory.stringToKey(vehicleKey)).apply {
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

        val result = RouteLogListResponse(year, month)

        for (e: Entity in pq.asIterable()) {
            result.routeList.add(DriveLog(e))
        }

        return result
    }
}