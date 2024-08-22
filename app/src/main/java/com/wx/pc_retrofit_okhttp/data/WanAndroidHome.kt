package com.wx.pc_retrofit_okhttp.data

data class WanAndroidHome(
    val datas: MutableList<HomeItemBean>,
    val curPage: Int,
    val size: Int,
    val total: Int
)