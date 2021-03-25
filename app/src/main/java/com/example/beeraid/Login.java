package com.example.beeraid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mCreateBtn, forgotTextLink;
    ProgressBar progressBar;
    FirebaseAuth fAuth;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.Password);
        progressBar = findViewById(R.id.progressBar2);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);
        forgotTextLink = findViewById(R.id.forgotPassword);

        mLoginBtn.setOnClickListener(v -> {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

            if(TextUtils.isEmpty(email)) {
            mEmail.setError("Email is Required");
            return;
        }
            if(TextUtils.isEmpty(password)) {
            mPassword.setError("Password is Required");
            return;
        }
            if(password.length()<6) {
            mPassword.setError("Password Must have at least 6 characters");
            return;
        }
            progressBar.setVisibility(View.VISIBLE);

        //authenticate the user
            Task<AuthResult> logged_in_successfully = fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        });
        mCreateBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        forgotTextLink.setOnClickListener(view -> {
            final EditText resetMail = new EditText(view.getContext());
            final AlertDialog.Builder passwordRestDialog = new AlertDialog.Builder(view.getContext());
            passwordRestDialog.setTitle("Reset Password ?");
            passwordRestDialog.setMessage("Enter Email Address");
            passwordRestDialog.setView(resetMail);

            passwordRestDialog.setPositiveButton("Yes", (dialogInterface, i) -> {
                //Extract email and send reset link
                String mail = resetMail.getText().toString();

                fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(aVoid -> Toast.makeText(Login.this, "Reset Link Sent To Your Email", Toast.LENGTH_SHORT). show()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this,"Error! Reset Link Is Not Sent" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });
            passwordRestDialog.setNegativeButton("No", (dialogInterface, i) -> {
                //close dialog
            });

                    passwordRestDialog.create().show();
        });
    }
}