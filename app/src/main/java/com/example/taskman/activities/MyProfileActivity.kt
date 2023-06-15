package com.example.taskman.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.taskman.R
import com.example.taskman.firebase.FirestoreClass
import com.example.taskman.models.User
import com.example.taskman.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private var mSelectedImageFileUri: Uri? = null

    private lateinit var mUserDetails: User

    private var mProfileImageURL: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setupActionBar()

        FirestoreClass().loadUserData(this@MyProfileActivity)

        val userImage = findViewById<CircleImageView>(R.id.iv_user_image)
        userImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this@MyProfileActivity)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        val btnUpdate = findViewById<Button>(R.id.btn_update)
        btnUpdate.setOnClickListener {
            // Here if the image is not selected then update the other details of user.
            if (mSelectedImageFileUri != null) {
                uploadUserImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                // Call a function to update user details in the database.
                updateUserProfileData()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val userImage = findViewById<CircleImageView>(R.id.iv_user_image)

        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            mSelectedImageFileUri = data.data
            try {
                Glide
                    .with(this@MyProfileActivity)
                    .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(userImage) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@MyProfileActivity)
            } else {
                Toast.makeText(
                    this,
                    "You just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }



    private fun setupActionBar() {
        val toolbarProfile = findViewById<Toolbar>(R.id.toolbar_my_profile_activity)
        setSupportActionBar(toolbarProfile)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_arrow_back)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        toolbarProfile.setNavigationOnClickListener { onBackPressed() }
    }

    // get user data dan tampilkan di edit profil
    fun setUserDataInUI(user: User) {
        mUserDetails = user

        val userImage = findViewById<CircleImageView>(R.id.iv_user_image)

        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(userImage)

        val fieldName = findViewById<EditText>(R.id.et_name)
        val fieldEmail = findViewById<EditText>(R.id.et_email)
        val fieldMobile = findViewById<EditText>(R.id.et_mobile)

        fieldName.setText(user.name)
        fieldEmail.setText(user.email)
        if (user.mobile != 0L) {
            fieldMobile.setText(user.mobile.toString())
        }
    }

    private fun updateUserProfileData() {
        val userHashMap = HashMap<String, Any>()
        val fieldName = findViewById<EditText>(R.id.et_name)
        val fieldMobile = findViewById<EditText>(R.id.et_mobile)

        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }
        if (fieldName.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = fieldName.text.toString()
        }
        if (fieldMobile.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = fieldMobile.text.toString().toLong()
        }
        // Update the data in the database.
        FirestoreClass().updateUserProfileData(this@MyProfileActivity, userHashMap)
    }

    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedImageFileUri != null) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(this@MyProfileActivity, mSelectedImageFileUri)
            )
            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )
                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())
                            // assign the image url to the variable.
                            mProfileImageURL = uri.toString()
                            // Call a function to update user details in the database.
                            updateUserProfileData()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@MyProfileActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()
                    hideProgressDialog()
                }
        }
    }

    fun profileUpdateSuccess() {
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

}