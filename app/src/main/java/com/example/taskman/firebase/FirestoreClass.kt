package com.example.taskman.firebase

import android.util.Log
import com.example.taskman.activities.RegisterActivity
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

    // get UserID dari current user
    fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

}