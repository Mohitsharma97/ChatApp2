package com.example.mohitsharma.chatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText mStatus;
    private Button status_btn;

    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrent_user;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null) {

            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            mUserRef.child("online").setValue(true);
        }
        mToolbar = (Toolbar)findViewById(R.id.Status_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrent_user.getUid();

        String Status_vlue = getIntent().getStringExtra("Status_value");

        mProgress = new ProgressDialog(this);
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);


        status_btn = (Button)findViewById(R.id.status_change_button);
        mStatus = (EditText) findViewById(R.id.Status_Input);
        mStatus.setText(Status_vlue);

        status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please Wait...");
                mProgress.show();
                String status = mStatus.getText().toString();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Please Check the Connection",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null)
        {
            mUserRef.child("online").setValue(true);
        }else {

        }
    }
}
