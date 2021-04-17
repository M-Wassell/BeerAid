package com.example.beeraid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
    public static TextView resulttextview;

    TextView verifyMsg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button resendCode,profileBtn, scan_btn, toast_btn;
    //ImageButton imageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resulttextview = findViewById(R.id.barcodetextview);
        scan_btn = findViewById(R.id.scanBtn);
        toast_btn = findViewById(R.id.toastBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        verifyMsg = findViewById(R.id.verifyMsg);
        resendCode = findViewById(R.id.resendCode);
        final FirebaseUser user = fAuth.getCurrentUser();
        //profileBtn = findViewById(R.id.profileBtn);
        //imageButton = findViewById(R.id.profileBtn);

//        imageButton.setOnClickListener(view -> {
//            Intent intent = new Intent(this,UserProfile.class);
//            startActivity(intent);
//        });

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ScanCodeActivity.class));
            }
        });

        toast_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,resulttextview.getText(), Toast.LENGTH_SHORT).show();
            }
        });


        if(!user.isEmailVerified()) {
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(view -> {
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
            });

        }

//        profileBtn.setOnClickListener(view -> {
//            Intent intent = new Intent(this, UserProfile.class);
//            startActivity(intent);
//        });

    }
    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.userProfileMenu){
            startActivity(new Intent(getApplicationContext(),UserProfile.class));
        }
        if(item.getItemId() == R.id.logout){
            startActivity(new Intent(getApplicationContext(),Login.class));
        }
        return super.onOptionsItemSelected(item);
    }
}