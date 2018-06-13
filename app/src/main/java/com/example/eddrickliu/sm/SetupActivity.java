package com.example.eddrickliu.sm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText userName, fullName, countryName;
    private Button saveInfoButton;
    private CircleImageView profilePic;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        userName = (EditText)findViewById(R.id.setup_username);
        fullName = (EditText)findViewById(R.id.setup_fullname);
        countryName = (EditText)findViewById(R.id.setup_country);
        saveInfoButton = (Button) findViewById(R.id.setup_save_button);
        profilePic = (CircleImageView) findViewById(R.id.setup_profile);

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

        }
    }
}
