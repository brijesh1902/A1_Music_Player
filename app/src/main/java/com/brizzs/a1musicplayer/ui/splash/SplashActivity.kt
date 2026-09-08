package com.brizzs.a1musicplayer.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.brizzs.a1musicplayer.R
import com.brizzs.a1musicplayer.ui.main.MainActivity
import com.brizzs.a1musicplayer.utils.MyApplication

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.size(160.dp),
                    shape = RoundedCornerShape(80.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF004FEE)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.headphones),
                            contentDescription = "Headphones Logo",
                            modifier = Modifier.size(120.dp)
                        )
                    }
                }
            }
        }
        startActivityTimer()
    }

    private fun startActivityTimer() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (MyApplication.getSharePreference().isFirstRun) {
                Intent(applicationContext, OnBoardingActivity::class.java)
            } else {
                Intent(applicationContext, MainActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 1200)
    }
}
