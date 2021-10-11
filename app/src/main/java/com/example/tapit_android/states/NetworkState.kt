package com.example.tapit_android.states

import com.example.tapit_android.models.Users


sealed class UserCredentialsState{
    data class Success(val data: Users) : UserCredentialsState()
    data class Error(val error:String) : UserCredentialsState()
    object InvalidData: UserCredentialsState()
    data class NetworkException(val error : String) : UserCredentialsState()
}
