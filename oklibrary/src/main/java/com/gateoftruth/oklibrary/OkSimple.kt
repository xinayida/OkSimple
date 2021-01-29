package com.gateoftruth.oklibrary

import android.app.Application
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object OkSimple {
    var okHttpClient = OkHttpClient.Builder().build()
        set(value) {
            field = value.newBuilder().addNetworkInterceptor(responseInterceptor()).build()
        }

    private fun responseInterceptor(): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                val response = chain.proceed(request)
                if (response.code >= 300) {
                    errorHandlers.forEach {
                        it.get()?.handleError(OkError(null, response = response))
                    }
                }
                return response
            }
        }
    }

    private val errorHandlers = mutableListOf<WeakReference<OkErrorHandler>>()

    /**
     * 注册错误处理回调
     */
    fun registerErrorHandler(handler: OkErrorHandler) {
        unRegisterErrorHandler(handler)
        errorHandlers.add(WeakReference(handler))
    }

    /**
     * 注销错误处理回调
     */
    fun unRegisterErrorHandler(handler: OkErrorHandler) {
        val iterator = errorHandlers.listIterator()
        iterator.forEach {
            if (it.get() == null) iterator.remove()
            if (it.get() == handler) return
        }
    }

    val mainHandler = OksimpleHandler()

    val globalHeaderMap = hashMapOf<String, String>()

    val globalParamsMap = hashMapOf<String, String>()

    var preventContinuousRequests = true

    var networkUnavailableForceCache = true

    val statusUrlMap = ConcurrentHashMap<String, Boolean>()

    internal val tagStrategyMap = ConcurrentHashMap<String, RequestStrategy>()

//    val cachedThreadPool: ExecutorService = Executors.newCachedThreadPool()

    var networkAvailable = true

    var application: Application? = null
        set(value) {
            field = value
            if (value != null) {
                OksimpleNetworkUtil.init(value)
            }
        }


    fun addGlobalHeader(key: String, value: String) {
        globalHeaderMap[key] = value
    }

    /**
     * append to url
     */
    fun addGlobalParams(key: String, value: String) {
        globalParamsMap[key] = value
    }

    fun get(url: String): SimpleRequest {
        return SimpleRequest(url, OkSimpleConstant.GET)
    }

    fun postJson(url: String, jsonObject: JSONObject): SimpleRequest {
        val request = SimpleRequest(url, OkSimpleConstant.POST_JSON)
        request.postJson(jsonObject)
        return request
    }

    fun post(url: String, valueMap: Map<String, String> = HashMap()): SimpleRequest {
        val request = SimpleRequest(url, OkSimpleConstant.POST)
        request.post(valueMap)
        return request
    }

    fun downloadFile(url: String, filePath: String): SimpleRequest {
        val request = SimpleRequest(url, OkSimpleConstant.DOWNLOAD_FILE)
        request.filePath = filePath
        return request
    }

    fun getBitmap(url: String): SimpleRequest {
        return SimpleRequest(url, OkSimpleConstant.GET_BITMAP)
    }

    fun uploadFile(
        url: String,
        file: File,
        mediaType: String = OkSimpleConstant.STREAM_MEDIA_TYPE_STRING
    ): SimpleRequest {
        val request = SimpleRequest(url, OkSimpleConstant.UPLOAD_FILE)
        request.uploadFile(file)
        request.defaultFileMediaType = mediaType.toMediaType()
        return request
    }

    fun postForm(url: String): SimpleRequest {
        return SimpleRequest(url, OkSimpleConstant.POST_FORM)
    }

    fun <G : GlideCallBack> getGlideClient(listener: G): OkHttpClient {
        return getBitmap("").prepare(listener)
    }

    internal fun strategyRequest(strategy: RequestStrategy) {
        SimpleRequest().process(strategy)
    }


    fun cancelCall(tag: String) {
        val runningCall = okHttpClient.dispatcher.runningCalls().firstOrNull {
            it.request().tag().toString() == tag
        }
        runningCall?.cancel()

        val strategy = tagStrategyMap[tag]
        if (strategy != null) {
            mainHandler.removeMessages(OkSimpleConstant.STRATEGY_MESSAGE, strategy)
        }

        val queuedCall = okHttpClient.dispatcher.queuedCalls().firstOrNull {
            it.request().tag().toString() == tag
        }
        queuedCall?.cancel()


        statusUrlMap.remove(tag)
    }

    fun cancelAll() {
        okHttpClient.dispatcher.cancelAll()
        statusUrlMap.clear()
        mainHandler.removeMessages(OkSimpleConstant.STRATEGY_MESSAGE)
    }


}