package kr.hyosang.cardiary.data.model.json

import kr.hyosang.cardiary.data.model.DriveLog
import kr.hyosang.cardiary.service.RouteLogPoint

data class RouteLogListResponse(
    val year: String,
    val month: String
) {
    val routeList = ArrayList<DriveLog>()

}

data class RouteLogDetailResponse(
    val encodedKey: String,
    val departure: String,
    val destination: String,
    val distance: Double,
    val points: List<RouteLogPoint>
)
