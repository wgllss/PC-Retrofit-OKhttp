package com.wx.pc_retrofit_okhttp.repository

import com.google.gson.Gson
import com.wx.pc_retrofit_okhttp.net.NetWorkAPi
import com.wx.pc_retrofit_okhttp.net.retrofit.RequestBodyWrapper
import com.wx.pc_retrofit_okhttp.net.retrofit.RetrofitUtils
import com.wx.pc_retrofit_okhttp.parseErrorString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import retrofit2.http.Field

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
            delay(10000) // 让java工程 程序主进程 在打印前不结束
        }
    }

    fun register(username: String, password: String, repassword: String) {
        runBlocking {
            flow {
                emit(api.register(username, password, repassword))
            }.flowOn(Dispatchers.IO).catch {
                println(it)
                println(it.parseErrorString()) // 打印异常信息
            }.collect {
                println(it)
            }
        }
    }

    /************************* 以下只 示例写法，接口调不通，因为找不到那么多 公开接口  全是 Retrofit的用法 来测试 *****************************************************/

    fun post1() {
        runBlocking {
            flow {
                val map = mutableMapOf<String, Any>()
                map["AAAAAA"] = "AAAAAA"
                map["BBBBBB"] = 1
                map["CCCCCC"] = true
                map["DDDDDD"] = 30.00f
                emit(api.post1(RequestBodyWrapper(Gson().toJson(map))))
            }.flowOn(Dispatchers.IO).catch {
                println(it)
                println(it.parseErrorString()) // 打印异常信息
            }.collect {
                println(it)
            }
        }
    }


    fun post12() {
        runBlocking {
            flow {
                val map = mutableMapOf<String, Any>()
                map["AAAAAA"] = "AAAAAA"
                map["BBBBBB"] = 1
                map["CCCCCC"] = true
                map["DDDDDD"] = 30.00f

                val headerMap = mapOf(
                    "Cookie" to "uab_collina=171755326751237298573402; tfstk=fFqEGyx3iMIEHdYy8fir_5GQozgKVDffUuGSE82odXcnd0awZ5V9FXiIAPlzs5lhp6CLa3Van4c7V09LWSwDAk_KV3-K20ffGisXpJn-qHga3LOpSxMhVYcnqYuGorffGis_pJn-qsa52bcgOYhMKYc3Zh0i6Y8oqX0ksFDmsbmoq7xGjxM2xU0oKdc9M402bY3h9NU_OKjk2plbKf-2BPkMqjsxT3Y7bvRjZJYDq3qZL2ESMIWRh2auHqEQQg-tA-zijYrRnUlruzqIjkfGmV3uz7DTW_8rgz2QkAqcFEh_bXNuYVXX04igcqm35NCIPDkoxqUOeeGur-rqAW19qXa4I5ubfCtjmrFa4qmc4XO-ISE82yRkr2xSQj6NI96RN__qDt2vyU3B6AlfK9YkrIKXVer5pUL-RaDZG9XA.; uag=7aed8d4dcb46b3eaa0e85f86b21df22e; ylogin=4024975; folder_id_c=10376791; phpdisk_info=WGhSYARkBz8BOgRjDmEEV1QwAAsAaAVlAzcDYw8xCjlTZFdjUjcDPFJiUAkMYFU9VD0NP109BWIPbgQxATMKPVg5UjUEMgdtAWAENQ5jBG5UZgA6AGoFawM4A2oPPwptU2FXYlJiAzxSZ1BhDF9VPlQzDThdNAVqDz0EZwExCj9Yb1Ji; PHPSESSID=pbba5e4111h3fk7om64mdgf4sbd0c6lm; __tins__21412745=%7B%22sid%22%3A%201717724688543%2C%20%22vd%22%3A%2012%2C%20%22expires%22%3A%201717727331949%7D; __51cke__=; __51laig__=12",
                    "Host" to "up.SSSSSwxxxoozooo.com",
                    "Origin" to "http://AAAAAAAAup.wxssosdsozosdoo.com",
                    "Referer" to "http://AAAAAAAup.wodsdsdsdsdosddszooo.com/mydisk.php?item=files&action=index&u=4024975"
                )

                emit(api.post12(RequestBodyWrapper(Gson().toJson(map)), headerMap))
            }.flowOn(Dispatchers.IO).catch {
                println(it)
                println(it.parseErrorString()) // 打印异常信息
            }.collect {
                println(it)
            }
        }
    }
}