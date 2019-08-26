package com.trueelogistics.checkin.interfaces

import android.view.View
import com.trueelogistics.checkin.model.NearByHubModel

interface OnClickItemCallback {
    fun onClickItem(view: View, dataModel: NearByHubModel)
}