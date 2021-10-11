package com.example.tapit_android.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.tapit_android.R;
import com.example.tapit_android.adapter.ContactListAdapter;
import com.example.tapit_android.databinding.ActivityHomeBinding;
import com.example.tapit_android.misc.CustomHash;
import com.example.tapit_android.misc.ScanQR;
import com.example.tapit_android.models.NewContactRequest;
import com.example.tapit_android.models.Owner;
import com.example.tapit_android.models.UserContact;
import com.example.tapit_android.models.UserToHash;
import com.example.tapit_android.models.Users;
import com.example.tapit_android.viewmodel.FormViewModel;
import com.example.tapit_android.viewmodel.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity{


    HomeActivity mInstance = this;
    final String TAG = "HomeActivity";
    HomeViewModel viewModel;
    ActivityHomeBinding binding;
    ContactListAdapter mAdapter;
    FirebaseUser firebaseUser;
    RecyclerView testRv;
    FormViewModel formViewModel = new FormViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityHomeBinding.inflate(getLayoutInflater());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        testRv = findViewById(R.id.today_rv);


        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupAuthObservers();
        setupObservers();


        updateUserHash();


        binding.greetingTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(mInstance).initiateScan();
            }
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        binding.slidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                findViewById(R.id.drag_line).setAlpha(1-slideOffset);
                findViewById(R.id.panel_search_view).setAlpha(slideOffset);

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });



        setContentView(binding.getRoot());
    }

    private void updateUserHash(){
        Log.d(TAG,"Fetching user hash by UID");
        formViewModel.getHashByUid();
    }

    private void populateContacts(List<Users> mList){
        binding.todayRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false));
        mAdapter = new ContactListAdapter(getApplicationContext(), mList);
        binding.todayRv.setAdapter(mAdapter);
        Log.d(TAG,"List we have"+mList.toString());
        mAdapter.notifyDataSetChanged();
    }

    private void setupAuthObservers(){
        final Observer<Boolean> hashByUidObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean userToHash) {
                Log.d(TAG,"User hash received, fetching owner details...");
                viewModel.getOwnerDetails();
            }
        };

        formViewModel.getHashReceived().observe(this, hashByUidObserver);
    }

    private void setupObservers(){

        final Observer<Users> ownerDataObserver = new Observer<Users>() {
            @Override
            public void onChanged(Users users) {
                Log.d(TAG,"Owner data received updating UI");
                updateUI();
            }
        };

        final Observer<Boolean> contactAddedObs = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Toast.makeText(getApplicationContext(),"New contact added!",Toast.LENGTH_LONG).show();
                viewModel.getAllContacts();
            }
        };

        viewModel.getContactAdded().observe(this, contactAddedObs);

        final Observer<NewContactRequest> newContactVerify = new Observer<NewContactRequest>() {
            @Override
            public void onChanged(NewContactRequest request) {

                if(!request.getUserExists())
                    createAlertDialog(request.getUser());
                else{
                    Log.d(TAG,"User already exists");
                    makeToast("User is already in your contacts");
                }
            }
        };

        final Observer<List<Users>> contactListObserver = new Observer<List<Users>>() {
            @Override
            public void onChanged(List<Users> users) {
                populateContacts(users);
            }
        };

        viewModel.getOwnerData().observe(this, ownerDataObserver);
        viewModel.getNewContact().observe(this, newContactVerify);
        viewModel.getContactsLiveData().observe(this, contactListObserver);
    }

    private void makeToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    private void createAlertDialog(Users user){

        Log.d(TAG,"About to create alert dialog box");
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        builder.setMessage("Add "+user.getDisplayName()+" to your contacts?")
                .setTitle("New Contact")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            addNewContact(user);
                    }
                });




        builder.create().show();
    }



    private void addNewContact(Users newUser){
        viewModel.addNewContact(newUser,"Owner");
    }

    private void verifyNewContact(String hash){
        viewModel.getContactByHash(hash);

    }


    private void updateUI(){
        String displayName=null;
        viewModel.getAllContacts();

        if(Owner.Companion.getOwner().getDisplayName()!=null){
            displayName = Owner.Companion.getOwner().getDisplayName();
        }
        if(firebaseUser.getDisplayName()!=null){
            displayName=firebaseUser.getDisplayName();
        }
        else{
            viewModel.getOwnerDetails();
        }

        binding.greetingTxt.setText("Hello, "+displayName);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result!=null){
            if(result.getContents()!=null){
                Toast.makeText(getApplicationContext(), "Scanned: "+result.getContents(),Toast.LENGTH_LONG).show();
                verifyNewContact(result.getContents());
            }
        }

    }
}