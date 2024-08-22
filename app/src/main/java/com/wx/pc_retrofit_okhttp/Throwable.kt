package com.wx.pc_retrofit_okhttp

import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

fun Throwable?.parseErrorString(): String = when (this) {
    is ConnectException, is SocketException -> {
        if (message?.contains("Network is unreachable") == true)
            "当前网络不可用"
        if (message?.contains("Failed to connect to") == true)
            "无法连接到服务器"
        else
            "网络错误"
    }
    is HttpException -> {
        if (message?.contains("HTTP 50") == true) {
            message!!.substring(0, 8)
        } else if (message?.contains("HTTP 40") == true) {
            message!!.substring(0, 8)
        } else if (message?.contains("HTTP 30") == true) {
            message!!.substring(0, 8)
        } else {
            "服务器异常"
        }
    }
    is InterruptedIOException -> {
        if (message?.contains("timeout") == true)
            "网络超时"
        else
            "网络错误"
    }
    is UnknownHostException -> "无网络连接"
    is JsonSyntaxException -> "数据错误,json解析错误"
    is SocketTimeoutException, is TimeoutException -> "网络超时"
    is IllegalArgumentException -> {
        if (message?.contains("baseUrl must end in ") == true)
            "baseUrl must end in"
        else message ?: "未知错误异常"
    }
//    is BusinessException -> {
//        message!!
//    }
    else -> "未知错误异常"
}