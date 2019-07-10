package com.trueelogistics.checkin.model

data class HubRootModel (
    var data : HubDataModel,
    var status : String? = null,
    var statusCodes : Int? = null
)