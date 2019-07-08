package com.trueelogistics.checkin.model



data class HistoryRootModel (
    var data : HistoryDataModel,
    var status : String? = null,
    var statusCodes : Int? = null
)