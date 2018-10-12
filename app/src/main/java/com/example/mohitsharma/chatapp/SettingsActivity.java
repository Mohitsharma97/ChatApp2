package com.example.mohitsharma.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mCurrentUser;
    private CircleImageView imageView;
    private TextView mdispalyname,mstatus;
    private Button imagebtn,statusbtn;
    private StorageReference mImageStorage;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        imageView = (CircleImageView)findViewById(R.id.Settings_image);
        mdispalyname = (TextView)findViewById(R.id.Settings_display_name);
        mstatus = (TextView)findViewById(R.id.settings_status);
        imagebtn= (Button)findViewById(R.id.Settings_image_btn);
        statusbtn =(Button)findViewById(R.id.Settings_status_btn);
        mImageStorage = FirebaseStorage.getInstance().getReference();


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String Current_uid = mCurrentUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Current_uid);
        mDatabaseReference.keepSynced(true);







        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thum_image").getValue().toString();
                mdispalyname.setText(name);
                mstatus.setText(status);
                Picasso.get().load(thumb_image).placeholder(R.drawable.profile).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        statusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_value= mstatus.getText().toString();
                Intent StatusIntent  = new Intent(SettingsActivity.this,StatusActivity.class);
                StatusIntent.putExtra("Status_value",status_value);

                startActivity(StatusIntent);
            }
        });


        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgress = new ProgressDialog(SettingsActivity.this);
                mProgress.setTitle("uploading Image");
                mProgress.setMessage("Please wait...");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                final Uri resultUri = result.getUri();

                File thumb_filepath = new File(resultUri.getPath());
                  String current_user_id = mCurrentUser.getUid();




                final StorageReference filepath  = mImageStorage.child("Profile_Images").child(current_user_id+".jpg");



                filepath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String mdownloadUri = downloadUri.toString();

                            mDatabaseReference.child("image").setValue(mdownloadUri);
                            Toast.makeText(SettingsActivity.this,"Profile Picture Updated",Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();


                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });








                try {
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200).setMaxHeight(200)
                            .compressToBitmap(thumb_filepath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] thumb_byte = baos.toByteArray();
                    final StorageReference thumbstorage_filepath = mImageStorage.child("Profile_Images").child("thumbs").child(current_user_id+".jpg");


                    UploadTask uploadTask = thumbstorage_filepath.putBytes(thumb_byte);

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return thumbstorage_filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                String thumbuser_image = downloadUri.toString();
                                mDatabaseReference.child("thum_image").setValue(thumbuser_image);
                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });









                } catch (IOException e) {
                    e.printStackTrace();
                }










                imageView.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }



}
