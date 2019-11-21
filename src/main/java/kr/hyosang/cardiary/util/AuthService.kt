package kr.hyosang.cardiary.util

import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.users.UserServiceFactory
import kr.hyosang.cardiary.data.model.MyUser
import java.util.*

class AuthService {
    companion object {
        @JvmStatic
        fun userFromSession(): Key {
            val us = UserServiceFactory.getUserService()
            if(us.isUserLoggedIn) {
                val key = MyUser.getUserKey(us.currentUser.email)
                if(key != null) {
                    return key
                }

                throw RuntimeException("Not joined")
            }

            throw RuntimeException("Not logged in.")
        }

        @JvmStatic
        fun userFromToken(token: String): Entity {
            return Optional.of(MyUser.getUserKeyByToken(token)).orElseThrow { RuntimeException("No user found!!!") }
        }
    }
}