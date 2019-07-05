package com.trueelogistics.checkin.model.history



data class RootModel (
    var data : DataModel,
    var status : String? = null,
    var statusCodes : Int? = null
)