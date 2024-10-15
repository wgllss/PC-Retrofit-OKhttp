
![8cabc839ly4h121t5arodj20u00e2q46.jpg](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/894c7b7dc0184e35926e98c582d7610c~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgV2dsbHNz:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiMzU2NjYxODM1MDgyNTczIn0%3D&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725238065&x-orig-sign=72HDsk6HNkLS8%2BKX76iZMpMBclw%3D)
> 作为程序员，得学会写个工具方便自己
## 一、前言
>   本文主要目的只为了分享快速调试接口，自己搭建一套网络请求工具  

  作为主攻Android 系统客户端的开发，网络请求调试数据是必不可少的一部分：  
1.   怎么快速找调试接口？怎么快速找一个网络请求工具？
 很多人马上想到   
**`Retrofit + Okhttp `**
 早起还有 **`HttpURLConnection `** 和 **`Volley`**  
 **但是**  这大家常用的都是集成在Android程序里面，需要安装运行在android设备里面，大多数需要触发按钮点击 才能调用访问接口,我们大多数情况下是需要 先看到数据结构正常，符合要求了，才进行开始写网络接口部分，特别是在自己研究某些网络接口，而这时候又没有类似可以调试的文档的时候
 
2.  网络请求工具PC版上有好多工具。类似 **`Swagger`**  文档 , **`ApiFox`**  ,或者其他在线网页请求工具？为什么又不用  
 其实这个问题是，有类似网络调试接口文档是最好的，有时候没有的情况下或者说这些工具，大大小小都不是能完全满足某些需求，特别是 post的各种方式，或者put等不常用的请求方式
3.  那 **`Retrofit + Okhttp`** 解耦的这么好用，可以搭建起来在PC上直接运行吗？可以的，不用那么麻烦的要查看一个接口数据，需要写好运行安装到手机里面，再点击按钮，这个过程大概需要 一分钟左右，如果写好代码直接调用，随时可以调用，不是节省很多时间。
4.  这样做有什么好处？  
(1). 方便调试，方便快速查看接口数据，节省很多时间  
(2). 可以快速用PC写好的网络部分拷贝到Android工程项目里面用  
(3)、在没有调试接口文档，调用第三方Api或者研究其他接口时快速调用下接口后看内容   
(4). 网络抓包，找其他资源情况下，可以快速抓取到

## 二、搭建Java工程，采用Kotlin+协程+FLow+Retrofit+OkHttp
1. 打开Android Studio 建一个Android 工程
2. 修改该工程的build.gradle文件为Java 工程,并配置如下：
```
plugins {
    id 'java-library'
    id 'kotlin'
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.7.0"
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-scalars:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.3'
    implementation 'com.squareup.okhttp3:okhttp:4.9.3'
    implementation 'com.squareup.okio:okio:2.10.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

    implementation 'com.google.protobuf:protobuf-java:3.5.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2'
}
```
3. 搭建一个 **`RetrofitUtils `** 请求工具类，这都是常规操作了，如下
```

class RetrofitUtils private constructor(private val baseUrl: String) {

    companion object {
        var instance: RetrofitUtils? = null

        fun getInstance(baseUrl: String) = instance ?: synchronized(this) {
            instance ?: RetrofitUtils(baseUrl).also { instance = it }
        }
    }

    private inline val retrofit: Retrofit
        get() {
            val logging = HttpLoggingInterceptor()
            val timeout = 30000L
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HeaderInterceptor())
                .addInterceptor(logging)
//                .addInterceptor(RetrofitClient.BaseUrlInterceptor())
                .callTimeout(timeout, TimeUnit.MILLISECONDS)
                //设置连接超时
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                //设置从主机读信息超时
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                //设置写信息超时
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
                .build();
            return Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()
        }

    fun <T> create(service: Class<T>?): T {
        if (service == null) {
            throw RuntimeException("Api service is null!")
        }
        return retrofit.create(service)!!
    }
}
```

