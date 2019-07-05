package com.trueelogistics.checkin.model.list_hub

import com.trueelogistics.checkin.model.history.InDataModel

data class DataModel(
    var data : ArrayList<InDataModel>,
    var total : Int? = null,
    var limit : Int? = null,
    var page : Int? = null,
    var hasNext : Boolean = false
)