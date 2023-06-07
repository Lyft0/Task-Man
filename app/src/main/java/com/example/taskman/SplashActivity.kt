package com.example.taskman

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.util.logging.Handler

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Mengubah font textview dengan asset
        var tvAppName: TextView = findViewById(R.id.tm_app_name)
        val typeFace: Typeface = Typeface.createFromAsset(assets,"Dosis-Bold.ttf")
        tvAppName.typeface = typeFace

        android.os.Handler().postDelayed({
            startActivity(Intent(this,IntroActivity::class.java))
            finish()
        },2500)
    }
}