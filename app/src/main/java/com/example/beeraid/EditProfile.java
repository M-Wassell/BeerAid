package com.example.beeraid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {


    public static final String TAG = "TAG";
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    EditText profileFullName, profileEmailAddress;
    ImageView profileImageView;
    Button saveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;
    //String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();


        Intent data = getIntent();
        String fullName = data.getStringExtra("fullName");
        String email = data.getStringExtra("email");

        profileFullName = findViewById(R.id.profileFullName);// = (EditText) has been added
        profileEmailAddress = findViewById(R.id.profileEmailAddress);
        profileImageView = findViewById(R.id.profileImageView);
        saveBtn = findViewById(R.id.saveProfileInfo);

        StorageReference profileRef = storageReference.child("users/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).fit().into(profileImageView));

        profileImageView.setOnClickListener(view -> {
            Toast.makeText(EditProfile.this, "Profile Image Clicked", Toast.LENGTH_SHORT).show();
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGalleryIntent, 1000);


            //dispatchTakePictureIntent();
            //askCameraPermissions();

            //Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //startActivityForResult(camera, CAMERA_REQUEST_CODE);

        });

        saveBtn.setOnClickListener(view -> {
            if(profileFullName.getText().toString().isEmpty()||profileEmailAddress.getText().toString().isEmpty()){
                Toast.makeText(EditProfile.this, "One Or Many Fields Are Empty", Toast.LENGTH_SHORT).show();
                return;
            }

            final String email1 = profileEmailAddress.getText().toString();
            user.updateEmail(email1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    DocumentReference docRef = fStore.collection("users").document(user.getUid());
                    Map<String,Object> edit = new HashMap<>();
                    edit.put("email", email1);
                    edit.put("fName", profileFullName.getText().toString());
                    docRef.update(edit).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),UserProfile.class));
                            finish();
                        }
                    });
                    Toast.makeText(EditProfile.this, "Email is Changed", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

        profileFullName.setText(fullName);
        profileEmailAddress.setText(email);


        Log.d(TAG, "onCreate: "+ fullName + " " + email);
    }
//3
//    private void askCameraPermissions() {
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
//        }else{
//            openCamera();
//            //dispatchTakePictureIntent();
//        }
//    }

    //4
    //@Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if(requestCode == CAMERA_PERM_CODE){
//            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                openCamera();
//                //dispatchTakePictureIntent();
//            }else{
//                Toast.makeText(this, "Camera Permission is Required to Use The Camera", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    //5
//      private void openCamera() {
//        Toast.makeText(this, "Camera Open Request", Toast.LENGTH_SHORT).show();
//        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(camera, CAMERA_REQUEST_CODE);
//    }


    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
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
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profileImageView));
            }
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show());
    }


//        if(requestCode == CAMERA_REQUEST_CODE){
//            if(resultCode == Activity.RESULT_OK) {
//                File f = new File(currentPhotoPath);
//                profileImageView.setImageURI(Uri.fromFile(f));
//
//                Log.d(TAG, "Absolute Url of Image is: " + Uri.fromFile(f));
//
//                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                Uri contentUri = Uri.fromFile(f);
//                mediaScanIntent.setData(contentUri);
//                this.sendBroadcast(mediaScanIntent);
////this line may not work
//
//
//            }
//
//        }
 {
//
//                //enable below to debug
//                //profileImage.setImageURI(imageUri);
////6
////           Bitmap image = (Bitmap) data.getExtras().get("data");
////            profileImageView.setImageBitmap(image);
//            //code for later use

//
//            }
//        }

    }



// After camera loads..
//    private File createImageFile() throws IOException {
//        //Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_"+ timeStamp + ".jpg";
//        File storageDir = Environment.getExternalStorageDirectory();
//        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//
//        if(!storageDir.exists()){
//            if(!storageDir.mkdir()){
//                Log.e("TAG","Error");
//                throw new IOException();
//            }
//        }
//
//        File image = File.createTempFile(
//                imageFileName, /* prefix */
//                "jpg", /* suffix */
//                storageDir /* directory */
//        );
//        //save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath();
//        return image;
//    }

// after camera loads
//    private void dispatchTakePictureIntent(){
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        if(takePictureIntent.resolveActivity(getPackageManager()) !=null){
//            File photoFile = null;
//            try{
//                photoFile = createImageFile();
//            }catch (IOException ex){
//
//            }
//            if(photoFile !=null){
//                Uri photoURI = FileProvider.getUriForFile(this,"com.example.beeraid.android.fileprovider",photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
//                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
//            }
//        }
//    }
// unused
//    private void galleryAddPic(){
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(currentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }


}