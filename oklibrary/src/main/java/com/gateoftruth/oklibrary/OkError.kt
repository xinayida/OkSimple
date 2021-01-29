package com.gateoftruth.oklibrary

import okhttp3.Response

class OkError(val exception: Exception?, val response: Response? = null) {

    companion object {
        const val ERROR_CODE_NETWORK = -1
        const val ERROR_CODE_USER = -2

        const val ERROR_MSG_NETWORK = "网络错误"
    }

    var errorCode = let {
        response?.code ?: ERROR_CODE_NETWORK
    }

    val errorMsg = let {
        response?.message ?: exception?.toString() ?: ERROR_MSG_NETWORK
    }
}