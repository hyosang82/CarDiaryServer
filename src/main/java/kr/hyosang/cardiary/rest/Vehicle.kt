package kr.hyosang.cardiary.rest

import kr.hyosang.cardiary.data.model.CDUser
import kr.hyosang.cardiary.data.model.json.VehicleListResponse
import kr.hyosang.cardiary.exception.CDBaseException
import kr.hyosang.cardiary.service.AuthService
import kr.hyosang.cardiary.service.VehicleService
import java.util.logging.LogManager
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "vehicleMapper", urlPatterns = ["/vehicles/*"])
class VehicleMapper: RestBaseServlet() {
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        try {
            val user = AuthService.getCurrentUserWithRequest(req)
            req?.requestURI?.let { uri ->
                if(uri.startsWith("/vehicles")) {
                    val path1 = uri.substring(9)

                    if(path1.isEmpty()) {
                        //Vehicle list
                        sendResponse(resp, getVehicleList(user))
                    }
                }
            }
        }catch(e: Exception) {
            sendError(resp, e)
        }
    }

    private fun getVehicleList(user: CDUser) : VehicleListResponse {
        val res = VehicleListResponse()
        VehicleService.listByUser(user.key!!).forEach { v ->
            res.addVehicleFromModel(v)
        }

        return res
    }
}

@WebServlet(name = "vehicleList", urlPatterns = ["/vehicle/list"])
class VehicleList: RestBaseServlet() {
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        try {
            val user = AuthService.getCurrentUserWithRequest(req)

            val res = VehicleListResponse()
            VehicleService.listByUser(user.key!!).forEach { v ->
                res.addVehicleFromModel(v)
            }

            sendResponse(resp, res)
        }catch(e: Exception) {
            sendError(resp, e)
        }
    }
}