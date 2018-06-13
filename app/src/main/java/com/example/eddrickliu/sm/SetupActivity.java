package com.example.eddrickliu.sm;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText userName, fullName, countryName;
    private Button saveInfoButton;
    private CircleImageView profilePic;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        userName = (EditText)findViewById(R.id.setup_username);
        fullName = (EditText)findViewById(R.id.setup_fullname);
        countryName = (EditText)findViewById(R.id.setup_country);
        saveInfoButton = (Button) findViewById(R.id.setup_save_button);
        profilePic = (CircleImageView) findViewById(R.id.setup_profile);
        loadingBar = new ProgressDialog(this);

        saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAccountSetupInfo();
            }
        });
    }

    private void saveAccountSetupInfo() {
        String username = userName.getText().toString();
        String fullname = fullName.getText().toString();
        String countryname = countryName.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please provide a username. ", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Please provide a full name. ", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(countryname)){
            Toast.makeText(this, "Please provide a country. ", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Saving information");
            loadingBar.setMessage("Please wait, while your data is being stored...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("country",countryname);
            userMap.put("status", "Hello World");
            userMap.put("gender", "none");
            userMap.put("dob", "none");
            userMap.put("relationshipstatus","none");
            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        sendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Account is created successfully!", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }else{
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
