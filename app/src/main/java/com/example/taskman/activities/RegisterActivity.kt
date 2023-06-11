package com.example.taskman.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.taskman.R
import com.example.taskman.firebase.FirestoreClass
import com.example.taskman.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupActionBar()
    }

    // user success register
    fun userRegisteredSuccess(){
        Toast.makeText(
            this@RegisterActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        // finish register screen
        finish()
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

        val btnRegister = findViewById<Button>(R.id.btn_register)
        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    // register user
    private fun registerUser(){
        val fieldName = findViewById<EditText>(R.id.field_name)
        val fieldEmail = findViewById<EditText>(R.id.field_email)
        val fieldPass = findViewById<EditText>(R.id.field_password)

        val name: String = fieldName.text.toString().trim { it <= ' ' }
        val email: String = fieldEmail.text.toString().trim { it <= ' ' }
        val password: String = fieldPass.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseApp.initializeApp(this)
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        // If the registration is successfully done
                        if (task.isSuccessful) {
                            // firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            // registered Email
                            val registeredEmail = firebaseUser.email!!
                            val user = User(
                                firebaseUser.uid, name, registeredEmail
                            )
                            FirestoreClass().registerUser(this@RegisterActivity, user)
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                task.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }

    // validasi form
    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
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