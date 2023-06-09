package com.example.taskman.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.taskman.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignInActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        auth = FirebaseAuth.getInstance()

        val btnSignIn = findViewById<Button>(R.id.btn_sign_in)
        btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }

        setupActionBar()
    }

    private fun setupActionBar(){
        val toolbarSign = findViewById<Toolbar>(R.id.toolbar_sign_in)
        setSupportActionBar(toolbarSign)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_arrow_back)
        }

        toolbarSign.setNavigationOnClickListener { onBackPressed() }
    }

    private fun signInRegisteredUser(){
        val fieldEmail = findViewById<EditText>(R.id.field_email_sign_in)
        val fieldPass = findViewById<EditText>(R.id.field_password_sign_in)

        val email: String = fieldEmail.text.toString().trim { it <= ' ' }
        val password: String = fieldPass.text.toString().trim { it <= ' ' }

        if(validateForm(email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseApp.initializeApp(this)
            // Sign-In dengan FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {

                        Toast.makeText(
                            this@SignInActivity,
                            "You have successfully signed in. $email",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

    }

    // validasi form
    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }

}
