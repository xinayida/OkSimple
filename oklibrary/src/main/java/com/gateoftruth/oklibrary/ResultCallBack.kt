package com.gateoftruth.oklibrary

import okhttp3.Call
import okhttp3.Response

abstract class ResultCallBack : BaseProgressListener {

    val urlToBeanMap = hashMapOf<String, DownloadBean>()

    abstract fun start()

    abstract fun response(call: Call, response: Response)

    open fun failure(error: OkException){
        error.exception?.printStackTrace()
    }

    abstract fun responseBodyGetNull(call: Call, response: Response)

//    abstract fun otherException(call: Call, response: Response, e: Exception)

}