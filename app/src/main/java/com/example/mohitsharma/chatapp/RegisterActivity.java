package com.example.mohitsharma.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
private EditText mDisplayname,mEmail,mPassword;
private Button button;
private FirebaseAuth mAuth;
private Toolbar mToolbar;
private DatabaseReference mDatabase;

private ProgressDialog mprogressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mEmail = (EditText)findViewById(R.id.emailreg);
        mPassword = (EditText)findViewById(R.id.passwordreg);
        button = (Button)findViewById(R.id.startregbutton);
        mAuth = FirebaseAuth.getInstance();
        mDisplayname = (EditText) findViewById(R.id.regname);
        mprogressDialog  = new ProgressDialog(this);


        mToolbar = (Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String displayname = mDisplayname.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(!TextUtils.isEmpty(displayname)||!TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password)){


                    mprogressDialog.setTitle("Registering User");
                    mprogressDialog.setMessage("Please Wait...");
                    mprogressDialog.setCanceledOnTouchOutside(false);
                    mprogressDialog.show();
                    registeruser(displayname,email,password);


                }else {
                    mprogressDialog.hide();
                    Toast.makeText(RegisterActivity.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
                }




            }
        });
    }

    private void registeruser(final String displayname, String email, String password) {



        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String,String> usermap= new HashMap<>();
                            usermap.put("name",displayname);
                            usermap.put("status","I am Using ChatApp");
                            usermap.put("image","default");
                            usermap.put("thum_image","default");

                            mDatabase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mprogressDialog.dismiss();
                                        // Sign in success, update UI with the signed-in user's information
                                        Intent  mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }
                            });





                        } else {
                            // If sign in fails, display a message to the user.
                               mprogressDialog.hide();
                            Toast.makeText(RegisterActivity.this, "AUthentication fail",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
}
