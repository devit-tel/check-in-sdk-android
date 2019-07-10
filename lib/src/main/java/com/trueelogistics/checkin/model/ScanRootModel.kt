package com.trueelogistics.checkin.model

data class ScanRootModel  (
    var data : ScanDataModel,
    var status : String? = null,
    var statusCodes : Int? = null
)