package com.example.taskman.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.taskman.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupActionBar()
    }

    private fun setupActionBar(){
        val toolbarRegister = findViewById<Toolbar>(R.id.toolbar_register)
        setSupportActionBar(toolbarRegister)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_arrow_back)
        }

        toolbarRegister.setNavigationOnClickListener { onBackPressed() }
    }
}