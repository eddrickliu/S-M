package com.example.eddrickliu.sm;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.*;
import java.text.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton selectPostImage;
    private Button createPostButton;
    private EditText postDesription;

    private static final int GALLERY_PICK = 1;
    private Uri ImageUri;
    private String description;

    private StorageReference postRef;

    private String saveCurrentDate, saveCurrentTime, postRandomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postRef = FirebaseStorage.getInstance().getReference();

        selectPostImage = (ImageButton) findViewById(R.id.select_post_image);
        createPostButton = (Button) findViewById(R.id.create_post);
        postDesription = (EditText) findViewById(R.id.post_description);


        mToolbar = (Toolbar) findViewById(R.id.update_post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");

        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePostInfo();
            }
        });

    }

    private void validatePostInfo() {
        description = postDesription.getText().toString();
        if(ImageUri == null){
            Toast.makeText(this,"Please select an image to post",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this,"Please add a description to your post",Toast.LENGTH_SHORT).show();
        }
        else{
            storingImageToFirebaseStorage();
        }
    }

    private void storingImageToFirebaseStorage() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;


        StorageReference filePath = postRef.child("post images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PostActivity.this,"Image uploaded successfully", Toast.LENGTH_SHORT).show();
                }else{
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this,"Error Occured: " + message ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode==RESULT_OK && data!=null){
            ImageUri = data.getData();
            selectPostImage.setImageURI(ImageUri);



        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            sendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);

    }
}
