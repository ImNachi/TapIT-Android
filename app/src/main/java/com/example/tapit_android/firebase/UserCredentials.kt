package com.example.tapit_android.firebase

import android.util.Log
import androidx.core.net.toUri
import com.example.tapit_android.models.Owner
import com.example.tapit_android.models.UserContact
import com.example.tapit_android.models.UserToHash
import com.example.tapit_android.models.Users
import com.example.tapit_android.viewmodel.HomeObserver
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class UserCredentials {
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().getCurrentUser()

    val mObservers:ArrayList<HomeObserver> = ArrayList()


    val ownerDocRef = db.collection("users").document(Owner.owner?.sha1Hash.toString())


    val TAG = "UserCredentials_Firebase"

    fun registerObserver(observer: HomeObserver){
        if(observer !in mObservers){
            mObservers.add(observer)
        }
    }

    suspend fun updateUserDetails(user: Users){
        db.collection("users").document(Owner.owner?.sha1Hash.toString()).set(user)
                .addOnCompleteListener(object : OnCompleteListener<Void>{
                    override fun onComplete(p0: Task<Void>) {
                        if(p0.isSuccessful){
                            Log.d(TAG, "User details updated successfully")
                            for( obs in mObservers){
                                obs.onUserDataUpdated()
                            }
                        }
                        else{
                            Log.d(TAG, "Error occured"+p0.exception.toString())
                        }
                    }

                })
                .addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
    }


    suspend fun updateUserProfile(newDisplayName:String, newDpUrl:String){
        val profileBuilder = UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .setPhotoUri(newDpUrl.toUri())
                .build()

        user?.updateProfile(profileBuilder)?.addOnCompleteListener(object: OnCompleteListener<Void>{
            override fun onComplete(p0: Task<Void>) {
                if(p0.isSuccessful){
                    Log.d(TAG,"Profile updated successfully")
                }
            }


        })
    }


    fun getOwnerData(){
          //Warning owner hash must be set before using this
            Log.d(TAG,"Owner hash-->"+Owner.owner?.sha1Hash.toString())
            db.collection("users").document(Owner?.owner?.sha1Hash.toString())
                    .get().addOnSuccessListener {
                Log.d(TAG,"Owner data received by hash -->"+it.toObject(Users::class.java))
                if(it!=null){
                    for(obs in mObservers){
                        obs.onUserDataFetched(it.toObject(Users::class.java))
                    }
                }
            }
        }


    fun addNewContact(newUser: Users, description:String){

//        val newContact = UserContact(FieldValue.serverTimestamp(), newUser, description, FieldValue.serverTimestamp())


        db.collection("users").document(Owner?.owner?.sha1Hash.toString()).collection("contacts").add(newUser)
                .addOnSuccessListener {
                    Log.d(TAG,"New Contact added!")
                    for(obs in mObservers){
                        obs.onContactAdded()
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG,it.toString())
                }
    }

    fun getUserByHash(hash:String){
        db.collection("users").document(hash)
                .get()
                .addOnSuccessListener {

                    if(it!=null){
                        Log.d(TAG,"User exists about to verify")
                        for(obs in mObservers){
                            obs.onContactFetchedByHash(it.toObject(Users::class.java), false)
                        }
                    }
                    else{
                        Log.d(TAG,"User doesn't exist")
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG,it.toString())
                }
    }

    fun getAllContacts(){
        Log.d(TAG,"User hash value--> "+Owner?.owner?.sha1Hash.toString())
        db.collection("users").document(Owner?.owner?.sha1Hash.toString())
                .collection("contacts").get()
                .addOnSuccessListener {
                    for(obs in mObservers){
                        obs.onContactListReceived(it.toObjects(Users::class.java))
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG,it.toString())
                }
    }

    fun newUserAlreadyExists(){
        for(obs in mObservers){
            obs.onContactFetchedByHash(null, true)
        }
    }


    fun getContactByReference(documentRef:DocumentReference){
        documentRef.get()
                .addOnSuccessListener {
                    for(obs in mObservers){
                        obs.onContactFetchedByReference(it.toObject(Users::class.java))
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG,it.toString())
                }
    }
}