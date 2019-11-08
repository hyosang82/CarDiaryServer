package kr.hyosang.cardiary.util

import com.google.appengine.api.datastore.Key
import com.google.appengine.api.users.UserServiceFactory
import kr.hyosang.cardiary.data.model.MyUser

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
    }
}