package com.gateoftruth.oklibrary

import okhttp3.Call
import okhttp3.Response
import java.io.File
import java.io.IOException


abstract class FileResultCallBack : ResultCallBack() {
    var downloadBean: DownloadBean? = null
    override fun start() {

    }

    final override fun failure(error: OkError) {
        downloadBean?.sink?.apply {
            close()
        }
//        val file = File(downloadBean!!.filePath)
//        val downloadLength = if (file.exists()) file.length() else 0
//        Log.d("Stefan", "download error ${downloadBean?.filePath} failed to: $downloadLength")
        downloadFailed(error, downloadBean!!.filePath)
    }

    abstract fun downloadFailed(error: OkError, filePath: String)


    override fun response(call: Call, response: Response) {
        val responseBody = response.body
        if (responseBody == null) {
            responseBodyGetNull(call, response)
            return
        }
        val file = File(downloadBean!!.filePath)
        OkSimple.mainHandler.post {
            if (!file.exists() || file.length() == 0L) {
                failure(OkError(IOException("File Download Failure"), response))
            } else {
                finish(file)
            }
        }
    }

    override fun responseBodyGetNull(call: Call, response: Response) {

    }

    override fun downloadProgress(url: String, total: Long, current: Long) {
        OkSimple.mainHandler.post {
            downloadProgressOnMainThread(url, total, current)
        }
    }

    override fun uploadProgress(fileName: String, total: Long, current: Long) {

    }

    override fun uploadProgressOnMainThread(fileName: String, total: Long, current: Long) {

    }

    abstract fun finish(file: File)


}