package com.example.beeraid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UserProfile extends AppCompatActivity {
    //private static final int GALLERY_INTENT_CODE = 1023;

    TextView fullName,email;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userId;
    Button restPassLocal, changeProfileImage;
    ImageView profileImage;


    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profileImage = (ImageView) findViewById(R.id.profileImage);//Working with = (ImageView) added

        fullName = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        restPassLocal = findViewById(R.id.resetPassLoc);
        changeProfileImage= (Button) findViewById(R.id.changeProfile);

        Button button = findViewById(R.id.backBtn);

        button.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });



        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit()
                        .into(profileImage);
            }
        });

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                email.setText(documentSnapshot.getString("email"));
                fullName.setText(documentSnapshot.getString("fName"));
            }
        });

        restPassLocal.setOnClickListener(view -> {

            final EditText resetPassword = new EditText(view.getContext());

            final AlertDialog.Builder passwordRestDialog = new AlertDialog.Builder(view.getContext());
            passwordRestDialog.setTitle("Reset Password ?");
            passwordRestDialog.setMessage("Please Enter New Password");
            passwordRestDialog.setView(resetPassword);

            passwordRestDialog.setPositiveButton("Yes", (dialogInterface, i) -> {
                //Extract email and send reset link
                String newPassword = resetPassword.getText().toString();
                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserProfile.this, "Password Rest Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfile.this, "Password Rest Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            passwordRestDialog.setNegativeButton("No", (dialogInterface, i) -> {
                //close
            });
            passwordRestDialog.create().show();
        });

        changeProfileImage.setOnClickListener(view -> {
            //open gallery
            Intent i = new Intent(view.getContext(), EditProfile.class);
            i.putExtra("fullName", "Isaac Michael Wassell");
            i.putExtra("email", "issacmwassell15@gmail.com");
            startActivity(i);
//                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(openGalleryIntent, 1000);
        });

        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode,
        @androidx.annotation.Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1000) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = data.getData();
                    //enable below to debug
                    //profileImage.setImageURI(imageUri);

                    upLoadImageToFirebase(imageUri);

                }
            }

        }

        private void upLoadImageToFirebase (Uri imageUri){
            //upload image to fire base
            final StorageReference fileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(profileImage);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }

}