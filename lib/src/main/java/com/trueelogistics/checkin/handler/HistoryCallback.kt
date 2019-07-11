package com.trueelogistics.checkin.handler

import com.trueelogistics.checkin.model.HistoryInDataModel

interface HistoryCallback {
    fun historyGenerate( dataModel : ArrayList<HistoryInDataModel>)
}