package com.example.taskman.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.taskman.R
import com.example.taskman.firebase.FirestoreClass

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Mengubah font textview dengan asset
        var tvAppName: TextView = findViewById(R.id.tm_app_name)
        val typeFace: Typeface = Typeface.createFromAsset(assets,"Dosis-Bold.ttf")
        tvAppName.typeface = typeFace

        android.os.Handler().postDelayed({
            val currentUserID = FirestoreClass().getCurrentUserID()
            // auto login
            if (currentUserID.isNotEmpty()) {
                // start main activity
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                // intro screen
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }
            finish()
        },2500)
    }
}