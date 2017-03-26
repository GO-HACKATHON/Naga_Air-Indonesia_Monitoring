package com.naga_air.studioteam.indonesiamonitoring;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener  {

    private Button buttonRegister;
    private EditText editTextEmail;
    private  EditText editTextPassword;
    private EditText editTextName;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextName = (EditText) findViewById(R.id.fullName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonRegister :
                registerUser();
                break;
        }
    }

    private void registerUser(){
        final String fullname = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //stop the function execution further
            return;
        }
        if(TextUtils.isEmpty(password)){
            //password empty
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();
            //stop the function execution further
            return;
        }
        //if validations are ok
        //we will first show a progressbar
        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            //user is successfully registered and logged in
                            //we will start the profile activity here
                            //right now lets display toast only
                            Toast.makeText(RegisterActivity.this, "Registered successfully, log in your new account", Toast.LENGTH_LONG).show();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String useremail = user.getEmail();
                            String fix_email = useremail.replace(".",",");
                            databaseReference = FirebaseDatabase.getInstance().getReference("users/" + fix_email);
                            databaseReference.child("uid").setValue(user.getUid());
                            databaseReference.child("nama").setValue(fullname);
                            startActivity(new Intent(RegisterActivity.this, AuthenticationActivity.class));
                        }else{
                            Toast.makeText(RegisterActivity.this, "Could not register...please try again later", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}