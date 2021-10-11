package com.example.tapit_android.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapit_android.R;
import com.example.tapit_android.ui.FormActivity;
import com.example.tapit_android.ui.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    TextView signinTxt;
    private FirebaseAuth mAuth;
    EditText emailTxt, passwordTxt;
    final String TAG = "SignupActivity";
    boolean emailIsValid = false, passwordIsValid=false;
    Button confirmBtn;
    ProgressBar progressBar;
    EditText nameTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailTxt = findViewById(R.id.email_edit);
        passwordTxt = findViewById(R.id.passwd_edit);
        confirmBtn = findViewById(R.id.submit_btn);
        progressBar = findViewById(R.id.progress_bar);
        signinTxt = findViewById(R.id.signin_txt);
        nameTxt = findViewById(R.id.name_field);


        mAuth = FirebaseAuth.getInstance();

        emailTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailIsValid = checkEmail(charSequence.toString());
                if(!emailIsValid)
                    emailTxt.setError("Enter valid email");
                if(emailIsValid && passwordIsValid)
                    confirmBtn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean checkPassword = checkPassword(charSequence.toString());
                if(!checkPassword)
                    passwordTxt.setError("At least one special character required");
                if(charSequence.length()<8)
                    passwordTxt.setError("Minimum 8 characters required");

                passwordIsValid = checkPassword && charSequence.length()>8;

                if(emailIsValid && passwordIsValid)
                    confirmBtn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        signinTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                registerNewUser(emailTxt.getText().toString(), passwordTxt.getText().toString());
            }
        });


    }

    boolean checkPassword(String pawd){
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(pawd);

        return matcher.matches();
    }

    static boolean checkEmail(String email){
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    void updateFirebaseUserData(FirebaseUser user,String display_name){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(display_name)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                }
                            }
                        });
            }
        });

        thread.run();


    }

    void registerNewUser(String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            finish();
                            Intent intent = new Intent(getApplicationContext(), FormActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


}