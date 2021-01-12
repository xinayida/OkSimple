package com.gateoftruth.oklibrary

import okio.BufferedSink

class DownloadBean {
//    var url = ""
    var filename = ""
    var filePath = ""
//    var downloadLength = 0L
//    var contentLength = 0L
    var sink : BufferedSink? = null
}