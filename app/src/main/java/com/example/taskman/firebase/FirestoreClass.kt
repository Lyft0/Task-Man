package com.example.taskman.firebase

import android.app.Activity
import android.util.Log
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