package com.example.tapit_android.authentication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapit_android.R;
import com.example.tapit_android.ui.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    EditText emailTxt, passwordTxt;
    Button submitBtn;
    TextView signupTxt;
    ProgressBar progressBar;

    final String TAG = "LoginActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTxt = findViewById(R.id.email_edit);
        passwordTxt = findViewById(R.id.passwd_edit);
        signupTxt = findViewById(R.id.signup_txt);
        submitBtn = findViewById(R.id.submit_btn);
        progressBar = findViewById(R.id.progress_bar);

        signupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                finish();
                startActivity(intent);
            }
        });



        emailTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!SignupActivity.checkEmail(charSequence.toString()))
                {
                    emailTxt.setError("Enter valid email");
                    submitBtn.setEnabled(false);
                }
                else
                    submitBtn.setEnabled(true);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                loginUser(emailTxt.getText().toString(), passwordTxt.getText().toString());
            }
        });

    }

    void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            finish();
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        if(currentUser != null){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }




    }
}