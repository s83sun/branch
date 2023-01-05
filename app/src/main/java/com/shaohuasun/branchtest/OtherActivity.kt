package com.shaohuasun.branchtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.shaohuasun.branchtest.data.DemoObject
import com.shaohuasun.branchtest.databinding.ActivityOtherBinding

class OtherActivity: AppCompatActivity() {
    companion object {
        val KEY_DIRECT = "other"
        val KEY_CONTENT = "content"
    }

    private lateinit var binding: ActivityOtherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.getStringExtra(KEY_CONTENT)?.let {
            val demoObj = Gson().fromJson(it, DemoObject::class.java)
            binding.tvTitle.text = demoObj.title
            binding.tvSubtitle.text = demoObj.subtitle
            if (!demoObj.imageUrl.isNullOrEmpty()) {
                Glide.with(this).load(demoObj.imageUrl).into(binding.ivOther)
            }
        }
    }
}