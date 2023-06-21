package com.example.taskman.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.taskman.R

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val typeFace: Typeface = Typeface.createFromAsset(assets,"Dosis-Bold.ttf")
        findViewById<TextView>(R.id.tm_app_name_intro).typeface = typeFace

        // Pindah ke register screen
        val btnRegister = findViewById<Button>(R.id.btn_register_intro)
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Pindah ke sign in screen
        val btnSignIn = findViewById<Button>(R.id.btn_sign_in_intro)
        btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

    }

}