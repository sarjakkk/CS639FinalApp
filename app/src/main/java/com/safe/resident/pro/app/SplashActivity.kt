package com.safe.resident.pro.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.safe.resident.pro.app.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@SplashActivity, R.layout.activity_splash)

        val sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false)

        binding.tvVersionName.text = "Version: V${BuildConfig.VERSION_NAME}"

        Handler(mainLooper).postDelayed({
            val destination = if (isLoggedIn) MainActivity::class.java else LoginActivity::class.java
            val intent = Intent(this, destination)
            startActivity(intent)
            finish()
        }, 2000)
    }
}