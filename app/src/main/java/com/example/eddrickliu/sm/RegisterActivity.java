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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail,userPassword,userConfirmPass;
    private Button createAccountButton;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        userEmail = (EditText)findViewById(R.id.register_email);
        userPassword = (EditText)findViewById(R.id.register_password);
        userConfirmPass = (EditText)findViewById(R.id.register_confirm_pass);
        createAccountButton = (Button)findViewById(R.id.register_create_acc);
        loadingBar = new ProgressDialog(this);

        createAccountButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private void CreateNewAccount() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String confirmPass = userConfirmPass.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please write your email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please write your password",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPass)){
            Toast.makeText(this,"Please confirm your password",Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPass)){
            Toast.makeText(this,"Your passwords do not match, please try again",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Creating new account");
            loadingBar.setMessage("Please wait, while your account is being created...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        SendUserToSetupActivity();

                        Toast.makeText(RegisterActivity.this,"Account Created!",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }else{
                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this,"Error Occured: " + message,Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}
