package kr.hyosang.cardiary.exception

import kr.hyosang.cardiary.Const

open class CDBaseException: Exception {
    val errorCode: Int
    constructor(code: Int, message: String): super(message) {
        this.errorCode = code
    }

    constructor(e: Throwable): super("Server error : ${e.message}") {
        this.errorCode = Const.ErrorCode.ERR_FROM_DEEP
    }

    fun toObject(): ErrorResponse {
        return ErrorResponse(errorCode, this.message ?: "")
    }
}

data class ErrorResponse(
    val errorCode: Int,
    val message: String
)

class NotLoggedInException: CDBaseException(Const.ErrorCode.ERR_NOT_LOGGED_IN, "Not logged in")
class NotJoinedException: CDBaseException(Const.ErrorCode.ERR_NOT_JOINED, "Not joined yet")
class InvalidAuthorizationException: CDBaseException(Const.ErrorCode.ERR_INVALID_AUTHORIZATION, "Invalid authorization information")
class MandatoryParameterOmittedException(paramName:String):
    CDBaseException(Const.ErrorCode.ERR_OMITTED_MANDATORY_PARAM, "Omitted mandatory parameter: $paramName")
class InvalidParameterValueException(paramName: String, value: String?):
        CDBaseException(Const.ErrorCode.ERR_INVALID_PARAMETER_VALUE, "Invalid parameter value $paramName=$value")
class InvalidOwnershipException(detailedMessage: String):
        CDBaseException(Const.ErrorCode.ERR_INVALID_OWNERSHIP, "Invalid ownership: $detailedMessage")