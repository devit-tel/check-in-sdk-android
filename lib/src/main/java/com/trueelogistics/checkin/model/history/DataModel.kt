package com.trueelogistics.checkin.model.history

data class DataModel(
    var data : ArrayList<InDataModel>,
    var total : Int? = null,
    var limit : Int? = null,
    var page : Int? = null,
    var hasNext : Boolean = false
)