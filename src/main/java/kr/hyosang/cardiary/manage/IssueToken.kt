package kr.hyosang.cardiary.manage

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.appengine.api.datastore.KeyFactory
import kr.hyosang.cardiary.data.model.CDUser
import kr.hyosang.cardiary.data.model.MyUser
import kr.hyosang.cardiary.data.model.Token
import kr.hyosang.cardiary.data.model.json.IssueTokenResponse
import kr.hyosang.cardiary.exception.BaseException
import kr.hyosang.cardiary.exception.CDBaseException
import kr.hyosang.cardiary.exception.InvalidOwnershipException
import kr.hyosang.cardiary.exception.InvalidParameterValueException
import kr.hyosang.cardiary.rest.RestBaseServlet
import kr.hyosang.cardiary.service.AuthService
import kr.hyosang.cardiary.service.TokenService
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "issueToken", urlPatterns = ["/token/issue"])
class IssueToken: RestBaseServlet() {
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        try {
            val user = AuthService.getCurrentUser()
            val token = TokenService().getToken(KeyFactory.keyToString(user.key))

            val res = IssueTokenResponse(user.email, token.token)

            sendResponse(resp, res)
        }catch(e: Exception) {
            sendError(resp, e)
        }
    }
}
@WebServlet(name = "registerToken", urlPatterns = ["/token/register"])
class RegisterToken: RestBaseServlet() {
    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        try {
            val ts = TokenService()
            val p = getMandatoryParams(req, "email", "token")
            val tokenKey = ts.findToken(p["token"] ?: "") ?: throw InvalidParameterValueException("token", p["token"])
            val user =
                CDUser.getUserByEmail(p["email"] ?: "") ?: throw InvalidParameterValueException("email", p["email"])

            val tu = CDUser.getUserByKey(tokenKey.parent)

            if (tu?.email == user.email) {
                //OK
                ts.makePermanent(tokenKey, req?.getHeader("User-Agent") ?: "")
            } else {
                throw InvalidOwnershipException("User ${p["email"]} is not owner of token=${p["token"]}")
            }
        }catch(e: Exception) {
            sendError(resp, e)
        }
    }
}