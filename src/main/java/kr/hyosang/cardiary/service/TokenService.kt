package kr.hyosang.cardiary.service

import com.google.appengine.api.datastore.*
import kr.hyosang.cardiary.data.model.CDToken
import kr.hyosang.cardiary.data.model.Token
import java.util.*

class TokenService {
    fun getToken(userKey: String): Token {
        val ds = DatastoreServiceFactory.getDatastoreService()
        val query = Query(Token.KIND, KeyFactory.stringToKey(userKey))

        val pq = ds.prepare(query)

        val entity = pq.asSingleEntity()

        return if(entity != null) {
            Token(userKey, entity.getProperty(Token.KEY_TOKEN) as String)
        }else {
            //create new
            val tokenStr = UUID.randomUUID().toString().replace("-", "")
            val token = Token(userKey, tokenStr)

            ds.put(token.asEntity())

            token
        }
    }

    fun findToken(token: String): Key? {
        val query = Query(Token.KIND)
        query.setFilter(Query.FilterPredicate(Token.KEY_TOKEN, Query.FilterOperator.EQUAL, token))

        return DatastoreServiceFactory.getDatastoreService().prepare(query)?.asSingleEntity()?.key
    }

    fun makePermanent(tokenKey: Key, ua: String) {
        DatastoreServiceFactory.getDatastoreService().run {
            val resToken = Token.getToken(tokenKey)
            val pemToken = CDToken(resToken.userKey, resToken.token)
            pemToken.userAgent = ua

            delete(tokenKey)

            put(pemToken.asEntity())
        }
    }
}