package com.example.eddrickliu.sm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText userName, fullName, countryName;
    private Button saveInfoButton;
    private CircleImageView profilePic;
    private ProgressDialog loadingBar;


    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference userProfileImageRef;

    String currentUserId;
    final static int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile images");

        userName = (EditText) findViewById(R.id.setup_username);
        fullName = (EditText) findViewById(R.id.setup_fullname);
        countryName = (EditText) findViewById(R.id.setup_country);
        saveInfoButton = (Button) findViewById(R.id.setup_save_button);
        profilePic = (CircleImageView) findViewById(R.id.setup_profile);
        loadingBar = new ProgressDialog(this);

        saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAccountSetupInfo();
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*go to gallery*/
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK);

            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profilepic")) {
                        String image = dataSnapshot.child("profilepic").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(profilePic);
                    }else{
                        Toast.makeText(SetupActivity.this, "Please select profile image first", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Profile image");
                loadingBar.setMessage("Please wait, while we update your profile pic...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SetupActivity.this, "Profile pic stored successfully to online storage", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            userRef.child("profilepic").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent backIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                        startActivity(backIntent);
                                        Toast.makeText(SetupActivity.this, "Profile pic stored successfully to Firebase Database successfully", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                    } else {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                Toast.makeText(SetupActivity.this, "Error Occured: Image cannot be cropped, try again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }

    }

    private void saveAccountSetupInfo() {
        String username = userName.getText().toString();
        String fullname = fullName.getText().toString();
        String countryname = countryName.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please provide a username. ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(this, "Please provide a full name. ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(countryname)) {
            Toast.makeText(this, "Please provide a country. ", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Saving information");
            loadingBar.setMessage("Please wait, while your data is being stored...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("country", countryname);
            userMap.put("status", "Hello World");
            userMap.put("gender", "none");
            userMap.put("dob", "none");
            userMap.put("relationshipstatus", "none");
            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        sendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Account is created successfully!", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                }
            });


        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
