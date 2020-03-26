package kr.hyosang.cardiary.manage

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.appengine.api.datastore.KeyFactory
import kr.hyosang.cardiary.data.model.CDUser
import kr.hyosang.cardiary.data.model.MyUser
import kr.hyosang.cardiary.data.model.Token
import kr.hyosang.cardiary.data.model.json.IssueTokenResponse
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
        val user = AuthService.getCurrentUser()
        val token = TokenService().getToken(KeyFactory.keyToString(user.key))

        val res = IssueTokenResponse(user.email, token.token)

        sendResponse(resp, res)
    }
}
@WebServlet(name = "registerToken", urlPatterns = ["/token/register"])
class RegisterToken: RestBaseServlet() {
    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val ts = TokenService()
        val p = getMandatoryParams(req, "email", "token")
        val tokenKey = ts.findToken(p["token"] ?: "") ?: throw RuntimeException("Cannot found token ${p["token"]}")
        val user = CDUser.getUserByEmail(p["email"] ?: "") ?: throw RuntimeException("Cannot found user ${p["email"]}")

        val tu = CDUser.getUserByKey(tokenKey.parent)

        if(tu?.email == user.email) {
            //OK
            ts.makePermanent(tokenKey, req?.getHeader("User-Agent") ?: "")
        }else {
            throw RuntimeException("User ${p["email"]} is not owner of token=${p["token"]}")
        }
    }
}