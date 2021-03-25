package com.example.beeraid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity
{
    TextView verifyMsg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button resendCode,profileBtn;
    //ImageButton imageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        verifyMsg = findViewById(R.id.verifyMsg);
        resendCode = findViewById(R.id.resendCode);
        final FirebaseUser user = fAuth.getCurrentUser();
        profileBtn = findViewById(R.id.profileBtn);
        //imageButton = findViewById(R.id.profileBtn);

//        imageButton.setOnClickListener(view -> {
//            Intent intent = new Intent(this,UserProfile.class);
//            startActivity(intent);
//        });


        if(!user.isEmailVerified()) {
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(view.getContext(), "Verification Email Has Been Sent. ", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "onFailure: Email not sent " + e.getMessage());

                        }
                    });

                }
            });

        }

        profileBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);
        });

    }



    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}