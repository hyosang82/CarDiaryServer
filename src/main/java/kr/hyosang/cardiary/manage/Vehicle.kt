package kr.hyosang.cardiary.manage

import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.Query
import kr.hyosang.cardiary.data.model.Vehicle

class Vehicle {
    companion object {
        @JvmStatic
        fun vehicleList(user: Key): List<Vehicle> {
            val q = Query(Vehicle.KIND, user)
            q.addSort(Vehicle.KEY_ORDER, Query.SortDirection.ASCENDING)
            val ds = DatastoreServiceFactory.getDatastoreService()
            val pq = ds.prepare(q)

            val list = ArrayList<Vehicle>()
            for (e in pq.asIterable()) {
                list.add(Vehicle(e))
            }

            return list
        }
    }
}