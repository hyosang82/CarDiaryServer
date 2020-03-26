package kr.hyosang.cardiary.service

import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.Query
import kr.hyosang.cardiary.data.model.Vehicle

class VehicleService {
    companion object {
        fun listByUser(userKey: Key): List<Vehicle> {
            val query = Query(Vehicle.KIND, userKey).apply {
                addSort(Vehicle.KEY_ORDER, Query.SortDirection.ASCENDING)
            }

            val result = ArrayList<Vehicle>()
            val pq = DatastoreServiceFactory.getDatastoreService().prepare(query)
            for(e: Entity in pq.asIterable()) {
                result.add(Vehicle(e))
            }

            return result
        }
    }
}