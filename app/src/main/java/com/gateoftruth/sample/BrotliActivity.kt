package com.gateoftruth.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gateoftruth.oklibrary.OkError
import com.gateoftruth.oklibrary.OkSimple
import okhttp3.Call
import okhttp3.Response
import okhttp3.brotli.BrotliInterceptor

class BrotliActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brotli)
        val builder = OkSimple.okHttpClient.newBuilder()
        builder.addInterceptor( BrotliInterceptor)
        OkSimple.okHttpClient = builder.build()
        OkSimple.get("https://www.google.com/").execute(object : GsonCallBack<Any>() {
            override fun success(data: Any, rawBodyString: String, call: Call, response: Response) {
                Toast.makeText(this@BrotliActivity, "success", Toast.LENGTH_LONG).show()
            }

            override fun failure(error: OkError) {
                super.failure(error)
                Toast.makeText(this@BrotliActivity, "failed", Toast.LENGTH_LONG).show()
            }

        })
    }
}
