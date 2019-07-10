package com.trueelogistics.checkin.model

data class HubDataModel(
    var data : ArrayList<HubInDataModel>,
    var total : Int? = null,
    var limit : Int? = null,
    var page : Int? = null,
    var hasNext : Boolean = false
)