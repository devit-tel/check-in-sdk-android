package com.trueelogistics.checkin.model.list_hub

data class RootModel (
    var data : DataModel,
    var status : String? = null,
    var statusCodes : Int? = null
)