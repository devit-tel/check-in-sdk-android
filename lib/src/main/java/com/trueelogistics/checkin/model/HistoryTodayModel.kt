package com.trueelogistics.checkin.model

data class HistoryTodayModel (
    var data : ArrayList<HistoryInDataModel>,
    var status : String? = null,
    var statusCodes : Int? = null
)