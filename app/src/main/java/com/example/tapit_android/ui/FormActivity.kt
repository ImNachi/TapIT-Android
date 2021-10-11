package com.example.tapit_android.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.tapit_android.R
import com.example.tapit_android.databinding.ActivityFormBinding
import com.example.tapit_android.misc.CustomHash
import com.example.tapit_android.models.Owner
import com.example.tapit_android.models.Users
import com.example.tapit_android.viewmodel.FormViewModel
import com.example.tapit_android.viewmodel.HomeViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.FirebaseUser





class FormActivity : AppCompatActivity() {

    var user = FirebaseAuth.getInstance().currentUser
    val authViewModel= FormViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityFormBinding.inflate(layoutInflater)

        val viewModel = HomeViewModel()




        binding.submitBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val newUser = getUserDetails(binding)
                viewModel.updateUserDetails(newUser)
            }

        })

        val userDetailsObserver = Observer<Boolean>{
            binding.progressBar.visibility = View.GONE
            if(it){
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }

        viewModel.userDataUpdated.observe(this, userDetailsObserver)

        setContentView(binding.root)
    }

    fun getUserDetails(binding: ActivityFormBinding): Users{

        val hashString:String = CustomHash.getUserHash(user?.email, binding.nameField.text.toString())
        Owner.owner?.sha1Hash = hashString
        Owner.owner?.displayName = binding.nameField.text.toString()

        authViewModel.setDisplayNameFirebase(binding.nameField.text.toString())
        authViewModel.userToHash(hashString)

        val user = Users(
                sha1Hash = hashString,
                displayName = binding.nameField.text.toString(),
                age = binding.ageField.text.toString(),
                email = user?.email,
                address = binding.addressField.text.toString(),
                companyName = binding.companyField.text.toString(),
                profession = binding.professionField.text.toString(),
                personalPhone = binding.phoneOneField.text.toString(),
                officePhone = binding.phoneTwoField.text.toString(),
                dpUrl = null
        )



        return user
    }
}