package kr.hyosang.cardiary.rest

import kr.hyosang.cardiary.data.model.json.VehicleListResponse
import kr.hyosang.cardiary.exception.CDBaseException
import kr.hyosang.cardiary.service.AuthService
import kr.hyosang.cardiary.service.VehicleService
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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