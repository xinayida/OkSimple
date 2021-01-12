package com.gateoftruth.oklibrary

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

class ProgressResponseBody(
    private val url: String,
    private val rawResponse: ResponseBody,
    private val baseProgressListener: BaseProgressListener,
    private val sink: BufferedSink?,
    private val downloaded: Long? = 0L
) : ResponseBody() {
    private val source: BufferedSource? = null
    override fun contentLength(): Long {
        return rawResponse.contentLength()
    }

    override fun contentType(): MediaType? {
        return rawResponse.contentType()
    }

    override fun source(): BufferedSource {
        return source ?: getSource(rawResponse.source()).buffer()
    }


    private fun getSource(source: Source): Source {
        return object : ForwardingSource(source) {
            var downloadByte = 0L
            override fun read(buffer: Buffer, byteCount: Long): Long {
                val byteRead = super.read(buffer, byteCount)
                if (byteRead != -1L) {
                    sink?.buffer?.apply {
                        buffer.copyTo(this, downloadByte, buffer.size - downloadByte)
                    }
                    downloadByte += byteRead
                } else {
                    sink?.flush()
                    sink?.close()
                }
                baseProgressListener.downloadProgress(
                    url,
                    downloaded!! + rawResponse.contentLength(),
                    downloaded + downloadByte
                )
                return byteRead
            }
        }
    }


}

