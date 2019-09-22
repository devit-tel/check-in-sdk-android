package com.trueelogistics.checkin.enums

import com.trueelogistics.checkin.R

enum class CheckInTELType(val value: String, val res: Int) {
    CheckIn("CHECK_IN", R.string.full_checkin_text),
    CheckBetween("CHECK_IN_BETWEEN", R.string.full_check_between_text),
    CheckOut("CHECK_OUT", R.string.full_checkout_text),
    CheckOutOverTime("CHECK_OUT_OVER_TIME", R.string.full_checkout_text)
}