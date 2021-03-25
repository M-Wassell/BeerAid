package com.example.beeraid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText mFullName,mEmail,mPassword;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName    = findViewById(R.id.fullName);
        mEmail       = findViewById(R.id.Email);
        mPassword    = findViewById(R.id.Password);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn    = findViewById(R.id.createText);

        fAuth       = FirebaseAuth.getInstance();
        fStore      = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);



        if(fAuth.getCurrentUser() !=null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(v -> {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String fullName = mFullName.getText().toString();


                if(TextUtils.isEmpty(email))
                {
                    mEmail.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    mPassword.setError("Password is Required");
                    return;
                }

                if(password.length()<6)
                {
                    mPassword.setError("Password Must have at least 6 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);



                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {

                            //Send email verification link
                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Verification Email Has Been Sent. ", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"onFailure: Email not sent " + e.getMessage());

                                }
                            });

                            //register user to firebase
                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName", fullName);
                            user.put("email", email);
                            documentReference.set(user).addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "onSuccess: usre Profile is created for " + userID);
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else
                        {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                        }
                });

        });

        mLoginBtn.setOnClickListener(v -> startActivity
                (new Intent(getApplicationContext(),Login.class)));

    }
}