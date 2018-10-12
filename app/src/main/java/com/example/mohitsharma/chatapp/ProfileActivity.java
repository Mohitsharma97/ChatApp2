package com.example.mohitsharma.chatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView mProfileimage;
    private TextView mprofileName,mProfileStatus,mprofileFriendscount;
    private Button mfreindsrequestbtn,decline_request_btn;
    private DatabaseReference mDatabaserefrence,mFriendRequestdatabase,mFriendDatabase,mNotificationDatabase,mChatdatabase;
    private ProgressDialog mprogressDialog;
    private FirebaseUser mCurrentUser;

    private  int current_state = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");
        mProfileimage = (CircleImageView)findViewById(R.id.Users_profile_image);
        mfreindsrequestbtn = (Button)findViewById(R.id.freinds_request_button);
        mprofileName = (TextView)findViewById(R.id.users_display_name);
        mProfileStatus = (TextView)findViewById(R.id.user_profile_status);
        mprofileFriendscount = (TextView)findViewById(R.id.Users_friends);
        decline_request_btn = (Button)findViewById(R.id.Decline_request);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        mprogressDialog = new ProgressDialog(this);
        mprogressDialog.setTitle("Loading User Data");
        mprogressDialog.setMessage("Please Wait...");
        mprogressDialog.setCanceledOnTouchOutside(false);
        mprogressDialog.show();

        mDatabaserefrence = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mDatabaserefrence.keepSynced(true);
        mFriendRequestdatabase = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        mFriendRequestdatabase.keepSynced(true);
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mChatdatabase = FirebaseDatabase.getInstance().getReference().child("Chat");
        mFriendDatabase.keepSynced(true);
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        decline_request_btn.setVisibility(View.INVISIBLE);
        if(user_id.equals(mCurrentUser.getUid())){ decline_request_btn.setVisibility(View.INVISIBLE);
            mfreindsrequestbtn.setVisibility(View.INVISIBLE);}



              mFriendDatabase.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get total available quest
                        int size = (int) dataSnapshot.getChildrenCount();
                        mprofileFriendscount.setText(size+" Friend");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mDatabaserefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                mprofileName.setText(display_name);
                mProfileStatus.setText(status);
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profilec).into(mProfileimage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image).placeholder(R.drawable.profilec).into(mProfileimage);
                    }
                });




                //*........................Friends List/ Request Feature ........................

                    mFriendRequestdatabase.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(mCurrentUser.getUid())){
                                String req_type = dataSnapshot.child(mCurrentUser.getUid()).child("request_type").getValue().toString();
                                if(req_type.equals("received")){



                                    current_state = 4;
                                    decline_request_btn.setVisibility(View.VISIBLE);
                                    decline_request_btn.setText("Accept Friend Request");
                                    mfreindsrequestbtn.setVisibility(View.INVISIBLE);
                                    mfreindsrequestbtn.setEnabled(true);



                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                mFriendRequestdatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                        if(dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();


                            if(req_type.equals("received")){



                                current_state = 4;
                                decline_request_btn.setVisibility(View.VISIBLE);
                                decline_request_btn.setText("Accept Friend Request");
                                mfreindsrequestbtn.setVisibility(View.INVISIBLE);
                                mfreindsrequestbtn.setEnabled(true);



                            } else if(req_type.equals("sent")){

                                current_state = 2;
                                decline_request_btn.setVisibility(View.VISIBLE);
                                decline_request_btn.setText("Cancel Friend Request");

                                mfreindsrequestbtn.setVisibility(View.INVISIBLE);
                                mfreindsrequestbtn.setEnabled(false);


                            }
                        }else{
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id)){
                                        current_state = 3;
                                        mfreindsrequestbtn.setText("UnFriend");
                                        decline_request_btn.setVisibility(View.INVISIBLE);
                                        decline_request_btn.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {


                                }
                            });
                        }


                        mprogressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        mfreindsrequestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mfreindsrequestbtn.setEnabled(false);

                //................SEND FRIEND REQUEST ....................................................

                if(current_state==0){
                    mFriendRequestdatabase.child(mCurrentUser.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       mFriendRequestdatabase.child(user_id).child(mCurrentUser.getUid()).child("request_type")
                                               .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                               HashMap<String,String> notificationdata = new HashMap<>();
                                               notificationdata.put("from",mCurrentUser.getUid());
                                               notificationdata.put("type","request");


                                               mNotificationDatabase.child(user_id).push().setValue(notificationdata);

                                               mfreindsrequestbtn.setEnabled(true);
                                               current_state = 2;
                                               decline_request_btn.setVisibility(View.VISIBLE);
                                               decline_request_btn.setText("Cancel Friend Request");
                                               mfreindsrequestbtn.setVisibility(View.INVISIBLE);
                                               mfreindsrequestbtn.setEnabled(false);


                                               Toast.makeText(ProfileActivity.this,"Request Sent Successfully",Toast.LENGTH_SHORT).show();
                                           }
                                       });
                                   }else {
                                       Toast.makeText(ProfileActivity.this,"Failed Sending Request",Toast.LENGTH_SHORT).show();
                                   }
                        }
                    });
                }


                //.................... REMOVE FRIEND AFTER ACCEPTING FRIEND REQUEST ....................................

                if(current_state==3){


                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void Void) {

                            mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<java.lang.Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mfreindsrequestbtn.setEnabled(true);
                                    current_state = 0;
                                    mfreindsrequestbtn.setVisibility(View.VISIBLE);
                                    mfreindsrequestbtn.setText("Sent Friend Request");
                                    decline_request_btn.setVisibility(View.INVISIBLE);
                                    decline_request_btn.setEnabled(false);
                                    Toast.makeText(ProfileActivity.this,"Friend removed Successfully",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });


                    mChatdatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void Void) {

                            mChatdatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<java.lang.Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ProfileActivity.this,"Friend removed Successfully",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });



                }


            }
        });







decline_request_btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        //..................... REMOVE SENT FRIEND REQUEST ................................


        if(current_state==2){


            mFriendRequestdatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void Void) {

                    mFriendRequestdatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<java.lang.Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mfreindsrequestbtn.setEnabled(true);
                            current_state = 0;
                            mfreindsrequestbtn.setVisibility(View.VISIBLE);
                            mfreindsrequestbtn.setText("Sent Friend Request");
                            decline_request_btn.setVisibility(View.INVISIBLE);
                            decline_request_btn.setEnabled(true);
                            Toast.makeText(ProfileActivity.this,"Request removed Successfully",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });



        }


        //................................ ACCEPT FRIEND REQUEST .................................................


        if(current_state==4){


            final String currentSate = DateFormat.getDateInstance().format(new Date());

            mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).child("date").setValue(currentSate).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).child("date").setValue(currentSate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendRequestdatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void Void) {

                                    mFriendRequestdatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<java.lang.Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mfreindsrequestbtn.setEnabled(true);
                                            current_state = 3;
                                            mfreindsrequestbtn.setVisibility(View.VISIBLE);
                                            mfreindsrequestbtn.setText("UnFriend");
                                            decline_request_btn.setVisibility(View.INVISIBLE);
                                            decline_request_btn.setEnabled(false);
                                            // Toast.makeText(ProfileActivity.this,"Request removed Successfully",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    })  ;
                }
            });
        }

    }
});


    }
}
