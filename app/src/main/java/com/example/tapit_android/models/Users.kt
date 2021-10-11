package com.example.tapit_android.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue


data class UserContact(val time_added: FieldValue, val contact:Users, val description:String, val last_accessed:FieldValue)


data class NewContactRequest(val user:Users?, val userExists: Boolean)

data class UsersDocument(var contactList:ArrayList<Users>){


}

data class Users(var sha1Hash:String?, val dpUrl:String?, var displayName:String?, val age:String?,
                 val email:String?, val address: String?, val companyName:String?, val profession:String?,
                 val personalPhone:String?, val officePhone:String?){
    constructor() : this(
            null,null,null,null,null,null,null,null,null,null
    )
}



class Owner{
    companion object{
        var owner:Users? = Users()
    }
}

