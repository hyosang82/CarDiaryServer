package kr.hyosang.cardiary.service

import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.users.UserServiceFactory
import kr.hyosang.cardiary.data.model.CDToken
import kr.hyosang.cardiary.data.model.CDUser
import kr.hyosang.cardiary.data.model.MyUser
import kr.hyosang.cardiary.exception.InvalidAuthorizationException
import kr.hyosang.cardiary.exception.NotJoinedException
import kr.hyosang.cardiary.exception.NotLoggedInException
import java.util.*
import java.util.regex.Pattern
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest

class AuthService {
    companion object {
        fun getCurrentUser(): CDUser {
            val us = UserServiceFactory.getUserService()
            if(us.isUserLoggedIn) {
                return CDUser.getUserByEmail(us.currentUser.email) ?: throw NotJoinedException()
            }else {
                throw NotLoggedInException()
            }
        }

        fun getCurrentUserWithRequest(req: HttpServletRequest?): CDUser {
            val userKey = req?.getHeader("Authorization")?.let { auth ->
                val pattern = Pattern.compile("^Bearer (.*)$")
                with(pattern.matcher(auth)) {
                    if(matches()) group(1) else null
                }?.let { token ->
                    CDToken.findToken(token)
                }
            }?.userKey

            return if(userKey != null && userKey.isNotEmpty()) {
                return CDUser.getUserByKey(KeyFactory.stringToKey(userKey)) ?: throw InvalidAuthorizationException()
            }else {
                getCurrentUser()
            }
        }

        fun checkOwnership(userKey: Key, vehicleKey: Key): Boolean {
            return (vehicleKey.parent == userKey)
        }
    }
}