package com.trueelogistics.checkin.model

data class HistoryDataModel(
    var data: ArrayList<HistoryInDataModel>,
    var total: Int? = null,
    var limit: Int? = null,
    var page: Int? = null,
    var hasNext: Boolean = false
)