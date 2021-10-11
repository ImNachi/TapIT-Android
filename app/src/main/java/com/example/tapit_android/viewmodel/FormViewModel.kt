package com.example.tapit_android.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tapit_android.firebase.AuthenticationFirebase
import com.example.tapit_android.models.Owner
import com.example.tapit_android.models.UserToHash
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FormViewModel: ViewModel(), FirebaseAuthObserver{

    val authFirebase = AuthenticationFirebase()
    val hashReceived:MutableLiveData<Boolean> = MutableLiveData()
    val TAG = "FormViewModel"


    init {
        authFirebase.registerObserver(this)
    }

    fun setDisplayNameFirebase(displayName:String){
        viewModelScope.launch(Dispatchers.IO) {
            authFirebase.setFirebaseDisplayName(displayName)
        }
    }

    fun userToHash(userHash:String){
        viewModelScope.launch(Dispatchers.IO) {
            authFirebase.linkUserWithHash(userHash)
        }
    }

    fun getHashByUid(){
        viewModelScope.launch(Dispatchers.IO) {
            authFirebase.getUserHashByUid()
        }
    }

    override fun onUserHashLinked() {
//        getHashByUid()
    }

    override fun onHashReceivedByUid(userHash: UserToHash?) {
        Owner.owner?.sha1Hash = userHash?.sha1Hash.toString()
        hashReceived.value = true
        Log.d(TAG, "Hash recevied by uid --> "+userHash)
    }


}