package kr.hyosang.cardiary.data.model

import com.google.appengine.api.datastore.*

data class Token(val userKey: String,
                 val token: String) {
    companion object {
        const val KIND = "Token"

        const val KEY_TOKEN = "token"

        fun getToken(key: Key): Token {
            return DatastoreServiceFactory.getDatastoreService().run {
                fromEntity(get(key))
            }
        }

        private fun fromEntity(entity: Entity): Token {
            return Token(KeyFactory.keyToString(entity.key.parent),
                entity.getProperty(KEY_TOKEN) as String
            )
        }
    }

    fun asEntity(): Entity {
        return Entity(KIND, KeyFactory.stringToKey(userKey)).apply {
            setProperty(KEY_TOKEN, token)
        }
    }
}

data class CDToken(val userKey: String,
                 val token: String) {
    companion object {
        const val KIND = "CDToken"

        const val KEY_TOKEN = "token"
        const val KEY_USER_AGENT = "user_agent"

        fun fromEntity(entity: Entity): CDToken {
            return CDToken(
                KeyFactory.keyToString(entity.key.parent),
                entity.getProperty(KEY_USER_AGENT) as String
            ).apply {
                userAgent = entity.getProperty(KEY_USER_AGENT) as String
            }
        }

        fun findToken(token: String): CDToken? {
            val query = Query(KIND).apply {
                setFilter(Query.FilterPredicate(KEY_TOKEN, Query.FilterOperator.EQUAL, token))
            }

            val entity = DatastoreServiceFactory.getDatastoreService().prepare(query).asSingleEntity()

            return if(entity != null) fromEntity(entity) else null
        }
    }

    var userAgent: String = ""

    fun asEntity(): Entity {
        return Entity(KIND, KeyFactory.stringToKey(userKey)).apply {
            setProperty(KEY_TOKEN, token)
            setProperty(KEY_USER_AGENT, userAgent)
        }
    }
}