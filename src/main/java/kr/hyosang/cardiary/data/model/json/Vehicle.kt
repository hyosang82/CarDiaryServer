package kr.hyosang.cardiary.data.model.json

class VehicleListResponse {
    inner class Vehicle(
        val key: String,
        val name: String,
        val vin: String,
        val plate: String
    )

    val vehicles = ArrayList<Vehicle>()

    fun addVehicleFromModel(v: kr.hyosang.cardiary.data.model.Vehicle) {
        vehicles.add(
            Vehicle(
            v.mEncodedKey,
            v.mModelName,
            v.mVin,
            v.mPlate
            )
        )
    }
}