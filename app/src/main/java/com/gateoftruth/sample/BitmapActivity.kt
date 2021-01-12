package com.gateoftruth.sample

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gateoftruth.oklibrary.BitmapResultCallBack
import com.gateoftruth.oklibrary.OkSimple
import kotlinx.android.synthetic.main.activity_bitmap.*
import okhttp3.Call

class BitmapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap)
        val url =
                "https://images.pexels.com/photos/280249/pexels-photo-280249.jpeg?cs=srgb&dl=architecture-big-ben-black-and-white-280249.jpg&fm=jpg"
        OkSimple.getBitmap(url).execute(object : BitmapResultCallBack() {
            override fun finish(bitmap: Bitmap) {
                iv_bitmap.setImageBitmap(bitmap)

            }

            override fun downloadProgressOnMainThread(url: String, total: Long, current: Long) {
                tv_bitmap_progress.text = "current:$current\t\ttotal:$total"
            }


        })
    }
}
