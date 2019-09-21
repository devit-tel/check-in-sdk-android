package com.trueelogistics.checkin.model

data class ProfileRootModel(
    var data: ProfileDataModel,
    var status: String? = null,
    var statusCodes: Int? = null
)