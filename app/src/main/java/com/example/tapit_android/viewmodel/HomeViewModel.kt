package com.example.tapit_android.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tapit_android.firebase.UserCredentials
import com.example.tapit_android.models.NewContactRequest
import com.example.tapit_android.models.Owner
import com.example.tapit_android.models.UserContact
import com.example.tapit_android.models.Users
import com.firebase.ui.auth.data.model.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel(), HomeObserver {

    val ownerData:MutableLiveData<Users?> = MutableLiveData()

    val TAG:String = "HomeViewModel"

    val contactAdded:MutableLiveData<Boolean> = MutableLiveData()

    val userDataUpdated: MutableLiveData<Boolean> = MutableLiveData()

    val contactsLiveData:MutableLiveData<List<Users>> = MutableLiveData()

    val homeRepository:UserCredentials = UserCredentials()

    val newContact:MutableLiveData<NewContactRequest> = MutableLiveData()

    val refreshUI:MutableLiveData<Boolean> = MutableLiveData()

    val contactByReference:MutableLiveData<Users?> = MutableLiveData()

    val finalContactList:MutableLiveData<ArrayList<Users?>> = MutableLiveData()


    init {
        homeRepository.registerObserver(this)
    }

    fun updateUserDetails(newUser:Users){
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Entered View model")
            homeRepository.updateUserDetails(newUser)
        }
    }

    fun addNewContact(newUser: Users, description:String){
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.addNewContact(newUser,description);
        }
    }

    fun getOwnerDetails(){
        homeRepository.getOwnerData()
    }

    fun getContactByHash(hash:String){
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG,"contactByHash called from viewmodel");
            if(!userAlreadyExists(hash))
                homeRepository.getUserByHash(hash)
            else{
                homeRepository.newUserAlreadyExists()
            }
        }
    }

    fun getAllContacts(){
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.getAllContacts()
        }
    }

//    fun getFinalContactList(contactList: List<UserContact>){
//
//        for(mContact in contactList){
//            viewModelScope.launch(Dispatchers.IO) {
//                homeRepository.getContactByReference(mContact.contact)
//            }
//        }
//    }

    override fun onUserDataUpdated() {
        userDataUpdated.value = true
    }

    override fun onUserDataFetched(data: Users?) {
        Owner.owner = data
        ownerData.value = data
        Log.d(TAG,"Owner details fetched --> "+data.toString())
    }

    override fun onContactAdded() {
        contactAdded.value=true
    }

    override fun onContactFetchedByHash(data: Users?, userExits:Boolean) {

       newContact.value = NewContactRequest(data, userExits)

    }

    override fun onContactListReceived(contactList: List<Users>) {
        contactsLiveData.value = contactList
//        getFinalContactList(contactList)
    }

    override fun onContactFetchedByReference(user: Users?) {
        finalContactList.value?.add(user)
    }

    private fun userAlreadyExists(checkUser:String):Boolean{
        for(mContact in contactsLiveData.value!!){
            if(mContact.sha1Hash!!.equals(checkUser)){
                return true
            }
        }

        return false
    }

    private fun updateOwnerDetails(user:Users){

    }
}