4. 建一个 **`NetWorkAPi`** 作为 **`Retrofit`** 请求接口：

 ```

interface NetWorkAPi {

    //示例post 请求
    @FormUrlEncoded
    @POST("https://www.wanandroid.com/user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ): String

    // 示例get 请求
    @GET("article/list/0/json")
    suspend fun getHomeList(): String

    // 示例get 请求2
    @GET("article/list/{path}/json")
    suspend fun getHomeList22(@Path("path") page: Int): HomeData

    /************************* 以下只 示例写法，接口调不通，因为找不到那么多 公开接口  全是 Retrofit的用法 来测试 *****************************************************/


//    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")  //todo 固定 header
    @POST("https://xxxxxxx")
    fun post1(@Body body: RequestBody): String

//    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("https://xxxxxxx22222")
    fun post12(@Body body: RequestBody, @HeaderMap map: Map<String, String>): String //todo  HeaderMap 多个请求头部自己填写
}
```

5. 建一个 **`WXRepository`** 为了和Android 里面保持一致，可以直接拿过去用
```

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
```

6. 新建一个 **`WXMainTool.kt`** 类作为 主程序调用main 函数入口：
 
```
fun main() {
//    WXRepository.instance.getHomeList()
    WXRepository.instance.getHomeList22()
//    WXRepository.instance.register("WXXXXXXXXXX","1212121","1212121")

}
```

## 三、运行调用接口

 这里运行 **`WXRepository.instance.getHomeList22() `**，而调用 **`WXRepository`**中的 
 
 ```
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
```

执行完打印出来数据：


![img_v3_02e3_37036c16-a6a5-4a7d-be57-428b380986cg.jpg](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/333c7adab667430b84eda569d7bd7fdd~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgV2dsbHNz:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiMzU2NjYxODM1MDgyNTczIn0%3D&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725238065&x-orig-sign=QvFWOMOPCGlsF%2FI9o7zjZDVp81Y%3D)

至此，一个简单的PC端 网络请求就完成了，其他类似的请求 基本就是 Retrofit 的用法了，这里不作详细介绍

## 四、总结
1. 分享了作为程序员，有些方便自己的工具可以自己搭建，无需要用别人的，可能自己搭建的更实用
2. 本文主要介绍 在PC端搭建 一套网络请求工具
3. 主要采取 Kotlin + Retrofit + Okhttp + 协程 来完成，保持和android端开发一直，可以直接拷贝过去用






## 五、我的其他开源
#### [Kotlin+协程+Flow+Retrofit+OkHttp这么好用，不运行安装到手机可以调试接口吗?可以自己搭建一套网络请求工具](https://juejin.cn/post/7406675078810910761)
#### [花式封装：Kotlin+协程+Flow+Retrofit+OkHttp +Repository，倾囊相授,彻底减少模版代码进阶之路](https://juejin.cn/post/7417847546323042345)
#### [注解处理器在架构，框架中实战应用：MVVM中数据源提供Repository类的自动生成](https://juejin.cn/post/7392258195089162290)

## 六、我的全动态插件化框架WXDynamicPlugin介绍文章：
#### [(一) 插件化框架开发背景：零反射，零HooK,全动态化，插件化框架，全网唯一结合启动优化的插件化架构](https://juejin.cn/post/7347994218235363382)
#### [(二）插件化框架主要介绍：零反射，零HooK,全动态化，插件化框架，全网唯一结合启动优化的插件化架构](https://juejin.cn/post/7367676494976532490)
#### [(三）插件化框架内部详细介绍: 零反射，零HooK,全动态化，插件化框架，全网唯一结合启动优化的插件化架构](https://juejin.cn/post/7368397264026370083)
#### [(四）插件化框架接入详细指南：零反射，零HooK,全动态化，插件化框架，全网唯一结合启动优化的插件化架构](https://juejin.cn/post/7372393698230550565)
#### [(五) 大型项目架构：全动态插件化+模块化+Kotlin+协程+Flow+Retrofit+JetPack+MVVM+极限瘦身+极限启动优化+架构示例+全网唯一](https://juejin.cn/post/7381787510071934985)
#### [(六) 大型项目架构：解析全动态插件化框架WXDynamicPlugin是如何做到全动态化的？](https://juejin.cn/post/7388891131037777929)
#### [(七) 还在不断升级发版吗？从0到1带你看懂WXDynamicPlugin全动态插件化框架？](https://juejin.cn/post/7412124636239904819)
#### [(八) Compose插件化：一个Demo带你入门Compose，同时带你入门插件化开发](https://juejin.cn/post/7425434773026537483)

## 感谢阅读：

#### 欢迎start
#### 你们的支持是我创作的动力
