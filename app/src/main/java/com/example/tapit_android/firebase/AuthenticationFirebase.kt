package com.example.tapit_android.firebase

import android.net.Uri
import android.util.Log
import com.example.tapit_android.models.UserToHash
import com.example.tapit_android.viewmodel.FirebaseAuthObserver
import com.example.tapit_android.viewmodel.HomeObserver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.nio.file.attribute.UserPrincipalLookupService

class AuthenticationFirebase {

    val TAG = "AuthenticationFirebase"

    val mObservers:ArrayList<FirebaseAuthObserver> = ArrayList()

    val authUser = FirebaseAuth.getInstance().getCurrentUser()
    val firestoreDb = FirebaseFirestore.getInstance()

    fun registerObserver(observer: FirebaseAuthObserver){
        if(observer !in mObservers){
            mObservers.add(observer)
        }
    }

    fun setFirebaseDisplayName(displayName:String){
        val user = FirebaseAuth.getInstance().currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

        user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG,it.toString())
                }
    }

    fun linkUserWithHash(userHash:String){

        val userToHash:UserToHash = UserToHash(userHash)

//        FirebaseDatabase.getInstance().getReference("Users")
//                .child(FirebaseAuth.getInstance().currentUser!!.uid)
//                .setValue(userToHash)
//                .addOnSuccessListener {
//                    Log.d(TAG,"Successfully added user to realtime db")
//                }
//                .addOnFailureListener {
//                    Log.d(TAG, it.toString())
//                }

        firestoreDb.collection("user_to_hash").document(authUser!!.uid)
                .set(userToHash)
                .addOnSuccessListener {
                    Log.d(TAG,"Successfully linked user with hash")
                    for(obs in mObservers)
                        obs.onUserHashLinked()
                }
                .addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
    }

    fun getUserHashByUid(){
        firestoreDb.collection("user_to_hash").document(authUser!!.uid)
                .get()
                .addOnSuccessListener {

                    for(obs in mObservers){
                        obs.onHashReceivedByUid(it.toObject(UserToHash::class.java))
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
    }
}