package com.trueelogistics.checkin.interfaces

import com.trueelogistics.checkin.model.GenerateItemHubModel

interface OnClickItemCallback {
    fun onClickItem( dataModel: GenerateItemHubModel)
}