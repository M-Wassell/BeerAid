package com.example.beeraid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {


    public static final String TAG = "TAG";
    EditText profileFullName, profileEmailAddress;
    ImageView profileImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent data = getIntent();
        String fullName = data.getStringExtra("fullName");
        String email = data.getStringExtra("email");

        profileFullName = findViewById(R.id.profileFullName);// = (EditText) has been added
        profileEmailAddress = findViewById(R.id.profileEmailAddress);
        profileImageView = findViewById(R.id.profileImageView);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditProfile.this, "Profile Image Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        profileFullName.setText(fullName);
        profileEmailAddress.setText(email);


        Log.d(TAG, "onCreate: "+ fullName + " " + email);
    }
}