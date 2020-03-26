package kr.hyosang.cardiary.data.model

import com.google.appengine.api.datastore.*

class CDUser {
    val key: Key?
    val email: String

    companion object {
        const val KIND = "MyUser"

        const val KEY_USER_ID = "user_id"

        fun getUserByEmail(email: String): CDUser? {
            val entity = getUserEntity(email)
            return if(entity != null) {
                return CDUser(entity)
            }else {
                null
            }
        }

        fun getKeyByEmail(email: String): Key? {
            return getUserEntity(email)?.key
        }

        fun getUserByKey(key: Key): CDUser? {
            val e = DatastoreServiceFactory.getDatastoreService().get(key)
            return if(e != null) CDUser(e) else null
        }

        private fun getUserEntity(email: String): Entity? {
            val query = Query(KIND)
            query.setFilter(Query.FilterPredicate(KEY_USER_ID, Query.FilterOperator.EQUAL, email))

            return DatastoreServiceFactory.getDatastoreService().prepare(query).run {
                if(countEntities(FetchOptions.Builder.withDefaults()) == 1) {
                    asSingleEntity()
                }else {
                    null
                }
            }
        }
    }

    constructor(e: Entity) {
        this.key = e.key
        this.email = e.getProperty(KEY_USER_ID) as String
    }
}