package com.example.taskman.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.taskman.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {
    // Digunakan untuk menggantikan appcompat activity

    // Back button
    private var doubleBackToExitPressedOnce = false
    // Progress loading
    private lateinit var mProgressDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(text: String){
        mProgressDialog = Dialog(this)

        // Set konten layar dari layout resource
        mProgressDialog.setContentView(R.layout.dialog_progress)
//        val tmProgressText = findViewById<TextView>(R.id.tm_progress_text)
//        mProgressDialog.tm_progress_text.text = text

        // Start dialog dan tampilkan ke layar
        mProgressDialog.show()
    }

    // Hide progress dialog
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    // get user id
    fun getCurrentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    // jika double back, exit
    fun doubleBackToExit(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            "Please click back again to exit.",
            Toast.LENGTH_SHORT
        ).show()

        // membuat delay jika user menekan back button
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    // Menampilkan error
    fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                R.color.snackbar_error_color
            )
        )
        snackBar.show()
    }

}