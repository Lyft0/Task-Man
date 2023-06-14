package com.example.taskman.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.taskman.activities.MainActivity
import com.example.taskman.activities.MyProfileActivity
import com.example.taskman.activities.RegisterActivity
import com.example.taskman.activities.SignInActivity
import com.example.taskman.models.User
import com.example.taskman.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    // membuat instance Firebase Firestore
    private val mFireStore = FirebaseFirestore.getInstance()

    // function untuk memasukkan registered user ke firebase firestore
    fun registerUser(activity: RegisterActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            // document ID untuk users fields
            .document(getCurrentUserID())
            // gabungkan userInfo apapun
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                // jika sukses
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                // jika gagal
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS) // Collection Name
            .document(getCurrentUserID()) // Document ID
            .update(userHashMap) // A hashmap of fields which are to be updated.
            .addOnSuccessListener {
                // Profile data is updated successfully.
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                // Notify the success result.
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
                Toast.makeText(activity, "Error when updating the profile", Toast.LENGTH_SHORT).show()
            }
    }

    // get user data ketika sign in
    fun loadUserData(activity: Activity){
        mFireStore.collection(Constants.USERS)
            // document ID untuk users fields
            .document(getCurrentUserID())
            // get userInfo
            .get()
            .addOnSuccessListener { document ->
                // jika sukses
                val loggedInUser = document.toObject(User::class.java)!!
                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }

    // get UserID dari current user
    fun getCurrentUserID(): String {
        // auto login
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

}