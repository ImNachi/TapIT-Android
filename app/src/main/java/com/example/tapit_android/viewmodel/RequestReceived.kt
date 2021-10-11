package com.example.tapit_android.viewmodel

import com.example.tapit_android.models.Owner
import com.example.tapit_android.models.UserContact
import com.example.tapit_android.models.UserToHash
import com.example.tapit_android.models.Users

interface HomeObserver {
    fun onUserDataUpdated()
    fun onUserDataFetched(data:Users?)
    fun onContactAdded()
    fun onContactFetchedByHash(data:Users?, userExists:Boolean)
    fun onContactListReceived(contactList:List<Users>)
    fun onContactFetchedByReference(user:Users?)
}

interface FirebaseAuthObserver{
    fun onUserHashLinked()
    fun onHashReceivedByUid(userHash:UserToHash?)
}