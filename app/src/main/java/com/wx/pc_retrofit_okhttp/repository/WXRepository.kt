package com.wx.pc_retrofit_okhttp.repository

import com.wx.pc_retrofit_okhttp.net.NetWorkAPi
import com.wx.pc_retrofit_okhttp.net.retrofit.RetrofitUtils
import com.wx.pc_retrofit_okhttp.parseErrorString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking

class WXRepository private constructor() {

    companion object {
        val instance by lazy { WXRepository() }
    }

    private val baseUrl = "https://www.wanandroid.com/"
    private val api = RetrofitUtils.getInstance(baseUrl).create(NetWorkAPi::class.java)

    fun getHomeList() {
        runBlocking {
            // 主协程下面执行 flow 异步请求
            flow {
                emit(api.getHomeList())
            }.flowOn(Dispatchers.IO).catch {
                println(it.parseErrorString()) // 打印异常信息
            }.collect {
                println("网络返回结果:$it") // 打印异常信息
            }
            delay(10000) // 让程序主进程 在打印前不结束
        }
    }

    fun getHomeList22() {
        runBlocking {
            // 主协程下面执行 flow 异步请求
            flow {
                emit(api.getHomeList22(1))
            }.flowOn(Dispatchers.IO).catch {
                println(it)
                println(it.parseErrorString()) // 打印异常信息
            }.collect {
                it.data?.datas?.forEachIndexed { i, it ->
                    println("第$i 条 : ${it.title}") // 打印异常信息
                }

            }
            delay(10000) // 让程序主进程 在打印前不结束
        }
    }
}