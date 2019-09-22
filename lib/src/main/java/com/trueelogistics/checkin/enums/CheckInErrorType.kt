package com.trueelogistics.checkin.enums

enum class CheckInErrorType(val message: String, val code: Int) {
    DATA_EMPTY_ERROR("Data empty", 10000),
    RESULT_INVALID_ERROR("Result not invalid", 10001),
    PERMISSION_DENIED_ERROR("Permission Denied", 10002),
}