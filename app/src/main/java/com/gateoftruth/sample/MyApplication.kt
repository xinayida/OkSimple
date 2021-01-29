package com.gateoftruth.sample

import android.app.Application
import com.gateoftruth.oklibrary.OkSimple
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        /*
        init your own okHttpClient if necessary
         */
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkSimple.okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .connectTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .cache(Cache(cacheDir, 1024 * 1024 * 10))
            .protocols(Collections.unmodifiableList(mutableListOf(Protocol.HTTP_1_1, Protocol.HTTP_2)))
            .build()
//        OkSimple.addGlobalParams("globeparams", "1")
        OkSimple.application = this
        /*
        replace by your global params and global header
         */
        /*OkSimple.addGlobalParams("testGlobalParams","1")
        OkSimple.addGlobalHeader("testGlobalHeader","2")*/
    }
}