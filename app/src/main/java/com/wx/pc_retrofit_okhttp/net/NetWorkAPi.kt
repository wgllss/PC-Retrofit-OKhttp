package com.wx.pc_retrofit_okhttp.net

import com.wx.pc_retrofit_okhttp.data.CommonData
import com.wx.pc_retrofit_okhttp.data.HomeData
import com.wx.pc_retrofit_okhttp.data.WanAndroidHome
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

interface NetWorkAPi {

    @POST("https://www.wanandroid.com/user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ): String

    @GET("article/list/0/json")
    suspend fun getHomeList(): String

    @GET("article/list/0/json")
    suspend fun getHomeList22(): HomeData
}