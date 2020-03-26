package kr.hyosang.cardiary.rest

import com.fasterxml.jackson.databind.ObjectMapper
import kr.hyosang.cardiary.exception.CDBaseException
import kr.hyosang.cardiary.exception.MandatoryParameterOmittedException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

open class RestBaseServlet: HttpServlet() {
    private val mapper = ObjectMapper()

    fun sendResponse(resp: HttpServletResponse?, obj: Any?) {
        resp?.addHeader("Content-Type", "application/json")
        resp?.writer?.write(mapper.writeValueAsString(obj))
    }

    fun sendError(resp: HttpServletResponse?, ex: Exception) {
        if(ex is CDBaseException) {
            sendError(resp, ex as CDBaseException)
        }else {
            sendError(resp, CDBaseException(ex))
        }
    }

    fun sendError(resp: HttpServletResponse?, ex: CDBaseException) {
        resp?.addHeader("Content-Type", "application/json")
        resp?.status = 500
        resp?.writer?.write(mapper.writeValueAsString(ex.toObject()))
    }

    fun send404(resp: HttpServletResponse?) {
        resp?.status = 404
    }

    fun getMandatoryParams(req: HttpServletRequest?, vararg params:String): Map<String, String> {
        val result = HashMap<String, String>()
        for(p in params) {
            val v = req?.getParameter(p)
            if(v != null && v.isNotEmpty()) {
                result[p] = v
            }else {
                throw MandatoryParameterOmittedException(p)
            }
        }

        return result
    }
}