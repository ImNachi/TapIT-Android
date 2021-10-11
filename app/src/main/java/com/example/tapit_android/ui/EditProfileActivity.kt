package com.example.tapit_android.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.tapit_android.R
import com.example.tapit_android.databinding.ActivityEditProfileBinding
import com.example.tapit_android.models.Owner

class EditProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding

    val TAG = "EditProfileActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        updateEditTexts()

        setContentView(binding.root)
    }

    fun updateEditTexts(){
        Log.d(TAG,Owner.owner.toString())
        val ownerObj = Owner.owner

        binding.nameEdit.setText(ownerObj?.displayName)
        binding.companyEdit.setText(ownerObj?.companyName)
        binding.professionEdit.setText(ownerObj?.profession)
        binding.personalPhoneEdit.setText(ownerObj?.personalPhone)
        binding.officePhoneEdit.setText(ownerObj?.officePhone)
    }
}