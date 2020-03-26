package kr.hyosang.cardiary.service

import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.KeyFactory
import kr.hyosang.cardiary.data.model.DriveLog
import kr.hyosang.cardiary.data.model.DriveLogData
import kr.hyosang.cardiary.data.model.json.DriveLogItem
import kr.hyosang.cardiary.data.model.json.RouteLogDetailResponse

class RouteLogService {
    fun getLogDetail(logKey: Key): RouteLogDetailResponse {
        val master = DriveLog.getByKey(logKey)

        if(master != null) {
            val list = DriveLogData.queryList(logKey).map { RouteLogPoint(it) }

            return RouteLogDetailResponse(
                KeyFactory.keyToString(logKey),
                master.mDeparture,
                master.mDestination,
                master.mDistance,
                list)
        }else {
            throw RuntimeException("Cannot found route information")
        }
    }
}


class RouteLogPoint {
    val timestamp: Long
    val longitude: Double
    val latitude: Double
    val altitude: Double
    val speed: Double

    constructor(legacy: DriveLogItem) {
        this.timestamp = legacy.mTimestamp
        this.longitude = legacy.mLongi
        this.latitude = legacy.mLati
        this.altitude = legacy.mAlti
        this.speed = legacy.mSpeed
    }
}