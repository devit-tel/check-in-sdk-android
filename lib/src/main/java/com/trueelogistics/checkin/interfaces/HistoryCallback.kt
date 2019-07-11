package com.trueelogistics.checkin.interfaces

import com.trueelogistics.checkin.model.HistoryInDataModel

interface HistoryCallback {
    fun historyGenerate( dataModel : ArrayList<HistoryInDataModel>)
}