package com.trueelogistics.checkin.model

data class GenQrRootModel (
    var data : GenQrDataModel,
    var status : String? = null,
    var statusCodes : Int? = null
)