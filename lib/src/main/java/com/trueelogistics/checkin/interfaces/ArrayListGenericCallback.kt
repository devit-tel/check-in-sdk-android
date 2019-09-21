package com.trueelogistics.checkin.interfaces

interface ArrayListGenericCallback<T> {
    fun onResponse(dataModel: ArrayList<T>? = null)
    fun onFailure(message: String? = "")
}