package com.wx.pc_retrofit_okhttp.data

open class CommonData<T> {
    var data: T? = null
    var errorCode: Int? = 0
    var errorMsg: String? = null
}